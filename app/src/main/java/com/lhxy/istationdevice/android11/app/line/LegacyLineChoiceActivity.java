package com.lhxy.istationdevice.android11.app.line;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;
import com.lhxy.istationdevice.android11.app.common.LegacyWheelPickerDialog;
import com.lhxy.istationdevice.android11.app.station.LegacyStationResourceStateRepository;
import com.lhxy.istationdevice.android11.domain.module.StationBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.util.Arrays;
import java.util.List;

/**
 * 旧版线路选择页。
 * <p>
 * 真机前先恢复“选线路 -> 选方向 -> 确认写回统一状态”的离线闭环，
 * 真实线路数据库和触摸滚动行为后续再继续替换。
 */
public final class LegacyLineChoiceActivity extends LegacyBaseActivity {
    private LegacyLineChoiceAdapter lineChoiceAdapter;
    private int selectedLineIndex;

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
        View titleBar = findViewById(R.id.lyLineInfoTitle);
        View affirmPanel = findViewById(R.id.rlAffirm);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewLine);
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
            if (recyclerView != null) {
                recyclerView.setVisibility(View.GONE);
            }
            if (affirmPanel != null) {
                affirmPanel.setVisibility(View.GONE);
            }
            return;
        }

        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (titleBar != null) {
            titleBar.setVisibility(View.VISIBLE);
        }
        if (affirmPanel != null) {
            affirmPanel.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            selectedLineIndex = resolveCurrentLineIndex();
            lineChoiceAdapter = new LegacyLineChoiceAdapter(this, lineProfiles, selectedLineIndex, position -> {
                selectedLineIndex = position;
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            if (recyclerView.getItemDecorationCount() == 0) {
                recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            }
            recyclerView.setAdapter(lineChoiceAdapter);
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (affirmButton != null) {
            affirmButton.setEnabled(selectedLineIndex >= 0);
            affirmButton.setOnClickListener(v -> {
                if (selectedLineIndex < 0) {
                    Toast.makeText(this, R.string.line_choice_pick_first, Toast.LENGTH_SHORT).show();
                    return;
                }
                confirmSelection();
            });
        }
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
        List<LegacyLineCatalog.LineProfile> lineProfiles = LegacyLineCatalog.all(this);
        if (selectedLineIndex < 0 || selectedLineIndex >= lineProfiles.size()) {
            selectedLineIndex = 0;
        }
        LegacyLineCatalog.LineProfile profile = lineProfiles.get(selectedLineIndex);
        List<String> directions = Arrays.asList(
                getString(R.string.collection_direction_upstream),
                getString(R.string.collection_direction_down)
        );
        int checkedDirection = resolveCurrentDirectionText().contains("下") ? 1 : 0;
        LegacyWheelPickerDialog.show(
                this,
                getString(R.string.collection_select_direction),
                directions,
                checkedDirection,
                (selectedIndex, selectedValue) -> applySelection(profile, selectedValue)
        );
    }

    private void applySelection(LegacyLineCatalog.LineProfile profile, String direction) {
        StationState stationState = requireStationState();
        stationState.applyLineProfile(profile.getLineName(), direction, profile.stationsForDirection(direction));
        stationState.setLineAttribute(profile.getLineAttribute());
        LegacyStationResourceStateRepository.updateRouteSelection(
                this,
                "line-choice",
                profile.getLineName(),
                direction,
                profile.getLineAttribute()
        );
        Toast.makeText(this, R.string.line_line_switch_suc, Toast.LENGTH_SHORT).show();
        finish();
    }

    private String resolveCurrentDirectionText() {
        StationState stationState = requireStationState();
        LegacyStationResourceStateRepository.StationResourceState resourceState = LegacyStationResourceStateRepository.getState(this);
        if (resourceState.isImported()
                && resourceState.getLineName() != null
                && !"-".equals(resourceState.getLineName().trim())
                && !LegacyLineCatalog.findByName(this, resourceState.getLineName()).matchesLineName(stationState.getLineName())) {
            return valueOrDefault(resourceState.getDirectionText(), "上行");
        }
        return valueOrDefault(stationState.getDirectionText(), valueOrDefault(resourceState.getDirectionText(), "上行"));
    }

    private String valueOrDefault(String value, String fallback) {
        if (value != null && !value.trim().isEmpty() && !"-".equals(value.trim())) {
            return value.trim();
        }
        return fallback;
    }

    private StationState requireStationState() {
        TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("station");
        if (module instanceof StationBusinessModule) {
            return ((StationBusinessModule) module).getStationState();
        }
        return new StationState();
    }
}
