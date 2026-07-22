package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewDebug;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class ColorDrawable extends Drawable {
    private boolean mMutated;
    private final Paint mPaint;

    @ViewDebug.ExportedProperty(deepExport = true, prefix = "state_")
    private ColorState mState;

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public ColorDrawable() {
        this((ColorState) null);
    }

    public ColorDrawable(int i) {
        this((ColorState) null);
        setColor(i);
    }

    private ColorDrawable(ColorState colorState) {
        this.mPaint = new Paint();
        this.mState = new ColorState(colorState);
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mState.mChangingConfigurations;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mState = new ColorState(this.mState);
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if ((this.mState.mUseColor >>> 24) != 0) {
            this.mPaint.setColor(this.mState.mUseColor);
            canvas.drawRect(getBounds(), this.mPaint);
        }
    }

    public int getColor() {
        return this.mState.mUseColor;
    }

    public void setColor(int i) {
        if (this.mState.mBaseColor == i && this.mState.mUseColor == i) {
            return;
        }
        invalidateSelf();
        ColorState colorState = this.mState;
        colorState.mUseColor = i;
        colorState.mBaseColor = i;
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mState.mUseColor >>> 24;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        int i2 = ((this.mState.mBaseColor >>> 24) * (i + (i >> 7))) >> 8;
        int i3 = this.mState.mUseColor;
        ColorState colorState = this.mState;
        colorState.mUseColor = (i2 << 24) | ((colorState.mBaseColor << 8) >>> 8);
        if (i3 != this.mState.mUseColor) {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        int i = this.mState.mUseColor >>> 24;
        if (i != 0) {
            return i != 255 ? -3 : -1;
        }
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, attributeSet);
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.ColorDrawable);
        int color = typedArrayObtainAttributes.getColor(0, this.mState.mBaseColor);
        ColorState colorState = this.mState;
        colorState.mUseColor = color;
        colorState.mBaseColor = color;
        typedArrayObtainAttributes.recycle();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.mState.mChangingConfigurations = getChangingConfigurations();
        return this.mState;
    }

    static final class ColorState extends Drawable.ConstantState {
        int mBaseColor;
        int mChangingConfigurations;

        @ViewDebug.ExportedProperty
        int mUseColor;

        ColorState(ColorState colorState) {
            if (colorState != null) {
                this.mBaseColor = colorState.mBaseColor;
                this.mUseColor = colorState.mUseColor;
                this.mChangingConfigurations = colorState.mChangingConfigurations;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new ColorDrawable(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new ColorDrawable(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }
}
