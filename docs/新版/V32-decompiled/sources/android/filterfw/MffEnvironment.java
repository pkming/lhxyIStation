package android.filterfw;

import android.filterfw.core.CachedFrameManager;
import android.filterfw.core.FilterContext;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLEnvironment;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class MffEnvironment {
    private FilterContext mContext;

    protected MffEnvironment(FrameManager frameManager) {
        frameManager = frameManager == null ? new CachedFrameManager() : frameManager;
        FilterContext filterContext = new FilterContext();
        this.mContext = filterContext;
        filterContext.setFrameManager(frameManager);
    }

    public FilterContext getContext() {
        return this.mContext;
    }

    public void setGLEnvironment(GLEnvironment gLEnvironment) {
        this.mContext.initGLEnvironment(gLEnvironment);
    }

    public void createGLEnvironment() {
        GLEnvironment gLEnvironment = new GLEnvironment();
        gLEnvironment.initWithNewContext();
        setGLEnvironment(gLEnvironment);
    }

    public void activateGLEnvironment() {
        Objects.requireNonNull(this.mContext.getGLEnvironment(), "No GLEnvironment in place to activate!");
        this.mContext.getGLEnvironment().activate();
    }

    public void deactivateGLEnvironment() {
        Objects.requireNonNull(this.mContext.getGLEnvironment(), "No GLEnvironment in place to deactivate!");
        this.mContext.getGLEnvironment().deactivate();
    }
}
