package com.lhxy.istationdevice.android11.app;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lhxy.istationdevice.android11.app.databinding.ActivityBusinessModulesBinding;
import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.debugtools.DebugIntentFactory;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalModuleHub;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.util.List;

/**
 * 业务模块页
 * <p>
 * 这一页不再只是入口列表，而是六个模块的操作台：
 * 每块都能看状态，也能直接跑当前最常用的一组业务动作。
 */
public final class BusinessModulesActivity extends AppCompatActivity {
    private static final String TAG = "BusinessModulesActivity";

    private final ShellRuntime shellRuntime = ShellRuntime.get();
    private final TerminalModuleHub moduleHub = shellRuntime.getModuleHub();
    private final SocketClientAdapter socketClientAdapter = shellRuntime.getSocketClientAdapter();
    private final Jt808SocketMonitor jt808SocketMonitor = shellRuntime.getJt808SocketMonitor();

    private ActivityBusinessModulesBinding binding;
    private ShellConfig shellConfig;
    private String latestActionText = "最近动作:\n- 还没有执行模块动作";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessModulesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        refreshConfig();
        bindActions();
        binding.btnOpenDebug.setVisibility(isDebugEntryEnabled() ? View.VISIBLE : View.GONE);
        renderAll();

        AppLogCenter.log(LogCategory.UI, LogLevel.INFO, TAG, "business modules page opened", TraceIds.next("biz-page"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshConfig();
        renderAll();
    }

    private void bindActions() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnOpenDebug.setOnClickListener(v -> startActivity(DebugIntentFactory.create(this)));
        binding.btnRunAll.setOnClickListener(v -> runAllModules());

        binding.btnDispatchRun.setOnClickListener(v -> runModule("dispatch"));
        binding.btnDispatchReplayAll.setOnClickListener(v -> replayDispatchAll());
        binding.btnDispatchJoinOperation.setOnClickListener(v -> joinOperation());
        binding.btnDispatchLeaveOperation.setOnClickListener(v -> leaveOperation());

        binding.btnStationRun.setOnClickListener(v -> runModule("station"));
        binding.btnStationBindGps.setOnClickListener(v -> bindGpsMonitor());
        binding.btnStationAdvance.setOnClickListener(v -> advanceStation());

        binding.btnSignInRun.setOnClickListener(v -> runModule("signin"));
        binding.btnReadCard.setOnClickListener(v -> readCard());

        binding.btnCameraRun.setOnClickListener(v -> runModule("camera_dvr"));
        binding.btnOpenDefaultCamera.setOnClickListener(v -> openDefaultCamera());
        binding.btnCloseDefaultCamera.setOnClickListener(v -> closeDefaultCamera());

        binding.btnUpgradeRun.setOnClickListener(v -> runModule("upgrade"));
        binding.btnSyncSystemTime.setOnClickListener(v -> syncSystemTime());

        binding.btnFileRun.setOnClickListener(v -> runModule("file"));
        binding.btnExportBundle.setOnClickListener(v -> exportBundle());
        binding.btnResetRuntimeConfig.setOnClickListener(v -> resetRuntimeConfig());
    }

    private void refreshConfig() {
        shellConfig = ShellConfigRepository.get(this);
        shellRuntime.applyConfig(this, shellConfig);
        jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, TraceIds.next("biz-socket-monitor"));
    }

    private void runAllModules() {
        String traceId = TraceIds.next("biz-all");
        List<ModuleRunResult> results = moduleHub.runAll(traceId);
        setLatestAction(moduleHub.describeResults(results), traceId, true);
        renderAll();
    }

    private void runModule(String moduleKey) {
        String traceId = TraceIds.next("biz-" + moduleKey);
        ModuleRunResult result = moduleHub.runModule(moduleKey, traceId);
        setLatestAction(result.describeBlock(), traceId, result.isSuccess());
        renderAll();
    }

    private void replayDispatchAll() {
        runModuleAction("dispatch", "replay_all", "biz-dispatch-full");
    }

    private void bindGpsMonitor() {
        runModuleAction("station", "bind_gps", "biz-gps-bind");
    }

    private void advanceStation() {
        runModuleAction("station", "advance_station", "biz-station-next");
    }

    private void readCard() {
        runModuleAction("signin", "read_card", "biz-read-card");
    }

    private void joinOperation() {
        runModuleAction("dispatch", "join_operation", "biz-join-operation");
    }

    private void leaveOperation() {
        runModuleAction("dispatch", "leave_operation", "biz-leave-operation");
    }

    private void openDefaultCamera() {
        runModuleAction("camera_dvr", "open_default_camera", "biz-open-camera");
    }

    private void closeDefaultCamera() {
        runModuleAction("camera_dvr", "close_default_camera", "biz-close-camera");
    }

    private void syncSystemTime() {
        runModuleAction("upgrade", "sync_system_time", "biz-sync-time");
    }

    private void exportBundle() {
        runModuleAction("file", "export_bundle", "biz-export");
    }

    private void resetRuntimeConfig() {
        runModuleAction("file", "reset_runtime_config", "biz-reset-config");
    }

    private void setLatestAction(String text, String traceId, boolean success) {
        latestActionText = text == null || text.trim().isEmpty() ? "最近动作:\n- 没有可展示内容" : text.trim();
        AppLogCenter.log(
                success ? LogCategory.BIZ : LogCategory.ERROR,
                success ? LogLevel.INFO : LogLevel.ERROR,
                TAG,
                latestActionText.replace('\n', ' '),
                traceId
        );
    }

    private void runModuleAction(String moduleKey, String actionKey, String tracePrefix) {
        String traceId = TraceIds.next(tracePrefix);
        ModuleRunResult result = moduleHub.runAction(moduleKey, actionKey, traceId);
        if ("file".equals(moduleKey) && "reset_runtime_config".equals(actionKey) && result.isSuccess()) {
            shellConfig = ShellConfigRepository.reload(this);
            shellRuntime.applyConfig(this, shellConfig);
            jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, traceId + "-monitor");
        }
        setLatestAction(result.describeBlock(), traceId, result.isSuccess());
        renderAll();
    }

    private void renderAll() {
        binding.tvSummary.setText(
                shellRuntime.describeFoundationStatus()
                        + "\n\n"
                        + shellRuntime.describeModuleStatus()
        );
        binding.tvStatus.setText(latestActionText);
        binding.tvDispatchStatus.setText(findModuleStatus("dispatch"));
        binding.tvStationStatus.setText(findModuleStatus("station"));
        binding.tvSignInStatus.setText(findModuleStatus("signin"));
        binding.tvCameraStatus.setText(findModuleStatus("camera_dvr"));
        binding.tvUpgradeStatus.setText(findModuleStatus("upgrade"));
        binding.tvFileStatus.setText(findModuleStatus("file"));
    }

    private String findModuleStatus(String moduleKey) {
        for (TerminalBusinessModule module : moduleHub.getModules()) {
            if (module.getKey().equals(moduleKey)) {
                return module.getTitle() + "\n" + module.describeStatus();
            }
        }
        return moduleKey + "\n- 当前没有找到模块";
    }

    private boolean isDebugEntryEnabled() {
        return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
