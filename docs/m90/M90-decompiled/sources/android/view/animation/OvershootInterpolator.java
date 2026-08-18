package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
public class OvershootInterpolator implements Interpolator {
    private final float mTension;

    public OvershootInterpolator() {
        this.mTension = 2.0f;
    }

    public OvershootInterpolator(float f) {
        this.mTension = f;
    }

    public OvershootInterpolator(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.OvershootInterpolator);
        this.mTension = typedArrayObtainStyledAttributes.getFloat(0, 2.0f);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float f2 = f - 1.0f;
        float f3 = this.mTension;
        return (f2 * f2 * (((f3 + 1.0f) * f2) + f3)) + 1.0f;
    }
}
