package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class NinePatch {
    private final Bitmap mBitmap;
    public final int mNativeChunk;
    private Paint mPaint;
    private String mSrcName;

    public static native boolean isNinePatchChunk(byte[] bArr);

    private static native void nativeDraw(int i, Rect rect, int i2, int i3, int i4, int i5, int i6);

    private static native void nativeDraw(int i, RectF rectF, int i2, int i3, int i4, int i5, int i6);

    private static native void nativeFinalize(int i);

    private static native int nativeGetTransparentRegion(int i, int i2, Rect rect);

    private static native int validateNinePatchChunk(int i, byte[] bArr);

    public NinePatch(Bitmap bitmap, byte[] bArr) {
        this(bitmap, bArr, null);
    }

    public NinePatch(Bitmap bitmap, byte[] bArr, String str) {
        this.mBitmap = bitmap;
        this.mSrcName = str;
        this.mNativeChunk = validateNinePatchChunk(bitmap.ni(), bArr);
    }

    public NinePatch(NinePatch ninePatch) {
        this.mBitmap = ninePatch.mBitmap;
        this.mSrcName = ninePatch.mSrcName;
        if (ninePatch.mPaint != null) {
            this.mPaint = new Paint(ninePatch.mPaint);
        }
        this.mNativeChunk = ninePatch.mNativeChunk;
    }

    protected void finalize() throws Throwable {
        try {
            nativeFinalize(this.mNativeChunk);
        } finally {
            super.finalize();
        }
    }

    public String getName() {
        return this.mSrcName;
    }

    public Paint getPaint() {
        return this.mPaint;
    }

    public void setPaint(Paint paint) {
        this.mPaint = paint;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public void draw(Canvas canvas, RectF rectF) {
        canvas.drawPatch(this, rectF, this.mPaint);
    }

    public void draw(Canvas canvas, Rect rect) {
        canvas.drawPatch(this, rect, this.mPaint);
    }

    public void draw(Canvas canvas, Rect rect, Paint paint) {
        canvas.drawPatch(this, rect, paint);
    }

    void drawSoftware(Canvas canvas, RectF rectF, Paint paint) {
        nativeDraw(canvas.mNativeCanvas, rectF, this.mBitmap.ni(), this.mNativeChunk, paint != null ? paint.mNativePaint : 0, canvas.mDensity, this.mBitmap.mDensity);
    }

    void drawSoftware(Canvas canvas, Rect rect, Paint paint) {
        nativeDraw(canvas.mNativeCanvas, rect, this.mBitmap.ni(), this.mNativeChunk, paint != null ? paint.mNativePaint : 0, canvas.mDensity, this.mBitmap.mDensity);
    }

    public int getDensity() {
        return this.mBitmap.mDensity;
    }

    public int getWidth() {
        return this.mBitmap.getWidth();
    }

    public int getHeight() {
        return this.mBitmap.getHeight();
    }

    public final boolean hasAlpha() {
        return this.mBitmap.hasAlpha();
    }

    public final Region getTransparentRegion(Rect rect) {
        int iNativeGetTransparentRegion = nativeGetTransparentRegion(this.mBitmap.ni(), this.mNativeChunk, rect);
        if (iNativeGetTransparentRegion != 0) {
            return new Region(iNativeGetTransparentRegion);
        }
        return null;
    }
}
