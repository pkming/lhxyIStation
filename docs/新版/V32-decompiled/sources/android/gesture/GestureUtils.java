package android.gesture;

import android.util.Log;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class GestureUtils {
    private static final float NONUNIFORM_SCALE = (float) Math.sqrt(2.0d);
    private static final float SCALING_THRESHOLD = 0.26f;

    private GestureUtils() {
    }

    static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.e(GestureConstants.LOG_TAG, "Could not close stream", e);
            }
        }
    }

    public static float[] spatialSampling(Gesture gesture, int i) {
        return spatialSampling(gesture, i, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:66:0x0128  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0142  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0026  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static float[] spatialSampling(android.gesture.Gesture r23, int r24, boolean r25) {
        /*
            Method dump skipped, instruction units count: 400
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.gesture.GestureUtils.spatialSampling(android.gesture.Gesture, int, boolean):float[]");
    }

    private static void plot(float f, float f2, float[] fArr, int i) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        double d = f;
        int iFloor = (int) Math.floor(d);
        int iCeil = (int) Math.ceil(d);
        double d2 = f2;
        int iFloor2 = (int) Math.floor(d2);
        int iCeil2 = (int) Math.ceil(d2);
        if (f == iFloor && f2 == iFloor2) {
            int i2 = (iCeil2 * i) + iCeil;
            if (fArr[i2] < 1.0f) {
                fArr[i2] = 1.0f;
                return;
            }
            return;
        }
        double dPow = Math.pow(r4 - f, 2.0d);
        double dPow2 = Math.pow(iFloor2 - f2, 2.0d);
        double dPow3 = Math.pow(iCeil - f, 2.0d);
        double dPow4 = Math.pow(iCeil2 - f2, 2.0d);
        float fSqrt = (float) Math.sqrt(dPow + dPow2);
        float fSqrt2 = (float) Math.sqrt(dPow2 + dPow3);
        float fSqrt3 = (float) Math.sqrt(dPow + dPow4);
        float fSqrt4 = (float) Math.sqrt(dPow3 + dPow4);
        float f3 = fSqrt + fSqrt2 + fSqrt3 + fSqrt4;
        float f4 = fSqrt / f3;
        int i3 = iFloor2 * i;
        int i4 = i3 + iFloor;
        if (f4 > fArr[i4]) {
            fArr[i4] = f4;
        }
        float f5 = fSqrt2 / f3;
        int i5 = i3 + iCeil;
        if (f5 > fArr[i5]) {
            fArr[i5] = f5;
        }
        float f6 = fSqrt3 / f3;
        int i6 = iCeil2 * i;
        int i7 = iFloor + i6;
        if (f6 > fArr[i7]) {
            fArr[i7] = f6;
        }
        float f7 = fSqrt4 / f3;
        int i8 = i6 + iCeil;
        if (f7 > fArr[i8]) {
            fArr[i8] = f7;
        }
    }

    public static float[] temporalSampling(GestureStroke gestureStroke, int i) {
        float f = gestureStroke.length / (i - 1);
        int i2 = 2;
        int i3 = i * 2;
        float[] fArr = new float[i3];
        float[] fArr2 = gestureStroke.points;
        int i4 = 0;
        float f2 = fArr2[0];
        float f3 = fArr2[1];
        fArr[0] = f2;
        fArr[1] = f3;
        int length = fArr2.length / 2;
        float f4 = Float.MIN_VALUE;
        float f5 = Float.MIN_VALUE;
        float f6 = Float.MIN_VALUE;
        float f7 = 0.0f;
        while (i4 < length) {
            if (f5 == f4) {
                i4++;
                if (i4 >= length) {
                    break;
                }
                int i5 = i4 * 2;
                float f8 = fArr2[i5];
                f6 = fArr2[i5 + 1];
                f5 = f8;
            }
            float f9 = f5 - f2;
            float f10 = f6 - f3;
            float f11 = f5;
            float fSqrt = (float) Math.sqrt((f9 * f9) + (f10 * f10));
            float f12 = f7 + fSqrt;
            if (f12 >= f) {
                float f13 = (f - f7) / fSqrt;
                f2 += f9 * f13;
                f3 += f13 * f10;
                fArr[i2] = f2;
                int i6 = i2 + 1;
                fArr[i6] = f3;
                i2 = i6 + 1;
                f5 = f11;
                f7 = 0.0f;
            } else {
                f7 = f12;
                f3 = f6;
                f2 = f11;
                f5 = Float.MIN_VALUE;
                f6 = Float.MIN_VALUE;
            }
            f4 = Float.MIN_VALUE;
        }
        while (i2 < i3) {
            fArr[i2] = f2;
            fArr[i2 + 1] = f3;
            i2 += 2;
        }
        return fArr;
    }

    static float[] computeCentroid(float[] fArr) {
        int length = fArr.length;
        float f = 0.0f;
        int i = 0;
        float f2 = 0.0f;
        while (i < length) {
            f += fArr[i];
            int i2 = i + 1;
            f2 += fArr[i2];
            i = i2 + 1;
        }
        float f3 = length;
        return new float[]{(f * 2.0f) / f3, (f2 * 2.0f) / f3};
    }

    private static float[][] computeCoVariance(float[] fArr) {
        float[][] fArr2 = (float[][]) Array.newInstance((Class<?>) float.class, 2, 2);
        fArr2[0][0] = 0.0f;
        fArr2[0][1] = 0.0f;
        fArr2[1][0] = 0.0f;
        fArr2[1][1] = 0.0f;
        int length = fArr.length;
        int i = 0;
        while (i < length) {
            float f = fArr[i];
            int i2 = i + 1;
            float f2 = fArr[i2];
            float[] fArr3 = fArr2[0];
            fArr3[0] = fArr3[0] + (f * f);
            float[] fArr4 = fArr2[0];
            fArr4[1] = fArr4[1] + (f * f2);
            fArr2[1][0] = fArr2[0][1];
            float[] fArr5 = fArr2[1];
            fArr5[1] = fArr5[1] + (f2 * f2);
            i = i2 + 1;
        }
        float[] fArr6 = fArr2[0];
        float f3 = length / 2;
        fArr6[0] = fArr6[0] / f3;
        float[] fArr7 = fArr2[0];
        fArr7[1] = fArr7[1] / f3;
        float[] fArr8 = fArr2[1];
        fArr8[0] = fArr8[0] / f3;
        float[] fArr9 = fArr2[1];
        fArr9[1] = fArr9[1] / f3;
        return fArr2;
    }

    static float computeTotalLength(float[] fArr) {
        int length = fArr.length - 4;
        float fSqrt = 0.0f;
        int i = 0;
        while (i < length) {
            int i2 = i + 2;
            float f = fArr[i2] - fArr[i];
            float f2 = fArr[i + 3] - fArr[i + 1];
            fSqrt = (float) (((double) fSqrt) + Math.sqrt((f * f) + (f2 * f2)));
            i = i2;
        }
        return fSqrt;
    }

    static float computeStraightness(float[] fArr) {
        float fComputeTotalLength = computeTotalLength(fArr);
        float f = fArr[2] - fArr[0];
        float f2 = fArr[3] - fArr[1];
        return ((float) Math.sqrt((f * f) + (f2 * f2))) / fComputeTotalLength;
    }

    static float computeStraightness(float[] fArr, float f) {
        float f2 = fArr[2] - fArr[0];
        float f3 = fArr[3] - fArr[1];
        return ((float) Math.sqrt((f2 * f2) + (f3 * f3))) / f;
    }

    static float squaredEuclideanDistance(float[] fArr, float[] fArr2) {
        int length = fArr.length;
        float f = 0.0f;
        for (int i = 0; i < length; i++) {
            float f2 = fArr[i] - fArr2[i];
            f += f2 * f2;
        }
        return f / length;
    }

    static float cosineDistance(float[] fArr, float[] fArr2) {
        int length = fArr.length;
        float f = 0.0f;
        for (int i = 0; i < length; i++) {
            f += fArr[i] * fArr2[i];
        }
        return (float) Math.acos(f);
    }

    static float minimumCosineDistance(float[] fArr, float[] fArr2, int i) {
        double dAcos;
        int length = fArr.length;
        float f = 0.0f;
        float f2 = 0.0f;
        for (int i2 = 0; i2 < length; i2 += 2) {
            int i3 = i2 + 1;
            f += (fArr[i2] * fArr2[i2]) + (fArr[i3] * fArr2[i3]);
            f2 += (fArr[i2] * fArr2[i3]) - (fArr[i3] * fArr2[i2]);
        }
        if (f == 0.0f) {
            return 1.5707964f;
        }
        double d = f2 / f;
        double dAtan = Math.atan(d);
        if (i > 2 && Math.abs(dAtan) >= 3.141592653589793d / ((double) i)) {
            dAcos = Math.acos(f);
        } else {
            double dCos = Math.cos(dAtan);
            dAcos = Math.acos((((double) f) * dCos) + (((double) f2) * d * dCos));
        }
        return (float) dAcos;
    }

    public static OrientedBoundingBox computeOrientedBoundingBox(ArrayList<GesturePoint> arrayList) {
        int size = arrayList.size();
        float[] fArr = new float[size * 2];
        for (int i = 0; i < size; i++) {
            GesturePoint gesturePoint = arrayList.get(i);
            int i2 = i * 2;
            fArr[i2] = gesturePoint.x;
            fArr[i2 + 1] = gesturePoint.y;
        }
        return computeOrientedBoundingBox(fArr, computeCentroid(fArr));
    }

    public static OrientedBoundingBox computeOrientedBoundingBox(float[] fArr) {
        int length = fArr.length;
        float[] fArr2 = new float[length];
        for (int i = 0; i < length; i++) {
            fArr2[i] = fArr[i];
        }
        return computeOrientedBoundingBox(fArr2, computeCentroid(fArr2));
    }

    private static OrientedBoundingBox computeOrientedBoundingBox(float[] fArr, float[] fArr2) {
        float fAtan2;
        translate(fArr, -fArr2[0], -fArr2[1]);
        float[] fArrComputeOrientation = computeOrientation(computeCoVariance(fArr));
        if (fArrComputeOrientation[0] == 0.0f && fArrComputeOrientation[1] == 0.0f) {
            fAtan2 = -1.5707964f;
        } else {
            fAtan2 = (float) Math.atan2(fArrComputeOrientation[1], fArrComputeOrientation[0]);
            rotate(fArr, -fAtan2);
        }
        int length = fArr.length;
        float f = Float.MIN_VALUE;
        int i = 0;
        float f2 = Float.MAX_VALUE;
        float f3 = Float.MAX_VALUE;
        float f4 = Float.MIN_VALUE;
        while (i < length) {
            if (fArr[i] < f2) {
                f2 = fArr[i];
            }
            if (fArr[i] > f) {
                f = fArr[i];
            }
            int i2 = i + 1;
            if (fArr[i2] < f3) {
                f3 = fArr[i2];
            }
            if (fArr[i2] > f4) {
                f4 = fArr[i2];
            }
            i = i2 + 1;
        }
        return new OrientedBoundingBox((float) (((double) (fAtan2 * 180.0f)) / 3.141592653589793d), fArr2[0], fArr2[1], f - f2, f4 - f3);
    }

    private static float[] computeOrientation(float[][] fArr) {
        float[] fArr2 = new float[2];
        if (fArr[0][1] == 0.0f || fArr[1][0] == 0.0f) {
            fArr2[0] = 1.0f;
            fArr2[1] = 0.0f;
        }
        float f = ((-fArr[0][0]) - fArr[1][1]) / 2.0f;
        float fSqrt = (float) Math.sqrt(Math.pow(f, 2.0d) - ((double) ((fArr[0][0] * fArr[1][1]) - (fArr[0][1] * fArr[1][0]))));
        float f2 = -f;
        float f3 = f2 + fSqrt;
        float f4 = f2 - fSqrt;
        if (f3 == f4) {
            fArr2[0] = 0.0f;
            fArr2[1] = 0.0f;
        } else {
            if (f3 <= f4) {
                f3 = f4;
            }
            fArr2[0] = 1.0f;
            fArr2[1] = (f3 - fArr[0][0]) / fArr[0][1];
        }
        return fArr2;
    }

    static float[] rotate(float[] fArr, float f) {
        double d = f;
        float fCos = (float) Math.cos(d);
        float fSin = (float) Math.sin(d);
        int length = fArr.length;
        for (int i = 0; i < length; i += 2) {
            int i2 = i + 1;
            float f2 = (fArr[i] * fCos) - (fArr[i2] * fSin);
            float f3 = (fArr[i] * fSin) + (fArr[i2] * fCos);
            fArr[i] = f2;
            fArr[i2] = f3;
        }
        return fArr;
    }

    static float[] translate(float[] fArr, float f, float f2) {
        int length = fArr.length;
        for (int i = 0; i < length; i += 2) {
            fArr[i] = fArr[i] + f;
            int i2 = i + 1;
            fArr[i2] = fArr[i2] + f2;
        }
        return fArr;
    }

    static float[] scale(float[] fArr, float f, float f2) {
        int length = fArr.length;
        for (int i = 0; i < length; i += 2) {
            fArr[i] = fArr[i] * f;
            int i2 = i + 1;
            fArr[i2] = fArr[i2] * f2;
        }
        return fArr;
    }
}
