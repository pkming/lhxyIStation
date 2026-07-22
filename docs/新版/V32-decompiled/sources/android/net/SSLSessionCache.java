package android.net;

import android.content.Context;
import android.util.Log;
import com.android.org.conscrypt.FileClientSessionCache;
import com.android.org.conscrypt.SSLClientSessionCache;
import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class SSLSessionCache {
    private static final String TAG = "SSLSessionCache";
    final SSLClientSessionCache mSessionCache;

    public SSLSessionCache(File file) throws IOException {
        this.mSessionCache = FileClientSessionCache.usingDirectory(file);
    }

    public SSLSessionCache(Context context) {
        SSLClientSessionCache sSLClientSessionCacheUsingDirectory;
        File dir = context.getDir("sslcache", 0);
        try {
            sSLClientSessionCacheUsingDirectory = FileClientSessionCache.usingDirectory(dir);
        } catch (IOException e) {
            Log.w(TAG, "Unable to create SSL session cache in " + dir, e);
            sSLClientSessionCacheUsingDirectory = null;
        }
        this.mSessionCache = sSLClientSessionCacheUsingDirectory;
    }
}
