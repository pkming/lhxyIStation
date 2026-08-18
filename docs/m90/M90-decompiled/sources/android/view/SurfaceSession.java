package android.view;

/* JADX INFO: loaded from: classes.dex */
public final class SurfaceSession {
    private int mNativeClient = nativeCreate();

    private static native int nativeCreate();

    private static native void nativeDestroy(int i);

    private static native void nativeKill(int i);

    protected void finalize() throws Throwable {
        try {
            int i = this.mNativeClient;
            if (i != 0) {
                nativeDestroy(i);
            }
        } finally {
            super.finalize();
        }
    }

    public void kill() {
        nativeKill(this.mNativeClient);
    }
}
