package android.net.wifi;

import android.accounts.AccountManager;
import android.net.LinkProperties;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import java.util.BitSet;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;

/* JADX INFO: loaded from: classes.dex */
public class WifiConfiguration implements Parcelable {
    public static final int DISABLED_ASSOCIATION_REJECT = 4;
    public static final int DISABLED_AUTH_FAILURE = 3;
    public static final int DISABLED_DHCP_FAILURE = 2;
    public static final int DISABLED_DNS_FAILURE = 1;
    public static final int DISABLED_UNKNOWN_REASON = 0;
    public static final int INVALID_NETWORK_ID = -1;
    private static final String TAG = "WifiConfiguration";
    public static final String bssidVarName = "bssid";
    public static final String hiddenSSIDVarName = "scan_ssid";
    public static final String priorityVarName = "priority";
    public static final String pskVarName = "psk";
    public static final String ssidVarName = "ssid";
    public static final String wepTxKeyIdxVarName = "wep_tx_keyidx";
    public String BSSID;
    public String SSID;
    public BitSet allowedAuthAlgorithms;
    public BitSet allowedGroupCiphers;
    public BitSet allowedKeyManagement;
    public BitSet allowedPairwiseCiphers;
    public BitSet allowedProtocols;
    public EnterpriseField anonymous_identity;
    public EnterpriseField ca_cert;
    public EnterpriseField client_cert;
    public int disableReason;
    public EnterpriseField eap;
    public EnterpriseField engine;
    public EnterpriseField engine_id;
    public WifiEnterpriseConfig enterpriseConfig;
    public EnterpriseField[] enterpriseFields;
    public boolean hiddenSSID;
    public EnterpriseField identity;
    public IpAssignment ipAssignment;
    public EnterpriseField key_id;
    public LinkProperties linkProperties;
    public int networkId;
    public EnterpriseField password;
    public EnterpriseField phase2;
    public String preSharedKey;
    public int priority;
    public ProxySettings proxySettings;
    public int status;
    public String[] wepKeys;
    public int wepTxKeyIndex;
    public static final String[] wepKeyVarNames = {"wep_key0", "wep_key1", "wep_key2", "wep_key3"};
    public static final Parcelable.Creator<WifiConfiguration> CREATOR = new Parcelable.Creator<WifiConfiguration>() { // from class: android.net.wifi.WifiConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiConfiguration createFromParcel(Parcel parcel) {
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.networkId = parcel.readInt();
            wifiConfiguration.status = parcel.readInt();
            wifiConfiguration.disableReason = parcel.readInt();
            wifiConfiguration.SSID = parcel.readString();
            wifiConfiguration.BSSID = parcel.readString();
            wifiConfiguration.preSharedKey = parcel.readString();
            for (int i = 0; i < wifiConfiguration.wepKeys.length; i++) {
                wifiConfiguration.wepKeys[i] = parcel.readString();
            }
            wifiConfiguration.wepTxKeyIndex = parcel.readInt();
            wifiConfiguration.priority = parcel.readInt();
            wifiConfiguration.hiddenSSID = parcel.readInt() != 0;
            wifiConfiguration.allowedKeyManagement = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.allowedProtocols = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.allowedAuthAlgorithms = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.allowedPairwiseCiphers = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.allowedGroupCiphers = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.enterpriseConfig = (WifiEnterpriseConfig) parcel.readParcelable(null);
            wifiConfiguration.ipAssignment = IpAssignment.valueOf(parcel.readString());
            wifiConfiguration.proxySettings = ProxySettings.valueOf(parcel.readString());
            wifiConfiguration.linkProperties = (LinkProperties) parcel.readParcelable(null);
            return wifiConfiguration;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiConfiguration[] newArray(int i) {
            return new WifiConfiguration[i];
        }
    };
    public boolean isValidAscii = true;
    public String originSsid = null;

    public enum IpAssignment {
        STATIC,
        DHCP,
        UNASSIGNED
    }

    public enum ProxySettings {
        NONE,
        STATIC,
        UNASSIGNED,
        PAC
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public class EnterpriseField {
        private String value;
        private String varName;

        private EnterpriseField(String str) {
            this.varName = str;
            this.value = null;
        }

        public void setValue(String str) {
            this.value = str;
        }

        public String varName() {
            return this.varName;
        }

        public String value() {
            return this.value;
        }
    }

    public static class KeyMgmt {
        public static final int IEEE8021X = 3;
        public static final int NONE = 0;
        public static final int WPA2_PSK = 4;
        public static final int WPA_EAP = 2;
        public static final int WPA_PSK = 1;
        public static final String[] strings = {"NONE", "WPA_PSK", "WPA_EAP", "IEEE8021X", "WPA2_PSK"};
        public static final String varName = "key_mgmt";

        private KeyMgmt() {
        }
    }

    public static class Protocol {
        public static final int RSN = 1;
        public static final int WPA = 0;
        public static final String[] strings = {"WPA", "RSN"};
        public static final String varName = "proto";

        private Protocol() {
        }
    }

    public static class AuthAlgorithm {
        public static final int LEAP = 2;
        public static final int OPEN = 0;
        public static final int SHARED = 1;
        public static final String[] strings = {"OPEN", "SHARED", "LEAP"};
        public static final String varName = "auth_alg";

        private AuthAlgorithm() {
        }
    }

    public static class PairwiseCipher {
        public static final int CCMP = 2;
        public static final int NONE = 0;
        public static final int TKIP = 1;
        public static final String[] strings = {"NONE", "TKIP", "CCMP"};
        public static final String varName = "pairwise";

        private PairwiseCipher() {
        }
    }

    public static class GroupCipher {
        public static final int CCMP = 3;
        public static final int TKIP = 2;
        public static final int WEP104 = 1;
        public static final int WEP40 = 0;
        public static final String[] strings = {"WEP40", "WEP104", "TKIP", "CCMP"};
        public static final String varName = "group";

        private GroupCipher() {
        }
    }

    public static class Status {
        public static final int CURRENT = 0;
        public static final int DISABLED = 1;
        public static final int ENABLED = 2;
        public static final String[] strings = {MSVSSConstants.TIME_CURRENT, "disabled", "enabled"};

        private Status() {
        }
    }

    public WifiConfiguration() {
        this.eap = new EnterpriseField("eap");
        this.phase2 = new EnterpriseField("phase2");
        this.identity = new EnterpriseField("identity");
        this.anonymous_identity = new EnterpriseField("anonymous_identity");
        this.password = new EnterpriseField(AccountManager.KEY_PASSWORD);
        this.client_cert = new EnterpriseField("client_cert");
        this.engine = new EnterpriseField(TextToSpeech.Engine.KEY_PARAM_ENGINE);
        this.engine_id = new EnterpriseField("engine_id");
        this.key_id = new EnterpriseField("key_id");
        EnterpriseField enterpriseField = new EnterpriseField("ca_cert");
        this.ca_cert = enterpriseField;
        int i = 0;
        this.enterpriseFields = new EnterpriseField[]{this.eap, this.phase2, this.identity, this.anonymous_identity, this.password, this.client_cert, this.engine, this.engine_id, this.key_id, enterpriseField};
        this.networkId = -1;
        this.SSID = null;
        this.BSSID = null;
        this.priority = 0;
        this.hiddenSSID = false;
        this.disableReason = 0;
        this.allowedKeyManagement = new BitSet();
        this.allowedProtocols = new BitSet();
        this.allowedAuthAlgorithms = new BitSet();
        this.allowedPairwiseCiphers = new BitSet();
        this.allowedGroupCiphers = new BitSet();
        this.wepKeys = new String[4];
        while (true) {
            String[] strArr = this.wepKeys;
            if (i < strArr.length) {
                strArr[i] = null;
                i++;
            } else {
                this.enterpriseConfig = new WifiEnterpriseConfig();
                this.ipAssignment = IpAssignment.UNASSIGNED;
                this.proxySettings = ProxySettings.UNASSIGNED;
                this.linkProperties = new LinkProperties();
                return;
            }
        }
    }

    public boolean isValid() {
        if (this.allowedKeyManagement.cardinality() > 1) {
            if (this.allowedKeyManagement.cardinality() != 2 || !this.allowedKeyManagement.get(2)) {
                return false;
            }
            if (!this.allowedKeyManagement.get(3) && !this.allowedKeyManagement.get(1)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = this.status;
        if (i == 0) {
            sb.append("* ");
        } else if (i == 1) {
            sb.append("- DSBLE: ").append(this.disableReason).append(" ");
        }
        sb.append("ID: ").append(this.networkId).append(" SSID: ").append(this.SSID).append(" BSSID: ").append(this.BSSID).append(" PRIO: ").append(this.priority).append('\n');
        sb.append(" KeyMgmt:");
        for (int i2 = 0; i2 < this.allowedKeyManagement.size(); i2++) {
            if (this.allowedKeyManagement.get(i2)) {
                sb.append(" ");
                if (i2 < KeyMgmt.strings.length) {
                    sb.append(KeyMgmt.strings[i2]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append(" Protocols:");
        for (int i3 = 0; i3 < this.allowedProtocols.size(); i3++) {
            if (this.allowedProtocols.get(i3)) {
                sb.append(" ");
                if (i3 < Protocol.strings.length) {
                    sb.append(Protocol.strings[i3]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append('\n');
        sb.append(" AuthAlgorithms:");
        for (int i4 = 0; i4 < this.allowedAuthAlgorithms.size(); i4++) {
            if (this.allowedAuthAlgorithms.get(i4)) {
                sb.append(" ");
                if (i4 < AuthAlgorithm.strings.length) {
                    sb.append(AuthAlgorithm.strings[i4]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append('\n');
        sb.append(" PairwiseCiphers:");
        for (int i5 = 0; i5 < this.allowedPairwiseCiphers.size(); i5++) {
            if (this.allowedPairwiseCiphers.get(i5)) {
                sb.append(" ");
                if (i5 < PairwiseCipher.strings.length) {
                    sb.append(PairwiseCipher.strings[i5]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append('\n');
        sb.append(" GroupCiphers:");
        for (int i6 = 0; i6 < this.allowedGroupCiphers.size(); i6++) {
            if (this.allowedGroupCiphers.get(i6)) {
                sb.append(" ");
                if (i6 < GroupCipher.strings.length) {
                    sb.append(GroupCipher.strings[i6]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append('\n').append(" PSK: ");
        if (this.preSharedKey != null) {
            sb.append('*');
        }
        sb.append(this.enterpriseConfig);
        sb.append('\n');
        sb.append("IP assignment: " + this.ipAssignment.toString());
        sb.append("\n");
        sb.append("Proxy settings: " + this.proxySettings.toString());
        sb.append("\n");
        sb.append(this.linkProperties.toString());
        sb.append("\n");
        return sb.toString();
    }

    public String getPrintableSsid() {
        String str = this.SSID;
        if (str == null) {
            return "";
        }
        int length = str.length();
        if (length > 2 && this.SSID.charAt(0) == '\"') {
            int i = length - 1;
            if (this.SSID.charAt(i) == '\"') {
                return this.SSID.substring(1, i);
            }
        }
        if (length > 3 && this.SSID.charAt(0) == 'P' && this.SSID.charAt(1) == '\"') {
            int i2 = length - 1;
            if (this.SSID.charAt(i2) == '\"') {
                return WifiSsid.createFromAsciiEncoded(this.SSID.substring(2, i2)).toString();
            }
        }
        return this.SSID;
    }

    String getKeyIdForCredentials(WifiConfiguration wifiConfiguration) {
        try {
            if (TextUtils.isEmpty(this.SSID)) {
                this.SSID = wifiConfiguration.SSID;
            }
            if (this.allowedKeyManagement.cardinality() == 0) {
                this.allowedKeyManagement = wifiConfiguration.allowedKeyManagement;
            }
            String str = this.allowedKeyManagement.get(2) ? KeyMgmt.strings[2] : null;
            if (this.allowedKeyManagement.get(3)) {
                str = str + KeyMgmt.strings[3];
            }
            if (TextUtils.isEmpty(str)) {
                throw new IllegalStateException("Not an EAP network");
            }
            return trimStringForKeyId(this.SSID) + "_" + str + "_" + trimStringForKeyId(this.enterpriseConfig.getKeyId(wifiConfiguration != null ? wifiConfiguration.enterpriseConfig : null));
        } catch (NullPointerException unused) {
            throw new IllegalStateException("Invalid config details");
        }
    }

    private String trimStringForKeyId(String str) {
        return str.replace("\"", "").replace(" ", "");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static BitSet readBitSet(Parcel parcel) {
        int i = parcel.readInt();
        BitSet bitSet = new BitSet();
        for (int i2 = 0; i2 < i; i2++) {
            bitSet.set(parcel.readInt());
        }
        return bitSet;
    }

    private static void writeBitSet(Parcel parcel, BitSet bitSet) {
        parcel.writeInt(bitSet.cardinality());
        int iNextSetBit = -1;
        while (true) {
            iNextSetBit = bitSet.nextSetBit(iNextSetBit + 1);
            if (iNextSetBit == -1) {
                return;
            } else {
                parcel.writeInt(iNextSetBit);
            }
        }
    }

    public int getAuthType() {
        if (!isValid()) {
            throw new IllegalStateException("Invalid configuration");
        }
        if (this.allowedKeyManagement.get(1)) {
            return 1;
        }
        if (this.allowedKeyManagement.get(4)) {
            return 4;
        }
        if (this.allowedKeyManagement.get(2)) {
            return 2;
        }
        return this.allowedKeyManagement.get(3) ? 3 : 0;
    }

    public WifiConfiguration(WifiConfiguration wifiConfiguration) {
        this.eap = new EnterpriseField("eap");
        this.phase2 = new EnterpriseField("phase2");
        this.identity = new EnterpriseField("identity");
        this.anonymous_identity = new EnterpriseField("anonymous_identity");
        this.password = new EnterpriseField(AccountManager.KEY_PASSWORD);
        this.client_cert = new EnterpriseField("client_cert");
        this.engine = new EnterpriseField(TextToSpeech.Engine.KEY_PARAM_ENGINE);
        this.engine_id = new EnterpriseField("engine_id");
        this.key_id = new EnterpriseField("key_id");
        EnterpriseField enterpriseField = new EnterpriseField("ca_cert");
        this.ca_cert = enterpriseField;
        int i = 0;
        this.enterpriseFields = new EnterpriseField[]{this.eap, this.phase2, this.identity, this.anonymous_identity, this.password, this.client_cert, this.engine, this.engine_id, this.key_id, enterpriseField};
        if (wifiConfiguration == null) {
            return;
        }
        this.networkId = wifiConfiguration.networkId;
        this.status = wifiConfiguration.status;
        this.disableReason = wifiConfiguration.disableReason;
        this.SSID = wifiConfiguration.SSID;
        this.BSSID = wifiConfiguration.BSSID;
        this.preSharedKey = wifiConfiguration.preSharedKey;
        this.wepKeys = new String[4];
        while (true) {
            String[] strArr = this.wepKeys;
            if (i < strArr.length) {
                strArr[i] = wifiConfiguration.wepKeys[i];
                i++;
            } else {
                this.wepTxKeyIndex = wifiConfiguration.wepTxKeyIndex;
                this.priority = wifiConfiguration.priority;
                this.hiddenSSID = wifiConfiguration.hiddenSSID;
                this.allowedKeyManagement = (BitSet) wifiConfiguration.allowedKeyManagement.clone();
                this.allowedProtocols = (BitSet) wifiConfiguration.allowedProtocols.clone();
                this.allowedAuthAlgorithms = (BitSet) wifiConfiguration.allowedAuthAlgorithms.clone();
                this.allowedPairwiseCiphers = (BitSet) wifiConfiguration.allowedPairwiseCiphers.clone();
                this.allowedGroupCiphers = (BitSet) wifiConfiguration.allowedGroupCiphers.clone();
                this.enterpriseConfig = new WifiEnterpriseConfig(wifiConfiguration.enterpriseConfig);
                this.ipAssignment = wifiConfiguration.ipAssignment;
                this.proxySettings = wifiConfiguration.proxySettings;
                this.linkProperties = new LinkProperties(wifiConfiguration.linkProperties);
                return;
            }
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.networkId);
        parcel.writeInt(this.status);
        parcel.writeInt(this.disableReason);
        parcel.writeString(this.SSID);
        parcel.writeString(this.BSSID);
        parcel.writeString(this.preSharedKey);
        for (String str : this.wepKeys) {
            parcel.writeString(str);
        }
        parcel.writeInt(this.wepTxKeyIndex);
        parcel.writeInt(this.priority);
        parcel.writeInt(this.hiddenSSID ? 1 : 0);
        writeBitSet(parcel, this.allowedKeyManagement);
        writeBitSet(parcel, this.allowedProtocols);
        writeBitSet(parcel, this.allowedAuthAlgorithms);
        writeBitSet(parcel, this.allowedPairwiseCiphers);
        writeBitSet(parcel, this.allowedGroupCiphers);
        parcel.writeParcelable(this.enterpriseConfig, i);
        parcel.writeString(this.ipAssignment.name());
        parcel.writeString(this.proxySettings.name());
        parcel.writeParcelable(this.linkProperties, i);
    }
}
