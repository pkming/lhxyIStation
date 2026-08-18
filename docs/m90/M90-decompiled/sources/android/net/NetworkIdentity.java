package android.net;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.android.internal.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class NetworkIdentity {
    public static final boolean COMBINE_SUBTYPE_ENABLED = true;
    public static final int SUBTYPE_COMBINED = -1;
    final String mNetworkId;
    final boolean mRoaming;
    final int mSubType = -1;
    final String mSubscriberId;
    final int mType;

    public NetworkIdentity(int i, int i2, String str, String str2, boolean z) {
        this.mType = i;
        this.mSubscriberId = str;
        this.mNetworkId = str2;
        this.mRoaming = z;
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{Integer.valueOf(this.mType), Integer.valueOf(this.mSubType), this.mSubscriberId, this.mNetworkId, Boolean.valueOf(this.mRoaming)});
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NetworkIdentity)) {
            return false;
        }
        NetworkIdentity networkIdentity = (NetworkIdentity) obj;
        return this.mType == networkIdentity.mType && this.mSubType == networkIdentity.mSubType && this.mRoaming == networkIdentity.mRoaming && Objects.equal(this.mSubscriberId, networkIdentity.mSubscriberId) && Objects.equal(this.mNetworkId, networkIdentity.mNetworkId);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append("type=").append(ConnectivityManager.getNetworkTypeName(this.mType));
        sb.append(", subType=");
        sb.append("COMBINED");
        if (this.mSubscriberId != null) {
            sb.append(", subscriberId=").append(scrubSubscriberId(this.mSubscriberId));
        }
        if (this.mNetworkId != null) {
            sb.append(", networkId=").append(this.mNetworkId);
        }
        if (this.mRoaming) {
            sb.append(", ROAMING");
        }
        return sb.append("]").toString();
    }

    public int getType() {
        return this.mType;
    }

    public int getSubType() {
        return this.mSubType;
    }

    public String getSubscriberId() {
        return this.mSubscriberId;
    }

    public String getNetworkId() {
        return this.mNetworkId;
    }

    public boolean getRoaming() {
        return this.mRoaming;
    }

    public static String scrubSubscriberId(String str) {
        return "eng".equals(Build.TYPE) ? str : str != null ? str.substring(0, Math.min(6, str.length())) + "..." : "null";
    }

    public static NetworkIdentity buildNetworkIdentity(Context context, NetworkState networkState) {
        String str;
        boolean z;
        String str2;
        String ssid;
        String subscriberId;
        int type = networkState.networkInfo.getType();
        int subtype = networkState.networkInfo.getSubtype();
        if (ConnectivityManager.isNetworkTypeMobile(type)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            boolean zIsNetworkRoaming = telephonyManager.isNetworkRoaming();
            if (networkState.subscriberId != null) {
                subscriberId = networkState.subscriberId;
            } else {
                subscriberId = telephonyManager.getSubscriberId();
            }
            str2 = subscriberId;
            z = zIsNetworkRoaming;
            str = null;
        } else if (type == 1) {
            if (networkState.networkId != null) {
                ssid = networkState.networkId;
            } else {
                WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
                ssid = connectionInfo != null ? connectionInfo.getSSID() : null;
            }
            str = ssid;
            z = false;
            str2 = null;
        } else {
            str = null;
            z = false;
            str2 = null;
        }
        return new NetworkIdentity(type, subtype, str2, str, z);
    }
}
