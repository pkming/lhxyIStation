package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;

/* JADX INFO: loaded from: classes.dex */
public class OvalShape extends RectShape {
    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawOval(rect(), paint);
    }
}
