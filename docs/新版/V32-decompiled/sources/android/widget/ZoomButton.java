package android.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/* JADX INFO: loaded from: classes.dex */
public class ZoomButton extends ImageButton implements View.OnLongClickListener {
    private final Handler mHandler;
    private boolean mIsInLongpress;
    private final Runnable mRunnable;
    private long mZoomSpeed;

    public ZoomButton(Context context) {
        this(context, null);
    }

    public ZoomButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ZoomButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mRunnable = new Runnable() { // from class: android.widget.ZoomButton.1
            @Override // java.lang.Runnable
            public void run() {
                if (ZoomButton.this.hasOnClickListeners() && ZoomButton.this.mIsInLongpress && ZoomButton.this.isEnabled()) {
                    ZoomButton.this.callOnClick();
                    ZoomButton.this.mHandler.postDelayed(this, ZoomButton.this.mZoomSpeed);
                }
            }
        };
        this.mZoomSpeed = 1000L;
        this.mHandler = new Handler();
        setOnLongClickListener(this);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
            this.mIsInLongpress = false;
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setZoomSpeed(long j) {
        this.mZoomSpeed = j;
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        this.mIsInLongpress = true;
        this.mHandler.post(this.mRunnable);
        return true;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        this.mIsInLongpress = false;
        return super.onKeyUp(i, keyEvent);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        if (!z) {
            setPressed(false);
        }
        super.setEnabled(z);
    }

    @Override // android.view.View
    public boolean dispatchUnhandledMove(View view, int i) {
        clearFocus();
        return super.dispatchUnhandledMove(view, i);
    }

    @Override // android.widget.ImageButton, android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(ZoomButton.class.getName());
    }

    @Override // android.widget.ImageButton, android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(ZoomButton.class.getName());
    }
}
