package android.app;

import android.app.IServiceConnection;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.Trace;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.DisplayAdjustments;
import com.android.internal.util.ArrayUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class LoadedApk {
    private static final String TAG = "LoadedApk";
    private final ActivityThread mActivityThread;
    private final String mAppDir;
    private Application mApplication;
    private final ApplicationInfo mApplicationInfo;
    private final ClassLoader mBaseClassLoader;
    private ClassLoader mClassLoader;
    int mClientCount;
    private final String mDataDir;
    private final File mDataDirFile;
    private final DisplayAdjustments mDisplayAdjustments;
    private final boolean mIncludeCode;
    private final String mLibDir;
    final String mPackageName;
    private final ArrayMap<Context, ArrayMap<BroadcastReceiver, ReceiverDispatcher>> mReceivers;
    private final String mResDir;
    Resources mResources;
    private final boolean mSecurityViolation;
    private final ArrayMap<Context, ArrayMap<ServiceConnection, ServiceDispatcher>> mServices;
    private final String[] mSharedLibraries;
    private final ArrayMap<Context, ArrayMap<ServiceConnection, ServiceDispatcher>> mUnboundServices;
    private final ArrayMap<Context, ArrayMap<BroadcastReceiver, ReceiverDispatcher>> mUnregisteredReceivers;

    Application getApplication() {
        return this.mApplication;
    }

    public LoadedApk(ActivityThread activityThread, ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo, ActivityThread activityThread2, ClassLoader classLoader, boolean z, boolean z2) {
        DisplayAdjustments displayAdjustments = new DisplayAdjustments();
        this.mDisplayAdjustments = displayAdjustments;
        this.mReceivers = new ArrayMap<>();
        this.mUnregisteredReceivers = new ArrayMap<>();
        this.mServices = new ArrayMap<>();
        this.mUnboundServices = new ArrayMap<>();
        this.mClientCount = 0;
        this.mActivityThread = activityThread;
        this.mApplicationInfo = applicationInfo;
        String str = applicationInfo.packageName;
        this.mPackageName = str;
        String str2 = applicationInfo.sourceDir;
        this.mAppDir = str2;
        int iMyUid = Process.myUid();
        this.mResDir = applicationInfo.uid == iMyUid ? applicationInfo.sourceDir : applicationInfo.publicSourceDir;
        if (!UserHandle.isSameUser(applicationInfo.uid, iMyUid) && !Process.isIsolated()) {
            applicationInfo.dataDir = PackageManager.getDataDirForUser(UserHandle.getUserId(iMyUid), str);
        }
        this.mSharedLibraries = applicationInfo.sharedLibraryFiles;
        String str3 = applicationInfo.dataDir;
        this.mDataDir = str3;
        this.mDataDirFile = str3 != null ? new File(str3) : null;
        this.mLibDir = applicationInfo.nativeLibraryDir;
        this.mBaseClassLoader = classLoader;
        this.mSecurityViolation = z;
        this.mIncludeCode = z2;
        displayAdjustments.setCompatibilityInfo(compatibilityInfo);
        if (str2 == null) {
            if (ActivityThread.mSystemContext == null) {
                ActivityThread.mSystemContext = ContextImpl.createSystemContext(activityThread2);
                ResourcesManager resourcesManager = ResourcesManager.getInstance();
                ActivityThread.mSystemContext.getResources().updateConfiguration(resourcesManager.getConfiguration(), resourcesManager.getDisplayMetricsLocked(0, displayAdjustments), compatibilityInfo);
            }
            this.mClassLoader = ActivityThread.mSystemContext.getClassLoader();
            this.mResources = ActivityThread.mSystemContext.getResources();
        }
    }

    public LoadedApk(ActivityThread activityThread, String str, Context context, ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo) {
        DisplayAdjustments displayAdjustments = new DisplayAdjustments();
        this.mDisplayAdjustments = displayAdjustments;
        this.mReceivers = new ArrayMap<>();
        this.mUnregisteredReceivers = new ArrayMap<>();
        this.mServices = new ArrayMap<>();
        this.mUnboundServices = new ArrayMap<>();
        this.mClientCount = 0;
        this.mActivityThread = activityThread;
        applicationInfo = applicationInfo == null ? new ApplicationInfo() : applicationInfo;
        this.mApplicationInfo = applicationInfo;
        applicationInfo.packageName = str;
        this.mPackageName = str;
        this.mAppDir = null;
        this.mResDir = null;
        this.mSharedLibraries = null;
        this.mDataDir = null;
        this.mDataDirFile = null;
        this.mLibDir = null;
        this.mBaseClassLoader = null;
        this.mSecurityViolation = false;
        this.mIncludeCode = true;
        this.mClassLoader = context.getClassLoader();
        this.mResources = context.getResources();
        displayAdjustments.setCompatibilityInfo(compatibilityInfo);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public ApplicationInfo getApplicationInfo() {
        return this.mApplicationInfo;
    }

    public boolean isSecurityViolation() {
        return this.mSecurityViolation;
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mDisplayAdjustments.getCompatibilityInfo();
    }

    public void setCompatibilityInfo(CompatibilityInfo compatibilityInfo) {
        this.mDisplayAdjustments.setCompatibilityInfo(compatibilityInfo);
    }

    private static String[] getLibrariesFor(String str) {
        try {
            ApplicationInfo applicationInfo = ActivityThread.getPackageManager().getApplicationInfo(str, 1024, UserHandle.myUserId());
            if (applicationInfo == null) {
                return null;
            }
            return applicationInfo.sharedLibraryFiles;
        } catch (RemoteException e) {
            throw new AssertionError(e);
        }
    }

    private static String combineLibs(String[] strArr, String[] strArr2) {
        StringBuilder sb = new StringBuilder(300);
        boolean z = true;
        if (strArr != null) {
            for (String str : strArr) {
                if (z) {
                    z = false;
                } else {
                    sb.append(':');
                }
                sb.append(str);
            }
        }
        boolean z2 = !z;
        if (strArr2 != null) {
            for (String str2 : strArr2) {
                if (!z2 || !ArrayUtils.contains(strArr, str2)) {
                    if (z) {
                        z = false;
                    } else {
                        sb.append(':');
                    }
                    sb.append(str2);
                }
            }
        }
        return sb.toString();
    }

    public ClassLoader getClassLoader() {
        synchronized (this) {
            ClassLoader classLoader = this.mClassLoader;
            if (classLoader != null) {
                return classLoader;
            }
            if (this.mIncludeCode && !this.mPackageName.equals("android")) {
                String str = this.mAppDir;
                String str2 = this.mLibDir;
                String str3 = this.mActivityThread.mInstrumentationAppDir;
                String str4 = this.mActivityThread.mInstrumentationAppLibraryDir;
                String str5 = this.mActivityThread.mInstrumentationAppPackage;
                String str6 = this.mActivityThread.mInstrumentedAppDir;
                String str7 = this.mActivityThread.mInstrumentedAppLibraryDir;
                String[] librariesFor = null;
                if (this.mAppDir.equals(str3) || this.mAppDir.equals(str6)) {
                    str = str3 + ":" + str6;
                    str2 = str4 + ":" + str7;
                    if (!str6.equals(str3)) {
                        librariesFor = getLibrariesFor(str5);
                    }
                }
                if (this.mSharedLibraries != null || librariesFor != null) {
                    str = combineLibs(this.mSharedLibraries, librariesFor) + ':' + str;
                }
                StrictMode.ThreadPolicy threadPolicyAllowThreadDiskReads = StrictMode.allowThreadDiskReads();
                this.mClassLoader = ApplicationLoaders.getDefault().getClassLoader(str, str2, this.mBaseClassLoader);
                initializeJavaContextClassLoader();
                StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskReads);
            } else {
                ClassLoader classLoader2 = this.mBaseClassLoader;
                if (classLoader2 == null) {
                    this.mClassLoader = ClassLoader.getSystemClassLoader();
                } else {
                    this.mClassLoader = classLoader2;
                }
            }
            return this.mClassLoader;
        }
    }

    private void initializeJavaContextClassLoader() {
        try {
            PackageInfo packageInfo = ActivityThread.getPackageManager().getPackageInfo(this.mPackageName, 0, UserHandle.myUserId());
            if (packageInfo == null) {
                throw new IllegalStateException("Unable to get package info for " + this.mPackageName + "; is package not installed?");
            }
            Thread.currentThread().setContextClassLoader((packageInfo.sharedUserId != null) || (packageInfo.applicationInfo != null && !this.mPackageName.equals(packageInfo.applicationInfo.processName)) ? new WarningContextClassLoader() : this.mClassLoader);
        } catch (RemoteException e) {
            throw new IllegalStateException("Unable to get package info for " + this.mPackageName + "; is system dying?", e);
        }
    }

    private static class WarningContextClassLoader extends ClassLoader {
        private static boolean warned = false;

        private WarningContextClassLoader() {
        }

        private void warn(String str) {
            if (warned) {
                return;
            }
            warned = true;
            Thread.currentThread().setContextClassLoader(getParent());
            Slog.w(ActivityThread.TAG, "ClassLoader." + str + ": The class loader returned by Thread.getContextClassLoader() may fail for processes that host multiple applications. You should explicitly specify a context class loader. For example: Thread.setContextClassLoader(getClass().getClassLoader());");
        }

        @Override // java.lang.ClassLoader
        public URL getResource(String str) {
            warn("getResource");
            return getParent().getResource(str);
        }

        @Override // java.lang.ClassLoader
        public Enumeration<URL> getResources(String str) throws IOException {
            warn("getResources");
            return getParent().getResources(str);
        }

        @Override // java.lang.ClassLoader
        public InputStream getResourceAsStream(String str) {
            warn("getResourceAsStream");
            return getParent().getResourceAsStream(str);
        }

        @Override // java.lang.ClassLoader
        public Class<?> loadClass(String str) throws ClassNotFoundException {
            warn("loadClass");
            return getParent().loadClass(str);
        }

        @Override // java.lang.ClassLoader
        public void setClassAssertionStatus(String str, boolean z) {
            warn("setClassAssertionStatus");
            getParent().setClassAssertionStatus(str, z);
        }

        @Override // java.lang.ClassLoader
        public void setPackageAssertionStatus(String str, boolean z) {
            warn("setPackageAssertionStatus");
            getParent().setPackageAssertionStatus(str, z);
        }

        @Override // java.lang.ClassLoader
        public void setDefaultAssertionStatus(boolean z) {
            warn("setDefaultAssertionStatus");
            getParent().setDefaultAssertionStatus(z);
        }

        @Override // java.lang.ClassLoader
        public void clearAssertionStatus() {
            warn("clearAssertionStatus");
            getParent().clearAssertionStatus();
        }
    }

    public String getAppDir() {
        return this.mAppDir;
    }

    public String getLibDir() {
        return this.mLibDir;
    }

    public String getResDir() {
        return this.mResDir;
    }

    public String getDataDir() {
        return this.mDataDir;
    }

    public File getDataDirFile() {
        return this.mDataDirFile;
    }

    public AssetManager getAssets(ActivityThread activityThread) {
        return getResources(activityThread).getAssets();
    }

    public Resources getResources(ActivityThread activityThread) {
        if (this.mResources == null) {
            this.mResources = activityThread.getTopLevelResources(this.mResDir, 0, null, this);
        }
        return this.mResources;
    }

    public Application makeApplication(boolean z, Instrumentation instrumentation) {
        Application application = this.mApplication;
        if (application != null) {
            return application;
        }
        String str = this.mApplicationInfo.className;
        if (z || str == null) {
            str = "android.app.Application";
        }
        Application applicationNewApplication = null;
        try {
            ClassLoader classLoader = getClassLoader();
            ContextImpl contextImpl = new ContextImpl();
            contextImpl.init(this, (IBinder) null, this.mActivityThread);
            applicationNewApplication = this.mActivityThread.mInstrumentation.newApplication(classLoader, str, contextImpl);
            contextImpl.setOuterContext(applicationNewApplication);
        } catch (Exception e) {
            if (!this.mActivityThread.mInstrumentation.onException(applicationNewApplication, e)) {
                throw new RuntimeException("Unable to instantiate application " + str + ": " + e.toString(), e);
            }
        }
        this.mActivityThread.mAllApplications.add(applicationNewApplication);
        this.mApplication = applicationNewApplication;
        if (instrumentation != null) {
            try {
                instrumentation.callApplicationOnCreate(applicationNewApplication);
            } catch (Exception e2) {
                if (!instrumentation.onException(applicationNewApplication, e2)) {
                    throw new RuntimeException("Unable to create application " + applicationNewApplication.getClass().getName() + ": " + e2.toString(), e2);
                }
            }
        }
        return applicationNewApplication;
    }

    public void removeContextRegistrations(Context context, String str, String str2) {
        boolean zVmRegistrationLeaksEnabled = StrictMode.vmRegistrationLeaksEnabled();
        ArrayMap<BroadcastReceiver, ReceiverDispatcher> arrayMapRemove = this.mReceivers.remove(context);
        if (arrayMapRemove != null) {
            for (int i = 0; i < arrayMapRemove.size(); i++) {
                ReceiverDispatcher receiverDispatcherValueAt = arrayMapRemove.valueAt(i);
                IntentReceiverLeaked intentReceiverLeaked = new IntentReceiverLeaked(str2 + " " + str + " has leaked IntentReceiver " + receiverDispatcherValueAt.getIntentReceiver() + " that was originally registered here. Are you missing a call to unregisterReceiver()?");
                intentReceiverLeaked.setStackTrace(receiverDispatcherValueAt.getLocation().getStackTrace());
                Slog.e(ActivityThread.TAG, intentReceiverLeaked.getMessage(), intentReceiverLeaked);
                if (zVmRegistrationLeaksEnabled) {
                    StrictMode.onIntentReceiverLeaked(intentReceiverLeaked);
                }
                try {
                    ActivityManagerNative.getDefault().unregisterReceiver(receiverDispatcherValueAt.getIIntentReceiver());
                } catch (RemoteException unused) {
                }
            }
        }
        this.mUnregisteredReceivers.remove(context);
        ArrayMap<ServiceConnection, ServiceDispatcher> arrayMapRemove2 = this.mServices.remove(context);
        if (arrayMapRemove2 != null) {
            for (int i2 = 0; i2 < arrayMapRemove2.size(); i2++) {
                ServiceDispatcher serviceDispatcherValueAt = arrayMapRemove2.valueAt(i2);
                ServiceConnectionLeaked serviceConnectionLeaked = new ServiceConnectionLeaked(str2 + " " + str + " has leaked ServiceConnection " + serviceDispatcherValueAt.getServiceConnection() + " that was originally bound here");
                serviceConnectionLeaked.setStackTrace(serviceDispatcherValueAt.getLocation().getStackTrace());
                Slog.e(ActivityThread.TAG, serviceConnectionLeaked.getMessage(), serviceConnectionLeaked);
                if (zVmRegistrationLeaksEnabled) {
                    StrictMode.onServiceConnectionLeaked(serviceConnectionLeaked);
                }
                try {
                    ActivityManagerNative.getDefault().unbindService(serviceDispatcherValueAt.getIServiceConnection());
                } catch (RemoteException unused2) {
                }
                serviceDispatcherValueAt.doForget();
            }
        }
        this.mUnboundServices.remove(context);
    }

    public IIntentReceiver getReceiverDispatcher(BroadcastReceiver broadcastReceiver, Context context, Handler handler, Instrumentation instrumentation, boolean z) {
        ArrayMap<BroadcastReceiver, ReceiverDispatcher> arrayMap;
        IIntentReceiver iIntentReceiver;
        synchronized (this.mReceivers) {
            ReceiverDispatcher receiverDispatcher = null;
            if (z) {
                try {
                    arrayMap = this.mReceivers.get(context);
                    if (arrayMap != null) {
                        receiverDispatcher = arrayMap.get(broadcastReceiver);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                arrayMap = null;
            }
            if (receiverDispatcher == null) {
                receiverDispatcher = new ReceiverDispatcher(broadcastReceiver, context, handler, instrumentation, z);
                if (z) {
                    if (arrayMap == null) {
                        arrayMap = new ArrayMap<>();
                        this.mReceivers.put(context, arrayMap);
                    }
                    arrayMap.put(broadcastReceiver, receiverDispatcher);
                }
            } else {
                receiverDispatcher.validate(context, handler);
            }
            receiverDispatcher.mForgotten = false;
            iIntentReceiver = receiverDispatcher.getIIntentReceiver();
        }
        return iIntentReceiver;
    }

    public IIntentReceiver forgetReceiverDispatcher(Context context, BroadcastReceiver broadcastReceiver) {
        ReceiverDispatcher receiverDispatcher;
        ReceiverDispatcher receiverDispatcher2;
        IIntentReceiver iIntentReceiver;
        synchronized (this.mReceivers) {
            ArrayMap<BroadcastReceiver, ReceiverDispatcher> arrayMap = this.mReceivers.get(context);
            if (arrayMap != null && (receiverDispatcher2 = arrayMap.get(broadcastReceiver)) != null) {
                arrayMap.remove(broadcastReceiver);
                if (arrayMap.size() == 0) {
                    this.mReceivers.remove(context);
                }
                if (broadcastReceiver.getDebugUnregister()) {
                    ArrayMap<BroadcastReceiver, ReceiverDispatcher> arrayMap2 = this.mUnregisteredReceivers.get(context);
                    if (arrayMap2 == null) {
                        arrayMap2 = new ArrayMap<>();
                        this.mUnregisteredReceivers.put(context, arrayMap2);
                    }
                    IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Originally unregistered here:");
                    illegalArgumentException.fillInStackTrace();
                    receiverDispatcher2.setUnregisterLocation(illegalArgumentException);
                    arrayMap2.put(broadcastReceiver, receiverDispatcher2);
                }
                receiverDispatcher2.mForgotten = true;
                iIntentReceiver = receiverDispatcher2.getIIntentReceiver();
            } else {
                ArrayMap<BroadcastReceiver, ReceiverDispatcher> arrayMap3 = this.mUnregisteredReceivers.get(context);
                if (arrayMap3 != null && (receiverDispatcher = arrayMap3.get(broadcastReceiver)) != null) {
                    throw new IllegalArgumentException("Unregistering Receiver " + broadcastReceiver + " that was already unregistered", receiverDispatcher.getUnregisterLocation());
                }
                if (context == null) {
                    throw new IllegalStateException("Unbinding Receiver " + broadcastReceiver + " from Context that is no longer in use: " + context);
                }
                throw new IllegalArgumentException("Receiver not registered: " + broadcastReceiver);
            }
        }
        return iIntentReceiver;
    }

    static final class ReceiverDispatcher {
        final Handler mActivityThread;
        final Context mContext;
        boolean mForgotten;
        final IIntentReceiver.Stub mIIntentReceiver;
        final Instrumentation mInstrumentation;
        final IntentReceiverLeaked mLocation;
        final BroadcastReceiver mReceiver;
        final boolean mRegistered;
        RuntimeException mUnregisterLocation;

        static final class InnerReceiver extends IIntentReceiver.Stub {
            final WeakReference<ReceiverDispatcher> mDispatcher;
            final ReceiverDispatcher mStrongRef;

            InnerReceiver(ReceiverDispatcher receiverDispatcher, boolean z) {
                this.mDispatcher = new WeakReference<>(receiverDispatcher);
                this.mStrongRef = z ? receiverDispatcher : null;
            }

            @Override // android.content.IIntentReceiver
            public void performReceive(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2) {
                ReceiverDispatcher receiverDispatcher = this.mDispatcher.get();
                if (receiverDispatcher != null) {
                    receiverDispatcher.performReceive(intent, i, str, bundle, z, z2, i2);
                    return;
                }
                IActivityManager iActivityManager = ActivityManagerNative.getDefault();
                if (bundle != null) {
                    try {
                        bundle.setAllowFds(false);
                    } catch (RemoteException unused) {
                        Slog.w(ActivityThread.TAG, "Couldn't finish broadcast to unregistered receiver");
                        return;
                    }
                }
                iActivityManager.finishReceiver(this, i, str, bundle, false);
            }
        }

        final class Args extends BroadcastReceiver.PendingResult implements Runnable {
            private Intent mCurIntent;
            private final boolean mOrdered;

            public Args(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2) {
                super(i, str, bundle, ReceiverDispatcher.this.mRegistered ? 1 : 2, z, z2, ReceiverDispatcher.this.mIIntentReceiver.asBinder(), i2);
                this.mCurIntent = intent;
                this.mOrdered = z;
            }

            @Override // java.lang.Runnable
            public void run() {
                BroadcastReceiver broadcastReceiver = ReceiverDispatcher.this.mReceiver;
                boolean z = this.mOrdered;
                IActivityManager iActivityManager = ActivityManagerNative.getDefault();
                Intent intent = this.mCurIntent;
                this.mCurIntent = null;
                if (broadcastReceiver == null || ReceiverDispatcher.this.mForgotten) {
                    if (ReceiverDispatcher.this.mRegistered && z) {
                        sendFinished(iActivityManager);
                        return;
                    }
                    return;
                }
                Trace.traceBegin(64L, "broadcastReceiveReg");
                try {
                    ClassLoader classLoader = ReceiverDispatcher.this.mReceiver.getClass().getClassLoader();
                    intent.setExtrasClassLoader(classLoader);
                    setExtrasClassLoader(classLoader);
                    broadcastReceiver.setPendingResult(this);
                    broadcastReceiver.onReceive(ReceiverDispatcher.this.mContext, intent);
                } catch (Exception e) {
                    if (ReceiverDispatcher.this.mRegistered && z) {
                        sendFinished(iActivityManager);
                    }
                    if (ReceiverDispatcher.this.mInstrumentation == null || !ReceiverDispatcher.this.mInstrumentation.onException(ReceiverDispatcher.this.mReceiver, e)) {
                        Trace.traceEnd(64L);
                        throw new RuntimeException("Error receiving broadcast " + intent + " in " + ReceiverDispatcher.this.mReceiver, e);
                    }
                }
                if (broadcastReceiver.getPendingResult() != null) {
                    finish();
                }
                Trace.traceEnd(64L);
            }
        }

        ReceiverDispatcher(BroadcastReceiver broadcastReceiver, Context context, Handler handler, Instrumentation instrumentation, boolean z) {
            Objects.requireNonNull(handler, "Handler must not be null");
            this.mIIntentReceiver = new InnerReceiver(this, !z);
            this.mReceiver = broadcastReceiver;
            this.mContext = context;
            this.mActivityThread = handler;
            this.mInstrumentation = instrumentation;
            this.mRegistered = z;
            IntentReceiverLeaked intentReceiverLeaked = new IntentReceiverLeaked(null);
            this.mLocation = intentReceiverLeaked;
            intentReceiverLeaked.fillInStackTrace();
        }

        void validate(Context context, Handler handler) {
            if (this.mContext != context) {
                throw new IllegalStateException("Receiver " + this.mReceiver + " registered with differing Context (was " + this.mContext + " now " + context + ")");
            }
            if (this.mActivityThread != handler) {
                throw new IllegalStateException("Receiver " + this.mReceiver + " registered with differing handler (was " + this.mActivityThread + " now " + handler + ")");
            }
        }

        IntentReceiverLeaked getLocation() {
            return this.mLocation;
        }

        BroadcastReceiver getIntentReceiver() {
            return this.mReceiver;
        }

        IIntentReceiver getIIntentReceiver() {
            return this.mIIntentReceiver;
        }

        void setUnregisterLocation(RuntimeException runtimeException) {
            this.mUnregisterLocation = runtimeException;
        }

        RuntimeException getUnregisterLocation() {
            return this.mUnregisterLocation;
        }

        public void performReceive(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2) {
            Args args = new Args(intent, i, str, bundle, z, z2, i2);
            if (!this.mActivityThread.post(args) && this.mRegistered && z) {
                args.sendFinished(ActivityManagerNative.getDefault());
            }
        }
    }

    public final IServiceConnection getServiceDispatcher(ServiceConnection serviceConnection, Context context, Handler handler, int i) {
        IServiceConnection iServiceConnection;
        synchronized (this.mServices) {
            ArrayMap<ServiceConnection, ServiceDispatcher> arrayMap = this.mServices.get(context);
            ServiceDispatcher serviceDispatcher = arrayMap != null ? arrayMap.get(serviceConnection) : null;
            if (serviceDispatcher == null) {
                serviceDispatcher = new ServiceDispatcher(serviceConnection, context, handler, i);
                if (arrayMap == null) {
                    arrayMap = new ArrayMap<>();
                    this.mServices.put(context, arrayMap);
                }
                arrayMap.put(serviceConnection, serviceDispatcher);
            } else {
                serviceDispatcher.validate(context, handler);
            }
            iServiceConnection = serviceDispatcher.getIServiceConnection();
        }
        return iServiceConnection;
    }

    public final IServiceConnection forgetServiceDispatcher(Context context, ServiceConnection serviceConnection) {
        ServiceDispatcher serviceDispatcher;
        ServiceDispatcher serviceDispatcher2;
        IServiceConnection iServiceConnection;
        synchronized (this.mServices) {
            ArrayMap<ServiceConnection, ServiceDispatcher> arrayMap = this.mServices.get(context);
            if (arrayMap != null && (serviceDispatcher2 = arrayMap.get(serviceConnection)) != null) {
                arrayMap.remove(serviceConnection);
                serviceDispatcher2.doForget();
                if (arrayMap.size() == 0) {
                    this.mServices.remove(context);
                }
                if ((serviceDispatcher2.getFlags() & 2) != 0) {
                    ArrayMap<ServiceConnection, ServiceDispatcher> arrayMap2 = this.mUnboundServices.get(context);
                    if (arrayMap2 == null) {
                        arrayMap2 = new ArrayMap<>();
                        this.mUnboundServices.put(context, arrayMap2);
                    }
                    IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Originally unbound here:");
                    illegalArgumentException.fillInStackTrace();
                    serviceDispatcher2.setUnbindLocation(illegalArgumentException);
                    arrayMap2.put(serviceConnection, serviceDispatcher2);
                }
                iServiceConnection = serviceDispatcher2.getIServiceConnection();
            } else {
                ArrayMap<ServiceConnection, ServiceDispatcher> arrayMap3 = this.mUnboundServices.get(context);
                if (arrayMap3 != null && (serviceDispatcher = arrayMap3.get(serviceConnection)) != null) {
                    throw new IllegalArgumentException("Unbinding Service " + serviceConnection + " that was already unbound", serviceDispatcher.getUnbindLocation());
                }
                if (context == null) {
                    throw new IllegalStateException("Unbinding Service " + serviceConnection + " from Context that is no longer in use: " + context);
                }
                throw new IllegalArgumentException("Service not registered: " + serviceConnection);
            }
        }
        return iServiceConnection;
    }

    static final class ServiceDispatcher {
        private final Handler mActivityThread;
        private final ServiceConnection mConnection;
        private final Context mContext;
        private boolean mDied;
        private final int mFlags;
        private boolean mForgotten;
        private final ServiceConnectionLeaked mLocation;
        private RuntimeException mUnbindLocation;
        private final ArrayMap<ComponentName, ConnectionInfo> mActiveConnections = new ArrayMap<>();
        private final InnerConnection mIServiceConnection = new InnerConnection(this);

        private static class ConnectionInfo {
            IBinder binder;
            IBinder.DeathRecipient deathMonitor;

            private ConnectionInfo() {
            }
        }

        private static class InnerConnection extends IServiceConnection.Stub {
            final WeakReference<ServiceDispatcher> mDispatcher;

            InnerConnection(ServiceDispatcher serviceDispatcher) {
                this.mDispatcher = new WeakReference<>(serviceDispatcher);
            }

            @Override // android.app.IServiceConnection
            public void connected(ComponentName componentName, IBinder iBinder) throws RemoteException {
                ServiceDispatcher serviceDispatcher = this.mDispatcher.get();
                if (serviceDispatcher != null) {
                    serviceDispatcher.connected(componentName, iBinder);
                }
            }
        }

        ServiceDispatcher(ServiceConnection serviceConnection, Context context, Handler handler, int i) {
            this.mConnection = serviceConnection;
            this.mContext = context;
            this.mActivityThread = handler;
            ServiceConnectionLeaked serviceConnectionLeaked = new ServiceConnectionLeaked(null);
            this.mLocation = serviceConnectionLeaked;
            serviceConnectionLeaked.fillInStackTrace();
            this.mFlags = i;
        }

        void validate(Context context, Handler handler) {
            if (this.mContext != context) {
                throw new RuntimeException("ServiceConnection " + this.mConnection + " registered with differing Context (was " + this.mContext + " now " + context + ")");
            }
            if (this.mActivityThread != handler) {
                throw new RuntimeException("ServiceConnection " + this.mConnection + " registered with differing handler (was " + this.mActivityThread + " now " + handler + ")");
            }
        }

        void doForget() {
            synchronized (this) {
                for (int i = 0; i < this.mActiveConnections.size(); i++) {
                    ConnectionInfo connectionInfoValueAt = this.mActiveConnections.valueAt(i);
                    connectionInfoValueAt.binder.unlinkToDeath(connectionInfoValueAt.deathMonitor, 0);
                }
                this.mActiveConnections.clear();
                this.mForgotten = true;
            }
        }

        ServiceConnectionLeaked getLocation() {
            return this.mLocation;
        }

        ServiceConnection getServiceConnection() {
            return this.mConnection;
        }

        IServiceConnection getIServiceConnection() {
            return this.mIServiceConnection;
        }

        int getFlags() {
            return this.mFlags;
        }

        void setUnbindLocation(RuntimeException runtimeException) {
            this.mUnbindLocation = runtimeException;
        }

        RuntimeException getUnbindLocation() {
            return this.mUnbindLocation;
        }

        public void connected(ComponentName componentName, IBinder iBinder) {
            Handler handler = this.mActivityThread;
            if (handler != null) {
                handler.post(new RunConnection(componentName, iBinder, 0));
            } else {
                doConnected(componentName, iBinder);
            }
        }

        public void death(ComponentName componentName, IBinder iBinder) {
            synchronized (this) {
                this.mDied = true;
                ConnectionInfo connectionInfoRemove = this.mActiveConnections.remove(componentName);
                if (connectionInfoRemove != null && connectionInfoRemove.binder == iBinder) {
                    connectionInfoRemove.binder.unlinkToDeath(connectionInfoRemove.deathMonitor, 0);
                    Handler handler = this.mActivityThread;
                    if (handler != null) {
                        handler.post(new RunConnection(componentName, iBinder, 1));
                    } else {
                        doDeath(componentName, iBinder);
                    }
                }
            }
        }

        public void doConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (this) {
                if (this.mForgotten) {
                    return;
                }
                ConnectionInfo connectionInfo = this.mActiveConnections.get(componentName);
                if (connectionInfo == null || connectionInfo.binder != iBinder) {
                    if (iBinder != null) {
                        this.mDied = false;
                        ConnectionInfo connectionInfo2 = new ConnectionInfo();
                        connectionInfo2.binder = iBinder;
                        connectionInfo2.deathMonitor = new DeathMonitor(componentName, iBinder);
                        try {
                            iBinder.linkToDeath(connectionInfo2.deathMonitor, 0);
                            this.mActiveConnections.put(componentName, connectionInfo2);
                        } catch (RemoteException unused) {
                            this.mActiveConnections.remove(componentName);
                            return;
                        }
                    } else {
                        this.mActiveConnections.remove(componentName);
                    }
                    if (connectionInfo != null) {
                        connectionInfo.binder.unlinkToDeath(connectionInfo.deathMonitor, 0);
                    }
                    if (connectionInfo != null) {
                        this.mConnection.onServiceDisconnected(componentName);
                    }
                    if (iBinder != null) {
                        this.mConnection.onServiceConnected(componentName, iBinder);
                    }
                }
            }
        }

        public void doDeath(ComponentName componentName, IBinder iBinder) {
            this.mConnection.onServiceDisconnected(componentName);
        }

        private final class RunConnection implements Runnable {
            final int mCommand;
            final ComponentName mName;
            final IBinder mService;

            RunConnection(ComponentName componentName, IBinder iBinder, int i) {
                this.mName = componentName;
                this.mService = iBinder;
                this.mCommand = i;
            }

            @Override // java.lang.Runnable
            public void run() {
                int i = this.mCommand;
                if (i == 0) {
                    ServiceDispatcher.this.doConnected(this.mName, this.mService);
                } else if (i == 1) {
                    ServiceDispatcher.this.doDeath(this.mName, this.mService);
                }
            }
        }

        private final class DeathMonitor implements IBinder.DeathRecipient {
            final ComponentName mName;
            final IBinder mService;

            DeathMonitor(ComponentName componentName, IBinder iBinder) {
                this.mName = componentName;
                this.mService = iBinder;
            }

            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                ServiceDispatcher.this.death(this.mName, this.mService);
            }
        }
    }
}
