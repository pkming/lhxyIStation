package android.hardware.input;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class KeyboardLayout implements Parcelable, Comparable<KeyboardLayout> {
    public static final Parcelable.Creator<KeyboardLayout> CREATOR = new Parcelable.Creator<KeyboardLayout>() { // from class: android.hardware.input.KeyboardLayout.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyboardLayout createFromParcel(Parcel parcel) {
            return new KeyboardLayout(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyboardLayout[] newArray(int i) {
            return new KeyboardLayout[i];
        }
    };
    private final String mCollection;
    private final String mDescriptor;
    private final String mLabel;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public KeyboardLayout(String str, String str2, String str3) {
        this.mDescriptor = str;
        this.mLabel = str2;
        this.mCollection = str3;
    }

    private KeyboardLayout(Parcel parcel) {
        this.mDescriptor = parcel.readString();
        this.mLabel = parcel.readString();
        this.mCollection = parcel.readString();
    }

    public String getDescriptor() {
        return this.mDescriptor;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public String getCollection() {
        return this.mCollection;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mDescriptor);
        parcel.writeString(this.mLabel);
        parcel.writeString(this.mCollection);
    }

    @Override // java.lang.Comparable
    public int compareTo(KeyboardLayout keyboardLayout) {
        int iCompareToIgnoreCase = this.mLabel.compareToIgnoreCase(keyboardLayout.mLabel);
        return iCompareToIgnoreCase == 0 ? this.mCollection.compareToIgnoreCase(keyboardLayout.mCollection) : iCompareToIgnoreCase;
    }

    public String toString() {
        if (this.mCollection.isEmpty()) {
            return this.mLabel;
        }
        return this.mLabel + " - " + this.mCollection;
    }
}
