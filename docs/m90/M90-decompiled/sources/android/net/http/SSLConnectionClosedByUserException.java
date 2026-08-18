package android.net.http;

import javax.net.ssl.SSLException;

/* JADX INFO: compiled from: HttpsConnection.java */
/* JADX INFO: loaded from: classes.dex */
class SSLConnectionClosedByUserException extends SSLException {
    public SSLConnectionClosedByUserException(String str) {
        super(str);
    }
}
