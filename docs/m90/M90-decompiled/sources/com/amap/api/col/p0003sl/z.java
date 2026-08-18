package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.amap.api.col.p0003sl.ap;
import com.amap.api.col.p0003sl.aq;
import com.amap.api.col.p0003sl.as;
import com.amap.api.col.p0003sl.at;
import com.amap.api.maps.model.AMapGestureListener;
import com.autonavi.base.ae.gmap.gesture.EAMapPlatformGestureInfo;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import com.autonavi.base.amap.mapcore.message.HoverGestureMapMessage;
import com.autonavi.base.amap.mapcore.message.MoveGestureMapMessage;
import com.autonavi.base.amap.mapcore.message.RotateGestureMapMessage;
import com.autonavi.base.amap.mapcore.message.ScaleGestureMapMessage;

/* JADX INFO: compiled from: GlMapGestureDetector.java */
/* JADX INFO: loaded from: classes2.dex */
public final class z {
    IAMapDelegate a;
    Context b;
    GestureDetector c;
    public AMapGestureListener d;
    private as e;
    private aq f;
    private ap g;
    private at h;
    private int r;
    private int s;
    private boolean i = false;
    private int j = 0;
    private int k = 0;
    private int l = 0;
    private int m = 0;
    private int n = 0;
    private boolean o = false;
    private boolean p = false;
    private boolean q = true;
    private Handler t = new Handler(Looper.getMainLooper());

    static /* synthetic */ int g(z zVar) {
        int i = zVar.k;
        zVar.k = i + 1;
        return i;
    }

    static /* synthetic */ int h(z zVar) {
        int i = zVar.l;
        zVar.l = i + 1;
        return i;
    }

    static /* synthetic */ int l(z zVar) {
        int i = zVar.j;
        zVar.j = i + 1;
        return i;
    }

    static /* synthetic */ int m(z zVar) {
        int i = zVar.m;
        zVar.m = i + 1;
        return i;
    }

    static /* synthetic */ boolean n(z zVar) {
        zVar.q = true;
        return true;
    }

    public final void a(AMapGestureListener aMapGestureListener) {
        this.d = aMapGestureListener;
    }

    public final void a() {
        this.j = 0;
        this.l = 0;
        this.k = 0;
        this.m = 0;
        this.n = 0;
    }

    public z(IAMapDelegate iAMapDelegate) {
        byte b2 = 0;
        this.b = iAMapDelegate.getContext();
        this.a = iAMapDelegate;
        a aVar = new a(this, b2);
        GestureDetector gestureDetector = new GestureDetector(this.b, aVar, this.t);
        this.c = gestureDetector;
        gestureDetector.setOnDoubleTapListener(aVar);
        this.e = new as(this.b, new d(this, b2));
        this.f = new aq(this.b, new c(this, b2));
        this.g = new ap(this.b, new b(this, b2));
        this.h = new at(this.b, new e(this, b2));
    }

    public final void a(int i, int i2) {
        this.r = i;
        this.s = i2;
        as asVar = this.e;
        if (asVar != null) {
            asVar.a(i, i2);
        }
        aq aqVar = this.f;
        if (aqVar != null) {
            aqVar.a(i, i2);
        }
        ap apVar = this.g;
        if (apVar != null) {
            apVar.a(i, i2);
        }
        at atVar = this.h;
        if (atVar != null) {
            atVar.a(i, i2);
        }
    }

    public final int b() {
        return this.r;
    }

    public final int c() {
        return this.s;
    }

    public final boolean a(MotionEvent motionEvent) {
        if (this.n < motionEvent.getPointerCount()) {
            this.n = motionEvent.getPointerCount();
        }
        if ((motionEvent.getAction() & 255) == 0) {
            this.p = false;
            this.q = false;
        }
        if (motionEvent.getAction() == 6 && motionEvent.getPointerCount() > 0) {
            this.p = true;
        }
        if (this.o && this.n >= 2) {
            this.o = false;
        }
        try {
            int[] iArr = {0, 0};
            IAMapDelegate iAMapDelegate = this.a;
            if (iAMapDelegate != null && iAMapDelegate.getGLMapView() != null) {
                this.a.getGLMapView().getLocationOnScreen(iArr);
            }
            if (this.d != null) {
                if (motionEvent.getAction() == 0) {
                    this.d.onDown(motionEvent.getX(), motionEvent.getY());
                } else if (motionEvent.getAction() == 1) {
                    this.d.onUp(motionEvent.getX(), motionEvent.getY());
                }
            }
            this.c.onTouchEvent(motionEvent);
            this.g.b(motionEvent, iArr[0], iArr[1]);
            if (!this.i || this.m <= 0) {
                this.h.b(motionEvent, iArr[0], iArr[1]);
                if (!this.o) {
                    this.e.a(motionEvent);
                    this.f.b(motionEvent, iArr[0], iArr[1]);
                }
            }
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: compiled from: GlMapGestureDetector.java */
    private class a implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {
        float a;
        long b;
        private int d;
        private EAMapPlatformGestureInfo e;

        @Override // android.view.GestureDetector.OnGestureListener
        public final boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        private a() {
            this.d = 0;
            this.a = 0.0f;
            this.e = new EAMapPlatformGestureInfo();
            this.b = 0L;
        }

        /* synthetic */ a(z zVar, byte b) {
            this();
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public final boolean onDown(MotionEvent motionEvent) {
            z.this.o = false;
            return true;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public final boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (z.this.d != null) {
                z.this.d.onFling(f, f2);
            }
            try {
                if (z.this.a.getUiSettings().isScrollGesturesEnabled() && z.this.m <= 0 && z.this.k <= 0 && z.this.l == 0 && !z.this.q) {
                    this.e.mGestureState = 3;
                    this.e.mGestureType = 3;
                    this.e.mLocation = new float[]{motionEvent2.getX(), motionEvent2.getY()};
                    int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.e);
                    z.this.a.onFling();
                    z.this.a.getGLMapEngine().startMapSlidAnim(engineIDWithGestureInfo, new Point((int) motionEvent2.getX(), (int) motionEvent2.getY()), f, f2);
                }
                return true;
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onFling");
                th.printStackTrace();
                return true;
            }
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public final void onLongPress(MotionEvent motionEvent) {
            if (z.this.n == 1) {
                this.e.mGestureState = 3;
                this.e.mGestureType = 7;
                this.e.mLocation = new float[]{motionEvent.getX(), motionEvent.getY()};
                z.this.a.onLongPress(z.this.a.getEngineIDWithGestureInfo(this.e), motionEvent);
                if (z.this.d != null) {
                    z.this.d.onLongPress(motionEvent.getX(), motionEvent.getY());
                }
            }
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public final boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (z.this.d == null) {
                return false;
            }
            z.this.d.onScroll(f, f2);
            return false;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public final void onShowPress(MotionEvent motionEvent) {
            try {
                this.e.mGestureState = 3;
                this.e.mGestureType = 7;
                this.e.mLocation = new float[]{motionEvent.getX(), motionEvent.getY()};
                z.this.a.getGLMapEngine().clearAnimations(z.this.a.getEngineIDWithGestureInfo(this.e), false);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

        @Override // android.view.GestureDetector.OnDoubleTapListener
        public final boolean onDoubleTap(MotionEvent motionEvent) {
            z.this.c.setIsLongpressEnabled(false);
            this.d = motionEvent.getPointerCount();
            if (z.this.d != null) {
                z.this.d.onDoubleTap(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        }

        @Override // android.view.GestureDetector.OnDoubleTapListener
        public final boolean onDoubleTapEvent(MotionEvent motionEvent) {
            if (this.d < motionEvent.getPointerCount()) {
                this.d = motionEvent.getPointerCount();
            }
            int action = motionEvent.getAction() & 255;
            if (this.d != 1) {
                return false;
            }
            try {
                if (!z.this.a.getUiSettings().isZoomGesturesEnabled()) {
                    z.this.c.setIsLongpressEnabled(true);
                    return false;
                }
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onDoubleTapEvent");
                th.printStackTrace();
            }
            if (action == 0) {
                this.e.mGestureState = 1;
                this.e.mGestureType = 9;
                this.e.mLocation = new float[]{motionEvent.getX(), motionEvent.getY()};
                int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.e);
                this.a = motionEvent.getY();
                z.this.a.addGestureMapMessage(engineIDWithGestureInfo, ScaleGestureMapMessage.obtain(100, 1.0f, 0, 0));
                this.b = SystemClock.uptimeMillis();
                return true;
            }
            if (action == 2) {
                z.this.o = true;
                float y = this.a - motionEvent.getY();
                if (Math.abs(y) < 20.0f) {
                    return true;
                }
                this.e.mGestureState = 2;
                this.e.mGestureType = 9;
                this.e.mLocation = new float[]{motionEvent.getX(), motionEvent.getY()};
                z.this.a.addGestureMapMessage(z.this.a.getEngineIDWithGestureInfo(this.e), ScaleGestureMapMessage.obtain(101, (y * 4.0f) / z.this.a.getMapHeight(), 0, 0));
                this.a = motionEvent.getY();
                return true;
            }
            this.e.mGestureState = 3;
            this.e.mGestureType = 9;
            this.e.mLocation = new float[]{motionEvent.getX(), motionEvent.getY()};
            int engineIDWithGestureInfo2 = z.this.a.getEngineIDWithGestureInfo(this.e);
            z.this.c.setIsLongpressEnabled(true);
            z.this.a.addGestureMapMessage(engineIDWithGestureInfo2, ScaleGestureMapMessage.obtain(102, 1.0f, 0, 0));
            if (action != 1) {
                z.this.o = false;
                return true;
            }
            z.this.a.setGestureStatus(engineIDWithGestureInfo2, 3);
            long jUptimeMillis = SystemClock.uptimeMillis() - this.b;
            if (z.this.o && jUptimeMillis >= 200) {
                z.this.o = false;
                return true;
            }
            return z.this.a.onDoubleTap(engineIDWithGestureInfo2, motionEvent);
        }

        @Override // android.view.GestureDetector.OnDoubleTapListener
        public final boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            if (z.this.n != 1) {
                return false;
            }
            this.e.mGestureState = 3;
            this.e.mGestureType = 8;
            this.e.mLocation = new float[]{motionEvent.getX(), motionEvent.getY()};
            int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.e);
            if (z.this.d != null) {
                try {
                    z.this.d.onSingleTap(motionEvent.getX(), motionEvent.getY());
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
            return z.this.a.onSingleTapConfirmed(engineIDWithGestureInfo, motionEvent);
        }
    }

    /* JADX INFO: compiled from: GlMapGestureDetector.java */
    private class d extends as.a {
        private boolean b;
        private boolean c;
        private boolean d;
        private Point e;
        private float[] f;
        private float g;
        private float[] h;
        private float i;
        private EAMapPlatformGestureInfo j;

        private d() {
            this.b = false;
            this.c = false;
            this.d = false;
            this.e = new Point();
            this.f = new float[10];
            this.g = 0.0f;
            this.h = new float[10];
            this.i = 0.0f;
            this.j = new EAMapPlatformGestureInfo();
        }

        /* synthetic */ d(z zVar, byte b) {
            this();
        }

        /* JADX WARN: Removed duplicated region for block: B:21:0x00af A[Catch: all -> 0x0118, TRY_LEAVE, TryCatch #4 {all -> 0x0118, blocks: (B:8:0x0086, B:10:0x0094, B:19:0x00ab, B:21:0x00af), top: B:88:0x0086 }] */
        /* JADX WARN: Removed duplicated region for block: B:31:0x00cf  */
        /* JADX WARN: Removed duplicated region for block: B:42:0x0116  */
        /* JADX WARN: Removed duplicated region for block: B:71:0x0177 A[Catch: all -> 0x01a8, TRY_LEAVE, TryCatch #3 {all -> 0x01a8, blocks: (B:47:0x0122, B:49:0x0130, B:51:0x013a, B:53:0x013e, B:55:0x0148, B:57:0x0150, B:58:0x0152, B:60:0x0156, B:71:0x0177, B:66:0x0168), top: B:86:0x0122 }] */
        @Override // com.amap.api.col.3sl.as.a
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final boolean a(com.amap.api.col.p0003sl.as r18) {
            /*
                Method dump skipped, instruction units count: 433
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.3sl.z.d.a(com.amap.api.col.3sl.as):boolean");
        }

        @Override // com.amap.api.col.3sl.as.a
        public final boolean b(as asVar) {
            this.j.mGestureState = 1;
            this.j.mGestureType = 4;
            this.j.mLocation = new float[]{asVar.a().getX(), asVar.a().getY()};
            int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.j);
            int iB = (int) asVar.b();
            int iC = (int) asVar.c();
            this.d = false;
            this.e.x = iB;
            this.e.y = iC;
            this.b = false;
            this.c = false;
            z.this.a.addGestureMapMessage(engineIDWithGestureInfo, ScaleGestureMapMessage.obtain(100, 1.0f, iB, iC));
            try {
                if (z.this.a.getUiSettings().isRotateGesturesEnabled() && !z.this.a.isLockMapAngle(engineIDWithGestureInfo)) {
                    z.this.a.addGestureMapMessage(engineIDWithGestureInfo, RotateGestureMapMessage.obtain(100, z.this.a.getMapAngle(engineIDWithGestureInfo), iB, iC));
                }
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onScaleRotateBegin");
                th.printStackTrace();
            }
            return true;
        }

        /* JADX WARN: Removed duplicated region for block: B:49:0x0129  */
        @Override // com.amap.api.col.3sl.as.a
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final void c(com.amap.api.col.p0003sl.as r12) {
            /*
                Method dump skipped, instruction units count: 330
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.3sl.z.d.c(com.amap.api.col.3sl.as):void");
        }
    }

    /* JADX INFO: compiled from: GlMapGestureDetector.java */
    private class c implements aq.a {
        private EAMapPlatformGestureInfo b;

        private c() {
            this.b = new EAMapPlatformGestureInfo();
        }

        /* synthetic */ c(z zVar, byte b) {
            this();
        }

        @Override // com.amap.api.col.3sl.aq.a
        public final boolean a(aq aqVar) {
            if (z.this.i) {
                return true;
            }
            try {
                if (z.this.a.getUiSettings().isScrollGesturesEnabled()) {
                    if (!z.this.p) {
                        this.b.mGestureState = 2;
                        this.b.mGestureType = 3;
                        this.b.mLocation = new float[]{aqVar.c().getX(), aqVar.c().getY()};
                        int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.b);
                        PointF pointFD = aqVar.d();
                        float f = z.this.j == 0 ? 4.0f : 1.0f;
                        if (Math.abs(pointFD.x) <= f && Math.abs(pointFD.y) <= f) {
                            return false;
                        }
                        if (z.this.j == 0) {
                            z.this.a.getGLMapEngine().clearAnimations(engineIDWithGestureInfo, false);
                        }
                        z.this.a.addGestureMapMessage(engineIDWithGestureInfo, MoveGestureMapMessage.obtain(101, pointFD.x, pointFD.y, aqVar.c().getX(), aqVar.c().getY()));
                        z.l(z.this);
                    }
                }
                return true;
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onMove");
                th.printStackTrace();
                return true;
            }
        }

        @Override // com.amap.api.col.3sl.aq.a
        public final boolean b(aq aqVar) {
            try {
                if (!z.this.a.getUiSettings().isScrollGesturesEnabled()) {
                    return true;
                }
                this.b.mGestureState = 1;
                this.b.mGestureType = 3;
                this.b.mLocation = new float[]{aqVar.c().getX(), aqVar.c().getY()};
                z.this.a.addGestureMapMessage(z.this.a.getEngineIDWithGestureInfo(this.b), MoveGestureMapMessage.obtain(100, 0.0f, 0.0f, aqVar.c().getX(), aqVar.c().getY()));
                return true;
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onMoveBegin");
                th.printStackTrace();
                return true;
            }
        }

        @Override // com.amap.api.col.3sl.aq.a
        public final void c(aq aqVar) {
            try {
                if (z.this.a.getUiSettings().isScrollGesturesEnabled()) {
                    this.b.mGestureState = 3;
                    this.b.mGestureType = 3;
                    this.b.mLocation = new float[]{aqVar.c().getX(), aqVar.c().getY()};
                    int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.b);
                    if (z.this.j > 0) {
                        z.this.a.setGestureStatus(engineIDWithGestureInfo, 5);
                    }
                    z.this.a.addGestureMapMessage(engineIDWithGestureInfo, MoveGestureMapMessage.obtain(102, 0.0f, 0.0f, aqVar.c().getX(), aqVar.c().getY()));
                }
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onMoveEnd");
                th.printStackTrace();
            }
        }
    }

    /* JADX INFO: compiled from: GlMapGestureDetector.java */
    private class b implements ap.a {
        private EAMapPlatformGestureInfo b;

        private b() {
            this.b = new EAMapPlatformGestureInfo();
        }

        /* synthetic */ b(z zVar, byte b) {
            this();
        }

        @Override // com.amap.api.col.3sl.ap.a
        public final boolean a(ap apVar) {
            this.b.mGestureState = 2;
            this.b.mGestureType = 6;
            boolean z = false;
            this.b.mLocation = new float[]{apVar.c().getX(), apVar.c().getY()};
            try {
                if (!z.this.a.getUiSettings().isTiltGesturesEnabled()) {
                    return true;
                }
                int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.b);
                if (z.this.a.isLockMapCameraDegree(engineIDWithGestureInfo) || z.this.l > 3) {
                    return false;
                }
                float f = apVar.d().x;
                float f2 = apVar.d().y;
                if (!z.this.i) {
                    PointF pointFA = apVar.a(0);
                    PointF pointFA2 = apVar.a(1);
                    if ((pointFA.y > 10.0f && pointFA2.y > 10.0f) || (pointFA.y < -10.0f && pointFA2.y < -10.0f)) {
                        z = true;
                    }
                    if (z && Math.abs(f2) > 10.0f && Math.abs(f) < 10.0f) {
                        z.this.i = true;
                    }
                }
                if (z.this.i) {
                    z.this.i = true;
                    float f3 = f2 / 6.0f;
                    if (Math.abs(f3) > 1.0f) {
                        z.this.a.addGestureMapMessage(engineIDWithGestureInfo, HoverGestureMapMessage.obtain(101, f3));
                        z.m(z.this);
                    }
                }
                return true;
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onHove");
                th.printStackTrace();
                return true;
            }
        }

        @Override // com.amap.api.col.3sl.ap.a
        public final boolean b(ap apVar) {
            this.b.mGestureState = 1;
            this.b.mGestureType = 6;
            this.b.mLocation = new float[]{apVar.c().getX(), apVar.c().getY()};
            try {
                if (!z.this.a.getUiSettings().isTiltGesturesEnabled()) {
                    return true;
                }
                int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.b);
                if (z.this.a.isLockMapCameraDegree(engineIDWithGestureInfo)) {
                    return false;
                }
                z.this.a.addGestureMapMessage(engineIDWithGestureInfo, HoverGestureMapMessage.obtain(100, z.this.a.getCameraDegree(engineIDWithGestureInfo)));
                return true;
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onHoveBegin");
                th.printStackTrace();
                return true;
            }
        }

        @Override // com.amap.api.col.3sl.ap.a
        public final void c(ap apVar) {
            this.b.mGestureState = 3;
            this.b.mGestureType = 6;
            this.b.mLocation = new float[]{apVar.c().getX(), apVar.c().getY()};
            try {
                if (z.this.a.getUiSettings().isTiltGesturesEnabled()) {
                    int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.b);
                    if (z.this.a.isLockMapCameraDegree(engineIDWithGestureInfo)) {
                        return;
                    }
                    if (z.this.a.getCameraDegree(engineIDWithGestureInfo) >= 0.0f && z.this.m > 0) {
                        z.this.a.setGestureStatus(engineIDWithGestureInfo, 7);
                    }
                    z.this.i = false;
                    z.this.a.addGestureMapMessage(engineIDWithGestureInfo, HoverGestureMapMessage.obtain(102, z.this.a.getCameraDegree(engineIDWithGestureInfo)));
                }
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onHoveEnd");
                th.printStackTrace();
            }
        }
    }

    /* JADX INFO: compiled from: GlMapGestureDetector.java */
    private class e extends at.b {
        EAMapPlatformGestureInfo a;

        private e() {
            this.a = new EAMapPlatformGestureInfo();
        }

        /* synthetic */ e(z zVar, byte b) {
            this();
        }

        @Override // com.amap.api.col.3sl.at.b, com.amap.api.col.3sl.at.a
        public final void a(at atVar) {
            try {
                if (z.this.a.getUiSettings().isZoomGesturesEnabled() && Math.abs(atVar.d()) <= 10.0f && Math.abs(atVar.e()) <= 10.0f && atVar.b() < 200) {
                    z.n(z.this);
                    this.a.mGestureState = 2;
                    this.a.mGestureType = 2;
                    this.a.mLocation = new float[]{atVar.c().getX(), atVar.c().getY()};
                    int engineIDWithGestureInfo = z.this.a.getEngineIDWithGestureInfo(this.a);
                    z.this.a.setGestureStatus(engineIDWithGestureInfo, 4);
                    z.this.a.zoomOut(engineIDWithGestureInfo);
                }
            } catch (Throwable th) {
                jw.c(th, "GLMapGestrureDetector", "onZoomOut");
                th.printStackTrace();
            }
        }
    }
}
