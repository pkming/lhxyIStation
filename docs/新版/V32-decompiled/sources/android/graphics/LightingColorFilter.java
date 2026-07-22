package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class LightingColorFilter extends ColorFilter {
    private static native int nCreateLightingFilter(int i, int i2, int i3);

    private static native int native_CreateLightingFilter(int i, int i2);

    public LightingColorFilter(int i, int i2) {
        this.native_instance = native_CreateLightingFilter(i, i2);
        this.nativeColorFilter = nCreateLightingFilter(this.native_instance, i, i2);
    }
}
