package android.database;

import android.database.IContentObserver;
import android.net.Uri;
import android.os.Handler;

/* JADX INFO: loaded from: classes.dex */
public abstract class ContentObserver {
    Handler mHandler;
    private final Object mLock = new Object();
    private Transport mTransport;

    public boolean deliverSelfNotifications() {
        return false;
    }

    public void onChange(boolean z) {
    }

    public ContentObserver(Handler handler) {
        this.mHandler = handler;
    }

    public IContentObserver getContentObserver() {
        Transport transport;
        synchronized (this.mLock) {
            if (this.mTransport == null) {
                this.mTransport = new Transport(this);
            }
            transport = this.mTransport;
        }
        return transport;
    }

    public IContentObserver releaseContentObserver() {
        Transport transport;
        synchronized (this.mLock) {
            transport = this.mTransport;
            if (transport != null) {
                transport.releaseContentObserver();
                this.mTransport = null;
            }
        }
        return transport;
    }

    public void onChange(boolean z, Uri uri) {
        onChange(z);
    }

    @Deprecated
    public final void dispatchChange(boolean z) {
        dispatchChange(z, null);
    }

    public final void dispatchChange(boolean z, Uri uri) {
        Handler handler = this.mHandler;
        if (handler == null) {
            onChange(z, uri);
        } else {
            handler.post(new NotificationRunnable(z, uri));
        }
    }

    private final class NotificationRunnable implements Runnable {
        private final boolean mSelfChange;
        private final Uri mUri;

        public NotificationRunnable(boolean z, Uri uri) {
            this.mSelfChange = z;
            this.mUri = uri;
        }

        @Override // java.lang.Runnable
        public void run() {
            ContentObserver.this.onChange(this.mSelfChange, this.mUri);
        }
    }

    private static final class Transport extends IContentObserver.Stub {
        private ContentObserver mContentObserver;

        public Transport(ContentObserver contentObserver) {
            this.mContentObserver = contentObserver;
        }

        @Override // android.database.IContentObserver
        public void onChange(boolean z, Uri uri) {
            ContentObserver contentObserver = this.mContentObserver;
            if (contentObserver != null) {
                contentObserver.dispatchChange(z, uri);
            }
        }

        public void releaseContentObserver() {
            this.mContentObserver = null;
        }
    }
}
