package com.lhxy.istationdevice.android11.debugtools;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.debugtools.databinding.ActivityDebugToolsBinding;
import com.lhxy.istationdevice.android11.devicem90.M90IoMap;
import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.DeviceFoundationUseCase;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigLoader;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigValidator;
import com.lhxy.istationdevice.android11.domain.debug.DebugBundleExporter;
import com.lhxy.istationdevice.android11.domain.debug.TerminalSelfCheckUseCase;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalModuleHub;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 调试工具页
 * <p>
 * 这一页只做两件事：
 * 1. 直接操作底座能力，确认配置和设备通不通。
 * 2. 把现场联调需要的日志、自检、回放和导出入口集中在一起。
 */
public class DebugToolsActivity extends AppCompatActivity {
    private static final String TAG = "DebugToolsActivity";

    private final ProtocolReplayUseCase protocolReplayUseCase = new ProtocolReplayUseCase();
    private final DeviceFoundationUseCase deviceFoundationUseCase = new DeviceFoundationUseCase();
    private final TerminalSelfCheckUseCase terminalSelfCheckUseCase = new TerminalSelfCheckUseCase();
    private final ShellRuntime shellRuntime = ShellRuntime.get();
    private final GpsSerialMonitor gpsSerialMonitor = shellRuntime.getGpsSerialMonitor();
    private final Jt808SocketMonitor jt808SocketMonitor = shellRuntime.getJt808SocketMonitor();
    private final SerialPortAdapter serialPortAdapter = shellRuntime.getSerialPortAdapter();
    private final SocketClientAdapter socketClientAdapter = shellRuntime.getSocketClientAdapter();
    private final GpioAdapter gpioAdapter = shellRuntime.getGpioAdapter();
    private final CameraAdapter cameraAdapter = shellRuntime.getCameraAdapter();
    private final RfidAdapter rfidAdapter = shellRuntime.getRfidAdapter();
    private final SystemOps systemOps = shellRuntime.getSystemOps();
    private final TerminalModuleHub moduleHub = shellRuntime.getModuleHub();

    private ActivityDebugToolsBinding binding;
    private ShellConfig shellConfig;
    private final List<ChannelOption> serialOptions = new ArrayList<>();
    private final List<ChannelOption> socketOptions = new ArrayList<>();
    private final List<ChannelOption> gpioOptions = new ArrayList<>();
    private final List<ChannelOption> cameraOptions = new ArrayList<>();
    private final List<ChannelOption> moduleOptions = new ArrayList<>();
    private String latestModuleRunSummary = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDebugToolsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        shellConfig = ShellConfigRepository.get(this);
        shellRuntime.applyConfig(this, shellConfig);
        jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, TraceIds.next("debug-socket-monitor"));

        bindChannelSelectors();
        renderSummary();
        bindActions();

        AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "debug tools opened", TraceIds.next("debug-page"));
        renderStatus(getString(R.string.debug_status_idle));
        renderSelfCheck();
        renderDeviceStatus();
        renderGpsMonitorStatus();
        renderLogs();
    }

    /**
     * 绑定页面按钮事件。
     */
    private void bindActions() {
        binding.btnRefresh.setOnClickListener(v -> refreshConfigAndLogs());
        binding.btnResetRuntimeConfig.setOnClickListener(v -> resetRuntimeConfig());
        binding.btnClear.setOnClickListener(v -> clearLogs());
        binding.btnExportLogs.setOnClickListener(v -> exportLogs());
        binding.btnReplayDisplay.setOnClickListener(v -> replayDisplay());
        binding.btnReplayJt808.setOnClickListener(v -> replayJt808());
        binding.btnReplayAll.setOnClickListener(v -> replayAll());
        binding.btnRunSelfCheck.setOnClickListener(v -> runSelfCheck());
        binding.btnRunSelectedModule.setOnClickListener(v -> runSelectedModule());
        binding.btnRunAllModules.setOnClickListener(v -> runAllModules());
        binding.btnOpenSerial.setOnClickListener(v -> openSelectedSerial());
        binding.btnCloseSerial.setOnClickListener(v -> closeSelectedSerial());
        binding.btnConnectSocket.setOnClickListener(v -> connectSelectedSocket());
        binding.btnDisconnectSocket.setOnClickListener(v -> disconnectSelectedSocket());
        binding.btnReadGpio.setOnClickListener(v -> readSelectedGpio());
        binding.btnWriteGpioHigh.setOnClickListener(v -> writeSelectedGpio(1));
        binding.btnWriteGpioLow.setOnClickListener(v -> writeSelectedGpio(0));
        binding.btnOpenCamera.setOnClickListener(v -> openSelectedCamera());
        binding.btnCloseCamera.setOnClickListener(v -> closeSelectedCamera());
        binding.btnReadRfid.setOnClickListener(v -> readRfid());
        binding.btnSyncSystemTime.setOnClickListener(v -> syncSystemTime());
        binding.btnRequestReboot.setOnClickListener(v -> requestReboot());
        binding.btnBindGpsMonitor.setOnClickListener(v -> bindGpsMonitor());
        binding.btnUnbindGpsMonitor.setOnClickListener(v -> unbindGpsMonitor());
        binding.btnSendSerialHex.setOnClickListener(v -> sendSelectedSerialHex());
        binding.btnSendSocketHex.setOnClickListener(v -> sendSelectedSocketHex());
        binding.btnInspectJt808Hex.setOnClickListener(v -> inspectJt808Hex());
        binding.btnInspectGpsNmea.setOnClickListener(v -> inspectGpsNmea());
    }

    /**
     * 重新加载配置并刷新页面。
     */
    private void refreshConfigAndLogs() {
        shellConfig = ShellConfigRepository.reload(this);
        shellRuntime.applyConfig(this, shellConfig);
        jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, TraceIds.next("debug-socket-monitor"));
        bindChannelSelectors();
        renderSummary();
        renderStatus("状态：配置已重新加载。");
        renderSelfCheck();
        renderDeviceStatus();
        renderGpsMonitorStatus();
        renderModuleStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 把运行期配置重置回当前打包值。
     */
    private void resetRuntimeConfig() {
        String traceId = TraceIds.next("reset-config");
        try {
            java.io.File runtimeConfigFile = ShellConfigLoader.resetRuntimeConfig(this);
            shellConfig = ShellConfigRepository.reload(this);
            shellRuntime.applyConfig(this, shellConfig);
            jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, traceId + "-socket-monitor");
            bindChannelSelectors();
            renderSummary();
            AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    TAG,
                    "运行期配置已重置: " + runtimeConfigFile.getAbsolutePath(),
                    traceId
            );
            renderStatus("状态：运行期配置已重置到打包值。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "重置运行期配置失败: " + e.getMessage(), traceId);
            renderStatus("状态：重置配置失败，" + safeMessage(e));
        }
        renderSelfCheck();
        renderDeviceStatus();
        renderGpsMonitorStatus();
        renderModuleStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 清空当前日志。
     */
    private void clearLogs() {
        AppLogCenter.clear();
        renderStatus("状态：日志已清空。");
        renderLogs();
    }

    /**
     * 执行一次终端自检。
     */
    private void runSelfCheck() {
        String traceId = TraceIds.next("self-check");
        renderSelfCheck();
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "已执行终端自检", traceId);
        renderStatus("状态：终端自检已刷新。");
        renderLogs();
    }

    /**
     * 执行当前选中的业务模块样例。
     */
    private void runSelectedModule() {
        String traceId = TraceIds.next("run-module");
        ModuleRunResult result = moduleHub.runModule(resolveSelectedModuleKey(), traceId);
        latestModuleRunSummary = result.describeBlock();
        AppLogCenter.log(
                result.isSuccess() ? LogCategory.BIZ : LogCategory.ERROR,
                result.isSuccess() ? LogLevel.INFO : LogLevel.ERROR,
                TAG,
                result.describeInline(),
                traceId
        );
        renderStatus("状态：" + result.describeInline());
        renderSummary();
        renderSelfCheck();
        renderDeviceStatus();
        renderGpsMonitorStatus();
        renderModuleStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 执行全部业务模块样例。
     */
    private void runAllModules() {
        String traceId = TraceIds.next("run-all-modules");
        List<ModuleRunResult> results = moduleHub.runAll(traceId);
        latestModuleRunSummary = moduleHub.describeResults(results);
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "已执行全部业务模块样例", traceId);
        renderStatus("状态：全部业务模块样例已执行。");
        renderSummary();
        renderSelfCheck();
        renderDeviceStatus();
        renderGpsMonitorStatus();
        renderModuleStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 回放屏显协议样例。
     */
    private void replayDisplay() {
        String traceId = TraceIds.next("display-debug");
        int count = protocolReplayUseCase.replayDisplayDemo(serialPortAdapter, shellConfig, traceId);
        AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "调试页回放屏显样例 " + count + " 条", traceId);
        renderStatus("状态：已回放屏显样例 " + count + " 条。");
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 回放 808/AL808 样例。
     */
    private void replayJt808() {
        String traceId = TraceIds.next("jt808-debug");
        jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, traceId + "-socket-monitor");
        int count = protocolReplayUseCase.replayJt808Demo(socketClientAdapter, shellConfig, traceId);
        AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "调试页回放 808/AL808 样例 " + count + " 条", traceId);
        renderStatus("状态：已回放 808/AL808 样例 " + count + " 条。");
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 全量回放。
     */
    private void replayAll() {
        replayDisplay();
        replayJt808();
    }

    /**
     * 显式打开当前选中的串口。
     */
    private void openSelectedSerial() {
        String traceId = TraceIds.next("open-serial");
        try {
            ShellConfig.SerialChannel serialChannel = resolveSelectedSerialChannel();
            serialPortAdapter.open(serialChannel.toSerialPortConfig(), traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已打开串口 " + serialChannel.getKey(), traceId);
            renderStatus("状态：已打开串口 " + serialChannel.getKey() + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "打开串口失败: " + e.getMessage(), traceId);
            renderStatus("状态：打开串口失败，" + safeMessage(e));
        }
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 显式关闭当前选中的串口。
     */
    private void closeSelectedSerial() {
        String traceId = TraceIds.next("close-serial");
        try {
            ShellConfig.SerialChannel serialChannel = resolveSelectedSerialChannel();
            serialPortAdapter.close(serialChannel.getPortName(), traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已关闭串口 " + serialChannel.getKey(), traceId);
            renderStatus("状态：已关闭串口 " + serialChannel.getKey() + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "关闭串口失败: " + e.getMessage(), traceId);
            renderStatus("状态：关闭串口失败，" + safeMessage(e));
        }
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 显式连接当前选中的 Socket。
     */
    private void connectSelectedSocket() {
        String traceId = TraceIds.next("connect-socket");
        try {
            ShellConfig.SocketChannel socketChannel = resolveSelectedSocketChannel();
            socketClientAdapter.connect(socketChannel.toSocketEndpointConfig(), traceId);
            jt808SocketMonitor.attach(socketClientAdapter, socketChannel, traceId + "-monitor");
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已连接 Socket " + socketChannel.getKey(), traceId);
            renderStatus("状态：已连接 Socket " + socketChannel.getKey() + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "连接 Socket 失败: " + e.getMessage(), traceId);
            renderStatus("状态：连接 Socket 失败，" + safeMessage(e));
        }
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 显式断开当前选中的 Socket。
     */
    private void disconnectSelectedSocket() {
        String traceId = TraceIds.next("disconnect-socket");
        try {
            ShellConfig.SocketChannel socketChannel = resolveSelectedSocketChannel();
            socketClientAdapter.disconnect(socketChannel.getChannelName(), traceId);
            jt808SocketMonitor.detach(socketClientAdapter, socketChannel.getChannelName(), traceId + "-monitor");
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已断开 Socket " + socketChannel.getKey(), traceId);
            renderStatus("状态：已断开 Socket " + socketChannel.getKey() + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "断开 Socket 失败: " + e.getMessage(), traceId);
            renderStatus("状态：断开 Socket 失败，" + safeMessage(e));
        }
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 读取当前选中的 GPIO。
     */
    private void readSelectedGpio() {
        String traceId = TraceIds.next("read-gpio");
        try {
            ShellConfig.GpioPin gpioPin = resolveSelectedGpioPin();
            int value = deviceFoundationUseCase.readGpio(gpioAdapter, shellConfig, gpioPin.getKey(), traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已读取 GPIO " + gpioPin.getKey() + "=" + value, traceId);
            renderStatus("状态：GPIO " + gpioPin.getKey() + "=" + value + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "读取 GPIO 失败: " + e.getMessage(), traceId);
            renderStatus("状态：读取 GPIO 失败，" + safeMessage(e));
        }
        renderDeviceStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 写当前选中的 GPIO。
     */
    private void writeSelectedGpio(int value) {
        String traceId = TraceIds.next("write-gpio");
        try {
            ShellConfig.GpioPin gpioPin = resolveSelectedGpioPin();
            deviceFoundationUseCase.writeGpio(gpioAdapter, shellConfig, gpioPin.getKey(), value, traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已写 GPIO " + gpioPin.getKey() + "=" + value, traceId);
            renderStatus("状态：已写 GPIO " + gpioPin.getKey() + "=" + value + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "写 GPIO 失败: " + e.getMessage(), traceId);
            renderStatus("状态：写 GPIO 失败，" + safeMessage(e));
        }
        renderDeviceStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 打开当前选中的 Camera。
     */
    private void openSelectedCamera() {
        String traceId = TraceIds.next("open-camera");
        try {
            ShellConfig.CameraChannel cameraChannel = resolveSelectedCameraChannel();
            deviceFoundationUseCase.openCamera(cameraAdapter, shellConfig, cameraChannel.getKey(), traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已打开 Camera " + cameraChannel.getKey(), traceId);
            renderStatus("状态：已打开 Camera " + cameraChannel.getKey() + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "打开 Camera 失败: " + e.getMessage(), traceId);
            renderStatus("状态：打开 Camera 失败，" + safeMessage(e));
        }
        renderDeviceStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 关闭当前选中的 Camera。
     */
    private void closeSelectedCamera() {
        String traceId = TraceIds.next("close-camera");
        try {
            ShellConfig.CameraChannel cameraChannel = resolveSelectedCameraChannel();
            deviceFoundationUseCase.closeCamera(cameraAdapter, shellConfig, cameraChannel.getKey(), traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已关闭 Camera " + cameraChannel.getKey(), traceId);
            renderStatus("状态：已关闭 Camera " + cameraChannel.getKey() + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "关闭 Camera 失败: " + e.getMessage(), traceId);
            renderStatus("状态：关闭 Camera 失败，" + safeMessage(e));
        }
        renderDeviceStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 读取一次 RFID。
     */
    private void readRfid() {
        String traceId = TraceIds.next("read-rfid");
        try {
            String cardNo = deviceFoundationUseCase.readRfid(rfidAdapter, traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已读取 RFID 卡号 " + cardNo, traceId);
            renderStatus("状态：RFID 卡号 " + (cardNo == null || cardNo.trim().isEmpty() ? "-" : cardNo.trim()) + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "读取 RFID 失败: " + e.getMessage(), traceId);
            renderStatus("状态：读取 RFID 失败，" + safeMessage(e));
        }
        renderDeviceStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 把系统时间同步到当前时间。
     */
    private void syncSystemTime() {
        String traceId = TraceIds.next("sync-time");
        try {
            long timeMillis = deviceFoundationUseCase.syncSystemTimeNow(systemOps, traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已请求同步系统时间 " + timeMillis, traceId);
            renderStatus("状态：已请求同步系统时间。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "同步系统时间失败: " + e.getMessage(), traceId);
            renderStatus("状态：同步系统时间失败，" + safeMessage(e));
        }
        renderDeviceStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 请求系统重启。
     */
    private void requestReboot() {
        String traceId = TraceIds.next("request-reboot");
        try {
            deviceFoundationUseCase.requestReboot(systemOps, "debug-tools", traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.WARN, TAG, "已发出重启请求", traceId);
            renderStatus("状态：已发出重启请求。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "请求重启失败: " + e.getMessage(), traceId);
            renderStatus("状态：请求重启失败，" + safeMessage(e));
        }
        renderDeviceStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 绑定 GPS 串口监听。
     */
    private void bindGpsMonitor() {
        String traceId = TraceIds.next("bind-gps-monitor");
        try {
            ShellConfig.SerialChannel serialChannel = resolveGpsSerialChannel();
            if (!serialPortAdapter.isOpen(serialChannel.getPortName())) {
                serialPortAdapter.open(serialChannel.toSerialPortConfig(), traceId);
            }
            gpsSerialMonitor.attach(serialPortAdapter, serialChannel, traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已绑定 GPS 监听到串口 " + serialChannel.getKey(), traceId);
            renderStatus("状态：已绑定 GPS 监听到串口 " + serialChannel.getKey() + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "绑定 GPS 监听失败: " + e.getMessage(), traceId);
            renderStatus("状态：绑定 GPS 监听失败，" + safeMessage(e));
        }
        renderGpsMonitorStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 解绑 GPS 串口监听。
     */
    private void unbindGpsMonitor() {
        String traceId = TraceIds.next("unbind-gps-monitor");
        try {
            ShellConfig.SerialChannel serialChannel = resolveGpsSerialChannel();
            gpsSerialMonitor.detach(serialPortAdapter, serialChannel.getPortName(), traceId);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "已解绑 GPS 监听 " + serialChannel.getKey(), traceId);
            renderStatus("状态：已解绑 GPS 监听 " + serialChannel.getKey() + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "解绑 GPS 监听失败: " + e.getMessage(), traceId);
            renderStatus("状态：解绑 GPS 监听失败，" + safeMessage(e));
        }
        renderGpsMonitorStatus();
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 手工发送 HEX 到屏显串口。
     */
    private void sendSelectedSerialHex() {
        String traceId = TraceIds.next("manual-serial");
        try {
            ShellConfig.SerialChannel serialChannel = resolveSelectedSerialChannel();
            int length = protocolReplayUseCase.sendManualHexToSerial(
                    serialPortAdapter,
                    serialChannel,
                    readHexInput(),
                    traceId
            );
            AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    TAG,
                    "手工 HEX 已发到串口 " + serialChannel.getKey() + "，字节数=" + length,
                    traceId
            );
            renderStatus("状态：已发到串口 " + serialChannel.getKey() + "，字节数 " + length + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "发串口 HEX 失败: " + e.getMessage(), traceId);
            renderStatus("状态：发串口失败，" + safeMessage(e));
        }
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 手工发送 HEX 到当前选中的 Socket 通道。
     */
    private void sendSelectedSocketHex() {
        String traceId = TraceIds.next("manual-socket");
        try {
            ShellConfig.SocketChannel socketChannel = resolveSelectedSocketChannel();
            int length = protocolReplayUseCase.sendManualHexToSocket(
                    socketClientAdapter,
                    socketChannel,
                    readHexInput(),
                    traceId
            );
            AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    TAG,
                    "手工 HEX 已发到 Socket " + socketChannel.getKey() + "，字节数=" + length,
                    traceId
            );
            renderStatus("状态：已发到 Socket " + socketChannel.getKey() + "，字节数 " + length + "。");
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "发 Socket HEX 失败: " + e.getMessage(), traceId);
            renderStatus("状态：发 Socket 失败，" + safeMessage(e));
        }
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 解析当前输入的 JT808 / AL808 HEX。
     */
    private void inspectJt808Hex() {
        String traceId = TraceIds.next("inspect-jt808");
        try {
            String inspectText = protocolReplayUseCase.inspectJt808Hex(readHexInput());
            binding.tvInspectResult.setText(inspectText);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "已解析一段 808 HEX", traceId);
            renderStatus("状态：808 HEX 解析完成。");
        } catch (Exception e) {
            binding.tvInspectResult.setText("解析结果:\n- 解析失败\n- 原因: " + safeMessage(e));
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "解析 808 HEX 失败: " + e.getMessage(), traceId);
            renderStatus("状态：808 HEX 解析失败，" + safeMessage(e));
        }
        renderLogs();
    }

    /**
     * 解析当前输入的 GPS NMEA 文本。
     */
    private void inspectGpsNmea() {
        String traceId = TraceIds.next("inspect-gps");
        try {
            String inspectText = protocolReplayUseCase.inspectGpsNmea(readHexInput());
            binding.tvGpsInspectResult.setText(inspectText);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "已解析一段 GPS NMEA", traceId);
            renderStatus("状态：GPS NMEA 解析完成。");
        } catch (Exception e) {
            binding.tvGpsInspectResult.setText("GPS 解析结果:\n- 解析失败\n- 原因: " + safeMessage(e));
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "解析 GPS NMEA 失败: " + e.getMessage(), traceId);
            renderStatus("状态：GPS NMEA 解析失败，" + safeMessage(e));
        }
        renderLogs();
    }

    /**
     * 导出当前日志。
     */
    private void exportLogs() {
        String traceId = TraceIds.next("export-log");
        try {
            java.io.File exportFile = DebugBundleExporter.export(
                    this,
                    shellConfig,
                    gpsSerialMonitor,
                    jt808SocketMonitor,
                    shellRuntime.describeFoundationStatus(),
                    shellRuntime.describeModuleStatus()
            );
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "调试包已导出: " + exportFile.getAbsolutePath(), traceId);
            renderStatus("状态：调试包已导出到 " + exportFile.getAbsolutePath());
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "调试包导出失败: " + e.getMessage(), traceId);
            renderStatus("状态：调试包导出失败，" + safeMessage(e));
        }
        renderLogs();
        renderChannelStatus();
    }

    /**
     * 渲染配置摘要。
     */
    private void renderSummary() {
        binding.tvDebugSummary.setText(
                shellConfig.describe()
                        + "\n"
                        + ShellConfigValidator.describe(shellConfig)
                        + "\n"
                        + ShellConfigLoader.describeConfigLocations(this)
                + "\n"
                + protocolReplayUseCase.describeMockCatalogCompact()
                        + "\n"
                        + jt808SocketMonitor.describeStatus()
                        + "\n"
                        + shellRuntime.describeFoundationStatus()
                        + "\n"
                        + shellRuntime.describeModuleStatus()
                        + "\n"
                        + M90IoMap.describe()
        );
    }

    /**
     * 渲染状态文本。
     */
    private void renderStatus(String statusText) {
        binding.tvDebugStatus.setText(statusText);
    }

    /**
     * 渲染终端自检结果。
     */
    private void renderSelfCheck() {
        binding.tvSelfCheck.setText(
                terminalSelfCheckUseCase.buildReport(
                        this,
                        shellConfig,
                        shellRuntime.describeFoundationStatus(),
                        shellRuntime.describeModuleStatus()
                )
        );
    }

    /**
     * 渲染底座设备状态。
     */
    private void renderDeviceStatus() {
        binding.tvDeviceStatus.setText(shellRuntime.describeFoundationStatus());
    }

    /**
     * 渲染 GPS 监听状态。
     */
    private void renderGpsMonitorStatus() {
        binding.tvGpsMonitorStatus.setText(
                "GPS 默认通道: " + shellConfig.getDebugReplay().getGpsSerialKey()
                        + "\n"
                        + gpsSerialMonitor.describeStatus()
        );
    }

    /**
     * 渲染业务模块状态。
     */
    private void renderModuleStatus() {
        StringBuilder builder = new StringBuilder(moduleHub.describeDetails());
        if (latestModuleRunSummary != null && !latestModuleRunSummary.trim().isEmpty()) {
            builder.append("\n\n最近一次执行:")
                    .append("\n")
                    .append(latestModuleRunSummary.trim());
        }
        binding.tvModuleStatus.setText(builder.toString());
    }

    /**
     * 渲染当前选中通道的连接状态。
     */
    private void renderChannelStatus() {
        try {
            ShellConfig.SerialChannel serialChannel = resolveSelectedSerialChannel();
            ShellConfig.SerialChannel gpsChannel = resolveGpsSerialChannel();
            ShellConfig.SocketChannel socketChannel = resolveSelectedSocketChannel();
            ShellConfig.GpioPin gpioPin = resolveSelectedGpioPin();
            ShellConfig.CameraChannel cameraChannel = resolveSelectedCameraChannel();
            binding.tvChannelStatus.setText(
                    "通道状态:"
                            + "\n- 串口 " + serialChannel.getKey() + " -> "
                            + (serialPortAdapter.isOpen(serialChannel.getPortName()) ? "已打开" : "未打开")
                            + "\n- GPS 默认口 " + gpsChannel.getKey() + " -> "
                            + (serialPortAdapter.isOpen(gpsChannel.getPortName()) ? "已打开" : "未打开")
                            + "\n- GPS 监听 -> "
                            + (gpsSerialMonitor.isAttached() ? "已绑定" : "未绑定")
                            + "\n- Socket " + socketChannel.getKey() + " -> "
                            + (socketClientAdapter.isConnected(socketChannel.getChannelName()) ? "已连接" : "未连接")
                            + "\n- Socket 协议监听 -> "
                            + (jt808SocketMonitor.isAttached(socketChannel.getChannelName()) ? "已绑定" : "未绑定")
                            + "\n- GPIO 默认项 -> " + gpioPin.getKey()
                            + "\n- Camera 默认项 -> " + cameraChannel.getKey()
                            + "\n- RFID -> " + (rfidAdapter.isAvailable() ? "可读" : "未就绪")
                            + "\n- SystemOps -> silentInstall=" + systemOps.supportsSilentInstall()
            );
        } catch (Exception e) {
            binding.tvChannelStatus.setText("通道状态:\n- 当前配置还没准备好");
        }
    }

    /**
     * 渲染日志文本。
     */
    private void renderLogs() {
        String dump = AppLogCenter.dumpPlainText();
        binding.tvLogs.setText(dump.isEmpty() ? "还没有日志，先回首页写入一条示例日志。" : dump);
    }

    /**
     * 读取当前输入的 HEX 文本。
     */
    private String readHexInput() {
        return binding.etHexInput.getText() == null ? "" : binding.etHexInput.getText().toString().trim();
    }

    /**
     * 返回更稳的异常消息。
     */
    private String safeMessage(Exception exception) {
        return exception.getMessage() == null || exception.getMessage().trim().isEmpty()
                ? exception.getClass().getSimpleName()
                : exception.getMessage();
    }

    /**
     * 绑定当前配置下的串口、Socket、GPIO 和 Camera 选项。
     */
    private void bindChannelSelectors() {
        serialOptions.clear();
        socketOptions.clear();
        gpioOptions.clear();
        cameraOptions.clear();
        moduleOptions.clear();

        for (Map.Entry<String, ShellConfig.SerialChannel> entry : shellConfig.getSerialChannels().entrySet()) {
            ShellConfig.SerialChannel channel = entry.getValue();
            serialOptions.add(new ChannelOption(
                    channel.getKey(),
                    channel.getKey() + " / " + channel.getPortName() + " @" + channel.getBaudRate() + " [" + channel.getMode().toConfigValue() + "]" + appendNote(channel.getNote())
            ));
        }

        for (Map.Entry<String, ShellConfig.SocketChannel> entry : shellConfig.getSocketChannels().entrySet()) {
            ShellConfig.SocketChannel channel = entry.getValue();
            socketOptions.add(new ChannelOption(
                    channel.getKey(),
                    channel.getKey() + " / " + channel.getHost() + ":" + channel.getPort() + " [" + channel.getMode().toConfigValue() + "]" + appendNote(channel.getNote())
            ));
        }

        for (Map.Entry<String, ShellConfig.GpioPin> entry : shellConfig.getGpioConfig().getPins().entrySet()) {
            ShellConfig.GpioPin pin = entry.getValue();
            gpioOptions.add(new ChannelOption(
                    pin.getKey(),
                    pin.getKey() + " / " + pin.getPinId() + appendNote(pin.getNote())
            ));
        }

        for (Map.Entry<String, ShellConfig.CameraChannel> entry : shellConfig.getCameraConfig().getChannels().entrySet()) {
            ShellConfig.CameraChannel channel = entry.getValue();
            cameraOptions.add(new ChannelOption(
                    channel.getKey(),
                    channel.getKey() + " / cameraId=" + channel.getCameraId() + appendNote(channel.getNote())
            ));
        }

        for (TerminalBusinessModule module : moduleHub.getModules()) {
            moduleOptions.add(new ChannelOption(
                    module.getKey(),
                    module.getKey() + " / " + module.getTitle() + appendNote(module.describePurpose())
            ));
        }

        ArrayAdapter<ChannelOption> serialSpinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                serialOptions
        );
        serialSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSerialChannel.setAdapter(serialSpinnerAdapter);
        binding.spinnerSerialChannel.setOnItemSelectedListener(new SimpleItemSelectedListener(this::renderChannelStatus));

        ArrayAdapter<ChannelOption> socketSpinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                socketOptions
        );
        socketSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSocketChannel.setAdapter(socketSpinnerAdapter);
        binding.spinnerSocketChannel.setOnItemSelectedListener(new SimpleItemSelectedListener(this::renderChannelStatus));

        ArrayAdapter<ChannelOption> gpioSpinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                gpioOptions
        );
        gpioSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGpioPin.setAdapter(gpioSpinnerAdapter);
        binding.spinnerGpioPin.setOnItemSelectedListener(new SimpleItemSelectedListener(this::renderChannelStatus));

        ArrayAdapter<ChannelOption> cameraSpinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cameraOptions
        );
        cameraSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCameraChannel.setAdapter(cameraSpinnerAdapter);
        binding.spinnerCameraChannel.setOnItemSelectedListener(new SimpleItemSelectedListener(this::renderChannelStatus));

        ArrayAdapter<ChannelOption> moduleSpinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                moduleOptions
        );
        moduleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBusinessModule.setAdapter(moduleSpinnerAdapter);
        binding.spinnerBusinessModule.setOnItemSelectedListener(new SimpleItemSelectedListener(this::renderModuleStatus));

        selectSerialOption(shellConfig.getDebugReplay().getDisplaySerialKey());
        selectSocketOption(shellConfig.getDebugReplay().getJt808SocketKey());
        selectGpioOption(shellConfig.getDebugReplay().getGpioPinKey());
        selectCameraOption(shellConfig.getDebugReplay().getCameraChannelKey());
        selectModuleOption("dispatch");
        renderChannelStatus();
        renderModuleStatus();
    }

    /**
     * 选中串口默认项。
     */
    private void selectSerialOption(String key) {
        for (int index = 0; index < serialOptions.size(); index++) {
            if (serialOptions.get(index).getKey().equals(key)) {
                binding.spinnerSerialChannel.setSelection(index);
                return;
            }
        }
        if (!serialOptions.isEmpty()) {
            binding.spinnerSerialChannel.setSelection(0);
        }
    }

    /**
     * 选中 Socket 默认项。
     */
    private void selectSocketOption(String key) {
        for (int index = 0; index < socketOptions.size(); index++) {
            if (socketOptions.get(index).getKey().equals(key)) {
                binding.spinnerSocketChannel.setSelection(index);
                return;
            }
        }
        if (!socketOptions.isEmpty()) {
            binding.spinnerSocketChannel.setSelection(0);
        }
    }

    /**
     * 选中 GPIO 默认项。
     */
    private void selectGpioOption(String key) {
        for (int index = 0; index < gpioOptions.size(); index++) {
            if (gpioOptions.get(index).getKey().equals(key)) {
                binding.spinnerGpioPin.setSelection(index);
                return;
            }
        }
        if (!gpioOptions.isEmpty()) {
            binding.spinnerGpioPin.setSelection(0);
        }
    }

    /**
     * 选中 Camera 默认项。
     */
    private void selectCameraOption(String key) {
        for (int index = 0; index < cameraOptions.size(); index++) {
            if (cameraOptions.get(index).getKey().equals(key)) {
                binding.spinnerCameraChannel.setSelection(index);
                return;
            }
        }
        if (!cameraOptions.isEmpty()) {
            binding.spinnerCameraChannel.setSelection(0);
        }
    }

    /**
     * 选中默认模块。
     */
    private void selectModuleOption(String key) {
        for (int index = 0; index < moduleOptions.size(); index++) {
            if (moduleOptions.get(index).getKey().equals(key)) {
                binding.spinnerBusinessModule.setSelection(index);
                return;
            }
        }
        if (!moduleOptions.isEmpty()) {
            binding.spinnerBusinessModule.setSelection(0);
        }
    }

    /**
     * 解析当前选中的串口。
     */
    private ShellConfig.SerialChannel resolveSelectedSerialChannel() {
        Object selectedItem = binding.spinnerSerialChannel.getSelectedItem();
        if (!(selectedItem instanceof ChannelOption)) {
            throw new IllegalStateException("当前没有可用串口配置");
        }
        return shellConfig.requireSerialChannel(((ChannelOption) selectedItem).getKey());
    }

    /**
     * 解析当前配置里默认的 GPS 串口。
     */
    private ShellConfig.SerialChannel resolveGpsSerialChannel() {
        String gpsSerialKey = shellConfig.getDebugReplay().getGpsSerialKey();
        if (gpsSerialKey == null || gpsSerialKey.trim().isEmpty()) {
            return resolveSelectedSerialChannel();
        }
        return shellConfig.requireSerialChannel(gpsSerialKey);
    }

    /**
     * 解析当前选中的 Socket。
     */
    private ShellConfig.SocketChannel resolveSelectedSocketChannel() {
        Object selectedItem = binding.spinnerSocketChannel.getSelectedItem();
        if (!(selectedItem instanceof ChannelOption)) {
            throw new IllegalStateException("当前没有可用 Socket 配置");
        }
        return shellConfig.requireSocketChannel(((ChannelOption) selectedItem).getKey());
    }

    /**
     * 解析当前选中的 GPIO。
     */
    private ShellConfig.GpioPin resolveSelectedGpioPin() {
        Object selectedItem = binding.spinnerGpioPin.getSelectedItem();
        if (!(selectedItem instanceof ChannelOption)) {
            throw new IllegalStateException("当前没有可用 GPIO 配置");
        }
        return shellConfig.getGpioConfig().requirePin(((ChannelOption) selectedItem).getKey());
    }

    /**
     * 解析当前选中的 Camera。
     */
    private ShellConfig.CameraChannel resolveSelectedCameraChannel() {
        Object selectedItem = binding.spinnerCameraChannel.getSelectedItem();
        if (!(selectedItem instanceof ChannelOption)) {
            throw new IllegalStateException("当前没有可用 Camera 配置");
        }
        return shellConfig.getCameraConfig().requireChannel(((ChannelOption) selectedItem).getKey());
    }

    /**
     * 解析当前选中的业务模块。
     */
    private String resolveSelectedModuleKey() {
        Object selectedItem = binding.spinnerBusinessModule.getSelectedItem();
        if (!(selectedItem instanceof ChannelOption)) {
            throw new IllegalStateException("当前没有可用业务模块配置");
        }
        return ((ChannelOption) selectedItem).getKey();
    }

    /**
     * 备注展示统一补前缀。
     */
    private String appendNote(String note) {
        return note == null || note.trim().isEmpty() ? "" : " / " + note.trim();
    }

    /**
     * 调试页下拉项。
     */
    private static final class ChannelOption {
        private final String key;
        private final String label;

        private ChannelOption(String key, String label) {
            this.key = key;
            this.label = label;
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /**
     * 精简版下拉监听，只关心选中后刷新状态。
     */
    private static final class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        private final Runnable onSelected;

        private SimpleItemSelectedListener(Runnable onSelected) {
            this.onSelected = onSelected;
        }

        @Override
        public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
            onSelected.run();
        }

        @Override
        public void onNothingSelected(android.widget.AdapterView<?> parent) {
            onSelected.run();
        }
    }
}
