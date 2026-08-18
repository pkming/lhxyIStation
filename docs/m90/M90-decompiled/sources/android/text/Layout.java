package android.text;

import android.emoji.EmojiFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.text.style.AlignmentSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;
import android.text.style.TabStopSpan;
import com.android.internal.util.ArrayUtils;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public abstract class Layout {
    static final Directions DIRS_ALL_LEFT_TO_RIGHT;
    static final Directions DIRS_ALL_RIGHT_TO_LEFT;
    public static final int DIR_LEFT_TO_RIGHT = 1;
    static final int DIR_REQUEST_DEFAULT_LTR = 2;
    static final int DIR_REQUEST_DEFAULT_RTL = -2;
    static final int DIR_REQUEST_LTR = 1;
    static final int DIR_REQUEST_RTL = -1;
    public static final int DIR_RIGHT_TO_LEFT = -1;
    static final char[] ELLIPSIS_NORMAL;
    static final char[] ELLIPSIS_TWO_DOTS;
    static final EmojiFactory EMOJI_FACTORY;
    static final int MAX_EMOJI;
    static final int MIN_EMOJI;
    private static final ParagraphStyle[] NO_PARA_SPANS = (ParagraphStyle[]) ArrayUtils.emptyArray(ParagraphStyle.class);
    static final int RUN_LENGTH_MASK = 67108863;
    static final int RUN_LEVEL_MASK = 63;
    static final int RUN_LEVEL_SHIFT = 26;
    static final int RUN_RTL_FLAG = 67108864;
    private static final int TAB_INCREMENT = 20;
    private static final Rect sTempRect;
    private Alignment mAlignment;
    private SpanSet<LineBackgroundSpan> mLineBackgroundSpans;
    private TextPaint mPaint;
    private float mSpacingAdd;
    private float mSpacingMult;
    private boolean mSpannedText;
    private CharSequence mText;
    private TextDirectionHeuristic mTextDir;
    private int mWidth;
    TextPaint mWorkPaint;

    public enum Alignment {
        ALIGN_NORMAL,
        ALIGN_OPPOSITE,
        ALIGN_CENTER,
        ALIGN_LEFT,
        ALIGN_RIGHT
    }

    public abstract int getBottomPadding();

    public abstract int getEllipsisCount(int i);

    public abstract int getEllipsisStart(int i);

    public abstract boolean getLineContainsTab(int i);

    public abstract int getLineCount();

    public abstract int getLineDescent(int i);

    public abstract Directions getLineDirections(int i);

    public abstract int getLineStart(int i);

    public abstract int getLineTop(int i);

    public abstract int getParagraphDirection(int i);

    public abstract int getTopPadding();

    static {
        EmojiFactory emojiFactoryNewAvailableInstance = EmojiFactory.newAvailableInstance();
        EMOJI_FACTORY = emojiFactoryNewAvailableInstance;
        if (emojiFactoryNewAvailableInstance != null) {
            MIN_EMOJI = emojiFactoryNewAvailableInstance.getMinimumAndroidPua();
            MAX_EMOJI = emojiFactoryNewAvailableInstance.getMaximumAndroidPua();
        } else {
            MIN_EMOJI = -1;
            MAX_EMOJI = -1;
        }
        sTempRect = new Rect();
        DIRS_ALL_LEFT_TO_RIGHT = new Directions(new int[]{0, RUN_LENGTH_MASK});
        DIRS_ALL_RIGHT_TO_LEFT = new Directions(new int[]{0, 134217727});
        ELLIPSIS_NORMAL = new char[]{8230};
        ELLIPSIS_TWO_DOTS = new char[]{8229};
    }

    public static float getDesiredWidth(CharSequence charSequence, TextPaint textPaint) {
        return getDesiredWidth(charSequence, 0, charSequence.length(), textPaint);
    }

    public static float getDesiredWidth(CharSequence charSequence, int i, int i2, TextPaint textPaint) {
        float f = 0.0f;
        while (i <= i2) {
            int iIndexOf = TextUtils.indexOf(charSequence, '\n', i, i2);
            if (iIndexOf < 0) {
                iIndexOf = i2;
            }
            float fMeasurePara = measurePara(textPaint, charSequence, i, iIndexOf);
            if (fMeasurePara > f) {
                f = fMeasurePara;
            }
            i = iIndexOf + 1;
        }
        return f;
    }

    protected Layout(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, float f, float f2) {
        this(charSequence, textPaint, i, alignment, TextDirectionHeuristics.FIRSTSTRONG_LTR, f, f2);
    }

    protected Layout(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, TextDirectionHeuristic textDirectionHeuristic, float f, float f2) {
        this.mAlignment = Alignment.ALIGN_NORMAL;
        if (i < 0) {
            throw new IllegalArgumentException("Layout: " + i + " < 0");
        }
        if (textPaint != null) {
            textPaint.bgColor = 0;
            textPaint.baselineShift = 0;
        }
        this.mText = charSequence;
        this.mPaint = textPaint;
        this.mWorkPaint = new TextPaint();
        this.mWidth = i;
        this.mAlignment = alignment;
        this.mSpacingMult = f;
        this.mSpacingAdd = f2;
        this.mSpannedText = charSequence instanceof Spanned;
        this.mTextDir = textDirectionHeuristic;
    }

    void replaceWith(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, float f, float f2) {
        if (i < 0) {
            throw new IllegalArgumentException("Layout: " + i + " < 0");
        }
        this.mText = charSequence;
        this.mPaint = textPaint;
        this.mWidth = i;
        this.mAlignment = alignment;
        this.mSpacingMult = f;
        this.mSpacingAdd = f2;
        this.mSpannedText = charSequence instanceof Spanned;
    }

    public void draw(Canvas canvas) {
        draw(canvas, null, null, 0);
    }

    public void draw(Canvas canvas, Path path, Paint paint, int i) {
        long lineRangeForDraw = getLineRangeForDraw(canvas);
        int iUnpackRangeStartFromLong = TextUtils.unpackRangeStartFromLong(lineRangeForDraw);
        int iUnpackRangeEndFromLong = TextUtils.unpackRangeEndFromLong(lineRangeForDraw);
        if (iUnpackRangeEndFromLong < 0) {
            return;
        }
        drawBackground(canvas, path, paint, i, iUnpackRangeStartFromLong, iUnpackRangeEndFromLong);
        drawText(canvas, iUnpackRangeStartFromLong, iUnpackRangeEndFromLong);
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x00a3  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00b1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawText(android.graphics.Canvas r39, int r40, int r41) {
        /*
            Method dump skipped, instruction units count: 641
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.Layout.drawText(android.graphics.Canvas, int, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x00bf A[LOOP:1: B:37:0x00bd->B:38:0x00bf, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawBackground(android.graphics.Canvas r32, android.graphics.Path r33, android.graphics.Paint r34, int r35, int r36, int r37) {
        /*
            Method dump skipped, instruction units count: 309
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.Layout.drawBackground(android.graphics.Canvas, android.graphics.Path, android.graphics.Paint, int, int, int):void");
    }

    public long getLineRangeForDraw(Canvas canvas) {
        Rect rect = sTempRect;
        synchronized (rect) {
            if (!canvas.getClipBounds(rect)) {
                return TextUtils.packRangeInLong(0, -1);
            }
            int i = rect.top;
            int i2 = rect.bottom;
            int iMax = Math.max(i, 0);
            int iMin = Math.min(getLineTop(getLineCount()), i2);
            if (iMax >= iMin) {
                return TextUtils.packRangeInLong(0, -1);
            }
            return TextUtils.packRangeInLong(getLineForVertical(iMax), getLineForVertical(iMin));
        }
    }

    private int getLineStartPos(int i, int i2, int i3) {
        Alignment paragraphAlignment = getParagraphAlignment(i);
        int paragraphDirection = getParagraphDirection(i);
        if (paragraphAlignment == Alignment.ALIGN_LEFT) {
            paragraphAlignment = paragraphDirection == 1 ? Alignment.ALIGN_NORMAL : Alignment.ALIGN_OPPOSITE;
        } else if (paragraphAlignment == Alignment.ALIGN_RIGHT) {
            paragraphAlignment = paragraphDirection == 1 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_NORMAL;
        }
        if (paragraphAlignment == Alignment.ALIGN_NORMAL) {
            return paragraphDirection == 1 ? i2 : i3;
        }
        TabStops tabStops = null;
        if (this.mSpannedText && getLineContainsTab(i)) {
            Spanned spanned = (Spanned) this.mText;
            int lineStart = getLineStart(i);
            TabStopSpan[] tabStopSpanArr = (TabStopSpan[]) getParagraphSpans(spanned, lineStart, spanned.nextSpanTransition(lineStart, spanned.length(), TabStopSpan.class), TabStopSpan.class);
            if (tabStopSpanArr.length > 0) {
                tabStops = new TabStops(20, tabStopSpanArr);
            }
        }
        int lineExtent = (int) getLineExtent(i, tabStops, false);
        if (paragraphAlignment == Alignment.ALIGN_OPPOSITE) {
            return paragraphDirection == 1 ? i3 - lineExtent : i2 - lineExtent;
        }
        return ((i2 + i3) - (lineExtent & (-2))) >> 1;
    }

    public final CharSequence getText() {
        return this.mText;
    }

    public final TextPaint getPaint() {
        return this.mPaint;
    }

    public final int getWidth() {
        return this.mWidth;
    }

    public int getEllipsizedWidth() {
        return this.mWidth;
    }

    public final void increaseWidthTo(int i) {
        if (i < this.mWidth) {
            throw new RuntimeException("attempted to reduce Layout width");
        }
        this.mWidth = i;
    }

    public int getHeight() {
        return getLineTop(getLineCount());
    }

    public final Alignment getAlignment() {
        return this.mAlignment;
    }

    public final float getSpacingMultiplier() {
        return this.mSpacingMult;
    }

    public final float getSpacingAdd() {
        return this.mSpacingAdd;
    }

    public final TextDirectionHeuristic getTextDirectionHeuristic() {
        return this.mTextDir;
    }

    public int getLineBounds(int i, Rect rect) {
        if (rect != null) {
            rect.left = 0;
            rect.top = getLineTop(i);
            rect.right = this.mWidth;
            rect.bottom = getLineTop(i + 1);
        }
        return getLineBaseline(i);
    }

    public boolean isLevelBoundary(int i) {
        int lineForOffset = getLineForOffset(i);
        Directions lineDirections = getLineDirections(lineForOffset);
        if (lineDirections == DIRS_ALL_LEFT_TO_RIGHT || lineDirections == DIRS_ALL_RIGHT_TO_LEFT) {
            return false;
        }
        int[] iArr = lineDirections.mDirections;
        int lineStart = getLineStart(lineForOffset);
        int lineEnd = getLineEnd(lineForOffset);
        if (i == lineStart || i == lineEnd) {
            return ((iArr[(i == lineStart ? 0 : iArr.length + (-2)) + 1] >>> 26) & 63) != (getParagraphDirection(lineForOffset) == 1 ? 0 : 1);
        }
        int i2 = i - lineStart;
        for (int i3 = 0; i3 < iArr.length; i3 += 2) {
            if (i2 == iArr[i3]) {
                return true;
            }
        }
        return false;
    }

    public boolean isRtlCharAt(int i) {
        int lineForOffset = getLineForOffset(i);
        Directions lineDirections = getLineDirections(lineForOffset);
        if (lineDirections == DIRS_ALL_LEFT_TO_RIGHT) {
            return false;
        }
        if (lineDirections == DIRS_ALL_RIGHT_TO_LEFT) {
            return true;
        }
        int[] iArr = lineDirections.mDirections;
        int lineStart = getLineStart(lineForOffset);
        for (int i2 = 0; i2 < iArr.length; i2 += 2) {
            if (i >= (iArr[i2] & RUN_LENGTH_MASK) + lineStart) {
                return (((iArr[i2 + 1] >>> 26) & 63) & 1) != 0;
            }
        }
        return false;
    }

    private boolean primaryIsTrailingPrevious(int i) {
        int i2;
        int i3;
        int lineForOffset = getLineForOffset(i);
        int lineStart = getLineStart(lineForOffset);
        int lineEnd = getLineEnd(lineForOffset);
        int[] iArr = getLineDirections(lineForOffset).mDirections;
        int i4 = 0;
        while (true) {
            i2 = -1;
            if (i4 >= iArr.length) {
                i3 = -1;
                break;
            }
            int i5 = iArr[i4] + lineStart;
            int i6 = i4 + 1;
            int i7 = (iArr[i6] & RUN_LENGTH_MASK) + i5;
            if (i7 > lineEnd) {
                i7 = lineEnd;
            }
            if (i < i5 || i >= i7) {
                i4 += 2;
            } else {
                if (i > i5) {
                    return false;
                }
                i3 = (iArr[i6] >>> 26) & 63;
            }
        }
        if (i3 == -1) {
            i3 = getParagraphDirection(lineForOffset) == 1 ? 0 : 1;
        }
        if (i != lineStart) {
            int i8 = i - 1;
            int i9 = 0;
            while (true) {
                if (i9 >= iArr.length) {
                    break;
                }
                int i10 = iArr[i9] + lineStart;
                int i11 = i9 + 1;
                int i12 = (iArr[i11] & RUN_LENGTH_MASK) + i10;
                if (i12 > lineEnd) {
                    i12 = lineEnd;
                }
                if (i8 >= i10 && i8 < i12) {
                    i2 = (iArr[i11] >>> 26) & 63;
                    break;
                }
                i9 += 2;
            }
        } else {
            i2 = getParagraphDirection(lineForOffset) == 1 ? 0 : 1;
        }
        return i2 < i3;
    }

    public float getPrimaryHorizontal(int i) {
        return getPrimaryHorizontal(i, false);
    }

    public float getPrimaryHorizontal(int i, boolean z) {
        return getHorizontal(i, primaryIsTrailingPrevious(i), z);
    }

    public float getSecondaryHorizontal(int i) {
        return getSecondaryHorizontal(i, false);
    }

    public float getSecondaryHorizontal(int i, boolean z) {
        return getHorizontal(i, !primaryIsTrailingPrevious(i), z);
    }

    private float getHorizontal(int i, boolean z, boolean z2) {
        return getHorizontal(i, z, getLineForOffset(i), z2);
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0036  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float getHorizontal(int r15, boolean r16, int r17, boolean r18) {
        /*
            r14 = this;
            r0 = r14
            r1 = r17
            int r11 = r14.getLineStart(r1)
            int r6 = r14.getLineEnd(r1)
            int r7 = r14.getParagraphDirection(r1)
            boolean r9 = r14.getLineContainsTab(r1)
            android.text.Layout$Directions r8 = r14.getLineDirections(r1)
            r12 = 0
            if (r9 == 0) goto L36
            java.lang.CharSequence r2 = r0.mText
            boolean r3 = r2 instanceof android.text.Spanned
            if (r3 == 0) goto L36
            android.text.Spanned r2 = (android.text.Spanned) r2
            java.lang.Class<android.text.style.TabStopSpan> r3 = android.text.style.TabStopSpan.class
            java.lang.Object[] r2 = getParagraphSpans(r2, r11, r6, r3)
            android.text.style.TabStopSpan[] r2 = (android.text.style.TabStopSpan[]) r2
            int r3 = r2.length
            if (r3 <= 0) goto L36
            android.text.Layout$TabStops r3 = new android.text.Layout$TabStops
            r4 = 20
            r3.<init>(r4, r2)
            r10 = r3
            goto L37
        L36:
            r10 = r12
        L37:
            android.text.TextLine r13 = android.text.TextLine.obtain()
            android.text.TextPaint r3 = r0.mPaint
            java.lang.CharSequence r4 = r0.mText
            r2 = r13
            r5 = r11
            r2.set(r3, r4, r5, r6, r7, r8, r9, r10)
            int r2 = r15 - r11
            r3 = r16
            float r2 = r13.measure(r2, r3, r12)
            android.text.TextLine.recycle(r13)
            if (r18 == 0) goto L59
            int r3 = r0.mWidth
            float r4 = (float) r3
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 <= 0) goto L59
            float r2 = (float) r3
        L59:
            int r3 = r14.getParagraphLeft(r1)
            int r4 = r14.getParagraphRight(r1)
            int r1 = r14.getLineStartPos(r1, r3, r4)
            float r1 = (float) r1
            float r1 = r1 + r2
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.Layout.getHorizontal(int, boolean, int, boolean):float");
    }

    public float getLineLeft(int i) {
        float paragraphRight;
        float lineMax;
        int paragraphDirection = getParagraphDirection(i);
        Alignment paragraphAlignment = getParagraphAlignment(i);
        if (paragraphAlignment == Alignment.ALIGN_LEFT) {
            return 0.0f;
        }
        if (paragraphAlignment == Alignment.ALIGN_NORMAL) {
            if (paragraphDirection != -1) {
                return 0.0f;
            }
            paragraphRight = getParagraphRight(i);
            lineMax = getLineMax(i);
        } else if (paragraphAlignment == Alignment.ALIGN_RIGHT) {
            paragraphRight = this.mWidth;
            lineMax = getLineMax(i);
        } else {
            if (paragraphAlignment != Alignment.ALIGN_OPPOSITE) {
                int paragraphLeft = getParagraphLeft(i);
                return paragraphLeft + (((getParagraphRight(i) - paragraphLeft) - (((int) getLineMax(i)) & (-2))) / 2);
            }
            if (paragraphDirection == -1) {
                return 0.0f;
            }
            paragraphRight = this.mWidth;
            lineMax = getLineMax(i);
        }
        return paragraphRight - lineMax;
    }

    public float getLineRight(int i) {
        float paragraphLeft;
        float lineMax;
        int paragraphDirection = getParagraphDirection(i);
        Alignment paragraphAlignment = getParagraphAlignment(i);
        if (paragraphAlignment == Alignment.ALIGN_LEFT) {
            paragraphLeft = getParagraphLeft(i);
            lineMax = getLineMax(i);
        } else {
            if (paragraphAlignment != Alignment.ALIGN_NORMAL) {
                if (paragraphAlignment == Alignment.ALIGN_RIGHT) {
                    return this.mWidth;
                }
                if (paragraphAlignment == Alignment.ALIGN_OPPOSITE) {
                    if (paragraphDirection == -1) {
                        return getLineMax(i);
                    }
                    return this.mWidth;
                }
                int paragraphLeft2 = getParagraphLeft(i);
                int paragraphRight = getParagraphRight(i);
                return paragraphRight - (((paragraphRight - paragraphLeft2) - (((int) getLineMax(i)) & (-2))) / 2);
            }
            if (paragraphDirection == -1) {
                return this.mWidth;
            }
            paragraphLeft = getParagraphLeft(i);
            lineMax = getLineMax(i);
        }
        return paragraphLeft + lineMax;
    }

    public float getLineMax(int i) {
        float paragraphLeadingMargin = getParagraphLeadingMargin(i);
        float lineExtent = getLineExtent(i, false);
        return paragraphLeadingMargin + lineExtent >= 0.0f ? lineExtent : -lineExtent;
    }

    public float getLineWidth(int i) {
        float paragraphLeadingMargin = getParagraphLeadingMargin(i);
        float lineExtent = getLineExtent(i, true);
        return paragraphLeadingMargin + lineExtent >= 0.0f ? lineExtent : -lineExtent;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0033  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float getLineExtent(int r10, boolean r11) {
        /*
            r9 = this;
            int r3 = r9.getLineStart(r10)
            if (r11 == 0) goto Lb
            int r11 = r9.getLineEnd(r10)
            goto Lf
        Lb:
            int r11 = r9.getLineVisibleEnd(r10)
        Lf:
            r4 = r11
            boolean r7 = r9.getLineContainsTab(r10)
            r11 = 0
            if (r7 == 0) goto L33
            java.lang.CharSequence r0 = r9.mText
            boolean r1 = r0 instanceof android.text.Spanned
            if (r1 == 0) goto L33
            android.text.Spanned r0 = (android.text.Spanned) r0
            java.lang.Class<android.text.style.TabStopSpan> r1 = android.text.style.TabStopSpan.class
            java.lang.Object[] r0 = getParagraphSpans(r0, r3, r4, r1)
            android.text.style.TabStopSpan[] r0 = (android.text.style.TabStopSpan[]) r0
            int r1 = r0.length
            if (r1 <= 0) goto L33
            android.text.Layout$TabStops r1 = new android.text.Layout$TabStops
            r2 = 20
            r1.<init>(r2, r0)
            r8 = r1
            goto L34
        L33:
            r8 = r11
        L34:
            android.text.Layout$Directions r6 = r9.getLineDirections(r10)
            if (r6 != 0) goto L3c
            r10 = 0
            return r10
        L3c:
            int r5 = r9.getParagraphDirection(r10)
            android.text.TextLine r10 = android.text.TextLine.obtain()
            android.text.TextPaint r1 = r9.mPaint
            java.lang.CharSequence r2 = r9.mText
            r0 = r10
            r0.set(r1, r2, r3, r4, r5, r6, r7, r8)
            float r11 = r10.metrics(r11)
            android.text.TextLine.recycle(r10)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.Layout.getLineExtent(int, boolean):float");
    }

    private float getLineExtent(int i, TabStops tabStops, boolean z) {
        int lineStart = getLineStart(i);
        int lineEnd = z ? getLineEnd(i) : getLineVisibleEnd(i);
        boolean lineContainsTab = getLineContainsTab(i);
        Directions lineDirections = getLineDirections(i);
        int paragraphDirection = getParagraphDirection(i);
        TextLine textLineObtain = TextLine.obtain();
        textLineObtain.set(this.mPaint, this.mText, lineStart, lineEnd, paragraphDirection, lineDirections, lineContainsTab, tabStops);
        float fMetrics = textLineObtain.metrics(null);
        TextLine.recycle(textLineObtain);
        return fMetrics;
    }

    public int getLineForVertical(int i) {
        int lineCount = getLineCount();
        int i2 = -1;
        while (lineCount - i2 > 1) {
            int i3 = (lineCount + i2) / 2;
            if (getLineTop(i3) > i) {
                lineCount = i3;
            } else {
                i2 = i3;
            }
        }
        if (i2 < 0) {
            return 0;
        }
        return i2;
    }

    public int getLineForOffset(int i) {
        int lineCount = getLineCount();
        int i2 = -1;
        while (lineCount - i2 > 1) {
            int i3 = (lineCount + i2) / 2;
            if (getLineStart(i3) > i) {
                lineCount = i3;
            } else {
                i2 = i3;
            }
        }
        if (i2 < 0) {
            return 0;
        }
        return i2;
    }

    public int getOffsetForHorizontal(int i, float f) {
        int i2 = 1;
        int lineEnd = getLineEnd(i) - 1;
        int lineStart = getLineStart(i);
        Directions lineDirections = getLineDirections(i);
        if (i == getLineCount() - 1) {
            lineEnd++;
        }
        float fAbs = Math.abs(getPrimaryHorizontal(lineStart) - f);
        int i3 = 0;
        int i4 = lineStart;
        while (i3 < lineDirections.mDirections.length) {
            int i5 = lineDirections.mDirections[i3] + lineStart;
            int i6 = i3 + 1;
            int i7 = (lineDirections.mDirections[i6] & RUN_LENGTH_MASK) + i5;
            int i8 = (lineDirections.mDirections[i6] & 67108864) != 0 ? -1 : i2;
            if (i7 > lineEnd) {
                i7 = lineEnd;
            }
            int i9 = (i7 - 1) + i2;
            int i10 = i5 + 1;
            int i11 = i10 - 1;
            while (i9 - i11 > i2) {
                int i12 = (i9 + i11) / 2;
                float f2 = i8;
                if (getPrimaryHorizontal(getOffsetAtStartOf(i12)) * f2 >= f2 * f) {
                    i9 = i12;
                } else {
                    i11 = i12;
                }
                i2 = 1;
            }
            if (i11 >= i10) {
                i10 = i11;
            }
            if (i10 < i7) {
                int offsetAtStartOf = getOffsetAtStartOf(i10);
                float fAbs2 = Math.abs(getPrimaryHorizontal(offsetAtStartOf) - f);
                int offsetAfter = TextUtils.getOffsetAfter(this.mText, offsetAtStartOf);
                if (offsetAfter < i7) {
                    float fAbs3 = Math.abs(getPrimaryHorizontal(offsetAfter) - f);
                    if (fAbs3 < fAbs2) {
                        fAbs2 = fAbs3;
                        offsetAtStartOf = offsetAfter;
                    }
                }
                if (fAbs2 < fAbs) {
                    i4 = offsetAtStartOf;
                    fAbs = fAbs2;
                }
            }
            float fAbs4 = Math.abs(getPrimaryHorizontal(i5) - f);
            if (fAbs4 < fAbs) {
                fAbs = fAbs4;
                i4 = i5;
            }
            i3 += 2;
            i2 = 1;
        }
        return Math.abs(getPrimaryHorizontal(lineEnd) - f) <= fAbs ? lineEnd : i4;
    }

    public final int getLineEnd(int i) {
        return getLineStart(i + 1);
    }

    public int getLineVisibleEnd(int i) {
        return getLineVisibleEnd(i, getLineStart(i), getLineStart(i + 1));
    }

    private int getLineVisibleEnd(int i, int i2, int i3) {
        CharSequence charSequence = this.mText;
        if (i == getLineCount() - 1) {
            return i3;
        }
        while (i3 > i2) {
            int i4 = i3 - 1;
            char cCharAt = charSequence.charAt(i4);
            if (cCharAt != '\n') {
                if (cCharAt != ' ' && cCharAt != '\t') {
                    break;
                }
                i3--;
            } else {
                return i4;
            }
        }
        return i3;
    }

    public final int getLineBottom(int i) {
        return getLineTop(i + 1);
    }

    public final int getLineBaseline(int i) {
        return getLineTop(i + 1) - getLineDescent(i);
    }

    public final int getLineAscent(int i) {
        return getLineTop(i) - (getLineTop(i + 1) - getLineDescent(i));
    }

    public int getOffsetToLeftOf(int i) {
        return getOffsetToLeftRightOf(i, true);
    }

    public int getOffsetToRightOf(int i) {
        return getOffsetToLeftRightOf(i, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x004b A[PHI: r1 r2
      0x004b: PHI (r1v1 int) = (r1v0 int), (r1v3 int) binds: [B:20:0x0035, B:22:0x0043] A[DONT_GENERATE, DONT_INLINE]
      0x004b: PHI (r2v1 int) = (r2v0 int), (r2v3 int) binds: [B:20:0x0035, B:22:0x0043] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int getOffsetToLeftRightOf(int r12, boolean r13) {
        /*
            r11 = this;
            int r0 = r11.getLineForOffset(r12)
            int r1 = r11.getLineStart(r0)
            int r2 = r11.getLineEnd(r0)
            int r3 = r11.getParagraphDirection(r0)
            r4 = -1
            r5 = 0
            r6 = 1
            if (r3 != r4) goto L17
            r4 = r6
            goto L18
        L17:
            r4 = r5
        L18:
            if (r13 != r4) goto L1c
            r4 = r6
            goto L1d
        L1c:
            r4 = r5
        L1d:
            if (r4 == 0) goto L2c
            if (r12 != r2) goto L35
            int r4 = r11.getLineCount()
            int r4 = r4 - r6
            if (r0 >= r4) goto L2b
            int r0 = r0 + 1
            goto L32
        L2b:
            return r12
        L2c:
            if (r12 != r1) goto L35
            if (r0 <= 0) goto L34
            int r0 = r0 + (-1)
        L32:
            r5 = r6
            goto L35
        L34:
            return r12
        L35:
            if (r5 == 0) goto L4b
            int r1 = r11.getLineStart(r0)
            int r2 = r11.getLineEnd(r0)
            int r4 = r11.getParagraphDirection(r0)
            if (r4 == r3) goto L4b
            r13 = r13 ^ 1
            r10 = r1
            r5 = r2
            r6 = r4
            goto L4e
        L4b:
            r10 = r1
            r5 = r2
            r6 = r3
        L4e:
            android.text.Layout$Directions r7 = r11.getLineDirections(r0)
            android.text.TextLine r0 = android.text.TextLine.obtain()
            android.text.TextPaint r2 = r11.mPaint
            java.lang.CharSequence r3 = r11.mText
            r8 = 0
            r9 = 0
            r1 = r0
            r4 = r10
            r1.set(r2, r3, r4, r5, r6, r7, r8, r9)
            int r12 = r12 - r10
            int r12 = r0.getOffsetToLeftRightOf(r12, r13)
            int r10 = r10 + r12
            android.text.TextLine.recycle(r0)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.Layout.getOffsetToLeftRightOf(int, boolean):int");
    }

    private int getOffsetAtStartOf(int i) {
        char cCharAt;
        if (i == 0) {
            return 0;
        }
        CharSequence charSequence = this.mText;
        char cCharAt2 = charSequence.charAt(i);
        if (cCharAt2 >= 56320 && cCharAt2 <= 57343 && (cCharAt = charSequence.charAt(i - 1)) >= 55296 && cCharAt <= 56319) {
            i--;
        }
        if (this.mSpannedText) {
            Spanned spanned = (Spanned) charSequence;
            ReplacementSpan[] replacementSpanArr = (ReplacementSpan[]) spanned.getSpans(i, i, ReplacementSpan.class);
            for (int i2 = 0; i2 < replacementSpanArr.length; i2++) {
                int spanStart = spanned.getSpanStart(replacementSpanArr[i2]);
                int spanEnd = spanned.getSpanEnd(replacementSpanArr[i2]);
                if (spanStart < i && spanEnd > i) {
                    i = spanStart;
                }
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: android.text.Layout$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$text$Layout$Alignment;

        static {
            int[] iArr = new int[Alignment.values().length];
            $SwitchMap$android$text$Layout$Alignment = iArr;
            try {
                iArr[Alignment.ALIGN_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public boolean shouldClampCursor(int i) {
        int i2 = AnonymousClass1.$SwitchMap$android$text$Layout$Alignment[getParagraphAlignment(i).ordinal()];
        if (i2 != 1) {
            return i2 == 2 && getParagraphDirection(i) > 0;
        }
        return true;
    }

    public void getCursorPath(int i, Path path, CharSequence charSequence) {
        path.reset();
        int lineForOffset = getLineForOffset(i);
        int lineTop = getLineTop(lineForOffset);
        int lineTop2 = getLineTop(lineForOffset + 1);
        boolean zShouldClampCursor = shouldClampCursor(lineForOffset);
        float primaryHorizontal = getPrimaryHorizontal(i, zShouldClampCursor) - 0.5f;
        float secondaryHorizontal = isLevelBoundary(i) ? getSecondaryHorizontal(i, zShouldClampCursor) - 0.5f : primaryHorizontal;
        int metaState = TextKeyListener.getMetaState(charSequence, 1) | TextKeyListener.getMetaState(charSequence, 2048);
        int metaState2 = TextKeyListener.getMetaState(charSequence, 2);
        int i2 = 0;
        if (metaState != 0 || metaState2 != 0) {
            i2 = (lineTop2 - lineTop) >> 2;
            if (metaState2 != 0) {
                lineTop += i2;
            }
            if (metaState != 0) {
                lineTop2 -= i2;
            }
        }
        if (primaryHorizontal < 0.5f) {
            primaryHorizontal = 0.5f;
        }
        if (secondaryHorizontal < 0.5f) {
            secondaryHorizontal = 0.5f;
        }
        if (Float.compare(primaryHorizontal, secondaryHorizontal) == 0) {
            path.moveTo(primaryHorizontal, lineTop);
            path.lineTo(primaryHorizontal, lineTop2);
        } else {
            path.moveTo(primaryHorizontal, lineTop);
            float f = (lineTop + lineTop2) >> 1;
            path.lineTo(primaryHorizontal, f);
            path.moveTo(secondaryHorizontal, f);
            path.lineTo(secondaryHorizontal, lineTop2);
        }
        if (metaState == 2) {
            float f2 = lineTop2;
            path.moveTo(secondaryHorizontal, f2);
            float f3 = i2;
            float f4 = lineTop2 + i2;
            path.lineTo(secondaryHorizontal - f3, f4);
            path.lineTo(secondaryHorizontal, f2);
            path.lineTo(secondaryHorizontal + f3, f4);
        } else if (metaState == 1) {
            float f5 = lineTop2;
            path.moveTo(secondaryHorizontal, f5);
            float f6 = i2;
            float f7 = secondaryHorizontal - f6;
            float f8 = lineTop2 + i2;
            path.lineTo(f7, f8);
            float f9 = f8 - 0.5f;
            path.moveTo(f7, f9);
            float f10 = f6 + secondaryHorizontal;
            path.lineTo(f10, f9);
            path.moveTo(f10, f8);
            path.lineTo(secondaryHorizontal, f5);
        }
        if (metaState2 == 2) {
            float f11 = lineTop;
            path.moveTo(primaryHorizontal, f11);
            float f12 = i2;
            float f13 = lineTop - i2;
            path.lineTo(primaryHorizontal - f12, f13);
            path.lineTo(primaryHorizontal, f11);
            path.lineTo(primaryHorizontal + f12, f13);
            return;
        }
        if (metaState2 == 1) {
            float f14 = lineTop;
            path.moveTo(primaryHorizontal, f14);
            float f15 = i2;
            float f16 = primaryHorizontal - f15;
            float f17 = lineTop - i2;
            path.lineTo(f16, f17);
            float f18 = 0.5f + f17;
            path.moveTo(f16, f18);
            float f19 = f15 + primaryHorizontal;
            path.lineTo(f19, f18);
            path.moveTo(f19, f17);
            path.lineTo(primaryHorizontal, f14);
        }
    }

    private void addSelection(int i, int i2, int i3, int i4, int i5, Path path) {
        int iMax;
        int iMin;
        int lineStart = getLineStart(i);
        int lineEnd = getLineEnd(i);
        Directions lineDirections = getLineDirections(i);
        if (lineEnd > lineStart && this.mText.charAt(lineEnd - 1) == '\n') {
            lineEnd--;
        }
        for (int i6 = 0; i6 < lineDirections.mDirections.length; i6 += 2) {
            int i7 = lineDirections.mDirections[i6] + lineStart;
            int i8 = (lineDirections.mDirections[i6 + 1] & RUN_LENGTH_MASK) + i7;
            if (i8 > lineEnd) {
                i8 = lineEnd;
            }
            if (i2 <= i8 && i3 >= i7 && (iMax = Math.max(i2, i7)) != (iMin = Math.min(i3, i8))) {
                float horizontal = getHorizontal(iMax, false, i, false);
                float horizontal2 = getHorizontal(iMin, true, i, false);
                path.addRect(Math.min(horizontal, horizontal2), i4, Math.max(horizontal, horizontal2), i5, Path.Direction.CW);
            }
        }
    }

    public void getSelectionPath(int i, int i2, Path path) {
        int i3;
        int i4;
        float f;
        path.reset();
        if (i == i2) {
            return;
        }
        if (i2 < i) {
            i4 = i;
            i3 = i2;
        } else {
            i3 = i;
            i4 = i2;
        }
        int lineForOffset = getLineForOffset(i3);
        int lineForOffset2 = getLineForOffset(i4);
        int lineTop = getLineTop(lineForOffset);
        int lineBottom = getLineBottom(lineForOffset2);
        if (lineForOffset == lineForOffset2) {
            addSelection(lineForOffset, i3, i4, lineTop, lineBottom, path);
            return;
        }
        float f2 = this.mWidth;
        addSelection(lineForOffset, i3, getLineEnd(lineForOffset), lineTop, getLineBottom(lineForOffset), path);
        if (getParagraphDirection(lineForOffset) == -1) {
            path.addRect(getLineLeft(lineForOffset), lineTop, 0.0f, getLineBottom(lineForOffset), Path.Direction.CW);
            f = f2;
        } else {
            f = f2;
            path.addRect(getLineRight(lineForOffset), lineTop, f2, getLineBottom(lineForOffset), Path.Direction.CW);
        }
        while (true) {
            lineForOffset++;
            if (lineForOffset >= lineForOffset2) {
                break;
            }
            path.addRect(0.0f, getLineTop(lineForOffset), f, getLineBottom(lineForOffset), Path.Direction.CW);
        }
        int lineTop2 = getLineTop(lineForOffset2);
        int lineBottom2 = getLineBottom(lineForOffset2);
        addSelection(lineForOffset2, getLineStart(lineForOffset2), i4, lineTop2, lineBottom2, path);
        if (getParagraphDirection(lineForOffset2) == -1) {
            path.addRect(f, lineTop2, getLineRight(lineForOffset2), lineBottom2, Path.Direction.CW);
        } else {
            path.addRect(0.0f, lineTop2, getLineLeft(lineForOffset2), lineBottom2, Path.Direction.CW);
        }
    }

    public final Alignment getParagraphAlignment(int i) {
        AlignmentSpan[] alignmentSpanArr;
        int length;
        Alignment alignment = this.mAlignment;
        return (!this.mSpannedText || (length = (alignmentSpanArr = (AlignmentSpan[]) getParagraphSpans((Spanned) this.mText, getLineStart(i), getLineEnd(i), AlignmentSpan.class)).length) <= 0) ? alignment : alignmentSpanArr[length - 1].getAlignment();
    }

    public final int getParagraphLeft(int i) {
        if (getParagraphDirection(i) == -1 || !this.mSpannedText) {
            return 0;
        }
        return getParagraphLeadingMargin(i);
    }

    public final int getParagraphRight(int i) {
        int i2 = this.mWidth;
        return (getParagraphDirection(i) == 1 || !this.mSpannedText) ? i2 : i2 - getParagraphLeadingMargin(i);
    }

    private int getParagraphLeadingMargin(int i) {
        boolean z;
        if (!this.mSpannedText) {
            return 0;
        }
        Spanned spanned = (Spanned) this.mText;
        int lineStart = getLineStart(i);
        LeadingMarginSpan[] leadingMarginSpanArr = (LeadingMarginSpan[]) getParagraphSpans(spanned, lineStart, spanned.nextSpanTransition(lineStart, getLineEnd(i), LeadingMarginSpan.class), LeadingMarginSpan.class);
        if (leadingMarginSpanArr.length == 0) {
            return 0;
        }
        boolean z2 = lineStart == 0 || spanned.charAt(lineStart - 1) == '\n';
        int leadingMargin = 0;
        for (LeadingMarginSpan leadingMarginSpan : leadingMarginSpanArr) {
            if (leadingMarginSpan instanceof LeadingMarginSpan.LeadingMarginSpan2) {
                z = i < getLineForOffset(spanned.getSpanStart(leadingMarginSpan)) + ((LeadingMarginSpan.LeadingMarginSpan2) leadingMarginSpan).getLeadingMarginLineCount();
            } else {
                z = z2;
            }
            leadingMargin += leadingMarginSpan.getLeadingMargin(z);
        }
        return leadingMargin;
    }

    static float measurePara(TextPaint textPaint, CharSequence charSequence, int i, int i2) {
        Directions directions;
        int i3;
        boolean z;
        TabStops tabStops;
        MeasuredText measuredTextObtain = MeasuredText.obtain();
        TextLine textLineObtain = TextLine.obtain();
        try {
            measuredTextObtain.setPara(charSequence, i, i2, TextDirectionHeuristics.LTR);
            if (measuredTextObtain.mEasy) {
                directions = DIRS_ALL_LEFT_TO_RIGHT;
                i3 = 1;
            } else {
                directions = AndroidBidi.directions(measuredTextObtain.mDir, measuredTextObtain.mLevels, 0, measuredTextObtain.mChars, 0, measuredTextObtain.mLen);
                i3 = measuredTextObtain.mDir;
            }
            char[] cArr = measuredTextObtain.mChars;
            int i4 = measuredTextObtain.mLen;
            int i5 = 0;
            while (true) {
                if (i5 >= i4) {
                    z = false;
                    break;
                }
                if (cArr[i5] != '\t') {
                    i5++;
                } else if (charSequence instanceof Spanned) {
                    Spanned spanned = (Spanned) charSequence;
                    TabStopSpan[] tabStopSpanArr = (TabStopSpan[]) getParagraphSpans(spanned, i, spanned.nextSpanTransition(i, i2, TabStopSpan.class), TabStopSpan.class);
                    z = true;
                    tabStops = tabStopSpanArr.length > 0 ? new TabStops(20, tabStopSpanArr) : null;
                } else {
                    z = true;
                }
            }
            tabStops = null;
            textLineObtain.set(textPaint, charSequence, i, i2, i3, directions, z, tabStops);
            return textLineObtain.metrics(null);
        } finally {
            TextLine.recycle(textLineObtain);
            MeasuredText.recycle(measuredTextObtain);
        }
    }

    static class TabStops {
        private int mIncrement;
        private int mNumStops;
        private int[] mStops;

        public static float nextDefaultStop(float f, int i) {
            float f2 = i;
            return ((int) ((f + f2) / f2)) * i;
        }

        TabStops(int i, Object[] objArr) {
            reset(i, objArr);
        }

        void reset(int i, Object[] objArr) {
            this.mIncrement = i;
            int i2 = 0;
            if (objArr != null) {
                int[] iArr = this.mStops;
                int i3 = 0;
                for (Object obj : objArr) {
                    if (obj instanceof TabStopSpan) {
                        if (iArr == null) {
                            iArr = new int[10];
                        } else if (i3 == iArr.length) {
                            int[] iArr2 = new int[i3 * 2];
                            for (int i4 = 0; i4 < i3; i4++) {
                                iArr2[i4] = iArr[i4];
                            }
                            iArr = iArr2;
                        }
                        iArr[i3] = ((TabStopSpan) obj).getTabStop();
                        i3++;
                    }
                }
                if (i3 > 1) {
                    Arrays.sort(iArr, 0, i3);
                }
                if (iArr != this.mStops) {
                    this.mStops = iArr;
                }
                i2 = i3;
            }
            this.mNumStops = i2;
        }

        float nextTab(float f) {
            int i = this.mNumStops;
            if (i > 0) {
                int[] iArr = this.mStops;
                for (int i2 = 0; i2 < i; i2++) {
                    float f2 = iArr[i2];
                    if (f2 > f) {
                        return f2;
                    }
                }
            }
            return nextDefaultStop(f, this.mIncrement);
        }
    }

    static float nextTab(CharSequence charSequence, int i, int i2, float f, Object[] objArr) {
        boolean z;
        if (charSequence instanceof Spanned) {
            if (objArr == null) {
                objArr = getParagraphSpans((Spanned) charSequence, i, i2, TabStopSpan.class);
                z = true;
            } else {
                z = false;
            }
            float f2 = Float.MAX_VALUE;
            for (int i3 = 0; i3 < objArr.length; i3++) {
                if (z || (objArr[i3] instanceof TabStopSpan)) {
                    float tabStop = ((TabStopSpan) objArr[i3]).getTabStop();
                    if (tabStop < f2 && tabStop > f) {
                        f2 = tabStop;
                    }
                }
            }
            if (f2 != Float.MAX_VALUE) {
                return f2;
            }
        }
        return ((int) ((f + 20.0f) / 20.0f)) * 20;
    }

    protected final boolean isSpanned() {
        return this.mSpannedText;
    }

    static <T> T[] getParagraphSpans(Spanned spanned, int i, int i2, Class<T> cls) {
        if (i == i2 && i > 0) {
            return (T[]) ArrayUtils.emptyArray(cls);
        }
        return (T[]) spanned.getSpans(i, i2, cls);
    }

    private char getEllipsisChar(TextUtils.TruncateAt truncateAt) {
        return truncateAt == TextUtils.TruncateAt.END_SMALL ? ELLIPSIS_TWO_DOTS[0] : ELLIPSIS_NORMAL[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ellipsize(int i, int i2, int i3, char[] cArr, int i4, TextUtils.TruncateAt truncateAt) {
        int ellipsisCount = getEllipsisCount(i3);
        if (ellipsisCount == 0) {
            return;
        }
        int ellipsisStart = getEllipsisStart(i3);
        int lineStart = getLineStart(i3);
        int i5 = ellipsisStart;
        while (i5 < ellipsisStart + ellipsisCount) {
            char ellipsisChar = i5 == ellipsisStart ? getEllipsisChar(truncateAt) : (char) 65279;
            int i6 = i5 + lineStart;
            if (i6 >= i && i6 < i2) {
                cArr[(i6 + i4) - i] = ellipsisChar;
            }
            i5++;
        }
    }

    public static class Directions {
        int[] mDirections;

        Directions(int[] iArr) {
            this.mDirections = iArr;
        }
    }

    static class Ellipsizer implements CharSequence, GetChars {
        Layout mLayout;
        TextUtils.TruncateAt mMethod;
        CharSequence mText;
        int mWidth;

        public Ellipsizer(CharSequence charSequence) {
            this.mText = charSequence;
        }

        @Override // java.lang.CharSequence
        public char charAt(int i) {
            char[] cArrObtain = TextUtils.obtain(1);
            getChars(i, i + 1, cArrObtain, 0);
            char c = cArrObtain[0];
            TextUtils.recycle(cArrObtain);
            return c;
        }

        @Override // android.text.GetChars
        public void getChars(int i, int i2, char[] cArr, int i3) {
            int lineForOffset = this.mLayout.getLineForOffset(i2);
            TextUtils.getChars(this.mText, i, i2, cArr, i3);
            for (int lineForOffset2 = this.mLayout.getLineForOffset(i); lineForOffset2 <= lineForOffset; lineForOffset2++) {
                this.mLayout.ellipsize(i, i2, lineForOffset2, cArr, i3, this.mMethod);
            }
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.mText.length();
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int i, int i2) {
            char[] cArr = new char[i2 - i];
            getChars(i, i2, cArr, 0);
            return new String(cArr);
        }

        @Override // java.lang.CharSequence
        public String toString() {
            char[] cArr = new char[length()];
            getChars(0, length(), cArr, 0);
            return new String(cArr);
        }
    }

    static class SpannedEllipsizer extends Ellipsizer implements Spanned {
        private Spanned mSpanned;

        public SpannedEllipsizer(CharSequence charSequence) {
            super(charSequence);
            this.mSpanned = (Spanned) charSequence;
        }

        @Override // android.text.Spanned
        public <T> T[] getSpans(int i, int i2, Class<T> cls) {
            return (T[]) this.mSpanned.getSpans(i, i2, cls);
        }

        @Override // android.text.Spanned
        public int getSpanStart(Object obj) {
            return this.mSpanned.getSpanStart(obj);
        }

        @Override // android.text.Spanned
        public int getSpanEnd(Object obj) {
            return this.mSpanned.getSpanEnd(obj);
        }

        @Override // android.text.Spanned
        public int getSpanFlags(Object obj) {
            return this.mSpanned.getSpanFlags(obj);
        }

        @Override // android.text.Spanned
        public int nextSpanTransition(int i, int i2, Class cls) {
            return this.mSpanned.nextSpanTransition(i, i2, cls);
        }

        @Override // android.text.Layout.Ellipsizer, java.lang.CharSequence
        public CharSequence subSequence(int i, int i2) {
            char[] cArr = new char[i2 - i];
            getChars(i, i2, cArr, 0);
            SpannableString spannableString = new SpannableString(new String(cArr));
            TextUtils.copySpansFrom(this.mSpanned, i, i2, Object.class, spannableString, 0);
            return spannableString;
        }
    }
}
