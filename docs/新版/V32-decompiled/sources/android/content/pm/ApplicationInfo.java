package android.content.pm;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Printer;
import java.text.Collator;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class ApplicationInfo extends PackageItemInfo implements Parcelable {
    public static final Parcelable.Creator<ApplicationInfo> CREATOR = new Parcelable.Creator<ApplicationInfo>() { // from class: android.content.pm.ApplicationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApplicationInfo createFromParcel(Parcel parcel) {
            return new ApplicationInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApplicationInfo[] newArray(int i) {
            return new ApplicationInfo[i];
        }
    };
    public static final int FLAG_ALLOW_BACKUP = 32768;
    public static final int FLAG_ALLOW_CLEAR_USER_DATA = 64;
    public static final int FLAG_ALLOW_TASK_REPARENTING = 32;
    public static final int FLAG_BLOCKED = 134217728;
    public static final int FLAG_CANT_SAVE_STATE = 268435456;
    public static final int FLAG_DEBUGGABLE = 2;
    public static final int FLAG_EXTERNAL_STORAGE = 262144;
    public static final int FLAG_FACTORY_TEST = 16;
    public static final int FLAG_FORWARD_LOCK = 536870912;
    public static final int FLAG_HAS_CODE = 4;
    public static final int FLAG_INSTALLED = 8388608;
    public static final int FLAG_IS_DATA_ONLY = 16777216;
    public static final int FLAG_KILL_AFTER_RESTORE = 65536;
    public static final int FLAG_LARGE_HEAP = 1048576;
    public static final int FLAG_PERSISTENT = 8;
    public static final int FLAG_PRIVILEGED = 1073741824;
    public static final int FLAG_RESIZEABLE_FOR_SCREENS = 4096;
    public static final int FLAG_RESTORE_ANY_VERSION = 131072;
    public static final int FLAG_STOPPED = 2097152;
    public static final int FLAG_SUPPORTS_LARGE_SCREENS = 2048;
    public static final int FLAG_SUPPORTS_NORMAL_SCREENS = 1024;
    public static final int FLAG_SUPPORTS_RTL = 4194304;
    public static final int FLAG_SUPPORTS_SCREEN_DENSITIES = 8192;
    public static final int FLAG_SUPPORTS_SMALL_SCREENS = 512;
    public static final int FLAG_SUPPORTS_XLARGE_SCREENS = 524288;
    public static final int FLAG_SYSTEM = 1;
    public static final int FLAG_TEST_ONLY = 256;
    public static final int FLAG_UPDATED_SYSTEM_APP = 128;
    public static final int FLAG_VM_SAFE_MODE = 16384;
    public String backupAgentName;
    public String className;
    public int compatibleWidthLimitDp;
    public String dataDir;
    public int descriptionRes;
    public boolean enabled;
    public int enabledSetting;
    public int flags;
    public int installLocation;
    public int largestWidthLimitDp;
    public String manageSpaceActivityName;
    public String nativeLibraryDir;
    public String permission;
    public String processName;
    public String publicSourceDir;
    public int requiresSmallestWidthDp;
    public String[] resourceDirs;
    public String seinfo;
    public String[] sharedLibraryFiles;
    public String sourceDir;
    public int targetSdkVersion;
    public String taskAffinity;
    public int theme;
    public int uiOptions;
    public int uid;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.content.pm.PackageItemInfo
    protected ApplicationInfo getApplicationInfo() {
        return this;
    }

    public void dump(Printer printer, String str) {
        super.dumpFront(printer, str);
        if (this.className != null) {
            printer.println(str + "className=" + this.className);
        }
        if (this.permission != null) {
            printer.println(str + "permission=" + this.permission);
        }
        printer.println(str + "processName=" + this.processName);
        printer.println(str + "taskAffinity=" + this.taskAffinity);
        printer.println(str + "uid=" + this.uid + " flags=0x" + Integer.toHexString(this.flags) + " theme=0x" + Integer.toHexString(this.theme));
        printer.println(str + "requiresSmallestWidthDp=" + this.requiresSmallestWidthDp + " compatibleWidthLimitDp=" + this.compatibleWidthLimitDp + " largestWidthLimitDp=" + this.largestWidthLimitDp);
        printer.println(str + "sourceDir=" + this.sourceDir);
        String str2 = this.sourceDir;
        if (str2 == null) {
            if (this.publicSourceDir != null) {
                printer.println(str + "publicSourceDir=" + this.publicSourceDir);
            }
        } else if (!str2.equals(this.publicSourceDir)) {
            printer.println(str + "publicSourceDir=" + this.publicSourceDir);
        }
        if (this.resourceDirs != null) {
            printer.println(str + "resourceDirs=" + this.resourceDirs);
        }
        if (this.seinfo != null) {
            printer.println(str + "seinfo=" + this.seinfo);
        }
        printer.println(str + "dataDir=" + this.dataDir);
        if (this.sharedLibraryFiles != null) {
            printer.println(str + "sharedLibraryFiles=" + this.sharedLibraryFiles);
        }
        printer.println(str + "enabled=" + this.enabled + " targetSdkVersion=" + this.targetSdkVersion);
        if (this.manageSpaceActivityName != null) {
            printer.println(str + "manageSpaceActivityName=" + this.manageSpaceActivityName);
        }
        if (this.descriptionRes != 0) {
            printer.println(str + "description=0x" + Integer.toHexString(this.descriptionRes));
        }
        if (this.uiOptions != 0) {
            printer.println(str + "uiOptions=0x" + Integer.toHexString(this.uiOptions));
        }
        printer.println(str + "supportsRtl=" + (hasRtlSupport() ? "true" : "false"));
        super.dumpBack(printer, str);
    }

    public boolean hasRtlSupport() {
        return (this.flags & 4194304) == 4194304;
    }

    public static class DisplayNameComparator implements Comparator<ApplicationInfo> {
        private PackageManager mPM;
        private final Collator sCollator = Collator.getInstance();

        public DisplayNameComparator(PackageManager packageManager) {
            this.mPM = packageManager;
        }

        @Override // java.util.Comparator
        public final int compare(ApplicationInfo applicationInfo, ApplicationInfo applicationInfo2) {
            CharSequence applicationLabel = this.mPM.getApplicationLabel(applicationInfo);
            if (applicationLabel == null) {
                applicationLabel = applicationInfo.packageName;
            }
            CharSequence applicationLabel2 = this.mPM.getApplicationLabel(applicationInfo2);
            if (applicationLabel2 == null) {
                applicationLabel2 = applicationInfo2.packageName;
            }
            return this.sCollator.compare(applicationLabel.toString(), applicationLabel2.toString());
        }
    }

    public ApplicationInfo() {
        this.uiOptions = 0;
        this.flags = 0;
        this.requiresSmallestWidthDp = 0;
        this.compatibleWidthLimitDp = 0;
        this.largestWidthLimitDp = 0;
        this.enabled = true;
        this.enabledSetting = 0;
        this.installLocation = -1;
    }

    public ApplicationInfo(ApplicationInfo applicationInfo) {
        super(applicationInfo);
        this.uiOptions = 0;
        this.flags = 0;
        this.requiresSmallestWidthDp = 0;
        this.compatibleWidthLimitDp = 0;
        this.largestWidthLimitDp = 0;
        this.enabled = true;
        this.enabledSetting = 0;
        this.installLocation = -1;
        this.taskAffinity = applicationInfo.taskAffinity;
        this.permission = applicationInfo.permission;
        this.processName = applicationInfo.processName;
        this.className = applicationInfo.className;
        this.theme = applicationInfo.theme;
        this.flags = applicationInfo.flags;
        this.requiresSmallestWidthDp = applicationInfo.requiresSmallestWidthDp;
        this.compatibleWidthLimitDp = applicationInfo.compatibleWidthLimitDp;
        this.largestWidthLimitDp = applicationInfo.largestWidthLimitDp;
        this.sourceDir = applicationInfo.sourceDir;
        this.publicSourceDir = applicationInfo.publicSourceDir;
        this.nativeLibraryDir = applicationInfo.nativeLibraryDir;
        this.resourceDirs = applicationInfo.resourceDirs;
        this.seinfo = applicationInfo.seinfo;
        this.sharedLibraryFiles = applicationInfo.sharedLibraryFiles;
        this.dataDir = applicationInfo.dataDir;
        this.uid = applicationInfo.uid;
        this.targetSdkVersion = applicationInfo.targetSdkVersion;
        this.enabled = applicationInfo.enabled;
        this.enabledSetting = applicationInfo.enabledSetting;
        this.installLocation = applicationInfo.installLocation;
        this.manageSpaceActivityName = applicationInfo.manageSpaceActivityName;
        this.descriptionRes = applicationInfo.descriptionRes;
        this.uiOptions = applicationInfo.uiOptions;
        this.backupAgentName = applicationInfo.backupAgentName;
    }

    public String toString() {
        return "ApplicationInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
    }

    @Override // android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.taskAffinity);
        parcel.writeString(this.permission);
        parcel.writeString(this.processName);
        parcel.writeString(this.className);
        parcel.writeInt(this.theme);
        parcel.writeInt(this.flags);
        parcel.writeInt(this.requiresSmallestWidthDp);
        parcel.writeInt(this.compatibleWidthLimitDp);
        parcel.writeInt(this.largestWidthLimitDp);
        parcel.writeString(this.sourceDir);
        parcel.writeString(this.publicSourceDir);
        parcel.writeString(this.nativeLibraryDir);
        parcel.writeStringArray(this.resourceDirs);
        parcel.writeString(this.seinfo);
        parcel.writeStringArray(this.sharedLibraryFiles);
        parcel.writeString(this.dataDir);
        parcel.writeInt(this.uid);
        parcel.writeInt(this.targetSdkVersion);
        parcel.writeInt(this.enabled ? 1 : 0);
        parcel.writeInt(this.enabledSetting);
        parcel.writeInt(this.installLocation);
        parcel.writeString(this.manageSpaceActivityName);
        parcel.writeString(this.backupAgentName);
        parcel.writeInt(this.descriptionRes);
        parcel.writeInt(this.uiOptions);
    }

    private ApplicationInfo(Parcel parcel) {
        super(parcel);
        this.uiOptions = 0;
        this.flags = 0;
        this.requiresSmallestWidthDp = 0;
        this.compatibleWidthLimitDp = 0;
        this.largestWidthLimitDp = 0;
        this.enabled = true;
        this.enabledSetting = 0;
        this.installLocation = -1;
        this.taskAffinity = parcel.readString();
        this.permission = parcel.readString();
        this.processName = parcel.readString();
        this.className = parcel.readString();
        this.theme = parcel.readInt();
        this.flags = parcel.readInt();
        this.requiresSmallestWidthDp = parcel.readInt();
        this.compatibleWidthLimitDp = parcel.readInt();
        this.largestWidthLimitDp = parcel.readInt();
        this.sourceDir = parcel.readString();
        this.publicSourceDir = parcel.readString();
        this.nativeLibraryDir = parcel.readString();
        this.resourceDirs = parcel.readStringArray();
        this.seinfo = parcel.readString();
        this.sharedLibraryFiles = parcel.readStringArray();
        this.dataDir = parcel.readString();
        this.uid = parcel.readInt();
        this.targetSdkVersion = parcel.readInt();
        this.enabled = parcel.readInt() != 0;
        this.enabledSetting = parcel.readInt();
        this.installLocation = parcel.readInt();
        this.manageSpaceActivityName = parcel.readString();
        this.backupAgentName = parcel.readString();
        this.descriptionRes = parcel.readInt();
        this.uiOptions = parcel.readInt();
    }

    public CharSequence loadDescription(PackageManager packageManager) {
        CharSequence text;
        if (this.descriptionRes == 0 || (text = packageManager.getText(this.packageName, this.descriptionRes, this)) == null) {
            return null;
        }
        return text;
    }

    public void disableCompatibilityMode() {
        this.flags |= 540160;
    }

    @Override // android.content.pm.PackageItemInfo
    protected Drawable loadDefaultIcon(PackageManager packageManager) {
        if ((this.flags & 262144) != 0 && isPackageUnavailable(packageManager)) {
            return Resources.getSystem().getDrawable(17303031);
        }
        return packageManager.getDefaultActivityIcon();
    }

    private boolean isPackageUnavailable(PackageManager packageManager) {
        try {
            return packageManager.getPackageInfo(this.packageName, 0) == null;
        } catch (PackageManager.NameNotFoundException unused) {
            return true;
        }
    }
}
