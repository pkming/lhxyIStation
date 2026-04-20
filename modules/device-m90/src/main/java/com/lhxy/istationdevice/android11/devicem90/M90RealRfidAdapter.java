package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * M90 真 RFID 适配器
 * <p>
 * 当前先支持两种桥接方式：
 * 1. 从配置文件读取卡号
 * 2. 执行配置命令读取卡号
 * 真机 SDK 到位后，只替这层。
 */
public final class M90RealRfidAdapter implements RfidAdapter {
    private static final String TAG = "M90RealRfid";

    private volatile ShellConfig.RfidConfig rfidConfig = ShellConfig.RfidConfig.stub();
    private volatile String lastCardNo = "";

    /**
     * 更新 RFID 配置。
     */
    public void updateConfig(ShellConfig.RfidConfig rfidConfig) {
        this.rfidConfig = rfidConfig == null ? ShellConfig.RfidConfig.stub() : rfidConfig;
        this.lastCardNo = "";
    }

    @Override
    public boolean isAvailable() {
        return !rfidConfig.getInputFilePath().trim().isEmpty()
                || !rfidConfig.getReadCommand().trim().isEmpty()
                || !rfidConfig.getMockCardNo().trim().isEmpty();
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

    /**
     * 返回最近一次读到的卡号。
     */
    public String getLastCardNo() {
        return lastCardNo;
    }

    private String tryReadCard() throws Exception {
        if (!rfidConfig.getInputFilePath().trim().isEmpty()) {
            File inputFile = new File(rfidConfig.getInputFilePath().trim());
            if (inputFile.exists()) {
                return M90CommandSupport.readFileText(inputFile);
            }
        }
        if (!rfidConfig.getReadCommand().trim().isEmpty()) {
            return M90CommandSupport.execForText(rfidConfig.getReadCommand().trim()).trim();
        }
        return rfidConfig.getMockCardNo().trim();
    }
}
