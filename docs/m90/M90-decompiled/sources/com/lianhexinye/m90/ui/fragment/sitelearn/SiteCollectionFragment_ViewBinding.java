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
public class SiteCollectionFragment_ViewBinding implements Unbinder {
    private SiteCollectionFragment target;
    private View view7f09004f;
    private View view7f09021e;
    private View view7f090223;

    public SiteCollectionFragment_ViewBinding(final SiteCollectionFragment siteCollectionFragment, View view) {
        this.target = siteCollectionFragment;
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butCollectionOperation, "field 'butCollectionOperation' and method 'onViewClicked'");
        siteCollectionFragment.butCollectionOperation = (Button) Utils.castView(viewFindRequiredView, R.id.butCollectionOperation, "field 'butCollectionOperation'", Button.class);
        this.view7f09004f = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                siteCollectionFragment.onViewClicked(view2);
            }
        });
        View viewFindRequiredView2 = Utils.findRequiredView(view, R.id.tvCollectionLineName, "field 'tvCollectionLineName' and method 'onViewClicked'");
        siteCollectionFragment.tvCollectionLineName = (TextView) Utils.castView(viewFindRequiredView2, R.id.tvCollectionLineName, "field 'tvCollectionLineName'", TextView.class);
        this.view7f09021e = viewFindRequiredView2;
        viewFindRequiredView2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                siteCollectionFragment.onViewClicked(view2);
            }
        });
        siteCollectionFragment.rbDirectionUpstream = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rbDirectionUpstream, "field 'rbDirectionUpstream'", RadioButton.class);
        siteCollectionFragment.rbDirectionDown = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rbDirectionDown, "field 'rbDirectionDown'", RadioButton.class);
        siteCollectionFragment.rgCollectionDirection = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rgCollectionDirection, "field 'rgCollectionDirection'", RadioGroup.class);
        View viewFindRequiredView3 = Utils.findRequiredView(view, R.id.tvCollectionSiteName, "field 'tvCollectionSiteName' and method 'onViewClicked'");
        siteCollectionFragment.tvCollectionSiteName = (TextView) Utils.castView(viewFindRequiredView3, R.id.tvCollectionSiteName, "field 'tvCollectionSiteName'", TextView.class);
        this.view7f090223 = viewFindRequiredView3;
        viewFindRequiredView3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment_ViewBinding.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                siteCollectionFragment.onViewClicked(view2);
            }
        });
        siteCollectionFragment.tvSiteGpsTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.tvSiteGpsTitle, "field 'tvSiteGpsTitle'", TextView.class);
        siteCollectionFragment.tvLongitude = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLongitude, "field 'tvLongitude'", TextView.class);
        siteCollectionFragment.vLineSite1 = Utils.findRequiredView(view, R.id.vLineSite1, "field 'vLineSite1'");
        siteCollectionFragment.tvLatitude = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLatitude, "field 'tvLatitude'", TextView.class);
        siteCollectionFragment.vLineSite2 = Utils.findRequiredView(view, R.id.vLineSite2, "field 'vLineSite2'");
        siteCollectionFragment.tvSpeed = (TextView) Utils.findRequiredViewAsType(view, R.id.tvSpeed, "field 'tvSpeed'", TextView.class);
        siteCollectionFragment.vLineSite3 = Utils.findRequiredView(view, R.id.vLineSite3, "field 'vLineSite3'");
        siteCollectionFragment.tvAngle = (TextView) Utils.findRequiredViewAsType(view, R.id.tvAngle, "field 'tvAngle'", TextView.class);
        siteCollectionFragment.tvCurrentGpsTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.tvCurrentGpsTitle, "field 'tvCurrentGpsTitle'", TextView.class);
        siteCollectionFragment.tvCurrentLongitude = (TextView) Utils.findRequiredViewAsType(view, R.id.tvCurrentLongitude, "field 'tvCurrentLongitude'", TextView.class);
        siteCollectionFragment.vLineCurrent1 = Utils.findRequiredView(view, R.id.vLineCurrent1, "field 'vLineCurrent1'");
        siteCollectionFragment.tvCurrentLatitude = (TextView) Utils.findRequiredViewAsType(view, R.id.tvCurrentLatitude, "field 'tvCurrentLatitude'", TextView.class);
        siteCollectionFragment.vLineCurrent2 = Utils.findRequiredView(view, R.id.vLineCurrent2, "field 'vLineCurrent2'");
        siteCollectionFragment.tvCurrentSpeed = (TextView) Utils.findRequiredViewAsType(view, R.id.tvCurrentSpeed, "field 'tvCurrentSpeed'", TextView.class);
        siteCollectionFragment.vLineCurrent3 = Utils.findRequiredView(view, R.id.vLineCurrent3, "field 'vLineCurrent3'");
        siteCollectionFragment.tvCurrentAngle = (TextView) Utils.findRequiredViewAsType(view, R.id.tvCurrentAngle, "field 'tvCurrentAngle'", TextView.class);
        siteCollectionFragment.tvCollectionOrderNumber = (TextView) Utils.findRequiredViewAsType(view, R.id.tvCollectionOrderNumber, "field 'tvCollectionOrderNumber'", TextView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        SiteCollectionFragment siteCollectionFragment = this.target;
        if (siteCollectionFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        siteCollectionFragment.butCollectionOperation = null;
        siteCollectionFragment.tvCollectionLineName = null;
        siteCollectionFragment.rbDirectionUpstream = null;
        siteCollectionFragment.rbDirectionDown = null;
        siteCollectionFragment.rgCollectionDirection = null;
        siteCollectionFragment.tvCollectionSiteName = null;
        siteCollectionFragment.tvSiteGpsTitle = null;
        siteCollectionFragment.tvLongitude = null;
        siteCollectionFragment.vLineSite1 = null;
        siteCollectionFragment.tvLatitude = null;
        siteCollectionFragment.vLineSite2 = null;
        siteCollectionFragment.tvSpeed = null;
        siteCollectionFragment.vLineSite3 = null;
        siteCollectionFragment.tvAngle = null;
        siteCollectionFragment.tvCurrentGpsTitle = null;
        siteCollectionFragment.tvCurrentLongitude = null;
        siteCollectionFragment.vLineCurrent1 = null;
        siteCollectionFragment.tvCurrentLatitude = null;
        siteCollectionFragment.vLineCurrent2 = null;
        siteCollectionFragment.tvCurrentSpeed = null;
        siteCollectionFragment.vLineCurrent3 = null;
        siteCollectionFragment.tvCurrentAngle = null;
        siteCollectionFragment.tvCollectionOrderNumber = null;
        this.view7f09004f.setOnClickListener(null);
        this.view7f09004f = null;
        this.view7f09021e.setOnClickListener(null);
        this.view7f09021e = null;
        this.view7f090223.setOnClickListener(null);
        this.view7f090223 = null;
    }
}
