package android.net.ethernet;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class EthernetDevInfo implements Parcelable {
    public static final Parcelable.Creator<EthernetDevInfo> CREATOR = new Parcelable.Creator<EthernetDevInfo>() { // from class: android.net.ethernet.EthernetDevInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EthernetDevInfo createFromParcel(Parcel parcel) {
            EthernetDevInfo ethernetDevInfo = new EthernetDevInfo();
            ethernetDevInfo.setIfName(parcel.readString());
            ethernetDevInfo.setHwaddr(parcel.readString());
            ethernetDevInfo.setIpAddress(parcel.readString());
            ethernetDevInfo.setGateWay(parcel.readString());
            ethernetDevInfo.setNetMask(parcel.readString());
            ethernetDevInfo.setDns1(parcel.readString());
            ethernetDevInfo.setDns2(parcel.readString());
            ethernetDevInfo.setMode(parcel.readString());
            ethernetDevInfo.setPasswd(parcel.readString());
            ethernetDevInfo.setUsername(parcel.readString());
            ethernetDevInfo.setRouteAddr(parcel.readString());
            ethernetDevInfo.setProxyOn(parcel.readInt() == 1);
            ethernetDevInfo.setProxyHost(parcel.readString());
            ethernetDevInfo.setProxyPort(parcel.readString());
            return ethernetDevInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EthernetDevInfo[] newArray(int i) {
            return new EthernetDevInfo[i];
        }
    };
    public static final int ETHERNET_CONN_MODE_DHCP = 1;
    public static final int ETHERNET_CONN_MODE_MANUAL = 0;
    private String route;
    private String ifacename = null;
    private String hwaddr = null;
    private String ipaddr = null;
    private String gateway = null;
    private String netmask = null;
    private String dns1 = null;
    private String dns2 = null;
    private String mode = EthernetManager.ETHERNET_CONNECT_MODE_DHCP;
    private String username = null;
    private String passwd = null;
    private int proxy_on = 0;
    private String proxy_host = null;
    private String proxy_port = null;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void setIfName(String str) {
        this.ifacename = str;
    }

    public String getIfName() {
        return this.ifacename;
    }

    public void setHwaddr(String str) {
        this.hwaddr = str;
    }

    public String getHwaddr() {
        return this.hwaddr;
    }

    public String getMacAddress() {
        return getHwaddr();
    }

    public void setIpAddress(String str) {
        this.ipaddr = str;
    }

    public String getIpAddress() {
        return this.ipaddr;
    }

    public void setGateWay(String str) {
        this.gateway = str;
    }

    public String getGateWay() {
        return this.gateway;
    }

    public void setNetMask(String str) {
        this.netmask = str;
    }

    public String getNetMask() {
        return this.netmask;
    }

    public void setDns1(String str) {
        this.dns1 = str;
    }

    public String getDns1() {
        return this.dns1;
    }

    public void setDnsAddr(String str) {
        setDns1(str);
    }

    public String getDnsAddr() {
        return getDns1();
    }

    public void setDns2(String str) {
        this.dns2 = str;
    }

    public String getDns2() {
        return this.dns2;
    }

    public void setDns2Addr(String str) {
        setDns2(str);
    }

    public String getDns2Addr() {
        return getDns2();
    }

    public void setMode(String str) {
        this.mode = str;
    }

    public String getMode() {
        return this.mode;
    }

    public void setConnectMode(int i) {
        if (i == 1) {
            setMode(EthernetManager.ETHERNET_CONNECT_MODE_DHCP);
        } else if (i == 0) {
            setMode(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL);
        }
    }

    public int getConnectMode() {
        return getMode().equals(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL) ? 0 : 1;
    }

    public void setPasswd(String str) {
        this.passwd = str;
    }

    public String getPasswd() {
        return this.passwd;
    }

    public void setUsername(String str) {
        this.username = str;
    }

    public String getUsername() {
        return this.username;
    }

    public void setRouteAddr(String str) {
        if (str != null) {
            this.route = str;
        }
    }

    public String getRouteAddr() {
        return this.route;
    }

    public void setProxyOn(boolean z) {
        this.proxy_on = z ? 1 : 0;
    }

    public boolean getProxyOn() {
        return this.proxy_on == 1;
    }

    public void setProxyHost(String str) {
        if (str != null) {
            this.proxy_host = str;
        }
    }

    public String getProxyHost() {
        return this.proxy_host;
    }

    public void setProxyPort(String str) {
        if (str != null) {
            this.proxy_port = str;
        }
    }

    public String getProxyPort() {
        return this.proxy_port;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.ifacename);
        parcel.writeString(this.hwaddr);
        parcel.writeString(this.ipaddr);
        parcel.writeString(this.gateway);
        parcel.writeString(this.netmask);
        parcel.writeString(this.dns1);
        parcel.writeString(this.dns2);
        parcel.writeString(this.mode);
        parcel.writeString(this.passwd);
        parcel.writeString(this.username);
    }
}
