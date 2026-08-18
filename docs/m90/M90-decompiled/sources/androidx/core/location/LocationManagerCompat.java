package androidx.core.location;

import android.location.LocationManager;
import android.os.Build;

/* JADX INFO: loaded from: classes2.dex */
public final class LocationManagerCompat {
    public static boolean isLocationEnabled(LocationManager locationManager) {
        if (Build.VERSION.SDK_INT >= 28) {
            return locationManager.isLocationEnabled();
        }
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled("gps");
    }

    private LocationManagerCompat() {
    }
}
