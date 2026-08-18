package android.os;

import android.os.Parcelable;
import android.util.Log;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.TypedProperties;
import dalvik.bytecode.OpcodeInfo;
import dalvik.system.VMDebug;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

/* JADX INFO: loaded from: classes.dex */
public final class Debug {
    private static final String DEFAULT_TRACE_BODY = "dmtrace";
    private static final String DEFAULT_TRACE_EXTENSION = ".trace";
    private static final String DEFAULT_TRACE_FILE_PATH;
    private static final String DEFAULT_TRACE_PATH_PREFIX;
    public static final int MEMINFO_BUFFERS = 2;
    public static final int MEMINFO_CACHED = 3;
    public static final int MEMINFO_COUNT = 9;
    public static final int MEMINFO_FREE = 1;
    public static final int MEMINFO_SHMEM = 4;
    public static final int MEMINFO_SLAB = 5;
    public static final int MEMINFO_SWAP_FREE = 7;
    public static final int MEMINFO_SWAP_TOTAL = 6;
    public static final int MEMINFO_TOTAL = 0;
    public static final int MEMINFO_ZRAM_TOTAL = 8;
    private static final int MIN_DEBUGGER_IDLE = 1300;
    public static final int SHOW_CLASSLOADER = 2;
    public static final int SHOW_FULL_DETAIL = 1;
    public static final int SHOW_INITIALIZED = 4;
    private static final int SPIN_DELAY = 200;
    private static final String SYSFS_QEMU_TRACE_STATE = "/sys/qemu_trace/state";
    private static final String TAG = "Debug";
    public static final int TRACE_COUNT_ALLOCS = 1;
    private static final TypedProperties debugProperties;
    private static volatile boolean mWaiting = false;

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DebugProperty {
    }

    @Deprecated
    public static void changeDebugPort(int i) {
    }

    public static native void dumpNativeBacktraceToFile(int i, String str);

    public static native void dumpNativeHeap(FileDescriptor fileDescriptor);

    public static final native int getBinderDeathObjectCount();

    public static final native int getBinderLocalObjectCount();

    public static final native int getBinderProxyObjectCount();

    public static native int getBinderReceivedTransactions();

    public static native int getBinderSentTransactions();

    @Deprecated
    public static int getGlobalExternalAllocCount() {
        return 0;
    }

    @Deprecated
    public static int getGlobalExternalAllocSize() {
        return 0;
    }

    @Deprecated
    public static int getGlobalExternalFreedCount() {
        return 0;
    }

    @Deprecated
    public static int getGlobalExternalFreedSize() {
        return 0;
    }

    public static native void getMemInfo(long[] jArr);

    public static native void getMemoryInfo(int i, MemoryInfo memoryInfo);

    public static native void getMemoryInfo(MemoryInfo memoryInfo);

    public static native long getNativeHeapAllocatedSize();

    public static native long getNativeHeapFreeSize();

    public static native long getNativeHeapSize();

    public static native long getPss();

    public static native long getPss(int i, long[] jArr);

    @Deprecated
    public static int getThreadExternalAllocCount() {
        return 0;
    }

    @Deprecated
    public static int getThreadExternalAllocSize() {
        return 0;
    }

    @Deprecated
    public static void resetGlobalExternalAllocCount() {
    }

    @Deprecated
    public static void resetGlobalExternalAllocSize() {
    }

    @Deprecated
    public static void resetGlobalExternalFreedCount() {
    }

    @Deprecated
    public static void resetGlobalExternalFreedSize() {
    }

    @Deprecated
    public static void resetThreadExternalAllocCount() {
    }

    @Deprecated
    public static void resetThreadExternalAllocSize() {
    }

    @Deprecated
    public static int setAllocationLimit(int i) {
        return -1;
    }

    @Deprecated
    public static int setGlobalAllocationLimit(int i) {
        return -1;
    }

    private Debug() {
    }

    static {
        String str = Environment.getLegacyExternalStorageDirectory().getPath() + "/";
        DEFAULT_TRACE_PATH_PREFIX = str;
        DEFAULT_TRACE_FILE_PATH = str + DEFAULT_TRACE_BODY + DEFAULT_TRACE_EXTENSION;
        debugProperties = null;
    }

    public static class MemoryInfo implements Parcelable {
        public static final Parcelable.Creator<MemoryInfo> CREATOR = new Parcelable.Creator<MemoryInfo>() { // from class: android.os.Debug.MemoryInfo.1
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
        public static final int NUM_CATEGORIES = 7;
        public static final int NUM_DVK_STATS = 5;
        public static final int NUM_OTHER_STATS = 16;
        public static final int offsetPrivateClean = 4;
        public static final int offsetPrivateDirty = 2;
        public static final int offsetPss = 0;
        public static final int offsetSharedClean = 5;
        public static final int offsetSharedDirty = 3;
        public static final int offsetSwappablePss = 1;
        public static final int offsetSwappedOut = 6;
        public int dalvikPrivateClean;
        public int dalvikPrivateDirty;
        public int dalvikPss;
        public int dalvikSharedClean;
        public int dalvikSharedDirty;
        public int dalvikSwappablePss;
        public int dalvikSwappedOut;
        public int nativePrivateClean;
        public int nativePrivateDirty;
        public int nativePss;
        public int nativeSharedClean;
        public int nativeSharedDirty;
        public int nativeSwappablePss;
        public int nativeSwappedOut;
        public int otherPrivateClean;
        public int otherPrivateDirty;
        public int otherPss;
        public int otherSharedClean;
        public int otherSharedDirty;
        private int[] otherStats;
        public int otherSwappablePss;
        public int otherSwappedOut;

        public static String getOtherLabel(int i) {
            switch (i) {
                case 0:
                    return "Dalvik Other";
                case 1:
                    return "Stack";
                case 2:
                    return "Cursor";
                case 3:
                    return "Ashmem";
                case 4:
                    return "Other dev";
                case 5:
                    return ".so mmap";
                case 6:
                    return ".jar mmap";
                case 7:
                    return ".apk mmap";
                case 8:
                    return ".ttf mmap";
                case 9:
                    return ".dex mmap";
                case 10:
                    return "code mmap";
                case 11:
                    return "image mmap";
                case 12:
                    return "Other mmap";
                case 13:
                    return "Graphics";
                case 14:
                    return "GL";
                case 15:
                    return "Memtrack";
                case 16:
                    return ".Heap";
                case 17:
                    return ".LOS";
                case 18:
                    return ".LinearAlloc";
                case 19:
                    return ".GC";
                case 20:
                    return ".JITCache";
                default:
                    return "????";
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public MemoryInfo() {
            this.otherStats = new int[147];
        }

        public int getTotalPss() {
            return this.dalvikPss + this.nativePss + this.otherPss;
        }

        public int getTotalUss() {
            return this.dalvikPrivateClean + this.dalvikPrivateDirty + this.nativePrivateClean + this.nativePrivateDirty + this.otherPrivateClean + this.otherPrivateDirty;
        }

        public int getTotalSwappablePss() {
            return this.dalvikSwappablePss + this.nativeSwappablePss + this.otherSwappablePss;
        }

        public int getTotalPrivateDirty() {
            return this.dalvikPrivateDirty + this.nativePrivateDirty + this.otherPrivateDirty;
        }

        public int getTotalSharedDirty() {
            return this.dalvikSharedDirty + this.nativeSharedDirty + this.otherSharedDirty;
        }

        public int getTotalPrivateClean() {
            return this.dalvikPrivateClean + this.nativePrivateClean + this.otherPrivateClean;
        }

        public int getTotalSharedClean() {
            return this.dalvikSharedClean + this.nativeSharedClean + this.otherSharedClean;
        }

        public int getTotalSwappedOut() {
            return this.dalvikSwappedOut + this.nativeSwappedOut + this.otherSwappedOut;
        }

        public int getOtherPss(int i) {
            return this.otherStats[(i * 7) + 0];
        }

        public int getOtherSwappablePss(int i) {
            return this.otherStats[(i * 7) + 1];
        }

        public int getOtherPrivateDirty(int i) {
            return this.otherStats[(i * 7) + 2];
        }

        public int getOtherSharedDirty(int i) {
            return this.otherStats[(i * 7) + 3];
        }

        public int getOtherPrivateClean(int i) {
            return this.otherStats[(i * 7) + 4];
        }

        public int getOtherSharedClean(int i) {
            return this.otherStats[(i * 7) + 5];
        }

        public int getOtherSwappedOut(int i) {
            return this.otherStats[(i * 7) + 6];
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.dalvikPss);
            parcel.writeInt(this.dalvikSwappablePss);
            parcel.writeInt(this.dalvikPrivateDirty);
            parcel.writeInt(this.dalvikSharedDirty);
            parcel.writeInt(this.dalvikPrivateClean);
            parcel.writeInt(this.dalvikSharedClean);
            parcel.writeInt(this.dalvikSwappedOut);
            parcel.writeInt(this.nativePss);
            parcel.writeInt(this.nativeSwappablePss);
            parcel.writeInt(this.nativePrivateDirty);
            parcel.writeInt(this.nativeSharedDirty);
            parcel.writeInt(this.nativePrivateClean);
            parcel.writeInt(this.nativeSharedClean);
            parcel.writeInt(this.nativeSwappedOut);
            parcel.writeInt(this.otherPss);
            parcel.writeInt(this.otherSwappablePss);
            parcel.writeInt(this.otherPrivateDirty);
            parcel.writeInt(this.otherSharedDirty);
            parcel.writeInt(this.otherPrivateClean);
            parcel.writeInt(this.otherSharedClean);
            parcel.writeInt(this.otherSwappedOut);
            parcel.writeIntArray(this.otherStats);
        }

        public void readFromParcel(Parcel parcel) {
            this.dalvikPss = parcel.readInt();
            this.dalvikSwappablePss = parcel.readInt();
            this.dalvikPrivateDirty = parcel.readInt();
            this.dalvikSharedDirty = parcel.readInt();
            this.dalvikPrivateClean = parcel.readInt();
            this.dalvikSharedClean = parcel.readInt();
            this.dalvikSwappedOut = parcel.readInt();
            this.nativePss = parcel.readInt();
            this.nativeSwappablePss = parcel.readInt();
            this.nativePrivateDirty = parcel.readInt();
            this.nativeSharedDirty = parcel.readInt();
            this.nativePrivateClean = parcel.readInt();
            this.nativeSharedClean = parcel.readInt();
            this.nativeSwappedOut = parcel.readInt();
            this.otherPss = parcel.readInt();
            this.otherSwappablePss = parcel.readInt();
            this.otherPrivateDirty = parcel.readInt();
            this.otherSharedDirty = parcel.readInt();
            this.otherPrivateClean = parcel.readInt();
            this.otherSharedClean = parcel.readInt();
            this.otherSwappedOut = parcel.readInt();
            this.otherStats = parcel.createIntArray();
        }

        private MemoryInfo(Parcel parcel) {
            this.otherStats = new int[147];
            readFromParcel(parcel);
        }
    }

    public static void waitForDebugger() {
        if (!VMDebug.isDebuggingEnabled() || isDebuggerConnected()) {
            return;
        }
        System.out.println("Sending WAIT chunk");
        DdmServer.sendChunk(new Chunk(ChunkHandler.type("WAIT"), new byte[]{0}, 0, 1));
        mWaiting = true;
        while (!isDebuggerConnected()) {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException unused) {
            }
        }
        mWaiting = false;
        System.out.println("Debugger has connected");
        while (true) {
            long jLastDebuggerActivity = VMDebug.lastDebuggerActivity();
            if (jLastDebuggerActivity < 0) {
                System.out.println("debugger detached?");
                return;
            } else if (jLastDebuggerActivity < 1300) {
                System.out.println("waiting for debugger to settle...");
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException unused2) {
                }
            } else {
                System.out.println("debugger has settled (" + jLastDebuggerActivity + ")");
                return;
            }
        }
    }

    public static boolean waitingForDebugger() {
        return mWaiting;
    }

    public static boolean isDebuggerConnected() {
        return VMDebug.isDebuggerConnected();
    }

    public static String[] getVmFeatureList() {
        return VMDebug.getVmFeatureList();
    }

    public static void startNativeTracing() throws Throwable {
        PrintWriter fastPrintWriter;
        Throwable th;
        PrintWriter printWriter = null;
        try {
            fastPrintWriter = new FastPrintWriter(new FileOutputStream(SYSFS_QEMU_TRACE_STATE));
            try {
                fastPrintWriter.println("1");
                fastPrintWriter.close();
            } catch (Exception unused) {
                printWriter = fastPrintWriter;
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (Throwable th2) {
                th = th2;
                if (fastPrintWriter != null) {
                    fastPrintWriter.close();
                }
                throw th;
            }
        } catch (Exception unused2) {
        } catch (Throwable th3) {
            fastPrintWriter = null;
            th = th3;
        }
        VMDebug.startEmulatorTracing();
    }

    public static void stopNativeTracing() throws Throwable {
        PrintWriter fastPrintWriter;
        Throwable th;
        VMDebug.stopEmulatorTracing();
        PrintWriter printWriter = null;
        try {
            fastPrintWriter = new FastPrintWriter(new FileOutputStream(SYSFS_QEMU_TRACE_STATE));
            try {
                fastPrintWriter.println("0");
                fastPrintWriter.close();
            } catch (Exception unused) {
                printWriter = fastPrintWriter;
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (Throwable th2) {
                th = th2;
                if (fastPrintWriter != null) {
                    fastPrintWriter.close();
                }
                throw th;
            }
        } catch (Exception unused2) {
        } catch (Throwable th3) {
            fastPrintWriter = null;
            th = th3;
        }
    }

    public static void enableEmulatorTraceOutput() {
        VMDebug.startEmulatorTracing();
    }

    public static void startMethodTracing() {
        VMDebug.startMethodTracing(DEFAULT_TRACE_FILE_PATH, 0, 0);
    }

    public static void startMethodTracing(String str) {
        startMethodTracing(str, 0, 0);
    }

    public static void startMethodTracing(String str, int i) {
        startMethodTracing(str, i, 0);
    }

    public static void startMethodTracing(String str, int i, int i2) {
        if (str.charAt(0) != '/') {
            str = DEFAULT_TRACE_PATH_PREFIX + str;
        }
        if (!str.endsWith(DEFAULT_TRACE_EXTENSION)) {
            str = str + DEFAULT_TRACE_EXTENSION;
        }
        VMDebug.startMethodTracing(str, i, i2);
    }

    public static void startMethodTracing(String str, FileDescriptor fileDescriptor, int i, int i2) {
        VMDebug.startMethodTracing(str, fileDescriptor, i, i2);
    }

    public static void startMethodTracingDdms(int i, int i2, boolean z, int i3) {
        VMDebug.startMethodTracingDdms(i, i2, z, i3);
    }

    public static int getMethodTracingMode() {
        return VMDebug.getMethodTracingMode();
    }

    public static void stopMethodTracing() {
        VMDebug.stopMethodTracing();
    }

    public static long threadCpuTimeNanos() {
        return VMDebug.threadCpuTimeNanos();
    }

    @Deprecated
    public static void startAllocCounting() {
        VMDebug.startAllocCounting();
    }

    @Deprecated
    public static void stopAllocCounting() {
        VMDebug.stopAllocCounting();
    }

    public static int getGlobalAllocCount() {
        return VMDebug.getAllocCount(1);
    }

    public static void resetGlobalAllocCount() {
        VMDebug.resetAllocCount(1);
    }

    public static int getGlobalAllocSize() {
        return VMDebug.getAllocCount(2);
    }

    public static void resetGlobalAllocSize() {
        VMDebug.resetAllocCount(2);
    }

    public static int getGlobalFreedCount() {
        return VMDebug.getAllocCount(4);
    }

    public static void resetGlobalFreedCount() {
        VMDebug.resetAllocCount(4);
    }

    public static int getGlobalFreedSize() {
        return VMDebug.getAllocCount(8);
    }

    public static void resetGlobalFreedSize() {
        VMDebug.resetAllocCount(8);
    }

    public static int getGlobalGcInvocationCount() {
        return VMDebug.getAllocCount(16);
    }

    public static void resetGlobalGcInvocationCount() {
        VMDebug.resetAllocCount(16);
    }

    public static int getGlobalClassInitCount() {
        return VMDebug.getAllocCount(32);
    }

    public static void resetGlobalClassInitCount() {
        VMDebug.resetAllocCount(32);
    }

    public static int getGlobalClassInitTime() {
        return VMDebug.getAllocCount(64);
    }

    public static void resetGlobalClassInitTime() {
        VMDebug.resetAllocCount(64);
    }

    public static int getThreadAllocCount() {
        return VMDebug.getAllocCount(65536);
    }

    public static void resetThreadAllocCount() {
        VMDebug.resetAllocCount(65536);
    }

    public static int getThreadAllocSize() {
        return VMDebug.getAllocCount(131072);
    }

    public static void resetThreadAllocSize() {
        VMDebug.resetAllocCount(131072);
    }

    public static int getThreadGcInvocationCount() {
        return VMDebug.getAllocCount(1048576);
    }

    public static void resetThreadGcInvocationCount() {
        VMDebug.resetAllocCount(1048576);
    }

    public static void resetAllCounts() {
        VMDebug.resetAllocCount(-1);
    }

    public static void printLoadedClasses(int i) {
        VMDebug.printLoadedClasses(i);
    }

    public static int getLoadedClassCount() {
        return VMDebug.getLoadedClassCount();
    }

    public static void dumpHprofData(String str) throws IOException {
        VMDebug.dumpHprofData(str);
    }

    public static void dumpHprofData(String str, FileDescriptor fileDescriptor) throws IOException {
        VMDebug.dumpHprofData(str, fileDescriptor);
    }

    public static void dumpHprofDataDdms() {
        VMDebug.dumpHprofDataDdms();
    }

    public static long countInstancesOfClass(Class cls) {
        return VMDebug.countInstancesOfClass(cls, true);
    }

    public static final boolean cacheRegisterMap(String str) {
        return VMDebug.cacheRegisterMap(str);
    }

    public static final void dumpReferenceTables() {
        VMDebug.dumpReferenceTables();
    }

    public static class InstructionCount {
        private static final int NUM_INSTR = OpcodeInfo.MAXIMUM_PACKED_VALUE + 1;
        private int[] mCounts = new int[NUM_INSTR];

        public boolean resetAndStart() {
            try {
                VMDebug.startInstructionCounting();
                VMDebug.resetInstructionCount();
                return true;
            } catch (UnsupportedOperationException unused) {
                return false;
            }
        }

        public boolean collect() {
            try {
                VMDebug.stopInstructionCounting();
                VMDebug.getInstructionCount(this.mCounts);
                return true;
            } catch (UnsupportedOperationException unused) {
                return false;
            }
        }

        public int globalTotal() {
            int i = 0;
            for (int i2 = 0; i2 < NUM_INSTR; i2++) {
                i += this.mCounts[i2];
            }
            return i;
        }

        public int globalMethodInvocations() {
            int i = 0;
            for (int i2 = 0; i2 < NUM_INSTR; i2++) {
                if (OpcodeInfo.isInvoke(i2)) {
                    i += this.mCounts[i2];
                }
            }
            return i;
        }
    }

    private static boolean fieldTypeMatches(Field field, Class<?> cls) {
        Class<?> type = field.getType();
        if (type == cls) {
            return true;
        }
        try {
            return type == ((Class) cls.getField("TYPE").get(null));
        } catch (IllegalAccessException | NoSuchFieldException unused) {
            return false;
        }
    }

    private static void modifyFieldIfSet(Field field, TypedProperties typedProperties, String str) {
        if (field.getType() == String.class) {
            int stringInfo = typedProperties.getStringInfo(str);
            if (stringInfo == -2) {
                throw new IllegalArgumentException("Type of " + str + "  does not match field type (" + field.getType() + ")");
            }
            if (stringInfo == -1) {
                return;
            }
            if (stringInfo == 0) {
                try {
                    field.set(null, null);
                    return;
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Cannot set field for " + str, e);
                }
            } else if (stringInfo != 1) {
                throw new IllegalStateException("Unexpected getStringInfo(" + str + ") return value " + stringInfo);
            }
        }
        Object obj = typedProperties.get(str);
        if (obj != null) {
            if (!fieldTypeMatches(field, obj.getClass())) {
                throw new IllegalArgumentException("Type of " + str + " (" + obj.getClass() + ")  does not match field type (" + field.getType() + ")");
            }
            try {
                field.set(null, obj);
            } catch (IllegalAccessException e2) {
                throw new IllegalArgumentException("Cannot set field for " + str, e2);
            }
        }
    }

    public static void setFieldsOn(Class<?> cls) {
        setFieldsOn(cls, false);
    }

    public static void setFieldsOn(Class<?> cls, boolean z) {
        Log.wtf(TAG, "setFieldsOn(" + (cls == null ? "null" : cls.getName()) + ") called in non-DEBUG build");
    }

    public static boolean dumpService(String str, FileDescriptor fileDescriptor, String[] strArr) {
        IBinder service = ServiceManager.getService(str);
        if (service == null) {
            Log.e(TAG, "Can't find service to dump: " + str);
            return false;
        }
        try {
            service.dump(fileDescriptor, strArr);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Can't dump service: " + str, e);
            return false;
        }
    }

    private static String getCaller(StackTraceElement[] stackTraceElementArr, int i) {
        int i2 = i + 4;
        if (i2 >= stackTraceElementArr.length) {
            return "<bottom of call stack>";
        }
        StackTraceElement stackTraceElement = stackTraceElementArr[i2];
        return stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber();
    }

    public static String getCallers(int i) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < i; i2++) {
            stringBuffer.append(getCaller(stackTrace, i2)).append(" ");
        }
        return stringBuffer.toString();
    }

    public static String getCallers(int i, int i2) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuffer stringBuffer = new StringBuffer();
        int i3 = i2 + i;
        while (i < i3) {
            stringBuffer.append(getCaller(stackTrace, i)).append(" ");
            i++;
        }
        return stringBuffer.toString();
    }

    public static String getCallers(int i, String str) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < i; i2++) {
            stringBuffer.append(str).append(getCaller(stackTrace, i2)).append("\n");
        }
        return stringBuffer.toString();
    }

    public static String getCaller() {
        return getCaller(Thread.currentThread().getStackTrace(), 0);
    }
}
