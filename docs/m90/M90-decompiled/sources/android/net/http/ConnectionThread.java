package android.net.http;

import android.app.backup.FullBackup;
import android.content.Context;
import android.net.http.RequestQueue;
import android.os.Process;
import android.os.SystemClock;

/* JADX INFO: loaded from: classes.dex */
class ConnectionThread extends Thread {
    static final int WAIT_TICK = 1000;
    static final int WAIT_TIMEOUT = 5000;
    Connection mConnection;
    private RequestQueue.ConnectionManager mConnectionManager;
    private Context mContext;
    long mCurrentThreadTime;
    private int mId;
    private RequestFeeder mRequestFeeder;
    private volatile boolean mRunning = true;
    long mTotalThreadTime;
    private boolean mWaiting;

    ConnectionThread(Context context, int i, RequestQueue.ConnectionManager connectionManager, RequestFeeder requestFeeder) {
        this.mContext = context;
        setName("http" + i);
        this.mId = i;
        this.mConnectionManager = connectionManager;
        this.mRequestFeeder = requestFeeder;
    }

    void requestStop() {
        synchronized (this.mRequestFeeder) {
            this.mRunning = false;
            this.mRequestFeeder.notify();
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Process.setThreadPriority(1);
        this.mCurrentThreadTime = 0L;
        this.mTotalThreadTime = 0L;
        while (this.mRunning) {
            if (this.mCurrentThreadTime == -1) {
                this.mCurrentThreadTime = SystemClock.currentThreadTimeMillis();
            }
            Request request = this.mRequestFeeder.getRequest();
            if (request == null) {
                synchronized (this.mRequestFeeder) {
                    this.mWaiting = true;
                    try {
                        this.mRequestFeeder.wait();
                    } catch (InterruptedException unused) {
                    }
                    this.mWaiting = false;
                    if (this.mCurrentThreadTime != 0) {
                        this.mCurrentThreadTime = SystemClock.currentThreadTimeMillis();
                    }
                }
            } else {
                Connection connection = this.mConnectionManager.getConnection(this.mContext, request.mHost);
                this.mConnection = connection;
                connection.processRequests(request);
                if (!this.mConnection.getCanPersist() || !this.mConnectionManager.recycleConnection(this.mConnection)) {
                    this.mConnection.closeConnection();
                }
                this.mConnection = null;
                long j = this.mCurrentThreadTime;
                if (j > 0) {
                    long jCurrentThreadTimeMillis = SystemClock.currentThreadTimeMillis();
                    this.mCurrentThreadTime = jCurrentThreadTimeMillis;
                    this.mTotalThreadTime += jCurrentThreadTimeMillis - j;
                }
            }
        }
    }

    @Override // java.lang.Thread
    public synchronized String toString() {
        Connection connection;
        connection = this.mConnection;
        return "cid " + this.mId + " " + (this.mWaiting ? "w" : FullBackup.APK_TREE_TOKEN) + " " + (connection == null ? "" : connection.toString());
    }
}
