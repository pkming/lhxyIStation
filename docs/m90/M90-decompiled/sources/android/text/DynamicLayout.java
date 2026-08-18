package android.text;

import android.graphics.Paint;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.UpdateLayout;
import android.text.style.WrapTogetherSpan;
import com.android.internal.util.ArrayUtils;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class DynamicLayout extends Layout {
    private static final int BLOCK_MINIMUM_CHARACTER_LENGTH = 400;
    private static final int COLUMNS_ELLIPSIZE = 5;
    private static final int COLUMNS_NORMAL = 3;
    private static final int DESCENT = 2;
    private static final int DIR = 0;
    private static final int DIR_SHIFT = 30;
    private static final int ELLIPSIS_COUNT = 4;
    private static final int ELLIPSIS_START = 3;
    private static final int ELLIPSIS_UNDEFINED = Integer.MIN_VALUE;
    public static final int INVALID_BLOCK_INDEX = -1;
    private static final int PRIORITY = 128;
    private static final int START = 0;
    private static final int START_MASK = 536870911;
    private static final int TAB = 0;
    private static final int TAB_MASK = 536870912;
    private static final int TOP = 1;
    private CharSequence mBase;
    private int[] mBlockEndLines;
    private int[] mBlockIndices;
    private int mBottomPadding;
    private CharSequence mDisplay;
    private boolean mEllipsize;
    private TextUtils.TruncateAt mEllipsizeAt;
    private int mEllipsizedWidth;
    private boolean mIncludePad;
    private int mIndexFirstChangedBlock;
    private PackedIntVector mInts;
    private int mNumberOfBlocks;
    private PackedObjectVector<Layout.Directions> mObjects;
    private int mTopPadding;
    private ChangeWatcher mWatcher;
    private static StaticLayout sStaticLayout = new StaticLayout(null);
    private static final Object[] sLock = new Object[0];

    public DynamicLayout(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, boolean z) {
        this(charSequence, charSequence, textPaint, i, alignment, f, f2, z);
    }

    public DynamicLayout(CharSequence charSequence, CharSequence charSequence2, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, boolean z) {
        this(charSequence, charSequence2, textPaint, i, alignment, f, f2, z, null, 0);
    }

    public DynamicLayout(CharSequence charSequence, CharSequence charSequence2, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i2) {
        this(charSequence, charSequence2, textPaint, i, alignment, TextDirectionHeuristics.FIRSTSTRONG_LTR, f, f2, z, truncateAt, i2);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public DynamicLayout(CharSequence charSequence, CharSequence charSequence2, TextPaint textPaint, int i, Layout.Alignment alignment, TextDirectionHeuristic textDirectionHeuristic, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i2) {
        CharSequence spannedEllipsizer;
        int[] iArr;
        if (truncateAt == null) {
            spannedEllipsizer = charSequence2;
        } else {
            spannedEllipsizer = charSequence2 instanceof Spanned ? new Layout.SpannedEllipsizer(charSequence2) : new Layout.Ellipsizer(charSequence2);
        }
        super(spannedEllipsizer, textPaint, i, alignment, textDirectionHeuristic, f, f2);
        this.mBase = charSequence;
        this.mDisplay = charSequence2;
        if (truncateAt != null) {
            this.mInts = new PackedIntVector(5);
            this.mEllipsizedWidth = i2;
            this.mEllipsizeAt = truncateAt;
        } else {
            this.mInts = new PackedIntVector(3);
            this.mEllipsizedWidth = i;
            this.mEllipsizeAt = null;
        }
        this.mObjects = new PackedObjectVector<>(1);
        this.mIncludePad = z;
        if (truncateAt != null) {
            Layout.Ellipsizer ellipsizer = (Layout.Ellipsizer) getText();
            ellipsizer.mLayout = this;
            ellipsizer.mWidth = i2;
            ellipsizer.mMethod = truncateAt;
            this.mEllipsize = true;
        }
        if (truncateAt != null) {
            iArr = new int[5];
            iArr[3] = Integer.MIN_VALUE;
        } else {
            iArr = new int[3];
        }
        Layout.Directions[] directionsArr = {DIRS_ALL_LEFT_TO_RIGHT};
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        int i3 = fontMetricsInt.ascent;
        int i4 = fontMetricsInt.descent;
        iArr[0] = 1073741824;
        iArr[1] = 0;
        iArr[2] = i4;
        this.mInts.insertAt(0, iArr);
        iArr[1] = i4 - i3;
        this.mInts.insertAt(1, iArr);
        this.mObjects.insertAt(0, directionsArr);
        reflow(charSequence, 0, 0, charSequence.length());
        if (charSequence instanceof Spannable) {
            if (this.mWatcher == null) {
                this.mWatcher = new ChangeWatcher(this);
            }
            Spannable spannable = (Spannable) charSequence;
            for (ChangeWatcher changeWatcher : (ChangeWatcher[]) spannable.getSpans(0, spannable.length(), ChangeWatcher.class)) {
                spannable.removeSpan(changeWatcher);
            }
            spannable.setSpan(this.mWatcher, 0, charSequence.length(), 8388626);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reflow(CharSequence charSequence, int i, int i2, int i3) {
        StaticLayout staticLayout;
        StaticLayout staticLayout2;
        int topPadding;
        int bottomPadding;
        int[] iArr;
        boolean z;
        if (charSequence != this.mBase) {
            return;
        }
        CharSequence charSequence2 = this.mDisplay;
        int length = charSequence2.length();
        int iLastIndexOf = TextUtils.lastIndexOf(charSequence2, '\n', i - 1);
        int i4 = i - (iLastIndexOf < 0 ? 0 : iLastIndexOf + 1);
        int i5 = i2 + i4;
        int i6 = i3 + i4;
        int i7 = i - i4;
        int i8 = i7 + i6;
        int iIndexOf = TextUtils.indexOf(charSequence2, '\n', i8);
        int i9 = (iIndexOf < 0 ? length : iIndexOf + 1) - i8;
        int i10 = i5 + i9;
        int i11 = i6 + i9;
        if (charSequence2 instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence2;
            do {
                Object[] spans = spanned.getSpans(i7, i7 + i11, WrapTogetherSpan.class);
                z = false;
                for (int i12 = 0; i12 < spans.length; i12++) {
                    int spanStart = spanned.getSpanStart(spans[i12]);
                    int spanEnd = spanned.getSpanEnd(spans[i12]);
                    if (spanStart < i7) {
                        int i13 = i7 - spanStart;
                        i10 += i13;
                        i11 += i13;
                        i7 -= i13;
                        z = true;
                    }
                    int i14 = i7 + i11;
                    if (spanEnd > i14) {
                        int i15 = spanEnd - i14;
                        i10 += i15;
                        i11 += i15;
                        z = true;
                    }
                }
            } while (z);
        }
        int i16 = i7;
        int i17 = i10;
        int i18 = i11;
        int lineForOffset = getLineForOffset(i16);
        int lineTop = getLineTop(lineForOffset);
        int lineForOffset2 = getLineForOffset(i16 + i17);
        int i19 = i16 + i18;
        if (i19 == length) {
            lineForOffset2 = getLineCount();
        }
        int i20 = lineForOffset2;
        int lineTop2 = getLineTop(i20);
        boolean z2 = i20 == getLineCount();
        synchronized (sLock) {
            staticLayout = sStaticLayout;
            sStaticLayout = null;
        }
        if (staticLayout == null) {
            staticLayout = new StaticLayout(null);
        } else {
            staticLayout.prepare();
        }
        StaticLayout staticLayout3 = staticLayout;
        staticLayout3.generate(charSequence2, i16, i19, getPaint(), getWidth(), getTextDirectionHeuristic(), getSpacingMultiplier(), getSpacingAdd(), false, true, this.mEllipsizedWidth, this.mEllipsizeAt);
        int lineCount = staticLayout3.getLineCount();
        if (i19 != length) {
            staticLayout2 = staticLayout3;
            if (staticLayout2.getLineStart(lineCount - 1) == i19) {
                lineCount--;
            }
        } else {
            staticLayout2 = staticLayout3;
        }
        int i21 = i20 - lineForOffset;
        this.mInts.deleteAt(lineForOffset, i21);
        this.mObjects.deleteAt(lineForOffset, i21);
        int lineTop3 = staticLayout2.getLineTop(lineCount);
        if (this.mIncludePad && lineForOffset == 0) {
            topPadding = staticLayout2.getTopPadding();
            this.mTopPadding = topPadding;
            lineTop3 -= topPadding;
        } else {
            topPadding = 0;
        }
        if (this.mIncludePad && z2) {
            bottomPadding = staticLayout2.getBottomPadding();
            this.mBottomPadding = bottomPadding;
            lineTop3 += bottomPadding;
        } else {
            bottomPadding = 0;
        }
        this.mInts.adjustValuesBelow(lineForOffset, 0, i18 - i17);
        this.mInts.adjustValuesBelow(lineForOffset, 1, (lineTop - lineTop2) + lineTop3);
        if (this.mEllipsize) {
            iArr = new int[5];
            iArr[3] = Integer.MIN_VALUE;
        } else {
            iArr = new int[3];
        }
        Layout.Directions[] directionsArr = new Layout.Directions[1];
        for (int i22 = 0; i22 < lineCount; i22++) {
            iArr[0] = staticLayout2.getLineStart(i22) | (staticLayout2.getParagraphDirection(i22) << 30) | (staticLayout2.getLineContainsTab(i22) ? 536870912 : 0);
            int lineTop4 = staticLayout2.getLineTop(i22) + lineTop;
            if (i22 > 0) {
                lineTop4 -= topPadding;
            }
            iArr[1] = lineTop4;
            int lineDescent = staticLayout2.getLineDescent(i22);
            if (i22 == lineCount - 1) {
                lineDescent += bottomPadding;
            }
            iArr[2] = lineDescent;
            directionsArr[0] = staticLayout2.getLineDirections(i22);
            if (this.mEllipsize) {
                iArr[3] = staticLayout2.getEllipsisStart(i22);
                iArr[4] = staticLayout2.getEllipsisCount(i22);
            }
            int i23 = lineForOffset + i22;
            this.mInts.insertAt(i23, iArr);
            this.mObjects.insertAt(i23, directionsArr);
        }
        updateBlocks(lineForOffset, i20 - 1, lineCount);
        synchronized (sLock) {
            sStaticLayout = staticLayout2;
            staticLayout2.finish();
        }
    }

    private void createBlocks() {
        this.mNumberOfBlocks = 0;
        CharSequence charSequence = this.mDisplay;
        int i = 400;
        while (true) {
            int iIndexOf = TextUtils.indexOf(charSequence, '\n', i);
            if (iIndexOf < 0) {
                break;
            }
            addBlockAtOffset(iIndexOf);
            i = iIndexOf + 400;
        }
        addBlockAtOffset(charSequence.length());
        this.mBlockIndices = new int[this.mBlockEndLines.length];
        for (int i2 = 0; i2 < this.mBlockEndLines.length; i2++) {
            this.mBlockIndices[i2] = -1;
        }
    }

    private void addBlockAtOffset(int i) {
        int lineForOffset = getLineForOffset(i);
        int[] iArr = this.mBlockEndLines;
        if (iArr == null) {
            int[] iArr2 = new int[ArrayUtils.idealIntArraySize(1)];
            this.mBlockEndLines = iArr2;
            int i2 = this.mNumberOfBlocks;
            iArr2[i2] = lineForOffset;
            this.mNumberOfBlocks = i2 + 1;
            return;
        }
        int i3 = this.mNumberOfBlocks;
        if (lineForOffset > iArr[i3 - 1]) {
            if (i3 == iArr.length) {
                int[] iArr3 = new int[ArrayUtils.idealIntArraySize(i3 + 1)];
                System.arraycopy(this.mBlockEndLines, 0, iArr3, 0, this.mNumberOfBlocks);
                this.mBlockEndLines = iArr3;
            }
            int[] iArr4 = this.mBlockEndLines;
            int i4 = this.mNumberOfBlocks;
            iArr4[i4] = lineForOffset;
            this.mNumberOfBlocks = i4 + 1;
        }
    }

    void updateBlocks(int i, int i2, int i3) {
        int i4;
        int i5;
        int i6;
        if (this.mBlockEndLines == null) {
            createBlocks();
            return;
        }
        int i7 = 0;
        while (true) {
            if (i7 >= this.mNumberOfBlocks) {
                i7 = -1;
                break;
            } else if (this.mBlockEndLines[i7] >= i) {
                break;
            } else {
                i7++;
            }
        }
        int i8 = i7;
        while (true) {
            i4 = this.mNumberOfBlocks;
            if (i8 >= i4) {
                i8 = -1;
                break;
            } else if (this.mBlockEndLines[i8] >= i2) {
                break;
            } else {
                i8++;
            }
        }
        int[] iArr = this.mBlockEndLines;
        int i9 = iArr[i8];
        boolean z = i > (i7 == 0 ? 0 : iArr[i7 + (-1)] + 1);
        boolean z2 = i3 > 0;
        boolean z3 = i2 < iArr[i8];
        int i10 = z ? 1 : 0;
        if (z2) {
            i10++;
        }
        if (z3) {
            i10++;
        }
        int i11 = (i4 + i10) - ((i8 - i7) + 1);
        if (i11 == 0) {
            iArr[0] = 0;
            this.mBlockIndices[0] = -1;
            this.mNumberOfBlocks = 1;
            return;
        }
        if (i11 > iArr.length) {
            int iIdealIntArraySize = ArrayUtils.idealIntArraySize(i11);
            int[] iArr2 = new int[iIdealIntArraySize];
            int[] iArr3 = new int[iIdealIntArraySize];
            System.arraycopy(this.mBlockEndLines, 0, iArr2, 0, i7);
            System.arraycopy(this.mBlockIndices, 0, iArr3, 0, i7);
            int i12 = i8 + 1;
            int i13 = i7 + i10;
            i5 = i9;
            System.arraycopy(this.mBlockEndLines, i12, iArr2, i13, (this.mNumberOfBlocks - i8) - 1);
            System.arraycopy(this.mBlockIndices, i12, iArr3, i13, (this.mNumberOfBlocks - i8) - 1);
            this.mBlockEndLines = iArr2;
            this.mBlockIndices = iArr3;
        } else {
            i5 = i9;
            int i14 = i8 + 1;
            int i15 = i7 + i10;
            System.arraycopy(iArr, i14, iArr, i15, (i4 - i8) - 1);
            int[] iArr4 = this.mBlockIndices;
            System.arraycopy(iArr4, i14, iArr4, i15, (this.mNumberOfBlocks - i8) - 1);
        }
        this.mNumberOfBlocks = i11;
        int i16 = i3 - ((i2 - i) + 1);
        if (i16 != 0) {
            i11 = i7 + i10;
            for (int i17 = i11; i17 < this.mNumberOfBlocks; i17++) {
                int[] iArr5 = this.mBlockEndLines;
                iArr5[i17] = iArr5[i17] + i16;
            }
        }
        this.mIndexFirstChangedBlock = Math.min(this.mIndexFirstChangedBlock, i11);
        if (z) {
            this.mBlockEndLines[i7] = i - 1;
            i6 = -1;
            this.mBlockIndices[i7] = -1;
            i7++;
        } else {
            i6 = -1;
        }
        if (z2) {
            this.mBlockEndLines[i7] = (i + i3) - 1;
            this.mBlockIndices[i7] = i6;
            i7++;
        }
        if (z3) {
            this.mBlockEndLines[i7] = i5 + i16;
            this.mBlockIndices[i7] = i6;
        }
    }

    void setBlocksDataForTest(int[] iArr, int[] iArr2, int i) {
        int[] iArr3 = new int[iArr.length];
        this.mBlockEndLines = iArr3;
        this.mBlockIndices = new int[iArr2.length];
        System.arraycopy(iArr, 0, iArr3, 0, iArr.length);
        System.arraycopy(iArr2, 0, this.mBlockIndices, 0, iArr2.length);
        this.mNumberOfBlocks = i;
    }

    public int[] getBlockEndLines() {
        return this.mBlockEndLines;
    }

    public int[] getBlockIndices() {
        return this.mBlockIndices;
    }

    public int getNumberOfBlocks() {
        return this.mNumberOfBlocks;
    }

    public int getIndexFirstChangedBlock() {
        return this.mIndexFirstChangedBlock;
    }

    public void setIndexFirstChangedBlock(int i) {
        this.mIndexFirstChangedBlock = i;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return this.mInts.size() - 1;
    }

    @Override // android.text.Layout
    public int getLineTop(int i) {
        return this.mInts.getValue(i, 1);
    }

    @Override // android.text.Layout
    public int getLineDescent(int i) {
        return this.mInts.getValue(i, 2);
    }

    @Override // android.text.Layout
    public int getLineStart(int i) {
        return this.mInts.getValue(i, 0) & 536870911;
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int i) {
        return (this.mInts.getValue(i, 0) & 536870912) != 0;
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int i) {
        return this.mInts.getValue(i, 0) >> 30;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int i) {
        return this.mObjects.getValue(i, 0);
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
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    private static class ChangeWatcher implements TextWatcher, SpanWatcher {
        private WeakReference<DynamicLayout> mLayout;

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public ChangeWatcher(DynamicLayout dynamicLayout) {
            this.mLayout = new WeakReference<>(dynamicLayout);
        }

        private void reflow(CharSequence charSequence, int i, int i2, int i3) {
            DynamicLayout dynamicLayout = this.mLayout.get();
            if (dynamicLayout != null) {
                dynamicLayout.reflow(charSequence, i, i2, i3);
            } else if (charSequence instanceof Spannable) {
                ((Spannable) charSequence).removeSpan(this);
            }
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            reflow(charSequence, i, i2, i3);
        }

        @Override // android.text.SpanWatcher
        public void onSpanAdded(Spannable spannable, Object obj, int i, int i2) {
            if (obj instanceof UpdateLayout) {
                int i3 = i2 - i;
                reflow(spannable, i, i3, i3);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanRemoved(Spannable spannable, Object obj, int i, int i2) {
            if (obj instanceof UpdateLayout) {
                int i3 = i2 - i;
                reflow(spannable, i, i3, i3);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanChanged(Spannable spannable, Object obj, int i, int i2, int i3, int i4) {
            if (obj instanceof UpdateLayout) {
                int i5 = i2 - i;
                reflow(spannable, i, i5, i5);
                int i6 = i4 - i3;
                reflow(spannable, i3, i6, i6);
            }
        }
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int i) {
        if (this.mEllipsizeAt == null) {
            return 0;
        }
        return this.mInts.getValue(i, 3);
    }

    @Override // android.text.Layout
    public int getEllipsisCount(int i) {
        if (this.mEllipsizeAt == null) {
            return 0;
        }
        return this.mInts.getValue(i, 4);
    }
}
