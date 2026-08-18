package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class ColorMatrixColorFilter extends ColorFilter {
    private static native int nColorMatrixFilter(int i, float[] fArr);

    private static native int nativeColorMatrixFilter(float[] fArr);

    public ColorMatrixColorFilter(ColorMatrix colorMatrix) {
        float[] array = colorMatrix.getArray();
        this.native_instance = nativeColorMatrixFilter(array);
        this.nativeColorFilter = nColorMatrixFilter(this.native_instance, array);
    }

    public ColorMatrixColorFilter(float[] fArr) {
        if (fArr.length < 20) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.native_instance = nativeColorMatrixFilter(fArr);
        this.nativeColorFilter = nColorMatrixFilter(this.native_instance, fArr);
    }
}
