package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class PackageInfo implements Parcelable {
    public static final Parcelable.Creator<PackageInfo> CREATOR = new Parcelable.Creator<PackageInfo>() { // from class: android.content.pm.PackageInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageInfo createFromParcel(Parcel parcel) {
            return new PackageInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageInfo[] newArray(int i) {
            return new PackageInfo[i];
        }
    };
    public static final int INSTALL_LOCATION_AUTO = 0;
    public static final int INSTALL_LOCATION_INTERNAL_ONLY = 1;
    public static final int INSTALL_LOCATION_PREFER_EXTERNAL = 2;
    public static final int INSTALL_LOCATION_UNSPECIFIED = -1;
    public static final int REQUESTED_PERMISSION_GRANTED = 2;
    public static final int REQUESTED_PERMISSION_REQUIRED = 1;
    public ActivityInfo[] activities;
    public ApplicationInfo applicationInfo;
    public ConfigurationInfo[] configPreferences;
    public long firstInstallTime;
    public int[] gids;
    public int installLocation;
    public InstrumentationInfo[] instrumentation;
    public long lastUpdateTime;
    public String packageName;
    public PermissionInfo[] permissions;
    public ProviderInfo[] providers;
    public ActivityInfo[] receivers;
    public FeatureInfo[] reqFeatures;
    public String[] requestedPermissions;
    public int[] requestedPermissionsFlags;
    public String requiredAccountType;
    public boolean requiredForAllUsers;
    public String restrictedAccountType;
    public ServiceInfo[] services;
    public String sharedUserId;
    public int sharedUserLabel;
    public Signature[] signatures;
    public int versionCode;
    public String versionName;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PackageInfo() {
        this.installLocation = 1;
    }

    public String toString() {
        return "PackageInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.packageName);
        parcel.writeInt(this.versionCode);
        parcel.writeString(this.versionName);
        parcel.writeString(this.sharedUserId);
        parcel.writeInt(this.sharedUserLabel);
        if (this.applicationInfo != null) {
            parcel.writeInt(1);
            this.applicationInfo.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeLong(this.firstInstallTime);
        parcel.writeLong(this.lastUpdateTime);
        parcel.writeIntArray(this.gids);
        parcel.writeTypedArray(this.activities, i);
        parcel.writeTypedArray(this.receivers, i);
        parcel.writeTypedArray(this.services, i);
        parcel.writeTypedArray(this.providers, i);
        parcel.writeTypedArray(this.instrumentation, i);
        parcel.writeTypedArray(this.permissions, i);
        parcel.writeStringArray(this.requestedPermissions);
        parcel.writeIntArray(this.requestedPermissionsFlags);
        parcel.writeTypedArray(this.signatures, i);
        parcel.writeTypedArray(this.configPreferences, i);
        parcel.writeTypedArray(this.reqFeatures, i);
        parcel.writeInt(this.installLocation);
        parcel.writeInt(this.requiredForAllUsers ? 1 : 0);
        parcel.writeString(this.restrictedAccountType);
        parcel.writeString(this.requiredAccountType);
    }

    private PackageInfo(Parcel parcel) {
        this.installLocation = 1;
        this.packageName = parcel.readString();
        this.versionCode = parcel.readInt();
        this.versionName = parcel.readString();
        this.sharedUserId = parcel.readString();
        this.sharedUserLabel = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.applicationInfo = ApplicationInfo.CREATOR.createFromParcel(parcel);
        }
        this.firstInstallTime = parcel.readLong();
        this.lastUpdateTime = parcel.readLong();
        this.gids = parcel.createIntArray();
        this.activities = (ActivityInfo[]) parcel.createTypedArray(ActivityInfo.CREATOR);
        this.receivers = (ActivityInfo[]) parcel.createTypedArray(ActivityInfo.CREATOR);
        this.services = (ServiceInfo[]) parcel.createTypedArray(ServiceInfo.CREATOR);
        this.providers = (ProviderInfo[]) parcel.createTypedArray(ProviderInfo.CREATOR);
        this.instrumentation = (InstrumentationInfo[]) parcel.createTypedArray(InstrumentationInfo.CREATOR);
        this.permissions = (PermissionInfo[]) parcel.createTypedArray(PermissionInfo.CREATOR);
        this.requestedPermissions = parcel.createStringArray();
        this.requestedPermissionsFlags = parcel.createIntArray();
        this.signatures = (Signature[]) parcel.createTypedArray(Signature.CREATOR);
        this.configPreferences = (ConfigurationInfo[]) parcel.createTypedArray(ConfigurationInfo.CREATOR);
        this.reqFeatures = (FeatureInfo[]) parcel.createTypedArray(FeatureInfo.CREATOR);
        this.installLocation = parcel.readInt();
        this.requiredForAllUsers = parcel.readInt() != 0;
        this.restrictedAccountType = parcel.readString();
        this.requiredAccountType = parcel.readString();
    }
}
