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
public class SiteCollectionFragment extends MvpFragment<SiteLearnView, SiteLearnPresenter> implements SiteLearnView {
    private BusLineModel busLineModelTmp;

    @BindView(R.id.butCollectionOperation)
    Button butCollectionOperation;

    @BindView(R.id.rbDirectionDown)
    RadioButton rbDirectionDown;

    @BindView(R.id.rbDirectionUpstream)
    RadioButton rbDirectionUpstream;

    @BindView(R.id.rgCollectionDirection)
    RadioGroup rgCollectionDirection;
    private SiteCollectionActivity siteCollectionActivity;

    @BindView(R.id.tvAngle)
    TextView tvAngle;

    @BindView(R.id.tvCollectionLineName)
    TextView tvCollectionLineName;

    @BindView(R.id.tvCollectionOrderNumber)
    TextView tvCollectionOrderNumber;

    @BindView(R.id.tvCollectionSiteName)
    TextView tvCollectionSiteName;

    @BindView(R.id.tvCurrentAngle)
    TextView tvCurrentAngle;

    @BindView(R.id.tvCurrentGpsTitle)
    TextView tvCurrentGpsTitle;

    @BindView(R.id.tvCurrentLatitude)
    TextView tvCurrentLatitude;

    @BindView(R.id.tvCurrentLongitude)
    TextView tvCurrentLongitude;

    @BindView(R.id.tvCurrentSpeed)
    TextView tvCurrentSpeed;

    @BindView(R.id.tvLatitude)
    TextView tvLatitude;

    @BindView(R.id.tvLongitude)
    TextView tvLongitude;

    @BindView(R.id.tvSiteGpsTitle)
    TextView tvSiteGpsTitle;

    @BindView(R.id.tvSpeed)
    TextView tvSpeed;

    @BindView(R.id.vLineCurrent1)
    View vLineCurrent1;

    @BindView(R.id.vLineCurrent2)
    View vLineCurrent2;

    @BindView(R.id.vLineCurrent3)
    View vLineCurrent3;

    @BindView(R.id.vLineSite1)
    View vLineSite1;

    @BindView(R.id.vLineSite2)
    View vLineSite2;

    @BindView(R.id.vLineSite3)
    View vLineSite3;
    private final String TAG = "SiteCollectionFragment";
    private String busLineName = "";
    private String direction = "S";
    private List<LineNameModel> lineNameModelList = new ArrayList();
    private List<BusLineModel> busLineModelList = new ArrayList();
    private Dialog lineDialog = null;
    private WheelPicker lineWheelPicker = null;
    private List<String> lineWheelList = null;
    private Dialog siteDialog = null;
    private WheelPicker siteWheelPicker = null;
    private List<String> siteWheelList = null;
    private List<Integer> busNoWheelList = null;
    boolean Running = true;
    private int lineWPCurrentIndex = 0;
    private int siteWPCurrentIndex = 0;
    private String sSpeed = "0";
    private String sAngle = "N";
    private GPSSerialPortResultListener gpsSerialPortResultListener = new GPSSerialPortResultListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment.6
        @Override // com.lianhexinye.m90.gps.GPSSerialPortResultListener
        public void onFailResult() {
        }

        @Override // com.lianhexinye.m90.gps.GPSSerialPortResultListener
        public void onSuccessResult() {
            if (SiteCollectionFragment.this.siteCollectionActivity == null || AppApplication.findActivity(SiteCollectionFragment.this.siteCollectionActivity.getClass()) == null) {
                return;
            }
            SiteCollectionFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment.6.1
                @Override // java.lang.Runnable
                public void run() {
                    SiteCollectionFragment.this.updateLocationView();
                }
            });
        }
    };

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void getLineFRDataSuccess(List<BusLineFriendRemindModel> list) {
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
        Log.d("SiteCollectionFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_site_collection, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("SiteCollectionFragment", "onCreateView()");
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Log.d("SiteCollectionFragment", "onViewCreated()");
        openGPSPort();
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
        this.rgCollectionDirection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment.1
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbDirectionDown /* 2131296599 */:
                        SiteCollectionFragment.this.direction = "X";
                        ((SiteLearnPresenter) SiteCollectionFragment.this.mvpPresenter).getSiteList(SiteCollectionFragment.this.busLineName + "X");
                        break;
                    case R.id.rbDirectionUpstream /* 2131296600 */:
                        SiteCollectionFragment.this.direction = "S";
                        ((SiteLearnPresenter) SiteCollectionFragment.this.mvpPresenter).getSiteList(SiteCollectionFragment.this.busLineName + "S");
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
                this.rgCollectionDirection.check(R.id.rbDirectionUpstream);
            } else {
                this.direction = "X";
                this.busLineName = this.busLineName.substring(0, r0.length() - 1);
                this.rgCollectionDirection.check(R.id.rbDirectionDown);
            }
            ColorStateList colorStateList = getResources().getColorStateList(R.color.c_ffffff);
            this.tvCollectionLineName.setText(this.busLineName);
            this.tvCollectionLineName.setTextColor(colorStateList);
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
    public void getSiteNameDataSuccess(List<BusLineModel> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        this.busLineModelList.clear();
        this.busLineModelList.addAll(list);
        initSiteWheelDialog(list);
    }

    @Override // com.lianhexinye.m90.mvp.sitelearn.SiteLearnView
    public void operationDataSuccess() {
        int size = this.busLineModelList.size();
        int i = this.siteWPCurrentIndex;
        if (size > i + 1) {
            BusLineModel busLineModel = this.busLineModelList.get(i);
            busLineModel.setLatitude(this.busLineModelTmp.getLatitude());
            busLineModel.setLongitude(this.busLineModelTmp.getLongitude());
            busLineModel.setSpeed(this.busLineModelTmp.getSpeed());
            busLineModel.setAngle(this.busLineModelTmp.getAngle());
            int i2 = this.siteWPCurrentIndex + 1;
            this.siteWPCurrentIndex = i2;
            BusLineModel busLineModel2 = this.busLineModelList.get(i2);
            this.tvCollectionSiteName.setText(busLineModel2.getBusName());
            this.tvCollectionOrderNumber.setText(String.valueOf(busLineModel2.getBusNo() + 1));
            setSiteLocation(busLineModel2);
            return;
        }
        if (this.direction.equals("S")) {
            this.direction = "X";
            this.rgCollectionDirection.check(R.id.rbDirectionDown);
        } else {
            this.direction = "S";
            this.rgCollectionDirection.check(R.id.rbDirectionUpstream);
        }
        ((SiteLearnPresenter) this.mvpPresenter).getSiteList(this.busLineName + this.direction);
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
        ((TextView) viewInflate.findViewById(R.id.tvConfirm)).setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SiteCollectionFragment.this.lineWPCurrentIndex != SiteCollectionFragment.this.lineWheelPicker.getCurrentItemPosition()) {
                    SiteCollectionFragment siteCollectionFragment = SiteCollectionFragment.this;
                    siteCollectionFragment.lineWPCurrentIndex = siteCollectionFragment.lineWheelPicker.getCurrentItemPosition();
                    SiteCollectionFragment siteCollectionFragment2 = SiteCollectionFragment.this;
                    siteCollectionFragment2.busLineName = (String) siteCollectionFragment2.lineWheelList.get(SiteCollectionFragment.this.lineWPCurrentIndex);
                    ColorStateList colorStateList = SiteCollectionFragment.this.getResources().getColorStateList(R.color.c_ffffff);
                    SiteCollectionFragment.this.tvCollectionLineName.setText(SiteCollectionFragment.this.busLineName);
                    SiteCollectionFragment.this.tvCollectionLineName.setTextColor(colorStateList);
                    SiteCollectionFragment.this.rgCollectionDirection.check(R.id.rbDirectionUpstream);
                    SiteCollectionFragment.this.direction = "S";
                    ((SiteLearnPresenter) SiteCollectionFragment.this.mvpPresenter).getSiteList(SiteCollectionFragment.this.busLineName + "S");
                }
                SiteCollectionFragment.this.lineDialog.dismiss();
            }
        });
        window.setContentView(viewInflate);
        window.setLayout(-1, -2);
    }

    private void initSiteWheelDialog(List<BusLineModel> list) {
        Dialog dialog = new Dialog(getActivity(), R.style.wheelDialogStyle);
        this.siteDialog = dialog;
        dialog.setCanceledOnTouchOutside(true);
        this.siteDialog.setCancelable(true);
        Window window = this.siteDialog.getWindow();
        window.setGravity(80);
        window.setWindowAnimations(R.style.wheelDialogAnimation);
        View viewInflate = View.inflate(getActivity(), R.layout.dlg_wheel_picker, null);
        this.siteWheelPicker = (WheelPicker) viewInflate.findViewById(R.id.wPicker);
        ((TextView) viewInflate.findViewById(R.id.tvWheelTitle)).setText(getResources().getString(R.string.collection_site_name_wp_hint));
        if (this.siteWheelList == null) {
            this.siteWheelList = new ArrayList();
        }
        this.siteWheelList.clear();
        if (this.busNoWheelList == null) {
            this.busNoWheelList = new ArrayList();
        }
        this.busNoWheelList.clear();
        ColorStateList colorStateList = getResources().getColorStateList(R.color.c_ffffff);
        this.tvCollectionSiteName.setText(list.get(0).getBusName());
        this.tvCollectionSiteName.setTextColor(colorStateList);
        this.tvCollectionOrderNumber.setText(String.valueOf(list.get(0).getBusNo() + 1));
        setSiteLocation(list.get(0));
        for (BusLineModel busLineModel : list) {
            this.siteWheelList.add(busLineModel.getBusName());
            this.busNoWheelList.add(Integer.valueOf(busLineModel.getBusNo()));
        }
        this.siteWPCurrentIndex = 0;
        this.siteWheelPicker.setData(this.siteWheelList);
        ((TextView) viewInflate.findViewById(R.id.tvConfirm)).setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SiteCollectionFragment siteCollectionFragment = SiteCollectionFragment.this;
                siteCollectionFragment.siteWPCurrentIndex = siteCollectionFragment.siteWheelPicker.getCurrentItemPosition();
                ColorStateList colorStateList2 = SiteCollectionFragment.this.getResources().getColorStateList(R.color.c_ffffff);
                SiteCollectionFragment.this.tvCollectionSiteName.setText((CharSequence) SiteCollectionFragment.this.siteWheelList.get(SiteCollectionFragment.this.siteWPCurrentIndex));
                SiteCollectionFragment.this.tvCollectionSiteName.setTextColor(colorStateList2);
                SiteCollectionFragment.this.tvCollectionOrderNumber.setText(String.valueOf(((Integer) SiteCollectionFragment.this.busNoWheelList.get(SiteCollectionFragment.this.siteWPCurrentIndex)).intValue() + 1));
                SiteCollectionFragment siteCollectionFragment2 = SiteCollectionFragment.this;
                siteCollectionFragment2.setSiteLocation((BusLineModel) siteCollectionFragment2.busLineModelList.get(SiteCollectionFragment.this.siteWPCurrentIndex));
                SiteCollectionFragment.this.siteDialog.dismiss();
            }
        });
        window.setContentView(viewInflate);
        window.setLayout(-1, -2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSiteLocation(BusLineModel busLineModel) {
        if (!JavaUtils.isEmpty(busLineModel.getLatitude())) {
            this.tvLatitude.setText(busLineModel.getLatitude());
        } else {
            this.tvLatitude.setText("0.0000");
        }
        if (!JavaUtils.isEmpty(busLineModel.getLongitude())) {
            this.tvLongitude.setText(busLineModel.getLongitude());
        } else {
            this.tvLongitude.setText("0.0000");
        }
        if (!JavaUtils.isEmpty(busLineModel.getSpeed())) {
            this.tvSpeed.setText(busLineModel.getSpeed() + " km/h");
        } else {
            this.tvSpeed.setText("0 km/h");
        }
        if (!JavaUtils.isEmpty(busLineModel.getAngle())) {
            this.tvAngle.setText(busLineModel.getAngle());
        } else {
            this.tvAngle.setText("N");
        }
    }

    @OnClick({R.id.butCollectionOperation, R.id.tvCollectionLineName, R.id.tvCollectionSiteName})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.butCollectionOperation) {
            if (this.busLineModelList.size() == 0) {
                toastShow(R.string.collection_tip);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.collection_dlg_msg);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    SiteCollectionFragment siteCollectionFragment = SiteCollectionFragment.this;
                    siteCollectionFragment.busLineModelTmp = (BusLineModel) siteCollectionFragment.busLineModelList.get(SiteCollectionFragment.this.siteWPCurrentIndex);
                    SiteCollectionFragment.this.busLineModelTmp.setSpeed(SiteCollectionFragment.this.sSpeed);
                    SiteCollectionFragment.this.busLineModelTmp.setLongitude(SiteCollectionFragment.this.tvCurrentLongitude.getText().toString());
                    SiteCollectionFragment.this.busLineModelTmp.setLatitude(SiteCollectionFragment.this.tvCurrentLatitude.getText().toString());
                    SiteCollectionFragment.this.busLineModelTmp.setAngle(SiteCollectionFragment.this.sAngle);
                    ((SiteLearnPresenter) SiteCollectionFragment.this.mvpPresenter).upSite(SiteCollectionFragment.this.busLineModelTmp);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (id == R.id.tvCollectionLineName) {
            if (this.lineDialog != null) {
                this.lineWheelPicker.setSelectedItemPosition(this.lineWPCurrentIndex);
                this.lineWheelPicker.setSelectedItemPosition(this.lineWPCurrentIndex, true);
                this.lineDialog.show();
                return;
            }
            toastShow(R.string.collection_tip1);
            return;
        }
        if (id != R.id.tvCollectionSiteName) {
            return;
        }
        if (this.siteDialog != null) {
            this.siteWheelPicker.setSelectedItemPosition(this.siteWPCurrentIndex);
            this.siteWheelPicker.setSelectedItemPosition(this.siteWPCurrentIndex, true);
            this.siteDialog.show();
            return;
        }
        toastShow(R.string.collection_tip2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLocationView() {
        if (GPSMonitor.bValidData && this.Running) {
            if (!JavaUtils.isEmpty(GPSMonitor.wszLatitude)) {
                if (-1 != GPSMonitor.wszLatitude.indexOf(".") && GPSMonitor.wszLatitude.substring(GPSMonitor.wszLatitude.indexOf(".")).length() > 5) {
                    this.tvCurrentLatitude.setText(GPSMonitor.wszLatitude.substring(0, GPSMonitor.wszLatitude.indexOf(".") + 5));
                } else {
                    this.tvCurrentLatitude.setText(GPSMonitor.wszLatitude);
                }
            } else {
                this.tvCurrentLatitude.setText("0.0000");
            }
            if (!JavaUtils.isEmpty(GPSMonitor.wszLongitude)) {
                if (-1 != GPSMonitor.wszLongitude.indexOf(".") && GPSMonitor.wszLongitude.substring(GPSMonitor.wszLongitude.indexOf(".")).length() > 5) {
                    this.tvCurrentLongitude.setText(GPSMonitor.wszLongitude.substring(0, GPSMonitor.wszLongitude.indexOf(".") + 5));
                } else {
                    this.tvCurrentLongitude.setText(GPSMonitor.wszLongitude);
                }
            } else {
                this.tvCurrentLongitude.setText("0.0000");
            }
            if (!JavaUtils.isEmpty(GPSMonitor.wSpeed)) {
                this.sSpeed = String.valueOf(Double.valueOf(GPSMonitor.wSpeed).doubleValue() * 1.852d);
                this.sSpeed = "" + Float.valueOf(this.sSpeed).intValue();
                this.tvCurrentSpeed.setText(this.sSpeed + " km/h");
            } else {
                this.sSpeed = "0";
                this.tvCurrentSpeed.setText("0 km/h");
            }
            if (!JavaUtils.isEmpty(GPSMonitor.wCourse)) {
                this.sAngle = GPSMonitor.wCourse;
                String str = "" + Float.valueOf(this.sAngle).intValue();
                this.sAngle = str;
                this.tvCurrentAngle.setText(str);
                return;
            }
            this.sAngle = "N";
            this.tvCurrentAngle.setText("N");
        }
    }

    private void openGPSPort() {
        this.Running = true;
        new GPSThread().start();
    }

    class GPSThread extends Thread {
        GPSThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (SiteCollectionFragment.this.Running) {
                if (AppApplication.opd != null) {
                    String str = AppApplication.opd.read();
                    if (str.length() > 0) {
                        byte[] bArr = new byte[str.length()];
                        byte[] bArrStringToByte2 = Function.StringToByte2(str, "GB2312");
                        GPSMonitor.getPortData(bArrStringToByte2, bArrStringToByte2.length, SiteCollectionFragment.this.gpsSerialPortResultListener);
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
        this.busLineModelList = null;
        this.lineDialog = null;
        this.lineWheelPicker = null;
        this.lineWheelList = null;
        this.siteDialog = null;
        this.siteWheelPicker = null;
        this.siteWheelList = null;
        this.busNoWheelList = null;
        this.gpsSerialPortResultListener = null;
    }
}
