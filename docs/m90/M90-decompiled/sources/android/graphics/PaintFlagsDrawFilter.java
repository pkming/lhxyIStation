package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class PaintFlagsDrawFilter extends DrawFilter {
    public final int clearBits;
    public final int setBits;

    private static native int nativeConstructor(int i, int i2);

    public PaintFlagsDrawFilter(int i, int i2) {
        this.clearBits = i;
        this.setBits = i2;
        this.mNativeInt = nativeConstructor(i, i2);
    }
}
