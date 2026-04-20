package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.StationBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.text.DateFormat;

/**
 * 旧版文件管理页骨架。
 */
public final class LegacyFileManageActivity extends LegacyBaseActivity {
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
        bindAction(R.id.butUpgrade, R.string.file_udisk_upgrade, R.string.file_upgrade_tip);
        bindAction(R.id.butExportLog, R.string.file_export_log, R.string.file_export_log_tip);
        renderResourceStatus(null);
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
        new Thread(() -> {
            TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("file");
            ModuleRunResult result = module == null
                    ? ModuleRunResult.failure("file", "文件", "文件模块未注册", "当前没有找到 file 模块")
                    : module.runAction(actionKey, TraceIds.next("legacy-file-manage-" + actionKey));
            runOnUiThread(() -> {
                ShellRuntime.get().applyConfig(this, ShellConfigRepository.get(this));
                TerminalBusinessModule stationModule = ShellRuntime.get().getModuleHub().findModule("station");
                if (stationModule instanceof StationBusinessModule) {
                    LegacyStationResourceStateRepository.StationResourceState state = LegacyStationResourceStateRepository.getState(this);
                    ((StationBusinessModule) stationModule).getStationState().setLineName(state.getLineName());
                }
                TextView fileTips = findViewById(R.id.tvFileTips);
                if (fileTips != null) {
                    fileTips.setVisibility(View.VISIBLE);
                }
                renderResourceStatus(result.describeBlock());
            });
        }, "legacy-file-manage-" + actionKey).start();
    }

    private String resolveCurrentLineName() {
        TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("station");
        if (module instanceof StationBusinessModule) {
            StationState stationState = ((StationBusinessModule) module).getStationState();
            String lineName = stationState.getLineName();
            if (lineName != null && !lineName.trim().isEmpty()) {
                return lineName.trim();
            }
        }
        return "101路";
    }
}
