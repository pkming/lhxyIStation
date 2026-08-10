package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import java.util.Calendar;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsAutoReportEngine;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsFlowUseCase;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.domain.passenger.JhyPassengerCounterMonitor;
import com.lhxy.istationdevice.android11.domain.station.LegacyStationAudioUseCase;
import com.lhxy.istationdevice.android11.domain.station.LegacyStationDisplayUseCase;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 报站模块。
 * <p>
 * 这一层把 GPS 监听、自动报站、屏显、语音和 DVR 串口联动收在一起。
 * <p>
 * 查找关键字：报站主链、服务音、自动报站、GPS 绑定、方向切换。
 */
public final class StationBusinessModule extends AbstractTerminalBusinessModule {
    private static final String TAG = "StationBusinessModule";

    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SerialPortAdapter serialPortAdapter;
    private final GpsSerialMonitor gpsSerialMonitor;
    private final DispatchBusinessModule dispatchBusinessModule;
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private final JhyPassengerCounterMonitor passengerCounterMonitor;
    private final StationState stationState = new StationState();
    private final LegacyGpsFlowUseCase gpsFlowUseCase = new LegacyGpsFlowUseCase();
    private final LegacyStationDisplayUseCase stationDisplayUseCase;
    private final LegacyStationAudioUseCase stationAudioUseCase;
    // V24：网络首次上线时触发一次 LCD 校时（GPS 尚未有效时）。只注册一次。
    private ConnectivityManager.NetworkCallback clockNetworkCallback;
    private volatile boolean clockNetworkCallbackRegistered;

    private ScheduledExecutorService periodicGpsReportExecutor;
    private ScheduledExecutorService autoGpsReportExecutor;
    private int periodicGpsReportIntervalSeconds;
    private long periodicGpsReportCount;
    private long lastPeriodicGpsReportTimeMs;
    private long autoGpsReportCount;
    private long lastAutoGpsReportTimeMs;
    // 自动报站每秒轮询：跳过原因只在“变化时”打一条，避免刷屏(参考串口 RX 节流)。
    private String lastAutoReportSkipReason;
    private long speedingStartTimeMs;
    private int speedingPeakKmh;
    private long lastSpeedWarningTimeMs;
    private long speedWarningGpsWarmupAnchorMs = -1L;
    private long lastNowTimeKey = -1L;

    public StationBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SerialPortAdapter serialPortAdapter,
            GpioAdapter gpioAdapter,
            GpsSerialMonitor gpsSerialMonitor,
            JhyPassengerCounterMonitor passengerCounterMonitor,
            DispatchBusinessModule dispatchBusinessModule,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.serialPortAdapter = serialPortAdapter;
        this.gpsSerialMonitor = gpsSerialMonitor;
        this.passengerCounterMonitor = passengerCounterMonitor;
        this.dispatchBusinessModule = dispatchBusinessModule;
        this.dvrSerialDispatchUseCase = dvrSerialDispatchUseCase;
        this.stationDisplayUseCase = new LegacyStationDisplayUseCase(serialPortAdapter);
        this.stationAudioUseCase = new LegacyStationAudioUseCase(gpioAdapter);
    }

    @Override
    public String getKey() {
        return "station";
    }

    @Override
    public String getTitle() {
        return "报站";
    }

    @Override
    public String describePurpose() {
        return "负责屏显报站、GPS 监听、自动报站状态机和 DVR 串口联动。";
    }

    public StationState getStationState() {
        return stationState;
    }

    public boolean applyPlatformLineSwitch(long lineNumber, boolean downDirection, String traceId) {
        Context context = getContext();
        if (context == null) {
            return false;
        }
        String directionText = downDirection ? "下行" : "上行";
        LegacyGpsRouteResource route = gpsFlowUseCase.loadByLineNumber(
                context,
                String.valueOf(lineNumber),
                directionText
        );
        if (route == null) {
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.WARN,
                    TAG,
                    "平台线路切换失败: 未找到 Line serial=" + lineNumber + " / direction=" + directionText,
                    traceId
            );
            return false;
        }
        stationState.applyLineProfile(route.getLineName(), route.getDirectionText(), route.stationNames());
        stationState.setLineAttribute(route.getAttributeLabel());
        gpsFlowUseCase.reset(route);
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "平台线路切换成功: " + route.getLineName() + " / " + route.getDirectionText(),
                traceId
        );
        return true;
    }

    /**
     * 线路资源变更后清缓存并重新对齐当前线路画像。
     */
    public void reloadRouteResources() {
        gpsFlowUseCase.clearCache();
        syncRouteProfileIfNeeded();
    }

    @Override
    protected void onContextUpdated() {
        syncRouteProfileIfNeeded();
        registerClockNetworkTriggerIfNeeded();
        if (isGpsMonitorAttached()) {
            startPeriodicGpsReportIfNeeded("station-config-gps-report");
            startAutoGpsReportIfNeeded("station-config-auto-report");
        } else {
            stopPeriodicGpsReport("station-config-gps-report-stop");
            stopAutoGpsReport("station-config-auto-report-stop");
        }
    }

    /**
     * V24：注册网络上线触发（只注册一次）。网络首次可用且 GPS 尚未有效时，往 LCD 发一次校时，
     * 对齐现场版 WiFi/4G 首次上线(UNCON→OK) && !GPS有效 的行为。校时本身仍受"系统时间可信"门槛约束。
     */
    private synchronized void registerClockNetworkTriggerIfNeeded() {
        if (clockNetworkCallbackRegistered) {
            return;
        }
        Context context = getContext();
        if (context == null) {
            return;
        }
        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        if (connectivityManager == null) {
            return;
        }
        clockNetworkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                onNetworkAvailableForClock();
            }
        };
        try {
            connectivityManager.registerDefaultNetworkCallback(clockNetworkCallback);
            clockNetworkCallbackRegistered = true;
            safeBusinessLog(LogCategory.BIZ, LogLevel.INFO, TAG, "校时: 已注册网络上线触发", "station-clock-net-register");
        } catch (Exception e) {
            clockNetworkCallback = null;
            safeBusinessLog(LogCategory.ERROR, LogLevel.WARN, TAG, "校时: 注册网络上线触发失败: " + emptyAsDash(e.getMessage()), "station-clock-net-register");
        }
    }

    private void onNetworkAvailableForClock() {
        String traceId = "station-clock-net-online";
        try {
            GpsFixSnapshot snapshot = getLatestGpsSnapshot();
            if (snapshot != null && snapshot.isValid()) {
                // GPS 已有效，走 GPS 时间那条链，网络触发跳过（对齐现场版 !bValidData 条件）。
                return;
            }
            ShellConfig shellConfig = requireShellConfig();
            LegacyGpsRouteResource route = resolveRouteOrNull();
            stationDisplayUseCase.sendClockSync(shellConfig, route, stationState, "网络上线", traceId);
        } catch (Exception e) {
            safeBusinessLog(LogCategory.ERROR, LogLevel.WARN, TAG, "校时: 网络上线发送失败: " + emptyAsDash(e.getMessage()), traceId);
        }
    }

    private LegacyGpsRouteResource resolveRouteOrNull() {
        try {
            return resolveRequiredRoute();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String describeStatus() {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel displayChannel =
                    shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getDisplaySerialKey());
            ShellConfig.SerialChannel gpsChannel =
                    shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            return "默认屏显口=" + displayChannel.getKey()
                    + "，GPS 口=" + gpsChannel.getKey()
                    + "\n- 屏显串口 -> " + (serialPortAdapter.isOpen(displayChannel.getPortName()) ? "已打开" : "未打开")
                    + "\n- GPS 串口 -> " + (serialPortAdapter.isOpen(gpsChannel.getPortName()) ? "已打开" : "未打开")
                    + "\n- GPS 监听 -> " + (isGpsMonitorAttached() ? "已绑定" : "未绑定")
                    + "\n- GPS periodic report -> " + describePeriodicGpsReport()
                    + "\n- GPS auto report -> " + describeAutoGpsReport()
                    + "\n- " + stationState.describe()
                    + "\n- " + describeActionMemory();
        } catch (Exception e) {
            return "当前还没有拿到完整报站配置: " + emptyAsDash(e.getMessage());
        }
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        return advanceStation(traceId);
    }

    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "报站动作入口 action=" + emptyAsDash(actionKey), traceId);
        if ("bind_gps".equals(actionKey)) {
            return replayAndBindGps(traceId, false);
        }
        if ("advance_station".equals(actionKey)) {
            return advanceStation(traceId);
        }
        if ("backward_station".equals(actionKey)) {
            return retreatStation(traceId);
        }
        if ("switch_direction".equals(actionKey)) {
            return switchDirection(traceId);
        }
        if ("repeat_station".equals(actionKey)) {
            return repeatStation(traceId);
        }
        if ("stop_station".equals(actionKey)) {
            return stopStation();
        }
        if (actionKey != null && actionKey.startsWith("service_tone_")) {
            return playServiceTone(actionKey, traceId);
        }
        return unsupportedAction(actionKey);
    }

    /**
     * 触发 0-9 服务音。
     * <p>
     * 本地音频链和 RS485 服务音帧都从这里一起收口。
     */
    private ModuleRunResult playServiceTone(String actionKey, String traceId) {
        try {
            int serviceNo = Integer.parseInt(actionKey.substring("service_tone_".length()));
            if (serviceNo < 0 || serviceNo > 9) {
                throw new IllegalArgumentException("服务音编号超出范围");
            }
            Context context = requireContextOrThrow();
            ShellConfig shellConfig = requireShellConfig();
            boolean audioTriggered = stationAudioUseCase.playServiceTone(context, shellConfig, serviceNo);
            boolean protocolSent = stationDisplayUseCase.sendServiceTone(shellConfig, serviceNo, traceId);
                AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "服务音完成 no=" + serviceNo + " / audio=" + yesNo(audioTriggered) + " / rs485=" + yesNo(protocolSent),
                    traceId
                );
            return success(
                    "已触发服务音 " + serviceNo,
                    "本地播报=" + yesNo(audioTriggered) + " / 485 服务音=" + yesNo(protocolSent)
            );
        } catch (Exception e) {
            return failure("服务音触发失败", e);
        }
    }

    /**
     * 重新绑定 GPS 主链，并按需要把线路/屏显一并同步起来。
     */
    private ModuleRunResult replayAndBindGps(String traceId, boolean replayDisplay) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel gpsChannel =
                    shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            ensureGpsReady(gpsChannel, traceId);
            LegacyGpsRouteResource route = resolveRequiredRoute();
            stationDisplayUseCase.syncRoute(shellConfig, route, stationState, traceId + "-display-route");
            stationDisplayUseCase.sendCurrentStation(shellConfig, route, stationState, traceId + "-display-current");
            if (replayDisplay) {
                protocolReplayUseCase.replayStationDemo(serialPortAdapter, shellConfig, traceId + "-demo");
            }
            sendSerialDispatchFramesIfNeeded(traceId + "-bind-gps");
            startPeriodicGpsReportIfNeeded(traceId + "-periodic-gps");
            startAutoGpsReportIfNeeded(traceId + "-auto-gps");
                AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "GPS 主链已绑定 port=" + gpsChannel.getPortName()
                        + " / line=" + stationState.getLineName()
                        + " / direction=" + stationState.getDirectionText(),
                    traceId
                );
            return success(
                    replayDisplay ? "已准备报站主链" : "已重新绑定 GPS 监听",
                    "GPS 已绑定到 " + gpsChannel.getKey() + "，屏显链路已同步"
            );
        } catch (Exception e) {
            return failure("报站样例执行失败", e);
        }
    }

    /**
     * 推进一站。
     * <p>
     * 这里会同时推进站点状态、更新屏显、播放语音，并把必要信息联动给调度和 DVR 串口链。
     */
    private ModuleRunResult advanceStation(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            LegacyGpsRouteResource route = resolveRequiredRoute();
            syncManualGpsSnapshotIfAvailable(traceId);
            // 对齐 V32 现场版：手动报站(forwardNewspaper)不做任何 GPS 判断，直接按两段式推进。
            // 端口原先加的“GPS 重合拦截”既非 V32 行为，坐标换算又不可靠(实测 distance 达 254 万米、overlapped 恒否)，已移除。
            if (stationState.getReportCount() == 0) {
                stationDisplayUseCase.syncRoute(shellConfig, route, stationState, traceId + "-display-route");
            }
            stationState.advanceStation();
            stationDisplayUseCase.sendCurrentStation(shellConfig, route, stationState, traceId + "-display-current");
            requestPassengerCounterForArrival(traceId + "-jhy-current-count");
            playCurrentStationAudio(requireContextOrThrow(), shellConfig, route);
            sendSerialDispatchFramesIfNeeded(traceId + "-advance-station");
            maybeAutoStartBus(traceId + "-advance-station");
            startPeriodicGpsReportIfNeeded(traceId + "-periodic-gps");
            startAutoGpsReportIfNeeded(traceId + "-auto-gps");
            if (!stationState.isPreviewingNext() && stationState.getCurrentStationNo() >= stationState.getStationCount() - 1) {
                handleDirectionSwitch(requireContextOrThrow(), route, traceId);
                LegacyGpsRouteResource switchedRoute = resolveRequiredRoute();
                stationDisplayUseCase.syncRoute(shellConfig, switchedRoute, stationState, traceId + "-display-switch-route");
                stationDisplayUseCase.sendCurrentStation(shellConfig, switchedRoute, stationState, traceId + "-display-switch-current");
                logStationSnapshot("手动推进到终点并切方向", traceId);
                return success("已到终点并自动切换方向", "当前方向 " + stationState.getDirectionText() + " / 本站 " + stationState.getCurrentStation());
            }
            logStationSnapshot("手动推进一站", traceId);
            return success("已推进一站", "当前站点已更新到 " + stationState.getCurrentStation());
        } catch (Exception e) {
            return failure("推进站点失败", e);
        }
    }

    private void requestPassengerCounterForArrival(String traceId) {
        if (passengerCounterMonitor == null) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "JHY query skipped on manual arrival: monitor unavailable", traceId);
            return;
        }
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "JHY query scheduled on manual arrival: station=" + stationState.getCurrentStation()
                        + " stationNo=" + stationState.getCurrentStationNo(),
                traceId
        );
        passengerCounterMonitor.requestCurrentCountAfterStationDisplay(traceId);
    }

    private void requestPassengerCounterForAutoStation(int stationType, String traceId) {
        if (passengerCounterMonitor == null) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "JHY query skipped on auto-station: monitor unavailable", traceId);
            return;
        }
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "JHY query scheduled on auto-station: stationType=" + stationType
                        + " station=" + stationState.getCurrentStation()
                        + " stationNo=" + stationState.getCurrentStationNo(),
                traceId
        );
        passengerCounterMonitor.requestCurrentCountAfterStationDisplay(traceId);
    }

    /**
     * 回退一站。
     */
    private ModuleRunResult retreatStation(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            LegacyGpsRouteResource route = resolveRequiredRoute();
            syncManualGpsSnapshotIfAvailable(traceId);
            if (stationState.getReportCount() == 0) {
                stationDisplayUseCase.syncRoute(shellConfig, route, stationState, traceId + "-display-route");
            }
            stationState.retreatStation();
            stationDisplayUseCase.sendCurrentStation(shellConfig, route, stationState, traceId + "-display-current");
            playCurrentStationAudio(requireContextOrThrow(), shellConfig, route);
            sendSerialDispatchFramesIfNeeded(traceId + "-backward-station");
            startPeriodicGpsReportIfNeeded(traceId + "-periodic-gps");
            startAutoGpsReportIfNeeded(traceId + "-auto-gps");
            logStationSnapshot("手动回退一站", traceId);
            return success("已回退一站", "当前站点已更新到 " + stationState.getCurrentStation());
        } catch (Exception e) {
            return failure("回退站点失败", e);
        }
    }

    /**
     * 手动切换上下行方向，并刷新当前线路屏显。
     */
    private ModuleRunResult switchDirection(String traceId) {
        try {
            Context context = requireContextOrThrow();
            ShellConfig shellConfig = requireShellConfig();
            LegacyGpsRouteResource route = resolveRequiredRoute();
            handleDirectionSwitch(context, route, traceId);
            LegacyGpsRouteResource switchedRoute = resolveRequiredRoute();
            stationDisplayUseCase.syncRoute(shellConfig, switchedRoute, stationState, traceId + "-display-route");
            stationDisplayUseCase.sendCurrentStation(shellConfig, switchedRoute, stationState, traceId + "-display-current");
            sendSerialDispatchFramesIfNeeded(traceId + "-switch-direction");
            logStationSnapshot("手动切换方向", traceId);
            return success("已切换方向", stationState.getLineName() + " / " + stationState.getDirectionText());
        } catch (Exception e) {
            return failure("切换方向失败", e);
        }
    }

    /**
     * 重复播报当前站。
     */
    private ModuleRunResult repeatStation(String traceId) {
        try {
            Context context = requireContextOrThrow();
            ShellConfig shellConfig = requireShellConfig();
            if (stationAudioUseCase.replayLastStation(context, shellConfig)) {
                logStationSnapshot("手动重复上一条播报", traceId);
                return success("已重复上一条播报", "重复播放刚才的报站音频");
            }
            LegacyGpsRouteResource route = resolveRequiredRoute();
            syncManualGpsSnapshotIfAvailable(traceId);
            if (stationState.getReportCount() == 0) {
                stationDisplayUseCase.syncRoute(shellConfig, route, stationState, traceId + "-display-route");
            }
            if (!stationState.repeatCurrentStation()) {
                return success("当前处于预报态", "旧版首页预报态不执行重复报站");
            }
            stationDisplayUseCase.sendCurrentStation(shellConfig, route, stationState, traceId + "-display-current");
            playCurrentStationAudio(context, shellConfig, route);
            sendSerialDispatchFramesIfNeeded(traceId + "-repeat-station");
            startPeriodicGpsReportIfNeeded(traceId + "-periodic-gps");
            startAutoGpsReportIfNeeded(traceId + "-auto-gps");
            logStationSnapshot("手动重复报站", traceId);
            return success("已重复播报当前站", "当前站点 " + stationState.getCurrentStation());
        } catch (Exception e) {
            return failure("重复报站失败", e);
        }
    }

    /**
     * 停止报站，并关闭周期/自动 GPS 上报任务。
     */
    private ModuleRunResult stopStation() {
        stationState.stopReport();
        stationAudioUseCase.stop();
        stopPeriodicGpsReport("station-stop-periodic-gps");
        stopAutoGpsReport("station-stop-auto-gps");
        logStationSnapshot("手动停止报站", "station-stop");
        return success("已停止报站", "当前报站状态已切到停止");
    }

    private void logStationSnapshot(String event, String traceId) {
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                event
                        + " / line=" + stationState.getLineName()
                        + " / direction=" + stationState.getDirectionText()
                        + " / stationNo=" + stationState.getCurrentStationNo()
                        + " / station=" + stationState.getCurrentStation()
                        + " / type=" + stationState.getCurrentStationType()
                        + " / preview=" + yesNo(stationState.isPreviewingNext())
                        + " / gps=" + stationState.getLongitude() + "," + stationState.getLatitude(),
                traceId
        );
    }

    /**
     * 确保 GPS 串口和 GPS 监听已经准备好。
     */
    private void ensureGpsReady(ShellConfig.SerialChannel gpsChannel, String traceId) {
        // GPS 串口由 GpsBusinessModule 通过原生 libgps_serial_port.so 管理。
        // 若 GpsSerialMonitor 已绑定（正常启动时 GpsBusinessModule 先于本模块初始化），
        // 跳过 open/attach，避免通过通用 serialPortAdapter（RandomAccessFile）重复打开造成阻塞。
        if (!isGpsMonitorAttached()) {
            if (!serialPortAdapter.isOpen(gpsChannel.getPortName())) {
                serialPortAdapter.open(gpsChannel.toSerialPortConfig(), traceId + "-gps-open");
            }
            gpsSerialMonitor.attach(serialPortAdapter, gpsChannel, traceId + "-gps-monitor");
        }
        stationState.bindGps(gpsChannel.getKey());
        stationState.updateGps(getLatestGpsSnapshot());
        syncRouteProfileIfNeeded();
    }

    private void syncManualGpsSnapshotIfAvailable(String traceId) {
        GpsFixSnapshot snapshot = getLatestGpsSnapshot();
        if (snapshot != null) {
            stationState.updateGps(snapshot);
        }
        syncRouteProfileIfNeeded();
        safeBusinessLog(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "手动报站 GPS 状态 gpsAttached=" + yesNo(isGpsMonitorAttached())
                        + " / valid=" + yesNo(snapshot != null && snapshot.isValid()),
                traceId
        );
    }

    private void safeBusinessLog(LogCategory category, LogLevel level, String tag, String message, String traceId) {
        try {
            AppLogCenter.log(category, level, tag, message, traceId);
        } catch (RuntimeException ignore) {
            // Local JVM unit tests do not mock every Android SDK method used by the log center.
        }
    }

    private boolean isGpsMonitorAttached() {
        return gpsSerialMonitor != null && gpsSerialMonitor.isAttached();
    }

    private GpsFixSnapshot getLatestGpsSnapshot() {
        if (gpsSerialMonitor == null) {
            return null;
        }
        return gpsSerialMonitor.getLatestSnapshot();
    }

    /**
     * 在串口调度模式下，把站点/GPS 变化同步发给 DVR 串口链。
     */
    private void sendSerialDispatchFramesIfNeeded(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig)) {
                return;
            }
            sendSerialGpsReport(shellConfig, traceId + "-gps");
            dvrSerialDispatchUseCase.sendSiteInfo(
                    shellConfig,
                    stationState,
                    getLatestGpsSnapshot(),
                    traceId + "-site"
            );
        } catch (Exception ignore) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "DVR 串口调度补发失败: " + ignore.getMessage(), traceId);
        }
    }

    private void sendSerialGpsReport(ShellConfig shellConfig, String traceId) {
        GpsFixSnapshot snapshot = getLatestGpsSnapshot();
        stationState.updateGps(snapshot);
        dvrSerialDispatchUseCase.sendGpsReport(shellConfig, stationState, snapshot, traceId);
    }

    private synchronized void startPeriodicGpsReportIfNeeded(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig) || !isGpsMonitorAttached()) {
                stopPeriodicGpsReport(traceId + "-disabled");
                return;
            }
            int intervalSeconds = resolvePeriodicGpsReportIntervalSeconds(shellConfig);
            boolean alreadyRunning = periodicGpsReportExecutor != null && !periodicGpsReportExecutor.isShutdown();
            if (alreadyRunning && periodicGpsReportIntervalSeconds == intervalSeconds) {
                return;
            }
            stopPeriodicGpsReport(traceId + "-restart");
            periodicGpsReportIntervalSeconds = intervalSeconds;
            periodicGpsReportExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "station-gps-periodic-report");
                thread.setDaemon(true);
                return thread;
            });
            periodicGpsReportExecutor.scheduleWithFixedDelay(
                    () -> sendPeriodicGpsReport(traceId),
                    intervalSeconds,
                    intervalSeconds,
                    TimeUnit.SECONDS
            );
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "已启动 DVR GPS 周期上报 intervalSeconds=" + intervalSeconds,
                    traceId
            );
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "启动 DVR GPS 周期上报失败: " + e.getMessage(),
                    traceId
            );
        }
    }

    private synchronized void stopPeriodicGpsReport(String traceId) {
        if (periodicGpsReportExecutor == null) {
            return;
        }
        periodicGpsReportExecutor.shutdownNow();
        periodicGpsReportExecutor = null;
        periodicGpsReportIntervalSeconds = 0;
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已停止 DVR GPS 周期上报",
                traceId
        );
    }

    private void sendPeriodicGpsReport(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig) || !isGpsMonitorAttached()) {
                return;
            }
            periodicGpsReportCount++;
            lastPeriodicGpsReportTimeMs = System.currentTimeMillis();
            sendSerialGpsReport(shellConfig, traceId + "-gps-" + periodicGpsReportCount);
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "DVR GPS 周期上报失败: " + e.getMessage(),
                    traceId
            );
        }
    }

    private synchronized void startAutoGpsReportIfNeeded(String traceId) {
        try {
            if (!isGpsMonitorAttached()) {
                stopAutoGpsReport(traceId + "-disabled");
                return;
            }
            boolean alreadyRunning = autoGpsReportExecutor != null && !autoGpsReportExecutor.isShutdown();
            if (alreadyRunning) {
                return;
            }
            stopAutoGpsReport(traceId + "-restart");
            autoGpsReportExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "station-gps-auto-report");
                thread.setDaemon(true);
                return thread;
            });
            autoGpsReportExecutor.scheduleWithFixedDelay(
                    () -> evaluateAutoGpsReport(traceId),
                    1,
                    1,
                    TimeUnit.SECONDS
            );
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "已启动 GPS 自动报站轮询",
                    traceId
            );
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "启动 GPS 自动报站轮询失败: " + e.getMessage(),
                    traceId
            );
        }
    }

    private synchronized void stopAutoGpsReport(String traceId) {
        if (autoGpsReportExecutor == null) {
            return;
        }
        autoGpsReportExecutor.shutdownNow();
        autoGpsReportExecutor = null;
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已停止 GPS 自动报站轮询",
                traceId
        );
    }

    // 自动报站跳过：仅在原因变化时打一条 WARN，避免每秒刷屏；恢复正常时打一条 INFO。
    private void logAutoReportSkipIfChanged(String reason, String traceId) {
        if (reason.equals(lastAutoReportSkipReason)) {
            return;
        }
        lastAutoReportSkipReason = reason;
        AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "自动报站跳过: " + reason, traceId + "-auto-gps");
    }

    private void clearAutoReportSkip(String traceId) {
        if (lastAutoReportSkipReason != null) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG,
                    "自动报站恢复正常(此前跳过原因: " + lastAutoReportSkipReason + ")", traceId + "-auto-gps");
            lastAutoReportSkipReason = null;
        }
    }

    private void evaluateAutoGpsReport(String traceId) {
        try {
            Context context = getContext();
            if (context == null) {
                logAutoReportSkipIfChanged("上下文为空", traceId);
                return;
            }
            GpsFixSnapshot snapshot = getLatestGpsSnapshot();
            if (snapshot == null) {
                logAutoReportSkipIfChanged("无GPS快照(gpsAttached=" + yesNo(isGpsMonitorAttached()) + ")", traceId);
                return;
            }
            stationState.updateGps(snapshot);
            ShellConfig shellConfig = requireShellConfig();
            LegacyGpsFlowUseCase.GpsFlowResult flowResult = gpsFlowUseCase.evaluate(
                    context,
                    shellConfig,
                    stationState.getLineName(),
                    stationState.getDirectionText(),
                    snapshot
            );
            LegacyGpsRouteResource route = flowResult.getRoute();
            if (!flowResult.hasRoute()) {
                logAutoReportSkipIfChanged("未解析到线路(line=" + stationState.getLineName()
                        + " dir=" + stationState.getDirectionText()
                        + " validFix=" + yesNo(snapshot.isValid()) + ")", traceId);
                return;
            }
            clearAutoReportSkip(traceId);
            stationState.setLineAttribute(route.getAttributeLabel());
            syncRouteProfileIfNeeded(route);
            handleAuxiliaryAudio(context, shellConfig, route, snapshot, traceId);

            autoGpsReportCount++;
            lastAutoGpsReportTimeMs = System.currentTimeMillis();
            LegacyGpsAutoReportEngine.AutoReportEvent event = flowResult.getEvent();
            if (event.isNone()) {
                return;
            }
            AppLogCenter.log(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    TAG,
                    "GPS auto report -> " + event.describe(),
                    traceId + "-auto-gps"
            );
            if (event.getOperationType() == LegacyGpsAutoReportEngine.OP_REMINDER) {
                if (event.getReminderPoint() != null) {
                    if (event.getReminderType() == LegacyGpsAutoReportEngine.REMINDER_TYPE_ENTER) {
                    stationState.recordReminder(
                        event.getReminderPoint().getReminderName(),
                        event.getReminderPoint().getCrossCode(),
                        event.getReminderPoint().getCrossType(),
                        event.getReminderPoint().getReminderNo(),
                        event.getReminderPoint().getCrossSpeedLimit(),
                        event.getReminderType()
                    );
                    dispatchBusinessModule.sendCrossInfoReport(
                        stationState,
                        event.getReminderPoint(),
                        snapshot,
                        event.getReminderType(),
                        traceId + "-cross-enter"
                    );
                        stationAudioUseCase.playReminder(context, shellConfig, route, event.getReminderPoint());
                    } else {
                    dispatchBusinessModule.sendCrossInfoReport(
                        stationState,
                        event.getReminderPoint(),
                        snapshot,
                        event.getReminderType(),
                        traceId + "-cross-leave"
                    );
                    stationState.recordReminder(
                        event.getReminderPoint().getReminderName(),
                        event.getReminderPoint().getCrossCode(),
                        event.getReminderPoint().getCrossType(),
                        event.getReminderPoint().getReminderNo(),
                        event.getReminderPoint().getCrossSpeedLimit(),
                        event.getReminderType()
                    );
                    }
                }
                return;
            }
            if (event.getOperationType() == LegacyGpsAutoReportEngine.OP_SWITCH_DIRECTION) {
                stationAudioUseCase.stop();
                handleDirectionSwitch(context, route, traceId);
                LegacyGpsRouteResource switchedRoute = resolveActiveRoute(context);
                if (switchedRoute != null) {
                    stationDisplayUseCase.syncRoute(shellConfig, switchedRoute, stationState, traceId + "-display-switch-route");
                    stationDisplayUseCase.sendCurrentStation(shellConfig, switchedRoute, stationState, traceId + "-display-switch-current");
                }
                return;
            }
            if (event.getOperationType() == LegacyGpsAutoReportEngine.OP_STATION && event.getStationPoint() != null) {
                stationState.recordAutoStation(
                        event.getStationPoint().getStationNo(),
                        event.getStationPoint().getStationName(),
                        event.getStationType()
                );
                stationAudioUseCase.playAutoStation(
                    context,
                    shellConfig,
                    route,
                    event.getStationPoint(),
                    event.getStationType()
                );
                stationDisplayUseCase.sendCurrentStation(shellConfig, route, stationState, traceId + "-display-current");
                requestPassengerCounterForAutoStation(event.getStationType(), traceId + "-auto-station-jhy-current-count");
                sendSerialDispatchFramesIfNeeded(traceId + "-auto-station");
                maybeAutoStartBus(traceId + "-auto-station");
            }
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "GPS 自动报站执行失败: " + e.getMessage(),
                    traceId
            );
        }
    }

    private void maybeAutoStartBus(String traceId) {
        if (!stationState.isFirstDeparturePreview()) {
            return;
        }
        dispatchBusinessModule.autoStartBusIfNeeded(traceId);
    }

    private void handleDirectionSwitch(Context context, LegacyGpsRouteResource currentRoute, String traceId) {
        LegacyGpsRouteResource switchedRoute = gpsFlowUseCase.resolveSwitchedRoute(context, currentRoute);
        if (switchedRoute == null) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG,
                    "切换方向失败: 未解析到反向线路 line=" + stationState.getLineName()
                            + " / 当前方向=" + stationState.getDirectionText(), traceId + "-switch-direction");
            return;
        }
        stationState.recordDirectionSwitch(switchedRoute.getDirectionText(), switchedRoute.stationNames());
        stationState.setLineAttribute(switchedRoute.getAttributeLabel());
        gpsFlowUseCase.reset(switchedRoute);
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "GPS 自动切换方向 -> " + switchedRoute.getLineName() + " / " + switchedRoute.getDirectionText(),
                traceId + "-switch-direction"
        );
    }

    private void syncRouteProfileIfNeeded() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        LegacyGpsRouteResource route = resolveActiveRoute(context);
        if (route != null) {
            syncRouteProfileIfNeeded(route);
        }
    }

    private void syncRouteProfileIfNeeded(LegacyGpsRouteResource route) {
        if (route == null) {
            return;
        }
        stationState.setLineName(route.getLineName());
        stationState.setDirectionText(route.getDirectionText());
        stationState.setLineAttribute(route.getAttributeLabel());
        if (shouldRefreshRouteProfile(route)) {
            stationState.applyLineProfile(route.getLineName(), route.getDirectionText(), route.stationNames());
            stationState.setLineAttribute(route.getAttributeLabel());
        }
    }

    private boolean shouldRefreshRouteProfile(LegacyGpsRouteResource route) {
        if (route == null || route.getStations().isEmpty()) {
            return false;
        }
        if (stationState.getStationCount() != route.getStations().size()) {
            return true;
        }
        if (!route.getLineName().equals(stationState.getLineName())) {
            return true;
        }
        if (!route.getDirectionText().equals(stationState.getDirectionText())) {
            return true;
        }
        return stationState.getCurrentStationNo() < 0;
    }

    private Context requireContextOrThrow() {
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("当前没有可用上下文");
        }
        return context;
    }

    private LegacyGpsRouteResource resolveRequiredRoute() {
        LegacyGpsRouteResource route = resolveActiveRoute(requireContextOrThrow());
        if (route == null) {
            throw new IllegalStateException("当前还没有拿到完整报站资源");
        }
        return route;
    }

    private LegacyGpsRouteResource resolveActiveRoute(Context context) {
        return gpsFlowUseCase.resolveActiveRoute(
                context,
                requireShellConfig(),
                stationState.getLineName(),
                stationState.getDirectionText()
        );
    }

    private int resolvePeriodicGpsReportIntervalSeconds(ShellConfig shellConfig) {
        int intervalSeconds = shellConfig.getBasicSetupConfig().getNetworkSettings().getInfoInterval();
        if (intervalSeconds <= 0) {
            return 5;
        }
        return Math.min(intervalSeconds, 3600);
    }

    private String describePeriodicGpsReport() {
        boolean running = periodicGpsReportExecutor != null && !periodicGpsReportExecutor.isShutdown();
        if (!running) {
            return "stopped";
        }
        return "running intervalSeconds=" + periodicGpsReportIntervalSeconds
                + " count=" + periodicGpsReportCount
                + " lastTime=" + (lastPeriodicGpsReportTimeMs <= 0 ? "-" : String.valueOf(lastPeriodicGpsReportTimeMs));
    }

    private String describeAutoGpsReport() {
        boolean running = autoGpsReportExecutor != null && !autoGpsReportExecutor.isShutdown();
        if (!running) {
            return "stopped";
        }
        return "running intervalSeconds=1"
                + " count=" + autoGpsReportCount
                + " lastTime=" + (lastAutoGpsReportTimeMs <= 0 ? "-" : String.valueOf(lastAutoGpsReportTimeMs));
    }

    private void playCurrentStationAudio(Context context, ShellConfig shellConfig, LegacyGpsRouteResource route) {
        LegacyGpsRouteResource.StationPoint currentStation = resolveCurrentStation(route);
        if (currentStation == null) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG,
                    "手动报站无声: 当前站解析失败 stationNo=" + stationState.getCurrentStationNo()
                            + " / 站点数=" + (route == null ? -1 : route.getStations().size()), "station-audio-nostation");
            return;
        }
        stationAudioUseCase.playManualStation(
                context,
                shellConfig,
                route,
                currentStation,
                stationState.getCurrentStationType()
        );
    }

    private LegacyGpsRouteResource.StationPoint resolveCurrentStation(LegacyGpsRouteResource route) {
        if (route == null) {
            return null;
        }
        int stationNo = stationState.getCurrentStationNo();
        if (stationNo < 0 || stationNo >= route.getStations().size()) {
            return null;
        }
        return route.getStations().get(stationNo);
    }

    private void handleAuxiliaryAudio(
            Context context,
            ShellConfig shellConfig,
            LegacyGpsRouteResource route,
            GpsFixSnapshot snapshot,
            String traceId
    ) {
        handleNowTime(context, shellConfig, traceId);
        handleSpeedWarning(context, shellConfig, route, snapshot, traceId);
    }

    private void handleNowTime(Context context, ShellConfig shellConfig, String traceId) {
        if (!shellConfig.getBasicSetupConfig().getNewspaperSettings().isNowTimeEnabled()) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MINUTE) != 0) {
            return;
        }
        long nowTimeKey = calendar.get(Calendar.YEAR) * 10_000L
                + calendar.get(Calendar.DAY_OF_YEAR) * 100L
                + calendar.get(Calendar.HOUR_OF_DAY);
        if (lastNowTimeKey == nowTimeKey) {
            return;
        }
        lastNowTimeKey = nowTimeKey;
        stationAudioUseCase.playNowTime(context, shellConfig, calendar.get(Calendar.HOUR_OF_DAY));
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "已触发整点报时 hour=" + calendar.get(Calendar.HOUR_OF_DAY), traceId + "-now-time");
    }

    private void handleSpeedWarning(
            Context context,
            ShellConfig shellConfig,
            LegacyGpsRouteResource route,
            GpsFixSnapshot snapshot,
            String traceId
    ) {
        if (!shellConfig.getBasicSetupConfig().getNewspaperSettings().isSpeedingWarningEnabled()) {
            resetSpeedWarningState();
            return;
        }
        long now = System.currentTimeMillis();
        if (!isGpsReadyForSpeedWarning(snapshot, now)) {
            resetSpeedWarningState();
            return;
        }
        int speedLimit = resolveSpeedLimit(route);
        if (speedLimit <= 0) {
            resetSpeedWarningState();
            return;
        }
        int speedKmh = parseSpeedKmh(snapshot);
        if (!hasSpeedWarningInput(snapshot, speedKmh)) {
            resetSpeedWarningState();
            return;
        }
        if (!isSpeedWarningActive(speedKmh, speedLimit)) {
            resetSpeedWarningState();
            return;
        }
        if (speedingStartTimeMs <= 0L) {
            speedingStartTimeMs = now;
            speedingPeakKmh = speedKmh;
            return;
        }
        speedingPeakKmh = Math.max(speedingPeakKmh, speedKmh);
        if (now - speedingStartTimeMs < 2_000L
            || now - lastSpeedWarningTimeMs < resolveSpeedWarningRepeatIntervalMs()
            || stationAudioUseCase.isBusy()) {
            return;
        }
        lastSpeedWarningTimeMs = now;
        if (stationState.isCrossingReminderActive()) {
            dispatchBusinessModule.sendCrossingOverspeedReport(
                    stationState,
                    route,
                    speedingPeakKmh,
                    parseSpeedHundredKmh(snapshot),
                    (now - speedingStartTimeMs) / 1000L,
                    snapshot,
                    traceId + "-speeding-cross"
            );
        }
        stationAudioUseCase.playSpeedWarning(context, shellConfig);
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已触发超速音 speed=" + speedKmh + "km/h limit=" + speedLimit,
                traceId + "-speed-warning"
        );
    }

    private void resetSpeedWarningState() {
        speedingStartTimeMs = 0L;
        speedingPeakKmh = 0;
    }

    private long resolveSpeedWarningRepeatIntervalMs() {
        return 2_500L;
    }

    private boolean isSpeedWarningActive(int speedKmh, int speedLimit) {
        return speedKmh > 0 && speedLimit > 0 && speedKmh > speedLimit;
    }

    private boolean hasSpeedWarningInput(GpsFixSnapshot snapshot, int speedKmh) {
        return snapshot != null
                && speedKmh > 0
                && speedKmh <= 90
                && hasText(snapshot.getLatitudeDecimal())
                && hasText(snapshot.getLongitudeDecimal());
    }

    private boolean isGpsReadyForSpeedWarning(GpsFixSnapshot snapshot, long nowMs) {
        if (snapshot == null || !snapshot.isValid()) {
            speedWarningGpsWarmupAnchorMs = nowMs;
            return false;
        }
        if (speedWarningGpsWarmupAnchorMs < 0L) {
            speedWarningGpsWarmupAnchorMs = nowMs;
            return false;
        }
        return nowMs - speedWarningGpsWarmupAnchorMs > 3_000L;
    }

    private int resolveSpeedLimit(LegacyGpsRouteResource route) {
        if (stationState.isCrossingReminderActive()) {
            int crossingSpeedLimit = parseSpeedLimitValue(stationState.getActiveCrossSpeedLimit());
            if (crossingSpeedLimit > 0) {
                return crossingSpeedLimit;
            }
        }
        LegacyGpsRouteResource.StationPoint stationPoint = resolveSpeedReferenceStation(route);
        if (stationPoint == null) {
            return 0;
        }
        return parseSpeedLimitValue(stationPoint.getSpeedLimit());
    }

    private int parseSpeedLimitValue(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(rawValue.trim()));
        } catch (Exception ignore) {
            return 0;
        }
    }

    private LegacyGpsRouteResource.StationPoint resolveSpeedReferenceStation(LegacyGpsRouteResource route) {
        if (route == null || route.getStations().isEmpty()) {
            return null;
        }
        int stationNo = stationState.getCurrentStationNo();
        if (stationState.isPreviewingNext()) {
            stationNo -= 1;
        }
        if (stationNo < 0 || stationNo >= route.getStations().size()) {
            stationNo = Math.max(0, Math.min(stationState.getCurrentStationNo(), route.getStations().size() - 1));
        }
        if (stationNo < 0 || stationNo >= route.getStations().size()) {
            return null;
        }
        return route.getStations().get(stationNo);
    }

    private int parseSpeedKmh(GpsFixSnapshot snapshot) {
        if (snapshot == null || snapshot.getSpeedKnots() == null || snapshot.getSpeedKnots().trim().isEmpty()) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(snapshot.getSpeedKnots().trim()) * 1.852d);
        } catch (Exception ignore) {
            return 0;
        }
    }

    private int parseSpeedHundredKmh(GpsFixSnapshot snapshot) {
        if (snapshot == null || snapshot.getSpeedKnots() == null || snapshot.getSpeedKnots().trim().isEmpty()) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(snapshot.getSpeedKnots().trim()) * 1.852d * 100d);
        } catch (Exception ignore) {
            return 0;
        }
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
