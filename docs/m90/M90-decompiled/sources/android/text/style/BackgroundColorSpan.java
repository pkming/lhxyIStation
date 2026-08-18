package android.text.style;

import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;

/* JADX INFO: loaded from: classes.dex */
public class BackgroundColorSpan extends CharacterStyle implements UpdateAppearance, ParcelableSpan {
    private final int mColor;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 12;
    }

    public BackgroundColorSpan(int i) {
        this.mColor = i;
    }

    public BackgroundColorSpan(Parcel parcel) {
        this.mColor = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mColor);
    }

    public int getBackgroundColor() {
        return this.mColor;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        textPaint.bgColor = this.mColor;
    }
}
