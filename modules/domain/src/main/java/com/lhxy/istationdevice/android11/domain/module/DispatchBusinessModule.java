package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LegacyInfoMessageRepository;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DispatchProfessionRequestPacketFactory;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.dispatch.Jt808CrossInfoPacketFactory;
import com.lhxy.istationdevice.android11.domain.dispatch.Jt808OverspeedInfoPacketFactory;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsAutoReportEngine;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.domain.station.LegacyStationAudioUseCase;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 调度模块
 * <p>
 * 先把 JT808 / AL808 的默认通道、收包监听和调度样例挂起来。
 * <p>
 * 查找关键字：调度主链、公告确认、发车、socket/串口归属、职业请求。
 */
public final class DispatchBusinessModule extends AbstractTerminalBusinessModule {
    private static final String TAG = "DispatchModule";
    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SocketClientAdapter socketClientAdapter;
    private final Jt808SocketMonitor jt808SocketMonitor;
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private final DispatchProfessionRequestPacketFactory professionRequestPacketFactory = new DispatchProfessionRequestPacketFactory();
    private final Jt808CrossInfoPacketFactory crossInfoPacketFactory = new Jt808CrossInfoPacketFactory();
    private final Jt808OverspeedInfoPacketFactory overspeedInfoPacketFactory = new Jt808OverspeedInfoPacketFactory();
    private final DispatchState dispatchState = new DispatchState();
    private final LegacyStationAudioUseCase stationAudioUseCase;
    private ScheduledExecutorService departureReminderExecutor;
    private String lastDepartureReminderKey = "-";
    private long lastDepartureMillisUntil = Long.MIN_VALUE;
    private Supplier<SignInState> signInStateSupplier;
    private Supplier<StationState> stationStateSupplier;

    public DispatchBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SocketClientAdapter socketClientAdapter,
            GpioAdapter gpioAdapter,
            Jt808SocketMonitor jt808SocketMonitor,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.socketClientAdapter = socketClientAdapter;
        this.jt808SocketMonitor = jt808SocketMonitor;
        this.dvrSerialDispatchUseCase = dvrSerialDispatchUseCase;
        this.stationAudioUseCase = new LegacyStationAudioUseCase(gpioAdapter);
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

    /**
     * 注入签到和报站状态提供者，方便调度发包时带上联动信息。
     */
    public void attachStateProviders(
            Supplier<SignInState> signInStateSupplier,
            Supplier<StationState> stationStateSupplier
    ) {
        this.signInStateSupplier = signInStateSupplier;
        this.stationStateSupplier = stationStateSupplier;
    }

    @Override
    protected void onContextUpdated() {
        startDepartureReminderMonitorIfNeeded();
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

    /**
     * 调度模块的动作总入口。
     * <p>
     * 页面上的确认公告、确认调度、发车、各类请求最终都会先落到这里。
     */
    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "调度动作入口 action=" + emptyAsDash(actionKey), traceId);
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
            return sendProfessionRequest(5, "已提交充电请求", "当前调度状态已记录充电申请", traceId);
        }
        if ("vehicle_failure".equals(actionKey)) {
            dispatchState.reportVehicleFailure();
            return sendProfessionRequest(10, "已上报车辆故障", "当前调度状态已记录故障事件", traceId);
        }
        if ("start_bus".equals(actionKey)) {
            dispatchState.markStartBus();
            sendSerialStartBusIfNeeded(traceId);
            return success("已执行发车", "车辆状态已切到运营中");
        }
        if ("request_schedule".equals(actionKey)) {
            dispatchState.markOperation(true, "已提交排班请求");
            return sendProfessionRequest(1, "已提交排班请求", "请求排班报文已发送", traceId);
        }
        if ("request_handover".equals(actionKey)) {
            dispatchState.markOperation(true, "已提交交班请求");
            return sendProfessionRequest(2, "已提交交班请求", "请求交班报文已发送", traceId);
        }
        if ("request_oil".equals(actionKey)) {
            dispatchState.markOperation(true, "已提交加油请求");
            return sendProfessionRequest(3, "请求加油已发送", "请求加油报文已发送", traceId);
        }
        if ("request_aerate".equals(actionKey)) {
            dispatchState.markOperation(true, "已提交加气请求");
            return sendProfessionRequest(4, "请求加气已发送", "请求加气报文已发送", traceId);
        }
        if ("manual_start".equals(actionKey)) {
            dispatchState.markStartBus();
            return sendProfessionRequest(7, "已提交手动开始", "手动开始报文已发送", traceId);
        }
        if ("manual_end".equals(actionKey)) {
            dispatchState.markOperation(false, "已提交手动结束");
            return sendProfessionRequest(8, "已提交手动结束", "手动结束报文已发送", traceId);
        }
        if ("request_charter".equals(actionKey)) {
            dispatchState.markOperation(true, "已提交包车请求");
            return sendProfessionRequest(9, "已提交包车请求", "包车请求报文已发送", traceId);
        }
        if ("request_repair".equals(actionKey)) {
            dispatchState.reportVehicleFailure();
            return sendProfessionRequest(10, "已提交维修请求", "维修请求报文已发送", traceId);
        }
        if ("other_requests".equals(actionKey)) {
            dispatchState.markOperation(true, "已提交其他请求");
            return sendProfessionRequest(11, "已提交其他请求", "其他请求报文已发送", traceId);
        }
        if ("intercom".equals(actionKey)) {
            dispatchState.markOperation(true, "已提交对讲请求");
            return sendProfessionRequest(13, "已提交对讲请求", "对讲请求报文已发送", traceId);
        }
        return unsupportedAction(actionKey);
    }

    /**
     * 发送职业请求类报文。
     */
    private ModuleRunResult sendProfessionRequest(int requestType, String summary, String detail, String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SocketChannel socketChannel = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
            byte[] payload = professionRequestPacketFactory.build(
                    shellConfig,
                    dispatchState,
                    resolveSignInState(),
                    resolveStationState(),
                    requestType
            );
            if (!socketClientAdapter.isConnected(socketChannel.getChannelName())) {
                socketClientAdapter.connect(socketChannel.toSocketEndpointConfig(), traceId + "-connect");
            }
            socketClientAdapter.send(socketChannel.getChannelName(), payload, traceId + "-send");
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "职业请求已发送 type=" + requestType
                            + " / channel=" + socketChannel.getKey()
                            + " / state=" + dispatchState.describe(),
                    traceId
            );
            return success(summary, detail + " / 通道=" + socketChannel.getKey());
        } catch (Exception e) {
            return failure(summary + "失败", e);
        }
    }

    private SignInState resolveSignInState() {
        return signInStateSupplier == null || signInStateSupplier.get() == null
                ? new SignInState()
                : signInStateSupplier.get();
    }

    private StationState resolveStationState() {
        return stationStateSupplier == null || stationStateSupplier.get() == null
                ? new StationState()
                : stationStateSupplier.get();
    }

    /**
     * 回放调度主链样例。
     * <p>
     * 当调度归属切到串口时，这里会主动跳过 socket 回放并给出收口提示。
     */
    private ModuleRunResult replayDispatch(String traceId, boolean fullReplay) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (shellConfig.getBasicSetupConfig().getProtocolLinkageSettings().isSerialDispatchEnabled()) {
                String protocol = shellConfig.getBasicSetupConfig().getSerialSettings().getRs2321Protocol();
                dispatchState.markReplay("RS232-1/" + protocol, fullReplay, 0);
                dispatchState.confirmDispatch();
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
            dispatchState.confirmDispatch();
            pushInfoMessage(dispatchState.getDispatchMessage());
            pushInfoMessage(dispatchState.getPendingNoticeMessage());
            playDispatchNoticeIfPossible(dispatchState.getPendingNoticeMessage(), traceId + "-replay-notice");
            return success("已回放调度样例 " + count + " 条", "默认监听已同步到 JT808 / AL808");
        } catch (Exception e) {
            return failure("调度样例执行失败", e);
        }
    }

    /**
     * 处理收到的下发公告，并补发语音提醒。
     */
    public void onDispatchNoticeReceived(String message, String traceId) {
        pushInfoMessage(message);
        playDispatchNoticeIfPossible(message, traceId + "-notice-audio");
    }

    /**
     * 处理收到的调度指令，并按旧主链自动确认。
     */
    public void onDispatchRequestReceived(String traceId) {
        dispatchState.confirmDispatch();
        pushInfoMessage(dispatchState.getDispatchMessage());
        sendSerialDispatchReplyIfNeeded(traceId + "-auto-confirm");
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已按旧主链自动确认调度下发",
                traceId
        );
    }

    /**
     * 发送 JT808 路口信息上报。
     */
    public void sendCrossInfoReport(
            StationState stationState,
            LegacyGpsRouteResource.ReminderPoint reminderPoint,
            GpsFixSnapshot snapshot,
            int reminderType,
            String traceId
    ) {
        if (stationState == null || reminderPoint == null) {
            return;
        }
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SocketChannel socketChannel = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
            if (!socketClientAdapter.isConnected(socketChannel.getChannelName())) {
                socketClientAdapter.connect(socketChannel.toSocketEndpointConfig(), traceId + "-connect");
            }
            byte[] payload = crossInfoPacketFactory.build(
                    shellConfig,
                    stationState,
                    reminderPoint,
                    stationState.getActiveCrossArrivalTime(),
                    reminderType == LegacyGpsAutoReportEngine.REMINDER_TYPE_LEAVE ? compactNowTime() : "000000000000",
                    snapshot == null ? 0 : parseAngle(snapshot.getCourse()),
                    snapshot == null ? stationState.getLongitude() : snapshot.getLongitudeDecimal(),
                    snapshot == null ? stationState.getLatitude() : snapshot.getLatitudeDecimal()
            );
            socketClientAdapter.send(socketChannel.getChannelName(), payload, traceId + "-send");
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "已发送 JT808 路口信息上报 reminder=" + reminderPoint.getReminderName()
                            + " type=" + (reminderType == LegacyGpsAutoReportEngine.REMINDER_TYPE_LEAVE ? "leave" : "enter"),
                    traceId
            );
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "发送 JT808 路口信息上报失败: " + e.getMessage(),
                    traceId
            );
        }
    }

    public void sendCrossingOverspeedReport(
            StationState stationState,
            LegacyGpsRouteResource route,
            int highSpeedKmh,
            int averageSpeedHundredKmh,
            long continueSeconds,
            GpsFixSnapshot snapshot,
            String traceId
    ) {
        if (stationState == null || !stationState.isCrossingReminderActive()) {
            return;
        }
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SocketChannel socketChannel = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
            if (!socketClientAdapter.isConnected(socketChannel.getChannelName())) {
                socketClientAdapter.connect(socketChannel.toSocketEndpointConfig(), traceId + "-connect");
            }
            byte[] payload = overspeedInfoPacketFactory.buildCrossing(
                    shellConfig,
                    stationState,
                    resolveSignInState(),
                    route,
                    highSpeedKmh,
                    averageSpeedHundredKmh,
                    continueSeconds,
                    snapshot == null ? stationState.getLongitude() : snapshot.getLongitudeDecimal(),
                    snapshot == null ? stationState.getLatitude() : snapshot.getLatitudeDecimal(),
                    compactNowTime()
            );
            socketClientAdapter.send(socketChannel.getChannelName(), payload, traceId + "-send");
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "已发送 JT808 路口超速上报 crossCode=" + stationState.getActiveCrossCode()
                            + " highSpeed=" + highSpeedKmh
                            + " avgSpeed=" + averageSpeedHundredKmh,
                    traceId
            );
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "发送 JT808 路口超速上报失败: " + e.getMessage(),
                    traceId
            );
        }
    }

    public boolean autoStartBusIfNeeded(String traceId) {
        if (!dispatchState.isDispatchedConfirmed() || dispatchState.isStartedBus()) {
            return false;
        }
        dispatchState.markStartBus();
        sendSerialStartBusIfNeeded(traceId + "-auto-start");
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已按旧主链在首站预报时自动发车",
                traceId
        );
        return true;
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

    private void pushInfoMessage(String message) {
        String safeMessage = emptyAsDash(message);
        if ("-".equals(safeMessage)) {
            return;
        }
        LegacyInfoMessageRepository.append(getContext(), safeMessage);
    }

    private int parseAngle(String course) {
        String value = emptyAsDash(course);
        if ("-".equals(value)) {
            return 0;
        }
        try {
            float parsed = Float.parseFloat(value.trim());
            return parsed > 0 ? (int) parsed : 0;
        } catch (Exception ignore) {
            return 0;
        }
    }

    private String compactNowTime() {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyMMddHHmmss", java.util.Locale.getDefault());
        return format.format(Calendar.getInstance().getTime());
    }

    private void sendSerialDispatchReplyIfNeeded(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig)) {
                return;
            }
            dvrSerialDispatchUseCase.sendDispatchReply(shellConfig, dispatchState, 1, 1, traceId + "-serial-dispatch-reply");
        } catch (Exception ignore) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "串口调度确认回复发送失败: " + ignore.getMessage(), traceId);
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
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "串口发车上报发送失败: " + ignore.getMessage(), traceId);
        }
    }

    private void playDispatchNoticeIfPossible(String message, String traceId) {
        try {
            Context context = getContext();
            if (context == null) {
                return;
            }
            stationAudioUseCase.playDispatchNotice(context, requireShellConfig(), message);
        } catch (Exception ignore) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "调度公告语音播放失败: " + ignore.getMessage(), traceId);
        }
    }

    private synchronized void startDepartureReminderMonitorIfNeeded() {
        if (getContext() == null) {
            return;
        }
        if (departureReminderExecutor != null && !departureReminderExecutor.isShutdown()) {
            return;
        }
        departureReminderExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "dispatch-departure-reminder");
            thread.setDaemon(true);
            return thread;
        });
        departureReminderExecutor.scheduleWithFixedDelay(
                this::evaluateDepartureReminder,
                5,
                5,
                TimeUnit.SECONDS
        );
    }

    private void evaluateDepartureReminder() {
        try {
            Context context = getContext();
            if (context == null) {
                return;
            }
            String reminderKey = resolveDepartureReminderKey();
            if (reminderKey == null) {
                resetDepartureReminderState();
                return;
            }
            long millisUntil = resolveDepartureMillisUntil(dispatchState.getPlannedDepartureTime());
            if (millisUntil == Long.MIN_VALUE) {
                resetDepartureReminderState();
                return;
            }
            if (!reminderKey.equals(lastDepartureReminderKey)) {
                lastDepartureReminderKey = reminderKey;
                lastDepartureMillisUntil = millisUntil;
                return;
            }
            if (lastDepartureMillisUntil > 180_000L && millisUntil <= 180_000L) {
                playDepartureReminder(context, "请于3分钟后发车", "dispatch-reminder-3m");
            }
            if (lastDepartureMillisUntil > 60_000L && millisUntil <= 60_000L) {
                playDepartureReminder(context, "请于1分钟后发车", "dispatch-reminder-1m");
            }
            if (lastDepartureMillisUntil > 0L && millisUntil <= 0L) {
                playDepartureReminder(context, "发车时间到了,请确认", "dispatch-reminder-due");
            }
            lastDepartureMillisUntil = millisUntil;
        } catch (Exception ignore) {
            // 发车提醒不阻断调度主链。
        }
    }

    private void playDepartureReminder(Context context, String message, String traceId) {
        stationAudioUseCase.playDispatchNotice(context, requireShellConfig(), message);
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "已触发发车提醒: " + message, traceId);
    }

    private String resolveDepartureReminderKey() {
        if (!dispatchState.isDispatchedConfirmed() || dispatchState.isStartedBus()) {
            return null;
        }
        String departureTime = emptyAsDash(dispatchState.getPlannedDepartureTime());
        if ("-".equals(departureTime)) {
            return null;
        }
        return departureTime + "|" + dispatchState.getLastUpdateTimeMillis();
    }

    private long resolveDepartureMillisUntil(String departureTime) {
        String safeValue = emptyAsDash(departureTime);
        if ("-".equals(safeValue) || safeValue.length() != 5 || safeValue.charAt(2) != ':') {
            return Long.MIN_VALUE;
        }
        try {
            int hour = Integer.parseInt(safeValue.substring(0, 2));
            int minute = Integer.parseInt(safeValue.substring(3, 5));
            Calendar now = Calendar.getInstance();
            Calendar target = (Calendar) now.clone();
            target.set(Calendar.HOUR_OF_DAY, hour);
            target.set(Calendar.MINUTE, minute);
            target.set(Calendar.SECOND, 0);
            target.set(Calendar.MILLISECOND, 0);
            return target.getTimeInMillis() - now.getTimeInMillis();
        } catch (Exception ignore) {
            return Long.MIN_VALUE;
        }
    }

    private void resetDepartureReminderState() {
        lastDepartureReminderKey = "-";
        lastDepartureMillisUntil = Long.MIN_VALUE;
    }
}
