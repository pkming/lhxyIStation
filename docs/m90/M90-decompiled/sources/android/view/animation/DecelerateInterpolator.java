package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
public class DecelerateInterpolator implements Interpolator {
    private float mFactor;

    public DecelerateInterpolator() {
        this.mFactor = 1.0f;
    }

    public DecelerateInterpolator(float f) {
        this.mFactor = 1.0f;
        this.mFactor = f;
    }

    public DecelerateInterpolator(Context context, AttributeSet attributeSet) {
        this.mFactor = 1.0f;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.DecelerateInterpolator);
        this.mFactor = typedArrayObtainStyledAttributes.getFloat(0, 1.0f);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        if (this.mFactor != 1.0f) {
            return (float) (1.0d - Math.pow(1.0f - f, r0 * 2.0f));
        }
        float f2 = 1.0f - f;
        return 1.0f - (f2 * f2);
    }
}
