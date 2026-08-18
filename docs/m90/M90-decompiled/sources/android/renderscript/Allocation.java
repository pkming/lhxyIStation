package android.renderscript;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Trace;
import android.renderscript.Element;
import android.renderscript.Type;
import android.util.Log;
import android.view.Surface;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class Allocation extends BaseObj {
    public static final int USAGE_GRAPHICS_CONSTANTS = 8;
    public static final int USAGE_GRAPHICS_RENDER_TARGET = 16;
    public static final int USAGE_GRAPHICS_TEXTURE = 2;
    public static final int USAGE_GRAPHICS_VERTEX = 4;
    public static final int USAGE_IO_INPUT = 32;
    public static final int USAGE_IO_OUTPUT = 64;
    public static final int USAGE_SCRIPT = 1;
    public static final int USAGE_SHARED = 128;
    static HashMap<Integer, Allocation> mAllocationMap = new HashMap<>();
    static BitmapFactory.Options mBitmapOptions;
    Allocation mAdaptedAllocation;
    Bitmap mBitmap;
    OnBufferAvailableListener mBufferNotifier;
    boolean mConstrainedFace;
    boolean mConstrainedLOD;
    boolean mConstrainedY;
    boolean mConstrainedZ;
    int mCurrentCount;
    int mCurrentDimX;
    int mCurrentDimY;
    int mCurrentDimZ;
    boolean mReadAllowed;
    Type.CubemapFace mSelectedFace;
    int mSelectedLOD;
    int mSelectedY;
    int mSelectedZ;
    int mSize;
    Type mType;
    int mUsage;
    boolean mWriteAllowed;

    public interface OnBufferAvailableListener {
        void onBufferAvailable(Allocation allocation);
    }

    static {
        BitmapFactory.Options options = new BitmapFactory.Options();
        mBitmapOptions = options;
        options.inScaled = false;
    }

    public enum MipmapControl {
        MIPMAP_NONE(0),
        MIPMAP_FULL(1),
        MIPMAP_ON_SYNC_TO_TEXTURE(2);

        int mID;

        MipmapControl(int i) {
            this.mID = i;
        }
    }

    private int getIDSafe() {
        Allocation allocation = this.mAdaptedAllocation;
        if (allocation != null) {
            return allocation.getID(this.mRS);
        }
        return getID(this.mRS);
    }

    public Element getElement() {
        return this.mType.getElement();
    }

    public int getUsage() {
        return this.mUsage;
    }

    public int getBytesSize() {
        return this.mType.getCount() * this.mType.getElement().getBytesSize();
    }

    private void updateCacheInfo(Type type) {
        this.mCurrentDimX = type.getX();
        this.mCurrentDimY = type.getY();
        int z = type.getZ();
        this.mCurrentDimZ = z;
        int i = this.mCurrentDimX;
        this.mCurrentCount = i;
        int i2 = this.mCurrentDimY;
        if (i2 > 1) {
            this.mCurrentCount = i * i2;
        }
        if (z > 1) {
            this.mCurrentCount *= z;
        }
    }

    private void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    Allocation(int i, RenderScript renderScript, Type type, int i2) {
        super(i, renderScript);
        this.mReadAllowed = true;
        this.mWriteAllowed = true;
        this.mSelectedFace = Type.CubemapFace.POSITIVE_X;
        if ((i2 & (-256)) != 0) {
            throw new RSIllegalArgumentException("Unknown usage specified.");
        }
        if ((i2 & 32) != 0) {
            this.mWriteAllowed = false;
            if ((i2 & (-36)) != 0) {
                throw new RSIllegalArgumentException("Invalid usage combination.");
            }
        }
        this.mType = type;
        this.mUsage = i2;
        if (type != null) {
            this.mSize = type.getCount() * this.mType.getElement().getBytesSize();
            updateCacheInfo(type);
        }
        try {
            RenderScript.registerNativeAllocation.invoke(RenderScript.sRuntime, Integer.valueOf(this.mSize));
        } catch (Exception e) {
            Log.e("RenderScript_jni", "Couldn't invoke registerNativeAllocation:" + e);
            throw new RSRuntimeException("Couldn't invoke registerNativeAllocation:" + e);
        }
    }

    @Override // android.renderscript.BaseObj
    protected void finalize() throws Throwable {
        RenderScript.registerNativeFree.invoke(RenderScript.sRuntime, Integer.valueOf(this.mSize));
        super.finalize();
    }

    private void validateIsInt32() {
        if (this.mType.mElement.mType != Element.DataType.SIGNED_32 && this.mType.mElement.mType != Element.DataType.UNSIGNED_32) {
            throw new RSIllegalArgumentException("32 bit integer source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsInt16() {
        if (this.mType.mElement.mType != Element.DataType.SIGNED_16 && this.mType.mElement.mType != Element.DataType.UNSIGNED_16) {
            throw new RSIllegalArgumentException("16 bit integer source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsInt8() {
        if (this.mType.mElement.mType != Element.DataType.SIGNED_8 && this.mType.mElement.mType != Element.DataType.UNSIGNED_8) {
            throw new RSIllegalArgumentException("8 bit integer source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsFloat32() {
        if (this.mType.mElement.mType != Element.DataType.FLOAT_32) {
            throw new RSIllegalArgumentException("32 bit float source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsObject() {
        if (this.mType.mElement.mType != Element.DataType.RS_ELEMENT && this.mType.mElement.mType != Element.DataType.RS_TYPE && this.mType.mElement.mType != Element.DataType.RS_ALLOCATION && this.mType.mElement.mType != Element.DataType.RS_SAMPLER && this.mType.mElement.mType != Element.DataType.RS_SCRIPT && this.mType.mElement.mType != Element.DataType.RS_MESH && this.mType.mElement.mType != Element.DataType.RS_PROGRAM_FRAGMENT && this.mType.mElement.mType != Element.DataType.RS_PROGRAM_VERTEX && this.mType.mElement.mType != Element.DataType.RS_PROGRAM_RASTER && this.mType.mElement.mType != Element.DataType.RS_PROGRAM_STORE) {
            throw new RSIllegalArgumentException("Object source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    @Override // android.renderscript.BaseObj
    void updateFromNative() {
        super.updateFromNative();
        int iNAllocationGetType = this.mRS.nAllocationGetType(getID(this.mRS));
        if (iNAllocationGetType != 0) {
            Type type = new Type(iNAllocationGetType, this.mRS);
            this.mType = type;
            type.updateFromNative();
            updateCacheInfo(this.mType);
        }
    }

    public Type getType() {
        return this.mType;
    }

    public void syncAll(int i) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "syncAll");
        if (i == 1 || i == 2) {
            if ((this.mUsage & 128) != 0) {
                copyFrom(this.mBitmap);
            }
        } else if (i != 4 && i != 8) {
            if (i == 128) {
                if ((this.mUsage & 128) != 0) {
                    copyTo(this.mBitmap);
                }
            } else {
                throw new RSIllegalArgumentException("Source must be exactly one usage type.");
            }
        }
        this.mRS.validate();
        this.mRS.nAllocationSyncAll(getIDSafe(), i);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void ioSend() {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "ioSend");
        if ((this.mUsage & 64) == 0) {
            throw new RSIllegalArgumentException("Can only send buffer if IO_OUTPUT usage specified.");
        }
        this.mRS.validate();
        this.mRS.nAllocationIoSend(getID(this.mRS));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void ioSendOutput() {
        ioSend();
    }

    public void ioReceive() {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "ioReceive");
        if ((this.mUsage & 32) == 0) {
            throw new RSIllegalArgumentException("Can only receive if IO_INPUT usage specified.");
        }
        this.mRS.validate();
        this.mRS.nAllocationIoReceive(getID(this.mRS));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(BaseObj[] baseObjArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        validateIsObject();
        if (baseObjArr.length != this.mCurrentCount) {
            throw new RSIllegalArgumentException("Array size mismatch, allocation sizeX = " + this.mCurrentCount + ", array length = " + baseObjArr.length);
        }
        int[] iArr = new int[baseObjArr.length];
        for (int i = 0; i < baseObjArr.length; i++) {
            iArr[i] = baseObjArr[i].getID(this.mRS);
        }
        copy1DRangeFromUnchecked(0, this.mCurrentCount, iArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    private void validateBitmapFormat(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            throw new RSIllegalArgumentException("Bitmap has an unsupported format for this operation");
        }
        int i = AnonymousClass1.$SwitchMap$android$graphics$Bitmap$Config[config.ordinal()];
        if (i == 1) {
            if (this.mType.getElement().mKind != Element.DataKind.PIXEL_A) {
                throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + config);
            }
            return;
        }
        if (i == 2) {
            if (this.mType.getElement().mKind != Element.DataKind.PIXEL_RGBA || this.mType.getElement().getBytesSize() != 4) {
                throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + config);
            }
        } else if (i == 3) {
            if (this.mType.getElement().mKind != Element.DataKind.PIXEL_RGB || this.mType.getElement().getBytesSize() != 2) {
                throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + config);
            }
        } else {
            if (i != 4) {
                return;
            }
            if (this.mType.getElement().mKind != Element.DataKind.PIXEL_RGBA || this.mType.getElement().getBytesSize() != 2) {
                throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + config);
            }
        }
    }

    /* JADX INFO: renamed from: android.renderscript.Allocation$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config;

        static {
            int[] iArr = new int[Bitmap.Config.values().length];
            $SwitchMap$android$graphics$Bitmap$Config = iArr;
            try {
                iArr[Bitmap.Config.ALPHA_8.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_8888.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.RGB_565.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_4444.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private void validateBitmapSize(Bitmap bitmap) {
        if (this.mCurrentDimX != bitmap.getWidth() || this.mCurrentDimY != bitmap.getHeight()) {
            throw new RSIllegalArgumentException("Cannot update allocation from bitmap, sizes mismatch");
        }
    }

    public void copyFromUnchecked(int[] iArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        this.mRS.validate();
        int i = this.mCurrentDimZ;
        if (i > 0) {
            copy3DRangeFromUnchecked(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, i, iArr);
        } else {
            int i2 = this.mCurrentDimY;
            if (i2 > 0) {
                copy2DRangeFromUnchecked(0, 0, this.mCurrentDimX, i2, iArr);
            } else {
                copy1DRangeFromUnchecked(0, this.mCurrentCount, iArr);
            }
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFromUnchecked(short[] sArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        this.mRS.validate();
        int i = this.mCurrentDimZ;
        if (i > 0) {
            copy3DRangeFromUnchecked(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, i, sArr);
        } else {
            int i2 = this.mCurrentDimY;
            if (i2 > 0) {
                copy2DRangeFromUnchecked(0, 0, this.mCurrentDimX, i2, sArr);
            } else {
                copy1DRangeFromUnchecked(0, this.mCurrentCount, sArr);
            }
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFromUnchecked(byte[] bArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        this.mRS.validate();
        int i = this.mCurrentDimZ;
        if (i > 0) {
            copy3DRangeFromUnchecked(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, i, bArr);
        } else {
            int i2 = this.mCurrentDimY;
            if (i2 > 0) {
                copy2DRangeFromUnchecked(0, 0, this.mCurrentDimX, i2, bArr);
            } else {
                copy1DRangeFromUnchecked(0, this.mCurrentCount, bArr);
            }
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFromUnchecked(float[] fArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        this.mRS.validate();
        int i = this.mCurrentDimZ;
        if (i > 0) {
            copy3DRangeFromUnchecked(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, i, fArr);
        } else {
            int i2 = this.mCurrentDimY;
            if (i2 > 0) {
                copy2DRangeFromUnchecked(0, 0, this.mCurrentDimX, i2, fArr);
            } else {
                copy1DRangeFromUnchecked(0, this.mCurrentCount, fArr);
            }
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(int[] iArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        int i = this.mCurrentDimZ;
        if (i > 0) {
            copy3DRangeFrom(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, i, iArr);
        } else {
            int i2 = this.mCurrentDimY;
            if (i2 > 0) {
                copy2DRangeFrom(0, 0, this.mCurrentDimX, i2, iArr);
            } else {
                copy1DRangeFrom(0, this.mCurrentCount, iArr);
            }
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(short[] sArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        int i = this.mCurrentDimZ;
        if (i > 0) {
            copy3DRangeFrom(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, i, sArr);
        } else {
            int i2 = this.mCurrentDimY;
            if (i2 > 0) {
                copy2DRangeFrom(0, 0, this.mCurrentDimX, i2, sArr);
            } else {
                copy1DRangeFrom(0, this.mCurrentCount, sArr);
            }
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(byte[] bArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        int i = this.mCurrentDimZ;
        if (i > 0) {
            copy3DRangeFrom(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, i, bArr);
        } else {
            int i2 = this.mCurrentDimY;
            if (i2 > 0) {
                copy2DRangeFrom(0, 0, this.mCurrentDimX, i2, bArr);
            } else {
                copy1DRangeFrom(0, this.mCurrentCount, bArr);
            }
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(float[] fArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        int i = this.mCurrentDimZ;
        if (i > 0) {
            copy3DRangeFrom(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, i, fArr);
        } else {
            int i2 = this.mCurrentDimY;
            if (i2 > 0) {
                copy2DRangeFrom(0, 0, this.mCurrentDimX, i2, fArr);
            } else {
                copy1DRangeFrom(0, this.mCurrentCount, fArr);
            }
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(Bitmap bitmap) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (bitmap.getConfig() == null) {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            new Canvas(bitmapCreateBitmap).drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            copyFrom(bitmapCreateBitmap);
        } else {
            validateBitmapSize(bitmap);
            validateBitmapFormat(bitmap);
            this.mRS.nAllocationCopyFromBitmap(getID(this.mRS), bitmap);
            Trace.traceEnd(Trace.TRACE_TAG_RS);
        }
    }

    public void copyFrom(Allocation allocation) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (!this.mType.equals(allocation.getType())) {
            throw new RSIllegalArgumentException("Types of allocations must match.");
        }
        copy2DRangeFrom(0, 0, this.mCurrentDimX, this.mCurrentDimY, allocation, 0, 0);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void setFromFieldPacker(int i, FieldPacker fieldPacker) {
        this.mRS.validate();
        int bytesSize = this.mType.mElement.getBytesSize();
        byte[] data = fieldPacker.getData();
        int length = data.length / bytesSize;
        if (bytesSize * length != data.length) {
            throw new RSIllegalArgumentException("Field packer length " + data.length + " not divisible by element size " + bytesSize + ".");
        }
        copy1DRangeFromUnchecked(i, length, data);
    }

    public void setFromFieldPacker(int i, int i2, FieldPacker fieldPacker) {
        this.mRS.validate();
        if (i2 >= this.mType.mElement.mElements.length) {
            throw new RSIllegalArgumentException("Component_number " + i2 + " out of range.");
        }
        if (i < 0) {
            throw new RSIllegalArgumentException("Offset must be >= 0.");
        }
        byte[] data = fieldPacker.getData();
        int bytesSize = this.mType.mElement.mElements[i2].getBytesSize() * this.mType.mElement.mArraySizes[i2];
        if (data.length != bytesSize) {
            throw new RSIllegalArgumentException("Field packer sizelength " + data.length + " does not match component size " + bytesSize + ".");
        }
        this.mRS.nAllocationElementData1D(getIDSafe(), i, this.mSelectedLOD, i2, data, data.length);
    }

    private void data1DChecks(int i, int i2, int i3, int i4) {
        this.mRS.validate();
        if (i < 0) {
            throw new RSIllegalArgumentException("Offset must be >= 0.");
        }
        if (i2 < 1) {
            throw new RSIllegalArgumentException("Count must be >= 1.");
        }
        if (i + i2 > this.mCurrentCount) {
            throw new RSIllegalArgumentException("Overflow, Available count " + this.mCurrentCount + ", got " + i2 + " at offset " + i + ".");
        }
        if (i3 < i4) {
            throw new RSIllegalArgumentException("Array too small for allocation type.");
        }
    }

    public void generateMipmaps() {
        this.mRS.nAllocationGenerateMipmaps(getID(this.mRS));
    }

    public void copy1DRangeFromUnchecked(int i, int i2, int[] iArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFromUnchecked");
        int bytesSize = this.mType.mElement.getBytesSize() * i2;
        data1DChecks(i, i2, iArr.length * 4, bytesSize);
        this.mRS.nAllocationData1D(getIDSafe(), i, this.mSelectedLOD, i2, iArr, bytesSize);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFromUnchecked(int i, int i2, short[] sArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFromUnchecked");
        int bytesSize = this.mType.mElement.getBytesSize() * i2;
        data1DChecks(i, i2, sArr.length * 2, bytesSize);
        this.mRS.nAllocationData1D(getIDSafe(), i, this.mSelectedLOD, i2, sArr, bytesSize);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFromUnchecked(int i, int i2, byte[] bArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFromUnchecked");
        int bytesSize = this.mType.mElement.getBytesSize() * i2;
        data1DChecks(i, i2, bArr.length, bytesSize);
        this.mRS.nAllocationData1D(getIDSafe(), i, this.mSelectedLOD, i2, bArr, bytesSize);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFromUnchecked(int i, int i2, float[] fArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFromUnchecked");
        int bytesSize = this.mType.mElement.getBytesSize() * i2;
        data1DChecks(i, i2, fArr.length * 4, bytesSize);
        this.mRS.nAllocationData1D(getIDSafe(), i, this.mSelectedLOD, i2, fArr, bytesSize);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int i, int i2, int[] iArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        validateIsInt32();
        copy1DRangeFromUnchecked(i, i2, iArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int i, int i2, short[] sArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        validateIsInt16();
        copy1DRangeFromUnchecked(i, i2, sArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int i, int i2, byte[] bArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        validateIsInt8();
        copy1DRangeFromUnchecked(i, i2, bArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int i, int i2, float[] fArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        validateIsFloat32();
        copy1DRangeFromUnchecked(i, i2, fArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int i, int i2, Allocation allocation, int i3) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        this.mRS.nAllocationData2D(getIDSafe(), i, 0, this.mSelectedLOD, this.mSelectedFace.mID, i2, 1, allocation.getID(this.mRS), i3, 0, allocation.mSelectedLOD, allocation.mSelectedFace.mID);
    }

    private void validate2DRange(int i, int i2, int i3, int i4) {
        if (this.mAdaptedAllocation != null) {
            return;
        }
        if (i < 0 || i2 < 0) {
            throw new RSIllegalArgumentException("Offset cannot be negative.");
        }
        if (i4 < 0 || i3 < 0) {
            throw new RSIllegalArgumentException("Height or width cannot be negative.");
        }
        if (i + i3 > this.mCurrentDimX || i2 + i4 > this.mCurrentDimY) {
            throw new RSIllegalArgumentException("Updated region larger than allocation.");
        }
    }

    void copy2DRangeFromUnchecked(int i, int i2, int i3, int i4, byte[] bArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFromUnchecked");
        this.mRS.validate();
        validate2DRange(i, i2, i3, i4);
        this.mRS.nAllocationData2D(getIDSafe(), i, i2, this.mSelectedLOD, this.mSelectedFace.mID, i3, i4, bArr, bArr.length);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    void copy2DRangeFromUnchecked(int i, int i2, int i3, int i4, short[] sArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFromUnchecked");
        this.mRS.validate();
        validate2DRange(i, i2, i3, i4);
        this.mRS.nAllocationData2D(getIDSafe(), i, i2, this.mSelectedLOD, this.mSelectedFace.mID, i3, i4, sArr, sArr.length * 2);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    void copy2DRangeFromUnchecked(int i, int i2, int i3, int i4, int[] iArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFromUnchecked");
        this.mRS.validate();
        validate2DRange(i, i2, i3, i4);
        this.mRS.nAllocationData2D(getIDSafe(), i, i2, this.mSelectedLOD, this.mSelectedFace.mID, i3, i4, iArr, iArr.length * 4);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    void copy2DRangeFromUnchecked(int i, int i2, int i3, int i4, float[] fArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFromUnchecked");
        this.mRS.validate();
        validate2DRange(i, i2, i3, i4);
        this.mRS.nAllocationData2D(getIDSafe(), i, i2, this.mSelectedLOD, this.mSelectedFace.mID, i3, i4, fArr, fArr.length * 4);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int i, int i2, int i3, int i4, byte[] bArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        validateIsInt8();
        copy2DRangeFromUnchecked(i, i2, i3, i4, bArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int i, int i2, int i3, int i4, short[] sArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        validateIsInt16();
        copy2DRangeFromUnchecked(i, i2, i3, i4, sArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int i, int i2, int i3, int i4, int[] iArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        validateIsInt32();
        copy2DRangeFromUnchecked(i, i2, i3, i4, iArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int i, int i2, int i3, int i4, float[] fArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        validateIsFloat32();
        copy2DRangeFromUnchecked(i, i2, i3, i4, fArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int i, int i2, int i3, int i4, Allocation allocation, int i5, int i6) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        this.mRS.validate();
        validate2DRange(i, i2, i3, i4);
        this.mRS.nAllocationData2D(getIDSafe(), i, i2, this.mSelectedLOD, this.mSelectedFace.mID, i3, i4, allocation.getID(this.mRS), i5, i6, allocation.mSelectedLOD, allocation.mSelectedFace.mID);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int i, int i2, Bitmap bitmap) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        this.mRS.validate();
        if (bitmap.getConfig() == null) {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            new Canvas(bitmapCreateBitmap).drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            copy2DRangeFrom(i, i2, bitmapCreateBitmap);
        } else {
            validateBitmapFormat(bitmap);
            validate2DRange(i, i2, bitmap.getWidth(), bitmap.getHeight());
            this.mRS.nAllocationData2D(getIDSafe(), i, i2, this.mSelectedLOD, this.mSelectedFace.mID, bitmap);
            Trace.traceEnd(Trace.TRACE_TAG_RS);
        }
    }

    private void validate3DRange(int i, int i2, int i3, int i4, int i5, int i6) {
        if (this.mAdaptedAllocation != null) {
            return;
        }
        if (i < 0 || i2 < 0 || i3 < 0) {
            throw new RSIllegalArgumentException("Offset cannot be negative.");
        }
        if (i5 < 0 || i4 < 0 || i6 < 0) {
            throw new RSIllegalArgumentException("Height or width cannot be negative.");
        }
        if (i + i4 > this.mCurrentDimX || i2 + i5 > this.mCurrentDimY || i3 + i6 > this.mCurrentDimZ) {
            throw new RSIllegalArgumentException("Updated region larger than allocation.");
        }
    }

    void copy3DRangeFromUnchecked(int i, int i2, int i3, int i4, int i5, int i6, byte[] bArr) {
        this.mRS.validate();
        validate3DRange(i, i2, i3, i4, i5, i6);
        this.mRS.nAllocationData3D(getIDSafe(), i, i2, i3, this.mSelectedLOD, i4, i5, i6, bArr, bArr.length);
    }

    void copy3DRangeFromUnchecked(int i, int i2, int i3, int i4, int i5, int i6, short[] sArr) {
        this.mRS.validate();
        validate3DRange(i, i2, i3, i4, i5, i6);
        this.mRS.nAllocationData3D(getIDSafe(), i, i2, i3, this.mSelectedLOD, i4, i5, i6, sArr, sArr.length * 2);
    }

    void copy3DRangeFromUnchecked(int i, int i2, int i3, int i4, int i5, int i6, int[] iArr) {
        this.mRS.validate();
        validate3DRange(i, i2, i3, i4, i5, i6);
        this.mRS.nAllocationData3D(getIDSafe(), i, i2, i3, this.mSelectedLOD, i4, i5, i6, iArr, iArr.length * 4);
    }

    void copy3DRangeFromUnchecked(int i, int i2, int i3, int i4, int i5, int i6, float[] fArr) {
        this.mRS.validate();
        validate3DRange(i, i2, i3, i4, i5, i6);
        this.mRS.nAllocationData3D(getIDSafe(), i, i2, i3, this.mSelectedLOD, i4, i5, i6, fArr, fArr.length * 4);
    }

    public void copy3DRangeFrom(int i, int i2, int i3, int i4, int i5, int i6, byte[] bArr) {
        validateIsInt8();
        copy3DRangeFromUnchecked(i, i2, i3, i4, i5, i6, bArr);
    }

    public void copy3DRangeFrom(int i, int i2, int i3, int i4, int i5, int i6, short[] sArr) {
        validateIsInt16();
        copy3DRangeFromUnchecked(i, i2, i3, i4, i5, i6, sArr);
    }

    public void copy3DRangeFrom(int i, int i2, int i3, int i4, int i5, int i6, int[] iArr) {
        validateIsInt32();
        copy3DRangeFromUnchecked(i, i2, i3, i4, i5, i6, iArr);
    }

    public void copy3DRangeFrom(int i, int i2, int i3, int i4, int i5, int i6, float[] fArr) {
        validateIsFloat32();
        copy3DRangeFromUnchecked(i, i2, i3, i4, i5, i6, fArr);
    }

    public void copy3DRangeFrom(int i, int i2, int i3, int i4, int i5, int i6, Allocation allocation, int i7, int i8, int i9) {
        this.mRS.validate();
        validate3DRange(i, i2, i3, i4, i5, i6);
        this.mRS.nAllocationData3D(getIDSafe(), i, i2, i3, this.mSelectedLOD, i4, i5, i6, allocation.getID(this.mRS), i7, i8, i9, allocation.mSelectedLOD);
    }

    public void copyTo(Bitmap bitmap) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        this.mRS.validate();
        validateBitmapFormat(bitmap);
        validateBitmapSize(bitmap);
        this.mRS.nAllocationCopyToBitmap(getID(this.mRS), bitmap);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyTo(byte[] bArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        validateIsInt8();
        this.mRS.validate();
        this.mRS.nAllocationRead(getID(this.mRS), bArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyTo(short[] sArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        validateIsInt16();
        this.mRS.validate();
        this.mRS.nAllocationRead(getID(this.mRS), sArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyTo(int[] iArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        validateIsInt32();
        this.mRS.validate();
        this.mRS.nAllocationRead(getID(this.mRS), iArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyTo(float[] fArr) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        validateIsFloat32();
        this.mRS.validate();
        this.mRS.nAllocationRead(getID(this.mRS), fArr);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public synchronized void resize(int i) {
        if (this.mType.getY() > 0 || this.mType.getZ() > 0 || this.mType.hasFaces() || this.mType.hasMipmaps()) {
            throw new RSInvalidStateException("Resize only support for 1D allocations at this time.");
        }
        this.mRS.nAllocationResize1D(getID(this.mRS), i);
        this.mRS.finish();
        Type type = new Type(this.mRS.nAllocationGetType(getID(this.mRS)), this.mRS);
        this.mType = type;
        type.updateFromNative();
        updateCacheInfo(this.mType);
    }

    public static Allocation createTyped(RenderScript renderScript, Type type, MipmapControl mipmapControl, int i) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "createTyped");
        renderScript.validate();
        if (type.getID(renderScript) == 0) {
            throw new RSInvalidStateException("Bad Type");
        }
        int iNAllocationCreateTyped = renderScript.nAllocationCreateTyped(type.getID(renderScript), mipmapControl.mID, i, 0);
        if (iNAllocationCreateTyped == 0) {
            throw new RSRuntimeException("Allocation creation failed.");
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
        return new Allocation(iNAllocationCreateTyped, renderScript, type, i);
    }

    public static Allocation createTyped(RenderScript renderScript, Type type, int i) {
        return createTyped(renderScript, type, MipmapControl.MIPMAP_NONE, i);
    }

    public static Allocation createTyped(RenderScript renderScript, Type type) {
        return createTyped(renderScript, type, MipmapControl.MIPMAP_NONE, 1);
    }

    public static Allocation createSized(RenderScript renderScript, Element element, int i, int i2) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "createSized");
        renderScript.validate();
        Type.Builder builder = new Type.Builder(renderScript, element);
        builder.setX(i);
        Type typeCreate = builder.create();
        int iNAllocationCreateTyped = renderScript.nAllocationCreateTyped(typeCreate.getID(renderScript), MipmapControl.MIPMAP_NONE.mID, i2, 0);
        if (iNAllocationCreateTyped == 0) {
            throw new RSRuntimeException("Allocation creation failed.");
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
        return new Allocation(iNAllocationCreateTyped, renderScript, typeCreate, i2);
    }

    public static Allocation createSized(RenderScript renderScript, Element element, int i) {
        return createSized(renderScript, element, i, 1);
    }

    static Element elementFromBitmap(RenderScript renderScript, Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == Bitmap.Config.ALPHA_8) {
            return Element.A_8(renderScript);
        }
        if (config == Bitmap.Config.ARGB_4444) {
            return Element.RGBA_4444(renderScript);
        }
        if (config == Bitmap.Config.ARGB_8888) {
            return Element.RGBA_8888(renderScript);
        }
        if (config == Bitmap.Config.RGB_565) {
            return Element.RGB_565(renderScript);
        }
        throw new RSInvalidStateException("Bad bitmap type: " + config);
    }

    static Type typeFromBitmap(RenderScript renderScript, Bitmap bitmap, MipmapControl mipmapControl) {
        Type.Builder builder = new Type.Builder(renderScript, elementFromBitmap(renderScript, bitmap));
        builder.setX(bitmap.getWidth());
        builder.setY(bitmap.getHeight());
        builder.setMipmaps(mipmapControl == MipmapControl.MIPMAP_FULL);
        return builder.create();
    }

    public static Allocation createFromBitmap(RenderScript renderScript, Bitmap bitmap, MipmapControl mipmapControl, int i) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "createFromBitmap");
        renderScript.validate();
        if (bitmap.getConfig() == null) {
            if ((i & 128) != 0) {
                throw new RSIllegalArgumentException("USAGE_SHARED cannot be used with a Bitmap that has a null config.");
            }
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            new Canvas(bitmapCreateBitmap).drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            return createFromBitmap(renderScript, bitmapCreateBitmap, mipmapControl, i);
        }
        Type typeTypeFromBitmap = typeFromBitmap(renderScript, bitmap, mipmapControl);
        if (mipmapControl == MipmapControl.MIPMAP_NONE && typeTypeFromBitmap.getElement().isCompatible(Element.RGBA_8888(renderScript)) && i == 131) {
            int iNAllocationCreateBitmapBackedAllocation = renderScript.nAllocationCreateBitmapBackedAllocation(typeTypeFromBitmap.getID(renderScript), mipmapControl.mID, bitmap, i);
            if (iNAllocationCreateBitmapBackedAllocation == 0) {
                throw new RSRuntimeException("Load failed.");
            }
            Allocation allocation = new Allocation(iNAllocationCreateBitmapBackedAllocation, renderScript, typeTypeFromBitmap, i);
            allocation.setBitmap(bitmap);
            return allocation;
        }
        int iNAllocationCreateFromBitmap = renderScript.nAllocationCreateFromBitmap(typeTypeFromBitmap.getID(renderScript), mipmapControl.mID, bitmap, i);
        if (iNAllocationCreateFromBitmap == 0) {
            throw new RSRuntimeException("Load failed.");
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
        return new Allocation(iNAllocationCreateFromBitmap, renderScript, typeTypeFromBitmap, i);
    }

    public Surface getSurface() {
        if ((this.mUsage & 32) == 0) {
            throw new RSInvalidStateException("Allocation is not a surface texture.");
        }
        return this.mRS.nAllocationGetSurface(getID(this.mRS));
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        setSurface(new Surface(surfaceTexture));
    }

    public void setSurface(Surface surface) {
        this.mRS.validate();
        if ((this.mUsage & 64) == 0) {
            throw new RSInvalidStateException("Allocation is not USAGE_IO_OUTPUT.");
        }
        this.mRS.nAllocationSetSurface(getID(this.mRS), surface);
    }

    public static Allocation createFromBitmap(RenderScript renderScript, Bitmap bitmap) {
        if (renderScript.getApplicationContext().getApplicationInfo().targetSdkVersion >= 18) {
            return createFromBitmap(renderScript, bitmap, MipmapControl.MIPMAP_NONE, 131);
        }
        return createFromBitmap(renderScript, bitmap, MipmapControl.MIPMAP_NONE, 2);
    }

    public static Allocation createCubemapFromBitmap(RenderScript renderScript, Bitmap bitmap, MipmapControl mipmapControl, int i) {
        renderScript.validate();
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        if (width % 6 != 0) {
            throw new RSIllegalArgumentException("Cubemap height must be multiple of 6");
        }
        if (width / 6 != height) {
            throw new RSIllegalArgumentException("Only square cube map faces supported");
        }
        if (!(((height + (-1)) & height) == 0)) {
            throw new RSIllegalArgumentException("Only power of 2 cube faces supported");
        }
        Element elementElementFromBitmap = elementFromBitmap(renderScript, bitmap);
        Type.Builder builder = new Type.Builder(renderScript, elementElementFromBitmap);
        builder.setX(height);
        builder.setY(height);
        builder.setFaces(true);
        builder.setMipmaps(mipmapControl == MipmapControl.MIPMAP_FULL);
        Type typeCreate = builder.create();
        int iNAllocationCubeCreateFromBitmap = renderScript.nAllocationCubeCreateFromBitmap(typeCreate.getID(renderScript), mipmapControl.mID, bitmap, i);
        if (iNAllocationCubeCreateFromBitmap == 0) {
            throw new RSRuntimeException("Load failed for bitmap " + bitmap + " element " + elementElementFromBitmap);
        }
        return new Allocation(iNAllocationCubeCreateFromBitmap, renderScript, typeCreate, i);
    }

    public static Allocation createCubemapFromBitmap(RenderScript renderScript, Bitmap bitmap) {
        return createCubemapFromBitmap(renderScript, bitmap, MipmapControl.MIPMAP_NONE, 2);
    }

    public static Allocation createCubemapFromCubeFaces(RenderScript renderScript, Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, Bitmap bitmap4, Bitmap bitmap5, Bitmap bitmap6, MipmapControl mipmapControl, int i) {
        int height = bitmap.getHeight();
        if (bitmap.getWidth() != height || bitmap2.getWidth() != height || bitmap2.getHeight() != height || bitmap3.getWidth() != height || bitmap3.getHeight() != height || bitmap4.getWidth() != height || bitmap4.getHeight() != height || bitmap5.getWidth() != height || bitmap5.getHeight() != height || bitmap6.getWidth() != height || bitmap6.getHeight() != height) {
            throw new RSIllegalArgumentException("Only square cube map faces supported");
        }
        if (!(((height + (-1)) & height) == 0)) {
            throw new RSIllegalArgumentException("Only power of 2 cube faces supported");
        }
        Type.Builder builder = new Type.Builder(renderScript, elementFromBitmap(renderScript, bitmap));
        builder.setX(height);
        builder.setY(height);
        builder.setFaces(true);
        builder.setMipmaps(mipmapControl == MipmapControl.MIPMAP_FULL);
        Allocation allocationCreateTyped = createTyped(renderScript, builder.create(), mipmapControl, i);
        AllocationAdapter allocationAdapterCreate2D = AllocationAdapter.create2D(renderScript, allocationCreateTyped);
        allocationAdapterCreate2D.setFace(Type.CubemapFace.POSITIVE_X);
        allocationAdapterCreate2D.copyFrom(bitmap);
        allocationAdapterCreate2D.setFace(Type.CubemapFace.NEGATIVE_X);
        allocationAdapterCreate2D.copyFrom(bitmap2);
        allocationAdapterCreate2D.setFace(Type.CubemapFace.POSITIVE_Y);
        allocationAdapterCreate2D.copyFrom(bitmap3);
        allocationAdapterCreate2D.setFace(Type.CubemapFace.NEGATIVE_Y);
        allocationAdapterCreate2D.copyFrom(bitmap4);
        allocationAdapterCreate2D.setFace(Type.CubemapFace.POSITIVE_Z);
        allocationAdapterCreate2D.copyFrom(bitmap5);
        allocationAdapterCreate2D.setFace(Type.CubemapFace.NEGATIVE_Z);
        allocationAdapterCreate2D.copyFrom(bitmap6);
        return allocationCreateTyped;
    }

    public static Allocation createCubemapFromCubeFaces(RenderScript renderScript, Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, Bitmap bitmap4, Bitmap bitmap5, Bitmap bitmap6) {
        return createCubemapFromCubeFaces(renderScript, bitmap, bitmap2, bitmap3, bitmap4, bitmap5, bitmap6, MipmapControl.MIPMAP_NONE, 2);
    }

    public static Allocation createFromBitmapResource(RenderScript renderScript, Resources resources, int i, MipmapControl mipmapControl, int i2) {
        renderScript.validate();
        if ((i2 & 224) != 0) {
            throw new RSIllegalArgumentException("Unsupported usage specified.");
        }
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(resources, i);
        Allocation allocationCreateFromBitmap = createFromBitmap(renderScript, bitmapDecodeResource, mipmapControl, i2);
        bitmapDecodeResource.recycle();
        return allocationCreateFromBitmap;
    }

    public static Allocation createFromBitmapResource(RenderScript renderScript, Resources resources, int i) {
        if (renderScript.getApplicationContext().getApplicationInfo().targetSdkVersion >= 18) {
            return createFromBitmapResource(renderScript, resources, i, MipmapControl.MIPMAP_NONE, 3);
        }
        return createFromBitmapResource(renderScript, resources, i, MipmapControl.MIPMAP_NONE, 2);
    }

    public static Allocation createFromString(RenderScript renderScript, String str, int i) {
        renderScript.validate();
        try {
            byte[] bytes = str.getBytes("UTF-8");
            Allocation allocationCreateSized = createSized(renderScript, Element.U8(renderScript), bytes.length, i);
            allocationCreateSized.copyFrom(bytes);
            return allocationCreateSized;
        } catch (Exception unused) {
            throw new RSRuntimeException("Could not convert string to utf-8.");
        }
    }

    public void setOnBufferAvailableListener(OnBufferAvailableListener onBufferAvailableListener) {
        synchronized (mAllocationMap) {
            mAllocationMap.put(new Integer(getID(this.mRS)), this);
            this.mBufferNotifier = onBufferAvailableListener;
        }
    }

    static void sendBufferNotification(int i) {
        OnBufferAvailableListener onBufferAvailableListener;
        synchronized (mAllocationMap) {
            Allocation allocation = mAllocationMap.get(new Integer(i));
            if (allocation != null && (onBufferAvailableListener = allocation.mBufferNotifier) != null) {
                onBufferAvailableListener.onBufferAvailable(allocation);
            }
        }
    }
}
