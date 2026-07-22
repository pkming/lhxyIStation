package android.util;

import com.android.internal.util.ArrayUtils;

/* JADX INFO: loaded from: classes.dex */
public class SparseIntArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private int[] mValues;

    public SparseIntArray() {
        this(10);
    }

    public SparseIntArray(int i) {
        if (i == 0) {
            this.mKeys = ContainerHelpers.EMPTY_INTS;
            this.mValues = ContainerHelpers.EMPTY_INTS;
        } else {
            int iIdealIntArraySize = ArrayUtils.idealIntArraySize(i);
            this.mKeys = new int[iIdealIntArraySize];
            this.mValues = new int[iIdealIntArraySize];
        }
        this.mSize = 0;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public SparseIntArray m17clone() {
        SparseIntArray sparseIntArray = null;
        try {
            SparseIntArray sparseIntArray2 = (SparseIntArray) super.clone();
            try {
                sparseIntArray2.mKeys = (int[]) this.mKeys.clone();
                sparseIntArray2.mValues = (int[]) this.mValues.clone();
                return sparseIntArray2;
            } catch (CloneNotSupportedException unused) {
                sparseIntArray = sparseIntArray2;
                return sparseIntArray;
            }
        } catch (CloneNotSupportedException unused2) {
        }
    }

    public int get(int i) {
        return get(i, 0);
    }

    public int get(int i, int i2) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        return iBinarySearch < 0 ? i2 : this.mValues[iBinarySearch];
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
        int[] iArr2 = this.mValues;
        System.arraycopy(iArr2, i2, iArr2, i, this.mSize - i2);
        this.mSize--;
    }

    public void put(int i, int i2) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        if (iBinarySearch >= 0) {
            this.mValues[iBinarySearch] = i2;
            return;
        }
        int i3 = ~iBinarySearch;
        int i4 = this.mSize;
        if (i4 >= this.mKeys.length) {
            int iIdealIntArraySize = ArrayUtils.idealIntArraySize(i4 + 1);
            int[] iArr = new int[iIdealIntArraySize];
            int[] iArr2 = new int[iIdealIntArraySize];
            int[] iArr3 = this.mKeys;
            System.arraycopy(iArr3, 0, iArr, 0, iArr3.length);
            int[] iArr4 = this.mValues;
            System.arraycopy(iArr4, 0, iArr2, 0, iArr4.length);
            this.mKeys = iArr;
            this.mValues = iArr2;
        }
        int i5 = this.mSize;
        if (i5 - i3 != 0) {
            int[] iArr5 = this.mKeys;
            int i6 = i3 + 1;
            System.arraycopy(iArr5, i3, iArr5, i6, i5 - i3);
            int[] iArr6 = this.mValues;
            System.arraycopy(iArr6, i3, iArr6, i6, this.mSize - i3);
        }
        this.mKeys[i3] = i;
        this.mValues[i3] = i2;
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public int keyAt(int i) {
        return this.mKeys[i];
    }

    public int valueAt(int i) {
        return this.mValues[i];
    }

    public int indexOfKey(int i) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
    }

    public int indexOfValue(int i) {
        for (int i2 = 0; i2 < this.mSize; i2++) {
            if (this.mValues[i2] == i) {
                return i2;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(int i, int i2) {
        int i3 = this.mSize;
        if (i3 != 0 && i <= this.mKeys[i3 - 1]) {
            put(i, i2);
            return;
        }
        if (i3 >= this.mKeys.length) {
            int iIdealIntArraySize = ArrayUtils.idealIntArraySize(i3 + 1);
            int[] iArr = new int[iIdealIntArraySize];
            int[] iArr2 = new int[iIdealIntArraySize];
            int[] iArr3 = this.mKeys;
            System.arraycopy(iArr3, 0, iArr, 0, iArr3.length);
            int[] iArr4 = this.mValues;
            System.arraycopy(iArr4, 0, iArr2, 0, iArr4.length);
            this.mKeys = iArr;
            this.mValues = iArr2;
        }
        this.mKeys[i3] = i;
        this.mValues[i3] = i2;
        this.mSize = i3 + 1;
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
