package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * M90 RFID 管理适配器
 * <p>
 * 统一管理 stub / real 切换和配置。
 */
public final class M90ManagedRfidAdapter implements RfidAdapter {
    private final M90StubRfidAdapter stubAdapter = new M90StubRfidAdapter();
    private final M90RealRfidAdapter realAdapter = new M90RealRfidAdapter();
    private volatile ShellConfig.RfidConfig rfidConfig = ShellConfig.RfidConfig.stub();

    /**
     * 应用最新 RFID 配置。
     */
    public void updateConfig(ShellConfig.RfidConfig rfidConfig) {
        this.rfidConfig = rfidConfig == null ? ShellConfig.RfidConfig.stub() : rfidConfig;
        stubAdapter.applyConfig(this.rfidConfig);
        realAdapter.updateConfig(this.rfidConfig);
    }

    @Override
    public boolean isAvailable() {
        return delegate().isAvailable();
    }

    @Override
    public String readCard(String traceId) {
        return delegate().readCard(traceId);
    }

    /**
     * 输出 RFID 当前状态摘要。
     */
    public String describeStatus() {
        String lastCardNo = rfidConfig.getMode() == DeviceMode.REAL ? realAdapter.getLastCardNo() : stubAdapter.getLastCardNo();
        return "RFID -> mode=" + rfidConfig.getMode().toConfigValue()
                + " / available=" + delegate().isAvailable()
                + " / lastCard=" + (lastCardNo == null || lastCardNo.trim().isEmpty() ? "-" : lastCardNo.trim());
    }

    private RfidAdapter delegate() {
        return rfidConfig.getMode() == DeviceMode.REAL ? realAdapter : stubAdapter;
    }
}
