package android.os;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import libcore.io.Libcore;

/* JADX INFO: loaded from: classes.dex */
public class Process {
    public static final String ANDROID_SHARED_MEDIA = "com.android.process.media";
    public static final int BLUETOOTH_UID = 1002;
    public static final int DRM_UID = 1019;
    public static final int FIRST_APPLICATION_UID = 10000;
    public static final int FIRST_ISOLATED_UID = 99000;
    public static final int FIRST_SHARED_APPLICATION_GID = 50000;
    public static final String GOOGLE_SHARED_APP_CONTENT = "com.google.process.content";
    public static final int LAST_APPLICATION_UID = 19999;
    public static final int LAST_ISOLATED_UID = 99999;
    public static final int LAST_SHARED_APPLICATION_GID = 59999;
    private static final String LOG_TAG = "Process";
    public static final int LOG_UID = 1007;
    public static final int MEDIA_RW_GID = 1023;
    public static final int MEDIA_UID = 1013;
    public static final int NFC_UID = 1027;
    public static final int PACKAGE_INFO_GID = 1032;
    public static final int PHONE_UID = 1001;
    public static final int PROC_COMBINE = 256;
    public static final int PROC_OUT_FLOAT = 16384;
    public static final int PROC_OUT_LONG = 8192;
    public static final int PROC_OUT_STRING = 4096;
    public static final int PROC_PARENS = 512;
    public static final int PROC_QUOTES = 1024;
    public static final int PROC_SPACE_TERM = 32;
    public static final int PROC_TAB_TERM = 9;
    public static final int PROC_TERM_MASK = 255;
    public static final int PROC_ZERO_TERM = 0;
    public static final int SCHED_BATCH = 3;
    public static final int SCHED_FIFO = 1;
    public static final int SCHED_IDLE = 5;
    public static final int SCHED_OTHER = 0;
    public static final int SCHED_RR = 2;
    public static final int SHELL_UID = 2000;
    public static final int SIGNAL_KILL = 9;
    public static final int SIGNAL_QUIT = 3;
    public static final int SIGNAL_USR1 = 10;
    public static final int SYSTEM_UID = 1000;
    public static final int THREAD_GROUP_AUDIO_APP = 3;
    public static final int THREAD_GROUP_AUDIO_SYS = 4;
    public static final int THREAD_GROUP_BG_NONINTERACTIVE = 0;
    public static final int THREAD_GROUP_DEFAULT = -1;
    private static final int THREAD_GROUP_FOREGROUND = 1;
    public static final int THREAD_GROUP_SYSTEM = 2;
    public static final int THREAD_PRIORITY_AUDIO = -16;
    public static final int THREAD_PRIORITY_BACKGROUND = 10;
    public static final int THREAD_PRIORITY_DEFAULT = 0;
    public static final int THREAD_PRIORITY_DISPLAY = -4;
    public static final int THREAD_PRIORITY_FOREGROUND = -2;
    public static final int THREAD_PRIORITY_LESS_FAVORABLE = 1;
    public static final int THREAD_PRIORITY_LOWEST = 19;
    public static final int THREAD_PRIORITY_MORE_FAVORABLE = -1;
    public static final int THREAD_PRIORITY_URGENT_AUDIO = -19;
    public static final int THREAD_PRIORITY_URGENT_DISPLAY = -8;
    public static final int VPN_UID = 1016;
    public static final int WIFI_UID = 1010;
    static final int ZYGOTE_RETRY_MILLIS = 500;
    private static final String ZYGOTE_SOCKET = "zygote";
    static boolean sPreviousZygoteOpenFailed;
    static DataInputStream sZygoteInputStream;
    static LocalSocket sZygoteSocket;
    static BufferedWriter sZygoteWriter;

    public static final class ProcessStartResult {
        public int pid;
        public boolean usingWrapper;
    }

    public static final native long getElapsedCpuTime();

    public static final native long getFreeMemory();

    public static final native int getGidForName(String str);

    public static final native int[] getPids(String str, int[] iArr);

    public static final native int[] getPidsForCommands(String[] strArr);

    public static final native int getProcessGroup(int i) throws SecurityException, IllegalArgumentException;

    public static final native long getPss(int i);

    public static final native int getThreadPriority(int i) throws IllegalArgumentException;

    public static final native long getTotalMemory();

    public static final native int getUidForName(String str);

    public static final native boolean parseProcLine(byte[] bArr, int i, int i2, int[] iArr, String[] strArr, long[] jArr, float[] fArr);

    public static final native boolean readProcFile(String str, int[] iArr, String[] strArr, long[] jArr, float[] fArr);

    public static final native void readProcLines(String str, String[] strArr, long[] jArr);

    public static final native void sendSignal(int i, int i2);

    public static final native void sendSignalQuiet(int i, int i2);

    public static final native void setArgV0(String str);

    public static final native void setCanSelfBackground(boolean z);

    public static final native int setGid(int i);

    public static final native boolean setOomAdj(int i, int i2);

    public static final native void setProcessGroup(int i, int i2) throws SecurityException, IllegalArgumentException;

    public static final native boolean setSwappiness(int i, boolean z);

    public static final native void setThreadGroup(int i, int i2) throws SecurityException, IllegalArgumentException;

    public static final native void setThreadPriority(int i) throws SecurityException, IllegalArgumentException;

    public static final native void setThreadPriority(int i, int i2) throws SecurityException, IllegalArgumentException;

    public static final native void setThreadScheduler(int i, int i2, int i3) throws IllegalArgumentException;

    public static final native int setUid(int i);

    @Deprecated
    public static final boolean supportsProcesses() {
        return true;
    }

    public static final ProcessStartResult start(String str, String str2, int i, int i2, int[] iArr, int i3, int i4, int i5, String str3, String[] strArr) {
        try {
            return startViaZygote(str, str2, i, i2, iArr, i3, i4, i5, str3, strArr);
        } catch (ZygoteStartFailedEx e) {
            Log.e(LOG_TAG, "Starting VM process through Zygote failed");
            throw new RuntimeException("Starting VM process through Zygote failed", e);
        }
    }

    private static void openZygoteSocketIfNeeded() throws ZygoteStartFailedEx {
        int i = sPreviousZygoteOpenFailed ? 0 : 10;
        for (int i2 = 0; sZygoteSocket == null && i2 < i + 1; i2++) {
            if (i2 > 0) {
                try {
                    Log.i("Zygote", "Zygote not up yet, sleeping...");
                    Thread.sleep(500L);
                } catch (InterruptedException unused) {
                }
            }
            try {
                LocalSocket localSocket = new LocalSocket();
                sZygoteSocket = localSocket;
                localSocket.connect(new LocalSocketAddress(ZYGOTE_SOCKET, LocalSocketAddress.Namespace.RESERVED));
                sZygoteSocket.setSoTimeout(5000);
                sZygoteInputStream = new DataInputStream(sZygoteSocket.getInputStream());
                sZygoteWriter = new BufferedWriter(new OutputStreamWriter(sZygoteSocket.getOutputStream()), 256);
                Log.i("Zygote", "Process: zygote socket opened");
                sPreviousZygoteOpenFailed = false;
                break;
            } catch (IOException unused2) {
                LocalSocket localSocket2 = sZygoteSocket;
                if (localSocket2 != null) {
                    try {
                        localSocket2.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "I/O exception on close after exception", e);
                    }
                }
                sZygoteSocket = null;
            }
        }
        if (sZygoteSocket != null) {
            return;
        }
        sPreviousZygoteOpenFailed = true;
        throw new ZygoteStartFailedEx("connect failed");
    }

    private static ProcessStartResult zygoteSendArgsAndGetResult(ArrayList<String> arrayList) throws ZygoteStartFailedEx {
        openZygoteSocketIfNeeded();
        try {
            sZygoteWriter.write(Integer.toString(arrayList.size()));
            sZygoteWriter.newLine();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                String str = arrayList.get(i);
                if (str.indexOf(10) >= 0) {
                    throw new ZygoteStartFailedEx("embedded newlines not allowed");
                }
                sZygoteWriter.write(str);
                sZygoteWriter.newLine();
            }
            sZygoteWriter.flush();
            ProcessStartResult processStartResult = new ProcessStartResult();
            processStartResult.pid = sZygoteInputStream.readInt();
            if (processStartResult.pid < 0) {
                throw new ZygoteStartFailedEx("fork() failed");
            }
            processStartResult.usingWrapper = sZygoteInputStream.readBoolean();
            return processStartResult;
        } catch (IOException e) {
            try {
                LocalSocket localSocket = sZygoteSocket;
                if (localSocket != null) {
                    localSocket.close();
                }
            } catch (IOException e2) {
                Log.e(LOG_TAG, "I/O exception on routine close", e2);
            }
            sZygoteSocket = null;
            throw new ZygoteStartFailedEx(e);
        }
    }

    private static ProcessStartResult startViaZygote(String str, String str2, int i, int i2, int[] iArr, int i3, int i4, int i5, String str3, String[] strArr) throws ZygoteStartFailedEx {
        ProcessStartResult processStartResultZygoteSendArgsAndGetResult;
        synchronized (Process.class) {
            ArrayList arrayList = new ArrayList();
            arrayList.add("--runtime-init");
            arrayList.add("--setuid=" + i);
            arrayList.add("--setgid=" + i2);
            if ((i3 & 16) != 0) {
                arrayList.add("--enable-jni-logging");
            }
            if ((i3 & 8) != 0) {
                arrayList.add("--enable-safemode");
            }
            if ((i3 & 1) != 0) {
                arrayList.add("--enable-debugger");
            }
            if ((i3 & 2) != 0) {
                arrayList.add("--enable-checkjni");
            }
            if ((i3 & 4) != 0) {
                arrayList.add("--enable-assert");
            }
            if (i4 == 2) {
                arrayList.add("--mount-external-multiuser");
            } else if (i4 == 3) {
                arrayList.add("--mount-external-multiuser-all");
            }
            arrayList.add("--target-sdk-version=" + i5);
            if (iArr != null && iArr.length > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("--setgroups=");
                int length = iArr.length;
                for (int i6 = 0; i6 < length; i6++) {
                    if (i6 != 0) {
                        sb.append(PhoneNumberUtils.PAUSE);
                    }
                    sb.append(iArr[i6]);
                }
                arrayList.add(sb.toString());
            }
            if (str2 != null) {
                arrayList.add("--nice-name=" + str2);
            }
            if (str3 != null) {
                arrayList.add("--seinfo=" + str3);
            }
            arrayList.add(str);
            if (strArr != null) {
                for (String str4 : strArr) {
                    arrayList.add(str4);
                }
            }
            processStartResultZygoteSendArgsAndGetResult = zygoteSendArgsAndGetResult(arrayList);
        }
        return processStartResultZygoteSendArgsAndGetResult;
    }

    public static final int myPid() {
        return Libcore.os.getpid();
    }

    public static final int myPpid() {
        return Libcore.os.getppid();
    }

    public static final int myTid() {
        return Libcore.os.gettid();
    }

    public static final int myUid() {
        return Libcore.os.getuid();
    }

    public static final UserHandle myUserHandle() {
        return new UserHandle(UserHandle.getUserId(myUid()));
    }

    public static final boolean isIsolated() {
        int appId = UserHandle.getAppId(myUid());
        return appId >= 99000 && appId <= 99999;
    }

    public static final int getUidForPid(int i) {
        long[] jArr = {-1};
        readProcLines("/proc/" + i + "/status", new String[]{"Uid:"}, jArr);
        return (int) jArr[0];
    }

    public static final int getParentPid(int i) {
        long[] jArr = {-1};
        readProcLines("/proc/" + i + "/status", new String[]{"PPid:"}, jArr);
        return (int) jArr[0];
    }

    public static final int getThreadGroupLeader(int i) {
        long[] jArr = {-1};
        readProcLines("/proc/" + i + "/status", new String[]{"Tgid:"}, jArr);
        return (int) jArr[0];
    }

    public static final void killProcess(int i) {
        sendSignal(i, 9);
    }

    public static final void killProcessQuiet(int i) {
        sendSignalQuiet(i, 9);
    }
}
