package android.opengl;

/* JADX INFO: loaded from: classes.dex */
public class EGLContext extends EGLObjectHandle {
    private EGLContext(int i) {
        super(i);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof EGLContext) && getHandle() == ((EGLContext) obj).getHandle();
    }
}
