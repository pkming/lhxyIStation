package android.service.notification;

import android.app.Notification;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class StatusBarNotification implements Parcelable {
    public static final Parcelable.Creator<StatusBarNotification> CREATOR = new Parcelable.Creator<StatusBarNotification>() { // from class: android.service.notification.StatusBarNotification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatusBarNotification createFromParcel(Parcel parcel) {
            return new StatusBarNotification(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatusBarNotification[] newArray(int i) {
            return new StatusBarNotification[i];
        }
    };
    private final String basePkg;
    private final int id;
    private final int initialPid;
    private final Notification notification;
    private final String pkg;
    private final long postTime;
    private final int score;
    private final String tag;
    private final int uid;
    private final UserHandle user;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public StatusBarNotification(String str, int i, String str2, int i2, int i3, int i4, Notification notification, UserHandle userHandle) {
        this(str, null, i, str2, i2, i3, i4, notification, userHandle);
    }

    public StatusBarNotification(String str, String str2, int i, String str3, int i2, int i3, int i4, Notification notification, UserHandle userHandle) {
        this(str, str2, i, str3, i2, i3, i4, notification, userHandle, System.currentTimeMillis());
    }

    public StatusBarNotification(String str, String str2, int i, String str3, int i2, int i3, int i4, Notification notification, UserHandle userHandle, long j) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(notification);
        this.pkg = str;
        this.basePkg = str;
        this.id = i;
        this.tag = str3;
        this.uid = i2;
        this.initialPid = i3;
        this.score = i4;
        this.notification = notification;
        this.user = userHandle;
        notification.setUser(userHandle);
        this.postTime = j;
    }

    public StatusBarNotification(Parcel parcel) {
        this.pkg = parcel.readString();
        this.basePkg = parcel.readString();
        this.id = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.tag = parcel.readString();
        } else {
            this.tag = null;
        }
        this.uid = parcel.readInt();
        this.initialPid = parcel.readInt();
        this.score = parcel.readInt();
        Notification notification = new Notification(parcel);
        this.notification = notification;
        UserHandle fromParcel = UserHandle.readFromParcel(parcel);
        this.user = fromParcel;
        notification.setUser(fromParcel);
        this.postTime = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.pkg);
        parcel.writeString(this.basePkg);
        parcel.writeInt(this.id);
        if (this.tag != null) {
            parcel.writeInt(1);
            parcel.writeString(this.tag);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.uid);
        parcel.writeInt(this.initialPid);
        parcel.writeInt(this.score);
        this.notification.writeToParcel(parcel, i);
        this.user.writeToParcel(parcel, i);
        parcel.writeLong(this.postTime);
    }

    public StatusBarNotification cloneLight() {
        Notification notification = new Notification();
        this.notification.cloneInto(notification, false);
        return new StatusBarNotification(this.pkg, this.basePkg, this.id, this.tag, this.uid, this.initialPid, this.score, notification, this.user, this.postTime);
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public StatusBarNotification m11clone() {
        return new StatusBarNotification(this.pkg, this.basePkg, this.id, this.tag, this.uid, this.initialPid, this.score, this.notification.m5clone(), this.user, this.postTime);
    }

    public String toString() {
        return String.format("StatusBarNotification(pkg=%s user=%s id=%d tag=%s score=%d: %s)", this.pkg, this.user, Integer.valueOf(this.id), this.tag, Integer.valueOf(this.score), this.notification);
    }

    public boolean isOngoing() {
        return (this.notification.flags & 2) != 0;
    }

    public boolean isClearable() {
        return (this.notification.flags & 2) == 0 && (this.notification.flags & 32) == 0;
    }

    public int getUserId() {
        return this.user.getIdentifier();
    }

    public String getPackageName() {
        return this.pkg;
    }

    public int getId() {
        return this.id;
    }

    public String getTag() {
        return this.tag;
    }

    public int getUid() {
        return this.uid;
    }

    public String getBasePkg() {
        return this.basePkg;
    }

    public int getInitialPid() {
        return this.initialPid;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public UserHandle getUser() {
        return this.user;
    }

    public long getPostTime() {
        return this.postTime;
    }

    public int getScore() {
        return this.score;
    }
}
