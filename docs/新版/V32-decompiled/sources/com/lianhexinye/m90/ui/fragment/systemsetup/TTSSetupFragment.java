package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.widget.DialogConfirmView;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.basisset.BasisSetPresenter;
import com.lianhexinye.m90.mvp.basisset.BasisSetView;
import com.lianhexinye.m90.ui.activity.BasicSetupActivity;

/* JADX INFO: loaded from: classes2.dex */
public class TTSSetupFragment extends MvpFragment<BasisSetView, BasisSetPresenter> implements BasisSetView<String> {
    private BasicSetupActivity basicSetupActivity;

    @BindView(R.id.butAffirm)
    Button butAffirm;
    private DialogConfirmView dialogConfirmView;

    @BindView(R.id.sTTS)
    Switch sTTS;

    @BindView(R.id.sbTTSInnerVolume)
    SeekBar sbTTSInnerVolume;

    @BindView(R.id.sbTTSOutsideVolume)
    SeekBar sbTTSOutsideVolume;
    private final String TAG = "TTSSetupFragment";
    private String ttsSWITCH = "否";
    private String ttsInnerVolume = "7";
    private String ttsOutsideVolume = "7";

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BasicSetupActivity) {
            this.basicSetupActivity = (BasicSetupActivity) activity;
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("TTSSetupFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_tts_setup, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("TTSSetupFragment", "onCreateView()");
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
        String str = (String) SPUserInfoUtils.get(getActivity(), "TTSInnerVolume", "7");
        this.ttsInnerVolume = str;
        this.sbTTSInnerVolume.setProgress(Integer.valueOf(str).intValue());
        String str2 = (String) SPUserInfoUtils.get(getActivity(), "TTSExternalVolume", "7");
        this.ttsOutsideVolume = str2;
        this.sbTTSOutsideVolume.setProgress(Integer.valueOf(str2).intValue());
        String str3 = (String) SPUserInfoUtils.get(getActivity(), "IfTTSReportStation", "否");
        this.ttsSWITCH = str3;
        if (str3.trim().equals("是")) {
            this.sTTS.setChecked(true);
        } else {
            this.sTTS.setChecked(false);
        }
        initEvent();
    }

    private void initEvent() {
        this.sTTS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$TTSSetupFragment$jDTlaV6cY5Z50ppA7vOuITiCev4
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initEvent$0$TTSSetupFragment(compoundButton, z);
            }
        });
        this.sbTTSInnerVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.TTSSetupFragment.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TTSSetupFragment.this.ttsInnerVolume = String.valueOf(i);
            }
        });
        this.sbTTSOutsideVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.TTSSetupFragment.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TTSSetupFragment.this.ttsOutsideVolume = String.valueOf(i);
            }
        });
    }

    public /* synthetic */ void lambda$initEvent$0$TTSSetupFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.ttsSWITCH = "是";
        } else {
            this.ttsSWITCH = "否";
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @OnClick({R.id.butAffirm})
    public void onViewClicked() {
        String str = (String) SPUserInfoUtils.get(getActivity(), SPUserInfoUtils.BUSDIRECTIONNAME, "");
        if (str != null && !str.equals("")) {
            DialogConfirmView dialogConfirmViewBuild = new DialogConfirmView.Builder(getActivity()).setContent(getResources().getString(R.string.tts_ok_tip)).setOnOKClickListener(new DialogConfirmView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.TTSSetupFragment.3
                @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                public void onOKClick() {
                    SPUserInfoUtils.put(TTSSetupFragment.this.getActivity(), "TTSInnerVolume", TTSSetupFragment.this.ttsInnerVolume);
                    ConfigInfoModel configInfoModel = new ConfigInfoModel();
                    configInfoModel.setConfigItem("TTSInnerVolume");
                    configInfoModel.setConfigValue(TTSSetupFragment.this.ttsInnerVolume);
                    ((BasisSetPresenter) TTSSetupFragment.this.mvpPresenter).upConfigData(configInfoModel);
                    SPUserInfoUtils.put(TTSSetupFragment.this.getActivity(), "TTSExternalVolume", TTSSetupFragment.this.ttsOutsideVolume);
                    ConfigInfoModel configInfoModel2 = new ConfigInfoModel();
                    configInfoModel2.setConfigItem("TTSExternalVolume");
                    configInfoModel2.setConfigValue(TTSSetupFragment.this.ttsOutsideVolume);
                    ((BasisSetPresenter) TTSSetupFragment.this.mvpPresenter).upConfigData(configInfoModel2);
                    SPUserInfoUtils.put(TTSSetupFragment.this.getActivity(), "IfTTSReportStation", TTSSetupFragment.this.ttsSWITCH);
                    ConfigInfoModel configInfoModel3 = new ConfigInfoModel();
                    configInfoModel3.setConfigItem("IfTTSReportStation");
                    configInfoModel3.setConfigValue(TTSSetupFragment.this.ttsSWITCH);
                    ((BasisSetPresenter) TTSSetupFragment.this.mvpPresenter).upConfigData(configInfoModel3);
                    TTSSetupFragment.this.toastShow(R.string.tts_ok);
                    TTSSetupFragment.this.dialogConfirmView.dismiss();
                }

                @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                public void onCancelClick() {
                    TTSSetupFragment.this.dialogConfirmView.dismiss();
                }
            }).build();
            this.dialogConfirmView = dialogConfirmViewBuild;
            dialogConfirmViewBuild.show();
            return;
        }
        toastShow("请先导入报站器资源.");
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.dialogConfirmView = null;
    }
}
