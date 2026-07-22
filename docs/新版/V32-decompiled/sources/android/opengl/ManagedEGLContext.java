package android.opengl;

import android.os.Looper;
import android.util.Log;
import com.google.android.gles_jni.EGLImpl;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;

/* JADX INFO: loaded from: classes.dex */
public abstract class ManagedEGLContext {
    static final String TAG = "ManagedEGLContext";
    static final ArrayList<ManagedEGLContext> sActive = new ArrayList<>();
    final javax.microedition.khronos.egl.EGLContext mContext;

    public abstract void onTerminate(javax.microedition.khronos.egl.EGLContext eGLContext);

    public ManagedEGLContext(javax.microedition.khronos.egl.EGLContext eGLContext) {
        this.mContext = eGLContext;
        ArrayList<ManagedEGLContext> arrayList = sActive;
        synchronized (arrayList) {
            arrayList.add(this);
        }
    }

    public javax.microedition.khronos.egl.EGLContext getContext() {
        return this.mContext;
    }

    public void terminate() {
        execTerminate();
    }

    void execTerminate() {
        onTerminate(this.mContext);
    }

    public static boolean doTerminate() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Called on wrong thread");
        }
        ArrayList<ManagedEGLContext> arrayList = sActive;
        synchronized (arrayList) {
            if (arrayList.size() <= 0) {
                return false;
            }
            javax.microedition.khronos.egl.EGLDisplay eGLDisplayEglGetDisplay = ((EGL10) javax.microedition.khronos.egl.EGLContext.getEGL()).eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (eGLDisplayEglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                Log.w(TAG, "doTerminate failed: no display");
                return false;
            }
            if (EGLImpl.getInitCount(eGLDisplayEglGetDisplay) != arrayList.size()) {
                Log.w(TAG, "doTerminate failed: EGL count is " + EGLImpl.getInitCount(eGLDisplayEglGetDisplay) + " but managed count is " + arrayList.size());
                return false;
            }
            ArrayList arrayList2 = new ArrayList(arrayList);
            arrayList.clear();
            for (int i = 0; i < arrayList2.size(); i++) {
                ((ManagedEGLContext) arrayList2.get(i)).execTerminate();
            }
            return true;
        }
    }
}
