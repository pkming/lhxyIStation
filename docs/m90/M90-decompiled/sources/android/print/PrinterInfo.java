package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public final class PrinterInfo implements Parcelable {
    public static final Parcelable.Creator<PrinterInfo> CREATOR = new Parcelable.Creator<PrinterInfo>() { // from class: android.print.PrinterInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrinterInfo createFromParcel(Parcel parcel) {
            return new PrinterInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrinterInfo[] newArray(int i) {
            return new PrinterInfo[i];
        }
    };
    public static final int STATUS_BUSY = 2;
    public static final int STATUS_IDLE = 1;
    public static final int STATUS_UNAVAILABLE = 3;
    private PrinterCapabilitiesInfo mCapabilities;
    private String mDescription;
    private PrinterId mId;
    private String mName;
    private int mStatus;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private PrinterInfo() {
    }

    private PrinterInfo(PrinterInfo printerInfo) {
        copyFrom(printerInfo);
    }

    public void copyFrom(PrinterInfo printerInfo) {
        if (this == printerInfo) {
            return;
        }
        this.mId = printerInfo.mId;
        this.mName = printerInfo.mName;
        this.mStatus = printerInfo.mStatus;
        this.mDescription = printerInfo.mDescription;
        PrinterCapabilitiesInfo printerCapabilitiesInfo = printerInfo.mCapabilities;
        if (printerCapabilitiesInfo != null) {
            PrinterCapabilitiesInfo printerCapabilitiesInfo2 = this.mCapabilities;
            if (printerCapabilitiesInfo2 != null) {
                printerCapabilitiesInfo2.copyFrom(printerCapabilitiesInfo);
                return;
            } else {
                this.mCapabilities = new PrinterCapabilitiesInfo(printerInfo.mCapabilities);
                return;
            }
        }
        this.mCapabilities = null;
    }

    public PrinterId getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public PrinterCapabilitiesInfo getCapabilities() {
        return this.mCapabilities;
    }

    private PrinterInfo(Parcel parcel) {
        this.mId = (PrinterId) parcel.readParcelable(null);
        this.mName = parcel.readString();
        this.mStatus = parcel.readInt();
        this.mDescription = parcel.readString();
        this.mCapabilities = (PrinterCapabilitiesInfo) parcel.readParcelable(null);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mId, i);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mStatus);
        parcel.writeString(this.mDescription);
        parcel.writeParcelable(this.mCapabilities, i);
    }

    public int hashCode() {
        PrinterId printerId = this.mId;
        int iHashCode = ((printerId != null ? printerId.hashCode() : 0) + 31) * 31;
        String str = this.mName;
        int iHashCode2 = (((iHashCode + (str != null ? str.hashCode() : 0)) * 31) + this.mStatus) * 31;
        String str2 = this.mDescription;
        int iHashCode3 = (iHashCode2 + (str2 != null ? str2.hashCode() : 0)) * 31;
        PrinterCapabilitiesInfo printerCapabilitiesInfo = this.mCapabilities;
        return iHashCode3 + (printerCapabilitiesInfo != null ? printerCapabilitiesInfo.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrinterInfo printerInfo = (PrinterInfo) obj;
        PrinterId printerId = this.mId;
        if (printerId == null) {
            if (printerInfo.mId != null) {
                return false;
            }
        } else if (!printerId.equals(printerInfo.mId)) {
            return false;
        }
        if (!TextUtils.equals(this.mName, printerInfo.mName) || this.mStatus != printerInfo.mStatus || !TextUtils.equals(this.mDescription, printerInfo.mDescription)) {
            return false;
        }
        PrinterCapabilitiesInfo printerCapabilitiesInfo = this.mCapabilities;
        if (printerCapabilitiesInfo == null) {
            if (printerInfo.mCapabilities != null) {
                return false;
            }
        } else if (!printerCapabilitiesInfo.equals(printerInfo.mCapabilities)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PrinterInfo{");
        sb.append("id=").append(this.mId);
        sb.append(", name=").append(this.mName);
        sb.append(", status=").append(this.mStatus);
        sb.append(", description=").append(this.mDescription);
        sb.append(", capabilities=").append(this.mCapabilities);
        sb.append("\"}");
        return sb.toString();
    }

    public static final class Builder {
        private final PrinterInfo mPrototype;

        private boolean isValidStatus(int i) {
            return i == 1 || i == 2 || i == 3;
        }

        public Builder(PrinterId printerId, String str, int i) {
            if (printerId == null) {
                throw new IllegalArgumentException("printerId cannot be null.");
            }
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("name cannot be empty.");
            }
            if (!isValidStatus(i)) {
                throw new IllegalArgumentException("status is invalid.");
            }
            PrinterInfo printerInfo = new PrinterInfo();
            this.mPrototype = printerInfo;
            printerInfo.mId = printerId;
            printerInfo.mName = str;
            printerInfo.mStatus = i;
        }

        public Builder(PrinterInfo printerInfo) {
            PrinterInfo printerInfo2 = new PrinterInfo();
            this.mPrototype = printerInfo2;
            printerInfo2.copyFrom(printerInfo);
        }

        public Builder setStatus(int i) {
            this.mPrototype.mStatus = i;
            return this;
        }

        public Builder setName(String str) {
            this.mPrototype.mName = str;
            return this;
        }

        public Builder setDescription(String str) {
            this.mPrototype.mDescription = str;
            return this;
        }

        public Builder setCapabilities(PrinterCapabilitiesInfo printerCapabilitiesInfo) {
            this.mPrototype.mCapabilities = printerCapabilitiesInfo;
            return this;
        }

        public PrinterInfo build() {
            return this.mPrototype;
        }
    }
}
