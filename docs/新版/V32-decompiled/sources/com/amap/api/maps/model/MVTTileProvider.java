package com.amap.api.maps.model;

import com.amap.api.col.p0003sl.df;

/* JADX INFO: loaded from: classes2.dex */
public final class MVTTileProvider implements TileProvider {
    private String id;
    private String key;
    private int tileSize = 256;
    private String url;

    public MVTTileProvider(String str, String str2, String str3) {
        this.url = str;
        this.key = str2;
        this.id = str3;
    }

    public final String getUrl() {
        return this.url;
    }

    public final String getKey() {
        return this.key;
    }

    public final String getId() {
        return this.id;
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
            return new a(a(i, i2, i3, this.tileSize)).makeHttpRequestWithInterrupted();
        } catch (Exception unused) {
            return null;
        }
    }

    private String a(int i, int i2, int i3, int i4) {
        return String.format(this.url + "?z=%d&x=%d&y=%d&size=%d&key=" + this.key + "&id=" + this.id, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    private class a extends df {
        String a;

        public a(String str) {
            this.isPostFlag = false;
            this.a = str;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final String getURL() {
            return this.a;
        }
    }
}
