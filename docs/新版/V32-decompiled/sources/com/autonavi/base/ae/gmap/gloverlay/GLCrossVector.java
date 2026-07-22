package com.autonavi.base.ae.gmap.gloverlay;

import com.autonavi.base.ae.gmap.gloverlay.GLOverlay;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;

/* JADX INFO: loaded from: classes2.dex */
public class GLCrossVector extends GLOverlay {
    private long mDiceNativeInstance;

    private static native void nativeAddVectorCar(long j, int i, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeAddVectorData(long j, int[] iArr, byte[] bArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeInitTextureCallback(long j, Object obj, boolean z);

    private static native void nativeSetArrowResId(long j, boolean z, int i);

    private static native void nativeSetBackgroundResId(long j, int i);

    private static native void nativeSetCarResId(long j, int i);

    public GLCrossVector(final int i, final IAMapDelegate iAMapDelegate, int i2) {
        super(i, iAMapDelegate, i2);
        this.mDiceNativeInstance = 0L;
        if (iAMapDelegate == null || iAMapDelegate.getGLMapEngine() == null) {
            return;
        }
        iAMapDelegate.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.GLCrossVector.1
            @Override // java.lang.Runnable
            public void run() {
                GLCrossVector.this.mNativeInstance = iAMapDelegate.getGLMapEngine().createOverlay(i, GLOverlay.EAMapOverlayTpye.AMAPOVERLAY_VECTOR.getId());
            }
        });
    }

    public int addVectorItem(AVectorCrossAttr aVectorCrossAttr, final byte[] bArr, int i) {
        if (aVectorCrossAttr == null || bArr == null || i == 0) {
            return -1;
        }
        final int[] iArr = {aVectorCrossAttr.stAreaRect.left, aVectorCrossAttr.stAreaRect.top, aVectorCrossAttr.stAreaRect.right, aVectorCrossAttr.stAreaRect.bottom, aVectorCrossAttr.stAreaColor, aVectorCrossAttr.fArrowBorderWidth, aVectorCrossAttr.stArrowBorderColor, aVectorCrossAttr.fArrowLineWidth, aVectorCrossAttr.stArrowLineColor, aVectorCrossAttr.dayMode ? 1 : 0};
        if (this.mGLMapView != null) {
            this.mGLMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.GLCrossVector.2
                @Override // java.lang.Runnable
                public void run() {
                    GLCrossVector.nativeAddVectorData(GLCrossVector.this.mNativeInstance, iArr, bArr);
                }
            });
        }
        return 0;
    }

    public void setArrowResId(boolean z, int i) {
        nativeSetArrowResId(this.mNativeInstance, z, i);
    }

    public void setCarResId(int i) {
        nativeSetCarResId(this.mNativeInstance, i);
    }

    public void setBackgroundResId(int i) {
        nativeSetBackgroundResId(this.mNativeInstance, i);
    }

    public void initTextureCallback(final CrossVectorOverlay crossVectorOverlay, final boolean z) {
        if (this.mGLMapView != null) {
            this.mGLMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.GLCrossVector.3
                @Override // java.lang.Runnable
                public void run() {
                    GLCrossVector.nativeInitTextureCallback(GLCrossVector.this.mNativeInstance, crossVectorOverlay, z);
                }
            });
        }
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.GLOverlay
    void releaseInstance() {
        long j = this.mNativeInstance;
        this.mNativeInstance = 0L;
        this.mGLMapView.getGLMapEngine().destroyOverlay(this.mEngineID, j);
        super.releaseInstance();
    }
}
