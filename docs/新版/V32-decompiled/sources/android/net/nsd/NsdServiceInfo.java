package android.net.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: classes.dex */
public final class NsdServiceInfo implements Parcelable {
    public static final Parcelable.Creator<NsdServiceInfo> CREATOR = new Parcelable.Creator<NsdServiceInfo>() { // from class: android.net.nsd.NsdServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NsdServiceInfo createFromParcel(Parcel parcel) {
            NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
            nsdServiceInfo.mServiceName = parcel.readString();
            nsdServiceInfo.mServiceType = parcel.readString();
            nsdServiceInfo.mTxtRecord = (DnsSdTxtRecord) parcel.readParcelable(null);
            if (parcel.readByte() == 1) {
                try {
                    nsdServiceInfo.mHost = InetAddress.getByAddress(parcel.createByteArray());
                } catch (UnknownHostException unused) {
                }
            }
            nsdServiceInfo.mPort = parcel.readInt();
            return nsdServiceInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NsdServiceInfo[] newArray(int i) {
            return new NsdServiceInfo[i];
        }
    };
    private InetAddress mHost;
    private int mPort;
    private String mServiceName;
    private String mServiceType;
    private DnsSdTxtRecord mTxtRecord;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NsdServiceInfo() {
    }

    public NsdServiceInfo(String str, String str2, DnsSdTxtRecord dnsSdTxtRecord) {
        this.mServiceName = str;
        this.mServiceType = str2;
        this.mTxtRecord = dnsSdTxtRecord;
    }

    public String getServiceName() {
        return this.mServiceName;
    }

    public void setServiceName(String str) {
        this.mServiceName = str;
    }

    public String getServiceType() {
        return this.mServiceType;
    }

    public void setServiceType(String str) {
        this.mServiceType = str;
    }

    public DnsSdTxtRecord getTxtRecord() {
        return this.mTxtRecord;
    }

    public void setTxtRecord(DnsSdTxtRecord dnsSdTxtRecord) {
        this.mTxtRecord = new DnsSdTxtRecord(dnsSdTxtRecord);
    }

    public InetAddress getHost() {
        return this.mHost;
    }

    public void setHost(InetAddress inetAddress) {
        this.mHost = inetAddress;
    }

    public int getPort() {
        return this.mPort;
    }

    public void setPort(int i) {
        this.mPort = i;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("name: ").append(this.mServiceName).append("type: ").append(this.mServiceType).append("host: ").append(this.mHost).append("port: ").append(this.mPort).append("txtRecord: ").append(this.mTxtRecord);
        return stringBuffer.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mServiceName);
        parcel.writeString(this.mServiceType);
        parcel.writeParcelable(this.mTxtRecord, i);
        if (this.mHost != null) {
            parcel.writeByte((byte) 1);
            parcel.writeByteArray(this.mHost.getAddress());
        } else {
            parcel.writeByte((byte) 0);
        }
        parcel.writeInt(this.mPort);
    }
}
