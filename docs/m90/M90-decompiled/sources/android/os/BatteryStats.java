package android.os;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.nfc.cardemulation.CardEmulation;
import android.telephony.PhoneNumberUtils;
import android.telephony.SignalStrength;
import android.util.Printer;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.SurfaceControl;
import com.unisound.common.r;
import de.innosystec.unrar.unpack.decode.Compress;
import de.innosystec.unrar.unpack.vm.RarVM;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.record.formula.MissingArgPtg;
import org.apache.poi.hssf.record.formula.UnaryPlusPtg;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
public abstract class BatteryStats implements Parcelable {
    private static final String APK_DATA = "apk";
    public static final int AUDIO_TURNED_ON = 7;
    private static final String BATTERY_DATA = "bt";
    private static final String BATTERY_DISCHARGE_DATA = "dc";
    private static final String BATTERY_LEVEL_DATA = "lv";
    private static final int BATTERY_STATS_CHECKIN_VERSION = 7;
    private static final long BYTES_PER_GB = 1073741824;
    private static final long BYTES_PER_KB = 1024;
    private static final long BYTES_PER_MB = 1048576;
    public static final int DATA_CONNECTION_1xRTT = 7;
    public static final int DATA_CONNECTION_CDMA = 4;
    private static final String DATA_CONNECTION_COUNT_DATA = "dcc";
    public static final int DATA_CONNECTION_EDGE = 2;
    public static final int DATA_CONNECTION_EHRPD = 14;
    public static final int DATA_CONNECTION_EVDO_0 = 5;
    public static final int DATA_CONNECTION_EVDO_A = 6;
    public static final int DATA_CONNECTION_EVDO_B = 12;
    public static final int DATA_CONNECTION_GPRS = 1;
    public static final int DATA_CONNECTION_HSDPA = 8;
    public static final int DATA_CONNECTION_HSPA = 10;
    public static final int DATA_CONNECTION_HSPAP = 15;
    public static final int DATA_CONNECTION_HSUPA = 9;
    public static final int DATA_CONNECTION_IDEN = 11;
    public static final int DATA_CONNECTION_LTE = 13;
    static final String[] DATA_CONNECTION_NAMES;
    public static final int DATA_CONNECTION_NONE = 0;
    public static final int DATA_CONNECTION_OTHER = 16;
    private static final String DATA_CONNECTION_TIME_DATA = "dct";
    public static final int DATA_CONNECTION_UMTS = 3;
    public static final int FOREGROUND_ACTIVITY = 10;
    private static final String FOREGROUND_DATA = "fg";
    public static final int FULL_WIFI_LOCK = 5;
    private static final String HISTORY_DATA = "h";
    public static final BitDescription[] HISTORY_STATE_DESCRIPTIONS;
    private static final String KERNEL_WAKELOCK_DATA = "kwl";
    private static final boolean LOCAL_LOGV = false;
    private static final String MISC_DATA = "m";
    private static final String NETWORK_DATA = "nt";
    public static final int NETWORK_MOBILE_RX_BYTES = 0;
    public static final int NETWORK_MOBILE_TX_BYTES = 1;
    public static final int NETWORK_WIFI_RX_BYTES = 2;
    public static final int NETWORK_WIFI_TX_BYTES = 3;
    public static final int NUM_DATA_CONNECTION_TYPES = 17;
    public static final int NUM_NETWORK_ACTIVITY_TYPES = 4;
    public static final int NUM_SCREEN_BRIGHTNESS_BINS = 5;
    private static final String PROCESS_DATA = "pr";
    public static final int SCREEN_BRIGHTNESS_BRIGHT = 4;
    public static final int SCREEN_BRIGHTNESS_DARK = 0;
    private static final String SCREEN_BRIGHTNESS_DATA = "br";
    public static final int SCREEN_BRIGHTNESS_DIM = 1;
    public static final int SCREEN_BRIGHTNESS_LIGHT = 3;
    public static final int SCREEN_BRIGHTNESS_MEDIUM = 2;
    static final String[] SCREEN_BRIGHTNESS_NAMES;
    public static final int SENSOR = 3;
    private static final String SENSOR_DATA = "sr";
    public static final String SERVICE_NAME = "batterystats";
    private static final String SIGNAL_SCANNING_TIME_DATA = "sst";
    private static final String SIGNAL_STRENGTH_COUNT_DATA = "sgc";
    private static final String SIGNAL_STRENGTH_TIME_DATA = "sgt";
    public static final int STATS_CURRENT = 2;
    public static final int STATS_LAST = 1;
    public static final int STATS_SINCE_CHARGED = 0;
    public static final int STATS_SINCE_UNPLUGGED = 3;
    private static final String[] STAT_NAMES = {"t", "l", FullBackup.CACHE_TREE_TOKEN, "u"};
    private static final String UID_DATA = "uid";
    private static final String USER_ACTIVITY_DATA = "ua";
    private static final String VIBRATOR_DATA = "vib";
    public static final int VIBRATOR_ON = 9;
    public static final int VIDEO_TURNED_ON = 8;
    private static final String WAKELOCK_DATA = "wl";
    public static final int WAKE_TYPE_FULL = 1;
    public static final int WAKE_TYPE_PARTIAL = 0;
    public static final int WAKE_TYPE_WINDOW = 2;
    public static final int WIFI_BATCHED_SCAN = 11;
    private static final String WIFI_DATA = "wfl";
    public static final int WIFI_MULTICAST_ENABLED = 7;
    public static final int WIFI_RUNNING = 4;
    public static final int WIFI_SCAN = 6;
    private final StringBuilder mFormatBuilder;
    private final Formatter mFormatter;

    public static abstract class Counter {
        public abstract int getCountLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    public static abstract class Timer {
        public abstract int getCountLocked(int i);

        public abstract long getTotalTimeLocked(long j, int i);

        public abstract void logState(Printer printer, String str);
    }

    public abstract long computeBatteryRealtime(long j, int i);

    public abstract long computeBatteryUptime(long j, int i);

    public abstract long computeRealtime(long j, int i);

    public abstract long computeUptime(long j, int i);

    public abstract void finishIteratingHistoryLocked();

    public abstract void finishIteratingOldHistoryLocked();

    public abstract long getBatteryRealtime(long j);

    public abstract long getBatteryUptime(long j);

    public abstract long getBluetoothOnTime(long j, int i);

    public abstract int getCpuSpeedSteps();

    public abstract int getDischargeAmountScreenOff();

    public abstract int getDischargeAmountScreenOffSinceCharge();

    public abstract int getDischargeAmountScreenOn();

    public abstract int getDischargeAmountScreenOnSinceCharge();

    public abstract int getDischargeCurrentLevel();

    public abstract int getDischargeStartLevel();

    public abstract long getGlobalWifiRunningTime(long j, int i);

    public abstract int getHighDischargeAmountSinceCharge();

    public abstract long getHistoryBaseTime();

    public abstract int getInputEventCount(int i);

    public abstract boolean getIsOnBattery();

    public abstract Map<String, ? extends Timer> getKernelWakelockStats();

    public abstract int getLowDischargeAmountSinceCharge();

    public abstract long getNetworkActivityCount(int i, int i2);

    public abstract boolean getNextHistoryLocked(HistoryItem historyItem);

    public abstract boolean getNextOldHistoryLocked(HistoryItem historyItem);

    public abstract int getPhoneDataConnectionCount(int i, int i2);

    public abstract long getPhoneDataConnectionTime(int i, long j, int i2);

    public abstract long getPhoneOnTime(long j, int i);

    public abstract long getPhoneSignalScanningTime(long j, int i);

    public abstract int getPhoneSignalStrengthCount(int i, int i2);

    public abstract long getPhoneSignalStrengthTime(int i, long j, int i2);

    public abstract long getRadioDataUptime();

    public abstract long getScreenBrightnessTime(int i, long j, int i2);

    public abstract long getScreenOnTime(long j, int i);

    public abstract int getStartCount();

    public abstract SparseArray<? extends Uid> getUidStats();

    public abstract long getWifiOnTime(long j, int i);

    public void prepareForDumpLocked() {
    }

    public abstract boolean startIteratingHistoryLocked();

    public abstract boolean startIteratingOldHistoryLocked();

    public BatteryStats() {
        StringBuilder sb = new StringBuilder(32);
        this.mFormatBuilder = sb;
        this.mFormatter = new Formatter(sb);
    }

    static {
        String[] strArr = {"dark", "dim", "medium", "light", "bright"};
        SCREEN_BRIGHTNESS_NAMES = strArr;
        String[] strArr2 = {"none", "gprs", "edge", "umts", "cdma", "evdo_0", "evdo_A", "1xrtt", "hsdpa", "hsupa", "hspa", "iden", "evdo_b", "lte", "ehrpd", "hspap", CardEmulation.CATEGORY_OTHER};
        DATA_CONNECTION_NAMES = strArr2;
        HISTORY_STATE_DESCRIPTIONS = new BitDescription[]{new BitDescription(524288, BatteryManager.EXTRA_PLUGGED), new BitDescription(1048576, "screen"), new BitDescription(268435456, "gps"), new BitDescription(262144, "phone_in_call"), new BitDescription(134217728, "phone_scanning"), new BitDescription(131072, "wifi"), new BitDescription(67108864, "wifi_running"), new BitDescription(33554432, "wifi_full_lock"), new BitDescription(16777216, "wifi_scan"), new BitDescription(8388608, "wifi_multicast"), new BitDescription(65536, "bluetooth"), new BitDescription(4194304, Context.AUDIO_SERVICE), new BitDescription(2097152, "video"), new BitDescription(1073741824, "wake_lock"), new BitDescription(536870912, Context.SENSOR_SERVICE), new BitDescription(15, 0, "brightness", strArr), new BitDescription(240, 4, "signal_strength", SignalStrength.SIGNAL_STRENGTH_NAMES), new BitDescription(HistoryItem.STATE_PHONE_STATE_MASK, 8, "phone_state", new String[]{"in", "out", "emergency", "off"}), new BitDescription(HistoryItem.STATE_DATA_CONNECTION_MASK, 12, "data_conn", strArr2)};
    }

    public static abstract class Uid {
        public static final int NUM_USER_ACTIVITY_TYPES = 3;
        public static final int NUM_WIFI_BATCHED_SCAN_BINS = 5;
        static final String[] USER_ACTIVITY_TYPES = {CardEmulation.CATEGORY_OTHER, "button", "touch"};

        public static abstract class Proc {

            public static class ExcessivePower {
                public static final int TYPE_CPU = 2;
                public static final int TYPE_WAKE = 1;
                public long overTime;
                public int type;
                public long usedTime;
            }

            public abstract int countExcessivePowers();

            public abstract ExcessivePower getExcessivePower(int i);

            public abstract long getForegroundTime(int i);

            public abstract int getStarts(int i);

            public abstract long getSystemTime(int i);

            public abstract long getTimeAtCpuSpeedStep(int i, int i2);

            public abstract long getUserTime(int i);
        }

        public static abstract class Sensor {
            public static final int GPS = -10000;

            public abstract int getHandle();

            public abstract Timer getSensorTime();
        }

        public static abstract class Wakelock {
            public abstract Timer getWakeTime(int i);
        }

        public abstract long getAudioTurnedOnTime(long j, int i);

        public abstract Timer getForegroundActivityTimer();

        public abstract long getFullWifiLockTime(long j, int i);

        public abstract long getNetworkActivityCount(int i, int i2);

        public abstract Map<String, ? extends Pkg> getPackageStats();

        public abstract SparseArray<? extends Pid> getPidStats();

        public abstract Map<String, ? extends Proc> getProcessStats();

        public abstract Map<Integer, ? extends Sensor> getSensorStats();

        public abstract int getUid();

        public abstract int getUserActivityCount(int i, int i2);

        public abstract Timer getVibratorOnTimer();

        public abstract long getVideoTurnedOnTime(long j, int i);

        public abstract Map<String, ? extends Wakelock> getWakelockStats();

        public abstract long getWifiBatchedScanTime(int i, long j, int i2);

        public abstract long getWifiMulticastTime(long j, int i);

        public abstract long getWifiRunningTime(long j, int i);

        public abstract long getWifiScanTime(long j, int i);

        public abstract boolean hasNetworkActivity();

        public abstract boolean hasUserActivity();

        public abstract void noteActivityPausedLocked();

        public abstract void noteActivityResumedLocked();

        public abstract void noteAudioTurnedOffLocked();

        public abstract void noteAudioTurnedOnLocked();

        public abstract void noteFullWifiLockAcquiredLocked();

        public abstract void noteFullWifiLockReleasedLocked();

        public abstract void noteUserActivityLocked(int i);

        public abstract void noteVideoTurnedOffLocked();

        public abstract void noteVideoTurnedOnLocked();

        public abstract void noteWifiBatchedScanStartedLocked(int i);

        public abstract void noteWifiBatchedScanStoppedLocked();

        public abstract void noteWifiMulticastDisabledLocked();

        public abstract void noteWifiMulticastEnabledLocked();

        public abstract void noteWifiRunningLocked();

        public abstract void noteWifiScanStartedLocked();

        public abstract void noteWifiScanStoppedLocked();

        public abstract void noteWifiStoppedLocked();

        public class Pid {
            public long mWakeStart;
            public long mWakeSum;

            public Pid() {
            }
        }

        public static abstract class Pkg {
            public abstract Map<String, ? extends Serv> getServiceStats();

            public abstract int getWakeups(int i);

            public abstract class Serv {
                public abstract int getLaunches(int i);

                public abstract long getStartTime(long j, int i);

                public abstract int getStarts(int i);

                public Serv() {
                }
            }
        }
    }

    public static final class HistoryItem implements Parcelable {
        public static final byte CMD_NULL = 0;
        public static final byte CMD_OVERFLOW = 3;
        public static final byte CMD_START = 2;
        public static final byte CMD_UPDATE = 1;
        static final boolean DEBUG = false;
        static final int DELTA_BATTERY_LEVEL_FLAG = 1048576;
        static final int DELTA_CMD_MASK = 3;
        static final int DELTA_CMD_SHIFT = 18;
        static final int DELTA_STATE_FLAG = 2097152;
        static final int DELTA_STATE_MASK = -4194304;
        static final int DELTA_TIME_ABS = 262141;
        static final int DELTA_TIME_INT = 262142;
        static final int DELTA_TIME_LONG = 262143;
        static final int DELTA_TIME_MASK = 262143;
        public static final int MOST_INTERESTING_STATES = 270270464;
        public static final int STATE_AUDIO_ON_FLAG = 4194304;
        public static final int STATE_BATTERY_PLUGGED_FLAG = 524288;
        public static final int STATE_BLUETOOTH_ON_FLAG = 65536;
        public static final int STATE_BRIGHTNESS_MASK = 15;
        public static final int STATE_BRIGHTNESS_SHIFT = 0;
        public static final int STATE_DATA_CONNECTION_MASK = 61440;
        public static final int STATE_DATA_CONNECTION_SHIFT = 12;
        public static final int STATE_GPS_ON_FLAG = 268435456;
        public static final int STATE_PHONE_IN_CALL_FLAG = 262144;
        public static final int STATE_PHONE_SCANNING_FLAG = 134217728;
        public static final int STATE_PHONE_STATE_MASK = 3840;
        public static final int STATE_PHONE_STATE_SHIFT = 8;
        public static final int STATE_SCREEN_ON_FLAG = 1048576;
        public static final int STATE_SENSOR_ON_FLAG = 536870912;
        public static final int STATE_SIGNAL_STRENGTH_MASK = 240;
        public static final int STATE_SIGNAL_STRENGTH_SHIFT = 4;
        public static final int STATE_VIDEO_ON_FLAG = 2097152;
        public static final int STATE_WAKE_LOCK_FLAG = 1073741824;
        public static final int STATE_WIFI_FULL_LOCK_FLAG = 33554432;
        public static final int STATE_WIFI_MULTICAST_ON_FLAG = 8388608;
        public static final int STATE_WIFI_ON_FLAG = 131072;
        public static final int STATE_WIFI_RUNNING_FLAG = 67108864;
        public static final int STATE_WIFI_SCAN_FLAG = 16777216;
        static final String TAG = "HistoryItem";
        public byte batteryHealth;
        public byte batteryLevel;
        public byte batteryPlugType;
        public byte batteryStatus;
        public char batteryTemperature;
        public char batteryVoltage;
        public byte cmd = 0;
        public HistoryItem next;
        public int states;
        public long time;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public HistoryItem() {
        }

        public HistoryItem(long j, Parcel parcel) {
            this.time = j;
            readFromParcel(parcel);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(this.time);
            parcel.writeInt((this.cmd & 255) | ((this.batteryLevel << 8) & 65280) | ((this.batteryStatus << 16) & SurfaceControl.FX_SURFACE_MASK) | ((this.batteryHealth << 20) & 15728640) | ((this.batteryPlugType << 24) & 251658240));
            parcel.writeInt((this.batteryTemperature & 65535) | ((this.batteryVoltage << 16) & (-65536)));
            parcel.writeInt(this.states);
        }

        private void readFromParcel(Parcel parcel) {
            int i = parcel.readInt();
            this.cmd = (byte) (i & 255);
            this.batteryLevel = (byte) ((i >> 8) & 255);
            this.batteryStatus = (byte) ((i >> 16) & 15);
            this.batteryHealth = (byte) ((i >> 20) & 15);
            this.batteryPlugType = (byte) ((i >> 24) & 15);
            int i2 = parcel.readInt();
            this.batteryTemperature = (char) (i2 & 65535);
            this.batteryVoltage = (char) ((i2 >> 16) & 65535);
            this.states = parcel.readInt();
        }

        public void writeDelta(Parcel parcel, HistoryItem historyItem) {
            if (historyItem == null || historyItem.cmd != 1) {
                parcel.writeInt(DELTA_TIME_ABS);
                writeToParcel(parcel, 0);
                return;
            }
            long j = this.time - historyItem.time;
            int iBuildBatteryLevelInt = historyItem.buildBatteryLevelInt();
            int iBuildStateInt = historyItem.buildStateInt();
            int i = (j < 0 || j > 2147483647L) ? RarVM.VM_MEMMASK : j >= 262141 ? DELTA_TIME_INT : (int) j;
            int i2 = (this.cmd << UnaryPlusPtg.sid) | i | (this.states & DELTA_STATE_MASK);
            int iBuildBatteryLevelInt2 = buildBatteryLevelInt();
            boolean z = iBuildBatteryLevelInt2 != iBuildBatteryLevelInt;
            if (z) {
                i2 |= 1048576;
            }
            int iBuildStateInt2 = buildStateInt();
            boolean z2 = iBuildStateInt2 != iBuildStateInt;
            if (z2) {
                i2 |= 2097152;
            }
            parcel.writeInt(i2);
            if (i >= DELTA_TIME_INT) {
                if (i == DELTA_TIME_INT) {
                    parcel.writeInt((int) j);
                } else {
                    parcel.writeLong(j);
                }
            }
            if (z) {
                parcel.writeInt(iBuildBatteryLevelInt2);
            }
            if (z2) {
                parcel.writeInt(iBuildStateInt2);
            }
        }

        private int buildBatteryLevelInt() {
            return ((this.batteryLevel << 24) & (-16777216)) | ((this.batteryTemperature << 14) & 16760832) | (this.batteryVoltage & 16383);
        }

        private int buildStateInt() {
            return ((this.batteryStatus << 28) & (-268435456)) | ((this.batteryHealth << 24) & 251658240) | ((this.batteryPlugType << MissingArgPtg.sid) & 12582912) | (this.states & Compress.MAXWINMASK);
        }

        public void readDelta(Parcel parcel) {
            int i = parcel.readInt();
            int i2 = 262143 & i;
            this.cmd = (byte) ((i >> 18) & 3);
            if (i2 < DELTA_TIME_ABS) {
                this.time += (long) i2;
            } else if (i2 == DELTA_TIME_ABS) {
                this.time = parcel.readLong();
                readFromParcel(parcel);
                return;
            } else if (i2 == DELTA_TIME_INT) {
                this.time += (long) parcel.readInt();
            } else {
                this.time += parcel.readLong();
            }
            if ((1048576 & i) != 0) {
                int i3 = parcel.readInt();
                this.batteryLevel = (byte) ((i3 >> 24) & 255);
                this.batteryTemperature = (char) ((i3 >> 14) & 1023);
                this.batteryVoltage = (char) (i3 & 16383);
            }
            if ((2097152 & i) != 0) {
                int i4 = parcel.readInt();
                this.states = (i & DELTA_STATE_MASK) | (i4 & Compress.MAXWINMASK);
                this.batteryStatus = (byte) ((i4 >> 28) & 15);
                this.batteryHealth = (byte) ((i4 >> 24) & 15);
                this.batteryPlugType = (byte) ((i4 >> 22) & 3);
                return;
            }
            this.states = (i & DELTA_STATE_MASK) | (this.states & Compress.MAXWINMASK);
        }

        public void clear() {
            this.time = 0L;
            this.cmd = (byte) 0;
            this.batteryLevel = (byte) 0;
            this.batteryStatus = (byte) 0;
            this.batteryHealth = (byte) 0;
            this.batteryPlugType = (byte) 0;
            this.batteryTemperature = (char) 0;
            this.batteryVoltage = (char) 0;
            this.states = 0;
        }

        public void setTo(HistoryItem historyItem) {
            this.time = historyItem.time;
            this.cmd = historyItem.cmd;
            this.batteryLevel = historyItem.batteryLevel;
            this.batteryStatus = historyItem.batteryStatus;
            this.batteryHealth = historyItem.batteryHealth;
            this.batteryPlugType = historyItem.batteryPlugType;
            this.batteryTemperature = historyItem.batteryTemperature;
            this.batteryVoltage = historyItem.batteryVoltage;
            this.states = historyItem.states;
        }

        public void setTo(long j, byte b, HistoryItem historyItem) {
            this.time = j;
            this.cmd = b;
            this.batteryLevel = historyItem.batteryLevel;
            this.batteryStatus = historyItem.batteryStatus;
            this.batteryHealth = historyItem.batteryHealth;
            this.batteryPlugType = historyItem.batteryPlugType;
            this.batteryTemperature = historyItem.batteryTemperature;
            this.batteryVoltage = historyItem.batteryVoltage;
            this.states = historyItem.states;
        }

        public boolean same(HistoryItem historyItem) {
            return this.batteryLevel == historyItem.batteryLevel && this.batteryStatus == historyItem.batteryStatus && this.batteryHealth == historyItem.batteryHealth && this.batteryPlugType == historyItem.batteryPlugType && this.batteryTemperature == historyItem.batteryTemperature && this.batteryVoltage == historyItem.batteryVoltage && this.states == historyItem.states;
        }
    }

    public static final class BitDescription {
        public final int mask;
        public final String name;
        public final int shift;
        public final String[] values;

        public BitDescription(int i, String str) {
            this.mask = i;
            this.shift = -1;
            this.name = str;
            this.values = null;
        }

        public BitDescription(int i, int i2, String str, String[] strArr) {
            this.mask = i;
            this.shift = i2;
            this.name = str;
            this.values = strArr;
        }
    }

    public long getRadioDataUptimeMs() {
        return getRadioDataUptime() / 1000;
    }

    private static final void formatTimeRaw(StringBuilder sb, long j) {
        long j2 = j / 86400;
        if (j2 != 0) {
            sb.append(j2);
            sb.append("d ");
        }
        long j3 = j2 * 60 * 60 * 24;
        long j4 = (j - j3) / 3600;
        if (j4 != 0 || j3 != 0) {
            sb.append(j4);
            sb.append("h ");
        }
        long j5 = j3 + (j4 * 60 * 60);
        long j6 = (j - j5) / 60;
        if (j6 != 0 || j5 != 0) {
            sb.append(j6);
            sb.append("m ");
        }
        long j7 = j5 + (j6 * 60);
        if (j == 0 && j7 == 0) {
            return;
        }
        sb.append(j - j7);
        sb.append("s ");
    }

    private static final void formatTime(StringBuilder sb, long j) {
        long j2 = j / 100;
        formatTimeRaw(sb, j2);
        sb.append((j - (j2 * 100)) * 10);
        sb.append("ms ");
    }

    private static final void formatTimeMs(StringBuilder sb, long j) {
        long j2 = j / 1000;
        formatTimeRaw(sb, j2);
        sb.append(j - (j2 * 1000));
        sb.append("ms ");
    }

    private final String formatRatioLocked(long j, long j2) {
        if (j2 == 0) {
            return "---%";
        }
        this.mFormatBuilder.setLength(0);
        this.mFormatter.format("%.1f%%", Float.valueOf((j / j2) * 100.0f));
        return this.mFormatBuilder.toString();
    }

    private final String formatBytesLocked(long j) {
        this.mFormatBuilder.setLength(0);
        if (j < 1024) {
            return j + "B";
        }
        if (j < 1048576) {
            this.mFormatter.format("%.2fKB", Double.valueOf(j / 1024.0d));
            return this.mFormatBuilder.toString();
        }
        if (j < 1073741824) {
            this.mFormatter.format("%.2fMB", Double.valueOf(j / 1048576.0d));
            return this.mFormatBuilder.toString();
        }
        this.mFormatter.format("%.2fGB", Double.valueOf(j / 1.073741824E9d));
        return this.mFormatBuilder.toString();
    }

    private static long computeWakeLock(Timer timer, long j, int i) {
        if (timer != null) {
            return (timer.getTotalTimeLocked(j, i) + 500) / 1000;
        }
        return 0L;
    }

    private static final String printWakeLock(StringBuilder sb, Timer timer, long j, String str, int i, String str2) {
        if (timer != null) {
            long jComputeWakeLock = computeWakeLock(timer, j, i);
            int countLocked = timer.getCountLocked(i);
            if (jComputeWakeLock != 0) {
                sb.append(str2);
                formatTimeMs(sb, jComputeWakeLock);
                if (str != null) {
                    sb.append(str);
                    sb.append(' ');
                }
                sb.append('(');
                sb.append(countLocked);
                sb.append(" times)");
                return ", ";
            }
        }
        return str2;
    }

    private static final String printWakeLockCheckin(StringBuilder sb, Timer timer, long j, String str, int i, String str2) {
        long totalTimeLocked;
        int countLocked;
        if (timer != null) {
            totalTimeLocked = timer.getTotalTimeLocked(j, i);
            countLocked = timer.getCountLocked(i);
        } else {
            totalTimeLocked = 0;
            countLocked = 0;
        }
        sb.append(str2);
        sb.append((totalTimeLocked + 500) / 1000);
        sb.append(PhoneNumberUtils.PAUSE);
        sb.append(str != null ? str + "," : "");
        sb.append(countLocked);
        return ",";
    }

    private static final void dumpLine(PrintWriter printWriter, int i, String str, String str2, Object... objArr) {
        printWriter.print(7);
        printWriter.print(PhoneNumberUtils.PAUSE);
        printWriter.print(i);
        printWriter.print(PhoneNumberUtils.PAUSE);
        printWriter.print(str);
        printWriter.print(PhoneNumberUtils.PAUSE);
        printWriter.print(str2);
        for (Object obj : objArr) {
            printWriter.print(PhoneNumberUtils.PAUSE);
            printWriter.print(obj);
        }
        printWriter.println();
    }

    public final void dumpCheckinLocked(PrintWriter printWriter, int i, int i2) {
        char c;
        int i3;
        StringBuilder sb;
        Iterator<Map.Entry<String, ? extends Uid.Pkg>> it;
        Iterator<Map.Entry<String, ? extends Uid.Pkg.Serv>> it2;
        int i4 = i2;
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        long batteryUptime = getBatteryUptime(jUptimeMillis);
        long batteryRealtime = getBatteryRealtime(jElapsedRealtime);
        long jComputeBatteryUptime = computeBatteryUptime(jUptimeMillis, i);
        long jComputeBatteryRealtime = computeBatteryRealtime(jElapsedRealtime, i);
        long jComputeRealtime = computeRealtime(jElapsedRealtime, i);
        long jComputeUptime = computeUptime(jUptimeMillis, i);
        long screenOnTime = getScreenOnTime(batteryRealtime, i);
        long phoneOnTime = getPhoneOnTime(batteryRealtime, i);
        long wifiOnTime = getWifiOnTime(batteryRealtime, i);
        long globalWifiRunningTime = getGlobalWifiRunningTime(batteryRealtime, i);
        long bluetoothOnTime = getBluetoothOnTime(batteryRealtime, i);
        StringBuilder sb2 = new StringBuilder(128);
        SparseArray<? extends Uid> uidStats = getUidStats();
        int size = uidStats.size();
        long j = batteryUptime;
        String str = STAT_NAMES[i];
        StringBuilder sb3 = sb2;
        Object[] objArr = new Object[5];
        objArr[0] = i == 0 ? Integer.valueOf(getStartCount()) : "N/A";
        objArr[1] = Long.valueOf(jComputeBatteryRealtime / 1000);
        int i5 = 2;
        objArr[2] = Long.valueOf(jComputeBatteryUptime / 1000);
        int i6 = 3;
        objArr[3] = Long.valueOf(jComputeRealtime / 1000);
        objArr[4] = Long.valueOf(jComputeUptime / 1000);
        int i7 = 0;
        dumpLine(printWriter, 0, str, BATTERY_DATA, objArr);
        int i8 = 0;
        long networkActivityCount = 0;
        long networkActivityCount2 = 0;
        long networkActivityCount3 = 0;
        long networkActivityCount4 = 0;
        long totalTimeLocked = 0;
        long totalTimeLocked2 = 0;
        while (i8 < size) {
            Uid uidValueAt = uidStats.valueAt(i8);
            networkActivityCount += uidValueAt.getNetworkActivityCount(i7, i);
            networkActivityCount2 += uidValueAt.getNetworkActivityCount(1, i);
            networkActivityCount3 += uidValueAt.getNetworkActivityCount(i5, i);
            networkActivityCount4 += uidValueAt.getNetworkActivityCount(i6, i);
            Map<String, ? extends Uid.Wakelock> wakelockStats = uidValueAt.getWakelockStats();
            if (wakelockStats.size() > 0) {
                Iterator<Map.Entry<String, ? extends Uid.Wakelock>> it3 = wakelockStats.entrySet().iterator();
                while (it3.hasNext()) {
                    Uid.Wakelock value = it3.next().getValue();
                    Timer wakeTime = value.getWakeTime(1);
                    if (wakeTime != null) {
                        totalTimeLocked += wakeTime.getTotalTimeLocked(batteryRealtime, i);
                    }
                    Timer wakeTime2 = value.getWakeTime(0);
                    if (wakeTime2 != null) {
                        totalTimeLocked2 += wakeTime2.getTotalTimeLocked(batteryRealtime, i);
                    }
                }
            }
            i8++;
            i7 = 0;
            i5 = 2;
            i6 = 3;
        }
        dumpLine(printWriter, 0, str, MISC_DATA, Long.valueOf(screenOnTime / 1000), Long.valueOf(phoneOnTime / 1000), Long.valueOf(wifiOnTime / 1000), Long.valueOf(globalWifiRunningTime / 1000), Long.valueOf(bluetoothOnTime / 1000), Long.valueOf(networkActivityCount), Long.valueOf(networkActivityCount2), Long.valueOf(networkActivityCount3), Long.valueOf(networkActivityCount4), Long.valueOf(totalTimeLocked), Long.valueOf(totalTimeLocked2), Integer.valueOf(getInputEventCount(i)));
        Object[] objArr2 = new Object[5];
        for (int i9 = 0; i9 < 5; i9++) {
            objArr2[i9] = Long.valueOf(getScreenBrightnessTime(i9, batteryRealtime, i) / 1000);
        }
        dumpLine(printWriter, 0, str, SCREEN_BRIGHTNESS_DATA, objArr2);
        Object[] objArr3 = new Object[5];
        int i10 = 0;
        for (int i11 = 5; i10 < i11; i11 = 5) {
            objArr3[i10] = Long.valueOf(getPhoneSignalStrengthTime(i10, batteryRealtime, i) / 1000);
            i10++;
        }
        dumpLine(printWriter, 0, str, SIGNAL_STRENGTH_TIME_DATA, objArr3);
        dumpLine(printWriter, 0, str, SIGNAL_SCANNING_TIME_DATA, Long.valueOf(getPhoneSignalScanningTime(batteryRealtime, i) / 1000));
        for (int i12 = 0; i12 < 5; i12++) {
            objArr3[i12] = Integer.valueOf(getPhoneSignalStrengthCount(i12, i));
        }
        dumpLine(printWriter, 0, str, SIGNAL_STRENGTH_COUNT_DATA, objArr3);
        Object[] objArr4 = new Object[17];
        for (int i13 = 0; i13 < 17; i13++) {
            objArr4[i13] = Long.valueOf(getPhoneDataConnectionTime(i13, batteryRealtime, i) / 1000);
        }
        dumpLine(printWriter, 0, str, DATA_CONNECTION_TIME_DATA, objArr4);
        for (int i14 = 0; i14 < 17; i14++) {
            objArr4[i14] = Integer.valueOf(getPhoneDataConnectionCount(i14, i));
        }
        dumpLine(printWriter, 0, str, DATA_CONNECTION_COUNT_DATA, objArr4);
        if (i == 3) {
            dumpLine(printWriter, 0, str, BATTERY_LEVEL_DATA, Integer.valueOf(getDischargeStartLevel()), Integer.valueOf(getDischargeCurrentLevel()));
        }
        if (i == 3) {
            dumpLine(printWriter, 0, str, BATTERY_DISCHARGE_DATA, Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeAmountScreenOn()), Integer.valueOf(getDischargeAmountScreenOff()));
            c = 4;
        } else {
            c = 4;
            dumpLine(printWriter, 0, str, BATTERY_DISCHARGE_DATA, Integer.valueOf(getLowDischargeAmountSinceCharge()), Integer.valueOf(getHighDischargeAmountSinceCharge()), Integer.valueOf(getDischargeAmountScreenOn()), Integer.valueOf(getDischargeAmountScreenOff()));
        }
        if (i4 < 0) {
            Map<String, ? extends Timer> kernelWakelockStats = getKernelWakelockStats();
            if (kernelWakelockStats.size() > 0) {
                Iterator<Map.Entry<String, ? extends Timer>> it4 = kernelWakelockStats.entrySet().iterator();
                while (it4.hasNext()) {
                    Map.Entry<String, ? extends Timer> next = it4.next();
                    sb3.setLength(0);
                    printWakeLockCheckin(sb3, next.getValue(), batteryRealtime, null, i, "");
                    dumpLine(printWriter, 0, str, KERNEL_WAKELOCK_DATA, next.getKey(), sb3.toString());
                    batteryRealtime = batteryRealtime;
                    it4 = it4;
                    c = 4;
                }
            }
        }
        long j2 = batteryRealtime;
        int i15 = 0;
        while (i15 < size) {
            int iKeyAt = uidStats.keyAt(i15);
            if (i4 < 0 || iKeyAt == i4) {
                Uid uidValueAt2 = uidStats.valueAt(i15);
                long networkActivityCount5 = uidValueAt2.getNetworkActivityCount(0, i);
                long networkActivityCount6 = uidValueAt2.getNetworkActivityCount(1, i);
                long networkActivityCount7 = uidValueAt2.getNetworkActivityCount(2, i);
                long networkActivityCount8 = uidValueAt2.getNetworkActivityCount(3, i);
                long fullWifiLockTime = uidValueAt2.getFullWifiLockTime(j2, i);
                long wifiScanTime = uidValueAt2.getWifiScanTime(j2, i);
                long wifiRunningTime = uidValueAt2.getWifiRunningTime(j2, i);
                if (networkActivityCount5 > 0 || networkActivityCount6 > 0 || networkActivityCount7 > 0 || networkActivityCount8 > 0) {
                    dumpLine(printWriter, iKeyAt, str, NETWORK_DATA, Long.valueOf(networkActivityCount5), Long.valueOf(networkActivityCount6), Long.valueOf(networkActivityCount7), Long.valueOf(networkActivityCount8));
                }
                if (fullWifiLockTime != 0 || wifiScanTime != 0 || wifiRunningTime != 0) {
                    dumpLine(printWriter, iKeyAt, str, WIFI_DATA, Long.valueOf(fullWifiLockTime), Long.valueOf(wifiScanTime), Long.valueOf(wifiRunningTime));
                }
                if (uidValueAt2.hasUserActivity()) {
                    Object[] objArr5 = new Object[3];
                    int i16 = 0;
                    boolean z = false;
                    for (int i17 = 3; i16 < i17; i17 = 3) {
                        int userActivityCount = uidValueAt2.getUserActivityCount(i16, i);
                        objArr5[i16] = Integer.valueOf(userActivityCount);
                        if (userActivityCount != 0) {
                            z = true;
                        }
                        i16++;
                    }
                    if (z) {
                        dumpLine(printWriter, 0, str, USER_ACTIVITY_DATA, objArr5);
                    }
                }
                Map<String, ? extends Uid.Wakelock> wakelockStats2 = uidValueAt2.getWakelockStats();
                if (wakelockStats2.size() > 0) {
                    for (Map.Entry<String, ? extends Uid.Wakelock> entry : wakelockStats2.entrySet()) {
                        Uid.Wakelock value2 = entry.getValue();
                        StringBuilder sb4 = sb3;
                        sb4.setLength(0);
                        Uid uid = uidValueAt2;
                        int i18 = iKeyAt;
                        int i19 = i15;
                        printWakeLockCheckin(sb4, value2.getWakeTime(2), j2, "w", i, printWakeLockCheckin(sb4, value2.getWakeTime(0), j2, "p", i, printWakeLockCheckin(sb4, value2.getWakeTime(1), j2, FullBackup.DATA_TREE_TOKEN, i, "")));
                        if (sb4.length() > 0) {
                            String key = entry.getKey();
                            if (key.indexOf(44) >= 0) {
                                key = key.replace(PhoneNumberUtils.PAUSE, '_');
                            }
                            dumpLine(printWriter, i18, str, WAKELOCK_DATA, key, sb4.toString());
                        }
                        iKeyAt = i18;
                        i15 = i19;
                        sb3 = sb4;
                        uidValueAt2 = uid;
                    }
                }
                Uid uid2 = uidValueAt2;
                int i20 = iKeyAt;
                i3 = i15;
                sb = sb3;
                Map<Integer, ? extends Uid.Sensor> sensorStats = uid2.getSensorStats();
                long j3 = 500;
                if (sensorStats.size() > 0) {
                    for (Map.Entry<Integer, ? extends Uid.Sensor> entry2 : sensorStats.entrySet()) {
                        Uid.Sensor value3 = entry2.getValue();
                        int iIntValue = entry2.getKey().intValue();
                        Timer sensorTime = value3.getSensorTime();
                        if (sensorTime != null) {
                            long totalTimeLocked3 = (sensorTime.getTotalTimeLocked(j2, i) + j3) / 1000;
                            int countLocked = sensorTime.getCountLocked(i);
                            if (totalTimeLocked3 != 0) {
                                dumpLine(printWriter, i20, str, SENSOR_DATA, Integer.valueOf(iIntValue), Long.valueOf(totalTimeLocked3), Integer.valueOf(countLocked));
                            }
                        }
                        j3 = 500;
                    }
                }
                Timer vibratorOnTimer = uid2.getVibratorOnTimer();
                if (vibratorOnTimer != null) {
                    long totalTimeLocked4 = (vibratorOnTimer.getTotalTimeLocked(j2, i) + 500) / 1000;
                    int countLocked2 = vibratorOnTimer.getCountLocked(i);
                    if (totalTimeLocked4 != 0) {
                        dumpLine(printWriter, i20, str, VIBRATOR_DATA, Long.valueOf(totalTimeLocked4), Integer.valueOf(countLocked2));
                    }
                }
                Timer foregroundActivityTimer = uid2.getForegroundActivityTimer();
                if (foregroundActivityTimer != null) {
                    long totalTimeLocked5 = (foregroundActivityTimer.getTotalTimeLocked(j2, i) + 500) / 1000;
                    int countLocked3 = foregroundActivityTimer.getCountLocked(i);
                    if (totalTimeLocked5 != 0) {
                        dumpLine(printWriter, i20, str, FOREGROUND_DATA, Long.valueOf(totalTimeLocked5), Integer.valueOf(countLocked3));
                    }
                }
                Map<String, ? extends Uid.Proc> processStats = uid2.getProcessStats();
                if (processStats.size() > 0) {
                    for (Map.Entry<String, ? extends Uid.Proc> entry3 : processStats.entrySet()) {
                        Uid.Proc value4 = entry3.getValue();
                        long userTime = value4.getUserTime(i) * 10;
                        long systemTime = value4.getSystemTime(i) * 10;
                        long foregroundTime = value4.getForegroundTime(i) * 10;
                        long starts = value4.getStarts(i);
                        if (userTime != 0 || systemTime != 0 || foregroundTime != 0 || starts != 0) {
                            dumpLine(printWriter, i20, str, PROCESS_DATA, entry3.getKey(), Long.valueOf(userTime), Long.valueOf(systemTime), Long.valueOf(foregroundTime), Long.valueOf(starts));
                        }
                    }
                }
                Map<String, ? extends Uid.Pkg> packageStats = uid2.getPackageStats();
                if (packageStats.size() > 0) {
                    Iterator<Map.Entry<String, ? extends Uid.Pkg>> it5 = packageStats.entrySet().iterator();
                    while (it5.hasNext()) {
                        Map.Entry<String, ? extends Uid.Pkg> next2 = it5.next();
                        Uid.Pkg value5 = next2.getValue();
                        int wakeups = value5.getWakeups(i);
                        Iterator<Map.Entry<String, ? extends Uid.Pkg.Serv>> it6 = value5.getServiceStats().entrySet().iterator();
                        while (it6.hasNext()) {
                            Map.Entry<String, ? extends Uid.Pkg.Serv> next3 = it6.next();
                            Uid.Pkg.Serv value6 = next3.getValue();
                            long j4 = j;
                            long startTime = value6.getStartTime(j4, i);
                            int starts2 = value6.getStarts(i);
                            int launches = value6.getLaunches(i);
                            if (startTime == 0 && starts2 == 0 && launches == 0) {
                                it = it5;
                                it2 = it6;
                            } else {
                                it = it5;
                                it2 = it6;
                                dumpLine(printWriter, i20, str, APK_DATA, Integer.valueOf(wakeups), next2.getKey(), next3.getKey(), Long.valueOf(startTime / 1000), Integer.valueOf(starts2), Integer.valueOf(launches));
                            }
                            j = j4;
                            it6 = it2;
                            it5 = it;
                        }
                    }
                }
            } else {
                i3 = i15;
                sb = sb3;
            }
            i4 = i2;
            j = j;
            sb3 = sb;
            i15 = i3 + 1;
        }
    }

    static final class TimerEntry {
        final int mId;
        final String mName;
        final long mTime;
        final Timer mTimer;

        TimerEntry(String str, int i, Timer timer, long j) {
            this.mName = str;
            this.mId = i;
            this.mTimer = timer;
            this.mTime = j;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x028b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void dumpLocked(java.io.PrintWriter r71, java.lang.String r72, int r73, int r74) {
        /*
            Method dump skipped, instruction units count: 3354
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.dumpLocked(java.io.PrintWriter, java.lang.String, int, int):void");
    }

    static void printBitDescriptions(PrintWriter printWriter, int i, int i2, BitDescription[] bitDescriptionArr) {
        int i3 = i ^ i2;
        if (i3 == 0) {
            return;
        }
        for (BitDescription bitDescription : bitDescriptionArr) {
            if ((bitDescription.mask & i3) != 0) {
                if (bitDescription.shift < 0) {
                    printWriter.print((bitDescription.mask & i2) != 0 ? " +" : " -");
                    printWriter.print(bitDescription.name);
                } else {
                    printWriter.print(" ");
                    printWriter.print(bitDescription.name);
                    printWriter.print("=");
                    int i4 = (bitDescription.mask & i2) >> bitDescription.shift;
                    if (bitDescription.values != null && i4 >= 0 && i4 < bitDescription.values.length) {
                        printWriter.print(bitDescription.values[i4]);
                    } else {
                        printWriter.print(i4);
                    }
                }
            }
        }
    }

    public static class HistoryPrinter {
        int oldState = 0;
        int oldStatus = -1;
        int oldHealth = -1;
        int oldPlug = -1;
        int oldTemp = -1;
        int oldVolt = -1;

        public void printNextItem(PrintWriter printWriter, HistoryItem historyItem, long j) {
            printWriter.print("  ");
            TimeUtils.formatDuration(historyItem.time - j, printWriter, 19);
            printWriter.print(" ");
            if (historyItem.cmd == 2) {
                printWriter.println(" START");
            } else if (historyItem.cmd == 3) {
                printWriter.println(" *OVERFLOW*");
            } else {
                if (historyItem.batteryLevel < 10) {
                    printWriter.print(TarConstants.VERSION_POSIX);
                } else if (historyItem.batteryLevel < 100) {
                    printWriter.print("0");
                }
                printWriter.print((int) historyItem.batteryLevel);
                printWriter.print(" ");
                if (historyItem.states < 16) {
                    printWriter.print("0000000");
                } else if (historyItem.states < 256) {
                    printWriter.print("000000");
                } else if (historyItem.states < 4096) {
                    printWriter.print("00000");
                } else if (historyItem.states < 65536) {
                    printWriter.print("0000");
                } else if (historyItem.states < 1048576) {
                    printWriter.print("000");
                } else if (historyItem.states < 16777216) {
                    printWriter.print(TarConstants.VERSION_POSIX);
                } else if (historyItem.states < 268435456) {
                    printWriter.print("0");
                }
                printWriter.print(Integer.toHexString(historyItem.states));
                if (this.oldStatus != historyItem.batteryStatus) {
                    this.oldStatus = historyItem.batteryStatus;
                    printWriter.print(" status=");
                    int i = this.oldStatus;
                    if (i == 1) {
                        printWriter.print("unknown");
                    } else if (i == 2) {
                        printWriter.print("charging");
                    } else if (i == 3) {
                        printWriter.print("discharging");
                    } else if (i == 4) {
                        printWriter.print("not-charging");
                    } else if (i == 5) {
                        printWriter.print("full");
                    } else {
                        printWriter.print(i);
                    }
                }
                if (this.oldHealth != historyItem.batteryHealth) {
                    this.oldHealth = historyItem.batteryHealth;
                    printWriter.print(" health=");
                    int i2 = this.oldHealth;
                    switch (i2) {
                        case 1:
                            printWriter.print("unknown");
                            break;
                        case 2:
                            printWriter.print("good");
                            break;
                        case 3:
                            printWriter.print("overheat");
                            break;
                        case 4:
                            printWriter.print("dead");
                            break;
                        case 5:
                            printWriter.print("over-voltage");
                            break;
                        case 6:
                            printWriter.print("failure");
                            break;
                        default:
                            printWriter.print(i2);
                            break;
                    }
                }
                if (this.oldPlug != historyItem.batteryPlugType) {
                    this.oldPlug = historyItem.batteryPlugType;
                    printWriter.print(" plug=");
                    int i3 = this.oldPlug;
                    if (i3 == 0) {
                        printWriter.print("none");
                    } else if (i3 == 1) {
                        printWriter.print("ac");
                    } else if (i3 == 2) {
                        printWriter.print(Context.USB_SERVICE);
                    } else if (i3 == 4) {
                        printWriter.print("wireless");
                    } else {
                        printWriter.print(i3);
                    }
                }
                if (this.oldTemp != historyItem.batteryTemperature) {
                    this.oldTemp = historyItem.batteryTemperature;
                    printWriter.print(" temp=");
                    printWriter.print(this.oldTemp);
                }
                if (this.oldVolt != historyItem.batteryVoltage) {
                    this.oldVolt = historyItem.batteryVoltage;
                    printWriter.print(" volt=");
                    printWriter.print(this.oldVolt);
                }
                BatteryStats.printBitDescriptions(printWriter, this.oldState, historyItem.states, BatteryStats.HISTORY_STATE_DESCRIPTIONS);
                printWriter.println();
            }
            this.oldState = historyItem.states;
        }

        public void printNextItemCheckin(PrintWriter printWriter, HistoryItem historyItem, long j) {
            printWriter.print(historyItem.time - j);
            printWriter.print(",");
            if (historyItem.cmd == 2) {
                printWriter.print(r.w);
                return;
            }
            if (historyItem.cmd == 3) {
                printWriter.print("overflow");
                return;
            }
            printWriter.print((int) historyItem.batteryLevel);
            printWriter.print(",");
            printWriter.print(historyItem.states);
            printWriter.print(",");
            printWriter.print((int) historyItem.batteryStatus);
            printWriter.print(",");
            printWriter.print((int) historyItem.batteryHealth);
            printWriter.print(",");
            printWriter.print((int) historyItem.batteryPlugType);
            printWriter.print(",");
            printWriter.print((int) historyItem.batteryTemperature);
            printWriter.print(",");
            printWriter.print((int) historyItem.batteryVoltage);
        }
    }

    public void dumpLocked(PrintWriter printWriter, boolean z, int i) {
        prepareForDumpLocked();
        long historyBaseTime = getHistoryBaseTime() + SystemClock.elapsedRealtime();
        HistoryItem historyItem = new HistoryItem();
        if (startIteratingHistoryLocked()) {
            printWriter.println("Battery History:");
            HistoryPrinter historyPrinter = new HistoryPrinter();
            while (getNextHistoryLocked(historyItem)) {
                historyPrinter.printNextItem(printWriter, historyItem, historyBaseTime);
            }
            finishIteratingHistoryLocked();
            printWriter.println("");
        }
        if (startIteratingOldHistoryLocked()) {
            printWriter.println("Old battery History:");
            HistoryPrinter historyPrinter2 = new HistoryPrinter();
            while (getNextOldHistoryLocked(historyItem)) {
                historyPrinter2.printNextItem(printWriter, historyItem, historyBaseTime);
            }
            finishIteratingOldHistoryLocked();
            printWriter.println("");
        }
        SparseArray<? extends Uid> uidStats = getUidStats();
        int size = uidStats.size();
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        int i2 = 0;
        boolean z2 = false;
        while (i2 < size) {
            SparseArray<? extends Uid.Pid> pidStats = uidStats.valueAt(i2).getPidStats();
            if (pidStats != null) {
                int i3 = 0;
                while (i3 < pidStats.size()) {
                    Uid.Pid pidValueAt = pidStats.valueAt(i3);
                    if (!z2) {
                        printWriter.println("Per-PID Stats:");
                        z2 = true;
                    }
                    long j = pidValueAt.mWakeSum;
                    int i4 = i2;
                    long j2 = 0;
                    if (pidValueAt.mWakeStart != 0) {
                        j2 = jElapsedRealtime - pidValueAt.mWakeStart;
                    }
                    printWriter.print("  PID ");
                    printWriter.print(pidStats.keyAt(i3));
                    printWriter.print(" wake time: ");
                    TimeUtils.formatDuration(j + j2, printWriter);
                    printWriter.println("");
                    i3++;
                    i2 = i4;
                }
            }
            i2++;
        }
        if (z2) {
            printWriter.println("");
        }
        if (!z) {
            printWriter.println("Statistics since last charge:");
            printWriter.println("  System starts: " + getStartCount() + ", currently on battery: " + getIsOnBattery());
            dumpLocked(printWriter, "", 0, i);
            printWriter.println("");
        }
        printWriter.println("Statistics since last unplugged:");
        dumpLocked(printWriter, "", 3, i);
    }

    public void dumpCheckinLocked(PrintWriter printWriter, List<ApplicationInfo> list, boolean z, boolean z2) {
        prepareForDumpLocked();
        long historyBaseTime = getHistoryBaseTime() + SystemClock.elapsedRealtime();
        if (z2) {
            HistoryItem historyItem = new HistoryItem();
            if (startIteratingHistoryLocked()) {
                HistoryPrinter historyPrinter = new HistoryPrinter();
                while (getNextHistoryLocked(historyItem)) {
                    printWriter.print(7);
                    printWriter.print(PhoneNumberUtils.PAUSE);
                    printWriter.print(0);
                    printWriter.print(PhoneNumberUtils.PAUSE);
                    printWriter.print("h");
                    printWriter.print(PhoneNumberUtils.PAUSE);
                    historyPrinter.printNextItemCheckin(printWriter, historyItem, historyBaseTime);
                    printWriter.println();
                }
                finishIteratingHistoryLocked();
            }
        }
        if (list != null) {
            SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < list.size(); i++) {
                ApplicationInfo applicationInfo = list.get(i);
                ArrayList arrayList = (ArrayList) sparseArray.get(applicationInfo.uid);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    sparseArray.put(applicationInfo.uid, arrayList);
                }
                arrayList.add(applicationInfo.packageName);
            }
            SparseArray<? extends Uid> uidStats = getUidStats();
            int size = uidStats.size();
            String[] strArr = new String[2];
            for (int i2 = 0; i2 < size; i2++) {
                int iKeyAt = uidStats.keyAt(i2);
                ArrayList arrayList2 = (ArrayList) sparseArray.get(iKeyAt);
                if (arrayList2 != null) {
                    for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                        strArr[0] = Integer.toString(iKeyAt);
                        strArr[1] = (String) arrayList2.get(i3);
                        dumpLine(printWriter, 0, "i", "uid", strArr);
                    }
                }
            }
        }
        if (z) {
            dumpCheckinLocked(printWriter, 3, -1);
        } else {
            dumpCheckinLocked(printWriter, 0, -1);
            dumpCheckinLocked(printWriter, 3, -1);
        }
    }
}
