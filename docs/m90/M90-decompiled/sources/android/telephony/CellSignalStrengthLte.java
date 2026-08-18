package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class CellSignalStrengthLte extends CellSignalStrength implements Parcelable {
    public static final Parcelable.Creator<CellSignalStrengthLte> CREATOR = new Parcelable.Creator<CellSignalStrengthLte>() { // from class: android.telephony.CellSignalStrengthLte.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthLte createFromParcel(Parcel parcel) {
            return new CellSignalStrengthLte(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthLte[] newArray(int i) {
            return new CellSignalStrengthLte[i];
        }
    };
    private static final boolean DBG = false;
    private static final String LOG_TAG = "CellSignalStrengthLte";
    private int mCqi;
    private int mRsrp;
    private int mRsrq;
    private int mRssnr;
    private int mSignalStrength;
    private int mTimingAdvance;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CellSignalStrengthLte() {
        setDefaultValues();
    }

    public CellSignalStrengthLte(int i, int i2, int i3, int i4, int i5, int i6) {
        initialize(i, i2, i3, i4, i5, i6);
    }

    public CellSignalStrengthLte(CellSignalStrengthLte cellSignalStrengthLte) {
        copyFrom(cellSignalStrengthLte);
    }

    public void initialize(int i, int i2, int i3, int i4, int i5, int i6) {
        this.mSignalStrength = i;
        this.mRsrp = i2;
        this.mRsrq = i3;
        this.mRssnr = i4;
        this.mCqi = i5;
        this.mTimingAdvance = i6;
    }

    public void initialize(SignalStrength signalStrength, int i) {
        this.mSignalStrength = signalStrength.getLteSignalStrength();
        this.mRsrp = signalStrength.getLteRsrp();
        this.mRsrq = signalStrength.getLteRsrq();
        this.mRssnr = signalStrength.getLteRssnr();
        this.mCqi = signalStrength.getLteCqi();
        this.mTimingAdvance = i;
    }

    protected void copyFrom(CellSignalStrengthLte cellSignalStrengthLte) {
        this.mSignalStrength = cellSignalStrengthLte.mSignalStrength;
        this.mRsrp = cellSignalStrengthLte.mRsrp;
        this.mRsrq = cellSignalStrengthLte.mRsrq;
        this.mRssnr = cellSignalStrengthLte.mRssnr;
        this.mCqi = cellSignalStrengthLte.mCqi;
        this.mTimingAdvance = cellSignalStrengthLte.mTimingAdvance;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthLte copy() {
        return new CellSignalStrengthLte(this);
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mSignalStrength = Integer.MAX_VALUE;
        this.mRsrp = Integer.MAX_VALUE;
        this.mRsrq = Integer.MAX_VALUE;
        this.mRssnr = Integer.MAX_VALUE;
        this.mCqi = Integer.MAX_VALUE;
        this.mTimingAdvance = Integer.MAX_VALUE;
    }

    @Override // android.telephony.CellSignalStrength
    public int getLevel() {
        int i = this.mRsrp;
        int i2 = 2;
        int i3 = i == Integer.MAX_VALUE ? 0 : i >= -95 ? 4 : i >= -105 ? 3 : i >= -115 ? 2 : 1;
        int i4 = this.mRssnr;
        if (i4 == Integer.MAX_VALUE) {
            i2 = 0;
        } else if (i4 >= 45) {
            i2 = 4;
        } else if (i4 >= 10) {
            i2 = 3;
        } else if (i4 < -30) {
            i2 = 1;
        }
        return (i != Integer.MAX_VALUE && (i4 == Integer.MAX_VALUE || i2 >= i3)) ? i3 : i2;
    }

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        return this.mRsrp;
    }

    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
        int dbm = getDbm();
        if (dbm <= -140) {
            return 0;
        }
        if (dbm >= -43) {
            return 97;
        }
        return dbm + 140;
    }

    public int getTimingAdvance() {
        return this.mTimingAdvance;
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return (this.mSignalStrength * 31) + (this.mRsrp * 31) + (this.mRsrq * 31) + (this.mRssnr * 31) + (this.mCqi * 31) + (this.mTimingAdvance * 31);
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object obj) {
        try {
            CellSignalStrengthLte cellSignalStrengthLte = (CellSignalStrengthLte) obj;
            return obj != null && this.mSignalStrength == cellSignalStrengthLte.mSignalStrength && this.mRsrp == cellSignalStrengthLte.mRsrp && this.mRsrq == cellSignalStrengthLte.mRsrq && this.mRssnr == cellSignalStrengthLte.mRssnr && this.mCqi == cellSignalStrengthLte.mCqi && this.mTimingAdvance == cellSignalStrengthLte.mTimingAdvance;
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public String toString() {
        return "CellSignalStrengthLte: ss=" + this.mSignalStrength + " rsrp=" + this.mRsrp + " rsrq=" + this.mRsrq + " rssnr=" + this.mRssnr + " cqi=" + this.mCqi + " ta=" + this.mTimingAdvance;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSignalStrength);
        parcel.writeInt(this.mRsrp * (-1));
        parcel.writeInt(this.mRsrq * (-1));
        parcel.writeInt(this.mRssnr);
        parcel.writeInt(this.mCqi);
        parcel.writeInt(this.mTimingAdvance);
    }

    private CellSignalStrengthLte(Parcel parcel) {
        this.mSignalStrength = parcel.readInt();
        this.mRsrp = parcel.readInt() * (-1);
        this.mRsrq = parcel.readInt() * (-1);
        this.mRssnr = parcel.readInt();
        this.mCqi = parcel.readInt();
        this.mTimingAdvance = parcel.readInt();
    }

    private static void log(String str) {
        Rlog.w(LOG_TAG, str);
    }
}
