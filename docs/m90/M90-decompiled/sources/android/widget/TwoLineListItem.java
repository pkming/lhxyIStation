package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class TwoLineListItem extends RelativeLayout {
    private TextView mText1;
    private TextView mText2;

    public TwoLineListItem(Context context) {
        this(context, null, 0);
    }

    public TwoLineListItem(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TwoLineListItem(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        context.obtainStyledAttributes(attributeSet, R.styleable.TwoLineListItem, i, 0).recycle();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mText1 = (TextView) findViewById(android.R.id.text1);
        this.mText2 = (TextView) findViewById(android.R.id.text2);
    }

    public TextView getText1() {
        return this.mText1;
    }

    public TextView getText2() {
        return this.mText2;
    }

    @Override // android.widget.RelativeLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(TwoLineListItem.class.getName());
    }

    @Override // android.widget.RelativeLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(TwoLineListItem.class.getName());
    }
}
