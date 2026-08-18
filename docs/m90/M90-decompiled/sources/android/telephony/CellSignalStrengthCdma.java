package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class CellSignalStrengthCdma extends CellSignalStrength implements Parcelable {
    public static final Parcelable.Creator<CellSignalStrengthCdma> CREATOR = new Parcelable.Creator<CellSignalStrengthCdma>() { // from class: android.telephony.CellSignalStrengthCdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthCdma createFromParcel(Parcel parcel) {
            return new CellSignalStrengthCdma(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthCdma[] newArray(int i) {
            return new CellSignalStrengthCdma[i];
        }
    };
    private static final boolean DBG = false;
    private static final String LOG_TAG = "CellSignalStrengthCdma";
    private int mCdmaDbm;
    private int mCdmaEcio;
    private int mEvdoDbm;
    private int mEvdoEcio;
    private int mEvdoSnr;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CellSignalStrengthCdma() {
        setDefaultValues();
    }

    public CellSignalStrengthCdma(int i, int i2, int i3, int i4, int i5) {
        initialize(i, i2, i3, i4, i5);
    }

    public CellSignalStrengthCdma(CellSignalStrengthCdma cellSignalStrengthCdma) {
        copyFrom(cellSignalStrengthCdma);
    }

    public void initialize(int i, int i2, int i3, int i4, int i5) {
        this.mCdmaDbm = i;
        this.mCdmaEcio = i2;
        this.mEvdoDbm = i3;
        this.mEvdoEcio = i4;
        this.mEvdoSnr = i5;
    }

    protected void copyFrom(CellSignalStrengthCdma cellSignalStrengthCdma) {
        this.mCdmaDbm = cellSignalStrengthCdma.mCdmaDbm;
        this.mCdmaEcio = cellSignalStrengthCdma.mCdmaEcio;
        this.mEvdoDbm = cellSignalStrengthCdma.mEvdoDbm;
        this.mEvdoEcio = cellSignalStrengthCdma.mEvdoEcio;
        this.mEvdoSnr = cellSignalStrengthCdma.mEvdoSnr;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthCdma copy() {
        return new CellSignalStrengthCdma(this);
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mCdmaDbm = Integer.MAX_VALUE;
        this.mCdmaEcio = Integer.MAX_VALUE;
        this.mEvdoDbm = Integer.MAX_VALUE;
        this.mEvdoEcio = Integer.MAX_VALUE;
        this.mEvdoSnr = Integer.MAX_VALUE;
    }

    @Override // android.telephony.CellSignalStrength
    public int getLevel() {
        int cdmaLevel = getCdmaLevel();
        int evdoLevel = getEvdoLevel();
        if (evdoLevel == 0) {
            return getCdmaLevel();
        }
        if (cdmaLevel == 0) {
            return getEvdoLevel();
        }
        return cdmaLevel < evdoLevel ? cdmaLevel : evdoLevel;
    }

    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
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

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        int cdmaDbm = getCdmaDbm();
        int evdoDbm = getEvdoDbm();
        return cdmaDbm < evdoDbm ? cdmaDbm : evdoDbm;
    }

    public int getCdmaDbm() {
        return this.mCdmaDbm;
    }

    public void setCdmaDbm(int i) {
        this.mCdmaDbm = i;
    }

    public int getCdmaEcio() {
        return this.mCdmaEcio;
    }

    public void setCdmaEcio(int i) {
        this.mCdmaEcio = i;
    }

    public int getEvdoDbm() {
        return this.mEvdoDbm;
    }

    public void setEvdoDbm(int i) {
        this.mEvdoDbm = i;
    }

    public int getEvdoEcio() {
        return this.mEvdoEcio;
    }

    public void setEvdoEcio(int i) {
        this.mEvdoEcio = i;
    }

    public int getEvdoSnr() {
        return this.mEvdoSnr;
    }

    public void setEvdoSnr(int i) {
        this.mEvdoSnr = i;
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return (this.mCdmaDbm * 31) + (this.mCdmaEcio * 31) + (this.mEvdoDbm * 31) + (this.mEvdoEcio * 31) + (this.mEvdoSnr * 31);
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object obj) {
        try {
            CellSignalStrengthCdma cellSignalStrengthCdma = (CellSignalStrengthCdma) obj;
            return obj != null && this.mCdmaDbm == cellSignalStrengthCdma.mCdmaDbm && this.mCdmaEcio == cellSignalStrengthCdma.mCdmaEcio && this.mEvdoDbm == cellSignalStrengthCdma.mEvdoDbm && this.mEvdoEcio == cellSignalStrengthCdma.mEvdoEcio && this.mEvdoSnr == cellSignalStrengthCdma.mEvdoSnr;
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public String toString() {
        return "CellSignalStrengthCdma: cdmaDbm=" + this.mCdmaDbm + " cdmaEcio=" + this.mCdmaEcio + " evdoDbm=" + this.mEvdoDbm + " evdoEcio=" + this.mEvdoEcio + " evdoSnr=" + this.mEvdoSnr;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mCdmaDbm * (-1));
        parcel.writeInt(this.mCdmaEcio * (-1));
        parcel.writeInt(this.mEvdoDbm * (-1));
        parcel.writeInt(this.mEvdoEcio * (-1));
        parcel.writeInt(this.mEvdoSnr);
    }

    private CellSignalStrengthCdma(Parcel parcel) {
        this.mCdmaDbm = parcel.readInt() * (-1);
        this.mCdmaEcio = parcel.readInt() * (-1);
        this.mEvdoDbm = parcel.readInt() * (-1);
        this.mEvdoEcio = parcel.readInt() * (-1);
        this.mEvdoSnr = parcel.readInt();
    }

    private static void log(String str) {
        Rlog.w(LOG_TAG, str);
    }
}
