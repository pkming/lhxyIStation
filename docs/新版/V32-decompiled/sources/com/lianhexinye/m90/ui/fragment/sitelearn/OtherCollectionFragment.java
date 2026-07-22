package com.lianhexinye.m90.ui.fragment.sitelearn;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.aigestudio.wheelpicker.WheelPicker;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.gps.Function;
import com.lianhexinye.m90.gps.GPSMonitor;
import com.lianhexinye.m90.gps.GPSSerialPortResultListener;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter;
import com.lianhexinye.m90.mvp.sitelearn.SiteLearnView;
import com.lianhexinye.m90.ui.activity.SiteCollectionActivity;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class OtherCollectionFragment extends MvpFragment<SiteLearnView, SiteLearnPresenter> implements SiteLearnView {
    private BusLineFriendRemindModel busLineFriendRemindModelTmp;

    @BindView(R.id.butCollectionOtherOperation)
    Button butCollectionOtherOperation;

    @BindView(R.id.rbOtherDirectionDown)
    RadioButton rbOtherDirectionDown;

    @BindView(R.id.rbOtherDirectionUpstream)
    RadioButton rbOtherDirectionUpstream;

    @BindView(R.id.rgCollectionOtherDirection)
    RadioGroup rgCollectionOtherDirection;
    private SiteCollectionActivity siteCollectionActivity;

    @BindView(R.id.tvCollectionOtherLineName)
    TextView tvCollectionOtherLineName;

    @BindView(R.id.tvCollectionOtherOrderNumber)
    TextView tvCollectionOtherOrderNumber;

    @BindView(R.id.tvCollectionOtherSiteName)
    TextView tvCollectionOtherSiteName;

    @BindView(R.id.tvOtherAngle)
    TextView tvOtherAngle;

    @BindView(R.id.tvOtherCurrentAngle)
    TextView tvOtherCurrentAngle;

    @BindView(R.id.tvOtherCurrentGpsTitle)
    TextView tvOtherCurrentGpsTitle;

    @BindView(R.id.tvOtherCurrentLatitude)
    TextView tvOtherCurrentLatitude;

    @BindView(R.id.tvOtherCurrentLongitude)
    TextView tvOtherCurrentLongitude;

    @BindView(R.id.tvOtherCurrentSpeed)
    TextView tvOtherCurrentSpeed;

    @BindView(R.id.tvOtherLatitude)
    TextView tvOtherLatitude;

    @BindView(R.id.tvOtherLongitude)
    TextView tvOtherLongitude;

    @BindView(R.id.tvOtherSiteGpsTitle)
    TextView tvOtherSiteGpsTitle;

    @BindView(R.id.tvOtherSpeed)
    TextView tvOtherSpeed;

    @BindView(R.id.vOtherLineCurrent1)
    View vOtherLineCurrent1;

    @BindView(R.id.vOtherLineCurrent2)
    View vOtherLineCurrent2;

    @BindView(R.id.vOtherLineCurrent3)
    View vOtherLineCurrent3;

    @BindView(R.id.vOtherLineSite1)
    View vOtherLineSite1;

    @BindView(R.id.vOtherLineSite2)
    View vOtherLineSite2;

    @BindView(R.id.vOtherLineSite3)
    View vOtherLineSite3;
    private final String TAG = "OtherCollectionFragment";
    private String busLineName = "";
    private String direction = "S";
    private List<LineNameModel> lineNameModelList = new ArrayList();
    private List<BusLineFriendRemindModel> busLineFriendRemindModels = new ArrayList();
    private Dialog lineDialog = null;
    private WheelPicker lineWheelPicker = null;
    private List<String> lineWheelList = null;
    private Dialog attributeDialog = null;
    private WheelPicker attributeWheelPicker = null;
    private List<String> attributeWheelList = null;
    private List<Integer> busFRNoWheelList = null;
    boolean Running = true;
    private int lineWPCurrentIndex = 0;
    private int attributeWPCurrentIndex = 0;
    private String sSpeed = "0";
    private String sAngle = "N";
    private GPSSerialPortResultListener gpsSerialPortResultListener = new GPSSerialPortResultListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment.6
        @Override // com.lianhexinye.m90.gps.GPSSerialPortResultListener
        public void onFailResult() {
        }

        @Override // com.lianhexinye.m90.gps.GPSSerialPortResultListener
        public void onSuccessResult() {
            if (OtherCollectionFragment.this.siteCollectionActivity == null || AppApplication.findActivity(OtherCollectionFragment.this.siteCollectionActivity.getClass()) == null) {
                return;
            }
            OtherCollectionFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment.6.1
                @Override // java.lang.Runnable
                public void run() {
                    OtherCollectionFragment.this.updateLocationView();
                }
            });
        }
    };

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void getSiteNameDataSuccess(List<BusLineModel> list) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, com.lianhexinye.m90.mvp.BaseFragment
    public void hide() {
    }

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void hideLoading() {
    }

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, com.lianhexinye.m90.mvp.BaseFragment
    public void show() {
    }

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void showLoading() {
    }

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void showLoading(String str) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SiteCollectionActivity) {
            this.siteCollectionActivity = (SiteCollectionActivity) activity;
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("OtherCollectionFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_other_collection, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("OtherCollectionFragment", "onCreateView()");
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        openGPSPort();
        Log.d("OtherCollectionFragment", "onViewCreated()");
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.Running = false;
        this.siteCollectionActivity = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public SiteLearnPresenter createPresenter() {
        return new SiteLearnPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        this.rgCollectionOtherDirection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment.1
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbOtherDirectionDown /* 2131296603 */:
                        OtherCollectionFragment.this.direction = "X";
                        ((SiteLearnPresenter) OtherCollectionFragment.this.mvpPresenter).getLineFRList(OtherCollectionFragment.this.busLineName + "X");
                        break;
                    case R.id.rbOtherDirectionUpstream /* 2131296604 */:
                        OtherCollectionFragment.this.direction = "S";
                        ((SiteLearnPresenter) OtherCollectionFragment.this.mvpPresenter).getLineFRList(OtherCollectionFragment.this.busLineName + "S");
                        break;
                }
            }
        });
        String str = (String) SPUserInfoUtils.get(getActivity(), SPUserInfoUtils.BUSDIRECTIONNAME, "");
        this.busLineName = str;
        if (!"".equals(str)) {
            if (this.busLineName.endsWith("S")) {
                this.direction = "S";
                this.busLineName = this.busLineName.substring(0, r0.length() - 1);
                this.rgCollectionOtherDirection.check(R.id.rbOtherDirectionUpstream);
            } else {
                this.direction = "X";
                this.busLineName = this.busLineName.substring(0, r0.length() - 1);
                this.rgCollectionOtherDirection.check(R.id.rbOtherDirectionDown);
            }
            ColorStateList colorStateList = getResources().getColorStateList(R.color.c_ffffff);
            this.tvCollectionOtherLineName.setText(this.busLineName);
            this.tvCollectionOtherLineName.setTextColor(colorStateList);
        }
        ((SiteLearnPresenter) this.mvpPresenter).getLineList();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void getLineNameDataSuccess(List<LineNameModel> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        this.lineNameModelList.clear();
        this.lineNameModelList.addAll(list);
        initLineWheelDialog(list);
    }

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void getLineFRDataSuccess(List<BusLineFriendRemindModel> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        this.busLineFriendRemindModels.clear();
        this.busLineFriendRemindModels.addAll(list);
        initAttributeWheelDialog(list);
        list.clear();
    }

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void operationDataSuccess() {
        int size = this.busLineFriendRemindModels.size();
        int i = this.attributeWPCurrentIndex;
        if (size > i + 1) {
            BusLineFriendRemindModel busLineFriendRemindModel = this.busLineFriendRemindModels.get(i);
            busLineFriendRemindModel.setLatitude(this.busLineFriendRemindModelTmp.getLatitude());
            busLineFriendRemindModel.setLongitude(this.busLineFriendRemindModelTmp.getLongitude());
            int i2 = this.attributeWPCurrentIndex + 1;
            this.attributeWPCurrentIndex = i2;
            BusLineFriendRemindModel busLineFriendRemindModel2 = this.busLineFriendRemindModels.get(i2);
            this.tvCollectionOtherSiteName.setText(busLineFriendRemindModel2.getFrVoice());
            this.tvCollectionOtherOrderNumber.setText(String.valueOf(busLineFriendRemindModel2.getFrNo() + 1));
            setLineFRLocation(busLineFriendRemindModel2);
            return;
        }
        if (this.direction.equals("S")) {
            this.direction = "X";
            this.rgCollectionOtherDirection.check(R.id.rbOtherDirectionDown);
        } else {
            this.direction = "S";
            this.rgCollectionOtherDirection.check(R.id.rbOtherDirectionUpstream);
        }
        ((SiteLearnPresenter) this.mvpPresenter).getLineFRList(this.busLineName + this.direction);
    }

    private void initLineWheelDialog(List<LineNameModel> list) {
        Dialog dialog = new Dialog(getActivity(), R.style.wheelDialogStyle);
        this.lineDialog = dialog;
        dialog.setCanceledOnTouchOutside(true);
        this.lineDialog.setCancelable(true);
        Window window = this.lineDialog.getWindow();
        window.setGravity(80);
        window.setWindowAnimations(R.style.wheelDialogAnimation);
        View viewInflate = View.inflate(getActivity(), R.layout.dlg_wheel_picker, null);
        this.lineWheelPicker = (WheelPicker) viewInflate.findViewById(R.id.wPicker);
        ((TextView) viewInflate.findViewById(R.id.tvWheelTitle)).setText(getResources().getString(R.string.collection_line_wp_hint));
        this.lineWheelList = new ArrayList();
        int i = 0;
        for (LineNameModel lineNameModel : list) {
            if (!"".equals(this.busLineName) && this.busLineName.equals(lineNameModel.getBusName())) {
                this.lineWheelPicker.setSelectedItemPosition(i);
                this.lineWPCurrentIndex = i;
            }
            this.lineWheelList.add(lineNameModel.getBusName());
            i++;
        }
        this.lineWheelPicker.setData(this.lineWheelList);
        ((TextView) viewInflate.findViewById(R.id.tvConfirm)).setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (OtherCollectionFragment.this.lineWPCurrentIndex != OtherCollectionFragment.this.lineWheelPicker.getCurrentItemPosition()) {
                    OtherCollectionFragment otherCollectionFragment = OtherCollectionFragment.this;
                    otherCollectionFragment.lineWPCurrentIndex = otherCollectionFragment.lineWheelPicker.getCurrentItemPosition();
                    OtherCollectionFragment otherCollectionFragment2 = OtherCollectionFragment.this;
                    otherCollectionFragment2.busLineName = (String) otherCollectionFragment2.lineWheelList.get(OtherCollectionFragment.this.lineWPCurrentIndex);
                    ColorStateList colorStateList = OtherCollectionFragment.this.getResources().getColorStateList(R.color.c_ffffff);
                    OtherCollectionFragment.this.tvCollectionOtherLineName.setText(OtherCollectionFragment.this.busLineName);
                    OtherCollectionFragment.this.tvCollectionOtherLineName.setTextColor(colorStateList);
                    OtherCollectionFragment.this.rgCollectionOtherDirection.check(R.id.rbOtherDirectionUpstream);
                    OtherCollectionFragment.this.direction = "S";
                    ((SiteLearnPresenter) OtherCollectionFragment.this.mvpPresenter).getLineFRList(OtherCollectionFragment.this.busLineName + "S");
                }
                OtherCollectionFragment.this.lineDialog.dismiss();
            }
        });
        window.setContentView(viewInflate);
        window.setLayout(-1, -2);
    }

    private void initAttributeWheelDialog(List<BusLineFriendRemindModel> list) {
        Dialog dialog = new Dialog(getActivity(), R.style.wheelDialogStyle);
        this.attributeDialog = dialog;
        dialog.setCanceledOnTouchOutside(true);
        this.attributeDialog.setCancelable(true);
        Window window = this.attributeDialog.getWindow();
        window.setGravity(80);
        window.setWindowAnimations(R.style.wheelDialogAnimation);
        View viewInflate = View.inflate(getActivity(), R.layout.dlg_wheel_picker, null);
        this.attributeWheelPicker = (WheelPicker) viewInflate.findViewById(R.id.wPicker);
        ((TextView) viewInflate.findViewById(R.id.tvWheelTitle)).setText(getResources().getString(R.string.collection_attribute_wp_hint));
        if (this.attributeWheelList == null) {
            this.attributeWheelList = new ArrayList();
        }
        this.attributeWheelList.clear();
        if (this.busFRNoWheelList == null) {
            this.busFRNoWheelList = new ArrayList();
        }
        this.busFRNoWheelList.clear();
        ColorStateList colorStateList = getResources().getColorStateList(R.color.c_ffffff);
        this.tvCollectionOtherSiteName.setText(list.get(0).getFrVoice());
        this.tvCollectionOtherSiteName.setTextColor(colorStateList);
        this.tvCollectionOtherOrderNumber.setText(String.valueOf(list.get(0).getFrNo() + 1));
        setLineFRLocation(list.get(0));
        for (BusLineFriendRemindModel busLineFriendRemindModel : list) {
            this.attributeWheelList.add(busLineFriendRemindModel.getFrVoice());
            this.busFRNoWheelList.add(Integer.valueOf(busLineFriendRemindModel.getFrNo()));
        }
        this.attributeWPCurrentIndex = 0;
        this.attributeWheelPicker.setData(this.attributeWheelList);
        ((TextView) viewInflate.findViewById(R.id.tvConfirm)).setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OtherCollectionFragment otherCollectionFragment = OtherCollectionFragment.this;
                otherCollectionFragment.attributeWPCurrentIndex = otherCollectionFragment.attributeWheelPicker.getCurrentItemPosition();
                ColorStateList colorStateList2 = OtherCollectionFragment.this.getResources().getColorStateList(R.color.c_ffffff);
                OtherCollectionFragment.this.tvCollectionOtherSiteName.setText((CharSequence) OtherCollectionFragment.this.attributeWheelList.get(OtherCollectionFragment.this.attributeWPCurrentIndex));
                OtherCollectionFragment.this.tvCollectionOtherSiteName.setTextColor(colorStateList2);
                OtherCollectionFragment.this.tvCollectionOtherOrderNumber.setText(String.valueOf(((Integer) OtherCollectionFragment.this.busFRNoWheelList.get(OtherCollectionFragment.this.attributeWPCurrentIndex)).intValue() + 1));
                OtherCollectionFragment otherCollectionFragment2 = OtherCollectionFragment.this;
                otherCollectionFragment2.setLineFRLocation((BusLineFriendRemindModel) otherCollectionFragment2.busLineFriendRemindModels.get(OtherCollectionFragment.this.attributeWPCurrentIndex));
                OtherCollectionFragment.this.attributeDialog.dismiss();
            }
        });
        window.setContentView(viewInflate);
        window.setLayout(-1, -2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLineFRLocation(BusLineFriendRemindModel busLineFriendRemindModel) {
        this.tvOtherLatitude.setText(busLineFriendRemindModel.getLatitude());
        this.tvOtherLongitude.setText(busLineFriendRemindModel.getLongitude());
    }

    @OnClick({R.id.butCollectionOtherOperation, R.id.tvCollectionOtherLineName, R.id.tvCollectionOtherSiteName})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.butCollectionOtherOperation) {
            if (this.busLineFriendRemindModels.size() == 0) {
                toastShow(R.string.collection_tip);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.collection_dlg_msg);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    OtherCollectionFragment otherCollectionFragment = OtherCollectionFragment.this;
                    otherCollectionFragment.busLineFriendRemindModelTmp = (BusLineFriendRemindModel) otherCollectionFragment.busLineFriendRemindModels.get(OtherCollectionFragment.this.attributeWPCurrentIndex);
                    OtherCollectionFragment.this.busLineFriendRemindModelTmp.setLongitude(OtherCollectionFragment.this.tvOtherCurrentLongitude.getText().toString());
                    LogUtils.d("busLineFriendRemindModelTmp", "setLongitude" + OtherCollectionFragment.this.tvOtherCurrentLongitude.getText().toString());
                    OtherCollectionFragment.this.busLineFriendRemindModelTmp.setLatitude(OtherCollectionFragment.this.tvOtherCurrentLatitude.getText().toString());
                    LogUtils.d("busLineFriendRemindModelTmp", "setLatitude" + OtherCollectionFragment.this.tvOtherCurrentLatitude.getText().toString());
                    ((SiteLearnPresenter) OtherCollectionFragment.this.mvpPresenter).upLineFR(OtherCollectionFragment.this.busLineFriendRemindModelTmp);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (id == R.id.tvCollectionOtherLineName) {
            if (this.lineDialog != null) {
                this.lineWheelPicker.setSelectedItemPosition(this.lineWPCurrentIndex);
                this.lineWheelPicker.setSelectedItemPosition(this.lineWPCurrentIndex, true);
                this.lineDialog.show();
                return;
            }
            toastShow(R.string.collection_tip1);
            return;
        }
        if (id != R.id.tvCollectionOtherSiteName) {
            return;
        }
        if (this.attributeDialog != null) {
            this.attributeWheelPicker.setSelectedItemPosition(this.attributeWPCurrentIndex);
            this.attributeWheelPicker.setSelectedItemPosition(this.attributeWPCurrentIndex, true);
            this.attributeDialog.show();
            return;
        }
        toastShow(R.string.collection_tip2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLocationView() {
        if (GPSMonitor.bValidData && this.Running) {
            if (!JavaUtils.isEmpty(GPSMonitor.wszLatitude)) {
                if (-1 != GPSMonitor.wszLatitude.indexOf(".") && GPSMonitor.wszLatitude.substring(GPSMonitor.wszLatitude.indexOf(".")).length() > 5) {
                    this.tvOtherCurrentLatitude.setText(GPSMonitor.wszLatitude.substring(0, GPSMonitor.wszLatitude.indexOf(".") + 5));
                } else {
                    this.tvOtherCurrentLatitude.setText(GPSMonitor.wszLatitude);
                }
            } else {
                this.tvOtherCurrentLatitude.setText("0.0000");
            }
            if (!JavaUtils.isEmpty(GPSMonitor.wszLongitude)) {
                if (-1 != GPSMonitor.wszLongitude.indexOf(".") && GPSMonitor.wszLongitude.substring(GPSMonitor.wszLongitude.indexOf(".")).length() > 5) {
                    this.tvOtherCurrentLongitude.setText(GPSMonitor.wszLongitude.substring(0, GPSMonitor.wszLongitude.indexOf(".") + 5));
                } else {
                    this.tvOtherCurrentLongitude.setText(GPSMonitor.wszLongitude);
                }
            } else {
                this.tvOtherCurrentLongitude.setText("0.0000");
            }
            if (!JavaUtils.isEmpty(GPSMonitor.wSpeed)) {
                this.sSpeed = String.valueOf(Double.valueOf(GPSMonitor.wSpeed).doubleValue() * 1.852d);
                this.tvOtherCurrentSpeed.setText(Float.valueOf(this.sSpeed).intValue() + " km/h");
            } else {
                this.sSpeed = "0";
                this.tvOtherCurrentSpeed.setText("0 km/h");
            }
            if (!JavaUtils.isEmpty(GPSMonitor.wCourse)) {
                String str = GPSMonitor.wCourse;
                this.sAngle = str;
                this.tvOtherCurrentAngle.setText(String.valueOf(Float.valueOf(str).intValue()));
            } else {
                this.sAngle = "N";
                this.tvOtherCurrentAngle.setText("N");
            }
        }
    }

    private void openGPSPort() {
        this.Running = true;
        new GPSThread().start();
    }

    class GPSThread extends Thread {
        byte[] temp = null;

        GPSThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (OtherCollectionFragment.this.Running) {
                if (AppApplication.opd != null) {
                    String str = AppApplication.opd.read();
                    if (str.length() > 0) {
                        this.temp = null;
                        this.temp = new byte[str.length()];
                        byte[] bArrStringToByte2 = Function.StringToByte2(str, "GB2312");
                        this.temp = bArrStringToByte2;
                        GPSMonitor.getPortData(bArrStringToByte2, bArrStringToByte2.length, OtherCollectionFragment.this.gpsSerialPortResultListener);
                    }
                }
            }
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
        this.lineNameModelList = null;
        this.busLineFriendRemindModels = null;
        this.lineDialog = null;
        this.lineWheelPicker = null;
        this.lineWheelList = null;
        this.attributeDialog = null;
        this.attributeWheelPicker = null;
        this.attributeWheelList = null;
        this.busFRNoWheelList = null;
        this.gpsSerialPortResultListener = null;
    }
}
