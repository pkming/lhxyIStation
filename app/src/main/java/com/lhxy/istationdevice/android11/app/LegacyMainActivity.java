package com.lhxy.istationdevice.android11.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.domain.module.DispatchBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.SignInBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.StationBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 旧版首页骨架。
 * <p>
 * 这页先恢复旧终端首页的 UI 结构和主导航，
 * 深业务动作后面再逐项接入新模块。
 */
public final class LegacyMainActivity extends AppCompatActivity {
    private static final int STATION_PREVIEW_NONE = 0;
    private static final int STATION_PREVIEW_NEXT = 1;
    private static final int STATION_PREVIEW_PREVIOUS = 2;

    private final ShellRuntime shellRuntime = ShellRuntime.get();
    private int stationPreviewMode = STATION_PREVIEW_NONE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        bindHeader();
        bindActions();
        refreshHomeState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHomeState();
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

    private void refreshHomeState() {
        updateClock();
        ShellConfig config = shellRuntime.getActiveConfig();
        if (config == null) {
            config = ShellConfigRepository.get(this);
            shellRuntime.applyConfig(this, config);
        }
        StationState stationState = requireStationState();
        SignInState signInState = requireSignInState();
        DispatchState dispatchState = requireDispatchState();
        GpsFixSnapshot gpsSnapshot = shellRuntime.getGpsSerialMonitor().getLatestSnapshot();
        LegacyStationResourceStateRepository.StationResourceState resourceState = LegacyStationResourceStateRepository.getState(this);
        syncImportedLineProfile(stationState, resourceState);

        setText(R.id.tvSatellites, getString(R.string.main_satellites_value, resolveSatellites(gpsSnapshot, stationState)));
        setText(R.id.tvLanState, getString(R.string.main_lan_value, resolveLanState(config)));
        setText(R.id.tvCMS, getString(R.string.main_cms_value, resolveCmsState(config, dispatchState)));
        setText(R.id.tvDVR, getString(R.string.main_dvr_value, shellRuntime.getCameraAdapter().isAvailable()
                ? getString(R.string.effective)
                : getString(R.string.invalid)));
        setText(R.id.tv4G, getString(R.string.main_4g_value, "--"));
        setText(R.id.tvWifi, getString(R.string.main_wifi_value, "--"));
        setText(R.id.tvLocationGps, getString(R.string.main_gps_value, resolveGpsState(gpsSnapshot)));
        setText(R.id.tvLineName, getString(R.string.main_line_en, resolveLineName(stationState, resourceState)));
        setText(R.id.tvLineNO, resolveStationNo(stationState));
        setText(R.id.tvLineDirection, valueOrDefault(stationState.getDirectionText(), "--"));
        setText(R.id.tvDriver, getString(R.string.main_driver, resolveDriverName(signInState)));
        setText(R.id.tvSpeedLimit, "00");
        setText(R.id.tvVideoMileage, valueOrDefault(gpsSnapshot == null ? null : gpsSnapshot.getSpeedKnots(), "0") + "km/h");
        setText(R.id.tvVideoSpeedLimit, "0km/h");
        setText(R.id.tvShouting, resolveDispatchOwner(config));
        setText(R.id.tvInfoTips, resolveInfoTips(dispatchState, stationState));
        setText(R.id.tvHomeNextStation, resolveStationPreviewLabel());
        TextView tvPlannedTime = findViewById(R.id.tvPlannedTime1);
        if (tvPlannedTime != null) {
            String html = getString(
                    R.string.main_planned_time,
                    resolveTripNo(stationState),
                    resolvePlannedTime(dispatchState)
            );
            tvPlannedTime.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        }
        String homeStationName = resolveStationPreviewValue(stationState);
        renderNameContainer(R.id.rlHomeNextStationName, homeStationName);
        renderNameContainer(R.id.rlEndBusName, valueOrDefault(stationState.getTerminalStation(), "--"));
        renderRouteSummary(stationState, gpsSnapshot);
    }

    private void bindActions() {
        Button btnKeyboard = findViewById(R.id.butKeyboard);
        Button btnEsc = findViewById(R.id.butESC);
        Button btnNewspaperStation = findViewById(R.id.butNewspaperStation);
        Button btnLineChoice = findViewById(R.id.butRepeat);
        Button btnSwitch = findViewById(R.id.butSwitch);
        Button btnCease = findViewById(R.id.butCease);
        Button btnMenu = findViewById(R.id.butMenu);
        Button btnVideo = findViewById(R.id.butVideo);
        Button btnNumberF1 = findViewById(R.id.butNumberF1);
        Button btnNumberF2 = findViewById(R.id.butNumberF2);
        Button btnNumberF3 = findViewById(R.id.butNumberF3);
        Button btnNumberF4 = findViewById(R.id.butNumberF4);
        Button btnNumberF5 = findViewById(R.id.butNumberF5);
        Button btnNumberF6 = findViewById(R.id.butNumberF6);
        Button btnNumberF7 = findViewById(R.id.butNumberF7);
        Button btnNumberF8 = findViewById(R.id.butNumberF8);
        Button btnNumberF9 = findViewById(R.id.butNumberF9);
        Button btnNumberF0 = findViewById(R.id.butNumberF0);
        Button btnMainUp = findViewById(R.id.butMainUp);
        Button btnMainDown = findViewById(R.id.butMainDown);
        Button btnMessage = findViewById(R.id.butMainMsg);
        Button btnQuery = findViewById(R.id.butMainQuery);
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
        if (btnNewspaperStation != null) {
            btnNewspaperStation.setOnClickListener(v -> runStationAction("advance_station"));
        }
        if (btnLineChoice != null) {
            btnLineChoice.setOnClickListener(v -> startActivity(new Intent(this, LegacyLineChoiceActivity.class)));
        }
        if (btnSwitch != null) {
            btnSwitch.setOnClickListener(v -> switchDirection());
        }
        if (btnCease != null) {
            btnCease.setOnClickListener(v -> runStationAction("stop_station"));
        }
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> startActivity(new Intent(this, LegacyLoginActivity.class)));
        }
        if (btnVideo != null) {
            btnVideo.setOnClickListener(v -> startActivity(LegacyVideoMonitorActivity.createIntent(this, "home-dvr")));
        }
        if (btnMessage != null) {
            btnMessage.setOnClickListener(v -> startActivity(new Intent(this, LegacyInfoBrowsActivity.class)));
        }
        if (btnQuery != null) {
            btnQuery.setOnClickListener(v -> startActivity(new Intent(this, LegacyDispatchCenterActivity.class)));
        }
        bindServiceToneButton(btnNumberF1, 1);
        bindServiceToneButton(btnNumberF2, 2);
        bindServiceToneButton(btnNumberF3, 3);
        bindServiceToneButton(btnNumberF4, 4);
        bindServiceToneButton(btnNumberF5, 5);
        bindServiceToneButton(btnNumberF6, 6);
        bindServiceToneButton(btnNumberF7, 7);
        bindServiceToneButton(btnNumberF8, 8);
        bindServiceToneButton(btnNumberF9, 9);
        bindServiceToneButton(btnNumberF0, 0);
        if (btnMainUp != null) {
            btnMainUp.setOnClickListener(v -> quickPreviewForward());
        }
        if (btnMainDown != null) {
            btnMainDown.setOnClickListener(v -> quickPreviewBackward());
        }
    }

    private void setText(int id, CharSequence value) {
        TextView view = findViewById(id);
        if (view != null) {
            view.setText(value);
        }
    }

    private void runStationAction(String actionKey) {
        ModuleRunResult result = shellRuntime.getModuleHub().runAction("station", actionKey, "legacy-main-" + actionKey);
        Toast.makeText(this, result.describeInline(), Toast.LENGTH_SHORT).show();
        stationPreviewMode = STATION_PREVIEW_NONE;
        refreshHomeState();
    }

    private void bindServiceToneButton(@Nullable Button button, int number) {
        if (button == null) {
            return;
        }
        button.setOnClickListener(v -> Toast.makeText(
                this,
                "服务音 " + number + " 已触发，真机音频/485 联动待验证。",
                Toast.LENGTH_SHORT
        ).show());
    }

    private void quickPreviewForward() {
        StationState stationState = requireStationState();
        if (stationPreviewMode != STATION_PREVIEW_NEXT
                && stationState.getNextStation() != null
                && !"-".equals(stationState.getNextStation())) {
            stationPreviewMode = STATION_PREVIEW_NEXT;
            refreshHomeState();
            return;
        }
        runStationAction("advance_station");
    }

    private void quickPreviewBackward() {
        if (stationPreviewMode != STATION_PREVIEW_NONE) {
            stationPreviewMode = STATION_PREVIEW_NONE;
            refreshHomeState();
            return;
        }
        StationState stationState = requireStationState();
        if (!"-".equals(stationState.getPreviousStation())) {
            stationPreviewMode = STATION_PREVIEW_PREVIOUS;
            refreshHomeState();
            return;
        }
        Toast.makeText(this, "当前没有上一站可回看。", Toast.LENGTH_SHORT).show();
    }

    private void switchDirection() {
        StationState stationState = requireStationState();
        String nextDirection = stationState.getDirectionText().contains("下") ? "上行" : "下行";
        LegacyLineCatalog.LineProfile profile = LegacyLineCatalog.findByName(this, resolveLineName(
                stationState,
                LegacyStationResourceStateRepository.getState(this)
        ));
        stationState.applyLineProfile(profile.getLineName(), nextDirection, profile.stationsForDirection(nextDirection));
        LegacyStationResourceStateRepository.updateLineSelection(this, "main-switch-direction", profile.getLineName());
        stationPreviewMode = STATION_PREVIEW_NONE;
        Toast.makeText(this, profile.getLineName() + " 已切到" + nextDirection, Toast.LENGTH_SHORT).show();
        refreshHomeState();
    }

    private String resolveStationPreviewLabel() {
        if (stationPreviewMode == STATION_PREVIEW_NEXT) {
            return getString(R.string.main_next_station);
        }
        if (stationPreviewMode == STATION_PREVIEW_PREVIOUS) {
            return getString(R.string.main_previous_station);
        }
        return getString(R.string.main_home_station);
    }

    private String resolveStationPreviewValue(StationState stationState) {
        if (stationPreviewMode == STATION_PREVIEW_NEXT) {
            return valueOrDefault(stationState.getNextStation(), valueOrDefault(stationState.getCurrentStation(), "--"));
        }
        if (stationPreviewMode == STATION_PREVIEW_PREVIOUS) {
            return valueOrDefault(stationState.getPreviousStation(), valueOrDefault(stationState.getCurrentStation(), "--"));
        }
        return valueOrDefault(stationState.getCurrentStation(), "--");
    }

    private StationState requireStationState() {
        TerminalBusinessModule module = shellRuntime.getModuleHub().findModule("station");
        if (module instanceof StationBusinessModule) {
            return ((StationBusinessModule) module).getStationState();
        }
        return new StationState();
    }

    @Nullable
    private DispatchState requireDispatchState() {
        TerminalBusinessModule module = shellRuntime.getModuleHub().findModule("dispatch");
        return module instanceof DispatchBusinessModule
                ? ((DispatchBusinessModule) module).getDispatchState()
                : null;
    }

    @Nullable
    private SignInState requireSignInState() {
        TerminalBusinessModule module = shellRuntime.getModuleHub().findModule("signin");
        return module instanceof SignInBusinessModule
                ? ((SignInBusinessModule) module).getSignInState()
                : null;
    }

    private String resolveSatellites(@Nullable GpsFixSnapshot snapshot, @NonNull StationState stationState) {
        if (snapshot != null && snapshot.isValid()) {
            return String.valueOf(snapshot.getUsedSatellites());
        }
        return stationState.getSatellites() > 0 ? String.valueOf(stationState.getSatellites()) : "--";
    }

    private String resolveLanState(@Nullable ShellConfig config) {
        if (config == null) {
            return getString(R.string.unconnected);
        }
        for (ShellConfig.SocketChannel channel : config.getSocketChannels().values()) {
            if (shellRuntime.getSocketClientAdapter().isConnected(channel.getChannelName())) {
                return getString(R.string.connected);
            }
        }
        return getString(R.string.unconnected);
    }

    private String resolveCmsState(@Nullable ShellConfig config, @Nullable DispatchState dispatchState) {
        if (config != null && config.getBasicSetupConfig().getProtocolLinkageSettings().isSerialDispatchEnabled()) {
            return "串口";
        }
        if (dispatchState != null && dispatchState.getActiveProtocol() != null && !dispatchState.getActiveProtocol().trim().isEmpty()) {
            return dispatchState.getActiveProtocol().trim();
        }
        return getString(R.string.unconnected);
    }

    private String resolveGpsState(@Nullable GpsFixSnapshot snapshot) {
        return snapshot != null && snapshot.isValid()
                ? getString(R.string.effective)
                : getString(R.string.invalid);
    }

    private String resolveLineName(@NonNull StationState stationState, @NonNull LegacyStationResourceStateRepository.StationResourceState resourceState) {
        if (stationState.getLineName() != null && !stationState.getLineName().trim().isEmpty()) {
            return LegacyLineCatalog.findByName(this, stationState.getLineName().trim()).getLineName();
        }
        if (resourceState.isImported() && resourceState.getLineName() != null && !resourceState.getLineName().trim().isEmpty()) {
            return LegacyLineCatalog.findByName(this, resourceState.getLineName().trim()).getLineName();
        }
        return "";
    }

    private String resolveStationNo(@NonNull StationState stationState) {
        return stationState.getReportCount() > 0
                ? getString(R.string.main_no, String.valueOf(stationState.getReportCount()))
                : getString(R.string.main_no_start);
    }

    private String resolveDriverName(@Nullable SignInState signInState) {
        if (signInState == null || !signInState.isSignedIn()) {
            return getString(R.string.undetected);
        }
        return valueOrDefault(signInState.getDriverName(), signInState.getCardNo());
    }

    private String resolveDispatchOwner(@Nullable ShellConfig config) {
        if (config == null) {
            return "调度待配置";
        }
        String owner = config.getBasicSetupConfig().getProtocolLinkageSettings().getDispatchOwner();
        if ("serial_rs232_1".equals(owner)) {
            return "调度:串口";
        }
        return "调度:网络";
    }

    private String resolveInfoTips(@Nullable DispatchState dispatchState, @NonNull StationState stationState) {
        if (dispatchState != null && dispatchState.getDispatchMessage() != null && !dispatchState.getDispatchMessage().trim().isEmpty()) {
            return dispatchState.getDispatchMessage().trim();
        }
        return valueOrDefault(stationState.getReportPhase(), "等待业务动作");
    }

    private String resolveTripNo(@NonNull StationState stationState) {
        return stationState.getReportCount() > 0 ? String.valueOf(stationState.getReportCount()) : "--";
    }

    private String resolvePlannedTime(@Nullable DispatchState dispatchState) {
        if (dispatchState == null) {
            return "-- : --";
        }
        return valueOrDefault(dispatchState.getPlannedDepartureTime(), "-- : --");
    }

    private void renderNameContainer(int containerId, String value) {
        ViewGroup container = findViewById(containerId);
        if (container == null) {
            return;
        }
        container.removeAllViews();
        TextView textView = new TextView(this);
        textView.setText(value);
        textView.setTextSize(36f);
        textView.setTextColor(ContextCompat.getColor(this, R.color.c_f1ff2d));
        container.addView(textView);
    }

    private void renderRouteSummary(@NonNull StationState stationState, @Nullable GpsFixSnapshot snapshot) {
        LinearLayout container = findViewById(R.id.lyLine);
        if (container == null) {
            return;
        }
        container.removeAllViews();
        LegacyLineCatalog.LineProfile profile = LegacyLineCatalog.findByName(
                this,
                valueOrDefault(stationState.getLineName(), LegacyLineCatalog.first(this).getLineName())
        );
        addRouteSummaryLine(container, "线路 " + valueOrDefault(stationState.getLineName(), "--"));
        addRouteSummaryLine(container, "属性 " + profile.getLineAttribute());
        addRouteSummaryLine(container, "概要 " + profile.getLineInfo());
        addRouteSummaryLine(container, "方向 " + valueOrDefault(stationState.getDirectionText(), "--"));
        addRouteSummaryLine(container, "本站 " + valueOrDefault(stationState.getCurrentStation(), "--"));
        addRouteSummaryLine(container, "下站 " + valueOrDefault(stationState.getNextStation(), "--"));
        addRouteSummaryLine(container, "阶段 " + valueOrDefault(stationState.getReportPhase(), "--"));
        if (snapshot != null && snapshot.isValid()) {
            addRouteSummaryLine(
                    container,
                    "定位 " + valueOrDefault(snapshot.getLatitudeDecimal(), "-") + " / " + valueOrDefault(snapshot.getLongitudeDecimal(), "-")
            );
        }
    }

    private void syncImportedLineProfile(
            @NonNull StationState stationState,
            @NonNull LegacyStationResourceStateRepository.StationResourceState resourceState
    ) {
        String preferredLine = resourceState.isImported()
                ? resourceState.getLineName()
                : stationState.getLineName();
        LegacyLineCatalog.LineProfile profile = LegacyLineCatalog.findByName(this, preferredLine);
        String direction = valueOrDefault(stationState.getDirectionText(), "上行");
        List<String> stations = profile.stationsForDirection(direction);
        if (stations.isEmpty()) {
            return;
        }
        String expectedTerminal = stations.get(stations.size() - 1);
        boolean lineChanged = !profile.matchesLineName(stationState.getLineName());
        boolean routeChanged = !expectedTerminal.equals(valueOrDefault(stationState.getTerminalStation(), "-"));
        if (lineChanged || routeChanged) {
            stationState.applyLineProfile(profile.getLineName(), direction, stations);
        }
    }

    private void addRouteSummaryLine(@NonNull LinearLayout container, @NonNull String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(24f);
        textView.setTextColor(ContextCompat.getColor(this, R.color.c_ffffff));
        textView.setPadding(0, 0, 0, 12);
        container.addView(textView);
    }

    private String valueOrDefault(@Nullable String primary, @Nullable String fallback) {
        if (primary != null && !primary.trim().isEmpty()) {
            return primary.trim();
        }
        return fallback == null || fallback.trim().isEmpty() ? "--" : fallback.trim();
    }
}
