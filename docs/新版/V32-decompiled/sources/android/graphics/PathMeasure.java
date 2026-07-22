package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class PathMeasure {
    public static final int POSITION_MATRIX_FLAG = 1;
    public static final int TANGENT_MATRIX_FLAG = 2;
    private Path mPath;
    private final int native_instance;

    private static native int native_create(int i, boolean z);

    private static native void native_destroy(int i);

    private static native float native_getLength(int i);

    private static native boolean native_getMatrix(int i, float f, int i2, int i3);

    private static native boolean native_getPosTan(int i, float f, float[] fArr, float[] fArr2);

    private static native boolean native_getSegment(int i, float f, float f2, int i2, boolean z);

    private static native boolean native_isClosed(int i);

    private static native boolean native_nextContour(int i);

    private static native void native_setPath(int i, int i2, boolean z);

    public PathMeasure() {
        this.mPath = null;
        this.native_instance = native_create(0, false);
    }

    public PathMeasure(Path path, boolean z) {
        this.mPath = path;
        this.native_instance = native_create(path != null ? path.ni() : 0, z);
    }

    public void setPath(Path path, boolean z) {
        this.mPath = path;
        native_setPath(this.native_instance, path != null ? path.ni() : 0, z);
    }

    public float getLength() {
        return native_getLength(this.native_instance);
    }

    public boolean getPosTan(float f, float[] fArr, float[] fArr2) {
        if ((fArr != null && fArr.length < 2) || (fArr2 != null && fArr2.length < 2)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return native_getPosTan(this.native_instance, f, fArr, fArr2);
    }

    public boolean getMatrix(float f, Matrix matrix, int i) {
        return native_getMatrix(this.native_instance, f, matrix.native_instance, i);
    }

    public boolean getSegment(float f, float f2, Path path, boolean z) {
        return native_getSegment(this.native_instance, f, f2, path.ni(), z);
    }

    public boolean isClosed() {
        return native_isClosed(this.native_instance);
    }

    public boolean nextContour() {
        return native_nextContour(this.native_instance);
    }

    protected void finalize() throws Throwable {
        native_destroy(this.native_instance);
    }
}
