package com.autonavi.base.ae.gmap;

import android.content.Context;
import com.amap.api.col.p0003sl.dv;
import com.amap.api.col.p0003sl.dx;
import com.amap.api.col.p0003sl.jw;
import com.amap.api.col.p0003sl.md;
import com.amap.api.maps.MapsInitializer;
import com.autonavi.amap.mapcore.tools.GlMapUtil;
import com.autonavi.base.amap.mapcore.maploader.AMapLoader;
import com.autonavi.base.amap.mapcore.maploader.NetworkState;
import com.autonavi.base.amap.mapcore.tools.GLConvertUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes2.dex */
public class NetworkProxyManager {
    private static NetworkProxyManager sInstance;
    private Context context;
    private NetworkProxy networkProxy;
    private NetworkState networkState;
    private String userAgent;
    private boolean isNetworkConnected = false;
    private Map<Long, AMapLoader> aMapLoaderHashtable = new ConcurrentHashMap();
    private Map<Long, GLMapEngine> engines = new ConcurrentHashMap();
    private NetworkState.NetworkChangeListener networkChangeListener = new NetworkState.NetworkChangeListener() { // from class: com.autonavi.base.ae.gmap.NetworkProxyManager.1
        @Override // com.autonavi.base.amap.mapcore.maploader.NetworkState.NetworkChangeListener
        public void networkStateChanged(Context context) {
            NetworkProxyManager.this.isNetworkConnected = NetworkState.isNetworkConnected(context);
            Iterator it = NetworkProxyManager.this.engines.entrySet().iterator();
            while (it.hasNext()) {
                ((GLMapEngine) ((Map.Entry) it.next()).getValue()).setNetStatus(NetworkProxyManager.this.isNetworkConnected);
            }
        }
    };

    private static native void nativeCancelDownLoad(long j);

    private static native long nativeCreateNetworkProxy(Object obj);

    private static native void nativeDestroyNetworkProxy(long j);

    private static native void nativeFailedDownLoad(long j, int i);

    private static native void nativeFinishDownLoad(long j);

    private static native void nativeInitNetworkProxy(long j);

    private static native void nativeReceiveNetData(byte[] bArr, long j, int i);

    public static NetworkProxyManager getInstance() {
        if (sInstance == null) {
            sInstance = new NetworkProxyManager();
        }
        return sInstance;
    }

    private NetworkProxyManager() {
    }

    public void init() {
        NetworkProxy networkProxyCreateNetworkProxy = createNetworkProxy();
        this.networkProxy = networkProxyCreateNetworkProxy;
        initNetworkProxy(networkProxyCreateNetworkProxy);
        initNetworkState();
    }

    public boolean isReady() {
        return (this.context == null || this.networkProxy == null || this.networkState == null) ? false : true;
    }

    public void destroy() {
        NetworkProxy networkProxy = this.networkProxy;
        if (networkProxy != null) {
            destroyNetworkProxy(networkProxy);
            this.networkProxy = null;
        }
        releaseNetworkState();
        this.context = null;
    }

    public void initContext(Context context) {
        if (context == null) {
            return;
        }
        this.context = context.getApplicationContext();
    }

    public NetworkProxy createNetworkProxy() {
        return new NetworkProxy(nativeCreateNetworkProxy(this));
    }

    public void initNetworkProxy(NetworkProxy networkProxy) {
        nativeInitNetworkProxy(networkProxy.networkProxyInstance);
    }

    public void destroyNetworkProxy(NetworkProxy networkProxy) {
        nativeDestroyNetworkProxy(networkProxy.networkProxyInstance);
    }

    public boolean isNetworkConnected() {
        return this.isNetworkConnected;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    private void initNetworkState() {
        this.userAgent = System.getProperty("http.agent") + " amap/" + GlMapUtil.getAppVersionName(this.context);
        NetworkState networkState = new NetworkState();
        this.networkState = networkState;
        networkState.setNetworkListener(this.networkChangeListener);
        this.networkState.registerNetChangeReceiver(this.context.getApplicationContext(), true);
        this.isNetworkConnected = NetworkState.isNetworkConnected(this.context.getApplicationContext());
    }

    private void releaseNetworkState() {
        NetworkState networkState = this.networkState;
        if (networkState != null) {
            networkState.registerNetChangeReceiver(this.context.getApplicationContext(), false);
            this.networkState.setNetworkListener(null);
            this.networkState = null;
        }
    }

    public void notifyMapEngineCreated(long j, GLMapEngine gLMapEngine) {
        this.engines.put(Long.valueOf(j), gLMapEngine);
    }

    private synchronized void cancelAllAMapDownload() {
        try {
            Iterator it = new ConcurrentHashMap(this.aMapLoaderHashtable).entrySet().iterator();
            while (it.hasNext()) {
                ((AMapLoader) ((Map.Entry) it.next()).getValue()).doCancelAndNotify();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void notifyMapEngineDestroyed(long j) {
        this.engines.remove(Long.valueOf(j));
        cancelAllAMapDownload();
    }

    public synchronized int requireMapDataAsyn(int i, byte[] bArr) {
        if (bArr != null) {
            AMapLoader.ADataRequestParam aDataRequestParam = new AMapLoader.ADataRequestParam();
            int i2 = GLConvertUtil.getInt(bArr, 0);
            aDataRequestParam.requestBaseUrl = GLConvertUtil.getString(bArr, 4, i2);
            int i3 = i2 + 4;
            int i4 = GLConvertUtil.getInt(bArr, i3);
            int i5 = i3 + 4;
            aDataRequestParam.requestUrl = GLConvertUtil.getString(bArr, i5, i4);
            int i6 = i5 + i4;
            aDataRequestParam.handler = GLConvertUtil.getLong(bArr, i6);
            int i7 = i6 + 8;
            aDataRequestParam.nRequestType = GLConvertUtil.getInt(bArr, i7);
            int i8 = i7 + 4;
            int i9 = GLConvertUtil.getInt(bArr, i8);
            int i10 = i8 + 4;
            aDataRequestParam.enCodeString = GLConvertUtil.getSubBytes(bArr, i10, i9);
            aDataRequestParam.nCompress = GLConvertUtil.getInt(bArr, i10 + i9);
            final AMapLoader aMapLoader = new AMapLoader(this.context, aDataRequestParam);
            this.aMapLoaderHashtable.put(Long.valueOf(aDataRequestParam.handler), aMapLoader);
            try {
                dv.a().a(new md() { // from class: com.autonavi.base.ae.gmap.NetworkProxyManager.2
                    @Override // com.amap.api.col.p0003sl.md
                    public void runTask() {
                        try {
                            aMapLoader.doRequest();
                        } catch (Throwable th) {
                            jw.c(th, "download Thread", "AMapLoader doRequest");
                            dx.a(th);
                        }
                    }
                });
            } catch (Throwable th) {
                jw.c(th, "download Thread", "requireMapData");
                dx.a(th);
            }
        }
        return 0;
    }

    public synchronized void receiveNetData(long j, byte[] bArr, int i) {
        if (this.aMapLoaderHashtable.containsKey(Long.valueOf(j))) {
            nativeReceiveNetData(bArr, j, i);
        }
    }

    public synchronized void finishDownLoad(long j) {
        if (this.aMapLoaderHashtable.containsKey(Long.valueOf(j))) {
            nativeFinishDownLoad(j);
            this.aMapLoaderHashtable.remove(Long.valueOf(j));
        }
    }

    public synchronized void netStop(long j, int i) {
        if (this.aMapLoaderHashtable.containsKey(Long.valueOf(j))) {
            nativeFailedDownLoad(j, -1);
            this.aMapLoaderHashtable.remove(Long.valueOf(j));
        }
    }

    public synchronized void netCancel(long j, int i) {
        if (this.aMapLoaderHashtable.containsKey(Long.valueOf(j))) {
            nativeFailedDownLoad(j, -1);
            this.aMapLoaderHashtable.remove(Long.valueOf(j));
        }
    }

    public synchronized void netError(long j, int i, int i2) {
        if (this.aMapLoaderHashtable.containsKey(Long.valueOf(j))) {
            nativeFailedDownLoad(j, i2);
            this.aMapLoaderHashtable.remove(Long.valueOf(j));
            try {
                if (MapsInitializer.getExceptionLogger() != null) {
                    MapsInitializer.getExceptionLogger().onDownloaderException(i, i2);
                }
            } catch (Throwable unused) {
            }
        }
    }
}
