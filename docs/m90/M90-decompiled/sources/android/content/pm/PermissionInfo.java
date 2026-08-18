package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import org.apache.tools.ant.taskdefs.SQLExec;

/* JADX INFO: loaded from: classes.dex */
public class PermissionInfo extends PackageItemInfo implements Parcelable {
    public static final Parcelable.Creator<PermissionInfo> CREATOR = new Parcelable.Creator<PermissionInfo>() { // from class: android.content.pm.PermissionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionInfo createFromParcel(Parcel parcel) {
            return new PermissionInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionInfo[] newArray(int i) {
            return new PermissionInfo[i];
        }
    };
    public static final int FLAG_COSTS_MONEY = 1;
    public static final int PROTECTION_DANGEROUS = 1;
    public static final int PROTECTION_FLAG_DEVELOPMENT = 32;
    public static final int PROTECTION_FLAG_SYSTEM = 16;
    public static final int PROTECTION_MASK_BASE = 15;
    public static final int PROTECTION_MASK_FLAGS = 240;
    public static final int PROTECTION_NORMAL = 0;
    public static final int PROTECTION_SIGNATURE = 2;
    public static final int PROTECTION_SIGNATURE_OR_SYSTEM = 3;
    public int descriptionRes;
    public int flags;
    public String group;
    public CharSequence nonLocalizedDescription;
    public int protectionLevel;

    public static int fixProtectionLevel(int i) {
        if (i == 3) {
            return 18;
        }
        return i;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static String protectionToString(int i) {
        int i2 = i & 15;
        String str = i2 != 0 ? i2 != 1 ? i2 != 2 ? i2 != 3 ? "????" : "signatureOrSystem" : "signature" : "dangerous" : SQLExec.DelimiterType.NORMAL;
        if ((i & 16) != 0) {
            str = str + "|system";
        }
        return (i & 32) != 0 ? str + "|development" : str;
    }

    public PermissionInfo() {
    }

    public PermissionInfo(PermissionInfo permissionInfo) {
        super(permissionInfo);
        this.protectionLevel = permissionInfo.protectionLevel;
        this.flags = permissionInfo.flags;
        this.group = permissionInfo.group;
        this.descriptionRes = permissionInfo.descriptionRes;
        this.nonLocalizedDescription = permissionInfo.nonLocalizedDescription;
    }

    public CharSequence loadDescription(PackageManager packageManager) {
        CharSequence text;
        CharSequence charSequence = this.nonLocalizedDescription;
        if (charSequence != null) {
            return charSequence;
        }
        if (this.descriptionRes == 0 || (text = packageManager.getText(this.packageName, this.descriptionRes, null)) == null) {
            return null;
        }
        return text;
    }

    public String toString() {
        return "PermissionInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "}";
    }

    @Override // android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.protectionLevel);
        parcel.writeInt(this.flags);
        parcel.writeString(this.group);
        parcel.writeInt(this.descriptionRes);
        TextUtils.writeToParcel(this.nonLocalizedDescription, parcel, i);
    }

    private PermissionInfo(Parcel parcel) {
        super(parcel);
        this.protectionLevel = parcel.readInt();
        this.flags = parcel.readInt();
        this.group = parcel.readString();
        this.descriptionRes = parcel.readInt();
        this.nonLocalizedDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
    }
}
