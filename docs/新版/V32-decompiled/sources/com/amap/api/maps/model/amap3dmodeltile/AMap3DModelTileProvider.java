package com.amap.api.maps.model.amap3dmodeltile;

import com.amap.api.col.p0003sl.ab;
import com.amap.api.col.p0003sl.df;
import com.amap.api.col.p0003sl.ig;
import com.amap.api.maps.model.Tile;
import com.amap.api.maps.model.TileProvider;

/* JADX INFO: loaded from: classes2.dex */
public final class AMap3DModelTileProvider implements TileProvider {
    private static final String DEFAULT_URL = "https://lbs-3dtiles-service.amap.com/basemap/tiles/staging?compose=building@1669011850923&compose=tree@1668678765481&z=%d&x=%d&y=%d";
    private int tileSize = 256;
    private String url;

    public final String getUrl() {
        return this.url;
    }

    public final void setUrl(String str) {
        this.url = str;
    }

    @Override // com.amap.api.maps.model.TileProvider
    public final Tile getTile(int i, int i2, int i3) {
        byte[] bArrA = a(i3, i, i2);
        if (bArrA == null) {
            return TileProvider.NO_TILE;
        }
        int i4 = this.tileSize;
        return new Tile(i4, i4, bArrA, false);
    }

    @Override // com.amap.api.maps.model.TileProvider
    public final int getTileWidth() {
        return this.tileSize;
    }

    @Override // com.amap.api.maps.model.TileProvider
    public final int getTileHeight() {
        return this.tileSize;
    }

    private byte[] a(int i, int i2, int i3) {
        try {
            return new AMap3DModelRequest(b(i, i2, i3)).makeHttpRequestWithInterrupted();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String b(int i, int i2, int i3) {
        String str = this.url;
        if (str == null) {
            str = DEFAULT_URL;
        }
        return String.format(str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static class AMap3DModelRequest extends df {
        private String baseQueryStr;
        private String baseUrl;

        public AMap3DModelRequest(String str) {
            this.baseUrl = "";
            this.baseQueryStr = "";
            this.isPostFlag = false;
            if (!str.contains("?")) {
                this.baseUrl = str + "?";
                return;
            }
            String[] strArrSplit = str.split("\\?");
            if (strArrSplit.length > 1) {
                this.baseUrl = strArrSplit[0] + "?";
                this.baseQueryStr = strArrSplit[1];
            }
        }

        @Override // com.amap.api.col.p0003sl.lb
        public String getURL() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.baseQueryStr);
            stringBuffer.append("&key=").append(ig.f(ab.a));
            return this.baseUrl + appendTsScode(stringBuffer.toString());
        }
    }
}
