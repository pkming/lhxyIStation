package android.content.pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Printer;
import android.util.Slog;
import java.text.Collator;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class ResolveInfo implements Parcelable {
    public static final Parcelable.Creator<ResolveInfo> CREATOR = new Parcelable.Creator<ResolveInfo>() { // from class: android.content.pm.ResolveInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResolveInfo createFromParcel(Parcel parcel) {
            return new ResolveInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResolveInfo[] newArray(int i) {
            return new ResolveInfo[i];
        }
    };
    private static final String TAG = "ResolveInfo";
    public ActivityInfo activityInfo;
    public IntentFilter filter;
    public int icon;
    public boolean isDefault;
    public int labelRes;
    public int match;
    public CharSequence nonLocalizedLabel;
    public int preferredOrder;
    public int priority;
    public ProviderInfo providerInfo;
    public String resolvePackageName;
    public ServiceInfo serviceInfo;
    public int specificIndex;
    public boolean system;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private ComponentInfo getComponentInfo() {
        ActivityInfo activityInfo = this.activityInfo;
        if (activityInfo != null) {
            return activityInfo;
        }
        ServiceInfo serviceInfo = this.serviceInfo;
        if (serviceInfo != null) {
            return serviceInfo;
        }
        ProviderInfo providerInfo = this.providerInfo;
        if (providerInfo != null) {
            return providerInfo;
        }
        throw new IllegalStateException("Missing ComponentInfo!");
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        CharSequence text;
        int i;
        CharSequence text2;
        CharSequence charSequence = this.nonLocalizedLabel;
        if (charSequence != null) {
            return charSequence;
        }
        String str = this.resolvePackageName;
        if (str != null && (i = this.labelRes) != 0 && (text2 = packageManager.getText(str, i, null)) != null) {
            return text2.toString().trim();
        }
        ComponentInfo componentInfo = getComponentInfo();
        ApplicationInfo applicationInfo = componentInfo.applicationInfo;
        if (this.labelRes != 0 && (text = packageManager.getText(componentInfo.packageName, this.labelRes, applicationInfo)) != null) {
            return text.toString().trim();
        }
        CharSequence charSequenceLoadLabel = componentInfo.loadLabel(packageManager);
        return charSequenceLoadLabel != null ? charSequenceLoadLabel.toString().trim() : charSequenceLoadLabel;
    }

    public Drawable loadIcon(PackageManager packageManager) {
        Drawable drawable;
        int i;
        Drawable drawable2;
        String str = this.resolvePackageName;
        if (str != null && (i = this.icon) != 0 && (drawable2 = packageManager.getDrawable(str, i, null)) != null) {
            return drawable2;
        }
        ComponentInfo componentInfo = getComponentInfo();
        return (this.icon == 0 || (drawable = packageManager.getDrawable(componentInfo.packageName, this.icon, componentInfo.applicationInfo)) == null) ? componentInfo.loadIcon(packageManager) : drawable;
    }

    public final int getIconResource() {
        int i = this.icon;
        if (i != 0) {
            return i;
        }
        ComponentInfo componentInfo = getComponentInfo();
        if (componentInfo != null) {
            return componentInfo.getIconResource();
        }
        return 0;
    }

    public void dump(Printer printer, String str) {
        if (this.filter != null) {
            printer.println(str + "Filter:");
            this.filter.dump(printer, str + "  ");
        }
        printer.println(str + "priority=" + this.priority + " preferredOrder=" + this.preferredOrder + " match=0x" + Integer.toHexString(this.match) + " specificIndex=" + this.specificIndex + " isDefault=" + this.isDefault);
        if (this.resolvePackageName != null) {
            printer.println(str + "resolvePackageName=" + this.resolvePackageName);
        }
        if (this.labelRes != 0 || this.nonLocalizedLabel != null || this.icon != 0) {
            printer.println(str + "labelRes=0x" + Integer.toHexString(this.labelRes) + " nonLocalizedLabel=" + ((Object) this.nonLocalizedLabel) + " icon=0x" + Integer.toHexString(this.icon));
        }
        if (this.activityInfo != null) {
            printer.println(str + "ActivityInfo:");
            this.activityInfo.dump(printer, str + "  ");
        } else if (this.serviceInfo != null) {
            printer.println(str + "ServiceInfo:");
            this.serviceInfo.dump(printer, str + "  ");
        } else if (this.providerInfo != null) {
            printer.println(str + "ProviderInfo:");
            this.providerInfo.dump(printer, str + "  ");
        }
    }

    public ResolveInfo() {
        this.specificIndex = -1;
    }

    public ResolveInfo(ResolveInfo resolveInfo) {
        this.specificIndex = -1;
        this.activityInfo = resolveInfo.activityInfo;
        this.serviceInfo = resolveInfo.serviceInfo;
        this.providerInfo = resolveInfo.providerInfo;
        this.filter = resolveInfo.filter;
        this.priority = resolveInfo.priority;
        this.preferredOrder = resolveInfo.preferredOrder;
        this.match = resolveInfo.match;
        this.specificIndex = resolveInfo.specificIndex;
        this.labelRes = resolveInfo.labelRes;
        this.nonLocalizedLabel = resolveInfo.nonLocalizedLabel;
        this.icon = resolveInfo.icon;
        this.resolvePackageName = resolveInfo.resolvePackageName;
        this.system = resolveInfo.system;
    }

    public String toString() {
        ComponentInfo componentInfo = getComponentInfo();
        StringBuilder sb = new StringBuilder(128);
        sb.append("ResolveInfo{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        ComponentName.appendShortString(sb, componentInfo.packageName, componentInfo.name);
        if (this.priority != 0) {
            sb.append(" p=");
            sb.append(this.priority);
        }
        if (this.preferredOrder != 0) {
            sb.append(" o=");
            sb.append(this.preferredOrder);
        }
        sb.append(" m=0x");
        sb.append(Integer.toHexString(this.match));
        sb.append('}');
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.activityInfo != null) {
            parcel.writeInt(1);
            this.activityInfo.writeToParcel(parcel, i);
        } else if (this.serviceInfo != null) {
            parcel.writeInt(2);
            this.serviceInfo.writeToParcel(parcel, i);
        } else if (this.providerInfo != null) {
            parcel.writeInt(3);
            this.providerInfo.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        if (this.filter != null) {
            parcel.writeInt(1);
            this.filter.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.priority);
        parcel.writeInt(this.preferredOrder);
        parcel.writeInt(this.match);
        parcel.writeInt(this.specificIndex);
        parcel.writeInt(this.labelRes);
        TextUtils.writeToParcel(this.nonLocalizedLabel, parcel, i);
        parcel.writeInt(this.icon);
        parcel.writeString(this.resolvePackageName);
        parcel.writeInt(this.system ? 1 : 0);
    }

    private ResolveInfo(Parcel parcel) {
        this.specificIndex = -1;
        this.activityInfo = null;
        this.serviceInfo = null;
        this.providerInfo = null;
        int i = parcel.readInt();
        if (i == 1) {
            this.activityInfo = ActivityInfo.CREATOR.createFromParcel(parcel);
        } else if (i == 2) {
            this.serviceInfo = ServiceInfo.CREATOR.createFromParcel(parcel);
        } else if (i == 3) {
            this.providerInfo = ProviderInfo.CREATOR.createFromParcel(parcel);
        } else {
            Slog.w(TAG, "Missing ComponentInfo!");
        }
        if (parcel.readInt() != 0) {
            this.filter = IntentFilter.CREATOR.createFromParcel(parcel);
        }
        this.priority = parcel.readInt();
        this.preferredOrder = parcel.readInt();
        this.match = parcel.readInt();
        this.specificIndex = parcel.readInt();
        this.labelRes = parcel.readInt();
        this.nonLocalizedLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.icon = parcel.readInt();
        this.resolvePackageName = parcel.readString();
        this.system = parcel.readInt() != 0;
    }

    public static class DisplayNameComparator implements Comparator<ResolveInfo> {
        private final Collator mCollator;
        private PackageManager mPM;

        public DisplayNameComparator(PackageManager packageManager) {
            Collator collator = Collator.getInstance();
            this.mCollator = collator;
            this.mPM = packageManager;
            collator.setStrength(0);
        }

        @Override // java.util.Comparator
        public final int compare(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
            CharSequence charSequenceLoadLabel = resolveInfo.loadLabel(this.mPM);
            if (charSequenceLoadLabel == null) {
                charSequenceLoadLabel = resolveInfo.activityInfo.name;
            }
            CharSequence charSequenceLoadLabel2 = resolveInfo2.loadLabel(this.mPM);
            if (charSequenceLoadLabel2 == null) {
                charSequenceLoadLabel2 = resolveInfo2.activityInfo.name;
            }
            return this.mCollator.compare(charSequenceLoadLabel.toString(), charSequenceLoadLabel2.toString());
        }
    }
}
