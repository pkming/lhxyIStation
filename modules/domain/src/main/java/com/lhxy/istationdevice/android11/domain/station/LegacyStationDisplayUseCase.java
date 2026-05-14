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
import com.lhxy.istationdevice.android11.protocol.legacy.LedGuideDisplayProtocol;
import com.lhxy.istationdevice.android11.protocol.legacy.LhxyDisplayProtocol;
import com.lhxy.istationdevice.android11.protocol.legacy.ProtocolBatchResult;
import com.lhxy.istationdevice.android11.protocol.legacy.TongDaDisplayProtocol;

import java.util.ArrayList;
import java.util.List;

/**
 * 旧版 RS485 报站屏显派发桥。
 * <p>
 * 负责把线路、站点和服务音转换成具体屏显协议帧并发送到默认显示串口。
 * <p>
 * 查找关键字：RS485 屏显、屏显协议、线路同步、当前站发送、服务音帧。
 */
public final class LegacyStationDisplayUseCase {
    private final SerialPortAdapter serialPortAdapter;
    private final LegacyStationSnapshotFactory snapshotFactory = new LegacyStationSnapshotFactory();
    private final TongDaDisplayProtocol tongDaProtocol = new TongDaDisplayProtocol();
    private final LhxyDisplayProtocol lhxyDisplayProtocol = new LhxyDisplayProtocol();
    private final HengWuDisplayProtocol hengWuDisplayProtocol = new HengWuDisplayProtocol();
    private final LedGuideDisplayProtocol ledGuideDisplayProtocol = new LedGuideDisplayProtocol();

    public LegacyStationDisplayUseCase(SerialPortAdapter serialPortAdapter) {
        this.serialPortAdapter = serialPortAdapter;
    }

    /**
     * 同步整条线路到报站屏。
     */
    public void syncRoute(ShellConfig shellConfig, LegacyGpsRouteResource route, StationState stationState, String traceId) {
        if (shellConfig == null || route == null) {
            return;
        }
        List<DisplayTarget> targets = resolveDisplayTargets(shellConfig, traceId);
        if (targets.isEmpty()) {
            return;
        }
        DisplayLanguage language = resolveLanguage(shellConfig);
        BusLineSnapshot currentSnapshot = snapshotFactory.createCurrentSnapshot(route, stationState);
        List<BusLineSnapshot> routeSnapshots = snapshotFactory.createRouteSnapshots(route, stationState);

        for (DisplayTarget target : targets) {
            DisplayProtocolGenerator generator = selectGenerator(target.protocolName, traceId);
            if (generator == null) {
                continue;
            }
            ShellConfig.SerialChannel displayChannel = ensureReady(shellConfig, target.serialKey, traceId + "-display-open-" + target.serialKey);
            send(displayChannel, generator.createLineState(currentSnapshot), target.protocolName + "_LINE_STATE", traceId + "-line-state-" + target.serialKey);
            send(displayChannel, generator.createLineName(currentSnapshot, language), target.protocolName + "_LINE_NAME", traceId + "-line-" + target.serialKey);
            if (routeSnapshots.isEmpty()) {
                continue;
            }
            ProtocolBatchResult batchResult = generator.createSiteInfo(routeSnapshots);
            if (batchResult != null) {
                send(displayChannel, batchResult.getPayload(), target.protocolName + "_SITE_INFO", traceId + "-site-info-" + target.serialKey);
            }
        }
    }

    /**
     * 发送当前站屏显内容。
     */
    public void sendCurrentStation(ShellConfig shellConfig, LegacyGpsRouteResource route, StationState stationState, String traceId) {
        if (shellConfig == null || route == null || stationState == null) {
            return;
        }
        List<DisplayTarget> targets = resolveDisplayTargets(shellConfig, traceId);
        if (targets.isEmpty()) {
            return;
        }
        BusLineSnapshot snapshot = snapshotFactory.createCurrentSnapshot(route, stationState);
        DisplayLanguage language = resolveLanguage(shellConfig);
        for (DisplayTarget target : targets) {
            DisplayProtocolGenerator generator = selectGenerator(target.protocolName, traceId);
            if (generator == null || !shouldSendStation(target.protocolName, snapshot)) {
                continue;
            }
            ShellConfig.SerialChannel displayChannel = ensureReady(shellConfig, target.serialKey, traceId + "-display-open-" + target.serialKey);
            send(displayChannel, generator.createNewspaperStation(snapshot, language), target.protocolName + "_STATION", traceId + "-station-" + target.serialKey);
            send(displayChannel, generator.createInternalScreen(snapshot, language), target.protocolName + "_INTERNAL", traceId + "-internal-" + target.serialKey);
        }
    }

    /**
     * 发送 RS485 服务音帧。
     * <p>
     * 是否真正有帧由具体协议生成器决定，通达/恒舞返回空帧时这里会直接跳过。
     */
    public boolean sendServiceTone(ShellConfig shellConfig, int serviceNo, String traceId) {
        if (shellConfig == null || serviceNo < 0 || serviceNo > 9) {
            return false;
        }
        boolean sent = false;
        for (DisplayTarget target : resolveDisplayTargets(shellConfig, traceId)) {
            DisplayProtocolGenerator generator = selectGenerator(target.protocolName, traceId);
            if (generator == null) {
                continue;
            }
            byte[] payload = generator.createServiceTone((byte) (0x10 + serviceNo));
            if (payload == null || payload.length == 0) {
                continue;
            }
            ShellConfig.SerialChannel displayChannel = ensureReady(shellConfig, target.serialKey, traceId + "-display-open-" + target.serialKey);
            send(displayChannel, payload, target.protocolName + "_SERVICE_TONE", traceId + "-service-tone-" + target.serialKey);
            sent = true;
        }
        return sent;
    }

    /**
     * 发送调度中心外设机务广告帧。
     * <p>
     * 当前仅对齐 M90 已有的通达/TD 系列协议；其它协议直接跳过。
     */
    public boolean sendLedAdvertisement(ShellConfig shellConfig, List<String> messages, String traceId) {
        if (shellConfig == null || messages == null || messages.isEmpty()) {
            return false;
        }
        boolean sent = false;
        for (DisplayTarget target : resolveDisplayTargets(shellConfig, traceId)) {
            if (!supportsLedAdvertisement(target.protocolName)) {
                continue;
            }
            byte[] payload = tongDaProtocol.createLedAdvInfo(messages);
            if (payload == null || payload.length == 0) {
                continue;
            }
            ShellConfig.SerialChannel displayChannel = ensureReady(shellConfig, target.serialKey, traceId + "-display-open-" + target.serialKey);
            send(displayChannel, payload, target.protocolName + "_LED_ADV", traceId + "-led-adv-" + target.serialKey);
            sent = true;
        }
        return sent;
    }

    /**
     * 选择当前 RS485 屏显协议生成器。
     */
    private DisplayProtocolGenerator selectGenerator(String protocolName, String traceId) {
        if ("通达".equals(protocolName) || "TD".equalsIgnoreCase(protocolName) || "LHXY-TD-LED2".equalsIgnoreCase(protocolName)) {
            return tongDaProtocol;
        }
        if ("LHXY".equals(protocolName)) {
            return lhxyDisplayProtocol;
        }
        if ("恒舞".equals(protocolName) || "武汉乐的".equals(protocolName)) {
            return hengWuDisplayProtocol;
        }
        if ("LED导程牌".equals(protocolName)) {
            return ledGuideDisplayProtocol;
        }
        AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, "LegacyStationDisplay", "当前 RS485 报站协议暂未接入: " + protocolName, traceId);
        return null;
    }

    /**
     * 按协议差异决定当前站是否应该下发。
     */
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

    private boolean supportsLedAdvertisement(String protocolName) {
        return "通达".equals(protocolName)
                || "TD".equalsIgnoreCase(protocolName)
                || "LHXY-TD-LED2".equalsIgnoreCase(protocolName);
    }

    private DisplayLanguage resolveLanguage(ShellConfig shellConfig) {
        return shellConfig.getBasicSetupConfig().getNewspaperSettings().isEnglishEnabled()
                ? DisplayLanguage.ENGLISH
                : DisplayLanguage.SIMPLIFIED_CHINESE;
    }

    private List<DisplayTarget> resolveDisplayTargets(ShellConfig shellConfig, String traceId) {
        List<DisplayTarget> targets = new ArrayList<>();
        if (shellConfig == null || shellConfig.getBasicSetupConfig() == null || shellConfig.getBasicSetupConfig().getSerialSettings() == null) {
            return targets;
        }
        ShellConfig.SerialSettings settings = shellConfig.getBasicSetupConfig().getSerialSettings();
        String primarySerialKey = shellConfig.getDebugReplay() == null ? "rs485_1" : shellConfig.getDebugReplay().getDisplaySerialKey();
        addDisplayTarget(targets, shellConfig, primarySerialKey, settings.getRs485Protocol(), traceId);
        addDisplayTarget(targets, shellConfig, "rs485_2", settings.getRs4852Protocol(), traceId);
        return targets;
    }

    private void addDisplayTarget(List<DisplayTarget> targets, ShellConfig shellConfig, String serialKey, String protocolName, String traceId) {
        if (isEmptyProtocol(protocolName) || serialKey == null || serialKey.trim().isEmpty()) {
            return;
        }
        if ("JHY".equalsIgnoreCase(protocolName.trim())) {
            return;
        }
        try {
            shellConfig.requireSerialChannel(serialKey);
            targets.add(new DisplayTarget(serialKey, protocolName.trim()));
        } catch (IllegalArgumentException e) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, "LegacyStationDisplay", "屏显串口未配置: " + serialKey + " / " + e.getMessage(), traceId);
        }
    }

    private boolean isEmptyProtocol(String protocolName) {
        return protocolName == null || protocolName.trim().isEmpty() || "无".equals(protocolName.trim());
    }

    /**
     * 确保默认显示串口已经打开。
     */
    private ShellConfig.SerialChannel ensureReady(ShellConfig shellConfig, String serialKey, String traceId) {
        ShellConfig.SerialChannel displayChannel = shellConfig.requireSerialChannel(serialKey);
        if (!serialPortAdapter.isOpen(displayChannel.getPortName())) {
            serialPortAdapter.open(displayChannel.toSerialPortConfig(), traceId);
        }
        return displayChannel;
    }

    /**
     * 统一输出屏显协议日志并发送串口数据。
     */
    private void send(ShellConfig.SerialChannel channel, byte[] payload, String label, String traceId) {
        if (payload == null || payload.length == 0) {
            return;
        }
        AppLogCenter.log(LogCategory.PROTOCOL_TX, LogLevel.DEBUG, "LegacyStationDisplay", label + " via " + channel.getKey() + " -> " + Hexs.toHex(payload), traceId);
        serialPortAdapter.send(channel.getPortName(), payload, traceId);
    }

    private static final class DisplayTarget {
        private final String serialKey;
        private final String protocolName;

        private DisplayTarget(String serialKey, String protocolName) {
            this.serialKey = serialKey;
            this.protocolName = protocolName;
        }
    }
}