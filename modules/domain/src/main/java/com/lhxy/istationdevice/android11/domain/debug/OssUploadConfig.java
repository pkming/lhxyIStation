package com.lhxy.istationdevice.android11.domain.debug;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class OssUploadConfig {
    private static final String ASSET_NAME = "oss-config.properties";

    final boolean enabled;
    final String region;
    final String endpoint;
    final String accessKeyId;
    final String accessKeySecret;
    final String bucket;
    final boolean secure;
    final String objectPrefix;
    final int timeoutMillis;
    final String disabledReason;

    private OssUploadConfig(
            boolean enabled,
            String region,
            String endpoint,
            String accessKeyId,
            String accessKeySecret,
            String bucket,
            boolean secure,
            String objectPrefix,
            int timeoutMillis,
            String disabledReason
    ) {
        this.enabled = enabled;
        this.region = trim(region);
        this.endpoint = trim(endpoint);
        this.accessKeyId = trim(accessKeyId);
        this.accessKeySecret = trim(accessKeySecret);
        this.bucket = trim(bucket);
        this.secure = secure;
        this.objectPrefix = trim(objectPrefix);
        this.timeoutMillis = timeoutMillis <= 0 ? 30000 : timeoutMillis;
        this.disabledReason = trim(disabledReason);
    }

    static OssUploadConfig load(Context context) {
        if (context == null) {
            return disabled("当前没有可用上下文");
        }
        Properties properties = new Properties();
        try (InputStream inputStream = context.getAssets().open(ASSET_NAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            return disabled("未找到 OSS 配置");
        }

        boolean enabled = Boolean.parseBoolean(properties.getProperty("enabled", "false"));
        String region = properties.getProperty("region", "");
        String endpoint = properties.getProperty("endpoint", "");
        String accessKeyId = properties.getProperty("accessKeyId", "");
        String accessKeySecret = properties.getProperty("accessKeySecret", "");
        String bucket = properties.getProperty("bucket", "");
        boolean secure = Boolean.parseBoolean(properties.getProperty("secure", "true"));
        String objectPrefix = properties.getProperty("objectPrefix", "");
        int timeoutMillis = parseInt(properties.getProperty("timeoutMillis", "30000"), 30000);
        OssUploadConfig config = new OssUploadConfig(
                enabled,
                region,
                endpoint,
                accessKeyId,
                accessKeySecret,
                bucket,
                secure,
                objectPrefix,
                timeoutMillis,
                enabled ? "" : "OSS 上传未启用"
        );
        if (!config.enabled) {
            return config;
        }
        if (config.bucket.isEmpty() || config.accessKeyId.isEmpty() || config.accessKeySecret.isEmpty()) {
            return new OssUploadConfig(false, region, endpoint, accessKeyId, accessKeySecret, bucket, secure, objectPrefix, timeoutMillis, "OSS 配置缺少 bucket/accessKey");
        }
        if (config.resolveEndpoint().isEmpty()) {
            return new OssUploadConfig(false, region, endpoint, accessKeyId, accessKeySecret, bucket, secure, objectPrefix, timeoutMillis, "OSS 配置缺少 region/endpoint");
        }
        return config;
    }

    boolean isUsable() {
        return enabled;
    }

    String resolveEndpoint() {
        if (!endpoint.isEmpty()) {
            return stripScheme(endpoint);
        }
        if (region.isEmpty()) {
            return "";
        }
        return stripScheme(region) + ".aliyuncs.com";
    }

    String resolveScheme() {
        return secure ? "https" : "http";
    }

    private static OssUploadConfig disabled(String reason) {
        return new OssUploadConfig(false, "", "", "", "", "", true, "", 30000, reason);
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(trim(value));
        } catch (Exception e) {
            return fallback;
        }
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
}