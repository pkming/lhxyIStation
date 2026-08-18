package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class ColorFilter {
    public int nativeColorFilter;
    int native_instance;

    private static native void finalizer(int i, int i2);

    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            finalizer(this.native_instance, this.nativeColorFilter);
        }
    }
}
