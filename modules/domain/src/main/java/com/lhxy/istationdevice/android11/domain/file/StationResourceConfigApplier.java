package com.lhxy.istationdevice.android11.domain.file;

import android.content.Context;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Applies M90 SourceFile/config.csv values that should affect runtime setup.
 */
public final class StationResourceConfigApplier {
    private StationResourceConfigApplier() {
    }

    public static ShellConfig applyImportResult(Context context, ShellConfig current, StationResourceArchiveUseCase.OperationResult result) {
        return applyImportResult(context, current, result, null, null, null);
    }

    public static ShellConfig applyImportResult(
            Context context,
            ShellConfig current,
            StationResourceArchiveUseCase.OperationResult result,
            String preferredLineName,
            String preferredDirectionText,
            String preferredLineAttribute
    ) {
        if (current == null || result == null) {
            return current;
        }
        ShellConfig.BasicSetupConfig basicSetup = current.getBasicSetupConfig();
        StationResourceArchiveUseCase.ResourceConfigOverrides overrides = result.getConfigOverrides();
        ShellConfig.SerialSettings importedSerialSettings =
                buildSerialSettingsWithResourceOverrides(basicSetup.getSerialSettings(), overrides);
        return new ShellConfig(
                current.getDeviceProfile(),
                current.getConfigVersion(),
                context == null ? "runtime:resource-import" : "runtime:" + ShellConfigRepository.getRuntimeConfigFile(context).getAbsolutePath(),
                buildSerialChannelsWithResourceOverrides(current.getSerialChannels(), overrides),
                current.getSocketChannels(),
                current.getGpioConfig(),
                current.getCameraConfig(),
                current.getRfidConfig(),
                current.getSystemConfig(),
                current.getLocationConfig(),
                current.getCanConfig(),
                current.getKeyboardConfig(),
                current.getDebugReplay(),
                new ShellConfig.BasicSetupConfig(
                        basicSetup.getNewspaperSettings(),
                        basicSetup.getNetworkSettings(),
                        importedSerialSettings,
                        basicSetup.getTtsSettings(),
                        buildLanguageSettingsWithResourceOverrides(basicSetup.getLanguageSettings(), overrides),
                        basicSetup.getOtherSettings(),
                        basicSetup.getWirelessSettings(),
                        new ShellConfig.ResourceImportSettings(
                                true,
                                result.getArchiveFile() == null ? "-" : result.getArchiveFile().getAbsolutePath(),
                                overrideOrCurrent(preferredLineName, result.getLineName()),
                                overrideOrCurrent(preferredDirectionText, "-"),
                                overrideOrCurrent(preferredLineAttribute, "-"),
                                System.currentTimeMillis()
                        ),
                        buildProtocolLinkageWithResourceOverrides(
                                basicSetup.getProtocolLinkageSettings(),
                                importedSerialSettings,
                                overrides
                        )
                )
        );
    }

    public static String describeAppliedConfig(ShellConfig shellConfig) {
        if (shellConfig == null) {
            return "-";
        }
        ShellConfig.BasicSetupConfig basicSetup = shellConfig.getBasicSetupConfig();
        ShellConfig.SerialSettings serialSettings = basicSetup.getSerialSettings();
        return "serialProtocols="
                + "RS232-1/" + serialSettings.getRs2321Protocol()
                + ", RS232-2/" + serialSettings.getRs2322Protocol()
                + ", RS485-1/" + serialSettings.getRs485Protocol()
                + ", RS485-2/" + serialSettings.getRs4852Protocol()
                + " / serialBaud="
                + describeSerialChannel(shellConfig, "rs232_1")
                + ", " + describeSerialChannel(shellConfig, "rs232_2")
                + ", " + describeSerialChannel(shellConfig, "rs485_1")
                + ", " + describeSerialChannel(shellConfig, "rs485_2")
                + " / language=" + basicSetup.getLanguageSettings().getLanguageCode()
                + " / resourceLine=" + basicSetup.getResourceImportSettings().getLineName();
    }

    private static String describeSerialChannel(ShellConfig shellConfig, String key) {
        ShellConfig.SerialChannel channel = shellConfig.getSerialChannels().get(key);
        if (channel == null) {
            return key + "/-";
        }
        return key + "/" + channel.getPortName() + "@" + channel.getBaudRate();
    }

    private static Map<String, ShellConfig.SerialChannel> buildSerialChannelsWithResourceOverrides(
            Map<String, ShellConfig.SerialChannel> current,
            StationResourceArchiveUseCase.ResourceConfigOverrides overrides
    ) {
        LinkedHashMap<String, ShellConfig.SerialChannel> updated = new LinkedHashMap<>();
        if (current != null) {
            updated.putAll(current);
        }
        replaceBaud(updated, "rs232_1", overrides.getRs2321Baud());
        replaceBaud(updated, "rs232_2", overrides.getRs2322Baud());
        replaceBaud(updated, "rs485_1", overrides.getRs485Baud());
        replaceBaud(updated, "rs485_2", overrides.getRs4852Baud());
        return updated;
    }

    private static void replaceBaud(Map<String, ShellConfig.SerialChannel> channels, String key, Integer baudRate) {
        if (channels == null || baudRate == null || baudRate <= 0) {
            return;
        }
        ShellConfig.SerialChannel channel = channels.get(key);
        if (channel == null) {
            return;
        }
        channels.put(key, new ShellConfig.SerialChannel(
                channel.getKey(),
                channel.getPortName(),
                baudRate,
                channel.getMode(),
                channel.getNote()
        ));
    }

    private static ShellConfig.SerialSettings buildSerialSettingsWithResourceOverrides(
            ShellConfig.SerialSettings current,
            StationResourceArchiveUseCase.ResourceConfigOverrides overrides
    ) {
        if (overrides == null || !overrides.hasAny()) {
            return current;
        }
        return new ShellConfig.SerialSettings(
                overrideOrCurrent(overrides.getRs2321Protocol(), current.getRs2321Protocol()),
                overrideOrCurrent(overrides.getRs2322Protocol(), current.getRs2322Protocol()),
                overrideOrCurrent(overrides.getRs485Protocol(), current.getRs485Protocol()),
                overrideOrCurrent(overrides.getRs4852Protocol(), current.getRs4852Protocol())
        );
    }

    private static ShellConfig.ProtocolLinkageSettings buildProtocolLinkageWithResourceOverrides(
            ShellConfig.ProtocolLinkageSettings current,
            ShellConfig.SerialSettings serialSettings,
            StationResourceArchiveUseCase.ResourceConfigOverrides overrides
    ) {
        if (overrides == null || overrides.getRs2321Protocol() == null) {
            return current;
        }
        String dispatchOwner = "无".equals(serialSettings.getRs2321Protocol())
                ? ShellConfig.ProtocolLinkageSettings.DISPATCH_OWNER_NETWORK
                : ShellConfig.ProtocolLinkageSettings.DISPATCH_OWNER_SERIAL_RS2321;
        return new ShellConfig.ProtocolLinkageSettings(dispatchOwner, System.currentTimeMillis());
    }

    private static ShellConfig.LanguageSettings buildLanguageSettingsWithResourceOverrides(
            ShellConfig.LanguageSettings current,
            StationResourceArchiveUseCase.ResourceConfigOverrides overrides
    ) {
        if (overrides == null || overrides.getLanguageCode() == null) {
            return current;
        }
        return new ShellConfig.LanguageSettings(overrides.getLanguageCode());
    }

    private static String overrideOrCurrent(String override, String current) {
        return override == null || override.trim().isEmpty() ? current : override.trim();
    }
}
