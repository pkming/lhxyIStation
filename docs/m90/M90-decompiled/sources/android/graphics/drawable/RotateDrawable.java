package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class RotateDrawable extends Drawable implements Drawable.Callback {
    private static final float MAX_LEVEL = 10000.0f;
    private boolean mMutated;
    private RotateState mState;

    public RotateDrawable() {
        this(null, null);
    }

    private RotateDrawable(RotateState rotateState, Resources resources) {
        this.mState = new RotateState(rotateState, this, resources);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int iSave = canvas.save();
        Rect bounds = this.mState.mDrawable.getBounds();
        int i = bounds.right - bounds.left;
        int i2 = bounds.bottom - bounds.top;
        RotateState rotateState = this.mState;
        canvas.rotate(rotateState.mCurrentDegrees, (rotateState.mPivotXRel ? i * rotateState.mPivotX : rotateState.mPivotX) + bounds.left, (rotateState.mPivotYRel ? i2 * rotateState.mPivotY : rotateState.mPivotY) + bounds.top);
        rotateState.mDrawable.draw(canvas);
        canvas.restoreToCount(iSave);
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
    public boolean setVisible(boolean z, boolean z2) {
        this.mState.mDrawable.setVisible(z, z2);
        return super.setVisible(z, z2);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mState.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean state = this.mState.mDrawable.setState(iArr);
        onBoundsChange(getBounds());
        return state;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        this.mState.mDrawable.setLevel(i);
        onBoundsChange(getBounds());
        RotateState rotateState = this.mState;
        rotateState.mCurrentDegrees = rotateState.mFromDegrees + ((this.mState.mToDegrees - this.mState.mFromDegrees) * (i / 10000.0f));
        invalidateSelf();
        return true;
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
        boolean z;
        float fraction;
        boolean z2;
        float fraction2;
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.RotateDrawable);
        super.inflateWithAttributes(resources, xmlPullParser, typedArrayObtainAttributes, 0);
        TypedValue typedValuePeekValue = typedArrayObtainAttributes.peekValue(4);
        int i = 1;
        if (typedValuePeekValue == null) {
            fraction = 0.5f;
            z = true;
        } else {
            z = typedValuePeekValue.type == 6;
            fraction = z ? typedValuePeekValue.getFraction(1.0f, 1.0f) : typedValuePeekValue.getFloat();
        }
        TypedValue typedValuePeekValue2 = typedArrayObtainAttributes.peekValue(5);
        if (typedValuePeekValue2 == null) {
            fraction2 = 0.5f;
            z2 = true;
        } else {
            z2 = typedValuePeekValue2.type == 6;
            fraction2 = z2 ? typedValuePeekValue2.getFraction(1.0f, 1.0f) : typedValuePeekValue2.getFloat();
        }
        float f = typedArrayObtainAttributes.getFloat(2, 0.0f);
        float f2 = typedArrayObtainAttributes.getFloat(3, 360.0f);
        int resourceId = typedArrayObtainAttributes.getResourceId(1, 0);
        Drawable drawable = resourceId > 0 ? resources.getDrawable(resourceId) : null;
        typedArrayObtainAttributes.recycle();
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == i || (next == 3 && xmlPullParser.getDepth() <= depth)) {
                break;
            }
            if (next != 2) {
                i = 1;
            } else {
                drawable = Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet);
                if (drawable == null) {
                    Log.w("drawable", "Bad element under <rotate>: " + xmlPullParser.getName());
                }
                i = 1;
            }
        }
        if (drawable == null) {
            Log.w("drawable", "No drawable specified for <rotate>");
        }
        this.mState.mDrawable = drawable;
        this.mState.mPivotXRel = z;
        this.mState.mPivotX = fraction;
        this.mState.mPivotYRel = z2;
        this.mState.mPivotY = fraction2;
        RotateState rotateState = this.mState;
        rotateState.mCurrentDegrees = f;
        rotateState.mFromDegrees = f;
        this.mState.mToDegrees = f2;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mState.mDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    static final class RotateState extends Drawable.ConstantState {
        private boolean mCanConstantState;
        int mChangingConfigurations;
        private boolean mCheckedConstantState;
        float mCurrentDegrees;
        Drawable mDrawable;
        float mFromDegrees;
        float mPivotX;
        boolean mPivotXRel;
        float mPivotY;
        boolean mPivotYRel;
        float mToDegrees;

        public RotateState(RotateState rotateState, RotateDrawable rotateDrawable, Resources resources) {
            if (rotateState != null) {
                if (resources != null) {
                    this.mDrawable = rotateState.mDrawable.getConstantState().newDrawable(resources);
                } else {
                    this.mDrawable = rotateState.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(rotateDrawable);
                this.mDrawable.setLayoutDirection(rotateState.mDrawable.getLayoutDirection());
                this.mPivotXRel = rotateState.mPivotXRel;
                this.mPivotX = rotateState.mPivotX;
                this.mPivotYRel = rotateState.mPivotYRel;
                this.mPivotY = rotateState.mPivotY;
                float f = rotateState.mFromDegrees;
                this.mCurrentDegrees = f;
                this.mFromDegrees = f;
                this.mToDegrees = rotateState.mToDegrees;
                this.mCheckedConstantState = true;
                this.mCanConstantState = true;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new RotateDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new RotateDrawable(this, resources);
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
