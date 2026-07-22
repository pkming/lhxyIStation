package com.lianhexinye.m90.ui.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class BasicSetupActivity_ViewBinding implements Unbinder {
    private BasicSetupActivity target;

    public BasicSetupActivity_ViewBinding(BasicSetupActivity basicSetupActivity) {
        this(basicSetupActivity, basicSetupActivity.getWindow().getDecorView());
    }

    public BasicSetupActivity_ViewBinding(BasicSetupActivity basicSetupActivity, View view) {
        this.target = basicSetupActivity;
        basicSetupActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        basicSetupActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        basicSetupActivity.rlToolbar = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlToolbar, "field 'rlToolbar'", RelativeLayout.class);
        basicSetupActivity.rbRadioNewspaper = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_newspaper, "field 'rbRadioNewspaper'", RadioButton.class);
        basicSetupActivity.rbRadioNetwork = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_network, "field 'rbRadioNetwork'", RadioButton.class);
        basicSetupActivity.rbRadioSerialPort = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_serial_port, "field 'rbRadioSerialPort'", RadioButton.class);
        basicSetupActivity.rbRadioLanguage = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_language, "field 'rbRadioLanguage'", RadioButton.class);
        basicSetupActivity.rgRadioSetupNavigation = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rg_radio_setup_navigation, "field 'rgRadioSetupNavigation'", RadioGroup.class);
        basicSetupActivity.flSetupContext = (FrameLayout) Utils.findRequiredViewAsType(view, R.id.flSetupContext, "field 'flSetupContext'", FrameLayout.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        BasicSetupActivity basicSetupActivity = this.target;
        if (basicSetupActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        basicSetupActivity.toolbarTitle = null;
        basicSetupActivity.toolbar = null;
        basicSetupActivity.rlToolbar = null;
        basicSetupActivity.rbRadioNewspaper = null;
        basicSetupActivity.rbRadioNetwork = null;
        basicSetupActivity.rbRadioSerialPort = null;
        basicSetupActivity.rbRadioLanguage = null;
        basicSetupActivity.rgRadioSetupNavigation = null;
        basicSetupActivity.flSetupContext = null;
    }
}
