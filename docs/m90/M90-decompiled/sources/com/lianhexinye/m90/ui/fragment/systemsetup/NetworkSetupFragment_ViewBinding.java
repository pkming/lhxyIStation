package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.widget.CompanyEdittext;

/* JADX INFO: loaded from: classes2.dex */
public class NetworkSetupFragment_ViewBinding implements Unbinder {
    private NetworkSetupFragment target;
    private View view7f090063;

    public NetworkSetupFragment_ViewBinding(final NetworkSetupFragment networkSetupFragment, View view) {
        this.target = networkSetupFragment;
        networkSetupFragment.etLongInterval = (CompanyEdittext) Utils.findRequiredViewAsType(view, R.id.etLongInterval, "field 'etLongInterval'", CompanyEdittext.class);
        networkSetupFragment.etSpeedingInterval = (CompanyEdittext) Utils.findRequiredViewAsType(view, R.id.etSpeedingInterval, "field 'etSpeedingInterval'", CompanyEdittext.class);
        networkSetupFragment.etAdwordsIP = (EditText) Utils.findRequiredViewAsType(view, R.id.etAdwordsIP, "field 'etAdwordsIP'", EditText.class);
        networkSetupFragment.etAdwordsPort = (EditText) Utils.findRequiredViewAsType(view, R.id.etAdwordsPort, "field 'etAdwordsPort'", EditText.class);
        networkSetupFragment.etAdwordsInterval = (CompanyEdittext) Utils.findRequiredViewAsType(view, R.id.etAdwordsInterval, "field 'etAdwordsInterval'", CompanyEdittext.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butNetWorkAffirm, "field 'butNetWorkAffirm' and method 'onViewClicked'");
        networkSetupFragment.butNetWorkAffirm = (Button) Utils.castView(viewFindRequiredView, R.id.butNetWorkAffirm, "field 'butNetWorkAffirm'", Button.class);
        this.view7f090063 = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                networkSetupFragment.onViewClicked();
            }
        });
        networkSetupFragment.etDispatchID = (EditText) Utils.findRequiredViewAsType(view, R.id.etDispatchID, "field 'etDispatchID'", EditText.class);
        networkSetupFragment.etDispatchIP = (EditText) Utils.findRequiredViewAsType(view, R.id.etDispatchIP, "field 'etDispatchIP'", EditText.class);
        networkSetupFragment.etDispatchPort = (EditText) Utils.findRequiredViewAsType(view, R.id.etDispatchPort, "field 'etDispatchPort'", EditText.class);
        networkSetupFragment.etInfoInterval = (CompanyEdittext) Utils.findRequiredViewAsType(view, R.id.etInfoInterval, "field 'etInfoInterval'", CompanyEdittext.class);
        networkSetupFragment.etAdwordsID = (EditText) Utils.findRequiredViewAsType(view, R.id.etAdwordsID, "field 'etAdwordsID'", EditText.class);
        networkSetupFragment.spDispatch = (Spinner) Utils.findRequiredViewAsType(view, R.id.spDispatch, "field 'spDispatch'", Spinner.class);
        networkSetupFragment.etAdwordsUser = (EditText) Utils.findRequiredViewAsType(view, R.id.etAdwordsUser, "field 'etAdwordsUser'", EditText.class);
        networkSetupFragment.sAdwordsSwitch = (Switch) Utils.findRequiredViewAsType(view, R.id.sAdwordsSwitch, "field 'sAdwordsSwitch'", Switch.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        NetworkSetupFragment networkSetupFragment = this.target;
        if (networkSetupFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        networkSetupFragment.etLongInterval = null;
        networkSetupFragment.etSpeedingInterval = null;
        networkSetupFragment.etAdwordsIP = null;
        networkSetupFragment.etAdwordsPort = null;
        networkSetupFragment.etAdwordsInterval = null;
        networkSetupFragment.butNetWorkAffirm = null;
        networkSetupFragment.etDispatchID = null;
        networkSetupFragment.etDispatchIP = null;
        networkSetupFragment.etDispatchPort = null;
        networkSetupFragment.etInfoInterval = null;
        networkSetupFragment.etAdwordsID = null;
        networkSetupFragment.spDispatch = null;
        networkSetupFragment.etAdwordsUser = null;
        networkSetupFragment.sAdwordsSwitch = null;
        this.view7f090063.setOnClickListener(null);
        this.view7f090063 = null;
    }
}
