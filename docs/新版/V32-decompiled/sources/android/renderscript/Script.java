package android.renderscript;

import android.util.SparseArray;
import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: classes.dex */
public class Script extends BaseObj {
    private final SparseArray<FieldID> mFIDs;
    private final SparseArray<KernelID> mKIDs;

    public static final class KernelID extends BaseObj {
        Script mScript;
        int mSig;
        int mSlot;

        KernelID(int i, RenderScript renderScript, Script script, int i2, int i3) {
            super(i, renderScript);
            this.mScript = script;
            this.mSlot = i2;
            this.mSig = i3;
        }
    }

    protected KernelID createKernelID(int i, int i2, Element element, Element element2) {
        KernelID kernelID = this.mKIDs.get(i);
        if (kernelID != null) {
            return kernelID;
        }
        int iNScriptKernelIDCreate = this.mRS.nScriptKernelIDCreate(getID(this.mRS), i, i2);
        if (iNScriptKernelIDCreate == 0) {
            throw new RSDriverException("Failed to create KernelID");
        }
        KernelID kernelID2 = new KernelID(iNScriptKernelIDCreate, this.mRS, this, i, i2);
        this.mKIDs.put(i, kernelID2);
        return kernelID2;
    }

    public static final class FieldID extends BaseObj {
        Script mScript;
        int mSlot;

        FieldID(int i, RenderScript renderScript, Script script, int i2) {
            super(i, renderScript);
            this.mScript = script;
            this.mSlot = i2;
        }
    }

    protected FieldID createFieldID(int i, Element element) {
        FieldID fieldID = this.mFIDs.get(i);
        if (fieldID != null) {
            return fieldID;
        }
        int iNScriptFieldIDCreate = this.mRS.nScriptFieldIDCreate(getID(this.mRS), i);
        if (iNScriptFieldIDCreate == 0) {
            throw new RSDriverException("Failed to create FieldID");
        }
        FieldID fieldID2 = new FieldID(iNScriptFieldIDCreate, this.mRS, this, i);
        this.mFIDs.put(i, fieldID2);
        return fieldID2;
    }

    protected void invoke(int i) {
        this.mRS.nScriptInvoke(getID(this.mRS), i);
    }

    protected void invoke(int i, FieldPacker fieldPacker) {
        if (fieldPacker != null) {
            this.mRS.nScriptInvokeV(getID(this.mRS), i, fieldPacker.getData());
        } else {
            this.mRS.nScriptInvoke(getID(this.mRS), i);
        }
    }

    protected void forEach(int i, Allocation allocation, Allocation allocation2, FieldPacker fieldPacker) {
        if (allocation == null && allocation2 == null) {
            throw new RSIllegalArgumentException("At least one of ain or aout is required to be non-null.");
        }
        this.mRS.nScriptForEach(getID(this.mRS), i, allocation != null ? allocation.getID(this.mRS) : 0, allocation2 != null ? allocation2.getID(this.mRS) : 0, fieldPacker != null ? fieldPacker.getData() : null);
    }

    protected void forEach(int i, Allocation allocation, Allocation allocation2, FieldPacker fieldPacker, LaunchOptions launchOptions) {
        if (allocation == null && allocation2 == null) {
            throw new RSIllegalArgumentException("At least one of ain or aout is required to be non-null.");
        }
        if (launchOptions == null) {
            forEach(i, allocation, allocation2, fieldPacker);
        } else {
            this.mRS.nScriptForEachClipped(getID(this.mRS), i, allocation != null ? allocation.getID(this.mRS) : 0, allocation2 != null ? allocation2.getID(this.mRS) : 0, fieldPacker != null ? fieldPacker.getData() : null, launchOptions.xstart, launchOptions.xend, launchOptions.ystart, launchOptions.yend, launchOptions.zstart, launchOptions.zend);
        }
    }

    Script(int i, RenderScript renderScript) {
        super(i, renderScript);
        this.mKIDs = new SparseArray<>();
        this.mFIDs = new SparseArray<>();
    }

    public void bindAllocation(Allocation allocation, int i) {
        this.mRS.validate();
        if (allocation != null) {
            this.mRS.nScriptBindAllocation(getID(this.mRS), allocation.getID(this.mRS), i);
        } else {
            this.mRS.nScriptBindAllocation(getID(this.mRS), 0, i);
        }
    }

    public void setVar(int i, float f) {
        this.mRS.nScriptSetVarF(getID(this.mRS), i, f);
    }

    public float getVarF(int i) {
        return this.mRS.nScriptGetVarF(getID(this.mRS), i);
    }

    public void setVar(int i, double d) {
        this.mRS.nScriptSetVarD(getID(this.mRS), i, d);
    }

    public double getVarD(int i) {
        return this.mRS.nScriptGetVarD(getID(this.mRS), i);
    }

    public void setVar(int i, int i2) {
        this.mRS.nScriptSetVarI(getID(this.mRS), i, i2);
    }

    public int getVarI(int i) {
        return this.mRS.nScriptGetVarI(getID(this.mRS), i);
    }

    public void setVar(int i, long j) {
        this.mRS.nScriptSetVarJ(getID(this.mRS), i, j);
    }

    public long getVarJ(int i) {
        return this.mRS.nScriptGetVarJ(getID(this.mRS), i);
    }

    public void setVar(int i, boolean z) {
        this.mRS.nScriptSetVarI(getID(this.mRS), i, z ? 1 : 0);
    }

    public boolean getVarB(int i) {
        return this.mRS.nScriptGetVarI(getID(this.mRS), i) > 0;
    }

    public void setVar(int i, BaseObj baseObj) {
        this.mRS.nScriptSetVarObj(getID(this.mRS), i, baseObj == null ? 0 : baseObj.getID(this.mRS));
    }

    public void setVar(int i, FieldPacker fieldPacker) {
        this.mRS.nScriptSetVarV(getID(this.mRS), i, fieldPacker.getData());
    }

    public void setVar(int i, FieldPacker fieldPacker, Element element, int[] iArr) {
        this.mRS.nScriptSetVarVE(getID(this.mRS), i, fieldPacker.getData(), element.getID(this.mRS), iArr);
    }

    public void getVarV(int i, FieldPacker fieldPacker) {
        this.mRS.nScriptGetVarV(getID(this.mRS), i, fieldPacker.getData());
    }

    public void setTimeZone(String str) {
        this.mRS.validate();
        try {
            this.mRS.nScriptSetTimeZone(getID(this.mRS), str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder {
        RenderScript mRS;

        Builder(RenderScript renderScript) {
            this.mRS = renderScript;
        }
    }

    public static class FieldBase {
        protected Allocation mAllocation;
        protected Element mElement;

        public void updateAllocation() {
        }

        protected void init(RenderScript renderScript, int i) {
            this.mAllocation = Allocation.createSized(renderScript, this.mElement, i, 1);
        }

        protected void init(RenderScript renderScript, int i, int i2) {
            this.mAllocation = Allocation.createSized(renderScript, this.mElement, i, i2 | 1);
        }

        protected FieldBase() {
        }

        public Element getElement() {
            return this.mElement;
        }

        public Type getType() {
            return this.mAllocation.getType();
        }

        public Allocation getAllocation() {
            return this.mAllocation;
        }
    }

    public static final class LaunchOptions {
        private int strategy;
        private int xstart = 0;
        private int ystart = 0;
        private int xend = 0;
        private int yend = 0;
        private int zstart = 0;
        private int zend = 0;

        public LaunchOptions setX(int i, int i2) {
            if (i < 0 || i2 <= i) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.xstart = i;
            this.xend = i2;
            return this;
        }

        public LaunchOptions setY(int i, int i2) {
            if (i < 0 || i2 <= i) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.ystart = i;
            this.yend = i2;
            return this;
        }

        public LaunchOptions setZ(int i, int i2) {
            if (i < 0 || i2 <= i) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.zstart = i;
            this.zend = i2;
            return this;
        }

        public int getXStart() {
            return this.xstart;
        }

        public int getXEnd() {
            return this.xend;
        }

        public int getYStart() {
            return this.ystart;
        }

        public int getYEnd() {
            return this.yend;
        }

        public int getZStart() {
            return this.zstart;
        }

        public int getZEnd() {
            return this.zend;
        }
    }
}
