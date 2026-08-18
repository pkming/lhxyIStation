package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class UsbDevice implements Parcelable {
    public static final Parcelable.Creator<UsbDevice> CREATOR = new Parcelable.Creator<UsbDevice>() { // from class: android.hardware.usb.UsbDevice.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UsbDevice createFromParcel(Parcel parcel) {
            return new UsbDevice(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readParcelableArray(UsbInterface.class.getClassLoader()));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UsbDevice[] newArray(int i) {
            return new UsbDevice[i];
        }
    };
    private static final String TAG = "UsbDevice";
    private final int mClass;
    private final Parcelable[] mInterfaces;
    private final String mName;
    private final int mProductId;
    private final int mProtocol;
    private final int mSubclass;
    private final int mVendorId;

    private static native int native_get_device_id(String str);

    private static native String native_get_device_name(int i);

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public UsbDevice(String str, int i, int i2, int i3, int i4, int i5, Parcelable[] parcelableArr) {
        this.mName = str;
        this.mVendorId = i;
        this.mProductId = i2;
        this.mClass = i3;
        this.mSubclass = i4;
        this.mProtocol = i5;
        this.mInterfaces = parcelableArr;
    }

    public String getDeviceName() {
        return this.mName;
    }

    public int getDeviceId() {
        return getDeviceId(this.mName);
    }

    public int getVendorId() {
        return this.mVendorId;
    }

    public int getProductId() {
        return this.mProductId;
    }

    public int getDeviceClass() {
        return this.mClass;
    }

    public int getDeviceSubclass() {
        return this.mSubclass;
    }

    public int getDeviceProtocol() {
        return this.mProtocol;
    }

    public int getInterfaceCount() {
        return this.mInterfaces.length;
    }

    public UsbInterface getInterface(int i) {
        return (UsbInterface) this.mInterfaces[i];
    }

    public boolean equals(Object obj) {
        if (obj instanceof UsbDevice) {
            return ((UsbDevice) obj).mName.equals(this.mName);
        }
        if (obj instanceof String) {
            return ((String) obj).equals(this.mName);
        }
        return false;
    }

    public int hashCode() {
        return this.mName.hashCode();
    }

    public String toString() {
        return "UsbDevice[mName=" + this.mName + ",mVendorId=" + this.mVendorId + ",mProductId=" + this.mProductId + ",mClass=" + this.mClass + ",mSubclass=" + this.mSubclass + ",mProtocol=" + this.mProtocol + ",mInterfaces=" + this.mInterfaces + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mName);
        parcel.writeInt(this.mVendorId);
        parcel.writeInt(this.mProductId);
        parcel.writeInt(this.mClass);
        parcel.writeInt(this.mSubclass);
        parcel.writeInt(this.mProtocol);
        parcel.writeParcelableArray(this.mInterfaces, 0);
    }

    public static int getDeviceId(String str) {
        return native_get_device_id(str);
    }

    public static String getDeviceName(int i) {
        return native_get_device_name(i);
    }
}
