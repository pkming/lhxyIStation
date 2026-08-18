package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class LayerDrawable extends Drawable implements Drawable.Callback {
    LayerState mLayerState;
    private boolean mMutated;
    private int mOpacityOverride;
    private int[] mPaddingB;
    private int[] mPaddingL;
    private int[] mPaddingR;
    private int[] mPaddingT;
    private final Rect mTmpRect;

    public LayerDrawable(Drawable[] drawableArr) {
        this(drawableArr, (LayerState) null);
    }

    LayerDrawable(Drawable[] drawableArr, LayerState layerState) {
        this(layerState, (Resources) null);
        int length = drawableArr.length;
        ChildDrawable[] childDrawableArr = new ChildDrawable[length];
        for (int i = 0; i < length; i++) {
            childDrawableArr[i] = new ChildDrawable();
            childDrawableArr[i].mDrawable = drawableArr[i];
            drawableArr[i].setCallback(this);
            this.mLayerState.mChildrenChangingConfigurations |= drawableArr[i].getChangingConfigurations();
        }
        this.mLayerState.mNum = length;
        this.mLayerState.mChildren = childDrawableArr;
        ensurePadding();
    }

    LayerDrawable() {
        this((LayerState) null, (Resources) null);
    }

    LayerDrawable(LayerState layerState, Resources resources) {
        this.mOpacityOverride = 0;
        this.mTmpRect = new Rect();
        LayerState layerStateCreateConstantState = createConstantState(layerState, resources);
        this.mLayerState = layerStateCreateConstantState;
        if (layerStateCreateConstantState.mNum > 0) {
            ensurePadding();
        }
    }

    LayerState createConstantState(LayerState layerState, Resources resources) {
        return new LayerState(layerState, this, resources);
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x00af, code lost:
    
        ensurePadding();
        onStateChange(getState());
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00b9, code lost:
    
        return;
     */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void inflate(android.content.res.Resources r16, org.xmlpull.v1.XmlPullParser r17, android.util.AttributeSet r18) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r15 = this;
            r7 = r15
            r8 = r16
            r9 = r18
            super.inflate(r16, r17, r18)
            int[] r0 = com.android.internal.R.styleable.LayerDrawable
            android.content.res.TypedArray r0 = r8.obtainAttributes(r9, r0)
            r10 = 0
            int r1 = r0.getInt(r10, r10)
            r7.mOpacityOverride = r1
            r11 = 1
            boolean r1 = r0.getBoolean(r11, r10)
            r15.setAutoMirrored(r1)
            r0.recycle()
            int r0 = r17.getDepth()
            int r12 = r0 + 1
        L26:
            int r0 = r17.next()
            if (r0 == r11) goto Laf
            int r1 = r17.getDepth()
            r2 = 3
            if (r1 >= r12) goto L35
            if (r0 == r2) goto Laf
        L35:
            r3 = 2
            if (r0 == r3) goto L39
            goto L26
        L39:
            if (r1 > r12) goto L26
            java.lang.String r0 = r17.getName()
            java.lang.String r1 = "item"
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L48
            goto L26
        L48:
            int[] r0 = com.android.internal.R.styleable.LayerDrawableItem
            android.content.res.TypedArray r0 = r8.obtainAttributes(r9, r0)
            int r4 = r0.getDimensionPixelOffset(r3, r10)
            int r5 = r0.getDimensionPixelOffset(r2, r10)
            r1 = 4
            int r6 = r0.getDimensionPixelOffset(r1, r10)
            r2 = 5
            int r13 = r0.getDimensionPixelOffset(r2, r10)
            int r2 = r0.getResourceId(r11, r10)
            r14 = -1
            int r14 = r0.getResourceId(r10, r14)
            r0.recycle()
            if (r2 == 0) goto L74
            android.graphics.drawable.Drawable r0 = r8.getDrawable(r2)
        L72:
            r1 = r0
            goto L82
        L74:
            int r0 = r17.next()
            if (r0 != r1) goto L7b
            goto L74
        L7b:
            if (r0 != r3) goto L8c
            android.graphics.drawable.Drawable r0 = android.graphics.drawable.Drawable.createFromXmlInner(r16, r17, r18)
            goto L72
        L82:
            r0 = r15
            r2 = r14
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r13
            r0.addLayer(r1, r2, r3, r4, r5, r6)
            goto L26
        L8c:
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = r17.getPositionDescription()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = ": <item> tag requires a 'drawable' attribute or "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = "child tag defining a drawable"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        Laf:
            r15.ensurePadding()
            int[] r0 = r15.getState()
            r15.onStateChange(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.LayerDrawable.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):void");
    }

    private void addLayer(Drawable drawable, int i, int i2, int i3, int i4, int i5) {
        LayerState layerState = this.mLayerState;
        int length = layerState.mChildren != null ? layerState.mChildren.length : 0;
        int i6 = layerState.mNum;
        if (i6 >= length) {
            ChildDrawable[] childDrawableArr = new ChildDrawable[length + 10];
            if (i6 > 0) {
                System.arraycopy(layerState.mChildren, 0, childDrawableArr, 0, i6);
            }
            layerState.mChildren = childDrawableArr;
        }
        this.mLayerState.mChildrenChangingConfigurations |= drawable.getChangingConfigurations();
        ChildDrawable childDrawable = new ChildDrawable();
        layerState.mChildren[i6] = childDrawable;
        childDrawable.mId = i;
        childDrawable.mDrawable = drawable;
        childDrawable.mDrawable.setAutoMirrored(isAutoMirrored());
        childDrawable.mInsetL = i2;
        childDrawable.mInsetT = i3;
        childDrawable.mInsetR = i4;
        childDrawable.mInsetB = i5;
        layerState.mNum++;
        drawable.setCallback(this);
    }

    public Drawable findDrawableByLayerId(int i) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        for (int i2 = this.mLayerState.mNum - 1; i2 >= 0; i2--) {
            if (childDrawableArr[i2].mId == i) {
                return childDrawableArr[i2].mDrawable;
            }
        }
        return null;
    }

    public void setId(int i, int i2) {
        this.mLayerState.mChildren[i].mId = i2;
    }

    public int getNumberOfLayers() {
        return this.mLayerState.mNum;
    }

    public Drawable getDrawable(int i) {
        return this.mLayerState.mChildren[i].mDrawable;
    }

    public int getId(int i) {
        return this.mLayerState.mChildren[i].mId;
    }

    public boolean setDrawableByLayerId(int i, Drawable drawable) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        for (int i2 = this.mLayerState.mNum - 1; i2 >= 0; i2--) {
            if (childDrawableArr[i2].mId == i) {
                if (childDrawableArr[i2].mDrawable != null) {
                    if (drawable != null) {
                        drawable.setBounds(childDrawableArr[i2].mDrawable.getBounds());
                    }
                    childDrawableArr[i2].mDrawable.setCallback(null);
                }
                if (drawable != null) {
                    drawable.setCallback(this);
                }
                childDrawableArr[i2].mDrawable = drawable;
                return true;
            }
        }
        return false;
    }

    public void setLayerInset(int i, int i2, int i3, int i4, int i5) {
        ChildDrawable childDrawable = this.mLayerState.mChildren[i];
        childDrawable.mInsetL = i2;
        childDrawable.mInsetT = i3;
        childDrawable.mInsetR = i4;
        childDrawable.mInsetB = i5;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, runnable, j);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, runnable);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        for (int i2 = 0; i2 < i; i2++) {
            childDrawableArr[i2].mDrawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mLayerState.mChangingConfigurations | this.mLayerState.mChildrenChangingConfigurations;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        rect.left = 0;
        rect.top = 0;
        rect.right = 0;
        rect.bottom = 0;
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        for (int i2 = 0; i2 < i; i2++) {
            reapplyPadding(i2, childDrawableArr[i2]);
            rect.left += this.mPaddingL[i2];
            rect.top += this.mPaddingT[i2];
            rect.right += this.mPaddingR[i2];
            rect.bottom += this.mPaddingB[i2];
        }
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        boolean visible = super.setVisible(z, z2);
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        for (int i2 = 0; i2 < i; i2++) {
            childDrawableArr[i2].mDrawable.setVisible(z, z2);
        }
        return visible;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean z) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        for (int i2 = 0; i2 < i; i2++) {
            childDrawableArr[i2].mDrawable.setDither(z);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i2 = this.mLayerState.mNum;
        for (int i3 = 0; i3 < i2; i3++) {
            childDrawableArr[i3].mDrawable.setAlpha(i);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        if (this.mLayerState.mNum > 0) {
            return childDrawableArr[0].mDrawable.getAlpha();
        }
        return super.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        for (int i2 = 0; i2 < i; i2++) {
            childDrawableArr[i2].mDrawable.setColorFilter(colorFilter);
        }
    }

    public void setOpacity(int i) {
        this.mOpacityOverride = i;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        int i = this.mOpacityOverride;
        return i != 0 ? i : this.mLayerState.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean z) {
        this.mLayerState.mAutoMirrored = z;
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        for (int i2 = 0; i2 < i; i2++) {
            childDrawableArr[i2].mDrawable.setAutoMirrored(z);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isAutoMirrored() {
        return this.mLayerState.mAutoMirrored;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mLayerState.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        boolean z = false;
        boolean z2 = false;
        for (int i2 = 0; i2 < i; i2++) {
            ChildDrawable childDrawable = childDrawableArr[i2];
            if (childDrawable.mDrawable.setState(iArr)) {
                z2 = true;
            }
            if (reapplyPadding(i2, childDrawable)) {
                z = true;
            }
        }
        if (z) {
            onBoundsChange(getBounds());
        }
        return z2;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i2 = this.mLayerState.mNum;
        boolean z = false;
        boolean z2 = false;
        for (int i3 = 0; i3 < i2; i3++) {
            ChildDrawable childDrawable = childDrawableArr[i3];
            if (childDrawable.mDrawable.setLevel(i)) {
                z2 = true;
            }
            if (reapplyPadding(i3, childDrawable)) {
                z = true;
            }
        }
        if (z) {
            onBoundsChange(getBounds());
        }
        return z2;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < i; i6++) {
            ChildDrawable childDrawable = childDrawableArr[i6];
            childDrawable.mDrawable.setBounds(rect.left + childDrawable.mInsetL + i2, rect.top + childDrawable.mInsetT + i3, (rect.right - childDrawable.mInsetR) - i4, (rect.bottom - childDrawable.mInsetB) - i5);
            i2 += this.mPaddingL[i6];
            i4 += this.mPaddingR[i6];
            i3 += this.mPaddingT[i6];
            i5 += this.mPaddingB[i6];
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        int i2 = 0;
        int i3 = -1;
        int i4 = 0;
        for (int i5 = 0; i5 < i; i5++) {
            ChildDrawable childDrawable = childDrawableArr[i5];
            int intrinsicWidth = childDrawable.mDrawable.getIntrinsicWidth() + childDrawable.mInsetL + childDrawable.mInsetR + i4 + i2;
            if (intrinsicWidth > i3) {
                i3 = intrinsicWidth;
            }
            i4 += this.mPaddingL[i5];
            i2 += this.mPaddingR[i5];
        }
        return i3;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i = this.mLayerState.mNum;
        int i2 = 0;
        int i3 = -1;
        int i4 = 0;
        for (int i5 = 0; i5 < i; i5++) {
            ChildDrawable childDrawable = childDrawableArr[i5];
            int intrinsicHeight = childDrawable.mDrawable.getIntrinsicHeight() + childDrawable.mInsetT + childDrawable.mInsetB + i4 + i2;
            if (intrinsicHeight > i3) {
                i3 = intrinsicHeight;
            }
            i4 += this.mPaddingT[i5];
            i2 += this.mPaddingB[i5];
        }
        return i3;
    }

    private boolean reapplyPadding(int i, ChildDrawable childDrawable) {
        Rect rect = this.mTmpRect;
        childDrawable.mDrawable.getPadding(rect);
        if (rect.left == this.mPaddingL[i] && rect.top == this.mPaddingT[i] && rect.right == this.mPaddingR[i] && rect.bottom == this.mPaddingB[i]) {
            return false;
        }
        this.mPaddingL[i] = rect.left;
        this.mPaddingT[i] = rect.top;
        this.mPaddingR[i] = rect.right;
        this.mPaddingB[i] = rect.bottom;
        return true;
    }

    private void ensurePadding() {
        int i = this.mLayerState.mNum;
        int[] iArr = this.mPaddingL;
        if (iArr == null || iArr.length < i) {
            this.mPaddingL = new int[i];
            this.mPaddingT = new int[i];
            this.mPaddingR = new int[i];
            this.mPaddingB = new int[i];
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (!this.mLayerState.canConstantState()) {
            return null;
        }
        this.mLayerState.mChangingConfigurations = getChangingConfigurations();
        return this.mLayerState;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            LayerState layerStateCreateConstantState = createConstantState(this.mLayerState, null);
            this.mLayerState = layerStateCreateConstantState;
            ChildDrawable[] childDrawableArr = layerStateCreateConstantState.mChildren;
            int i = this.mLayerState.mNum;
            for (int i2 = 0; i2 < i; i2++) {
                childDrawableArr[i2].mDrawable.mutate();
            }
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void setLayoutDirection(int i) {
        ChildDrawable[] childDrawableArr = this.mLayerState.mChildren;
        int i2 = this.mLayerState.mNum;
        for (int i3 = 0; i3 < i2; i3++) {
            childDrawableArr[i3].mDrawable.setLayoutDirection(i);
        }
        super.setLayoutDirection(i);
    }

    static class ChildDrawable {
        public Drawable mDrawable;
        public int mId;
        public int mInsetB;
        public int mInsetL;
        public int mInsetR;
        public int mInsetT;

        ChildDrawable() {
        }
    }

    static class LayerState extends Drawable.ConstantState {
        private boolean mAutoMirrored;
        private boolean mCanConstantState;
        int mChangingConfigurations;
        private boolean mCheckedConstantState;
        ChildDrawable[] mChildren;
        int mChildrenChangingConfigurations;
        private boolean mHaveOpacity;
        private boolean mHaveStateful;
        int mNum;
        private int mOpacity;
        private boolean mStateful;

        LayerState(LayerState layerState, LayerDrawable layerDrawable, Resources resources) {
            this.mHaveOpacity = false;
            this.mHaveStateful = false;
            if (layerState != null) {
                ChildDrawable[] childDrawableArr = layerState.mChildren;
                int i = layerState.mNum;
                this.mNum = i;
                this.mChildren = new ChildDrawable[i];
                this.mChangingConfigurations = layerState.mChangingConfigurations;
                this.mChildrenChangingConfigurations = layerState.mChildrenChangingConfigurations;
                for (int i2 = 0; i2 < i; i2++) {
                    ChildDrawable[] childDrawableArr2 = this.mChildren;
                    ChildDrawable childDrawable = new ChildDrawable();
                    childDrawableArr2[i2] = childDrawable;
                    ChildDrawable childDrawable2 = childDrawableArr[i2];
                    if (resources != null) {
                        childDrawable.mDrawable = childDrawable2.mDrawable.getConstantState().newDrawable(resources);
                    } else {
                        childDrawable.mDrawable = childDrawable2.mDrawable.getConstantState().newDrawable();
                    }
                    childDrawable.mDrawable.setCallback(layerDrawable);
                    childDrawable.mDrawable.setLayoutDirection(childDrawable2.mDrawable.getLayoutDirection());
                    childDrawable.mInsetL = childDrawable2.mInsetL;
                    childDrawable.mInsetT = childDrawable2.mInsetT;
                    childDrawable.mInsetR = childDrawable2.mInsetR;
                    childDrawable.mInsetB = childDrawable2.mInsetB;
                    childDrawable.mId = childDrawable2.mId;
                }
                this.mHaveOpacity = layerState.mHaveOpacity;
                this.mOpacity = layerState.mOpacity;
                this.mHaveStateful = layerState.mHaveStateful;
                this.mStateful = layerState.mStateful;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
                this.mAutoMirrored = layerState.mAutoMirrored;
                return;
            }
            this.mNum = 0;
            this.mChildren = null;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new LayerDrawable(this, (Resources) null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new LayerDrawable(this, resources);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public final int getOpacity() {
            if (this.mHaveOpacity) {
                return this.mOpacity;
            }
            int i = this.mNum;
            int opacity = i > 0 ? this.mChildren[0].mDrawable.getOpacity() : -2;
            for (int i2 = 1; i2 < i; i2++) {
                opacity = Drawable.resolveOpacity(opacity, this.mChildren[i2].mDrawable.getOpacity());
            }
            this.mOpacity = opacity;
            this.mHaveOpacity = true;
            return opacity;
        }

        public final boolean isStateful() {
            if (this.mHaveStateful) {
                return this.mStateful;
            }
            int i = this.mNum;
            boolean z = false;
            int i2 = 0;
            while (true) {
                if (i2 >= i) {
                    break;
                }
                if (this.mChildren[i2].mDrawable.isStateful()) {
                    z = true;
                    break;
                }
                i2++;
            }
            this.mStateful = z;
            this.mHaveStateful = true;
            return z;
        }

        public boolean canConstantState() {
            if (!this.mCheckedConstantState && this.mChildren != null) {
                this.mCanConstantState = true;
                int i = this.mNum;
                int i2 = 0;
                while (true) {
                    if (i2 >= i) {
                        break;
                    }
                    if (this.mChildren[i2].mDrawable.getConstantState() == null) {
                        this.mCanConstantState = false;
                        break;
                    }
                    i2++;
                }
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }
    }
}
