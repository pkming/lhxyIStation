package android.bluetooth.le;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class ScanResult implements Parcelable {
    public static final Parcelable.Creator<ScanResult> CREATOR = new Parcelable.Creator<ScanResult>() { // from class: android.bluetooth.le.ScanResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScanResult createFromParcel(Parcel parcel) {
            return new ScanResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScanResult[] newArray(int i) {
            return new ScanResult[i];
        }
    };
    private BluetoothDevice mDevice;
    private int mRssi;
    private ScanRecord mScanRecord;
    private long mTimestampNanos;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ScanResult(BluetoothDevice bluetoothDevice, ScanRecord scanRecord, int i, long j) {
        this.mDevice = bluetoothDevice;
        this.mScanRecord = scanRecord;
        this.mRssi = i;
        this.mTimestampNanos = j;
    }

    private ScanResult(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.mDevice != null) {
            parcel.writeInt(1);
            this.mDevice.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        if (this.mScanRecord != null) {
            parcel.writeInt(1);
            parcel.writeByteArray(this.mScanRecord.getBytes());
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.mRssi);
        parcel.writeLong(this.mTimestampNanos);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readInt() == 1) {
            this.mDevice = BluetoothDevice.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() == 1) {
            this.mScanRecord = ScanRecord.parseFromBytes(parcel.createByteArray());
        }
        this.mRssi = parcel.readInt();
        this.mTimestampNanos = parcel.readLong();
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public ScanRecord getScanRecord() {
        return this.mScanRecord;
    }

    public int getRssi() {
        return this.mRssi;
    }

    public long getTimestampNanos() {
        return this.mTimestampNanos;
    }

    public int hashCode() {
        return Objects.hash(this.mDevice, Integer.valueOf(this.mRssi), this.mScanRecord, Long.valueOf(this.mTimestampNanos));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ScanResult scanResult = (ScanResult) obj;
        return Objects.equals(this.mDevice, scanResult.mDevice) && this.mRssi == scanResult.mRssi && Objects.equals(this.mScanRecord, scanResult.mScanRecord) && this.mTimestampNanos == scanResult.mTimestampNanos;
    }

    public String toString() {
        return "ScanResult{mDevice=" + this.mDevice + ", mScanRecord=" + Objects.toString(this.mScanRecord) + ", mRssi=" + this.mRssi + ", mTimestampNanos=" + this.mTimestampNanos + '}';
    }
}
