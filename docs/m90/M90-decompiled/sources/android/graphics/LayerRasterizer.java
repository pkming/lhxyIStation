package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class LayerRasterizer extends Rasterizer {
    private static native void nativeAddLayer(int i, int i2, float f, float f2);

    private static native int nativeConstructor();

    public LayerRasterizer() {
        this.native_instance = nativeConstructor();
    }

    public void addLayer(Paint paint, float f, float f2) {
        nativeAddLayer(this.native_instance, paint.mNativePaint, f, f2);
    }

    public void addLayer(Paint paint) {
        nativeAddLayer(this.native_instance, paint.mNativePaint, 0.0f, 0.0f);
    }
}
