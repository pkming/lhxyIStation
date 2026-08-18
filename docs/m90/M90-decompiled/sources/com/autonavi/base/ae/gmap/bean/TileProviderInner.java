package com.autonavi.base.ae.gmap.bean;

import android.os.Build;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.dv;
import com.amap.api.col.p0003sl.md;
import com.amap.api.maps.interfaces.IGlOverlayLayer;
import com.amap.api.maps.model.Tile;
import com.amap.api.maps.model.TileOverlaySource;
import com.amap.api.maps.model.TileProvider;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class TileProviderInner {
    private WeakReference<IGlOverlayLayer> glOverlayLayerRef;
    private List<TileOverlaySource> mTileSource;
    private String overlayName;
    private final HashMap<String, md> reqTaskHandleHashMap = new HashMap<>();
    private final TileProvider tileProvider;

    private String createKey(int i, int i2, int i3, long j) {
        return i + " " + i2 + " " + i3 + "-" + j;
    }

    public TileProviderInner(TileProvider tileProvider) {
        this.tileProvider = tileProvider;
    }

    public void setTileSource(List<TileOverlaySource> list) {
        this.mTileSource = list;
    }

    public void getTile(final TileSourceReq tileSourceReq, final TileReqTaskHandle tileReqTaskHandle) {
        final String strCreateKey = createKey(tileSourceReq.x, tileSourceReq.y, tileSourceReq.zoom, tileReqTaskHandle.nativeObj);
        md mdVar = new md() { // from class: com.autonavi.base.ae.gmap.bean.TileProviderInner.1
            @Override // com.amap.api.col.p0003sl.md
            public void runTask() {
                try {
                    synchronized (TileProviderInner.this.reqTaskHandleHashMap) {
                        if (TileProviderInner.this.reqTaskHandleHashMap.containsKey(strCreateKey)) {
                            if (TileProviderInner.this.tileProvider != null) {
                                Tile tile = TileProvider.NO_TILE;
                                try {
                                    tile = TileProviderInner.this.tileProvider instanceof TileSourceProvider ? ((TileSourceProvider) TileProviderInner.this.tileProvider).getTile(tileSourceReq) : TileProviderInner.this.tileProvider.getTile(tileSourceReq.x, tileSourceReq.y, tileSourceReq.zoom);
                                } catch (Throwable unused) {
                                }
                                TileProviderInner.this.finishDownload(tile, tileReqTaskHandle, strCreateKey);
                            }
                        }
                    }
                } catch (Throwable th) {
                    TileProviderInner.this.finishDownload(TileProvider.NO_TILE, tileReqTaskHandle, strCreateKey);
                    th.printStackTrace();
                }
            }
        };
        synchronized (this.reqTaskHandleHashMap) {
            if (this.reqTaskHandleHashMap.containsKey(strCreateKey)) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 5) {
                this.reqTaskHandleHashMap.put(strCreateKey, mdVar);
            }
            dv.a().a(mdVar);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishDownload(final Tile tile, final TileReqTaskHandle tileReqTaskHandle, final String str) {
        IGlOverlayLayer iGlOverlayLayer = this.glOverlayLayerRef.get();
        if (iGlOverlayLayer == null) {
            return;
        }
        iGlOverlayLayer.getMap().queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.bean.TileProviderInner.2
            @Override // java.lang.Runnable
            public void run() {
                boolean z;
                synchronized (TileProviderInner.this.reqTaskHandleHashMap) {
                    if (TileProviderInner.this.reqTaskHandleHashMap.containsKey(str)) {
                        if (TileProviderInner.this.reqTaskHandleHashMap.containsKey(str)) {
                            TileProviderInner.this.reqTaskHandleHashMap.remove(str);
                            z = true;
                        } else {
                            z = false;
                        }
                        if (z) {
                            tileReqTaskHandle.finish(tile);
                            TileProviderInner.this.callNativeFunction("finishTileReqTask", new Object[]{tileReqTaskHandle});
                        }
                    }
                }
            }
        });
    }

    public void cancelTile(TileSourceReq tileSourceReq, TileReqTaskHandle tileReqTaskHandle) {
        String strCreateKey = createKey(tileSourceReq.x, tileSourceReq.y, tileSourceReq.zoom, tileReqTaskHandle.nativeObj);
        synchronized (this.reqTaskHandleHashMap) {
            if (this.reqTaskHandleHashMap.containsKey(strCreateKey)) {
                md mdVar = this.reqTaskHandleHashMap.get(strCreateKey);
                if (mdVar != null) {
                    dv.a();
                    dv.b(mdVar);
                }
                if (tileReqTaskHandle != null) {
                    tileReqTaskHandle.status = 1;
                    finishDownload(TileProvider.NO_TILE, tileReqTaskHandle, strCreateKey);
                }
                try {
                    TileProvider tileProvider = this.tileProvider;
                    if (tileProvider instanceof TileSourceProvider) {
                        ((TileSourceProvider) tileProvider).cancel(tileSourceReq);
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
    }

    public int getTileWidth() {
        TileProvider tileProvider = this.tileProvider;
        if (tileProvider != null) {
            return tileProvider.getTileWidth();
        }
        return 0;
    }

    public int getTileHeight() {
        TileProvider tileProvider = this.tileProvider;
        if (tileProvider != null) {
            return tileProvider.getTileHeight();
        }
        return 0;
    }

    public void init(IGlOverlayLayer iGlOverlayLayer, String str) {
        this.glOverlayLayerRef = new WeakReference<>(iGlOverlayLayer);
        this.overlayName = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object callNativeFunction(String str, Object[] objArr) {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.glOverlayLayerRef.get();
            if (TextUtils.isEmpty(this.overlayName) || iGlOverlayLayer == null) {
                return null;
            }
            return iGlOverlayLayer.getNativeProperties(this.overlayName, str, objArr);
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }
}
