package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes.dex */
public class EdgeEffect {
    private static final float EPSILON = 0.001f;
    private static final float HELD_EDGE_SCALE_Y = 0.5f;
    private static final float MAX_ALPHA = 1.0f;
    private static final float MAX_GLOW_HEIGHT = 4.0f;
    private static final int MAX_VELOCITY = 10000;
    private static final int MIN_VELOCITY = 100;
    private static final int MIN_WIDTH = 300;
    private static final int PULL_DECAY_TIME = 1000;
    private static final float PULL_DISTANCE_ALPHA_GLOW_FACTOR = 1.1f;
    private static final int PULL_DISTANCE_EDGE_FACTOR = 7;
    private static final int PULL_DISTANCE_GLOW_FACTOR = 7;
    private static final float PULL_EDGE_BEGIN = 0.6f;
    private static final float PULL_GLOW_BEGIN = 1.0f;
    private static final int PULL_TIME = 167;
    private static final int RECEDE_TIME = 1000;
    private static final int STATE_ABSORB = 2;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PULL = 1;
    private static final int STATE_PULL_DECAY = 4;
    private static final int STATE_RECEDE = 3;
    private static final String TAG = "EdgeEffect";
    private static final int VELOCITY_EDGE_FACTOR = 8;
    private static final int VELOCITY_GLOW_FACTOR = 12;
    private float mDuration;
    private final Drawable mEdge;
    private float mEdgeAlpha;
    private float mEdgeAlphaFinish;
    private float mEdgeAlphaStart;
    private final int mEdgeHeight;
    private float mEdgeScaleY;
    private float mEdgeScaleYFinish;
    private float mEdgeScaleYStart;
    private final Drawable mGlow;
    private float mGlowAlpha;
    private float mGlowAlphaFinish;
    private float mGlowAlphaStart;
    private final int mGlowHeight;
    private float mGlowScaleY;
    private float mGlowScaleYFinish;
    private float mGlowScaleYStart;
    private final int mGlowWidth;
    private int mHeight;
    private final Interpolator mInterpolator;
    private final int mMaxEffectHeight;
    private final int mMinWidth;
    private float mPullDistance;
    private long mStartTime;
    private int mWidth;
    private int mX;
    private int mY;
    private int mState = 0;
    private final Rect mBounds = new Rect();

    public EdgeEffect(Context context) {
        Resources resources = context.getResources();
        Drawable drawable = resources.getDrawable(17302655);
        this.mEdge = drawable;
        Drawable drawable2 = resources.getDrawable(17302656);
        this.mGlow = drawable2;
        this.mEdgeHeight = drawable.getIntrinsicHeight();
        int intrinsicHeight = drawable2.getIntrinsicHeight();
        this.mGlowHeight = intrinsicHeight;
        int intrinsicWidth = drawable2.getIntrinsicWidth();
        this.mGlowWidth = intrinsicWidth;
        this.mMaxEffectHeight = (int) (Math.min((((intrinsicHeight * MAX_GLOW_HEIGHT) * intrinsicHeight) / intrinsicWidth) * 0.6f, intrinsicHeight * MAX_GLOW_HEIGHT) + 0.5f);
        this.mMinWidth = (int) ((resources.getDisplayMetrics().density * 300.0f) + 0.5f);
        this.mInterpolator = new DecelerateInterpolator();
    }

    public void setSize(int i, int i2) {
        this.mWidth = i;
        this.mHeight = i2;
    }

    void setPosition(int i, int i2) {
        this.mX = i;
        this.mY = i2;
    }

    public boolean isFinished() {
        return this.mState == 0;
    }

    public void finish() {
        this.mState = 0;
    }

    public void onPull(float f) {
        long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        int i = this.mState;
        if (i != 4 || jCurrentAnimationTimeMillis - this.mStartTime >= this.mDuration) {
            if (i != 1) {
                this.mGlowScaleY = 1.0f;
            }
            this.mState = 1;
            this.mStartTime = jCurrentAnimationTimeMillis;
            this.mDuration = 167.0f;
            float f2 = this.mPullDistance + f;
            this.mPullDistance = f2;
            float fAbs = Math.abs(f2);
            float fMax = Math.max(0.6f, Math.min(fAbs, 1.0f));
            this.mEdgeAlphaStart = fMax;
            this.mEdgeAlpha = fMax;
            float fMax2 = Math.max(0.5f, Math.min(fAbs * 7.0f, 1.0f));
            this.mEdgeScaleYStart = fMax2;
            this.mEdgeScaleY = fMax2;
            float fMin = Math.min(1.0f, this.mGlowAlpha + (Math.abs(f) * PULL_DISTANCE_ALPHA_GLOW_FACTOR));
            this.mGlowAlphaStart = fMin;
            this.mGlowAlpha = fMin;
            float fAbs2 = Math.abs(f);
            if (f > 0.0f && this.mPullDistance < 0.0f) {
                fAbs2 = -fAbs2;
            }
            if (this.mPullDistance == 0.0f) {
                this.mGlowScaleY = 0.0f;
            }
            float fMin2 = Math.min(MAX_GLOW_HEIGHT, Math.max(0.0f, this.mGlowScaleY + (fAbs2 * 7.0f)));
            this.mGlowScaleYStart = fMin2;
            this.mGlowScaleY = fMin2;
            this.mEdgeAlphaFinish = this.mEdgeAlpha;
            this.mEdgeScaleYFinish = this.mEdgeScaleY;
            this.mGlowAlphaFinish = this.mGlowAlpha;
            this.mGlowScaleYFinish = fMin2;
        }
    }

    public void onRelease() {
        this.mPullDistance = 0.0f;
        int i = this.mState;
        if (i == 1 || i == 4) {
            this.mState = 3;
            this.mEdgeAlphaStart = this.mEdgeAlpha;
            this.mEdgeScaleYStart = this.mEdgeScaleY;
            this.mGlowAlphaStart = this.mGlowAlpha;
            this.mGlowScaleYStart = this.mGlowScaleY;
            this.mEdgeAlphaFinish = 0.0f;
            this.mEdgeScaleYFinish = 0.0f;
            this.mGlowAlphaFinish = 0.0f;
            this.mGlowScaleYFinish = 0.0f;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = 1000.0f;
        }
    }

    public void onAbsorb(int i) {
        this.mState = 2;
        int iMin = Math.min(Math.max(100, Math.abs(i)), 10000);
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mDuration = (iMin * 0.02f) + 0.15f;
        this.mEdgeAlphaStart = 0.0f;
        this.mEdgeScaleYStart = 0.0f;
        this.mEdgeScaleY = 0.0f;
        this.mGlowAlphaStart = 0.3f;
        this.mGlowScaleYStart = 0.0f;
        this.mEdgeAlphaFinish = Math.max(0, Math.min(r0, 1));
        this.mEdgeScaleYFinish = Math.max(0.5f, Math.min(iMin * 8, 1.0f));
        this.mGlowScaleYFinish = Math.min(((iMin / 100) * iMin * 1.5E-4f) + 0.025f, 1.75f);
        this.mGlowAlphaFinish = Math.max(this.mGlowAlphaStart, Math.min(iMin * 12 * 1.0E-5f, 1.0f));
    }

    public boolean draw(Canvas canvas) {
        update();
        this.mGlow.setAlpha((int) (Math.max(0.0f, Math.min(this.mGlowAlpha, 1.0f)) * 255.0f));
        int i = this.mGlowHeight;
        int iMin = (int) Math.min((((i * this.mGlowScaleY) * i) / this.mGlowWidth) * 0.6f, i * MAX_GLOW_HEIGHT);
        int i2 = this.mWidth;
        int i3 = this.mMinWidth;
        if (i2 < i3) {
            int i4 = (i2 - i3) / 2;
            this.mGlow.setBounds(i4, 0, i2 - i4, iMin);
        } else {
            this.mGlow.setBounds(0, 0, i2, iMin);
        }
        this.mGlow.draw(canvas);
        this.mEdge.setAlpha((int) (Math.max(0.0f, Math.min(this.mEdgeAlpha, 1.0f)) * 255.0f));
        int i5 = (int) (this.mEdgeHeight * this.mEdgeScaleY);
        int i6 = this.mWidth;
        int i7 = this.mMinWidth;
        if (i6 < i7) {
            int i8 = (i6 - i7) / 2;
            this.mEdge.setBounds(i8, 0, i6 - i8, i5);
        } else {
            this.mEdge.setBounds(0, 0, i6, i5);
        }
        this.mEdge.draw(canvas);
        if (this.mState == 3 && iMin == 0 && i5 == 0) {
            this.mState = 0;
        }
        return this.mState != 0;
    }

    public Rect getBounds(boolean z) {
        this.mBounds.set(0, 0, this.mWidth, this.mMaxEffectHeight);
        this.mBounds.offset(this.mX, this.mY - (z ? this.mMaxEffectHeight : 0));
        return this.mBounds;
    }

    private void update() {
        float fMin = Math.min((AnimationUtils.currentAnimationTimeMillis() - this.mStartTime) / this.mDuration, 1.0f);
        float interpolation = this.mInterpolator.getInterpolation(fMin);
        float f = this.mEdgeAlphaStart;
        this.mEdgeAlpha = f + ((this.mEdgeAlphaFinish - f) * interpolation);
        float f2 = this.mEdgeScaleYStart;
        float f3 = this.mEdgeScaleYFinish;
        this.mEdgeScaleY = ((f3 - f2) * interpolation) + f2;
        float f4 = this.mGlowAlphaStart;
        this.mGlowAlpha = f4 + ((this.mGlowAlphaFinish - f4) * interpolation);
        float f5 = this.mGlowScaleYStart;
        float f6 = this.mGlowScaleYFinish;
        this.mGlowScaleY = f5 + ((f6 - f5) * interpolation);
        if (fMin >= 0.999f) {
            int i = this.mState;
            if (i == 1) {
                this.mState = 4;
                this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                this.mDuration = 1000.0f;
                this.mEdgeAlphaStart = this.mEdgeAlpha;
                this.mEdgeScaleYStart = this.mEdgeScaleY;
                this.mGlowAlphaStart = this.mGlowAlpha;
                this.mGlowScaleYStart = this.mGlowScaleY;
                this.mEdgeAlphaFinish = 0.0f;
                this.mEdgeScaleYFinish = 0.0f;
                this.mGlowAlphaFinish = 0.0f;
                this.mGlowScaleYFinish = 0.0f;
                return;
            }
            if (i != 2) {
                if (i == 3) {
                    this.mState = 0;
                    return;
                } else {
                    if (i != 4) {
                        return;
                    }
                    this.mEdgeScaleY = f2 + ((f3 - f2) * interpolation * (f6 != 0.0f ? 1.0f / (f6 * f6) : Float.MAX_VALUE));
                    this.mState = 3;
                    return;
                }
            }
            this.mState = 3;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = 1000.0f;
            this.mEdgeAlphaStart = this.mEdgeAlpha;
            this.mEdgeScaleYStart = this.mEdgeScaleY;
            this.mGlowAlphaStart = this.mGlowAlpha;
            this.mGlowScaleYStart = this.mGlowScaleY;
            this.mEdgeAlphaFinish = 0.0f;
            this.mEdgeScaleYFinish = 0.0f;
            this.mGlowAlphaFinish = 0.0f;
            this.mGlowScaleYFinish = 0.0f;
        }
    }
}
