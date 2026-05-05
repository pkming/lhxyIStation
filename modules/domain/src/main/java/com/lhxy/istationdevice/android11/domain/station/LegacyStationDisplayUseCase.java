package com.lhxy.istationdevice.android11.domain.station;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.protocol.legacy.BusLineSnapshot;
import com.lhxy.istationdevice.android11.protocol.legacy.DisplayLanguage;
import com.lhxy.istationdevice.android11.protocol.legacy.DisplayProtocolGenerator;
import com.lhxy.istationdevice.android11.protocol.legacy.HengWuDisplayProtocol;
import com.lhxy.istationdevice.android11.protocol.legacy.LhxyDisplayProtocol;
import com.lhxy.istationdevice.android11.protocol.legacy.ProtocolBatchResult;
import com.lhxy.istationdevice.android11.protocol.legacy.TongDaDisplayProtocol;

import java.util.List;

/**
 * 旧版 RS485 报站屏显派发桥。
 */
public final class LegacyStationDisplayUseCase {
    private final SerialPortAdapter serialPortAdapter;
    private final LegacyStationSnapshotFactory snapshotFactory = new LegacyStationSnapshotFactory();
    private final TongDaDisplayProtocol tongDaProtocol = new TongDaDisplayProtocol();
    private final LhxyDisplayProtocol lhxyDisplayProtocol = new LhxyDisplayProtocol();
    private final HengWuDisplayProtocol hengWuDisplayProtocol = new HengWuDisplayProtocol();

    public LegacyStationDisplayUseCase(SerialPortAdapter serialPortAdapter) {
        this.serialPortAdapter = serialPortAdapter;
    }

    public void syncRoute(ShellConfig shellConfig, LegacyGpsRouteResource route, StationState stationState, String traceId) {
        if (shellConfig == null || route == null) {
            return;
        }
        DisplayProtocolGenerator generator = selectGenerator(shellConfig, traceId);
        if (generator == null) {
            return;
        }
        ShellConfig.SerialChannel displayChannel = ensureReady(shellConfig, traceId + "-display-open");
        DisplayLanguage language = resolveLanguage(shellConfig);
        BusLineSnapshot currentSnapshot = snapshotFactory.createCurrentSnapshot(route, stationState);
        send(displayChannel, generator.createLineName(currentSnapshot, language), protocolName(shellConfig) + "_LINE_NAME", traceId + "-line");

        List<BusLineSnapshot> routeSnapshots = snapshotFactory.createRouteSnapshots(route, stationState);
        if (!routeSnapshots.isEmpty()) {
            ProtocolBatchResult batchResult = generator.createSiteInfo(routeSnapshots);
            if (batchResult != null) {
                send(displayChannel, batchResult.getPayload(), protocolName(shellConfig) + "_SITE_INFO", traceId + "-site-info");
            }
        }
    }

    public void sendCurrentStation(ShellConfig shellConfig, LegacyGpsRouteResource route, StationState stationState, String traceId) {
        if (shellConfig == null || route == null || stationState == null) {
            return;
        }
        DisplayProtocolGenerator generator = selectGenerator(shellConfig, traceId);
        if (generator == null) {
            return;
        }
        BusLineSnapshot snapshot = snapshotFactory.createCurrentSnapshot(route, stationState);
        if (!shouldSendStation(protocolName(shellConfig), snapshot)) {
            return;
        }
        ShellConfig.SerialChannel displayChannel = ensureReady(shellConfig, traceId + "-display-open");
        DisplayLanguage language = resolveLanguage(shellConfig);
        send(displayChannel, generator.createNewspaperStation(snapshot, language), protocolName(shellConfig) + "_STATION", traceId + "-station");
        send(displayChannel, generator.createInternalScreen(snapshot, language), protocolName(shellConfig) + "_INTERNAL", traceId + "-internal");
    }

    public boolean sendServiceTone(ShellConfig shellConfig, int serviceNo, String traceId) {
        if (shellConfig == null || serviceNo < 0 || serviceNo > 9) {
            return false;
        }
        DisplayProtocolGenerator generator = selectGenerator(shellConfig, traceId);
        if (generator == null) {
            return false;
        }
        byte[] payload = generator.createServiceTone((byte) (0x10 + serviceNo));
        if (payload == null || payload.length == 0) {
            return false;
        }
        ShellConfig.SerialChannel displayChannel = ensureReady(shellConfig, traceId + "-display-open");
        send(displayChannel, payload, protocolName(shellConfig) + "_SERVICE_TONE", traceId + "-service-tone");
        return true;
    }

    private DisplayProtocolGenerator selectGenerator(ShellConfig shellConfig, String traceId) {
        String protocolName = protocolName(shellConfig);
        if ("通达".equals(protocolName)) {
            return tongDaProtocol;
        }
        if ("LHXY".equals(protocolName)) {
            return lhxyDisplayProtocol;
        }
        if ("恒舞".equals(protocolName) || "武汉乐的".equals(protocolName)) {
            return hengWuDisplayProtocol;
        }
        AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, "LegacyStationDisplay", "当前 RS485 报站协议暂未接入: " + protocolName, traceId);
        return null;
    }

    private boolean shouldSendStation(String protocolName, BusLineSnapshot snapshot) {
        if (snapshot == null) {
            return false;
        }
        if ("LHXY".equals(protocolName)) {
            return !(snapshot.getBusNo() == 0 && snapshot.getStationType() == 0);
        }
        if ("恒舞".equals(protocolName) || "武汉乐的".equals(protocolName)) {
            return (snapshot.getBusNo() == 0 && snapshot.getStationType() == 1) || snapshot.getBusNo() > 0;
        }
        return true;
    }

    private DisplayLanguage resolveLanguage(ShellConfig shellConfig) {
        return shellConfig.getBasicSetupConfig().getNewspaperSettings().isEnglishEnabled()
                ? DisplayLanguage.ENGLISH
                : DisplayLanguage.SIMPLIFIED_CHINESE;
    }

    private String protocolName(ShellConfig shellConfig) {
        return shellConfig.getBasicSetupConfig().getSerialSettings().getRs485Protocol();
    }

    private ShellConfig.SerialChannel ensureReady(ShellConfig shellConfig, String traceId) {
        ShellConfig.SerialChannel displayChannel = shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getDisplaySerialKey());
        if (!serialPortAdapter.isOpen(displayChannel.getPortName())) {
            serialPortAdapter.open(displayChannel.toSerialPortConfig(), traceId);
        }
        return displayChannel;
    }

    private void send(ShellConfig.SerialChannel channel, byte[] payload, String label, String traceId) {
        if (payload == null || payload.length == 0) {
            return;
        }
        AppLogCenter.log(LogCategory.PROTOCOL_TX, LogLevel.DEBUG, "LegacyStationDisplay", label + " via " + channel.getKey() + " -> " + Hexs.toHex(payload), traceId);
        serialPortAdapter.send(channel.getPortName(), payload, traceId);
    }
}