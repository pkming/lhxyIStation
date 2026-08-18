package com.lianhexinye.m90.ui.fragment.dispatch;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class LedPeripheralFragment_ViewBinding implements Unbinder {
    private LedPeripheralFragment target;
    private View view7f09007b;

    public LedPeripheralFragment_ViewBinding(final LedPeripheralFragment ledPeripheralFragment, View view) {
        this.target = ledPeripheralFragment;
        ledPeripheralFragment.rlToolbarLedPeripheral = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlToolbarLedPeripheral, "field 'rlToolbarLedPeripheral'", RelativeLayout.class);
        ledPeripheralFragment.vInfoLedPeripheral = Utils.findRequiredView(view, R.id.vInfoLedPeripheral, "field 'vInfoLedPeripheral'");
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butSendLedPeripheral, "field 'butSendLedPeripheral' and method 'onViewClicked'");
        ledPeripheralFragment.butSendLedPeripheral = (Button) Utils.castView(viewFindRequiredView, R.id.butSendLedPeripheral, "field 'butSendLedPeripheral'", Button.class);
        this.view7f09007b = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.LedPeripheralFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                ledPeripheralFragment.onViewClicked();
            }
        });
        ledPeripheralFragment.rlSendLedPeripheral = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlSendLedPeripheral, "field 'rlSendLedPeripheral'", RelativeLayout.class);
        ledPeripheralFragment.lvInfoLedPeripheral = (ListView) Utils.findRequiredViewAsType(view, R.id.lvInfoLedPeripheral, "field 'lvInfoLedPeripheral'", ListView.class);
        ledPeripheralFragment.tvInfoLedPeripheralTip = (TextView) Utils.findRequiredViewAsType(view, R.id.tvInfoLedPeripheralTip, "field 'tvInfoLedPeripheralTip'", TextView.class);
        ledPeripheralFragment.lyNotInfoLedPeripheral = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyNotInfoLedPeripheral, "field 'lyNotInfoLedPeripheral'", LinearLayout.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        LedPeripheralFragment ledPeripheralFragment = this.target;
        if (ledPeripheralFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        ledPeripheralFragment.rlToolbarLedPeripheral = null;
        ledPeripheralFragment.vInfoLedPeripheral = null;
        ledPeripheralFragment.butSendLedPeripheral = null;
        ledPeripheralFragment.rlSendLedPeripheral = null;
        ledPeripheralFragment.lvInfoLedPeripheral = null;
        ledPeripheralFragment.tvInfoLedPeripheralTip = null;
        ledPeripheralFragment.lyNotInfoLedPeripheral = null;
        this.view7f09007b.setOnClickListener(null);
        this.view7f09007b = null;
    }
}
