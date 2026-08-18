package android.net.http;

import android.net.ParseException;
import android.net.WebAddress;
import android.text.format.DateFormat;
import android.webkit.CookieManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import junit.framework.Assert;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;

/* JADX INFO: loaded from: classes.dex */
public class RequestHandle {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    public static final int MAX_REDIRECT_COUNT = 16;
    private static final String PROXY_AUTHORIZATION_HEADER = "Proxy-Authorization";
    private int mBodyLength;
    private InputStream mBodyProvider;
    private Connection mConnection;
    private Map<String, String> mHeaders;
    private String mMethod;
    private int mRedirectCount;
    private Request mRequest;
    private RequestQueue mRequestQueue;
    private WebAddress mUri;
    private String mUrl;

    public static String authorizationHeader(boolean z) {
        return !z ? AUTHORIZATION_HEADER : PROXY_AUTHORIZATION_HEADER;
    }

    public RequestHandle(RequestQueue requestQueue, String str, WebAddress webAddress, String str2, Map<String, String> map, InputStream inputStream, int i, Request request) {
        this.mRedirectCount = 0;
        this.mHeaders = map == null ? new HashMap<>() : map;
        this.mBodyProvider = inputStream;
        this.mBodyLength = i;
        this.mMethod = str2 == null ? "GET" : str2;
        this.mUrl = str;
        this.mUri = webAddress;
        this.mRequestQueue = requestQueue;
        this.mRequest = request;
    }

    public RequestHandle(RequestQueue requestQueue, String str, WebAddress webAddress, String str2, Map<String, String> map, InputStream inputStream, int i, Request request, Connection connection) {
        this(requestQueue, str, webAddress, str2, map, inputStream, i, request);
        this.mConnection = connection;
    }

    public void cancel() {
        Request request = this.mRequest;
        if (request != null) {
            request.cancel();
        }
    }

    public void pauseRequest(boolean z) {
        Request request = this.mRequest;
        if (request != null) {
            request.setLoadingPaused(z);
        }
    }

    public void handleSslErrorResponse(boolean z) {
        Request request = this.mRequest;
        if (request != null) {
            request.handleSslErrorResponse(z);
        }
    }

    public boolean isRedirectMax() {
        return this.mRedirectCount >= 16;
    }

    public int getRedirectCount() {
        return this.mRedirectCount;
    }

    public void setRedirectCount(int i) {
        this.mRedirectCount = i;
    }

    public boolean setupRedirect(String str, int i, Map<String, String> map) {
        this.mHeaders.remove(AUTHORIZATION_HEADER);
        this.mHeaders.remove(PROXY_AUTHORIZATION_HEADER);
        int i2 = this.mRedirectCount + 1;
        this.mRedirectCount = i2;
        if (i2 == 16) {
            this.mRequest.error(-9, 17039528);
            return false;
        }
        if (this.mUrl.startsWith("https:") && str.startsWith("http:")) {
            this.mHeaders.remove("Referer");
        }
        this.mUrl = str;
        try {
            this.mUri = new WebAddress(this.mUrl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.mHeaders.remove("Cookie");
        String cookie = CookieManager.getInstance().getCookie(this.mUri);
        if (cookie != null && cookie.length() > 0) {
            this.mHeaders.put("Cookie", cookie);
        }
        if ((i == 302 || i == 303) && this.mMethod.equals("POST")) {
            this.mMethod = "GET";
        }
        if (i == 307) {
            try {
                InputStream inputStream = this.mBodyProvider;
                if (inputStream != null) {
                    inputStream.reset();
                }
            } catch (IOException unused) {
                return false;
            }
        } else {
            this.mHeaders.remove("Content-Type");
            this.mBodyProvider = null;
        }
        this.mHeaders.putAll(map);
        createAndQueueNewRequest();
        return true;
    }

    public void setupBasicAuthResponse(boolean z, String str, String str2) {
        this.mHeaders.put(authorizationHeader(z), "Basic " + computeBasicAuthResponse(str, str2));
        setupAuthResponse();
    }

    public void setupDigestAuthResponse(boolean z, String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        this.mHeaders.put(authorizationHeader(z), "Digest " + computeDigestAuthResponse(str, str2, str3, str4, str5, str6, str7));
        setupAuthResponse();
    }

    private void setupAuthResponse() {
        try {
            InputStream inputStream = this.mBodyProvider;
            if (inputStream != null) {
                inputStream.reset();
            }
        } catch (IOException unused) {
        }
        createAndQueueNewRequest();
    }

    public String getMethod() {
        return this.mMethod;
    }

    public static String computeBasicAuthResponse(String str, String str2) {
        Assert.assertNotNull(str);
        Assert.assertNotNull(str2);
        return new String(Base64.encodeBase64((str + ':' + str2).getBytes()));
    }

    public void waitUntilComplete() {
        this.mRequest.waitUntilComplete();
    }

    public void processRequest() {
        Connection connection = this.mConnection;
        if (connection != null) {
            connection.processRequests(this.mRequest);
        }
    }

    private String computeDigestAuthResponse(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        Assert.assertNotNull(str);
        Assert.assertNotNull(str2);
        Assert.assertNotNull(str3);
        String str8 = this.mMethod + ":" + this.mUrl;
        String strComputeCnonce = computeCnonce();
        String str9 = (((("username=" + doubleQuote(str) + ", ") + "realm=" + doubleQuote(str3) + ", ") + "nonce=" + doubleQuote(str4) + ", ") + "uri=" + doubleQuote(this.mUrl) + ", ") + "response=" + doubleQuote(computeDigest(str + ":" + str3 + ":" + str2, str8, str4, str5, "00000001", strComputeCnonce));
        if (str7 != null) {
            str9 = str9 + ", opaque=" + doubleQuote(str7);
        }
        if (str6 != null) {
            str9 = str9 + ", algorithm=" + str6;
        }
        return str5 != null ? str9 + ", qop=" + str5 + ", nc=00000001, cnonce=" + doubleQuote(strComputeCnonce) : str9;
    }

    private String computeDigest(String str, String str2, String str3, String str4, String str5, String str6) {
        if (str4 == null) {
            return KD(H(str), str3 + ":" + H(str2));
        }
        if (str4.equalsIgnoreCase("auth")) {
            return KD(H(str), str3 + ":" + str5 + ":" + str6 + ":" + str4 + ":" + H(str2));
        }
        return null;
    }

    private String KD(String str, String str2) {
        return H(str + ":" + str2);
    }

    private String H(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] bArrDigest = MessageDigest.getInstance("MD5").digest(str.getBytes());
            if (bArrDigest != null) {
                return bufferToHex(bArrDigest);
            }
            return null;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String bufferToHex(byte[] bArr) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.AM_PM, 'b', 'c', DateFormat.DATE, 'e', 'f'};
        if (bArr == null) {
            return null;
        }
        int length = bArr.length;
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length * 2);
        for (int i = 0; i < length; i++) {
            byte b = (byte) (bArr[i] & HSSFErrorConstants.ERROR_VALUE);
            sb.append(cArr[(byte) ((bArr[i] & 240) >> 4)]);
            sb.append(cArr[b]);
        }
        return sb.toString();
    }

    private String computeCnonce() {
        int iNextInt = new Random().nextInt();
        return Integer.toString(iNextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(iNextInt), 16);
    }

    private String doubleQuote(String str) {
        if (str != null) {
            return "\"" + str + "\"";
        }
        return null;
    }

    private void createAndQueueNewRequest() {
        if (this.mConnection != null) {
            RequestHandle requestHandleQueueSynchronousRequest = this.mRequestQueue.queueSynchronousRequest(this.mUrl, this.mUri, this.mMethod, this.mHeaders, this.mRequest.mEventHandler, this.mBodyProvider, this.mBodyLength);
            this.mRequest = requestHandleQueueSynchronousRequest.mRequest;
            this.mConnection = requestHandleQueueSynchronousRequest.mConnection;
            requestHandleQueueSynchronousRequest.processRequest();
            return;
        }
        this.mRequest = this.mRequestQueue.queueRequest(this.mUrl, this.mUri, this.mMethod, this.mHeaders, this.mRequest.mEventHandler, this.mBodyProvider, this.mBodyLength).mRequest;
    }
}
