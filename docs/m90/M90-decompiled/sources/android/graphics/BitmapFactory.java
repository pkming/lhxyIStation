package android.graphics;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Trace;
import android.util.TypedValue;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class BitmapFactory {
    private static final int DECODE_BUFFER_SIZE = 16384;

    private static native Bitmap nativeDecodeAsset(int i, Rect rect, Options options);

    private static native Bitmap nativeDecodeByteArray(byte[] bArr, int i, int i2, Options options);

    private static native Bitmap nativeDecodeFileDescriptor(FileDescriptor fileDescriptor, Rect rect, Options options);

    private static native Bitmap nativeDecodeStream(InputStream inputStream, byte[] bArr, Rect rect, Options options);

    private static native boolean nativeIsSeekable(FileDescriptor fileDescriptor);

    public static class Options {
        public Bitmap inBitmap;
        public int inDensity;
        public boolean inInputShareable;
        public boolean inJustDecodeBounds;
        public boolean inMutable;
        public boolean inPreferQualityOverSpeed;
        public boolean inPurgeable;
        public int inSampleSize;
        public int inScreenDensity;
        public int inTargetDensity;
        public byte[] inTempStorage;
        public boolean mCancel;
        public int outHeight;
        public String outMimeType;
        public int outWidth;
        public Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;
        public boolean inDither = false;
        public boolean inScaled = true;
        public boolean inPremultiplied = true;

        private native void requestCancel();

        public void requestCancelDecode() {
            this.mCancel = true;
            requestCancel();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0034 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.graphics.Bitmap decodeFile(java.lang.String r4, android.graphics.BitmapFactory.Options r5) throws java.lang.Throwable {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L10 java.lang.Exception -> L12
            r1.<init>(r4)     // Catch: java.lang.Throwable -> L10 java.lang.Exception -> L12
            android.graphics.Bitmap r0 = decodeStream(r1, r0, r5)     // Catch: java.lang.Exception -> Le java.lang.Throwable -> L30
        La:
            r1.close()     // Catch: java.io.IOException -> L2f
            goto L2f
        Le:
            r4 = move-exception
            goto L14
        L10:
            r4 = move-exception
            goto L32
        L12:
            r4 = move-exception
            r1 = r0
        L14:
            java.lang.String r5 = "BitmapFactory"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L30
            r2.<init>()     // Catch: java.lang.Throwable -> L30
            java.lang.String r3 = "Unable to decode stream: "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L30
            java.lang.StringBuilder r4 = r2.append(r4)     // Catch: java.lang.Throwable -> L30
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L30
            android.util.Log.e(r5, r4)     // Catch: java.lang.Throwable -> L30
            if (r1 == 0) goto L2f
            goto La
        L2f:
            return r0
        L30:
            r4 = move-exception
            r0 = r1
        L32:
            if (r0 == 0) goto L37
            r0.close()     // Catch: java.io.IOException -> L37
        L37:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.BitmapFactory.decodeFile(java.lang.String, android.graphics.BitmapFactory$Options):android.graphics.Bitmap");
    }

    public static Bitmap decodeFile(String str) {
        return decodeFile(str, null);
    }

    public static Bitmap decodeResourceStream(Resources resources, TypedValue typedValue, InputStream inputStream, Rect rect, Options options) {
        if (options == null) {
            options = new Options();
        }
        if (options.inDensity == 0 && typedValue != null) {
            int i = typedValue.density;
            if (i == 0) {
                options.inDensity = 160;
            } else if (i != 65535) {
                options.inDensity = i;
            }
        }
        if (options.inTargetDensity == 0 && resources != null) {
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
        }
        return decodeStream(inputStream, rect, options);
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0010 A[EXC_TOP_SPLITTER, PHI: r0 r3
      0x0010: PHI (r0v3 android.graphics.Bitmap) = (r0v9 android.graphics.Bitmap), (r0v7 android.graphics.Bitmap) binds: [B:15:0x001f, B:5:0x000e] A[DONT_GENERATE, DONT_INLINE]
      0x0010: PHI (r3v3 java.io.InputStream) = (r3v2 java.io.InputStream), (r3v5 java.io.InputStream) binds: [B:15:0x001f, B:5:0x000e] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.graphics.Bitmap decodeResource(android.content.res.Resources r2, int r3, android.graphics.BitmapFactory.Options r4) throws java.lang.Throwable {
        /*
            r0 = 0
            android.util.TypedValue r1 = new android.util.TypedValue     // Catch: java.lang.Throwable -> L17 java.lang.Exception -> L1e
            r1.<init>()     // Catch: java.lang.Throwable -> L17 java.lang.Exception -> L1e
            java.io.InputStream r3 = r2.openRawResource(r3, r1)     // Catch: java.lang.Throwable -> L17 java.lang.Exception -> L1e
            android.graphics.Bitmap r0 = decodeResourceStream(r2, r1, r3, r0, r4)     // Catch: java.lang.Throwable -> L14 java.lang.Exception -> L1f
            if (r3 == 0) goto L22
        L10:
            r3.close()     // Catch: java.io.IOException -> L22
            goto L22
        L14:
            r2 = move-exception
            r0 = r3
            goto L18
        L17:
            r2 = move-exception
        L18:
            if (r0 == 0) goto L1d
            r0.close()     // Catch: java.io.IOException -> L1d
        L1d:
            throw r2
        L1e:
            r3 = r0
        L1f:
            if (r3 == 0) goto L22
            goto L10
        L22:
            if (r0 != 0) goto L33
            if (r4 == 0) goto L33
            android.graphics.Bitmap r2 = r4.inBitmap
            if (r2 != 0) goto L2b
            goto L33
        L2b:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "Problem decoding into existing bitmap"
            r2.<init>(r3)
            throw r2
        L33:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.BitmapFactory.decodeResource(android.content.res.Resources, int, android.graphics.BitmapFactory$Options):android.graphics.Bitmap");
    }

    public static Bitmap decodeResource(Resources resources, int i) {
        return decodeResource(resources, i, null);
    }

    public static Bitmap decodeByteArray(byte[] bArr, int i, int i2, Options options) {
        if ((i | i2) < 0 || bArr.length < i + i2) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Trace.traceBegin(2L, "decodeBitmap");
        try {
            Bitmap bitmapNativeDecodeByteArray = nativeDecodeByteArray(bArr, i, i2, options);
            if (bitmapNativeDecodeByteArray == null && options != null && options.inBitmap != null) {
                throw new IllegalArgumentException("Problem decoding into existing bitmap");
            }
            setDensityFromOptions(bitmapNativeDecodeByteArray, options);
            return bitmapNativeDecodeByteArray;
        } finally {
            Trace.traceEnd(2L);
        }
    }

    public static Bitmap decodeByteArray(byte[] bArr, int i, int i2) {
        return decodeByteArray(bArr, i, i2, null);
    }

    private static void setDensityFromOptions(Bitmap bitmap, Options options) {
        if (bitmap == null || options == null) {
            return;
        }
        int i = options.inDensity;
        if (i != 0) {
            bitmap.setDensity(i);
            int i2 = options.inTargetDensity;
            if (i2 == 0 || i == i2 || i == options.inScreenDensity) {
                return;
            }
            byte[] ninePatchChunk = bitmap.getNinePatchChunk();
            boolean z = ninePatchChunk != null && NinePatch.isNinePatchChunk(ninePatchChunk);
            if (options.inScaled || z) {
                bitmap.setDensity(i2);
                return;
            }
            return;
        }
        if (options.inBitmap != null) {
            bitmap.setDensity(Bitmap.getDefaultDensity());
        }
    }

    public static Bitmap decodeStream(InputStream inputStream, Rect rect, Options options) {
        Bitmap bitmapDecodeStreamInternal;
        if (inputStream == null) {
            return null;
        }
        Trace.traceBegin(2L, "decodeBitmap");
        try {
            if (inputStream instanceof AssetManager.AssetInputStream) {
                bitmapDecodeStreamInternal = nativeDecodeAsset(((AssetManager.AssetInputStream) inputStream).getAssetInt(), rect, options);
            } else {
                bitmapDecodeStreamInternal = decodeStreamInternal(inputStream, rect, options);
            }
            if (bitmapDecodeStreamInternal == null && options != null && options.inBitmap != null) {
                throw new IllegalArgumentException("Problem decoding into existing bitmap");
            }
            setDensityFromOptions(bitmapDecodeStreamInternal, options);
            return bitmapDecodeStreamInternal;
        } finally {
            Trace.traceEnd(2L);
        }
    }

    private static Bitmap decodeStreamInternal(InputStream inputStream, Rect rect, Options options) {
        byte[] bArr = options != null ? options.inTempStorage : null;
        if (bArr == null) {
            bArr = new byte[16384];
        }
        return nativeDecodeStream(inputStream, bArr, rect, options);
    }

    public static Bitmap decodeStream(InputStream inputStream) {
        return decodeStream(inputStream, null, null);
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fileDescriptor, Rect rect, Options options) {
        Bitmap bitmapDecodeStreamInternal;
        Trace.traceBegin(2L, "decodeFileDescriptor");
        try {
            if (nativeIsSeekable(fileDescriptor)) {
                bitmapDecodeStreamInternal = nativeDecodeFileDescriptor(fileDescriptor, rect, options);
            } else {
                FileInputStream fileInputStream = new FileInputStream(fileDescriptor);
                try {
                    bitmapDecodeStreamInternal = decodeStreamInternal(fileInputStream, rect, options);
                } finally {
                    try {
                        fileInputStream.close();
                    } catch (Throwable unused) {
                    }
                }
            }
            if (bitmapDecodeStreamInternal == null && options != null && options.inBitmap != null) {
                throw new IllegalArgumentException("Problem decoding into existing bitmap");
            }
            setDensityFromOptions(bitmapDecodeStreamInternal, options);
            return bitmapDecodeStreamInternal;
        } finally {
            Trace.traceEnd(2L);
        }
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fileDescriptor) {
        return decodeFileDescriptor(fileDescriptor, null, null);
    }
}
