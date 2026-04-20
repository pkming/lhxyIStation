package com.lhxy.istationdevice.android11.domain.module;

import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;

/**
 * 调度模块
 * <p>
 * 先把 JT808 / AL808 的默认通道、收包监听和调度样例挂起来。
 */
public final class DispatchBusinessModule extends AbstractTerminalBusinessModule {
    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SocketClientAdapter socketClientAdapter;
    private final Jt808SocketMonitor jt808SocketMonitor;
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private final DispatchState dispatchState = new DispatchState();

    public DispatchBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SocketClientAdapter socketClientAdapter,
            Jt808SocketMonitor jt808SocketMonitor,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.socketClientAdapter = socketClientAdapter;
        this.jt808SocketMonitor = jt808SocketMonitor;
        this.dvrSerialDispatchUseCase = dvrSerialDispatchUseCase;
    }

    @Override
    public String getKey() {
        return "dispatch";
    }

    @Override
    public String getTitle() {
        return "调度";
    }

    @Override
    public String describePurpose() {
        return "承接 JT808 / AL808 调度链路、回包监听和状态落地入口。";
    }

    public DispatchState getDispatchState() {
        return dispatchState;
    }

    @Override
    public String describeStatus() {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.ProtocolLinkageSettings linkageSettings = shellConfig.getBasicSetupConfig().getProtocolLinkageSettings();
            if (linkageSettings.isSerialDispatchEnabled()) {
                return "当前调度归属=RS232-1/串口"
                        + "\n- 串口协议=" + shellConfig.getBasicSetupConfig().getSerialSettings().getRs2321Protocol()
                        + "\n- 串口主链已开始接入 DVR 调度发帧；socket 回放/监听降级为保底"
                        + "\n- " + dispatchState.describe()
                        + "\n- " + describeActionMemory();
            }
            ShellConfig.SocketChannel jt808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
            ShellConfig.SocketChannel al808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getAl808SocketKey());
            return "当前调度归属=网络/socket"
                    + "\n- 默认通道=" + jt808.getKey() + "/" + al808.getKey()
                    + "\n- JT808 -> connected=" + yesNo(socketClientAdapter.isConnected(jt808.getChannelName()))
                    + ", monitor=" + yesNo(jt808SocketMonitor.isAttached(jt808.getChannelName()))
                    + "\n- AL808 -> connected=" + yesNo(socketClientAdapter.isConnected(al808.getChannelName()))
                    + ", monitor=" + yesNo(jt808SocketMonitor.isAttached(al808.getChannelName()))
                    + "\n- " + dispatchState.describe()
                    + "\n- " + describeActionMemory();
        } catch (Exception e) {
            return "当前还没拿到完整调度配置: " + emptyAsDash(e.getMessage());
        }
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        return replayDispatch(traceId, false);
    }

    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        if ("replay_all".equals(actionKey)) {
            return replayDispatch(traceId, true);
        }
        if ("join_operation".equals(actionKey)) {
            dispatchState.markOperation(true, "已加入运营，等待发车指令");
            return success("已切到运营中", "当前调度状态已更新为参加运营");
        }
        if ("leave_operation".equals(actionKey)) {
            dispatchState.markOperation(false, "已退出运营，等待重新签到或调度恢复");
            return success("已切到停运", "当前调度状态已更新为退出运营");
        }
        if ("confirm_dispatch".equals(actionKey)) {
            dispatchState.confirmDispatch();
            sendSerialDispatchReplyIfNeeded(traceId);
            return success("已确认调度消息", "当前调度状态已切到待发车");
        }
        if ("ack_notice".equals(actionKey)) {
            return acknowledgeNotice(traceId);
        }
        if ("request_charge".equals(actionKey)) {
            dispatchState.requestCharge();
            return success("已提交充电请求", "当前调度状态已记录充电申请");
        }
        if ("vehicle_failure".equals(actionKey)) {
            dispatchState.reportVehicleFailure();
            return success("已上报车辆故障", "当前调度状态已记录故障事件");
        }
        if ("start_bus".equals(actionKey)) {
            dispatchState.markStartBus();
            sendSerialStartBusIfNeeded(traceId);
            return success("已执行发车", "车辆状态已切到运营中");
        }
        return unsupportedAction(actionKey);
    }

    private ModuleRunResult replayDispatch(String traceId, boolean fullReplay) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (shellConfig.getBasicSetupConfig().getProtocolLinkageSettings().isSerialDispatchEnabled()) {
                String protocol = shellConfig.getBasicSetupConfig().getSerialSettings().getRs2321Protocol();
                dispatchState.markReplay("RS232-1/" + protocol, fullReplay, 0);
                return success(
                        "当前调度归属为串口，已跳过 socket 样例回放",
                        "RS232-1 协议=" + protocol + "，确认调度/公告回复/发车/站点-GPS 上报已开始走串口主链"
                );
            }
            jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, traceId + "-monitor");
            int count = fullReplay
                    ? protocolReplayUseCase.replayJt808Demo(socketClientAdapter, shellConfig, traceId)
                    : protocolReplayUseCase.replayDispatchDemo(socketClientAdapter, shellConfig, traceId);
            dispatchState.markReplay(fullReplay ? "JT808/AL808 全量" : "JT808/AL808 主链", fullReplay, count);
            return success("已回放调度样例 " + count + " 条", "默认监听已同步到 JT808 / AL808");
        } catch (Exception e) {
            return failure("调度样例执行失败", e);
        }
    }

    private ModuleRunResult acknowledgeNotice(String traceId) {
        if (dispatchState.isPendingNoticeAcked()) {
            return success("当前没有待确认公告", "无需发送下发回复");
        }
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (dvrSerialDispatchUseCase.canUse(shellConfig)) {
                dvrSerialDispatchUseCase.sendLowerReply(shellConfig, dispatchState, 1, traceId + "-serial-lower-reply");
            }
            dispatchState.acknowledgeNotice();
            return success("已确认下发公告", "公告应答已完成" + (dvrSerialDispatchUseCase.canUse(shellConfig) ? "，并发送 DVR 下发回复帧" : ""));
        } catch (Exception e) {
            return failure("确认下发公告失败", e);
        }
    }

    private void sendSerialDispatchReplyIfNeeded(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig)) {
                return;
            }
            dvrSerialDispatchUseCase.sendDispatchReply(shellConfig, dispatchState, 1, 1, traceId + "-serial-dispatch-reply");
        } catch (Exception ignore) {
            // 串口调度补发失败不阻断当前页面动作，现场通过日志继续看。
        }
    }

    private void sendSerialStartBusIfNeeded(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig)) {
                return;
            }
            dvrSerialDispatchUseCase.sendStartBusReport(shellConfig, dispatchState, shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName(), 1, traceId + "-serial-start-bus");
        } catch (Exception ignore) {
            // 同上，不阻断当前业务态推进。
        }
    }
}
