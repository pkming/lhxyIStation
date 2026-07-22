package android.hardware.camera2.impl;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.Face;
import android.hardware.camera2.Rational;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class CameraMetadataNative extends CameraMetadata implements Parcelable {
    public static final Parcelable.Creator<CameraMetadataNative> CREATOR;
    private static final int NATIVE_JPEG_FORMAT = 33;
    public static final int NUM_TYPES = 6;
    private static final String TAG = "CameraMetadataJV";
    public static final int TYPE_BYTE = 0;
    public static final int TYPE_DOUBLE = 4;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_INT32 = 1;
    public static final int TYPE_INT64 = 3;
    public static final int TYPE_RATIONAL = 5;
    private static final boolean VERBOSE;
    private static final HashMap<Class<? extends Enum>, int[]> sEnumValues;
    static HashMap<Class<?>, MetadataMarshalClass<?>> sMarshalerMap;
    private long mMetadataPtr;

    private native long nativeAllocate();

    private native long nativeAllocateCopy(CameraMetadataNative cameraMetadataNative) throws NullPointerException;

    private static native void nativeClassInit();

    private native synchronized void nativeClose();

    private native synchronized int nativeGetEntryCount();

    private static native int nativeGetTagFromKey(String str) throws IllegalArgumentException;

    private static native int nativeGetTypeFromTag(int i) throws IllegalArgumentException;

    private native synchronized boolean nativeIsEmpty();

    private native synchronized void nativeReadFromParcel(Parcel parcel);

    private native synchronized byte[] nativeReadValues(int i);

    private native synchronized void nativeSwap(CameraMetadataNative cameraMetadataNative) throws NullPointerException;

    private native synchronized void nativeWriteToParcel(Parcel parcel);

    private native synchronized void nativeWriteValues(int i, byte[] bArr);

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    static {
        boolean zIsLoggable = Log.isLoggable(TAG, 2);
        VERBOSE = zIsLoggable;
        CREATOR = new Parcelable.Creator<CameraMetadataNative>() { // from class: android.hardware.camera2.impl.CameraMetadataNative.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public CameraMetadataNative createFromParcel(Parcel parcel) {
                CameraMetadataNative cameraMetadataNative = new CameraMetadataNative();
                cameraMetadataNative.readFromParcel(parcel);
                return cameraMetadataNative;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public CameraMetadataNative[] newArray(int i) {
                return new CameraMetadataNative[i];
            }
        };
        sEnumValues = new HashMap<>();
        sMarshalerMap = new HashMap<>();
        nativeClassInit();
        if (zIsLoggable) {
            Log.v(TAG, "Shall register metadata marshalers");
        }
        registerMarshaler(new MetadataMarshalRect());
        registerMarshaler(new MetadataMarshalSize());
        registerMarshaler(new MetadataMarshalString());
        if (zIsLoggable) {
            Log.v(TAG, "Registered metadata marshalers");
        }
    }

    public CameraMetadataNative() {
        long jNativeAllocate = nativeAllocate();
        this.mMetadataPtr = jNativeAllocate;
        if (jNativeAllocate == 0) {
            throw new OutOfMemoryError("Failed to allocate native CameraMetadata");
        }
    }

    public CameraMetadataNative(CameraMetadataNative cameraMetadataNative) {
        long jNativeAllocateCopy = nativeAllocateCopy(cameraMetadataNative);
        this.mMetadataPtr = jNativeAllocateCopy;
        if (jNativeAllocateCopy == 0) {
            throw new OutOfMemoryError("Failed to allocate native CameraMetadata");
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        nativeWriteToParcel(parcel);
    }

    @Override // android.hardware.camera2.CameraMetadata
    public <T> T get(CameraMetadata.Key<T> key) {
        T t = (T) getOverride(key);
        return t != null ? t : (T) getBase(key);
    }

    public void readFromParcel(Parcel parcel) {
        nativeReadFromParcel(parcel);
    }

    public <T> void set(CameraMetadata.Key<T> key, T t) {
        if (setOverride(key, t)) {
            return;
        }
        setBase(key, t);
    }

    private void close() {
        nativeClose();
        this.mMetadataPtr = 0L;
    }

    private static int getTypeSize(int i) {
        if (i == 0) {
            return 1;
        }
        if (i == 1 || i == 2) {
            return 4;
        }
        if (i == 3 || i == 4 || i == 5) {
            return 8;
        }
        throw new UnsupportedOperationException("Unknown type, can't get size " + i);
    }

    private static Class<?> getExpectedType(int i) {
        if (i == 0) {
            return Byte.TYPE;
        }
        if (i == 1) {
            return Integer.TYPE;
        }
        if (i == 2) {
            return Float.TYPE;
        }
        if (i == 3) {
            return Long.TYPE;
        }
        if (i == 4) {
            return Double.TYPE;
        }
        if (i == 5) {
            return Rational.class;
        }
        throw new UnsupportedOperationException("Unknown type, can't map to Java type " + i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [T] */
    /* JADX WARN: Type inference failed for: r2v24 */
    /* JADX WARN: Type inference failed for: r2v25 */
    /* JADX WARN: Type inference failed for: r2v26 */
    /* JADX WARN: Type inference failed for: r2v7 */
    private static <T> int packSingleNative(T t, ByteBuffer byteBuffer, Class<T> cls, int i, boolean z) {
        ?? r2;
        if (!z) {
            if (i == 0 && cls == Boolean.TYPE) {
                r2 = (T) Byte.valueOf(((Boolean) t).booleanValue() ? (byte) 1 : (byte) 0);
            } else if (i == 0 && cls == Integer.TYPE) {
                r2 = (T) Byte.valueOf((byte) ((Integer) t).intValue());
            } else {
                r2 = t;
                if (cls != getExpectedType(i)) {
                    throw new UnsupportedOperationException("Tried to pack a type of " + cls + " but we expected the type to be " + getExpectedType(i));
                }
            }
            if (i == 0) {
                byteBuffer.put(((Byte) r2).byteValue());
            } else if (i == 1) {
                byteBuffer.putInt(((Integer) r2).intValue());
            } else if (i == 2) {
                byteBuffer.putFloat(((Float) r2).floatValue());
            } else if (i == 3) {
                byteBuffer.putLong(((Long) r2).longValue());
            } else if (i == 4) {
                byteBuffer.putDouble(((Double) r2).doubleValue());
            } else if (i == 5) {
                Rational rational = (Rational) r2;
                byteBuffer.putInt(rational.getNumerator());
                byteBuffer.putInt(rational.getDenominator());
            }
        }
        return getTypeSize(i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <T> int packSingle(T t, ByteBuffer byteBuffer, Class<T> cls, int i, boolean z) {
        if (cls.isPrimitive() || cls == Rational.class) {
            return packSingleNative(t, byteBuffer, cls, i, z);
        }
        if (cls.isEnum()) {
            return packEnum((Enum) t, byteBuffer, cls, i, z);
        }
        if (cls.isArray()) {
            return packArray(t, byteBuffer, cls, i, z);
        }
        return packClass(t, byteBuffer, cls, i, z);
    }

    private static <T extends Enum<T>> int packEnum(T t, ByteBuffer byteBuffer, Class<T> cls, int i, boolean z) {
        return packSingleNative(Integer.valueOf(getEnumValue(t)), byteBuffer, Integer.TYPE, i, z);
    }

    private static <T> int packClass(T t, ByteBuffer byteBuffer, Class<T> cls, int i, boolean z) {
        MetadataMarshalClass marshaler = getMarshaler(cls, i);
        if (marshaler == null) {
            throw new IllegalArgumentException(String.format("Unknown Key type: %s", cls));
        }
        return marshaler.marshal(t, byteBuffer, i, z);
    }

    private static <T> int packArray(T t, ByteBuffer byteBuffer, Class<T> cls, int i, boolean z) {
        int length = Array.getLength(t);
        Class<?> componentType = cls.getComponentType();
        int iPackSingle = 0;
        for (int i2 = 0; i2 < length; i2++) {
            iPackSingle += packSingle(Array.get(t, i2), byteBuffer, componentType, i, z);
        }
        return iPackSingle;
    }

    private static <T> T unpackSingleNative(ByteBuffer byteBuffer, Class<T> cls, int i) {
        T t;
        if (i == 0) {
            t = (T) Byte.valueOf(byteBuffer.get());
        } else if (i == 1) {
            t = (T) Integer.valueOf(byteBuffer.getInt());
        } else if (i == 2) {
            t = (T) Float.valueOf(byteBuffer.getFloat());
        } else if (i == 3) {
            t = (T) Long.valueOf(byteBuffer.getLong());
        } else if (i == 4) {
            t = (T) Double.valueOf(byteBuffer.getDouble());
        } else if (i == 5) {
            t = (T) new Rational(byteBuffer.getInt(), byteBuffer.getInt());
        } else {
            throw new UnsupportedOperationException("Unknown type, can't unpack a native type " + i);
        }
        if (i == 0 && cls == Boolean.TYPE) {
            return (T) Boolean.valueOf(((Byte) t).byteValue() != 0);
        }
        if (i == 0 && cls == Integer.TYPE) {
            return (T) Integer.valueOf(((Byte) t).byteValue());
        }
        if (cls == getExpectedType(i)) {
            return t;
        }
        throw new UnsupportedOperationException("Tried to unpack a type of " + cls + " but we expected the type to be " + getExpectedType(i));
    }

    private static <T> T unpackSingle(ByteBuffer byteBuffer, Class<T> cls, int i) {
        if (cls.isPrimitive() || cls == Rational.class) {
            return (T) unpackSingleNative(byteBuffer, cls, i);
        }
        if (cls.isEnum()) {
            return (T) unpackEnum(byteBuffer, cls, i);
        }
        if (cls.isArray()) {
            return (T) unpackArray(byteBuffer, cls, i);
        }
        return (T) unpackClass(byteBuffer, cls, i);
    }

    private static <T extends Enum<T>> T unpackEnum(ByteBuffer byteBuffer, Class<T> cls, int i) {
        return (T) getEnumFromValue(cls, ((Integer) unpackSingleNative(byteBuffer, Integer.TYPE, i)).intValue());
    }

    private static <T> T unpackClass(ByteBuffer byteBuffer, Class<T> cls, int i) {
        MetadataMarshalClass marshaler = getMarshaler(cls, i);
        if (marshaler == null) {
            throw new IllegalArgumentException("Unknown class type: " + cls);
        }
        return (T) marshaler.unmarshal(byteBuffer, i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <T> T unpackArray(ByteBuffer byteBuffer, Class<T> cls, int i) {
        T t;
        Class<?> componentType = cls.getComponentType();
        int typeSize = getTypeSize(i);
        MetadataMarshalClass marshaler = getMarshaler(componentType, i);
        if (marshaler != null) {
            typeSize = marshaler.getNativeSize(i);
        }
        if (typeSize != -1) {
            int iRemaining = byteBuffer.remaining();
            int i2 = iRemaining / typeSize;
            if (VERBOSE) {
                Log.v(TAG, String.format("Attempting to unpack array (count = %d, element size = %d, bytes remaining = %d) for type %s", Integer.valueOf(i2), Integer.valueOf(typeSize), Integer.valueOf(iRemaining), cls));
            }
            t = (T) Array.newInstance(componentType, i2);
            for (int i3 = 0; i3 < i2; i3++) {
                Array.set(t, i3, unpackSingle(byteBuffer, componentType, i));
            }
        } else {
            ArrayList arrayList = new ArrayList();
            int typeSize2 = getTypeSize(i);
            while (byteBuffer.remaining() >= typeSize2) {
                arrayList.add(unpackSingle(byteBuffer, componentType, i));
            }
            t = (T) arrayList.toArray((Object[]) Array.newInstance(componentType, 0));
        }
        if (byteBuffer.remaining() != 0) {
            Log.e(TAG, "Trailing bytes (" + byteBuffer.remaining() + ") left over after unpacking " + cls);
        }
        return t;
    }

    private <T> T getBase(CameraMetadata.Key<T> key) {
        int tag = key.getTag();
        byte[] values = readValues(tag);
        if (values == null) {
            return null;
        }
        return (T) unpackSingle(ByteBuffer.wrap(values).order(ByteOrder.nativeOrder()), key.getType(), getNativeType(tag));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> T getOverride(CameraMetadata.Key<T> key) {
        if (key.equals(CameraCharacteristics.SCALER_AVAILABLE_FORMATS)) {
            return (T) getAvailableFormats();
        }
        if (key.equals(CaptureResult.STATISTICS_FACES)) {
            return (T) getFaces();
        }
        if (key.equals(CaptureResult.STATISTICS_FACE_RECTANGLES)) {
            return (T) fixFaceRectangles();
        }
        return null;
    }

    private int[] getAvailableFormats() {
        int[] iArr = (int[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_FORMATS);
        for (int i = 0; i < iArr.length; i++) {
            if (iArr[i] == 33) {
                iArr[i] = 256;
            }
        }
        return iArr;
    }

    private Face[] getFaces() {
        Integer num = (Integer) get(CaptureResult.STATISTICS_FACE_DETECT_MODE);
        int i = 0;
        Integer num2 = 1;
        if (num == null) {
            Log.w(TAG, "Face detect mode metadata is null, assuming the mode is SIMPLE");
            num = num2;
        } else {
            if (num.intValue() == 0) {
                return new Face[0];
            }
            if (num.intValue() != 1 && num.intValue() != 2) {
                Log.w(TAG, "Unknown face detect mode: " + num);
                return new Face[0];
            }
        }
        byte[] bArr = (byte[]) get(CaptureResult.STATISTICS_FACE_SCORES);
        Rect[] rectArr = (Rect[]) get(CaptureResult.STATISTICS_FACE_RECTANGLES);
        if (bArr == null || rectArr == null) {
            Log.w(TAG, "Expect face scores and rectangles to be non-null");
            return new Face[0];
        }
        if (bArr.length != rectArr.length) {
            Log.w(TAG, String.format("Face score size(%d) doesn match face rectangle size(%d)!", Integer.valueOf(bArr.length), Integer.valueOf(rectArr.length)));
        }
        int iMin = Math.min(bArr.length, rectArr.length);
        int[] iArr = (int[]) get(CaptureResult.STATISTICS_FACE_IDS);
        int[] iArr2 = (int[]) get(CaptureResult.STATISTICS_FACE_LANDMARKS);
        if (num.intValue() != 2) {
            num2 = num;
        } else if (iArr == null || iArr2 == null) {
            Log.w(TAG, "Expect face ids and landmarks to be non-null for FULL mode,fallback to SIMPLE mode");
        } else {
            if (iArr.length != iMin || iArr2.length != iMin * 6) {
                Log.w(TAG, String.format("Face id size(%d), or face landmark size(%d) don'tmatch face number(%d)!", Integer.valueOf(iArr.length), Integer.valueOf(iArr2.length * 6), Integer.valueOf(iMin)));
            }
            iMin = Math.min(Math.min(iMin, iArr.length), iArr2.length / 6);
            num2 = num;
        }
        ArrayList arrayList = new ArrayList();
        if (num2.intValue() == 1) {
            while (i < iMin) {
                if (bArr[i] <= 100 && bArr[i] >= 1) {
                    arrayList.add(new Face(rectArr[i], bArr[i]));
                }
                i++;
            }
        } else {
            while (i < iMin) {
                if (bArr[i] <= 100 && bArr[i] >= 1 && iArr[i] >= 0) {
                    int i2 = i * 6;
                    arrayList.add(new Face(rectArr[i], bArr[i], iArr[i], new Point(iArr2[i2], iArr2[i2 + 1]), new Point(iArr2[i2 + 2], iArr2[i2 + 3]), new Point(iArr2[i2 + 4], iArr2[i2 + 5])));
                }
                i++;
            }
        }
        Face[] faceArr = new Face[arrayList.size()];
        arrayList.toArray(faceArr);
        return faceArr;
    }

    private Rect[] fixFaceRectangles() {
        Rect[] rectArr = (Rect[]) getBase(CaptureResult.STATISTICS_FACE_RECTANGLES);
        if (rectArr == null) {
            return null;
        }
        Rect[] rectArr2 = new Rect[rectArr.length];
        for (int i = 0; i < rectArr.length; i++) {
            rectArr2[i] = new Rect(rectArr[i].left, rectArr[i].top, rectArr[i].right - rectArr[i].left, rectArr[i].bottom - rectArr[i].top);
        }
        return rectArr2;
    }

    private <T> void setBase(CameraMetadata.Key<T> key, T t) {
        int tag = key.getTag();
        if (t == null) {
            writeValues(tag, null);
            return;
        }
        int nativeType = getNativeType(tag);
        byte[] bArr = new byte[packSingle(t, null, key.getType(), nativeType, true)];
        packSingle(t, ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder()), key.getType(), nativeType, false);
        writeValues(tag, bArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> boolean setOverride(CameraMetadata.Key<T> key, T t) {
        if (key.equals(CameraCharacteristics.SCALER_AVAILABLE_FORMATS)) {
            return setAvailableFormats((int[]) t);
        }
        return false;
    }

    private boolean setAvailableFormats(int[] iArr) {
        if (iArr == null) {
            return false;
        }
        int[] iArr2 = new int[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr2[i] = iArr[i];
            if (iArr[i] == 256) {
                iArr2[i] = 33;
            }
        }
        setBase(CameraCharacteristics.SCALER_AVAILABLE_FORMATS, iArr2);
        return true;
    }

    public void swap(CameraMetadataNative cameraMetadataNative) {
        nativeSwap(cameraMetadataNative);
    }

    public int getEntryCount() {
        return nativeGetEntryCount();
    }

    public boolean isEmpty() {
        return nativeIsEmpty();
    }

    public static int getTag(String str) {
        return nativeGetTagFromKey(str);
    }

    public static int getNativeType(int i) {
        return nativeGetTypeFromTag(i);
    }

    public void writeValues(int i, byte[] bArr) {
        nativeWriteValues(i, bArr);
    }

    public byte[] readValues(int i) {
        return nativeReadValues(i);
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    public static <T extends Enum<T>> void registerEnumValues(Class<T> cls, int[] iArr) {
        if (cls.getEnumConstants().length != iArr.length) {
            throw new IllegalArgumentException("Expected values array to be the same size as the enumTypes values " + iArr.length + " for type " + cls);
        }
        if (VERBOSE) {
            Log.v(TAG, "Registered enum values for type " + cls + " values");
        }
        sEnumValues.put(cls, iArr);
    }

    private static <T extends Enum<T>> int getEnumValue(T t) {
        int[] iArr = sEnumValues.get(t.getClass());
        int iOrdinal = t.ordinal();
        return iArr != null ? iArr[iOrdinal] : iOrdinal;
    }

    private static <T extends Enum<T>> T getEnumFromValue(Class<T> cls, int i) {
        int i2;
        int[] iArr = sEnumValues.get(cls);
        if (iArr != null) {
            i2 = -1;
            int i3 = 0;
            while (true) {
                if (i3 >= iArr.length) {
                    break;
                }
                if (iArr[i3] == i) {
                    i2 = i3;
                    break;
                }
                i3++;
            }
        } else {
            i2 = i;
        }
        T[] enumConstants = cls.getEnumConstants();
        if (i2 < 0 || i2 >= enumConstants.length) {
            Object[] objArr = new Object[3];
            objArr[0] = Integer.valueOf(i);
            objArr[1] = cls;
            objArr[2] = Boolean.valueOf(iArr != null);
            throw new IllegalArgumentException(String.format("Argument 'value' (%d) was not a valid enum value for type %s (registered? %b)", objArr));
        }
        return enumConstants[i2];
    }

    private static <T> void registerMarshaler(MetadataMarshalClass<T> metadataMarshalClass) {
        sMarshalerMap.put(metadataMarshalClass.getMarshalingClass(), metadataMarshalClass);
    }

    private static <T> MetadataMarshalClass<T> getMarshaler(Class<T> cls, int i) {
        MetadataMarshalClass<T> metadataMarshalClass = (MetadataMarshalClass) sMarshalerMap.get(cls);
        if (metadataMarshalClass == null || metadataMarshalClass.isNativeTypeSupported(i)) {
            return metadataMarshalClass;
        }
        throw new UnsupportedOperationException("Unsupported type " + i + " to be marshalled to/from a " + cls);
    }
}
