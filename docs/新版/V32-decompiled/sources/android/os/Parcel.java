package android.os;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class Parcel {
    private static final boolean DEBUG_ARRAY_MAP = false;
    private static final boolean DEBUG_RECYCLE = false;
    private static final int EX_BAD_PARCELABLE = -2;
    private static final int EX_HAS_REPLY_HEADER = -128;
    private static final int EX_ILLEGAL_ARGUMENT = -3;
    private static final int EX_ILLEGAL_STATE = -5;
    private static final int EX_NETWORK_MAIN_THREAD = -6;
    private static final int EX_NULL_POINTER = -4;
    private static final int EX_SECURITY = -1;
    private static final int POOL_SIZE = 6;
    private static final String TAG = "Parcel";
    private static final int VAL_BOOLEAN = 9;
    private static final int VAL_BOOLEANARRAY = 23;
    private static final int VAL_BUNDLE = 3;
    private static final int VAL_BYTE = 20;
    private static final int VAL_BYTEARRAY = 13;
    private static final int VAL_CHARSEQUENCE = 10;
    private static final int VAL_CHARSEQUENCEARRAY = 24;
    private static final int VAL_DOUBLE = 8;
    private static final int VAL_FLOAT = 7;
    private static final int VAL_IBINDER = 15;
    private static final int VAL_INTARRAY = 18;
    private static final int VAL_INTEGER = 1;
    private static final int VAL_LIST = 11;
    private static final int VAL_LONG = 6;
    private static final int VAL_LONGARRAY = 19;
    private static final int VAL_MAP = 2;
    private static final int VAL_NULL = -1;
    private static final int VAL_OBJECTARRAY = 17;
    private static final int VAL_PARCELABLE = 4;
    private static final int VAL_PARCELABLEARRAY = 16;
    private static final int VAL_SERIALIZABLE = 21;
    private static final int VAL_SHORT = 5;
    private static final int VAL_SPARSEARRAY = 12;
    private static final int VAL_SPARSEBOOLEANARRAY = 22;
    private static final int VAL_STRING = 0;
    private static final int VAL_STRINGARRAY = 14;
    private int mNativePtr;
    private boolean mOwnsNativeParcelObject;
    private RuntimeException mStack;
    private static final Parcel[] sOwnedPool = new Parcel[6];
    private static final Parcel[] sHolderPool = new Parcel[6];
    public static final Parcelable.Creator<String> STRING_CREATOR = new Parcelable.Creator<String>() { // from class: android.os.Parcel.1
        @Override // android.os.Parcelable.Creator
        public String createFromParcel(Parcel parcel) {
            return parcel.readString();
        }

        @Override // android.os.Parcelable.Creator
        public String[] newArray(int i) {
            return new String[i];
        }
    };
    private static final HashMap<ClassLoader, HashMap<String, Parcelable.Creator>> mCreators = new HashMap<>();

    static native void clearFileDescriptor(FileDescriptor fileDescriptor);

    static native void closeFileDescriptor(FileDescriptor fileDescriptor) throws IOException;

    static native FileDescriptor dupFileDescriptor(FileDescriptor fileDescriptor) throws IOException;

    private static native void nativeAppendFrom(int i, int i2, int i3, int i4);

    private static native int nativeCreate();

    private static native byte[] nativeCreateByteArray(int i);

    private static native int nativeDataAvail(int i);

    private static native int nativeDataCapacity(int i);

    private static native int nativeDataPosition(int i);

    private static native int nativeDataSize(int i);

    private static native void nativeDestroy(int i);

    private static native void nativeEnforceInterface(int i, String str);

    private static native void nativeFreeBuffer(int i);

    private static native boolean nativeHasFileDescriptors(int i);

    private static native byte[] nativeMarshall(int i);

    private static native boolean nativePushAllowFds(int i, boolean z);

    private static native double nativeReadDouble(int i);

    private static native FileDescriptor nativeReadFileDescriptor(int i);

    private static native float nativeReadFloat(int i);

    private static native int nativeReadInt(int i);

    private static native long nativeReadLong(int i);

    private static native String nativeReadString(int i);

    private static native IBinder nativeReadStrongBinder(int i);

    private static native void nativeRestoreAllowFds(int i, boolean z);

    private static native void nativeSetDataCapacity(int i, int i2);

    private static native void nativeSetDataPosition(int i, int i2);

    private static native void nativeSetDataSize(int i, int i2);

    private static native void nativeUnmarshall(int i, byte[] bArr, int i2, int i3);

    private static native void nativeWriteByteArray(int i, byte[] bArr, int i2, int i3);

    private static native void nativeWriteDouble(int i, double d);

    private static native void nativeWriteFileDescriptor(int i, FileDescriptor fileDescriptor);

    private static native void nativeWriteFloat(int i, float f);

    private static native void nativeWriteInt(int i, int i2);

    private static native void nativeWriteInterfaceToken(int i, String str);

    private static native void nativeWriteLong(int i, long j);

    private static native void nativeWriteString(int i, String str);

    private static native void nativeWriteStrongBinder(int i, IBinder iBinder);

    static native FileDescriptor openFileDescriptor(String str, int i) throws FileNotFoundException;

    public static Parcel obtain() {
        Parcel[] parcelArr = sOwnedPool;
        synchronized (parcelArr) {
            for (int i = 0; i < 6; i++) {
                Parcel parcel = parcelArr[i];
                if (parcel != null) {
                    parcelArr[i] = null;
                    return parcel;
                }
            }
            return new Parcel(0);
        }
    }

    public final void recycle() {
        Parcel[] parcelArr;
        freeBuffer();
        if (this.mOwnsNativeParcelObject) {
            parcelArr = sOwnedPool;
        } else {
            this.mNativePtr = 0;
            parcelArr = sHolderPool;
        }
        synchronized (parcelArr) {
            for (int i = 0; i < 6; i++) {
                if (parcelArr[i] == null) {
                    parcelArr[i] = this;
                    return;
                }
            }
        }
    }

    public final int dataSize() {
        return nativeDataSize(this.mNativePtr);
    }

    public final int dataAvail() {
        return nativeDataAvail(this.mNativePtr);
    }

    public final int dataPosition() {
        return nativeDataPosition(this.mNativePtr);
    }

    public final int dataCapacity() {
        return nativeDataCapacity(this.mNativePtr);
    }

    public final void setDataSize(int i) {
        nativeSetDataSize(this.mNativePtr, i);
    }

    public final void setDataPosition(int i) {
        nativeSetDataPosition(this.mNativePtr, i);
    }

    public final void setDataCapacity(int i) {
        nativeSetDataCapacity(this.mNativePtr, i);
    }

    public final boolean pushAllowFds(boolean z) {
        return nativePushAllowFds(this.mNativePtr, z);
    }

    public final void restoreAllowFds(boolean z) {
        nativeRestoreAllowFds(this.mNativePtr, z);
    }

    public final byte[] marshall() {
        return nativeMarshall(this.mNativePtr);
    }

    public final void unmarshall(byte[] bArr, int i, int i2) {
        nativeUnmarshall(this.mNativePtr, bArr, i, i2);
    }

    public final void appendFrom(Parcel parcel, int i, int i2) {
        nativeAppendFrom(this.mNativePtr, parcel.mNativePtr, i, i2);
    }

    public final boolean hasFileDescriptors() {
        return nativeHasFileDescriptors(this.mNativePtr);
    }

    public final void writeInterfaceToken(String str) {
        nativeWriteInterfaceToken(this.mNativePtr, str);
    }

    public final void enforceInterface(String str) {
        nativeEnforceInterface(this.mNativePtr, str);
    }

    public final void writeByteArray(byte[] bArr) {
        writeByteArray(bArr, 0, bArr != null ? bArr.length : 0);
    }

    public final void writeByteArray(byte[] bArr, int i, int i2) {
        if (bArr == null) {
            writeInt(-1);
        } else {
            Arrays.checkOffsetAndCount(bArr.length, i, i2);
            nativeWriteByteArray(this.mNativePtr, bArr, i, i2);
        }
    }

    public final void writeInt(int i) {
        nativeWriteInt(this.mNativePtr, i);
    }

    public final void writeLong(long j) {
        nativeWriteLong(this.mNativePtr, j);
    }

    public final void writeFloat(float f) {
        nativeWriteFloat(this.mNativePtr, f);
    }

    public final void writeDouble(double d) {
        nativeWriteDouble(this.mNativePtr, d);
    }

    public final void writeString(String str) {
        nativeWriteString(this.mNativePtr, str);
    }

    public final void writeCharSequence(CharSequence charSequence) {
        TextUtils.writeToParcel(charSequence, this, 0);
    }

    public final void writeStrongBinder(IBinder iBinder) {
        nativeWriteStrongBinder(this.mNativePtr, iBinder);
    }

    public final void writeStrongInterface(IInterface iInterface) {
        writeStrongBinder(iInterface == null ? null : iInterface.asBinder());
    }

    public final void writeFileDescriptor(FileDescriptor fileDescriptor) {
        nativeWriteFileDescriptor(this.mNativePtr, fileDescriptor);
    }

    public final void writeByte(byte b) {
        writeInt(b);
    }

    public final void writeMap(Map map) {
        writeMapInternal(map);
    }

    void writeMapInternal(Map<String, Object> map) {
        if (map == null) {
            writeInt(-1);
            return;
        }
        Set<Map.Entry<String, Object>> setEntrySet = map.entrySet();
        writeInt(setEntrySet.size());
        for (Map.Entry<String, Object> entry : setEntrySet) {
            writeValue(entry.getKey());
            writeValue(entry.getValue());
        }
    }

    void writeArrayMapInternal(ArrayMap<String, Object> arrayMap) {
        if (arrayMap == null) {
            writeInt(-1);
            return;
        }
        int size = arrayMap.size();
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeValue(arrayMap.keyAt(i));
            writeValue(arrayMap.valueAt(i));
        }
    }

    public final void writeBundle(Bundle bundle) {
        if (bundle == null) {
            writeInt(-1);
        } else {
            bundle.writeToParcel(this, 0);
        }
    }

    public final void writeList(List list) {
        if (list == null) {
            writeInt(-1);
            return;
        }
        int size = list.size();
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeValue(list.get(i));
        }
    }

    public final void writeArray(Object[] objArr) {
        if (objArr == null) {
            writeInt(-1);
            return;
        }
        writeInt(objArr.length);
        for (Object obj : objArr) {
            writeValue(obj);
        }
    }

    public final void writeSparseArray(SparseArray<Object> sparseArray) {
        if (sparseArray == null) {
            writeInt(-1);
            return;
        }
        int size = sparseArray.size();
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeInt(sparseArray.keyAt(i));
            writeValue(sparseArray.valueAt(i));
        }
    }

    public final void writeSparseBooleanArray(SparseBooleanArray sparseBooleanArray) {
        if (sparseBooleanArray == null) {
            writeInt(-1);
            return;
        }
        int size = sparseBooleanArray.size();
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeInt(sparseBooleanArray.keyAt(i));
            writeByte(sparseBooleanArray.valueAt(i) ? (byte) 1 : (byte) 0);
        }
    }

    public final void writeBooleanArray(boolean[] zArr) {
        if (zArr != null) {
            writeInt(zArr.length);
            for (boolean z : zArr) {
                writeInt(z ? 1 : 0);
            }
            return;
        }
        writeInt(-1);
    }

    public final boolean[] createBooleanArray() {
        int i = readInt();
        if (i < 0 || i > (dataAvail() >> 2)) {
            return null;
        }
        boolean[] zArr = new boolean[i];
        for (int i2 = 0; i2 < i; i2++) {
            zArr[i2] = readInt() != 0;
        }
        return zArr;
    }

    public final void readBooleanArray(boolean[] zArr) {
        int i = readInt();
        if (i != zArr.length) {
            throw new RuntimeException("bad array lengths");
        }
        for (int i2 = 0; i2 < i; i2++) {
            zArr[i2] = readInt() != 0;
        }
    }

    public final void writeCharArray(char[] cArr) {
        if (cArr != null) {
            writeInt(cArr.length);
            for (char c : cArr) {
                writeInt(c);
            }
            return;
        }
        writeInt(-1);
    }

    public final char[] createCharArray() {
        int i = readInt();
        if (i < 0 || i > (dataAvail() >> 2)) {
            return null;
        }
        char[] cArr = new char[i];
        for (int i2 = 0; i2 < i; i2++) {
            cArr[i2] = (char) readInt();
        }
        return cArr;
    }

    public final void readCharArray(char[] cArr) {
        int i = readInt();
        if (i != cArr.length) {
            throw new RuntimeException("bad array lengths");
        }
        for (int i2 = 0; i2 < i; i2++) {
            cArr[i2] = (char) readInt();
        }
    }

    public final void writeIntArray(int[] iArr) {
        if (iArr != null) {
            writeInt(iArr.length);
            for (int i : iArr) {
                writeInt(i);
            }
            return;
        }
        writeInt(-1);
    }

    public final int[] createIntArray() {
        int i = readInt();
        if (i < 0 || i > (dataAvail() >> 2)) {
            return null;
        }
        int[] iArr = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            iArr[i2] = readInt();
        }
        return iArr;
    }

    public final void readIntArray(int[] iArr) {
        int i = readInt();
        if (i != iArr.length) {
            throw new RuntimeException("bad array lengths");
        }
        for (int i2 = 0; i2 < i; i2++) {
            iArr[i2] = readInt();
        }
    }

    public final void writeLongArray(long[] jArr) {
        if (jArr != null) {
            writeInt(jArr.length);
            for (long j : jArr) {
                writeLong(j);
            }
            return;
        }
        writeInt(-1);
    }

    public final long[] createLongArray() {
        int i = readInt();
        if (i < 0 || i > (dataAvail() >> 3)) {
            return null;
        }
        long[] jArr = new long[i];
        for (int i2 = 0; i2 < i; i2++) {
            jArr[i2] = readLong();
        }
        return jArr;
    }

    public final void readLongArray(long[] jArr) {
        int i = readInt();
        if (i != jArr.length) {
            throw new RuntimeException("bad array lengths");
        }
        for (int i2 = 0; i2 < i; i2++) {
            jArr[i2] = readLong();
        }
    }

    public final void writeFloatArray(float[] fArr) {
        if (fArr != null) {
            writeInt(fArr.length);
            for (float f : fArr) {
                writeFloat(f);
            }
            return;
        }
        writeInt(-1);
    }

    public final float[] createFloatArray() {
        int i = readInt();
        if (i < 0 || i > (dataAvail() >> 2)) {
            return null;
        }
        float[] fArr = new float[i];
        for (int i2 = 0; i2 < i; i2++) {
            fArr[i2] = readFloat();
        }
        return fArr;
    }

    public final void readFloatArray(float[] fArr) {
        int i = readInt();
        if (i != fArr.length) {
            throw new RuntimeException("bad array lengths");
        }
        for (int i2 = 0; i2 < i; i2++) {
            fArr[i2] = readFloat();
        }
    }

    public final void writeDoubleArray(double[] dArr) {
        if (dArr != null) {
            writeInt(dArr.length);
            for (double d : dArr) {
                writeDouble(d);
            }
            return;
        }
        writeInt(-1);
    }

    public final double[] createDoubleArray() {
        int i = readInt();
        if (i < 0 || i > (dataAvail() >> 3)) {
            return null;
        }
        double[] dArr = new double[i];
        for (int i2 = 0; i2 < i; i2++) {
            dArr[i2] = readDouble();
        }
        return dArr;
    }

    public final void readDoubleArray(double[] dArr) {
        int i = readInt();
        if (i != dArr.length) {
            throw new RuntimeException("bad array lengths");
        }
        for (int i2 = 0; i2 < i; i2++) {
            dArr[i2] = readDouble();
        }
    }

    public final void writeStringArray(String[] strArr) {
        if (strArr != null) {
            writeInt(strArr.length);
            for (String str : strArr) {
                writeString(str);
            }
            return;
        }
        writeInt(-1);
    }

    public final String[] createStringArray() {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        String[] strArr = new String[i];
        for (int i2 = 0; i2 < i; i2++) {
            strArr[i2] = readString();
        }
        return strArr;
    }

    public final void readStringArray(String[] strArr) {
        int i = readInt();
        if (i != strArr.length) {
            throw new RuntimeException("bad array lengths");
        }
        for (int i2 = 0; i2 < i; i2++) {
            strArr[i2] = readString();
        }
    }

    public final void writeBinderArray(IBinder[] iBinderArr) {
        if (iBinderArr != null) {
            writeInt(iBinderArr.length);
            for (IBinder iBinder : iBinderArr) {
                writeStrongBinder(iBinder);
            }
            return;
        }
        writeInt(-1);
    }

    public final void writeCharSequenceArray(CharSequence[] charSequenceArr) {
        if (charSequenceArr != null) {
            writeInt(charSequenceArr.length);
            for (CharSequence charSequence : charSequenceArr) {
                writeCharSequence(charSequence);
            }
            return;
        }
        writeInt(-1);
    }

    public final IBinder[] createBinderArray() {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        IBinder[] iBinderArr = new IBinder[i];
        for (int i2 = 0; i2 < i; i2++) {
            iBinderArr[i2] = readStrongBinder();
        }
        return iBinderArr;
    }

    public final void readBinderArray(IBinder[] iBinderArr) {
        int i = readInt();
        if (i != iBinderArr.length) {
            throw new RuntimeException("bad array lengths");
        }
        for (int i2 = 0; i2 < i; i2++) {
            iBinderArr[i2] = readStrongBinder();
        }
    }

    public final <T extends Parcelable> void writeTypedList(List<T> list) {
        if (list == null) {
            writeInt(-1);
            return;
        }
        int size = list.size();
        writeInt(size);
        for (int i = 0; i < size; i++) {
            T t = list.get(i);
            if (t != null) {
                writeInt(1);
                t.writeToParcel(this, 0);
            } else {
                writeInt(0);
            }
        }
    }

    public final void writeStringList(List<String> list) {
        if (list == null) {
            writeInt(-1);
            return;
        }
        int size = list.size();
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeString(list.get(i));
        }
    }

    public final void writeBinderList(List<IBinder> list) {
        if (list == null) {
            writeInt(-1);
            return;
        }
        int size = list.size();
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeStrongBinder(list.get(i));
        }
    }

    public final <T extends Parcelable> void writeTypedArray(T[] tArr, int i) {
        if (tArr != null) {
            writeInt(tArr.length);
            for (T t : tArr) {
                if (t != null) {
                    writeInt(1);
                    t.writeToParcel(this, i);
                } else {
                    writeInt(0);
                }
            }
            return;
        }
        writeInt(-1);
    }

    public final void writeValue(Object obj) {
        if (obj == null) {
            writeInt(-1);
            return;
        }
        if (obj instanceof String) {
            writeInt(0);
            writeString((String) obj);
            return;
        }
        if (obj instanceof Integer) {
            writeInt(1);
            writeInt(((Integer) obj).intValue());
            return;
        }
        if (obj instanceof Map) {
            writeInt(2);
            writeMap((Map) obj);
            return;
        }
        if (obj instanceof Bundle) {
            writeInt(3);
            writeBundle((Bundle) obj);
            return;
        }
        if (obj instanceof Parcelable) {
            writeInt(4);
            writeParcelable((Parcelable) obj, 0);
            return;
        }
        if (obj instanceof Short) {
            writeInt(5);
            writeInt(((Short) obj).intValue());
            return;
        }
        if (obj instanceof Long) {
            writeInt(6);
            writeLong(((Long) obj).longValue());
            return;
        }
        if (obj instanceof Float) {
            writeInt(7);
            writeFloat(((Float) obj).floatValue());
            return;
        }
        if (obj instanceof Double) {
            writeInt(8);
            writeDouble(((Double) obj).doubleValue());
            return;
        }
        if (obj instanceof Boolean) {
            writeInt(9);
            writeInt(((Boolean) obj).booleanValue() ? 1 : 0);
            return;
        }
        if (obj instanceof CharSequence) {
            writeInt(10);
            writeCharSequence((CharSequence) obj);
            return;
        }
        if (obj instanceof List) {
            writeInt(11);
            writeList((List) obj);
            return;
        }
        if (obj instanceof SparseArray) {
            writeInt(12);
            writeSparseArray((SparseArray) obj);
            return;
        }
        if (obj instanceof boolean[]) {
            writeInt(23);
            writeBooleanArray((boolean[]) obj);
            return;
        }
        if (obj instanceof byte[]) {
            writeInt(13);
            writeByteArray((byte[]) obj);
            return;
        }
        if (obj instanceof String[]) {
            writeInt(14);
            writeStringArray((String[]) obj);
            return;
        }
        if (obj instanceof CharSequence[]) {
            writeInt(24);
            writeCharSequenceArray((CharSequence[]) obj);
            return;
        }
        if (obj instanceof IBinder) {
            writeInt(15);
            writeStrongBinder((IBinder) obj);
            return;
        }
        if (obj instanceof Parcelable[]) {
            writeInt(16);
            writeParcelableArray((Parcelable[]) obj, 0);
            return;
        }
        if (obj instanceof Object[]) {
            writeInt(17);
            writeArray((Object[]) obj);
            return;
        }
        if (obj instanceof int[]) {
            writeInt(18);
            writeIntArray((int[]) obj);
            return;
        }
        if (obj instanceof long[]) {
            writeInt(19);
            writeLongArray((long[]) obj);
        } else if (obj instanceof Byte) {
            writeInt(20);
            writeInt(((Byte) obj).byteValue());
        } else {
            if (obj instanceof Serializable) {
                writeInt(21);
                writeSerializable((Serializable) obj);
                return;
            }
            throw new RuntimeException("Parcel: unable to marshal value " + obj);
        }
    }

    public final void writeParcelable(Parcelable parcelable, int i) {
        if (parcelable == null) {
            writeString(null);
        } else {
            writeString(parcelable.getClass().getName());
            parcelable.writeToParcel(this, i);
        }
    }

    public final void writeParcelableCreator(Parcelable parcelable) {
        writeString(parcelable.getClass().getName());
    }

    public final void writeSerializable(Serializable serializable) {
        if (serializable == null) {
            writeString(null);
            return;
        }
        String name = serializable.getClass().getName();
        writeString(name);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(serializable);
            objectOutputStream.close();
            writeByteArray(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Parcelable encountered IOException writing serializable object (name = " + name + ")", e);
        }
    }

    public final void writeException(Exception exc) {
        int i;
        if (exc instanceof SecurityException) {
            i = -1;
        } else if (exc instanceof BadParcelableException) {
            i = -2;
        } else if (exc instanceof IllegalArgumentException) {
            i = -3;
        } else if (exc instanceof NullPointerException) {
            i = -4;
        } else if (exc instanceof IllegalStateException) {
            i = -5;
        } else {
            i = exc instanceof NetworkOnMainThreadException ? -6 : 0;
        }
        writeInt(i);
        StrictMode.clearGatheredViolations();
        if (i == 0) {
            if (exc instanceof RuntimeException) {
                throw ((RuntimeException) exc);
            }
            throw new RuntimeException(exc);
        }
        writeString(exc.getMessage());
    }

    public final void writeNoException() {
        if (StrictMode.hasGatheredViolations()) {
            writeInt(EX_HAS_REPLY_HEADER);
            int iDataPosition = dataPosition();
            writeInt(0);
            StrictMode.writeGatheredViolationsToParcel(this);
            int iDataPosition2 = dataPosition();
            setDataPosition(iDataPosition);
            writeInt(iDataPosition2 - iDataPosition);
            setDataPosition(iDataPosition2);
            return;
        }
        writeInt(0);
    }

    public final void readException() {
        int exceptionCode = readExceptionCode();
        if (exceptionCode != 0) {
            readException(exceptionCode, readString());
        }
    }

    public final int readExceptionCode() {
        int i = readInt();
        if (i != EX_HAS_REPLY_HEADER) {
            return i;
        }
        if (readInt() == 0) {
            Log.e(TAG, "Unexpected zero-sized Parcel reply header.");
        } else {
            StrictMode.readAndHandleBinderCallViolations(this);
        }
        return 0;
    }

    public final void readException(int i, String str) {
        switch (i) {
            case -6:
                throw new NetworkOnMainThreadException();
            case -5:
                throw new IllegalStateException(str);
            case -4:
                throw new NullPointerException(str);
            case -3:
                throw new IllegalArgumentException(str);
            case -2:
                throw new BadParcelableException(str);
            case -1:
                throw new SecurityException(str);
            default:
                throw new RuntimeException("Unknown exception code: " + i + " msg " + str);
        }
    }

    public final int readInt() {
        return nativeReadInt(this.mNativePtr);
    }

    public final long readLong() {
        return nativeReadLong(this.mNativePtr);
    }

    public final float readFloat() {
        return nativeReadFloat(this.mNativePtr);
    }

    public final double readDouble() {
        return nativeReadDouble(this.mNativePtr);
    }

    public final String readString() {
        return nativeReadString(this.mNativePtr);
    }

    public final CharSequence readCharSequence() {
        return TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(this);
    }

    public final IBinder readStrongBinder() {
        return nativeReadStrongBinder(this.mNativePtr);
    }

    public final ParcelFileDescriptor readFileDescriptor() {
        FileDescriptor fileDescriptorNativeReadFileDescriptor = nativeReadFileDescriptor(this.mNativePtr);
        if (fileDescriptorNativeReadFileDescriptor != null) {
            return new ParcelFileDescriptor(fileDescriptorNativeReadFileDescriptor);
        }
        return null;
    }

    public final FileDescriptor readRawFileDescriptor() {
        return nativeReadFileDescriptor(this.mNativePtr);
    }

    public final byte readByte() {
        return (byte) (readInt() & 255);
    }

    public final void readMap(Map map, ClassLoader classLoader) {
        readMapInternal(map, readInt(), classLoader);
    }

    public final void readList(List list, ClassLoader classLoader) {
        readListInternal(list, readInt(), classLoader);
    }

    public final HashMap readHashMap(ClassLoader classLoader) {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        HashMap map = new HashMap(i);
        readMapInternal(map, i, classLoader);
        return map;
    }

    public final Bundle readBundle() {
        return readBundle(null);
    }

    public final Bundle readBundle(ClassLoader classLoader) {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        Bundle bundle = new Bundle(this, i);
        if (classLoader != null) {
            bundle.setClassLoader(classLoader);
        }
        return bundle;
    }

    public final byte[] createByteArray() {
        return nativeCreateByteArray(this.mNativePtr);
    }

    public final void readByteArray(byte[] bArr) {
        byte[] bArrCreateByteArray = createByteArray();
        if (bArrCreateByteArray.length == bArr.length) {
            System.arraycopy(bArrCreateByteArray, 0, bArr, 0, bArrCreateByteArray.length);
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final String[] readStringArray() {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        String[] strArr = new String[i];
        for (int i2 = 0; i2 < i; i2++) {
            strArr[i2] = readString();
        }
        return strArr;
    }

    public final CharSequence[] readCharSequenceArray() {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        CharSequence[] charSequenceArr = new CharSequence[i];
        for (int i2 = 0; i2 < i; i2++) {
            charSequenceArr[i2] = readCharSequence();
        }
        return charSequenceArr;
    }

    public final ArrayList readArrayList(ClassLoader classLoader) {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList(i);
        readListInternal(arrayList, i, classLoader);
        return arrayList;
    }

    public final Object[] readArray(ClassLoader classLoader) {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        Object[] objArr = new Object[i];
        readArrayInternal(objArr, i, classLoader);
        return objArr;
    }

    public final SparseArray readSparseArray(ClassLoader classLoader) {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        SparseArray sparseArray = new SparseArray(i);
        readSparseArrayInternal(sparseArray, i, classLoader);
        return sparseArray;
    }

    public final SparseBooleanArray readSparseBooleanArray() {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray(i);
        readSparseBooleanArrayInternal(sparseBooleanArray, i);
        return sparseBooleanArray;
    }

    public final <T> ArrayList<T> createTypedArrayList(Parcelable.Creator<T> creator) {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        ArrayList<T> arrayList = new ArrayList<>(i);
        while (i > 0) {
            if (readInt() != 0) {
                arrayList.add(creator.createFromParcel(this));
            } else {
                arrayList.add(null);
            }
            i--;
        }
        return arrayList;
    }

    public final <T> void readTypedList(List<T> list, Parcelable.Creator<T> creator) {
        int size = list.size();
        int i = readInt();
        int i2 = 0;
        while (i2 < size && i2 < i) {
            if (readInt() != 0) {
                list.set(i2, creator.createFromParcel(this));
            } else {
                list.set(i2, null);
            }
            i2++;
        }
        while (i2 < i) {
            if (readInt() != 0) {
                list.add(creator.createFromParcel(this));
            } else {
                list.add(null);
            }
            i2++;
        }
        while (i2 < size) {
            list.remove(i);
            i2++;
        }
    }

    public final ArrayList<String> createStringArrayList() {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>(i);
        while (i > 0) {
            arrayList.add(readString());
            i--;
        }
        return arrayList;
    }

    public final ArrayList<IBinder> createBinderArrayList() {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        ArrayList<IBinder> arrayList = new ArrayList<>(i);
        while (i > 0) {
            arrayList.add(readStrongBinder());
            i--;
        }
        return arrayList;
    }

    public final void readStringList(List<String> list) {
        int size = list.size();
        int i = readInt();
        int i2 = 0;
        while (i2 < size && i2 < i) {
            list.set(i2, readString());
            i2++;
        }
        while (i2 < i) {
            list.add(readString());
            i2++;
        }
        while (i2 < size) {
            list.remove(i);
            i2++;
        }
    }

    public final void readBinderList(List<IBinder> list) {
        int size = list.size();
        int i = readInt();
        int i2 = 0;
        while (i2 < size && i2 < i) {
            list.set(i2, readStrongBinder());
            i2++;
        }
        while (i2 < i) {
            list.add(readStrongBinder());
            i2++;
        }
        while (i2 < size) {
            list.remove(i);
            i2++;
        }
    }

    public final <T> T[] createTypedArray(Parcelable.Creator<T> creator) {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        T[] tArrNewArray = creator.newArray(i);
        for (int i2 = 0; i2 < i; i2++) {
            if (readInt() != 0) {
                tArrNewArray[i2] = creator.createFromParcel(this);
            }
        }
        return tArrNewArray;
    }

    public final <T> void readTypedArray(T[] tArr, Parcelable.Creator<T> creator) {
        int i = readInt();
        if (i != tArr.length) {
            throw new RuntimeException("bad array lengths");
        }
        for (int i2 = 0; i2 < i; i2++) {
            if (readInt() != 0) {
                tArr[i2] = creator.createFromParcel(this);
            } else {
                tArr[i2] = null;
            }
        }
    }

    @Deprecated
    public final <T> T[] readTypedArray(Parcelable.Creator<T> creator) {
        return (T[]) createTypedArray(creator);
    }

    public final <T extends Parcelable> void writeParcelableArray(T[] tArr, int i) {
        if (tArr != null) {
            writeInt(tArr.length);
            for (T t : tArr) {
                writeParcelable(t, i);
            }
            return;
        }
        writeInt(-1);
    }

    public final Object readValue(ClassLoader classLoader) {
        int i = readInt();
        switch (i) {
            case -1:
                return null;
            case 0:
                return readString();
            case 1:
                return Integer.valueOf(readInt());
            case 2:
                return readHashMap(classLoader);
            case 3:
                return readBundle(classLoader);
            case 4:
                return readParcelable(classLoader);
            case 5:
                return Short.valueOf((short) readInt());
            case 6:
                return Long.valueOf(readLong());
            case 7:
                return Float.valueOf(readFloat());
            case 8:
                return Double.valueOf(readDouble());
            case 9:
                return Boolean.valueOf(readInt() == 1);
            case 10:
                return readCharSequence();
            case 11:
                return readArrayList(classLoader);
            case 12:
                return readSparseArray(classLoader);
            case 13:
                return createByteArray();
            case 14:
                return readStringArray();
            case 15:
                return readStrongBinder();
            case 16:
                return readParcelableArray(classLoader);
            case 17:
                return readArray(classLoader);
            case 18:
                return createIntArray();
            case 19:
                return createLongArray();
            case 20:
                return Byte.valueOf(readByte());
            case 21:
                return readSerializable();
            case 22:
                return readSparseBooleanArray();
            case 23:
                return createBooleanArray();
            case 24:
                return readCharSequenceArray();
            default:
                throw new RuntimeException("Parcel " + this + ": Unmarshalling unknown type code " + i + " at offset " + (dataPosition() - 4));
        }
    }

    public final <T extends Parcelable> T readParcelable(ClassLoader classLoader) {
        Parcelable.Creator<T> parcelableCreator = readParcelableCreator(classLoader);
        if (parcelableCreator == null) {
            return null;
        }
        if (parcelableCreator instanceof Parcelable.ClassLoaderCreator) {
            return (T) ((Parcelable.ClassLoaderCreator) parcelableCreator).createFromParcel(this, classLoader);
        }
        return parcelableCreator.createFromParcel(this);
    }

    public final <T extends Parcelable> T readCreator(Parcelable.Creator<T> creator, ClassLoader classLoader) {
        if (creator instanceof Parcelable.ClassLoaderCreator) {
            return (T) ((Parcelable.ClassLoaderCreator) creator).createFromParcel(this, classLoader);
        }
        return creator.createFromParcel(this);
    }

    public final <T extends Parcelable> Parcelable.Creator<T> readParcelableCreator(ClassLoader classLoader) {
        Parcelable.Creator<T> creator;
        String string = readString();
        if (string == null) {
            return null;
        }
        HashMap<ClassLoader, HashMap<String, Parcelable.Creator>> map = mCreators;
        synchronized (map) {
            HashMap<String, Parcelable.Creator> map2 = map.get(classLoader);
            if (map2 == null) {
                map2 = new HashMap<>();
                map.put(classLoader, map2);
            }
            creator = map2.get(string);
            if (creator == null) {
                try {
                    try {
                        try {
                            creator = (Parcelable.Creator) (classLoader == null ? Class.forName(string) : Class.forName(string, true, classLoader)).getField("CREATOR").get(null);
                            if (creator == null) {
                                throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class " + string);
                            }
                            map2.put(string, creator);
                        } catch (IllegalAccessException e) {
                            Log.e(TAG, "Illegal access when unmarshalling: " + string, e);
                            throw new BadParcelableException("IllegalAccessException when unmarshalling: " + string);
                        }
                    } catch (ClassNotFoundException e2) {
                        Log.e(TAG, "Class not found when unmarshalling: " + string, e2);
                        throw new BadParcelableException("ClassNotFoundException when unmarshalling: " + string);
                    } catch (NullPointerException unused) {
                        throw new BadParcelableException("Parcelable protocol requires the CREATOR object to be static on class " + string);
                    }
                } catch (ClassCastException unused2) {
                    throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class " + string);
                } catch (NoSuchFieldException unused3) {
                    throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class " + string);
                }
            }
        }
        return creator;
    }

    public final Parcelable[] readParcelableArray(ClassLoader classLoader) {
        int i = readInt();
        if (i < 0) {
            return null;
        }
        Parcelable[] parcelableArr = new Parcelable[i];
        for (int i2 = 0; i2 < i; i2++) {
            parcelableArr[i2] = readParcelable(classLoader);
        }
        return parcelableArr;
    }

    public final Serializable readSerializable() {
        String string = readString();
        if (string == null) {
            return null;
        }
        try {
            return (Serializable) new ObjectInputStream(new ByteArrayInputStream(createByteArray())).readObject();
        } catch (IOException e) {
            throw new RuntimeException("Parcelable encountered IOException reading a Serializable object (name = " + string + ")", e);
        } catch (ClassNotFoundException e2) {
            throw new RuntimeException("Parcelable encounteredClassNotFoundException reading a Serializable object (name = " + string + ")", e2);
        }
    }

    protected static final Parcel obtain(int i) {
        Parcel[] parcelArr = sHolderPool;
        synchronized (parcelArr) {
            for (int i2 = 0; i2 < 6; i2++) {
                Parcel parcel = parcelArr[i2];
                if (parcel != null) {
                    parcelArr[i2] = null;
                    parcel.init(i);
                    return parcel;
                }
            }
            return new Parcel(i);
        }
    }

    private Parcel(int i) {
        init(i);
    }

    private void init(int i) {
        if (i != 0) {
            this.mNativePtr = i;
            this.mOwnsNativeParcelObject = false;
        } else {
            this.mNativePtr = nativeCreate();
            this.mOwnsNativeParcelObject = true;
        }
    }

    private void freeBuffer() {
        if (this.mOwnsNativeParcelObject) {
            nativeFreeBuffer(this.mNativePtr);
        }
    }

    private void destroy() {
        int i = this.mNativePtr;
        if (i != 0) {
            if (this.mOwnsNativeParcelObject) {
                nativeDestroy(i);
            }
            this.mNativePtr = 0;
        }
    }

    protected void finalize() throws Throwable {
        destroy();
    }

    void readMapInternal(Map map, int i, ClassLoader classLoader) {
        while (i > 0) {
            map.put(readValue(classLoader), readValue(classLoader));
            i--;
        }
    }

    void readArrayMapInternal(ArrayMap arrayMap, int i, ClassLoader classLoader) {
        while (i > 0) {
            arrayMap.append(readValue(classLoader), readValue(classLoader));
            i--;
        }
    }

    void readArrayMapSafelyInternal(ArrayMap arrayMap, int i, ClassLoader classLoader) {
        while (i > 0) {
            arrayMap.put(readValue(classLoader), readValue(classLoader));
            i--;
        }
    }

    private void readListInternal(List list, int i, ClassLoader classLoader) {
        while (i > 0) {
            list.add(readValue(classLoader));
            i--;
        }
    }

    private void readArrayInternal(Object[] objArr, int i, ClassLoader classLoader) {
        for (int i2 = 0; i2 < i; i2++) {
            objArr[i2] = readValue(classLoader);
        }
    }

    private void readSparseArrayInternal(SparseArray sparseArray, int i, ClassLoader classLoader) {
        while (i > 0) {
            sparseArray.append(readInt(), readValue(classLoader));
            i--;
        }
    }

    private void readSparseBooleanArrayInternal(SparseBooleanArray sparseBooleanArray, int i) {
        while (i > 0) {
            int i2 = readInt();
            boolean z = true;
            if (readByte() != 1) {
                z = false;
            }
            sparseBooleanArray.append(i2, z);
            i--;
        }
    }
}
