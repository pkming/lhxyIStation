package android.graphics;

import android.graphics.Shader;

/* JADX INFO: loaded from: classes.dex */
public class RadialGradient extends Shader {
    private static final int TYPE_COLORS_AND_POSITIONS = 1;
    private static final int TYPE_COLOR_CENTER_AND_COLOR_EDGE = 2;
    private int mColor0;
    private int mColor1;
    private int[] mColors;
    private float[] mPositions;
    private float mRadius;
    private Shader.TileMode mTileMode;
    private int mType;
    private float mX;
    private float mY;

    private static native int nativeCreate1(float f, float f2, float f3, int[] iArr, float[] fArr, int i);

    private static native int nativeCreate2(float f, float f2, float f3, int i, int i2, int i3);

    private static native int nativePostCreate1(int i, float f, float f2, float f3, int[] iArr, float[] fArr, int i2);

    private static native int nativePostCreate2(int i, float f, float f2, float f3, int i2, int i3, int i4);

    public RadialGradient(float f, float f2, float f3, int[] iArr, float[] fArr, Shader.TileMode tileMode) {
        if (f3 <= 0.0f) {
            throw new IllegalArgumentException("radius must be > 0");
        }
        if (iArr.length < 2) {
            throw new IllegalArgumentException("needs >= 2 number of colors");
        }
        if (fArr != null && iArr.length != fArr.length) {
            throw new IllegalArgumentException("color and position arrays must be of equal length");
        }
        this.mType = 1;
        this.mX = f;
        this.mY = f2;
        this.mRadius = f3;
        this.mColors = iArr;
        this.mPositions = fArr;
        this.mTileMode = tileMode;
        this.native_instance = nativeCreate1(f, f2, f3, iArr, fArr, tileMode.nativeInt);
        this.native_shader = nativePostCreate1(this.native_instance, f, f2, f3, iArr, fArr, tileMode.nativeInt);
    }

    public RadialGradient(float f, float f2, float f3, int i, int i2, Shader.TileMode tileMode) {
        if (f3 <= 0.0f) {
            throw new IllegalArgumentException("radius must be > 0");
        }
        this.mType = 2;
        this.mX = f;
        this.mY = f2;
        this.mRadius = f3;
        this.mColor0 = i;
        this.mColor1 = i2;
        this.mTileMode = tileMode;
        this.native_instance = nativeCreate2(f, f2, f3, i, i2, tileMode.nativeInt);
        this.native_shader = nativePostCreate2(this.native_instance, f, f2, f3, i, i2, tileMode.nativeInt);
    }

    @Override // android.graphics.Shader
    protected Shader copy() {
        RadialGradient radialGradient;
        int i = this.mType;
        if (i == 1) {
            float f = this.mX;
            float f2 = this.mY;
            float f3 = this.mRadius;
            int[] iArr = (int[]) this.mColors.clone();
            float[] fArr = this.mPositions;
            radialGradient = new RadialGradient(f, f2, f3, iArr, fArr != null ? (float[]) fArr.clone() : null, this.mTileMode);
        } else if (i == 2) {
            radialGradient = new RadialGradient(this.mX, this.mY, this.mRadius, this.mColor0, this.mColor1, this.mTileMode);
        } else {
            throw new IllegalArgumentException("RadialGradient should be created with either colors and positions or center color and edge color");
        }
        copyLocalMatrix(radialGradient);
        return radialGradient;
    }
}
