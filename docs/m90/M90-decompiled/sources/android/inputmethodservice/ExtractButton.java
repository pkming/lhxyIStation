package android.inputmethodservice;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/* JADX INFO: loaded from: classes.dex */
class ExtractButton extends Button {
    public ExtractButton(Context context) {
        super(context, null);
    }

    public ExtractButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R.attr.buttonStyle);
    }

    public ExtractButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    public boolean hasWindowFocus() {
        return isEnabled() && getVisibility() == 0;
    }
}
