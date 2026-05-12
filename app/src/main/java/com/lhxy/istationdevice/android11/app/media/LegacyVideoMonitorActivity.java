package com.lhxy.istationdevice.android11.app.media;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lhxy.istationdevice.android11.app.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.module.CameraDvrBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

/**
 * 旧版视频监控页。
 * <p>
 * 旧项目这里主要是 Camera 预览、返回浮层和触摸协议。
 * 新壳当前已经把旧页壳、等待层、返回控件和触摸协议接回正式入口。
 * 触摸协议按 M90 1280x800 坐标系缩放，并只发送 down/up 两种事件。
 * <p>
 * 查找关键字：视频监控页、DVR 触摸、Camera 预览、自动切监控。
 */
public final class LegacyVideoMonitorActivity extends AppCompatActivity {
    private static final String EXTRA_SOURCE = "source";
    private static final int DVR_TOUCH_WIDTH = 1280;
    private static final int DVR_TOUCH_HEIGHT = 800;
    private static final long DVR_OPENING_DELAY_MS = 2_000L;
    private static final long HIDE_CONTROLS_DELAY_MS = 5_000L;
    private static final long AUTO_MONITOR_POLL_INTERVAL_MS = 500L;
    private SurfaceView previewSurface;
    private View controlsContainer;
    private View openingOverlay;
    private View contentContainer;
    private boolean pageResumed;
    private boolean openingCompleted;
    private boolean previewOpened;
    private String currentCameraKey;
    private String lastAutoMonitorCameraKey;
    private String manualCameraKey;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable hideControlsRunnable = new Runnable() {
        @Override
        public void run() {
            if (controlsContainer != null) {
                controlsContainer.setVisibility(View.GONE);
            }
        }
    };
    private final Runnable autoMonitorRunnable = new Runnable() {
        @Override
        public void run() {
            if (!pageResumed) {
                return;
            }
            syncAutoMonitorChannel();
            uiHandler.postDelayed(this, AUTO_MONITOR_POLL_INTERVAL_MS);
        }
    };
    private final Runnable finishOpeningRunnable = new Runnable() {
        @Override
        public void run() {
            if (!pageResumed) {
                return;
            }
            openingCompleted = true;
            if (openingOverlay != null) {
                openingOverlay.setVisibility(View.GONE);
            }
            if (contentContainer != null) {
                contentContainer.setVisibility(View.VISIBLE);
            }
            openPreviewIfReady(false);
            syncAutoMonitorChannel();
            scheduleAutoMonitorSync();
        }
    };

    /**
     * 创建视频监控页入口，并记录来源页面用于日志定位。
     */
    public static Intent createIntent(Context context, String source) {
        Intent intent = new Intent(context, LegacyVideoMonitorActivity.class);
        intent.putExtra(EXTRA_SOURCE, source);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video_monitor);
        controlsContainer = findViewById(R.id.lyBut);
        openingOverlay = findViewById(R.id.lyDvrCameraOpen);
        contentContainer = findViewById(R.id.lyDvrSurfaceView);
        bindBackButton();
        bindChannelButtons();
        bindCameraPreview();
        applyOpeningState();
        AppLogCenter.log(
                LogCategory.UI,
                LogLevel.INFO,
                "LegacyVideoMonitorActivity",
                "video monitor opened from " + source(),
                TraceIds.next("legacy-video-open")
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageResumed = true;
        applyOpeningState();
        uiHandler.postDelayed(finishOpeningRunnable, DVR_OPENING_DELAY_MS);
    }

    @Override
    protected void onPause() {
        pageResumed = false;
        uiHandler.removeCallbacks(finishOpeningRunnable);
        cancelAutoMonitorSync();
        lastAutoMonitorCameraKey = null;
        cancelControlsHide();
        applyOpeningState();
        closeActivePreview(false);
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event != null && event.getActionMasked() == MotionEvent.ACTION_DOWN && controlsContainer != null) {
            if (openingCompleted && controlsContainer.getVisibility() != View.VISIBLE) {
                controlsContainer.setVisibility(View.VISIBLE);
            }
            if (openingCompleted) {
                scheduleControlsHide();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!openingCompleted) {
            return super.onTouchEvent(event);
        }
        if (handlePageTouch(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复打开中遮罩，等预设延时结束后再显示视频区和控制条。
     */
    private void applyOpeningState() {
        openingCompleted = false;
        if (openingOverlay != null) {
            openingOverlay.setVisibility(View.VISIBLE);
        }
        if (contentContainer != null) {
            contentContainer.setVisibility(View.GONE);
        }
        if (controlsContainer != null) {
            controlsContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 绑定返回按钮，直接退出旧视频页。
     */
    private void bindBackButton() {
        View view = findViewById(R.id.tvBack);
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * 绑定顶部四个视频通道按钮。
     */
    private void bindChannelButtons() {
        bindChannelButton(R.id.tvMiddleDoor, "middle_door");
        bindChannelButton(R.id.tvReverse, "reverse");
        bindChannelButton(R.id.tvDvr, "av_out");
        bindChannelButton(R.id.tvMonitor, "monitor");
    }

    /**
     * 手动切换指定摄像头通道，并暂时压过自动监控切换。
     */
    private void bindChannelButton(int viewId, String cameraKey) {
        View view = findViewById(viewId);
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> {
            manualCameraKey = cameraKey;
            lastAutoMonitorCameraKey = cameraKey;
            openCameraChannel(cameraKey, true);
            scheduleControlsHide();
        });
    }

    /**
     * 绑定预览 Surface 生命周期，Surface 就绪后再真正打开预览。
     */
    private void bindCameraPreview() {
        previewSurface = findViewById(R.id.core_surface);
        if (previewSurface == null) {
            return;
        }
        previewSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                openPreviewIfReady(true);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (previewOpened) {
                    return;
                }
                openPreviewIfReady(false);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                closeActivePreview(false);
            }
        });
    }

    private void scheduleControlsHide() {
        cancelControlsHide();
        uiHandler.postDelayed(hideControlsRunnable, HIDE_CONTROLS_DELAY_MS);
    }

    private void cancelControlsHide() {
        uiHandler.removeCallbacks(hideControlsRunnable);
    }

    private void scheduleAutoMonitorSync() {
        cancelAutoMonitorSync();
        uiHandler.postDelayed(autoMonitorRunnable, AUTO_MONITOR_POLL_INTERVAL_MS);
    }

    private void cancelAutoMonitorSync() {
        uiHandler.removeCallbacks(autoMonitorRunnable);
    }

    /**
     * 在页面、遮罩、Surface 都准备好后尝试打开摄像头预览。
     */
    private void openPreviewIfReady(boolean showToastOnSuccess) {
        if (!pageResumed || !openingCompleted || previewSurface == null || previewOpened) {
            return;
        }
        SurfaceHolder holder = previewSurface.getHolder();
        if (holder == null || holder.getSurface() == null || !holder.getSurface().isValid()) {
            return;
        }
        try {
            ShellRuntime runtime = ShellRuntime.get();
            ShellConfig shellConfig = runtime.getActiveConfig();
            if (shellConfig == null) {
                throw new IllegalStateException("Camera config is not ready");
            }
            openPreviewForChannel(resolveDefaultCameraKey(shellConfig), showToastOnSuccess);
        } catch (Exception e) {
            previewOpened = false;
            Toast.makeText(this, "Camera 预览打开失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.ERROR,
                    "LegacyVideoMonitorActivity",
                    "camera preview open failed: " + e.getMessage(),
                    TraceIds.next("legacy-video-preview-error")
            );
        }
    }

    private String resolveDefaultCameraKey(ShellConfig shellConfig) {
        if (manualCameraKey != null && !manualCameraKey.trim().isEmpty()) {
            return manualCameraKey;
        }
        if (currentCameraKey != null && !currentCameraKey.trim().isEmpty()) {
            return currentCameraKey;
        }
        return shellConfig.getDebugReplay().getCameraChannelKey();
    }

    private void openCameraChannel(String cameraKey, boolean showToastOnSuccess) {
        if (previewSurface == null) {
            return;
        }
        SurfaceHolder holder = previewSurface.getHolder();
        if (holder == null || holder.getSurface() == null || !holder.getSurface().isValid()) {
            currentCameraKey = cameraKey;
            return;
        }
        openPreviewForChannel(cameraKey, showToastOnSuccess);
    }

    private void syncAutoMonitorChannel() {
        if (manualCameraKey != null && !manualCameraKey.trim().isEmpty()) {
            return;
        }
        String desiredCameraKey = resolveAutoMonitorCameraKey();
        if (desiredCameraKey == null || desiredCameraKey.trim().isEmpty()) {
            return;
        }
        if (desiredCameraKey.equals(lastAutoMonitorCameraKey)) {
            return;
        }
        lastAutoMonitorCameraKey = desiredCameraKey;
        openCameraChannel(desiredCameraKey, false);
    }

    @Nullable
    private String resolveAutoMonitorCameraKey() {
        TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("camera_dvr");
        if (!(module instanceof CameraDvrBusinessModule)) {
            return null;
        }
        try {
            return ((CameraDvrBusinessModule) module).resolveMonitorCameraKey(TraceIds.next("legacy-video-auto-monitor"));
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "LegacyVideoMonitorActivity",
                    "resolve auto monitor failed: " + e.getMessage(),
                    TraceIds.next("legacy-video-auto-monitor-error")
            );
            return null;
        }
    }

    private void openPreviewForChannel(String cameraKey, boolean showToastOnSuccess) {
        if (cameraKey == null || cameraKey.trim().isEmpty() || previewSurface == null) {
            return;
        }
        SurfaceHolder holder = previewSurface.getHolder();
        if (holder == null || holder.getSurface() == null || !holder.getSurface().isValid()) {
            currentCameraKey = cameraKey;
            previewOpened = false;
            return;
        }
        try {
            String nextCameraKey = cameraKey.trim();
            if (previewOpened && currentCameraKey != null && !currentCameraKey.equals(nextCameraKey)) {
                closeActivePreview(false);
            }
            currentCameraKey = nextCameraKey;
            ShellRuntime.get().getCameraAdapter().openPreview(
                    currentCameraKey,
                    holder.getSurface(),
                    Math.max(1, previewSurface.getWidth()),
                    Math.max(1, previewSurface.getHeight()),
                    TraceIds.next("legacy-video-preview-" + currentCameraKey)
            );
            previewOpened = true;
            if (showToastOnSuccess) {
                Toast.makeText(
                        this,
                        getString(R.string.legacy_video_channel_opened, cameraLabel(currentCameraKey)),
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (Exception e) {
            previewOpened = false;
            Toast.makeText(this, "Camera 预览打开失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.ERROR,
                    "LegacyVideoMonitorActivity",
                    "camera preview open failed: " + cameraKey + " / " + e.getMessage(),
                    TraceIds.next("legacy-video-preview-error")
            );
        }
    }

    private void closeActivePreview(boolean showToastOnSuccess) {
        String cameraKey = currentCameraKey;
        previewOpened = false;
        if (cameraKey == null || cameraKey.trim().isEmpty()) {
            if (showToastOnSuccess) {
                Toast.makeText(this, R.string.legacy_video_channel_closed, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        try {
            ShellRuntime.get().getCameraAdapter().close(cameraKey, TraceIds.next("legacy-video-close-" + cameraKey));
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "LegacyVideoMonitorActivity",
                    "camera preview close failed: " + cameraKey + " / " + e.getMessage(),
                    TraceIds.next("legacy-video-close-error")
            );
        }
        if (showToastOnSuccess) {
            Toast.makeText(this, R.string.legacy_video_channel_closed, Toast.LENGTH_SHORT).show();
        }
    }

    private String cameraLabel(String cameraKey) {
        if ("middle_door".equals(cameraKey)) {
            return "中门";
        }
        if ("reverse".equals(cameraKey)) {
            return "倒车";
        }
        if ("av_out".equals(cameraKey)) {
            return "DVR";
        }
        if ("monitor".equals(cameraKey)) {
            return "监控";
        }
        return cameraKey == null ? "-" : cameraKey;
    }

    private boolean handlePageTouch(MotionEvent event) {
        if (event == null) {
            return false;
        }
        String phase = mapTouchPhase(event.getActionMasked());
        if (phase == null) {
            return false;
        }
        float rawX = event.getX();
        float rawY = event.getY();
        View rootView = findViewById(android.R.id.content);
        int viewWidth = rootView == null ? 0 : rootView.getWidth();
        int viewHeight = rootView == null ? 0 : rootView.getHeight();
        int touchX = scaleCoordinate(rawX, viewWidth, DVR_TOUCH_WIDTH);
        int touchY = scaleCoordinate(rawY, viewHeight, DVR_TOUCH_HEIGHT);
        ModuleRunResult result = ShellRuntime.get().getModuleHub().runAction(
                "camera_dvr",
                buildTouchActionKey(phase, touchX, touchY),
                TraceIds.next("legacy-video-touch")
        );
        AppLogCenter.log(
                result.isSuccess() ? LogCategory.UI : LogCategory.ERROR,
                result.isSuccess() ? LogLevel.INFO : LogLevel.WARN,
                "LegacyVideoMonitorActivity",
                "touch phase=" + phase
                        + " / raw=(" + Math.round(rawX) + "," + Math.round(rawY) + ")"
                        + " / view=" + viewWidth + "x" + viewHeight
                        + " / scaled=(" + touchX + "," + touchY + ")"
                        + (result.isSuccess() ? " / DVR touch sent" : " / failed: " + result.describeInline()),
                TraceIds.next("legacy-video-touch-log")
        );
        if (!result.isSuccess() && ("down".equals(phase) || "up".equals(phase))) {
            Toast.makeText(this, result.describeInline(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private String buildTouchActionKey(String phase, int x, int y) {
        return "dvr_touch_" + phase + "_" + x + "_" + y;
    }

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

    private String source() {
        String value = getIntent().getStringExtra(EXTRA_SOURCE);
        return value == null || value.trim().isEmpty() ? "unknown" : value.trim();
    }
}
