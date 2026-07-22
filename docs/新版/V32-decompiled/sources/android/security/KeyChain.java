package android.security;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.security.IKeyChainAliasCallback;
import android.security.IKeyChainService;
import com.android.org.conscrypt.OpenSSLEngine;
import com.android.org.conscrypt.TrustedCertificateStore;
import com.unisound.common.k;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.security.InvalidKeyException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/* JADX INFO: loaded from: classes.dex */
public final class KeyChain {
    public static final String ACCOUNT_TYPE = "com.android.keychain";
    private static final String ACTION_CHOOSER = "com.android.keychain.CHOOSER";
    private static final String ACTION_INSTALL = "android.credentials.INSTALL";
    public static final String ACTION_STORAGE_CHANGED = "android.security.STORAGE_CHANGED";
    public static final String EXTRA_ALIAS = "alias";
    public static final String EXTRA_CERTIFICATE = "CERT";
    public static final String EXTRA_HOST = "host";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_PKCS12 = "PKCS12";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_RESPONSE = "response";
    public static final String EXTRA_SENDER = "sender";
    private static final String TAG = "KeyChain";

    public static Intent createInstallIntent() {
        Intent intent = new Intent("android.credentials.INSTALL");
        intent.setClassName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain");
        return intent;
    }

    public static void choosePrivateKeyAlias(Activity activity, KeyChainAliasCallback keyChainAliasCallback, String[] strArr, Principal[] principalArr, String str, int i, String str2) {
        Objects.requireNonNull(activity, "activity == null");
        Objects.requireNonNull(keyChainAliasCallback, "response == null");
        Intent intent = new Intent(ACTION_CHOOSER);
        intent.putExtra("response", new AliasResponse(keyChainAliasCallback));
        intent.putExtra(EXTRA_HOST, str);
        intent.putExtra(EXTRA_PORT, i);
        intent.putExtra(EXTRA_ALIAS, str2);
        intent.putExtra(EXTRA_SENDER, PendingIntent.getActivity(activity, 0, new Intent(), 0));
        activity.startActivity(intent);
    }

    private static class AliasResponse extends IKeyChainAliasCallback.Stub {
        private final KeyChainAliasCallback keyChainAliasResponse;

        private AliasResponse(KeyChainAliasCallback keyChainAliasCallback) {
            this.keyChainAliasResponse = keyChainAliasCallback;
        }

        @Override // android.security.IKeyChainAliasCallback
        public void alias(String str) {
            this.keyChainAliasResponse.alias(str);
        }
    }

    public static PrivateKey getPrivateKey(Context context, String str) throws InterruptedException, KeyChainException {
        Objects.requireNonNull(str, "alias == null");
        KeyChainConnection keyChainConnectionBind = bind(context);
        try {
            try {
                try {
                    String strRequestPrivateKey = keyChainConnectionBind.getService().requestPrivateKey(str);
                    if (strRequestPrivateKey == null) {
                        throw new KeyChainException("keystore had a problem");
                    }
                    return OpenSSLEngine.getInstance("keystore").getPrivateKeyById(strRequestPrivateKey);
                } catch (InvalidKeyException e) {
                    throw new KeyChainException(e);
                }
            } catch (RemoteException e2) {
                throw new KeyChainException(e2);
            } catch (RuntimeException e3) {
                throw new KeyChainException(e3);
            }
        } finally {
            keyChainConnectionBind.close();
        }
    }

    public static X509Certificate[] getCertificateChain(Context context, String str) throws InterruptedException, KeyChainException {
        Objects.requireNonNull(str, "alias == null");
        KeyChainConnection keyChainConnectionBind = bind(context);
        try {
            try {
                try {
                    byte[] certificate = keyChainConnectionBind.getService().getCertificate(str);
                    if (certificate != null) {
                        List certificateChain = new TrustedCertificateStore().getCertificateChain(toCertificate(certificate));
                        return (X509Certificate[]) certificateChain.toArray(new X509Certificate[certificateChain.size()]);
                    }
                    return null;
                } catch (CertificateException e) {
                    throw new KeyChainException(e);
                }
            } catch (RemoteException e2) {
                throw new KeyChainException(e2);
            } catch (RuntimeException e3) {
                throw new KeyChainException(e3);
            }
        } finally {
            keyChainConnectionBind.close();
        }
    }

    public static boolean isKeyAlgorithmSupported(String str) {
        String upperCase = str.toUpperCase(Locale.US);
        return "DSA".equals(upperCase) || k.i.equals(upperCase) || "RSA".equals(upperCase);
    }

    public static boolean isBoundKeyAlgorithm(String str) {
        if (isKeyAlgorithmSupported(str)) {
            return KeyStore.getInstance().isHardwareBacked(str);
        }
        return false;
    }

    private static X509Certificate toCertificate(byte[] bArr) {
        if (bArr == null) {
            throw new IllegalArgumentException("bytes == null");
        }
        try {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr));
        } catch (CertificateException e) {
            throw new AssertionError(e);
        }
    }

    public static final class KeyChainConnection implements Closeable {
        private final Context context;
        private final IKeyChainService service;
        private final ServiceConnection serviceConnection;

        private KeyChainConnection(Context context, ServiceConnection serviceConnection, IKeyChainService iKeyChainService) {
            this.context = context;
            this.serviceConnection = serviceConnection;
            this.service = iKeyChainService;
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.context.unbindService(this.serviceConnection);
        }

        public IKeyChainService getService() {
            return this.service;
        }
    }

    public static KeyChainConnection bind(Context context) throws InterruptedException {
        Objects.requireNonNull(context, "context == null");
        ensureNotOnMainThread(context);
        final LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(1);
        ServiceConnection serviceConnection = new ServiceConnection() { // from class: android.security.KeyChain.1
            volatile boolean mConnectedAtLeastOnce = false;

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                if (this.mConnectedAtLeastOnce) {
                    return;
                }
                this.mConnectedAtLeastOnce = true;
                try {
                    linkedBlockingQueue.put(IKeyChainService.Stub.asInterface(iBinder));
                } catch (InterruptedException unused) {
                }
            }
        };
        Intent intent = new Intent(IKeyChainService.class.getName());
        intent.setComponent(intent.resolveSystemService(context.getPackageManager(), 0));
        if (!context.bindService(intent, serviceConnection, 1)) {
            throw new AssertionError("could not bind to KeyChainService");
        }
        return new KeyChainConnection(context, serviceConnection, (IKeyChainService) linkedBlockingQueue.take());
    }

    private static void ensureNotOnMainThread(Context context) {
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper != null && looperMyLooper == context.getMainLooper()) {
            throw new IllegalStateException("calling this from your main thread can lead to deadlock");
        }
    }
}
