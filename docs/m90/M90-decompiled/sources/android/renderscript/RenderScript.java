package android.renderscript;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Surface;
import java.io.File;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class RenderScript {
    static final boolean DEBUG = false;
    static final boolean LOG_ENABLED = false;
    static final String LOG_TAG = "RenderScript_jni";
    static final long TRACE_TAG = 32768;
    static File mCacheDir = null;
    static Method registerNativeAllocation = null;
    static Method registerNativeFree = null;
    static boolean sInitialized = false;
    static Object sRuntime;
    private Context mApplicationContext;
    int mContext;
    int mDev;
    Element mElement_ALLOCATION;
    Element mElement_A_8;
    Element mElement_BOOLEAN;
    Element mElement_CHAR_2;
    Element mElement_CHAR_3;
    Element mElement_CHAR_4;
    Element mElement_DOUBLE_2;
    Element mElement_DOUBLE_3;
    Element mElement_DOUBLE_4;
    Element mElement_ELEMENT;
    Element mElement_F32;
    Element mElement_F64;
    Element mElement_FLOAT_2;
    Element mElement_FLOAT_3;
    Element mElement_FLOAT_4;
    Element mElement_FONT;
    Element mElement_I16;
    Element mElement_I32;
    Element mElement_I64;
    Element mElement_I8;
    Element mElement_INT_2;
    Element mElement_INT_3;
    Element mElement_INT_4;
    Element mElement_LONG_2;
    Element mElement_LONG_3;
    Element mElement_LONG_4;
    Element mElement_MATRIX_2X2;
    Element mElement_MATRIX_3X3;
    Element mElement_MATRIX_4X4;
    Element mElement_MESH;
    Element mElement_PROGRAM_FRAGMENT;
    Element mElement_PROGRAM_RASTER;
    Element mElement_PROGRAM_STORE;
    Element mElement_PROGRAM_VERTEX;
    Element mElement_RGBA_4444;
    Element mElement_RGBA_5551;
    Element mElement_RGBA_8888;
    Element mElement_RGB_565;
    Element mElement_RGB_888;
    Element mElement_SAMPLER;
    Element mElement_SCRIPT;
    Element mElement_SHORT_2;
    Element mElement_SHORT_3;
    Element mElement_SHORT_4;
    Element mElement_TYPE;
    Element mElement_U16;
    Element mElement_U32;
    Element mElement_U64;
    Element mElement_U8;
    Element mElement_UCHAR_2;
    Element mElement_UCHAR_3;
    Element mElement_UCHAR_4;
    Element mElement_UINT_2;
    Element mElement_UINT_3;
    Element mElement_UINT_4;
    Element mElement_ULONG_2;
    Element mElement_ULONG_3;
    Element mElement_ULONG_4;
    Element mElement_USHORT_2;
    Element mElement_USHORT_3;
    Element mElement_USHORT_4;
    Element mElement_YUV;
    MessageThread mMessageThread;
    ProgramRaster mProgramRaster_CULL_BACK;
    ProgramRaster mProgramRaster_CULL_FRONT;
    ProgramRaster mProgramRaster_CULL_NONE;
    ProgramStore mProgramStore_BLEND_ALPHA_DEPTH_NO_DEPTH;
    ProgramStore mProgramStore_BLEND_ALPHA_DEPTH_TEST;
    ProgramStore mProgramStore_BLEND_NONE_DEPTH_NO_DEPTH;
    ProgramStore mProgramStore_BLEND_NONE_DEPTH_TEST;
    Sampler mSampler_CLAMP_LINEAR;
    Sampler mSampler_CLAMP_LINEAR_MIP_LINEAR;
    Sampler mSampler_CLAMP_NEAREST;
    Sampler mSampler_MIRRORED_REPEAT_LINEAR;
    Sampler mSampler_MIRRORED_REPEAT_LINEAR_MIP_LINEAR;
    Sampler mSampler_MIRRORED_REPEAT_NEAREST;
    Sampler mSampler_WRAP_LINEAR;
    Sampler mSampler_WRAP_LINEAR_MIP_LINEAR;
    Sampler mSampler_WRAP_NEAREST;
    RSMessageHandler mMessageCallback = null;
    RSErrorHandler mErrorCallback = null;
    ContextType mContextType = ContextType.NORMAL;

    public static class RSErrorHandler implements Runnable {
        protected String mErrorMessage;
        protected int mErrorNum;

        @Override // java.lang.Runnable
        public void run() {
        }
    }

    public static class RSMessageHandler implements Runnable {
        protected int[] mData;
        protected int mID;
        protected int mLength;

        @Override // java.lang.Runnable
        public void run() {
        }
    }

    static native void _nInit();

    native void nContextDeinitToClient(int i);

    native String nContextGetErrorMessage(int i);

    native int nContextGetUserMessage(int i, int[] iArr);

    native void nContextInitToClient(int i);

    native int nContextPeekMessage(int i, int[] iArr);

    native int nDeviceCreate();

    native void nDeviceDestroy(int i);

    native void nDeviceSetConfig(int i, int i2, int i3);

    native void rsnAllocationCopyFromBitmap(int i, int i2, Bitmap bitmap);

    native void rsnAllocationCopyToBitmap(int i, int i2, Bitmap bitmap);

    native int rsnAllocationCreateBitmapBackedAllocation(int i, int i2, int i3, Bitmap bitmap, int i4);

    native int rsnAllocationCreateBitmapRef(int i, int i2, Bitmap bitmap);

    native int rsnAllocationCreateFromAssetStream(int i, int i2, int i3, int i4);

    native int rsnAllocationCreateFromBitmap(int i, int i2, int i3, Bitmap bitmap, int i4);

    native int rsnAllocationCreateTyped(int i, int i2, int i3, int i4, int i5);

    native int rsnAllocationCubeCreateFromBitmap(int i, int i2, int i3, Bitmap bitmap, int i4);

    native void rsnAllocationData1D(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6);

    native void rsnAllocationData1D(int i, int i2, int i3, int i4, int i5, float[] fArr, int i6);

    native void rsnAllocationData1D(int i, int i2, int i3, int i4, int i5, int[] iArr, int i6);

    native void rsnAllocationData1D(int i, int i2, int i3, int i4, int i5, short[] sArr, int i6);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, byte[] bArr, int i9);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float[] fArr, int i9);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int[] iArr, int i9);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, short[] sArr, int i9);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, Bitmap bitmap);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, byte[] bArr, int i10);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, float[] fArr, int i10);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int[] iArr, int i10);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, short[] sArr, int i10);

    native void rsnAllocationElementData1D(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6);

    native void rsnAllocationGenerateMipmaps(int i, int i2);

    native Surface rsnAllocationGetSurface(int i, int i2);

    native int rsnAllocationGetType(int i, int i2);

    native void rsnAllocationIoReceive(int i, int i2);

    native void rsnAllocationIoSend(int i, int i2);

    native void rsnAllocationRead(int i, int i2, byte[] bArr);

    native void rsnAllocationRead(int i, int i2, float[] fArr);

    native void rsnAllocationRead(int i, int i2, int[] iArr);

    native void rsnAllocationRead(int i, int i2, short[] sArr);

    native void rsnAllocationResize1D(int i, int i2, int i3);

    native void rsnAllocationSetSurface(int i, int i2, Surface surface);

    native void rsnAllocationSyncAll(int i, int i2, int i3);

    native void rsnAssignName(int i, int i2, byte[] bArr);

    native void rsnContextBindProgramFragment(int i, int i2);

    native void rsnContextBindProgramRaster(int i, int i2);

    native void rsnContextBindProgramStore(int i, int i2);

    native void rsnContextBindProgramVertex(int i, int i2);

    native void rsnContextBindRootScript(int i, int i2);

    native void rsnContextBindSampler(int i, int i2, int i3);

    native int rsnContextCreate(int i, int i2, int i3, int i4);

    native int rsnContextCreateGL(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, float f, int i14);

    native void rsnContextDestroy(int i);

    native void rsnContextDump(int i, int i2);

    native void rsnContextFinish(int i);

    native void rsnContextPause(int i);

    native void rsnContextResume(int i);

    native void rsnContextSendMessage(int i, int i2, int[] iArr);

    native void rsnContextSetPriority(int i, int i2);

    native void rsnContextSetSurface(int i, int i2, int i3, Surface surface);

    native void rsnContextSetSurfaceTexture(int i, int i2, int i3, SurfaceTexture surfaceTexture);

    native int rsnElementCreate(int i, int i2, int i3, boolean z, int i4);

    native int rsnElementCreate2(int i, int[] iArr, String[] strArr, int[] iArr2);

    native void rsnElementGetNativeData(int i, int i2, int[] iArr);

    native void rsnElementGetSubElements(int i, int i2, int[] iArr, String[] strArr, int[] iArr2);

    native int rsnFileA3DCreateFromAsset(int i, AssetManager assetManager, String str);

    native int rsnFileA3DCreateFromAssetStream(int i, int i2);

    native int rsnFileA3DCreateFromFile(int i, String str);

    native int rsnFileA3DGetEntryByIndex(int i, int i2, int i3);

    native void rsnFileA3DGetIndexEntries(int i, int i2, int i3, int[] iArr, String[] strArr);

    native int rsnFileA3DGetNumIndexEntries(int i, int i2);

    native int rsnFontCreateFromAsset(int i, AssetManager assetManager, String str, float f, int i2);

    native int rsnFontCreateFromAssetStream(int i, String str, float f, int i2, int i3);

    native int rsnFontCreateFromFile(int i, String str, float f, int i2);

    native String rsnGetName(int i, int i2);

    native int rsnMeshCreate(int i, int[] iArr, int[] iArr2, int[] iArr3);

    native int rsnMeshGetIndexCount(int i, int i2);

    native void rsnMeshGetIndices(int i, int i2, int[] iArr, int[] iArr2, int i3);

    native int rsnMeshGetVertexBufferCount(int i, int i2);

    native void rsnMeshGetVertices(int i, int i2, int[] iArr, int i3);

    native void rsnObjDestroy(int i, int i2);

    native int rsnPathCreate(int i, int i2, boolean z, int i3, int i4, float f);

    native void rsnProgramBindConstants(int i, int i2, int i3, int i4);

    native void rsnProgramBindSampler(int i, int i2, int i3, int i4);

    native void rsnProgramBindTexture(int i, int i2, int i3, int i4);

    native int rsnProgramFragmentCreate(int i, String str, String[] strArr, int[] iArr);

    native int rsnProgramRasterCreate(int i, boolean z, int i2);

    native int rsnProgramStoreCreate(int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, int i2, int i3, int i4);

    native int rsnProgramVertexCreate(int i, String str, String[] strArr, int[] iArr);

    native int rsnSamplerCreate(int i, int i2, int i3, int i4, int i5, int i6, float f);

    native void rsnScriptBindAllocation(int i, int i2, int i3, int i4);

    native int rsnScriptCCreate(int i, String str, String str2, byte[] bArr, int i2);

    native int rsnScriptFieldIDCreate(int i, int i2, int i3);

    native void rsnScriptForEach(int i, int i2, int i3, int i4, int i5);

    native void rsnScriptForEach(int i, int i2, int i3, int i4, int i5, byte[] bArr);

    native void rsnScriptForEachClipped(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11);

    native void rsnScriptForEachClipped(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6, int i7, int i8, int i9, int i10, int i11);

    native double rsnScriptGetVarD(int i, int i2, int i3);

    native float rsnScriptGetVarF(int i, int i2, int i3);

    native int rsnScriptGetVarI(int i, int i2, int i3);

    native long rsnScriptGetVarJ(int i, int i2, int i3);

    native void rsnScriptGetVarV(int i, int i2, int i3, byte[] bArr);

    native int rsnScriptGroupCreate(int i, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5);

    native void rsnScriptGroupExecute(int i, int i2);

    native void rsnScriptGroupSetInput(int i, int i2, int i3, int i4);

    native void rsnScriptGroupSetOutput(int i, int i2, int i3, int i4);

    native int rsnScriptIntrinsicCreate(int i, int i2, int i3);

    native void rsnScriptInvoke(int i, int i2, int i3);

    native void rsnScriptInvokeV(int i, int i2, int i3, byte[] bArr);

    native int rsnScriptKernelIDCreate(int i, int i2, int i3, int i4);

    native void rsnScriptSetTimeZone(int i, int i2, byte[] bArr);

    native void rsnScriptSetVarD(int i, int i2, int i3, double d);

    native void rsnScriptSetVarF(int i, int i2, int i3, float f);

    native void rsnScriptSetVarI(int i, int i2, int i3, int i4);

    native void rsnScriptSetVarJ(int i, int i2, int i3, long j);

    native void rsnScriptSetVarObj(int i, int i2, int i3, int i4);

    native void rsnScriptSetVarV(int i, int i2, int i3, byte[] bArr);

    native void rsnScriptSetVarVE(int i, int i2, int i3, byte[] bArr, int i4, int[] iArr);

    native int rsnTypeCreate(int i, int i2, int i3, int i4, int i5, boolean z, boolean z2, int i6);

    native void rsnTypeGetNativeData(int i, int i2, int[] iArr);

    static {
        if (SystemProperties.getBoolean("config.disable_renderscript", false)) {
            return;
        }
        try {
            Class<?> cls = Class.forName("dalvik.system.VMRuntime");
            sRuntime = cls.getDeclaredMethod("getRuntime", new Class[0]).invoke(null, new Object[0]);
            registerNativeAllocation = cls.getDeclaredMethod("registerNativeAllocation", Integer.TYPE);
            registerNativeFree = cls.getDeclaredMethod("registerNativeFree", Integer.TYPE);
            try {
                System.loadLibrary("rs_jni");
                _nInit();
                sInitialized = true;
            } catch (UnsatisfiedLinkError e) {
                Log.e(LOG_TAG, "Error loading RS jni library: " + e);
                throw new RSRuntimeException("Error loading RS jni library: " + e);
            }
        } catch (Exception e2) {
            Log.e(LOG_TAG, "Error loading GC methods: " + e2);
            throw new RSRuntimeException("Error loading GC methods: " + e2);
        }
    }

    public static void setupDiskCache(File file) {
        if (!sInitialized) {
            Log.e(LOG_TAG, "RenderScript.setupDiskCache() called when disabled");
        } else {
            mCacheDir = file;
        }
    }

    public enum ContextType {
        NORMAL(0),
        DEBUG(1),
        PROFILE(2);

        int mID;

        ContextType(int i) {
            this.mID = i;
        }
    }

    synchronized int nContextCreateGL(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, float f, int i14) {
        return rsnContextCreateGL(i, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, f, i14);
    }

    synchronized int nContextCreate(int i, int i2, int i3, int i4) {
        return rsnContextCreate(i, i2, i3, i4);
    }

    synchronized void nContextDestroy() {
        validate();
        rsnContextDestroy(this.mContext);
    }

    synchronized void nContextSetSurface(int i, int i2, Surface surface) {
        validate();
        rsnContextSetSurface(this.mContext, i, i2, surface);
    }

    synchronized void nContextSetSurfaceTexture(int i, int i2, SurfaceTexture surfaceTexture) {
        validate();
        rsnContextSetSurfaceTexture(this.mContext, i, i2, surfaceTexture);
    }

    synchronized void nContextSetPriority(int i) {
        validate();
        rsnContextSetPriority(this.mContext, i);
    }

    synchronized void nContextDump(int i) {
        validate();
        rsnContextDump(this.mContext, i);
    }

    synchronized void nContextFinish() {
        validate();
        rsnContextFinish(this.mContext);
    }

    synchronized void nContextSendMessage(int i, int[] iArr) {
        validate();
        rsnContextSendMessage(this.mContext, i, iArr);
    }

    synchronized void nContextBindRootScript(int i) {
        validate();
        rsnContextBindRootScript(this.mContext, i);
    }

    synchronized void nContextBindSampler(int i, int i2) {
        validate();
        rsnContextBindSampler(this.mContext, i, i2);
    }

    synchronized void nContextBindProgramStore(int i) {
        validate();
        rsnContextBindProgramStore(this.mContext, i);
    }

    synchronized void nContextBindProgramFragment(int i) {
        validate();
        rsnContextBindProgramFragment(this.mContext, i);
    }

    synchronized void nContextBindProgramVertex(int i) {
        validate();
        rsnContextBindProgramVertex(this.mContext, i);
    }

    synchronized void nContextBindProgramRaster(int i) {
        validate();
        rsnContextBindProgramRaster(this.mContext, i);
    }

    synchronized void nContextPause() {
        validate();
        rsnContextPause(this.mContext);
    }

    synchronized void nContextResume() {
        validate();
        rsnContextResume(this.mContext);
    }

    synchronized void nAssignName(int i, byte[] bArr) {
        validate();
        rsnAssignName(this.mContext, i, bArr);
    }

    synchronized String nGetName(int i) {
        validate();
        return rsnGetName(this.mContext, i);
    }

    synchronized void nObjDestroy(int i) {
        int i2 = this.mContext;
        if (i2 != 0) {
            rsnObjDestroy(i2, i);
        }
    }

    synchronized int nElementCreate(int i, int i2, boolean z, int i3) {
        validate();
        return rsnElementCreate(this.mContext, i, i2, z, i3);
    }

    synchronized int nElementCreate2(int[] iArr, String[] strArr, int[] iArr2) {
        validate();
        return rsnElementCreate2(this.mContext, iArr, strArr, iArr2);
    }

    synchronized void nElementGetNativeData(int i, int[] iArr) {
        validate();
        rsnElementGetNativeData(this.mContext, i, iArr);
    }

    synchronized void nElementGetSubElements(int i, int[] iArr, String[] strArr, int[] iArr2) {
        validate();
        rsnElementGetSubElements(this.mContext, i, iArr, strArr, iArr2);
    }

    synchronized int nTypeCreate(int i, int i2, int i3, int i4, boolean z, boolean z2, int i5) {
        validate();
        return rsnTypeCreate(this.mContext, i, i2, i3, i4, z, z2, i5);
    }

    synchronized void nTypeGetNativeData(int i, int[] iArr) {
        validate();
        rsnTypeGetNativeData(this.mContext, i, iArr);
    }

    synchronized int nAllocationCreateTyped(int i, int i2, int i3, int i4) {
        validate();
        return rsnAllocationCreateTyped(this.mContext, i, i2, i3, i4);
    }

    synchronized int nAllocationCreateFromBitmap(int i, int i2, Bitmap bitmap, int i3) {
        validate();
        return rsnAllocationCreateFromBitmap(this.mContext, i, i2, bitmap, i3);
    }

    synchronized int nAllocationCreateBitmapBackedAllocation(int i, int i2, Bitmap bitmap, int i3) {
        validate();
        return rsnAllocationCreateBitmapBackedAllocation(this.mContext, i, i2, bitmap, i3);
    }

    synchronized int nAllocationCubeCreateFromBitmap(int i, int i2, Bitmap bitmap, int i3) {
        validate();
        return rsnAllocationCubeCreateFromBitmap(this.mContext, i, i2, bitmap, i3);
    }

    synchronized int nAllocationCreateBitmapRef(int i, Bitmap bitmap) {
        validate();
        return rsnAllocationCreateBitmapRef(this.mContext, i, bitmap);
    }

    synchronized int nAllocationCreateFromAssetStream(int i, int i2, int i3) {
        validate();
        return rsnAllocationCreateFromAssetStream(this.mContext, i, i2, i3);
    }

    synchronized void nAllocationCopyToBitmap(int i, Bitmap bitmap) {
        validate();
        rsnAllocationCopyToBitmap(this.mContext, i, bitmap);
    }

    synchronized void nAllocationSyncAll(int i, int i2) {
        validate();
        rsnAllocationSyncAll(this.mContext, i, i2);
    }

    synchronized Surface nAllocationGetSurface(int i) {
        validate();
        return rsnAllocationGetSurface(this.mContext, i);
    }

    synchronized void nAllocationSetSurface(int i, Surface surface) {
        validate();
        rsnAllocationSetSurface(this.mContext, i, surface);
    }

    synchronized void nAllocationIoSend(int i) {
        validate();
        rsnAllocationIoSend(this.mContext, i);
    }

    synchronized void nAllocationIoReceive(int i) {
        validate();
        rsnAllocationIoReceive(this.mContext, i);
    }

    synchronized void nAllocationGenerateMipmaps(int i) {
        validate();
        rsnAllocationGenerateMipmaps(this.mContext, i);
    }

    synchronized void nAllocationCopyFromBitmap(int i, Bitmap bitmap) {
        validate();
        rsnAllocationCopyFromBitmap(this.mContext, i, bitmap);
    }

    synchronized void nAllocationData1D(int i, int i2, int i3, int i4, int[] iArr, int i5) {
        validate();
        rsnAllocationData1D(this.mContext, i, i2, i3, i4, iArr, i5);
    }

    synchronized void nAllocationData1D(int i, int i2, int i3, int i4, short[] sArr, int i5) {
        validate();
        rsnAllocationData1D(this.mContext, i, i2, i3, i4, sArr, i5);
    }

    synchronized void nAllocationData1D(int i, int i2, int i3, int i4, byte[] bArr, int i5) {
        validate();
        rsnAllocationData1D(this.mContext, i, i2, i3, i4, bArr, i5);
    }

    synchronized void nAllocationData1D(int i, int i2, int i3, int i4, float[] fArr, int i5) {
        validate();
        rsnAllocationData1D(this.mContext, i, i2, i3, i4, fArr, i5);
    }

    synchronized void nAllocationElementData1D(int i, int i2, int i3, int i4, byte[] bArr, int i5) {
        validate();
        rsnAllocationElementData1D(this.mContext, i, i2, i3, i4, bArr, i5);
    }

    synchronized void nAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12) {
        validate();
        rsnAllocationData2D(this.mContext, i, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12);
    }

    synchronized void nAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, byte[] bArr, int i8) {
        validate();
        rsnAllocationData2D(this.mContext, i, i2, i3, i4, i5, i6, i7, bArr, i8);
    }

    synchronized void nAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, short[] sArr, int i8) {
        validate();
        rsnAllocationData2D(this.mContext, i, i2, i3, i4, i5, i6, i7, sArr, i8);
    }

    synchronized void nAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int[] iArr, int i8) {
        validate();
        rsnAllocationData2D(this.mContext, i, i2, i3, i4, i5, i6, i7, iArr, i8);
    }

    synchronized void nAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, float[] fArr, int i8) {
        validate();
        rsnAllocationData2D(this.mContext, i, i2, i3, i4, i5, i6, i7, fArr, i8);
    }

    synchronized void nAllocationData2D(int i, int i2, int i3, int i4, int i5, Bitmap bitmap) {
        validate();
        rsnAllocationData2D(this.mContext, i, i2, i3, i4, i5, bitmap);
    }

    synchronized void nAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13) {
        validate();
        rsnAllocationData3D(this.mContext, i, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13);
    }

    synchronized void nAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, byte[] bArr, int i9) {
        validate();
        rsnAllocationData3D(this.mContext, i, i2, i3, i4, i5, i6, i7, i8, bArr, i9);
    }

    synchronized void nAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, short[] sArr, int i9) {
        validate();
        rsnAllocationData3D(this.mContext, i, i2, i3, i4, i5, i6, i7, i8, sArr, i9);
    }

    synchronized void nAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int[] iArr, int i9) {
        validate();
        rsnAllocationData3D(this.mContext, i, i2, i3, i4, i5, i6, i7, i8, iArr, i9);
    }

    synchronized void nAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float[] fArr, int i9) {
        validate();
        rsnAllocationData3D(this.mContext, i, i2, i3, i4, i5, i6, i7, i8, fArr, i9);
    }

    synchronized void nAllocationRead(int i, byte[] bArr) {
        validate();
        rsnAllocationRead(this.mContext, i, bArr);
    }

    synchronized void nAllocationRead(int i, short[] sArr) {
        validate();
        rsnAllocationRead(this.mContext, i, sArr);
    }

    synchronized void nAllocationRead(int i, int[] iArr) {
        validate();
        rsnAllocationRead(this.mContext, i, iArr);
    }

    synchronized void nAllocationRead(int i, float[] fArr) {
        validate();
        rsnAllocationRead(this.mContext, i, fArr);
    }

    synchronized int nAllocationGetType(int i) {
        validate();
        return rsnAllocationGetType(this.mContext, i);
    }

    synchronized void nAllocationResize1D(int i, int i2) {
        validate();
        rsnAllocationResize1D(this.mContext, i, i2);
    }

    synchronized int nFileA3DCreateFromAssetStream(int i) {
        validate();
        return rsnFileA3DCreateFromAssetStream(this.mContext, i);
    }

    synchronized int nFileA3DCreateFromFile(String str) {
        validate();
        return rsnFileA3DCreateFromFile(this.mContext, str);
    }

    synchronized int nFileA3DCreateFromAsset(AssetManager assetManager, String str) {
        validate();
        return rsnFileA3DCreateFromAsset(this.mContext, assetManager, str);
    }

    synchronized int nFileA3DGetNumIndexEntries(int i) {
        validate();
        return rsnFileA3DGetNumIndexEntries(this.mContext, i);
    }

    synchronized void nFileA3DGetIndexEntries(int i, int i2, int[] iArr, String[] strArr) {
        validate();
        rsnFileA3DGetIndexEntries(this.mContext, i, i2, iArr, strArr);
    }

    synchronized int nFileA3DGetEntryByIndex(int i, int i2) {
        validate();
        return rsnFileA3DGetEntryByIndex(this.mContext, i, i2);
    }

    synchronized int nFontCreateFromFile(String str, float f, int i) {
        validate();
        return rsnFontCreateFromFile(this.mContext, str, f, i);
    }

    synchronized int nFontCreateFromAssetStream(String str, float f, int i, int i2) {
        validate();
        return rsnFontCreateFromAssetStream(this.mContext, str, f, i, i2);
    }

    synchronized int nFontCreateFromAsset(AssetManager assetManager, String str, float f, int i) {
        validate();
        return rsnFontCreateFromAsset(this.mContext, assetManager, str, f, i);
    }

    synchronized void nScriptBindAllocation(int i, int i2, int i3) {
        validate();
        rsnScriptBindAllocation(this.mContext, i, i2, i3);
    }

    synchronized void nScriptSetTimeZone(int i, byte[] bArr) {
        validate();
        rsnScriptSetTimeZone(this.mContext, i, bArr);
    }

    synchronized void nScriptInvoke(int i, int i2) {
        validate();
        rsnScriptInvoke(this.mContext, i, i2);
    }

    synchronized void nScriptForEach(int i, int i2, int i3, int i4, byte[] bArr) {
        validate();
        if (bArr == null) {
            rsnScriptForEach(this.mContext, i, i2, i3, i4);
        } else {
            rsnScriptForEach(this.mContext, i, i2, i3, i4, bArr);
        }
    }

    synchronized void nScriptForEachClipped(int i, int i2, int i3, int i4, byte[] bArr, int i5, int i6, int i7, int i8, int i9, int i10) {
        validate();
        if (bArr == null) {
            rsnScriptForEachClipped(this.mContext, i, i2, i3, i4, i5, i6, i7, i8, i9, i10);
        } else {
            rsnScriptForEachClipped(this.mContext, i, i2, i3, i4, bArr, i5, i6, i7, i8, i9, i10);
        }
    }

    synchronized void nScriptInvokeV(int i, int i2, byte[] bArr) {
        validate();
        rsnScriptInvokeV(this.mContext, i, i2, bArr);
    }

    synchronized void nScriptSetVarI(int i, int i2, int i3) {
        validate();
        rsnScriptSetVarI(this.mContext, i, i2, i3);
    }

    synchronized int nScriptGetVarI(int i, int i2) {
        validate();
        return rsnScriptGetVarI(this.mContext, i, i2);
    }

    synchronized void nScriptSetVarJ(int i, int i2, long j) {
        validate();
        rsnScriptSetVarJ(this.mContext, i, i2, j);
    }

    synchronized long nScriptGetVarJ(int i, int i2) {
        validate();
        return rsnScriptGetVarJ(this.mContext, i, i2);
    }

    synchronized void nScriptSetVarF(int i, int i2, float f) {
        validate();
        rsnScriptSetVarF(this.mContext, i, i2, f);
    }

    synchronized float nScriptGetVarF(int i, int i2) {
        validate();
        return rsnScriptGetVarF(this.mContext, i, i2);
    }

    synchronized void nScriptSetVarD(int i, int i2, double d) {
        validate();
        rsnScriptSetVarD(this.mContext, i, i2, d);
    }

    synchronized double nScriptGetVarD(int i, int i2) {
        validate();
        return rsnScriptGetVarD(this.mContext, i, i2);
    }

    synchronized void nScriptSetVarV(int i, int i2, byte[] bArr) {
        validate();
        rsnScriptSetVarV(this.mContext, i, i2, bArr);
    }

    synchronized void nScriptGetVarV(int i, int i2, byte[] bArr) {
        validate();
        rsnScriptGetVarV(this.mContext, i, i2, bArr);
    }

    synchronized void nScriptSetVarVE(int i, int i2, byte[] bArr, int i3, int[] iArr) {
        validate();
        rsnScriptSetVarVE(this.mContext, i, i2, bArr, i3, iArr);
    }

    synchronized void nScriptSetVarObj(int i, int i2, int i3) {
        validate();
        rsnScriptSetVarObj(this.mContext, i, i2, i3);
    }

    synchronized int nScriptCCreate(String str, String str2, byte[] bArr, int i) {
        validate();
        return rsnScriptCCreate(this.mContext, str, str2, bArr, i);
    }

    synchronized int nScriptIntrinsicCreate(int i, int i2) {
        validate();
        return rsnScriptIntrinsicCreate(this.mContext, i, i2);
    }

    synchronized int nScriptKernelIDCreate(int i, int i2, int i3) {
        validate();
        return rsnScriptKernelIDCreate(this.mContext, i, i2, i3);
    }

    synchronized int nScriptFieldIDCreate(int i, int i2) {
        validate();
        return rsnScriptFieldIDCreate(this.mContext, i, i2);
    }

    synchronized int nScriptGroupCreate(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5) {
        validate();
        return rsnScriptGroupCreate(this.mContext, iArr, iArr2, iArr3, iArr4, iArr5);
    }

    synchronized void nScriptGroupSetInput(int i, int i2, int i3) {
        validate();
        rsnScriptGroupSetInput(this.mContext, i, i2, i3);
    }

    synchronized void nScriptGroupSetOutput(int i, int i2, int i3) {
        validate();
        rsnScriptGroupSetOutput(this.mContext, i, i2, i3);
    }

    synchronized void nScriptGroupExecute(int i) {
        validate();
        rsnScriptGroupExecute(this.mContext, i);
    }

    synchronized int nSamplerCreate(int i, int i2, int i3, int i4, int i5, float f) {
        validate();
        return rsnSamplerCreate(this.mContext, i, i2, i3, i4, i5, f);
    }

    synchronized int nProgramStoreCreate(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, int i, int i2, int i3) {
        validate();
        return rsnProgramStoreCreate(this.mContext, z, z2, z3, z4, z5, z6, i, i2, i3);
    }

    synchronized int nProgramRasterCreate(boolean z, int i) {
        validate();
        return rsnProgramRasterCreate(this.mContext, z, i);
    }

    synchronized void nProgramBindConstants(int i, int i2, int i3) {
        validate();
        rsnProgramBindConstants(this.mContext, i, i2, i3);
    }

    synchronized void nProgramBindTexture(int i, int i2, int i3) {
        validate();
        rsnProgramBindTexture(this.mContext, i, i2, i3);
    }

    synchronized void nProgramBindSampler(int i, int i2, int i3) {
        validate();
        rsnProgramBindSampler(this.mContext, i, i2, i3);
    }

    synchronized int nProgramFragmentCreate(String str, String[] strArr, int[] iArr) {
        validate();
        return rsnProgramFragmentCreate(this.mContext, str, strArr, iArr);
    }

    synchronized int nProgramVertexCreate(String str, String[] strArr, int[] iArr) {
        validate();
        return rsnProgramVertexCreate(this.mContext, str, strArr, iArr);
    }

    synchronized int nMeshCreate(int[] iArr, int[] iArr2, int[] iArr3) {
        validate();
        return rsnMeshCreate(this.mContext, iArr, iArr2, iArr3);
    }

    synchronized int nMeshGetVertexBufferCount(int i) {
        validate();
        return rsnMeshGetVertexBufferCount(this.mContext, i);
    }

    synchronized int nMeshGetIndexCount(int i) {
        validate();
        return rsnMeshGetIndexCount(this.mContext, i);
    }

    synchronized void nMeshGetVertices(int i, int[] iArr, int i2) {
        validate();
        rsnMeshGetVertices(this.mContext, i, iArr, i2);
    }

    synchronized void nMeshGetIndices(int i, int[] iArr, int[] iArr2, int i2) {
        validate();
        rsnMeshGetIndices(this.mContext, i, iArr, iArr2, i2);
    }

    synchronized int nPathCreate(int i, boolean z, int i2, int i3, float f) {
        validate();
        return rsnPathCreate(this.mContext, i, z, i2, i3, f);
    }

    public void setMessageHandler(RSMessageHandler rSMessageHandler) {
        this.mMessageCallback = rSMessageHandler;
    }

    public RSMessageHandler getMessageHandler() {
        return this.mMessageCallback;
    }

    public void sendMessage(int i, int[] iArr) {
        nContextSendMessage(i, iArr);
    }

    public void setErrorHandler(RSErrorHandler rSErrorHandler) {
        this.mErrorCallback = rSErrorHandler;
    }

    public RSErrorHandler getErrorHandler() {
        return this.mErrorCallback;
    }

    public enum Priority {
        LOW(15),
        NORMAL(-4);

        int mID;

        Priority(int i) {
            this.mID = i;
        }
    }

    void validate() {
        if (this.mContext == 0) {
            throw new RSInvalidStateException("Calling RS with no Context active.");
        }
    }

    public void setPriority(Priority priority) {
        validate();
        nContextSetPriority(priority.mID);
    }

    static class MessageThread extends Thread {
        static final int RS_ERROR_FATAL_DEBUG = 2048;
        static final int RS_ERROR_FATAL_UNKNOWN = 4096;
        static final int RS_MESSAGE_TO_CLIENT_ERROR = 3;
        static final int RS_MESSAGE_TO_CLIENT_EXCEPTION = 1;
        static final int RS_MESSAGE_TO_CLIENT_NEW_BUFFER = 5;
        static final int RS_MESSAGE_TO_CLIENT_NONE = 0;
        static final int RS_MESSAGE_TO_CLIENT_RESIZE = 2;
        static final int RS_MESSAGE_TO_CLIENT_USER = 4;
        int[] mAuxData;
        RenderScript mRS;
        boolean mRun;

        MessageThread(RenderScript renderScript) {
            super("RSMessageThread");
            this.mRun = true;
            this.mAuxData = new int[2];
            this.mRS = renderScript;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int[] iArr = new int[16];
            RenderScript renderScript = this.mRS;
            renderScript.nContextInitToClient(renderScript.mContext);
            while (this.mRun) {
                iArr[0] = 0;
                RenderScript renderScript2 = this.mRS;
                int iNContextPeekMessage = renderScript2.nContextPeekMessage(renderScript2.mContext, this.mAuxData);
                int[] iArr2 = this.mAuxData;
                int i = iArr2[1];
                int i2 = iArr2[0];
                if (iNContextPeekMessage == 4) {
                    if ((i >> 2) >= iArr.length) {
                        iArr = new int[(i + 3) >> 2];
                    }
                    RenderScript renderScript3 = this.mRS;
                    if (renderScript3.nContextGetUserMessage(renderScript3.mContext, iArr) != 4) {
                        throw new RSDriverException("Error processing message from RenderScript.");
                    }
                    if (this.mRS.mMessageCallback != null) {
                        this.mRS.mMessageCallback.mData = iArr;
                        this.mRS.mMessageCallback.mID = i2;
                        this.mRS.mMessageCallback.mLength = i;
                        this.mRS.mMessageCallback.run();
                    } else {
                        throw new RSInvalidStateException("Received a message from the script with no message handler installed.");
                    }
                } else if (iNContextPeekMessage == 3) {
                    RenderScript renderScript4 = this.mRS;
                    String strNContextGetErrorMessage = renderScript4.nContextGetErrorMessage(renderScript4.mContext);
                    if (i2 >= 4096 || (i2 >= 2048 && (this.mRS.mContextType != ContextType.DEBUG || this.mRS.mErrorCallback == null))) {
                        throw new RSRuntimeException("Fatal error " + i2 + ", details: " + strNContextGetErrorMessage);
                    }
                    if (this.mRS.mErrorCallback != null) {
                        this.mRS.mErrorCallback.mErrorMessage = strNContextGetErrorMessage;
                        this.mRS.mErrorCallback.mErrorNum = i2;
                        this.mRS.mErrorCallback.run();
                    } else {
                        Log.e(RenderScript.LOG_TAG, "non fatal RS error, " + strNContextGetErrorMessage);
                    }
                } else if (iNContextPeekMessage == 5) {
                    Allocation.sendBufferNotification(i2);
                } else {
                    try {
                        sleep(1L, 0);
                    } catch (InterruptedException unused) {
                    }
                }
            }
        }
    }

    RenderScript(Context context) {
        if (context != null) {
            this.mApplicationContext = context.getApplicationContext();
        }
    }

    public final Context getApplicationContext() {
        return this.mApplicationContext;
    }

    public static RenderScript create(Context context, int i) {
        return create(context, i, ContextType.NORMAL);
    }

    public static RenderScript create(Context context, int i, ContextType contextType) {
        if (!sInitialized) {
            Log.e(LOG_TAG, "RenderScript.create() called when disabled; someone is likely to crash");
            return null;
        }
        RenderScript renderScript = new RenderScript(context);
        int iNDeviceCreate = renderScript.nDeviceCreate();
        renderScript.mDev = iNDeviceCreate;
        int iNContextCreate = renderScript.nContextCreate(iNDeviceCreate, 0, i, contextType.mID);
        renderScript.mContext = iNContextCreate;
        renderScript.mContextType = contextType;
        if (iNContextCreate == 0) {
            throw new RSDriverException("Failed to create RS context.");
        }
        MessageThread messageThread = new MessageThread(renderScript);
        renderScript.mMessageThread = messageThread;
        messageThread.start();
        return renderScript;
    }

    public static RenderScript create(Context context) {
        return create(context, ContextType.NORMAL);
    }

    public static RenderScript create(Context context, ContextType contextType) {
        return create(context, context.getApplicationInfo().targetSdkVersion, contextType);
    }

    public void contextDump() {
        validate();
        nContextDump(0);
    }

    public void finish() {
        nContextFinish();
    }

    public void destroy() {
        validate();
        nContextDeinitToClient(this.mContext);
        this.mMessageThread.mRun = false;
        try {
            this.mMessageThread.join();
        } catch (InterruptedException unused) {
        }
        nContextDestroy();
        this.mContext = 0;
        nDeviceDestroy(this.mDev);
        this.mDev = 0;
    }

    boolean isAlive() {
        return this.mContext != 0;
    }

    int safeID(BaseObj baseObj) {
        if (baseObj != null) {
            return baseObj.getID(this);
        }
        return 0;
    }
}
