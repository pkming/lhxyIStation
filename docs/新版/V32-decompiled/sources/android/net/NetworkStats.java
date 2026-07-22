package android.net;

import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.SparseBooleanArray;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Objects;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class NetworkStats implements Parcelable {
    public static final Parcelable.Creator<NetworkStats> CREATOR = new Parcelable.Creator<NetworkStats>() { // from class: android.net.NetworkStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkStats createFromParcel(Parcel parcel) {
            return new NetworkStats(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkStats[] newArray(int i) {
            return new NetworkStats[i];
        }
    };
    public static final String IFACE_ALL = null;
    public static final int SET_ALL = -1;
    public static final int SET_DEFAULT = 0;
    public static final int SET_FOREGROUND = 1;
    public static final int TAG_NONE = 0;
    public static final int UID_ALL = -1;
    private final long elapsedRealtime;
    private String[] iface;
    private long[] operations;
    private long[] rxBytes;
    private long[] rxPackets;
    private int[] set;
    private int size;
    private int[] tag;
    private long[] txBytes;
    private long[] txPackets;
    private int[] uid;

    public interface NonMonotonicObserver<C> {
        void foundNonMonotonic(NetworkStats networkStats, int i, NetworkStats networkStats2, int i2, C c);
    }

    public static String setToString(int i) {
        return i != -1 ? i != 0 ? i != 1 ? MediaPlayer.CHARSET_UNKNOWN : "FOREGROUND" : "DEFAULT" : "ALL";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static class Entry {
        public String iface;
        public long operations;
        public long rxBytes;
        public long rxPackets;
        public int set;
        public int tag;
        public long txBytes;
        public long txPackets;
        public int uid;

        public Entry() {
            this(NetworkStats.IFACE_ALL, -1, 0, 0, 0L, 0L, 0L, 0L, 0L);
        }

        public Entry(long j, long j2, long j3, long j4, long j5) {
            this(NetworkStats.IFACE_ALL, -1, 0, 0, j, j2, j3, j4, j5);
        }

        public Entry(String str, int i, int i2, int i3, long j, long j2, long j3, long j4, long j5) {
            this.iface = str;
            this.uid = i;
            this.set = i2;
            this.tag = i3;
            this.rxBytes = j;
            this.rxPackets = j2;
            this.txBytes = j3;
            this.txPackets = j4;
            this.operations = j5;
        }

        public boolean isNegative() {
            return this.rxBytes < 0 || this.rxPackets < 0 || this.txBytes < 0 || this.txPackets < 0 || this.operations < 0;
        }

        public boolean isEmpty() {
            return this.rxBytes == 0 && this.rxPackets == 0 && this.txBytes == 0 && this.txPackets == 0 && this.operations == 0;
        }

        public void add(Entry entry) {
            this.rxBytes += entry.rxBytes;
            this.rxPackets += entry.rxPackets;
            this.txBytes += entry.txBytes;
            this.txPackets += entry.txPackets;
            this.operations += entry.operations;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("iface=").append(this.iface);
            sb.append(" uid=").append(this.uid);
            sb.append(" set=").append(NetworkStats.setToString(this.set));
            sb.append(" tag=").append(NetworkStats.tagToString(this.tag));
            sb.append(" rxBytes=").append(this.rxBytes);
            sb.append(" rxPackets=").append(this.rxPackets);
            sb.append(" txBytes=").append(this.txBytes);
            sb.append(" txPackets=").append(this.txPackets);
            sb.append(" operations=").append(this.operations);
            return sb.toString();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            return this.uid == entry.uid && this.set == entry.set && this.tag == entry.tag && this.rxBytes == entry.rxBytes && this.rxPackets == entry.rxPackets && this.txBytes == entry.txBytes && this.txPackets == entry.txPackets && this.operations == entry.operations && this.iface.equals(entry.iface);
        }
    }

    public NetworkStats(long j, int i) {
        this.elapsedRealtime = j;
        this.size = 0;
        this.iface = new String[i];
        this.uid = new int[i];
        this.set = new int[i];
        this.tag = new int[i];
        this.rxBytes = new long[i];
        this.rxPackets = new long[i];
        this.txBytes = new long[i];
        this.txPackets = new long[i];
        this.operations = new long[i];
    }

    public NetworkStats(Parcel parcel) {
        this.elapsedRealtime = parcel.readLong();
        this.size = parcel.readInt();
        this.iface = parcel.createStringArray();
        this.uid = parcel.createIntArray();
        this.set = parcel.createIntArray();
        this.tag = parcel.createIntArray();
        this.rxBytes = parcel.createLongArray();
        this.rxPackets = parcel.createLongArray();
        this.txBytes = parcel.createLongArray();
        this.txPackets = parcel.createLongArray();
        this.operations = parcel.createLongArray();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.elapsedRealtime);
        parcel.writeInt(this.size);
        parcel.writeStringArray(this.iface);
        parcel.writeIntArray(this.uid);
        parcel.writeIntArray(this.set);
        parcel.writeIntArray(this.tag);
        parcel.writeLongArray(this.rxBytes);
        parcel.writeLongArray(this.rxPackets);
        parcel.writeLongArray(this.txBytes);
        parcel.writeLongArray(this.txPackets);
        parcel.writeLongArray(this.operations);
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public NetworkStats m10clone() {
        NetworkStats networkStats = new NetworkStats(this.elapsedRealtime, this.size);
        Entry values = null;
        for (int i = 0; i < this.size; i++) {
            values = getValues(i, values);
            networkStats.addValues(values);
        }
        return networkStats;
    }

    public NetworkStats addIfaceValues(String str, long j, long j2, long j3, long j4) {
        return addValues(str, -1, 0, 0, j, j2, j3, j4, 0L);
    }

    public NetworkStats addValues(String str, int i, int i2, int i3, long j, long j2, long j3, long j4, long j5) {
        return addValues(new Entry(str, i, i2, i3, j, j2, j3, j4, j5));
    }

    public NetworkStats addValues(Entry entry) {
        int i = this.size;
        String[] strArr = this.iface;
        if (i >= strArr.length) {
            int iMax = (Math.max(strArr.length, 10) * 3) / 2;
            this.iface = (String[]) Arrays.copyOf(this.iface, iMax);
            this.uid = Arrays.copyOf(this.uid, iMax);
            this.set = Arrays.copyOf(this.set, iMax);
            this.tag = Arrays.copyOf(this.tag, iMax);
            this.rxBytes = Arrays.copyOf(this.rxBytes, iMax);
            this.rxPackets = Arrays.copyOf(this.rxPackets, iMax);
            this.txBytes = Arrays.copyOf(this.txBytes, iMax);
            this.txPackets = Arrays.copyOf(this.txPackets, iMax);
            this.operations = Arrays.copyOf(this.operations, iMax);
        }
        this.iface[this.size] = entry.iface;
        this.uid[this.size] = entry.uid;
        this.set[this.size] = entry.set;
        this.tag[this.size] = entry.tag;
        this.rxBytes[this.size] = entry.rxBytes;
        this.rxPackets[this.size] = entry.rxPackets;
        this.txBytes[this.size] = entry.txBytes;
        this.txPackets[this.size] = entry.txPackets;
        this.operations[this.size] = entry.operations;
        this.size++;
        return this;
    }

    public Entry getValues(int i, Entry entry) {
        if (entry == null) {
            entry = new Entry();
        }
        entry.iface = this.iface[i];
        entry.uid = this.uid[i];
        entry.set = this.set[i];
        entry.tag = this.tag[i];
        entry.rxBytes = this.rxBytes[i];
        entry.rxPackets = this.rxPackets[i];
        entry.txBytes = this.txBytes[i];
        entry.txPackets = this.txPackets[i];
        entry.operations = this.operations[i];
        return entry;
    }

    public long getElapsedRealtime() {
        return this.elapsedRealtime;
    }

    public long getElapsedRealtimeAge() {
        return SystemClock.elapsedRealtime() - this.elapsedRealtime;
    }

    public int size() {
        return this.size;
    }

    public int internalSize() {
        return this.iface.length;
    }

    @Deprecated
    public NetworkStats combineValues(String str, int i, int i2, long j, long j2, long j3, long j4, long j5) {
        return combineValues(str, i, 0, i2, j, j2, j3, j4, j5);
    }

    public NetworkStats combineValues(String str, int i, int i2, int i3, long j, long j2, long j3, long j4, long j5) {
        return combineValues(new Entry(str, i, i2, i3, j, j2, j3, j4, j5));
    }

    public NetworkStats combineValues(Entry entry) {
        int iFindIndex = findIndex(entry.iface, entry.uid, entry.set, entry.tag);
        if (iFindIndex == -1) {
            addValues(entry);
        } else {
            long[] jArr = this.rxBytes;
            jArr[iFindIndex] = jArr[iFindIndex] + entry.rxBytes;
            long[] jArr2 = this.rxPackets;
            jArr2[iFindIndex] = jArr2[iFindIndex] + entry.rxPackets;
            long[] jArr3 = this.txBytes;
            jArr3[iFindIndex] = jArr3[iFindIndex] + entry.txBytes;
            long[] jArr4 = this.txPackets;
            jArr4[iFindIndex] = jArr4[iFindIndex] + entry.txPackets;
            long[] jArr5 = this.operations;
            jArr5[iFindIndex] = jArr5[iFindIndex] + entry.operations;
        }
        return this;
    }

    public void combineAllValues(NetworkStats networkStats) {
        Entry values = null;
        for (int i = 0; i < networkStats.size; i++) {
            values = networkStats.getValues(i, values);
            combineValues(values);
        }
    }

    public int findIndex(String str, int i, int i2, int i3) {
        for (int i4 = 0; i4 < this.size; i4++) {
            if (i == this.uid[i4] && i2 == this.set[i4] && i3 == this.tag[i4] && Objects.equal(str, this.iface[i4])) {
                return i4;
            }
        }
        return -1;
    }

    public int findIndexHinted(String str, int i, int i2, int i3, int i4) {
        int i5;
        int i6 = 0;
        while (true) {
            int i7 = this.size;
            if (i6 >= i7) {
                return -1;
            }
            int i8 = i6 / 2;
            if (i6 % 2 == 0) {
                i5 = (i8 + i4) % i7;
            } else {
                i5 = (((i7 + i4) - i8) - 1) % i7;
            }
            if (i == this.uid[i5] && i2 == this.set[i5] && i3 == this.tag[i5] && Objects.equal(str, this.iface[i5])) {
                return i5;
            }
            i6++;
        }
    }

    public void spliceOperationsFrom(NetworkStats networkStats) {
        for (int i = 0; i < this.size; i++) {
            int iFindIndex = networkStats.findIndex(this.iface[i], this.uid[i], this.set[i], this.tag[i]);
            if (iFindIndex == -1) {
                this.operations[i] = 0;
            } else {
                this.operations[i] = networkStats.operations[iFindIndex];
            }
        }
    }

    public String[] getUniqueIfaces() {
        HashSet hashSet = new HashSet();
        for (String str : this.iface) {
            if (str != IFACE_ALL) {
                hashSet.add(str);
            }
        }
        return (String[]) hashSet.toArray(new String[hashSet.size()]);
    }

    public int[] getUniqueUids() {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        for (int i : this.uid) {
            sparseBooleanArray.put(i, true);
        }
        int size = sparseBooleanArray.size();
        int[] iArr = new int[size];
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = sparseBooleanArray.keyAt(i2);
        }
        return iArr;
    }

    public long getTotalBytes() {
        Entry total = getTotal(null);
        return total.rxBytes + total.txBytes;
    }

    public Entry getTotal(Entry entry) {
        return getTotal(entry, null, -1, false);
    }

    public Entry getTotal(Entry entry, int i) {
        return getTotal(entry, null, i, false);
    }

    public Entry getTotal(Entry entry, HashSet<String> hashSet) {
        return getTotal(entry, hashSet, -1, false);
    }

    public Entry getTotalIncludingTags(Entry entry) {
        return getTotal(entry, null, -1, true);
    }

    private Entry getTotal(Entry entry, HashSet<String> hashSet, int i, boolean z) {
        if (entry == null) {
            entry = new Entry();
        }
        entry.iface = IFACE_ALL;
        entry.uid = i;
        entry.set = -1;
        entry.tag = 0;
        entry.rxBytes = 0L;
        entry.rxPackets = 0L;
        entry.txBytes = 0L;
        entry.txPackets = 0L;
        entry.operations = 0L;
        for (int i2 = 0; i2 < this.size; i2++) {
            boolean z2 = true;
            boolean z3 = i == -1 || i == this.uid[i2];
            if (hashSet != null && !hashSet.contains(this.iface[i2])) {
                z2 = false;
            }
            if (z3 && z2 && (this.tag[i2] == 0 || z)) {
                entry.rxBytes += this.rxBytes[i2];
                entry.rxPackets += this.rxPackets[i2];
                entry.txBytes += this.txBytes[i2];
                entry.txPackets += this.txPackets[i2];
                entry.operations += this.operations[i2];
            }
        }
        return entry;
    }

    public NetworkStats subtract(NetworkStats networkStats) {
        return subtract(this, networkStats, null, null);
    }

    public static <C> NetworkStats subtract(NetworkStats networkStats, NetworkStats networkStats2, NonMonotonicObserver<C> nonMonotonicObserver, C c) {
        long j = networkStats.elapsedRealtime - networkStats2.elapsedRealtime;
        if (j < 0) {
            if (nonMonotonicObserver != null) {
                nonMonotonicObserver.foundNonMonotonic(networkStats, -1, networkStats2, -1, c);
            }
            j = 0;
        }
        Entry entry = new Entry();
        NetworkStats networkStats3 = new NetworkStats(j, networkStats.size);
        for (int i = 0; i < networkStats.size; i++) {
            entry.iface = networkStats.iface[i];
            entry.uid = networkStats.uid[i];
            entry.set = networkStats.set[i];
            entry.tag = networkStats.tag[i];
            int iFindIndexHinted = networkStats2.findIndexHinted(entry.iface, entry.uid, entry.set, entry.tag, i);
            if (iFindIndexHinted == -1) {
                entry.rxBytes = networkStats.rxBytes[i];
                entry.rxPackets = networkStats.rxPackets[i];
                entry.txBytes = networkStats.txBytes[i];
                entry.txPackets = networkStats.txPackets[i];
                entry.operations = networkStats.operations[i];
            } else {
                entry.rxBytes = networkStats.rxBytes[i] - networkStats2.rxBytes[iFindIndexHinted];
                entry.rxPackets = networkStats.rxPackets[i] - networkStats2.rxPackets[iFindIndexHinted];
                entry.txBytes = networkStats.txBytes[i] - networkStats2.txBytes[iFindIndexHinted];
                entry.txPackets = networkStats.txPackets[i] - networkStats2.txPackets[iFindIndexHinted];
                entry.operations = networkStats.operations[i] - networkStats2.operations[iFindIndexHinted];
                if (entry.rxBytes < 0 || entry.rxPackets < 0 || entry.txBytes < 0 || entry.txPackets < 0 || entry.operations < 0) {
                    if (nonMonotonicObserver != null) {
                        nonMonotonicObserver.foundNonMonotonic(networkStats, i, networkStats2, iFindIndexHinted, c);
                    }
                    entry.rxBytes = Math.max(entry.rxBytes, 0L);
                    entry.rxPackets = Math.max(entry.rxPackets, 0L);
                    entry.txBytes = Math.max(entry.txBytes, 0L);
                    entry.txPackets = Math.max(entry.txPackets, 0L);
                    entry.operations = Math.max(entry.operations, 0L);
                }
            }
            networkStats3.addValues(entry);
        }
        return networkStats3;
    }

    public NetworkStats groupedByIface() {
        NetworkStats networkStats = new NetworkStats(this.elapsedRealtime, 10);
        Entry entry = new Entry();
        entry.uid = -1;
        entry.set = -1;
        entry.tag = 0;
        entry.operations = 0L;
        for (int i = 0; i < this.size; i++) {
            if (this.tag[i] == 0) {
                entry.iface = this.iface[i];
                entry.rxBytes = this.rxBytes[i];
                entry.rxPackets = this.rxPackets[i];
                entry.txBytes = this.txBytes[i];
                entry.txPackets = this.txPackets[i];
                networkStats.combineValues(entry);
            }
        }
        return networkStats;
    }

    public NetworkStats groupedByUid() {
        NetworkStats networkStats = new NetworkStats(this.elapsedRealtime, 10);
        Entry entry = new Entry();
        entry.iface = IFACE_ALL;
        entry.set = -1;
        entry.tag = 0;
        for (int i = 0; i < this.size; i++) {
            if (this.tag[i] == 0) {
                entry.uid = this.uid[i];
                entry.rxBytes = this.rxBytes[i];
                entry.rxPackets = this.rxPackets[i];
                entry.txBytes = this.txBytes[i];
                entry.txPackets = this.txPackets[i];
                entry.operations = this.operations[i];
                networkStats.combineValues(entry);
            }
        }
        return networkStats;
    }

    public NetworkStats withoutUids(int[] iArr) {
        NetworkStats networkStats = new NetworkStats(this.elapsedRealtime, 10);
        Entry entry = new Entry();
        for (int i = 0; i < this.size; i++) {
            entry = getValues(i, entry);
            if (!ArrayUtils.contains(iArr, entry.uid)) {
                networkStats.addValues(entry);
            }
        }
        return networkStats;
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("NetworkStats: elapsedRealtime=");
        printWriter.println(this.elapsedRealtime);
        for (int i = 0; i < this.size; i++) {
            printWriter.print(str);
            printWriter.print("  [");
            printWriter.print(i);
            printWriter.print("]");
            printWriter.print(" iface=");
            printWriter.print(this.iface[i]);
            printWriter.print(" uid=");
            printWriter.print(this.uid[i]);
            printWriter.print(" set=");
            printWriter.print(setToString(this.set[i]));
            printWriter.print(" tag=");
            printWriter.print(tagToString(this.tag[i]));
            printWriter.print(" rxBytes=");
            printWriter.print(this.rxBytes[i]);
            printWriter.print(" rxPackets=");
            printWriter.print(this.rxPackets[i]);
            printWriter.print(" txBytes=");
            printWriter.print(this.txBytes[i]);
            printWriter.print(" txPackets=");
            printWriter.print(this.txPackets[i]);
            printWriter.print(" operations=");
            printWriter.println(this.operations[i]);
        }
    }

    public static String tagToString(int i) {
        return "0x" + Integer.toHexString(i);
    }

    public String toString() {
        CharArrayWriter charArrayWriter = new CharArrayWriter();
        dump("", new PrintWriter(charArrayWriter));
        return charArrayWriter.toString();
    }
}
