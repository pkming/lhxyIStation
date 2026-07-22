package android.location;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes.dex */
public final class GpsStatus {
    public static final int GPS_EVENT_FIRST_FIX = 3;
    public static final int GPS_EVENT_SATELLITE_STATUS = 4;
    public static final int GPS_EVENT_STARTED = 1;
    public static final int GPS_EVENT_STOPPED = 2;
    private static final int NUM_SATELLITES = 255;
    private int mTimeToFirstFix;
    private GpsSatellite[] mSatellites = new GpsSatellite[255];
    private Iterable<GpsSatellite> mSatelliteList = new Iterable<GpsSatellite>() { // from class: android.location.GpsStatus.1
        @Override // java.lang.Iterable
        public Iterator<GpsSatellite> iterator() {
            GpsStatus gpsStatus = GpsStatus.this;
            return gpsStatus.new SatelliteIterator(gpsStatus.mSatellites);
        }
    };

    public interface Listener {
        void onGpsStatusChanged(int i);
    }

    public interface NmeaListener {
        void onNmeaReceived(long j, String str);
    }

    public int getMaxSatellites() {
        return 255;
    }

    private final class SatelliteIterator implements Iterator<GpsSatellite> {
        int mIndex = 0;
        private GpsSatellite[] mSatellites;

        SatelliteIterator(GpsSatellite[] gpsSatelliteArr) {
            this.mSatellites = gpsSatelliteArr;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            int i = this.mIndex;
            while (true) {
                GpsSatellite[] gpsSatelliteArr = this.mSatellites;
                if (i >= gpsSatelliteArr.length) {
                    return false;
                }
                if (gpsSatelliteArr[i].mValid) {
                    return true;
                }
                i++;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public GpsSatellite next() {
            GpsSatellite gpsSatellite;
            do {
                int i = this.mIndex;
                GpsSatellite[] gpsSatelliteArr = this.mSatellites;
                if (i < gpsSatelliteArr.length) {
                    this.mIndex = i + 1;
                    gpsSatellite = gpsSatelliteArr[i];
                } else {
                    throw new NoSuchElementException();
                }
            } while (!gpsSatellite.mValid);
            return gpsSatellite;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    GpsStatus() {
        int i = 0;
        while (true) {
            GpsSatellite[] gpsSatelliteArr = this.mSatellites;
            if (i >= gpsSatelliteArr.length) {
                return;
            }
            int i2 = i + 1;
            gpsSatelliteArr[i] = new GpsSatellite(i2);
            i = i2;
        }
    }

    synchronized void setStatus(int i, int[] iArr, float[] fArr, float[] fArr2, float[] fArr3, int i2, int i3, int i4) {
        int i5 = 0;
        while (true) {
            GpsSatellite[] gpsSatelliteArr = this.mSatellites;
            if (i5 >= gpsSatelliteArr.length) {
                break;
            }
            gpsSatelliteArr[i5].mValid = false;
            i5++;
        }
        for (int i6 = 0; i6 < i; i6++) {
            boolean z = true;
            int i7 = iArr[i6] - 1;
            int i8 = 1 << i7;
            if (i7 >= 0) {
                GpsSatellite[] gpsSatelliteArr2 = this.mSatellites;
                if (i7 < gpsSatelliteArr2.length) {
                    GpsSatellite gpsSatellite = gpsSatelliteArr2[i7];
                    gpsSatellite.mValid = true;
                    gpsSatellite.mSnr = fArr[i6];
                    gpsSatellite.mElevation = fArr2[i6];
                    gpsSatellite.mAzimuth = fArr3[i6];
                    gpsSatellite.mHasEphemeris = (i2 & i8) != 0;
                    gpsSatellite.mHasAlmanac = (i3 & i8) != 0;
                    if ((i8 & i4) == 0) {
                        z = false;
                    }
                    gpsSatellite.mUsedInFix = z;
                }
            }
        }
    }

    void setStatus(GpsStatus gpsStatus) {
        this.mTimeToFirstFix = gpsStatus.getTimeToFirstFix();
        int i = 0;
        while (true) {
            GpsSatellite[] gpsSatelliteArr = this.mSatellites;
            if (i >= gpsSatelliteArr.length) {
                return;
            }
            gpsSatelliteArr[i].setStatus(gpsStatus.mSatellites[i]);
            i++;
        }
    }

    void setTimeToFirstFix(int i) {
        this.mTimeToFirstFix = i;
    }

    public int getTimeToFirstFix() {
        return this.mTimeToFirstFix;
    }

    public Iterable<GpsSatellite> getSatellites() {
        return this.mSatelliteList;
    }
}
