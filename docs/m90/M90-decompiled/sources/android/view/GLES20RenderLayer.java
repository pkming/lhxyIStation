package android.view;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.GLES20Layer;

/* JADX INFO: loaded from: classes.dex */
class GLES20RenderLayer extends GLES20Layer {
    private final GLES20Canvas mCanvas;
    private int mLayerHeight;
    private int mLayerWidth;

    @Override // android.view.HardwareLayer
    void setTransform(Matrix matrix) {
    }

    GLES20RenderLayer(int i, int i2, boolean z) {
        super(i, i2, z);
        int[] iArr = new int[2];
        this.mLayer = GLES20Canvas.nCreateLayer(i, i2, z, iArr);
        if (this.mLayer != 0) {
            this.mLayerWidth = iArr[0];
            this.mLayerHeight = iArr[1];
            this.mCanvas = new GLES20Canvas(this.mLayer, true ^ z);
            this.mFinalizer = new GLES20Layer.Finalizer(this.mLayer);
            return;
        }
        this.mCanvas = null;
        this.mFinalizer = null;
    }

    @Override // android.view.HardwareLayer
    boolean isValid() {
        return this.mLayer != 0 && this.mLayerWidth > 0 && this.mLayerHeight > 0;
    }

    @Override // android.view.HardwareLayer
    boolean resize(int i, int i2) {
        if (!isValid() || i <= 0 || i2 <= 0) {
            return false;
        }
        this.mWidth = i;
        this.mHeight = i2;
        if (i != this.mLayerWidth || i2 != this.mLayerHeight) {
            int[] iArr = new int[2];
            if (GLES20Canvas.nResizeLayer(this.mLayer, i, i2, iArr)) {
                this.mLayerWidth = iArr[0];
                this.mLayerHeight = iArr[1];
            } else {
                this.mLayer = 0;
                this.mLayerWidth = 0;
                this.mLayerHeight = 0;
            }
        }
        return isValid();
    }

    @Override // android.view.HardwareLayer
    void setOpaque(boolean z) {
        this.mOpaque = z;
        GLES20Canvas.nSetOpaqueLayer(this.mLayer, z);
    }

    @Override // android.view.HardwareLayer
    HardwareCanvas getCanvas() {
        return this.mCanvas;
    }

    @Override // android.view.HardwareLayer
    void end(Canvas canvas) {
        HardwareCanvas canvas2 = getCanvas();
        if (canvas2 != null) {
            canvas2.onPostDraw();
        }
        if (canvas instanceof GLES20Canvas) {
            ((GLES20Canvas) canvas).resume();
        }
    }

    @Override // android.view.HardwareLayer
    HardwareCanvas start(Canvas canvas) {
        return start(canvas, null);
    }

    @Override // android.view.HardwareLayer
    HardwareCanvas start(Canvas canvas, Rect rect) {
        if (canvas instanceof GLES20Canvas) {
            ((GLES20Canvas) canvas).interrupt();
        }
        HardwareCanvas canvas2 = getCanvas();
        canvas2.setViewport(this.mWidth, this.mHeight);
        canvas2.onPreDraw(rect);
        return canvas2;
    }

    @Override // android.view.HardwareLayer
    void redrawLater(DisplayList displayList, Rect rect) {
        GLES20Canvas.nUpdateRenderLayer(this.mLayer, this.mCanvas.getRenderer(), ((GLES20DisplayList) displayList).getNativeDisplayList(), rect.left, rect.top, rect.right, rect.bottom);
    }
}
