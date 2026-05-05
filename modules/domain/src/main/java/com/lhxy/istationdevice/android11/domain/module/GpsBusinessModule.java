package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteCatalog;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.GpsState;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;

/**
 * GPS 独立业务模块。
 */
public final class GpsBusinessModule extends AbstractTerminalBusinessModule {
    private final SerialPortAdapter serialPortAdapter;
    private final GpsSerialMonitor gpsSerialMonitor;
    private final LegacyGpsRouteCatalog gpsRouteCatalog = new LegacyGpsRouteCatalog();
    private final GpsState gpsState = new GpsState();

    public GpsBusinessModule(SerialPortAdapter serialPortAdapter, GpsSerialMonitor gpsSerialMonitor) {
        this.serialPortAdapter = serialPortAdapter;
        this.gpsSerialMonitor = gpsSerialMonitor;
    }

    @Override
    public String getKey() {
        return "gps";
    }

    @Override
    public String getTitle() {
        return "GPS";
    }

    @Override
    public String describePurpose() {
        return "承接 GPS 串口绑定、线路资源核对和完整迁移前的基线校验。";
    }

    @Override
    protected void onContextUpdated() {
        syncMonitorState();
        syncPreferredRouteSelection();
        if (getContext() != null && hasPreferredLineName()) {
            inspectActiveRoute();
        }
    }

    @Override
    public String describeStatus() {
        syncMonitorState();
        if (getContext() != null && hasPreferredLineName()) {
            inspectActiveRoute();
        }
        return gpsState.describe() + "\n- " + describeActionMemory();
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        return bindGps(traceId);
    }

    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        if ("bind_gps".equals(actionKey)) {
            return bindGps(traceId);
        }
        if ("scan_active_route".equals(actionKey)) {
            return scanActiveRoute(traceId);
        }
        if ("scan_l1_baseline".equals(actionKey)) {
            return scanL1Baseline(traceId);
        }
        if ("switch_direction".equals(actionKey)) {
            return switchDirection(traceId);
        }
        return unsupportedAction(actionKey);
    }

    private ModuleRunResult bindGps(String traceId) {
        try {
            ShellConfig.SerialChannel gpsChannel = ensureGpsReady(traceId);
            LegacyGpsRouteResource route = hasPreferredLineName() ? inspectActiveRoute() : null;
            String detail = "GPS 已绑定到 " + gpsChannel.getKey() + "/" + gpsChannel.getPortName();
            if (route != null) {
                detail += "，当前线路 " + buildRouteSummary(route);
            }
            return success("已绑定 GPS 模块", detail);
        } catch (Exception e) {
            return failure("绑定 GPS 模块失败", e);
        }
    }

    private ModuleRunResult scanActiveRoute(String traceId) {
        try {
            syncMonitorState();
            if (!hasPreferredLineName()) {
                return failureText("扫描 GPS 线路资源失败", "当前还没有可用线路名，请先导入 SourceFile 资源");
            }
            LegacyGpsRouteResource route = inspectActiveRoute();
            if (route == null) {
                return failureText("扫描 GPS 线路资源失败", "没有找到线路 " + gpsState.getLineName() + " / " + gpsState.getDirectionText());
            }
            return success("已扫描 GPS 线路资源", buildRouteSummary(route));
        } catch (Exception e) {
            return failure("扫描 GPS 线路资源失败", e);
        }
    }

    private ModuleRunResult scanL1Baseline(String traceId) {
        try {
            Context context = requireContextOrThrow();
            LegacyGpsRouteResource upRoute = gpsRouteCatalog.load(context, "L1", "上行");
            LegacyGpsRouteResource downRoute = gpsRouteCatalog.load(context, "L1", "下行");
            if (upRoute == null && downRoute == null) {
                return failureText("扫描 L1 基线失败", "当前资源里没有找到 L1 上下行线路");
            }
            String summary = "L1 基线"
                    + " / up=" + summarizeMaybeRoute(upRoute)
                    + " / down=" + summarizeMaybeRoute(downRoute);
            gpsState.markBaselineSummary(summary);
            return success("已扫描 L1 基线", summary);
        } catch (Exception e) {
            return failure("扫描 L1 基线失败", e);
        }
    }

    private ModuleRunResult switchDirection(String traceId) {
        try {
            syncPreferredRouteSelection();
            if (!hasPreferredLineName()) {
                return failureText("切换 GPS 方向失败", "当前还没有可用线路名，请先导入 SourceFile 资源");
            }
            gpsState.toggleDirection();
            LegacyGpsRouteResource route = inspectActiveRoute();
            if (route == null) {
                return failureText("切换 GPS 方向失败", "没有找到线路 " + gpsState.getLineName() + " / " + gpsState.getDirectionText());
            }
            return success("已切换 GPS 方向", buildRouteSummary(route));
        } catch (Exception e) {
            return failure("切换 GPS 方向失败", e);
        }
    }

    private void syncMonitorState() {
        gpsState.bindMonitor(gpsSerialMonitor.getAttachedChannelKey(), gpsSerialMonitor.getAttachedPortName(), gpsSerialMonitor.isAttached());
        gpsState.applySnapshot(gpsSerialMonitor.getLatestSnapshot());
    }

    private void syncPreferredRouteSelection() {
        ShellConfig.ResourceImportSettings resourceImportSettings = requireShellConfig().getBasicSetupConfig().getResourceImportSettings();
        if (resourceImportSettings.getLineName() != null
                && !resourceImportSettings.getLineName().trim().isEmpty()
                && !"-".equals(resourceImportSettings.getLineName().trim())) {
            gpsState.setPreferredLineName(resourceImportSettings.getLineName());
            if (resourceImportSettings.getDirectionText() != null
                    && !resourceImportSettings.getDirectionText().trim().isEmpty()
                    && !"-".equals(resourceImportSettings.getDirectionText().trim())) {
                gpsState.setPreferredDirectionText(resourceImportSettings.getDirectionText());
                return;
            }
        }
        gpsState.setPreferredDirectionText(gpsState.getDirectionText());
    }

    private ShellConfig.SerialChannel ensureGpsReady(String traceId) {
        ShellConfig shellConfig = requireShellConfig();
        ShellConfig.SerialChannel gpsChannel = shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
        if (!serialPortAdapter.isOpen(gpsChannel.getPortName())) {
            serialPortAdapter.open(gpsChannel.toSerialPortConfig(), traceId + "-gps-open");
        }
        if (!gpsSerialMonitor.isAttached()) {
            gpsSerialMonitor.attach(serialPortAdapter, gpsChannel, traceId + "-gps-monitor");
        }
        gpsState.bindMonitor(gpsChannel.getKey(), gpsChannel.getPortName(), true);
        gpsState.applySnapshot(gpsSerialMonitor.getLatestSnapshot());
        return gpsChannel;
    }

    private LegacyGpsRouteResource inspectActiveRoute() {
        Context context = requireContextOrThrow();
        LegacyGpsRouteResource route = gpsRouteCatalog.load(context, gpsState.getLineName(), gpsState.getDirectionText());
        if (route == null) {
            gpsState.markMissingRoute(gpsState.getLineName(), gpsState.getDirectionText());
            return null;
        }
        gpsState.applyRoute(route);
        return route;
    }

    private boolean hasPreferredLineName() {
        String lineName = gpsState.getLineName();
        return lineName != null && !lineName.trim().isEmpty() && !"-".equals(lineName.trim());
    }

    private String buildRouteSummary(LegacyGpsRouteResource route) {
        return route.getLineName() + " / " + route.getDirectionText()
                + " / attr=" + route.getAttributeLabel()
                + " / stations=" + countStationCoordinates(route) + "/" + route.getStations().size()
                + " / reminders=" + countReminderCoordinates(route) + "/" + route.getReminders().size();
    }

    private String summarizeMaybeRoute(LegacyGpsRouteResource route) {
        if (route == null) {
            return "missing";
        }
        return countStationCoordinates(route) + "/" + route.getStations().size()
                + " stations, " + countReminderCoordinates(route) + "/" + route.getReminders().size() + " reminders";
    }

    private int countStationCoordinates(LegacyGpsRouteResource route) {
        int count = 0;
        for (LegacyGpsRouteResource.StationPoint point : route.getStations()) {
            if (hasCoordinate(point.getLongitudeDecimal(), point.getLatitudeDecimal())) {
                count++;
            }
        }
        return count;
    }

    private int countReminderCoordinates(LegacyGpsRouteResource route) {
        int count = 0;
        for (LegacyGpsRouteResource.ReminderPoint point : route.getReminders()) {
            if (hasCoordinate(point.getLongitudeDecimal(), point.getLatitudeDecimal())) {
                count++;
            }
        }
        return count;
    }

    private boolean hasCoordinate(double longitude, double latitude) {
        return Math.abs(longitude) > 0.000001d && Math.abs(latitude) > 0.000001d;
    }

    private Context requireContextOrThrow() {
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("当前没有可用上下文");
        }
        return context;
    }
}