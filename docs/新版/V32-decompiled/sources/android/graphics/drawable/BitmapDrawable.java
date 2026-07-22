package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import com.android.internal.R;
import com.bumptech.glide.Registry;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class BitmapDrawable extends Drawable {
    private static final int DEFAULT_PAINT_FLAGS = 6;
    private boolean mApplyGravity;
    private Bitmap mBitmap;
    private int mBitmapHeight;
    private BitmapState mBitmapState;
    private int mBitmapWidth;
    private final Rect mDstRect;
    private Matrix mMirrorMatrix;
    private boolean mMutated;
    private int mTargetDensity;

    @Deprecated
    public BitmapDrawable() {
        this.mDstRect = new Rect();
        this.mBitmapState = new BitmapState((Bitmap) null);
    }

    @Deprecated
    public BitmapDrawable(Resources resources) {
        this.mDstRect = new Rect();
        BitmapState bitmapState = new BitmapState((Bitmap) null);
        this.mBitmapState = bitmapState;
        bitmapState.mTargetDensity = this.mTargetDensity;
    }

    @Deprecated
    public BitmapDrawable(Bitmap bitmap) {
        this(new BitmapState(bitmap), (Resources) null);
    }

    public BitmapDrawable(Resources resources, Bitmap bitmap) {
        this(new BitmapState(bitmap), resources);
        this.mBitmapState.mTargetDensity = this.mTargetDensity;
    }

    @Deprecated
    public BitmapDrawable(String str) {
        this(new BitmapState(BitmapFactory.decodeFile(str)), (Resources) null);
        if (this.mBitmap == null) {
            Log.w(Registry.BUCKET_BITMAP_DRAWABLE, "BitmapDrawable cannot decode " + str);
        }
    }

    public BitmapDrawable(Resources resources, String str) {
        this(new BitmapState(BitmapFactory.decodeFile(str)), (Resources) null);
        this.mBitmapState.mTargetDensity = this.mTargetDensity;
        if (this.mBitmap == null) {
            Log.w(Registry.BUCKET_BITMAP_DRAWABLE, "BitmapDrawable cannot decode " + str);
        }
    }

    @Deprecated
    public BitmapDrawable(InputStream inputStream) {
        this(new BitmapState(BitmapFactory.decodeStream(inputStream)), (Resources) null);
        if (this.mBitmap == null) {
            Log.w(Registry.BUCKET_BITMAP_DRAWABLE, "BitmapDrawable cannot decode " + inputStream);
        }
    }

    public BitmapDrawable(Resources resources, InputStream inputStream) {
        this(new BitmapState(BitmapFactory.decodeStream(inputStream)), (Resources) null);
        this.mBitmapState.mTargetDensity = this.mTargetDensity;
        if (this.mBitmap == null) {
            Log.w(Registry.BUCKET_BITMAP_DRAWABLE, "BitmapDrawable cannot decode " + inputStream);
        }
    }

    public final Paint getPaint() {
        return this.mBitmapState.mPaint;
    }

    public final Bitmap getBitmap() {
        return this.mBitmap;
    }

    private void computeBitmapSize() {
        this.mBitmapWidth = this.mBitmap.getScaledWidth(this.mTargetDensity);
        this.mBitmapHeight = this.mBitmap.getScaledHeight(this.mTargetDensity);
    }

    private void setBitmap(Bitmap bitmap) {
        if (bitmap != this.mBitmap) {
            this.mBitmap = bitmap;
            if (bitmap != null) {
                computeBitmapSize();
            } else {
                this.mBitmapHeight = -1;
                this.mBitmapWidth = -1;
            }
            invalidateSelf();
        }
    }

    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    public void setTargetDensity(DisplayMetrics displayMetrics) {
        setTargetDensity(displayMetrics.densityDpi);
    }

    public void setTargetDensity(int i) {
        if (this.mTargetDensity != i) {
            if (i == 0) {
                i = 160;
            }
            this.mTargetDensity = i;
            if (this.mBitmap != null) {
                computeBitmapSize();
            }
            invalidateSelf();
        }
    }

    public int getGravity() {
        return this.mBitmapState.mGravity;
    }

    public void setGravity(int i) {
        if (this.mBitmapState.mGravity != i) {
            this.mBitmapState.mGravity = i;
            this.mApplyGravity = true;
            invalidateSelf();
        }
    }

    public void setMipMap(boolean z) {
        if (this.mBitmapState.mBitmap != null) {
            this.mBitmapState.mBitmap.setHasMipMap(z);
            invalidateSelf();
        }
    }

    public boolean hasMipMap() {
        return this.mBitmapState.mBitmap != null && this.mBitmapState.mBitmap.hasMipMap();
    }

    public void setAntiAlias(boolean z) {
        this.mBitmapState.mPaint.setAntiAlias(z);
        invalidateSelf();
    }

    public boolean hasAntiAlias() {
        return this.mBitmapState.mPaint.isAntiAlias();
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean z) {
        this.mBitmapState.mPaint.setFilterBitmap(z);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean z) {
        this.mBitmapState.mPaint.setDither(z);
        invalidateSelf();
    }

    public Shader.TileMode getTileModeX() {
        return this.mBitmapState.mTileModeX;
    }

    public Shader.TileMode getTileModeY() {
        return this.mBitmapState.mTileModeY;
    }

    public void setTileModeX(Shader.TileMode tileMode) {
        setTileModeXY(tileMode, this.mBitmapState.mTileModeY);
    }

    public final void setTileModeY(Shader.TileMode tileMode) {
        setTileModeXY(this.mBitmapState.mTileModeX, tileMode);
    }

    public void setTileModeXY(Shader.TileMode tileMode, Shader.TileMode tileMode2) {
        BitmapState bitmapState = this.mBitmapState;
        if (bitmapState.mTileModeX == tileMode && bitmapState.mTileModeY == tileMode2) {
            return;
        }
        bitmapState.mTileModeX = tileMode;
        bitmapState.mTileModeY = tileMode2;
        bitmapState.mRebuildShader = true;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean z) {
        if (this.mBitmapState.mAutoMirrored != z) {
            this.mBitmapState.mAutoMirrored = z;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isAutoMirrored() {
        return this.mBitmapState.mAutoMirrored;
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mBitmapState.mChangingConfigurations;
    }

    private boolean needMirroring() {
        return isAutoMirrored() && getLayoutDirection() == 1;
    }

    private void updateMirrorMatrix(float f) {
        if (this.mMirrorMatrix == null) {
            this.mMirrorMatrix = new Matrix();
        }
        this.mMirrorMatrix.setTranslate(f, 0.0f);
        this.mMirrorMatrix.preScale(-1.0f, 1.0f);
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mApplyGravity = true;
        Shader shader = this.mBitmapState.mPaint.getShader();
        if (shader != null) {
            if (needMirroring()) {
                updateMirrorMatrix(rect.right - rect.left);
                shader.setLocalMatrix(this.mMirrorMatrix);
            } else if (this.mMirrorMatrix != null) {
                this.mMirrorMatrix = null;
                shader.setLocalMatrix(Matrix.IDENTITY_MATRIX);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            BitmapState bitmapState = this.mBitmapState;
            if (bitmapState.mRebuildShader) {
                Shader.TileMode tileMode = bitmapState.mTileModeX;
                Shader.TileMode tileMode2 = bitmapState.mTileModeY;
                if (tileMode == null && tileMode2 == null) {
                    bitmapState.mPaint.setShader(null);
                } else {
                    Paint paint = bitmapState.mPaint;
                    if (tileMode == null) {
                        tileMode = Shader.TileMode.CLAMP;
                    }
                    if (tileMode2 == null) {
                        tileMode2 = Shader.TileMode.CLAMP;
                    }
                    paint.setShader(new BitmapShader(bitmap, tileMode, tileMode2));
                }
                bitmapState.mRebuildShader = false;
                copyBounds(this.mDstRect);
            }
            Shader shader = bitmapState.mPaint.getShader();
            boolean zNeedMirroring = needMirroring();
            if (shader == null) {
                if (this.mApplyGravity) {
                    Gravity.apply(bitmapState.mGravity, this.mBitmapWidth, this.mBitmapHeight, getBounds(), this.mDstRect, getLayoutDirection());
                    this.mApplyGravity = false;
                }
                if (zNeedMirroring) {
                    canvas.save();
                    canvas.translate(this.mDstRect.right - this.mDstRect.left, 0.0f);
                    canvas.scale(-1.0f, 1.0f);
                }
                canvas.drawBitmap(bitmap, (Rect) null, this.mDstRect, bitmapState.mPaint);
                if (zNeedMirroring) {
                    canvas.restore();
                    return;
                }
                return;
            }
            if (this.mApplyGravity) {
                copyBounds(this.mDstRect);
                this.mApplyGravity = false;
            }
            if (zNeedMirroring) {
                updateMirrorMatrix(this.mDstRect.right - this.mDstRect.left);
                shader.setLocalMatrix(this.mMirrorMatrix);
            } else if (this.mMirrorMatrix != null) {
                this.mMirrorMatrix = null;
                shader.setLocalMatrix(Matrix.IDENTITY_MATRIX);
            }
            canvas.drawRect(this.mDstRect, bitmapState.mPaint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (i != this.mBitmapState.mPaint.getAlpha()) {
            this.mBitmapState.mPaint.setAlpha(i);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mBitmapState.mPaint.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mBitmapState.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setXfermode(Xfermode xfermode) {
        this.mBitmapState.mPaint.setXfermode(xfermode);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mBitmapState = new BitmapState(this.mBitmapState);
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, attributeSet);
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.BitmapDrawable);
        int resourceId = typedArrayObtainAttributes.getResourceId(1, 0);
        if (resourceId == 0) {
            throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": <bitmap> requires a valid src attribute");
        }
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(resources, resourceId);
        if (bitmapDecodeResource == null) {
            throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": <bitmap> requires a valid src attribute");
        }
        this.mBitmapState.mBitmap = bitmapDecodeResource;
        setBitmap(bitmapDecodeResource);
        setTargetDensity(resources.getDisplayMetrics());
        setMipMap(typedArrayObtainAttributes.getBoolean(6, bitmapDecodeResource.hasMipMap()));
        setAutoMirrored(typedArrayObtainAttributes.getBoolean(7, false));
        Paint paint = this.mBitmapState.mPaint;
        paint.setAntiAlias(typedArrayObtainAttributes.getBoolean(2, paint.isAntiAlias()));
        paint.setFilterBitmap(typedArrayObtainAttributes.getBoolean(3, paint.isFilterBitmap()));
        paint.setDither(typedArrayObtainAttributes.getBoolean(4, paint.isDither()));
        setGravity(typedArrayObtainAttributes.getInt(0, 119));
        int i = typedArrayObtainAttributes.getInt(5, -1);
        if (i != -1) {
            if (i == 0) {
                setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            } else if (i == 1) {
                setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            } else if (i == 2) {
                setTileModeXY(Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
            }
        }
        typedArrayObtainAttributes.recycle();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        Bitmap bitmap;
        return (this.mBitmapState.mGravity == 119 && (bitmap = this.mBitmap) != null && !bitmap.hasAlpha() && this.mBitmapState.mPaint.getAlpha() >= 255) ? -1 : -3;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        this.mBitmapState.mChangingConfigurations = getChangingConfigurations();
        return this.mBitmapState;
    }

    static final class BitmapState extends Drawable.ConstantState {
        boolean mAutoMirrored;
        Bitmap mBitmap;
        int mChangingConfigurations;
        int mGravity;
        Paint mPaint;
        boolean mRebuildShader;
        int mTargetDensity;
        Shader.TileMode mTileModeX;
        Shader.TileMode mTileModeY;

        BitmapState(Bitmap bitmap) {
            this.mGravity = 119;
            this.mPaint = new Paint(6);
            this.mTileModeX = null;
            this.mTileModeY = null;
            this.mTargetDensity = 160;
            this.mBitmap = bitmap;
        }

        BitmapState(BitmapState bitmapState) {
            this(bitmapState.mBitmap);
            this.mChangingConfigurations = bitmapState.mChangingConfigurations;
            this.mGravity = bitmapState.mGravity;
            this.mTileModeX = bitmapState.mTileModeX;
            this.mTileModeY = bitmapState.mTileModeY;
            this.mTargetDensity = bitmapState.mTargetDensity;
            this.mPaint = new Paint(bitmapState.mPaint);
            this.mRebuildShader = bitmapState.mRebuildShader;
            this.mAutoMirrored = bitmapState.mAutoMirrored;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Bitmap getBitmap() {
            return this.mBitmap;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new BitmapDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new BitmapDrawable(this, resources);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }

    private BitmapDrawable(BitmapState bitmapState, Resources resources) {
        this.mDstRect = new Rect();
        this.mBitmapState = bitmapState;
        if (resources != null) {
            this.mTargetDensity = resources.getDisplayMetrics().densityDpi;
        } else {
            this.mTargetDensity = bitmapState.mTargetDensity;
        }
        setBitmap(bitmapState != null ? bitmapState.mBitmap : null);
    }
}
