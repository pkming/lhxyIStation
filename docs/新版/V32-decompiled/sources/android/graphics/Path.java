package android.graphics;

import android.graphics.Region;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class Path {
    static final FillType[] sFillTypeArray = {FillType.WINDING, FillType.EVEN_ODD, FillType.INVERSE_WINDING, FillType.INVERSE_EVEN_ODD};
    public boolean isSimplePath;
    private boolean mDetectSimplePaths;
    private Direction mLastDirection;
    public final int mNativePath;
    public Region rects;

    public enum Op {
        DIFFERENCE,
        INTERSECT,
        UNION,
        XOR,
        REVERSE_DIFFERENCE
    }

    private static native void finalizer(int i);

    private static native int init1();

    private static native int init2(int i);

    private static native void native_addArc(int i, RectF rectF, float f, float f2);

    private static native void native_addCircle(int i, float f, float f2, float f3, int i2);

    private static native void native_addOval(int i, RectF rectF, int i2);

    private static native void native_addPath(int i, int i2);

    private static native void native_addPath(int i, int i2, float f, float f2);

    private static native void native_addPath(int i, int i2, int i3);

    private static native void native_addRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native void native_addRect(int i, RectF rectF, int i2);

    private static native void native_addRoundRect(int i, RectF rectF, float f, float f2, int i2);

    private static native void native_addRoundRect(int i, RectF rectF, float[] fArr, int i2);

    private static native void native_arcTo(int i, RectF rectF, float f, float f2, boolean z);

    private static native void native_close(int i);

    private static native void native_computeBounds(int i, RectF rectF);

    private static native void native_cubicTo(int i, float f, float f2, float f3, float f4, float f5, float f6);

    private static native int native_getFillType(int i);

    private static native void native_incReserve(int i, int i2);

    private static native boolean native_isEmpty(int i);

    private static native boolean native_isRect(int i, RectF rectF);

    private static native void native_lineTo(int i, float f, float f2);

    private static native void native_moveTo(int i, float f, float f2);

    private static native void native_offset(int i, float f, float f2);

    private static native void native_offset(int i, float f, float f2, int i2);

    private static native boolean native_op(int i, int i2, int i3, int i4);

    private static native void native_quadTo(int i, float f, float f2, float f3, float f4);

    private static native void native_rCubicTo(int i, float f, float f2, float f3, float f4, float f5, float f6);

    private static native void native_rLineTo(int i, float f, float f2);

    private static native void native_rMoveTo(int i, float f, float f2);

    private static native void native_rQuadTo(int i, float f, float f2, float f3, float f4);

    private static native void native_reset(int i);

    private static native void native_rewind(int i);

    private static native void native_set(int i, int i2);

    private static native void native_setFillType(int i, int i2);

    private static native void native_setLastPoint(int i, float f, float f2);

    private static native void native_transform(int i, int i2);

    private static native void native_transform(int i, int i2, int i3);

    public Path() {
        this.isSimplePath = true;
        this.mLastDirection = null;
        this.mNativePath = init1();
        this.mDetectSimplePaths = android.view.HardwareRenderer.isAvailable();
    }

    public Path(Path path) {
        int i;
        this.isSimplePath = true;
        this.mLastDirection = null;
        if (path != null) {
            i = path.mNativePath;
            this.isSimplePath = path.isSimplePath;
            if (path.rects != null) {
                this.rects = new Region(path.rects);
            }
        } else {
            i = 0;
        }
        this.mNativePath = init2(i);
        this.mDetectSimplePaths = android.view.HardwareRenderer.isAvailable();
    }

    public void reset() {
        this.isSimplePath = true;
        if (this.mDetectSimplePaths) {
            this.mLastDirection = null;
            Region region = this.rects;
            if (region != null) {
                region.setEmpty();
            }
        }
        FillType fillType = getFillType();
        native_reset(this.mNativePath);
        setFillType(fillType);
    }

    public void rewind() {
        this.isSimplePath = true;
        if (this.mDetectSimplePaths) {
            this.mLastDirection = null;
            Region region = this.rects;
            if (region != null) {
                region.setEmpty();
            }
        }
        native_rewind(this.mNativePath);
    }

    public void set(Path path) {
        if (this != path) {
            this.isSimplePath = path.isSimplePath;
            native_set(this.mNativePath, path.mNativePath);
        }
    }

    public boolean op(Path path, Op op) {
        return op(this, path, op);
    }

    public boolean op(Path path, Path path2, Op op) {
        if (!native_op(path.mNativePath, path2.mNativePath, op.ordinal(), this.mNativePath)) {
            return false;
        }
        this.isSimplePath = false;
        this.rects = null;
        return true;
    }

    public enum FillType {
        WINDING(0),
        EVEN_ODD(1),
        INVERSE_WINDING(2),
        INVERSE_EVEN_ODD(3);

        final int nativeInt;

        FillType(int i) {
            this.nativeInt = i;
        }
    }

    public FillType getFillType() {
        return sFillTypeArray[native_getFillType(this.mNativePath)];
    }

    public void setFillType(FillType fillType) {
        native_setFillType(this.mNativePath, fillType.nativeInt);
    }

    public boolean isInverseFillType() {
        return (native_getFillType(this.mNativePath) & 2) != 0;
    }

    public void toggleInverseFillType() {
        native_setFillType(this.mNativePath, native_getFillType(this.mNativePath) ^ 2);
    }

    public boolean isEmpty() {
        return native_isEmpty(this.mNativePath);
    }

    public boolean isRect(RectF rectF) {
        return native_isRect(this.mNativePath, rectF);
    }

    public void computeBounds(RectF rectF, boolean z) {
        native_computeBounds(this.mNativePath, rectF);
    }

    public void incReserve(int i) {
        native_incReserve(this.mNativePath, i);
    }

    public void moveTo(float f, float f2) {
        native_moveTo(this.mNativePath, f, f2);
    }

    public void rMoveTo(float f, float f2) {
        native_rMoveTo(this.mNativePath, f, f2);
    }

    public void lineTo(float f, float f2) {
        this.isSimplePath = false;
        native_lineTo(this.mNativePath, f, f2);
    }

    public void rLineTo(float f, float f2) {
        this.isSimplePath = false;
        native_rLineTo(this.mNativePath, f, f2);
    }

    public void quadTo(float f, float f2, float f3, float f4) {
        this.isSimplePath = false;
        native_quadTo(this.mNativePath, f, f2, f3, f4);
    }

    public void rQuadTo(float f, float f2, float f3, float f4) {
        this.isSimplePath = false;
        native_rQuadTo(this.mNativePath, f, f2, f3, f4);
    }

    public void cubicTo(float f, float f2, float f3, float f4, float f5, float f6) {
        this.isSimplePath = false;
        native_cubicTo(this.mNativePath, f, f2, f3, f4, f5, f6);
    }

    public void rCubicTo(float f, float f2, float f3, float f4, float f5, float f6) {
        this.isSimplePath = false;
        native_rCubicTo(this.mNativePath, f, f2, f3, f4, f5, f6);
    }

    public void arcTo(RectF rectF, float f, float f2, boolean z) {
        this.isSimplePath = false;
        native_arcTo(this.mNativePath, rectF, f, f2, z);
    }

    public void arcTo(RectF rectF, float f, float f2) {
        this.isSimplePath = false;
        native_arcTo(this.mNativePath, rectF, f, f2, false);
    }

    public void close() {
        this.isSimplePath = false;
        native_close(this.mNativePath);
    }

    public enum Direction {
        CW(1),
        CCW(2);

        final int nativeInt;

        Direction(int i) {
            this.nativeInt = i;
        }
    }

    private void detectSimplePath(float f, float f2, float f3, float f4, Direction direction) {
        if (this.mDetectSimplePaths) {
            if (this.mLastDirection == null) {
                this.mLastDirection = direction;
            }
            if (this.mLastDirection != direction) {
                this.isSimplePath = false;
                return;
            }
            if (this.rects == null) {
                this.rects = new Region();
            }
            this.rects.op((int) f, (int) f2, (int) f3, (int) f4, Region.Op.UNION);
        }
    }

    public void addRect(RectF rectF, Direction direction) {
        Objects.requireNonNull(rectF, "need rect parameter");
        detectSimplePath(rectF.left, rectF.top, rectF.right, rectF.bottom, direction);
        native_addRect(this.mNativePath, rectF, direction.nativeInt);
    }

    public void addRect(float f, float f2, float f3, float f4, Direction direction) {
        detectSimplePath(f, f2, f3, f4, direction);
        native_addRect(this.mNativePath, f, f2, f3, f4, direction.nativeInt);
    }

    public void addOval(RectF rectF, Direction direction) {
        Objects.requireNonNull(rectF, "need oval parameter");
        this.isSimplePath = false;
        native_addOval(this.mNativePath, rectF, direction.nativeInt);
    }

    public void addCircle(float f, float f2, float f3, Direction direction) {
        this.isSimplePath = false;
        native_addCircle(this.mNativePath, f, f2, f3, direction.nativeInt);
    }

    public void addArc(RectF rectF, float f, float f2) {
        Objects.requireNonNull(rectF, "need oval parameter");
        this.isSimplePath = false;
        native_addArc(this.mNativePath, rectF, f, f2);
    }

    public void addRoundRect(RectF rectF, float f, float f2, Direction direction) {
        Objects.requireNonNull(rectF, "need rect parameter");
        this.isSimplePath = false;
        native_addRoundRect(this.mNativePath, rectF, f, f2, direction.nativeInt);
    }

    public void addRoundRect(RectF rectF, float[] fArr, Direction direction) {
        Objects.requireNonNull(rectF, "need rect parameter");
        if (fArr.length < 8) {
            throw new ArrayIndexOutOfBoundsException("radii[] needs 8 values");
        }
        this.isSimplePath = false;
        native_addRoundRect(this.mNativePath, rectF, fArr, direction.nativeInt);
    }

    public void addPath(Path path, float f, float f2) {
        this.isSimplePath = false;
        native_addPath(this.mNativePath, path.mNativePath, f, f2);
    }

    public void addPath(Path path) {
        this.isSimplePath = false;
        native_addPath(this.mNativePath, path.mNativePath);
    }

    public void addPath(Path path, Matrix matrix) {
        if (!path.isSimplePath) {
            this.isSimplePath = false;
        }
        native_addPath(this.mNativePath, path.mNativePath, matrix.native_instance);
    }

    public void offset(float f, float f2, Path path) {
        int i = 0;
        if (path != null) {
            int i2 = path.mNativePath;
            path.isSimplePath = false;
            i = i2;
        }
        native_offset(this.mNativePath, f, f2, i);
    }

    public void offset(float f, float f2) {
        this.isSimplePath = false;
        native_offset(this.mNativePath, f, f2);
    }

    public void setLastPoint(float f, float f2) {
        this.isSimplePath = false;
        native_setLastPoint(this.mNativePath, f, f2);
    }

    public void transform(Matrix matrix, Path path) {
        int i = 0;
        if (path != null) {
            path.isSimplePath = false;
            i = path.mNativePath;
        }
        native_transform(this.mNativePath, matrix.native_instance, i);
    }

    public void transform(Matrix matrix) {
        this.isSimplePath = false;
        native_transform(this.mNativePath, matrix.native_instance);
    }

    protected void finalize() throws Throwable {
        try {
            finalizer(this.mNativePath);
        } finally {
            super.finalize();
        }
    }

    final int ni() {
        return this.mNativePath;
    }
}
