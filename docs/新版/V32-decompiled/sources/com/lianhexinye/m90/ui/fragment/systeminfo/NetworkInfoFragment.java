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
import com.lianhexinye.m90.common.service.advert.NotifyResult;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.retrofit.ApiManager;
import com.lianhexinye.m90.ui.activity.SystemInfoActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes2.dex */
public class NetworkInfoFragment extends MvpFragment<MainView, MainPresenter> implements MainView {
    private final String TAG = "NetworkInfoFragment";
    private Call<NotifyResult> resultCall;
    private SystemInfoActivity systemInfoActivity;

    @BindView(R.id.tv4GState)
    TextView tv4GState;

    @BindView(R.id.tvGpsState)
    TextView tvGpsState;

    @BindView(R.id.tvLanState)
    TextView tvLanState;

    @BindView(R.id.tvServerIP1)
    TextView tvServerIP1;

    @BindView(R.id.tvServerIP2)
    TextView tvServerIP2;

    @BindView(R.id.tvServerState1)
    TextView tvServerState1;

    @BindView(R.id.tvServerState2)
    TextView tvServerState2;

    @BindView(R.id.tvWifiIP)
    TextView tvWifiIP;

    @BindView(R.id.tvWifiState)
    TextView tvWifiState;

    @BindView(R.id.tvWiredIP)
    TextView tvWiredIP;

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
        Log.d("NetworkInfoFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_networkinfo, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("NetworkInfoFragment", "onCreateView()");
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        Call<NotifyResult> call = this.resultCall;
        if (call != null) {
            call.cancel();
            this.resultCall = null;
        }
        this.systemInfoActivity = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        this.tvServerIP1.setText((String) SPUserInfoUtils.get(getActivity(), "NetDispatchIP", "39.104.66.194"));
        this.tvServerState1.setText(AppApplication.cmsState);
        this.tvServerIP2.setText(ApiManager.SERVER_PRODUCT.substring(ApiManager.SERVER_PRODUCT.indexOf("//") + 2, ApiManager.SERVER_PRODUCT.lastIndexOf(":")));
        this.tvGpsState.setText(AppApplication.gpsState);
        this.tvWifiState.setText(AppApplication.wifiState);
        if (AndroidUtils.isNetConnected()) {
            new AnonymousClass1().start();
        } else {
            this.tvServerState2.setText(getResources().getString(R.string.unconnected));
        }
        this.tv4GState.setText(AppApplication.simState);
        if (AndroidUtils.isNetworkAvailable() == 1) {
            this.tvWiredIP.setText(AndroidUtils.getEtherNetIp());
            this.tvLanState.setText(getResources().getString(R.string.connected));
        } else {
            this.tvLanState.setText(getResources().getString(R.string.unconnected));
        }
    }

    /* JADX INFO: renamed from: com.lianhexinye.m90.ui.fragment.systeminfo.NetworkInfoFragment$1, reason: invalid class name */
    class AnonymousClass1 extends Thread {
        AnonymousClass1() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            NetworkInfoFragment.this.resultCall = ApiManager.apiManager.getNotify(AndroidUtils.getMacAddress());
            NetworkInfoFragment.this.resultCall.enqueue(new Callback<NotifyResult>() { // from class: com.lianhexinye.m90.ui.fragment.systeminfo.NetworkInfoFragment.1.1
                @Override // retrofit2.Callback
                public void onResponse(Call<NotifyResult> call, Response<NotifyResult> response) {
                    if (NetworkInfoFragment.this.tvServerState2 != null) {
                        NetworkInfoFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.fragment.systeminfo.NetworkInfoFragment.1.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (NetworkInfoFragment.this.tvServerState2 != null) {
                                    NetworkInfoFragment.this.tvServerState2.setText(NetworkInfoFragment.this.getResources().getString(R.string.connected));
                                }
                            }
                        });
                    }
                }

                @Override // retrofit2.Callback
                public void onFailure(Call<NotifyResult> call, Throwable th) {
                    if (NetworkInfoFragment.this.tvServerState2 != null) {
                        NetworkInfoFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.fragment.systeminfo.NetworkInfoFragment.1.1.2
                            @Override // java.lang.Runnable
                            public void run() {
                                if (NetworkInfoFragment.this.tvServerState2 != null) {
                                    NetworkInfoFragment.this.tvServerState2.setText(NetworkInfoFragment.this.getResources().getString(R.string.unconnected));
                                }
                            }
                        });
                    }
                }
            });
        }
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
