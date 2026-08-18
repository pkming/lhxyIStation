package android.content;

import android.accounts.Account;
import android.content.ISyncAdapter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.Trace;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractThreadedSyncAdapter {

    @Deprecated
    public static final int LOG_SYNC_DETAILS = 2743;
    private boolean mAllowParallelSyncs;
    private final boolean mAutoInitialize;
    private final Context mContext;
    private final ISyncAdapterImpl mISyncAdapterImpl;
    private final AtomicInteger mNumSyncStarts;
    private final Object mSyncThreadLock;
    private final HashMap<Account, SyncThread> mSyncThreads;

    public abstract void onPerformSync(Account account, Bundle bundle, String str, ContentProviderClient contentProviderClient, SyncResult syncResult);

    public AbstractThreadedSyncAdapter(Context context, boolean z) {
        this(context, z, false);
    }

    public AbstractThreadedSyncAdapter(Context context, boolean z, boolean z2) {
        this.mSyncThreads = new HashMap<>();
        this.mSyncThreadLock = new Object();
        this.mContext = context;
        this.mISyncAdapterImpl = new ISyncAdapterImpl();
        this.mNumSyncStarts = new AtomicInteger(0);
        this.mAutoInitialize = z;
        this.mAllowParallelSyncs = z2;
    }

    public Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Account toSyncKey(Account account) {
        if (this.mAllowParallelSyncs) {
            return account;
        }
        return null;
    }

    private class ISyncAdapterImpl extends ISyncAdapter.Stub {
        private ISyncAdapterImpl() {
        }

        @Override // android.content.ISyncAdapter
        public void startSync(ISyncContext iSyncContext, String str, Account account, Bundle bundle) {
            SyncContext syncContext = new SyncContext(iSyncContext);
            Account syncKey = AbstractThreadedSyncAdapter.this.toSyncKey(account);
            synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                boolean z = false;
                if (AbstractThreadedSyncAdapter.this.mSyncThreads.containsKey(syncKey)) {
                    z = true;
                } else {
                    if (AbstractThreadedSyncAdapter.this.mAutoInitialize && bundle != null && bundle.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false)) {
                        try {
                            if (ContentResolver.getIsSyncable(account, str) < 0) {
                                ContentResolver.setIsSyncable(account, str, 1);
                            }
                            return;
                        } finally {
                            syncContext.onFinished(new SyncResult());
                        }
                    }
                    SyncThread syncThread = new SyncThread("SyncAdapterThread-" + AbstractThreadedSyncAdapter.this.mNumSyncStarts.incrementAndGet(), syncContext, str, account, bundle);
                    AbstractThreadedSyncAdapter.this.mSyncThreads.put(syncKey, syncThread);
                    syncThread.start();
                }
                if (z) {
                    syncContext.onFinished(SyncResult.ALREADY_IN_PROGRESS);
                }
            }
        }

        @Override // android.content.ISyncAdapter
        public void cancelSync(ISyncContext iSyncContext) {
            SyncThread syncThread;
            synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                Iterator it = AbstractThreadedSyncAdapter.this.mSyncThreads.values().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        syncThread = null;
                        break;
                    } else {
                        syncThread = (SyncThread) it.next();
                        if (syncThread.mSyncContext.getSyncContextBinder() == iSyncContext.asBinder()) {
                            break;
                        }
                    }
                }
            }
            if (syncThread != null) {
                if (AbstractThreadedSyncAdapter.this.mAllowParallelSyncs) {
                    AbstractThreadedSyncAdapter.this.onSyncCanceled(syncThread);
                } else {
                    AbstractThreadedSyncAdapter.this.onSyncCanceled();
                }
            }
        }

        @Override // android.content.ISyncAdapter
        public void initialize(Account account, String str) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
            startSync(null, str, account, bundle);
        }
    }

    private class SyncThread extends Thread {
        private final Account mAccount;
        private final String mAuthority;
        private final Bundle mExtras;
        private final SyncContext mSyncContext;
        private final Account mThreadsKey;

        private SyncThread(String str, SyncContext syncContext, String str2, Account account, Bundle bundle) {
            super(str);
            this.mSyncContext = syncContext;
            this.mAuthority = str2;
            this.mAccount = account;
            this.mExtras = bundle;
            this.mThreadsKey = AbstractThreadedSyncAdapter.this.toSyncKey(account);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws Throwable {
            ContentProviderClient contentProviderClientAcquireContentProviderClient;
            Throwable th;
            Process.setThreadPriority(10);
            Trace.traceBegin(128L, this.mAuthority);
            SyncResult syncResult = new SyncResult();
            try {
                if (!isCanceled()) {
                    contentProviderClientAcquireContentProviderClient = AbstractThreadedSyncAdapter.this.mContext.getContentResolver().acquireContentProviderClient(this.mAuthority);
                    try {
                        if (contentProviderClientAcquireContentProviderClient != null) {
                            AbstractThreadedSyncAdapter.this.onPerformSync(this.mAccount, this.mExtras, this.mAuthority, contentProviderClientAcquireContentProviderClient, syncResult);
                        } else {
                            syncResult.databaseError = true;
                        }
                        Trace.traceEnd(128L);
                        if (contentProviderClientAcquireContentProviderClient != null) {
                            contentProviderClientAcquireContentProviderClient.release();
                        }
                        if (!isCanceled()) {
                            this.mSyncContext.onFinished(syncResult);
                        }
                        synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                            AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                        }
                        return;
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } else {
                    Trace.traceEnd(128L);
                    if (!isCanceled()) {
                        this.mSyncContext.onFinished(syncResult);
                    }
                    synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                        AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                    }
                    return;
                }
            } catch (Throwable th3) {
                contentProviderClientAcquireContentProviderClient = null;
                th = th3;
            }
            Trace.traceEnd(128L);
            if (contentProviderClientAcquireContentProviderClient != null) {
                contentProviderClientAcquireContentProviderClient.release();
            }
            if (!isCanceled()) {
                this.mSyncContext.onFinished(syncResult);
            }
            synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
            }
            throw th;
        }

        private boolean isCanceled() {
            return Thread.currentThread().isInterrupted();
        }
    }

    public final IBinder getSyncAdapterBinder() {
        return this.mISyncAdapterImpl.asBinder();
    }

    public void onSyncCanceled() {
        SyncThread syncThread;
        synchronized (this.mSyncThreadLock) {
            syncThread = this.mSyncThreads.get(null);
        }
        if (syncThread != null) {
            syncThread.interrupt();
        }
    }

    public void onSyncCanceled(Thread thread) {
        thread.interrupt();
    }
}
