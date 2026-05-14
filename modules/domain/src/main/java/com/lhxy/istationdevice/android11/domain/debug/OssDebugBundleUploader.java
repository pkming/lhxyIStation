package com.lhxy.istationdevice.android11.domain.debug;

import android.content.Context;
import android.util.Base64;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class OssDebugBundleUploader {
    private static final String TAG = "OssDebugBundleUploader";
    private static final String ZIP_CONTENT_TYPE = "application/zip";
    private static final String TEXT_CONTENT_TYPE = "text/plain; charset=utf-8";

    private OssDebugBundleUploader() {
    }

    static UploadResult uploadIfConfigured(Context context, File file) {
        OssUploadConfig config = OssUploadConfig.load(context);
        if (!config.isUsable()) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "跳过 OSS 上传: " + config.disabledReason, null);
            return UploadResult.skipped(config.disabledReason);
        }
        if (file == null || !file.exists() || !file.isFile()) {
            UploadResult result = UploadResult.failure("本地日志文件不存在", "", "", -1);
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "OSS 上传跳过: 本地日志文件不存在", null);
            return result;
        }

        Date uploadTime = new Date();
        String objectKey = buildObjectKey(config.objectPrefix, uploadTime, "zip");
        String url = config.resolveScheme() + "://" + config.bucket + "." + config.resolveEndpoint() + "/" + encodeObjectKey(objectKey);
        try {
            int statusCode = uploadFile(config, file, objectKey, url, uploadTime, ZIP_CONTENT_TYPE);
            if (statusCode >= 200 && statusCode < 300) {
                UploadResult result = UploadResult.success(objectKey, url, statusCode);
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "OSS 上传成功: " + result.describeForUser(), null);
                return result;
            }
            UploadResult result = UploadResult.failure("OSS 上传失败 status=" + statusCode, objectKey, url, statusCode);
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, result.describeForUser(), null);
            return result;
        } catch (Exception e) {
            UploadResult result = UploadResult.failure("OSS 上传异常: " + e.getClass().getSimpleName() + ": " + safeMessage(e), objectKey, url, -1);
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, result.describeForUser(), null);
            return result;
        }
    }

    static UploadResult uploadTextIfConfigured(Context context, String content) {
        OssUploadConfig config = OssUploadConfig.load(context);
        if (!config.isUsable()) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "跳过 OSS TXT 上传: " + config.disabledReason, null);
            return UploadResult.skipped(config.disabledReason);
        }
        byte[] payload = (content == null ? "" : content).getBytes(StandardCharsets.UTF_8);
        Date uploadTime = new Date();
        String objectKey = buildObjectKey(config.objectPrefix, uploadTime, "txt");
        String url = config.resolveScheme() + "://" + config.bucket + "." + config.resolveEndpoint() + "/" + encodeObjectKey(objectKey);
        try {
            int statusCode = uploadBytes(config, payload, objectKey, url, uploadTime, TEXT_CONTENT_TYPE);
            if (statusCode >= 200 && statusCode < 300) {
                UploadResult result = UploadResult.success(objectKey, url, statusCode);
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "OSS TXT 上传成功: " + result.describeForUser(), null);
                return result;
            }
            UploadResult result = UploadResult.failure("OSS TXT 上传失败 status=" + statusCode, objectKey, url, statusCode);
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, result.describeForUser(), null);
            return result;
        } catch (Exception e) {
            UploadResult result = UploadResult.failure("OSS TXT 上传异常: " + e.getClass().getSimpleName() + ": " + safeMessage(e), objectKey, url, -1);
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, result.describeForUser(), null);
            return result;
        }
    }

    static UploadResult uploadZippedTextIfConfigured(Context context, String content) {
        OssUploadConfig config = OssUploadConfig.load(context);
        if (!config.isUsable()) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "跳过 OSS 合并日志上传: " + config.disabledReason, null);
            return UploadResult.skipped(config.disabledReason);
        }
        Date uploadTime = new Date();
        String objectKey = buildObjectKey(config.objectPrefix, uploadTime, "zip");
        String url = config.resolveScheme() + "://" + config.bucket + "." + config.resolveEndpoint() + "/" + encodeObjectKey(objectKey);
        try {
            byte[] payload = zipText(content, buildTextEntryName(uploadTime));
            int statusCode = uploadBytes(config, payload, objectKey, url, uploadTime, ZIP_CONTENT_TYPE);
            if (statusCode >= 200 && statusCode < 300) {
                UploadResult result = UploadResult.success(objectKey, url, statusCode);
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "OSS 合并日志 ZIP 上传成功: " + result.describeForUser(), null);
                return result;
            }
            UploadResult result = UploadResult.failure("OSS 合并日志 ZIP 上传失败 status=" + statusCode, objectKey, url, statusCode);
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, result.describeForUser(), null);
            return result;
        } catch (Exception e) {
            UploadResult result = UploadResult.failure("OSS 合并日志 ZIP 上传异常: " + e.getClass().getSimpleName() + ": " + safeMessage(e), objectKey, url, -1);
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, result.describeForUser(), null);
            return result;
        }
    }

    private static byte[] zipText(String content, String entryName) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream, StandardCharsets.UTF_8)) {
            zipOutputStream.putNextEntry(new ZipEntry(entryName));
            zipOutputStream.write((content == null ? "" : content).getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static String buildTextEntryName(Date uploadTime) {
        return "android11-test-log-" + new SimpleDateFormat("yyyyMMdd-HHmmssSSS", Locale.US).format(uploadTime) + ".txt";
    }

    private static int uploadFile(OssUploadConfig config, File file, String objectKey, String targetUrl, Date uploadTime, String contentType) throws Exception {
        String dateHeader = formatRfc1123(uploadTime);
        String authorization = buildAuthorization(config, objectKey, dateHeader, contentType);
        HttpURLConnection connection = openPutConnection(config, targetUrl, dateHeader, authorization, contentType, file.length());
        try (OutputStream outputStream = connection.getOutputStream(); FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
        return finishUpload(connection);
    }

    private static int uploadBytes(OssUploadConfig config, byte[] payload, String objectKey, String targetUrl, Date uploadTime, String contentType) throws Exception {
        String dateHeader = formatRfc1123(uploadTime);
        String authorization = buildAuthorization(config, objectKey, dateHeader, contentType);
        HttpURLConnection connection = openPutConnection(config, targetUrl, dateHeader, authorization, contentType, payload.length);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(payload);
        }
        return finishUpload(connection);
    }

    private static HttpURLConnection openPutConnection(OssUploadConfig config, String targetUrl, String dateHeader, String authorization, String contentType, long contentLength) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();
        connection.setConnectTimeout(config.timeoutMillis);
        connection.setReadTimeout(config.timeoutMillis);
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Date", dateHeader);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Authorization", authorization);
        connection.setFixedLengthStreamingMode(contentLength);
        return connection;
    }

    private static int finishUpload(HttpURLConnection connection) throws Exception {
        int statusCode = connection.getResponseCode();
        if (statusCode < 200 || statusCode >= 300) {
            String response = readResponse(connection);
            if (!response.isEmpty()) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "OSS 上传响应: " + compact(response), null);
            }
        }
        connection.disconnect();
        return statusCode;
    }

    private static String buildAuthorization(OssUploadConfig config, String objectKey, String dateHeader, String contentType) throws Exception {
        String canonicalizedResource = "/" + config.bucket + "/" + objectKey;
        String stringToSign = "PUT\n\n" + contentType + "\n" + dateHeader + "\n" + canonicalizedResource;
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(config.accessKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        String signature = Base64.encodeToString(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)), Base64.NO_WRAP);
        return "OSS " + config.accessKeyId + ":" + signature;
    }

    private static String buildObjectKey(String objectPrefix, Date uploadTime, String extension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmssSSS", Locale.US);
        String prefix = normalizePrefix(objectPrefix);
        return prefix + dateFormat.format(uploadTime) + "/" + timeFormat.format(uploadTime) + "." + extension;
    }

    private static String normalizePrefix(String objectPrefix) {
        String prefix = objectPrefix == null ? "" : objectPrefix.trim();
        while (prefix.startsWith("/")) {
            prefix = prefix.substring(1);
        }
        while (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix.isEmpty() ? "" : prefix + "/";
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

    private static String readResponse(HttpURLConnection connection) {
        InputStream stream = connection.getErrorStream();
        if (stream == null) {
            try {
                stream = connection.getInputStream();
            } catch (Exception ignored) {
                return "";
            }
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static String compact(String value) {
        if (value == null) {
            return "";
        }
        String text = value.replace('\n', ' ').replace('\r', ' ').trim();
        return text.length() <= 500 ? text : text.substring(0, 500) + "...";
    }

    private static String safeMessage(Exception e) {
        return e.getMessage() == null ? "-" : e.getMessage();
    }

    static final class UploadResult {
        private final boolean attempted;
        private final boolean success;
        private final String message;
        private final String objectKey;
        private final String url;
        private final int statusCode;

        private UploadResult(boolean attempted, boolean success, String message, String objectKey, String url, int statusCode) {
            this.attempted = attempted;
            this.success = success;
            this.message = message == null ? "" : message;
            this.objectKey = objectKey == null ? "" : objectKey;
            this.url = url == null ? "" : url;
            this.statusCode = statusCode;
        }

        static UploadResult skipped(String message) {
            return new UploadResult(false, false, message, "", "", -1);
        }

        static UploadResult success(String objectKey, String url, int statusCode) {
            return new UploadResult(true, true, "上传成功", objectKey, url, statusCode);
        }

        static UploadResult failure(String message, String objectKey, String url, int statusCode) {
            return new UploadResult(true, false, message, objectKey, url, statusCode);
        }

        public boolean isAttempted() {
            return attempted;
        }

        public boolean isSuccess() {
            return success;
        }

        public String describeForUser() {
            if (!attempted) {
                return message.isEmpty() ? "未上传" : message;
            }
            StringBuilder builder = new StringBuilder(success ? "上传成功" : message);
            if (!objectKey.isEmpty()) {
                builder.append("，object=").append(objectKey);
            }
            if (!url.isEmpty()) {
                builder.append("，url=").append(url);
            }
            if (statusCode > 0) {
                builder.append("，status=").append(statusCode);
            }
            return builder.toString();
        }
    }
}