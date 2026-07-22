package android.graphics;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class PixelXorXfermode extends Xfermode {
    private static native int nativeCreate(int i);

    public PixelXorXfermode(int i) {
        this.native_instance = nativeCreate(i);
    }
}
