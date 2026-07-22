package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class ScanResult implements Parcelable {
    public static final Parcelable.Creator<ScanResult> CREATOR = new Parcelable.Creator<ScanResult>() { // from class: android.net.wifi.ScanResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScanResult createFromParcel(Parcel parcel) {
            return new ScanResult(parcel.readInt() == 1 ? WifiSsid.CREATOR.createFromParcel(parcel) : null, parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readLong(), parcel.readInt(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScanResult[] newArray(int i) {
            return new ScanResult[i];
        }
    };
    public static final int UNSPECIFIED = -1;
    public String BSSID;
    public String SSID;
    public String capabilities;
    public int distanceCm;
    public int distanceSdCm;
    public int frequency;
    public int level;
    public long timestamp;
    public WifiSsid wifiSsid;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ScanResult(WifiSsid wifiSsid, String str, String str2, int i, int i2, long j) {
        this.wifiSsid = wifiSsid;
        this.SSID = wifiSsid != null ? wifiSsid.toString() : WifiSsid.NONE;
        this.BSSID = str;
        this.capabilities = str2;
        this.level = i;
        this.frequency = i2;
        this.timestamp = j;
        this.distanceCm = -1;
        this.distanceSdCm = -1;
    }

    public ScanResult(WifiSsid wifiSsid, String str, String str2, int i, int i2, long j, int i3, int i4) {
        this.wifiSsid = wifiSsid;
        this.SSID = wifiSsid != null ? wifiSsid.toString() : WifiSsid.NONE;
        this.BSSID = str;
        this.capabilities = str2;
        this.level = i;
        this.frequency = i2;
        this.timestamp = j;
        this.distanceCm = i3;
        this.distanceSdCm = i4;
    }

    public ScanResult(ScanResult scanResult) {
        if (scanResult != null) {
            this.wifiSsid = scanResult.wifiSsid;
            this.SSID = scanResult.SSID;
            this.BSSID = scanResult.BSSID;
            this.capabilities = scanResult.capabilities;
            this.level = scanResult.level;
            this.frequency = scanResult.frequency;
            this.timestamp = scanResult.timestamp;
            this.distanceCm = scanResult.distanceCm;
            this.distanceSdCm = scanResult.distanceSdCm;
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBufferAppend = stringBuffer.append("SSID: ");
        Object obj = this.wifiSsid;
        if (obj == null) {
            obj = WifiSsid.NONE;
        }
        StringBuffer stringBufferAppend2 = stringBufferAppend.append(obj).append(", BSSID: ");
        String str = this.BSSID;
        if (str == null) {
            str = "<none>";
        }
        StringBuffer stringBufferAppend3 = stringBufferAppend2.append(str).append(", capabilities: ");
        String str2 = this.capabilities;
        stringBufferAppend3.append(str2 != null ? str2 : "<none>").append(", level: ").append(this.level).append(", frequency: ").append(this.frequency).append(", timestamp: ").append(this.timestamp);
        StringBuffer stringBufferAppend4 = stringBuffer.append(", distance: ");
        int i = this.distanceCm;
        stringBufferAppend4.append(i != -1 ? Integer.valueOf(i) : "?").append("(cm)");
        StringBuffer stringBufferAppend5 = stringBuffer.append(", distanceSd: ");
        int i2 = this.distanceSdCm;
        stringBufferAppend5.append(i2 != -1 ? Integer.valueOf(i2) : "?").append("(cm)");
        return stringBuffer.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.wifiSsid != null) {
            parcel.writeInt(1);
            this.wifiSsid.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeString(this.BSSID);
        parcel.writeString(this.capabilities);
        parcel.writeInt(this.level);
        parcel.writeInt(this.frequency);
        parcel.writeLong(this.timestamp);
        parcel.writeInt(this.distanceCm);
        parcel.writeInt(this.distanceSdCm);
    }
}
