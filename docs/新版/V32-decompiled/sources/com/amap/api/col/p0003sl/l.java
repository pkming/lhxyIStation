package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.amap.api.col.p0003sl.cu;
import com.amap.api.col.p0003sl.ef;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.col.p0003sl.k;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.InfoWindowAnimationManager;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.interfaces.IGlOverlayLayer;
import com.amap.api.maps.model.AMapCameraInfo;
import com.amap.api.maps.model.AMapGestureListener;
import com.amap.api.maps.model.Arc;
import com.amap.api.maps.model.ArcOptions;
import com.amap.api.maps.model.BaseOptions;
import com.amap.api.maps.model.BaseOverlay;
import com.amap.api.maps.model.BuildingOverlay;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.CrossOverlay;
import com.amap.api.maps.model.CrossOverlayOptions;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.GL3DModel;
import com.amap.api.maps.model.GL3DModelOptions;
import com.amap.api.maps.model.GLTFOverlay;
import com.amap.api.maps.model.GLTFOverlayOptions;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.HeatMapGridLayer;
import com.amap.api.maps.model.HeatMapGridLayerOptions;
import com.amap.api.maps.model.HeatMapLayer;
import com.amap.api.maps.model.HeatMapLayerOptions;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.IndoorBuildingInfo;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MVTTileOverlay;
import com.amap.api.maps.model.MVTTileOverlayOptions;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MultiPointItem;
import com.amap.api.maps.model.MultiPointOverlay;
import com.amap.api.maps.model.MultiPointOverlayOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.MyTrafficStyle;
import com.amap.api.maps.model.NavigateArrow;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.RouteOverlay;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.TileProvider;
import com.amap.api.maps.model.amap3dmodeltile.AMap3DModelTileOverlay;
import com.amap.api.maps.model.amap3dmodeltile.AMap3DModelTileOverlayOptions;
import com.amap.api.maps.model.particle.ParticleOverlay;
import com.amap.api.maps.model.particle.ParticleOverlayOptions;
import com.autonavi.amap.api.mapcore.IGLMapState;
import com.autonavi.amap.mapcore.AbstractCameraUpdateMessage;
import com.autonavi.amap.mapcore.DPoint;
import com.autonavi.amap.mapcore.IPoint;
import com.autonavi.amap.mapcore.VirtualEarthProjection;
import com.autonavi.base.ae.gmap.AMapAppRequestParam;
import com.autonavi.base.ae.gmap.GLMapEngine;
import com.autonavi.base.ae.gmap.GLMapRender;
import com.autonavi.base.ae.gmap.GLMapState;
import com.autonavi.base.ae.gmap.MapPoi;
import com.autonavi.base.ae.gmap.bean.NativeTextGenerate;
import com.autonavi.base.ae.gmap.gesture.EAMapPlatformGestureInfo;
import com.autonavi.base.ae.gmap.gloverlay.BaseMapOverlay;
import com.autonavi.base.ae.gmap.gloverlay.CrossVectorOverlay;
import com.autonavi.base.ae.gmap.gloverlay.GLOverlayBundle;
import com.autonavi.base.ae.gmap.gloverlay.GLTextureProperty;
import com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner;
import com.autonavi.base.ae.gmap.listener.AMapWidgetListener;
import com.autonavi.base.ae.gmap.style.StyleItem;
import com.autonavi.base.amap.api.mapcore.BaseOverlayImp;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import com.autonavi.base.amap.api.mapcore.IGLSurfaceView;
import com.autonavi.base.amap.api.mapcore.IProjectionDelegate;
import com.autonavi.base.amap.api.mapcore.IUiSettingsDelegate;
import com.autonavi.base.amap.mapcore.AeUtil;
import com.autonavi.base.amap.mapcore.FPoint;
import com.autonavi.base.amap.mapcore.MapConfig;
import com.autonavi.base.amap.mapcore.Rectangle;
import com.autonavi.base.amap.mapcore.interfaces.IAMapListener;
import com.autonavi.base.amap.mapcore.message.AbstractGestureMapMessage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* JADX INFO: compiled from: AMapDelegateImp.java */
/* JADX INFO: loaded from: classes2.dex */
public final class l implements cu.a, k.a, IAMapDelegate, IAMapListener {
    private boolean A;
    private final IGLSurfaceView B;
    private ei C;
    private final IGlOverlayLayer D;
    private boolean E;
    private int F;
    private AtomicBoolean G;
    private boolean H;
    private boolean I;
    private boolean J;
    private cl K;
    private LocationSource L;
    private boolean M;
    private boolean N;
    private Marker O;
    private GLTFOverlay P;
    private boolean Q;
    private boolean R;
    private boolean S;
    private boolean T;
    private int U;
    private boolean V;
    private boolean W;
    private Rect X;
    private int Y;
    private MyTrafficStyle Z;
    protected boolean a;
    private Lock aA;
    private int aB;
    private int aC;
    private int aD;
    private b aE;
    private cq aF;
    private AMap.OnMultiPointClickListener aG;
    private k aH;
    private long aI;
    private a aJ;
    private a aK;
    private a aL;
    private a aM;
    private a aN;
    private a aO;
    private a aP;
    private a aQ;
    private a aR;
    private a aS;
    private a aT;
    private a aU;
    private a aV;
    private Runnable aW;
    private a aX;
    private com.autonavi.extra.b aY;
    private String aZ;
    private Thread aa;
    private Thread ab;
    private v ac;
    private boolean ad;
    private boolean ae;
    private int af;
    private CustomRenderer ag;
    private int ah;
    private int ai;
    private List<ac> aj;
    private cs ak;
    private cu al;
    private long am;
    private GLMapRender an;
    private z ao;
    private boolean ap;
    private float aq;
    private float ar;
    private float as;
    private boolean at;
    private boolean au;
    private boolean av;
    private volatile boolean aw;
    private volatile boolean ax;
    private boolean ay;
    private boolean az;
    protected MapConfig b;
    private String ba;
    private boolean bb;
    private boolean bc;
    private int bd;
    private EAMapPlatformGestureInfo be;
    private long bf;
    private au bg;
    private IPoint[] bh;
    protected au c;
    Cdo d;
    protected Context e;
    protected GLMapEngine f;
    public int g;
    public int h;
    boolean i;
    protected final Handler j;
    Point k;
    protected String l;
    float[] m;
    float[] n;
    float[] o;
    float[] p;
    String q;
    String r;
    int s;
    private p t;
    private q u;
    private AMapGestureListener v;
    private av w;
    private UiSettings x;
    private IProjectionDelegate y;
    private final ag z;

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final AMapCameraInfo getCamerInfo() {
        return null;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final InfoWindowAnimationManager getInfoWindowAnimationManager() {
        return null;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final boolean isLockMapCameraDegree(int i) {
        return false;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void reloadMap() {
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMaskLayerParams(int i, int i2, int i3, int i4, int i5, long j) {
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setZOrderOnTop(boolean z) throws RemoteException {
    }

    static /* synthetic */ boolean f(l lVar) {
        lVar.av = false;
        return false;
    }

    static /* synthetic */ boolean i(l lVar) {
        lVar.T = false;
        return false;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setMapWidgetListener(AMapWidgetListener aMapWidgetListener) {
        try {
            q qVar = this.u;
            if (qVar != null) {
                qVar.a(AMapWidgetListener.class.hashCode(), aMapWidgetListener);
            }
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(CameraPosition cameraPosition) {
        if (this.b.getMapLanguage().equals("en")) {
            boolean zC = c(cameraPosition);
            if (zC != this.W) {
                this.W = zC;
                b(this.F, zC);
                return;
            }
            return;
        }
        if (this.W) {
            return;
        }
        this.W = true;
        b(this.F, true);
    }

    private boolean c(CameraPosition cameraPosition) {
        if (cameraPosition.zoom < 6.0f) {
            return false;
        }
        if (cameraPosition.isAbroad) {
            return true;
        }
        if (this.b == null) {
            return false;
        }
        try {
            return !dq.a(r3.getGeoRectangle().getClipRect());
        } catch (Throwable th) {
            th.printStackTrace();
            dx.a(th);
            return false;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setVisibilityEx(int i) {
        IGLSurfaceView iGLSurfaceView = this.B;
        if (iGLSurfaceView != null) {
            try {
                iGLSurfaceView.setVisibility(i);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void onActivityPause() {
        this.H = true;
        c(this.F);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void onActivityResume() {
        this.H = false;
        d(this.F);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void queueEvent(Runnable runnable) {
        long id;
        try {
            try {
                id = Thread.currentThread().getId();
            } catch (Throwable th) {
                dx.a(th);
                jw.c(th, "AMapdelegateImp", "queueEvent");
                id = -1;
            }
            if (id != -1 && id == this.am) {
                runnable.run();
            } else if (this.f != null) {
                this.B.queueEvent(runnable);
            }
        } catch (Throwable th2) {
            dx.a(th2);
            jw.c(th2, "AMapdelegateImp", "queueEvent");
        }
    }

    /* JADX INFO: compiled from: AMapDelegateImp.java */
    private static abstract class a implements Runnable {
        boolean b;
        boolean c;
        int d;
        int e;
        int f;
        int g;
        int h;
        byte[] i;
        MyTrafficStyle j;

        private a() {
            this.b = false;
            this.c = false;
            this.g = 0;
            this.h = 0;
        }

        /* synthetic */ a(byte b) {
            this();
        }

        @Override // java.lang.Runnable
        public void run() {
            this.b = false;
        }
    }

    public l(IGLSurfaceView iGLSurfaceView, Context context) {
        this(iGLSurfaceView, context, false);
    }

    public l(IGLSurfaceView iGLSurfaceView, Context context, boolean z) {
        this.t = null;
        this.u = new q();
        byte b2 = 0;
        this.a = false;
        this.A = false;
        this.E = false;
        this.G = new AtomicBoolean(false);
        this.H = false;
        this.b = new MapConfig(true);
        this.I = false;
        this.J = false;
        this.M = false;
        this.N = false;
        this.O = null;
        this.P = null;
        this.Q = false;
        this.R = false;
        this.S = false;
        this.T = false;
        this.U = 0;
        this.V = true;
        this.W = true;
        this.X = new Rect();
        this.Y = 1;
        this.Z = null;
        this.ad = false;
        this.ae = false;
        this.af = 0;
        this.ah = -1;
        this.ai = -1;
        this.aj = new ArrayList();
        this.d = null;
        this.am = -1L;
        this.ap = false;
        this.aq = 0.0f;
        this.ar = 1.0f;
        this.as = 1.0f;
        this.at = true;
        this.au = false;
        this.av = false;
        this.aw = false;
        this.ax = false;
        this.ay = false;
        this.az = false;
        this.aA = new ReentrantLock();
        this.aB = 0;
        this.i = true;
        this.j = new Handler(Looper.getMainLooper()) { // from class: com.amap.api.col.3sl.l.1
            @Override // android.os.Handler
            public final void handleMessage(Message message) {
                int i;
                el elVarF;
                if (message == null || l.this.G.get()) {
                    return;
                }
                try {
                    i = message.what;
                } catch (Throwable th) {
                    jw.c(th, "AMapDelegateImp", "handleMessage");
                    th.printStackTrace();
                    return;
                }
                if (i == 2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Key验证失败：[");
                    if (message.obj != null) {
                        sb.append(message.obj);
                    } else {
                        sb.append(ih.b);
                    }
                    sb.append("]");
                    Log.w("amapsdk", sb.toString());
                    return;
                }
                if (i != 30) {
                    int i2 = 0;
                    switch (i) {
                        case 10:
                            CameraPosition cameraPosition = (CameraPosition) message.obj;
                            try {
                                List listA = l.this.u.a(AMap.OnCameraChangeListener.class.hashCode());
                                if (cameraPosition != null && listA != null && listA.size() > 0) {
                                    synchronized (listA) {
                                        Iterator it = listA.iterator();
                                        while (it.hasNext()) {
                                            ((AMap.OnCameraChangeListener) it.next()).onCameraChange(cameraPosition);
                                        }
                                    }
                                }
                                break;
                            } catch (Throwable th2) {
                                dx.a(th2);
                            }
                            l.this.b.addChangedCounter();
                            return;
                        case 11:
                            try {
                                CameraPosition cameraPosition2 = l.this.getCameraPosition();
                                if (cameraPosition2 != null && l.this.C != null) {
                                    l.this.C.a(cameraPosition2);
                                }
                                l.this.b(cameraPosition2);
                                if (l.this.av) {
                                    l.f(l.this);
                                    if (l.this.D != null) {
                                        l.this.D.setFlingState(false);
                                    }
                                    l.this.b();
                                }
                                if (l.this.T) {
                                    l.this.redrawInfoWindow();
                                    l.i(l.this);
                                }
                                l.this.a(cameraPosition2);
                                return;
                            } catch (Throwable th3) {
                                jw.c(th3, "AMapDelegateImp", "CameraUpdateFinish");
                                dx.a(th3);
                                return;
                            }
                        case 12:
                            if (l.this.C != null) {
                                l.this.C.a(Float.valueOf(l.this.getZoomLevel()));
                                return;
                            }
                            return;
                        case 13:
                            if (l.this.C != null) {
                                l.this.C.h();
                                return;
                            }
                            return;
                        case 14:
                            try {
                                List listA2 = l.this.u.a(AMap.OnMapTouchListener.class.hashCode());
                                if (listA2 == null || listA2.size() <= 0) {
                                    return;
                                }
                                synchronized (listA2) {
                                    Iterator it2 = listA2.iterator();
                                    while (it2.hasNext()) {
                                        ((AMap.OnMapTouchListener) it2.next()).onTouch((MotionEvent) message.obj);
                                    }
                                    break;
                                }
                                return;
                            } catch (Throwable th4) {
                                jw.c(th4, "AMapDelegateImp", "onTouchHandler");
                                th4.printStackTrace();
                                return;
                            }
                        case 15:
                            Bitmap bitmap = (Bitmap) message.obj;
                            int i3 = message.arg1;
                            if (bitmap == null || l.this.C == null) {
                                try {
                                    List listA3 = l.this.u.a(AMap.onMapPrintScreenListener.class.hashCode());
                                    ArrayList arrayList = listA3 != null ? new ArrayList(listA3) : null;
                                    List listA4 = l.this.u.a(AMap.OnMapScreenShotListener.class.hashCode());
                                    ArrayList arrayList2 = listA4 != null ? new ArrayList(listA4) : null;
                                    l.this.u.a(Integer.valueOf(AMap.onMapPrintScreenListener.class.hashCode()));
                                    l.this.u.a(Integer.valueOf(AMap.OnMapScreenShotListener.class.hashCode()));
                                    if (arrayList != null && arrayList.size() > 0) {
                                        synchronized (arrayList) {
                                            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                                                ((AMap.onMapPrintScreenListener) arrayList.get(i4)).onMapPrint(null);
                                                break;
                                            }
                                            break;
                                        }
                                    }
                                    if (arrayList2 == null || arrayList2.size() <= 0) {
                                        return;
                                    }
                                    synchronized (arrayList2) {
                                        while (i2 < arrayList2.size()) {
                                            ((AMap.OnMapScreenShotListener) arrayList2.get(i2)).onMapScreenShot(null);
                                            ((AMap.OnMapScreenShotListener) arrayList2.get(i2)).onMapScreenShot(null, i3);
                                            i2++;
                                            break;
                                        }
                                        break;
                                    }
                                    return;
                                } catch (Throwable th5) {
                                    th5.printStackTrace();
                                    return;
                                }
                            }
                            Canvas canvas = new Canvas(bitmap);
                            if (l.this.V && (elVarF = l.this.C.f()) != null) {
                                elVarF.onDraw(canvas);
                            }
                            l.this.C.a(canvas);
                            try {
                                List listA5 = l.this.u.a(AMap.onMapPrintScreenListener.class.hashCode());
                                ArrayList arrayList3 = listA5 != null ? new ArrayList(listA5) : null;
                                List listA6 = l.this.u.a(AMap.OnMapScreenShotListener.class.hashCode());
                                ArrayList arrayList4 = listA6 != null ? new ArrayList(listA6) : null;
                                l.this.u.a(Integer.valueOf(AMap.onMapPrintScreenListener.class.hashCode()));
                                l.this.u.a(Integer.valueOf(AMap.OnMapScreenShotListener.class.hashCode()));
                                if (arrayList3 != null && arrayList3.size() > 0) {
                                    synchronized (arrayList3) {
                                        for (int i5 = 0; i5 < arrayList3.size(); i5++) {
                                            ((AMap.onMapPrintScreenListener) arrayList3.get(i5)).onMapPrint(new BitmapDrawable(l.this.e.getResources(), bitmap));
                                        }
                                        break;
                                    }
                                }
                                if (arrayList4 == null || arrayList4.size() <= 0) {
                                    return;
                                }
                                synchronized (arrayList4) {
                                    while (i2 < arrayList4.size()) {
                                        ((AMap.OnMapScreenShotListener) arrayList4.get(i2)).onMapScreenShot(bitmap);
                                        ((AMap.OnMapScreenShotListener) arrayList4.get(i2)).onMapScreenShot(bitmap, i3);
                                        i2++;
                                    }
                                    break;
                                }
                                return;
                            } catch (Throwable th6) {
                                th6.printStackTrace();
                                return;
                            }
                        case 16:
                            try {
                                List listA7 = l.this.u.a(AMap.OnMapLoadedListener.class.hashCode());
                                if (listA7 != null) {
                                    synchronized (listA7) {
                                        while (i2 < listA7.size()) {
                                            ((AMap.OnMapLoadedListener) listA7.get(i2)).onMapLoaded();
                                            i2++;
                                        }
                                    }
                                }
                                break;
                            } catch (Throwable th7) {
                                jw.c(th7, "AMapDelegateImp", "onMapLoaded");
                                th7.printStackTrace();
                                dx.a(th7);
                            }
                            if (l.this.C != null) {
                                l.this.C.i();
                                return;
                            }
                            return;
                        case 17:
                            if (!l.this.f.isInMapAnimation(l.this.F) || l.this.D == null) {
                                return;
                            }
                            l.this.D.setFlingState(false);
                            return;
                        case 18:
                            if (l.this.w != null) {
                                l.this.w.b();
                                return;
                            }
                            return;
                        case 19:
                            List listA8 = l.this.u.a(AMap.OnMapClickListener.class.hashCode());
                            if (listA8 != null) {
                                DPoint dPointObtain = DPoint.obtain();
                                l.this.getPixel2LatLng(message.arg1, message.arg2, dPointObtain);
                                try {
                                    synchronized (listA8) {
                                        Iterator it3 = listA8.iterator();
                                        while (it3.hasNext()) {
                                            ((AMap.OnMapClickListener) it3.next()).onMapClick(new LatLng(dPointObtain.y, dPointObtain.x));
                                        }
                                        break;
                                    }
                                    dPointObtain.recycle();
                                    return;
                                } catch (Throwable th8) {
                                    jw.c(th8, "AMapDelegateImp", "OnMapClickListener.onMapClick");
                                    th8.printStackTrace();
                                    return;
                                }
                            }
                            return;
                        case 20:
                            try {
                                List listA9 = l.this.u.a(AMap.OnPOIClickListener.class.hashCode());
                                if (listA9 == null || listA9.size() <= 0) {
                                    return;
                                }
                                synchronized (listA9) {
                                    while (i2 < listA9.size()) {
                                        ((AMap.OnPOIClickListener) listA9.get(i2)).onPOIClick((Poi) message.obj);
                                        i2++;
                                    }
                                    break;
                                }
                                return;
                            } catch (Throwable th9) {
                                jw.c(th9, "AMapDelegateImp", "OnPOIClickListener.onPOIClick");
                                th9.printStackTrace();
                                return;
                            }
                        default:
                            return;
                    }
                    jw.c(th, "AMapDelegateImp", "handleMessage");
                    th.printStackTrace();
                    return;
                }
                if (l.this.f != null) {
                    l.this.f.triggerMainThread();
                }
            }
        };
        this.aJ = new a() { // from class: com.amap.api.col.3sl.l.11
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                try {
                    l.this.setTrafficEnabled(this.c);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        this.aK = new a() { // from class: com.amap.api.col.3sl.l.21
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                try {
                    l lVar = l.this;
                    lVar.setCenterToPixel(lVar.aC, l.this.aD);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        this.aL = new a() { // from class: com.amap.api.col.3sl.l.32
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                l.this.a(this.f, this.d, this.e);
            }
        };
        this.aM = new a() { // from class: com.amap.api.col.3sl.l.41
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                l.this.setMapCustomEnable(this.c);
            }
        };
        this.aN = new a() { // from class: com.amap.api.col.3sl.l.42
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                l.this.a(this.f, this.c);
            }
        };
        this.aO = new a() { // from class: com.amap.api.col.3sl.l.43
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                try {
                    l.this.setMapTextEnable(this.c);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        this.aP = new a() { // from class: com.amap.api.col.3sl.l.44
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                try {
                    l.this.setRoadArrowEnable(this.c);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        this.aQ = new a() { // from class: com.amap.api.col.3sl.l.45
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                try {
                    l.this.setNaviLabelEnable(this.c, this.g, this.h);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        this.aR = new a() { // from class: com.amap.api.col.3sl.l.2
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                try {
                    l.this.setConstructingRoadEnable(this.c);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        this.aS = new a() { // from class: com.amap.api.col.3sl.l.3
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                try {
                    l.this.setTrafficStyleWithTextureData(this.i);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        this.aT = new a() { // from class: com.amap.api.col.3sl.l.4
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                try {
                    l.this.setTrafficStyleWithTexture(this.i, this.j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        this.aU = new a() { // from class: com.amap.api.col.3sl.l.5
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                l.this.b(this.f, this.c);
            }
        };
        this.aV = new a() { // from class: com.amap.api.col.3sl.l.6
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                try {
                    l.this.setIndoorEnabled(this.c);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        this.aW = new Runnable() { // from class: com.amap.api.col.3sl.l.7
            @Override // java.lang.Runnable
            public final void run() {
                el elVarF;
                if (l.this.C == null || (elVarF = l.this.C.f()) == null) {
                    return;
                }
                elVarF.c();
            }
        };
        this.aX = new a() { // from class: com.amap.api.col.3sl.l.8
            @Override // com.amap.api.col.3sl.l.a, java.lang.Runnable
            public final void run() {
                super.run();
                l.this.c(this.f, this.c);
            }
        };
        this.aZ = "";
        this.ba = "";
        this.bb = false;
        this.bc = false;
        this.bd = 0;
        this.be = new EAMapPlatformGestureInfo();
        this.k = new Point();
        this.bf = 0L;
        this.l = null;
        this.bg = null;
        this.m = new float[16];
        this.n = new float[16];
        this.o = new float[16];
        this.bh = null;
        this.p = new float[12];
        this.q = "precision highp float;\nattribute vec3 aVertex;//顶点数组,三维坐标\nuniform mat4 aMVPMatrix;//mvp矩阵\nvoid main(){\n  gl_Position = aMVPMatrix * vec4(aVertex, 1.0);\n}";
        this.r = "//有颜色 没有纹理\nprecision highp float;\nvoid main(){\n  gl_FragColor = vec4(1.0,0,0,1.0);\n}";
        this.s = -1;
        this.e = context;
        iq iqVarA = ip.a(context, dx.a());
        if (iqVarA.a == ip.c.SuccessCode) {
            dz.a(context);
            dz.a(dy.c, "init map delegate");
        }
        com.autonavi.extra.b bVar = new com.autonavi.extra.b();
        this.aY = bVar;
        bVar.a();
        this.aY.b();
        jw.a(this.e);
        dl.a().a(this.e);
        w.b = ig.c(context);
        dc.a(this.e);
        this.ao = new z(this);
        GLMapRender gLMapRender = new GLMapRender(this);
        this.an = gLMapRender;
        this.B = iGLSurfaceView;
        iGLSurfaceView.setRenderer(gLMapRender);
        aa aaVar = new aa(this, this.e);
        this.D = aaVar;
        this.f = new GLMapEngine(this.e, this);
        this.C = new eh(this.e, this, aaVar);
        this.z = new ag(this);
        this.C.a(new c(this, b2));
        this.aE = new b();
        iGLSurfaceView.setRenderMode(0);
        this.an.setRenderFps(15.0f);
        this.f.setMapListener(this);
        this.y = new ad(this);
        this.t = new p(this);
        av avVar = new av(this.e);
        this.w = avVar;
        avVar.a(this.C);
        this.w.b(new cn(aaVar, context));
        this.aa = new t(this.e, this);
        this.L = new aw(this.e);
        this.ak = new cs(this.e, this);
        cu cuVar = new cu(this.e);
        this.al = cuVar;
        cuVar.a(this);
        a(z);
        MapConfig mapConfig = this.b;
        k kVar = new k(this, this.e, mapConfig != null ? mapConfig.isAbroadEnable() : false);
        this.aH = kVar;
        kVar.a(this);
        if (iqVarA.a != ip.c.SuccessCode) {
            this.b.setMapEnable(false);
        }
        this.F = this.f.getEngineIDWithType(1);
    }

    private void a(boolean z) {
        com.autonavi.extra.b bVar = this.aY;
        if (bVar != null) {
            Object objJ = bVar.j();
            if (objJ != null && (objJ instanceof Boolean)) {
                MapConfig mapConfig = this.b;
                if (mapConfig != null) {
                    mapConfig.setAbroadEnable(z && ((Boolean) objJ).booleanValue());
                }
                if (z && ((Boolean) objJ).booleanValue()) {
                    MapsInitializer.setSupportRecycleView(false);
                }
            }
            Object objJ2 = this.aY.j();
            if (objJ2 != null && (objJ2 instanceof Boolean)) {
                this.C.a(((Boolean) objJ2).booleanValue());
            }
            Object objJ3 = this.aY.j();
            if (objJ2 == null || !(objJ2 instanceof Integer)) {
                return;
            }
            this.af = ((Integer) objJ3).intValue();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final com.autonavi.extra.b getAMapExtraInterfaceManager() {
        return this.aY;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final IGlOverlayLayer getGlOverlayLayer() {
        return this.D;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setMapEnable(boolean z) {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            mapConfig.setMapEnable(z);
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final GLMapEngine getGLMapEngine() {
        return this.f;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setGestureStatus(int i, int i2) {
        if (this.aB == 0 || i2 != 5) {
            this.aB = i2;
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float getPreciseLevel(int i) {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            return mapConfig.getSZ();
        }
        return 0.0f;
    }

    private float c() {
        if (this.b != null) {
            return getMapConfig().getSZ();
        }
        return 0.0f;
    }

    private boolean a(int i, int i2) {
        AbstractCameraUpdateMessage abstractCameraUpdateMessageA;
        if (!this.aw || ((int) c()) >= this.b.getMaxZoomLevel()) {
            return false;
        }
        try {
            if (!this.I && !this.z.isZoomInByScreenCenter()) {
                this.k.x = i;
                this.k.y = i2;
                abstractCameraUpdateMessageA = al.a(1.0f, this.k);
            } else {
                abstractCameraUpdateMessageA = al.a(1.0f, (Point) null);
            }
            animateCamera(abstractCameraUpdateMessageA);
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "onDoubleTap");
            th.printStackTrace();
        }
        resetRenderTime();
        return true;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void zoomOut(int i) {
        if (this.aw && ((int) c()) > this.b.getMinZoomLevel()) {
            try {
                animateCamera(al.b());
            } catch (Throwable th) {
                jw.c(th, "AMapDelegateImp", "onDoubleTap");
                th.printStackTrace();
            }
            resetRenderTime();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final boolean isLockMapAngle(int i) {
        return g(i);
    }

    private void d() {
        if (this.aw) {
            this.ao.a();
            this.ap = true;
            this.au = true;
            try {
                stopAnimation();
            } catch (RemoteException unused) {
            }
        }
    }

    private void e() {
        this.ap = true;
        this.au = false;
        if (this.R) {
            this.R = false;
        }
        if (this.Q) {
            this.Q = false;
        }
        if (this.S) {
            this.S = false;
        }
        try {
            if (this.M) {
                List listA = this.u.a(AMap.OnMarkerDragListener.class.hashCode());
                if (listA != null && listA.size() > 0 && this.O != null) {
                    synchronized (listA) {
                        for (int i = 0; i < listA.size(); i++) {
                            ((AMap.OnMarkerDragListener) listA.get(i)).onMarkerDragEnd(this.O);
                        }
                    }
                    this.O = null;
                }
                this.M = false;
            }
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "OnMarkerDragListener.onMarkerDragEnd");
            th.printStackTrace();
        }
        if (this.N) {
            if (this.P != null) {
                this.P = null;
            }
            this.N = false;
        }
    }

    private void a(MotionEvent motionEvent) throws RemoteException {
        if (!this.M || this.O == null) {
            return;
        }
        int x = (int) motionEvent.getX();
        int y = (int) (motionEvent.getY() - 60.0f);
        if (this.O.getPosition() != null) {
            DPoint dPointObtain = DPoint.obtain();
            getPixel2LatLng(x, y, dPointObtain);
            LatLng latLng = new LatLng(dPointObtain.y, dPointObtain.x);
            dPointObtain.recycle();
            this.O.setPosition(latLng);
            try {
                List listA = this.u.a(AMap.OnMarkerDragListener.class.hashCode());
                if (listA == null || listA.size() <= 0) {
                    return;
                }
                synchronized (listA) {
                    for (int i = 0; i < listA.size(); i++) {
                        ((AMap.OnMarkerDragListener) listA.get(i)).onMarkerDrag(this.O);
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    private void b(MotionEvent motionEvent) {
        if (!this.N || this.P == null) {
            return;
        }
        int x = (int) motionEvent.getX();
        int y = (int) (motionEvent.getY() - 60.0f);
        if (this.P.getLatlng() != null) {
            DPoint dPointObtain = DPoint.obtain();
            getPixel2LatLng(x, y, dPointObtain);
            LatLng latLng = new LatLng(dPointObtain.y, dPointObtain.x);
            dPointObtain.recycle();
            this.P.setLatLng(latLng);
        }
    }

    private void b(boolean z) {
        this.at = z;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void clearTileCache() {
        this.D.clearTileCache();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final GLMapState getMapProjection() {
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            return gLMapEngine.getMapState(this.F);
        }
        return null;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void showMyLocationOverlay(Location location) throws RemoteException {
        if (location == null) {
            return;
        }
        try {
            if (this.E && this.L != null) {
                if (this.K == null) {
                    this.K = new cl(this, this.e);
                }
                if (location.getLongitude() != 0.0d && location.getLatitude() != 0.0d) {
                    this.K.a(location);
                }
                List listA = this.u.a(AMap.OnMyLocationChangeListener.class.hashCode());
                if (listA != null && listA.size() > 0) {
                    synchronized (listA) {
                        for (int i = 0; i < listA.size(); i++) {
                            ((AMap.OnMyLocationChangeListener) listA.get(i)).onMyLocationChange(location);
                        }
                    }
                }
                resetRenderTime();
                return;
            }
            cl clVar = this.K;
            if (clVar != null) {
                clVar.c();
            }
            this.K = null;
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "showMyLocationOverlay");
            th.printStackTrace();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final boolean removeGLOverlay(String str) throws RemoteException {
        resetRenderTime();
        return this.D.removeOverlay(str);
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final boolean removeGLModel(String str) {
        try {
            this.D.removeOverlay(str);
            return false;
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "removeGLModel");
            th.printStackTrace();
            return false;
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void changeGLOverlayIndex() {
        this.D.changeOverlayIndex();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float checkZoomLevel(float f) throws RemoteException {
        return dx.a(this.b, f);
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void latlon2Geo(double d2, double d3, IPoint iPoint) {
        Point pointLatLongToPixels = VirtualEarthProjection.latLongToPixels(d2, d3, 20);
        iPoint.x = pointLatLongToPixels.x;
        iPoint.y = pointLatLongToPixels.y;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void geo2Map(int i, int i2, FPoint fPoint) {
        fPoint.x = (int) (((double) i) - this.b.getSX());
        fPoint.y = (int) (((double) i2) - this.b.getSY());
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void geo2Latlng(int i, int i2, DPoint dPoint) {
        DPoint dPointPixelsToLatLong = VirtualEarthProjection.pixelsToLatLong(i, i2, 20);
        dPoint.x = dPointPixelsToLatLong.x;
        dPoint.y = dPointPixelsToLatLong.y;
        dPointPixelsToLatLong.recycle();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float getZoomLevel() {
        return c();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final IUiSettingsDelegate getUiSettings() {
        return this.z;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final IProjectionDelegate getProjection() throws RemoteException {
        return this.y;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final k getCustomStyleManager() {
        return this.aH;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final AMap.OnCameraChangeListener getOnCameraChangeListener() throws RemoteException {
        try {
            List listA = this.u.a(AMap.OnCameraChangeListener.class.hashCode());
            if (listA == null && listA.size() != 0) {
                return (AMap.OnCameraChangeListener) listA.get(0);
            }
        } catch (Throwable unused) {
        }
        return null;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void showInfoWindow(BaseOverlayImp baseOverlayImp) throws RemoteException {
        av avVar;
        if (baseOverlayImp == null || (avVar = this.w) == null) {
            return;
        }
        try {
            avVar.a(baseOverlayImp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void showInfoWindow(BaseOverlay baseOverlay) throws RemoteException {
        av avVar;
        if (baseOverlay == null || (avVar = this.w) == null) {
            return;
        }
        try {
            avVar.a(baseOverlay);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void hideInfoWindow() {
        av avVar = this.w;
        if (avVar != null) {
            avVar.c();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void getLatLng2Map(double d2, double d3, FPoint fPoint) {
        IPoint iPointObtain = IPoint.obtain();
        latlon2Geo(d2, d3, iPointObtain);
        geo2Map(iPointObtain.x, iPointObtain.y, fPoint);
        iPointObtain.recycle();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void getPixel2LatLng(int i, int i2, DPoint dPoint) {
        GLMapEngine gLMapEngine;
        GLMapState mapState;
        if (this.G.get() || !this.aw || (gLMapEngine = this.f) == null || (mapState = gLMapEngine.getMapState(this.F)) == null) {
            return;
        }
        IPoint iPointObtain = IPoint.obtain();
        mapState.screenToP20Point(i, i2, iPointObtain);
        DPoint dPointPixelsToLatLong = VirtualEarthProjection.pixelsToLatLong(iPointObtain.x, iPointObtain.y, 20);
        dPoint.x = dPointPixelsToLatLong.x;
        dPoint.y = dPointPixelsToLatLong.y;
        iPointObtain.recycle();
        dPointPixelsToLatLong.recycle();
    }

    private void a(GLMapState gLMapState, int i, int i2, DPoint dPoint) {
        if (!this.aw || this.f == null) {
            return;
        }
        gLMapState.screenToP20Point(i, i2, new Point());
        DPoint dPointPixelsToLatLong = VirtualEarthProjection.pixelsToLatLong(r0.x, r0.y, 20);
        dPoint.x = dPointPixelsToLatLong.x;
        dPoint.y = dPointPixelsToLatLong.y;
        dPointPixelsToLatLong.recycle();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void getLatLng2Pixel(double d2, double d3, IPoint iPoint) {
        if (this.G.get() || !this.aw || this.f == null) {
            return;
        }
        try {
            Point pointLatLongToPixels = VirtualEarthProjection.latLongToPixels(d2, d3, 20);
            FPoint fPointObtain = FPoint.obtain();
            a(pointLatLongToPixels.x, pointLatLongToPixels.y, fPointObtain);
            if (fPointObtain.x == -10000.0f && fPointObtain.y == -10000.0f) {
                GLMapState gLMapState = (GLMapState) this.f.getNewMapState(this.F);
                gLMapState.setCameraDegree(0.0f);
                gLMapState.recalculate();
                gLMapState.p20ToScreenPoint(pointLatLongToPixels.x, pointLatLongToPixels.y, fPointObtain);
                gLMapState.recycle();
            }
            iPoint.x = (int) fPointObtain.x;
            iPoint.y = (int) fPointObtain.y;
            fPointObtain.recycle();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void getPixel2Geo(int i, int i2, IPoint iPoint) {
        GLMapEngine gLMapEngine;
        GLMapState mapState;
        if (this.G.get() || !this.aw || (gLMapEngine = this.f) == null || (mapState = gLMapEngine.getMapState(this.F)) == null) {
            return;
        }
        mapState.screenToP20Point(i, i2, iPoint);
    }

    private void a(int i, int i2, FPoint fPoint) {
        GLMapEngine gLMapEngine;
        GLMapState mapState;
        if (this.G.get() || !this.aw || (gLMapEngine = this.f) == null || (mapState = gLMapEngine.getMapState(this.F)) == null) {
            return;
        }
        mapState.p20ToScreenPoint(i, i2, fPoint);
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void redrawInfoWindow() {
        if (!this.G.get() && this.aw) {
            this.j.sendEmptyMessage(18);
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final av getInfoWindowDelegate() {
        return this.w;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void showZoomControlsEnabled(boolean z) {
        ei eiVar;
        if (this.G.get() || (eiVar = this.C) == null) {
            return;
        }
        eiVar.b(Boolean.valueOf(z));
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void showIndoorSwitchControlsEnabled(boolean z) {
        ei eiVar;
        if (this.G.get() || (eiVar = this.C) == null) {
            return;
        }
        eiVar.a(Boolean.valueOf(z));
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void showMyLocationButtonEnabled(boolean z) {
        ei eiVar;
        if (this.G.get() || (eiVar = this.C) == null) {
            return;
        }
        eiVar.c(Boolean.valueOf(z));
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void showCompassEnabled(boolean z) {
        ei eiVar;
        if (this.G.get() || (eiVar = this.C) == null) {
            return;
        }
        eiVar.d(Boolean.valueOf(z));
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void showScaleEnabled(boolean z) {
        ei eiVar;
        if (this.G.get() || (eiVar = this.C) == null) {
            return;
        }
        eiVar.e(Boolean.valueOf(z));
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setZoomPosition(int i) {
        ei eiVar;
        if (this.G.get() || (eiVar = this.C) == null) {
            return;
        }
        eiVar.a(Integer.valueOf(i));
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final Rect getRect() {
        return this.X;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final LatLngBounds getMapBounds(LatLng latLng, float f, float f2, float f3) {
        int mapWidth = getMapWidth();
        int mapHeight = getMapHeight();
        if (mapWidth <= 0 || mapHeight <= 0 || this.G.get()) {
            return null;
        }
        float fA = dx.a(this.b, f);
        GLMapState gLMapState = new GLMapState(this.F, this.f.getNativeInstance());
        if (latLng != null) {
            IPoint iPointObtain = IPoint.obtain();
            latlon2Geo(latLng.latitude, latLng.longitude, iPointObtain);
            gLMapState.setCameraDegree(f3);
            gLMapState.setMapAngle(f2);
            gLMapState.setMapGeoCenter(iPointObtain.x, iPointObtain.y);
            gLMapState.setMapZoomer(fA);
            gLMapState.recalculate();
            iPointObtain.recycle();
        }
        DPoint dPointObtain = DPoint.obtain();
        a(gLMapState, 0, 0, dPointObtain);
        LatLng latLng2 = new LatLng(dPointObtain.y, dPointObtain.x, false);
        a(gLMapState, mapWidth, mapHeight, dPointObtain);
        LatLng latLng3 = new LatLng(dPointObtain.y, dPointObtain.x, false);
        dPointObtain.recycle();
        gLMapState.recycle();
        return LatLngBounds.builder().include(latLng3).include(latLng2).build();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void onResume() {
        try {
            this.an.setRenderFps(15.0f);
            this.B.setRenderMode(0);
            IGlOverlayLayer iGlOverlayLayer = this.D;
            if (iGlOverlayLayer != null) {
                iGlOverlayLayer.setFlingState(true);
            }
            cl clVar = this.K;
            if (clVar != null) {
                clVar.b();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void onPause() {
        f();
        IGlOverlayLayer iGlOverlayLayer = this.D;
        if (iGlOverlayLayer != null) {
            iGlOverlayLayer.setFlingState(false);
        }
    }

    private void f() {
        GLMapState gLMapState;
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine == null || (gLMapState = (GLMapState) gLMapEngine.getNewMapState(this.F)) == null) {
            return;
        }
        IPoint iPointObtain = IPoint.obtain();
        gLMapState.recalculate();
        gLMapState.getMapGeoCenter(iPointObtain);
        this.b.setSX(iPointObtain.x);
        this.b.setSY(iPointObtain.y);
        this.b.setSZ(gLMapState.getMapZoomer());
        this.b.setSC(gLMapState.getCameraDegree());
        this.b.setSR(gLMapState.getMapAngle());
        gLMapState.recycle();
        iPointObtain.recycle();
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:45:0x00ae -> B:58:0x00b1). Please report as a decompilation issue!!! */
    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.H || !this.aw || !this.at) {
            return false;
        }
        this.be.mGestureState = 3;
        this.be.mGestureType = 8;
        this.be.mLocation = new float[]{motionEvent.getX(), motionEvent.getY()};
        getEngineIDWithGestureInfo(this.be);
        l();
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            m();
            d();
        } else if (action == 1) {
            e();
        }
        if (motionEvent.getAction() == 2 && this.M) {
            try {
                a(motionEvent);
            } catch (Throwable th) {
                jw.c(th, "AMapDelegateImp", "onDragMarker");
                th.printStackTrace();
            }
            return true;
        }
        if (motionEvent.getAction() == 2 && this.N) {
            try {
                b(motionEvent);
            } catch (Throwable th2) {
                jw.c(th2, "AMapDelegateImp", "onDragGLTF");
                th2.printStackTrace();
            }
            return true;
        }
        if (this.ap) {
            try {
                List listA = this.u.a(AMap.SignleClickInterceptorListener.class.hashCode());
                if (listA == null || listA.size() <= 0 || !((AMap.SignleClickInterceptorListener) listA.get(0)).isInterceptorSignleClick(motionEvent)) {
                    this.ao.a(motionEvent);
                }
            } catch (Throwable th3) {
                th3.printStackTrace();
            }
        }
        try {
            List listA2 = this.u.a(AMap.OnMapTouchListener.class.hashCode());
            if (listA2 != null && listA2.size() > 0) {
                this.j.removeMessages(14);
                Message messageObtainMessage = this.j.obtainMessage();
                messageObtainMessage.what = 14;
                messageObtainMessage.obj = MotionEvent.obtain(motionEvent);
                messageObtainMessage.sendToTarget();
            }
        } catch (Throwable unused) {
        }
        return true;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void pixel2Map(int i, int i2, PointF pointF) {
        if (!this.aw || this.H || this.f == null) {
            return;
        }
        IPoint iPointObtain = IPoint.obtain();
        getPixel2Geo(i, i2, iPointObtain);
        pointF.x = iPointObtain.x - ((float) this.b.getSX());
        pointF.y = iPointObtain.y - ((float) this.b.getSY());
        iPointObtain.recycle();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void map2Geo(float f, float f2, IPoint iPoint) {
        iPoint.x = (int) (((double) f) + this.b.getSX());
        iPoint.y = (int) (((double) f2) + this.b.getSY());
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float toMapLenWithWin(int i) {
        GLMapEngine gLMapEngine;
        if (!this.aw || this.H || (gLMapEngine = this.f) == null) {
            return 0.0f;
        }
        return gLMapEngine.getMapState(this.F).getGLUnitWithWin(i);
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final CameraPosition getCameraPositionPrj(boolean z) {
        LatLng latLngG;
        try {
            if (this.b == null) {
                return null;
            }
            if (this.aw && !this.H && this.f != null) {
                if (z) {
                    DPoint dPointObtain = DPoint.obtain();
                    getPixel2LatLng(this.b.getAnchorX(), this.b.getAnchorY(), dPointObtain);
                    latLngG = new LatLng(dPointObtain.y, dPointObtain.x, false);
                    dPointObtain.recycle();
                } else {
                    latLngG = g();
                }
                return CameraPosition.builder().target(latLngG).bearing(this.b.getSR()).tilt(this.b.getSC()).zoom(this.b.getSZ()).build();
            }
            DPoint dPointObtain2 = DPoint.obtain();
            geo2Latlng((int) this.b.getSX(), (int) this.b.getSY(), dPointObtain2);
            LatLng latLng = new LatLng(dPointObtain2.y, dPointObtain2.x);
            dPointObtain2.recycle();
            return CameraPosition.builder().target(latLng).bearing(this.b.getSR()).tilt(this.b.getSC()).zoom(this.b.getSZ()).build();
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    private LatLng g() {
        MapConfig mapConfig = this.b;
        if (mapConfig == null) {
            return null;
        }
        DPoint dPointPixelsToLatLong = VirtualEarthProjection.pixelsToLatLong(mapConfig.getSX(), this.b.getSY(), 20);
        LatLng latLng = new LatLng(dPointPixelsToLatLong.y, dPointPixelsToLatLong.x, false);
        dPointPixelsToLatLong.recycle();
        return latLng;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final boolean isUseAnchor() {
        return this.I;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final Point getWaterMarkerPositon() {
        ei eiVar = this.C;
        if (eiVar != null) {
            return eiVar.a();
        }
        return new Point();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final View getGLMapView() {
        Object obj = this.B;
        if (obj instanceof View) {
            return (View) obj;
        }
        return null;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final boolean canShowIndoorSwitch() {
        au auVar;
        if (getZoomLevel() < 17.0f || (auVar = this.c) == null || auVar.g == null) {
            return false;
        }
        FPoint fPointObtain = FPoint.obtain();
        a(this.c.g.x, this.c.g.y, fPointObtain);
        return this.X.contains((int) fPointObtain.x, (int) fPointObtain.y);
    }

    private synchronized void h() {
        synchronized (this.aj) {
            int size = this.aj.size();
            for (int i = 0; i < size; i++) {
                this.aj.get(i).a().recycle();
            }
            this.aj.clear();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final int getLogoPosition() {
        try {
            return this.z.getLogoPosition();
        } catch (RemoteException e) {
            jw.c(e, "AMapDelegateImp", "getLogoPosition");
            e.printStackTrace();
            return 0;
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setLogoPosition(int i) {
        ei eiVar = this.C;
        if (eiVar != null) {
            eiVar.b(Integer.valueOf(i));
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setLogoBottomMargin(int i) {
        ei eiVar = this.C;
        if (eiVar != null) {
            eiVar.c(Integer.valueOf(i));
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setLogoLeftMargin(int i) {
        ei eiVar = this.C;
        if (eiVar != null) {
            eiVar.d(Integer.valueOf(i));
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float getLogoMarginRate(int i) {
        ei eiVar = this.C;
        if (eiVar != null) {
            return eiVar.a(i);
        }
        return 0.0f;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setLogoMarginRate(int i, float f) {
        ei eiVar = this.C;
        if (eiVar != null) {
            eiVar.a(Integer.valueOf(i), Float.valueOf(f));
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final int getMaskLayerType() {
        return this.ah;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void post(Runnable runnable) {
        IGLSurfaceView iGLSurfaceView = this.B;
        if (iGLSurfaceView != null) {
            iGLSurfaceView.post(runnable);
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void onLongPress(int i, MotionEvent motionEvent) {
        int i2 = 0;
        try {
            this.Q = false;
            b(i);
            BaseOverlay hitBaseOverlay = this.D.getHitBaseOverlay(motionEvent, 1);
            if (hitBaseOverlay instanceof Marker) {
                this.O = (Marker) hitBaseOverlay;
            }
            BaseOverlay hitBaseOverlay2 = this.D.getHitBaseOverlay(motionEvent, 3);
            if (hitBaseOverlay2 instanceof GLTFOverlay) {
                this.P = (GLTFOverlay) hitBaseOverlay2;
            }
            Marker marker = this.O;
            if (marker != null && marker.isDraggable()) {
                LatLng position = this.O.getPosition();
                if (position != null) {
                    IPoint iPointObtain = IPoint.obtain();
                    getLatLng2Pixel(position.latitude, position.longitude, iPointObtain);
                    iPointObtain.y -= 60;
                    DPoint dPointObtain = DPoint.obtain();
                    getPixel2LatLng(iPointObtain.x, iPointObtain.y, dPointObtain);
                    this.O.setPosition(new LatLng(dPointObtain.y, dPointObtain.x));
                    this.D.set2Top(this.O.getId());
                    try {
                        List listA = this.u.a(AMap.OnMarkerDragListener.class.hashCode());
                        if (listA != null && listA.size() > 0) {
                            synchronized (listA) {
                                while (i2 < listA.size()) {
                                    ((AMap.OnMarkerDragListener) listA.get(i2)).onMarkerDragStart(this.O);
                                    i2++;
                                }
                            }
                        }
                    } catch (Throwable th) {
                        jw.c(th, "AMapDelegateImp", "onMarkerDragStart");
                        th.printStackTrace();
                    }
                    this.M = true;
                    iPointObtain.recycle();
                    dPointObtain.recycle();
                }
            } else {
                GLTFOverlay gLTFOverlay = this.P;
                if (gLTFOverlay != null && gLTFOverlay.isDraggable()) {
                    LatLng latlng = this.P.getLatlng();
                    if (latlng != null) {
                        IPoint iPointObtain2 = IPoint.obtain();
                        getLatLng2Pixel(latlng.latitude, latlng.longitude, iPointObtain2);
                        iPointObtain2.y -= 60;
                        DPoint dPointObtain2 = DPoint.obtain();
                        getPixel2LatLng(iPointObtain2.x, iPointObtain2.y, dPointObtain2);
                        this.P.setLatLng(new LatLng(dPointObtain2.y, dPointObtain2.x));
                        this.D.set2Top(this.P.getId());
                        this.N = true;
                        iPointObtain2.recycle();
                        dPointObtain2.recycle();
                    }
                } else {
                    List listA2 = this.u.a(AMap.OnMapLongClickListener.class.hashCode());
                    if (listA2 != null && listA2.size() > 0) {
                        DPoint dPointObtain3 = DPoint.obtain();
                        getPixel2LatLng((int) motionEvent.getX(), (int) motionEvent.getY(), dPointObtain3);
                        synchronized (listA2) {
                            while (i2 < listA2.size()) {
                                ((AMap.OnMapLongClickListener) listA2.get(i2)).onMapLongClick(new LatLng(dPointObtain3.y, dPointObtain3.x));
                                i2++;
                            }
                        }
                        this.R = true;
                        dPointObtain3.recycle();
                    }
                }
            }
            this.an.resetTickCount(30);
        } catch (Throwable th2) {
            jw.c(th2, "AMapDelegateImp", "onLongPress");
            th2.printStackTrace();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final boolean onDoubleTap(int i, MotionEvent motionEvent) {
        if (!this.aw) {
            return false;
        }
        a((int) motionEvent.getX(), (int) motionEvent.getY());
        return false;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final boolean onSingleTapConfirmed(int i, MotionEvent motionEvent) {
        if (!this.aw) {
            return false;
        }
        try {
            b(i);
            if (h(motionEvent) || e(motionEvent) || g(motionEvent)) {
                return true;
            }
            d(motionEvent);
            if (f(motionEvent)) {
                return true;
            }
            c(motionEvent);
            return true;
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "onSingleTapUp");
            th.printStackTrace();
            return true;
        }
    }

    private void c(final MotionEvent motionEvent) {
        queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.9
            @Override // java.lang.Runnable
            public final void run() {
                try {
                    l.this.f.addGestureSingleTapMessage(motionEvent.getX(), motionEvent.getY());
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        });
    }

    private void a(final double d2, final double d3) {
        this.j.post(new Runnable() { // from class: com.amap.api.col.3sl.l.10
            @Override // java.lang.Runnable
            public final void run() {
                Message messageObtain = Message.obtain();
                messageObtain.what = 19;
                messageObtain.arg1 = (int) d2;
                messageObtain.arg2 = (int) d3;
                l.this.j.sendMessage(messageObtain);
            }
        });
    }

    private boolean d(MotionEvent motionEvent) {
        try {
            List listA = this.u.a(AMap.OnPolylineClickListener.class.hashCode());
            if (listA != null && listA.size() > 0) {
                DPoint dPointObtain = DPoint.obtain();
                getPixel2LatLng((int) motionEvent.getX(), (int) motionEvent.getY(), dPointObtain);
                LatLng latLng = new LatLng(dPointObtain.y, dPointObtain.x);
                dPointObtain.recycle();
                Polyline hitOverlay = this.D.getHitOverlay(latLng, 2);
                if (hitOverlay != null) {
                    synchronized (listA) {
                        Iterator it = listA.iterator();
                        while (it.hasNext()) {
                            ((AMap.OnPolylineClickListener) it.next()).onPolylineClick(hitOverlay);
                        }
                    }
                    return false;
                }
            }
        } catch (Throwable unused) {
        }
        return false;
    }

    private boolean e(MotionEvent motionEvent) throws RemoteException {
        LatLng position;
        DPoint dPointObtain = DPoint.obtain();
        getPixel2LatLng((int) motionEvent.getX(), (int) motionEvent.getY(), dPointObtain);
        LatLng latLng = new LatLng(dPointObtain.y, dPointObtain.x);
        dPointObtain.recycle();
        boolean z = true;
        BaseOverlay hitBaseOverlay = this.D.getHitBaseOverlay(latLng, 1);
        if ((hitBaseOverlay instanceof Marker) && ((Marker) hitBaseOverlay).getId().contains("MARKER")) {
            try {
                Marker marker = (Marker) hitBaseOverlay;
                this.D.set2Top(marker.getId());
                List listA = this.u.a(AMap.OnMarkerClickListener.class.hashCode());
                if (listA != null && listA.size() > 0) {
                    synchronized (listA) {
                        if (listA.size() == 1) {
                            boolean zOnMarkerClick = ((AMap.OnMarkerClickListener) listA.get(0)).onMarkerClick(marker);
                            if (zOnMarkerClick) {
                                return true;
                            }
                            z = zOnMarkerClick;
                        } else {
                            Iterator it = listA.iterator();
                            boolean zOnMarkerClick2 = false;
                            while (it.hasNext()) {
                                zOnMarkerClick2 |= ((AMap.OnMarkerClickListener) it.next()).onMarkerClick(marker);
                            }
                            if (zOnMarkerClick2) {
                                return true;
                            }
                            z = zOnMarkerClick2;
                        }
                    }
                }
                this.D.showInfoWindow(marker.getId());
                if (!marker.isViewMode() && (position = marker.getPosition()) != null) {
                    IPoint iPointObtain = IPoint.obtain();
                    latlon2Geo(position.latitude, position.longitude, iPointObtain);
                    moveCamera(al.a(iPointObtain));
                }
                return z;
            } catch (Throwable th) {
                jw.c(th, "AMapDelegateImp", "onMarkerTap");
                th.printStackTrace();
            }
        }
        return false;
    }

    private boolean f(MotionEvent motionEvent) {
        DPoint dPointObtain = DPoint.obtain();
        getPixel2LatLng((int) motionEvent.getX(), (int) motionEvent.getY(), dPointObtain);
        LatLng latLng = new LatLng(dPointObtain.y, dPointObtain.x);
        dPointObtain.recycle();
        BaseOverlay hitBaseOverlay = this.D.getHitBaseOverlay(latLng, 3);
        if (!(hitBaseOverlay instanceof GLTFOverlay)) {
            return false;
        }
        GLTFOverlay gLTFOverlay = (GLTFOverlay) hitBaseOverlay;
        if (!gLTFOverlay.isClickable()) {
            return false;
        }
        try {
            this.D.set2Top(gLTFOverlay.getId());
            gLTFOverlay.tapClick();
            return true;
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "onGLTFTap");
            th.printStackTrace();
            return false;
        }
    }

    private boolean g(MotionEvent motionEvent) {
        if (this.D != null && this.aG != null) {
            DPoint dPointObtain = DPoint.obtain();
            if (this.f != null) {
                getPixel2LatLng((int) motionEvent.getX(), (int) motionEvent.getY(), dPointObtain);
                MultiPointItem multiPointItem = this.D.getMultiPointItem(new LatLng(dPointObtain.y, dPointObtain.x));
                if (multiPointItem == null) {
                    return false;
                }
                boolean zOnPointClick = this.aG.onPointClick(multiPointItem);
                dPointObtain.recycle();
                return zOnPointClick;
            }
        }
        return false;
    }

    private boolean h(MotionEvent motionEvent) throws RemoteException {
        try {
            List listA = this.u.a(AMap.OnInfoWindowClickListener.class.hashCode());
            BaseOverlay baseOverlayA = this.w.a(motionEvent);
            if (baseOverlayA != null && (baseOverlayA instanceof Marker)) {
                synchronized (listA) {
                    for (int i = 0; i < listA.size(); i++) {
                        ((AMap.OnInfoWindowClickListener) listA.get(i)).onInfoWindowClick((Marker) baseOverlayA);
                    }
                }
                return true;
            }
        } catch (Throwable unused) {
        }
        return false;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void drawFrame(GL10 gl10) {
        if (this.G.get() || this.f == null || EGL14.eglGetCurrentContext() == EGL14.EGL_NO_CONTEXT) {
            return;
        }
        MapConfig mapConfig = this.b;
        if (mapConfig != null && !mapConfig.isMapEnable()) {
            GLES20.glClear(16640);
            return;
        }
        a(this.F);
        this.f.renderAMap();
        this.f.pushRendererState();
        this.j.sendEmptyMessage(30);
        k kVar = this.aH;
        if (kVar != null) {
            kVar.a();
        }
        i();
        k();
        if (!this.ay) {
            this.ay = true;
        }
        this.f.popRendererState();
        if (Cdo.a()) {
            try {
                if (this.B instanceof o) {
                    if (this.d == null) {
                        this.d = new Cdo();
                    }
                    this.d.e();
                    if (!this.d.f() || this.d.d()) {
                        return;
                    }
                    if (this.d.a(((o) this.B).getBitmap())) {
                        if (Cdo.b()) {
                            removecache();
                        }
                        if (Cdo.c()) {
                            Cdo.g();
                        }
                        dz.b(dy.g, "pure screen: found pure check");
                    }
                }
            } catch (Throwable th) {
                jw.c(th, "AMapDelegateImp", "PureScreenCheckTool.checkBlackScreen");
            }
        }
    }

    private void a(int i) {
        int i2 = this.ai;
        if (i2 != -1) {
            this.an.setRenderFps(i2);
            resetRenderTime();
        } else if (this.f.isInMapAction(i) || this.au) {
            this.an.setRenderFps(40.0f);
        } else if (this.f.isInMapAnimation(i) || this.D.isGLTFAnimated()) {
            this.an.setRenderFps(30.0f);
            this.an.resetTickCount(15);
        } else {
            this.an.setRenderFps(15.0f);
        }
        if (this.b.isWorldMapEnable() != MapsInitializer.isLoadWorldGridMap()) {
            b();
            this.b.setWorldMapEnable(MapsInitializer.isLoadWorldGridMap());
        }
    }

    private void i() {
        if (this.U > 0) {
            boolean zCanStopMapRender = this.f.canStopMapRender(this.F);
            if (!zCanStopMapRender) {
                int i = this.U - 1;
                this.U = i;
                if (i > 0) {
                    this.f.renderAMap();
                    return;
                }
            }
            this.U = 0;
            Message messageObtainMessage = this.j.obtainMessage(15, this.f.getScreenShot(this.F, 0, 0, getMapWidth(), getMapHeight()));
            messageObtainMessage.arg1 = zCanStopMapRender ? 1 : 0;
            messageObtainMessage.sendToTarget();
        }
    }

    private void j() {
        GLMapState mapState;
        List listA;
        List listA2;
        List listA3;
        if (this.G.get() || (mapState = this.f.getMapState(this.F)) == null) {
            return;
        }
        mapState.getViewMatrix(this.b.getViewMatrix());
        mapState.getProjectionMatrix(this.b.getProjectionMatrix());
        this.b.updateFinalMatrix();
        DPoint mapGeoCenter = mapState.getMapGeoCenter();
        this.b.setSX(mapGeoCenter.x);
        this.b.setSY(mapGeoCenter.y);
        this.b.setSZ(mapState.getMapZoomer());
        this.b.setSC(mapState.getCameraDegree());
        this.b.setSR(mapState.getMapAngle());
        this.b.setSkyHeight(mapState.getSkyHeight());
        if (this.b.isMapStateChange()) {
            DPoint dPointPixelsToLatLong = VirtualEarthProjection.pixelsToLatLong(mapGeoCenter.x, mapGeoCenter.y, 20);
            CameraPosition cameraPosition = new CameraPosition(new LatLng(dPointPixelsToLatLong.y, dPointPixelsToLatLong.x, false), this.b.getSZ(), this.b.getSC(), this.b.getSR());
            dPointPixelsToLatLong.recycle();
            Message messageObtainMessage = this.j.obtainMessage();
            messageObtainMessage.what = 10;
            messageObtainMessage.obj = cameraPosition;
            this.j.sendMessage(messageObtainMessage);
            boolean z = true;
            this.av = true;
            redrawInfoWindow();
            try {
                if (this.z.isZoomControlsEnabled() && this.b.isNeedUpdateZoomControllerState() && (listA3 = this.u.a(AMapWidgetListener.class.hashCode())) != null && listA3.size() > 0) {
                    synchronized (listA3) {
                        for (int i = 0; i < listA3.size(); i++) {
                            ((AMapWidgetListener) listA3.get(i)).invalidateZoomController(this.b.getSZ());
                        }
                    }
                }
                if (this.b.getChangeGridRatio() != 1.0d) {
                    b();
                }
                if (!this.z.isCompassEnabled() || (!this.b.isTiltChanged() && !this.b.isBearingChanged())) {
                    z = false;
                }
                if (z && (listA2 = this.u.a(AMapWidgetListener.class.hashCode())) != null && listA2.size() > 0) {
                    synchronized (listA2) {
                        for (int i2 = 0; i2 < listA2.size(); i2++) {
                            ((AMapWidgetListener) listA2.get(i2)).invalidateCompassView();
                        }
                    }
                }
                if (!this.z.isScaleControlsEnabled() || (listA = this.u.a(AMapWidgetListener.class.hashCode())) == null || listA.size() <= 0) {
                    return;
                }
                synchronized (listA) {
                    for (int i3 = 0; i3 < listA.size(); i3++) {
                        ((AMapWidgetListener) listA.get(i3)).invalidateScaleView();
                    }
                }
                return;
            } catch (Throwable th) {
                th.printStackTrace();
                return;
            }
        }
        if (!this.au && this.f.getAnimateionsCount() == 0 && this.f.getStateMessageCount() == 0) {
            onChangeFinish();
        }
    }

    private void k() {
        if (!this.J) {
            this.j.sendEmptyMessage(16);
            this.J = true;
            b();
        }
        long j = this.bf;
        if (j < 2) {
            this.bf = j + 1;
            return;
        }
        final ed edVarD = this.C.d();
        if (edVarD == null || edVarD.getVisibility() == 8) {
            return;
        }
        du.a(this.e, System.currentTimeMillis() - this.aI);
        this.j.post(new Runnable() { // from class: com.amap.api.col.3sl.l.12
            @Override // java.lang.Runnable
            public final void run() {
                if (l.this.H) {
                    return;
                }
                try {
                    if (l.this.c != null) {
                        l lVar = l.this;
                        lVar.setIndoorBuildingInfo(lVar.c);
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                edVarD.a();
            }
        });
        this.f.setStyleChangeGradualEnable(this.F, true);
    }

    final void b() {
        this.j.obtainMessage(17, 1, 0).sendToTarget();
    }

    private void b(final int i) {
        queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.13
            @Override // java.lang.Runnable
            public final void run() {
                if (!l.this.aw || l.this.f == null) {
                    return;
                }
                l.this.f.setHighlightSubwayEnable(i, false);
            }
        });
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float getMapAngle(int i) {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            return mapConfig.getSR();
        }
        return 0.0f;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void getGeoCenter(int i, IPoint iPoint) {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            iPoint.x = (int) mapConfig.getSX();
            iPoint.y = (int) this.b.getSY();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float getCameraDegree(int i) {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            return mapConfig.getSC();
        }
        return 0.0f;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void addGestureMapMessage(int i, AbstractGestureMapMessage abstractGestureMapMessage) {
        if (!this.aw || this.f == null) {
            return;
        }
        try {
            abstractGestureMapMessage.isUseAnchor = this.I;
            abstractGestureMapMessage.anchorX = this.b.getAnchorX();
            abstractGestureMapMessage.anchorY = this.b.getAnchorY();
            this.f.addGestureMessage(i, abstractGestureMapMessage, this.z.isGestureScaleByMapCenter(), this.b.getAnchorX(), this.b.getAnchorY());
        } catch (RemoteException unused) {
        }
    }

    private void c(int i) {
        if (this.au) {
            this.au = false;
        }
        GLMapRender gLMapRender = this.an;
        if (gLMapRender != null) {
            gLMapRender.renderPause();
        }
        f(i);
    }

    private void d(int i) {
        f(i);
        GLMapRender gLMapRender = this.an;
        if (gLMapRender != null) {
            gLMapRender.renderResume();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void resetRenderTimeLongLong() {
        GLMapRender gLMapRender = this.an;
        if (gLMapRender != null) {
            gLMapRender.resetTickCount(30);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void resetRenderTime() {
        GLMapRender gLMapRender = this.an;
        if (gLMapRender != null) {
            gLMapRender.resetTickCount(2);
        }
    }

    private void l() {
        GLMapRender gLMapRender = this.an;
        if (gLMapRender != null) {
            gLMapRender.resetTickCount(2);
        }
    }

    private void m() {
        GLMapRender gLMapRender;
        if (!this.aw || (gLMapRender = this.an) == null || gLMapRender.isRenderPause()) {
            return;
        }
        requestRender();
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void requestRender() {
        GLMapRender gLMapRender = this.an;
        if (gLMapRender == null || gLMapRender.isRenderPause()) {
            return;
        }
        this.B.requestRender();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void changeMapLogo(int i, boolean z) {
        if (this.G.get()) {
            return;
        }
        try {
            List listA = this.u.a(AMapWidgetListener.class.hashCode());
            if (listA == null || listA.size() <= 0) {
                return;
            }
            this.C.g(Boolean.valueOf(!z));
        } catch (Throwable unused) {
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final int getRenderMode() {
        return this.B.getRenderMode();
    }

    private void n() {
        if (this.ad) {
            return;
        }
        try {
            this.aa.setName("AuthThread");
            this.aa.start();
            this.ad = true;
        } catch (Throwable th) {
            th.printStackTrace();
            dx.a(th);
        }
    }

    private void o() {
        if (this.ae) {
            return;
        }
        try {
            if (this.ab == null) {
                this.ab = new r(this.e, this);
            }
            this.ab.setName("AuthProThread");
            this.ab.start();
            this.ae = true;
        } catch (Throwable th) {
            th.printStackTrace();
            dx.a(th);
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float getMapZoomScale() {
        return this.ar;
    }

    public final synchronized void a(int i, int i2, int i3) {
        a(i, i2, i3, false, null);
    }

    private synchronized void a(final int i, final int i2, final int i3, final boolean z, final StyleItem[] styleItemArr) {
        if (this.ax && this.aw && this.a) {
            e(i3);
            queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.14
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        l.this.f.setMapModeAndStyle(i, i2, i3, z, styleItemArr);
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
        } else {
            this.aL.f = i;
            this.aL.d = i2;
            this.aL.e = i3;
            this.aL.b = true;
        }
    }

    private void e(int i) {
        ei eiVar = this.C;
        if (eiVar != null) {
            if (i == 0) {
                if (eiVar.b()) {
                    this.C.g(Boolean.FALSE);
                    this.C.c();
                    return;
                }
                return;
            }
            if (eiVar.b()) {
                return;
            }
            this.C.g(Boolean.TRUE);
            this.C.c();
        }
    }

    private void f(final int i) {
        queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.15
            @Override // java.lang.Runnable
            public final void run() {
                try {
                    l.this.f.clearAllMessages(i);
                    l.this.f.clearAnimations(i, true);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        });
    }

    public final void a(final int i, final boolean z) {
        if (this.aw && this.ax) {
            resetRenderTime();
            queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.16
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        l.this.f.setBuildingEnable(i, z);
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
        } else {
            this.aN.c = z;
            this.aN.b = true;
            this.aN.f = i;
        }
    }

    public final void b(final int i, final boolean z) {
        if (this.aw && this.ax) {
            resetRenderTime();
            queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.17
                @Override // java.lang.Runnable
                public final void run() {
                    if (l.this.f != null) {
                        if (z) {
                            l.this.f.setAllContentEnable(i, true);
                        } else {
                            l.this.f.setAllContentEnable(i, false);
                        }
                        l.this.f.setSimple3DEnable(i, false);
                    }
                }
            });
        } else {
            this.aU.c = z;
            this.aU.b = true;
            this.aU.f = i;
        }
    }

    public final void c(final int i, final boolean z) {
        if (this.aw && this.ax) {
            resetRenderTime();
            queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.18
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        if (z) {
                            l.this.f.setBuildingTextureEnable(i, true);
                        } else {
                            l.this.f.setBuildingTextureEnable(i, false);
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
        } else {
            this.aX.c = z;
            this.aX.b = true;
            this.aX.f = i;
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final synchronized void createSurface(int i, GL10 gl10, EGLConfig eGLConfig) {
        dz.a(dy.c, "createSurface");
        this.aI = System.currentTimeMillis();
        int i2 = this.Y;
        if (i2 == 3 || i2 == 6) {
            this.C.d().a(ed.b);
        } else {
            this.C.d().a(ed.a);
        }
        this.ax = false;
        this.g = this.B.getWidth();
        this.h = this.B.getHeight();
        this.az = false;
        try {
            AeUtil.loadLib(this.e);
            dz.a(dy.c, "load lib complete");
            AeUtil.initCrashHandle(this.e);
            GLMapEngine.InitParam initParamInitResource = AeUtil.initResource(this.e);
            dz.a(dy.c, "load res complete");
            this.f.createAMapInstance(initParamInitResource);
            dz.a(dy.c, "create engine complete");
            this.aF = new cq();
            dz.a(dy.c, "init shader complete");
            com.autonavi.extra.b bVar = this.aY;
            if (bVar != null) {
                bVar.i();
            }
            this.aw = true;
            this.l = gl10.glGetString(7937);
        } catch (Throwable th) {
            dx.a(th);
            jw.c(th, "AMapDElegateImp", "createSurface");
            dz.b(dy.c, "createSurface failed " + th.getMessage());
            du.b(this.e, "init failed:" + th.getMessage());
        }
        if (ip.a(this.e, dx.a()).a == ip.c.SuccessCode) {
            n();
        }
        if (MapsInitializer.isTerrainEnable() && this.ac == null) {
            v vVar = new v(this.e, this);
            this.ac = vVar;
            vVar.a(0L);
        }
        CustomRenderer customRenderer = this.ag;
        if (customRenderer != null) {
            customRenderer.onSurfaceCreated(gl10, eGLConfig);
        }
        com.autonavi.extra.b bVar2 = this.aY;
        if (bVar2 != null) {
            bVar2.c();
        }
        this.D.onCreateAMapInstance();
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void changeSurface(int i, GL10 gl10, int i2, int i3) {
        WindowManager windowManager;
        dz.a(dy.c, "changeSurface " + i2 + " " + i3);
        this.az = false;
        if (!this.aw) {
            createSurface(i, gl10, null);
        }
        z zVar = this.ao;
        if (zVar != null && this.e != null && ((this.g != zVar.b() || this.h != this.ao.c()) && (windowManager = (WindowManager) this.e.getSystemService(Context.WINDOW_SERVICE)) != null)) {
            Display defaultDisplay = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            if (defaultDisplay != null) {
                defaultDisplay.getRealMetrics(displayMetrics);
                this.ao.a(displayMetrics.widthPixels, displayMetrics.heightPixels);
            }
        }
        this.g = i2;
        this.h = i3;
        this.X = new Rect(0, 0, i2, i3);
        this.F = a(i, new Rect(0, 0, this.g, this.h), this.g, this.h);
        dz.a(dy.c, "create engine with frame complete");
        if (!this.ax) {
            MapConfig mapConfig = this.b;
            if (mapConfig != null) {
                mapConfig.setMapZoomScale(this.ar);
                this.b.setMapWidth(i2);
                this.b.setMapHeight(i3);
            }
            this.f.setIndoorEnable(this.F, false);
            this.f.setSimple3DEnable(this.F, false);
            this.f.setStyleChangeGradualEnable(this.F, false);
            this.f.initMapOpenLayer("{\"bounds\" : [{\"x2\" : 235405312,\"x1\" : 188874751,\"y2\" : 85065727,\"y1\" : 122421247}],\"sublyr\" : [{\"type\" : 4,\"sid\" : 9000006,\"zlevel\" : 2}],\"id\" : 9006,\"minzoom\" : 6,\"update_period\" : 90,\"maxzoom\" : 20,\"cachemode\" : 2,\"url\" : \"http://mpsapi.amap.com/ws/mps/lyrdata/ugc/\"}");
            GLMapEngine.InitParam initParam = new GLMapEngine.InitParam();
            AeUtil.initIntersectionRes(this.e, initParam);
            this.f.setVectorOverlayPath(initParam.mIntersectionResPath);
        }
        synchronized (this) {
            this.ax = true;
        }
        if (!this.I) {
            this.b.setAnchorX(i2 >> 1);
            this.b.setAnchorY(i3 >> 1);
        } else {
            this.b.setAnchorX(Math.max(1, Math.min(this.aC, i2 - 1)));
            this.b.setAnchorY(Math.max(1, Math.min(this.aD, i3 - 1)));
        }
        this.f.setProjectionCenter(this.F, this.b.getAnchorX(), this.b.getAnchorY());
        this.a = true;
        if (this.aU.b) {
            this.aU.run();
        }
        if (this.aL.b) {
            this.aL.run();
        }
        if (this.aM.b) {
            this.aM.run();
        }
        if (this.aJ.b) {
            this.aJ.run();
        }
        if (this.aN.b) {
            this.aN.run();
        }
        if (this.aX.b) {
            this.aX.run();
        }
        if (this.aO.b) {
            this.aO.run();
        }
        if (this.aP.b) {
            this.aP.run();
        }
        if (this.aQ.b) {
            this.aQ.run();
        }
        if (this.aV.b) {
            this.aV.run();
        }
        if (this.aK.b) {
            this.aK.run();
        }
        if (this.aR.b) {
            this.aR.run();
        }
        a aVar = this.aS;
        if (aVar != null) {
            aVar.run();
        }
        a aVar2 = this.aT;
        if (aVar2 != null) {
            aVar2.run();
        }
        CustomRenderer customRenderer = this.ag;
        if (customRenderer != null) {
            customRenderer.onSurfaceChanged(gl10, i2, i3);
        }
        com.autonavi.extra.b bVar = this.aY;
        if (bVar != null) {
            bVar.d();
        }
        Handler handler = this.j;
        if (handler != null) {
            handler.post(this.aW);
        }
        redrawInfoWindow();
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void destroySurface(int i) {
        this.aA.lock();
        try {
            if (this.aw) {
                EGL14.eglGetCurrentContext();
                EGLContext eGLContext = EGL14.EGL_NO_CONTEXT;
                r();
                GLMapEngine gLMapEngine = this.f;
                if (gLMapEngine != null) {
                    if (gLMapEngine.getOverlayBundle(this.F) != null) {
                        this.f.getOverlayBundle(this.F).removeAll(true);
                    }
                    this.f.destroyAMapEngine();
                    this.f = null;
                    int i2 = this.bd;
                    if (i2 > 0) {
                        du.a(this.e, i2);
                    }
                    dz.a(dy.c, "destroy engine complete");
                }
                com.autonavi.extra.b bVar = this.aY;
                if (bVar != null) {
                    bVar.f();
                }
            }
            this.aw = false;
            this.ax = false;
            this.az = false;
        } finally {
            try {
            } finally {
            }
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final Context getContext() {
        return this.e;
    }

    private int a(int i, Rect rect, int i2, int i3) {
        int engineIDWithType;
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine == null || i < 0) {
            engineIDWithType = 0;
        } else {
            engineIDWithType = gLMapEngine.getEngineIDWithType(i);
            if (!this.f.isEngineCreated(engineIDWithType)) {
                int i4 = Build.VERSION.SDK_INT >= 4 ? this.e.getResources().getDisplayMetrics().densityDpi : 0;
                float f = this.e.getResources().getDisplayMetrics().density;
                NativeTextGenerate.getInstance().setDensity(f);
                GLMapEngine.MapViewInitParam mapViewInitParam = new GLMapEngine.MapViewInitParam();
                mapViewInitParam.engineId = engineIDWithType;
                mapViewInitParam.x = rect.left;
                mapViewInitParam.y = rect.top;
                mapViewInitParam.width = rect.width();
                mapViewInitParam.height = rect.height();
                mapViewInitParam.screenWidth = i2;
                mapViewInitParam.screenHeight = i3;
                mapViewInitParam.screenScale = f;
                mapViewInitParam.textScale = this.as * f;
                mapViewInitParam.mapZoomScale = this.ar;
                mapViewInitParam.taskThreadCount = 3;
                this.f.createAMapEngineWithFrame(mapViewInitParam);
                GLMapState mapState = this.f.getMapState(engineIDWithType);
                mapState.setMapZoomer(this.b.getSZ());
                mapState.setCameraDegree(this.b.getSC());
                mapState.setMapAngle(this.b.getSR());
                mapState.setMapGeoCenter(this.b.getSX(), this.b.getSY());
                this.f.setMapState(engineIDWithType, mapState);
                this.ar = mapState.calMapZoomScalefactor(i2, i3, i4);
                this.f.setOvelayBundle(engineIDWithType, new GLOverlayBundle<>(engineIDWithType, this));
            } else {
                a(engineIDWithType, rect.left, rect.top, rect.width(), rect.height(), i2, i3);
            }
        }
        this.G.set(false);
        return engineIDWithType;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final int getEngineIDWithGestureInfo(EAMapPlatformGestureInfo eAMapPlatformGestureInfo) {
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            return gLMapEngine.getEngineIDWithGestureInfo(eAMapPlatformGestureInfo);
        }
        return this.F;
    }

    private void a(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            gLMapEngine.setServiceViewRect(i, i2, i3, i4, i5, i6, i7);
        }
    }

    private boolean g(int i) {
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            return gLMapEngine.getSrvViewStateBoolValue(i, 7);
        }
        return false;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final CameraPosition getCameraPosition() throws RemoteException {
        return getCameraPositionPrj(this.I);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final float getMaxZoomLevel() {
        try {
            MapConfig mapConfig = this.b;
            if (mapConfig != null) {
                return mapConfig.getMaxZoomLevel();
            }
            return 20.0f;
        } catch (Throwable th) {
            dx.a(th);
            return 20.0f;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final float getMinZoomLevel() {
        try {
            MapConfig mapConfig = this.b;
            if (mapConfig != null) {
                return mapConfig.getMinZoomLevel();
            }
            return 3.0f;
        } catch (Throwable th) {
            dx.a(th);
            return 3.0f;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void moveCamera(CameraUpdate cameraUpdate) throws RemoteException {
        if (cameraUpdate == null) {
            return;
        }
        try {
            moveCamera(cameraUpdate.getCameraUpdateFactoryDelegate());
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void moveCamera(AbstractCameraUpdateMessage abstractCameraUpdateMessage) throws RemoteException {
        if (this.f == null || this.G.get()) {
            return;
        }
        try {
            if (this.H && this.f.getStateMessageCount() > 0) {
                AbstractCameraUpdateMessage abstractCameraUpdateMessageC = al.c();
                abstractCameraUpdateMessageC.nowType = AbstractCameraUpdateMessage.Type.changeGeoCenterZoomTiltBearing;
                abstractCameraUpdateMessageC.geoPoint = new DPoint(this.b.getSX(), this.b.getSY());
                abstractCameraUpdateMessageC.zoom = this.b.getSZ();
                abstractCameraUpdateMessageC.bearing = this.b.getSR();
                abstractCameraUpdateMessageC.tilt = this.b.getSC();
                this.f.addMessage(abstractCameraUpdateMessage, false);
                while (this.f.getStateMessageCount() > 0) {
                    AbstractCameraUpdateMessage stateMessage = this.f.getStateMessage();
                    if (stateMessage != null) {
                        stateMessage.mergeCameraUpdateDelegate(abstractCameraUpdateMessageC);
                    }
                }
                abstractCameraUpdateMessage = abstractCameraUpdateMessageC;
            }
        } catch (Throwable th) {
            dx.a(th);
        }
        resetRenderTime();
        this.f.clearAnimations(this.F, false);
        abstractCameraUpdateMessage.isChangeFinished = true;
        a(abstractCameraUpdateMessage);
        this.f.addMessage(abstractCameraUpdateMessage, false);
    }

    private void a(AbstractCameraUpdateMessage abstractCameraUpdateMessage) {
        abstractCameraUpdateMessage.isUseAnchor = this.I;
        if (this.I) {
            abstractCameraUpdateMessage.anchorX = this.b.getAnchorX();
            abstractCameraUpdateMessage.anchorY = this.b.getAnchorY();
        }
        if (abstractCameraUpdateMessage.width == 0) {
            abstractCameraUpdateMessage.width = getMapWidth();
        }
        if (abstractCameraUpdateMessage.height == 0) {
            abstractCameraUpdateMessage.height = getMapHeight();
        }
        abstractCameraUpdateMessage.mapConfig = this.b;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void animateCamera(CameraUpdate cameraUpdate) throws RemoteException {
        if (cameraUpdate == null) {
            return;
        }
        animateCamera(cameraUpdate.getCameraUpdateFactoryDelegate());
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void animateCamera(AbstractCameraUpdateMessage abstractCameraUpdateMessage) throws RemoteException {
        animateCameraWithDurationAndCallback(abstractCameraUpdateMessage, 250L, (AMap.CancelableCallback) null);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void animateCameraWithCallback(CameraUpdate cameraUpdate, AMap.CancelableCallback cancelableCallback) throws RemoteException {
        if (cameraUpdate == null) {
            return;
        }
        animateCameraWithDurationAndCallback(cameraUpdate, 250L, cancelableCallback);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void animateCameraWithDurationAndCallback(CameraUpdate cameraUpdate, long j, AMap.CancelableCallback cancelableCallback) {
        if (cameraUpdate == null) {
            return;
        }
        animateCameraWithDurationAndCallback(cameraUpdate.getCameraUpdateFactoryDelegate(), j, cancelableCallback);
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void animateCameraWithDurationAndCallback(AbstractCameraUpdateMessage abstractCameraUpdateMessage, long j, AMap.CancelableCallback cancelableCallback) {
        if (abstractCameraUpdateMessage == null || this.G.get() || this.f == null) {
            return;
        }
        abstractCameraUpdateMessage.mCallback = cancelableCallback;
        abstractCameraUpdateMessage.mDuration = j;
        if (this.H || getMapHeight() == 0 || getMapWidth() == 0) {
            try {
                moveCamera(abstractCameraUpdateMessage);
                if (abstractCameraUpdateMessage.mCallback != null) {
                    abstractCameraUpdateMessage.mCallback.onFinish();
                    return;
                }
                return;
            } catch (Throwable th) {
                th.printStackTrace();
                dx.a(th);
                return;
            }
        }
        try {
            this.f.interruptAnimation();
            resetRenderTime();
            a(abstractCameraUpdateMessage);
            this.f.addMessage(abstractCameraUpdateMessage, true);
        } catch (Throwable th2) {
            dx.a(th2);
            th2.printStackTrace();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void stopAnimation() throws RemoteException {
        try {
            GLMapEngine gLMapEngine = this.f;
            if (gLMapEngine != null) {
                gLMapEngine.interruptAnimation();
            }
            resetRenderTime();
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Polyline addPolyline(PolylineOptions polylineOptions) throws RemoteException {
        if (polylineOptions == null) {
            return null;
        }
        try {
            resetRenderTime();
            String strCreateId = this.D.createId("POLYLINE");
            Polyline polyline = (Polyline) this.D.addOverlayObject(strCreateId, new Polyline(this.D, polylineOptions, strCreateId), polylineOptions);
            dz.c(dy.d, "addPolyline ".concat(String.valueOf(strCreateId)));
            return polyline;
        } catch (Throwable th) {
            dx.a(th);
            dz.a(dy.d, "addPolyline failed " + th.getMessage(), polylineOptions);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final BuildingOverlay addBuildingOverlay() {
        try {
            du.h(this.e);
            String strCreateId = this.D.createId("BUILDINGOVERLAY");
            BuildingOverlay buildingOverlay = new BuildingOverlay(this.D, strCreateId);
            Field declaredField = buildingOverlay.getClass().getDeclaredField("buildingOverlayTotalOptions");
            if (declaredField == null) {
                return null;
            }
            resetRenderTime();
            declaredField.setAccessible(true);
            Object obj = declaredField.get(buildingOverlay);
            IGlOverlayLayer iGlOverlayLayer = this.D;
            return (iGlOverlayLayer == null || !(obj instanceof BaseOptions)) ? buildingOverlay : (BuildingOverlay) iGlOverlayLayer.addOverlayObject(strCreateId, buildingOverlay, (BaseOptions) obj);
        } catch (Exception e) {
            e.printStackTrace();
            dx.a(e);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final GL3DModel addGLModel(GL3DModelOptions gL3DModelOptions) {
        resetRenderTime();
        String strCreateId = this.D.createId("GL3DMODEL");
        GL3DModel gL3DModel = new GL3DModel(this.D, gL3DModelOptions, strCreateId);
        this.D.addOverlayObject(strCreateId, gL3DModel, gL3DModelOptions);
        return gL3DModel;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final ParticleOverlay addParticleOverlay(ParticleOverlayOptions particleOverlayOptions) {
        if (particleOverlayOptions == null) {
            return null;
        }
        try {
            resetRenderTime();
            du.c(this.e);
            String strCreateId = this.D.createId("PARTICLEOVERLAY");
            return (ParticleOverlay) this.D.addOverlayObject(strCreateId, new ParticleOverlay(this.D, particleOverlayOptions, strCreateId), particleOverlayOptions);
        } catch (Throwable th) {
            dx.a(th);
            th.printStackTrace();
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final NavigateArrow addNavigateArrow(NavigateArrowOptions navigateArrowOptions) throws RemoteException {
        if (navigateArrowOptions == null) {
            return null;
        }
        try {
            resetRenderTime();
            NavigateArrowOptions navigateArrowOptionsM42clone = navigateArrowOptions.m42clone();
            String strCreateId = this.D.createId("NAVIGATEARROW");
            NavigateArrow navigateArrow = new NavigateArrow(this.D, navigateArrowOptionsM42clone, strCreateId);
            IGlOverlayLayer iGlOverlayLayer = this.D;
            return iGlOverlayLayer != null ? (NavigateArrow) iGlOverlayLayer.addOverlayObject(strCreateId, navigateArrow, navigateArrowOptionsM42clone) : navigateArrow;
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Polygon addPolygon(PolygonOptions polygonOptions) throws RemoteException {
        if (polygonOptions == null) {
            return null;
        }
        try {
            resetRenderTime();
            PolygonOptions polygonOptionsM43clone = polygonOptions.m43clone();
            String strCreateId = this.D.createId("POLYGON");
            Polygon polygon = new Polygon(this.D, polygonOptionsM43clone, strCreateId);
            IGlOverlayLayer iGlOverlayLayer = this.D;
            return iGlOverlayLayer != null ? (Polygon) iGlOverlayLayer.addOverlayObject(strCreateId, polygon, polygonOptionsM43clone) : polygon;
        } catch (Throwable th) {
            dx.a(th);
            dz.a(dy.d, "addPolygon failed " + th.getMessage(), polygonOptions);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Circle addCircle(CircleOptions circleOptions) throws RemoteException {
        if (circleOptions == null) {
            return null;
        }
        try {
            resetRenderTime();
            CircleOptions circleOptionsM36clone = circleOptions.m36clone();
            String strCreateId = this.D.createId("CIRCLE");
            Circle circle = new Circle(this.D, circleOptionsM36clone, strCreateId);
            IGlOverlayLayer iGlOverlayLayer = this.D;
            return iGlOverlayLayer != null ? (Circle) iGlOverlayLayer.addOverlayObject(strCreateId, circle, circleOptionsM36clone) : circle;
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Arc addArc(ArcOptions arcOptions) throws RemoteException {
        if (arcOptions == null) {
            return null;
        }
        try {
            resetRenderTime();
            ArcOptions arcOptionsM34clone = arcOptions.m34clone();
            String strCreateId = this.D.createId("ARC");
            Arc arc = new Arc(this.D, arcOptionsM34clone, strCreateId);
            IGlOverlayLayer iGlOverlayLayer = this.D;
            return iGlOverlayLayer != null ? (Arc) iGlOverlayLayer.addOverlayObject(strCreateId, arc, arcOptionsM34clone) : arc;
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final GroundOverlay addGroundOverlay(GroundOverlayOptions groundOverlayOptions) throws RemoteException {
        if (groundOverlayOptions == null) {
            return null;
        }
        try {
            resetRenderTime();
            GroundOverlayOptions groundOverlayOptionsM38clone = groundOverlayOptions.m38clone();
            String strCreateId = this.D.createId("GROUNDOVERLAY");
            GroundOverlay groundOverlay = new GroundOverlay(this.D, groundOverlayOptionsM38clone, strCreateId);
            IGlOverlayLayer iGlOverlayLayer = this.D;
            return iGlOverlayLayer != null ? (GroundOverlay) iGlOverlayLayer.addOverlayObject(strCreateId, groundOverlay, groundOverlayOptionsM38clone) : groundOverlay;
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final MultiPointOverlay addMultiPointOverlay(MultiPointOverlayOptions multiPointOverlayOptions) throws RemoteException {
        if (multiPointOverlayOptions == null) {
            return null;
        }
        try {
            resetRenderTime();
            MultiPointOverlayOptions multiPointOverlayOptionsM41clone = multiPointOverlayOptions.m41clone();
            String strCreateId = this.D.createId("MULTIOVERLAY");
            MultiPointOverlay multiPointOverlay = new MultiPointOverlay(this.D, multiPointOverlayOptionsM41clone, strCreateId);
            IGlOverlayLayer iGlOverlayLayer = this.D;
            return iGlOverlayLayer != null ? (MultiPointOverlay) iGlOverlayLayer.addOverlayObject(strCreateId, multiPointOverlay, multiPointOverlayOptionsM41clone) : multiPointOverlay;
        } catch (Throwable unused) {
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Marker addMarker(MarkerOptions markerOptions) throws RemoteException {
        try {
            resetRenderTime();
            MarkerOptions markerOptionsM40clone = markerOptions.m40clone();
            String strCreateId = this.D.createId("MARKER");
            Marker marker = new Marker(this.D, markerOptionsM40clone, strCreateId);
            this.D.addOverlayObject(strCreateId, marker, markerOptionsM40clone);
            return marker;
        } catch (Throwable th) {
            dx.a(th);
            dz.a(dy.d, "addMarker failed " + th.getMessage(), markerOptions);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Text addText(TextOptions textOptions) throws RemoteException {
        try {
            resetRenderTime();
            String strCreateId = this.D.createId("TEXT");
            TextOptions textOptionsM44clone = textOptions.m44clone();
            MarkerOptions markerOptionsA = co.a(textOptionsM44clone);
            Marker marker = new Marker(this.D, markerOptionsA, strCreateId);
            marker.setObject(textOptionsM44clone.getObject());
            this.D.addOverlayObject(strCreateId, marker, markerOptionsA);
            return new Text(marker, textOptionsM44clone);
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final ArrayList<Marker> addMarkers(ArrayList<MarkerOptions> arrayList, boolean z) throws RemoteException {
        try {
            resetRenderTime();
            ArrayList<Marker> arrayList2 = new ArrayList<>();
            final LatLngBounds.Builder builder = LatLngBounds.builder();
            for (int i = 0; i < arrayList.size(); i++) {
                MarkerOptions markerOptions = arrayList.get(i);
                if (arrayList.get(i) != null) {
                    arrayList2.add(addMarker(markerOptions));
                    if (markerOptions.getPosition() != null) {
                        builder.include(markerOptions.getPosition());
                    }
                }
            }
            if (z && arrayList2.size() > 0) {
                getMainHandler().postDelayed(new Runnable() { // from class: com.amap.api.col.3sl.l.19
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            l.this.moveCamera(al.a(builder.build(), 50));
                        } catch (Throwable unused) {
                        }
                    }
                }, 50L);
            }
            return arrayList2;
        } catch (Throwable th) {
            dx.a(th);
            dz.a(dy.d, "addMarkers failed " + th.getMessage(), arrayList);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final TileOverlay addTileOverlay(TileOverlayOptions tileOverlayOptions) throws RemoteException {
        try {
            TileProvider tileProvider = tileOverlayOptions.getTileProvider();
            if (tileProvider != null && (tileProvider instanceof HeatmapTileProvider)) {
                du.a(this.e);
            }
            String strCreateId = this.D.createId("TILEOVERLAY");
            TileOverlay tileOverlay = new TileOverlay(this.D, tileOverlayOptions, strCreateId);
            this.D.addOverlayObject(strCreateId, tileOverlay, tileOverlayOptions);
            return tileOverlay;
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final MVTTileOverlay addMVTTileOverlay(MVTTileOverlayOptions mVTTileOverlayOptions) throws RemoteException {
        try {
            if (x.a().b() && !x.a().a("feature_mvt")) {
                dx.a(new AMapException(AMapException.FEATURE_MVT_NOT_SUPPORT));
                return null;
            }
            String strCreateId = this.D.createId("MVTTILEOVERLAY");
            MVTTileOverlay mVTTileOverlay = new MVTTileOverlay(this.D, mVTTileOverlayOptions, strCreateId);
            this.D.addOverlayObject(strCreateId, mVTTileOverlay, mVTTileOverlayOptions);
            return mVTTileOverlay;
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final HeatMapLayer addHeatMapLayer(HeatMapLayerOptions heatMapLayerOptions) throws RemoteException {
        try {
            resetRenderTime();
            if (heatMapLayerOptions == null) {
                return null;
            }
            String strCreateId = this.D.createId("HEATMAPLAYER");
            return (HeatMapLayer) this.D.addOverlayObject(strCreateId, new HeatMapLayer(this.D, heatMapLayerOptions, strCreateId), heatMapLayerOptions);
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final HeatMapGridLayer addHeatMapGridLayer(HeatMapGridLayerOptions heatMapGridLayerOptions) throws RemoteException {
        try {
            resetRenderTime();
            if (heatMapGridLayerOptions == null) {
                return null;
            }
            String strCreateId = this.D.createId("HEATMAPGRIDLAYER");
            return (HeatMapGridLayer) this.D.addOverlayObject(strCreateId, new HeatMapGridLayer(this.D, heatMapGridLayerOptions, strCreateId), heatMapGridLayerOptions);
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final GLTFOverlay addGLTFOverlay(GLTFOverlayOptions gLTFOverlayOptions) throws RemoteException {
        try {
            if (x.a().b() && !x.a().a("feature_gltf")) {
                dx.a(new AMapException(AMapException.FEATURE_GLTF_NOT_SUPPORT));
                return null;
            }
            resetRenderTime();
            GLTFOverlayOptions gLTFOverlayOptionsM37clone = gLTFOverlayOptions.m37clone();
            String strCreateId = this.D.createId("GLTFOVERLAY");
            GLTFOverlay gLTFOverlay = new GLTFOverlay(this.D, gLTFOverlayOptionsM37clone, strCreateId);
            this.D.addOverlayObject(strCreateId, gLTFOverlay, gLTFOverlayOptionsM37clone);
            return gLTFOverlay;
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final AMap3DModelTileOverlay add3DModelTileOverlay(AMap3DModelTileOverlayOptions aMap3DModelTileOverlayOptions) throws RemoteException {
        try {
            resetRenderTime();
            String strCreateId = this.D.createId("AMAP3DMODELTILE");
            AMap3DModelTileOverlay aMap3DModelTileOverlay = new AMap3DModelTileOverlay(this.D, aMap3DModelTileOverlayOptions, strCreateId);
            this.D.addOverlayObject(strCreateId, aMap3DModelTileOverlay, aMap3DModelTileOverlayOptions);
            return aMap3DModelTileOverlay;
        } catch (Throwable th) {
            dx.a(th);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final ep addContourLineOverlay(eo eoVar) {
        try {
            String strCreateId = this.D.createId("CONTOURLINE");
            ep epVar = new ep(this.D, eoVar, strCreateId);
            this.D.addOverlayObject(strCreateId, epVar, eoVar);
            return epVar;
        } catch (Exception e) {
            dx.a(e);
            return null;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void clear() throws RemoteException {
        try {
            clear(false);
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "clear");
            dx.a(th);
            th.printStackTrace();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void clear(boolean z) throws RemoteException {
        try {
            hideInfoWindow();
            String strD = null;
            String strE = "";
            cl clVar = this.K;
            if (clVar != null) {
                if (z) {
                    strD = clVar.d();
                    strE = this.K.e();
                } else {
                    clVar.f();
                }
            }
            this.D.clear(strD, strE);
            queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.20
                @Override // java.lang.Runnable
                public final void run() {
                    if (l.this.f == null || l.this.G.get()) {
                        return;
                    }
                    l.this.f.removeNativeAllOverlay(l.this.F);
                }
            });
            resetRenderTime();
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "clear");
            dx.a(th);
            th.printStackTrace();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final int getMapType() throws RemoteException {
        return this.Y;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMapType(int i) throws RemoteException {
        MapConfig mapConfig;
        if (i != this.Y || ((mapConfig = this.b) != null && mapConfig.isCustomStyleEnable())) {
            this.Y = i;
            h(i);
        }
    }

    private void h(int i) {
        int i2;
        this.Y = i;
        int i3 = 3;
        if (i != 1) {
            if (i == 2) {
                i3 = 1;
            } else if (i == 3) {
                i2 = 1;
                i3 = 0;
            } else if (i == 4) {
                i3 = 2;
            } else if (i == 6) {
                i3 = 2;
                i2 = 1;
            } else if (i != 5) {
                try {
                    this.Y = 1;
                    i2 = 0;
                    i3 = 0;
                } catch (Throwable th) {
                    jw.c(th, "AMapDelegateImp", "setMaptype");
                    th.printStackTrace();
                    dx.a(th);
                    return;
                }
            }
            i2 = 0;
        } else {
            i2 = 0;
            i3 = 0;
        }
        this.b.setMapStyleMode(i3);
        this.b.setMapStyleTime(i2);
        if (this.b.isCustomStyleEnable()) {
            k kVar = this.aH;
            if (kVar != null && kVar.d()) {
                this.aH.e();
            } else {
                a(this.F, i3, i2);
                this.b.setCustomStyleEnable(false);
            }
            this.z.setLogoEnable(true);
        } else {
            if (this.b.getMapLanguage().equals("en")) {
                setMapLanguage("zh_cn");
            }
            a(this.F, i3, i2);
        }
        resetRenderTime();
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final boolean isTrafficEnabled() throws RemoteException {
        return this.b.isTrafficEnabled();
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setTrafficEnabled(final boolean z) throws RemoteException {
        try {
            if (this.aw && !this.G.get()) {
                queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.22
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            if (l.this.b.isTrafficEnabled() != z) {
                                l.this.b.setTrafficEnabled(z);
                                l.this.an.setTrafficMode(z);
                                l.this.f.setTrafficEnable(l.this.F, z);
                                l.this.resetRenderTime();
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                            dx.a(th);
                        }
                    }
                });
                return;
            }
            this.aJ.c = z;
            this.aJ.b = true;
            this.aJ.f = this.F;
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final boolean isIndoorEnabled() throws RemoteException {
        return this.b.isIndoorEnable();
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setIndoorEnabled(final boolean z) throws RemoteException {
        List listA;
        try {
            if (this.aw && !this.G.get()) {
                this.b.setIndoorEnable(z);
                resetRenderTime();
                if (z) {
                    GLMapEngine gLMapEngine = this.f;
                    if (gLMapEngine != null) {
                        gLMapEngine.setIndoorEnable(this.F, true);
                    }
                } else {
                    GLMapEngine gLMapEngine2 = this.f;
                    if (gLMapEngine2 != null) {
                        gLMapEngine2.setIndoorEnable(this.F, false);
                    }
                    MapConfig mapConfig = this.b;
                    mapConfig.maxZoomLevel = mapConfig.isSetLimitZoomLevel() ? this.b.getMaxZoomLevel() : 20.0f;
                    try {
                        if (this.z.isZoomControlsEnabled() && (listA = this.u.a(AMapWidgetListener.class.hashCode())) != null && listA.size() > 0) {
                            synchronized (listA) {
                                for (int i = 0; i < listA.size(); i++) {
                                    ((AMapWidgetListener) listA.get(i)).invalidateZoomController(this.b.getSZ());
                                }
                            }
                        }
                    } catch (Throwable unused) {
                    }
                }
                du.c(this.e, z);
                if (this.z.isIndoorSwitchEnabled()) {
                    this.j.post(new Runnable() { // from class: com.amap.api.col.3sl.l.23
                        @Override // java.lang.Runnable
                        public final void run() {
                            if (!z) {
                                if (l.this.C != null) {
                                    l.this.C.i(Boolean.FALSE);
                                    return;
                                }
                                return;
                            }
                            l.this.showIndoorSwitchControlsEnabled(true);
                        }
                    });
                    return;
                }
                return;
            }
            this.aV.c = z;
            this.aV.b = true;
            this.aV.f = this.F;
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void set3DBuildingEnabled(boolean z) throws RemoteException {
        try {
            c(this.F);
            a(this.F, z);
            d(this.F);
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final boolean isMyLocationEnabled() throws RemoteException {
        return this.E;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMyLocationEnabled(boolean z) throws RemoteException {
        if (this.G.get()) {
            return;
        }
        try {
            ei eiVar = this.C;
            if (eiVar != null) {
                LocationSource locationSource = this.L;
                if (locationSource == null) {
                    eiVar.h(Boolean.FALSE);
                } else if (z) {
                    locationSource.activate(this.t);
                    this.C.h(Boolean.TRUE);
                    if (this.K == null) {
                        this.K = new cl(this, this.e);
                    }
                } else {
                    cl clVar = this.K;
                    if (clVar != null) {
                        clVar.c();
                        this.K = null;
                    }
                    this.L.deactivate();
                }
            }
            if (!z) {
                this.z.setMyLocationButtonEnabled(z);
            }
            this.E = z;
            resetRenderTime();
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "setMyLocationEnabled");
            th.printStackTrace();
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setLoadOfflineData(final boolean z) throws RemoteException {
        queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.24
            @Override // java.lang.Runnable
            public final void run() {
                if (l.this.f != null) {
                    l.this.f.setOfflineDataEnable(l.this.F, z);
                }
            }
        });
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMyLocationStyle(MyLocationStyle myLocationStyle) throws RemoteException {
        if (this.G.get()) {
            return;
        }
        try {
            if (this.K == null) {
                this.K = new cl(this, this.e);
            }
            if (this.K != null) {
                if (myLocationStyle.getInterval() < 1000) {
                    myLocationStyle.interval(1000L);
                }
                LocationSource locationSource = this.L;
                if (locationSource != null && (locationSource instanceof aw)) {
                    ((aw) locationSource).a(myLocationStyle.getInterval());
                    ((aw) this.L).a(myLocationStyle.getMyLocationType());
                }
                this.K.a(myLocationStyle);
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMyLocationType(int i) throws RemoteException {
        try {
            cl clVar = this.K;
            if (clVar == null || clVar.a() == null) {
                return;
            }
            this.K.a().myLocationType(i);
            setMyLocationStyle(this.K.a());
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final List<Marker> getMapScreenMarkers() throws RemoteException {
        if (!dx.a(getMapWidth(), getMapHeight())) {
            return new ArrayList();
        }
        if (this.G.get()) {
            return new ArrayList();
        }
        return this.D.getMapScreenMarkers();
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMapTextEnable(final boolean z) throws RemoteException {
        try {
            if (this.aw && this.ax) {
                resetRenderTime();
                queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.25
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            l.this.f.setLabelEnable(l.this.F, z);
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                });
            } else {
                this.aO.c = z;
                this.aO.b = true;
                this.aO.f = this.F;
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setRoadArrowEnable(final boolean z) throws RemoteException {
        try {
            if (this.aw && this.ax) {
                resetRenderTime();
                queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.26
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            l.this.f.setRoadArrowEnable(l.this.F, z);
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                });
            } else {
                this.aP.c = z;
                this.aP.b = true;
                this.aP.f = this.F;
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setNaviLabelEnable(final boolean z, final int i, final int i2) throws RemoteException {
        try {
            if (this.aw && this.ax) {
                resetRenderTime();
                queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.27
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            l.this.f.setNaviLabelEnable(l.this.F, z, i, i2);
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                });
                return;
            }
            this.aQ.c = z;
            this.aQ.g = i;
            this.aQ.h = i2;
            this.aQ.b = true;
            this.aQ.f = this.F;
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setConstructingRoadEnable(final boolean z) {
        try {
            if (this.aw && this.ax) {
                resetRenderTime();
                queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.28
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            l.this.f.setMapOpenLayerEnable(z);
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                });
            } else {
                this.aR.c = z;
                this.aR.b = true;
                this.aR.f = this.F;
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setTrafficStyleWithTexture(final byte[] bArr, final MyTrafficStyle myTrafficStyle) {
        if (this.G.get()) {
            return;
        }
        try {
            if (this.aw && this.ax && bArr != null && myTrafficStyle != null) {
                resetRenderTime();
                queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.29
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            l.this.f.setTrafficStyleWithTexture(l.this.F, bArr, myTrafficStyle);
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                });
                return;
            }
            this.aT.i = bArr;
            this.aT.j = myTrafficStyle;
            this.aT.b = true;
            this.aT.f = this.F;
        } catch (Exception e) {
            dx.a(e);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setTrafficStyleWithTextureData(final byte[] bArr) {
        if (this.G.get()) {
            return;
        }
        try {
            if (this.aw && this.ax && bArr != null) {
                resetRenderTime();
                queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.30
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            l.this.f.setTrafficStyleWithTextureData(l.this.F, bArr);
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                });
            } else {
                this.aS.i = bArr;
                this.aS.b = true;
                this.aS.f = this.F;
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Location getMyLocation() throws RemoteException {
        if (this.L != null) {
            return this.t.a;
        }
        return null;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setLocationSource(LocationSource locationSource) throws RemoteException {
        try {
            if (this.G.get()) {
                return;
            }
            LocationSource locationSource2 = this.L;
            if (locationSource2 != null && (locationSource2 instanceof aw)) {
                locationSource2.deactivate();
            }
            this.L = locationSource;
            if (locationSource != null) {
                this.C.h(Boolean.TRUE);
            } else {
                this.C.h(Boolean.FALSE);
            }
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "setLocationSource");
            th.printStackTrace();
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMyLocationRotateAngle(float f) throws RemoteException {
        try {
            cl clVar = this.K;
            if (clVar != null) {
                clVar.a(f);
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final UiSettings getAMapUiSettings() throws RemoteException {
        if (this.x == null) {
            this.x = new UiSettings(this.z);
        }
        return this.x;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Projection getAMapProjection() throws RemoteException {
        return new Projection(this.y);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnMapClickListener(AMap.OnMapClickListener onMapClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnMapClickListener.class.hashCode(), onMapClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnMapTouchListener(AMap.OnMapTouchListener onMapTouchListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnMapTouchListener.class.hashCode(), onMapTouchListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnPOIClickListener(AMap.OnPOIClickListener onPOIClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnPOIClickListener.class.hashCode(), onPOIClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnMapLongClickListener(AMap.OnMapLongClickListener onMapLongClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnMapLongClickListener.class.hashCode(), onMapLongClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnMarkerClickListener(AMap.OnMarkerClickListener onMarkerClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnMarkerClickListener.class.hashCode(), onMarkerClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnPolylineClickListener(AMap.OnPolylineClickListener onPolylineClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnPolylineClickListener.class.hashCode(), onPolylineClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnMarkerDragListener(AMap.OnMarkerDragListener onMarkerDragListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnMarkerDragListener.class.hashCode(), onMarkerDragListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnMaploadedListener(AMap.OnMapLoadedListener onMapLoadedListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnMapLoadedListener.class.hashCode(), onMapLoadedListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnCameraChangeListener(AMap.OnCameraChangeListener onCameraChangeListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnCameraChangeListener.class.hashCode(), onCameraChangeListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnInfoWindowClickListener(AMap.OnInfoWindowClickListener onInfoWindowClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnInfoWindowClickListener.class.hashCode(), onInfoWindowClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnIndoorBuildingActiveListener(AMap.OnIndoorBuildingActiveListener onIndoorBuildingActiveListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnIndoorBuildingActiveListener.class.hashCode(), onIndoorBuildingActiveListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnMyLocationChangeListener(AMap.OnMyLocationChangeListener onMyLocationChangeListener) {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(AMap.OnMyLocationChangeListener.class.hashCode(), onMyLocationChangeListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setInfoWindowAdapter(AMap.InfoWindowAdapter infoWindowAdapter) throws RemoteException {
        av avVar;
        if (this.G.get() || (avVar = this.w) == null) {
            return;
        }
        avVar.a(infoWindowAdapter);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setInfoWindowAdapter(AMap.CommonInfoWindowAdapter commonInfoWindowAdapter) throws RemoteException {
        av avVar;
        if (this.G.get() || (avVar = this.w) == null) {
            return;
        }
        avVar.a(commonInfoWindowAdapter);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setOnMultiPointClickListener(AMap.OnMultiPointClickListener onMultiPointClickListener) {
        this.aG = onMultiPointClickListener;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final String getMapContentApprovalNumber() {
        MapConfig mapConfig = this.b;
        if (mapConfig == null || mapConfig.isCustomStyleEnable()) {
            return null;
        }
        du.d(this.e);
        String strA = dn.a(this.e, "approval_number", "mc", "");
        return !TextUtils.isEmpty(strA) ? strA : "GS (2023)551号 | GS (2023)2175号";
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final String getSatelliteImageApprovalNumber() {
        du.e(this.e);
        String strA = dn.a(this.e, "approval_number", "si", "");
        return !TextUtils.isEmpty(strA) ? strA : "GS (2023)4047号";
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final String getTerrainApprovalNumber() {
        du.f(this.e);
        String strA = dn.a(this.e, "approval_number", "te", "");
        return !TextUtils.isEmpty(strA) ? strA : "GS(2021)6352号";
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMapLanguage(String str) {
        MapConfig mapConfig;
        if (TextUtils.isEmpty(str) || (mapConfig = this.b) == null || mapConfig.isCustomStyleEnable() || this.b.getMapLanguage().equals(str)) {
            return;
        }
        if (!str.equals("en")) {
            this.b.setMapLanguage("zh_cn");
            this.af = 0;
        } else {
            if (this.Y != 1) {
                try {
                    setMapType(1);
                } catch (Throwable th) {
                    dx.a(th);
                    th.printStackTrace();
                }
            }
            this.b.setMapLanguage("en");
            this.af = -10000;
        }
        try {
            b(getCameraPosition());
        } catch (Throwable th2) {
            dx.a(th2);
            th2.printStackTrace();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setWorldVectorMapStyle(String str) {
        if (a(false, true) || TextUtils.isEmpty(str) || this.b == null || this.ba.equals(str)) {
            return;
        }
        this.ba = str;
        com.autonavi.extra.b bVar = this.aY;
        if (bVar != null) {
            bVar.i();
        }
        resetRenderTime();
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final String getWorldVectorMapStyle() {
        return this.ba;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final String getCurrentWorldVectorMapStyle() {
        try {
            com.autonavi.extra.b bVar = this.aY;
            if (bVar == null) {
                return "";
            }
            Object objJ = bVar.j();
            return objJ instanceof String ? (String) objJ : "";
        } catch (Throwable th) {
            dx.a(th);
            return "";
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void accelerateNetworkInChinese(boolean z) {
        com.autonavi.extra.b bVar = this.aY;
        if (bVar != null) {
            Boolean.valueOf(z);
            bVar.i();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final String getWorldVectorMapLanguage() {
        return this.aZ;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void getMapPrintScreen(AMap.onMapPrintScreenListener onmapprintscreenlistener) {
        try {
            this.u.a(Integer.valueOf(AMap.onMapPrintScreenListener.class.hashCode()), onmapprintscreenlistener);
            this.U = 20;
            this.V = true;
            resetRenderTime();
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void getMapScreenShot(AMap.OnMapScreenShotListener onMapScreenShotListener, boolean z) {
        try {
            this.u.a(Integer.valueOf(AMap.OnMapScreenShotListener.class.hashCode()), onMapScreenShotListener);
            this.U = 20;
            this.V = z;
            resetRenderTime();
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final float getScalePerPixel() throws RemoteException {
        try {
            return ((float) ((((Math.cos((getCameraPosition().target.latitude * 3.141592653589793d) / 180.0d) * 2.0d) * 3.141592653589793d) * 6378137.0d) / (Math.pow(2.0d, getZoomLevel()) * 256.0d))) * getMapZoomScale();
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "getScalePerPixel");
            dx.a(th);
            th.printStackTrace();
            return 0.0f;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setRunLowFrame(boolean z) {
        if (z) {
            return;
        }
        try {
            if (this.ai == -1) {
                m();
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removecache() throws RemoteException {
        removecache(null);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removecache(AMap.OnCacheRemoveListener onCacheRemoveListener) throws RemoteException {
        if (this.j == null || this.f == null) {
            return;
        }
        try {
            d dVar = new d(this.e, onCacheRemoveListener);
            this.j.removeCallbacks(dVar);
            this.j.post(dVar);
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "removecache");
            th.printStackTrace();
            dx.a(th);
        }
    }

    /* JADX INFO: compiled from: AMapDelegateImp.java */
    private class d implements Runnable {
        private Context b;
        private AMap.OnCacheRemoveListener c;

        public d(Context context, AMap.OnCacheRemoveListener onCacheRemoveListener) {
            this.b = context;
            this.c = onCacheRemoveListener;
        }

        /* JADX WARN: Removed duplicated region for block: B:14:0x0033  */
        /* JADX WARN: Removed duplicated region for block: B:19:0x003d  */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final void run() {
            /*
                r6 = this;
                r0 = 0
                r1 = 1
                android.content.Context r2 = r6.b     // Catch: java.lang.Throwable -> L62
                android.content.Context r2 = r2.getApplicationContext()     // Catch: java.lang.Throwable -> L62
                java.lang.String r3 = com.amap.api.col.p0003sl.dx.c(r2)     // Catch: java.lang.Throwable -> L62
                java.lang.String r4 = com.amap.api.col.p0003sl.dx.a(r2)     // Catch: java.lang.Throwable -> L62
                java.io.File r5 = new java.io.File     // Catch: java.lang.Throwable -> L62
                r5.<init>(r3)     // Catch: java.lang.Throwable -> L62
                boolean r3 = r5.exists()     // Catch: java.lang.Throwable -> L62
                if (r3 == 0) goto L20
                boolean r3 = com.autonavi.base.amap.mapcore.FileUtil.deleteFile(r5)     // Catch: java.lang.Throwable -> L62
                goto L21
            L20:
                r3 = r1
            L21:
                if (r3 == 0) goto L33
                java.io.File r5 = new java.io.File     // Catch: java.lang.Throwable -> L30
                r5.<init>(r4)     // Catch: java.lang.Throwable -> L30
                boolean r3 = com.autonavi.base.amap.mapcore.FileUtil.deleteFile(r5)     // Catch: java.lang.Throwable -> L30
                if (r3 == 0) goto L33
                r3 = r1
                goto L34
            L30:
                r2 = move-exception
                r1 = r3
                goto L63
            L33:
                r3 = r0
            L34:
                if (r3 == 0) goto L3d
                boolean r2 = com.amap.api.col.p0003sl.dx.e(r2)     // Catch: java.lang.Throwable -> L30
                if (r2 == 0) goto L3d
                goto L3e
            L3d:
                r1 = r0
            L3e:
                com.amap.api.col.3sl.l r2 = com.amap.api.col.p0003sl.l.this     // Catch: java.lang.Throwable -> L62
                com.amap.api.maps.interfaces.IGlOverlayLayer r2 = com.amap.api.col.p0003sl.l.g(r2)     // Catch: java.lang.Throwable -> L62
                if (r2 == 0) goto L4f
                com.amap.api.col.3sl.l r2 = com.amap.api.col.p0003sl.l.this     // Catch: java.lang.Throwable -> L62
                com.amap.api.maps.interfaces.IGlOverlayLayer r2 = com.amap.api.col.p0003sl.l.g(r2)     // Catch: java.lang.Throwable -> L62
                r2.clearTileCache()     // Catch: java.lang.Throwable -> L62
            L4f:
                com.amap.api.col.3sl.l r0 = com.amap.api.col.p0003sl.l.this     // Catch: java.lang.Throwable -> L5d
                com.autonavi.base.ae.gmap.GLMapEngine r0 = r0.f     // Catch: java.lang.Throwable -> L5d
                if (r0 == 0) goto L5c
                com.amap.api.maps.AMap$OnCacheRemoveListener r0 = r6.c     // Catch: java.lang.Throwable -> L5d
                if (r0 == 0) goto L5c
                r0.onRemoveCacheFinish(r1)     // Catch: java.lang.Throwable -> L5d
            L5c:
                return
            L5d:
                r0 = move-exception
                r0.printStackTrace()
                return
            L62:
                r2 = move-exception
            L63:
                com.amap.api.col.p0003sl.dx.a(r2)     // Catch: java.lang.Throwable -> L80
                java.lang.String r3 = "AMapDelegateImp"
                java.lang.String r4 = "RemoveCacheRunnable"
                com.amap.api.col.p0003sl.jw.c(r2, r3, r4)     // Catch: java.lang.Throwable -> L80
                com.amap.api.col.3sl.l r1 = com.amap.api.col.p0003sl.l.this     // Catch: java.lang.Throwable -> L7b
                com.autonavi.base.ae.gmap.GLMapEngine r1 = r1.f     // Catch: java.lang.Throwable -> L7b
                if (r1 == 0) goto L7a
                com.amap.api.maps.AMap$OnCacheRemoveListener r1 = r6.c     // Catch: java.lang.Throwable -> L7b
                if (r1 == 0) goto L7a
                r1.onRemoveCacheFinish(r0)     // Catch: java.lang.Throwable -> L7b
            L7a:
                return
            L7b:
                r0 = move-exception
                r0.printStackTrace()
                return
            L80:
                r0 = move-exception
                com.amap.api.col.3sl.l r2 = com.amap.api.col.p0003sl.l.this     // Catch: java.lang.Throwable -> L8f
                com.autonavi.base.ae.gmap.GLMapEngine r2 = r2.f     // Catch: java.lang.Throwable -> L8f
                if (r2 == 0) goto L93
                com.amap.api.maps.AMap$OnCacheRemoveListener r2 = r6.c     // Catch: java.lang.Throwable -> L8f
                if (r2 == 0) goto L93
                r2.onRemoveCacheFinish(r1)     // Catch: java.lang.Throwable -> L8f
                goto L93
            L8f:
                r1 = move-exception
                r1.printStackTrace()
            L93:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.3sl.l.d.run():void");
        }

        public final boolean equals(Object obj) {
            return obj instanceof d;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setCustomRenderer(CustomRenderer customRenderer) throws RemoteException {
        this.ag = customRenderer;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setCenterToPixel(int i, int i2) throws RemoteException {
        this.I = true;
        this.aC = i;
        this.aD = i2;
        if (this.ax && this.aw) {
            if (this.b.getAnchorX() == this.aC && this.b.getAnchorY() == this.aD) {
                return;
            }
            this.b.setAnchorX(this.aC);
            this.b.setAnchorY(this.aD);
            queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.31
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        l.this.b.setAnchorX(Math.max(0, Math.min(l.this.aC, l.this.g)));
                        l.this.b.setAnchorY(Math.max(0, Math.min(l.this.aD, l.this.h)));
                        l.this.f.setProjectionCenter(l.this.F, l.this.b.getAnchorX(), l.this.b.getAnchorY());
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMapTextZIndex(int i) throws RemoteException {
        this.af = i;
        this.i = false;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final int getMapTextZIndex() throws RemoteException {
        return this.af;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setRenderFps(int i) {
        try {
            if (i == -1) {
                this.ai = i;
            } else {
                this.ai = Math.max(10, Math.min(i, 40));
            }
            du.g(this.e);
        } catch (Throwable th) {
            dx.a(th);
            th.printStackTrace();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setIndoorBuildingInfo(IndoorBuildingInfo indoorBuildingInfo) throws RemoteException {
        if (this.G.get() || indoorBuildingInfo == null || indoorBuildingInfo.activeFloorName == null || indoorBuildingInfo.poiid == null) {
            return;
        }
        this.c = (au) indoorBuildingInfo;
        resetRenderTime();
        queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.33
            @Override // java.lang.Runnable
            public final void run() {
                if (l.this.f != null) {
                    l.this.f.setIndoorBuildingToBeActive(l.this.F, l.this.c.activeFloorName, l.this.c.activeFloorIndex, l.this.c.poiid);
                }
            }
        });
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setAMapGestureListener(AMapGestureListener aMapGestureListener) {
        z zVar = this.ao;
        if (zVar != null) {
            this.v = aMapGestureListener;
            zVar.a(aMapGestureListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final float getZoomToSpanLevel(LatLng latLng, LatLng latLng2) {
        try {
            MapConfig mapConfig = getMapConfig();
            if (latLng != null && latLng2 != null && this.aw && !this.G.get()) {
                Pair<Float, IPoint> pairA = dx.a(mapConfig, 0, 0, 0, 0, new LatLngBounds.Builder().include(latLng).include(latLng2).build(), getMapWidth(), getMapHeight());
                if (pairA != null) {
                    return pairA.first.floatValue();
                }
                GLMapState gLMapState = new GLMapState(this.F, this.f.getNativeInstance());
                float mapZoomer = gLMapState.getMapZoomer();
                gLMapState.recycle();
                return mapZoomer;
            }
            return mapConfig.getSZ();
        } catch (Throwable th) {
            dx.a(th);
            return 0.0f;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Pair<Float, LatLng> calculateZoomToSpanLevel(int i, int i2, int i3, int i4, LatLng latLng, LatLng latLng2) {
        if (latLng != null && latLng2 != null && i == i2 && i2 == i3 && i3 == i4 && latLng.latitude == latLng2.latitude && latLng.longitude == latLng2.longitude) {
            return new Pair<>(Float.valueOf(getMaxZoomLevel()), latLng);
        }
        MapConfig mapConfig = getMapConfig();
        if (latLng != null && latLng2 != null && this.aw && !this.G.get()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(latLng);
            builder.include(latLng2);
            GLMapState gLMapState = new GLMapState(this.F, this.f.getNativeInstance());
            Pair<Float, IPoint> pairA = dx.a(mapConfig, i, i2, i3, i4, builder.build(), getMapWidth(), getMapHeight());
            gLMapState.recycle();
            if (pairA == null) {
                return null;
            }
            DPoint dPointObtain = DPoint.obtain();
            GLMapState.geo2LonLat(pairA.second.x, pairA.second.y, dPointObtain);
            Pair<Float, LatLng> pair = new Pair<>(pairA.first, new LatLng(dPointObtain.y, dPointObtain.x));
            dPointObtain.recycle();
            return pair;
        }
        DPoint dPointObtain2 = DPoint.obtain();
        GLMapState.geo2LonLat((int) mapConfig.getSX(), (int) mapConfig.getSY(), dPointObtain2);
        Pair<Float, LatLng> pair2 = new Pair<>(Float.valueOf(mapConfig.getSZ()), new LatLng(dPointObtain2.y, dPointObtain2.x));
        dPointObtain2.recycle();
        return pair2;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMaxZoomLevel(float f) {
        this.b.setMaxZoomLevel(f);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMinZoomLevel(float f) {
        this.b.setMinZoomLevel(f);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void resetMinMaxZoomPreference() {
        List listA;
        this.b.resetMinMaxZoomPreference();
        try {
            if (!this.z.isZoomControlsEnabled() || !this.b.isNeedUpdateZoomControllerState() || (listA = this.u.a(AMapWidgetListener.class.hashCode())) == null || listA.size() <= 0) {
                return;
            }
            synchronized (listA) {
                for (int i = 0; i < listA.size(); i++) {
                    ((AMapWidgetListener) listA.get(i)).invalidateZoomController(this.b.getSZ());
                }
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMapStatusLimits(LatLngBounds latLngBounds) {
        try {
            this.b.setLimitLatLngBounds(latLngBounds);
            p();
        } catch (Throwable th) {
            th.printStackTrace();
            dx.a(th);
        }
    }

    private static boolean a(LatLngBounds latLngBounds) {
        return (latLngBounds == null || latLngBounds.northeast == null || latLngBounds.southwest == null) ? false : true;
    }

    private void p() {
        try {
            LatLngBounds limitLatLngBounds = this.b.getLimitLatLngBounds();
            if (this.f != null && a(limitLatLngBounds)) {
                GLMapState gLMapState = new GLMapState(this.F, this.f.getNativeInstance());
                IPoint iPointObtain = IPoint.obtain();
                GLMapState.lonlat2Geo(limitLatLngBounds.northeast.longitude, limitLatLngBounds.northeast.latitude, iPointObtain);
                IPoint iPointObtain2 = IPoint.obtain();
                GLMapState.lonlat2Geo(limitLatLngBounds.southwest.longitude, limitLatLngBounds.southwest.latitude, iPointObtain2);
                this.b.setLimitIPoints(new IPoint[]{iPointObtain, iPointObtain2});
                gLMapState.recycle();
                return;
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        this.b.setLimitIPoints(null);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final Handler getMainHandler() {
        return this.j;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void onChangeFinish() {
        Message messageObtainMessage = this.j.obtainMessage();
        messageObtainMessage.what = 11;
        this.j.sendMessage(messageObtainMessage);
    }

    protected final void a(CameraPosition cameraPosition) {
        MapConfig mapConfig = this.b;
        if (mapConfig == null || mapConfig.getChangedCounter() == 0) {
            return;
        }
        try {
            if (!this.au && this.f.getAnimateionsCount() == 0 && this.f.getStateMessageCount() == 0) {
                AMapGestureListener aMapGestureListener = this.v;
                if (aMapGestureListener != null) {
                    aMapGestureListener.onMapStable();
                }
                if (this.B.isEnabled()) {
                    try {
                        List listA = this.u.a(AMap.OnCameraChangeListener.class.hashCode());
                        if (listA != null && listA.size() != 0) {
                            if (cameraPosition == null) {
                                try {
                                    cameraPosition = getCameraPosition();
                                } catch (Throwable th) {
                                    jw.c(th, "AMapDelegateImp", "cameraChangeFinish");
                                    th.printStackTrace();
                                }
                            }
                            synchronized (listA) {
                                Iterator it = listA.iterator();
                                while (it.hasNext()) {
                                    ((AMap.OnCameraChangeListener) it.next()).onCameraChangeFinish(cameraPosition);
                                }
                            }
                        }
                    } catch (Throwable unused) {
                    }
                    this.b.resetChangedCounter();
                }
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
            dx.a(th2);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setZoomScaleParam(float f) {
        this.ar = f;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void onFling() {
        IGlOverlayLayer iGlOverlayLayer = this.D;
        if (iGlOverlayLayer != null) {
            iGlOverlayLayer.setFlingState(true);
        }
        this.T = true;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final int getMapWidth() {
        return this.g;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final int getMapHeight() {
        return this.h;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final float getCameraAngle() {
        return getCameraDegree(this.F);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final float getSkyHeight() {
        return this.b.getSkyHeight();
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final boolean isMaploaded() {
        return this.J;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final MapConfig getMapConfig() {
        return this.b;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final View getView() throws RemoteException {
        ei eiVar = this.C;
        if (eiVar != null) {
            return eiVar.j();
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x00bf  */
    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void onIndoorBuildingActivity(int r8, byte[] r9) {
        /*
            Method dump skipped, instruction units count: 211
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.l.onIndoorBuildingActivity(int, byte[]):void");
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void destroy() {
        this.G.set(true);
        dz.a(dy.c, "destroy map");
        try {
            LocationSource locationSource = this.L;
            if (locationSource != null) {
                locationSource.deactivate();
            }
            this.L = null;
            this.aE = null;
            GLMapRender gLMapRender = this.an;
            if (gLMapRender != null) {
                gLMapRender.renderPause();
            }
            h();
            Thread thread = this.aa;
            if (thread != null) {
                thread.interrupt();
                this.aa = null;
            }
            Thread thread2 = this.ab;
            if (thread2 != null) {
                thread2.interrupt();
                this.ab = null;
            }
            v vVar = this.ac;
            if (vVar != null) {
                vVar.a();
                this.ac = null;
            }
            cs csVar = this.ak;
            if (csVar != null) {
                csVar.a();
                this.ak = null;
            }
            cu cuVar = this.al;
            if (cuVar != null) {
                cuVar.a((cu.a) null);
                this.al.a();
                this.al = null;
            }
            dl.b();
            GLMapEngine gLMapEngine = this.f;
            if (gLMapEngine != null) {
                gLMapEngine.setMapListener(null);
                queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.35
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            if (l.this.D != null) {
                                l.this.D.destroy();
                            }
                            l lVar = l.this;
                            lVar.destroySurface(lVar.F);
                        } catch (Throwable th) {
                            th.printStackTrace();
                            dx.a(th);
                        }
                    }
                });
                int i = 0;
                while (this.f != null) {
                    int i2 = i + 1;
                    if (i >= 50) {
                        break;
                    }
                    try {
                        Thread.sleep(20L);
                    } catch (InterruptedException e) {
                        dx.a(e);
                    }
                    i = i2;
                }
            }
            IGLSurfaceView iGLSurfaceView = this.B;
            if (iGLSurfaceView != null) {
                try {
                    iGLSurfaceView.onDetachedGLThread();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    dx.a(e2);
                }
            }
            ei eiVar = this.C;
            if (eiVar != null) {
                eiVar.g();
                this.C = null;
            }
            cl clVar = this.K;
            if (clVar != null) {
                clVar.c();
                this.K = null;
            }
            this.L = null;
            this.t = null;
            q();
            this.Z = null;
            dz.a();
            jw.b();
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImp", "destroy");
            dx.a(th);
            th.printStackTrace();
        }
    }

    private void q() {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a();
        }
    }

    /* JADX INFO: compiled from: AMapDelegateImp.java */
    private class c implements ef.a {
        private c() {
        }

        /* synthetic */ c(l lVar, byte b) {
            this();
        }

        @Override // com.amap.api.col.3sl.ef.a
        public final void a(int i) {
            if (l.this.c != null) {
                l.this.c.activeFloorIndex = l.this.c.floor_indexs[i];
                l.this.c.activeFloorName = l.this.c.floor_names[i];
                try {
                    l lVar = l.this;
                    lVar.setIndoorBuildingInfo(lVar.c);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* JADX INFO: compiled from: AMapDelegateImp.java */
    class b {
        b() {
        }

        public final void a(au auVar) {
            List listA;
            List listA2;
            if (l.this.b == null || !l.this.b.isIndoorEnable()) {
                return;
            }
            final ef efVarE = l.this.C.e();
            if (auVar == null) {
                try {
                    List listA3 = l.this.u.a(AMap.OnIndoorBuildingActiveListener.class.hashCode());
                    if (listA3 != null && listA3.size() > 0) {
                        synchronized (listA3) {
                            for (int i = 0; i < listA3.size(); i++) {
                                ((AMap.OnIndoorBuildingActiveListener) listA3.get(i)).OnIndoorBuilding(auVar);
                            }
                        }
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                if (l.this.c != null) {
                    l.this.c.g = null;
                }
                if (efVarE.b()) {
                    l.this.j.post(new Runnable() { // from class: com.amap.api.col.3sl.l.b.1
                        @Override // java.lang.Runnable
                        public final void run() {
                            efVarE.a(false);
                        }
                    });
                }
                l.this.b.maxZoomLevel = l.this.b.isSetLimitZoomLevel() ? l.this.b.getMaxZoomLevel() : 20.0f;
                try {
                    if (!l.this.z.isZoomControlsEnabled() || (listA = l.this.u.a(AMapWidgetListener.class.hashCode())) == null || listA.size() <= 0) {
                        return;
                    }
                    synchronized (listA) {
                        for (int i2 = 0; i2 < listA.size(); i2++) {
                            ((AMapWidgetListener) listA.get(i2)).invalidateZoomController(l.this.b.getSZ());
                        }
                    }
                    return;
                } catch (Throwable th2) {
                    th2.printStackTrace();
                }
            }
            if (auVar != null && auVar.floor_indexs != null && auVar.floor_names != null && auVar.floor_indexs.length == auVar.floor_names.length) {
                int i3 = 0;
                while (true) {
                    if (i3 >= auVar.floor_indexs.length) {
                        break;
                    }
                    if (auVar.activeFloorIndex == auVar.floor_indexs[i3]) {
                        auVar.activeFloorName = auVar.floor_names[i3];
                        break;
                    }
                    i3++;
                }
            }
            if (auVar == null || l.this.c == null || l.this.c.activeFloorIndex == auVar.activeFloorIndex || !efVarE.b()) {
                if (auVar != null && (l.this.c == null || !l.this.c.poiid.equals(auVar.poiid) || l.this.c.g == null)) {
                    l.this.c = auVar;
                    if (l.this.b != null) {
                        if (l.this.c.g == null) {
                            l.this.c.g = new Point();
                        }
                        DPoint mapGeoCenter = l.this.b.getMapGeoCenter();
                        if (mapGeoCenter != null) {
                            l.this.c.g.x = (int) mapGeoCenter.x;
                            l.this.c.g.y = (int) mapGeoCenter.y;
                        }
                    }
                }
                try {
                    List listA4 = l.this.u.a(AMap.OnIndoorBuildingActiveListener.class.hashCode());
                    if (listA4 != null && listA4.size() > 0) {
                        synchronized (listA4) {
                            for (int i4 = 0; i4 < listA4.size(); i4++) {
                                ((AMap.OnIndoorBuildingActiveListener) listA4.get(i4)).OnIndoorBuilding(auVar);
                            }
                        }
                    }
                    l.this.b.maxZoomLevel = l.this.b.isSetLimitZoomLevel() ? l.this.b.getMaxZoomLevel() : 20.0f;
                    if (l.this.z.isZoomControlsEnabled() && (listA2 = l.this.u.a(AMapWidgetListener.class.hashCode())) != null && listA2.size() > 0) {
                        synchronized (listA2) {
                            for (int i5 = 0; i5 < listA2.size(); i5++) {
                                ((AMapWidgetListener) listA2.get(i5)).invalidateZoomController(l.this.b.getSZ());
                            }
                        }
                    }
                    if (!l.this.z.isIndoorSwitchEnabled()) {
                        if (l.this.z.isIndoorSwitchEnabled() || !efVarE.b()) {
                            return;
                        }
                        l.this.z.setIndoorSwitchEnabled(false);
                        return;
                    }
                    if (!efVarE.b()) {
                        l.this.z.setIndoorSwitchEnabled(true);
                    }
                    l.this.j.post(new Runnable() { // from class: com.amap.api.col.3sl.l.b.2
                        @Override // java.lang.Runnable
                        public final void run() {
                            try {
                                efVarE.a(l.this.c.floor_names);
                                efVarE.a(l.this.c.activeFloorName);
                                if (efVarE.b()) {
                                    return;
                                }
                                efVarE.a(true);
                            } catch (Throwable th3) {
                                th3.printStackTrace();
                            }
                        }
                    });
                } catch (Throwable th3) {
                    th3.printStackTrace();
                }
            }
        }
    }

    @Override // com.autonavi.base.amap.mapcore.interfaces.IAMapListener
    public final void beforeDrawLabel(int i, GLMapState gLMapState) {
        j();
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            gLMapEngine.pushRendererState();
        }
        this.bd = this.D.draw(0, this.af, this.i) ? this.bd : this.bd + 1;
        GLMapEngine gLMapEngine2 = this.f;
        if (gLMapEngine2 != null) {
            gLMapEngine2.popRendererState();
        }
    }

    @Override // com.autonavi.base.amap.mapcore.interfaces.IAMapListener
    public final void afterDrawLabel(int i, GLMapState gLMapState) {
        j();
        com.autonavi.extra.b bVar = this.aY;
        if (bVar != null) {
            bVar.e();
        }
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            gLMapEngine.pushRendererState();
        }
        this.bd = this.D.draw(1, this.af, this.i) ? this.bd : this.bd + 1;
        GLMapEngine gLMapEngine2 = this.f;
        if (gLMapEngine2 != null) {
            gLMapEngine2.popRendererState();
        }
    }

    @Override // com.autonavi.base.amap.mapcore.interfaces.IAMapListener
    public final void afterRendererOver(int i, GLMapState gLMapState) {
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            gLMapEngine.pushRendererState();
        }
        this.D.draw(2, this.af, this.i);
        GLMapEngine gLMapEngine2 = this.f;
        if (gLMapEngine2 != null) {
            gLMapEngine2.popRendererState();
        }
        CustomRenderer customRenderer = this.ag;
        if (customRenderer != null) {
            customRenderer.onDrawFrame(null);
        }
    }

    @Override // com.autonavi.base.amap.mapcore.interfaces.IAMapListener
    public final void afterDrawFrame(int i, GLMapState gLMapState) {
        float mapZoomer = gLMapState.getMapZoomer();
        GLMapEngine gLMapEngine = this.f;
        if (!(gLMapEngine != null && (gLMapEngine.isInMapAction(i) || this.f.isInMapAnimation(i)))) {
            int i2 = this.ai;
            if (i2 != -1) {
                this.an.setRenderFps(i2);
            } else {
                this.an.setRenderFps(15.0f);
            }
            if (this.aq != mapZoomer) {
                this.aq = mapZoomer;
            }
        }
        if (this.az) {
            return;
        }
        this.az = true;
    }

    @Override // com.autonavi.base.amap.mapcore.interfaces.IAMapListener
    public final void afterAnimation() {
        redrawInfoWindow();
    }

    @Override // com.autonavi.base.amap.mapcore.interfaces.IAMapListener
    public final void onMapPOIClick(MapPoi mapPoi) {
        List listA;
        MapConfig mapConfig = this.b;
        if (mapConfig == null || !mapConfig.isTouchPoiEnable() || (listA = this.u.a(AMap.OnPOIClickListener.class.hashCode())) == null || listA.size() <= 0 || mapPoi == null) {
            return;
        }
        Message messageObtain = Message.obtain();
        messageObtain.what = 20;
        messageObtain.obj = new Poi(mapPoi.getName(), new LatLng(mapPoi.getLatitude(), mapPoi.getLongitude()), mapPoi.getPoiid());
        this.j.sendMessage(messageObtain);
    }

    @Override // com.autonavi.base.amap.mapcore.interfaces.IAMapListener
    public final void onMapBlankClick(double d2, double d3) {
        a(d2, d3);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final long createGLOverlay(int i) {
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            return gLMapEngine.createOverlay(this.F, i);
        }
        return 0L;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final long getGlOverlayMgrPtr() {
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            return gLMapEngine.getGlOverlayMgrPtr(this.F);
        }
        return 0L;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final CrossOverlay addCrossVector(CrossOverlayOptions crossOverlayOptions) {
        if (crossOverlayOptions == null || crossOverlayOptions.getRes() == null) {
            return null;
        }
        final CrossVectorOverlay crossVectorOverlay = new CrossVectorOverlay(this.F, getContext(), this);
        if (crossOverlayOptions != null) {
            crossVectorOverlay.setAttribute(crossOverlayOptions.getAttribute());
        }
        queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.36
            @Override // java.lang.Runnable
            public final void run() {
                GLOverlayBundle overlayBundle;
                if (l.this.f == null || (overlayBundle = l.this.f.getOverlayBundle(l.this.F)) == null) {
                    return;
                }
                overlayBundle.addOverlay(crossVectorOverlay);
            }
        });
        crossVectorOverlay.resumeMarker(crossOverlayOptions.getRes());
        return new CrossOverlay(crossOverlayOptions, crossVectorOverlay);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final RouteOverlay addNaviRouteOverlay() {
        final RouteOverlayInner routeOverlayInner = new RouteOverlayInner(this.F, getContext(), this);
        queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.37
            @Override // java.lang.Runnable
            public final void run() {
                GLOverlayBundle overlayBundle;
                if (l.this.f == null || (overlayBundle = l.this.f.getOverlayBundle(l.this.F)) == null) {
                    return;
                }
                overlayBundle.addOverlay(routeOverlayInner);
            }
        });
        return new RouteOverlay(routeOverlayInner);
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void addOverlayTexture(int i, GLTextureProperty gLTextureProperty) {
        GLOverlayBundle overlayBundle;
        try {
            GLMapEngine gLMapEngine = this.f;
            if (gLMapEngine != null && (overlayBundle = gLMapEngine.getOverlayBundle(i)) != null && gLTextureProperty != null && gLTextureProperty.mBitmap != null) {
                this.f.addOverlayTexture(i, gLTextureProperty);
                overlayBundle.addOverlayTextureItem(gLTextureProperty.mId, gLTextureProperty.mAnchor, gLTextureProperty.mXRatio, gLTextureProperty.mYRatio, gLTextureProperty.mBitmap.getWidth(), gLTextureProperty.mBitmap.getHeight());
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final int createOverlayTexture(int i, Bitmap bitmap) {
        try {
            GLMapEngine gLMapEngine = this.f;
            if (gLMapEngine == null || bitmap == null) {
                return -1;
            }
            return gLMapEngine.createOverlayTexture(i, bitmap);
        } catch (Throwable th) {
            dx.a(th);
        }
        return -1;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void onAMapAppResourceRequest(AMapAppRequestParam aMapAppRequestParam) {
        q qVar = this.u;
        if (qVar == null) {
            return;
        }
        for (AMap.AMapAppResourceRequestListener aMapAppResourceRequestListener : qVar.a(AMap.AMapAppResourceRequestListener.class.hashCode())) {
            if (aMapAppResourceRequestListener != null) {
                aMapAppResourceRequestListener.onRequest(aMapAppRequestParam);
            }
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setTerrainAuth(boolean z) {
        GLMapEngine gLMapEngine;
        if (this.G.get() || (gLMapEngine = this.f) == null) {
            return;
        }
        gLMapEngine.setTerrainAuth(z);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setCustomMapStylePath(String str) {
        if (TextUtils.isEmpty(str) || str.equals(this.b.getCustomStylePath())) {
            return;
        }
        this.b.setCustomStylePath(str);
        this.A = true;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setCustomMapStyleID(String str) {
        if (TextUtils.isEmpty(str) || str.equals(this.b.getCustomStyleID())) {
            return;
        }
        this.b.setCustomStyleID(str);
        this.A = true;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setCustomTextureResourcePath(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.b.setCustomTextureResourcePath(str);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setCustomMapStyle(CustomMapStyleOptions customMapStyleOptions) {
        if (customMapStyleOptions != null) {
            try {
                if (a(true, false)) {
                    return;
                }
                if (customMapStyleOptions.isEnable() && (customMapStyleOptions.getStyleId() != null || customMapStyleOptions.getStyleTexturePath() != null || customMapStyleOptions.getStyleTextureData() != null || customMapStyleOptions.getStyleResDataPath() != null || customMapStyleOptions.getStyleResData() != null)) {
                    o();
                }
                this.aH.c();
                this.aH.a(customMapStyleOptions);
                com.autonavi.extra.b bVar = this.aY;
                if (bVar != null) {
                    bVar.i();
                }
            } catch (Throwable th) {
                dx.a(th);
                return;
            }
        }
        resetRenderTime();
    }

    @Override // com.amap.api.col.3sl.k.a
    public final void a() {
        com.autonavi.extra.b bVar = this.aY;
        if (bVar != null) {
            bVar.i();
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final MyLocationStyle getMyLocationStyle() throws RemoteException {
        cl clVar = this.K;
        if (clVar != null) {
            return clVar.a();
        }
        return null;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void reloadMapCustomStyle() {
        k kVar = this.aH;
        if (kVar != null) {
            kVar.b();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setMapCustomEnable(boolean z, boolean z2) {
        cs csVar;
        if (this.aw && !this.G.get()) {
            boolean z3 = z2 ? z2 : false;
            if (TextUtils.isEmpty(this.b.getCustomStylePath()) && TextUtils.isEmpty(this.b.getCustomStyleID())) {
                return;
            }
            if (z) {
                try {
                    if (this.b.isProFunctionAuthEnable() && !TextUtils.isEmpty(this.b.getCustomStyleID()) && (csVar = this.ak) != null) {
                        csVar.a(this.b.getCustomStyleID());
                        this.ak.b();
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                    dx.a(th);
                    return;
                }
            }
            if (z2 || this.A || (this.b.isCustomStyleEnable() ^ z)) {
                a(z, (byte[]) null, z3);
            }
            this.A = false;
            return;
        }
        this.aM.b = true;
        this.aM.c = z;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setMapCustomEnable(boolean z) {
        if (z) {
            o();
        }
        setMapCustomEnable(z, false);
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setCustomMapStyle(boolean z, byte[] bArr) {
        a(z, bArr, false);
    }

    private void a(boolean z, byte[] bArr, boolean z2) {
        da daVarA;
        try {
            this.b.setCustomStyleEnable(z);
            boolean z3 = false;
            if (this.b.isHideLogoEnable()) {
                this.z.setLogoEnable(!z);
            }
            if (z) {
                c(this.F, true);
                cz czVar = new cz();
                MyTrafficStyle myTrafficStyle = this.Z;
                if (myTrafficStyle != null && myTrafficStyle.getTrafficRoadBackgroundColor() != -1) {
                    czVar.a(this.Z.getTrafficRoadBackgroundColor());
                }
                if (this.b.isProFunctionAuthEnable() && !TextUtils.isEmpty(this.b.getCustomTextureResourcePath())) {
                    z3 = true;
                }
                StyleItem[] styleItemArrC = null;
                if (bArr != null) {
                    daVarA = czVar.a(bArr, z3);
                    if (daVarA != null && (styleItemArrC = daVarA.c()) != null) {
                        this.b.setUseProFunction(true);
                    }
                } else {
                    daVarA = null;
                }
                if (styleItemArrC == null && (daVarA = czVar.a(this.b.getCustomStylePath(), z3)) != null) {
                    styleItemArrC = daVarA.c();
                }
                if (czVar.a() != 0) {
                    this.b.setCustomBackgroundColor(czVar.a());
                }
                if (daVarA != null && daVarA.d() != null) {
                    if (this.al != null) {
                        this.al.a((String) daVarA.d());
                        this.al.a(daVarA);
                        this.al.b();
                        return;
                    }
                    return;
                }
                a(styleItemArrC, z2);
                return;
            }
            c(this.F, false);
            a(this.F, this.b.getMapStyleMode(), this.b.getMapStyleTime());
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.amap.api.col.3sl.cu.a
    public final void a(String str, da daVar) {
        setCustomTextureResourcePath(str);
        if (!this.b.isCustomStyleEnable() || daVar == null) {
            return;
        }
        a(daVar.c(), false);
    }

    private void a(StyleItem[] styleItemArr, boolean z) {
        if (z || (styleItemArr != null && styleItemArr.length > 0)) {
            a(this.F, 0, 0, true, styleItemArr);
            du.a(this.e, true);
        } else {
            du.a(this.e, false);
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void removeEngineGLOverlay(final BaseMapOverlay baseMapOverlay) {
        if (this.f != null) {
            queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.38
                @Override // java.lang.Runnable
                public final void run() {
                    l.this.f.getOverlayBundle(l.this.F).removeOverlay(baseMapOverlay);
                }
            });
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float[] getFinalMatrix() {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            return mapConfig.getMvpMatrix();
        }
        return this.m;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final String createId(String str) {
        IGlOverlayLayer iGlOverlayLayer = this.D;
        if (iGlOverlayLayer != null) {
            return iGlOverlayLayer.createId(str);
        }
        return null;
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void showLogoEnabled(boolean z) {
        if (this.G.get()) {
            return;
        }
        this.C.f(Boolean.valueOf(z));
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final float[] getViewMatrix() {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            return mapConfig.getViewMatrix();
        }
        return this.n;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final float[] getProjectionMatrix() {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            return mapConfig.getProjectionMatrix();
        }
        return this.o;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void changeSurface(GL10 gl10, int i, int i2) {
        try {
            changeSurface(1, gl10, i, i2);
        } catch (Throwable th) {
            th.printStackTrace();
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void createSurface(GL10 gl10, EGLConfig eGLConfig) {
        try {
            this.am = Thread.currentThread().getId();
        } catch (Throwable th) {
            th.printStackTrace();
            dx.a(th);
        }
        try {
            createSurface(1, gl10, eGLConfig);
        } catch (Throwable th2) {
            th2.printStackTrace();
            dx.a(th2);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void renderSurface(GL10 gl10) {
        drawFrame(gl10);
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final boolean canStopMapRender() {
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine == null) {
            return true;
        }
        gLMapEngine.canStopMapRender(this.F);
        return true;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void getLatLngRect(DPoint[] dPointArr) {
        try {
            Rectangle geoRectangle = this.b.getGeoRectangle();
            if (geoRectangle != null) {
                IPoint[] clipRect = geoRectangle.getClipRect();
                for (int i = 0; i < 4; i++) {
                    GLMapState.geo2LonLat(clipRect[i].x, clipRect[i].y, dPointArr[i]);
                }
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void checkMapState(IGLMapState iGLMapState) {
        if (this.b == null || this.G.get()) {
            return;
        }
        LatLngBounds limitLatLngBounds = this.b.getLimitLatLngBounds();
        try {
            if (limitLatLngBounds != null) {
                IPoint[] limitIPoints = this.b.getLimitIPoints();
                if (limitIPoints == null) {
                    IPoint iPointObtain = IPoint.obtain();
                    GLMapState.lonlat2Geo(limitLatLngBounds.northeast.longitude, limitLatLngBounds.northeast.latitude, iPointObtain);
                    IPoint iPointObtain2 = IPoint.obtain();
                    GLMapState.lonlat2Geo(limitLatLngBounds.southwest.longitude, limitLatLngBounds.southwest.latitude, iPointObtain2);
                    IPoint[] iPointArr = {iPointObtain, iPointObtain2};
                    this.b.setLimitIPoints(iPointArr);
                    limitIPoints = iPointArr;
                }
                float fA = dx.a(this.b, limitIPoints[0].x, limitIPoints[0].y, limitIPoints[1].x, limitIPoints[1].y, getMapWidth(), getMapHeight());
                float mapZoomer = iGLMapState.getMapZoomer();
                if (this.b.isSetLimitZoomLevel()) {
                    float maxZoomLevel = this.b.getMaxZoomLevel();
                    float minZoomLevel = this.b.getMinZoomLevel();
                    float fMax = Math.max(fA, Math.min(mapZoomer, maxZoomLevel));
                    if (fA <= maxZoomLevel) {
                        maxZoomLevel = fMax;
                    }
                    fA = maxZoomLevel < minZoomLevel ? minZoomLevel : maxZoomLevel;
                } else if (fA <= 0.0f || mapZoomer >= fA) {
                    fA = mapZoomer;
                }
                iGLMapState.setMapZoomer(fA);
                IPoint iPointObtain3 = IPoint.obtain();
                iGLMapState.getMapGeoCenter(iPointObtain3);
                int[] iArrA = dx.a(limitIPoints[0].x, limitIPoints[0].y, limitIPoints[1].x, limitIPoints[1].y, this.b, iGLMapState, iPointObtain3.x, iPointObtain3.y);
                iGLMapState.setMapGeoCenter(iArrA[0], iArrA[1]);
                iPointObtain3.recycle();
                return;
            }
            if (this.b.isSetLimitZoomLevel()) {
                iGLMapState.setMapZoomer(Math.max(this.b.getMinZoomLevel(), Math.min(iGLMapState.getMapZoomer(), this.b.getMaxZoomLevel())));
            }
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setRenderMode(int i) {
        try {
            IGLSurfaceView iGLSurfaceView = this.B;
            if (iGLSurfaceView != null) {
                iGLSurfaceView.setRenderMode(i);
            }
        } catch (Throwable unused) {
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void changeSize(int i, int i2) {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            this.g = i;
            this.h = i2;
            mapConfig.setMapWidth(i);
            this.b.setMapHeight(i2);
        }
    }

    public final Size a(Size size) {
        Size size2 = new Size(getMapWidth(), getMapHeight());
        a(getNativeEngineID(), 0, 0, size.getWidth(), size.getHeight(), size.getWidth(), size.getHeight());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(size.getWidth(), size.getHeight());
        getGLMapView().setLayoutParams(layoutParams);
        this.C.f().setLayoutParams(layoutParams);
        changeSize(size.getWidth(), size.getHeight());
        b(false);
        return size2;
    }

    public final void b(Size size) {
        a(getNativeEngineID(), 0, 0, size.getWidth(), size.getHeight(), size.getWidth(), size.getHeight());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
        getGLMapView().setLayoutParams(layoutParams);
        this.C.f().setLayoutParams(layoutParams);
        changeSize(size.getWidth(), size.getHeight());
        b(true);
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void setHideLogoEnble(boolean z) {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            mapConfig.setHideLogoEnble(z);
            if (this.b.isCustomStyleEnable()) {
                this.z.setLogoEnable(!z);
            }
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void changeLogoIconStyle(String str, boolean z, int i) {
        ei eiVar = this.C;
        if (eiVar != null) {
            eiVar.a(str, Boolean.valueOf(z), Integer.valueOf(i));
        }
        ag agVar = this.z;
        if (agVar != null) {
            agVar.requestRefreshLogo();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final void refreshLogo() {
        ei eiVar = this.C;
        if (eiVar != null) {
            eiVar.c();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.IAMapDelegate
    public final float getUnitLengthByZoom(int i) {
        GLMapState gLMapState = new GLMapState(this.F, this.f.getNativeInstance());
        gLMapState.setMapZoomer(i);
        gLMapState.recalculate();
        float gLUnitWithWin = gLMapState.getGLUnitWithWin(1);
        gLMapState.recycle();
        return gLUnitWithWin;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void setTouchPoiEnable(boolean z) {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            mapConfig.setTouchPoiEnable(z);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final boolean isTouchPoiEnable() {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            return mapConfig.isTouchPoiEnable();
        }
        return true;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final int getSY() {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            return (int) mapConfig.getSY();
        }
        return -1;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final int getSX() {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            return (int) mapConfig.getSX();
        }
        return -1;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final long getNativeMapController() {
        GLMapEngine gLMapEngine = this.f;
        if (gLMapEngine != null) {
            return gLMapEngine.getNativeMapController(this.F);
        }
        return 0L;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final int getNativeEngineID() {
        return this.F;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnCameraChangeListener(AMap.OnCameraChangeListener onCameraChangeListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnCameraChangeListener.class.hashCode()), onCameraChangeListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnMapClickListener(AMap.OnMapClickListener onMapClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnMapClickListener.class.hashCode()), onMapClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnMarkerDragListener(AMap.OnMarkerDragListener onMarkerDragListener) {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnMarkerDragListener.class.hashCode()), onMarkerDragListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnMapLoadedListener(AMap.OnMapLoadedListener onMapLoadedListener) {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnMapLoadedListener.class.hashCode()), onMapLoadedListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnMapTouchListener(AMap.OnMapTouchListener onMapTouchListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnMapTouchListener.class.hashCode()), onMapTouchListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnMarkerClickListener(AMap.OnMarkerClickListener onMarkerClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnMarkerClickListener.class.hashCode()), onMarkerClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnPolylineClickListener(AMap.OnPolylineClickListener onPolylineClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnPolylineClickListener.class.hashCode()), onPolylineClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnPOIClickListener(AMap.OnPOIClickListener onPOIClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnPOIClickListener.class.hashCode()), onPOIClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnMapLongClickListener(AMap.OnMapLongClickListener onMapLongClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnMapLongClickListener.class.hashCode()), onMapLongClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnInfoWindowClickListener(AMap.OnInfoWindowClickListener onInfoWindowClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnInfoWindowClickListener.class.hashCode()), onInfoWindowClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnIndoorBuildingActiveListener(AMap.OnIndoorBuildingActiveListener onIndoorBuildingActiveListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnIndoorBuildingActiveListener.class.hashCode()), onIndoorBuildingActiveListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addOnMyLocationChangeListener(AMap.OnMyLocationChangeListener onMyLocationChangeListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.OnMyLocationChangeListener.class.hashCode()), onMyLocationChangeListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnCameraChangeListener(AMap.OnCameraChangeListener onCameraChangeListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnCameraChangeListener.class.hashCode()), onCameraChangeListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnMapClickListener(AMap.OnMapClickListener onMapClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnMapClickListener.class.hashCode()), onMapClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnMarkerDragListener(AMap.OnMarkerDragListener onMarkerDragListener) {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnMarkerDragListener.class.hashCode()), onMarkerDragListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnMapLoadedListener(AMap.OnMapLoadedListener onMapLoadedListener) {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnMapLoadedListener.class.hashCode()), onMapLoadedListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnMapTouchListener(AMap.OnMapTouchListener onMapTouchListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnMapTouchListener.class.hashCode()), onMapTouchListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnMarkerClickListener(AMap.OnMarkerClickListener onMarkerClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnMarkerClickListener.class.hashCode()), onMarkerClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnPolylineClickListener(AMap.OnPolylineClickListener onPolylineClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnPolylineClickListener.class.hashCode()), onPolylineClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnPOIClickListener(AMap.OnPOIClickListener onPOIClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnPOIClickListener.class.hashCode()), onPOIClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnMapLongClickListener(AMap.OnMapLongClickListener onMapLongClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnMapLongClickListener.class.hashCode()), onMapLongClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnInfoWindowClickListener(AMap.OnInfoWindowClickListener onInfoWindowClickListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnInfoWindowClickListener.class.hashCode()), onInfoWindowClickListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnIndoorBuildingActiveListener(AMap.OnIndoorBuildingActiveListener onIndoorBuildingActiveListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnIndoorBuildingActiveListener.class.hashCode()), onIndoorBuildingActiveListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeOnMyLocationChangeListener(AMap.OnMyLocationChangeListener onMyLocationChangeListener) throws RemoteException {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.OnMyLocationChangeListener.class.hashCode()), onMyLocationChangeListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addAMapAppResourceListener(AMap.AMapAppResourceRequestListener aMapAppResourceRequestListener) {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.AMapAppResourceRequestListener.class.hashCode()), aMapAppResourceRequestListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeAMapAppResourceListener(AMap.AMapAppResourceRequestListener aMapAppResourceRequestListener) {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.AMapAppResourceRequestListener.class.hashCode()), aMapAppResourceRequestListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final int hideBuildings(final List<LatLng> list) {
        if (this.f == null) {
            return -1;
        }
        FutureTask futureTask = new FutureTask(new Callable<Integer>() { // from class: com.amap.api.col.3sl.l.39
            /* JADX INFO: Access modifiers changed from: private */
            @Override // java.util.concurrent.Callable
            /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
            public Integer call() throws Exception {
                return Integer.valueOf(l.this.f.hideBuildings(list));
            }
        });
        queueEvent(futureTask);
        try {
            return ((Integer) futureTask.get()).intValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        } catch (ExecutionException e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void showHideBuildings(final int i) {
        if (this.f == null) {
            return;
        }
        queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.l.40
            @Override // java.lang.Runnable
            public final void run() {
                l.this.f.showHideBuildings(i);
            }
        });
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void addSignleClickInterceptorListener(AMap.SignleClickInterceptorListener signleClickInterceptorListener) {
        q qVar = this.u;
        if (qVar != null) {
            qVar.a(Integer.valueOf(AMap.SignleClickInterceptorListener.class.hashCode()), signleClickInterceptorListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void removeSignleClickInterceptorListener(AMap.SignleClickInterceptorListener signleClickInterceptorListener) {
        q qVar = this.u;
        if (qVar != null) {
            qVar.b(Integer.valueOf(AMap.SignleClickInterceptorListener.class.hashCode()), signleClickInterceptorListener);
        }
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IAMap
    public final void loadWorldVectorMap(boolean z) {
        MapConfig mapConfig = this.b;
        if (mapConfig != null) {
            mapConfig.setAbroadEnable(z);
        }
    }

    private boolean a(boolean z, boolean z2) {
        if (z) {
            if (this.bc) {
                dd.a("setCustomMapStyle 和 setWorldVectorMapStyle 不能同时使用，setCustomMapStyle将不会生效");
                return true;
            }
            this.bb = true;
        }
        if (!z2) {
            return false;
        }
        if (this.bb) {
            dd.a("setCustomMapStyle 和 setWorldVectorMapStyle 不能同时使用，setWorldVectorMapStyle将不会生效");
            return true;
        }
        this.bc = true;
        return false;
    }

    private void r() {
        cq cqVar = this.aF;
        if (cqVar != null) {
            cqVar.a();
            this.aF = null;
        }
    }
}
