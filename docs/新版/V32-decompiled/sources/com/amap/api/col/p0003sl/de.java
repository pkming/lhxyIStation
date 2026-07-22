package com.amap.api.col.p0003sl;

import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.Tile;
import com.amap.api.maps.model.TileProvider;
import com.autonavi.base.amap.mapcore.MapConfig;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

/* JADX INFO: compiled from: BaseTileProvider.java */
/* JADX INFO: loaded from: classes2.dex */
public final class de implements TileProvider {
    private MapConfig c;
    private final int a = 256;
    private final int b = 256;
    private final boolean d = false;

    public de(MapConfig mapConfig) {
        this.c = mapConfig;
    }

    @Override // com.amap.api.maps.model.TileProvider
    public final Tile getTile(int i, int i2, int i3) {
        try {
            if (!this.d) {
                if (this.c.getMapLanguage().equals("zh_cn")) {
                    if (!MapsInitializer.isLoadWorldGridMap()) {
                        return NO_TILE;
                    }
                    if (i3 < 6 || dq.a(i, i2, i3)) {
                        return NO_TILE;
                    }
                } else if (!MapsInitializer.isLoadWorldGridMap() && i3 >= 6 && !dq.a(i, i2, i3)) {
                    return NO_TILE;
                }
            }
            MapConfig mapConfig = this.c;
            byte[] bArrA = a(i, i2, i3, mapConfig != null ? mapConfig.getMapLanguage() : "zh_cn");
            if (bArrA == null) {
                return NO_TILE;
            }
            return Tile.obtain(this.a, this.b, bArrA);
        } catch (IOException unused) {
            return NO_TILE;
        }
    }

    private byte[] a(int i, int i2, int i3, String str) throws IOException {
        try {
            return new a(i, i2, i3, str).makeHttpRequestWithInterrupted();
        } catch (Throwable unused) {
            return null;
        }
    }

    @Override // com.amap.api.maps.model.TileProvider
    public final int getTileWidth() {
        return this.a;
    }

    @Override // com.amap.api.maps.model.TileProvider
    public final int getTileHeight() {
        return this.b;
    }

    /* JADX INFO: compiled from: BaseTileProvider.java */
    private class a extends df {
        Random a = new Random();
        private int c;
        private int d;
        private int e;
        private String f;
        private String g;

        public a(int i, int i2, int i3, String str) {
            this.g = "";
            this.c = i;
            this.d = i2;
            this.e = i3;
            this.f = str;
            this.g = c();
        }

        private String c() {
            if (dq.a(this.c, this.d, this.e) || this.e < 6) {
                return String.format(Locale.US, "http://wprd0%d.is.autonavi.com/appmaptile?", Integer.valueOf((this.a.nextInt(100000) % 4) + 1));
            }
            if (MapsInitializer.isLoadWorldGridMap()) {
                return "http://restsdk.amap.com/v4/gridmap?";
            }
            return null;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final String getURL() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("key=").append(ig.f(ab.a));
            stringBuffer.append("&channel=amapapi");
            if (dq.a(this.c, this.d, this.e) || this.e < 6) {
                stringBuffer.append("&z=").append(this.e).append("&x=").append(this.c).append("&y=").append(this.d).append("&lang=en&size=1&scale=1&style=7");
            } else if (MapsInitializer.isLoadWorldGridMap()) {
                stringBuffer.append("&x=").append(this.c);
                stringBuffer.append("&y=").append(this.d);
                stringBuffer.append("&z=").append(this.e);
                stringBuffer.append("&ds=0");
                stringBuffer.append("&dpitype=webrd");
                stringBuffer.append("&lang=").append(this.f);
                stringBuffer.append("&scale=2");
            }
            return this.g + appendTsScode(stringBuffer.toString());
        }
    }
}
