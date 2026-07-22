package android.text.style;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class URLSpan extends ClickableSpan implements ParcelableSpan {
    private final String mURL;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 11;
    }

    public URLSpan(String str) {
        this.mURL = str;
    }

    public URLSpan(Parcel parcel) {
        this.mURL = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mURL);
    }

    public String getURL() {
        return this.mURL;
    }

    @Override // android.text.style.ClickableSpan
    public void onClick(View view) {
        Uri uri = Uri.parse(getURL());
        Context context = view.getContext();
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        context.startActivity(intent);
    }
}
