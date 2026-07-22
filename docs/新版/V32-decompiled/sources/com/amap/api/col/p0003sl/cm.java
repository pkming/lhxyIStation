package com.amap.api.col.p0003sl;

import android.mtp.MtpConstants;
import android.os.RemoteException;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlayOptions;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;

/* JADX INFO: compiled from: NativeBaseTileOverlay.java */
/* JADX INFO: loaded from: classes2.dex */
public final class cm {
    private final IAMapDelegate a;
    private TileOverlay b;
    private TileOverlay c;
    private boolean d = false;
    private boolean e = false;

    public cm(IAMapDelegate iAMapDelegate) {
        this.a = iAMapDelegate;
    }

    private void b() {
        if (this.b == null) {
            TileOverlayOptions tileOverlayOptionsTileProvider = new TileOverlayOptions().tileProvider(new de(this.a.getMapConfig()));
            tileOverlayOptionsTileProvider.memCacheSize(10485760);
            tileOverlayOptionsTileProvider.diskCacheSize(MtpConstants.DEVICE_PROPERTY_UNDEFINED);
            tileOverlayOptionsTileProvider.visible(this.d);
            try {
                this.b = this.a.addTileOverlay(tileOverlayOptionsTileProvider);
                this.c = this.a.addTileOverlay(tileOverlayOptionsTileProvider);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public final void a() {
        c();
        d();
    }

    private void c() {
        boolean zE = e();
        if (zE) {
            b();
        }
        if (this.d != zE) {
            this.d = zE;
            TileOverlay tileOverlay = this.b;
            if (tileOverlay != null) {
                tileOverlay.setVisible(zE);
            }
        }
    }

    private void d() {
        boolean zF = f();
        if (zF) {
            b();
        }
        if (this.e != zF) {
            this.e = zF;
            TileOverlay tileOverlay = this.c;
            if (tileOverlay != null) {
                tileOverlay.setVisible(zF);
            }
        }
    }

    private boolean e() {
        IAMapDelegate iAMapDelegate = this.a;
        if (iAMapDelegate == null) {
            return false;
        }
        return iAMapDelegate.getMapConfig().getMapLanguage().equals("en");
    }

    private static boolean f() {
        return MapsInitializer.isLoadWorldGridMap();
    }
}
