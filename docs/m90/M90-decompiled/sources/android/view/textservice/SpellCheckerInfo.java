package android.view.textservice;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class SpellCheckerInfo implements Parcelable {
    public static final Parcelable.Creator<SpellCheckerInfo> CREATOR = new Parcelable.Creator<SpellCheckerInfo>() { // from class: android.view.textservice.SpellCheckerInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SpellCheckerInfo createFromParcel(Parcel parcel) {
            return new SpellCheckerInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SpellCheckerInfo[] newArray(int i) {
            return new SpellCheckerInfo[i];
        }
    };
    private static final String TAG = "SpellCheckerInfo";
    private final String mId;
    private final int mLabel;
    private final ResolveInfo mService;
    private final String mSettingsActivityName;
    private final ArrayList<SpellCheckerSubtype> mSubtypes;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x00a5, code lost:
    
        if (r0 == null) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00a7, code lost:
    
        r0.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x00aa, code lost:
    
        r12.mLabel = r6;
        r12.mSettingsActivityName = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00ae, code lost:
    
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public SpellCheckerInfo(android.content.Context r13, android.content.pm.ResolveInfo r14) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 251
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.textservice.SpellCheckerInfo.<init>(android.content.Context, android.content.pm.ResolveInfo):void");
    }

    public SpellCheckerInfo(Parcel parcel) {
        ArrayList<SpellCheckerSubtype> arrayList = new ArrayList<>();
        this.mSubtypes = arrayList;
        this.mLabel = parcel.readInt();
        this.mId = parcel.readString();
        this.mSettingsActivityName = parcel.readString();
        this.mService = ResolveInfo.CREATOR.createFromParcel(parcel);
        parcel.readTypedList(arrayList, SpellCheckerSubtype.CREATOR);
    }

    public String getId() {
        return this.mId;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public String getPackageName() {
        return this.mService.serviceInfo.packageName;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mLabel);
        parcel.writeString(this.mId);
        parcel.writeString(this.mSettingsActivityName);
        this.mService.writeToParcel(parcel, i);
        parcel.writeTypedList(this.mSubtypes);
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        return (this.mLabel == 0 || packageManager == null) ? "" : packageManager.getText(getPackageName(), this.mLabel, this.mService.serviceInfo.applicationInfo);
    }

    public Drawable loadIcon(PackageManager packageManager) {
        return this.mService.loadIcon(packageManager);
    }

    public ServiceInfo getServiceInfo() {
        return this.mService.serviceInfo;
    }

    public String getSettingsActivity() {
        return this.mSettingsActivityName;
    }

    public int getSubtypeCount() {
        return this.mSubtypes.size();
    }

    public SpellCheckerSubtype getSubtypeAt(int i) {
        return this.mSubtypes.get(i);
    }
}
