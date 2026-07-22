package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class TTSSetupFragment_ViewBinding implements Unbinder {
    private TTSSetupFragment target;
    private View view7f09004a;

    public TTSSetupFragment_ViewBinding(final TTSSetupFragment tTSSetupFragment, View view) {
        this.target = tTSSetupFragment;
        tTSSetupFragment.sTTS = (Switch) Utils.findRequiredViewAsType(view, R.id.sTTS, "field 'sTTS'", Switch.class);
        tTSSetupFragment.sbTTSInnerVolume = (SeekBar) Utils.findRequiredViewAsType(view, R.id.sbTTSInnerVolume, "field 'sbTTSInnerVolume'", SeekBar.class);
        tTSSetupFragment.sbTTSOutsideVolume = (SeekBar) Utils.findRequiredViewAsType(view, R.id.sbTTSOutsideVolume, "field 'sbTTSOutsideVolume'", SeekBar.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butAffirm, "field 'butAffirm' and method 'onViewClicked'");
        tTSSetupFragment.butAffirm = (Button) Utils.castView(viewFindRequiredView, R.id.butAffirm, "field 'butAffirm'", Button.class);
        this.view7f09004a = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.TTSSetupFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                tTSSetupFragment.onViewClicked();
            }
        });
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        TTSSetupFragment tTSSetupFragment = this.target;
        if (tTSSetupFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        tTSSetupFragment.sTTS = null;
        tTSSetupFragment.sbTTSInnerVolume = null;
        tTSSetupFragment.sbTTSOutsideVolume = null;
        tTSSetupFragment.butAffirm = null;
        this.view7f09004a.setOnClickListener(null);
        this.view7f09004a = null;
    }
}
