package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LegacyHomeStatusRepository;
import com.lhxy.istationdevice.android11.core.LegacyInfoMessageRepository;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DispatchProfessionRequestPacketFactory;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.dispatch.Jt808CrossInfoPacketFactory;
import com.lhxy.istationdevice.android11.domain.dispatch.Jt808OverspeedInfoPacketFactory;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsAutoReportEngine;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsFlowUseCase;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.domain.station.LegacyStationAudioUseCase;
import com.lhxy.istationdevice.android11.domain.station.LegacyStationDisplayUseCase;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808DispatchControlCommand;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808DispatchControlCommandParser;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808DispatchPlanCommand;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808DispatchPlanCommandParser;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808Frame;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808GeneralResponse;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808LegacyMessages;
import com.lhxy.istationdevice.android11.protocol.jt808.Cc808LegacyMessages;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808LineSwitchSnapshot;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808PositionSnapshot;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808ProfessionResponse;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808ProfessionResponseParser;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808ReportStationSnapshot;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808SetTerminalParametersCommand;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808SetTerminalParametersCommandParser;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808TerminalProfile;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808TextMessageCommand;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808TextMessageCommandParser;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808VehicleOperationCommand;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808VehicleOperationCommandParser;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808Variant;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808PassthroughMessage;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808PassthroughMessageEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
    private static final String FRAME_LISTENER_KEY = "dispatch-platform-state";
    private static final int MSG_PLATFORM_GENERAL_RESPONSE = 0x8001;
    private static final int MSG_REGISTER_RESPONSE = 0x8100;
    private static final int MSG_SET_TERMINAL_PARAMETERS = 0x8103;
    private static final int MSG_PLATFORM_TEXT_MESSAGE = 0x8300;
    private static final int MSG_PLATFORM_DISPATCH_PLAN = 0x8B01;
    private static final int MSG_PLATFORM_DISPATCH_CONTROL = 0x8B02;
    private static final int MSG_VEHICLE_OPERATION = 0x8B05;
    private static final int MSG_PROFESSION_RESPONSE = 0x8B09;
    private static final int MSG_PLATFORM_UPGRADE = 0x8B0A;
    private static final int CC808_VEHICLE_STATUS = 1;
    private static final long PLATFORM_TEXT_DISPLAY_MILLIS = 10_000L;
    private static final long SOCKET_HANDSHAKE_TIMEOUT_MILLIS = 15_000L;
    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SocketClientAdapter socketClientAdapter;
    private final Jt808SocketMonitor jt808SocketMonitor;
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private final GpsSerialMonitor gpsSerialMonitor;
    private final LegacyGpsFlowUseCase gpsFlowUseCase = new LegacyGpsFlowUseCase();
    private final DispatchProfessionRequestPacketFactory professionRequestPacketFactory = new DispatchProfessionRequestPacketFactory();
    private final Jt808CrossInfoPacketFactory crossInfoPacketFactory = new Jt808CrossInfoPacketFactory();
    private final Jt808OverspeedInfoPacketFactory overspeedInfoPacketFactory = new Jt808OverspeedInfoPacketFactory();
    private final Jt808LegacyMessages jt808Messages = new Jt808LegacyMessages();
    private final Cc808LegacyMessages cc808Messages = new Cc808LegacyMessages();
    private final DispatchState dispatchState = new DispatchState();
    private final LegacyStationAudioUseCase stationAudioUseCase;
    private final LegacyStationDisplayUseCase stationDisplayUseCase;
    private final SerialPortAdapter serialPortAdapter;
    private final Map<Integer, Integer> pendingProfessionRequestTypes = new ConcurrentHashMap<>();
    private PlatformLineSwitchHandler platformLineSwitchHandler;
    private ScheduledExecutorService departureReminderExecutor;
    private ScheduledExecutorService socketReportExecutor;
    private ScheduledExecutorService platformTextExecutor;
    private ScheduledFuture<?> pendingPlatformTextClear;
    private long platformTextGeneration;
    private int socketReportIntervalSeconds;
    private long socketReportCount;
    private long lastSocketReportTimeMs;
    // Registration and authentication belong to one protocol/terminal/endpoint session.
    private String registeredSocketChannel = "";
    private String registeredSocketIdentity = "";
    private String socketReportIdentity = "";
    private int pendingRegisterSerialNumber = -1;
    private boolean socketRegistrationAccepted;
    private int pendingAuthoritySerialNumber = -1;
    private boolean socketAuthenticated;
    private long socketHandshakeSentAtMs;
    private Jt808LineSwitchSnapshot pendingLineSwitch;
    // The active route must precede station progress: CC808 0B0B / AL808 DB0E.
    private String reportedLineSwitchKey = "";
    // 上次已上报的报站 key（stationNo:type）；断开/重连时复位以重报当前站。
    private String lastReportedStationKey = "";
    // V32 preserves the last arrival timestamp and reuses it in the matching
    // departure report. The platform can use the pair to derive stationState.
    private String lastStationArrivalTime = "";
    private String lastDepartureReminderKey = "-";
    private long lastDepartureMillisUntil = Long.MIN_VALUE;
    private Supplier<SignInState> signInStateSupplier;
    private Supplier<StationState> stationStateSupplier;

    public DispatchBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SocketClientAdapter socketClientAdapter,
            SerialPortAdapter serialPortAdapter,
            GpioAdapter gpioAdapter,
            Jt808SocketMonitor jt808SocketMonitor,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase,
            GpsSerialMonitor gpsSerialMonitor
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.socketClientAdapter = socketClientAdapter;
        this.serialPortAdapter = serialPortAdapter;
        this.jt808SocketMonitor = jt808SocketMonitor;
        this.dvrSerialDispatchUseCase = dvrSerialDispatchUseCase;
        this.gpsSerialMonitor = gpsSerialMonitor;
        this.stationAudioUseCase = new LegacyStationAudioUseCase(gpioAdapter);
        this.stationDisplayUseCase = new LegacyStationDisplayUseCase(serialPortAdapter);
        this.jt808SocketMonitor.registerFrameListener(FRAME_LISTENER_KEY, this::handleSocketFrame);
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

    public void attachPlatformLineSwitchHandler(PlatformLineSwitchHandler handler) {
        platformLineSwitchHandler = handler;
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

    /**
     * Immediately report the current station after a manual or automatic station action.
     * The legacy station button path sends only the station frame. Heartbeat and position
     * remain owned by the periodic GPS task.
     */
    public synchronized void reportStationProgress(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SocketChannel channel = resolveActiveDispatchChannel(shellConfig);
            if (!isSocketSessionReady(shellConfig, channel)) {
                return;
            }
            Jt808Variant variant = resolveDispatchVariant(shellConfig);
            Jt808TerminalProfile profile = buildTerminalProfile(shellConfig, variant);
            StationState station = resolveStationState();
            boolean lineSwitchReady = ensureLineSwitchReported(
                    shellConfig,
                    channel,
                    profile.getTerminalId(),
                    traceId + "-line-switch"
            );
            sendStationReportIfChanged(
                    shellConfig,
                    channel,
                    variant,
                    profile,
                    station,
                    getLatestGpsSnapshot(),
                    lineSwitchReady,
                    traceId + "-station-now"
            );
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "即时上报站点失败: " + emptyAsDash(e.getMessage()), traceId);
        }
    }

    @Override
    protected void onContextUpdated() {
        startDepartureReminderMonitorIfNeeded();
        startSocketDispatchReportIfNeeded("dispatch-config-socket-report");
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
                    + "\n- socket周期上报 -> interval=" + socketReportIntervalSeconds + "s"
                    + " / count=" + socketReportCount
                    + " / lastTime=" + (lastSocketReportTimeMs <= 0 ? "-" : String.valueOf(lastSocketReportTimeMs))
                    + " / registered=" + emptyAsDash(registeredSocketChannel)
                    + " / authenticated=" + socketAuthenticated
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
    private synchronized ModuleRunResult sendProfessionRequest(int requestType, String summary, String detail, String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SocketChannel socketChannel = resolveActiveDispatchChannel(shellConfig);
            if (!isSocketSessionReady(shellConfig, socketChannel)) {
                return failureText("调度尚未鉴权", "连接就绪后请重试");
            }
            DispatchProfessionRequestPacketFactory.BuiltPacket request = null;
            byte[] payload;
            int serialNumber;
            if (isCc808(shellConfig)) {
                StationState station = resolveStationState();
                SignInState signIn = resolveSignInState();
                GpsFixSnapshot gps = getLatestGpsSnapshot();
                boolean validFix = gps != null && gps.isValid();
                Jt808TerminalProfile profile = buildTerminalProfile(shellConfig, Jt808Variant.CC808);
                Jt808Frame frame = cc808Messages.createProfessionRequest(
                        profile.getTerminalId(),
                        station.getLineName(),
                        signIn.getCardNo(),
                        requestType,
                        compactNowTime(),
                        validFix ? gps.getLongitudeDecimal() : "0",
                        validFix ? gps.getLatitudeDecimal() : "0"
                );
                payload = cc808Messages.encode(frame);
                serialNumber = frame.getSerialNumber();
            } else {
                request = professionRequestPacketFactory.buildRequest(
                        shellConfig,
                        dispatchState,
                        resolveSignInState(),
                        resolveStationState(),
                        getLatestGpsSnapshot(),
                        requestType
                );
                payload = request.getPayload();
                serialNumber = request.getSerialNumber();
            }
            pendingProfessionRequestTypes.put(serialNumber, requestType);
            socketClientAdapter.send(socketChannel.getChannelName(), payload, traceId + "-send");
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "职业请求已发送 type=" + requestType
                            + " / channel=" + socketChannel.getKey()
                            + " / serial=" + serialNumber
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

    // Registration -> authentication -> line switch -> position and station reports.
    private synchronized void startSocketDispatchReportIfNeeded(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            // 对齐 V32：socket 调度始终保持连接，无论串口调度是否启用。
            // V32 的 SocketManage.connect() 没有"串口模式则跳过"的逻辑，两路独立运行。
            ShellConfig.SocketChannel channel = resolveActiveDispatchChannel(shellConfig);
            if (!isUsableSocketChannel(channel)) {
                AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG,
                        "调度 socket 周期上报未启动: 协议未启用/不支持，或通道不可用 protocol="
                                + shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchProtocol() + " / channel="
                                + (channel == null ? "null" : channel.getHost() + ":" + channel.getPort()), traceId);
                stopSocketDispatchReport(traceId + "-no-channel");
                return;
            }
            jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, traceId + "-monitor");
            int intervalSeconds = resolveSocketReportIntervalSeconds(shellConfig);
            boolean alreadyRunning = socketReportExecutor != null && !socketReportExecutor.isShutdown();
            String identity = socketIdentity(shellConfig, channel);
            if (alreadyRunning && socketReportIntervalSeconds == intervalSeconds
                    && identity.equals(socketReportIdentity)) {
                return;
            }
            stopSocketDispatchReport(traceId + "-restart");
            socketReportIdentity = identity;
            socketReportIntervalSeconds = intervalSeconds;
            socketReportExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "dispatch-socket-report");
                thread.setDaemon(true);
                return thread;
            });
            socketReportExecutor.scheduleWithFixedDelay(
                    () -> sendPeriodicSocketReport(traceId),
                    intervalSeconds,
                    intervalSeconds,
                    TimeUnit.SECONDS
            );
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                    "调度 socket 周期上报已启动 interval=" + intervalSeconds + "s / channel=" + channel.getKey(), traceId);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "启动调度 socket 上报失败: " + emptyAsDash(e.getMessage()), traceId);
        }
    }

    private synchronized void stopSocketDispatchReport(String traceId) {
        if (socketReportExecutor != null) {
            socketReportExecutor.shutdownNow();
        }
        if (!registeredSocketChannel.isEmpty()) {
            socketClientAdapter.disconnect(registeredSocketChannel, traceId + "-disconnect");
        }
        socketReportExecutor = null;
        socketReportIdentity = "";
        socketReportIntervalSeconds = 0;
        resetSocketRegistrationState();
        reportedLineSwitchKey = "";
        lastStationArrivalTime = "";
        lastReportedStationKey = "";
    }

    private synchronized void sendPeriodicSocketReport(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            // 对齐 V32：不因串口调度启用而跳过 socket 上报
            ShellConfig.SocketChannel channel = resolveActiveDispatchChannel(shellConfig);
            if (!isUsableSocketChannel(channel)) {
                return;
            }
            Jt808Variant variant = resolveDispatchVariant(shellConfig);
            Jt808TerminalProfile profile = buildTerminalProfile(shellConfig, variant);
            String channelName = channel.getChannelName();
            boolean cc808 = isCc808(shellConfig);

            // 1) 连接（断开则复位注册标记，重连后会重发注册）
            if (!socketClientAdapter.isConnected(channelName)) {
                resetSocketRegistrationState();
                socketClientAdapter.connect(channel.toSocketEndpointConfig(), traceId + "-connect");
                if (!socketClientAdapter.isConnected(channelName)) {
                    return;
                }
            }
            if (!registeredSocketIdentity.isEmpty()
                    && !registeredSocketIdentity.equals(socketIdentity(shellConfig, channel))) {
                socketClientAdapter.disconnect(registeredSocketChannel, traceId + "-identity-changed");
                resetSocketRegistrationState();
                return;
            }
            if (!socketAuthenticated && socketHandshakeSentAtMs > 0
                    && System.currentTimeMillis() - socketHandshakeSentAtMs >= SOCKET_HANDSHAKE_TIMEOUT_MILLIS) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                        "调度注册/鉴权超时，重新连接 channel=" + channelName, traceId);
                socketClientAdapter.disconnect(channelName, traceId + "-handshake-timeout");
                resetSocketRegistrationState();
                return;
            }
            // 2) Register once per connection; wait for both 8100 and 8001(0102).
            if (!channelName.equals(registeredSocketChannel)) {
                Jt808Frame registerFrame = cc808
                        ? cc808Messages.createRegister(profile)
                        : jt808Messages.createRegister(variant, profile);
                registeredSocketChannel = channelName;
                registeredSocketIdentity = socketIdentity(shellConfig, channel);
                pendingRegisterSerialNumber = registerFrame.getSerialNumber();
                socketRegistrationAccepted = false;
                socketHandshakeSentAtMs = System.currentTimeMillis();
                socketClientAdapter.send(channelName,
                        cc808 ? cc808Messages.encode(registerFrame) : jt808Messages.encode(registerFrame),
                        traceId + "-register");
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                        "调度 socket 已发送注册 channel=" + channel.getKey()
                                + " / variant=" + variant.getProtocolName()
                                + " / terminalId=" + profile.getTerminalId()
                                + " / serial=" + registerFrame.getSerialNumber(), traceId);
                return;
            }
            if (!socketAuthenticated) {
                return;
            }
            // 3) 心跳 0x0002
            socketClientAdapter.send(channelName,
                    cc808 ? cc808Messages.encode(cc808Messages.createHeartbeat(profile.getTerminalId()))
                            : jt808Messages.encode(jt808Messages.createHeartbeat(variant, profile.getTerminalId())),
                    traceId + "-heartbeat");
            boolean lineSwitchReady = ensureLineSwitchReported(
                    shellConfig,
                    channel,
                    profile.getTerminalId(),
                    traceId + "-line-switch"
            );
            if (!lineSwitchReady) {
                return;
            }
            // 4) 周期位置 0x0200。V32 的 GPS 线程在无速度/无有效定位时不会进入位置组包。
            GpsFixSnapshot snapshot = getLatestGpsSnapshot();
            StationState station = resolveStationState();
            boolean positionSent = snapshot != null && snapshot.isValid();
            if (positionSent) {
                socketClientAdapter.send(channelName,
                        cc808 ? cc808Messages.encode(cc808Messages.createPositionReport(
                                        profile,
                                        buildPositionSnapshot(variant, snapshot),
                                        String.valueOf(resolveLineNumber(station)),
                                        cc808RouteDirectionValue(station.getDirectionText()),
                                        positionStationNumber(station.getCurrentStationNo()),
                                        CC808_VEHICLE_STATUS,
                                        station.getSatellites()))
                                : jt808Messages.encode(jt808Messages.createPositionReport(variant, profile, buildPositionSnapshot(variant, snapshot))),
                        traceId + "-position");
            }

            socketReportCount++;
            lastSocketReportTimeMs = System.currentTimeMillis();
            dispatchState.markSocketReportSent(channelName);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                    "调度 socket 已上报心跳" + (positionSent ? "+位置" : "，无有效定位故跳过位置")
                            + " channel=" + channel.getKey()
                            + " / gpsValid=" + yesNo(snapshot != null && snapshot.isValid())
                            + " / count=" + socketReportCount, traceId);

            // 5) Report a station-state change once. CC808 uses the legacy
            // 0x0900 frame; JT808/AL808 uses 0x0B02.
            if (shouldSendPeriodicStationReport(snapshot != null && snapshot.isValid(), station.getCurrentReportType())) {
                sendStationReportIfChanged(
                        shellConfig,
                        channel,
                        variant,
                        profile,
                        station,
                        snapshot,
                        lineSwitchReady,
                        traceId
                );
            } else {
                AppLogCenter.log(
                        LogCategory.BIZ,
                        LogLevel.INFO,
                        TAG,
                        "无有效定位，周期任务跳过自动报站",
                        traceId + "-station-skip"
                );
            }
        } catch (Exception e) {
            resetSocketRegistrationState();
            reportedLineSwitchKey = "";
            lastStationArrivalTime = "";
            dispatchState.markSocketReportFailed();
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "调度 socket 周期上报失败: " + emptyAsDash(e.getMessage()), traceId);
        }
    }

    private void sendStationReportIfChanged(
            ShellConfig shellConfig,
            ShellConfig.SocketChannel channel,
            Jt808Variant variant,
            Jt808TerminalProfile profile,
            StationState station,
            GpsFixSnapshot snapshot,
            boolean lineSwitchReady,
            String traceId
    ) {
        int stationNo = station.getCurrentStationNo();
        int stationType = station.getCurrentStationType();
        int reportType = station.getCurrentReportType();
        String stationKey = station.getLineName() + ":" + station.getDirectionText() + ":" + stationReportKey(
                stationNo,
                stationType,
                reportType,
                station.getReportCount()
        );
        if (!lineSwitchReady || !shouldReportStation(variant, stationNo, stationType)
                || stationKey.equals(lastReportedStationKey)) {
            return;
        }

        boolean cc808 = isCc808(shellConfig);
        Jt808ReportStationSnapshot report = buildReportStationSnapshot(variant, resolveLineNumber(station), station, snapshot);
        String stationCode = cc808 ? resolveReportStationCode(station) : "";
        int plannedTrips = dispatchState.getTimesNo();
        String stationEventTime = compactNowTime();
        String arrivalTime;
        String outboundTime;
        if (stationType == 0) {
            lastStationArrivalTime = stationEventTime;
            arrivalTime = stationEventTime;
            outboundTime = "000000000000";
        } else {
            arrivalTime = lastStationArrivalTime.isEmpty()
                    ? stationEventTime
                    : lastStationArrivalTime;
            outboundTime = stationEventTime;
        }
        Jt808Frame frame;
        if (cc808) {
            frame = cc808Messages.createReportStation(
                            profile.getTerminalId(),
                            CC808_VEHICLE_STATUS,
                            stationCode,
                            plannedTrips,
                            report.getBusNo(),
                            cc808RouteDirectionValue(station.getDirectionText()),
                            String.valueOf(report.getLineNumber()),
                            arrivalTime,
                            outboundTime,
                            reportType,
                            report.getAngle(),
                            report.getLongitude(),
                            report.getLatitude(),
                            report.getStatus());
        } else {
            frame = jt808Messages.createReportStation(variant, profile.getTerminalId(), report);
        }
        socketClientAdapter.send(channel.getChannelName(),
                cc808 ? cc808Messages.encode(frame) : jt808Messages.encode(frame),
                traceId + (cc808 ? "-report-station-cc808" : "-report-station"));
        lastReportedStationKey = stationKey;
        if (cc808) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                    "CC808 report station code=" + emptyAsDash(stationCode)
                            + " / stationNo=" + stationNo + " / type=" + stationType
                            + " / status=" + report.getStatus()
                            + " / busNo=" + report.getBusNo()
                            + " / routeDirection=" + cc808RouteDirectionValue(station.getDirectionText())
                            + " / arrivalTime=" + arrivalTime
                            + " / outboundTime=" + outboundTime
                            + " / plannedTrips=" + plannedTrips + " / reportType=" + reportType
                            + " / lineSwitchReady=" + yesNo(lineSwitchReady),
                    traceId + "-site-code");
        }
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                "调度 socket 已上报报站 channel=" + channel.getKey()
                        + " / variant=" + variant.getProtocolName()
                        + " / message=" + String.format("0x%04X", frame.getMessageId())
                        + " / serial=" + frame.getSerialNumber() + " / bodyLength=" + frame.getBody().length
                        + " / terminalId=" + profile.getTerminalId()
                        + " / line=" + report.getLineNumber()
                        + " / status=" + report.getStatus() + " / busNo=" + report.getBusNo()
                        + " / stationNo=" + stationNo + " / type=" + stationType
                        + " / station=" + emptyAsDash(station.getCurrentStation()), traceId);
    }

    private boolean ensureLineSwitchReported(
            ShellConfig shellConfig, ShellConfig.SocketChannel channel, String terminalId, String traceId
    ) {
        if (!isSocketSessionReady(shellConfig, channel)) {
            return false;
        }
        StationState station = resolveStationState();
        int lineNumber = resolveLineNumber(station);
        int direction = jq808DirectionValue(station.getDirectionText());
        if (lineNumber <= 0) {
            return false;
        }
        String switchKey = lineSwitchKey(channel.getChannelName(), lineNumber, direction);
        if (pendingLineSwitch == null && switchKey.equals(reportedLineSwitchKey)) {
            return true;
        }
        Jt808Variant variant = resolveDispatchVariant(shellConfig);
        Jt808LineSwitchSnapshot snapshot = pendingLineSwitch;
        if (snapshot == null || snapshot.getLineNumber() != lineNumber || snapshot.getDirection() != direction) {
            // On reconnect restore the current station, not an unconditional return to station 1.
            int busNo = variant == Jt808Variant.AL808
                    ? reportStationBusNumber(variant, station.getCurrentStationNo(), station.getCurrentStationType()) : 1;
            snapshot = new Jt808LineSwitchSnapshot(1, lineNumber, direction, busNo,
                    lineNumber, direction, busNo, LocalDateTime.now(), 0);
        }
        Jt808Frame frame = variant == Jt808Variant.CC808
                ? cc808Messages.createLineSwitchInfo(terminalId, snapshot)
                : jt808Messages.createLineSwitchInfo(variant, terminalId, snapshot);
        socketClientAdapter.send(channel.getChannelName(),
                variant == Jt808Variant.CC808 ? cc808Messages.encode(frame) : jt808Messages.encode(frame), traceId + "-send");
        reportedLineSwitchKey = switchKey;
        pendingLineSwitch = null;
        lastReportedStationKey = "";
        lastStationArrivalTime = "";
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                "调度线路切换已发送 variant=" + variant.getProtocolName()
                        + " / message=" + String.format("0x%04X", frame.getMessageId())
                        + " / serial=" + frame.getSerialNumber() + " / bodyLength=" + frame.getBody().length
                        + " / line=" + lineNumber + " / direction=" + direction + " / busNo=" + snapshot.getBusNo(), traceId);
        return true;
    }

    private String lineSwitchKey(String channelName, int lineNumber, int direction) {
        return emptyAsDash(channelName) + ":" + lineNumber + ":" + direction;
    }

    private void resetSocketRegistrationState() {
        registeredSocketChannel = "";
        registeredSocketIdentity = "";
        pendingRegisterSerialNumber = -1;
        socketRegistrationAccepted = false;
        pendingAuthoritySerialNumber = -1;
        socketAuthenticated = false;
        socketHandshakeSentAtMs = 0L;
        reportedLineSwitchKey = "";
        lastReportedStationKey = "";
        lastStationArrivalTime = "";
        dispatchState.markSocketReportFailed();
    }

    private String socketIdentity(ShellConfig config, ShellConfig.SocketChannel channel) {
        ShellConfig.NetworkSettings network = config.getBasicSetupConfig().getNetworkSettings();
        return network.getDispatchProtocol() + ":" + network.getDispatchId() + ":" + channel.getChannelName()
                + ":" + channel.getHost() + ":" + channel.getPort() + ":" + channel.getMode();
    }

    private boolean isSocketSessionReady(ShellConfig config, ShellConfig.SocketChannel channel) {
        return isUsableSocketChannel(channel) && socketClientAdapter.isConnected(channel.getChannelName())
                && socketAuthenticated && registeredSocketIdentity.equals(socketIdentity(config, channel));
    }

    private synchronized void handleSocketFrame(String channelName, byte[] rawFrame, Jt808Frame frame) {
        if (frame == null) {
            return;
        }
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SocketChannel channel = resolveActiveDispatchChannel(shellConfig);
            if (channel == null || !channel.getChannelName().equals(channelName)) {
                return;
            }
            String terminalId = shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchId();
            if (!registeredSocketIdentity.equals(socketIdentity(shellConfig, channel))
                    || !terminalId.replaceFirst("^0+(?!$)", "").equals(frame.getTerminalId().replaceFirst("^0+(?!$)", ""))) {
                return;
            }
            int messageId = frame.getMessageId();
            if (messageId == Jt808TextMessageCommand.MESSAGE_ID) {
                handlePlatformTextMessage(channelName, frame);
                return;
            }
            if (messageId == Jt808DispatchPlanCommand.MESSAGE_ID) {
                handlePlatformDispatchPlan(channelName, frame);
                return;
            }
            if (messageId == Jt808DispatchControlCommand.MESSAGE_ID) {
                handlePlatformDispatchControl(channelName, frame);
                return;
            }
            if (messageId == Jt808ProfessionResponse.MESSAGE_ID) {
                handleProfessionResponse(frame);
                return;
            }
            if (messageId == MSG_REGISTER_RESPONSE) {
                boolean matchesPendingRegister = channelName.equals(registeredSocketChannel)
                        && pendingRegisterSerialNumber >= 0
                        && registerResponseSerial(frame.getBody()) == pendingRegisterSerialNumber;
                if (!matchesPendingRegister) {
                    return;
                }
                if (!isRegisterAccepted(frame.getBody())) {
                    resetSocketRegistrationState();
                    return;
                }
                socketRegistrationAccepted = true;
                pendingRegisterSerialNumber = -1;
                sendAuthorityFromRegisterResponse(shellConfig, channel, frame, "dispatch-platform-register-response");
                return;
            }
            if (messageId == MSG_PLATFORM_GENERAL_RESPONSE) {
                byte[] body = frame.getBody();
                if (body.length < 5) {
                    return;
                }
                int responseMessageId = ((body[2] & 0xFF) << 8) | (body[3] & 0xFF);
                if (responseMessageId == 0x0102) {
                    if (!socketRegistrationAccepted || pendingAuthoritySerialNumber < 0
                            || registerResponseSerial(body) != pendingAuthoritySerialNumber) {
                        return;
                    }
                    if (!isGeneralResponseAccepted(body)) {
                        resetSocketRegistrationState();
                        return;
                    }
                    socketAuthenticated = true;
                    pendingAuthoritySerialNumber = -1;
                    socketHandshakeSentAtMs = 0L;
                    dispatchState.markPlatformResponse(messageId, true);
                    AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                            "调度鉴权成功 variant=" + shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchProtocol()
                                    + " / terminalId=" + frame.getTerminalId(), "dispatch-authority-accepted");
                } else if (socketAuthenticated) {
                    dispatchState.markPlatformResponse(messageId, isGeneralResponseAccepted(body));
                }
                return;
            }
            if (messageId == MSG_SET_TERMINAL_PARAMETERS) {
                handleSetTerminalParameters(channelName, frame);
                return;
            }
            if ((messageId & 0x8000) == 0x8000) {
                dispatchState.markPlatformResponse(messageId, true);
            }
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "处理调度平台回包失败: " + emptyAsDash(e.getMessage()), "dispatch-platform-rx");
        }
    }

    private void handlePlatformTextMessage(String channelName, Jt808Frame frame) {
        String traceId = "dispatch-platform-text-" + frame.getSerialNumber();
        final Jt808TextMessageCommand command;
        try {
            command = Jt808TextMessageCommandParser.parse(frame);
        } catch (RuntimeException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "解析 8300 文本消息失败: " + emptyAsDash(e.getMessage()),
                    traceId
            );
            return;
        }

        if (command.hasSupportedAction()) {
            runPlatformTextAction("消息入库", () -> pushInfoMessage(command.getContent()), traceId);
        }
        if (command.shouldDisplay()) {
            runPlatformTextAction("首页展示", () -> publishPlatformText(command.getContent()), traceId);
        }
        if (command.shouldSpeak()) {
            playPlatformTextMessage(command, traceId + "-audio");
        }
        
        // RS485转发（乘客端或明确标记需要转发）
        if (command.shouldSendRs485() || command.isToPassenger()) {
            runPlatformTextAction("RS485转发", () -> sendTextToRs485Device(command, traceId + "-rs485"), traceId);
        }
        
        try {
            sendPlatformGeneralResponse(channelName, command, traceId);
        } catch (RuntimeException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "8300 文本消息平台应答失败: " + emptyAsDash(e.getMessage()),
                    traceId
            );
        }
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已处理 8300 文本消息 flag=0x" + Integer.toHexString(command.getFlag()).toUpperCase()
                        + " / 目标=" + command.getTargetEndpoint()
                        + " / 紧急=" + yesNo(command.isEmergency())
                        + " / 展示=" + yesNo(command.shouldDisplay())
                        + " / 播报=" + yesNo(command.shouldSpeak())
                        + " / RS485=" + yesNo(command.shouldSendRs485() || command.isToPassenger())
                        + " / 内容=" + command.getContent(),
                traceId
        );
    }

    private void handlePlatformDispatchPlan(String channelName, Jt808Frame frame) {
        String traceId = "dispatch-platform-plan-" + frame.getSerialNumber();
        final Jt808DispatchPlanCommand command;
        try {
            command = Jt808DispatchPlanCommandParser.parse(frame);
        } catch (RuntimeException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "解析 8B01 调度计划失败: " + emptyAsDash(e.getMessage()),
                    traceId
            );
            return;
        }

        dispatchState.applyPlatformDispatchPlan(
                command.getRequestSerialNumber(),
                command.getTimesNo(),
                command.getDepartureTime(),
                command.getOvertimeMinutes(),
                command.getOvertimeSpeakIntervalMinutes(),
                command.getPrepareSpeakIntervalMinutes(),
                command.getScheduleText()
        );
        playDispatchNoticeIfPossible("收到新的调度信息，请按计划时间发车", traceId + "-audio");
        try {
            sendPlatformGeneralResponse(
                    channelName,
                    command.getVariant(),
                    command.getTerminalId(),
                    command.getRequestSerialNumber(),
                    Jt808DispatchPlanCommand.MESSAGE_ID,
                    0,
                    traceId
            );
        } catch (RuntimeException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "8B01 调度计划平台应答失败: " + emptyAsDash(e.getMessage()),
                    traceId
            );
        }
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已处理 8B01 调度计划 timesNo=" + command.getTimesNo()
                        + " / departure=" + command.getDepartureTime()
                        + " / ot=" + command.getOvertimeMinutes()
                        + " / ots=" + command.getOvertimeSpeakIntervalMinutes()
                        + " / ts=" + command.getPrepareSpeakIntervalMinutes(),
                traceId
        );
    }

    private void handlePlatformDispatchControl(String channelName, Jt808Frame frame) {
        String traceId = "dispatch-platform-control-" + frame.getSerialNumber();
        final Jt808DispatchControlCommand command;
        try {
            command = Jt808DispatchControlCommandParser.parse(frame);
        } catch (RuntimeException e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "解析 8B02 调度控制失败: " + emptyAsDash(e.getMessage()), traceId);
            return;
        }

        int result;
        String action;
        switch (command.getType()) {
            case Jt808DispatchControlCommand.TYPE_SWITCH_UP:
            case Jt808DispatchControlCommand.TYPE_SWITCH_DOWN:
                boolean switched = platformLineSwitchHandler != null
                        && platformLineSwitchHandler.switchLine(
                                command.getLineNumber(),
                                command.getType() == Jt808DispatchControlCommand.TYPE_SWITCH_DOWN,
                                traceId + "-line-switch"
                        );
                result = switched ? 0 : 2;
                action = switched ? "线路切换成功" : "线路切换失败，未找到对应线路资源";
                if (switched) {
                    dispatchState.markPlatformLineSwitch(command.getLineNumber());
                    playDispatchNoticeIfPossible("线路切换成功", traceId + "-audio");
                }
                break;
            case Jt808DispatchControlCommand.TYPE_CANCEL_PLAN:
                dispatchState.cancelPlatformPlan();
                playDispatchNoticeIfPossible("取消计划成功", traceId + "-audio");
                result = 0;
                action = "取消计划成功";
                break;
            case Jt808DispatchControlCommand.TYPE_UPDATE_TRIPS:
                dispatchState.updateTripMessages(
                        command.getNextTrip(),
                        command.getThisTrip(),
                        command.getTomorrow()
                );
                result = 0;
                action = "班次信息已更新";
                break;
            default:
                result = 3;
                action = "不支持的调度控制类型 0x" + Integer.toHexString(command.getType()).toUpperCase();
                break;
        }
        try {
            sendPlatformGeneralResponse(
                    channelName,
                    command.getVariant(),
                    command.getTerminalId(),
                    command.getRequestSerialNumber(),
                    Jt808DispatchControlCommand.MESSAGE_ID,
                    result,
                    traceId
            );
        } catch (RuntimeException e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "8B02 调度控制平台应答失败: " + emptyAsDash(e.getMessage()), traceId);
        }
        AppLogCenter.log(LogCategory.BIZ, result == 0 ? LogLevel.INFO : LogLevel.WARN, TAG,
                "已处理 8B02 调度控制 type=0x" + Integer.toHexString(command.getType()).toUpperCase()
                        + " / lineNumber=" + command.getLineNumber()
                        + " / result=" + result
                        + " / action=" + action,
                traceId);
    }

    private void handleProfessionResponse(Jt808Frame frame) {
        String traceId = "dispatch-profession-response-" + frame.getSerialNumber();
        final Jt808ProfessionResponse response;
        try {
            response = Jt808ProfessionResponseParser.parse(frame);
        } catch (RuntimeException e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "解析 8B09 职业请求应答失败: " + emptyAsDash(e.getMessage()), traceId);
            return;
        }
        Integer requestType = pendingProfessionRequestTypes.remove(response.getRequestSerialNumber());
        if (requestType == null) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG,
                    "8B09 未找到对应职业请求 serial=" + response.getRequestSerialNumber()
                            + " / result=" + response.getResult(), traceId);
            return;
        }
        String message = professionRequestLabel(requestType)
                + (response.isAccepted() ? "同意" : "不同意");
        dispatchState.markProfessionResponse(requestType, response.isAccepted(), message);
        playDispatchNoticeIfPossible(message, traceId + "-audio");
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                "已处理 8B09 职业请求应答 serial=" + response.getRequestSerialNumber()
                        + " / type=" + requestType
                        + " / result=" + response.getResult()
                        + " / message=" + message,
                traceId);
    }

    private String professionRequestLabel(int requestType) {
        switch (requestType) {
            case 1:
                return "排班";
            case 2:
                return "交班";
            case 3:
                return "加油";
            case 4:
                return "加气";
            case 5:
                return "充电";
            case 6:
                return "退出营运";
            case 7:
                return "手动开始";
            case 8:
                return "手动结束";
            case 9:
                return "包车";
            case 10:
                return "维修";
            case 13:
                return "对讲";
            default:
                return "其他请求";
        }
    }

    private void runPlatformTextAction(String action, Runnable runnable, String traceId) {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "8300 文本消息" + action + "失败，继续处理后续动作: " + emptyAsDash(e.getMessage()),
                    traceId
            );
        }
    }

    private void sendPlatformGeneralResponse(
            String channelName,
            Jt808TextMessageCommand command,
            String traceId
    ) {
        sendPlatformGeneralResponse(
                channelName,
                command.getVariant(),
                command.getTerminalId(),
                command.getRequestSerialNumber(),
                Jt808TextMessageCommand.MESSAGE_ID,
                0,
                traceId
        );
    }

    private void sendPlatformGeneralResponse(
            String channelName,
            Jt808SetTerminalParametersCommand command,
            String traceId
    ) {
        sendPlatformGeneralResponse(
                channelName,
                command.getVariant(),
                command.getTerminalId(),
                command.getRequestSerialNumber(),
                Jt808SetTerminalParametersCommand.MESSAGE_ID,
                0,
                traceId
        );
    }

    private void sendPlatformGeneralResponse(
            String channelName,
            Jt808Variant requestedVariant,
            String terminalId,
            int requestSerialNumber,
            int responseMessageId,
            int result,
            String traceId
    ) {
        Jt808Variant variant = requestedVariant == null ? Jt808Variant.JT808 : requestedVariant;
        Jt808GeneralResponse response = new Jt808GeneralResponse(
                requestSerialNumber,
                responseMessageId,
                result
        );
        byte[] payload = variant == Jt808Variant.CC808
                ? cc808Messages.encode(cc808Messages.createGeneralResponse(terminalId, response))
                : jt808Messages.encode(jt808Messages.createGeneralResponse(variant, terminalId, response));
        socketClientAdapter.send(channelName, payload, traceId + "-general-response");
    }

    /** Keep the selected route pending until this connection is authenticated. */
    public synchronized void sendLineSwitchReport(
            String firstLineName, int firstDirection, int firstBusNo,
            String lineName, int direction, int busNo, int type, String traceId
    ) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            pendingLineSwitch = new Jt808LineSwitchSnapshot(type,
                    resolveLineNumber(firstLineName, firstDirection == 2 ? "下行" : "上行"), firstDirection, firstBusNo,
                    resolveLineNumber(lineName, direction == 2 ? "下行" : "上行"), direction, busNo, LocalDateTime.now(), 0);
            reportedLineSwitchKey = "";
            lastReportedStationKey = "";
            ShellConfig.SocketChannel channel = resolveActiveDispatchChannel(shellConfig);
            if (isSocketSessionReady(shellConfig, channel)) {
                ensureLineSwitchReported(shellConfig, channel,
                        shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchId(), traceId);
            }
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "线路切换上报失败: " + emptyAsDash(e.getMessage()), traceId);
        }
    }

    public interface PlatformLineSwitchHandler {
        boolean switchLine(long lineNumber, boolean downDirection, String traceId);
    }

    private void publishPlatformText(String content) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        LegacyHomeStatusRepository.setInformation(context, content);
        schedulePlatformTextClear(context);
    }

    private synchronized void schedulePlatformTextClear(Context context) {
        platformTextGeneration++;
        long generation = platformTextGeneration;
        if (pendingPlatformTextClear != null) {
            pendingPlatformTextClear.cancel(false);
        }
        if (platformTextExecutor == null || platformTextExecutor.isShutdown()) {
            platformTextExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "dispatch-platform-text-clear");
                thread.setDaemon(true);
                return thread;
            });
        }
        pendingPlatformTextClear = platformTextExecutor.schedule(() -> {
            synchronized (DispatchBusinessModule.this) {
                if (generation != platformTextGeneration) {
                    return;
                }
                LegacyHomeStatusRepository.clearInformation(context);
                pendingPlatformTextClear = null;
            }
        }, PLATFORM_TEXT_DISPLAY_MILLIS, TimeUnit.MILLISECONDS);
    }

    private boolean isRegisterAccepted(byte[] body) {
        return body != null && body.length >= 3 && (body[2] & 0xFF) == 0x00;
    }

    private int registerResponseSerial(byte[] body) {
        if (body == null || body.length < 2) {
            return -1;
        }
        return ((body[0] & 0xFF) << 8) | (body[1] & 0xFF);
    }

    private boolean isGeneralResponseAccepted(byte[] body) {
        return body != null && body.length >= 5 && (body[4] & 0xFF) == 0x00;
    }

    private void sendAuthorityFromRegisterResponse(
            ShellConfig shellConfig, ShellConfig.SocketChannel channel, Jt808Frame registerResponse, String traceId
    ) {
        try {
            Jt808Variant variant = resolveDispatchVariant(shellConfig);
            String terminalId = shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchId();
            byte[] body = registerResponse.getBody();
            byte[] authorityCode = java.util.Arrays.copyOfRange(body, 3, body.length);
            Jt808Frame frame = variant == Jt808Variant.CC808
                    ? cc808Messages.createAuthority(terminalId, authorityCode)
                    : jt808Messages.createAuthority(variant, terminalId, authorityCode);
            pendingAuthoritySerialNumber = frame.getSerialNumber();
            socketHandshakeSentAtMs = System.currentTimeMillis();
            socketClientAdapter.send(channel.getChannelName(),
                    variant == Jt808Variant.CC808 ? cc808Messages.encode(frame) : jt808Messages.encode(frame), traceId + "-authority");
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                    "调度平台注册通过，等待鉴权应答 channel=" + channel.getKey()
                            + " / serial=" + frame.getSerialNumber() + " / authorityBytes=" + authorityCode.length, traceId);
        } catch (Exception e) {
            resetSocketRegistrationState();
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "调度平台鉴权发送失败: " + emptyAsDash(e.getMessage()), traceId);
        }
    }

    private ShellConfig.SocketChannel resolveActiveDispatchChannel(ShellConfig shellConfig) {
        String protocol = shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchProtocol();
        // ALINK is a separate legacy protocol, not an alias for AL808/0x0B02.
        if (!"CC808".equals(protocol) && !"AL808".equals(protocol) && !"JT808".equals(protocol)) {
            return null;
        }
        String socketKey = shellConfig.getDispatchSocketKey(protocol);
        try {
            return shellConfig.requireSocketChannel(socketKey);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                    "调度 socket 通道解析失败 protocol=" + protocol + " / key=" + socketKey
                            + " / err=" + e.getMessage(), "dispatch-channel");
            return null;
        }
    }

    private boolean isUsableSocketChannel(ShellConfig.SocketChannel channel) {
        return channel != null
                && channel.getHost() != null
                && !channel.getHost().trim().isEmpty()
                && channel.getPort() > 0;
    }

    private Jt808Variant resolveDispatchVariant(ShellConfig shellConfig) {
        return Jt808Variant.valueOf(shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchProtocol());
    }

    private boolean isCc808(ShellConfig shellConfig) {
        return "CC808".equalsIgnoreCase(shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchProtocol());
    }

    static int cc808RouteDirectionValue(String directionText) {
        return directionText != null && directionText.contains("下") ? 2 : 1;
    }

    static int jq808DirectionValue(String directionText) {
        return directionText != null && directionText.contains("下") ? 2 : 1;
    }

    private Jt808TerminalProfile buildTerminalProfile(ShellConfig shellConfig, Jt808Variant variant) {
        String dispatchId = emptyAsDash(shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchId());
        String terminalId = "-".equals(dispatchId) ? "0" : dispatchId;
        // AL808 registration retains the V7 compatibility model and fixed plate fields.
        return new Jt808TerminalProfile(
                terminalId,
                variant == Jt808Variant.AL808 ? "M90V702" : "K80V0101",
                variant == Jt808Variant.AL808 ? "粤B00235" : "粤B00000",
                "414C31313031",
                0xABE0,
                0xAD0C,
                0x00
        );
    }

    static Jt808PositionSnapshot buildPositionSnapshot(Jt808Variant variant, GpsFixSnapshot snapshot) {
        boolean valid = snapshot != null && snapshot.isValid();
        String latitude = valid ? emptyToZero(snapshot.getLatitudeDecimal()) : "0";
        String longitude = valid ? emptyToZero(snapshot.getLongitudeDecimal()) : "0";
        boolean al808 = variant == Jt808Variant.AL808;
        int speed = valid ? (al808 ? legacyAl808Speed(snapshot.getSpeedKnots()) : knotsToKmh(snapshot.getSpeedKnots())) : 0;
        int direction = al808 ? legacyAl808Angle(valid ? snapshot.getCourse() : null, false)
                : valid ? parseIntSafe(snapshot.getCourse()) : 0;
        long statusFlag = valid ? (al808 ? 0x00080013L : 0x02L) : 0L;
        return new Jt808PositionSnapshot(0L, statusFlag, latitude, longitude, speed, direction, 0, LocalDateTime.now());
    }

    static Jt808ReportStationSnapshot buildReportStationSnapshot(
            Jt808Variant variant, int lineNumber, StationState station, GpsFixSnapshot snapshot
    ) {
        boolean valid = snapshot != null && snapshot.isValid();
        String latitude = valid ? emptyToZero(snapshot.getLatitudeDecimal()) : "0";
        String longitude = valid ? emptyToZero(snapshot.getLongitudeDecimal()) : "0";
        boolean al808 = variant == Jt808Variant.AL808;
        int speed = valid ? (al808 ? legacyAl808Speed(snapshot.getSpeedKnots()) : knotsToKmhTenths(snapshot.getSpeedKnots())) : 0;
        int angle = al808 ? legacyAl808Angle(valid ? snapshot.getCourse() : null, true)
                : valid ? parseIntSafe(snapshot.getCourse()) : 0;
        boolean previewingNext = station.getCurrentStationType() == 1;
        int status = al808 ? (previewingNext ? 2 : 1) : (previewingNext ? 1 : 0);
        int busNo = reportStationBusNumber(variant, station.getCurrentStationNo(), station.getCurrentStationType());
        return new Jt808ReportStationSnapshot(lineNumber, status, jq808DirectionValue(station.getDirectionText()),
                busNo, latitude, longitude, speed, angle, LocalDateTime.now());
    }

    // V7 suppresses movement below one knot; both 0200 and 0B02 use 0.1 km/h.
    private static int legacyAl808Speed(String knots) {
        double speed = parseDoubleSafe(knots);
        return speed < 1 ? 0 : (int) Math.round(speed * 1.852d * 10d);
    }

    private static int legacyAl808Angle(String course, boolean stationReport) {
        try {
            double value = Double.parseDouble(course);
            if (!Double.isFinite(value)) {
                return 361;
            }
            int angle = (int) value;
            return stationReport && angle <= 0 ? 361 : angle;
        } catch (Exception e) {
            return 361;
        }
    }

    private String resolveReportStationCode(StationState station) {
        Context context = getContext();
        if (context == null || station == null) {
            return "";
        }
        LegacyGpsRouteResource route = gpsFlowUseCase.resolveActiveRoute(
                context,
                requireShellConfig(),
                station.getLineName(),
                station.getDirectionText()
        );
        if (route == null || route.getStations().isEmpty()) {
            return "";
        }
        int stationIndex = station.getCurrentStationNo();
        if (station.getCurrentStationType() == 1) {
            stationIndex--;
        }
        stationIndex = Math.max(0, Math.min(stationIndex, route.getStations().size() - 1));
        LegacyGpsRouteResource.StationPoint point = route.getStations().get(stationIndex);
        if (point == null) {
            return String.valueOf(stationIndex);
        }
        String siteCode = point.getSiteCode();
        // Older 16-column resources have no UID column.  Preserve the legacy
        // behavior and leave the field empty so the platform matches by
        // direction and coordinates; only populated UID values are sent.
        return siteCode == null || siteCode.trim().isEmpty()
                ? ""
                : siteCode;
    }

    static int positionStationNumber(int stationNo) {
        return Math.max(0, stationNo);
    }

    static int reportStationBusNumber(Jt808Variant variant, int stationNo, int stationType) {
        int normalizedStationNo = Math.max(0, stationNo);
        // A next-station preview reports departure from the preceding station.
        if (variant == Jt808Variant.AL808 && stationType == 1) {
            return normalizedStationNo;
        }
        return normalizedStationNo + 1;
    }

    static boolean shouldReportStation(Jt808Variant variant, int stationNo, int stationType) {
        return stationNo >= 0 && !(variant == Jt808Variant.AL808 && stationNo == 0 && stationType == 0);
    }

    static boolean shouldSendPeriodicStationReport(boolean gpsValid, int reportType) {
        // A manual action made while connecting still needs its report after authentication.
        return gpsValid || reportType == 1;
    }

    static String stationReportKey(int stationNo, int stationType, int reportType, int reportCount) {
        return stationNo + ":" + stationType + ":" + reportType + ":" + reportCount;
    }

    private int resolveLineNumber(StationState station) {
        if (station == null) {
            return 0;
        }
        return resolveLineNumber(station.getLineName(), station.getDirectionText());
    }

    private int resolveLineNumber(String lineName, String directionText) {
        Context context = getContext();
        if (context != null) {
            LegacyGpsRouteResource route = gpsFlowUseCase.load(context, lineName, directionText);
            if (route != null && route.getLineNumber() > 0) {
                return route.getLineNumber();
            }
            int resourceLineNumber = gpsFlowUseCase.resolveLineNumber(context, lineName);
            if (resourceLineNumber > 0) {
                AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG,
                        "线路站点资源加载失败，仍使用 lineInfo.csv 线路号 line="
                                + emptyAsDash(lineName) + " / direction=" + emptyAsDash(directionText)
                                + " / lineNumber=" + resourceLineNumber,
                        "dispatch-line-number");
                return resourceLineNumber;
            }
        }
        int fallback = parseLineNumber(lineName);
        AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                "未找到 lineInfo.csv 线路号，使用线路名数字回退 line=" + emptyAsDash(lineName)
                        + " / direction=" + emptyAsDash(directionText)
                        + " / lineNumber=" + fallback,
                "dispatch-line-number-fallback");
        return fallback;
    }

    private int parseLineNumber(String lineName) {
        if (lineName == null) {
            return 0;
        }
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < lineName.length(); i++) {
            char c = lineName.charAt(i);
            if (c >= '0' && c <= '9') {
                digits.append(c);
            }
        }
        if (digits.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(digits.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private GpsFixSnapshot getLatestGpsSnapshot() {
        return gpsSerialMonitor == null ? null : gpsSerialMonitor.getLatestSnapshot();
    }

    private int resolveSocketReportIntervalSeconds(ShellConfig shellConfig) {
        int intervalSeconds = shellConfig.getBasicSetupConfig().getNetworkSettings().getInfoInterval();
        if (intervalSeconds <= 0) {
            return 10;
        }
        return Math.min(intervalSeconds, 3600);
    }

    private static int knotsToKmh(String knots) {
        return (int) Math.round(parseDoubleSafe(knots) * 1.852d);
    }

    /** 0.1km/h 单位(km/h×10)——0x0b02 报站上报专用，对齐 V32(i==3 分支)。 */
    private static int knotsToKmhTenths(String knots) {
        return (int) Math.round(parseDoubleSafe(knots) * 1.852d * 10d);
    }

    private static int parseIntSafe(String value) {
        return (int) Math.round(parseDoubleSafe(value));
    }

    private static double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty() || "-".equals(value.trim())) {
            return 0d;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            return 0d;
        }
    }

    private static String emptyToZero(String value) {
        return value == null || value.trim().isEmpty() || "-".equals(value.trim()) ? "0" : value.trim();
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
    public synchronized void sendCrossInfoReport(
            StationState stationState,
            LegacyGpsRouteResource.ReminderPoint reminderPoint,
            GpsFixSnapshot snapshot,
            int reminderType,
            String traceId
    ) {
        if (stationState == null || reminderPoint == null) {
            return;
        }
        boolean validFix = snapshot != null && snapshot.isValid();
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SocketChannel socketChannel = resolveActiveDispatchChannel(shellConfig);
            if (!isSocketSessionReady(shellConfig, socketChannel)) {
                return;
            }
            byte[] payload;
            Jt808Variant variant = resolveDispatchVariant(shellConfig);
            if (variant == Jt808Variant.CC808) {
                payload = cc808Messages.encode(cc808Messages.createCrossInfo(
                        shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchId(),
                        stationState.getLineName(),
                        reminderPoint.getCrossCode(),
                        stationState.getActiveCrossArrivalTime(),
                        reminderType == LegacyGpsAutoReportEngine.REMINDER_TYPE_LEAVE ? compactNowTime() : "000000000000",
                        validFix ? parseAngle(snapshot.getCourse()) : 0,
                        validFix ? snapshot.getLongitudeDecimal() : "0",
                        validFix ? snapshot.getLatitudeDecimal() : "0"
                ));
            } else {
                payload = crossInfoPacketFactory.build(
                        shellConfig,
                        stationState,
                        reminderPoint,
                        stationState.getActiveCrossArrivalTime(),
                        reminderType == LegacyGpsAutoReportEngine.REMINDER_TYPE_LEAVE ? compactNowTime() : "000000000000",
                        validFix ? parseAngle(snapshot.getCourse()) : 0,
                        validFix ? snapshot.getLongitudeDecimal() : "0",
                        validFix ? snapshot.getLatitudeDecimal() : "0"
                );
            }
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

    public synchronized void sendCrossingOverspeedReport(
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
        boolean validFix = snapshot != null && snapshot.isValid();
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SocketChannel socketChannel = resolveActiveDispatchChannel(shellConfig);
            if (!isSocketSessionReady(shellConfig, socketChannel)) {
                return;
            }
            byte[] payload;
            Jt808Variant variant = resolveDispatchVariant(shellConfig);
            if (variant == Jt808Variant.CC808) {
                payload = cc808Messages.encode(cc808Messages.createOverspeedInfo(
                        shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchId(),
                        22,
                        65320,
                        148,
                        stationState.getLineName(),
                        stationState.getActiveCrossCode(),
                        compactNowTime(),
                        continueSeconds,
                        highSpeedKmh,
                        validFix ? snapshot.getLongitudeDecimal() : "0",
                        validFix ? snapshot.getLatitudeDecimal() : "0",
                        parseIntSafe(stationState.getActiveCrossSpeedLimit()),
                        0,
                        Math.max(0, stationState.getCurrentStationNo()),
                        averageSpeedHundredKmh,
                        resolveSignInState().getCardNo(),
                        parseIntSafe(stationState.getActiveCrossType()),
                        Math.max(0, stationState.getActiveReminderNo()),
                        parseIntSafe(stationState.getActiveCrossSpeedLimit())
                ));
            } else {
                payload = overspeedInfoPacketFactory.buildCrossing(
                        shellConfig,
                        stationState,
                        resolveSignInState(),
                        route,
                        highSpeedKmh,
                        averageSpeedHundredKmh,
                        continueSeconds,
                        validFix ? snapshot.getLongitudeDecimal() : "0",
                        validFix ? snapshot.getLatitudeDecimal() : "0",
                        compactNowTime()
                );
            }
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

    /**
     * 播放平台文本消息语音（根据目标端选择音频路由）
     */
    private void playPlatformTextMessage(Jt808TextMessageCommand command, String traceId) {
        try {
            Context context = getContext();
            if (context == null) {
                return;
            }
            ShellConfig shellConfig = requireShellConfig();
            
            // 根据目标端选择音频路由
            if (command.isToDriver()) {
                // 司机端：小喇叭播放
                stationAudioUseCase.playDispatchNoticeToDriver(context, shellConfig, command.getContent());
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "司机端文本语音: 小喇叭播放", traceId);
            } else if (command.isToPassenger()) {
                // 乘客端：内音播放
                stationAudioUseCase.playDispatchNoticeToPassenger(context, shellConfig, command.getContent());
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "乘客端文本语音: 内音播放", traceId);
            } else {
                // 未指定目标端：默认调度播报（小喇叭，对标现场版）
                stationAudioUseCase.playDispatchNotice(context, shellConfig, command.getContent());
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "文本语音: 默认调度播报", traceId);
            }
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "文本语音播放失败: " + e.getMessage(), traceId);
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
    
    /**
     * 通过RS485发送文本消息到第三方设备
     */
    private void sendTextToRs485Device(Jt808TextMessageCommand command, String traceId) {
        try {
            Context context = getContext();
            if (context == null) {
                return;
            }
            ShellConfig shellConfig = requireShellConfig();
            
            // 将文本消息转换为单行列表发送
            List<String> messages = new ArrayList<>();
            messages.add(command.getContent());
            
            boolean success = stationDisplayUseCase.sendLedAdvertisement(shellConfig, messages, traceId);
            if (success) {
                AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, 
                    "RS485转发成功: \"" + command.getContent() + "\"", traceId);
            } else {
                AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, 
                    "RS485转发失败: 不支持的协议或设备未连接", traceId);
            }
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, 
                "RS485转发异常: " + e.getMessage(), traceId);
        }
    }
    
    /**
     * 处理0x8103设置终端参数
     */
    private void handleSetTerminalParameters(String channelName, Jt808Frame frame) {
        String traceId = "dispatch-set-params-" + frame.getSerialNumber();
        final Jt808SetTerminalParametersCommand command;
        try {
            command = Jt808SetTerminalParametersCommandParser.parse(frame);
        } catch (RuntimeException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "解析 8103 参数设置失败: " + emptyAsDash(e.getMessage()),
                    traceId
            );
            return;
        }

        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "收到参数设置: 参数数量=" + command.getParameterCount(),
                traceId
        );

        // 应用参数
        applyTerminalParameters(command, traceId);

        // 回复平台通用应答
        try {
            sendPlatformGeneralResponse(channelName, command, traceId);
        } catch (RuntimeException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "8103 参数设置平台应答失败: " + emptyAsDash(e.getMessage()),
                    traceId
            );
        }

        dispatchState.markPlatformResponse(frame.getMessageId(), true);
    }

    /**
     * 应用终端参数到配置
     */
    private void applyTerminalParameters(Jt808SetTerminalParametersCommand command, String traceId) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        boolean needRestart = false;

        // 心跳间隔（0x0001）
        Integer heartbeatInterval = command.getParameterAsDword(Jt808SetTerminalParametersCommand.PARAM_HEARTBEAT_INTERVAL);
        if (heartbeatInterval != null && heartbeatInterval > 0 && heartbeatInterval <= 3600) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, 
                "应用参数: 心跳间隔=" + heartbeatInterval + "秒", traceId);
            // TODO: 更新ShellConfig并重启心跳定时器
            needRestart = true;
        }

        // GPS定位汇报间隔（0x0029）
        Integer gpsInterval = command.getParameterAsDword(Jt808SetTerminalParametersCommand.PARAM_GPS_URGENT_REPORT_INTERVAL);
        if (gpsInterval != null && gpsInterval > 0 && gpsInterval <= 3600) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, 
                "应用参数: GPS间隔=" + gpsInterval + "秒", traceId);
            // TODO: 更新ShellConfig并调整GPS上报频率
        }

        // 超速持续时间（0x0055）
        Integer overspeedDuration = command.getParameterAsDword(Jt808SetTerminalParametersCommand.PARAM_OVERSPEED_DURATION);
        if (overspeedDuration != null && overspeedDuration > 0) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, 
                "应用参数: 超速持续时间=" + overspeedDuration + "秒", traceId);
            // TODO: 更新超速监控参数
        }

        // 超速预警差值（0x0056，单位：1/10 km/h）
        Integer overspeedThreshold = command.getParameterAsDword(Jt808SetTerminalParametersCommand.PARAM_OVERSPEED_ALARM_SPEED_DIFF);
        if (overspeedThreshold != null && overspeedThreshold > 0) {
            int thresholdKmh = overspeedThreshold / 10;
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, 
                "应用参数: 超速阈值=" + thresholdKmh + "km/h", traceId);
            // TODO: 更新超速监控阈值
        }

        if (needRestart) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, 
                "参数更新需要重启Socket连接以生效", traceId);
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
        } catch (Exception e) {
            // 不阻断调度主链，但记录：否则“发车倒计时提醒不响”查不到是时间解析还是音频异常。
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "发车提醒评估异常: " + e, "dispatch-reminder");
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
