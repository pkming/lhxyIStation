package com.lianhexinye.m90.ui.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.TimedRemoteCaller;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.camera.Preview;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.mvp.BasePresenter;
import com.lianhexinye.m90.mvp.BaseView;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.lianhexinye.m90.serialport.DVRProtocol;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes2.dex */
public class VideoMonitorActivity extends MvpActivity<BaseView, BasePresenter> implements BaseView {

    @BindView(R.id.core_surface)
    SurfaceView coreSurface;
    private Timer dvrOperatorTime;
    private Timer hideTimer;

    @BindView(R.id.lyBut)
    LinearLayout lyBut;

    @BindView(R.id.lyDvrCameraOpen)
    LinearLayout lyDvrCameraOpen;

    @BindView(R.id.lyDvrSurfaceView)
    LinearLayout lyDvrSurfaceView;

    @BindView(R.id.tvBack)
    TextView tvBack;
    private final String TAG = "VideoMonitorActivity";
    private Camera camera = null;
    private Camera.Parameters param = null;
    private boolean previewRunning = false;
    private boolean isOpen = false;
    private Preview mDvrPreview = null;
    private Camera mDvrCamera = null;

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video_monitor);
        ButterKnife.bind(this);
        LogUtils.d("VideoMonitorActivity", "onCreate----");
        Timer timer = new Timer();
        this.dvrOperatorTime = timer;
        timer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.VideoMonitorActivity.1
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                VideoMonitorActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.VideoMonitorActivity.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        VideoMonitorActivity.this.lyDvrCameraOpen.setVisibility(8);
                        VideoMonitorActivity.this.lyDvrSurfaceView.setVisibility(0);
                        VideoMonitorActivity.this.openDVRCamera();
                    }
                });
            }
        }, FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
    }

    @OnClick({R.id.tvBack})
    public void onViewClicked(View view) {
        if (view.getId() != R.id.tvBack) {
            return;
        }
        finish();
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && this.lyBut.getVisibility() == 8) {
            this.lyBut.setVisibility(0);
            delayedHideSetBut();
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private void delayedHideSetBut() {
        Timer timer = this.hideTimer;
        if (timer != null) {
            timer.cancel();
        }
        Timer timer2 = new Timer();
        this.hideTimer = timer2;
        timer2.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.VideoMonitorActivity.2
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                VideoMonitorActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.VideoMonitorActivity.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (VideoMonitorActivity.this.lyBut != null) {
                            VideoMonitorActivity.this.lyBut.setVisibility(8);
                        }
                    }
                });
            }
        }, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            LogUtils.d("onTouchEvent-ACTION_DOWN", "x坐标：" + x + "y坐标" + y);
            DVRProtocol.getInstance().sendTouchKey(x, y, (byte) 1);
        } else if (action == 1) {
            int x2 = (int) motionEvent.getX();
            int y2 = (int) motionEvent.getY();
            LogUtils.d("onTouchEvent-ACTION_UP", "x坐标：" + x2 + "y坐标" + y2);
            DVRProtocol.getInstance().sendTouchKey(x2, y2, (byte) 0);
        }
        return true;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity
    protected BasePresenter createPresenter() {
        return new BasePresenter();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openDVRCamera() {
        this.coreSurface.getHolder().addCallback(new CoreSurfaceViewCallback());
        SurfaceHolder holder = this.coreSurface.getHolder();
        this.coreSurface.getHolder();
        holder.setType(3);
    }

    private class CoreSurfaceViewCallback implements SurfaceHolder.Callback {
        private CoreSurfaceViewCallback() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            LogUtils.d("VideoMonitorActivity", "surfaceChanged单独的DVR摄像头画面--------");
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [com.lianhexinye.m90.ui.activity.VideoMonitorActivity$CoreSurfaceViewCallback$1] */
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(final SurfaceHolder surfaceHolder) {
            LogUtils.d("VideoMonitorActivity", "surfaceCreated单独的DVR摄像头画面--------");
            new Thread() { // from class: com.lianhexinye.m90.ui.activity.VideoMonitorActivity.CoreSurfaceViewCallback.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    if (VideoMonitorActivity.this.previewRunning) {
                        return;
                    }
                    VideoMonitorActivity.this.InitCamera(surfaceHolder);
                }
            }.start();
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            LogUtils.d("VideoMonitorActivity", "surfaceDestroyed单独的DVR摄像头画面--------");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void InitCamera(SurfaceHolder surfaceHolder) {
        LogUtils.d("VideoMonitorActivity", "初始化打开预览单独的DVR摄像头画面--------");
        try {
            if (this.isOpen) {
                return;
            }
            Camera cameraOpen = Camera.open(0);
            this.camera = cameraOpen;
            Camera.Parameters parameters = cameraOpen.getParameters();
            this.param = parameters;
            parameters.setPreviewSize(1920, 1080);
            if (this.param.getSupportedFocusModes().contains("auto")) {
                this.param.setFocusMode("auto");
                this.camera.setParameters(this.param);
            }
            this.camera.setPreviewDisplay(surfaceHolder);
            this.camera.startPreview();
            this.previewRunning = true;
            this.isOpen = true;
        } catch (IOException | RuntimeException e) {
            LogUtils.e("CameraError", "Fail to connect to camera service:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initAHDCamera() {
        LogUtils.d("initAHDCamera", "初始化dvr-准备操作的摄像头----");
        this.mDvrPreview = new Preview(this, this.coreSurface);
        try {
            Camera cameraOpen = Camera.open(0);
            this.mDvrCamera = cameraOpen;
            this.mDvrPreview.setCamera(cameraOpen);
        } catch (RuntimeException unused) {
            Toast.makeText(this, "No camera hardware found", 1).show();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        LogUtils.d("VideoMonitorActivity", "onResume----");
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        LogUtils.d("VideoMonitorActivity", "onPause----");
        closeCamera();
    }

    private void closeCamera() {
        if (this.camera != null) {
            LogUtils.d("VideoMonitorActivity", "关闭预览单独的DVR摄像头画面，摄像头不为空--------");
            this.camera.setPreviewCallback(null);
            if (this.previewRunning) {
                this.camera.stopPreview();
                this.previewRunning = false;
            }
            this.camera.release();
            this.param = null;
            this.camera = null;
            this.isOpen = false;
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("VideoMonitorActivity", "onDestroy----");
        Timer timer = this.dvrOperatorTime;
        if (timer != null) {
            timer.cancel();
        }
        Timer timer2 = this.hideTimer;
        if (timer2 != null) {
            timer2.cancel();
        }
    }
}
