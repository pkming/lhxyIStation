package android.database;

import android.content.res.Resources;
import android.database.sqlite.SQLiteClosable;
import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.util.Log;
import android.util.SparseIntArray;
import cn.yunzhisheng.asr.JniUscClient;
import dalvik.system.CloseGuard;

/* JADX INFO: loaded from: classes.dex */
public class CursorWindow extends SQLiteClosable implements Parcelable {
    private static final String STATS_TAG = "CursorWindowStats";
    private final CloseGuard mCloseGuard;
    private final String mName;
    private int mStartPos;
    public int mWindowPtr;
    private static final int sCursorWindowSize = Resources.getSystem().getInteger(17694779) * 1024;
    public static final Parcelable.Creator<CursorWindow> CREATOR = new Parcelable.Creator<CursorWindow>() { // from class: android.database.CursorWindow.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CursorWindow createFromParcel(Parcel parcel) {
            return new CursorWindow(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CursorWindow[] newArray(int i) {
            return new CursorWindow[i];
        }
    };
    private static final SparseIntArray sWindowToPidMap = new SparseIntArray();

    private static native boolean nativeAllocRow(int i);

    private static native void nativeClear(int i);

    private static native void nativeCopyStringToBuffer(int i, int i2, int i3, CharArrayBuffer charArrayBuffer);

    private static native int nativeCreate(String str, int i);

    private static native int nativeCreateFromParcel(Parcel parcel);

    private static native void nativeDispose(int i);

    private static native void nativeFreeLastRow(int i);

    private static native byte[] nativeGetBlob(int i, int i2, int i3);

    private static native double nativeGetDouble(int i, int i2, int i3);

    private static native long nativeGetLong(int i, int i2, int i3);

    private static native String nativeGetName(int i);

    private static native int nativeGetNumRows(int i);

    private static native String nativeGetString(int i, int i2, int i3);

    private static native int nativeGetType(int i, int i2, int i3);

    private static native boolean nativePutBlob(int i, byte[] bArr, int i2, int i3);

    private static native boolean nativePutDouble(int i, double d, int i2, int i3);

    private static native boolean nativePutLong(int i, long j, int i2, int i3);

    private static native boolean nativePutNull(int i, int i2, int i3);

    private static native boolean nativePutString(int i, String str, int i2, int i3);

    private static native boolean nativeSetNumColumns(int i, int i2);

    private static native void nativeWriteToParcel(int i, Parcel parcel);

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CursorWindow(String str) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mStartPos = 0;
        str = (str == null || str.length() == 0) ? "<unnamed>" : str;
        this.mName = str;
        int i = sCursorWindowSize;
        int iNativeCreate = nativeCreate(str, i);
        this.mWindowPtr = iNativeCreate;
        if (iNativeCreate == 0) {
            throw new CursorWindowAllocationException("Cursor window allocation of " + (i / 1024) + " kb failed. " + printStats());
        }
        closeGuard.open(JniUscClient.r);
        recordNewWindow(Binder.getCallingPid(), this.mWindowPtr);
    }

    @Deprecated
    public CursorWindow(boolean z) {
        this((String) null);
    }

    private CursorWindow(Parcel parcel) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mStartPos = parcel.readInt();
        int iNativeCreateFromParcel = nativeCreateFromParcel(parcel);
        this.mWindowPtr = iNativeCreateFromParcel;
        if (iNativeCreateFromParcel == 0) {
            throw new CursorWindowAllocationException("Cursor window could not be created from binder.");
        }
        this.mName = nativeGetName(iNativeCreateFromParcel);
        closeGuard.open(JniUscClient.r);
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            dispose();
        } finally {
            super.finalize();
        }
    }

    private void dispose() {
        CloseGuard closeGuard = this.mCloseGuard;
        if (closeGuard != null) {
            closeGuard.close();
        }
        int i = this.mWindowPtr;
        if (i != 0) {
            recordClosingOfWindow(i);
            nativeDispose(this.mWindowPtr);
            this.mWindowPtr = 0;
        }
    }

    public String getName() {
        return this.mName;
    }

    public void clear() {
        acquireReference();
        try {
            this.mStartPos = 0;
            nativeClear(this.mWindowPtr);
        } finally {
            releaseReference();
        }
    }

    public int getStartPosition() {
        return this.mStartPos;
    }

    public void setStartPosition(int i) {
        this.mStartPos = i;
    }

    public int getNumRows() {
        acquireReference();
        try {
            return nativeGetNumRows(this.mWindowPtr);
        } finally {
            releaseReference();
        }
    }

    public boolean setNumColumns(int i) {
        acquireReference();
        try {
            return nativeSetNumColumns(this.mWindowPtr, i);
        } finally {
            releaseReference();
        }
    }

    public boolean allocRow() {
        acquireReference();
        try {
            return nativeAllocRow(this.mWindowPtr);
        } finally {
            releaseReference();
        }
    }

    public void freeLastRow() {
        acquireReference();
        try {
            nativeFreeLastRow(this.mWindowPtr);
        } finally {
            releaseReference();
        }
    }

    @Deprecated
    public boolean isNull(int i, int i2) {
        return getType(i, i2) == 0;
    }

    @Deprecated
    public boolean isBlob(int i, int i2) {
        int type = getType(i, i2);
        return type == 4 || type == 0;
    }

    @Deprecated
    public boolean isLong(int i, int i2) {
        return getType(i, i2) == 1;
    }

    @Deprecated
    public boolean isFloat(int i, int i2) {
        return getType(i, i2) == 2;
    }

    @Deprecated
    public boolean isString(int i, int i2) {
        int type = getType(i, i2);
        return type == 3 || type == 0;
    }

    public int getType(int i, int i2) {
        acquireReference();
        try {
            return nativeGetType(this.mWindowPtr, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public byte[] getBlob(int i, int i2) {
        acquireReference();
        try {
            return nativeGetBlob(this.mWindowPtr, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public String getString(int i, int i2) {
        acquireReference();
        try {
            return nativeGetString(this.mWindowPtr, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public void copyStringToBuffer(int i, int i2, CharArrayBuffer charArrayBuffer) {
        if (charArrayBuffer == null) {
            throw new IllegalArgumentException("CharArrayBuffer should not be null");
        }
        acquireReference();
        try {
            nativeCopyStringToBuffer(this.mWindowPtr, i - this.mStartPos, i2, charArrayBuffer);
        } finally {
            releaseReference();
        }
    }

    public long getLong(int i, int i2) {
        acquireReference();
        try {
            return nativeGetLong(this.mWindowPtr, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public double getDouble(int i, int i2) {
        acquireReference();
        try {
            return nativeGetDouble(this.mWindowPtr, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public short getShort(int i, int i2) {
        return (short) getLong(i, i2);
    }

    public int getInt(int i, int i2) {
        return (int) getLong(i, i2);
    }

    public float getFloat(int i, int i2) {
        return (float) getDouble(i, i2);
    }

    public boolean putBlob(byte[] bArr, int i, int i2) {
        acquireReference();
        try {
            return nativePutBlob(this.mWindowPtr, bArr, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public boolean putString(String str, int i, int i2) {
        acquireReference();
        try {
            return nativePutString(this.mWindowPtr, str, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public boolean putLong(long j, int i, int i2) {
        acquireReference();
        try {
            return nativePutLong(this.mWindowPtr, j, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public boolean putDouble(double d, int i, int i2) {
        acquireReference();
        try {
            return nativePutDouble(this.mWindowPtr, d, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public boolean putNull(int i, int i2) {
        acquireReference();
        try {
            return nativePutNull(this.mWindowPtr, i - this.mStartPos, i2);
        } finally {
            releaseReference();
        }
    }

    public static CursorWindow newFromParcel(Parcel parcel) {
        return CREATOR.createFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        acquireReference();
        try {
            parcel.writeInt(this.mStartPos);
            nativeWriteToParcel(this.mWindowPtr, parcel);
            releaseReference();
            if ((i & 1) != 0) {
            }
        } finally {
            releaseReference();
        }
    }

    @Override // android.database.sqlite.SQLiteClosable
    protected void onAllReferencesReleased() {
        dispose();
    }

    private void recordNewWindow(int i, int i2) {
        SparseIntArray sparseIntArray = sWindowToPidMap;
        synchronized (sparseIntArray) {
            sparseIntArray.put(i2, i);
            if (Log.isLoggable(STATS_TAG, 2)) {
                Log.i(STATS_TAG, "Created a new Cursor. " + printStats());
            }
        }
    }

    private void recordClosingOfWindow(int i) {
        SparseIntArray sparseIntArray = sWindowToPidMap;
        synchronized (sparseIntArray) {
            if (sparseIntArray.size() == 0) {
                return;
            }
            sparseIntArray.delete(i);
        }
    }

    private String printStats() {
        StringBuilder sb = new StringBuilder();
        int iMyPid = Process.myPid();
        SparseIntArray sparseIntArray = new SparseIntArray();
        SparseIntArray sparseIntArray2 = sWindowToPidMap;
        synchronized (sparseIntArray2) {
            int size = sparseIntArray2.size();
            if (size == 0) {
                return "";
            }
            for (int i = 0; i < size; i++) {
                int iValueAt = sWindowToPidMap.valueAt(i);
                sparseIntArray.put(iValueAt, sparseIntArray.get(iValueAt) + 1);
            }
            int size2 = sparseIntArray.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size2; i3++) {
                sb.append(" (# cursors opened by ");
                int iKeyAt = sparseIntArray.keyAt(i3);
                if (iKeyAt == iMyPid) {
                    sb.append("this proc=");
                } else {
                    sb.append("pid " + iKeyAt + "=");
                }
                int i4 = sparseIntArray.get(iKeyAt);
                sb.append(i4 + ")");
                i2 += i4;
            }
            return "# Open Cursors=" + i2 + (sb.length() > 980 ? sb.substring(0, 980) : sb.toString());
        }
    }

    public String toString() {
        return getName() + " {" + Integer.toHexString(this.mWindowPtr) + "}";
    }
}
