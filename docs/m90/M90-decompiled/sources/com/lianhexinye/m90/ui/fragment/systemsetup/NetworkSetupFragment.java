package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.widget.CompanyEdittext;
import com.lianhexinye.m90.common.widget.DialogConfirmView;
import com.lianhexinye.m90.common.widget.DialogloadView;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.basisset.BasisSetPresenter;
import com.lianhexinye.m90.mvp.basisset.BasisSetView;
import com.lianhexinye.m90.retrofit.request.RegisterReqBody;
import com.lianhexinye.m90.socket.SocketManage;
import com.lianhexinye.m90.ui.activity.BasicSetupActivity;

/* JADX INFO: loaded from: classes2.dex */
public class NetworkSetupFragment extends MvpFragment<BasisSetView, BasisSetPresenter> implements BasisSetView<String> {
    private BasicSetupActivity basicSetupActivity;

    @BindView(R.id.butNetWorkAffirm)
    Button butNetWorkAffirm;
    private volatile DialogConfirmView dialogConfirmView;
    private DialogloadView dialogloadView;

    @BindView(R.id.etAdwordsID)
    EditText etAdwordsID;

    @BindView(R.id.etAdwordsIP)
    EditText etAdwordsIP;

    @BindView(R.id.etAdwordsInterval)
    CompanyEdittext etAdwordsInterval;

    @BindView(R.id.etAdwordsPort)
    EditText etAdwordsPort;

    @BindView(R.id.etAdwordsUser)
    EditText etAdwordsUser;

    @BindView(R.id.etDispatchID)
    EditText etDispatchID;

    @BindView(R.id.etDispatchIP)
    EditText etDispatchIP;

    @BindView(R.id.etDispatchPort)
    EditText etDispatchPort;

    @BindView(R.id.etInfoInterval)
    CompanyEdittext etInfoInterval;

    @BindView(R.id.etLongInterval)
    CompanyEdittext etLongInterval;

    @BindView(R.id.etSpeedingInterval)
    CompanyEdittext etSpeedingInterval;

    @BindView(R.id.sAdwordsSwitch)
    Switch sAdwordsSwitch;

    @BindView(R.id.spDispatch)
    Spinner spDispatch;
    private final String TAG = "NetworkSetupFragment";
    private String protocol = "无";
    private String iNetAdvert = "是";

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void getDataSuccess(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, com.lianhexinye.m90.mvp.BaseFragment
    public void hide() {
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
        Log.d("NetworkSetupFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_network, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("NetworkSetupFragment", "onCreateView()");
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
        this.etAdwordsIP.setInputType(2);
        this.etAdwordsIP.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        this.etDispatchID.setText(String.valueOf(SPUserInfoUtils.get(getActivity(), "NetDispatchID", "")));
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource = ArrayAdapter.createFromResource(getActivity(), R.array.arrayprotocoldispatch, R.layout.spinner_item);
        arrayAdapterCreateFromResource.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spDispatch.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource);
        int count = arrayAdapterCreateFromResource.getCount();
        this.protocol = (String) SPUserInfoUtils.get(getActivity(), "NetDispatchProtocol", "无");
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            if (this.protocol.equals(arrayAdapterCreateFromResource.getItem(i).toString())) {
                this.spDispatch.setSelection(i, true);
                break;
            }
            i++;
        }
        this.spDispatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j) {
                NetworkSetupFragment.this.protocol = adapterView.getItemAtPosition(i2).toString();
            }
        });
        String strValueOf = String.valueOf(SPUserInfoUtils.get(getActivity(), "NetDispatchIP", "211.154.159.34"));
        EditText editText = this.etDispatchIP;
        if (strValueOf.trim().length() <= 0) {
            strValueOf = "39.104.66.194";
        }
        editText.setText(strValueOf);
        String strValueOf2 = String.valueOf(SPUserInfoUtils.get(getActivity(), "NetDispatchPort", "7000"));
        EditText editText2 = this.etDispatchPort;
        if (strValueOf2.trim().length() <= 0) {
            strValueOf2 = "8081";
        }
        editText2.setText(strValueOf2);
        this.etLongInterval.setText(String.valueOf(SPUserInfoUtils.get(getActivity(), "NetDdispatchHeartbeatInterval", "15")));
        this.etInfoInterval.setText(String.valueOf(SPUserInfoUtils.get(getActivity(), "NetDispatchInfoInterval", "5")));
        this.etSpeedingInterval.setText(String.valueOf(SPUserInfoUtils.get(getActivity(), "NetDispatchSpeedingInterval", "10")));
        this.etAdwordsID.setText(String.valueOf(SPUserInfoUtils.get(getActivity(), "NetAdvertID", "")));
        this.etAdwordsUser.setText(String.valueOf(SPUserInfoUtils.get(getActivity(), "NetAdvertUser", "admin")));
        String strValueOf3 = String.valueOf(SPUserInfoUtils.get(getActivity(), "NetAdvertIP", "39.104.66.194"));
        this.etAdwordsIP.setText(strValueOf3.trim().length() > 0 ? strValueOf3 : "39.104.66.194");
        String strValueOf4 = String.valueOf(SPUserInfoUtils.get(getActivity(), "NetAdvertPort", "8081"));
        this.etAdwordsPort.setText(strValueOf4.trim().length() > 0 ? strValueOf4 : "8081");
        this.etAdwordsInterval.setText(String.valueOf(SPUserInfoUtils.get(getActivity(), "NetAdvertInterfaceInterval", "10")));
        String str = (String) SPUserInfoUtils.get(getActivity(), "IfNetAdvert", "否");
        this.iNetAdvert = str;
        if (str.trim().equals("是")) {
            this.sAdwordsSwitch.setChecked(true);
        } else {
            this.sAdwordsSwitch.setChecked(false);
        }
        this.sAdwordsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.-$$Lambda$NetworkSetupFragment$vaGM3H7D6kFNusDwe_ERlM3Lz1U
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initView$0$NetworkSetupFragment(compoundButton, z);
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$NetworkSetupFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.iNetAdvert = "是";
        } else {
            this.iNetAdvert = "否";
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @OnClick({R.id.butNetWorkAffirm})
    public void onViewClicked() {
        if (JavaUtils.isEmpty(this.etLongInterval.getText()) || JavaUtils.isEmpty(this.etAdwordsIP.getText()) || JavaUtils.isEmpty(this.etAdwordsPort.getText()) || JavaUtils.isEmpty(this.etAdwordsInterval.getText()) || JavaUtils.isEmpty(this.etDispatchIP.getText()) || JavaUtils.isEmpty(this.etDispatchPort.getText()) || JavaUtils.isEmpty(this.etInfoInterval.getText())) {
            toastShow(R.string.network_errer_tip);
            return;
        }
        String str = (String) SPUserInfoUtils.get(AppApplication.getInstance(), SPUserInfoUtils.BUSDIRECTIONNAME, "");
        if (str != null && !str.equals("")) {
            this.dialogConfirmView = new DialogConfirmView.Builder(getActivity()).setButOkTxt(getResources().getString(R.string.restart)).setContent(getResources().getString(R.string.network_dialog_tip)).setOnOKClickListener(new AnonymousClass2()).build();
            this.dialogConfirmView.show();
        } else {
            toastShow("请先导入报站器资源.");
        }
    }

    /* JADX INFO: renamed from: com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment$2, reason: invalid class name */
    class AnonymousClass2 implements DialogConfirmView.OnOKCancelClickListener {
        AnonymousClass2() {
        }

        /* JADX WARN: Type inference failed for: r0v33, types: [com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment$2$1] */
        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
        public void onOKClick() {
            boolean z;
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetDispatchID", NetworkSetupFragment.this.etDispatchID.getText().toString());
            ConfigInfoModel configInfoModel = new ConfigInfoModel();
            configInfoModel.setConfigItem("NetDispatchID");
            configInfoModel.setConfigValue(NetworkSetupFragment.this.etDispatchID.getText().toString());
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetDispatchIP", NetworkSetupFragment.this.etDispatchIP.getText().toString());
            ConfigInfoModel configInfoModel2 = new ConfigInfoModel();
            configInfoModel2.setConfigItem("NetDispatchIP");
            configInfoModel2.setConfigValue(NetworkSetupFragment.this.etDispatchIP.getText().toString());
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel2);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetDispatchPort", NetworkSetupFragment.this.etDispatchPort.getText().toString());
            ConfigInfoModel configInfoModel3 = new ConfigInfoModel();
            configInfoModel3.setConfigItem("NetDispatchPort");
            configInfoModel3.setConfigValue(NetworkSetupFragment.this.etDispatchPort.getText().toString());
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel3);
            String strSubstring = NetworkSetupFragment.this.etLongInterval.getText().toString().substring(0, NetworkSetupFragment.this.etLongInterval.getText().toString().trim().length() - 1);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetDdispatchHeartbeatInterval", strSubstring);
            ConfigInfoModel configInfoModel4 = new ConfigInfoModel();
            configInfoModel4.setConfigItem("NetDdispatchHeartbeatInterval");
            configInfoModel4.setConfigValue(strSubstring);
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel4);
            String strSubstring2 = NetworkSetupFragment.this.etInfoInterval.getText().toString().substring(0, NetworkSetupFragment.this.etInfoInterval.getText().toString().trim().length() - 1);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetDispatchInfoInterval", strSubstring2);
            ConfigInfoModel configInfoModel5 = new ConfigInfoModel();
            configInfoModel5.setConfigItem("NetDispatchInfoInterval");
            configInfoModel5.setConfigValue(strSubstring2);
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel5);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetDispatchSpeedingInterval", NetworkSetupFragment.this.etSpeedingInterval.getText().toString().substring(0, NetworkSetupFragment.this.etSpeedingInterval.getText().toString().trim().length() - 1));
            ConfigInfoModel configInfoModel6 = new ConfigInfoModel();
            configInfoModel6.setConfigItem("NetDispatchSpeedingInterval");
            configInfoModel6.setConfigValue(strSubstring2);
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel6);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetDispatchProtocol", NetworkSetupFragment.this.protocol);
            ConfigInfoModel configInfoModel7 = new ConfigInfoModel();
            configInfoModel7.setConfigItem("NetDispatchProtocol");
            configInfoModel7.setConfigValue(NetworkSetupFragment.this.protocol);
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel7);
            if (!NetworkSetupFragment.this.iNetAdvert.equals("是") || JavaUtils.isEmpty(NetworkSetupFragment.this.etAdwordsID.getText().toString())) {
                z = true;
            } else {
                RegisterReqBody registerReqBody = new RegisterReqBody();
                if (!((String) SPUserInfoUtils.get(NetworkSetupFragment.this.getActivity(), "NetAdvertID", "")).equals(NetworkSetupFragment.this.etAdwordsID.getText().toString())) {
                    registerReqBody.setIp(AndroidUtils.getIP());
                    registerReqBody.setKey(AndroidUtils.getMacAddress());
                    registerReqBody.setMac(AndroidUtils.getMacAddress());
                    registerReqBody.setOs("Android");
                    registerReqBody.setName(NetworkSetupFragment.this.etAdwordsID.getText().toString());
                    registerReqBody.setUName(NetworkSetupFragment.this.etAdwordsUser.getText().toString());
                    if (JavaUtils.isEmpty(NetworkSetupFragment.this.etAdwordsUser.getText().toString())) {
                        registerReqBody.setUName("admin");
                    }
                    ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).register(registerReqBody);
                } else {
                    if (!((String) SPUserInfoUtils.get(NetworkSetupFragment.this.getActivity(), "NetAdvertUser", "admin")).equals(NetworkSetupFragment.this.etAdwordsUser.getText().toString())) {
                        registerReqBody.setIp(AndroidUtils.getIP());
                        registerReqBody.setKey(AndroidUtils.getMacAddress());
                        registerReqBody.setMac(AndroidUtils.getMacAddress());
                        registerReqBody.setOs("Android");
                        registerReqBody.setName(NetworkSetupFragment.this.etAdwordsID.getText().toString());
                        registerReqBody.setUName(NetworkSetupFragment.this.etAdwordsUser.getText().toString());
                        if (JavaUtils.isEmpty(NetworkSetupFragment.this.etAdwordsUser.getText().toString())) {
                            registerReqBody.setUName("admin");
                        }
                        ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).register(registerReqBody);
                    }
                    z = true;
                }
                z = false;
            }
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "IfNetAdvert", NetworkSetupFragment.this.iNetAdvert);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetAdvertID", NetworkSetupFragment.this.etAdwordsID.getText().toString());
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetAdvertUser", NetworkSetupFragment.this.etAdwordsUser.getText().toString());
            ConfigInfoModel configInfoModel8 = new ConfigInfoModel();
            configInfoModel8.setConfigItem("IfNetAdvert");
            configInfoModel8.setConfigValue(NetworkSetupFragment.this.iNetAdvert);
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel8);
            ConfigInfoModel configInfoModel9 = new ConfigInfoModel();
            configInfoModel9.setConfigItem("NetAdvertUser");
            configInfoModel9.setConfigValue(NetworkSetupFragment.this.etAdwordsUser.getText().toString());
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel9);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetAdvertIP", NetworkSetupFragment.this.etAdwordsIP.getText().toString());
            ConfigInfoModel configInfoModel10 = new ConfigInfoModel();
            configInfoModel10.setConfigItem("NetAdvertIP");
            configInfoModel10.setConfigValue(NetworkSetupFragment.this.etAdwordsIP.getText().toString());
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel10);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetAdvertPort", NetworkSetupFragment.this.etAdwordsPort.getText().toString());
            ConfigInfoModel configInfoModel11 = new ConfigInfoModel();
            configInfoModel11.setConfigItem("NetAdvertPort");
            configInfoModel11.setConfigValue(NetworkSetupFragment.this.etAdwordsPort.getText().toString());
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel11);
            String strSubstring3 = NetworkSetupFragment.this.etAdwordsInterval.getText().toString().substring(0, NetworkSetupFragment.this.etAdwordsInterval.getText().toString().trim().length() - 1);
            SPUserInfoUtils.put(NetworkSetupFragment.this.getActivity(), "NetAdvertInterfaceInterval", strSubstring3);
            ConfigInfoModel configInfoModel12 = new ConfigInfoModel();
            configInfoModel12.setConfigItem("NetAdvertInterfaceInterval");
            configInfoModel12.setConfigValue(strSubstring3);
            ((BasisSetPresenter) NetworkSetupFragment.this.mvpPresenter).upConfigData(configInfoModel12);
            NetworkSetupFragment.this.dialogConfirmView.dismiss();
            if (z) {
                new Thread() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment.2.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        super.run();
                        SocketManage.getInstance().closeAllSocket();
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        NetworkSetupFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment.2.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                FragmentActivity activity = NetworkSetupFragment.this.getActivity();
                                NetworkSetupFragment.this.getActivity();
                                ((PowerManager) activity.getSystemService(Context.POWER_SERVICE)).reboot("重启");
                            }
                        });
                    }
                }.start();
            }
        }

        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
        public void onCancelClick() {
            NetworkSetupFragment.this.dialogConfirmView.dismiss();
        }
    }

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void showLoading(String str) {
        DialogloadView dialogloadView = new DialogloadView(getActivity());
        this.dialogloadView = dialogloadView;
        dialogloadView.setTipTextView(str);
        this.dialogloadView.show();
    }

    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void hideLoading() {
        DialogloadView dialogloadView = this.dialogloadView;
        if (dialogloadView != null) {
            dialogloadView.dismiss();
            this.dialogloadView = null;
        }
    }

    /* JADX WARN: Type inference failed for: r4v9, types: [com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment$3] */
    @Override // com.lianhexinye.m90.mvp.basisset.BasisSetView
    public void authorizationResult(int i, String str) {
        if (i == 1) {
            SPUserInfoUtils.put(getActivity(), SPUserInfoUtils.ISAUTHORIZATION, true);
            toastShow(R.string.main_dlg_author_suss);
            new Thread() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment.3
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    NetworkSetupFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            FragmentActivity activity = NetworkSetupFragment.this.getActivity();
                            NetworkSetupFragment.this.getActivity();
                            ((PowerManager) activity.getSystemService(Context.POWER_SERVICE)).reboot("重启");
                        }
                    });
                }
            }.start();
        } else if (i == 0) {
            SPUserInfoUtils.put(getActivity(), SPUserInfoUtils.ISAUTHORIZATION, false);
            toastShow(R.string.main_dlg_author_errer_tip);
        } else {
            SPUserInfoUtils.put(getActivity(), SPUserInfoUtils.ISAUTHORIZATION, false);
            toastShow("code:" + i + ",msg:" + str);
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }
}
