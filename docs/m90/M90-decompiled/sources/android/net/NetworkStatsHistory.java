package android.net;

import android.net.NetworkStats;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Trace;
import android.util.MathUtils;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import java.io.CharArrayWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Arrays;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class NetworkStatsHistory implements Parcelable {
    public static final Parcelable.Creator<NetworkStatsHistory> CREATOR = new Parcelable.Creator<NetworkStatsHistory>() { // from class: android.net.NetworkStatsHistory.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkStatsHistory createFromParcel(Parcel parcel) {
            return new NetworkStatsHistory(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkStatsHistory[] newArray(int i) {
            return new NetworkStatsHistory[i];
        }
    };
    public static final int FIELD_ACTIVE_TIME = 1;
    public static final int FIELD_ALL = -1;
    public static final int FIELD_OPERATIONS = 32;
    public static final int FIELD_RX_BYTES = 2;
    public static final int FIELD_RX_PACKETS = 4;
    public static final int FIELD_TX_BYTES = 8;
    public static final int FIELD_TX_PACKETS = 16;
    private static final int VERSION_ADD_ACTIVE = 3;
    private static final int VERSION_ADD_PACKETS = 2;
    private static final int VERSION_INIT = 1;
    private long[] activeTime;
    private int bucketCount;
    private long bucketDuration;
    private long[] bucketStart;
    private long[] operations;
    private long[] rxBytes;
    private long[] rxPackets;
    private long totalBytes;
    private long[] txBytes;
    private long[] txPackets;

    public static class Entry {
        public static final long UNKNOWN = -1;
        public long activeTime;
        public long bucketDuration;
        public long bucketStart;
        public long operations;
        public long rxBytes;
        public long rxPackets;
        public long txBytes;
        public long txPackets;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NetworkStatsHistory(long j) {
        this(j, 10, -1);
    }

    public NetworkStatsHistory(long j, int i) {
        this(j, i, -1);
    }

    public NetworkStatsHistory(long j, int i, int i2) {
        this.bucketDuration = j;
        this.bucketStart = new long[i];
        if ((i2 & 1) != 0) {
            this.activeTime = new long[i];
        }
        if ((i2 & 2) != 0) {
            this.rxBytes = new long[i];
        }
        if ((i2 & 4) != 0) {
            this.rxPackets = new long[i];
        }
        if ((i2 & 8) != 0) {
            this.txBytes = new long[i];
        }
        if ((i2 & 16) != 0) {
            this.txPackets = new long[i];
        }
        if ((i2 & 32) != 0) {
            this.operations = new long[i];
        }
        this.bucketCount = 0;
        this.totalBytes = 0L;
    }

    public NetworkStatsHistory(NetworkStatsHistory networkStatsHistory, long j) {
        this(j, networkStatsHistory.estimateResizeBuckets(j));
        recordEntireHistory(networkStatsHistory);
    }

    public NetworkStatsHistory(Parcel parcel) {
        this.bucketDuration = parcel.readLong();
        this.bucketStart = ParcelUtils.readLongArray(parcel);
        this.activeTime = ParcelUtils.readLongArray(parcel);
        this.rxBytes = ParcelUtils.readLongArray(parcel);
        this.rxPackets = ParcelUtils.readLongArray(parcel);
        this.txBytes = ParcelUtils.readLongArray(parcel);
        this.txPackets = ParcelUtils.readLongArray(parcel);
        this.operations = ParcelUtils.readLongArray(parcel);
        this.bucketCount = this.bucketStart.length;
        this.totalBytes = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.bucketDuration);
        ParcelUtils.writeLongArray(parcel, this.bucketStart, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.activeTime, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.rxBytes, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.rxPackets, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.txBytes, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.txPackets, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.operations, this.bucketCount);
        parcel.writeLong(this.totalBytes);
    }

    public NetworkStatsHistory(DataInputStream dataInputStream) throws IOException {
        int i = dataInputStream.readInt();
        if (i == 1) {
            this.bucketDuration = dataInputStream.readLong();
            this.bucketStart = DataStreamUtils.readFullLongArray(dataInputStream);
            this.rxBytes = DataStreamUtils.readFullLongArray(dataInputStream);
            this.rxPackets = new long[this.bucketStart.length];
            this.txBytes = DataStreamUtils.readFullLongArray(dataInputStream);
            long[] jArr = this.bucketStart;
            this.txPackets = new long[jArr.length];
            this.operations = new long[jArr.length];
            this.bucketCount = jArr.length;
            this.totalBytes = ArrayUtils.total(this.rxBytes) + ArrayUtils.total(this.txBytes);
        } else if (i == 2 || i == 3) {
            this.bucketDuration = dataInputStream.readLong();
            long[] varLongArray = DataStreamUtils.readVarLongArray(dataInputStream);
            this.bucketStart = varLongArray;
            this.activeTime = i >= 3 ? DataStreamUtils.readVarLongArray(dataInputStream) : new long[varLongArray.length];
            this.rxBytes = DataStreamUtils.readVarLongArray(dataInputStream);
            this.rxPackets = DataStreamUtils.readVarLongArray(dataInputStream);
            this.txBytes = DataStreamUtils.readVarLongArray(dataInputStream);
            this.txPackets = DataStreamUtils.readVarLongArray(dataInputStream);
            this.operations = DataStreamUtils.readVarLongArray(dataInputStream);
            this.bucketCount = this.bucketStart.length;
            this.totalBytes = ArrayUtils.total(this.rxBytes) + ArrayUtils.total(this.txBytes);
        } else {
            throw new ProtocolException("unexpected version: " + i);
        }
        int length = this.bucketStart.length;
        int i2 = this.bucketCount;
        if (length != i2 || this.rxBytes.length != i2 || this.rxPackets.length != i2 || this.txBytes.length != i2 || this.txPackets.length != i2 || this.operations.length != i2) {
            throw new ProtocolException("Mismatched history lengths");
        }
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(3);
        dataOutputStream.writeLong(this.bucketDuration);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.bucketStart, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.activeTime, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.rxBytes, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.rxPackets, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.txBytes, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.txPackets, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.operations, this.bucketCount);
    }

    public int size() {
        return this.bucketCount;
    }

    public long getBucketDuration() {
        return this.bucketDuration;
    }

    public long getStart() {
        return this.bucketCount > 0 ? this.bucketStart[0] : LinkQualityInfo.UNKNOWN_LONG;
    }

    public long getEnd() {
        int i = this.bucketCount;
        if (i > 0) {
            return this.bucketStart[i - 1] + this.bucketDuration;
        }
        return Long.MIN_VALUE;
    }

    public long getTotalBytes() {
        return this.totalBytes;
    }

    public int getIndexBefore(long j) {
        int iBinarySearch = Arrays.binarySearch(this.bucketStart, 0, this.bucketCount, j);
        return MathUtils.constrain(iBinarySearch < 0 ? (~iBinarySearch) - 1 : iBinarySearch - 1, 0, this.bucketCount - 1);
    }

    public int getIndexAfter(long j) {
        int iBinarySearch = Arrays.binarySearch(this.bucketStart, 0, this.bucketCount, j);
        return MathUtils.constrain(iBinarySearch < 0 ? ~iBinarySearch : iBinarySearch + 1, 0, this.bucketCount - 1);
    }

    public Entry getValues(int i, Entry entry) {
        if (entry == null) {
            entry = new Entry();
        }
        entry.bucketStart = this.bucketStart[i];
        entry.bucketDuration = this.bucketDuration;
        entry.activeTime = getLong(this.activeTime, i, -1L);
        entry.rxBytes = getLong(this.rxBytes, i, -1L);
        entry.rxPackets = getLong(this.rxPackets, i, -1L);
        entry.txBytes = getLong(this.txBytes, i, -1L);
        entry.txPackets = getLong(this.txPackets, i, -1L);
        entry.operations = getLong(this.operations, i, -1L);
        return entry;
    }

    @Deprecated
    public void recordData(long j, long j2, long j3, long j4) {
        recordData(j, j2, new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, 0, 0, j3, 0L, j4, 0L, 0L));
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0050  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void recordData(long r29, long r31, android.net.NetworkStats.Entry r33) {
        /*
            r28 = this;
            r0 = r28
            r1 = r29
            r3 = r31
            r5 = r33
            long r6 = r5.rxBytes
            long r8 = r5.rxPackets
            long r10 = r5.txBytes
            long r12 = r5.txPackets
            long r14 = r5.operations
            boolean r16 = r33.isNegative()
            if (r16 != 0) goto Lb0
            boolean r16 = r33.isEmpty()
            if (r16 == 0) goto L1f
            return
        L1f:
            r28.ensureBuckets(r29, r31)
            long r16 = r3 - r1
            int r18 = r0.getIndexAfter(r3)
            r5 = r18
        L2a:
            if (r5 < 0) goto La3
            r18 = r14
            long[] r14 = r0.bucketStart
            r20 = r12
            r12 = r14[r5]
            long r14 = r0.bucketDuration
            long r14 = r14 + r12
            int r22 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r22 >= 0) goto L3c
            goto La3
        L3c:
            int r22 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r22 <= 0) goto L41
            goto L50
        L41:
            long r14 = java.lang.Math.min(r14, r3)
            long r12 = java.lang.Math.max(r12, r1)
            long r14 = r14 - r12
            r12 = 0
            int r12 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r12 > 0) goto L55
        L50:
            r14 = r18
            r12 = r20
            goto L9c
        L55:
            long r12 = r6 * r14
            long r12 = r12 / r16
            long r22 = r8 * r14
            long r1 = r22 / r16
            long r22 = r10 * r14
            long r3 = r22 / r16
            long r22 = r20 * r14
            r24 = r10
            long r10 = r22 / r16
            long r22 = r18 * r14
            r26 = r10
            long r10 = r22 / r16
            r22 = r10
            long[] r10 = r0.activeTime
            addLong(r10, r5, r14)
            long[] r10 = r0.rxBytes
            addLong(r10, r5, r12)
            long r6 = r6 - r12
            long[] r10 = r0.rxPackets
            addLong(r10, r5, r1)
            long r8 = r8 - r1
            long[] r1 = r0.txBytes
            addLong(r1, r5, r3)
            long r10 = r24 - r3
            long[] r1 = r0.txPackets
            r2 = r26
            addLong(r1, r5, r2)
            long r12 = r20 - r2
            long[] r1 = r0.operations
            r2 = r22
            addLong(r1, r5, r2)
            long r1 = r18 - r2
            long r16 = r16 - r14
            r14 = r1
        L9c:
            int r5 = r5 + (-1)
            r1 = r29
            r3 = r31
            goto L2a
        La3:
            long r1 = r0.totalBytes
            r3 = r33
            long r4 = r3.rxBytes
            long r6 = r3.txBytes
            long r4 = r4 + r6
            long r1 = r1 + r4
            r0.totalBytes = r1
            return
        Lb0:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "tried recording negative data"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.NetworkStatsHistory.recordData(long, long, android.net.NetworkStats$Entry):void");
    }

    public void recordEntireHistory(NetworkStatsHistory networkStatsHistory) {
        recordHistory(networkStatsHistory, Long.MIN_VALUE, LinkQualityInfo.UNKNOWN_LONG);
    }

    public void recordHistory(NetworkStatsHistory networkStatsHistory, long j, long j2) {
        NetworkStats.Entry entry;
        NetworkStats.Entry entry2 = entry;
        NetworkStats.Entry entry3 = new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, 0, 0, 0L, 0L, 0L, 0L, 0L);
        int i = 0;
        while (i < networkStatsHistory.bucketCount) {
            long j3 = networkStatsHistory.bucketStart[i];
            long j4 = networkStatsHistory.bucketDuration + j3;
            if (j3 < j || j4 > j2) {
                entry = entry2;
            } else {
                entry = entry2;
                entry.rxBytes = getLong(networkStatsHistory.rxBytes, i, 0L);
                entry.rxPackets = getLong(networkStatsHistory.rxPackets, i, 0L);
                entry.txBytes = getLong(networkStatsHistory.txBytes, i, 0L);
                entry.txPackets = getLong(networkStatsHistory.txPackets, i, 0L);
                entry.operations = getLong(networkStatsHistory.operations, i, 0L);
                recordData(j3, j4, entry);
            }
            i++;
            entry2 = entry;
        }
    }

    private void ensureBuckets(long j, long j2) {
        long j3 = this.bucketDuration;
        long j4 = j - (j % j3);
        long j5 = j2 + ((j3 - (j2 % j3)) % j3);
        while (j4 < j5) {
            int iBinarySearch = Arrays.binarySearch(this.bucketStart, 0, this.bucketCount, j4);
            if (iBinarySearch < 0) {
                insertBucket(~iBinarySearch, j4);
            }
            j4 += this.bucketDuration;
        }
    }

    private void insertBucket(int i, long j) {
        int i2 = this.bucketCount;
        long[] jArr = this.bucketStart;
        if (i2 >= jArr.length) {
            int iMax = (Math.max(jArr.length, 10) * 3) / 2;
            this.bucketStart = Arrays.copyOf(this.bucketStart, iMax);
            long[] jArr2 = this.activeTime;
            if (jArr2 != null) {
                this.activeTime = Arrays.copyOf(jArr2, iMax);
            }
            long[] jArr3 = this.rxBytes;
            if (jArr3 != null) {
                this.rxBytes = Arrays.copyOf(jArr3, iMax);
            }
            long[] jArr4 = this.rxPackets;
            if (jArr4 != null) {
                this.rxPackets = Arrays.copyOf(jArr4, iMax);
            }
            long[] jArr5 = this.txBytes;
            if (jArr5 != null) {
                this.txBytes = Arrays.copyOf(jArr5, iMax);
            }
            long[] jArr6 = this.txPackets;
            if (jArr6 != null) {
                this.txPackets = Arrays.copyOf(jArr6, iMax);
            }
            long[] jArr7 = this.operations;
            if (jArr7 != null) {
                this.operations = Arrays.copyOf(jArr7, iMax);
            }
        }
        int i3 = this.bucketCount;
        if (i < i3) {
            int i4 = i + 1;
            int i5 = i3 - i;
            long[] jArr8 = this.bucketStart;
            System.arraycopy(jArr8, i, jArr8, i4, i5);
            long[] jArr9 = this.activeTime;
            if (jArr9 != null) {
                System.arraycopy(jArr9, i, jArr9, i4, i5);
            }
            long[] jArr10 = this.rxBytes;
            if (jArr10 != null) {
                System.arraycopy(jArr10, i, jArr10, i4, i5);
            }
            long[] jArr11 = this.rxPackets;
            if (jArr11 != null) {
                System.arraycopy(jArr11, i, jArr11, i4, i5);
            }
            long[] jArr12 = this.txBytes;
            if (jArr12 != null) {
                System.arraycopy(jArr12, i, jArr12, i4, i5);
            }
            long[] jArr13 = this.txPackets;
            if (jArr13 != null) {
                System.arraycopy(jArr13, i, jArr13, i4, i5);
            }
            long[] jArr14 = this.operations;
            if (jArr14 != null) {
                System.arraycopy(jArr14, i, jArr14, i4, i5);
            }
        }
        this.bucketStart[i] = j;
        setLong(this.activeTime, i, 0L);
        setLong(this.rxBytes, i, 0L);
        setLong(this.rxPackets, i, 0L);
        setLong(this.txBytes, i, 0L);
        setLong(this.txPackets, i, 0L);
        setLong(this.operations, i, 0L);
        this.bucketCount++;
    }

    @Deprecated
    public void removeBucketsBefore(long j) {
        int i = 0;
        while (i < this.bucketCount && this.bucketStart[i] + this.bucketDuration <= j) {
            i++;
        }
        if (i > 0) {
            long[] jArr = this.bucketStart;
            int length = jArr.length;
            this.bucketStart = Arrays.copyOfRange(jArr, i, length);
            long[] jArr2 = this.activeTime;
            if (jArr2 != null) {
                this.activeTime = Arrays.copyOfRange(jArr2, i, length);
            }
            long[] jArr3 = this.rxBytes;
            if (jArr3 != null) {
                this.rxBytes = Arrays.copyOfRange(jArr3, i, length);
            }
            long[] jArr4 = this.rxPackets;
            if (jArr4 != null) {
                this.rxPackets = Arrays.copyOfRange(jArr4, i, length);
            }
            long[] jArr5 = this.txBytes;
            if (jArr5 != null) {
                this.txBytes = Arrays.copyOfRange(jArr5, i, length);
            }
            long[] jArr6 = this.txPackets;
            if (jArr6 != null) {
                this.txPackets = Arrays.copyOfRange(jArr6, i, length);
            }
            long[] jArr7 = this.operations;
            if (jArr7 != null) {
                this.operations = Arrays.copyOfRange(jArr7, i, length);
            }
            this.bucketCount -= i;
        }
    }

    public Entry getValues(long j, long j2, Entry entry) {
        return getValues(j, j2, LinkQualityInfo.UNKNOWN_LONG, entry);
    }

    public Entry getValues(long j, long j2, long j3, Entry entry) {
        Entry entry2 = entry != null ? entry : new Entry();
        entry2.bucketDuration = j2 - j;
        entry2.bucketStart = j;
        long j4 = 0;
        entry2.activeTime = this.activeTime != null ? 0L : -1L;
        entry2.rxBytes = this.rxBytes != null ? 0L : -1L;
        entry2.rxPackets = this.rxPackets != null ? 0L : -1L;
        entry2.txBytes = this.txBytes != null ? 0L : -1L;
        entry2.txPackets = this.txPackets != null ? 0L : -1L;
        entry2.operations = this.operations != null ? 0L : -1L;
        int indexAfter = getIndexAfter(j2);
        while (indexAfter >= 0) {
            long j5 = this.bucketStart[indexAfter];
            long j6 = this.bucketDuration;
            long j7 = j5 + j6;
            if (j7 <= j) {
                break;
            }
            if (j5 < j2) {
                if (!(j5 < j3 && j7 > j3)) {
                    if (j7 >= j2) {
                        j7 = j2;
                    }
                    if (j5 <= j) {
                        j5 = j;
                    }
                    j6 = j7 - j5;
                }
                if (j6 > j4) {
                    if (this.activeTime != null) {
                        entry2.activeTime += (this.activeTime[indexAfter] * j6) / this.bucketDuration;
                    }
                    if (this.rxBytes != null) {
                        entry2.rxBytes += (this.rxBytes[indexAfter] * j6) / this.bucketDuration;
                    }
                    if (this.rxPackets != null) {
                        entry2.rxPackets += (this.rxPackets[indexAfter] * j6) / this.bucketDuration;
                    }
                    if (this.txBytes != null) {
                        entry2.txBytes += (this.txBytes[indexAfter] * j6) / this.bucketDuration;
                    }
                    if (this.txPackets != null) {
                        entry2.txPackets += (this.txPackets[indexAfter] * j6) / this.bucketDuration;
                    }
                    if (this.operations != null) {
                        entry2.operations += (this.operations[indexAfter] * j6) / this.bucketDuration;
                    }
                }
            }
            indexAfter--;
            j4 = 0;
        }
        return entry2;
    }

    @Deprecated
    public void generateRandom(long j, long j2, long j3) {
        Random random = new Random();
        float fNextFloat = random.nextFloat();
        float f = j3;
        long j4 = (long) (f * fNextFloat);
        long j5 = (long) (f * (1.0f - fNextFloat));
        generateRandom(j, j2, j4, j4 / 1024, j5, j5 / 1024, j4 / Trace.TRACE_TAG_HAL, random);
    }

    @Deprecated
    public void generateRandom(long j, long j2, long j3, long j4, long j5, long j6, long j7, Random random) {
        long j8 = j2;
        ensureBuckets(j, j2);
        NetworkStats.Entry entry = new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, 0, 0, 0L, 0L, 0L, 0L, 0L);
        long j9 = j3;
        long j10 = j4;
        long j11 = j5;
        long j12 = j6;
        long j13 = j7;
        while (true) {
            if (j9 <= 1024 && j10 <= 128 && j11 <= 1024 && j12 <= 128 && j13 <= 32) {
                return;
            }
            long jRandomLong = randomLong(random, j, j8);
            long jRandomLong2 = randomLong(random, 0L, (j8 - jRandomLong) / 2) + jRandomLong;
            entry.rxBytes = randomLong(random, 0L, j9);
            entry.rxPackets = randomLong(random, 0L, j10);
            entry.txBytes = randomLong(random, 0L, j11);
            entry.txPackets = randomLong(random, 0L, j12);
            entry.operations = randomLong(random, 0L, j13);
            j9 -= entry.rxBytes;
            j10 -= entry.rxPackets;
            j11 -= entry.txBytes;
            j12 -= entry.txPackets;
            j13 -= entry.operations;
            recordData(jRandomLong, jRandomLong2, entry);
            j8 = j2;
        }
    }

    public static long randomLong(Random random, long j, long j2) {
        return (long) (j + (random.nextFloat() * (j2 - j)));
    }

    public void dump(IndentingPrintWriter indentingPrintWriter, boolean z) {
        indentingPrintWriter.print("NetworkStatsHistory: bucketDuration=");
        indentingPrintWriter.println(this.bucketDuration);
        indentingPrintWriter.increaseIndent();
        int iMax = z ? 0 : Math.max(0, this.bucketCount - 32);
        if (iMax > 0) {
            indentingPrintWriter.print("(omitting ");
            indentingPrintWriter.print(iMax);
            indentingPrintWriter.println(" buckets)");
        }
        while (iMax < this.bucketCount) {
            indentingPrintWriter.print("bucketStart=");
            indentingPrintWriter.print(this.bucketStart[iMax]);
            if (this.activeTime != null) {
                indentingPrintWriter.print(" activeTime=");
                indentingPrintWriter.print(this.activeTime[iMax]);
            }
            if (this.rxBytes != null) {
                indentingPrintWriter.print(" rxBytes=");
                indentingPrintWriter.print(this.rxBytes[iMax]);
            }
            if (this.rxPackets != null) {
                indentingPrintWriter.print(" rxPackets=");
                indentingPrintWriter.print(this.rxPackets[iMax]);
            }
            if (this.txBytes != null) {
                indentingPrintWriter.print(" txBytes=");
                indentingPrintWriter.print(this.txBytes[iMax]);
            }
            if (this.txPackets != null) {
                indentingPrintWriter.print(" txPackets=");
                indentingPrintWriter.print(this.txPackets[iMax]);
            }
            if (this.operations != null) {
                indentingPrintWriter.print(" operations=");
                indentingPrintWriter.print(this.operations[iMax]);
            }
            indentingPrintWriter.println();
            iMax++;
        }
        indentingPrintWriter.decreaseIndent();
    }

    public String toString() {
        CharArrayWriter charArrayWriter = new CharArrayWriter();
        dump(new IndentingPrintWriter(charArrayWriter, "  "), false);
        return charArrayWriter.toString();
    }

    private static long getLong(long[] jArr, int i, long j) {
        return jArr != null ? jArr[i] : j;
    }

    private static void setLong(long[] jArr, int i, long j) {
        if (jArr != null) {
            jArr[i] = j;
        }
    }

    private static void addLong(long[] jArr, int i, long j) {
        if (jArr != null) {
            jArr[i] = jArr[i] + j;
        }
    }

    public int estimateResizeBuckets(long j) {
        return (int) ((((long) size()) * getBucketDuration()) / j);
    }

    public static class DataStreamUtils {
        @Deprecated
        public static long[] readFullLongArray(DataInputStream dataInputStream) throws IOException {
            int i = dataInputStream.readInt();
            if (i < 0) {
                throw new ProtocolException("negative array size");
            }
            long[] jArr = new long[i];
            for (int i2 = 0; i2 < i; i2++) {
                jArr[i2] = dataInputStream.readLong();
            }
            return jArr;
        }

        public static long readVarLong(DataInputStream dataInputStream) throws IOException {
            long j = 0;
            for (int i = 0; i < 64; i += 7) {
                byte b = dataInputStream.readByte();
                j |= ((long) (b & 127)) << i;
                if ((b & 128) == 0) {
                    return j;
                }
            }
            throw new ProtocolException("malformed long");
        }

        public static void writeVarLong(DataOutputStream dataOutputStream, long j) throws IOException {
            while (((-128) & j) != 0) {
                dataOutputStream.writeByte((((int) j) & 127) | 128);
                j >>>= 7;
            }
            dataOutputStream.writeByte((int) j);
        }

        public static long[] readVarLongArray(DataInputStream dataInputStream) throws IOException {
            int i = dataInputStream.readInt();
            if (i == -1) {
                return null;
            }
            if (i < 0) {
                throw new ProtocolException("negative array size");
            }
            long[] jArr = new long[i];
            for (int i2 = 0; i2 < i; i2++) {
                jArr[i2] = readVarLong(dataInputStream);
            }
            return jArr;
        }

        public static void writeVarLongArray(DataOutputStream dataOutputStream, long[] jArr, int i) throws IOException {
            if (jArr == null) {
                dataOutputStream.writeInt(-1);
                return;
            }
            if (i > jArr.length) {
                throw new IllegalArgumentException("size larger than length");
            }
            dataOutputStream.writeInt(i);
            for (int i2 = 0; i2 < i; i2++) {
                writeVarLong(dataOutputStream, jArr[i2]);
            }
        }
    }

    public static class ParcelUtils {
        public static long[] readLongArray(Parcel parcel) {
            int i = parcel.readInt();
            if (i == -1) {
                return null;
            }
            long[] jArr = new long[i];
            for (int i2 = 0; i2 < i; i2++) {
                jArr[i2] = parcel.readLong();
            }
            return jArr;
        }

        public static void writeLongArray(Parcel parcel, long[] jArr, int i) {
            if (jArr == null) {
                parcel.writeInt(-1);
                return;
            }
            if (i > jArr.length) {
                throw new IllegalArgumentException("size larger than length");
            }
            parcel.writeInt(i);
            for (int i2 = 0; i2 < i; i2++) {
                parcel.writeLong(jArr[i2]);
            }
        }
    }
}
