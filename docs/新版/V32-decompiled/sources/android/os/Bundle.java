package android.os;

import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class Bundle implements Parcelable, Cloneable {
    static final int BUNDLE_MAGIC = 1279544898;
    public static final Parcelable.Creator<Bundle> CREATOR;
    static final boolean DEBUG = false;
    public static final Bundle EMPTY;
    static final Parcel EMPTY_PARCEL;
    private static final String TAG = "Bundle";
    private boolean mAllowFds;
    private ClassLoader mClassLoader;
    private boolean mFdsKnown;
    private boolean mHasFds;
    ArrayMap<String, Object> mMap;
    Parcel mParcelledData;

    static {
        Bundle bundle = new Bundle();
        EMPTY = bundle;
        bundle.mMap = ArrayMap.EMPTY;
        EMPTY_PARCEL = Parcel.obtain();
        CREATOR = new Parcelable.Creator<Bundle>() { // from class: android.os.Bundle.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Bundle createFromParcel(Parcel parcel) {
                return parcel.readBundle();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Bundle[] newArray(int i) {
                return new Bundle[i];
            }
        };
    }

    public Bundle() {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        this.mMap = new ArrayMap<>();
        this.mClassLoader = getClass().getClassLoader();
    }

    Bundle(Parcel parcel) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        readFromParcel(parcel);
    }

    Bundle(Parcel parcel, int i) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        readFromParcelInner(parcel, i);
    }

    public Bundle(ClassLoader classLoader) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        this.mMap = new ArrayMap<>();
        this.mClassLoader = classLoader;
    }

    public Bundle(int i) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        this.mMap = new ArrayMap<>(i);
        this.mClassLoader = getClass().getClassLoader();
    }

    public Bundle(Bundle bundle) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        Parcel parcel = bundle.mParcelledData;
        if (parcel != null) {
            Parcel parcel2 = EMPTY_PARCEL;
            if (parcel == parcel2) {
                this.mParcelledData = parcel2;
            } else {
                Parcel parcelObtain = Parcel.obtain();
                this.mParcelledData = parcelObtain;
                Parcel parcel3 = bundle.mParcelledData;
                parcelObtain.appendFrom(parcel3, 0, parcel3.dataSize());
                this.mParcelledData.setDataPosition(0);
            }
        } else {
            this.mParcelledData = null;
        }
        if (bundle.mMap != null) {
            this.mMap = new ArrayMap<>(bundle.mMap);
        } else {
            this.mMap = null;
        }
        this.mHasFds = bundle.mHasFds;
        this.mFdsKnown = bundle.mFdsKnown;
        this.mClassLoader = bundle.mClassLoader;
    }

    public static Bundle forPair(String str, String str2) {
        Bundle bundle = new Bundle(1);
        bundle.putString(str, str2);
        return bundle;
    }

    public String getPairValue() {
        unparcel();
        int size = this.mMap.size();
        if (size > 1) {
            Log.w(TAG, "getPairValue() used on Bundle with multiple pairs.");
        }
        if (size == 0) {
            return null;
        }
        Object objValueAt = this.mMap.valueAt(0);
        try {
            return (String) objValueAt;
        } catch (ClassCastException e) {
            typeWarning("getPairValue()", objValueAt, "String", e);
            return null;
        }
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.mClassLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return this.mClassLoader;
    }

    public boolean setAllowFds(boolean z) {
        boolean z2 = this.mAllowFds;
        this.mAllowFds = z;
        return z2;
    }

    public Object clone() {
        return new Bundle(this);
    }

    synchronized void unparcel() {
        Parcel parcel = this.mParcelledData;
        if (parcel == null) {
            return;
        }
        if (parcel == EMPTY_PARCEL) {
            ArrayMap<String, Object> arrayMap = this.mMap;
            if (arrayMap == null) {
                this.mMap = new ArrayMap<>(1);
            } else {
                arrayMap.erase();
            }
            this.mParcelledData = null;
            return;
        }
        int i = parcel.readInt();
        if (i < 0) {
            return;
        }
        ArrayMap<String, Object> arrayMap2 = this.mMap;
        if (arrayMap2 == null) {
            this.mMap = new ArrayMap<>(i);
        } else {
            arrayMap2.erase();
            this.mMap.ensureCapacity(i);
        }
        this.mParcelledData.readArrayMapInternal(this.mMap, i, this.mClassLoader);
        this.mParcelledData.recycle();
        this.mParcelledData = null;
    }

    public boolean isParcelled() {
        return this.mParcelledData != null;
    }

    public int size() {
        unparcel();
        return this.mMap.size();
    }

    public boolean isEmpty() {
        unparcel();
        return this.mMap.isEmpty();
    }

    public void clear() {
        unparcel();
        this.mMap.clear();
        this.mHasFds = false;
        this.mFdsKnown = true;
    }

    public boolean containsKey(String str) {
        unparcel();
        return this.mMap.containsKey(str);
    }

    public Object get(String str) {
        unparcel();
        return this.mMap.get(str);
    }

    public void remove(String str) {
        unparcel();
        this.mMap.remove(str);
    }

    public void putAll(Bundle bundle) {
        unparcel();
        bundle.unparcel();
        this.mMap.putAll((ArrayMap<? extends String, ? extends Object>) bundle.mMap);
        this.mHasFds |= bundle.mHasFds;
        this.mFdsKnown = this.mFdsKnown && bundle.mFdsKnown;
    }

    public Set<String> keySet() {
        unparcel();
        return this.mMap.keySet();
    }

    public boolean hasFileDescriptors() {
        if (!this.mFdsKnown) {
            Parcel parcel = this.mParcelledData;
            boolean z = false;
            if (parcel != null) {
                if (parcel.hasFileDescriptors()) {
                    z = true;
                    break;
                }
                this.mHasFds = z;
                this.mFdsKnown = true;
            } else {
                boolean z2 = false;
                for (int size = this.mMap.size() - 1; size >= 0; size--) {
                    Object objValueAt = this.mMap.valueAt(size);
                    if (objValueAt instanceof Parcelable) {
                        if ((((Parcelable) objValueAt).describeContents() & 1) != 0) {
                            z = true;
                            break;
                        }
                    } else if (objValueAt instanceof Parcelable[]) {
                        Parcelable[] parcelableArr = (Parcelable[]) objValueAt;
                        for (int length = parcelableArr.length - 1; length >= 0; length--) {
                            if ((parcelableArr[length].describeContents() & 1) != 0) {
                                z2 = true;
                                break;
                            }
                        }
                    } else if (objValueAt instanceof SparseArray) {
                        SparseArray sparseArray = (SparseArray) objValueAt;
                        for (int size2 = sparseArray.size() - 1; size2 >= 0; size2--) {
                            if ((((Parcelable) sparseArray.get(size2)).describeContents() & 1) != 0) {
                                z2 = true;
                                break;
                                break;
                            }
                        }
                    } else if (objValueAt instanceof ArrayList) {
                        ArrayList arrayList = (ArrayList) objValueAt;
                        if (arrayList.size() > 0 && (arrayList.get(0) instanceof Parcelable)) {
                            for (int size3 = arrayList.size() - 1; size3 >= 0; size3--) {
                                Parcelable parcelable = (Parcelable) arrayList.get(size3);
                                if (parcelable != null && (parcelable.describeContents() & 1) != 0) {
                                    z2 = true;
                                    break;
                                    break;
                                }
                            }
                        }
                    }
                }
                z = z2;
                this.mHasFds = z;
                this.mFdsKnown = true;
            }
        }
        return this.mHasFds;
    }

    public void putBoolean(String str, boolean z) {
        unparcel();
        this.mMap.put(str, Boolean.valueOf(z));
    }

    public void putByte(String str, byte b) {
        unparcel();
        this.mMap.put(str, Byte.valueOf(b));
    }

    public void putChar(String str, char c) {
        unparcel();
        this.mMap.put(str, Character.valueOf(c));
    }

    public void putShort(String str, short s) {
        unparcel();
        this.mMap.put(str, Short.valueOf(s));
    }

    public void putInt(String str, int i) {
        unparcel();
        this.mMap.put(str, Integer.valueOf(i));
    }

    public void putLong(String str, long j) {
        unparcel();
        this.mMap.put(str, Long.valueOf(j));
    }

    public void putFloat(String str, float f) {
        unparcel();
        this.mMap.put(str, Float.valueOf(f));
    }

    public void putDouble(String str, double d) {
        unparcel();
        this.mMap.put(str, Double.valueOf(d));
    }

    public void putString(String str, String str2) {
        unparcel();
        this.mMap.put(str, str2);
    }

    public void putCharSequence(String str, CharSequence charSequence) {
        unparcel();
        this.mMap.put(str, charSequence);
    }

    public void putParcelable(String str, Parcelable parcelable) {
        unparcel();
        this.mMap.put(str, parcelable);
        this.mFdsKnown = false;
    }

    public void putParcelableArray(String str, Parcelable[] parcelableArr) {
        unparcel();
        this.mMap.put(str, parcelableArr);
        this.mFdsKnown = false;
    }

    public void putParcelableArrayList(String str, ArrayList<? extends Parcelable> arrayList) {
        unparcel();
        this.mMap.put(str, arrayList);
        this.mFdsKnown = false;
    }

    public void putParcelableList(String str, List<? extends Parcelable> list) {
        unparcel();
        this.mMap.put(str, list);
        this.mFdsKnown = false;
    }

    public void putSparseParcelableArray(String str, SparseArray<? extends Parcelable> sparseArray) {
        unparcel();
        this.mMap.put(str, sparseArray);
        this.mFdsKnown = false;
    }

    public void putIntegerArrayList(String str, ArrayList<Integer> arrayList) {
        unparcel();
        this.mMap.put(str, arrayList);
    }

    public void putStringArrayList(String str, ArrayList<String> arrayList) {
        unparcel();
        this.mMap.put(str, arrayList);
    }

    public void putCharSequenceArrayList(String str, ArrayList<CharSequence> arrayList) {
        unparcel();
        this.mMap.put(str, arrayList);
    }

    public void putSerializable(String str, Serializable serializable) {
        unparcel();
        this.mMap.put(str, serializable);
    }

    public void putBooleanArray(String str, boolean[] zArr) {
        unparcel();
        this.mMap.put(str, zArr);
    }

    public void putByteArray(String str, byte[] bArr) {
        unparcel();
        this.mMap.put(str, bArr);
    }

    public void putShortArray(String str, short[] sArr) {
        unparcel();
        this.mMap.put(str, sArr);
    }

    public void putCharArray(String str, char[] cArr) {
        unparcel();
        this.mMap.put(str, cArr);
    }

    public void putIntArray(String str, int[] iArr) {
        unparcel();
        this.mMap.put(str, iArr);
    }

    public void putLongArray(String str, long[] jArr) {
        unparcel();
        this.mMap.put(str, jArr);
    }

    public void putFloatArray(String str, float[] fArr) {
        unparcel();
        this.mMap.put(str, fArr);
    }

    public void putDoubleArray(String str, double[] dArr) {
        unparcel();
        this.mMap.put(str, dArr);
    }

    public void putStringArray(String str, String[] strArr) {
        unparcel();
        this.mMap.put(str, strArr);
    }

    public void putCharSequenceArray(String str, CharSequence[] charSequenceArr) {
        unparcel();
        this.mMap.put(str, charSequenceArr);
    }

    public void putBundle(String str, Bundle bundle) {
        unparcel();
        this.mMap.put(str, bundle);
    }

    public void putBinder(String str, IBinder iBinder) {
        unparcel();
        this.mMap.put(str, iBinder);
    }

    @Deprecated
    public void putIBinder(String str, IBinder iBinder) {
        unparcel();
        this.mMap.put(str, iBinder);
    }

    public boolean getBoolean(String str) {
        unparcel();
        return getBoolean(str, false);
    }

    private void typeWarning(String str, Object obj, String str2, Object obj2, ClassCastException classCastException) {
        Log.w(TAG, "Key " + str + " expected " + str2 + " but value was a " + obj.getClass().getName() + ".  The default value " + obj2 + " was returned.");
        Log.w(TAG, "Attempt to cast generated internal exception:", classCastException);
    }

    private void typeWarning(String str, Object obj, String str2, ClassCastException classCastException) {
        typeWarning(str, obj, str2, "<null>", classCastException);
    }

    public boolean getBoolean(String str, boolean z) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return z;
        }
        try {
            return ((Boolean) obj).booleanValue();
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Boolean", Boolean.valueOf(z), e);
            return z;
        }
    }

    public byte getByte(String str) {
        unparcel();
        return getByte(str, (byte) 0).byteValue();
    }

    public Byte getByte(String str, byte b) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return Byte.valueOf(b);
        }
        try {
            return (Byte) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Byte", Byte.valueOf(b), e);
            return Byte.valueOf(b);
        }
    }

    public char getChar(String str) {
        unparcel();
        return getChar(str, (char) 0);
    }

    public char getChar(String str, char c) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return c;
        }
        try {
            return ((Character) obj).charValue();
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Character", Character.valueOf(c), e);
            return c;
        }
    }

    public short getShort(String str) {
        unparcel();
        return getShort(str, (short) 0);
    }

    public short getShort(String str, short s) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return s;
        }
        try {
            return ((Short) obj).shortValue();
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Short", Short.valueOf(s), e);
            return s;
        }
    }

    public int getInt(String str) {
        unparcel();
        return getInt(str, 0);
    }

    public int getInt(String str, int i) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return i;
        }
        try {
            return ((Integer) obj).intValue();
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Integer", Integer.valueOf(i), e);
            return i;
        }
    }

    public long getLong(String str) {
        unparcel();
        return getLong(str, 0L);
    }

    public long getLong(String str, long j) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return j;
        }
        try {
            return ((Long) obj).longValue();
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Long", Long.valueOf(j), e);
            return j;
        }
    }

    public float getFloat(String str) {
        unparcel();
        return getFloat(str, 0.0f);
    }

    public float getFloat(String str, float f) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return f;
        }
        try {
            return ((Float) obj).floatValue();
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Float", Float.valueOf(f), e);
            return f;
        }
    }

    public double getDouble(String str) {
        unparcel();
        return getDouble(str, 0.0d);
    }

    public double getDouble(String str, double d) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return d;
        }
        try {
            return ((Double) obj).doubleValue();
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Double", Double.valueOf(d), e);
            return d;
        }
    }

    public String getString(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        try {
            return (String) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "String", e);
            return null;
        }
    }

    public String getString(String str, String str2) {
        String string = getString(str);
        return string == null ? str2 : string;
    }

    public CharSequence getCharSequence(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        try {
            return (CharSequence) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "CharSequence", e);
            return null;
        }
    }

    public CharSequence getCharSequence(String str, CharSequence charSequence) {
        CharSequence charSequence2 = getCharSequence(str);
        return charSequence2 == null ? charSequence : charSequence2;
    }

    public Bundle getBundle(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (Bundle) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, TAG, e);
            return null;
        }
    }

    public <T extends Parcelable> T getParcelable(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (T) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Parcelable", e);
            return null;
        }
    }

    public Parcelable[] getParcelableArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (Parcelable[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Parcelable[]", e);
            return null;
        }
    }

    public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (ArrayList) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "ArrayList", e);
            return null;
        }
    }

    public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (SparseArray) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "SparseArray", e);
            return null;
        }
    }

    public Serializable getSerializable(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (Serializable) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Serializable", e);
            return null;
        }
    }

    public ArrayList<Integer> getIntegerArrayList(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (ArrayList) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "ArrayList<Integer>", e);
            return null;
        }
    }

    public ArrayList<String> getStringArrayList(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (ArrayList) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "ArrayList<String>", e);
            return null;
        }
    }

    public ArrayList<CharSequence> getCharSequenceArrayList(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (ArrayList) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "ArrayList<CharSequence>", e);
            return null;
        }
    }

    public boolean[] getBooleanArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (boolean[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "byte[]", e);
            return null;
        }
    }

    public byte[] getByteArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (byte[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "byte[]", e);
            return null;
        }
    }

    public short[] getShortArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (short[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "short[]", e);
            return null;
        }
    }

    public char[] getCharArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (char[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "char[]", e);
            return null;
        }
    }

    public int[] getIntArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (int[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "int[]", e);
            return null;
        }
    }

    public long[] getLongArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (long[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "long[]", e);
            return null;
        }
    }

    public float[] getFloatArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (float[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "float[]", e);
            return null;
        }
    }

    public double[] getDoubleArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (double[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "double[]", e);
            return null;
        }
    }

    public String[] getStringArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (String[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "String[]", e);
            return null;
        }
    }

    public CharSequence[] getCharSequenceArray(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (CharSequence[]) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "CharSequence[]", e);
            return null;
        }
    }

    public IBinder getBinder(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (IBinder) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "IBinder", e);
            return null;
        }
    }

    @Deprecated
    public IBinder getIBinder(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (IBinder) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "IBinder", e);
            return null;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return hasFileDescriptors() ? 1 : 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        boolean zPushAllowFds = parcel.pushAllowFds(this.mAllowFds);
        try {
            Parcel parcel2 = this.mParcelledData;
            if (parcel2 != null) {
                if (parcel2 == EMPTY_PARCEL) {
                    parcel.writeInt(0);
                } else {
                    int iDataSize = parcel2.dataSize();
                    parcel.writeInt(iDataSize);
                    parcel.writeInt(BUNDLE_MAGIC);
                    parcel.appendFrom(this.mParcelledData, 0, iDataSize);
                }
            } else {
                ArrayMap<String, Object> arrayMap = this.mMap;
                if (arrayMap != null && arrayMap.size() > 0) {
                    int iDataPosition = parcel.dataPosition();
                    parcel.writeInt(-1);
                    parcel.writeInt(BUNDLE_MAGIC);
                    int iDataPosition2 = parcel.dataPosition();
                    parcel.writeArrayMapInternal(this.mMap);
                    int iDataPosition3 = parcel.dataPosition();
                    parcel.setDataPosition(iDataPosition);
                    parcel.writeInt(iDataPosition3 - iDataPosition2);
                    parcel.setDataPosition(iDataPosition3);
                }
                parcel.writeInt(0);
            }
        } finally {
            parcel.restoreAllowFds(zPushAllowFds);
        }
    }

    public void readFromParcel(Parcel parcel) {
        int i = parcel.readInt();
        if (i < 0) {
            throw new RuntimeException("Bad length in parcel: " + i);
        }
        readFromParcelInner(parcel, i);
    }

    void readFromParcelInner(Parcel parcel, int i) {
        if (i == 0) {
            this.mParcelledData = EMPTY_PARCEL;
            this.mHasFds = false;
            this.mFdsKnown = true;
            return;
        }
        int i2 = parcel.readInt();
        if (i2 != BUNDLE_MAGIC) {
            throw new IllegalStateException("Bad magic number for Bundle: 0x" + Integer.toHexString(i2));
        }
        int iDataPosition = parcel.dataPosition();
        parcel.setDataPosition(iDataPosition + i);
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.setDataPosition(0);
        parcelObtain.appendFrom(parcel, iDataPosition, i);
        parcelObtain.setDataPosition(0);
        this.mParcelledData = parcelObtain;
        this.mHasFds = parcelObtain.hasFileDescriptors();
        this.mFdsKnown = true;
    }

    public synchronized String toString() {
        Parcel parcel = this.mParcelledData;
        if (parcel != null) {
            if (parcel == EMPTY_PARCEL) {
                return "Bundle[EMPTY_PARCEL]";
            }
            return "Bundle[mParcelledData.dataSize=" + this.mParcelledData.dataSize() + "]";
        }
        return "Bundle[" + this.mMap.toString() + "]";
    }
}
