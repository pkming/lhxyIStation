package com.lhxy.istationdevice.android11.app.file;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.lhxy.istationdevice.android11.domain.file.StationResourceArchiveUseCase;
import com.lhxy.istationdevice.android11.domain.module.FileBusinessModule;
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
        bindCheckUpdateAction();
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
        button.setOnClickListener(v -> promptImportCandidateSelection());
    }

    private void promptImportCandidateSelection() {
        FileBusinessModule fileBusinessModule = resolveFileBusinessModule();
        if (fileBusinessModule == null) {
            confirmAndRunFileAction(
                    R.string.file_import_tip,
                    R.string.file_import_load_tip,
                    "import_station_resources"
            );
            return;
        }
        java.util.List<StationResourceArchiveUseCase.ImportCandidate> candidates = fileBusinessModule.listImportCandidates();
        if (candidates.isEmpty()) {
            fileBusinessModule.setPendingImportCandidatePath(null);
            confirmAndRunFileAction(
                    R.string.file_import_tip,
                    R.string.file_import_load_tip,
                    "import_station_resources"
            );
            return;
        }
        if (candidates.size() == 1) {
            fileBusinessModule.setPendingImportCandidatePath(candidates.get(0).getAbsolutePath());
            confirmAndRunFileAction(
                    R.string.file_import_tip,
                    R.string.file_import_load_tip,
                    "import_station_resources"
            );
            return;
        }

        CharSequence[] items = new CharSequence[candidates.size()];
        int defaultIndex = 0;
        for (int index = 0; index < candidates.size(); index++) {
            StationResourceArchiveUseCase.ImportCandidate candidate = candidates.get(index);
            items[index] = buildImportCandidateLabel(candidate);
            if (defaultIndex == 0 && candidate.isZip()) {
                defaultIndex = index;
            }
        }
        final int[] selectedIndex = {defaultIndex};
        new AlertDialog.Builder(this)
                .setTitle(R.string.file_import_choose_package_title)
                .setSingleChoiceItems(items, defaultIndex, (dialog, which) -> selectedIndex[0] = which)
                .setPositiveButton(R.string.file_import_choose_package_confirm, (dialog, which) -> {
                    StationResourceArchiveUseCase.ImportCandidate selected = candidates.get(selectedIndex[0]);
                    fileBusinessModule.setPendingImportCandidatePath(selected.getAbsolutePath());
                    runFileActionAsync("import_station_resources", getString(R.string.file_import_load_tip));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private FileBusinessModule resolveFileBusinessModule() {
        TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("file");
        if (module instanceof FileBusinessModule) {
            return (FileBusinessModule) module;
        }
        return null;
    }

    private CharSequence buildImportCandidateLabel(StationResourceArchiveUseCase.ImportCandidate candidate) {
        StringBuilder builder = new StringBuilder();
        builder.append(candidate.getFileName())
                .append("  ")
                .append(candidate.getArchiveTypeLabel())
                .append(candidate.isSupported()
                        ? (candidate.isRar() ? " / 可导入(旧格式)" : " / 可导入")
                        : " / 当前不支持");
        if (!candidate.isSupported() && candidate.getUnsupportedReason() != null && !candidate.getUnsupportedReason().trim().isEmpty()) {
            builder.append("\n限制：").append(candidate.getUnsupportedReason().trim());
        }
        String sourceLabel = candidate.getSourceLabel();
        if (sourceLabel != null && !sourceLabel.trim().isEmpty()) {
            builder.append("\n来源：").append(sourceLabel.trim());
        }
        builder.append("\n大小：").append(formatFileSize(candidate.getFileSize()));
        if (candidate.getLastModified() > 0) {
            builder.append("  修改：")
                    .append(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(candidate.getLastModified()));
        }
        return builder.toString();
    }

    private String formatFileSize(long fileSize) {
        if (fileSize <= 0) {
            return "0 B";
        }
        if (fileSize < 1024L) {
            return fileSize + " B";
        }
        if (fileSize < 1024L * 1024L) {
            return String.format(java.util.Locale.getDefault(), "%.1f KB", fileSize / 1024d);
        }
        return String.format(java.util.Locale.getDefault(), "%.2f MB", fileSize / (1024d * 1024d));
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

    private void bindCheckUpdateAction() {
        Button button = findViewById(R.id.butCheckUpdate);
        if (button == null) {
            return;
        }
        applyButtonState(button, true);
        button.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage(R.string.file_check_hot_update_tip)
                .setPositiveButton(R.string.confirm, (dialog, which) -> runCheckHotUpdateAsync())
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
                maybeShowImportDiagnostics(actionKey, result);
            });
        }, "legacy-file-manage-" + actionKey).start();
    }

    private void maybeShowImportDiagnostics(String actionKey, ModuleRunResult result) {
        if (!"import_station_resources".equals(actionKey) || result == null || !result.hasDiagnostics() || isFinishing()) {
            return;
        }
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_file_import_diagnostics, null, false);
        TextView summaryView = dialogView.findViewById(R.id.tvImportDiagnosticsSummary);
        LinearLayout itemsContainer = dialogView.findViewById(R.id.llImportDiagnosticsItems);
        if (summaryView != null) {
            summaryView.setText(buildDiagnosticsSummary(result));
        }
        if (itemsContainer != null) {
            itemsContainer.removeAllViews();
            for (ModuleRunResult.DiagnosticItem item : result.getDiagnostics()) {
                itemsContainer.addView(createDiagnosticItemView(item));
            }
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.file_import_diagnostics_title)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private CharSequence buildDiagnosticsSummary(ModuleRunResult result) {
        int okCount = 0;
        int warnCount = 0;
        int failCount = 0;
        for (ModuleRunResult.DiagnosticItem item : result.getDiagnostics()) {
            if (ModuleRunResult.DiagnosticItem.LEVEL_OK.equals(item.getLevel())) {
                okCount++;
            } else if (ModuleRunResult.DiagnosticItem.LEVEL_FAIL.equals(item.getLevel())) {
                failCount++;
            } else {
                warnCount++;
            }
        }
        return getString(
                R.string.file_import_diagnostics_summary,
                result.getSummary(),
                okCount,
                warnCount,
            failCount
        );
    }

    private View createDiagnosticItemView(ModuleRunResult.DiagnosticItem item) {
        boolean isFailure = ModuleRunResult.DiagnosticItem.LEVEL_FAIL.equals(item.getLevel());
        boolean showMessageBlock = !ModuleRunResult.DiagnosticItem.LEVEL_OK.equals(item.getLevel())
            && item.getMessage() != null
            && !item.getMessage().trim().isEmpty();
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(14), isFailure ? dp(12) : dp(10), dp(14), isFailure ? dp(12) : dp(10));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = dp(6);
        container.setLayoutParams(params);
        container.setBackgroundColor(ContextCompat.getColor(this, R.color.c_f6f6f6));

        TextView titleView = new TextView(this);
        titleView.setTextSize(15f);
        titleView.setTextColor(resolveDiagnosticColor(item.getLevel()));
        if (showMessageBlock) {
            titleView.setText("[" + item.getLevel() + "] " + compactDiagnosticTarget(item.getTarget()));
        } else {
            titleView.setEllipsize(TextUtils.TruncateAt.END);
            titleView.setSingleLine(true);
            titleView.setText("[" + item.getLevel() + "] " + buildCompactDiagnosticLine(item));
        }
        container.addView(titleView);

        if (showMessageBlock) {
            TextView messageView = new TextView(this);
            messageView.setTextSize(14f);
            messageView.setTextColor(ContextCompat.getColor(this, R.color.c_333333));
            messageView.setPadding(0, dp(6), 0, 0);
            messageView.setText(item.getMessage());
            container.addView(messageView);
        }

        return container;
    }

    private String buildCompactDiagnosticLine(ModuleRunResult.DiagnosticItem item) {
        String target = compactDiagnosticTarget(item.getTarget());
        String message = normalizeSingleLine(item.getMessage());
        if (target.isEmpty()) {
            return message;
        }
        if (message.isEmpty()) {
            return target;
        }
        return target + "  " + message;
    }

    private String compactDiagnosticTarget(String target) {
        if (target == null || target.trim().isEmpty()) {
            return "-";
        }
        String normalized = target.trim().replace('\\', '/');
        String[] parts = normalized.split("/");
        if (parts.length == 0) {
            return normalized;
        }
        String last = parts[parts.length - 1];
        if (last.endsWith(".csv") || last.endsWith(".xls") || last.endsWith(".xlsx")) {
            if (parts.length >= 2) {
                return parts[parts.length - 2] + "/" + last;
            }
            return last;
        }
        return last;
    }

    private String normalizeSingleLine(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return value.replace('\n', ' ')
                .replace('\r', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    private int resolveDiagnosticColor(String level) {
        if (ModuleRunResult.DiagnosticItem.LEVEL_FAIL.equals(level)) {
            return ContextCompat.getColor(this, R.color.c_b31800);
        }
        if (ModuleRunResult.DiagnosticItem.LEVEL_OK.equals(level)) {
            return ContextCompat.getColor(this, R.color.c_66cdaa);
        }
        return ContextCompat.getColor(this, R.color.c_8b6914);
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
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

    private void runCheckHotUpdateAsync() {
        TextView tips = findViewById(R.id.tvFileTips);
        if (tips != null) {
            tips.setVisibility(View.VISIBLE);
            tips.setText(R.string.file_check_hot_update_load_tip);
        }
        publishHomeLoadingState("check_hot_update");
        new Thread(() -> {
            ModuleRunResult result = ShellRuntime.get()
                    .getModuleHub()
                    .runAction("upgrade", "check_hot_update", TraceIds.next("legacy-file-manage-hot-update"));
            runOnUiThread(() -> {
                publishHomeResultState("check_hot_update", result);
                renderResourceStatus(result == null ? "" : result.describeBlock());
                refreshFileActionState();
            });
        }, "legacy-file-manage-hot-update").start();
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
        if ("check_hot_update".equals(actionKey)) {
            return getString(R.string.home_info_tip_hot_update_loading);
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
        if ("check_hot_update".equals(actionKey)) {
            return result != null && result.isSuccess()
                    ? compactResultText(result)
                    : getString(R.string.home_info_tip_hot_update_failed);
        }
        return compactResultText(result);
    }
}
