package android.hardware.camera2;

import android.widget.ExpandableListView;

/* JADX INFO: loaded from: classes.dex */
public final class Rational {
    private final int mDenominator;
    private final int mNumerator;

    public Rational(int i, int i2) {
        if (i2 < 0) {
            i = -i;
            i2 = -i2;
        }
        this.mNumerator = i;
        this.mDenominator = i2;
    }

    public int getNumerator() {
        if (this.mDenominator == 0) {
            return 0;
        }
        return this.mNumerator;
    }

    public int getDenominator() {
        return this.mDenominator;
    }

    private boolean isNaN() {
        return this.mDenominator == 0 && this.mNumerator == 0;
    }

    private boolean isInf() {
        return this.mDenominator == 0 && this.mNumerator > 0;
    }

    private boolean isNegInf() {
        return this.mDenominator == 0 && this.mNumerator < 0;
    }

    public boolean equals(Object obj) {
        int i;
        if (obj != null && (obj instanceof Rational)) {
            Rational rational = (Rational) obj;
            int i2 = this.mDenominator;
            if (i2 == 0 || (i = rational.mDenominator) == 0) {
                if (isNaN() && rational.isNaN()) {
                    return true;
                }
                if ((isInf() && rational.isInf()) || (isNegInf() && rational.isNegInf())) {
                    return true;
                }
            } else {
                if (this.mNumerator == rational.mNumerator && i2 == i) {
                    return true;
                }
                int iGcd = gcd();
                int iGcd2 = rational.gcd();
                return this.mNumerator / iGcd == rational.mNumerator / iGcd2 && this.mDenominator / iGcd == rational.mDenominator / iGcd2;
            }
        }
        return false;
    }

    public String toString() {
        return isNaN() ? "NaN" : isInf() ? "Infinity" : isNegInf() ? "-Infinity" : this.mNumerator + "/" + this.mDenominator;
    }

    public float toFloat() {
        return this.mNumerator / this.mDenominator;
    }

    public int hashCode() {
        return Long.valueOf(((((long) this.mNumerator) & ExpandableListView.PACKED_POSITION_VALUE_NULL) << 32) | (ExpandableListView.PACKED_POSITION_VALUE_NULL & ((long) this.mDenominator))).hashCode();
    }

    public int gcd() {
        int i = this.mNumerator;
        int i2 = this.mDenominator;
        while (true) {
            int i3 = i2;
            int i4 = i;
            i = i3;
            if (i != 0) {
                i2 = i4 % i;
            } else {
                return Math.abs(i4);
            }
        }
    }
}
