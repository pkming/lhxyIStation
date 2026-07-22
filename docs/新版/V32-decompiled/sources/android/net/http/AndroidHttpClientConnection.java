package android.net.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.HttpConnectionMetricsImpl;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.apache.http.impl.io.HttpRequestWriter;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.SocketInputBuffer;
import org.apache.http.impl.io.SocketOutputBuffer;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/* JADX INFO: loaded from: classes.dex */
public class AndroidHttpClientConnection implements HttpInetConnection, org.apache.http.HttpConnection {
    private int maxHeaderCount;
    private int maxLineLength;
    private volatile boolean open;
    private SessionInputBuffer inbuffer = null;
    private SessionOutputBuffer outbuffer = null;
    private HttpMessageWriter requestWriter = null;
    private HttpConnectionMetricsImpl metrics = null;
    private Socket socket = null;
    private final EntitySerializer entityserializer = new EntitySerializer(new StrictContentLengthStrategy());

    public void bind(Socket socket, HttpParams httpParams) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        if (httpParams == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        assertNotOpen();
        socket.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(httpParams));
        socket.setSoTimeout(HttpConnectionParams.getSoTimeout(httpParams));
        int linger = HttpConnectionParams.getLinger(httpParams);
        if (linger >= 0) {
            socket.setSoLinger(linger > 0, linger);
        }
        this.socket = socket;
        int socketBufferSize = HttpConnectionParams.getSocketBufferSize(httpParams);
        this.inbuffer = new SocketInputBuffer(socket, socketBufferSize, httpParams);
        this.outbuffer = new SocketOutputBuffer(socket, socketBufferSize, httpParams);
        this.maxHeaderCount = httpParams.getIntParameter("http.connection.max-header-count", -1);
        this.maxLineLength = httpParams.getIntParameter("http.connection.max-line-length", -1);
        this.requestWriter = new HttpRequestWriter(this.outbuffer, null, httpParams);
        this.metrics = new HttpConnectionMetricsImpl(this.inbuffer.getMetrics(), this.outbuffer.getMetrics());
        this.open = true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("[");
        if (isOpen()) {
            sb.append(getRemotePort());
        } else {
            sb.append("closed");
        }
        sb.append("]");
        return sb.toString();
    }

    private void assertNotOpen() {
        if (this.open) {
            throw new IllegalStateException("Connection is already open");
        }
    }

    private void assertOpen() {
        if (!this.open) {
            throw new IllegalStateException("Connection is not open");
        }
    }

    @Override // org.apache.http.HttpConnection
    public boolean isOpen() {
        Socket socket;
        return this.open && (socket = this.socket) != null && socket.isConnected();
    }

    @Override // org.apache.http.HttpInetConnection
    public InetAddress getLocalAddress() {
        Socket socket = this.socket;
        if (socket != null) {
            return socket.getLocalAddress();
        }
        return null;
    }

    @Override // org.apache.http.HttpInetConnection
    public int getLocalPort() {
        Socket socket = this.socket;
        if (socket != null) {
            return socket.getLocalPort();
        }
        return -1;
    }

    @Override // org.apache.http.HttpInetConnection
    public InetAddress getRemoteAddress() {
        Socket socket = this.socket;
        if (socket != null) {
            return socket.getInetAddress();
        }
        return null;
    }

    @Override // org.apache.http.HttpInetConnection
    public int getRemotePort() {
        Socket socket = this.socket;
        if (socket != null) {
            return socket.getPort();
        }
        return -1;
    }

    @Override // org.apache.http.HttpConnection
    public void setSocketTimeout(int i) {
        assertOpen();
        Socket socket = this.socket;
        if (socket != null) {
            try {
                socket.setSoTimeout(i);
            } catch (SocketException unused) {
            }
        }
    }

    @Override // org.apache.http.HttpConnection
    public int getSocketTimeout() {
        Socket socket = this.socket;
        if (socket != null) {
            try {
                return socket.getSoTimeout();
            } catch (SocketException unused) {
            }
        }
        return -1;
    }

    @Override // org.apache.http.HttpConnection
    public void shutdown() throws IOException {
        this.open = false;
        Socket socket = this.socket;
        if (socket != null) {
            socket.close();
        }
    }

    @Override // org.apache.http.HttpConnection
    public void close() throws IOException {
        if (this.open) {
            this.open = false;
            doFlush();
            try {
                try {
                    this.socket.shutdownOutput();
                } catch (IOException | UnsupportedOperationException unused) {
                }
            } catch (IOException unused2) {
            }
            this.socket.shutdownInput();
            this.socket.close();
        }
    }

    public void sendRequestHeader(HttpRequest httpRequest) throws org.apache.http.HttpException, IOException {
        if (httpRequest == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        assertOpen();
        this.requestWriter.write(httpRequest);
        this.metrics.incrementRequestCount();
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest httpEntityEnclosingRequest) throws org.apache.http.HttpException, IOException {
        if (httpEntityEnclosingRequest == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        assertOpen();
        if (httpEntityEnclosingRequest.getEntity() == null) {
            return;
        }
        this.entityserializer.serialize(this.outbuffer, httpEntityEnclosingRequest, httpEntityEnclosingRequest.getEntity());
    }

    protected void doFlush() throws IOException {
        this.outbuffer.flush();
    }

    public void flush() throws IOException {
        assertOpen();
        doFlush();
    }

    /* JADX WARN: Code restructure failed: missing block: B:42:0x00b1, code lost:
    
        if (r9 == null) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00b3, code lost:
    
        r17.parseHeader(r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00b8, code lost:
    
        if (r6 < 200) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00ba, code lost:
    
        r16.metrics.incrementResponseCount();
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00bf, code lost:
    
        return r4;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public org.apache.http.StatusLine parseResponseHeader(android.net.http.Headers r17) throws org.apache.http.ParseException, java.io.IOException {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r16.assertOpen()
            org.apache.http.util.CharArrayBuffer r2 = new org.apache.http.util.CharArrayBuffer
            r3 = 64
            r2.<init>(r3)
            org.apache.http.io.SessionInputBuffer r4 = r0.inbuffer
            int r4 = r4.readLine(r2)
            r5 = -1
            if (r4 == r5) goto Lc0
            org.apache.http.message.BasicLineParser r4 = org.apache.http.message.BasicLineParser.DEFAULT
            org.apache.http.message.ParserCursor r6 = new org.apache.http.message.ParserCursor
            int r7 = r2.length()
            r8 = 0
            r6.<init>(r8, r7)
            org.apache.http.StatusLine r4 = r4.parseStatusLine(r2, r6)
            int r6 = r4.getStatusCode()
            r7 = 0
            r9 = r7
            r10 = r8
        L2e:
            if (r2 != 0) goto L36
            org.apache.http.util.CharArrayBuffer r2 = new org.apache.http.util.CharArrayBuffer
            r2.<init>(r3)
            goto L39
        L36:
            r2.clear()
        L39:
            org.apache.http.io.SessionInputBuffer r11 = r0.inbuffer
            int r11 = r11.readLine(r2)
            if (r11 == r5) goto Lb1
            int r11 = r2.length()
            r12 = 1
            if (r11 >= r12) goto L4a
            goto Lb1
        L4a:
            char r11 = r2.charAt(r8)
            r13 = 9
            r14 = 32
            if (r11 == r14) goto L56
            if (r11 != r13) goto L95
        L56:
            if (r9 == 0) goto L95
            int r11 = r2.length()
            r15 = r8
        L5d:
            if (r15 >= r11) goto L6d
            char r3 = r2.charAt(r15)
            if (r3 == r14) goto L68
            if (r3 == r13) goto L68
            goto L6d
        L68:
            int r15 = r15 + 1
            r3 = 64
            goto L5d
        L6d:
            int r3 = r0.maxLineLength
            if (r3 <= 0) goto L89
            int r3 = r9.length()
            int r3 = r3 + r12
            int r11 = r2.length()
            int r3 = r3 + r11
            int r3 = r3 - r15
            int r11 = r0.maxLineLength
            if (r3 > r11) goto L81
            goto L89
        L81:
            java.io.IOException r1 = new java.io.IOException
            java.lang.String r2 = "Maximum line length limit exceeded"
            r1.<init>(r2)
            throw r1
        L89:
            r9.append(r14)
            int r3 = r2.length()
            int r3 = r3 - r15
            r9.append(r2, r15, r3)
            goto L9e
        L95:
            if (r9 == 0) goto L9a
            r1.parseHeader(r9)
        L9a:
            int r10 = r10 + 1
            r9 = r2
            r2 = r7
        L9e:
            int r3 = r0.maxHeaderCount
            if (r3 <= 0) goto Lad
            if (r10 >= r3) goto La5
            goto Lad
        La5:
            java.io.IOException r1 = new java.io.IOException
            java.lang.String r2 = "Maximum header count exceeded"
            r1.<init>(r2)
            throw r1
        Lad:
            r3 = 64
            goto L2e
        Lb1:
            if (r9 == 0) goto Lb6
            r1.parseHeader(r9)
        Lb6:
            r1 = 200(0xc8, float:2.8E-43)
            if (r6 < r1) goto Lbf
            org.apache.http.impl.HttpConnectionMetricsImpl r1 = r0.metrics
            r1.incrementResponseCount()
        Lbf:
            return r4
        Lc0:
            org.apache.http.NoHttpResponseException r1 = new org.apache.http.NoHttpResponseException
            java.lang.String r2 = "The target server failed to respond"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.http.AndroidHttpClientConnection.parseResponseHeader(android.net.http.Headers):org.apache.http.StatusLine");
    }

    public HttpEntity receiveResponseEntity(Headers headers) {
        assertOpen();
        BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        long jDetermineLength = determineLength(headers);
        if (jDetermineLength == -2) {
            basicHttpEntity.setChunked(true);
            basicHttpEntity.setContentLength(-1L);
            basicHttpEntity.setContent(new ChunkedInputStream(this.inbuffer));
        } else if (jDetermineLength == -1) {
            basicHttpEntity.setChunked(false);
            basicHttpEntity.setContentLength(-1L);
            basicHttpEntity.setContent(new IdentityInputStream(this.inbuffer));
        } else {
            basicHttpEntity.setChunked(false);
            basicHttpEntity.setContentLength(jDetermineLength);
            basicHttpEntity.setContent(new ContentLengthInputStream(this.inbuffer, jDetermineLength));
        }
        String contentType = headers.getContentType();
        if (contentType != null) {
            basicHttpEntity.setContentType(contentType);
        }
        String contentEncoding = headers.getContentEncoding();
        if (contentEncoding != null) {
            basicHttpEntity.setContentEncoding(contentEncoding);
        }
        return basicHttpEntity;
    }

    private long determineLength(Headers headers) {
        long transferEncoding = headers.getTransferEncoding();
        if (transferEncoding < 0) {
            return transferEncoding;
        }
        long contentLength = headers.getContentLength();
        if (contentLength > -1) {
            return contentLength;
        }
        return -1L;
    }

    @Override // org.apache.http.HttpConnection
    public boolean isStale() {
        assertOpen();
        try {
            this.inbuffer.isDataAvailable(1);
            return false;
        } catch (IOException unused) {
            return true;
        }
    }

    @Override // org.apache.http.HttpConnection
    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}
