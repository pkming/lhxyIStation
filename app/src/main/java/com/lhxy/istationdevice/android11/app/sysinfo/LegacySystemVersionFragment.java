package com.lhxy.istationdevice.android11.app.sysinfo;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.station.LegacyStationResourceStateRepository;
import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LegacyHomeStatusRepository;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.upgrade.TinkerHotUpdateStateStore;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 旧版系统信息-版本信息页。
 */
public final class LegacySystemVersionFragment extends Fragment {
    private static final long HOT_UPDATE_TIMEOUT_MILLIS = 3L * 60L * 1000L;
    private static final long HOT_UPDATE_POLL_INTERVAL_MILLIS = 5_000L;

    private TextView tvSVersionCode;
    private TextView tvVersionCode;
    private TextView tvVersionName;
    private TextView tvHotUpdateVersion;
    private TextView tvBuildTime;
    private TextView tvDataVersionCode;
    private TextView tvSourceVersionTime;
    private Button butCheckUpdate;
    private TextView tvCheckUpdateStatus;
    private ProgressBar hotUpdateProgressBar;
    private TextView hotUpdateProgressView;
    private SharedPreferences.OnSharedPreferenceChangeListener hotUpdateStateListener;
    private final Runnable hotUpdateStatePoller = new Runnable() {
        @Override
        public void run() {
            renderCheckUpdateState();
            scheduleHotUpdateStatePoller();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_versioninfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvSVersionCode = view.findViewById(R.id.tvSVersionCode);
        tvVersionCode = view.findViewById(R.id.tvVersionCode);
        tvVersionName = view.findViewById(R.id.tvVersionName);
        tvHotUpdateVersion = view.findViewById(R.id.tvHotUpdateVersion);
        tvBuildTime = view.findViewById(R.id.tvBuildTime);
        tvDataVersionCode = view.findViewById(R.id.tvDataVersionCode);
        tvSourceVersionTime = view.findViewById(R.id.tvSourceVersionTime);
        butCheckUpdate = view.findViewById(R.id.butCheckUpdate);
        tvCheckUpdateStatus = view.findViewById(R.id.tvCheckUpdateStatus);
        bindCheckUpdateAction();
        attachCheckUpdateProgressViews(view);
        registerHotUpdateStateListener();
        render();
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
        scheduleHotUpdateStatePoller();
    }

    @Override
    public void onPause() {
        stopHotUpdateStatePoller();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        stopHotUpdateStatePoller();
        unregisterHotUpdateStateListener();
        super.onDestroyView();
        tvSVersionCode = null;
        tvVersionCode = null;
        tvVersionName = null;
        tvHotUpdateVersion = null;
        tvBuildTime = null;
        tvDataVersionCode = null;
        tvSourceVersionTime = null;
        butCheckUpdate = null;
        tvCheckUpdateStatus = null;
        hotUpdateProgressBar = null;
        hotUpdateProgressView = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            stopHotUpdateStatePoller();
            return;
        }
        render();
        scheduleHotUpdateStatePoller();
    }

    private void render() {
        if (getContext() == null || isHidden()) {
            return;
        }
        ShellConfig shellConfig = ShellRuntime.get().getActiveConfig();
        PackageInfo packageInfo = loadPackageInfo();
        LegacyStationResourceStateRepository.StationResourceState resourceState =
                LegacyStationResourceStateRepository.getState(requireContext());
        String hardwareVersion = shellConfig == null ? Build.MODEL : shellConfig.getDeviceProfile();
        String softwareCode = packageInfo == null ? "-" : String.valueOf(resolveVersionCode(packageInfo));
        String softwareName = packageInfo == null ? "-" : safe(packageInfo.versionName);
        String hotUpdateVersion = safeHotUpdateVersion(TinkerHotUpdateStateStore.getLastPatchVersion(requireContext()));
        String buildTime = safe(getString(R.string.app_build_time_value));
        String dataVersion = shellConfig == null ? "-" : safe(shellConfig.getConfigVersion());
        String sourceVersionTime = formatTime(resourceState.getUpdatedAt());

        if (tvSVersionCode != null) {
            tvSVersionCode.setText(getString(R.string.version_hardware, hardwareVersion));
        }
        if (tvVersionCode != null) {
            tvVersionCode.setText(getString(R.string.version_software_number, softwareCode));
        }
        if (tvVersionName != null) {
            tvVersionName.setText(getString(R.string.version_software_name, softwareName));
        }
        if (tvHotUpdateVersion != null) {
            tvHotUpdateVersion.setText(getString(R.string.version_hot_update_version, hotUpdateVersion));
        }
        if (tvBuildTime != null) {
            tvBuildTime.setText(getString(R.string.version_build_time, buildTime));
        }
        if (tvDataVersionCode != null) {
            tvDataVersionCode.setText(getString(R.string.version_data_number, dataVersion));
        }
        if (tvSourceVersionTime != null) {
            tvSourceVersionTime.setText(sourceVersionTime);
        }
        renderCheckUpdateState();
    }

    private void bindCheckUpdateAction() {
        if (butCheckUpdate == null || getContext() == null) {
            return;
        }
        applyButtonState(!TinkerHotUpdateStateStore.isProcessing(requireContext()));
        butCheckUpdate.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setMessage(R.string.file_check_hot_update_tip)
                .setPositiveButton(R.string.confirm, (dialog, which) -> runCheckHotUpdateAsync())
                .setNegativeButton(android.R.string.cancel, null)
                .show());
    }

    private void runCheckHotUpdateAsync() {
        FragmentActivity activity = getActivity();
        if (activity == null || getContext() == null) {
            return;
        }
        if (TinkerHotUpdateStateStore.isProcessing(requireContext())) {
            renderCheckUpdateState();
            return;
        }
        String traceId = TraceIds.next("legacy-system-version-hot-update");
        AppLogCenter.log(
                com.lhxy.istationdevice.android11.core.LogCategory.BIZ,
                com.lhxy.istationdevice.android11.core.LogLevel.INFO,
                "LegacySystemVersionFragment",
                "用户点击检查热更新",
                traceId
        );
        showCheckUpdateStatus(getString(R.string.file_check_hot_update_load_tip));
        LegacyHomeStatusRepository.setInfoTips(requireContext(), getString(R.string.home_info_tip_hot_update_loading));
        renderCheckUpdateState();
        new Thread(() -> {
            ModuleRunResult result = ShellRuntime.get()
                    .getModuleHub()
                    .runAction("upgrade", "check_hot_update", traceId);
            FragmentActivity currentActivity = getActivity();
            if (currentActivity == null) {
                return;
            }
            currentActivity.runOnUiThread(() -> {
                if (!isAdded()) {
                    return;
                }
                publishHomeResultState(result);
                showCheckUpdateStatus(resolveCheckUpdateStatusText(result));
                showHotUpdateResultToast(result);
                render();
            });
        }, "legacy-system-version-hot-update").start();
    }

    private void publishHomeResultState(ModuleRunResult result) {
        if (getContext() == null) {
            return;
        }
        LegacyHomeStatusRepository.setInfoTips(
                requireContext(),
                result != null && result.isSuccess()
                        ? compactResultText(result)
                        : getString(R.string.home_info_tip_hot_update_failed)
        );
    }

    private void renderCheckUpdateState() {
        if (getContext() == null || hotUpdateProgressBar == null || hotUpdateProgressView == null || butCheckUpdate == null) {
            return;
        }
        reconcileLoadedHotUpdateState();
        resolveTimedOutHotUpdateState();
        int progress = TinkerHotUpdateStateStore.getProgressPercent(requireContext());
        boolean processing = TinkerHotUpdateStateStore.isProcessing(requireContext());
        applyButtonState(!processing);
        if (!processing) {
            hotUpdateProgressBar.setVisibility(View.GONE);
            hotUpdateProgressView.setVisibility(View.GONE);
            hotUpdateProgressBar.setProgress(0);
            return;
        }
        hotUpdateProgressBar.setVisibility(View.VISIBLE);
        hotUpdateProgressView.setVisibility(View.VISIBLE);
        hotUpdateProgressBar.setProgress(Math.max(progress, 1));
        hotUpdateProgressView.setText("热更新处理中 " + Math.max(progress, 1) + "%");
    }

    private void reconcileLoadedHotUpdateState() {
        if (getContext() == null || !TinkerHotUpdateStateStore.isProcessing(requireContext())) {
            return;
        }
        try {
            Tinker tinker = Tinker.with(requireContext().getApplicationContext());
            if (!tinker.isTinkerLoaded()) {
                return;
            }
            TinkerLoadResult loadResult = tinker.getTinkerLoadResultIfPresent();
            String currentVersion = loadResult == null ? "" : safeTrim(loadResult.currentVersion);
            if (currentVersion.isEmpty()) {
                return;
            }
            String patchVersion = firstNonBlank(TinkerHotUpdateStateStore.getPendingPatchVersion(requireContext()), currentVersion);
            String patchMd5 = TinkerHotUpdateStateStore.getPendingPatchMd5(requireContext());
            TinkerHotUpdateStateStore.markApplied(requireContext(), patchVersion, patchMd5);
            LegacyHomeStatusRepository.setInfoTips(requireContext(), "热更新补丁已生效");
            showCheckUpdateStatus("热更新补丁已生效");
            AppLogCenter.log(
                    com.lhxy.istationdevice.android11.core.LogCategory.BIZ,
                    com.lhxy.istationdevice.android11.core.LogLevel.INFO,
                    "LegacySystemVersionFragment",
                    "检测到补丁已加载，已自动清理处理中状态 currentVersion=" + currentVersion,
                    firstNonBlank(TinkerHotUpdateStateStore.getPendingTraceId(requireContext()), "legacy-hot-update-reconcile")
            );
        } catch (Exception e) {
            AppLogCenter.log(
                    com.lhxy.istationdevice.android11.core.LogCategory.ERROR,
                    com.lhxy.istationdevice.android11.core.LogLevel.WARN,
                    "LegacySystemVersionFragment",
                    "检查补丁加载状态失败: " + e.getMessage(),
                    firstNonBlank(TinkerHotUpdateStateStore.getPendingTraceId(requireContext()), "legacy-hot-update-reconcile")
            );
        }
    }

    private void resolveTimedOutHotUpdateState() {
        if (getContext() == null || !TinkerHotUpdateStateStore.isProcessing(requireContext())) {
            return;
        }
        long pendingStartedAt = TinkerHotUpdateStateStore.getPendingStartedAt(requireContext());
        if (pendingStartedAt <= 0L) {
            return;
        }
        long elapsedMillis = System.currentTimeMillis() - pendingStartedAt;
        if (elapsedMillis < HOT_UPDATE_TIMEOUT_MILLIS) {
            return;
        }
        TinkerHotUpdateStateStore.clearPending(requireContext());
        LegacyHomeStatusRepository.setInfoTips(requireContext(), "热更新处理超时，请重新检查或手动重启应用");
        showCheckUpdateStatus("热更新处理超时，请重新检查或手动重启应用");
        AppLogCenter.log(
                com.lhxy.istationdevice.android11.core.LogCategory.ERROR,
                com.lhxy.istationdevice.android11.core.LogLevel.WARN,
                "LegacySystemVersionFragment",
                "热更新长时间未收到结果回调，已按超时失败收口",
                firstNonBlank(TinkerHotUpdateStateStore.getPendingTraceId(requireContext()), "legacy-hot-update-timeout")
        );
    }

    private void attachCheckUpdateProgressViews(View view) {
        RelativeLayout container = view.findViewById(R.id.rlCheckUpdate);
        if (container == null || hotUpdateProgressBar != null || hotUpdateProgressView != null || getContext() == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = dp(96);
            container.setLayoutParams(layoutParams);
        }

        hotUpdateProgressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal);
        hotUpdateProgressBar.setId(View.generateViewId());
        hotUpdateProgressBar.setMax(99);
        hotUpdateProgressBar.setVisibility(View.GONE);
        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                dp(6)
        );
        progressParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        progressParams.leftMargin = dp(2);
        progressParams.rightMargin = dp(220);
        progressParams.bottomMargin = dp(12);
        container.addView(hotUpdateProgressBar, progressParams);

        hotUpdateProgressView = new TextView(requireContext());
        hotUpdateProgressView.setTextColor(ContextCompat.getColor(requireContext(), R.color.c_ffffff));
        hotUpdateProgressView.setTextSize(16f);
        hotUpdateProgressView.setVisibility(View.GONE);
        RelativeLayout.LayoutParams progressTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        progressTextParams.addRule(RelativeLayout.ABOVE, hotUpdateProgressBar.getId());
        progressTextParams.leftMargin = dp(2);
        progressTextParams.bottomMargin = dp(4);
        container.addView(hotUpdateProgressView, progressTextParams);
    }

    private void registerHotUpdateStateListener() {
        if (hotUpdateStateListener != null || getContext() == null) {
            return;
        }
        hotUpdateStateListener = TinkerHotUpdateStateStore.registerListener(requireContext(), () -> {
            FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() -> {
                if (!isAdded()) {
                    return;
                }
                render();
            });
        });
    }

    private void unregisterHotUpdateStateListener() {
        if (getContext() == null) {
            hotUpdateStateListener = null;
            return;
        }
        TinkerHotUpdateStateStore.unregisterListener(requireContext(), hotUpdateStateListener);
        hotUpdateStateListener = null;
    }

    private void scheduleHotUpdateStatePoller() {
        if (hotUpdateProgressView == null || isHidden()) {
            return;
        }
        hotUpdateProgressView.removeCallbacks(hotUpdateStatePoller);
        hotUpdateProgressView.postDelayed(hotUpdateStatePoller, HOT_UPDATE_POLL_INTERVAL_MILLIS);
    }

    private void stopHotUpdateStatePoller() {
        if (hotUpdateProgressView == null) {
            return;
        }
        hotUpdateProgressView.removeCallbacks(hotUpdateStatePoller);
    }

    private void applyButtonState(boolean enabled) {
        if (butCheckUpdate == null || getContext() == null) {
            return;
        }
        butCheckUpdate.setEnabled(enabled);
        butCheckUpdate.setTextColor(ContextCompat.getColor(requireContext(), enabled ? R.color.c_000000 : R.color.c_414141));
        butCheckUpdate.setBackgroundResource(enabled ? R.drawable.btn_basic_setup_bg : R.drawable.btn_upgrade_bg);
    }

    private void showCheckUpdateStatus(String text) {
        if (tvCheckUpdateStatus == null) {
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            tvCheckUpdateStatus.setText("");
            tvCheckUpdateStatus.setVisibility(View.GONE);
            return;
        }
        tvCheckUpdateStatus.setText(text.trim());
        tvCheckUpdateStatus.setVisibility(View.VISIBLE);
    }

    private String resolveCheckUpdateStatusText(ModuleRunResult result) {
        String block = result == null ? "" : safeTrim(result.describeBlock());
        if (!block.isEmpty()) {
            return block;
        }
        return compactResultText(result);
    }

    private String compactResultText(ModuleRunResult result) {
        if (result == null) {
            return "";
        }
        String summary = result.getSummary();
        if (summary != null && !summary.trim().isEmpty()) {
            return summary.trim();
        }
        String detail = result.getDetail();
        return detail == null ? "" : detail.trim();
    }

    private void showHotUpdateResultToast(ModuleRunResult result) {
        if (getContext() == null) {
            return;
        }
        String message = compactResultText(result);
        if (message == null || message.trim().isEmpty()) {
            message = result != null && result.isSuccess() ? "热更新检查完成" : "热更新检查失败";
        }
        Toast.makeText(requireContext(), message.trim(), Toast.LENGTH_LONG).show();
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.trim().isEmpty()) {
            return first.trim();
        }
        if (second != null && !second.trim().isEmpty()) {
            return second.trim();
        }
        return "NO_TRACE";
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private int dp(int value) {
        return Math.round(value * requireContext().getResources().getDisplayMetrics().density);
    }

    @Nullable
    private PackageInfo loadPackageInfo() {
        if (getContext() == null) {
            return null;
        }
        PackageManager packageManager = getContext().getPackageManager();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return packageManager.getPackageInfo(getContext().getPackageName(), PackageManager.PackageInfoFlags.of(0));
            }
            return packageManager.getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private long resolveVersionCode(PackageInfo packageInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return packageInfo.getLongVersionCode();
        }
        return packageInfo.versionCode;
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private String safeHotUpdateVersion(String value) {
        if (value == null || value.trim().isEmpty()) {
            return getString(R.string.version_hot_update_not_applied);
        }
        return value.trim();
    }

    private String formatTime(long timeMillis) {
        if (timeMillis <= 0L) {
            return "-";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timeMillis));
    }
}
