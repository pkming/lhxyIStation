package android.view;

import android.util.Pools;

/* JADX INFO: loaded from: classes.dex */
public final class VelocityTracker {
    private static final int ACTIVE_POINTER_ID = -1;
    private static final Pools.SynchronizedPool<VelocityTracker> sPool = new Pools.SynchronizedPool<>(2);
    private int mPtr;
    private final String mStrategy;

    private static native void nativeAddMovement(int i, MotionEvent motionEvent);

    private static native void nativeClear(int i);

    private static native void nativeComputeCurrentVelocity(int i, int i2, float f);

    private static native void nativeDispose(int i);

    private static native boolean nativeGetEstimator(int i, int i2, Estimator estimator);

    private static native float nativeGetXVelocity(int i, int i2);

    private static native float nativeGetYVelocity(int i, int i2);

    private static native int nativeInitialize(String str);

    public static VelocityTracker obtain() {
        VelocityTracker velocityTrackerAcquire = sPool.acquire();
        return velocityTrackerAcquire != null ? velocityTrackerAcquire : new VelocityTracker(null);
    }

    public static VelocityTracker obtain(String str) {
        if (str == null) {
            return obtain();
        }
        return new VelocityTracker(str);
    }

    public void recycle() {
        if (this.mStrategy == null) {
            clear();
            sPool.release(this);
        }
    }

    private VelocityTracker(String str) {
        this.mPtr = nativeInitialize(str);
        this.mStrategy = str;
    }

    protected void finalize() throws Throwable {
        try {
            int i = this.mPtr;
            if (i != 0) {
                nativeDispose(i);
                this.mPtr = 0;
            }
        } finally {
            super.finalize();
        }
    }

    public void clear() {
        nativeClear(this.mPtr);
    }

    public void addMovement(MotionEvent motionEvent) {
        if (motionEvent == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        nativeAddMovement(this.mPtr, motionEvent);
    }

    public void computeCurrentVelocity(int i) {
        nativeComputeCurrentVelocity(this.mPtr, i, Float.MAX_VALUE);
    }

    public void computeCurrentVelocity(int i, float f) {
        nativeComputeCurrentVelocity(this.mPtr, i, f);
    }

    public float getXVelocity() {
        return nativeGetXVelocity(this.mPtr, -1);
    }

    public float getYVelocity() {
        return nativeGetYVelocity(this.mPtr, -1);
    }

    public float getXVelocity(int i) {
        return nativeGetXVelocity(this.mPtr, i);
    }

    public float getYVelocity(int i) {
        return nativeGetYVelocity(this.mPtr, i);
    }

    public boolean getEstimator(int i, Estimator estimator) {
        if (estimator == null) {
            throw new IllegalArgumentException("outEstimator must not be null");
        }
        return nativeGetEstimator(this.mPtr, i, estimator);
    }

    public static final class Estimator {
        private static final int MAX_DEGREE = 4;
        public float confidence;
        public int degree;
        public final float[] xCoeff = new float[5];
        public final float[] yCoeff = new float[5];

        public float estimateX(float f) {
            return estimate(f, this.xCoeff);
        }

        public float estimateY(float f) {
            return estimate(f, this.yCoeff);
        }

        public float getXCoeff(int i) {
            if (i <= this.degree) {
                return this.xCoeff[i];
            }
            return 0.0f;
        }

        public float getYCoeff(int i) {
            if (i <= this.degree) {
                return this.yCoeff[i];
            }
            return 0.0f;
        }

        private float estimate(float f, float[] fArr) {
            float f2 = 0.0f;
            float f3 = 1.0f;
            for (int i = 0; i <= this.degree; i++) {
                f2 += fArr[i] * f3;
                f3 *= f;
            }
            return f2;
        }
    }
}
