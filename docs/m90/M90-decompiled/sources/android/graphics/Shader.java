package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class Shader {
    private Matrix mLocalMatrix;
    public int native_instance;
    public int native_shader;

    private static native void nativeDestructor(int i, int i2);

    private static native void nativeSetLocalMatrix(int i, int i2, int i3);

    public enum TileMode {
        CLAMP(0),
        REPEAT(1),
        MIRROR(2);

        final int nativeInt;

        TileMode(int i) {
            this.nativeInt = i;
        }
    }

    public boolean getLocalMatrix(Matrix matrix) {
        Matrix matrix2 = this.mLocalMatrix;
        if (matrix2 == null) {
            return false;
        }
        matrix.set(matrix2);
        return !this.mLocalMatrix.isIdentity();
    }

    public void setLocalMatrix(Matrix matrix) {
        this.mLocalMatrix = matrix;
        nativeSetLocalMatrix(this.native_instance, this.native_shader, matrix == null ? 0 : matrix.native_instance);
    }

    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            nativeDestructor(this.native_instance, this.native_shader);
        }
    }

    protected Shader copy() {
        Shader shader = new Shader();
        copyLocalMatrix(shader);
        return shader;
    }

    protected void copyLocalMatrix(Shader shader) {
        if (this.mLocalMatrix != null) {
            Matrix matrix = new Matrix();
            getLocalMatrix(matrix);
            shader.setLocalMatrix(matrix);
            return;
        }
        shader.setLocalMatrix(null);
    }
}
