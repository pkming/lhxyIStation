package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class Rasterizer {
    int native_instance;

    private static native void finalizer(int i);

    protected void finalize() throws Throwable {
        finalizer(this.native_instance);
    }
}
