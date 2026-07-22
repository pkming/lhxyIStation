package android.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;

/* JADX INFO: loaded from: classes.dex */
class TextLine {
    private static final boolean DEBUG = false;
    private static final int TAB_INCREMENT = 20;
    private static final TextLine[] sCached = new TextLine[3];
    private char[] mChars;
    private boolean mCharsValid;
    private int mDir;
    private Layout.Directions mDirections;
    private boolean mHasTabs;
    private int mLen;
    private TextPaint mPaint;
    private Spanned mSpanned;
    private int mStart;
    private Layout.TabStops mTabs;
    private CharSequence mText;
    private final TextPaint mWorkPaint = new TextPaint();
    private final SpanSet<MetricAffectingSpan> mMetricAffectingSpanSpanSet = new SpanSet<>(MetricAffectingSpan.class);
    private final SpanSet<CharacterStyle> mCharacterStyleSpanSet = new SpanSet<>(CharacterStyle.class);
    private final SpanSet<ReplacementSpan> mReplacementSpanSpanSet = new SpanSet<>(ReplacementSpan.class);

    TextLine() {
    }

    static TextLine obtain() {
        TextLine[] textLineArr;
        TextLine[] textLineArr2 = sCached;
        synchronized (textLineArr2) {
            int length = textLineArr2.length;
            do {
                length--;
                if (length >= 0) {
                    textLineArr = sCached;
                } else {
                    return new TextLine();
                }
            } while (textLineArr[length] == null);
            TextLine textLine = textLineArr[length];
            textLineArr[length] = null;
            return textLine;
        }
    }

    static TextLine recycle(TextLine textLine) {
        textLine.mText = null;
        textLine.mPaint = null;
        textLine.mDirections = null;
        textLine.mMetricAffectingSpanSpanSet.recycle();
        textLine.mCharacterStyleSpanSet.recycle();
        textLine.mReplacementSpanSpanSet.recycle();
        synchronized (sCached) {
            int i = 0;
            while (true) {
                TextLine[] textLineArr = sCached;
                if (i >= textLineArr.length) {
                    break;
                }
                if (textLineArr[i] == null) {
                    textLineArr[i] = textLine;
                    break;
                }
                i++;
            }
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x002d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void set(android.text.TextPaint r3, java.lang.CharSequence r4, int r5, int r6, int r7, android.text.Layout.Directions r8, boolean r9, android.text.Layout.TabStops r10) {
        /*
            r2 = this;
            r2.mPaint = r3
            r2.mText = r4
            r2.mStart = r5
            int r3 = r6 - r5
            r2.mLen = r3
            r2.mDir = r7
            r2.mDirections = r8
            if (r8 == 0) goto L83
            r2.mHasTabs = r9
            r3 = 0
            r2.mSpanned = r3
            boolean r3 = r4 instanceof android.text.Spanned
            r7 = 1
            r0 = 0
            if (r3 == 0) goto L2d
            r3 = r4
            android.text.Spanned r3 = (android.text.Spanned) r3
            r2.mSpanned = r3
            android.text.SpanSet<android.text.style.ReplacementSpan> r1 = r2.mReplacementSpanSpanSet
            r1.init(r3, r5, r6)
            android.text.SpanSet<android.text.style.ReplacementSpan> r3 = r2.mReplacementSpanSpanSet
            int r3 = r3.numberOfSpans
            if (r3 <= 0) goto L2d
            r3 = r7
            goto L2e
        L2d:
            r3 = r0
        L2e:
            if (r3 != 0) goto L39
            if (r9 != 0) goto L39
            android.text.Layout$Directions r9 = android.text.Layout.DIRS_ALL_LEFT_TO_RIGHT
            if (r8 == r9) goto L37
            goto L39
        L37:
            r8 = r0
            goto L3a
        L39:
            r8 = r7
        L3a:
            r2.mCharsValid = r8
            if (r8 == 0) goto L80
            char[] r8 = r2.mChars
            if (r8 == 0) goto L47
            int r8 = r8.length
            int r9 = r2.mLen
            if (r8 >= r9) goto L51
        L47:
            int r8 = r2.mLen
            int r8 = com.android.internal.util.ArrayUtils.idealCharArraySize(r8)
            char[] r8 = new char[r8]
            r2.mChars = r8
        L51:
            char[] r8 = r2.mChars
            android.text.TextUtils.getChars(r4, r5, r6, r8, r0)
            if (r3 == 0) goto L80
            char[] r3 = r2.mChars
            r4 = r5
        L5b:
            if (r4 >= r6) goto L80
            android.text.SpanSet<android.text.style.ReplacementSpan> r8 = r2.mReplacementSpanSpanSet
            int r8 = r8.getNextTransition(r4, r6)
            android.text.SpanSet<android.text.style.ReplacementSpan> r9 = r2.mReplacementSpanSpanSet
            boolean r9 = r9.hasSpansIntersecting(r4, r8)
            if (r9 == 0) goto L7e
            int r4 = r4 - r5
            r9 = 65532(0xfffc, float:9.183E-41)
            r3[r4] = r9
            int r4 = r4 + r7
            int r9 = r8 - r5
        L74:
            if (r4 >= r9) goto L7e
            r0 = 65279(0xfeff, float:9.1475E-41)
            r3[r4] = r0
            int r4 = r4 + 1
            goto L74
        L7e:
            r4 = r8
            goto L5b
        L80:
            r2.mTabs = r10
            return
        L83:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.String r4 = "Directions cannot be null"
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.TextLine.set(android.text.TextPaint, java.lang.CharSequence, int, int, int, android.text.Layout$Directions, boolean, android.text.Layout$TabStops):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:62:0x00d1  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x00f4  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0109  */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void draw(android.graphics.Canvas r26, float r27, int r28, int r29, int r30) {
        /*
            Method dump skipped, instruction units count: 353
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.TextLine.draw(android.graphics.Canvas, float, int, int, int):void");
    }

    float metrics(Paint.FontMetricsInt fontMetricsInt) {
        return measure(this.mLen, false, fontMetricsInt);
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    float measure(int i, boolean z, Paint.FontMetricsInt fontMetricsInt) {
        Bitmap bitmap;
        int i2;
        float fMeasureRun;
        int i3 = z ? i - 1 : i;
        float f = 0.0f;
        if (i3 < 0) {
            return 0.0f;
        }
        if (!this.mHasTabs) {
            if (this.mDirections == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                return measureRun(0, i, this.mLen, false, fontMetricsInt);
            }
            if (this.mDirections == Layout.DIRS_ALL_RIGHT_TO_LEFT) {
                return measureRun(0, i, this.mLen, true, fontMetricsInt);
            }
        }
        char[] cArr = this.mChars;
        int[] iArr = this.mDirections.mDirections;
        int i4 = 0;
        while (i4 < iArr.length) {
            int i5 = iArr[i4];
            int i6 = i4 + 1;
            int i7 = (iArr[i6] & 67108863) + i5;
            int i8 = this.mLen;
            int i9 = i7 > i8 ? i8 : i7;
            boolean z2 = (iArr[i6] & 67108864) != 0;
            float width = f;
            int i10 = i5;
            int i11 = this.mHasTabs ? i5 : i9;
            while (i11 <= i9) {
                Bitmap bitmapFromAndroidPua = null;
                if (!this.mHasTabs || i11 >= i9) {
                    bitmap = null;
                    i2 = 0;
                } else {
                    char c = cArr[i11];
                    int i12 = c;
                    if (c >= 55296) {
                        i12 = c;
                        if (c < 56320) {
                            int i13 = i11 + 1;
                            i12 = c;
                            if (i13 < i9) {
                                int iCodePointAt = Character.codePointAt(cArr, i11);
                                if (iCodePointAt < Layout.MIN_EMOJI || iCodePointAt > Layout.MAX_EMOJI) {
                                    i12 = iCodePointAt;
                                    if (iCodePointAt > 65535) {
                                        i11 = i13;
                                        i11++;
                                    }
                                } else {
                                    bitmapFromAndroidPua = Layout.EMOJI_FACTORY.getBitmapFromAndroidPua(iCodePointAt);
                                    i12 = iCodePointAt;
                                }
                            }
                        }
                    }
                    bitmap = bitmapFromAndroidPua;
                    i2 = i12;
                }
                if (i11 == i9 || i2 == 9 || bitmap != null) {
                    boolean z3 = i3 >= i10 && i3 < i11;
                    boolean z4 = (this.mDir == -1) == z2;
                    if (z3 && z4) {
                        fMeasureRun = measureRun(i10, i, i11, z2, fontMetricsInt);
                    } else {
                        int i14 = i2;
                        int i15 = i11;
                        int i16 = i10;
                        float fMeasureRun2 = measureRun(i10, i11, i11, z2, fontMetricsInt);
                        if (!z4) {
                            fMeasureRun2 = -fMeasureRun2;
                        }
                        width += fMeasureRun2;
                        if (z3) {
                            fMeasureRun = measureRun(i16, i, i15, z2, null);
                        } else {
                            if (i14 == 9) {
                                if (i == i15) {
                                    return width;
                                }
                                int i17 = this.mDir;
                                width = i17 * nextTab(i17 * width);
                                if (i3 == i15) {
                                    return width;
                                }
                            }
                            if (bitmap != null) {
                                width += this.mDir * ((bitmap.getWidth() * (-ascent(i15))) / bitmap.getHeight());
                                i11 = i15 + 1;
                            } else {
                                i11 = i15;
                            }
                            i10 = i11 + 1;
                        }
                    }
                    return width + fMeasureRun;
                }
                i11++;
            }
            i4 += 2;
            f = width;
        }
        return f;
    }

    private float drawRun(Canvas canvas, int i, int i2, boolean z, float f, int i3, int i4, int i5, boolean z2) {
        if ((this.mDir == 1) == z) {
            float f2 = -measureRun(i, i2, i2, z, null);
            handleRun(i, i2, i2, z, canvas, f + f2, i3, i4, i5, null, false);
            return f2;
        }
        return handleRun(i, i2, i2, z, canvas, f, i3, i4, i5, null, z2);
    }

    private float measureRun(int i, int i2, int i3, boolean z, Paint.FontMetricsInt fontMetricsInt) {
        return handleRun(i, i2, i3, z, null, 0.0f, 0, 0, 0, fontMetricsInt, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:0x012b, code lost:
    
        if (r10 != (-1)) goto L103;
     */
    /* JADX WARN: Code restructure failed: missing block: B:101:0x012d, code lost:
    
        if (r1 == false) goto L93;
     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x0135, code lost:
    
        if (r10 > r9) goto L128;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x0137, code lost:
    
        if (r1 == false) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x013a, code lost:
    
        r9 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x013c, code lost:
    
        return r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:127:?, code lost:
    
        return r22.mLen + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:128:?, code lost:
    
        return r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x012a, code lost:
    
        r0 = -1;
     */
    /* JADX WARN: Removed duplicated region for block: B:61:0x00bd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    int getOffsetToLeftRightOf(int r23, boolean r24) {
        /*
            Method dump skipped, instruction units count: 317
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.TextLine.getOffsetToLeftRightOf(int, boolean):int");
    }

    private int getOffsetBeforeAfter(int i, int i2, int i3, boolean z, int i4, boolean z2) {
        int i5;
        if (i >= 0) {
            if (i4 != (z2 ? this.mLen : 0)) {
                TextPaint textPaint = this.mWorkPaint;
                textPaint.set(this.mPaint);
                if (this.mSpanned != null) {
                    int i6 = z2 ? i4 + 1 : i4;
                    int i7 = this.mStart + i3;
                    while (true) {
                        int iNextSpanTransition = this.mSpanned.nextSpanTransition(this.mStart + i2, i7, MetricAffectingSpan.class);
                        i5 = this.mStart;
                        i3 = iNextSpanTransition - i5;
                        if (i3 >= i6) {
                            break;
                        }
                        i2 = i3;
                    }
                    MetricAffectingSpan[] metricAffectingSpanArr = (MetricAffectingSpan[]) TextUtils.removeEmptySpans((MetricAffectingSpan[]) this.mSpanned.getSpans(i5 + i2, i5 + i3, MetricAffectingSpan.class), this.mSpanned, MetricAffectingSpan.class);
                    if (metricAffectingSpanArr.length > 0) {
                        ReplacementSpan replacementSpan = null;
                        for (MetricAffectingSpan metricAffectingSpan : metricAffectingSpanArr) {
                            if (metricAffectingSpan instanceof ReplacementSpan) {
                                replacementSpan = (ReplacementSpan) metricAffectingSpan;
                            } else {
                                metricAffectingSpan.updateMeasureState(textPaint);
                            }
                        }
                        if (replacementSpan != null) {
                            return z2 ? i3 : i2;
                        }
                    }
                }
                int i8 = i2;
                int i9 = z2 ? 0 : 2;
                if (this.mCharsValid) {
                    return textPaint.getTextRunCursor(this.mChars, i8, i3 - i8, z ? 1 : 0, i4, i9);
                }
                CharSequence charSequence = this.mText;
                int i10 = this.mStart;
                return textPaint.getTextRunCursor(charSequence, i8 + i10, i10 + i3, z ? 1 : 0, i10 + i4, i9) - this.mStart;
            }
        }
        if (z2) {
            return TextUtils.getOffsetAfter(this.mText, i4 + this.mStart) - this.mStart;
        }
        return TextUtils.getOffsetBefore(this.mText, i4 + this.mStart) - this.mStart;
    }

    private static void expandMetricsFromPaint(Paint.FontMetricsInt fontMetricsInt, TextPaint textPaint) {
        int i = fontMetricsInt.top;
        int i2 = fontMetricsInt.ascent;
        int i3 = fontMetricsInt.descent;
        int i4 = fontMetricsInt.bottom;
        int i5 = fontMetricsInt.leading;
        textPaint.getFontMetricsInt(fontMetricsInt);
        updateMetrics(fontMetricsInt, i, i2, i3, i4, i5);
    }

    static void updateMetrics(Paint.FontMetricsInt fontMetricsInt, int i, int i2, int i3, int i4, int i5) {
        fontMetricsInt.top = Math.min(fontMetricsInt.top, i);
        fontMetricsInt.ascent = Math.min(fontMetricsInt.ascent, i2);
        fontMetricsInt.descent = Math.max(fontMetricsInt.descent, i3);
        fontMetricsInt.bottom = Math.max(fontMetricsInt.bottom, i4);
        fontMetricsInt.leading = Math.max(fontMetricsInt.leading, i5);
    }

    private float handleText(TextPaint textPaint, int i, int i2, int i3, int i4, boolean z, Canvas canvas, float f, int i5, int i6, int i7, Paint.FontMetricsInt fontMetricsInt, boolean z2) {
        if (fontMetricsInt != null) {
            expandMetricsFromPaint(fontMetricsInt, textPaint);
        }
        int i8 = i2 - i;
        float textRunAdvances = 0.0f;
        if (i8 == 0) {
            return 0.0f;
        }
        int i9 = i4 - i3;
        if (z2 || (canvas != null && (textPaint.bgColor != 0 || textPaint.underlineColor != 0 || z))) {
            if (this.mCharsValid) {
                textRunAdvances = textPaint.getTextRunAdvances(this.mChars, i, i8, i3, i9, z ? 1 : 0, (float[]) null, 0);
            } else {
                int i10 = this.mStart;
                textRunAdvances = textPaint.getTextRunAdvances(this.mText, i10 + i, i10 + i2, i10 + i3, i10 + i4, z ? 1 : 0, (float[]) null, 0);
            }
        }
        float f2 = textRunAdvances;
        if (canvas != null) {
            float f3 = z ? f - f2 : f;
            if (textPaint.bgColor != 0) {
                int color = textPaint.getColor();
                Paint.Style style = textPaint.getStyle();
                textPaint.setColor(textPaint.bgColor);
                textPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(f3, i5, f3 + f2, i7, textPaint);
                textPaint.setStyle(style);
                textPaint.setColor(color);
            }
            if (textPaint.underlineColor != 0) {
                float textSize = (textPaint.getTextSize() * 0.11111111f) + i6 + textPaint.baselineShift;
                int color2 = textPaint.getColor();
                Paint.Style style2 = textPaint.getStyle();
                boolean zIsAntiAlias = textPaint.isAntiAlias();
                textPaint.setStyle(Paint.Style.FILL);
                textPaint.setAntiAlias(true);
                textPaint.setColor(textPaint.underlineColor);
                canvas.drawRect(f3, textSize, f3 + f2, textSize + textPaint.underlineThickness, textPaint);
                textPaint.setStyle(style2);
                textPaint.setColor(color2);
                textPaint.setAntiAlias(zIsAntiAlias);
            }
            drawTextRun(canvas, textPaint, i, i2, i3, i4, z, f3, i6 + textPaint.baselineShift);
        }
        return z ? -f2 : f2;
    }

    private float handleReplacement(ReplacementSpan replacementSpan, TextPaint textPaint, int i, int i2, boolean z, Canvas canvas, float f, int i3, int i4, int i5, Paint.FontMetricsInt fontMetricsInt, boolean z2) {
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        float f2;
        float f3;
        int i11 = this.mStart;
        int i12 = i11 + i;
        int i13 = i11 + i2;
        if (z2 || (canvas != null && z)) {
            boolean z3 = fontMetricsInt != null;
            if (z3) {
                int i14 = fontMetricsInt.top;
                i6 = i14;
                i7 = fontMetricsInt.ascent;
                i8 = fontMetricsInt.descent;
                i9 = fontMetricsInt.bottom;
                i10 = fontMetricsInt.leading;
            } else {
                i6 = 0;
                i7 = 0;
                i8 = 0;
                i9 = 0;
                i10 = 0;
            }
            float size = replacementSpan.getSize(textPaint, this.mText, i12, i13, fontMetricsInt);
            if (z3) {
                f2 = size;
                updateMetrics(fontMetricsInt, i6, i7, i8, i9, i10);
            } else {
                f2 = size;
            }
            f3 = f2;
        } else {
            f3 = 0.0f;
        }
        if (canvas != null) {
            replacementSpan.draw(canvas, this.mText, i12, i13, z ? f - f3 : f, i3, i4, i5, textPaint);
        }
        return z ? -f3 : f3;
    }

    private float handleRun(int i, int i2, int i3, boolean z, Canvas canvas, float f, int i4, int i5, int i6, Paint.FontMetricsInt fontMetricsInt, boolean z2) {
        int i7;
        if (i == i2) {
            TextPaint textPaint = this.mWorkPaint;
            textPaint.set(this.mPaint);
            if (fontMetricsInt == null) {
                return 0.0f;
            }
            expandMetricsFromPaint(fontMetricsInt, textPaint);
            return 0.0f;
        }
        Spanned spanned = this.mSpanned;
        if (spanned == null) {
            TextPaint textPaint2 = this.mWorkPaint;
            textPaint2.set(this.mPaint);
            return handleText(textPaint2, i, i2, i, i3, z, canvas, f, i4, i5, i6, fontMetricsInt, z2 || i2 < i2);
        }
        SpanSet<MetricAffectingSpan> spanSet = this.mMetricAffectingSpanSpanSet;
        int i8 = this.mStart;
        spanSet.init(spanned, i8 + i, i8 + i3);
        SpanSet<CharacterStyle> spanSet2 = this.mCharacterStyleSpanSet;
        Spanned spanned2 = this.mSpanned;
        int i9 = this.mStart;
        spanSet2.init(spanned2, i9 + i, i9 + i3);
        float fHandleText = f;
        int i10 = i;
        while (i10 < i2) {
            TextPaint textPaint3 = this.mWorkPaint;
            textPaint3.set(this.mPaint);
            SpanSet<MetricAffectingSpan> spanSet3 = this.mMetricAffectingSpanSpanSet;
            int i11 = this.mStart;
            int nextTransition = spanSet3.getNextTransition(i11 + i10, i11 + i3) - this.mStart;
            int iMin = Math.min(nextTransition, i2);
            ReplacementSpan replacementSpan = null;
            for (int i12 = 0; i12 < this.mMetricAffectingSpanSpanSet.numberOfSpans; i12++) {
                if (this.mMetricAffectingSpanSpanSet.spanStarts[i12] < this.mStart + iMin && this.mMetricAffectingSpanSpanSet.spanEnds[i12] > this.mStart + i10) {
                    MetricAffectingSpan metricAffectingSpan = this.mMetricAffectingSpanSpanSet.spans[i12];
                    if (metricAffectingSpan instanceof ReplacementSpan) {
                        replacementSpan = (ReplacementSpan) metricAffectingSpan;
                    } else {
                        metricAffectingSpan.updateDrawState(textPaint3);
                    }
                }
            }
            if (replacementSpan != null) {
                i7 = nextTransition;
                fHandleText += handleReplacement(replacementSpan, textPaint3, i10, iMin, z, canvas, fHandleText, i4, i5, i6, fontMetricsInt, z2 || iMin < i2);
            } else {
                i7 = nextTransition;
                int i13 = i10;
                while (i13 < iMin) {
                    SpanSet<CharacterStyle> spanSet4 = this.mCharacterStyleSpanSet;
                    int i14 = this.mStart;
                    int nextTransition2 = spanSet4.getNextTransition(i14 + i13, i14 + iMin) - this.mStart;
                    textPaint3.set(this.mPaint);
                    for (int i15 = 0; i15 < this.mCharacterStyleSpanSet.numberOfSpans; i15++) {
                        if (this.mCharacterStyleSpanSet.spanStarts[i15] < this.mStart + nextTransition2 && this.mCharacterStyleSpanSet.spanEnds[i15] > this.mStart + i13) {
                            this.mCharacterStyleSpanSet.spans[i15].updateDrawState(textPaint3);
                        }
                    }
                    fHandleText += handleText(textPaint3, i13, nextTransition2, i10, i7, z, canvas, fHandleText, i4, i5, i6, fontMetricsInt, z2 || nextTransition2 < i2);
                    iMin = iMin;
                    i13 = nextTransition2;
                    textPaint3 = textPaint3;
                    i10 = i10;
                }
            }
            i10 = i7;
        }
        return fHandleText - f;
    }

    private void drawTextRun(Canvas canvas, TextPaint textPaint, int i, int i2, int i3, int i4, boolean z, float f, int i5) {
        if (this.mCharsValid) {
            canvas.drawTextRun(this.mChars, i, i2 - i, i3, i4 - i3, f, i5, z ? 1 : 0, textPaint);
        } else {
            int i6 = this.mStart;
            canvas.drawTextRun(this.mText, i6 + i, i6 + i2, i6 + i3, i6 + i4, f, i5, z ? 1 : 0, textPaint);
        }
    }

    float ascent(int i) {
        Spanned spanned = this.mSpanned;
        if (spanned == null) {
            return this.mPaint.ascent();
        }
        int i2 = i + this.mStart;
        MetricAffectingSpan[] metricAffectingSpanArr = (MetricAffectingSpan[]) spanned.getSpans(i2, i2 + 1, MetricAffectingSpan.class);
        if (metricAffectingSpanArr.length == 0) {
            return this.mPaint.ascent();
        }
        TextPaint textPaint = this.mWorkPaint;
        textPaint.set(this.mPaint);
        for (MetricAffectingSpan metricAffectingSpan : metricAffectingSpanArr) {
            metricAffectingSpan.updateMeasureState(textPaint);
        }
        return textPaint.ascent();
    }

    float nextTab(float f) {
        Layout.TabStops tabStops = this.mTabs;
        if (tabStops != null) {
            return tabStops.nextTab(f);
        }
        return Layout.TabStops.nextDefaultStop(f, 20);
    }
}
