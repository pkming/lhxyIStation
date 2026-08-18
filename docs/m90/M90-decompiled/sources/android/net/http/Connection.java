package android.net.http;

import android.content.Context;
import android.os.SystemClock;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/* JADX INFO: loaded from: classes.dex */
abstract class Connection {
    private static final int DONE = 3;
    private static final int DRAIN = 2;
    private static final String HTTP_CONNECTION = "http.connection";
    private static final int MAX_PIPE = 3;
    private static final int MIN_PIPE = 2;
    private static final int READ = 1;
    private static final int RETRY_REQUEST_LIMIT = 2;
    private static final int SEND = 0;
    static final int SOCKET_TIMEOUT = 60000;
    private byte[] mBuf;
    Context mContext;
    HttpHost mHost;
    RequestFeeder mRequestFeeder;
    private static final String[] states = {"SEND", "READ", "DRAIN", "DONE"};
    private static int STATE_NORMAL = 0;
    private static int STATE_CANCEL_REQUESTED = 1;
    protected AndroidHttpClientConnection mHttpClientConnection = null;
    protected SslCertificate mCertificate = null;
    private int mActive = STATE_NORMAL;
    private boolean mCanPersist = false;
    private HttpContext mHttpContext = new BasicHttpContext(null);

    abstract void closeConnection();

    abstract String getScheme();

    abstract AndroidHttpClientConnection openConnection(Request request) throws IOException;

    protected Connection(Context context, HttpHost httpHost, RequestFeeder requestFeeder) {
        this.mContext = context;
        this.mHost = httpHost;
        this.mRequestFeeder = requestFeeder;
    }

    HttpHost getHost() {
        return this.mHost;
    }

    static Connection getConnection(Context context, HttpHost httpHost, HttpHost httpHost2, RequestFeeder requestFeeder) {
        if (httpHost.getSchemeName().equals("http")) {
            return new HttpConnection(context, httpHost, requestFeeder);
        }
        return new HttpsConnection(context, httpHost, httpHost2, requestFeeder);
    }

    SslCertificate getCertificate() {
        return this.mCertificate;
    }

    void cancel() {
        this.mActive = STATE_CANCEL_REQUESTED;
        closeConnection();
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0044  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void processRequests(android.net.http.Request r18) {
        /*
            Method dump skipped, instruction units count: 261
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.http.Connection.processRequests(android.net.http.Request):void");
    }

    private boolean clearPipe(LinkedList<Request> linkedList) {
        boolean z;
        synchronized (this.mRequestFeeder) {
            z = true;
            while (!linkedList.isEmpty()) {
                this.mRequestFeeder.requeueRequest(linkedList.removeLast());
                z = false;
            }
            if (z) {
                z = !this.mRequestFeeder.haveRequest(this.mHost);
            }
        }
        return z;
    }

    private boolean openHttpConnection(Request request) {
        AndroidHttpClientConnection androidHttpClientConnectionOpenConnection;
        SystemClock.uptimeMillis();
        int i = -6;
        Exception e = null;
        try {
            this.mCertificate = null;
            androidHttpClientConnectionOpenConnection = openConnection(request);
            this.mHttpClientConnection = androidHttpClientConnectionOpenConnection;
        } catch (SSLConnectionClosedByUserException unused) {
            request.mFailCount = 2;
            return false;
        } catch (IOException e2) {
            e = e2;
        } catch (IllegalArgumentException e3) {
            e = e3;
            request.mFailCount = 2;
        } catch (UnknownHostException e4) {
            e = e4;
            i = -2;
        } catch (SSLHandshakeException e5) {
            e = e5;
            request.mFailCount = 2;
            i = -11;
        }
        if (androidHttpClientConnectionOpenConnection != null) {
            androidHttpClientConnectionOpenConnection.setSocketTimeout(SOCKET_TIMEOUT);
            this.mHttpContext.setAttribute(HTTP_CONNECTION, this.mHttpClientConnection);
            i = 0;
            if (i == 0) {
                return true;
            }
            if (request.mFailCount < 2) {
                this.mRequestFeeder.requeueRequest(request);
                request.mFailCount++;
            } else {
                httpFailure(request, i, e);
            }
            return i == 0;
        }
        request.mFailCount = 2;
        return false;
    }

    private boolean httpFailure(Request request, int i, Exception exc) {
        String string;
        boolean z = true;
        int i2 = request.mFailCount + 1;
        request.mFailCount = i2;
        if (i2 >= 2) {
            z = false;
            if (i < 0) {
                string = ErrorStrings.getString(i, this.mContext);
            } else {
                Throwable cause = exc.getCause();
                string = cause != null ? cause.toString() : exc.getMessage();
            }
            request.mEventHandler.error(i, string);
            request.complete();
        }
        closeConnection();
        this.mHttpContext.removeAttribute(HTTP_CONNECTION);
        return z;
    }

    HttpContext getHttpContext() {
        return this.mHttpContext;
    }

    private boolean keepAlive(HttpEntity httpEntity, ProtocolVersion protocolVersion, int i, HttpContext httpContext) {
        org.apache.http.HttpConnection httpConnection = (org.apache.http.HttpConnection) httpContext.getAttribute(HTTP_CONNECTION);
        if (httpConnection != null && !httpConnection.isOpen()) {
            return false;
        }
        if ((httpEntity != null && httpEntity.getContentLength() < 0 && (!httpEntity.isChunked() || protocolVersion.lessEquals(HttpVersion.HTTP_1_0))) || i == 1) {
            return false;
        }
        if (i == 2) {
            return true;
        }
        return true ^ protocolVersion.lessEquals(HttpVersion.HTTP_1_0);
    }

    void setCanPersist(HttpEntity httpEntity, ProtocolVersion protocolVersion, int i) {
        this.mCanPersist = keepAlive(httpEntity, protocolVersion, i, this.mHttpContext);
    }

    void setCanPersist(boolean z) {
        this.mCanPersist = z;
    }

    boolean getCanPersist() {
        return this.mCanPersist;
    }

    public synchronized String toString() {
        return this.mHost.toString();
    }

    byte[] getBuf() {
        if (this.mBuf == null) {
            this.mBuf = new byte[8192];
        }
        return this.mBuf;
    }
}
