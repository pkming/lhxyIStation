package com.amap.api.maps.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.col.p0003sl.dx;

/* JADX INFO: loaded from: classes2.dex */
public final class BitmapDescriptor implements Parcelable, Cloneable {
    public static final BitmapDescriptorCreator CREATOR = new BitmapDescriptorCreator();
    int a;
    int b;
    protected Object mBitmap;
    private String mId;

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    BitmapDescriptor(Bitmap bitmap, String str) {
        this.a = 0;
        this.b = 0;
        if (bitmap != null) {
            try {
                this.a = bitmap.getWidth();
                this.b = bitmap.getHeight();
                if (bitmap.getConfig() == null) {
                    this.mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                } else {
                    this.mBitmap = bitmap.copy(bitmap.getConfig(), true);
                }
            } catch (Throwable th) {
                dx.a(th);
                return;
            }
        }
        this.mId = str;
    }

    private BitmapDescriptor(Bitmap bitmap, int i, int i2, String str) {
        this.a = 0;
        this.b = 0;
        this.a = i;
        this.b = i2;
        this.mBitmap = bitmap;
        this.mId = str;
    }

    public final String getId() {
        return this.mId;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public final BitmapDescriptor m35clone() {
        try {
            Object obj = this.mBitmap;
            return new BitmapDescriptor(((Bitmap) obj).copy(((Bitmap) obj).getConfig(), true), this.a, this.b, this.mId);
        } catch (Throwable th) {
            th.printStackTrace();
            dx.a(th);
            return null;
        }
    }

    public final Bitmap getBitmap() {
        return (Bitmap) this.mBitmap;
    }

    public final int getWidth() {
        return this.a;
    }

    public final int getHeight() {
        return this.b;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mId);
        parcel.writeParcelable((Bitmap) this.mBitmap, i);
        parcel.writeInt(this.a);
        parcel.writeInt(this.b);
    }

    public final void recycle() {
        try {
            dx.a((Bitmap) this.mBitmap);
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    public final boolean equals(Object obj) {
        BitmapDescriptor bitmapDescriptor;
        Object obj2;
        Object obj3 = this.mBitmap;
        if (obj3 == null || ((Bitmap) obj3).isRecycled() || obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() == obj.getClass() && (obj2 = (bitmapDescriptor = (BitmapDescriptor) obj).mBitmap) != null && !((Bitmap) obj2).isRecycled() && this.a == bitmapDescriptor.getWidth() && this.b == bitmapDescriptor.getHeight()) {
            try {
                return ((Bitmap) this.mBitmap).sameAs((Bitmap) bitmapDescriptor.mBitmap);
            } catch (Throwable th) {
                dx.a(th);
            }
        }
        return false;
    }

    public final int hashCode() {
        return super.hashCode();
    }
}
