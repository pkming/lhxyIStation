package com.lianhexinye.m90.ui.fragment.dispatch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.MaintenanceModel;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.serialport.SerialPortManager;
import com.lianhexinye.m90.socket.SocketManage;
import com.lianhexinye.m90.socket.request.Generate808ReqPackage;
import com.lianhexinye.m90.ui.activity.DispatchCenterActivity;
import com.lianhexinye.m90.ui.adapter.MaintenanceChoiceAdapter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class LedPeripheralFragment extends MvpFragment<MainView, MainPresenter> implements MainView {

    @BindView(R.id.butSendLedPeripheral)
    Button butSendLedPeripheral;
    private DispatchCenterActivity dispatchCenterActivity;

    @BindView(R.id.lvInfoLedPeripheral)
    ListView lvInfoLedPeripheral;

    @BindView(R.id.lyNotInfoLedPeripheral)
    LinearLayout lyNotInfoLedPeripheral;
    private MaintenanceChoiceAdapter maintenanceChoiceAdapter;
    private MaintenanceModel maintenanceModelSend;
    private List<MaintenanceModel> maintenanceModels;

    @BindView(R.id.rlSendLedPeripheral)
    RelativeLayout rlSendLedPeripheral;

    @BindView(R.id.rlToolbarLedPeripheral)
    RelativeLayout rlToolbarLedPeripheral;

    @BindView(R.id.tvInfoLedPeripheralTip)
    TextView tvInfoLedPeripheralTip;

    @BindView(R.id.vInfoLedPeripheral)
    View vInfoLedPeripheral;
    private final String TAG = "LedPeripheralFragment";
    private SerialPortManager serialPortManager = new SerialPortManager();

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

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
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
        setHasOptionsMenu(true);
        Log.d("LedPeripheralFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_led_peripheral, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("LedPeripheralFragment", "onCreateView()");
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.dispatchCenterActivity = null;
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        List<MaintenanceModel> listQueryArrayMaintenance = ((MainPresenter) this.mvpPresenter).queryArrayMaintenance();
        if (listQueryArrayMaintenance != null && listQueryArrayMaintenance.size() > 0) {
            Iterator<MaintenanceModel> it = listQueryArrayMaintenance.iterator();
            while (it.hasNext()) {
                it.next().setChecked(false);
            }
            this.lvInfoLedPeripheral.setVisibility(0);
            this.lyNotInfoLedPeripheral.setVisibility(8);
            this.rlSendLedPeripheral.setVisibility(0);
            this.maintenanceModels = listQueryArrayMaintenance;
            MaintenanceChoiceAdapter maintenanceChoiceAdapter = new MaintenanceChoiceAdapter(getContext(), this.maintenanceModels);
            this.maintenanceChoiceAdapter = maintenanceChoiceAdapter;
            this.lvInfoLedPeripheral.setAdapter((ListAdapter) maintenanceChoiceAdapter);
        } else {
            this.lvInfoLedPeripheral.setVisibility(8);
            this.lyNotInfoLedPeripheral.setVisibility(0);
            this.rlSendLedPeripheral.setVisibility(8);
        }
        this.lvInfoLedPeripheral.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.LedPeripheralFragment.1
            int currentNum = -1;

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Iterator it2 = LedPeripheralFragment.this.maintenanceModels.iterator();
                while (it2.hasNext()) {
                    ((MaintenanceModel) it2.next()).setChecked(false);
                }
                int i2 = this.currentNum;
                if (i2 == -1) {
                    ((MaintenanceModel) LedPeripheralFragment.this.maintenanceModels.get(i)).setChecked(true);
                    LedPeripheralFragment ledPeripheralFragment = LedPeripheralFragment.this;
                    ledPeripheralFragment.maintenanceModelSend = (MaintenanceModel) ledPeripheralFragment.maintenanceModels.get(i);
                    this.currentNum = i;
                } else if (i2 == i) {
                    LedPeripheralFragment.this.maintenanceModelSend = null;
                    this.currentNum = -1;
                } else if (i2 != i) {
                    ((MaintenanceModel) LedPeripheralFragment.this.maintenanceModels.get(i)).setChecked(true);
                    this.currentNum = i;
                    LedPeripheralFragment ledPeripheralFragment2 = LedPeripheralFragment.this;
                    ledPeripheralFragment2.maintenanceModelSend = (MaintenanceModel) ledPeripheralFragment2.maintenanceModels.get(i);
                }
                LedPeripheralFragment.this.maintenanceChoiceAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick({R.id.butSendLedPeripheral})
    public void onViewClicked() {
        if (this.maintenanceModelSend != null) {
            toastShow("已发送！");
            if (AppApplication.mSerial485Control != null && AppApplication.mSerial485Control.isOpen()) {
                AppApplication.send485Thread.send(this.serialPortManager.crateTongdaProtocol().createLedAdvInfo(this.maintenanceModels));
            }
            LogUtils.d("LedPeripheralFragment", this.maintenanceModelSend.getContent());
            ReportInfoModel reportInfoModel = new ReportInfoModel();
            reportInfoModel.setLineNumber(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, "").toString());
            reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
            reportInfoModel.setTerminalDate(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
            String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DRIVERCARDID, "").toString();
            reportInfoModel.setContent(this.maintenanceModelSend.getContent());
            if (string.length() > 0) {
                reportInfoModel.setCardNo(string.substring(10, 18));
            } else {
                reportInfoModel.setCardNo("00000000");
            }
            SocketManage.getInstance().sendReq(Generate808ReqPackage.generateRequestMessage(reportInfoModel));
            return;
        }
        toastShow("请勾选需要发送的信息");
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.del || menuItem.getItemId() != R.id.index) {
            return true;
        }
        DispatchCenterActivity dispatchCenterActivity = this.dispatchCenterActivity;
        getActivity();
        dispatchCenterActivity.setResult(-1);
        this.dispatchCenterActivity.finish();
        return true;
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
