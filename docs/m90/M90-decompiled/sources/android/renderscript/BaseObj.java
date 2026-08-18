package android.renderscript;

import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: classes.dex */
public class BaseObj {
    private boolean mDestroyed;
    private int mID;
    private String mName;
    RenderScript mRS;

    BaseObj(int i, RenderScript renderScript) {
        renderScript.validate();
        this.mRS = renderScript;
        this.mID = i;
        this.mDestroyed = false;
    }

    void setID(int i) {
        if (this.mID != 0) {
            throw new RSRuntimeException("Internal Error, reset of object ID.");
        }
        this.mID = i;
    }

    int getID(RenderScript renderScript) {
        this.mRS.validate();
        if (this.mDestroyed) {
            throw new RSInvalidStateException("using a destroyed object.");
        }
        int i = this.mID;
        if (i == 0) {
            throw new RSRuntimeException("Internal error: Object id 0.");
        }
        if (renderScript == null || renderScript == this.mRS) {
            return i;
        }
        throw new RSInvalidStateException("using object with mismatched context.");
    }

    void checkValid() {
        if (this.mID == 0) {
            throw new RSIllegalArgumentException("Invalid object.");
        }
    }

    public void setName(String str) {
        if (str == null) {
            throw new RSIllegalArgumentException("setName requires a string of non-zero length.");
        }
        if (str.length() < 1) {
            throw new RSIllegalArgumentException("setName does not accept a zero length string.");
        }
        if (this.mName != null) {
            throw new RSIllegalArgumentException("setName object already has a name.");
        }
        try {
            this.mRS.nAssignName(this.mID, str.getBytes("UTF-8"));
            this.mName = str;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return this.mName;
    }

    protected void finalize() throws Throwable {
        if (!this.mDestroyed) {
            if (this.mID != 0 && this.mRS.isAlive()) {
                this.mRS.nObjDestroy(this.mID);
            }
            this.mRS = null;
            this.mID = 0;
            this.mDestroyed = true;
        }
        super.finalize();
    }

    public synchronized void destroy() {
        if (this.mDestroyed) {
            throw new RSInvalidStateException("Object already destroyed.");
        }
        this.mDestroyed = true;
        this.mRS.nObjDestroy(this.mID);
    }

    void updateFromNative() {
        this.mRS.validate();
        RenderScript renderScript = this.mRS;
        this.mName = renderScript.nGetName(getID(renderScript));
    }

    public int hashCode() {
        return this.mID;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return getClass() == obj.getClass() && this.mID == ((BaseObj) obj).mID;
    }
}
