package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.language.SPUtil;
import com.lianhexinye.m90.common.widget.DialogConfirmView;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.basisset.BasisSetPresenter;
import com.lianhexinye.m90.mvp.basisset.BasisSetView;
import com.lianhexinye.m90.ui.activity.BasicSetupActivity;

/* JADX INFO: loaded from: classes2.dex */
public class LanguageSetupFragment extends MvpFragment<BasisSetView, BasisSetPresenter> implements BasisSetView<String> {
    private BasicSetupActivity basicSetupActivity;

    @BindView(R.id.butAffirm)
    Button butAffirm;
    private volatile DialogConfirmView dialogConfirmView;

    @BindView(R.id.rb_radio_language_auto)
    RadioButton rbRadioLanguageAuto;

    @BindView(R.id.rb_radio_language_ch)
    RadioButton rbRadioLanguageCh;

    @BindView(R.id.rb_radio_language_en)
    RadioButton rbRadioLanguageEn;

    @BindView(R.id.rb_radio_language_ko)
    RadioButton rbRadioLanguageKo;

    @BindView(R.id.rb_radio_language_tr)
    RadioButton rbRadioLanguageTr;

    @BindView(R.id.rg_radio_language)
    RadioGroup rgRadioLanguage;
    private final String TAG = "LanguageSetupFragment";
    private String seleLanguage = "0";

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void authorizationResult(int i, String str) {
    }

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void getDataSuccess(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, com.lianhexinye.m90.mvp.BaseFragment
    public void hide() {
    }

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void hideLoading() {
    }

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, com.lianhexinye.m90.mvp.BaseFragment
    public void show() {
    }

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void showLoading() {
    }

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void showLoading(String str) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BasicSetupActivity) {
            this.basicSetupActivity = (BasicSetupActivity) context;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT >= 23 || !(activity instanceof BasicSetupActivity)) {
            return;
        }
        this.basicSetupActivity = (BasicSetupActivity) activity;
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("LanguageSetupFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_language, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("LanguageSetupFragment", "onCreateView()");
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ((BasisSetPresenter) this.mvpPresenter).detachView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public BasisSetPresenter createPresenter() {
        return new BasisSetPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        String str = "" + SPUtil.getInstance(getActivity()).getSelectLanguage();
        this.seleLanguage = str;
        int iIntValue = Integer.valueOf(str).intValue();
        if (iIntValue == 0) {
            this.rgRadioLanguage.check(R.id.rb_radio_language_auto);
        } else {
            if (iIntValue == 1) {
                this.rgRadioLanguage.check(R.id.rb_radio_language_ch);
            } else if (iIntValue != 2) {
                if (iIntValue == 3) {
                    this.rgRadioLanguage.check(R.id.rb_radio_language_en);
                } else if (iIntValue == 4) {
                    this.rgRadioLanguage.check(R.id.rb_radio_language_ko);
                } else if (iIntValue == 5) {
                    this.rgRadioLanguage.check(R.id.rb_radio_language_es);
                }
            }
            this.rgRadioLanguage.check(R.id.rb_radio_language_tr);
        }
        this.rgRadioLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.LanguageSetupFragment.1
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_radio_language_auto /* 2131296611 */:
                        LanguageSetupFragment.this.seleLanguage = "0";
                        break;
                    case R.id.rb_radio_language_ch /* 2131296612 */:
                        LanguageSetupFragment.this.seleLanguage = "1";
                        break;
                    case R.id.rb_radio_language_en /* 2131296613 */:
                        LanguageSetupFragment.this.seleLanguage = "3";
                        break;
                    case R.id.rb_radio_language_es /* 2131296614 */:
                        LanguageSetupFragment.this.seleLanguage = "5";
                        break;
                    case R.id.rb_radio_language_ko /* 2131296615 */:
                        LanguageSetupFragment.this.seleLanguage = "4";
                        break;
                    case R.id.rb_radio_language_tr /* 2131296616 */:
                        LanguageSetupFragment.this.seleLanguage = "2";
                        break;
                }
            }
        });
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @OnClick({R.id.butAffirm})
    public void onViewClicked() {
        String str = (String) SPUserInfoUtils.get(getActivity(), SPUserInfoUtils.BUSDIRECTIONNAME, "");
        if (str != null && !str.equals("")) {
            this.dialogConfirmView = new DialogConfirmView.Builder(getActivity()).setContent(getResources().getString(R.string.language_tip)).setButOkTxt(getResources().getString(R.string.restart)).setOnOKClickListener(new DialogConfirmView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.LanguageSetupFragment.2
                @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                public void onOKClick() {
                    SPUtil.getInstance(LanguageSetupFragment.this.getActivity()).saveLanguage(LanguageSetupFragment.this.seleLanguage);
                    ConfigInfoModel configInfoModel = new ConfigInfoModel();
                    configInfoModel.setConfigItem("LanguageSettings");
                    configInfoModel.setConfigValue(LanguageSetupFragment.this.seleLanguage);
                    ((BasisSetPresenter) LanguageSetupFragment.this.mvpPresenter).upConfigData(configInfoModel);
                    LanguageSetupFragment.this.dialogConfirmView.dismiss();
                    FragmentActivity activity = LanguageSetupFragment.this.getActivity();
                    LanguageSetupFragment.this.getActivity();
                    ((PowerManager) activity.getSystemService(Context.POWER_SERVICE)).reboot("重启");
                }

                @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                public void onCancelClick() {
                    LanguageSetupFragment.this.dialogConfirmView.dismiss();
                }
            }).build();
            this.dialogConfirmView.show();
        } else {
            toastShow("请先导入报站器资源.");
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.dialogConfirmView = null;
    }
}
