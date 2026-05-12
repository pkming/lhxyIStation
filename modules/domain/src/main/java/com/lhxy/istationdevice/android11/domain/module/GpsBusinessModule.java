package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsAutoReportEngine;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsFlowUseCase;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteCatalog;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.GpsState;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.util.Calendar;

/**
 * GPS 独立业务模块。
 * <p>
 * 负责 GPS 串口绑定、线路资源扫描、自动报站判定和 GPS 校时。
 * <p>
 * 查找关键字：GPS 绑定、自动报站判定、线路扫描、GPS 校时。
 */
public final class GpsBusinessModule extends AbstractTerminalBusinessModule {
    private final SerialPortAdapter serialPortAdapter;
    private final GpsSerialMonitor gpsSerialMonitor;
    private final SystemOps systemOps;
    private final LegacyGpsRouteCatalog gpsRouteCatalog = new LegacyGpsRouteCatalog();
    private final LegacyGpsFlowUseCase gpsFlowUseCase = new LegacyGpsFlowUseCase();
    private final GpsState gpsState = new GpsState();
    private volatile boolean gpsTimeInitialized;
    private volatile String lastGpsTimeKey = "-";

    public GpsBusinessModule(SerialPortAdapter serialPortAdapter, GpsSerialMonitor gpsSerialMonitor, SystemOps systemOps) {
        this.serialPortAdapter = serialPortAdapter;
        this.gpsSerialMonitor = gpsSerialMonitor;
        this.systemOps = systemOps;
        this.gpsSerialMonitor.addSnapshotListener(this::onGpsSnapshot);
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
        try {
            ensureGpsReady("gps-context");
        } catch (Exception ignore) {
            // Keep the module context refresh resilient while the device config is still incomplete.
        }
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

    /**
     * GPS 模块动作总入口。
     */
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
        if ("evaluate_auto_report".equals(actionKey)) {
            return evaluateAutoReport(traceId);
        }
        if ("sync_gps_time".equals(actionKey)) {
            return syncGpsTime(traceId, true);
        }
        if ("switch_direction".equals(actionKey)) {
            return switchDirection(traceId);
        }
        return unsupportedAction(actionKey);
    }

    /**
     * 绑定 GPS 串口与监听器，并在可用时补看当前线路。
     */
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

    /**
     * 扫描当前生效线路资源。
     */
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

    /**
     * 扫描 L1 基线线路，便于和旧项目资源做快速对照。
     */
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

    /**
     * 执行一次自动报站判定。
     * <p>
     * 这里只做判定和摘要，不直接推进站点状态。
     */
    private ModuleRunResult evaluateAutoReport(String traceId) {
        try {
            ensureGpsReady(traceId);
            if (!hasPreferredLineName()) {
                gpsState.recordAutoReport("自动报站判定未执行", "当前还没有可用线路名，请先导入 SourceFile 资源");
                return failureText("自动报站判定失败", "当前还没有可用线路名，请先导入 SourceFile 资源");
            }
            LegacyGpsRouteResource route = inspectActiveRoute();
            if (route == null) {
                gpsState.recordAutoReport(
                        "自动报站判定未执行",
                        "没有找到线路 " + gpsState.getLineName() + " / " + gpsState.getDirectionText()
                );
                return failureText("自动报站判定失败", "没有找到线路 " + gpsState.getLineName() + " / " + gpsState.getDirectionText());
            }
            LegacyGpsFlowUseCase.GpsFlowResult flowResult = gpsFlowUseCase.evaluate(
                    requireContextOrThrow(),
                    requireShellConfig(),
                    gpsState.getLineName(),
                    gpsState.getDirectionText(),
                    gpsSerialMonitor.getLatestSnapshot()
            );
            GpsFixSnapshot snapshot = flowResult.getSnapshot();
            if (!flowResult.hasValidFix()) {
                gpsState.recordAutoReport("自动报站判定未命中", "当前 GPS 还没有有效定位");
                return failureText("自动报站判定失败", "当前 GPS 还没有有效定位");
            }
            LegacyGpsAutoReportEngine.AutoReportEvent event = flowResult.getEvent();
            String detail = buildAutoReportDetail(route, snapshot, event, flowResult.isAngleEnabled());
            if (event.isNone()) {
                gpsState.recordAutoReport("自动报站未命中", detail);
                return success("当前未命中自动报站", detail);
            }
            String summary = resolveAutoReportSummary(event);
            gpsState.recordAutoReport(summary, detail);
            return success(summary, detail);
        } catch (Exception e) {
            return failure("自动报站判定失败", e);
        }
    }

    /**
     * 按当前 GPS 时间执行校时。
     */
    private ModuleRunResult syncGpsTime(String traceId, boolean force) {
        try {
            ensureGpsReady(traceId);
            GpsFixSnapshot snapshot = gpsSerialMonitor.getLatestSnapshot();
            if (snapshot == null || !snapshot.isValid()) {
                gpsState.recordTimeSync("GPS 校时未执行", "当前 GPS 还没有有效定位");
                return failureText("GPS 校时失败", "当前 GPS 还没有有效定位");
            }
            String gpsTimeKey = buildGpsTimeKey(snapshot);
            if (!force && (gpsTimeInitialized || gpsTimeKey.equals(lastGpsTimeKey))) {
                gpsState.recordTimeSync("GPS 校时已完成", "当前批次 GPS 时间已经同步过");
                return success("GPS 校时已完成", "当前批次 GPS 时间已经同步过");
            }
            long timeMillis = buildLegacyGpsTimeMillis(snapshot);
            if (timeMillis <= 0L) {
                gpsState.recordTimeSync("GPS 校时未执行", "GPS 日期时间还不满足旧 M90 校时条件");
                return failureText("GPS 校时失败", "GPS 日期时间还不满足旧 M90 校时条件");
            }
            systemOps.setSystemTime(timeMillis, traceId + "-gps-time");
            gpsTimeInitialized = true;
            lastGpsTimeKey = gpsTimeKey;
            String detail = "gpsDate=" + emptyAsDash(snapshot.getDate()) + " / gpsTime=" + emptyAsDash(snapshot.getTime());
            gpsState.recordTimeSync("已按 GPS 时间完成校时", detail);
            return success("已按 GPS 时间完成校时", detail);
        } catch (Exception e) {
            return failure("GPS 校时失败", e);
        }
    }

    /**
     * 切换当前偏好的上下行方向，并重新加载对应线路。
     */
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

    /**
     * 把 GPS 监听器里的最新状态同步到页面状态对象。
     */
    private void syncMonitorState() {
        gpsState.bindMonitor(gpsSerialMonitor.getAttachedChannelKey(), gpsSerialMonitor.getAttachedPortName(), gpsSerialMonitor.isAttached());
        gpsState.applySnapshot(gpsSerialMonitor.getLatestSnapshot());
    }

    /**
     * 从资源导入配置里同步首选线路和方向。
     */
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

    /**
     * 确保 GPS 串口和监听器已经附着完成。
     */
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

    /**
     * 解析当前激活线路，并把摘要回写到 GPS 状态。
     */
    private LegacyGpsRouteResource inspectActiveRoute() {
        Context context = requireContextOrThrow();
        LegacyGpsRouteResource route = gpsFlowUseCase.resolveActiveRoute(
                context,
                requireShellConfig(),
                gpsState.getLineName(),
                gpsState.getDirectionText()
        );
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

    private String resolveAutoReportSummary(LegacyGpsAutoReportEngine.AutoReportEvent event) {
        if (event.getOperationType() == LegacyGpsAutoReportEngine.OP_REMINDER) {
            return "已命中友情提醒";
        }
        if (event.getOperationType() == LegacyGpsAutoReportEngine.OP_SWITCH_DIRECTION) {
            return "已命中方向切换判定";
        }
        if (event.getOperationType() == LegacyGpsAutoReportEngine.OP_STATION) {
            return event.getStationType() == LegacyGpsAutoReportEngine.STATION_TYPE_ENTER
                    ? "已命中进站判定"
                    : "已命中出站判定";
        }
        return "自动报站未命中";
    }

    private String buildAutoReportDetail(
            LegacyGpsRouteResource route,
            GpsFixSnapshot snapshot,
            LegacyGpsAutoReportEngine.AutoReportEvent event,
            boolean angleEnabled
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append(route.getLineName())
                .append(" / ")
                .append(route.getDirectionText())
                .append(" / angle=")
                .append(yesNo(angleEnabled))
                .append(" / ");
        if (event == null || event.isNone()) {
            builder.append("event=none");
        } else {
            builder.append("event=").append(event.describe());
        }
        builder.append(" / ").append(snapshot.describeSummary());
        return builder.toString();
    }

    private void onGpsSnapshot(GpsFixSnapshot snapshot) {
        if (snapshot == null || getContext() == null) {
            return;
        }
        gpsState.applySnapshot(snapshot);
        tryAutoSyncGpsTime(snapshot);
    }

    private void tryAutoSyncGpsTime(GpsFixSnapshot snapshot) {
        if (snapshot == null || !snapshot.isValid()) {
            return;
        }
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!shellConfig.getSystemConfig().isAllowSetTime()) {
                return;
            }
            if (gpsTimeInitialized) {
                return;
            }
            String gpsTimeKey = buildGpsTimeKey(snapshot);
            if (gpsTimeKey.equals(lastGpsTimeKey)) {
                return;
            }
            long timeMillis = buildLegacyGpsTimeMillis(snapshot);
            if (timeMillis <= 0L) {
                return;
            }
            systemOps.setSystemTime(timeMillis, getKey() + "-auto-gps-time");
            gpsTimeInitialized = true;
            lastGpsTimeKey = gpsTimeKey;
            gpsState.recordTimeSync(
                    "已按 GPS 时间自动校时",
                    "gpsDate=" + emptyAsDash(snapshot.getDate()) + " / gpsTime=" + emptyAsDash(snapshot.getTime())
            );
        } catch (Exception ignored) {
            // Keep GPS parsing alive even if time sync fails.
        }
    }

    private String buildGpsTimeKey(GpsFixSnapshot snapshot) {
        if (snapshot == null) {
            return "-";
        }
        return emptyAsDash(snapshot.getDate()) + "-" + emptyAsDash(snapshot.getTime());
    }

    private long buildLegacyGpsTimeMillis(GpsFixSnapshot snapshot) {
        if (snapshot == null) {
            return 0L;
        }
        String gpsDate = snapshot.getDate();
        String gpsTime = snapshot.getTime();
        if (gpsDate == null || gpsTime == null) {
            return 0L;
        }
        gpsDate = gpsDate.trim();
        gpsTime = gpsTime.trim();
        if (gpsDate.length() < 6 || gpsTime.length() < 6) {
            return 0L;
        }
        String yearSuffix = gpsDate.substring(4, 6);
        if (!isNumeric(yearSuffix) || Integer.parseInt(yearSuffix) < 19) {
            return 0L;
        }
        String day = gpsDate.substring(0, 2);
        String month = gpsDate.substring(2, 4);
        String year = "20" + yearSuffix;
        String hour = gpsTime.substring(0, 2);
        String minute = gpsTime.substring(2, 4);
        String second = gpsTime.substring(4, 6);
        if (!isNumeric(day) || !isNumeric(month) || !isNumeric(hour) || !isNumeric(minute) || !isNumeric(second)) {
            return 0L;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                Integer.parseInt(year),
                Integer.parseInt(month) - 1,
                Integer.parseInt(day),
                Integer.parseInt(hour) + 8,
                Integer.parseInt(minute),
                Integer.parseInt(second)
        );
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            if (!Character.isDigit(value.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private Context requireContextOrThrow() {
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("当前没有可用上下文");
        }
        return context;
    }
}