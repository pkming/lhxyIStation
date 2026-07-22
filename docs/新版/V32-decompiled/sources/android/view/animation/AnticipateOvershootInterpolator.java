package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
public class AnticipateOvershootInterpolator implements Interpolator {
    private final float mTension;

    private static float a(float f, float f2) {
        return f * f * (((1.0f + f2) * f) - f2);
    }

    private static float o(float f, float f2) {
        return f * f * (((1.0f + f2) * f) + f2);
    }

    public AnticipateOvershootInterpolator() {
        this.mTension = 3.0f;
    }

    public AnticipateOvershootInterpolator(float f) {
        this.mTension = f * 1.5f;
    }

    public AnticipateOvershootInterpolator(float f, float f2) {
        this.mTension = f * f2;
    }

    public AnticipateOvershootInterpolator(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AnticipateOvershootInterpolator);
        this.mTension = typedArrayObtainStyledAttributes.getFloat(0, 2.0f) * typedArrayObtainStyledAttributes.getFloat(1, 1.5f);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float fO;
        if (f < 0.5f) {
            fO = a(f * 2.0f, this.mTension);
        } else {
            fO = o((f * 2.0f) - 2.0f, this.mTension) + 2.0f;
        }
        return fO * 0.5f;
    }
}
