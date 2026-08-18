package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class SerialPortSetupFragment_ViewBinding implements Unbinder {
    private SerialPortSetupFragment target;
    private View view7f090071;
    private View view7f09007a;

    public SerialPortSetupFragment_ViewBinding(final SerialPortSetupFragment serialPortSetupFragment, View view) {
        this.target = serialPortSetupFragment;
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butPortAffirm, "field 'butPortAffirm' and method 'onViewClicked'");
        serialPortSetupFragment.butPortAffirm = (Button) Utils.castView(viewFindRequiredView, R.id.butPortAffirm, "field 'butPortAffirm'", Button.class);
        this.view7f090071 = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                serialPortSetupFragment.onViewClicked(view2);
            }
        });
        serialPortSetupFragment.rlPortAffirm = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlPortAffirm, "field 'rlPortAffirm'", RelativeLayout.class);
        serialPortSetupFragment.spPortBaud2321 = (Spinner) Utils.findRequiredViewAsType(view, R.id.spPortBaud2321, "field 'spPortBaud2321'", Spinner.class);
        serialPortSetupFragment.spPortProtocol2321 = (Spinner) Utils.findRequiredViewAsType(view, R.id.spPortProtocol2321, "field 'spPortProtocol2321'", Spinner.class);
        serialPortSetupFragment.spPortBaud2322 = (Spinner) Utils.findRequiredViewAsType(view, R.id.spPortBaud2322, "field 'spPortBaud2322'", Spinner.class);
        serialPortSetupFragment.spPortProtocol2322 = (Spinner) Utils.findRequiredViewAsType(view, R.id.spPortProtocol2322, "field 'spPortProtocol2322'", Spinner.class);
        serialPortSetupFragment.spPortBaud485 = (Spinner) Utils.findRequiredViewAsType(view, R.id.spPortBaud485, "field 'spPortBaud485'", Spinner.class);
        serialPortSetupFragment.spPortProtocol485 = (Spinner) Utils.findRequiredViewAsType(view, R.id.spPortProtocol485, "field 'spPortProtocol485'", Spinner.class);
        serialPortSetupFragment.spPortBaud4852 = (Spinner) Utils.findRequiredViewAsType(view, R.id.spPortBaud4852, "field 'spPortBaud4852'", Spinner.class);
        serialPortSetupFragment.spPortProtocol4852 = (Spinner) Utils.findRequiredViewAsType(view, R.id.spPortProtocol4852, "field 'spPortProtocol4852'", Spinner.class);
        serialPortSetupFragment.spPortNumber1 = (Spinner) Utils.findRequiredViewAsType(view, R.id.spPortNumber1, "field 'spPortNumber1'", Spinner.class);
        serialPortSetupFragment.rbTxtType = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rbTxtType, "field 'rbTxtType'", RadioButton.class);
        serialPortSetupFragment.rbHexType = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rbHexType, "field 'rbHexType'", RadioButton.class);
        serialPortSetupFragment.rgDataType = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rgDataType, "field 'rgDataType'", RadioGroup.class);
        View viewFindRequiredView2 = Utils.findRequiredView(view, R.id.butSend, "field 'butSend' and method 'onViewClicked'");
        serialPortSetupFragment.butSend = (Button) Utils.castView(viewFindRequiredView2, R.id.butSend, "field 'butSend'", Button.class);
        this.view7f09007a = viewFindRequiredView2;
        viewFindRequiredView2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                serialPortSetupFragment.onViewClicked(view2);
            }
        });
        serialPortSetupFragment.etPortData = (EditText) Utils.findRequiredViewAsType(view, R.id.etPortData, "field 'etPortData'", EditText.class);
        serialPortSetupFragment.spChannel = (Spinner) Utils.findRequiredViewAsType(view, R.id.spChannel, "field 'spChannel'", Spinner.class);
        serialPortSetupFragment.gpsChannel = (Spinner) Utils.findRequiredViewAsType(view, R.id.gpsChannel, "field 'gpsChannel'", Spinner.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        SerialPortSetupFragment serialPortSetupFragment = this.target;
        if (serialPortSetupFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        serialPortSetupFragment.butPortAffirm = null;
        serialPortSetupFragment.rlPortAffirm = null;
        serialPortSetupFragment.spPortBaud2321 = null;
        serialPortSetupFragment.spPortProtocol2321 = null;
        serialPortSetupFragment.spPortBaud2322 = null;
        serialPortSetupFragment.spPortProtocol2322 = null;
        serialPortSetupFragment.spPortBaud485 = null;
        serialPortSetupFragment.spPortProtocol485 = null;
        serialPortSetupFragment.spPortBaud4852 = null;
        serialPortSetupFragment.spPortProtocol4852 = null;
        serialPortSetupFragment.spPortNumber1 = null;
        serialPortSetupFragment.rbTxtType = null;
        serialPortSetupFragment.rbHexType = null;
        serialPortSetupFragment.rgDataType = null;
        serialPortSetupFragment.butSend = null;
        serialPortSetupFragment.etPortData = null;
        serialPortSetupFragment.spChannel = null;
        serialPortSetupFragment.gpsChannel = null;
        this.view7f090071.setOnClickListener(null);
        this.view7f090071 = null;
        this.view7f09007a.setOnClickListener(null);
        this.view7f09007a = null;
    }
}
