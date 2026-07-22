package android.view;

import android.graphics.Bitmap;
import android.graphics.Paint;

/* JADX INFO: loaded from: classes.dex */
abstract class GLES20Layer extends HardwareLayer {
    Finalizer mFinalizer;
    int mLayer;

    GLES20Layer() {
    }

    GLES20Layer(int i, int i2, boolean z) {
        super(i, i2, z);
    }

    public int getLayer() {
        return this.mLayer;
    }

    @Override // android.view.HardwareLayer
    void setLayerPaint(Paint paint) {
        if (paint != null) {
            GLES20Canvas.nSetLayerPaint(this.mLayer, paint.mNativePaint);
            GLES20Canvas.nSetLayerColorFilter(this.mLayer, paint.getColorFilter() != null ? paint.getColorFilter().nativeColorFilter : 0);
        }
    }

    @Override // android.view.HardwareLayer
    public boolean copyInto(Bitmap bitmap) {
        return GLES20Canvas.nCopyLayer(this.mLayer, bitmap.mNativeBitmap);
    }

    @Override // android.view.HardwareLayer
    public void destroy() {
        if (this.mDisplayList != null) {
            this.mDisplayList.reset();
        }
        Finalizer finalizer = this.mFinalizer;
        if (finalizer != null) {
            finalizer.destroy();
            this.mFinalizer = null;
        }
        this.mLayer = 0;
    }

    @Override // android.view.HardwareLayer
    void clearStorage() {
        int i = this.mLayer;
        if (i != 0) {
            GLES20Canvas.nClearLayerTexture(i);
        }
    }

    static class Finalizer {
        private int mLayerId;

        public Finalizer(int i) {
            this.mLayerId = i;
        }

        protected void finalize() throws Throwable {
            try {
                int i = this.mLayerId;
                if (i != 0) {
                    GLES20Canvas.nDestroyLayerDeferred(i);
                }
            } finally {
                super.finalize();
            }
        }

        void destroy() {
            GLES20Canvas.nDestroyLayer(this.mLayerId);
            this.mLayerId = 0;
        }
    }
}
