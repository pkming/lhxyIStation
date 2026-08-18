package android.net;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.android.net.IProxyService;
import com.google.android.collect.Lists;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PacProxySelector extends ProxySelector {
    public static final String PROXY_SERVICE = "com.android.net.IProxyService";
    private static final String TAG = "PacProxySelector";
    private final List<java.net.Proxy> mDefaultList;
    private IProxyService mProxyService;

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress socketAddress, IOException iOException) {
    }

    public PacProxySelector() {
        IProxyService iProxyServiceAsInterface = IProxyService.Stub.asInterface(ServiceManager.getService(PROXY_SERVICE));
        this.mProxyService = iProxyServiceAsInterface;
        if (iProxyServiceAsInterface == null) {
            Log.e(TAG, "PacManager: no proxy service");
        }
        this.mDefaultList = Lists.newArrayList(new java.net.Proxy[]{java.net.Proxy.NO_PROXY});
    }

    @Override // java.net.ProxySelector
    public List<java.net.Proxy> select(URI uri) {
        String host;
        if (this.mProxyService == null) {
            this.mProxyService = IProxyService.Stub.asInterface(ServiceManager.getService(PROXY_SERVICE));
        }
        if (this.mProxyService == null) {
            Log.e(TAG, "select: no proxy service return NO_PROXY");
            return Lists.newArrayList(new java.net.Proxy[]{java.net.Proxy.NO_PROXY});
        }
        String strResolvePacFile = null;
        try {
            host = uri.toURL().toString();
        } catch (MalformedURLException unused) {
            host = uri.getHost();
        }
        try {
            strResolvePacFile = this.mProxyService.resolvePacFile(uri.getHost(), host);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (strResolvePacFile == null) {
            return this.mDefaultList;
        }
        return parseResponse(strResolvePacFile);
    }

    private static List<java.net.Proxy> parseResponse(String str) {
        int i;
        String[] strArrSplit = str.split(";");
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        for (String str2 : strArrSplit) {
            String strTrim = str2.trim();
            if (strTrim.equals("DIRECT")) {
                arrayListNewArrayList.add(java.net.Proxy.NO_PROXY);
            } else if (strTrim.startsWith("PROXY ")) {
                String[] strArrSplit2 = strTrim.substring(6).split(":");
                String str3 = strArrSplit2[0];
                try {
                    i = Integer.parseInt(strArrSplit2[1]);
                } catch (Exception unused) {
                    i = 8080;
                }
                arrayListNewArrayList.add(new java.net.Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(str3, i)));
            }
        }
        if (arrayListNewArrayList.size() == 0) {
            arrayListNewArrayList.add(java.net.Proxy.NO_PROXY);
        }
        return arrayListNewArrayList;
    }
}
