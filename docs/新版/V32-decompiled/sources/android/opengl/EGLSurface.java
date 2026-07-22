package android.opengl;

/* JADX INFO: loaded from: classes.dex */
public class EGLSurface extends EGLObjectHandle {
    private EGLSurface(int i) {
        super(i);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof EGLSurface) && getHandle() == ((EGLSurface) obj).getHandle();
    }
}
