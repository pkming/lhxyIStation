package android.view;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.view.GLES20Layer;

/* JADX INFO: loaded from: classes.dex */
class GLES20TextureLayer extends GLES20Layer {
    private SurfaceTexture mSurface;
    private int mTexture;

    @Override // android.view.HardwareLayer
    void end(Canvas canvas) {
    }

    @Override // android.view.HardwareLayer
    HardwareCanvas getCanvas() {
        return null;
    }

    @Override // android.view.HardwareLayer
    void redrawLater(DisplayList displayList, Rect rect) {
    }

    @Override // android.view.HardwareLayer
    HardwareCanvas start(Canvas canvas) {
        return null;
    }

    @Override // android.view.HardwareLayer
    HardwareCanvas start(Canvas canvas, Rect rect) {
        return null;
    }

    GLES20TextureLayer(boolean z) {
        int[] iArr = new int[2];
        this.mLayer = GLES20Canvas.nCreateTextureLayer(z, iArr);
        if (this.mLayer != 0) {
            this.mTexture = iArr[0];
            this.mFinalizer = new GLES20Layer.Finalizer(this.mLayer);
        } else {
            this.mFinalizer = null;
        }
    }

    @Override // android.view.HardwareLayer
    boolean isValid() {
        return (this.mLayer == 0 || this.mTexture == 0) ? false : true;
    }

    @Override // android.view.HardwareLayer
    boolean resize(int i, int i2) {
        return isValid();
    }

    SurfaceTexture getSurfaceTexture() {
        if (this.mSurface == null) {
            this.mSurface = new SurfaceTexture(this.mTexture);
        }
        return this.mSurface;
    }

    void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        SurfaceTexture surfaceTexture2 = this.mSurface;
        if (surfaceTexture2 != null) {
            surfaceTexture2.release();
        }
        this.mSurface = surfaceTexture;
        surfaceTexture.attachToGLContext(this.mTexture);
    }

    @Override // android.view.HardwareLayer
    void update(int i, int i2, boolean z) {
        super.update(i, i2, z);
        GLES20Canvas.nUpdateTextureLayer(this.mLayer, i, i2, z, this.mSurface);
    }

    @Override // android.view.HardwareLayer
    void setOpaque(boolean z) {
        throw new UnsupportedOperationException("Use update(int, int, boolean) instead");
    }

    @Override // android.view.HardwareLayer
    void setTransform(Matrix matrix) {
        GLES20Canvas.nSetTextureLayerTransform(this.mLayer, matrix.native_instance);
    }
}
