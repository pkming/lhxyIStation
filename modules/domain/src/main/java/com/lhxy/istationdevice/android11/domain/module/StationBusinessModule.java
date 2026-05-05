package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

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
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteCatalog;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.domain.station.LegacyStationAudioUseCase;
import com.lhxy.istationdevice.android11.domain.station.LegacyStationDisplayUseCase;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Station module with real GPS binding, DVR linkage, and legacy auto-report logic.
 */
public final class StationBusinessModule extends AbstractTerminalBusinessModule {
    private static final String TAG = "StationBusinessModule";

    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SerialPortAdapter serialPortAdapter;
    private final GpsSerialMonitor gpsSerialMonitor;
    private final DispatchBusinessModule dispatchBusinessModule;
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private final StationState stationState = new StationState();
    private final LegacyGpsRouteCatalog gpsRouteCatalog = new LegacyGpsRouteCatalog();
    private final LegacyGpsAutoReportEngine gpsAutoReportEngine = new LegacyGpsAutoReportEngine();
    private final LegacyStationDisplayUseCase stationDisplayUseCase;
    private final LegacyStationAudioUseCase stationAudioUseCase;

    private ScheduledExecutorService periodicGpsReportExecutor;
    private ScheduledExecutorService autoGpsReportExecutor;
    private int periodicGpsReportIntervalSeconds;
    private long periodicGpsReportCount;
    private long lastPeriodicGpsReportTimeMs;
    private long autoGpsReportCount;
    private long lastAutoGpsReportTimeMs;
    private long speedingStartTimeMs;
    private long lastSpeedWarningTimeMs;
    private long lastNowTimeKey = -1L;

    public StationBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SerialPortAdapter serialPortAdapter,
            GpioAdapter gpioAdapter,
            GpsSerialMonitor gpsSerialMonitor,
            DispatchBusinessModule dispatchBusinessModule,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.serialPortAdapter = serialPortAdapter;
        this.gpsSerialMonitor = gpsSerialMonitor;
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

    public void reloadRouteResources() {
        gpsRouteCatalog.clearCache();
        syncRouteProfileIfNeeded();
    }

    @Override
    protected void onContextUpdated() {
        syncRouteProfileIfNeeded();
        if (gpsSerialMonitor.isAttached()) {
            startPeriodicGpsReportIfNeeded("station-config-gps-report");
            startAutoGpsReportIfNeeded("station-config-auto-report");
        } else {
            stopPeriodicGpsReport("station-config-gps-report-stop");
            stopAutoGpsReport("station-config-auto-report-stop");
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
                    + "\n- GPS 监听 -> " + (gpsSerialMonitor.isAttached() ? "已绑定" : "未绑定")
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
            return success(
                    "已触发服务音 " + serviceNo,
                    "本地播报=" + yesNo(audioTriggered) + " / 485 服务音=" + yesNo(protocolSent)
            );
        } catch (Exception e) {
            return failure("服务音触发失败", e);
        }
    }

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
            return success(
                    replayDisplay ? "已准备报站主链" : "已重新绑定 GPS 监听",
                    "GPS 已绑定到 " + gpsChannel.getKey() + "，屏显链路已同步"
            );
        } catch (Exception e) {
            return failure("报站样例执行失败", e);
        }
    }

    private ModuleRunResult advanceStation(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel gpsChannel =
                    shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            ensureGpsReady(gpsChannel, traceId);
            LegacyGpsRouteResource route = resolveRequiredRoute();
            if (stationState.getReportCount() == 0) {
                stationDisplayUseCase.syncRoute(shellConfig, route, stationState, traceId + "-display-route");
            }
            stationState.advanceStation();
            stationDisplayUseCase.sendCurrentStation(shellConfig, route, stationState, traceId + "-display-current");
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
                return success("已到终点并自动切换方向", "当前方向 " + stationState.getDirectionText() + " / 本站 " + stationState.getCurrentStation());
            }
            return success("已推进一站", "当前站点已更新到 " + stationState.getCurrentStation());
        } catch (Exception e) {
            return failure("推进站点失败", e);
        }
    }

    private ModuleRunResult retreatStation(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel gpsChannel =
                    shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            ensureGpsReady(gpsChannel, traceId);
            LegacyGpsRouteResource route = resolveRequiredRoute();
            if (stationState.getReportCount() == 0) {
                stationDisplayUseCase.syncRoute(shellConfig, route, stationState, traceId + "-display-route");
            }
            stationState.retreatStation();
            stationDisplayUseCase.sendCurrentStation(shellConfig, route, stationState, traceId + "-display-current");
            playCurrentStationAudio(requireContextOrThrow(), shellConfig, route);
            sendSerialDispatchFramesIfNeeded(traceId + "-backward-station");
            startPeriodicGpsReportIfNeeded(traceId + "-periodic-gps");
            startAutoGpsReportIfNeeded(traceId + "-auto-gps");
            return success("已回退一站", "当前站点已更新到 " + stationState.getCurrentStation());
        } catch (Exception e) {
            return failure("回退站点失败", e);
        }
    }

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
            return success("已切换方向", stationState.getLineName() + " / " + stationState.getDirectionText());
        } catch (Exception e) {
            return failure("切换方向失败", e);
        }
    }

    private ModuleRunResult repeatStation(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel gpsChannel =
                    shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            ensureGpsReady(gpsChannel, traceId);
            LegacyGpsRouteResource route = resolveRequiredRoute();
            if (stationState.getReportCount() == 0) {
                stationDisplayUseCase.syncRoute(shellConfig, route, stationState, traceId + "-display-route");
            }
            if (!stationState.repeatCurrentStation()) {
                return success("当前处于预报态", "旧版首页预报态不执行重复报站");
            }
            stationDisplayUseCase.sendCurrentStation(shellConfig, route, stationState, traceId + "-display-current");
            playCurrentStationAudio(requireContextOrThrow(), shellConfig, route);
            sendSerialDispatchFramesIfNeeded(traceId + "-repeat-station");
            startPeriodicGpsReportIfNeeded(traceId + "-periodic-gps");
            startAutoGpsReportIfNeeded(traceId + "-auto-gps");
            return success("已重复播报当前站", "当前站点 " + stationState.getCurrentStation());
        } catch (Exception e) {
            return failure("重复报站失败", e);
        }
    }

    private ModuleRunResult stopStation() {
        stationState.stopReport();
        stationAudioUseCase.stop();
        stopPeriodicGpsReport("station-stop-periodic-gps");
        stopAutoGpsReport("station-stop-auto-gps");
        return success("已停止报站", "当前报站状态已切到停止");
    }

    private void ensureGpsReady(ShellConfig.SerialChannel gpsChannel, String traceId) {
        if (!serialPortAdapter.isOpen(gpsChannel.getPortName())) {
            serialPortAdapter.open(gpsChannel.toSerialPortConfig(), traceId + "-gps-open");
        }
        if (!gpsSerialMonitor.isAttached()) {
            gpsSerialMonitor.attach(serialPortAdapter, gpsChannel, traceId + "-gps-monitor");
        }
        stationState.bindGps(gpsChannel.getKey());
        stationState.updateGps(gpsSerialMonitor.getLatestSnapshot());
        syncRouteProfileIfNeeded();
    }

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
                    gpsSerialMonitor.getLatestSnapshot(),
                    traceId + "-site"
            );
        } catch (Exception ignore) {
            // Keep station state moving even when serial dispatch frames fail.
        }
    }

    private void sendSerialGpsReport(ShellConfig shellConfig, String traceId) {
        GpsFixSnapshot snapshot = gpsSerialMonitor.getLatestSnapshot();
        stationState.updateGps(snapshot);
        dvrSerialDispatchUseCase.sendGpsReport(shellConfig, stationState, snapshot, traceId);
    }

    private synchronized void startPeriodicGpsReportIfNeeded(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig) || !gpsSerialMonitor.isAttached()) {
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
            if (!dvrSerialDispatchUseCase.canUse(shellConfig) || !gpsSerialMonitor.isAttached()) {
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
            if (!gpsSerialMonitor.isAttached()) {
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

    private void evaluateAutoGpsReport(String traceId) {
        try {
            Context context = getContext();
            if (context == null) {
                return;
            }
            GpsFixSnapshot snapshot = gpsSerialMonitor.getLatestSnapshot();
            if (snapshot == null) {
                return;
            }
            stationState.updateGps(snapshot);
            LegacyGpsRouteResource route = resolveActiveRoute(context);
            if (route == null) {
                return;
            }
            stationState.setLineAttribute(route.getAttributeLabel());
            syncRouteProfileIfNeeded(route);
                ShellConfig shellConfig = requireShellConfig();
                handleAuxiliaryAudio(context, shellConfig, route, snapshot, traceId);

            autoGpsReportCount++;
            lastAutoGpsReportTimeMs = System.currentTimeMillis();
            LegacyGpsAutoReportEngine.AutoReportEvent event = gpsAutoReportEngine.evaluate(
                    route,
                    snapshot,
                    shellConfig.getBasicSetupConfig().getNewspaperSettings().isAngleEnabled()
            );
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
                    stationState.recordReminder(event.getReminderPoint().getReminderName());
                    stationAudioUseCase.playReminder(context, shellConfig, route, event.getReminderPoint());
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
        String nextDirection = currentRoute.getDirectionText().contains("下") ? "上行" : "下行";
        LegacyGpsRouteResource switchedRoute = gpsRouteCatalog.load(context, currentRoute.getLineName(), nextDirection);
        if (switchedRoute == null) {
            return;
        }
        stationState.recordDirectionSwitch(switchedRoute.getDirectionText(), switchedRoute.stationNames());
        stationState.setLineAttribute(switchedRoute.getAttributeLabel());
        gpsAutoReportEngine.reset(switchedRoute.getLineName() + "|" + switchedRoute.getDirectionText());
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
        String preferredLineName = resolvePreferredLineName();
        String preferredDirectionText = resolvePreferredDirectionText();
        return gpsRouteCatalog.load(context, preferredLineName, preferredDirectionText);
    }

    private String resolvePreferredLineName() {
        ShellConfig.ResourceImportSettings resourceImportSettings =
                requireShellConfig().getBasicSetupConfig().getResourceImportSettings();
        if (resourceImportSettings.getLineName() != null
            && !resourceImportSettings.getLineName().trim().isEmpty()
                && !"-".equals(resourceImportSettings.getLineName().trim())) {
            return resourceImportSettings.getLineName().trim();
        }
        return stationState.getLineName();
    }

    private String resolvePreferredDirectionText() {
        ShellConfig.ResourceImportSettings resourceImportSettings =
                requireShellConfig().getBasicSetupConfig().getResourceImportSettings();
        if (resourceImportSettings.getLineName() != null
            && !resourceImportSettings.getLineName().trim().isEmpty()
            && !"-".equals(resourceImportSettings.getLineName().trim())
            && resourceImportSettings.getDirectionText() != null
            && !resourceImportSettings.getDirectionText().trim().isEmpty()
            && !"-".equals(resourceImportSettings.getDirectionText().trim())) {
            return resourceImportSettings.getDirectionText().trim();
        }
        String directionText = stationState.getDirectionText();
        if (directionText == null || directionText.trim().isEmpty()) {
            return "上行";
        }
        return directionText.trim();
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
        int speedLimit = resolveSpeedLimit(route);
        if (speedLimit <= 0) {
            resetSpeedWarningState();
            return;
        }
        int speedKmh = parseSpeedKmh(snapshot);
        if (speedKmh <= 0 || speedKmh <= speedLimit - 4) {
            resetSpeedWarningState();
            return;
        }
        long now = System.currentTimeMillis();
        if (speedingStartTimeMs <= 0L) {
            speedingStartTimeMs = now;
            return;
        }
        if (now - speedingStartTimeMs < 2_000L || now - lastSpeedWarningTimeMs < 1_000L || stationAudioUseCase.isBusy()) {
            return;
        }
        lastSpeedWarningTimeMs = now;
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
    }

    private int resolveSpeedLimit(LegacyGpsRouteResource route) {
        LegacyGpsRouteResource.StationPoint stationPoint = resolveSpeedReferenceStation(route);
        if (stationPoint == null || stationPoint.getSpeedLimit() == null || stationPoint.getSpeedLimit().trim().isEmpty()) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(stationPoint.getSpeedLimit().trim()));
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
}
