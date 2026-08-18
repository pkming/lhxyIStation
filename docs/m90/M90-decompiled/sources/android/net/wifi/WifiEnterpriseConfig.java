package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.security.Credentials;
import android.security.KeyChain;
import android.security.KeyStore;
import android.text.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.net.imap.IMAPSClient;

/* JADX INFO: loaded from: classes.dex */
public class WifiEnterpriseConfig implements Parcelable {
    private static final String ANON_IDENTITY_KEY = "anonymous_identity";
    private static final String CA_CERT_KEY = "ca_cert";
    private static final String CA_CERT_PREFIX = "keystore://CACERT_";
    private static final String CLIENT_CERT_KEY = "client_cert";
    private static final String CLIENT_CERT_PREFIX = "keystore://USRCERT_";
    public static final Parcelable.Creator<WifiEnterpriseConfig> CREATOR = new Parcelable.Creator<WifiEnterpriseConfig>() { // from class: android.net.wifi.WifiEnterpriseConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiEnterpriseConfig createFromParcel(Parcel parcel) {
            WifiEnterpriseConfig wifiEnterpriseConfig = new WifiEnterpriseConfig();
            int i = parcel.readInt();
            for (int i2 = 0; i2 < i; i2++) {
                wifiEnterpriseConfig.mFields.put(parcel.readString(), parcel.readString());
            }
            wifiEnterpriseConfig.mCaCert = readCertificate(parcel);
            int i3 = parcel.readInt();
            PrivateKey privateKeyGeneratePrivate = null;
            if (i3 > 0) {
                try {
                    byte[] bArr = new byte[i3];
                    parcel.readByteArray(bArr);
                    privateKeyGeneratePrivate = KeyFactory.getInstance(parcel.readString()).generatePrivate(new PKCS8EncodedKeySpec(bArr));
                } catch (NoSuchAlgorithmException | InvalidKeySpecException unused) {
                }
            }
            wifiEnterpriseConfig.mClientPrivateKey = privateKeyGeneratePrivate;
            wifiEnterpriseConfig.mClientCertificate = readCertificate(parcel);
            return wifiEnterpriseConfig;
        }

        private X509Certificate readCertificate(Parcel parcel) {
            int i = parcel.readInt();
            if (i <= 0) {
                return null;
            }
            try {
                byte[] bArr = new byte[i];
                parcel.readByteArray(bArr);
                return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr));
            } catch (CertificateException unused) {
                return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiEnterpriseConfig[] newArray(int i) {
            return new WifiEnterpriseConfig[i];
        }
    };
    private static final boolean DBG = false;
    private static final String EAP_KEY = "eap";
    static final String EMPTY_VALUE = "NULL";
    private static final String ENGINE_DISABLE = "0";
    private static final String ENGINE_ENABLE = "1";
    private static final String ENGINE_ID_KEY = "engine_id";
    private static final String ENGINE_ID_KEYSTORE = "keystore";
    private static final String ENGINE_KEY = "engine";
    private static final String IDENTITY_KEY = "identity";
    private static final String KEYSTORE_URI = "keystore://";
    private static final String OLD_PRIVATE_KEY_NAME = "private_key";
    private static final String OPP_KEY_CACHING = "proactive_key_caching";
    private static final String PASSWORD_KEY = "password";
    private static final String PHASE2_KEY = "phase2";
    private static final String PRIVATE_KEY_ID_KEY = "key_id";
    private static final String SUBJECT_MATCH_KEY = "subject_match";
    private static final String TAG = "WifiEnterpriseConfig";
    private X509Certificate mCaCert;
    private X509Certificate mClientCertificate;
    private PrivateKey mClientPrivateKey;
    private HashMap<String, String> mFields = new HashMap<>();
    private boolean mNeedsSoftwareKeystore = false;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WifiEnterpriseConfig() {
    }

    public WifiEnterpriseConfig(WifiEnterpriseConfig wifiEnterpriseConfig) {
        for (String str : wifiEnterpriseConfig.mFields.keySet()) {
            this.mFields.put(str, wifiEnterpriseConfig.mFields.get(str));
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mFields.size());
        for (Map.Entry<String, String> entry : this.mFields.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
        writeCertificate(parcel, this.mCaCert);
        PrivateKey privateKey = this.mClientPrivateKey;
        if (privateKey != null) {
            String algorithm = privateKey.getAlgorithm();
            byte[] encoded = this.mClientPrivateKey.getEncoded();
            parcel.writeInt(encoded.length);
            parcel.writeByteArray(encoded);
            parcel.writeString(algorithm);
        } else {
            parcel.writeInt(0);
        }
        writeCertificate(parcel, this.mClientCertificate);
    }

    private void writeCertificate(Parcel parcel, X509Certificate x509Certificate) {
        if (x509Certificate != null) {
            try {
                byte[] encoded = x509Certificate.getEncoded();
                parcel.writeInt(encoded.length);
                parcel.writeByteArray(encoded);
                return;
            } catch (CertificateEncodingException unused) {
                parcel.writeInt(0);
                return;
            }
        }
        parcel.writeInt(0);
    }

    public static final class Eap {
        public static final int NONE = -1;
        public static final int PEAP = 0;
        public static final int PWD = 3;
        public static final int TLS = 1;
        public static final int TTLS = 2;
        public static final String[] strings = {"PEAP", IMAPSClient.DEFAULT_PROTOCOL, "TTLS", "PWD"};

        private Eap() {
        }
    }

    public static final class Phase2 {
        public static final int GTC = 4;
        public static final int MSCHAP = 2;
        public static final int MSCHAPV2 = 3;
        public static final int NONE = 0;
        public static final int PAP = 1;
        private static final String PREFIX = "auth=";
        public static final String[] strings = {WifiEnterpriseConfig.EMPTY_VALUE, "PAP", "MSCHAP", "MSCHAPV2", "GTC"};

        private Phase2() {
        }
    }

    HashMap<String, String> getFields() {
        return this.mFields;
    }

    static String[] getSupplicantKeys() {
        return new String[]{EAP_KEY, PHASE2_KEY, IDENTITY_KEY, ANON_IDENTITY_KEY, "password", CLIENT_CERT_KEY, CA_CERT_KEY, SUBJECT_MATCH_KEY, "engine", ENGINE_ID_KEY, PRIVATE_KEY_ID_KEY};
    }

    public void setEapMethod(int i) {
        if (i == 0 || i == 1 || i == 2 || i == 3) {
            this.mFields.put(EAP_KEY, Eap.strings[i]);
            this.mFields.put(OPP_KEY_CACHING, ENGINE_ENABLE);
            return;
        }
        throw new IllegalArgumentException("Unknown EAP method");
    }

    public int getEapMethod() {
        return getStringIndex(Eap.strings, this.mFields.get(EAP_KEY), -1);
    }

    public void setPhase2Method(int i) {
        if (i == 0) {
            this.mFields.put(PHASE2_KEY, EMPTY_VALUE);
        } else {
            if (i == 1 || i == 2 || i == 3 || i == 4) {
                this.mFields.put(PHASE2_KEY, convertToQuotedString("auth=" + Phase2.strings[i]));
                return;
            }
            throw new IllegalArgumentException("Unknown Phase 2 method");
        }
    }

    public int getPhase2Method() {
        String strRemoveDoubleQuotes = removeDoubleQuotes(this.mFields.get(PHASE2_KEY));
        if (strRemoveDoubleQuotes.startsWith("auth=")) {
            strRemoveDoubleQuotes = strRemoveDoubleQuotes.substring(5);
        }
        return getStringIndex(Phase2.strings, strRemoveDoubleQuotes, 0);
    }

    public void setIdentity(String str) {
        setFieldValue(IDENTITY_KEY, str, "");
    }

    public String getIdentity() {
        return getFieldValue(IDENTITY_KEY, "");
    }

    public void setAnonymousIdentity(String str) {
        setFieldValue(ANON_IDENTITY_KEY, str, "");
    }

    public String getAnonymousIdentity() {
        return getFieldValue(ANON_IDENTITY_KEY, "");
    }

    public void setPassword(String str) {
        setFieldValue("password", str, "");
    }

    public String getPassword() {
        return getFieldValue("password", "");
    }

    public void setCaCertificateAlias(String str) {
        setFieldValue(CA_CERT_KEY, str, CA_CERT_PREFIX);
    }

    public String getCaCertificateAlias() {
        return getFieldValue(CA_CERT_KEY, CA_CERT_PREFIX);
    }

    public void setCaCertificate(X509Certificate x509Certificate) {
        if (x509Certificate != null) {
            if (x509Certificate.getBasicConstraints() >= 0) {
                this.mCaCert = x509Certificate;
                return;
            }
            throw new IllegalArgumentException("Not a CA certificate");
        }
        this.mCaCert = null;
    }

    public X509Certificate getCaCertificate() {
        return this.mCaCert;
    }

    public void setClientCertificateAlias(String str) {
        setFieldValue(CLIENT_CERT_KEY, str, CLIENT_CERT_PREFIX);
        setFieldValue(PRIVATE_KEY_ID_KEY, str, Credentials.USER_PRIVATE_KEY);
        if (TextUtils.isEmpty(str)) {
            this.mFields.put("engine", ENGINE_DISABLE);
            this.mFields.put(ENGINE_ID_KEY, EMPTY_VALUE);
        } else {
            this.mFields.put("engine", ENGINE_ENABLE);
            this.mFields.put(ENGINE_ID_KEY, convertToQuotedString(ENGINE_ID_KEYSTORE));
        }
    }

    public String getClientCertificateAlias() {
        return getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX);
    }

    public void setClientKeyEntry(PrivateKey privateKey, X509Certificate x509Certificate) {
        if (x509Certificate != null) {
            if (x509Certificate.getBasicConstraints() != -1) {
                throw new IllegalArgumentException("Cannot be a CA certificate");
            }
            if (privateKey == null) {
                throw new IllegalArgumentException("Client cert without a private key");
            }
            if (privateKey.getEncoded() == null) {
                throw new IllegalArgumentException("Private key cannot be encoded");
            }
        }
        this.mClientPrivateKey = privateKey;
        this.mClientCertificate = x509Certificate;
    }

    public X509Certificate getClientCertificate() {
        return this.mClientCertificate;
    }

    boolean needsKeyStore() {
        return (this.mClientCertificate == null && this.mCaCert == null) ? false : true;
    }

    static boolean isHardwareBackedKey(PrivateKey privateKey) {
        return KeyChain.isBoundKeyAlgorithm(privateKey.getAlgorithm());
    }

    static boolean hasHardwareBackedKey(Certificate certificate) {
        return KeyChain.isBoundKeyAlgorithm(certificate.getPublicKey().getAlgorithm());
    }

    boolean needsSoftwareBackedKeyStore() {
        return this.mNeedsSoftwareKeystore;
    }

    boolean installKeys(KeyStore keyStore, String str) {
        boolean zImportKey;
        String str2 = Credentials.USER_PRIVATE_KEY + str;
        String str3 = Credentials.USER_CERTIFICATE + str;
        String str4 = Credentials.CA_CERTIFICATE + str;
        boolean zPutCertInKeyStore = true;
        if (this.mClientCertificate != null) {
            byte[] encoded = this.mClientPrivateKey.getEncoded();
            if (isHardwareBackedKey(this.mClientPrivateKey)) {
                zImportKey = keyStore.importKey(str2, encoded, 1010, 0);
            } else {
                zImportKey = keyStore.importKey(str2, encoded, 1010, 1);
                this.mNeedsSoftwareKeystore = true;
            }
            if (!zImportKey) {
                return zImportKey;
            }
            zPutCertInKeyStore = putCertInKeyStore(keyStore, str3, this.mClientCertificate);
            if (!zPutCertInKeyStore) {
                keyStore.delKey(str2, 1010);
                return zPutCertInKeyStore;
            }
        }
        X509Certificate x509Certificate = this.mCaCert;
        if (x509Certificate != null && !(zPutCertInKeyStore = putCertInKeyStore(keyStore, str4, x509Certificate))) {
            if (this.mClientCertificate != null) {
                keyStore.delKey(str2, 1010);
                keyStore.delete(str3, 1010);
            }
            return zPutCertInKeyStore;
        }
        if (this.mClientCertificate != null) {
            setClientCertificateAlias(str);
            this.mClientPrivateKey = null;
            this.mClientCertificate = null;
        }
        if (this.mCaCert != null) {
            setCaCertificateAlias(str);
            this.mCaCert = null;
        }
        return zPutCertInKeyStore;
    }

    private boolean putCertInKeyStore(KeyStore keyStore, String str, Certificate certificate) {
        try {
            return keyStore.put(str, Credentials.convertToPem(certificate), 1010, 0);
        } catch (IOException | CertificateException unused) {
            return false;
        }
    }

    void removeKeys(KeyStore keyStore) {
        String fieldValue = getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX);
        if (!TextUtils.isEmpty(fieldValue)) {
            keyStore.delKey(Credentials.USER_PRIVATE_KEY + fieldValue, 1010);
            keyStore.delete(Credentials.USER_CERTIFICATE + fieldValue, 1010);
        }
        String fieldValue2 = getFieldValue(CA_CERT_KEY, CA_CERT_PREFIX);
        if (TextUtils.isEmpty(fieldValue2)) {
            return;
        }
        keyStore.delete(Credentials.CA_CERTIFICATE + fieldValue2, 1010);
    }

    public void setSubjectMatch(String str) {
        setFieldValue(SUBJECT_MATCH_KEY, str, "");
    }

    public String getSubjectMatch() {
        return getFieldValue(SUBJECT_MATCH_KEY, "");
    }

    String getKeyId(WifiEnterpriseConfig wifiEnterpriseConfig) {
        String str = this.mFields.get(EAP_KEY);
        String str2 = this.mFields.get(PHASE2_KEY);
        if (TextUtils.isEmpty(str)) {
            str = wifiEnterpriseConfig.mFields.get(EAP_KEY);
        }
        if (TextUtils.isEmpty(str2)) {
            str2 = wifiEnterpriseConfig.mFields.get(PHASE2_KEY);
        }
        return str + "_" + str2;
    }

    boolean migrateOldEapTlsNative(WifiNative wifiNative, int i) {
        String networkVariable = wifiNative.getNetworkVariable(i, OLD_PRIVATE_KEY_NAME);
        if (TextUtils.isEmpty(networkVariable)) {
            return false;
        }
        String strRemoveDoubleQuotes = removeDoubleQuotes(networkVariable);
        if (TextUtils.isEmpty(strRemoveDoubleQuotes)) {
            return false;
        }
        this.mFields.put("engine", ENGINE_ENABLE);
        this.mFields.put(ENGINE_ID_KEY, convertToQuotedString(ENGINE_ID_KEYSTORE));
        if (strRemoveDoubleQuotes.startsWith(KEYSTORE_URI)) {
            strRemoveDoubleQuotes = new String(strRemoveDoubleQuotes.substring(11));
        }
        this.mFields.put(PRIVATE_KEY_ID_KEY, convertToQuotedString(strRemoveDoubleQuotes));
        wifiNative.setNetworkVariable(i, "engine", this.mFields.get("engine"));
        wifiNative.setNetworkVariable(i, ENGINE_ID_KEY, this.mFields.get(ENGINE_ID_KEY));
        wifiNative.setNetworkVariable(i, PRIVATE_KEY_ID_KEY, this.mFields.get(PRIVATE_KEY_ID_KEY));
        wifiNative.setNetworkVariable(i, OLD_PRIVATE_KEY_NAME, EMPTY_VALUE);
        return true;
    }

    void migrateCerts(KeyStore keyStore) {
        String fieldValue = getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX);
        if (!TextUtils.isEmpty(fieldValue) && !keyStore.contains(Credentials.USER_PRIVATE_KEY + fieldValue, 1010)) {
            keyStore.duplicate(Credentials.USER_PRIVATE_KEY + fieldValue, -1, Credentials.USER_PRIVATE_KEY + fieldValue, 1010);
            keyStore.duplicate(Credentials.USER_CERTIFICATE + fieldValue, -1, Credentials.USER_CERTIFICATE + fieldValue, 1010);
        }
        String fieldValue2 = getFieldValue(CA_CERT_KEY, CA_CERT_PREFIX);
        if (TextUtils.isEmpty(fieldValue2) || keyStore.contains(Credentials.CA_CERTIFICATE + fieldValue2, 1010)) {
            return;
        }
        keyStore.duplicate(Credentials.CA_CERTIFICATE + fieldValue2, -1, Credentials.CA_CERTIFICATE + fieldValue2, 1010);
    }

    void initializeSoftwareKeystoreFlag(KeyStore keyStore) {
        if (TextUtils.isEmpty(getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX))) {
            return;
        }
        this.mNeedsSoftwareKeystore = true;
    }

    private String removeDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int length = str.length();
        if (length <= 1 || str.charAt(0) != '\"') {
            return str;
        }
        int i = length - 1;
        return str.charAt(i) == '\"' ? str.substring(1, i) : str;
    }

    private String convertToQuotedString(String str) {
        return "\"" + str + "\"";
    }

    private int getStringIndex(String[] strArr, String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return i;
        }
        for (int i2 = 0; i2 < strArr.length; i2++) {
            if (str.equals(strArr[i2])) {
                return i2;
            }
        }
        return i;
    }

    private String getFieldValue(String str, String str2) {
        String str3 = this.mFields.get(str);
        if (TextUtils.isEmpty(str3) || EMPTY_VALUE.equals(str3)) {
            return "";
        }
        String strRemoveDoubleQuotes = removeDoubleQuotes(str3);
        return strRemoveDoubleQuotes.startsWith(str2) ? strRemoveDoubleQuotes.substring(str2.length()) : strRemoveDoubleQuotes;
    }

    private void setFieldValue(String str, String str2, String str3) {
        if (TextUtils.isEmpty(str2)) {
            this.mFields.put(str, EMPTY_VALUE);
        } else {
            this.mFields.put(str, convertToQuotedString(str3 + str2));
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : this.mFields.keySet()) {
            stringBuffer.append(str).append(" ").append(this.mFields.get(str)).append("\n");
        }
        return stringBuffer.toString();
    }
}
