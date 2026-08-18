package android.view.animation;

import android.graphics.Matrix;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class Transformation {
    public static final int TYPE_ALPHA = 1;
    public static final int TYPE_BOTH = 3;
    public static final int TYPE_IDENTITY = 0;
    public static final int TYPE_MATRIX = 2;
    protected float mAlpha;
    protected Matrix mMatrix;
    protected int mTransformationType;

    public Transformation() {
        clear();
    }

    public void clear() {
        Matrix matrix = this.mMatrix;
        if (matrix == null) {
            this.mMatrix = new Matrix();
        } else {
            matrix.reset();
        }
        this.mAlpha = 1.0f;
        this.mTransformationType = 3;
    }

    public int getTransformationType() {
        return this.mTransformationType;
    }

    public void setTransformationType(int i) {
        this.mTransformationType = i;
    }

    public void set(Transformation transformation) {
        this.mAlpha = transformation.getAlpha();
        this.mMatrix.set(transformation.getMatrix());
        this.mTransformationType = transformation.getTransformationType();
    }

    public void compose(Transformation transformation) {
        this.mAlpha *= transformation.getAlpha();
        this.mMatrix.preConcat(transformation.getMatrix());
    }

    public void postCompose(Transformation transformation) {
        this.mAlpha *= transformation.getAlpha();
        this.mMatrix.postConcat(transformation.getMatrix());
    }

    public Matrix getMatrix() {
        return this.mMatrix;
    }

    public void setAlpha(float f) {
        this.mAlpha = f;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("Transformation");
        toShortString(sb);
        return sb.toString();
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder(64);
        toShortString(sb);
        return sb.toString();
    }

    public void toShortString(StringBuilder sb) {
        sb.append("{alpha=");
        sb.append(this.mAlpha);
        sb.append(" matrix=");
        this.mMatrix.toShortString(sb);
        sb.append('}');
    }

    public void printShortString(PrintWriter printWriter) {
        printWriter.print("{alpha=");
        printWriter.print(this.mAlpha);
        printWriter.print(" matrix=");
        this.mMatrix.printShortString(printWriter);
        printWriter.print('}');
    }
}
