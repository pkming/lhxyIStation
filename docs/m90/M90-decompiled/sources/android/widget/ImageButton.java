package android.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

/* JADX INFO: loaded from: classes.dex */
@RemoteViews.RemoteView
public class ImageButton extends ImageView {
    @Override // android.view.View
    protected boolean onSetAlpha(int i) {
        return false;
    }

    public ImageButton(Context context) {
        this(context, null);
    }

    public ImageButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.imageButtonStyle);
    }

    public ImageButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setFocusable(true);
    }

    @Override // android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(ImageButton.class.getName());
    }

    @Override // android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(ImageButton.class.getName());
    }
}
