package com.lhxy.istationdevice.android11.core;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志导出器
 * <p>
 * 把当前内存里的日志导出到应用目录，方便真机联调后直接拿文件。
 */
public final class AppLogExporter {
    private AppLogExporter() {
    }

    /**
     * 导出日志到应用目录。
     *
     * @param context 应用上下文
     * @param prefix  文件名前缀
     * @return 导出的日志文件
     * @throws Exception 写文件失败时抛出异常
     */
    public static File export(Context context, String prefix) throws Exception {
        File baseDir = context.getExternalFilesDir("exports");
        if (baseDir == null) {
            baseDir = new File(context.getFilesDir(), "exports");
        }
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IllegalStateException("无法创建日志导出目录: " + baseDir.getAbsolutePath());
        }

        String fileName = (prefix == null || prefix.trim().isEmpty() ? "app-log" : prefix.trim())
                + "-"
                + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date())
                + ".log";
        File exportFile = new File(baseDir, fileName);

        try (FileOutputStream outputStream = new FileOutputStream(exportFile)) {
            outputStream.write(AppLogCenter.dumpPlainText().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        return exportFile;
    }
}
