package com.lianhexinye.m90.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public class SpeedControlView extends View {
    private int baseX;
    private int baseY;
    private float linePointerX;
    private float linePointerY;
    private Context mContext;
    private float mDensityDpi;
    private Paint mPaint;
    private int pointX;
    private int pointY;
    private float raduis;
    private float sRaduis;
    private int screenHeight;
    private int screenWidth;
    private int speed;
    private Paint speedAreaPaint;
    private RectF speedRectF;
    private RectF speedRectFInner;
    private boolean start;
    private Paint textPaint;
    private float textScale;
    private int type;

    public void setType(int i) {
        this.type = i;
    }

    public void setStart(boolean z) {
        this.start = z;
    }

    public void setSpeed(int i) {
        this.speed = i;
        postInvalidate();
    }

    public SpeedControlView(Context context) {
        this(context, null);
    }

    public SpeedControlView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SpeedControlView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.start = true;
        this.mContext = context;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.screenWidth = 100;
        this.screenHeight = 100;
        this.mDensityDpi = displayMetrics.densityDpi / 320;
        setLayerType(1, null);
        Paint paint = new Paint(1);
        this.mPaint = paint;
        paint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setStrokeWidth(this.mDensityDpi * 5.0f);
        int i2 = this.screenWidth;
        this.raduis = (i2 / 2) - 5;
        int i3 = i2 / 2;
        this.pointY = i3;
        this.pointX = i3;
        Paint paint2 = new Paint(1);
        this.textPaint = paint2;
        paint2.setAntiAlias(true);
        this.textPaint.setColor(-1);
        this.textPaint.setTypeface(Typeface.createFromAsset(this.mContext.getAssets(), "speedttf/kt.ttf"));
        Paint paint3 = new Paint(1);
        this.speedAreaPaint = paint3;
        paint3.setAntiAlias(true);
        this.speedAreaPaint.setStyle(Paint.Style.FILL);
        int i4 = this.pointX;
        float f = this.raduis;
        int i5 = this.pointY;
        this.speedAreaPaint.setShader(new LinearGradient(i4 - f, i5, i4 + f, i5, new int[]{-12296467, -16307479, -16374322}, (float[]) null, Shader.TileMode.CLAMP));
        int i6 = this.pointX;
        float f2 = this.raduis;
        float f3 = this.mDensityDpi;
        int i7 = this.pointY;
        this.speedRectF = new RectF((i6 - f2) + (f3 * 10.0f), (i7 - f2) + (f3 * 10.0f), (i6 + f2) - (f3 * 10.0f), (i7 + f2) - (f3 * 10.0f));
        int i8 = this.pointX;
        float f4 = this.raduis;
        int i9 = this.pointY;
        this.speedRectFInner = new RectF(i8 - (f4 / 2.0f), i9 - (f4 / 2.0f), i8 + (f4 / 2.0f), i9 + (f4 / 2.0f));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(-3481089);
        drawCicle(canvas);
        this.speedAreaPaint.setColor(2118078901);
        drawSpeedArea(canvas);
        this.mPaint.setColor(-1086362955);
        drawScale(canvas);
        this.textPaint.setTextSize(this.mDensityDpi * 10.0f);
        this.mPaint.setColor(-1);
        this.sRaduis = this.raduis - (this.mDensityDpi * 50.0f);
        this.textScale = Math.abs(this.textPaint.descent() + this.textPaint.ascent()) / 2.0f;
        for (int i = 0; i < 8; i++) {
            drawText(canvas, i * 30);
        }
        drawCenter(canvas);
    }

    private void drawCicle(Canvas canvas) {
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(-13355980);
        canvas.drawCircle(this.pointX, this.pointY, this.raduis, this.mPaint);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setColor(-1086362955);
        this.mPaint.setStrokeWidth(this.mDensityDpi * 4.0f);
        canvas.drawCircle(this.pointX, this.pointY, this.raduis, this.mPaint);
        this.mPaint.setStrokeWidth(this.mDensityDpi * 3.0f);
        canvas.drawCircle(this.pointX, this.pointY, this.raduis - (this.mDensityDpi * 10.0f), this.mPaint);
        this.mPaint.setStrokeWidth(this.mDensityDpi * 5.0f);
        this.mPaint.setColor(-415280715);
        canvas.drawCircle(this.pointX, this.pointY, this.raduis / 2.0f, this.mPaint);
        this.mPaint.setColor(2118078901);
        canvas.drawCircle(this.pointX, this.pointY, (this.raduis / 2.0f) + (this.mDensityDpi * 5.0f), this.mPaint);
        this.mPaint.setStrokeWidth(this.mDensityDpi * 3.0f);
    }

    private void drawSpeedArea(Canvas canvas) {
        int i = this.speed;
        float f = i < 210 ? (i * 36) / 30 : 252;
        canvas.drawArc(this.speedRectF, 144.0f, f, true, this.speedAreaPaint);
        this.mPaint.setColor(-13355980);
        this.mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(this.speedRectFInner, 144.0f, f, true, this.mPaint);
        this.mPaint.setStyle(Paint.Style.STROKE);
    }

    private void drawScale(Canvas canvas) {
        for (int i = 0; i < 60; i++) {
            if (i % 6 == 0) {
                int i2 = this.pointX;
                float f = this.raduis;
                float f2 = this.mDensityDpi;
                int i3 = this.pointY;
                canvas.drawLine((i2 - f) + (10.0f * f2), i3, (i2 - f) + (f2 * 50.0f), i3, this.mPaint);
            } else {
                int i4 = this.pointX;
                float f3 = this.raduis;
                float f4 = this.mDensityDpi;
                int i5 = this.pointY;
                canvas.drawLine((i4 - f3) + (10.0f * f4), i5, (i4 - f3) + (f4 * 30.0f), i5, this.mPaint);
            }
            canvas.rotate(6.0f, this.pointX, this.pointY);
        }
    }

    private void drawText(Canvas canvas, int i) {
        String strValueOf = String.valueOf(i);
        if (i == 0) {
            this.baseX = (int) ((((double) this.pointX) - (((double) this.sRaduis) * Math.cos(0.6283185307179586d))) + ((double) (this.textPaint.measureText(strValueOf) / 2.0f)) + ((double) (this.textScale / 2.0f)));
            this.baseY = (int) (((double) this.pointY) + (((double) this.sRaduis) * Math.sin(0.6283185307179586d)) + ((double) (this.textScale / 2.0f)));
        } else if (i == 30) {
            this.baseX = (int) ((this.pointX - this.raduis) + (this.mDensityDpi * 50.0f) + (this.textPaint.measureText(strValueOf) / 2.0f));
            this.baseY = (int) (this.pointY + this.textScale);
        } else if (i == 60) {
            this.baseX = (int) ((((double) this.pointX) - (((double) this.sRaduis) * Math.cos(0.6283185307179586d))) + ((double) this.textScale));
            this.baseY = (int) ((((double) this.pointY) - (((double) this.sRaduis) * Math.sin(0.6283185307179586d))) + ((double) (this.textScale * 2.0f)));
        } else if (i == 90) {
            this.baseX = (int) ((((double) this.pointX) - (((double) this.sRaduis) * Math.cos(1.2566370614359172d))) - ((double) (this.textScale / 2.0f)));
            this.baseY = (int) ((((double) this.pointY) - (((double) this.sRaduis) * Math.sin(1.2566370614359172d))) + ((double) (this.textScale * 2.0f)));
        } else if (i == 120) {
            this.baseX = (int) ((((double) this.pointX) + (((double) this.sRaduis) * Math.sin(0.3141592653589793d))) - ((double) (this.textPaint.measureText(strValueOf) / 2.0f)));
            this.baseY = (int) ((((double) this.pointY) - (((double) this.sRaduis) * Math.cos(0.3141592653589793d))) + ((double) (this.textScale * 2.0f)));
        } else if (i == 150) {
            this.baseX = (int) (((((double) this.pointX) + (((double) this.sRaduis) * Math.cos(0.6283185307179586d))) - ((double) this.textPaint.measureText(strValueOf))) - ((double) (this.textScale / 2.0f)));
            this.baseY = (int) ((((double) this.pointY) - (((double) this.sRaduis) * Math.sin(0.6283185307179586d))) + ((double) (this.textScale * 2.0f)));
        } else if (i == 180) {
            float fMeasureText = (this.pointX + this.sRaduis) - this.textPaint.measureText(strValueOf);
            float f = this.textScale;
            this.baseX = (int) (fMeasureText - (f / 2.0f));
            this.baseY = (int) (this.pointY + f);
        } else if (i == 210) {
            this.baseX = (int) (((((double) this.pointX) + (((double) this.sRaduis) * Math.cos(0.6283185307179586d))) - ((double) this.textPaint.measureText(strValueOf))) - ((double) (this.textScale / 2.0f)));
            this.baseY = (int) ((((double) this.pointY) + (((double) this.sRaduis) * Math.sin(0.6283185307179586d))) - ((double) (this.textScale / 2.0f)));
        }
        canvas.drawText(strValueOf, this.baseX, this.baseY, this.textPaint);
    }

    private void drawCenter(Canvas canvas) {
        this.textPaint.setTextSize(this.mDensityDpi * 30.0f);
        this.baseX = (int) (this.pointX - (this.textPaint.measureText(String.valueOf(this.speed)) / 2.0f));
        this.baseY = (int) (this.pointY + (Math.abs(this.textPaint.descent() + this.textPaint.ascent()) / 4.0f));
        canvas.drawText(String.valueOf(this.speed), this.baseX, this.baseY, this.textPaint);
        this.textPaint.setTextSize(this.mDensityDpi * 24.0f);
        this.baseX = (int) (this.pointX - (this.textPaint.measureText("km/h") / 2.0f));
        int iAbs = (int) (this.pointY + (this.raduis / 4.0f) + (Math.abs(this.textPaint.descent() + this.textPaint.ascent()) / 4.0f));
        this.baseY = iAbs;
        canvas.drawText("km/h", this.baseX, iAbs, this.textPaint);
    }

    public void run() {
        while (this.start) {
            int i = this.type;
            int i2 = 3;
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        this.speed = 0;
                    }
                    i2 = -1;
                } else {
                    i2 = -5;
                }
            }
            int i3 = this.speed + i2;
            this.speed = i3;
            if (i3 < 1) {
                this.speed = 0;
            }
            try {
                Thread.sleep(50L);
                setSpeed(this.speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
