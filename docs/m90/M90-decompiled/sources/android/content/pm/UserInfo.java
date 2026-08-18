package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;

/* JADX INFO: loaded from: classes.dex */
public class UserInfo implements Parcelable {
    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() { // from class: android.content.pm.UserInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserInfo createFromParcel(Parcel parcel) {
            return new UserInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserInfo[] newArray(int i) {
            return new UserInfo[i];
        }
    };
    public static final int FLAG_ADMIN = 2;
    public static final int FLAG_GUEST = 4;
    public static final int FLAG_INITIALIZED = 16;
    public static final int FLAG_MASK_USER_TYPE = 63;
    public static final int FLAG_PRIMARY = 1;
    public static final int FLAG_RESTRICTED = 8;
    public long creationTime;
    public int flags;
    public String iconPath;
    public int id;
    public long lastLoggedInTime;
    public String name;
    public boolean partial;
    public int serialNumber;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public UserInfo(int i, String str, int i2) {
        this(i, str, null, i2);
    }

    public UserInfo(int i, String str, String str2, int i2) {
        this.id = i;
        this.name = str;
        this.flags = i2;
        this.iconPath = str2;
    }

    public boolean isPrimary() {
        return (this.flags & 1) == 1;
    }

    public boolean isAdmin() {
        return (this.flags & 2) == 2;
    }

    public boolean isGuest() {
        return (this.flags & 4) == 4;
    }

    public boolean isRestricted() {
        return (this.flags & 8) == 8;
    }

    public UserInfo() {
    }

    public UserInfo(UserInfo userInfo) {
        this.name = userInfo.name;
        this.iconPath = userInfo.iconPath;
        this.id = userInfo.id;
        this.flags = userInfo.flags;
        this.serialNumber = userInfo.serialNumber;
        this.creationTime = userInfo.creationTime;
        this.lastLoggedInTime = userInfo.lastLoggedInTime;
        this.partial = userInfo.partial;
    }

    public UserHandle getUserHandle() {
        return new UserHandle(this.id);
    }

    public String toString() {
        return "UserInfo{" + this.id + ":" + this.name + ":" + Integer.toHexString(this.flags) + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.name);
        parcel.writeString(this.iconPath);
        parcel.writeInt(this.flags);
        parcel.writeInt(this.serialNumber);
        parcel.writeLong(this.creationTime);
        parcel.writeLong(this.lastLoggedInTime);
        parcel.writeInt(this.partial ? 1 : 0);
    }

    private UserInfo(Parcel parcel) {
        this.id = parcel.readInt();
        this.name = parcel.readString();
        this.iconPath = parcel.readString();
        this.flags = parcel.readInt();
        this.serialNumber = parcel.readInt();
        this.creationTime = parcel.readLong();
        this.lastLoggedInTime = parcel.readLong();
        this.partial = parcel.readInt() != 0;
    }
}
