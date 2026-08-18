package android.graphics;

import android.graphics.Shader;

/* JADX INFO: loaded from: classes.dex */
public class LinearGradient extends Shader {
    private static final int TYPE_COLORS_AND_POSITIONS = 1;
    private static final int TYPE_COLOR_START_AND_COLOR_END = 2;
    private int mColor0;
    private int mColor1;
    private int[] mColors;
    private float[] mPositions;
    private Shader.TileMode mTileMode;
    private int mType;
    private float mX0;
    private float mX1;
    private float mY0;
    private float mY1;

    private native int nativeCreate1(float f, float f2, float f3, float f4, int[] iArr, float[] fArr, int i);

    private native int nativeCreate2(float f, float f2, float f3, float f4, int i, int i2, int i3);

    private native int nativePostCreate1(int i, float f, float f2, float f3, float f4, int[] iArr, float[] fArr, int i2);

    private native int nativePostCreate2(int i, float f, float f2, float f3, float f4, int i2, int i3, int i4);

    public LinearGradient(float f, float f2, float f3, float f4, int[] iArr, float[] fArr, Shader.TileMode tileMode) {
        if (iArr.length < 2) {
            throw new IllegalArgumentException("needs >= 2 number of colors");
        }
        if (fArr != null && iArr.length != fArr.length) {
            throw new IllegalArgumentException("color and position arrays must be of equal length");
        }
        this.mType = 1;
        this.mX0 = f;
        this.mY0 = f2;
        this.mX1 = f3;
        this.mY1 = f4;
        this.mColors = iArr;
        this.mPositions = fArr;
        this.mTileMode = tileMode;
        this.native_instance = nativeCreate1(f, f2, f3, f4, iArr, fArr, tileMode.nativeInt);
        this.native_shader = nativePostCreate1(this.native_instance, f, f2, f3, f4, iArr, fArr, tileMode.nativeInt);
    }

    public LinearGradient(float f, float f2, float f3, float f4, int i, int i2, Shader.TileMode tileMode) {
        this.mType = 2;
        this.mX0 = f;
        this.mY0 = f2;
        this.mX1 = f3;
        this.mY1 = f4;
        this.mColor0 = i;
        this.mColor1 = i2;
        this.mTileMode = tileMode;
        this.native_instance = nativeCreate2(f, f2, f3, f4, i, i2, tileMode.nativeInt);
        this.native_shader = nativePostCreate2(this.native_instance, f, f2, f3, f4, i, i2, tileMode.nativeInt);
    }

    @Override // android.graphics.Shader
    protected Shader copy() {
        LinearGradient linearGradient;
        int i = this.mType;
        if (i == 1) {
            float f = this.mX0;
            float f2 = this.mY0;
            float f3 = this.mX1;
            float f4 = this.mY1;
            int[] iArr = (int[]) this.mColors.clone();
            float[] fArr = this.mPositions;
            linearGradient = new LinearGradient(f, f2, f3, f4, iArr, fArr != null ? (float[]) fArr.clone() : null, this.mTileMode);
        } else if (i == 2) {
            linearGradient = new LinearGradient(this.mX0, this.mY0, this.mX1, this.mY1, this.mColor0, this.mColor1, this.mTileMode);
        } else {
            throw new IllegalArgumentException("LinearGradient should be created with either colors and positions or start color and end color");
        }
        copyLocalMatrix(linearGradient);
        return linearGradient;
    }
}
