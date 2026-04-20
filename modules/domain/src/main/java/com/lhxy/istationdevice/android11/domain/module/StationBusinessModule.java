package com.lhxy.istationdevice.android11.domain.module;

import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;

/**
 * 报站模块
 * <p>
 * 先承接屏显样例、GPS 默认口和后续报站状态机的入口。
 */
public final class StationBusinessModule extends AbstractTerminalBusinessModule {
    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SerialPortAdapter serialPortAdapter;
    private final GpsSerialMonitor gpsSerialMonitor;
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private final StationState stationState = new StationState();

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
        return "承接 RS485 屏显、GPS 串口监听和后续报站状态机。";
    }

    public StationState getStationState() {
        return stationState;
    }

    @Override
    public String describeStatus() {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel displayChannel = shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getDisplaySerialKey());
            ShellConfig.SerialChannel gpsChannel = shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            return "默认屏显口=" + displayChannel.getKey()
                    + "，GPS 口=" + gpsChannel.getKey()
                    + "\n- 屏显串口 -> " + (serialPortAdapter.isOpen(displayChannel.getPortName()) ? "已打开" : "未打开")
                    + "\n- GPS 串口 -> " + (serialPortAdapter.isOpen(gpsChannel.getPortName()) ? "已打开" : "未打开")
                    + "\n- GPS 监听 -> " + (gpsSerialMonitor.isAttached() ? "已绑定" : "未绑定")
                    + "\n- " + stationState.describe()
                    + "\n- " + describeActionMemory();
        } catch (Exception e) {
            return "当前还没拿到完整报站配置: " + emptyAsDash(e.getMessage());
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
            ShellConfig.SerialChannel gpsChannel = shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            int count = replayDisplay ? protocolReplayUseCase.replayStationDemo(serialPortAdapter, shellConfig, traceId) : 0;
            if (!serialPortAdapter.isOpen(gpsChannel.getPortName())) {
                serialPortAdapter.open(gpsChannel.toSerialPortConfig(), traceId + "-gps-open");
            }
            gpsSerialMonitor.attach(serialPortAdapter, gpsChannel, traceId + "-gps-monitor");
            stationState.bindGps(gpsChannel.getKey());
            stationState.updateGps(gpsSerialMonitor.getLatestSnapshot());
            if (replayDisplay) {
                stationState.advanceStation();
            }
            sendSerialDispatchFramesIfNeeded(traceId + "-bind-gps");
            return success(
                    replayDisplay ? "已回放报站样例 " + count + " 条" : "已重新绑定 GPS 监听",
                    "GPS 默认监听已绑定到 " + gpsChannel.getKey()
            );
        } catch (Exception e) {
            return failure("报站样例执行失败", e);
        }
    }

    private ModuleRunResult advanceStation(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel gpsChannel = shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            if (!serialPortAdapter.isOpen(gpsChannel.getPortName())) {
                serialPortAdapter.open(gpsChannel.toSerialPortConfig(), traceId + "-gps-open");
            }
            if (!gpsSerialMonitor.isAttached()) {
                gpsSerialMonitor.attach(serialPortAdapter, gpsChannel, traceId + "-gps-monitor");
            }
            stationState.bindGps(gpsChannel.getKey());
            stationState.updateGps(gpsSerialMonitor.getLatestSnapshot());
            stationState.advanceStation();
                sendSerialDispatchFramesIfNeeded(traceId + "-advance-station");
            return success("已推进一站", "当前站点已更新到 " + stationState.getCurrentStation());
        } catch (Exception e) {
            return failure("推进站点失败", e);
        }
    }

    private ModuleRunResult repeatStation(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.SerialChannel gpsChannel = shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            if (!serialPortAdapter.isOpen(gpsChannel.getPortName())) {
                serialPortAdapter.open(gpsChannel.toSerialPortConfig(), traceId + "-gps-open");
            }
            if (!gpsSerialMonitor.isAttached()) {
                gpsSerialMonitor.attach(serialPortAdapter, gpsChannel, traceId + "-gps-monitor");
            }
            stationState.bindGps(gpsChannel.getKey());
            stationState.updateGps(gpsSerialMonitor.getLatestSnapshot());
            stationState.repeatCurrentStation();
            sendSerialDispatchFramesIfNeeded(traceId + "-repeat-station");
            return success("已重复播报当前站", "当前站点 " + stationState.getCurrentStation());
        } catch (Exception e) {
            return failure("重复报站失败", e);
        }
    }

    private ModuleRunResult stopStation() {
        stationState.stopReport();
        return success("已停止报站", "当前报站状态已切到停止");
    }

    private void sendSerialDispatchFramesIfNeeded(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig)) {
                return;
            }
            dvrSerialDispatchUseCase.sendGpsReport(shellConfig, stationState, gpsSerialMonitor.getLatestSnapshot(), traceId + "-gps");
            dvrSerialDispatchUseCase.sendSiteInfo(shellConfig, stationState, gpsSerialMonitor.getLatestSnapshot(), traceId + "-site");
        } catch (Exception ignore) {
            // 串口调度补发失败不阻断站点状态机推进。
        }
    }
}
