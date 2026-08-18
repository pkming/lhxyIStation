package com.lianhexinye.m90.ui.fragment.systemsetup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.widget.DialogConfirmView;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.basisset.BasisSetPresenter;
import com.lianhexinye.m90.mvp.basisset.BasisSetView;
import com.lianhexinye.m90.socket.SocketManage;
import com.lianhexinye.m90.ui.activity.BasicSetupActivity;

/* JADX INFO: loaded from: classes2.dex */
public class SerialPortSetupFragment extends MvpFragment<BasisSetView, BasisSetPresenter> implements BasisSetView<String> {
    private BasicSetupActivity basicSetupActivity;

    @BindView(R.id.butPortAffirm)
    Button butPortAffirm;

    @BindView(R.id.butSend)
    Button butSend;
    private volatile DialogConfirmView dialogConfirmView;

    @BindView(R.id.etPortData)
    EditText etPortData;

    @BindView(R.id.gpsChannel)
    Spinner gpsChannel;

    @BindView(R.id.rbHexType)
    RadioButton rbHexType;

    @BindView(R.id.rbTxtType)
    RadioButton rbTxtType;

    @BindView(R.id.rgDataType)
    RadioGroup rgDataType;

    @BindView(R.id.rlPortAffirm)
    RelativeLayout rlPortAffirm;

    @BindView(R.id.spChannel)
    Spinner spChannel;

    @BindView(R.id.spPortBaud2321)
    Spinner spPortBaud2321;

    @BindView(R.id.spPortBaud2322)
    Spinner spPortBaud2322;

    @BindView(R.id.spPortBaud485)
    Spinner spPortBaud485;

    @BindView(R.id.spPortBaud4852)
    Spinner spPortBaud4852;

    @BindView(R.id.spPortNumber1)
    Spinner spPortNumber1;

    @BindView(R.id.spPortProtocol2321)
    Spinner spPortProtocol2321;

    @BindView(R.id.spPortProtocol2322)
    Spinner spPortProtocol2322;

    @BindView(R.id.spPortProtocol485)
    Spinner spPortProtocol485;

    @BindView(R.id.spPortProtocol4852)
    Spinner spPortProtocol4852;
    private final String TAG = "SerialPortSetupFragment";
    private int dataType = 0;
    private String portProtocol2321 = "无";
    private String portBaud2321 = "9600";
    private String portProtocol2322 = "无";
    private String portBaud2322 = "9600";
    private String portProtocol485 = "无";
    private String portBaud485 = "9600";
    private String portProtocol4852 = "无";
    private String portBaud4852 = "9600";
    private String channelDvr = "VIN1-AHD";
    private String portBaudGps = "115200";

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
        Log.d("SerialPortSetupFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_serial_port, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("SerialPortSetupFragment", "onCreateView()");
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public BasisSetPresenter createPresenter() {
        return new BasisSetPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource = ArrayAdapter.createFromResource(getActivity(), R.array.arraybaud, R.layout.spinner_item);
        arrayAdapterCreateFromResource.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spPortBaud2321.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource);
        int count = arrayAdapterCreateFromResource.getCount();
        this.portBaud2321 = (String) SPUserInfoUtils.get(getActivity(), "RS232-1Baud", "9600");
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= count) {
                break;
            }
            if (this.portBaud2321.equals(arrayAdapterCreateFromResource.getItem(i2).toString())) {
                this.spPortBaud2321.setSelection(i2, true);
                break;
            }
            i2++;
        }
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource2 = ArrayAdapter.createFromResource(getActivity(), R.array.arrayprotocol232, R.layout.spinner_item);
        arrayAdapterCreateFromResource2.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spPortProtocol2321.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource2);
        int count2 = arrayAdapterCreateFromResource2.getCount();
        this.portProtocol2321 = (String) SPUserInfoUtils.get(getActivity(), "RS232-1Protocol", "无");
        int i3 = 0;
        while (true) {
            if (i3 >= count2) {
                break;
            }
            if (this.portProtocol2321.equals(arrayAdapterCreateFromResource2.getItem(i3).toString())) {
                this.spPortProtocol2321.setSelection(i3, true);
                break;
            }
            i3++;
        }
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource3 = ArrayAdapter.createFromResource(getActivity(), R.array.arrayprotocol232, R.layout.spinner_item);
        arrayAdapterCreateFromResource3.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spPortProtocol2322.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource3);
        int count3 = arrayAdapterCreateFromResource3.getCount();
        this.portProtocol2322 = (String) SPUserInfoUtils.get(getActivity(), "RS232-2Protocol", "无");
        int i4 = 0;
        while (true) {
            if (i4 >= count3) {
                break;
            }
            if (this.portProtocol2322.equals(arrayAdapterCreateFromResource3.getItem(i4).toString())) {
                this.spPortProtocol2322.setSelection(i4, true);
                break;
            }
            i4++;
        }
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource4 = ArrayAdapter.createFromResource(getActivity(), R.array.arraybaud, R.layout.spinner_item);
        arrayAdapterCreateFromResource4.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spPortBaud2322.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource4);
        int count4 = arrayAdapterCreateFromResource4.getCount();
        this.portBaud2322 = (String) SPUserInfoUtils.get(getActivity(), "RS232-2Baud", "9600");
        int i5 = 0;
        while (true) {
            if (i5 >= count4) {
                break;
            }
            if (this.portBaud2322.equals(arrayAdapterCreateFromResource4.getItem(i5).toString())) {
                this.spPortBaud2322.setSelection(i5, true);
                break;
            }
            i5++;
        }
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource5 = ArrayAdapter.createFromResource(getActivity(), R.array.arrayprotocoltype, R.layout.spinner_item);
        arrayAdapterCreateFromResource5.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spPortProtocol485.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource5);
        int count5 = arrayAdapterCreateFromResource5.getCount();
        this.portProtocol485 = (String) SPUserInfoUtils.get(getActivity(), "RS485Protocol", "无");
        int i6 = 0;
        while (true) {
            if (i6 >= count5) {
                break;
            }
            if (this.portProtocol485.equals(arrayAdapterCreateFromResource5.getItem(i6).toString())) {
                this.spPortProtocol485.setSelection(i6, true);
                break;
            }
            i6++;
        }
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource6 = ArrayAdapter.createFromResource(getActivity(), R.array.arraybaud, R.layout.spinner_item);
        arrayAdapterCreateFromResource6.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spPortBaud485.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource6);
        int count6 = arrayAdapterCreateFromResource6.getCount();
        this.portBaud485 = (String) SPUserInfoUtils.get(getActivity(), "RS485Baud", "9600");
        int i7 = 0;
        while (true) {
            if (i7 >= count6) {
                break;
            }
            if (this.portBaud485.equals(arrayAdapterCreateFromResource6.getItem(i7).toString())) {
                this.spPortBaud485.setSelection(i7, true);
                break;
            }
            i7++;
        }
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource7 = ArrayAdapter.createFromResource(getActivity(), R.array.arrayprotocoltype, R.layout.spinner_item);
        arrayAdapterCreateFromResource7.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spPortProtocol4852.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource7);
        int count7 = arrayAdapterCreateFromResource7.getCount();
        this.portProtocol4852 = (String) SPUserInfoUtils.get(getActivity(), "RS485-2Protocol", "无");
        int i8 = 0;
        while (true) {
            if (i8 >= count7) {
                break;
            }
            if (this.portProtocol4852.equals(arrayAdapterCreateFromResource5.getItem(i8).toString())) {
                this.spPortProtocol4852.setSelection(i8, true);
                break;
            }
            i8++;
        }
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource8 = ArrayAdapter.createFromResource(getActivity(), R.array.arraybaud, R.layout.spinner_item);
        arrayAdapterCreateFromResource8.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spPortBaud4852.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource8);
        int count8 = arrayAdapterCreateFromResource6.getCount();
        this.portBaud4852 = (String) SPUserInfoUtils.get(getActivity(), "RS485-2Baud", "9600");
        int i9 = 0;
        while (true) {
            if (i9 >= count8) {
                break;
            }
            if (this.portBaud4852.equals(arrayAdapterCreateFromResource6.getItem(i9).toString())) {
                this.spPortBaud4852.setSelection(i9, true);
                break;
            }
            i9++;
        }
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource9 = ArrayAdapter.createFromResource(getActivity(), R.array.channelport, R.layout.spinner_item);
        arrayAdapterCreateFromResource9.setDropDownViewResource(R.layout.dropdown_stytle);
        this.spChannel.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource9);
        int count9 = arrayAdapterCreateFromResource9.getCount();
        this.channelDvr = (String) SPUserInfoUtils.get(getActivity(), "DVR-Channel", "VIN1-AHD");
        int i10 = 0;
        while (true) {
            if (i10 >= count9) {
                break;
            }
            if (this.channelDvr.equals(arrayAdapterCreateFromResource9.getItem(i10).toString())) {
                this.spChannel.setSelection(i10, true);
                break;
            }
            i10++;
        }
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource10 = ArrayAdapter.createFromResource(getActivity(), R.array.gpsportbaud, R.layout.spinner_item);
        arrayAdapterCreateFromResource10.setDropDownViewResource(R.layout.dropdown_stytle);
        this.gpsChannel.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource10);
        int count10 = arrayAdapterCreateFromResource10.getCount();
        this.portBaudGps = (String) SPUserInfoUtils.get(getActivity(), "gpsportbaud", "115200");
        while (true) {
            if (i >= count10) {
                break;
            }
            if (this.portBaudGps.equals(arrayAdapterCreateFromResource10.getItem(i).toString())) {
                this.gpsChannel.setSelection(i, true);
                break;
            }
            i++;
        }
        initEvent();
    }

    private void initEvent() {
        this.spPortProtocol2321.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.portProtocol2321 = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.spPortBaud2321.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.2
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.portBaud2321 = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.spPortProtocol2322.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.3
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.portProtocol2322 = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.spPortBaud2322.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.4
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.portBaud2322 = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.spPortProtocol485.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.5
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.portProtocol485 = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.spPortBaud485.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.6
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.portBaud485 = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.spPortProtocol4852.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.7
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.portProtocol4852 = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.spPortBaud4852.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.8
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.portBaud4852 = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.spChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.9
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.channelDvr = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.gpsChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.10
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SerialPortSetupFragment.this.portBaudGps = adapterView.getItemAtPosition(i).toString();
            }
        });
        this.rgDataType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.11
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbHexType) {
                    SerialPortSetupFragment.this.dataType = 1;
                } else {
                    if (i != R.id.rbTxtType) {
                        return;
                    }
                    SerialPortSetupFragment.this.dataType = 0;
                }
            }
        });
    }

    @OnClick({R.id.butPortAffirm, R.id.butSend})
    public void onViewClicked(View view) {
        if (view.getId() != R.id.butPortAffirm) {
            return;
        }
        String str = (String) SPUserInfoUtils.get(AppApplication.getInstance(), SPUserInfoUtils.BUSDIRECTIONNAME, "");
        if (str != null && !str.equals("")) {
            this.dialogConfirmView = new DialogConfirmView.Builder(getActivity()).setButOkTxt(getResources().getString(R.string.restart)).setContent(getResources().getString(R.string.port_ok_tip)).setOnOKClickListener(new AnonymousClass12()).build();
            this.dialogConfirmView.show();
        } else {
            toastShow("请先导入报站器资源.");
        }
    }

    /* JADX INFO: renamed from: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment$12, reason: invalid class name */
    class AnonymousClass12 implements DialogConfirmView.OnOKCancelClickListener {
        AnonymousClass12() {
        }

        /* JADX WARN: Type inference failed for: r0v35, types: [com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment$12$1] */
        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
        public void onOKClick() {
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "RS232-1Protocol", SerialPortSetupFragment.this.portProtocol2321);
            ConfigInfoModel configInfoModel = new ConfigInfoModel();
            configInfoModel.setConfigItem("RS232-1Protocol");
            configInfoModel.setConfigValue(SerialPortSetupFragment.this.portProtocol2321);
            ((BasisSetPresenter) SerialPortSetupFragment.this.mvpPresenter).upConfigData(configInfoModel);
            if (!SerialPortSetupFragment.this.portProtocol2321.trim().equals("") && !SerialPortSetupFragment.this.portProtocol2321.trim().equals("无")) {
                SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "NetDispatchProtocol", "无");
                ConfigInfoModel configInfoModel2 = new ConfigInfoModel();
                configInfoModel2.setConfigItem("NetDispatchProtocol");
                configInfoModel2.setConfigValue("无");
                ((BasisSetPresenter) SerialPortSetupFragment.this.mvpPresenter).upConfigData(configInfoModel2);
            }
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "RS232-1Baud", SerialPortSetupFragment.this.portBaud2321);
            ConfigInfoModel configInfoModel3 = new ConfigInfoModel();
            configInfoModel3.setConfigItem("RS232-1Baud");
            configInfoModel3.setConfigValue(SerialPortSetupFragment.this.portBaud2321);
            ((BasisSetPresenter) SerialPortSetupFragment.this.mvpPresenter).upConfigData(configInfoModel3);
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "RS232-2Protocol", SerialPortSetupFragment.this.portProtocol2322);
            ConfigInfoModel configInfoModel4 = new ConfigInfoModel();
            configInfoModel4.setConfigItem("RS232-2Protocol");
            configInfoModel4.setConfigValue(SerialPortSetupFragment.this.portProtocol2322);
            ((BasisSetPresenter) SerialPortSetupFragment.this.mvpPresenter).upConfigData(configInfoModel4);
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "RS232-2Baud", SerialPortSetupFragment.this.portBaud2322);
            ConfigInfoModel configInfoModel5 = new ConfigInfoModel();
            configInfoModel5.setConfigItem("RS232-2Baud");
            configInfoModel5.setConfigValue(SerialPortSetupFragment.this.portBaud2322);
            ((BasisSetPresenter) SerialPortSetupFragment.this.mvpPresenter).upConfigData(configInfoModel5);
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "RS485Protocol", SerialPortSetupFragment.this.portProtocol485);
            ConfigInfoModel configInfoModel6 = new ConfigInfoModel();
            configInfoModel6.setConfigItem("RS485Protocol");
            configInfoModel6.setConfigValue(SerialPortSetupFragment.this.portProtocol485);
            ((BasisSetPresenter) SerialPortSetupFragment.this.mvpPresenter).upConfigData(configInfoModel6);
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "RS485Baud", SerialPortSetupFragment.this.portBaud485);
            ConfigInfoModel configInfoModel7 = new ConfigInfoModel();
            configInfoModel7.setConfigItem("RS485Baud");
            configInfoModel7.setConfigValue(SerialPortSetupFragment.this.portBaud485);
            ((BasisSetPresenter) SerialPortSetupFragment.this.mvpPresenter).upConfigData(configInfoModel7);
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "RS485-2Protocol", SerialPortSetupFragment.this.portProtocol4852);
            ConfigInfoModel configInfoModel8 = new ConfigInfoModel();
            configInfoModel8.setConfigItem("RS485-2Protocol");
            configInfoModel8.setConfigValue(SerialPortSetupFragment.this.portProtocol4852);
            ((BasisSetPresenter) SerialPortSetupFragment.this.mvpPresenter).upConfigData(configInfoModel8);
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "RS485-2Baud", SerialPortSetupFragment.this.portBaud4852);
            ConfigInfoModel configInfoModel9 = new ConfigInfoModel();
            configInfoModel9.setConfigItem("RS485-2Baud");
            configInfoModel9.setConfigValue(SerialPortSetupFragment.this.portBaud4852);
            ((BasisSetPresenter) SerialPortSetupFragment.this.mvpPresenter).upConfigData(configInfoModel9);
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "DVR-Channel", SerialPortSetupFragment.this.channelDvr);
            SPUserInfoUtils.put(SerialPortSetupFragment.this.getActivity(), "gpsportbaud", SerialPortSetupFragment.this.portBaudGps);
            SerialPortSetupFragment.this.toastShow(R.string.port_ok);
            SerialPortSetupFragment.this.dialogConfirmView.dismiss();
            new Thread() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.12.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    super.run();
                    SocketManage.getInstance().closeAllSocket();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SerialPortSetupFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment.12.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            FragmentActivity activity = SerialPortSetupFragment.this.getActivity();
                            SerialPortSetupFragment.this.getActivity();
                            ((PowerManager) activity.getSystemService(Context.POWER_SERVICE)).reboot("重启");
                        }
                    });
                }
            }.start();
        }

        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
        public void onCancelClick() {
            SerialPortSetupFragment.this.dialogConfirmView.dismiss();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ((BasisSetPresenter) this.mvpPresenter).detachView();
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.dialogConfirmView = null;
    }
}
