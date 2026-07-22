package com.lianhexinye.m90.ui.fragment.sitelearn;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class OtherCollectionFragment_ViewBinding implements Unbinder {
    private OtherCollectionFragment target;
    private View view7f090050;
    private View view7f090220;
    private View view7f090222;

    public OtherCollectionFragment_ViewBinding(final OtherCollectionFragment otherCollectionFragment, View view) {
        this.target = otherCollectionFragment;
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butCollectionOtherOperation, "field 'butCollectionOtherOperation' and method 'onViewClicked'");
        otherCollectionFragment.butCollectionOtherOperation = (Button) Utils.castView(viewFindRequiredView, R.id.butCollectionOtherOperation, "field 'butCollectionOtherOperation'", Button.class);
        this.view7f090050 = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                otherCollectionFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView2 = Utils.findRequiredView(view, R.id.tvCollectionOtherLineName, "field 'tvCollectionOtherLineName' and method 'onViewClicked'");
        otherCollectionFragment.tvCollectionOtherLineName = (TextView) Utils.castView(viewFindRequiredView2, R.id.tvCollectionOtherLineName, "field 'tvCollectionOtherLineName'", TextView.class);
        this.view7f090220 = viewFindRequiredView2;
        viewFindRequiredView2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                otherCollectionFragment.onViewClicked(view2);
            }
        });
        otherCollectionFragment.rbOtherDirectionUpstream = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rbOtherDirectionUpstream, "field 'rbOtherDirectionUpstream'", RadioButton.class);
        otherCollectionFragment.rbOtherDirectionDown = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rbOtherDirectionDown, "field 'rbOtherDirectionDown'", RadioButton.class);
        otherCollectionFragment.rgCollectionOtherDirection = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rgCollectionOtherDirection, "field 'rgCollectionOtherDirection'", RadioGroup.class);
        View viewFindRequiredView3 = Utils.findRequiredView(view, R.id.tvCollectionOtherSiteName, "field 'tvCollectionOtherSiteName' and method 'onViewClicked'");
        otherCollectionFragment.tvCollectionOtherSiteName = (TextView) Utils.castView(viewFindRequiredView3, R.id.tvCollectionOtherSiteName, "field 'tvCollectionOtherSiteName'", TextView.class);
        this.view7f090222 = viewFindRequiredView3;
        viewFindRequiredView3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment_ViewBinding.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                otherCollectionFragment.onViewClicked(view2);
            }
        });
        otherCollectionFragment.tvOtherSiteGpsTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherSiteGpsTitle, "field 'tvOtherSiteGpsTitle'", TextView.class);
        otherCollectionFragment.tvOtherLongitude = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherLongitude, "field 'tvOtherLongitude'", TextView.class);
        otherCollectionFragment.vOtherLineSite1 = Utils.findRequiredView(view, R.id.vOtherLineSite1, "field 'vOtherLineSite1'");
        otherCollectionFragment.tvOtherLatitude = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherLatitude, "field 'tvOtherLatitude'", TextView.class);
        otherCollectionFragment.vOtherLineSite2 = Utils.findRequiredView(view, R.id.vOtherLineSite2, "field 'vOtherLineSite2'");
        otherCollectionFragment.tvOtherSpeed = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherSpeed, "field 'tvOtherSpeed'", TextView.class);
        otherCollectionFragment.vOtherLineSite3 = Utils.findRequiredView(view, R.id.vOtherLineSite3, "field 'vOtherLineSite3'");
        otherCollectionFragment.tvOtherAngle = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherAngle, "field 'tvOtherAngle'", TextView.class);
        otherCollectionFragment.tvOtherCurrentGpsTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherCurrentGpsTitle, "field 'tvOtherCurrentGpsTitle'", TextView.class);
        otherCollectionFragment.tvOtherCurrentLongitude = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherCurrentLongitude, "field 'tvOtherCurrentLongitude'", TextView.class);
        otherCollectionFragment.vOtherLineCurrent1 = Utils.findRequiredView(view, R.id.vOtherLineCurrent1, "field 'vOtherLineCurrent1'");
        otherCollectionFragment.tvOtherCurrentLatitude = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherCurrentLatitude, "field 'tvOtherCurrentLatitude'", TextView.class);
        otherCollectionFragment.vOtherLineCurrent2 = Utils.findRequiredView(view, R.id.vOtherLineCurrent2, "field 'vOtherLineCurrent2'");
        otherCollectionFragment.tvOtherCurrentSpeed = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherCurrentSpeed, "field 'tvOtherCurrentSpeed'", TextView.class);
        otherCollectionFragment.vOtherLineCurrent3 = Utils.findRequiredView(view, R.id.vOtherLineCurrent3, "field 'vOtherLineCurrent3'");
        otherCollectionFragment.tvOtherCurrentAngle = (TextView) Utils.findRequiredViewAsType(view, R.id.tvOtherCurrentAngle, "field 'tvOtherCurrentAngle'", TextView.class);
        otherCollectionFragment.tvCollectionOtherOrderNumber = (TextView) Utils.findRequiredViewAsType(view, R.id.tvCollectionOtherOrderNumber, "field 'tvCollectionOtherOrderNumber'", TextView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        OtherCollectionFragment otherCollectionFragment = this.target;
        if (otherCollectionFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        otherCollectionFragment.butCollectionOtherOperation = null;
        otherCollectionFragment.tvCollectionOtherLineName = null;
        otherCollectionFragment.rbOtherDirectionUpstream = null;
        otherCollectionFragment.rbOtherDirectionDown = null;
        otherCollectionFragment.rgCollectionOtherDirection = null;
        otherCollectionFragment.tvCollectionOtherSiteName = null;
        otherCollectionFragment.tvOtherSiteGpsTitle = null;
        otherCollectionFragment.tvOtherLongitude = null;
        otherCollectionFragment.vOtherLineSite1 = null;
        otherCollectionFragment.tvOtherLatitude = null;
        otherCollectionFragment.vOtherLineSite2 = null;
        otherCollectionFragment.tvOtherSpeed = null;
        otherCollectionFragment.vOtherLineSite3 = null;
        otherCollectionFragment.tvOtherAngle = null;
        otherCollectionFragment.tvOtherCurrentGpsTitle = null;
        otherCollectionFragment.tvOtherCurrentLongitude = null;
        otherCollectionFragment.vOtherLineCurrent1 = null;
        otherCollectionFragment.tvOtherCurrentLatitude = null;
        otherCollectionFragment.vOtherLineCurrent2 = null;
        otherCollectionFragment.tvOtherCurrentSpeed = null;
        otherCollectionFragment.vOtherLineCurrent3 = null;
        otherCollectionFragment.tvOtherCurrentAngle = null;
        otherCollectionFragment.tvCollectionOtherOrderNumber = null;
        this.view7f090050.setOnClickListener(null);
        this.view7f090050 = null;
        this.view7f090220.setOnClickListener(null);
        this.view7f090220 = null;
        this.view7f090222.setOnClickListener(null);
        this.view7f090222 = null;
    }
}
