package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.widget.DialogConfirmView;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.basisset.BasisSetPresenter;
import com.lianhexinye.m90.mvp.basisset.BasisSetView;
import com.lianhexinye.m90.ui.activity.BasicSetupActivity;

/* JADX INFO: loaded from: classes2.dex */
public class WirelessSetupFragment extends MvpFragment<BasisSetView, BasisSetPresenter> implements BasisSetView<String> {
    private final String TAG = "WirelessSetupFragment";
    private BasicSetupActivity basicSetupActivity;
    private volatile DialogConfirmView dialogConfirmView;

    @BindView(R.id.rlGotoSystemUp)
    RelativeLayout rlGotoSystemUp;

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

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
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
        Log.d("WirelessSetupFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_wireless_setup, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("WirelessSetupFragment", "onCreateView()");
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

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @OnClick({R.id.rlGotoSystemUp})
    public void onViewClicked() {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }
}
