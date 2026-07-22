package com.amap.api.col.p0003sl;

import android.graphics.Point;
import com.autonavi.amap.api.mapcore.IGLMapState;
import com.autonavi.amap.mapcore.AbstractCameraUpdateMessage;

/* JADX INFO: compiled from: AbstractCameraScrollMessage.java */
/* JADX INFO: loaded from: classes2.dex */
public final class aj extends AbstractCameraUpdateMessage {
    @Override // com.autonavi.amap.mapcore.AbstractCameraUpdateMessage
    public final void mergeCameraUpdateDelegate(AbstractCameraUpdateMessage abstractCameraUpdateMessage) {
    }

    @Override // com.autonavi.amap.mapcore.AbstractCameraUpdateMessage
    public final void runCameraUpdate(IGLMapState iGLMapState) {
        float f = this.xPixel;
        float f2 = (this.width / 2.0f) + f;
        float f3 = (this.height / 2.0f) + this.yPixel;
        a(iGLMapState, (int) f2, (int) f3, new Point());
        iGLMapState.setMapGeoCenter(r1.x, r1.y);
    }

    private static void a(IGLMapState iGLMapState, int i, int i2, Point point) {
        iGLMapState.screenToP20Point(i, i2, point);
    }
}
