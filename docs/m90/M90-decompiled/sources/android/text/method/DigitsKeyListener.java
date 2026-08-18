package android.text.method;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

/* JADX INFO: loaded from: classes.dex */
public class DigitsKeyListener extends NumberKeyListener {
    private static final int DECIMAL = 2;
    private static final int SIGN = 1;
    private char[] mAccepted;
    private boolean mDecimal;
    private boolean mSign;
    private static final char[][] CHARACTERS = {new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'}, new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+'}, new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'}, new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+', '.'}};
    private static DigitsKeyListener[] sInstance = new DigitsKeyListener[4];

    private static boolean isDecimalPointChar(char c) {
        return c == '.';
    }

    private static boolean isSignChar(char c) {
        return c == '-' || c == '+';
    }

    @Override // android.text.method.NumberKeyListener
    protected char[] getAcceptedChars() {
        return this.mAccepted;
    }

    public DigitsKeyListener() {
        this(false, false);
    }

    public DigitsKeyListener(boolean z, boolean z2) {
        this.mSign = z;
        this.mDecimal = z2;
        this.mAccepted = CHARACTERS[(z ? 1 : 0) | (z2 ? 2 : 0)];
    }

    public static DigitsKeyListener getInstance() {
        return getInstance(false, false);
    }

    public static DigitsKeyListener getInstance(boolean z, boolean z2) {
        int i = (z2 ? 2 : 0) | (z ? 1 : 0);
        DigitsKeyListener[] digitsKeyListenerArr = sInstance;
        if (digitsKeyListenerArr[i] != null) {
            return digitsKeyListenerArr[i];
        }
        digitsKeyListenerArr[i] = new DigitsKeyListener(z, z2);
        return sInstance[i];
    }

    public static DigitsKeyListener getInstance(String str) {
        DigitsKeyListener digitsKeyListener = new DigitsKeyListener();
        digitsKeyListener.mAccepted = new char[str.length()];
        str.getChars(0, str.length(), digitsKeyListener.mAccepted, 0);
        return digitsKeyListener;
    }

    @Override // android.text.method.KeyListener
    public int getInputType() {
        int i = this.mSign ? 4098 : 2;
        return this.mDecimal ? i | 8192 : i;
    }

    @Override // android.text.method.NumberKeyListener, android.text.InputFilter
    public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        CharSequence charSequenceFilter = super.filter(charSequence, i, i2, spanned, i3, i4);
        if (!this.mSign && !this.mDecimal) {
            return charSequenceFilter;
        }
        if (charSequenceFilter != null) {
            i2 = charSequenceFilter.length();
            charSequence = charSequenceFilter;
            i = 0;
        }
        int length = spanned.length();
        int i5 = -1;
        int i6 = -1;
        for (int i7 = 0; i7 < i3; i7++) {
            char cCharAt = spanned.charAt(i7);
            if (isSignChar(cCharAt)) {
                i6 = i7;
            } else if (isDecimalPointChar(cCharAt)) {
                i5 = i7;
            }
        }
        while (i4 < length) {
            char cCharAt2 = spanned.charAt(i4);
            if (isSignChar(cCharAt2)) {
                return "";
            }
            if (isDecimalPointChar(cCharAt2)) {
                i5 = i4;
            }
            i4++;
        }
        SpannableStringBuilder spannableStringBuilder = null;
        for (int i8 = i2 - 1; i8 >= i; i8--) {
            char cCharAt3 = charSequence.charAt(i8);
            boolean z = true;
            if (isSignChar(cCharAt3)) {
                if (i8 == i && i3 == 0 && i6 < 0) {
                    i6 = i8;
                    z = false;
                }
            } else if (!isDecimalPointChar(cCharAt3)) {
                z = false;
            } else if (i5 < 0) {
                i5 = i8;
                z = false;
            }
            if (z) {
                if (i2 == i + 1) {
                    return "";
                }
                if (spannableStringBuilder == null) {
                    spannableStringBuilder = new SpannableStringBuilder(charSequence, i, i2);
                }
                spannableStringBuilder.delete(i8 - i, (i8 + 1) - i);
            }
        }
        if (spannableStringBuilder != null) {
            return spannableStringBuilder;
        }
        if (charSequenceFilter != null) {
            return charSequenceFilter;
        }
        return null;
    }
}
