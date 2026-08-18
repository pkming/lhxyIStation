package android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/* JADX INFO: loaded from: classes.dex */
public class ImageSwitcher extends ViewSwitcher {
    public ImageSwitcher(Context context) {
        super(context);
    }

    public ImageSwitcher(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setImageResource(int i) throws Throwable {
        ((ImageView) getNextView()).setImageResource(i);
        showNext();
    }

    public void setImageURI(Uri uri) throws Throwable {
        ((ImageView) getNextView()).setImageURI(uri);
        showNext();
    }

    public void setImageDrawable(Drawable drawable) {
        ((ImageView) getNextView()).setImageDrawable(drawable);
        showNext();
    }

    @Override // android.widget.ViewSwitcher, android.widget.ViewAnimator, android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(ImageSwitcher.class.getName());
    }

    @Override // android.widget.ViewSwitcher, android.widget.ViewAnimator, android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(ImageSwitcher.class.getName());
    }
}
