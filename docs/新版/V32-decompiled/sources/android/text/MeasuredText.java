package android.text;

import android.graphics.Paint;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;

/* JADX INFO: loaded from: classes.dex */
class MeasuredText {
    private static final boolean localLOGV = false;
    char[] mChars;
    int mDir;
    boolean mEasy;
    int mLen;
    byte[] mLevels;
    private int mPos;
    CharSequence mText;
    int mTextStart;
    float[] mWidths;
    private TextPaint mWorkPaint = new TextPaint();
    private static final Object[] sLock = new Object[0];
    private static MeasuredText[] sCached = new MeasuredText[3];

    private MeasuredText() {
    }

    static MeasuredText obtain() {
        MeasuredText[] measuredTextArr;
        synchronized (sLock) {
            int length = sCached.length;
            do {
                length--;
                if (length >= 0) {
                    measuredTextArr = sCached;
                } else {
                    return new MeasuredText();
                }
            } while (measuredTextArr[length] == null);
            MeasuredText measuredText = measuredTextArr[length];
            measuredTextArr[length] = null;
            return measuredText;
        }
    }

    static MeasuredText recycle(MeasuredText measuredText) {
        measuredText.mText = null;
        if (measuredText.mLen < 1000) {
            synchronized (sLock) {
                int i = 0;
                while (true) {
                    MeasuredText[] measuredTextArr = sCached;
                    if (i >= measuredTextArr.length) {
                        break;
                    }
                    if (measuredTextArr[i] == null) {
                        measuredTextArr[i] = measuredText;
                        measuredText.mText = null;
                        break;
                    }
                    i++;
                }
            }
        }
        return null;
    }

    void setPos(int i) {
        this.mPos = i - this.mTextStart;
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x0096  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void setPara(java.lang.CharSequence r8, int r9, int r10, android.text.TextDirectionHeuristic r11) {
        /*
            r7 = this;
            r7.mText = r8
            r7.mTextStart = r9
            int r0 = r10 - r9
            r7.mLen = r0
            r1 = 0
            r7.mPos = r1
            float[] r2 = r7.mWidths
            if (r2 == 0) goto L12
            int r2 = r2.length
            if (r2 >= r0) goto L1a
        L12:
            int r2 = com.android.internal.util.ArrayUtils.idealFloatArraySize(r0)
            float[] r2 = new float[r2]
            r7.mWidths = r2
        L1a:
            char[] r2 = r7.mChars
            if (r2 == 0) goto L21
            int r2 = r2.length
            if (r2 >= r0) goto L29
        L21:
            int r2 = com.android.internal.util.ArrayUtils.idealCharArraySize(r0)
            char[] r2 = new char[r2]
            r7.mChars = r2
        L29:
            char[] r2 = r7.mChars
            android.text.TextUtils.getChars(r8, r9, r10, r2, r1)
            boolean r2 = r8 instanceof android.text.Spanned
            if (r2 == 0) goto L63
            android.text.Spanned r8 = (android.text.Spanned) r8
            java.lang.Class<android.text.style.ReplacementSpan> r2 = android.text.style.ReplacementSpan.class
            java.lang.Object[] r10 = r8.getSpans(r9, r10, r2)
            android.text.style.ReplacementSpan[] r10 = (android.text.style.ReplacementSpan[]) r10
            r2 = r1
        L3d:
            int r3 = r10.length
            if (r2 >= r3) goto L63
            r3 = r10[r2]
            int r3 = r8.getSpanStart(r3)
            int r3 = r3 - r9
            r4 = r10[r2]
            int r4 = r8.getSpanEnd(r4)
            int r4 = r4 - r9
            if (r3 >= 0) goto L51
            r3 = r1
        L51:
            if (r4 <= r0) goto L54
            r4 = r0
        L54:
            if (r3 >= r4) goto L60
            char[] r5 = r7.mChars
            r6 = 65532(0xfffc, float:9.183E-41)
            r5[r3] = r6
            int r3 = r3 + 1
            goto L54
        L60:
            int r2 = r2 + 1
            goto L3d
        L63:
            android.text.TextDirectionHeuristic r8 = android.text.TextDirectionHeuristics.LTR
            r9 = 1
            if (r11 == r8) goto L70
            android.text.TextDirectionHeuristic r8 = android.text.TextDirectionHeuristics.FIRSTSTRONG_LTR
            if (r11 == r8) goto L70
            android.text.TextDirectionHeuristic r8 = android.text.TextDirectionHeuristics.ANYRTL_LTR
            if (r11 != r8) goto L7d
        L70:
            char[] r8 = r7.mChars
            boolean r8 = android.text.TextUtils.doesNotNeedBidi(r8, r1, r0)
            if (r8 == 0) goto L7d
            r7.mDir = r9
            r7.mEasy = r9
            goto Lb9
        L7d:
            byte[] r8 = r7.mLevels
            if (r8 == 0) goto L84
            int r8 = r8.length
            if (r8 >= r0) goto L8c
        L84:
            int r8 = com.android.internal.util.ArrayUtils.idealByteArraySize(r0)
            byte[] r8 = new byte[r8]
            r7.mLevels = r8
        L8c:
            android.text.TextDirectionHeuristic r8 = android.text.TextDirectionHeuristics.LTR
            r10 = -1
            if (r11 != r8) goto L92
            goto Lad
        L92:
            android.text.TextDirectionHeuristic r8 = android.text.TextDirectionHeuristics.RTL
            if (r11 != r8) goto L98
        L96:
            r9 = r10
            goto Lad
        L98:
            android.text.TextDirectionHeuristic r8 = android.text.TextDirectionHeuristics.FIRSTSTRONG_LTR
            if (r11 != r8) goto L9e
            r9 = 2
            goto Lad
        L9e:
            android.text.TextDirectionHeuristic r8 = android.text.TextDirectionHeuristics.FIRSTSTRONG_RTL
            if (r11 != r8) goto La4
            r9 = -2
            goto Lad
        La4:
            char[] r8 = r7.mChars
            boolean r8 = r11.isRtl(r8, r1, r0)
            if (r8 == 0) goto Lad
            goto L96
        Lad:
            char[] r8 = r7.mChars
            byte[] r10 = r7.mLevels
            int r8 = android.text.AndroidBidi.bidi(r9, r8, r10, r0, r1)
            r7.mDir = r8
            r7.mEasy = r1
        Lb9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.MeasuredText.setPara(java.lang.CharSequence, int, int, android.text.TextDirectionHeuristic):void");
    }

    float addStyleRun(TextPaint textPaint, int i, Paint.FontMetricsInt fontMetricsInt) {
        if (fontMetricsInt != null) {
            textPaint.getFontMetricsInt(fontMetricsInt);
        }
        int i2 = this.mPos;
        int i3 = i2 + i;
        this.mPos = i3;
        if (this.mEasy) {
            return textPaint.getTextRunAdvances(this.mChars, i2, i, i2, i, this.mDir == 1 ? 0 : 1, this.mWidths, i2);
        }
        byte b = this.mLevels[i2];
        float textRunAdvances = 0.0f;
        int i4 = i2 + 1;
        while (true) {
            if (i4 == i3 || this.mLevels[i4] != b) {
                int i5 = i4 - i2;
                textRunAdvances += textPaint.getTextRunAdvances(this.mChars, i2, i5, i2, i5, (b & 1) == 0 ? 0 : 1, this.mWidths, i2);
                if (i4 == i3) {
                    return textRunAdvances;
                }
                b = this.mLevels[i4];
                i2 = i4;
            }
            i4++;
        }
    }

    float addStyleRun(TextPaint textPaint, MetricAffectingSpan[] metricAffectingSpanArr, int i, Paint.FontMetricsInt fontMetricsInt) {
        float size;
        TextPaint textPaint2 = this.mWorkPaint;
        textPaint2.set(textPaint);
        textPaint2.baselineShift = 0;
        ReplacementSpan replacementSpan = null;
        for (MetricAffectingSpan metricAffectingSpan : metricAffectingSpanArr) {
            if (metricAffectingSpan instanceof ReplacementSpan) {
                replacementSpan = (ReplacementSpan) metricAffectingSpan;
            } else {
                metricAffectingSpan.updateMeasureState(textPaint2);
            }
        }
        if (replacementSpan == null) {
            size = addStyleRun(textPaint2, i, fontMetricsInt);
        } else {
            CharSequence charSequence = this.mText;
            int i2 = this.mTextStart;
            int i3 = this.mPos;
            size = replacementSpan.getSize(textPaint2, charSequence, i2 + i3, i2 + i3 + i, fontMetricsInt);
            float[] fArr = this.mWidths;
            int i4 = this.mPos;
            fArr[i4] = size;
            int i5 = i4 + i;
            for (int i6 = i4 + 1; i6 < i5; i6++) {
                fArr[i6] = 0.0f;
            }
            this.mPos += i;
        }
        if (fontMetricsInt != null) {
            if (textPaint2.baselineShift < 0) {
                fontMetricsInt.ascent += textPaint2.baselineShift;
                fontMetricsInt.top += textPaint2.baselineShift;
            } else {
                fontMetricsInt.descent += textPaint2.baselineShift;
                fontMetricsInt.bottom += textPaint2.baselineShift;
            }
        }
        return size;
    }

    int breakText(int i, boolean z, float f) {
        float[] fArr = this.mWidths;
        if (z) {
            int i2 = 0;
            while (i2 < i) {
                f -= fArr[i2];
                if (f < 0.0f) {
                    break;
                }
                i2++;
            }
            while (i2 > 0 && this.mChars[i2 - 1] == ' ') {
                i2--;
            }
            return i2;
        }
        int i3 = i - 1;
        int i4 = i3;
        while (i4 >= 0) {
            f -= fArr[i4];
            if (f < 0.0f) {
                break;
            }
            i4--;
        }
        while (i4 < i3) {
            int i5 = i4 + 1;
            if (this.mChars[i5] != ' ') {
                break;
            }
            i4 = i5;
        }
        return (i - i4) - 1;
    }

    float measure(int i, int i2) {
        float[] fArr = this.mWidths;
        float f = 0.0f;
        while (i < i2) {
            f += fArr[i];
            i++;
        }
        return f;
    }
}
