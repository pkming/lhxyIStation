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
public class ClipDrawable extends Drawable implements Drawable.Callback {
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    private ClipState mClipState;
    private final Rect mTmpRect;

    ClipDrawable() {
        this(null, null);
    }

    public ClipDrawable(Drawable drawable, int i, int i2) {
        this(null, null);
        this.mClipState.mDrawable = drawable;
        this.mClipState.mGravity = i;
        this.mClipState.mOrientation = i2;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, attributeSet);
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.ClipDrawable);
        int i = typedArrayObtainAttributes.getInt(2, 1);
        int i2 = typedArrayObtainAttributes.getInt(0, 3);
        Drawable drawable = typedArrayObtainAttributes.getDrawable(1);
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
            throw new IllegalArgumentException("No drawable specified for <clip>");
        }
        this.mClipState.mDrawable = drawable;
        this.mClipState.mOrientation = i;
        this.mClipState.mGravity = i2;
        drawable.setCallback(this);
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
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mClipState.mChangingConfigurations | this.mClipState.mDrawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        return this.mClipState.mDrawable.getPadding(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        this.mClipState.mDrawable.setVisible(z, z2);
        return super.setVisible(z, z2);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mClipState.mDrawable.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mClipState.mDrawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mClipState.mDrawable.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mClipState.mDrawable.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mClipState.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        return this.mClipState.mDrawable.setState(iArr);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        this.mClipState.mDrawable.setLevel(i);
        invalidateSelf();
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        this.mClipState.mDrawable.setBounds(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mClipState.mDrawable.getLevel() == 0) {
            return;
        }
        Rect rect = this.mTmpRect;
        Rect bounds = getBounds();
        int level = getLevel();
        int iWidth = bounds.width();
        if ((this.mClipState.mOrientation & 1) != 0) {
            iWidth -= ((iWidth + 0) * (10000 - level)) / 10000;
        }
        int i = iWidth;
        int iHeight = bounds.height();
        if ((this.mClipState.mOrientation & 2) != 0) {
            iHeight -= ((iHeight + 0) * (10000 - level)) / 10000;
        }
        int i2 = iHeight;
        Gravity.apply(this.mClipState.mGravity, i, i2, bounds, rect, getLayoutDirection());
        if (i <= 0 || i2 <= 0) {
            return;
        }
        canvas.save();
        canvas.clipRect(rect);
        this.mClipState.mDrawable.draw(canvas);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mClipState.mDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mClipState.mDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (!this.mClipState.canConstantState()) {
            return null;
        }
        this.mClipState.mChangingConfigurations = getChangingConfigurations();
        return this.mClipState;
    }

    @Override // android.graphics.drawable.Drawable
    public void setLayoutDirection(int i) {
        this.mClipState.mDrawable.setLayoutDirection(i);
        super.setLayoutDirection(i);
    }

    static final class ClipState extends Drawable.ConstantState {
        private boolean mCanConstantState;
        int mChangingConfigurations;
        private boolean mCheckedConstantState;
        Drawable mDrawable;
        int mGravity;
        int mOrientation;

        ClipState(ClipState clipState, ClipDrawable clipDrawable, Resources resources) {
            if (clipState != null) {
                if (resources != null) {
                    this.mDrawable = clipState.mDrawable.getConstantState().newDrawable(resources);
                } else {
                    this.mDrawable = clipState.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(clipDrawable);
                this.mDrawable.setLayoutDirection(clipState.mDrawable.getLayoutDirection());
                this.mOrientation = clipState.mOrientation;
                this.mGravity = clipState.mGravity;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new ClipDrawable(this, (Resources) null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new ClipDrawable(this, resources);
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

    private ClipDrawable(ClipState clipState, Resources resources) {
        this.mTmpRect = new Rect();
        this.mClipState = new ClipState(clipState, this, resources);
    }
}
