package android.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class ArrayMap<K, V> implements Map<K, V> {
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean DEBUG = false;
    public static final ArrayMap EMPTY = new ArrayMap(true);
    static final int[] EMPTY_IMMUTABLE_INTS = new int[0];
    private static final String TAG = "ArrayMap";
    static Object[] mBaseCache;
    static int mBaseCacheSize;
    static Object[] mTwiceBaseCache;
    static int mTwiceBaseCacheSize;
    Object[] mArray;
    MapCollections<K, V> mCollections;
    int[] mHashes;
    int mSize;

    int indexOf(Object obj, int i) {
        int i2 = this.mSize;
        if (i2 == 0) {
            return -1;
        }
        int iBinarySearch = ContainerHelpers.binarySearch(this.mHashes, i2, i);
        if (iBinarySearch < 0 || obj.equals(this.mArray[iBinarySearch << 1])) {
            return iBinarySearch;
        }
        int i3 = iBinarySearch + 1;
        while (i3 < i2 && this.mHashes[i3] == i) {
            if (obj.equals(this.mArray[i3 << 1])) {
                return i3;
            }
            i3++;
        }
        for (int i4 = iBinarySearch - 1; i4 >= 0 && this.mHashes[i4] == i; i4--) {
            if (obj.equals(this.mArray[i4 << 1])) {
                return i4;
            }
        }
        return ~i3;
    }

    int indexOfNull() {
        int i = this.mSize;
        if (i == 0) {
            return -1;
        }
        int iBinarySearch = ContainerHelpers.binarySearch(this.mHashes, i, 0);
        if (iBinarySearch < 0 || this.mArray[iBinarySearch << 1] == null) {
            return iBinarySearch;
        }
        int i2 = iBinarySearch + 1;
        while (i2 < i && this.mHashes[i2] == 0) {
            if (this.mArray[i2 << 1] == null) {
                return i2;
            }
            i2++;
        }
        for (int i3 = iBinarySearch - 1; i3 >= 0 && this.mHashes[i3] == 0; i3--) {
            if (this.mArray[i3 << 1] == null) {
                return i3;
            }
        }
        return ~i2;
    }

    private void allocArrays(int i) {
        if (this.mHashes == EMPTY_IMMUTABLE_INTS) {
            throw new UnsupportedOperationException("ArrayMap is immutable");
        }
        if (i == 8) {
            synchronized (ArrayMap.class) {
                Object[] objArr = mTwiceBaseCache;
                if (objArr != null) {
                    this.mArray = objArr;
                    mTwiceBaseCache = (Object[]) objArr[0];
                    this.mHashes = (int[]) objArr[1];
                    objArr[1] = null;
                    objArr[0] = null;
                    mTwiceBaseCacheSize--;
                    return;
                }
            }
        } else if (i == 4) {
            synchronized (ArrayMap.class) {
                Object[] objArr2 = mBaseCache;
                if (objArr2 != null) {
                    this.mArray = objArr2;
                    mBaseCache = (Object[]) objArr2[0];
                    this.mHashes = (int[]) objArr2[1];
                    objArr2[1] = null;
                    objArr2[0] = null;
                    mBaseCacheSize--;
                    return;
                }
            }
        }
        this.mHashes = new int[i];
        this.mArray = new Object[i << 1];
    }

    private static void freeArrays(int[] iArr, Object[] objArr, int i) {
        if (iArr.length == 8) {
            synchronized (ArrayMap.class) {
                if (mTwiceBaseCacheSize < 10) {
                    objArr[0] = mTwiceBaseCache;
                    objArr[1] = iArr;
                    for (int i2 = (i << 1) - 1; i2 >= 2; i2--) {
                        objArr[i2] = null;
                    }
                    mTwiceBaseCache = objArr;
                    mTwiceBaseCacheSize++;
                }
            }
            return;
        }
        if (iArr.length == 4) {
            synchronized (ArrayMap.class) {
                if (mBaseCacheSize < 10) {
                    objArr[0] = mBaseCache;
                    objArr[1] = iArr;
                    for (int i3 = (i << 1) - 1; i3 >= 2; i3--) {
                        objArr[i3] = null;
                    }
                    mBaseCache = objArr;
                    mBaseCacheSize++;
                }
            }
        }
    }

    public ArrayMap() {
        this.mHashes = ContainerHelpers.EMPTY_INTS;
        this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        this.mSize = 0;
    }

    public ArrayMap(int i) {
        if (i == 0) {
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        } else {
            allocArrays(i);
        }
        this.mSize = 0;
    }

    private ArrayMap(boolean z) {
        this.mHashes = EMPTY_IMMUTABLE_INTS;
        this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        this.mSize = 0;
    }

    public ArrayMap(ArrayMap arrayMap) {
        this();
        if (arrayMap != null) {
            putAll(arrayMap);
        }
    }

    @Override // java.util.Map
    public void clear() {
        int i = this.mSize;
        if (i > 0) {
            freeArrays(this.mHashes, this.mArray, i);
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            this.mSize = 0;
        }
    }

    public void erase() {
        int i = this.mSize;
        if (i > 0) {
            int i2 = i << 1;
            Object[] objArr = this.mArray;
            for (int i3 = 0; i3 < i2; i3++) {
                objArr[i3] = null;
            }
            this.mSize = 0;
        }
    }

    public void ensureCapacity(int i) {
        int[] iArr = this.mHashes;
        if (iArr.length < i) {
            Object[] objArr = this.mArray;
            allocArrays(i);
            int i2 = this.mSize;
            if (i2 > 0) {
                System.arraycopy(iArr, 0, this.mHashes, 0, i2);
                System.arraycopy(objArr, 0, this.mArray, 0, this.mSize << 1);
            }
            freeArrays(iArr, objArr, this.mSize);
        }
    }

    @Override // java.util.Map
    public boolean containsKey(Object obj) {
        if (obj == null) {
            if (indexOfNull() >= 0) {
                return true;
            }
        } else if (indexOf(obj, obj.hashCode()) >= 0) {
            return true;
        }
        return false;
    }

    int indexOfValue(Object obj) {
        int i = this.mSize * 2;
        Object[] objArr = this.mArray;
        if (obj == null) {
            for (int i2 = 1; i2 < i; i2 += 2) {
                if (objArr[i2] == null) {
                    return i2 >> 1;
                }
            }
            return -1;
        }
        for (int i3 = 1; i3 < i; i3 += 2) {
            if (obj.equals(objArr[i3])) {
                return i3 >> 1;
            }
        }
        return -1;
    }

    @Override // java.util.Map
    public boolean containsValue(Object obj) {
        return indexOfValue(obj) >= 0;
    }

    @Override // java.util.Map
    public V get(Object obj) {
        int iIndexOfNull = obj == null ? indexOfNull() : indexOf(obj, obj.hashCode());
        if (iIndexOfNull >= 0) {
            return (V) this.mArray[(iIndexOfNull << 1) + 1];
        }
        return null;
    }

    public K keyAt(int i) {
        return (K) this.mArray[i << 1];
    }

    public V valueAt(int i) {
        return (V) this.mArray[(i << 1) + 1];
    }

    public V setValueAt(int i, V v) {
        int i2 = (i << 1) + 1;
        Object[] objArr = this.mArray;
        V v2 = (V) objArr[i2];
        objArr[i2] = v;
        return v2;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.mSize <= 0;
    }

    @Override // java.util.Map
    public V put(K k, V v) {
        int i;
        int iIndexOf;
        if (k == null) {
            iIndexOf = indexOfNull();
            i = 0;
        } else {
            int iHashCode = k.hashCode();
            i = iHashCode;
            iIndexOf = indexOf(k, iHashCode);
        }
        if (iIndexOf >= 0) {
            int i2 = (iIndexOf << 1) + 1;
            Object[] objArr = this.mArray;
            V v2 = (V) objArr[i2];
            objArr[i2] = v;
            return v2;
        }
        int i3 = ~iIndexOf;
        int i4 = this.mSize;
        int[] iArr = this.mHashes;
        if (i4 >= iArr.length) {
            int i5 = 4;
            if (i4 >= 8) {
                i5 = (i4 >> 1) + i4;
            } else if (i4 >= 4) {
                i5 = 8;
            }
            Object[] objArr2 = this.mArray;
            allocArrays(i5);
            int[] iArr2 = this.mHashes;
            if (iArr2.length > 0) {
                System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
                System.arraycopy(objArr2, 0, this.mArray, 0, objArr2.length);
            }
            freeArrays(iArr, objArr2, this.mSize);
        }
        int i6 = this.mSize;
        if (i3 < i6) {
            int[] iArr3 = this.mHashes;
            int i7 = i3 + 1;
            System.arraycopy(iArr3, i3, iArr3, i7, i6 - i3);
            Object[] objArr3 = this.mArray;
            System.arraycopy(objArr3, i3 << 1, objArr3, i7 << 1, (this.mSize - i3) << 1);
        }
        this.mHashes[i3] = i;
        Object[] objArr4 = this.mArray;
        int i8 = i3 << 1;
        objArr4[i8] = k;
        objArr4[i8 + 1] = v;
        this.mSize++;
        return null;
    }

    public void append(K k, V v) {
        int i = this.mSize;
        int iHashCode = k == null ? 0 : k.hashCode();
        int[] iArr = this.mHashes;
        if (i >= iArr.length) {
            throw new IllegalStateException("Array is full");
        }
        if (i > 0) {
            int i2 = i - 1;
            if (iArr[i2] > iHashCode) {
                RuntimeException runtimeException = new RuntimeException("here");
                runtimeException.fillInStackTrace();
                Log.w(TAG, "New hash " + iHashCode + " is before end of array hash " + this.mHashes[i2] + " at index " + i + " key " + k, runtimeException);
                put(k, v);
                return;
            }
        }
        this.mSize = i + 1;
        iArr[i] = iHashCode;
        int i3 = i << 1;
        Object[] objArr = this.mArray;
        objArr[i3] = k;
        objArr[i3 + 1] = v;
    }

    public void putAll(ArrayMap<? extends K, ? extends V> arrayMap) {
        int i = arrayMap.mSize;
        ensureCapacity(this.mSize + i);
        if (this.mSize != 0) {
            for (int i2 = 0; i2 < i; i2++) {
                put(arrayMap.keyAt(i2), arrayMap.valueAt(i2));
            }
        } else if (i > 0) {
            System.arraycopy(arrayMap.mHashes, 0, this.mHashes, 0, i);
            System.arraycopy(arrayMap.mArray, 0, this.mArray, 0, i << 1);
            this.mSize = i;
        }
    }

    @Override // java.util.Map
    public V remove(Object obj) {
        int iIndexOfNull = obj == null ? indexOfNull() : indexOf(obj, obj.hashCode());
        if (iIndexOfNull >= 0) {
            return removeAt(iIndexOfNull);
        }
        return null;
    }

    public V removeAt(int i) {
        Object[] objArr = this.mArray;
        int i2 = i << 1;
        V v = (V) objArr[i2 + 1];
        int i3 = this.mSize;
        if (i3 <= 1) {
            freeArrays(this.mHashes, objArr, i3);
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            this.mSize = 0;
        } else {
            int[] iArr = this.mHashes;
            if (iArr.length > 8 && i3 < iArr.length / 3) {
                allocArrays(i3 > 8 ? i3 + (i3 >> 1) : 8);
                this.mSize--;
                if (i > 0) {
                    System.arraycopy(iArr, 0, this.mHashes, 0, i);
                    System.arraycopy(objArr, 0, this.mArray, 0, i2);
                }
                int i4 = this.mSize;
                if (i < i4) {
                    int i5 = i + 1;
                    System.arraycopy(iArr, i5, this.mHashes, i, i4 - i);
                    System.arraycopy(objArr, i5 << 1, this.mArray, i2, (this.mSize - i) << 1);
                }
            } else {
                int i6 = i3 - 1;
                this.mSize = i6;
                if (i < i6) {
                    int i7 = i + 1;
                    System.arraycopy(iArr, i7, iArr, i, i6 - i);
                    Object[] objArr2 = this.mArray;
                    System.arraycopy(objArr2, i7 << 1, objArr2, i2, (this.mSize - i) << 1);
                }
                Object[] objArr3 = this.mArray;
                int i8 = this.mSize;
                objArr3[i8 << 1] = null;
                objArr3[(i8 << 1) + 1] = null;
            }
        }
        return v;
    }

    @Override // java.util.Map
    public int size() {
        return this.mSize;
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Map) {
            Map map = (Map) obj;
            if (size() != map.size()) {
                return false;
            }
            for (int i = 0; i < this.mSize; i++) {
                try {
                    K kKeyAt = keyAt(i);
                    V vValueAt = valueAt(i);
                    Object obj2 = map.get(kKeyAt);
                    if (vValueAt == null) {
                        if (obj2 != null || !map.containsKey(kKeyAt)) {
                            return false;
                        }
                    } else if (!vValueAt.equals(obj2)) {
                        return false;
                    }
                } catch (ClassCastException | NullPointerException unused) {
                }
            }
            return true;
        }
        return false;
    }

    @Override // java.util.Map
    public int hashCode() {
        int[] iArr = this.mHashes;
        Object[] objArr = this.mArray;
        int i = this.mSize;
        int i2 = 1;
        int i3 = 0;
        int iHashCode = 0;
        while (i3 < i) {
            Object obj = objArr[i2];
            iHashCode += (obj == null ? 0 : obj.hashCode()) ^ iArr[i3];
            i3++;
            i2 += 2;
        }
        return iHashCode;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.mSize * 28);
        sb.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            K kKeyAt = keyAt(i);
            if (kKeyAt != this) {
                sb.append(kKeyAt);
            } else {
                sb.append("(this Map)");
            }
            sb.append('=');
            V vValueAt = valueAt(i);
            if (vValueAt != this) {
                sb.append(vValueAt);
            } else {
                sb.append("(this Map)");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    private MapCollections<K, V> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new MapCollections<K, V>() { // from class: android.util.ArrayMap.1
                @Override // android.util.MapCollections
                protected int colGetSize() {
                    return ArrayMap.this.mSize;
                }

                @Override // android.util.MapCollections
                protected Object colGetEntry(int i, int i2) {
                    return ArrayMap.this.mArray[(i << 1) + i2];
                }

                @Override // android.util.MapCollections
                protected int colIndexOfKey(Object obj) {
                    return obj == null ? ArrayMap.this.indexOfNull() : ArrayMap.this.indexOf(obj, obj.hashCode());
                }

                @Override // android.util.MapCollections
                protected int colIndexOfValue(Object obj) {
                    return ArrayMap.this.indexOfValue(obj);
                }

                @Override // android.util.MapCollections
                protected Map<K, V> colGetMap() {
                    return ArrayMap.this;
                }

                @Override // android.util.MapCollections
                protected void colPut(K k, V v) {
                    ArrayMap.this.put(k, v);
                }

                @Override // android.util.MapCollections
                protected V colSetValue(int i, V v) {
                    return (V) ArrayMap.this.setValueAt(i, v);
                }

                @Override // android.util.MapCollections
                protected void colRemoveAt(int i) {
                    ArrayMap.this.removeAt(i);
                }

                @Override // android.util.MapCollections
                protected void colClear() {
                    ArrayMap.this.clear();
                }
            };
        }
        return this.mCollections;
    }

    public boolean containsAll(Collection<?> collection) {
        return MapCollections.containsAllHelper(this, collection);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        ensureCapacity(this.mSize + map.size());
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public boolean removeAll(Collection<?> collection) {
        return MapCollections.removeAllHelper(this, collection);
    }

    public boolean retainAll(Collection<?> collection) {
        return MapCollections.retainAllHelper(this, collection);
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return getCollection().getEntrySet();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return getCollection().getKeySet();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        return getCollection().getValues();
    }
}
