package android.webkit;

import android.os.StrictMode;
import android.util.AndroidRuntimeException;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public final class WebViewFactory {
    private static final String CHROMIUM_WEBVIEW_FACTORY = "com.android.webview.chromium.WebViewChromiumFactoryProvider";
    private static final boolean DEBUG = false;
    private static final String LOGTAG = "WebViewFactory";
    private static WebViewFactoryProvider sProviderInstance;
    private static final Object sProviderLock = new Object();

    public static boolean isExperimentalWebViewAvailable() {
        return false;
    }

    public static boolean isUseExperimentalWebViewSet() {
        return false;
    }

    public static void setUseExperimentalWebView(boolean z) {
    }

    public static boolean useExperimentalWebView() {
        return true;
    }

    private static class Preloader {
        static WebViewFactoryProvider sPreloadedProvider;

        private Preloader() {
        }

        static {
            try {
                sPreloadedProvider = (WebViewFactoryProvider) WebViewFactory.getFactoryClass().newInstance();
            } catch (Exception e) {
                Log.w(WebViewFactory.LOGTAG, "error preloading provider", e);
            }
        }
    }

    static WebViewFactoryProvider getProvider() {
        synchronized (sProviderLock) {
            WebViewFactoryProvider webViewFactoryProvider = sProviderInstance;
            if (webViewFactoryProvider != null) {
                return webViewFactoryProvider;
            }
            try {
                Class<WebViewFactoryProvider> factoryClass = getFactoryClass();
                if (Preloader.sPreloadedProvider != null && Preloader.sPreloadedProvider.getClass() == factoryClass) {
                    WebViewFactoryProvider webViewFactoryProvider2 = Preloader.sPreloadedProvider;
                    sProviderInstance = webViewFactoryProvider2;
                    return webViewFactoryProvider2;
                }
                StrictMode.ThreadPolicy threadPolicyAllowThreadDiskReads = StrictMode.allowThreadDiskReads();
                try {
                    try {
                        WebViewFactoryProvider webViewFactoryProviderNewInstance = factoryClass.newInstance();
                        sProviderInstance = webViewFactoryProviderNewInstance;
                        return webViewFactoryProviderNewInstance;
                    } finally {
                        StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskReads);
                    }
                } catch (Exception e) {
                    Log.e(LOGTAG, "error instantiating provider", e);
                    throw new AndroidRuntimeException(e);
                }
            } catch (ClassNotFoundException e2) {
                Log.e(LOGTAG, "error loading provider", e2);
                throw new AndroidRuntimeException(e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Class<WebViewFactoryProvider> getFactoryClass() throws ClassNotFoundException {
        return Class.forName(CHROMIUM_WEBVIEW_FACTORY);
    }
}
