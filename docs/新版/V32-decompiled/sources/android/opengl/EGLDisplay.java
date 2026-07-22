package android.opengl;

/* JADX INFO: loaded from: classes.dex */
public class EGLDisplay extends EGLObjectHandle {
    private EGLDisplay(int i) {
        super(i);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof EGLDisplay) && getHandle() == ((EGLDisplay) obj).getHandle();
    }
}
