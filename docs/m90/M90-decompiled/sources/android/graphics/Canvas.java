package android.graphics;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Region;
import android.text.GraphicsOperations;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import java.util.Objects;
import javax.microedition.khronos.opengles.GL;

/* JADX INFO: loaded from: classes.dex */
public class Canvas {
    public static final int ALL_SAVE_FLAG = 31;
    public static final int CLIP_SAVE_FLAG = 2;
    public static final int CLIP_TO_LAYER_SAVE_FLAG = 16;
    public static final int DIRECTION_LTR = 0;
    public static final int DIRECTION_RTL = 1;
    public static final int FULL_COLOR_LAYER_SAVE_FLAG = 8;
    public static final int HAS_ALPHA_LAYER_SAVE_FLAG = 4;
    public static final int MATRIX_SAVE_FLAG = 1;
    private static final int MAXMIMUM_BITMAP_SIZE = 32766;
    private Bitmap mBitmap;
    protected int mDensity;
    private DrawFilter mDrawFilter;
    private final CanvasFinalizer mFinalizer;
    public int mNativeCanvas;
    protected int mScreenDensity;
    private int mSurfaceFormat;

    private static native void copyNativeCanvasState(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void finalizer(int i);

    public static native void freeCaches();

    public static native void freeTextLayoutCaches();

    private static native int initRaster(int i);

    private static native void nativeDrawBitmapMatrix(int i, int i2, int i3, int i4);

    private static native void nativeDrawBitmapMesh(int i, int i2, int i3, int i4, float[] fArr, int i5, int[] iArr, int i6, int i7);

    private static native void nativeDrawVertices(int i, int i2, int i3, float[] fArr, int i4, float[] fArr2, int i5, int[] iArr, int i6, short[] sArr, int i7, int i8, int i9);

    private static native void nativeSetDrawFilter(int i, int i2);

    private static native boolean native_clipPath(int i, int i2, int i3);

    private static native boolean native_clipRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native boolean native_clipRegion(int i, int i2, int i3);

    private static native void native_concat(int i, int i2);

    private static native void native_drawARGB(int i, int i2, int i3, int i4, int i5);

    private static native void native_drawArc(int i, RectF rectF, float f, float f2, boolean z, int i2);

    private native void native_drawBitmap(int i, int i2, float f, float f2, int i3, int i4, int i5, int i6);

    private static native void native_drawBitmap(int i, int i2, Rect rect, Rect rect2, int i3, int i4, int i5);

    private native void native_drawBitmap(int i, int i2, Rect rect, RectF rectF, int i3, int i4, int i5);

    private static native void native_drawBitmap(int i, int[] iArr, int i2, int i3, float f, float f2, int i4, int i5, boolean z, int i6);

    private static native void native_drawCircle(int i, float f, float f2, float f3, int i2);

    private static native void native_drawColor(int i, int i2);

    private static native void native_drawColor(int i, int i2, int i3);

    private static native void native_drawLine(int i, float f, float f2, float f3, float f4, int i2);

    private static native void native_drawOval(int i, RectF rectF, int i2);

    private static native void native_drawPaint(int i, int i2);

    private static native void native_drawPath(int i, int i2, int i3);

    private static native void native_drawPosText(int i, String str, float[] fArr, int i2);

    private static native void native_drawPosText(int i, char[] cArr, int i2, int i3, float[] fArr, int i4);

    private static native void native_drawRGB(int i, int i2, int i3, int i4);

    private static native void native_drawRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native void native_drawRect(int i, RectF rectF, int i2);

    private static native void native_drawRoundRect(int i, RectF rectF, float f, float f2, int i2);

    private static native void native_drawText(int i, String str, int i2, int i3, float f, float f2, int i4, int i5);

    private static native void native_drawText(int i, char[] cArr, int i2, int i3, float f, float f2, int i4, int i5);

    private static native void native_drawTextOnPath(int i, String str, int i2, float f, float f2, int i3, int i4);

    private static native void native_drawTextOnPath(int i, char[] cArr, int i2, int i3, int i4, float f, float f2, int i5, int i6);

    private static native void native_drawTextRun(int i, String str, int i2, int i3, int i4, int i5, float f, float f2, int i6, int i7);

    private static native void native_drawTextRun(int i, char[] cArr, int i2, int i3, int i4, int i5, float f, float f2, int i6, int i7);

    private static native void native_getCTM(int i, int i2);

    private static native boolean native_getClipBounds(int i, Rect rect);

    private static native boolean native_quickReject(int i, float f, float f2, float f3, float f4);

    private static native boolean native_quickReject(int i, int i2);

    private static native boolean native_quickReject(int i, RectF rectF);

    private static native int native_saveLayer(int i, float f, float f2, float f3, float f4, int i2, int i3);

    private static native int native_saveLayer(int i, RectF rectF, int i2, int i3);

    private static native int native_saveLayerAlpha(int i, float f, float f2, float f3, float f4, int i2, int i3);

    private static native int native_saveLayerAlpha(int i, RectF rectF, int i2, int i3);

    private static native void native_setMatrix(int i, int i2);

    public native boolean clipRect(float f, float f2, float f3, float f4);

    public native boolean clipRect(int i, int i2, int i3, int i4);

    public native boolean clipRect(Rect rect);

    public native boolean clipRect(RectF rectF);

    public native void drawLines(float[] fArr, int i, int i2, Paint paint);

    public native void drawPoint(float f, float f2, Paint paint);

    public native void drawPoints(float[] fArr, int i, int i2, Paint paint);

    @Deprecated
    protected GL getGL() {
        return null;
    }

    public native int getHeight();

    public int getMaximumBitmapHeight() {
        return MAXMIMUM_BITMAP_SIZE;
    }

    public int getMaximumBitmapWidth() {
        return MAXMIMUM_BITMAP_SIZE;
    }

    public native int getSaveCount();

    public native int getWidth();

    public boolean isHardwareAccelerated() {
        return false;
    }

    public native boolean isOpaque();

    public native void restore();

    public native void restoreToCount(int i);

    public native void rotate(float f);

    public native int save();

    public native int save(int i);

    public native void scale(float f, float f2);

    public void setViewport(int i, int i2) {
    }

    public native void skew(float f, float f2);

    public native void translate(float f, float f2);

    private static final class CanvasFinalizer {
        private int mNativeCanvas;

        public CanvasFinalizer(int i) {
            this.mNativeCanvas = i;
        }

        protected void finalize() throws Throwable {
            try {
                dispose();
            } finally {
                super.finalize();
            }
        }

        public void dispose() {
            int i = this.mNativeCanvas;
            if (i != 0) {
                Canvas.finalizer(i);
                this.mNativeCanvas = 0;
            }
        }
    }

    public Canvas() {
        this.mDensity = 0;
        this.mScreenDensity = 0;
        if (!isHardwareAccelerated()) {
            this.mNativeCanvas = initRaster(0);
            this.mFinalizer = new CanvasFinalizer(this.mNativeCanvas);
        } else {
            this.mFinalizer = null;
        }
    }

    public Canvas(Bitmap bitmap) {
        this.mDensity = 0;
        this.mScreenDensity = 0;
        if (!bitmap.isMutable()) {
            throw new IllegalStateException("Immutable bitmap passed to Canvas constructor");
        }
        throwIfCannotDraw(bitmap);
        this.mNativeCanvas = initRaster(bitmap.ni());
        this.mFinalizer = new CanvasFinalizer(this.mNativeCanvas);
        this.mBitmap = bitmap;
        this.mDensity = bitmap.mDensity;
    }

    public Canvas(int i) {
        this.mDensity = 0;
        this.mScreenDensity = 0;
        if (i == 0) {
            throw new IllegalStateException();
        }
        this.mNativeCanvas = i;
        this.mFinalizer = new CanvasFinalizer(this.mNativeCanvas);
        this.mDensity = Bitmap.getDefaultDensity();
    }

    private void safeCanvasSwap(int i, boolean z) {
        int i2 = this.mNativeCanvas;
        this.mNativeCanvas = i;
        this.mFinalizer.mNativeCanvas = i;
        if (z) {
            copyNativeCanvasState(i2, this.mNativeCanvas);
        }
        finalizer(i2);
    }

    public int getNativeCanvas() {
        return this.mNativeCanvas;
    }

    public void setBitmap(Bitmap bitmap) {
        if (isHardwareAccelerated()) {
            throw new RuntimeException("Can't set a bitmap device on a GL canvas");
        }
        if (bitmap == null) {
            safeCanvasSwap(initRaster(0), false);
            this.mDensity = 0;
        } else {
            if (!bitmap.isMutable()) {
                throw new IllegalStateException();
            }
            throwIfCannotDraw(bitmap);
            safeCanvasSwap(initRaster(bitmap.ni()), true);
            this.mDensity = bitmap.mDensity;
        }
        this.mBitmap = bitmap;
    }

    public int getDensity() {
        return this.mDensity;
    }

    public void setDensity(int i) {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            bitmap.setDensity(i);
        }
        this.mDensity = i;
    }

    public void setScreenDensity(int i) {
        this.mScreenDensity = i;
    }

    public int saveLayer(RectF rectF, Paint paint, int i) {
        return native_saveLayer(this.mNativeCanvas, rectF, paint != null ? paint.mNativePaint : 0, i);
    }

    public int saveLayer(float f, float f2, float f3, float f4, Paint paint, int i) {
        return native_saveLayer(this.mNativeCanvas, f, f2, f3, f4, paint != null ? paint.mNativePaint : 0, i);
    }

    public int saveLayerAlpha(RectF rectF, int i, int i2) {
        return native_saveLayerAlpha(this.mNativeCanvas, rectF, Math.min(255, Math.max(0, i)), i2);
    }

    public int saveLayerAlpha(float f, float f2, float f3, float f4, int i, int i2) {
        return native_saveLayerAlpha(this.mNativeCanvas, f, f2, f3, f4, i, i2);
    }

    public final void scale(float f, float f2, float f3, float f4) {
        translate(f3, f4);
        scale(f, f2);
        translate(-f3, -f4);
    }

    public final void rotate(float f, float f2, float f3) {
        translate(f2, f3);
        rotate(f);
        translate(-f2, -f3);
    }

    public void concat(Matrix matrix) {
        if (matrix != null) {
            native_concat(this.mNativeCanvas, matrix.native_instance);
        }
    }

    public void setMatrix(Matrix matrix) {
        native_setMatrix(this.mNativeCanvas, matrix == null ? 0 : matrix.native_instance);
    }

    @Deprecated
    public void getMatrix(Matrix matrix) {
        native_getCTM(this.mNativeCanvas, matrix.native_instance);
    }

    @Deprecated
    public final Matrix getMatrix() {
        Matrix matrix = new Matrix();
        getMatrix(matrix);
        return matrix;
    }

    public boolean clipRect(RectF rectF, Region.Op op) {
        return native_clipRect(this.mNativeCanvas, rectF.left, rectF.top, rectF.right, rectF.bottom, op.nativeInt);
    }

    public boolean clipRect(Rect rect, Region.Op op) {
        return native_clipRect(this.mNativeCanvas, rect.left, rect.top, rect.right, rect.bottom, op.nativeInt);
    }

    public boolean clipRect(float f, float f2, float f3, float f4, Region.Op op) {
        return native_clipRect(this.mNativeCanvas, f, f2, f3, f4, op.nativeInt);
    }

    public boolean clipPath(Path path, Region.Op op) {
        return native_clipPath(this.mNativeCanvas, path.ni(), op.nativeInt);
    }

    public boolean clipPath(Path path) {
        return clipPath(path, Region.Op.INTERSECT);
    }

    public boolean clipRegion(Region region, Region.Op op) {
        return native_clipRegion(this.mNativeCanvas, region.ni(), op.nativeInt);
    }

    public boolean clipRegion(Region region) {
        return clipRegion(region, Region.Op.INTERSECT);
    }

    public DrawFilter getDrawFilter() {
        return this.mDrawFilter;
    }

    public void setDrawFilter(DrawFilter drawFilter) {
        int i = drawFilter != null ? drawFilter.mNativeInt : 0;
        this.mDrawFilter = drawFilter;
        nativeSetDrawFilter(this.mNativeCanvas, i);
    }

    public enum EdgeType {
        BW(0),
        AA(1);

        public final int nativeInt;

        EdgeType(int i) {
            this.nativeInt = i;
        }
    }

    public boolean quickReject(RectF rectF, EdgeType edgeType) {
        return native_quickReject(this.mNativeCanvas, rectF);
    }

    public boolean quickReject(Path path, EdgeType edgeType) {
        return native_quickReject(this.mNativeCanvas, path.ni());
    }

    public boolean quickReject(float f, float f2, float f3, float f4, EdgeType edgeType) {
        return native_quickReject(this.mNativeCanvas, f, f2, f3, f4);
    }

    public boolean getClipBounds(Rect rect) {
        return native_getClipBounds(this.mNativeCanvas, rect);
    }

    public final Rect getClipBounds() {
        Rect rect = new Rect();
        getClipBounds(rect);
        return rect;
    }

    public void drawRGB(int i, int i2, int i3) {
        native_drawRGB(this.mNativeCanvas, i, i2, i3);
    }

    public void drawARGB(int i, int i2, int i3, int i4) {
        native_drawARGB(this.mNativeCanvas, i, i2, i3, i4);
    }

    public void drawColor(int i) {
        native_drawColor(this.mNativeCanvas, i);
    }

    public void drawColor(int i, PorterDuff.Mode mode) {
        native_drawColor(this.mNativeCanvas, i, mode.nativeInt);
    }

    public void drawPaint(Paint paint) {
        native_drawPaint(this.mNativeCanvas, paint.mNativePaint);
    }

    public void drawPoints(float[] fArr, Paint paint) {
        drawPoints(fArr, 0, fArr.length, paint);
    }

    public void drawLine(float f, float f2, float f3, float f4, Paint paint) {
        native_drawLine(this.mNativeCanvas, f, f2, f3, f4, paint.mNativePaint);
    }

    public void drawLines(float[] fArr, Paint paint) {
        drawLines(fArr, 0, fArr.length, paint);
    }

    public void drawRect(RectF rectF, Paint paint) {
        native_drawRect(this.mNativeCanvas, rectF, paint.mNativePaint);
    }

    public void drawRect(Rect rect, Paint paint) {
        drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
    }

    public void drawRect(float f, float f2, float f3, float f4, Paint paint) {
        native_drawRect(this.mNativeCanvas, f, f2, f3, f4, paint.mNativePaint);
    }

    public void drawOval(RectF rectF, Paint paint) {
        Objects.requireNonNull(rectF);
        native_drawOval(this.mNativeCanvas, rectF, paint.mNativePaint);
    }

    public void drawCircle(float f, float f2, float f3, Paint paint) {
        native_drawCircle(this.mNativeCanvas, f, f2, f3, paint.mNativePaint);
    }

    public void drawArc(RectF rectF, float f, float f2, boolean z, Paint paint) {
        Objects.requireNonNull(rectF);
        native_drawArc(this.mNativeCanvas, rectF, f, f2, z, paint.mNativePaint);
    }

    public void drawRoundRect(RectF rectF, float f, float f2, Paint paint) {
        Objects.requireNonNull(rectF);
        native_drawRoundRect(this.mNativeCanvas, rectF, f, f2, paint.mNativePaint);
    }

    public void drawPath(Path path, Paint paint) {
        native_drawPath(this.mNativeCanvas, path.ni(), paint.mNativePaint);
    }

    protected static void throwIfCannotDraw(Bitmap bitmap) {
        if (bitmap.isRecycled()) {
            throw new RuntimeException("Canvas: trying to use a recycled bitmap " + bitmap);
        }
        if (!bitmap.isPremultiplied() && bitmap.getConfig() == Bitmap.Config.ARGB_8888 && bitmap.hasAlpha()) {
            throw new RuntimeException("Canvas: trying to use a non-premultiplied bitmap " + bitmap);
        }
    }

    public void drawPatch(NinePatch ninePatch, Rect rect, Paint paint) {
        ninePatch.drawSoftware(this, rect, paint);
    }

    public void drawPatch(NinePatch ninePatch, RectF rectF, Paint paint) {
        ninePatch.drawSoftware(this, rectF, paint);
    }

    public void drawBitmap(Bitmap bitmap, float f, float f2, Paint paint) {
        throwIfCannotDraw(bitmap);
        native_drawBitmap(this.mNativeCanvas, bitmap.ni(), f, f2, paint != null ? paint.mNativePaint : 0, this.mDensity, this.mScreenDensity, bitmap.mDensity);
    }

    public void drawBitmap(Bitmap bitmap, Rect rect, RectF rectF, Paint paint) {
        Objects.requireNonNull(rectF);
        throwIfCannotDraw(bitmap);
        native_drawBitmap(this.mNativeCanvas, bitmap.ni(), rect, rectF, paint != null ? paint.mNativePaint : 0, this.mScreenDensity, bitmap.mDensity);
    }

    public void drawBitmap(Bitmap bitmap, Rect rect, Rect rect2, Paint paint) {
        Objects.requireNonNull(rect2);
        throwIfCannotDraw(bitmap);
        native_drawBitmap(this.mNativeCanvas, bitmap.ni(), rect, rect2, paint != null ? paint.mNativePaint : 0, this.mScreenDensity, bitmap.mDensity);
    }

    public void drawBitmap(int[] iArr, int i, int i2, float f, float f2, int i3, int i4, boolean z, Paint paint) {
        if (i3 < 0) {
            throw new IllegalArgumentException("width must be >= 0");
        }
        if (i4 < 0) {
            throw new IllegalArgumentException("height must be >= 0");
        }
        if (Math.abs(i2) < i3) {
            throw new IllegalArgumentException("abs(stride) must be >= width");
        }
        int i5 = ((i4 - 1) * i2) + i;
        int length = iArr.length;
        if (i < 0 || i + i3 > length || i5 < 0 || i5 + i3 > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (i3 == 0 || i4 == 0) {
            return;
        }
        native_drawBitmap(this.mNativeCanvas, iArr, i, i2, f, f2, i3, i4, z, paint != null ? paint.mNativePaint : 0);
    }

    public void drawBitmap(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6, boolean z, Paint paint) {
        drawBitmap(iArr, i, i2, i3, i4, i5, i6, z, paint);
    }

    public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
        nativeDrawBitmapMatrix(this.mNativeCanvas, bitmap.ni(), matrix.ni(), paint != null ? paint.mNativePaint : 0);
    }

    protected static void checkRange(int i, int i2, int i3) {
        if ((i2 | i3) < 0 || i2 + i3 > i) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void drawBitmapMesh(Bitmap bitmap, int i, int i2, float[] fArr, int i3, int[] iArr, int i4, Paint paint) {
        if ((i | i2 | i3 | i4) < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (i == 0 || i2 == 0) {
            return;
        }
        int i5 = (i + 1) * (i2 + 1);
        checkRange(fArr.length, i3, i5 * 2);
        if (iArr != null) {
            checkRange(iArr.length, i4, i5);
        }
        nativeDrawBitmapMesh(this.mNativeCanvas, bitmap.ni(), i, i2, fArr, i3, iArr, i4, paint != null ? paint.mNativePaint : 0);
    }

    public enum VertexMode {
        TRIANGLES(0),
        TRIANGLE_STRIP(1),
        TRIANGLE_FAN(2);

        public final int nativeInt;

        VertexMode(int i) {
            this.nativeInt = i;
        }
    }

    public void drawVertices(VertexMode vertexMode, int i, float[] fArr, int i2, float[] fArr2, int i3, int[] iArr, int i4, short[] sArr, int i5, int i6, Paint paint) {
        checkRange(fArr.length, i2, i);
        if (fArr2 != null) {
            checkRange(fArr2.length, i3, i);
        }
        if (iArr != null) {
            checkRange(iArr.length, i4, i / 2);
        }
        if (sArr != null) {
            checkRange(sArr.length, i5, i6);
        }
        nativeDrawVertices(this.mNativeCanvas, vertexMode.nativeInt, i, fArr, i2, fArr2, i3, iArr, i4, sArr, i5, i6, paint.mNativePaint);
    }

    public void drawText(char[] cArr, int i, int i2, float f, float f2, Paint paint) {
        if ((i | i2 | (i + i2) | ((cArr.length - i) - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        native_drawText(this.mNativeCanvas, cArr, i, i2, f, f2, paint.mBidiFlags, paint.mNativePaint);
    }

    public void drawText(String str, float f, float f2, Paint paint) {
        native_drawText(this.mNativeCanvas, str, 0, str.length(), f, f2, paint.mBidiFlags, paint.mNativePaint);
    }

    public void drawText(String str, int i, int i2, float f, float f2, Paint paint) {
        if ((i | i2 | (i2 - i) | (str.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        native_drawText(this.mNativeCanvas, str, i, i2, f, f2, paint.mBidiFlags, paint.mNativePaint);
    }

    public void drawText(CharSequence charSequence, int i, int i2, float f, float f2, Paint paint) {
        if ((charSequence instanceof String) || (charSequence instanceof SpannedString) || (charSequence instanceof SpannableString)) {
            native_drawText(this.mNativeCanvas, charSequence.toString(), i, i2, f, f2, paint.mBidiFlags, paint.mNativePaint);
            return;
        }
        if (charSequence instanceof GraphicsOperations) {
            ((GraphicsOperations) charSequence).drawText(this, i, i2, f, f2, paint);
            return;
        }
        int i3 = i2 - i;
        char[] cArrObtain = TemporaryBuffer.obtain(i3);
        TextUtils.getChars(charSequence, i, i2, cArrObtain, 0);
        native_drawText(this.mNativeCanvas, cArrObtain, 0, i3, f, f2, paint.mBidiFlags, paint.mNativePaint);
        TemporaryBuffer.recycle(cArrObtain);
    }

    public void drawTextRun(char[] cArr, int i, int i2, int i3, int i4, float f, float f2, int i5, Paint paint) {
        Objects.requireNonNull(cArr, "text is null");
        Objects.requireNonNull(paint, "paint is null");
        if ((i | i2 | ((cArr.length - i) - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (i5 != 0 && i5 != 1) {
            throw new IllegalArgumentException("unknown dir: " + i5);
        }
        native_drawTextRun(this.mNativeCanvas, cArr, i, i2, i3, i4, f, f2, i5, paint.mNativePaint);
    }

    public void drawTextRun(CharSequence charSequence, int i, int i2, int i3, int i4, float f, float f2, int i5, Paint paint) {
        Objects.requireNonNull(charSequence, "text is null");
        Objects.requireNonNull(paint, "paint is null");
        int i6 = i2 - i;
        if ((i | i2 | i6 | (charSequence.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        int i7 = i5 == 0 ? 0 : 1;
        if ((charSequence instanceof String) || (charSequence instanceof SpannedString) || (charSequence instanceof SpannableString)) {
            native_drawTextRun(this.mNativeCanvas, charSequence.toString(), i, i2, i3, i4, f, f2, i7, paint.mNativePaint);
            return;
        }
        if (charSequence instanceof GraphicsOperations) {
            ((GraphicsOperations) charSequence).drawTextRun(this, i, i2, i3, i4, f, f2, i7, paint);
            return;
        }
        int i8 = i4 - i3;
        char[] cArrObtain = TemporaryBuffer.obtain(i8);
        TextUtils.getChars(charSequence, i3, i4, cArrObtain, 0);
        native_drawTextRun(this.mNativeCanvas, cArrObtain, i - i3, i6, 0, i8, f, f2, i7, paint.mNativePaint);
        TemporaryBuffer.recycle(cArrObtain);
    }

    @Deprecated
    public void drawPosText(char[] cArr, int i, int i2, float[] fArr, Paint paint) {
        if (i < 0 || i + i2 > cArr.length || i2 * 2 > fArr.length) {
            throw new IndexOutOfBoundsException();
        }
        native_drawPosText(this.mNativeCanvas, cArr, i, i2, fArr, paint.mNativePaint);
    }

    @Deprecated
    public void drawPosText(String str, float[] fArr, Paint paint) {
        if (str.length() * 2 > fArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        native_drawPosText(this.mNativeCanvas, str, fArr, paint.mNativePaint);
    }

    public void drawTextOnPath(char[] cArr, int i, int i2, Path path, float f, float f2, Paint paint) {
        if (i < 0 || i + i2 > cArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        native_drawTextOnPath(this.mNativeCanvas, cArr, i, i2, path.ni(), f, f2, paint.mBidiFlags, paint.mNativePaint);
    }

    public void drawTextOnPath(String str, Path path, float f, float f2, Paint paint) {
        if (str.length() > 0) {
            native_drawTextOnPath(this.mNativeCanvas, str, path.ni(), f, f2, paint.mBidiFlags, paint.mNativePaint);
        }
    }

    public void drawPicture(Picture picture) {
        picture.endRecording();
        int iSave = save();
        picture.draw(this);
        restoreToCount(iSave);
    }

    public void drawPicture(Picture picture, RectF rectF) {
        save();
        translate(rectF.left, rectF.top);
        if (picture.getWidth() > 0 && picture.getHeight() > 0) {
            scale(rectF.width() / picture.getWidth(), rectF.height() / picture.getHeight());
        }
        drawPicture(picture);
        restore();
    }

    public void drawPicture(Picture picture, Rect rect) {
        save();
        translate(rect.left, rect.top);
        if (picture.getWidth() > 0 && picture.getHeight() > 0) {
            scale(rect.width() / picture.getWidth(), rect.height() / picture.getHeight());
        }
        drawPicture(picture);
        restore();
    }

    public void release() {
        this.mFinalizer.dispose();
    }
}
