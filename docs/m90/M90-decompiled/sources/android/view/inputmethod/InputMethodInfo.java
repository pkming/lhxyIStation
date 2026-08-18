package android.view.inputmethod;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Printer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public final class InputMethodInfo implements Parcelable {
    public static final Parcelable.Creator<InputMethodInfo> CREATOR = new Parcelable.Creator<InputMethodInfo>() { // from class: android.view.inputmethod.InputMethodInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputMethodInfo createFromParcel(Parcel parcel) {
            return new InputMethodInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputMethodInfo[] newArray(int i) {
            return new InputMethodInfo[i];
        }
    };
    static final String TAG = "InputMethodInfo";
    private final boolean mForceDefault;
    final String mId;
    private final boolean mIsAuxIme;
    final int mIsDefaultResId;
    final ResolveInfo mService;
    final String mSettingsActivityName;
    private final ArrayList<InputMethodSubtype> mSubtypes;
    private final boolean mSupportsSwitchingToNextInputMethod;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public InputMethodInfo(Context context, ResolveInfo resolveInfo) throws XmlPullParserException, IOException {
        this(context, resolveInfo, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0103, code lost:
    
        if (r5 == null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0105, code lost:
    
        r5.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x010e, code lost:
    
        if (r16.mSubtypes.size() != 0) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0110, code lost:
    
        r13 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0111, code lost:
    
        if (r19 == null) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0119, code lost:
    
        if (r19.containsKey(r16.mId) == false) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x011b, code lost:
    
        r0 = r19.get(r16.mId);
        r2 = r0.size();
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0127, code lost:
    
        if (r3 >= r2) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0129, code lost:
    
        r4 = r0.get(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0135, code lost:
    
        if (r16.mSubtypes.contains(r4) != false) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0137, code lost:
    
        r16.mSubtypes.add(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x013d, code lost:
    
        android.util.Slog.w(android.view.inputmethod.InputMethodInfo.TAG, "Duplicated subtype definition found: " + r4.getLocale() + ", " + r4.getMode());
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0167, code lost:
    
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x016a, code lost:
    
        r16.mSettingsActivityName = r10;
        r16.mIsDefaultResId = r11;
        r16.mIsAuxIme = r13;
        r16.mSupportsSwitchingToNextInputMethod = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0172, code lost:
    
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public InputMethodInfo(android.content.Context r17, android.content.pm.ResolveInfo r18, java.util.Map<java.lang.String, java.util.List<android.view.inputmethod.InputMethodSubtype>> r19) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 422
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.inputmethod.InputMethodInfo.<init>(android.content.Context, android.content.pm.ResolveInfo, java.util.Map):void");
    }

    InputMethodInfo(Parcel parcel) {
        ArrayList<InputMethodSubtype> arrayList = new ArrayList<>();
        this.mSubtypes = arrayList;
        this.mId = parcel.readString();
        this.mSettingsActivityName = parcel.readString();
        this.mIsDefaultResId = parcel.readInt();
        this.mIsAuxIme = parcel.readInt() == 1;
        this.mSupportsSwitchingToNextInputMethod = parcel.readInt() == 1;
        this.mService = ResolveInfo.CREATOR.createFromParcel(parcel);
        parcel.readTypedList(arrayList, InputMethodSubtype.CREATOR);
        this.mForceDefault = false;
    }

    public InputMethodInfo(String str, String str2, CharSequence charSequence, String str3) {
        this(buildDummyResolveInfo(str, str2, charSequence), false, str3, null, 0, false);
    }

    public InputMethodInfo(ResolveInfo resolveInfo, boolean z, String str, List<InputMethodSubtype> list, int i, boolean z2) {
        ArrayList<InputMethodSubtype> arrayList = new ArrayList<>();
        this.mSubtypes = arrayList;
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        this.mService = resolveInfo;
        this.mId = new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToShortString();
        this.mSettingsActivityName = str;
        this.mIsDefaultResId = i;
        this.mIsAuxIme = z;
        if (list != null) {
            arrayList.addAll(list);
        }
        this.mForceDefault = z2;
        this.mSupportsSwitchingToNextInputMethod = true;
    }

    private static ResolveInfo buildDummyResolveInfo(String str, String str2, CharSequence charSequence) {
        ResolveInfo resolveInfo = new ResolveInfo();
        ServiceInfo serviceInfo = new ServiceInfo();
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.packageName = str;
        applicationInfo.enabled = true;
        serviceInfo.applicationInfo = applicationInfo;
        serviceInfo.enabled = true;
        serviceInfo.packageName = str;
        serviceInfo.name = str2;
        serviceInfo.exported = true;
        serviceInfo.nonLocalizedLabel = charSequence;
        resolveInfo.serviceInfo = serviceInfo;
        return resolveInfo;
    }

    public String getId() {
        return this.mId;
    }

    public String getPackageName() {
        return this.mService.serviceInfo.packageName;
    }

    public String getServiceName() {
        return this.mService.serviceInfo.name;
    }

    public ServiceInfo getServiceInfo() {
        return this.mService.serviceInfo;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        return this.mService.loadLabel(packageManager);
    }

    public Drawable loadIcon(PackageManager packageManager) {
        return this.mService.loadIcon(packageManager);
    }

    public String getSettingsActivity() {
        return this.mSettingsActivityName;
    }

    public int getSubtypeCount() {
        return this.mSubtypes.size();
    }

    public InputMethodSubtype getSubtypeAt(int i) {
        return this.mSubtypes.get(i);
    }

    public int getIsDefaultResourceId() {
        return this.mIsDefaultResId;
    }

    public boolean isDefault(Context context) {
        if (this.mForceDefault) {
            return true;
        }
        try {
            return context.createPackageContext(getPackageName(), 0).getResources().getBoolean(getIsDefaultResourceId());
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public void dump(Printer printer, String str) {
        printer.println(str + "mId=" + this.mId + " mSettingsActivityName=" + this.mSettingsActivityName);
        printer.println(str + "mIsDefaultResId=0x" + Integer.toHexString(this.mIsDefaultResId));
        printer.println(str + "Service:");
        this.mService.dump(printer, str + "  ");
    }

    public String toString() {
        return "InputMethodInfo{" + this.mId + ", settings: " + this.mSettingsActivityName + "}";
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && (obj instanceof InputMethodInfo)) {
            return this.mId.equals(((InputMethodInfo) obj).mId);
        }
        return false;
    }

    public int hashCode() {
        return this.mId.hashCode();
    }

    public boolean isAuxiliaryIme() {
        return this.mIsAuxIme;
    }

    public boolean supportsSwitchingToNextInputMethod() {
        return this.mSupportsSwitchingToNextInputMethod;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mId);
        parcel.writeString(this.mSettingsActivityName);
        parcel.writeInt(this.mIsDefaultResId);
        parcel.writeInt(this.mIsAuxIme ? 1 : 0);
        parcel.writeInt(this.mSupportsSwitchingToNextInputMethod ? 1 : 0);
        this.mService.writeToParcel(parcel, i);
        parcel.writeTypedList(this.mSubtypes);
    }
}
