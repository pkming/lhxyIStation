package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class ProxyProperties implements Parcelable {
    public static final Parcelable.Creator<ProxyProperties> CREATOR = new Parcelable.Creator<ProxyProperties>() { // from class: android.net.ProxyProperties.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProxyProperties createFromParcel(Parcel parcel) {
            String string;
            int i;
            if (parcel.readByte() != 0) {
                return new ProxyProperties(parcel.readString(), parcel.readInt());
            }
            if (parcel.readByte() != 0) {
                string = parcel.readString();
                i = parcel.readInt();
            } else {
                string = null;
                i = 0;
            }
            return new ProxyProperties(string, i, parcel.readString(), parcel.readStringArray());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProxyProperties[] newArray(int i) {
            return new ProxyProperties[i];
        }
    };
    public static final String LOCAL_EXCL_LIST = "";
    public static final String LOCAL_HOST = "localhost";
    public static final int LOCAL_PORT = -1;
    private String mExclusionList;
    private String mHost;
    private String mPacFileUrl;
    private String[] mParsedExclusionList;
    private int mPort;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ProxyProperties(String str, int i, String str2) {
        this.mHost = str;
        this.mPort = i;
        setExclusionList(str2);
    }

    public ProxyProperties(String str) {
        this.mHost = "localhost";
        this.mPort = -1;
        setExclusionList("");
        this.mPacFileUrl = str;
    }

    public ProxyProperties(String str, int i) {
        this.mHost = "localhost";
        this.mPort = i;
        setExclusionList("");
        this.mPacFileUrl = str;
    }

    private ProxyProperties(String str, int i, String str2, String[] strArr) {
        this.mHost = str;
        this.mPort = i;
        this.mExclusionList = str2;
        this.mParsedExclusionList = strArr;
        this.mPacFileUrl = null;
    }

    public ProxyProperties(ProxyProperties proxyProperties) {
        if (proxyProperties != null) {
            this.mHost = proxyProperties.getHost();
            this.mPort = proxyProperties.getPort();
            this.mPacFileUrl = proxyProperties.getPacFileUrl();
            this.mExclusionList = proxyProperties.getExclusionList();
            this.mParsedExclusionList = proxyProperties.mParsedExclusionList;
        }
    }

    public InetSocketAddress getSocketAddress() {
        try {
            return new InetSocketAddress(this.mHost, this.mPort);
        } catch (IllegalArgumentException unused) {
            return null;
        }
    }

    public String getPacFileUrl() {
        return this.mPacFileUrl;
    }

    public String getHost() {
        return this.mHost;
    }

    public int getPort() {
        return this.mPort;
    }

    public String getExclusionList() {
        return this.mExclusionList;
    }

    private void setExclusionList(String str) {
        this.mExclusionList = str;
        if (str == null) {
            this.mParsedExclusionList = new String[0];
            return;
        }
        String[] strArrSplit = str.toLowerCase(Locale.ROOT).split(",");
        this.mParsedExclusionList = new String[strArrSplit.length * 2];
        for (int i = 0; i < strArrSplit.length; i++) {
            String strTrim = strArrSplit[i].trim();
            if (strTrim.startsWith(".")) {
                strTrim = strTrim.substring(1);
            }
            String[] strArr = this.mParsedExclusionList;
            int i2 = i * 2;
            strArr[i2] = strTrim;
            strArr[i2 + 1] = "." + strTrim;
        }
    }

    public boolean isExcluded(String str) {
        String[] strArr;
        String host;
        if (TextUtils.isEmpty(str) || (strArr = this.mParsedExclusionList) == null || strArr.length == 0 || (host = Uri.parse(str).getHost()) == null) {
            return false;
        }
        int i = 0;
        while (true) {
            String[] strArr2 = this.mParsedExclusionList;
            if (i >= strArr2.length) {
                break;
            }
            if (host.equals(strArr2[i]) || host.endsWith(this.mParsedExclusionList[i + 1])) {
                return true;
            }
            i += 2;
        }
        return false;
    }

    public boolean isValid() {
        if (!TextUtils.isEmpty(this.mPacFileUrl)) {
            return true;
        }
        try {
            String str = this.mHost;
            String str2 = "";
            if (str == null) {
                str = "";
            }
            int i = this.mPort;
            String string = i == 0 ? "" : Integer.toString(i);
            String str3 = this.mExclusionList;
            if (str3 != null) {
                str2 = str3;
            }
            Proxy.validate(str, string, str2);
            return true;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    public java.net.Proxy makeProxy() {
        java.net.Proxy proxy = java.net.Proxy.NO_PROXY;
        if (this.mHost == null) {
            return proxy;
        }
        try {
            return new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.mHost, this.mPort));
        } catch (IllegalArgumentException unused) {
            return proxy;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.mPacFileUrl != null) {
            sb.append("PAC Script: ");
            sb.append(this.mPacFileUrl);
        } else if (this.mHost != null) {
            sb.append("[");
            sb.append(this.mHost);
            sb.append("] ");
            sb.append(Integer.toString(this.mPort));
            if (this.mExclusionList != null) {
                sb.append(" xl=").append(this.mExclusionList);
            }
        } else {
            sb.append("[ProxyProperties.mHost == null]");
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ProxyProperties)) {
            return false;
        }
        ProxyProperties proxyProperties = (ProxyProperties) obj;
        if (!TextUtils.isEmpty(this.mPacFileUrl)) {
            return this.mPacFileUrl.equals(proxyProperties.getPacFileUrl()) && this.mPort == proxyProperties.mPort;
        }
        if (!TextUtils.isEmpty(proxyProperties.getPacFileUrl())) {
            return false;
        }
        String str = this.mExclusionList;
        if (str != null && !str.equals(proxyProperties.getExclusionList())) {
            return false;
        }
        if (this.mHost != null && proxyProperties.getHost() != null && !this.mHost.equals(proxyProperties.getHost())) {
            return false;
        }
        String str2 = this.mHost;
        if (str2 == null || proxyProperties.mHost != null) {
            return (str2 != null || proxyProperties.mHost == null) && this.mPort == proxyProperties.mPort;
        }
        return false;
    }

    public int hashCode() {
        String str = this.mHost;
        int iHashCode = str == null ? 0 : str.hashCode();
        String str2 = this.mExclusionList;
        return iHashCode + (str2 != null ? str2.hashCode() : 0) + this.mPort;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.mPacFileUrl != null) {
            parcel.writeByte((byte) 1);
            parcel.writeString(this.mPacFileUrl);
            parcel.writeInt(this.mPort);
            return;
        }
        parcel.writeByte((byte) 0);
        if (this.mHost != null) {
            parcel.writeByte((byte) 1);
            parcel.writeString(this.mHost);
            parcel.writeInt(this.mPort);
        } else {
            parcel.writeByte((byte) 0);
        }
        parcel.writeString(this.mExclusionList);
        parcel.writeStringArray(this.mParsedExclusionList);
    }
}
