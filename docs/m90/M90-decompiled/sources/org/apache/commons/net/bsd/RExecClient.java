package org.apache.commons.net.bsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.commons.net.SocketClient;
import org.apache.commons.net.io.SocketInputStream;

/* JADX INFO: loaded from: classes3.dex */
public class RExecClient extends SocketClient {
    public static final int DEFAULT_PORT = 512;
    protected static final char NULL_CHAR = 0;
    private boolean __remoteVerificationEnabled;
    protected InputStream _errorStream_ = null;

    InputStream _createErrorStream() throws IOException {
        ServerSocket serverSocketCreateServerSocket = this._serverSocketFactory_.createServerSocket(0, 1, getLocalAddress());
        this._output_.write(Integer.toString(serverSocketCreateServerSocket.getLocalPort()).getBytes("UTF-8"));
        this._output_.write(0);
        this._output_.flush();
        Socket socketAccept = serverSocketCreateServerSocket.accept();
        serverSocketCreateServerSocket.close();
        if (this.__remoteVerificationEnabled && !verifyRemote(socketAccept)) {
            socketAccept.close();
            throw new IOException("Security violation: unexpected connection attempt by " + socketAccept.getInetAddress().getHostAddress());
        }
        return new SocketInputStream(socketAccept, socketAccept.getInputStream());
    }

    public RExecClient() {
        setDefaultPort(512);
    }

    public InputStream getInputStream() {
        return this._input_;
    }

    public OutputStream getOutputStream() {
        return this._output_;
    }

    public InputStream getErrorStream() {
        return this._errorStream_;
    }

    public void rexec(String str, String str2, String str3, boolean z) throws IOException {
        if (z) {
            this._errorStream_ = _createErrorStream();
        } else {
            this._output_.write(0);
        }
        this._output_.write(str.getBytes(getCharsetName()));
        this._output_.write(0);
        this._output_.write(str2.getBytes(getCharsetName()));
        this._output_.write(0);
        this._output_.write(str3.getBytes(getCharsetName()));
        this._output_.write(0);
        this._output_.flush();
        int i = this._input_.read();
        if (i <= 0) {
            if (i < 0) {
                throw new IOException("Server closed connection.");
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        while (true) {
            int i2 = this._input_.read();
            if (i2 == -1 || i2 == 10) {
                break;
            } else {
                sb.append((char) i2);
            }
        }
        throw new IOException(sb.toString());
    }

    public void rexec(String str, String str2, String str3) throws IOException {
        rexec(str, str2, str3, false);
    }

    @Override // org.apache.commons.net.SocketClient
    public void disconnect() throws IOException {
        InputStream inputStream = this._errorStream_;
        if (inputStream != null) {
            inputStream.close();
        }
        this._errorStream_ = null;
        super.disconnect();
    }

    public final void setRemoteVerificationEnabled(boolean z) {
        this.__remoteVerificationEnabled = z;
    }

    public final boolean isRemoteVerificationEnabled() {
        return this.__remoteVerificationEnabled;
    }
}
