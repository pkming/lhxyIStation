package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataEditor;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: compiled from: IndoorFloorSwitchView.java */
/* JADX INFO: loaded from: classes2.dex */
public class ef extends ScrollView {
    public static final String a = "ef";
    int b;
    private Context c;
    private LinearLayout d;
    private int e;
    private List<String> f;
    private int g;
    private int h;
    private Bitmap i;
    private int j;
    private int k;
    private int l;
    private int m;
    private int n;
    private int o;
    private Runnable p;
    private int q;
    private a r;

    /* JADX INFO: compiled from: IndoorFloorSwitchView.java */
    public interface a {
        void a(int i);
    }

    public ef(Context context) {
        super(context);
        this.e = 0;
        this.g = -1;
        this.i = null;
        this.j = Color.parseColor("#eeffffff");
        this.k = Color.parseColor("#44383838");
        this.l = 4;
        this.m = 1;
        this.b = 1;
        this.q = 50;
        a(context);
    }

    private void a(Context context) {
        this.c = context;
        setVerticalScrollBarEnabled(false);
        try {
            if (this.i == null) {
                InputStream inputStreamOpen = dr.a(context).open("map_indoor_select.png");
                this.i = BitmapFactory.decodeStream(inputStreamOpen);
                inputStreamOpen.close();
            }
        } catch (Throwable unused) {
        }
        LinearLayout linearLayout = new LinearLayout(context);
        this.d = linearLayout;
        linearLayout.setOrientation(1);
        addView(this.d);
        this.p = new Runnable() { // from class: com.amap.api.col.3sl.ef.1
            @Override // java.lang.Runnable
            public final void run() {
                if (ef.this.o - ef.this.getScrollY() == 0) {
                    if (ef.this.e == 0) {
                        return;
                    }
                    final int i = ef.this.o % ef.this.e;
                    final int i2 = ef.this.o / ef.this.e;
                    if (i != 0) {
                        if (i > ef.this.e / 2) {
                            ef.this.post(new Runnable() { // from class: com.amap.api.col.3sl.ef.1.1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ef.this.smoothScrollTo(0, (ef.this.o - i) + ef.this.e);
                                    ef.this.b = i2 + ef.this.m + 1;
                                    ef.this.f();
                                }
                            });
                            return;
                        } else {
                            ef.this.post(new Runnable() { // from class: com.amap.api.col.3sl.ef.1.2
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ef.this.smoothScrollTo(0, ef.this.o - i);
                                    ef.this.b = i2 + ef.this.m;
                                    ef.this.f();
                                }
                            });
                            return;
                        }
                    }
                    ef efVar = ef.this;
                    efVar.b = i2 + efVar.m;
                    ef.this.f();
                    return;
                }
                ef efVar2 = ef.this;
                efVar2.o = efVar2.getScrollY();
                ef efVar3 = ef.this;
                efVar3.postDelayed(efVar3.p, ef.this.q);
            }
        };
    }

    private void c() {
        this.o = getScrollY();
        postDelayed(this.p, this.q);
    }

    private void d() {
        List<String> list = this.f;
        if (list == null || list.size() == 0) {
            return;
        }
        this.d.removeAllViews();
        this.n = (this.m * 2) + 1;
        for (int size = this.f.size() - 1; size >= 0; size--) {
            this.d.addView(b(this.f.get(size)));
        }
        a(0);
    }

    private TextView b(String str) {
        TextView textView = new TextView(this.c);
        textView.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        textView.setSingleLine(true);
        textView.setTextSize(2, 16.0f);
        textView.setText(str);
        textView.setGravity(17);
        textView.getPaint().setFakeBoldText(true);
        int iA = a(this.c, 8.0f);
        int iA2 = a(this.c, 6.0f);
        textView.setPadding(iA, iA2, iA, iA2);
        if (this.e == 0) {
            this.e = a(textView);
            this.d.setLayoutParams(new FrameLayout.LayoutParams(-2, this.e * this.n));
            setLayoutParams(new LinearLayout.LayoutParams(-2, this.e * this.n));
        }
        return textView;
    }

    private void a(int i) {
        int i2 = this.e;
        if (i2 == 0) {
            return;
        }
        int i3 = this.m;
        int i4 = (i / i2) + i3;
        int i5 = i % i2;
        int i6 = i / i2;
        if (i5 == 0) {
            i4 = i6 + i3;
        } else if (i5 > i2 / 2) {
            i4 = i6 + i3 + 1;
        }
        int childCount = this.d.getChildCount();
        for (int i7 = 0; i7 < childCount; i7++) {
            TextView textView = (TextView) this.d.getChildAt(i7);
            if (textView == null) {
                return;
            }
            if (i4 == i7) {
                textView.setTextColor(Color.parseColor("#0288ce"));
            } else {
                textView.setTextColor(Color.parseColor("#bbbbbb"));
            }
        }
    }

    public final void a(String[] strArr) {
        if (this.f == null) {
            this.f = new ArrayList();
        }
        this.f.clear();
        for (String str : strArr) {
            this.f.add(str);
        }
        for (int i = 0; i < this.m; i++) {
            this.f.add(0, "");
            this.f.add("");
        }
        d();
    }

    @Override // android.view.View
    public void setBackgroundColor(int i) {
        this.j = i;
    }

    public final void a() {
        Bitmap bitmap = this.i;
        if (bitmap != null && !bitmap.isRecycled()) {
            dx.a(this.i);
            this.i = null;
        }
        if (this.r != null) {
            this.r = null;
        }
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        if (this.h == 0) {
            try {
                WindowManager windowManager = (WindowManager) this.c.getSystemService(Context.WINDOW_SERVICE);
                if (windowManager != null) {
                    this.h = windowManager.getDefaultDisplay().getWidth();
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        super.setBackgroundDrawable(new Drawable() { // from class: com.amap.api.col.3sl.ef.2
            @Override // android.graphics.drawable.Drawable
            public final int getOpacity() {
                return 0;
            }

            @Override // android.graphics.drawable.Drawable
            public final void setAlpha(int i) {
            }

            @Override // android.graphics.drawable.Drawable
            public final void setColorFilter(ColorFilter colorFilter) {
            }

            @Override // android.graphics.drawable.Drawable
            public final void draw(Canvas canvas) {
                try {
                    a(canvas);
                    b(canvas);
                    c(canvas);
                } catch (Throwable unused) {
                }
            }

            private void a(Canvas canvas) {
                canvas.drawColor(ef.this.j);
            }

            private void b(Canvas canvas) {
                Paint paint = new Paint();
                Rect rect = new Rect();
                Rect rect2 = new Rect();
                rect.left = 0;
                rect.top = 0;
                rect.right = ef.this.i.getWidth() + 0;
                rect.bottom = ef.this.i.getHeight() + 0;
                rect2.left = 0;
                rect2.top = ef.this.e()[0];
                rect2.right = ef.this.h + 0;
                rect2.bottom = ef.this.e()[1];
                canvas.drawBitmap(ef.this.i, rect, rect2, paint);
            }

            private void c(Canvas canvas) {
                Paint paint = new Paint();
                Rect clipBounds = canvas.getClipBounds();
                paint.setColor(ef.this.k);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(ef.this.l);
                canvas.drawRect(clipBounds, paint);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int[] e() {
        int i = this.e;
        int i2 = this.m;
        return new int[]{i * i2, i * (i2 + 1)};
    }

    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.h = i;
        try {
            setBackgroundDrawable(null);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        a(i2);
        if (i2 > i4) {
            this.g = 1;
        } else {
            this.g = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void f() {
        a aVar = this.r;
        if (aVar != null) {
            try {
                aVar.a(g());
            } catch (Throwable unused) {
            }
        }
    }

    public final void a(String str) {
        List<String> list = this.f;
        if (list == null || list.size() == 0) {
            return;
        }
        int iIndexOf = this.f.indexOf(str);
        int size = this.f.size();
        final int i = ((size - r1) - 1) - iIndexOf;
        this.b = this.m + i;
        post(new Runnable() { // from class: com.amap.api.col.3sl.ef.3
            @Override // java.lang.Runnable
            public final void run() {
                ef efVar = ef.this;
                efVar.smoothScrollTo(0, i * efVar.e);
            }
        });
    }

    private int g() {
        List<String> list = this.f;
        if (list == null || list.size() == 0) {
            return 0;
        }
        return Math.min(this.f.size() - (this.m * 2), Math.max(0, ((this.f.size() - 1) - this.b) - this.m));
    }

    @Override // android.widget.ScrollView
    public void fling(int i) {
        super.fling(i / 3);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            c();
        }
        return super.onTouchEvent(motionEvent);
    }

    public final void a(a aVar) {
        this.r = aVar;
    }

    private static int a(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private static int a(View view) {
        b(view);
        return view.getMeasuredHeight();
    }

    private static void b(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(MediaMetadataEditor.KEY_EDITABLE_MASK, Integer.MIN_VALUE));
    }

    public final void a(boolean z) {
        setVisibility(z ? 0 : 8);
    }

    public final boolean b() {
        return getVisibility() == 0;
    }
}
