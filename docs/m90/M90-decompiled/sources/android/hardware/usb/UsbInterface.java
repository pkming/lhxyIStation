package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class UsbInterface implements Parcelable {
    public static final Parcelable.Creator<UsbInterface> CREATOR = new Parcelable.Creator<UsbInterface>() { // from class: android.hardware.usb.UsbInterface.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UsbInterface createFromParcel(Parcel parcel) {
            return new UsbInterface(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readParcelableArray(UsbEndpoint.class.getClassLoader()));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UsbInterface[] newArray(int i) {
            return new UsbInterface[i];
        }
    };
    private final int mClass;
    private final Parcelable[] mEndpoints;
    private final int mId;
    private final int mProtocol;
    private final int mSubclass;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public UsbInterface(int i, int i2, int i3, int i4, Parcelable[] parcelableArr) {
        this.mId = i;
        this.mClass = i2;
        this.mSubclass = i3;
        this.mProtocol = i4;
        this.mEndpoints = parcelableArr;
    }

    public int getId() {
        return this.mId;
    }

    public int getInterfaceClass() {
        return this.mClass;
    }

    public int getInterfaceSubclass() {
        return this.mSubclass;
    }

    public int getInterfaceProtocol() {
        return this.mProtocol;
    }

    public int getEndpointCount() {
        return this.mEndpoints.length;
    }

    public UsbEndpoint getEndpoint(int i) {
        return (UsbEndpoint) this.mEndpoints[i];
    }

    public String toString() {
        return "UsbInterface[mId=" + this.mId + ",mClass=" + this.mClass + ",mSubclass=" + this.mSubclass + ",mProtocol=" + this.mProtocol + ",mEndpoints=" + this.mEndpoints + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mId);
        parcel.writeInt(this.mClass);
        parcel.writeInt(this.mSubclass);
        parcel.writeInt(this.mProtocol);
        parcel.writeParcelableArray(this.mEndpoints, 0);
    }
}
