package android.util;

import com.android.internal.util.ArrayUtils;

/* JADX INFO: loaded from: classes.dex */
public class SparseLongArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private long[] mValues;

    public SparseLongArray() {
        this(10);
    }

    public SparseLongArray(int i) {
        if (i == 0) {
            this.mKeys = ContainerHelpers.EMPTY_INTS;
            this.mValues = ContainerHelpers.EMPTY_LONGS;
        } else {
            int iIdealLongArraySize = ArrayUtils.idealLongArraySize(i);
            this.mKeys = new int[iIdealLongArraySize];
            this.mValues = new long[iIdealLongArraySize];
        }
        this.mSize = 0;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public SparseLongArray m18clone() {
        SparseLongArray sparseLongArray = null;
        try {
            SparseLongArray sparseLongArray2 = (SparseLongArray) super.clone();
            try {
                sparseLongArray2.mKeys = (int[]) this.mKeys.clone();
                sparseLongArray2.mValues = (long[]) this.mValues.clone();
                return sparseLongArray2;
            } catch (CloneNotSupportedException unused) {
                sparseLongArray = sparseLongArray2;
                return sparseLongArray;
            }
        } catch (CloneNotSupportedException unused2) {
        }
    }

    public long get(int i) {
        return get(i, 0L);
    }

    public long get(int i, long j) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        return iBinarySearch < 0 ? j : this.mValues[iBinarySearch];
    }

    public void delete(int i) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        if (iBinarySearch >= 0) {
            removeAt(iBinarySearch);
        }
    }

    public void removeAt(int i) {
        int[] iArr = this.mKeys;
        int i2 = i + 1;
        System.arraycopy(iArr, i2, iArr, i, this.mSize - i2);
        long[] jArr = this.mValues;
        System.arraycopy(jArr, i2, jArr, i, this.mSize - i2);
        this.mSize--;
    }

    public void put(int i, long j) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        if (iBinarySearch >= 0) {
            this.mValues[iBinarySearch] = j;
            return;
        }
        int i2 = ~iBinarySearch;
        int i3 = this.mSize;
        if (i3 >= this.mKeys.length) {
            growKeyAndValueArrays(i3 + 1);
        }
        int i4 = this.mSize;
        if (i4 - i2 != 0) {
            int[] iArr = this.mKeys;
            int i5 = i2 + 1;
            System.arraycopy(iArr, i2, iArr, i5, i4 - i2);
            long[] jArr = this.mValues;
            System.arraycopy(jArr, i2, jArr, i5, this.mSize - i2);
        }
        this.mKeys[i2] = i;
        this.mValues[i2] = j;
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public int keyAt(int i) {
        return this.mKeys[i];
    }

    public long valueAt(int i) {
        return this.mValues[i];
    }

    public int indexOfKey(int i) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
    }

    public int indexOfValue(long j) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == j) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(int i, long j) {
        int i2 = this.mSize;
        if (i2 != 0 && i <= this.mKeys[i2 - 1]) {
            put(i, j);
            return;
        }
        if (i2 >= this.mKeys.length) {
            growKeyAndValueArrays(i2 + 1);
        }
        this.mKeys[i2] = i;
        this.mValues[i2] = j;
        this.mSize = i2 + 1;
    }

    private void growKeyAndValueArrays(int i) {
        int iIdealLongArraySize = ArrayUtils.idealLongArraySize(i);
        int[] iArr = new int[iIdealLongArraySize];
        long[] jArr = new long[iIdealLongArraySize];
        int[] iArr2 = this.mKeys;
        System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
        long[] jArr2 = this.mValues;
        System.arraycopy(jArr2, 0, jArr, 0, jArr2.length);
        this.mKeys = iArr;
        this.mValues = jArr;
    }

    public String toString() {
        if (size() <= 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.mSize * 28);
        sb.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(keyAt(i));
            sb.append('=');
            sb.append(valueAt(i));
        }
        sb.append('}');
        return sb.toString();
    }
}
