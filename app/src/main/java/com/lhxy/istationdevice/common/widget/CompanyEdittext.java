package com.lhxy.istationdevice.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.lhxy.istationdevice.android11.app.R;

/**
 * 兼容旧布局里的企业输入框。
 */
@SuppressLint("AppCompatCustomView")
public class CompanyEdittext extends EditText {
    private final Context context;
    private String ceText = "";
    private int ceColor;

    public CompanyEdittext(Context context) {
        super(context);
        this.context = context;
        initView(null);
    }

    public CompanyEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(attrs);
    }

    public CompanyEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CompanyEdittext);
            String value = array.getString(R.styleable.CompanyEdittext_ce_text);
            ceText = value == null ? "" : value;
            ceColor = array.getColor(R.styleable.CompanyEdittext_ce_text_color, 0);
            array.recycle();
        }
        addTextChangedListener(textWatcher);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (ceText.trim().isEmpty()) {
                return;
            }
            if (s != null) {
                removeTextChangedListener(this);
                if (s.toString().trim().equals(ceText)) {
                    setText("");
                } else {
                    String value = s.toString().replace(ceText, "") + ceText;
                    if (ceColor != 0) {
                        SpannableStringBuilder builder = new SpannableStringBuilder(value);
                        builder.setSpan(
                                new ForegroundColorSpan(ceColor),
                                value.length() - ceText.length(),
                                value.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        setText(builder);
                    } else {
                        setText(value);
                    }
                }
                addTextChangedListener(this);
            }
        }
    };

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        String value = getText() == null ? "" : getText().toString();
        if (!value.isEmpty() && !ceText.isEmpty() && selEnd == value.length()) {
            setSelection(Math.max(0, value.length() - ceText.length()));
        } else {
            setSelection(selStart);
        }
    }
}
