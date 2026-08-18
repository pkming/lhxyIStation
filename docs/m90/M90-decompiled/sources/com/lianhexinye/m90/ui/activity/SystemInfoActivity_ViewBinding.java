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
public class SystemInfoActivity_ViewBinding implements Unbinder {
    private SystemInfoActivity target;

    public SystemInfoActivity_ViewBinding(SystemInfoActivity systemInfoActivity) {
        this(systemInfoActivity, systemInfoActivity.getWindow().getDecorView());
    }

    public SystemInfoActivity_ViewBinding(SystemInfoActivity systemInfoActivity, View view) {
        this.target = systemInfoActivity;
        systemInfoActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        systemInfoActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        systemInfoActivity.rlToolbar = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlToolbar, "field 'rlToolbar'", RelativeLayout.class);
        systemInfoActivity.rbRadioSysVersion = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_sys_version, "field 'rbRadioSysVersion'", RadioButton.class);
        systemInfoActivity.rbRadioSysNetwork = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_sys_network, "field 'rbRadioSysNetwork'", RadioButton.class);
        systemInfoActivity.rgRadioSysNavigation = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rg_radio_sys_navigation, "field 'rgRadioSysNavigation'", RadioGroup.class);
        systemInfoActivity.flSysContext = (FrameLayout) Utils.findRequiredViewAsType(view, R.id.flSysContext, "field 'flSysContext'", FrameLayout.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        SystemInfoActivity systemInfoActivity = this.target;
        if (systemInfoActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        systemInfoActivity.toolbarTitle = null;
        systemInfoActivity.toolbar = null;
        systemInfoActivity.rlToolbar = null;
        systemInfoActivity.rbRadioSysVersion = null;
        systemInfoActivity.rbRadioSysNetwork = null;
        systemInfoActivity.rgRadioSysNavigation = null;
        systemInfoActivity.flSysContext = null;
    }
}
