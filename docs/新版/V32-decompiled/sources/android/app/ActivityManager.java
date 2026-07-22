package android.app;

import android.R;
import android.app.IThumbnailRetriever;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.BatteryStats;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.app.IUsageStats;
import com.android.internal.os.PkgUsageStats;
import com.android.internal.os.TransferPipe;
import com.android.internal.util.FastPrintWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ActivityManager {
    public static final int BROADCAST_STICKY_CANT_HAVE_PERMISSION = -1;
    public static final int BROADCAST_SUCCESS = 0;
    public static final int COMPAT_MODE_ALWAYS = -1;
    public static final int COMPAT_MODE_DISABLED = 0;
    public static final int COMPAT_MODE_ENABLED = 1;
    public static final int COMPAT_MODE_NEVER = -2;
    public static final int COMPAT_MODE_TOGGLE = 2;
    public static final int COMPAT_MODE_UNKNOWN = -3;
    public static final int INTENT_SENDER_ACTIVITY = 2;
    public static final int INTENT_SENDER_ACTIVITY_RESULT = 3;
    public static final int INTENT_SENDER_BROADCAST = 1;
    public static final int INTENT_SENDER_SERVICE = 4;
    public static final String META_HOME_ALTERNATE = "android.app.home.alternate";
    public static final int MOVE_TASK_NO_USER_ACTION = 2;
    public static final int MOVE_TASK_WITH_HOME = 1;
    public static final int PROCESS_STATE_BACKUP = 5;
    public static final int PROCESS_STATE_CACHED_ACTIVITY = 11;
    public static final int PROCESS_STATE_CACHED_ACTIVITY_CLIENT = 12;
    public static final int PROCESS_STATE_CACHED_EMPTY = 13;
    public static final int PROCESS_STATE_HEAVY_WEIGHT = 6;
    public static final int PROCESS_STATE_HOME = 9;
    public static final int PROCESS_STATE_IMPORTANT_BACKGROUND = 4;
    public static final int PROCESS_STATE_IMPORTANT_FOREGROUND = 3;
    public static final int PROCESS_STATE_LAST_ACTIVITY = 10;
    public static final int PROCESS_STATE_PERSISTENT = 0;
    public static final int PROCESS_STATE_PERSISTENT_UI = 1;
    public static final int PROCESS_STATE_RECEIVER = 8;
    public static final int PROCESS_STATE_SERVICE = 7;
    public static final int PROCESS_STATE_TOP = 2;
    public static final int RECENT_IGNORE_UNAVAILABLE = 2;
    public static final int RECENT_WITH_EXCLUDED = 1;
    public static final int REMOVE_TASK_KILL_PROCESS = 1;
    public static final int START_CANCELED = -6;
    public static final int START_CLASS_NOT_FOUND = -2;
    public static final int START_DELIVERED_TO_TOP = 3;
    public static final int START_FLAG_AUTO_STOP_PROFILER = 8;
    public static final int START_FLAG_DEBUG = 2;
    public static final int START_FLAG_ONLY_IF_NEEDED = 1;
    public static final int START_FLAG_OPENGL_TRACES = 4;
    public static final int START_FORWARD_AND_REQUEST_CONFLICT = -3;
    public static final int START_INTENT_NOT_RESOLVED = -1;
    public static final int START_NOT_ACTIVITY = -5;
    public static final int START_PERMISSION_DENIED = -4;
    public static final int START_RETURN_INTENT_TO_CALLER = 1;
    public static final int START_SUCCESS = 0;
    public static final int START_SWITCHES_CANCELED = 4;
    public static final int START_TASK_TO_FRONT = 2;
    private static String TAG = "ActivityManager";
    public static final int USER_OP_IS_CURRENT = -2;
    public static final int USER_OP_SUCCESS = 0;
    public static final int USER_OP_UNKNOWN_USER = -1;
    private static boolean localLOGV = false;
    private final Context mContext;
    private final Handler mHandler;

    ActivityManager(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public int getFrontActivityScreenCompatMode() {
        try {
            return ActivityManagerNative.getDefault().getFrontActivityScreenCompatMode();
        } catch (RemoteException unused) {
            return 0;
        }
    }

    public void setFrontActivityScreenCompatMode(int i) {
        try {
            ActivityManagerNative.getDefault().setFrontActivityScreenCompatMode(i);
        } catch (RemoteException unused) {
        }
    }

    public int getPackageScreenCompatMode(String str) {
        try {
            return ActivityManagerNative.getDefault().getPackageScreenCompatMode(str);
        } catch (RemoteException unused) {
            return 0;
        }
    }

    public void setPackageScreenCompatMode(String str, int i) {
        try {
            ActivityManagerNative.getDefault().setPackageScreenCompatMode(str, i);
        } catch (RemoteException unused) {
        }
    }

    public boolean getPackageAskScreenCompat(String str) {
        try {
            return ActivityManagerNative.getDefault().getPackageAskScreenCompat(str);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void setPackageAskScreenCompat(String str, boolean z) {
        try {
            ActivityManagerNative.getDefault().setPackageAskScreenCompat(str, z);
        } catch (RemoteException unused) {
        }
    }

    public int getMemoryClass() {
        return staticGetMemoryClass();
    }

    public static int staticGetMemoryClass() {
        String str = SystemProperties.get("dalvik.vm.heapgrowthlimit", "");
        if (str != null && !"".equals(str)) {
            return Integer.parseInt(str.substring(0, str.length() - 1));
        }
        return staticGetLargeMemoryClass();
    }

    public int getLargeMemoryClass() {
        return staticGetLargeMemoryClass();
    }

    public static int staticGetLargeMemoryClass() {
        return Integer.parseInt(SystemProperties.get("dalvik.vm.heapsize", "16m").substring(0, r0.length() - 1));
    }

    public boolean isLowRamDevice() {
        return isLowRamDeviceStatic();
    }

    public static boolean isLowRamDeviceStatic() {
        return "true".equals(SystemProperties.get("ro.config.low_ram", "false"));
    }

    public static boolean isHighEndGfx() {
        return (isLowRamDeviceStatic() || Resources.getSystem().getBoolean(17891350)) ? false : true;
    }

    public static class RecentTaskInfo implements Parcelable {
        public static final Parcelable.Creator<RecentTaskInfo> CREATOR = new Parcelable.Creator<RecentTaskInfo>() { // from class: android.app.ActivityManager.RecentTaskInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RecentTaskInfo createFromParcel(Parcel parcel) {
                return new RecentTaskInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RecentTaskInfo[] newArray(int i) {
                return new RecentTaskInfo[i];
            }
        };
        public Intent baseIntent;
        public CharSequence description;
        public int id;
        public ComponentName origActivity;
        public int persistentId;
        public int stackId;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public RecentTaskInfo() {
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.id);
            parcel.writeInt(this.persistentId);
            if (this.baseIntent != null) {
                parcel.writeInt(1);
                this.baseIntent.writeToParcel(parcel, 0);
            } else {
                parcel.writeInt(0);
            }
            ComponentName.writeToParcel(this.origActivity, parcel);
            TextUtils.writeToParcel(this.description, parcel, 1);
            parcel.writeInt(this.stackId);
        }

        public void readFromParcel(Parcel parcel) {
            this.id = parcel.readInt();
            this.persistentId = parcel.readInt();
            if (parcel.readInt() != 0) {
                this.baseIntent = Intent.CREATOR.createFromParcel(parcel);
            } else {
                this.baseIntent = null;
            }
            this.origActivity = ComponentName.readFromParcel(parcel);
            this.description = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.stackId = parcel.readInt();
        }

        private RecentTaskInfo(Parcel parcel) {
            readFromParcel(parcel);
        }
    }

    public List<RecentTaskInfo> getRecentTasks(int i, int i2) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getRecentTasks(i, i2, UserHandle.myUserId());
        } catch (RemoteException unused) {
            return null;
        }
    }

    public List<RecentTaskInfo> getRecentTasksForUser(int i, int i2, int i3) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getRecentTasks(i, i2, i3);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public static class RunningTaskInfo implements Parcelable {
        public static final Parcelable.Creator<RunningTaskInfo> CREATOR = new Parcelable.Creator<RunningTaskInfo>() { // from class: android.app.ActivityManager.RunningTaskInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningTaskInfo createFromParcel(Parcel parcel) {
                return new RunningTaskInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningTaskInfo[] newArray(int i) {
                return new RunningTaskInfo[i];
            }
        };
        public ComponentName baseActivity;
        public CharSequence description;
        public int id;
        public long lastActiveTime;
        public int numActivities;
        public int numRunning;
        public Bitmap thumbnail;
        public ComponentName topActivity;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public RunningTaskInfo() {
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.id);
            ComponentName.writeToParcel(this.baseActivity, parcel);
            ComponentName.writeToParcel(this.topActivity, parcel);
            if (this.thumbnail != null) {
                parcel.writeInt(1);
                this.thumbnail.writeToParcel(parcel, 0);
            } else {
                parcel.writeInt(0);
            }
            TextUtils.writeToParcel(this.description, parcel, 1);
            parcel.writeInt(this.numActivities);
            parcel.writeInt(this.numRunning);
        }

        public void readFromParcel(Parcel parcel) {
            this.id = parcel.readInt();
            this.baseActivity = ComponentName.readFromParcel(parcel);
            this.topActivity = ComponentName.readFromParcel(parcel);
            if (parcel.readInt() != 0) {
                this.thumbnail = Bitmap.CREATOR.createFromParcel(parcel);
            } else {
                this.thumbnail = null;
            }
            this.description = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.numActivities = parcel.readInt();
            this.numRunning = parcel.readInt();
        }

        private RunningTaskInfo(Parcel parcel) {
            readFromParcel(parcel);
        }
    }

    public List<RunningTaskInfo> getRunningTasks(int i, int i2, IThumbnailReceiver iThumbnailReceiver) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getTasks(i, i2, iThumbnailReceiver);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public List<RunningTaskInfo> getRunningTasks(int i) throws SecurityException {
        return getRunningTasks(i, 0, null);
    }

    public boolean removeSubTask(int i, int i2) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().removeSubTask(i, i2);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean removeTask(int i, int i2) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().removeTask(i, i2);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static class TaskThumbnails implements Parcelable {
        public static final Parcelable.Creator<TaskThumbnails> CREATOR = new Parcelable.Creator<TaskThumbnails>() { // from class: android.app.ActivityManager.TaskThumbnails.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TaskThumbnails createFromParcel(Parcel parcel) {
                return new TaskThumbnails(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TaskThumbnails[] newArray(int i) {
                return new TaskThumbnails[i];
            }
        };
        public Bitmap mainThumbnail;
        public int numSubThumbbails;
        public IThumbnailRetriever retriever;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public TaskThumbnails() {
        }

        public Bitmap getSubThumbnail(int i) {
            try {
                return this.retriever.getThumbnail(i);
            } catch (RemoteException unused) {
                return null;
            }
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            if (this.mainThumbnail != null) {
                parcel.writeInt(1);
                this.mainThumbnail.writeToParcel(parcel, 0);
            } else {
                parcel.writeInt(0);
            }
            parcel.writeInt(this.numSubThumbbails);
            parcel.writeStrongInterface(this.retriever);
        }

        public void readFromParcel(Parcel parcel) {
            if (parcel.readInt() != 0) {
                this.mainThumbnail = Bitmap.CREATOR.createFromParcel(parcel);
            } else {
                this.mainThumbnail = null;
            }
            this.numSubThumbbails = parcel.readInt();
            this.retriever = IThumbnailRetriever.Stub.asInterface(parcel.readStrongBinder());
        }

        private TaskThumbnails(Parcel parcel) {
            readFromParcel(parcel);
        }
    }

    public TaskThumbnails getTaskThumbnails(int i) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getTaskThumbnails(i);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public Bitmap getTaskTopThumbnail(int i) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getTaskTopThumbnail(i);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public void moveTaskToFront(int i, int i2) {
        moveTaskToFront(i, i2, null);
    }

    public void moveTaskToFront(int i, int i2, Bundle bundle) {
        try {
            ActivityManagerNative.getDefault().moveTaskToFront(i, i2, bundle);
        } catch (RemoteException unused) {
        }
    }

    public static class RunningServiceInfo implements Parcelable {
        public static final Parcelable.Creator<RunningServiceInfo> CREATOR = new Parcelable.Creator<RunningServiceInfo>() { // from class: android.app.ActivityManager.RunningServiceInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningServiceInfo createFromParcel(Parcel parcel) {
                return new RunningServiceInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningServiceInfo[] newArray(int i) {
                return new RunningServiceInfo[i];
            }
        };
        public static final int FLAG_FOREGROUND = 2;
        public static final int FLAG_PERSISTENT_PROCESS = 8;
        public static final int FLAG_STARTED = 1;
        public static final int FLAG_SYSTEM_PROCESS = 4;
        public long activeSince;
        public int clientCount;
        public int clientLabel;
        public String clientPackage;
        public int crashCount;
        public int flags;
        public boolean foreground;
        public long lastActivityTime;
        public int pid;
        public String process;
        public long restarting;
        public ComponentName service;
        public boolean started;
        public int uid;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public RunningServiceInfo() {
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            ComponentName.writeToParcel(this.service, parcel);
            parcel.writeInt(this.pid);
            parcel.writeInt(this.uid);
            parcel.writeString(this.process);
            parcel.writeInt(this.foreground ? 1 : 0);
            parcel.writeLong(this.activeSince);
            parcel.writeInt(this.started ? 1 : 0);
            parcel.writeInt(this.clientCount);
            parcel.writeInt(this.crashCount);
            parcel.writeLong(this.lastActivityTime);
            parcel.writeLong(this.restarting);
            parcel.writeInt(this.flags);
            parcel.writeString(this.clientPackage);
            parcel.writeInt(this.clientLabel);
        }

        public void readFromParcel(Parcel parcel) {
            this.service = ComponentName.readFromParcel(parcel);
            this.pid = parcel.readInt();
            this.uid = parcel.readInt();
            this.process = parcel.readString();
            this.foreground = parcel.readInt() != 0;
            this.activeSince = parcel.readLong();
            this.started = parcel.readInt() != 0;
            this.clientCount = parcel.readInt();
            this.crashCount = parcel.readInt();
            this.lastActivityTime = parcel.readLong();
            this.restarting = parcel.readLong();
            this.flags = parcel.readInt();
            this.clientPackage = parcel.readString();
            this.clientLabel = parcel.readInt();
        }

        private RunningServiceInfo(Parcel parcel) {
            readFromParcel(parcel);
        }
    }

    public List<RunningServiceInfo> getRunningServices(int i) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getServices(i, 0);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public PendingIntent getRunningServiceControlPanel(ComponentName componentName) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getRunningServiceControlPanel(componentName);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public static class MemoryInfo implements Parcelable {
        public static final Parcelable.Creator<MemoryInfo> CREATOR = new Parcelable.Creator<MemoryInfo>() { // from class: android.app.ActivityManager.MemoryInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MemoryInfo createFromParcel(Parcel parcel) {
                return new MemoryInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MemoryInfo[] newArray(int i) {
                return new MemoryInfo[i];
            }
        };
        public long availMem;
        public long foregroundAppThreshold;
        public long hiddenAppThreshold;
        public boolean lowMemory;
        public long secondaryServerThreshold;
        public long threshold;
        public long totalMem;
        public long visibleAppThreshold;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public MemoryInfo() {
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(this.availMem);
            parcel.writeLong(this.totalMem);
            parcel.writeLong(this.threshold);
            parcel.writeInt(this.lowMemory ? 1 : 0);
            parcel.writeLong(this.hiddenAppThreshold);
            parcel.writeLong(this.secondaryServerThreshold);
            parcel.writeLong(this.visibleAppThreshold);
            parcel.writeLong(this.foregroundAppThreshold);
        }

        public void readFromParcel(Parcel parcel) {
            this.availMem = parcel.readLong();
            this.totalMem = parcel.readLong();
            this.threshold = parcel.readLong();
            this.lowMemory = parcel.readInt() != 0;
            this.hiddenAppThreshold = parcel.readLong();
            this.secondaryServerThreshold = parcel.readLong();
            this.visibleAppThreshold = parcel.readLong();
            this.foregroundAppThreshold = parcel.readLong();
        }

        private MemoryInfo(Parcel parcel) {
            readFromParcel(parcel);
        }
    }

    public void getMemoryInfo(MemoryInfo memoryInfo) {
        try {
            ActivityManagerNative.getDefault().getMemoryInfo(memoryInfo);
        } catch (RemoteException unused) {
        }
    }

    public static class StackBoxInfo implements Parcelable {
        public static final Parcelable.Creator<StackBoxInfo> CREATOR = new Parcelable.Creator<StackBoxInfo>() { // from class: android.app.ActivityManager.StackBoxInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StackBoxInfo createFromParcel(Parcel parcel) {
                return new StackBoxInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StackBoxInfo[] newArray(int i) {
                return new StackBoxInfo[i];
            }
        };
        public Rect bounds;
        public StackBoxInfo[] children;
        public StackInfo stack;
        public int stackBoxId;
        public int stackId;
        public boolean vertical;
        public float weight;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.stackBoxId);
            parcel.writeFloat(this.weight);
            parcel.writeInt(this.vertical ? 1 : 0);
            this.bounds.writeToParcel(parcel, i);
            parcel.writeInt(this.stackId);
            StackBoxInfo[] stackBoxInfoArr = this.children;
            if (stackBoxInfoArr != null) {
                stackBoxInfoArr[0].writeToParcel(parcel, i);
                this.children[1].writeToParcel(parcel, i);
            } else {
                this.stack.writeToParcel(parcel, i);
            }
        }

        public void readFromParcel(Parcel parcel) {
            this.stackBoxId = parcel.readInt();
            this.weight = parcel.readFloat();
            this.vertical = parcel.readInt() == 1;
            this.bounds = Rect.CREATOR.createFromParcel(parcel);
            int i = parcel.readInt();
            this.stackId = i;
            if (i == -1) {
                StackBoxInfo[] stackBoxInfoArr = new StackBoxInfo[2];
                this.children = stackBoxInfoArr;
                Parcelable.Creator<StackBoxInfo> creator = CREATOR;
                stackBoxInfoArr[0] = creator.createFromParcel(parcel);
                this.children[1] = creator.createFromParcel(parcel);
                return;
            }
            this.stack = StackInfo.CREATOR.createFromParcel(parcel);
        }

        public StackBoxInfo() {
        }

        public StackBoxInfo(Parcel parcel) {
            readFromParcel(parcel);
        }

        public String toString(String str) {
            StringBuilder sb = new StringBuilder(256);
            sb.append(str);
            sb.append("Box id=" + this.stackBoxId);
            sb.append(" weight=" + this.weight);
            sb.append(" vertical=" + this.vertical);
            sb.append(" bounds=" + this.bounds.toShortString());
            sb.append("\n");
            if (this.children != null) {
                sb.append(str);
                sb.append("First child=\n");
                sb.append(this.children[0].toString(str + "  "));
                sb.append(str);
                sb.append("Second child=\n");
                sb.append(this.children[1].toString(str + "  "));
            } else {
                sb.append(str);
                sb.append("Stack=\n");
                sb.append(this.stack.toString(str + "  "));
            }
            return sb.toString();
        }

        public String toString() {
            return toString("");
        }
    }

    public static class StackInfo implements Parcelable {
        public static final Parcelable.Creator<StackInfo> CREATOR = new Parcelable.Creator<StackInfo>() { // from class: android.app.ActivityManager.StackInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StackInfo createFromParcel(Parcel parcel) {
                return new StackInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StackInfo[] newArray(int i) {
                return new StackInfo[i];
            }
        };
        public Rect bounds;
        public int stackId;
        public int[] taskIds;
        public String[] taskNames;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.stackId);
            parcel.writeInt(this.bounds.left);
            parcel.writeInt(this.bounds.top);
            parcel.writeInt(this.bounds.right);
            parcel.writeInt(this.bounds.bottom);
            parcel.writeIntArray(this.taskIds);
            parcel.writeStringArray(this.taskNames);
        }

        public void readFromParcel(Parcel parcel) {
            this.stackId = parcel.readInt();
            this.bounds = new Rect(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
            this.taskIds = parcel.createIntArray();
            this.taskNames = parcel.createStringArray();
        }

        public StackInfo() {
        }

        private StackInfo(Parcel parcel) {
            readFromParcel(parcel);
        }

        public String toString(String str) {
            StringBuilder sb = new StringBuilder(256);
            sb.append(str);
            sb.append("Stack id=");
            sb.append(this.stackId);
            sb.append(" bounds=");
            sb.append(this.bounds.toShortString());
            sb.append("\n");
            String str2 = str + "  ";
            for (int i = 0; i < this.taskIds.length; i++) {
                sb.append(str2);
                sb.append("taskId=");
                sb.append(this.taskIds[i]);
                sb.append(": ");
                sb.append(this.taskNames[i]);
                sb.append("\n");
            }
            return sb.toString();
        }

        public String toString() {
            return toString("");
        }
    }

    public boolean clearApplicationUserData(String str, IPackageDataObserver iPackageDataObserver) {
        try {
            return ActivityManagerNative.getDefault().clearApplicationUserData(str, iPackageDataObserver, UserHandle.myUserId());
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean clearApplicationUserData() {
        return clearApplicationUserData(this.mContext.getPackageName(), null);
    }

    public static class ProcessErrorStateInfo implements Parcelable {
        public static final int CRASHED = 1;
        public static final Parcelable.Creator<ProcessErrorStateInfo> CREATOR = new Parcelable.Creator<ProcessErrorStateInfo>() { // from class: android.app.ActivityManager.ProcessErrorStateInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ProcessErrorStateInfo createFromParcel(Parcel parcel) {
                return new ProcessErrorStateInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ProcessErrorStateInfo[] newArray(int i) {
                return new ProcessErrorStateInfo[i];
            }
        };
        public static final int NOT_RESPONDING = 2;
        public static final int NO_ERROR = 0;
        public int condition;
        public byte[] crashData;
        public String longMsg;
        public int pid;
        public String processName;
        public String shortMsg;
        public String stackTrace;
        public String tag;
        public int uid;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public ProcessErrorStateInfo() {
            this.crashData = null;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.condition);
            parcel.writeString(this.processName);
            parcel.writeInt(this.pid);
            parcel.writeInt(this.uid);
            parcel.writeString(this.tag);
            parcel.writeString(this.shortMsg);
            parcel.writeString(this.longMsg);
            parcel.writeString(this.stackTrace);
        }

        public void readFromParcel(Parcel parcel) {
            this.condition = parcel.readInt();
            this.processName = parcel.readString();
            this.pid = parcel.readInt();
            this.uid = parcel.readInt();
            this.tag = parcel.readString();
            this.shortMsg = parcel.readString();
            this.longMsg = parcel.readString();
            this.stackTrace = parcel.readString();
        }

        private ProcessErrorStateInfo(Parcel parcel) {
            this.crashData = null;
            readFromParcel(parcel);
        }
    }

    public List<ProcessErrorStateInfo> getProcessesInErrorState() {
        try {
            return ActivityManagerNative.getDefault().getProcessesInErrorState();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public static class RunningAppProcessInfo implements Parcelable {
        public static final Parcelable.Creator<RunningAppProcessInfo> CREATOR = new Parcelable.Creator<RunningAppProcessInfo>() { // from class: android.app.ActivityManager.RunningAppProcessInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningAppProcessInfo createFromParcel(Parcel parcel) {
                return new RunningAppProcessInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningAppProcessInfo[] newArray(int i) {
                return new RunningAppProcessInfo[i];
            }
        };
        public static final int FLAG_CANT_SAVE_STATE = 1;
        public static final int FLAG_HAS_ACTIVITIES = 4;
        public static final int FLAG_PERSISTENT = 2;
        public static final int IMPORTANCE_BACKGROUND = 400;
        public static final int IMPORTANCE_CANT_SAVE_STATE = 170;
        public static final int IMPORTANCE_EMPTY = 500;
        public static final int IMPORTANCE_FOREGROUND = 100;
        public static final int IMPORTANCE_PERCEPTIBLE = 130;
        public static final int IMPORTANCE_PERSISTENT = 50;
        public static final int IMPORTANCE_SERVICE = 300;
        public static final int IMPORTANCE_VISIBLE = 200;
        public static final int REASON_PROVIDER_IN_USE = 1;
        public static final int REASON_SERVICE_IN_USE = 2;
        public static final int REASON_UNKNOWN = 0;
        public int flags;
        public int importance;
        public int importanceReasonCode;
        public ComponentName importanceReasonComponent;
        public int importanceReasonImportance;
        public int importanceReasonPid;
        public int lastTrimLevel;
        public int lru;
        public int pid;
        public String[] pkgList;
        public String processName;
        public int uid;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public RunningAppProcessInfo() {
            this.importance = 100;
            this.importanceReasonCode = 0;
        }

        public RunningAppProcessInfo(String str, int i, String[] strArr) {
            this.processName = str;
            this.pid = i;
            this.pkgList = strArr;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.processName);
            parcel.writeInt(this.pid);
            parcel.writeInt(this.uid);
            parcel.writeStringArray(this.pkgList);
            parcel.writeInt(this.flags);
            parcel.writeInt(this.lastTrimLevel);
            parcel.writeInt(this.importance);
            parcel.writeInt(this.lru);
            parcel.writeInt(this.importanceReasonCode);
            parcel.writeInt(this.importanceReasonPid);
            ComponentName.writeToParcel(this.importanceReasonComponent, parcel);
            parcel.writeInt(this.importanceReasonImportance);
        }

        public void readFromParcel(Parcel parcel) {
            this.processName = parcel.readString();
            this.pid = parcel.readInt();
            this.uid = parcel.readInt();
            this.pkgList = parcel.readStringArray();
            this.flags = parcel.readInt();
            this.lastTrimLevel = parcel.readInt();
            this.importance = parcel.readInt();
            this.lru = parcel.readInt();
            this.importanceReasonCode = parcel.readInt();
            this.importanceReasonPid = parcel.readInt();
            this.importanceReasonComponent = ComponentName.readFromParcel(parcel);
            this.importanceReasonImportance = parcel.readInt();
        }

        private RunningAppProcessInfo(Parcel parcel) {
            readFromParcel(parcel);
        }
    }

    public List<ApplicationInfo> getRunningExternalApplications() {
        try {
            return ActivityManagerNative.getDefault().getRunningExternalApplications();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public List<RunningAppProcessInfo> getRunningAppProcesses() {
        try {
            return ActivityManagerNative.getDefault().getRunningAppProcesses();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public static void getMyMemoryState(RunningAppProcessInfo runningAppProcessInfo) {
        try {
            ActivityManagerNative.getDefault().getMyMemoryState(runningAppProcessInfo);
        } catch (RemoteException unused) {
        }
    }

    public Debug.MemoryInfo[] getProcessMemoryInfo(int[] iArr) {
        try {
            return ActivityManagerNative.getDefault().getProcessMemoryInfo(iArr);
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Deprecated
    public void restartPackage(String str) {
        killBackgroundProcesses(str);
    }

    public void killBackgroundProcesses(String str) {
        try {
            ActivityManagerNative.getDefault().killBackgroundProcesses(str, UserHandle.myUserId());
        } catch (RemoteException unused) {
        }
    }

    public void forceStopPackage(String str) {
        try {
            ActivityManagerNative.getDefault().forceStopPackage(str, UserHandle.myUserId());
        } catch (RemoteException unused) {
        }
    }

    public ConfigurationInfo getDeviceConfigurationInfo() {
        try {
            return ActivityManagerNative.getDefault().getDeviceConfigurationInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public int getLauncherLargeIconDensity() {
        Resources resources = this.mContext.getResources();
        int i = resources.getDisplayMetrics().densityDpi;
        if (resources.getConfiguration().smallestScreenWidthDp < 600) {
            return i;
        }
        if (i == 120) {
            return 160;
        }
        if (i == 160) {
            return 240;
        }
        if (i == 213 || i == 240) {
            return 320;
        }
        if (i == 320) {
            return 480;
        }
        if (i != 480) {
            return (int) ((i * 1.5f) + 0.5f);
        }
        return 640;
    }

    public int getLauncherLargeIconSize() {
        Resources resources = this.mContext.getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.app_icon_size);
        if (resources.getConfiguration().smallestScreenWidthDp < 600) {
            return dimensionPixelSize;
        }
        int i = resources.getDisplayMetrics().densityDpi;
        if (i == 120) {
            return (dimensionPixelSize * 160) / 120;
        }
        if (i == 160) {
            return (dimensionPixelSize * 240) / 160;
        }
        if (i == 213) {
            return (dimensionPixelSize * 320) / 240;
        }
        if (i == 240) {
            return (dimensionPixelSize * 320) / 240;
        }
        if (i != 320) {
            return i != 480 ? (int) ((dimensionPixelSize * 1.5f) + 0.5f) : ((dimensionPixelSize * 320) * 2) / 480;
        }
        return (dimensionPixelSize * 480) / 320;
    }

    public static boolean isUserAMonkey() {
        try {
            return ActivityManagerNative.getDefault().isUserAMonkey();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static boolean isRunningInTestHarness() {
        return SystemProperties.getBoolean("ro.test_harness", false);
    }

    public Map<String, Integer> getAllPackageLaunchCounts() {
        try {
            IUsageStats iUsageStatsAsInterface = IUsageStats.Stub.asInterface(ServiceManager.getService("usagestats"));
            if (iUsageStatsAsInterface == null) {
                return new HashMap();
            }
            PkgUsageStats[] allPkgUsageStats = iUsageStatsAsInterface.getAllPkgUsageStats();
            if (allPkgUsageStats == null) {
                return new HashMap();
            }
            HashMap map = new HashMap();
            for (PkgUsageStats pkgUsageStats : allPkgUsageStats) {
                map.put(pkgUsageStats.packageName, Integer.valueOf(pkgUsageStats.launchCount));
            }
            return map;
        } catch (RemoteException e) {
            Log.w(TAG, "Could not query launch counts", e);
            return new HashMap();
        }
    }

    public static int checkComponentPermission(String str, int i, int i2, boolean z) {
        if (i == 0 || i == 1000) {
            return 0;
        }
        if (UserHandle.isIsolated(i)) {
            return -1;
        }
        if (i2 >= 0 && UserHandle.isSameApp(i, i2)) {
            return 0;
        }
        if (!z) {
            return -1;
        }
        if (str == null) {
            return 0;
        }
        try {
            return AppGlobals.getPackageManager().checkUidPermission(str, i);
        } catch (RemoteException e) {
            Slog.e(TAG, "PackageManager is dead?!?", e);
            return -1;
        }
    }

    public static int checkUidPermission(String str, int i) {
        try {
            return AppGlobals.getPackageManager().checkUidPermission(str, i);
        } catch (RemoteException e) {
            Slog.e(TAG, "PackageManager is dead?!?", e);
            return -1;
        }
    }

    public static int handleIncomingUser(int i, int i2, int i3, boolean z, boolean z2, String str, String str2) {
        if (UserHandle.getUserId(i2) == i3) {
            return i3;
        }
        try {
            return ActivityManagerNative.getDefault().handleIncomingUser(i, i2, i3, z, z2, str, str2);
        } catch (RemoteException e) {
            throw new SecurityException("Failed calling activity manager", e);
        }
    }

    public static int getCurrentUser() {
        try {
            UserInfo currentUser = ActivityManagerNative.getDefault().getCurrentUser();
            if (currentUser != null) {
                return currentUser.id;
            }
            return 0;
        } catch (RemoteException unused) {
            return 0;
        }
    }

    public PkgUsageStats[] getAllPackageUsageStats() {
        try {
            IUsageStats iUsageStatsAsInterface = IUsageStats.Stub.asInterface(ServiceManager.getService("usagestats"));
            if (iUsageStatsAsInterface != null) {
                return iUsageStatsAsInterface.getAllPkgUsageStats();
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Could not query usage stats", e);
        }
        return new PkgUsageStats[0];
    }

    public boolean switchUser(int i) {
        try {
            return ActivityManagerNative.getDefault().switchUser(i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean isUserRunning(int i) {
        try {
            return ActivityManagerNative.getDefault().isUserRunning(i, false);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void dumpPackageState(FileDescriptor fileDescriptor, String str) {
        dumpPackageStateStatic(fileDescriptor, str);
    }

    public static void dumpPackageStateStatic(FileDescriptor fileDescriptor, String str) {
        FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(fileDescriptor));
        dumpService(fastPrintWriter, fileDescriptor, Context.ACTIVITY_SERVICE, new String[]{"-a", "package", str});
        fastPrintWriter.println();
        dumpService(fastPrintWriter, fileDescriptor, "meminfo", new String[]{"--local", str});
        fastPrintWriter.println();
        dumpService(fastPrintWriter, fileDescriptor, "procstats", new String[]{"-a", str});
        fastPrintWriter.println();
        dumpService(fastPrintWriter, fileDescriptor, "usagestats", new String[]{"--packages", str});
        fastPrintWriter.println();
        dumpService(fastPrintWriter, fileDescriptor, "package", new String[]{str});
        fastPrintWriter.println();
        dumpService(fastPrintWriter, fileDescriptor, BatteryStats.SERVICE_NAME, new String[]{str});
        fastPrintWriter.flush();
    }

    private static void dumpService(PrintWriter printWriter, FileDescriptor fileDescriptor, String str, String[] strArr) {
        printWriter.print("DUMP OF SERVICE ");
        printWriter.print(str);
        printWriter.println(":");
        IBinder iBinderCheckService = ServiceManager.checkService(str);
        if (iBinderCheckService == null) {
            printWriter.println("  (Service not found)");
            return;
        }
        TransferPipe transferPipe = null;
        try {
            printWriter.flush();
            TransferPipe transferPipe2 = new TransferPipe();
            try {
                transferPipe2.setBufferPrefix("  ");
                iBinderCheckService.dumpAsync(transferPipe2.getWriteFd().getFileDescriptor(), strArr);
                transferPipe2.go(fileDescriptor);
            } catch (Throwable th) {
                th = th;
                transferPipe = transferPipe2;
                if (transferPipe != null) {
                    transferPipe.kill();
                }
                printWriter.println("Failure dumping service:");
                th.printStackTrace(printWriter);
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }
}
