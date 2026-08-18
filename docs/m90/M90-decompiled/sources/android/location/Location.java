package android.location;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Printer;
import android.util.TimeUtils;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes.dex */
public class Location implements Parcelable {
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() { // from class: android.location.Location.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Location createFromParcel(Parcel parcel) {
            Location location = new Location(parcel.readString());
            location.mTime = parcel.readLong();
            location.mElapsedRealtimeNanos = parcel.readLong();
            location.mLatitude = parcel.readDouble();
            location.mLongitude = parcel.readDouble();
            location.mHasAltitude = parcel.readInt() != 0;
            location.mAltitude = parcel.readDouble();
            location.mHasSpeed = parcel.readInt() != 0;
            location.mSpeed = parcel.readFloat();
            location.mHasBearing = parcel.readInt() != 0;
            location.mBearing = parcel.readFloat();
            location.mHasAccuracy = parcel.readInt() != 0;
            location.mAccuracy = parcel.readFloat();
            location.mExtras = parcel.readBundle();
            location.mIsFromMockProvider = parcel.readInt() != 0;
            return location;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Location[] newArray(int i) {
            return new Location[i];
        }
    };
    public static final String EXTRA_COARSE_LOCATION = "coarseLocation";
    public static final String EXTRA_NO_GPS_LOCATION = "noGPSLocation";
    public static final int FORMAT_DEGREES = 0;
    public static final int FORMAT_MINUTES = 1;
    public static final int FORMAT_SECONDS = 2;
    private float mAccuracy;
    private double mAltitude;
    private float mBearing;
    private float mDistance;
    private long mElapsedRealtimeNanos;
    private Bundle mExtras;
    private boolean mHasAccuracy;
    private boolean mHasAltitude;
    private boolean mHasBearing;
    private boolean mHasSpeed;
    private float mInitialBearing;
    private boolean mIsFromMockProvider;
    private double mLat1;
    private double mLat2;
    private double mLatitude;
    private double mLon1;
    private double mLon2;
    private double mLongitude;
    private String mProvider;
    private final float[] mResults;
    private float mSpeed;
    private long mTime;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Location(String str) {
        this.mTime = 0L;
        this.mElapsedRealtimeNanos = 0L;
        this.mLatitude = 0.0d;
        this.mLongitude = 0.0d;
        this.mHasAltitude = false;
        this.mAltitude = 0.0d;
        this.mHasSpeed = false;
        this.mSpeed = 0.0f;
        this.mHasBearing = false;
        this.mBearing = 0.0f;
        this.mHasAccuracy = false;
        this.mAccuracy = 0.0f;
        this.mExtras = null;
        this.mIsFromMockProvider = false;
        this.mLat1 = 0.0d;
        this.mLon1 = 0.0d;
        this.mLat2 = 0.0d;
        this.mLon2 = 0.0d;
        this.mDistance = 0.0f;
        this.mInitialBearing = 0.0f;
        this.mResults = new float[2];
        this.mProvider = str;
    }

    public Location(Location location) {
        this.mTime = 0L;
        this.mElapsedRealtimeNanos = 0L;
        this.mLatitude = 0.0d;
        this.mLongitude = 0.0d;
        this.mHasAltitude = false;
        this.mAltitude = 0.0d;
        this.mHasSpeed = false;
        this.mSpeed = 0.0f;
        this.mHasBearing = false;
        this.mBearing = 0.0f;
        this.mHasAccuracy = false;
        this.mAccuracy = 0.0f;
        this.mExtras = null;
        this.mIsFromMockProvider = false;
        this.mLat1 = 0.0d;
        this.mLon1 = 0.0d;
        this.mLat2 = 0.0d;
        this.mLon2 = 0.0d;
        this.mDistance = 0.0f;
        this.mInitialBearing = 0.0f;
        this.mResults = new float[2];
        set(location);
    }

    public void set(Location location) {
        this.mProvider = location.mProvider;
        this.mTime = location.mTime;
        this.mElapsedRealtimeNanos = location.mElapsedRealtimeNanos;
        this.mLatitude = location.mLatitude;
        this.mLongitude = location.mLongitude;
        this.mHasAltitude = location.mHasAltitude;
        this.mAltitude = location.mAltitude;
        this.mHasSpeed = location.mHasSpeed;
        this.mSpeed = location.mSpeed;
        this.mHasBearing = location.mHasBearing;
        this.mBearing = location.mBearing;
        this.mHasAccuracy = location.mHasAccuracy;
        this.mAccuracy = location.mAccuracy;
        this.mExtras = location.mExtras == null ? null : new Bundle(location.mExtras);
        this.mIsFromMockProvider = location.mIsFromMockProvider;
    }

    public void reset() {
        this.mProvider = null;
        this.mTime = 0L;
        this.mElapsedRealtimeNanos = 0L;
        this.mLatitude = 0.0d;
        this.mLongitude = 0.0d;
        this.mHasAltitude = false;
        this.mAltitude = 0.0d;
        this.mHasSpeed = false;
        this.mSpeed = 0.0f;
        this.mHasBearing = false;
        this.mBearing = 0.0f;
        this.mHasAccuracy = false;
        this.mAccuracy = 0.0f;
        this.mExtras = null;
        this.mIsFromMockProvider = false;
    }

    public static String convert(double d, int i) {
        if (d < -180.0d || d > 180.0d || Double.isNaN(d)) {
            throw new IllegalArgumentException("coordinate=" + d);
        }
        if (i != 0 && i != 1 && i != 2) {
            throw new IllegalArgumentException("outputType=" + i);
        }
        StringBuilder sb = new StringBuilder();
        if (d < 0.0d) {
            sb.append('-');
            d = -d;
        }
        DecimalFormat decimalFormat = new DecimalFormat("###.#####");
        if (i == 1 || i == 2) {
            int iFloor = (int) Math.floor(d);
            sb.append(iFloor);
            sb.append(':');
            d = (d - ((double) iFloor)) * 60.0d;
            if (i == 2) {
                int iFloor2 = (int) Math.floor(d);
                sb.append(iFloor2);
                sb.append(':');
                d = (d - ((double) iFloor2)) * 60.0d;
            }
        }
        sb.append(decimalFormat.format(d));
        return sb.toString();
    }

    public static double convert(String str) {
        boolean z;
        double d;
        double d2;
        Objects.requireNonNull(str, "coordinate");
        boolean z2 = false;
        if (str.charAt(0) == '-') {
            str = str.substring(1);
            z = true;
        } else {
            z = false;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, ":");
        int iCountTokens = stringTokenizer.countTokens();
        if (iCountTokens < 1) {
            throw new IllegalArgumentException("coordinate=" + str);
        }
        try {
            String strNextToken = stringTokenizer.nextToken();
            if (iCountTokens == 1) {
                double d3 = Double.parseDouble(strNextToken);
                return z ? -d3 : d3;
            }
            String strNextToken2 = stringTokenizer.nextToken();
            int i = Integer.parseInt(strNextToken);
            if (stringTokenizer.hasMoreTokens()) {
                d = Integer.parseInt(strNextToken2);
                d2 = Double.parseDouble(stringTokenizer.nextToken());
            } else {
                d = Double.parseDouble(strNextToken2);
                d2 = 0.0d;
            }
            if (z && i == 180 && d == 0.0d && d2 == 0.0d) {
                z2 = true;
            }
            double d4 = i;
            if (d4 < 0.0d || (i > 179 && !z2)) {
                throw new IllegalArgumentException("coordinate=" + str);
            }
            if (d < 0.0d || d > 59.0d) {
                throw new IllegalArgumentException("coordinate=" + str);
            }
            if (d2 < 0.0d || d2 > 59.0d) {
                throw new IllegalArgumentException("coordinate=" + str);
            }
            double d5 = (((d4 * 3600.0d) + (d * 60.0d)) + d2) / 3600.0d;
            return z ? -d5 : d5;
        } catch (NumberFormatException unused) {
            throw new IllegalArgumentException("coordinate=" + str);
        }
    }

    private static void computeDistanceAndBearing(double d, double d2, double d3, double d4, float[] fArr) {
        double d5;
        double d6;
        double d7 = (0.017453292519943295d * d4) - (d2 * 0.017453292519943295d);
        double dAtan = Math.atan(Math.tan(d * 0.017453292519943295d) * 0.996647189328169d);
        double dAtan2 = Math.atan(0.996647189328169d * Math.tan(d3 * 0.017453292519943295d));
        double dCos = Math.cos(dAtan);
        double dCos2 = Math.cos(dAtan2);
        double dSin = Math.sin(dAtan);
        double dSin2 = Math.sin(dAtan2);
        double d8 = dCos * dCos2;
        double d9 = dSin * dSin2;
        double d10 = d7;
        double dCos3 = 0.0d;
        double d11 = 0.0d;
        double dSin3 = 0.0d;
        double dAtan22 = 0.0d;
        double d12 = 0.0d;
        int i = 0;
        while (true) {
            if (i >= 20) {
                d5 = dSin;
                d6 = dSin2;
                break;
            }
            dCos3 = Math.cos(d10);
            dSin3 = Math.sin(d10);
            double d13 = dCos2 * dSin3;
            double d14 = (dCos * dSin2) - ((dSin * dCos2) * dCos3);
            d5 = dSin;
            double dSqrt = Math.sqrt((d13 * d13) + (d14 * d14));
            d6 = dSin2;
            double d15 = d9 + (d8 * dCos3);
            dAtan22 = Math.atan2(dSqrt, d15);
            double d16 = dSqrt == 0.0d ? 0.0d : (d8 * dSin3) / dSqrt;
            double d17 = 1.0d - (d16 * d16);
            double d18 = d17 == 0.0d ? 0.0d : d15 - ((d9 * 2.0d) / d17);
            double d19 = 0.006739496756586903d * d17;
            double d20 = ((d19 / 16384.0d) * (((((320.0d - (175.0d * d19)) * d19) - 768.0d) * d19) + 4096.0d)) + 1.0d;
            double d21 = (d19 / 1024.0d) * ((d19 * (((74.0d - (47.0d * d19)) * d19) - 128.0d)) + 256.0d);
            double d22 = 2.0955066698943685E-4d * d17 * (((4.0d - (d17 * 3.0d)) * 0.0033528106718309896d) + 4.0d);
            double d23 = d18 * d18;
            d12 = d21 * dSqrt * (d18 + ((d21 / 4.0d) * ((((d23 * 2.0d) - 1.0d) * d15) - ((((d21 / 6.0d) * d18) * (((dSqrt * 4.0d) * dSqrt) - 3.0d)) * ((d23 * 4.0d) - 3.0d)))));
            double d24 = d7 + ((1.0d - d22) * 0.0033528106718309896d * d16 * (dAtan22 + (dSqrt * d22 * (d18 + (d22 * d15 * (((2.0d * d18) * d18) - 1.0d))))));
            if (Math.abs((d24 - d10) / d24) < 1.0E-12d) {
                d11 = d20;
                break;
            }
            i++;
            dSin = d5;
            dSin2 = d6;
            d10 = d24;
            d11 = d20;
        }
        fArr[0] = (float) (6356752.3142d * d11 * (dAtan22 - d12));
        if (fArr.length > 1) {
            double d25 = dCos * d6;
            fArr[1] = (float) (((double) ((float) Math.atan2(dCos2 * dSin3, d25 - ((d5 * dCos2) * dCos3)))) * 57.29577951308232d);
            if (fArr.length > 2) {
                fArr[2] = (float) (((double) ((float) Math.atan2(dCos * dSin3, ((-d5) * dCos2) + (d25 * dCos3)))) * 57.29577951308232d);
            }
        }
    }

    public static void distanceBetween(double d, double d2, double d3, double d4, float[] fArr) {
        if (fArr == null || fArr.length < 1) {
            throw new IllegalArgumentException("results is null or has length < 1");
        }
        computeDistanceAndBearing(d, d2, d3, d4, fArr);
    }

    public float distanceTo(Location location) {
        float f;
        synchronized (this.mResults) {
            double d = this.mLatitude;
            if (d != this.mLat1 || this.mLongitude != this.mLon1 || location.mLatitude != this.mLat2 || location.mLongitude != this.mLon2) {
                computeDistanceAndBearing(d, this.mLongitude, location.mLatitude, location.mLongitude, this.mResults);
                this.mLat1 = this.mLatitude;
                this.mLon1 = this.mLongitude;
                this.mLat2 = location.mLatitude;
                this.mLon2 = location.mLongitude;
                float[] fArr = this.mResults;
                this.mDistance = fArr[0];
                this.mInitialBearing = fArr[1];
            }
            f = this.mDistance;
        }
        return f;
    }

    public float bearingTo(Location location) {
        float f;
        synchronized (this.mResults) {
            double d = this.mLatitude;
            if (d != this.mLat1 || this.mLongitude != this.mLon1 || location.mLatitude != this.mLat2 || location.mLongitude != this.mLon2) {
                computeDistanceAndBearing(d, this.mLongitude, location.mLatitude, location.mLongitude, this.mResults);
                this.mLat1 = this.mLatitude;
                this.mLon1 = this.mLongitude;
                this.mLat2 = location.mLatitude;
                this.mLon2 = location.mLongitude;
                float[] fArr = this.mResults;
                this.mDistance = fArr[0];
                this.mInitialBearing = fArr[1];
            }
            f = this.mInitialBearing;
        }
        return f;
    }

    public String getProvider() {
        return this.mProvider;
    }

    public void setProvider(String str) {
        this.mProvider = str;
    }

    public long getTime() {
        return this.mTime;
    }

    public void setTime(long j) {
        this.mTime = j;
    }

    public long getElapsedRealtimeNanos() {
        return this.mElapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long j) {
        this.mElapsedRealtimeNanos = j;
    }

    public double getLatitude() {
        return this.mLatitude;
    }

    public void setLatitude(double d) {
        this.mLatitude = d;
    }

    public double getLongitude() {
        return this.mLongitude;
    }

    public void setLongitude(double d) {
        this.mLongitude = d;
    }

    public boolean hasAltitude() {
        return this.mHasAltitude;
    }

    public double getAltitude() {
        return this.mAltitude;
    }

    public void setAltitude(double d) {
        this.mAltitude = d;
        this.mHasAltitude = true;
    }

    public void removeAltitude() {
        this.mAltitude = 0.0d;
        this.mHasAltitude = false;
    }

    public boolean hasSpeed() {
        return this.mHasSpeed;
    }

    public float getSpeed() {
        return this.mSpeed;
    }

    public void setSpeed(float f) {
        this.mSpeed = f;
        this.mHasSpeed = true;
    }

    public void removeSpeed() {
        this.mSpeed = 0.0f;
        this.mHasSpeed = false;
    }

    public boolean hasBearing() {
        return this.mHasBearing;
    }

    public float getBearing() {
        return this.mBearing;
    }

    public void setBearing(float f) {
        while (f < 0.0f) {
            f += 360.0f;
        }
        while (f >= 360.0f) {
            f -= 360.0f;
        }
        this.mBearing = f;
        this.mHasBearing = true;
    }

    public void removeBearing() {
        this.mBearing = 0.0f;
        this.mHasBearing = false;
    }

    public boolean hasAccuracy() {
        return this.mHasAccuracy;
    }

    public float getAccuracy() {
        return this.mAccuracy;
    }

    public void setAccuracy(float f) {
        this.mAccuracy = f;
        this.mHasAccuracy = true;
    }

    public void removeAccuracy() {
        this.mAccuracy = 0.0f;
        this.mHasAccuracy = false;
    }

    public boolean isComplete() {
        return (this.mProvider == null || !this.mHasAccuracy || this.mTime == 0 || this.mElapsedRealtimeNanos == 0) ? false : true;
    }

    public void makeComplete() {
        if (this.mProvider == null) {
            this.mProvider = "?";
        }
        if (!this.mHasAccuracy) {
            this.mHasAccuracy = true;
            this.mAccuracy = 100.0f;
        }
        if (this.mTime == 0) {
            this.mTime = System.currentTimeMillis();
        }
        if (this.mElapsedRealtimeNanos == 0) {
            this.mElapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        }
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public void setExtras(Bundle bundle) {
        this.mExtras = bundle == null ? null : new Bundle(bundle);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Location[");
        sb.append(this.mProvider);
        sb.append(String.format(" %.6f,%.6f", Double.valueOf(this.mLatitude), Double.valueOf(this.mLongitude)));
        if (this.mHasAccuracy) {
            sb.append(String.format(" acc=%.0f", Float.valueOf(this.mAccuracy)));
        } else {
            sb.append(" acc=???");
        }
        if (this.mTime == 0) {
            sb.append(" t=?!?");
        }
        if (this.mElapsedRealtimeNanos == 0) {
            sb.append(" et=?!?");
        } else {
            sb.append(" et=");
            TimeUtils.formatDuration(this.mElapsedRealtimeNanos / 1000000, sb);
        }
        if (this.mHasAltitude) {
            sb.append(" alt=").append(this.mAltitude);
        }
        if (this.mHasSpeed) {
            sb.append(" vel=").append(this.mSpeed);
        }
        if (this.mHasBearing) {
            sb.append(" bear=").append(this.mBearing);
        }
        if (this.mIsFromMockProvider) {
            sb.append(" mock");
        }
        if (this.mExtras != null) {
            sb.append(" {").append(this.mExtras).append('}');
        }
        sb.append(']');
        return sb.toString();
    }

    public void dump(Printer printer, String str) {
        printer.println(str + toString());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mProvider);
        parcel.writeLong(this.mTime);
        parcel.writeLong(this.mElapsedRealtimeNanos);
        parcel.writeDouble(this.mLatitude);
        parcel.writeDouble(this.mLongitude);
        parcel.writeInt(this.mHasAltitude ? 1 : 0);
        parcel.writeDouble(this.mAltitude);
        parcel.writeInt(this.mHasSpeed ? 1 : 0);
        parcel.writeFloat(this.mSpeed);
        parcel.writeInt(this.mHasBearing ? 1 : 0);
        parcel.writeFloat(this.mBearing);
        parcel.writeInt(this.mHasAccuracy ? 1 : 0);
        parcel.writeFloat(this.mAccuracy);
        parcel.writeBundle(this.mExtras);
        parcel.writeInt(this.mIsFromMockProvider ? 1 : 0);
    }

    public Location getExtraLocation(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        Parcelable parcelable = bundle.getParcelable(str);
        if (parcelable instanceof Location) {
            return (Location) parcelable;
        }
        return null;
    }

    public void setExtraLocation(String str, Location location) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putParcelable(str, location);
    }

    public boolean isFromMockProvider() {
        return this.mIsFromMockProvider;
    }

    public void setIsFromMockProvider(boolean z) {
        this.mIsFromMockProvider = z;
    }
}
