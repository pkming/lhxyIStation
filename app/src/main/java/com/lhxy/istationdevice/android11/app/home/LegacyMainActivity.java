package com.lhxy.istationdevice.android11.app.home;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
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

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.auth.LegacyLoginActivity;
import com.lhxy.istationdevice.android11.app.line.LegacyLineChoiceActivity;
import com.lhxy.istationdevice.android11.app.line.LegacyLineCatalog;
import com.lhxy.istationdevice.android11.app.station.LegacyStationResourceStateRepository;
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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
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
    private final ShellRuntime shellRuntime = ShellRuntime.get();

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
        updateWarningButton(config);
        StationState stationState = requireStationState();
        SignInState signInState = requireSignInState();
        DispatchState dispatchState = requireDispatchState();
        GpsFixSnapshot gpsSnapshot = shellRuntime.getGpsSerialMonitor().getLatestSnapshot();
        LegacyStationResourceStateRepository.StationResourceState resourceState = LegacyStationResourceStateRepository.getState(this);
        syncImportedLineProfile(stationState, resourceState);
        LegacyLineCatalog.LineProfile lineProfile = LegacyLineCatalog.findByName(
            this,
            resolveLineName(stationState, resourceState)
        );
        String speedKmh = formatSpeedKmh(gpsSnapshot);
        String speedLimit = resolveDisplayedSpeedLimit(lineProfile, stationState);

        setText(R.id.tvSatellites, getString(R.string.main_satellites_value, resolveSatellites(gpsSnapshot, stationState)));
        setText(R.id.tvLanState, getString(R.string.main_lan_value, resolveLanState()));
        setText(R.id.tvCMS, getString(R.string.main_cms_value, resolveCmsState(config, dispatchState)));
        setText(R.id.tvDVR, getString(R.string.main_dvr_value, shellRuntime.getDvrSerialMonitor().isOnline()
                ? getString(R.string.effective)
                : getString(R.string.invalid)));
        setText(R.id.tv4G, getString(R.string.main_4g_value, resolveTransportState(NetworkCapabilities.TRANSPORT_CELLULAR)));
        setText(R.id.tvWifi, getString(R.string.main_wifi_value, resolveTransportState(NetworkCapabilities.TRANSPORT_WIFI)));
        setText(R.id.tvLocationGps, getString(R.string.main_gps_value, resolveGpsState(gpsSnapshot)));
        setText(R.id.tvLineName, getString(R.string.main_line_en, lineProfile.getLineName()));
        setText(R.id.tvLineNO, resolveStationNo(stationState));
        setText(R.id.tvLineDirection, valueOrDefault(stationState.getDirectionText(), "--"));
        setText(R.id.tvDriver, getString(R.string.main_driver, resolveDriverName(signInState)));
        setText(R.id.tvSpeedLimit, speedLimit);
        setText(R.id.tvMileage, speedKmh);
        setText(R.id.tvVideoMileage, speedKmh + "km/h");
        setText(R.id.tvVideoSpeedLimit, speedLimit + "km/h");
        setText(R.id.tvShouting, "");
        setText(R.id.tvInfoTips, resolveInfoTips(dispatchState));
        setText(R.id.tvHomeNextStation, resolveStationPreviewLabel());
        View plannedTimePanel = findViewById(R.id.lyPlannedTime);
        if (plannedTimePanel != null) {
            plannedTimePanel.setVisibility(View.VISIBLE);
        }
        TextView tvPlannedTime = findViewById(R.id.tvPlannedTime1);
        if (tvPlannedTime != null) {
            String html = resolvePlannedTimeHtml(dispatchState);
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
        Button btnWarning = findViewById(R.id.butVideo);
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
            btnLineChoice.setOnClickListener(v -> {
                shellRuntime.getModuleHub().runAction("station", "repeat_station", "legacy-main-repeat-station");
                startActivity(new Intent(this, LegacyLineChoiceActivity.class));
            });
        }
        if (btnSwitch != null) {
            btnSwitch.setOnClickListener(v -> runStationAction("switch_direction"));
        }
        if (btnCease != null) {
            btnCease.setOnClickListener(v -> runStationAction("stop_station"));
        }
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> startActivity(new Intent(this, LegacyLoginActivity.class)));
        }
        if (btnWarning != null) {
            btnWarning.setOnClickListener(v -> toggleSpeedWarning());
        }
        if (btnMessage != null) {
            btnMessage.setOnClickListener(v -> {
                // Old 4.4 keeps this key reserved on the home keyboard.
            });
        }
        if (btnQuery != null) {
            btnQuery.setOnClickListener(v -> {
                // Old 4.4 keeps this key reserved on the home keyboard.
            });
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
        refreshHomeState();
    }

    private void bindServiceToneButton(@Nullable Button button, int number) {
        if (button == null) {
            return;
        }
        button.setOnClickListener(v -> runStationAction("service_tone_" + number));
    }

    private void quickPreviewForward() {
        StationState stationState = requireStationState();
        if (stationState.quickStepForward()) {
            refreshHomeState();
        }
    }

    private void quickPreviewBackward() {
        if (requireStationState().quickStepBackward()) {
            refreshHomeState();
        }
    }

    private void toggleSpeedWarning() {
        ShellConfig current = shellRuntime.getActiveConfig();
        if (current == null) {
            current = ShellConfigRepository.get(this);
        }
        boolean enabled = !current.getBasicSetupConfig().getNewspaperSettings().isSpeedingWarningEnabled();
        try {
            ShellConfig updated = buildShellConfigWithSpeedWarning(current, enabled);
            ShellConfigRepository.save(this, updated);
            shellRuntime.applyConfig(this, updated);
            Toast.makeText(
                    this,
                    enabled ? getString(R.string.main_warning_enabled) : getString(R.string.main_warning_disabled),
                    Toast.LENGTH_SHORT
            ).show();
            refreshHomeState();
        } catch (Exception e) {
            Toast.makeText(this, R.string.main_warning_toggle_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private ShellConfig buildShellConfigWithSpeedWarning(ShellConfig current, boolean enabled) {
        ShellConfig.BasicSetupConfig basicSetup = current.getBasicSetupConfig();
        ShellConfig.NewspaperSettings newspaperSettings = basicSetup.getNewspaperSettings();
        return new ShellConfig(
                current.getDeviceProfile(),
                current.getConfigVersion(),
                "runtime:" + ShellConfigRepository.getRuntimeConfigFile(this).getAbsolutePath(),
                current.getSerialChannels(),
                current.getSocketChannels(),
                current.getGpioConfig(),
                current.getCameraConfig(),
                current.getRfidConfig(),
                current.getSystemConfig(),
                current.getDebugReplay(),
                new ShellConfig.BasicSetupConfig(
                        new ShellConfig.NewspaperSettings(
                                newspaperSettings.getInnerVolume(),
                                newspaperSettings.getOuterVolume(),
                                newspaperSettings.getLineProperty(),
                                newspaperSettings.isAngleEnabled(),
                                newspaperSettings.isDialectEnabled(),
                                newspaperSettings.isEnglishEnabled(),
                                newspaperSettings.isExternalSoundEnabled(),
                                newspaperSettings.isNowTimeEnabled(),
                                enabled
                        ),
                        basicSetup.getNetworkSettings(),
                        basicSetup.getSerialSettings(),
                        basicSetup.getTtsSettings(),
                        basicSetup.getLanguageSettings(),
                        basicSetup.getOtherSettings(),
                        basicSetup.getWirelessSettings(),
                        basicSetup.getResourceImportSettings(),
                        basicSetup.getProtocolLinkageSettings()
                )
        );
    }

    private String resolveStationPreviewLabel() {
        if (requireStationState().isPreviewingNext()) {
            return getString(R.string.main_next_station);
        }
        return getString(R.string.main_home_station);
    }

    private String resolveStationPreviewValue(StationState stationState) {
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

    private String resolveLanState() {
        if (hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || hasText(findIpAddress("eth"))) {
            return getString(R.string.connected);
        }
        return getString(R.string.unconnected);
    }

    private String resolveTransportState(int transportType) {
        return hasTransport(transportType)
                ? getString(R.string.connected)
                : getString(R.string.unconnected);
    }

    private boolean hasTransport(int transportType) {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        if (connectivityManager == null) {
            return false;
        }
        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null && capabilities.hasTransport(transportType)) {
                return true;
            }
        }
        return false;
    }

    private String findIpAddress(@NonNull String interfacePrefix) {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface == null || !networkInterface.isUp()) {
                    continue;
                }
                String name = networkInterface.getName();
                if (name == null || !name.startsWith(interfacePrefix)) {
                    continue;
                }
                for (InetAddress address : Collections.list(networkInterface.getInetAddresses())) {
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {
            // ignore
        }
        return "";
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
        if (resourceState.getLineName() != null
                && !resourceState.getLineName().trim().isEmpty()
                && !"-".equals(resourceState.getLineName().trim())) {
            return LegacyLineCatalog.findByName(this, resourceState.getLineName().trim()).getLineName();
        }
        return "";
    }

    private String resolveStationNo(@NonNull StationState stationState) {
        int stationNo = stationState.getCurrentStationNo();
        if (stationNo <= 0) {
            return getString(R.string.main_no_start);
        }
        if (stationNo >= stationState.getStationCount() - 1) {
            return getString(R.string.main_no_end);
        }
        return getString(R.string.main_no, String.valueOf(stationNo + 1));
    }

    private String resolveDriverName(@Nullable SignInState signInState) {
        if (signInState == null || !signInState.isSignedIn()) {
            return getString(R.string.undetected);
        }
        return valueOrDefault(signInState.getDriverName(), signInState.getCardNo());
    }

    private String resolveInfoTips(@Nullable DispatchState dispatchState) {
        if (dispatchState == null) {
            return "";
        }
        String message = valueOrDefault(dispatchState.getDispatchMessage(), "");
        if ("等待调度消息".equals(message) || "-".equals(message)) {
            return "";
        }
        return "";
    }

    private String resolveTripNo(@Nullable DispatchState dispatchState) {
        if (dispatchState == null) {
            return "--";
        }
        if (dispatchState.getTimesNo() > 0) {
            return String.valueOf(dispatchState.getTimesNo());
        }
        int scheduleNo = dispatchState.getScheduleNoValue();
        return scheduleNo > 0 ? String.valueOf(scheduleNo) : "--";
    }

    private String resolvePlannedTime(@Nullable DispatchState dispatchState) {
        if (dispatchState == null) {
            return "-- : --";
        }
        return valueOrDefault(dispatchState.getPlannedDepartureTime(), "-- : --");
    }

    private String resolvePlannedTimeHtml(@Nullable DispatchState dispatchState) {
        if (dispatchState != null) {
            String protocol = valueOrDefault(dispatchState.getActiveProtocol(), "").toUpperCase(Locale.ROOT);
            if (protocol.contains("AL808") || protocol.contains("ALINK")) {
                return getString(
                        R.string.main_planned_time_al808,
                        valueOrDefault(dispatchState.getPlannedDepartureTime(), "-- : --"),
                        valueOrDefault(dispatchState.getPlannedArrivalTime(), "-- : --")
                );
            }
        }
        return getString(
                R.string.main_planned_time,
                resolveTripNo(dispatchState),
                resolvePlannedTime(dispatchState)
        );
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
        List<String> stations = profile.stationsForDirection(stationState.getDirectionText());
        if (stations.isEmpty()) {
            addRouteSummaryLine(container, "暂无站点");
            return;
        }
        int highlightIndex = stationState.getCurrentStationNo();
        if (highlightIndex < 0 || highlightIndex >= stations.size()) {
            highlightIndex = 0;
        }
        int start = Math.max(0, highlightIndex - 3);
        int end = Math.min(stations.size(), start + 8);
        if (end - start < 8) {
            start = Math.max(0, end - 8);
        }
        if (start > 0) {
            addRouteSummaryLine(container, "...");
        }
        for (int index = start; index < end; index++) {
            addRouteStationLine(container, stations.get(index), index, highlightIndex, stations.size());
        }
        if (end < stations.size()) {
            addRouteSummaryLine(container, "...");
        }
    }

    private void syncImportedLineProfile(
            @NonNull StationState stationState,
            @NonNull LegacyStationResourceStateRepository.StationResourceState resourceState
    ) {
        String preferredLine = resourceState.isImported()
                ? resourceState.getLineName()
                : stationState.getLineName();
        if (preferredLine == null || preferredLine.trim().isEmpty() || "-".equals(preferredLine.trim())) {
            preferredLine = resourceState.getLineName();
        }
        LegacyLineCatalog.LineProfile profile = LegacyLineCatalog.findByName(this, preferredLine);
        boolean lineChanged = !profile.matchesLineName(stationState.getLineName());
        String direction = valueOrDefault(resourceState.getDirectionText(), valueOrDefault(stationState.getDirectionText(), "上行"));
        List<String> stations = profile.stationsForDirection(direction);
        if (stations.isEmpty()) {
            return;
        }
        String expectedTerminal = stations.get(stations.size() - 1);
        boolean routeChanged = !expectedTerminal.equals(valueOrDefault(stationState.getTerminalStation(), "-"));
        if (lineChanged || routeChanged) {
            stationState.applyLineProfile(profile.getLineName(), direction, stations);
            stationState.setLineAttribute(profile.getLineAttribute());
        } else if (!profile.getLineAttribute().equals(valueOrDefault(stationState.getLineAttribute(), "-"))) {
            stationState.setLineAttribute(profile.getLineAttribute());
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

    private void addRouteStationLine(
            @NonNull LinearLayout container,
            @NonNull String stationName,
            int stationIndex,
            int highlightIndex,
            int stationCount
    ) {
        TextView textView = new TextView(this);
        String prefix;
        if (stationIndex == 0) {
            prefix = "起 ";
        } else if (stationIndex == stationCount - 1) {
            prefix = "终 ";
        } else {
            prefix = String.format(Locale.CHINA, "%02d ", stationIndex + 1);
        }
        textView.setText(prefix + stationName);
        textView.setTextSize(stationIndex == highlightIndex ? 28f : 24f);
        textView.setTextColor(ContextCompat.getColor(
                this,
                stationIndex == highlightIndex ? R.color.c_f1ff2d : R.color.c_ffffff
        ));
        textView.setPadding(0, 0, 0, 12);
        container.addView(textView);
    }

    private String valueOrDefault(@Nullable String primary, @Nullable String fallback) {
        if (primary != null && !primary.trim().isEmpty()) {
            return primary.trim();
        }
        return fallback == null || fallback.trim().isEmpty() ? "--" : fallback.trim();
    }

    private boolean hasText(@Nullable String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String formatSpeedKmh(@Nullable GpsFixSnapshot snapshot) {
        if (snapshot == null || snapshot.getSpeedKnots() == null || snapshot.getSpeedKnots().trim().isEmpty()) {
            return "0";
        }
        try {
            double speedKmh = Double.parseDouble(snapshot.getSpeedKnots().trim()) * 1.852d;
            return String.format(Locale.US, "%.0f", speedKmh);
        } catch (Exception ignored) {
            return "0";
        }
    }

    private String resolveDisplayedSpeedLimit(
            @NonNull LegacyLineCatalog.LineProfile profile,
            @NonNull StationState stationState
    ) {
        return profile.speedLimitForDirection(stationState.getDirectionText(), stationState.getDisplayStationNo());
    }

    private void updateWarningButton(@Nullable ShellConfig config) {
        Button button = findViewById(R.id.butVideo);
        if (button == null) {
            return;
        }
        boolean enabled = config != null
                && config.getBasicSetupConfig().getNewspaperSettings().isSpeedingWarningEnabled();
        button.setText(enabled ? R.string.main_warning_on : R.string.main_warning_off);
    }
}
