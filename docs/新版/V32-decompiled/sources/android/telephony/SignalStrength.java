package android.telephony;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class SignalStrength implements Parcelable {
    private static final boolean DBG = false;
    public static final int INVALID = Integer.MAX_VALUE;
    private static final String LOG_TAG = "SignalStrength";
    public static final int NUM_SIGNAL_STRENGTH_BINS = 5;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    private boolean isGsm;
    private int mCdmaDbm;
    private int mCdmaEcio;
    private int mEvdoDbm;
    private int mEvdoEcio;
    private int mEvdoSnr;
    private int mGsmBitErrorRate;
    private int mGsmSignalStrength;
    private int mLteCqi;
    private int mLteRsrp;
    private int mLteRsrq;
    private int mLteRssnr;
    private int mLteSignalStrength;
    public static final String[] SIGNAL_STRENGTH_NAMES = {"none", "poor", "moderate", "good", "great"};
    public static final Parcelable.Creator<SignalStrength> CREATOR = new Parcelable.Creator() { // from class: android.telephony.SignalStrength.1
        @Override // android.os.Parcelable.Creator
        public SignalStrength createFromParcel(Parcel parcel) {
            return new SignalStrength(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SignalStrength[] newArray(int i) {
            return new SignalStrength[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static SignalStrength newFromBundle(Bundle bundle) {
        SignalStrength signalStrength = new SignalStrength();
        signalStrength.setFromNotifierBundle(bundle);
        return signalStrength;
    }

    public SignalStrength() {
        this.mGsmSignalStrength = 99;
        this.mGsmBitErrorRate = -1;
        this.mCdmaDbm = -1;
        this.mCdmaEcio = -1;
        this.mEvdoDbm = -1;
        this.mEvdoEcio = -1;
        this.mEvdoSnr = -1;
        this.mLteSignalStrength = 99;
        this.mLteRsrp = Integer.MAX_VALUE;
        this.mLteRsrq = Integer.MAX_VALUE;
        this.mLteRssnr = Integer.MAX_VALUE;
        this.mLteCqi = Integer.MAX_VALUE;
        this.isGsm = true;
    }

    public SignalStrength(boolean z) {
        this.mGsmSignalStrength = 99;
        this.mGsmBitErrorRate = -1;
        this.mCdmaDbm = -1;
        this.mCdmaEcio = -1;
        this.mEvdoDbm = -1;
        this.mEvdoEcio = -1;
        this.mEvdoSnr = -1;
        this.mLteSignalStrength = 99;
        this.mLteRsrp = Integer.MAX_VALUE;
        this.mLteRsrq = Integer.MAX_VALUE;
        this.mLteRssnr = Integer.MAX_VALUE;
        this.mLteCqi = Integer.MAX_VALUE;
        this.isGsm = z;
    }

    public SignalStrength(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, boolean z) {
        initialize(i, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, z);
    }

    public SignalStrength(int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean z) {
        initialize(i, i2, i3, i4, i5, i6, i7, 99, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, z);
    }

    public SignalStrength(SignalStrength signalStrength) {
        copyFrom(signalStrength);
    }

    public void initialize(int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean z) {
        initialize(i, i2, i3, i4, i5, i6, i7, 99, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, z);
    }

    public void initialize(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, boolean z) {
        this.mGsmSignalStrength = i;
        this.mGsmBitErrorRate = i2;
        this.mCdmaDbm = i3;
        this.mCdmaEcio = i4;
        this.mEvdoDbm = i5;
        this.mEvdoEcio = i6;
        this.mEvdoSnr = i7;
        this.mLteSignalStrength = i8;
        this.mLteRsrp = i9;
        this.mLteRsrq = i10;
        this.mLteRssnr = i11;
        this.mLteCqi = i12;
        this.isGsm = z;
    }

    protected void copyFrom(SignalStrength signalStrength) {
        this.mGsmSignalStrength = signalStrength.mGsmSignalStrength;
        this.mGsmBitErrorRate = signalStrength.mGsmBitErrorRate;
        this.mCdmaDbm = signalStrength.mCdmaDbm;
        this.mCdmaEcio = signalStrength.mCdmaEcio;
        this.mEvdoDbm = signalStrength.mEvdoDbm;
        this.mEvdoEcio = signalStrength.mEvdoEcio;
        this.mEvdoSnr = signalStrength.mEvdoSnr;
        this.mLteSignalStrength = signalStrength.mLteSignalStrength;
        this.mLteRsrp = signalStrength.mLteRsrp;
        this.mLteRsrq = signalStrength.mLteRsrq;
        this.mLteRssnr = signalStrength.mLteRssnr;
        this.mLteCqi = signalStrength.mLteCqi;
        this.isGsm = signalStrength.isGsm;
    }

    public SignalStrength(Parcel parcel) {
        this.mGsmSignalStrength = parcel.readInt();
        this.mGsmBitErrorRate = parcel.readInt();
        this.mCdmaDbm = parcel.readInt();
        this.mCdmaEcio = parcel.readInt();
        this.mEvdoDbm = parcel.readInt();
        this.mEvdoEcio = parcel.readInt();
        this.mEvdoSnr = parcel.readInt();
        this.mLteSignalStrength = parcel.readInt();
        this.mLteRsrp = parcel.readInt();
        this.mLteRsrq = parcel.readInt();
        this.mLteRssnr = parcel.readInt();
        this.mLteCqi = parcel.readInt();
        this.isGsm = parcel.readInt() != 0;
    }

    public static SignalStrength makeSignalStrengthFromRilParcel(Parcel parcel) {
        SignalStrength signalStrength = new SignalStrength();
        signalStrength.mGsmSignalStrength = parcel.readInt();
        signalStrength.mGsmBitErrorRate = parcel.readInt();
        signalStrength.mCdmaDbm = parcel.readInt();
        signalStrength.mCdmaEcio = parcel.readInt();
        signalStrength.mEvdoDbm = parcel.readInt();
        signalStrength.mEvdoEcio = parcel.readInt();
        signalStrength.mEvdoSnr = parcel.readInt();
        signalStrength.mLteSignalStrength = parcel.readInt();
        signalStrength.mLteRsrp = parcel.readInt();
        signalStrength.mLteRsrq = parcel.readInt();
        signalStrength.mLteRssnr = parcel.readInt();
        signalStrength.mLteCqi = parcel.readInt();
        return signalStrength;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mGsmSignalStrength);
        parcel.writeInt(this.mGsmBitErrorRate);
        parcel.writeInt(this.mCdmaDbm);
        parcel.writeInt(this.mCdmaEcio);
        parcel.writeInt(this.mEvdoDbm);
        parcel.writeInt(this.mEvdoEcio);
        parcel.writeInt(this.mEvdoSnr);
        parcel.writeInt(this.mLteSignalStrength);
        parcel.writeInt(this.mLteRsrp);
        parcel.writeInt(this.mLteRsrq);
        parcel.writeInt(this.mLteRssnr);
        parcel.writeInt(this.mLteCqi);
        parcel.writeInt(this.isGsm ? 1 : 0);
    }

    public void validateInput() {
        int i = this.mGsmSignalStrength;
        if (i < 0) {
            i = 99;
        }
        this.mGsmSignalStrength = i;
        int i2 = this.mCdmaDbm;
        this.mCdmaDbm = i2 > 0 ? -i2 : -120;
        int i3 = this.mCdmaEcio;
        this.mCdmaEcio = i3 > 0 ? -i3 : -160;
        int i4 = this.mEvdoDbm;
        this.mEvdoDbm = i4 > 0 ? -i4 : -120;
        int i5 = this.mEvdoEcio;
        int i6 = -1;
        this.mEvdoEcio = i5 >= 0 ? -i5 : -1;
        int i7 = this.mEvdoSnr;
        if (i7 > 0 && i7 <= 8) {
            i6 = i7;
        }
        this.mEvdoSnr = i6;
        int i8 = this.mLteSignalStrength;
        this.mLteSignalStrength = i8 >= 0 ? i8 : 99;
        int i9 = this.mLteRsrp;
        int i10 = Integer.MAX_VALUE;
        this.mLteRsrp = (i9 < 44 || i9 > 140) ? Integer.MAX_VALUE : -i9;
        int i11 = this.mLteRsrq;
        this.mLteRsrq = (i11 < 3 || i11 > 20) ? Integer.MAX_VALUE : -i11;
        int i12 = this.mLteRssnr;
        if (i12 >= -200 && i12 <= 300) {
            i10 = i12;
        }
        this.mLteRssnr = i10;
    }

    public void setGsm(boolean z) {
        this.isGsm = z;
    }

    public int getGsmSignalStrength() {
        return this.mGsmSignalStrength;
    }

    public int getGsmBitErrorRate() {
        return this.mGsmBitErrorRate;
    }

    public int getCdmaDbm() {
        return this.mCdmaDbm;
    }

    public int getCdmaEcio() {
        return this.mCdmaEcio;
    }

    public int getEvdoDbm() {
        return this.mEvdoDbm;
    }

    public int getEvdoEcio() {
        return this.mEvdoEcio;
    }

    public int getEvdoSnr() {
        return this.mEvdoSnr;
    }

    public int getLteSignalStrength() {
        return this.mLteSignalStrength;
    }

    public int getLteRsrp() {
        return this.mLteRsrp;
    }

    public int getLteRsrq() {
        return this.mLteRsrq;
    }

    public int getLteRssnr() {
        return this.mLteRssnr;
    }

    public int getLteCqi() {
        return this.mLteCqi;
    }

    public int getLevel() {
        if (this.isGsm) {
            int lteLevel = getLteLevel();
            return lteLevel == 0 ? getGsmLevel() : lteLevel;
        }
        int cdmaLevel = getCdmaLevel();
        int evdoLevel = getEvdoLevel();
        return evdoLevel == 0 ? cdmaLevel : (cdmaLevel != 0 && cdmaLevel < evdoLevel) ? cdmaLevel : evdoLevel;
    }

    public int getAsuLevel() {
        if (this.isGsm) {
            if (getLteLevel() == 0) {
                return getGsmAsuLevel();
            }
            return getLteAsuLevel();
        }
        int cdmaAsuLevel = getCdmaAsuLevel();
        int evdoAsuLevel = getEvdoAsuLevel();
        return evdoAsuLevel == 0 ? cdmaAsuLevel : (cdmaAsuLevel != 0 && cdmaAsuLevel < evdoAsuLevel) ? cdmaAsuLevel : evdoAsuLevel;
    }

    public int getDbm() {
        if (isGsm()) {
            if (getLteLevel() == 0) {
                return getGsmDbm();
            }
            return getLteDbm();
        }
        int cdmaDbm = getCdmaDbm();
        int evdoDbm = getEvdoDbm();
        return evdoDbm == -120 ? cdmaDbm : (cdmaDbm != -120 && cdmaDbm < evdoDbm) ? cdmaDbm : evdoDbm;
    }

    public int getGsmDbm() {
        int gsmSignalStrength = getGsmSignalStrength();
        if (gsmSignalStrength == 99) {
            gsmSignalStrength = -1;
        }
        if (gsmSignalStrength != -1) {
            return (gsmSignalStrength * 2) - 113;
        }
        return -1;
    }

    public int getGsmLevel() {
        int gsmSignalStrength = getGsmSignalStrength();
        if (gsmSignalStrength <= 2 || gsmSignalStrength == 99) {
            return 0;
        }
        if (gsmSignalStrength >= 12) {
            return 4;
        }
        if (gsmSignalStrength >= 8) {
            return 3;
        }
        return gsmSignalStrength >= 5 ? 2 : 1;
    }

    public int getGsmAsuLevel() {
        return getGsmSignalStrength();
    }

    public int getCdmaLevel() {
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        int i = 1;
        int i2 = cdmaDbm >= -75 ? 4 : cdmaDbm >= -85 ? 3 : cdmaDbm >= -95 ? 2 : cdmaDbm >= -100 ? 1 : 0;
        if (cdmaEcio >= -90) {
            i = 4;
        } else if (cdmaEcio >= -110) {
            i = 3;
        } else if (cdmaEcio >= -130) {
            i = 2;
        } else if (cdmaEcio < -150) {
            i = 0;
        }
        return i2 < i ? i2 : i;
    }

    public int getCdmaAsuLevel() {
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        int i = 1;
        int i2 = cdmaDbm >= -75 ? 16 : cdmaDbm >= -82 ? 8 : cdmaDbm >= -90 ? 4 : cdmaDbm >= -95 ? 2 : cdmaDbm >= -100 ? 1 : 99;
        if (cdmaEcio >= -90) {
            i = 16;
        } else if (cdmaEcio >= -100) {
            i = 8;
        } else if (cdmaEcio >= -115) {
            i = 4;
        } else if (cdmaEcio >= -130) {
            i = 2;
        } else if (cdmaEcio < -150) {
            i = 99;
        }
        return i2 < i ? i2 : i;
    }

    public int getEvdoLevel() {
        int evdoDbm = getEvdoDbm();
        int evdoSnr = getEvdoSnr();
        int i = 0;
        int i2 = evdoDbm >= -65 ? 4 : evdoDbm >= -75 ? 3 : evdoDbm >= -90 ? 2 : evdoDbm >= -105 ? 1 : 0;
        if (evdoSnr >= 7) {
            i = 4;
        } else if (evdoSnr >= 5) {
            i = 3;
        } else if (evdoSnr >= 3) {
            i = 2;
        } else if (evdoSnr >= 1) {
            i = 1;
        }
        return i2 < i ? i2 : i;
    }

    public int getEvdoAsuLevel() {
        int evdoDbm = getEvdoDbm();
        int evdoSnr = getEvdoSnr();
        int i = 99;
        int i2 = evdoDbm >= -65 ? 16 : evdoDbm >= -75 ? 8 : evdoDbm >= -85 ? 4 : evdoDbm >= -95 ? 2 : evdoDbm >= -105 ? 1 : 99;
        if (evdoSnr >= 7) {
            i = 16;
        } else if (evdoSnr >= 6) {
            i = 8;
        } else if (evdoSnr >= 5) {
            i = 4;
        } else if (evdoSnr >= 3) {
            i = 2;
        } else if (evdoSnr >= 1) {
            i = 1;
        }
        return i2 < i ? i2 : i;
    }

    public int getLteDbm() {
        return this.mLteRsrp;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0031  */
    /* JADX WARN: Removed duplicated region for block: B:4:0x000c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int getLteLevel() {
        /*
            r9 = this;
            int r0 = r9.mLteRsrp
            r1 = 1
            r2 = 2
            r3 = 3
            r4 = 4
            r5 = 0
            r6 = -1
            r7 = -44
            if (r0 <= r7) goto Le
        Lc:
            r0 = r6
            goto L2b
        Le:
            r7 = -85
            if (r0 < r7) goto L14
            r0 = r4
            goto L2b
        L14:
            r7 = -95
            if (r0 < r7) goto L1a
            r0 = r3
            goto L2b
        L1a:
            r7 = -105(0xffffffffffffff97, float:NaN)
            if (r0 < r7) goto L20
            r0 = r2
            goto L2b
        L20:
            r7 = -115(0xffffffffffffff8d, float:NaN)
            if (r0 < r7) goto L26
            r0 = r1
            goto L2b
        L26:
            r7 = -140(0xffffffffffffff74, float:NaN)
            if (r0 < r7) goto Lc
            r0 = r5
        L2b:
            int r7 = r9.mLteRssnr
            r8 = 300(0x12c, float:4.2E-43)
            if (r7 <= r8) goto L33
        L31:
            r7 = r6
            goto L50
        L33:
            r8 = 130(0x82, float:1.82E-43)
            if (r7 < r8) goto L39
            r7 = r4
            goto L50
        L39:
            r8 = 45
            if (r7 < r8) goto L3f
            r7 = r3
            goto L50
        L3f:
            r8 = 10
            if (r7 < r8) goto L45
            r7 = r2
            goto L50
        L45:
            r8 = -30
            if (r7 < r8) goto L4b
            r7 = r1
            goto L50
        L4b:
            r8 = -200(0xffffffffffffff38, float:NaN)
            if (r7 < r8) goto L31
            r7 = r5
        L50:
            if (r7 == r6) goto L59
            if (r0 == r6) goto L59
            if (r0 >= r7) goto L57
            goto L58
        L57:
            r0 = r7
        L58:
            return r0
        L59:
            if (r7 == r6) goto L5c
            return r7
        L5c:
            if (r0 == r6) goto L5f
            return r0
        L5f:
            int r0 = r9.mLteSignalStrength
            r6 = 63
            if (r0 <= r6) goto L67
        L65:
            r1 = r5
            goto L7a
        L67:
            r6 = 12
            if (r0 < r6) goto L6d
            r1 = r4
            goto L7a
        L6d:
            r4 = 8
            if (r0 < r4) goto L73
            r1 = r3
            goto L7a
        L73:
            r3 = 5
            if (r0 < r3) goto L78
            r1 = r2
            goto L7a
        L78:
            if (r0 < 0) goto L65
        L7a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.telephony.SignalStrength.getLteLevel():int");
    }

    public int getLteAsuLevel() {
        int lteDbm = getLteDbm();
        if (lteDbm == Integer.MAX_VALUE) {
            return 255;
        }
        return lteDbm + 140;
    }

    public boolean isGsm() {
        return this.isGsm;
    }

    public int hashCode() {
        return (this.mGsmSignalStrength * 31) + (this.mGsmBitErrorRate * 31) + (this.mCdmaDbm * 31) + (this.mCdmaEcio * 31) + (this.mEvdoDbm * 31) + (this.mEvdoEcio * 31) + (this.mEvdoSnr * 31) + (this.mLteSignalStrength * 31) + (this.mLteRsrp * 31) + (this.mLteRsrq * 31) + (this.mLteRssnr * 31) + (this.mLteCqi * 31) + (this.isGsm ? 1 : 0);
    }

    public boolean equals(Object obj) {
        try {
            SignalStrength signalStrength = (SignalStrength) obj;
            return obj != null && this.mGsmSignalStrength == signalStrength.mGsmSignalStrength && this.mGsmBitErrorRate == signalStrength.mGsmBitErrorRate && this.mCdmaDbm == signalStrength.mCdmaDbm && this.mCdmaEcio == signalStrength.mCdmaEcio && this.mEvdoDbm == signalStrength.mEvdoDbm && this.mEvdoEcio == signalStrength.mEvdoEcio && this.mEvdoSnr == signalStrength.mEvdoSnr && this.mLteSignalStrength == signalStrength.mLteSignalStrength && this.mLteRsrp == signalStrength.mLteRsrp && this.mLteRsrq == signalStrength.mLteRsrq && this.mLteRssnr == signalStrength.mLteRssnr && this.mLteCqi == signalStrength.mLteCqi && this.isGsm == signalStrength.isGsm;
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public String toString() {
        return "SignalStrength: " + this.mGsmSignalStrength + " " + this.mGsmBitErrorRate + " " + this.mCdmaDbm + " " + this.mCdmaEcio + " " + this.mEvdoDbm + " " + this.mEvdoEcio + " " + this.mEvdoSnr + " " + this.mLteSignalStrength + " " + this.mLteRsrp + " " + this.mLteRsrq + " " + this.mLteRssnr + " " + this.mLteCqi + " " + (this.isGsm ? "gsm|lte" : "cdma");
    }

    private void setFromNotifierBundle(Bundle bundle) {
        this.mGsmSignalStrength = bundle.getInt("GsmSignalStrength");
        this.mGsmBitErrorRate = bundle.getInt("GsmBitErrorRate");
        this.mCdmaDbm = bundle.getInt("CdmaDbm");
        this.mCdmaEcio = bundle.getInt("CdmaEcio");
        this.mEvdoDbm = bundle.getInt("EvdoDbm");
        this.mEvdoEcio = bundle.getInt("EvdoEcio");
        this.mEvdoSnr = bundle.getInt("EvdoSnr");
        this.mLteSignalStrength = bundle.getInt("LteSignalStrength");
        this.mLteRsrp = bundle.getInt("LteRsrp");
        this.mLteRsrq = bundle.getInt("LteRsrq");
        this.mLteRssnr = bundle.getInt("LteRssnr");
        this.mLteCqi = bundle.getInt("LteCqi");
        this.isGsm = bundle.getBoolean("isGsm");
    }

    public void fillInNotifierBundle(Bundle bundle) {
        bundle.putInt("GsmSignalStrength", this.mGsmSignalStrength);
        bundle.putInt("GsmBitErrorRate", this.mGsmBitErrorRate);
        bundle.putInt("CdmaDbm", this.mCdmaDbm);
        bundle.putInt("CdmaEcio", this.mCdmaEcio);
        bundle.putInt("EvdoDbm", this.mEvdoDbm);
        bundle.putInt("EvdoEcio", this.mEvdoEcio);
        bundle.putInt("EvdoSnr", this.mEvdoSnr);
        bundle.putInt("LteSignalStrength", this.mLteSignalStrength);
        bundle.putInt("LteRsrp", this.mLteRsrp);
        bundle.putInt("LteRsrq", this.mLteRsrq);
        bundle.putInt("LteRssnr", this.mLteRssnr);
        bundle.putInt("LteCqi", this.mLteCqi);
        bundle.putBoolean("isGsm", Boolean.valueOf(this.isGsm).booleanValue());
    }

    private static void log(String str) {
        Rlog.w(LOG_TAG, str);
    }
}
