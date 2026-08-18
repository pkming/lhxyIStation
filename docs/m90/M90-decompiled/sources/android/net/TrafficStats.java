package android.net;

import android.content.Context;
import android.net.INetworkStatsService;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.server.NetworkManagementSocketTagger;
import dalvik.system.SocketTagger;
import java.net.Socket;
import java.net.SocketException;

/* JADX INFO: loaded from: classes.dex */
public class TrafficStats {
    public static final long GB_IN_BYTES = 1073741824;
    public static final long KB_IN_BYTES = 1024;
    public static final long MB_IN_BYTES = 1048576;
    public static final int TAG_SYSTEM_BACKUP = -253;
    public static final int TAG_SYSTEM_DOWNLOAD = -255;
    public static final int TAG_SYSTEM_MEDIA = -254;
    private static final int TYPE_RX_BYTES = 0;
    private static final int TYPE_RX_PACKETS = 1;
    private static final int TYPE_TCP_RX_PACKETS = 4;
    private static final int TYPE_TCP_TX_PACKETS = 5;
    private static final int TYPE_TX_BYTES = 2;
    private static final int TYPE_TX_PACKETS = 3;
    public static final int UID_REMOVED = -4;
    public static final int UID_TETHERING = -5;
    public static final int UNSUPPORTED = -1;
    private static NetworkStats sActiveProfilingStart;
    private static Object sProfilingLock = new Object();
    private static INetworkStatsService sStatsService;

    @Deprecated
    public static long getUidTcpRxBytes(int i) {
        return -1L;
    }

    @Deprecated
    public static long getUidTcpRxSegments(int i) {
        return -1L;
    }

    @Deprecated
    public static long getUidTcpTxBytes(int i) {
        return -1L;
    }

    @Deprecated
    public static long getUidTcpTxSegments(int i) {
        return -1L;
    }

    @Deprecated
    public static long getUidUdpRxBytes(int i) {
        return -1L;
    }

    @Deprecated
    public static long getUidUdpRxPackets(int i) {
        return -1L;
    }

    @Deprecated
    public static long getUidUdpTxBytes(int i) {
        return -1L;
    }

    @Deprecated
    public static long getUidUdpTxPackets(int i) {
        return -1L;
    }

    private static native long nativeGetIfaceStat(String str, int i);

    private static native long nativeGetTotalStat(int i);

    private static native long nativeGetUidStat(int i, int i2);

    private static synchronized INetworkStatsService getStatsService() {
        if (sStatsService == null) {
            sStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService(Context.NETWORK_STATS_SERVICE));
        }
        return sStatsService;
    }

    public static void setThreadStatsTag(int i) {
        NetworkManagementSocketTagger.setThreadSocketStatsTag(i);
    }

    public static int getThreadStatsTag() {
        return NetworkManagementSocketTagger.getThreadSocketStatsTag();
    }

    public static void clearThreadStatsTag() {
        NetworkManagementSocketTagger.setThreadSocketStatsTag(-1);
    }

    public static void setThreadStatsUid(int i) {
        NetworkManagementSocketTagger.setThreadSocketStatsUid(i);
    }

    public static void clearThreadStatsUid() {
        NetworkManagementSocketTagger.setThreadSocketStatsUid(-1);
    }

    public static void tagSocket(Socket socket) throws SocketException {
        SocketTagger.get().tag(socket);
    }

    public static void untagSocket(Socket socket) throws SocketException {
        SocketTagger.get().untag(socket);
    }

    public static void startDataProfiling(Context context) {
        synchronized (sProfilingLock) {
            if (sActiveProfilingStart != null) {
                throw new IllegalStateException("already profiling data");
            }
            sActiveProfilingStart = getDataLayerSnapshotForUid(context);
        }
    }

    public static NetworkStats stopDataProfiling(Context context) {
        NetworkStats networkStatsSubtract;
        synchronized (sProfilingLock) {
            if (sActiveProfilingStart == null) {
                throw new IllegalStateException("not profiling data");
            }
            networkStatsSubtract = NetworkStats.subtract(getDataLayerSnapshotForUid(context), sActiveProfilingStart, null, null);
            sActiveProfilingStart = null;
        }
        return networkStatsSubtract;
    }

    public static void incrementOperationCount(int i) {
        incrementOperationCount(getThreadStatsTag(), i);
    }

    public static void incrementOperationCount(int i, int i2) {
        try {
            getStatsService().incrementOperationCount(Process.myUid(), i, i2);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeQuietly(INetworkStatsSession iNetworkStatsSession) {
        if (iNetworkStatsSession != null) {
            try {
                iNetworkStatsSession.close();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception unused) {
            }
        }
    }

    public static long getMobileTxPackets() {
        long txPackets = 0;
        for (String str : getMobileIfaces()) {
            txPackets += getTxPackets(str);
        }
        return txPackets;
    }

    public static long getMobileRxPackets() {
        long rxPackets = 0;
        for (String str : getMobileIfaces()) {
            rxPackets += getRxPackets(str);
        }
        return rxPackets;
    }

    public static long getMobileTxBytes() {
        long txBytes = 0;
        for (String str : getMobileIfaces()) {
            txBytes += getTxBytes(str);
        }
        return txBytes;
    }

    public static long getMobileRxBytes() {
        long rxBytes = 0;
        for (String str : getMobileIfaces()) {
            rxBytes += getRxBytes(str);
        }
        return rxBytes;
    }

    public static long getMobileTcpRxPackets() {
        long j = 0;
        for (String str : getMobileIfaces()) {
            long jNativeGetIfaceStat = nativeGetIfaceStat(str, 4);
            if (jNativeGetIfaceStat != -1) {
                j += jNativeGetIfaceStat;
            }
        }
        return j;
    }

    public static long getMobileTcpTxPackets() {
        long j = 0;
        for (String str : getMobileIfaces()) {
            long jNativeGetIfaceStat = nativeGetIfaceStat(str, 5);
            if (jNativeGetIfaceStat != -1) {
                j += jNativeGetIfaceStat;
            }
        }
        return j;
    }

    public static long getTxPackets(String str) {
        return nativeGetIfaceStat(str, 3);
    }

    public static long getRxPackets(String str) {
        return nativeGetIfaceStat(str, 1);
    }

    public static long getTxBytes(String str) {
        return nativeGetIfaceStat(str, 2);
    }

    public static long getRxBytes(String str) {
        return nativeGetIfaceStat(str, 0);
    }

    public static long getTotalTxPackets() {
        return nativeGetTotalStat(3);
    }

    public static long getTotalRxPackets() {
        return nativeGetTotalStat(1);
    }

    public static long getTotalTxBytes() {
        return nativeGetTotalStat(2);
    }

    public static long getTotalRxBytes() {
        return nativeGetTotalStat(0);
    }

    public static long getUidTxBytes(int i) {
        return nativeGetUidStat(i, 2);
    }

    public static long getUidRxBytes(int i) {
        return nativeGetUidStat(i, 0);
    }

    public static long getUidTxPackets(int i) {
        return nativeGetUidStat(i, 3);
    }

    public static long getUidRxPackets(int i) {
        return nativeGetUidStat(i, 1);
    }

    private static NetworkStats getDataLayerSnapshotForUid(Context context) {
        try {
            return getStatsService().getDataLayerSnapshotForUid(Process.myUid());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] getMobileIfaces() {
        try {
            return getStatsService().getMobileIfaces();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
