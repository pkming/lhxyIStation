package android.net;

import android.content.Context;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.format.Time;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class NetworkPolicyManager {
    private static final boolean ALLOW_PLATFORM_APP_POLICY = true;
    public static final String EXTRA_NETWORK_TEMPLATE = "android.net.NETWORK_TEMPLATE";
    public static final int POLICY_NONE = 0;
    public static final int POLICY_REJECT_METERED_BACKGROUND = 1;
    public static final int RULE_ALLOW_ALL = 0;
    public static final int RULE_REJECT_METERED = 1;
    private INetworkPolicyManager mService;

    public NetworkPolicyManager(INetworkPolicyManager iNetworkPolicyManager) {
        if (iNetworkPolicyManager == null) {
            throw new IllegalArgumentException("missing INetworkPolicyManager");
        }
        this.mService = iNetworkPolicyManager;
    }

    public static NetworkPolicyManager from(Context context) {
        return (NetworkPolicyManager) context.getSystemService(Context.NETWORK_POLICY_SERVICE);
    }

    public void setUidPolicy(int i, int i2) {
        try {
            this.mService.setUidPolicy(i, i2);
        } catch (RemoteException unused) {
        }
    }

    public int getUidPolicy(int i) {
        try {
            return this.mService.getUidPolicy(i);
        } catch (RemoteException unused) {
            return 0;
        }
    }

    public int[] getUidsWithPolicy(int i) {
        try {
            return this.mService.getUidsWithPolicy(i);
        } catch (RemoteException unused) {
            return new int[0];
        }
    }

    public void registerListener(INetworkPolicyListener iNetworkPolicyListener) {
        try {
            this.mService.registerListener(iNetworkPolicyListener);
        } catch (RemoteException unused) {
        }
    }

    public void unregisterListener(INetworkPolicyListener iNetworkPolicyListener) {
        try {
            this.mService.unregisterListener(iNetworkPolicyListener);
        } catch (RemoteException unused) {
        }
    }

    public void setNetworkPolicies(NetworkPolicy[] networkPolicyArr) {
        try {
            this.mService.setNetworkPolicies(networkPolicyArr);
        } catch (RemoteException unused) {
        }
    }

    public NetworkPolicy[] getNetworkPolicies() {
        try {
            return this.mService.getNetworkPolicies();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public void setRestrictBackground(boolean z) {
        try {
            this.mService.setRestrictBackground(z);
        } catch (RemoteException unused) {
        }
    }

    public boolean getRestrictBackground() {
        try {
            return this.mService.getRestrictBackground();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static long computeLastCycleBoundary(long j, NetworkPolicy networkPolicy) {
        if (networkPolicy.cycleDay == -1) {
            throw new IllegalArgumentException("Unable to compute boundary without cycleDay");
        }
        Time time = new Time(networkPolicy.cycleTimezone);
        time.set(j);
        Time time2 = new Time(time);
        time2.second = 0;
        time2.minute = 0;
        time2.hour = 0;
        snapToCycleDay(time2, networkPolicy.cycleDay);
        if (Time.compare(time2, time) >= 0) {
            Time time3 = new Time(time);
            time3.second = 0;
            time3.minute = 0;
            time3.hour = 0;
            time3.monthDay = 1;
            time3.month--;
            time3.normalize(true);
            time2.set(time3);
            snapToCycleDay(time2, networkPolicy.cycleDay);
        }
        return time2.toMillis(true);
    }

    public static long computeNextCycleBoundary(long j, NetworkPolicy networkPolicy) {
        if (networkPolicy.cycleDay == -1) {
            throw new IllegalArgumentException("Unable to compute boundary without cycleDay");
        }
        Time time = new Time(networkPolicy.cycleTimezone);
        time.set(j);
        Time time2 = new Time(time);
        time2.second = 0;
        time2.minute = 0;
        time2.hour = 0;
        snapToCycleDay(time2, networkPolicy.cycleDay);
        if (Time.compare(time2, time) <= 0) {
            Time time3 = new Time(time);
            time3.second = 0;
            time3.minute = 0;
            time3.hour = 0;
            time3.monthDay = 1;
            time3.month++;
            time3.normalize(true);
            time2.set(time3);
            snapToCycleDay(time2, networkPolicy.cycleDay);
        }
        return time2.toMillis(true);
    }

    public static void snapToCycleDay(Time time, int i) {
        if (i > time.getActualMaximum(4)) {
            time.month++;
            time.monthDay = 1;
            time.second = -1;
        } else {
            time.monthDay = i;
        }
        time.normalize(true);
    }

    @Deprecated
    public static boolean isUidValidForPolicy(Context context, int i) {
        return UserHandle.isApp(i);
    }

    public static void dumpPolicy(PrintWriter printWriter, int i) {
        printWriter.write("[");
        if ((i & 1) != 0) {
            printWriter.write("REJECT_METERED_BACKGROUND");
        }
        printWriter.write("]");
    }

    public static void dumpRules(PrintWriter printWriter, int i) {
        printWriter.write("[");
        if ((i & 1) != 0) {
            printWriter.write("REJECT_METERED");
        }
        printWriter.write("]");
    }
}
