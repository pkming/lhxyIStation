package com.lhxy.istationdevice.android11.domain.debug;

import android.content.Context;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigLoader;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigValidator;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.protocol.ProtocolMockCatalog;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 调试包导出器
 * <p>
 * 把日志、当前生效配置和底座状态一起导出，方便现场问题留档。
 */
public final class DebugBundleExporter {
    private DebugBundleExporter() {
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
            writeEntry(outputStream, "logs/app-log.txt", AppLogCenter.dumpPlainText());
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

        return exportFile;
    }

    private static void writeEntry(ZipOutputStream outputStream, String entryName, String content) throws Exception {
        ZipEntry zipEntry = new ZipEntry(entryName);
        outputStream.putNextEntry(zipEntry);
        outputStream.write((content == null ? "" : content).getBytes(StandardCharsets.UTF_8));
        outputStream.closeEntry();
    }
}
