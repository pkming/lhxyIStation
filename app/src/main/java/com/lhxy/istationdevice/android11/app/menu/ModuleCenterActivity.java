package com.lhxy.istationdevice.android11.app.menu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.databinding.ActivityModuleCenterBinding;
import com.lhxy.istationdevice.android11.app.media.LegacyVideoMonitorActivity;
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

/**
 * 正式业务模块页。
 * <p>
 * 首页只做总览和跳转，具体业务操作统一进入这里，
 * 避免后面所有动作都堆在首页和 debug 页里。
 * <p>
 * 查找关键字：模块详情页、模块动作分发、运行时同步、视频入口。
 */
public final class ModuleCenterActivity extends AppCompatActivity {
    private static final String EXTRA_MODULE_KEY = "module_key";

    private final ShellRuntime shellRuntime = ShellRuntime.get();
    private final TerminalModuleHub moduleHub = shellRuntime.getModuleHub();
    private final SocketClientAdapter socketClientAdapter = shellRuntime.getSocketClientAdapter();
    private final Jt808SocketMonitor jt808SocketMonitor = shellRuntime.getJt808SocketMonitor();

    private ActivityModuleCenterBinding binding;
    private ShellConfig shellConfig;
    private String latestActionText = "等待业务动作。";
    private ModuleScreenSpec screenSpec;

    /**
     * 根据模块 key 打开对应的模块详情页。
     */
    public static Intent createIntent(Context context, String moduleKey) {
        Intent intent = new Intent(context, ModuleCenterActivity.class);
        intent.putExtra(EXTRA_MODULE_KEY, moduleKey);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModuleCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        screenSpec = ModuleScreenSpec.from(getIntent().getStringExtra(EXTRA_MODULE_KEY));
        bindActions();
        updateDebugVisibility();
        refreshRuntime();
        renderSpec();

        AppLogCenter.log(
                LogCategory.UI,
                LogLevel.INFO,
                "ModuleCenterActivity",
                "module center opened: " + screenSpec.moduleKey,
                TraceIds.next("module-center")
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRuntime();
    }

    /**
     * 绑定详情页头部和动作按钮。
     */
    private void bindActions() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnOpenDebug.setOnClickListener(v -> startActivity(DebugIntentFactory.create(this)));
        binding.btnRunPrimary.setOnClickListener(v -> runPrimaryAction());
        binding.btnActionOne.setOnClickListener(v -> runConfiguredAction(screenSpec.actionOne));
        binding.btnActionTwo.setOnClickListener(v -> runConfiguredAction(screenSpec.actionTwo));
        binding.btnActionThree.setOnClickListener(v -> runConfiguredAction(screenSpec.actionThree));
    }

    private void updateDebugVisibility() {
        binding.btnOpenDebug.setVisibility(isDebugEntryEnabled() ? View.VISIBLE : View.GONE);
    }

    /**
     * 拉取当前配置并同步到共享运行时，再刷新 Socket 监听状态。
     */
    private void refreshRuntime() {
        shellConfig = ShellConfigRepository.get(this);
        shellRuntime.applyConfig(this, shellConfig);
        jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, TraceIds.next("module-socket-monitor"));
        renderStatus();
    }

    /**
     * 按 screenSpec 把标题、提示语和按钮文案渲染到页面。
     */
    private void renderSpec() {
        binding.tvTitle.setText(screenSpec.title);
        binding.tvHint.setText(screenSpec.hint);
        binding.btnRunPrimary.setText(screenSpec.primaryLabel);
        renderActionButton(binding.btnActionOne, screenSpec.actionOne);
        renderActionButton(binding.btnActionTwo, screenSpec.actionTwo);
        renderActionButton(binding.btnActionThree, screenSpec.actionThree);
    }

    private void renderActionButton(com.google.android.material.button.MaterialButton button, @Nullable ActionSpec spec) {
        if (spec == null) {
            button.setVisibility(View.GONE);
            return;
        }
        button.setVisibility(View.VISIBLE);
        button.setText(spec.label);
    }

    /**
     * 刷新当前模块状态摘要和最近动作结果。
     */
    private void renderStatus() {
        TerminalBusinessModule module = moduleHub.findModule(screenSpec.moduleKey);
        String status = module == null ? "当前没有找到模块。" : module.describeStatus();
        binding.tvStatusValue.setText(status);
        binding.tvLatestAction.setText(latestActionText);
    }

    /**
     * 执行当前模块的默认主动作。
     * <p>
     * 视频模块的主动作不是直接跑 moduleHub，而是跳到旧版视频监控页继续联调。
     */
    private void runPrimaryAction() {
        if ("camera_dvr".equals(screenSpec.moduleKey)) {
            startActivity(LegacyVideoMonitorActivity.createIntent(this, "module_center"));
            latestActionText = "已进入旧版视频监控页。";
            renderStatus();
            return;
        }
        String traceId = TraceIds.next("module-primary-" + screenSpec.moduleKey);
        ModuleRunResult result = moduleHub.runModule(screenSpec.moduleKey, traceId);
        applyResult(result, traceId);
    }

    /**
     * 执行页面上配置好的次级动作。
     * <p>
     * 文件模块重置运行时配置后，会立刻重载配置并重新同步监视器。
     */
    private void runConfiguredAction(@Nullable ActionSpec actionSpec) {
        if (actionSpec == null) {
            return;
        }
        String traceId = TraceIds.next(actionSpec.tracePrefix);
        ModuleRunResult result = moduleHub.runAction(screenSpec.moduleKey, actionSpec.actionKey, traceId);
        if ("file".equals(screenSpec.moduleKey) && "reset_runtime_config".equals(actionSpec.actionKey) && result.isSuccess()) {
            shellConfig = ShellConfigRepository.reload(this);
            shellRuntime.applyConfig(this, shellConfig);
            jt808SocketMonitor.syncDefaultChannels(socketClientAdapter, shellConfig, traceId + "-monitor");
        }
        applyResult(result, traceId);
    }

    /**
     * 统一接收模块执行结果，更新页面文案并落日志。
     */
    private void applyResult(ModuleRunResult result, String traceId) {
        latestActionText = result.describeBlock();
        AppLogCenter.log(
                result.isSuccess() ? LogCategory.BIZ : LogCategory.ERROR,
                result.isSuccess() ? LogLevel.INFO : LogLevel.ERROR,
                "ModuleCenterActivity",
                latestActionText.replace('\n', ' '),
                traceId
        );
        renderStatus();
    }

    private boolean isDebugEntryEnabled() {
        return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    private static final class ActionSpec {
        private final String label;
        private final String actionKey;
        private final String tracePrefix;

        private ActionSpec(String label, String actionKey, String tracePrefix) {
            this.label = label;
            this.actionKey = actionKey;
            this.tracePrefix = tracePrefix;
        }
    }

    private static final class ModuleScreenSpec {
        private final String moduleKey;
        private final String title;
        private final String hint;
        private final String primaryLabel;
        private final ActionSpec actionOne;
        private final ActionSpec actionTwo;
        private final ActionSpec actionThree;

        private ModuleScreenSpec(
                String moduleKey,
                String title,
                String hint,
                String primaryLabel,
                @Nullable ActionSpec actionOne,
                @Nullable ActionSpec actionTwo,
                @Nullable ActionSpec actionThree
        ) {
            this.moduleKey = moduleKey;
            this.title = title;
            this.hint = hint;
            this.primaryLabel = primaryLabel;
            this.actionOne = actionOne;
            this.actionTwo = actionTwo;
            this.actionThree = actionThree;
        }

        /**
         * 根据模块 key 生成页面展示和动作配置。
         * <p>
         * 这里集中维护“模块 key -> 文案/按钮/动作”的映射，后面排查入口错配时先看这里。
         */
        private static ModuleScreenSpec from(@Nullable String moduleKey) {
            if ("dispatch".equals(moduleKey)) {
                return new ModuleScreenSpec(
                        "dispatch",
                        "调度中心",
                    "这里承接调度主链的软件流程，先把运营状态、公告应答和收口动作放在正式业务页。",
                        "执行调度主链",
                    new ActionSpec("确认公告", "ack_notice", "module-dispatch-notice"),
                        new ActionSpec("确认调度", "confirm_dispatch", "module-dispatch-confirm"),
                        new ActionSpec("执行发车", "start_bus", "module-dispatch-start")
                );
            }
            if ("station".equals(moduleKey)) {
                return new ModuleScreenSpec(
                        "station",
                        "报站中心",
                        "这里承接报站、GPS 和屏显动作，后面真实 GPS 自动报站也直接接在这里。",
                        "报站",
                        new ActionSpec("切换方向", "switch_direction", "module-station-direction"),
                        new ActionSpec("重复报站", "repeat_station", "module-station-repeat"),
                        new ActionSpec("停止报站", "stop_station", "module-station-stop")
                );
            }
            if ("gps".equals(moduleKey)) {
                return new ModuleScreenSpec(
                        "gps",
                        "GPS中心",
                        "这里承接 GPS 串口绑定、线路资源核对，以及自动报站判定的独立诊断入口。",
                        "绑定 GPS",
                        new ActionSpec("扫描线路", "scan_active_route", "module-gps-route"),
                        new ActionSpec("自动判定", "evaluate_auto_report", "module-gps-auto-report"),
                        new ActionSpec("GPS校时", "sync_gps_time", "module-gps-time-sync")
                );
            }
            if ("signin".equals(moduleKey)) {
                return new ModuleScreenSpec(
                        "signin",
                        "签到中心",
                        "这里承接司机刷卡、签到签退和后续考勤上报，页面直接看司机状态。",
                        "执行签到主链",
                        new ActionSpec("读取卡号", "read_card", "module-signin-read"),
                        new ActionSpec("手动签退", "manual_sign_out", "module-signin-out"),
                        null
                );
            }
            if ("camera_dvr".equals(moduleKey)) {
                return new ModuleScreenSpec(
                        "camera_dvr",
                        "视频中心",
                        "这里承接 Camera、GPIO 和后续 DVR 业务动作，正式页先切到旧版视频监控界面。",
                        "打开视频监控",
                        new ActionSpec("打开默认 Camera", "open_default_camera", "module-camera-open"),
                        new ActionSpec("关闭默认 Camera", "close_default_camera", "module-camera-close"),
                        null
                );
            }
            if ("upgrade".equals(moduleKey)) {
                return new ModuleScreenSpec(
                        "upgrade",
                        "系统中心",
                        "这里承接校时、升级和系统动作，后面真机能力就直接挂这里。",
                        "执行系统主链",
                        new ActionSpec("同步系统时间", "sync_system_time", "module-upgrade-time"),
                        null,
                        null
                );
            }
            return new ModuleScreenSpec(
                    "file",
                    "文件中心",
                    "这里承接调试包、运行期配置和导入导出主链，后面 U 盘/文件流也直接接这里。",
                    "执行文件主链",
                    new ActionSpec("导出调试包", "export_bundle", "module-file-export"),
                    new ActionSpec("重置运行配置", "reset_runtime_config", "module-file-reset"),
                    new ActionSpec("导出日志包", "export_logs", "module-file-logs")
            );
        }
    }
}
