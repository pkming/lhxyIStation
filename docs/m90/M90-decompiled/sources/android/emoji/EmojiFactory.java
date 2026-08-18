package android.emoji;

import android.graphics.Bitmap;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class EmojiFactory {
    private String mName;
    private int mNativeEmojiFactory;
    private int sCacheSize = 100;
    private Map<Integer, WeakReference<Bitmap>> mCache = new CustomLinkedHashMap();

    private native void nativeDestructor(int i);

    private native int nativeGetAndroidPuaFromVendorSpecificPua(int i, int i2);

    private native int nativeGetAndroidPuaFromVendorSpecificSjis(int i, char c);

    private native Bitmap nativeGetBitmapFromAndroidPua(int i, int i2);

    private native int nativeGetMaximumAndroidPua(int i);

    private native int nativeGetMaximumVendorSpecificPua(int i);

    private native int nativeGetMinimumAndroidPua(int i);

    private native int nativeGetMinimumVendorSpecificPua(int i);

    private native int nativeGetVendorSpecificPuaFromAndroidPua(int i, int i2);

    private native int nativeGetVendorSpecificSjisFromAndroidPua(int i, int i2);

    public static native EmojiFactory newAvailableInstance();

    public static native EmojiFactory newInstance(String str);

    private class CustomLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        public CustomLinkedHashMap() {
            super(16, 0.75f, true);
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
            return size() > EmojiFactory.this.sCacheSize;
        }
    }

    private EmojiFactory(int i, String str) {
        this.mNativeEmojiFactory = i;
        this.mName = str;
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeEmojiFactory);
        } finally {
            super.finalize();
        }
    }

    public String name() {
        return this.mName;
    }

    public synchronized Bitmap getBitmapFromAndroidPua(int i) {
        WeakReference<Bitmap> weakReference = this.mCache.get(Integer.valueOf(i));
        if (weakReference == null) {
            Bitmap bitmapNativeGetBitmapFromAndroidPua = nativeGetBitmapFromAndroidPua(this.mNativeEmojiFactory, i);
            if (bitmapNativeGetBitmapFromAndroidPua != null) {
                this.mCache.put(Integer.valueOf(i), new WeakReference<>(bitmapNativeGetBitmapFromAndroidPua));
            }
            return bitmapNativeGetBitmapFromAndroidPua;
        }
        Bitmap bitmap = weakReference.get();
        if (bitmap != null) {
            return bitmap;
        }
        Bitmap bitmapNativeGetBitmapFromAndroidPua2 = nativeGetBitmapFromAndroidPua(this.mNativeEmojiFactory, i);
        this.mCache.put(Integer.valueOf(i), new WeakReference<>(bitmapNativeGetBitmapFromAndroidPua2));
        return bitmapNativeGetBitmapFromAndroidPua2;
    }

    public synchronized Bitmap getBitmapFromVendorSpecificSjis(char c) {
        return getBitmapFromAndroidPua(getAndroidPuaFromVendorSpecificSjis(c));
    }

    public synchronized Bitmap getBitmapFromVendorSpecificPua(int i) {
        return getBitmapFromAndroidPua(getAndroidPuaFromVendorSpecificPua(i));
    }

    public int getAndroidPuaFromVendorSpecificSjis(char c) {
        return nativeGetAndroidPuaFromVendorSpecificSjis(this.mNativeEmojiFactory, c);
    }

    public int getVendorSpecificSjisFromAndroidPua(int i) {
        return nativeGetVendorSpecificSjisFromAndroidPua(this.mNativeEmojiFactory, i);
    }

    public int getAndroidPuaFromVendorSpecificPua(int i) {
        return nativeGetAndroidPuaFromVendorSpecificPua(this.mNativeEmojiFactory, i);
    }

    public String getAndroidPuaFromVendorSpecificPua(String str) {
        int androidPuaFromVendorSpecificPua;
        if (str == null) {
            return null;
        }
        int iNativeGetMinimumVendorSpecificPua = nativeGetMinimumVendorSpecificPua(this.mNativeEmojiFactory);
        int iNativeGetMaximumVendorSpecificPua = nativeGetMaximumVendorSpecificPua(this.mNativeEmojiFactory);
        int length = str.length();
        int[] iArr = new int[str.codePointCount(0, length)];
        int iOffsetByCodePoints = 0;
        int i = 0;
        while (iOffsetByCodePoints < length) {
            int iCodePointAt = str.codePointAt(iOffsetByCodePoints);
            if (iNativeGetMinimumVendorSpecificPua <= iCodePointAt && iCodePointAt <= iNativeGetMaximumVendorSpecificPua && (androidPuaFromVendorSpecificPua = getAndroidPuaFromVendorSpecificPua(iCodePointAt)) > 0) {
                iArr[i] = androidPuaFromVendorSpecificPua;
            } else {
                iArr[i] = iCodePointAt;
            }
            iOffsetByCodePoints = str.offsetByCodePoints(iOffsetByCodePoints, 1);
            i++;
        }
        return new String(iArr, 0, i);
    }

    public int getVendorSpecificPuaFromAndroidPua(int i) {
        return nativeGetVendorSpecificPuaFromAndroidPua(this.mNativeEmojiFactory, i);
    }

    public String getVendorSpecificPuaFromAndroidPua(String str) {
        int vendorSpecificPuaFromAndroidPua;
        if (str == null) {
            return null;
        }
        int iNativeGetMinimumAndroidPua = nativeGetMinimumAndroidPua(this.mNativeEmojiFactory);
        int iNativeGetMaximumAndroidPua = nativeGetMaximumAndroidPua(this.mNativeEmojiFactory);
        int length = str.length();
        int[] iArr = new int[str.codePointCount(0, length)];
        int iOffsetByCodePoints = 0;
        int i = 0;
        while (iOffsetByCodePoints < length) {
            int iCodePointAt = str.codePointAt(iOffsetByCodePoints);
            if (iNativeGetMinimumAndroidPua <= iCodePointAt && iCodePointAt <= iNativeGetMaximumAndroidPua && (vendorSpecificPuaFromAndroidPua = getVendorSpecificPuaFromAndroidPua(iCodePointAt)) > 0) {
                iArr[i] = vendorSpecificPuaFromAndroidPua;
            } else {
                iArr[i] = iCodePointAt;
            }
            iOffsetByCodePoints = str.offsetByCodePoints(iOffsetByCodePoints, 1);
            i++;
        }
        return new String(iArr, 0, i);
    }

    public int getMinimumAndroidPua() {
        return nativeGetMinimumAndroidPua(this.mNativeEmojiFactory);
    }

    public int getMaximumAndroidPua() {
        return nativeGetMaximumAndroidPua(this.mNativeEmojiFactory);
    }
}
