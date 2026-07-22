package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pools;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class Region implements Parcelable {
    private static final int MAX_POOL_SIZE = 10;
    public final int mNativeRegion;
    private static final Pools.SynchronizedPool<Region> sPool = new Pools.SynchronizedPool<>(10);
    public static final Parcelable.Creator<Region> CREATOR = new Parcelable.Creator<Region>() { // from class: android.graphics.Region.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Region createFromParcel(Parcel parcel) {
            int iNativeCreateFromParcel = Region.nativeCreateFromParcel(parcel);
            if (iNativeCreateFromParcel == 0) {
                throw new RuntimeException();
            }
            return new Region(iNativeCreateFromParcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Region[] newArray(int i) {
            return new Region[i];
        }
    };

    private static native int nativeConstructor();

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeCreateFromParcel(Parcel parcel);

    private static native void nativeDestructor(int i);

    private static native boolean nativeEquals(int i, int i2);

    private static native boolean nativeGetBoundaryPath(int i, int i2);

    private static native boolean nativeGetBounds(int i, Rect rect);

    private static native boolean nativeOp(int i, int i2, int i3, int i4);

    private static native boolean nativeOp(int i, int i2, int i3, int i4, int i5, int i6);

    private static native boolean nativeOp(int i, Rect rect, int i2, int i3);

    private static native boolean nativeSetPath(int i, int i2, int i3);

    private static native boolean nativeSetRect(int i, int i2, int i3, int i4, int i5);

    private static native void nativeSetRegion(int i, int i2);

    private static native String nativeToString(int i);

    private static native boolean nativeWriteToParcel(int i, Parcel parcel);

    public native boolean contains(int i, int i2);

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public native boolean isComplex();

    public native boolean isEmpty();

    public native boolean isRect();

    public native boolean quickContains(int i, int i2, int i3, int i4);

    public native boolean quickReject(int i, int i2, int i3, int i4);

    public native boolean quickReject(Region region);

    public native void scale(float f, Region region);

    public native void translate(int i, int i2, Region region);

    public enum Op {
        DIFFERENCE(0),
        INTERSECT(1),
        UNION(2),
        XOR(3),
        REVERSE_DIFFERENCE(4),
        REPLACE(5);

        public final int nativeInt;

        Op(int i) {
            this.nativeInt = i;
        }
    }

    public Region() {
        this(nativeConstructor());
    }

    public Region(Region region) {
        this(nativeConstructor());
        nativeSetRegion(this.mNativeRegion, region.mNativeRegion);
    }

    public Region(Rect rect) {
        int iNativeConstructor = nativeConstructor();
        this.mNativeRegion = iNativeConstructor;
        nativeSetRect(iNativeConstructor, rect.left, rect.top, rect.right, rect.bottom);
    }

    public Region(int i, int i2, int i3, int i4) {
        int iNativeConstructor = nativeConstructor();
        this.mNativeRegion = iNativeConstructor;
        nativeSetRect(iNativeConstructor, i, i2, i3, i4);
    }

    public void setEmpty() {
        nativeSetRect(this.mNativeRegion, 0, 0, 0, 0);
    }

    public boolean set(Region region) {
        nativeSetRegion(this.mNativeRegion, region.mNativeRegion);
        return true;
    }

    public boolean set(Rect rect) {
        return nativeSetRect(this.mNativeRegion, rect.left, rect.top, rect.right, rect.bottom);
    }

    public boolean set(int i, int i2, int i3, int i4) {
        return nativeSetRect(this.mNativeRegion, i, i2, i3, i4);
    }

    public boolean setPath(Path path, Region region) {
        return nativeSetPath(this.mNativeRegion, path.ni(), region.mNativeRegion);
    }

    public Rect getBounds() {
        Rect rect = new Rect();
        nativeGetBounds(this.mNativeRegion, rect);
        return rect;
    }

    public boolean getBounds(Rect rect) {
        Objects.requireNonNull(rect);
        return nativeGetBounds(this.mNativeRegion, rect);
    }

    public Path getBoundaryPath() {
        Path path = new Path();
        nativeGetBoundaryPath(this.mNativeRegion, path.ni());
        return path;
    }

    public boolean getBoundaryPath(Path path) {
        return nativeGetBoundaryPath(this.mNativeRegion, path.ni());
    }

    public boolean quickContains(Rect rect) {
        return quickContains(rect.left, rect.top, rect.right, rect.bottom);
    }

    public boolean quickReject(Rect rect) {
        return quickReject(rect.left, rect.top, rect.right, rect.bottom);
    }

    public void translate(int i, int i2) {
        translate(i, i2, null);
    }

    public void scale(float f) {
        scale(f, null);
    }

    public final boolean union(Rect rect) {
        return op(rect, Op.UNION);
    }

    public boolean op(Rect rect, Op op) {
        return nativeOp(this.mNativeRegion, rect.left, rect.top, rect.right, rect.bottom, op.nativeInt);
    }

    public boolean op(int i, int i2, int i3, int i4, Op op) {
        return nativeOp(this.mNativeRegion, i, i2, i3, i4, op.nativeInt);
    }

    public boolean op(Region region, Op op) {
        return op(this, region, op);
    }

    public boolean op(Rect rect, Region region, Op op) {
        return nativeOp(this.mNativeRegion, rect, region.mNativeRegion, op.nativeInt);
    }

    public boolean op(Region region, Region region2, Op op) {
        return nativeOp(this.mNativeRegion, region.mNativeRegion, region2.mNativeRegion, op.nativeInt);
    }

    public String toString() {
        return nativeToString(this.mNativeRegion);
    }

    public static Region obtain() {
        Region regionAcquire = sPool.acquire();
        return regionAcquire != null ? regionAcquire : new Region();
    }

    public static Region obtain(Region region) {
        Region regionObtain = obtain();
        regionObtain.set(region);
        return regionObtain;
    }

    public void recycle() {
        setEmpty();
        sPool.release(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (!nativeWriteToParcel(this.mNativeRegion, parcel)) {
            throw new RuntimeException();
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Region)) {
            return false;
        }
        return nativeEquals(this.mNativeRegion, ((Region) obj).mNativeRegion);
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeRegion);
        } finally {
            super.finalize();
        }
    }

    Region(int i) {
        if (i == 0) {
            throw new RuntimeException();
        }
        this.mNativeRegion = i;
    }

    private Region(int i, int i2) {
        this(i);
    }

    final int ni() {
        return this.mNativeRegion;
    }
}
