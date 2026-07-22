package com.lianhexinye.m90.camera;

import android.content.Context;
import android.hardware.Camera;
import android.media.videoeditor.MediaProperties;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import java.io.IOException;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG;
    Camera mCamera;
    SurfaceHolder mHolder;
    Camera.Size mPreviewSize;
    boolean mPreviewed;
    List<Camera.Size> mSupportedPreviewSizes;
    boolean mSurfaceCreated;
    SurfaceView mSurfaceView;

    public Preview(Context context, SurfaceView surfaceView) {
        super(context);
        this.TAG = "Preview";
        this.mPreviewed = false;
        this.mSurfaceCreated = false;
        this.mSurfaceView = surfaceView;
        SurfaceHolder holder = surfaceView.getHolder();
        this.mHolder = holder;
        holder.addCallback(this);
        this.mHolder.setType(3);
        this.mSurfaceView.setZOrderMediaOverlay(true);
        setBackgroundColor(0);
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
        if (camera != null) {
            this.mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
            requestLayout();
            Camera.Parameters parameters = this.mCamera.getParameters();
            parameters.setPreviewSize(MediaProperties.HEIGHT_720, 576);
            if (parameters.getSupportedFocusModes().contains("auto")) {
                parameters.setFocusMode("auto");
                this.mCamera.setParameters(parameters);
            }
            if (this.mPreviewed || !this.mSurfaceCreated) {
                return;
            }
            try {
                this.mCamera.setPreviewDisplay(this.mHolder);
                this.mCamera.startPreview();
                this.mPreviewed = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int iResolveSize = resolveSize(getSuggestedMinimumWidth(), i);
        int iResolveSize2 = resolveSize(getSuggestedMinimumHeight(), i2);
        setMeasuredDimension(iResolveSize, iResolveSize2);
        List<Camera.Size> list = this.mSupportedPreviewSizes;
        if (list != null) {
            this.mPreviewSize = getOptimalPreviewSize(list, iResolveSize, iResolveSize2);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        if (!z || getChildCount() <= 0) {
            return;
        }
        View childAt = getChildAt(0);
        int i7 = i3 - i;
        int i8 = i4 - i2;
        Camera.Size size = this.mPreviewSize;
        if (size != null) {
            i5 = size.width;
            i6 = this.mPreviewSize.height;
        } else {
            i5 = i7;
            i6 = i8;
        }
        int i9 = i7 * i6;
        int i10 = i8 * i5;
        if (i9 > i10) {
            int i11 = i10 / i6;
            childAt.layout((i7 - i11) / 2, 0, (i7 + i11) / 2, i8);
        } else {
            int i12 = i9 / i5;
            childAt.layout(0, (i8 - i12) / 2, i7, (i8 + i12) / 2);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        LogUtils.d("Preview", "********surfaceCreated************");
        try {
            Camera camera = this.mCamera;
            if (camera == null || this.mPreviewed) {
                return;
            }
            this.mHolder = surfaceHolder;
            camera.setPreviewDisplay(surfaceHolder);
            this.mCamera.startPreview();
            this.mPreviewed = true;
            this.mSurfaceCreated = true;
        } catch (IOException e) {
            LogUtils.e("Preview", "IOException caused by setPreviewDisplay()", (Throwable) e);
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        return this.mHolder;
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Camera camera = this.mCamera;
        if (camera != null) {
            camera.stopPreview();
            this.mPreviewed = false;
        }
        this.mSurfaceCreated = false;
    }

    public boolean isPreviewing() {
        return this.mPreviewed;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> list, int i, int i2) {
        double d = ((double) i) / ((double) i2);
        Camera.Size size = null;
        if (list == null) {
            return null;
        }
        double dAbs = Double.MAX_VALUE;
        double dAbs2 = Double.MAX_VALUE;
        for (Camera.Size size2 : list) {
            if (Math.abs((((double) size2.width) / ((double) size2.height)) - d) <= 0.1d && Math.abs(size2.height - i2) < dAbs2) {
                dAbs2 = Math.abs(size2.height - i2);
                size = size2;
            }
        }
        if (size == null) {
            for (Camera.Size size3 : list) {
                if (Math.abs(size3.height - i2) < dAbs) {
                    size = size3;
                    dAbs = Math.abs(size3.height - i2);
                }
            }
        }
        return size;
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        LogUtils.d("Preview", "==========surfaceChanged=================");
    }
}
