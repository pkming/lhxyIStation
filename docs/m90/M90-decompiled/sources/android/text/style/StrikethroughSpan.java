package android.text.style;

import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;

/* JADX INFO: loaded from: classes.dex */
public class StrikethroughSpan extends CharacterStyle implements UpdateAppearance, ParcelableSpan {
    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 5;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
    }

    public StrikethroughSpan() {
    }

    public StrikethroughSpan(Parcel parcel) {
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setStrikeThruText(true);
    }
}
