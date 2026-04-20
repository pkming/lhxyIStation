package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * M90 系统能力管理适配器
 * <p>
 * 统一管理 stub / real 切换。
 */
public final class M90ManagedSystemOps implements SystemOps {
    private final M90StubSystemOps stubSystemOps = new M90StubSystemOps();
    private final M90RealSystemOps realSystemOps = new M90RealSystemOps();
    private volatile ShellConfig.SystemConfig systemConfig = ShellConfig.SystemConfig.stub();

    /**
     * 应用最新系统能力配置。
     */
    public void updateConfig(ShellConfig.SystemConfig systemConfig) {
        this.systemConfig = systemConfig == null ? ShellConfig.SystemConfig.stub() : systemConfig;
        stubSystemOps.applyConfig(this.systemConfig);
        realSystemOps.updateConfig(this.systemConfig);
    }

    @Override
    public boolean supportsSilentInstall() {
        return delegate().supportsSilentInstall();
    }

    @Override
    public void reboot(String reason, String traceId) {
        delegate().reboot(reason, traceId);
    }

    @Override
    public void setSystemTime(long timeMillis, String traceId) {
        delegate().setSystemTime(timeMillis, traceId);
    }

    /**
     * 输出系统能力当前状态摘要。
     */
    public String describeStatus() {
        return "SystemOps -> mode=" + systemConfig.getMode().toConfigValue()
                + " / silentInstall=" + supportsSilentInstall()
                + " / reboot=" + systemConfig.isAllowReboot()
                + " / setTime=" + systemConfig.isAllowSetTime();
    }

    private SystemOps delegate() {
        return systemConfig.getMode() == DeviceMode.REAL ? realSystemOps : stubSystemOps;
    }
}
