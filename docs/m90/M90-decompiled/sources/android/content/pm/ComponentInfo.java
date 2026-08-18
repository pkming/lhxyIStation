package android.content.pm;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.util.Printer;

/* JADX INFO: loaded from: classes.dex */
public class ComponentInfo extends PackageItemInfo {
    public ApplicationInfo applicationInfo;
    public int descriptionRes;
    public boolean enabled;
    public boolean exported;
    public String processName;

    public ComponentInfo() {
        this.enabled = true;
        this.exported = false;
    }

    public ComponentInfo(ComponentInfo componentInfo) {
        super(componentInfo);
        this.enabled = true;
        this.exported = false;
        this.applicationInfo = componentInfo.applicationInfo;
        this.processName = componentInfo.processName;
        this.descriptionRes = componentInfo.descriptionRes;
        this.enabled = componentInfo.enabled;
        this.exported = componentInfo.exported;
    }

    @Override // android.content.pm.PackageItemInfo
    public CharSequence loadLabel(PackageManager packageManager) {
        CharSequence text;
        CharSequence text2;
        if (this.nonLocalizedLabel != null) {
            return this.nonLocalizedLabel;
        }
        ApplicationInfo applicationInfo = this.applicationInfo;
        if (this.labelRes != 0 && (text2 = packageManager.getText(this.packageName, this.labelRes, applicationInfo)) != null) {
            return text2;
        }
        if (applicationInfo.nonLocalizedLabel != null) {
            return applicationInfo.nonLocalizedLabel;
        }
        return (applicationInfo.labelRes == 0 || (text = packageManager.getText(this.packageName, applicationInfo.labelRes, applicationInfo)) == null) ? this.name : text;
    }

    public boolean isEnabled() {
        return this.enabled && this.applicationInfo.enabled;
    }

    public final int getIconResource() {
        return this.icon != 0 ? this.icon : this.applicationInfo.icon;
    }

    public final int getLogoResource() {
        return this.logo != 0 ? this.logo : this.applicationInfo.logo;
    }

    @Override // android.content.pm.PackageItemInfo
    protected void dumpFront(Printer printer, String str) {
        super.dumpFront(printer, str);
        printer.println(str + "enabled=" + this.enabled + " exported=" + this.exported + " processName=" + this.processName);
        if (this.descriptionRes != 0) {
            printer.println(str + "description=" + this.descriptionRes);
        }
    }

    @Override // android.content.pm.PackageItemInfo
    protected void dumpBack(Printer printer, String str) {
        if (this.applicationInfo != null) {
            printer.println(str + "ApplicationInfo:");
            this.applicationInfo.dump(printer, str + "  ");
        } else {
            printer.println(str + "ApplicationInfo: null");
        }
        super.dumpBack(printer, str);
    }

    @Override // android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        this.applicationInfo.writeToParcel(parcel, i);
        parcel.writeString(this.processName);
        parcel.writeInt(this.descriptionRes);
        parcel.writeInt(this.enabled ? 1 : 0);
        parcel.writeInt(this.exported ? 1 : 0);
    }

    protected ComponentInfo(Parcel parcel) {
        super(parcel);
        this.enabled = true;
        this.exported = false;
        this.applicationInfo = ApplicationInfo.CREATOR.createFromParcel(parcel);
        this.processName = parcel.readString();
        this.descriptionRes = parcel.readInt();
        this.enabled = parcel.readInt() != 0;
        this.exported = parcel.readInt() != 0;
    }

    @Override // android.content.pm.PackageItemInfo
    protected Drawable loadDefaultIcon(PackageManager packageManager) {
        return this.applicationInfo.loadIcon(packageManager);
    }

    @Override // android.content.pm.PackageItemInfo
    protected Drawable loadDefaultLogo(PackageManager packageManager) {
        return this.applicationInfo.loadLogo(packageManager);
    }

    @Override // android.content.pm.PackageItemInfo
    protected ApplicationInfo getApplicationInfo() {
        return this.applicationInfo;
    }
}
