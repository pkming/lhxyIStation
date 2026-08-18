package android.graphics;

import android.graphics.PorterDuff;

/* JADX INFO: loaded from: classes.dex */
public class PorterDuffXfermode extends Xfermode {
    public final PorterDuff.Mode mode;

    private static native int nativeCreateXfermode(int i);

    public PorterDuffXfermode(PorterDuff.Mode mode) {
        this.mode = mode;
        this.native_instance = nativeCreateXfermode(mode.nativeInt);
    }
}
