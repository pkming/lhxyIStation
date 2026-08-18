package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class PathEffect {
    int native_instance;

    private static native void nativeDestructor(int i);

    protected void finalize() throws Throwable {
        nativeDestructor(this.native_instance);
    }
}
