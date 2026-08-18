package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AlphaAnimation;

/* JADX INFO: loaded from: classes.dex */
public class ZoomControls extends LinearLayout {
    private final ZoomButton mZoomIn;
    private final ZoomButton mZoomOut;

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public ZoomControls(Context context) {
        this(context, null);
    }

    public ZoomControls(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(false);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(17367241, (ViewGroup) this, true);
        this.mZoomIn = (ZoomButton) findViewById(16909191);
        this.mZoomOut = (ZoomButton) findViewById(16909190);
    }

    public void setOnZoomInClickListener(View.OnClickListener onClickListener) {
        this.mZoomIn.setOnClickListener(onClickListener);
    }

    public void setOnZoomOutClickListener(View.OnClickListener onClickListener) {
        this.mZoomOut.setOnClickListener(onClickListener);
    }

    public void setZoomSpeed(long j) {
        this.mZoomIn.setZoomSpeed(j);
        this.mZoomOut.setZoomSpeed(j);
    }

    public void show() {
        fade(0, 0.0f, 1.0f);
    }

    public void hide() {
        fade(8, 1.0f, 0.0f);
    }

    private void fade(int i, float f, float f2) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setDuration(500L);
        startAnimation(alphaAnimation);
        setVisibility(i);
    }

    public void setIsZoomInEnabled(boolean z) {
        this.mZoomIn.setEnabled(z);
    }

    public void setIsZoomOutEnabled(boolean z) {
        this.mZoomOut.setEnabled(z);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean hasFocus() {
        return this.mZoomIn.hasFocus() || this.mZoomOut.hasFocus();
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(ZoomControls.class.getName());
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(ZoomControls.class.getName());
    }
}
