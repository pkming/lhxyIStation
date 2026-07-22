package android.net;

import android.content.Context;
import android.text.TextUtils;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.tools.ant.util.ProxySetup;

/* JADX INFO: loaded from: classes.dex */
public final class Proxy {
    private static final boolean DEBUG = false;
    public static final String EXTRA_PROXY_INFO = "proxy";
    private static final String NAME_IP_REGEX = "[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*";
    public static final String PROXY_CHANGE_ACTION = "android.intent.action.PROXY_CHANGE";
    private static final String TAG = "Proxy";
    private static ConnectivityManager sConnectivityManager;
    private static final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$";
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEXP);
    private static final String EXCLLIST_REGEXP = "$|^(.?[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*)+(,(.?[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*))*$";
    private static final Pattern EXCLLIST_PATTERN = Pattern.compile(EXCLLIST_REGEXP);
    private static final ProxySelector sDefaultProxySelector = ProxySelector.getDefault();

    public static final java.net.Proxy getProxy(Context context, String str) {
        String host = str != null ? URI.create(str).getHost() : "";
        if (!isLocalHost(host)) {
            if (sConnectivityManager == null) {
                sConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            ConnectivityManager connectivityManager = sConnectivityManager;
            if (connectivityManager == null) {
                return java.net.Proxy.NO_PROXY;
            }
            ProxyProperties proxy = connectivityManager.getProxy();
            if (proxy != null && !proxy.isExcluded(host)) {
                return proxy.makeProxy();
            }
        }
        return java.net.Proxy.NO_PROXY;
    }

    public static final String getHost(Context context) {
        java.net.Proxy proxy = getProxy(context, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return null;
        }
        try {
            return ((InetSocketAddress) proxy.address()).getHostName();
        } catch (Exception unused) {
            return null;
        }
    }

    public static final int getPort(Context context) {
        java.net.Proxy proxy = getProxy(context, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return -1;
        }
        try {
            return ((InetSocketAddress) proxy.address()).getPort();
        } catch (Exception unused) {
            return -1;
        }
    }

    public static final String getDefaultHost() {
        String property = System.getProperty(ProxySetup.HTTP_PROXY_HOST);
        if (TextUtils.isEmpty(property)) {
            return null;
        }
        return property;
    }

    public static final int getDefaultPort() {
        if (getDefaultHost() == null) {
            return -1;
        }
        try {
            return Integer.parseInt(System.getProperty(ProxySetup.HTTP_PROXY_PORT));
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    public static final HttpHost getPreferredHttpHost(Context context, String str) {
        java.net.Proxy proxy = getProxy(context, str);
        if (proxy.equals(java.net.Proxy.NO_PROXY)) {
            return null;
        }
        InetSocketAddress inetSocketAddress = (InetSocketAddress) proxy.address();
        return new HttpHost(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), "http");
    }

    private static final boolean isLocalHost(String str) {
        if (str != null && str != null) {
            try {
                if (str.equalsIgnoreCase("localhost")) {
                    return true;
                }
                if (NetworkUtils.numericToInetAddress(str).isLoopbackAddress()) {
                    return true;
                }
            } catch (IllegalArgumentException unused) {
            }
        }
        return false;
    }

    public static void validate(String str, String str2, String str3) {
        Matcher matcher = HOSTNAME_PATTERN.matcher(str);
        Matcher matcher2 = EXCLLIST_PATTERN.matcher(str3);
        if (!matcher.matches()) {
            throw new IllegalArgumentException();
        }
        if (!matcher2.matches()) {
            throw new IllegalArgumentException();
        }
        if (str.length() > 0 && str2.length() == 0) {
            throw new IllegalArgumentException();
        }
        if (str2.length() > 0) {
            if (str.length() == 0) {
                throw new IllegalArgumentException();
            }
            try {
                int i = Integer.parseInt(str2);
                if (i <= 0 || i > 65535) {
                    throw new IllegalArgumentException();
                }
            } catch (NumberFormatException unused) {
                throw new IllegalArgumentException();
            }
        }
    }

    static class AndroidProxySelectorRoutePlanner extends ProxySelectorRoutePlanner {
        private Context mContext;

        public AndroidProxySelectorRoutePlanner(SchemeRegistry schemeRegistry, ProxySelector proxySelector, Context context) {
            super(schemeRegistry, proxySelector);
            this.mContext = context;
        }

        @Override // org.apache.http.impl.conn.ProxySelectorRoutePlanner
        protected java.net.Proxy chooseProxy(List<java.net.Proxy> list, HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
            return Proxy.getProxy(this.mContext, httpHost.getHostName());
        }

        @Override // org.apache.http.impl.conn.ProxySelectorRoutePlanner
        protected HttpHost determineProxy(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
            return Proxy.getPreferredHttpHost(this.mContext, httpHost.getHostName());
        }

        @Override // org.apache.http.impl.conn.ProxySelectorRoutePlanner, org.apache.http.conn.routing.HttpRoutePlanner
        public HttpRoute determineRoute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
            HttpHost preferredHttpHost = Proxy.getPreferredHttpHost(this.mContext, httpHost.getHostName());
            if (preferredHttpHost == null) {
                return new HttpRoute(httpHost);
            }
            return new HttpRoute(httpHost, null, preferredHttpHost, false);
        }
    }

    public static final HttpRoutePlanner getAndroidProxySelectorRoutePlanner(Context context) {
        return new AndroidProxySelectorRoutePlanner(new SchemeRegistry(), ProxySelector.getDefault(), context);
    }

    public static final void setHttpProxySystemProperty(ProxyProperties proxyProperties) {
        String pacFileUrl;
        String string;
        String exclusionList;
        String host = null;
        if (proxyProperties != null) {
            host = proxyProperties.getHost();
            string = Integer.toString(proxyProperties.getPort());
            exclusionList = proxyProperties.getExclusionList();
            pacFileUrl = proxyProperties.getPacFileUrl();
        } else {
            pacFileUrl = null;
            string = null;
            exclusionList = null;
        }
        setHttpProxySystemProperty(host, string, exclusionList, pacFileUrl);
    }

    public static final void setHttpProxySystemProperty(String str, String str2, String str3, String str4) {
        if (str3 != null) {
            str3 = str3.replace(",", "|");
        }
        if (str != null) {
            System.setProperty(ProxySetup.HTTP_PROXY_HOST, str);
            System.setProperty(ProxySetup.HTTPS_PROXY_HOST, str);
        } else {
            System.clearProperty(ProxySetup.HTTP_PROXY_HOST);
            System.clearProperty(ProxySetup.HTTPS_PROXY_HOST);
        }
        if (str2 != null) {
            System.setProperty(ProxySetup.HTTP_PROXY_PORT, str2);
            System.setProperty(ProxySetup.HTTPS_PROXY_PORT, str2);
        } else {
            System.clearProperty(ProxySetup.HTTP_PROXY_PORT);
            System.clearProperty(ProxySetup.HTTPS_PROXY_PORT);
        }
        if (str3 != null) {
            System.setProperty(ProxySetup.HTTP_NON_PROXY_HOSTS, str3);
            System.setProperty(ProxySetup.HTTPS_NON_PROXY_HOSTS, str3);
        } else {
            System.clearProperty(ProxySetup.HTTP_NON_PROXY_HOSTS);
            System.clearProperty(ProxySetup.HTTPS_NON_PROXY_HOSTS);
        }
        if (!TextUtils.isEmpty(str4)) {
            ProxySelector.setDefault(new PacProxySelector());
        } else {
            ProxySelector.setDefault(sDefaultProxySelector);
        }
    }
}
