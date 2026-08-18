package com.lianhexinye.m90.ui.fragment.dispatch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.gps.GPSMonitor;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.socket.SocketManage;
import com.lianhexinye.m90.socket.request.Generate808ReqPackage;
import com.lianhexinye.m90.ui.activity.DispatchCenterActivity;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class DispatchFragment extends MvpFragment<MainView, MainPresenter> implements MainView {
    private final String TAG = "DispatchFragment";

    @BindView(R.id.butExitOperation)
    Button butExitOperation;

    @BindView(R.id.butIntercom)
    Button butIntercom;

    @BindView(R.id.butManualEnd)
    Button butManualEnd;

    @BindView(R.id.butManualStart)
    Button butManualStart;

    @BindView(R.id.butOtherRequests)
    Button butOtherRequests;

    @BindView(R.id.butRequestAerate)
    Button butRequestAerate;

    @BindView(R.id.butRequestCharge)
    Button butRequestCharge;

    @BindView(R.id.butRequestCharter)
    Button butRequestCharter;

    @BindView(R.id.butRequestHandover)
    Button butRequestHandover;

    @BindView(R.id.butRequestOil)
    Button butRequestOil;

    @BindView(R.id.butRequestRepair)
    Button butRequestRepair;

    @BindView(R.id.butRequestSchedule)
    Button butRequestSchedule;
    private DispatchCenterActivity dispatchCenterActivity;
    private String latitudeTmp;
    private String longitudeTmp;

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

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
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
        if (activity instanceof DispatchCenterActivity) {
            this.dispatchCenterActivity = (DispatchCenterActivity) activity;
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("DispatchFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_dispatch, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("DispatchFragment", "onCreateView()");
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @OnClick({R.id.butRequestSchedule, R.id.butRequestOil, R.id.butRequestAerate, R.id.butRequestCharge, R.id.butExitOperation, R.id.butManualStart, R.id.butManualEnd, R.id.butRequestCharter, R.id.butRequestRepair, R.id.butOtherRequests, R.id.butIntercom})
    public void onViewClicked(View view) {
        ReportInfoModel reportInfoModel = new ReportInfoModel();
        int id = view.getId();
        if (id == R.id.butExitOperation) {
            reportInfoModel.setProfessionRequestType(6);
        } else if (id == R.id.butIntercom) {
            reportInfoModel.setProfessionRequestType(13);
        } else if (id != R.id.butOtherRequests) {
            switch (id) {
                case R.id.butManualEnd /* 2131296352 */:
                    reportInfoModel.setProfessionRequestType(8);
                    break;
                case R.id.butManualStart /* 2131296353 */:
                    reportInfoModel.setProfessionRequestType(7);
                    break;
                default:
                    switch (id) {
                        case R.id.butRequestAerate /* 2131296371 */:
                            reportInfoModel.setProfessionRequestType(4);
                            break;
                        case R.id.butRequestCharge /* 2131296372 */:
                            reportInfoModel.setProfessionRequestType(5);
                            break;
                        case R.id.butRequestCharter /* 2131296373 */:
                            reportInfoModel.setProfessionRequestType(9);
                            break;
                        case R.id.butRequestHandover /* 2131296374 */:
                            reportInfoModel.setProfessionRequestType(2);
                            break;
                        case R.id.butRequestOil /* 2131296375 */:
                            reportInfoModel.setProfessionRequestType(3);
                            break;
                        case R.id.butRequestRepair /* 2131296376 */:
                            reportInfoModel.setProfessionRequestType(10);
                            break;
                        case R.id.butRequestSchedule /* 2131296377 */:
                            reportInfoModel.setProfessionRequestType(1);
                            break;
                    }
                    break;
            }
        } else {
            reportInfoModel.setProfessionRequestType(11);
        }
        reportInfoModel.setTransmissionType("81");
        reportInfoModel.setLineNumber(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, "").toString());
        String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DRIVERCARDID, "").toString();
        LogUtils.v("DispatchFragment", "read driverCardId：" + string);
        if (string.length() > 0) {
            reportInfoModel.setCardNo(string.substring(8, 16));
        } else {
            reportInfoModel.setCardNo("00000000");
        }
        reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
        reportInfoModel.setTerminalDate(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
        if (GPSMonitor.bValidData && GPSMonitor.wszLatitude != null && !GPSMonitor.wszLatitude.trim().equals("") && GPSMonitor.wszLongitude != null && !GPSMonitor.wszLongitude.trim().equals("")) {
            this.latitudeTmp = GPSMonitor.wszLatitude;
            LogUtils.d("DispatchFragment", "langitudeTmp:" + this.latitudeTmp);
            String[] strArrSplit = String.valueOf(Double.valueOf(this.latitudeTmp).doubleValue() / 100.0d).split("\\.");
            if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                reportInfoModel.setLatitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue())));
                LogUtils.d("DispatchFragment", "latValue:" + reportInfoModel.getLatitude());
                this.longitudeTmp = GPSMonitor.wszLongitude;
                LogUtils.d("DispatchFragment", "longitudeTmp:" + this.longitudeTmp);
                String[] strArrSplit2 = String.valueOf(Double.valueOf(this.longitudeTmp).doubleValue() / 100.0d).split("\\.");
                if (strArrSplit2.length > 1 && strArrSplit2[1].length() > 2) {
                    reportInfoModel.setLongitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue())));
                    LogUtils.d("DispatchFragment", "longValue:" + reportInfoModel.getLongitude());
                } else {
                    reportInfoModel.setLongitude("0");
                    reportInfoModel.setLatitude("0");
                }
            } else {
                reportInfoModel.setLongitude("0");
                reportInfoModel.setLatitude("0");
            }
        } else {
            reportInfoModel.setLongitude("0");
            reportInfoModel.setLatitude("0");
        }
        SocketManage.getInstance().sendReq(Generate808ReqPackage.generateProfessionRequest(reportInfoModel));
        toastShow("已发送！");
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.dispatchCenterActivity = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
    }
}
