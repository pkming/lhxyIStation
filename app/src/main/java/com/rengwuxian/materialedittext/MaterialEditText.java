package com.rengwuxian.materialedittext;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.view.ViewCompat;

import com.lhxy.istationdevice.android11.app.R;

/**
 * Local compatibility shim for the legacy MaterialEditText widget.
 * It keeps the old XML class name and custom attrs used by the M90 password page
 * without pulling the incompatible legacy library into the AndroidX resource merge.
 */
public class MaterialEditText extends AppCompatEditText {
    public MaterialEditText(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText);
        try {
            int baseColor = typedArray.getColor(R.styleable.MaterialEditText_met_baseColor, Color.TRANSPARENT);
            if (baseColor != Color.TRANSPARENT) {
                setHintTextColor(baseColor);
            }

            if (typedArray.getBoolean(R.styleable.MaterialEditText_met_singleLineEllipsis, false)) {
                setSingleLine(true);
                setEllipsize(TextUtils.TruncateAt.END);
            }

            ColorStateList tintList = resolveTintList(typedArray);
            if (tintList != null) {
                ViewCompat.setBackgroundTintList(this, tintList);
            }
        } finally {
            typedArray.recycle();
        }
    }

    @Nullable
    private ColorStateList resolveTintList(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.MaterialEditText_met_underlineColor)) {
            int underlineColor = typedArray.getColor(R.styleable.MaterialEditText_met_underlineColor, Color.TRANSPARENT);
            if (underlineColor != Color.TRANSPARENT) {
                return ColorStateList.valueOf(underlineColor);
            }
        }
        if (typedArray.hasValue(R.styleable.MaterialEditText_met_primaryColor)) {
            int primaryColor = typedArray.getColor(R.styleable.MaterialEditText_met_primaryColor, Color.TRANSPARENT);
            if (primaryColor != Color.TRANSPARENT) {
                return ColorStateList.valueOf(primaryColor);
            }
        }
        return null;
    }
}