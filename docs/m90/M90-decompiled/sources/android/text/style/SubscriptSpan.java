package android.text.style;

import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;

/* JADX INFO: loaded from: classes.dex */
public class SubscriptSpan extends MetricAffectingSpan implements ParcelableSpan {
    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 15;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
    }

    public SubscriptSpan() {
    }

    public SubscriptSpan(Parcel parcel) {
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        textPaint.baselineShift -= (int) (textPaint.ascent() / 2.0f);
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint textPaint) {
        textPaint.baselineShift -= (int) (textPaint.ascent() / 2.0f);
    }
}
