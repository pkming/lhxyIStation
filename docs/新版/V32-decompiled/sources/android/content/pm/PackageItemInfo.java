package android.content.pm;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Printer;
import java.text.Collator;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class PackageItemInfo {
    public int icon;
    public int labelRes;
    public int logo;
    public Bundle metaData;
    public String name;
    public CharSequence nonLocalizedLabel;
    public String packageName;

    protected void dumpBack(Printer printer, String str) {
    }

    protected ApplicationInfo getApplicationInfo() {
        return null;
    }

    protected Drawable loadDefaultLogo(PackageManager packageManager) {
        return null;
    }

    public PackageItemInfo() {
    }

    public PackageItemInfo(PackageItemInfo packageItemInfo) {
        String str = packageItemInfo.name;
        this.name = str;
        if (str != null) {
            this.name = str.trim();
        }
        this.packageName = packageItemInfo.packageName;
        this.labelRes = packageItemInfo.labelRes;
        CharSequence charSequence = packageItemInfo.nonLocalizedLabel;
        this.nonLocalizedLabel = charSequence;
        if (charSequence != null) {
            this.nonLocalizedLabel = charSequence.toString().trim();
        }
        this.icon = packageItemInfo.icon;
        this.logo = packageItemInfo.logo;
        this.metaData = packageItemInfo.metaData;
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        CharSequence text;
        CharSequence charSequence = this.nonLocalizedLabel;
        if (charSequence != null) {
            return charSequence;
        }
        int i = this.labelRes;
        if (i != 0 && (text = packageManager.getText(this.packageName, i, getApplicationInfo())) != null) {
            return text.toString().trim();
        }
        String str = this.name;
        return str != null ? str : this.packageName;
    }

    public Drawable loadIcon(PackageManager packageManager) {
        Drawable drawable;
        int i = this.icon;
        return (i == 0 || (drawable = packageManager.getDrawable(this.packageName, i, getApplicationInfo())) == null) ? loadDefaultIcon(packageManager) : drawable;
    }

    protected Drawable loadDefaultIcon(PackageManager packageManager) {
        return packageManager.getDefaultActivityIcon();
    }

    public Drawable loadLogo(PackageManager packageManager) {
        Drawable drawable;
        int i = this.logo;
        return (i == 0 || (drawable = packageManager.getDrawable(this.packageName, i, getApplicationInfo())) == null) ? loadDefaultLogo(packageManager) : drawable;
    }

    public XmlResourceParser loadXmlMetaData(PackageManager packageManager, String str) {
        int i;
        Bundle bundle = this.metaData;
        if (bundle == null || (i = bundle.getInt(str)) == 0) {
            return null;
        }
        return packageManager.getXml(this.packageName, i, getApplicationInfo());
    }

    protected void dumpFront(Printer printer, String str) {
        if (this.name != null) {
            printer.println(str + "name=" + this.name);
        }
        printer.println(str + "packageName=" + this.packageName);
        if (this.labelRes == 0 && this.nonLocalizedLabel == null && this.icon == 0) {
            return;
        }
        printer.println(str + "labelRes=0x" + Integer.toHexString(this.labelRes) + " nonLocalizedLabel=" + ((Object) this.nonLocalizedLabel) + " icon=0x" + Integer.toHexString(this.icon));
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.packageName);
        parcel.writeInt(this.labelRes);
        TextUtils.writeToParcel(this.nonLocalizedLabel, parcel, i);
        parcel.writeInt(this.icon);
        parcel.writeInt(this.logo);
        parcel.writeBundle(this.metaData);
    }

    protected PackageItemInfo(Parcel parcel) {
        this.name = parcel.readString();
        this.packageName = parcel.readString();
        this.labelRes = parcel.readInt();
        this.nonLocalizedLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.icon = parcel.readInt();
        this.logo = parcel.readInt();
        this.metaData = parcel.readBundle();
    }

    public static class DisplayNameComparator implements Comparator<PackageItemInfo> {
        private PackageManager mPM;
        private final Collator sCollator = Collator.getInstance();

        public DisplayNameComparator(PackageManager packageManager) {
            this.mPM = packageManager;
        }

        @Override // java.util.Comparator
        public final int compare(PackageItemInfo packageItemInfo, PackageItemInfo packageItemInfo2) {
            CharSequence charSequenceLoadLabel = packageItemInfo.loadLabel(this.mPM);
            if (charSequenceLoadLabel == null) {
                charSequenceLoadLabel = packageItemInfo.name;
            }
            CharSequence charSequenceLoadLabel2 = packageItemInfo2.loadLabel(this.mPM);
            if (charSequenceLoadLabel2 == null) {
                charSequenceLoadLabel2 = packageItemInfo2.name;
            }
            return this.sCollator.compare(charSequenceLoadLabel.toString(), charSequenceLoadLabel2.toString());
        }
    }
}
