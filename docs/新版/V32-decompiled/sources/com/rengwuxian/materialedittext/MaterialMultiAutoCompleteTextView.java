package com.rengwuxian.materialedittext;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.rengwuxian.materialedittext.validation.METLengthChecker;
import com.rengwuxian.materialedittext.validation.METValidator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class MaterialMultiAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {
    public static final int FLOATING_LABEL_HIGHLIGHT = 2;
    public static final int FLOATING_LABEL_NONE = 0;
    public static final int FLOATING_LABEL_NORMAL = 1;
    private Typeface accentTypeface;
    private boolean autoValidate;
    private int baseColor;
    private int bottomEllipsisSize;
    private float bottomLines;
    ObjectAnimator bottomLinesAnimator;
    private int bottomSpacing;
    private int bottomTextSize;
    private boolean charactersCountValid;
    private boolean checkCharactersCountAtBeginning;
    private Bitmap[] clearButtonBitmaps;
    private boolean clearButtonClicking;
    private boolean clearButtonTouched;
    private float currentBottomLines;
    private int errorColor;
    private int extraPaddingBottom;
    private int extraPaddingLeft;
    private int extraPaddingRight;
    private int extraPaddingTop;
    private boolean firstShown;
    private boolean floatingLabelAlwaysShown;
    private boolean floatingLabelAnimating;
    private boolean floatingLabelEnabled;
    private float floatingLabelFraction;
    private int floatingLabelPadding;
    private boolean floatingLabelShown;
    private CharSequence floatingLabelText;
    private int floatingLabelTextColor;
    private int floatingLabelTextSize;
    private ArgbEvaluator focusEvaluator;
    private float focusFraction;
    private String helperText;
    private boolean helperTextAlwaysShown;
    private int helperTextColor;
    private boolean hideUnderline;
    private boolean highlightFloatingLabel;
    private Bitmap[] iconLeftBitmaps;
    private int iconOuterHeight;
    private int iconOuterWidth;
    private int iconPadding;
    private Bitmap[] iconRightBitmaps;
    private int iconSize;
    View.OnFocusChangeListener innerFocusChangeListener;
    private int innerPaddingBottom;
    private int innerPaddingLeft;
    private int innerPaddingRight;
    private int innerPaddingTop;
    ObjectAnimator labelAnimator;
    ObjectAnimator labelFocusAnimator;
    private METLengthChecker lengthChecker;
    private int maxCharacters;
    private int minBottomLines;
    private int minBottomTextLines;
    private int minCharacters;
    View.OnFocusChangeListener outerFocusChangeListener;
    Paint paint;
    private int primaryColor;
    private boolean showClearButton;
    private boolean singleLineEllipsis;
    private String tempErrorText;
    private ColorStateList textColorHintStateList;
    private ColorStateList textColorStateList;
    StaticLayout textLayout;
    TextPaint textPaint;
    private Typeface typeface;
    private int underlineColor;
    private boolean validateOnFocusLost;
    private List<METValidator> validators;

    public @interface FloatingLabelType {
    }

    public MaterialMultiAutoCompleteTextView(Context context) {
        super(context);
        this.helperTextColor = -1;
        this.focusEvaluator = new ArgbEvaluator();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        init(context, null);
    }

    public MaterialMultiAutoCompleteTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.helperTextColor = -1;
        this.focusEvaluator = new ArgbEvaluator();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        init(context, attributeSet);
    }

    public MaterialMultiAutoCompleteTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.helperTextColor = -1;
        this.focusEvaluator = new ArgbEvaluator();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        int i;
        this.iconSize = getPixel(32);
        this.iconOuterWidth = getPixel(48);
        this.iconOuterHeight = getPixel(32);
        this.bottomSpacing = getResources().getDimensionPixelSize(R.dimen.inner_components_spacing);
        this.bottomEllipsisSize = getResources().getDimensionPixelSize(R.dimen.bottom_ellipsis_height);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MaterialEditText);
        this.textColorStateList = typedArrayObtainStyledAttributes.getColorStateList(R.styleable.MaterialEditText_met_textColor);
        this.textColorHintStateList = typedArrayObtainStyledAttributes.getColorStateList(R.styleable.MaterialEditText_met_textColorHint);
        this.baseColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MaterialEditText_met_baseColor, -16777216);
        TypedValue typedValue = new TypedValue();
        try {
            try {
            } catch (Exception unused) {
                int identifier = getResources().getIdentifier("colorPrimary", "attr", getContext().getPackageName());
                if (identifier != 0) {
                    context.getTheme().resolveAttribute(identifier, typedValue, true);
                    i = typedValue.data;
                } else {
                    throw new RuntimeException("colorPrimary not found");
                }
            }
        } catch (Exception unused2) {
            i = this.baseColor;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            context.getTheme().resolveAttribute(16843827, typedValue, true);
            i = typedValue.data;
            this.primaryColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MaterialEditText_met_primaryColor, i);
            setFloatingLabelInternal(typedArrayObtainStyledAttributes.getInt(R.styleable.MaterialEditText_met_floatingLabel, 0));
            this.errorColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MaterialEditText_met_errorColor, Color.parseColor("#e7492E"));
            this.minCharacters = typedArrayObtainStyledAttributes.getInt(R.styleable.MaterialEditText_met_minCharacters, 0);
            this.maxCharacters = typedArrayObtainStyledAttributes.getInt(R.styleable.MaterialEditText_met_maxCharacters, 0);
            this.singleLineEllipsis = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialEditText_met_singleLineEllipsis, false);
            this.helperText = typedArrayObtainStyledAttributes.getString(R.styleable.MaterialEditText_met_helperText);
            this.helperTextColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MaterialEditText_met_helperTextColor, -1);
            this.minBottomTextLines = typedArrayObtainStyledAttributes.getInt(R.styleable.MaterialEditText_met_minBottomTextLines, 0);
            String string = typedArrayObtainStyledAttributes.getString(R.styleable.MaterialEditText_met_accentTypeface);
            if (string != null && !isInEditMode()) {
                Typeface customTypeface = getCustomTypeface(string);
                this.accentTypeface = customTypeface;
                this.textPaint.setTypeface(customTypeface);
            }
            String string2 = typedArrayObtainStyledAttributes.getString(R.styleable.MaterialEditText_met_typeface);
            if (string2 != null && !isInEditMode()) {
                Typeface customTypeface2 = getCustomTypeface(string2);
                this.typeface = customTypeface2;
                setTypeface(customTypeface2);
            }
            String string3 = typedArrayObtainStyledAttributes.getString(R.styleable.MaterialEditText_met_floatingLabelText);
            this.floatingLabelText = string3;
            if (string3 == null) {
                this.floatingLabelText = getHint();
            }
            this.floatingLabelPadding = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MaterialEditText_met_floatingLabelPadding, this.bottomSpacing);
            this.floatingLabelTextSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MaterialEditText_met_floatingLabelTextSize, getResources().getDimensionPixelSize(R.dimen.floating_label_text_size));
            this.floatingLabelTextColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MaterialEditText_met_floatingLabelTextColor, -1);
            this.floatingLabelAnimating = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialEditText_met_floatingLabelAnimating, true);
            this.bottomTextSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MaterialEditText_met_bottomTextSize, getResources().getDimensionPixelSize(R.dimen.bottom_text_size));
            this.hideUnderline = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialEditText_met_hideUnderline, false);
            this.underlineColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MaterialEditText_met_underlineColor, -1);
            this.autoValidate = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialEditText_met_autoValidate, false);
            this.iconLeftBitmaps = generateIconBitmaps(typedArrayObtainStyledAttributes.getResourceId(R.styleable.MaterialEditText_met_iconLeft, -1));
            this.iconRightBitmaps = generateIconBitmaps(typedArrayObtainStyledAttributes.getResourceId(R.styleable.MaterialEditText_met_iconRight, -1));
            this.showClearButton = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialEditText_met_clearButton, false);
            this.clearButtonBitmaps = generateIconBitmaps(R.drawable.met_ic_clear);
            this.iconPadding = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MaterialEditText_met_iconPadding, getPixel(16));
            this.floatingLabelAlwaysShown = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialEditText_met_floatingLabelAlwaysShown, false);
            this.helperTextAlwaysShown = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialEditText_met_helperTextAlwaysShown, false);
            this.validateOnFocusLost = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialEditText_met_validateOnFocusLost, false);
            this.checkCharactersCountAtBeginning = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialEditText_met_checkCharactersCountAtBeginning, true);
            typedArrayObtainStyledAttributes.recycle();
            TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, new int[]{android.R.attr.padding, android.R.attr.paddingLeft, android.R.attr.paddingTop, android.R.attr.paddingRight, android.R.attr.paddingBottom});
            int dimensionPixelSize = typedArrayObtainStyledAttributes2.getDimensionPixelSize(0, 0);
            this.innerPaddingLeft = typedArrayObtainStyledAttributes2.getDimensionPixelSize(1, dimensionPixelSize);
            this.innerPaddingTop = typedArrayObtainStyledAttributes2.getDimensionPixelSize(2, dimensionPixelSize);
            this.innerPaddingRight = typedArrayObtainStyledAttributes2.getDimensionPixelSize(3, dimensionPixelSize);
            this.innerPaddingBottom = typedArrayObtainStyledAttributes2.getDimensionPixelSize(4, dimensionPixelSize);
            typedArrayObtainStyledAttributes2.recycle();
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(null);
            } else {
                setBackgroundDrawable(null);
            }
            if (this.singleLineEllipsis) {
                TransformationMethod transformationMethod = getTransformationMethod();
                setSingleLine();
                setTransformationMethod(transformationMethod);
            }
            initMinBottomLines();
            initPadding();
            initText();
            initFloatingLabel();
            initTextWatcher();
            checkCharactersCount();
            return;
        }
        throw new RuntimeException("SDK_INT less than LOLLIPOP");
    }

    private void initText() {
        if (!TextUtils.isEmpty(getText())) {
            Editable text = getText();
            setText((CharSequence) null);
            resetHintTextColor();
            setText(text);
            setSelection(text.length());
            this.floatingLabelFraction = 1.0f;
            this.floatingLabelShown = true;
        } else {
            resetHintTextColor();
        }
        resetTextColor();
    }

    private void initTextWatcher() {
        addTextChangedListener(new TextWatcher() { // from class: com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                MaterialMultiAutoCompleteTextView.this.checkCharactersCount();
                if (MaterialMultiAutoCompleteTextView.this.autoValidate) {
                    MaterialMultiAutoCompleteTextView.this.validate();
                } else {
                    MaterialMultiAutoCompleteTextView.this.setError(null);
                }
                MaterialMultiAutoCompleteTextView.this.postInvalidate();
            }
        });
    }

    private Typeface getCustomTypeface(String str) {
        return Typeface.createFromAsset(getContext().getAssets(), str);
    }

    public void setIconLeft(int i) {
        this.iconLeftBitmaps = generateIconBitmaps(i);
        initPadding();
    }

    public void setIconLeft(Drawable drawable) {
        this.iconLeftBitmaps = generateIconBitmaps(drawable);
        initPadding();
    }

    public void setIconLeft(Bitmap bitmap) {
        this.iconLeftBitmaps = generateIconBitmaps(bitmap);
        initPadding();
    }

    public void setIconRight(int i) {
        this.iconRightBitmaps = generateIconBitmaps(i);
        initPadding();
    }

    public void setIconRight(Drawable drawable) {
        this.iconRightBitmaps = generateIconBitmaps(drawable);
        initPadding();
    }

    public void setIconRight(Bitmap bitmap) {
        this.iconRightBitmaps = generateIconBitmaps(bitmap);
        initPadding();
    }

    public boolean isShowClearButton() {
        return this.showClearButton;
    }

    public void setShowClearButton(boolean z) {
        this.showClearButton = z;
        correctPaddings();
    }

    private Bitmap[] generateIconBitmaps(int i) throws Throwable {
        if (i == -1) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), i, options);
        int iMax = Math.max(options.outWidth, options.outHeight);
        int i2 = this.iconSize;
        options.inSampleSize = iMax > i2 ? iMax / i2 : 1;
        options.inJustDecodeBounds = false;
        return generateIconBitmaps(BitmapFactory.decodeResource(getResources(), i, options));
    }

    private Bitmap[] generateIconBitmaps(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        int i = this.iconSize;
        return generateIconBitmaps(Bitmap.createScaledBitmap(bitmapCreateBitmap, i, i, false));
    }

    private Bitmap[] generateIconBitmaps(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Bitmap[] bitmapArr = new Bitmap[4];
        Bitmap bitmapScaleIcon = scaleIcon(bitmap);
        bitmapArr[0] = bitmapScaleIcon.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapArr[0]);
        int i = this.baseColor;
        canvas.drawColor((Colors.isLight(i) ? -16777216 : -1979711488) | (i & 16777215), PorterDuff.Mode.SRC_IN);
        bitmapArr[1] = bitmapScaleIcon.copy(Bitmap.Config.ARGB_8888, true);
        new Canvas(bitmapArr[1]).drawColor(this.primaryColor, PorterDuff.Mode.SRC_IN);
        bitmapArr[2] = bitmapScaleIcon.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas2 = new Canvas(bitmapArr[2]);
        int i2 = this.baseColor;
        canvas2.drawColor((Colors.isLight(i2) ? 1275068416 : 1107296256) | (16777215 & i2), PorterDuff.Mode.SRC_IN);
        bitmapArr[3] = bitmapScaleIcon.copy(Bitmap.Config.ARGB_8888, true);
        new Canvas(bitmapArr[3]).drawColor(this.errorColor, PorterDuff.Mode.SRC_IN);
        return bitmapArr;
    }

    private Bitmap scaleIcon(Bitmap bitmap) {
        int i;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int iMax = Math.max(width, height);
        int i2 = this.iconSize;
        if (iMax == i2 || iMax <= i2) {
            return bitmap;
        }
        if (width > i2) {
            i = (int) (i2 * (height / width));
        } else {
            i2 = (int) (i2 * (width / height));
            i = i2;
        }
        return Bitmap.createScaledBitmap(bitmap, i2, i, false);
    }

    public float getFloatingLabelFraction() {
        return this.floatingLabelFraction;
    }

    public void setFloatingLabelFraction(float f) {
        this.floatingLabelFraction = f;
        invalidate();
    }

    public float getFocusFraction() {
        return this.focusFraction;
    }

    public void setFocusFraction(float f) {
        this.focusFraction = f;
        invalidate();
    }

    public float getCurrentBottomLines() {
        return this.currentBottomLines;
    }

    public void setCurrentBottomLines(float f) {
        this.currentBottomLines = f;
        initPadding();
    }

    public boolean isFloatingLabelAlwaysShown() {
        return this.floatingLabelAlwaysShown;
    }

    public void setFloatingLabelAlwaysShown(boolean z) {
        this.floatingLabelAlwaysShown = z;
        invalidate();
    }

    public boolean isHelperTextAlwaysShown() {
        return this.helperTextAlwaysShown;
    }

    public void setHelperTextAlwaysShown(boolean z) {
        this.helperTextAlwaysShown = z;
        invalidate();
    }

    public Typeface getAccentTypeface() {
        return this.accentTypeface;
    }

    public void setAccentTypeface(Typeface typeface) {
        this.accentTypeface = typeface;
        this.textPaint.setTypeface(typeface);
        postInvalidate();
    }

    public boolean isHideUnderline() {
        return this.hideUnderline;
    }

    public void setHideUnderline(boolean z) {
        this.hideUnderline = z;
        initPadding();
        postInvalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setUnderlineColor(int i) {
        this.underlineColor = i;
        postInvalidate();
    }

    public CharSequence getFloatingLabelText() {
        return this.floatingLabelText;
    }

    public void setFloatingLabelText(CharSequence charSequence) {
        if (charSequence == null) {
            charSequence = getHint();
        }
        this.floatingLabelText = charSequence;
        postInvalidate();
    }

    public int getFloatingLabelTextSize() {
        return this.floatingLabelTextSize;
    }

    public void setFloatingLabelTextSize(int i) {
        this.floatingLabelTextSize = i;
        initPadding();
    }

    public int getFloatingLabelTextColor() {
        return this.floatingLabelTextColor;
    }

    public void setFloatingLabelTextColor(int i) {
        this.floatingLabelTextColor = i;
        postInvalidate();
    }

    public int getBottomTextSize() {
        return this.bottomTextSize;
    }

    public void setBottomTextSize(int i) {
        this.bottomTextSize = i;
        initPadding();
    }

    private int getPixel(int i) {
        return Density.dp2px(getContext(), i);
    }

    private void initPadding() {
        this.extraPaddingTop = this.floatingLabelEnabled ? this.floatingLabelTextSize + this.floatingLabelPadding : this.floatingLabelPadding;
        this.textPaint.setTextSize(this.bottomTextSize);
        Paint.FontMetrics fontMetrics = this.textPaint.getFontMetrics();
        this.extraPaddingBottom = ((int) ((fontMetrics.descent - fontMetrics.ascent) * this.currentBottomLines)) + (this.hideUnderline ? this.bottomSpacing : this.bottomSpacing * 2);
        this.extraPaddingLeft = this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding;
        this.extraPaddingRight = this.iconRightBitmaps != null ? this.iconPadding + this.iconOuterWidth : 0;
        correctPaddings();
    }

    private void initMinBottomLines() {
        int i = 0;
        boolean z = this.minCharacters > 0 || this.maxCharacters > 0 || this.singleLineEllipsis || this.tempErrorText != null || this.helperText != null;
        int i2 = this.minBottomTextLines;
        if (i2 > 0) {
            i = i2;
        } else if (z) {
            i = 1;
        }
        this.minBottomLines = i;
        this.currentBottomLines = i;
    }

    @Override // android.widget.TextView, android.view.View
    @Deprecated
    public final void setPadding(int i, int i2, int i3, int i4) {
        super.setPadding(i, i2, i3, i4);
    }

    public void setPaddings(int i, int i2, int i3, int i4) {
        this.innerPaddingTop = i2;
        this.innerPaddingBottom = i4;
        this.innerPaddingLeft = i;
        this.innerPaddingRight = i3;
        correctPaddings();
    }

    private void correctPaddings() {
        int buttonsCount = this.iconOuterWidth * getButtonsCount();
        int i = 0;
        if (!isRTL()) {
            i = buttonsCount;
            buttonsCount = 0;
        }
        super.setPadding(this.innerPaddingLeft + this.extraPaddingLeft + buttonsCount, this.innerPaddingTop + this.extraPaddingTop, this.innerPaddingRight + this.extraPaddingRight + i, this.innerPaddingBottom + this.extraPaddingBottom);
    }

    private int getButtonsCount() {
        return isShowClearButton() ? 1 : 0;
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.firstShown) {
            return;
        }
        this.firstShown = true;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            adjustBottomLines();
        }
    }

    private boolean adjustBottomLines() {
        Layout.Alignment alignment;
        int iMax;
        if (getWidth() == 0) {
            return false;
        }
        this.textPaint.setTextSize(this.bottomTextSize);
        if (this.tempErrorText != null || this.helperText != null) {
            if ((getGravity() & 5) == 5 || isRTL()) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            } else {
                alignment = (getGravity() & 3) == 3 ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_CENTER;
            }
            Layout.Alignment alignment2 = alignment;
            String str = this.tempErrorText;
            if (str == null) {
                str = this.helperText;
            }
            StaticLayout staticLayout = new StaticLayout(str, this.textPaint, (((getWidth() - getBottomTextLeftOffset()) - getBottomTextRightOffset()) - getPaddingLeft()) - getPaddingRight(), alignment2, 1.0f, 0.0f, true);
            this.textLayout = staticLayout;
            iMax = Math.max(staticLayout.getLineCount(), this.minBottomTextLines);
        } else {
            iMax = this.minBottomLines;
        }
        float f = iMax;
        if (this.bottomLines != f) {
            getBottomLinesAnimator(f).start();
        }
        this.bottomLines = f;
        return true;
    }

    public int getInnerPaddingTop() {
        return this.innerPaddingTop;
    }

    public int getInnerPaddingBottom() {
        return this.innerPaddingBottom;
    }

    public int getInnerPaddingLeft() {
        return this.innerPaddingLeft;
    }

    public int getInnerPaddingRight() {
        return this.innerPaddingRight;
    }

    private void initFloatingLabel() {
        addTextChangedListener(new TextWatcher() { // from class: com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (MaterialMultiAutoCompleteTextView.this.floatingLabelEnabled) {
                    if (editable.length() == 0) {
                        if (MaterialMultiAutoCompleteTextView.this.floatingLabelShown) {
                            MaterialMultiAutoCompleteTextView.this.floatingLabelShown = false;
                            MaterialMultiAutoCompleteTextView.this.getLabelAnimator().reverse();
                            return;
                        }
                        return;
                    }
                    if (MaterialMultiAutoCompleteTextView.this.floatingLabelShown) {
                        return;
                    }
                    MaterialMultiAutoCompleteTextView.this.floatingLabelShown = true;
                    MaterialMultiAutoCompleteTextView.this.getLabelAnimator().start();
                }
            }
        });
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() { // from class: com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (MaterialMultiAutoCompleteTextView.this.floatingLabelEnabled && MaterialMultiAutoCompleteTextView.this.highlightFloatingLabel) {
                    if (z) {
                        MaterialMultiAutoCompleteTextView.this.getLabelFocusAnimator().start();
                    } else {
                        MaterialMultiAutoCompleteTextView.this.getLabelFocusAnimator().reverse();
                    }
                }
                if (MaterialMultiAutoCompleteTextView.this.validateOnFocusLost && !z) {
                    MaterialMultiAutoCompleteTextView.this.validate();
                }
                if (MaterialMultiAutoCompleteTextView.this.outerFocusChangeListener != null) {
                    MaterialMultiAutoCompleteTextView.this.outerFocusChangeListener.onFocusChange(view, z);
                }
            }
        };
        this.innerFocusChangeListener = onFocusChangeListener;
        super.setOnFocusChangeListener(onFocusChangeListener);
    }

    public boolean isValidateOnFocusLost() {
        return this.validateOnFocusLost;
    }

    public void setValidateOnFocusLost(boolean z) {
        this.validateOnFocusLost = z;
    }

    public void setBaseColor(int i) {
        if (this.baseColor != i) {
            this.baseColor = i;
        }
        initText();
        postInvalidate();
    }

    public void setPrimaryColor(int i) {
        this.primaryColor = i;
        postInvalidate();
    }

    public void setMetTextColor(int i) {
        this.textColorStateList = ColorStateList.valueOf(i);
        resetTextColor();
    }

    public void setMetTextColor(ColorStateList colorStateList) {
        this.textColorStateList = colorStateList;
        resetTextColor();
    }

    private void resetTextColor() {
        ColorStateList colorStateList = this.textColorStateList;
        if (colorStateList == null) {
            int[][] iArr = {new int[]{android.R.attr.state_enabled}, EMPTY_STATE_SET};
            int i = this.baseColor;
            ColorStateList colorStateList2 = new ColorStateList(iArr, new int[]{(i & 16777215) | (-553648128), (i & 16777215) | 1140850688});
            this.textColorStateList = colorStateList2;
            setTextColor(colorStateList2);
            return;
        }
        setTextColor(colorStateList);
    }

    public void setMetHintTextColor(int i) {
        this.textColorHintStateList = ColorStateList.valueOf(i);
        resetHintTextColor();
    }

    public void setMetHintTextColor(ColorStateList colorStateList) {
        this.textColorHintStateList = colorStateList;
        resetHintTextColor();
    }

    private void resetHintTextColor() {
        ColorStateList colorStateList = this.textColorHintStateList;
        if (colorStateList == null) {
            setHintTextColor((this.baseColor & 16777215) | 1140850688);
        } else {
            setHintTextColor(colorStateList);
        }
    }

    private void setFloatingLabelInternal(int i) {
        if (i == 1) {
            this.floatingLabelEnabled = true;
            this.highlightFloatingLabel = false;
        } else if (i == 2) {
            this.floatingLabelEnabled = true;
            this.highlightFloatingLabel = true;
        } else {
            this.floatingLabelEnabled = false;
            this.highlightFloatingLabel = false;
        }
    }

    public void setFloatingLabel(int i) {
        setFloatingLabelInternal(i);
        initPadding();
    }

    public int getFloatingLabelPadding() {
        return this.floatingLabelPadding;
    }

    public void setFloatingLabelPadding(int i) {
        this.floatingLabelPadding = i;
        postInvalidate();
    }

    public boolean isFloatingLabelAnimating() {
        return this.floatingLabelAnimating;
    }

    public void setFloatingLabelAnimating(boolean z) {
        this.floatingLabelAnimating = z;
    }

    public void setSingleLineEllipsis() {
        setSingleLineEllipsis(true);
    }

    public void setSingleLineEllipsis(boolean z) {
        this.singleLineEllipsis = z;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public int getMaxCharacters() {
        return this.maxCharacters;
    }

    public void setMaxCharacters(int i) {
        this.maxCharacters = i;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public int getMinCharacters() {
        return this.minCharacters;
    }

    public void setMinCharacters(int i) {
        this.minCharacters = i;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public int getMinBottomTextLines() {
        return this.minBottomTextLines;
    }

    public void setMinBottomTextLines(int i) {
        this.minBottomTextLines = i;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public boolean isAutoValidate() {
        return this.autoValidate;
    }

    public void setAutoValidate(boolean z) {
        this.autoValidate = z;
        if (z) {
            validate();
        }
    }

    public int getErrorColor() {
        return this.errorColor;
    }

    public void setErrorColor(int i) {
        this.errorColor = i;
        postInvalidate();
    }

    public void setHelperText(CharSequence charSequence) {
        this.helperText = charSequence == null ? null : charSequence.toString();
        if (adjustBottomLines()) {
            postInvalidate();
        }
    }

    public String getHelperText() {
        return this.helperText;
    }

    public int getHelperTextColor() {
        return this.helperTextColor;
    }

    public void setHelperTextColor(int i) {
        this.helperTextColor = i;
        postInvalidate();
    }

    @Override // android.widget.TextView
    public void setError(CharSequence charSequence) {
        this.tempErrorText = charSequence == null ? null : charSequence.toString();
        if (adjustBottomLines()) {
            postInvalidate();
        }
    }

    @Override // android.widget.TextView
    public CharSequence getError() {
        return this.tempErrorText;
    }

    private boolean isInternalValid() {
        return this.tempErrorText == null && isCharactersCountValid();
    }

    @Deprecated
    public boolean isValid(String str) {
        if (str == null) {
            return false;
        }
        return Pattern.compile(str).matcher(getText()).matches();
    }

    @Deprecated
    public boolean validate(String str, CharSequence charSequence) {
        boolean zIsValid = isValid(str);
        if (!zIsValid) {
            setError(charSequence);
        }
        postInvalidate();
        return zIsValid;
    }

    public boolean validateWith(METValidator mETValidator) {
        Editable text = getText();
        boolean zIsValid = mETValidator.isValid(text, text.length() == 0);
        if (!zIsValid) {
            setError(mETValidator.getErrorMessage());
        }
        postInvalidate();
        return zIsValid;
    }

    public boolean validate() {
        List<METValidator> list = this.validators;
        if (list == null || list.isEmpty()) {
            return true;
        }
        Editable text = getText();
        boolean z = text.length() == 0;
        Iterator<METValidator> it = this.validators.iterator();
        boolean z2 = true;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            METValidator next = it.next();
            z2 = z2 && next.isValid(text, z);
            if (!z2) {
                setError(next.getErrorMessage());
                break;
            }
        }
        if (z2) {
            setError(null);
        }
        postInvalidate();
        return z2;
    }

    public boolean hasValidators() {
        List<METValidator> list = this.validators;
        return (list == null || list.isEmpty()) ? false : true;
    }

    public MaterialMultiAutoCompleteTextView addValidator(METValidator mETValidator) {
        if (this.validators == null) {
            this.validators = new ArrayList();
        }
        this.validators.add(mETValidator);
        return this;
    }

    public void clearValidators() {
        List<METValidator> list = this.validators;
        if (list != null) {
            list.clear();
        }
    }

    public List<METValidator> getValidators() {
        return this.validators;
    }

    public void setLengthChecker(METLengthChecker mETLengthChecker) {
        this.lengthChecker = mETLengthChecker;
    }

    @Override // android.view.View
    public void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        if (this.innerFocusChangeListener == null) {
            super.setOnFocusChangeListener(onFocusChangeListener);
        } else {
            this.outerFocusChangeListener = onFocusChangeListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ObjectAnimator getLabelAnimator() {
        if (this.labelAnimator == null) {
            this.labelAnimator = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0.0f, 1.0f);
        }
        this.labelAnimator.setDuration(this.floatingLabelAnimating ? 300L : 0L);
        return this.labelAnimator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ObjectAnimator getLabelFocusAnimator() {
        if (this.labelFocusAnimator == null) {
            this.labelFocusAnimator = ObjectAnimator.ofFloat(this, "focusFraction", 0.0f, 1.0f);
        }
        return this.labelFocusAnimator;
    }

    private ObjectAnimator getBottomLinesAnimator(float f) {
        ObjectAnimator objectAnimator = this.bottomLinesAnimator;
        if (objectAnimator == null) {
            this.bottomLinesAnimator = ObjectAnimator.ofFloat(this, "currentBottomLines", f);
        } else {
            objectAnimator.cancel();
            this.bottomLinesAnimator.setFloatValues(f);
        }
        return this.bottomLinesAnimator;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        int innerPaddingLeft;
        int i;
        int i2;
        int scrollX = getScrollX() + (this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding);
        int scrollX2 = getScrollX() + (this.iconRightBitmaps == null ? getWidth() : (getWidth() - this.iconOuterWidth) - this.iconPadding);
        int scrollY = (getScrollY() + getHeight()) - getPaddingBottom();
        this.paint.setAlpha(255);
        Bitmap[] bitmapArr = this.iconLeftBitmaps;
        if (bitmapArr != null) {
            Bitmap bitmap = bitmapArr[!isInternalValid() ? (char) 3 : !isEnabled() ? (char) 2 : hasFocus() ? (char) 1 : (char) 0];
            int i3 = scrollX - this.iconPadding;
            int i4 = this.iconOuterWidth;
            int width = (i3 - i4) + ((i4 - bitmap.getWidth()) / 2);
            int i5 = this.bottomSpacing + scrollY;
            int i6 = this.iconOuterHeight;
            canvas.drawBitmap(bitmap, width, (i5 - i6) + ((i6 - bitmap.getHeight()) / 2), this.paint);
        }
        Bitmap[] bitmapArr2 = this.iconRightBitmaps;
        if (bitmapArr2 != null) {
            Bitmap bitmap2 = bitmapArr2[!isInternalValid() ? (char) 3 : !isEnabled() ? (char) 2 : hasFocus() ? (char) 1 : (char) 0];
            int width2 = this.iconPadding + scrollX2 + ((this.iconOuterWidth - bitmap2.getWidth()) / 2);
            int i7 = this.bottomSpacing + scrollY;
            int i8 = this.iconOuterHeight;
            canvas.drawBitmap(bitmap2, width2, (i7 - i8) + ((i8 - bitmap2.getHeight()) / 2), this.paint);
        }
        if (hasFocus() && this.showClearButton && !TextUtils.isEmpty(getText())) {
            this.paint.setAlpha(255);
            int i9 = isRTL() ? scrollX : scrollX2 - this.iconOuterWidth;
            Bitmap bitmap3 = this.clearButtonBitmaps[0];
            int width3 = i9 + ((this.iconOuterWidth - bitmap3.getWidth()) / 2);
            int i10 = this.bottomSpacing + scrollY;
            int i11 = this.iconOuterHeight;
            canvas.drawBitmap(bitmap3, width3, (i10 - i11) + ((i11 - bitmap3.getHeight()) / 2), this.paint);
        }
        if (!this.hideUnderline) {
            int i12 = scrollY + this.bottomSpacing;
            if (!isInternalValid()) {
                this.paint.setColor(this.errorColor);
                i2 = i12;
                canvas.drawRect(scrollX, i12, scrollX2, getPixel(2) + i12, this.paint);
            } else {
                i2 = i12;
                if (!isEnabled()) {
                    Paint paint = this.paint;
                    int i13 = this.underlineColor;
                    if (i13 == -1) {
                        i13 = (this.baseColor & 16777215) | 1140850688;
                    }
                    paint.setColor(i13);
                    float pixel = getPixel(1);
                    float f = 0.0f;
                    while (f < getWidth()) {
                        float f2 = scrollX + f;
                        float f3 = pixel;
                        canvas.drawRect(f2, i2, f2 + pixel, getPixel(1) + i2, this.paint);
                        f += f3 * 3.0f;
                        pixel = f3;
                    }
                } else if (hasFocus()) {
                    this.paint.setColor(this.primaryColor);
                    canvas.drawRect(scrollX, i2, scrollX2, i2 + getPixel(2), this.paint);
                } else {
                    Paint paint2 = this.paint;
                    int i14 = this.underlineColor;
                    if (i14 == -1) {
                        i14 = (this.baseColor & 16777215) | 503316480;
                    }
                    paint2.setColor(i14);
                    canvas.drawRect(scrollX, i2, scrollX2, i2 + getPixel(1), this.paint);
                }
            }
            scrollY = i2;
        }
        this.textPaint.setTextSize(this.bottomTextSize);
        Paint.FontMetrics fontMetrics = this.textPaint.getFontMetrics();
        float f4 = (-fontMetrics.ascent) - fontMetrics.descent;
        float f5 = this.bottomTextSize + fontMetrics.ascent + fontMetrics.descent;
        if ((hasFocus() && hasCharactersCounter()) || !isCharactersCountValid()) {
            this.textPaint.setColor(isCharactersCountValid() ? (this.baseColor & 16777215) | 1140850688 : this.errorColor);
            String charactersCounterText = getCharactersCounterText();
            canvas.drawText(charactersCounterText, isRTL() ? scrollX : scrollX2 - this.textPaint.measureText(charactersCounterText), this.bottomSpacing + scrollY + f4, this.textPaint);
        }
        if (this.textLayout != null && (this.tempErrorText != null || ((this.helperTextAlwaysShown || hasFocus()) && !TextUtils.isEmpty(this.helperText)))) {
            TextPaint textPaint = this.textPaint;
            if (this.tempErrorText != null) {
                i = this.errorColor;
            } else {
                i = this.helperTextColor;
                if (i == -1) {
                    i = (this.baseColor & 16777215) | 1140850688;
                }
            }
            textPaint.setColor(i);
            canvas.save();
            if (isRTL()) {
                canvas.translate(scrollX2 - this.textLayout.getWidth(), (this.bottomSpacing + scrollY) - f5);
            } else {
                canvas.translate(getBottomTextLeftOffset() + scrollX, (this.bottomSpacing + scrollY) - f5);
            }
            this.textLayout.draw(canvas);
            canvas.restore();
        }
        if (this.floatingLabelEnabled && !TextUtils.isEmpty(this.floatingLabelText)) {
            this.textPaint.setTextSize(this.floatingLabelTextSize);
            TextPaint textPaint2 = this.textPaint;
            ArgbEvaluator argbEvaluator = this.focusEvaluator;
            float f6 = this.focusFraction;
            int i15 = this.floatingLabelTextColor;
            if (i15 == -1) {
                i15 = (this.baseColor & 16777215) | 1140850688;
            }
            textPaint2.setColor(((Integer) argbEvaluator.evaluate(f6, Integer.valueOf(i15), Integer.valueOf(this.primaryColor))).intValue());
            float fMeasureText = this.textPaint.measureText(this.floatingLabelText.toString());
            if ((getGravity() & 5) == 5 || isRTL()) {
                innerPaddingLeft = (int) (scrollX2 - fMeasureText);
            } else {
                innerPaddingLeft = (getGravity() & 3) == 3 ? scrollX : ((int) (getInnerPaddingLeft() + ((((getWidth() - getInnerPaddingLeft()) - getInnerPaddingRight()) - fMeasureText) / 2.0f))) + scrollX;
            }
            int scrollY2 = (int) ((((this.innerPaddingTop + this.floatingLabelTextSize) + r4) - (this.floatingLabelPadding * (this.floatingLabelAlwaysShown ? 1.0f : this.floatingLabelFraction))) + getScrollY());
            this.textPaint.setAlpha((int) ((this.floatingLabelAlwaysShown ? 1.0f : this.floatingLabelFraction) * 255.0f * ((this.focusFraction * 0.74f) + 0.26f) * (this.floatingLabelTextColor == -1 ? Color.alpha(r6) / 256.0f : 1.0f)));
            canvas.drawText(this.floatingLabelText.toString(), innerPaddingLeft, scrollY2, this.textPaint);
        }
        if (hasFocus() && this.singleLineEllipsis && getScrollX() != 0) {
            this.paint.setColor(isInternalValid() ? this.primaryColor : this.errorColor);
            float f7 = scrollY + this.bottomSpacing;
            if (isRTL()) {
                scrollX = scrollX2;
            }
            int i16 = isRTL() ? -1 : 1;
            int i17 = this.bottomEllipsisSize;
            canvas.drawCircle(((i16 * i17) / 2) + scrollX, (i17 / 2) + f7, i17 / 2, this.paint);
            int i18 = this.bottomEllipsisSize;
            canvas.drawCircle((((i16 * i18) * 5) / 2) + scrollX, (i18 / 2) + f7, i18 / 2, this.paint);
            int i19 = this.bottomEllipsisSize;
            canvas.drawCircle(scrollX + (((i16 * i19) * 9) / 2), f7 + (i19 / 2), i19 / 2, this.paint);
        }
        super.onDraw(canvas);
    }

    private boolean isRTL() {
        return Build.VERSION.SDK_INT >= 17 && getResources().getConfiguration().getLayoutDirection() == 1;
    }

    private int getBottomTextLeftOffset() {
        return isRTL() ? getCharactersCounterWidth() : getBottomEllipsisWidth();
    }

    private int getBottomTextRightOffset() {
        return isRTL() ? getBottomEllipsisWidth() : getCharactersCounterWidth();
    }

    private int getCharactersCounterWidth() {
        if (hasCharactersCounter()) {
            return (int) this.textPaint.measureText(getCharactersCounterText());
        }
        return 0;
    }

    private int getBottomEllipsisWidth() {
        if (this.singleLineEllipsis) {
            return (this.bottomEllipsisSize * 5) + getPixel(4);
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkCharactersCount() {
        int i;
        boolean z = true;
        if ((!this.firstShown && !this.checkCharactersCountAtBeginning) || !hasCharactersCounter()) {
            this.charactersCountValid = true;
            return;
        }
        Editable text = getText();
        int iCheckLength = text == null ? 0 : checkLength(text);
        if (iCheckLength < this.minCharacters || ((i = this.maxCharacters) > 0 && iCheckLength > i)) {
            z = false;
        }
        this.charactersCountValid = z;
    }

    public boolean isCharactersCountValid() {
        return this.charactersCountValid;
    }

    private boolean hasCharactersCounter() {
        return this.minCharacters > 0 || this.maxCharacters > 0;
    }

    private String getCharactersCounterText() {
        StringBuilder sbAppend;
        int iCheckLength;
        StringBuilder sbAppend2;
        int iCheckLength2;
        if (this.minCharacters <= 0) {
            if (isRTL()) {
                sbAppend2 = new StringBuilder().append(this.maxCharacters).append(" / ");
                iCheckLength2 = checkLength(getText());
            } else {
                sbAppend2 = new StringBuilder().append(checkLength(getText())).append(" / ");
                iCheckLength2 = this.maxCharacters;
            }
            return sbAppend2.append(iCheckLength2).toString();
        }
        if (this.maxCharacters <= 0) {
            return (isRTL() ? new StringBuilder().append("+").append(this.minCharacters).append(" / ").append(checkLength(getText())) : new StringBuilder().append(checkLength(getText())).append(" / ").append(this.minCharacters).append("+")).toString();
        }
        if (isRTL()) {
            sbAppend = new StringBuilder().append(this.maxCharacters).append("-").append(this.minCharacters).append(" / ");
            iCheckLength = checkLength(getText());
        } else {
            sbAppend = new StringBuilder().append(checkLength(getText())).append(" / ").append(this.minCharacters).append("-");
            iCheckLength = this.maxCharacters;
        }
        return sbAppend.append(iCheckLength).toString();
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.singleLineEllipsis && getScrollX() > 0 && motionEvent.getAction() == 0 && motionEvent.getX() < getPixel(20) && motionEvent.getY() > (getHeight() - this.extraPaddingBottom) - this.innerPaddingBottom && motionEvent.getY() < getHeight() - this.innerPaddingBottom) {
            setSelection(0);
            return false;
        }
        if (hasFocus() && this.showClearButton) {
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1) {
                    if (this.clearButtonClicking) {
                        if (!TextUtils.isEmpty(getText())) {
                            setText((CharSequence) null);
                        }
                        this.clearButtonClicking = false;
                    }
                    if (this.clearButtonTouched) {
                        this.clearButtonTouched = false;
                        return true;
                    }
                    this.clearButtonTouched = false;
                } else if (action != 2) {
                    if (action == 3) {
                        this.clearButtonTouched = false;
                        this.clearButtonClicking = false;
                    }
                }
            } else if (insideClearButton(motionEvent)) {
                this.clearButtonTouched = true;
                this.clearButtonClicking = true;
                return true;
            }
            if (this.clearButtonClicking && !insideClearButton(motionEvent)) {
                this.clearButtonClicking = false;
            }
            if (this.clearButtonTouched) {
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private boolean insideClearButton(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int scrollX = getScrollX() + (this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding);
        int scrollX2 = getScrollX() + (this.iconRightBitmaps == null ? getWidth() : (getWidth() - this.iconOuterWidth) - this.iconPadding);
        if (!isRTL()) {
            scrollX = scrollX2 - this.iconOuterWidth;
        }
        int scrollY = ((getScrollY() + getHeight()) - getPaddingBottom()) + this.bottomSpacing;
        int i = this.iconOuterHeight;
        int i2 = scrollY - i;
        return x >= ((float) scrollX) && x < ((float) (scrollX + this.iconOuterWidth)) && y >= ((float) i2) && y < ((float) (i2 + i));
    }

    private int checkLength(CharSequence charSequence) {
        METLengthChecker mETLengthChecker = this.lengthChecker;
        return mETLengthChecker == null ? charSequence.length() : mETLengthChecker.getLength(charSequence);
    }
}
