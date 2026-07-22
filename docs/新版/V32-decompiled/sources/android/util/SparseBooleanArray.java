package android.util;

import com.android.internal.util.ArrayUtils;

/* JADX INFO: loaded from: classes.dex */
public class SparseBooleanArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private boolean[] mValues;

    public SparseBooleanArray() {
        this(10);
    }

    public SparseBooleanArray(int i) {
        if (i == 0) {
            this.mKeys = ContainerHelpers.EMPTY_INTS;
            this.mValues = ContainerHelpers.EMPTY_BOOLEANS;
        } else {
            int iIdealIntArraySize = ArrayUtils.idealIntArraySize(i);
            this.mKeys = new int[iIdealIntArraySize];
            this.mValues = new boolean[iIdealIntArraySize];
        }
        this.mSize = 0;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public SparseBooleanArray m16clone() {
        SparseBooleanArray sparseBooleanArray = null;
        try {
            SparseBooleanArray sparseBooleanArray2 = (SparseBooleanArray) super.clone();
            try {
                sparseBooleanArray2.mKeys = (int[]) this.mKeys.clone();
                sparseBooleanArray2.mValues = (boolean[]) this.mValues.clone();
                return sparseBooleanArray2;
            } catch (CloneNotSupportedException unused) {
                sparseBooleanArray = sparseBooleanArray2;
                return sparseBooleanArray;
            }
        } catch (CloneNotSupportedException unused2) {
        }
    }

    public boolean get(int i) {
        return get(i, false);
    }

    public boolean get(int i, boolean z) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        return iBinarySearch < 0 ? z : this.mValues[iBinarySearch];
    }

    public void delete(int i) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        if (iBinarySearch >= 0) {
            int[] iArr = this.mKeys;
            int i2 = iBinarySearch + 1;
            System.arraycopy(iArr, i2, iArr, iBinarySearch, this.mSize - i2);
            boolean[] zArr = this.mValues;
            System.arraycopy(zArr, i2, zArr, iBinarySearch, this.mSize - i2);
            this.mSize--;
        }
    }

    public void put(int i, boolean z) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        if (iBinarySearch >= 0) {
            this.mValues[iBinarySearch] = z;
            return;
        }
        int i2 = ~iBinarySearch;
        int i3 = this.mSize;
        if (i3 >= this.mKeys.length) {
            int iIdealIntArraySize = ArrayUtils.idealIntArraySize(i3 + 1);
            int[] iArr = new int[iIdealIntArraySize];
            boolean[] zArr = new boolean[iIdealIntArraySize];
            int[] iArr2 = this.mKeys;
            System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
            boolean[] zArr2 = this.mValues;
            System.arraycopy(zArr2, 0, zArr, 0, zArr2.length);
            this.mKeys = iArr;
            this.mValues = zArr;
        }
        int i4 = this.mSize;
        if (i4 - i2 != 0) {
            int[] iArr3 = this.mKeys;
            int i5 = i2 + 1;
            System.arraycopy(iArr3, i2, iArr3, i5, i4 - i2);
            boolean[] zArr3 = this.mValues;
            System.arraycopy(zArr3, i2, zArr3, i5, this.mSize - i2);
        }
        this.mKeys[i2] = i;
        this.mValues[i2] = z;
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public int keyAt(int i) {
        return this.mKeys[i];
    }

    public boolean valueAt(int i) {
        return this.mValues[i];
    }

    public int indexOfKey(int i) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
    }

    public int indexOfValue(boolean z) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == z) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(int i, boolean z) {
        int i2 = this.mSize;
        if (i2 != 0 && i <= this.mKeys[i2 - 1]) {
            put(i, z);
            return;
        }
        if (i2 >= this.mKeys.length) {
            int iIdealIntArraySize = ArrayUtils.idealIntArraySize(i2 + 1);
            int[] iArr = new int[iIdealIntArraySize];
            boolean[] zArr = new boolean[iIdealIntArraySize];
            int[] iArr2 = this.mKeys;
            System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
            boolean[] zArr2 = this.mValues;
            System.arraycopy(zArr2, 0, zArr, 0, zArr2.length);
            this.mKeys = iArr;
            this.mValues = zArr;
        }
        this.mKeys[i2] = i;
        this.mValues[i2] = z;
        this.mSize = i2 + 1;
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
