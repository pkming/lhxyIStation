package android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
abstract class HardwareLayer {
    static final int DIMENSION_UNDEFINED = -1;
    DisplayList mDisplayList;
    int mHeight;
    boolean mOpaque;
    int mWidth;

    abstract void clearStorage();

    abstract boolean copyInto(Bitmap bitmap);

    abstract void destroy();

    abstract void end(Canvas canvas);

    abstract HardwareCanvas getCanvas();

    abstract boolean isValid();

    abstract void redrawLater(DisplayList displayList, Rect rect);

    abstract boolean resize(int i, int i2);

    void setLayerPaint(Paint paint) {
    }

    abstract void setOpaque(boolean z);

    abstract void setTransform(Matrix matrix);

    abstract HardwareCanvas start(Canvas canvas);

    abstract HardwareCanvas start(Canvas canvas, Rect rect);

    HardwareLayer() {
        this(-1, -1, false);
    }

    HardwareLayer(int i, int i2, boolean z) {
        this.mWidth = i;
        this.mHeight = i2;
        this.mOpaque = z;
    }

    int getWidth() {
        return this.mWidth;
    }

    int getHeight() {
        return this.mHeight;
    }

    DisplayList getDisplayList() {
        return this.mDisplayList;
    }

    void setDisplayList(DisplayList displayList) {
        this.mDisplayList = displayList;
    }

    boolean isOpaque() {
        return this.mOpaque;
    }

    void update(int i, int i2, boolean z) {
        this.mWidth = i;
        this.mHeight = i2;
        this.mOpaque = z;
    }
}
