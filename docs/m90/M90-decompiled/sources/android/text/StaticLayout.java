package android.text;

import android.graphics.Paint;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.LineHeightSpan;
import android.util.Log;
import com.android.internal.util.ArrayUtils;

/* JADX INFO: loaded from: classes.dex */
public class StaticLayout extends Layout {
    private static final char CHAR_FIRST_CJK = 11904;
    private static final int CHAR_FIRST_HIGH_SURROGATE = 55296;
    private static final char CHAR_HYPHEN = '-';
    private static final int CHAR_LAST_LOW_SURROGATE = 57343;
    private static final char CHAR_NEW_LINE = '\n';
    private static final char CHAR_SLASH = '/';
    private static final char CHAR_SPACE = ' ';
    private static final char CHAR_TAB = '\t';
    private static final char CHAR_ZWSP = 8203;
    private static final int COLUMNS_ELLIPSIZE = 5;
    private static final int COLUMNS_NORMAL = 3;
    private static final int DESCENT = 2;
    private static final int DIR = 0;
    private static final int DIR_SHIFT = 30;
    private static final int ELLIPSIS_COUNT = 4;
    private static final int ELLIPSIS_START = 3;
    private static final double EXTRA_ROUNDING = 0.5d;
    private static final int START = 0;
    private static final int START_MASK = 536870911;
    private static final int TAB = 0;
    private static final int TAB_INCREMENT = 20;
    private static final int TAB_MASK = 536870912;
    static final String TAG = "StaticLayout";
    private static final int TOP = 1;
    private int mBottomPadding;
    private int mColumns;
    private int mEllipsizedWidth;
    private Paint.FontMetricsInt mFontMetricsInt;
    private int mLineCount;
    private Layout.Directions[] mLineDirections;
    private int[] mLines;
    private int mMaximumVisibleLineCount;
    private MeasuredText mMeasured;
    private int mTopPadding;

    private static final boolean isIdeographic(char c, boolean z) {
        if ((c >= 11904 && c <= 12287) || c == 12288) {
            return true;
        }
        if (c >= 12352 && c <= 12447) {
            if (!z) {
                if (c != 12353 && c != 12355 && c != 12357 && c != 12359 && c != 12361 && c != 12387 && c != 12419 && c != 12421 && c != 12423 && c != 12430 && c != 12437 && c != 12438) {
                    switch (c) {
                    }
                    return true;
                }
                return false;
            }
            return true;
        }
        if (c >= 12448 && c <= 12543) {
            if (!z) {
                if (c != 12448 && c != 12449 && c != 12451 && c != 12453 && c != 12455 && c != 12457 && c != 12483 && c != 12515 && c != 12517 && c != 12519 && c != 12526 && c != 12533 && c != 12534) {
                    switch (c) {
                    }
                    return true;
                }
                return false;
            }
            return true;
        }
        if (c >= 13312 && c <= 19893) {
            return true;
        }
        if (c >= 19968 && c <= 40891) {
            return true;
        }
        if (c >= 63744 && c <= 64217) {
            return true;
        }
        if (c >= 40960 && c <= 42127) {
            return true;
        }
        if (c >= 42128 && c <= 42191) {
            return true;
        }
        if (c < 65122 || c > 65126) {
            return c >= 65296 && c <= 65305;
        }
        return true;
    }

    public StaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, boolean z) {
        this(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z);
    }

    public StaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, TextDirectionHeuristic textDirectionHeuristic, float f, float f2, boolean z) {
        this(charSequence, 0, charSequence.length(), textPaint, i, alignment, textDirectionHeuristic, f, f2, z);
    }

    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, float f, float f2, boolean z) {
        this(charSequence, i, i2, textPaint, i3, alignment, f, f2, z, null, 0);
    }

    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, TextDirectionHeuristic textDirectionHeuristic, float f, float f2, boolean z) {
        this(charSequence, i, i2, textPaint, i3, alignment, textDirectionHeuristic, f, f2, z, null, 0, Integer.MAX_VALUE);
    }

    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i4) {
        this(charSequence, i, i2, textPaint, i3, alignment, TextDirectionHeuristics.FIRSTSTRONG_LTR, f, f2, z, truncateAt, i4, Integer.MAX_VALUE);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, TextDirectionHeuristic textDirectionHeuristic, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i4, int i5) {
        CharSequence spannedEllipsizer;
        if (truncateAt == null) {
            spannedEllipsizer = charSequence;
        } else {
            spannedEllipsizer = charSequence instanceof Spanned ? new Layout.SpannedEllipsizer(charSequence) : new Layout.Ellipsizer(charSequence);
        }
        super(spannedEllipsizer, textPaint, i3, alignment, textDirectionHeuristic, f, f2);
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        this.mFontMetricsInt = new Paint.FontMetricsInt();
        if (truncateAt != null) {
            Layout.Ellipsizer ellipsizer = (Layout.Ellipsizer) getText();
            ellipsizer.mLayout = this;
            ellipsizer.mWidth = i4;
            ellipsizer.mMethod = truncateAt;
            this.mEllipsizedWidth = i4;
            this.mColumns = 5;
        } else {
            this.mColumns = 3;
            this.mEllipsizedWidth = i3;
        }
        this.mLines = new int[ArrayUtils.idealIntArraySize(this.mColumns * 2)];
        this.mLineDirections = new Layout.Directions[ArrayUtils.idealIntArraySize(this.mColumns * 2)];
        this.mMaximumVisibleLineCount = i5;
        this.mMeasured = MeasuredText.obtain();
        generate(charSequence, i, i2, textPaint, i3, textDirectionHeuristic, f, f2, z, z, i4, truncateAt);
        this.mMeasured = MeasuredText.recycle(this.mMeasured);
        this.mFontMetricsInt = null;
    }

    StaticLayout(CharSequence charSequence) {
        super(charSequence, null, 0, null, 0.0f, 0.0f);
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        this.mFontMetricsInt = new Paint.FontMetricsInt();
        this.mColumns = 5;
        this.mLines = new int[ArrayUtils.idealIntArraySize(5 * 2)];
        this.mLineDirections = new Layout.Directions[ArrayUtils.idealIntArraySize(this.mColumns * 2)];
        this.mMeasured = MeasuredText.obtain();
    }

    /* JADX WARN: Removed duplicated region for block: B:163:0x03e4  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x040d  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01a1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void generate(java.lang.CharSequence r70, int r71, int r72, android.text.TextPaint r73, int r74, android.text.TextDirectionHeuristic r75, float r76, float r77, boolean r78, boolean r79, float r80, android.text.TextUtils.TruncateAt r81) {
        /*
            Method dump skipped, instruction units count: 1494
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.StaticLayout.generate(java.lang.CharSequence, int, int, android.text.TextPaint, int, android.text.TextDirectionHeuristic, float, float, boolean, boolean, float, android.text.TextUtils$TruncateAt):void");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v2, types: [int] */
    /* JADX WARN: Type inference failed for: r8v9 */
    private int out(CharSequence charSequence, int i, int i2, int i3, int i4, int i5, int i6, int i7, float f, float f2, LineHeightSpan[] lineHeightSpanArr, int[] iArr, Paint.FontMetricsInt fontMetricsInt, boolean z, boolean z2, byte[] bArr, int i8, boolean z3, int i9, boolean z4, boolean z5, char[] cArr, float[] fArr, int i10, TextUtils.TruncateAt truncateAt, float f3, float f4, TextPaint textPaint, boolean z6) {
        int[] iArr2;
        int i11;
        int i12;
        int i13;
        boolean z7;
        ?? r8;
        int i14;
        boolean z8;
        int i15 = this.mLineCount;
        int i16 = this.mColumns;
        int i17 = i15 * i16;
        int i18 = i16 + i17 + 1;
        int[] iArr3 = this.mLines;
        boolean z9 = false;
        if (i18 >= iArr3.length) {
            int iIdealIntArraySize = ArrayUtils.idealIntArraySize(i18 + 1);
            int[] iArr4 = new int[iIdealIntArraySize];
            System.arraycopy(iArr3, 0, iArr4, 0, iArr3.length);
            this.mLines = iArr4;
            Layout.Directions[] directionsArr = new Layout.Directions[iIdealIntArraySize];
            Layout.Directions[] directionsArr2 = this.mLineDirections;
            System.arraycopy(directionsArr2, 0, directionsArr, 0, directionsArr2.length);
            this.mLineDirections = directionsArr;
            iArr2 = iArr4;
        } else {
            iArr2 = iArr3;
        }
        int i19 = i3;
        if (lineHeightSpanArr != null) {
            fontMetricsInt.ascent = i19;
            fontMetricsInt.descent = i4;
            fontMetricsInt.top = i5;
            fontMetricsInt.bottom = i6;
            int i20 = 0;
            while (i20 < lineHeightSpanArr.length) {
                if (lineHeightSpanArr[i20] instanceof LineHeightSpan.WithDensity) {
                    i14 = i20;
                    z8 = z9;
                    ((LineHeightSpan.WithDensity) lineHeightSpanArr[i20]).chooseHeight(charSequence, i, i2, iArr[i20], i7, fontMetricsInt, textPaint);
                } else {
                    i14 = i20;
                    z8 = z9;
                    lineHeightSpanArr[i14].chooseHeight(charSequence, i, i2, iArr[i14], i7, fontMetricsInt);
                }
                i20 = i14 + 1;
                z9 = z8;
            }
            z7 = z9;
            i19 = fontMetricsInt.ascent;
            i11 = fontMetricsInt.descent;
            i12 = fontMetricsInt.top;
            i13 = fontMetricsInt.bottom;
        } else {
            i11 = i4;
            i12 = i5;
            i13 = i6;
            z7 = false;
        }
        if (i15 == 0) {
            if (z5) {
                this.mTopPadding = i12 - i19;
            }
            if (z4) {
                i19 = i12;
            }
        }
        if (i2 == i9) {
            if (z5) {
                this.mBottomPadding = i13 - i11;
            }
            if (z4) {
                i11 = i13;
            }
        }
        if (z2) {
            double d = ((i11 - i19) * (f - 1.0f)) + f2;
            r8 = d >= 0.0d ? (int) (d + EXTRA_ROUNDING) : -((int) ((-d) + EXTRA_ROUNDING));
        } else {
            r8 = z7;
        }
        int i21 = i17 + 0;
        iArr2[i21] = i;
        iArr2[i17 + 1] = i7;
        iArr2[i17 + 2] = i11 + r8;
        int i22 = i7 + (i11 - i19) + r8;
        int i23 = this.mColumns;
        iArr2[i17 + i23 + 0] = i2;
        iArr2[i17 + i23 + 1] = i22;
        if (z) {
            iArr2[i21] = iArr2[i21] | 536870912;
        }
        iArr2[i21] = iArr2[i21] | (i8 << 30);
        Layout.Directions directions = DIRS_ALL_LEFT_TO_RIGHT;
        if (z3) {
            this.mLineDirections[i15] = directions;
        } else {
            int i24 = i - i10;
            this.mLineDirections[i15] = AndroidBidi.directions(i8, bArr, i24, cArr, i24, i2 - i);
        }
        if (truncateAt != null) {
            boolean z10 = i15 == 0 ? true : z7;
            int i25 = i15 + 1;
            int i26 = this.mMaximumVisibleLineCount;
            boolean z11 = i25 == i26 ? true : z7;
            boolean z12 = (z6 && this.mLineCount + 1 == i26) ? true : z7;
            if ((((!(i26 == 1 && z6) && (!z10 || z6)) || truncateAt == TextUtils.TruncateAt.MARQUEE) && (z10 || ((!z11 && z6) || truncateAt != TextUtils.TruncateAt.END))) ? z7 : true) {
                calculateEllipsis(i, i2, fArr, i10, f3, truncateAt, i15, f4, textPaint, z12);
            }
        }
        this.mLineCount++;
        return i22;
    }

    private void calculateEllipsis(int i, int i2, float[] fArr, int i3, float f, TextUtils.TruncateAt truncateAt, int i4, float f2, TextPaint textPaint, boolean z) {
        int i5;
        int i6 = 0;
        if (f2 <= f && !z) {
            int[] iArr = this.mLines;
            int i7 = this.mColumns;
            iArr[(i7 * i4) + 3] = 0;
            iArr[(i7 * i4) + 4] = 0;
            return;
        }
        float fMeasureText = textPaint.measureText(truncateAt == TextUtils.TruncateAt.END_SMALL ? ELLIPSIS_TWO_DOTS : ELLIPSIS_NORMAL, 0, 1);
        int i8 = i2 - i;
        float f3 = 0.0f;
        if (truncateAt == TextUtils.TruncateAt.START) {
            if (this.mMaximumVisibleLineCount == 1) {
                while (i8 >= 0) {
                    f3 += fArr[((i8 - 1) + i) - i3];
                    if (f3 + fMeasureText > f) {
                        break;
                    } else {
                        i8--;
                    }
                }
                i5 = i8;
            } else {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "Start Ellipsis only supported with one line");
                }
                i5 = 0;
            }
        } else if (truncateAt == TextUtils.TruncateAt.END || truncateAt == TextUtils.TruncateAt.MARQUEE || truncateAt == TextUtils.TruncateAt.END_SMALL) {
            while (i6 < i8) {
                f3 += fArr[(i6 + i) - i3];
                if (f3 + fMeasureText > f) {
                    break;
                } else {
                    i6++;
                }
            }
            i5 = i8 - i6;
            if (z && i5 == 0 && i8 > 0) {
                i6 = i8 - 1;
                i5 = 1;
            }
        } else if (this.mMaximumVisibleLineCount == 1) {
            float f4 = f - fMeasureText;
            float f5 = f4 / 2.0f;
            float f6 = 0.0f;
            while (i8 >= 0) {
                float f7 = fArr[((i8 - 1) + i) - i3] + f6;
                if (f7 > f5) {
                    break;
                }
                i8--;
                f6 = f7;
            }
            float f8 = f4 - f6;
            while (i6 < i8) {
                f3 += fArr[(i6 + i) - i3];
                if (f3 > f8) {
                    break;
                } else {
                    i6++;
                }
            }
            i5 = i8 - i6;
        } else {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Middle Ellipsis only supported with one line");
            }
            i5 = 0;
        }
        int[] iArr2 = this.mLines;
        int i9 = this.mColumns;
        iArr2[(i9 * i4) + 3] = i6;
        iArr2[(i9 * i4) + 4] = i5;
    }

    @Override // android.text.Layout
    public int getLineForVertical(int i) {
        int i2 = this.mLineCount;
        int[] iArr = this.mLines;
        int i3 = -1;
        while (i2 - i3 > 1) {
            int i4 = (i2 + i3) >> 1;
            if (iArr[(this.mColumns * i4) + 1] > i) {
                i2 = i4;
            } else {
                i3 = i4;
            }
        }
        if (i3 < 0) {
            return 0;
        }
        return i3;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return this.mLineCount;
    }

    @Override // android.text.Layout
    public int getLineTop(int i) {
        int i2 = this.mLines[(this.mColumns * i) + 1];
        int i3 = this.mMaximumVisibleLineCount;
        return (i3 <= 0 || i < i3 || i == this.mLineCount) ? i2 : i2 + getBottomPadding();
    }

    @Override // android.text.Layout
    public int getLineDescent(int i) {
        int i2 = this.mLines[(this.mColumns * i) + 2];
        int i3 = this.mMaximumVisibleLineCount;
        return (i3 <= 0 || i < i3 + (-1) || i == this.mLineCount) ? i2 : i2 + getBottomPadding();
    }

    @Override // android.text.Layout
    public int getLineStart(int i) {
        return this.mLines[(this.mColumns * i) + 0] & 536870911;
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int i) {
        return this.mLines[(this.mColumns * i) + 0] >> 30;
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int i) {
        return (this.mLines[(this.mColumns * i) + 0] & 536870912) != 0;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int i) {
        return this.mLineDirections[i];
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
        int i2 = this.mColumns;
        if (i2 < 5) {
            return 0;
        }
        return this.mLines[(i2 * i) + 4];
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int i) {
        int i2 = this.mColumns;
        if (i2 < 5) {
            return 0;
        }
        return this.mLines[(i2 * i) + 3];
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    void prepare() {
        this.mMeasured = MeasuredText.obtain();
    }

    void finish() {
        this.mMeasured = MeasuredText.recycle(this.mMeasured);
    }
}
