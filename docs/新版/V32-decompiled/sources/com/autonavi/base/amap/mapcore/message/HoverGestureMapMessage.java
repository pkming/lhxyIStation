package com.autonavi.base.amap.mapcore.message;

import com.autonavi.base.ae.gmap.GLMapState;
import com.autonavi.base.ae.gmap.maploader.Pools;

/* JADX INFO: loaded from: classes2.dex */
public class HoverGestureMapMessage extends AbstractGestureMapMessage {
    private static final Pools.SynchronizedPool<HoverGestureMapMessage> M_POOL = new Pools.SynchronizedPool<>(256);
    public float angleDelta;

    @Override // com.autonavi.base.amap.mapcore.message.AbstractGestureMapMessage, com.autonavi.base.ae.gmap.AbstractMapMessage
    public int getType() {
        return 3;
    }

    public static HoverGestureMapMessage obtain(int i, float f) {
        HoverGestureMapMessage hoverGestureMapMessageAcquire = M_POOL.acquire();
        if (hoverGestureMapMessageAcquire == null) {
            hoverGestureMapMessageAcquire = new HoverGestureMapMessage(i, f);
        } else {
            hoverGestureMapMessageAcquire.reset();
        }
        hoverGestureMapMessageAcquire.setParams(i, f);
        return hoverGestureMapMessageAcquire;
    }

    public void recycle() {
        M_POOL.release(this);
    }

    public static void destory() {
        M_POOL.destory();
    }

    private void setParams(int i, float f) {
        setState(i);
        this.angleDelta = f;
    }

    public HoverGestureMapMessage(int i, float f) {
        super(i);
        this.angleDelta = 0.0f;
        this.angleDelta = f;
    }

    @Override // com.autonavi.base.amap.mapcore.message.AbstractGestureMapMessage
    public void runCameraUpdate(GLMapState gLMapState) {
        float cameraDegree = gLMapState.getCameraDegree() + this.angleDelta;
        if (cameraDegree < 0.0f) {
            cameraDegree = 0.0f;
        } else if (cameraDegree > 80.0f) {
            cameraDegree = 80.0f;
        } else if (gLMapState.getCameraDegree() > 40.0f && cameraDegree > 40.0f && gLMapState.getCameraDegree() > cameraDegree) {
            cameraDegree = 40.0f;
        }
        gLMapState.setCameraDegree(cameraDegree);
        gLMapState.recalculate();
    }
}
