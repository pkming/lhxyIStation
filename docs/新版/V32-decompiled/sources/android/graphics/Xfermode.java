package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class Xfermode {
    int native_instance;

    private static native void finalizer(int i);

    protected void finalize() throws Throwable {
        try {
            finalizer(this.native_instance);
        } finally {
            super.finalize();
        }
    }
}
