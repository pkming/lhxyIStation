package android.view;

import android.graphics.Matrix;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class GLES20DisplayList extends DisplayList {
    private GLES20RecordingCanvas mCanvas;
    private ArrayList<DisplayList> mChildDisplayLists;
    private DisplayListFinalizer mFinalizer;
    private final String mName;
    private boolean mValid;

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nDestroyDisplayList(int i);

    private static native float nGetAlpha(int i);

    private static native float nGetBottom(int i);

    private static native float nGetCameraDistance(int i);

    private static native int nGetDisplayListSize(int i);

    private static native float nGetLeft(int i);

    private static native void nGetMatrix(int i, int i2);

    private static native float nGetPivotX(int i);

    private static native float nGetPivotY(int i);

    private static native float nGetRight(int i);

    private static native float nGetRotation(int i);

    private static native float nGetRotationX(int i);

    private static native float nGetRotationY(int i);

    private static native float nGetScaleX(int i);

    private static native float nGetScaleY(int i);

    private static native float nGetTop(int i);

    private static native float nGetTranslationX(int i);

    private static native float nGetTranslationY(int i);

    private static native boolean nHasOverlappingRendering(int i);

    private static native void nOffsetLeftAndRight(int i, float f);

    private static native void nOffsetTopAndBottom(int i, float f);

    private static native void nReset(int i);

    private static native void nSetAlpha(int i, float f);

    private static native void nSetAnimationMatrix(int i, int i2);

    private static native void nSetBottom(int i, int i2);

    private static native void nSetCaching(int i, boolean z);

    private static native void nSetCameraDistance(int i, float f);

    private static native void nSetClipToBounds(int i, boolean z);

    private static native void nSetDisplayListName(int i, String str);

    private static native void nSetHasOverlappingRendering(int i, boolean z);

    private static native void nSetLeft(int i, int i2);

    private static native void nSetLeftTopRightBottom(int i, int i2, int i3, int i4, int i5);

    private static native void nSetPivotX(int i, float f);

    private static native void nSetPivotY(int i, float f);

    private static native void nSetRight(int i, int i2);

    private static native void nSetRotation(int i, float f);

    private static native void nSetRotationX(int i, float f);

    private static native void nSetRotationY(int i, float f);

    private static native void nSetScaleX(int i, float f);

    private static native void nSetScaleY(int i, float f);

    private static native void nSetStaticMatrix(int i, int i2);

    private static native void nSetTop(int i, int i2);

    private static native void nSetTransformationInfo(int i, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8);

    private static native void nSetTranslationX(int i, float f);

    private static native void nSetTranslationY(int i, float f);

    GLES20DisplayList(String str) {
        this.mName = str;
    }

    boolean hasNativeDisplayList() {
        return this.mValid && this.mFinalizer != null;
    }

    int getNativeDisplayList() {
        DisplayListFinalizer displayListFinalizer;
        if (!this.mValid || (displayListFinalizer = this.mFinalizer) == null) {
            throw new IllegalStateException("The display list is not valid.");
        }
        return displayListFinalizer.mNativeDisplayList;
    }

    @Override // android.view.DisplayList
    public HardwareCanvas start(int i, int i2) {
        if (this.mCanvas != null) {
            throw new IllegalStateException("Recording has already started");
        }
        this.mValid = false;
        GLES20RecordingCanvas gLES20RecordingCanvasObtain = GLES20RecordingCanvas.obtain(this);
        this.mCanvas = gLES20RecordingCanvasObtain;
        gLES20RecordingCanvasObtain.start();
        this.mCanvas.setViewport(i, i2);
        this.mCanvas.onPreDraw(null);
        return this.mCanvas;
    }

    @Override // android.view.DisplayList
    public void clear() {
        clearDirty();
        GLES20RecordingCanvas gLES20RecordingCanvas = this.mCanvas;
        if (gLES20RecordingCanvas != null) {
            gLES20RecordingCanvas.recycle();
            this.mCanvas = null;
        }
        this.mValid = false;
        clearReferences();
    }

    void clearReferences() {
        ArrayList<DisplayList> arrayList = this.mChildDisplayLists;
        if (arrayList != null) {
            arrayList.clear();
        }
    }

    ArrayList<DisplayList> getChildDisplayLists() {
        if (this.mChildDisplayLists == null) {
            this.mChildDisplayLists = new ArrayList<>();
        }
        return this.mChildDisplayLists;
    }

    @Override // android.view.DisplayList
    public void reset() {
        if (hasNativeDisplayList()) {
            nReset(this.mFinalizer.mNativeDisplayList);
        }
        clear();
    }

    @Override // android.view.DisplayList
    public boolean isValid() {
        return this.mValid;
    }

    @Override // android.view.DisplayList
    public void end() {
        GLES20RecordingCanvas gLES20RecordingCanvas = this.mCanvas;
        if (gLES20RecordingCanvas != null) {
            gLES20RecordingCanvas.onPostDraw();
            DisplayListFinalizer displayListFinalizer = this.mFinalizer;
            if (displayListFinalizer != null) {
                this.mCanvas.end(displayListFinalizer.mNativeDisplayList);
            } else {
                DisplayListFinalizer displayListFinalizer2 = new DisplayListFinalizer(this.mCanvas.end(0));
                this.mFinalizer = displayListFinalizer2;
                nSetDisplayListName(displayListFinalizer2.mNativeDisplayList, this.mName);
            }
            this.mCanvas.recycle();
            this.mCanvas = null;
            this.mValid = true;
        }
    }

    @Override // android.view.DisplayList
    public int getSize() {
        DisplayListFinalizer displayListFinalizer = this.mFinalizer;
        if (displayListFinalizer == null) {
            return 0;
        }
        return nGetDisplayListSize(displayListFinalizer.mNativeDisplayList);
    }

    @Override // android.view.DisplayList
    public void setCaching(boolean z) {
        if (hasNativeDisplayList()) {
            nSetCaching(this.mFinalizer.mNativeDisplayList, z);
        }
    }

    @Override // android.view.DisplayList
    public void setClipToBounds(boolean z) {
        if (hasNativeDisplayList()) {
            nSetClipToBounds(this.mFinalizer.mNativeDisplayList, z);
        }
    }

    @Override // android.view.DisplayList
    public void setMatrix(Matrix matrix) {
        if (hasNativeDisplayList()) {
            nSetStaticMatrix(this.mFinalizer.mNativeDisplayList, matrix.native_instance);
        }
    }

    @Override // android.view.DisplayList
    public Matrix getMatrix(Matrix matrix) {
        if (hasNativeDisplayList()) {
            nGetMatrix(this.mFinalizer.mNativeDisplayList, matrix.native_instance);
        }
        return matrix;
    }

    @Override // android.view.DisplayList
    public void setAnimationMatrix(Matrix matrix) {
        if (hasNativeDisplayList()) {
            nSetAnimationMatrix(this.mFinalizer.mNativeDisplayList, matrix != null ? matrix.native_instance : 0);
        }
    }

    @Override // android.view.DisplayList
    public void setAlpha(float f) {
        if (hasNativeDisplayList()) {
            nSetAlpha(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getAlpha() {
        if (hasNativeDisplayList()) {
            return nGetAlpha(this.mFinalizer.mNativeDisplayList);
        }
        return 1.0f;
    }

    @Override // android.view.DisplayList
    public void setHasOverlappingRendering(boolean z) {
        if (hasNativeDisplayList()) {
            nSetHasOverlappingRendering(this.mFinalizer.mNativeDisplayList, z);
        }
    }

    @Override // android.view.DisplayList
    public boolean hasOverlappingRendering() {
        if (hasNativeDisplayList()) {
            return nHasOverlappingRendering(this.mFinalizer.mNativeDisplayList);
        }
        return true;
    }

    @Override // android.view.DisplayList
    public void setTranslationX(float f) {
        if (hasNativeDisplayList()) {
            nSetTranslationX(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getTranslationX() {
        if (hasNativeDisplayList()) {
            return nGetTranslationX(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setTranslationY(float f) {
        if (hasNativeDisplayList()) {
            nSetTranslationY(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getTranslationY() {
        if (hasNativeDisplayList()) {
            return nGetTranslationY(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setRotation(float f) {
        if (hasNativeDisplayList()) {
            nSetRotation(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getRotation() {
        if (hasNativeDisplayList()) {
            return nGetRotation(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setRotationX(float f) {
        if (hasNativeDisplayList()) {
            nSetRotationX(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getRotationX() {
        if (hasNativeDisplayList()) {
            return nGetRotationX(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setRotationY(float f) {
        if (hasNativeDisplayList()) {
            nSetRotationY(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getRotationY() {
        if (hasNativeDisplayList()) {
            return nGetRotationY(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setScaleX(float f) {
        if (hasNativeDisplayList()) {
            nSetScaleX(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getScaleX() {
        if (hasNativeDisplayList()) {
            return nGetScaleX(this.mFinalizer.mNativeDisplayList);
        }
        return 1.0f;
    }

    @Override // android.view.DisplayList
    public void setScaleY(float f) {
        if (hasNativeDisplayList()) {
            nSetScaleY(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getScaleY() {
        if (hasNativeDisplayList()) {
            return nGetScaleY(this.mFinalizer.mNativeDisplayList);
        }
        return 1.0f;
    }

    @Override // android.view.DisplayList
    public void setTransformationInfo(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        if (hasNativeDisplayList()) {
            nSetTransformationInfo(this.mFinalizer.mNativeDisplayList, f, f2, f3, f4, f5, f6, f7, f8);
        }
    }

    @Override // android.view.DisplayList
    public void setPivotX(float f) {
        if (hasNativeDisplayList()) {
            nSetPivotX(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getPivotX() {
        if (hasNativeDisplayList()) {
            return nGetPivotX(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setPivotY(float f) {
        if (hasNativeDisplayList()) {
            nSetPivotY(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getPivotY() {
        if (hasNativeDisplayList()) {
            return nGetPivotY(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setCameraDistance(float f) {
        if (hasNativeDisplayList()) {
            nSetCameraDistance(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public float getCameraDistance() {
        if (hasNativeDisplayList()) {
            return nGetCameraDistance(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setLeft(int i) {
        if (hasNativeDisplayList()) {
            nSetLeft(this.mFinalizer.mNativeDisplayList, i);
        }
    }

    @Override // android.view.DisplayList
    public float getLeft() {
        if (hasNativeDisplayList()) {
            return nGetLeft(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setTop(int i) {
        if (hasNativeDisplayList()) {
            nSetTop(this.mFinalizer.mNativeDisplayList, i);
        }
    }

    @Override // android.view.DisplayList
    public float getTop() {
        if (hasNativeDisplayList()) {
            return nGetTop(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setRight(int i) {
        if (hasNativeDisplayList()) {
            nSetRight(this.mFinalizer.mNativeDisplayList, i);
        }
    }

    @Override // android.view.DisplayList
    public float getRight() {
        if (hasNativeDisplayList()) {
            return nGetRight(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setBottom(int i) {
        if (hasNativeDisplayList()) {
            nSetBottom(this.mFinalizer.mNativeDisplayList, i);
        }
    }

    @Override // android.view.DisplayList
    public float getBottom() {
        if (hasNativeDisplayList()) {
            return nGetBottom(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setLeftTopRightBottom(int i, int i2, int i3, int i4) {
        if (hasNativeDisplayList()) {
            nSetLeftTopRightBottom(this.mFinalizer.mNativeDisplayList, i, i2, i3, i4);
        }
    }

    @Override // android.view.DisplayList
    public void offsetLeftAndRight(float f) {
        if (hasNativeDisplayList()) {
            nOffsetLeftAndRight(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    @Override // android.view.DisplayList
    public void offsetTopAndBottom(float f) {
        if (hasNativeDisplayList()) {
            nOffsetTopAndBottom(this.mFinalizer.mNativeDisplayList, f);
        }
    }

    private static class DisplayListFinalizer {
        final int mNativeDisplayList;

        public DisplayListFinalizer(int i) {
            this.mNativeDisplayList = i;
        }

        protected void finalize() throws Throwable {
            try {
                GLES20DisplayList.nDestroyDisplayList(this.mNativeDisplayList);
            } finally {
                super.finalize();
            }
        }
    }
}
