package com.lhxy.istationdevice.android11.domain.upgrade;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Base64;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LegacyHomeStatusRepository;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class TinkerHotUpdateManager {
    private static final String TAG = "TinkerHotUpdate";
    private static final long PENDING_STALE_MILLIS = 3L * 60L * 1000L;
    private static final String ASSET_NAME = "oss-config.properties";
    private static final boolean FORCE_TEST_CONFIG = true;
    private static final String TEST_OSS_BUCKET = "p138-register-lucky";
    private static final String TEST_OSS_ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
    private static final String TEST_OSS_ACCESS_KEY_ID = "LTAI5t8Xfh2S1AdvFS2nfZeq";
    private static final String TEST_OSS_ACCESS_KEY_SECRET = "TK3C3vULd41uoqf2RvdWlOax0WKqzk";
    private static final String TEST_MANIFEST_OBJECT_KEY = "hotfix/android11/manifest.json";
    private static final int TEST_TIMEOUT_MILLIS = 30000;

    private Context appContext;

    public void updateContext(Context context) {
        appContext = context == null ? null : context.getApplicationContext();
    }

    public Result checkForUpdate(String traceId) {
        Context context = appContext;
        if (context == null) {
            return Result.failure("热更新检查失败", "当前没有可用上下文");
        }
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "热更新检查开始", traceId);
        Result inProgressResult = resolvePendingResult(context, traceId);
        if (inProgressResult != null) {
            return inProgressResult;
        }
        HotUpdateConfig config = HotUpdateConfig.load(context);
        if (!config.isUsable()) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "热更新配置不可用: " + config.disabledReason, traceId);
            return Result.failure("热更新未配置", config.disabledReason);
        }
        TinkerHotUpdateStateStore.startProgress(context, traceId);
        setTip(context, "正在检查热更新...");
        try {
            HotUpdateManifest manifest = loadManifest(config, traceId);
            TinkerHotUpdateStateStore.setProgressPercent(context, 15);
            if (manifest.isNoUpdate()) {
                TinkerHotUpdateStateStore.clearPending(context);
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "热更新检查结束: 无可用补丁", traceId);
                return Result.success("当前已是最新热更新", manifest.describeInline());
            }
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            long localVersionCode = resolveVersionCode(packageInfo);
            String localVersionName = packageInfo.versionName == null ? "" : packageInfo.versionName.trim();
            if (!manifest.enabled) {
                TinkerHotUpdateStateStore.clearPending(context);
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "热更新已禁用 enabled=false", traceId);
                return Result.success(buildLatestSummary(manifest), manifest.describeInline());
            }
            if (manifest.targetVersionCode > 0 && manifest.targetVersionCode != localVersionCode) {
                TinkerHotUpdateStateStore.clearPending(context);
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "补丁版本码不匹配 local=" + localVersionCode + " target=" + manifest.targetVersionCode, traceId);
                return Result.failure(
                        "当前版本不匹配补丁",
                        "localVersionCode=" + localVersionCode + " / targetVersionCode=" + manifest.targetVersionCode
                );
            }
            if (!manifest.targetVersionName.isEmpty() && !manifest.targetVersionName.equals(localVersionName)) {
                TinkerHotUpdateStateStore.clearPending(context);
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "补丁版本名不匹配 local=" + emptyAsDash(localVersionName) + " target=" + manifest.targetVersionName, traceId);
                return Result.failure(
                        "当前版本名不匹配补丁",
                        "localVersionName=" + emptyAsDash(localVersionName) + " / targetVersionName=" + manifest.targetVersionName
                );
            }
            String normalizedPatchVersion = manifest.resolvePatchVersion();
            String lastPatchVersion = TinkerHotUpdateStateStore.getLastPatchVersion(context);
            String lastPatchMd5 = TinkerHotUpdateStateStore.getLastPatchMd5(context);
            if (normalizedPatchVersion.equals(lastPatchVersion)
                    && (manifest.patchMd5.isEmpty() || manifest.patchMd5.equalsIgnoreCase(lastPatchMd5))) {
                TinkerHotUpdateStateStore.clearPending(context);
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "补丁已应用，无需重复下发 patchVersion=" + normalizedPatchVersion, traceId);
                return Result.success(buildLatestSummary(manifest), manifest.describeInline());
            }

            File patchFile = resolvePatchFile(context, manifest);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "准备下载补丁 patchVersion=" + normalizedPatchVersion + " / targetFile=" + patchFile.getAbsolutePath(), traceId);
            TinkerHotUpdateStateStore.setProgressPercent(context, 25);
            downloadPatch(config, manifest, patchFile, traceId);
            TinkerHotUpdateStateStore.setProgressPercent(context, 95);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "开始校验补丁 patchVersion=" + normalizedPatchVersion, traceId);
            verifyPatch(manifest, patchFile);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "补丁校验通过 patchVersion=" + normalizedPatchVersion, traceId);
            String patchMd5 = manifest.patchMd5.isEmpty() ? computeMd5(patchFile) : manifest.patchMd5;
            TinkerHotUpdateStateStore.markPending(context, normalizedPatchVersion, patchMd5);
            setTip(context, "热更新补丁已下载，正在合成，请勿重复检查...");
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "开始下发补丁到 Tinker patchVersion=" + normalizedPatchVersion + " / md5=" + patchMd5, traceId);
            TinkerInstaller.onReceiveUpgradePatch(context, patchFile.getAbsolutePath());
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "热更新补丁已下发 patchVersion=" + normalizedPatchVersion + " / file=" + patchFile.getAbsolutePath(),
                    traceId
            );
            return Result.success(buildIssuedSummary(normalizedPatchVersion), manifest.describeInline() + " / 正在合成补丁，完成后应用会自动重启");
        } catch (Exception e) {
            TinkerHotUpdateStateStore.clearPending(context);
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.ERROR,
                    TAG,
                    "热更新检查失败: " + safeMessage(e),
                    traceId
            );
            return Result.failure("热更新检查失败", safeMessage(e));
        }
    }

    private HotUpdateManifest loadManifest(HotUpdateConfig config, String traceId) throws Exception {
        RequestSpec requestSpec = config.resolveManifestRequest();
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "开始加载 manifest " + requestSpec.describe(), traceId);
        HttpURLConnection connection = openConnection(requestSpec, config.timeoutMillis);
        try {
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "热更新 manifest 不存在，视为当前最新 " + requestSpec.describe(), traceId);
                return HotUpdateManifest.noUpdate("manifest 不存在");
            }
            if (statusCode < 200 || statusCode >= 300) {
                throw new IllegalStateException("热更新 manifest 请求失败 status=" + statusCode + " / " + requestSpec.describe());
            }
            String payload = readResponse(connection.getInputStream());
            HotUpdateManifest manifest = HotUpdateManifest.parse(payload);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "热更新 manifest 已加载 " + manifest.describeInline(), traceId);
            return manifest;
        } finally {
            connection.disconnect();
        }
    }

    private void downloadPatch(HotUpdateConfig config, HotUpdateManifest manifest, File patchFile, String traceId) throws Exception {
        RequestSpec requestSpec = config.resolvePatchRequest(manifest);
        if (requestSpec == null) {
            throw new IllegalStateException("manifest 未提供 patchUrl/patchObjectKey");
        }
        if (patchFile.exists() && !patchFile.delete()) {
            throw new IllegalStateException("无法覆盖已有补丁文件: " + patchFile.getAbsolutePath());
        }
        File parent = patchFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("无法创建补丁目录: " + parent.getAbsolutePath());
        }
        HttpURLConnection connection = openConnection(requestSpec, config.timeoutMillis);
        try {
            int statusCode = connection.getResponseCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new IllegalStateException("补丁下载失败 status=" + statusCode + " / " + requestSpec.describe());
            }
            long totalBytes = manifest.patchSizeBytes > 0 ? manifest.patchSizeBytes : connection.getContentLengthLong();
            boolean knownTotalBytes = totalBytes > 0;
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "开始下载补丁 source=" + requestSpec.describe() + " / totalBytes=" + (knownTotalBytes ? String.valueOf(totalBytes) : "unknown"),
                    traceId
            );
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(patchFile)) {
                byte[] buffer = new byte[8192];
                long downloadedBytes = 0L;
                int lastPercent = -1;
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                    downloadedBytes += length;
                    int displayPercent = resolveDownloadProgressPercent(knownTotalBytes, totalBytes, downloadedBytes);
                    if (displayPercent != lastPercent) {
                        lastPercent = displayPercent;
                        TinkerHotUpdateStateStore.setProgressPercent(appContext, displayPercent);
                        if (knownTotalBytes) {
                            setTip(appContext, "正在下载热更新补丁：" + displayPercent + "%");
                        } else {
                            setTip(appContext, "正在下载热更新补丁...");
                        }
                    }
                }
            }
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "补丁下载完成 file=" + patchFile.getAbsolutePath(), traceId);
        } catch (Exception e) {
            if (patchFile.exists()) {
                patchFile.delete();
            }
            throw e;
        } finally {
            connection.disconnect();
        }
    }

    private void verifyPatch(HotUpdateManifest manifest, File patchFile) throws Exception {
        if (!patchFile.exists() || !patchFile.isFile()) {
            throw new IllegalStateException("补丁文件不存在");
        }
        if (manifest.patchSizeBytes > 0 && patchFile.length() != manifest.patchSizeBytes) {
            throw new IllegalStateException("补丁大小校验失败 local=" + patchFile.length() + " / remote=" + manifest.patchSizeBytes);
        }
        if (!manifest.patchMd5.isEmpty()) {
            String localMd5 = computeMd5(patchFile);
            if (!manifest.patchMd5.equalsIgnoreCase(localMd5)) {
                throw new IllegalStateException("补丁 MD5 校验失败 local=" + localMd5 + " / remote=" + manifest.patchMd5);
            }
        }
    }

    private File resolvePatchFile(Context context, HotUpdateManifest manifest) {
        File directory = new File(context.getFilesDir(), "hot-update");
        return new File(directory, manifest.resolveLocalFileName());
    }

    private HttpURLConnection openConnection(RequestSpec requestSpec, int timeoutMillis) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(requestSpec.url).openConnection();
        connection.setConnectTimeout(timeoutMillis);
        connection.setReadTimeout(timeoutMillis);
        connection.setRequestMethod("GET");
        for (String key : requestSpec.headers.keySet()) {
            connection.setRequestProperty(key, requestSpec.headers.get(key));
        }
        return connection;
    }

    private static String computeMd5(File file) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, length);
            }
        }
        byte[] digest = messageDigest.digest();
        StringBuilder builder = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            String hex = Integer.toHexString(value & 0xFF);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    private static long resolveVersionCode(PackageInfo packageInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return packageInfo.getLongVersionCode();
        }
        return packageInfo.versionCode;
    }

    private static void setTip(Context context, String message) {
        if (context == null) {
            return;
        }
        LegacyHomeStatusRepository.setInfoTips(context, message == null ? "" : message.trim());
    }

    private static String readResponse(InputStream inputStream) throws Exception {
        if (inputStream == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(line);
            }
            return builder.toString();
        }
    }

    private static String safeMessage(Exception exception) {
        if (exception == null || exception.getMessage() == null || exception.getMessage().trim().isEmpty()) {
            return exception == null ? "未知错误" : exception.getClass().getSimpleName();
        }
        return exception.getMessage().trim();
    }

    private static String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private int resolveDownloadProgressPercent(boolean knownTotalBytes, long totalBytes, long downloadedBytes) {
        if (knownTotalBytes && totalBytes > 0L) {
            int rawPercent = (int) Math.min(100L, downloadedBytes * 100L / totalBytes);
            return Math.max(25, Math.min(rawPercent, 94));
        }
        if (downloadedBytes >= 2L * 1024L * 1024L) {
            return 90;
        }
        if (downloadedBytes >= 512L * 1024L) {
            return 75;
        }
        if (downloadedBytes > 0L) {
            return 60;
        }
        return 25;
    }

    private Result resolvePendingResult(Context context, String traceId) {
        String pendingPatchVersion = TinkerHotUpdateStateStore.getPendingPatchVersion(context);
        String pendingPatchMd5 = TinkerHotUpdateStateStore.getPendingPatchMd5(context);
        int progressPercent = TinkerHotUpdateStateStore.getProgressPercent(context);
        if (!TinkerHotUpdateStateStore.isProcessing(context)) {
            return null;
        }
        long pendingStartedAt = TinkerHotUpdateStateStore.getPendingStartedAt(context);
        String pendingTraceId = firstNonBlank(TinkerHotUpdateStateStore.getPendingTraceId(context), traceId);
        if (pendingStartedAt > 0L && System.currentTimeMillis() - pendingStartedAt > PENDING_STALE_MILLIS) {
            TinkerHotUpdateStateStore.clearPending(context);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "发现过期 pending 补丁状态，已自动清理", pendingTraceId);
            return null;
        }
        String summary = pendingPatchVersion.isEmpty()
                ? "热更新补丁正在处理中"
                : "热更新补丁正在处理中 " + pendingPatchVersion;
        String detail = "上一个补丁仍在下载或合成，当前进度 " + Math.max(progressPercent, 1) + "% ，请勿重复检查，完成后应用会自动重启";
        setTip(context, summary);
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, summary + " / pendingMd5=" + emptyAsDash(pendingPatchMd5), pendingTraceId);
        return Result.success(summary, detail);
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.trim().isEmpty()) {
            return first.trim();
        }
        if (second != null && !second.trim().isEmpty()) {
            return second.trim();
        }
        return "NO_TRACE";
    }

    private static String buildLatestSummary(HotUpdateManifest manifest) {
        String patchVersion = manifest == null ? "" : manifest.resolvePatchVersion();
        if (patchVersion == null || patchVersion.trim().isEmpty()) {
            return "当前已是最新热更新";
        }
        return "当前已是最新热更新 " + patchVersion.trim();
    }

    private static String buildIssuedSummary(String patchVersion) {
        if (patchVersion == null || patchVersion.trim().isEmpty()) {
            return "热更新补丁已下发，正在合成";
        }
        return "热更新补丁已下发，正在合成 " + patchVersion.trim();
    }

    public static final class Result {
        private final boolean success;
        private final String summary;
        private final String detail;

        private Result(boolean success, String summary, String detail) {
            this.success = success;
            this.summary = summary == null ? "" : summary.trim();
            this.detail = detail == null ? "" : detail.trim();
        }

        public static Result success(String summary, String detail) {
            return new Result(true, summary, detail);
        }

        public static Result failure(String summary, String detail) {
            return new Result(false, summary, detail);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getSummary() {
            return summary;
        }

        public String getDetail() {
            return detail;
        }
    }

    private static final class HotUpdateConfig {
        private final boolean enabled;
        private final String bucket;
        private final String endpoint;
        private final String accessKeyId;
        private final String accessKeySecret;
        private final boolean secure;
        private final int timeoutMillis;
        private final String manifestUrl;
        private final String manifestObjectKey;
        private final String disabledReason;

        private HotUpdateConfig(
                boolean enabled,
                String bucket,
                String endpoint,
                String accessKeyId,
                String accessKeySecret,
                boolean secure,
                int timeoutMillis,
                String manifestUrl,
                String manifestObjectKey,
                String disabledReason
        ) {
            this.enabled = enabled;
            this.bucket = trim(bucket);
            this.endpoint = trim(endpoint);
            this.accessKeyId = trim(accessKeyId);
            this.accessKeySecret = trim(accessKeySecret);
            this.secure = secure;
            this.timeoutMillis = timeoutMillis <= 0 ? 30000 : timeoutMillis;
            this.manifestUrl = trim(manifestUrl);
            this.manifestObjectKey = normalizeObjectKey(manifestObjectKey);
            this.disabledReason = trim(disabledReason);
        }

        static HotUpdateConfig load(Context context) {
            if (FORCE_TEST_CONFIG) {
                return testing();
            }
            Properties properties = new Properties();
            try (InputStream inputStream = context.getAssets().open(ASSET_NAME)) {
                properties.load(inputStream);
            } catch (Exception e) {
                return disabled("未找到 OSS 配置");
            }
            boolean uploadEnabled = Boolean.parseBoolean(properties.getProperty("enabled", "false"));
            boolean hotUpdateEnabled = Boolean.parseBoolean(properties.getProperty("hotUpdateEnabled", String.valueOf(uploadEnabled)));
            HotUpdateConfig config = new HotUpdateConfig(
                    hotUpdateEnabled,
                    properties.getProperty("bucket", ""),
                    resolveEndpoint(properties),
                    properties.getProperty("accessKeyId", ""),
                    properties.getProperty("accessKeySecret", ""),
                    Boolean.parseBoolean(properties.getProperty("secure", "true")),
                    parseInt(properties.getProperty("hotUpdateTimeoutMillis", properties.getProperty("timeoutMillis", "30000")), 30000),
                    properties.getProperty("hotUpdateManifestUrl", ""),
                    properties.getProperty("hotUpdateManifestObjectKey", ""),
                    hotUpdateEnabled ? "" : "热更新未启用"
            );
            if (!config.enabled) {
                return config;
            }
            if (!config.manifestUrl.isEmpty()) {
                return config;
            }
            if (config.manifestObjectKey.isEmpty()) {
                return disabled("缺少 hotUpdateManifestUrl/hotUpdateManifestObjectKey");
            }
            if (config.bucket.isEmpty() || config.accessKeyId.isEmpty() || config.accessKeySecret.isEmpty() || config.endpoint.isEmpty()) {
                return disabled("OSS 配置缺少 bucket/accessKey/endpoint");
            }
            return config;
        }

        private static HotUpdateConfig testing() {
            return new HotUpdateConfig(
                    true,
                    TEST_OSS_BUCKET,
                    TEST_OSS_ENDPOINT,
                    TEST_OSS_ACCESS_KEY_ID,
                    TEST_OSS_ACCESS_KEY_SECRET,
                    true,
                    TEST_TIMEOUT_MILLIS,
                    "",
                    TEST_MANIFEST_OBJECT_KEY,
                    ""
            );
        }

        boolean isUsable() {
            return enabled;
        }

        RequestSpec resolveManifestRequest() throws Exception {
            if (!manifestUrl.isEmpty()) {
                return new RequestSpec(manifestUrl);
            }
            return buildSignedRequest(manifestObjectKey);
        }

        RequestSpec resolvePatchRequest(HotUpdateManifest manifest) throws Exception {
            if (!manifest.patchUrl.isEmpty()) {
                return new RequestSpec(manifest.patchUrl);
            }
            if (!manifest.patchObjectKey.isEmpty()) {
                return buildSignedRequest(manifest.patchObjectKey);
            }
            return null;
        }

        private RequestSpec buildSignedRequest(String objectKey) throws Exception {
            String normalizedObjectKey = normalizeObjectKey(objectKey);
            String targetUrl = resolveScheme() + "://" + bucket + "." + endpoint + "/" + encodeObjectKey(normalizedObjectKey);
            String dateHeader = formatRfc1123(new Date());
            String authorization = buildAuthorization(bucket, normalizedObjectKey, accessKeyId, accessKeySecret, dateHeader);
            RequestSpec requestSpec = new RequestSpec(targetUrl);
            requestSpec.headers.put("Date", dateHeader);
            requestSpec.headers.put("Authorization", authorization);
            return requestSpec;
        }

        private String resolveScheme() {
            return secure ? "https" : "http";
        }

        private static String resolveEndpoint(Properties properties) {
            String endpoint = trim(properties.getProperty("endpoint", ""));
            if (!endpoint.isEmpty()) {
                return stripScheme(endpoint);
            }
            String region = trim(properties.getProperty("region", ""));
            if (region.isEmpty()) {
                return "";
            }
            return stripScheme(region) + ".aliyuncs.com";
        }

        private static HotUpdateConfig disabled(String reason) {
            return new HotUpdateConfig(false, "", "", "", "", true, 30000, "", "", reason);
        }
    }

    private static final class HotUpdateManifest {
        private final boolean enabled;
        private final boolean noUpdate;
        private final String noUpdateReason;
        private final String patchVersion;
        private final long targetVersionCode;
        private final String targetVersionName;
        private final String patchUrl;
        private final String patchObjectKey;
        private final String patchMd5;
        private final long patchSizeBytes;
        private final String releaseNotes;

        private HotUpdateManifest(
                boolean enabled,
            boolean noUpdate,
            String noUpdateReason,
                String patchVersion,
                long targetVersionCode,
                String targetVersionName,
                String patchUrl,
                String patchObjectKey,
                String patchMd5,
                long patchSizeBytes,
                String releaseNotes
        ) {
            this.enabled = enabled;
            this.noUpdate = noUpdate;
            this.noUpdateReason = trim(noUpdateReason);
            this.patchVersion = trim(patchVersion);
            this.targetVersionCode = targetVersionCode;
            this.targetVersionName = trim(targetVersionName);
            this.patchUrl = trim(patchUrl);
            this.patchObjectKey = normalizeObjectKey(patchObjectKey);
            this.patchMd5 = trim(patchMd5);
            this.patchSizeBytes = patchSizeBytes;
            this.releaseNotes = trim(releaseNotes);
        }

        static HotUpdateManifest parse(String payload) throws Exception {
            JSONObject object = new JSONObject(payload);
            return new HotUpdateManifest(
                    object.optBoolean("enabled", true),
                    false,
                    "",
                    coalesce(object.optString("patchVersion", ""), object.optString("version", "")),
                    optLong(object, "targetVersionCode", optLong(object, "baseVersionCode", 0L)),
                    coalesce(object.optString("targetVersionName", ""), object.optString("baseVersionName", "")),
                    object.optString("patchUrl", ""),
                    object.optString("patchObjectKey", ""),
                    object.optString("patchMd5", ""),
                    optLong(object, "patchSizeBytes", 0L),
                    object.optString("releaseNotes", "")
            );
        }

        static HotUpdateManifest noUpdate(String reason) {
            return new HotUpdateManifest(false, true, reason, "", 0L, "", "", "", "", 0L, "");
        }

        boolean isNoUpdate() {
            return noUpdate;
        }

        String resolvePatchVersion() {
            if (!patchVersion.isEmpty()) {
                return patchVersion;
            }
            if (!patchMd5.isEmpty()) {
                return patchMd5;
            }
            if (!patchObjectKey.isEmpty()) {
                return patchObjectKey;
            }
            return patchUrl;
        }

        String resolveLocalFileName() {
            String candidate = !patchObjectKey.isEmpty() ? patchObjectKey : patchUrl;
            if (candidate == null || candidate.trim().isEmpty()) {
                return "patch.apk";
            }
            String normalized = candidate.replace('\\', '/');
            int separator = normalized.lastIndexOf('/');
            String name = separator >= 0 ? normalized.substring(separator + 1) : normalized;
            if (name.trim().isEmpty()) {
                return "patch.apk";
            }
            return name.trim();
        }

        String describeInline() {
            StringBuilder builder = new StringBuilder();
            if (noUpdate) {
                builder.append("status=no-update");
                if (!noUpdateReason.isEmpty()) {
                    builder.append(" / reason=").append(noUpdateReason);
                }
                return builder.toString();
            }
            builder.append("patchVersion=").append(emptyAsDash(resolvePatchVersion()));
            if (targetVersionCode > 0) {
                builder.append(" / targetVersionCode=").append(targetVersionCode);
            }
            if (!targetVersionName.isEmpty()) {
                builder.append(" / targetVersionName=").append(targetVersionName);
            }
            if (!releaseNotes.isEmpty()) {
                builder.append(" / notes=").append(releaseNotes.replace('\n', ' ').trim());
            }
            return builder.toString();
        }
    }

    private static final class RequestSpec {
        private final String url;
        private final java.util.Map<String, String> headers = new java.util.LinkedHashMap<>();

        private RequestSpec(String url) {
            this.url = trim(url);
        }

        private String describe() {
            return url;
        }
    }

    private static long optLong(JSONObject object, String key, long fallback) {
        Object value = object.opt(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong(((String) value).trim());
            } catch (Exception ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(trim(value));
        } catch (Exception e) {
            return fallback;
        }
    }

    private static String buildAuthorization(String bucket, String objectKey, String accessKeyId, String accessKeySecret, String dateHeader) throws Exception {
        String canonicalizedResource = "/" + bucket + "/" + objectKey;
        String stringToSign = "GET\n\n\n" + dateHeader + "\n" + canonicalizedResource;
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(accessKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        String signature = Base64.encodeToString(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)), Base64.NO_WRAP);
        return "OSS " + accessKeyId + ":" + signature;
    }

    private static String formatRfc1123(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

    private static String encodeObjectKey(String objectKey) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < objectKey.length(); index++) {
            char ch = objectKey.charAt(index);
            if (ch == '/') {
                builder.append('/');
            } else if (isUnreserved(ch)) {
                builder.append(ch);
            } else {
                byte[] bytes = String.valueOf(ch).getBytes(StandardCharsets.UTF_8);
                for (byte value : bytes) {
                    builder.append('%');
                    String hex = Integer.toHexString(value & 0xFF).toUpperCase(Locale.US);
                    if (hex.length() == 1) {
                        builder.append('0');
                    }
                    builder.append(hex);
                }
            }
        }
        return builder.toString();
    }

    private static boolean isUnreserved(char ch) {
        return (ch >= 'A' && ch <= 'Z')
                || (ch >= 'a' && ch <= 'z')
                || (ch >= '0' && ch <= '9')
                || ch == '-' || ch == '_' || ch == '.' || ch == '~';
    }

    private static String normalizeObjectKey(String value) {
        String text = trim(value);
        while (text.startsWith("/")) {
            text = text.substring(1);
        }
        return text;
    }

    private static String stripScheme(String value) {
        String text = trim(value);
        if (text.startsWith("https://")) {
            return text.substring("https://".length());
        }
        if (text.startsWith("http://")) {
            return text.substring("http://".length());
        }
        return text;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static String coalesce(String first, String second) {
        String left = trim(first);
        if (!left.isEmpty()) {
            return left;
        }
        return trim(second);
    }
}