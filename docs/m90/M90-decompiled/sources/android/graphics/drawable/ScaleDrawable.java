package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class ScaleDrawable extends Drawable implements Drawable.Callback {
    private boolean mMutated;
    private ScaleState mScaleState;
    private final Rect mTmpRect;

    ScaleDrawable() {
        this(null, null);
    }

    public ScaleDrawable(Drawable drawable, int i, float f, float f2) {
        this(null, null);
        this.mScaleState.mDrawable = drawable;
        this.mScaleState.mGravity = i;
        this.mScaleState.mScaleWidth = f;
        this.mScaleState.mScaleHeight = f2;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    public Drawable getDrawable() {
        return this.mScaleState.mDrawable;
    }

    private static float getPercent(TypedArray typedArray, int i) {
        String string = typedArray.getString(i);
        if (string == null || !string.endsWith("%")) {
            return -1.0f;
        }
        return Float.parseFloat(string.substring(0, string.length() - 1)) / 100.0f;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, attributeSet);
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.ScaleDrawable);
        float percent = getPercent(typedArrayObtainAttributes, 1);
        float percent2 = getPercent(typedArrayObtainAttributes, 2);
        int i = typedArrayObtainAttributes.getInt(3, 3);
        boolean z = typedArrayObtainAttributes.getBoolean(4, false);
        Drawable drawable = typedArrayObtainAttributes.getDrawable(0);
        typedArrayObtainAttributes.recycle();
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1 || (next == 3 && xmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next == 2) {
                drawable = Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet);
            }
        }
        if (drawable == null) {
            throw new IllegalArgumentException("No drawable specified for <scale>");
        }
        this.mScaleState.mDrawable = drawable;
        this.mScaleState.mScaleWidth = percent;
        this.mScaleState.mScaleHeight = percent2;
        this.mScaleState.mGravity = i;
        this.mScaleState.mUseIntrinsicSizeAsMin = z;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (getCallback() != null) {
            getCallback().invalidateDrawable(this);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        if (getCallback() != null) {
            getCallback().scheduleDrawable(this, runnable, j);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        if (getCallback() != null) {
            getCallback().unscheduleDrawable(this, runnable);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mScaleState.mDrawable.getLevel() != 0) {
            this.mScaleState.mDrawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mScaleState.mChangingConfigurations | this.mScaleState.mDrawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        return this.mScaleState.mDrawable.getPadding(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        this.mScaleState.mDrawable.setVisible(z, z2);
        return super.setVisible(z, z2);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mScaleState.mDrawable.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mScaleState.mDrawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mScaleState.mDrawable.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mScaleState.mDrawable.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mScaleState.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean state = this.mScaleState.mDrawable.setState(iArr);
        onBoundsChange(getBounds());
        return state;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        this.mScaleState.mDrawable.setLevel(i);
        onBoundsChange(getBounds());
        invalidateSelf();
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        Rect rect2 = this.mTmpRect;
        boolean z = this.mScaleState.mUseIntrinsicSizeAsMin;
        int level = getLevel();
        int iWidth = rect.width();
        if (this.mScaleState.mScaleWidth > 0.0f) {
            iWidth -= (int) ((((iWidth - (z ? this.mScaleState.mDrawable.getIntrinsicWidth() : 0)) * (10000 - level)) * this.mScaleState.mScaleWidth) / 10000.0f);
        }
        int i = iWidth;
        int iHeight = rect.height();
        if (this.mScaleState.mScaleHeight > 0.0f) {
            iHeight -= (int) ((((iHeight - (z ? this.mScaleState.mDrawable.getIntrinsicHeight() : 0)) * (10000 - level)) * this.mScaleState.mScaleHeight) / 10000.0f);
        }
        int i2 = iHeight;
        Gravity.apply(this.mScaleState.mGravity, i, i2, rect, rect2, getLayoutDirection());
        if (i <= 0 || i2 <= 0) {
            return;
        }
        this.mScaleState.mDrawable.setBounds(rect2.left, rect2.top, rect2.right, rect2.bottom);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mScaleState.mDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mScaleState.mDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (!this.mScaleState.canConstantState()) {
            return null;
        }
        this.mScaleState.mChangingConfigurations = getChangingConfigurations();
        return this.mScaleState;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mScaleState.mDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    static final class ScaleState extends Drawable.ConstantState {
        private boolean mCanConstantState;
        int mChangingConfigurations;
        private boolean mCheckedConstantState;
        Drawable mDrawable;
        int mGravity;
        float mScaleHeight;
        float mScaleWidth;
        boolean mUseIntrinsicSizeAsMin;

        ScaleState(ScaleState scaleState, ScaleDrawable scaleDrawable, Resources resources) {
            if (scaleState != null) {
                if (resources != null) {
                    this.mDrawable = scaleState.mDrawable.getConstantState().newDrawable(resources);
                } else {
                    this.mDrawable = scaleState.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(scaleDrawable);
                this.mDrawable.setLayoutDirection(scaleState.mDrawable.getLayoutDirection());
                this.mScaleWidth = scaleState.mScaleWidth;
                this.mScaleHeight = scaleState.mScaleHeight;
                this.mGravity = scaleState.mGravity;
                this.mUseIntrinsicSizeAsMin = scaleState.mUseIntrinsicSizeAsMin;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new ScaleDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new ScaleDrawable(this, resources);
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

    private ScaleDrawable(ScaleState scaleState, Resources resources) {
        this.mTmpRect = new Rect();
        this.mScaleState = new ScaleState(scaleState, this, resources);
    }
}
