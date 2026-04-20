package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * M90 stub 系统能力
 * <p>
 * 这层不执行真实命令，只负责把动作记进日志，方便先把底座和调试链路跑通。
 */
public final class M90StubSystemOps implements SystemOps {
    private static final String TAG = "M90StubSystemOps";
    private volatile ShellConfig.SystemConfig systemConfig = ShellConfig.SystemConfig.stub();

    /**
     * 应用系统能力配置。
     */
    public void applyConfig(ShellConfig.SystemConfig systemConfig) {
        this.systemConfig = systemConfig == null ? ShellConfig.SystemConfig.stub() : systemConfig;
    }

    @Override
    public boolean supportsSilentInstall() {
        return systemConfig.isSupportSilentInstall();
    }

    @Override
    public void reboot(String reason, String traceId) {
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "stub reboot blocked: " + reason, traceId);
    }

    @Override
    public void setSystemTime(long timeMillis, String traceId) {
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "stub setTime blocked: " + timeMillis, traceId);
    }
}
