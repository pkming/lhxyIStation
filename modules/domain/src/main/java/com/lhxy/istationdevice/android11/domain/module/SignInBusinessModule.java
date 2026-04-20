package com.lhxy.istationdevice.android11.domain.module;

import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;

/**
 * 签到模块
 * <p>
 * 先把 RFID 读取和司机考勤样例绑在一起，后面再补真正的签到签退状态机。
 */
public final class SignInBusinessModule extends AbstractTerminalBusinessModule {
    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SocketClientAdapter socketClientAdapter;
    private final RfidAdapter rfidAdapter;
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private int lastAttendanceReplayCount;
    private final SignInState signInState = new SignInState();

    public SignInBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SocketClientAdapter socketClientAdapter,
            RfidAdapter rfidAdapter,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.socketClientAdapter = socketClientAdapter;
        this.rfidAdapter = rfidAdapter;
        this.dvrSerialDispatchUseCase = dvrSerialDispatchUseCase;
    }

    @Override
    public String getKey() {
        return "signin";
    }

    @Override
    public String getTitle() {
        return "签到";
    }

    @Override
    public String describePurpose() {
        return "承接 RFID 刷卡、司机考勤和后续签到签退业务。";
    }

    public SignInState getSignInState() {
        return signInState;
    }

    @Override
    public String describeStatus() {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (dvrSerialDispatchUseCase.canUse(shellConfig)) {
                return "RFID=" + (rfidAdapter.isAvailable() ? "可读" : "未就绪")
                        + "\n- 调度考勤主链 -> RS232-1/DVR"
                        + "\n- " + signInState.describe()
                        + "\n- replayCount=" + lastAttendanceReplayCount
                        + "\n- " + describeActionMemory();
            }
            ShellConfig.SocketChannel jt808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
            return "RFID=" + (rfidAdapter.isAvailable() ? "可读" : "未就绪")
                    + "\n- 调度上报码通道 -> " + jt808.getKey()
                    + " / connected=" + yesNo(socketClientAdapter.isConnected(jt808.getChannelName()))
                    + "\n- " + signInState.describe()
                    + "\n- replayCount=" + lastAttendanceReplayCount
                    + "\n- " + describeActionMemory();
        } catch (Exception e) {
            return "当前还没拿到完整签到配置: " + emptyAsDash(e.getMessage());
        }
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        return handleReadAndReplay(traceId, true);
    }

    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        if ("read_card".equals(actionKey)) {
            return handleReadAndReplay(traceId, false);
        }
        if ("manual_sign_out".equals(actionKey)) {
            signInState.manualSignOut();
            return sendAttendanceAfterStateChange(traceId, "已手动签退", "当前司机状态已切到签退");
        }
        return unsupportedAction(actionKey);
    }

    private ModuleRunResult handleReadAndReplay(String traceId, boolean replayAttendance) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            String cardNo = null;
            if (rfidAdapter.isAvailable()) {
                cardNo = rfidAdapter.readCard(traceId + "-rfid");
            }
            if (cardNo == null || cardNo.trim().isEmpty()) {
                if (!replayAttendance) {
                    return failure("读取卡号失败", new IllegalStateException("未读取到有效卡号"));
                }
                cardNo = "DRIVER0001";
            }
            signInState.applyCard(cardNo);
            if (dvrSerialDispatchUseCase.canUse(shellConfig)) {
                lastAttendanceReplayCount = 0;
                dvrSerialDispatchUseCase.sendDriverAttendance(
                        shellConfig,
                        shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName(),
                        signInState,
                        traceId + "-serial-attendance"
                );
                return success(
                        replayAttendance ? "已执行签到主链并发送 DVR 考勤帧" : "已读取卡号并发送 DVR 考勤帧",
                        "RFID 卡号=" + signInState.getCardNo() + " / " + signInState.getAttendanceMode() + " / RS232-1"
                );
            }
            int count = replayAttendance ? protocolReplayUseCase.replaySignInDemo(socketClientAdapter, shellConfig, traceId) : 0;
            if (replayAttendance) {
                lastAttendanceReplayCount = count;
            }
            return success(
                    replayAttendance ? "已回放签到样例 " + count + " 条" : "已读取一次卡号",
                    "RFID 卡号=" + signInState.getCardNo() + " / " + signInState.getAttendanceMode()
            );
        } catch (Exception e) {
            return failure("签到样例执行失败", e);
        }
    }

    private ModuleRunResult sendAttendanceAfterStateChange(String traceId, String successSummary, String successDetail) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (dvrSerialDispatchUseCase.canUse(shellConfig)) {
                dvrSerialDispatchUseCase.sendDriverAttendance(
                        shellConfig,
                        shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName(),
                        signInState,
                        traceId + "-serial-attendance"
                );
                return success(successSummary, successDetail + "，并已发送 DVR 考勤帧");
            }
            return success(successSummary, successDetail);
        } catch (Exception e) {
            return failure("司机考勤发送失败", e);
        }
    }
}
