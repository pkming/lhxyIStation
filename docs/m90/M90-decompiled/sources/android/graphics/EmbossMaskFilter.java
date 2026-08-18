package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class EmbossMaskFilter extends MaskFilter {
    private static native int nativeConstructor(float[] fArr, float f, float f2, float f3);

    public EmbossMaskFilter(float[] fArr, float f, float f2, float f3) {
        if (fArr.length < 3) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.native_instance = nativeConstructor(fArr, f, f2, f3);
    }
}
