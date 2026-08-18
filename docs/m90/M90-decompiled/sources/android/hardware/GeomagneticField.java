package android.hardware;

import java.util.GregorianCalendar;

/* JADX INFO: loaded from: classes.dex */
public class GeomagneticField {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final float EARTH_REFERENCE_RADIUS_KM = 6371.2f;
    private static final float EARTH_SEMI_MAJOR_AXIS_KM = 6378.137f;
    private static final float EARTH_SEMI_MINOR_AXIS_KM = 6356.7524f;
    private static final float[][] G_COEFF;
    private static final float[][] SCHMIDT_QUASI_NORM_FACTORS;
    private float mGcLatitudeRad;
    private float mGcLongitudeRad;
    private float mGcRadiusKm;
    private float mX;
    private float mY;
    private float mZ;
    private static final float[][] H_COEFF = {new float[]{0.0f}, new float[]{0.0f, 4944.4f}, new float[]{0.0f, -2707.7f, -576.1f}, new float[]{0.0f, -160.2f, 251.9f, -536.6f}, new float[]{0.0f, 286.4f, -211.2f, 164.3f, -309.1f}, new float[]{0.0f, 44.6f, 188.9f, -118.2f, 0.0f, 100.9f}, new float[]{0.0f, -20.8f, 44.1f, 61.5f, -66.3f, 3.1f, 55.0f}, new float[]{0.0f, -57.9f, -21.1f, 6.5f, 24.9f, 7.0f, -27.7f, -3.3f}, new float[]{0.0f, 11.0f, -20.0f, 11.9f, -17.4f, 16.7f, 7.0f, -10.8f, 1.7f}, new float[]{0.0f, -20.5f, 11.5f, 12.8f, -7.2f, -7.4f, 8.0f, 2.1f, -6.1f, 7.0f}, new float[]{0.0f, 2.8f, -0.1f, 4.7f, 4.4f, -7.2f, -1.0f, -3.9f, -2.0f, -2.0f, -8.3f}, new float[]{0.0f, 0.2f, 1.7f, -0.6f, -1.8f, 0.9f, -0.4f, -2.5f, -1.3f, -2.1f, -1.9f, -1.8f}, new float[]{0.0f, -0.9f, 0.3f, 2.1f, -2.5f, 0.5f, 0.6f, 0.0f, 0.1f, 0.3f, -0.9f, -0.2f, 0.9f}};
    private static final float[][] DELTA_G = {new float[]{0.0f}, new float[]{11.6f, 16.5f}, new float[]{-12.1f, -4.4f, 1.9f}, new float[]{0.4f, -4.1f, -2.9f, -7.7f}, new float[]{-1.8f, 2.3f, -8.7f, 4.6f, -2.1f}, new float[]{-1.0f, 0.6f, -1.8f, -1.0f, 0.9f, 1.0f}, new float[]{-0.2f, -0.2f, -0.1f, 2.0f, -1.7f, -0.3f, 1.7f}, new float[]{0.1f, -0.1f, -0.6f, 1.3f, 0.4f, 0.3f, -0.7f, 0.6f}, new float[]{-0.1f, 0.1f, -0.6f, 0.2f, -0.2f, 0.3f, 0.3f, -0.6f, 0.2f}, new float[]{0.0f, -0.1f, 0.0f, 0.3f, -0.4f, -0.3f, 0.1f, -0.1f, -0.4f, -0.2f}, new float[]{0.0f, 0.0f, -0.1f, 0.2f, 0.0f, -0.1f, -0.2f, 0.0f, -0.1f, -0.2f, -0.2f}, new float[]{0.0f, 0.0f, 0.0f, 0.1f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1f, 0.0f}, new float[]{0.0f, 0.0f, 0.1f, 0.1f, -0.1f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1f, 0.1f}};
    private static final float[][] DELTA_H = {new float[]{0.0f}, new float[]{0.0f, -25.9f}, new float[]{0.0f, -22.5f, -11.8f}, new float[]{0.0f, 7.3f, -3.9f, -2.6f}, new float[]{0.0f, 1.1f, 2.7f, 3.9f, -0.8f}, new float[]{0.0f, 0.4f, 1.8f, 1.2f, 4.0f, -0.6f}, new float[]{0.0f, -0.2f, -2.1f, -0.4f, -0.6f, 0.5f, 0.9f}, new float[]{0.0f, 0.7f, 0.3f, -0.1f, -0.1f, -0.8f, -0.3f, 0.3f}, new float[]{0.0f, -0.1f, 0.2f, 0.4f, 0.4f, 0.1f, -0.1f, 0.4f, 0.3f}, new float[]{0.0f, 0.0f, -0.2f, 0.0f, -0.1f, 0.1f, 0.0f, -0.2f, 0.3f, 0.2f}, new float[]{0.0f, 0.1f, -0.1f, 0.0f, -0.1f, -0.1f, 0.0f, -0.1f, -0.2f, 0.0f, -0.1f}, new float[]{0.0f, 0.0f, 0.1f, 0.0f, 0.1f, 0.0f, 0.1f, 0.0f, -0.1f, -0.1f, 0.0f, -0.1f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}};
    private static final long BASE_TIME = new GregorianCalendar(2010, 1, 1).getTimeInMillis();

    static {
        float[][] fArr = {new float[]{0.0f}, new float[]{-29496.6f, -1586.3f}, new float[]{-2396.6f, 3026.1f, 1668.6f}, new float[]{1340.1f, -2326.2f, 1231.9f, 634.0f}, new float[]{912.6f, 808.9f, 166.7f, -357.1f, 89.4f}, new float[]{-230.9f, 357.2f, 200.3f, -141.1f, -163.0f, -7.8f}, new float[]{72.8f, 68.6f, 76.0f, -141.4f, -22.8f, 13.2f, -77.9f}, new float[]{80.5f, -75.1f, -4.7f, 45.3f, 13.9f, 10.4f, 1.7f, 4.9f}, new float[]{24.4f, 8.1f, -14.5f, -5.6f, -19.3f, 11.5f, 10.9f, -14.1f, -3.7f}, new float[]{5.4f, 9.4f, 3.4f, -5.2f, 3.1f, -12.4f, -0.7f, 8.4f, -8.5f, -10.1f}, new float[]{-2.0f, -6.3f, 0.9f, -1.1f, -0.2f, 2.5f, -0.3f, 2.2f, 3.1f, -1.0f, -2.8f}, new float[]{3.0f, -1.5f, -2.1f, 1.7f, -0.5f, 0.5f, -0.8f, 0.4f, 1.8f, 0.1f, 0.7f, 3.8f}, new float[]{-2.2f, -0.2f, 0.3f, 1.0f, -0.6f, 0.9f, -0.1f, 0.5f, -0.4f, -0.4f, 0.2f, -0.8f, 0.0f}};
        G_COEFF = fArr;
        SCHMIDT_QUASI_NORM_FACTORS = computeSchmidtQuasiNormFactors(fArr.length);
    }

    public GeomagneticField(float f, float f2, float f3, long j) {
        int length = G_COEFF.length;
        float fMin = Math.min(89.99999f, Math.max(-89.99999f, f));
        computeGeocentricCoordinates(fMin, f2, f3);
        LegendreTable legendreTable = new LegendreTable(length - 1, (float) (1.5707963267948966d - ((double) this.mGcLatitudeRad)));
        int i = length + 2;
        float[] fArr = new float[i];
        int i2 = 0;
        fArr[0] = 1.0f;
        int i3 = 1;
        fArr[1] = EARTH_REFERENCE_RADIUS_KM / this.mGcRadiusKm;
        for (int i4 = 2; i4 < i; i4++) {
            fArr[i4] = fArr[i4 - 1] * fArr[1];
        }
        float[] fArr2 = new float[length];
        float[] fArr3 = new float[length];
        float f4 = 0.0f;
        fArr2[0] = 0.0f;
        fArr3[0] = 1.0f;
        fArr2[1] = (float) Math.sin(this.mGcLongitudeRad);
        fArr3[1] = (float) Math.cos(this.mGcLongitudeRad);
        for (int i5 = 2; i5 < length; i5++) {
            int i6 = i5 >> 1;
            int i7 = i5 - i6;
            fArr2[i5] = (fArr2[i7] * fArr3[i6]) + (fArr3[i7] * fArr2[i6]);
            fArr3[i5] = (fArr3[i7] * fArr3[i6]) - (fArr2[i7] * fArr2[i6]);
        }
        float fCos = 1.0f / ((float) Math.cos(this.mGcLatitudeRad));
        float f5 = (j - BASE_TIME) / 3.1536001E10f;
        float f6 = 0.0f;
        float f7 = 0.0f;
        while (i3 < length) {
            int i8 = i2;
            while (i8 <= i3) {
                float f8 = G_COEFF[i3][i8] + (DELTA_G[i3][i8] * f5);
                float f9 = H_COEFF[i3][i8] + (DELTA_H[i3][i8] * f5);
                int i9 = i3 + 2;
                float f10 = fArr[i9] * ((fArr3[i8] * f8) + (fArr2[i8] * f9)) * legendreTable.mPDeriv[i3][i8];
                float[][] fArr4 = SCHMIDT_QUASI_NORM_FACTORS;
                f4 += f10 * fArr4[i3][i8];
                f7 += fArr[i9] * i8 * ((fArr2[i8] * f8) - (fArr3[i8] * f9)) * legendreTable.mP[i3][i8] * fArr4[i3][i8] * fCos;
                f6 -= ((((i3 + 1) * fArr[i9]) * ((f8 * fArr3[i8]) + (f9 * fArr2[i8]))) * legendreTable.mP[i3][i8]) * fArr4[i3][i8];
                i8++;
                length = length;
            }
            i3++;
            i2 = 0;
        }
        double radians = Math.toRadians(fMin) - ((double) this.mGcLatitudeRad);
        double d = f6;
        this.mX = (float) ((((double) f4) * Math.cos(radians)) + (Math.sin(radians) * d));
        this.mY = f7;
        this.mZ = (float) ((((double) (-f4)) * Math.sin(radians)) + (d * Math.cos(radians)));
    }

    public float getX() {
        return this.mX;
    }

    public float getY() {
        return this.mY;
    }

    public float getZ() {
        return this.mZ;
    }

    public float getDeclination() {
        return (float) Math.toDegrees(Math.atan2(this.mY, this.mX));
    }

    public float getInclination() {
        return (float) Math.toDegrees(Math.atan2(this.mZ, getHorizontalStrength()));
    }

    public float getHorizontalStrength() {
        float f = this.mX;
        float f2 = this.mY;
        return (float) Math.sqrt((f * f) + (f2 * f2));
    }

    public float getFieldStrength() {
        float f = this.mX;
        float f2 = this.mY;
        float f3 = (f * f) + (f2 * f2);
        float f4 = this.mZ;
        return (float) Math.sqrt(f3 + (f4 * f4));
    }

    private void computeGeocentricCoordinates(float f, float f2, float f3) {
        double radians = Math.toRadians(f);
        float fCos = (float) Math.cos(radians);
        float fSin = (float) Math.sin(radians);
        float fSqrt = ((float) Math.sqrt((fCos * 4.0680636E7f * fCos) + (fSin * 4.04083E7f * fSin))) * (f3 / 1000.0f);
        this.mGcLatitudeRad = (float) Math.atan(((fSin / fCos) * (4.04083E7f + fSqrt)) / (fSqrt + 4.0680636E7f));
        this.mGcLongitudeRad = (float) Math.toRadians(f2);
        this.mGcRadiusKm = (float) Math.sqrt((r12 * r12) + (r12 * 2.0f * ((float) Math.sqrt(r5))) + ((((1.65491412E15f * fCos) * fCos) + ((1.63283074E15f * fSin) * fSin)) / r3));
    }

    private static class LegendreTable {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        public final float[][] mP;
        public final float[][] mPDeriv;

        public LegendreTable(int i, float f) {
            int i2;
            double d = f;
            float fCos = (float) Math.cos(d);
            float fSin = (float) Math.sin(d);
            int i3 = i + 1;
            float[][] fArr = new float[i3][];
            this.mP = fArr;
            float[][] fArr2 = new float[i3][];
            this.mPDeriv = fArr2;
            fArr[0] = new float[]{1.0f};
            fArr2[0] = new float[]{0.0f};
            int i4 = 1;
            while (i4 <= i) {
                int i5 = i4 + 1;
                this.mP[i4] = new float[i5];
                this.mPDeriv[i4] = new float[i5];
                for (int i6 = 0; i6 <= i4; i6++) {
                    if (i4 == i6) {
                        float[][] fArr3 = this.mP;
                        int i7 = i4 - 1;
                        int i8 = i6 - 1;
                        fArr3[i4][i6] = fArr3[i7][i8] * fSin;
                        float[][] fArr4 = this.mPDeriv;
                        fArr4[i4][i6] = (fArr3[i7][i8] * fCos) + (fArr4[i7][i8] * fSin);
                    } else if (i4 == 1 || i6 == i4 - 1) {
                        float[][] fArr5 = this.mP;
                        int i9 = i4 - 1;
                        fArr5[i4][i6] = fArr5[i9][i6] * fCos;
                        float[][] fArr6 = this.mPDeriv;
                        fArr6[i4][i6] = ((-fSin) * fArr5[i9][i6]) + (fArr6[i9][i6] * fCos);
                    } else {
                        int i10 = i4 * 2;
                        float f2 = ((i2 * i2) - (i6 * i6)) / ((i10 - 1) * (i10 - 3));
                        float[][] fArr7 = this.mP;
                        int i11 = i4 - 2;
                        fArr7[i4][i6] = (fArr7[i2][i6] * fCos) - (fArr7[i11][i6] * f2);
                        float[][] fArr8 = this.mPDeriv;
                        fArr8[i4][i6] = (((-fSin) * fArr7[i2][i6]) + (fArr8[i2][i6] * fCos)) - (f2 * fArr8[i11][i6]);
                    }
                }
                i4 = i5;
            }
        }
    }

    private static float[][] computeSchmidtQuasiNormFactors(int i) {
        float[][] fArr = new float[i + 1][];
        fArr[0] = new float[]{1.0f};
        int i2 = 1;
        while (i2 <= i) {
            int i3 = i2 + 1;
            fArr[i2] = new float[i3];
            fArr[i2][0] = (fArr[i2 - 1][0] * ((i2 * 2) - 1)) / i2;
            int i4 = 1;
            while (i4 <= i2) {
                fArr[i2][i4] = fArr[i2][i4 - 1] * ((float) Math.sqrt((((i2 - i4) + 1) * (i4 == 1 ? 2 : 1)) / (i2 + i4)));
                i4++;
            }
            i2 = i3;
        }
        return fArr;
    }
}
