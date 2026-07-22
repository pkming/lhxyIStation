package com.amap.api.maps.model;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class VisibleRegionCreator implements Parcelable.Creator<VisibleRegion> {
    public static final int CONTENT_DESCRIPTION = 0;

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public VisibleRegion createFromParcel(Parcel parcel) {
        LatLng latLng;
        LatLng latLng2;
        LatLng latLng3;
        LatLng latLng4;
        LatLngBounds latLngBounds;
        int i = parcel.readInt();
        try {
            latLng = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
        } catch (BadParcelableException e) {
            e = e;
            latLng = null;
            latLng2 = null;
        }
        try {
            latLng2 = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
            try {
                latLng3 = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
                try {
                    latLng4 = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
                } catch (BadParcelableException e2) {
                    e = e2;
                    latLng4 = null;
                }
                try {
                    latLngBounds = (LatLngBounds) parcel.readParcelable(LatLngBounds.class.getClassLoader());
                } catch (BadParcelableException e3) {
                    e = e3;
                    e.printStackTrace();
                    latLngBounds = null;
                }
            } catch (BadParcelableException e4) {
                e = e4;
                latLng3 = null;
                latLng4 = latLng3;
                e.printStackTrace();
                latLngBounds = null;
                return new VisibleRegion(i, latLng, latLng2, latLng3, latLng4, latLngBounds);
            }
        } catch (BadParcelableException e5) {
            e = e5;
            latLng2 = null;
            latLng3 = latLng2;
            latLng4 = latLng3;
            e.printStackTrace();
            latLngBounds = null;
            return new VisibleRegion(i, latLng, latLng2, latLng3, latLng4, latLngBounds);
        }
        return new VisibleRegion(i, latLng, latLng2, latLng3, latLng4, latLngBounds);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public VisibleRegion[] newArray(int i) {
        return new VisibleRegion[i];
    }

    static void a(VisibleRegion visibleRegion, Parcel parcel, int i) {
        parcel.writeInt(visibleRegion.a());
        parcel.writeParcelable(visibleRegion.nearLeft, i);
        parcel.writeParcelable(visibleRegion.nearRight, i);
        parcel.writeParcelable(visibleRegion.farLeft, i);
        parcel.writeParcelable(visibleRegion.farRight, i);
        parcel.writeParcelable(visibleRegion.latLngBounds, i);
    }
}
