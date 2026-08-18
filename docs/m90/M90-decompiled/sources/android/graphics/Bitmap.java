package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class Bitmap implements Parcelable {
    public static final Parcelable.Creator<Bitmap> CREATOR = new Parcelable.Creator<Bitmap>() { // from class: android.graphics.Bitmap.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Bitmap createFromParcel(Parcel parcel) {
            Bitmap bitmapNativeCreateFromParcel = Bitmap.nativeCreateFromParcel(parcel);
            if (bitmapNativeCreateFromParcel != null) {
                return bitmapNativeCreateFromParcel;
            }
            throw new RuntimeException("Failed to unparcel Bitmap");
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Bitmap[] newArray(int i) {
            return new Bitmap[i];
        }
    };
    public static final int DENSITY_NONE = 0;
    private static final int WORKING_COMPRESS_STORAGE = 4096;
    private static volatile int sDefaultDensity = -1;
    private static volatile Matrix sScaleMatrix;
    public byte[] mBuffer;
    int mDensity;
    private final BitmapFinalizer mFinalizer;
    private int mHeight;
    private final boolean mIsMutable;
    private boolean mIsPremultiplied;
    private int[] mLayoutBounds;
    public final int mNativeBitmap;
    private byte[] mNinePatchChunk;
    private boolean mRecycled;
    private int mWidth;

    private static native boolean nativeCompress(int i, int i2, int i3, OutputStream outputStream, byte[] bArr);

    private static native int nativeConfig(int i);

    private static native Bitmap nativeCopy(int i, int i2, boolean z);

    private static native void nativeCopyPixelsFromBuffer(int i, Buffer buffer);

    private static native void nativeCopyPixelsToBuffer(int i, Buffer buffer);

    private static native Bitmap nativeCreate(int[] iArr, int i, int i2, int i3, int i4, int i5, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public static native Bitmap nativeCreateFromParcel(Parcel parcel);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeDestructor(int i);

    private static native void nativeErase(int i, int i2);

    private static native Bitmap nativeExtractAlpha(int i, int i2, int[] iArr);

    private static native int nativeGenerationId(int i);

    private static native int nativeGetPixel(int i, int i2, int i3, boolean z);

    private static native void nativeGetPixels(int i, int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7, boolean z);

    private static native boolean nativeHasAlpha(int i);

    private static native boolean nativeHasMipMap(int i);

    private static native void nativePrepareToDraw(int i);

    private static native void nativeReconfigure(int i, int i2, int i3, int i4, int i5);

    private static native boolean nativeRecycle(int i);

    private static native int nativeRowBytes(int i);

    private static native boolean nativeSameAs(int i, int i2);

    private static native void nativeSetHasAlpha(int i, boolean z);

    private static native void nativeSetHasMipMap(int i, boolean z);

    private static native void nativeSetPixel(int i, int i2, int i3, int i4, boolean z);

    private static native void nativeSetPixels(int i, int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7, boolean z);

    private static native boolean nativeWriteToParcel(int i, boolean z, int i2, Parcel parcel);

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static void setDefaultDensity(int i) {
        sDefaultDensity = i;
    }

    static int getDefaultDensity() {
        if (sDefaultDensity >= 0) {
            return sDefaultDensity;
        }
        sDefaultDensity = DisplayMetrics.DENSITY_DEVICE;
        return sDefaultDensity;
    }

    Bitmap(int i, byte[] bArr, int i2, int i3, int i4, boolean z, boolean z2, byte[] bArr2, int[] iArr) {
        this.mDensity = getDefaultDensity();
        if (i == 0) {
            throw new RuntimeException("internal error: native bitmap is 0");
        }
        this.mWidth = i2;
        this.mHeight = i3;
        this.mIsMutable = z;
        this.mIsPremultiplied = z2;
        this.mBuffer = bArr;
        this.mNativeBitmap = i;
        this.mFinalizer = new BitmapFinalizer(i);
        this.mNinePatchChunk = bArr2;
        this.mLayoutBounds = iArr;
        if (i4 >= 0) {
            this.mDensity = i4;
        }
    }

    void reinit(int i, int i2, boolean z) {
        this.mWidth = i;
        this.mHeight = i2;
        this.mIsPremultiplied = z;
    }

    public int getDensity() {
        return this.mDensity;
    }

    public void setDensity(int i) {
        this.mDensity = i;
    }

    public void reconfigure(int i, int i2, Config config) {
        checkRecycled("Can't call reconfigure() on a recycled bitmap");
        if (i <= 0 || i2 <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        if (!isMutable()) {
            throw new IllegalStateException("only mutable bitmaps may be reconfigured");
        }
        if (this.mBuffer == null) {
            throw new IllegalStateException("native-backed bitmaps may not be reconfigured");
        }
        nativeReconfigure(this.mNativeBitmap, i, i2, config.nativeInt, this.mBuffer.length);
        this.mWidth = i;
        this.mHeight = i2;
    }

    public void setWidth(int i) {
        reconfigure(i, getHeight(), getConfig());
    }

    public void setHeight(int i) {
        reconfigure(getWidth(), i, getConfig());
    }

    public void setConfig(Config config) {
        reconfigure(getWidth(), getHeight(), config);
    }

    public void setNinePatchChunk(byte[] bArr) {
        this.mNinePatchChunk = bArr;
    }

    public void setLayoutBounds(int[] iArr) {
        this.mLayoutBounds = iArr;
    }

    public void recycle() {
        if (this.mRecycled) {
            return;
        }
        if (nativeRecycle(this.mNativeBitmap)) {
            this.mBuffer = null;
            this.mNinePatchChunk = null;
        }
        this.mRecycled = true;
    }

    public final boolean isRecycled() {
        return this.mRecycled;
    }

    public int getGenerationId() {
        return nativeGenerationId(this.mNativeBitmap);
    }

    private void checkRecycled(String str) {
        if (this.mRecycled) {
            throw new IllegalStateException(str);
        }
    }

    private static void checkXYSign(int i, int i2) {
        if (i < 0) {
            throw new IllegalArgumentException("x must be >= 0");
        }
        if (i2 < 0) {
            throw new IllegalArgumentException("y must be >= 0");
        }
    }

    private static void checkWidthHeight(int i, int i2) {
        if (i <= 0) {
            throw new IllegalArgumentException("width must be > 0");
        }
        if (i2 <= 0) {
            throw new IllegalArgumentException("height must be > 0");
        }
    }

    public enum Config {
        ALPHA_8(2),
        RGB_565(4),
        ARGB_4444(5),
        ARGB_8888(6);

        final int nativeInt;
        private static Config[] sConfigs = {null, null, ALPHA_8, null, RGB_565, ARGB_4444, ARGB_8888};

        Config(int i) {
            this.nativeInt = i;
        }

        static Config nativeToConfig(int i) {
            return sConfigs[i];
        }
    }

    public void copyPixelsToBuffer(Buffer buffer) {
        char c;
        int iRemaining = buffer.remaining();
        if (buffer instanceof ByteBuffer) {
            c = 0;
        } else if (buffer instanceof ShortBuffer) {
            c = 1;
        } else {
            if (!(buffer instanceof IntBuffer)) {
                throw new RuntimeException("unsupported Buffer subclass");
            }
            c = 2;
        }
        long j = ((long) iRemaining) << c;
        long byteCount = getByteCount();
        if (j < byteCount) {
            throw new RuntimeException("Buffer not large enough for pixels");
        }
        nativeCopyPixelsToBuffer(this.mNativeBitmap, buffer);
        buffer.position((int) (((long) buffer.position()) + (byteCount >> c)));
    }

    public void copyPixelsFromBuffer(Buffer buffer) {
        char c;
        checkRecycled("copyPixelsFromBuffer called on recycled bitmap");
        int iRemaining = buffer.remaining();
        if (buffer instanceof ByteBuffer) {
            c = 0;
        } else if (buffer instanceof ShortBuffer) {
            c = 1;
        } else {
            if (!(buffer instanceof IntBuffer)) {
                throw new RuntimeException("unsupported Buffer subclass");
            }
            c = 2;
        }
        long j = ((long) iRemaining) << c;
        long byteCount = getByteCount();
        if (j < byteCount) {
            throw new RuntimeException("Buffer not large enough for pixels");
        }
        nativeCopyPixelsFromBuffer(this.mNativeBitmap, buffer);
        buffer.position((int) (((long) buffer.position()) + (byteCount >> c)));
    }

    public Bitmap copy(Config config, boolean z) {
        checkRecycled("Can't copy a recycled bitmap");
        Bitmap bitmapNativeCopy = nativeCopy(this.mNativeBitmap, config.nativeInt, z);
        if (bitmapNativeCopy != null) {
            bitmapNativeCopy.mIsPremultiplied = this.mIsPremultiplied;
            bitmapNativeCopy.mDensity = this.mDensity;
        }
        return bitmapNativeCopy;
    }

    public static Bitmap createScaledBitmap(Bitmap bitmap, int i, int i2, boolean z) {
        Matrix matrix;
        synchronized (Bitmap.class) {
            matrix = sScaleMatrix;
            sScaleMatrix = null;
        }
        if (matrix == null) {
            matrix = new Matrix();
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setScale(i / width, i2 / height);
        Bitmap bitmapCreateBitmap = createBitmap(bitmap, 0, 0, width, height, matrix, z);
        synchronized (Bitmap.class) {
            if (sScaleMatrix == null) {
                sScaleMatrix = matrix;
            }
        }
        return bitmapCreateBitmap;
    }

    public static Bitmap createBitmap(Bitmap bitmap) {
        return createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public static Bitmap createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4) {
        return createBitmap(bitmap, i, i2, i3, i4, (Matrix) null, false);
    }

    public static Bitmap createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4, Matrix matrix, boolean z) {
        Bitmap bitmapCreateBitmap;
        Paint paint;
        checkXYSign(i, i2);
        checkWidthHeight(i3, i4);
        int i5 = i + i3;
        if (i5 > bitmap.getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        }
        int i6 = i2 + i4;
        if (i6 > bitmap.getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        }
        if (!bitmap.isMutable() && i == 0 && i2 == 0 && i3 == bitmap.getWidth() && i4 == bitmap.getHeight() && (matrix == null || matrix.isIdentity())) {
            return bitmap;
        }
        Canvas canvas = new Canvas();
        Rect rect = new Rect(i, i2, i5, i6);
        RectF rectF = new RectF(0.0f, 0.0f, i3, i4);
        Config config = Config.ARGB_8888;
        Config config2 = bitmap.getConfig();
        if (config2 != null) {
            int i7 = AnonymousClass2.$SwitchMap$android$graphics$Bitmap$Config[config2.ordinal()];
            if (i7 == 1) {
                config = Config.RGB_565;
            } else if (i7 == 2) {
                config = Config.ALPHA_8;
            } else {
                config = Config.ARGB_8888;
            }
        }
        if (matrix == null || matrix.isIdentity()) {
            bitmapCreateBitmap = createBitmap(i3, i4, config, bitmap.hasAlpha());
            paint = null;
        } else {
            boolean z2 = !matrix.rectStaysRect();
            RectF rectF2 = new RectF();
            matrix.mapRect(rectF2, rectF);
            int iRound = Math.round(rectF2.width());
            int iRound2 = Math.round(rectF2.height());
            if (z2) {
                config = Config.ARGB_8888;
            }
            bitmapCreateBitmap = createBitmap(iRound, iRound2, config, z2 || bitmap.hasAlpha());
            canvas.translate(-rectF2.left, -rectF2.top);
            canvas.concat(matrix);
            paint = new Paint();
            paint.setFilterBitmap(z);
            if (z2) {
                paint.setAntiAlias(true);
            }
        }
        bitmapCreateBitmap.mDensity = bitmap.mDensity;
        bitmapCreateBitmap.mIsPremultiplied = bitmap.mIsPremultiplied;
        canvas.setBitmap(bitmapCreateBitmap);
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        canvas.setBitmap(null);
        return bitmapCreateBitmap;
    }

    /* JADX INFO: renamed from: android.graphics.Bitmap$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config;

        static {
            int[] iArr = new int[Config.values().length];
            $SwitchMap$android$graphics$Bitmap$Config = iArr;
            try {
                iArr[Config.RGB_565.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ALPHA_8.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_4444.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_8888.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public static Bitmap createBitmap(int i, int i2, Config config) {
        return createBitmap(i, i2, config, true);
    }

    public static Bitmap createBitmap(DisplayMetrics displayMetrics, int i, int i2, Config config) {
        return createBitmap(displayMetrics, i, i2, config, true);
    }

    private static Bitmap createBitmap(int i, int i2, Config config, boolean z) {
        return createBitmap((DisplayMetrics) null, i, i2, config, z);
    }

    private static Bitmap createBitmap(DisplayMetrics displayMetrics, int i, int i2, Config config, boolean z) {
        if (i <= 0 || i2 <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        Bitmap bitmapNativeCreate = nativeCreate(null, 0, i, i, i2, config.nativeInt, true);
        if (displayMetrics != null) {
            bitmapNativeCreate.mDensity = displayMetrics.densityDpi;
        }
        if (config == Config.ARGB_8888 && !z) {
            nativeErase(bitmapNativeCreate.mNativeBitmap, -16777216);
            nativeSetHasAlpha(bitmapNativeCreate.mNativeBitmap, z);
        }
        return bitmapNativeCreate;
    }

    public static Bitmap createBitmap(int[] iArr, int i, int i2, int i3, int i4, Config config) {
        return createBitmap((DisplayMetrics) null, iArr, i, i2, i3, i4, config);
    }

    public static Bitmap createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, int i3, int i4, Config config) {
        checkWidthHeight(i3, i4);
        if (Math.abs(i2) < i3) {
            throw new IllegalArgumentException("abs(stride) must be >= width");
        }
        int i5 = ((i4 - 1) * i2) + i;
        int length = iArr.length;
        if (i < 0 || i + i3 > length || i5 < 0 || i5 + i3 > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (i3 <= 0 || i4 <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        Bitmap bitmapNativeCreate = nativeCreate(iArr, i, i2, i3, i4, config.nativeInt, false);
        if (displayMetrics != null) {
            bitmapNativeCreate.mDensity = displayMetrics.densityDpi;
        }
        return bitmapNativeCreate;
    }

    public static Bitmap createBitmap(int[] iArr, int i, int i2, Config config) {
        return createBitmap((DisplayMetrics) null, iArr, 0, i, i, i2, config);
    }

    public static Bitmap createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, Config config) {
        return createBitmap(displayMetrics, iArr, 0, i, i, i2, config);
    }

    public byte[] getNinePatchChunk() {
        return this.mNinePatchChunk;
    }

    public int[] getLayoutBounds() {
        return this.mLayoutBounds;
    }

    public enum CompressFormat {
        JPEG(0),
        PNG(1),
        WEBP(2);

        final int nativeInt;

        CompressFormat(int i) {
            this.nativeInt = i;
        }
    }

    public boolean compress(CompressFormat compressFormat, int i, OutputStream outputStream) {
        checkRecycled("Can't compress a recycled bitmap");
        Objects.requireNonNull(outputStream);
        if (i < 0 || i > 100) {
            throw new IllegalArgumentException("quality must be 0..100");
        }
        return nativeCompress(this.mNativeBitmap, compressFormat.nativeInt, i, outputStream, new byte[4096]);
    }

    public final boolean isMutable() {
        return this.mIsMutable;
    }

    public final boolean isPremultiplied() {
        return this.mIsPremultiplied && getConfig() != Config.RGB_565 && hasAlpha();
    }

    public final void setPremultiplied(boolean z) {
        this.mIsPremultiplied = z;
    }

    public final int getWidth() {
        return this.mWidth;
    }

    public final int getHeight() {
        return this.mHeight;
    }

    public int getScaledWidth(Canvas canvas) {
        return scaleFromDensity(getWidth(), this.mDensity, canvas.mDensity);
    }

    public int getScaledHeight(Canvas canvas) {
        return scaleFromDensity(getHeight(), this.mDensity, canvas.mDensity);
    }

    public int getScaledWidth(DisplayMetrics displayMetrics) {
        return scaleFromDensity(getWidth(), this.mDensity, displayMetrics.densityDpi);
    }

    public int getScaledHeight(DisplayMetrics displayMetrics) {
        return scaleFromDensity(getHeight(), this.mDensity, displayMetrics.densityDpi);
    }

    public int getScaledWidth(int i) {
        return scaleFromDensity(getWidth(), this.mDensity, i);
    }

    public int getScaledHeight(int i) {
        return scaleFromDensity(getHeight(), this.mDensity, i);
    }

    public static int scaleFromDensity(int i, int i2, int i3) {
        return (i2 == 0 || i3 == 0 || i2 == i3) ? i : ((i * i3) + (i2 >> 1)) / i2;
    }

    public final int getRowBytes() {
        return nativeRowBytes(this.mNativeBitmap);
    }

    public final int getByteCount() {
        return getRowBytes() * getHeight();
    }

    public final int getAllocationByteCount() {
        byte[] bArr = this.mBuffer;
        if (bArr == null) {
            return getByteCount();
        }
        return bArr.length;
    }

    public final Config getConfig() {
        return Config.nativeToConfig(nativeConfig(this.mNativeBitmap));
    }

    public final boolean hasAlpha() {
        return nativeHasAlpha(this.mNativeBitmap);
    }

    public void setHasAlpha(boolean z) {
        nativeSetHasAlpha(this.mNativeBitmap, z);
    }

    public final boolean hasMipMap() {
        return nativeHasMipMap(this.mNativeBitmap);
    }

    public final void setHasMipMap(boolean z) {
        nativeSetHasMipMap(this.mNativeBitmap, z);
    }

    public void eraseColor(int i) {
        checkRecycled("Can't erase a recycled bitmap");
        if (!isMutable()) {
            throw new IllegalStateException("cannot erase immutable bitmaps");
        }
        nativeErase(this.mNativeBitmap, i);
    }

    public int getPixel(int i, int i2) {
        checkRecycled("Can't call getPixel() on a recycled bitmap");
        checkPixelAccess(i, i2);
        return nativeGetPixel(this.mNativeBitmap, i, i2, this.mIsPremultiplied);
    }

    public void getPixels(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6) {
        checkRecycled("Can't call getPixels() on a recycled bitmap");
        if (i5 == 0 || i6 == 0) {
            return;
        }
        checkPixelsAccess(i3, i4, i5, i6, i, i2, iArr);
        nativeGetPixels(this.mNativeBitmap, iArr, i, i2, i3, i4, i5, i6, this.mIsPremultiplied);
    }

    private void checkPixelAccess(int i, int i2) {
        checkXYSign(i, i2);
        if (i >= getWidth()) {
            throw new IllegalArgumentException("x must be < bitmap.width()");
        }
        if (i2 >= getHeight()) {
            throw new IllegalArgumentException("y must be < bitmap.height()");
        }
    }

    private void checkPixelsAccess(int i, int i2, int i3, int i4, int i5, int i6, int[] iArr) {
        checkXYSign(i, i2);
        if (i3 < 0) {
            throw new IllegalArgumentException("width must be >= 0");
        }
        if (i4 < 0) {
            throw new IllegalArgumentException("height must be >= 0");
        }
        if (i + i3 > getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        }
        if (i2 + i4 > getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        }
        if (Math.abs(i6) < i3) {
            throw new IllegalArgumentException("abs(stride) must be >= width");
        }
        int i7 = ((i4 - 1) * i6) + i5;
        int length = iArr.length;
        if (i5 < 0 || i5 + i3 > length || i7 < 0 || i7 + i3 > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void setPixel(int i, int i2, int i3) {
        checkRecycled("Can't call setPixel() on a recycled bitmap");
        if (!isMutable()) {
            throw new IllegalStateException();
        }
        checkPixelAccess(i, i2);
        nativeSetPixel(this.mNativeBitmap, i, i2, i3, this.mIsPremultiplied);
    }

    public void setPixels(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6) {
        checkRecycled("Can't call setPixels() on a recycled bitmap");
        if (!isMutable()) {
            throw new IllegalStateException();
        }
        if (i5 == 0 || i6 == 0) {
            return;
        }
        checkPixelsAccess(i3, i4, i5, i6, i, i2, iArr);
        nativeSetPixels(this.mNativeBitmap, iArr, i, i2, i3, i4, i5, i6, this.mIsPremultiplied);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        checkRecycled("Can't parcel a recycled bitmap");
        if (!nativeWriteToParcel(this.mNativeBitmap, this.mIsMutable, this.mDensity, parcel)) {
            throw new RuntimeException("native writeToParcel failed");
        }
    }

    public Bitmap extractAlpha() {
        return extractAlpha(null, null);
    }

    public Bitmap extractAlpha(Paint paint, int[] iArr) {
        checkRecycled("Can't extractAlpha on a recycled bitmap");
        Bitmap bitmapNativeExtractAlpha = nativeExtractAlpha(this.mNativeBitmap, paint != null ? paint.mNativePaint : 0, iArr);
        if (bitmapNativeExtractAlpha == null) {
            throw new RuntimeException("Failed to extractAlpha on Bitmap");
        }
        bitmapNativeExtractAlpha.mDensity = this.mDensity;
        return bitmapNativeExtractAlpha;
    }

    public boolean sameAs(Bitmap bitmap) {
        return this == bitmap || (bitmap != null && nativeSameAs(this.mNativeBitmap, bitmap.mNativeBitmap));
    }

    public void prepareToDraw() {
        nativePrepareToDraw(this.mNativeBitmap);
    }

    private static class BitmapFinalizer {
        private final int mNativeBitmap;

        BitmapFinalizer(int i) {
            this.mNativeBitmap = i;
        }

        public void finalize() {
            try {
                super.finalize();
            } catch (Throwable unused) {
            }
            Bitmap.nativeDestructor(this.mNativeBitmap);
        }
    }

    final int ni() {
        return this.mNativeBitmap;
    }
}
