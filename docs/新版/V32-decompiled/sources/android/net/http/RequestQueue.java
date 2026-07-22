package android.net.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.WebAddress;
import android.util.Log;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.http.HttpHost;

/* JADX INFO: loaded from: classes.dex */
public class RequestQueue implements RequestFeeder {
    private static final int CONNECTION_COUNT = 4;
    private final ActivePool mActivePool;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final LinkedHashMap<HttpHost, LinkedList<Request>> mPending;
    private BroadcastReceiver mProxyChangeReceiver;
    private HttpHost mProxyHost;

    interface ConnectionManager {
        Connection getConnection(Context context, HttpHost httpHost);

        HttpHost getProxyHost();

        boolean recycleConnection(Connection connection);
    }

    class ActivePool implements ConnectionManager {
        private int mConnectionCount;
        IdleCache mIdleCache = new IdleCache();
        ConnectionThread[] mThreads;
        private int mTotalConnection;
        private int mTotalRequest;

        static /* synthetic */ int access$408(ActivePool activePool) {
            int i = activePool.mTotalRequest;
            activePool.mTotalRequest = i + 1;
            return i;
        }

        ActivePool(int i) {
            this.mConnectionCount = i;
            this.mThreads = new ConnectionThread[i];
            for (int i2 = 0; i2 < this.mConnectionCount; i2++) {
                this.mThreads[i2] = new ConnectionThread(RequestQueue.this.mContext, i2, this, RequestQueue.this);
            }
        }

        void startup() {
            for (int i = 0; i < this.mConnectionCount; i++) {
                this.mThreads[i].start();
            }
        }

        void shutdown() {
            for (int i = 0; i < this.mConnectionCount; i++) {
                this.mThreads[i].requestStop();
            }
        }

        void startConnectionThread() {
            synchronized (RequestQueue.this) {
                RequestQueue.this.notify();
            }
        }

        public void startTiming() {
            for (int i = 0; i < this.mConnectionCount; i++) {
                ConnectionThread connectionThread = this.mThreads[i];
                connectionThread.mCurrentThreadTime = -1L;
                connectionThread.mTotalThreadTime = 0L;
            }
            this.mTotalRequest = 0;
            this.mTotalConnection = 0;
        }

        public void stopTiming() {
            int i = 0;
            for (int i2 = 0; i2 < this.mConnectionCount; i2++) {
                ConnectionThread connectionThread = this.mThreads[i2];
                if (connectionThread.mCurrentThreadTime != -1) {
                    i = (int) (((long) i) + connectionThread.mTotalThreadTime);
                }
                connectionThread.mCurrentThreadTime = 0L;
            }
            Log.d("Http", "Http thread used " + i + " ms  for " + this.mTotalRequest + " requests and " + this.mTotalConnection + " new connections");
        }

        void logState() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.mConnectionCount; i++) {
                sb.append(this.mThreads[i] + "\n");
            }
            HttpLog.v(sb.toString());
        }

        @Override // android.net.http.RequestQueue.ConnectionManager
        public HttpHost getProxyHost() {
            return RequestQueue.this.mProxyHost;
        }

        void disablePersistence() {
            for (int i = 0; i < this.mConnectionCount; i++) {
                Connection connection = this.mThreads[i].mConnection;
                if (connection != null) {
                    connection.setCanPersist(false);
                }
            }
            this.mIdleCache.clear();
        }

        ConnectionThread getThread(HttpHost httpHost) {
            synchronized (RequestQueue.this) {
                int i = 0;
                while (true) {
                    ConnectionThread[] connectionThreadArr = this.mThreads;
                    if (i >= connectionThreadArr.length) {
                        return null;
                    }
                    ConnectionThread connectionThread = connectionThreadArr[i];
                    Connection connection = connectionThread.mConnection;
                    if (connection != null && connection.mHost.equals(httpHost)) {
                        return connectionThread;
                    }
                    i++;
                }
            }
        }

        @Override // android.net.http.RequestQueue.ConnectionManager
        public Connection getConnection(Context context, HttpHost httpHost) {
            HttpHost httpHostDetermineHost = RequestQueue.this.determineHost(httpHost);
            Connection connection = this.mIdleCache.getConnection(httpHostDetermineHost);
            if (connection != null) {
                return connection;
            }
            this.mTotalConnection++;
            return Connection.getConnection(RequestQueue.this.mContext, httpHostDetermineHost, RequestQueue.this.mProxyHost, RequestQueue.this);
        }

        @Override // android.net.http.RequestQueue.ConnectionManager
        public boolean recycleConnection(Connection connection) {
            return this.mIdleCache.cacheConnection(connection.getHost(), connection);
        }
    }

    public RequestQueue(Context context) {
        this(context, 4);
    }

    public RequestQueue(Context context, int i) {
        this.mProxyHost = null;
        this.mContext = context;
        this.mPending = new LinkedHashMap<>(32);
        ActivePool activePool = new ActivePool(i);
        this.mActivePool = activePool;
        activePool.startup();
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public synchronized void enablePlatformNotifications() {
        if (this.mProxyChangeReceiver == null) {
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: android.net.http.RequestQueue.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    RequestQueue.this.setProxyConfig();
                }
            };
            this.mProxyChangeReceiver = broadcastReceiver;
            this.mContext.registerReceiver(broadcastReceiver, new IntentFilter(Proxy.PROXY_CHANGE_ACTION));
        }
        setProxyConfig();
    }

    public synchronized void disablePlatformNotifications() {
        BroadcastReceiver broadcastReceiver = this.mProxyChangeReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
            this.mProxyChangeReceiver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void setProxyConfig() {
        String host;
        NetworkInfo activeNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
        if ((activeNetworkInfo != null && activeNetworkInfo.getType() == 1) || (host = Proxy.getHost(this.mContext)) == null) {
            this.mProxyHost = null;
        } else {
            this.mActivePool.disablePersistence();
            this.mProxyHost = new HttpHost(host, Proxy.getPort(this.mContext), "http");
        }
    }

    public HttpHost getProxyHost() {
        return this.mProxyHost;
    }

    public RequestHandle queueRequest(String str, String str2, Map<String, String> map, EventHandler eventHandler, InputStream inputStream, int i) {
        return queueRequest(str, new WebAddress(str), str2, map, eventHandler, inputStream, i);
    }

    public RequestHandle queueRequest(String str, WebAddress webAddress, String str2, Map<String, String> map, EventHandler eventHandler, InputStream inputStream, int i) {
        Request request = new Request(str2, new HttpHost(webAddress.getHost(), webAddress.getPort(), webAddress.getScheme()), this.mProxyHost, webAddress.getPath(), inputStream, i, eventHandler == null ? new LoggingEventHandler() : eventHandler, map);
        queueRequest(request, false);
        ActivePool.access$408(this.mActivePool);
        this.mActivePool.startConnectionThread();
        return new RequestHandle(this, str, webAddress, str2, map, inputStream, i, request);
    }

    private static class SyncFeeder implements RequestFeeder {
        private Request mRequest;

        SyncFeeder() {
        }

        @Override // android.net.http.RequestFeeder
        public Request getRequest() {
            Request request = this.mRequest;
            this.mRequest = null;
            return request;
        }

        @Override // android.net.http.RequestFeeder
        public Request getRequest(HttpHost httpHost) {
            return getRequest();
        }

        @Override // android.net.http.RequestFeeder
        public boolean haveRequest(HttpHost httpHost) {
            return this.mRequest != null;
        }

        @Override // android.net.http.RequestFeeder
        public void requeueRequest(Request request) {
            this.mRequest = request;
        }
    }

    public RequestHandle queueSynchronousRequest(String str, WebAddress webAddress, String str2, Map<String, String> map, EventHandler eventHandler, InputStream inputStream, int i) {
        HttpHost httpHost = new HttpHost(webAddress.getHost(), webAddress.getPort(), webAddress.getScheme());
        return new RequestHandle(this, str, webAddress, str2, map, inputStream, i, new Request(str2, httpHost, this.mProxyHost, webAddress.getPath(), inputStream, i, eventHandler, map), Connection.getConnection(this.mContext, determineHost(httpHost), this.mProxyHost, new SyncFeeder()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HttpHost determineHost(HttpHost httpHost) {
        return (this.mProxyHost == null || "https".equals(httpHost.getSchemeName())) ? httpHost : this.mProxyHost;
    }

    synchronized boolean requestsPending() {
        return !this.mPending.isEmpty();
    }

    synchronized void dump() {
        HttpLog.v("dump()");
        StringBuilder sb = new StringBuilder();
        if (!this.mPending.isEmpty()) {
            Iterator<Map.Entry<HttpHost, LinkedList<Request>>> it = this.mPending.entrySet().iterator();
            int i = 0;
            while (it.hasNext()) {
                Map.Entry<HttpHost, LinkedList<Request>> next = it.next();
                int i2 = i + 1;
                StringBuilder sb2 = new StringBuilder("p" + i + " " + next.getKey().getHostName() + " ");
                next.getValue().listIterator(0);
                while (it.hasNext()) {
                    sb2.append(((Request) it.next()) + " ");
                }
                sb.append((CharSequence) sb2);
                sb.append("\n");
                i = i2;
            }
        }
        HttpLog.v(sb.toString());
    }

    @Override // android.net.http.RequestFeeder
    public synchronized Request getRequest() {
        return this.mPending.isEmpty() ? null : removeFirst(this.mPending);
    }

    @Override // android.net.http.RequestFeeder
    public synchronized Request getRequest(HttpHost httpHost) {
        Request request;
        request = null;
        if (this.mPending.containsKey(httpHost)) {
            LinkedList<Request> linkedList = this.mPending.get(httpHost);
            Request requestRemoveFirst = linkedList.removeFirst();
            if (linkedList.isEmpty()) {
                this.mPending.remove(httpHost);
            }
            request = requestRemoveFirst;
        }
        return request;
    }

    @Override // android.net.http.RequestFeeder
    public synchronized boolean haveRequest(HttpHost httpHost) {
        return this.mPending.containsKey(httpHost);
    }

    @Override // android.net.http.RequestFeeder
    public void requeueRequest(Request request) {
        queueRequest(request, true);
    }

    public void shutdown() {
        this.mActivePool.shutdown();
    }

    protected synchronized void queueRequest(Request request, boolean z) {
        LinkedList<Request> linkedList;
        HttpHost httpHost = request.mProxyHost == null ? request.mHost : request.mProxyHost;
        if (this.mPending.containsKey(httpHost)) {
            linkedList = this.mPending.get(httpHost);
        } else {
            LinkedList<Request> linkedList2 = new LinkedList<>();
            this.mPending.put(httpHost, linkedList2);
            linkedList = linkedList2;
        }
        if (z) {
            linkedList.addFirst(request);
        } else {
            linkedList.add(request);
        }
    }

    public void startTiming() {
        this.mActivePool.startTiming();
    }

    public void stopTiming() {
        this.mActivePool.stopTiming();
    }

    private Request removeFirst(LinkedHashMap<HttpHost, LinkedList<Request>> linkedHashMap) {
        Iterator<Map.Entry<HttpHost, LinkedList<Request>>> it = linkedHashMap.entrySet().iterator();
        if (!it.hasNext()) {
            return null;
        }
        Map.Entry<HttpHost, LinkedList<Request>> next = it.next();
        LinkedList<Request> value = next.getValue();
        Request requestRemoveFirst = value.removeFirst();
        if (!value.isEmpty()) {
            return requestRemoveFirst;
        }
        linkedHashMap.remove(next.getKey());
        return requestRemoveFirst;
    }
}
