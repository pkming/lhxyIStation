package com.lhxy.istationdevice.android11.app.file;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;
import com.lhxy.istationdevice.android11.app.line.LegacyLineCatalog;
import com.lhxy.istationdevice.android11.app.station.LegacyStationResourceStateRepository;
import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LegacyHomeStatusRepository;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.StationBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.domain.upgrade.LocalUpgradeApkFinder;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.io.File;
import java.text.DateFormat;

/**
 * 旧版文件管理页骨架。
 */
public final class LegacyFileManageActivity extends LegacyBaseActivity {
    private BroadcastReceiver storageReceiver;

    @Override
    protected int getLayoutId() {
        return R.layout.act_file_manage;
    }

    @Override
    protected int getTitleResId() {
        return R.string.filemanage_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        bindImportAction();
        bindExportAction();
        bindUpgradeAction();
        bindExportLogAction();
        bindDeleteLogAction();
        renderResourceStatus(null);
        registerStorageReceiver();
        refreshFileActionState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFileActionState();
    }

    @Override
    protected void onDestroy() {
        unregisterStorageReceiver();
        super.onDestroy();
    }

    private void bindImportAction() {
        Button button = findViewById(R.id.butImport);
        if (button == null) {
            return;
        }
        button.setEnabled(true);
        button.setTextColor(ContextCompat.getColor(this, R.color.c_000000));
        button.setOnClickListener(v -> confirmAndRunFileAction(
                R.string.file_import_tip,
                R.string.file_import_load_tip,
                "import_station_resources"
        ));
    }

    private void bindExportAction() {
        Button button = findViewById(R.id.butExport);
        if (button == null) {
            return;
        }
        button.setEnabled(true);
        button.setTextColor(ContextCompat.getColor(this, R.color.c_000000));
        button.setOnClickListener(v -> confirmAndRunFileAction(
                R.string.file_export_tip,
                R.string.file_export_load_tip,
                "export_station_resources"
        ));
    }

    private void bindUpgradeAction() {
        Button button = findViewById(R.id.butUpgrade);
        if (button == null) {
            return;
        }
        button.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage(R.string.file_upgrade_tip)
                .setPositiveButton(R.string.confirm, (dialog, which) -> runUpgradeAsync())
                .setNegativeButton(android.R.string.cancel, null)
                .show());
    }

    private void bindExportLogAction() {
        Button button = findViewById(R.id.butExportLog);
        if (button == null) {
            return;
        }
        applyButtonState(button, true);
        button.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage(R.string.file_export_log_tip)
                .setPositiveButton(R.string.confirm, (dialog, which) -> runFileActionAsync("export_bundle", getString(R.string.file_export_log_load_tip)))
                .setNegativeButton(android.R.string.cancel, null)
                .show());
    }

    private void bindDeleteLogAction() {
        Button button = findViewById(R.id.butDeleteLog);
        if (button == null) {
            return;
        }
        applyButtonState(button, true);
        button.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage(R.string.file_delete_log_tip)
                .setPositiveButton(R.string.confirm, (dialog, which) -> deleteLocalLog())
                .setNegativeButton(android.R.string.cancel, null)
                .show());
    }

    private void bindAction(int buttonId, int titleResId, int tipResId) {
        Button button = findViewById(buttonId);
        if (button == null) {
            return;
        }
        button.setEnabled(true);
        button.setTextColor(ContextCompat.getColor(this, R.color.c_000000));
        button.setOnClickListener(v -> {
            TextView tips = findViewById(R.id.tvFileTips);
            if (tips != null) {
                tips.setVisibility(View.VISIBLE);
                tips.setText(getString(titleResId) + "：\n" + getString(tipResId));
            }
        });
    }

    private void renderResourceStatus(String prefixText) {
        TextView tips = findViewById(R.id.tvFileTips);
        if (tips == null) {
            return;
        }
        LegacyStationResourceStateRepository.StationResourceState state = LegacyStationResourceStateRepository.getState(this);
        String statusText;
        if (!state.isImported()) {
            statusText = getString(R.string.legacy_station_resource_not_ready);
        } else {
            String updated = state.getUpdatedAt() <= 0
                    ? "-"
                    : DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(state.getUpdatedAt());
            statusText = getString(
                    R.string.legacy_station_resource_ready_status,
                    state.getLineName(),
                    state.getSource(),
                    updated
            );
        }
        tips.setVisibility(View.VISIBLE);
        if (prefixText == null || prefixText.trim().isEmpty()) {
            tips.setText(statusText);
            return;
        }
        tips.setText(prefixText.trim() + "\n\n" + statusText);
    }

    private void confirmAndRunFileAction(int messageResId, int loadingResId, String actionKey) {
        new AlertDialog.Builder(this)
                .setMessage(messageResId)
                .setPositiveButton(R.string.confirm, (dialog, which) -> runFileActionAsync(actionKey, getString(loadingResId)))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void runFileActionAsync(String actionKey, String loadingText) {
        TextView tips = findViewById(R.id.tvFileTips);
        if (tips != null) {
            tips.setVisibility(View.VISIBLE);
            tips.setText(loadingText);
        }
        publishHomeLoadingState(actionKey);
        new Thread(() -> {
            ModuleRunResult result = ShellRuntime.get()
                .getModuleHub()
                .runAction("file", actionKey, TraceIds.next("legacy-file-manage-" + actionKey));
            runOnUiThread(() -> {
                ShellRuntime.get().applyConfig(this, ShellConfigRepository.get(this));
                TerminalBusinessModule stationModule = ShellRuntime.get().getModuleHub().findModule("station");
                if (stationModule instanceof StationBusinessModule) {
                    LegacyStationResourceStateRepository.StationResourceState state = LegacyStationResourceStateRepository.getState(this);
                    StationState stationState = ((StationBusinessModule) stationModule).getStationState();
                    LegacyLineCatalog.LineProfile profile = LegacyLineCatalog.findByName(this, state.getLineName());
                    stationState.applyLineProfile(
                            profile.getLineName(),
                            stationState.getDirectionText(),
                            profile.stationsForDirection(stationState.getDirectionText())
                    );
                }
                TextView fileTips = findViewById(R.id.tvFileTips);
                if (fileTips != null) {
                    fileTips.setVisibility(View.VISIBLE);
                }
                publishHomeResultState(actionKey, result);
                renderResourceStatus(result.describeBlock());
                refreshFileActionState();
            });
        }, "legacy-file-manage-" + actionKey).start();
    }

    private void runUpgradeAsync() {
        File apkFile = LocalUpgradeApkFinder.findBest(this);
        if (apkFile == null) {
            TextView tips = findViewById(R.id.tvFileTips);
            if (tips != null) {
                tips.setVisibility(View.VISIBLE);
                tips.setText(R.string.file_upgrade_not_found);
            }
            LegacyHomeStatusRepository.setInfoTips(this, getString(R.string.file_upgrade_not_found));
            refreshFileActionState();
            return;
        }
        TextView tips = findViewById(R.id.tvFileTips);
        if (tips != null) {
            tips.setVisibility(View.VISIBLE);
            tips.setText(R.string.file_upgrade_load_tip);
        }
        new Thread(() -> {
            ModuleRunResult result = ShellRuntime.get()
                    .getModuleHub()
                    .runAction("upgrade", "install_local_apk", TraceIds.next("legacy-file-manage-upgrade"));
            runOnUiThread(() -> {
                if (result != null && !result.isSuccess()) {
                    publishHomeResultState("install_local_apk", result);
                }
                renderResourceStatus(result.describeBlock());
                refreshFileActionState();
            });
        }, "legacy-file-manage-upgrade").start();
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

    private void renderUpgradeHint() {
        TextView upgradeFile = findViewById(R.id.tvUpgradeFile);
        Button upgradeButton = findViewById(R.id.butUpgrade);
        if (upgradeFile == null || upgradeButton == null) {
            return;
        }
        File apkFile = LocalUpgradeApkFinder.findBest(this);
        if (apkFile == null) {
            upgradeFile.setText("");
            applyButtonState(upgradeButton, false);
            return;
        }
        upgradeFile.setText(getString(R.string.file_upgrade_find, apkFile.getName()));
        applyButtonState(upgradeButton, true);
    }

    private void refreshFileActionState() {
        renderUpgradeHint();
        renderExportLogState();
    }

    private void renderExportLogState() {
        Button exportLogButton = findViewById(R.id.butExportLog);
        if (exportLogButton == null) {
            return;
        }
        applyButtonState(exportLogButton, canExportLogBundle());
    }

    private void applyButtonState(Button button, boolean enabled) {
        if (button == null) {
            return;
        }
        button.setEnabled(enabled);
        button.setTextColor(ContextCompat.getColor(this, enabled ? R.color.c_000000 : R.color.c_414141));
        button.setBackgroundResource(enabled ? R.drawable.btn_basic_setup_bg : R.drawable.btn_upgrade_bg);
    }

    private boolean canExportLogBundle() {
        return AppLogCenter.getCurrentSessionFile() != null;
    }

    private void deleteLocalLog() {
        File logFile = AppLogCenter.getCurrentSessionFile();
        TextView tips = findViewById(R.id.tvFileTips);
        if (tips == null) {
            return;
        }
        if (logFile == null || !logFile.exists()) {
            tips.setVisibility(View.VISIBLE);
            tips.setText(R.string.file_delete_log_empty);
            return;
        }
        boolean deleted = logFile.delete();
        tips.setVisibility(View.VISIBLE);
        tips.setText(deleted ? R.string.file_delete_log_success : R.string.file_delete_log_empty);
        refreshFileActionState();
    }

    private void registerStorageReceiver() {
        if (storageReceiver != null) {
            return;
        }
        storageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshFileActionState();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addDataScheme("file");
        registerReceiver(storageReceiver, filter);
    }

    private void unregisterStorageReceiver() {
        if (storageReceiver == null) {
            return;
        }
        unregisterReceiver(storageReceiver);
        storageReceiver = null;
    }

    private String resolveHomeLoadingTip(String actionKey) {
        if ("import_station_resources".equals(actionKey)) {
            return getString(R.string.home_info_tip_import_loading);
        }
        if ("export_station_resources".equals(actionKey)) {
            return getString(R.string.home_info_tip_export_loading);
        }
        if ("export_bundle".equals(actionKey)) {
            return getString(R.string.home_info_tip_log_upload_loading);
        }
        return "";
    }

    private void publishHomeLoadingState(String actionKey) {
        if ("export_bundle".equals(actionKey)) {
            LegacyHomeStatusRepository.setInfoOperation(
                    this,
                    LegacyHomeStatusRepository.InfoOperation.UPLOAD_LOG_PROGRESS,
                    0
            );
            return;
        }
        LegacyHomeStatusRepository.setInfoTips(this, resolveHomeLoadingTip(actionKey));
    }

    private void publishHomeResultState(String actionKey, ModuleRunResult result) {
        if ("export_bundle".equals(actionKey)) {
            if (result != null && result.isSuccess()) {
                LegacyHomeStatusRepository.setInfoOperation(
                        this,
                        LegacyHomeStatusRepository.InfoOperation.UPLOAD_LOG_PROGRESS,
                        100
                );
            } else {
                LegacyHomeStatusRepository.setInfoTips(this, getString(R.string.home_info_tip_log_upload_failed));
            }
            return;
        }
        LegacyHomeStatusRepository.setInfoTips(this, resolveHomeResultTip(actionKey, result));
    }

    private String resolveHomeResultTip(String actionKey, ModuleRunResult result) {
        boolean success = result != null && result.isSuccess();
        if ("import_station_resources".equals(actionKey)) {
            return success ? "" : getString(R.string.home_info_tip_import_failed);
        }
        if ("export_station_resources".equals(actionKey)) {
            return success ? "" : getString(R.string.home_info_tip_export_failed);
        }
        if ("export_bundle".equals(actionKey)) {
            return success ? getString(R.string.home_info_tip_log_upload_complete) : getString(R.string.home_info_tip_log_upload_failed);
        }
        if ("install_local_apk".equals(actionKey)) {
            if (result != null && "未找到本地升级包".equals(result.getSummary())) {
                return getString(R.string.file_upgrade_not_found);
            }
            return getString(R.string.home_info_tip_upgrade_failed);
        }
        return compactResultText(result);
    }
}
