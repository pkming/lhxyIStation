package android.text.style;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R;
import java.util.Arrays;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class SuggestionSpan extends CharacterStyle implements ParcelableSpan {
    public static final String ACTION_SUGGESTION_PICKED = "android.text.style.SUGGESTION_PICKED";
    public static final Parcelable.Creator<SuggestionSpan> CREATOR = new Parcelable.Creator<SuggestionSpan>() { // from class: android.text.style.SuggestionSpan.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SuggestionSpan createFromParcel(Parcel parcel) {
            return new SuggestionSpan(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SuggestionSpan[] newArray(int i) {
            return new SuggestionSpan[i];
        }
    };
    public static final int FLAG_AUTO_CORRECTION = 4;
    public static final int FLAG_EASY_CORRECT = 1;
    public static final int FLAG_MISSPELLED = 2;
    public static final int SUGGESTIONS_MAX_SIZE = 5;
    public static final String SUGGESTION_SPAN_PICKED_AFTER = "after";
    public static final String SUGGESTION_SPAN_PICKED_BEFORE = "before";
    public static final String SUGGESTION_SPAN_PICKED_HASHCODE = "hashcode";
    private static final String TAG = "SuggestionSpan";
    private int mAutoCorrectionUnderlineColor;
    private float mAutoCorrectionUnderlineThickness;
    private int mEasyCorrectUnderlineColor;
    private float mEasyCorrectUnderlineThickness;
    private int mFlags;
    private final int mHashCode;
    private final String mLocaleString;
    private int mMisspelledUnderlineColor;
    private float mMisspelledUnderlineThickness;
    private final String mNotificationTargetClassName;
    private final String mNotificationTargetPackageName;
    private final String[] mSuggestions;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 19;
    }

    public SuggestionSpan(Context context, String[] strArr, int i) {
        this(context, null, strArr, i, null);
    }

    public SuggestionSpan(Locale locale, String[] strArr, int i) {
        this(null, locale, strArr, i, null);
    }

    public SuggestionSpan(Context context, Locale locale, String[] strArr, int i, Class<?> cls) {
        String[] strArr2 = (String[]) Arrays.copyOf(strArr, Math.min(5, strArr.length));
        this.mSuggestions = strArr2;
        this.mFlags = i;
        if (locale != null) {
            this.mLocaleString = locale.toString();
        } else if (context != null) {
            this.mLocaleString = context.getResources().getConfiguration().locale.toString();
        } else {
            Log.e(TAG, "No locale or context specified in SuggestionSpan constructor");
            this.mLocaleString = "";
        }
        if (context != null) {
            this.mNotificationTargetPackageName = context.getPackageName();
        } else {
            this.mNotificationTargetPackageName = null;
        }
        if (cls != null) {
            this.mNotificationTargetClassName = cls.getCanonicalName();
        } else {
            this.mNotificationTargetClassName = "";
        }
        this.mHashCode = hashCodeInternal(strArr2, this.mLocaleString, this.mNotificationTargetClassName);
        initStyle(context);
    }

    private void initStyle(Context context) {
        if (context == null) {
            this.mMisspelledUnderlineThickness = 0.0f;
            this.mEasyCorrectUnderlineThickness = 0.0f;
            this.mAutoCorrectionUnderlineThickness = 0.0f;
            this.mMisspelledUnderlineColor = -16777216;
            this.mEasyCorrectUnderlineColor = -16777216;
            this.mAutoCorrectionUnderlineColor = -16777216;
            return;
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(null, R.styleable.SuggestionSpan, 16843762, 0);
        this.mMisspelledUnderlineThickness = typedArrayObtainStyledAttributes.getDimension(1, 0.0f);
        this.mMisspelledUnderlineColor = typedArrayObtainStyledAttributes.getColor(0, -16777216);
        TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(null, R.styleable.SuggestionSpan, 16843715, 0);
        this.mEasyCorrectUnderlineThickness = typedArrayObtainStyledAttributes2.getDimension(1, 0.0f);
        this.mEasyCorrectUnderlineColor = typedArrayObtainStyledAttributes2.getColor(0, -16777216);
        TypedArray typedArrayObtainStyledAttributes3 = context.obtainStyledAttributes(null, R.styleable.SuggestionSpan, 16843763, 0);
        this.mAutoCorrectionUnderlineThickness = typedArrayObtainStyledAttributes3.getDimension(1, 0.0f);
        this.mAutoCorrectionUnderlineColor = typedArrayObtainStyledAttributes3.getColor(0, -16777216);
    }

    public SuggestionSpan(Parcel parcel) {
        this.mSuggestions = parcel.readStringArray();
        this.mFlags = parcel.readInt();
        this.mLocaleString = parcel.readString();
        this.mNotificationTargetClassName = parcel.readString();
        this.mNotificationTargetPackageName = parcel.readString();
        this.mHashCode = parcel.readInt();
        this.mEasyCorrectUnderlineColor = parcel.readInt();
        this.mEasyCorrectUnderlineThickness = parcel.readFloat();
        this.mMisspelledUnderlineColor = parcel.readInt();
        this.mMisspelledUnderlineThickness = parcel.readFloat();
        this.mAutoCorrectionUnderlineColor = parcel.readInt();
        this.mAutoCorrectionUnderlineThickness = parcel.readFloat();
    }

    public String[] getSuggestions() {
        return this.mSuggestions;
    }

    public String getLocale() {
        return this.mLocaleString;
    }

    public String getNotificationTargetClassName() {
        return this.mNotificationTargetClassName;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public void setFlags(int i) {
        this.mFlags = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(this.mSuggestions);
        parcel.writeInt(this.mFlags);
        parcel.writeString(this.mLocaleString);
        parcel.writeString(this.mNotificationTargetClassName);
        parcel.writeString(this.mNotificationTargetPackageName);
        parcel.writeInt(this.mHashCode);
        parcel.writeInt(this.mEasyCorrectUnderlineColor);
        parcel.writeFloat(this.mEasyCorrectUnderlineThickness);
        parcel.writeInt(this.mMisspelledUnderlineColor);
        parcel.writeFloat(this.mMisspelledUnderlineThickness);
        parcel.writeInt(this.mAutoCorrectionUnderlineColor);
        parcel.writeFloat(this.mAutoCorrectionUnderlineThickness);
    }

    public boolean equals(Object obj) {
        return (obj instanceof SuggestionSpan) && ((SuggestionSpan) obj).hashCode() == this.mHashCode;
    }

    public int hashCode() {
        return this.mHashCode;
    }

    private static int hashCodeInternal(String[] strArr, String str, String str2) {
        return Arrays.hashCode(new Object[]{Long.valueOf(SystemClock.uptimeMillis()), strArr, str, str2});
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        int i = this.mFlags;
        boolean z = (i & 2) != 0;
        boolean z2 = (i & 1) != 0;
        boolean z3 = (i & 4) != 0;
        if (!z2) {
            if (z3) {
                textPaint.setUnderlineText(this.mAutoCorrectionUnderlineColor, this.mAutoCorrectionUnderlineThickness);
            }
        } else if (!z) {
            textPaint.setUnderlineText(this.mEasyCorrectUnderlineColor, this.mEasyCorrectUnderlineThickness);
        } else if (textPaint.underlineColor == 0) {
            textPaint.setUnderlineText(this.mMisspelledUnderlineColor, this.mMisspelledUnderlineThickness);
        }
    }

    public int getUnderlineColor() {
        int i = this.mFlags;
        boolean z = (i & 2) != 0;
        boolean z2 = (i & 1) != 0;
        boolean z3 = (i & 4) != 0;
        if (z2) {
            if (!z) {
                return this.mEasyCorrectUnderlineColor;
            }
            return this.mMisspelledUnderlineColor;
        }
        if (z3) {
            return this.mAutoCorrectionUnderlineColor;
        }
        return 0;
    }

    public void notifySelection(Context context, String str, int i) {
        String str2;
        Intent intent = new Intent();
        if (context == null || (str2 = this.mNotificationTargetClassName) == null) {
            return;
        }
        String[] strArr = this.mSuggestions;
        if (strArr == null || i < 0 || i >= strArr.length) {
            Log.w(TAG, "Unable to notify the suggestion as the index is out of range index=" + i + " length=" + this.mSuggestions.length);
            return;
        }
        String str3 = this.mNotificationTargetPackageName;
        if (str3 != null) {
            intent.setClassName(str3, str2);
            intent.setAction(ACTION_SUGGESTION_PICKED);
            intent.putExtra(SUGGESTION_SPAN_PICKED_BEFORE, str);
            intent.putExtra(SUGGESTION_SPAN_PICKED_AFTER, this.mSuggestions[i]);
            intent.putExtra(SUGGESTION_SPAN_PICKED_HASHCODE, hashCode());
            context.sendBroadcast(intent);
            return;
        }
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null) {
            inputMethodManagerPeekInstance.notifySuggestionPicked(this, str, i);
        }
    }
}
