package com.lianhexinye.m90.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.EditText;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class CompanyEdittext extends EditText {
    private int ceColor;
    private String ceText;
    private Context context;
    private TextWatcher textWatcher;

    public CompanyEdittext(Context context) {
        super(context);
        this.textWatcher = new TextWatcher() { // from class: com.lianhexinye.m90.common.widget.CompanyEdittext.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (CompanyEdittext.this.ceText.trim().isEmpty() || editable == null) {
                    return;
                }
                CompanyEdittext.this.removeTextChangedListener(this);
                if (!editable.toString().trim().equals(CompanyEdittext.this.ceText)) {
                    String str = editable.toString().replace(CompanyEdittext.this.ceText, "") + CompanyEdittext.this.ceText;
                    if (CompanyEdittext.this.ceColor != 0) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(CompanyEdittext.this.ceColor), str.length() - CompanyEdittext.this.ceText.length(), str.length(), 33);
                        CompanyEdittext.this.setText(spannableStringBuilder);
                    } else {
                        CompanyEdittext.this.setText(str);
                    }
                } else {
                    CompanyEdittext.this.setText("");
                }
                CompanyEdittext.this.addTextChangedListener(this);
            }
        };
        this.context = context;
        initView(null);
    }

    public CompanyEdittext(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.textWatcher = new TextWatcher() { // from class: com.lianhexinye.m90.common.widget.CompanyEdittext.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (CompanyEdittext.this.ceText.trim().isEmpty() || editable == null) {
                    return;
                }
                CompanyEdittext.this.removeTextChangedListener(this);
                if (!editable.toString().trim().equals(CompanyEdittext.this.ceText)) {
                    String str = editable.toString().replace(CompanyEdittext.this.ceText, "") + CompanyEdittext.this.ceText;
                    if (CompanyEdittext.this.ceColor != 0) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(CompanyEdittext.this.ceColor), str.length() - CompanyEdittext.this.ceText.length(), str.length(), 33);
                        CompanyEdittext.this.setText(spannableStringBuilder);
                    } else {
                        CompanyEdittext.this.setText(str);
                    }
                } else {
                    CompanyEdittext.this.setText("");
                }
                CompanyEdittext.this.addTextChangedListener(this);
            }
        };
        this.context = context;
        initView(attributeSet);
    }

    public CompanyEdittext(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.textWatcher = new TextWatcher() { // from class: com.lianhexinye.m90.common.widget.CompanyEdittext.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (CompanyEdittext.this.ceText.trim().isEmpty() || editable == null) {
                    return;
                }
                CompanyEdittext.this.removeTextChangedListener(this);
                if (!editable.toString().trim().equals(CompanyEdittext.this.ceText)) {
                    String str = editable.toString().replace(CompanyEdittext.this.ceText, "") + CompanyEdittext.this.ceText;
                    if (CompanyEdittext.this.ceColor != 0) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(CompanyEdittext.this.ceColor), str.length() - CompanyEdittext.this.ceText.length(), str.length(), 33);
                        CompanyEdittext.this.setText(spannableStringBuilder);
                    } else {
                        CompanyEdittext.this.setText(str);
                    }
                } else {
                    CompanyEdittext.this.setText("");
                }
                CompanyEdittext.this.addTextChangedListener(this);
            }
        };
        this.context = context;
        initView(attributeSet);
    }

    private void initView(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = this.context.obtainStyledAttributes(attributeSet, R.styleable.CompanyEdittext);
            this.ceText = typedArrayObtainStyledAttributes.getString(0);
            this.ceColor = typedArrayObtainStyledAttributes.getColor(1, 0);
            typedArrayObtainStyledAttributes.recycle();
        }
        addTextChangedListener(this.textWatcher);
    }

    @Override // android.widget.TextView
    protected void onSelectionChanged(int i, int i2) {
        super.onSelectionChanged(i, i2);
        if (!getText().toString().isEmpty() && i2 == getText().toString().length()) {
            setSelection(getText().toString().length() - this.ceText.length());
        } else {
            setSelection(i);
        }
    }
}
