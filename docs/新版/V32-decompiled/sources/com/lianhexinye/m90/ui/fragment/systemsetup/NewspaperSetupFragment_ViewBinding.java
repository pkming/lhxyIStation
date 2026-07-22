package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class NewspaperSetupFragment_ViewBinding implements Unbinder {
    private NewspaperSetupFragment target;
    private View view7f09004a;

    public NewspaperSetupFragment_ViewBinding(final NewspaperSetupFragment newspaperSetupFragment, View view) {
        this.target = newspaperSetupFragment;
        newspaperSetupFragment.sbInnerVolume = (SeekBar) Utils.findRequiredViewAsType(view, R.id.sbInnerVolume, "field 'sbInnerVolume'", SeekBar.class);
        newspaperSetupFragment.sbOutsideVolume = (SeekBar) Utils.findRequiredViewAsType(view, R.id.sbOutsideVolume, "field 'sbOutsideVolume'", SeekBar.class);
        newspaperSetupFragment.rbUpAndDown = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rbUpAndDown, "field 'rbUpAndDown'", RadioButton.class);
        newspaperSetupFragment.rbLoopLine = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rbLoopLine, "field 'rbLoopLine'", RadioButton.class);
        newspaperSetupFragment.rbAntiReverse = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rbAntiReverse, "field 'rbAntiReverse'", RadioButton.class);
        newspaperSetupFragment.rgLineProperty = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rgLineProperty, "field 'rgLineProperty'", RadioGroup.class);
        newspaperSetupFragment.sBroadcastDialect = (Switch) Utils.findRequiredViewAsType(view, R.id.sBroadcastDialect, "field 'sBroadcastDialect'", Switch.class);
        newspaperSetupFragment.sBroadcastEnglish = (Switch) Utils.findRequiredViewAsType(view, R.id.sBroadcastEnglish, "field 'sBroadcastEnglish'", Switch.class);
        newspaperSetupFragment.sExternalSound = (Switch) Utils.findRequiredViewAsType(view, R.id.sExternalSound, "field 'sExternalSound'", Switch.class);
        newspaperSetupFragment.sNowTime = (Switch) Utils.findRequiredViewAsType(view, R.id.sNowTime, "field 'sNowTime'", Switch.class);
        newspaperSetupFragment.sOpenSpeeding = (Switch) Utils.findRequiredViewAsType(view, R.id.sOpenSpeeding, "field 'sOpenSpeeding'", Switch.class);
        newspaperSetupFragment.sOpenVideo = (Switch) Utils.findRequiredViewAsType(view, R.id.sOpenVideo, "field 'sOpenVideo'", Switch.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butAffirm, "field 'butAffirm' and method 'onViewClicked'");
        newspaperSetupFragment.butAffirm = (Button) Utils.castView(viewFindRequiredView, R.id.butAffirm, "field 'butAffirm'", Button.class);
        this.view7f09004a = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NewspaperSetupFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                newspaperSetupFragment.onViewClicked();
            }
        });
        newspaperSetupFragment.rlAffirm = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlAffirm, "field 'rlAffirm'", RelativeLayout.class);
        newspaperSetupFragment.sAngle = (Switch) Utils.findRequiredViewAsType(view, R.id.sAngle, "field 'sAngle'", Switch.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        NewspaperSetupFragment newspaperSetupFragment = this.target;
        if (newspaperSetupFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        newspaperSetupFragment.sbInnerVolume = null;
        newspaperSetupFragment.sbOutsideVolume = null;
        newspaperSetupFragment.rbUpAndDown = null;
        newspaperSetupFragment.rbLoopLine = null;
        newspaperSetupFragment.rbAntiReverse = null;
        newspaperSetupFragment.rgLineProperty = null;
        newspaperSetupFragment.sBroadcastDialect = null;
        newspaperSetupFragment.sBroadcastEnglish = null;
        newspaperSetupFragment.sExternalSound = null;
        newspaperSetupFragment.sNowTime = null;
        newspaperSetupFragment.sOpenSpeeding = null;
        newspaperSetupFragment.sOpenVideo = null;
        newspaperSetupFragment.butAffirm = null;
        newspaperSetupFragment.rlAffirm = null;
        newspaperSetupFragment.sAngle = null;
        this.view7f09004a.setOnClickListener(null);
        this.view7f09004a = null;
    }
}
