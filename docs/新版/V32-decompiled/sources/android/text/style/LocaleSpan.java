package android.text.style;

import android.graphics.Paint;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class LocaleSpan extends MetricAffectingSpan implements ParcelableSpan {
    private final Locale mLocale;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 23;
    }

    public LocaleSpan(Locale locale) {
        this.mLocale = locale;
    }

    public LocaleSpan(Parcel parcel) {
        this.mLocale = new Locale(parcel.readString(), parcel.readString(), parcel.readString());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mLocale.getLanguage());
        parcel.writeString(this.mLocale.getCountry());
        parcel.writeString(this.mLocale.getVariant());
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        apply(textPaint, this.mLocale);
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint textPaint) {
        apply(textPaint, this.mLocale);
    }

    private static void apply(Paint paint, Locale locale) {
        paint.setTextLocale(locale);
    }
}
