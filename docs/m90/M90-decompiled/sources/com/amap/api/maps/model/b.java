package com.amap.api.maps.model;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: compiled from: TileCreator.java */
/* JADX INFO: loaded from: classes2.dex */
final class b implements Parcelable.Creator<Tile> {
    b() {
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Tile createFromParcel(Parcel parcel) {
        return a(parcel);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Tile[] newArray(int i) {
        return a(i);
    }

    private static Tile a(Parcel parcel) {
        return new Tile(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.createByteArray(), parcel.readInt() == 1);
    }

    private static Tile[] a(int i) {
        return new Tile[i];
    }
}
