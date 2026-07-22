package android.os;

import android.app.ActivityThread;
import android.content.Context;
import android.os.IVibratorService;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class SystemVibrator extends Vibrator {
    private static final String TAG = "Vibrator";
    private final String mPackageName;
    private final IVibratorService mService;
    private final Binder mToken;

    public SystemVibrator() {
        this.mToken = new Binder();
        this.mPackageName = ActivityThread.currentPackageName();
        this.mService = IVibratorService.Stub.asInterface(ServiceManager.getService(Context.VIBRATOR_SERVICE));
    }

    public SystemVibrator(Context context) {
        this.mToken = new Binder();
        this.mPackageName = context.getOpPackageName();
        this.mService = IVibratorService.Stub.asInterface(ServiceManager.getService(Context.VIBRATOR_SERVICE));
    }

    @Override // android.os.Vibrator
    public boolean hasVibrator() {
        IVibratorService iVibratorService = this.mService;
        if (iVibratorService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
            return false;
        }
        try {
            return iVibratorService.hasVibrator();
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.os.Vibrator
    public void vibrate(long j) {
        vibrate(Process.myUid(), this.mPackageName, j);
    }

    @Override // android.os.Vibrator
    public void vibrate(long[] jArr, int i) {
        vibrate(Process.myUid(), this.mPackageName, jArr, i);
    }

    @Override // android.os.Vibrator
    public void vibrate(int i, String str, long j) {
        IVibratorService iVibratorService = this.mService;
        if (iVibratorService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
            return;
        }
        try {
            iVibratorService.vibrate(i, str, j, this.mToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to vibrate.", e);
        }
    }

    @Override // android.os.Vibrator
    public void vibrate(int i, String str, long[] jArr, int i2) {
        IVibratorService iVibratorService = this.mService;
        if (iVibratorService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
        } else {
            if (i2 < jArr.length) {
                try {
                    iVibratorService.vibratePattern(i, str, jArr, i2, this.mToken);
                    return;
                } catch (RemoteException e) {
                    Log.w(TAG, "Failed to vibrate.", e);
                    return;
                }
            }
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override // android.os.Vibrator
    public void cancel() {
        IVibratorService iVibratorService = this.mService;
        if (iVibratorService == null) {
            return;
        }
        try {
            iVibratorService.cancelVibrate(this.mToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to cancel vibration.", e);
        }
    }
}
