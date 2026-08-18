package android.text.style;

import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;

/* JADX INFO: loaded from: classes.dex */
public class AbsoluteSizeSpan extends MetricAffectingSpan implements ParcelableSpan {
    private boolean mDip;
    private final int mSize;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 16;
    }

    public AbsoluteSizeSpan(int i) {
        this.mSize = i;
    }

    public AbsoluteSizeSpan(int i, boolean z) {
        this.mSize = i;
        this.mDip = z;
    }

    public AbsoluteSizeSpan(Parcel parcel) {
        this.mSize = parcel.readInt();
        this.mDip = parcel.readInt() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSize);
        parcel.writeInt(this.mDip ? 1 : 0);
    }

    public int getSize() {
        return this.mSize;
    }

    public boolean getDip() {
        return this.mDip;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        if (this.mDip) {
            textPaint.setTextSize(this.mSize * textPaint.density);
        } else {
            textPaint.setTextSize(this.mSize);
        }
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint textPaint) {
        if (this.mDip) {
            textPaint.setTextSize(this.mSize * textPaint.density);
        } else {
            textPaint.setTextSize(this.mSize);
        }
    }
}
