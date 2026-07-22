package android.media;

import android.media.MediaFocusControl;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes.dex */
class FocusRequester {
    private static final boolean DEBUG = false;
    private static final String TAG = "MediaFocusControl";
    private final int mCallingUid;
    private final String mClientId;
    private MediaFocusControl.AudioFocusDeathHandler mDeathHandler;
    private final IAudioFocusDispatcher mFocusDispatcher;
    private final int mFocusGainRequest;
    private int mFocusLossReceived = 0;
    private final String mPackageName;
    private final IBinder mSourceRef;
    private final int mStreamType;

    FocusRequester(int i, int i2, IAudioFocusDispatcher iAudioFocusDispatcher, IBinder iBinder, String str, MediaFocusControl.AudioFocusDeathHandler audioFocusDeathHandler, String str2, int i3) {
        this.mStreamType = i;
        this.mFocusDispatcher = iAudioFocusDispatcher;
        this.mSourceRef = iBinder;
        this.mClientId = str;
        this.mDeathHandler = audioFocusDeathHandler;
        this.mPackageName = str2;
        this.mCallingUid = i3;
        this.mFocusGainRequest = i2;
    }

    boolean hasSameClient(String str) {
        try {
            return this.mClientId.compareTo(str) == 0;
        } catch (NullPointerException unused) {
            return false;
        }
    }

    boolean hasSameBinder(IBinder iBinder) {
        IBinder iBinder2 = this.mSourceRef;
        return iBinder2 != null && iBinder2.equals(iBinder);
    }

    boolean hasSamePackage(String str) {
        try {
            return this.mPackageName.compareTo(str) == 0;
        } catch (NullPointerException unused) {
            return false;
        }
    }

    boolean hasSameUid(int i) {
        return this.mCallingUid == i;
    }

    int getGainRequest() {
        return this.mFocusGainRequest;
    }

    int getStreamType() {
        return this.mStreamType;
    }

    private static String focusChangeToString(int i) {
        switch (i) {
            case -3:
                return "LOSS_TRANSIENT_CAN_DUCK";
            case -2:
                return "LOSS_TRANSIENT";
            case -1:
                return "LOSS";
            case 0:
                return "none";
            case 1:
                return "GAIN";
            case 2:
                return "GAIN_TRANSIENT";
            case 3:
                return "GAIN_TRANSIENT_MAY_DUCK";
            case 4:
                return "GAIN_TRANSIENT_EXCLUSIVE";
            default:
                return "[invalid focus change" + i + "]";
        }
    }

    private String focusGainToString() {
        return focusChangeToString(this.mFocusGainRequest);
    }

    private String focusLossToString() {
        return focusChangeToString(this.mFocusLossReceived);
    }

    void dump(PrintWriter printWriter) {
        printWriter.println("  source:" + this.mSourceRef + " -- pack: " + this.mPackageName + " -- client: " + this.mClientId + " -- gain: " + focusGainToString() + " -- loss: " + focusLossToString() + " -- uid: " + this.mCallingUid + " -- stream: " + this.mStreamType);
    }

    void release() {
        MediaFocusControl.AudioFocusDeathHandler audioFocusDeathHandler;
        try {
            IBinder iBinder = this.mSourceRef;
            if (iBinder == null || (audioFocusDeathHandler = this.mDeathHandler) == null) {
                return;
            }
            iBinder.unlinkToDeath(audioFocusDeathHandler, 0);
            this.mDeathHandler = null;
        } catch (NoSuchElementException e) {
            Log.e(TAG, "FocusRequester.release() hit ", e);
        }
    }

    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0022, code lost:
    
        if (r0 != 0) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x002c, code lost:
    
        if (r0 != 0) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x000d, code lost:
    
        if (r5 != 4) goto L26;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int focusLossForGainRequest(int r5) {
        /*
            r4 = this;
            r0 = 1
            r1 = -3
            r2 = -2
            r3 = -1
            if (r5 == r0) goto L10
            r0 = 2
            if (r5 == r0) goto L1a
            r0 = 3
            if (r5 == r0) goto L24
            r0 = 4
            if (r5 == r0) goto L1a
            goto L2e
        L10:
            int r0 = r4.mFocusLossReceived
            if (r0 == r1) goto L4d
            if (r0 == r2) goto L4d
            if (r0 == r3) goto L4d
            if (r0 == 0) goto L4d
        L1a:
            int r0 = r4.mFocusLossReceived
            if (r0 == r1) goto L4c
            if (r0 == r2) goto L4c
            if (r0 == r3) goto L4b
            if (r0 == 0) goto L4c
        L24:
            int r0 = r4.mFocusLossReceived
            if (r0 == r1) goto L4a
            if (r0 == r2) goto L49
            if (r0 == r3) goto L48
            if (r0 == 0) goto L4a
        L2e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "focusLossForGainRequest() for invalid focus request "
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r5 = r0.append(r5)
            java.lang.String r5 = r5.toString()
            java.lang.String r0 = "MediaFocusControl"
            android.util.Log.e(r0, r5)
            r5 = 0
            return r5
        L48:
            return r3
        L49:
            return r2
        L4a:
            return r1
        L4b:
            return r3
        L4c:
            return r2
        L4d:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.FocusRequester.focusLossForGainRequest(int):int");
    }

    void handleExternalFocusGain(int i) {
        handleFocusLoss(focusLossForGainRequest(i));
    }

    void handleFocusGain(int i) {
        try {
            IAudioFocusDispatcher iAudioFocusDispatcher = this.mFocusDispatcher;
            if (iAudioFocusDispatcher != null) {
                iAudioFocusDispatcher.dispatchAudioFocusChange(i, this.mClientId);
            }
            this.mFocusLossReceived = 0;
        } catch (RemoteException e) {
            Log.e(TAG, "Failure to signal gain of audio focus due to: ", e);
        }
    }

    void handleFocusLoss(int i) {
        try {
            if (i != this.mFocusLossReceived) {
                IAudioFocusDispatcher iAudioFocusDispatcher = this.mFocusDispatcher;
                if (iAudioFocusDispatcher != null) {
                    iAudioFocusDispatcher.dispatchAudioFocusChange(i, this.mClientId);
                }
                this.mFocusLossReceived = i;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Failure to signal loss of audio focus due to:", e);
        }
    }
}
