package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/* JADX INFO: compiled from: AbstractTwoFingerGestureDetector.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class an extends ao {
    protected float a;
    protected float b;
    protected float c;
    protected float d;
    private final float n;
    private float o;
    private float p;
    private float q;
    private float r;
    private float s;
    private float t;
    private float u;
    private float v;

    public an(Context context) {
        super(context);
        this.s = 0.0f;
        this.t = 0.0f;
        this.u = 0.0f;
        this.v = 0.0f;
        this.n = ViewConfiguration.get(context).getScaledEdgeSlop();
    }

    @Override // com.amap.api.col.p0003sl.ao
    protected void a(MotionEvent motionEvent) {
        super.a(motionEvent);
        MotionEvent motionEvent2 = this.g;
        int pointerCount = this.g.getPointerCount();
        int pointerCount2 = motionEvent.getPointerCount();
        if (pointerCount2 == 2 && pointerCount2 == pointerCount) {
            this.q = -1.0f;
            this.r = -1.0f;
            float x = motionEvent2.getX(0);
            float y = motionEvent2.getY(0);
            float x2 = motionEvent2.getX(1);
            float y2 = motionEvent2.getY(1);
            this.a = x2 - x;
            this.b = y2 - y;
            float x3 = motionEvent.getX(0);
            float y3 = motionEvent.getY(0);
            float x4 = motionEvent.getX(1);
            float y4 = motionEvent.getY(1);
            this.c = x4 - x3;
            this.d = y4 - y3;
            this.s = x3 - x;
            this.t = y3 - y;
            this.u = x4 - x2;
            this.v = y4 - y2;
        }
    }

    public final PointF a(int i) {
        if (i == 0) {
            return new PointF(this.s, this.t);
        }
        return new PointF(this.u, this.v);
    }

    private static float a(MotionEvent motionEvent, int i) {
        float x = (i + motionEvent.getX()) - motionEvent.getRawX();
        if (1 < motionEvent.getPointerCount()) {
            return motionEvent.getX(1) + x;
        }
        return 0.0f;
    }

    private static float b(MotionEvent motionEvent, int i) {
        float y = (i + motionEvent.getY()) - motionEvent.getRawY();
        if (1 < motionEvent.getPointerCount()) {
            return motionEvent.getY(1) + y;
        }
        return 0.0f;
    }

    protected final boolean a(MotionEvent motionEvent, int i, int i2) {
        if (this.l == 0 || this.m == 0) {
            DisplayMetrics displayMetrics = this.e.getResources().getDisplayMetrics();
            this.o = displayMetrics.widthPixels - this.n;
            this.p = displayMetrics.heightPixels - this.n;
        } else {
            this.o = this.l - this.n;
            this.p = this.m - this.n;
        }
        float f = this.n;
        float f2 = this.o;
        float f3 = this.p;
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        float fA = a(motionEvent, i);
        float fB = b(motionEvent, i2);
        boolean z = rawX < f || rawY < f || rawX > f2 || rawY > f3;
        boolean z2 = fA < f || fB < f || fA > f2 || fB > f3;
        return (z && z2) || z || z2;
    }
}
