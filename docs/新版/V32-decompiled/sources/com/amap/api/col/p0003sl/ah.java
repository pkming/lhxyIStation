package com.amap.api.col.p0003sl;

import android.util.Pair;
import com.autonavi.amap.api.mapcore.IGLMapState;
import com.autonavi.amap.mapcore.AbstractCameraUpdateMessage;
import com.autonavi.amap.mapcore.IPoint;

/* JADX INFO: compiled from: AbstractCameraBoundsMessage.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ah extends AbstractCameraUpdateMessage {
    @Override // com.autonavi.amap.mapcore.AbstractCameraUpdateMessage
    public final void mergeCameraUpdateDelegate(AbstractCameraUpdateMessage abstractCameraUpdateMessage) {
    }

    @Override // com.autonavi.amap.mapcore.AbstractCameraUpdateMessage
    public final void runCameraUpdate(IGLMapState iGLMapState) {
        Pair<Float, IPoint> pairA = dx.a(this, this.mapConfig);
        if (pairA == null) {
            return;
        }
        iGLMapState.setMapZoomer(pairA.first.floatValue());
        iGLMapState.setMapGeoCenter(pairA.second.x, pairA.second.y);
        iGLMapState.setCameraDegree(0.0f);
        iGLMapState.setMapAngle(0.0f);
    }
}
