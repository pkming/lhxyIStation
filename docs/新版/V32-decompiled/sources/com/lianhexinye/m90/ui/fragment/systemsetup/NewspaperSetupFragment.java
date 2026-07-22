package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.widget.DialogConfirmView;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.basisset.BasisSetPresenter;
import com.lianhexinye.m90.mvp.basisset.BasisSetView;
import com.lianhexinye.m90.ui.activity.BasicSetupActivity;

/* JADX INFO: loaded from: classes2.dex */
public class NewspaperSetupFragment extends MvpFragment<BasisSetView, BasisSetPresenter> implements BasisSetView<String> {
    private BasicSetupActivity basicSetupActivity;

    @BindView(R.id.butAffirm)
    Button butAffirm;
    private volatile DialogConfirmView dialogConfirmView;

    @BindView(R.id.rbAntiReverse)
    RadioButton rbAntiReverse;

    @BindView(R.id.rbLoopLine)
    RadioButton rbLoopLine;

    @BindView(R.id.rbUpAndDown)
    RadioButton rbUpAndDown;

    @BindView(R.id.rgLineProperty)
    RadioGroup rgLineProperty;

    @BindView(R.id.rlAffirm)
    RelativeLayout rlAffirm;

    @BindView(R.id.sAngle)
    Switch sAngle;

    @BindView(R.id.sBroadcastDialect)
    Switch sBroadcastDialect;

    @BindView(R.id.sBroadcastEnglish)
    Switch sBroadcastEnglish;

    @BindView(R.id.sExternalSound)
    Switch sExternalSound;

    @BindView(R.id.sNowTime)
    Switch sNowTime;

    @BindView(R.id.sOpenSpeeding)
    Switch sOpenSpeeding;

    @BindView(R.id.sOpenVideo)
    Switch sOpenVideo;

    @BindView(R.id.sbInnerVolume)
    SeekBar sbInnerVolume;

    @BindView(R.id.sbOutsideVolume)
    SeekBar sbOutsideVolume;
    private final String TAG = "NewspaperSetupFragment";
    private int lineProperty = 0;
    private String innerVolume = "";
    private String outsideVolume = "";
    private int stationSpeech = 0;
    private String iAngle = "";
    private String iExternalSound = "";
    private String iBroadcastDialect = "";
    private String iBroadcastEnglish = "";
    private String iNowTime = "";
    private String iOpenSpeeding = "";
    private String iOpenVideo = "";

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
        Log.d("NewspaperSetupFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_newspaper_setup, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("NewspaperSetupFragment", "onCreateView()");
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.basicSetupActivity = null;
        ((BasisSetPresenter) this.mvpPresenter).detachView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public BasisSetPresenter createPresenter() {
        return new BasisSetPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        String str = (String) SPUserInfoUtils.get(getActivity(), "InnerVolume", "7");
        this.innerVolume = str;
        this.sbInnerVolume.setProgress(Integer.valueOf(str).intValue());
        String str2 = (String) SPUserInfoUtils.get(getActivity(), "ExternalVolume", "7");
        this.outsideVolume = str2;
        this.sbOutsideVolume.setProgress(Integer.valueOf(str2).intValue());
        String str3 = (String) SPUserInfoUtils.get(getActivity(), "IfAngle", "否");
        this.iAngle = str3;
        if (str3.trim().equals("是")) {
            this.sAngle.setChecked(true);
        } else {
            this.sAngle.setChecked(false);
        }
        String str4 = (String) SPUserInfoUtils.get(getActivity(), "IfSound", "否");
        this.iExternalSound = str4;
        if (str4.trim().equals("是")) {
            this.sExternalSound.setChecked(true);
        } else {
            this.sExternalSound.setChecked(false);
        }
        String str5 = (String) SPUserInfoUtils.get(getActivity(), "IfDialect", "否");
        this.iBroadcastDialect = str5;
        if (str5.trim().equals("是")) {
            this.sBroadcastDialect.setChecked(true);
        } else {
            this.sBroadcastDialect.setChecked(false);
        }
        String str6 = (String) SPUserInfoUtils.get(getActivity(), "IfEnglish", "否");
        this.iBroadcastEnglish = str6;
        if (str6.trim().equals("是")) {
            this.sBroadcastEnglish.setChecked(true);
        } else {
            this.sBroadcastEnglish.setChecked(false);
        }
        String str7 = (String) SPUserInfoUtils.get(getActivity(), "IfSpeed", "否");
        this.iOpenSpeeding = str7;
        if (str7.trim().equals("是")) {
            this.sOpenSpeeding.setChecked(true);
        } else {
            this.sOpenSpeeding.setChecked(false);
        }
        String str8 = (String) SPUserInfoUtils.get(getActivity(), "IfOpenVideo", "否");
        this.iOpenVideo = str8;
        if (str8.trim().equals("是")) {
            this.sOpenVideo.setChecked(true);
        } else {
            this.sOpenVideo.setChecked(false);
        }
        String str9 = (String) SPUserInfoUtils.get(getActivity(), "IfIntegralPoint", "否");
        this.iNowTime = str9;
        if (str9.trim().equals("是")) {
            this.sNowTime.setChecked(true);
        } else {
            this.sNowTime.setChecked(false);
        }
        int iIntValue = ((Integer) SPUserInfoUtils.get(getActivity(), SPUserInfoUtils.LINEATTRIBUTE, 1)).intValue();
        this.lineProperty = iIntValue;
        if (iIntValue == 1) {
            this.rgLineProperty.check(R.id.rbUpAndDown);
        } else if (iIntValue == 2) {
            this.rgLineProperty.check(R.id.rbLoopLine);
        } else if (iIntValue == 3) {
            this.rgLineProperty.check(R.id.rbAntiReverse);
        }
        initEvent();
    }

    private void initEvent() {
        this.sbInnerVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NewspaperSetupFragment.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                NewspaperSetupFragment.this.innerVolume = String.valueOf(i);
            }
        });
        this.sbOutsideVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NewspaperSetupFragment.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                NewspaperSetupFragment.this.outsideVolume = String.valueOf(i);
            }
        });
        this.sAngle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$NewspaperSetupFragment$YaJgxtwoj4h5TEHTWoXe6HM_G7M
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initEvent$0$NewspaperSetupFragment(compoundButton, z);
            }
        });
        this.sExternalSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$NewspaperSetupFragment$zPQndWEbi15VXEtp-82CEr1PCzg
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initEvent$1$NewspaperSetupFragment(compoundButton, z);
            }
        });
        this.sBroadcastDialect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$NewspaperSetupFragment$q3cKRREzMpSzEe80u9k94gh8Rk0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initEvent$2$NewspaperSetupFragment(compoundButton, z);
            }
        });
        this.sBroadcastEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$NewspaperSetupFragment$1oqrOla7fsmR397GvKKFvVBA6BM
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initEvent$3$NewspaperSetupFragment(compoundButton, z);
            }
        });
        this.sOpenSpeeding.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$NewspaperSetupFragment$zexCfeCviJKvzRVOiiMEtGCBPMs
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initEvent$4$NewspaperSetupFragment(compoundButton, z);
            }
        });
        this.sOpenVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$NewspaperSetupFragment$Zo3I1VFoiyg3UdRonc0U6hY7lPw
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initEvent$5$NewspaperSetupFragment(compoundButton, z);
            }
        });
        this.sNowTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$NewspaperSetupFragment$KJSzh_YrMcnhxT2JLzqUgHQvIxk
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initEvent$6$NewspaperSetupFragment(compoundButton, z);
            }
        });
        this.rgLineProperty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$NewspaperSetupFragment$myzfQ2zY3XJUhWxYwL2T84sTo5E
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public final void onCheckedChanged(RadioGroup radioGroup, int i) {
                this.f$0.lambda$initEvent$7$NewspaperSetupFragment(radioGroup, i);
            }
        });
    }

    public /* synthetic */ void lambda$initEvent$0$NewspaperSetupFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.iAngle = "是";
        } else {
            this.iAngle = "否";
        }
    }

    public /* synthetic */ void lambda$initEvent$1$NewspaperSetupFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.iExternalSound = "是";
        } else {
            this.iExternalSound = "否";
        }
    }

    public /* synthetic */ void lambda$initEvent$2$NewspaperSetupFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.iBroadcastDialect = "是";
        } else {
            this.iBroadcastDialect = "否";
        }
    }

    public /* synthetic */ void lambda$initEvent$3$NewspaperSetupFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.iBroadcastEnglish = "是";
        } else {
            this.iBroadcastEnglish = "否";
        }
    }

    public /* synthetic */ void lambda$initEvent$4$NewspaperSetupFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.iOpenSpeeding = "是";
        } else {
            this.iOpenSpeeding = "否";
        }
    }

    public /* synthetic */ void lambda$initEvent$5$NewspaperSetupFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.iOpenVideo = "是";
        } else {
            this.iOpenVideo = "否";
        }
    }

    public /* synthetic */ void lambda$initEvent$6$NewspaperSetupFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.iNowTime = "是";
        } else {
            this.iNowTime = "否";
        }
    }

    public /* synthetic */ void lambda$initEvent$7$NewspaperSetupFragment(RadioGroup radioGroup, int i) {
        if (i == R.id.rbAntiReverse) {
            this.lineProperty = 3;
        } else if (i == R.id.rbLoopLine) {
            this.lineProperty = 2;
        } else {
            if (i != R.id.rbUpAndDown) {
                return;
            }
            this.lineProperty = 1;
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
            this.dialogConfirmView = new DialogConfirmView.Builder(getActivity()).setContent(getResources().getString(R.string.newspaper_ok_tip)).setOnOKClickListener(new DialogConfirmView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NewspaperSetupFragment.3
                @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                public void onOKClick() {
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), SPUserInfoUtils.LINEATTRIBUTE, Integer.valueOf(NewspaperSetupFragment.this.lineProperty));
                    BusLineInfoModel busLineInfoModel = new BusLineInfoModel();
                    busLineInfoModel.setAttribute(NewspaperSetupFragment.this.lineProperty);
                    busLineInfoModel.setLineName((String) SPUserInfoUtils.get(NewspaperSetupFragment.this.getActivity(), SPUserInfoUtils.BUSLINENAME, ""));
                    ((BasisSetPresenter) NewspaperSetupFragment.this.mvpPresenter).upBusLineInfoModel(busLineInfoModel);
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), "InnerVolume", NewspaperSetupFragment.this.innerVolume);
                    ConfigInfoModel configInfoModel = new ConfigInfoModel();
                    configInfoModel.setConfigItem("InnerVolume");
                    configInfoModel.setConfigValue(NewspaperSetupFragment.this.innerVolume);
                    ((BasisSetPresenter) NewspaperSetupFragment.this.mvpPresenter).upConfigData(configInfoModel);
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), "ExternalVolume", NewspaperSetupFragment.this.outsideVolume);
                    ConfigInfoModel configInfoModel2 = new ConfigInfoModel();
                    configInfoModel2.setConfigItem("ExternalVolume");
                    configInfoModel2.setConfigValue(NewspaperSetupFragment.this.outsideVolume);
                    ((BasisSetPresenter) NewspaperSetupFragment.this.mvpPresenter).upConfigData(configInfoModel2);
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), "IfAngle", NewspaperSetupFragment.this.iAngle);
                    ConfigInfoModel configInfoModel3 = new ConfigInfoModel();
                    configInfoModel3.setConfigItem("IfAngle");
                    configInfoModel3.setConfigValue(NewspaperSetupFragment.this.iAngle);
                    ((BasisSetPresenter) NewspaperSetupFragment.this.mvpPresenter).upConfigData(configInfoModel3);
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), "IfSound", NewspaperSetupFragment.this.iExternalSound);
                    ConfigInfoModel configInfoModel4 = new ConfigInfoModel();
                    configInfoModel4.setConfigItem("IfSound");
                    configInfoModel4.setConfigValue(NewspaperSetupFragment.this.iExternalSound);
                    ((BasisSetPresenter) NewspaperSetupFragment.this.mvpPresenter).upConfigData(configInfoModel4);
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), "IfDialect", NewspaperSetupFragment.this.iBroadcastDialect);
                    ConfigInfoModel configInfoModel5 = new ConfigInfoModel();
                    configInfoModel5.setConfigItem("IfDialect");
                    configInfoModel5.setConfigValue(NewspaperSetupFragment.this.iBroadcastDialect);
                    ((BasisSetPresenter) NewspaperSetupFragment.this.mvpPresenter).upConfigData(configInfoModel5);
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), "IfEnglish", NewspaperSetupFragment.this.iBroadcastEnglish);
                    ConfigInfoModel configInfoModel6 = new ConfigInfoModel();
                    configInfoModel6.setConfigItem("IfEnglish");
                    configInfoModel6.setConfigValue(NewspaperSetupFragment.this.iBroadcastEnglish);
                    ((BasisSetPresenter) NewspaperSetupFragment.this.mvpPresenter).upConfigData(configInfoModel6);
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), "IfSpeed", NewspaperSetupFragment.this.iOpenSpeeding);
                    ConfigInfoModel configInfoModel7 = new ConfigInfoModel();
                    configInfoModel7.setConfigItem("IfSpeed");
                    configInfoModel7.setConfigValue(NewspaperSetupFragment.this.iOpenSpeeding);
                    ((BasisSetPresenter) NewspaperSetupFragment.this.mvpPresenter).upConfigData(configInfoModel7);
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), "IfOpenVideo", NewspaperSetupFragment.this.iOpenVideo);
                    SPUserInfoUtils.put(NewspaperSetupFragment.this.getActivity(), "IfIntegralPoint", NewspaperSetupFragment.this.iNowTime);
                    ConfigInfoModel configInfoModel8 = new ConfigInfoModel();
                    configInfoModel8.setConfigItem("IfIntegralPoint");
                    configInfoModel8.setConfigValue(NewspaperSetupFragment.this.iNowTime);
                    ((BasisSetPresenter) NewspaperSetupFragment.this.mvpPresenter).upConfigData(configInfoModel8);
                    NewspaperSetupFragment.this.toastShow(R.string.newspaper_ok);
                    NewspaperSetupFragment.this.dialogConfirmView.dismiss();
                }

                @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                public void onCancelClick() {
                    NewspaperSetupFragment.this.dialogConfirmView.dismiss();
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
    }
}
