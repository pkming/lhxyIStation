package com.lhxy.istationdevice.android11.domain.gps;

import android.content.Context;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

/**
 * 旧版 GPS 运行流程用例。
 * <p>
 * 负责线路解析、自动报站判定和方向切换后的线路重选。
 * <p>
 * 查找关键字：线路解析、自动报站流程、切换方向、GPS 流程总入口。
 */
public final class LegacyGpsFlowUseCase {
    private final LegacyGpsRouteCatalog routeCatalog = new LegacyGpsRouteCatalog();
    private final LegacyGpsAutoReportEngine autoReportEngine = new LegacyGpsAutoReportEngine();

    /**
     * 清空线路资源缓存。
     */
    public void clearCache() {
        routeCatalog.clearCache();
    }

    /**
     * 直接按线路名和方向读取线路资源。
     */
    public LegacyGpsRouteResource load(Context context, String lineName, String directionText) {
        return routeCatalog.load(context, lineName, directionText);
    }

    /**
     * 解析当前激活线路。
     * <p>
     * 优先使用资源导入配置里的线路和方向，再回退到调用方给的兜底值。
     */
    public LegacyGpsRouteResource resolveActiveRoute(
            Context context,
            ShellConfig shellConfig,
            String fallbackLineName,
            String fallbackDirectionText
    ) {
        if (context == null || shellConfig == null) {
            return null;
        }
        return routeCatalog.load(
                context,
                resolvePreferredLineName(shellConfig, fallbackLineName),
                resolvePreferredDirectionText(shellConfig, fallbackLineName, fallbackDirectionText)
        );
    }

        /**
         * 执行一次 GPS 流程判定，并返回线路、快照和自动报站事件摘要。
         */
    public GpsFlowResult evaluate(
            Context context,
            ShellConfig shellConfig,
            String fallbackLineName,
            String fallbackDirectionText,
            GpsFixSnapshot snapshot
    ) {
        LegacyGpsRouteResource route = resolveActiveRoute(context, shellConfig, fallbackLineName, fallbackDirectionText);
        if (route == null) {
            return GpsFlowResult.missingRoute(snapshot);
        }
        boolean angleEnabled = shellConfig.getBasicSetupConfig().getNewspaperSettings().isAngleEnabled();
        if (snapshot == null || !snapshot.isValid()) {
            return GpsFlowResult.invalidFix(route, snapshot, angleEnabled);
        }
        LegacyGpsAutoReportEngine.AutoReportEvent event = autoReportEngine.evaluate(route, snapshot, angleEnabled);
        return GpsFlowResult.of(route, snapshot, angleEnabled, event);
    }

    /**
     * 根据当前线路切换出对向线路。
     */
    public LegacyGpsRouteResource resolveSwitchedRoute(Context context, LegacyGpsRouteResource currentRoute) {
        if (context == null || currentRoute == null) {
            return null;
        }
        String nextDirection = currentRoute.getDirectionText().contains("下") ? "上行" : "下行";
        return routeCatalog.load(context, currentRoute.getLineName(), nextDirection);
    }

    /**
     * 重置某条线路对应的自动报站状态机。
     */
    public void reset(LegacyGpsRouteResource route) {
        if (route == null) {
            return;
        }
        autoReportEngine.reset(route.getLineName() + "|" + route.getDirectionText());
    }

    private String resolvePreferredLineName(ShellConfig shellConfig, String fallbackLineName) {
        ShellConfig.ResourceImportSettings resourceImportSettings =
                shellConfig.getBasicSetupConfig().getResourceImportSettings();
        if (resourceImportSettings.getLineName() != null
                && !resourceImportSettings.getLineName().trim().isEmpty()
                && !"-".equals(resourceImportSettings.getLineName().trim())) {
            return resourceImportSettings.getLineName().trim();
        }
        return fallbackLineName == null ? "-" : fallbackLineName.trim();
    }

    private String resolvePreferredDirectionText(
            ShellConfig shellConfig,
            String fallbackLineName,
            String fallbackDirectionText
    ) {
        ShellConfig.ResourceImportSettings resourceImportSettings =
                shellConfig.getBasicSetupConfig().getResourceImportSettings();
        if (resourceImportSettings.getLineName() != null
                && !resourceImportSettings.getLineName().trim().isEmpty()
                && !"-".equals(resourceImportSettings.getLineName().trim())
                && resourceImportSettings.getDirectionText() != null
                && !resourceImportSettings.getDirectionText().trim().isEmpty()
                && !"-".equals(resourceImportSettings.getDirectionText().trim())) {
            return resourceImportSettings.getDirectionText().trim();
        }
        if (fallbackLineName != null && !fallbackLineName.trim().isEmpty()) {
            if (fallbackDirectionText != null && !fallbackDirectionText.trim().isEmpty()) {
                return fallbackDirectionText.trim();
            }
        }
        return "上行";
    }

    /**
     * GPS 流程执行结果。
     */
    public static final class GpsFlowResult {
        private final LegacyGpsRouteResource route;
        private final GpsFixSnapshot snapshot;
        private final boolean angleEnabled;
        private final LegacyGpsAutoReportEngine.AutoReportEvent event;

        private GpsFlowResult(
                LegacyGpsRouteResource route,
                GpsFixSnapshot snapshot,
                boolean angleEnabled,
                LegacyGpsAutoReportEngine.AutoReportEvent event
        ) {
            this.route = route;
            this.snapshot = snapshot;
            this.angleEnabled = angleEnabled;
            this.event = event == null ? LegacyGpsAutoReportEngine.AutoReportEvent.none() : event;
        }

        public static GpsFlowResult of(
                LegacyGpsRouteResource route,
                GpsFixSnapshot snapshot,
                boolean angleEnabled,
                LegacyGpsAutoReportEngine.AutoReportEvent event
        ) {
            return new GpsFlowResult(route, snapshot, angleEnabled, event);
        }

        public static GpsFlowResult missingRoute(GpsFixSnapshot snapshot) {
            return new GpsFlowResult(null, snapshot, false, LegacyGpsAutoReportEngine.AutoReportEvent.none());
        }

        public static GpsFlowResult invalidFix(
                LegacyGpsRouteResource route,
                GpsFixSnapshot snapshot,
                boolean angleEnabled
        ) {
            return new GpsFlowResult(route, snapshot, angleEnabled, LegacyGpsAutoReportEngine.AutoReportEvent.none());
        }

        public LegacyGpsRouteResource getRoute() {
            return route;
        }

        public GpsFixSnapshot getSnapshot() {
            return snapshot;
        }

        public boolean isAngleEnabled() {
            return angleEnabled;
        }

        public LegacyGpsAutoReportEngine.AutoReportEvent getEvent() {
            return event;
        }

        public boolean hasRoute() {
            return route != null;
        }

        public boolean hasValidFix() {
            return snapshot != null && snapshot.isValid();
        }
    }
}