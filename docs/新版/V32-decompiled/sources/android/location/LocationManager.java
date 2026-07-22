package android.location;

import android.app.PendingIntent;
import android.content.Context;
import android.location.GpsStatus;
import android.location.IGpsStatusListener;
import android.location.ILocationListener;
import android.net.LinkQualityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.location.ProviderProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LocationManager {
    public static final String EXTRA_GPS_ENABLED = "enabled";
    public static final String FUSED_PROVIDER = "fused";
    public static final String GPS_ENABLED_CHANGE_ACTION = "android.location.GPS_ENABLED_CHANGE";
    public static final String GPS_FIX_CHANGE_ACTION = "android.location.GPS_FIX_CHANGE";
    public static final String GPS_PROVIDER = "gps";
    public static final String HIGH_POWER_REQUEST_CHANGE_ACTION = "android.location.HIGH_POWER_REQUEST_CHANGE";
    public static final String KEY_LOCATION_CHANGED = "location";
    public static final String KEY_PROVIDER_ENABLED = "providerEnabled";
    public static final String KEY_PROXIMITY_ENTERING = "entering";
    public static final String KEY_STATUS_CHANGED = "status";
    public static final String MODE_CHANGED_ACTION = "android.location.MODE_CHANGED";
    public static final String NETWORK_PROVIDER = "network";
    public static final String PASSIVE_PROVIDER = "passive";
    public static final String PROVIDERS_CHANGED_ACTION = "android.location.PROVIDERS_CHANGED";
    private static final String TAG = "LocationManager";
    private final Context mContext;
    private final ILocationManager mService;
    private final HashMap<GpsStatus.Listener, GpsStatusListenerTransport> mGpsStatusListeners = new HashMap<>();
    private final HashMap<GpsStatus.NmeaListener, GpsStatusListenerTransport> mNmeaListeners = new HashMap<>();
    private final GpsStatus mGpsStatus = new GpsStatus();
    private HashMap<LocationListener, ListenerTransport> mListeners = new HashMap<>();

    private class ListenerTransport extends ILocationListener.Stub {
        private static final int TYPE_LOCATION_CHANGED = 1;
        private static final int TYPE_PROVIDER_DISABLED = 4;
        private static final int TYPE_PROVIDER_ENABLED = 3;
        private static final int TYPE_STATUS_CHANGED = 2;
        private LocationListener mListener;
        private final Handler mListenerHandler;

        ListenerTransport(LocationListener locationListener, Looper looper) {
            this.mListener = locationListener;
            if (looper == null) {
                this.mListenerHandler = new Handler() { // from class: android.location.LocationManager.ListenerTransport.1
                    @Override // android.os.Handler
                    public void handleMessage(Message message) {
                        ListenerTransport.this._handleMessage(message);
                    }
                };
            } else {
                this.mListenerHandler = new Handler(looper) { // from class: android.location.LocationManager.ListenerTransport.2
                    @Override // android.os.Handler
                    public void handleMessage(Message message) {
                        ListenerTransport.this._handleMessage(message);
                    }
                };
            }
        }

        @Override // android.location.ILocationListener
        public void onLocationChanged(Location location) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 1;
            messageObtain.obj = location;
            this.mListenerHandler.sendMessage(messageObtain);
        }

        @Override // android.location.ILocationListener
        public void onStatusChanged(String str, int i, Bundle bundle) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 2;
            Bundle bundle2 = new Bundle();
            bundle2.putString("provider", str);
            bundle2.putInt("status", i);
            if (bundle != null) {
                bundle2.putBundle("extras", bundle);
            }
            messageObtain.obj = bundle2;
            this.mListenerHandler.sendMessage(messageObtain);
        }

        @Override // android.location.ILocationListener
        public void onProviderEnabled(String str) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 3;
            messageObtain.obj = str;
            this.mListenerHandler.sendMessage(messageObtain);
        }

        @Override // android.location.ILocationListener
        public void onProviderDisabled(String str) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 4;
            messageObtain.obj = str;
            this.mListenerHandler.sendMessage(messageObtain);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void _handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                this.mListener.onLocationChanged(new Location((Location) message.obj));
            } else if (i == 2) {
                Bundle bundle = (Bundle) message.obj;
                this.mListener.onStatusChanged(bundle.getString("provider"), bundle.getInt("status"), bundle.getBundle("extras"));
            } else if (i == 3) {
                this.mListener.onProviderEnabled((String) message.obj);
            } else if (i == 4) {
                this.mListener.onProviderDisabled((String) message.obj);
            }
            try {
                LocationManager.this.mService.locationCallbackFinished(this);
            } catch (RemoteException e) {
                Log.e(LocationManager.TAG, "locationCallbackFinished: RemoteException", e);
            }
        }
    }

    public LocationManager(Context context, ILocationManager iLocationManager) {
        this.mService = iLocationManager;
        this.mContext = context;
    }

    private LocationProvider createProvider(String str, ProviderProperties providerProperties) {
        return new LocationProvider(str, providerProperties);
    }

    public List<String> getAllProviders() {
        try {
            return this.mService.getAllProviders();
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
            return null;
        }
    }

    public List<String> getProviders(boolean z) {
        try {
            return this.mService.getProviders(null, z);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
            return null;
        }
    }

    public LocationProvider getProvider(String str) {
        checkProvider(str);
        try {
            ProviderProperties providerProperties = this.mService.getProviderProperties(str);
            if (providerProperties == null) {
                return null;
            }
            return createProvider(str, providerProperties);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
            return null;
        }
    }

    public List<String> getProviders(Criteria criteria, boolean z) {
        checkCriteria(criteria);
        try {
            return this.mService.getProviders(criteria, z);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
            return null;
        }
    }

    public String getBestProvider(Criteria criteria, boolean z) {
        checkCriteria(criteria);
        try {
            return this.mService.getBestProvider(criteria, z);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
            return null;
        }
    }

    public void requestLocationUpdates(String str, long j, float f, LocationListener locationListener) {
        checkProvider(str);
        checkListener(locationListener);
        requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(str, j, f, false), locationListener, (Looper) null, (PendingIntent) null);
    }

    public void requestLocationUpdates(String str, long j, float f, LocationListener locationListener, Looper looper) {
        checkProvider(str);
        checkListener(locationListener);
        requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(str, j, f, false), locationListener, looper, (PendingIntent) null);
    }

    public void requestLocationUpdates(long j, float f, Criteria criteria, LocationListener locationListener, Looper looper) {
        checkCriteria(criteria);
        checkListener(locationListener);
        requestLocationUpdates(LocationRequest.createFromDeprecatedCriteria(criteria, j, f, false), locationListener, looper, (PendingIntent) null);
    }

    public void requestLocationUpdates(String str, long j, float f, PendingIntent pendingIntent) {
        checkProvider(str);
        checkPendingIntent(pendingIntent);
        requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(str, j, f, false), (LocationListener) null, (Looper) null, pendingIntent);
    }

    public void requestLocationUpdates(long j, float f, Criteria criteria, PendingIntent pendingIntent) {
        checkCriteria(criteria);
        checkPendingIntent(pendingIntent);
        requestLocationUpdates(LocationRequest.createFromDeprecatedCriteria(criteria, j, f, false), (LocationListener) null, (Looper) null, pendingIntent);
    }

    public void requestSingleUpdate(String str, LocationListener locationListener, Looper looper) {
        checkProvider(str);
        checkListener(locationListener);
        requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(str, 0L, 0.0f, true), locationListener, looper, (PendingIntent) null);
    }

    public void requestSingleUpdate(Criteria criteria, LocationListener locationListener, Looper looper) {
        checkCriteria(criteria);
        checkListener(locationListener);
        requestLocationUpdates(LocationRequest.createFromDeprecatedCriteria(criteria, 0L, 0.0f, true), locationListener, looper, (PendingIntent) null);
    }

    public void requestSingleUpdate(String str, PendingIntent pendingIntent) {
        checkProvider(str);
        checkPendingIntent(pendingIntent);
        requestLocationUpdates(LocationRequest.createFromDeprecatedProvider(str, 0L, 0.0f, true), (LocationListener) null, (Looper) null, pendingIntent);
    }

    public void requestSingleUpdate(Criteria criteria, PendingIntent pendingIntent) {
        checkCriteria(criteria);
        checkPendingIntent(pendingIntent);
        requestLocationUpdates(LocationRequest.createFromDeprecatedCriteria(criteria, 0L, 0.0f, true), (LocationListener) null, (Looper) null, pendingIntent);
    }

    public void requestLocationUpdates(LocationRequest locationRequest, LocationListener locationListener, Looper looper) {
        checkListener(locationListener);
        requestLocationUpdates(locationRequest, locationListener, looper, (PendingIntent) null);
    }

    public void requestLocationUpdates(LocationRequest locationRequest, PendingIntent pendingIntent) {
        checkPendingIntent(pendingIntent);
        requestLocationUpdates(locationRequest, (LocationListener) null, (Looper) null, pendingIntent);
    }

    private ListenerTransport wrapListener(LocationListener locationListener, Looper looper) {
        ListenerTransport listenerTransport;
        if (locationListener == null) {
            return null;
        }
        synchronized (this.mListeners) {
            listenerTransport = this.mListeners.get(locationListener);
            if (listenerTransport == null) {
                listenerTransport = new ListenerTransport(locationListener, looper);
            }
            this.mListeners.put(locationListener, listenerTransport);
        }
        return listenerTransport;
    }

    private void requestLocationUpdates(LocationRequest locationRequest, LocationListener locationListener, Looper looper, PendingIntent pendingIntent) {
        String packageName = this.mContext.getPackageName();
        try {
            this.mService.requestLocationUpdates(locationRequest, wrapListener(locationListener, looper), pendingIntent, packageName);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void removeUpdates(LocationListener locationListener) {
        ListenerTransport listenerTransportRemove;
        checkListener(locationListener);
        String packageName = this.mContext.getPackageName();
        synchronized (this.mListeners) {
            listenerTransportRemove = this.mListeners.remove(locationListener);
        }
        if (listenerTransportRemove == null) {
            return;
        }
        try {
            this.mService.removeUpdates(listenerTransportRemove, null, packageName);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void removeUpdates(PendingIntent pendingIntent) {
        checkPendingIntent(pendingIntent);
        try {
            this.mService.removeUpdates(null, pendingIntent, this.mContext.getPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void addProximityAlert(double d, double d2, float f, long j, PendingIntent pendingIntent) {
        checkPendingIntent(pendingIntent);
        if (j < 0) {
            j = LinkQualityInfo.UNKNOWN_LONG;
        }
        Geofence geofenceCreateCircle = Geofence.createCircle(d, d2, f);
        try {
            this.mService.requestGeofence(new LocationRequest().setExpireIn(j), geofenceCreateCircle, pendingIntent, this.mContext.getPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void addGeofence(LocationRequest locationRequest, Geofence geofence, PendingIntent pendingIntent) {
        checkPendingIntent(pendingIntent);
        checkGeofence(geofence);
        try {
            this.mService.requestGeofence(locationRequest, geofence, pendingIntent, this.mContext.getPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void removeProximityAlert(PendingIntent pendingIntent) {
        checkPendingIntent(pendingIntent);
        try {
            this.mService.removeGeofence(null, pendingIntent, this.mContext.getPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void removeGeofence(Geofence geofence, PendingIntent pendingIntent) {
        checkPendingIntent(pendingIntent);
        checkGeofence(geofence);
        try {
            this.mService.removeGeofence(geofence, pendingIntent, this.mContext.getPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void removeAllGeofences(PendingIntent pendingIntent) {
        checkPendingIntent(pendingIntent);
        try {
            this.mService.removeGeofence(null, pendingIntent, this.mContext.getPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public boolean isProviderEnabled(String str) {
        checkProvider(str);
        try {
            return this.mService.isProviderEnabled(str);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
            return false;
        }
    }

    public Location getLastLocation() {
        try {
            return this.mService.getLastLocation(null, this.mContext.getPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
            return null;
        }
    }

    public Location getLastKnownLocation(String str) {
        checkProvider(str);
        String packageName = this.mContext.getPackageName();
        try {
            return this.mService.getLastLocation(LocationRequest.createFromDeprecatedProvider(str, 0L, 0.0f, true), packageName);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
            return null;
        }
    }

    public void addTestProvider(String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, int i, int i2) {
        ProviderProperties providerProperties = new ProviderProperties(z, z2, z3, z4, z5, z6, z7, i, i2);
        if (str.matches(LocationProvider.BAD_CHARS_REGEX)) {
            throw new IllegalArgumentException("provider name contains illegal character: " + str);
        }
        try {
            this.mService.addTestProvider(str, providerProperties);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void removeTestProvider(String str) {
        try {
            this.mService.removeTestProvider(str);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void setTestProviderLocation(String str, Location location) {
        if (!location.isComplete()) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Incomplete location object, missing timestamp or accuracy? " + location);
            if (this.mContext.getApplicationInfo().targetSdkVersion <= 16) {
                Log.w(TAG, illegalArgumentException);
                location.makeComplete();
            } else {
                throw illegalArgumentException;
            }
        }
        try {
            this.mService.setTestProviderLocation(str, location);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void clearTestProviderLocation(String str) {
        try {
            this.mService.clearTestProviderLocation(str);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void setTestProviderEnabled(String str, boolean z) {
        try {
            this.mService.setTestProviderEnabled(str, z);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void clearTestProviderEnabled(String str) {
        try {
            this.mService.clearTestProviderEnabled(str);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void setTestProviderStatus(String str, int i, Bundle bundle, long j) {
        try {
            this.mService.setTestProviderStatus(str, i, bundle, j);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    public void clearTestProviderStatus(String str) {
        try {
            this.mService.clearTestProviderStatus(str);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    private class GpsStatusListenerTransport extends IGpsStatusListener.Stub {
        private static final int NMEA_RECEIVED = 1000;
        private final Handler mGpsHandler;
        private final GpsStatus.Listener mListener;
        private ArrayList<Nmea> mNmeaBuffer;
        private final GpsStatus.NmeaListener mNmeaListener;

        private class Nmea {
            String mNmea;
            long mTimestamp;

            Nmea(long j, String str) {
                this.mTimestamp = j;
                this.mNmea = str;
            }
        }

        GpsStatusListenerTransport(GpsStatus.Listener listener) {
            this.mGpsHandler = new Handler() { // from class: android.location.LocationManager.GpsStatusListenerTransport.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    if (message.what != 1000) {
                        synchronized (LocationManager.this.mGpsStatus) {
                            GpsStatusListenerTransport.this.mListener.onGpsStatusChanged(message.what);
                        }
                        return;
                    }
                    synchronized (GpsStatusListenerTransport.this.mNmeaBuffer) {
                        int size = GpsStatusListenerTransport.this.mNmeaBuffer.size();
                        for (int i = 0; i < size; i++) {
                            Nmea nmea = (Nmea) GpsStatusListenerTransport.this.mNmeaBuffer.get(i);
                            GpsStatusListenerTransport.this.mNmeaListener.onNmeaReceived(nmea.mTimestamp, nmea.mNmea);
                        }
                        GpsStatusListenerTransport.this.mNmeaBuffer.clear();
                    }
                }
            };
            this.mListener = listener;
            this.mNmeaListener = null;
        }

        GpsStatusListenerTransport(GpsStatus.NmeaListener nmeaListener) {
            this.mGpsHandler = new Handler() { // from class: android.location.LocationManager.GpsStatusListenerTransport.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    if (message.what != 1000) {
                        synchronized (LocationManager.this.mGpsStatus) {
                            GpsStatusListenerTransport.this.mListener.onGpsStatusChanged(message.what);
                        }
                        return;
                    }
                    synchronized (GpsStatusListenerTransport.this.mNmeaBuffer) {
                        int size = GpsStatusListenerTransport.this.mNmeaBuffer.size();
                        for (int i = 0; i < size; i++) {
                            Nmea nmea = (Nmea) GpsStatusListenerTransport.this.mNmeaBuffer.get(i);
                            GpsStatusListenerTransport.this.mNmeaListener.onNmeaReceived(nmea.mTimestamp, nmea.mNmea);
                        }
                        GpsStatusListenerTransport.this.mNmeaBuffer.clear();
                    }
                }
            };
            this.mNmeaListener = nmeaListener;
            this.mListener = null;
            this.mNmeaBuffer = new ArrayList<>();
        }

        @Override // android.location.IGpsStatusListener
        public void onGpsStarted() {
            if (this.mListener != null) {
                Message messageObtain = Message.obtain();
                messageObtain.what = 1;
                this.mGpsHandler.sendMessage(messageObtain);
            }
        }

        @Override // android.location.IGpsStatusListener
        public void onGpsStopped() {
            if (this.mListener != null) {
                Message messageObtain = Message.obtain();
                messageObtain.what = 2;
                this.mGpsHandler.sendMessage(messageObtain);
            }
        }

        @Override // android.location.IGpsStatusListener
        public void onFirstFix(int i) {
            if (this.mListener != null) {
                LocationManager.this.mGpsStatus.setTimeToFirstFix(i);
                Message messageObtain = Message.obtain();
                messageObtain.what = 3;
                this.mGpsHandler.sendMessage(messageObtain);
            }
        }

        @Override // android.location.IGpsStatusListener
        public void onSvStatusChanged(int i, int[] iArr, float[] fArr, float[] fArr2, float[] fArr3, int i2, int i3, int i4) {
            if (this.mListener != null) {
                LocationManager.this.mGpsStatus.setStatus(i, iArr, fArr, fArr2, fArr3, i2, i3, i4);
                Message messageObtain = Message.obtain();
                messageObtain.what = 4;
                this.mGpsHandler.removeMessages(4);
                this.mGpsHandler.sendMessage(messageObtain);
            }
        }

        @Override // android.location.IGpsStatusListener
        public void onNmeaReceived(long j, String str) {
            if (this.mNmeaListener != null) {
                synchronized (this.mNmeaBuffer) {
                    this.mNmeaBuffer.add(new Nmea(j, str));
                }
                Message messageObtain = Message.obtain();
                messageObtain.what = 1000;
                this.mGpsHandler.removeMessages(1000);
                this.mGpsHandler.sendMessage(messageObtain);
            }
        }
    }

    public boolean addGpsStatusListener(GpsStatus.Listener listener) {
        if (this.mGpsStatusListeners.get(listener) != null) {
            return true;
        }
        try {
            GpsStatusListenerTransport gpsStatusListenerTransport = new GpsStatusListenerTransport(listener);
            boolean zAddGpsStatusListener = this.mService.addGpsStatusListener(gpsStatusListenerTransport, this.mContext.getPackageName());
            if (!zAddGpsStatusListener) {
                return zAddGpsStatusListener;
            }
            this.mGpsStatusListeners.put(listener, gpsStatusListenerTransport);
            return zAddGpsStatusListener;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in registerGpsStatusListener: ", e);
            return false;
        }
    }

    public void removeGpsStatusListener(GpsStatus.Listener listener) {
        try {
            GpsStatusListenerTransport gpsStatusListenerTransportRemove = this.mGpsStatusListeners.remove(listener);
            if (gpsStatusListenerTransportRemove != null) {
                this.mService.removeGpsStatusListener(gpsStatusListenerTransportRemove);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in unregisterGpsStatusListener: ", e);
        }
    }

    public boolean addNmeaListener(GpsStatus.NmeaListener nmeaListener) {
        if (this.mNmeaListeners.get(nmeaListener) != null) {
            return true;
        }
        try {
            GpsStatusListenerTransport gpsStatusListenerTransport = new GpsStatusListenerTransport(nmeaListener);
            boolean zAddGpsStatusListener = this.mService.addGpsStatusListener(gpsStatusListenerTransport, this.mContext.getPackageName());
            if (!zAddGpsStatusListener) {
                return zAddGpsStatusListener;
            }
            this.mNmeaListeners.put(nmeaListener, gpsStatusListenerTransport);
            return zAddGpsStatusListener;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in registerGpsStatusListener: ", e);
            return false;
        }
    }

    public void removeNmeaListener(GpsStatus.NmeaListener nmeaListener) {
        try {
            GpsStatusListenerTransport gpsStatusListenerTransportRemove = this.mNmeaListeners.remove(nmeaListener);
            if (gpsStatusListenerTransportRemove != null) {
                this.mService.removeGpsStatusListener(gpsStatusListenerTransportRemove);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in unregisterGpsStatusListener: ", e);
        }
    }

    public GpsStatus getGpsStatus(GpsStatus gpsStatus) {
        if (gpsStatus == null) {
            gpsStatus = new GpsStatus();
        }
        gpsStatus.setStatus(this.mGpsStatus);
        return gpsStatus;
    }

    public boolean sendExtraCommand(String str, String str2, Bundle bundle) {
        try {
            return this.mService.sendExtraCommand(str, str2, bundle);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in sendExtraCommand: ", e);
            return false;
        }
    }

    public boolean sendNiResponse(int i, int i2) {
        try {
            return this.mService.sendNiResponse(i, i2);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in sendNiResponse: ", e);
            return false;
        }
    }

    private static void checkProvider(String str) {
        if (str == null) {
            throw new IllegalArgumentException("invalid provider: " + str);
        }
    }

    private static void checkCriteria(Criteria criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("invalid criteria: " + criteria);
        }
    }

    private static void checkListener(LocationListener locationListener) {
        if (locationListener == null) {
            throw new IllegalArgumentException("invalid listener: " + locationListener);
        }
    }

    private void checkPendingIntent(PendingIntent pendingIntent) {
        if (pendingIntent == null) {
            throw new IllegalArgumentException("invalid pending intent: " + pendingIntent);
        }
        if (pendingIntent.isTargetedToPackage()) {
            return;
        }
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("pending intent msut be targeted to package");
        if (this.mContext.getApplicationInfo().targetSdkVersion > 16) {
            throw illegalArgumentException;
        }
        Log.w(TAG, illegalArgumentException);
    }

    private static void checkGeofence(Geofence geofence) {
        if (geofence == null) {
            throw new IllegalArgumentException("invalid geofence: " + geofence);
        }
    }
}
