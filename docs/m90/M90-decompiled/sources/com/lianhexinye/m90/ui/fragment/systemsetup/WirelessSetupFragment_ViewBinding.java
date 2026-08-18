package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.view.View;
import android.widget.RelativeLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class WirelessSetupFragment_ViewBinding implements Unbinder {
    private WirelessSetupFragment target;
    private View view7f09018e;

    public WirelessSetupFragment_ViewBinding(final WirelessSetupFragment wirelessSetupFragment, View view) {
        this.target = wirelessSetupFragment;
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.rlGotoSystemUp, "field 'rlGotoSystemUp' and method 'onViewClicked'");
        wirelessSetupFragment.rlGotoSystemUp = (RelativeLayout) Utils.castView(viewFindRequiredView, R.id.rlGotoSystemUp, "field 'rlGotoSystemUp'", RelativeLayout.class);
        this.view7f09018e = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.WirelessSetupFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                wirelessSetupFragment.onViewClicked();
            }
        });
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        WirelessSetupFragment wirelessSetupFragment = this.target;
        if (wirelessSetupFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        wirelessSetupFragment.rlGotoSystemUp = null;
        this.view7f09018e.setOnClickListener(null);
        this.view7f09018e = null;
    }
}
