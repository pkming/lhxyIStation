package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: classes.dex */
public class LinkAddress implements Parcelable {
    public static final Parcelable.Creator<LinkAddress> CREATOR = new Parcelable.Creator<LinkAddress>() { // from class: android.net.LinkAddress.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkAddress createFromParcel(Parcel parcel) {
            InetAddress byAddress = null;
            int i = 0;
            if (parcel.readByte() == 1) {
                try {
                    byAddress = InetAddress.getByAddress(parcel.createByteArray());
                    i = parcel.readInt();
                } catch (UnknownHostException unused) {
                }
            }
            return new LinkAddress(byAddress, i);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkAddress[] newArray(int i) {
            return new LinkAddress[i];
        }
    };
    private InetAddress address;
    private int prefixLength;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private void init(InetAddress inetAddress, int i) {
        if (inetAddress == null || i < 0 || (((inetAddress instanceof Inet4Address) && i > 32) || i > 128)) {
            throw new IllegalArgumentException("Bad LinkAddress params " + inetAddress + "/" + i);
        }
        this.address = inetAddress;
        this.prefixLength = i;
    }

    public LinkAddress(InetAddress inetAddress, int i) {
        init(inetAddress, i);
    }

    public LinkAddress(InterfaceAddress interfaceAddress) {
        init(interfaceAddress.getAddress(), interfaceAddress.getNetworkPrefixLength());
    }

    public LinkAddress(String str) {
        int i;
        InetAddress numericAddress = null;
        try {
            String[] strArrSplit = str.split("/", 2);
            i = Integer.parseInt(strArrSplit[1]);
            try {
                numericAddress = InetAddress.parseNumericAddress(strArrSplit[0]);
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | NullPointerException | NumberFormatException unused) {
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | NullPointerException | NumberFormatException unused2) {
            i = -1;
        }
        if (numericAddress == null || i == -1) {
            throw new IllegalArgumentException("Bad LinkAddress params " + str);
        }
        init(numericAddress, i);
    }

    public String toString() {
        return this.address == null ? "" : this.address.getHostAddress() + "/" + this.prefixLength;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LinkAddress)) {
            return false;
        }
        LinkAddress linkAddress = (LinkAddress) obj;
        return this.address.equals(linkAddress.address) && this.prefixLength == linkAddress.prefixLength;
    }

    public int hashCode() {
        InetAddress inetAddress = this.address;
        return (inetAddress == null ? 0 : inetAddress.hashCode()) + this.prefixLength;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getNetworkPrefixLength() {
        return this.prefixLength;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.address != null) {
            parcel.writeByte((byte) 1);
            parcel.writeByteArray(this.address.getAddress());
            parcel.writeInt(this.prefixLength);
            return;
        }
        parcel.writeByte((byte) 0);
    }
}
