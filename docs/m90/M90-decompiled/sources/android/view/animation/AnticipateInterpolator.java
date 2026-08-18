package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
public class AnticipateInterpolator implements Interpolator {
    private final float mTension;

    public AnticipateInterpolator() {
        this.mTension = 2.0f;
    }

    public AnticipateInterpolator(float f) {
        this.mTension = f;
    }

    public AnticipateInterpolator(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AnticipateInterpolator);
        this.mTension = typedArrayObtainStyledAttributes.getFloat(0, 2.0f);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float f2 = this.mTension;
        return f * f * (((1.0f + f2) * f) - f2);
    }
}
