package com.lhxy.istationdevice.android11.domain.upgrade;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.net.Uri;
import android.util.Base64;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LegacyHomeStatusRepository;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.file.StationResourceArchiveUseCase;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808GeneralResponse;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808LegacyMessages;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808UpgradeCommand;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808UpgradeNotification;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808Variant;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 旧 M90 风格升级下载执行器。
 */
public final class LegacyUpgradeDownloadAgent {
    private static final String TAG = "UpgradeDownloadAgent";
    private static final int MAX_RETRY_COUNT = 4;
    private static final int STATUS_ALREADY_COMPLETED = 6;
    private static final int STATUS_DOWNLOAD_PROGRESS = 8;
    private static final int STATUS_DOWNLOAD_FINISHED = 1;
    private static final int STATUS_RESOURCE_FAILED = 5;
    private static final long RETRY_DELAY_MILLIS = 4000L;

    private final SocketClientAdapter socketClientAdapter;
    private final SystemOps systemOps;
    private final StationResourceArchiveUseCase stationResourceArchiveUseCase = new StationResourceArchiveUseCase();
    private final Jt808LegacyMessages legacyMessages = new Jt808LegacyMessages();
    private final LegacyUpgradeTaskStore taskStore = new LegacyUpgradeTaskStore();
    private final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Object taskLock = new Object();
    private final Map<Integer, ManagedTask> taskByType = new HashMap<>();

    private volatile Context appContext;
    private volatile boolean tasksRestored;

    public LegacyUpgradeDownloadAgent(SocketClientAdapter socketClientAdapter, SystemOps systemOps) {
        this.socketClientAdapter = socketClientAdapter;
        this.systemOps = systemOps;
    }

    public void updateContext(Context context) {
        appContext = context == null ? null : context.getApplicationContext();
        if (appContext == null) {
            tasksRestored = false;
            return;
        }
        if (!tasksRestored) {
            restorePersistedTasks();
            tasksRestored = true;
        }
    }

    public void handleCommand(String channelName, Jt808UpgradeCommand command, String traceId) {
        Context context = appContext;
        if (context == null || command == null) {
            return;
        }

        if (command.isCancelCommand()) {
            sendGeneralResponse(channelName, command, 0, traceId);
            cancelBySerial(command.getCancelSerialHex(), traceId);
            return;
        }

        int downloadType = command.resolveDownloadType();
        if (downloadType == 0) {
            sendGeneralResponse(channelName, command, 3, traceId);
            return;
        }

        if (downloadType == Jt808UpgradeCommand.DOWNLOAD_TYPE_APK && !isSupportedRemoteApk(context, command, traceId, channelName)) {
            sendGeneralResponse(channelName, command, 2, traceId);
            return;
        }

        sendGeneralResponse(channelName, command, 0, traceId);
        Long scheduledAtMillis = command.isScheduledCommand() ? resolveScheduledAtMillis(command.getScheduleTimeBcd()) : null;
        enqueueTask(channelName, command, downloadType, scheduledAtMillis, traceId);
    }

    private void enqueueTask(
            String channelName,
            Jt808UpgradeCommand command,
            int downloadType,
            Long scheduledAtMillis,
            String traceId
    ) {
        ManagedTask task;
        synchronized (taskLock) {
            ManagedTask existing = taskByType.get(downloadType);
            if (existing != null && existing.sameTransfer(command)) {
                existing.updateRequest(channelName, command, scheduledAtMillis);
                if (existing.state == TaskState.COMPLETED) {
                    sendUpgradeStatus(channelName, command, STATUS_ALREADY_COMPLETED, 0, traceId);
                    persistTask(existing);
                    return;
                }
                if (existing.state == TaskState.SCHEDULED || existing.state == TaskState.RUNNING) {
                    persistTask(existing);
                    return;
                }
                cancelInternal(existing, existing.state == TaskState.FAILED);
                taskByType.remove(downloadType);
                removePersistedTask(downloadType);
            }
            if (existing != null) {
                cancelInternal(existing, true);
                taskByType.remove(downloadType);
                removePersistedTask(downloadType);
            }

            task = new ManagedTask(downloadType, channelName, command, resolveLocalTarget(downloadType), scheduledAtMillis);
            taskByType.put(downloadType, task);
            persistTask(task);
            if (scheduledAtMillis != null && scheduledAtMillis > System.currentTimeMillis()) {
                long delayMillis = Math.max(0L, scheduledAtMillis - System.currentTimeMillis());
                task.state = TaskState.SCHEDULED;
                persistTask(task);
                task.scheduledFuture = scheduler.schedule(() -> startTask(task, traceId), delayMillis, TimeUnit.MILLISECONDS);
                AppLogCenter.log(
                        LogCategory.BIZ,
                        LogLevel.INFO,
                        TAG,
                        "已登记计划升级任务 type=" + downloadType + " / serial=" + command.getRequestSerialHex() + " / delayMs=" + delayMillis,
                        traceId
                );
                return;
            }
        }
        startTask(task, traceId);
    }

    private void startTask(ManagedTask task, String traceId) {
        synchronized (taskLock) {
            if (task.cancelled.get()) {
                return;
            }
            task.state = TaskState.RUNNING;
            persistTask(task);
            task.runningFuture = downloadExecutor.submit(() -> runTask(task, traceId));
        }
    }

    private void runTask(ManagedTask task, String traceId) {
        Context context = appContext;
        if (context == null) {
            finishTransferFailure(task, new IllegalStateException("当前没有可用上下文"), traceId);
            return;
        }

        try {
            ensureParent(task.localFile);
            if (task.localFile.exists() && !task.localFile.delete()) {
                throw new IllegalStateException("无法覆盖旧下载文件: " + task.localFile.getAbsolutePath());
            }

            publishStart(context, task.downloadType);
            downloadToLocalFile(task, traceId);
            if (task.cancelled.get()) {
                cleanupPartialFile(task.localFile);
                return;
            }

            task.retryCount = 0;
            persistTask(task);

            if (task.downloadType == Jt808UpgradeCommand.DOWNLOAD_TYPE_APK) {
                LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.APK_UPGRADING);
                systemOps.installPackage(task.localFile.getAbsolutePath(), traceId + "-install");
            } else {
                LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.RESOURCE_PARSING);
                StationResourceArchiveUseCase.OperationResult result = stationResourceArchiveUseCase.importStationResources(context);
                if (!result.isSuccess()) {
                    finishResourceFailure(task, result.getSummary() + ": " + result.getDetail(), traceId);
                    return;
                }
                LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.NO_DOWNLOAD_TASK);
            }

            sendUpgradeStatus(task.channelName, task.command, STATUS_DOWNLOAD_FINISHED, 0, traceId);
            synchronized (taskLock) {
                if (taskByType.get(task.downloadType) == task) {
                    task.state = TaskState.COMPLETED;
                    persistTask(task);
                }
            }
        } catch (Exception e) {
            if (task.cancelled.get()) {
                cleanupPartialFile(task.localFile);
                return;
            }
            finishTransferFailure(task, e, traceId);
        }
    }

    private void finishTransferFailure(ManagedTask task, Exception exception, String traceId) {
        if (task.retryCount < MAX_RETRY_COUNT) {
            scheduleRetry(task, traceId, exception);
            return;
        }

        Context context = appContext;
        cleanupPartialFile(task.localFile);
        synchronized (taskLock) {
            if (taskByType.get(task.downloadType) == task) {
                task.state = TaskState.FAILED;
                persistTask(task);
            }
        }
        sendGeneralResponse(task.channelName, task.command, 1, traceId);
        if (context != null) {
            if (task.downloadType == Jt808UpgradeCommand.DOWNLOAD_TYPE_SOURCE_FILE) {
                LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.NO_DOWNLOAD_TASK);
            } else {
                LegacyHomeStatusRepository.setInfoTips(context, "APK download failed");
            }
        }
        AppLogCenter.log(
                LogCategory.ERROR,
                LogLevel.WARN,
                TAG,
                "升级下载任务失败 type=" + task.downloadType + " / error=" + safeMessage(exception),
                traceId
        );
    }

    private void finishResourceFailure(ManagedTask task, String detail, String traceId) {
        Context context = appContext;
        synchronized (taskLock) {
            if (taskByType.get(task.downloadType) == task) {
                task.state = TaskState.FAILED;
                persistTask(task);
            }
        }
        if (context != null) {
            LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.RESOURCE_DOWNLOAD_FAILED);
        }
        sendUpgradeStatus(task.channelName, task.command, STATUS_RESOURCE_FAILED, 0, traceId);
        AppLogCenter.log(
                LogCategory.ERROR,
                LogLevel.WARN,
                TAG,
                "资源包导入失败 type=" + task.downloadType + " / error=" + detail,
                traceId
        );
    }

    private void scheduleRetry(ManagedTask task, String traceId, Exception exception) {
        cleanupPartialFile(task.localFile);
        long retryAtMillis = System.currentTimeMillis() + RETRY_DELAY_MILLIS;
        synchronized (taskLock) {
            if (taskByType.get(task.downloadType) != task) {
                return;
            }
            task.retryCount++;
            task.state = TaskState.SCHEDULED;
            task.scheduledAtMillis = retryAtMillis;
            task.scheduledFuture = scheduler.schedule(
                    () -> startTask(task, traceId + "-retry-" + task.retryCount),
                    RETRY_DELAY_MILLIS,
                    TimeUnit.MILLISECONDS
            );
            persistTask(task);
        }
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.WARN,
                TAG,
                "升级下载失败，准备重试 retry=" + task.retryCount + "/" + MAX_RETRY_COUNT + " / error=" + safeMessage(exception),
                traceId
        );
    }

    private void publishStart(Context context, int downloadType) {
        if (downloadType == Jt808UpgradeCommand.DOWNLOAD_TYPE_SOURCE_FILE) {
            LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.RESOURCE_DOWNLOADING);
            LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.DOWNLOAD_SOURCEFILE_PROGRESS, 0);
        } else {
            LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.DOWNLOAD_APK_PROGRESS, 0);
        }
    }

    private void downloadToLocalFile(ManagedTask task, String traceId) throws Exception {
        String downloadUrl = buildDownloadUrl(task.command);
        if (downloadUrl.startsWith("http://") || downloadUrl.startsWith("https://")) {
            downloadViaHttp(task, downloadUrl, traceId);
            return;
        }
        downloadViaFtp(task, downloadUrl, traceId);
    }

    private void downloadViaHttp(ManagedTask task, String downloadUrl, String traceId) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
        task.activeHttpConnection = connection;
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);
        if (!task.command.getLoginName().isEmpty()) {
            String auth = task.command.getLoginName() + ":" + task.command.getLoginPwd();
            String encoded = Base64.encodeToString(auth.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + encoded);
        }
        connection.connect();
        int statusCode = connection.getResponseCode();
        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("HTTP 下载失败 code=" + statusCode);
        }
        long totalBytes = connection.getContentLengthLong();
        try (InputStream inputStream = connection.getInputStream(); FileOutputStream outputStream = new FileOutputStream(task.localFile, false)) {
            task.activeInputStream = inputStream;
            copyStream(task, inputStream, outputStream, totalBytes, traceId);
        } finally {
            task.activeInputStream = null;
            connection.disconnect();
            task.activeHttpConnection = null;
        }
    }

    private void downloadViaFtp(ManagedTask task, String downloadUrl, String traceId) throws Exception {
        Uri ftpUri = Uri.parse(downloadUrl);
        String host = Objects.requireNonNull(ftpUri.getHost(), "FTP 地址缺少 host");
        int port = ftpUri.getPort() > 0 ? ftpUri.getPort() : (task.command.getServerAddressPort() > 0 ? task.command.getServerAddressPort() : 21);
        String remotePath = ftpUri.getPath();
        if (remotePath == null || remotePath.trim().isEmpty()) {
            throw new IllegalArgumentException("FTP 下载路径为空");
        }
        FTPClient ftpClient = new FTPClient();
        task.activeFtpClient = ftpClient;
        ftpClient.setConnectTimeout(15000);
        ftpClient.setDefaultTimeout(30000);
        ftpClient.connect(host, port);
        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            throw new IOException("FTP 连接失败 code=" + ftpClient.getReplyCode());
        }
        if (!ftpClient.login(emptyAsAnonymous(task.command.getLoginName()), task.command.getLoginPwd())) {
            throw new IOException("FTP 登录失败");
        }
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        long totalBytes = resolveFtpFileSize(ftpClient, remotePath);
        try (InputStream inputStream = ftpClient.retrieveFileStream(remotePath); FileOutputStream outputStream = new FileOutputStream(task.localFile, false)) {
            if (inputStream == null) {
                throw new IOException("FTP 读取文件流失败: " + remotePath);
            }
            task.activeInputStream = inputStream;
            copyStream(task, inputStream, outputStream, totalBytes, traceId);
        } finally {
            task.activeInputStream = null;
            try {
                ftpClient.completePendingCommand();
            } catch (IOException ignored) {
            }
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                } catch (IOException ignored) {
                }
                try {
                    ftpClient.disconnect();
                } catch (IOException ignored) {
                }
            }
            task.activeFtpClient = null;
        }
    }

    private long resolveFtpFileSize(FTPClient ftpClient, String remotePath) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(remotePath);
        if (ftpFiles != null && ftpFiles.length > 0) {
            return ftpFiles[0].getSize();
        }
        FTPFile ftpFile = ftpClient.mlistFile(remotePath);
        return ftpFile == null ? -1L : ftpFile.getSize();
    }

    private void copyStream(ManagedTask task, InputStream inputStream, FileOutputStream outputStream, long totalBytes, String traceId) throws Exception {
        byte[] buffer = new byte[16 * 1024];
        long written = 0L;
        int lastPercent = -1;
        while (true) {
            if (task.cancelled.get() || Thread.currentThread().isInterrupted()) {
                throw new IOException("下载任务已取消");
            }
            int read = inputStream.read(buffer);
            if (read < 0) {
                break;
            }
            outputStream.write(buffer, 0, read);
            written += read;
            if (totalBytes > 0L) {
                int percent = (int) Math.min(100L, (written * 100L) / totalBytes);
                if (percent != lastPercent) {
                    publishProgress(task, percent, traceId);
                    lastPercent = percent;
                }
            }
        }
        outputStream.flush();
        if (lastPercent < 100) {
            publishProgress(task, 100, traceId);
        }
    }

    private void publishProgress(ManagedTask task, int percent, String traceId) {
        Context context = appContext;
        if (context == null) {
            return;
        }
        if (task.downloadType == Jt808UpgradeCommand.DOWNLOAD_TYPE_SOURCE_FILE) {
            LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.DOWNLOAD_SOURCEFILE_PROGRESS, percent);
        } else {
            LegacyHomeStatusRepository.setInfoOperation(context, LegacyHomeStatusRepository.InfoOperation.DOWNLOAD_APK_PROGRESS, percent);
        }
        sendUpgradeStatus(task.channelName, task.command, STATUS_DOWNLOAD_PROGRESS, percent, traceId);
    }

    private File resolveLocalTarget(int downloadType) {
        Context context = Objects.requireNonNull(appContext, "当前没有上下文，无法计算下载路径");
        if (downloadType == Jt808UpgradeCommand.DOWNLOAD_TYPE_APK) {
            File root = context.getExternalFilesDir("upgrade");
            if (root == null) {
                root = new File(context.getFilesDir(), "upgrade");
            }
            return new File(root, "M90.apk");
        }
        File root = context.getExternalFilesDir(null);
        if (root == null) {
            root = context.getFilesDir();
        }
        return new File(root, "imports/BusRes/BusImport/SourceFile.zip");
    }

    private void sendGeneralResponse(String channelName, Jt808UpgradeCommand command, int result, String traceId) {
        if (channelName == null || command == null) {
            return;
        }
        socketClientAdapter.send(
                channelName,
                legacyMessages.encode(
                        legacyMessages.createGeneralResponse(
                                Jt808Variant.JT808,
                                command.getTerminalId(),
                                new Jt808GeneralResponse(command.getRequestSerialNumber(), 0x8B0A, result)
                        )
                ),
                traceId + "-general-response"
        );
    }

    private void sendUpgradeStatus(String channelName, Jt808UpgradeCommand command, int status, int progress, String traceId) {
        if (channelName == null || command == null) {
            return;
        }
        socketClientAdapter.send(
                channelName,
                legacyMessages.encode(
                        legacyMessages.createUpgradeNotification(
                                command.getTerminalId(),
                                new Jt808UpgradeNotification(command.getRequestSerialNumber(), status, progress)
                        )
                ),
                traceId + "-upgrade-status"
        );
    }

    private void cancelBySerial(String serialHex, String traceId) {
        if (serialHex == null || serialHex.trim().isEmpty()) {
            return;
        }
        synchronized (taskLock) {
            for (Map.Entry<Integer, ManagedTask> entry : new HashMap<>(taskByType).entrySet()) {
                ManagedTask task = entry.getValue();
                if (!task.matchesSerial(serialHex)) {
                    continue;
                }
                cancelInternal(task, true);
                taskByType.remove(entry.getKey());
                removePersistedTask(entry.getKey());
                AppLogCenter.log(
                        LogCategory.BIZ,
                        LogLevel.INFO,
                        TAG,
                        "已取消升级下载任务 serial=" + serialHex + " / type=" + entry.getKey(),
                        traceId
                );
            }
        }
    }

    private void cancelInternal(ManagedTask task, boolean cleanupLocalFile) {
        if (task == null) {
            return;
        }
        task.cancelled.set(true);
        ScheduledFuture<?> scheduledFuture = task.scheduledFuture;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        Future<?> runningFuture = task.runningFuture;
        if (runningFuture != null) {
            runningFuture.cancel(true);
        }
        InputStream activeInputStream = task.activeInputStream;
        if (activeInputStream != null) {
            try {
                activeInputStream.close();
            } catch (IOException ignored) {
            }
        }
        HttpURLConnection activeHttpConnection = task.activeHttpConnection;
        if (activeHttpConnection != null) {
            activeHttpConnection.disconnect();
        }
        FTPClient activeFtpClient = task.activeFtpClient;
        if (activeFtpClient != null && activeFtpClient.isConnected()) {
            try {
                activeFtpClient.disconnect();
            } catch (IOException ignored) {
            }
        }
        if (cleanupLocalFile) {
            cleanupPartialFile(task.localFile);
        }
        task.state = TaskState.CANCELLED;
    }

    private void restorePersistedTasks() {
        Context context = appContext;
        if (context == null) {
            return;
        }
        Map<Integer, ManagedTask> restoredTasks = new HashMap<>();
        for (LegacyUpgradeTaskStore.StoredTask storedTask : taskStore.load(context)) {
            ManagedTask task = ManagedTask.fromStoredTask(storedTask);
            if (task != null) {
                restoredTasks.put(task.downloadType, task);
            }
        }
        if (restoredTasks.isEmpty()) {
            return;
        }

        synchronized (taskLock) {
            if (!taskByType.isEmpty()) {
                return;
            }
            taskByType.putAll(restoredTasks);
        }

        long now = System.currentTimeMillis();
        for (ManagedTask task : restoredTasks.values()) {
            if (task.state == TaskState.COMPLETED || task.state == TaskState.FAILED || task.state == TaskState.CANCELLED) {
                continue;
            }
            if (task.scheduledAtMillis > now) {
                long delayMillis = Math.max(0L, task.scheduledAtMillis - now);
                task.state = TaskState.SCHEDULED;
                task.scheduledFuture = scheduler.schedule(
                        () -> startTask(task, "upgrade-restore-" + task.command.getRequestSerialHex()),
                        delayMillis,
                        TimeUnit.MILLISECONDS
                );
                continue;
            }
            startTask(task, "upgrade-restore-" + task.command.getRequestSerialHex());
        }
    }

    private boolean isSupportedRemoteApk(Context context, Jt808UpgradeCommand command, String traceId, String channelName) {
        Integer remoteVersion = resolveRemoteApkVersion(command.getVersionUrl());
        if (remoteVersion == null) {
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.WARN,
                    TAG,
                    "APK 升级包版本号不合法，拒绝受理 channel=" + channelName + " / url=" + command.getVersionUrl(),
                    traceId
            );
            return false;
        }
        long localVersion = resolveLocalVersionCode(context);
        if (localVersion > remoteVersion) {
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "本地 APK 版本高于远端，拒绝重复升级 local=" + localVersion + " / remote=" + remoteVersion,
                    traceId
            );
            return false;
        }
        return true;
    }

    private Integer resolveRemoteApkVersion(String versionUrl) {
        if (versionUrl == null) {
            return null;
        }
        String lowerCaseUrl = versionUrl.trim().toLowerCase(Locale.US);
        if (!lowerCaseUrl.endsWith(".apk")) {
            return null;
        }
        int versionMarkerIndex = lowerCaseUrl.lastIndexOf('v');
        int apkSuffixIndex = lowerCaseUrl.lastIndexOf(".apk");
        if (versionMarkerIndex < 0 || versionMarkerIndex + 1 >= apkSuffixIndex) {
            return null;
        }
        String versionText = lowerCaseUrl.substring(versionMarkerIndex + 1, apkSuffixIndex).trim();
        if (versionText.isEmpty() || !versionText.matches("\\d+")) {
            return null;
        }
        try {
            return Integer.parseInt(versionText);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private long resolveLocalVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return packageInfo.getLongVersionCode();
            }
            return packageInfo.versionCode;
        } catch (Exception e) {
            return 0L;
        }
    }

    private void persistTask(ManagedTask task) {
        Context context = appContext;
        if (context == null || task == null) {
            return;
        }
        taskStore.save(context, task.toStoredTask());
    }

    private void removePersistedTask(int downloadType) {
        Context context = appContext;
        if (context == null) {
            return;
        }
        taskStore.remove(context, downloadType);
    }

    private Long resolveScheduledAtMillis(String scheduleTimeBcd) {
        if (scheduleTimeBcd == null || scheduleTimeBcd.length() != 12) {
            return null;
        }
        try {
            int year = 2000 + Integer.parseInt(scheduleTimeBcd.substring(0, 2));
            int month = Integer.parseInt(scheduleTimeBcd.substring(2, 4));
            int day = Integer.parseInt(scheduleTimeBcd.substring(4, 6));
            int hour = Integer.parseInt(scheduleTimeBcd.substring(6, 8));
            int minute = Integer.parseInt(scheduleTimeBcd.substring(8, 10));
            int second = Integer.parseInt(scheduleTimeBcd.substring(10, 12));
            return LocalDateTime.of(year, month, day, hour, minute, second)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        } catch (Exception e) {
            return null;
        }
    }

    private String buildDownloadUrl(Jt808UpgradeCommand command) {
        String url = command.getVersionUrl();
        String lowerCaseUrl = url.toLowerCase(Locale.US);
        if (lowerCaseUrl.startsWith("ftp://") || lowerCaseUrl.startsWith("http://") || lowerCaseUrl.startsWith("https://")) {
            return url;
        }
        String normalizedPath = url.startsWith("/") ? url : "/" + url;
        if (command.getProtocolType() == 1) {
            return "http://" + command.getServerAddress() + ":" + command.getServerAddressPort() + normalizedPath;
        }
        return "ftp://" + command.getServerAddress() + ":" + command.getServerAddressPort() + normalizedPath;
    }

    private String emptyAsAnonymous(String loginName) {
        return loginName == null || loginName.trim().isEmpty() ? "anonymous" : loginName.trim();
    }

    private void ensureParent(File targetFile) {
        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("无法创建下载目录: " + parent.getAbsolutePath());
        }
    }

    private void cleanupPartialFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    private String safeMessage(Exception exception) {
        if (exception == null || exception.getMessage() == null || exception.getMessage().trim().isEmpty()) {
            return exception == null ? "未知错误" : exception.getClass().getSimpleName();
        }
        return exception.getMessage().trim();
    }

    private static final class ManagedTask {
        private final int downloadType;
        private final File localFile;
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        private volatile String channelName;
        private volatile Jt808UpgradeCommand command;
        private volatile TaskState state = TaskState.SCHEDULED;
        private volatile Long scheduledAtMillis;
        private volatile int retryCount;
        private volatile ScheduledFuture<?> scheduledFuture;
        private volatile Future<?> runningFuture;
        private volatile InputStream activeInputStream;
        private volatile HttpURLConnection activeHttpConnection;
        private volatile FTPClient activeFtpClient;

        private ManagedTask(int downloadType, String channelName, Jt808UpgradeCommand command, File localFile, Long scheduledAtMillis) {
            this.downloadType = downloadType;
            this.channelName = channelName;
            this.command = command;
            this.localFile = localFile;
            this.scheduledAtMillis = scheduledAtMillis == null ? 0L : scheduledAtMillis;
        }

        private boolean sameTransfer(Jt808UpgradeCommand other) {
            return other != null && command != null && command.buildTransferSignature().equals(other.buildTransferSignature());
        }

        private void updateRequest(String channelName, Jt808UpgradeCommand command, Long scheduledAtMillis) {
            this.channelName = channelName;
            this.command = command;
            this.scheduledAtMillis = scheduledAtMillis == null ? 0L : scheduledAtMillis;
        }

        private boolean matchesSerial(String serialHex) {
            if (serialHex == null || serialHex.trim().isEmpty()) {
                return false;
            }
            return serialHex.trim().equalsIgnoreCase(command.getRequestSerialHex());
        }

        private LegacyUpgradeTaskStore.StoredTask toStoredTask() {
            return new LegacyUpgradeTaskStore.StoredTask(
                    channelName,
                    downloadType,
                    state.name(),
                    scheduledAtMillis == null ? 0L : scheduledAtMillis,
                    localFile.getAbsolutePath(),
                    command.getTerminalId(),
                    command.getRequestSerialNumber(),
                    command.getServerAddress(),
                    command.getServerAddressPort(),
                    command.getProtocolType(),
                    command.getLoginName(),
                    command.getLoginPwd(),
                    command.getVersionUrl(),
                    command.getUpgradeType(),
                    command.getScheduleTimeBcd(),
                    command.getCancelSerialHex(),
                    retryCount
            );
        }

        private static ManagedTask fromStoredTask(LegacyUpgradeTaskStore.StoredTask storedTask) {
            if (storedTask == null || storedTask.getLocalPath().isEmpty()) {
                return null;
            }
            Jt808UpgradeCommand command = new Jt808UpgradeCommand(
                    storedTask.getTerminalId(),
                    storedTask.getRequestSerialNumber(),
                    storedTask.getServerAddress(),
                    storedTask.getServerAddressPort(),
                    storedTask.getProtocolType(),
                    storedTask.getLoginName(),
                    storedTask.getLoginPwd(),
                    storedTask.getVersionUrl(),
                    storedTask.getUpgradeType(),
                    storedTask.getScheduleTimeBcd(),
                    storedTask.getCancelSerialHex()
            );
            ManagedTask task = new ManagedTask(
                    storedTask.getDownloadType(),
                    storedTask.getChannelName(),
                    command,
                    new File(storedTask.getLocalPath()),
                    storedTask.getScheduledAtMillis()
            );
            task.state = TaskState.fromName(storedTask.getStateName());
            task.retryCount = storedTask.getRetryCount();
            return task;
        }
    }

    private enum TaskState {
        SCHEDULED,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED;

        private static TaskState fromName(String value) {
            if (value == null || value.trim().isEmpty()) {
                return FAILED;
            }
            try {
                return TaskState.valueOf(value.trim());
            } catch (IllegalArgumentException ignored) {
                return FAILED;
            }
        }
    }
}