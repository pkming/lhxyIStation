package android.graphics;

import android.text.GraphicsOperations;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import java.util.Locale;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class Paint {
    public static final int ANTI_ALIAS_FLAG = 1;
    public static final int AUTO_HINTING_TEXT_FLAG = 2048;
    public static final int BIDI_DEFAULT_LTR = 2;
    public static final int BIDI_DEFAULT_RTL = 3;
    private static final int BIDI_FLAG_MASK = 7;
    public static final int BIDI_FORCE_LTR = 4;
    public static final int BIDI_FORCE_RTL = 5;
    public static final int BIDI_LTR = 0;
    private static final int BIDI_MAX_FLAG_VALUE = 5;
    public static final int BIDI_RTL = 1;
    public static final int CURSOR_AFTER = 0;
    public static final int CURSOR_AT = 4;
    public static final int CURSOR_AT_OR_AFTER = 1;
    public static final int CURSOR_AT_OR_BEFORE = 3;
    public static final int CURSOR_BEFORE = 2;
    private static final int CURSOR_OPT_MAX_VALUE = 4;
    static final int DEFAULT_PAINT_FLAGS = 1280;
    public static final int DEV_KERN_TEXT_FLAG = 256;
    public static final int DIRECTION_LTR = 0;
    public static final int DIRECTION_RTL = 1;
    public static final int DITHER_FLAG = 4;
    public static final int EMBEDDED_BITMAP_TEXT_FLAG = 1024;
    public static final int FAKE_BOLD_TEXT_FLAG = 32;
    public static final int FILTER_BITMAP_FLAG = 2;
    public static final int HINTING_OFF = 0;
    public static final int HINTING_ON = 1;
    public static final int LCD_RENDER_TEXT_FLAG = 512;
    public static final int LINEAR_TEXT_FLAG = 64;
    public static final int STRIKE_THRU_TEXT_FLAG = 16;
    public static final int SUBPIXEL_TEXT_FLAG = 128;
    public static final int UNDERLINE_TEXT_FLAG = 8;
    public static final int VERTICAL_TEXT_FLAG = 4096;
    public boolean hasShadow;
    public int mBidiFlags;
    private ColorFilter mColorFilter;
    private float mCompatScaling;
    private boolean mHasCompatScaling;
    private float mInvCompatScaling;
    private Locale mLocale;
    private MaskFilter mMaskFilter;
    public int mNativePaint;
    private PathEffect mPathEffect;
    private Rasterizer mRasterizer;
    private Shader mShader;
    private Typeface mTypeface;
    private Xfermode mXfermode;
    public int shadowColor;
    public float shadowDx;
    public float shadowDy;
    public float shadowRadius;
    static final Style[] sStyleArray = {Style.FILL, Style.STROKE, Style.FILL_AND_STROKE};
    static final Cap[] sCapArray = {Cap.BUTT, Cap.ROUND, Cap.SQUARE};
    static final Join[] sJoinArray = {Join.MITER, Join.ROUND, Join.BEVEL};
    static final Align[] sAlignArray = {Align.LEFT, Align.CENTER, Align.RIGHT};

    public static class FontMetrics {
        public float ascent;
        public float bottom;
        public float descent;
        public float leading;
        public float top;
    }

    private static native void finalizer(int i);

    private native void nSetShadowLayer(float f, float f2, float f3, int i);

    private static native void nativeGetCharArrayBounds(int i, char[] cArr, int i2, int i3, int i4, Rect rect);

    private static native void nativeGetStringBounds(int i, String str, int i2, int i3, int i4, Rect rect);

    private native int native_breakText(String str, boolean z, float f, int i, float[] fArr);

    private native int native_breakText(char[] cArr, int i, int i2, float f, int i3, float[] fArr);

    private static native boolean native_getFillPath(int i, int i2, int i3);

    private static native int native_getStrokeCap(int i);

    private static native int native_getStrokeJoin(int i);

    private static native int native_getStyle(int i);

    private static native int native_getTextAlign(int i);

    private static native int native_getTextGlyphs(int i, String str, int i2, int i3, int i4, int i5, int i6, char[] cArr);

    private static native void native_getTextPath(int i, int i2, String str, int i3, int i4, float f, float f2, int i5);

    private static native void native_getTextPath(int i, int i2, char[] cArr, int i3, int i4, float f, float f2, int i5);

    private static native float native_getTextRunAdvances(int i, String str, int i2, int i3, int i4, int i5, int i6, float[] fArr, int i7);

    private static native float native_getTextRunAdvances(int i, char[] cArr, int i2, int i3, int i4, int i5, int i6, float[] fArr, int i7);

    private native int native_getTextRunCursor(int i, String str, int i2, int i3, int i4, int i5, int i6);

    private native int native_getTextRunCursor(int i, char[] cArr, int i2, int i3, int i4, int i5, int i6);

    private static native int native_getTextWidths(int i, String str, int i2, int i3, int i4, float[] fArr);

    private static native int native_getTextWidths(int i, char[] cArr, int i2, int i3, int i4, float[] fArr);

    private static native int native_init();

    private static native int native_initWithPaint(int i);

    private native float native_measureText(String str, int i);

    private native float native_measureText(String str, int i, int i2, int i3);

    private native float native_measureText(char[] cArr, int i, int i2, int i3);

    private static native void native_reset(int i);

    private static native void native_set(int i, int i2);

    private static native int native_setColorFilter(int i, int i2);

    private static native int native_setMaskFilter(int i, int i2);

    private static native int native_setPathEffect(int i, int i2);

    private static native int native_setRasterizer(int i, int i2);

    private static native int native_setShader(int i, int i2);

    private static native void native_setStrokeCap(int i, int i2);

    private static native void native_setStrokeJoin(int i, int i2);

    private static native void native_setStyle(int i, int i2);

    private static native void native_setTextAlign(int i, int i2);

    private static native void native_setTextLocale(int i, String str);

    private static native int native_setTypeface(int i, int i2);

    private static native int native_setXfermode(int i, int i2);

    public native float ascent();

    public native float descent();

    public native int getAlpha();

    public native int getColor();

    public native int getFlags();

    public native float getFontMetrics(FontMetrics fontMetrics);

    public native int getFontMetricsInt(FontMetricsInt fontMetricsInt);

    public native int getHinting();

    public native float getStrokeMiter();

    public native float getStrokeWidth();

    public native float getTextScaleX();

    public native float getTextSize();

    public native float getTextSkewX();

    public native void setAlpha(int i);

    public native void setAntiAlias(boolean z);

    public native void setColor(int i);

    public native void setDither(boolean z);

    public native void setFakeBoldText(boolean z);

    public native void setFilterBitmap(boolean z);

    public native void setFlags(int i);

    public native void setHinting(int i);

    public native void setLinearText(boolean z);

    public native void setStrikeThruText(boolean z);

    public native void setStrokeMiter(float f);

    public native void setStrokeWidth(float f);

    public native void setSubpixelText(boolean z);

    public native void setTextScaleX(float f);

    public native void setTextSize(float f);

    public native void setTextSkewX(float f);

    public native void setUnderlineText(boolean z);

    public enum Style {
        FILL(0),
        STROKE(1),
        FILL_AND_STROKE(2);

        final int nativeInt;

        Style(int i) {
            this.nativeInt = i;
        }
    }

    public enum Cap {
        BUTT(0),
        ROUND(1),
        SQUARE(2);

        final int nativeInt;

        Cap(int i) {
            this.nativeInt = i;
        }
    }

    public enum Join {
        MITER(0),
        ROUND(1),
        BEVEL(2);

        final int nativeInt;

        Join(int i) {
            this.nativeInt = i;
        }
    }

    public enum Align {
        LEFT(0),
        CENTER(1),
        RIGHT(2);

        final int nativeInt;

        Align(int i) {
            this.nativeInt = i;
        }
    }

    public Paint() {
        this(0);
    }

    public Paint(int i) {
        this.mBidiFlags = 2;
        this.mNativePaint = native_init();
        setFlags(i | 1280);
        this.mInvCompatScaling = 1.0f;
        this.mCompatScaling = 1.0f;
        setTextLocale(Locale.getDefault());
    }

    public Paint(Paint paint) {
        this.mBidiFlags = 2;
        this.mNativePaint = native_initWithPaint(paint.mNativePaint);
        setClassVariablesFrom(paint);
    }

    public void reset() {
        native_reset(this.mNativePaint);
        setFlags(1280);
        this.mColorFilter = null;
        this.mMaskFilter = null;
        this.mPathEffect = null;
        this.mRasterizer = null;
        this.mShader = null;
        this.mTypeface = null;
        this.mXfermode = null;
        this.mHasCompatScaling = false;
        this.mCompatScaling = 1.0f;
        this.mInvCompatScaling = 1.0f;
        this.hasShadow = false;
        this.shadowDx = 0.0f;
        this.shadowDy = 0.0f;
        this.shadowRadius = 0.0f;
        this.shadowColor = 0;
        this.mBidiFlags = 2;
        setTextLocale(Locale.getDefault());
    }

    public void set(Paint paint) {
        if (this != paint) {
            native_set(this.mNativePaint, paint.mNativePaint);
            setClassVariablesFrom(paint);
        }
    }

    private void setClassVariablesFrom(Paint paint) {
        this.mColorFilter = paint.mColorFilter;
        this.mMaskFilter = paint.mMaskFilter;
        this.mPathEffect = paint.mPathEffect;
        this.mRasterizer = paint.mRasterizer;
        Shader shader = paint.mShader;
        if (shader != null) {
            this.mShader = shader.copy();
        } else {
            this.mShader = null;
        }
        this.mTypeface = paint.mTypeface;
        this.mXfermode = paint.mXfermode;
        this.mHasCompatScaling = paint.mHasCompatScaling;
        this.mCompatScaling = paint.mCompatScaling;
        this.mInvCompatScaling = paint.mInvCompatScaling;
        this.hasShadow = paint.hasShadow;
        this.shadowDx = paint.shadowDx;
        this.shadowDy = paint.shadowDy;
        this.shadowRadius = paint.shadowRadius;
        this.shadowColor = paint.shadowColor;
        this.mBidiFlags = paint.mBidiFlags;
        this.mLocale = paint.mLocale;
    }

    public void setCompatibilityScaling(float f) {
        if (f == 1.0d) {
            this.mHasCompatScaling = false;
            this.mInvCompatScaling = 1.0f;
            this.mCompatScaling = 1.0f;
        } else {
            this.mHasCompatScaling = true;
            this.mCompatScaling = f;
            this.mInvCompatScaling = 1.0f / f;
        }
    }

    public int getBidiFlags() {
        return this.mBidiFlags;
    }

    public void setBidiFlags(int i) {
        int i2 = i & 7;
        if (i2 > 5) {
            throw new IllegalArgumentException("unknown bidi flag: " + i2);
        }
        this.mBidiFlags = i2;
    }

    public final boolean isAntiAlias() {
        return (getFlags() & 1) != 0;
    }

    public final boolean isDither() {
        return (getFlags() & 4) != 0;
    }

    public final boolean isLinearText() {
        return (getFlags() & 64) != 0;
    }

    public final boolean isSubpixelText() {
        return (getFlags() & 128) != 0;
    }

    public final boolean isUnderlineText() {
        return (getFlags() & 8) != 0;
    }

    public final boolean isStrikeThruText() {
        return (getFlags() & 16) != 0;
    }

    public final boolean isFakeBoldText() {
        return (getFlags() & 32) != 0;
    }

    public final boolean isFilterBitmap() {
        return (getFlags() & 2) != 0;
    }

    public Style getStyle() {
        return sStyleArray[native_getStyle(this.mNativePaint)];
    }

    public void setStyle(Style style) {
        native_setStyle(this.mNativePaint, style.nativeInt);
    }

    public void setARGB(int i, int i2, int i3, int i4) {
        setColor((i << 24) | (i2 << 16) | (i3 << 8) | i4);
    }

    public Cap getStrokeCap() {
        return sCapArray[native_getStrokeCap(this.mNativePaint)];
    }

    public void setStrokeCap(Cap cap) {
        native_setStrokeCap(this.mNativePaint, cap.nativeInt);
    }

    public Join getStrokeJoin() {
        return sJoinArray[native_getStrokeJoin(this.mNativePaint)];
    }

    public void setStrokeJoin(Join join) {
        native_setStrokeJoin(this.mNativePaint, join.nativeInt);
    }

    public boolean getFillPath(Path path, Path path2) {
        return native_getFillPath(this.mNativePaint, path.ni(), path2.ni());
    }

    public Shader getShader() {
        return this.mShader;
    }

    public Shader setShader(Shader shader) {
        native_setShader(this.mNativePaint, shader != null ? shader.native_instance : 0);
        this.mShader = shader;
        return shader;
    }

    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    public ColorFilter setColorFilter(ColorFilter colorFilter) {
        native_setColorFilter(this.mNativePaint, colorFilter != null ? colorFilter.native_instance : 0);
        this.mColorFilter = colorFilter;
        return colorFilter;
    }

    public Xfermode getXfermode() {
        return this.mXfermode;
    }

    public Xfermode setXfermode(Xfermode xfermode) {
        native_setXfermode(this.mNativePaint, xfermode != null ? xfermode.native_instance : 0);
        this.mXfermode = xfermode;
        return xfermode;
    }

    public PathEffect getPathEffect() {
        return this.mPathEffect;
    }

    public PathEffect setPathEffect(PathEffect pathEffect) {
        native_setPathEffect(this.mNativePaint, pathEffect != null ? pathEffect.native_instance : 0);
        this.mPathEffect = pathEffect;
        return pathEffect;
    }

    public MaskFilter getMaskFilter() {
        return this.mMaskFilter;
    }

    public MaskFilter setMaskFilter(MaskFilter maskFilter) {
        native_setMaskFilter(this.mNativePaint, maskFilter != null ? maskFilter.native_instance : 0);
        this.mMaskFilter = maskFilter;
        return maskFilter;
    }

    public Typeface getTypeface() {
        return this.mTypeface;
    }

    public Typeface setTypeface(Typeface typeface) {
        native_setTypeface(this.mNativePaint, typeface != null ? typeface.native_instance : 0);
        this.mTypeface = typeface;
        return typeface;
    }

    public Rasterizer getRasterizer() {
        return this.mRasterizer;
    }

    public Rasterizer setRasterizer(Rasterizer rasterizer) {
        native_setRasterizer(this.mNativePaint, rasterizer != null ? rasterizer.native_instance : 0);
        this.mRasterizer = rasterizer;
        return rasterizer;
    }

    public void setShadowLayer(float f, float f2, float f3, int i) {
        this.hasShadow = f > 0.0f;
        this.shadowRadius = f;
        this.shadowDx = f2;
        this.shadowDy = f3;
        this.shadowColor = i;
        nSetShadowLayer(f, f2, f3, i);
    }

    public void clearShadowLayer() {
        this.hasShadow = false;
        nSetShadowLayer(0.0f, 0.0f, 0.0f, 0);
    }

    public Align getTextAlign() {
        return sAlignArray[native_getTextAlign(this.mNativePaint)];
    }

    public void setTextAlign(Align align) {
        native_setTextAlign(this.mNativePaint, align.nativeInt);
    }

    public Locale getTextLocale() {
        return this.mLocale;
    }

    public void setTextLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null");
        }
        if (locale.equals(this.mLocale)) {
            return;
        }
        this.mLocale = locale;
        native_setTextLocale(this.mNativePaint, locale.toString());
    }

    public FontMetrics getFontMetrics() {
        FontMetrics fontMetrics = new FontMetrics();
        getFontMetrics(fontMetrics);
        return fontMetrics;
    }

    public static class FontMetricsInt {
        public int ascent;
        public int bottom;
        public int descent;
        public int leading;
        public int top;

        public String toString() {
            return "FontMetricsInt: top=" + this.top + " ascent=" + this.ascent + " descent=" + this.descent + " bottom=" + this.bottom + " leading=" + this.leading;
        }
    }

    public FontMetricsInt getFontMetricsInt() {
        FontMetricsInt fontMetricsInt = new FontMetricsInt();
        getFontMetricsInt(fontMetricsInt);
        return fontMetricsInt;
    }

    public float getFontSpacing() {
        return getFontMetrics(null);
    }

    public float measureText(char[] cArr, int i, int i2) {
        double dCeil;
        if (cArr == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((i | i2) < 0 || i + i2 > cArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (cArr.length == 0 || i2 == 0) {
            return 0.0f;
        }
        if (!this.mHasCompatScaling) {
            dCeil = Math.ceil(native_measureText(cArr, i, i2, this.mBidiFlags));
        } else {
            float textSize = getTextSize();
            setTextSize(this.mCompatScaling * textSize);
            float fNative_measureText = native_measureText(cArr, i, i2, this.mBidiFlags);
            setTextSize(textSize);
            dCeil = Math.ceil(fNative_measureText * this.mInvCompatScaling);
        }
        return (float) dCeil;
    }

    public float measureText(String str, int i, int i2) {
        double dCeil;
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((i | i2 | (i2 - i) | (str.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (str.length() == 0 || i == i2) {
            return 0.0f;
        }
        if (!this.mHasCompatScaling) {
            dCeil = Math.ceil(native_measureText(str, i, i2, this.mBidiFlags));
        } else {
            float textSize = getTextSize();
            setTextSize(this.mCompatScaling * textSize);
            float fNative_measureText = native_measureText(str, i, i2, this.mBidiFlags);
            setTextSize(textSize);
            dCeil = Math.ceil(fNative_measureText * this.mInvCompatScaling);
        }
        return (float) dCeil;
    }

    public float measureText(String str) {
        double dCeil;
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (str.length() == 0) {
            return 0.0f;
        }
        if (!this.mHasCompatScaling) {
            dCeil = Math.ceil(native_measureText(str, this.mBidiFlags));
        } else {
            float textSize = getTextSize();
            setTextSize(this.mCompatScaling * textSize);
            float fNative_measureText = native_measureText(str, this.mBidiFlags);
            setTextSize(textSize);
            dCeil = Math.ceil(fNative_measureText * this.mInvCompatScaling);
        }
        return (float) dCeil;
    }

    public float measureText(CharSequence charSequence, int i, int i2) {
        if (charSequence == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        int i3 = i2 - i;
        if ((i | i2 | i3 | (charSequence.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (charSequence.length() == 0 || i == i2) {
            return 0.0f;
        }
        if (charSequence instanceof String) {
            return measureText((String) charSequence, i, i2);
        }
        if ((charSequence instanceof SpannedString) || (charSequence instanceof SpannableString)) {
            return measureText(charSequence.toString(), i, i2);
        }
        if (charSequence instanceof GraphicsOperations) {
            return ((GraphicsOperations) charSequence).measureText(i, i2, this);
        }
        char[] cArrObtain = TemporaryBuffer.obtain(i3);
        TextUtils.getChars(charSequence, i, i2, cArrObtain, 0);
        float fMeasureText = measureText(cArrObtain, 0, i3);
        TemporaryBuffer.recycle(cArrObtain);
        return fMeasureText;
    }

    public int breakText(char[] cArr, int i, int i2, float f, float[] fArr) {
        if (cArr == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (i < 0 || cArr.length - i < Math.abs(i2)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (cArr.length == 0 || i2 == 0) {
            return 0;
        }
        if (!this.mHasCompatScaling) {
            return native_breakText(cArr, i, i2, f, this.mBidiFlags, fArr);
        }
        float textSize = getTextSize();
        setTextSize(this.mCompatScaling * textSize);
        int iNative_breakText = native_breakText(cArr, i, i2, f * this.mCompatScaling, this.mBidiFlags, fArr);
        setTextSize(textSize);
        if (fArr != null) {
            fArr[0] = fArr[0] * this.mInvCompatScaling;
        }
        return iNative_breakText;
    }

    public int breakText(CharSequence charSequence, int i, int i2, boolean z, float f, float[] fArr) {
        int iBreakText;
        if (charSequence == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        int i3 = i2 - i;
        if ((i | i2 | i3 | (charSequence.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (charSequence.length() == 0 || i == i2) {
            return 0;
        }
        if (i == 0 && (charSequence instanceof String) && i2 == charSequence.length()) {
            return breakText((String) charSequence, z, f, fArr);
        }
        char[] cArrObtain = TemporaryBuffer.obtain(i3);
        TextUtils.getChars(charSequence, i, i2, cArrObtain, 0);
        if (z) {
            iBreakText = breakText(cArrObtain, 0, i3, f, fArr);
        } else {
            iBreakText = breakText(cArrObtain, 0, -i3, f, fArr);
        }
        TemporaryBuffer.recycle(cArrObtain);
        return iBreakText;
    }

    public int breakText(String str, boolean z, float f, float[] fArr) {
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (str.length() == 0) {
            return 0;
        }
        if (!this.mHasCompatScaling) {
            return native_breakText(str, z, f, this.mBidiFlags, fArr);
        }
        float textSize = getTextSize();
        setTextSize(this.mCompatScaling * textSize);
        int iNative_breakText = native_breakText(str, z, f * this.mCompatScaling, this.mBidiFlags, fArr);
        setTextSize(textSize);
        if (fArr != null) {
            fArr[0] = fArr[0] * this.mInvCompatScaling;
        }
        return iNative_breakText;
    }

    public int getTextWidths(char[] cArr, int i, int i2, float[] fArr) {
        if (cArr == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((i | i2) < 0 || i + i2 > cArr.length || i2 > fArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (cArr.length == 0 || i2 == 0) {
            return 0;
        }
        if (!this.mHasCompatScaling) {
            return native_getTextWidths(this.mNativePaint, cArr, i, i2, this.mBidiFlags, fArr);
        }
        float textSize = getTextSize();
        setTextSize(this.mCompatScaling * textSize);
        int iNative_getTextWidths = native_getTextWidths(this.mNativePaint, cArr, i, i2, this.mBidiFlags, fArr);
        setTextSize(textSize);
        for (int i3 = 0; i3 < iNative_getTextWidths; i3++) {
            fArr[i3] = fArr[i3] * this.mInvCompatScaling;
        }
        return iNative_getTextWidths;
    }

    public int getTextWidths(CharSequence charSequence, int i, int i2, float[] fArr) {
        if (charSequence == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        int i3 = i2 - i;
        if ((i | i2 | i3 | (charSequence.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (i3 > fArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (charSequence.length() == 0 || i == i2) {
            return 0;
        }
        if (charSequence instanceof String) {
            return getTextWidths((String) charSequence, i, i2, fArr);
        }
        if ((charSequence instanceof SpannedString) || (charSequence instanceof SpannableString)) {
            return getTextWidths(charSequence.toString(), i, i2, fArr);
        }
        if (charSequence instanceof GraphicsOperations) {
            return ((GraphicsOperations) charSequence).getTextWidths(i, i2, fArr, this);
        }
        char[] cArrObtain = TemporaryBuffer.obtain(i3);
        TextUtils.getChars(charSequence, i, i2, cArrObtain, 0);
        int textWidths = getTextWidths(cArrObtain, 0, i3, fArr);
        TemporaryBuffer.recycle(cArrObtain);
        return textWidths;
    }

    public int getTextWidths(String str, int i, int i2, float[] fArr) {
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        int i3 = i2 - i;
        if ((i | i2 | i3 | (str.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (i3 > fArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (str.length() == 0 || i == i2) {
            return 0;
        }
        if (!this.mHasCompatScaling) {
            return native_getTextWidths(this.mNativePaint, str, i, i2, this.mBidiFlags, fArr);
        }
        float textSize = getTextSize();
        setTextSize(this.mCompatScaling * textSize);
        int iNative_getTextWidths = native_getTextWidths(this.mNativePaint, str, i, i2, this.mBidiFlags, fArr);
        setTextSize(textSize);
        for (int i4 = 0; i4 < iNative_getTextWidths; i4++) {
            fArr[i4] = fArr[i4] * this.mInvCompatScaling;
        }
        return iNative_getTextWidths;
    }

    public int getTextWidths(String str, float[] fArr) {
        return getTextWidths(str, 0, str.length(), fArr);
    }

    public int getTextGlyphs(String str, int i, int i2, int i3, int i4, int i5, char[] cArr) {
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (i5 != 0 && i5 != 1) {
            throw new IllegalArgumentException("unknown flags value: " + i5);
        }
        int i6 = i2 - i;
        if ((i | i2 | i3 | i4 | i6 | (i - i3) | (i4 - i2) | (str.length() - i2) | (str.length() - i4)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (i6 > cArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return native_getTextGlyphs(this.mNativePaint, str, i, i2, i3, i4, i5, cArr);
    }

    public float getTextRunAdvances(char[] cArr, int i, int i2, int i3, int i4, int i5, float[] fArr, int i6) {
        if (cArr == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (i5 != 0 && i5 != 1) {
            throw new IllegalArgumentException("unknown flags value: " + i5);
        }
        int i7 = i3 + i4;
        if ((i | i2 | i3 | i4 | i6 | (i - i3) | (i4 - i2) | (i7 - (i + i2)) | (cArr.length - i7) | (fArr == null ? 0 : fArr.length - (i6 + i2))) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (cArr.length == 0 || i2 == 0) {
            return 0.0f;
        }
        if (!this.mHasCompatScaling) {
            return native_getTextRunAdvances(this.mNativePaint, cArr, i, i2, i3, i4, i5, fArr, i6);
        }
        float textSize = getTextSize();
        setTextSize(this.mCompatScaling * textSize);
        float fNative_getTextRunAdvances = native_getTextRunAdvances(this.mNativePaint, cArr, i, i2, i3, i4, i5, fArr, i6);
        setTextSize(textSize);
        if (fArr != null) {
            int i8 = i6 + i2;
            for (int i9 = i6; i9 < i8; i9++) {
                fArr[i9] = fArr[i9] * this.mInvCompatScaling;
            }
        }
        return fNative_getTextRunAdvances * this.mInvCompatScaling;
    }

    public float getTextRunAdvances(CharSequence charSequence, int i, int i2, int i3, int i4, int i5, float[] fArr, int i6) {
        if (charSequence == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        int i7 = i2 - i;
        int i8 = i - i3;
        if ((i | i2 | i3 | i4 | i6 | i7 | i8 | (i4 - i2) | (charSequence.length() - i4) | (fArr == null ? 0 : (fArr.length - i6) - i7)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (charSequence instanceof String) {
            return getTextRunAdvances((String) charSequence, i, i2, i3, i4, i5, fArr, i6);
        }
        if ((charSequence instanceof SpannedString) || (charSequence instanceof SpannableString)) {
            return getTextRunAdvances(charSequence.toString(), i, i2, i3, i4, i5, fArr, i6);
        }
        if (charSequence instanceof GraphicsOperations) {
            return ((GraphicsOperations) charSequence).getTextRunAdvances(i, i2, i3, i4, i5, fArr, i6, this);
        }
        if (charSequence.length() == 0 || i2 == i) {
            return 0.0f;
        }
        int i9 = i4 - i3;
        char[] cArrObtain = TemporaryBuffer.obtain(i9);
        TextUtils.getChars(charSequence, i3, i4, cArrObtain, 0);
        float textRunAdvances = getTextRunAdvances(cArrObtain, i8, i7, 0, i9, i5, fArr, i6);
        TemporaryBuffer.recycle(cArrObtain);
        return textRunAdvances;
    }

    public float getTextRunAdvances(String str, int i, int i2, int i3, int i4, int i5, float[] fArr, int i6) {
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (i5 != 0 && i5 != 1) {
            throw new IllegalArgumentException("unknown flags value: " + i5);
        }
        int i7 = i2 - i;
        if ((i | i2 | i3 | i4 | i6 | i7 | (i - i3) | (i4 - i2) | (str.length() - i4) | (fArr == null ? 0 : (fArr.length - i6) - i7)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (str.length() == 0 || i == i2) {
            return 0.0f;
        }
        if (!this.mHasCompatScaling) {
            return native_getTextRunAdvances(this.mNativePaint, str, i, i2, i3, i4, i5, fArr, i6);
        }
        float textSize = getTextSize();
        setTextSize(this.mCompatScaling * textSize);
        float fNative_getTextRunAdvances = native_getTextRunAdvances(this.mNativePaint, str, i, i2, i3, i4, i5, fArr, i6);
        setTextSize(textSize);
        if (fArr != null) {
            int i8 = i6 + i7;
            for (int i9 = i6; i9 < i8; i9++) {
                fArr[i9] = fArr[i9] * this.mInvCompatScaling;
            }
        }
        return fNative_getTextRunAdvances * this.mInvCompatScaling;
    }

    public int getTextRunCursor(char[] cArr, int i, int i2, int i3, int i4, int i5) {
        int i6 = i + i2;
        if ((i | i6 | i4 | (i6 - i) | (i4 - i) | (i6 - i4) | (cArr.length - i6) | i5) < 0 || i5 > 4) {
            throw new IndexOutOfBoundsException();
        }
        return native_getTextRunCursor(this.mNativePaint, cArr, i, i2, i3, i4, i5);
    }

    public int getTextRunCursor(CharSequence charSequence, int i, int i2, int i3, int i4, int i5) {
        if ((charSequence instanceof String) || (charSequence instanceof SpannedString) || (charSequence instanceof SpannableString)) {
            return getTextRunCursor(charSequence.toString(), i, i2, i3, i4, i5);
        }
        if (charSequence instanceof GraphicsOperations) {
            return ((GraphicsOperations) charSequence).getTextRunCursor(i, i2, i3, i4, i5, this);
        }
        int i6 = i2 - i;
        char[] cArrObtain = TemporaryBuffer.obtain(i6);
        TextUtils.getChars(charSequence, i, i2, cArrObtain, 0);
        int textRunCursor = getTextRunCursor(cArrObtain, 0, i6, i3, i4 - i, i5);
        TemporaryBuffer.recycle(cArrObtain);
        return textRunCursor;
    }

    public int getTextRunCursor(String str, int i, int i2, int i3, int i4, int i5) {
        if ((i | i2 | i4 | (i2 - i) | (i4 - i) | (i2 - i4) | (str.length() - i2) | i5) < 0 || i5 > 4) {
            throw new IndexOutOfBoundsException();
        }
        return native_getTextRunCursor(this.mNativePaint, str, i, i2, i3, i4, i5);
    }

    public void getTextPath(char[] cArr, int i, int i2, float f, float f2, Path path) {
        if ((i | i2) < 0 || i + i2 > cArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        native_getTextPath(this.mNativePaint, this.mBidiFlags, cArr, i, i2, f, f2, path.ni());
    }

    public void getTextPath(String str, int i, int i2, float f, float f2, Path path) {
        if ((i | i2 | (i2 - i) | (str.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        native_getTextPath(this.mNativePaint, this.mBidiFlags, str, i, i2, f, f2, path.ni());
    }

    public void getTextBounds(String str, int i, int i2, Rect rect) {
        if ((i | i2 | (i2 - i) | (str.length() - i2)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        Objects.requireNonNull(rect, "need bounds Rect");
        nativeGetStringBounds(this.mNativePaint, str, i, i2, this.mBidiFlags, rect);
    }

    public void getTextBounds(char[] cArr, int i, int i2, Rect rect) {
        if ((i | i2) < 0 || i + i2 > cArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Objects.requireNonNull(rect, "need bounds Rect");
        nativeGetCharArrayBounds(this.mNativePaint, cArr, i, i2, this.mBidiFlags, rect);
    }

    protected void finalize() throws Throwable {
        try {
            finalizer(this.mNativePaint);
        } finally {
            super.finalize();
        }
    }
}
