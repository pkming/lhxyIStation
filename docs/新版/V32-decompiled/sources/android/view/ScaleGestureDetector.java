package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.SystemClock;
import android.util.FloatMath;
import android.view.GestureDetector;

/* JADX INFO: loaded from: classes.dex */
public class ScaleGestureDetector {
    private static final int DOUBLE_TAP_MODE_IN_PROGRESS = 1;
    private static final int DOUBLE_TAP_MODE_NONE = 0;
    private static final float SCALE_FACTOR = 0.5f;
    private static final String TAG = "ScaleGestureDetector";
    private static final long TOUCH_STABILIZE_TIME = 128;
    private final Context mContext;
    private float mCurrSpan;
    private float mCurrSpanX;
    private float mCurrSpanY;
    private long mCurrTime;
    private MotionEvent mDoubleTapEvent;
    private int mDoubleTapMode;
    private boolean mEventBeforeOrAboveStartingGestureEvent;
    private float mFocusX;
    private float mFocusY;
    private GestureDetector mGestureDetector;
    private final Handler mHandler;
    private boolean mInProgress;
    private float mInitialSpan;
    private final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    private final OnScaleGestureListener mListener;
    private int mMinSpan;
    private float mPrevSpan;
    private float mPrevSpanX;
    private float mPrevSpanY;
    private long mPrevTime;
    private boolean mQuickScaleEnabled;
    private int mSpanSlop;
    private int mTouchHistoryDirection;
    private float mTouchHistoryLastAccepted;
    private long mTouchHistoryLastAcceptedTime;
    private float mTouchLower;
    private int mTouchMinMajor;
    private float mTouchUpper;

    public interface OnScaleGestureListener {
        boolean onScale(ScaleGestureDetector scaleGestureDetector);

        boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector);

        void onScaleEnd(ScaleGestureDetector scaleGestureDetector);
    }

    public static class SimpleOnScaleGestureListener implements OnScaleGestureListener {
        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            return false;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        }
    }

    public ScaleGestureDetector(Context context, OnScaleGestureListener onScaleGestureListener) {
        this(context, onScaleGestureListener, null);
    }

    public ScaleGestureDetector(Context context, OnScaleGestureListener onScaleGestureListener, Handler handler) {
        this.mDoubleTapMode = 0;
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        this.mContext = context;
        this.mListener = onScaleGestureListener;
        this.mSpanSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 2;
        Resources resources = context.getResources();
        this.mTouchMinMajor = resources.getDimensionPixelSize(17104906);
        this.mMinSpan = resources.getDimensionPixelSize(17104905);
        this.mHandler = handler;
        if (context.getApplicationInfo().targetSdkVersion > 18) {
            setQuickScaleEnabled(true);
        }
    }

    private void addTouchHistory(MotionEvent motionEvent) {
        float touchMajor;
        int iSignum;
        int i;
        long jUptimeMillis = SystemClock.uptimeMillis();
        int pointerCount = motionEvent.getPointerCount();
        boolean z = jUptimeMillis - this.mTouchHistoryLastAcceptedTime >= 128;
        float f = 0.0f;
        int i2 = 0;
        for (int i3 = 0; i3 < pointerCount; i3++) {
            boolean z2 = !Float.isNaN(this.mTouchHistoryLastAccepted);
            int historySize = motionEvent.getHistorySize();
            int i4 = historySize + 1;
            int i5 = 0;
            while (i5 < i4) {
                if (i5 < historySize) {
                    touchMajor = motionEvent.getHistoricalTouchMajor(i3, i5);
                } else {
                    touchMajor = motionEvent.getTouchMajor(i3);
                }
                int i6 = this.mTouchMinMajor;
                if (touchMajor < i6) {
                    touchMajor = i6;
                }
                f += touchMajor;
                if (Float.isNaN(this.mTouchUpper) || touchMajor > this.mTouchUpper) {
                    this.mTouchUpper = touchMajor;
                }
                if (Float.isNaN(this.mTouchLower) || touchMajor < this.mTouchLower) {
                    this.mTouchLower = touchMajor;
                }
                if (z2 && ((iSignum = (int) Math.signum(touchMajor - this.mTouchHistoryLastAccepted)) != (i = this.mTouchHistoryDirection) || (iSignum == 0 && i == 0))) {
                    this.mTouchHistoryDirection = iSignum;
                    this.mTouchHistoryLastAcceptedTime = i5 < historySize ? motionEvent.getHistoricalEventTime(i5) : motionEvent.getEventTime();
                    z = false;
                }
                i5++;
            }
            i2 += i4;
        }
        float f2 = f / i2;
        if (z) {
            float f3 = this.mTouchUpper;
            float f4 = this.mTouchLower;
            float f5 = ((f3 + f4) + f2) / 3.0f;
            this.mTouchUpper = (f3 + f5) / 2.0f;
            this.mTouchLower = (f4 + f5) / 2.0f;
            this.mTouchHistoryLastAccepted = f5;
            this.mTouchHistoryDirection = 0;
            this.mTouchHistoryLastAcceptedTime = motionEvent.getEventTime();
        }
    }

    private void clearTouchHistory() {
        this.mTouchUpper = Float.NaN;
        this.mTouchLower = Float.NaN;
        this.mTouchHistoryLastAccepted = Float.NaN;
        this.mTouchHistoryDirection = 0;
        this.mTouchHistoryLastAcceptedTime = 0L;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float y;
        float x;
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onTouchEvent(motionEvent, 0);
        }
        this.mCurrTime = motionEvent.getEventTime();
        int actionMasked = motionEvent.getActionMasked();
        if (this.mQuickScaleEnabled) {
            this.mGestureDetector.onTouchEvent(motionEvent);
        }
        boolean z = actionMasked == 1 || actionMasked == 3;
        float fAbs = 0.0f;
        if (actionMasked == 0 || z) {
            if (this.mInProgress) {
                this.mListener.onScaleEnd(this);
                this.mInProgress = false;
                this.mInitialSpan = 0.0f;
                this.mDoubleTapMode = 0;
            } else if (this.mDoubleTapMode == 1 && z) {
                this.mInProgress = false;
                this.mInitialSpan = 0.0f;
                this.mDoubleTapMode = 0;
            }
            if (z) {
                clearTouchHistory();
                return true;
            }
        }
        boolean z2 = actionMasked == 0 || actionMasked == 6 || actionMasked == 5;
        boolean z3 = actionMasked == 6;
        int actionIndex = z3 ? motionEvent.getActionIndex() : -1;
        int pointerCount = motionEvent.getPointerCount();
        int i = z3 ? pointerCount - 1 : pointerCount;
        if (this.mDoubleTapMode == 1) {
            x = this.mDoubleTapEvent.getX();
            y = this.mDoubleTapEvent.getY();
            if (motionEvent.getY() < y) {
                this.mEventBeforeOrAboveStartingGestureEvent = true;
            } else {
                this.mEventBeforeOrAboveStartingGestureEvent = false;
            }
        } else {
            float x2 = 0.0f;
            float y2 = 0.0f;
            for (int i2 = 0; i2 < pointerCount; i2++) {
                if (actionIndex != i2) {
                    x2 += motionEvent.getX(i2);
                    y2 += motionEvent.getY(i2);
                }
            }
            float f = i;
            float f2 = x2 / f;
            y = y2 / f;
            x = f2;
        }
        addTouchHistory(motionEvent);
        float fAbs2 = 0.0f;
        for (int i3 = 0; i3 < pointerCount; i3++) {
            if (actionIndex != i3) {
                float f3 = this.mTouchHistoryLastAccepted / 2.0f;
                fAbs += Math.abs(motionEvent.getX(i3) - x) + f3;
                fAbs2 += Math.abs(motionEvent.getY(i3) - y) + f3;
            }
        }
        float f4 = i;
        float f5 = (fAbs / f4) * 2.0f;
        float f6 = (fAbs2 / f4) * 2.0f;
        float fSqrt = inDoubleTapMode() ? f6 : FloatMath.sqrt((f5 * f5) + (f6 * f6));
        boolean z4 = this.mInProgress;
        this.mFocusX = x;
        this.mFocusY = y;
        if (!inDoubleTapMode() && this.mInProgress && (fSqrt < this.mMinSpan || z2)) {
            this.mListener.onScaleEnd(this);
            this.mInProgress = false;
            this.mInitialSpan = fSqrt;
            this.mDoubleTapMode = 0;
        }
        if (z2) {
            this.mCurrSpanX = f5;
            this.mPrevSpanX = f5;
            this.mCurrSpanY = f6;
            this.mPrevSpanY = f6;
            this.mCurrSpan = fSqrt;
            this.mPrevSpan = fSqrt;
            this.mInitialSpan = fSqrt;
        }
        int i4 = inDoubleTapMode() ? this.mSpanSlop : this.mMinSpan;
        if (!this.mInProgress && fSqrt >= i4 && (z4 || Math.abs(fSqrt - this.mInitialSpan) > this.mSpanSlop)) {
            this.mCurrSpanX = f5;
            this.mPrevSpanX = f5;
            this.mCurrSpanY = f6;
            this.mPrevSpanY = f6;
            this.mCurrSpan = fSqrt;
            this.mPrevSpan = fSqrt;
            this.mPrevTime = this.mCurrTime;
            this.mInProgress = this.mListener.onScaleBegin(this);
        }
        if (actionMasked == 2) {
            this.mCurrSpanX = f5;
            this.mCurrSpanY = f6;
            this.mCurrSpan = fSqrt;
            if (this.mInProgress ? this.mListener.onScale(this) : true) {
                this.mPrevSpanX = this.mCurrSpanX;
                this.mPrevSpanY = this.mCurrSpanY;
                this.mPrevSpan = this.mCurrSpan;
                this.mPrevTime = this.mCurrTime;
            }
        }
        return true;
    }

    private boolean inDoubleTapMode() {
        return this.mDoubleTapMode == 1;
    }

    public void setQuickScaleEnabled(boolean z) {
        this.mQuickScaleEnabled = z;
        if (z && this.mGestureDetector == null) {
            this.mGestureDetector = new GestureDetector(this.mContext, new GestureDetector.SimpleOnGestureListener() { // from class: android.view.ScaleGestureDetector.1
                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                public boolean onDoubleTap(MotionEvent motionEvent) {
                    ScaleGestureDetector.this.mDoubleTapEvent = motionEvent;
                    ScaleGestureDetector.this.mDoubleTapMode = 1;
                    return true;
                }
            }, this.mHandler);
        }
    }

    public boolean isQuickScaleEnabled() {
        return this.mQuickScaleEnabled;
    }

    public boolean isInProgress() {
        return this.mInProgress;
    }

    public float getFocusX() {
        return this.mFocusX;
    }

    public float getFocusY() {
        return this.mFocusY;
    }

    public float getCurrentSpan() {
        return this.mCurrSpan;
    }

    public float getCurrentSpanX() {
        return this.mCurrSpanX;
    }

    public float getCurrentSpanY() {
        return this.mCurrSpanY;
    }

    public float getPreviousSpan() {
        return this.mPrevSpan;
    }

    public float getPreviousSpanX() {
        return this.mPrevSpanX;
    }

    public float getPreviousSpanY() {
        return this.mPrevSpanY;
    }

    public float getScaleFactor() {
        if (inDoubleTapMode()) {
            boolean z = this.mEventBeforeOrAboveStartingGestureEvent;
            boolean z2 = (z && this.mCurrSpan < this.mPrevSpan) || (!z && this.mCurrSpan > this.mPrevSpan);
            float fAbs = Math.abs(1.0f - (this.mCurrSpan / this.mPrevSpan)) * 0.5f;
            if (this.mPrevSpan <= 0.0f) {
                return 1.0f;
            }
            return z2 ? 1.0f + fAbs : 1.0f - fAbs;
        }
        float f = this.mPrevSpan;
        if (f > 0.0f) {
            return this.mCurrSpan / f;
        }
        return 1.0f;
    }

    public long getTimeDelta() {
        return this.mCurrTime - this.mPrevTime;
    }

    public long getEventTime() {
        return this.mCurrTime;
    }
}
