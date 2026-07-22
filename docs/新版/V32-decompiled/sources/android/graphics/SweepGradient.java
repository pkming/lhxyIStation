package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class SweepGradient extends Shader {
    private static final int TYPE_COLORS_AND_POSITIONS = 1;
    private static final int TYPE_COLOR_START_AND_COLOR_END = 2;
    private int mColor0;
    private int mColor1;
    private int[] mColors;
    private float mCx;
    private float mCy;
    private float[] mPositions;
    private int mType;

    private static native int nativeCreate1(float f, float f2, int[] iArr, float[] fArr);

    private static native int nativeCreate2(float f, float f2, int i, int i2);

    private static native int nativePostCreate1(int i, float f, float f2, int[] iArr, float[] fArr);

    private static native int nativePostCreate2(int i, float f, float f2, int i2, int i3);

    public SweepGradient(float f, float f2, int[] iArr, float[] fArr) {
        if (iArr.length < 2) {
            throw new IllegalArgumentException("needs >= 2 number of colors");
        }
        if (fArr != null && iArr.length != fArr.length) {
            throw new IllegalArgumentException("color and position arrays must be of equal length");
        }
        this.mType = 1;
        this.mCx = f;
        this.mCy = f2;
        this.mColors = iArr;
        this.mPositions = fArr;
        this.native_instance = nativeCreate1(f, f2, iArr, fArr);
        this.native_shader = nativePostCreate1(this.native_instance, f, f2, iArr, fArr);
    }

    public SweepGradient(float f, float f2, int i, int i2) {
        this.mType = 2;
        this.mCx = f;
        this.mCy = f2;
        this.mColor0 = i;
        this.mColor1 = i2;
        this.native_instance = nativeCreate2(f, f2, i, i2);
        this.native_shader = nativePostCreate2(this.native_instance, f, f2, i, i2);
    }

    @Override // android.graphics.Shader
    protected Shader copy() {
        SweepGradient sweepGradient;
        int i = this.mType;
        if (i == 1) {
            float f = this.mCx;
            float f2 = this.mCy;
            int[] iArr = (int[]) this.mColors.clone();
            float[] fArr = this.mPositions;
            sweepGradient = new SweepGradient(f, f2, iArr, fArr != null ? (float[]) fArr.clone() : null);
        } else if (i == 2) {
            sweepGradient = new SweepGradient(this.mCx, this.mCy, this.mColor0, this.mColor1);
        } else {
            throw new IllegalArgumentException("SweepGradient should be created with either colors and positions or start color and end color");
        }
        copyLocalMatrix(sweepGradient);
        return sweepGradient;
    }
}
