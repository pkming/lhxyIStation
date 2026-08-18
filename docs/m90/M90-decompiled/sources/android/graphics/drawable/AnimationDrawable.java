package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.DrawableContainer;
import android.os.SystemClock;

/* JADX INFO: loaded from: classes.dex */
public class AnimationDrawable extends DrawableContainer implements Runnable, Animatable {
    private final AnimationState mAnimationState;
    private int mCurFrame;
    private boolean mMutated;

    public AnimationDrawable() {
        this(null, null);
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        boolean visible = super.setVisible(z, z2);
        if (!z) {
            unscheduleSelf(this);
        } else if (visible || z2) {
            setFrame(0, true, true);
        }
        return visible;
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (isRunning()) {
            return;
        }
        run();
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        if (isRunning()) {
            unscheduleSelf(this);
        }
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.mCurFrame > -1;
    }

    @Override // java.lang.Runnable
    public void run() {
        nextFrame(false);
    }

    @Override // android.graphics.drawable.Drawable
    public void unscheduleSelf(Runnable runnable) {
        this.mCurFrame = -1;
        super.unscheduleSelf(runnable);
    }

    public int getNumberOfFrames() {
        return this.mAnimationState.getChildCount();
    }

    public Drawable getFrame(int i) {
        return this.mAnimationState.getChild(i);
    }

    public int getDuration(int i) {
        return this.mAnimationState.mDurations[i];
    }

    public boolean isOneShot() {
        return this.mAnimationState.mOneShot;
    }

    public void setOneShot(boolean z) {
        this.mAnimationState.mOneShot = z;
    }

    public void addFrame(Drawable drawable, int i) {
        this.mAnimationState.addFrame(drawable, i);
        if (this.mCurFrame < 0) {
            setFrame(0, true, false);
        }
    }

    private void nextFrame(boolean z) {
        boolean z2 = true;
        int i = this.mCurFrame + 1;
        int childCount = this.mAnimationState.getChildCount();
        if (i >= childCount) {
            i = 0;
        }
        if (this.mAnimationState.mOneShot && i >= childCount - 1) {
            z2 = false;
        }
        setFrame(i, z, z2);
    }

    private void setFrame(int i, boolean z, boolean z2) {
        if (i >= this.mAnimationState.getChildCount()) {
            return;
        }
        this.mCurFrame = i;
        selectDrawable(i);
        if (z) {
            unscheduleSelf(this);
        }
        if (z2) {
            this.mCurFrame = i;
            scheduleSelf(this, SystemClock.uptimeMillis() + ((long) this.mAnimationState.mDurations[i]));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x00bb, code lost:
    
        setFrame(0, true, false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00be, code lost:
    
        return;
     */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void inflate(android.content.res.Resources r8, org.xmlpull.v1.XmlPullParser r9, android.util.AttributeSet r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r7 = this;
            int[] r0 = com.android.internal.R.styleable.AnimationDrawable
            android.content.res.TypedArray r0 = r8.obtainAttributes(r10, r0)
            r1 = 0
            super.inflateWithAttributes(r8, r9, r0, r1)
            android.graphics.drawable.AnimationDrawable$AnimationState r2 = r7.mAnimationState
            r3 = 1
            boolean r4 = r0.getBoolean(r3, r1)
            r2.setVariablePadding(r4)
            android.graphics.drawable.AnimationDrawable$AnimationState r2 = r7.mAnimationState
            r4 = 2
            boolean r5 = r0.getBoolean(r4, r1)
            android.graphics.drawable.AnimationDrawable.AnimationState.access$102(r2, r5)
            r0.recycle()
            int r0 = r9.getDepth()
            int r0 = r0 + r3
        L26:
            int r2 = r9.next()
            if (r2 == r3) goto Lbb
            int r5 = r9.getDepth()
            if (r5 >= r0) goto L35
            r6 = 3
            if (r2 == r6) goto Lbb
        L35:
            if (r2 == r4) goto L38
            goto L26
        L38:
            if (r5 > r0) goto L26
            java.lang.String r2 = r9.getName()
            java.lang.String r5 = "item"
            boolean r2 = r2.equals(r5)
            if (r2 != 0) goto L47
            goto L26
        L47:
            int[] r2 = com.android.internal.R.styleable.AnimationDrawableItem
            android.content.res.TypedArray r2 = r8.obtainAttributes(r10, r2)
            r5 = -1
            int r5 = r2.getInt(r1, r5)
            if (r5 < 0) goto L9e
            int r6 = r2.getResourceId(r3, r1)
            r2.recycle()
            if (r6 == 0) goto L62
            android.graphics.drawable.Drawable r2 = r8.getDrawable(r6)
            goto L70
        L62:
            int r2 = r9.next()
            r6 = 4
            if (r2 != r6) goto L6a
            goto L62
        L6a:
            if (r2 != r4) goto L7b
            android.graphics.drawable.Drawable r2 = android.graphics.drawable.Drawable.createFromXmlInner(r8, r9, r10)
        L70:
            android.graphics.drawable.AnimationDrawable$AnimationState r6 = r7.mAnimationState
            r6.addFrame(r2, r5)
            if (r2 == 0) goto L26
            r2.setCallback(r7)
            goto L26
        L7b:
            org.xmlpull.v1.XmlPullParserException r8 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r9 = r9.getPositionDescription()
            java.lang.StringBuilder r9 = r10.append(r9)
            java.lang.String r10 = ": <item> tag requires a 'drawable' attribute or child tag"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = " defining a drawable"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.<init>(r9)
            throw r8
        L9e:
            org.xmlpull.v1.XmlPullParserException r8 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r9 = r9.getPositionDescription()
            java.lang.StringBuilder r9 = r10.append(r9)
            java.lang.String r10 = ": <item> tag requires a 'duration' attribute"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.<init>(r9)
            throw r8
        Lbb:
            r7.setFrame(r1, r3, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.AnimationDrawable.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):void");
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            AnimationState animationState = this.mAnimationState;
            animationState.mDurations = (int[]) animationState.mDurations.clone();
            this.mMutated = true;
        }
        return this;
    }

    private static final class AnimationState extends DrawableContainer.DrawableContainerState {
        private int[] mDurations;
        private boolean mOneShot;

        AnimationState(AnimationState animationState, AnimationDrawable animationDrawable, Resources resources) {
            super(animationState, animationDrawable, resources);
            if (animationState != null) {
                this.mDurations = animationState.mDurations;
                this.mOneShot = animationState.mOneShot;
            } else {
                this.mDurations = new int[getCapacity()];
                this.mOneShot = true;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new AnimationDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new AnimationDrawable(this, resources);
        }

        public void addFrame(Drawable drawable, int i) {
            this.mDurations[super.addChild(drawable)] = i;
        }

        @Override // android.graphics.drawable.DrawableContainer.DrawableContainerState
        public void growArray(int i, int i2) {
            super.growArray(i, i2);
            int[] iArr = new int[i2];
            System.arraycopy(this.mDurations, 0, iArr, 0, i);
            this.mDurations = iArr;
        }
    }

    private AnimationDrawable(AnimationState animationState, Resources resources) {
        this.mCurFrame = -1;
        AnimationState animationState2 = new AnimationState(animationState, this, resources);
        this.mAnimationState = animationState2;
        setConstantState(animationState2);
        if (animationState != null) {
            setFrame(0, true, false);
        }
    }
}
