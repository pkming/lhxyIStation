package com.lianhexinye.m90.ui.fragment.systeminfo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.ui.activity.SystemInfoActivity;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class VersionInfoFragment extends MvpFragment<MainView, MainPresenter> implements MainView {
    private final String TAG = "VersionInfoFragment";
    private SystemInfoActivity systemInfoActivity;

    @BindView(R.id.tvDataVersionCode)
    TextView tvDataVersionCode;

    @BindView(R.id.tvSVersionCode)
    TextView tvSVersionCode;

    @BindView(R.id.tvSourceVersionTime)
    TextView tvSourceVersionTime;

    @BindView(R.id.tvVersionCode)
    TextView tvVersionCode;

    @BindView(R.id.tvVersionName)
    TextView tvVersionName;

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void authorizationResult(int i, String str) {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getDataSuccess() {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getLineNameDataSuccess2(List<LineNameModel> list) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, com.lianhexinye.m90.mvp.BaseFragment
    public void hide() {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void hideLoading() {
    }

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, com.lianhexinye.m90.mvp.BaseFragment
    public void show() {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void showLoading() {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void showLoading(String str) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SystemInfoActivity) {
            this.systemInfoActivity = (SystemInfoActivity) activity;
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("VersionInfoFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_versioninfo, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("VersionInfoFragment", "onCreateView()");
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.systemInfoActivity = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        String string = getResources().getString(R.string.version_hardware);
        this.tvSVersionCode.setText(String.format(string, "MLXY-Pg02-ALAHD68FV18"));
        this.tvVersionCode.setText(String.format(getResources().getString(R.string.version_software_number), "" + AndroidUtils.getLocalVersion()));
        this.tvVersionName.setText(String.format(getResources().getString(R.string.version_software_name), "" + AndroidUtils.getLocalVersionName()));
        this.tvDataVersionCode.setText(String.format(string, "" + GreenDaoUtils.getSingleTon().getDb().getVersion()));
        this.tvSourceVersionTime.setText(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.SOURCEFILELASTMODIFYTIME, "2000-01-01 00:00:00").toString());
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }
}
