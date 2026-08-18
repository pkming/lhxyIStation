package android.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.IMediaScannerListener;
import android.media.IMediaScannerService;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public class MediaScannerConnection implements ServiceConnection {
    private static final String TAG = "MediaScannerConnection";
    private MediaScannerConnectionClient mClient;
    private boolean mConnected;
    private Context mContext;
    private final IMediaScannerListener.Stub mListener = new IMediaScannerListener.Stub() { // from class: android.media.MediaScannerConnection.1
        @Override // android.media.IMediaScannerListener
        public void scanCompleted(String str, Uri uri) {
            MediaScannerConnectionClient mediaScannerConnectionClient = MediaScannerConnection.this.mClient;
            if (mediaScannerConnectionClient != null) {
                mediaScannerConnectionClient.onScanCompleted(str, uri);
            }
        }
    };
    private IMediaScannerService mService;

    public interface MediaScannerConnectionClient extends OnScanCompletedListener {
        void onMediaScannerConnected();

        @Override // android.media.MediaScannerConnection.OnScanCompletedListener
        void onScanCompleted(String str, Uri uri);
    }

    public interface OnScanCompletedListener {
        void onScanCompleted(String str, Uri uri);
    }

    public MediaScannerConnection(Context context, MediaScannerConnectionClient mediaScannerConnectionClient) {
        this.mContext = context;
        this.mClient = mediaScannerConnectionClient;
    }

    public void connect() {
        synchronized (this) {
            if (!this.mConnected) {
                Intent intent = new Intent(IMediaScannerService.class.getName());
                intent.setComponent(new ComponentName("com.android.providers.media", "com.android.providers.media.MediaScannerService"));
                this.mContext.bindService(intent, this, 1);
                this.mConnected = true;
            }
        }
    }

    public void disconnect() {
        synchronized (this) {
            if (this.mConnected) {
                try {
                    this.mContext.unbindService(this);
                } catch (IllegalArgumentException unused) {
                }
                this.mConnected = false;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:8:0x000b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean isConnected() {
        /*
            r1 = this;
            monitor-enter(r1)
            android.media.IMediaScannerService r0 = r1.mService     // Catch: java.lang.Throwable -> Le
            if (r0 == 0) goto Lb
            boolean r0 = r1.mConnected     // Catch: java.lang.Throwable -> Le
            if (r0 == 0) goto Lb
            r0 = 1
            goto Lc
        Lb:
            r0 = 0
        Lc:
            monitor-exit(r1)
            return r0
        Le:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaScannerConnection.isConnected():boolean");
    }

    public void scanFile(String str, String str2) {
        synchronized (this) {
            IMediaScannerService iMediaScannerService = this.mService;
            if (iMediaScannerService == null || !this.mConnected) {
                throw new IllegalStateException("not connected to MediaScannerService");
            }
            try {
                iMediaScannerService.requestScanFile(str, str2, this.mListener);
            } catch (RemoteException unused) {
            }
        }
    }

    static class ClientProxy implements MediaScannerConnectionClient {
        final OnScanCompletedListener mClient;
        MediaScannerConnection mConnection;
        final String[] mMimeTypes;
        int mNextPath;
        final String[] mPaths;

        ClientProxy(String[] strArr, String[] strArr2, OnScanCompletedListener onScanCompletedListener) {
            this.mPaths = strArr;
            this.mMimeTypes = strArr2;
            this.mClient = onScanCompletedListener;
        }

        @Override // android.media.MediaScannerConnection.MediaScannerConnectionClient
        public void onMediaScannerConnected() {
            scanNextPath();
        }

        @Override // android.media.MediaScannerConnection.MediaScannerConnectionClient, android.media.MediaScannerConnection.OnScanCompletedListener
        public void onScanCompleted(String str, Uri uri) {
            OnScanCompletedListener onScanCompletedListener = this.mClient;
            if (onScanCompletedListener != null) {
                onScanCompletedListener.onScanCompleted(str, uri);
            }
            scanNextPath();
        }

        void scanNextPath() {
            int i = this.mNextPath;
            String[] strArr = this.mPaths;
            if (i >= strArr.length) {
                this.mConnection.disconnect();
                return;
            }
            String[] strArr2 = this.mMimeTypes;
            this.mConnection.scanFile(strArr[i], strArr2 != null ? strArr2[i] : null);
            this.mNextPath++;
        }
    }

    public static void scanFile(Context context, String[] strArr, String[] strArr2, OnScanCompletedListener onScanCompletedListener) {
        ClientProxy clientProxy = new ClientProxy(strArr, strArr2, onScanCompletedListener);
        MediaScannerConnection mediaScannerConnection = new MediaScannerConnection(context, clientProxy);
        clientProxy.mConnection = mediaScannerConnection;
        mediaScannerConnection.connect();
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MediaScannerConnectionClient mediaScannerConnectionClient;
        synchronized (this) {
            IMediaScannerService iMediaScannerServiceAsInterface = IMediaScannerService.Stub.asInterface(iBinder);
            this.mService = iMediaScannerServiceAsInterface;
            if (iMediaScannerServiceAsInterface != null && (mediaScannerConnectionClient = this.mClient) != null) {
                mediaScannerConnectionClient.onMediaScannerConnected();
            }
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        synchronized (this) {
            this.mService = null;
        }
    }
}
