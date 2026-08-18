package android.text.method;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public abstract class NumberKeyListener extends BaseKeyListener implements InputFilter {
    protected abstract char[] getAcceptedChars();

    protected int lookup(KeyEvent keyEvent, Spannable spannable) {
        return keyEvent.getMatch(getAcceptedChars(), getMetaState(spannable, keyEvent));
    }

    public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        char[] acceptedChars = getAcceptedChars();
        int i5 = i;
        while (i5 < i2 && ok(acceptedChars, charSequence.charAt(i5))) {
            i5++;
        }
        if (i5 == i2) {
            return null;
        }
        int i6 = i2 - i;
        if (i6 == 1) {
            return "";
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence, i, i2);
        int i7 = i5 - i;
        for (int i8 = i6 - 1; i8 >= i7; i8--) {
            if (!ok(acceptedChars, charSequence.charAt(i8))) {
                spannableStringBuilder.delete(i8, i8 + 1);
            }
        }
        return spannableStringBuilder;
    }

    protected static boolean ok(char[] cArr, char c) {
        for (int length = cArr.length - 1; length >= 0; length--) {
            if (cArr[length] == c) {
                return true;
            }
        }
        return false;
    }

    @Override // android.text.method.BaseKeyListener, android.text.method.MetaKeyKeyListener, android.text.method.KeyListener
    public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
        int selectionStart = Selection.getSelectionStart(editable);
        int selectionEnd = Selection.getSelectionEnd(editable);
        int iMin = Math.min(selectionStart, selectionEnd);
        int iMax = Math.max(selectionStart, selectionEnd);
        if (iMin < 0 || iMax < 0) {
            Selection.setSelection(editable, 0);
            iMax = 0;
            iMin = 0;
        }
        int iLookup = keyEvent != null ? lookup(keyEvent, editable) : 0;
        int repeatCount = keyEvent != null ? keyEvent.getRepeatCount() : 0;
        if (repeatCount == 0) {
            if (iLookup != 0) {
                if (iMin != iMax) {
                    Selection.setSelection(editable, iMax);
                }
                editable.replace(iMin, iMax, String.valueOf((char) iLookup));
                adjustMetaAfterKeypress(editable);
                return true;
            }
        } else if (iLookup == 48 && repeatCount == 1 && iMin == iMax && iMax > 0) {
            int i2 = iMin - 1;
            if (editable.charAt(i2) == '0') {
                editable.replace(i2, iMax, String.valueOf('+'));
                adjustMetaAfterKeypress(editable);
                return true;
            }
        }
        adjustMetaAfterKeypress(editable);
        return super.onKeyDown(view, editable, i, keyEvent);
    }
}
