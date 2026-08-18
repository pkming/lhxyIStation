package android.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Pools;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import com.amap.api.services.core.AMapException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
@RemoteViews.RemoteView
public class ProgressBar extends View {
    private static final int MAX_LEVEL = 10000;
    private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;
    private AccessibilityEventSender mAccessibilityEventSender;
    private AlphaAnimation mAnimation;
    private boolean mAttached;
    private int mBehavior;
    private Drawable mCurrentDrawable;
    private int mDuration;
    private boolean mHasAnimation;
    private boolean mInDrawing;
    private boolean mIndeterminate;
    private Drawable mIndeterminateDrawable;
    private Interpolator mInterpolator;
    private int mMax;
    int mMaxHeight;
    int mMaxWidth;
    int mMinHeight;
    int mMinWidth;
    boolean mMirrorForRtl;
    private boolean mNoInvalidate;
    private boolean mOnlyIndeterminate;
    private int mProgress;
    private Drawable mProgressDrawable;
    private final ArrayList<RefreshData> mRefreshData;
    private boolean mRefreshIsPosted;
    private RefreshProgressRunnable mRefreshProgressRunnable;
    Bitmap mSampleTile;
    private int mSecondaryProgress;
    private boolean mShouldStartAnimationDrawable;
    private Transformation mTransformation;
    private long mUiThreadId;

    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.progressBarStyle);
    }

    public ProgressBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ProgressBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        this.mMirrorForRtl = false;
        this.mRefreshData = new ArrayList<>();
        this.mUiThreadId = Thread.currentThread().getId();
        initProgressBar();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.android.internal.R.styleable.ProgressBar, i, i2);
        this.mNoInvalidate = true;
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(8);
        if (drawable != null) {
            setProgressDrawable(tileify(drawable, false));
        }
        this.mDuration = typedArrayObtainStyledAttributes.getInt(9, this.mDuration);
        this.mMinWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(11, this.mMinWidth);
        this.mMaxWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(0, this.mMaxWidth);
        this.mMinHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(12, this.mMinHeight);
        this.mMaxHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(1, this.mMaxHeight);
        this.mBehavior = typedArrayObtainStyledAttributes.getInt(10, this.mBehavior);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(13, R.anim.linear_interpolator);
        if (resourceId > 0) {
            setInterpolator(context, resourceId);
        }
        setMax(typedArrayObtainStyledAttributes.getInt(2, this.mMax));
        setProgress(typedArrayObtainStyledAttributes.getInt(3, this.mProgress));
        setSecondaryProgress(typedArrayObtainStyledAttributes.getInt(4, this.mSecondaryProgress));
        Drawable drawable2 = typedArrayObtainStyledAttributes.getDrawable(7);
        if (drawable2 != null) {
            setIndeterminateDrawable(tileifyIndeterminate(drawable2));
        }
        boolean z = typedArrayObtainStyledAttributes.getBoolean(6, this.mOnlyIndeterminate);
        this.mOnlyIndeterminate = z;
        this.mNoInvalidate = false;
        setIndeterminate(z || typedArrayObtainStyledAttributes.getBoolean(5, this.mIndeterminate));
        this.mMirrorForRtl = typedArrayObtainStyledAttributes.getBoolean(15, this.mMirrorForRtl);
        typedArrayObtainStyledAttributes.recycle();
    }

    private Drawable tileify(Drawable drawable, boolean z) {
        int i = 0;
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            int numberOfLayers = layerDrawable.getNumberOfLayers();
            Drawable[] drawableArr = new Drawable[numberOfLayers];
            for (int i2 = 0; i2 < numberOfLayers; i2++) {
                int id = layerDrawable.getId(i2);
                drawableArr[i2] = tileify(layerDrawable.getDrawable(i2), id == 16908301 || id == 16908303);
            }
            LayerDrawable layerDrawable2 = new LayerDrawable(drawableArr);
            while (i < numberOfLayers) {
                layerDrawable2.setId(i, layerDrawable.getId(i));
                i++;
            }
            return layerDrawable2;
        }
        if (drawable instanceof StateListDrawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) drawable;
            StateListDrawable stateListDrawable2 = new StateListDrawable();
            int stateCount = stateListDrawable.getStateCount();
            while (i < stateCount) {
                stateListDrawable2.addState(stateListDrawable.getStateSet(i), tileify(stateListDrawable.getStateDrawable(i), z));
                i++;
            }
            return stateListDrawable2;
        }
        if (!(drawable instanceof BitmapDrawable)) {
            return drawable;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (this.mSampleTile == null) {
            this.mSampleTile = bitmap;
        }
        ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
        shapeDrawable.getPaint().setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP));
        return z ? new ClipDrawable(shapeDrawable, 3, 1) : shapeDrawable;
    }

    Shape getDrawableShape() {
        return new RoundRectShape(new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f}, null, null);
    }

    private Drawable tileifyIndeterminate(Drawable drawable) {
        if (!(drawable instanceof AnimationDrawable)) {
            return drawable;
        }
        AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
        int numberOfFrames = animationDrawable.getNumberOfFrames();
        AnimationDrawable animationDrawable2 = new AnimationDrawable();
        animationDrawable2.setOneShot(animationDrawable.isOneShot());
        for (int i = 0; i < numberOfFrames; i++) {
            Drawable drawableTileify = tileify(animationDrawable.getFrame(i), true);
            drawableTileify.setLevel(10000);
            animationDrawable2.addFrame(drawableTileify, animationDrawable.getDuration(i));
        }
        animationDrawable2.setLevel(10000);
        return animationDrawable2;
    }

    private void initProgressBar() {
        this.mMax = 100;
        this.mProgress = 0;
        this.mSecondaryProgress = 0;
        this.mIndeterminate = false;
        this.mOnlyIndeterminate = false;
        this.mDuration = AMapException.CODE_AMAP_SHARE_LICENSE_IS_EXPIRED;
        this.mBehavior = 1;
        this.mMinWidth = 24;
        this.mMaxWidth = 48;
        this.mMinHeight = 24;
        this.mMaxHeight = 48;
    }

    @ViewDebug.ExportedProperty(category = NotificationCompat.CATEGORY_PROGRESS)
    public synchronized boolean isIndeterminate() {
        return this.mIndeterminate;
    }

    @RemotableViewMethod
    public synchronized void setIndeterminate(boolean z) {
        if ((!this.mOnlyIndeterminate || !this.mIndeterminate) && z != this.mIndeterminate) {
            this.mIndeterminate = z;
            if (z) {
                this.mCurrentDrawable = this.mIndeterminateDrawable;
                startAnimation();
            } else {
                this.mCurrentDrawable = this.mProgressDrawable;
                stopAnimation();
            }
        }
    }

    public Drawable getIndeterminateDrawable() {
        return this.mIndeterminateDrawable;
    }

    public void setIndeterminateDrawable(Drawable drawable) {
        if (drawable != null) {
            drawable.setCallback(this);
        }
        this.mIndeterminateDrawable = drawable;
        if (drawable != null && canResolveLayoutDirection()) {
            this.mIndeterminateDrawable.setLayoutDirection(getLayoutDirection());
        }
        if (this.mIndeterminate) {
            this.mCurrentDrawable = drawable;
            postInvalidate();
        }
    }

    public Drawable getProgressDrawable() {
        return this.mProgressDrawable;
    }

    public void setProgressDrawable(Drawable drawable) {
        boolean z;
        Drawable drawable2 = this.mProgressDrawable;
        if (drawable2 == null || drawable == drawable2) {
            z = false;
        } else {
            drawable2.setCallback(null);
            z = true;
        }
        if (drawable != null) {
            drawable.setCallback(this);
            if (canResolveLayoutDirection()) {
                drawable.setLayoutDirection(getLayoutDirection());
            }
            int minimumHeight = drawable.getMinimumHeight();
            if (this.mMaxHeight < minimumHeight) {
                this.mMaxHeight = minimumHeight;
                requestLayout();
            }
        }
        this.mProgressDrawable = drawable;
        if (!this.mIndeterminate) {
            this.mCurrentDrawable = drawable;
            postInvalidate();
        }
        if (z) {
            updateDrawableBounds(getWidth(), getHeight());
            updateDrawableState();
            doRefreshProgress(R.id.progress, this.mProgress, false, false);
            doRefreshProgress(R.id.secondaryProgress, this.mSecondaryProgress, false, false);
        }
    }

    Drawable getCurrentDrawable() {
        return this.mCurrentDrawable;
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mProgressDrawable || drawable == this.mIndeterminateDrawable || super.verifyDrawable(drawable);
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
        Drawable drawable2 = this.mIndeterminateDrawable;
        if (drawable2 != null) {
            drawable2.jumpToCurrentState();
        }
    }

    @Override // android.view.View
    public void onResolveDrawables(int i) {
        Drawable drawable = this.mCurrentDrawable;
        if (drawable != null) {
            drawable.setLayoutDirection(i);
        }
        Drawable drawable2 = this.mIndeterminateDrawable;
        if (drawable2 != null) {
            drawable2.setLayoutDirection(i);
        }
        Drawable drawable3 = this.mProgressDrawable;
        if (drawable3 != null) {
            drawable3.setLayoutDirection(i);
        }
    }

    @Override // android.view.View
    public void postInvalidate() {
        if (this.mNoInvalidate) {
            return;
        }
        super.postInvalidate();
    }

    private class RefreshProgressRunnable implements Runnable {
        private RefreshProgressRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (ProgressBar.this) {
                int size = ProgressBar.this.mRefreshData.size();
                for (int i = 0; i < size; i++) {
                    RefreshData refreshData = (RefreshData) ProgressBar.this.mRefreshData.get(i);
                    ProgressBar.this.doRefreshProgress(refreshData.id, refreshData.progress, refreshData.fromUser, true);
                    refreshData.recycle();
                }
                ProgressBar.this.mRefreshData.clear();
                ProgressBar.this.mRefreshIsPosted = false;
            }
        }
    }

    private static class RefreshData {
        private static final int POOL_MAX = 24;
        private static final Pools.SynchronizedPool<RefreshData> sPool = new Pools.SynchronizedPool<>(24);
        public boolean fromUser;
        public int id;
        public int progress;

        private RefreshData() {
        }

        public static RefreshData obtain(int i, int i2, boolean z) {
            RefreshData refreshDataAcquire = sPool.acquire();
            if (refreshDataAcquire == null) {
                refreshDataAcquire = new RefreshData();
            }
            refreshDataAcquire.id = i;
            refreshDataAcquire.progress = i2;
            refreshDataAcquire.fromUser = z;
            return refreshDataAcquire;
        }

        public void recycle() {
            sPool.release(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void doRefreshProgress(int i, int i2, boolean z, boolean z2) {
        int i3 = this.mMax;
        float f = i3 > 0 ? i2 / i3 : 0.0f;
        Drawable drawable = this.mCurrentDrawable;
        if (drawable != null) {
            Drawable drawableFindDrawableByLayerId = null;
            if ((drawable instanceof LayerDrawable) && (drawableFindDrawableByLayerId = ((LayerDrawable) drawable).findDrawableByLayerId(i)) != null && canResolveLayoutDirection()) {
                drawableFindDrawableByLayerId.setLayoutDirection(getLayoutDirection());
            }
            int i4 = (int) (10000.0f * f);
            if (drawableFindDrawableByLayerId != null) {
                drawable = drawableFindDrawableByLayerId;
            }
            drawable.setLevel(i4);
        } else {
            invalidate();
        }
        if (z2 && i == 16908301) {
            onProgressRefresh(f, z);
        }
    }

    void onProgressRefresh(float f, boolean z) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            scheduleAccessibilityEventSender();
        }
    }

    private synchronized void refreshProgress(int i, int i2, boolean z) {
        if (this.mUiThreadId == Thread.currentThread().getId()) {
            doRefreshProgress(i, i2, z, true);
        } else {
            if (this.mRefreshProgressRunnable == null) {
                this.mRefreshProgressRunnable = new RefreshProgressRunnable();
            }
            this.mRefreshData.add(RefreshData.obtain(i, i2, z));
            if (this.mAttached && !this.mRefreshIsPosted) {
                post(this.mRefreshProgressRunnable);
                this.mRefreshIsPosted = true;
            }
        }
    }

    @RemotableViewMethod
    public synchronized void setProgress(int i) {
        setProgress(i, false);
    }

    @RemotableViewMethod
    synchronized void setProgress(int i, boolean z) {
        if (this.mIndeterminate) {
            return;
        }
        if (i < 0) {
            i = 0;
        }
        int i2 = this.mMax;
        if (i > i2) {
            i = i2;
        }
        if (i != this.mProgress) {
            this.mProgress = i;
            refreshProgress(R.id.progress, i, z);
        }
    }

    @RemotableViewMethod
    public synchronized void setSecondaryProgress(int i) {
        if (this.mIndeterminate) {
            return;
        }
        if (i < 0) {
            i = 0;
        }
        int i2 = this.mMax;
        if (i > i2) {
            i = i2;
        }
        if (i != this.mSecondaryProgress) {
            this.mSecondaryProgress = i;
            refreshProgress(R.id.secondaryProgress, i, false);
        }
    }

    @ViewDebug.ExportedProperty(category = NotificationCompat.CATEGORY_PROGRESS)
    public synchronized int getProgress() {
        return this.mIndeterminate ? 0 : this.mProgress;
    }

    @ViewDebug.ExportedProperty(category = NotificationCompat.CATEGORY_PROGRESS)
    public synchronized int getSecondaryProgress() {
        return this.mIndeterminate ? 0 : this.mSecondaryProgress;
    }

    @ViewDebug.ExportedProperty(category = NotificationCompat.CATEGORY_PROGRESS)
    public synchronized int getMax() {
        return this.mMax;
    }

    @RemotableViewMethod
    public synchronized void setMax(int i) {
        if (i < 0) {
            i = 0;
        }
        if (i != this.mMax) {
            this.mMax = i;
            postInvalidate();
            if (this.mProgress > i) {
                this.mProgress = i;
            }
            refreshProgress(R.id.progress, this.mProgress, false);
        }
    }

    public final synchronized void incrementProgressBy(int i) {
        setProgress(this.mProgress + i);
    }

    public final synchronized void incrementSecondaryProgressBy(int i) {
        setSecondaryProgress(this.mSecondaryProgress + i);
    }

    void startAnimation() {
        if (getVisibility() != 0) {
            return;
        }
        if (this.mIndeterminateDrawable instanceof Animatable) {
            this.mShouldStartAnimationDrawable = true;
            this.mHasAnimation = false;
        } else {
            this.mHasAnimation = true;
            if (this.mInterpolator == null) {
                this.mInterpolator = new LinearInterpolator();
            }
            Transformation transformation = this.mTransformation;
            if (transformation == null) {
                this.mTransformation = new Transformation();
            } else {
                transformation.clear();
            }
            AlphaAnimation alphaAnimation = this.mAnimation;
            if (alphaAnimation == null) {
                this.mAnimation = new AlphaAnimation(0.0f, 1.0f);
            } else {
                alphaAnimation.reset();
            }
            this.mAnimation.setRepeatMode(this.mBehavior);
            this.mAnimation.setRepeatCount(-1);
            this.mAnimation.setDuration(this.mDuration);
            this.mAnimation.setInterpolator(this.mInterpolator);
            this.mAnimation.setStartTime(-1L);
        }
        postInvalidate();
    }

    void stopAnimation() {
        this.mHasAnimation = false;
        Object obj = this.mIndeterminateDrawable;
        if (obj instanceof Animatable) {
            ((Animatable) obj).stop();
            this.mShouldStartAnimationDrawable = false;
        }
        postInvalidate();
    }

    public void setInterpolator(Context context, int i) {
        setInterpolator(AnimationUtils.loadInterpolator(context, i));
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setVisibility(int i) {
        if (getVisibility() != i) {
            super.setVisibility(i);
            if (this.mIndeterminate) {
                if (i == 8 || i == 4) {
                    stopAnimation();
                } else {
                    startAnimation();
                }
            }
        }
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (this.mIndeterminate) {
            if (i == 8 || i == 4) {
                stopAnimation();
            } else {
                startAnimation();
            }
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (this.mInDrawing) {
            return;
        }
        if (verifyDrawable(drawable)) {
            Rect bounds = drawable.getBounds();
            int i = this.mScrollX + this.mPaddingLeft;
            int i2 = this.mScrollY + this.mPaddingTop;
            invalidate(bounds.left + i, bounds.top + i2, bounds.right + i, bounds.bottom + i2);
            return;
        }
        super.invalidateDrawable(drawable);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        updateDrawableBounds(i, i2);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x004c  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x005e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateDrawableBounds(int r8, int r9) {
        /*
            r7 = this;
            int r0 = r7.mPaddingRight
            int r1 = r7.mPaddingLeft
            int r0 = r0 + r1
            int r8 = r8 - r0
            int r0 = r7.mPaddingTop
            int r1 = r7.mPaddingBottom
            int r0 = r0 + r1
            int r9 = r9 - r0
            android.graphics.drawable.Drawable r0 = r7.mIndeterminateDrawable
            r1 = 0
            if (r0 == 0) goto L64
            boolean r2 = r7.mOnlyIndeterminate
            if (r2 == 0) goto L4c
            boolean r2 = r0 instanceof android.graphics.drawable.AnimationDrawable
            if (r2 != 0) goto L4c
            int r0 = r0.getIntrinsicWidth()
            android.graphics.drawable.Drawable r2 = r7.mIndeterminateDrawable
            int r2 = r2.getIntrinsicHeight()
            float r0 = (float) r0
            float r2 = (float) r2
            float r0 = r0 / r2
            float r2 = (float) r8
            float r3 = (float) r9
            float r4 = r2 / r3
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 == 0) goto L4c
            int r4 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r4 <= 0) goto L3d
            float r3 = r3 * r0
            int r0 = (int) r3
            int r2 = r8 - r0
            int r2 = r2 / 2
            int r0 = r0 + r2
            r3 = r2
            r2 = r0
            r0 = r1
            goto L4f
        L3d:
            r3 = 1065353216(0x3f800000, float:1.0)
            float r3 = r3 / r0
            float r2 = r2 * r3
            int r0 = (int) r2
            int r9 = r9 - r0
            int r9 = r9 / 2
            int r0 = r0 + r9
            r2 = r8
            r3 = r1
            r6 = r0
            r0 = r9
            r9 = r6
            goto L4f
        L4c:
            r2 = r8
            r0 = r1
            r3 = r0
        L4f:
            boolean r4 = r7.isLayoutRtl()
            if (r4 == 0) goto L5e
            boolean r4 = r7.mMirrorForRtl
            if (r4 == 0) goto L5e
            int r2 = r8 - r2
            int r8 = r8 - r3
            r3 = r2
            goto L5f
        L5e:
            r8 = r2
        L5f:
            android.graphics.drawable.Drawable r2 = r7.mIndeterminateDrawable
            r2.setBounds(r3, r0, r8, r9)
        L64:
            android.graphics.drawable.Drawable r0 = r7.mProgressDrawable
            if (r0 == 0) goto L6b
            r0.setBounds(r1, r1, r8, r9)
        L6b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.ProgressBar.updateDrawableBounds(int, int):void");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // android.view.View
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = this.mCurrentDrawable;
        if (drawable != 0) {
            canvas.save();
            if (isLayoutRtl() && this.mMirrorForRtl) {
                canvas.translate(getWidth() - this.mPaddingRight, this.mPaddingTop);
                canvas.scale(-1.0f, 1.0f);
            } else {
                canvas.translate(this.mPaddingLeft, this.mPaddingTop);
            }
            long drawingTime = getDrawingTime();
            if (this.mHasAnimation) {
                this.mAnimation.getTransformation(drawingTime, this.mTransformation);
                float alpha = this.mTransformation.getAlpha();
                try {
                    this.mInDrawing = true;
                    drawable.setLevel((int) (alpha * 10000.0f));
                    this.mInDrawing = false;
                    postInvalidateOnAnimation();
                } catch (Throwable th) {
                    this.mInDrawing = false;
                    throw th;
                }
            }
            drawable.draw(canvas);
            canvas.restore();
            if (this.mShouldStartAnimationDrawable && (drawable instanceof Animatable)) {
                ((Animatable) drawable).start();
                this.mShouldStartAnimationDrawable = false;
            }
        }
    }

    @Override // android.view.View
    protected synchronized void onMeasure(int i, int i2) {
        int iMax;
        int iMax2;
        Drawable drawable = this.mCurrentDrawable;
        if (drawable != null) {
            iMax2 = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, drawable.getIntrinsicWidth()));
            iMax = Math.max(this.mMinHeight, Math.min(this.mMaxHeight, drawable.getIntrinsicHeight()));
        } else {
            iMax = 0;
            iMax2 = 0;
        }
        updateDrawableState();
        setMeasuredDimension(resolveSizeAndState(iMax2 + this.mPaddingLeft + this.mPaddingRight, i, 0), resolveSizeAndState(iMax + this.mPaddingTop + this.mPaddingBottom, i2, 0));
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateDrawableState();
    }

    private void updateDrawableState() {
        int[] drawableState = getDrawableState();
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null && drawable.isStateful()) {
            this.mProgressDrawable.setState(drawableState);
        }
        Drawable drawable2 = this.mIndeterminateDrawable;
        if (drawable2 == null || !drawable2.isStateful()) {
            return;
        }
        this.mIndeterminateDrawable.setState(drawableState);
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.ProgressBar.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int progress;
        int secondaryProgress;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.progress = parcel.readInt();
            this.secondaryProgress = parcel.readInt();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.progress);
            parcel.writeInt(this.secondaryProgress);
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.progress = this.mProgress;
        savedState.secondaryProgress = this.mSecondaryProgress;
        return savedState;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setProgress(savedState.progress);
        setSecondaryProgress(savedState.secondaryProgress);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIndeterminate) {
            startAnimation();
        }
        if (this.mRefreshData != null) {
            synchronized (this) {
                int size = this.mRefreshData.size();
                for (int i = 0; i < size; i++) {
                    RefreshData refreshData = this.mRefreshData.get(i);
                    doRefreshProgress(refreshData.id, refreshData.progress, refreshData.fromUser, true);
                    refreshData.recycle();
                }
                this.mRefreshData.clear();
            }
        }
        this.mAttached = true;
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        if (this.mIndeterminate) {
            stopAnimation();
        }
        RefreshProgressRunnable refreshProgressRunnable = this.mRefreshProgressRunnable;
        if (refreshProgressRunnable != null) {
            removeCallbacks(refreshProgressRunnable);
        }
        RefreshProgressRunnable refreshProgressRunnable2 = this.mRefreshProgressRunnable;
        if (refreshProgressRunnable2 != null && this.mRefreshIsPosted) {
            removeCallbacks(refreshProgressRunnable2);
        }
        AccessibilityEventSender accessibilityEventSender = this.mAccessibilityEventSender;
        if (accessibilityEventSender != null) {
            removeCallbacks(accessibilityEventSender);
        }
        super.onDetachedFromWindow();
        this.mAttached = false;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(ProgressBar.class.getName());
        accessibilityEvent.setItemCount(this.mMax);
        accessibilityEvent.setCurrentItemIndex(this.mProgress);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(ProgressBar.class.getName());
    }

    private void scheduleAccessibilityEventSender() {
        AccessibilityEventSender accessibilityEventSender = this.mAccessibilityEventSender;
        if (accessibilityEventSender == null) {
            this.mAccessibilityEventSender = new AccessibilityEventSender();
        } else {
            removeCallbacks(accessibilityEventSender);
        }
        postDelayed(this.mAccessibilityEventSender, 200L);
    }

    private class AccessibilityEventSender implements Runnable {
        private AccessibilityEventSender() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ProgressBar.this.sendAccessibilityEvent(4);
        }
    }
}
