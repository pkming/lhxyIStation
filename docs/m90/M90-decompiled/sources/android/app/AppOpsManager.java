package android.app;

import android.Manifest;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.util.ArrayMap;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AppOpsManager {
    public static final int MODE_ALLOWED = 0;
    public static final int MODE_ERRORED = 2;
    public static final int MODE_IGNORED = 1;
    public static final int OP_ACCESS_NOTIFICATIONS = 25;
    public static final int OP_AUDIO_ALARM_VOLUME = 37;
    public static final int OP_AUDIO_BLUETOOTH_VOLUME = 39;
    public static final int OP_AUDIO_MASTER_VOLUME = 33;
    public static final int OP_AUDIO_MEDIA_VOLUME = 36;
    public static final int OP_AUDIO_NOTIFICATION_VOLUME = 38;
    public static final int OP_AUDIO_RING_VOLUME = 35;
    public static final int OP_AUDIO_VOICE_VOLUME = 34;
    public static final int OP_CALL_PHONE = 13;
    public static final int OP_CAMERA = 26;
    public static final int OP_COARSE_LOCATION = 0;
    public static final int OP_FINE_LOCATION = 1;
    public static final int OP_GPS = 2;
    public static final int OP_MONITOR_HIGH_POWER_LOCATION = 42;
    public static final int OP_MONITOR_LOCATION = 41;
    public static final int OP_NEIGHBORING_CELLS = 12;
    public static final int OP_NONE = -1;
    public static final int OP_PLAY_AUDIO = 28;
    public static final int OP_POST_NOTIFICATION = 11;
    public static final int OP_READ_CALENDAR = 8;
    public static final int OP_READ_CALL_LOG = 6;
    public static final int OP_READ_CLIPBOARD = 29;
    public static final int OP_READ_CONTACTS = 4;
    public static final int OP_READ_ICC_SMS = 21;
    public static final int OP_READ_SMS = 14;
    public static final int OP_RECEIVE_EMERGECY_SMS = 17;
    public static final int OP_RECEIVE_MMS = 18;
    public static final int OP_RECEIVE_SMS = 16;
    public static final int OP_RECEIVE_WAP_PUSH = 19;
    public static final int OP_RECORD_AUDIO = 27;
    public static final int OP_SEND_SMS = 20;
    public static final int OP_SYSTEM_ALERT_WINDOW = 24;
    public static final int OP_TAKE_AUDIO_FOCUS = 32;
    public static final int OP_TAKE_MEDIA_BUTTONS = 31;
    public static final int OP_VIBRATE = 3;
    public static final int OP_WAKE_LOCK = 40;
    public static final int OP_WIFI_SCAN = 10;
    public static final int OP_WRITE_CALENDAR = 9;
    public static final int OP_WRITE_CALL_LOG = 7;
    public static final int OP_WRITE_CLIPBOARD = 30;
    public static final int OP_WRITE_CONTACTS = 5;
    public static final int OP_WRITE_ICC_SMS = 22;
    public static final int OP_WRITE_SETTINGS = 23;
    public static final int OP_WRITE_SMS = 15;
    public static final int _NUM_OP = 43;
    static IBinder sToken;
    final Context mContext;
    final ArrayMap<OnOpChangedListener, IAppOpsCallback> mModeWatchers = new ArrayMap<>();
    final IAppOpsService mService;
    private static int[] sOpToSwitch = {0, 0, 0, 3, 4, 5, 6, 7, 8, 9, 0, 11, 0, 13, 14, 15, 16, 16, 16, 16, 20, 14, 15, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 0, 0};
    public static final String OPSTR_COARSE_LOCATION = "android:coarse_location";
    public static final String OPSTR_FINE_LOCATION = "android:fine_location";
    public static final String OPSTR_MONITOR_LOCATION = "android:monitor_location";
    public static final String OPSTR_MONITOR_HIGH_POWER_LOCATION = "android:monitor_location_high_power";
    private static String[] sOpToString = {OPSTR_COARSE_LOCATION, OPSTR_FINE_LOCATION, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, OPSTR_MONITOR_LOCATION, OPSTR_MONITOR_HIGH_POWER_LOCATION};
    private static String[] sOpNames = {"COARSE_LOCATION", "FINE_LOCATION", "GPS", "VIBRATE", "READ_CONTACTS", "WRITE_CONTACTS", "READ_CALL_LOG", "WRITE_CALL_LOG", "READ_CALENDAR", "WRITE_CALENDAR", "WIFI_SCAN", "POST_NOTIFICATION", "NEIGHBORING_CELLS", "CALL_PHONE", "READ_SMS", "WRITE_SMS", "RECEIVE_SMS", "RECEIVE_EMERGECY_SMS", "RECEIVE_MMS", "RECEIVE_WAP_PUSH", "SEND_SMS", "READ_ICC_SMS", "WRITE_ICC_SMS", "WRITE_SETTINGS", "SYSTEM_ALERT_WINDOW", "ACCESS_NOTIFICATIONS", "CAMERA", "RECORD_AUDIO", "PLAY_AUDIO", "READ_CLIPBOARD", "WRITE_CLIPBOARD", "TAKE_MEDIA_BUTTONS", "TAKE_AUDIO_FOCUS", "AUDIO_MASTER_VOLUME", "AUDIO_VOICE_VOLUME", "AUDIO_RING_VOLUME", "AUDIO_MEDIA_VOLUME", "AUDIO_ALARM_VOLUME", "AUDIO_NOTIFICATION_VOLUME", "AUDIO_BLUETOOTH_VOLUME", "WAKE_LOCK", "MONITOR_LOCATION", "MONITOR_HIGH_POWER_LOCATION"};
    private static String[] sOpPerms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, null, Manifest.permission.VIBRATE, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR, null, Manifest.permission.ACCESS_WIFI_STATE, null, Manifest.permission.CALL_PHONE, Manifest.permission.READ_SMS, Manifest.permission.WRITE_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_EMERGENCY_BROADCAST, Manifest.permission.RECEIVE_MMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.WRITE_SMS, Manifest.permission.WRITE_SETTINGS, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.ACCESS_NOTIFICATIONS, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, null, null, null, null, null, null, null, null, null, null, null, null, Manifest.permission.WAKE_LOCK, null, null};
    private static int[] sOpDefaultMode = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static boolean[] sOpDisableReset = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    private static HashMap<String, Integer> sOpStrToOp = new HashMap<>();

    public static class OnOpChangedInternalListener implements OnOpChangedListener {
        public void onOpChanged(int i, String str) {
        }

        @Override // android.app.AppOpsManager.OnOpChangedListener
        public void onOpChanged(String str, String str2) {
        }
    }

    public interface OnOpChangedListener {
        void onOpChanged(String str, String str2);
    }

    static {
        if (sOpToSwitch.length != 43) {
            throw new IllegalStateException("sOpToSwitch length " + sOpToSwitch.length + " should be 43");
        }
        if (sOpToString.length != 43) {
            throw new IllegalStateException("sOpToString length " + sOpToString.length + " should be 43");
        }
        if (sOpNames.length != 43) {
            throw new IllegalStateException("sOpNames length " + sOpNames.length + " should be 43");
        }
        if (sOpPerms.length != 43) {
            throw new IllegalStateException("sOpPerms length " + sOpPerms.length + " should be 43");
        }
        if (sOpDefaultMode.length != 43) {
            throw new IllegalStateException("sOpDefaultMode length " + sOpDefaultMode.length + " should be 43");
        }
        if (sOpDisableReset.length != 43) {
            throw new IllegalStateException("sOpDisableReset length " + sOpDisableReset.length + " should be 43");
        }
        for (int i = 0; i < 43; i++) {
            String[] strArr = sOpToString;
            if (strArr[i] != null) {
                sOpStrToOp.put(strArr[i], Integer.valueOf(i));
            }
        }
    }

    public static int opToSwitch(int i) {
        return sOpToSwitch[i];
    }

    public static String opToName(int i) {
        if (i == -1) {
            return "NONE";
        }
        String[] strArr = sOpNames;
        return i < strArr.length ? strArr[i] : "Unknown(" + i + ")";
    }

    public static String opToPermission(int i) {
        return sOpPerms[i];
    }

    public static int opToDefaultMode(int i) {
        return sOpDefaultMode[i];
    }

    public static boolean opAllowsReset(int i) {
        return !sOpDisableReset[i];
    }

    public static class PackageOps implements Parcelable {
        public static final Parcelable.Creator<PackageOps> CREATOR = new Parcelable.Creator<PackageOps>() { // from class: android.app.AppOpsManager.PackageOps.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PackageOps createFromParcel(Parcel parcel) {
                return new PackageOps(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PackageOps[] newArray(int i) {
                return new PackageOps[i];
            }
        };
        private final List<OpEntry> mEntries;
        private final String mPackageName;
        private final int mUid;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public PackageOps(String str, int i, List<OpEntry> list) {
            this.mPackageName = str;
            this.mUid = i;
            this.mEntries = list;
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        public int getUid() {
            return this.mUid;
        }

        public List<OpEntry> getOps() {
            return this.mEntries;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.mPackageName);
            parcel.writeInt(this.mUid);
            parcel.writeInt(this.mEntries.size());
            for (int i2 = 0; i2 < this.mEntries.size(); i2++) {
                this.mEntries.get(i2).writeToParcel(parcel, i);
            }
        }

        PackageOps(Parcel parcel) {
            this.mPackageName = parcel.readString();
            this.mUid = parcel.readInt();
            this.mEntries = new ArrayList();
            int i = parcel.readInt();
            for (int i2 = 0; i2 < i; i2++) {
                this.mEntries.add(OpEntry.CREATOR.createFromParcel(parcel));
            }
        }
    }

    public static class OpEntry implements Parcelable {
        public static final Parcelable.Creator<OpEntry> CREATOR = new Parcelable.Creator<OpEntry>() { // from class: android.app.AppOpsManager.OpEntry.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public OpEntry createFromParcel(Parcel parcel) {
                return new OpEntry(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public OpEntry[] newArray(int i) {
                return new OpEntry[i];
            }
        };
        private final int mDuration;
        private final int mMode;
        private final int mOp;
        private final long mRejectTime;
        private final long mTime;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public OpEntry(int i, int i2, long j, long j2, int i3) {
            this.mOp = i;
            this.mMode = i2;
            this.mTime = j;
            this.mRejectTime = j2;
            this.mDuration = i3;
        }

        public int getOp() {
            return this.mOp;
        }

        public int getMode() {
            return this.mMode;
        }

        public long getTime() {
            return this.mTime;
        }

        public long getRejectTime() {
            return this.mRejectTime;
        }

        public boolean isRunning() {
            return this.mDuration == -1;
        }

        public int getDuration() {
            int i = this.mDuration;
            return i == -1 ? (int) (System.currentTimeMillis() - this.mTime) : i;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mOp);
            parcel.writeInt(this.mMode);
            parcel.writeLong(this.mTime);
            parcel.writeLong(this.mRejectTime);
            parcel.writeInt(this.mDuration);
        }

        OpEntry(Parcel parcel) {
            this.mOp = parcel.readInt();
            this.mMode = parcel.readInt();
            this.mTime = parcel.readLong();
            this.mRejectTime = parcel.readLong();
            this.mDuration = parcel.readInt();
        }
    }

    AppOpsManager(Context context, IAppOpsService iAppOpsService) {
        this.mContext = context;
        this.mService = iAppOpsService;
    }

    public List<PackageOps> getPackagesForOps(int[] iArr) {
        try {
            return this.mService.getPackagesForOps(iArr);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public List<PackageOps> getOpsForPackage(int i, String str, int[] iArr) {
        try {
            return this.mService.getOpsForPackage(i, str, iArr);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public void setMode(int i, int i2, String str, int i3) {
        try {
            this.mService.setMode(i, i2, str, i3);
        } catch (RemoteException unused) {
        }
    }

    public void resetAllModes() {
        try {
            this.mService.resetAllModes();
        } catch (RemoteException unused) {
        }
    }

    public void startWatchingMode(String str, String str2, OnOpChangedListener onOpChangedListener) {
        startWatchingMode(strOpToOp(str), str2, onOpChangedListener);
    }

    public void startWatchingMode(int i, String str, final OnOpChangedListener onOpChangedListener) {
        synchronized (this.mModeWatchers) {
            IAppOpsCallback iAppOpsCallback = this.mModeWatchers.get(onOpChangedListener);
            if (iAppOpsCallback == null) {
                iAppOpsCallback = new IAppOpsCallback.Stub() { // from class: android.app.AppOpsManager.1
                    public void opChanged(int i2, String str2) {
                        OnOpChangedListener onOpChangedListener2 = onOpChangedListener;
                        if (onOpChangedListener2 instanceof OnOpChangedInternalListener) {
                            ((OnOpChangedInternalListener) onOpChangedListener2).onOpChanged(i2, str2);
                        }
                        if (AppOpsManager.sOpToString[i2] != null) {
                            onOpChangedListener.onOpChanged(AppOpsManager.sOpToString[i2], str2);
                        }
                    }
                };
                this.mModeWatchers.put(onOpChangedListener, iAppOpsCallback);
            }
            try {
                this.mService.startWatchingMode(i, str, iAppOpsCallback);
            } catch (RemoteException unused) {
            }
        }
    }

    public void stopWatchingMode(OnOpChangedListener onOpChangedListener) {
        synchronized (this.mModeWatchers) {
            IAppOpsCallback iAppOpsCallback = this.mModeWatchers.get(onOpChangedListener);
            if (iAppOpsCallback != null) {
                try {
                    this.mService.stopWatchingMode(iAppOpsCallback);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    private String buildSecurityExceptionMsg(int i, int i2, String str) {
        return str + " from uid " + i2 + " not allowed to perform " + sOpNames[i];
    }

    private int strOpToOp(String str) {
        Integer num = sOpStrToOp.get(str);
        if (num == null) {
            throw new IllegalArgumentException("Unknown operation string: " + str);
        }
        return num.intValue();
    }

    public int checkOp(String str, int i, String str2) {
        return checkOp(strOpToOp(str), i, str2);
    }

    public int checkOpNoThrow(String str, int i, String str2) {
        return checkOpNoThrow(strOpToOp(str), i, str2);
    }

    public int noteOp(String str, int i, String str2) {
        return noteOp(strOpToOp(str), i, str2);
    }

    public int noteOpNoThrow(String str, int i, String str2) {
        return noteOpNoThrow(strOpToOp(str), i, str2);
    }

    public int startOp(String str, int i, String str2) {
        return startOp(strOpToOp(str), i, str2);
    }

    public int startOpNoThrow(String str, int i, String str2) {
        return startOpNoThrow(strOpToOp(str), i, str2);
    }

    public void finishOp(String str, int i, String str2) {
        finishOp(strOpToOp(str), i, str2);
    }

    public int checkOp(int i, int i2, String str) {
        try {
            int iCheckOperation = this.mService.checkOperation(i, i2, str);
            if (iCheckOperation != 2) {
                return iCheckOperation;
            }
            throw new SecurityException(buildSecurityExceptionMsg(i, i2, str));
        } catch (RemoteException unused) {
            return 1;
        }
    }

    public int checkOpNoThrow(int i, int i2, String str) {
        try {
            return this.mService.checkOperation(i, i2, str);
        } catch (RemoteException unused) {
            return 1;
        }
    }

    public void checkPackage(int i, String str) {
        try {
            if (this.mService.checkPackage(i, str) == 0) {
            } else {
                throw new SecurityException("Package " + str + " does not belong to " + i);
            }
        } catch (RemoteException e) {
            throw new SecurityException("Unable to verify package ownership", e);
        }
    }

    public int noteOp(int i, int i2, String str) {
        try {
            int iNoteOperation = this.mService.noteOperation(i, i2, str);
            if (iNoteOperation != 2) {
                return iNoteOperation;
            }
            throw new SecurityException(buildSecurityExceptionMsg(i, i2, str));
        } catch (RemoteException unused) {
            return 1;
        }
    }

    public int noteOpNoThrow(int i, int i2, String str) {
        try {
            return this.mService.noteOperation(i, i2, str);
        } catch (RemoteException unused) {
            return 1;
        }
    }

    public int noteOp(int i) {
        return noteOp(i, Process.myUid(), this.mContext.getOpPackageName());
    }

    public static IBinder getToken(IAppOpsService iAppOpsService) {
        synchronized (AppOpsManager.class) {
            IBinder iBinder = sToken;
            if (iBinder != null) {
                return iBinder;
            }
            try {
                sToken = iAppOpsService.getToken(new Binder());
            } catch (RemoteException unused) {
            }
            return sToken;
        }
    }

    public int startOp(int i, int i2, String str) {
        try {
            IAppOpsService iAppOpsService = this.mService;
            int iStartOperation = iAppOpsService.startOperation(getToken(iAppOpsService), i, i2, str);
            if (iStartOperation != 2) {
                return iStartOperation;
            }
            throw new SecurityException(buildSecurityExceptionMsg(i, i2, str));
        } catch (RemoteException unused) {
            return 1;
        }
    }

    public int startOpNoThrow(int i, int i2, String str) {
        try {
            IAppOpsService iAppOpsService = this.mService;
            return iAppOpsService.startOperation(getToken(iAppOpsService), i, i2, str);
        } catch (RemoteException unused) {
            return 1;
        }
    }

    public int startOp(int i) {
        return startOp(i, Process.myUid(), this.mContext.getOpPackageName());
    }

    public void finishOp(int i, int i2, String str) {
        try {
            IAppOpsService iAppOpsService = this.mService;
            iAppOpsService.finishOperation(getToken(iAppOpsService), i, i2, str);
        } catch (RemoteException unused) {
        }
    }

    public void finishOp(int i) {
        finishOp(i, Process.myUid(), this.mContext.getOpPackageName());
    }
}
