package android.opengl;

/* JADX INFO: loaded from: classes.dex */
public abstract class EGLObjectHandle {
    private final int mHandle;

    protected EGLObjectHandle(int i) {
        this.mHandle = i;
    }

    public int getHandle() {
        return this.mHandle;
    }

    public int hashCode() {
        return getHandle();
    }
}
