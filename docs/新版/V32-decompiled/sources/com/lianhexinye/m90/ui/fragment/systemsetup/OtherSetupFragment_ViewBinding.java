package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class OtherSetupFragment_ViewBinding implements Unbinder {
    private OtherSetupFragment target;
    private View view7f09004a;

    public OtherSetupFragment_ViewBinding(final OtherSetupFragment otherSetupFragment, View view) {
        this.target = otherSetupFragment;
        otherSetupFragment.sbShoutingVolume = (SeekBar) Utils.findRequiredViewAsType(view, R.id.sbShoutingVolume, "field 'sbShoutingVolume'", SeekBar.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butAffirm, "field 'butAffirm' and method 'onViewClicked'");
        otherSetupFragment.butAffirm = (Button) Utils.castView(viewFindRequiredView, R.id.butAffirm, "field 'butAffirm'", Button.class);
        this.view7f09004a = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.OtherSetupFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                otherSetupFragment.onViewClicked();
            }
        });
        otherSetupFragment.sbDispatchVolume = (SeekBar) Utils.findRequiredViewAsType(view, R.id.sbDispatchVolume, "field 'sbDispatchVolume'", SeekBar.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        OtherSetupFragment otherSetupFragment = this.target;
        if (otherSetupFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        otherSetupFragment.sbShoutingVolume = null;
        otherSetupFragment.butAffirm = null;
        otherSetupFragment.sbDispatchVolume = null;
        this.view7f09004a.setOnClickListener(null);
        this.view7f09004a = null;
    }
}
