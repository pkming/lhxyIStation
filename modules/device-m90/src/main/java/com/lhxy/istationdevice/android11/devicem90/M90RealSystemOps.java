package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * M90 真系统能力适配器
 * <p>
 * 当前先走命令模板。权限和命令是否真的可用，仍由终端系统和厂商镜像决定。
 */
public final class M90RealSystemOps implements SystemOps {
    private static final String TAG = "M90RealSystemOps";

    private volatile ShellConfig.SystemConfig systemConfig = ShellConfig.SystemConfig.stub();

    /**
     * 更新系统能力配置。
     */
    public void updateConfig(ShellConfig.SystemConfig systemConfig) {
        this.systemConfig = systemConfig == null ? ShellConfig.SystemConfig.stub() : systemConfig;
    }

    @Override
    public boolean supportsSilentInstall() {
        return systemConfig.isSupportSilentInstall() && !systemConfig.getSilentInstallCommand().trim().isEmpty();
    }

    @Override
    public void reboot(String reason, String traceId) {
        if (!systemConfig.isAllowReboot()) {
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "reboot blocked by config: " + reason, traceId);
            return;
        }
        if (systemConfig.getRebootCommand().trim().isEmpty()) {
            throw new IllegalStateException("rebootCommand 为空");
        }
        String command = M90CommandSupport.fillValueCommand(systemConfig.getRebootCommand(), reason);
        try {
            M90CommandSupport.exec(command);
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "reboot requested: " + command, traceId);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "reboot failed: " + e.getMessage(), traceId);
            throw new IllegalStateException("请求重启失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void setSystemTime(long timeMillis, String traceId) {
        if (!systemConfig.isAllowSetTime()) {
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "setTime blocked by config: " + timeMillis, traceId);
            return;
        }
        if (systemConfig.getSetTimeCommand().trim().isEmpty()) {
            throw new IllegalStateException("setTimeCommand 为空");
        }
        String command = M90CommandSupport.fillTimeCommand(systemConfig.getSetTimeCommand(), timeMillis);
        try {
            M90CommandSupport.exec(command);
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "setTime requested: " + command, traceId);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "setTime failed: " + e.getMessage(), traceId);
            throw new IllegalStateException("请求校时失败: " + e.getMessage(), e);
        }
    }
}
