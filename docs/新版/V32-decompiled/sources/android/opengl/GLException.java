package android.opengl;

/* JADX INFO: loaded from: classes.dex */
public class GLException extends RuntimeException {
    private final int mError;

    public GLException(int i) {
        super(getErrorString(i));
        this.mError = i;
    }

    public GLException(int i, String str) {
        super(str);
        this.mError = i;
    }

    private static String getErrorString(int i) {
        String strGluErrorString = GLU.gluErrorString(i);
        return strGluErrorString == null ? "Unknown error 0x" + Integer.toHexString(i) : strGluErrorString;
    }

    int getError() {
        return this.mError;
    }
}
