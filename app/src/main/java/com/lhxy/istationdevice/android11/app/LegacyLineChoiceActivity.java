package com.lhxy.istationdevice.android11.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lhxy.istationdevice.android11.domain.module.StationBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.util.ArrayList;
import java.util.List;

/**
 * 旧版线路选择页。
 * <p>
 * 真机前先恢复“选线路 -> 选方向 -> 确认写回统一状态”的离线闭环，
 * 真实线路数据库和触摸滚动行为后续再继续替换。
 */
public final class LegacyLineChoiceActivity extends LegacyBaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.act_linelist;
    }

    @Override
    protected int getTitleResId() {
        return R.string.line_choice_title;
    }

    @Override
    protected void onPageReady(@Nullable Bundle savedInstanceState) {
        LinearLayout emptyView = findViewById(R.id.lyNotLine);
        View titleBar = findViewById(R.id.rlLineInfoTitle);
        ListView listView = findViewById(R.id.lvLine);
        TextView emptyTip = findViewById(R.id.tvLineTip);
        Button affirmButton = findViewById(R.id.butAffirm);
        List<LegacyLineCatalog.LineProfile> lineProfiles = LegacyLineCatalog.all(this);

        if (lineProfiles.isEmpty()) {
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
            }
            if (emptyTip != null) {
                emptyTip.setText(R.string.line_not_tip);
            }
            if (affirmButton != null) {
                affirmButton.setEnabled(false);
            }
            return;
        }

        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (titleBar != null) {
            titleBar.setVisibility(View.VISIBLE);
        }
        if (listView != null) {
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_single_choice,
                    buildDisplayRows()
            ));
            int currentIndex = resolveCurrentLineIndex();
            listView.setItemChecked(currentIndex, true);
        }
        if (affirmButton != null) {
            affirmButton.setOnClickListener(v -> confirmSelection());
        }
    }

    private List<String> buildDisplayRows() {
        List<String> rows = new ArrayList<>();
        for (LegacyLineCatalog.LineProfile profile : LegacyLineCatalog.all(this)) {
            rows.add(profile.describeForList());
        }
        return rows;
    }

    private int resolveCurrentLineIndex() {
        String currentLine = resolveCurrentLineName();
        List<LegacyLineCatalog.LineProfile> lineProfiles = LegacyLineCatalog.all(this);
        for (int i = 0; i < lineProfiles.size(); i++) {
            if (lineProfiles.get(i).matchesLineName(currentLine)) {
                return i;
            }
        }
        return 0;
    }

    private String resolveCurrentLineName() {
        StationState stationState = requireStationState();
        if (stationState.getLineName() != null && !stationState.getLineName().trim().isEmpty()) {
            return LegacyLineCatalog.findByName(this, stationState.getLineName().trim()).getLineName();
        }
        LegacyStationResourceStateRepository.StationResourceState state = LegacyStationResourceStateRepository.getState(this);
        return LegacyLineCatalog.findByName(this, state.getLineName()).getLineName();
    }

    private void confirmSelection() {
        ListView listView = findViewById(R.id.lvLine);
        List<LegacyLineCatalog.LineProfile> lineProfiles = LegacyLineCatalog.all(this);
        int checkedPosition = listView == null ? 0 : listView.getCheckedItemPosition();
        if (checkedPosition < 0 || checkedPosition >= lineProfiles.size()) {
            checkedPosition = 0;
        }
        LegacyLineCatalog.LineProfile profile = lineProfiles.get(checkedPosition);
        final String[] directions = {"上行", "下行"};
        int checkedDirection = requireStationState().getDirectionText().contains("下") ? 1 : 0;
        new AlertDialog.Builder(this)
                .setTitle("请选择方向")
                .setSingleChoiceItems(directions, checkedDirection, null)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    int index = alertDialog.getListView().getCheckedItemPosition();
                    if (index < 0 || index >= directions.length) {
                        index = 0;
                    }
                    applySelection(profile, directions[index]);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void applySelection(LegacyLineCatalog.LineProfile profile, String direction) {
        StationState stationState = requireStationState();
        stationState.applyLineProfile(profile.getLineName(), direction, profile.stationsForDirection(direction));
        LegacyStationResourceStateRepository.updateLineSelection(this, "line-choice", profile.getLineName());
        Toast.makeText(
                this,
                profile.getLineName() + " / " + direction + " / " + profile.getLineAttribute(),
                Toast.LENGTH_SHORT
        ).show();
        finish();
    }

    private StationState requireStationState() {
        TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("station");
        if (module instanceof StationBusinessModule) {
            return ((StationBusinessModule) module).getStationState();
        }
        return new StationState();
    }
}
