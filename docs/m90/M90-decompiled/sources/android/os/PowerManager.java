package android.os;

import android.content.Context;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public final class PowerManager {
    public static final int ACQUIRE_CAUSES_WAKEUP = 268435456;
    public static final int BRIGHTNESS_OFF = 0;
    public static final int BRIGHTNESS_ON = 255;

    @Deprecated
    public static final int FULL_WAKE_LOCK = 26;
    public static final int GO_TO_SLEEP_REASON_DEVICE_ADMIN = 1;
    public static final int GO_TO_SLEEP_REASON_TIMEOUT = 2;
    public static final int GO_TO_SLEEP_REASON_USER = 0;
    public static final int ON_AFTER_RELEASE = 536870912;
    public static final int PARTIAL_WAKE_LOCK = 1;
    public static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;

    @Deprecated
    public static final int SCREEN_BRIGHT_WAKE_LOCK = 10;

    @Deprecated
    public static final int SCREEN_DIM_WAKE_LOCK = 6;
    private static final String TAG = "PowerManager";
    public static final int USER_ACTIVITY_EVENT_BUTTON = 1;
    public static final int USER_ACTIVITY_EVENT_OTHER = 0;
    public static final int USER_ACTIVITY_EVENT_TOUCH = 2;
    public static final int USER_ACTIVITY_FLAG_NO_CHANGE_LIGHTS = 1;
    public static final int WAIT_FOR_PROXIMITY_NEGATIVE = 1;
    public static final int WAKE_LOCK_LEVEL_MASK = 65535;
    final Context mContext;
    final Handler mHandler;
    final IPowerManager mService;

    public PowerManager(Context context, IPowerManager iPowerManager, Handler handler) {
        this.mContext = context;
        this.mService = iPowerManager;
        this.mHandler = handler;
    }

    public int getMinimumScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(17694762);
    }

    public int getMaximumScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(17694763);
    }

    public int getDefaultScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(17694764);
    }

    public static boolean useScreenAutoBrightnessAdjustmentFeature() {
        return SystemProperties.getBoolean("persist.power.useautobrightadj", false);
    }

    public static boolean useTwilightAdjustmentFeature() {
        return SystemProperties.getBoolean("persist.power.usetwilightadj", false);
    }

    public WakeLock newWakeLock(int i, String str) {
        validateWakeLockParameters(i, str);
        return new WakeLock(i, str, this.mContext.getOpPackageName());
    }

    public static void validateWakeLockParameters(int i, String str) {
        int i2 = i & 65535;
        if (i2 != 1 && i2 != 6 && i2 != 10 && i2 != 26 && i2 != 32) {
            throw new IllegalArgumentException("Must specify a valid wake lock level.");
        }
        if (str == null) {
            throw new IllegalArgumentException("The tag must not be null.");
        }
    }

    public void userActivity(long j, boolean z) {
        try {
            this.mService.userActivity(j, 0, z ? 1 : 0);
        } catch (RemoteException unused) {
        }
    }

    public void goToSleep(long j) {
        try {
            this.mService.goToSleep(j, 0);
        } catch (RemoteException unused) {
        }
    }

    public void wakeUp(long j) {
        try {
            this.mService.wakeUp(j);
        } catch (RemoteException unused) {
        }
    }

    public void nap(long j) {
        try {
            this.mService.nap(j);
        } catch (RemoteException unused) {
        }
    }

    public void goToBootFastSleep(long j) {
        try {
            this.mService.goToBootFastSleep(j);
        } catch (RemoteException unused) {
        }
    }

    public void bootFastWake(long j) {
        try {
            this.mService.bootFastWake(j);
        } catch (RemoteException unused) {
        }
    }

    public boolean isBootFastStatus() {
        try {
            return this.mService.isBootFastStatus();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean isBootFastWakeFromStandby() {
        try {
            return this.mService.isBootFastWakeFromStandby();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public long getEnterBootFastTime() {
        try {
            return this.mService.getEnterBootFastTime();
        } catch (RemoteException unused) {
            return 0L;
        }
    }

    public void setBacklightBrightness(int i) {
        try {
            this.mService.setTemporaryScreenBrightnessSettingOverride(i);
        } catch (RemoteException unused) {
        }
    }

    public boolean isWakeLockLevelSupported(int i) {
        try {
            return this.mService.isWakeLockLevelSupported(i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean isScreenOn() {
        try {
            return this.mService.isScreenOn();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void reboot(String str) {
        try {
            this.mService.reboot(false, str, true);
        } catch (RemoteException unused) {
        }
    }

    public void setBatteryChargeState(boolean z) {
        try {
            this.mService.setBatteryChargeState(z);
        } catch (RemoteException unused) {
        }
    }

    public final class WakeLock {
        private int mCount;
        private final int mFlags;
        private boolean mHeld;
        private final String mPackageName;
        private final String mTag;
        private WorkSource mWorkSource;
        private boolean mRefCounted = true;
        private final Runnable mReleaser = new Runnable() { // from class: android.os.PowerManager.WakeLock.1
            @Override // java.lang.Runnable
            public void run() {
                WakeLock.this.release();
            }
        };
        private final IBinder mToken = new Binder();

        WakeLock(int i, String str, String str2) {
            this.mFlags = i;
            this.mTag = str;
            this.mPackageName = str2;
        }

        protected void finalize() throws Throwable {
            synchronized (this.mToken) {
                if (this.mHeld) {
                    Log.wtf(PowerManager.TAG, "WakeLock finalized while still held: " + this.mTag);
                    try {
                        PowerManager.this.mService.releaseWakeLock(this.mToken, 0);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }

        public void setReferenceCounted(boolean z) {
            synchronized (this.mToken) {
                this.mRefCounted = z;
            }
        }

        public void acquire() {
            synchronized (this.mToken) {
                acquireLocked();
            }
        }

        public void acquire(long j) {
            synchronized (this.mToken) {
                acquireLocked();
                PowerManager.this.mHandler.postDelayed(this.mReleaser, j);
            }
        }

        private void acquireLocked() {
            if (this.mRefCounted) {
                int i = this.mCount;
                this.mCount = i + 1;
                if (i != 0) {
                    return;
                }
            }
            PowerManager.this.mHandler.removeCallbacks(this.mReleaser);
            try {
                PowerManager.this.mService.acquireWakeLock(this.mToken, this.mFlags, this.mTag, this.mPackageName, this.mWorkSource);
            } catch (RemoteException unused) {
            }
            this.mHeld = true;
        }

        public void release() {
            release(0);
        }

        /* JADX WARN: Removed duplicated region for block: B:8:0x000f A[Catch: all -> 0x0049, TRY_LEAVE, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x0007, B:13:0x0028, B:15:0x002c, B:17:0x002e, B:18:0x0048, B:8:0x000f, B:10:0x001c, B:12:0x0026), top: B:23:0x0003 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void release(int r4) {
            /*
                r3 = this;
                android.os.IBinder r0 = r3.mToken
                monitor-enter(r0)
                boolean r1 = r3.mRefCounted     // Catch: java.lang.Throwable -> L49
                if (r1 == 0) goto Lf
                int r1 = r3.mCount     // Catch: java.lang.Throwable -> L49
                int r1 = r1 + (-1)
                r3.mCount = r1     // Catch: java.lang.Throwable -> L49
                if (r1 != 0) goto L28
            Lf:
                android.os.PowerManager r1 = android.os.PowerManager.this     // Catch: java.lang.Throwable -> L49
                android.os.Handler r1 = r1.mHandler     // Catch: java.lang.Throwable -> L49
                java.lang.Runnable r2 = r3.mReleaser     // Catch: java.lang.Throwable -> L49
                r1.removeCallbacks(r2)     // Catch: java.lang.Throwable -> L49
                boolean r1 = r3.mHeld     // Catch: java.lang.Throwable -> L49
                if (r1 == 0) goto L28
                android.os.PowerManager r1 = android.os.PowerManager.this     // Catch: android.os.RemoteException -> L25 java.lang.Throwable -> L49
                android.os.IPowerManager r1 = r1.mService     // Catch: android.os.RemoteException -> L25 java.lang.Throwable -> L49
                android.os.IBinder r2 = r3.mToken     // Catch: android.os.RemoteException -> L25 java.lang.Throwable -> L49
                r1.releaseWakeLock(r2, r4)     // Catch: android.os.RemoteException -> L25 java.lang.Throwable -> L49
            L25:
                r4 = 0
                r3.mHeld = r4     // Catch: java.lang.Throwable -> L49
            L28:
                int r4 = r3.mCount     // Catch: java.lang.Throwable -> L49
                if (r4 < 0) goto L2e
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L49
                return
            L2e:
                java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch: java.lang.Throwable -> L49
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L49
                r1.<init>()     // Catch: java.lang.Throwable -> L49
                java.lang.String r2 = "WakeLock under-locked "
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L49
                java.lang.String r2 = r3.mTag     // Catch: java.lang.Throwable -> L49
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L49
                java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> L49
                r4.<init>(r1)     // Catch: java.lang.Throwable -> L49
                throw r4     // Catch: java.lang.Throwable -> L49
            L49:
                r4 = move-exception
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L49
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.PowerManager.WakeLock.release(int):void");
        }

        public boolean isHeld() {
            boolean z;
            synchronized (this.mToken) {
                z = this.mHeld;
            }
            return z;
        }

        public void setWorkSource(WorkSource workSource) {
            synchronized (this.mToken) {
                if (workSource != null) {
                    try {
                        if (workSource.size() == 0) {
                            workSource = null;
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                boolean zDiff = true;
                if (workSource == null) {
                    if (this.mWorkSource == null) {
                        zDiff = false;
                    }
                    this.mWorkSource = null;
                } else {
                    WorkSource workSource2 = this.mWorkSource;
                    if (workSource2 == null) {
                        this.mWorkSource = new WorkSource(workSource);
                    } else {
                        zDiff = workSource2.diff(workSource);
                        if (zDiff) {
                            this.mWorkSource.set(workSource);
                        }
                    }
                }
                if (zDiff && this.mHeld) {
                    try {
                        PowerManager.this.mService.updateWakeLockWorkSource(this.mToken, this.mWorkSource);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }

        public String toString() {
            String str;
            synchronized (this.mToken) {
                str = "WakeLock{" + Integer.toHexString(System.identityHashCode(this)) + " held=" + this.mHeld + ", refCount=" + this.mCount + "}";
            }
            return str;
        }
    }
}
