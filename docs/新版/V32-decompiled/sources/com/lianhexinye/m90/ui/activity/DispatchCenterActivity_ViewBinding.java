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
public class DispatchCenterActivity_ViewBinding implements Unbinder {
    private DispatchCenterActivity target;

    public DispatchCenterActivity_ViewBinding(DispatchCenterActivity dispatchCenterActivity) {
        this(dispatchCenterActivity, dispatchCenterActivity.getWindow().getDecorView());
    }

    public DispatchCenterActivity_ViewBinding(DispatchCenterActivity dispatchCenterActivity, View view) {
        this.target = dispatchCenterActivity;
        dispatchCenterActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        dispatchCenterActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        dispatchCenterActivity.rlToolbar = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlToolbar, "field 'rlToolbar'", RelativeLayout.class);
        dispatchCenterActivity.rbRadioAttendance = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_attendance, "field 'rbRadioAttendance'", RadioButton.class);
        dispatchCenterActivity.rbRadioLedPeripheral = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_ledPeripheral, "field 'rbRadioLedPeripheral'", RadioButton.class);
        dispatchCenterActivity.rgRadioDispatch = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rg_radio_dispatch, "field 'rgRadioDispatch'", RadioGroup.class);
        dispatchCenterActivity.flDispatchContext = (FrameLayout) Utils.findRequiredViewAsType(view, R.id.flDispatchContext, "field 'flDispatchContext'", FrameLayout.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        DispatchCenterActivity dispatchCenterActivity = this.target;
        if (dispatchCenterActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        dispatchCenterActivity.toolbarTitle = null;
        dispatchCenterActivity.toolbar = null;
        dispatchCenterActivity.rlToolbar = null;
        dispatchCenterActivity.rbRadioAttendance = null;
        dispatchCenterActivity.rbRadioLedPeripheral = null;
        dispatchCenterActivity.rgRadioDispatch = null;
        dispatchCenterActivity.flDispatchContext = null;
    }
}
