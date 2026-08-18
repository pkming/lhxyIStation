package com.lianhexinye.m90.ui.fragment.dispatch;

import android.view.View;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class DispatchFragment_ViewBinding implements Unbinder {
    private DispatchFragment target;
    private View view7f090055;
    private View view7f090059;
    private View view7f090060;
    private View view7f090061;
    private View view7f090070;
    private View view7f090073;
    private View view7f090074;
    private View view7f090075;
    private View view7f090077;
    private View view7f090078;
    private View view7f090079;

    public DispatchFragment_ViewBinding(final DispatchFragment dispatchFragment, View view) {
        this.target = dispatchFragment;
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butRequestSchedule, "field 'butRequestSchedule' and method 'onViewClicked'");
        dispatchFragment.butRequestSchedule = (Button) Utils.castView(viewFindRequiredView, R.id.butRequestSchedule, "field 'butRequestSchedule'", Button.class);
        this.view7f090079 = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        dispatchFragment.butRequestHandover = (Button) Utils.findRequiredViewAsType(view, R.id.butRequestHandover, "field 'butRequestHandover'", Button.class);
        View viewFindRequiredView2 = Utils.findRequiredView(view, R.id.butRequestOil, "field 'butRequestOil' and method 'onViewClicked'");
        dispatchFragment.butRequestOil = (Button) Utils.castView(viewFindRequiredView2, R.id.butRequestOil, "field 'butRequestOil'", Button.class);
        this.view7f090077 = viewFindRequiredView2;
        viewFindRequiredView2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView3 = Utils.findRequiredView(view, R.id.butRequestAerate, "field 'butRequestAerate' and method 'onViewClicked'");
        dispatchFragment.butRequestAerate = (Button) Utils.castView(viewFindRequiredView3, R.id.butRequestAerate, "field 'butRequestAerate'", Button.class);
        this.view7f090073 = viewFindRequiredView3;
        viewFindRequiredView3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView4 = Utils.findRequiredView(view, R.id.butRequestCharge, "field 'butRequestCharge' and method 'onViewClicked'");
        dispatchFragment.butRequestCharge = (Button) Utils.castView(viewFindRequiredView4, R.id.butRequestCharge, "field 'butRequestCharge'", Button.class);
        this.view7f090074 = viewFindRequiredView4;
        viewFindRequiredView4.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.4
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView5 = Utils.findRequiredView(view, R.id.butExitOperation, "field 'butExitOperation' and method 'onViewClicked'");
        dispatchFragment.butExitOperation = (Button) Utils.castView(viewFindRequiredView5, R.id.butExitOperation, "field 'butExitOperation'", Button.class);
        this.view7f090055 = viewFindRequiredView5;
        viewFindRequiredView5.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.5
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView6 = Utils.findRequiredView(view, R.id.butManualStart, "field 'butManualStart' and method 'onViewClicked'");
        dispatchFragment.butManualStart = (Button) Utils.castView(viewFindRequiredView6, R.id.butManualStart, "field 'butManualStart'", Button.class);
        this.view7f090061 = viewFindRequiredView6;
        viewFindRequiredView6.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.6
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView7 = Utils.findRequiredView(view, R.id.butManualEnd, "field 'butManualEnd' and method 'onViewClicked'");
        dispatchFragment.butManualEnd = (Button) Utils.castView(viewFindRequiredView7, R.id.butManualEnd, "field 'butManualEnd'", Button.class);
        this.view7f090060 = viewFindRequiredView7;
        viewFindRequiredView7.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.7
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView8 = Utils.findRequiredView(view, R.id.butRequestCharter, "field 'butRequestCharter' and method 'onViewClicked'");
        dispatchFragment.butRequestCharter = (Button) Utils.castView(viewFindRequiredView8, R.id.butRequestCharter, "field 'butRequestCharter'", Button.class);
        this.view7f090075 = viewFindRequiredView8;
        viewFindRequiredView8.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.8
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView9 = Utils.findRequiredView(view, R.id.butRequestRepair, "field 'butRequestRepair' and method 'onViewClicked'");
        dispatchFragment.butRequestRepair = (Button) Utils.castView(viewFindRequiredView9, R.id.butRequestRepair, "field 'butRequestRepair'", Button.class);
        this.view7f090078 = viewFindRequiredView9;
        viewFindRequiredView9.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.9
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView10 = Utils.findRequiredView(view, R.id.butOtherRequests, "field 'butOtherRequests' and method 'onViewClicked'");
        dispatchFragment.butOtherRequests = (Button) Utils.castView(viewFindRequiredView10, R.id.butOtherRequests, "field 'butOtherRequests'", Button.class);
        this.view7f090070 = viewFindRequiredView10;
        viewFindRequiredView10.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.10
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView11 = Utils.findRequiredView(view, R.id.butIntercom, "field 'butIntercom' and method 'onViewClicked'");
        dispatchFragment.butIntercom = (Button) Utils.castView(viewFindRequiredView11, R.id.butIntercom, "field 'butIntercom'", Button.class);
        this.view7f090059 = viewFindRequiredView11;
        viewFindRequiredView11.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.DispatchFragment_ViewBinding.11
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                dispatchFragment.onViewClicked(view2);
            }
        });
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        DispatchFragment dispatchFragment = this.target;
        if (dispatchFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        dispatchFragment.butRequestSchedule = null;
        dispatchFragment.butRequestHandover = null;
        dispatchFragment.butRequestOil = null;
        dispatchFragment.butRequestAerate = null;
        dispatchFragment.butRequestCharge = null;
        dispatchFragment.butExitOperation = null;
        dispatchFragment.butManualStart = null;
        dispatchFragment.butManualEnd = null;
        dispatchFragment.butRequestCharter = null;
        dispatchFragment.butRequestRepair = null;
        dispatchFragment.butOtherRequests = null;
        dispatchFragment.butIntercom = null;
        this.view7f090079.setOnClickListener(null);
        this.view7f090079 = null;
        this.view7f090077.setOnClickListener(null);
        this.view7f090077 = null;
        this.view7f090073.setOnClickListener(null);
        this.view7f090073 = null;
        this.view7f090074.setOnClickListener(null);
        this.view7f090074 = null;
        this.view7f090055.setOnClickListener(null);
        this.view7f090055 = null;
        this.view7f090061.setOnClickListener(null);
        this.view7f090061 = null;
        this.view7f090060.setOnClickListener(null);
        this.view7f090060 = null;
        this.view7f090075.setOnClickListener(null);
        this.view7f090075 = null;
        this.view7f090078.setOnClickListener(null);
        this.view7f090078 = null;
        this.view7f090070.setOnClickListener(null);
        this.view7f090070 = null;
        this.view7f090059.setOnClickListener(null);
        this.view7f090059 = null;
    }
}
