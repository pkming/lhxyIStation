package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
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
public class OtherSetupFragment extends MvpFragment<BasisSetView, BasisSetPresenter> implements BasisSetView<String> {
    private BasicSetupActivity basicSetupActivity;

    @BindView(R.id.butAffirm)
    Button butAffirm;
    private volatile DialogConfirmView dialogConfirmView;

    @BindView(R.id.sbDispatchVolume)
    SeekBar sbDispatchVolume;

    @BindView(R.id.sbShoutingVolume)
    SeekBar sbShoutingVolume;
    private final String TAG = "NewspaperSetupFragment";
    private String shoutVolume = "50";
    private String dispatchVolume = "7";

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
        View viewInflate = layoutInflater.inflate(R.layout.f_other_setup, viewGroup, false);
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
        ((BasisSetPresenter) this.mvpPresenter).detachView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public BasisSetPresenter createPresenter() {
        return new BasisSetPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        String str = (String) SPUserInfoUtils.get(getActivity(), "ShoutingVolume", "50");
        this.shoutVolume = str;
        this.sbShoutingVolume.setProgress(Integer.valueOf(str).intValue());
        String str2 = (String) SPUserInfoUtils.get(getActivity(), "DispatchVolume", "7");
        this.dispatchVolume = str2;
        this.sbDispatchVolume.setProgress(Integer.valueOf(str2).intValue());
        initEvent();
    }

    private void initEvent() {
        this.sbShoutingVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.OtherSetupFragment.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                OtherSetupFragment.this.shoutVolume = "" + i;
            }
        });
        this.sbDispatchVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.OtherSetupFragment.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                OtherSetupFragment.this.dispatchVolume = "" + i;
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
            this.dialogConfirmView = new DialogConfirmView.Builder(getActivity()).setContent(getResources().getString(R.string.other_ok_tip)).setOnOKClickListener(new DialogConfirmView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.OtherSetupFragment.3
                @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                public void onOKClick() {
                    SPUserInfoUtils.put(OtherSetupFragment.this.getActivity(), "ShoutingVolume", OtherSetupFragment.this.shoutVolume);
                    ConfigInfoModel configInfoModel = new ConfigInfoModel();
                    configInfoModel.setConfigItem("ShoutingVolume");
                    configInfoModel.setConfigValue(OtherSetupFragment.this.shoutVolume);
                    ((BasisSetPresenter) OtherSetupFragment.this.mvpPresenter).upConfigData(configInfoModel);
                    SPUserInfoUtils.put(OtherSetupFragment.this.getActivity(), "DispatchVolume", OtherSetupFragment.this.dispatchVolume);
                    ConfigInfoModel configInfoModel2 = new ConfigInfoModel();
                    configInfoModel2.setConfigItem("DispatchVolume");
                    configInfoModel2.setConfigValue(OtherSetupFragment.this.dispatchVolume);
                    ((BasisSetPresenter) OtherSetupFragment.this.mvpPresenter).upConfigData(configInfoModel2);
                    OtherSetupFragment.this.toastShow(R.string.newspaper_ok);
                    OtherSetupFragment.this.dialogConfirmView.dismiss();
                }

                @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                public void onCancelClick() {
                    OtherSetupFragment.this.dialogConfirmView.dismiss();
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
