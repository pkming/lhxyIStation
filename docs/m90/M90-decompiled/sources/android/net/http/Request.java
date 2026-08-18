package android.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.RequestContent;

/* JADX INFO: loaded from: classes.dex */
class Request {
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String CONTENT_LENGTH_HEADER = "content-length";
    private static final String HOST_HEADER = "Host";
    private static RequestContent requestContentProcessor = new RequestContent();
    private int mBodyLength;
    private InputStream mBodyProvider;
    private Connection mConnection;
    EventHandler mEventHandler;
    HttpHost mHost;
    BasicHttpRequest mHttpRequest;
    String mPath;
    HttpHost mProxyHost;
    volatile boolean mCancelled = false;
    int mFailCount = 0;
    private int mReceivedBytes = 0;
    private final Object mClientResource = new Object();
    private boolean mLoadingPaused = false;

    Request(String str, HttpHost httpHost, HttpHost httpHost2, String str2, InputStream inputStream, int i, EventHandler eventHandler, Map<String, String> map) {
        this.mEventHandler = eventHandler;
        this.mHost = httpHost;
        this.mProxyHost = httpHost2;
        this.mPath = str2;
        this.mBodyProvider = inputStream;
        this.mBodyLength = i;
        if (inputStream == null && !"POST".equalsIgnoreCase(str)) {
            this.mHttpRequest = new BasicHttpRequest(str, getUri());
        } else {
            this.mHttpRequest = new BasicHttpEntityEnclosingRequest(str, getUri());
            if (inputStream != null) {
                setBodyProvider(inputStream, i);
            }
        }
        addHeader(HOST_HEADER, getHostPort());
        addHeader(ACCEPT_ENCODING_HEADER, "gzip");
        addHeaders(map);
    }

    synchronized void setLoadingPaused(boolean z) {
        this.mLoadingPaused = z;
        if (!z) {
            notify();
        }
    }

    void setConnection(Connection connection) {
        this.mConnection = connection;
    }

    EventHandler getEventHandler() {
        return this.mEventHandler;
    }

    void addHeader(String str, String str2) {
        if (str == null) {
            HttpLog.e("Null http header name");
            throw new NullPointerException("Null http header name");
        }
        if (str2 == null || str2.length() == 0) {
            String str3 = "Null or empty value for header \"" + str + "\"";
            HttpLog.e(str3);
            throw new RuntimeException(str3);
        }
        this.mHttpRequest.addHeader(str, str2);
    }

    void addHeaders(Map<String, String> map) {
        if (map == null) {
            return;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
    }

    void sendRequest(AndroidHttpClientConnection androidHttpClientConnection) throws org.apache.http.HttpException, IOException {
        if (this.mCancelled) {
            return;
        }
        requestContentProcessor.process(this.mHttpRequest, this.mConnection.getHttpContext());
        androidHttpClientConnection.sendRequestHeader(this.mHttpRequest);
        HttpMessage httpMessage = this.mHttpRequest;
        if (httpMessage instanceof HttpEntityEnclosingRequest) {
            androidHttpClientConnection.sendRequestEntity((HttpEntityEnclosingRequest) httpMessage);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:25:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00fa A[Catch: all -> 0x00d7, TRY_ENTER, TRY_LEAVE, TryCatch #5 {all -> 0x00d7, blocks: (B:26:0x007c, B:30:0x008a, B:39:0x00b1, B:41:0x00b9, B:43:0x00bc, B:46:0x00c5, B:50:0x00ce, B:71:0x00fa), top: B:81:0x005c }] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0102  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0108 A[PHI: r9
      0x0108: PHI (r9v8 java.io.InputStream) = (r9v9 java.io.InputStream), (r9v10 java.io.InputStream) binds: [B:76:0x0106, B:54:0x00d4] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v3, types: [java.io.InputStream] */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void readResponse(android.net.http.AndroidHttpClientConnection r18) throws org.apache.http.ParseException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 289
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.http.Request.readResponse(android.net.http.AndroidHttpClientConnection):void");
    }

    synchronized void cancel() {
        this.mLoadingPaused = false;
        notify();
        this.mCancelled = true;
        Connection connection = this.mConnection;
        if (connection != null) {
            connection.cancel();
        }
    }

    String getHostPort() {
        String schemeName = this.mHost.getSchemeName();
        int port = this.mHost.getPort();
        if ((port != 80 && schemeName.equals("http")) || (port != 443 && schemeName.equals("https"))) {
            return this.mHost.toHostString();
        }
        return this.mHost.getHostName();
    }

    String getUri() {
        if (this.mProxyHost == null || this.mHost.getSchemeName().equals("https")) {
            return this.mPath;
        }
        return this.mHost.getSchemeName() + "://" + getHostPort() + this.mPath;
    }

    public String toString() {
        return this.mPath;
    }

    void reset() {
        this.mHttpRequest.removeHeaders("content-length");
        InputStream inputStream = this.mBodyProvider;
        if (inputStream != null) {
            try {
                inputStream.reset();
            } catch (IOException unused) {
            }
            setBodyProvider(this.mBodyProvider, this.mBodyLength);
        }
        if (this.mReceivedBytes > 0) {
            this.mFailCount = 0;
            HttpLog.v("*** Request.reset() to range:" + this.mReceivedBytes);
            this.mHttpRequest.setHeader("Range", "bytes=" + this.mReceivedBytes + "-");
        }
    }

    void waitUntilComplete() {
        synchronized (this.mClientResource) {
            try {
                this.mClientResource.wait();
            } catch (InterruptedException unused) {
            }
        }
    }

    void complete() {
        synchronized (this.mClientResource) {
            this.mClientResource.notifyAll();
        }
    }

    private static boolean canResponseHaveBody(HttpRequest httpRequest, int i) {
        return ("HEAD".equalsIgnoreCase(httpRequest.getRequestLine().getMethod()) || i < 200 || i == 204 || i == 304) ? false : true;
    }

    private void setBodyProvider(InputStream inputStream, int i) {
        if (!inputStream.markSupported()) {
            throw new IllegalArgumentException("bodyProvider must support mark()");
        }
        inputStream.mark(Integer.MAX_VALUE);
        ((BasicHttpEntityEnclosingRequest) this.mHttpRequest).setEntity(new InputStreamEntity(inputStream, i));
    }

    public void handleSslErrorResponse(boolean z) {
        HttpsConnection httpsConnection = (HttpsConnection) this.mConnection;
        if (httpsConnection != null) {
            httpsConnection.restartConnection(z);
        }
    }

    void error(int i, int i2) {
        this.mEventHandler.error(i, this.mConnection.mContext.getText(i2).toString());
    }
}
