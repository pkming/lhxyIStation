package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

/* JADX INFO: loaded from: classes.dex */
public class GradientDrawable extends Drawable {
    public static final int LINE = 2;
    public static final int LINEAR_GRADIENT = 0;
    public static final int OVAL = 1;
    public static final int RADIAL_GRADIENT = 1;
    public static final int RECTANGLE = 0;
    public static final int RING = 3;
    public static final int SWEEP_GRADIENT = 2;
    private int mAlpha;
    private ColorFilter mColorFilter;
    private boolean mDither;
    private final Paint mFillPaint;
    private GradientState mGradientState;
    private Paint mLayerPaint;
    private boolean mMutated;
    private Rect mPadding;
    private final Path mPath;
    private boolean mPathIsDirty;
    private final RectF mRect;
    private boolean mRectIsDirty;
    private Path mRingPath;
    private Paint mStrokePaint;

    public enum Orientation {
        TOP_BOTTOM,
        TR_BL,
        RIGHT_LEFT,
        BR_TL,
        BOTTOM_TOP,
        BL_TR,
        LEFT_RIGHT,
        TL_BR
    }

    /* synthetic */ GradientDrawable(GradientState gradientState, AnonymousClass1 anonymousClass1) {
        this(gradientState);
    }

    public GradientDrawable() {
        this(new GradientState(Orientation.TOP_BOTTOM, null));
    }

    public GradientDrawable(Orientation orientation, int[] iArr) {
        this(new GradientState(orientation, iArr));
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        Rect rect2 = this.mPadding;
        if (rect2 != null) {
            rect.set(rect2);
            return true;
        }
        return super.getPadding(rect);
    }

    public void setCornerRadii(float[] fArr) {
        this.mGradientState.setCornerRadii(fArr);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setCornerRadius(float f) {
        this.mGradientState.setCornerRadius(f);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setStroke(int i, int i2) {
        setStroke(i, i2, 0.0f, 0.0f);
    }

    public void setStroke(int i, int i2, float f, float f2) {
        this.mGradientState.setStroke(i, i2, f, f2);
        if (this.mStrokePaint == null) {
            Paint paint = new Paint(1);
            this.mStrokePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
        }
        this.mStrokePaint.setStrokeWidth(i);
        this.mStrokePaint.setColor(i2);
        this.mStrokePaint.setPathEffect(f > 0.0f ? new DashPathEffect(new float[]{f, f2}, 0.0f) : null);
        invalidateSelf();
    }

    public void setSize(int i, int i2) {
        this.mGradientState.setSize(i, i2);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setShape(int i) {
        this.mRingPath = null;
        this.mPathIsDirty = true;
        this.mGradientState.setShape(i);
        invalidateSelf();
    }

    public void setGradientType(int i) {
        this.mGradientState.setGradientType(i);
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    public void setGradientCenter(float f, float f2) {
        this.mGradientState.setGradientCenter(f, f2);
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    public void setGradientRadius(float f) {
        this.mGradientState.setGradientRadius(f);
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    public void setUseLevel(boolean z) {
        this.mGradientState.mUseLevel = z;
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    private int modulateAlpha(int i) {
        int i2 = this.mAlpha;
        return (i * (i2 + (i2 >> 7))) >> 8;
    }

    public Orientation getOrientation() {
        return this.mGradientState.mOrientation;
    }

    public void setOrientation(Orientation orientation) {
        this.mGradientState.mOrientation = orientation;
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    public void setColors(int[] iArr) {
        this.mGradientState.setColors(iArr);
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int i;
        Paint paint;
        if (ensureValidRect()) {
            int alpha = this.mFillPaint.getAlpha();
            Paint paint2 = this.mStrokePaint;
            int alpha2 = paint2 != null ? paint2.getAlpha() : 0;
            int iModulateAlpha = modulateAlpha(alpha);
            int iModulateAlpha2 = modulateAlpha(alpha2);
            boolean z = iModulateAlpha2 > 0 && (paint = this.mStrokePaint) != null && paint.getStrokeWidth() > 0.0f;
            boolean z2 = iModulateAlpha > 0;
            GradientState gradientState = this.mGradientState;
            boolean z3 = z && z2 && gradientState.mShape != 2 && iModulateAlpha2 < 255 && (this.mAlpha < 255 || this.mColorFilter != null);
            if (z3) {
                if (this.mLayerPaint == null) {
                    this.mLayerPaint = new Paint();
                }
                this.mLayerPaint.setDither(this.mDither);
                this.mLayerPaint.setAlpha(this.mAlpha);
                this.mLayerPaint.setColorFilter(this.mColorFilter);
                float strokeWidth = this.mStrokePaint.getStrokeWidth();
                i = 2;
                canvas.saveLayer(this.mRect.left - strokeWidth, this.mRect.top - strokeWidth, this.mRect.right + strokeWidth, this.mRect.bottom + strokeWidth, this.mLayerPaint, 4);
                this.mFillPaint.setColorFilter(null);
                this.mStrokePaint.setColorFilter(null);
            } else {
                i = 2;
                this.mFillPaint.setAlpha(iModulateAlpha);
                this.mFillPaint.setDither(this.mDither);
                this.mFillPaint.setColorFilter(this.mColorFilter);
                if (this.mColorFilter != null && !this.mGradientState.mHasSolidColor) {
                    this.mFillPaint.setColor(this.mAlpha << 24);
                }
                if (z) {
                    this.mStrokePaint.setAlpha(iModulateAlpha2);
                    this.mStrokePaint.setDither(this.mDither);
                    this.mStrokePaint.setColorFilter(this.mColorFilter);
                }
            }
            int i2 = gradientState.mShape;
            if (i2 != 0) {
                if (i2 == 1) {
                    canvas.drawOval(this.mRect, this.mFillPaint);
                    if (z) {
                        canvas.drawOval(this.mRect, this.mStrokePaint);
                    }
                } else if (i2 == i) {
                    RectF rectF = this.mRect;
                    float fCenterY = rectF.centerY();
                    canvas.drawLine(rectF.left, fCenterY, rectF.right, fCenterY, this.mStrokePaint);
                } else if (i2 == 3) {
                    Path pathBuildRing = buildRing(gradientState);
                    canvas.drawPath(pathBuildRing, this.mFillPaint);
                    if (z) {
                        canvas.drawPath(pathBuildRing, this.mStrokePaint);
                    }
                }
            } else if (gradientState.mRadiusArray != null) {
                if (this.mPathIsDirty || this.mRectIsDirty) {
                    this.mPath.reset();
                    this.mPath.addRoundRect(this.mRect, gradientState.mRadiusArray, Path.Direction.CW);
                    this.mRectIsDirty = false;
                    this.mPathIsDirty = false;
                }
                canvas.drawPath(this.mPath, this.mFillPaint);
                if (z) {
                    canvas.drawPath(this.mPath, this.mStrokePaint);
                }
            } else if (gradientState.mRadius > 0.0f) {
                float f = gradientState.mRadius;
                float fMin = Math.min(this.mRect.width(), this.mRect.height()) * 0.5f;
                if (f > fMin) {
                    f = fMin;
                }
                canvas.drawRoundRect(this.mRect, f, f, this.mFillPaint);
                if (z) {
                    canvas.drawRoundRect(this.mRect, f, f, this.mStrokePaint);
                }
            } else {
                if (this.mFillPaint.getColor() != 0 || this.mColorFilter != null || this.mFillPaint.getShader() != null) {
                    canvas.drawRect(this.mRect, this.mFillPaint);
                }
                if (z) {
                    canvas.drawRect(this.mRect, this.mStrokePaint);
                }
            }
            if (z3) {
                canvas.restore();
                return;
            }
            this.mFillPaint.setAlpha(alpha);
            if (z) {
                this.mStrokePaint.setAlpha(alpha2);
            }
        }
    }

    private Path buildRing(GradientState gradientState) {
        if (this.mRingPath != null && (!gradientState.mUseLevelForShape || !this.mPathIsDirty)) {
            return this.mRingPath;
        }
        this.mPathIsDirty = false;
        float level = gradientState.mUseLevelForShape ? (getLevel() * 360.0f) / 10000.0f : 360.0f;
        RectF rectF = new RectF(this.mRect);
        float fWidth = rectF.width() / 2.0f;
        float fHeight = rectF.height() / 2.0f;
        float fWidth2 = gradientState.mThickness != -1 ? gradientState.mThickness : rectF.width() / gradientState.mThicknessRatio;
        float fWidth3 = gradientState.mInnerRadius != -1 ? gradientState.mInnerRadius : rectF.width() / gradientState.mInnerRadiusRatio;
        RectF rectF2 = new RectF(rectF);
        rectF2.inset(fWidth - fWidth3, fHeight - fWidth3);
        RectF rectF3 = new RectF(rectF2);
        float f = -fWidth2;
        rectF3.inset(f, f);
        Path path = this.mRingPath;
        if (path == null) {
            this.mRingPath = new Path();
        } else {
            path.reset();
        }
        Path path2 = this.mRingPath;
        if (level < 360.0f && level > -360.0f) {
            path2.setFillType(Path.FillType.EVEN_ODD);
            float f2 = fWidth + fWidth3;
            path2.moveTo(f2, fHeight);
            path2.lineTo(f2 + fWidth2, fHeight);
            path2.arcTo(rectF3, 0.0f, level, false);
            path2.arcTo(rectF2, level, -level, false);
            path2.close();
        } else {
            path2.addOval(rectF3, Path.Direction.CW);
            path2.addOval(rectF2, Path.Direction.CCW);
        }
        return path2;
    }

    public void setColor(int i) {
        this.mGradientState.setSolidColor(i);
        this.mFillPaint.setColor(i);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mGradientState.mChangingConfigurations;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (i != this.mAlpha) {
            this.mAlpha = i;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean z) {
        if (z != this.mDither) {
            this.mDither = z;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter != this.mColorFilter) {
            this.mColorFilter = colorFilter;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mGradientState.mOpaque ? -1 : -3;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mRingPath = null;
        this.mPathIsDirty = true;
        this.mRectIsDirty = true;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        super.onLevelChange(i);
        this.mRectIsDirty = true;
        this.mPathIsDirty = true;
        invalidateSelf();
        return true;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:15:0x0057. Please report as an issue. */
    private boolean ensureValidRect() {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        float f12;
        float f13;
        float f14;
        float f15;
        if (this.mRectIsDirty) {
            this.mRectIsDirty = false;
            Rect bounds = getBounds();
            Paint paint = this.mStrokePaint;
            float strokeWidth = paint != null ? paint.getStrokeWidth() * 0.5f : 0.0f;
            GradientState gradientState = this.mGradientState;
            this.mRect.set(bounds.left + strokeWidth, bounds.top + strokeWidth, bounds.right - strokeWidth, bounds.bottom - strokeWidth);
            int[] iArr = gradientState.mColors;
            if (iArr != null) {
                RectF rectF = this.mRect;
                if (gradientState.mGradient != 0) {
                    if (gradientState.mGradient != 1) {
                        if (gradientState.mGradient == 2) {
                            float f16 = rectF.left + ((rectF.right - rectF.left) * gradientState.mCenterX);
                            float f17 = rectF.top + ((rectF.bottom - rectF.top) * gradientState.mCenterY);
                            float[] fArr = null;
                            if (gradientState.mUseLevel) {
                                int[] iArr2 = gradientState.mTempColors;
                                int length = iArr.length;
                                if (iArr2 == null || iArr2.length != length + 1) {
                                    iArr2 = new int[length + 1];
                                    gradientState.mTempColors = iArr2;
                                }
                                System.arraycopy(iArr, 0, iArr2, 0, length);
                                int i = length - 1;
                                iArr2[length] = iArr[i];
                                float[] fArr2 = gradientState.mTempPositions;
                                float f18 = 1.0f / i;
                                if (fArr2 == null || fArr2.length != length + 1) {
                                    fArr2 = new float[length + 1];
                                    gradientState.mTempPositions = fArr2;
                                }
                                float level = getLevel() / 10000.0f;
                                for (int i2 = 0; i2 < length; i2++) {
                                    fArr2[i2] = i2 * f18 * level;
                                }
                                fArr2[length] = 1.0f;
                                int[] iArr3 = iArr2;
                                fArr = fArr2;
                                iArr = iArr3;
                            }
                            this.mFillPaint.setShader(new SweepGradient(f16, f17, iArr, fArr));
                        }
                    } else {
                        this.mFillPaint.setShader(new RadialGradient(rectF.left + ((rectF.right - rectF.left) * gradientState.mCenterX), rectF.top + ((rectF.bottom - rectF.top) * gradientState.mCenterY), (gradientState.mUseLevel ? getLevel() / 10000.0f : 1.0f) * gradientState.mGradientRadius, iArr, (float[]) null, Shader.TileMode.CLAMP));
                    }
                } else {
                    float level2 = gradientState.mUseLevel ? getLevel() / 10000.0f : 1.0f;
                    switch (AnonymousClass1.$SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[gradientState.mOrientation.ordinal()]) {
                        case 1:
                            f = rectF.left;
                            f2 = rectF.top;
                            f3 = rectF.bottom;
                            f11 = level2 * f3;
                            f12 = f;
                            f13 = f12;
                            f14 = f2;
                            f15 = f11;
                            break;
                        case 2:
                            f4 = rectF.right;
                            f5 = rectF.top;
                            f6 = rectF.left * level2;
                            f7 = rectF.bottom;
                            f11 = level2 * f7;
                            f12 = f4;
                            f14 = f5;
                            f13 = f6;
                            f15 = f11;
                            break;
                        case 3:
                            f8 = rectF.right;
                            f9 = rectF.top;
                            f10 = rectF.left;
                            f12 = f8;
                            f14 = f9;
                            f15 = f14;
                            f13 = level2 * f10;
                            break;
                        case 4:
                            f4 = rectF.right;
                            f5 = rectF.bottom;
                            f6 = rectF.left * level2;
                            f7 = rectF.top;
                            f11 = level2 * f7;
                            f12 = f4;
                            f14 = f5;
                            f13 = f6;
                            f15 = f11;
                            break;
                        case 5:
                            f = rectF.left;
                            f2 = rectF.bottom;
                            f3 = rectF.top;
                            f11 = level2 * f3;
                            f12 = f;
                            f13 = f12;
                            f14 = f2;
                            f15 = f11;
                            break;
                        case 6:
                            f4 = rectF.left;
                            f5 = rectF.bottom;
                            f6 = rectF.right * level2;
                            f7 = rectF.top;
                            f11 = level2 * f7;
                            f12 = f4;
                            f14 = f5;
                            f13 = f6;
                            f15 = f11;
                            break;
                        case 7:
                            f8 = rectF.left;
                            f9 = rectF.top;
                            f10 = rectF.right;
                            f12 = f8;
                            f14 = f9;
                            f15 = f14;
                            f13 = level2 * f10;
                            break;
                        default:
                            f4 = rectF.left;
                            f5 = rectF.top;
                            f6 = rectF.right * level2;
                            f7 = rectF.bottom;
                            f11 = level2 * f7;
                            f12 = f4;
                            f14 = f5;
                            f13 = f6;
                            f15 = f11;
                            break;
                    }
                    this.mFillPaint.setShader(new LinearGradient(f12, f14, f13, f15, iArr, gradientState.mPositions, Shader.TileMode.CLAMP));
                }
                if (!gradientState.mHasSolidColor) {
                    this.mFillPaint.setColor(-16777216);
                }
            }
        }
        return !this.mRect.isEmpty();
    }

    /* JADX INFO: renamed from: android.graphics.drawable.GradientDrawable$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;

        static {
            int[] iArr = new int[Orientation.values().length];
            $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation = iArr;
            try {
                iArr[Orientation.TOP_BOTTOM.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.TR_BL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.RIGHT_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.BR_TL.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.BOTTOM_TOP.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.BL_TR.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.LEFT_RIGHT.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:108:0x02ec, code lost:
    
        r18.mGradientState.computeOpacity();
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x02f1, code lost:
    
        return;
     */
    /* JADX WARN: Removed duplicated region for block: B:72:0x017b  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01b3  */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void inflate(android.content.res.Resources r19, org.xmlpull.v1.XmlPullParser r20, android.util.AttributeSet r21) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 754
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.GradientDrawable.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):void");
    }

    private static float getFloatOrFraction(TypedArray typedArray, int i, float f) {
        TypedValue typedValuePeekValue = typedArray.peekValue(i);
        if (typedValuePeekValue != null) {
            return typedValuePeekValue.type == 6 ? typedValuePeekValue.getFraction(1.0f, 1.0f) : typedValuePeekValue.getFloat();
        }
        return f;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mGradientState.mWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mGradientState.mHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.mGradientState.mChangingConfigurations = getChangingConfigurations();
        return this.mGradientState;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            GradientState gradientState = new GradientState(this.mGradientState);
            this.mGradientState = gradientState;
            initializeWithState(gradientState);
            this.mMutated = true;
        }
        return this;
    }

    static final class GradientState extends Drawable.ConstantState {
        private float mCenterX;
        private float mCenterY;
        public int mChangingConfigurations;
        public int[] mColors;
        public int mGradient;
        private float mGradientRadius;
        public boolean mHasSolidColor;
        public int mHeight;
        public int mInnerRadius;
        public float mInnerRadiusRatio;
        private boolean mOpaque;
        public Orientation mOrientation;
        public Rect mPadding;
        public float[] mPositions;
        public float mRadius;
        public float[] mRadiusArray;
        public int mShape;
        public int mSolidColor;
        public int mStrokeColor;
        public float mStrokeDashGap;
        public float mStrokeDashWidth;
        public int mStrokeWidth;
        public int[] mTempColors;
        public float[] mTempPositions;
        public int mThickness;
        public float mThicknessRatio;
        private boolean mUseLevel;
        private boolean mUseLevelForShape;
        public int mWidth;

        private static boolean isOpaque(int i) {
            return ((i >> 24) & 255) == 255;
        }

        GradientState(Orientation orientation, int[] iArr) {
            this.mShape = 0;
            this.mGradient = 0;
            this.mStrokeWidth = -1;
            this.mWidth = -1;
            this.mHeight = -1;
            this.mCenterX = 0.5f;
            this.mCenterY = 0.5f;
            this.mGradientRadius = 0.5f;
            this.mOrientation = orientation;
            setColors(iArr);
        }

        public GradientState(GradientState gradientState) {
            this.mShape = 0;
            this.mGradient = 0;
            this.mStrokeWidth = -1;
            this.mWidth = -1;
            this.mHeight = -1;
            this.mCenterX = 0.5f;
            this.mCenterY = 0.5f;
            this.mGradientRadius = 0.5f;
            this.mChangingConfigurations = gradientState.mChangingConfigurations;
            this.mShape = gradientState.mShape;
            this.mGradient = gradientState.mGradient;
            this.mOrientation = gradientState.mOrientation;
            int[] iArr = gradientState.mColors;
            if (iArr != null) {
                this.mColors = (int[]) iArr.clone();
            }
            float[] fArr = gradientState.mPositions;
            if (fArr != null) {
                this.mPositions = (float[]) fArr.clone();
            }
            this.mHasSolidColor = gradientState.mHasSolidColor;
            this.mSolidColor = gradientState.mSolidColor;
            this.mStrokeWidth = gradientState.mStrokeWidth;
            this.mStrokeColor = gradientState.mStrokeColor;
            this.mStrokeDashWidth = gradientState.mStrokeDashWidth;
            this.mStrokeDashGap = gradientState.mStrokeDashGap;
            this.mRadius = gradientState.mRadius;
            float[] fArr2 = gradientState.mRadiusArray;
            if (fArr2 != null) {
                this.mRadiusArray = (float[]) fArr2.clone();
            }
            if (gradientState.mPadding != null) {
                this.mPadding = new Rect(gradientState.mPadding);
            }
            this.mWidth = gradientState.mWidth;
            this.mHeight = gradientState.mHeight;
            this.mInnerRadiusRatio = gradientState.mInnerRadiusRatio;
            this.mThicknessRatio = gradientState.mThicknessRatio;
            this.mInnerRadius = gradientState.mInnerRadius;
            this.mThickness = gradientState.mThickness;
            this.mCenterX = gradientState.mCenterX;
            this.mCenterY = gradientState.mCenterY;
            this.mGradientRadius = gradientState.mGradientRadius;
            this.mUseLevel = gradientState.mUseLevel;
            this.mUseLevelForShape = gradientState.mUseLevelForShape;
            this.mOpaque = gradientState.mOpaque;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new GradientDrawable(this, (AnonymousClass1) null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new GradientDrawable(this, (AnonymousClass1) null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public void setShape(int i) {
            this.mShape = i;
            computeOpacity();
        }

        public void setGradientType(int i) {
            this.mGradient = i;
        }

        public void setGradientCenter(float f, float f2) {
            this.mCenterX = f;
            this.mCenterY = f2;
        }

        public void setColors(int[] iArr) {
            this.mHasSolidColor = false;
            this.mColors = iArr;
            computeOpacity();
        }

        public void setSolidColor(int i) {
            this.mHasSolidColor = true;
            this.mSolidColor = i;
            this.mColors = null;
            computeOpacity();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void computeOpacity() {
            if (this.mShape != 0) {
                this.mOpaque = false;
                return;
            }
            if (this.mRadius > 0.0f || this.mRadiusArray != null) {
                this.mOpaque = false;
                return;
            }
            if (this.mStrokeWidth > 0 && !isOpaque(this.mStrokeColor)) {
                this.mOpaque = false;
                return;
            }
            if (this.mHasSolidColor) {
                this.mOpaque = isOpaque(this.mSolidColor);
                return;
            }
            if (this.mColors != null) {
                int i = 0;
                while (true) {
                    int[] iArr = this.mColors;
                    if (i >= iArr.length) {
                        break;
                    }
                    if (!isOpaque(iArr[i])) {
                        this.mOpaque = false;
                        return;
                    }
                    i++;
                }
            }
            this.mOpaque = true;
        }

        public void setStroke(int i, int i2) {
            this.mStrokeWidth = i;
            this.mStrokeColor = i2;
            computeOpacity();
        }

        public void setStroke(int i, int i2, float f, float f2) {
            this.mStrokeWidth = i;
            this.mStrokeColor = i2;
            this.mStrokeDashWidth = f;
            this.mStrokeDashGap = f2;
            computeOpacity();
        }

        public void setCornerRadius(float f) {
            if (f < 0.0f) {
                f = 0.0f;
            }
            this.mRadius = f;
            this.mRadiusArray = null;
        }

        public void setCornerRadii(float[] fArr) {
            this.mRadiusArray = fArr;
            if (fArr == null) {
                this.mRadius = 0.0f;
            }
        }

        public void setSize(int i, int i2) {
            this.mWidth = i;
            this.mHeight = i2;
        }

        public void setGradientRadius(float f) {
            this.mGradientRadius = f;
        }
    }

    private GradientDrawable(GradientState gradientState) {
        this.mFillPaint = new Paint(1);
        this.mAlpha = 255;
        this.mPath = new Path();
        this.mRect = new RectF();
        this.mPathIsDirty = true;
        this.mGradientState = gradientState;
        initializeWithState(gradientState);
        this.mRectIsDirty = true;
        this.mMutated = false;
    }

    private void initializeWithState(GradientState gradientState) {
        if (gradientState.mHasSolidColor) {
            this.mFillPaint.setColor(gradientState.mSolidColor);
        } else if (gradientState.mColors == null) {
            this.mFillPaint.setColor(0);
        } else {
            this.mFillPaint.setColor(-16777216);
        }
        this.mPadding = gradientState.mPadding;
        if (gradientState.mStrokeWidth >= 0) {
            Paint paint = new Paint(1);
            this.mStrokePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.mStrokePaint.setStrokeWidth(gradientState.mStrokeWidth);
            this.mStrokePaint.setColor(gradientState.mStrokeColor);
            if (gradientState.mStrokeDashWidth != 0.0f) {
                this.mStrokePaint.setPathEffect(new DashPathEffect(new float[]{gradientState.mStrokeDashWidth, gradientState.mStrokeDashGap}, 0.0f));
            }
        }
    }
}
