package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.Objects;
import com.android.internal.util.Preconditions;

/* JADX INFO: loaded from: classes.dex */
public class NetworkPolicy implements Parcelable, Comparable<NetworkPolicy> {
    public static final Parcelable.Creator<NetworkPolicy> CREATOR = new Parcelable.Creator<NetworkPolicy>() { // from class: android.net.NetworkPolicy.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkPolicy createFromParcel(Parcel parcel) {
            return new NetworkPolicy(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkPolicy[] newArray(int i) {
            return new NetworkPolicy[i];
        }
    };
    public static final int CYCLE_NONE = -1;
    private static final long DEFAULT_MTU = 1500;
    public static final long LIMIT_DISABLED = -1;
    public static final long SNOOZE_NEVER = -1;
    public static final long WARNING_DISABLED = -1;
    public int cycleDay;
    public String cycleTimezone;
    public boolean inferred;
    public long lastLimitSnooze;
    public long lastWarningSnooze;
    public long limitBytes;
    public boolean metered;
    public final NetworkTemplate template;
    public long warningBytes;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Deprecated
    public NetworkPolicy(NetworkTemplate networkTemplate, int i, String str, long j, long j2, boolean z) {
        this(networkTemplate, i, str, j, j2, -1L, -1L, z, false);
    }

    public NetworkPolicy(NetworkTemplate networkTemplate, int i, String str, long j, long j2, long j3, long j4, boolean z, boolean z2) {
        this.template = (NetworkTemplate) Preconditions.checkNotNull(networkTemplate, "missing NetworkTemplate");
        this.cycleDay = i;
        this.cycleTimezone = (String) Preconditions.checkNotNull(str, "missing cycleTimezone");
        this.warningBytes = j;
        this.limitBytes = j2;
        this.lastWarningSnooze = j3;
        this.lastLimitSnooze = j4;
        this.metered = z;
        this.inferred = z2;
    }

    public NetworkPolicy(Parcel parcel) {
        this.template = (NetworkTemplate) parcel.readParcelable(null);
        this.cycleDay = parcel.readInt();
        this.cycleTimezone = parcel.readString();
        this.warningBytes = parcel.readLong();
        this.limitBytes = parcel.readLong();
        this.lastWarningSnooze = parcel.readLong();
        this.lastLimitSnooze = parcel.readLong();
        this.metered = parcel.readInt() != 0;
        this.inferred = parcel.readInt() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.template, i);
        parcel.writeInt(this.cycleDay);
        parcel.writeString(this.cycleTimezone);
        parcel.writeLong(this.warningBytes);
        parcel.writeLong(this.limitBytes);
        parcel.writeLong(this.lastWarningSnooze);
        parcel.writeLong(this.lastLimitSnooze);
        parcel.writeInt(this.metered ? 1 : 0);
        parcel.writeInt(this.inferred ? 1 : 0);
    }

    public boolean isOverWarning(long j) {
        long j2 = this.warningBytes;
        return j2 != -1 && j >= j2;
    }

    public boolean isOverLimit(long j) {
        long j2 = j + 3000;
        long j3 = this.limitBytes;
        return j3 != -1 && j2 >= j3;
    }

    public void clearSnooze() {
        this.lastWarningSnooze = -1L;
        this.lastLimitSnooze = -1L;
    }

    public boolean hasCycle() {
        return this.cycleDay != -1;
    }

    @Override // java.lang.Comparable
    public int compareTo(NetworkPolicy networkPolicy) {
        if (networkPolicy == null) {
            return -1;
        }
        long j = networkPolicy.limitBytes;
        if (j == -1) {
            return -1;
        }
        long j2 = this.limitBytes;
        return (j2 == -1 || j < j2) ? 1 : 0;
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.template, Integer.valueOf(this.cycleDay), this.cycleTimezone, Long.valueOf(this.warningBytes), Long.valueOf(this.limitBytes), Long.valueOf(this.lastWarningSnooze), Long.valueOf(this.lastLimitSnooze), Boolean.valueOf(this.metered), Boolean.valueOf(this.inferred)});
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NetworkPolicy)) {
            return false;
        }
        NetworkPolicy networkPolicy = (NetworkPolicy) obj;
        return this.cycleDay == networkPolicy.cycleDay && this.warningBytes == networkPolicy.warningBytes && this.limitBytes == networkPolicy.limitBytes && this.lastWarningSnooze == networkPolicy.lastWarningSnooze && this.lastLimitSnooze == networkPolicy.lastLimitSnooze && this.metered == networkPolicy.metered && this.inferred == networkPolicy.inferred && Objects.equal(this.cycleTimezone, networkPolicy.cycleTimezone) && Objects.equal(this.template, networkPolicy.template);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("NetworkPolicy");
        sb.append("[").append(this.template).append("]:");
        sb.append(" cycleDay=").append(this.cycleDay);
        sb.append(", cycleTimezone=").append(this.cycleTimezone);
        sb.append(", warningBytes=").append(this.warningBytes);
        sb.append(", limitBytes=").append(this.limitBytes);
        sb.append(", lastWarningSnooze=").append(this.lastWarningSnooze);
        sb.append(", lastLimitSnooze=").append(this.lastLimitSnooze);
        sb.append(", metered=").append(this.metered);
        sb.append(", inferred=").append(this.inferred);
        return sb.toString();
    }
}
