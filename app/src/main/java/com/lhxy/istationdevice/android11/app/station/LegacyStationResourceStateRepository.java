package com.lhxy.istationdevice.android11.app.station;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

/**
 * 旧项目“报站资源已导入”状态源。
 * <p>
 * 旧项目不少页面依赖资源是否已导入来决定是否允许保存；
 * 新壳先收口成统一状态，后续真实导入链路接回时继续复用这里。
 */
public final class LegacyStationResourceStateRepository {
    private LegacyStationResourceStateRepository() {
    }

    public static boolean isImported(@NonNull Context context) {
        return getState(context).isImported();
    }

    public static void markImported(@NonNull Context context, @NonNull String source, @NonNull String lineName) {
        StationResourceState current = getState(context);
        updateState(context, true, source, lineName, current.getDirectionText(), current.getLineAttribute());
    }

    public static void updateLineSelection(@NonNull Context context, @NonNull String source, @NonNull String lineName) {
        StationResourceState current = getState(context);
        updateState(context, current.isImported(), source, lineName, current.getDirectionText(), current.getLineAttribute());
    }

    public static void updateRouteSelection(
            @NonNull Context context,
            @NonNull String source,
            @NonNull String lineName,
            @NonNull String directionText,
            @NonNull String lineAttribute
    ) {
        StationResourceState current = getState(context);
        updateState(context, current.isImported(), source, lineName, directionText, lineAttribute);
    }

    private static void updateState(
            @NonNull Context context,
            boolean imported,
            @NonNull String source,
            @NonNull String lineName,
            @NonNull String directionText,
            @NonNull String lineAttribute
    ) {
        Context appContext = context.getApplicationContext();
        ShellConfig current = ShellConfigRepository.get(appContext);
        ShellConfig.BasicSetupConfig basicSetup = current.getBasicSetupConfig();
        ShellConfig.ResourceImportSettings resourceImportSettings = new ShellConfig.ResourceImportSettings(
                imported,
                safe(source),
                safe(lineName),
                safe(directionText),
                safe(lineAttribute),
                System.currentTimeMillis()
        );
        ShellConfig updated = new ShellConfig(
                current.getDeviceProfile(),
                current.getConfigVersion(),
                "runtime:" + ShellConfigRepository.getRuntimeConfigFile(appContext).getAbsolutePath(),
                current.getSerialChannels(),
                current.getSocketChannels(),
                current.getGpioConfig(),
                current.getCameraConfig(),
                current.getRfidConfig(),
                current.getSystemConfig(),
                current.getDebugReplay(),
                new ShellConfig.BasicSetupConfig(
                        basicSetup.getNewspaperSettings(),
                        basicSetup.getNetworkSettings(),
                        basicSetup.getSerialSettings(),
                        basicSetup.getTtsSettings(),
                        basicSetup.getLanguageSettings(),
                        basicSetup.getOtherSettings(),
                        basicSetup.getWirelessSettings(),
                        resourceImportSettings,
                        basicSetup.getProtocolLinkageSettings()
                )
        );
        try {
            ShellConfigRepository.save(appContext, updated);
            ShellRuntime.get().applyConfig(appContext, updated);
        } catch (Exception e) {
            throw new IllegalStateException("保存报站资源状态失败: " + safe(e.getMessage()), e);
        }
    }

    public static StationResourceState getState(@NonNull Context context) {
        ShellConfig.ResourceImportSettings settings = ShellConfigRepository.get(context.getApplicationContext())
                .getBasicSetupConfig()
                .getResourceImportSettings();
        return new StationResourceState(
                settings.isStationResourceImported(),
                settings.getSource(),
                settings.getLineName(),
            settings.getDirectionText(),
            settings.getLineAttribute(),
                settings.getUpdatedAt()
        );
    }

    private static String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    public static final class StationResourceState {
        private final boolean imported;
        private final String source;
        private final String lineName;
        private final String directionText;
        private final String lineAttribute;
        private final long updatedAt;

        StationResourceState(boolean imported, String source, String lineName, String directionText, String lineAttribute, long updatedAt) {
            this.imported = imported;
            this.source = source == null ? "-" : source;
            this.lineName = lineName == null ? "-" : lineName;
            this.directionText = directionText == null ? "-" : directionText;
            this.lineAttribute = lineAttribute == null ? "-" : lineAttribute;
            this.updatedAt = updatedAt;
        }

        public boolean isImported() {
            return imported;
        }

        public String getSource() {
            return source;
        }

        public String getLineName() {
            return lineName;
        }

        public String getDirectionText() {
            return directionText;
        }

        public String getLineAttribute() {
            return lineAttribute;
        }

        public long getUpdatedAt() {
            return updatedAt;
        }
    }
}
