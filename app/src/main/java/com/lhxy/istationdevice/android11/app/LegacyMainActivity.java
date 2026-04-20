package com.lhxy.istationdevice.android11.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 旧版首页骨架。
 * <p>
 * 这页先恢复旧终端首页的 UI 结构和主导航，
 * 深业务动作后面再逐项接入新模块。
 */
public final class LegacyMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        bindHeader();
        bindStaticStatus();
        bindActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateClock();
    }

    private void bindHeader() {
        TextView tvTitle = findViewById(R.id.tvToolbarTitle);
        if (tvTitle != null) {
            tvTitle.setText(getString(R.string.app_name));
        }
        updateClock();
    }

    private void updateClock() {
        TextView tvTime = findViewById(R.id.tvToolbarLeft);
        if (tvTime != null) {
            String value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            tvTime.setText(value);
        }
    }

    private void bindStaticStatus() {
        setText(R.id.tvSatellites, getString(R.string.main_satellites_value, "--"));
        setText(R.id.tvLanState, getString(R.string.main_lan_value, getString(R.string.unconnected)));
        setText(R.id.tvCMS, getString(R.string.main_cms_value, getString(R.string.unconnected)));
        setText(R.id.tvDVR, getString(R.string.main_dvr_value, getString(R.string.invalid)));
        setText(R.id.tv4G, getString(R.string.main_4g_value, "--"));
        setText(R.id.tvWifi, getString(R.string.main_wifi_value, "--"));
        setText(R.id.tvLocationGps, getString(R.string.main_gps_value, getString(R.string.invalid)));
        setText(R.id.tvLineName, getString(R.string.main_line_en, ""));
        setText(R.id.tvDriver, getString(R.string.main_driver, getString(R.string.undetected)));
        setText(R.id.tvSpeedLimit, "00");
        setText(R.id.tvVideoMileage, "0km/h");
        setText(R.id.tvVideoSpeedLimit, "0km/h");

        TextView tvPlannedTime = findViewById(R.id.tvPlannedTime1);
        if (tvPlannedTime != null) {
            String html = getString(R.string.main_planned_time, "--", "-- : --");
            tvPlannedTime.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        }
    }

    private void bindActions() {
        Button btnKeyboard = findViewById(R.id.butKeyboard);
        Button btnEsc = findViewById(R.id.butESC);
        Button btnMenu = findViewById(R.id.butMenu);
        Button btnVideo = findViewById(R.id.butVideo);
        LinearLayout operationPanel = findViewById(R.id.rlOperation);
        LinearLayout keyboardPanel = findViewById(R.id.lyNumberkeyboard);

        if (btnKeyboard != null && operationPanel != null && keyboardPanel != null) {
            btnKeyboard.setOnClickListener(v -> {
                keyboardPanel.setVisibility(View.VISIBLE);
                operationPanel.setVisibility(View.GONE);
            });
        }
        if (btnEsc != null && operationPanel != null && keyboardPanel != null) {
            btnEsc.setOnClickListener(v -> {
                keyboardPanel.setVisibility(View.GONE);
                operationPanel.setVisibility(View.VISIBLE);
            });
        }
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> startActivity(new Intent(this, LegacyLoginActivity.class)));
        }
        if (btnVideo != null) {
            btnVideo.setOnClickListener(v -> {
                CharSequence current = btnVideo.getText();
                if ("报警开".contentEquals(current)) {
                    btnVideo.setText("报警关");
                } else {
                    btnVideo.setText("报警开");
                }
            });
        }
    }

    private void setText(int id, CharSequence value) {
        TextView view = findViewById(id);
        if (view != null) {
            view.setText(value);
        }
    }
}
