package com.lhxy.istationdevice.android11.domain.debug;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigLoader;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigValidator;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.protocol.ProtocolMockCatalog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 调试包导出器
 * <p>
 * 把日志、当前生效配置和底座状态一起导出，方便现场问题留档。
 */
public final class DebugBundleExporter {
    private static final int TEST_HISTORY_SESSION_COUNT = 50;
    private static final int TEST_LOGCAT_LINE_COUNT = 12000;
    private static final int TEST_WARNING_LOGCAT_LINE_COUNT = 4000;
    private static final long COMMAND_TIMEOUT_SECONDS = 8L;

    private DebugBundleExporter() {
    }

    public static final class ExportResult {
        private final File file;
        private final OssDebugBundleUploader.UploadResult uploadResult;

        ExportResult(File file, OssDebugBundleUploader.UploadResult uploadResult) {
            this.file = file;
            this.uploadResult = uploadResult;
        }

        public File getFile() {
            return file;
        }

        public String describeForUser() {
            StringBuilder builder = new StringBuilder(file == null ? "-" : file.getAbsolutePath());
            if (uploadResult != null && uploadResult.isAttempted()) {
                builder.append("\nOSS: ").append(uploadResult.describeForUser());
            }
            return builder.toString();
        }
    }

    /**
     * 导出调试包。
     */
    public static File export(
            Context context,
            ShellConfig shellConfig,
            GpsSerialMonitor gpsSerialMonitor,
            Jt808SocketMonitor jt808SocketMonitor,
            String foundationStatus,
            String moduleStatus
    ) throws Exception {
        return exportDetailed(context, shellConfig, gpsSerialMonitor, jt808SocketMonitor, foundationStatus, moduleStatus).getFile();
    }

    public static ExportResult exportDetailed(
            Context context,
            ShellConfig shellConfig,
            GpsSerialMonitor gpsSerialMonitor,
            Jt808SocketMonitor jt808SocketMonitor,
            String foundationStatus,
            String moduleStatus
    ) throws Exception {
        File baseDir = context.getExternalFilesDir("exports");
        if (baseDir == null) {
            baseDir = new File(context.getFilesDir(), "exports");
        }
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IllegalStateException("无法创建调试包目录: " + baseDir.getAbsolutePath());
        }

        File exportFile = new File(
                baseDir,
                "debug-bundle-" + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date()) + ".zip"
        );

        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(exportFile))) {
            writeLogEntries(outputStream);
            writeEntry(outputStream, "config/config-summary.txt", shellConfig.describe());
            writeEntry(outputStream, "config/config-validation.txt", ShellConfigValidator.describe(shellConfig));
            writeEntry(outputStream, "config/config-source.txt", shellConfig.getConfigSource());
            writeEntry(outputStream, "config/config-locations.txt", ShellConfigLoader.describeConfigLocations(context));
            writeEntry(outputStream, "config/raw-terminal-config.json", ShellConfigLoader.loadCurrentConfigText(context));
            writeEntry(outputStream, "protocol/mock-catalog.txt", ProtocolMockCatalog.describeCatalog());
            writeEntry(outputStream, "device/self-check.txt", new TerminalSelfCheckUseCase().buildReport(context, shellConfig, foundationStatus, moduleStatus));
            writeEntry(outputStream, "device/foundation-status.txt", foundationStatus);
            writeEntry(outputStream, "modules/module-status.txt", moduleStatus);
            writeEntry(
                    outputStream,
                    "gps/monitor-status.txt",
                    gpsSerialMonitor == null ? "GPS 监听:\n- 当前没有可用监视器实例" : gpsSerialMonitor.describeStatus()
            );
            writeEntry(
                    outputStream,
                    "socket/monitor-status.txt",
                    jt808SocketMonitor == null ? "Socket 协议监听:\n- 当前没有可用监视器实例" : jt808SocketMonitor.describeStatus()
            );
        }

        String uploadText = buildMergedUploadText(context, shellConfig, gpsSerialMonitor, jt808SocketMonitor, foundationStatus, moduleStatus, exportFile);
        return new ExportResult(exportFile, OssDebugBundleUploader.uploadZippedTextIfConfigured(context, uploadText));
    }

    /**
     * 单独导出日志包。
     */
    public static File exportLogs(Context context) throws Exception {
        return exportLogsDetailed(context).getFile();
    }

    public static ExportResult exportLogsDetailed(Context context) throws Exception {
        File baseDir = context.getExternalFilesDir("exports");
        if (baseDir == null) {
            baseDir = new File(context.getFilesDir(), "exports");
        }
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IllegalStateException("无法创建日志包目录: " + baseDir.getAbsolutePath());
        }

        File exportFile = new File(
                baseDir,
                "logs-bundle-" + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date()) + ".zip"
        );
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(exportFile))) {
            writeLogEntries(outputStream);
        }
        return new ExportResult(exportFile, OssDebugBundleUploader.uploadZippedTextIfConfigured(context, buildMergedLogUploadText(context, exportFile)));
    }

    private static String buildMergedUploadText(
            Context context,
            ShellConfig shellConfig,
            GpsSerialMonitor gpsSerialMonitor,
            Jt808SocketMonitor jt808SocketMonitor,
            String foundationStatus,
            String moduleStatus,
            File localExportFile
    ) throws Exception {
        StringBuilder builder = new StringBuilder();
        appendHeader(builder, "IStationDevice Android11 测试日志合并版");
        appendSection(builder, "导出信息", buildExportInfo(context, localExportFile));
        appendCurrentLogSections(builder);
        appendSection(builder, "配置摘要", shellConfig == null ? "-" : shellConfig.describe());
        appendSection(builder, "配置校验", shellConfig == null ? "-" : ShellConfigValidator.describe(shellConfig));
        appendSection(builder, "配置来源", shellConfig == null ? "-" : shellConfig.getConfigSource());
        appendSection(builder, "配置位置", ShellConfigLoader.describeConfigLocations(context));
        appendSection(builder, "当前原始配置", ShellConfigLoader.loadCurrentConfigText(context));
        appendSection(builder, "协议 Mock 目录", ProtocolMockCatalog.describeCatalog());
        appendSection(builder, "设备自检", new TerminalSelfCheckUseCase().buildReport(context, shellConfig, foundationStatus, moduleStatus));
        appendSection(builder, "底座状态", foundationStatus);
        appendSection(builder, "模块状态", moduleStatus);
        appendSection(builder, "GPS 监听状态", gpsSerialMonitor == null ? "GPS 监听:\n- 当前没有可用监视器实例" : gpsSerialMonitor.describeStatus());
        appendSection(builder, "Socket 协议监听状态", jt808SocketMonitor == null ? "Socket 协议监听:\n- 当前没有可用监视器实例" : jt808SocketMonitor.describeStatus());
        appendSystemSnapshotSections(builder);
        return builder.toString();
    }

    private static String buildMergedLogUploadText(Context context, File localExportFile) throws Exception {
        StringBuilder builder = new StringBuilder();
        appendHeader(builder, "IStationDevice Android11 日志合并版");
        appendSection(builder, "导出信息", buildExportInfo(context, localExportFile));
        appendCurrentLogSections(builder);
        appendSystemSnapshotSections(builder);
        return builder.toString();
    }

    private static void appendHeader(StringBuilder builder, String title) {
        builder.append(title)
                .append('\n')
                .append("generatedAt=")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date()))
                .append('\n')
                .append("ossFormat=single-merged-txt")
                .append('\n')
                .append("ossContainer=zip-with-one-txt")
                .append('\n')
                .append("localFormat=zip-kept-for-compatibility")
                .append('\n');
    }

    private static String buildExportInfo(Context context, File localExportFile) {
        return "localFile=" + (localExportFile == null ? "-" : localExportFile.getAbsolutePath())
                + "\nlocalSize=" + (localExportFile == null || !localExportFile.exists() ? "-" : localExportFile.length())
                + "\nappVersion=" + describeAppVersion(context)
                + "\nossObjectRule=yyyyMMdd/HHmmssSSS.zip"
                + "\nnote=OSS 上传为压缩包，包内只有一个当前会话合并 TXT；本地仍保留原 zip。"
                + "\nhistoryPolicy=OSS 不上传历史 session，只上传当前联调版本/当前导出时刻的日志与系统快照。";
    }

    private static void appendCurrentLogSections(StringBuilder builder) {
        appendSection(builder, "测试诊断索引", buildTestDiagnosticsIndex());
        appendSection(builder, "日志覆盖说明", buildCoverageMap());
        appendSection(builder, "日志摘要", AppLogCenter.dumpSummary());
        appendSection(builder, "近期错误", AppLogCenter.dumpRecentErrors(200));
        appendSection(builder, "当前日志会话", AppLogCenter.describeSession());
        appendSection(builder, "AppLog 当前会话完整日志", AppLogCenter.dumpSessionFileText());
        appendSection(builder, "AppLog 内存缓冲", AppLogCenter.dumpPlainText());
    }

    private static String describeAppVersion(Context context) {
        if (context == null) {
            return "-";
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return context.getPackageName()
                    + " / versionName=" + packageInfo.versionName
                    + " / versionCode=" + packageInfo.getLongVersionCode();
        } catch (Exception e) {
            return context.getPackageName() + " / version=unknown / error=" + e.getMessage();
        }
    }

    private static void appendSystemSnapshotSections(StringBuilder builder) {
        appendCommandSection(builder, "Android logcat", "logcat", "-d", "-v", "threadtime", "-t", String.valueOf(TEST_LOGCAT_LINE_COUNT));
        appendCommandSection(builder, "Android logcat warnings", "logcat", "-d", "-v", "threadtime", "-t", String.valueOf(TEST_WARNING_LOGCAT_LINE_COUNT), "*:W");
        appendCommandSection(builder, "Android logcat events", "logcat", "-d", "-b", "events", "-v", "threadtime", "-t", "3000");
        appendCommandSection(builder, "系统属性 getprop", "getprop");
        appendCommandSection(builder, "dumpsys audio", "dumpsys", "audio");
        appendCommandSection(builder, "dumpsys media_session", "dumpsys", "media_session");
        appendCommandSection(builder, "dumpsys connectivity", "dumpsys", "connectivity");
        appendCommandSection(builder, "dumpsys wifi", "dumpsys", "wifi");
        appendCommandSection(builder, "dumpsys battery", "dumpsys", "battery");
        appendCommandSection(builder, "dumpsys package", "dumpsys", "package", "com.lhxy.istationdevice.android11");
        appendCommandSection(builder, "CAN link state", "ip", "-details", "-statistics", "link", "show");
        appendCommandSection(builder, "CAN proc stats", "cat", "/proc/net/can/stats");
        appendCommandSection(builder, "CAN proc receive list", "cat", "/proc/net/can/rcvlist_all");
        appendCommandSection(builder, "df", "df");
        appendCommandSection(builder, "mount", "mount");
    }

    private static void appendCommandSection(StringBuilder builder, String title, String... command) {
        appendSection(builder, title, runCommandText(command));
    }

    private static void appendSection(StringBuilder builder, String title, String content) {
        builder.append('\n')
                .append("==================== ")
                .append(title == null ? "-" : title)
                .append(" ====================")
                .append('\n')
                .append(content == null ? "" : content)
                .append('\n');
    }

    private static void appendFileSection(StringBuilder builder, String title, File file) throws Exception {
        if (file == null || !file.exists() || !file.isFile()) {
            appendSection(builder, title, "文件不存在");
            return;
        }
        appendSection(builder, title, new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
    }

    private static void writeLogEntries(ZipOutputStream outputStream) throws Exception {
        writeEntry(outputStream, "logs/app-log.txt", AppLogCenter.dumpSessionFileText());
        writeEntry(outputStream, "logs/app-log-buffer.txt", AppLogCenter.dumpPlainText());
        writeEntry(outputStream, "logs/by-module.txt", AppLogCenter.dumpByTag());
        writeEntry(outputStream, "logs/by-trace.txt", AppLogCenter.dumpByTraceId());
        writeEntry(outputStream, "logs/summary.txt", AppLogCenter.dumpSummary());
        writeEntry(outputStream, "logs/recent-errors.txt", AppLogCenter.dumpRecentErrors(200));
        writeEntry(outputStream, "logs/session-info.txt", AppLogCenter.describeSession());
        writeEntry(outputStream, "logs/recent-sessions.txt", AppLogCenter.describeRecentSessions(TEST_HISTORY_SESSION_COUNT));
        writeEntry(outputStream, "logs/coverage-map.txt", buildCoverageMap());
        writeEntry(outputStream, "logs/test-diagnostics-index.txt", buildTestDiagnosticsIndex());

        List<File> sessionFiles = AppLogCenter.listSessionFiles(TEST_HISTORY_SESSION_COUNT);
        for (File sessionFile : sessionFiles) {
            writeFileEntry(outputStream, "logs/history/" + sessionFile.getName(), sessionFile);
        }

        writeCommandEntry(outputStream, "logs/android-logcat.txt", "logcat", "-d", "-v", "threadtime", "-t", String.valueOf(TEST_LOGCAT_LINE_COUNT));
        writeCommandEntry(outputStream, "logs/android-logcat-warnings.txt", "logcat", "-d", "-v", "threadtime", "-t", String.valueOf(TEST_WARNING_LOGCAT_LINE_COUNT), "*:W");
        writeCommandEntry(outputStream, "logs/android-logcat-events.txt", "logcat", "-d", "-b", "events", "-v", "threadtime", "-t", "3000");
        writeCommandEntry(outputStream, "device/system-properties.txt", "getprop");
        writeCommandEntry(outputStream, "device/dumpsys-audio.txt", "dumpsys", "audio");
        writeCommandEntry(outputStream, "device/dumpsys-media-session.txt", "dumpsys", "media_session");
        writeCommandEntry(outputStream, "device/dumpsys-connectivity.txt", "dumpsys", "connectivity");
        writeCommandEntry(outputStream, "device/dumpsys-wifi.txt", "dumpsys", "wifi");
        writeCommandEntry(outputStream, "device/dumpsys-battery.txt", "dumpsys", "battery");
        writeCommandEntry(outputStream, "device/dumpsys-package.txt", "dumpsys", "package", "com.lhxy.istationdevice.android11");
        writeCommandEntry(outputStream, "device/can-link-state.txt", "ip", "-details", "-statistics", "link", "show");
        writeCommandEntry(outputStream, "device/can-proc-stats.txt", "cat", "/proc/net/can/stats");
        writeCommandEntry(outputStream, "device/can-proc-receive-list.txt", "cat", "/proc/net/can/rcvlist_all");
        writeCommandEntry(outputStream, "device/df.txt", "df");
        writeCommandEntry(outputStream, "device/mount.txt", "mount");
    }

    private static String buildCoverageMap() {
        return "log coverage map:"
                + "\n- protocol tx/rx: logs/app-log.txt, logs/by-module.txt, logs/by-trace.txt; categories PROTOCOL_TX / PROTOCOL_RX include SerialPortAdapter tx/rx, socket, DVR, GPS NMEA, JHY, station-display, JT808/AL808, crossing and overspeed frames."
                + "\n- business flow: category BIZ includes station, dispatch, sign-in, upgrade/download, GPS auto-report, passenger counter, file import/export decisions."
                + "\n- ui flow: category UI includes legacy home actions, DVR touch coordinates, monitor mode changes, setup/file/login page actions."
                + "\n- device operations: category DEVICE includes GPIO, camera, RFID, serial/socket open-close, system reboot/install/time operations."
                + "\n- failures: logs/recent-errors.txt plus category ERROR entries in app-log/by-module/by-trace."
                + "\n- runtime state: config/*, device/*, gps/monitor-status.txt, socket/monitor-status.txt, modules/module-status.txt."
                + "\n- android system snapshot: logs/android-logcat*.txt, device/system-properties.txt, device/dumpsys-*.txt, device/can-*.txt, device/df.txt, device/mount.txt."
                + "\n- test phase policy: verbose evidence first; reduce log volume only after the related function passes testing."
                + "\n- external evidence still needed for DVR internal firmware logs, kernel logs blocked by Android permissions, and visual/audio-only effects.";
    }

    private static String buildTestDiagnosticsIndex() {
        return "test diagnostics index:"
                + "\n- quick start: open logs/summary.txt first, then logs/recent-errors.txt, then logs/by-trace.txt."
                + "\n- protocol frames: search PROTOCOL_TX / PROTOCOL_RX, M90RealSerial, M90StubSerial, DvrSerialMonitor, DvrSerialDispatchUseCase, GpsSerialMonitor, Jt808SocketMonitor, LegacyStationDisplay, JhyPassengerCounterMonitor."
                + "\n- station flow: search StationBusinessModule, station-action, gps-auto-report, station-snapshot, LegacyStationDisplay."
                + "\n- dispatch/network flow: search DispatchBusinessModule, JT808, AL808, sendProfessionRequest, socket/monitor-status.txt, device/dumpsys-connectivity.txt."
                + "\n- audio playback: audio is not a protocol frame; search LegacyStationAudio, station-audio-plan, station-audio-player, station-audio-player-error, station-audio-gpio, station-audio-volume, station-audio-tts; also read device/dumpsys-audio.txt and device/dumpsys-media-session.txt."
                + "\n- DVR/video/touch: search LegacyMainActivity, home-monitor, home-dvr-touch, DvrSerialMonitor, DvrSerialDispatchUseCase, dvr raw, DVR 在线, DVR 离线, CameraAdapter; also read logs/android-logcat-warnings.txt."
                + "\n- GPIO/device operations: search DEVICE, GPIO, writePin, TerminalSelfCheck, device/self-check.txt."
                + "\n- CAN state: current code has CAN config/self-check only, no CAN business tx/rx adapter yet; read CAN 检查, can-link-state, can-proc-stats, can-proc-receive-list."
                + "\n- GPS/resources: search GpsSerialMonitor, gps raw sample, gps-fix, GpsBusinessModule, LegacyGpsAutoReportEngine, gps-auto-report, JT808 路口, 路口超速, StationResourceArchiveUseCase, config/raw-terminal-config.json."
                + "\n- RFID/sign-in: search RFID, SignInBusinessModule, read_card, manual_sign_out, dispatch attendance."
                + "\n- file/import/export/OSS: search FileBusinessModule, StationResourceArchiveUseCase, OssDebugBundleUploader, export_bundle, import_station_resources."
                + "\n- upgrade/download: search LegacyUpgradeDownloadAgent, HTTP 下载, FTP 下载, 8B0A, endpoint."
                + "\n- system state: read config/*, modules/module-status.txt, device/foundation-status.txt, device/system-properties.txt, device/dumpsys-package.txt, device/df.txt."
                + "\n- known blind spots: DVR firmware private logs, kernel logs blocked by permission, and purely visual/audio physical effects still need photo/video or on-site measurement.";
    }

    private static void writeCommandEntry(ZipOutputStream outputStream, String entryName, String... command) throws Exception {
        writeEntry(outputStream, entryName, runCommandText(command));
    }

    private static String runCommandText(String... command) {
        StringBuilder builder = new StringBuilder();
        builder.append("$ ").append(joinCommand(command)).append('\n');
        Process process = null;
        Thread readerThread = null;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(true).start();
            Process runningProcess = process;
            readerThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(runningProcess.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        synchronized (builder) {
                            builder.append(line).append('\n');
                        }
                    }
                } catch (Exception e) {
                    synchronized (builder) {
                        builder.append("read command output failed: ")
                                .append(e.getClass().getSimpleName())
                                .append(": ")
                                .append(e.getMessage())
                                .append('\n');
                    }
                }
            }, "debug-bundle-command-reader");
            readerThread.start();
            boolean finished = process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                synchronized (builder) {
                    builder.append("\ncommand timed out after ").append(COMMAND_TIMEOUT_SECONDS).append("s\n");
                }
            }
            int exitCode = finished ? process.exitValue() : -1;
            if (readerThread != null) {
                readerThread.join(1000L);
            }
            synchronized (builder) {
                builder.append("\nexitCode=").append(exitCode).append('\n');
            }
        } catch (Exception e) {
            synchronized (builder) {
                builder.append("command failed: ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage()).append('\n');
            }
            if (process != null) {
                process.destroyForcibly();
            }
        }
        return builder.toString();
    }

    private static String joinCommand(String... command) {
        if (command == null || command.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String part : command) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(part == null ? "" : part);
        }
        return builder.toString();
    }

    private static void writeEntry(ZipOutputStream outputStream, String entryName, String content) throws Exception {
        ZipEntry zipEntry = new ZipEntry(entryName);
        outputStream.putNextEntry(zipEntry);
        outputStream.write((content == null ? "" : content).getBytes(StandardCharsets.UTF_8));
        outputStream.closeEntry();
    }

    private static void writeFileEntry(ZipOutputStream outputStream, String entryName, File file) throws Exception {
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }
        ZipEntry zipEntry = new ZipEntry(entryName);
        outputStream.putNextEntry(zipEntry);
        byte[] buffer = new byte[4096];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
        outputStream.closeEntry();
    }
}
