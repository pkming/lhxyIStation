package com.lhxy.istationdevice.android11.app.home;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.auth.LegacyAuthSession;
import com.lhxy.istationdevice.android11.app.auth.LegacyLoginActivity;
import com.lhxy.istationdevice.android11.app.line.LegacyLineCatalog;
import com.lhxy.istationdevice.android11.app.line.LegacyLineChoiceActivity;
import com.lhxy.istationdevice.android11.app.menu.LegacyMenuActivity;
import com.lhxy.istationdevice.android11.app.media.LegacyVideoMonitorActivity;
import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.app.station.LegacyStationResourceStateRepository;
import com.lhxy.istationdevice.android11.core.LegacyHomeStatusRepository;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.domain.module.CameraDvrBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.DispatchBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.SignInBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.StationBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.domain.passenger.JhyPassengerCounterState;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 旧版首页骨架。
 * <p>
 * 这页先恢复旧终端首页的 UI 结构和主导航，
 * 深业务动作后面再逐项接入新模块。
 * <p>
 * 查找关键字：旧首页入口、状态刷新、首页监控预览、服务键和快捷入口。
 */
public final class LegacyMainActivity extends AppCompatActivity {
    private static volatile LegacyMainActivity activeHomeActivity;
    private static final int DVR_TOUCH_WIDTH = 1280;
    private static final int DVR_TOUCH_HEIGHT = 800;
    private static final long HOME_MONITOR_SWITCH_DEBOUNCE_MS = 600L;
    private static final long HOME_MONITOR_STARTUP_RESET_MS = 1_500L;
    private static final long HOME_MONITOR_GPIO_POLL_MS = 350L;  // GPIO监听周期：350ms（对标现场版M90）
    private static final int HOME_MONITOR_GPIO_STABLE_THRESHOLD = 3;  // GPIO连续稳定次数阈值
    
    // GPIO监听暂停标志（用于音频播放期间避免GPIO冲突）
    private volatile boolean homeMonitorGpioPaused = false;
    private boolean startupMonitorGpioBaselineCaptured;
    private int startupMonitorPrimary = Integer.MIN_VALUE;
    private int startupMonitorSecondary = Integer.MIN_VALUE;
    
    // GPIO稳定性检测
    private HomeMonitorMode lastGpioResolvedMode = null;  // 上次GPIO解析的模式
    private int gpioStableCount = 0;  // GPIO连续稳定次数计数器
    private static final int SHOUTING_ROUTE_OUTER = 1;
    private static final int SHOUTING_ROUTE_INNER = 2;
    private static final int SHOUTING_ROUTE_BOTH = 3;
    private static final int SHOUTING_ROUTE_IDLE = 4;
    private static final int HEADPHONE_POWER_ACTIVE = 0;
    private static final int HEADPHONE_POWER_IDLE = 1;
    private static final int HOME_SHOUTING_PERMISSION_REQUEST = 810;
    private static final int HOME_SHOUTING_SAMPLE_RATE = 8000;

    private enum HomeMonitorMode {
        MIDDLE_DOOR,
        REVERSE,
        DVR,
        REVERSE_PRIORITY,
        NONE
    }

    private final ShellRuntime shellRuntime = ShellRuntime.get();
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService homeActionExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "legacy-home-action");
        thread.setDaemon(true);
        return thread;
    });
    private SurfaceView homeDvrSurface;
    private SurfaceView homeMiddleDoorSurface;
    private SurfaceView homeReverseSurface;
    private boolean homeDvrSurfaceReady;
    private boolean homeMiddleDoorSurfaceReady;
    private boolean homeReverseSurfaceReady;
    private boolean homeMonitorPreviewOpening;
    private boolean homeMonitorPreviewOpened;
    private String homeMonitorCameraKey;
    private long lastHomeMonitorSwitchTimeMs;
    private long homeMonitorStartupResetUntilMs;
    private final String homeMonitorOwnerToken = "legacy-home-monitor@" + Integer.toHexString(System.identityHashCode(this));
    private HomeMonitorMode currentHomeMonitorMode = HomeMonitorMode.DVR;
    private HomeMonitorMode lastLoggedHomeMonitorMode;
    private int lastHomeShoutingRoute = Integer.MIN_VALUE;
    private AudioRecord homeShoutingRecord;
    private AudioTrack homeShoutingTrack;
    private Thread homeShoutingPlaybackThread;
    private volatile boolean homeShoutingRecording;
    private int homeShoutingRecordBufferSize;
    private int homeShoutingTrackBufferSize;
    private boolean homeShoutingPermissionRequested;
    private SharedPreferences.OnSharedPreferenceChangeListener homeStatusListener;
    private Thread homeMonitorGpioThread;  // GPIO监听线程
    private volatile boolean homeMonitorGpioRunning;  // GPIO监听运行标志
    private final Runnable clockTicker = new Runnable() {
        @Override
        public void run() {
            refreshHomeState();
            clockHandler.postDelayed(this, 1_000L);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        applyImmersiveFullscreen();
        bindHeader();
        bindActions();
        bindHomeDvrPanel();
        refreshHomeState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activeHomeActivity = this;
        applyImmersiveFullscreen();
        registerHomeStatusListener();
        shellRuntime.getPassengerCounterMonitor().setStateListener(
                state -> runOnUiThread(() -> bindPassengerCounters(state))
        );
        resetHomeMonitorAtStartup();
        startClockTicker();
        startHomeMonitorGpioThread();  // 启动GPIO监听线程
        refreshHomeState();
        openHomeMonitorPreviewIfReady();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            applyImmersiveFullscreen();
        }
    }

    @Override
    protected void onPause() {
        applyHomeShoutingIdle();
        closeHomeMonitorPreview(false);
        if (activeHomeActivity == this) {
            activeHomeActivity = null;
        }
        shellRuntime.getPassengerCounterMonitor().setStateListener(null);
        unregisterHomeStatusListener();
        super.onPause();
        stopClockTicker();
        stopHomeMonitorGpioThread();  // 停止GPIO监听线程
    }

    @Override
    protected void onDestroy() {
        applyHomeShoutingIdle();
        releaseHomeShoutingAudio();
        if (activeHomeActivity == this) {
            activeHomeActivity = null;
        }
        shellRuntime.getPassengerCounterMonitor().setStateListener(null);
        unregisterHomeStatusListener();
        homeActionExecutor.shutdownNow();
        stopHomeMonitorGpioThread();  // 确保停止GPIO监听线程
        super.onDestroy();
    }

    /**
     * 绑定首页 DVR/中门/倒车三个监控面板和触摸转发。
     */
    private void bindHomeDvrPanel() {
        homeDvrSurface = findViewById(R.id.surfaceViewDVR2);
        homeMiddleDoorSurface = findViewById(R.id.surfaceViewMittertor);
        homeReverseSurface = findViewById(R.id.surfaceViewBackingup);
        applyHomeMonitorMode(currentHomeMonitorMode);
        bindHomeMonitorSurface(homeDvrSurface, HomeMonitorMode.DVR);
        bindHomeMonitorSurface(homeMiddleDoorSurface, HomeMonitorMode.MIDDLE_DOOR);
        bindHomeMonitorSurface(homeReverseSurface, HomeMonitorMode.REVERSE);
    }

    /**
     * 订阅首页状态仓库，保证首页能跟着资源和业务状态变化一起刷新。
     */
    private void registerHomeStatusListener() {
        if (homeStatusListener != null) {
            return;
        }
        homeStatusListener = LegacyHomeStatusRepository.registerListener(this, () -> runOnUiThread(this::refreshHomeState));
    }

    private void unregisterHomeStatusListener() {
        LegacyHomeStatusRepository.unregisterListener(this, homeStatusListener);
        homeStatusListener = null;
    }

    /**
     * 给某一路监控 Surface 绑定生命周期和触摸入口。
     */
    private void bindHomeMonitorSurface(@Nullable SurfaceView surfaceView, @NonNull HomeMonitorMode mode) {
        if (surfaceView == null) {
            return;
        }
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                setHomeMonitorSurfaceReady(mode, true);
                openHomeMonitorPreviewIfReady();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                setHomeMonitorSurfaceReady(mode, width > 0 && height > 0);
                openHomeMonitorPreviewIfReady();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                setHomeMonitorSurfaceReady(mode, false);
                closeHomeMonitorPreview(false);
            }
        });
    }

    private void bindHeader() {
        TextView tvInformation = findViewById(R.id.tvInformation);
        if (tvInformation != null) {
            tvInformation.setText("");
        }
        ImageView imageInformation = findViewById(R.id.imageInformation);
        if (imageInformation != null) {
            imageInformation.setClickable(true);
            imageInformation.setFocusable(true);
            imageInformation.setOnClickListener(v -> runDriverAction());
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

    private void startClockTicker() {
        stopClockTicker();
        clockTicker.run();
    }

    /**
     * 首页走沉浸式全屏，避免旧壳布局被系统栏挤压。
     */
    private void applyImmersiveFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller == null) {
            return;
        }
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        controller.hide(WindowInsetsCompat.Type.systemBars());
    }

    private void stopClockTicker() {
        clockHandler.removeCallbacks(clockTicker);
    }

    /**
     * 首页总刷新入口。
     * <p>
     * 这里把配置、监控状态、线路资源、GPS、调度、签到等快照统一拍平成旧首页展示字段。
     */
    private void refreshHomeState() {
        updateClock();
        ShellConfig config = shellRuntime.getActiveConfig();
        if (config == null) {
            config = ShellConfigRepository.get(this);
            shellRuntime.applyConfig(this, config);
        }
        updateHomeDvrPanel(config);
        // Legacy home shows a flattened snapshot of multiple modules, so this refresh keeps
        // display mapping in one place instead of pushing old page semantics back into modules.
        StationState stationState = requireStationState();
        SignInState signInState = requireSignInState();
        updateDriverActionButton(signInState);
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
        boolean overspeed = isOverspeed(speedKmh, speedLimit);

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
        setText(R.id.tvLineDirection, resolveDirectionLabel(stationState));
        setText(R.id.tvJobNumber, resolveHomeJobNumber(signInState));
        setText(R.id.tvCarNumber, resolveHomeCarNumber(config));
        setText(R.id.tvSpeedLimit, speedLimit);
        setText(R.id.tvMileage, speedKmh);
        bindPassengerCounters(stationState);
        applySpeedWarningStyle(overspeed);
        setText(R.id.tvShouting, resolveHomeShouting(config));
        setText(R.id.tvInfoTips, resolveInfoTips());
        setText(R.id.tvInformation, LegacyHomeStatusRepository.getState(this).getInformation());
        bindOptionalDispatchText(R.id.tvNextTrip, dispatchState == null ? "-" : dispatchState.getNextTrip());
        bindOptionalDispatchText(R.id.tvThisTrip, dispatchState == null ? "-" : dispatchState.getThisTrip());
        bindOptionalDispatchText(R.id.tvTomorrow, dispatchState == null ? "-" : dispatchState.getTomorrow());
        updateLineChoiceShortcutButton();
        bindVehicleStatus(dispatchState, signInState);
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
        setText(R.id.tvHomeNextStationName, homeStationName);
        setText(R.id.tvEndBusName, valueOrDefault(stationState.getTerminalStation(), "--"));
    }

    private void updateHomeDvrPanel(@NonNull ShellConfig config) {
        if (homeMonitorGpioPaused) {
            return;
        }
        if (System.currentTimeMillis() < homeMonitorStartupResetUntilMs) {
            currentHomeMonitorMode = HomeMonitorMode.DVR;
            applyHomeMonitorMode(HomeMonitorMode.DVR);
            updateHomeMonitorSurfaceVisibility(HomeMonitorMode.DVR, config.getCameraConfig().getMode() == DeviceMode.REAL);
            return;
        }
        currentHomeMonitorMode = resolveHomeMonitorMode(config);
        logHomeMonitorModeIfChanged(currentHomeMonitorMode, config);
        applyHomeMonitorMode(currentHomeMonitorMode);
        boolean isRealCamera = config.getCameraConfig().getMode() == DeviceMode.REAL;
        String cameraKey = resolveHomeMonitorCameraKey(currentHomeMonitorMode, config);
        updateHomeMonitorSurfaceVisibility(currentHomeMonitorMode, isRealCamera);
        if (currentHomeMonitorMode == HomeMonitorMode.NONE) {
            closeHomeMonitorPreview(false);
            return;
        }
        if (currentHomeMonitorMode != HomeMonitorMode.DVR) {
            if (!isRealCamera) {
                closeHomeMonitorPreview(false);
                return;
            }
            if (homeMonitorPreviewOpened && cameraKey.equals(homeMonitorCameraKey)) {
                return;
            }
            openHomeMonitorPreviewIfReady();
            return;
        }
        if (!isRealCamera) {
            closeHomeMonitorPreview(false);
            return;
        }
        if (homeMonitorPreviewOpened && cameraKey.equals(homeMonitorCameraKey)) {
            return;
        }
        openHomeMonitorPreviewIfReady();
    }

    /**
     * 每次回到首页先恢复默认 DVR 布局，给 GPIO 留出稳定时间，避免启动瞬间沿用误判状态。
     */
    private void resetHomeMonitorAtStartup() {
        homeMonitorGpioPaused = false;
        startupMonitorGpioBaselineCaptured = false;
        startupMonitorPrimary = Integer.MIN_VALUE;
        startupMonitorSecondary = Integer.MIN_VALUE;
        lastGpioResolvedMode = null;
        gpioStableCount = 0;
        homeMonitorStartupResetUntilMs = System.currentTimeMillis() + HOME_MONITOR_STARTUP_RESET_MS;
        currentHomeMonitorMode = HomeMonitorMode.DVR;
        applyHomeMonitorMode(HomeMonitorMode.DVR);
        closeHomeMonitorPreview(false);
        AppLogCenter.log(
                LogCategory.UI,
                LogLevel.INFO,
                "LegacyMainActivity",
                "首页监控启动重置为 DVR，等待 GPIO 稳定",
                "legacy-home-monitor-startup-reset"
        );
    }

    private void openHomeMonitorPreviewIfReady() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        ShellConfig config = shellRuntime.getActiveConfig();
        if (config == null || config.getCameraConfig().getMode() != DeviceMode.REAL) {
            return;
        }
        SurfaceView previewSurface = resolveHomePreviewSurface(currentHomeMonitorMode);
        if (previewSurface == null || !isHomeMonitorSurfaceReady(currentHomeMonitorMode)) {
            return;
        }
        SurfaceHolder holder = previewSurface.getHolder();
        if (holder == null || holder.getSurface() == null || !holder.getSurface().isValid()) {
            return;
        }
        String cameraKey = resolveHomeMonitorCameraKey(currentHomeMonitorMode, config);
        if (cameraKey.isEmpty()) {
            return;
        }
        if (isHomeMonitorPreviewActive(cameraKey)) {
            return;
        }
        try {
            long now = System.currentTimeMillis();
            if (now - lastHomeMonitorSwitchTimeMs < HOME_MONITOR_SWITCH_DEBOUNCE_MS) {
                return;
            }
            lastHomeMonitorSwitchTimeMs = now;
            if (homeMonitorPreviewOpened || homeMonitorPreviewOpening) {
                closeHomeMonitorPreview(false);
            }
            homeMonitorCameraKey = cameraKey;
            homeMonitorPreviewOpening = true;
            shellRuntime.getCameraAdapter().openPreview(
                    cameraKey,
                    holder.getSurface(),
                    Math.max(1, previewSurface.getWidth()),
                    Math.max(1, previewSurface.getHeight()),
                    homeMonitorOwnerToken,
                    TraceIds.next("legacy-home-monitor-preview-" + cameraKey)
            );
            homeMonitorPreviewOpened = true;
            clockHandler.postDelayed(() -> {
                if (cameraKey.equals(homeMonitorCameraKey)) {
                    homeMonitorPreviewOpening = false;
                }
            }, HOME_MONITOR_SWITCH_DEBOUNCE_MS);
                AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    "LegacyMainActivity",
                    "首页监控预览已打开 mode=" + currentHomeMonitorMode + " / camera=" + cameraKey
                        + " / size=" + Math.max(1, previewSurface.getWidth()) + "x" + Math.max(1, previewSurface.getHeight()),
                    TraceIds.next("legacy-home-monitor-open")
                );
        } catch (Exception e) {
            homeMonitorPreviewOpening = false;
            homeMonitorPreviewOpened = false;
                AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "LegacyMainActivity",
                    "首页监控预览打开失败 mode=" + currentHomeMonitorMode + " / camera=" + cameraKey + " / error=" + e.getMessage(),
                    TraceIds.next("legacy-home-monitor-open-failed")
                );
        }
    }

    private void closeHomeMonitorPreview(boolean showPlaceholder) {
        String cameraKey = homeMonitorCameraKey;
        boolean wasOpened = homeMonitorPreviewOpened || homeMonitorPreviewOpening;
        lastHomeMonitorSwitchTimeMs = System.currentTimeMillis();
        homeMonitorPreviewOpening = false;
        homeMonitorPreviewOpened = false;
        homeMonitorCameraKey = null;
        
        // 对标现场版M90：切换前关闭所有摄像头通道，避免资源冲突
        if (wasOpened) {
            try {
                // 关闭所有可能的摄像头通道
                String[] allCameraKeys = {"middle_door", "reverse", "av_out"};
                for (String key : allCameraKeys) {
                    try {
                        shellRuntime.getCameraAdapter().close(key, homeMonitorOwnerToken, 
                            TraceIds.next("legacy-home-monitor-close-all-" + key));
                    } catch (Exception ignore) {
                        // 某些摄像头可能本来就没打开，忽略关闭失败
                    }
                }
                
                if (cameraKey != null && !cameraKey.trim().isEmpty()) {
                    AppLogCenter.log(
                            LogCategory.UI,
                            LogLevel.INFO,
                            "LegacyMainActivity",
                            "首页监控预览已关闭（关闭所有通道） camera=" + cameraKey,
                            TraceIds.next("legacy-home-monitor-close")
                    );
                }
            } catch (Exception ignore) {
                // Keep the home page responsive even if preview teardown fails.
            }
        }
    }

    private boolean isHomeMonitorPreviewActive(@NonNull String cameraKey) {
        return (homeMonitorPreviewOpened || homeMonitorPreviewOpening) && cameraKey.equals(homeMonitorCameraKey);
    }

    private void setHomeMonitorSurfaceReady(@NonNull HomeMonitorMode mode, boolean ready) {
        switch (mode) {
            case MIDDLE_DOOR:
                homeMiddleDoorSurfaceReady = ready;
                break;
            case REVERSE:
            case REVERSE_PRIORITY:
                homeReverseSurfaceReady = ready;
                break;
            case DVR:
                homeDvrSurfaceReady = ready;
                break;
            default:
                break;
        }
    }

    private boolean isHomeMonitorSurfaceReady(@NonNull HomeMonitorMode mode) {
        switch (mode) {
            case MIDDLE_DOOR:
                return homeMiddleDoorSurfaceReady;
            case REVERSE:
            case REVERSE_PRIORITY:
                return homeReverseSurfaceReady;
            case DVR:
                return homeDvrSurfaceReady;
            default:
                return false;
        }
    }

    @Nullable
    private SurfaceView resolveHomePreviewSurface(@NonNull HomeMonitorMode mode) {
        switch (mode) {
            case MIDDLE_DOOR:
                return homeMiddleDoorSurface;
            case REVERSE:
            case REVERSE_PRIORITY:
                return homeReverseSurface;
            case DVR:
                return homeDvrSurface;
            default:
                return null;
        }
    }

    @NonNull
    private String resolveHomeMonitorCameraKey(@NonNull HomeMonitorMode mode, @NonNull ShellConfig config) {
        switch (mode) {
            case MIDDLE_DOOR:
                return "middle_door";
            case REVERSE:
            case REVERSE_PRIORITY:
                return "reverse";
            case DVR:
                return valueOrDefault(config.getDebugReplay().getCameraChannelKey(), "av_out");
            default:
                return "";
        }
    }

    private void updateHomeMonitorSurfaceVisibility(@NonNull HomeMonitorMode mode, boolean isRealCamera) {
        if (homeDvrSurface != null) {
            homeDvrSurface.setVisibility(isRealCamera && mode == HomeMonitorMode.DVR ? View.VISIBLE : View.GONE);
        }
        if (homeMiddleDoorSurface != null) {
            homeMiddleDoorSurface.setVisibility(isRealCamera && mode == HomeMonitorMode.MIDDLE_DOOR ? View.VISIBLE : View.GONE);
        }
        if (homeReverseSurface != null) {
            boolean showReverse = mode == HomeMonitorMode.REVERSE || mode == HomeMonitorMode.REVERSE_PRIORITY;
            homeReverseSurface.setVisibility(isRealCamera && showReverse ? View.VISIBLE : View.GONE);
        }
    }

    private void bindHomeDvrTouch(@Nullable View target) {
        if (target == null) {
            return;
        }
        target.setOnTouchListener((view, event) -> handleHomeDvrTouch(view, event));
    }

    private void applyHomeMonitorMode(@NonNull HomeMonitorMode mode) {
        currentHomeMonitorMode = mode;
        View busInfo = findViewById(R.id.rlBusInfo);
        View lineDriver = findViewById(R.id.rlLineDriver);
        View dvrContainer = findViewById(R.id.lyVideoDVRImage);
        View multiVideoContainer = findViewById(R.id.lyVideoImage);
        if (busInfo == null || lineDriver == null || dvrContainer == null || multiVideoContainer == null) {
            return;
        }
        switch (mode) {
            case MIDDLE_DOOR:
            case REVERSE:
            case REVERSE_PRIORITY:
                multiVideoContainer.setVisibility(View.VISIBLE);
                busInfo.setVisibility(View.GONE);
                lineDriver.setVisibility(View.GONE);
                dvrContainer.setVisibility(View.GONE);
                break;
            case DVR:
            case NONE:
            default:
                multiVideoContainer.setVisibility(View.GONE);
                busInfo.setVisibility(View.VISIBLE);
                lineDriver.setVisibility(View.VISIBLE);
                dvrContainer.setVisibility(View.VISIBLE);
                break;
        }
    }

    private HomeMonitorMode resolveConfiguredMonitorMode(@Nullable String cameraKey) {
        String safeCameraKey = valueOrDefault(cameraKey, "av_out");
        if ("middle_door".equals(safeCameraKey)) {
            return HomeMonitorMode.MIDDLE_DOOR;
        }
        if ("reverse".equals(safeCameraKey)) {
            return HomeMonitorMode.REVERSE;
        }
        return HomeMonitorMode.DVR;
    }

    private HomeMonitorMode resolveHomeMonitorMode(@NonNull ShellConfig config) {
        String primaryKey = valueOrDefault(config.getDebugReplay().getMonitorPrimaryGpioKey(), "").trim();
        String secondaryKey = valueOrDefault(config.getDebugReplay().getMonitorSecondaryGpioKey(), "").trim();
        String defaultCameraKey = valueOrDefault(config.getDebugReplay().getCameraChannelKey(), "av_out").trim();
        if (config.getCameraConfig().getMode() != DeviceMode.REAL) {
            return HomeMonitorMode.DVR;
        }
        if (config.getGpioConfig().getMode() != DeviceMode.REAL) {
            return resolveConfiguredMonitorMode(defaultCameraKey);
        }
        if (!primaryKey.isEmpty() && !secondaryKey.isEmpty()) {
            try {
                // 对标现场版M90防抖机制：50ms双重读取验证（MainActivity.java 1354-1372行）
                // 第1次读取
                int primary1 = shellRuntime.getGpioAdapter().read(primaryKey, TraceIds.next("legacy-home-monitor-primary-1"));
                int secondary1 = shellRuntime.getGpioAdapter().read(secondaryKey, TraceIds.next("legacy-home-monitor-secondary-1"));
                
                // 等待50ms（对标现场版：5次×10ms循环）
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return currentHomeMonitorMode != null ? currentHomeMonitorMode : HomeMonitorMode.DVR;
                }
                
                // 第2次读取
                int primary2 = shellRuntime.getGpioAdapter().read(primaryKey, TraceIds.next("legacy-home-monitor-primary-2"));
                int secondary2 = shellRuntime.getGpioAdapter().read(secondaryKey, TraceIds.next("legacy-home-monitor-secondary-2"));
                
                // 两次读取必须完全一致才认为状态稳定（对标现场版1368-1372行）
                if (primary1 != primary2 || secondary1 != secondary2) {
                    // GPIO信号不稳定，重置稳定计数器（但保留lastGpioResolvedMode，避免下次误判为新模式）
                    gpioStableCount = 0;
                    AppLogCenter.log(
                            LogCategory.UI,
                            LogLevel.DEBUG,
                            "LegacyMainActivity",
                            String.format("GPIO信号不稳定，忽略切换 (第1次: %d/%d, 第2次: %d/%d)", 
                                primary1, secondary1, primary2, secondary2),
                            TraceIds.next("home-monitor-gpio-unstable")
                    );
                    return currentHomeMonitorMode != null ? currentHomeMonitorMode : HomeMonitorMode.DVR;
                }
                
                // GPIO信号稳定，解析视频模式（对标现场版1373-1428行）
                int primary = primary2;
                int secondary = secondary2;

                // Keep the startup GPIO level as a baseline. Only a later edge may switch away from DVR.
                if (!startupMonitorGpioBaselineCaptured) {
                    startupMonitorPrimary = primary;
                    startupMonitorSecondary = secondary;
                    startupMonitorGpioBaselineCaptured = true;
                    lastGpioResolvedMode = null;
                    gpioStableCount = 0;
                    AppLogCenter.log(
                            LogCategory.UI,
                            LogLevel.INFO,
                            "LegacyMainActivity",
                            "记录首页监控启动 GPIO 基线 primary=" + primary + " / secondary=" + secondary + "，保持 DVR",
                            TraceIds.next("home-monitor-gpio-startup-baseline")
                    );
                    return HomeMonitorMode.DVR;
                }
                if (primary == startupMonitorPrimary && secondary == startupMonitorSecondary) {
                    lastGpioResolvedMode = null;
                    gpioStableCount = 0;
                    return HomeMonitorMode.DVR;
                }

                HomeMonitorMode resolvedMode;
                if (primary == 1 && secondary == 0) {
                    resolvedMode = HomeMonitorMode.MIDDLE_DOOR;
                } else if (primary == 0 && secondary == 1) {
                    resolvedMode = HomeMonitorMode.REVERSE;
                } else if (primary == 0 && secondary == 0) {
                    resolvedMode = HomeMonitorMode.REVERSE_PRIORITY;
                } else {
                    resolvedMode = HomeMonitorMode.DVR;
                }

                // 连续稳定性检测：必须连续N次解析到相同模式才允许切换
                if (resolvedMode == lastGpioResolvedMode) {
                    gpioStableCount++;
                    if (gpioStableCount >= HOME_MONITOR_GPIO_STABLE_THRESHOLD) {
                        // 已连续稳定，允许切换
                        return resolvedMode;
                    } else {
                        // 稳定中，但尚未达到阈值，保持当前模式
                        AppLogCenter.log(
                                LogCategory.UI,
                                LogLevel.DEBUG,
                                "LegacyMainActivity",
                                String.format("GPIO稳定中 mode=%s / count=%d/%d / 保持当前模式", 
                                    monitorModeLabel(resolvedMode), gpioStableCount, HOME_MONITOR_GPIO_STABLE_THRESHOLD),
                                TraceIds.next("home-monitor-gpio-stabilizing")
                        );
                        return currentHomeMonitorMode != null ? currentHomeMonitorMode : HomeMonitorMode.DVR;
                    }
                } else {
                    // 模式变化，重置计数器
                    lastGpioResolvedMode = resolvedMode;
                    gpioStableCount = 1;
                    AppLogCenter.log(
                            LogCategory.UI,
                            LogLevel.DEBUG,
                            "LegacyMainActivity",
                            String.format("GPIO模式变化，重置计数器 newMode=%s / count=1/%d", 
                                monitorModeLabel(resolvedMode), HOME_MONITOR_GPIO_STABLE_THRESHOLD),
                            TraceIds.next("home-monitor-gpio-mode-changed")
                    );
                    return currentHomeMonitorMode != null ? currentHomeMonitorMode : HomeMonitorMode.DVR;
                }
            } catch (Exception ignore) {
                // Fall back to the configured default camera mode when monitor GPIOs are absent or unreadable.
            }
        }
        return resolveConfiguredMonitorMode(defaultCameraKey);
    }

    private String monitorModeLabel(@NonNull HomeMonitorMode mode) {
        switch (mode) {
            case MIDDLE_DOOR:
                return "中门视频";
            case REVERSE:
                return "倒车视频";
            case REVERSE_PRIORITY:
                return "倒车优先";
            case NONE:
                return "视频关闭";
            case DVR:
            default:
                return "DVR";
        }
    }

    private boolean handleHomeDvrTouch(@NonNull View view, @Nullable MotionEvent event) {
        if (event == null) {
            return false;
        }
        String phase = mapTouchPhase(event.getActionMasked());
        if (phase == null) {
            return false;
        }
        int touchX = scaleCoordinate(event.getX(), view.getWidth(), DVR_TOUCH_WIDTH);
        int touchY = scaleCoordinate(event.getY(), view.getHeight(), DVR_TOUCH_HEIGHT);
        float rawX = event.getX();
        float rawY = event.getY();
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        homeActionExecutor.execute(() -> {
            ModuleRunResult result = shellRuntime.getModuleHub().runAction(
                    "camera_dvr",
                    buildTouchActionKey(phase, touchX, touchY),
                    TraceIds.next("legacy-home-dvr-touch")
            );
            runOnUiThread(() -> {
                AppLogCenter.log(
                    result.isSuccess() ? LogCategory.UI : LogCategory.ERROR,
                    result.isSuccess() ? LogLevel.INFO : LogLevel.WARN,
                    "LegacyMainActivity",
                    "首页 DVR 触摸 phase=" + phase + " / raw=" + rawX + "," + rawY
                        + " / view=" + viewWidth + "x" + viewHeight
                        + " / scaled=" + touchX + "," + touchY
                        + " / result=" + result.describeInline(),
                    TraceIds.next("legacy-home-dvr-touch-result")
                );
                if (!result.isSuccess() && ("down".equals(phase) || "up".equals(phase))) {
                    Toast.makeText(this, result.describeInline(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        return true;
    }

        private void logHomeMonitorModeIfChanged(@NonNull HomeMonitorMode mode, @NonNull ShellConfig config) {
        if (mode == lastLoggedHomeMonitorMode) {
            return;
        }
        lastLoggedHomeMonitorMode = mode;
        AppLogCenter.log(
            LogCategory.UI,
            LogLevel.INFO,
            "LegacyMainActivity",
            "首页监控模式=" + monitorModeLabel(mode)
                + " / cameraMode=" + config.getCameraConfig().getMode()
                + " / gpioMode=" + config.getGpioConfig().getMode()
                + " / primary=" + valueOrDefault(config.getDebugReplay().getMonitorPrimaryGpioKey(), "-")
                + " / secondary=" + valueOrDefault(config.getDebugReplay().getMonitorSecondaryGpioKey(), "-")
                + " / defaultCamera=" + valueOrDefault(config.getDebugReplay().getCameraChannelKey(), "av_out"),
            TraceIds.next("legacy-home-monitor-mode")
        );
        }

    private String buildTouchActionKey(String phase, int x, int y) {
        return "dvr_touch_" + phase + "_" + x + "_" + y;
    }

    @Nullable
    private String mapTouchPhase(int actionMasked) {
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                return "down";
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return "up";
            default:
                return null;
        }
    }

    private int scaleCoordinate(float value, int sourceSize, int targetSize) {
        if (sourceSize <= 0 || targetSize <= 0) {
            return 0;
        }
        float clamped = Math.max(0f, Math.min(value, sourceSize));
        return Math.round((clamped / (float) sourceSize) * targetSize);
    }

    private void bindActions() {
        Button btnKeyboard = findViewById(R.id.butKeyboard);
        Button btnEsc = findViewById(R.id.butESC);
        Button btnLineChoice = findViewById(R.id.tvDriver);
        Button btnNewspaperStation = findViewById(R.id.butNewspaperStation);
        Button btnRepeat = findViewById(R.id.butRepeat);
        Button btnSwitch = findViewById(R.id.butSwitch);
        Button btnCease = findViewById(R.id.butCease);
        Button btnMenu = findViewById(R.id.butMenu);
        Button btnLineChoiceShortcut = findViewById(R.id.butVideo);
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
            btnLineChoice.setText(R.string.menu_line_selection);
            btnLineChoice.setOnClickListener(v -> openLineChoiceFromHome());
        }
        if (btnRepeat != null) {
            btnRepeat.setOnClickListener(v -> runStationAction("repeat_station"));
        }
        if (btnSwitch != null) {
            btnSwitch.setOnClickListener(v -> runStationAction("switch_direction"));
        }
        if (btnCease != null) {
            btnCease.setOnClickListener(v -> runStationAction("stop_station"));
        }
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> openMenuFromHome());
        }
        if (btnLineChoiceShortcut != null) {
            btnLineChoiceShortcut.setEnabled(true);
            btnLineChoiceShortcut.setBackgroundResource(R.drawable.txt_key_bck);
            btnLineChoiceShortcut.setOnClickListener(v -> startActivity(LegacyVideoMonitorActivity.createIntent(this, "home")));
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

    private void bindOptionalDispatchText(int id, String value) {
        TextView view = findViewById(id);
        if (view == null) {
            return;
        }
        String safeValue = value == null ? "" : value.trim();
        boolean visible = !safeValue.isEmpty() && !"-".equals(safeValue);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        view.setText(visible ? safeValue : "");
    }

    private void runStationAction(String actionKey) {
        homeActionExecutor.execute(() -> {
            ModuleRunResult result = shellRuntime.getModuleHub().runAction("station", actionKey, "legacy-main-" + actionKey);
            persistStationRouteSelectionIfNeeded(actionKey, result);
            runOnUiThread(() -> {
                AppLogCenter.log(
                        result.isSuccess() ? LogCategory.UI : LogCategory.ERROR,
                        result.isSuccess() ? LogLevel.INFO : LogLevel.WARN,
                        "LegacyMainActivity",
                        "首页站点动作 action=" + actionKey + " / result=" + result.describeInline(),
                        "legacy-main-" + actionKey
                );
                Toast.makeText(this, result.describeInline(), Toast.LENGTH_SHORT).show();
                refreshHomeState();
            });
        });
    }

    private void persistStationRouteSelectionIfNeeded(@NonNull String actionKey, @NonNull ModuleRunResult result) {
        if (!result.isSuccess() || !"switch_direction".equals(actionKey)) {
            return;
        }
        try {
            StationState stationState = requireStationState();
            LegacyStationResourceStateRepository.StationResourceState resourceState =
                    LegacyStationResourceStateRepository.getState(this);
            String source = valueOrDefault(resourceState.getSource(), "home-switch-direction");
            String lineName = valueOrDefault(stationState.getLineName(), resourceState.getLineName());
            String directionText = valueOrDefault(stationState.getDirectionText(), resourceState.getDirectionText());
            String lineAttribute = valueOrDefault(stationState.getLineAttribute(), resourceState.getLineAttribute());
            LegacyStationResourceStateRepository.updateRouteSelection(
                    this,
                    source,
                    lineName,
                    directionText,
                    lineAttribute
            );
            AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    "LegacyMainActivity",
                    "首页切换方向已同步到资源状态: " + lineName + " / " + directionText,
                    "legacy-main-switch-direction-persist"
            );
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "LegacyMainActivity",
                    "首页切换方向同步资源状态失败: " + e.getMessage(),
                    "legacy-main-switch-direction-persist"
            );
        }
    }

    private void runDriverAction() {
        SignInState signInState = requireSignInState();
        String actionKey = shouldReadDriverCard(signInState) ? "read_card" : "manual_sign_out";
        homeActionExecutor.execute(() -> {
            ModuleRunResult result = shellRuntime.getModuleHub().runAction("signin", actionKey, "legacy-main-" + actionKey);
            runOnUiThread(() -> {
                AppLogCenter.log(
                    result.isSuccess() ? LogCategory.UI : LogCategory.ERROR,
                    result.isSuccess() ? LogLevel.INFO : LogLevel.WARN,
                    "LegacyMainActivity",
                    "首页司机动作 action=" + actionKey + " / result=" + result.describeInline(),
                    "legacy-main-" + actionKey
                );
                Toast.makeText(this, result.describeInline(), Toast.LENGTH_SHORT).show();
                refreshHomeState();
            });
        });
    }

    private void openLineChoiceFromHome() {
        startActivity(new Intent(this, LegacyLineChoiceActivity.class));
    }

    private void openMenuFromHome() {
        if (LegacyAuthSession.isValid(this)) {
            LegacyAuthSession.touch(this);
            startActivity(new Intent(this, LegacyMenuActivity.class));
            return;
        }
        startActivity(LegacyLoginActivity.createIntent(this));
    }

    private void updateDriverActionButton(@Nullable SignInState signInState) {
        ImageView button = findViewById(R.id.imageInformation);
        if (button == null) {
            return;
        }
        button.setSelected(signInState != null && signInState.isSignedIn());
    }

    private void updateLineChoiceShortcutButton() {
        Button button = findViewById(R.id.butVideo);
        if (button == null) {
            return;
        }
        button.setEnabled(true);
        button.setBackgroundResource(R.drawable.txt_key_bck);
    }

    @NonNull
    private CharSequence resolveDriverActionButtonText(@Nullable SignInState signInState) {
        if (signInState == null || !signInState.isSignedIn()) {
            return getString(R.string.main_logout);
        }
        if (!hasResolvedDriverIdentity(signInState)) {
            return getString(R.string.main_login);
        }
        String driverIdentity = valueOrDefault(signInState.getDriverName(), valueOrDefault(signInState.getCardNo(), "")).trim();
        return getString(R.string.main_driver, driverIdentity);
    }

    private void bindServiceToneButton(@Nullable Button button, int number) {
        if (button == null) {
            return;
        }
        // The legacy keypad couples local service-tone playback and outbound reporting into one key.
        button.setOnClickListener(v -> runStationAction("service_tone_" + number));
    }

    private void quickPreviewForward() {
        StationState stationState = requireStationState();
        if (stationState.quickStepForward()) {
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyMainActivity", "首页上翻预览 -> " + stationState.getCurrentStation(), "legacy-main-preview-forward");
            refreshHomeState();
        }
    }

    private void quickPreviewBackward() {
        StationState stationState = requireStationState();
        if (stationState.quickStepBackward()) {
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyMainActivity", "首页下翻预览 -> " + stationState.getCurrentStation(), "legacy-main-preview-backward");
            refreshHomeState();
        }
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
        return isDispatchPlatformOnline(config, dispatchState)
                ? getString(R.string.connected)
                : getString(R.string.unconnected);
    }

    private boolean isDispatchPlatformOnline(@Nullable ShellConfig config, @Nullable DispatchState dispatchState) {
        if (config == null || dispatchState == null || config.getSocketChannels().isEmpty()) {
            return false;
        }
        try {
            ShellConfig.SocketChannel channel = config.requireSocketChannel(config.getDebugReplay().getJt808SocketKey());
            return shellRuntime.getSocketClientAdapter().isConnected(channel.getChannelName())
                    && dispatchState.isPlatformOnline(120_000L);
        } catch (Exception e) {
            return false;
        }
    }

    private String resolveGpsState(@Nullable GpsFixSnapshot snapshot) {
        // 首页 GPS 状态只显示 Y/N(定位有效=Y)，不再用 有效/无效 文案。
        return snapshot != null && snapshot.isValid() ? "Y" : "N";
    }

    private String resolveLineName(@NonNull StationState stationState, @NonNull LegacyStationResourceStateRepository.StationResourceState resourceState) {
        // Imported resources win over empty runtime state here because the legacy home page is judged
        // by whatever line bundle the operator most recently selected, not by raw state object defaults.
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

    private String resolveDirectionLabel(@NonNull StationState stationState) {
        String lineAttribute = valueOrDefault(stationState.getLineAttribute(), "");
        if ("环线".equals(lineAttribute)) {
            return getString(R.string.basic_newspaper_attribute_loop);
        }
        String direction = valueOrDefault(stationState.getDirectionText(), "");
        if (direction.contains("下")) {
            return getString(R.string.down);
        }
        if (direction.contains("上")) {
            return getString(R.string.upstream);
        }
        return valueOrDefault(direction, "--");
    }

    private String resolveDriverName(@Nullable SignInState signInState) {
        if (signInState == null) {
            return getString(R.string.undetected);
        }
        String cardNo = valueOrDefault(signInState.getCardNo(), "");
        String driverIdentity = valueOrDefault(signInState.getDriverName(), cardNo);
        if (!hasResolvedDriverIdentity(signInState)) {
            return getString(R.string.undetected);
        }
        // Legacy home keeps the last attendance result visible, so signed-out drivers should
        // still render as "已签退 ..." instead of dropping back to an empty placeholder.
        return (signInState.isSignedIn() ? "已签到 " : "已签退 ") + driverIdentity;
    }

    private boolean hasResolvedDriverIdentity(@Nullable SignInState signInState) {
        return signInState != null && signInState.hasResolvedDriverIdentity();
    }

    private boolean shouldReadDriverCard(@Nullable SignInState signInState) {
        return signInState == null || !signInState.isSignedIn() || !hasResolvedDriverIdentity(signInState);
    }

    private String resolveHomeJobNumber(@Nullable SignInState signInState) {
        if (signInState == null) {
            return "--";
        }
        String driverId = valueOrDefault(signInState.getDriverId(), "").replaceAll("[^0-9A-Za-z]", "");
        if (!driverId.isEmpty() && !"-".equals(driverId)) {
            return driverId;
        }
        String digits = valueOrDefault(signInState.getCardNo(), "").replaceAll("[^0-9A-Za-z]", "");
        if (digits.isEmpty() || "-".equals(digits)) {
            return "--";
        }
        if (digits.length() <= 6) {
            return digits;
        }
        return digits.substring(digits.length() - 6);
    }

    private String resolveHomeCarNumber(@Nullable ShellConfig config) {
        if (config == null) {
            return "--";
        }
        String vehicleNumber = valueOrDefault(config.getBasicSetupConfig().getOtherSettings().getVehicleNumber(), "").trim();
        return vehicleNumber.isEmpty() ? "--" : vehicleNumber;
    }

    private String resolveHomeShouting(@Nullable ShellConfig config) {
        String homeStatusValue = LegacyHomeStatusRepository.getState(this).getShouting();
        if ("IP PHONE...".equals(homeStatusValue)) {
            return homeStatusValue;
        }
        String gpioValue = resolveGpioShouting(config);
        if (!gpioValue.isEmpty()) {
            return gpioValue;
        }
        return homeStatusValue;
    }

    private void bindVehicleStatus(@Nullable DispatchState dispatchState, @Nullable SignInState signInState) {
        TextView tvVehicleStatus = findViewById(R.id.tvVehicleStatus);
        if (tvVehicleStatus == null) {
            return;
        }
        String value = resolveVehicleStatus(dispatchState, signInState);
        if (value.isEmpty()) {
            tvVehicleStatus.setVisibility(View.GONE);
            tvVehicleStatus.setText("");
            return;
        }
        tvVehicleStatus.setVisibility(View.VISIBLE);
        tvVehicleStatus.setText(value);
    }

    private String resolveVehicleStatus(@Nullable DispatchState dispatchState, @Nullable SignInState signInState) {
        if (dispatchState != null) {
            if (dispatchState.isReportedVehicleFailure()) {
                return "故障上报";
            }
            if (dispatchState.isRequestedCharge()) {
                return "申请充电";
            }
            if (dispatchState.isStartedBus()) {
                return "运营中";
            }
            if (dispatchState.isJoinedOperation()) {
                return "待发车";
            }
        }
        if (signInState != null && signInState.isSignedIn()) {
            return "已签到";
        }
        return "";
    }

    private String resolveInfoTips() {
        return LegacyHomeStatusRepository.getState(this).getInfoTips();
    }

    private void bindPassengerCounters(@NonNull StationState stationState) {
        JhyPassengerCounterState passengerState = shellRuntime.getPassengerCounterMonitor().getState();
        bindPassengerCounters(passengerState);
    }

    private void bindPassengerCounters(@NonNull JhyPassengerCounterState passengerState) {
        setText(R.id.tvFin001, passengerState.getFrontInText());
        setText(R.id.tvFout001, passengerState.getFrontOutText());
        setText(R.id.tvBin001, passengerState.getBackInText());
        setText(R.id.tvBout001, passengerState.getBackOutText());
        setText(R.id.tvAll001, passengerState.getTotalText());
    }

    private void applySpeedWarningStyle(boolean overspeed) {
        TextView speedView = findViewById(R.id.tvMileage);
        TextView speedLimitView = findViewById(R.id.tvSpeedLimit);
        if (speedView != null) {
            speedView.setTextColor(ContextCompat.getColor(this, overspeed ? R.color.c_ff0000 : R.color.c_ffffff));
        }
        if (speedLimitView != null) {
            speedLimitView.setTextColor(ContextCompat.getColor(this, overspeed ? R.color.c_ffffff : R.color.c_000000));
            speedLimitView.setBackgroundResource(overspeed ? R.drawable.txt_key_red : R.drawable.shape_red_oval);
        }
    }

    private String resolveGpioShouting(@Nullable ShellConfig config) {
        if (config == null) {
            return "";
        }
        if (config.getGpioConfig().getMode() != DeviceMode.REAL) {
            return "";
        }
        ShellConfig.OtherSettings otherSettings = config.getBasicSetupConfig().getOtherSettings();
        String primaryKey = valueOrDefault(otherSettings.getShoutingPrimaryGpioKey(), "").trim();
        String secondaryKey = valueOrDefault(otherSettings.getShoutingSecondaryGpioKey(), "").trim();
        if (primaryKey.isEmpty() || secondaryKey.isEmpty()) {
            return "";
        }
        try {
            int primary = shellRuntime.getGpioAdapter().read(primaryKey, TraceIds.next("legacy-home-shouting-primary"));
            int secondary = shellRuntime.getGpioAdapter().read(secondaryKey, TraceIds.next("legacy-home-shouting-secondary"));
            int route = resolveHomeShoutingRoute(primary, secondary);
            applyHomeShoutingRoute(config, route, primary, secondary);
            if (route == SHOUTING_ROUTE_OUTER) {
                return "SPK_OUT...";
            }
            if (route == SHOUTING_ROUTE_INNER) {
                return "SPK_IN...";
            }
            if (route == SHOUTING_ROUTE_BOTH) {
                return "SPK_IN_OUT...";
            }
        } catch (Exception ignore) {
            // Fall back to the shared voice-call state if shouting GPIOs are not readable.
            applyHomeShoutingIdle();
        }
        return "";
    }

    private int resolveHomeShoutingRoute(int primary, int secondary) {
        if (primary == 1 && secondary == 0) {
            return SHOUTING_ROUTE_OUTER;
        }
        if (primary == 0 && secondary == 1) {
            return SHOUTING_ROUTE_INNER;
        }
        if (primary == 0 && secondary == 0) {
            return SHOUTING_ROUTE_BOTH;
        }
        return SHOUTING_ROUTE_IDLE;
    }

    private void applyHomeShoutingRoute(@Nullable ShellConfig config, int route, int primary, int secondary) {
        boolean innerEnabled = route == SHOUTING_ROUTE_INNER || route == SHOUTING_ROUTE_BOTH;
        boolean outerEnabled = route == SHOUTING_ROUTE_OUTER || route == SHOUTING_ROUTE_BOTH;
        boolean active = innerEnabled || outerEnabled;
        if (route == lastHomeShoutingRoute) {
            if (active) {
                startHomeShoutingPlayback(config);
            } else {
                stopHomeShoutingPlayback();
            }
            return;
        }
        lastHomeShoutingRoute = route;
        writeHomeShoutingPin("inner_audio", innerEnabled ? 1 : 0);
        writeHomeShoutingPin("outer_audio", outerEnabled ? 1 : 0);
        writeHomeShoutingPin("headphone_detect_power", HEADPHONE_POWER_IDLE);
        writeHomeShoutingPin("inner_speaker", 0);
        applyHomeAudioRoute(config, active);
        if (active) {
            startHomeShoutingPlayback(config);
        } else {
            stopHomeShoutingPlayback();
        }
        AppLogCenter.log(
                LogCategory.DEVICE,
                LogLevel.INFO,
                "LegacyMainActivity",
                "首页喊话开关状态 route=" + describeHomeShoutingRoute(route)
                        + " / primary=" + primary
                        + " / secondary=" + secondary
                        + " / innerAudio=" + (innerEnabled ? 1 : 0)
                        + " / outerAudio=" + (outerEnabled ? 1 : 0)
                        + " / headphonePower=" + HEADPHONE_POWER_IDLE
                        + " / audioMode=LOCAL_RECORD_PLAY",
                TraceIds.next("legacy-home-shouting-route")
        );
    }

    private void applyHomeShoutingIdle() {
        if (lastHomeShoutingRoute == Integer.MIN_VALUE || lastHomeShoutingRoute == SHOUTING_ROUTE_IDLE) {
            return;
        }
        ShellConfig config = shellRuntime.getActiveConfig();
        applyHomeShoutingRoute(config, SHOUTING_ROUTE_IDLE, -1, -1);
    }

    private void applyHomeAudioRoute(@Nullable ShellConfig config, boolean active) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager == null) {
            return;
        }
        try {
            if (active) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(false);
                int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int percent = config == null ? 100 : config.getBasicSetupConfig().getOtherSettings().getShoutingVolume();
                int target = Math.max(0, Math.min(Math.round(max * Math.max(0, Math.min(percent, 100)) / 100.0f), max));
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0);
            } else {
                audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_NORMAL);
            }
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "LegacyMainActivity",
                    "首页喊话音频路由设置失败: " + safeMessage(e),
                    TraceIds.next("legacy-home-shouting-audio-route-error")
            );
        }
    }

    private void startHomeShoutingPlayback(@Nullable ShellConfig config) {
        if (homeShoutingPlaybackThread != null && homeShoutingPlaybackThread.isAlive()) {
            updateHomeShoutingTrackVolume(config);
            return;
        }
        if (!ensureHomeShoutingAudioReady(config)) {
            return;
        }
        homeShoutingRecording = true;
        homeShoutingPlaybackThread = new Thread(this::runHomeShoutingPlayback, "legacy-home-shouting-audio");
        homeShoutingPlaybackThread.setDaemon(true);
        homeShoutingPlaybackThread.start();
    }

    private boolean ensureHomeShoutingAudioReady(@Nullable ShellConfig config) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (!homeShoutingPermissionRequested) {
                homeShoutingPermissionRequested = true;
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, HOME_SHOUTING_PERMISSION_REQUEST);
            }
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "LegacyMainActivity",
                    "首页喊话录音权限未授权，无法启动本地喊话",
                    TraceIds.next("legacy-home-shouting-record-permission")
            );
            return false;
        }
        if (homeShoutingRecordBufferSize <= 0) {
            homeShoutingRecordBufferSize = AudioRecord.getMinBufferSize(
                    HOME_SHOUTING_SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
            );
        }
        if (homeShoutingTrackBufferSize <= 0) {
            homeShoutingTrackBufferSize = AudioTrack.getMinBufferSize(
                    HOME_SHOUTING_SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
            );
        }
        int recordBufferSize = Math.max(homeShoutingRecordBufferSize, HOME_SHOUTING_SAMPLE_RATE);
        int trackBufferSize = Math.max(homeShoutingTrackBufferSize, HOME_SHOUTING_SAMPLE_RATE);
        try {
            if (homeShoutingRecord == null) {
                homeShoutingRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        HOME_SHOUTING_SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        recordBufferSize
                );
            }
            if (homeShoutingTrack == null) {
                homeShoutingTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        HOME_SHOUTING_SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        trackBufferSize,
                        AudioTrack.MODE_STREAM
                );
            }
            updateHomeShoutingTrackVolume(config);
            boolean initialized = homeShoutingRecord.getState() == AudioRecord.STATE_INITIALIZED
                    && homeShoutingTrack.getState() == AudioTrack.STATE_INITIALIZED;
            if (!initialized) {
                AppLogCenter.log(
                        LogCategory.ERROR,
                        LogLevel.WARN,
                        "LegacyMainActivity",
                        "首页喊话音频设备未初始化",
                        TraceIds.next("legacy-home-shouting-audio-uninitialized")
                );
                releaseHomeShoutingAudio();
            }
            return initialized;
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "LegacyMainActivity",
                    "首页喊话音频初始化失败: " + safeMessage(e),
                    TraceIds.next("legacy-home-shouting-audio-init-error")
            );
            releaseHomeShoutingAudio();
            return false;
        }
    }

    private void updateHomeShoutingTrackVolume(@Nullable ShellConfig config) {
        if (homeShoutingTrack == null) {
            return;
        }
        int percent = config == null ? 50 : config.getBasicSetupConfig().getOtherSettings().getShoutingVolume();
        float volume = Math.max(0.0f, Math.min(percent, 100)) / 100.0f;
        try {
            homeShoutingTrack.setStereoVolume(volume, volume);
        } catch (Exception ignore) {
            // AudioTrack volume is best-effort on this legacy path.
        }
    }

    private void runHomeShoutingPlayback() {
        AudioRecord record = homeShoutingRecord;
        AudioTrack track = homeShoutingTrack;
        if (record == null || track == null) {
            homeShoutingRecording = false;
            return;
        }
        short[] buffer = new short[Math.max(1, Math.max(homeShoutingRecordBufferSize, HOME_SHOUTING_SAMPLE_RATE) / 2)];
        try {
            record.startRecording();
            track.play();
            AppLogCenter.log(
                    LogCategory.DEVICE,
                    LogLevel.INFO,
                    "LegacyMainActivity",
                    "首页喊话录音直放已启动",
                    TraceIds.next("legacy-home-shouting-audio-start")
            );
            while (homeShoutingRecording && !Thread.currentThread().isInterrupted()) {
                int read = record.read(buffer, 0, buffer.length);
                if (read > 0) {
                    track.write(buffer, 0, read);
                } else if (read < 0) {
                    throw new IllegalStateException("AudioRecord read failed: " + read);
                }
            }
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "LegacyMainActivity",
                    "首页喊话录音直放异常: " + safeMessage(e),
                    TraceIds.next("legacy-home-shouting-audio-error")
            );
        } finally {
            stopAudioTrackSafely(track);
            stopAudioRecordSafely(record);
            AppLogCenter.log(
                    LogCategory.DEVICE,
                    LogLevel.INFO,
                    "LegacyMainActivity",
                    "首页喊话录音直放已停止",
                    TraceIds.next("legacy-home-shouting-audio-stop")
            );
        }
    }

    private void stopHomeShoutingPlayback() {
        homeShoutingRecording = false;
        if (homeShoutingRecord != null) {
            stopAudioRecordSafely(homeShoutingRecord);
        }
        if (homeShoutingTrack != null) {
            stopAudioTrackSafely(homeShoutingTrack);
        }
        Thread thread = homeShoutingPlaybackThread;
        homeShoutingPlaybackThread = null;
        if (thread != null) {
            thread.interrupt();
        }
    }

    private void releaseHomeShoutingAudio() {
        stopHomeShoutingPlayback();
        AudioTrack track = homeShoutingTrack;
        homeShoutingTrack = null;
        if (track != null) {
            stopAudioTrackSafely(track);
            track.release();
        }
        AudioRecord record = homeShoutingRecord;
        homeShoutingRecord = null;
        if (record != null) {
            stopAudioRecordSafely(record);
            record.release();
        }
    }

    private void stopAudioTrackSafely(@NonNull AudioTrack track) {
        try {
            if (track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                track.stop();
            }
        } catch (Exception ignore) {
            // Already stopped or not initialized.
        }
    }

    private void stopAudioRecordSafely(@NonNull AudioRecord record) {
        try {
            if (record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                record.stop();
            }
        } catch (Exception ignore) {
            // Already stopped or not initialized.
        }
    }

    private void writeHomeShoutingPin(String pinKey, int value) {
        try {
            shellRuntime.getGpioAdapter().write(pinKey, value, "legacy-home-shouting-" + pinKey + "-" + value);
        } catch (Exception ignore) {
            // Keep the home screen responsive even when a GPIO output is unavailable.
        }
    }

    private String describeHomeShoutingRoute(int route) {
        switch (route) {
            case SHOUTING_ROUTE_OUTER:
                return "OUTER";
            case SHOUTING_ROUTE_INNER:
                return "INNER";
            case SHOUTING_ROUTE_BOTH:
                return "BOTH";
            case SHOUTING_ROUTE_IDLE:
                return "IDLE";
            default:
                return "UNKNOWN(" + route + ")";
        }
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

    private String resolvePlannedTimeHtml(@Nullable DispatchState dispatchState) {
        return getString(
                R.string.main_planned_time,
                resolveTripNo(dispatchState),
                dispatchState == null ? "-- : --" : valueOrDefault(dispatchState.getPlannedDepartureTime(), "-- : --"),
                dispatchState == null ? "-- : --" : valueOrDefault(dispatchState.getPlannedArrivalTime(), "-- : --")
        );
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
        String direction = valueOrDefault(resourceState.getDirectionText(), valueOrDefault(stationState.getDirectionText(), "上行"));
        List<String> stations = profile.stationsForDirection(direction);
        if (stations.isEmpty()) {
            return;
        }
        // 只在线路或站点内容“真的”变了（换线/换向得到不同的站序）时才重置游标。
        // 此前用终点站名/方向文本做对账，字段格式只要不完全一致就误判为变化，
        // 于是每次首页刷新都会 applyLineProfile 把游标打回起点站，正在报站的推进被吃掉。
        boolean lineChanged = !profile.matchesLineName(stationState.getLineName());
        boolean routeChanged = !stationState.matchesRoute(stations);
        if (lineChanged || routeChanged) {
            stationState.applyLineProfile(profile.getLineName(), direction, stations);
            stationState.setLineAttribute(profile.getLineAttribute());
        } else if (!profile.getLineAttribute().equals(valueOrDefault(stationState.getLineAttribute(), "-"))) {
            stationState.setLineAttribute(profile.getLineAttribute());
        }
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

    private String safeMessage(Exception e) {
        String message = e == null ? null : e.getMessage();
        return message == null || message.trim().isEmpty() ? "unknown" : message.trim();
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
        if (stationState.isCrossingReminderActive()) {
            String crossSpeedLimit = valueOrDefault(stationState.getActiveCrossSpeedLimit(), "0");
            return "--".equals(crossSpeedLimit) ? "0" : crossSpeedLimit;
        }
        String speedLimit = profile.speedLimitForDirection(stationState.getDirectionText(), stationState.getDisplayStationNo());
        return valueOrDefault(speedLimit, "0");
    }

    private boolean isOverspeed(@Nullable String speedKmh, @Nullable String speedLimit) {
        try {
            int speed = Integer.parseInt(valueOrDefault(speedKmh, "0"));
            int limit = Integer.parseInt(valueOrDefault(speedLimit, "0"));
            return limit > 0 && speed > limit;
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * 启动GPIO监听线程（对标现场版M90的MonitorThread）
     */
    private void startHomeMonitorGpioThread() {
        stopHomeMonitorGpioThread();
        homeMonitorGpioRunning = true;
        homeMonitorGpioThread = new Thread(() -> {
            String traceId = TraceIds.next("home-monitor-gpio-thread");
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyMainActivity", "GPIO监听线程已启动", traceId);
            
            while (homeMonitorGpioRunning && !Thread.currentThread().isInterrupted()) {
                try {
                    // 如果GPIO监听被暂停（音频播放期间），跳过本次轮询
                    if (!homeMonitorGpioPaused) {
                        // 每350ms触发一次刷新，让updateHomeDvrPanel去读取GPIO并决定视频模式
                        runOnUiThread(() -> {
                            ShellConfig config = shellRuntime.getActiveConfig();
                            if (config != null) {
                                updateHomeDvrPanel(config);
                            }
                        });
                    }
                    
                    // 主循环间隔350ms（对标现场版）
                    Thread.sleep(HOME_MONITOR_GPIO_POLL_MS);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, "LegacyMainActivity", 
                        "GPIO监听异常: " + e.getMessage(), traceId);
                    try {
                        Thread.sleep(HOME_MONITOR_GPIO_POLL_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyMainActivity", "GPIO监听线程已停止", traceId);
        }, "home-monitor-gpio-thread");
        
        homeMonitorGpioThread.setDaemon(true);
        homeMonitorGpioThread.start();
    }

    /**
     * 停止GPIO监听线程
     */
    private void stopHomeMonitorGpioThread() {
        homeMonitorGpioRunning = false;
        if (homeMonitorGpioThread != null) {
            homeMonitorGpioThread.interrupt();
            try {
                homeMonitorGpioThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            homeMonitorGpioThread = null;
        }
    }
    
    /**
     * 暂停GPIO监听（音频播放期间调用，避免GPIO冲突）
     */
    public void pauseHomeMonitorGpio() {
        homeMonitorGpioPaused = true;
        AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyMainActivity", 
            "GPIO监听已暂停（音频播放期间）", "home-monitor-gpio-pause");
    }
    
    /**
     * 恢复GPIO监听（音频播放结束后调用）
     */
    public void resumeHomeMonitorGpio() {
        homeMonitorGpioPaused = false;
        // Audio routing may leave both monitor inputs low. Treat the first reading after
        // playback as a fresh baseline so it cannot turn the home page into the large
        // reverse-camera layout without a real GPIO edge.
        startupMonitorGpioBaselineCaptured = false;
        startupMonitorPrimary = Integer.MIN_VALUE;
        startupMonitorSecondary = Integer.MIN_VALUE;
        lastGpioResolvedMode = null;
        gpioStableCount = 0;
        homeMonitorStartupResetUntilMs = System.currentTimeMillis() + HOME_MONITOR_STARTUP_RESET_MS;
        currentHomeMonitorMode = HomeMonitorMode.DVR;
        applyHomeMonitorMode(HomeMonitorMode.DVR);
        closeHomeMonitorPreview(false);
        AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyMainActivity", 
            "GPIO监听已恢复，首页监控重置为 DVR 并重新采集基线", "home-monitor-gpio-resume");
        clockHandler.postDelayed(() -> {
            if (!homeMonitorGpioPaused && !isFinishing() && !isDestroyed()) {
                refreshHomeState();
            }
        }, HOME_MONITOR_SWITCH_DEBOUNCE_MS);
    }

    /**
     * 音频模块只持有 Application 上下文，因此通过当前可见首页转发 GPIO 暂停请求。
     */
    public static void pauseActiveHomeMonitorGpio() {
        LegacyMainActivity activity = activeHomeActivity;
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            activity.pauseHomeMonitorGpio();
        }
    }

    public static void resumeActiveHomeMonitorGpio() {
        LegacyMainActivity activity = activeHomeActivity;
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            activity.resumeHomeMonitorGpio();
        }
    }

    /**
     * 根据GPIO值解析视频模式（对标现场版逻辑）
     */
}
