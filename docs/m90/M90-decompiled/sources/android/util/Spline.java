package android.util;

/* JADX INFO: loaded from: classes.dex */
public final class Spline {
    private final float[] mM;
    private final float[] mX;
    private final float[] mY;

    private Spline(float[] fArr, float[] fArr2, float[] fArr3) {
        this.mX = fArr;
        this.mY = fArr2;
        this.mM = fArr3;
    }

    public static Spline createMonotoneCubicSpline(float[] fArr, float[] fArr2) {
        if (fArr == null || fArr2 == null || fArr.length != fArr2.length || fArr.length < 2) {
            throw new IllegalArgumentException("There must be at least two control points and the arrays must be of equal length.");
        }
        int length = fArr.length;
        int i = length - 1;
        float[] fArr3 = new float[i];
        float[] fArr4 = new float[length];
        int i2 = 0;
        while (i2 < i) {
            int i3 = i2 + 1;
            float f = fArr[i3] - fArr[i2];
            if (f <= 0.0f) {
                throw new IllegalArgumentException("The control points must all have strictly increasing X values.");
            }
            fArr3[i2] = (fArr2[i3] - fArr2[i2]) / f;
            i2 = i3;
        }
        fArr4[0] = fArr3[0];
        for (int i4 = 1; i4 < i; i4++) {
            fArr4[i4] = (fArr3[i4 - 1] + fArr3[i4]) * 0.5f;
        }
        fArr4[i] = fArr3[length - 2];
        for (int i5 = 0; i5 < i; i5++) {
            if (fArr3[i5] == 0.0f) {
                fArr4[i5] = 0.0f;
                fArr4[i5 + 1] = 0.0f;
            } else {
                float f2 = fArr4[i5] / fArr3[i5];
                int i6 = i5 + 1;
                float f3 = fArr4[i6] / fArr3[i5];
                if (f2 < 0.0f || f3 < 0.0f) {
                    throw new IllegalArgumentException("The control points must have monotonic Y values.");
                }
                float fHypot = FloatMath.hypot(f2, f3);
                if (fHypot > 9.0f) {
                    float f4 = 3.0f / fHypot;
                    fArr4[i5] = f2 * f4 * fArr3[i5];
                    fArr4[i6] = f4 * f3 * fArr3[i5];
                }
            }
        }
        return new Spline(fArr, fArr2, fArr4);
    }

    public float interpolate(float f) {
        int length = this.mX.length;
        if (Float.isNaN(f)) {
            return f;
        }
        float[] fArr = this.mX;
        int i = 0;
        if (f <= fArr[0]) {
            return this.mY[0];
        }
        int i2 = length - 1;
        if (f >= fArr[i2]) {
            return this.mY[i2];
        }
        while (true) {
            float[] fArr2 = this.mX;
            int i3 = i + 1;
            if (f < fArr2[i3]) {
                float f2 = fArr2[i3] - fArr2[i];
                float f3 = (f - fArr2[i]) / f2;
                float[] fArr3 = this.mY;
                float f4 = 2.0f * f3;
                float f5 = fArr3[i] * (f4 + 1.0f);
                float[] fArr4 = this.mM;
                float f6 = f5 + (fArr4[i] * f2 * f3);
                float f7 = 1.0f - f3;
                return (f6 * f7 * f7) + (((fArr3[i3] * (3.0f - f4)) + (f2 * fArr4[i3] * (f3 - 1.0f))) * f3 * f3);
            }
            if (f == fArr2[i3]) {
                return this.mY[i3];
            }
            i = i3;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int length = this.mX.length;
        sb.append("[");
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append("(").append(this.mX[i]);
            sb.append(", ").append(this.mY[i]);
            sb.append(": ").append(this.mM[i]).append(")");
        }
        sb.append("]");
        return sb.toString();
    }
}
