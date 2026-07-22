package android.text.style;

import android.os.Parcel;
import android.text.ParcelableSpan;

/* JADX INFO: loaded from: classes.dex */
public class SpellCheckSpan implements ParcelableSpan {
    private boolean mSpellCheckInProgress;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 20;
    }

    public SpellCheckSpan() {
        this.mSpellCheckInProgress = false;
    }

    public SpellCheckSpan(Parcel parcel) {
        this.mSpellCheckInProgress = parcel.readInt() != 0;
    }

    public void setSpellCheckInProgress(boolean z) {
        this.mSpellCheckInProgress = z;
    }

    public boolean isSpellCheckInProgress() {
        return this.mSpellCheckInProgress;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSpellCheckInProgress ? 1 : 0);
    }
}
