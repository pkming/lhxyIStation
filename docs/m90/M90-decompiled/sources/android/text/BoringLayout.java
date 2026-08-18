package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.TextUtils;
import android.util.FloatMath;

/* JADX INFO: loaded from: classes.dex */
public class BoringLayout extends Layout implements TextUtils.EllipsizeCallback {
    private static final char FIRST_RIGHT_TO_LEFT = 1424;
    private static final TextPaint sTemp = new TextPaint();
    int mBottom;
    private int mBottomPadding;
    int mDesc;
    private String mDirect;
    private int mEllipsizedCount;
    private int mEllipsizedStart;
    private int mEllipsizedWidth;
    private float mMax;
    private Paint mPaint;
    private int mTopPadding;

    @Override // android.text.Layout
    public boolean getLineContainsTab(int i) {
        return false;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return 1;
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int i) {
        return 1;
    }

    public static BoringLayout make(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, Metrics metrics, boolean z) {
        return new BoringLayout(charSequence, textPaint, i, alignment, f, f2, metrics, z);
    }

    public static BoringLayout make(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, Metrics metrics, boolean z, TextUtils.TruncateAt truncateAt, int i2) {
        return new BoringLayout(charSequence, textPaint, i, alignment, f, f2, metrics, z, truncateAt, i2);
    }

    public BoringLayout replaceOrMake(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, Metrics metrics, boolean z) {
        replaceWith(charSequence, textPaint, i, alignment, f, f2);
        this.mEllipsizedWidth = i;
        this.mEllipsizedStart = 0;
        this.mEllipsizedCount = 0;
        init(charSequence, textPaint, i, alignment, f, f2, metrics, z, true);
        return this;
    }

    public BoringLayout replaceOrMake(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, Metrics metrics, boolean z, TextUtils.TruncateAt truncateAt, int i2) {
        boolean z2;
        if (truncateAt == null || truncateAt == TextUtils.TruncateAt.MARQUEE) {
            replaceWith(charSequence, textPaint, i, alignment, f, f2);
            this.mEllipsizedWidth = i;
            this.mEllipsizedStart = 0;
            this.mEllipsizedCount = 0;
            z2 = true;
        } else {
            replaceWith(TextUtils.ellipsize(charSequence, textPaint, i2, truncateAt, true, this), textPaint, i, alignment, f, f2);
            this.mEllipsizedWidth = i2;
            z2 = false;
        }
        init(getText(), textPaint, i, alignment, f, f2, metrics, z, z2);
        return this;
    }

    public BoringLayout(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, Metrics metrics, boolean z) {
        super(charSequence, textPaint, i, alignment, f, f2);
        this.mEllipsizedWidth = i;
        this.mEllipsizedStart = 0;
        this.mEllipsizedCount = 0;
        init(charSequence, textPaint, i, alignment, f, f2, metrics, z, true);
    }

    public BoringLayout(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, Metrics metrics, boolean z, TextUtils.TruncateAt truncateAt, int i2) {
        boolean z2;
        super(charSequence, textPaint, i, alignment, f, f2);
        if (truncateAt == null || truncateAt == TextUtils.TruncateAt.MARQUEE) {
            this.mEllipsizedWidth = i;
            this.mEllipsizedStart = 0;
            this.mEllipsizedCount = 0;
            z2 = true;
        } else {
            replaceWith(TextUtils.ellipsize(charSequence, textPaint, i2, truncateAt, true, this), textPaint, i, alignment, f, f2);
            this.mEllipsizedWidth = i2;
            z2 = false;
        }
        init(getText(), textPaint, i, alignment, f, f2, metrics, z, z2);
    }

    void init(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, Metrics metrics, boolean z, boolean z2) {
        int i2;
        int i3;
        if ((charSequence instanceof String) && alignment == Layout.Alignment.ALIGN_NORMAL) {
            this.mDirect = charSequence.toString();
        } else {
            this.mDirect = null;
        }
        this.mPaint = textPaint;
        if (z) {
            i2 = metrics.bottom;
            i3 = metrics.top;
        } else {
            i2 = metrics.descent;
            i3 = metrics.ascent;
        }
        int i4 = i2 - i3;
        if (f != 1.0f || f2 != 0.0f) {
            i4 = (int) ((i4 * f) + f2 + 0.5f);
        }
        this.mBottom = i4;
        if (z) {
            this.mDesc = i4 + metrics.top;
        } else {
            this.mDesc = i4 + metrics.ascent;
        }
        if (z2) {
            this.mMax = metrics.width;
        } else {
            TextLine textLineObtain = TextLine.obtain();
            textLineObtain.set(textPaint, charSequence, 0, charSequence.length(), 1, Layout.DIRS_ALL_LEFT_TO_RIGHT, false, null);
            this.mMax = (int) FloatMath.ceil(textLineObtain.metrics(null));
            TextLine.recycle(textLineObtain);
        }
        if (z) {
            this.mTopPadding = metrics.top - metrics.ascent;
            this.mBottomPadding = metrics.bottom - metrics.descent;
        }
    }

    public static Metrics isBoring(CharSequence charSequence, TextPaint textPaint) {
        return isBoring(charSequence, textPaint, TextDirectionHeuristics.FIRSTSTRONG_LTR, null);
    }

    public static Metrics isBoring(CharSequence charSequence, TextPaint textPaint, TextDirectionHeuristic textDirectionHeuristic) {
        return isBoring(charSequence, textPaint, textDirectionHeuristic, null);
    }

    public static Metrics isBoring(CharSequence charSequence, TextPaint textPaint, Metrics metrics) {
        return isBoring(charSequence, textPaint, TextDirectionHeuristics.FIRSTSTRONG_LTR, metrics);
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0030, code lost:
    
        if (r12 == null) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0036, code lost:
    
        if (r12.isRtl(r0, 0, r4) == false) goto L46;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0039, code lost:
    
        r2 = r3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.text.BoringLayout.Metrics isBoring(java.lang.CharSequence r10, android.text.TextPaint r11, android.text.TextDirectionHeuristic r12, android.text.BoringLayout.Metrics r13) {
        /*
            r0 = 500(0x1f4, float:7.0E-43)
            char[] r0 = android.text.TextUtils.obtain(r0)
            int r5 = r10.length()
            r1 = 0
            r2 = r1
        Lc:
            if (r2 >= r5) goto L3b
            int r3 = r2 + 500
            if (r3 <= r5) goto L14
            r4 = r5
            goto L15
        L14:
            r4 = r3
        L15:
            android.text.TextUtils.getChars(r10, r2, r4, r0, r1)
            int r4 = r4 - r2
            r2 = r1
        L1a:
            if (r2 >= r4) goto L30
            char r6 = r0[r2]
            r7 = 10
            if (r6 == r7) goto L2e
            r7 = 9
            if (r6 == r7) goto L2e
            r7 = 1424(0x590, float:1.995E-42)
            if (r6 < r7) goto L2b
            goto L2e
        L2b:
            int r2 = r2 + 1
            goto L1a
        L2e:
            r12 = r1
            goto L3c
        L30:
            if (r12 == 0) goto L39
            boolean r2 = r12.isRtl(r0, r1, r4)
            if (r2 == 0) goto L39
            goto L2e
        L39:
            r2 = r3
            goto Lc
        L3b:
            r12 = 1
        L3c:
            android.text.TextUtils.recycle(r0)
            if (r12 == 0) goto L52
            boolean r0 = r10 instanceof android.text.Spanned
            if (r0 == 0) goto L52
            r0 = r10
            android.text.Spanned r0 = (android.text.Spanned) r0
            java.lang.Class<android.text.style.ParagraphStyle> r2 = android.text.style.ParagraphStyle.class
            java.lang.Object[] r0 = r0.getSpans(r1, r5, r2)
            int r0 = r0.length
            if (r0 <= 0) goto L52
            goto L53
        L52:
            r1 = r12
        L53:
            if (r1 == 0) goto L7b
            if (r13 != 0) goto L5c
            android.text.BoringLayout$Metrics r13 = new android.text.BoringLayout$Metrics
            r13.<init>()
        L5c:
            android.text.TextLine r12 = android.text.TextLine.obtain()
            r4 = 0
            r6 = 1
            android.text.Layout$Directions r7 = android.text.Layout.DIRS_ALL_LEFT_TO_RIGHT
            r8 = 0
            r9 = 0
            r1 = r12
            r2 = r11
            r3 = r10
            r1.set(r2, r3, r4, r5, r6, r7, r8, r9)
            float r10 = r12.metrics(r13)
            float r10 = android.util.FloatMath.ceil(r10)
            int r10 = (int) r10
            r13.width = r10
            android.text.TextLine.recycle(r12)
            return r13
        L7b:
            r10 = 0
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.BoringLayout.isBoring(java.lang.CharSequence, android.text.TextPaint, android.text.TextDirectionHeuristic, android.text.BoringLayout$Metrics):android.text.BoringLayout$Metrics");
    }

    @Override // android.text.Layout
    public int getHeight() {
        return this.mBottom;
    }

    @Override // android.text.Layout
    public int getLineTop(int i) {
        if (i == 0) {
            return 0;
        }
        return this.mBottom;
    }

    @Override // android.text.Layout
    public int getLineDescent(int i) {
        return this.mDesc;
    }

    @Override // android.text.Layout
    public int getLineStart(int i) {
        if (i == 0) {
            return 0;
        }
        return getText().length();
    }

    @Override // android.text.Layout
    public float getLineMax(int i) {
        return this.mMax;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int i) {
        return Layout.DIRS_ALL_LEFT_TO_RIGHT;
    }

    @Override // android.text.Layout
    public int getTopPadding() {
        return this.mTopPadding;
    }

    @Override // android.text.Layout
    public int getBottomPadding() {
        return this.mBottomPadding;
    }

    @Override // android.text.Layout
    public int getEllipsisCount(int i) {
        return this.mEllipsizedCount;
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int i) {
        return this.mEllipsizedStart;
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    @Override // android.text.Layout
    public void draw(Canvas canvas, Path path, Paint paint, int i) {
        String str = this.mDirect;
        if (str != null && path == null) {
            canvas.drawText(str, 0.0f, this.mBottom - this.mDesc, this.mPaint);
        } else {
            super.draw(canvas, path, paint, i);
        }
    }

    @Override // android.text.TextUtils.EllipsizeCallback
    public void ellipsized(int i, int i2) {
        this.mEllipsizedStart = i;
        this.mEllipsizedCount = i2 - i;
    }

    public static class Metrics extends Paint.FontMetricsInt {
        public int width;

        @Override // android.graphics.Paint.FontMetricsInt
        public String toString() {
            return super.toString() + " width=" + this.width;
        }
    }
}
