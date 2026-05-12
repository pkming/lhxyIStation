package com.lhxy.istationdevice.android11.domain.module;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LegacyHomeStatusRepository;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.domain.upgrade.LegacyUpgradeDownloadAgent;
import com.lhxy.istationdevice.android11.domain.upgrade.LocalUpgradeApkFinder;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808Frame;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808UpgradeCommand;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808UpgradeCommandParser;

import java.io.File;

/**
 * 升级模块
 * <p>
 * 当前先接升级通知样例和系统能力状态，后面再补安装、回滚和文件分发。
 * <p>
 * 查找关键字：升级样例、8B0A 下载命令、本地 APK 安装、系统时间同步。
 */
public final class UpgradeBusinessModule extends AbstractTerminalBusinessModule {
    private static final String FRAME_LISTENER_KEY = "upgrade-download-agent";

    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SocketClientAdapter socketClientAdapter;
    private final SystemOps systemOps;
    private final LegacyUpgradeDownloadAgent upgradeDownloadAgent;
    private int lastUpgradeReplayCount;
    private long lastSyncTimeMillis;
    private String lastInstallApkPath = "-";

    public UpgradeBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SocketClientAdapter socketClientAdapter,
            SystemOps systemOps,
            Jt808SocketMonitor jt808SocketMonitor
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.socketClientAdapter = socketClientAdapter;
        this.systemOps = systemOps;
        this.upgradeDownloadAgent = new LegacyUpgradeDownloadAgent(socketClientAdapter, systemOps);
        jt808SocketMonitor.registerFrameListener(FRAME_LISTENER_KEY, this::handleSocketFrame);
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
        return "承接 8B0A 下载命令、后台 FTP/HTTP 下载、APK 安装和 SourceFile 导入。";
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
                    + "\n- localApk=" + resolveLocalApkName()
                    + "\n- lastUpgradeReplayCount=" + lastUpgradeReplayCount + " / lastSyncTime=" + (lastSyncTimeMillis <= 0 ? "-" : String.valueOf(lastSyncTimeMillis))
                    + " / lastInstallApk=" + lastInstallApkPath
                    + "\n- " + describeActionMemory();
        } catch (Exception e) {
            return "当前还没拿到完整升级配置: " + emptyAsDash(e.getMessage());
        }
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        return replayUpgrade(traceId);
    }

    /**
     * 升级模块动作总入口。
     */
    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        if ("sync_system_time".equals(actionKey)) {
            return syncSystemTime(traceId);
        }
        if ("install_local_apk".equals(actionKey)) {
            return installLocalApk(traceId);
        }
        return unsupportedAction(actionKey);
    }

    /**
     * 回放升级协议样例。
     */
    private ModuleRunResult replayUpgrade(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            int count = protocolReplayUseCase.replayUpgradeDemo(socketClientAdapter, shellConfig, traceId);
            lastUpgradeReplayCount = count;
            return success(
                    "已回放升级样例 " + count + " 条",
                    "silentInstall=" + yesNo(systemOps.supportsSilentInstall()) + " / 真实下载链改为等待 8B0A 下载命令"
            );
        } catch (Exception e) {
            publishUpgradeFailure("升级样例执行失败");
            return failure("升级样例执行失败", e);
        }
    }

    /**
     * 请求同步系统时间。
     */
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

    /**
     * 安装本地找到的最佳升级 APK。
     */
    private ModuleRunResult installLocalApk(String traceId) {
        try {
            File apkFile = LocalUpgradeApkFinder.findBest(getContext());
            if (apkFile == null) {
                publishUpgradeFailure("未找到本地升级包");
                return failureText("未找到本地升级包", "扫描目录中没有可安装的 APK 文件");
            }
            LegacyHomeStatusRepository.setInfoOperation(getContext(), LegacyHomeStatusRepository.InfoOperation.APK_UPGRADING);
            systemOps.installPackage(apkFile.getAbsolutePath(), traceId);
            lastInstallApkPath = apkFile.getAbsolutePath();
            return success("已请求安装本地升级包", apkFile.getAbsolutePath());
        } catch (Exception e) {
            publishUpgradeFailure("APK upgrade failed");
            return failure("本地升级包安装失败", e);
        }
    }

    private String resolveLocalApkName() {
        File apkFile = LocalUpgradeApkFinder.findBest(getContext());
        return apkFile == null ? "-" : apkFile.getName();
    }

    @Override
    protected void onContextUpdated() {
        upgradeDownloadAgent.updateContext(getContext());
    }

    /**
     * 把升级失败状态抛到首页提示区。
     */
    private void publishUpgradeFailure(String message) {
        if (getContext() == null) {
            return;
        }
        LegacyHomeStatusRepository.setInfoTips(getContext(), message);
    }

    /**
     * 监听在线 8B0A 下载命令，并交给下载代理处理。
     */
    private void handleSocketFrame(String channelName, byte[] rawFrame, Jt808Frame frame) {
        if (frame == null || frame.getMessageId() != 0x8B0A || getContext() == null) {
            return;
        }
        String traceId = "upgrade-live-" + frame.getSerialNumber();
        try {
            Jt808UpgradeCommand command = Jt808UpgradeCommandParser.parse(frame);
            upgradeDownloadAgent.handleCommand(channelName, command, traceId);
        } catch (RuntimeException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "UpgradeBusinessModule",
                    "解析 8B0A 下载命令失败: " + emptyAsDash(e.getMessage()),
                    traceId
            );
        }
    }
}
