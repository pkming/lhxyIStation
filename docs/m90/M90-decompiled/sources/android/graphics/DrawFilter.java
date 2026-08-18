package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class DrawFilter {
    int mNativeInt;

    private static native void nativeDestructor(int i);

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeInt);
        } finally {
            super.finalize();
        }
    }
}
