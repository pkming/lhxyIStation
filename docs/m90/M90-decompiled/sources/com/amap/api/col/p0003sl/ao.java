package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.view.MotionEvent;

/* JADX INFO: compiled from: BaseGestureDetector.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class ao {
    protected final Context e;
    protected boolean f;
    protected MotionEvent g;
    protected MotionEvent h;
    protected float i;
    protected float j;
    protected long k;
    protected int l = 0;
    protected int m = 0;

    protected abstract void a(int i, MotionEvent motionEvent);

    protected abstract void a(int i, MotionEvent motionEvent, int i2, int i3);

    public final void a(int i, int i2) {
        this.l = i;
        this.m = i2;
    }

    public ao(Context context) {
        this.e = context;
    }

    public final boolean b(MotionEvent motionEvent, int i, int i2) {
        int action = motionEvent.getAction() & 255;
        if (!this.f) {
            a(action, motionEvent, i, i2);
            return true;
        }
        a(action, motionEvent);
        return true;
    }

    protected void a(MotionEvent motionEvent) {
        MotionEvent motionEvent2 = this.g;
        MotionEvent motionEvent3 = this.h;
        if (motionEvent3 != null) {
            motionEvent3.recycle();
            this.h = null;
        }
        this.h = MotionEvent.obtain(motionEvent);
        this.k = motionEvent.getEventTime() - motionEvent2.getEventTime();
        if (Build.VERSION.SDK_INT >= 8) {
            this.i = motionEvent.getPressure(motionEvent.getActionIndex());
            this.j = motionEvent2.getPressure(motionEvent2.getActionIndex());
        } else {
            this.i = motionEvent.getPressure(0);
            this.j = motionEvent2.getPressure(0);
        }
    }

    protected void a() {
        MotionEvent motionEvent = this.g;
        if (motionEvent != null) {
            motionEvent.recycle();
            this.g = null;
        }
        MotionEvent motionEvent2 = this.h;
        if (motionEvent2 != null) {
            motionEvent2.recycle();
            this.h = null;
        }
        this.f = false;
    }

    public final long b() {
        return this.k;
    }

    public static PointF b(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        float x = 0.0f;
        float y = 0.0f;
        for (int i = 0; i < pointerCount; i++) {
            x += motionEvent.getX(i);
            y += motionEvent.getY(i);
        }
        float f = pointerCount;
        return new PointF(x / f, y / f);
    }

    public final MotionEvent c() {
        return this.h;
    }
}
