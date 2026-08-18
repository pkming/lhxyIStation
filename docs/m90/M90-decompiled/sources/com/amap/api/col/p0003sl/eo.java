package com.amap.api.col.p0003sl;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.maps.model.BaseOptions;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: compiled from: ContourLineOptions.java */
/* JADX INFO: loaded from: classes2.dex */
public final class eo extends BaseOptions implements Parcelable {
    public static final Parcelable.Creator<eo> CREATOR = new Parcelable.Creator<eo>() { // from class: com.amap.api.col.3sl.eo.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ eo createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ eo[] newArray(int i) {
            return a(i);
        }

        private static eo a(Parcel parcel) {
            return new eo(parcel);
        }

        private static eo[] a(int i) {
            return new eo[i];
        }
    };
    private float a;
    private float b;
    private float c;
    private float d;
    private float e;
    private boolean f;
    private int g;
    private double h;
    private List<en> i;

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public eo() {
        this.a = 3.0f;
        this.b = 20.0f;
        this.c = Float.MIN_VALUE;
        this.d = Float.MAX_VALUE;
        this.e = 200.0f;
        this.f = true;
        this.g = Color.LTGRAY;
        this.h = 3.0d;
        this.i = new ArrayList();
        this.type = "ContourLineOptions";
    }

    protected eo(Parcel parcel) {
        this.a = 3.0f;
        this.b = 20.0f;
        this.c = Float.MIN_VALUE;
        this.d = Float.MAX_VALUE;
        this.e = 200.0f;
        this.f = true;
        this.g = Color.LTGRAY;
        this.h = 3.0d;
        this.i = new ArrayList();
        this.a = parcel.readFloat();
        this.b = parcel.readFloat();
        this.c = parcel.readFloat();
        this.d = parcel.readFloat();
        this.e = parcel.readFloat();
        boolean[] zArr = new boolean[1];
        parcel.readBooleanArray(zArr);
        this.f = zArr[0];
        this.g = parcel.readInt();
        this.h = parcel.readDouble();
        this.i = parcel.readArrayList(en.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.a);
        parcel.writeFloat(this.b);
        parcel.writeFloat(this.c);
        parcel.writeFloat(this.d);
        parcel.writeFloat(this.e);
        parcel.writeBooleanArray(new boolean[]{this.f});
        parcel.writeInt(this.g);
        parcel.writeDouble(this.h);
        parcel.writeList(this.i);
    }
}
