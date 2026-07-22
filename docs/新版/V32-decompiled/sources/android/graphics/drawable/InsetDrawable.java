package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class InsetDrawable extends Drawable implements Drawable.Callback {
    private InsetState mInsetState;
    private boolean mMutated;
    private final Rect mTmpRect;

    InsetDrawable() {
        this((InsetState) null, (Resources) null);
    }

    public InsetDrawable(Drawable drawable, int i) {
        this(drawable, i, i, i, i);
    }

    public InsetDrawable(Drawable drawable, int i, int i2, int i3, int i4) {
        this((InsetState) null, (Resources) null);
        this.mInsetState.mDrawable = drawable;
        this.mInsetState.mInsetLeft = i;
        this.mInsetState.mInsetTop = i2;
        this.mInsetState.mInsetRight = i3;
        this.mInsetState.mInsetBottom = i4;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int next;
        Drawable drawableCreateFromXmlInner;
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.InsetDrawable);
        super.inflateWithAttributes(resources, xmlPullParser, typedArrayObtainAttributes, 0);
        int resourceId = typedArrayObtainAttributes.getResourceId(1, 0);
        int dimensionPixelOffset = typedArrayObtainAttributes.getDimensionPixelOffset(2, 0);
        int dimensionPixelOffset2 = typedArrayObtainAttributes.getDimensionPixelOffset(4, 0);
        int dimensionPixelOffset3 = typedArrayObtainAttributes.getDimensionPixelOffset(3, 0);
        int dimensionPixelOffset4 = typedArrayObtainAttributes.getDimensionPixelOffset(5, 0);
        typedArrayObtainAttributes.recycle();
        if (resourceId != 0) {
            drawableCreateFromXmlInner = resources.getDrawable(resourceId);
        } else {
            do {
                next = xmlPullParser.next();
            } while (next == 4);
            if (next != 2) {
                throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": <inset> tag requires a 'drawable' attribute or child tag defining a drawable");
            }
            drawableCreateFromXmlInner = Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet);
        }
        if (drawableCreateFromXmlInner == null) {
            Log.w("drawable", "No drawable specified for <inset>");
        }
        this.mInsetState.mDrawable = drawableCreateFromXmlInner;
        this.mInsetState.mInsetLeft = dimensionPixelOffset;
        this.mInsetState.mInsetRight = dimensionPixelOffset3;
        this.mInsetState.mInsetTop = dimensionPixelOffset2;
        this.mInsetState.mInsetBottom = dimensionPixelOffset4;
        if (drawableCreateFromXmlInner != null) {
            drawableCreateFromXmlInner.setCallback(this);
        }
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
        this.mInsetState.mDrawable.draw(canvas);
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mInsetState.mChangingConfigurations | this.mInsetState.mDrawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        boolean padding = this.mInsetState.mDrawable.getPadding(rect);
        rect.left += this.mInsetState.mInsetLeft;
        rect.right += this.mInsetState.mInsetRight;
        rect.top += this.mInsetState.mInsetTop;
        rect.bottom += this.mInsetState.mInsetBottom;
        return padding || (((this.mInsetState.mInsetLeft | this.mInsetState.mInsetRight) | this.mInsetState.mInsetTop) | this.mInsetState.mInsetBottom) != 0;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        this.mInsetState.mDrawable.setVisible(z, z2);
        return super.setVisible(z, z2);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mInsetState.mDrawable.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mInsetState.mDrawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mInsetState.mDrawable.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public void setLayoutDirection(int i) {
        this.mInsetState.mDrawable.setLayoutDirection(i);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mInsetState.mDrawable.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mInsetState.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean state = this.mInsetState.mDrawable.setState(iArr);
        onBoundsChange(getBounds());
        return state;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        Rect rect2 = this.mTmpRect;
        rect2.set(rect);
        rect2.left += this.mInsetState.mInsetLeft;
        rect2.top += this.mInsetState.mInsetTop;
        rect2.right -= this.mInsetState.mInsetRight;
        rect2.bottom -= this.mInsetState.mInsetBottom;
        this.mInsetState.mDrawable.setBounds(rect2.left, rect2.top, rect2.right, rect2.bottom);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mInsetState.mDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mInsetState.mDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (!this.mInsetState.canConstantState()) {
            return null;
        }
        this.mInsetState.mChangingConfigurations = getChangingConfigurations();
        return this.mInsetState;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mInsetState.mDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    public Drawable getDrawable() {
        return this.mInsetState.mDrawable;
    }

    static final class InsetState extends Drawable.ConstantState {
        boolean mCanConstantState;
        int mChangingConfigurations;
        boolean mCheckedConstantState;
        Drawable mDrawable;
        int mInsetBottom;
        int mInsetLeft;
        int mInsetRight;
        int mInsetTop;

        InsetState(InsetState insetState, InsetDrawable insetDrawable, Resources resources) {
            if (insetState != null) {
                if (resources != null) {
                    this.mDrawable = insetState.mDrawable.getConstantState().newDrawable(resources);
                } else {
                    this.mDrawable = insetState.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(insetDrawable);
                this.mDrawable.setLayoutDirection(insetState.mDrawable.getLayoutDirection());
                this.mInsetLeft = insetState.mInsetLeft;
                this.mInsetTop = insetState.mInsetTop;
                this.mInsetRight = insetState.mInsetRight;
                this.mInsetBottom = insetState.mInsetBottom;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new InsetDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new InsetDrawable(this, resources);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        boolean canConstantState() {
            if (!this.mCheckedConstantState) {
                this.mCanConstantState = this.mDrawable.getConstantState() != null;
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }
    }

    private InsetDrawable(InsetState insetState, Resources resources) {
        this.mTmpRect = new Rect();
        this.mInsetState = new InsetState(insetState, this, resources);
    }
}
