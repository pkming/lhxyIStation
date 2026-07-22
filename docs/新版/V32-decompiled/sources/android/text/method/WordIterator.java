package android.text.method;

import android.text.Selection;
import android.text.SpannableStringBuilder;
import java.text.BreakIterator;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class WordIterator implements Selection.PositionIterator {
    private static final int WINDOW_WIDTH = 50;
    private BreakIterator mIterator;
    private int mOffsetShift;
    private String mString;

    public WordIterator() {
        this(Locale.getDefault());
    }

    public WordIterator(Locale locale) {
        this.mIterator = BreakIterator.getWordInstance(locale);
    }

    public void setCharSequence(CharSequence charSequence, int i, int i2) {
        this.mOffsetShift = Math.max(0, i - 50);
        int iMin = Math.min(charSequence.length(), i2 + 50);
        if (charSequence instanceof SpannableStringBuilder) {
            this.mString = ((SpannableStringBuilder) charSequence).substring(this.mOffsetShift, iMin);
        } else {
            this.mString = charSequence.subSequence(this.mOffsetShift, iMin).toString();
        }
        this.mIterator.setText(this.mString);
    }

    @Override // android.text.Selection.PositionIterator
    public int preceding(int i) {
        int iPreceding = i - this.mOffsetShift;
        do {
            iPreceding = this.mIterator.preceding(iPreceding);
            if (iPreceding == -1) {
                return -1;
            }
        } while (!isOnLetterOrDigit(iPreceding));
        return iPreceding + this.mOffsetShift;
    }

    @Override // android.text.Selection.PositionIterator
    public int following(int i) {
        int iFollowing = i - this.mOffsetShift;
        do {
            iFollowing = this.mIterator.following(iFollowing);
            if (iFollowing == -1) {
                return -1;
            }
        } while (!isAfterLetterOrDigit(iFollowing));
        return iFollowing + this.mOffsetShift;
    }

    public int getBeginning(int i) {
        int i2;
        int iPreceding = i - this.mOffsetShift;
        checkOffsetIsValid(iPreceding);
        if (isOnLetterOrDigit(iPreceding)) {
            if (this.mIterator.isBoundary(iPreceding)) {
                i2 = this.mOffsetShift;
            } else {
                iPreceding = this.mIterator.preceding(iPreceding);
                i2 = this.mOffsetShift;
            }
        } else {
            if (!isAfterLetterOrDigit(iPreceding)) {
                return -1;
            }
            iPreceding = this.mIterator.preceding(iPreceding);
            i2 = this.mOffsetShift;
        }
        return iPreceding + i2;
    }

    public int getEnd(int i) {
        int i2;
        int iFollowing = i - this.mOffsetShift;
        checkOffsetIsValid(iFollowing);
        if (isAfterLetterOrDigit(iFollowing)) {
            if (this.mIterator.isBoundary(iFollowing)) {
                i2 = this.mOffsetShift;
            } else {
                iFollowing = this.mIterator.following(iFollowing);
                i2 = this.mOffsetShift;
            }
        } else {
            if (!isOnLetterOrDigit(iFollowing)) {
                return -1;
            }
            iFollowing = this.mIterator.following(iFollowing);
            i2 = this.mOffsetShift;
        }
        return iFollowing + i2;
    }

    private boolean isAfterLetterOrDigit(int i) {
        return i >= 1 && i <= this.mString.length() && Character.isLetterOrDigit(this.mString.codePointBefore(i));
    }

    private boolean isOnLetterOrDigit(int i) {
        return i >= 0 && i < this.mString.length() && Character.isLetterOrDigit(this.mString.codePointAt(i));
    }

    private void checkOffsetIsValid(int i) {
        if (i < 0 || i > this.mString.length()) {
            throw new IllegalArgumentException("Invalid offset: " + (i + this.mOffsetShift) + ". Valid range is [" + this.mOffsetShift + ", " + (this.mString.length() + this.mOffsetShift) + "]");
        }
    }
}
