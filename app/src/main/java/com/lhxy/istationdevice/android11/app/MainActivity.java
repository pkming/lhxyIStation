package com.lhxy.istationdevice.android11.app;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lhxy.istationdevice.android11.app.databinding.ActivityMainBinding;
import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.debugtools.DebugIntentFactory;
import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigValidator;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.module.DispatchBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.SignInBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.StationBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalModuleHub;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 正式业务首页。
 * <p>
 * 这页开始从“开发态摘要页”切到“终端业务首页”：
 * 上面看运营状态，下面放业务动作，调试入口只在 debug 环境保留。
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ShellMainActivity";

    private final ShellRuntime shellRuntime = ShellRuntime.get();
    private final SerialPortAdapter serialPortAdapter = shellRuntime.getSerialPortAdapter();
    private final SocketClientAdapter socketClientAdapter = shellRuntime.getSocketClientAdapter();
    private final CameraAdapter cameraAdapter = shellRuntime.getCameraAdapter();
    private final RfidAdapter rfidAdapter = shellRuntime.getRfidAdapter();
    private final GpsSerialMonitor gpsSerialMonitor = shellRuntime.getGpsSerialMonitor();
    private final Jt808SocketMonitor jt808SocketMonitor = shellRuntime.getJt808SocketMonitor();
    private final SystemOps systemOps = shellRuntime.getSystemOps();
    private final TerminalModuleHub moduleHub = shellRuntime.getModuleHub();

    private ActivityMainBinding binding;
    private ShellConfig shellConfig;
    private String latestActionText = "终端已就绪，等待业务动作。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindActions();
        updateDebugVisibility();
        refreshRuntime();
        AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "business home opened", TraceIds.next("home"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRuntime();
    }

    /**
     * 绑定首页业务动作和 debug 入口。
     */
    private void bindActions() {
        binding.btnDispatchAction.setOnClickListener(v -> openModuleCenter("dispatch"));
        binding.btnStationAction.setOnClickListener(v -> openModuleCenter("station"));
        binding.btnSignInAction.setOnClickListener(v -> openModuleCenter("signin"));
        binding.btnCameraAction.setOnClickListener(v -> openModuleCenter("camera_dvr"));
        binding.btnUpgradeAction.setOnClickListener(v -> openModuleCenter("upgrade"));
        binding.btnFileAction.setOnClickListener(v -> openModuleCenter("file"));

        binding.btnRunAllModules.setOnClickListener(v -> runAllModulesAction());
        binding.btnOpenModules.setOnClickListener(v -> startActivity(new Intent(this, BusinessModulesActivity.class)));
        binding.btnOpenDebug.setOnClickListener(v -> startActivity(DebugIntentFactory.create(this)));
        binding.btnClearLogs.setOnClickListener(v -> {
            AppLogCenter.clear();
            latestActionText = "日志已清空，调试链路可重新开始。";
            renderHome();
        });
    }

    private void openModuleCenter(String moduleKey) {
        startActivity(ModuleCenterActivity.createIntent(this, moduleKey));
    }

    /**
     * debug 入口只在调试环境保留。
     */
    private void updateDebugVisibility() {
        binding.debugPanel.setVisibility(isDebugEntryEnabled() ? View.VISIBLE : View.GONE);
    }

    /**
     * 刷新配置、共享运行时和首页状态。
     */
    private void refreshRuntime() {
        shellConfig = ShellConfigRepository.get(this);
        shellRuntime.applyConfig(this, shellConfig);
        jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, TraceIds.next("home-socket-monitor"));
        renderHome();
    }

    private void runModuleAction(String moduleKey) {
        String traceId = TraceIds.next("home-" + moduleKey);
        ModuleRunResult result = moduleHub.runModule(moduleKey, traceId);
        latestActionText = result.describeBlock();
        AppLogCenter.log(
                result.isSuccess() ? LogCategory.BIZ : LogCategory.ERROR,
                result.isSuccess() ? LogLevel.INFO : LogLevel.ERROR,
                TAG,
                latestActionText.replace('\n', ' '),
                traceId
        );
        renderHome();
    }

    private void runAllModulesAction() {
        String traceId = TraceIds.next("home-all");
        latestActionText = moduleHub.describeResults(moduleHub.runAll(traceId));
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, latestActionText.replace('\n', ' '), traceId);
        renderHome();
    }

    /**
     * 渲染首页业务状态。
     */
    private void renderHome() {
        if (shellConfig == null) {
            return;
        }

        binding.tvHomeSubtitle.setText(buildHeaderSubtitle());
        binding.tvTerminalBadge.setText("终端 " + valueOrDash(shellConfig.getDeviceProfile()));
        binding.tvConfigBadge.setText("配置 v" + valueOrDash(shellConfig.getConfigVersion()));
        binding.tvDispatchBadge.setText(buildDispatchBadgeText());

        binding.tvDispatchValue.setText(buildDispatchValue());
        binding.tvDispatchDetail.setText(buildDispatchDetail());

        binding.tvStationValue.setText(buildStationValue());
        binding.tvStationDetail.setText(buildStationDetail());

        binding.tvSignInValue.setText(buildSignInValue());
        binding.tvSignInDetail.setText(buildSignInDetail());

        binding.tvCameraValue.setText(buildCameraValue());
        binding.tvCameraDetail.setText(buildCameraDetail());

        binding.tvSystemValue.setText(buildSystemValue());
        binding.tvSystemDetail.setText(buildSystemDetail());

        binding.tvConfigValue.setText(buildConfigValue());
        binding.tvConfigDetail.setText(buildConfigDetail());

        binding.tvLatestAction.setText(latestActionText);
        binding.tvOpsStatus.setText(buildRuntimeStatus());
    }

    private String buildHeaderSubtitle() {
        return "M90 Android 11 终端 / 当前配置来源 "
                + valueOrDash(shellConfig.getConfigSource())
                + " / "
                + ShellConfigValidator.describe(shellConfig);
    }

    private String buildDispatchBadgeText() {
        if (shellConfig.getBasicSetupConfig().getProtocolLinkageSettings().isSerialDispatchEnabled()) {
            return "调度走串口";
        }
        try {
            ShellConfig.SocketChannel jt808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
            ShellConfig.SocketChannel al808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getAl808SocketKey());
            int connected = 0;
            if (socketClientAdapter.isConnected(jt808.getChannelName())) {
                connected++;
            }
            if (socketClientAdapter.isConnected(al808.getChannelName())) {
                connected++;
            }
            if (connected == 0) {
                return "调度待连接";
            }
            return "调度在线 " + connected + "/2";
        } catch (Exception e) {
            return "调度未配置";
        }
    }

    private String buildDispatchValue() {
        DispatchState dispatchState = getDispatchState();
        if (shellConfig.getBasicSetupConfig().getProtocolLinkageSettings().isSerialDispatchEnabled()) {
            String protocol = valueOrDash(shellConfig.getBasicSetupConfig().getSerialSettings().getRs2321Protocol());
            return "串口调度/" + protocol;
        }
        if (dispatchState != null) {
            if (dispatchState.isJoinedOperation()) {
                return "已参加运营";
            }
            if (!"-".equals(dispatchState.getScheduleNo())) {
                return "待发车";
            }
        }
        try {
            ShellConfig.SocketChannel jt808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
            ShellConfig.SocketChannel al808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getAl808SocketKey());
            int connected = 0;
            if (socketClientAdapter.isConnected(jt808.getChannelName())) {
                connected++;
            }
            if (socketClientAdapter.isConnected(al808.getChannelName())) {
                connected++;
            }
            return connected == 0 ? "待连接" : "在线 " + connected + "/2";
        } catch (Exception e) {
            return "未配置";
        }
    }

    private String buildDispatchDetail() {
        DispatchState dispatchState = getDispatchState();
        if (shellConfig.getBasicSetupConfig().getProtocolLinkageSettings().isSerialDispatchEnabled()) {
            return "当前归属 RS232-1"
                    + "\n协议 " + valueOrDash(shellConfig.getBasicSetupConfig().getSerialSettings().getRs2321Protocol())
                    + "\n网络 socket 当前只保留配置，不作为主链路"
                    + "\n班次 " + (dispatchState == null ? "-" : valueOrDash(dispatchState.getScheduleNo()))
                    + " / 协议 " + (dispatchState == null ? "-" : valueOrDash(dispatchState.getActiveProtocol()))
                    + "\n" + (dispatchState == null ? "等待串口调度接入" : valueOrDash(dispatchState.getDispatchMessage()));
        }
        try {
            ShellConfig.SocketChannel jt808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
            ShellConfig.SocketChannel al808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getAl808SocketKey());
            return "默认通道 " + jt808.getKey() + " / " + al808.getKey()
                    + "\n监听状态 " + yesNo(jt808SocketMonitor.isAttached(jt808.getChannelName()))
                    + " / " + yesNo(jt808SocketMonitor.isAttached(al808.getChannelName()))
                    + "\n班次 " + (dispatchState == null ? "-" : valueOrDash(dispatchState.getScheduleNo()))
                    + " / 协议 " + (dispatchState == null ? "-" : valueOrDash(dispatchState.getActiveProtocol()))
                    + "\n发车 " + (dispatchState == null ? "-" : valueOrDash(dispatchState.getPlannedDepartureTime()))
                    + " / 到站 " + (dispatchState == null ? "-" : valueOrDash(dispatchState.getPlannedArrivalTime()))
                    + "\n" + (dispatchState == null ? "等待调度消息" : valueOrDash(dispatchState.getDispatchMessage()));
        } catch (Exception e) {
            return "当前还没拿到完整调度配置";
        }
    }

    private String buildStationValue() {
        StationState stationState = getStationState();
        if (stationState != null && !"-".equals(stationState.getCurrentStation())) {
            return stationState.getCurrentStation();
        }
        GpsFixSnapshot snapshot = gpsSerialMonitor.getLatestSnapshot();
        if (snapshot != null && snapshot.isValid()) {
            return "已定位";
        }
        return gpsSerialMonitor.isAttached() ? "等待定位" : "待绑定 GPS";
    }

    private String buildStationDetail() {
        StationState stationState = getStationState();
        GpsFixSnapshot snapshot = gpsSerialMonitor.getLatestSnapshot();
        try {
            ShellConfig.SerialChannel displayChannel = shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getDisplaySerialKey());
            ShellConfig.SerialChannel gpsChannel = shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getGpsSerialKey());
            String lat = snapshot != null && snapshot.isValid()
                    ? valueOrDash(snapshot.getLatitudeDecimal())
                    : (stationState == null ? "-" : valueOrDash(stationState.getLatitude()));
            String lng = snapshot != null && snapshot.isValid()
                    ? valueOrDash(snapshot.getLongitudeDecimal())
                    : (stationState == null ? "-" : valueOrDash(stationState.getLongitude()));
            int satellites = snapshot != null && snapshot.isValid()
                    ? snapshot.getUsedSatellites()
                    : (stationState == null ? 0 : stationState.getSatellites());
            return valueOrDash(stationState == null ? null : stationState.getLineName())
                    + " / " + valueOrDash(stationState == null ? null : stationState.getDirectionText())
                    + "\n本站 " + valueOrDash(stationState == null ? null : stationState.getCurrentStation())
                    + " / 下站 " + valueOrDash(stationState == null ? null : stationState.getNextStation())
                    + "\n阶段 " + valueOrDash(stationState == null ? null : stationState.getReportPhase())
                    + " / 终点 " + valueOrDash(stationState == null ? null : stationState.getTerminalStation())
                    + "\n屏显口 " + displayChannel.getKey()
                    + " / GPS 口 " + gpsChannel.getKey()
                    + "\nGPS 监听 " + (gpsSerialMonitor.isAttached() ? "已绑定" : "未绑定")
                    + " / 卫星 " + satellites
                    + "\n定位 " + lat + " , " + lng;
        } catch (Exception e) {
            return "当前还没拿到完整报站配置";
        }
    }

    private String buildSignInValue() {
        SignInState signInState = getSignInState();
        if (signInState != null && signInState.isSignedIn()) {
            return signInState.getDriverName();
        }
        return rfidAdapter.isAvailable() ? "待签到" : "等待设备";
    }

    private String buildSignInDetail() {
        SignInState signInState = getSignInState();
        if (signInState == null) {
            return compactBlock(findModuleStatus("signin"), 3);
        }
        return "卡号 " + valueOrDash(signInState.getCardNo())
                + "\n状态 " + (signInState.isSignedIn() ? "已签到" : "已签退")
                + " / 类型 " + valueOrDash(signInState.getAttendanceMode())
                + "\n刷卡次数 " + signInState.getAttendanceCount()
                + " / 最近 " + formatTime(signInState.getLastAttendanceTimeMillis());
    }

    private String buildCameraValue() {
        return cameraAdapter.isAvailable() ? "视频就绪" : "等待设备";
    }

    private String buildCameraDetail() {
        try {
            String cameraKey = shellConfig.getDebugReplay().getCameraChannelKey();
            ShellConfig.CameraChannel channel = shellConfig.getCameraConfig().requireChannel(cameraKey);
            return "默认通道 " + cameraKey
                    + " / cameraId " + valueOrDash(channel.getCameraId())
                    + "\n" + compactBlock(findModuleStatus("camera_dvr"), 2);
        } catch (Exception e) {
            return compactBlock(findModuleStatus("camera_dvr"), 2);
        }
    }

    private String buildSystemValue() {
        int enabled = 0;
        if (systemOps.supportsSilentInstall()) {
            enabled++;
        }
        if (shellConfig.getSystemConfig().isAllowReboot()) {
            enabled++;
        }
        if (shellConfig.getSystemConfig().isAllowSetTime()) {
            enabled++;
        }
        return enabled == 0 ? "基础模式" : "已开放 " + enabled + " 项";
    }

    private String buildSystemDetail() {
        return "静默安装 " + yesNo(systemOps.supportsSilentInstall())
                + " / 重启 " + yesNo(shellConfig.getSystemConfig().isAllowReboot())
                + " / 校时 " + yesNo(shellConfig.getSystemConfig().isAllowSetTime())
                + "\n" + compactBlock(findModuleStatus("upgrade"), 2);
    }

    private String buildConfigValue() {
        return valueOrDash(shellConfig.getDeviceProfile()) + " / " + valueOrDash(shellConfig.getConfigSource());
    }

    private String buildConfigDetail() {
        return "版本 " + valueOrDash(shellConfig.getConfigVersion())
                + "\n默认回放 " + valueOrDash(shellConfig.getDebugReplay().getDisplaySerialKey())
                + " / " + valueOrDash(shellConfig.getDebugReplay().getJt808SocketKey())
                + "\n资源导入 " + yesNo(shellConfig.getBasicSetupConfig().getResourceImportSettings().isStationResourceImported())
                + " / 调度归属 " + valueOrDash(shellConfig.getBasicSetupConfig().getProtocolLinkageSettings().getDispatchOwner())
                + "\n" + compactBlock(findModuleStatus("file"), 2);
    }

    private String buildRuntimeStatus() {
        return compactBlock(shellRuntime.describeFoundationStatus(), 4)
                + "\n"
                + compactBlock(moduleHub.describeStatus(), 4);
    }

    private String findModuleStatus(String moduleKey) {
        TerminalBusinessModule module = moduleHub.findModule(moduleKey);
        return module == null ? "模块未注册" : module.describeStatus();
    }

    private DispatchState getDispatchState() {
        TerminalBusinessModule module = moduleHub.findModule("dispatch");
        return module instanceof DispatchBusinessModule
                ? ((DispatchBusinessModule) module).getDispatchState()
                : null;
    }

    private StationState getStationState() {
        TerminalBusinessModule module = moduleHub.findModule("station");
        return module instanceof StationBusinessModule
                ? ((StationBusinessModule) module).getStationState()
                : null;
    }

    private SignInState getSignInState() {
        TerminalBusinessModule module = moduleHub.findModule("signin");
        return module instanceof SignInBusinessModule
                ? ((SignInBusinessModule) module).getSignInState()
                : null;
    }

    private String compactBlock(String text, int maxLines) {
        if (text == null || text.trim().isEmpty()) {
            return "-";
        }
        String[] lines = text.trim().split("\\n");
        StringBuilder builder = new StringBuilder();
        int appended = 0;
        for (String line : lines) {
            String cleaned = line == null ? "" : line.trim();
            if (cleaned.isEmpty()) {
                continue;
            }
            if (cleaned.endsWith(":")) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(cleaned.replaceFirst("^-\\s*", ""));
            appended++;
            if (appended >= maxLines) {
                break;
            }
        }
        return builder.length() == 0 ? "-" : builder.toString();
    }

    private String yesNo(boolean value) {
        return value ? "已开启" : "未开启";
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private String formatTime(long timeMillis) {
        if (timeMillis <= 0) {
            return "-";
        }
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(timeMillis));
    }

    private boolean isDebugEntryEnabled() {
        return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
