package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.android.internal.util.ArrayUtils;
import java.lang.reflect.Array;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;

/* JADX INFO: loaded from: classes.dex */
public class SpannableStringBuilder implements CharSequence, GetChars, Spannable, Editable, Appendable, GraphicsOperations {
    private static final int END_MASK = 15;
    private static final int MARK = 1;
    private static final InputFilter[] NO_FILTERS = new InputFilter[0];
    private static final int PARAGRAPH = 3;
    private static final int POINT = 2;
    private static final int SPAN_END_AT_END = 32768;
    private static final int SPAN_END_AT_START = 16384;
    private static final int SPAN_START_AT_END = 8192;
    private static final int SPAN_START_AT_START = 4096;
    private static final int SPAN_START_END_MASK = 61440;
    private static final int START_MASK = 240;
    private static final int START_SHIFT = 4;
    private InputFilter[] mFilters;
    private int mGapLength;
    private int mGapStart;
    private int mSpanCount;
    private int mSpanCountBeforeAdd;
    private int[] mSpanEnds;
    private int[] mSpanFlags;
    private int[] mSpanStarts;
    private Object[] mSpans;
    private char[] mText;

    public SpannableStringBuilder() {
        this("");
    }

    public SpannableStringBuilder(CharSequence charSequence) {
        this(charSequence, 0, charSequence.length());
    }

    public SpannableStringBuilder(CharSequence charSequence, int i, int i2) {
        this.mFilters = NO_FILTERS;
        int i3 = i2 - i;
        if (i3 < 0) {
            throw new StringIndexOutOfBoundsException();
        }
        int iIdealCharArraySize = ArrayUtils.idealCharArraySize(i3 + 1);
        char[] cArr = new char[iIdealCharArraySize];
        this.mText = cArr;
        this.mGapStart = i3;
        this.mGapLength = iIdealCharArraySize - i3;
        TextUtils.getChars(charSequence, i, i2, cArr, 0);
        this.mSpanCount = 0;
        int iIdealIntArraySize = ArrayUtils.idealIntArraySize(0);
        this.mSpans = new Object[iIdealIntArraySize];
        this.mSpanStarts = new int[iIdealIntArraySize];
        this.mSpanEnds = new int[iIdealIntArraySize];
        this.mSpanFlags = new int[iIdealIntArraySize];
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            Object[] spans = spanned.getSpans(i, i2, Object.class);
            for (int i4 = 0; i4 < spans.length; i4++) {
                if (!(spans[i4] instanceof NoCopySpan)) {
                    int spanStart = spanned.getSpanStart(spans[i4]) - i;
                    int spanEnd = spanned.getSpanEnd(spans[i4]) - i;
                    int spanFlags = spanned.getSpanFlags(spans[i4]);
                    spanStart = spanStart < 0 ? 0 : spanStart;
                    int i5 = spanStart > i3 ? i3 : spanStart;
                    spanEnd = spanEnd < 0 ? 0 : spanEnd;
                    setSpan(false, spans[i4], i5, spanEnd > i3 ? i3 : spanEnd, spanFlags);
                }
            }
        }
    }

    public static SpannableStringBuilder valueOf(CharSequence charSequence) {
        if (charSequence instanceof SpannableStringBuilder) {
            return (SpannableStringBuilder) charSequence;
        }
        return new SpannableStringBuilder(charSequence);
    }

    @Override // java.lang.CharSequence
    public char charAt(int i) {
        int length = length();
        if (i < 0) {
            throw new IndexOutOfBoundsException("charAt: " + i + " < 0");
        }
        if (i >= length) {
            throw new IndexOutOfBoundsException("charAt: " + i + " >= length " + length);
        }
        if (i >= this.mGapStart) {
            return this.mText[i + this.mGapLength];
        }
        return this.mText[i];
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.mText.length - this.mGapLength;
    }

    private void resizeFor(int i) {
        int length = this.mText.length;
        int iIdealCharArraySize = ArrayUtils.idealCharArraySize(i + 1);
        int i2 = iIdealCharArraySize - length;
        if (i2 == 0) {
            return;
        }
        char[] cArr = new char[iIdealCharArraySize];
        System.arraycopy(this.mText, 0, cArr, 0, this.mGapStart);
        int i3 = length - (this.mGapStart + this.mGapLength);
        System.arraycopy(this.mText, length - i3, cArr, iIdealCharArraySize - i3, i3);
        this.mText = cArr;
        int i4 = this.mGapLength + i2;
        this.mGapLength = i4;
        if (i4 < 1) {
            new Exception("mGapLength < 1").printStackTrace();
        }
        for (int i5 = 0; i5 < this.mSpanCount; i5++) {
            int[] iArr = this.mSpanStarts;
            int i6 = iArr[i5];
            int i7 = this.mGapStart;
            if (i6 > i7) {
                iArr[i5] = iArr[i5] + i2;
            }
            int[] iArr2 = this.mSpanEnds;
            if (iArr2[i5] > i7) {
                iArr2[i5] = iArr2[i5] + i2;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x005a  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x005f  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x0063  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void moveGapTo(int r11) {
        /*
            r10 = this;
            int r0 = r10.mGapStart
            if (r11 != r0) goto L5
            return
        L5:
            int r0 = r10.length()
            r1 = 0
            if (r11 != r0) goto Le
            r0 = 1
            goto Lf
        Le:
            r0 = r1
        Lf:
            int r2 = r10.mGapStart
            if (r11 >= r2) goto L1f
            int r3 = r2 - r11
            char[] r4 = r10.mText
            int r5 = r10.mGapLength
            int r2 = r2 + r5
            int r2 = r2 - r3
            java.lang.System.arraycopy(r4, r11, r4, r2, r3)
            goto L2a
        L1f:
            int r3 = r11 - r2
            char[] r4 = r10.mText
            int r5 = r10.mGapLength
            int r5 = r5 + r11
            int r5 = r5 - r3
            java.lang.System.arraycopy(r4, r5, r4, r2, r3)
        L2a:
            int r2 = r10.mSpanCount
            if (r1 >= r2) goto L7b
            int[] r2 = r10.mSpanStarts
            r3 = r2[r1]
            int[] r4 = r10.mSpanEnds
            r5 = r4[r1]
            int r6 = r10.mGapStart
            if (r3 <= r6) goto L3d
            int r7 = r10.mGapLength
            int r3 = r3 - r7
        L3d:
            r7 = 3
            r8 = 2
            if (r3 <= r11) goto L45
            int r9 = r10.mGapLength
        L43:
            int r3 = r3 + r9
            goto L58
        L45:
            if (r3 != r11) goto L58
            int[] r9 = r10.mSpanFlags
            r9 = r9[r1]
            r9 = r9 & 240(0xf0, float:3.36E-43)
            int r9 = r9 >> 4
            if (r9 == r8) goto L55
            if (r0 == 0) goto L58
            if (r9 != r7) goto L58
        L55:
            int r9 = r10.mGapLength
            goto L43
        L58:
            if (r5 <= r6) goto L5d
            int r6 = r10.mGapLength
            int r5 = r5 - r6
        L5d:
            if (r5 <= r11) goto L63
            int r6 = r10.mGapLength
        L61:
            int r5 = r5 + r6
            goto L74
        L63:
            if (r5 != r11) goto L74
            int[] r6 = r10.mSpanFlags
            r6 = r6[r1]
            r6 = r6 & 15
            if (r6 == r8) goto L71
            if (r0 == 0) goto L74
            if (r6 != r7) goto L74
        L71:
            int r6 = r10.mGapLength
            goto L61
        L74:
            r2[r1] = r3
            r4[r1] = r5
            int r1 = r1 + 1
            goto L2a
        L7b:
            r10.mGapStart = r11
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.SpannableStringBuilder.moveGapTo(int):void");
    }

    @Override // android.text.Editable
    public SpannableStringBuilder insert(int i, CharSequence charSequence, int i2, int i3) {
        return replace(i, i, charSequence, i2, i3);
    }

    @Override // android.text.Editable
    public SpannableStringBuilder insert(int i, CharSequence charSequence) {
        return replace(i, i, charSequence, 0, charSequence.length());
    }

    @Override // android.text.Editable
    public SpannableStringBuilder delete(int i, int i2) {
        SpannableStringBuilder spannableStringBuilderReplace = replace(i, i2, "", 0, 0);
        if (this.mGapLength > length() * 2) {
            resizeFor(length());
        }
        return spannableStringBuilderReplace;
    }

    @Override // android.text.Editable
    public void clear() {
        replace(0, length(), "", 0, 0);
    }

    @Override // android.text.Editable
    public void clearSpans() {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            Object[] objArr = this.mSpans;
            Object obj = objArr[i];
            int i2 = this.mSpanStarts[i];
            int i3 = this.mSpanEnds[i];
            int i4 = this.mGapStart;
            if (i2 > i4) {
                i2 -= this.mGapLength;
            }
            if (i3 > i4) {
                i3 -= this.mGapLength;
            }
            this.mSpanCount = i;
            objArr[i] = null;
            sendSpanRemoved(obj, i2, i3);
        }
    }

    @Override // java.lang.Appendable
    public SpannableStringBuilder append(CharSequence charSequence) {
        int length = length();
        return replace(length, length, charSequence, 0, charSequence.length());
    }

    @Override // java.lang.Appendable
    public SpannableStringBuilder append(CharSequence charSequence, int i, int i2) {
        int length = length();
        return replace(length, length, charSequence, i, i2);
    }

    @Override // java.lang.Appendable
    public SpannableStringBuilder append(char c) {
        return append((CharSequence) String.valueOf(c));
    }

    private void change(int i, int i2, CharSequence charSequence, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int i8 = i2 - i;
        int i9 = i4 - i3;
        int i10 = i9 - i8;
        int i11 = this.mSpanCount - 1;
        while (true) {
            int i12 = 0;
            if (i11 < 0) {
                break;
            }
            int i13 = this.mSpanStarts[i11];
            int i14 = this.mGapStart;
            if (i13 > i14) {
                i13 -= this.mGapLength;
            }
            int i15 = this.mSpanEnds[i11];
            if (i15 > i14) {
                i15 -= this.mGapLength;
            }
            if ((this.mSpanFlags[i11] & 51) == 51) {
                int length = length();
                if (i13 <= i || i13 > i2) {
                    i5 = i13;
                } else {
                    int i16 = i2;
                    while (i16 < length && (i16 <= i2 || charAt(i16 - 1) != '\n')) {
                        i16++;
                    }
                    i5 = i16;
                }
                if (i15 <= i || i15 > i2) {
                    i6 = i15;
                } else {
                    int i17 = i2;
                    while (i17 < length && (i17 <= i2 || charAt(i17 - 1) != '\n')) {
                        i17++;
                    }
                    i6 = i17;
                }
                if (i5 == i13 && i6 == i15) {
                    i7 = i5;
                } else {
                    i7 = i5;
                    setSpan(false, this.mSpans[i11], i5, i6, this.mSpanFlags[i11]);
                }
                i15 = i6;
                i13 = i7;
            }
            if (i13 == i) {
                i12 = 4096;
            } else if (i13 == i2 + i10) {
                i12 = 8192;
            }
            int i18 = i12;
            if (i15 == i) {
                i18 |= 16384;
            } else if (i15 == i2 + i10) {
                i18 |= 32768;
            }
            int[] iArr = this.mSpanFlags;
            iArr[i11] = i18 | iArr[i11];
            i11--;
        }
        moveGapTo(i2);
        int i19 = this.mGapLength;
        if (i10 >= i19) {
            resizeFor((this.mText.length + i10) - i19);
        }
        boolean z = i9 == 0;
        if (i8 > 0) {
            int i20 = 0;
            while (i20 < this.mSpanCount) {
                if ((this.mSpanFlags[i20] & 33) == 33) {
                    int[] iArr2 = this.mSpanStarts;
                    if (iArr2[i20] >= i) {
                        int i21 = iArr2[i20];
                        int i22 = this.mGapStart;
                        int i23 = this.mGapLength;
                        if (i21 < i22 + i23) {
                            int[] iArr3 = this.mSpanEnds;
                            if (iArr3[i20] >= i && iArr3[i20] < i23 + i22 && (z || iArr2[i20] > i || iArr3[i20] < i22)) {
                                removeSpan(i20);
                            }
                        }
                    }
                }
                i20++;
            }
        }
        this.mGapStart += i10;
        int i24 = this.mGapLength - i10;
        this.mGapLength = i24;
        if (i24 < 1) {
            new Exception("mGapLength < 1").printStackTrace();
        }
        TextUtils.getChars(charSequence, i3, i4, this.mText, i);
        if (i8 > 0) {
            boolean z2 = this.mGapStart + this.mGapLength == this.mText.length;
            for (int i25 = 0; i25 < this.mSpanCount; i25++) {
                int i26 = (this.mSpanFlags[i25] & 240) >> 4;
                int[] iArr4 = this.mSpanStarts;
                boolean z3 = z2;
                iArr4[i25] = updatedIntervalBound(iArr4[i25], i, i10, i26, z3, z);
                int i27 = this.mSpanFlags[i25] & 15;
                int[] iArr5 = this.mSpanEnds;
                iArr5[i25] = updatedIntervalBound(iArr5[i25], i, i10, i27, z3, z);
            }
        }
        this.mSpanCountBeforeAdd = this.mSpanCount;
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            Object[] spans = spanned.getSpans(i3, i4, Object.class);
            for (int i28 = 0; i28 < spans.length; i28++) {
                int spanStart = spanned.getSpanStart(spans[i28]);
                int spanEnd = spanned.getSpanEnd(spans[i28]);
                if (spanStart < i3) {
                    spanStart = i3;
                }
                if (spanEnd > i4) {
                    spanEnd = i4;
                }
                if (getSpanStart(spans[i28]) < 0) {
                    setSpan(false, spans[i28], (spanStart - i3) + i, (spanEnd - i3) + i, spanned.getSpanFlags(spans[i28]));
                }
            }
        }
    }

    private int updatedIntervalBound(int i, int i2, int i3, int i4, boolean z, boolean z2) {
        if (i >= i2) {
            int i5 = this.mGapStart;
            int i6 = this.mGapLength;
            if (i < i5 + i6) {
                if (i4 == 2) {
                    if (z2 || i > i2) {
                        return i5 + i6;
                    }
                } else {
                    if (i4 != 3) {
                        return (z2 || i < i5 - i3) ? i2 : i5;
                    }
                    if (z) {
                        return i5 + i6;
                    }
                }
            }
        }
        return i;
    }

    private void removeSpan(int i) {
        Object[] objArr = this.mSpans;
        Object obj = objArr[i];
        int i2 = this.mSpanStarts[i];
        int i3 = this.mSpanEnds[i];
        int i4 = this.mGapStart;
        if (i2 > i4) {
            i2 -= this.mGapLength;
        }
        if (i3 > i4) {
            i3 -= this.mGapLength;
        }
        int i5 = i + 1;
        int i6 = this.mSpanCount - i5;
        System.arraycopy(objArr, i5, objArr, i, i6);
        int[] iArr = this.mSpanStarts;
        System.arraycopy(iArr, i5, iArr, i, i6);
        int[] iArr2 = this.mSpanEnds;
        System.arraycopy(iArr2, i5, iArr2, i, i6);
        int[] iArr3 = this.mSpanFlags;
        System.arraycopy(iArr3, i5, iArr3, i, i6);
        int i7 = this.mSpanCount - 1;
        this.mSpanCount = i7;
        this.mSpans[i7] = null;
        sendSpanRemoved(obj, i2, i3);
    }

    @Override // android.text.Editable
    public SpannableStringBuilder replace(int i, int i2, CharSequence charSequence) {
        return replace(i, i2, charSequence, 0, charSequence.length());
    }

    @Override // android.text.Editable
    public SpannableStringBuilder replace(int i, int i2, CharSequence charSequence, int i3, int i4) {
        int selectionEnd;
        checkRange(MSVSSConstants.WRITABLE_REPLACE, i, i2);
        int length = this.mFilters.length;
        int selectionStart = 0;
        CharSequence charSequence2 = charSequence;
        int i5 = i3;
        int length2 = i4;
        for (int i6 = 0; i6 < length; i6++) {
            CharSequence charSequenceFilter = this.mFilters[i6].filter(charSequence2, i5, length2, this, i, i2);
            if (charSequenceFilter != null) {
                charSequence2 = charSequenceFilter;
                length2 = charSequenceFilter.length();
                i5 = 0;
            }
        }
        int i7 = i2 - i;
        int i8 = length2 - i5;
        if (i7 == 0 && i8 == 0 && !hasNonExclusiveExclusiveSpanAt(charSequence2, i5)) {
            return this;
        }
        TextWatcher[] textWatcherArr = (TextWatcher[]) getSpans(i, i + i7, TextWatcher.class);
        sendBeforeTextChanged(textWatcherArr, i, i7, i8);
        boolean z = (i7 == 0 || i8 == 0) ? false : true;
        if (z) {
            selectionStart = Selection.getSelectionStart(this);
            selectionEnd = Selection.getSelectionEnd(this);
        } else {
            selectionEnd = 0;
        }
        int i9 = selectionEnd;
        change(i, i2, charSequence2, i5, length2);
        if (z) {
            if (selectionStart > i && selectionStart < i2) {
                int i10 = i + (((selectionStart - i) * i8) / i7);
                setSpan(false, Selection.SELECTION_START, i10, i10, 34);
            }
            if (i9 > i && i9 < i2) {
                int i11 = i + (((i9 - i) * i8) / i7);
                setSpan(false, Selection.SELECTION_END, i11, i11, 34);
            }
        }
        sendTextChanged(textWatcherArr, i, i7, i8);
        sendAfterTextChanged(textWatcherArr);
        sendToSpanWatchers(i, i2, i8 - i7);
        return this;
    }

    private static boolean hasNonExclusiveExclusiveSpanAt(CharSequence charSequence, int i) {
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            for (Object obj : spanned.getSpans(i, i, Object.class)) {
                if (spanned.getSpanFlags(obj) != 33) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0044  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0069 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void sendToSpanWatchers(int r12, int r13, int r14) {
        /*
            r11 = this;
            r0 = 0
            r1 = r0
        L2:
            int r2 = r11.mSpanCountBeforeAdd
            if (r1 >= r2) goto L76
            int[] r2 = r11.mSpanStarts
            r2 = r2[r1]
            int[] r3 = r11.mSpanEnds
            r3 = r3[r1]
            int r4 = r11.mGapStart
            if (r2 <= r4) goto L15
            int r5 = r11.mGapLength
            int r2 = r2 - r5
        L15:
            r9 = r2
            if (r3 <= r4) goto L1b
            int r2 = r11.mGapLength
            int r3 = r3 - r2
        L1b:
            r10 = r3
            int[] r2 = r11.mSpanFlags
            r2 = r2[r1]
            int r3 = r13 + r14
            r4 = 1
            if (r9 <= r3) goto L2c
            if (r14 == 0) goto L40
            int r5 = r9 - r14
            r7 = r5
            r5 = r4
            goto L42
        L2c:
            if (r9 < r12) goto L40
            if (r9 != r12) goto L36
            r5 = r2 & 4096(0x1000, float:5.74E-42)
            r6 = 4096(0x1000, float:5.74E-42)
            if (r5 == r6) goto L40
        L36:
            if (r9 != r3) goto L3e
            r5 = r2 & 8192(0x2000, float:1.14794E-41)
            r6 = 8192(0x2000, float:1.14794E-41)
            if (r5 == r6) goto L40
        L3e:
            r5 = r4
            goto L41
        L40:
            r5 = r0
        L41:
            r7 = r9
        L42:
            if (r10 <= r3) goto L4a
            if (r14 == 0) goto L5d
            int r2 = r10 - r14
            r8 = r2
            goto L5f
        L4a:
            if (r10 < r12) goto L5d
            if (r10 != r12) goto L54
            r6 = r2 & 16384(0x4000, float:2.2959E-41)
            r8 = 16384(0x4000, float:2.2959E-41)
            if (r6 == r8) goto L5d
        L54:
            if (r10 != r3) goto L5e
            r3 = 32768(0x8000, float:4.5918E-41)
            r2 = r2 & r3
            if (r2 == r3) goto L5d
            goto L5e
        L5d:
            r4 = r5
        L5e:
            r8 = r10
        L5f:
            if (r4 == 0) goto L69
            java.lang.Object[] r2 = r11.mSpans
            r6 = r2[r1]
            r5 = r11
            r5.sendSpanChanged(r6, r7, r8, r9, r10)
        L69:
            int[] r2 = r11.mSpanFlags
            r3 = r2[r1]
            r4 = -61441(0xffffffffffff0fff, float:NaN)
            r3 = r3 & r4
            r2[r1] = r3
            int r1 = r1 + 1
            goto L2
        L76:
            int r12 = r11.mSpanCount
            if (r2 >= r12) goto L98
            int[] r12 = r11.mSpanStarts
            r12 = r12[r2]
            int[] r13 = r11.mSpanEnds
            r13 = r13[r2]
            int r14 = r11.mGapStart
            if (r12 <= r14) goto L89
            int r0 = r11.mGapLength
            int r12 = r12 - r0
        L89:
            if (r13 <= r14) goto L8e
            int r14 = r11.mGapLength
            int r13 = r13 - r14
        L8e:
            java.lang.Object[] r14 = r11.mSpans
            r14 = r14[r2]
            r11.sendSpanAdded(r14, r12, r13)
            int r2 = r2 + 1
            goto L76
        L98:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.SpannableStringBuilder.sendToSpanWatchers(int, int, int):void");
    }

    @Override // android.text.Spannable
    public void setSpan(Object obj, int i, int i2, int i3) {
        setSpan(true, obj, i, i2, i3);
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x007c  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0080  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x00d3  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x011b  */
    /* JADX WARN: Removed duplicated region for block: B:76:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setSpan(boolean r16, java.lang.Object r17, int r18, int r19, int r20) {
        /*
            Method dump skipped, instruction units count: 287
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.SpannableStringBuilder.setSpan(boolean, java.lang.Object, int, int, int):void");
    }

    @Override // android.text.Spannable
    public void removeSpan(Object obj) {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            if (this.mSpans[i] == obj) {
                removeSpan(i);
                return;
            }
        }
    }

    @Override // android.text.Spanned
    public int getSpanStart(Object obj) {
        int i = this.mSpanCount;
        Object[] objArr = this.mSpans;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            if (objArr[i2] == obj) {
                int i3 = this.mSpanStarts[i2];
                return i3 > this.mGapStart ? i3 - this.mGapLength : i3;
            }
        }
        return -1;
    }

    @Override // android.text.Spanned
    public int getSpanEnd(Object obj) {
        int i = this.mSpanCount;
        Object[] objArr = this.mSpans;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            if (objArr[i2] == obj) {
                int i3 = this.mSpanEnds[i2];
                return i3 > this.mGapStart ? i3 - this.mGapLength : i3;
            }
        }
        return -1;
    }

    @Override // android.text.Spanned
    public int getSpanFlags(Object obj) {
        int i = this.mSpanCount;
        Object[] objArr = this.mSpans;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            if (objArr[i2] == obj) {
                return this.mSpanFlags[i2];
            }
        }
        return 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.text.Spanned
    public <T> T[] getSpans(int i, int i2, Class<T> cls) {
        int[] iArr;
        int i3 = i;
        if (cls == null) {
            return (T[]) ArrayUtils.emptyArray(cls);
        }
        int i4 = this.mSpanCount;
        Object[] objArr = this.mSpans;
        int[] iArr2 = this.mSpanStarts;
        int[] iArr3 = this.mSpanEnds;
        int[] iArr4 = this.mSpanFlags;
        int i5 = this.mGapStart;
        int i6 = this.mGapLength;
        Object[] objArr2 = (T[]) null;
        Object obj = null;
        int i7 = 0;
        int i8 = 0;
        while (i7 < i4) {
            int i9 = iArr2[i7];
            if (i9 > i5) {
                i9 -= i6;
            }
            if (i9 > i2) {
                iArr = iArr2;
            } else {
                iArr = iArr2;
                int i10 = iArr3[i7];
                if (i10 > i5) {
                    i10 -= i6;
                }
                if (i10 >= i3 && ((i9 == i10 || i3 == i2 || (i9 != i2 && i10 != i3)) && cls.isInstance(objArr[i7]))) {
                    if (i8 == 0) {
                        i8++;
                        obj = objArr[i7];
                    } else {
                        if (i8 == 1) {
                            objArr2 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, (i4 - i7) + 1));
                            objArr2[0] = obj;
                        }
                        int i11 = iArr4[i7] & Spanned.SPAN_PRIORITY;
                        if (i11 != 0) {
                            int i12 = 0;
                            while (i12 < i8 && i11 <= (getSpanFlags(objArr2[i12]) & Spanned.SPAN_PRIORITY)) {
                                i12++;
                            }
                            System.arraycopy(objArr2, i12, objArr2, i12 + 1, i8 - i12);
                            objArr2[i12] = objArr[i7];
                            i8++;
                        } else {
                            objArr2[i8] = objArr[i7];
                            i8++;
                        }
                    }
                }
            }
            i7++;
            i3 = i;
            iArr2 = iArr;
        }
        if (i8 == 0) {
            return (T[]) ArrayUtils.emptyArray(cls);
        }
        if (i8 == 1) {
            T[] tArr = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, 1));
            tArr[0] = obj;
            return tArr;
        }
        if (i8 == objArr2.length) {
            return (T[]) objArr2;
        }
        T[] tArr2 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, i8));
        System.arraycopy(objArr2, 0, tArr2, 0, i8);
        return tArr2;
    }

    @Override // android.text.Spanned
    public int nextSpanTransition(int i, int i2, Class cls) {
        int i3 = this.mSpanCount;
        Object[] objArr = this.mSpans;
        int[] iArr = this.mSpanStarts;
        int[] iArr2 = this.mSpanEnds;
        int i4 = this.mGapStart;
        int i5 = this.mGapLength;
        if (cls == null) {
            cls = Object.class;
        }
        for (int i6 = 0; i6 < i3; i6++) {
            int i7 = iArr[i6];
            int i8 = iArr2[i6];
            if (i7 > i4) {
                i7 -= i5;
            }
            if (i8 > i4) {
                i8 -= i5;
            }
            if (i7 > i && i7 < i2 && cls.isInstance(objArr[i6])) {
                i2 = i7;
            }
            if (i8 > i && i8 < i2 && cls.isInstance(objArr[i6])) {
                i2 = i8;
            }
        }
        return i2;
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int i, int i2) {
        return new SpannableStringBuilder(this, i, i2);
    }

    @Override // android.text.GetChars
    public void getChars(int i, int i2, char[] cArr, int i3) {
        checkRange("getChars", i, i2);
        int i4 = this.mGapStart;
        if (i2 <= i4) {
            System.arraycopy(this.mText, i, cArr, i3, i2 - i);
            return;
        }
        if (i >= i4) {
            System.arraycopy(this.mText, this.mGapLength + i, cArr, i3, i2 - i);
            return;
        }
        System.arraycopy(this.mText, i, cArr, i3, i4 - i);
        char[] cArr2 = this.mText;
        int i5 = this.mGapStart;
        System.arraycopy(cArr2, this.mGapLength + i5, cArr, i3 + (i5 - i), i2 - i5);
    }

    @Override // java.lang.CharSequence
    public String toString() {
        int length = length();
        char[] cArr = new char[length];
        getChars(0, length, cArr, 0);
        return new String(cArr);
    }

    public String substring(int i, int i2) {
        char[] cArr = new char[i2 - i];
        getChars(i, i2, cArr, 0);
        return new String(cArr);
    }

    private void sendBeforeTextChanged(TextWatcher[] textWatcherArr, int i, int i2, int i3) {
        for (TextWatcher textWatcher : textWatcherArr) {
            textWatcher.beforeTextChanged(this, i, i2, i3);
        }
    }

    private void sendTextChanged(TextWatcher[] textWatcherArr, int i, int i2, int i3) {
        for (TextWatcher textWatcher : textWatcherArr) {
            textWatcher.onTextChanged(this, i, i2, i3);
        }
    }

    private void sendAfterTextChanged(TextWatcher[] textWatcherArr) {
        for (TextWatcher textWatcher : textWatcherArr) {
            textWatcher.afterTextChanged(this);
        }
    }

    private void sendSpanAdded(Object obj, int i, int i2) {
        for (SpanWatcher spanWatcher : (SpanWatcher[]) getSpans(i, i2, SpanWatcher.class)) {
            spanWatcher.onSpanAdded(this, obj, i, i2);
        }
    }

    private void sendSpanRemoved(Object obj, int i, int i2) {
        for (SpanWatcher spanWatcher : (SpanWatcher[]) getSpans(i, i2, SpanWatcher.class)) {
            spanWatcher.onSpanRemoved(this, obj, i, i2);
        }
    }

    private void sendSpanChanged(Object obj, int i, int i2, int i3, int i4) {
        for (SpanWatcher spanWatcher : (SpanWatcher[]) getSpans(Math.min(i, i3), Math.min(Math.max(i2, i4), length()), SpanWatcher.class)) {
            spanWatcher.onSpanChanged(this, obj, i, i2, i3, i4);
        }
    }

    private static String region(int i, int i2) {
        return "(" + i + " ... " + i2 + ")";
    }

    private void checkRange(String str, int i, int i2) {
        if (i2 < i) {
            throw new IndexOutOfBoundsException(str + " " + region(i, i2) + " has end before start");
        }
        int length = length();
        if (i > length || i2 > length) {
            throw new IndexOutOfBoundsException(str + " " + region(i, i2) + " ends beyond length " + length);
        }
        if (i < 0 || i2 < 0) {
            throw new IndexOutOfBoundsException(str + " " + region(i, i2) + " starts before 0");
        }
    }

    @Override // android.text.GraphicsOperations
    public void drawText(Canvas canvas, int i, int i2, float f, float f2, Paint paint) {
        checkRange("drawText", i, i2);
        int i3 = this.mGapStart;
        if (i2 <= i3) {
            canvas.drawText(this.mText, i, i2 - i, f, f2, paint);
            return;
        }
        if (i >= i3) {
            canvas.drawText(this.mText, i + this.mGapLength, i2 - i, f, f2, paint);
            return;
        }
        int i4 = i2 - i;
        char[] cArrObtain = TextUtils.obtain(i4);
        getChars(i, i2, cArrObtain, 0);
        canvas.drawText(cArrObtain, 0, i4, f, f2, paint);
        TextUtils.recycle(cArrObtain);
    }

    @Override // android.text.GraphicsOperations
    public void drawTextRun(Canvas canvas, int i, int i2, int i3, int i4, float f, float f2, int i5, Paint paint) {
        checkRange("drawTextRun", i, i2);
        int i6 = i4 - i3;
        int i7 = i2 - i;
        int i8 = this.mGapStart;
        if (i4 <= i8) {
            canvas.drawTextRun(this.mText, i, i7, i3, i6, f, f2, i5, paint);
            return;
        }
        if (i3 >= i8) {
            char[] cArr = this.mText;
            int i9 = this.mGapLength;
            canvas.drawTextRun(cArr, i + i9, i7, i3 + i9, i6, f, f2, i5, paint);
        } else {
            char[] cArrObtain = TextUtils.obtain(i6);
            getChars(i3, i4, cArrObtain, 0);
            canvas.drawTextRun(cArrObtain, i - i3, i7, 0, i6, f, f2, i5, paint);
            TextUtils.recycle(cArrObtain);
        }
    }

    @Override // android.text.GraphicsOperations
    public float measureText(int i, int i2, Paint paint) {
        checkRange("measureText", i, i2);
        int i3 = this.mGapStart;
        if (i2 <= i3) {
            return paint.measureText(this.mText, i, i2 - i);
        }
        if (i >= i3) {
            return paint.measureText(this.mText, this.mGapLength + i, i2 - i);
        }
        int i4 = i2 - i;
        char[] cArrObtain = TextUtils.obtain(i4);
        getChars(i, i2, cArrObtain, 0);
        float fMeasureText = paint.measureText(cArrObtain, 0, i4);
        TextUtils.recycle(cArrObtain);
        return fMeasureText;
    }

    @Override // android.text.GraphicsOperations
    public int getTextWidths(int i, int i2, float[] fArr, Paint paint) {
        checkRange("getTextWidths", i, i2);
        int i3 = this.mGapStart;
        if (i2 <= i3) {
            return paint.getTextWidths(this.mText, i, i2 - i, fArr);
        }
        if (i >= i3) {
            return paint.getTextWidths(this.mText, this.mGapLength + i, i2 - i, fArr);
        }
        int i4 = i2 - i;
        char[] cArrObtain = TextUtils.obtain(i4);
        getChars(i, i2, cArrObtain, 0);
        int textWidths = paint.getTextWidths(cArrObtain, 0, i4, fArr);
        TextUtils.recycle(cArrObtain);
        return textWidths;
    }

    @Override // android.text.GraphicsOperations
    public float getTextRunAdvances(int i, int i2, int i3, int i4, int i5, float[] fArr, int i6, Paint paint) {
        int i7 = i4 - i3;
        int i8 = i2 - i;
        int i9 = this.mGapStart;
        if (i2 <= i9) {
            return paint.getTextRunAdvances(this.mText, i, i8, i3, i7, i5, fArr, i6);
        }
        if (i >= i9) {
            char[] cArr = this.mText;
            int i10 = this.mGapLength;
            return paint.getTextRunAdvances(cArr, i + i10, i8, i3 + i10, i7, i5, fArr, i6);
        }
        char[] cArrObtain = TextUtils.obtain(i7);
        getChars(i3, i4, cArrObtain, 0);
        float textRunAdvances = paint.getTextRunAdvances(cArrObtain, i - i3, i8, 0, i7, i5, fArr, i6);
        TextUtils.recycle(cArrObtain);
        return textRunAdvances;
    }

    @Override // android.text.GraphicsOperations
    @Deprecated
    public int getTextRunCursor(int i, int i2, int i3, int i4, int i5, Paint paint) {
        int i6 = i2 - i;
        int i7 = this.mGapStart;
        if (i2 <= i7) {
            return paint.getTextRunCursor(this.mText, i, i6, i3, i4, i5);
        }
        if (i >= i7) {
            char[] cArr = this.mText;
            int i8 = this.mGapLength;
            return paint.getTextRunCursor(cArr, i + i8, i6, i3, i4 + i8, i5) - this.mGapLength;
        }
        char[] cArrObtain = TextUtils.obtain(i6);
        getChars(i, i2, cArrObtain, 0);
        int textRunCursor = i + paint.getTextRunCursor(cArrObtain, 0, i6, i3, i4 - i, i5);
        TextUtils.recycle(cArrObtain);
        return textRunCursor;
    }

    @Override // android.text.Editable
    public void setFilters(InputFilter[] inputFilterArr) {
        if (inputFilterArr == null) {
            throw new IllegalArgumentException();
        }
        this.mFilters = inputFilterArr;
    }

    @Override // android.text.Editable
    public InputFilter[] getFilters() {
        return this.mFilters;
    }

    public boolean equals(Object obj) {
        if ((obj instanceof Spanned) && toString().equals(obj.toString())) {
            Spanned spanned = (Spanned) obj;
            Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
            if (this.mSpanCount == spans.length) {
                for (int i = 0; i < this.mSpanCount; i++) {
                    Object obj2 = this.mSpans[i];
                    Object obj3 = spans[i];
                    if (obj2 == this) {
                        if (spanned != obj3 || getSpanStart(obj2) != spanned.getSpanStart(obj3) || getSpanEnd(obj2) != spanned.getSpanEnd(obj3) || getSpanFlags(obj2) != spanned.getSpanFlags(obj3)) {
                            return false;
                        }
                    } else if (!obj2.equals(obj3) || getSpanStart(obj2) != spanned.getSpanStart(obj3) || getSpanEnd(obj2) != spanned.getSpanEnd(obj3) || getSpanFlags(obj2) != spanned.getSpanFlags(obj3)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int iHashCode = (toString().hashCode() * 31) + this.mSpanCount;
        for (int i = 0; i < this.mSpanCount; i++) {
            Object obj = this.mSpans[i];
            if (obj != this) {
                iHashCode = (iHashCode * 31) + obj.hashCode();
            }
            iHashCode = (((((iHashCode * 31) + getSpanStart(obj)) * 31) + getSpanEnd(obj)) * 31) + getSpanFlags(obj);
        }
        return iHashCode;
    }
}
