package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;
import com.autonavi.amap.mapcore.AMapEngineUtils;
import com.unisound.common.r;
import java.io.File;
import java.io.InputStream;

/* JADX INFO: compiled from: WaterMarkerView.java */
/* JADX INFO: loaded from: classes2.dex */
public final class el extends View {
    private Bitmap a;
    private Bitmap b;
    private Bitmap c;
    private Bitmap d;
    private Bitmap e;
    private Bitmap f;
    private Bitmap g;
    private Paint h;
    private boolean i;
    private int j;
    private int k;
    private int l;
    private int m;
    private int n;
    private int o;
    private int p;
    private int q;
    private boolean r;
    private boolean s;
    private Context t;
    private boolean u;
    private float v;
    private float w;
    private boolean x;
    private boolean y;

    public final void a() {
        try {
            Bitmap bitmap = this.a;
            if (bitmap != null) {
                dx.a(bitmap);
                this.a = null;
            }
            Bitmap bitmap2 = this.b;
            if (bitmap2 != null) {
                dx.a(bitmap2);
                this.b = null;
            }
            this.a = null;
            this.b = null;
            Bitmap bitmap3 = this.f;
            if (bitmap3 != null) {
                dx.a(bitmap3);
                this.f = null;
            }
            Bitmap bitmap4 = this.g;
            if (bitmap4 != null) {
                dx.a(bitmap4);
                this.g = null;
            }
            Bitmap bitmap5 = this.c;
            if (bitmap5 != null) {
                dx.a(bitmap5);
            }
            this.c = null;
            Bitmap bitmap6 = this.d;
            if (bitmap6 != null) {
                dx.a(bitmap6);
            }
            this.d = null;
            Bitmap bitmap7 = this.e;
            if (bitmap7 != null) {
                bitmap7.recycle();
            }
            this.h = null;
        } catch (Throwable th) {
            jw.c(th, "WaterMarkerView", "destory");
            th.printStackTrace();
        }
    }

    public el(Context context) {
        InputStream inputStream;
        super(context);
        this.h = new Paint();
        this.i = false;
        this.j = 0;
        this.k = 0;
        this.l = 0;
        this.m = 10;
        this.n = 0;
        this.o = 0;
        this.p = 10;
        this.q = 8;
        this.r = false;
        this.s = false;
        this.u = true;
        this.v = 0.0f;
        this.w = 0.0f;
        this.x = true;
        this.y = false;
        InputStream inputStreamOpen = null;
        try {
            this.t = context.getApplicationContext();
            InputStream inputStreamOpen2 = dr.a(context).open("ap.data");
            try {
                Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpen2);
                this.f = bitmapDecodeStream;
                this.a = dx.a(bitmapDecodeStream, w.a);
                inputStreamOpen2.close();
                inputStreamOpen = dr.a(context).open("ap1.data");
                Bitmap bitmapDecodeStream2 = BitmapFactory.decodeStream(inputStreamOpen);
                this.g = bitmapDecodeStream2;
                this.b = dx.a(bitmapDecodeStream2, w.a);
                inputStreamOpen.close();
                this.k = this.b.getWidth();
                this.j = this.b.getHeight();
                this.h.setAntiAlias(true);
                this.h.setColor(-16777216);
                this.h.setStyle(Paint.Style.STROKE);
                AMapEngineUtils.LOGO_CUSTOM_ICON_DAY_NAME = context.getFilesDir() + "/icon_web_day.data";
                AMapEngineUtils.LOGO_CUSTOM_ICON_NIGHT_NAME = context.getFilesDir() + "/icon_web_night.data";
                dv.a().a(new md() { // from class: com.amap.api.col.3sl.el.1
                    @Override // com.amap.api.col.p0003sl.md
                    public final void runTask() {
                        el.this.a(AMapEngineUtils.LOGO_CUSTOM_ICON_DAY_NAME, 0);
                        el.this.a(AMapEngineUtils.LOGO_CUSTOM_ICON_NIGHT_NAME, 1);
                        if ("".equals(dn.a(el.this.t, "amap_web_logo", "md5_day", ""))) {
                            if (el.this.c == null || el.this.d == null) {
                                dn.a(el.this.t, "amap_web_logo", "md5_day", (Object) "0b718b5f291b09d2b62be725dfb977b3");
                                dn.a(el.this.t, "amap_web_logo", "md5_night", (Object) "4b1405462a5c910de0e0723ffd96c018");
                                return;
                            }
                            dn.a(el.this.t, "amap_web_logo", "md5_day", (Object) io.a(AMapEngineUtils.LOGO_CUSTOM_ICON_DAY_NAME));
                            String strA = io.a(AMapEngineUtils.LOGO_CUSTOM_ICON_NIGHT_NAME);
                            if (!"".equals(strA)) {
                                dn.a(el.this.t, "amap_web_logo", "md5_night", (Object) strA);
                            }
                            el.this.d(true);
                        }
                    }
                });
                if (inputStreamOpen2 != null) {
                    try {
                        inputStreamOpen2.close();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
                if (inputStreamOpen != null) {
                    try {
                        inputStreamOpen.close();
                    } catch (Throwable th2) {
                        th2.printStackTrace();
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                inputStream = inputStreamOpen;
                inputStreamOpen = inputStreamOpen2;
                try {
                    jw.c(th, "WaterMarkerView", r.s);
                    if (inputStreamOpen != null) {
                        try {
                            inputStreamOpen.close();
                        } catch (Throwable th4) {
                            th4.printStackTrace();
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th5) {
                            th5.printStackTrace();
                        }
                    }
                } finally {
                }
            }
        } catch (Throwable th6) {
            th = th6;
            inputStream = null;
        }
    }

    private Bitmap e() {
        Bitmap bitmap;
        Bitmap bitmap2;
        Bitmap bitmap3;
        return (!this.y || (bitmap3 = this.e) == null) ? this.i ? (!this.s || (bitmap2 = this.d) == null) ? this.b : bitmap2 : (!this.s || (bitmap = this.c) == null) ? this.a : bitmap : bitmap3;
    }

    public final void a(boolean z) {
        if (this.u) {
            try {
                this.i = z;
                if (z) {
                    this.h.setColor(-1);
                } else {
                    this.h.setColor(-16777216);
                }
            } catch (Throwable th) {
                jw.c(th, "WaterMarkerView", "changeBitmap");
                th.printStackTrace();
            }
        }
    }

    public final Point b() {
        return new Point(this.m, this.n - 2);
    }

    public final void a(int i) {
        this.o = 0;
        this.l = i;
        c();
    }

    public final void b(int i) {
        this.o = 1;
        this.q = i;
        c();
    }

    public final void c(int i) {
        this.o = 1;
        this.p = i;
        c();
    }

    public final float d(int i) {
        float f;
        if (!this.u) {
            return 0.0f;
        }
        if (i == 0) {
            return this.v;
        }
        if (i == 1) {
            f = this.v;
        } else {
            if (i != 2) {
                return 0.0f;
            }
            f = this.w;
        }
        return 1.0f - f;
    }

    public final void a(int i, float f) {
        if (this.u) {
            this.o = 2;
            float fMax = Math.max(0.0f, Math.min(f, 1.0f));
            if (i == 0) {
                this.v = fMax;
                this.x = true;
            } else if (i == 1) {
                this.v = 1.0f - fMax;
                this.x = false;
            } else if (i == 2) {
                this.w = 1.0f - fMax;
            }
            c();
        }
    }

    public final void c() {
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        f();
        postInvalidate();
    }

    @Override // android.view.View
    public final void onDraw(Canvas canvas) {
        try {
            if (!this.u || getWidth() == 0 || getHeight() == 0 || this.b == null) {
                return;
            }
            if (!this.r) {
                f();
                this.r = true;
            }
            canvas.drawBitmap(e(), this.m, this.n, this.h);
        } catch (Throwable th) {
            jw.c(th, "WaterMarkerView", "onDraw");
            th.printStackTrace();
        }
    }

    private void f() {
        int i = this.o;
        if (i == 0) {
            h();
        } else if (i == 2) {
            g();
        }
        this.m = this.p;
        int height = (getHeight() - this.q) - this.j;
        this.n = height;
        if (this.m < 0) {
            this.m = 0;
        }
        if (height < 0) {
            this.n = 0;
        }
    }

    private void g() {
        if (this.x) {
            this.p = (int) (getWidth() * this.v);
        } else {
            this.p = (int) ((getWidth() * this.v) - this.k);
        }
        this.q = (int) (getHeight() * this.w);
    }

    private void h() {
        int i = this.l;
        if (i == 1) {
            this.p = (getWidth() - this.k) / 2;
        } else if (i == 2) {
            this.p = (getWidth() - this.k) - 10;
        } else {
            this.p = 10;
        }
        this.q = 8;
    }

    public final void a(String str, int i) {
        try {
            if (this.u && new File(str).exists()) {
                if (i == 0) {
                    Bitmap bitmap = this.c;
                    Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str);
                    this.f = bitmapDecodeFile;
                    this.c = dx.a(bitmapDecodeFile, w.a);
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    dx.a(bitmap);
                    return;
                }
                if (i == 1) {
                    Bitmap bitmap2 = this.d;
                    Bitmap bitmapDecodeFile2 = BitmapFactory.decodeFile(str);
                    this.f = bitmapDecodeFile2;
                    this.d = dx.a(bitmapDecodeFile2, w.a);
                    if (bitmap2 == null || bitmap2.isRecycled()) {
                        return;
                    }
                    dx.a(bitmap2);
                }
            }
        } catch (Throwable th) {
            jw.c(th, "WaterMarkerView", r.s);
            th.printStackTrace();
        }
    }

    public final void b(boolean z) {
        if (this.u) {
            this.y = z;
            if (z) {
                Bitmap bitmap = this.e;
                if (bitmap != null) {
                    this.k = bitmap.getWidth();
                    this.j = this.e.getHeight();
                    return;
                }
                return;
            }
            this.k = this.a.getWidth();
            this.j = this.a.getHeight();
        }
    }

    public final void c(boolean z) {
        this.u = z;
    }

    public final void d(boolean z) {
        if (this.u && this.s != z) {
            this.s = z;
            if (z) {
                if (this.i) {
                    Bitmap bitmap = this.d;
                    if (bitmap != null) {
                        this.k = bitmap.getWidth();
                        this.j = this.d.getHeight();
                        return;
                    }
                    return;
                }
                Bitmap bitmap2 = this.c;
                if (bitmap2 != null) {
                    this.k = bitmap2.getWidth();
                    this.j = this.c.getHeight();
                    return;
                }
                return;
            }
            this.k = this.a.getWidth();
            this.j = this.a.getHeight();
        }
    }

    public final boolean d() {
        return this.i;
    }
}
