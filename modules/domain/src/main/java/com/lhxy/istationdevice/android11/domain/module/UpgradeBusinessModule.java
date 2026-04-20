package com.lhxy.istationdevice.android11.domain.module;

import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * 升级模块
 * <p>
 * 当前先接升级通知样例和系统能力状态，后面再补安装、回滚和文件分发。
 */
public final class UpgradeBusinessModule extends AbstractTerminalBusinessModule {
    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SocketClientAdapter socketClientAdapter;
    private final SystemOps systemOps;
    private int lastUpgradeReplayCount;
    private long lastSyncTimeMillis;

    public UpgradeBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SocketClientAdapter socketClientAdapter,
            SystemOps systemOps
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.socketClientAdapter = socketClientAdapter;
        this.systemOps = systemOps;
    }

    @Override
    public String getKey() {
        return "upgrade";
    }

    @Override
    public String getTitle() {
        return "升级";
    }

    @Override
    public String describePurpose() {
        return "承接升级通知、系统安装能力和后续版本发布流程。";
    }

    @Override
    public String describeStatus() {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SocketChannel al808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getAl808SocketKey());
            return "默认升级通道=" + al808.getKey()
                    + "\n- silentInstall=" + yesNo(systemOps.supportsSilentInstall())
                    + "\n- allowReboot=" + yesNo(shellConfig.getSystemConfig().isAllowReboot())
                    + "\n- allowSetTime=" + yesNo(shellConfig.getSystemConfig().isAllowSetTime())
                    + "\n- AL808 connected=" + yesNo(socketClientAdapter.isConnected(al808.getChannelName()))
                    + "\n- lastUpgradeReplayCount=" + lastUpgradeReplayCount + " / lastSyncTime=" + (lastSyncTimeMillis <= 0 ? "-" : String.valueOf(lastSyncTimeMillis))
                    + "\n- " + describeActionMemory();
        } catch (Exception e) {
            return "当前还没拿到完整升级配置: " + emptyAsDash(e.getMessage());
        }
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        return replayUpgrade(traceId);
    }

    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        if ("sync_system_time".equals(actionKey)) {
            return syncSystemTime(traceId);
        }
        return unsupportedAction(actionKey);
    }

    private ModuleRunResult replayUpgrade(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            int count = protocolReplayUseCase.replayUpgradeDemo(socketClientAdapter, shellConfig, traceId);
            lastUpgradeReplayCount = count;
            return success(
                    "已回放升级样例 " + count + " 条",
                    "silentInstall=" + yesNo(systemOps.supportsSilentInstall())
            );
        } catch (Exception e) {
            return failure("升级样例执行失败", e);
        }
    }

    private ModuleRunResult syncSystemTime(String traceId) {
        try {
            long now = System.currentTimeMillis();
            systemOps.setSystemTime(now, traceId);
            lastSyncTimeMillis = now;
            return success("已请求同步系统时间", "timeMillis=" + now);
        } catch (Exception e) {
            return failure("系统时间同步失败", e);
        }
    }
}
