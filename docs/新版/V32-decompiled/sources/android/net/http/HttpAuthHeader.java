package android.net.http;

import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class HttpAuthHeader {
    private static final String ALGORITHM_TOKEN = "algorithm";
    public static final int BASIC = 1;
    public static final String BASIC_TOKEN = "Basic";
    public static final int DIGEST = 2;
    public static final String DIGEST_TOKEN = "Digest";
    private static final String NONCE_TOKEN = "nonce";
    private static final String OPAQUE_TOKEN = "opaque";
    private static final String QOP_TOKEN = "qop";
    private static final String REALM_TOKEN = "realm";
    private static final String STALE_TOKEN = "stale";
    public static final int UNKNOWN = 0;
    private String mAlgorithm;
    private boolean mIsProxy;
    private String mNonce;
    private String mOpaque;
    private String mPassword;
    private String mQop;
    private String mRealm;
    private int mScheme;
    private boolean mStale;
    private String mUsername;

    public HttpAuthHeader(String str) {
        if (str != null) {
            parseHeader(str);
        }
    }

    public boolean isProxy() {
        return this.mIsProxy;
    }

    public void setProxy() {
        this.mIsProxy = true;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public void setUsername(String str) {
        this.mUsername = str;
    }

    public String getPassword() {
        return this.mPassword;
    }

    public void setPassword(String str) {
        this.mPassword = str;
    }

    public boolean isBasic() {
        return this.mScheme == 1;
    }

    public boolean isDigest() {
        return this.mScheme == 2;
    }

    public int getScheme() {
        return this.mScheme;
    }

    public boolean getStale() {
        return this.mStale;
    }

    public String getRealm() {
        return this.mRealm;
    }

    public String getNonce() {
        return this.mNonce;
    }

    public String getOpaque() {
        return this.mOpaque;
    }

    public String getQop() {
        return this.mQop;
    }

    public String getAlgorithm() {
        return this.mAlgorithm;
    }

    public boolean isSupportedScheme() {
        if (this.mRealm == null) {
            return false;
        }
        int i = this.mScheme;
        if (i == 1) {
            return true;
        }
        if (i != 2 || !this.mAlgorithm.equals("md5")) {
            return false;
        }
        String str = this.mQop;
        return str == null || str.equals("auth");
    }

    private void parseHeader(String str) {
        String scheme;
        if (str == null || (scheme = parseScheme(str)) == null || this.mScheme == 0) {
            return;
        }
        parseParameters(scheme);
    }

    private String parseScheme(String str) {
        int iIndexOf;
        if (str == null || (iIndexOf = str.indexOf(32)) < 0) {
            return null;
        }
        String strTrim = str.substring(0, iIndexOf).trim();
        if (strTrim.equalsIgnoreCase(DIGEST_TOKEN)) {
            this.mScheme = 2;
            this.mAlgorithm = "md5";
        } else if (strTrim.equalsIgnoreCase(BASIC_TOKEN)) {
            this.mScheme = 1;
        }
        return str.substring(iIndexOf + 1);
    }

    private void parseParameters(String str) {
        int iIndexOf;
        if (str != null) {
            do {
                iIndexOf = str.indexOf(44);
                if (iIndexOf < 0) {
                    parseParameter(str);
                } else {
                    parseParameter(str.substring(0, iIndexOf));
                    str = str.substring(iIndexOf + 1);
                }
            } while (iIndexOf >= 0);
        }
    }

    private void parseParameter(String str) {
        int iIndexOf;
        if (str == null || (iIndexOf = str.indexOf(61)) < 0) {
            return;
        }
        String strTrim = str.substring(0, iIndexOf).trim();
        String strTrimDoubleQuotesIfAny = trimDoubleQuotesIfAny(str.substring(iIndexOf + 1).trim());
        if (strTrim.equalsIgnoreCase(REALM_TOKEN)) {
            this.mRealm = strTrimDoubleQuotesIfAny;
        } else if (this.mScheme == 2) {
            parseParameter(strTrim, strTrimDoubleQuotesIfAny);
        }
    }

    private void parseParameter(String str, String str2) {
        if (str == null || str2 == null) {
            return;
        }
        if (str.equalsIgnoreCase(NONCE_TOKEN)) {
            this.mNonce = str2;
            return;
        }
        if (str.equalsIgnoreCase(STALE_TOKEN)) {
            parseStale(str2);
            return;
        }
        if (str.equalsIgnoreCase(OPAQUE_TOKEN)) {
            this.mOpaque = str2;
        } else if (str.equalsIgnoreCase(QOP_TOKEN)) {
            this.mQop = str2.toLowerCase(Locale.ROOT);
        } else if (str.equalsIgnoreCase(ALGORITHM_TOKEN)) {
            this.mAlgorithm = str2.toLowerCase(Locale.ROOT);
        }
    }

    private void parseStale(String str) {
        if (str == null || !str.equalsIgnoreCase("true")) {
            return;
        }
        this.mStale = true;
    }

    private static String trimDoubleQuotesIfAny(String str) {
        int length;
        if (str == null || (length = str.length()) <= 2 || str.charAt(0) != '\"') {
            return str;
        }
        int i = length - 1;
        return str.charAt(i) == '\"' ? str.substring(1, i) : str;
    }
}
