package android.text;

import android.os.Parcel;

/* JADX INFO: loaded from: classes.dex */
public class Annotation implements ParcelableSpan {
    private final String mKey;
    private final String mValue;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 18;
    }

    public Annotation(String str, String str2) {
        this.mKey = str;
        this.mValue = str2;
    }

    public Annotation(Parcel parcel) {
        this.mKey = parcel.readString();
        this.mValue = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mKey);
        parcel.writeString(this.mValue);
    }

    public String getKey() {
        return this.mKey;
    }

    public String getValue() {
        return this.mValue;
    }
}
