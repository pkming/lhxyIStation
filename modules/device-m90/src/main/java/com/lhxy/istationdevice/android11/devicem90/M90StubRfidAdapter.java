package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

public final class M90StubRfidAdapter implements RfidAdapter {
    private static final String TAG = "M90StubRfid";
    private volatile String mockCardNo = "RFID-DEMO-001";
    private volatile String lastCardNo = "";

    /**
     * 应用默认 RFID 配置。
     */
    public void applyConfig(ShellConfig.RfidConfig rfidConfig) {
        mockCardNo = rfidConfig == null ? "RFID-DEMO-001" : rfidConfig.getMockCardNo();
        lastCardNo = "";
    }

    @Override
    public boolean isAvailable() {
        return mockCardNo != null && !mockCardNo.trim().isEmpty();
    }

    @Override
    public String readCard(String traceId) {
        lastCardNo = mockCardNo == null ? "" : mockCardNo.trim();
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "stub readCard -> " + (lastCardNo.isEmpty() ? "-" : lastCardNo), traceId);
        return lastCardNo;
    }

    /**
     * 返回最近一次模拟卡号。
     */
    public String getLastCardNo() {
        return lastCardNo;
    }
}
