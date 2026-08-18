package android.net.http;

/* JADX INFO: loaded from: classes.dex */
public class LoggingEventHandler implements EventHandler {
    @Override // android.net.http.EventHandler
    public void certificate(SslCertificate sslCertificate) {
    }

    @Override // android.net.http.EventHandler
    public void data(byte[] bArr, int i) {
    }

    @Override // android.net.http.EventHandler
    public void endData() {
    }

    @Override // android.net.http.EventHandler
    public void error(int i, String str) {
    }

    @Override // android.net.http.EventHandler
    public boolean handleSslErrorRequest(SslError sslError) {
        return false;
    }

    @Override // android.net.http.EventHandler
    public void headers(Headers headers) {
    }

    public void locationChanged(String str, boolean z) {
    }

    @Override // android.net.http.EventHandler
    public void status(int i, int i2, int i3, String str) {
    }

    public void requestSent() {
        HttpLog.v("LoggingEventHandler:requestSent()");
    }
}
