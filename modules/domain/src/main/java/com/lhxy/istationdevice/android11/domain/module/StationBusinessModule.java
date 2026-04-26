package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsAutoReportEngine;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteCatalog;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.util.List;
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
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private final StationState stationState = new StationState();
    private final LegacyGpsRouteCatalog gpsRouteCatalog = new LegacyGpsRouteCatalog();
    private final LegacyGpsAutoReportEngine gpsAutoReportEngine = new LegacyGpsAutoReportEngine();

    private ScheduledExecutorService periodicGpsReportExecutor;
    private ScheduledExecutorService autoGpsReportExecutor;
    private int periodicGpsReportIntervalSeconds;
    private long periodicGpsReportCount;
    private long lastPeriodicGpsReportTimeMs;
    private long autoGpsReportCount;
    private long lastAutoGpsReportTimeMs;

    public StationBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SerialPortAdapter serialPortAdapter,
            GpsSerialMonitor gpsSerialMonitor,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.serialPortAdapter = serialPortAdapter;
        this.gpsSerialMonitor = gpsSerialMonitor;
        this.dvrSerialDispatchUseCase = dvrSerialDispatchUseCase;
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
        return replayAndBindGps(traceId, true);
    }

    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        if ("bind_gps".equals(actionKey)) {
            return replayAndBindGps(traceId, false);
        }
        if ("advance_station".equals(actionKey)) {
            return advanceStation(traceId);
        }
        if ("repeat_station".equals(actionKey)) {
            return repeatStation(traceId);
        }
        if ("stop_station".equals(actionKey)) {
            return stopStation();
        }
        return unsupportedAction(actionKey);
    }

    private ModuleRunResult replayAndBindGps(String traceId, boolean replayDisplay) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel gpsChannel =
                    shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            int count = replayDisplay
                    ? protocolReplayUseCase.replayStationDemo(serialPortAdapter, shellConfig, traceId)
                    : 0;
            ensureGpsReady(gpsChannel, traceId);
            if (replayDisplay) {
                stationState.advanceStation();
            }
            sendSerialDispatchFramesIfNeeded(traceId + "-bind-gps");
            startPeriodicGpsReportIfNeeded(traceId + "-periodic-gps");
            startAutoGpsReportIfNeeded(traceId + "-auto-gps");
            return success(
                    replayDisplay ? "已回放报站样例 " + count + " 条" : "已重新绑定 GPS 监听",
                    "GPS 已绑定到 " + gpsChannel.getKey()
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
            stationState.advanceStation();
            sendSerialDispatchFramesIfNeeded(traceId + "-advance-station");
            startPeriodicGpsReportIfNeeded(traceId + "-periodic-gps");
            startAutoGpsReportIfNeeded(traceId + "-auto-gps");
            return success("已推进一站", "当前站点已更新到 " + stationState.getCurrentStation());
        } catch (Exception e) {
            return failure("推进站点失败", e);
        }
    }

    private ModuleRunResult repeatStation(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel gpsChannel =
                    shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            ensureGpsReady(gpsChannel, traceId);
            stationState.repeatCurrentStation();
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

            autoGpsReportCount++;
            lastAutoGpsReportTimeMs = System.currentTimeMillis();
            LegacyGpsAutoReportEngine.AutoReportEvent event = gpsAutoReportEngine.evaluate(
                    route,
                    snapshot,
                    requireShellConfig().getBasicSetupConfig().getNewspaperSettings().isAngleEnabled()
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
                }
                return;
            }
            if (event.getOperationType() == LegacyGpsAutoReportEngine.OP_SWITCH_DIRECTION) {
                handleDirectionSwitch(context, route, traceId);
                return;
            }
            if (event.getOperationType() == LegacyGpsAutoReportEngine.OP_STATION && event.getStationPoint() != null) {
                stationState.recordAutoStation(
                        event.getStationPoint().getStationNo(),
                        event.getStationPoint().getStationName(),
                        event.getStationType()
                );
                sendSerialDispatchFramesIfNeeded(traceId + "-auto-station");
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
        if (shouldRefreshRouteProfile(route.stationNames())) {
            stationState.applyLineProfile(route.getLineName(), route.getDirectionText(), route.stationNames());
            stationState.setLineAttribute(route.getAttributeLabel());
        }
    }

    private boolean shouldRefreshRouteProfile(List<String> stationNames) {
        if (stationNames == null || stationNames.isEmpty()) {
            return false;
        }
        if ("-".equals(stationState.getTerminalStation()) || "-".equals(stationState.getNextStation())) {
            return true;
        }
        return stationState.getReportCount() == 0 && "-".equals(stationState.getCurrentStation());
    }

    private LegacyGpsRouteResource resolveActiveRoute(Context context) {
        String preferredLineName = resolvePreferredLineName();
        String preferredDirectionText = resolvePreferredDirectionText();
        return gpsRouteCatalog.load(context, preferredLineName, preferredDirectionText);
    }

    private String resolvePreferredLineName() {
        ShellConfig.ResourceImportSettings resourceImportSettings =
                requireShellConfig().getBasicSetupConfig().getResourceImportSettings();
        if (resourceImportSettings.isStationResourceImported()
                && resourceImportSettings.getLineName() != null
                && !"-".equals(resourceImportSettings.getLineName().trim())) {
            return resourceImportSettings.getLineName().trim();
        }
        return stationState.getLineName();
    }

    private String resolvePreferredDirectionText() {
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
}
