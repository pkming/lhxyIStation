package android.widget;

import android.R;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;

/* JADX INFO: loaded from: classes.dex */
public class DialerFilter extends RelativeLayout {
    public static final int DIGITS_AND_LETTERS = 1;
    public static final int DIGITS_AND_LETTERS_NO_DIGITS = 2;
    public static final int DIGITS_AND_LETTERS_NO_LETTERS = 3;
    public static final int DIGITS_ONLY = 4;
    public static final int LETTERS_ONLY = 5;
    EditText mDigits;
    EditText mHint;
    ImageView mIcon;
    InputFilter[] mInputFilters;
    private boolean mIsQwerty;
    EditText mLetters;
    int mMode;
    EditText mPrimary;

    protected void onModeChange(int i, int i2) {
    }

    public DialerFilter(Context context) {
        super(context);
    }

    public DialerFilter(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mInputFilters = new InputFilter[]{new InputFilter.AllCaps()};
        EditText editText = (EditText) findViewById(R.id.hint);
        this.mHint = editText;
        if (editText == null) {
            throw new IllegalStateException("DialerFilter must have a child EditText named hint");
        }
        editText.setFilters(this.mInputFilters);
        EditText editText2 = this.mHint;
        this.mLetters = editText2;
        editText2.setKeyListener(TextKeyListener.getInstance());
        this.mLetters.setMovementMethod(null);
        this.mLetters.setFocusable(false);
        EditText editText3 = (EditText) findViewById(R.id.primary);
        this.mPrimary = editText3;
        if (editText3 == null) {
            throw new IllegalStateException("DialerFilter must have a child EditText named primary");
        }
        editText3.setFilters(this.mInputFilters);
        EditText editText4 = this.mPrimary;
        this.mDigits = editText4;
        editText4.setKeyListener(DialerKeyListener.getInstance());
        this.mDigits.setMovementMethod(null);
        this.mDigits.setFocusable(false);
        this.mIcon = (ImageView) findViewById(R.id.icon);
        setFocusable(true);
        this.mIsQwerty = true;
        setMode(1);
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        ImageView imageView = this.mIcon;
        if (imageView != null) {
            imageView.setVisibility(z ? 0 : 8);
        }
    }

    public boolean isQwertyKeyboard() {
        return this.mIsQwerty;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0027  */
    @Override // android.view.View, android.view.KeyEvent.Callback
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onKeyDown(int r8, android.view.KeyEvent r9) {
        /*
            Method dump skipped, instruction units count: 236
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.DialerFilter.onKeyDown(int, android.view.KeyEvent):boolean");
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return this.mLetters.onKeyUp(i, keyEvent) || this.mDigits.onKeyUp(i, keyEvent);
    }

    public int getMode() {
        return this.mMode;
    }

    public void setMode(int i) {
        if (i == 1) {
            makeDigitsPrimary();
            this.mLetters.setVisibility(0);
            this.mDigits.setVisibility(0);
        } else if (i == 2) {
            makeLettersPrimary();
            this.mLetters.setVisibility(0);
            this.mDigits.setVisibility(4);
        } else if (i == 3) {
            makeDigitsPrimary();
            this.mLetters.setVisibility(4);
            this.mDigits.setVisibility(0);
        } else if (i == 4) {
            makeDigitsPrimary();
            this.mLetters.setVisibility(8);
            this.mDigits.setVisibility(0);
        } else if (i == 5) {
            makeLettersPrimary();
            this.mLetters.setVisibility(0);
            this.mDigits.setVisibility(8);
        }
        int i2 = this.mMode;
        this.mMode = i;
        onModeChange(i2, i);
    }

    private void makeLettersPrimary() {
        if (this.mPrimary == this.mDigits) {
            swapPrimaryAndHint(true);
        }
    }

    private void makeDigitsPrimary() {
        if (this.mPrimary == this.mLetters) {
            swapPrimaryAndHint(false);
        }
    }

    private void swapPrimaryAndHint(boolean z) {
        Editable text = this.mLetters.getText();
        Editable text2 = this.mDigits.getText();
        KeyListener keyListener = this.mLetters.getKeyListener();
        KeyListener keyListener2 = this.mDigits.getKeyListener();
        if (z) {
            this.mLetters = this.mPrimary;
            this.mDigits = this.mHint;
        } else {
            this.mLetters = this.mHint;
            this.mDigits = this.mPrimary;
        }
        this.mLetters.setKeyListener(keyListener);
        this.mLetters.setText(text);
        Editable text3 = this.mLetters.getText();
        Selection.setSelection(text3, text3.length());
        this.mDigits.setKeyListener(keyListener2);
        this.mDigits.setText(text2);
        Editable text4 = this.mDigits.getText();
        Selection.setSelection(text4, text4.length());
        this.mPrimary.setFilters(this.mInputFilters);
        this.mHint.setFilters(this.mInputFilters);
    }

    public CharSequence getLetters() {
        return this.mLetters.getVisibility() == 0 ? this.mLetters.getText() : "";
    }

    public CharSequence getDigits() {
        return this.mDigits.getVisibility() == 0 ? this.mDigits.getText() : "";
    }

    public CharSequence getFilterText() {
        if (this.mMode != 4) {
            return getLetters();
        }
        return getDigits();
    }

    public void append(String str) {
        int i = this.mMode;
        if (i == 1) {
            this.mDigits.getText().append((CharSequence) str);
            this.mLetters.getText().append((CharSequence) str);
            return;
        }
        if (i != 2) {
            if (i == 3 || i == 4) {
                this.mDigits.getText().append((CharSequence) str);
                return;
            } else if (i != 5) {
                return;
            }
        }
        this.mLetters.getText().append((CharSequence) str);
    }

    public void clearText() {
        this.mLetters.getText().clear();
        this.mDigits.getText().clear();
        if (this.mIsQwerty) {
            setMode(1);
        } else {
            setMode(4);
        }
    }

    public void setLettersWatcher(TextWatcher textWatcher) {
        Editable text = this.mLetters.getText();
        text.setSpan(textWatcher, 0, text.length(), 18);
    }

    public void setDigitsWatcher(TextWatcher textWatcher) {
        Editable text = this.mDigits.getText();
        text.setSpan(textWatcher, 0, text.length(), 18);
    }

    public void setFilterWatcher(TextWatcher textWatcher) {
        if (this.mMode != 4) {
            setLettersWatcher(textWatcher);
        } else {
            setDigitsWatcher(textWatcher);
        }
    }

    public void removeFilterWatcher(TextWatcher textWatcher) {
        Editable text;
        if (this.mMode != 4) {
            text = this.mLetters.getText();
        } else {
            text = this.mDigits.getText();
        }
        text.removeSpan(textWatcher);
    }
}
