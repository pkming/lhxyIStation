package com.amap.api.col.p0003sl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.autonavi.amap.mapcore.interfaces.IAMap;
import com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate;

/* JADX INFO: compiled from: MapFragmentDelegateImp.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ab implements IMapFragmentDelegate {
    public static volatile Context a;
    private static String f;
    public int b = 0;
    boolean c = true;
    private IAMap d;
    private int e;
    private AMapOptions g;

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final boolean isReady() throws RemoteException {
        return false;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void onCreate(Bundle bundle) throws RemoteException {
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void onDestroyView() throws RemoteException {
    }

    public ab(int i) {
        this.e = 0;
        this.e = i % 3;
        b();
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void setContext(Context context) {
        a(context);
    }

    private static void a(Context context) {
        if (context != null) {
            a = context.getApplicationContext();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void setOptions(AMapOptions aMapOptions) {
        this.g = aMapOptions;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final IAMap getMap() throws RemoteException {
        if (this.d == null) {
            if (a == null) {
                Log.w("MapFragmentDelegateImp", "Context 为 null 请在地图调用之前 使用 MapsInitializer.initialize(Context paramContext) 来设置Context");
                return null;
            }
            int i = a.getResources().getDisplayMetrics().densityDpi;
            if (i <= 120) {
                w.a = 0.5f;
            } else if (i <= 160) {
                w.a = 0.8f;
            } else if (i <= 240) {
                w.a = 0.87f;
            } else if (i <= 320) {
                w.a = 1.0f;
            } else if (i <= 480) {
                w.a = 1.5f;
            } else if (i <= 640) {
                w.a = 1.8f;
            } else {
                w.a = 0.9f;
            }
            int i2 = this.e;
            if (i2 == 0) {
                this.d = new n(a, this.c).a();
            } else if (i2 == 1) {
                this.d = new o(a, this.c).a();
            } else {
                this.d = new m(a).a();
            }
        }
        return this.d;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void onInflate(Activity activity, AMapOptions aMapOptions, Bundle bundle) throws RemoteException {
        setContext(activity.getApplicationContext());
        this.g = aMapOptions;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) throws RemoteException {
        byte[] byteArray;
        if (a == null && layoutInflater != null) {
            setContext(layoutInflater.getContext().getApplicationContext());
        }
        try {
            IAMap map = getMap();
            this.d = map;
            map.setVisibilityEx(this.b);
            if (this.g == null && bundle != null && (byteArray = bundle.getByteArray("MAP_OPTIONS")) != null) {
                Parcel parcelObtain = Parcel.obtain();
                parcelObtain.unmarshall(byteArray, 0, byteArray.length);
                parcelObtain.setDataPosition(0);
                this.g = AMapOptions.CREATOR.createFromParcel(parcelObtain);
            }
            a(this.g);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return this.d.getView();
    }

    private void a(AMapOptions aMapOptions) throws RemoteException {
        if (aMapOptions == null || this.d == null) {
            return;
        }
        CameraPosition camera = aMapOptions.getCamera();
        if (camera != null) {
            this.d.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
        }
        UiSettings aMapUiSettings = this.d.getAMapUiSettings();
        aMapUiSettings.setRotateGesturesEnabled(aMapOptions.getRotateGesturesEnabled());
        aMapUiSettings.setScrollGesturesEnabled(aMapOptions.getScrollGesturesEnabled());
        aMapUiSettings.setTiltGesturesEnabled(aMapOptions.getTiltGesturesEnabled());
        aMapUiSettings.setZoomControlsEnabled(aMapOptions.getZoomControlsEnabled());
        aMapUiSettings.setZoomGesturesEnabled(aMapOptions.getZoomGesturesEnabled());
        aMapUiSettings.setCompassEnabled(aMapOptions.getCompassEnabled());
        aMapUiSettings.setScaleControlsEnabled(aMapOptions.getScaleControlsEnabled());
        aMapUiSettings.setLogoPosition(aMapOptions.getLogoPosition());
        this.d.setMapType(aMapOptions.getMapType());
        this.d.setZOrderOnTop(aMapOptions.getZOrderOnTop());
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void onResume() throws RemoteException {
        IAMap iAMap = this.d;
        if (iAMap != null) {
            iAMap.onActivityResume();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void onPause() throws RemoteException {
        IAMap iAMap = this.d;
        if (iAMap != null) {
            iAMap.onActivityPause();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void onDestroy() throws RemoteException {
        a();
        IAMap iAMap = this.d;
        if (iAMap != null) {
            iAMap.clear();
            this.d.destroy();
            this.d = null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void onLowMemory() throws RemoteException {
        Log.d("onLowMemory", "onLowMemory run");
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void onSaveInstanceState(Bundle bundle) throws RemoteException {
        if (this.d != null) {
            if (this.g == null) {
                this.g = new AMapOptions();
            }
            try {
                Parcel parcelObtain = Parcel.obtain();
                AMapOptions aMapOptionsCamera = this.g.camera(getMap().getCameraPosition());
                this.g = aMapOptionsCamera;
                aMapOptionsCamera.writeToParcel(parcelObtain, 0);
                bundle.putByteArray("MAP_OPTIONS", parcelObtain.marshall());
            } catch (Throwable unused) {
            }
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void setVisibility(int i) {
        this.b = i;
        IAMap iAMap = this.d;
        if (iAMap != null) {
            iAMap.setVisibilityEx(i);
        }
    }

    private static void a() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            for (int i = 0; i < stackTrace.length; i++) {
                if (stackTrace[i].getClassName() != null && stackTrace[i].getClassName().endsWith("TextureMapView")) {
                    z2 = true;
                }
                if (stackTrace[i].getClassName() != null && stackTrace[i].getClassName().endsWith("Fragment")) {
                    z = true;
                }
                if ("OnDestroyView".equalsIgnoreCase(stackTrace[i].getMethodName())) {
                    z3 = true;
                }
            }
            if (z && z2 && !z3) {
                c();
            }
        } catch (Throwable unused) {
        }
    }

    private static void b() {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 80; i++) {
                sb.append("=");
            }
            f = sb.toString();
        } catch (Throwable unused) {
        }
    }

    private static void c() {
        Log.i("errorLog", f);
        Log.i("errorLog", "OnDestroy 方法需要在OnDestroyView中调用");
        Log.i("errorLog", f);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate
    public final void loadWorldVectorMap(boolean z) {
        this.c = z;
        IAMap iAMap = this.d;
        if (iAMap != null) {
            iAMap.loadWorldVectorMap(z);
        }
    }
}
