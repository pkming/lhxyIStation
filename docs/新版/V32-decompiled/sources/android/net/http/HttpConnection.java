package android.net.http;

import android.content.Context;
import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpHost;
import org.apache.http.params.BasicHttpParams;

/* JADX INFO: loaded from: classes.dex */
class HttpConnection extends Connection {
    @Override // android.net.http.Connection
    String getScheme() {
        return "http";
    }

    void restartConnection(boolean z) {
    }

    HttpConnection(Context context, HttpHost httpHost, RequestFeeder requestFeeder) {
        super(context, httpHost, requestFeeder);
    }

    @Override // android.net.http.Connection
    AndroidHttpClientConnection openConnection(Request request) throws IOException {
        EventHandler eventHandler = request.getEventHandler();
        this.mCertificate = null;
        eventHandler.certificate(this.mCertificate);
        AndroidHttpClientConnection androidHttpClientConnection = new AndroidHttpClientConnection();
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        Socket socket = new Socket(this.mHost.getHostName(), this.mHost.getPort());
        basicHttpParams.setIntParameter("http.socket.buffer-size", 8192);
        androidHttpClientConnection.bind(socket, basicHttpParams);
        return androidHttpClientConnection;
    }

    @Override // android.net.http.Connection
    void closeConnection() {
        try {
            if (this.mHttpClientConnection == null || !this.mHttpClientConnection.isOpen()) {
                return;
            }
            this.mHttpClientConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
