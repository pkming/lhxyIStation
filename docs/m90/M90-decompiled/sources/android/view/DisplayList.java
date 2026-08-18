package android.view;

import android.graphics.Matrix;

/* JADX INFO: loaded from: classes.dex */
public abstract class DisplayList {
    public static final int FLAG_CLIP_CHILDREN = 1;
    public static final int STATUS_DONE = 0;
    public static final int STATUS_DRAW = 1;
    public static final int STATUS_DREW = 4;
    public static final int STATUS_INVOKE = 2;
    private boolean mDirty;

    public abstract void clear();

    public abstract void end();

    public abstract float getAlpha();

    public abstract float getBottom();

    public abstract float getCameraDistance();

    public abstract float getLeft();

    public abstract Matrix getMatrix(Matrix matrix);

    public abstract float getPivotX();

    public abstract float getPivotY();

    public abstract float getRight();

    public abstract float getRotation();

    public abstract float getRotationX();

    public abstract float getRotationY();

    public abstract float getScaleX();

    public abstract float getScaleY();

    public abstract int getSize();

    public abstract float getTop();

    public abstract float getTranslationX();

    public abstract float getTranslationY();

    public abstract boolean hasOverlappingRendering();

    public abstract boolean isValid();

    public abstract void offsetLeftAndRight(float f);

    public abstract void offsetTopAndBottom(float f);

    public abstract void reset();

    public abstract void setAlpha(float f);

    public abstract void setAnimationMatrix(Matrix matrix);

    public abstract void setBottom(int i);

    public abstract void setCaching(boolean z);

    public abstract void setCameraDistance(float f);

    public abstract void setClipToBounds(boolean z);

    public abstract void setHasOverlappingRendering(boolean z);

    public abstract void setLeft(int i);

    public abstract void setLeftTopRightBottom(int i, int i2, int i3, int i4);

    public abstract void setMatrix(Matrix matrix);

    public abstract void setPivotX(float f);

    public abstract void setPivotY(float f);

    public abstract void setRight(int i);

    public abstract void setRotation(float f);

    public abstract void setRotationX(float f);

    public abstract void setRotationY(float f);

    public abstract void setScaleX(float f);

    public abstract void setScaleY(float f);

    public abstract void setTop(int i);

    public abstract void setTransformationInfo(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8);

    public abstract void setTranslationX(float f);

    public abstract void setTranslationY(float f);

    public abstract HardwareCanvas start(int i, int i2);

    public void markDirty() {
        this.mDirty = true;
    }

    protected void clearDirty() {
        this.mDirty = false;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public Matrix getMatrix() {
        return getMatrix(new Matrix());
    }
}
