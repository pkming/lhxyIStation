package com.amap.api.maps.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.maps.model.PolylineOptions;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class PolylineOptionsCreator implements Parcelable.Creator<PolylineOptions> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public PolylineOptions createFromParcel(Parcel parcel) {
        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList arrayList = new ArrayList();
        parcel.readTypedList(arrayList, LatLng.CREATOR);
        float f = parcel.readFloat();
        int i = parcel.readInt();
        int i2 = parcel.readInt();
        float f2 = parcel.readFloat();
        float f3 = parcel.readFloat();
        polylineOptions.a = parcel.readString();
        polylineOptions.lineCapType(PolylineOptions.LineCapType.valueOf(parcel.readInt()));
        polylineOptions.lineJoinType(PolylineOptions.LineJoinType.valueOf(parcel.readInt()));
        boolean[] zArr = new boolean[5];
        parcel.readBooleanArray(zArr);
        BitmapDescriptor bitmapDescriptor = (BitmapDescriptor) parcel.readParcelable(BitmapDescriptor.class.getClassLoader());
        polylineOptions.addAll(arrayList);
        polylineOptions.width(f);
        polylineOptions.color(i);
        polylineOptions.setDottedLineType(i2);
        polylineOptions.zIndex(f2);
        polylineOptions.transparency(f3);
        polylineOptions.visible(zArr[0]);
        polylineOptions.setDottedLine(zArr[1]);
        polylineOptions.geodesic(zArr[2]);
        polylineOptions.useGradient(zArr[3]);
        polylineOptions.aboveMaskLayer(zArr[4]);
        polylineOptions.setCustomTexture(bitmapDescriptor);
        polylineOptions.setCustomTextureList(parcel.readArrayList(BitmapDescriptor.class.getClassLoader()));
        polylineOptions.setCustomTextureIndex(parcel.readArrayList(Integer.class.getClassLoader()));
        polylineOptions.colorValues(parcel.readArrayList(Integer.class.getClassLoader()));
        polylineOptions.setShownRatio(parcel.readFloat());
        return polylineOptions;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public PolylineOptions[] newArray(int i) {
        return new PolylineOptions[i];
    }
}
