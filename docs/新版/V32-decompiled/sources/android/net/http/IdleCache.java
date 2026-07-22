package android.net.http;

import android.os.Process;
import android.os.SystemClock;
import org.apache.http.HttpHost;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes.dex */
class IdleCache {
    private static final int CHECK_INTERVAL = 2000;
    private static final int EMPTY_CHECK_MAX = 5;
    private static final int IDLE_CACHE_MAX = 8;
    private static final int TIMEOUT = 6000;
    private Entry[] mEntries = new Entry[8];
    private int mCount = 0;
    private IdleReaper mThread = null;
    private int mCached = 0;
    private int mReused = 0;

    class Entry {
        Connection mConnection;
        HttpHost mHost;
        long mTimeout;

        Entry() {
        }
    }

    IdleCache() {
        for (int i = 0; i < 8; i++) {
            this.mEntries[i] = new Entry();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0028, code lost:
    
        if (r8.mThread != null) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x002a, code lost:
    
        r9 = new android.net.http.IdleCache.IdleReaper(r8, null);
        r8.mThread = r9;
        r9.start();
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0035, code lost:
    
        r2 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0018, code lost:
    
        r6.mHost = r9;
        r6.mConnection = r10;
        r6.mTimeout = r4 + 6000;
        r8.mCount++;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    synchronized boolean cacheConnection(org.apache.http.HttpHost r9, android.net.http.Connection r10) {
        /*
            r8 = this;
            monitor-enter(r8)
            int r0 = r8.mCount     // Catch: java.lang.Throwable -> L3c
            r1 = 8
            r2 = 0
            r3 = 1
            if (r0 >= r1) goto L3a
            long r4 = android.os.SystemClock.uptimeMillis()     // Catch: java.lang.Throwable -> L3c
            r0 = r2
        Le:
            if (r0 >= r1) goto L3a
            android.net.http.IdleCache$Entry[] r6 = r8.mEntries     // Catch: java.lang.Throwable -> L3c
            r6 = r6[r0]     // Catch: java.lang.Throwable -> L3c
            org.apache.http.HttpHost r7 = r6.mHost     // Catch: java.lang.Throwable -> L3c
            if (r7 != 0) goto L37
            r6.mHost = r9     // Catch: java.lang.Throwable -> L3c
            r6.mConnection = r10     // Catch: java.lang.Throwable -> L3c
            r9 = 6000(0x1770, double:2.9644E-320)
            long r4 = r4 + r9
            r6.mTimeout = r4     // Catch: java.lang.Throwable -> L3c
            int r9 = r8.mCount     // Catch: java.lang.Throwable -> L3c
            int r9 = r9 + r3
            r8.mCount = r9     // Catch: java.lang.Throwable -> L3c
            android.net.http.IdleCache$IdleReaper r9 = r8.mThread     // Catch: java.lang.Throwable -> L3c
            if (r9 != 0) goto L35
            android.net.http.IdleCache$IdleReaper r9 = new android.net.http.IdleCache$IdleReaper     // Catch: java.lang.Throwable -> L3c
            r10 = 0
            r9.<init>()     // Catch: java.lang.Throwable -> L3c
            r8.mThread = r9     // Catch: java.lang.Throwable -> L3c
            r9.start()     // Catch: java.lang.Throwable -> L3c
        L35:
            r2 = r3
            goto L3a
        L37:
            int r0 = r0 + 1
            goto Le
        L3a:
            monitor-exit(r8)
            return r2
        L3c:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.http.IdleCache.cacheConnection(org.apache.http.HttpHost, android.net.http.Connection):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0019, code lost:
    
        r5 = r2.mConnection;
        r2.mHost = null;
        r2.mConnection = null;
        r4.mCount--;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0025, code lost:
    
        r1 = r5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    synchronized android.net.http.Connection getConnection(org.apache.http.HttpHost r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            int r0 = r4.mCount     // Catch: java.lang.Throwable -> L2c
            r1 = 0
            if (r0 <= 0) goto L2a
            r0 = 0
        L7:
            r2 = 8
            if (r0 >= r2) goto L2a
            android.net.http.IdleCache$Entry[] r2 = r4.mEntries     // Catch: java.lang.Throwable -> L2c
            r2 = r2[r0]     // Catch: java.lang.Throwable -> L2c
            org.apache.http.HttpHost r3 = r2.mHost     // Catch: java.lang.Throwable -> L2c
            if (r3 == 0) goto L27
            boolean r3 = r3.equals(r5)     // Catch: java.lang.Throwable -> L2c
            if (r3 == 0) goto L27
            android.net.http.Connection r5 = r2.mConnection     // Catch: java.lang.Throwable -> L2c
            r2.mHost = r1     // Catch: java.lang.Throwable -> L2c
            r2.mConnection = r1     // Catch: java.lang.Throwable -> L2c
            int r0 = r4.mCount     // Catch: java.lang.Throwable -> L2c
            int r0 = r0 + (-1)
            r4.mCount = r0     // Catch: java.lang.Throwable -> L2c
            r1 = r5
            goto L2a
        L27:
            int r0 = r0 + 1
            goto L7
        L2a:
            monitor-exit(r4)
            return r1
        L2c:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.http.IdleCache.getConnection(org.apache.http.HttpHost):android.net.http.Connection");
    }

    synchronized void clear() {
        for (int i = 0; this.mCount > 0 && i < 8; i++) {
            Entry entry = this.mEntries[i];
            if (entry.mHost != null) {
                entry.mHost = null;
                entry.mConnection.closeConnection();
                entry.mConnection = null;
                this.mCount--;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void clearIdle() {
        if (this.mCount > 0) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            for (int i = 0; i < 8; i++) {
                Entry entry = this.mEntries[i];
                if (entry.mHost != null && jUptimeMillis > entry.mTimeout) {
                    entry.mHost = null;
                    entry.mConnection.closeConnection();
                    entry.mConnection = null;
                    this.mCount--;
                }
            }
        }
    }

    private class IdleReaper extends Thread {
        private IdleReaper() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int i;
            setName("IdleReaper");
            Process.setThreadPriority(10);
            synchronized (IdleCache.this) {
                while (true) {
                    for (0; i < 5; i + 1) {
                        try {
                            IdleCache.this.wait(FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
                        } catch (InterruptedException unused) {
                        }
                        i = IdleCache.this.mCount == 0 ? i + 1 : 0;
                    }
                    IdleCache.this.mThread = null;
                    IdleCache.this.clearIdle();
                }
            }
        }
    }
}
