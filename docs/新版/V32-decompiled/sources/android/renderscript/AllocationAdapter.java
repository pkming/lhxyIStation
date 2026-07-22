package android.renderscript;

import android.renderscript.Type;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AllocationAdapter extends Allocation {
    AllocationAdapter(int i, RenderScript renderScript, Allocation allocation) {
        super(i, renderScript, allocation.mType, allocation.mUsage);
        this.mAdaptedAllocation = allocation;
    }

    @Override // android.renderscript.BaseObj
    int getID(RenderScript renderScript) {
        throw new RSInvalidStateException("This operation is not supported with adapters at this time.");
    }

    public void subData(int i, FieldPacker fieldPacker) {
        super.setFromFieldPacker(i, fieldPacker);
    }

    public void subElementData(int i, int i2, FieldPacker fieldPacker) {
        super.setFromFieldPacker(i, i2, fieldPacker);
    }

    public void subData1D(int i, int i2, int[] iArr) {
        super.copy1DRangeFrom(i, i2, iArr);
    }

    public void subData1D(int i, int i2, short[] sArr) {
        super.copy1DRangeFrom(i, i2, sArr);
    }

    public void subData1D(int i, int i2, byte[] bArr) {
        super.copy1DRangeFrom(i, i2, bArr);
    }

    public void subData1D(int i, int i2, float[] fArr) {
        super.copy1DRangeFrom(i, i2, fArr);
    }

    public void subData2D(int i, int i2, int i3, int i4, int[] iArr) {
        super.copy2DRangeFrom(i, i2, i3, i4, iArr);
    }

    public void subData2D(int i, int i2, int i3, int i4, float[] fArr) {
        super.copy2DRangeFrom(i, i2, i3, i4, fArr);
    }

    public void readData(int[] iArr) {
        super.copyTo(iArr);
    }

    public void readData(float[] fArr) {
        super.copyTo(fArr);
    }

    void initLOD(int i) {
        if (i < 0) {
            throw new RSIllegalArgumentException("Attempting to set negative lod (" + i + ").");
        }
        int x = this.mAdaptedAllocation.mType.getX();
        int y = this.mAdaptedAllocation.mType.getY();
        int z = this.mAdaptedAllocation.mType.getZ();
        for (int i2 = 0; i2 < i; i2++) {
            if (x == 1 && y == 1 && z == 1) {
                throw new RSIllegalArgumentException("Attempting to set lod (" + i + ") out of range.");
            }
            if (x > 1) {
                x >>= 1;
            }
            if (y > 1) {
                y >>= 1;
            }
            if (z > 1) {
                z >>= 1;
            }
        }
        this.mCurrentDimX = x;
        this.mCurrentDimY = y;
        this.mCurrentDimZ = z;
        this.mCurrentCount = this.mCurrentDimX;
        if (this.mCurrentDimY > 1) {
            this.mCurrentCount *= this.mCurrentDimY;
        }
        if (this.mCurrentDimZ > 1) {
            this.mCurrentCount *= this.mCurrentDimZ;
        }
        this.mSelectedY = 0;
        this.mSelectedZ = 0;
    }

    public void setLOD(int i) {
        if (!this.mAdaptedAllocation.getType().hasMipmaps()) {
            throw new RSInvalidStateException("Cannot set LOD when the allocation type does not include mipmaps.");
        }
        if (!this.mConstrainedLOD) {
            throw new RSInvalidStateException("Cannot set LOD when the adapter includes mipmaps.");
        }
        initLOD(i);
    }

    public void setFace(Type.CubemapFace cubemapFace) {
        if (!this.mAdaptedAllocation.getType().hasFaces()) {
            throw new RSInvalidStateException("Cannot set Face when the allocation type does not include faces.");
        }
        if (!this.mConstrainedFace) {
            throw new RSInvalidStateException("Cannot set LOD when the adapter includes mipmaps.");
        }
        if (cubemapFace == null) {
            throw new RSIllegalArgumentException("Cannot set null face.");
        }
        this.mSelectedFace = cubemapFace;
    }

    public void setY(int i) {
        if (this.mAdaptedAllocation.getType().getY() == 0) {
            throw new RSInvalidStateException("Cannot set Y when the allocation type does not include Y dim.");
        }
        if (this.mAdaptedAllocation.getType().getY() <= i) {
            throw new RSInvalidStateException("Cannot set Y greater than dimension of allocation.");
        }
        if (!this.mConstrainedY) {
            throw new RSInvalidStateException("Cannot set Y when the adapter includes Y.");
        }
        this.mSelectedY = i;
    }

    public void setZ(int i) {
        if (this.mAdaptedAllocation.getType().getZ() == 0) {
            throw new RSInvalidStateException("Cannot set Z when the allocation type does not include Z dim.");
        }
        if (this.mAdaptedAllocation.getType().getZ() <= i) {
            throw new RSInvalidStateException("Cannot set Z greater than dimension of allocation.");
        }
        if (!this.mConstrainedZ) {
            throw new RSInvalidStateException("Cannot set Z when the adapter includes Z.");
        }
        this.mSelectedZ = i;
    }

    public static AllocationAdapter create1D(RenderScript renderScript, Allocation allocation) {
        renderScript.validate();
        AllocationAdapter allocationAdapter = new AllocationAdapter(0, renderScript, allocation);
        allocationAdapter.mConstrainedLOD = true;
        allocationAdapter.mConstrainedFace = true;
        allocationAdapter.mConstrainedY = true;
        allocationAdapter.mConstrainedZ = true;
        allocationAdapter.initLOD(0);
        return allocationAdapter;
    }

    public static AllocationAdapter create2D(RenderScript renderScript, Allocation allocation) {
        Log.e("rs", "create2d " + allocation);
        renderScript.validate();
        AllocationAdapter allocationAdapter = new AllocationAdapter(0, renderScript, allocation);
        allocationAdapter.mConstrainedLOD = true;
        allocationAdapter.mConstrainedFace = true;
        allocationAdapter.mConstrainedY = false;
        allocationAdapter.mConstrainedZ = true;
        allocationAdapter.initLOD(0);
        return allocationAdapter;
    }

    @Override // android.renderscript.Allocation
    public synchronized void resize(int i) {
        throw new RSInvalidStateException("Resize not allowed for Adapters.");
    }
}
