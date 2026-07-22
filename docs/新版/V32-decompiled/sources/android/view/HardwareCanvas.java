package android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public abstract class HardwareCanvas extends Canvas {
    private String mName;

    abstract void attachFunctor(int i);

    public int callDrawGLFunction(int i) {
        return 0;
    }

    abstract void cancelLayerUpdate(HardwareLayer hardwareLayer);

    abstract void clearLayerUpdates();

    abstract void detachFunctor(int i);

    public abstract int drawDisplayList(DisplayList displayList, Rect rect, int i);

    abstract void drawHardwareLayer(HardwareLayer hardwareLayer, float f, float f2, Paint paint);

    abstract void flushLayerUpdates();

    public int invokeFunctors(Rect rect) {
        return 0;
    }

    @Override // android.graphics.Canvas
    public boolean isHardwareAccelerated() {
        return true;
    }

    public abstract void onPostDraw();

    public abstract int onPreDraw(Rect rect);

    abstract void outputDisplayList(DisplayList displayList);

    abstract void pushLayerUpdate(HardwareLayer hardwareLayer);

    @Override // android.graphics.Canvas
    public void setBitmap(Bitmap bitmap) {
        throw new UnsupportedOperationException();
    }

    public void setName(String str) {
        this.mName = str;
    }

    public String getName() {
        return this.mName;
    }

    public void drawDisplayList(DisplayList displayList) {
        drawDisplayList(displayList, null, 1);
    }
}
