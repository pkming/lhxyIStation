package android.text.style;

import android.os.Parcel;
import android.text.Layout;
import android.text.ParcelableSpan;

/* JADX INFO: loaded from: classes.dex */
public interface AlignmentSpan extends ParagraphStyle {
    Layout.Alignment getAlignment();

    public static class Standard implements AlignmentSpan, ParcelableSpan {
        private final Layout.Alignment mAlignment;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.text.ParcelableSpan
        public int getSpanTypeId() {
            return 1;
        }

        public Standard(Layout.Alignment alignment) {
            this.mAlignment = alignment;
        }

        public Standard(Parcel parcel) {
            this.mAlignment = Layout.Alignment.valueOf(parcel.readString());
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.mAlignment.name());
        }

        @Override // android.text.style.AlignmentSpan
        public Layout.Alignment getAlignment() {
            return this.mAlignment;
        }
    }
}
