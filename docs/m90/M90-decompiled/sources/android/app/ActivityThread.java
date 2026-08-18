package android.app;

import android.R;
import android.app.Activity;
import android.app.IActivityManager;
import android.app.backup.BackupAgent;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDebug;
import android.ddm.DdmHandleAppName;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.display.DisplayManagerGlobal;
import android.net.IConnectivityManager;
import android.net.Proxy;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.renderscript.RenderScript;
import android.security.AndroidKeyStoreProvider;
import android.telephony.PhoneNumberUtils;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SuperNotCalledException;
import android.view.HardwareRenderer;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewRootImpl;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.os.BinderInternal;
import com.android.internal.os.RuntimeInit;
import com.android.internal.os.SamplingProfilerIntegration;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.Objects;
import com.android.org.conscrypt.OpenSSLSocketImpl;
import com.google.android.collect.Lists;
import dalvik.system.CloseGuard;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.security.Security;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;
import libcore.io.DropBox;
import libcore.io.EventLogger;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes.dex */
public final class ActivityThread {
    private static final int ACTIVITY_THREAD_CHECKIN_VERSION = 3;
    private static final boolean DEBUG_BACKUP = false;
    public static final boolean DEBUG_BROADCAST = false;
    public static final boolean DEBUG_CONFIGURATION = false;
    private static final boolean DEBUG_MEMORY_TRIM = false;
    static final boolean DEBUG_MESSAGES = false;
    private static final boolean DEBUG_PROVIDER = false;
    private static final boolean DEBUG_RESULTS = false;
    private static final boolean DEBUG_SERVICE = false;
    private static final String HEAP_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s";
    private static final String HEAP_FULL_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s";
    private static final int LOG_ON_PAUSE_CALLED = 30021;
    private static final int LOG_ON_RESUME_CALLED = 30022;
    private static final long MIN_TIME_BETWEEN_GCS = 5000;
    private static final int SQLITE_MEM_RELEASED_EVENT_LOG_TAG = 75003;
    public static final String TAG = "ActivityThread";
    static final boolean localLOGV = false;
    private static ActivityThread sCurrentActivityThread;
    static Handler sMainThreadHandler;
    static IPackageManager sPackageManager;
    final ApplicationThread mAppThread;
    AppBindData mBoundApplication;
    Configuration mCompatConfiguration;
    Configuration mConfiguration;
    int mCurDefaultDisplayDpi;
    boolean mDensityCompatMode;
    final H mH;
    Application mInitialApplication;
    Instrumentation mInstrumentation;
    Profiler mProfiler;
    private static final Bitmap.Config THUMBNAIL_FORMAT = Bitmap.Config.RGB_565;
    private static final Pattern PATTERN_SEMICOLON = Pattern.compile(";");
    static ContextImpl mSystemContext = null;
    private static final ThreadLocal<Intent> sCurrentBroadcastIntent = new ThreadLocal<>();
    final Looper mLooper = Looper.myLooper();
    final ArrayMap<IBinder, ActivityClientRecord> mActivities = new ArrayMap<>();
    ActivityClientRecord mNewActivities = null;
    int mNumVisibleActivities = 0;
    final ArrayMap<IBinder, Service> mServices = new ArrayMap<>();
    final ArrayList<Application> mAllApplications = new ArrayList<>();
    final ArrayMap<String, BackupAgent> mBackupAgents = new ArrayMap<>();
    String mInstrumentationAppDir = null;
    String mInstrumentationAppLibraryDir = null;
    String mInstrumentationAppPackage = null;
    String mInstrumentedAppDir = null;
    String mInstrumentedAppLibraryDir = null;
    boolean mSystemThread = false;
    boolean mJitEnabled = false;
    final ArrayMap<String, WeakReference<LoadedApk>> mPackages = new ArrayMap<>();
    final ArrayMap<String, WeakReference<LoadedApk>> mResourcePackages = new ArrayMap<>();
    final ArrayList<ActivityClientRecord> mRelaunchingActivities = new ArrayList<>();
    Configuration mPendingConfiguration = null;
    final ArrayMap<ProviderKey, ProviderClientRecord> mProviderMap = new ArrayMap<>();
    final ArrayMap<IBinder, ProviderRefCount> mProviderRefCountMap = new ArrayMap<>();
    final ArrayMap<IBinder, ProviderClientRecord> mLocalProviders = new ArrayMap<>();
    final ArrayMap<ComponentName, ProviderClientRecord> mLocalProvidersByName = new ArrayMap<>();
    final ArrayMap<Activity, ArrayList<OnActivityPausedListener>> mOnPauseListeners = new ArrayMap<>();
    final GcIdler mGcIdler = new GcIdler();
    boolean mGcIdlerScheduled = false;
    Bundle mCoreSettings = null;
    private Configuration mMainThreadConfig = new Configuration();
    private int mThumbnailWidth = -1;
    private int mThumbnailHeight = -1;
    private Bitmap mAvailThumbnailBitmap = null;
    private Canvas mThumbnailCanvas = null;
    private final ResourcesManager mResourcesManager = ResourcesManager.getInstance();

    /* JADX INFO: Access modifiers changed from: private */
    public native void dumpGraphicsInfo(FileDescriptor fileDescriptor);

    private static final class ProviderKey {
        final String authority;
        final int userId;

        public ProviderKey(String str, int i) {
            this.authority = str;
            this.userId = i;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ProviderKey)) {
                return false;
            }
            ProviderKey providerKey = (ProviderKey) obj;
            return Objects.equal(this.authority, providerKey.authority) && this.userId == providerKey.userId;
        }

        public int hashCode() {
            String str = this.authority;
            return (str != null ? str.hashCode() : 0) ^ this.userId;
        }
    }

    static final class ActivityClientRecord {
        Activity activity;
        ActivityInfo activityInfo;
        boolean autoStopProfiler;
        CompatibilityInfo compatInfo;
        Configuration createdConfig;
        int ident;
        Intent intent;
        boolean isForward;
        Activity.NonConfigurationInstances lastNonConfigurationInstances;
        View mPendingRemoveWindow;
        WindowManager mPendingRemoveWindowManager;
        Configuration newConfig;
        boolean onlyLocalRequest;
        LoadedApk packageInfo;
        int pendingConfigChanges;
        List<Intent> pendingIntents;
        List<ResultInfo> pendingResults;
        ParcelFileDescriptor profileFd;
        String profileFile;
        boolean startsNotResumed;
        Bundle state;
        IBinder token;
        Window window;
        Activity parent = null;
        String embeddedID = null;
        boolean paused = false;
        boolean stopped = false;
        boolean hideForNow = false;
        ActivityClientRecord nextIdle = null;

        ActivityClientRecord() {
        }

        public boolean isPreHoneycomb() {
            Activity activity = this.activity;
            return activity != null && activity.getApplicationInfo().targetSdkVersion < 11;
        }

        public String toString() {
            Intent intent = this.intent;
            ComponentName component = intent != null ? intent.getComponent() : null;
            return "ActivityRecord{" + Integer.toHexString(System.identityHashCode(this)) + " token=" + this.token + " " + (component == null ? "no component name" : component.toShortString()) + "}";
        }
    }

    final class ProviderClientRecord {
        final IActivityManager.ContentProviderHolder mHolder;
        final ContentProvider mLocalProvider;
        final String[] mNames;
        final IContentProvider mProvider;

        ProviderClientRecord(String[] strArr, IContentProvider iContentProvider, ContentProvider contentProvider, IActivityManager.ContentProviderHolder contentProviderHolder) {
            this.mNames = strArr;
            this.mProvider = iContentProvider;
            this.mLocalProvider = contentProvider;
            this.mHolder = contentProviderHolder;
        }
    }

    static final class NewIntentData {
        List<Intent> intents;
        IBinder token;

        NewIntentData() {
        }

        public String toString() {
            return "NewIntentData{intents=" + this.intents + " token=" + this.token + "}";
        }
    }

    static final class ReceiverData extends BroadcastReceiver.PendingResult {
        CompatibilityInfo compatInfo;
        ActivityInfo info;
        Intent intent;

        public ReceiverData(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, IBinder iBinder, int i2) {
            super(i, str, bundle, 0, z, z2, iBinder, i2);
            this.intent = intent;
        }

        public String toString() {
            return "ReceiverData{intent=" + this.intent + " packageName=" + this.info.packageName + " resultCode=" + getResultCode() + " resultData=" + getResultData() + " resultExtras=" + getResultExtras(false) + "}";
        }
    }

    static final class CreateBackupAgentData {
        ApplicationInfo appInfo;
        int backupMode;
        CompatibilityInfo compatInfo;

        CreateBackupAgentData() {
        }

        public String toString() {
            return "CreateBackupAgentData{appInfo=" + this.appInfo + " backupAgent=" + this.appInfo.backupAgentName + " mode=" + this.backupMode + "}";
        }
    }

    static final class CreateServiceData {
        CompatibilityInfo compatInfo;
        ServiceInfo info;
        Intent intent;
        IBinder token;

        CreateServiceData() {
        }

        public String toString() {
            return "CreateServiceData{token=" + this.token + " className=" + this.info.name + " packageName=" + this.info.packageName + " intent=" + this.intent + "}";
        }
    }

    static final class BindServiceData {
        Intent intent;
        boolean rebind;
        IBinder token;

        BindServiceData() {
        }

        public String toString() {
            return "BindServiceData{token=" + this.token + " intent=" + this.intent + "}";
        }
    }

    static final class ServiceArgsData {
        Intent args;
        int flags;
        int startId;
        boolean taskRemoved;
        IBinder token;

        ServiceArgsData() {
        }

        public String toString() {
            return "ServiceArgsData{token=" + this.token + " startId=" + this.startId + " args=" + this.args + "}";
        }
    }

    static final class AppBindData {
        ApplicationInfo appInfo;
        CompatibilityInfo compatInfo;
        Configuration config;
        int debugMode;
        boolean enableOpenGlTrace;
        LoadedApk info;
        boolean initAutoStopProfiler;
        ParcelFileDescriptor initProfileFd;
        String initProfileFile;
        Bundle instrumentationArgs;
        ComponentName instrumentationName;
        IUiAutomationConnection instrumentationUiAutomationConnection;
        IInstrumentationWatcher instrumentationWatcher;
        boolean persistent;
        String processName;
        List<ProviderInfo> providers;
        boolean restrictedBackupMode;

        AppBindData() {
        }

        public String toString() {
            return "AppBindData{appInfo=" + this.appInfo + "}";
        }
    }

    static final class Profiler {
        boolean autoStopProfiler;
        boolean handlingProfiling;
        ParcelFileDescriptor profileFd;
        String profileFile;
        boolean profiling;

        Profiler() {
        }

        public void setProfiler(String str, ParcelFileDescriptor parcelFileDescriptor) {
            if (this.profiling) {
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                        return;
                    } catch (IOException unused) {
                        return;
                    }
                }
                return;
            }
            ParcelFileDescriptor parcelFileDescriptor2 = this.profileFd;
            if (parcelFileDescriptor2 != null) {
                try {
                    parcelFileDescriptor2.close();
                } catch (IOException unused2) {
                }
            }
            this.profileFile = str;
            this.profileFd = parcelFileDescriptor;
        }

        public void startProfiling() {
            ParcelFileDescriptor parcelFileDescriptor = this.profileFd;
            if (parcelFileDescriptor == null || this.profiling) {
                return;
            }
            try {
                Debug.startMethodTracing(this.profileFile, parcelFileDescriptor.getFileDescriptor(), 8388608, 0);
                this.profiling = true;
            } catch (RuntimeException unused) {
                Slog.w(ActivityThread.TAG, "Profiling failed on path " + this.profileFile);
                try {
                    this.profileFd.close();
                    this.profileFd = null;
                } catch (IOException e) {
                    Slog.w(ActivityThread.TAG, "Failure closing profile fd", e);
                }
            }
        }

        public void stopProfiling() {
            if (this.profiling) {
                this.profiling = false;
                Debug.stopMethodTracing();
                ParcelFileDescriptor parcelFileDescriptor = this.profileFd;
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException unused) {
                    }
                }
                this.profileFd = null;
                this.profileFile = null;
            }
        }
    }

    static final class DumpComponentInfo {
        String[] args;
        ParcelFileDescriptor fd;
        String prefix;
        IBinder token;

        DumpComponentInfo() {
        }
    }

    static final class ResultData {
        List<ResultInfo> results;
        IBinder token;

        ResultData() {
        }

        public String toString() {
            return "ResultData{token=" + this.token + " results" + this.results + "}";
        }
    }

    static final class ContextCleanupInfo {
        ContextImpl context;
        String what;
        String who;

        ContextCleanupInfo() {
        }
    }

    static final class ProfilerControlData {
        ParcelFileDescriptor fd;
        String path;

        ProfilerControlData() {
        }
    }

    static final class DumpHeapData {
        ParcelFileDescriptor fd;
        String path;

        DumpHeapData() {
        }
    }

    static final class UpdateCompatibilityData {
        CompatibilityInfo info;
        String pkg;

        UpdateCompatibilityData() {
        }
    }

    static final class RequestAssistContextExtras {
        IBinder activityToken;
        IBinder requestToken;
        int requestType;

        RequestAssistContextExtras() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class ApplicationThread extends ApplicationThreadNative {
        private static final String DB_INFO_FORMAT = "  %8s %8s %14s %14s  %s";
        private static final String ONE_COUNT_COLUMN = "%21s %8d";
        private static final String TWO_COUNT_COLUMNS = "%21s %8d %21s %8d";
        private int mLastProcessState;

        private ApplicationThread() {
            this.mLastProcessState = -1;
        }

        private void updatePendingConfiguration(Configuration configuration) {
            synchronized (ActivityThread.this.mResourcesManager) {
                if (ActivityThread.this.mPendingConfiguration == null || ActivityThread.this.mPendingConfiguration.isOtherSeqNewer(configuration)) {
                    ActivityThread.this.mPendingConfiguration = configuration;
                }
            }
        }

        @Override // android.app.IApplicationThread
        public final void schedulePauseActivity(IBinder iBinder, boolean z, boolean z2, int i) {
            ActivityThread.this.sendMessage(z ? 102 : 101, iBinder, z2 ? 1 : 0, i);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleStopActivity(IBinder iBinder, boolean z, int i) {
            ActivityThread.this.sendMessage(z ? 103 : 104, iBinder, 0, i);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleWindowVisibility(IBinder iBinder, boolean z) {
            ActivityThread.this.sendMessage(z ? 105 : 106, iBinder);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleSleeping(IBinder iBinder, boolean z) {
            ActivityThread.this.sendMessage(137, iBinder, z ? 1 : 0);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleResumeActivity(IBinder iBinder, int i, boolean z) {
            updateProcessState(i, false);
            ActivityThread.this.sendMessage(107, iBinder, z ? 1 : 0);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleSendResult(IBinder iBinder, List<ResultInfo> list) {
            ResultData resultData = new ResultData();
            resultData.token = iBinder;
            resultData.results = list;
            ActivityThread.this.sendMessage(108, resultData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleLaunchActivity(Intent intent, IBinder iBinder, int i, ActivityInfo activityInfo, Configuration configuration, CompatibilityInfo compatibilityInfo, int i2, Bundle bundle, List<ResultInfo> list, List<Intent> list2, boolean z, boolean z2, String str, ParcelFileDescriptor parcelFileDescriptor, boolean z3) {
            updateProcessState(i2, false);
            ActivityClientRecord activityClientRecord = new ActivityClientRecord();
            activityClientRecord.token = iBinder;
            activityClientRecord.ident = i;
            activityClientRecord.intent = intent;
            activityClientRecord.activityInfo = activityInfo;
            activityClientRecord.compatInfo = compatibilityInfo;
            activityClientRecord.state = bundle;
            activityClientRecord.pendingResults = list;
            activityClientRecord.pendingIntents = list2;
            activityClientRecord.startsNotResumed = z;
            activityClientRecord.isForward = z2;
            activityClientRecord.profileFile = str;
            activityClientRecord.profileFd = parcelFileDescriptor;
            activityClientRecord.autoStopProfiler = z3;
            updatePendingConfiguration(configuration);
            ActivityThread.this.sendMessage(100, activityClientRecord);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleRelaunchActivity(IBinder iBinder, List<ResultInfo> list, List<Intent> list2, int i, boolean z, Configuration configuration) {
            ActivityThread.this.requestRelaunchActivity(iBinder, list, list2, i, z, configuration, true);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleNewIntent(List<Intent> list, IBinder iBinder) {
            NewIntentData newIntentData = new NewIntentData();
            newIntentData.intents = list;
            newIntentData.token = iBinder;
            ActivityThread.this.sendMessage(112, newIntentData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleDestroyActivity(IBinder iBinder, boolean z, int i) {
            ActivityThread.this.sendMessage(109, iBinder, z ? 1 : 0, i);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleReceiver(Intent intent, ActivityInfo activityInfo, CompatibilityInfo compatibilityInfo, int i, String str, Bundle bundle, boolean z, int i2, int i3) {
            updateProcessState(i3, false);
            ReceiverData receiverData = new ReceiverData(intent, i, str, bundle, z, false, ActivityThread.this.mAppThread.asBinder(), i2);
            receiverData.info = activityInfo;
            receiverData.compatInfo = compatibilityInfo;
            ActivityThread.this.sendMessage(113, receiverData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleCreateBackupAgent(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo, int i) {
            CreateBackupAgentData createBackupAgentData = new CreateBackupAgentData();
            createBackupAgentData.appInfo = applicationInfo;
            createBackupAgentData.compatInfo = compatibilityInfo;
            createBackupAgentData.backupMode = i;
            ActivityThread.this.sendMessage(128, createBackupAgentData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleDestroyBackupAgent(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo) {
            CreateBackupAgentData createBackupAgentData = new CreateBackupAgentData();
            createBackupAgentData.appInfo = applicationInfo;
            createBackupAgentData.compatInfo = compatibilityInfo;
            ActivityThread.this.sendMessage(129, createBackupAgentData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleCreateService(IBinder iBinder, ServiceInfo serviceInfo, CompatibilityInfo compatibilityInfo, int i) {
            updateProcessState(i, false);
            CreateServiceData createServiceData = new CreateServiceData();
            createServiceData.token = iBinder;
            createServiceData.info = serviceInfo;
            createServiceData.compatInfo = compatibilityInfo;
            ActivityThread.this.sendMessage(114, createServiceData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleBindService(IBinder iBinder, Intent intent, boolean z, int i) {
            updateProcessState(i, false);
            BindServiceData bindServiceData = new BindServiceData();
            bindServiceData.token = iBinder;
            bindServiceData.intent = intent;
            bindServiceData.rebind = z;
            ActivityThread.this.sendMessage(121, bindServiceData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleUnbindService(IBinder iBinder, Intent intent) {
            BindServiceData bindServiceData = new BindServiceData();
            bindServiceData.token = iBinder;
            bindServiceData.intent = intent;
            ActivityThread.this.sendMessage(122, bindServiceData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleServiceArgs(IBinder iBinder, boolean z, int i, int i2, Intent intent) {
            ServiceArgsData serviceArgsData = new ServiceArgsData();
            serviceArgsData.token = iBinder;
            serviceArgsData.taskRemoved = z;
            serviceArgsData.startId = i;
            serviceArgsData.flags = i2;
            serviceArgsData.args = intent;
            ActivityThread.this.sendMessage(115, serviceArgsData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleStopService(IBinder iBinder) {
            ActivityThread.this.sendMessage(116, iBinder);
        }

        @Override // android.app.IApplicationThread
        public final void bindApplication(String str, ApplicationInfo applicationInfo, List<ProviderInfo> list, ComponentName componentName, String str2, ParcelFileDescriptor parcelFileDescriptor, boolean z, Bundle bundle, IInstrumentationWatcher iInstrumentationWatcher, IUiAutomationConnection iUiAutomationConnection, int i, boolean z2, boolean z3, boolean z4, Configuration configuration, CompatibilityInfo compatibilityInfo, Map<String, IBinder> map, Bundle bundle2) {
            if (map != null) {
                ServiceManager.initServiceCache(map);
            }
            setCoreSettings(bundle2);
            AppBindData appBindData = new AppBindData();
            appBindData.processName = str;
            appBindData.appInfo = applicationInfo;
            appBindData.providers = list;
            appBindData.instrumentationName = componentName;
            appBindData.instrumentationArgs = bundle;
            appBindData.instrumentationWatcher = iInstrumentationWatcher;
            appBindData.instrumentationUiAutomationConnection = iUiAutomationConnection;
            appBindData.debugMode = i;
            appBindData.enableOpenGlTrace = z2;
            appBindData.restrictedBackupMode = z3;
            appBindData.persistent = z4;
            appBindData.config = configuration;
            appBindData.compatInfo = compatibilityInfo;
            appBindData.initProfileFile = str2;
            appBindData.initProfileFd = parcelFileDescriptor;
            appBindData.initAutoStopProfiler = false;
            ActivityThread.this.sendMessage(110, appBindData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleExit() {
            ActivityThread.this.sendMessage(111, null);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleSuicide() {
            ActivityThread.this.sendMessage(130, null);
        }

        @Override // android.app.IApplicationThread
        public void requestThumbnail(IBinder iBinder) {
            ActivityThread.this.sendMessage(117, iBinder);
        }

        @Override // android.app.IApplicationThread
        public void scheduleConfigurationChanged(Configuration configuration) {
            updatePendingConfiguration(configuration);
            ActivityThread.this.sendMessage(118, configuration);
        }

        @Override // android.app.IApplicationThread
        public void updateTimeZone() {
            TimeZone.setDefault(null);
        }

        @Override // android.app.IApplicationThread
        public void clearDnsCache() {
            InetAddress.clearDnsCache();
        }

        @Override // android.app.IApplicationThread
        public void setHttpProxy(String str, String str2, String str3, String str4) {
            Proxy.setHttpProxySystemProperty(str, str2, str3, str4);
        }

        @Override // android.app.IApplicationThread
        public void processInBackground() {
            ActivityThread.this.mH.removeMessages(120);
            ActivityThread.this.mH.sendMessage(ActivityThread.this.mH.obtainMessage(120));
        }

        @Override // android.app.IApplicationThread
        public void dumpService(FileDescriptor fileDescriptor, IBinder iBinder, String[] strArr) {
            DumpComponentInfo dumpComponentInfo = new DumpComponentInfo();
            try {
                dumpComponentInfo.fd = ParcelFileDescriptor.dup(fileDescriptor);
                dumpComponentInfo.token = iBinder;
                dumpComponentInfo.args = strArr;
                ActivityThread.this.sendMessage(123, dumpComponentInfo, 0, 0, true);
            } catch (IOException e) {
                Slog.w(ActivityThread.TAG, "dumpService failed", e);
            }
        }

        @Override // android.app.IApplicationThread
        public void scheduleRegisteredReceiver(IIntentReceiver iIntentReceiver, Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2, int i3) throws RemoteException {
            updateProcessState(i3, false);
            iIntentReceiver.performReceive(intent, i, str, bundle, z, z2, i2);
        }

        @Override // android.app.IApplicationThread
        public void scheduleLowMemory() {
            ActivityThread.this.sendMessage(124, null);
        }

        @Override // android.app.IApplicationThread
        public void scheduleActivityConfigurationChanged(IBinder iBinder) {
            ActivityThread.this.sendMessage(125, iBinder);
        }

        @Override // android.app.IApplicationThread
        public void profilerControl(boolean z, String str, ParcelFileDescriptor parcelFileDescriptor, int i) {
            ProfilerControlData profilerControlData = new ProfilerControlData();
            profilerControlData.path = str;
            profilerControlData.fd = parcelFileDescriptor;
            ActivityThread.this.sendMessage(127, profilerControlData, z ? 1 : 0, i);
        }

        @Override // android.app.IApplicationThread
        public void dumpHeap(boolean z, String str, ParcelFileDescriptor parcelFileDescriptor) {
            DumpHeapData dumpHeapData = new DumpHeapData();
            dumpHeapData.path = str;
            dumpHeapData.fd = parcelFileDescriptor;
            ActivityThread.this.sendMessage(135, dumpHeapData, z ? 1 : 0, 0, true);
        }

        @Override // android.app.IApplicationThread
        public void setSchedulingGroup(int i) {
            try {
                Process.setProcessGroup(Process.myPid(), i);
            } catch (Exception e) {
                Slog.w(ActivityThread.TAG, "Failed setting process group to " + i, e);
            }
        }

        @Override // android.app.IApplicationThread
        public void dispatchPackageBroadcast(int i, String[] strArr) {
            ActivityThread.this.sendMessage(133, strArr, i);
        }

        @Override // android.app.IApplicationThread
        public void scheduleCrash(String str) {
            ActivityThread.this.sendMessage(134, str);
        }

        @Override // android.app.IApplicationThread
        public void dumpActivity(FileDescriptor fileDescriptor, IBinder iBinder, String str, String[] strArr) {
            DumpComponentInfo dumpComponentInfo = new DumpComponentInfo();
            try {
                dumpComponentInfo.fd = ParcelFileDescriptor.dup(fileDescriptor);
                dumpComponentInfo.token = iBinder;
                dumpComponentInfo.prefix = str;
                dumpComponentInfo.args = strArr;
                ActivityThread.this.sendMessage(136, dumpComponentInfo, 0, 0, true);
            } catch (IOException e) {
                Slog.w(ActivityThread.TAG, "dumpActivity failed", e);
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpProvider(FileDescriptor fileDescriptor, IBinder iBinder, String[] strArr) {
            DumpComponentInfo dumpComponentInfo = new DumpComponentInfo();
            try {
                dumpComponentInfo.fd = ParcelFileDescriptor.dup(fileDescriptor);
                dumpComponentInfo.token = iBinder;
                dumpComponentInfo.args = strArr;
                ActivityThread.this.sendMessage(141, dumpComponentInfo, 0, 0, true);
            } catch (IOException e) {
                Slog.w(ActivityThread.TAG, "dumpProvider failed", e);
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpMemInfo(FileDescriptor fileDescriptor, Debug.MemoryInfo memoryInfo, boolean z, boolean z2, boolean z3, String[] strArr) {
            FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(fileDescriptor));
            try {
                dumpMemInfo(fastPrintWriter, memoryInfo, z, z2, z3);
            } finally {
                fastPrintWriter.flush();
            }
        }

        private void dumpMemInfo(PrintWriter printWriter, Debug.MemoryInfo memoryInfo, boolean z, boolean z2, boolean z3) {
            long nativeHeapSize = Debug.getNativeHeapSize() / 1024;
            long nativeHeapAllocatedSize = Debug.getNativeHeapAllocatedSize() / 1024;
            long nativeHeapFreeSize = Debug.getNativeHeapFreeSize() / 1024;
            Runtime runtime = Runtime.getRuntime();
            long j = runtime.totalMemory() / 1024;
            long jFreeMemory = runtime.freeMemory() / 1024;
            long j2 = j - jFreeMemory;
            long viewInstanceCount = ViewDebug.getViewInstanceCount();
            long viewRootImplCount = ViewDebug.getViewRootImplCount();
            long jCountInstancesOfClass = Debug.countInstancesOfClass(ContextImpl.class);
            long jCountInstancesOfClass2 = Debug.countInstancesOfClass(Activity.class);
            int globalAssetCount = AssetManager.getGlobalAssetCount();
            int globalAssetManagerCount = AssetManager.getGlobalAssetManagerCount();
            int binderLocalObjectCount = Debug.getBinderLocalObjectCount();
            int binderProxyObjectCount = Debug.getBinderProxyObjectCount();
            int binderDeathObjectCount = Debug.getBinderDeathObjectCount();
            long jCountInstancesOfClass3 = Debug.countInstancesOfClass(OpenSSLSocketImpl.class);
            SQLiteDebug.PagerStats databaseInfo = SQLiteDebug.getDatabaseInfo();
            ActivityThread.dumpMemInfoTable(printWriter, memoryInfo, z, z2, z3, Process.myPid(), ActivityThread.this.mBoundApplication != null ? ActivityThread.this.mBoundApplication.processName : "unknown", nativeHeapSize, nativeHeapAllocatedSize, nativeHeapFreeSize, j, j2, jFreeMemory);
            if (z) {
                printWriter.print(viewInstanceCount);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(viewRootImplCount);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(jCountInstancesOfClass);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(jCountInstancesOfClass2);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(globalAssetCount);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(globalAssetManagerCount);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(binderLocalObjectCount);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(binderProxyObjectCount);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(binderDeathObjectCount);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(jCountInstancesOfClass3);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(databaseInfo.memoryUsed / 1024);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(databaseInfo.memoryUsed / 1024);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(databaseInfo.pageCacheOverflow / 1024);
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(databaseInfo.largestMemAlloc / 1024);
                for (int i = 0; i < databaseInfo.dbStats.size(); i++) {
                    SQLiteDebug.DbStats dbStats = databaseInfo.dbStats.get(i);
                    printWriter.print(PhoneNumberUtils.PAUSE);
                    printWriter.print(dbStats.dbName);
                    printWriter.print(PhoneNumberUtils.PAUSE);
                    printWriter.print(dbStats.pageSize);
                    printWriter.print(PhoneNumberUtils.PAUSE);
                    printWriter.print(dbStats.dbSize);
                    printWriter.print(PhoneNumberUtils.PAUSE);
                    printWriter.print(dbStats.lookaside);
                    printWriter.print(PhoneNumberUtils.PAUSE);
                    printWriter.print(dbStats.cache);
                    printWriter.print(PhoneNumberUtils.PAUSE);
                    printWriter.print(dbStats.cache);
                }
                printWriter.println();
                return;
            }
            printWriter.println(" ");
            printWriter.println(" Objects");
            ActivityThread.printRow(printWriter, TWO_COUNT_COLUMNS, "Views:", Long.valueOf(viewInstanceCount), "ViewRootImpl:", Long.valueOf(viewRootImplCount));
            ActivityThread.printRow(printWriter, TWO_COUNT_COLUMNS, "AppContexts:", Long.valueOf(jCountInstancesOfClass), "Activities:", Long.valueOf(jCountInstancesOfClass2));
            ActivityThread.printRow(printWriter, TWO_COUNT_COLUMNS, "Assets:", Integer.valueOf(globalAssetCount), "AssetManagers:", Integer.valueOf(globalAssetManagerCount));
            ActivityThread.printRow(printWriter, TWO_COUNT_COLUMNS, "Local Binders:", Integer.valueOf(binderLocalObjectCount), "Proxy Binders:", Integer.valueOf(binderProxyObjectCount));
            ActivityThread.printRow(printWriter, ONE_COUNT_COLUMN, "Death Recipients:", Integer.valueOf(binderDeathObjectCount));
            ActivityThread.printRow(printWriter, ONE_COUNT_COLUMN, "OpenSSL Sockets:", Long.valueOf(jCountInstancesOfClass3));
            printWriter.println(" ");
            printWriter.println(" SQL");
            ActivityThread.printRow(printWriter, ONE_COUNT_COLUMN, "MEMORY_USED:", Integer.valueOf(databaseInfo.memoryUsed / 1024));
            ActivityThread.printRow(printWriter, TWO_COUNT_COLUMNS, "PAGECACHE_OVERFLOW:", Integer.valueOf(databaseInfo.pageCacheOverflow / 1024), "MALLOC_SIZE:", Integer.valueOf(databaseInfo.largestMemAlloc / 1024));
            printWriter.println(" ");
            int size = databaseInfo.dbStats.size();
            if (size > 0) {
                printWriter.println(" DATABASES");
                int i2 = 5;
                ActivityThread.printRow(printWriter, DB_INFO_FORMAT, "pgsz", "dbsz", "Lookaside(b)", "cache", "Dbname");
                int i3 = 0;
                while (i3 < size) {
                    SQLiteDebug.DbStats dbStats2 = databaseInfo.dbStats.get(i3);
                    Object[] objArr = new Object[i2];
                    objArr[0] = dbStats2.pageSize > 0 ? String.valueOf(dbStats2.pageSize) : " ";
                    int i4 = i3;
                    objArr[1] = dbStats2.dbSize > 0 ? String.valueOf(dbStats2.dbSize) : " ";
                    objArr[2] = dbStats2.lookaside > 0 ? String.valueOf(dbStats2.lookaside) : " ";
                    objArr[3] = dbStats2.cache;
                    objArr[4] = dbStats2.dbName;
                    ActivityThread.printRow(printWriter, DB_INFO_FORMAT, objArr);
                    i3 = i4 + 1;
                    i2 = 5;
                }
            }
            String assetAllocations = AssetManager.getAssetAllocations();
            if (assetAllocations != null) {
                printWriter.println(" ");
                printWriter.println(" Asset Allocations");
                printWriter.print(assetAllocations);
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpGfxInfo(FileDescriptor fileDescriptor, String[] strArr) {
            ActivityThread.this.dumpGraphicsInfo(fileDescriptor);
            WindowManagerGlobal.getInstance().dumpGfxInfo(fileDescriptor);
        }

        @Override // android.app.IApplicationThread
        public void dumpDbInfo(FileDescriptor fileDescriptor, String[] strArr) {
            FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(fileDescriptor));
            SQLiteDebug.dump(new PrintWriterPrinter(fastPrintWriter), strArr);
            fastPrintWriter.flush();
        }

        @Override // android.app.IApplicationThread
        public void unstableProviderDied(IBinder iBinder) {
            ActivityThread.this.sendMessage(142, iBinder);
        }

        @Override // android.app.IApplicationThread
        public void requestAssistContextExtras(IBinder iBinder, IBinder iBinder2, int i) {
            RequestAssistContextExtras requestAssistContextExtras = new RequestAssistContextExtras();
            requestAssistContextExtras.activityToken = iBinder;
            requestAssistContextExtras.requestToken = iBinder2;
            requestAssistContextExtras.requestType = i;
            ActivityThread.this.sendMessage(143, requestAssistContextExtras);
        }

        @Override // android.app.IApplicationThread
        public void setCoreSettings(Bundle bundle) {
            ActivityThread.this.sendMessage(138, bundle);
        }

        @Override // android.app.IApplicationThread
        public void updatePackageCompatibilityInfo(String str, CompatibilityInfo compatibilityInfo) {
            UpdateCompatibilityData updateCompatibilityData = new UpdateCompatibilityData();
            updateCompatibilityData.pkg = str;
            updateCompatibilityData.info = compatibilityInfo;
            ActivityThread.this.sendMessage(139, updateCompatibilityData);
        }

        @Override // android.app.IApplicationThread
        public void scheduleTrimMemory(int i) {
            ActivityThread.this.sendMessage(140, null, i);
        }

        @Override // android.app.IApplicationThread
        public void scheduleTranslucentConversionComplete(IBinder iBinder, boolean z) {
            ActivityThread.this.sendMessage(144, iBinder, z ? 1 : 0);
        }

        @Override // android.app.IApplicationThread
        public void setProcessState(int i) {
            updateProcessState(i, true);
        }

        public void updateProcessState(int i, boolean z) {
            synchronized (this) {
                if (this.mLastProcessState != i) {
                    this.mLastProcessState = i;
                }
            }
        }

        @Override // android.app.IApplicationThread
        public void scheduleInstallProvider(ProviderInfo providerInfo) {
            ActivityThread.this.sendMessage(145, providerInfo);
        }
    }

    private class H extends Handler {
        public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
        public static final int BIND_APPLICATION = 110;
        public static final int BIND_SERVICE = 121;
        public static final int CLEAN_UP_CONTEXT = 119;
        public static final int CONFIGURATION_CHANGED = 118;
        public static final int CREATE_BACKUP_AGENT = 128;
        public static final int CREATE_SERVICE = 114;
        public static final int DESTROY_ACTIVITY = 109;
        public static final int DESTROY_BACKUP_AGENT = 129;
        public static final int DISPATCH_PACKAGE_BROADCAST = 133;
        public static final int DUMP_ACTIVITY = 136;
        public static final int DUMP_HEAP = 135;
        public static final int DUMP_PROVIDER = 141;
        public static final int DUMP_SERVICE = 123;
        public static final int ENABLE_JIT = 132;
        public static final int EXIT_APPLICATION = 111;
        public static final int GC_WHEN_IDLE = 120;
        public static final int HIDE_WINDOW = 106;
        public static final int INSTALL_PROVIDER = 145;
        public static final int LAUNCH_ACTIVITY = 100;
        public static final int LOW_MEMORY = 124;
        public static final int NEW_INTENT = 112;
        public static final int PAUSE_ACTIVITY = 101;
        public static final int PAUSE_ACTIVITY_FINISHING = 102;
        public static final int PROFILER_CONTROL = 127;
        public static final int RECEIVER = 113;
        public static final int RELAUNCH_ACTIVITY = 126;
        public static final int REMOVE_PROVIDER = 131;
        public static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
        public static final int REQUEST_THUMBNAIL = 117;
        public static final int RESUME_ACTIVITY = 107;
        public static final int SCHEDULE_CRASH = 134;
        public static final int SEND_RESULT = 108;
        public static final int SERVICE_ARGS = 115;
        public static final int SET_CORE_SETTINGS = 138;
        public static final int SHOW_WINDOW = 105;
        public static final int SLEEPING = 137;
        public static final int STOP_ACTIVITY_HIDE = 104;
        public static final int STOP_ACTIVITY_SHOW = 103;
        public static final int STOP_SERVICE = 116;
        public static final int SUICIDE = 130;
        public static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;
        public static final int TRIM_MEMORY = 140;
        public static final int UNBIND_SERVICE = 122;
        public static final int UNSTABLE_PROVIDER_DIED = 142;
        public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;

        private H() {
        }

        String codeToString(int i) {
            return Integer.toString(i);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) throws PackageManager.NameNotFoundException {
            switch (message.what) {
                case 100:
                    Trace.traceBegin(64L, "activityStart");
                    ActivityClientRecord activityClientRecord = (ActivityClientRecord) message.obj;
                    activityClientRecord.packageInfo = ActivityThread.this.getPackageInfoNoCheck(activityClientRecord.activityInfo.applicationInfo, activityClientRecord.compatInfo);
                    ActivityThread.this.handleLaunchActivity(activityClientRecord, null);
                    Trace.traceEnd(64L);
                    return;
                case 101:
                    Trace.traceBegin(64L, "activityPause");
                    ActivityThread.this.handlePauseActivity((IBinder) message.obj, false, message.arg1 != 0, message.arg2);
                    maybeSnapshot();
                    Trace.traceEnd(64L);
                    return;
                case 102:
                    Trace.traceBegin(64L, "activityPause");
                    ActivityThread.this.handlePauseActivity((IBinder) message.obj, true, message.arg1 != 0, message.arg2);
                    Trace.traceEnd(64L);
                    return;
                case 103:
                    Trace.traceBegin(64L, "activityStop");
                    ActivityThread.this.handleStopActivity((IBinder) message.obj, true, message.arg2);
                    Trace.traceEnd(64L);
                    return;
                case 104:
                    Trace.traceBegin(64L, "activityStop");
                    ActivityThread.this.handleStopActivity((IBinder) message.obj, false, message.arg2);
                    Trace.traceEnd(64L);
                    return;
                case 105:
                    Trace.traceBegin(64L, "activityShowWindow");
                    ActivityThread.this.handleWindowVisibility((IBinder) message.obj, true);
                    Trace.traceEnd(64L);
                    return;
                case 106:
                    Trace.traceBegin(64L, "activityHideWindow");
                    ActivityThread.this.handleWindowVisibility((IBinder) message.obj, false);
                    Trace.traceEnd(64L);
                    return;
                case 107:
                    Trace.traceBegin(64L, "activityResume");
                    ActivityThread.this.handleResumeActivity((IBinder) message.obj, true, message.arg1 != 0, true);
                    Trace.traceEnd(64L);
                    return;
                case 108:
                    Trace.traceBegin(64L, "activityDeliverResult");
                    ActivityThread.this.handleSendResult((ResultData) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 109:
                    Trace.traceBegin(64L, "activityDestroy");
                    ActivityThread.this.handleDestroyActivity((IBinder) message.obj, message.arg1 != 0, message.arg2, false);
                    Trace.traceEnd(64L);
                    return;
                case 110:
                    Trace.traceBegin(64L, "bindApplication");
                    ActivityThread.this.handleBindApplication((AppBindData) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 111:
                    if (ActivityThread.this.mInitialApplication != null) {
                        ActivityThread.this.mInitialApplication.onTerminate();
                    }
                    Looper.myLooper().quit();
                    return;
                case 112:
                    Trace.traceBegin(64L, "activityNewIntent");
                    ActivityThread.this.handleNewIntent((NewIntentData) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 113:
                    Trace.traceBegin(64L, "broadcastReceiveComp");
                    ActivityThread.this.handleReceiver((ReceiverData) message.obj);
                    maybeSnapshot();
                    Trace.traceEnd(64L);
                    return;
                case 114:
                    Trace.traceBegin(64L, "serviceCreate");
                    ActivityThread.this.handleCreateService((CreateServiceData) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 115:
                    Trace.traceBegin(64L, "serviceStart");
                    ActivityThread.this.handleServiceArgs((ServiceArgsData) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 116:
                    Trace.traceBegin(64L, "serviceStop");
                    ActivityThread.this.handleStopService((IBinder) message.obj);
                    maybeSnapshot();
                    Trace.traceEnd(64L);
                    return;
                case 117:
                    Trace.traceBegin(64L, "requestThumbnail");
                    ActivityThread.this.handleRequestThumbnail((IBinder) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 118:
                    Trace.traceBegin(64L, "configChanged");
                    ActivityThread.this.mCurDefaultDisplayDpi = ((Configuration) message.obj).densityDpi;
                    ActivityThread.this.handleConfigurationChanged((Configuration) message.obj, null);
                    Trace.traceEnd(64L);
                    return;
                case 119:
                    ContextCleanupInfo contextCleanupInfo = (ContextCleanupInfo) message.obj;
                    contextCleanupInfo.context.performFinalCleanup(contextCleanupInfo.who, contextCleanupInfo.what);
                    return;
                case 120:
                    ActivityThread.this.scheduleGcIdler();
                    return;
                case 121:
                    Trace.traceBegin(64L, "serviceBind");
                    ActivityThread.this.handleBindService((BindServiceData) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 122:
                    Trace.traceBegin(64L, "serviceUnbind");
                    ActivityThread.this.handleUnbindService((BindServiceData) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 123:
                    ActivityThread.this.handleDumpService((DumpComponentInfo) message.obj);
                    return;
                case 124:
                    Trace.traceBegin(64L, "lowMemory");
                    ActivityThread.this.handleLowMemory();
                    Trace.traceEnd(64L);
                    return;
                case 125:
                    Trace.traceBegin(64L, "activityConfigChanged");
                    ActivityThread.this.handleActivityConfigurationChanged((IBinder) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 126:
                    Trace.traceBegin(64L, "activityRestart");
                    ActivityThread.this.handleRelaunchActivity((ActivityClientRecord) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 127:
                    ActivityThread.this.handleProfilerControl(message.arg1 != 0, (ProfilerControlData) message.obj, message.arg2);
                    return;
                case 128:
                    Trace.traceBegin(64L, "backupCreateAgent");
                    ActivityThread.this.handleCreateBackupAgent((CreateBackupAgentData) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 129:
                    Trace.traceBegin(64L, "backupDestroyAgent");
                    ActivityThread.this.handleDestroyBackupAgent((CreateBackupAgentData) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 130:
                    Process.killProcess(Process.myPid());
                    return;
                case 131:
                    Trace.traceBegin(64L, "providerRemove");
                    ActivityThread.this.completeRemoveProvider((ProviderRefCount) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 132:
                    ActivityThread.this.ensureJitEnabled();
                    return;
                case 133:
                    Trace.traceBegin(64L, "broadcastPackage");
                    ActivityThread.this.handleDispatchPackageBroadcast(message.arg1, (String[]) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 134:
                    throw new RemoteServiceException((String) message.obj);
                case 135:
                    ActivityThread.handleDumpHeap(message.arg1 != 0, (DumpHeapData) message.obj);
                    return;
                case 136:
                    ActivityThread.this.handleDumpActivity((DumpComponentInfo) message.obj);
                    return;
                case 137:
                    Trace.traceBegin(64L, "sleeping");
                    ActivityThread.this.handleSleeping((IBinder) message.obj, message.arg1 != 0);
                    Trace.traceEnd(64L);
                    return;
                case 138:
                    Trace.traceBegin(64L, "setCoreSettings");
                    ActivityThread.this.handleSetCoreSettings((Bundle) message.obj);
                    Trace.traceEnd(64L);
                    return;
                case 139:
                    ActivityThread.this.handleUpdatePackageCompatibilityInfo((UpdateCompatibilityData) message.obj);
                    return;
                case 140:
                    Trace.traceBegin(64L, "trimMemory");
                    ActivityThread.this.handleTrimMemory(message.arg1);
                    Trace.traceEnd(64L);
                    return;
                case 141:
                    ActivityThread.this.handleDumpProvider((DumpComponentInfo) message.obj);
                    return;
                case 142:
                    ActivityThread.this.handleUnstableProviderDied((IBinder) message.obj, false);
                    return;
                case 143:
                    ActivityThread.this.handleRequestAssistContextExtras((RequestAssistContextExtras) message.obj);
                    return;
                case 144:
                    ActivityThread.this.handleTranslucentConversionComplete((IBinder) message.obj, message.arg1 == 1);
                    return;
                case 145:
                    ActivityThread.this.handleInstallProvider((ProviderInfo) message.obj);
                    return;
                default:
                    return;
            }
        }

        private void maybeSnapshot() {
            ContextImpl systemContext;
            if (ActivityThread.this.mBoundApplication == null || !SamplingProfilerIntegration.isEnabled()) {
                return;
            }
            String str = ActivityThread.this.mBoundApplication.info.mPackageName;
            PackageInfo packageInfo = null;
            try {
                systemContext = ActivityThread.this.getSystemContext();
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(ActivityThread.TAG, "cannot get package info for " + str, e);
            }
            if (systemContext == null) {
                Log.e(ActivityThread.TAG, "cannot get a valid context");
                return;
            }
            PackageManager packageManager = systemContext.getPackageManager();
            if (packageManager == null) {
                Log.e(ActivityThread.TAG, "cannot get a valid PackageManager");
            } else {
                packageInfo = packageManager.getPackageInfo(str, 1);
                SamplingProfilerIntegration.writeSnapshot(ActivityThread.this.mBoundApplication.processName, packageInfo);
            }
        }
    }

    private class Idler implements MessageQueue.IdleHandler {
        private Idler() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public final boolean queueIdle() {
            ActivityClientRecord activityClientRecord = ActivityThread.this.mNewActivities;
            boolean z = (ActivityThread.this.mBoundApplication == null || ActivityThread.this.mProfiler.profileFd == null || !ActivityThread.this.mProfiler.autoStopProfiler) ? false : true;
            if (activityClientRecord != null) {
                ActivityThread.this.mNewActivities = null;
                IActivityManager iActivityManager = ActivityManagerNative.getDefault();
                while (true) {
                    if (activityClientRecord.activity != null && !activityClientRecord.activity.mFinished) {
                        try {
                            iActivityManager.activityIdle(activityClientRecord.token, activityClientRecord.createdConfig, z);
                            activityClientRecord.createdConfig = null;
                        } catch (RemoteException unused) {
                        }
                    }
                    ActivityClientRecord activityClientRecord2 = activityClientRecord.nextIdle;
                    activityClientRecord.nextIdle = null;
                    if (activityClientRecord2 == null) {
                        break;
                    }
                    activityClientRecord = activityClientRecord2;
                }
            }
            if (z) {
                ActivityThread.this.mProfiler.stopProfiling();
            }
            ActivityThread.this.ensureJitEnabled();
            return false;
        }
    }

    final class GcIdler implements MessageQueue.IdleHandler {
        GcIdler() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public final boolean queueIdle() {
            ActivityThread.this.doGcIfNeeded();
            return false;
        }
    }

    public static ActivityThread currentActivityThread() {
        return sCurrentActivityThread;
    }

    public static String currentPackageName() {
        AppBindData appBindData;
        ActivityThread activityThreadCurrentActivityThread = currentActivityThread();
        if (activityThreadCurrentActivityThread == null || (appBindData = activityThreadCurrentActivityThread.mBoundApplication) == null) {
            return null;
        }
        return appBindData.appInfo.packageName;
    }

    public static String currentProcessName() {
        AppBindData appBindData;
        ActivityThread activityThreadCurrentActivityThread = currentActivityThread();
        if (activityThreadCurrentActivityThread == null || (appBindData = activityThreadCurrentActivityThread.mBoundApplication) == null) {
            return null;
        }
        return appBindData.processName;
    }

    public static Application currentApplication() {
        ActivityThread activityThreadCurrentActivityThread = currentActivityThread();
        if (activityThreadCurrentActivityThread != null) {
            return activityThreadCurrentActivityThread.mInitialApplication;
        }
        return null;
    }

    public static IPackageManager getPackageManager() {
        IPackageManager iPackageManager = sPackageManager;
        if (iPackageManager != null) {
            return iPackageManager;
        }
        IPackageManager iPackageManagerAsInterface = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        sPackageManager = iPackageManagerAsInterface;
        return iPackageManagerAsInterface;
    }

    Configuration applyConfigCompatMainThread(int i, Configuration configuration, CompatibilityInfo compatibilityInfo) {
        if (configuration == null) {
            return null;
        }
        if (compatibilityInfo.supportsScreen()) {
            return configuration;
        }
        this.mMainThreadConfig.setTo(configuration);
        Configuration configuration2 = this.mMainThreadConfig;
        compatibilityInfo.applyToConfiguration(i, configuration2);
        return configuration2;
    }

    Resources getTopLevelResources(String str, int i, Configuration configuration, LoadedApk loadedApk) {
        return this.mResourcesManager.getTopLevelResources(str, i, configuration, loadedApk.getCompatibilityInfo(), null);
    }

    final Handler getHandler() {
        return this.mH;
    }

    public final LoadedApk getPackageInfo(String str, CompatibilityInfo compatibilityInfo, int i) {
        return getPackageInfo(str, compatibilityInfo, i, UserHandle.myUserId());
    }

    public final LoadedApk getPackageInfo(String str, CompatibilityInfo compatibilityInfo, int i, int i2) {
        WeakReference<LoadedApk> weakReference;
        ApplicationInfo applicationInfo;
        synchronized (this.mResourcesManager) {
            if ((i & 1) != 0) {
                weakReference = this.mPackages.get(str);
            } else {
                weakReference = this.mResourcePackages.get(str);
            }
            LoadedApk loadedApk = weakReference != null ? weakReference.get() : null;
            if (loadedApk != null && (loadedApk.mResources == null || loadedApk.mResources.getAssets().isUpToDate())) {
                if (loadedApk.isSecurityViolation() && (i & 2) == 0) {
                    throw new SecurityException("Requesting code from " + str + " to be run in process " + this.mBoundApplication.processName + "/" + this.mBoundApplication.appInfo.uid);
                }
                return loadedApk;
            }
            try {
                applicationInfo = getPackageManager().getApplicationInfo(str, 1024, i2);
            } catch (RemoteException unused) {
                applicationInfo = null;
            }
            if (applicationInfo != null) {
                return getPackageInfo(applicationInfo, compatibilityInfo, i);
            }
            return null;
        }
    }

    public final LoadedApk getPackageInfo(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo, int i) {
        boolean z = (i & 1) != 0;
        boolean z2 = z && applicationInfo.uid != 0 && applicationInfo.uid != 1000 && (this.mBoundApplication == null || !UserHandle.isSameApp(applicationInfo.uid, this.mBoundApplication.appInfo.uid));
        if ((i & 3) == 1 && z2) {
            String str = "Requesting code from " + applicationInfo.packageName + " (with uid " + applicationInfo.uid + ")";
            if (this.mBoundApplication != null) {
                str = str + " to be run in process " + this.mBoundApplication.processName + " (with uid " + this.mBoundApplication.appInfo.uid + ")";
            }
            throw new SecurityException(str);
        }
        return getPackageInfo(applicationInfo, compatibilityInfo, null, z2, z);
    }

    public final LoadedApk getPackageInfoNoCheck(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo) {
        return getPackageInfo(applicationInfo, compatibilityInfo, null, false, true);
    }

    public final LoadedApk peekPackageInfo(String str, boolean z) {
        WeakReference<LoadedApk> weakReference;
        LoadedApk loadedApk;
        synchronized (this.mResourcesManager) {
            if (z) {
                weakReference = this.mPackages.get(str);
            } else {
                weakReference = this.mResourcePackages.get(str);
            }
            loadedApk = weakReference != null ? weakReference.get() : null;
        }
        return loadedApk;
    }

    private LoadedApk getPackageInfo(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo, ClassLoader classLoader, boolean z, boolean z2) {
        WeakReference<LoadedApk> weakReference;
        LoadedApk loadedApk;
        synchronized (this.mResourcesManager) {
            if (z2) {
                weakReference = this.mPackages.get(applicationInfo.packageName);
            } else {
                weakReference = this.mResourcePackages.get(applicationInfo.packageName);
            }
            loadedApk = weakReference != null ? weakReference.get() : null;
            if (loadedApk == null || (loadedApk.mResources != null && !loadedApk.mResources.getAssets().isUpToDate())) {
                LoadedApk loadedApk2 = new LoadedApk(this, applicationInfo, compatibilityInfo, this, classLoader, z, z2 && (applicationInfo.flags & 4) != 0);
                if (z2) {
                    this.mPackages.put(applicationInfo.packageName, new WeakReference<>(loadedApk2));
                } else {
                    this.mResourcePackages.put(applicationInfo.packageName, new WeakReference<>(loadedApk2));
                }
                loadedApk = loadedApk2;
            }
        }
        return loadedApk;
    }

    ActivityThread() {
        this.mAppThread = new ApplicationThread();
        this.mH = new H();
    }

    public ApplicationThread getApplicationThread() {
        return this.mAppThread;
    }

    public Instrumentation getInstrumentation() {
        return this.mInstrumentation;
    }

    public boolean isProfiling() {
        Profiler profiler = this.mProfiler;
        return (profiler == null || profiler.profileFile == null || this.mProfiler.profileFd != null) ? false : true;
    }

    public String getProfileFilePath() {
        return this.mProfiler.profileFile;
    }

    public Looper getLooper() {
        return this.mLooper;
    }

    public Application getApplication() {
        return this.mInitialApplication;
    }

    public String getProcessName() {
        return this.mBoundApplication.processName;
    }

    public ContextImpl getSystemContext() {
        synchronized (this) {
            if (mSystemContext == null) {
                ContextImpl contextImplCreateSystemContext = ContextImpl.createSystemContext(this);
                contextImplCreateSystemContext.init(new LoadedApk(this, "android", contextImplCreateSystemContext, null, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO), (IBinder) null, this);
                contextImplCreateSystemContext.getResources().updateConfiguration(this.mResourcesManager.getConfiguration(), this.mResourcesManager.getDisplayMetricsLocked(0));
                mSystemContext = contextImplCreateSystemContext;
            }
        }
        return mSystemContext;
    }

    public void installSystemApplicationInfo(ApplicationInfo applicationInfo) {
        synchronized (this) {
            ContextImpl systemContext = getSystemContext();
            systemContext.init(new LoadedApk(this, "android", systemContext, applicationInfo, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO), (IBinder) null, this);
            this.mProfiler = new Profiler();
        }
    }

    void ensureJitEnabled() {
        if (this.mJitEnabled) {
            return;
        }
        this.mJitEnabled = true;
        VMRuntime.getRuntime().startJitCompilation();
    }

    void scheduleGcIdler() {
        if (!this.mGcIdlerScheduled) {
            this.mGcIdlerScheduled = true;
            Looper.myQueue().addIdleHandler(this.mGcIdler);
        }
        this.mH.removeMessages(120);
    }

    void unscheduleGcIdler() {
        if (this.mGcIdlerScheduled) {
            this.mGcIdlerScheduled = false;
            Looper.myQueue().removeIdleHandler(this.mGcIdler);
        }
        this.mH.removeMessages(120);
    }

    void doGcIfNeeded() {
        this.mGcIdlerScheduled = false;
        if (BinderInternal.getLastGcTime() + 5000 < SystemClock.uptimeMillis()) {
            BinderInternal.forceGc("bg");
        }
    }

    static void printRow(PrintWriter printWriter, String str, Object... objArr) {
        printWriter.println(String.format(str, objArr));
    }

    public static void dumpMemInfoTable(PrintWriter printWriter, Debug.MemoryInfo memoryInfo, boolean z, boolean z2, boolean z3, int i, String str, long j, long j2, long j3, long j4, long j5, long j6) {
        String str2;
        Debug.MemoryInfo memoryInfo2 = memoryInfo;
        if (z) {
            printWriter.print(3);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(i);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(str);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(j);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(j4);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print("N/A,");
            printWriter.print(j + j4);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(j2);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(j5);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print("N/A,");
            printWriter.print(j2 + j5);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(j3);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(j6);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print("N/A,");
            printWriter.print(j3 + j6);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.nativePss);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.dalvikPss);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.otherPss);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo.getTotalPss());
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.nativeSwappablePss);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.dalvikSwappablePss);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.otherSwappablePss);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo.getTotalSwappablePss());
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.nativeSharedDirty);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.dalvikSharedDirty);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.otherSharedDirty);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo.getTotalSharedDirty());
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.nativeSharedClean);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.dalvikSharedClean);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.otherSharedClean);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo.getTotalSharedClean());
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.nativePrivateDirty);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.dalvikPrivateDirty);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.otherPrivateDirty);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo.getTotalPrivateDirty());
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.nativePrivateClean);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.dalvikPrivateClean);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo2.otherPrivateClean);
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(memoryInfo.getTotalPrivateClean());
            printWriter.print(PhoneNumberUtils.PAUSE);
            for (int i2 = 0; i2 < 16; i2++) {
                printWriter.print(Debug.MemoryInfo.getOtherLabel(i2));
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(memoryInfo2.getOtherPss(i2));
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(memoryInfo2.getOtherSwappablePss(i2));
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(memoryInfo2.getOtherSharedDirty(i2));
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(memoryInfo2.getOtherSharedClean(i2));
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(memoryInfo2.getOtherPrivateDirty(i2));
                printWriter.print(PhoneNumberUtils.PAUSE);
                printWriter.print(memoryInfo2.getOtherPrivateClean(i2));
                printWriter.print(PhoneNumberUtils.PAUSE);
            }
            return;
        }
        if (z2) {
            printRow(printWriter, HEAP_FULL_COLUMN, "", "Pss", "Pss", "Shared", "Private", "Shared", "Private", "Swapped", "Heap", "Heap", "Heap");
            printRow(printWriter, HEAP_FULL_COLUMN, "", "Total", "Clean", "Dirty", "Dirty", "Clean", "Clean", "Dirty", "Size", "Alloc", "Free");
            printRow(printWriter, HEAP_FULL_COLUMN, "", "------", "------", "------", "------", "------", "------", "------", "------", "------", "------");
            printRow(printWriter, HEAP_FULL_COLUMN, "Native Heap", Integer.valueOf(memoryInfo2.nativePss), Integer.valueOf(memoryInfo2.nativeSwappablePss), Integer.valueOf(memoryInfo2.nativeSharedDirty), Integer.valueOf(memoryInfo2.nativePrivateDirty), Integer.valueOf(memoryInfo2.nativeSharedClean), Integer.valueOf(memoryInfo2.nativePrivateClean), Integer.valueOf(memoryInfo2.nativeSwappedOut), Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3));
            printRow(printWriter, HEAP_FULL_COLUMN, "Dalvik Heap", Integer.valueOf(memoryInfo2.dalvikPss), Integer.valueOf(memoryInfo2.dalvikSwappablePss), Integer.valueOf(memoryInfo2.dalvikSharedDirty), Integer.valueOf(memoryInfo2.dalvikPrivateDirty), Integer.valueOf(memoryInfo2.dalvikSharedClean), Integer.valueOf(memoryInfo2.dalvikPrivateClean), Integer.valueOf(memoryInfo2.dalvikSwappedOut), Long.valueOf(j4), Long.valueOf(j5), Long.valueOf(j6));
        } else {
            printRow(printWriter, HEAP_COLUMN, "", "Pss", "Private", "Private", "Swapped", "Heap", "Heap", "Heap");
            printRow(printWriter, HEAP_COLUMN, "", "Total", "Dirty", "Clean", "Dirty", "Size", "Alloc", "Free");
            printRow(printWriter, HEAP_COLUMN, "", "------", "------", "------", "------", "------", "------", "------", "------");
            printRow(printWriter, HEAP_COLUMN, "Native Heap", Integer.valueOf(memoryInfo2.nativePss), Integer.valueOf(memoryInfo2.nativePrivateDirty), Integer.valueOf(memoryInfo2.nativePrivateClean), Integer.valueOf(memoryInfo2.nativeSwappedOut), Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3));
            printRow(printWriter, HEAP_COLUMN, "Dalvik Heap", Integer.valueOf(memoryInfo2.dalvikPss), Integer.valueOf(memoryInfo2.dalvikPrivateDirty), Integer.valueOf(memoryInfo2.dalvikPrivateClean), Integer.valueOf(memoryInfo2.dalvikSwappedOut), Long.valueOf(j4), Long.valueOf(j5), Long.valueOf(j6));
        }
        int i3 = memoryInfo2.otherPss;
        int i4 = memoryInfo2.otherSwappablePss;
        int i5 = memoryInfo2.otherSharedDirty;
        int i6 = memoryInfo2.otherPrivateDirty;
        int i7 = memoryInfo2.otherSharedClean;
        int i8 = memoryInfo2.otherPrivateClean;
        int i9 = 0;
        int i10 = memoryInfo2.otherSwappedOut;
        int i11 = i3;
        int i12 = i6;
        int i13 = i5;
        int i14 = i4;
        while (i9 < 16) {
            int otherPss = memoryInfo2.getOtherPss(i9);
            int otherSwappablePss = memoryInfo2.getOtherSwappablePss(i9);
            int otherSharedDirty = memoryInfo2.getOtherSharedDirty(i9);
            int otherPrivateDirty = memoryInfo2.getOtherPrivateDirty(i9);
            int otherSharedClean = memoryInfo2.getOtherSharedClean(i9);
            int otherPrivateClean = memoryInfo2.getOtherPrivateClean(i9);
            int otherSwappedOut = memoryInfo2.getOtherSwappedOut(i9);
            if (otherPss != 0 || otherSharedDirty != 0 || otherPrivateDirty != 0 || otherSharedClean != 0 || otherPrivateClean != 0 || otherSwappedOut != 0) {
                if (z2) {
                    printRow(printWriter, HEAP_FULL_COLUMN, Debug.MemoryInfo.getOtherLabel(i9), Integer.valueOf(otherPss), Integer.valueOf(otherSwappablePss), Integer.valueOf(otherSharedDirty), Integer.valueOf(otherPrivateDirty), Integer.valueOf(otherSharedClean), Integer.valueOf(otherPrivateClean), Integer.valueOf(otherSwappedOut), "", "", "");
                } else {
                    printRow(printWriter, HEAP_COLUMN, Debug.MemoryInfo.getOtherLabel(i9), Integer.valueOf(otherPss), Integer.valueOf(otherPrivateDirty), Integer.valueOf(otherPrivateClean), Integer.valueOf(otherSwappedOut), "", "", "");
                }
                i11 -= otherPss;
                i14 -= otherSwappablePss;
                i13 -= otherSharedDirty;
                i12 -= otherPrivateDirty;
                i7 -= otherSharedClean;
                i8 -= otherPrivateClean;
                i10 -= otherSwappedOut;
            }
            i9++;
            memoryInfo2 = memoryInfo;
        }
        if (z2) {
            printRow(printWriter, HEAP_FULL_COLUMN, "Unknown", Integer.valueOf(i11), Integer.valueOf(i14), Integer.valueOf(i13), Integer.valueOf(i12), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i10), "", "", "");
            str2 = HEAP_COLUMN;
            printRow(printWriter, HEAP_FULL_COLUMN, "TOTAL", Integer.valueOf(memoryInfo.getTotalPss()), Integer.valueOf(memoryInfo.getTotalSwappablePss()), Integer.valueOf(memoryInfo.getTotalSharedDirty()), Integer.valueOf(memoryInfo.getTotalPrivateDirty()), Integer.valueOf(memoryInfo.getTotalSharedClean()), Integer.valueOf(memoryInfo.getTotalPrivateClean()), Integer.valueOf(memoryInfo.getTotalSwappedOut()), Long.valueOf(j + j4), Long.valueOf(j2 + j5), Long.valueOf(j3 + j6));
        } else {
            str2 = HEAP_COLUMN;
            printRow(printWriter, str2, "Unknown", Integer.valueOf(i11), Integer.valueOf(i12), Integer.valueOf(i8), Integer.valueOf(i10), "", "", "");
            printRow(printWriter, str2, "TOTAL", Integer.valueOf(memoryInfo.getTotalPss()), Integer.valueOf(memoryInfo.getTotalPrivateDirty()), Integer.valueOf(memoryInfo.getTotalPrivateClean()), Integer.valueOf(memoryInfo.getTotalSwappedOut()), Long.valueOf(j + j4), Long.valueOf(j2 + j5), Long.valueOf(j3 + j6));
        }
        if (z3) {
            printWriter.println(" ");
            printWriter.println(" Dalvik Details");
            for (int i15 = 16; i15 < 21; i15++) {
                int otherPss2 = memoryInfo.getOtherPss(i15);
                int otherSwappablePss2 = memoryInfo.getOtherSwappablePss(i15);
                int otherSharedDirty2 = memoryInfo.getOtherSharedDirty(i15);
                int otherPrivateDirty2 = memoryInfo.getOtherPrivateDirty(i15);
                int otherSharedClean2 = memoryInfo.getOtherSharedClean(i15);
                int otherPrivateClean2 = memoryInfo.getOtherPrivateClean(i15);
                int otherSwappedOut2 = memoryInfo.getOtherSwappedOut(i15);
                if (otherPss2 != 0 || otherSharedDirty2 != 0 || otherPrivateDirty2 != 0 || otherSharedClean2 != 0 || otherPrivateClean2 != 0) {
                    if (z2) {
                        printRow(printWriter, HEAP_FULL_COLUMN, Debug.MemoryInfo.getOtherLabel(i15), Integer.valueOf(otherPss2), Integer.valueOf(otherSwappablePss2), Integer.valueOf(otherSharedDirty2), Integer.valueOf(otherPrivateDirty2), Integer.valueOf(otherSharedClean2), Integer.valueOf(otherPrivateClean2), Integer.valueOf(otherSwappedOut2), "", "", "");
                    } else {
                        printRow(printWriter, str2, Debug.MemoryInfo.getOtherLabel(i15), Integer.valueOf(otherPss2), Integer.valueOf(otherPrivateDirty2), Integer.valueOf(otherPrivateClean2), Integer.valueOf(otherSwappedOut2), "", "", "");
                    }
                }
            }
        }
    }

    public void registerOnActivityPausedListener(Activity activity, OnActivityPausedListener onActivityPausedListener) {
        synchronized (this.mOnPauseListeners) {
            ArrayList<OnActivityPausedListener> arrayList = this.mOnPauseListeners.get(activity);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mOnPauseListeners.put(activity, arrayList);
            }
            arrayList.add(onActivityPausedListener);
        }
    }

    public void unregisterOnActivityPausedListener(Activity activity, OnActivityPausedListener onActivityPausedListener) {
        synchronized (this.mOnPauseListeners) {
            ArrayList<OnActivityPausedListener> arrayList = this.mOnPauseListeners.get(activity);
            if (arrayList != null) {
                arrayList.remove(onActivityPausedListener);
            }
        }
    }

    public final ActivityInfo resolveActivityInfo(Intent intent) {
        ActivityInfo activityInfoResolveActivityInfo = intent.resolveActivityInfo(this.mInitialApplication.getPackageManager(), 1024);
        if (activityInfoResolveActivityInfo == null) {
            Instrumentation.checkStartActivityResult(-2, intent);
        }
        return activityInfoResolveActivityInfo;
    }

    public final Activity startActivityNow(Activity activity, String str, Intent intent, ActivityInfo activityInfo, IBinder iBinder, Bundle bundle, Activity.NonConfigurationInstances nonConfigurationInstances) {
        ActivityClientRecord activityClientRecord = new ActivityClientRecord();
        activityClientRecord.token = iBinder;
        activityClientRecord.ident = 0;
        activityClientRecord.intent = intent;
        activityClientRecord.state = bundle;
        activityClientRecord.parent = activity;
        activityClientRecord.embeddedID = str;
        activityClientRecord.activityInfo = activityInfo;
        activityClientRecord.lastNonConfigurationInstances = nonConfigurationInstances;
        return performLaunchActivity(activityClientRecord, null);
    }

    public final Activity getActivity(IBinder iBinder) {
        return this.mActivities.get(iBinder).activity;
    }

    public final void sendActivityResult(IBinder iBinder, String str, int i, int i2, Intent intent) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ResultInfo(str, i, i2, intent));
        this.mAppThread.scheduleSendResult(iBinder, arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage(int i, Object obj) {
        sendMessage(i, obj, 0, 0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage(int i, Object obj, int i2) {
        sendMessage(i, obj, i2, 0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage(int i, Object obj, int i2, int i3) {
        sendMessage(i, obj, i2, i3, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage(int i, Object obj, int i2, int i3, boolean z) {
        Message messageObtain = Message.obtain();
        messageObtain.what = i;
        messageObtain.obj = obj;
        messageObtain.arg1 = i2;
        messageObtain.arg2 = i3;
        if (z) {
            messageObtain.setAsynchronous(true);
        }
        this.mH.sendMessage(messageObtain);
    }

    final void scheduleContextCleanup(ContextImpl contextImpl, String str, String str2) {
        ContextCleanupInfo contextCleanupInfo = new ContextCleanupInfo();
        contextCleanupInfo.context = contextImpl;
        contextCleanupInfo.who = str;
        contextCleanupInfo.what = str2;
        sendMessage(119, contextCleanupInfo);
    }

    /* JADX WARN: Removed duplicated region for block: B:80:0x01bc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.app.Activity performLaunchActivity(android.app.ActivityThread.ActivityClientRecord r24, android.content.Intent r25) {
        /*
            Method dump skipped, instruction units count: 528
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.ActivityThread.performLaunchActivity(android.app.ActivityThread$ActivityClientRecord, android.content.Intent):android.app.Activity");
    }

    private Context createBaseContextForActivity(ActivityClientRecord activityClientRecord, Activity activity) {
        ContextImpl contextImpl = new ContextImpl();
        contextImpl.init(activityClientRecord.packageInfo, activityClientRecord.token, this);
        contextImpl.setOuterContext(activity);
        String str = SystemProperties.get("debug.second-display.pkg");
        if (str == null || str.isEmpty() || !activityClientRecord.packageInfo.mPackageName.contains(str)) {
            return contextImpl;
        }
        DisplayManagerGlobal displayManagerGlobal = DisplayManagerGlobal.getInstance();
        for (int i : displayManagerGlobal.getDisplayIds()) {
            if (i != 0) {
                return contextImpl.createDisplayContext(displayManagerGlobal.getRealDisplay(i, activityClientRecord.token));
            }
        }
        return contextImpl;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLaunchActivity(ActivityClientRecord activityClientRecord, Intent intent) {
        unscheduleGcIdler();
        if (activityClientRecord.profileFd != null) {
            this.mProfiler.setProfiler(activityClientRecord.profileFile, activityClientRecord.profileFd);
            this.mProfiler.startProfiling();
            this.mProfiler.autoStopProfiler = activityClientRecord.autoStopProfiler;
        }
        handleConfigurationChanged(null, null);
        if (performLaunchActivity(activityClientRecord, intent) != null) {
            activityClientRecord.createdConfig = new Configuration(this.mConfiguration);
            Bundle bundle = activityClientRecord.state;
            handleResumeActivity(activityClientRecord.token, false, activityClientRecord.isForward, (activityClientRecord.activity.mFinished || activityClientRecord.startsNotResumed) ? false : true);
            if (activityClientRecord.activity.mFinished || !activityClientRecord.startsNotResumed) {
                return;
            }
            try {
                activityClientRecord.activity.mCalled = false;
                this.mInstrumentation.callActivityOnPause(activityClientRecord.activity);
                if (activityClientRecord.isPreHoneycomb()) {
                    activityClientRecord.state = bundle;
                }
            } catch (SuperNotCalledException e) {
                throw e;
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(activityClientRecord.activity, e2)) {
                    throw new RuntimeException("Unable to pause activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                }
            }
            if (!activityClientRecord.activity.mCalled) {
                throw new SuperNotCalledException("Activity " + activityClientRecord.intent.getComponent().toShortString() + " did not call through to super.onPause()");
            }
            activityClientRecord.paused = true;
            return;
        }
        try {
            ActivityManagerNative.getDefault().finishActivity(activityClientRecord.token, 0, null);
        } catch (RemoteException unused) {
        }
    }

    private void deliverNewIntents(ActivityClientRecord activityClientRecord, List<Intent> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Intent intent = list.get(i);
            intent.setExtrasClassLoader(activityClientRecord.activity.getClassLoader());
            activityClientRecord.activity.mFragments.noteStateNotSaved();
            this.mInstrumentation.callActivityOnNewIntent(activityClientRecord.activity, intent);
        }
    }

    public final void performNewIntents(IBinder iBinder, List<Intent> list) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            boolean z = !activityClientRecord.paused;
            if (z) {
                activityClientRecord.activity.mTemporaryPause = true;
                this.mInstrumentation.callActivityOnPause(activityClientRecord.activity);
            }
            deliverNewIntents(activityClientRecord, list);
            if (z) {
                activityClientRecord.activity.performResume();
                activityClientRecord.activity.mTemporaryPause = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNewIntent(NewIntentData newIntentData) {
        performNewIntents(newIntentData.token, newIntentData.intents);
    }

    public void handleRequestAssistContextExtras(RequestAssistContextExtras requestAssistContextExtras) {
        Bundle bundle = new Bundle();
        ActivityClientRecord activityClientRecord = this.mActivities.get(requestAssistContextExtras.activityToken);
        if (activityClientRecord != null) {
            activityClientRecord.activity.getApplication().dispatchOnProvideAssistData(activityClientRecord.activity, bundle);
            activityClientRecord.activity.onProvideAssistData(bundle);
        }
        if (bundle.isEmpty()) {
            bundle = null;
        }
        try {
            ActivityManagerNative.getDefault().reportAssistContextExtras(requestAssistContextExtras.requestToken, bundle);
        } catch (RemoteException unused) {
        }
    }

    public void handleTranslucentConversionComplete(IBinder iBinder, boolean z) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            activityClientRecord.activity.onTranslucentConversionComplete(z);
        }
    }

    public void handleInstallProvider(ProviderInfo providerInfo) {
        installContentProviders(this.mInitialApplication, Lists.newArrayList(new ProviderInfo[]{providerInfo}));
    }

    public static Intent getIntentBeingBroadcast() {
        return sCurrentBroadcastIntent.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleReceiver(ReceiverData receiverData) {
        unscheduleGcIdler();
        String className = receiverData.intent.getComponent().getClassName();
        LoadedApk packageInfoNoCheck = getPackageInfoNoCheck(receiverData.info.applicationInfo, receiverData.compatInfo);
        IActivityManager iActivityManager = ActivityManagerNative.getDefault();
        try {
            ClassLoader classLoader = packageInfoNoCheck.getClassLoader();
            receiverData.intent.setExtrasClassLoader(classLoader);
            receiverData.setExtrasClassLoader(classLoader);
            BroadcastReceiver broadcastReceiver = (BroadcastReceiver) classLoader.loadClass(className).newInstance();
            try {
                try {
                    ContextImpl contextImpl = (ContextImpl) packageInfoNoCheck.makeApplication(false, this.mInstrumentation).getBaseContext();
                    ThreadLocal<Intent> threadLocal = sCurrentBroadcastIntent;
                    threadLocal.set(receiverData.intent);
                    broadcastReceiver.setPendingResult(receiverData);
                    broadcastReceiver.onReceive(contextImpl.getReceiverRestrictedContext(), receiverData.intent);
                    threadLocal.set(null);
                } catch (Exception e) {
                    receiverData.sendFinished(iActivityManager);
                    if (!this.mInstrumentation.onException(broadcastReceiver, e)) {
                        throw new RuntimeException("Unable to start receiver " + className + ": " + e.toString(), e);
                    }
                    sCurrentBroadcastIntent.set(null);
                }
                if (broadcastReceiver.getPendingResult() != null) {
                    receiverData.finish();
                }
            } catch (Throwable th) {
                sCurrentBroadcastIntent.set(null);
                throw th;
            }
        } catch (Exception e2) {
            receiverData.sendFinished(iActivityManager);
            throw new RuntimeException("Unable to instantiate receiver " + className + ": " + e2.toString(), e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void handleCreateBackupAgent(CreateBackupAgentData createBackupAgentData) {
        try {
            if (getPackageManager().getPackageInfo(createBackupAgentData.appInfo.packageName, 0, UserHandle.myUserId()).applicationInfo.uid != Process.myUid()) {
                Slog.w(TAG, "Asked to instantiate non-matching package " + createBackupAgentData.appInfo.packageName);
                return;
            }
            unscheduleGcIdler();
            LoadedApk packageInfoNoCheck = getPackageInfoNoCheck(createBackupAgentData.appInfo, createBackupAgentData.compatInfo);
            String str = packageInfoNoCheck.mPackageName;
            if (str == null) {
                Slog.d(TAG, "Asked to create backup agent for nonexistent package");
                return;
            }
            if (this.mBackupAgents.get(str) != null) {
                Slog.d(TAG, "BackupAgent   for " + str + " already exists");
                return;
            }
            String str2 = createBackupAgentData.appInfo.backupAgentName;
            if (str2 == null && (createBackupAgentData.backupMode == 1 || createBackupAgentData.backupMode == 3)) {
                str2 = "android.app.backup.FullBackupAgent";
            }
            IBinder iBinderOnBind = null;
            try {
                try {
                    BackupAgent backupAgent = (BackupAgent) packageInfoNoCheck.getClassLoader().loadClass(str2).newInstance();
                    ContextImpl contextImpl = new ContextImpl();
                    contextImpl.init(packageInfoNoCheck, (IBinder) null, this);
                    contextImpl.setOuterContext(backupAgent);
                    backupAgent.attach(contextImpl);
                    backupAgent.onCreate();
                    iBinderOnBind = backupAgent.onBind();
                    this.mBackupAgents.put(str, backupAgent);
                } catch (Exception e) {
                    Slog.e(TAG, "Agent threw during creation: " + e);
                    if (createBackupAgentData.backupMode != 2 && createBackupAgentData.backupMode != 3) {
                        throw e;
                    }
                }
                try {
                    ActivityManagerNative.getDefault().backupAgentCreated(str, iBinderOnBind);
                } catch (RemoteException unused) {
                }
            } catch (Exception e2) {
                throw new RuntimeException("Unable to create BackupAgent " + str2 + ": " + e2.toString(), e2);
            }
        } catch (RemoteException e3) {
            Slog.e(TAG, "Can't reach package manager", e3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroyBackupAgent(CreateBackupAgentData createBackupAgentData) {
        String str = getPackageInfoNoCheck(createBackupAgentData.appInfo, createBackupAgentData.compatInfo).mPackageName;
        BackupAgent backupAgent = this.mBackupAgents.get(str);
        if (backupAgent != null) {
            try {
                backupAgent.onDestroy();
            } catch (Exception e) {
                Slog.w(TAG, "Exception thrown in onDestroy by backup agent of " + createBackupAgentData.appInfo);
                e.printStackTrace();
            }
            this.mBackupAgents.remove(str);
            return;
        }
        Slog.w(TAG, "Attempt to destroy unknown backup agent " + createBackupAgentData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCreateService(CreateServiceData createServiceData) {
        Service service;
        unscheduleGcIdler();
        LoadedApk packageInfoNoCheck = getPackageInfoNoCheck(createServiceData.info.applicationInfo, createServiceData.compatInfo);
        try {
            service = (Service) packageInfoNoCheck.getClassLoader().loadClass(createServiceData.info.name).newInstance();
        } catch (Exception e) {
            if (!this.mInstrumentation.onException(null, e)) {
                throw new RuntimeException("Unable to instantiate service " + createServiceData.info.name + ": " + e.toString(), e);
            }
            service = null;
        }
        try {
            ContextImpl contextImpl = new ContextImpl();
            contextImpl.init(packageInfoNoCheck, (IBinder) null, this);
            Application applicationMakeApplication = packageInfoNoCheck.makeApplication(false, this.mInstrumentation);
            contextImpl.setOuterContext(service);
            service.attach(contextImpl, this, createServiceData.info.name, createServiceData.token, applicationMakeApplication, ActivityManagerNative.getDefault());
            service.onCreate();
            this.mServices.put(createServiceData.token, service);
            try {
                ActivityManagerNative.getDefault().serviceDoneExecuting(createServiceData.token, 0, 0, 0);
            } catch (RemoteException unused) {
            }
        } catch (Exception e2) {
            if (!this.mInstrumentation.onException(service, e2)) {
                throw new RuntimeException("Unable to create service " + createServiceData.info.name + ": " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBindService(BindServiceData bindServiceData) {
        Service service = this.mServices.get(bindServiceData.token);
        if (service != null) {
            try {
                bindServiceData.intent.setExtrasClassLoader(service.getClassLoader());
                try {
                    if (!bindServiceData.rebind) {
                        ActivityManagerNative.getDefault().publishService(bindServiceData.token, bindServiceData.intent, service.onBind(bindServiceData.intent));
                    } else {
                        service.onRebind(bindServiceData.intent);
                        ActivityManagerNative.getDefault().serviceDoneExecuting(bindServiceData.token, 0, 0, 0);
                    }
                    ensureJitEnabled();
                } catch (RemoteException unused) {
                }
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(service, e)) {
                    throw new RuntimeException("Unable to bind to service " + service + " with " + bindServiceData.intent + ": " + e.toString(), e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUnbindService(BindServiceData bindServiceData) {
        Service service = this.mServices.get(bindServiceData.token);
        if (service != null) {
            try {
                bindServiceData.intent.setExtrasClassLoader(service.getClassLoader());
                boolean zOnUnbind = service.onUnbind(bindServiceData.intent);
                try {
                    if (zOnUnbind) {
                        ActivityManagerNative.getDefault().unbindFinished(bindServiceData.token, bindServiceData.intent, zOnUnbind);
                    } else {
                        ActivityManagerNative.getDefault().serviceDoneExecuting(bindServiceData.token, 0, 0, 0);
                    }
                } catch (RemoteException unused) {
                }
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(service, e)) {
                    throw new RuntimeException("Unable to unbind to service " + service + " with " + bindServiceData.intent + ": " + e.toString(), e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDumpService(DumpComponentInfo dumpComponentInfo) {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            Service service = this.mServices.get(dumpComponentInfo.token);
            if (service != null) {
                FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(dumpComponentInfo.fd.getFileDescriptor()));
                service.dump(dumpComponentInfo.fd.getFileDescriptor(), fastPrintWriter, dumpComponentInfo.args);
                fastPrintWriter.flush();
            }
        } finally {
            IoUtils.closeQuietly(dumpComponentInfo.fd);
            StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDumpActivity(DumpComponentInfo dumpComponentInfo) {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            ActivityClientRecord activityClientRecord = this.mActivities.get(dumpComponentInfo.token);
            if (activityClientRecord != null && activityClientRecord.activity != null) {
                PrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(dumpComponentInfo.fd.getFileDescriptor()));
                activityClientRecord.activity.dump(dumpComponentInfo.prefix, dumpComponentInfo.fd.getFileDescriptor(), fastPrintWriter, dumpComponentInfo.args);
                fastPrintWriter.flush();
            }
        } finally {
            IoUtils.closeQuietly(dumpComponentInfo.fd);
            StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDumpProvider(DumpComponentInfo dumpComponentInfo) {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            ProviderClientRecord providerClientRecord = this.mLocalProviders.get(dumpComponentInfo.token);
            if (providerClientRecord != null && providerClientRecord.mLocalProvider != null) {
                PrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(dumpComponentInfo.fd.getFileDescriptor()));
                providerClientRecord.mLocalProvider.dump(dumpComponentInfo.fd.getFileDescriptor(), fastPrintWriter, dumpComponentInfo.args);
                fastPrintWriter.flush();
            }
        } finally {
            IoUtils.closeQuietly(dumpComponentInfo.fd);
            StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleServiceArgs(ServiceArgsData serviceArgsData) {
        int iOnStartCommand;
        Service service = this.mServices.get(serviceArgsData.token);
        if (service != null) {
            try {
                if (serviceArgsData.args != null) {
                    serviceArgsData.args.setExtrasClassLoader(service.getClassLoader());
                }
                if (!serviceArgsData.taskRemoved) {
                    iOnStartCommand = service.onStartCommand(serviceArgsData.args, serviceArgsData.flags, serviceArgsData.startId);
                } else {
                    service.onTaskRemoved(serviceArgsData.args);
                    iOnStartCommand = 1000;
                }
                QueuedWork.waitToFinish();
                try {
                    ActivityManagerNative.getDefault().serviceDoneExecuting(serviceArgsData.token, 1, serviceArgsData.startId, iOnStartCommand);
                } catch (RemoteException unused) {
                }
                ensureJitEnabled();
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(service, e)) {
                    throw new RuntimeException("Unable to start service " + service + " with " + serviceArgsData.args + ": " + e.toString(), e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStopService(IBinder iBinder) {
        Service serviceRemove = this.mServices.remove(iBinder);
        if (serviceRemove != null) {
            try {
                serviceRemove.onDestroy();
                Context baseContext = serviceRemove.getBaseContext();
                if (baseContext instanceof ContextImpl) {
                    ((ContextImpl) baseContext).scheduleFinalCleanup(serviceRemove.getClassName(), "Service");
                }
                QueuedWork.waitToFinish();
                try {
                    ActivityManagerNative.getDefault().serviceDoneExecuting(iBinder, 0, 0, 0);
                } catch (RemoteException unused) {
                }
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(serviceRemove, e)) {
                    throw new RuntimeException("Unable to stop service " + serviceRemove + ": " + e.toString(), e);
                }
            }
        }
    }

    public final ActivityClientRecord performResumeActivity(IBinder iBinder, boolean z) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null && !activityClientRecord.activity.mFinished) {
            if (z) {
                activityClientRecord.hideForNow = false;
                activityClientRecord.activity.mStartedActivity = false;
            }
            try {
                activityClientRecord.activity.mFragments.noteStateNotSaved();
                if (activityClientRecord.pendingIntents != null) {
                    deliverNewIntents(activityClientRecord, activityClientRecord.pendingIntents);
                    activityClientRecord.pendingIntents = null;
                }
                if (activityClientRecord.pendingResults != null) {
                    deliverResults(activityClientRecord, activityClientRecord.pendingResults);
                    activityClientRecord.pendingResults = null;
                }
                activityClientRecord.activity.performResume();
                EventLog.writeEvent(LOG_ON_RESUME_CALLED, Integer.valueOf(UserHandle.myUserId()), activityClientRecord.activity.getComponentName().getClassName());
                activityClientRecord.paused = false;
                activityClientRecord.stopped = false;
                activityClientRecord.state = null;
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                    throw new RuntimeException("Unable to resume activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
                }
            }
        }
        return activityClientRecord;
    }

    static final void cleanUpPendingRemoveWindows(ActivityClientRecord activityClientRecord) {
        if (activityClientRecord.mPendingRemoveWindow != null) {
            activityClientRecord.mPendingRemoveWindowManager.removeViewImmediate(activityClientRecord.mPendingRemoveWindow);
            IBinder windowToken = activityClientRecord.mPendingRemoveWindow.getWindowToken();
            if (windowToken != null) {
                WindowManagerGlobal.getInstance().closeAll(windowToken, activityClientRecord.activity.getClass().getName(), "Activity");
            }
        }
        activityClientRecord.mPendingRemoveWindow = null;
        activityClientRecord.mPendingRemoveWindowManager = null;
    }

    final void handleResumeActivity(IBinder iBinder, boolean z, boolean z2, boolean z3) {
        unscheduleGcIdler();
        ActivityClientRecord activityClientRecordPerformResumeActivity = performResumeActivity(iBinder, z);
        try {
            if (activityClientRecordPerformResumeActivity != null) {
                Activity activity = activityClientRecordPerformResumeActivity.activity;
                int i = z2 ? 256 : 0;
                boolean zWillActivityBeVisible = !activity.mStartedActivity;
                if (!zWillActivityBeVisible) {
                    try {
                        zWillActivityBeVisible = ActivityManagerNative.getDefault().willActivityBeVisible(activity.getActivityToken());
                    } catch (RemoteException unused) {
                    }
                }
                if (activityClientRecordPerformResumeActivity.window == null && !activity.mFinished && zWillActivityBeVisible) {
                    activityClientRecordPerformResumeActivity.window = activityClientRecordPerformResumeActivity.activity.getWindow();
                    View decorView = activityClientRecordPerformResumeActivity.window.getDecorView();
                    decorView.setVisibility(4);
                    WindowManager windowManager = activity.getWindowManager();
                    WindowManager.LayoutParams attributes = activityClientRecordPerformResumeActivity.window.getAttributes();
                    activity.mDecor = decorView;
                    attributes.type = 1;
                    attributes.softInputMode |= i;
                    if (activity.mVisibleFromClient) {
                        activity.mWindowAdded = true;
                        windowManager.addView(decorView, attributes);
                    }
                } else if (!zWillActivityBeVisible) {
                    activityClientRecordPerformResumeActivity.hideForNow = true;
                }
                cleanUpPendingRemoveWindows(activityClientRecordPerformResumeActivity);
                if (!activityClientRecordPerformResumeActivity.activity.mFinished && zWillActivityBeVisible && activityClientRecordPerformResumeActivity.activity.mDecor != null && !activityClientRecordPerformResumeActivity.hideForNow) {
                    if (activityClientRecordPerformResumeActivity.newConfig != null) {
                        performConfigurationChanged(activityClientRecordPerformResumeActivity.activity, activityClientRecordPerformResumeActivity.newConfig);
                        freeTextLayoutCachesIfNeeded(activityClientRecordPerformResumeActivity.activity.mCurrentConfig.diff(activityClientRecordPerformResumeActivity.newConfig));
                        activityClientRecordPerformResumeActivity.newConfig = null;
                    }
                    WindowManager.LayoutParams attributes2 = activityClientRecordPerformResumeActivity.window.getAttributes();
                    if ((256 & attributes2.softInputMode) != i) {
                        attributes2.softInputMode = i | (attributes2.softInputMode & (-257));
                        if (activityClientRecordPerformResumeActivity.activity.mVisibleFromClient) {
                            activity.getWindowManager().updateViewLayout(activityClientRecordPerformResumeActivity.window.getDecorView(), attributes2);
                        }
                    }
                    activityClientRecordPerformResumeActivity.activity.mVisibleFromServer = true;
                    this.mNumVisibleActivities++;
                    if (activityClientRecordPerformResumeActivity.activity.mVisibleFromClient) {
                        activityClientRecordPerformResumeActivity.activity.makeVisible();
                    }
                }
                if (!activityClientRecordPerformResumeActivity.onlyLocalRequest) {
                    activityClientRecordPerformResumeActivity.nextIdle = this.mNewActivities;
                    this.mNewActivities = activityClientRecordPerformResumeActivity;
                    Looper.myQueue().addIdleHandler(new Idler());
                }
                activityClientRecordPerformResumeActivity.onlyLocalRequest = false;
                if (!z3) {
                } else {
                    ActivityManagerNative.getDefault().activityResumed(iBinder);
                }
            } else {
                ActivityManagerNative.getDefault().finishActivity(iBinder, 0, null);
            }
        } catch (RemoteException unused2) {
        }
    }

    private Bitmap createThumbnailBitmap(ActivityClientRecord activityClientRecord) {
        int dimensionPixelSize;
        Bitmap bitmapCreateBitmap = this.mAvailThumbnailBitmap;
        if (bitmapCreateBitmap == null) {
            try {
                int dimensionPixelSize2 = this.mThumbnailWidth;
                if (dimensionPixelSize2 < 0) {
                    Resources resources = activityClientRecord.activity.getResources();
                    dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.thumbnail_height);
                    this.mThumbnailHeight = dimensionPixelSize;
                    dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.thumbnail_width);
                    this.mThumbnailWidth = dimensionPixelSize2;
                } else {
                    dimensionPixelSize = this.mThumbnailHeight;
                }
                if (dimensionPixelSize2 > 0 && dimensionPixelSize > 0) {
                    bitmapCreateBitmap = Bitmap.createBitmap(activityClientRecord.activity.getResources().getDisplayMetrics(), dimensionPixelSize2, dimensionPixelSize, THUMBNAIL_FORMAT);
                    bitmapCreateBitmap.eraseColor(0);
                }
            } catch (Exception e) {
                if (this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                    return null;
                }
                throw new RuntimeException("Unable to create thumbnail of " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
            }
        }
        if (bitmapCreateBitmap != null) {
            Canvas canvas = this.mThumbnailCanvas;
            if (canvas == null) {
                canvas = new Canvas();
                this.mThumbnailCanvas = canvas;
            }
            canvas.setBitmap(bitmapCreateBitmap);
            if (!activityClientRecord.activity.onCreateThumbnail(bitmapCreateBitmap, canvas)) {
                this.mAvailThumbnailBitmap = bitmapCreateBitmap;
                bitmapCreateBitmap = null;
            }
            canvas.setBitmap(null);
        }
        return bitmapCreateBitmap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePauseActivity(IBinder iBinder, boolean z, boolean z2, int i) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            if (z2) {
                performUserLeavingActivity(activityClientRecord);
            }
            Activity activity = activityClientRecord.activity;
            activity.mConfigChangeFlags = i | activity.mConfigChangeFlags;
            performPauseActivity(iBinder, z, activityClientRecord.isPreHoneycomb());
            if (activityClientRecord.isPreHoneycomb()) {
                QueuedWork.waitToFinish();
            }
            try {
                ActivityManagerNative.getDefault().activityPaused(iBinder);
            } catch (RemoteException unused) {
            }
        }
    }

    final void performUserLeavingActivity(ActivityClientRecord activityClientRecord) {
        this.mInstrumentation.callActivityOnUserLeaving(activityClientRecord.activity);
    }

    final Bundle performPauseActivity(IBinder iBinder, boolean z, boolean z2) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            return performPauseActivity(activityClientRecord, z, z2);
        }
        return null;
    }

    final Bundle performPauseActivity(ActivityClientRecord activityClientRecord, boolean z, boolean z2) {
        Exception e;
        Bundle bundle;
        ArrayList<OnActivityPausedListener> arrayListRemove;
        Bundle bundle2 = null;
        if (activityClientRecord.paused) {
            if (activityClientRecord.activity.mFinished) {
                return null;
            }
            RuntimeException runtimeException = new RuntimeException("Performing pause of activity that is not resumed: " + activityClientRecord.intent.getComponent().toShortString());
            Slog.e(TAG, runtimeException.getMessage(), runtimeException);
        }
        if (z) {
            activityClientRecord.activity.mFinished = true;
        }
        try {
            try {
                if (!activityClientRecord.activity.mFinished && z2) {
                    bundle = new Bundle();
                    try {
                        bundle.setAllowFds(false);
                        this.mInstrumentation.callActivityOnSaveInstanceState(activityClientRecord.activity, bundle);
                        activityClientRecord.state = bundle;
                        bundle2 = bundle;
                    } catch (Exception e2) {
                        e = e2;
                        if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                            throw new RuntimeException("Unable to pause activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
                        }
                        bundle2 = bundle;
                    }
                }
                activityClientRecord.activity.mCalled = false;
                this.mInstrumentation.callActivityOnPause(activityClientRecord.activity);
                EventLog.writeEvent(LOG_ON_PAUSE_CALLED, Integer.valueOf(UserHandle.myUserId()), activityClientRecord.activity.getComponentName().getClassName());
            } catch (SuperNotCalledException e3) {
                throw e3;
            }
        } catch (Exception e4) {
            Bundle bundle3 = bundle2;
            e = e4;
            bundle = bundle3;
        }
        if (!activityClientRecord.activity.mCalled) {
            throw new SuperNotCalledException("Activity " + activityClientRecord.intent.getComponent().toShortString() + " did not call through to super.onPause()");
        }
        activityClientRecord.paused = true;
        synchronized (this.mOnPauseListeners) {
            arrayListRemove = this.mOnPauseListeners.remove(activityClientRecord.activity);
        }
        int size = arrayListRemove != null ? arrayListRemove.size() : 0;
        for (int i = 0; i < size; i++) {
            arrayListRemove.get(i).onPaused(activityClientRecord.activity);
        }
        return bundle2;
    }

    final void performStopActivity(IBinder iBinder, boolean z) {
        performStopActivityInner(this.mActivities.get(iBinder), null, false, z);
    }

    private static class StopInfo implements Runnable {
        ActivityClientRecord activity;
        CharSequence description;
        Bundle state;
        Bitmap thumbnail;

        private StopInfo() {
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                ActivityManagerNative.getDefault().activityStopped(this.activity.token, this.state, this.thumbnail, this.description);
            } catch (RemoteException unused) {
            }
        }
    }

    private static final class ProviderRefCount {
        public final ProviderClientRecord client;
        public final IActivityManager.ContentProviderHolder holder;
        public boolean removePending;
        public int stableCount;
        public int unstableCount;

        ProviderRefCount(IActivityManager.ContentProviderHolder contentProviderHolder, ProviderClientRecord providerClientRecord, int i, int i2) {
            this.holder = contentProviderHolder;
            this.client = providerClientRecord;
            this.stableCount = i;
            this.unstableCount = i2;
        }
    }

    private void performStopActivityInner(ActivityClientRecord activityClientRecord, StopInfo stopInfo, boolean z, boolean z2) {
        if (activityClientRecord != null) {
            if (!z && activityClientRecord.stopped) {
                if (activityClientRecord.activity.mFinished) {
                    return;
                }
                RuntimeException runtimeException = new RuntimeException("Performing stop of activity that is not resumed: " + activityClientRecord.intent.getComponent().toShortString());
                Slog.e(TAG, runtimeException.getMessage(), runtimeException);
            }
            if (stopInfo != null) {
                try {
                    stopInfo.thumbnail = null;
                    stopInfo.description = activityClientRecord.activity.onCreateDescription();
                } catch (Exception e) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                        throw new RuntimeException("Unable to save state of activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
                    }
                }
            }
            if (!activityClientRecord.activity.mFinished && z2) {
                if (activityClientRecord.state == null) {
                    Bundle bundle = new Bundle();
                    bundle.setAllowFds(false);
                    this.mInstrumentation.callActivityOnSaveInstanceState(activityClientRecord.activity, bundle);
                    activityClientRecord.state = bundle;
                } else {
                    Bundle bundle2 = activityClientRecord.state;
                }
            }
            if (!z) {
                try {
                    activityClientRecord.activity.performStop();
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e2)) {
                        throw new RuntimeException("Unable to stop activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                    }
                }
                activityClientRecord.stopped = true;
            }
            activityClientRecord.paused = true;
        }
    }

    private void updateVisibility(ActivityClientRecord activityClientRecord, boolean z) {
        View view = activityClientRecord.activity.mDecor;
        if (view != null) {
            if (z) {
                if (!activityClientRecord.activity.mVisibleFromServer) {
                    activityClientRecord.activity.mVisibleFromServer = true;
                    this.mNumVisibleActivities++;
                    if (activityClientRecord.activity.mVisibleFromClient) {
                        activityClientRecord.activity.makeVisible();
                    }
                }
                if (activityClientRecord.newConfig != null) {
                    performConfigurationChanged(activityClientRecord.activity, activityClientRecord.newConfig);
                    freeTextLayoutCachesIfNeeded(activityClientRecord.activity.mCurrentConfig.diff(activityClientRecord.newConfig));
                    activityClientRecord.newConfig = null;
                    return;
                }
                return;
            }
            if (activityClientRecord.activity.mVisibleFromServer) {
                activityClientRecord.activity.mVisibleFromServer = false;
                this.mNumVisibleActivities--;
                view.setVisibility(4);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStopActivity(IBinder iBinder, boolean z, int i) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        Activity activity = activityClientRecord.activity;
        activity.mConfigChangeFlags = i | activity.mConfigChangeFlags;
        StopInfo stopInfo = new StopInfo();
        performStopActivityInner(activityClientRecord, stopInfo, z, true);
        updateVisibility(activityClientRecord, z);
        if (!activityClientRecord.isPreHoneycomb()) {
            QueuedWork.waitToFinish();
        }
        stopInfo.activity = activityClientRecord;
        stopInfo.state = activityClientRecord.state;
        this.mH.post(stopInfo);
    }

    final void performRestartActivity(IBinder iBinder) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord.stopped) {
            activityClientRecord.activity.performRestart();
            activityClientRecord.stopped = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWindowVisibility(IBinder iBinder, boolean z) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord == null) {
            Log.w(TAG, "handleWindowVisibility: no activity for token " + iBinder);
            return;
        }
        if (!z && !activityClientRecord.stopped) {
            performStopActivityInner(activityClientRecord, null, z, false);
        } else if (z && activityClientRecord.stopped) {
            unscheduleGcIdler();
            activityClientRecord.activity.performRestart();
            activityClientRecord.stopped = false;
        }
        if (activityClientRecord.activity.mDecor != null) {
            updateVisibility(activityClientRecord, z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSleeping(IBinder iBinder, boolean z) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord == null) {
            Log.w(TAG, "handleSleeping: no activity for token " + iBinder);
            return;
        }
        if (z) {
            if (!activityClientRecord.stopped && !activityClientRecord.isPreHoneycomb()) {
                try {
                    activityClientRecord.activity.performStop();
                } catch (Exception e) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                        throw new RuntimeException("Unable to stop activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
                    }
                }
                activityClientRecord.stopped = true;
            }
            if (!activityClientRecord.isPreHoneycomb()) {
                QueuedWork.waitToFinish();
            }
            try {
                ActivityManagerNative.getDefault().activitySlept(activityClientRecord.token);
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        if (activityClientRecord.stopped && activityClientRecord.activity.mVisibleFromServer) {
            activityClientRecord.activity.performRestart();
            activityClientRecord.stopped = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetCoreSettings(Bundle bundle) {
        synchronized (this.mResourcesManager) {
            this.mCoreSettings = bundle;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUpdatePackageCompatibilityInfo(UpdateCompatibilityData updateCompatibilityData) {
        LoadedApk loadedApkPeekPackageInfo = peekPackageInfo(updateCompatibilityData.pkg, false);
        if (loadedApkPeekPackageInfo != null) {
            loadedApkPeekPackageInfo.setCompatibilityInfo(updateCompatibilityData.info);
        }
        LoadedApk loadedApkPeekPackageInfo2 = peekPackageInfo(updateCompatibilityData.pkg, true);
        if (loadedApkPeekPackageInfo2 != null) {
            loadedApkPeekPackageInfo2.setCompatibilityInfo(updateCompatibilityData.info);
        }
        handleConfigurationChanged(this.mConfiguration, updateCompatibilityData.info);
        WindowManagerGlobal.getInstance().reportNewConfiguration(this.mConfiguration);
    }

    private void deliverResults(ActivityClientRecord activityClientRecord, List<ResultInfo> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ResultInfo resultInfo = list.get(i);
            try {
                if (resultInfo.mData != null) {
                    resultInfo.mData.setExtrasClassLoader(activityClientRecord.activity.getClassLoader());
                }
                activityClientRecord.activity.dispatchActivityResult(resultInfo.mResultWho, resultInfo.mRequestCode, resultInfo.mResultCode, resultInfo.mData);
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                    throw new RuntimeException("Failure delivering result " + resultInfo + " to activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSendResult(ResultData resultData) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(resultData.token);
        if (activityClientRecord != null) {
            boolean z = !activityClientRecord.paused;
            if (!activityClientRecord.activity.mFinished && activityClientRecord.activity.mDecor != null && activityClientRecord.hideForNow && z) {
                updateVisibility(activityClientRecord, true);
            }
            if (z) {
                try {
                    activityClientRecord.activity.mCalled = false;
                    activityClientRecord.activity.mTemporaryPause = true;
                    this.mInstrumentation.callActivityOnPause(activityClientRecord.activity);
                    if (!activityClientRecord.activity.mCalled) {
                        throw new SuperNotCalledException("Activity " + activityClientRecord.intent.getComponent().toShortString() + " did not call through to super.onPause()");
                    }
                } catch (SuperNotCalledException e) {
                    throw e;
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e2)) {
                        throw new RuntimeException("Unable to pause activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                    }
                }
            }
            deliverResults(activityClientRecord, resultData.results);
            if (z) {
                activityClientRecord.activity.performResume();
                activityClientRecord.activity.mTemporaryPause = false;
            }
        }
    }

    public final ActivityClientRecord performDestroyActivity(IBinder iBinder, boolean z) {
        return performDestroyActivity(iBinder, z, 0, false);
    }

    private ActivityClientRecord performDestroyActivity(IBinder iBinder, boolean z, int i, boolean z2) {
        Class<?> cls;
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            cls = activityClientRecord.activity.getClass();
            Activity activity = activityClientRecord.activity;
            activity.mConfigChangeFlags = i | activity.mConfigChangeFlags;
            if (z) {
                activityClientRecord.activity.mFinished = true;
            }
            if (!activityClientRecord.paused) {
                try {
                    activityClientRecord.activity.mCalled = false;
                    this.mInstrumentation.callActivityOnPause(activityClientRecord.activity);
                    EventLog.writeEvent(LOG_ON_PAUSE_CALLED, Integer.valueOf(UserHandle.myUserId()), activityClientRecord.activity.getComponentName().getClassName());
                } catch (SuperNotCalledException e) {
                    throw e;
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e2)) {
                        throw new RuntimeException("Unable to pause activity " + safeToComponentShortString(activityClientRecord.intent) + ": " + e2.toString(), e2);
                    }
                }
                if (!activityClientRecord.activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + safeToComponentShortString(activityClientRecord.intent) + " did not call through to super.onPause()");
                }
                activityClientRecord.paused = true;
            }
            if (!activityClientRecord.stopped) {
                try {
                    activityClientRecord.activity.performStop();
                } catch (SuperNotCalledException e3) {
                    throw e3;
                } catch (Exception e4) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e4)) {
                        throw new RuntimeException("Unable to stop activity " + safeToComponentShortString(activityClientRecord.intent) + ": " + e4.toString(), e4);
                    }
                }
                activityClientRecord.stopped = true;
            }
            if (z2) {
                try {
                    activityClientRecord.lastNonConfigurationInstances = activityClientRecord.activity.retainNonConfigurationInstances();
                } catch (Exception e5) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e5)) {
                        throw new RuntimeException("Unable to retain activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e5.toString(), e5);
                    }
                }
            }
            try {
                activityClientRecord.activity.mCalled = false;
                this.mInstrumentation.callActivityOnDestroy(activityClientRecord.activity);
                if (!activityClientRecord.activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + safeToComponentShortString(activityClientRecord.intent) + " did not call through to super.onDestroy()");
                }
                if (activityClientRecord.window != null) {
                    activityClientRecord.window.closeAllPanels();
                }
            } catch (SuperNotCalledException e6) {
                throw e6;
            } catch (Exception e7) {
                if (!this.mInstrumentation.onException(activityClientRecord.activity, e7)) {
                    throw new RuntimeException("Unable to destroy activity " + safeToComponentShortString(activityClientRecord.intent) + ": " + e7.toString(), e7);
                }
            }
        } else {
            cls = null;
        }
        this.mActivities.remove(iBinder);
        StrictMode.decrementExpectedActivityCount(cls);
        return activityClientRecord;
    }

    private static String safeToComponentShortString(Intent intent) {
        ComponentName component = intent.getComponent();
        return component == null ? "[Unknown]" : component.toShortString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroyActivity(IBinder iBinder, boolean z, int i, boolean z2) {
        ActivityClientRecord activityClientRecordPerformDestroyActivity = performDestroyActivity(iBinder, z, i, z2);
        if (activityClientRecordPerformDestroyActivity != null) {
            cleanUpPendingRemoveWindows(activityClientRecordPerformDestroyActivity);
            WindowManager windowManager = activityClientRecordPerformDestroyActivity.activity.getWindowManager();
            View view = activityClientRecordPerformDestroyActivity.activity.mDecor;
            if (view != null) {
                if (activityClientRecordPerformDestroyActivity.activity.mVisibleFromServer) {
                    this.mNumVisibleActivities--;
                }
                IBinder windowToken = view.getWindowToken();
                if (activityClientRecordPerformDestroyActivity.activity.mWindowAdded) {
                    if (activityClientRecordPerformDestroyActivity.onlyLocalRequest) {
                        activityClientRecordPerformDestroyActivity.mPendingRemoveWindow = view;
                        activityClientRecordPerformDestroyActivity.mPendingRemoveWindowManager = windowManager;
                    } else {
                        windowManager.removeViewImmediate(view);
                    }
                }
                if (windowToken != null && activityClientRecordPerformDestroyActivity.mPendingRemoveWindow == null) {
                    WindowManagerGlobal.getInstance().closeAll(windowToken, activityClientRecordPerformDestroyActivity.activity.getClass().getName(), "Activity");
                }
                activityClientRecordPerformDestroyActivity.activity.mDecor = null;
            }
            if (activityClientRecordPerformDestroyActivity.mPendingRemoveWindow == null) {
                WindowManagerGlobal.getInstance().closeAll(iBinder, activityClientRecordPerformDestroyActivity.activity.getClass().getName(), "Activity");
            }
            Context baseContext = activityClientRecordPerformDestroyActivity.activity.getBaseContext();
            if (baseContext instanceof ContextImpl) {
                ((ContextImpl) baseContext).scheduleFinalCleanup(activityClientRecordPerformDestroyActivity.activity.getClass().getName(), "Activity");
            }
        }
        if (z) {
            try {
                ActivityManagerNative.getDefault().activityDestroyed(iBinder);
            } catch (RemoteException unused) {
            }
        }
    }

    public final void requestRelaunchActivity(IBinder iBinder, List<ResultInfo> list, List<Intent> list2, int i, boolean z, Configuration configuration, boolean z2) {
        ActivityClientRecord activityClientRecord;
        synchronized (this.mResourcesManager) {
            int i2 = 0;
            while (true) {
                if (i2 >= this.mRelaunchingActivities.size()) {
                    activityClientRecord = null;
                    break;
                }
                activityClientRecord = this.mRelaunchingActivities.get(i2);
                if (activityClientRecord.token == iBinder) {
                    if (list != null) {
                        if (activityClientRecord.pendingResults != null) {
                            activityClientRecord.pendingResults.addAll(list);
                        } else {
                            activityClientRecord.pendingResults = list;
                        }
                    }
                    if (list2 != null) {
                        if (activityClientRecord.pendingIntents != null) {
                            activityClientRecord.pendingIntents.addAll(list2);
                        } else {
                            activityClientRecord.pendingIntents = list2;
                        }
                    }
                } else {
                    i2++;
                }
            }
            if (activityClientRecord == null) {
                activityClientRecord = new ActivityClientRecord();
                activityClientRecord.token = iBinder;
                activityClientRecord.pendingResults = list;
                activityClientRecord.pendingIntents = list2;
                if (!z2) {
                    ActivityClientRecord activityClientRecord2 = this.mActivities.get(iBinder);
                    if (activityClientRecord2 != null) {
                        activityClientRecord.startsNotResumed = activityClientRecord2.paused;
                    }
                    activityClientRecord.onlyLocalRequest = true;
                }
                this.mRelaunchingActivities.add(activityClientRecord);
                sendMessage(126, activityClientRecord);
            }
            if (z2) {
                activityClientRecord.startsNotResumed = z;
                activityClientRecord.onlyLocalRequest = false;
            }
            if (configuration != null) {
                activityClientRecord.createdConfig = configuration;
            }
            activityClientRecord.pendingConfigChanges |= i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRelaunchActivity(ActivityClientRecord activityClientRecord) {
        unscheduleGcIdler();
        synchronized (this.mResourcesManager) {
            int size = this.mRelaunchingActivities.size();
            IBinder iBinder = activityClientRecord.token;
            int i = 0;
            int i2 = 0;
            ActivityClientRecord activityClientRecord2 = null;
            while (i < size) {
                ActivityClientRecord activityClientRecord3 = this.mRelaunchingActivities.get(i);
                if (activityClientRecord3.token == iBinder) {
                    i2 |= activityClientRecord3.pendingConfigChanges;
                    this.mRelaunchingActivities.remove(i);
                    i--;
                    size--;
                    activityClientRecord2 = activityClientRecord3;
                }
                i++;
            }
            if (activityClientRecord2 == null) {
                return;
            }
            Configuration configuration = this.mPendingConfiguration;
            if (configuration != null) {
                this.mPendingConfiguration = null;
            } else {
                configuration = null;
            }
            if (activityClientRecord2.createdConfig != null && ((this.mConfiguration == null || (activityClientRecord2.createdConfig.isOtherSeqNewer(this.mConfiguration) && this.mConfiguration.diff(activityClientRecord2.createdConfig) != 0)) && (configuration == null || activityClientRecord2.createdConfig.isOtherSeqNewer(configuration)))) {
                configuration = activityClientRecord2.createdConfig;
            }
            if (configuration != null) {
                this.mCurDefaultDisplayDpi = configuration.densityDpi;
                updateDefaultDensity();
                handleConfigurationChanged(configuration, null);
            }
            ActivityClientRecord activityClientRecord4 = this.mActivities.get(activityClientRecord2.token);
            if (activityClientRecord4 == null) {
                return;
            }
            activityClientRecord4.activity.mConfigChangeFlags |= i2;
            activityClientRecord4.onlyLocalRequest = activityClientRecord2.onlyLocalRequest;
            Intent intent = activityClientRecord4.activity.mIntent;
            activityClientRecord4.activity.mChangingConfigurations = true;
            if (!activityClientRecord4.paused) {
                performPauseActivity(activityClientRecord4.token, false, activityClientRecord4.isPreHoneycomb());
            }
            if (activityClientRecord4.state == null && !activityClientRecord4.stopped && !activityClientRecord4.isPreHoneycomb()) {
                activityClientRecord4.state = new Bundle();
                activityClientRecord4.state.setAllowFds(false);
                this.mInstrumentation.callActivityOnSaveInstanceState(activityClientRecord4.activity, activityClientRecord4.state);
            }
            handleDestroyActivity(activityClientRecord4.token, false, i2, true);
            activityClientRecord4.activity = null;
            activityClientRecord4.window = null;
            activityClientRecord4.hideForNow = false;
            activityClientRecord4.nextIdle = null;
            if (activityClientRecord2.pendingResults != null) {
                if (activityClientRecord4.pendingResults == null) {
                    activityClientRecord4.pendingResults = activityClientRecord2.pendingResults;
                } else {
                    activityClientRecord4.pendingResults.addAll(activityClientRecord2.pendingResults);
                }
            }
            if (activityClientRecord2.pendingIntents != null) {
                if (activityClientRecord4.pendingIntents == null) {
                    activityClientRecord4.pendingIntents = activityClientRecord2.pendingIntents;
                } else {
                    activityClientRecord4.pendingIntents.addAll(activityClientRecord2.pendingIntents);
                }
            }
            activityClientRecord4.startsNotResumed = activityClientRecord2.startsNotResumed;
            handleLaunchActivity(activityClientRecord4, intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRequestThumbnail(IBinder iBinder) {
        CharSequence charSequenceOnCreateDescription;
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        Bitmap bitmapCreateThumbnailBitmap = createThumbnailBitmap(activityClientRecord);
        try {
            charSequenceOnCreateDescription = activityClientRecord.activity.onCreateDescription();
        } catch (Exception e) {
            if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                throw new RuntimeException("Unable to create description of activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
            }
            charSequenceOnCreateDescription = null;
        }
        try {
            ActivityManagerNative.getDefault().reportThumbnail(iBinder, bitmapCreateThumbnailBitmap, charSequenceOnCreateDescription);
        } catch (RemoteException unused) {
        }
    }

    ArrayList<ComponentCallbacks2> collectComponentCallbacks(boolean z, Configuration configuration) {
        int i;
        ArrayList<ComponentCallbacks2> arrayList = new ArrayList<>();
        synchronized (this.mResourcesManager) {
            int size = this.mAllApplications.size();
            for (int i2 = 0; i2 < size; i2++) {
                arrayList.add(this.mAllApplications.get(i2));
            }
            int size2 = this.mActivities.size();
            for (int i3 = 0; i3 < size2; i3++) {
                ActivityClientRecord activityClientRecordValueAt = this.mActivities.valueAt(i3);
                Activity activity = activityClientRecordValueAt.activity;
                if (activity != null) {
                    Configuration configurationApplyConfigCompatMainThread = applyConfigCompatMainThread(this.mCurDefaultDisplayDpi, configuration, activityClientRecordValueAt.packageInfo.getCompatibilityInfo());
                    if (!activityClientRecordValueAt.activity.mFinished && (z || !activityClientRecordValueAt.paused)) {
                        arrayList.add(activity);
                    } else if (configurationApplyConfigCompatMainThread != null) {
                        activityClientRecordValueAt.newConfig = configurationApplyConfigCompatMainThread;
                    }
                }
            }
            int size3 = this.mServices.size();
            for (int i4 = 0; i4 < size3; i4++) {
                arrayList.add(this.mServices.valueAt(i4));
            }
        }
        synchronized (this.mProviderMap) {
            int size4 = this.mLocalProviders.size();
            for (i = 0; i < size4; i++) {
                arrayList.add(this.mLocalProviders.valueAt(i).mLocalProvider);
            }
        }
        return arrayList;
    }

    private static void performConfigurationChanged(ComponentCallbacks2 componentCallbacks2, Configuration configuration) {
        int iDiff;
        Activity activity = componentCallbacks2 instanceof Activity ? (Activity) componentCallbacks2 : null;
        if (activity != null) {
            activity.mCalled = false;
        }
        boolean z = true;
        if (activity != null && activity.mCurrentConfig != null && ((iDiff = activity.mCurrentConfig.diff(configuration)) == 0 || (iDiff & (~activity.mActivityInfo.getRealConfigChanged())) != 0)) {
            z = false;
        }
        if (z) {
            componentCallbacks2.onConfigurationChanged(configuration);
            if (activity != null) {
                if (!activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + activity.getLocalClassName() + " did not call through to super.onConfigurationChanged()");
                }
                activity.mConfigChangeFlags = 0;
                activity.mCurrentConfig = new Configuration(configuration);
            }
        }
    }

    public final void applyConfigurationToResources(Configuration configuration) {
        synchronized (this.mResourcesManager) {
            this.mResourcesManager.applyConfigurationToResourcesLocked(configuration, null);
        }
    }

    final Configuration applyCompatConfiguration(int i) {
        Configuration configuration = this.mConfiguration;
        if (this.mCompatConfiguration == null) {
            this.mCompatConfiguration = new Configuration();
        }
        this.mCompatConfiguration.setTo(this.mConfiguration);
        return this.mResourcesManager.applyCompatConfiguration(i, this.mCompatConfiguration) ? this.mCompatConfiguration : configuration;
    }

    final void handleConfigurationChanged(Configuration configuration, CompatibilityInfo compatibilityInfo) {
        synchronized (this.mResourcesManager) {
            Configuration configuration2 = this.mPendingConfiguration;
            if (configuration2 != null) {
                if (!configuration2.isOtherSeqNewer(configuration)) {
                    configuration = this.mPendingConfiguration;
                    this.mCurDefaultDisplayDpi = configuration.densityDpi;
                    updateDefaultDensity();
                }
                this.mPendingConfiguration = null;
            }
            if (configuration == null) {
                return;
            }
            this.mResourcesManager.applyConfigurationToResourcesLocked(configuration, compatibilityInfo);
            if (this.mConfiguration == null) {
                this.mConfiguration = new Configuration();
            }
            if (this.mConfiguration.isOtherSeqNewer(configuration) || compatibilityInfo != null) {
                int iDiff = this.mConfiguration.diff(configuration);
                this.mConfiguration.updateFrom(configuration);
                Configuration configurationApplyCompatConfiguration = applyCompatConfiguration(this.mCurDefaultDisplayDpi);
                ArrayList<ComponentCallbacks2> arrayListCollectComponentCallbacks = collectComponentCallbacks(false, configurationApplyCompatConfiguration);
                WindowManagerGlobal.getInstance().trimLocalMemory();
                freeTextLayoutCachesIfNeeded(iDiff);
                if (arrayListCollectComponentCallbacks != null) {
                    int size = arrayListCollectComponentCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        performConfigurationChanged(arrayListCollectComponentCallbacks.get(i), configurationApplyCompatConfiguration);
                    }
                }
            }
        }
    }

    static void freeTextLayoutCachesIfNeeded(int i) {
        if (i != 0) {
            if ((i & 4) != 0) {
                Canvas.freeTextLayoutCaches();
            }
        }
    }

    final void handleActivityConfigurationChanged(IBinder iBinder) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord == null || activityClientRecord.activity == null) {
            return;
        }
        performConfigurationChanged(activityClientRecord.activity, this.mCompatConfiguration);
        freeTextLayoutCachesIfNeeded(activityClientRecord.activity.mCurrentConfig.diff(this.mCompatConfiguration));
    }

    final void handleProfilerControl(boolean z, ProfilerControlData profilerControlData, int i) {
        try {
            if (z) {
                try {
                    try {
                        this.mProfiler.setProfiler(profilerControlData.path, profilerControlData.fd);
                        this.mProfiler.autoStopProfiler = false;
                        this.mProfiler.startProfiling();
                        profilerControlData.fd.close();
                    } catch (RuntimeException unused) {
                        Slog.w(TAG, "Profiling failed on path " + profilerControlData.path + " -- can the process access this path?");
                        profilerControlData.fd.close();
                    }
                    return;
                } catch (Throwable th) {
                    try {
                        profilerControlData.fd.close();
                    } catch (IOException e) {
                        Slog.w(TAG, "Failure closing profile fd", e);
                    }
                    throw th;
                }
            }
            this.mProfiler.stopProfiling();
        } catch (IOException e2) {
            Slog.w(TAG, "Failure closing profile fd", e2);
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:8:0x0018 -> B:21:0x0056). Please report as a decompilation issue!!! */
    static final void handleDumpHeap(boolean z, DumpHeapData dumpHeapData) {
        try {
            try {
                if (z) {
                    try {
                        Debug.dumpHprofData(dumpHeapData.path, dumpHeapData.fd.getFileDescriptor());
                        dumpHeapData.fd.close();
                    } catch (IOException unused) {
                        Slog.w(TAG, "Managed heap dump failed on path " + dumpHeapData.path + " -- can the process access this path?");
                        dumpHeapData.fd.close();
                    }
                } else {
                    Debug.dumpNativeHeap(dumpHeapData.fd.getFileDescriptor());
                }
            } catch (IOException e) {
                Slog.w(TAG, "Failure closing profile fd", e);
            }
        } catch (Throwable th) {
            try {
                dumpHeapData.fd.close();
            } catch (IOException e2) {
                Slog.w(TAG, "Failure closing profile fd", e2);
            }
            throw th;
        }
    }

    final void handleDispatchPackageBroadcast(int i, String[] strArr) {
        WeakReference<LoadedApk> weakReference;
        WeakReference<LoadedApk> weakReference2;
        boolean z = false;
        if (strArr != null) {
            for (int length = strArr.length - 1; length >= 0; length--) {
                if (!z && (((weakReference = this.mPackages.get(strArr[length])) != null && weakReference.get() != null) || ((weakReference2 = this.mResourcePackages.get(strArr[length])) != null && weakReference2.get() != null))) {
                    z = true;
                }
                this.mPackages.remove(strArr[length]);
                this.mResourcePackages.remove(strArr[length]);
            }
        }
        ApplicationPackageManager.handlePackageBroadcast(i, strArr, z);
    }

    final void handleLowMemory() {
        ArrayList<ComponentCallbacks2> arrayListCollectComponentCallbacks = collectComponentCallbacks(true, null);
        int size = arrayListCollectComponentCallbacks.size();
        for (int i = 0; i < size; i++) {
            arrayListCollectComponentCallbacks.get(i).onLowMemory();
        }
        if (Process.myUid() != 1000) {
            EventLog.writeEvent(SQLITE_MEM_RELEASED_EVENT_LOG_TAG, SQLiteDatabase.releaseMemory());
        }
        Canvas.freeCaches();
        Canvas.freeTextLayoutCaches();
        BinderInternal.forceGc("mem");
    }

    final void handleTrimMemory(int i) {
        WindowManagerGlobal windowManagerGlobal = WindowManagerGlobal.getInstance();
        windowManagerGlobal.startTrimMemory(i);
        ArrayList<ComponentCallbacks2> arrayListCollectComponentCallbacks = collectComponentCallbacks(true, null);
        int size = arrayListCollectComponentCallbacks.size();
        for (int i2 = 0; i2 < size; i2++) {
            arrayListCollectComponentCallbacks.get(i2).onTrimMemory(i);
        }
        windowManagerGlobal.endTrimMemory();
    }

    private void setupGraphicsSupport(LoadedApk loadedApk, File file) {
        if (Process.isIsolated()) {
            return;
        }
        try {
            String[] packagesForUid = getPackageManager().getPackagesForUid(Process.myUid());
            if (packagesForUid == null || packagesForUid.length != 1) {
                return;
            }
            HardwareRenderer.setupDiskCache(file);
            RenderScript.setupDiskCache(file);
        } catch (RemoteException unused) {
        }
    }

    private void updateDefaultDensity() {
        int i = this.mCurDefaultDisplayDpi;
        if (i == 0 || i == DisplayMetrics.DENSITY_DEVICE || this.mDensityCompatMode) {
            return;
        }
        Slog.i(TAG, "Switching default density from " + DisplayMetrics.DENSITY_DEVICE + " to " + this.mCurDefaultDisplayDpi);
        DisplayMetrics.DENSITY_DEVICE = this.mCurDefaultDisplayDpi;
        Bitmap.setDefaultDensity(160);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBindApplication(AppBindData appBindData) throws PackageManager.NameNotFoundException {
        InstrumentationInfo instrumentationInfo;
        List<ProviderInfo> list;
        this.mBoundApplication = appBindData;
        this.mConfiguration = new Configuration(appBindData.config);
        this.mCompatConfiguration = new Configuration(appBindData.config);
        Profiler profiler = new Profiler();
        this.mProfiler = profiler;
        profiler.profileFile = appBindData.initProfileFile;
        this.mProfiler.profileFd = appBindData.initProfileFd;
        this.mProfiler.autoStopProfiler = appBindData.initAutoStopProfiler;
        Process.setArgV0(appBindData.processName);
        DdmHandleAppName.setAppName(appBindData.processName, UserHandle.myUserId());
        if (appBindData.persistent && !ActivityManager.isHighEndGfx()) {
            HardwareRenderer.disable(false);
        }
        if (this.mProfiler.profileFd != null) {
            this.mProfiler.startProfiling();
        }
        if (appBindData.appInfo.targetSdkVersion <= 12) {
            AsyncTask.setDefaultExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        TimeZone.setDefault(null);
        Locale.setDefault(appBindData.config.locale);
        this.mResourcesManager.applyConfigurationToResourcesLocked(appBindData.config, appBindData.compatInfo);
        int i = appBindData.config.densityDpi;
        this.mCurDefaultDisplayDpi = i;
        applyCompatConfiguration(i);
        appBindData.info = getPackageInfoNoCheck(appBindData.appInfo, appBindData.compatInfo);
        if ((appBindData.appInfo.flags & 8192) == 0) {
            this.mDensityCompatMode = true;
            Bitmap.setDefaultDensity(160);
        }
        updateDefaultDensity();
        ContextImpl contextImpl = new ContextImpl();
        contextImpl.init(appBindData.info, (IBinder) null, this);
        if (!Process.isIsolated()) {
            File cacheDir = contextImpl.getCacheDir();
            if (cacheDir != null) {
                System.setProperty("java.io.tmpdir", cacheDir.getAbsolutePath());
                setupGraphicsSupport(appBindData.info, cacheDir);
            } else {
                Log.e(TAG, "Unable to setupGraphicsSupport due to missing cache directory");
            }
        }
        if ((appBindData.appInfo.flags & 129) != 0) {
            StrictMode.conditionallyEnableDebugLogging();
        }
        if (appBindData.appInfo.targetSdkVersion > 9) {
            StrictMode.enableDeathOnNetwork();
        }
        if (appBindData.debugMode != 0) {
            Debug.changeDebugPort(8100);
            if (appBindData.debugMode == 2) {
                Slog.w(TAG, "Application " + appBindData.info.getPackageName() + " is waiting for the debugger on port 8100...");
                IActivityManager iActivityManager = ActivityManagerNative.getDefault();
                try {
                    iActivityManager.showWaitingForDebugger(this.mAppThread, true);
                } catch (RemoteException unused) {
                }
                Debug.waitForDebugger();
                try {
                    iActivityManager.showWaitingForDebugger(this.mAppThread, false);
                } catch (RemoteException unused2) {
                }
            } else {
                Slog.w(TAG, "Application " + appBindData.info.getPackageName() + " can be debugged on port 8100...");
            }
        }
        if (appBindData.enableOpenGlTrace) {
            GLUtils.setTracingLevel(1);
        }
        Trace.setAppTracingAllowed((appBindData.appInfo.flags & 2) != 0);
        IBinder service = ServiceManager.getService(Context.CONNECTIVITY_SERVICE);
        if (service != null) {
            try {
                Proxy.setHttpProxySystemProperty(IConnectivityManager.Stub.asInterface(service).getProxy());
            } catch (RemoteException unused3) {
            }
        }
        if (appBindData.instrumentationName != null) {
            try {
                instrumentationInfo = contextImpl.getPackageManager().getInstrumentationInfo(appBindData.instrumentationName, 0);
            } catch (PackageManager.NameNotFoundException unused4) {
                instrumentationInfo = null;
            }
            if (instrumentationInfo == null) {
                throw new RuntimeException("Unable to find instrumentation info for: " + appBindData.instrumentationName);
            }
            this.mInstrumentationAppDir = instrumentationInfo.sourceDir;
            this.mInstrumentationAppLibraryDir = instrumentationInfo.nativeLibraryDir;
            this.mInstrumentationAppPackage = instrumentationInfo.packageName;
            this.mInstrumentedAppDir = appBindData.info.getAppDir();
            this.mInstrumentedAppLibraryDir = appBindData.info.getLibDir();
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.packageName = instrumentationInfo.packageName;
            applicationInfo.sourceDir = instrumentationInfo.sourceDir;
            applicationInfo.publicSourceDir = instrumentationInfo.publicSourceDir;
            applicationInfo.dataDir = instrumentationInfo.dataDir;
            applicationInfo.nativeLibraryDir = instrumentationInfo.nativeLibraryDir;
            LoadedApk packageInfo = getPackageInfo(applicationInfo, appBindData.compatInfo, contextImpl.getClassLoader(), false, true);
            ContextImpl contextImpl2 = new ContextImpl();
            contextImpl2.init(packageInfo, (IBinder) null, this);
            try {
                Instrumentation instrumentation = (Instrumentation) contextImpl2.getClassLoader().loadClass(appBindData.instrumentationName.getClassName()).newInstance();
                this.mInstrumentation = instrumentation;
                instrumentation.init(this, contextImpl2, contextImpl, new ComponentName(instrumentationInfo.packageName, instrumentationInfo.name), appBindData.instrumentationWatcher, appBindData.instrumentationUiAutomationConnection);
                if (this.mProfiler.profileFile != null && !instrumentationInfo.handleProfiling && this.mProfiler.profileFd == null) {
                    this.mProfiler.handlingProfiling = true;
                    File file = new File(this.mProfiler.profileFile);
                    file.getParentFile().mkdirs();
                    Debug.startMethodTracing(file.toString(), 8388608);
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate instrumentation " + appBindData.instrumentationName + ": " + e.toString(), e);
            }
        } else {
            this.mInstrumentation = new Instrumentation();
        }
        if ((appBindData.appInfo.flags & 1048576) != 0) {
            VMRuntime.getRuntime().clearGrowthLimit();
        }
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            Application applicationMakeApplication = appBindData.info.makeApplication(appBindData.restrictedBackupMode, null);
            this.mInitialApplication = applicationMakeApplication;
            if (!appBindData.restrictedBackupMode && (list = appBindData.providers) != null) {
                installContentProviders(applicationMakeApplication, list);
                this.mH.sendEmptyMessageDelayed(132, 10000L);
            }
            try {
                this.mInstrumentation.onCreate(appBindData.instrumentationArgs);
                try {
                    this.mInstrumentation.callApplicationOnCreate(applicationMakeApplication);
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(applicationMakeApplication, e2)) {
                        throw new RuntimeException("Unable to create application " + applicationMakeApplication.getClass().getName() + ": " + e2.toString(), e2);
                    }
                }
            } catch (Exception e3) {
                throw new RuntimeException("Exception thrown in onCreate() of " + appBindData.instrumentationName + ": " + e3.toString(), e3);
            }
        } finally {
            StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        }
    }

    final void finishInstrumentation(int i, Bundle bundle) {
        IActivityManager iActivityManager = ActivityManagerNative.getDefault();
        if (this.mProfiler.profileFile != null && this.mProfiler.handlingProfiling && this.mProfiler.profileFd == null) {
            Debug.stopMethodTracing();
        }
        try {
            iActivityManager.finishInstrumentation(this.mAppThread, i, bundle);
        } catch (RemoteException unused) {
        }
    }

    private void installContentProviders(Context context, List<ProviderInfo> list) {
        ArrayList arrayList = new ArrayList();
        Iterator<ProviderInfo> it = list.iterator();
        while (it.hasNext()) {
            IActivityManager.ContentProviderHolder contentProviderHolderInstallProvider = installProvider(context, null, it.next(), false, true, true);
            if (contentProviderHolderInstallProvider != null) {
                contentProviderHolderInstallProvider.noReleaseNeeded = true;
                arrayList.add(contentProviderHolderInstallProvider);
            }
        }
        try {
            ActivityManagerNative.getDefault().publishContentProviders(getApplicationThread(), arrayList);
        } catch (RemoteException unused) {
        }
    }

    public final IContentProvider acquireProvider(Context context, String str, int i, boolean z) throws RemoteException {
        IActivityManager.ContentProviderHolder contentProvider;
        IContentProvider iContentProviderAcquireExistingProvider = acquireExistingProvider(context, str, i, z);
        if (iContentProviderAcquireExistingProvider != null) {
            return iContentProviderAcquireExistingProvider;
        }
        try {
            contentProvider = ActivityManagerNative.getDefault().getContentProvider(getApplicationThread(), str, i, z);
        } catch (RemoteException unused) {
            contentProvider = null;
        }
        if (contentProvider == null) {
            Slog.e(TAG, "Failed to find provider info for " + str);
            return null;
        }
        return installProvider(context, contentProvider, contentProvider.info, true, contentProvider.noReleaseNeeded, z).provider;
    }

    private final void incProviderRefLocked(ProviderRefCount providerRefCount, boolean z) {
        int i = 0;
        try {
            if (z) {
                providerRefCount.stableCount++;
                if (providerRefCount.stableCount != 1) {
                    return;
                }
                if (providerRefCount.removePending) {
                    providerRefCount.removePending = false;
                    this.mH.removeMessages(131, providerRefCount);
                    i = -1;
                }
                ActivityManagerNative.getDefault().refContentProvider(providerRefCount.holder.connection, 1, i);
            } else {
                providerRefCount.unstableCount++;
                if (providerRefCount.unstableCount != 1) {
                    return;
                }
                if (providerRefCount.removePending) {
                    providerRefCount.removePending = false;
                    this.mH.removeMessages(131, providerRefCount);
                    return;
                }
                ActivityManagerNative.getDefault().refContentProvider(providerRefCount.holder.connection, 0, 1);
            }
        } catch (RemoteException unused) {
        }
    }

    public final IContentProvider acquireExistingProvider(Context context, String str, int i, boolean z) {
        synchronized (this.mProviderMap) {
            ProviderClientRecord providerClientRecord = this.mProviderMap.get(new ProviderKey(str, i));
            if (providerClientRecord == null) {
                return null;
            }
            IContentProvider iContentProvider = providerClientRecord.mProvider;
            IBinder iBinderAsBinder = iContentProvider.asBinder();
            if (!iBinderAsBinder.isBinderAlive()) {
                Log.i(TAG, "Acquiring provider " + str + " for user " + i + ": existing object's process dead");
                handleUnstableProviderDiedLocked(iBinderAsBinder, true);
                return null;
            }
            ProviderRefCount providerRefCount = this.mProviderRefCountMap.get(iBinderAsBinder);
            if (providerRefCount != null) {
                incProviderRefLocked(providerRefCount, z);
            }
            return iContentProvider;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x0066 A[Catch: all -> 0x009a, TRY_ENTER, TryCatch #1 {, blocks: (B:7:0x000b, B:9:0x0015, B:13:0x001b, B:15:0x001f, B:17:0x0021, B:19:0x002a, B:23:0x0031, B:26:0x003c, B:42:0x0066, B:44:0x006a, B:45:0x007a, B:46:0x0098, B:28:0x0040, B:30:0x0044, B:32:0x0046, B:34:0x004f, B:39:0x0058), top: B:55:0x000b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean releaseProvider(android.content.IContentProvider r7, boolean r8) {
        /*
            r6 = this;
            r0 = 0
            if (r7 != 0) goto L4
            return r0
        L4:
            android.os.IBinder r7 = r7.asBinder()
            android.util.ArrayMap<android.app.ActivityThread$ProviderKey, android.app.ActivityThread$ProviderClientRecord> r1 = r6.mProviderMap
            monitor-enter(r1)
            android.util.ArrayMap<android.os.IBinder, android.app.ActivityThread$ProviderRefCount> r2 = r6.mProviderRefCountMap     // Catch: java.lang.Throwable -> L9a
            java.lang.Object r7 = r2.get(r7)     // Catch: java.lang.Throwable -> L9a
            android.app.ActivityThread$ProviderRefCount r7 = (android.app.ActivityThread.ProviderRefCount) r7     // Catch: java.lang.Throwable -> L9a
            if (r7 != 0) goto L17
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L9a
            return r0
        L17:
            r2 = -1
            r3 = 1
            if (r8 == 0) goto L40
            int r8 = r7.stableCount     // Catch: java.lang.Throwable -> L9a
            if (r8 != 0) goto L21
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L9a
            return r0
        L21:
            int r8 = r7.stableCount     // Catch: java.lang.Throwable -> L9a
            int r8 = r8 - r3
            r7.stableCount = r8     // Catch: java.lang.Throwable -> L9a
            int r8 = r7.stableCount     // Catch: java.lang.Throwable -> L9a
            if (r8 != 0) goto L64
            int r8 = r7.unstableCount     // Catch: java.lang.Throwable -> L9a
            if (r8 != 0) goto L30
            r8 = r3
            goto L31
        L30:
            r8 = r0
        L31:
            android.app.IActivityManager r4 = android.app.ActivityManagerNative.getDefault()     // Catch: android.os.RemoteException -> L63 java.lang.Throwable -> L9a
            android.app.IActivityManager$ContentProviderHolder r5 = r7.holder     // Catch: android.os.RemoteException -> L63 java.lang.Throwable -> L9a
            android.os.IBinder r5 = r5.connection     // Catch: android.os.RemoteException -> L63 java.lang.Throwable -> L9a
            if (r8 == 0) goto L3c
            r0 = r3
        L3c:
            r4.refContentProvider(r5, r2, r0)     // Catch: android.os.RemoteException -> L63 java.lang.Throwable -> L9a
            goto L63
        L40:
            int r8 = r7.unstableCount     // Catch: java.lang.Throwable -> L9a
            if (r8 != 0) goto L46
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L9a
            return r0
        L46:
            int r8 = r7.unstableCount     // Catch: java.lang.Throwable -> L9a
            int r8 = r8 - r3
            r7.unstableCount = r8     // Catch: java.lang.Throwable -> L9a
            int r8 = r7.unstableCount     // Catch: java.lang.Throwable -> L9a
            if (r8 != 0) goto L64
            int r8 = r7.stableCount     // Catch: java.lang.Throwable -> L9a
            if (r8 != 0) goto L55
            r8 = r3
            goto L56
        L55:
            r8 = r0
        L56:
            if (r8 != 0) goto L63
            android.app.IActivityManager r4 = android.app.ActivityManagerNative.getDefault()     // Catch: android.os.RemoteException -> L63 java.lang.Throwable -> L9a
            android.app.IActivityManager$ContentProviderHolder r5 = r7.holder     // Catch: android.os.RemoteException -> L63 java.lang.Throwable -> L9a
            android.os.IBinder r5 = r5.connection     // Catch: android.os.RemoteException -> L63 java.lang.Throwable -> L9a
            r4.refContentProvider(r5, r0, r2)     // Catch: android.os.RemoteException -> L63 java.lang.Throwable -> L9a
        L63:
            r0 = r8
        L64:
            if (r0 == 0) goto L98
            boolean r8 = r7.removePending     // Catch: java.lang.Throwable -> L9a
            if (r8 != 0) goto L7a
            r7.removePending = r3     // Catch: java.lang.Throwable -> L9a
            android.app.ActivityThread$H r8 = r6.mH     // Catch: java.lang.Throwable -> L9a
            r0 = 131(0x83, float:1.84E-43)
            android.os.Message r7 = r8.obtainMessage(r0, r7)     // Catch: java.lang.Throwable -> L9a
            android.app.ActivityThread$H r8 = r6.mH     // Catch: java.lang.Throwable -> L9a
            r8.sendMessage(r7)     // Catch: java.lang.Throwable -> L9a
            goto L98
        L7a:
            java.lang.String r8 = "ActivityThread"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L9a
            r0.<init>()     // Catch: java.lang.Throwable -> L9a
            java.lang.String r2 = "Duplicate remove pending of provider "
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch: java.lang.Throwable -> L9a
            android.app.IActivityManager$ContentProviderHolder r7 = r7.holder     // Catch: java.lang.Throwable -> L9a
            android.content.pm.ProviderInfo r7 = r7.info     // Catch: java.lang.Throwable -> L9a
            java.lang.String r7 = r7.name     // Catch: java.lang.Throwable -> L9a
            java.lang.StringBuilder r7 = r0.append(r7)     // Catch: java.lang.Throwable -> L9a
            java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> L9a
            android.util.Slog.w(r8, r7)     // Catch: java.lang.Throwable -> L9a
        L98:
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L9a
            return r3
        L9a:
            r7 = move-exception
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L9a
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.ActivityThread.releaseProvider(android.content.IContentProvider, boolean):boolean");
    }

    final void completeRemoveProvider(ProviderRefCount providerRefCount) {
        synchronized (this.mProviderMap) {
            if (providerRefCount.removePending) {
                providerRefCount.removePending = false;
                IBinder iBinderAsBinder = providerRefCount.holder.provider.asBinder();
                if (this.mProviderRefCountMap.get(iBinderAsBinder) == providerRefCount) {
                    this.mProviderRefCountMap.remove(iBinderAsBinder);
                }
                for (int size = this.mProviderMap.size() - 1; size >= 0; size--) {
                    if (this.mProviderMap.valueAt(size).mProvider.asBinder() == iBinderAsBinder) {
                        this.mProviderMap.removeAt(size);
                    }
                }
                try {
                    ActivityManagerNative.getDefault().removeContentProvider(providerRefCount.holder.connection, false);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    final void handleUnstableProviderDied(IBinder iBinder, boolean z) {
        synchronized (this.mProviderMap) {
            handleUnstableProviderDiedLocked(iBinder, z);
        }
    }

    final void handleUnstableProviderDiedLocked(IBinder iBinder, boolean z) {
        ProviderRefCount providerRefCount = this.mProviderRefCountMap.get(iBinder);
        if (providerRefCount != null) {
            this.mProviderRefCountMap.remove(iBinder);
            for (int size = this.mProviderMap.size() - 1; size >= 0; size--) {
                ProviderClientRecord providerClientRecordValueAt = this.mProviderMap.valueAt(size);
                if (providerClientRecordValueAt != null && providerClientRecordValueAt.mProvider.asBinder() == iBinder) {
                    Slog.i(TAG, "Removing dead content provider:" + providerClientRecordValueAt.mProvider.toString());
                    this.mProviderMap.removeAt(size);
                }
            }
            if (z) {
                try {
                    ActivityManagerNative.getDefault().unstableProviderDied(providerRefCount.holder.connection);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    final void appNotRespondingViaProvider(IBinder iBinder) {
        synchronized (this.mProviderMap) {
            ProviderRefCount providerRefCount = this.mProviderRefCountMap.get(iBinder);
            if (providerRefCount != null) {
                try {
                    ActivityManagerNative.getDefault().appNotRespondingViaProvider(providerRefCount.holder.connection);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    private ProviderClientRecord installProviderAuthoritiesLocked(IContentProvider iContentProvider, ContentProvider contentProvider, IActivityManager.ContentProviderHolder contentProviderHolder) {
        String[] strArrSplit = PATTERN_SEMICOLON.split(contentProviderHolder.info.authority);
        int userId = UserHandle.getUserId(contentProviderHolder.info.applicationInfo.uid);
        ProviderClientRecord providerClientRecord = new ProviderClientRecord(strArrSplit, iContentProvider, contentProvider, contentProviderHolder);
        for (String str : strArrSplit) {
            ProviderKey providerKey = new ProviderKey(str, userId);
            if (this.mProviderMap.get(providerKey) != null) {
                Slog.w(TAG, "Content provider " + providerClientRecord.mHolder.info.name + " already published as " + str);
            } else {
                this.mProviderMap.put(providerKey, providerClientRecord);
            }
        }
        return providerClientRecord;
    }

    private IActivityManager.ContentProviderHolder installProvider(Context context, IActivityManager.ContentProviderHolder contentProviderHolder, ProviderInfo providerInfo, boolean z, boolean z2, boolean z3) {
        IContentProvider iContentProvider;
        IActivityManager.ContentProviderHolder contentProviderHolder2;
        ContentProvider contentProvider = null;
        if (contentProviderHolder == null || contentProviderHolder.provider == null) {
            if (z) {
                Slog.d(TAG, "Loading provider " + providerInfo.authority + ": " + providerInfo.name);
            }
            ApplicationInfo applicationInfo = providerInfo.applicationInfo;
            if (!context.getPackageName().equals(applicationInfo.packageName)) {
                Application application = this.mInitialApplication;
                if (application != null && application.getPackageName().equals(applicationInfo.packageName)) {
                    context = this.mInitialApplication;
                } else {
                    try {
                        context = context.createPackageContext(applicationInfo.packageName, 1);
                    } catch (PackageManager.NameNotFoundException unused) {
                        context = null;
                    }
                }
            }
            if (context == null) {
                Slog.w(TAG, "Unable to get context for package " + applicationInfo.packageName + " while loading content provider " + providerInfo.name);
                return null;
            }
            try {
                ContentProvider contentProvider2 = (ContentProvider) context.getClassLoader().loadClass(providerInfo.name).newInstance();
                IContentProvider iContentProvider2 = contentProvider2.getIContentProvider();
                if (iContentProvider2 == null) {
                    Slog.e(TAG, "Failed to instantiate class " + providerInfo.name + " from sourceDir " + providerInfo.applicationInfo.sourceDir);
                    return null;
                }
                contentProvider2.attachInfo(context, providerInfo);
                contentProvider = contentProvider2;
                iContentProvider = iContentProvider2;
            } catch (Exception e) {
                if (this.mInstrumentation.onException(null, e)) {
                    return null;
                }
                throw new RuntimeException("Unable to get provider " + providerInfo.name + ": " + e.toString(), e);
            }
        } else {
            iContentProvider = contentProviderHolder.provider;
        }
        synchronized (this.mProviderMap) {
            IBinder iBinderAsBinder = iContentProvider.asBinder();
            if (contentProvider != null) {
                ComponentName componentName = new ComponentName(providerInfo.packageName, providerInfo.name);
                ProviderClientRecord providerClientRecordInstallProviderAuthoritiesLocked = this.mLocalProvidersByName.get(componentName);
                if (providerClientRecordInstallProviderAuthoritiesLocked != null) {
                    IContentProvider iContentProvider3 = providerClientRecordInstallProviderAuthoritiesLocked.mProvider;
                } else {
                    IActivityManager.ContentProviderHolder contentProviderHolder3 = new IActivityManager.ContentProviderHolder(providerInfo);
                    contentProviderHolder3.provider = iContentProvider;
                    contentProviderHolder3.noReleaseNeeded = true;
                    providerClientRecordInstallProviderAuthoritiesLocked = installProviderAuthoritiesLocked(iContentProvider, contentProvider, contentProviderHolder3);
                    this.mLocalProviders.put(iBinderAsBinder, providerClientRecordInstallProviderAuthoritiesLocked);
                    this.mLocalProvidersByName.put(componentName, providerClientRecordInstallProviderAuthoritiesLocked);
                }
                contentProviderHolder2 = providerClientRecordInstallProviderAuthoritiesLocked.mHolder;
            } else {
                ProviderRefCount providerRefCount = this.mProviderRefCountMap.get(iBinderAsBinder);
                if (providerRefCount == null) {
                    ProviderClientRecord providerClientRecordInstallProviderAuthoritiesLocked2 = installProviderAuthoritiesLocked(iContentProvider, contentProvider, contentProviderHolder);
                    if (z2) {
                        providerRefCount = new ProviderRefCount(contentProviderHolder, providerClientRecordInstallProviderAuthoritiesLocked2, 1000, 1000);
                    } else {
                        providerRefCount = z3 ? new ProviderRefCount(contentProviderHolder, providerClientRecordInstallProviderAuthoritiesLocked2, 1, 0) : new ProviderRefCount(contentProviderHolder, providerClientRecordInstallProviderAuthoritiesLocked2, 0, 1);
                    }
                    this.mProviderRefCountMap.put(iBinderAsBinder, providerRefCount);
                } else if (!z2) {
                    incProviderRefLocked(providerRefCount, z3);
                    try {
                        ActivityManagerNative.getDefault().removeContentProvider(contentProviderHolder.connection, z3);
                    } catch (RemoteException unused2) {
                    }
                }
                contentProviderHolder2 = providerRefCount.holder;
            }
        }
        return contentProviderHolder2;
    }

    private void attach(boolean z) {
        sCurrentActivityThread = this;
        this.mSystemThread = z;
        if (!z) {
            ViewRootImpl.addFirstDrawHandler(new Runnable() { // from class: android.app.ActivityThread.1
                @Override // java.lang.Runnable
                public void run() {
                    ActivityThread.this.ensureJitEnabled();
                }
            });
            DdmHandleAppName.setAppName("<pre-initialized>", UserHandle.myUserId());
            RuntimeInit.setApplicationObject(this.mAppThread.asBinder());
            try {
                ActivityManagerNative.getDefault().attachApplication(this.mAppThread);
            } catch (RemoteException unused) {
            }
        } else {
            DdmHandleAppName.setAppName("system_process", UserHandle.myUserId());
            try {
                this.mInstrumentation = new Instrumentation();
                ContextImpl contextImpl = new ContextImpl();
                contextImpl.init(getSystemContext().mPackageInfo, (IBinder) null, this);
                Application applicationNewApplication = Instrumentation.newApplication(Application.class, contextImpl);
                this.mAllApplications.add(applicationNewApplication);
                this.mInitialApplication = applicationNewApplication;
                applicationNewApplication.onCreate();
            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate Application():" + e.toString(), e);
            }
        }
        DropBox.setReporter(new DropBoxReporter());
        ViewRootImpl.addConfigCallback(new ComponentCallbacks2() { // from class: android.app.ActivityThread.2
            @Override // android.content.ComponentCallbacks
            public void onLowMemory() {
            }

            @Override // android.content.ComponentCallbacks2
            public void onTrimMemory(int i) {
            }

            @Override // android.content.ComponentCallbacks
            public void onConfigurationChanged(Configuration configuration) {
                synchronized (ActivityThread.this.mResourcesManager) {
                    if (ActivityThread.this.mResourcesManager.applyConfigurationToResourcesLocked(configuration, null) && (ActivityThread.this.mPendingConfiguration == null || ActivityThread.this.mPendingConfiguration.isOtherSeqNewer(configuration))) {
                        ActivityThread.this.mPendingConfiguration = configuration;
                        ActivityThread.this.sendMessage(118, configuration);
                    }
                }
            }
        });
    }

    public static ActivityThread systemMain() {
        HardwareRenderer.disable(true);
        ActivityThread activityThread = new ActivityThread();
        activityThread.attach(true);
        return activityThread;
    }

    public final void installSystemProviders(List<ProviderInfo> list) {
        if (list != null) {
            installContentProviders(this.mInitialApplication, list);
        }
    }

    public int getIntCoreSetting(String str, int i) {
        synchronized (this.mResourcesManager) {
            Bundle bundle = this.mCoreSettings;
            if (bundle == null) {
                return i;
            }
            return bundle.getInt(str, i);
        }
    }

    private static class EventLoggingReporter implements EventLogger.Reporter {
        private EventLoggingReporter() {
        }

        public void report(int i, Object... objArr) {
            EventLog.writeEvent(i, objArr);
        }
    }

    private class DropBoxReporter implements DropBox.Reporter {
        private DropBoxManager dropBox;

        public DropBoxReporter() {
            this.dropBox = (DropBoxManager) ActivityThread.this.getSystemContext().getSystemService(Context.DROPBOX_SERVICE);
        }

        public void addData(String str, byte[] bArr, int i) {
            this.dropBox.addData(str, bArr, i);
        }

        public void addText(String str, String str2) {
            this.dropBox.addText(str, str2);
        }
    }

    public static void main(String[] strArr) {
        SamplingProfilerIntegration.start();
        CloseGuard.setEnabled(false);
        Environment.initForCurrentUser();
        EventLogger.setReporter(new EventLoggingReporter());
        Security.addProvider(new AndroidKeyStoreProvider());
        Process.setArgV0("<pre-initialized>");
        Looper.prepareMainLooper();
        ActivityThread activityThread = new ActivityThread();
        activityThread.attach(false);
        if (sMainThreadHandler == null) {
            sMainThreadHandler = activityThread.getHandler();
        }
        AsyncTask.init();
        Looper.loop();
        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
}
