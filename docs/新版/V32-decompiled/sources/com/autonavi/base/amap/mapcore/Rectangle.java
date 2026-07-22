package com.autonavi.base.amap.mapcore;

import android.graphics.Rect;
import com.autonavi.amap.mapcore.IPoint;

/* JADX INFO: loaded from: classes2.dex */
public class Rectangle {
    private int beyond180Mode;
    public float bottom;
    public FPoint[] clipMapRect;
    public IPoint[] clipRect;
    private int[] jniClipRect;
    public float left;
    public Rect rect;
    public float right;
    public float top;

    public int getBeyond180Mode() {
        return this.beyond180Mode;
    }

    public Rectangle() {
        this.rect = new Rect();
        this.beyond180Mode = 0;
        this.clipRect = null;
        this.clipMapRect = null;
        this.jniClipRect = new int[8];
    }

    public Rectangle(Rect rect, int i, int i2) {
        this.rect = new Rect();
        this.beyond180Mode = 0;
        this.clipRect = null;
        this.clipMapRect = null;
        this.jniClipRect = new int[8];
        this.rect = rect;
        if (rect != null) {
            updateRect(rect, i, i2);
            updateClipRect();
            updateClipMapRect(rect.centerX(), rect.centerY());
        }
    }

    public void updateRect(Rect rect, int i, int i2) {
        if (rect != null) {
            this.rect = rect;
            rect.inset((-rect.width()) / 8, (-rect.height()) / 8);
            if ((this.rect.left / 100000.0f) * (this.rect.right / 100000.0f) < 0.0f) {
                this.beyond180Mode = -1;
            } else if (this.rect.right > 268435456) {
                this.beyond180Mode = 1;
            } else {
                this.beyond180Mode = 0;
            }
            updateClipRect();
            updateClipMapRect(i, i2);
        }
    }

    private void updateClipRect() {
        if (this.clipRect == null) {
            IPoint[] iPointArr = new IPoint[4];
            this.clipRect = iPointArr;
            iPointArr[0] = IPoint.obtain(0, 0);
            this.clipRect[1] = IPoint.obtain(0, 0);
            this.clipRect[2] = IPoint.obtain(0, 0);
            this.clipRect[3] = IPoint.obtain(0, 0);
        }
        Rect rect = this.rect;
        if (rect != null) {
            this.clipRect[0].x = rect.left;
            this.clipRect[0].y = this.rect.top;
            this.clipRect[1].x = this.rect.right;
            this.clipRect[1].y = this.rect.top;
            this.clipRect[2].x = this.rect.right;
            this.clipRect[2].y = this.rect.bottom;
            this.clipRect[3].x = this.rect.left;
            this.clipRect[3].y = this.rect.bottom;
        }
    }

    private void updateClipMapRect(int i, int i2) {
        int i3 = 0;
        if (this.clipMapRect == null) {
            FPoint[] fPointArr = new FPoint[4];
            this.clipMapRect = fPointArr;
            fPointArr[0] = FPoint.obtain(0.0f, 0.0f);
            this.clipMapRect[1] = FPoint.obtain(0.0f, 0.0f);
            this.clipMapRect[2] = FPoint.obtain(0.0f, 0.0f);
            this.clipMapRect[3] = FPoint.obtain(0.0f, 0.0f);
        }
        if (this.rect != null) {
            this.clipMapRect[0].x = r0.left - i;
            this.clipMapRect[0].y = this.rect.top - i2;
            this.clipMapRect[1].x = this.rect.right - i;
            this.clipMapRect[1].y = this.rect.top - i2;
            this.clipMapRect[2].x = this.rect.right - i;
            this.clipMapRect[2].y = this.rect.bottom - i2;
            this.clipMapRect[3].x = this.rect.left - i;
            this.clipMapRect[3].y = this.rect.bottom - i2;
        }
        if (this.jniClipRect == null) {
            this.jniClipRect = new int[this.clipMapRect.length * 2];
        }
        while (true) {
            FPoint[] fPointArr2 = this.clipMapRect;
            if (i3 >= fPointArr2.length) {
                return;
            }
            int i4 = i3 * 2;
            this.jniClipRect[i4] = (int) fPointArr2[i3].x;
            this.jniClipRect[i4 + 1] = (int) this.clipMapRect[i3].y;
            i3++;
        }
    }

    public Rect getRect() {
        return this.rect;
    }

    public IPoint[] getClipRect() {
        return this.clipRect;
    }

    public FPoint[] getClipMapRect() {
        return this.clipMapRect;
    }

    public boolean contains(int i, int i2) {
        Rect rect = this.rect;
        if (rect == null) {
            return false;
        }
        if (rect.contains(i, i2)) {
            return true;
        }
        if (this.beyond180Mode != 0) {
            return this.rect.contains(i - 268435456, i2) || this.rect.contains(i + 268435456, i2);
        }
        return false;
    }

    public boolean contains(IPoint iPoint) {
        if (iPoint == null) {
            return false;
        }
        return contains(iPoint.x, iPoint.y);
    }

    public boolean contains(Rect rect) {
        if (rect == null) {
            return false;
        }
        return this.rect.contains(rect);
    }

    public boolean isOverlap(Rect rect) {
        Rect rect2 = this.rect;
        return rect2 != null && rect != null && rect2.left + this.rect.width() > rect.left && rect.left + rect.width() > this.rect.left && this.rect.top + this.rect.height() > rect.top && rect.top + rect.height() > this.rect.top;
    }

    public boolean isOverlap(int i, int i2, int i3, int i4) {
        Rect rect = this.rect;
        return rect != null && rect.left + this.rect.width() > i && i + i3 > this.rect.left && this.rect.top + this.rect.height() > i2 && i2 + i4 > this.rect.top;
    }

    public Rectangle(float f, float f2, float f3, float f4) {
        this.rect = new Rect();
        this.beyond180Mode = 0;
        this.clipRect = null;
        this.clipMapRect = null;
        this.jniClipRect = new int[8];
        if (f >= f2 || f4 <= f3) {
            return;
        }
        this.left = f;
        this.right = f2;
        this.top = f4;
        this.bottom = f3;
    }
}
