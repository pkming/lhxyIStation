package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.AppLogEntry;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 旧版信息浏览页。
 * <p>
 * 先承接离线可见的运行摘要和最近日志，
 * 真机协议消息后面继续叠到同一页，不再保留空壳。
 */
public final class LegacyInfoBrowsActivity extends LegacyBaseActivity {
    private final List<String> lines = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_infobrows;
    }

    @Override
    protected int getTitleResId() {
        return R.string.infobrows_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        ListView listView = findViewById(R.id.lvInfoBrows);
        LinearLayout emptyView = findViewById(R.id.lyNotInfo);
        if (listView != null) {
            adapter = new ArrayAdapter<>(this, R.layout.item_legacy_info_line, R.id.tvInfoLine, lines);
            listView.setAdapter(adapter);
            if (emptyView != null) {
                listView.setEmptyView(emptyView);
            }
        }
        Button affirmButton = findViewById(R.id.butAffirm);
        if (affirmButton != null) {
            affirmButton.setText(R.string.legacy_refresh);
            affirmButton.setOnClickListener(v -> refreshContent(true));
        }
        refreshContent(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContent(false);
    }

    private void refreshContent(boolean notify) {
        lines.clear();
        lines.addAll(buildLines());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (notify) {
            Toast.makeText(this, "信息浏览已刷新。", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> buildLines() {
        List<String> result = new ArrayList<>();
        ShellRuntime runtime = ShellRuntime.get();
        ShellConfig config = runtime.getActiveConfig();
        if (config == null) {
            config = ShellConfigRepository.get(this);
            runtime.applyConfig(this, config);
        }
        LegacyStationResourceStateRepository.StationResourceState resourceState =
                LegacyStationResourceStateRepository.getState(this);

        result.add("运行概览");
        result.add("配置: " + safe(config.getDeviceProfile()) + " / 版本 " + safe(config.getConfigVersion()));
        result.add("资源导入: " + (resourceState.isImported() ? "已导入" : "未导入"));
        result.add("当前线路: " + safe(resourceState.getLineName()) + " / 来源 " + safe(resourceState.getSource()));
        result.add("最近更新: " + formatTime(resourceState.getUpdatedAt()));
        appendMultiline(result, runtime.describeFoundationStatus());
        appendMultiline(result, runtime.getModuleHub().describeStatus());

        result.add("最近日志");
        List<AppLogEntry> snapshot = AppLogCenter.snapshot();
        if (snapshot.isEmpty()) {
            result.add("暂无运行日志。");
            return result;
        }
        int start = Math.max(0, snapshot.size() - 40);
        List<AppLogEntry> recentEntries = new ArrayList<>(snapshot.subList(start, snapshot.size()));
        Collections.reverse(recentEntries);
        for (AppLogEntry entry : recentEntries) {
            result.add(entry.toDisplayLine());
        }
        return result;
    }

    private void appendMultiline(List<String> target, String block) {
        if (block == null || block.trim().isEmpty()) {
            return;
        }
        String[] rows = block.trim().split("\\n");
        for (String row : rows) {
            String line = row == null ? "" : row.trim();
            if (!line.isEmpty()) {
                target.add(line);
            }
        }
    }

    private String formatTime(long timeMillis) {
        if (timeMillis <= 0L) {
            return "-";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timeMillis));
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
