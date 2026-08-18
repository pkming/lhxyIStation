package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class LanguageSetupFragment_ViewBinding implements Unbinder {
    private LanguageSetupFragment target;
    private View view7f09004a;

    public LanguageSetupFragment_ViewBinding(final LanguageSetupFragment languageSetupFragment, View view) {
        this.target = languageSetupFragment;
        languageSetupFragment.rbRadioLanguageCh = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_language_ch, "field 'rbRadioLanguageCh'", RadioButton.class);
        languageSetupFragment.rbRadioLanguageEn = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_language_en, "field 'rbRadioLanguageEn'", RadioButton.class);
        languageSetupFragment.rgRadioLanguage = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rg_radio_language, "field 'rgRadioLanguage'", RadioGroup.class);
        languageSetupFragment.rbRadioLanguageAuto = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_language_auto, "field 'rbRadioLanguageAuto'", RadioButton.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butAffirm, "field 'butAffirm' and method 'onViewClicked'");
        languageSetupFragment.butAffirm = (Button) Utils.castView(viewFindRequiredView, R.id.butAffirm, "field 'butAffirm'", Button.class);
        this.view7f09004a = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.LanguageSetupFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                languageSetupFragment.onViewClicked();
            }
        });
        languageSetupFragment.rbRadioLanguageTr = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_language_tr, "field 'rbRadioLanguageTr'", RadioButton.class);
        languageSetupFragment.rbRadioLanguageKo = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_language_ko, "field 'rbRadioLanguageKo'", RadioButton.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        LanguageSetupFragment languageSetupFragment = this.target;
        if (languageSetupFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        languageSetupFragment.rbRadioLanguageCh = null;
        languageSetupFragment.rbRadioLanguageEn = null;
        languageSetupFragment.rgRadioLanguage = null;
        languageSetupFragment.rbRadioLanguageAuto = null;
        languageSetupFragment.butAffirm = null;
        languageSetupFragment.rbRadioLanguageTr = null;
        languageSetupFragment.rbRadioLanguageKo = null;
        this.view7f09004a.setOnClickListener(null);
        this.view7f09004a = null;
    }
}
