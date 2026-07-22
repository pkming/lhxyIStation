package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class AnimatedRotateDrawable extends Drawable implements Drawable.Callback, Runnable, Animatable {
    private float mCurrentDegrees;
    private float mIncrement;
    private boolean mMutated;
    private boolean mRunning;
    private AnimatedRotateState mState;

    public AnimatedRotateDrawable() {
        this(null, null);
    }

    private AnimatedRotateDrawable(AnimatedRotateState animatedRotateState, Resources resources) {
        this.mState = new AnimatedRotateState(animatedRotateState, this, resources);
        init();
    }

    private void init() {
        AnimatedRotateState animatedRotateState = this.mState;
        this.mIncrement = 360.0f / animatedRotateState.mFramesCount;
        Drawable drawable = animatedRotateState.mDrawable;
        if (drawable != null) {
            drawable.setFilterBitmap(true);
            if (drawable instanceof BitmapDrawable) {
                ((BitmapDrawable) drawable).setAntiAlias(true);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int iSave = canvas.save();
        AnimatedRotateState animatedRotateState = this.mState;
        Drawable drawable = animatedRotateState.mDrawable;
        Rect bounds = drawable.getBounds();
        canvas.rotate(this.mCurrentDegrees, (animatedRotateState.mPivotXRel ? (bounds.right - bounds.left) * animatedRotateState.mPivotX : animatedRotateState.mPivotX) + bounds.left, (animatedRotateState.mPivotYRel ? (bounds.bottom - bounds.top) * animatedRotateState.mPivotY : animatedRotateState.mPivotY) + bounds.top);
        drawable.draw(canvas);
        canvas.restoreToCount(iSave);
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (this.mRunning) {
            return;
        }
        this.mRunning = true;
        nextFrame();
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.mRunning = false;
        unscheduleSelf(this);
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.mRunning;
    }

    private void nextFrame() {
        unscheduleSelf(this);
        scheduleSelf(this, SystemClock.uptimeMillis() + ((long) this.mState.mFrameDuration));
    }

    @Override // java.lang.Runnable
    public void run() {
        float f = this.mCurrentDegrees;
        float f2 = this.mIncrement;
        float f3 = f + f2;
        this.mCurrentDegrees = f3;
        if (f3 > 360.0f - f2) {
            this.mCurrentDegrees = 0.0f;
        }
        invalidateSelf();
        nextFrame();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        this.mState.mDrawable.setVisible(z, z2);
        boolean visible = super.setVisible(z, z2);
        if (!z) {
            unscheduleSelf(this);
        } else if (visible || z2) {
            this.mCurrentDegrees = 0.0f;
            nextFrame();
        }
        return visible;
    }

    public Drawable getDrawable() {
        return this.mState.mDrawable;
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mState.mChangingConfigurations | this.mState.mDrawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mState.mDrawable.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mState.mDrawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mState.mDrawable.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mState.mDrawable.getOpacity();
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
    public boolean getPadding(Rect rect) {
        return this.mState.mDrawable.getPadding(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mState.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        this.mState.mDrawable.setBounds(rect.left, rect.top, rect.right, rect.bottom);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mState.mDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mState.mDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (!this.mState.canConstantState()) {
            return null;
        }
        this.mState.mChangingConfigurations = getChangingConfigurations();
        return this.mState;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AnimatedRotateDrawable);
        super.inflateWithAttributes(resources, xmlPullParser, typedArrayObtainAttributes, 0);
        TypedValue typedValuePeekValue = typedArrayObtainAttributes.peekValue(2);
        boolean z = typedValuePeekValue.type == 6;
        float fraction = z ? typedValuePeekValue.getFraction(1.0f, 1.0f) : typedValuePeekValue.getFloat();
        TypedValue typedValuePeekValue2 = typedArrayObtainAttributes.peekValue(3);
        boolean z2 = typedValuePeekValue2.type == 6;
        float fraction2 = z2 ? typedValuePeekValue2.getFraction(1.0f, 1.0f) : typedValuePeekValue2.getFloat();
        setFramesCount(typedArrayObtainAttributes.getInt(5, 12));
        setFramesDuration(typedArrayObtainAttributes.getInt(4, 150));
        int resourceId = typedArrayObtainAttributes.getResourceId(1, 0);
        Drawable drawable = resourceId > 0 ? resources.getDrawable(resourceId) : null;
        typedArrayObtainAttributes.recycle();
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1 || (next == 3 && xmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next == 2 && (drawable = Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet)) == null) {
                Log.w("drawable", "Bad element under <animated-rotate>: " + xmlPullParser.getName());
            }
        }
        if (drawable == null) {
            Log.w("drawable", "No drawable specified for <animated-rotate>");
        }
        AnimatedRotateState animatedRotateState = this.mState;
        animatedRotateState.mDrawable = drawable;
        animatedRotateState.mPivotXRel = z;
        animatedRotateState.mPivotX = fraction;
        animatedRotateState.mPivotYRel = z2;
        animatedRotateState.mPivotY = fraction2;
        init();
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    public void setFramesCount(int i) {
        this.mState.mFramesCount = i;
        this.mIncrement = 360.0f / this.mState.mFramesCount;
    }

    public void setFramesDuration(int i) {
        this.mState.mFrameDuration = i;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mState.mDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    static final class AnimatedRotateState extends Drawable.ConstantState {
        private boolean mCanConstantState;
        int mChangingConfigurations;
        private boolean mCheckedConstantState;
        Drawable mDrawable;
        int mFrameDuration;
        int mFramesCount;
        float mPivotX;
        boolean mPivotXRel;
        float mPivotY;
        boolean mPivotYRel;

        public AnimatedRotateState(AnimatedRotateState animatedRotateState, AnimatedRotateDrawable animatedRotateDrawable, Resources resources) {
            if (animatedRotateState != null) {
                if (resources != null) {
                    this.mDrawable = animatedRotateState.mDrawable.getConstantState().newDrawable(resources);
                } else {
                    this.mDrawable = animatedRotateState.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(animatedRotateDrawable);
                this.mDrawable.setLayoutDirection(animatedRotateState.mDrawable.getLayoutDirection());
                this.mPivotXRel = animatedRotateState.mPivotXRel;
                this.mPivotX = animatedRotateState.mPivotX;
                this.mPivotYRel = animatedRotateState.mPivotYRel;
                this.mPivotY = animatedRotateState.mPivotY;
                this.mFramesCount = animatedRotateState.mFramesCount;
                this.mFrameDuration = animatedRotateState.mFrameDuration;
                this.mCheckedConstantState = true;
                this.mCanConstantState = true;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new AnimatedRotateDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new AnimatedRotateDrawable(this, resources);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public boolean canConstantState() {
            if (!this.mCheckedConstantState) {
                this.mCanConstantState = this.mDrawable.getConstantState() != null;
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }
    }
}
