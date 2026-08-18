package android.view.textservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public final class TextInfo implements Parcelable {
    public static final Parcelable.Creator<TextInfo> CREATOR = new Parcelable.Creator<TextInfo>() { // from class: android.view.textservice.TextInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextInfo createFromParcel(Parcel parcel) {
            return new TextInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextInfo[] newArray(int i) {
            return new TextInfo[i];
        }
    };
    private final int mCookie;
    private final int mSequence;
    private final String mText;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public TextInfo(String str) {
        this(str, 0, 0);
    }

    public TextInfo(String str, int i, int i2) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException(str);
        }
        this.mText = str;
        this.mCookie = i;
        this.mSequence = i2;
    }

    public TextInfo(Parcel parcel) {
        this.mText = parcel.readString();
        this.mCookie = parcel.readInt();
        this.mSequence = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mText);
        parcel.writeInt(this.mCookie);
        parcel.writeInt(this.mSequence);
    }

    public String getText() {
        return this.mText;
    }

    public int getCookie() {
        return this.mCookie;
    }

    public int getSequence() {
        return this.mSequence;
    }
}
