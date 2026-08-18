package com.autonavi.base.ae.gmap.bean;

import com.amap.api.maps.model.Tile;
import com.amap.api.maps.model.TileProvider;

/* JADX INFO: loaded from: classes2.dex */
public interface TileSourceProvider extends TileProvider {
    void cancel(TileSourceReq tileSourceReq);

    Tile getTile(TileSourceReq tileSourceReq);
}
