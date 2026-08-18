package android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.LinkQualityInfo;
import android.net.SntpClient;
import android.os.SystemClock;
import android.provider.Settings;

/* JADX INFO: loaded from: classes.dex */
public class NtpTrustedTime implements TrustedTime {
    private static final boolean LOGD = false;
    private static final String TAG = "NtpTrustedTime";
    private static NtpTrustedTime sSingleton;
    private long mCachedNtpCertainty;
    private long mCachedNtpElapsedRealtime;
    private long mCachedNtpTime;
    private boolean mHasCache;
    private final String mServer;
    private final long mTimeout;

    private NtpTrustedTime(String str, long j) {
        this.mServer = str;
        this.mTimeout = j;
    }

    public static synchronized NtpTrustedTime getInstance(Context context) {
        if (sSingleton == null) {
            Resources resources = context.getResources();
            ContentResolver contentResolver = context.getContentResolver();
            String string = resources.getString(17039408);
            long integer = resources.getInteger(17694784);
            String string2 = Settings.Global.getString(contentResolver, "ntp_server");
            long j = Settings.Global.getLong(contentResolver, Settings.Global.NTP_TIMEOUT, integer);
            if (string2 != null) {
                string = string2;
            }
            sSingleton = new NtpTrustedTime(string, j);
        }
        return sSingleton;
    }

    @Override // android.util.TrustedTime
    public boolean forceRefresh() {
        if (this.mServer == null) {
            return false;
        }
        SntpClient sntpClient = new SntpClient();
        if (!sntpClient.requestTime(this.mServer, (int) this.mTimeout)) {
            return false;
        }
        this.mHasCache = true;
        this.mCachedNtpTime = sntpClient.getNtpTime();
        this.mCachedNtpElapsedRealtime = sntpClient.getNtpTimeReference();
        this.mCachedNtpCertainty = sntpClient.getRoundTripTime() / 2;
        return true;
    }

    @Override // android.util.TrustedTime
    public boolean hasCache() {
        return this.mHasCache;
    }

    @Override // android.util.TrustedTime
    public long getCacheAge() {
        return this.mHasCache ? SystemClock.elapsedRealtime() - this.mCachedNtpElapsedRealtime : LinkQualityInfo.UNKNOWN_LONG;
    }

    @Override // android.util.TrustedTime
    public long getCacheCertainty() {
        return this.mHasCache ? this.mCachedNtpCertainty : LinkQualityInfo.UNKNOWN_LONG;
    }

    @Override // android.util.TrustedTime
    public long currentTimeMillis() {
        if (!this.mHasCache) {
            throw new IllegalStateException("Missing authoritative time source");
        }
        return this.mCachedNtpTime + getCacheAge();
    }

    public long getCachedNtpTime() {
        return this.mCachedNtpTime;
    }

    public long getCachedNtpTimeReference() {
        return this.mCachedNtpElapsedRealtime;
    }
}
