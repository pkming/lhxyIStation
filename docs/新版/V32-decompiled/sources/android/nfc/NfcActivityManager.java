package android.nfc;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.nfc.IAppCallback;
import android.nfc.NfcAdapter;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class NfcActivityManager extends IAppCallback.Stub implements Application.ActivityLifecycleCallbacks {
    static final Boolean DBG = false;
    static final String TAG = "NFC";
    final NfcAdapter mAdapter;
    final NfcEvent mDefaultEvent;
    final List<NfcActivityState> mActivities = new LinkedList();
    final List<NfcApplicationState> mApps = new ArrayList(1);

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
    }

    class NfcApplicationState {
        final Application app;
        int refCount = 0;

        public NfcApplicationState(Application application) {
            this.app = application;
        }

        public void register() {
            int i = this.refCount + 1;
            this.refCount = i;
            if (i == 1) {
                this.app.registerActivityLifecycleCallbacks(NfcActivityManager.this);
            }
        }

        public void unregister() {
            int i = this.refCount - 1;
            this.refCount = i;
            if (i == 0) {
                this.app.unregisterActivityLifecycleCallbacks(NfcActivityManager.this);
            } else if (i < 0) {
                Log.e(NfcActivityManager.TAG, "-ve refcount for " + this.app);
            }
        }
    }

    NfcApplicationState findAppState(Application application) {
        for (NfcApplicationState nfcApplicationState : this.mApps) {
            if (nfcApplicationState.app == application) {
                return nfcApplicationState;
            }
        }
        return null;
    }

    void registerApplication(Application application) {
        NfcApplicationState nfcApplicationStateFindAppState = findAppState(application);
        if (nfcApplicationStateFindAppState == null) {
            nfcApplicationStateFindAppState = new NfcApplicationState(application);
            this.mApps.add(nfcApplicationStateFindAppState);
        }
        nfcApplicationStateFindAppState.register();
    }

    void unregisterApplication(Application application) {
        NfcApplicationState nfcApplicationStateFindAppState = findAppState(application);
        if (nfcApplicationStateFindAppState == null) {
            Log.e(TAG, "app was not registered " + application);
        } else {
            nfcApplicationStateFindAppState.unregister();
        }
    }

    class NfcActivityState {
        Activity activity;
        boolean resumed;
        Binder token;
        NdefMessage ndefMessage = null;
        NfcAdapter.CreateNdefMessageCallback ndefMessageCallback = null;
        NfcAdapter.OnNdefPushCompleteCallback onNdefPushCompleteCallback = null;
        NfcAdapter.CreateBeamUrisCallback uriCallback = null;
        Uri[] uris = null;
        int flags = 0;
        int readerModeFlags = 0;
        NfcAdapter.ReaderCallback readerCallback = null;
        Bundle readerModeExtras = null;

        public NfcActivityState(Activity activity) {
            this.resumed = false;
            if (activity.getWindow().isDestroyed()) {
                throw new IllegalStateException("activity is already destroyed");
            }
            this.resumed = activity.isResumed();
            this.activity = activity;
            this.token = new Binder();
            NfcActivityManager.this.registerApplication(activity.getApplication());
        }

        public void destroy() {
            NfcActivityManager.this.unregisterApplication(this.activity.getApplication());
            this.resumed = false;
            this.activity = null;
            this.ndefMessage = null;
            this.ndefMessageCallback = null;
            this.onNdefPushCompleteCallback = null;
            this.uriCallback = null;
            this.uris = null;
            this.readerModeFlags = 0;
            this.token = null;
        }

        public String toString() {
            StringBuilder sbAppend = new StringBuilder("[").append(" ");
            sbAppend.append(this.ndefMessage).append(" ").append(this.ndefMessageCallback).append(" ");
            sbAppend.append(this.uriCallback).append(" ");
            Uri[] uriArr = this.uris;
            if (uriArr != null) {
                for (Uri uri : uriArr) {
                    sbAppend.append(this.onNdefPushCompleteCallback).append(" ").append(uri).append("]");
                }
            }
            return sbAppend.toString();
        }
    }

    synchronized NfcActivityState findActivityState(Activity activity) {
        for (NfcActivityState nfcActivityState : this.mActivities) {
            if (nfcActivityState.activity == activity) {
                return nfcActivityState;
            }
        }
        return null;
    }

    synchronized NfcActivityState getActivityState(Activity activity) {
        NfcActivityState nfcActivityStateFindActivityState;
        nfcActivityStateFindActivityState = findActivityState(activity);
        if (nfcActivityStateFindActivityState == null) {
            nfcActivityStateFindActivityState = new NfcActivityState(activity);
            this.mActivities.add(nfcActivityStateFindActivityState);
        }
        return nfcActivityStateFindActivityState;
    }

    synchronized NfcActivityState findResumedActivityState() {
        for (NfcActivityState nfcActivityState : this.mActivities) {
            if (nfcActivityState.resumed) {
                return nfcActivityState;
            }
        }
        return null;
    }

    synchronized void destroyActivityState(Activity activity) {
        NfcActivityState nfcActivityStateFindActivityState = findActivityState(activity);
        if (nfcActivityStateFindActivityState != null) {
            nfcActivityStateFindActivityState.destroy();
            this.mActivities.remove(nfcActivityStateFindActivityState);
        }
    }

    public NfcActivityManager(NfcAdapter nfcAdapter) {
        this.mAdapter = nfcAdapter;
        this.mDefaultEvent = new NfcEvent(nfcAdapter);
    }

    public void enableReaderMode(Activity activity, NfcAdapter.ReaderCallback readerCallback, int i, Bundle bundle) {
        Binder binder;
        boolean z;
        synchronized (this) {
            NfcActivityState activityState = getActivityState(activity);
            activityState.readerCallback = readerCallback;
            activityState.readerModeFlags = i;
            activityState.readerModeExtras = bundle;
            binder = activityState.token;
            z = activityState.resumed;
        }
        if (z) {
            setReaderMode(binder, i, bundle);
        }
    }

    public void disableReaderMode(Activity activity) {
        Binder binder;
        boolean z;
        synchronized (this) {
            NfcActivityState activityState = getActivityState(activity);
            activityState.readerCallback = null;
            activityState.readerModeFlags = 0;
            activityState.readerModeExtras = null;
            binder = activityState.token;
            z = activityState.resumed;
        }
        if (z) {
            setReaderMode(binder, 0, null);
        }
    }

    public void setReaderMode(Binder binder, int i, Bundle bundle) {
        if (DBG.booleanValue()) {
            Log.d(TAG, "Setting reader mode");
        }
        try {
            NfcAdapter.sService.setReaderMode(binder, this, i, bundle);
        } catch (RemoteException e) {
            this.mAdapter.attemptDeadServiceRecovery(e);
        }
    }

    public void setNdefPushContentUri(Activity activity, Uri[] uriArr) {
        boolean z;
        synchronized (this) {
            NfcActivityState activityState = getActivityState(activity);
            activityState.uris = uriArr;
            z = activityState.resumed;
        }
        if (z) {
            requestNfcServiceCallback();
        }
    }

    public void setNdefPushContentUriCallback(Activity activity, NfcAdapter.CreateBeamUrisCallback createBeamUrisCallback) {
        boolean z;
        synchronized (this) {
            NfcActivityState activityState = getActivityState(activity);
            activityState.uriCallback = createBeamUrisCallback;
            z = activityState.resumed;
        }
        if (z) {
            requestNfcServiceCallback();
        }
    }

    public void setNdefPushMessage(Activity activity, NdefMessage ndefMessage, int i) {
        boolean z;
        synchronized (this) {
            NfcActivityState activityState = getActivityState(activity);
            activityState.ndefMessage = ndefMessage;
            activityState.flags = i;
            z = activityState.resumed;
        }
        if (z) {
            requestNfcServiceCallback();
        }
    }

    public void setNdefPushMessageCallback(Activity activity, NfcAdapter.CreateNdefMessageCallback createNdefMessageCallback, int i) {
        boolean z;
        synchronized (this) {
            NfcActivityState activityState = getActivityState(activity);
            activityState.ndefMessageCallback = createNdefMessageCallback;
            activityState.flags = i;
            z = activityState.resumed;
        }
        if (z) {
            requestNfcServiceCallback();
        }
    }

    public void setOnNdefPushCompleteCallback(Activity activity, NfcAdapter.OnNdefPushCompleteCallback onNdefPushCompleteCallback) {
        boolean z;
        synchronized (this) {
            NfcActivityState activityState = getActivityState(activity);
            activityState.onNdefPushCompleteCallback = onNdefPushCompleteCallback;
            z = activityState.resumed;
        }
        if (z) {
            requestNfcServiceCallback();
        }
    }

    void requestNfcServiceCallback() {
        try {
            NfcAdapter.sService.setAppCallback(this);
        } catch (RemoteException e) {
            this.mAdapter.attemptDeadServiceRecovery(e);
        }
    }

    @Override // android.nfc.IAppCallback
    public BeamShareData createBeamShareData() {
        synchronized (this) {
            NfcActivityState nfcActivityStateFindResumedActivityState = findResumedActivityState();
            if (nfcActivityStateFindResumedActivityState == null) {
                return null;
            }
            NfcAdapter.CreateNdefMessageCallback createNdefMessageCallback = nfcActivityStateFindResumedActivityState.ndefMessageCallback;
            NfcAdapter.CreateBeamUrisCallback createBeamUrisCallback = nfcActivityStateFindResumedActivityState.uriCallback;
            NdefMessage ndefMessageCreateNdefMessage = nfcActivityStateFindResumedActivityState.ndefMessage;
            Uri[] uriArrCreateBeamUris = nfcActivityStateFindResumedActivityState.uris;
            int i = nfcActivityStateFindResumedActivityState.flags;
            if (createNdefMessageCallback != null) {
                ndefMessageCreateNdefMessage = createNdefMessageCallback.createNdefMessage(this.mDefaultEvent);
            }
            if (createBeamUrisCallback != null && (uriArrCreateBeamUris = createBeamUrisCallback.createBeamUris(this.mDefaultEvent)) != null) {
                for (Uri uri : uriArrCreateBeamUris) {
                    if (uri == null) {
                        Log.e(TAG, "Uri not allowed to be null.");
                        return null;
                    }
                    String scheme = uri.getScheme();
                    if (scheme == null || !(scheme.equalsIgnoreCase("file") || scheme.equalsIgnoreCase("content"))) {
                        Log.e(TAG, "Uri needs to have either scheme file or scheme content");
                        return null;
                    }
                }
            }
            return new BeamShareData(ndefMessageCreateNdefMessage, uriArrCreateBeamUris, i);
        }
    }

    @Override // android.nfc.IAppCallback
    public void onNdefPushComplete() {
        synchronized (this) {
            NfcActivityState nfcActivityStateFindResumedActivityState = findResumedActivityState();
            if (nfcActivityStateFindResumedActivityState == null) {
                return;
            }
            NfcAdapter.OnNdefPushCompleteCallback onNdefPushCompleteCallback = nfcActivityStateFindResumedActivityState.onNdefPushCompleteCallback;
            if (onNdefPushCompleteCallback != null) {
                onNdefPushCompleteCallback.onNdefPushComplete(this.mDefaultEvent);
            }
        }
    }

    @Override // android.nfc.IAppCallback
    public void onTagDiscovered(Tag tag) throws RemoteException {
        synchronized (this) {
            NfcActivityState nfcActivityStateFindResumedActivityState = findResumedActivityState();
            if (nfcActivityStateFindResumedActivityState == null) {
                return;
            }
            NfcAdapter.ReaderCallback readerCallback = nfcActivityStateFindResumedActivityState.readerCallback;
            if (readerCallback != null) {
                readerCallback.onTagDiscovered(tag);
            }
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        synchronized (this) {
            NfcActivityState nfcActivityStateFindActivityState = findActivityState(activity);
            if (DBG.booleanValue()) {
                Log.d(TAG, "onResume() for " + activity + " " + nfcActivityStateFindActivityState);
            }
            if (nfcActivityStateFindActivityState == null) {
                return;
            }
            nfcActivityStateFindActivityState.resumed = true;
            Binder binder = nfcActivityStateFindActivityState.token;
            int i = nfcActivityStateFindActivityState.readerModeFlags;
            Bundle bundle = nfcActivityStateFindActivityState.readerModeExtras;
            if (i != 0) {
                setReaderMode(binder, i, bundle);
            }
            requestNfcServiceCallback();
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
        synchronized (this) {
            NfcActivityState nfcActivityStateFindActivityState = findActivityState(activity);
            if (DBG.booleanValue()) {
                Log.d(TAG, "onPause() for " + activity + " " + nfcActivityStateFindActivityState);
            }
            if (nfcActivityStateFindActivityState == null) {
                return;
            }
            nfcActivityStateFindActivityState.resumed = false;
            Binder binder = nfcActivityStateFindActivityState.token;
            boolean z = nfcActivityStateFindActivityState.readerModeFlags != 0;
            if (z) {
                setReaderMode(binder, 0, null);
            }
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
        synchronized (this) {
            NfcActivityState nfcActivityStateFindActivityState = findActivityState(activity);
            if (DBG.booleanValue()) {
                Log.d(TAG, "onDestroy() for " + activity + " " + nfcActivityStateFindActivityState);
            }
            if (nfcActivityStateFindActivityState != null) {
                destroyActivityState(activity);
            }
        }
    }
}
