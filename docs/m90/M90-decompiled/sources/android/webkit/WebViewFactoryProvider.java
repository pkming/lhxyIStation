package android.webkit;

import android.content.Context;
import android.webkit.WebView;

/* JADX INFO: loaded from: classes.dex */
public interface WebViewFactoryProvider {

    public interface Statics {
        String findAddress(String str);

        String getDefaultUserAgent(Context context);

        void setPlatformNotificationsEnabled(boolean z);

        void setWebContentsDebuggingEnabled(boolean z);
    }

    WebViewProvider createWebView(WebView webView, WebView.PrivateAccess privateAccess);

    CookieManager getCookieManager();

    GeolocationPermissions getGeolocationPermissions();

    Statics getStatics();

    WebIconDatabase getWebIconDatabase();

    WebStorage getWebStorage();

    WebViewDatabase getWebViewDatabase(Context context);
}
