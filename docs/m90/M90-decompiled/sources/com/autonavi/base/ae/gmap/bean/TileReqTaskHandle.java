package com.autonavi.base.ae.gmap.bean;

import com.amap.api.maps.model.Tile;

/* JADX INFO: loaded from: classes2.dex */
public class TileReqTaskHandle {
    long nativeObj;
    int status;
    Tile tile;

    public void finish(Tile tile) {
        this.tile = tile;
    }
}
