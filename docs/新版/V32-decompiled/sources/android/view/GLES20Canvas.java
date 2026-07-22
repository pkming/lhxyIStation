package android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.TemporaryBuffer;
import android.text.GraphicsOperations;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
class GLES20Canvas extends HardwareCanvas {
    static final int FLUSH_CACHES_FULL = 2;
    static final int FLUSH_CACHES_LAYERS = 0;
    static final int FLUSH_CACHES_MODERATE = 1;
    private static final int MODIFIER_COLOR_FILTER = 4;
    private static final int MODIFIER_NONE = 0;
    private static final int MODIFIER_SHADER = 2;
    private static final int MODIFIER_SHADOW = 1;
    private static boolean sIsAvailable = nIsAvailable();
    private Rect mClipBounds;
    private DrawFilter mFilter;
    private CanvasFinalizer mFinalizer;
    private int mHeight;
    private float[] mLine;
    private final boolean mOpaque;
    private RectF mPathBounds;
    private float[] mPoint;
    private int mRenderer;
    private int mWidth;

    private static native void nAttachFunctor(int i, int i2);

    private static native int nCallDrawGLFunction(int i, int i2);

    private static native void nCancelLayerUpdate(int i, int i2);

    static native void nClearLayerTexture(int i);

    private static native void nClearLayerUpdates(int i);

    private static native boolean nClipPath(int i, int i2, int i3);

    private static native boolean nClipRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native boolean nClipRect(int i, int i2, int i3, int i4, int i5, int i6);

    private static native boolean nClipRegion(int i, int i2, int i3);

    private static native void nConcatMatrix(int i, int i2);

    static native boolean nCopyLayer(int i, int i2);

    private static native int nCreateDisplayListRenderer();

    static native int nCreateLayer(int i, int i2, boolean z, int[] iArr);

    private static native int nCreateLayerRenderer(int i);

    private static native int nCreateRenderer();

    static native int nCreateTextureLayer(boolean z, int[] iArr);

    static native void nDestroyLayer(int i);

    static native void nDestroyLayerDeferred(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nDestroyRenderer(int i);

    private static native void nDetachFunctor(int i, int i2);

    private static native void nDrawArc(int i, float f, float f2, float f3, float f4, float f5, float f6, boolean z, int i2);

    private static native void nDrawBitmap(int i, int i2, byte[] bArr, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i3);

    private static native void nDrawBitmap(int i, int i2, byte[] bArr, float f, float f2, int i3);

    private static native void nDrawBitmap(int i, int i2, byte[] bArr, int i3, int i4);

    private static native void nDrawBitmap(int i, int[] iArr, int i2, int i3, float f, float f2, int i4, int i5, boolean z, int i6);

    private static native void nDrawBitmapMesh(int i, int i2, byte[] bArr, int i3, int i4, float[] fArr, int i5, int[] iArr, int i6, int i7);

    private static native void nDrawCircle(int i, float f, float f2, float f3, int i2);

    private static native void nDrawColor(int i, int i2, int i3);

    private static native int nDrawDisplayList(int i, int i2, Rect rect, int i3);

    private static native void nDrawLayer(int i, int i2, float f, float f2);

    private static native void nDrawLines(int i, float[] fArr, int i2, int i3, int i4);

    private static native void nDrawOval(int i, float f, float f2, float f3, float f4, int i2);

    private static native void nDrawPatch(int i, int i2, byte[] bArr, int i3, float f, float f2, float f3, float f4, int i4);

    private static native void nDrawPath(int i, int i2, int i3);

    private static native void nDrawPoints(int i, float[] fArr, int i2, int i3, int i4);

    private static native void nDrawPosText(int i, String str, int i2, int i3, float[] fArr, int i4);

    private static native void nDrawPosText(int i, char[] cArr, int i2, int i3, float[] fArr, int i4);

    private static native void nDrawRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native void nDrawRects(int i, int i2, int i3);

    private static native void nDrawRects(int i, float[] fArr, int i2, int i3);

    private static native void nDrawRoundRect(int i, float f, float f2, float f3, float f4, float f5, float f6, int i2);

    private static native void nDrawText(int i, String str, int i2, int i3, float f, float f2, int i4, int i5);

    private static native void nDrawText(int i, char[] cArr, int i2, int i3, float f, float f2, int i4, int i5);

    private static native void nDrawTextOnPath(int i, String str, int i2, int i3, int i4, float f, float f2, int i5, int i6);

    private static native void nDrawTextOnPath(int i, char[] cArr, int i2, int i3, int i4, float f, float f2, int i5, int i6);

    private static native void nDrawTextRun(int i, String str, int i2, int i3, int i4, int i5, float f, float f2, int i6, int i7);

    private static native void nDrawTextRun(int i, char[] cArr, int i2, int i3, int i4, int i5, float f, float f2, int i6, int i7);

    private static native void nFinish(int i);

    private static native void nFlushCaches(int i);

    private static native void nFlushLayerUpdates(int i);

    private static native boolean nGetClipBounds(int i, Rect rect);

    private static native int nGetDisplayList(int i, int i2);

    private static native void nGetMatrix(int i, int i2);

    private static native int nGetMaximumTextureHeight();

    private static native int nGetMaximumTextureWidth();

    static native float nGetOverdraw(int i);

    private static native int nGetSaveCount(int i);

    private static native int nGetStencilSize();

    private static native void nInitAtlas(GraphicBuffer graphicBuffer, int[] iArr, int i);

    private static native boolean nInitCaches();

    private static native void nInterrupt(int i);

    private static native int nInvokeFunctors(int i, Rect rect);

    private static native boolean nIsAvailable();

    private static native void nOutputDisplayList(int i, int i2);

    private static native int nPrepare(int i, boolean z);

    private static native int nPrepareDirty(int i, int i2, int i3, int i4, int i5, boolean z);

    private static native void nPushLayerUpdate(int i, int i2);

    private static native boolean nQuickReject(int i, float f, float f2, float f3, float f4);

    private static native void nResetDisplayListRenderer(int i);

    private static native void nResetModifiers(int i, int i2);

    private static native void nResetPaintFilter(int i);

    static native boolean nResizeLayer(int i, int i2, int i3, int[] iArr);

    private static native void nRestore(int i);

    private static native void nRestoreToCount(int i, int i2);

    private static native void nResume(int i);

    private static native void nRotate(int i, float f);

    private static native int nSave(int i, int i2);

    private static native int nSaveLayer(int i, float f, float f2, float f3, float f4, int i2, int i3);

    private static native int nSaveLayer(int i, int i2, int i3);

    private static native int nSaveLayerAlpha(int i, float f, float f2, float f3, float f4, int i2, int i3);

    private static native int nSaveLayerAlpha(int i, int i2, int i3);

    private static native void nScale(int i, float f, float f2);

    static native void nSetCountOverdrawEnabled(int i, boolean z);

    static native void nSetLayerColorFilter(int i, int i2);

    static native void nSetLayerPaint(int i, int i2);

    private static native void nSetMatrix(int i, int i2);

    private static native void nSetName(int i, String str);

    static native void nSetOpaqueLayer(int i, boolean z);

    static native void nSetTextureLayerTransform(int i, int i2);

    private static native void nSetViewport(int i, int i2, int i3);

    private static native void nSetupColorFilter(int i, int i2);

    private static native void nSetupPaintFilter(int i, int i2, int i3);

    private static native void nSetupShader(int i, int i2);

    private static native void nSetupShadow(int i, float f, float f2, float f3, int i2);

    private static native void nSkew(int i, float f, float f2);

    private static native void nTerminateCaches();

    private static native void nTranslate(int i, float f, float f2);

    static native void nUpdateRenderLayer(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    static native void nUpdateTextureLayer(int i, int i2, int i3, boolean z, SurfaceTexture surfaceTexture);

    @Override // android.graphics.Canvas
    public void drawVertices(Canvas.VertexMode vertexMode, int i, float[] fArr, int i2, float[] fArr2, int i3, int[] iArr, int i4, short[] sArr, int i5, int i6, Paint paint) {
    }

    static boolean isAvailable() {
        return sIsAvailable;
    }

    GLES20Canvas(boolean z) {
        this(false, z);
    }

    GLES20Canvas(int i, boolean z) {
        this.mOpaque = !z;
        this.mRenderer = nCreateLayerRenderer(i);
        setupFinalizer();
    }

    protected GLES20Canvas(boolean z, boolean z2) {
        this.mOpaque = !z2;
        if (z) {
            this.mRenderer = nCreateDisplayListRenderer();
        } else {
            this.mRenderer = nCreateRenderer();
        }
        setupFinalizer();
    }

    private void setupFinalizer() {
        if (this.mRenderer == 0) {
            throw new IllegalStateException("Could not create GLES20Canvas renderer");
        }
        this.mFinalizer = new CanvasFinalizer(this.mRenderer);
    }

    protected void resetDisplayListRenderer() {
        nResetDisplayListRenderer(this.mRenderer);
    }

    private static final class CanvasFinalizer {
        private final int mRenderer;

        public CanvasFinalizer(int i) {
            this.mRenderer = i;
        }

        protected void finalize() throws Throwable {
            try {
                GLES20Canvas.nDestroyRenderer(this.mRenderer);
            } finally {
                super.finalize();
            }
        }
    }

    @Override // android.view.HardwareCanvas
    public void setName(String str) {
        super.setName(str);
        nSetName(this.mRenderer, str);
    }

    @Override // android.view.HardwareCanvas
    void pushLayerUpdate(HardwareLayer hardwareLayer) {
        nPushLayerUpdate(this.mRenderer, ((GLES20RenderLayer) hardwareLayer).mLayer);
    }

    @Override // android.view.HardwareCanvas
    void cancelLayerUpdate(HardwareLayer hardwareLayer) {
        nCancelLayerUpdate(this.mRenderer, ((GLES20RenderLayer) hardwareLayer).mLayer);
    }

    @Override // android.view.HardwareCanvas
    void flushLayerUpdates() {
        nFlushLayerUpdates(this.mRenderer);
    }

    @Override // android.view.HardwareCanvas
    void clearLayerUpdates() {
        nClearLayerUpdates(this.mRenderer);
    }

    @Override // android.graphics.Canvas
    public boolean isOpaque() {
        return this.mOpaque;
    }

    @Override // android.graphics.Canvas
    public int getWidth() {
        return this.mWidth;
    }

    @Override // android.graphics.Canvas
    public int getHeight() {
        return this.mHeight;
    }

    @Override // android.graphics.Canvas
    public int getMaximumBitmapWidth() {
        return nGetMaximumTextureWidth();
    }

    @Override // android.graphics.Canvas
    public int getMaximumBitmapHeight() {
        return nGetMaximumTextureHeight();
    }

    int getRenderer() {
        return this.mRenderer;
    }

    @Override // android.graphics.Canvas
    public void setViewport(int i, int i2) {
        this.mWidth = i;
        this.mHeight = i2;
        nSetViewport(this.mRenderer, i, i2);
    }

    @Override // android.view.HardwareCanvas
    public int onPreDraw(Rect rect) {
        if (rect != null) {
            return nPrepareDirty(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, this.mOpaque);
        }
        return nPrepare(this.mRenderer, this.mOpaque);
    }

    @Override // android.view.HardwareCanvas
    public void onPostDraw() {
        nFinish(this.mRenderer);
    }

    public static int getStencilSize() {
        return nGetStencilSize();
    }

    void setCountOverdrawEnabled(boolean z) {
        nSetCountOverdrawEnabled(this.mRenderer, z);
    }

    float getOverdraw() {
        return nGetOverdraw(this.mRenderer);
    }

    @Override // android.view.HardwareCanvas
    public int callDrawGLFunction(int i) {
        return nCallDrawGLFunction(this.mRenderer, i);
    }

    @Override // android.view.HardwareCanvas
    public int invokeFunctors(Rect rect) {
        return nInvokeFunctors(this.mRenderer, rect);
    }

    @Override // android.view.HardwareCanvas
    public void detachFunctor(int i) {
        nDetachFunctor(this.mRenderer, i);
    }

    @Override // android.view.HardwareCanvas
    public void attachFunctor(int i) {
        nAttachFunctor(this.mRenderer, i);
    }

    static void flushCaches(int i) {
        nFlushCaches(i);
    }

    static void terminateCaches() {
        nTerminateCaches();
    }

    static boolean initCaches() {
        return nInitCaches();
    }

    static void initAtlas(GraphicBuffer graphicBuffer, int[] iArr) {
        nInitAtlas(graphicBuffer, iArr, iArr.length);
    }

    int getDisplayList(int i) {
        return nGetDisplayList(this.mRenderer, i);
    }

    @Override // android.view.HardwareCanvas
    void outputDisplayList(DisplayList displayList) {
        nOutputDisplayList(this.mRenderer, ((GLES20DisplayList) displayList).getNativeDisplayList());
    }

    @Override // android.view.HardwareCanvas
    public int drawDisplayList(DisplayList displayList, Rect rect, int i) {
        return nDrawDisplayList(this.mRenderer, ((GLES20DisplayList) displayList).getNativeDisplayList(), rect, i);
    }

    @Override // android.view.HardwareCanvas
    void drawHardwareLayer(HardwareLayer hardwareLayer, float f, float f2, Paint paint) {
        hardwareLayer.setLayerPaint(paint);
        nDrawLayer(this.mRenderer, ((GLES20Layer) hardwareLayer).getLayer(), f, f2);
    }

    void interrupt() {
        nInterrupt(this.mRenderer);
    }

    void resume() {
        nResume(this.mRenderer);
    }

    private Rect getInternalClipBounds() {
        if (this.mClipBounds == null) {
            this.mClipBounds = new Rect();
        }
        return this.mClipBounds;
    }

    private RectF getPathBounds() {
        if (this.mPathBounds == null) {
            this.mPathBounds = new RectF();
        }
        return this.mPathBounds;
    }

    private float[] getPointStorage() {
        if (this.mPoint == null) {
            this.mPoint = new float[2];
        }
        return this.mPoint;
    }

    private float[] getLineStorage() {
        if (this.mLine == null) {
            this.mLine = new float[4];
        }
        return this.mLine;
    }

    @Override // android.graphics.Canvas
    public boolean clipPath(Path path) {
        return nClipPath(this.mRenderer, path.mNativePath, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipPath(Path path, Region.Op op) {
        return nClipPath(this.mRenderer, path.mNativePath, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(float f, float f2, float f3, float f4) {
        return nClipRect(this.mRenderer, f, f2, f3, f4, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(float f, float f2, float f3, float f4, Region.Op op) {
        return nClipRect(this.mRenderer, f, f2, f3, f4, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(int i, int i2, int i3, int i4) {
        return nClipRect(this.mRenderer, i, i2, i3, i4, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(Rect rect) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(Rect rect, Region.Op op) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(RectF rectF) {
        return nClipRect(this.mRenderer, rectF.left, rectF.top, rectF.right, rectF.bottom, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(RectF rectF, Region.Op op) {
        return nClipRect(this.mRenderer, rectF.left, rectF.top, rectF.right, rectF.bottom, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRegion(Region region) {
        return nClipRegion(this.mRenderer, region.mNativeRegion, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRegion(Region region, Region.Op op) {
        return nClipRegion(this.mRenderer, region.mNativeRegion, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean getClipBounds(Rect rect) {
        return nGetClipBounds(this.mRenderer, rect);
    }

    @Override // android.graphics.Canvas
    public boolean quickReject(float f, float f2, float f3, float f4, Canvas.EdgeType edgeType) {
        return nQuickReject(this.mRenderer, f, f2, f3, f4);
    }

    @Override // android.graphics.Canvas
    public boolean quickReject(Path path, Canvas.EdgeType edgeType) {
        RectF pathBounds = getPathBounds();
        path.computeBounds(pathBounds, true);
        return nQuickReject(this.mRenderer, pathBounds.left, pathBounds.top, pathBounds.right, pathBounds.bottom);
    }

    @Override // android.graphics.Canvas
    public boolean quickReject(RectF rectF, Canvas.EdgeType edgeType) {
        return nQuickReject(this.mRenderer, rectF.left, rectF.top, rectF.right, rectF.bottom);
    }

    @Override // android.graphics.Canvas
    public void translate(float f, float f2) {
        if (f == 0.0f && f2 == 0.0f) {
            return;
        }
        nTranslate(this.mRenderer, f, f2);
    }

    @Override // android.graphics.Canvas
    public void skew(float f, float f2) {
        nSkew(this.mRenderer, f, f2);
    }

    @Override // android.graphics.Canvas
    public void rotate(float f) {
        nRotate(this.mRenderer, f);
    }

    @Override // android.graphics.Canvas
    public void scale(float f, float f2) {
        nScale(this.mRenderer, f, f2);
    }

    @Override // android.graphics.Canvas
    public void setMatrix(Matrix matrix) {
        nSetMatrix(this.mRenderer, matrix == null ? 0 : matrix.native_instance);
    }

    @Override // android.graphics.Canvas
    public void getMatrix(Matrix matrix) {
        nGetMatrix(this.mRenderer, matrix.native_instance);
    }

    @Override // android.graphics.Canvas
    public void concat(Matrix matrix) {
        if (matrix != null) {
            nConcatMatrix(this.mRenderer, matrix.native_instance);
        }
    }

    @Override // android.graphics.Canvas
    public int save() {
        return nSave(this.mRenderer, 3);
    }

    @Override // android.graphics.Canvas
    public int save(int i) {
        return nSave(this.mRenderer, i);
    }

    @Override // android.graphics.Canvas
    public int saveLayer(RectF rectF, Paint paint, int i) {
        if (rectF != null) {
            return saveLayer(rectF.left, rectF.top, rectF.right, rectF.bottom, paint, i);
        }
        int i2 = 0;
        int i3 = paint != null ? setupColorFilter(paint) : 0;
        if (paint != null) {
            try {
                i2 = paint.mNativePaint;
            } finally {
                if (i3 != 0) {
                    nResetModifiers(this.mRenderer, i3);
                }
            }
        }
        return nSaveLayer(this.mRenderer, i2, i);
    }

    @Override // android.graphics.Canvas
    public int saveLayer(float f, float f2, float f3, float f4, Paint paint, int i) {
        if (f < f3 && f2 < f4) {
            int i2 = 0;
            int i3 = paint != null ? setupColorFilter(paint) : 0;
            if (paint != null) {
                try {
                    i2 = paint.mNativePaint;
                } finally {
                    if (i3 != 0) {
                        nResetModifiers(this.mRenderer, i3);
                    }
                }
            }
            return nSaveLayer(this.mRenderer, f, f2, f3, f4, i2, i);
        }
        return save(i);
    }

    @Override // android.graphics.Canvas
    public int saveLayerAlpha(RectF rectF, int i, int i2) {
        if (rectF != null) {
            return saveLayerAlpha(rectF.left, rectF.top, rectF.right, rectF.bottom, i, i2);
        }
        return nSaveLayerAlpha(this.mRenderer, i, i2);
    }

    @Override // android.graphics.Canvas
    public int saveLayerAlpha(float f, float f2, float f3, float f4, int i, int i2) {
        if (f < f3 && f2 < f4) {
            return nSaveLayerAlpha(this.mRenderer, f, f2, f3, f4, i, i2);
        }
        return save(i2);
    }

    @Override // android.graphics.Canvas
    public void restore() {
        nRestore(this.mRenderer);
    }

    @Override // android.graphics.Canvas
    public void restoreToCount(int i) {
        nRestoreToCount(this.mRenderer, i);
    }

    @Override // android.graphics.Canvas
    public int getSaveCount() {
        return nGetSaveCount(this.mRenderer);
    }

    @Override // android.graphics.Canvas
    public void setDrawFilter(DrawFilter drawFilter) {
        this.mFilter = drawFilter;
        if (drawFilter == null) {
            nResetPaintFilter(this.mRenderer);
        } else if (drawFilter instanceof PaintFlagsDrawFilter) {
            PaintFlagsDrawFilter paintFlagsDrawFilter = (PaintFlagsDrawFilter) drawFilter;
            nSetupPaintFilter(this.mRenderer, paintFlagsDrawFilter.clearBits, paintFlagsDrawFilter.setBits);
        }
    }

    @Override // android.graphics.Canvas
    public DrawFilter getDrawFilter() {
        return this.mFilter;
    }

    @Override // android.graphics.Canvas
    public void drawArc(RectF rectF, float f, float f2, boolean z, Paint paint) {
        int i = setupModifiers(paint, 6);
        try {
            nDrawArc(this.mRenderer, rectF.left, rectF.top, rectF.right, rectF.bottom, f, f2, z, paint.mNativePaint);
        } finally {
            if (i != 0) {
                nResetModifiers(this.mRenderer, i);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawARGB(int i, int i2, int i3, int i4) {
        drawColor(((i & 255) << 24) | ((i2 & 255) << 16) | ((i3 & 255) << 8) | (i4 & 255));
    }

    @Override // android.graphics.Canvas
    public void drawPatch(NinePatch ninePatch, Rect rect, Paint paint) {
        Bitmap bitmap = ninePatch.getBitmap();
        throwIfCannotDraw(bitmap);
        int i = 0;
        int i2 = paint != null ? setupColorFilter(paint) : 0;
        if (paint != null) {
            try {
                i = paint.mNativePaint;
            } finally {
                if (i2 != 0) {
                    nResetModifiers(this.mRenderer, i2);
                }
            }
        }
        nDrawPatch(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, ninePatch.mNativeChunk, rect.left, rect.top, rect.right, rect.bottom, i);
    }

    @Override // android.graphics.Canvas
    public void drawPatch(NinePatch ninePatch, RectF rectF, Paint paint) {
        Bitmap bitmap = ninePatch.getBitmap();
        throwIfCannotDraw(bitmap);
        int i = 0;
        int i2 = paint != null ? setupColorFilter(paint) : 0;
        if (paint != null) {
            try {
                i = paint.mNativePaint;
            } finally {
                if (i2 != 0) {
                    nResetModifiers(this.mRenderer, i2);
                }
            }
        }
        nDrawPatch(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, ninePatch.mNativeChunk, rectF.left, rectF.top, rectF.right, rectF.bottom, i);
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(Bitmap bitmap, float f, float f2, Paint paint) {
        throwIfCannotDraw(bitmap);
        int i = 0;
        int i2 = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint != null) {
            try {
                i = paint.mNativePaint;
            } finally {
                if (i2 != 0) {
                    nResetModifiers(this.mRenderer, i2);
                }
            }
        }
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, f, f2, i);
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
        throwIfCannotDraw(bitmap);
        int i = 0;
        int i2 = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint != null) {
            try {
                i = paint.mNativePaint;
            } finally {
                if (i2 != 0) {
                    nResetModifiers(this.mRenderer, i2);
                }
            }
        }
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, matrix.native_instance, i);
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(Bitmap bitmap, Rect rect, Rect rect2, Paint paint) {
        int i;
        int i2;
        int height;
        int width;
        throwIfCannotDraw(bitmap);
        int i3 = 0;
        int i4 = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } finally {
                if (i4 != 0) {
                    nResetModifiers(this.mRenderer, i4);
                }
            }
        }
        if (rect == null) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            i2 = 0;
        } else {
            i3 = rect.left;
            int i5 = rect.right;
            i2 = rect.top;
            height = rect.bottom;
            width = i5;
        }
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, i3, i2, width, height, rect2.left, rect2.top, rect2.right, rect2.bottom, i);
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(Bitmap bitmap, Rect rect, RectF rectF, Paint paint) {
        float height;
        float f;
        float width;
        float f2;
        throwIfCannotDraw(bitmap);
        int i = 0;
        int i2 = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint != null) {
            try {
                i = paint.mNativePaint;
            } finally {
                if (i2 != 0) {
                    nResetModifiers(this.mRenderer, i2);
                }
            }
        }
        int i3 = i;
        if (rect == null) {
            f = 0.0f;
            f2 = 0.0f;
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        } else {
            float f3 = rect.left;
            float f4 = rect.right;
            float f5 = rect.top;
            height = rect.bottom;
            f = f3;
            width = f4;
            f2 = f5;
        }
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, f, f2, width, height, rectF.left, rectF.top, rectF.right, rectF.bottom, i3);
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(int[] iArr, int i, int i2, float f, float f2, int i3, int i4, boolean z, Paint paint) {
        int i5;
        if (i3 < 0) {
            throw new IllegalArgumentException("width must be >= 0");
        }
        if (i4 < 0) {
            throw new IllegalArgumentException("height must be >= 0");
        }
        if (Math.abs(i2) < i3) {
            throw new IllegalArgumentException("abs(stride) must be >= width");
        }
        int i6 = ((i4 - 1) * i2) + i;
        int length = iArr.length;
        if (i < 0 || i + i3 > length || i6 < 0 || i6 + i3 > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int i7 = paint != null ? setupColorFilter(paint) : 0;
        if (paint == null) {
            i5 = 0;
        } else {
            try {
                i5 = paint.mNativePaint;
            } finally {
                if (i7 != 0) {
                    nResetModifiers(this.mRenderer, i7);
                }
            }
        }
        nDrawBitmap(this.mRenderer, iArr, i, i2, f, f2, i3, i4, z, i5);
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6, boolean z, Paint paint) {
        drawBitmap(iArr, i, i2, i3, i4, i5, i6, z, paint);
    }

    @Override // android.graphics.Canvas
    public void drawBitmapMesh(Bitmap bitmap, int i, int i2, float[] fArr, int i3, int[] iArr, int i4, Paint paint) {
        int i5;
        throwIfCannotDraw(bitmap);
        if (i < 0 || i2 < 0 || i3 < 0 || i4 < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (i == 0 || i2 == 0) {
            return;
        }
        int i6 = (i + 1) * (i2 + 1);
        checkRange(fArr.length, i3, i6 * 2);
        if (iArr != null) {
            checkRange(iArr.length, i4, i6);
        }
        int i7 = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint == null) {
            i5 = 0;
        } else {
            try {
                i5 = paint.mNativePaint;
            } finally {
                if (i7 != 0) {
                    nResetModifiers(this.mRenderer, i7);
                }
            }
        }
        nDrawBitmapMesh(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, i, i2, fArr, i3, iArr, i4, i5);
    }

    @Override // android.graphics.Canvas
    public void drawCircle(float f, float f2, float f3, Paint paint) {
        int i = setupModifiers(paint, 6);
        try {
            nDrawCircle(this.mRenderer, f, f2, f3, paint.mNativePaint);
        } finally {
            if (i != 0) {
                nResetModifiers(this.mRenderer, i);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawColor(int i) {
        drawColor(i, PorterDuff.Mode.SRC_OVER);
    }

    @Override // android.graphics.Canvas
    public void drawColor(int i, PorterDuff.Mode mode) {
        nDrawColor(this.mRenderer, i, mode.nativeInt);
    }

    @Override // android.graphics.Canvas
    public void drawLine(float f, float f2, float f3, float f4, Paint paint) {
        float[] lineStorage = getLineStorage();
        lineStorage[0] = f;
        lineStorage[1] = f2;
        lineStorage[2] = f3;
        lineStorage[3] = f4;
        drawLines(lineStorage, 0, 4, paint);
    }

    @Override // android.graphics.Canvas
    public void drawLines(float[] fArr, int i, int i2, Paint paint) {
        if (i2 < 4) {
            return;
        }
        if ((i | i2) < 0 || i + i2 > fArr.length) {
            throw new IllegalArgumentException("The lines array must contain 4 elements per line.");
        }
        int i3 = setupModifiers(paint, 6);
        try {
            nDrawLines(this.mRenderer, fArr, i, i2, paint.mNativePaint);
        } finally {
            if (i3 != 0) {
                nResetModifiers(this.mRenderer, i3);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawLines(float[] fArr, Paint paint) {
        drawLines(fArr, 0, fArr.length, paint);
    }

    @Override // android.graphics.Canvas
    public void drawOval(RectF rectF, Paint paint) {
        int i = setupModifiers(paint, 6);
        try {
            nDrawOval(this.mRenderer, rectF.left, rectF.top, rectF.right, rectF.bottom, paint.mNativePaint);
        } finally {
            if (i != 0) {
                nResetModifiers(this.mRenderer, i);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawPaint(Paint paint) {
        nGetClipBounds(this.mRenderer, getInternalClipBounds());
        drawRect(r0.left, r0.top, r0.right, r0.bottom, paint);
    }

    @Override // android.graphics.Canvas
    public void drawPath(Path path, Paint paint) {
        int i = setupModifiers(paint, 6);
        try {
            if (path.isSimplePath) {
                if (path.rects != null) {
                    nDrawRects(this.mRenderer, path.rects.mNativeRegion, paint.mNativePaint);
                }
            } else {
                nDrawPath(this.mRenderer, path.mNativePath, paint.mNativePaint);
            }
        } finally {
            if (i != 0) {
                nResetModifiers(this.mRenderer, i);
            }
        }
    }

    void drawRects(float[] fArr, int i, Paint paint) {
        int i2 = setupModifiers(paint, 6);
        try {
            nDrawRects(this.mRenderer, fArr, i, paint.mNativePaint);
        } finally {
            if (i2 != 0) {
                nResetModifiers(this.mRenderer, i2);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawPicture(Picture picture) {
        if (picture.createdFromStream) {
            return;
        }
        picture.endRecording();
    }

    @Override // android.graphics.Canvas
    public void drawPicture(Picture picture, Rect rect) {
        if (picture.createdFromStream) {
            return;
        }
        save();
        translate(rect.left, rect.top);
        if (picture.getWidth() > 0 && picture.getHeight() > 0) {
            scale(rect.width() / picture.getWidth(), rect.height() / picture.getHeight());
        }
        drawPicture(picture);
        restore();
    }

    @Override // android.graphics.Canvas
    public void drawPicture(Picture picture, RectF rectF) {
        if (picture.createdFromStream) {
            return;
        }
        save();
        translate(rectF.left, rectF.top);
        if (picture.getWidth() > 0 && picture.getHeight() > 0) {
            scale(rectF.width() / picture.getWidth(), rectF.height() / picture.getHeight());
        }
        drawPicture(picture);
        restore();
    }

    @Override // android.graphics.Canvas
    public void drawPoint(float f, float f2, Paint paint) {
        float[] pointStorage = getPointStorage();
        pointStorage[0] = f;
        pointStorage[1] = f2;
        drawPoints(pointStorage, 0, 2, paint);
    }

    @Override // android.graphics.Canvas
    public void drawPoints(float[] fArr, Paint paint) {
        drawPoints(fArr, 0, fArr.length, paint);
    }

    @Override // android.graphics.Canvas
    public void drawPoints(float[] fArr, int i, int i2, Paint paint) {
        if (i2 < 2) {
            return;
        }
        int i3 = setupModifiers(paint, 6);
        try {
            nDrawPoints(this.mRenderer, fArr, i, i2, paint.mNativePaint);
        } finally {
            if (i3 != 0) {
                nResetModifiers(this.mRenderer, i3);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawPosText(char[] cArr, int i, int i2, float[] fArr, Paint paint) {
        if (i < 0 || i + i2 > cArr.length || i2 * 2 > fArr.length) {
            throw new IndexOutOfBoundsException();
        }
        int i3 = setupModifiers(paint);
        try {
            nDrawPosText(this.mRenderer, cArr, i, i2, fArr, paint.mNativePaint);
        } finally {
            if (i3 != 0) {
                nResetModifiers(this.mRenderer, i3);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawPosText(String str, float[] fArr, Paint paint) {
        if (str.length() * 2 > fArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int i = setupModifiers(paint);
        try {
            nDrawPosText(this.mRenderer, str, 0, str.length(), fArr, paint.mNativePaint);
        } finally {
            if (i != 0) {
                nResetModifiers(this.mRenderer, i);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawRect(float f, float f2, float f3, float f4, Paint paint) {
        if (f == f3 || f2 == f4) {
            return;
        }
        int i = setupModifiers(paint, 6);
        try {
            nDrawRect(this.mRenderer, f, f2, f3, f4, paint.mNativePaint);
        } finally {
            if (i != 0) {
                nResetModifiers(this.mRenderer, i);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawRect(Rect rect, Paint paint) {
        drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
    }

    @Override // android.graphics.Canvas
    public void drawRect(RectF rectF, Paint paint) {
        drawRect(rectF.left, rectF.top, rectF.right, rectF.bottom, paint);
    }

    @Override // android.graphics.Canvas
    public void drawRGB(int i, int i2, int i3) {
        drawColor(((i & 255) << 16) | (-16777216) | ((i2 & 255) << 8) | (i3 & 255));
    }

    @Override // android.graphics.Canvas
    public void drawRoundRect(RectF rectF, float f, float f2, Paint paint) {
        int i = setupModifiers(paint, 6);
        try {
            nDrawRoundRect(this.mRenderer, rectF.left, rectF.top, rectF.right, rectF.bottom, f, f2, paint.mNativePaint);
        } finally {
            if (i != 0) {
                nResetModifiers(this.mRenderer, i);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawText(char[] cArr, int i, int i2, float f, float f2, Paint paint) {
        if ((i | i2 | (i + i2) | ((cArr.length - i) - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        int i3 = setupModifiers(paint);
        try {
            nDrawText(this.mRenderer, cArr, i, i2, f, f2, paint.mBidiFlags, paint.mNativePaint);
        } finally {
            if (i3 != 0) {
                nResetModifiers(this.mRenderer, i3);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawText(CharSequence charSequence, int i, int i2, float f, float f2, Paint paint) {
        int i3 = setupModifiers(paint);
        try {
            if ((charSequence instanceof String) || (charSequence instanceof SpannedString) || (charSequence instanceof SpannableString)) {
                nDrawText(this.mRenderer, charSequence.toString(), i, i2, f, f2, paint.mBidiFlags, paint.mNativePaint);
            } else if (charSequence instanceof GraphicsOperations) {
                ((GraphicsOperations) charSequence).drawText(this, i, i2, f, f2, paint);
            } else {
                int i4 = i2 - i;
                char[] cArrObtain = TemporaryBuffer.obtain(i4);
                TextUtils.getChars(charSequence, i, i2, cArrObtain, 0);
                nDrawText(this.mRenderer, cArrObtain, 0, i4, f, f2, paint.mBidiFlags, paint.mNativePaint);
                TemporaryBuffer.recycle(cArrObtain);
            }
        } finally {
            if (i3 != 0) {
                nResetModifiers(this.mRenderer, i3);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawText(String str, int i, int i2, float f, float f2, Paint paint) {
        if ((i | i2 | (i2 - i) | (str.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        int i3 = setupModifiers(paint);
        try {
            nDrawText(this.mRenderer, str, i, i2, f, f2, paint.mBidiFlags, paint.mNativePaint);
        } finally {
            if (i3 != 0) {
                nResetModifiers(this.mRenderer, i3);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawText(String str, float f, float f2, Paint paint) {
        int i = setupModifiers(paint);
        try {
            nDrawText(this.mRenderer, str, 0, str.length(), f, f2, paint.mBidiFlags, paint.mNativePaint);
        } finally {
            if (i != 0) {
                nResetModifiers(this.mRenderer, i);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawTextOnPath(char[] cArr, int i, int i2, Path path, float f, float f2, Paint paint) {
        if (i < 0 || i + i2 > cArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int i3 = setupModifiers(paint);
        try {
            nDrawTextOnPath(this.mRenderer, cArr, i, i2, path.mNativePath, f, f2, paint.mBidiFlags, paint.mNativePaint);
        } finally {
            if (i3 != 0) {
                nResetModifiers(this.mRenderer, i3);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawTextOnPath(String str, Path path, float f, float f2, Paint paint) {
        if (str.length() == 0) {
            return;
        }
        int i = setupModifiers(paint);
        try {
            nDrawTextOnPath(this.mRenderer, str, 0, str.length(), path.mNativePath, f, f2, paint.mBidiFlags, paint.mNativePaint);
        } finally {
            if (i != 0) {
                nResetModifiers(this.mRenderer, i);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawTextRun(char[] cArr, int i, int i2, int i3, int i4, float f, float f2, int i5, Paint paint) {
        if ((i | i2 | ((cArr.length - i) - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (i5 != 0 && i5 != 1) {
            throw new IllegalArgumentException("Unknown direction: " + i5);
        }
        int i6 = setupModifiers(paint);
        try {
            nDrawTextRun(this.mRenderer, cArr, i, i2, i3, i4, f, f2, i5, paint.mNativePaint);
        } finally {
            if (i6 != 0) {
                nResetModifiers(this.mRenderer, i6);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0092  */
    /* JADX WARN: Removed duplicated region for block: B:44:? A[RETURN, SYNTHETIC] */
    @Override // android.graphics.Canvas
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawTextRun(java.lang.CharSequence r23, int r24, int r25, int r26, int r27, float r28, float r29, int r30, android.graphics.Paint r31) throws java.lang.Throwable {
        /*
            r22 = this;
            r11 = r22
            r0 = r23
            r5 = r26
            r6 = r27
            r10 = r31
            r1 = r24 | r25
            int r15 = r25 - r24
            r1 = r1 | r15
            int r2 = r23.length()
            int r2 = r2 - r25
            r1 = r1 | r2
            if (r1 < 0) goto La7
            int r14 = r11.setupModifiers(r10)
            r1 = 0
            if (r30 != 0) goto L22
            r20 = r1
            goto L25
        L22:
            r2 = 1
            r20 = r2
        L25:
            boolean r2 = r0 instanceof java.lang.String     // Catch: java.lang.Throwable -> L9d
            if (r2 != 0) goto L72
            boolean r2 = r0 instanceof android.text.SpannedString     // Catch: java.lang.Throwable -> L9d
            if (r2 != 0) goto L72
            boolean r2 = r0 instanceof android.text.SpannableString     // Catch: java.lang.Throwable -> L9d
            if (r2 == 0) goto L32
            goto L72
        L32:
            boolean r2 = r0 instanceof android.text.GraphicsOperations     // Catch: java.lang.Throwable -> L9d
            if (r2 == 0) goto L50
            r1 = r0
            android.text.GraphicsOperations r1 = (android.text.GraphicsOperations) r1     // Catch: java.lang.Throwable -> L9d
            r2 = r22
            r3 = r24
            r4 = r25
            r5 = r26
            r6 = r27
            r7 = r28
            r8 = r29
            r9 = r20
            r10 = r31
            r1.drawTextRun(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch: java.lang.Throwable -> L9d
            r12 = r14
            goto L90
        L50:
            int r17 = r6 - r5
            char[] r2 = android.graphics.TemporaryBuffer.obtain(r17)     // Catch: java.lang.Throwable -> L9d
            android.text.TextUtils.getChars(r0, r5, r6, r2, r1)     // Catch: java.lang.Throwable -> L9d
            int r12 = r11.mRenderer     // Catch: java.lang.Throwable -> L9d
            int r0 = r24 - r5
            r16 = 0
            int r1 = r10.mNativePaint     // Catch: java.lang.Throwable -> L9d
            r13 = r2
            r9 = r14
            r14 = r0
            r18 = r28
            r19 = r29
            r21 = r1
            nDrawTextRun(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch: java.lang.Throwable -> L9a
            android.graphics.TemporaryBuffer.recycle(r2)     // Catch: java.lang.Throwable -> L9a
            r12 = r9
            goto L90
        L72:
            r9 = r14
            int r1 = r11.mRenderer     // Catch: java.lang.Throwable -> L9a
            java.lang.String r2 = r23.toString()     // Catch: java.lang.Throwable -> L9a
            int r10 = r10.mNativePaint     // Catch: java.lang.Throwable -> L9a
            r0 = r1
            r1 = r2
            r2 = r24
            r3 = r25
            r4 = r26
            r5 = r27
            r6 = r28
            r7 = r29
            r8 = r20
            r12 = r9
            r9 = r10
            nDrawTextRun(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9)     // Catch: java.lang.Throwable -> L98
        L90:
            if (r12 == 0) goto L97
            int r0 = r11.mRenderer
            nResetModifiers(r0, r12)
        L97:
            return
        L98:
            r0 = move-exception
            goto L9f
        L9a:
            r0 = move-exception
            r12 = r9
            goto L9f
        L9d:
            r0 = move-exception
            r12 = r14
        L9f:
            if (r12 == 0) goto La6
            int r1 = r11.mRenderer
            nResetModifiers(r1, r12)
        La6:
            throw r0
        La7:
            java.lang.IndexOutOfBoundsException r0 = new java.lang.IndexOutOfBoundsException
            r0.<init>()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.GLES20Canvas.drawTextRun(java.lang.CharSequence, int, int, int, int, float, float, int, android.graphics.Paint):void");
    }

    private int setupModifiers(Bitmap bitmap, Paint paint) {
        if (bitmap.getConfig() != Bitmap.Config.ALPHA_8) {
            ColorFilter colorFilter = paint.getColorFilter();
            if (colorFilter == null) {
                return 0;
            }
            nSetupColorFilter(this.mRenderer, colorFilter.nativeColorFilter);
            return 4;
        }
        return setupModifiers(paint);
    }

    private int setupModifiers(Paint paint) {
        int i;
        if (paint.hasShadow) {
            nSetupShadow(this.mRenderer, paint.shadowRadius, paint.shadowDx, paint.shadowDy, paint.shadowColor);
            i = 1;
        } else {
            i = 0;
        }
        Shader shader = paint.getShader();
        if (shader != null) {
            nSetupShader(this.mRenderer, shader.native_shader);
            i |= 2;
        }
        ColorFilter colorFilter = paint.getColorFilter();
        if (colorFilter == null) {
            return i;
        }
        nSetupColorFilter(this.mRenderer, colorFilter.nativeColorFilter);
        return i | 4;
    }

    private int setupModifiers(Paint paint, int i) {
        int i2;
        if (!paint.hasShadow || (i & 1) == 0) {
            i2 = 0;
        } else {
            nSetupShadow(this.mRenderer, paint.shadowRadius, paint.shadowDx, paint.shadowDy, paint.shadowColor);
            i2 = 1;
        }
        Shader shader = paint.getShader();
        if (shader != null && (i & 2) != 0) {
            nSetupShader(this.mRenderer, shader.native_shader);
            i2 |= 2;
        }
        ColorFilter colorFilter = paint.getColorFilter();
        if (colorFilter == null || (i & 4) == 0) {
            return i2;
        }
        nSetupColorFilter(this.mRenderer, colorFilter.nativeColorFilter);
        return i2 | 4;
    }

    private int setupColorFilter(Paint paint) {
        ColorFilter colorFilter = paint.getColorFilter();
        if (colorFilter == null) {
            return 0;
        }
        nSetupColorFilter(this.mRenderer, colorFilter.nativeColorFilter);
        return 4;
    }
}
