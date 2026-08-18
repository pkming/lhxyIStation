package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.SparseArray;

/* JADX INFO: loaded from: classes.dex */
public class DrawableContainer extends Drawable implements Drawable.Callback {
    private static final boolean DEBUG = false;
    private static final boolean DEFAULT_DITHER = true;
    private static final String TAG = "DrawableContainer";
    private Runnable mAnimationRunnable;
    private ColorFilter mColorFilter;
    private Drawable mCurrDrawable;
    private DrawableContainerState mDrawableContainerState;
    private long mEnterAnimationEnd;
    private long mExitAnimationEnd;
    private Drawable mLastDrawable;
    private boolean mMutated;
    private int mAlpha = 255;
    private int mCurIndex = -1;
    private Insets mInsets = Insets.NONE;

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        Drawable drawable2 = this.mLastDrawable;
        if (drawable2 != null) {
            drawable2.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mDrawableContainerState.mChangingConfigurations | this.mDrawableContainerState.mChildrenChangingConfigurations;
    }

    private boolean needsMirroring() {
        return isAutoMirrored() && getLayoutDirection() == 1;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        boolean padding;
        Rect constantPadding = this.mDrawableContainerState.getConstantPadding();
        if (constantPadding != null) {
            rect.set(constantPadding);
            padding = (constantPadding.right | ((constantPadding.left | constantPadding.top) | constantPadding.bottom)) != 0;
        } else {
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                padding = drawable.getPadding(rect);
            } else {
                padding = super.getPadding(rect);
            }
        }
        if (needsMirroring()) {
            int i = rect.left;
            rect.left = rect.right;
            rect.right = i;
        }
        return padding;
    }

    @Override // android.graphics.drawable.Drawable
    public Insets getOpticalInsets() {
        return this.mInsets;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (this.mAlpha != i) {
            this.mAlpha = i;
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                if (this.mEnterAnimationEnd == 0) {
                    drawable.mutate().setAlpha(i);
                } else {
                    animate(false);
                }
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean z) {
        if (this.mDrawableContainerState.mDither != z) {
            this.mDrawableContainerState.mDither = z;
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                drawable.mutate().setDither(this.mDrawableContainerState.mDither);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        if (this.mColorFilter != colorFilter) {
            this.mColorFilter = colorFilter;
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                drawable.mutate().setColorFilter(colorFilter);
            }
        }
    }

    public void setEnterFadeDuration(int i) {
        this.mDrawableContainerState.mEnterFadeDuration = i;
    }

    public void setExitFadeDuration(int i) {
        this.mDrawableContainerState.mExitFadeDuration = i;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        Drawable drawable = this.mLastDrawable;
        if (drawable != null) {
            drawable.setBounds(rect);
        }
        Drawable drawable2 = this.mCurrDrawable;
        if (drawable2 != null) {
            drawable2.setBounds(rect);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mDrawableContainerState.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean z) {
        this.mDrawableContainerState.mAutoMirrored = z;
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            drawable.mutate().setAutoMirrored(this.mDrawableContainerState.mAutoMirrored);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isAutoMirrored() {
        return this.mDrawableContainerState.mAutoMirrored;
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        boolean z;
        Drawable drawable = this.mLastDrawable;
        boolean z2 = true;
        if (drawable != null) {
            drawable.jumpToCurrentState();
            this.mLastDrawable = null;
            z = true;
        } else {
            z = false;
        }
        Drawable drawable2 = this.mCurrDrawable;
        if (drawable2 != null) {
            drawable2.jumpToCurrentState();
            this.mCurrDrawable.mutate().setAlpha(this.mAlpha);
        }
        if (this.mExitAnimationEnd != 0) {
            this.mExitAnimationEnd = 0L;
            z = true;
        }
        if (this.mEnterAnimationEnd != 0) {
            this.mEnterAnimationEnd = 0L;
        } else {
            z2 = z;
        }
        if (z2) {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        Drawable drawable = this.mLastDrawable;
        if (drawable != null) {
            return drawable.setState(iArr);
        }
        Drawable drawable2 = this.mCurrDrawable;
        if (drawable2 != null) {
            return drawable2.setState(iArr);
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        Drawable drawable = this.mLastDrawable;
        if (drawable != null) {
            return drawable.setLevel(i);
        }
        Drawable drawable2 = this.mCurrDrawable;
        if (drawable2 != null) {
            return drawable2.setLevel(i);
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantWidth();
        }
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return -1;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantHeight();
        }
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return -1;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantMinimumWidth();
        }
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            return drawable.getMinimumWidth();
        }
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantMinimumHeight();
        }
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            return drawable.getMinimumHeight();
        }
        return 0;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (drawable != this.mCurrDrawable || getCallback() == null) {
            return;
        }
        getCallback().invalidateDrawable(this);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        if (drawable != this.mCurrDrawable || getCallback() == null) {
            return;
        }
        getCallback().scheduleDrawable(this, runnable, j);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        if (drawable != this.mCurrDrawable || getCallback() == null) {
            return;
        }
        getCallback().unscheduleDrawable(this, runnable);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        boolean visible = super.setVisible(z, z2);
        Drawable drawable = this.mLastDrawable;
        if (drawable != null) {
            drawable.setVisible(z, z2);
        }
        Drawable drawable2 = this.mCurrDrawable;
        if (drawable2 != null) {
            drawable2.setVisible(z, z2);
        }
        return visible;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        Drawable drawable = this.mCurrDrawable;
        if (drawable == null || !drawable.isVisible()) {
            return -2;
        }
        return this.mDrawableContainerState.getOpacity();
    }

    public boolean selectDrawable(int i) {
        if (i == this.mCurIndex) {
            return false;
        }
        long jUptimeMillis = SystemClock.uptimeMillis();
        if (this.mDrawableContainerState.mExitFadeDuration > 0) {
            Drawable drawable = this.mLastDrawable;
            if (drawable != null) {
                drawable.setVisible(false, false);
            }
            Drawable drawable2 = this.mCurrDrawable;
            if (drawable2 != null) {
                this.mLastDrawable = drawable2;
                this.mExitAnimationEnd = ((long) this.mDrawableContainerState.mExitFadeDuration) + jUptimeMillis;
            } else {
                this.mLastDrawable = null;
                this.mExitAnimationEnd = 0L;
            }
        } else {
            Drawable drawable3 = this.mCurrDrawable;
            if (drawable3 != null) {
                drawable3.setVisible(false, false);
            }
        }
        if (i >= 0 && i < this.mDrawableContainerState.mNumChildren) {
            Drawable child = this.mDrawableContainerState.getChild(i);
            this.mCurrDrawable = child;
            this.mCurIndex = i;
            if (child != null) {
                this.mInsets = child.getOpticalInsets();
                child.mutate();
                if (this.mDrawableContainerState.mEnterFadeDuration > 0) {
                    this.mEnterAnimationEnd = jUptimeMillis + ((long) this.mDrawableContainerState.mEnterFadeDuration);
                } else {
                    child.setAlpha(this.mAlpha);
                }
                child.setVisible(isVisible(), true);
                child.setDither(this.mDrawableContainerState.mDither);
                child.setColorFilter(this.mColorFilter);
                child.setState(getState());
                child.setLevel(getLevel());
                child.setBounds(getBounds());
                child.setLayoutDirection(getLayoutDirection());
                child.setAutoMirrored(this.mDrawableContainerState.mAutoMirrored);
            } else {
                this.mInsets = Insets.NONE;
            }
        } else {
            this.mCurrDrawable = null;
            this.mInsets = Insets.NONE;
            this.mCurIndex = -1;
        }
        if (this.mEnterAnimationEnd != 0 || this.mExitAnimationEnd != 0) {
            Runnable runnable = this.mAnimationRunnable;
            if (runnable == null) {
                this.mAnimationRunnable = new Runnable() { // from class: android.graphics.drawable.DrawableContainer.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DrawableContainer.this.animate(true);
                        DrawableContainer.this.invalidateSelf();
                    }
                };
            } else {
                unscheduleSelf(runnable);
            }
            animate(true);
        }
        invalidateSelf();
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0045  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x006f  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0074 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:26:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void animate(boolean r14) {
        /*
            r13 = this;
            long r0 = android.os.SystemClock.uptimeMillis()
            android.graphics.drawable.Drawable r2 = r13.mCurrDrawable
            r3 = 1
            r4 = 255(0xff, double:1.26E-321)
            r6 = 0
            r7 = 0
            if (r2 == 0) goto L3e
            long r9 = r13.mEnterAnimationEnd
            int r11 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r11 == 0) goto L40
            int r11 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r11 > 0) goto L24
            android.graphics.drawable.Drawable r2 = r2.mutate()
            int r9 = r13.mAlpha
            r2.setAlpha(r9)
            r13.mEnterAnimationEnd = r7
            goto L40
        L24:
            long r9 = r9 - r0
            long r9 = r9 * r4
            int r2 = (int) r9
            android.graphics.drawable.DrawableContainer$DrawableContainerState r9 = r13.mDrawableContainerState
            int r9 = r9.mEnterFadeDuration
            int r2 = r2 / r9
            android.graphics.drawable.Drawable r9 = r13.mCurrDrawable
            android.graphics.drawable.Drawable r9 = r9.mutate()
            int r2 = 255 - r2
            int r10 = r13.mAlpha
            int r2 = r2 * r10
            int r2 = r2 / 255
            r9.setAlpha(r2)
            r2 = r3
            goto L41
        L3e:
            r13.mEnterAnimationEnd = r7
        L40:
            r2 = r6
        L41:
            android.graphics.drawable.Drawable r9 = r13.mLastDrawable
            if (r9 == 0) goto L6f
            long r10 = r13.mExitAnimationEnd
            int r12 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r12 == 0) goto L71
            int r12 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r12 > 0) goto L58
            r9.setVisible(r6, r6)
            r3 = 0
            r13.mLastDrawable = r3
            r13.mExitAnimationEnd = r7
            goto L71
        L58:
            long r10 = r10 - r0
            long r10 = r10 * r4
            int r2 = (int) r10
            android.graphics.drawable.DrawableContainer$DrawableContainerState r4 = r13.mDrawableContainerState
            int r4 = r4.mExitFadeDuration
            int r2 = r2 / r4
            android.graphics.drawable.Drawable r4 = r13.mLastDrawable
            android.graphics.drawable.Drawable r4 = r4.mutate()
            int r5 = r13.mAlpha
            int r2 = r2 * r5
            int r2 = r2 / 255
            r4.setAlpha(r2)
            goto L72
        L6f:
            r13.mExitAnimationEnd = r7
        L71:
            r3 = r2
        L72:
            if (r14 == 0) goto L7e
            if (r3 == 0) goto L7e
            java.lang.Runnable r14 = r13.mAnimationRunnable
            r2 = 16
            long r0 = r0 + r2
            r13.scheduleSelf(r14, r0)
        L7e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.DrawableContainer.animate(boolean):void");
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable getCurrent() {
        return this.mCurrDrawable;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (!this.mDrawableContainerState.canConstantState()) {
            return null;
        }
        this.mDrawableContainerState.mChangingConfigurations = getChangingConfigurations();
        return this.mDrawableContainerState;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mDrawableContainerState.mutate();
            this.mMutated = true;
        }
        return this;
    }

    public static abstract class DrawableContainerState extends Drawable.ConstantState {
        boolean mAutoMirrored;
        boolean mCanConstantState;
        int mChangingConfigurations;
        boolean mCheckedConstantState;
        boolean mCheckedOpacity;
        boolean mCheckedStateful;
        int mChildrenChangingConfigurations;
        boolean mComputedConstantSize;
        int mConstantHeight;
        int mConstantMinimumHeight;
        int mConstantMinimumWidth;
        Rect mConstantPadding;
        boolean mConstantSize;
        int mConstantWidth;
        boolean mDither;
        SparseArray<ConstantStateFuture> mDrawableFutures;
        Drawable[] mDrawables;
        int mEnterFadeDuration;
        int mExitFadeDuration;
        int mLayoutDirection;
        boolean mMutated;
        int mNumChildren;
        int mOpacity;
        final DrawableContainer mOwner;
        boolean mPaddingChecked;
        final Resources mRes;
        boolean mStateful;
        boolean mVariablePadding;

        DrawableContainerState(DrawableContainerState drawableContainerState, DrawableContainer drawableContainer, Resources resources) {
            this.mDither = true;
            this.mOwner = drawableContainer;
            this.mRes = resources;
            if (drawableContainerState != null) {
                this.mChangingConfigurations = drawableContainerState.mChangingConfigurations;
                this.mChildrenChangingConfigurations = drawableContainerState.mChildrenChangingConfigurations;
                this.mCheckedConstantState = true;
                this.mCanConstantState = true;
                this.mVariablePadding = drawableContainerState.mVariablePadding;
                this.mConstantSize = drawableContainerState.mConstantSize;
                this.mDither = drawableContainerState.mDither;
                this.mMutated = drawableContainerState.mMutated;
                this.mLayoutDirection = drawableContainerState.mLayoutDirection;
                this.mEnterFadeDuration = drawableContainerState.mEnterFadeDuration;
                this.mExitFadeDuration = drawableContainerState.mExitFadeDuration;
                this.mAutoMirrored = drawableContainerState.mAutoMirrored;
                this.mConstantPadding = drawableContainerState.getConstantPadding();
                this.mPaddingChecked = true;
                this.mConstantWidth = drawableContainerState.getConstantWidth();
                this.mConstantHeight = drawableContainerState.getConstantHeight();
                this.mConstantMinimumWidth = drawableContainerState.getConstantMinimumWidth();
                this.mConstantMinimumHeight = drawableContainerState.getConstantMinimumHeight();
                this.mComputedConstantSize = true;
                this.mOpacity = drawableContainerState.getOpacity();
                this.mCheckedOpacity = true;
                this.mStateful = drawableContainerState.isStateful();
                this.mCheckedStateful = true;
                Drawable[] drawableArr = drawableContainerState.mDrawables;
                this.mDrawables = new Drawable[drawableArr.length];
                this.mNumChildren = drawableContainerState.mNumChildren;
                SparseArray<ConstantStateFuture> sparseArray = drawableContainerState.mDrawableFutures;
                if (sparseArray != null) {
                    this.mDrawableFutures = sparseArray.m15clone();
                } else {
                    this.mDrawableFutures = new SparseArray<>(this.mNumChildren);
                }
                int i = this.mNumChildren;
                for (int i2 = 0; i2 < i; i2++) {
                    if (drawableArr[i2] != null) {
                        this.mDrawableFutures.put(i2, new ConstantStateFuture(drawableArr[i2]));
                    }
                }
                return;
            }
            this.mDrawables = new Drawable[10];
            this.mNumChildren = 0;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations | this.mChildrenChangingConfigurations;
        }

        public final int addChild(Drawable drawable) {
            int i = this.mNumChildren;
            if (i >= this.mDrawables.length) {
                growArray(i, i + 10);
            }
            drawable.setVisible(false, true);
            drawable.setCallback(this.mOwner);
            this.mDrawables[i] = drawable;
            this.mNumChildren++;
            this.mChildrenChangingConfigurations = drawable.getChangingConfigurations() | this.mChildrenChangingConfigurations;
            this.mCheckedStateful = false;
            this.mCheckedOpacity = false;
            this.mConstantPadding = null;
            this.mPaddingChecked = false;
            this.mComputedConstantSize = false;
            return i;
        }

        final int getCapacity() {
            return this.mDrawables.length;
        }

        private final void createAllFutures() {
            SparseArray<ConstantStateFuture> sparseArray = this.mDrawableFutures;
            if (sparseArray != null) {
                int size = sparseArray.size();
                for (int i = 0; i < size; i++) {
                    this.mDrawables[this.mDrawableFutures.keyAt(i)] = this.mDrawableFutures.valueAt(i).get(this);
                }
                this.mDrawableFutures = null;
            }
        }

        public final int getChildCount() {
            return this.mNumChildren;
        }

        public final Drawable[] getChildren() {
            createAllFutures();
            return this.mDrawables;
        }

        public final Drawable getChild(int i) {
            int iIndexOfKey;
            Drawable drawable = this.mDrawables[i];
            if (drawable != null) {
                return drawable;
            }
            SparseArray<ConstantStateFuture> sparseArray = this.mDrawableFutures;
            if (sparseArray == null || (iIndexOfKey = sparseArray.indexOfKey(i)) < 0) {
                return null;
            }
            Drawable drawable2 = this.mDrawableFutures.valueAt(iIndexOfKey).get(this);
            this.mDrawables[i] = drawable2;
            this.mDrawableFutures.removeAt(iIndexOfKey);
            return drawable2;
        }

        final void setLayoutDirection(int i) {
            int i2 = this.mNumChildren;
            Drawable[] drawableArr = this.mDrawables;
            for (int i3 = 0; i3 < i2; i3++) {
                if (drawableArr[i3] != null) {
                    drawableArr[i3].setLayoutDirection(i);
                }
            }
            this.mLayoutDirection = i;
        }

        final void mutate() {
            int i = this.mNumChildren;
            Drawable[] drawableArr = this.mDrawables;
            for (int i2 = 0; i2 < i; i2++) {
                if (drawableArr[i2] != null) {
                    drawableArr[i2].mutate();
                }
            }
            this.mMutated = true;
        }

        public final void setVariablePadding(boolean z) {
            this.mVariablePadding = z;
        }

        public final Rect getConstantPadding() {
            Rect rect = null;
            if (this.mVariablePadding) {
                return null;
            }
            Rect rect2 = this.mConstantPadding;
            if (rect2 != null || this.mPaddingChecked) {
                return rect2;
            }
            createAllFutures();
            Rect rect3 = new Rect();
            int i = this.mNumChildren;
            Drawable[] drawableArr = this.mDrawables;
            for (int i2 = 0; i2 < i; i2++) {
                if (drawableArr[i2].getPadding(rect3)) {
                    if (rect == null) {
                        rect = new Rect(0, 0, 0, 0);
                    }
                    if (rect3.left > rect.left) {
                        rect.left = rect3.left;
                    }
                    if (rect3.top > rect.top) {
                        rect.top = rect3.top;
                    }
                    if (rect3.right > rect.right) {
                        rect.right = rect3.right;
                    }
                    if (rect3.bottom > rect.bottom) {
                        rect.bottom = rect3.bottom;
                    }
                }
            }
            this.mPaddingChecked = true;
            this.mConstantPadding = rect;
            return rect;
        }

        public final void setConstantSize(boolean z) {
            this.mConstantSize = z;
        }

        public final boolean isConstantSize() {
            return this.mConstantSize;
        }

        public final int getConstantWidth() {
            if (!this.mComputedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantWidth;
        }

        public final int getConstantHeight() {
            if (!this.mComputedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantHeight;
        }

        public final int getConstantMinimumWidth() {
            if (!this.mComputedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantMinimumWidth;
        }

        public final int getConstantMinimumHeight() {
            if (!this.mComputedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantMinimumHeight;
        }

        protected void computeConstantSize() {
            this.mComputedConstantSize = true;
            createAllFutures();
            int i = this.mNumChildren;
            Drawable[] drawableArr = this.mDrawables;
            this.mConstantHeight = -1;
            this.mConstantWidth = -1;
            this.mConstantMinimumHeight = 0;
            this.mConstantMinimumWidth = 0;
            for (int i2 = 0; i2 < i; i2++) {
                Drawable drawable = drawableArr[i2];
                int intrinsicWidth = drawable.getIntrinsicWidth();
                if (intrinsicWidth > this.mConstantWidth) {
                    this.mConstantWidth = intrinsicWidth;
                }
                int intrinsicHeight = drawable.getIntrinsicHeight();
                if (intrinsicHeight > this.mConstantHeight) {
                    this.mConstantHeight = intrinsicHeight;
                }
                int minimumWidth = drawable.getMinimumWidth();
                if (minimumWidth > this.mConstantMinimumWidth) {
                    this.mConstantMinimumWidth = minimumWidth;
                }
                int minimumHeight = drawable.getMinimumHeight();
                if (minimumHeight > this.mConstantMinimumHeight) {
                    this.mConstantMinimumHeight = minimumHeight;
                }
            }
        }

        public final void setEnterFadeDuration(int i) {
            this.mEnterFadeDuration = i;
        }

        public final int getEnterFadeDuration() {
            return this.mEnterFadeDuration;
        }

        public final void setExitFadeDuration(int i) {
            this.mExitFadeDuration = i;
        }

        public final int getExitFadeDuration() {
            return this.mExitFadeDuration;
        }

        public final int getOpacity() {
            if (this.mCheckedOpacity) {
                return this.mOpacity;
            }
            createAllFutures();
            this.mCheckedOpacity = true;
            int i = this.mNumChildren;
            Drawable[] drawableArr = this.mDrawables;
            int opacity = i > 0 ? drawableArr[0].getOpacity() : -2;
            for (int i2 = 1; i2 < i; i2++) {
                opacity = Drawable.resolveOpacity(opacity, drawableArr[i2].getOpacity());
            }
            this.mOpacity = opacity;
            return opacity;
        }

        public final boolean isStateful() {
            if (this.mCheckedStateful) {
                return this.mStateful;
            }
            createAllFutures();
            this.mCheckedStateful = true;
            int i = this.mNumChildren;
            Drawable[] drawableArr = this.mDrawables;
            for (int i2 = 0; i2 < i; i2++) {
                if (drawableArr[i2].isStateful()) {
                    this.mStateful = true;
                    return true;
                }
            }
            this.mStateful = false;
            return false;
        }

        public void growArray(int i, int i2) {
            Drawable[] drawableArr = new Drawable[i2];
            System.arraycopy(this.mDrawables, 0, drawableArr, 0, i);
            this.mDrawables = drawableArr;
        }

        public synchronized boolean canConstantState() {
            if (this.mCheckedConstantState) {
                return this.mCanConstantState;
            }
            createAllFutures();
            this.mCheckedConstantState = true;
            int i = this.mNumChildren;
            Drawable[] drawableArr = this.mDrawables;
            for (int i2 = 0; i2 < i; i2++) {
                if (drawableArr[i2].getConstantState() == null) {
                    this.mCanConstantState = false;
                    return false;
                }
            }
            this.mCanConstantState = true;
            return true;
        }

        private static class ConstantStateFuture {
            private final Drawable.ConstantState mConstantState;

            private ConstantStateFuture(Drawable drawable) {
                this.mConstantState = drawable.getConstantState();
            }

            public Drawable get(DrawableContainerState drawableContainerState) {
                Drawable drawableNewDrawable = drawableContainerState.mRes == null ? this.mConstantState.newDrawable() : this.mConstantState.newDrawable(drawableContainerState.mRes);
                drawableNewDrawable.setLayoutDirection(drawableContainerState.mLayoutDirection);
                drawableNewDrawable.setCallback(drawableContainerState.mOwner);
                if (drawableContainerState.mMutated) {
                    drawableNewDrawable.mutate();
                }
                return drawableNewDrawable;
            }
        }
    }

    protected void setConstantState(DrawableContainerState drawableContainerState) {
        this.mDrawableContainerState = drawableContainerState;
    }
}
