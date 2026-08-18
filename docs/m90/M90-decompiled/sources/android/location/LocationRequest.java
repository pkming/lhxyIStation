package android.location;

import android.net.LinkQualityInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.WorkSource;
import android.util.TimeUtils;

/* JADX INFO: loaded from: classes.dex */
public final class LocationRequest implements Parcelable {
    public static final int ACCURACY_BLOCK = 102;
    public static final int ACCURACY_CITY = 104;
    public static final int ACCURACY_FINE = 100;
    public static final Parcelable.Creator<LocationRequest> CREATOR = new Parcelable.Creator<LocationRequest>() { // from class: android.location.LocationRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LocationRequest createFromParcel(Parcel parcel) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setQuality(parcel.readInt());
            locationRequest.setFastestInterval(parcel.readLong());
            locationRequest.setInterval(parcel.readLong());
            locationRequest.setExpireAt(parcel.readLong());
            locationRequest.setNumUpdates(parcel.readInt());
            locationRequest.setSmallestDisplacement(parcel.readFloat());
            locationRequest.setHideFromAppOps(parcel.readInt() != 0);
            String string = parcel.readString();
            if (string != null) {
                locationRequest.setProvider(string);
            }
            WorkSource workSource = (WorkSource) parcel.readParcelable(null);
            if (workSource != null) {
                locationRequest.setWorkSource(workSource);
            }
            return locationRequest;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LocationRequest[] newArray(int i) {
            return new LocationRequest[i];
        }
    };
    private static final double FASTEST_INTERVAL_FACTOR = 6.0d;
    public static final int POWER_HIGH = 203;
    public static final int POWER_LOW = 201;
    public static final int POWER_NONE = 200;
    private long mExpireAt;
    private boolean mExplicitFastestInterval;
    private long mFastestInterval;
    private boolean mHideFromAppOps;
    private long mInterval;
    private int mNumUpdates;
    private String mProvider;
    private int mQuality;
    private float mSmallestDisplacement;
    private WorkSource mWorkSource;

    public static String qualityToString(int i) {
        return i != 100 ? i != 102 ? i != 104 ? i != 203 ? i != 200 ? i != 201 ? "???" : "POWER_LOW" : "POWER_NONE" : "POWER_HIGH" : "ACCURACY_CITY" : "ACCURACY_BLOCK" : "ACCURACY_FINE";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static LocationRequest create() {
        return new LocationRequest();
    }

    public static LocationRequest createFromDeprecatedProvider(String str, long j, float f, boolean z) {
        int i;
        if (j < 0) {
            j = 0;
        }
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (LocationManager.PASSIVE_PROVIDER.equals(str)) {
            i = 200;
        } else {
            i = "gps".equals(str) ? 100 : 201;
        }
        LocationRequest smallestDisplacement = new LocationRequest().setProvider(str).setQuality(i).setInterval(j).setFastestInterval(j).setSmallestDisplacement(f);
        if (z) {
            smallestDisplacement.setNumUpdates(1);
        }
        return smallestDisplacement;
    }

    public static LocationRequest createFromDeprecatedCriteria(Criteria criteria, long j, float f, boolean z) {
        int i;
        if (j < 0) {
            j = 0;
        }
        if (f < 0.0f) {
            f = 0.0f;
        }
        int accuracy = criteria.getAccuracy();
        if (accuracy == 1) {
            i = 100;
        } else if (accuracy != 2) {
            criteria.getPowerRequirement();
            i = 201;
        } else {
            i = 102;
        }
        LocationRequest smallestDisplacement = new LocationRequest().setQuality(i).setInterval(j).setFastestInterval(j).setSmallestDisplacement(f);
        if (z) {
            smallestDisplacement.setNumUpdates(1);
        }
        return smallestDisplacement;
    }

    public LocationRequest() {
        this.mQuality = 201;
        this.mInterval = 3600000L;
        this.mFastestInterval = (long) (3600000 / FASTEST_INTERVAL_FACTOR);
        this.mExplicitFastestInterval = false;
        this.mExpireAt = LinkQualityInfo.UNKNOWN_LONG;
        this.mNumUpdates = Integer.MAX_VALUE;
        this.mSmallestDisplacement = 0.0f;
        this.mWorkSource = null;
        this.mHideFromAppOps = false;
        this.mProvider = LocationManager.FUSED_PROVIDER;
    }

    public LocationRequest(LocationRequest locationRequest) {
        this.mQuality = 201;
        this.mInterval = 3600000L;
        this.mFastestInterval = (long) (3600000 / FASTEST_INTERVAL_FACTOR);
        this.mExplicitFastestInterval = false;
        this.mExpireAt = LinkQualityInfo.UNKNOWN_LONG;
        this.mNumUpdates = Integer.MAX_VALUE;
        this.mSmallestDisplacement = 0.0f;
        this.mWorkSource = null;
        this.mHideFromAppOps = false;
        this.mProvider = LocationManager.FUSED_PROVIDER;
        this.mQuality = locationRequest.mQuality;
        this.mInterval = locationRequest.mInterval;
        this.mFastestInterval = locationRequest.mFastestInterval;
        this.mExplicitFastestInterval = locationRequest.mExplicitFastestInterval;
        this.mExpireAt = locationRequest.mExpireAt;
        this.mNumUpdates = locationRequest.mNumUpdates;
        this.mSmallestDisplacement = locationRequest.mSmallestDisplacement;
        this.mProvider = locationRequest.mProvider;
        this.mWorkSource = locationRequest.mWorkSource;
        this.mHideFromAppOps = locationRequest.mHideFromAppOps;
    }

    public LocationRequest setQuality(int i) {
        checkQuality(i);
        this.mQuality = i;
        return this;
    }

    public int getQuality() {
        return this.mQuality;
    }

    public LocationRequest setInterval(long j) {
        checkInterval(j);
        this.mInterval = j;
        if (!this.mExplicitFastestInterval) {
            this.mFastestInterval = (long) (j / FASTEST_INTERVAL_FACTOR);
        }
        return this;
    }

    public long getInterval() {
        return this.mInterval;
    }

    public LocationRequest setFastestInterval(long j) {
        checkInterval(j);
        this.mExplicitFastestInterval = true;
        this.mFastestInterval = j;
        return this;
    }

    public long getFastestInterval() {
        return this.mFastestInterval;
    }

    public LocationRequest setExpireIn(long j) {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (j > LinkQualityInfo.UNKNOWN_LONG - jElapsedRealtime) {
            this.mExpireAt = LinkQualityInfo.UNKNOWN_LONG;
        } else {
            this.mExpireAt = j + jElapsedRealtime;
        }
        if (this.mExpireAt < 0) {
            this.mExpireAt = 0L;
        }
        return this;
    }

    public LocationRequest setExpireAt(long j) {
        this.mExpireAt = j;
        if (j < 0) {
            this.mExpireAt = 0L;
        }
        return this;
    }

    public long getExpireAt() {
        return this.mExpireAt;
    }

    public LocationRequest setNumUpdates(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("invalid numUpdates: " + i);
        }
        this.mNumUpdates = i;
        return this;
    }

    public int getNumUpdates() {
        return this.mNumUpdates;
    }

    public void decrementNumUpdates() {
        int i = this.mNumUpdates;
        if (i != Integer.MAX_VALUE) {
            this.mNumUpdates = i - 1;
        }
        if (this.mNumUpdates < 0) {
            this.mNumUpdates = 0;
        }
    }

    public LocationRequest setProvider(String str) {
        checkProvider(str);
        this.mProvider = str;
        return this;
    }

    public String getProvider() {
        return this.mProvider;
    }

    public LocationRequest setSmallestDisplacement(float f) {
        checkDisplacement(f);
        this.mSmallestDisplacement = f;
        return this;
    }

    public float getSmallestDisplacement() {
        return this.mSmallestDisplacement;
    }

    public void setWorkSource(WorkSource workSource) {
        this.mWorkSource = workSource;
    }

    public WorkSource getWorkSource() {
        return this.mWorkSource;
    }

    public void setHideFromAppOps(boolean z) {
        this.mHideFromAppOps = z;
    }

    public boolean getHideFromAppOps() {
        return this.mHideFromAppOps;
    }

    private static void checkInterval(long j) {
        if (j < 0) {
            throw new IllegalArgumentException("invalid interval: " + j);
        }
    }

    private static void checkQuality(int i) {
        if (i != 100 && i != 102 && i != 104 && i != 203 && i != 200 && i != 201) {
            throw new IllegalArgumentException("invalid quality: " + i);
        }
    }

    private static void checkDisplacement(float f) {
        if (f < 0.0f) {
            throw new IllegalArgumentException("invalid displacement: " + f);
        }
    }

    private static void checkProvider(String str) {
        if (str == null) {
            throw new IllegalArgumentException("invalid provider: " + str);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mQuality);
        parcel.writeLong(this.mFastestInterval);
        parcel.writeLong(this.mInterval);
        parcel.writeLong(this.mExpireAt);
        parcel.writeInt(this.mNumUpdates);
        parcel.writeFloat(this.mSmallestDisplacement);
        parcel.writeInt(this.mHideFromAppOps ? 1 : 0);
        parcel.writeString(this.mProvider);
        parcel.writeParcelable(this.mWorkSource, 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Request[").append(qualityToString(this.mQuality));
        if (this.mProvider != null) {
            sb.append(' ').append(this.mProvider);
        }
        if (this.mQuality != 200) {
            sb.append(" requested=");
            TimeUtils.formatDuration(this.mInterval, sb);
        }
        sb.append(" fastest=");
        TimeUtils.formatDuration(this.mFastestInterval, sb);
        long j = this.mExpireAt;
        if (j != LinkQualityInfo.UNKNOWN_LONG) {
            long jElapsedRealtime = j - SystemClock.elapsedRealtime();
            sb.append(" expireIn=");
            TimeUtils.formatDuration(jElapsedRealtime, sb);
        }
        if (this.mNumUpdates != Integer.MAX_VALUE) {
            sb.append(" num=").append(this.mNumUpdates);
        }
        sb.append(']');
        return sb.toString();
    }
}
