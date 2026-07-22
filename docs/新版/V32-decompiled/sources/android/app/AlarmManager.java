package android.app;

import android.content.Context;
import android.os.RemoteException;
import android.os.WorkSource;

/* JADX INFO: loaded from: classes.dex */
public class AlarmManager {
    public static final int ELAPSED_REALTIME = 3;
    public static final int ELAPSED_REALTIME_WAKEUP = 2;
    public static final long INTERVAL_DAY = 86400000;
    public static final long INTERVAL_FIFTEEN_MINUTES = 900000;
    public static final long INTERVAL_HALF_DAY = 43200000;
    public static final long INTERVAL_HALF_HOUR = 1800000;
    public static final long INTERVAL_HOUR = 3600000;
    public static final int RTC = 1;
    public static final int RTC_SHUTDOWN_WAKEUP = 4;
    public static final int RTC_SUSPEND_WAKEUP = 5;
    public static final int RTC_WAKEUP = 0;
    private static final String TAG = "AlarmManager";
    public static final long WINDOW_EXACT = 0;
    public static final long WINDOW_HEURISTIC = -1;
    private final boolean mAlwaysExact;
    private final IAlarmManager mService;

    AlarmManager(IAlarmManager iAlarmManager, Context context) {
        this.mService = iAlarmManager;
        this.mAlwaysExact = context.getApplicationInfo().targetSdkVersion < 19;
    }

    private long legacyExactLength() {
        return this.mAlwaysExact ? 0L : -1L;
    }

    public void set(int i, long j, PendingIntent pendingIntent) {
        setImpl(i, j, legacyExactLength(), 0L, pendingIntent, null);
    }

    public void setRepeating(int i, long j, long j2, PendingIntent pendingIntent) {
        setImpl(i, j, legacyExactLength(), j2, pendingIntent, null);
    }

    public void setWindow(int i, long j, long j2, PendingIntent pendingIntent) {
        setImpl(i, j, j2, 0L, pendingIntent, null);
    }

    public void setExact(int i, long j, PendingIntent pendingIntent) {
        setImpl(i, j, 0L, 0L, pendingIntent, null);
    }

    public void set(int i, long j, long j2, long j3, PendingIntent pendingIntent, WorkSource workSource) {
        setImpl(i, j, j2, j3, pendingIntent, workSource);
    }

    private void setImpl(int i, long j, long j2, long j3, PendingIntent pendingIntent, WorkSource workSource) {
        AlarmManager alarmManager;
        long j4;
        if (j < 0) {
            j4 = 0;
            alarmManager = this;
        } else {
            alarmManager = this;
            j4 = j;
        }
        try {
            alarmManager.mService.set(i, j4, j2, j3, pendingIntent, workSource);
        } catch (RemoteException unused) {
        }
    }

    public void setInexactRepeating(int i, long j, long j2, PendingIntent pendingIntent) {
        setImpl(i, j, -1L, j2, pendingIntent, null);
    }

    public void cancel(PendingIntent pendingIntent) {
        try {
            this.mService.remove(pendingIntent);
        } catch (RemoteException unused) {
        }
    }

    public void setTime(long j) {
        try {
            this.mService.setTime(j);
        } catch (RemoteException unused) {
        }
    }

    public void setTimeZone(String str) {
        try {
            this.mService.setTimeZone(str);
        } catch (RemoteException unused) {
        }
    }
}
