package android.renderscript;

import android.renderscript.Script;

/* JADX INFO: loaded from: classes.dex */
public final class ScriptIntrinsic3DLUT extends ScriptIntrinsic {
    private Element mElement;
    private Allocation mLUT;

    private ScriptIntrinsic3DLUT(int i, RenderScript renderScript, Element element) {
        super(i, renderScript);
        this.mElement = element;
    }

    public static ScriptIntrinsic3DLUT create(RenderScript renderScript, Element element) {
        int iNScriptIntrinsicCreate = renderScript.nScriptIntrinsicCreate(8, element.getID(renderScript));
        if (!element.isCompatible(Element.U8_4(renderScript))) {
            throw new RSIllegalArgumentException("Element must be compatible with uchar4.");
        }
        return new ScriptIntrinsic3DLUT(iNScriptIntrinsicCreate, renderScript, element);
    }

    public void setLUT(Allocation allocation) {
        Type type = allocation.getType();
        if (type.getZ() == 0) {
            throw new RSIllegalArgumentException("LUT must be 3d.");
        }
        if (!type.getElement().isCompatible(this.mElement)) {
            throw new RSIllegalArgumentException("LUT element type must match.");
        }
        this.mLUT = allocation;
        setVar(0, allocation);
    }

    public void forEach(Allocation allocation, Allocation allocation2) {
        forEach(0, allocation, allocation2, null);
    }

    public Script.KernelID getKernelID() {
        return createKernelID(0, 3, null, null);
    }
}
