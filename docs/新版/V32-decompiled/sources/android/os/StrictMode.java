package android.os;

import android.animation.ValueAnimator;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.ApplicationErrorReport;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.LinkQualityInfo;
import android.os.MessageQueue;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Printer;
import android.util.Singleton;
import android.view.IWindowManager;
import com.android.internal.os.RuntimeInit;
import com.android.internal.util.FastPrintWriter;
import dalvik.system.BlockGuard;
import dalvik.system.CloseGuard;
import dalvik.system.VMDebug;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public final class StrictMode {
    private static final int ALL_THREAD_DETECT_BITS = 15;
    private static final int ALL_VM_DETECT_BITS = 32256;
    public static final int DETECT_CUSTOM = 8;
    public static final int DETECT_DISK_READ = 2;
    public static final int DETECT_DISK_WRITE = 1;
    public static final int DETECT_NETWORK = 4;
    public static final int DETECT_VM_ACTIVITY_LEAKS = 2048;
    public static final int DETECT_VM_CLOSABLE_LEAKS = 1024;
    public static final int DETECT_VM_CURSOR_LEAKS = 512;
    private static final int DETECT_VM_FILE_URI_EXPOSURE = 16384;
    private static final int DETECT_VM_INSTANCE_LEAKS = 4096;
    public static final int DETECT_VM_REGISTRATION_LEAKS = 8192;
    public static final String DISABLE_PROPERTY = "persist.sys.strictmode.disable";
    private static final int MAX_OFFENSES_PER_LOOP = 10;
    private static final int MAX_SPAN_TAGS = 20;
    private static final long MIN_DIALOG_INTERVAL_MS = 30000;
    private static final long MIN_LOG_INTERVAL_MS = 1000;
    public static final int PENALTY_DEATH = 64;
    public static final int PENALTY_DEATH_ON_NETWORK = 512;
    public static final int PENALTY_DIALOG = 32;
    public static final int PENALTY_DROPBOX = 128;
    public static final int PENALTY_FLASH = 2048;
    public static final int PENALTY_GATHER = 256;
    public static final int PENALTY_LOG = 16;
    private static final int THREAD_PENALTY_MASK = 3056;
    public static final String VISUAL_PROPERTY = "persist.sys.strictmode.visual";
    private static final int VM_PENALTY_MASK = 208;
    private static final String TAG = "StrictMode";
    private static final boolean LOG_V = Log.isLoggable(TAG, 2);
    private static final boolean IS_USER_BUILD = Context.USER_SERVICE.equals(Build.TYPE);
    private static final boolean IS_ENG_BUILD = "eng".equals(Build.TYPE);
    private static final HashMap<Class, Integer> EMPTY_CLASS_LIMIT_MAP = new HashMap<>();
    private static volatile int sVmPolicyMask = 0;
    private static volatile VmPolicy sVmPolicy = VmPolicy.LAX;
    private static final AtomicInteger sDropboxCallsInFlight = new AtomicInteger(0);
    private static final ThreadLocal<ArrayList<ViolationInfo>> gatheredViolations = new ThreadLocal<ArrayList<ViolationInfo>>() { // from class: android.os.StrictMode.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public ArrayList<ViolationInfo> initialValue() {
            return null;
        }
    };
    private static final ThreadLocal<ArrayList<ViolationInfo>> violationsBeingTimed = new ThreadLocal<ArrayList<ViolationInfo>>() { // from class: android.os.StrictMode.2
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public ArrayList<ViolationInfo> initialValue() {
            return new ArrayList<>();
        }
    };
    private static final ThreadLocal<Handler> threadHandler = new ThreadLocal<Handler>() { // from class: android.os.StrictMode.3
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Handler initialValue() {
            return new Handler();
        }
    };
    private static final ThreadLocal<AndroidBlockGuardPolicy> threadAndroidPolicy = new ThreadLocal<AndroidBlockGuardPolicy>() { // from class: android.os.StrictMode.4
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public AndroidBlockGuardPolicy initialValue() {
            return new AndroidBlockGuardPolicy(0);
        }
    };
    private static long sLastInstanceCountCheckMillis = 0;
    private static boolean sIsIdlerRegistered = false;
    private static final MessageQueue.IdleHandler sProcessIdleHandler = new MessageQueue.IdleHandler() { // from class: android.os.StrictMode.6
        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            long jUptimeMillis = SystemClock.uptimeMillis();
            if (jUptimeMillis - StrictMode.sLastInstanceCountCheckMillis <= StrictMode.MIN_DIALOG_INTERVAL_MS) {
                return true;
            }
            long unused = StrictMode.sLastInstanceCountCheckMillis = jUptimeMillis;
            StrictMode.conditionallyCheckInstanceCounts();
            return true;
        }
    };
    private static final HashMap<Integer, Long> sLastVmViolationTime = new HashMap<>();
    private static final Span NO_OP_SPAN = new Span() { // from class: android.os.StrictMode.7
        @Override // android.os.StrictMode.Span
        public void finish() {
        }
    };
    private static final ThreadLocal<ThreadSpanState> sThisThreadSpanState = new ThreadLocal<ThreadSpanState>() { // from class: android.os.StrictMode.8
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public ThreadSpanState initialValue() {
            return new ThreadSpanState();
        }
    };
    private static Singleton<IWindowManager> sWindowManager = new Singleton<IWindowManager>() { // from class: android.os.StrictMode.9
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public IWindowManager create() {
            return IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
        }
    };
    private static final HashMap<Class, Integer> sExpectedActivityInstanceCount = new HashMap<>();

    private StrictMode() {
    }

    public static final class ThreadPolicy {
        public static final ThreadPolicy LAX = new ThreadPolicy(0);
        final int mask;

        private ThreadPolicy(int i) {
            this.mask = i;
        }

        public String toString() {
            return "[StrictMode.ThreadPolicy; mask=" + this.mask + "]";
        }

        public static final class Builder {
            private int mMask;

            public Builder() {
                this.mMask = 0;
                this.mMask = 0;
            }

            public Builder(ThreadPolicy threadPolicy) {
                this.mMask = 0;
                this.mMask = threadPolicy.mask;
            }

            public Builder detectAll() {
                return enable(15);
            }

            public Builder permitAll() {
                return disable(15);
            }

            public Builder detectNetwork() {
                return enable(4);
            }

            public Builder permitNetwork() {
                return disable(4);
            }

            public Builder detectDiskReads() {
                return enable(2);
            }

            public Builder permitDiskReads() {
                return disable(2);
            }

            public Builder detectCustomSlowCalls() {
                return enable(8);
            }

            public Builder permitCustomSlowCalls() {
                return disable(8);
            }

            public Builder detectDiskWrites() {
                return enable(1);
            }

            public Builder permitDiskWrites() {
                return disable(1);
            }

            public Builder penaltyDialog() {
                return enable(32);
            }

            public Builder penaltyDeath() {
                return enable(64);
            }

            public Builder penaltyDeathOnNetwork() {
                return enable(512);
            }

            public Builder penaltyFlashScreen() {
                return enable(2048);
            }

            public Builder penaltyLog() {
                return enable(16);
            }

            public Builder penaltyDropBox() {
                return enable(128);
            }

            private Builder enable(int i) {
                this.mMask = i | this.mMask;
                return this;
            }

            private Builder disable(int i) {
                this.mMask = (~i) & this.mMask;
                return this;
            }

            public ThreadPolicy build() {
                int i = this.mMask;
                if (i != 0 && (i & 240) == 0) {
                    penaltyLog();
                }
                return new ThreadPolicy(this.mMask);
            }
        }
    }

    public static final class VmPolicy {
        public static final VmPolicy LAX = new VmPolicy(0, StrictMode.EMPTY_CLASS_LIMIT_MAP);
        final HashMap<Class, Integer> classInstanceLimit;
        final int mask;

        private VmPolicy(int i, HashMap<Class, Integer> map) {
            Objects.requireNonNull(map, "classInstanceLimit == null");
            this.mask = i;
            this.classInstanceLimit = map;
        }

        public String toString() {
            return "[StrictMode.VmPolicy; mask=" + this.mask + "]";
        }

        public static final class Builder {
            private HashMap<Class, Integer> mClassInstanceLimit;
            private boolean mClassInstanceLimitNeedCow;
            private int mMask;

            public Builder() {
                this.mClassInstanceLimitNeedCow = false;
                this.mMask = 0;
            }

            public Builder(VmPolicy vmPolicy) {
                this.mClassInstanceLimitNeedCow = false;
                this.mMask = vmPolicy.mask;
                this.mClassInstanceLimitNeedCow = true;
                this.mClassInstanceLimit = vmPolicy.classInstanceLimit;
            }

            public Builder setClassInstanceLimit(Class cls, int i) {
                Objects.requireNonNull(cls, "klass == null");
                if (this.mClassInstanceLimitNeedCow) {
                    if (this.mClassInstanceLimit.containsKey(cls) && this.mClassInstanceLimit.get(cls).intValue() == i) {
                        return this;
                    }
                    this.mClassInstanceLimitNeedCow = false;
                    this.mClassInstanceLimit = (HashMap) this.mClassInstanceLimit.clone();
                } else if (this.mClassInstanceLimit == null) {
                    this.mClassInstanceLimit = new HashMap<>();
                }
                this.mMask |= 4096;
                this.mClassInstanceLimit.put(cls, Integer.valueOf(i));
                return this;
            }

            public Builder detectActivityLeaks() {
                return enable(2048);
            }

            public Builder detectAll() {
                return enable(28160);
            }

            public Builder detectLeakedSqlLiteObjects() {
                return enable(512);
            }

            public Builder detectLeakedClosableObjects() {
                return enable(1024);
            }

            public Builder detectLeakedRegistrationObjects() {
                return enable(8192);
            }

            public Builder detectFileUriExposure() {
                return enable(16384);
            }

            public Builder penaltyDeath() {
                return enable(64);
            }

            public Builder penaltyLog() {
                return enable(16);
            }

            public Builder penaltyDropBox() {
                return enable(128);
            }

            private Builder enable(int i) {
                this.mMask = i | this.mMask;
                return this;
            }

            public VmPolicy build() {
                int i = this.mMask;
                if (i != 0 && (i & 240) == 0) {
                    penaltyLog();
                }
                int i2 = this.mMask;
                HashMap<Class, Integer> map = this.mClassInstanceLimit;
                if (map == null) {
                    map = StrictMode.EMPTY_CLASS_LIMIT_MAP;
                }
                return new VmPolicy(i2, map);
            }
        }
    }

    public static void setThreadPolicy(ThreadPolicy threadPolicy) {
        setThreadPolicyMask(threadPolicy.mask);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setThreadPolicyMask(int i) {
        setBlockGuardPolicy(i);
        Binder.setThreadStrictModePolicy(i);
    }

    private static void setBlockGuardPolicy(int i) {
        AndroidBlockGuardPolicy androidBlockGuardPolicy;
        if (i == 0) {
            BlockGuard.setThreadPolicy(BlockGuard.LAX_POLICY);
            return;
        }
        BlockGuard.Policy threadPolicy = BlockGuard.getThreadPolicy();
        if (threadPolicy instanceof AndroidBlockGuardPolicy) {
            androidBlockGuardPolicy = (AndroidBlockGuardPolicy) threadPolicy;
        } else {
            androidBlockGuardPolicy = threadAndroidPolicy.get();
            BlockGuard.setThreadPolicy(androidBlockGuardPolicy);
        }
        androidBlockGuardPolicy.setPolicyMask(i);
    }

    private static void setCloseGuardEnabled(boolean z) {
        if (!(CloseGuard.getReporter() instanceof AndroidCloseGuardReporter)) {
            CloseGuard.setReporter(new AndroidCloseGuardReporter());
        }
        CloseGuard.setEnabled(z);
    }

    public static class StrictModeViolation extends BlockGuard.BlockGuardPolicyException {
        public StrictModeViolation(int i, int i2, String str) {
            super(i, i2, str);
        }
    }

    public static class StrictModeNetworkViolation extends StrictModeViolation {
        public StrictModeNetworkViolation(int i) {
            super(i, 4, null);
        }
    }

    private static class StrictModeDiskReadViolation extends StrictModeViolation {
        public StrictModeDiskReadViolation(int i) {
            super(i, 2, null);
        }
    }

    private static class StrictModeDiskWriteViolation extends StrictModeViolation {
        public StrictModeDiskWriteViolation(int i) {
            super(i, 1, null);
        }
    }

    private static class StrictModeCustomViolation extends StrictModeViolation {
        public StrictModeCustomViolation(int i, String str) {
            super(i, 8, str);
        }
    }

    public static int getThreadPolicyMask() {
        return BlockGuard.getThreadPolicy().getPolicyMask();
    }

    public static ThreadPolicy getThreadPolicy() {
        return new ThreadPolicy(getThreadPolicyMask());
    }

    public static ThreadPolicy allowThreadDiskWrites() {
        int threadPolicyMask = getThreadPolicyMask();
        int i = threadPolicyMask & (-4);
        if (i != threadPolicyMask) {
            setThreadPolicyMask(i);
        }
        return new ThreadPolicy(threadPolicyMask);
    }

    public static ThreadPolicy allowThreadDiskReads() {
        int threadPolicyMask = getThreadPolicyMask();
        int i = threadPolicyMask & (-3);
        if (i != threadPolicyMask) {
            setThreadPolicyMask(i);
        }
        return new ThreadPolicy(threadPolicyMask);
    }

    private static boolean amTheSystemServerProcess() {
        if (Process.myUid() != 1000) {
            return false;
        }
        Throwable th = new Throwable();
        th.fillInStackTrace();
        for (StackTraceElement stackTraceElement : th.getStackTrace()) {
            String className = stackTraceElement.getClassName();
            if (className != null && className.startsWith("com.android.server.")) {
                return true;
            }
        }
        return false;
    }

    public static boolean conditionallyEnableDebugLogging() {
        boolean z = SystemProperties.getBoolean(VISUAL_PROPERTY, false) && !amTheSystemServerProcess();
        boolean z2 = SystemProperties.getBoolean(DISABLE_PROPERTY, false);
        if (!z && (IS_USER_BUILD || z2)) {
            setCloseGuardEnabled(false);
            return false;
        }
        boolean z3 = IS_ENG_BUILD;
        if (z3) {
            z = true;
        }
        boolean z4 = IS_USER_BUILD;
        int i = z4 ? 7 : 135;
        if (z) {
            i |= 2048;
        }
        setThreadPolicyMask(i);
        if (z4) {
            setCloseGuardEnabled(false);
        } else {
            VmPolicy.Builder builderPenaltyDropBox = new VmPolicy.Builder().detectAll().penaltyDropBox();
            if (z3) {
                builderPenaltyDropBox.penaltyLog();
            }
            setVmPolicy(builderPenaltyDropBox.build());
            setCloseGuardEnabled(vmClosableObjectLeaksEnabled());
        }
        return true;
    }

    public static void enableDeathOnNetwork() {
        setThreadPolicyMask(getThreadPolicyMask() | 4 | 512);
    }

    private static int parsePolicyFromMessage(String str) {
        int iIndexOf;
        if (str == null || !str.startsWith("policy=") || (iIndexOf = str.indexOf(32)) == -1) {
            return 0;
        }
        try {
            return Integer.valueOf(str.substring(7, iIndexOf)).intValue();
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int parseViolationFromMessage(String str) {
        int iIndexOf;
        if (str == null || (iIndexOf = str.indexOf("violation=")) == -1) {
            return 0;
        }
        int i = iIndexOf + 10;
        int iIndexOf2 = str.indexOf(32, i);
        if (iIndexOf2 == -1) {
            iIndexOf2 = str.length();
        }
        try {
            return Integer.valueOf(str.substring(i, iIndexOf2)).intValue();
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean tooManyViolationsThisLoop() {
        return violationsBeingTimed.get().size() >= 10;
    }

    private static class AndroidBlockGuardPolicy implements BlockGuard.Policy {
        private ArrayMap<Integer, Long> mLastViolationTime;
        private int mPolicyMask;

        public AndroidBlockGuardPolicy(int i) {
            this.mPolicyMask = i;
        }

        public String toString() {
            return "AndroidBlockGuardPolicy; mPolicyMask=" + this.mPolicyMask;
        }

        public int getPolicyMask() {
            return this.mPolicyMask;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
        public void onWriteToDisk() throws BlockGuard.BlockGuardPolicyException {
            if ((this.mPolicyMask & 1) == 0 || StrictMode.tooManyViolationsThisLoop()) {
                return;
            }
            StrictModeDiskWriteViolation strictModeDiskWriteViolation = new StrictModeDiskWriteViolation(this.mPolicyMask);
            strictModeDiskWriteViolation.fillInStackTrace();
            startHandlingViolationException(strictModeDiskWriteViolation);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
        void onCustomSlowCall(String str) throws BlockGuard.BlockGuardPolicyException {
            if ((this.mPolicyMask & 8) == 0 || StrictMode.tooManyViolationsThisLoop()) {
                return;
            }
            StrictModeCustomViolation strictModeCustomViolation = new StrictModeCustomViolation(this.mPolicyMask, str);
            strictModeCustomViolation.fillInStackTrace();
            startHandlingViolationException(strictModeCustomViolation);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
        public void onReadFromDisk() throws BlockGuard.BlockGuardPolicyException {
            if ((this.mPolicyMask & 2) == 0 || StrictMode.tooManyViolationsThisLoop()) {
                return;
            }
            StrictModeDiskReadViolation strictModeDiskReadViolation = new StrictModeDiskReadViolation(this.mPolicyMask);
            strictModeDiskReadViolation.fillInStackTrace();
            startHandlingViolationException(strictModeDiskReadViolation);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
        public void onNetwork() throws BlockGuard.BlockGuardPolicyException {
            int i = this.mPolicyMask;
            if ((i & 4) == 0) {
                return;
            }
            if ((i & 512) == 0) {
                if (StrictMode.tooManyViolationsThisLoop()) {
                    return;
                }
                StrictModeNetworkViolation strictModeNetworkViolation = new StrictModeNetworkViolation(this.mPolicyMask);
                strictModeNetworkViolation.fillInStackTrace();
                startHandlingViolationException(strictModeNetworkViolation);
                return;
            }
            throw new NetworkOnMainThreadException();
        }

        public void setPolicyMask(int i) {
            this.mPolicyMask = i;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
        void startHandlingViolationException(BlockGuard.BlockGuardPolicyException blockGuardPolicyException) throws BlockGuard.BlockGuardPolicyException {
            ViolationInfo violationInfo = new ViolationInfo((Throwable) blockGuardPolicyException, blockGuardPolicyException.getPolicy());
            violationInfo.violationUptimeMillis = SystemClock.uptimeMillis();
            handleViolationWithTimingAttempt(violationInfo);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
        void handleViolationWithTimingAttempt(ViolationInfo violationInfo) throws BlockGuard.BlockGuardPolicyException {
            if (Looper.myLooper() != null && (violationInfo.policy & 3056) != 64) {
                final ArrayList arrayList = (ArrayList) StrictMode.violationsBeingTimed.get();
                if (arrayList.size() >= 10) {
                    return;
                }
                arrayList.add(violationInfo);
                if (arrayList.size() > 1) {
                    return;
                }
                final IWindowManager iWindowManager = (violationInfo.policy & 2048) != 0 ? (IWindowManager) StrictMode.sWindowManager.get() : null;
                if (iWindowManager != null) {
                    try {
                        iWindowManager.showStrictModeViolation(true);
                    } catch (RemoteException unused) {
                    }
                }
                ((Handler) StrictMode.threadHandler.get()).postAtFrontOfQueue(new Runnable() { // from class: android.os.StrictMode.AndroidBlockGuardPolicy.1
                    /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
                    @Override // java.lang.Runnable
                    public void run() throws BlockGuard.BlockGuardPolicyException {
                        long jUptimeMillis = SystemClock.uptimeMillis();
                        IWindowManager iWindowManager2 = iWindowManager;
                        int i = 0;
                        if (iWindowManager2 != null) {
                            try {
                                iWindowManager2.showStrictModeViolation(false);
                            } catch (RemoteException unused2) {
                            }
                        }
                        while (i < arrayList.size()) {
                            ViolationInfo violationInfo2 = (ViolationInfo) arrayList.get(i);
                            i++;
                            violationInfo2.violationNumThisLoop = i;
                            violationInfo2.durationMillis = (int) (jUptimeMillis - violationInfo2.violationUptimeMillis);
                            AndroidBlockGuardPolicy.this.handleViolation(violationInfo2);
                        }
                        arrayList.clear();
                    }
                });
                return;
            }
            violationInfo.durationMillis = -1;
            handleViolation(violationInfo);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
        /* JADX WARN: Removed duplicated region for block: B:37:0x00ad  */
        /* JADX WARN: Removed duplicated region for block: B:38:0x00b3  */
        /* JADX WARN: Removed duplicated region for block: B:45:0x00c6  */
        /* JADX WARN: Removed duplicated region for block: B:46:0x00ed  */
        /* JADX WARN: Removed duplicated region for block: B:52:0x0116  */
        /* JADX WARN: Removed duplicated region for block: B:58:0x0124  */
        /* JADX WARN: Removed duplicated region for block: B:76:0x0164  */
        /* JADX WARN: Removed duplicated region for block: B:87:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        void handleViolation(android.os.StrictMode.ViolationInfo r12) throws dalvik.system.BlockGuard.BlockGuardPolicyException {
            /*
                Method dump skipped, instruction units count: 367
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.StrictMode.AndroidBlockGuardPolicy.handleViolation(android.os.StrictMode$ViolationInfo):void");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
    public static void executeDeathPenalty(ViolationInfo violationInfo) throws BlockGuard.BlockGuardPolicyException {
        throw new StrictModeViolation(violationInfo.policy, parseViolationFromMessage(violationInfo.crashInfo.exceptionMessage), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v2, types: [android.os.StrictMode$5] */
    public static void dropboxViolationAsync(final int i, final ViolationInfo violationInfo) {
        AtomicInteger atomicInteger = sDropboxCallsInFlight;
        int iIncrementAndGet = atomicInteger.incrementAndGet();
        if (iIncrementAndGet > 20) {
            atomicInteger.decrementAndGet();
            return;
        }
        if (LOG_V) {
            Log.d(TAG, "Dropboxing async; in-flight=" + iIncrementAndGet);
        }
        new Thread("callActivityManagerForStrictModeDropbox") { // from class: android.os.StrictMode.5
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Process.setThreadPriority(10);
                try {
                    IActivityManager iActivityManager = ActivityManagerNative.getDefault();
                    if (iActivityManager == null) {
                        Log.d(StrictMode.TAG, "No activity manager; failed to Dropbox violation.");
                    } else {
                        iActivityManager.handleApplicationStrictModeViolation(RuntimeInit.getApplicationObject(), i, violationInfo);
                    }
                } catch (RemoteException e) {
                    Log.e(StrictMode.TAG, "RemoteException handling StrictMode violation", e);
                }
                int iDecrementAndGet = StrictMode.sDropboxCallsInFlight.decrementAndGet();
                if (StrictMode.LOG_V) {
                    Log.d(StrictMode.TAG, "Dropbox complete; in-flight=" + iDecrementAndGet);
                }
            }
        }.start();
    }

    private static class AndroidCloseGuardReporter implements CloseGuard.Reporter {
        private AndroidCloseGuardReporter() {
        }

        public void report(String str, Throwable th) {
            StrictMode.onVmPolicyViolation(str, th);
        }
    }

    static boolean hasGatheredViolations() {
        return gatheredViolations.get() != null;
    }

    static void clearGatheredViolations() {
        gatheredViolations.set(null);
    }

    public static void conditionallyCheckInstanceCounts() {
        VmPolicy vmPolicy = getVmPolicy();
        if (vmPolicy.classInstanceLimit.size() == 0) {
            return;
        }
        Runtime.getRuntime().gc();
        for (Map.Entry<Class, Integer> entry : vmPolicy.classInstanceLimit.entrySet()) {
            Class key = entry.getKey();
            int iIntValue = entry.getValue().intValue();
            long jCountInstancesOfClass = VMDebug.countInstancesOfClass(key, false);
            if (jCountInstancesOfClass > iIntValue) {
                InstanceCountViolation instanceCountViolation = new InstanceCountViolation(key, jCountInstancesOfClass, iIntValue);
                onVmPolicyViolation(instanceCountViolation.getMessage(), instanceCountViolation);
            }
        }
    }

    public static void setVmPolicy(VmPolicy vmPolicy) {
        synchronized (StrictMode.class) {
            sVmPolicy = vmPolicy;
            sVmPolicyMask = vmPolicy.mask;
            setCloseGuardEnabled(vmClosableObjectLeaksEnabled());
            Looper mainLooper = Looper.getMainLooper();
            if (mainLooper != null) {
                MessageQueue messageQueue = mainLooper.mQueue;
                if (vmPolicy.classInstanceLimit.size() == 0 || (sVmPolicyMask & 208) == 0) {
                    messageQueue.removeIdleHandler(sProcessIdleHandler);
                    sIsIdlerRegistered = false;
                } else if (!sIsIdlerRegistered) {
                    messageQueue.addIdleHandler(sProcessIdleHandler);
                    sIsIdlerRegistered = true;
                }
            }
        }
    }

    public static VmPolicy getVmPolicy() {
        VmPolicy vmPolicy;
        synchronized (StrictMode.class) {
            vmPolicy = sVmPolicy;
        }
        return vmPolicy;
    }

    public static void enableDefaults() {
        setThreadPolicy(new ThreadPolicy.Builder().detectAll().penaltyLog().build());
        setVmPolicy(new VmPolicy.Builder().detectAll().penaltyLog().build());
    }

    public static boolean vmSqliteObjectLeaksEnabled() {
        return (sVmPolicyMask & 512) != 0;
    }

    public static boolean vmClosableObjectLeaksEnabled() {
        return (sVmPolicyMask & 1024) != 0;
    }

    public static boolean vmRegistrationLeaksEnabled() {
        return (sVmPolicyMask & 8192) != 0;
    }

    public static boolean vmFileUriExposureEnabled() {
        return (sVmPolicyMask & 16384) != 0;
    }

    public static void onSqliteObjectLeaked(String str, Throwable th) {
        onVmPolicyViolation(str, th);
    }

    public static void onWebViewMethodCalledOnWrongThread(Throwable th) {
        onVmPolicyViolation(null, th);
    }

    public static void onIntentReceiverLeaked(Throwable th) {
        onVmPolicyViolation(null, th);
    }

    public static void onServiceConnectionLeaked(Throwable th) {
        onVmPolicyViolation(null, th);
    }

    public static void onFileUriExposed(String str) {
        String str2 = "file:// Uri exposed through " + str;
        onVmPolicyViolation(str2, new Throwable(str2));
    }

    public static void onVmPolicyViolation(String str, Throwable th) {
        long j;
        boolean z = (sVmPolicyMask & 128) != 0;
        boolean z2 = (sVmPolicyMask & 64) != 0;
        boolean z3 = (sVmPolicyMask & 16) != 0;
        ViolationInfo violationInfo = new ViolationInfo(th, sVmPolicyMask);
        violationInfo.numAnimationsRunning = 0;
        violationInfo.tags = null;
        violationInfo.broadcastIntentAction = null;
        Integer numValueOf = Integer.valueOf(violationInfo.hashCode());
        long jUptimeMillis = SystemClock.uptimeMillis();
        long j2 = LinkQualityInfo.UNKNOWN_LONG;
        HashMap<Integer, Long> map = sLastVmViolationTime;
        synchronized (map) {
            if (map.containsKey(numValueOf)) {
                long jLongValue = map.get(numValueOf).longValue();
                j2 = jUptimeMillis - jLongValue;
                j = jLongValue;
            } else {
                j = 0;
            }
            if (j2 > 1000) {
                map.put(numValueOf, Long.valueOf(jUptimeMillis));
            }
        }
        if (z3 && j2 > 1000) {
            Log.e(TAG, str, th);
        }
        int i = (sVmPolicyMask & ALL_VM_DETECT_BITS) | 128;
        if (z && !z2) {
            dropboxViolationAsync(i, violationInfo);
            return;
        }
        if (z && j == 0) {
            int threadPolicyMask = getThreadPolicyMask();
            try {
                try {
                    setThreadPolicyMask(0);
                    ActivityManagerNative.getDefault().handleApplicationStrictModeViolation(RuntimeInit.getApplicationObject(), i, violationInfo);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException trying to handle StrictMode violation", e);
                }
            } finally {
                setThreadPolicyMask(threadPolicyMask);
            }
        }
        if (z2) {
            System.err.println("StrictMode VmPolicy violation with POLICY_DEATH; shutting down.");
            Process.killProcess(Process.myPid());
            System.exit(10);
        }
    }

    static void writeGatheredViolationsToParcel(Parcel parcel) {
        ArrayList<ViolationInfo> arrayList = gatheredViolations.get();
        if (arrayList == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(arrayList.size());
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList.get(i).writeToParcel(parcel, 0);
            }
            if (LOG_V) {
                Log.d(TAG, "wrote violations to response parcel; num=" + arrayList.size());
            }
            arrayList.clear();
        }
        gatheredViolations.set(null);
    }

    private static class LogStackTrace extends Exception {
        private LogStackTrace() {
        }
    }

    static void readAndHandleBinderCallViolations(Parcel parcel) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter fastPrintWriter = new FastPrintWriter(stringWriter, false, 256);
        new LogStackTrace().printStackTrace(fastPrintWriter);
        fastPrintWriter.flush();
        String string = stringWriter.toString();
        boolean z = (getThreadPolicyMask() & 256) != 0;
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            if (LOG_V) {
                Log.d(TAG, "strict mode violation stacks read from binder call.  i=" + i2);
            }
            ViolationInfo violationInfo = new ViolationInfo(parcel, !z);
            StringBuilder sb = new StringBuilder();
            ApplicationErrorReport.CrashInfo crashInfo = violationInfo.crashInfo;
            crashInfo.stackTrace = sb.append(crashInfo.stackTrace).append("# via Binder call with stack:\n").append(string).toString();
            BlockGuard.Policy threadPolicy = BlockGuard.getThreadPolicy();
            if (threadPolicy instanceof AndroidBlockGuardPolicy) {
                ((AndroidBlockGuardPolicy) threadPolicy).handleViolationWithTimingAttempt(violationInfo);
            }
        }
    }

    private static void onBinderStrictModePolicyChange(int i) {
        setBlockGuardPolicy(i);
    }

    public static class Span {
        private final ThreadSpanState mContainerState;
        private long mCreateMillis;
        private String mName;
        private Span mNext;
        private Span mPrev;

        Span(ThreadSpanState threadSpanState) {
            this.mContainerState = threadSpanState;
        }

        protected Span() {
            this.mContainerState = null;
        }

        public void finish() {
            ThreadSpanState threadSpanState = this.mContainerState;
            synchronized (threadSpanState) {
                if (this.mName == null) {
                    return;
                }
                Span span = this.mPrev;
                if (span != null) {
                    span.mNext = this.mNext;
                }
                Span span2 = this.mNext;
                if (span2 != null) {
                    span2.mPrev = span;
                }
                if (threadSpanState.mActiveHead == this) {
                    threadSpanState.mActiveHead = this.mNext;
                }
                threadSpanState.mActiveSize--;
                if (StrictMode.LOG_V) {
                    Log.d(StrictMode.TAG, "Span finished=" + this.mName + "; size=" + threadSpanState.mActiveSize);
                }
                this.mCreateMillis = -1L;
                this.mName = null;
                this.mPrev = null;
                this.mNext = null;
                if (threadSpanState.mFreeListSize < 5) {
                    this.mNext = threadSpanState.mFreeListHead;
                    threadSpanState.mFreeListHead = this;
                    threadSpanState.mFreeListSize++;
                }
            }
        }
    }

    private static class ThreadSpanState {
        public Span mActiveHead;
        public int mActiveSize;
        public Span mFreeListHead;
        public int mFreeListSize;

        private ThreadSpanState() {
        }
    }

    public static Span enterCriticalSpan(String str) {
        Span span;
        if (IS_USER_BUILD) {
            return NO_OP_SPAN;
        }
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("name must be non-null and non-empty");
        }
        ThreadSpanState threadSpanState = sThisThreadSpanState.get();
        synchronized (threadSpanState) {
            if (threadSpanState.mFreeListHead != null) {
                span = threadSpanState.mFreeListHead;
                threadSpanState.mFreeListHead = span.mNext;
                threadSpanState.mFreeListSize--;
            } else {
                span = new Span(threadSpanState);
            }
            span.mName = str;
            span.mCreateMillis = SystemClock.uptimeMillis();
            span.mNext = threadSpanState.mActiveHead;
            span.mPrev = null;
            threadSpanState.mActiveHead = span;
            threadSpanState.mActiveSize++;
            if (span.mNext != null) {
                span.mNext.mPrev = span;
            }
            if (LOG_V) {
                Log.d(TAG, "Span enter=" + str + "; size=" + threadSpanState.mActiveSize);
            }
        }
        return span;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
    public static void noteSlowCall(String str) throws BlockGuard.BlockGuardPolicyException {
        BlockGuard.Policy threadPolicy = BlockGuard.getThreadPolicy();
        if (threadPolicy instanceof AndroidBlockGuardPolicy) {
            ((AndroidBlockGuardPolicy) threadPolicy).onCustomSlowCall(str);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
    public static void noteDiskRead() throws BlockGuard.BlockGuardPolicyException {
        BlockGuard.Policy threadPolicy = BlockGuard.getThreadPolicy();
        if (threadPolicy instanceof AndroidBlockGuardPolicy) {
            ((AndroidBlockGuardPolicy) threadPolicy).onReadFromDisk();
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
    public static void noteDiskWrite() throws BlockGuard.BlockGuardPolicyException {
        BlockGuard.Policy threadPolicy = BlockGuard.getThreadPolicy();
        if (threadPolicy instanceof AndroidBlockGuardPolicy) {
            ((AndroidBlockGuardPolicy) threadPolicy).onWriteToDisk();
        }
    }

    public static Object trackActivity(Object obj) {
        return new InstanceTracker(obj);
    }

    public static void incrementExpectedActivityCount(Class cls) {
        if (cls == null) {
            return;
        }
        synchronized (StrictMode.class) {
            if ((sVmPolicy.mask & 2048) == 0) {
                return;
            }
            HashMap<Class, Integer> map = sExpectedActivityInstanceCount;
            Integer num = map.get(cls);
            int iIntValue = 1;
            if (num != null) {
                iIntValue = 1 + num.intValue();
            }
            map.put(cls, Integer.valueOf(iIntValue));
        }
    }

    public static void decrementExpectedActivityCount(Class cls) {
        if (cls == null) {
            return;
        }
        synchronized (StrictMode.class) {
            if ((sVmPolicy.mask & 2048) == 0) {
                return;
            }
            HashMap<Class, Integer> map = sExpectedActivityInstanceCount;
            Integer num = map.get(cls);
            int iIntValue = (num == null || num.intValue() == 0) ? 0 : num.intValue() - 1;
            if (iIntValue == 0) {
                map.remove(cls);
            } else {
                map.put(cls, Integer.valueOf(iIntValue));
            }
            int i = iIntValue + 1;
            if (InstanceTracker.getInstanceCount(cls) <= i) {
                return;
            }
            Runtime.getRuntime().gc();
            long jCountInstancesOfClass = VMDebug.countInstancesOfClass(cls, false);
            if (jCountInstancesOfClass > i) {
                InstanceCountViolation instanceCountViolation = new InstanceCountViolation(cls, jCountInstancesOfClass, i);
                onVmPolicyViolation(instanceCountViolation.getMessage(), instanceCountViolation);
            }
        }
    }

    public static class ViolationInfo {
        public String broadcastIntentAction;
        public final ApplicationErrorReport.CrashInfo crashInfo;
        public int durationMillis;
        public int numAnimationsRunning;
        public long numInstances;
        public final int policy;
        public String[] tags;
        public int violationNumThisLoop;
        public long violationUptimeMillis;

        public ViolationInfo() {
            this.durationMillis = -1;
            this.numAnimationsRunning = 0;
            this.numInstances = -1L;
            this.crashInfo = null;
            this.policy = 0;
        }

        public ViolationInfo(Throwable th, int i) {
            this.durationMillis = -1;
            int i2 = 0;
            this.numAnimationsRunning = 0;
            this.numInstances = -1L;
            this.crashInfo = new ApplicationErrorReport.CrashInfo(th);
            this.violationUptimeMillis = SystemClock.uptimeMillis();
            this.policy = i;
            this.numAnimationsRunning = ValueAnimator.getCurrentAnimationsCount();
            Intent intentBeingBroadcast = ActivityThread.getIntentBeingBroadcast();
            if (intentBeingBroadcast != null) {
                this.broadcastIntentAction = intentBeingBroadcast.getAction();
            }
            ThreadSpanState threadSpanState = (ThreadSpanState) StrictMode.sThisThreadSpanState.get();
            if (th instanceof InstanceCountViolation) {
                this.numInstances = ((InstanceCountViolation) th).mInstances;
            }
            synchronized (threadSpanState) {
                int i3 = threadSpanState.mActiveSize;
                i3 = i3 > 20 ? 20 : i3;
                if (i3 != 0) {
                    this.tags = new String[i3];
                    for (Span span = threadSpanState.mActiveHead; span != null && i2 < i3; span = span.mNext) {
                        this.tags[i2] = span.mName;
                        i2++;
                    }
                }
            }
        }

        public int hashCode() {
            int iHashCode = 629 + this.crashInfo.stackTrace.hashCode();
            if (this.numAnimationsRunning != 0) {
                iHashCode *= 37;
            }
            String str = this.broadcastIntentAction;
            if (str != null) {
                iHashCode = (iHashCode * 37) + str.hashCode();
            }
            String[] strArr = this.tags;
            if (strArr != null) {
                for (String str2 : strArr) {
                    iHashCode = (iHashCode * 37) + str2.hashCode();
                }
            }
            return iHashCode;
        }

        public ViolationInfo(Parcel parcel) {
            this(parcel, false);
        }

        public ViolationInfo(Parcel parcel, boolean z) {
            this.durationMillis = -1;
            this.numAnimationsRunning = 0;
            this.numInstances = -1L;
            this.crashInfo = new ApplicationErrorReport.CrashInfo(parcel);
            int i = parcel.readInt();
            if (z) {
                this.policy = i & (-257);
            } else {
                this.policy = i;
            }
            this.durationMillis = parcel.readInt();
            this.violationNumThisLoop = parcel.readInt();
            this.numAnimationsRunning = parcel.readInt();
            this.violationUptimeMillis = parcel.readLong();
            this.numInstances = parcel.readLong();
            this.broadcastIntentAction = parcel.readString();
            this.tags = parcel.readStringArray();
        }

        public void writeToParcel(Parcel parcel, int i) {
            this.crashInfo.writeToParcel(parcel, i);
            parcel.writeInt(this.policy);
            parcel.writeInt(this.durationMillis);
            parcel.writeInt(this.violationNumThisLoop);
            parcel.writeInt(this.numAnimationsRunning);
            parcel.writeLong(this.violationUptimeMillis);
            parcel.writeLong(this.numInstances);
            parcel.writeString(this.broadcastIntentAction);
            parcel.writeStringArray(this.tags);
        }

        public void dump(Printer printer, String str) {
            this.crashInfo.dump(printer, str);
            printer.println(str + "policy: " + this.policy);
            if (this.durationMillis != -1) {
                printer.println(str + "durationMillis: " + this.durationMillis);
            }
            if (this.numInstances != -1) {
                printer.println(str + "numInstances: " + this.numInstances);
            }
            if (this.violationNumThisLoop != 0) {
                printer.println(str + "violationNumThisLoop: " + this.violationNumThisLoop);
            }
            if (this.numAnimationsRunning != 0) {
                printer.println(str + "numAnimationsRunning: " + this.numAnimationsRunning);
            }
            printer.println(str + "violationUptimeMillis: " + this.violationUptimeMillis);
            if (this.broadcastIntentAction != null) {
                printer.println(str + "broadcastIntentAction: " + this.broadcastIntentAction);
            }
            String[] strArr = this.tags;
            if (strArr != null) {
                int length = strArr.length;
                int i = 0;
                int i2 = 0;
                while (i < length) {
                    printer.println(str + "tag[" + i2 + "]: " + strArr[i]);
                    i++;
                    i2++;
                }
            }
        }
    }

    private static class InstanceCountViolation extends Throwable {
        private static final StackTraceElement[] FAKE_STACK = {new StackTraceElement("android.os.StrictMode", "setClassInstanceLimit", "StrictMode.java", 1)};
        final Class mClass;
        final long mInstances;
        final int mLimit;

        public InstanceCountViolation(Class cls, long j, int i) {
            super(cls.toString() + "; instances=" + j + "; limit=" + i);
            setStackTrace(FAKE_STACK);
            this.mClass = cls;
            this.mInstances = j;
            this.mLimit = i;
        }
    }

    private static final class InstanceTracker {
        private static final HashMap<Class<?>, Integer> sInstanceCounts = new HashMap<>();
        private final Class<?> mKlass;

        public InstanceTracker(Object obj) {
            Class<?> cls = obj.getClass();
            this.mKlass = cls;
            HashMap<Class<?>, Integer> map = sInstanceCounts;
            synchronized (map) {
                Integer num = map.get(cls);
                map.put(cls, Integer.valueOf(num != null ? 1 + num.intValue() : 1));
            }
        }

        protected void finalize() throws Throwable {
            try {
                HashMap<Class<?>, Integer> map = sInstanceCounts;
                synchronized (map) {
                    Integer num = map.get(this.mKlass);
                    if (num != null) {
                        int iIntValue = num.intValue() - 1;
                        if (iIntValue > 0) {
                            map.put(this.mKlass, Integer.valueOf(iIntValue));
                        } else {
                            map.remove(this.mKlass);
                        }
                    }
                }
            } finally {
                super.finalize();
            }
        }

        public static int getInstanceCount(Class<?> cls) {
            int iIntValue;
            HashMap<Class<?>, Integer> map = sInstanceCounts;
            synchronized (map) {
                Integer num = map.get(cls);
                iIntValue = num != null ? num.intValue() : 0;
            }
            return iIntValue;
        }
    }
}
