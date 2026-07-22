package com.lianhexinye.m90.ui.fragment.dispatch;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class PaycardTestFragment_ViewBinding implements Unbinder {
    private PaycardTestFragment target;
    private View view7f09004c;

    public PaycardTestFragment_ViewBinding(final PaycardTestFragment paycardTestFragment, View view) {
        this.target = paycardTestFragment;
        paycardTestFragment.tvPaycard = (TextView) Utils.findRequiredViewAsType(view, R.id.tvPaycard, "field 'tvPaycard'", TextView.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butBroadcastTest, "field 'butBroadcastTest' and method 'onViewClicked'");
        paycardTestFragment.butBroadcastTest = (Button) Utils.castView(viewFindRequiredView, R.id.butBroadcastTest, "field 'butBroadcastTest'", Button.class);
        this.view7f09004c = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.PaycardTestFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                paycardTestFragment.onViewClicked(view2);
            }
        });
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        PaycardTestFragment paycardTestFragment = this.target;
        if (paycardTestFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        paycardTestFragment.tvPaycard = null;
        paycardTestFragment.butBroadcastTest = null;
        this.view7f09004c.setOnClickListener(null);
        this.view7f09004c = null;
    }
}
