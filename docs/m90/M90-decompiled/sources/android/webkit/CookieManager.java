package android.webkit;

import android.net.WebAddress;

/* JADX INFO: loaded from: classes.dex */
public class CookieManager {
    protected CookieManager() {
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("doesn't implement Cloneable");
    }

    public static synchronized CookieManager getInstance() {
        return WebViewFactory.getProvider().getCookieManager();
    }

    public synchronized void setAcceptCookie(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized boolean acceptCookie() {
        throw new MustOverrideException();
    }

    public void setCookie(String str, String str2) {
        throw new MustOverrideException();
    }

    public String getCookie(String str) {
        throw new MustOverrideException();
    }

    public String getCookie(String str, boolean z) {
        throw new MustOverrideException();
    }

    public synchronized String getCookie(WebAddress webAddress) {
        throw new MustOverrideException();
    }

    public void removeSessionCookie() {
        throw new MustOverrideException();
    }

    public void removeAllCookie() {
        throw new MustOverrideException();
    }

    public synchronized boolean hasCookies() {
        throw new MustOverrideException();
    }

    public synchronized boolean hasCookies(boolean z) {
        throw new MustOverrideException();
    }

    public void removeExpiredCookie() {
        throw new MustOverrideException();
    }

    protected void flushCookieStore() {
        throw new MustOverrideException();
    }

    public static boolean allowFileSchemeCookies() {
        return getInstance().allowFileSchemeCookiesImpl();
    }

    protected boolean allowFileSchemeCookiesImpl() {
        throw new MustOverrideException();
    }

    public static void setAcceptFileSchemeCookies(boolean z) {
        getInstance().setAcceptFileSchemeCookiesImpl(z);
    }

    protected void setAcceptFileSchemeCookiesImpl(boolean z) {
        throw new MustOverrideException();
    }
}
