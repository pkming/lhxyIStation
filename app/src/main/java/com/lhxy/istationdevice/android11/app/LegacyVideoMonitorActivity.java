package com.lhxy.istationdevice.android11.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

/**
 * 旧版视频监控页。
 * <p>
 * 旧项目这里除了 Camera 预览，还有 DVR 键位发码和触摸协议。
 * 新壳当前已经把旧页壳、Camera 开关、DVR 键位和第一版触摸协议接回正式入口。
 * 触摸协议目前按文档摘要假设使用 M90 1280x800 坐标系，并把 down/move/up 映射成一帧触摸发送。
 */
public final class LegacyVideoMonitorActivity extends AppCompatActivity {
    private static final String EXTRA_SOURCE = "source";
    private static final int DVR_TOUCH_WIDTH = 1280;
    private static final int DVR_TOUCH_HEIGHT = 800;
    private static final long MOVE_REPORT_INTERVAL_MS = 40L;
    private long lastTouchMoveReportAt;

    public static Intent createIntent(Context context, String source) {
        Intent intent = new Intent(context, LegacyVideoMonitorActivity.class);
        intent.putExtra(EXTRA_SOURCE, source);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video_monitor);
        bindKeys();
        bindPreviewTouch();
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
        runCameraAction("open_default_camera", R.string.legacy_video_camera_opened, true);
    }

    @Override
    protected void onPause() {
        runCameraAction("close_default_camera", R.string.legacy_video_camera_closed, false);
        super.onPause();
    }

    private void bindKeys() {
        bindKey(R.id.butBack, null);
        bindKey(R.id.butNumber1, "1");
        bindKey(R.id.butNumber2, "2");
        bindKey(R.id.butNumber3, "3");
        bindKey(R.id.butNumber4, "4");
        bindKey(R.id.butNumber5, "5");
        bindKey(R.id.butNumber6, "6");
        bindKey(R.id.butNumber7, "7");
        bindKey(R.id.butNumber8, "8");
        bindKey(R.id.butNumber9, "9");
        bindKey(R.id.butNumber0, "0");
        bindKey(R.id.butDVRUP, "UP");
        bindKey(R.id.butDVRDown, "DOWN");
        bindKey(R.id.butDVRLeft, "LEFT");
        bindKey(R.id.butDVRRight, "RIGHT");
        bindKey(R.id.butDVRM, "M");
        bindKey(R.id.butDVRENT, "ENT");
        bindKey(R.id.butDVRDEL, "DEL");
        bindKey(R.id.butDVRESC, "ESC");
    }

    private void bindKey(int viewId, @Nullable String keyLabel) {
        View view = findViewById(viewId);
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> {
            if (viewId == R.id.butBack) {
                finish();
                return;
            }
            String message = getString(R.string.legacy_video_key_pressed, keyLabel == null ? "-" : keyLabel);
            ModuleRunResult result = runDvrKeyAction(viewId);
            Toast.makeText(this, result.isSuccess() ? message : result.describeInline(), Toast.LENGTH_SHORT).show();
            AppLogCenter.log(
                    result.isSuccess() ? LogCategory.UI : LogCategory.ERROR,
                    result.isSuccess() ? LogLevel.INFO : LogLevel.WARN,
                    "LegacyVideoMonitorActivity",
                    message + (result.isSuccess() ? " / 已发送 DVR 键码" : " / 发送失败: " + result.describeInline()),
                    TraceIds.next("legacy-video-key")
            );
        });
    }

    private void bindPreviewTouch() {
        View preview = findViewById(R.id.core_surface);
        if (preview == null) {
            return;
        }
        preview.setOnTouchListener((v, event) -> handlePreviewTouch(v, event));
    }

    private boolean handlePreviewTouch(View view, MotionEvent event) {
        if (event == null) {
            return false;
        }
        String phase = mapTouchPhase(event.getActionMasked());
        if (phase == null) {
            return false;
        }
        if ("move".equals(phase) && !shouldReportMove()) {
            return true;
        }
        float rawX = event.getX();
        float rawY = event.getY();
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
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

    private boolean shouldReportMove() {
        long now = System.currentTimeMillis();
        if (now - lastTouchMoveReportAt < MOVE_REPORT_INTERVAL_MS) {
            return false;
        }
        lastTouchMoveReportAt = now;
        return true;
    }

    private String buildTouchActionKey(String phase, int x, int y) {
        return "dvr_touch_" + phase + "_" + x + "_" + y;
    }

    private String mapTouchPhase(int actionMasked) {
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                return "down";
            case MotionEvent.ACTION_MOVE:
                return "move";
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

    private ModuleRunResult runDvrKeyAction(int viewId) {
        String actionKey = mapKeyAction(viewId);
        if (actionKey == null) {
            return ModuleRunResult.failure("camera_dvr", "摄像头/DVR", "DVR 键位发送失败", "未识别的键位");
        }
        return ShellRuntime.get().getModuleHub().runAction("camera_dvr", actionKey, TraceIds.next("legacy-video-key-action"));
    }

    private String mapKeyAction(int viewId) {
        if (viewId == R.id.butDVRM) {
            return "dvr_key_01";
        }
        if (viewId == R.id.butDVRENT) {
            return "dvr_key_02";
        }
        if (viewId == R.id.butDVRESC) {
            return "dvr_key_03";
        }
        if (viewId == R.id.butDVRUP) {
            return "dvr_key_04";
        }
        if (viewId == R.id.butDVRDown) {
            return "dvr_key_05";
        }
        if (viewId == R.id.butDVRLeft) {
            return "dvr_key_06";
        }
        if (viewId == R.id.butDVRRight) {
            return "dvr_key_07";
        }
        if (viewId == R.id.butDVRDEL) {
            return "dvr_key_08";
        }
        if (viewId == R.id.butNumber1) {
            return "dvr_key_09";
        }
        if (viewId == R.id.butNumber2) {
            return "dvr_key_0A";
        }
        if (viewId == R.id.butNumber3) {
            return "dvr_key_0B";
        }
        if (viewId == R.id.butNumber4) {
            return "dvr_key_0C";
        }
        if (viewId == R.id.butNumber5) {
            return "dvr_key_0D";
        }
        if (viewId == R.id.butNumber6) {
            return "dvr_key_0E";
        }
        if (viewId == R.id.butNumber7) {
            return "dvr_key_0F";
        }
        if (viewId == R.id.butNumber8) {
            return "dvr_key_10";
        }
        if (viewId == R.id.butNumber9) {
            return "dvr_key_11";
        }
        if (viewId == R.id.butNumber0) {
            return "dvr_key_12";
        }
        return null;
    }

    private void runCameraAction(String actionKey, int toastResId, boolean showToastOnSuccess) {
        ModuleRunResult result = ShellRuntime.get().getModuleHub()
                .runAction("camera_dvr", actionKey, TraceIds.next("legacy-video-" + actionKey));
        if (!result.isSuccess()) {
            Toast.makeText(this, result.describeInline(), Toast.LENGTH_SHORT).show();
            return;
        }
        if (showToastOnSuccess) {
            Toast.makeText(this, toastResId, Toast.LENGTH_SHORT).show();
        }
    }

    private String source() {
        String value = getIntent().getStringExtra(EXTRA_SOURCE);
        return value == null || value.trim().isEmpty() ? "unknown" : value.trim();
    }
}
