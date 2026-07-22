package com.amap.api.col.p0003sl;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/* JADX INFO: compiled from: ScaleGestureDetector.java */
/* JADX INFO: loaded from: classes2.dex */
public class ar {
    private final Context a;
    private final a b;
    private boolean c;
    private MotionEvent d;
    private MotionEvent e;
    private float f;
    private float g;
    private float h;
    private float i;
    private float j;
    private float k;
    private float l;
    private float m;
    private float n;
    private float o;
    private float p;
    private long q;
    private final float r;
    private float s;
    private float t;
    private boolean u;
    private boolean v;
    private int w;
    private int x;
    private boolean y;
    private int z = 0;
    private int A = 0;

    /* JADX INFO: compiled from: ScaleGestureDetector.java */
    public interface a {
        boolean a(ar arVar);

        boolean b(ar arVar);

        void c(ar arVar);
    }

    public final MotionEvent a() {
        return this.e;
    }

    public ar(Context context, a aVar) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.a = context;
        this.b = aVar;
        this.r = viewConfiguration.getScaledEdgeSlop();
    }

    public final void a(int i, int i2) {
        this.z = i;
        this.A = i2;
    }

    /* JADX WARN: Removed duplicated region for block: B:158:0x02a6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean a(android.view.MotionEvent r14) {
        /*
            Method dump skipped, instruction units count: 919
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ar.a(android.view.MotionEvent):boolean");
    }

    private int a(MotionEvent motionEvent, int i, int i2) {
        int pointerCount = motionEvent.getPointerCount();
        int iFindPointerIndex = motionEvent.findPointerIndex(i);
        for (int i3 = 0; i3 < pointerCount; i3++) {
            if (i3 != i2 && i3 != iFindPointerIndex) {
                float f = this.r;
                float f2 = this.s;
                float f3 = this.t;
                float fA = a(motionEvent, i3);
                float fB = b(motionEvent, i3);
                if (fA >= f && fB >= f && fA <= f2 && fB <= f3) {
                    return i3;
                }
            }
        }
        return -1;
    }

    private static float a(MotionEvent motionEvent, int i) {
        if (i < 0) {
            return Float.MIN_VALUE;
        }
        if (i == 0) {
            return motionEvent.getRawX();
        }
        return motionEvent.getX(i) + (motionEvent.getRawX() - motionEvent.getX());
    }

    private static float b(MotionEvent motionEvent, int i) {
        if (i < 0) {
            return Float.MIN_VALUE;
        }
        if (i == 0) {
            return motionEvent.getRawY();
        }
        return motionEvent.getY(i) + (motionEvent.getRawY() - motionEvent.getY());
    }

    private void b(MotionEvent motionEvent) {
        MotionEvent motionEvent2 = this.e;
        if (motionEvent2 != null) {
            motionEvent2.recycle();
        }
        this.e = MotionEvent.obtain(motionEvent);
        this.l = -1.0f;
        this.m = -1.0f;
        this.n = -1.0f;
        MotionEvent motionEvent3 = this.d;
        int iFindPointerIndex = motionEvent3.findPointerIndex(this.w);
        int iFindPointerIndex2 = motionEvent3.findPointerIndex(this.x);
        int iFindPointerIndex3 = motionEvent.findPointerIndex(this.w);
        int iFindPointerIndex4 = motionEvent.findPointerIndex(this.x);
        if (iFindPointerIndex < 0 || iFindPointerIndex2 < 0 || iFindPointerIndex3 < 0 || iFindPointerIndex4 < 0) {
            this.v = true;
            if (this.c) {
                this.b.c(this);
                return;
            }
            return;
        }
        float x = motionEvent3.getX(iFindPointerIndex);
        float y = motionEvent3.getY(iFindPointerIndex);
        float x2 = motionEvent3.getX(iFindPointerIndex2);
        float y2 = motionEvent3.getY(iFindPointerIndex2);
        float x3 = motionEvent.getX(iFindPointerIndex3);
        float y3 = motionEvent.getY(iFindPointerIndex3);
        float x4 = motionEvent.getX(iFindPointerIndex4) - x3;
        float y4 = motionEvent.getY(iFindPointerIndex4) - y3;
        this.h = x2 - x;
        this.i = y2 - y;
        this.j = x4;
        this.k = y4;
        this.f = x3 + (x4 * 0.5f);
        this.g = y3 + (y4 * 0.5f);
        this.q = motionEvent.getEventTime() - motionEvent3.getEventTime();
        this.o = motionEvent.getPressure(iFindPointerIndex3) + motionEvent.getPressure(iFindPointerIndex4);
        this.p = motionEvent3.getPressure(iFindPointerIndex) + motionEvent3.getPressure(iFindPointerIndex2);
    }

    private void j() {
        MotionEvent motionEvent = this.d;
        if (motionEvent != null) {
            motionEvent.recycle();
            this.d = null;
        }
        MotionEvent motionEvent2 = this.e;
        if (motionEvent2 != null) {
            motionEvent2.recycle();
            this.e = null;
        }
        this.u = false;
        this.c = false;
        this.w = -1;
        this.x = -1;
        this.v = false;
    }

    public final float b() {
        return this.f;
    }

    public final float c() {
        return this.g;
    }

    private float k() {
        if (this.l == -1.0f) {
            float f = this.j;
            float f2 = this.k;
            this.l = (float) Math.sqrt((f * f) + (f2 * f2));
        }
        return this.l;
    }

    public final float d() {
        return this.j;
    }

    public final float e() {
        return this.k;
    }

    private float l() {
        if (this.m == -1.0f) {
            float f = this.h;
            float f2 = this.i;
            this.m = (float) Math.sqrt((f * f) + (f2 * f2));
        }
        return this.m;
    }

    public final float f() {
        return this.h;
    }

    public final float g() {
        return this.i;
    }

    public final float h() {
        if (this.n == -1.0f) {
            this.n = k() / l();
        }
        return this.n;
    }

    public final long i() {
        return this.q;
    }
}
