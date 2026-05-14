package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.io.File;

/**
 * M90 真 RFID 适配器
 * <p>
 * 优先复用 M90 原生 RFID JNI；本地文件/命令仍保留为回退桥接。
 */
public final class M90RealRfidAdapter implements RfidAdapter {
    private static final String TAG = "M90RealRfid";
    private static final byte M90_CARD_PRESENT_FLAG = 0x38;
    private static final byte M90_RFID_CHANNEL = 0x01;

    private volatile ShellConfig.RfidConfig rfidConfig = ShellConfig.RfidConfig.stub();
    private volatile String lastCardNo = "";
    private final Object nativeLock = new Object();
    private volatile M90I2CPort nativePort;
    private volatile boolean nativeInitAttempted;
    private volatile boolean nativeReady;

    /**
     * 更新 RFID 配置。
     */
    public void updateConfig(ShellConfig.RfidConfig rfidConfig) {
        this.rfidConfig = rfidConfig == null ? ShellConfig.RfidConfig.stub() : rfidConfig;
        this.lastCardNo = "";
        synchronized (nativeLock) {
            if (nativePort != null) {
                try {
                    nativePort.RfidClose();
                } catch (Throwable ignore) {
                    // 某些设备不实现 close，不影响重新初始化。
                }
            }
            nativePort = null;
            nativeInitAttempted = false;
            nativeReady = false;
        }
    }

    @Override
    public boolean isAvailable() {
        return (rfidConfig.getMode() == DeviceMode.REAL && M90I2CPort.isLibraryLoaded())
                || hasFallbackBridge();
    }

    @Override
    public String readCard(String traceId) {
        try {
            String cardNo = tryReadCard();
            lastCardNo = cardNo == null ? "" : cardNo.trim();
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "real readCard -> " + (lastCardNo.isEmpty() ? "-" : lastCardNo), traceId);
            return lastCardNo;
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "real readCard failed: " + e.getMessage(), traceId);
            throw new IllegalStateException("RFID 读卡失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean waitCardRemoved(String traceId, long timeoutMs, long pollIntervalMs) {
        if (shouldUseNative()) {
            if (!ensureNativeReady(traceId)) {
                return false;
            }
            try {
                nativePort.RfidWaitCardOff();
                lastCardNo = "";
                AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "native waitCardRemoved -> removed", traceId);
                return true;
            } catch (Throwable throwable) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "native waitCardRemoved failed: " + throwable.getMessage(), traceId);
                return false;
            }
        }

        long boundedPollInterval = Math.max(50L, pollIntervalMs);
        long deadline = timeoutMs <= 0 ? Long.MAX_VALUE : System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() <= deadline) {
            try {
                String cardNo = tryReadCard();
                if (cardNo == null || cardNo.trim().isEmpty()) {
                    lastCardNo = "";
                    AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "real waitCardRemoved -> removed", traceId);
                    return true;
                }
                Thread.sleep(boundedPollInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "real waitCardRemoved interrupted", traceId);
                return false;
            } catch (Exception e) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "real waitCardRemoved failed: " + e.getMessage(), traceId);
                return false;
            }
        }
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "real waitCardRemoved timeout", traceId);
        return false;
    }

    /**
     * 返回最近一次读到的卡号。
     */
    public String getLastCardNo() {
        return lastCardNo;
    }

    private String tryReadCard() throws Exception {
        if (shouldUseNative()) {
            if (!ensureNativeReady("rfid-native-init")) {
                throw new IllegalStateException("M90 RFID JNI 未就绪: " + M90I2CPort.getLoadErrorMessage());
            }
            return readNativeCard();
        }

        if (!rfidConfig.getInputFilePath().trim().isEmpty()) {
            File inputFile = new File(rfidConfig.getInputFilePath().trim());
            if (inputFile.exists()) {
                return M90CommandSupport.readFileText(inputFile);
            }
        }
        if (!rfidConfig.getReadCommand().trim().isEmpty()) {
            return M90CommandSupport.execForText(rfidConfig.getReadCommand().trim()).trim();
        }
        if (!rfidConfig.getI2cDevicePath().trim().isEmpty() && rfidConfig.getMockCardNo().trim().isEmpty()) {
            throw new IllegalStateException("RFID I2C-3 已配置，但 M90 RFID JNI 不可用且缺少回退桥接");
        }
        return rfidConfig.getMockCardNo().trim();
    }

    private boolean shouldUseNative() {
        return rfidConfig.getMode() == DeviceMode.REAL && M90I2CPort.isLibraryLoaded();
    }

    private boolean hasFallbackBridge() {
        return !rfidConfig.getInputFilePath().trim().isEmpty()
                || !rfidConfig.getReadCommand().trim().isEmpty()
                || !rfidConfig.getMockCardNo().trim().isEmpty();
    }

    private boolean ensureNativeReady(String traceId) {
        if (nativeReady && nativePort != null) {
            return true;
        }
        synchronized (nativeLock) {
            if (nativeReady && nativePort != null) {
                return true;
            }
            if (!M90I2CPort.isLibraryLoaded()) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "native RFID library missing: " + M90I2CPort.getLoadErrorMessage(), traceId);
                return false;
            }
            if (nativeInitAttempted) {
                return false;
            }
            nativeInitAttempted = true;
            try {
                M90I2CPort port = new M90I2CPort();
                port.RfidInit();
                nativePort = port;
                nativeReady = true;
                AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "native RFID init ready", traceId);
                return true;
            } catch (Throwable throwable) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "native RFID init failed: " + throwable.getMessage(), traceId);
                nativePort = null;
                nativeReady = false;
                return false;
            }
        }
    }

    private String readNativeCard() {
        byte[] raw = nativePort.RfidGetId(M90_RFID_CHANNEL);
        if (raw == null || raw.length == 0 || raw[0] != M90_CARD_PRESENT_FLAG) {
            return "";
        }
        return toHexString(raw);
    }

    private String toHexString(byte[] value) {
        StringBuilder builder = new StringBuilder(value.length * 2);
        for (byte item : value) {
            int current = item & 0xFF;
            if (current < 0x10) {
                builder.append('0');
            }
            builder.append(Integer.toHexString(current).toUpperCase());
        }
        return builder.toString();
    }
}
