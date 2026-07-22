package android.net.wifi;

import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.autonavi.base.ae.gmap.glanimation.AbstractAdglAnimation;
import com.softwinner.utils.Config;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class WifiInfo implements Parcelable {
    public static final Parcelable.Creator<WifiInfo> CREATOR;
    public static final String LINK_SPEED_UNITS = "Mbps";
    private static final String TAG = "WifiInfo";
    private static final EnumMap<SupplicantState, NetworkInfo.DetailedState> stateMap;
    private String mBSSID;
    private boolean mHiddenSSID;
    private InetAddress mIpAddress;
    private int mLinkSpeed;
    private String mMacAddress;
    private boolean mMeteredHint;
    private int mNetworkId;
    private int mRssi;
    private SupplicantState mSupplicantState;
    private WifiSsid mWifiSsid;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    static {
        EnumMap<SupplicantState, NetworkInfo.DetailedState> enumMap = new EnumMap<>(SupplicantState.class);
        stateMap = enumMap;
        enumMap.put(SupplicantState.DISCONNECTED, NetworkInfo.DetailedState.DISCONNECTED);
        enumMap.put(SupplicantState.INTERFACE_DISABLED, NetworkInfo.DetailedState.DISCONNECTED);
        enumMap.put(SupplicantState.INACTIVE, NetworkInfo.DetailedState.IDLE);
        enumMap.put(SupplicantState.SCANNING, NetworkInfo.DetailedState.SCANNING);
        enumMap.put(SupplicantState.AUTHENTICATING, NetworkInfo.DetailedState.CONNECTING);
        enumMap.put(SupplicantState.ASSOCIATING, NetworkInfo.DetailedState.CONNECTING);
        enumMap.put(SupplicantState.ASSOCIATED, NetworkInfo.DetailedState.CONNECTING);
        enumMap.put(SupplicantState.FOUR_WAY_HANDSHAKE, NetworkInfo.DetailedState.AUTHENTICATING);
        enumMap.put(SupplicantState.GROUP_HANDSHAKE, NetworkInfo.DetailedState.AUTHENTICATING);
        enumMap.put(SupplicantState.COMPLETED, NetworkInfo.DetailedState.OBTAINING_IPADDR);
        enumMap.put(SupplicantState.DORMANT, NetworkInfo.DetailedState.DISCONNECTED);
        enumMap.put(SupplicantState.UNINITIALIZED, NetworkInfo.DetailedState.IDLE);
        enumMap.put(SupplicantState.INVALID, NetworkInfo.DetailedState.FAILED);
        CREATOR = new Parcelable.Creator<WifiInfo>() { // from class: android.net.wifi.WifiInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public WifiInfo createFromParcel(Parcel parcel) {
                WifiInfo wifiInfo = new WifiInfo();
                wifiInfo.setNetworkId(parcel.readInt());
                wifiInfo.setRssi(parcel.readInt());
                wifiInfo.setLinkSpeed(parcel.readInt());
                if (parcel.readByte() == 1) {
                    try {
                        wifiInfo.setInetAddress(InetAddress.getByAddress(parcel.createByteArray()));
                    } catch (UnknownHostException unused) {
                    }
                }
                if (parcel.readInt() == 1) {
                    wifiInfo.mWifiSsid = WifiSsid.CREATOR.createFromParcel(parcel);
                }
                wifiInfo.mBSSID = parcel.readString();
                wifiInfo.mMacAddress = parcel.readString();
                wifiInfo.mMeteredHint = parcel.readInt() != 0;
                wifiInfo.mSupplicantState = SupplicantState.CREATOR.createFromParcel(parcel);
                return wifiInfo;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public WifiInfo[] newArray(int i) {
                return new WifiInfo[i];
            }
        };
    }

    WifiInfo() {
        this.mWifiSsid = null;
        this.mBSSID = null;
        this.mNetworkId = -1;
        this.mSupplicantState = SupplicantState.UNINITIALIZED;
        this.mRssi = AbstractAdglAnimation.INVALIDE_VALUE;
        this.mLinkSpeed = -1;
        this.mHiddenSSID = false;
    }

    public WifiInfo(WifiInfo wifiInfo) {
        if (wifiInfo != null) {
            this.mSupplicantState = wifiInfo.mSupplicantState;
            this.mBSSID = wifiInfo.mBSSID;
            this.mWifiSsid = wifiInfo.mWifiSsid;
            this.mNetworkId = wifiInfo.mNetworkId;
            this.mHiddenSSID = wifiInfo.mHiddenSSID;
            this.mRssi = wifiInfo.mRssi;
            this.mLinkSpeed = wifiInfo.mLinkSpeed;
            this.mIpAddress = wifiInfo.mIpAddress;
            this.mMacAddress = wifiInfo.mMacAddress;
            this.mMeteredHint = wifiInfo.mMeteredHint;
        }
    }

    void setSSID(WifiSsid wifiSsid) {
        this.mWifiSsid = wifiSsid;
        this.mHiddenSSID = false;
    }

    public String getSSID() {
        WifiSsid wifiSsid = this.mWifiSsid;
        if (wifiSsid != null) {
            String string = wifiSsid.toString();
            if (!TextUtils.isEmpty(string)) {
                return "\"" + string + "\"";
            }
            if (Config.getTargetPlatform(1) != 8) {
                return this.mWifiSsid.getHexString();
            }
            return null;
        }
        if (Config.getTargetPlatform(1) != 8) {
            return WifiSsid.NONE;
        }
        return null;
    }

    public WifiSsid getWifiSsid() {
        return this.mWifiSsid;
    }

    void setBSSID(String str) {
        this.mBSSID = str;
    }

    public String getBSSID() {
        return this.mBSSID;
    }

    public int getRssi() {
        return this.mRssi;
    }

    void setRssi(int i) {
        this.mRssi = i;
    }

    public int getLinkSpeed() {
        return this.mLinkSpeed;
    }

    void setLinkSpeed(int i) {
        this.mLinkSpeed = i;
    }

    void setMacAddress(String str) {
        this.mMacAddress = str;
    }

    public String getMacAddress() {
        return this.mMacAddress;
    }

    public void setMeteredHint(boolean z) {
        this.mMeteredHint = z;
    }

    public boolean getMeteredHint() {
        return this.mMeteredHint;
    }

    void setNetworkId(int i) {
        this.mNetworkId = i;
    }

    public int getNetworkId() {
        return this.mNetworkId;
    }

    public SupplicantState getSupplicantState() {
        return this.mSupplicantState;
    }

    void setSupplicantState(SupplicantState supplicantState) {
        this.mSupplicantState = supplicantState;
    }

    void setInetAddress(InetAddress inetAddress) {
        this.mIpAddress = inetAddress;
    }

    public int getIpAddress() {
        InetAddress inetAddress = this.mIpAddress;
        if (inetAddress instanceof Inet4Address) {
            return NetworkUtils.inetAddressToInt((Inet4Address) inetAddress);
        }
        return 0;
    }

    public boolean getHiddenSSID() {
        return this.mHiddenSSID;
    }

    public void setHiddenSSID(boolean z) {
        this.mHiddenSSID = z;
    }

    public static NetworkInfo.DetailedState getDetailedStateOf(SupplicantState supplicantState) {
        return stateMap.get(supplicantState);
    }

    void setSupplicantState(String str) {
        this.mSupplicantState = valueOf(str);
    }

    static SupplicantState valueOf(String str) {
        if ("4WAY_HANDSHAKE".equalsIgnoreCase(str)) {
            return SupplicantState.FOUR_WAY_HANDSHAKE;
        }
        try {
            return SupplicantState.valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException unused) {
            return SupplicantState.INVALID;
        }
    }

    public static String removeDoubleQuotes(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length <= 1 || str.charAt(0) != '\"') {
            return str;
        }
        int i = length - 1;
        return str.charAt(i) == '\"' ? str.substring(1, i) : str;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBufferAppend = stringBuffer.append("SSID: ");
        Object obj = this.mWifiSsid;
        if (obj == null) {
            obj = WifiSsid.NONE;
        }
        StringBuffer stringBufferAppend2 = stringBufferAppend.append(obj).append(", BSSID: ");
        String str = this.mBSSID;
        if (str == null) {
            str = "<none>";
        }
        StringBuffer stringBufferAppend3 = stringBufferAppend2.append(str).append(", MAC: ");
        String str2 = this.mMacAddress;
        if (str2 == null) {
            str2 = "<none>";
        }
        StringBuffer stringBufferAppend4 = stringBufferAppend3.append(str2).append(", Supplicant state: ");
        SupplicantState supplicantState = this.mSupplicantState;
        stringBufferAppend4.append(supplicantState != null ? supplicantState : "<none>").append(", RSSI: ").append(this.mRssi).append(", Link speed: ").append(this.mLinkSpeed).append(", Net ID: ").append(this.mNetworkId).append(", Metered hint: ").append(this.mMeteredHint);
        return stringBuffer.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mNetworkId);
        parcel.writeInt(this.mRssi);
        parcel.writeInt(this.mLinkSpeed);
        if (this.mIpAddress != null) {
            parcel.writeByte((byte) 1);
            parcel.writeByteArray(this.mIpAddress.getAddress());
        } else {
            parcel.writeByte((byte) 0);
        }
        if (this.mWifiSsid != null) {
            parcel.writeInt(1);
            this.mWifiSsid.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeString(this.mBSSID);
        parcel.writeString(this.mMacAddress);
        parcel.writeInt(this.mMeteredHint ? 1 : 0);
        this.mSupplicantState.writeToParcel(parcel, i);
    }
}
