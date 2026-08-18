package android.os;

import android.os.Parcelable;
import android.text.format.DateFormat;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public final class UserHandle implements Parcelable {
    public static final boolean MU_ENABLED = true;
    public static final int PER_USER_RANGE = 100000;
    public static final int USER_ALL = -1;
    public static final int USER_CURRENT = -2;
    public static final int USER_CURRENT_OR_SELF = -3;
    public static final int USER_NULL = -10000;
    public static final int USER_OWNER = 0;
    final int mHandle;
    public static final UserHandle ALL = new UserHandle(-1);
    public static final UserHandle CURRENT = new UserHandle(-2);
    public static final UserHandle CURRENT_OR_SELF = new UserHandle(-3);
    public static final UserHandle OWNER = new UserHandle(0);
    public static final Parcelable.Creator<UserHandle> CREATOR = new Parcelable.Creator<UserHandle>() { // from class: android.os.UserHandle.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserHandle createFromParcel(Parcel parcel) {
            return new UserHandle(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserHandle[] newArray(int i) {
            return new UserHandle[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static final boolean isSameUser(int i, int i2) {
        return getUserId(i) == getUserId(i2);
    }

    public static final boolean isSameApp(int i, int i2) {
        return getAppId(i) == getAppId(i2);
    }

    public static final boolean isIsolated(int i) {
        int appId;
        return i > 0 && (appId = getAppId(i)) >= 99000 && appId <= 99999;
    }

    public static boolean isApp(int i) {
        int appId;
        return i > 0 && (appId = getAppId(i)) >= 10000 && appId <= 19999;
    }

    public static final int getUserId(int i) {
        return i / 100000;
    }

    public static final int getCallingUserId() {
        return getUserId(Binder.getCallingUid());
    }

    public static final int getUid(int i, int i2) {
        return (i * 100000) + (i2 % 100000);
    }

    public static final int getAppId(int i) {
        return i % 100000;
    }

    public static final int getSharedAppGid(int i) {
        return ((i % 100000) + 50000) - 10000;
    }

    public static void formatUid(StringBuilder sb, int i) {
        if (i < 10000) {
            sb.append(i);
            return;
        }
        sb.append('u');
        sb.append(getUserId(i));
        int appId = getAppId(i);
        if (appId >= 99000 && appId <= 99999) {
            sb.append('i');
            sb.append(appId - Process.FIRST_ISOLATED_UID);
        } else if (appId >= 10000) {
            sb.append(DateFormat.AM_PM);
            sb.append(appId - 10000);
        } else {
            sb.append('s');
            sb.append(appId);
        }
    }

    public static void formatUid(PrintWriter printWriter, int i) {
        if (i < 10000) {
            printWriter.print(i);
            return;
        }
        printWriter.print('u');
        printWriter.print(getUserId(i));
        int appId = getAppId(i);
        if (appId >= 99000 && appId <= 99999) {
            printWriter.print('i');
            printWriter.print(appId - Process.FIRST_ISOLATED_UID);
        } else if (appId >= 10000) {
            printWriter.print(DateFormat.AM_PM);
            printWriter.print(appId - 10000);
        } else {
            printWriter.print('s');
            printWriter.print(appId);
        }
    }

    public static final int myUserId() {
        return getUserId(Process.myUid());
    }

    public UserHandle(int i) {
        this.mHandle = i;
    }

    public int getIdentifier() {
        return this.mHandle;
    }

    public String toString() {
        return "UserHandle{" + this.mHandle + "}";
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            return this.mHandle == ((UserHandle) obj).mHandle;
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public int hashCode() {
        return this.mHandle;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mHandle);
    }

    public static void writeToParcel(UserHandle userHandle, Parcel parcel) {
        if (userHandle != null) {
            userHandle.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(-10000);
        }
    }

    public static UserHandle readFromParcel(Parcel parcel) {
        int i = parcel.readInt();
        if (i != -10000) {
            return new UserHandle(i);
        }
        return null;
    }

    public UserHandle(Parcel parcel) {
        this.mHandle = parcel.readInt();
    }
}
