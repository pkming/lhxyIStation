package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Collection;

/* JADX INFO: loaded from: classes.dex */
public class RouteInfo implements Parcelable {
    public static final Parcelable.Creator<RouteInfo> CREATOR = new Parcelable.Creator<RouteInfo>() { // from class: android.net.RouteInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        /* JADX WARN: Removed duplicated region for block: B:11:0x001d  */
        /* JADX WARN: Removed duplicated region for block: B:14:0x0026  */
        /* JADX WARN: Removed duplicated region for block: B:17:0x002d  */
        @Override // android.os.Parcelable.Creator
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public android.net.RouteInfo createFromParcel(android.os.Parcel r6) {
            /*
                r5 = this;
                byte r0 = r6.readByte()
                r1 = 1
                r2 = 0
                if (r0 != r1) goto L15
                byte[] r0 = r6.createByteArray()
                int r3 = r6.readInt()
                java.net.InetAddress r0 = java.net.InetAddress.getByAddress(r0)     // Catch: java.net.UnknownHostException -> L16
                goto L17
            L15:
                r3 = 0
            L16:
                r0 = r2
            L17:
                byte r4 = r6.readByte()
                if (r4 != r1) goto L26
                byte[] r1 = r6.createByteArray()
                java.net.InetAddress r1 = java.net.InetAddress.getByAddress(r1)     // Catch: java.net.UnknownHostException -> L26
                goto L27
            L26:
                r1 = r2
            L27:
                java.lang.String r6 = r6.readString()
                if (r0 == 0) goto L32
                android.net.LinkAddress r2 = new android.net.LinkAddress
                r2.<init>(r0, r3)
            L32:
                android.net.RouteInfo r0 = new android.net.RouteInfo
                r0.<init>(r2, r1, r6)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.RouteInfo.AnonymousClass1.createFromParcel(android.os.Parcel):android.net.RouteInfo");
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RouteInfo[] newArray(int i) {
            return new RouteInfo[i];
        }
    };
    private final LinkAddress mDestination;
    private final InetAddress mGateway;
    private final boolean mHasGateway;
    private final String mInterface;
    private final boolean mIsDefault;
    private final boolean mIsHost;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public RouteInfo(LinkAddress linkAddress, InetAddress inetAddress, String str) {
        if (linkAddress == null) {
            if (inetAddress != null) {
                if (inetAddress instanceof Inet4Address) {
                    linkAddress = new LinkAddress(Inet4Address.ANY, 0);
                } else {
                    linkAddress = new LinkAddress(Inet6Address.ANY, 0);
                }
            } else {
                throw new IllegalArgumentException("Invalid arguments passed in: " + inetAddress + "," + linkAddress);
            }
        }
        if (inetAddress == null) {
            if (linkAddress.getAddress() instanceof Inet4Address) {
                inetAddress = Inet4Address.ANY;
            } else {
                inetAddress = Inet6Address.ANY;
            }
        }
        this.mHasGateway = !inetAddress.isAnyLocalAddress();
        this.mDestination = new LinkAddress(NetworkUtils.getNetworkPart(linkAddress.getAddress(), linkAddress.getNetworkPrefixLength()), linkAddress.getNetworkPrefixLength());
        this.mGateway = inetAddress;
        this.mInterface = str;
        this.mIsDefault = isDefault();
        this.mIsHost = isHost();
    }

    public RouteInfo(LinkAddress linkAddress, InetAddress inetAddress) {
        this(linkAddress, inetAddress, null);
    }

    public RouteInfo(InetAddress inetAddress) {
        this(null, inetAddress, null);
    }

    public RouteInfo(LinkAddress linkAddress) {
        this(linkAddress, null, null);
    }

    public static RouteInfo makeHostRoute(InetAddress inetAddress, String str) {
        return makeHostRoute(inetAddress, null, str);
    }

    public static RouteInfo makeHostRoute(InetAddress inetAddress, InetAddress inetAddress2, String str) {
        if (inetAddress == null) {
            return null;
        }
        if (inetAddress instanceof Inet4Address) {
            return new RouteInfo(new LinkAddress(inetAddress, 32), inetAddress2, str);
        }
        return new RouteInfo(new LinkAddress(inetAddress, 128), inetAddress2, str);
    }

    private boolean isHost() {
        return ((this.mDestination.getAddress() instanceof Inet4Address) && this.mDestination.getNetworkPrefixLength() == 32) || ((this.mDestination.getAddress() instanceof Inet6Address) && this.mDestination.getNetworkPrefixLength() == 128);
    }

    private boolean isDefault() {
        InetAddress inetAddress = this.mGateway;
        if (inetAddress == null) {
            return false;
        }
        if (inetAddress instanceof Inet4Address) {
            LinkAddress linkAddress = this.mDestination;
            if (linkAddress != null && linkAddress.getNetworkPrefixLength() != 0) {
                return false;
            }
        } else {
            LinkAddress linkAddress2 = this.mDestination;
            if (linkAddress2 != null && linkAddress2.getNetworkPrefixLength() != 0) {
                return false;
            }
        }
        return true;
    }

    public LinkAddress getDestination() {
        return this.mDestination;
    }

    public InetAddress getGateway() {
        return this.mGateway;
    }

    public String getInterface() {
        return this.mInterface;
    }

    public boolean isDefaultRoute() {
        return this.mIsDefault;
    }

    public boolean isHostRoute() {
        return this.mIsHost;
    }

    public boolean hasGateway() {
        return this.mHasGateway;
    }

    public String toString() {
        LinkAddress linkAddress = this.mDestination;
        String string = linkAddress != null ? linkAddress.toString() : "";
        return this.mGateway != null ? string + " -> " + this.mGateway.getHostAddress() : string;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.mDestination == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeByteArray(this.mDestination.getAddress().getAddress());
            parcel.writeInt(this.mDestination.getNetworkPrefixLength());
        }
        if (this.mGateway == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeByteArray(this.mGateway.getAddress());
        }
        parcel.writeString(this.mInterface);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RouteInfo)) {
            return false;
        }
        RouteInfo routeInfo = (RouteInfo) obj;
        LinkAddress linkAddress = this.mDestination;
        boolean zEquals = linkAddress == null ? routeInfo.getDestination() == null : linkAddress.equals(routeInfo.getDestination());
        InetAddress inetAddress = this.mGateway;
        boolean zEquals2 = inetAddress == null ? routeInfo.getGateway() == null : inetAddress.equals(routeInfo.getGateway());
        String str = this.mInterface;
        return zEquals && zEquals2 && (str == null ? routeInfo.getInterface() == null : str.equals(routeInfo.getInterface())) && this.mIsDefault == routeInfo.mIsDefault;
    }

    public int hashCode() {
        LinkAddress linkAddress = this.mDestination;
        int iHashCode = linkAddress == null ? 0 : linkAddress.hashCode() * 41;
        InetAddress inetAddress = this.mGateway;
        int iHashCode2 = iHashCode + (inetAddress == null ? 0 : inetAddress.hashCode() * 47);
        String str = this.mInterface;
        return iHashCode2 + (str != null ? str.hashCode() * 67 : 0) + (this.mIsDefault ? 3 : 7);
    }

    protected boolean matches(InetAddress inetAddress) {
        if (inetAddress == null) {
            return false;
        }
        return this.mDestination.getAddress().equals(NetworkUtils.getNetworkPart(inetAddress, this.mDestination.getNetworkPrefixLength()));
    }

    public static RouteInfo selectBestRoute(Collection<RouteInfo> collection, InetAddress inetAddress) {
        RouteInfo routeInfo = null;
        if (collection != null && inetAddress != null) {
            for (RouteInfo routeInfo2 : collection) {
                if (NetworkUtils.addressTypeMatches(routeInfo2.mDestination.getAddress(), inetAddress) && (routeInfo == null || routeInfo.mDestination.getNetworkPrefixLength() < routeInfo2.mDestination.getNetworkPrefixLength())) {
                    if (routeInfo2.matches(inetAddress)) {
                        routeInfo = routeInfo2;
                    }
                }
            }
        }
        return routeInfo;
    }
}
