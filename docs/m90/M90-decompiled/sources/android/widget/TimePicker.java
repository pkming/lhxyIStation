package android.widget;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.NumberPicker;
import com.android.internal.R;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class TimePicker extends FrameLayout {
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() { // from class: android.widget.TimePicker.1
        @Override // android.widget.TimePicker.OnTimeChangedListener
        public void onTimeChanged(TimePicker timePicker, int i, int i2) {
        }
    };
    private final Button mAmPmButton;
    private final NumberPicker mAmPmSpinner;
    private final EditText mAmPmSpinnerInput;
    private final String[] mAmPmStrings;
    private Locale mCurrentLocale;
    private final TextView mDivider;
    private char mHourFormat;
    private final NumberPicker mHourSpinner;
    private final EditText mHourSpinnerInput;
    private boolean mHourWithTwoDigit;
    private boolean mIs24HourView;
    private boolean mIsAm;
    private boolean mIsEnabled;
    private final NumberPicker mMinuteSpinner;
    private final EditText mMinuteSpinnerInput;
    private OnTimeChangedListener mOnTimeChangedListener;
    private Calendar mTempCalendar;

    public interface OnTimeChangedListener {
        void onTimeChanged(TimePicker timePicker, int i, int i2);
    }

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16843780);
    }

    public TimePicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsEnabled = true;
        setCurrentLocale(Locale.getDefault());
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TimePicker, i, 0);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, 17367218);
        typedArrayObtainStyledAttributes.recycle();
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resourceId, (ViewGroup) this, true);
        NumberPicker numberPicker = (NumberPicker) findViewById(16909136);
        this.mHourSpinner = numberPicker;
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: android.widget.TimePicker.2
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker2, int i2, int i3) {
                TimePicker.this.updateInputState();
                if (!TimePicker.this.is24HourView() && ((i2 == 11 && i3 == 12) || (i2 == 12 && i3 == 11))) {
                    TimePicker.this.mIsAm = !r2.mIsAm;
                    TimePicker.this.updateAmPmControl();
                }
                TimePicker.this.onTimeChanged();
            }
        });
        EditText editText = (EditText) numberPicker.findViewById(16909039);
        this.mHourSpinnerInput = editText;
        editText.setImeOptions(5);
        TextView textView = (TextView) findViewById(16909140);
        this.mDivider = textView;
        if (textView != null) {
            setDividerText();
        }
        NumberPicker numberPicker2 = (NumberPicker) findViewById(16909137);
        this.mMinuteSpinner = numberPicker2;
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(59);
        numberPicker2.setOnLongPressUpdateInterval(100L);
        numberPicker2.setFormatter(NumberPicker.getTwoDigitFormatter());
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: android.widget.TimePicker.3
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker3, int i2, int i3) {
                TimePicker.this.updateInputState();
                int minValue = TimePicker.this.mMinuteSpinner.getMinValue();
                int maxValue = TimePicker.this.mMinuteSpinner.getMaxValue();
                if (i2 == maxValue && i3 == minValue) {
                    int value = TimePicker.this.mHourSpinner.getValue() + 1;
                    if (!TimePicker.this.is24HourView() && value == 12) {
                        TimePicker.this.mIsAm = !r3.mIsAm;
                        TimePicker.this.updateAmPmControl();
                    }
                    TimePicker.this.mHourSpinner.setValue(value);
                } else if (i2 == minValue && i3 == maxValue) {
                    int value2 = TimePicker.this.mHourSpinner.getValue() - 1;
                    if (!TimePicker.this.is24HourView() && value2 == 11) {
                        TimePicker.this.mIsAm = !r3.mIsAm;
                        TimePicker.this.updateAmPmControl();
                    }
                    TimePicker.this.mHourSpinner.setValue(value2);
                }
                TimePicker.this.onTimeChanged();
            }
        });
        EditText editText2 = (EditText) numberPicker2.findViewById(16909039);
        this.mMinuteSpinnerInput = editText2;
        editText2.setImeOptions(5);
        String[] amPmStrings = new DateFormatSymbols().getAmPmStrings();
        this.mAmPmStrings = amPmStrings;
        View viewFindViewById = findViewById(16909138);
        if (viewFindViewById instanceof Button) {
            this.mAmPmSpinner = null;
            this.mAmPmSpinnerInput = null;
            Button button = (Button) viewFindViewById;
            this.mAmPmButton = button;
            button.setOnClickListener(new View.OnClickListener() { // from class: android.widget.TimePicker.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    view.requestFocus();
                    TimePicker.this.mIsAm = !r2.mIsAm;
                    TimePicker.this.updateAmPmControl();
                    TimePicker.this.onTimeChanged();
                }
            });
        } else {
            this.mAmPmButton = null;
            NumberPicker numberPicker3 = (NumberPicker) viewFindViewById;
            this.mAmPmSpinner = numberPicker3;
            numberPicker3.setMinValue(0);
            numberPicker3.setMaxValue(1);
            numberPicker3.setDisplayedValues(amPmStrings);
            numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: android.widget.TimePicker.5
                @Override // android.widget.NumberPicker.OnValueChangeListener
                public void onValueChange(NumberPicker numberPicker4, int i2, int i3) {
                    TimePicker.this.updateInputState();
                    numberPicker4.requestFocus();
                    TimePicker.this.mIsAm = !r1.mIsAm;
                    TimePicker.this.updateAmPmControl();
                    TimePicker.this.onTimeChanged();
                }
            });
            EditText editText3 = (EditText) numberPicker3.findViewById(16909039);
            this.mAmPmSpinnerInput = editText3;
            editText3.setImeOptions(6);
        }
        if (isAmPmAtStart()) {
            ViewGroup viewGroup = (ViewGroup) findViewById(16909135);
            viewGroup.removeView(viewFindViewById);
            viewGroup.addView(viewFindViewById, 0);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) viewFindViewById.getLayoutParams();
            int marginStart = marginLayoutParams.getMarginStart();
            int marginEnd = marginLayoutParams.getMarginEnd();
            if (marginStart != marginEnd) {
                marginLayoutParams.setMarginStart(marginEnd);
                marginLayoutParams.setMarginEnd(marginStart);
            }
        }
        getHourFormatData();
        updateHourControl();
        updateMinuteControl();
        updateAmPmControl();
        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);
        setCurrentHour(Integer.valueOf(this.mTempCalendar.get(11)));
        setCurrentMinute(Integer.valueOf(this.mTempCalendar.get(12)));
        if (!isEnabled()) {
            setEnabled(false);
        }
        setContentDescriptions();
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    private void getHourFormatData() {
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), this.mIs24HourView ? "Hm" : "hm");
        int length = bestDateTimePattern.length();
        this.mHourWithTwoDigit = false;
        for (int i = 0; i < length; i++) {
            char cCharAt = bestDateTimePattern.charAt(i);
            if (cCharAt == 'H' || cCharAt == 'h' || cCharAt == 'K' || cCharAt == 'k') {
                this.mHourFormat = cCharAt;
                int i2 = i + 1;
                if (i2 >= length || cCharAt != bestDateTimePattern.charAt(i2)) {
                    return;
                }
                this.mHourWithTwoDigit = true;
                return;
            }
        }
    }

    private boolean isAmPmAtStart() {
        return DateFormat.getBestDateTimePattern(Locale.getDefault(), "hm").startsWith(FullBackup.APK_TREE_TOKEN);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        if (this.mIsEnabled == z) {
            return;
        }
        super.setEnabled(z);
        this.mMinuteSpinner.setEnabled(z);
        TextView textView = this.mDivider;
        if (textView != null) {
            textView.setEnabled(z);
        }
        this.mHourSpinner.setEnabled(z);
        NumberPicker numberPicker = this.mAmPmSpinner;
        if (numberPicker != null) {
            numberPicker.setEnabled(z);
        } else {
            this.mAmPmButton.setEnabled(z);
        }
        this.mIsEnabled = z;
    }

    @Override // android.view.View
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setCurrentLocale(configuration.locale);
    }

    private void setCurrentLocale(Locale locale) {
        if (locale.equals(this.mCurrentLocale)) {
            return;
        }
        this.mCurrentLocale = locale;
        this.mTempCalendar = Calendar.getInstance(locale);
    }

    private static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.TimePicker.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        private final int mHour;
        private final int mMinute;

        private SavedState(Parcelable parcelable, int i, int i2) {
            super(parcelable);
            this.mHour = i;
            this.mMinute = i2;
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mHour = parcel.readInt();
            this.mMinute = parcel.readInt();
        }

        public int getHour() {
            return this.mHour;
        }

        public int getMinute() {
            return this.mMinute;
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mHour);
            parcel.writeInt(this.mMinute);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), getCurrentHour().intValue(), getCurrentMinute().intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setCurrentHour(Integer.valueOf(savedState.getHour()));
        setCurrentMinute(Integer.valueOf(savedState.getMinute()));
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.mOnTimeChangedListener = onTimeChangedListener;
    }

    public Integer getCurrentHour() {
        int value = this.mHourSpinner.getValue();
        if (is24HourView()) {
            return Integer.valueOf(value);
        }
        if (this.mIsAm) {
            return Integer.valueOf(value % 12);
        }
        return Integer.valueOf((value % 12) + 12);
    }

    public void setCurrentHour(Integer num) {
        setCurrentHour(num, true);
    }

    private void setCurrentHour(Integer num, boolean z) {
        if (num == null || num == getCurrentHour()) {
            return;
        }
        if (!is24HourView()) {
            if (num.intValue() >= 12) {
                this.mIsAm = false;
                if (num.intValue() > 12) {
                    num = Integer.valueOf(num.intValue() - 12);
                }
            } else {
                this.mIsAm = true;
                if (num.intValue() == 0) {
                    num = 12;
                }
            }
            updateAmPmControl();
        }
        this.mHourSpinner.setValue(num.intValue());
        if (z) {
            onTimeChanged();
        }
    }

    public void setIs24HourView(Boolean bool) {
        if (this.mIs24HourView == bool.booleanValue()) {
            return;
        }
        int iIntValue = getCurrentHour().intValue();
        this.mIs24HourView = bool.booleanValue();
        getHourFormatData();
        updateHourControl();
        setCurrentHour(Integer.valueOf(iIntValue), false);
        updateMinuteControl();
        updateAmPmControl();
    }

    public boolean is24HourView() {
        return this.mIs24HourView;
    }

    public Integer getCurrentMinute() {
        return Integer.valueOf(this.mMinuteSpinner.getValue());
    }

    public void setCurrentMinute(Integer num) {
        if (num == getCurrentMinute()) {
            return;
        }
        this.mMinuteSpinner.setValue(num.intValue());
        onTimeChanged();
    }

    private void setDividerText() {
        String strSubstring;
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), this.mIs24HourView ? "Hm" : "hm");
        int iLastIndexOf = bestDateTimePattern.lastIndexOf(72);
        if (iLastIndexOf == -1) {
            iLastIndexOf = bestDateTimePattern.lastIndexOf(104);
        }
        if (iLastIndexOf == -1) {
            strSubstring = ":";
        } else {
            int i = iLastIndexOf + 1;
            int iIndexOf = bestDateTimePattern.indexOf(109, i);
            if (iIndexOf == -1) {
                strSubstring = Character.toString(bestDateTimePattern.charAt(i));
            } else {
                strSubstring = bestDateTimePattern.substring(i, iIndexOf);
            }
        }
        this.mDivider.setText(strSubstring);
    }

    @Override // android.view.View
    public int getBaseline() {
        return this.mHourSpinner.getBaseline();
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        onPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        int i = this.mIs24HourView ? 129 : 65;
        this.mTempCalendar.set(11, getCurrentHour().intValue());
        this.mTempCalendar.set(12, getCurrentMinute().intValue());
        accessibilityEvent.getText().add(DateUtils.formatDateTime(this.mContext, this.mTempCalendar.getTimeInMillis(), i));
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(TimePicker.class.getName());
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(TimePicker.class.getName());
    }

    private void updateHourControl() {
        if (is24HourView()) {
            if (this.mHourFormat == 'k') {
                this.mHourSpinner.setMinValue(1);
                this.mHourSpinner.setMaxValue(24);
            } else {
                this.mHourSpinner.setMinValue(0);
                this.mHourSpinner.setMaxValue(23);
            }
        } else if (this.mHourFormat == 'K') {
            this.mHourSpinner.setMinValue(0);
            this.mHourSpinner.setMaxValue(11);
        } else {
            this.mHourSpinner.setMinValue(1);
            this.mHourSpinner.setMaxValue(12);
        }
        this.mHourSpinner.setFormatter(this.mHourWithTwoDigit ? NumberPicker.getTwoDigitFormatter() : null);
    }

    private void updateMinuteControl() {
        if (is24HourView()) {
            this.mMinuteSpinnerInput.setImeOptions(6);
        } else {
            this.mMinuteSpinnerInput.setImeOptions(5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAmPmControl() {
        if (is24HourView()) {
            NumberPicker numberPicker = this.mAmPmSpinner;
            if (numberPicker != null) {
                numberPicker.setVisibility(8);
            } else {
                this.mAmPmButton.setVisibility(8);
            }
        } else {
            int i = !this.mIsAm ? 1 : 0;
            NumberPicker numberPicker2 = this.mAmPmSpinner;
            if (numberPicker2 != null) {
                numberPicker2.setValue(i);
                this.mAmPmSpinner.setVisibility(0);
            } else {
                this.mAmPmButton.setText(this.mAmPmStrings[i]);
                this.mAmPmButton.setVisibility(0);
            }
        }
        sendAccessibilityEvent(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        sendAccessibilityEvent(4);
        OnTimeChangedListener onTimeChangedListener = this.mOnTimeChangedListener;
        if (onTimeChangedListener != null) {
            onTimeChangedListener.onTimeChanged(this, getCurrentHour().intValue(), getCurrentMinute().intValue());
        }
    }

    private void setContentDescriptions() {
        trySetContentDescription(this.mMinuteSpinner, 16909038, 17040623);
        trySetContentDescription(this.mMinuteSpinner, 16909040, 17040624);
        trySetContentDescription(this.mHourSpinner, 16909038, 17040625);
        trySetContentDescription(this.mHourSpinner, 16909040, 17040626);
        NumberPicker numberPicker = this.mAmPmSpinner;
        if (numberPicker != null) {
            trySetContentDescription(numberPicker, 16909038, 17040627);
            trySetContentDescription(this.mAmPmSpinner, 16909040, 17040628);
        }
    }

    private void trySetContentDescription(View view, int i, int i2) {
        View viewFindViewById = view.findViewById(i);
        if (viewFindViewById != null) {
            viewFindViewById.setContentDescription(this.mContext.getString(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInputState() {
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null) {
            if (inputMethodManagerPeekInstance.isActive(this.mHourSpinnerInput)) {
                this.mHourSpinnerInput.clearFocus();
                inputMethodManagerPeekInstance.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManagerPeekInstance.isActive(this.mMinuteSpinnerInput)) {
                this.mMinuteSpinnerInput.clearFocus();
                inputMethodManagerPeekInstance.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManagerPeekInstance.isActive(this.mAmPmSpinnerInput)) {
                this.mAmPmSpinnerInput.clearFocus();
                inputMethodManagerPeekInstance.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }
}
