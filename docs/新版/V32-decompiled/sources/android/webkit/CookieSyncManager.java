package android.webkit;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public final class CookieSyncManager extends WebSyncManager {
    private static boolean sGetInstanceAllowed = false;
    private static CookieSyncManager sRef;

    @Override // android.webkit.WebSyncManager
    public /* bridge */ /* synthetic */ void resetSync() {
        super.resetSync();
    }

    @Override // android.webkit.WebSyncManager, java.lang.Runnable
    public /* bridge */ /* synthetic */ void run() {
        super.run();
    }

    @Override // android.webkit.WebSyncManager
    public /* bridge */ /* synthetic */ void startSync() {
        super.startSync();
    }

    @Override // android.webkit.WebSyncManager
    public /* bridge */ /* synthetic */ void stopSync() {
        super.stopSync();
    }

    @Override // android.webkit.WebSyncManager
    public /* bridge */ /* synthetic */ void sync() {
        super.sync();
    }

    private CookieSyncManager() {
        super("CookieSyncManager");
    }

    public static synchronized CookieSyncManager getInstance() {
        checkInstanceIsAllowed();
        if (sRef == null) {
            sRef = new CookieSyncManager();
        }
        return sRef;
    }

    public static synchronized CookieSyncManager createInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Invalid context argument");
        }
        setGetInstanceIsAllowed();
        return getInstance();
    }

    @Override // android.webkit.WebSyncManager
    protected void syncFromRamToFlash() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager.acceptCookie()) {
            cookieManager.flushCookieStore();
        }
    }

    static void setGetInstanceIsAllowed() {
        sGetInstanceAllowed = true;
    }

    private static void checkInstanceIsAllowed() {
        if (!sGetInstanceAllowed) {
            throw new IllegalStateException("CookieSyncManager::createInstance() needs to be called before CookieSyncManager::getInstance()");
        }
    }
}
