package com.lianhexinye.m90.ui.fragment.dispatch;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class AttendanceFragment_ViewBinding implements Unbinder {
    private AttendanceFragment target;

    public AttendanceFragment_ViewBinding(AttendanceFragment attendanceFragment, View view) {
        this.target = attendanceFragment;
        attendanceFragment.etCardID = (EditText) Utils.findRequiredViewAsType(view, R.id.etCardID, "field 'etCardID'", EditText.class);
        attendanceFragment.tvDriverName = (TextView) Utils.findRequiredViewAsType(view, R.id.tvDriverName, "field 'tvDriverName'", TextView.class);
        attendanceFragment.rbRadioGotoSignin = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_goto_signin, "field 'rbRadioGotoSignin'", RadioButton.class);
        attendanceFragment.rbRadioGooffSignin = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_gooff_signin, "field 'rbRadioGooffSignin'", RadioButton.class);
        attendanceFragment.rgRadioSignin = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rg_radio_signin, "field 'rgRadioSignin'", RadioGroup.class);
        attendanceFragment.butAffirmOperation = (Button) Utils.findRequiredViewAsType(view, R.id.butAffirmOperation, "field 'butAffirmOperation'", Button.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        AttendanceFragment attendanceFragment = this.target;
        if (attendanceFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        attendanceFragment.etCardID = null;
        attendanceFragment.tvDriverName = null;
        attendanceFragment.rbRadioGotoSignin = null;
        attendanceFragment.rbRadioGooffSignin = null;
        attendanceFragment.rgRadioSignin = null;
        attendanceFragment.butAffirmOperation = null;
    }
}
