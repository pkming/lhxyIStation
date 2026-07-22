package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.amap.api.col.p0003sl.eh;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import com.unisound.common.r;

/* JADX INFO: compiled from: ZoomControllerView.java */
/* JADX INFO: loaded from: classes2.dex */
final class em extends LinearLayout {
    private Bitmap a;
    private Bitmap b;
    private Bitmap c;
    private Bitmap d;
    private Bitmap e;
    private Bitmap f;
    private Bitmap g;
    private Bitmap h;
    private Bitmap i;
    private Bitmap j;
    private Bitmap k;
    private Bitmap l;
    private ImageView m;
    private ImageView n;
    private IAMapDelegate o;

    public final void a() {
        try {
            removeAllViews();
            dx.a(this.a);
            dx.a(this.b);
            dx.a(this.c);
            dx.a(this.d);
            dx.a(this.e);
            dx.a(this.f);
            this.a = null;
            this.b = null;
            this.c = null;
            this.d = null;
            this.e = null;
            this.f = null;
            Bitmap bitmap = this.g;
            if (bitmap != null) {
                dx.a(bitmap);
                this.g = null;
            }
            Bitmap bitmap2 = this.h;
            if (bitmap2 != null) {
                dx.a(bitmap2);
                this.h = null;
            }
            Bitmap bitmap3 = this.i;
            if (bitmap3 != null) {
                dx.a(bitmap3);
                this.i = null;
            }
            Bitmap bitmap4 = this.j;
            if (bitmap4 != null) {
                dx.a(bitmap4);
                this.g = null;
            }
            Bitmap bitmap5 = this.k;
            if (bitmap5 != null) {
                dx.a(bitmap5);
                this.k = null;
            }
            Bitmap bitmap6 = this.l;
            if (bitmap6 != null) {
                dx.a(bitmap6);
                this.l = null;
            }
            this.m = null;
            this.n = null;
        } catch (Throwable th) {
            jw.c(th, "ZoomControllerView", "destory");
            th.printStackTrace();
        }
    }

    public em(Context context, IAMapDelegate iAMapDelegate) {
        super(context);
        this.o = iAMapDelegate;
        try {
            Bitmap bitmapA = dx.a(context, "zoomin_selected.png");
            this.g = bitmapA;
            this.a = dx.a(bitmapA, w.a);
            Bitmap bitmapA2 = dx.a(context, "zoomin_unselected.png");
            this.h = bitmapA2;
            this.b = dx.a(bitmapA2, w.a);
            Bitmap bitmapA3 = dx.a(context, "zoomout_selected.png");
            this.i = bitmapA3;
            this.c = dx.a(bitmapA3, w.a);
            Bitmap bitmapA4 = dx.a(context, "zoomout_unselected.png");
            this.j = bitmapA4;
            this.d = dx.a(bitmapA4, w.a);
            Bitmap bitmapA5 = dx.a(context, "zoomin_pressed.png");
            this.k = bitmapA5;
            this.e = dx.a(bitmapA5, w.a);
            Bitmap bitmapA6 = dx.a(context, "zoomout_pressed.png");
            this.l = bitmapA6;
            this.f = dx.a(bitmapA6, w.a);
            ImageView imageView = new ImageView(context);
            this.m = imageView;
            imageView.setImageBitmap(this.a);
            this.m.setClickable(true);
            ImageView imageView2 = new ImageView(context);
            this.n = imageView2;
            imageView2.setImageBitmap(this.c);
            this.n.setClickable(true);
            this.m.setOnTouchListener(new View.OnTouchListener() { // from class: com.amap.api.col.3sl.em.1
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    try {
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                    if (em.this.o.getZoomLevel() < em.this.o.getMaxZoomLevel() && em.this.o.isMaploaded()) {
                        if (motionEvent.getAction() == 0) {
                            em.this.m.setImageBitmap(em.this.e);
                        } else if (motionEvent.getAction() == 1) {
                            em.this.m.setImageBitmap(em.this.a);
                            try {
                                em.this.o.animateCamera(al.a());
                            } catch (RemoteException e) {
                                jw.c(e, "ZoomControllerView", "zoomin ontouch");
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                    return false;
                }
            });
            this.n.setOnTouchListener(new View.OnTouchListener() { // from class: com.amap.api.col.3sl.em.2
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    try {
                    } catch (Throwable th) {
                        jw.c(th, "ZoomControllerView", "zoomout ontouch");
                        th.printStackTrace();
                    }
                    if (em.this.o.getZoomLevel() > em.this.o.getMinZoomLevel() && em.this.o.isMaploaded()) {
                        if (motionEvent.getAction() == 0) {
                            em.this.n.setImageBitmap(em.this.f);
                        } else if (motionEvent.getAction() == 1) {
                            em.this.n.setImageBitmap(em.this.c);
                            em.this.o.animateCamera(al.b());
                        }
                        return false;
                    }
                    return false;
                }
            });
            this.m.setPadding(0, 0, 20, -2);
            this.n.setPadding(0, 0, 20, 20);
            setOrientation(1);
            addView(this.m);
            addView(this.n);
        } catch (Throwable th) {
            jw.c(th, "ZoomControllerView", r.s);
            th.printStackTrace();
        }
    }

    public final void a(float f) {
        try {
            if (f < this.o.getMaxZoomLevel() && f > this.o.getMinZoomLevel()) {
                this.m.setImageBitmap(this.a);
                this.n.setImageBitmap(this.c);
            } else if (f == this.o.getMinZoomLevel()) {
                this.n.setImageBitmap(this.d);
                this.m.setImageBitmap(this.a);
            } else if (f == this.o.getMaxZoomLevel()) {
                this.m.setImageBitmap(this.b);
                this.n.setImageBitmap(this.c);
            }
        } catch (Throwable th) {
            jw.c(th, "ZoomControllerView", "setZoomBitmap");
            th.printStackTrace();
        }
    }

    public final void a(int i) {
        try {
            eh.a aVar = (eh.a) getLayoutParams();
            if (i == 1) {
                aVar.e = 16;
            } else if (i == 2) {
                aVar.e = 80;
            }
            setLayoutParams(aVar);
        } catch (Throwable th) {
            jw.c(th, "ZoomControllerView", "setZoomPosition");
            th.printStackTrace();
        }
    }

    public final void a(boolean z) {
        if (z) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
    }
}
