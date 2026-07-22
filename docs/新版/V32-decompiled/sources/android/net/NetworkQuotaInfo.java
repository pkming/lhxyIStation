package android.net;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class NetworkQuotaInfo implements Parcelable {
    public static final Parcelable.Creator<NetworkQuotaInfo> CREATOR = new Parcelable.Creator<NetworkQuotaInfo>() { // from class: android.net.NetworkQuotaInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkQuotaInfo createFromParcel(Parcel parcel) {
            return new NetworkQuotaInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkQuotaInfo[] newArray(int i) {
            return new NetworkQuotaInfo[i];
        }
    };
    public static final long NO_LIMIT = -1;
    private final long mEstimatedBytes;
    private final long mHardLimitBytes;
    private final long mSoftLimitBytes;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NetworkQuotaInfo(long j, long j2, long j3) {
        this.mEstimatedBytes = j;
        this.mSoftLimitBytes = j2;
        this.mHardLimitBytes = j3;
    }

    public NetworkQuotaInfo(Parcel parcel) {
        this.mEstimatedBytes = parcel.readLong();
        this.mSoftLimitBytes = parcel.readLong();
        this.mHardLimitBytes = parcel.readLong();
    }

    public long getEstimatedBytes() {
        return this.mEstimatedBytes;
    }

    public long getSoftLimitBytes() {
        return this.mSoftLimitBytes;
    }

    public long getHardLimitBytes() {
        return this.mHardLimitBytes;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.mEstimatedBytes);
        parcel.writeLong(this.mSoftLimitBytes);
        parcel.writeLong(this.mHardLimitBytes);
    }
}
