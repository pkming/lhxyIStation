package android.graphics;

import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class RegionIterator {
    private final int mNativeIter;

    private static native int nativeConstructor(int i);

    private static native void nativeDestructor(int i);

    private static native boolean nativeNext(int i, Rect rect);

    public RegionIterator(Region region) {
        this.mNativeIter = nativeConstructor(region.ni());
    }

    public final boolean next(Rect rect) {
        Objects.requireNonNull(rect, "The Rect must be provided");
        return nativeNext(this.mNativeIter, rect);
    }

    protected void finalize() throws Throwable {
        nativeDestructor(this.mNativeIter);
    }
}
