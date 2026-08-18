package com.lianhexinye.m90.ui.fragment.dispatch;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.gpio.GpioOperation;
import com.lianhexinye.m90.gps.GPSMonitor;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.socket.SocketManage;
import com.lianhexinye.m90.socket.request.Generate808ReqPackage;
import com.lianhexinye.m90.tts.SystemTTS;
import com.lianhexinye.m90.ui.activity.DispatchCenterActivity;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/* JADX INFO: loaded from: classes2.dex */
public class AttendanceFragment extends MvpFragment<MainView, MainPresenter> implements MainView {
    private AudioManager audioManager;

    @BindView(R.id.butAffirmOperation)
    Button butAffirmOperation;
    private String cardId;
    private DispatchCenterActivity dispatchCenterActivity;

    @BindView(R.id.etCardID)
    EditText etCardID;
    private String latitudeTmp;
    private String longitudeTmp;

    @BindView(R.id.rb_radio_gooff_signin)
    RadioButton rbRadioGooffSignin;

    @BindView(R.id.rb_radio_goto_signin)
    RadioButton rbRadioGotoSignin;
    private RfidTestThread rfidTestThread;

    @BindView(R.id.rg_radio_signin)
    RadioGroup rgRadioSignin;
    private SystemTTS systemTTS;

    @BindView(R.id.tvDriverName)
    TextView tvDriverName;
    private final String TAG = "AttendanceFragment";
    private Timer cardTestTimer = null;

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
        if (activity instanceof DispatchCenterActivity) {
            this.dispatchCenterActivity = (DispatchCenterActivity) activity;
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("AttendanceFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_attendance, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("AttendanceFragment", "onCreateView()");
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
        closeRfid();
        Timer timer = this.cardTestTimer;
        if (timer != null) {
            timer.cancel();
            this.cardTestTimer = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        initRfidTest();
        initTTS();
        this.butAffirmOperation.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.AttendanceFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AttendanceFragment.this.etCardID.getText().toString().trim().length() == 0) {
                    AttendanceFragment.this.toastShow("卡号不能为空，请输入卡号");
                    return;
                }
                if (!JavaUtils.isEmpty(AttendanceFragment.this.etCardID.getText().toString().trim())) {
                    AttendanceFragment attendanceFragment = AttendanceFragment.this;
                    attendanceFragment.playTTSTest(attendanceFragment.etCardID.getText().toString());
                } else {
                    AttendanceFragment.this.toastShow("暂无卡号~");
                }
                ReportInfoModel reportInfoModel = new ReportInfoModel();
                switch (AttendanceFragment.this.rgRadioSignin.getCheckedRadioButtonId()) {
                    case R.id.rb_radio_gooff_signin /* 2131296608 */:
                        reportInfoModel.setdStatus(1);
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.SIGNINOUTSTATUS, "1");
                        break;
                    case R.id.rb_radio_goto_signin /* 2131296609 */:
                        reportInfoModel.setdStatus(0);
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.SIGNINOUTSTATUS, "0");
                        break;
                }
                try {
                    byte[] bytes = AttendanceFragment.this.etCardID.getText().toString().trim().getBytes("gb2312");
                    reportInfoModel.setCardNo(JavaUtils.bytesToHexString(bytes, bytes.length));
                    reportInfoModel.setDeviceSignMode(1);
                    reportInfoModel.setTerminalDate(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
                    reportInfoModel.setTransmissionType("09");
                    reportInfoModel.setDeviceVersion(String.valueOf(AndroidUtils.getLocalVersion()));
                    reportInfoModel.setDevicePassword("999999");
                    if (GPSMonitor.bValidData && GPSMonitor.wszLatitude != null && !GPSMonitor.wszLatitude.trim().equals("") && GPSMonitor.wszLongitude != null && !GPSMonitor.wszLongitude.trim().equals("")) {
                        AttendanceFragment.this.latitudeTmp = GPSMonitor.wszLatitude;
                        LogUtils.d("AttendanceFragment", "langitudeTmp:" + AttendanceFragment.this.latitudeTmp);
                        String[] strArrSplit = String.valueOf(Double.valueOf(AttendanceFragment.this.latitudeTmp).doubleValue() / 100.0d).split("\\.");
                        if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                            reportInfoModel.setLatitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue())));
                            LogUtils.d("AttendanceFragment", "latValue:" + reportInfoModel.getLatitude());
                            AttendanceFragment.this.longitudeTmp = GPSMonitor.wszLongitude;
                            LogUtils.d("AttendanceFragment", "longitudeTmp:" + AttendanceFragment.this.longitudeTmp);
                            String[] strArrSplit2 = String.valueOf(Double.valueOf(AttendanceFragment.this.longitudeTmp).doubleValue() / 100.0d).split("\\.");
                            if (strArrSplit2.length > 1 && strArrSplit2[1].length() > 2) {
                                reportInfoModel.setLongitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue())));
                                LogUtils.d("AttendanceFragment", "longValue:" + reportInfoModel.getLongitude());
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
                    reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
                    reportInfoModel.setSimCardIccid(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
                    SocketManage.getInstance().sendReq(Generate808ReqPackage.generateDriverLoginLogout(reportInfoModel));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playTTSTest(final String str) {
        stopTTS();
        controlHorn(false, false, true, 0);
        if (this.cardTestTimer == null) {
            this.cardTestTimer = new Timer();
        }
        this.cardTestTimer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.AttendanceFragment.2
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (AttendanceFragment.this.systemTTS != null) {
                    LogUtils.d("AttendanceFragment", "playDispatchTTS systemTTS开始");
                    AttendanceFragment.this.systemTTS.playText(str);
                }
            }
        }, 300L);
    }

    private void initTTS() {
        this.systemTTS = SystemTTS.getInstance(getContext());
    }

    private void stopTTS() {
        controlHorn(false, false, false, 0);
        SystemTTS systemTTS = this.systemTTS;
        if (systemTTS == null || !systemTTS.isPlaying()) {
            return;
        }
        this.systemTTS.stopSpeak();
    }

    private boolean controlHorn(boolean z, boolean z2, boolean z3, int i) {
        GpioOperation.gpioWrite("gpio1b1", z ? "1" : "0");
        GpioOperation.gpioWrite("gpio1b2", z2 ? "1" : "0");
        GpioOperation.gpioWrite("gpio3a6", z3 ? "0" : "1");
        GpioOperation.gpioWrite("gpio0d6", z3 ? "1" : "0");
        if (i == 0) {
            if (z) {
                this.audioManager.setStreamVolume(3, Integer.valueOf((String) SPUserInfoUtils.get(getContext(), "InnerVolume", "7")).intValue(), 0);
            }
            if (!z2) {
                return true;
            }
            this.audioManager.setStreamVolume(3, Integer.valueOf((String) SPUserInfoUtils.get(getContext(), "ExternalVolume", "7")).intValue(), 0);
            return true;
        }
        if (i != 2) {
            return true;
        }
        if (z) {
            this.audioManager.setStreamVolume(3, Integer.valueOf((String) SPUserInfoUtils.get(getContext(), "TTSInnerVolume", "7")).intValue(), 0);
        }
        if (!z2) {
            return true;
        }
        this.audioManager.setStreamVolume(3, Integer.valueOf((String) SPUserInfoUtils.get(getContext(), "TTSExternalVolume", "7")).intValue(), 0);
        return true;
    }

    private void initRfidTest() {
        Log.v("AttendanceFragment", "read rfid test init");
        RfidTestThread rfidTestThread = new RfidTestThread();
        this.rfidTestThread = rfidTestThread;
        rfidTestThread.start();
    }

    private class RfidTestThread extends Thread {
        private byte[] rfidID;
        private boolean suspend;

        private RfidTestThread() {
            this.suspend = false;
            this.rfidID = new byte[16];
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            while (!this.suspend) {
                AttendanceFragment.this.setTvPaycard();
                try {
                    Thread.sleep(400L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTvPaycard() {
        getActivity().runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.AttendanceFragment.3
            @Override // java.lang.Runnable
            public void run() {
                String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DRIVERCARDID, "").toString();
                if (string.length() > 0) {
                    try {
                        AttendanceFragment.this.etCardID.setText(JavaUtils.stringToGBK(string.substring(8, 16)));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    AttendanceFragment.this.toastShow("setTvPaycard:" + string);
                }
                AttendanceFragment.this.tvDriverName.setText(SPUserInfoUtils.get(AttendanceFragment.this.getActivity(), SPUserInfoUtils.DRIVERNAME, "").toString());
            }
        });
    }

    private void closeRfid() {
        RfidTestThread rfidTestThread = this.rfidTestThread;
        if (rfidTestThread != null) {
            rfidTestThread.setSuspend(true);
        }
    }
}
