package android.graphics;

import android.content.res.AssetManager;
import android.util.SparseArray;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class Typeface {
    public static final int BOLD = 1;
    public static final int BOLD_ITALIC = 3;
    public static final Typeface DEFAULT;
    public static final Typeface DEFAULT_BOLD;
    public static final int ITALIC = 2;
    public static final Typeface MONOSPACE;
    public static final int NORMAL = 0;
    public static final Typeface SANS_SERIF;
    public static final Typeface SERIF;
    static Typeface[] sDefaults;
    private static final SparseArray<SparseArray<Typeface>> sTypefaceCache = new SparseArray<>(3);
    private int mStyle;
    int native_instance;

    private static native int nativeCreate(String str, int i);

    private static native int nativeCreateFromAsset(AssetManager assetManager, String str);

    private static native int nativeCreateFromFile(String str);

    private static native int nativeCreateFromTypeface(int i, int i2);

    private static native int nativeGetStyle(int i);

    private static native void nativeUnref(int i);

    static {
        String str = (String) null;
        Typeface typefaceCreate = create(str, 0);
        DEFAULT = typefaceCreate;
        Typeface typefaceCreate2 = create(str, 1);
        DEFAULT_BOLD = typefaceCreate2;
        SANS_SERIF = create("sans-serif", 0);
        SERIF = create("serif", 0);
        MONOSPACE = create("monospace", 0);
        sDefaults = new Typeface[]{typefaceCreate, typefaceCreate2, create(str, 2), create(str, 3)};
    }

    public int getStyle() {
        return this.mStyle;
    }

    public final boolean isBold() {
        return (this.mStyle & 1) != 0;
    }

    public final boolean isItalic() {
        return (this.mStyle & 2) != 0;
    }

    public static Typeface create(String str, int i) {
        return new Typeface(nativeCreate(str, i));
    }

    public static Typeface create(Typeface typeface, int i) {
        int i2;
        Typeface typeface2;
        if (typeface == null) {
            i2 = 0;
        } else {
            if (typeface.mStyle == i) {
                return typeface;
            }
            i2 = typeface.native_instance;
        }
        SparseArray<SparseArray<Typeface>> sparseArray = sTypefaceCache;
        SparseArray<Typeface> sparseArray2 = sparseArray.get(i2);
        if (sparseArray2 != null && (typeface2 = sparseArray2.get(i)) != null) {
            return typeface2;
        }
        Typeface typeface3 = new Typeface(nativeCreateFromTypeface(i2, i));
        if (sparseArray2 == null) {
            sparseArray2 = new SparseArray<>(4);
            sparseArray.put(i2, sparseArray2);
        }
        sparseArray2.put(i, typeface3);
        return typeface3;
    }

    public static Typeface defaultFromStyle(int i) {
        return sDefaults[i];
    }

    public static Typeface createFromAsset(AssetManager assetManager, String str) {
        return new Typeface(nativeCreateFromAsset(assetManager, str));
    }

    public static Typeface createFromFile(File file) {
        return new Typeface(nativeCreateFromFile(file.getAbsolutePath()));
    }

    public static Typeface createFromFile(String str) {
        return new Typeface(nativeCreateFromFile(str));
    }

    private Typeface(int i) {
        this.mStyle = 0;
        if (i == 0) {
            throw new RuntimeException("native typeface cannot be made");
        }
        this.native_instance = i;
        this.mStyle = nativeGetStyle(i);
    }

    protected void finalize() throws Throwable {
        try {
            nativeUnref(this.native_instance);
        } finally {
            super.finalize();
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Typeface typeface = (Typeface) obj;
        return this.mStyle == typeface.mStyle && this.native_instance == typeface.native_instance;
    }

    public int hashCode() {
        return (this.native_instance * 31) + this.mStyle;
    }
}
