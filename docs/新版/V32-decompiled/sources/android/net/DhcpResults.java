package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class DhcpResults implements Parcelable {
    public static final Parcelable.Creator<DhcpResults> CREATOR = new Parcelable.Creator<DhcpResults>() { // from class: android.net.DhcpResults.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpResults createFromParcel(Parcel parcel) {
            DhcpResults dhcpResults = new DhcpResults((LinkProperties) parcel.readParcelable(null));
            dhcpResults.leaseDuration = parcel.readInt();
            if (parcel.readByte() == 1) {
                try {
                    dhcpResults.serverAddress = InetAddress.getByAddress(parcel.createByteArray());
                } catch (UnknownHostException unused) {
                }
            }
            dhcpResults.vendorInfo = parcel.readString();
            return dhcpResults;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpResults[] newArray(int i) {
            return new DhcpResults[i];
        }
    };
    private static final String TAG = "DhcpResults";
    public int leaseDuration;
    public final LinkProperties linkProperties;
    public InetAddress serverAddress;
    public String vendorInfo;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DhcpResults() {
        this.linkProperties = new LinkProperties();
    }

    public DhcpResults(DhcpResults dhcpResults) {
        if (dhcpResults != null) {
            this.linkProperties = new LinkProperties(dhcpResults.linkProperties);
            this.serverAddress = dhcpResults.serverAddress;
            this.leaseDuration = dhcpResults.leaseDuration;
            this.vendorInfo = dhcpResults.vendorInfo;
            return;
        }
        this.linkProperties = new LinkProperties();
    }

    public DhcpResults(LinkProperties linkProperties) {
        this.linkProperties = new LinkProperties(linkProperties);
    }

    public void updateFromDhcpRequest(DhcpResults dhcpResults) {
        if (dhcpResults == null || dhcpResults.linkProperties == null) {
            return;
        }
        if (this.linkProperties.getRoutes().size() == 0) {
            Iterator<RouteInfo> it = dhcpResults.linkProperties.getRoutes().iterator();
            while (it.hasNext()) {
                this.linkProperties.addRoute(it.next());
            }
        }
        if (this.linkProperties.getDnses().size() == 0) {
            Iterator<InetAddress> it2 = dhcpResults.linkProperties.getDnses().iterator();
            while (it2.hasNext()) {
                this.linkProperties.addDns(it2.next());
            }
        }
    }

    public boolean hasMeteredHint() {
        String str = this.vendorInfo;
        if (str != null) {
            return str.contains("ANDROID_METERED");
        }
        return false;
    }

    public void clear() {
        this.linkProperties.clear();
        this.serverAddress = null;
        this.vendorInfo = null;
        this.leaseDuration = 0;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(this.linkProperties.toString());
        stringBuffer.append(" DHCP server ").append(this.serverAddress);
        stringBuffer.append(" Vendor info ").append(this.vendorInfo);
        stringBuffer.append(" lease ").append(this.leaseDuration).append(" seconds");
        return stringBuffer.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DhcpResults)) {
            return false;
        }
        DhcpResults dhcpResults = (DhcpResults) obj;
        LinkProperties linkProperties = this.linkProperties;
        if (linkProperties == null) {
            if (dhcpResults.linkProperties != null) {
                return false;
            }
        } else if (!linkProperties.equals(dhcpResults.linkProperties)) {
            return false;
        }
        InetAddress inetAddress = this.serverAddress;
        if (inetAddress == null) {
            if (dhcpResults.serverAddress != null) {
                return false;
            }
        } else if (!inetAddress.equals(dhcpResults.serverAddress)) {
            return false;
        }
        String str = this.vendorInfo;
        if (str == null) {
            if (dhcpResults.vendorInfo != null) {
                return false;
            }
        } else if (!str.equals(dhcpResults.vendorInfo)) {
            return false;
        }
        return this.leaseDuration == dhcpResults.leaseDuration;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.linkProperties.writeToParcel(parcel, i);
        parcel.writeInt(this.leaseDuration);
        if (this.serverAddress != null) {
            parcel.writeByte((byte) 1);
            parcel.writeByteArray(this.serverAddress.getAddress());
        } else {
            parcel.writeByte((byte) 0);
        }
        parcel.writeString(this.vendorInfo);
    }

    public void setInterfaceName(String str) {
        this.linkProperties.setInterfaceName(str);
    }

    public boolean addLinkAddress(String str, int i) {
        try {
            LinkAddress linkAddress = new LinkAddress(NetworkUtils.numericToInetAddress(str), i);
            this.linkProperties.addLinkAddress(linkAddress);
            this.linkProperties.addRoute(new RouteInfo(linkAddress));
            return false;
        } catch (IllegalArgumentException unused) {
            Log.e(TAG, "addLinkAddress failed with addrString " + str);
            return true;
        }
    }

    public boolean addGateway(String str) {
        try {
            this.linkProperties.addRoute(new RouteInfo(NetworkUtils.numericToInetAddress(str)));
            return false;
        } catch (IllegalArgumentException unused) {
            Log.e(TAG, "addGateway failed with addrString " + str);
            return true;
        }
    }

    public boolean addDns(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            this.linkProperties.addDns(NetworkUtils.numericToInetAddress(str));
            return false;
        } catch (IllegalArgumentException unused) {
            Log.e(TAG, "addDns failed with addrString " + str);
            return true;
        }
    }

    public boolean setServerAddress(String str) {
        try {
            this.serverAddress = NetworkUtils.numericToInetAddress(str);
            return false;
        } catch (IllegalArgumentException unused) {
            Log.e(TAG, "setServerAddress failed with addrString " + str);
            return true;
        }
    }

    public void setLeaseDuration(int i) {
        this.leaseDuration = i;
    }

    public void setVendorInfo(String str) {
        this.vendorInfo = str;
    }

    public void setDomains(String str) {
        this.linkProperties.setDomains(str);
    }
}
