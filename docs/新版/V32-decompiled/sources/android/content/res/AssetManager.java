package android.content.res;

import android.os.ParcelFileDescriptor;
import android.util.TypedValue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public final class AssetManager {
    public static final int ACCESS_BUFFER = 3;
    public static final int ACCESS_RANDOM = 1;
    public static final int ACCESS_STREAMING = 2;
    public static final int ACCESS_UNKNOWN = 0;
    private static final boolean DEBUG_REFS = false;
    static final int STYLE_ASSET_COOKIE = 2;
    static final int STYLE_CHANGING_CONFIGURATIONS = 4;
    static final int STYLE_DATA = 1;
    static final int STYLE_DENSITY = 5;
    static final int STYLE_NUM_ENTRIES = 6;
    static final int STYLE_RESOURCE_ID = 3;
    static final int STYLE_TYPE = 0;
    private static final String TAG = "AssetManager";
    private static final boolean localLOGV = false;
    private static final Object sSync = new Object();
    static AssetManager sSystem;
    private int mNObject;
    private int mNumRefs;
    private int mObject;
    private final long[] mOffsets;
    private boolean mOpen;
    private HashMap<Integer, RuntimeException> mRefStacks;
    private StringBlock[] mStringBlocks;
    private final TypedValue mValue;

    private final native int addAssetPathNative(String str);

    static final native boolean applyStyle(int i, int i2, int i3, int i4, int[] iArr, int[] iArr2, int[] iArr3);

    static final native void applyThemeStyle(int i, int i2, boolean z);

    static final native void copyTheme(int i, int i2);

    private final native void deleteTheme(int i);

    private final native void destroy();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void destroyAsset(int i);

    static final native void dumpTheme(int i, int i2, String str, String str2);

    private final native int[] getArrayStringInfo(int i);

    private final native String[] getArrayStringResource(int i);

    public static final native String getAssetAllocations();

    /* JADX INFO: Access modifiers changed from: private */
    public final native long getAssetLength(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public final native long getAssetRemainingLength(int i);

    public static final native int getGlobalAssetCount();

    public static final native int getGlobalAssetManagerCount();

    private final native int getNativeStringBlock(int i);

    private final native int getStringBlockCount();

    private final native void init();

    private final native int loadResourceBagValue(int i, int i2, TypedValue typedValue, boolean z);

    private final native int loadResourceValue(int i, short s, TypedValue typedValue, boolean z);

    static final native int loadThemeAttributeValue(int i, int i2, TypedValue typedValue, boolean z);

    private final native int newTheme();

    private final native int openAsset(String str, int i);

    private final native ParcelFileDescriptor openAssetFd(String str, long[] jArr) throws IOException;

    private native ParcelFileDescriptor openNonAssetFdNative(int i, String str, long[] jArr) throws IOException;

    private final native int openNonAssetNative(int i, String str, int i2);

    private final native int openXmlAssetNative(int i, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public final native int readAsset(int i, byte[] bArr, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: private */
    public final native int readAssetChar(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public final native long seekAsset(int i, long j, int i2);

    final native int[] getArrayIntResource(int i);

    final native int getArraySize(int i);

    public final native String getCookieName(int i);

    public final native String[] getLocales();

    final native String getResourceEntryName(int i);

    final native int getResourceIdentifier(String str, String str2, String str3);

    final native String getResourceName(int i);

    final native String getResourcePackageName(int i);

    final native String getResourceTypeName(int i);

    public final native boolean isUpToDate();

    public final native String[] list(String str) throws IOException;

    final native int retrieveArray(int i, int[] iArr);

    final native boolean retrieveAttributes(int i, int[] iArr, int[] iArr2, int[] iArr3);

    public final native void setConfiguration(int i, int i2, String str, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16);

    public final native void setLocale(String str);

    public AssetManager() {
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mStringBlocks = null;
        this.mNumRefs = 1;
        this.mOpen = true;
        synchronized (this) {
            init();
            ensureSystemAssets();
        }
    }

    private static void ensureSystemAssets() {
        synchronized (sSync) {
            if (sSystem == null) {
                AssetManager assetManager = new AssetManager(true);
                assetManager.makeStringBlocks(false);
                sSystem = assetManager;
            }
        }
    }

    private AssetManager(boolean z) {
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mStringBlocks = null;
        this.mNumRefs = 1;
        this.mOpen = true;
        init();
    }

    public static AssetManager getSystem() {
        ensureSystemAssets();
        return sSystem;
    }

    public void close() {
        synchronized (this) {
            if (this.mOpen) {
                this.mOpen = false;
                decRefsLocked(hashCode());
            }
        }
    }

    final CharSequence getResourceText(int i) {
        synchronized (this) {
            TypedValue typedValue = this.mValue;
            int iLoadResourceValue = loadResourceValue(i, (short) 0, typedValue, true);
            if (iLoadResourceValue < 0) {
                return null;
            }
            if (typedValue.type == 3) {
                return this.mStringBlocks[iLoadResourceValue].get(typedValue.data);
            }
            return typedValue.coerceToString();
        }
    }

    final CharSequence getResourceBagText(int i, int i2) {
        synchronized (this) {
            TypedValue typedValue = this.mValue;
            int iLoadResourceBagValue = loadResourceBagValue(i, i2, typedValue, true);
            if (iLoadResourceBagValue < 0) {
                return null;
            }
            if (typedValue.type == 3) {
                return this.mStringBlocks[iLoadResourceBagValue].get(typedValue.data);
            }
            return typedValue.coerceToString();
        }
    }

    final String[] getResourceStringArray(int i) {
        return getArrayStringResource(i);
    }

    final boolean getResourceValue(int i, int i2, TypedValue typedValue, boolean z) {
        int iLoadResourceValue = loadResourceValue(i, (short) i2, typedValue, z);
        if (iLoadResourceValue < 0) {
            return false;
        }
        if (typedValue.type != 3) {
            return true;
        }
        typedValue.string = this.mStringBlocks[iLoadResourceValue].get(typedValue.data);
        return true;
    }

    final CharSequence[] getResourceTextArray(int i) {
        int[] arrayStringInfo = getArrayStringInfo(i);
        int length = arrayStringInfo.length;
        CharSequence[] charSequenceArr = new CharSequence[length / 2];
        int i2 = 0;
        int i3 = 0;
        while (i2 < length) {
            int i4 = arrayStringInfo[i2];
            int i5 = arrayStringInfo[i2 + 1];
            charSequenceArr[i3] = i5 >= 0 ? this.mStringBlocks[i4].get(i5) : null;
            i2 += 2;
            i3++;
        }
        return charSequenceArr;
    }

    final boolean getThemeValue(int i, int i2, TypedValue typedValue, boolean z) {
        int iLoadThemeAttributeValue = loadThemeAttributeValue(i, i2, typedValue, z);
        if (iLoadThemeAttributeValue < 0) {
            return false;
        }
        if (typedValue.type != 3) {
            return true;
        }
        StringBlock[] stringBlockArr = this.mStringBlocks;
        if (stringBlockArr == null) {
            ensureStringBlocks();
            stringBlockArr = this.mStringBlocks;
        }
        typedValue.string = stringBlockArr[iLoadThemeAttributeValue].get(typedValue.data);
        return true;
    }

    final void ensureStringBlocks() {
        if (this.mStringBlocks == null) {
            synchronized (this) {
                if (this.mStringBlocks == null) {
                    makeStringBlocks(true);
                }
            }
        }
    }

    final void makeStringBlocks(boolean z) {
        int length = z ? sSystem.mStringBlocks.length : 0;
        int stringBlockCount = getStringBlockCount();
        this.mStringBlocks = new StringBlock[stringBlockCount];
        for (int i = 0; i < stringBlockCount; i++) {
            if (i < length) {
                this.mStringBlocks[i] = sSystem.mStringBlocks[i];
            } else {
                this.mStringBlocks[i] = new StringBlock(getNativeStringBlock(i), true);
            }
        }
    }

    final CharSequence getPooledString(int i, int i2) {
        return this.mStringBlocks[i - 1].get(i2);
    }

    public final InputStream open(String str) throws IOException {
        return open(str, 2);
    }

    public final InputStream open(String str, int i) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            int iOpenAsset = openAsset(str, i);
            if (iOpenAsset != 0) {
                AssetInputStream assetInputStream = new AssetInputStream(iOpenAsset);
                incRefsLocked(assetInputStream.hashCode());
                return assetInputStream;
            }
            throw new FileNotFoundException("Asset file: " + str);
        }
    }

    public final AssetFileDescriptor openFd(String str) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            ParcelFileDescriptor parcelFileDescriptorOpenAssetFd = openAssetFd(str, this.mOffsets);
            if (parcelFileDescriptorOpenAssetFd != null) {
                long[] jArr = this.mOffsets;
                return new AssetFileDescriptor(parcelFileDescriptorOpenAssetFd, jArr[0], jArr[1]);
            }
            throw new FileNotFoundException("Asset file: " + str);
        }
    }

    public final InputStream openNonAsset(String str) throws IOException {
        return openNonAsset(0, str, 2);
    }

    public final InputStream openNonAsset(String str, int i) throws IOException {
        return openNonAsset(0, str, i);
    }

    public final InputStream openNonAsset(int i, String str) throws IOException {
        return openNonAsset(i, str, 2);
    }

    public final InputStream openNonAsset(int i, String str, int i2) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            int iOpenNonAssetNative = openNonAssetNative(i, str, i2);
            if (iOpenNonAssetNative != 0) {
                AssetInputStream assetInputStream = new AssetInputStream(iOpenNonAssetNative);
                incRefsLocked(assetInputStream.hashCode());
                return assetInputStream;
            }
            throw new FileNotFoundException("Asset absolute file: " + str);
        }
    }

    public final AssetFileDescriptor openNonAssetFd(String str) throws IOException {
        return openNonAssetFd(0, str);
    }

    public final AssetFileDescriptor openNonAssetFd(int i, String str) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            ParcelFileDescriptor parcelFileDescriptorOpenNonAssetFdNative = openNonAssetFdNative(i, str, this.mOffsets);
            if (parcelFileDescriptorOpenNonAssetFdNative != null) {
                long[] jArr = this.mOffsets;
                return new AssetFileDescriptor(parcelFileDescriptorOpenNonAssetFdNative, jArr[0], jArr[1]);
            }
            throw new FileNotFoundException("Asset absolute file: " + str);
        }
    }

    public final XmlResourceParser openXmlResourceParser(String str) throws IOException {
        return openXmlResourceParser(0, str);
    }

    public final XmlResourceParser openXmlResourceParser(int i, String str) throws IOException {
        XmlBlock xmlBlockOpenXmlBlockAsset = openXmlBlockAsset(i, str);
        XmlResourceParser xmlResourceParserNewParser = xmlBlockOpenXmlBlockAsset.newParser();
        xmlBlockOpenXmlBlockAsset.close();
        return xmlResourceParserNewParser;
    }

    final XmlBlock openXmlBlockAsset(String str) throws IOException {
        return openXmlBlockAsset(0, str);
    }

    final XmlBlock openXmlBlockAsset(int i, String str) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            int iOpenXmlAssetNative = openXmlAssetNative(i, str);
            if (iOpenXmlAssetNative != 0) {
                XmlBlock xmlBlock = new XmlBlock(this, iOpenXmlAssetNative);
                incRefsLocked(xmlBlock.hashCode());
                return xmlBlock;
            }
            throw new FileNotFoundException("Asset XML file: " + str);
        }
    }

    void xmlBlockGone(int i) {
        synchronized (this) {
            decRefsLocked(i);
        }
    }

    final int createTheme() {
        int iNewTheme;
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            iNewTheme = newTheme();
            incRefsLocked(iNewTheme);
        }
        return iNewTheme;
    }

    final void releaseTheme(int i) {
        synchronized (this) {
            deleteTheme(i);
            decRefsLocked(i);
        }
    }

    protected void finalize() throws Throwable {
        try {
            destroy();
        } finally {
            super.finalize();
        }
    }

    public final class AssetInputStream extends InputStream {
        private int mAsset;
        private long mLength;
        private long mMarkPos;

        @Override // java.io.InputStream
        public final boolean markSupported() {
            return true;
        }

        public final int getAssetInt() {
            return this.mAsset;
        }

        private AssetInputStream(int i) {
            this.mAsset = i;
            this.mLength = AssetManager.this.getAssetLength(i);
        }

        @Override // java.io.InputStream
        public final int read() throws IOException {
            return AssetManager.this.readAssetChar(this.mAsset);
        }

        @Override // java.io.InputStream
        public final int available() throws IOException {
            long assetRemainingLength = AssetManager.this.getAssetRemainingLength(this.mAsset);
            if (assetRemainingLength > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) assetRemainingLength;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public final void close() throws IOException {
            synchronized (AssetManager.this) {
                int i = this.mAsset;
                if (i != 0) {
                    AssetManager.this.destroyAsset(i);
                    this.mAsset = 0;
                    AssetManager.this.decRefsLocked(hashCode());
                }
            }
        }

        @Override // java.io.InputStream
        public final void mark(int i) {
            this.mMarkPos = AssetManager.this.seekAsset(this.mAsset, 0L, 0);
        }

        @Override // java.io.InputStream
        public final void reset() throws IOException {
            AssetManager.this.seekAsset(this.mAsset, this.mMarkPos, -1);
        }

        @Override // java.io.InputStream
        public final int read(byte[] bArr) throws IOException {
            return AssetManager.this.readAsset(this.mAsset, bArr, 0, bArr.length);
        }

        @Override // java.io.InputStream
        public final int read(byte[] bArr, int i, int i2) throws IOException {
            return AssetManager.this.readAsset(this.mAsset, bArr, i, i2);
        }

        @Override // java.io.InputStream
        public final long skip(long j) throws IOException {
            long jSeekAsset = AssetManager.this.seekAsset(this.mAsset, 0L, 0);
            long j2 = jSeekAsset + j;
            long j3 = this.mLength;
            if (j2 > j3) {
                j = j3 - jSeekAsset;
            }
            if (j > 0) {
                AssetManager.this.seekAsset(this.mAsset, j, 0);
            }
            return j;
        }

        protected void finalize() throws Throwable {
            close();
        }
    }

    public final int addAssetPath(String str) {
        return addAssetPathNative(str);
    }

    public final int[] addAssetPaths(String[] strArr) {
        if (strArr == null) {
            return null;
        }
        int[] iArr = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            iArr[i] = addAssetPath(strArr[i]);
        }
        return iArr;
    }

    private final void incRefsLocked(int i) {
        this.mNumRefs++;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void decRefsLocked(int i) {
        int i2 = this.mNumRefs - 1;
        this.mNumRefs = i2;
        if (i2 == 0) {
            destroy();
        }
    }
}
