package com.lhxy.istationdevice.android11.devicem90;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * M90 命令辅助
 * <p>
 * 设备底座里有几块能力只能先靠文件或 shell 命令桥接，
 * 这类逻辑统一放这里，避免散在每个适配器里各写一份。
 */
final class M90CommandSupport {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            .withZone(ZoneId.systemDefault());

    private M90CommandSupport() {
    }

    /**
     * 执行命令并返回标准输出。
     */
    static String execForText(String command) throws Exception {
        Process process = new ProcessBuilder("sh", "-c", command).redirectErrorStream(true).start();
        String output = readText(process.getInputStream()).trim();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("命令执行失败(" + exitCode + "): " + command + " / " + output);
        }
        return output;
    }

    /**
     * 执行命令，不关心输出。
     */
    static void exec(String command) throws Exception {
        execForText(command);
    }

    /**
     * 读取文本文件。
     */
    static String readFileText(File file) throws Exception {
        if (file == null) {
            throw new IllegalArgumentException("file 不能为空");
        }
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return readText(inputStream).trim();
        }
    }

    /**
     * 套用时间占位符。
     */
    static String fillTimeCommand(String template, long timeMillis) {
        return safe(template)
                .replace("%millis%", String.valueOf(timeMillis))
                .replace("%seconds%", String.valueOf(timeMillis / 1000L))
                .replace("%iso%", ISO_FORMATTER.format(Instant.ofEpochMilli(timeMillis)));
    }

    /**
     * 套用通用文本占位符。
     */
    static String fillValueCommand(String template, String value) {
        return safe(template).replace("%value%", safe(value));
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String readText(InputStream inputStream) throws Exception {
        try (InputStream source = inputStream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = source.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            return output.toString(StandardCharsets.UTF_8.name());
        }
    }
}
