package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.amap.api.maps.model.LatLng;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import com.unisound.common.r;

/* JADX INFO: compiled from: LocationView.java */
/* JADX INFO: loaded from: classes2.dex */
public final class eg extends LinearLayout {
    Bitmap a;
    Bitmap b;
    Bitmap c;
    Bitmap d;
    Bitmap e;
    Bitmap f;
    ImageView g;
    IAMapDelegate h;
    boolean i;

    public final void a() {
        try {
            removeAllViews();
            Bitmap bitmap = this.a;
            if (bitmap != null) {
                dx.a(bitmap);
            }
            Bitmap bitmap2 = this.b;
            if (bitmap2 != null) {
                dx.a(bitmap2);
            }
            if (this.b != null) {
                dx.a(this.c);
            }
            this.a = null;
            this.b = null;
            this.c = null;
            Bitmap bitmap3 = this.d;
            if (bitmap3 != null) {
                dx.a(bitmap3);
                this.d = null;
            }
            Bitmap bitmap4 = this.e;
            if (bitmap4 != null) {
                dx.a(bitmap4);
                this.e = null;
            }
            Bitmap bitmap5 = this.f;
            if (bitmap5 != null) {
                dx.a(bitmap5);
                this.f = null;
            }
        } catch (Throwable th) {
            jw.c(th, "LocationView", "destroy");
            th.printStackTrace();
        }
    }

    public eg(Context context, IAMapDelegate iAMapDelegate) {
        super(context);
        this.i = false;
        this.h = iAMapDelegate;
        try {
            Bitmap bitmapA = dx.a(context, "location_selected.png");
            this.d = bitmapA;
            this.a = dx.a(bitmapA, w.a);
            Bitmap bitmapA2 = dx.a(context, "location_pressed.png");
            this.e = bitmapA2;
            this.b = dx.a(bitmapA2, w.a);
            Bitmap bitmapA3 = dx.a(context, "location_unselected.png");
            this.f = bitmapA3;
            this.c = dx.a(bitmapA3, w.a);
            ImageView imageView = new ImageView(context);
            this.g = imageView;
            imageView.setImageBitmap(this.a);
            this.g.setClickable(true);
            this.g.setPadding(0, 20, 20, 0);
            this.g.setOnTouchListener(new View.OnTouchListener() { // from class: com.amap.api.col.3sl.eg.1
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    if (!eg.this.i) {
                        return false;
                    }
                    if (motionEvent.getAction() == 0) {
                        eg.this.g.setImageBitmap(eg.this.b);
                    } else if (motionEvent.getAction() == 1) {
                        try {
                            eg.this.g.setImageBitmap(eg.this.a);
                            eg.this.h.setMyLocationEnabled(true);
                            Location myLocation = eg.this.h.getMyLocation();
                            if (myLocation == null) {
                                return false;
                            }
                            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                            eg.this.h.showMyLocationOverlay(myLocation);
                            eg.this.h.moveCamera(al.a(latLng, eg.this.h.getZoomLevel()));
                        } catch (Throwable th) {
                            jw.c(th, "LocationView", "onTouch");
                            th.printStackTrace();
                        }
                    }
                    return false;
                }
            });
            addView(this.g);
        } catch (Throwable th) {
            jw.c(th, "LocationView", r.s);
            th.printStackTrace();
        }
    }

    public final void a(boolean z) {
        this.i = z;
        try {
            if (z) {
                this.g.setImageBitmap(this.a);
            } else {
                this.g.setImageBitmap(this.c);
            }
            this.g.invalidate();
        } catch (Throwable th) {
            jw.c(th, "LocationView", "showSelect");
            th.printStackTrace();
        }
    }
}
