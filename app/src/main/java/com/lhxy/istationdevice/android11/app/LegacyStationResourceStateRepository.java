package com.lhxy.istationdevice.android11.app;

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
final class LegacyStationResourceStateRepository {
    private LegacyStationResourceStateRepository() {
    }

    static boolean isImported(@NonNull Context context) {
        return getState(context).isImported();
    }

    static void markImported(@NonNull Context context, @NonNull String source, @NonNull String lineName) {
        updateState(context, true, source, lineName);
    }

    static void updateLineSelection(@NonNull Context context, @NonNull String source, @NonNull String lineName) {
        StationResourceState current = getState(context);
        updateState(context, current.isImported(), source, lineName);
    }

    private static void updateState(
            @NonNull Context context,
            boolean imported,
            @NonNull String source,
            @NonNull String lineName
    ) {
        Context appContext = context.getApplicationContext();
        ShellConfig current = ShellConfigRepository.get(appContext);
        ShellConfig.BasicSetupConfig basicSetup = current.getBasicSetupConfig();
        ShellConfig.ResourceImportSettings resourceImportSettings = new ShellConfig.ResourceImportSettings(
                imported,
                safe(source),
                safe(lineName),
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

    static StationResourceState getState(@NonNull Context context) {
        ShellConfig.ResourceImportSettings settings = ShellConfigRepository.get(context.getApplicationContext())
                .getBasicSetupConfig()
                .getResourceImportSettings();
        return new StationResourceState(
                settings.isStationResourceImported(),
                settings.getSource(),
                settings.getLineName(),
                settings.getUpdatedAt()
        );
    }

    private static String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    static final class StationResourceState {
        private final boolean imported;
        private final String source;
        private final String lineName;
        private final long updatedAt;

        StationResourceState(boolean imported, String source, String lineName, long updatedAt) {
            this.imported = imported;
            this.source = source == null ? "-" : source;
            this.lineName = lineName == null ? "-" : lineName;
            this.updatedAt = updatedAt;
        }

        boolean isImported() {
            return imported;
        }

        String getSource() {
            return source;
        }

        String getLineName() {
            return lineName;
        }

        long getUpdatedAt() {
            return updatedAt;
        }
    }
}
