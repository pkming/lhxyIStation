package com.lianhexinye.m90.ui.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.videoeditor.MediaProperties;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.util.TimedRemoteCaller;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.gpsserialport.GPSSerialPort;
import com.lianhexinye.iicport.I2CPort;
import com.lianhexinye.jhyserialport.JHYSerialPort;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.Constants;
import com.lianhexinye.m90.common.service.FTPSendFilesService;
import com.lianhexinye.m90.common.service.GSMPhoneStatListener;
import com.lianhexinye.m90.common.service.NetWorkReceiver;
import com.lianhexinye.m90.common.service.NetWorkStateReceiver;
import com.lianhexinye.m90.common.service.ftp.FTPDownloadService;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.language.SPUtil;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.common.widget.DialogDriverSignInView;
import com.lianhexinye.m90.common.widget.DialogDriverSignOutView;
import com.lianhexinye.m90.common.widget.DialogSingleView;
import com.lianhexinye.m90.gpio.GpioOperation;
import com.lianhexinye.m90.gps.Function;
import com.lianhexinye.m90.gps.GPSMonitor;
import com.lianhexinye.m90.gps.GPSSerialPortResultListener;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import com.lianhexinye.m90.jhy.JHYMonitor;
import com.lianhexinye.m90.jhy.JHYSerialPortResultListener;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.GPSOperationParam;
import com.lianhexinye.m90.mvp.main.GPSReportStationResult;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.mvp.main.VoicePlayOperationParam;
import com.lianhexinye.m90.serialport.DVRProtocol;
import com.lianhexinye.m90.serialport.JHYProtocol;
import com.lianhexinye.m90.serialport.NiKeProtocol;
import com.lianhexinye.m90.serialport.ProtocolResult;
import com.lianhexinye.m90.serialport.SerialPortManager;
import com.lianhexinye.m90.socket.SocketIntercomManage;
import com.lianhexinye.m90.socket.SocketManage;
import com.lianhexinye.m90.socket.request.Generate808ReqPackage;
import com.lianhexinye.m90.socket.request.GenerateReqPackage;
import com.lianhexinye.m90.tts.ICallBack;
import com.lianhexinye.m90.tts.SystemTTS;
import com.lianhexinye.serialprot.ComBean;
import com.lianhexinye.serialprot.SerialHelper;
import engineer.jsp.g711a.G711a;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.poi.hssf.record.formula.AttrPtg;
import org.apache.poi.hssf.record.formula.MissingArgPtg;
import org.apache.poi.hssf.record.formula.ParenthesisPtg;
import org.apache.poi.hssf.record.formula.UnaryMinusPtg;
import org.apache.poi.hssf.record.formula.UnaryPlusPtg;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView {
    private G711a.Result acceptResult;
    private AudioManager audioManager;
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private Timer authorTimer;

    @BindView(R.id.butCease)
    Button butCease;

    @BindView(R.id.butESC)
    Button butESC;

    @BindView(R.id.butKeyboard)
    Button butKeyboard;

    @BindView(R.id.butMainDown)
    Button butMainDown;

    @BindView(R.id.butMainMsg)
    Button butMainMsg;

    @BindView(R.id.butMainQuery)
    Button butMainQuery;

    @BindView(R.id.butMainUp)
    Button butMainUp;

    @BindView(R.id.butMenu)
    Button butMenu;

    @BindView(R.id.butNewspaperStation)
    Button butNewspaperStation;

    @BindView(R.id.butNumberF0)
    Button butNumberF0;

    @BindView(R.id.butNumberF1)
    Button butNumberF1;

    @BindView(R.id.butNumberF2)
    Button butNumberF2;

    @BindView(R.id.butNumberF3)
    Button butNumberF3;

    @BindView(R.id.butNumberF4)
    Button butNumberF4;

    @BindView(R.id.butNumberF5)
    Button butNumberF5;

    @BindView(R.id.butNumberF6)
    Button butNumberF6;

    @BindView(R.id.butNumberF7)
    Button butNumberF7;

    @BindView(R.id.butNumberF8)
    Button butNumberF8;

    @BindView(R.id.butNumberF9)
    Button butNumberF9;

    @BindView(R.id.butRepeat)
    Button butRepeat;

    @BindView(R.id.butSwitch)
    Button butSwitch;

    @BindView(R.id.butVideo)
    Button butVideo;
    private Timer closeTimerTip;
    private DialogDriverSignInView dialogDriverSignInView;
    private DialogDriverSignOutView dialogDriverSignOutView;
    private int firstDirection;
    private String firstLineNumber;
    private Timer gpsPortTimer;
    private GPSReport gpsReport;
    private GPSThread gpsThread;
    private String iOpenSpeeding;

    @BindView(R.id.imageInformation)
    ImageView imageInformation;
    private InitThread initThread;
    private IntercomBagThread intercomBagThread;
    private JHYThread jhyThread;
    private String lastTimeLatitude;
    private String lastTimeLongitude;
    private String latitudeTmp;
    private String longitudeTmp;

    @BindView(R.id.lyNumberkeyboard)
    LinearLayout lyNumberkeyboard;

    @BindView(R.id.lyPlannedTime)
    RelativeLayout lyPlannedTime;

    @BindView(R.id.lySwitchInfo)
    ConstraintLayout lySwitchInfo;

    @BindView(R.id.lyToolbarRight)
    LinearLayout lyToolbarRight;

    @BindView(R.id.lyVideoDVRImage)
    LinearLayout lyVideoDVRImage;

    @BindView(R.id.lyVideoImage)
    LinearLayout lyVideoImage;
    private I2CPort mDrvRfid;
    private Timer messageTimer;
    private MsgReceiver msgReceiver;
    private NetWorkReceiver netWorkReceiver;
    private NetWorkStateReceiver netWorkStateReceiver;
    private GSMPhoneStatListener phoneSignalStateListener;
    private String protocol4852Type;
    private String protocolType;
    private RfidThread rfidThread;

    @BindView(R.id.rlBusInfo)
    RelativeLayout rlBusInfo;

    @BindView(R.id.rlLineDriver)
    RelativeLayout rlLineDriver;

    @BindView(R.id.rlLineInfo)
    RelativeLayout rlLineInfo;

    @BindView(R.id.rlOperation)
    LinearLayout rlOperation;

    @BindView(R.id.rlPcs001)
    RelativeLayout rlPcs001;

    @BindView(R.id.rlSpeedLimit)
    RelativeLayout rlSpeedLimit;

    @BindView(R.id.rlToolbar)
    LinearLayout rlToolbar;
    private G711a.Result sendData;
    private String speedPositionTmp;
    private String speedStationTmp;
    private String speedTmp;
    private SystemTTS systemTTS;
    private TelephonyManager telephoneyManager;
    private Timer timer;
    private Timer timer1;
    private Timer timer2;
    private Timer timer3;

    @BindView(R.id.tv4G)
    TextView tv4G;

    @BindView(R.id.tvAll001)
    TextView tvAll001;

    @BindView(R.id.tvBin001)
    TextView tvBin001;

    @BindView(R.id.tvBout001)
    TextView tvBout001;

    @BindView(R.id.tvCMS)
    TextView tvCMS;

    @BindView(R.id.tvCarNumber)
    TextView tvCarNumber;

    @BindView(R.id.tvDatetime)
    TextClock tvDatetime;

    @BindView(R.id.tvDriver)
    Button tvDriver;

    @BindView(R.id.tvEndBus)
    TextView tvEndBus;

    @BindView(R.id.tvEndBusName)
    TextView tvEndBusName;

    @BindView(R.id.tvFin001)
    TextView tvFin001;

    @BindView(R.id.tvFout001)
    TextView tvFout001;

    @BindView(R.id.tvHomeNextStation)
    TextView tvHomeNextStation;

    @BindView(R.id.tvHomeNextStationName)
    TextView tvHomeNextStationName;

    @BindView(R.id.tvInfoTips)
    TextView tvInfoTips;

    @BindView(R.id.tvInformation)
    TextView tvInformation;

    @BindView(R.id.tvJobNumber)
    TextView tvJobNumber;

    @BindView(R.id.tvLanState)
    TextView tvLanState;

    @BindView(R.id.tvLineDirection)
    TextView tvLineDirection;

    @BindView(R.id.tvLineNO)
    TextView tvLineNO;

    @BindView(R.id.tvLineName)
    TextView tvLineName;

    @BindView(R.id.tvLocationGps)
    TextView tvLocationGps;

    @BindView(R.id.tvMileage)
    TextView tvMileage;

    @BindView(R.id.tvNextTrip)
    TextView tvNextTrip;

    @BindView(R.id.tvPlannedTime1)
    TextView tvPlannedTime1;

    @BindView(R.id.tvShouting)
    TextView tvShouting;

    @BindView(R.id.tvSpeedLimit)
    TextView tvSpeedLimit;

    @BindView(R.id.tvThisTrip)
    TextView tvThisTrip;

    @BindView(R.id.tvTomorrow)
    TextView tvTomorrow;

    @BindView(R.id.tvVehicleStatus)
    TextView tvVehicleStatus;

    @BindView(R.id.tvWifi)
    TextView tvWifi;
    private final String TAG = "MainActivity";
    private String primaryDepartureTime = "";
    private String primaryArrivalTime = "";
    boolean isDispatchEnd = false;
    boolean isDispatchEndRelevance = false;
    private String dvrValue = "";
    private String cmsValue = "";
    private String gpsValue = "";
    private String effective = "";
    private String invalid = "";
    private String speedLimit = "";
    private String nextTrip = "";
    private String thisTrip = "";
    private String tomorrow = "";
    private String dvrState = "无效";
    private boolean isFirstPage = true;
    private int currentStation = 0;
    private int currentCross = 0;
    private boolean isNextStation = false;
    private boolean isNextCross = true;
    private final SerialPortManager serialPortManager = new SerialPortManager();
    private boolean isSwitchLine = false;
    private String dispatchProtocol = "";
    private String ttsSwitch = "否";
    private int switchType = 1;
    private final List<String> busTTSContentList = new ArrayList();
    private final List<String> busVoiceContentList = new ArrayList();
    private final List<Integer> playControlFlow = new ArrayList();
    private int busTTSContentIndex = 0;
    private int busVoiceContentIndex = 0;
    private int playControlFlowIndex = 0;
    private boolean istTTSMixture = false;
    private double totalMileage = 0.0d;
    private int deviationCount = 0;
    private int assembleGPSDataCount = 0;
    private int lastMessageNo = 0;
    private int reportType = 0;
    private boolean isVIN1AHD = true;
    IntentFilter intentHeadsetFilter = new IntentFilter();
    private final BroadcastReceiver mHeadsetOnReceiver = new BroadcastReceiver() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.getIntExtra("state", 0) == 1) {
                    LogUtils.d("MainActivity", "插入耳机");
                } else {
                    LogUtils.d("MainActivity", "拔出耳机");
                    MainActivity.this.audioManager.setStreamVolume(3, Integer.valueOf((String) SPUserInfoUtils.get(AppApplication.getContext(), "DispatchVolume", "7")).intValue(), 0);
                }
            }
        }
    };
    IntentFilter timeFilter = new IntentFilter();
    private final BroadcastReceiver mTimeReceiver = new BroadcastReceiver() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(11);
            int i2 = calendar.get(12);
            LogUtils.d("mTimeReceiver", "Time:" + System.currentTimeMillis() + ",hour:" + i);
            if (i2 == 0) {
                MainActivity.this.nowTime(i);
            }
        }
    };
    private MonitorThread monitorThread = null;
    private SurfaceView surfaceViewBackingup = null;
    private SurfaceView surfaceViewMittertor = null;
    private TextView tvBackingup = null;
    private TextView tvMittertor = null;
    private Camera cameraBackingup = null;
    private Camera cameraMittertor = null;
    private Camera.Parameters paramBackingup = null;
    private Camera.Parameters paramMittertor = null;
    private boolean previewRunningBackingup = false;
    private boolean previewRunningMittertor = false;
    private int operationStep = 0;
    private boolean firstCamera = true;
    private boolean isCamera = false;
    private boolean isScanCamera = false;
    private SurfaceView surfaceViewDVR2Camera = null;
    private TextView tvDVR2Camera = null;
    private Camera cameraDVR2 = null;
    private Camera.Parameters paramCameraDVR2 = null;
    private boolean previewCameraDVR2 = false;
    String arrivalTime = "";
    String arrivalCrossTime = "";
    private Intent intentFTPDownLoad = null;
    private final ServiceConnection connFTPDownLoad = new ServiceConnection() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.7
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    private String lineDirectionName = "S";
    private String lineName = "";
    private String lineNumber = "";
    private int lineAttribute = 0;
    private final SocketManage.SocketActionCallback socketActionCallback = new AnonymousClass9();
    private Intent intentFTPSendFiles = null;
    private ServiceConnection connFTPSendFiles = new ServiceConnection() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.10
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    boolean isRecording = false;
    private final int FREQUENCY = 8000;
    private final int CHANNELCONFIGURATION = 2;
    private final int AUDIOENCODING = 2;
    private int recBufSize = 0;
    private int playBufSize = 0;
    private int shoutingOperationStep = 0;
    private ShoutingThread shoutingThread = null;
    private int vehicleStatusId = 1;
    private final DVRProtocol.DVRCallback dvrCallback = new AnonymousClass21();
    private DialogSingleView dialogSingleView = null;
    private MediaPlayer musicPlayer = null;
    private final MediaPlayer mNextPlayer = null;
    private Timer timerPlayer = null;
    private final List<String> busVoiceList = new ArrayList();
    private final List<String> busExternalVoiceList = new ArrayList();
    private int plaIndex = 0;
    private boolean isIntercomRecording = false;
    private String intercomIP = "";
    private int intercomTcpPort = 0;
    private int intercomUPDPort = 0;
    private int channelNumber = 0;
    private int intercomType = 0;
    private final int contentCursor = 0;
    private final byte[] intercomBodyBytes = new byte[640];
    private String terminalID = "";
    private final ShoutingHandle shoutingHandle = new ShoutingHandle(this);
    private JHYSerialPortResultListener jhySerialPortResultListener = new JHYSerialPortResultListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.36
        @Override // com.lianhexinye.m90.jhy.JHYSerialPortResultListener
        public void onFailResult() {
        }

        @Override // com.lianhexinye.m90.jhy.JHYSerialPortResultListener
        public void onSuccessResult(String str) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.36.1
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.tvFin001 != null && !MainActivity.this.isFinishing() && !MainActivity.this.isDestroyed()) {
                        if (!JavaUtils.isEmpty(JHYMonitor.qjString)) {
                            MainActivity.this.tvFin001.setText(JHYMonitor.qjString);
                        } else {
                            MainActivity.this.tvFin001.setText("N");
                        }
                    } else {
                        MainActivity.this.toastShow("Please check1 data!!!");
                    }
                    if (MainActivity.this.tvFout001 != null && !MainActivity.this.isFinishing() && !MainActivity.this.isDestroyed()) {
                        if (!JavaUtils.isEmpty(JHYMonitor.qcString)) {
                            MainActivity.this.tvFout001.setText(JHYMonitor.qcString);
                        } else {
                            MainActivity.this.tvFout001.setText("N");
                        }
                    } else {
                        MainActivity.this.toastShow("Please check2 data!!!");
                    }
                    if (MainActivity.this.tvBin001 != null && !MainActivity.this.isFinishing() && !MainActivity.this.isDestroyed()) {
                        if (!JavaUtils.isEmpty(JHYMonitor.hjString)) {
                            MainActivity.this.tvBin001.setText(JHYMonitor.hjString);
                        } else {
                            MainActivity.this.tvBin001.setText("N");
                        }
                    } else {
                        MainActivity.this.toastShow("Please check3 data!!!");
                    }
                    if (MainActivity.this.tvBout001 != null && !MainActivity.this.isFinishing() && !MainActivity.this.isDestroyed()) {
                        if (!JavaUtils.isEmpty(JHYMonitor.hcString)) {
                            MainActivity.this.tvBout001.setText(JHYMonitor.hcString);
                        } else {
                            MainActivity.this.tvBout001.setText("N");
                        }
                    } else {
                        MainActivity.this.toastShow("Please check4 data!!!");
                    }
                    if (MainActivity.this.tvAll001 != null && !MainActivity.this.isFinishing() && !MainActivity.this.isDestroyed()) {
                        if (!JavaUtils.isEmpty(JHYMonitor.allString)) {
                            MainActivity.this.tvAll001.setText(JHYMonitor.allString);
                            return;
                        } else {
                            MainActivity.this.tvAll001.setText("N");
                            return;
                        }
                    }
                    MainActivity.this.toastShow("Please check5 data!!!");
                }
            });
        }
    };
    boolean Running = true;
    private final GPSOperationParam gpsOperationParam = new GPSOperationParam();
    private GPSReportStationResult gpsReportStationResult = null;
    private long validDataTime = 0;
    private long speedTime = 0;
    private long intervalTime = 0;
    private boolean iSpeed = false;
    private String speedingInterval = "10";
    private boolean iValidDataTime = false;
    private boolean iGpsInit = false;
    private boolean isCharteredBus = false;
    private GPSSerialPortResultListener gpsSerialPortResultListener = new AnonymousClass38();
    ICallBack iCallBack = new ICallBack() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.39
        @Override // com.lianhexinye.m90.tts.ICallBack
        public void onCompleted(int i) {
            LogUtils.d("MainActivity", "tts播放完成");
            if (i != 0) {
                return;
            }
            if (MainActivity.this.istTTSMixture && MainActivity.this.playControlFlow.size() > MainActivity.this.playControlFlowIndex + 1) {
                MainActivity.access$10608(MainActivity.this);
                LogUtils.d("MainActivity", "playControlFlowIndex:" + MainActivity.this.playControlFlowIndex);
                if (((Integer) MainActivity.this.playControlFlow.get(MainActivity.this.playControlFlowIndex)).intValue() == 0) {
                    MainActivity.access$10708(MainActivity.this);
                    LogUtils.d("MainActivity", "busTTSContentIndex:" + MainActivity.this.busTTSContentIndex);
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.playMixtureTTS((String) mainActivity.busTTSContentList.get(MainActivity.this.busTTSContentIndex));
                    return;
                }
                MainActivity.access$11008(MainActivity.this);
                LogUtils.d("MainActivity", "busVoiceContentIndex:" + MainActivity.this.busVoiceContentIndex);
                return;
            }
            LogUtils.d("MainActivity", "tts结束");
            MainActivity.this.istTTSMixture = false;
            MainActivity.this.controlHorn(false, false, false, 0);
            MainActivity.this.sendLCDOpenSound();
        }
    };
    private final SocketIntercomManage.SocketIntercomCallback socketIntercomCallback = new SocketIntercomManage.SocketIntercomCallback() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.42
        @Override // com.lianhexinye.m90.socket.SocketIntercomManage.SocketIntercomCallback
        public void onSocketClose(String str) {
        }

        @Override // com.lianhexinye.m90.socket.SocketIntercomManage.SocketIntercomCallback
        public void onSocketConnectionFailed(String str) {
        }

        @Override // com.lianhexinye.m90.socket.SocketIntercomManage.SocketIntercomCallback
        public void onSocketConnectionSuccess() {
        }

        @Override // com.lianhexinye.m90.socket.SocketIntercomManage.SocketIntercomCallback
        public void onSocketDisconnection(String str) {
        }

        @Override // com.lianhexinye.m90.socket.SocketIntercomManage.SocketIntercomCallback
        public void onSocketReadResponse(int i, String str) {
        }

        @Override // com.lianhexinye.m90.socket.SocketIntercomManage.SocketIntercomCallback
        public void onSocketTimeoutException(String str) {
        }

        @Override // com.lianhexinye.m90.socket.SocketIntercomManage.SocketIntercomCallback
        public void onSocketRead808Response(int i, String str) {
            MainActivity.this.audioRecordPay(JavaUtils.hexStringToBytes(str));
        }
    };
    private final byte[] synchronizationByte = new byte[0];
    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.file.FileView
    public void hideLoading() {
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
    }

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void showLoading(String str) {
    }

    static /* synthetic */ int access$10608(MainActivity mainActivity) {
        int i = mainActivity.playControlFlowIndex;
        mainActivity.playControlFlowIndex = i + 1;
        return i;
    }

    static /* synthetic */ int access$10708(MainActivity mainActivity) {
        int i = mainActivity.busTTSContentIndex;
        mainActivity.busTTSContentIndex = i + 1;
        return i;
    }

    static /* synthetic */ int access$11008(MainActivity mainActivity) {
        int i = mainActivity.busVoiceContentIndex;
        mainActivity.busVoiceContentIndex = i + 1;
        return i;
    }

    static /* synthetic */ int access$11508(MainActivity mainActivity) {
        int i = mainActivity.plaIndex;
        mainActivity.plaIndex = i + 1;
        return i;
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        checkNeedPermissions();
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        this.butVideo.setEnabled(false);
        readConfiguration();
        initView();
        InitThread initThread = new InitThread();
        this.initThread = initThread;
        initThread.start();
        if (!((String) SPUserInfoUtils.get(AppApplication.getContext(), "RS485Protocol", "无")).trim().equals("无")) {
            this.protocolType = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "RS485Protocol", "无")).trim();
            open485SerialPort();
        }
        initNetwork();
        openCameraThread();
        controlHorn(false, false, false, 0);
        AppApplication.opd = new GPSSerialPort();
        if (((String) SPUserInfoUtils.get(AppApplication.getContext(), "gpsportbaud", "115200")).trim().equals("115200")) {
            AppApplication.opd.open("/dev/ttyS5", 115200);
        } else {
            AppApplication.opd.open("/dev/ttyS5", 9600);
        }
        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.SIGNINOUTSTATUS, "1");
        this.tvDriver.setText(getResources().getString(R.string.main_logout));
        this.dvrValue = getResources().getString(R.string.main_dvr_value);
        this.cmsValue = getResources().getString(R.string.main_cms_value);
        this.gpsValue = getResources().getString(R.string.main_gps_value);
        AppApplication.cmsState = getResources().getString(R.string.unconnected);
        this.tvCMS.setText(String.format(this.cmsValue, AppApplication.cmsState));
        this.tvLocationGps.setText(String.format(this.gpsValue, this.invalid));
        SocketIntercomManage.getInstance().registerCallback(this.socketIntercomCallback);
        if (!((String) SPUserInfoUtils.get(AppApplication.getContext(), "RS485-2Protocol", "无")).trim().equals("无")) {
            this.protocol4852Type = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "RS485-2Protocol", "无")).trim();
            AppApplication.jhyopd = new JHYSerialPort();
            AppApplication.jhyopd.open("/dev/ttyS9", 9600);
        }
        if (!((String) SPUserInfoUtils.get(AppApplication.getContext(), "DVR-Channel", "VIN1-AHD")).trim().equals("VIN1-AHD")) {
            this.isVIN1AHD = false;
        }
        this.lastTimeLatitude = SPUserInfoUtils.get(this, SPUserInfoUtils.LASTTIMELATITUDE, "").toString();
        this.lastTimeLongitude = SPUserInfoUtils.get(this, SPUserInfoUtils.LASTTIMELONGITUDE, "").toString();
        this.totalMileage = Double.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.TOTALMILEAGE, "0.0").toString()).doubleValue();
        this.lastMessageNo = Integer.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.MESSAGENO, "0").toString()).intValue();
        initTTS();
        openJHYPort();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        startTimer();
        startListenHeadsetOn();
        if (!this.isCharteredBus) {
            refreshBus();
        }
        openGPSPort();
        this.vehicleStatusId = Integer.parseInt(SPUserInfoUtils.get(this, SPUserInfoUtils.VEHICLESTATUSID, 1).toString());
        openShouting();
        this.isScanCamera = true;
        this.firstCamera = true;
        initRfid();
    }

    private void initView() {
        Resources resources;
        this.primaryDepartureTime = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.PRIMARYDEPARTURETIME, "").toString();
        this.primaryArrivalTime = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.PRIMARYARRIVALTIME, "").toString();
        this.isDispatchEnd = ((Boolean) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHEND, false)).booleanValue();
        if (SPUtil.getInstance(this).getSelectLanguage() == 3) {
            this.butNewspaperStation.setTextSize(getResources().getDimension(R.dimen.main_but_size2));
            this.butRepeat.setTextSize(getResources().getDimension(R.dimen.main_but_size2));
            this.butSwitch.setTextSize(getResources().getDimension(R.dimen.main_but_size2));
        }
        this.effective = getResources().getString(R.string.effective);
        this.invalid = getResources().getString(R.string.invalid);
        this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        String str = (String) SPUserInfoUtils.get(this, SPUserInfoUtils.DEPARTURETIME, "");
        if (str != null && !str.trim().equals("")) {
            String str2 = (String) SPUserInfoUtils.get(this, SPUserInfoUtils.ARRIVALTIME, "");
            String string = SPUserInfoUtils.get(this, SPUserInfoUtils.GUIDEBOARDNAME, "0").toString();
            String[] strArrSplit = str.trim().split(":");
            String[] strArrSplit2 = str2.trim().split(":");
            this.tvPlannedTime1.setText(Html.fromHtml(getString(R.string.main_planned_time, string, strArrSplit[0].substring(strArrSplit[0].length() - 2) + ":" + strArrSplit[1], strArrSplit2[0].substring(strArrSplit2[0].length() - 2) + ":" + strArrSplit2[1])));
        } else {
            this.tvPlannedTime1.setText(Html.fromHtml(getString(R.string.main_planned_time, "--", "-- : --", "-- : --")));
        }
        this.tvLineNO.setText(R.string.main_order_number);
        getResources().getString(R.string.main_driver);
        String[] strArrSplit3 = ((String) SPUserInfoUtils.get(this, SPUserInfoUtils.CARDSTATUSINFO, "0")).split("\\*");
        int length = strArrSplit3.length;
        int i = R.string.main_logout;
        if (length > 1) {
            Button button = this.tvDriver;
            if (strArrSplit3[1].equals("0")) {
                resources = getResources();
                i = R.string.main_login;
            } else {
                resources = getResources();
            }
            button.setText(resources.getString(i));
        } else {
            this.tvDriver.setText(getResources().getString(R.string.main_logout));
        }
        String str3 = (String) SPUserInfoUtils.get(this, SPUserInfoUtils.NEXTTRIP, "");
        this.nextTrip = str3;
        if (str3.length() > 0) {
            this.tvNextTrip.setText(this.nextTrip);
            this.tvNextTrip.setVisibility(0);
        }
        String str4 = (String) SPUserInfoUtils.get(this, SPUserInfoUtils.THISTRIP, "");
        this.thisTrip = str4;
        if (str4.length() > 0) {
            this.tvThisTrip.setText(this.thisTrip);
            this.tvThisTrip.setVisibility(0);
        }
        String str5 = (String) SPUserInfoUtils.get(this, SPUserInfoUtils.TOMORROW, "");
        this.tomorrow = str5;
        if (str5.length() > 0) {
            this.tvTomorrow.setText(this.tomorrow);
            this.tvTomorrow.setVisibility(0);
        }
        getResources().getString(R.string.main_line_en);
        this.tvCarNumber.setText(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.CARNUMBER, "").toString());
        this.tvJobNumber.setText(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.JOBNUMBER, "").toString());
        this.tvMileage.setText("0");
        this.tvSpeedLimit.setText(TarConstants.VERSION_POSIX);
        this.speedLimit = "0";
        this.tvLanState.setText(String.format(getResources().getString(R.string.main_lan_value), getResources().getString(R.string.unconnected)));
    }

    private void initDriverSignDlg() {
        this.tvDriver.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                final ReportInfoModel reportInfoModel = new ReportInfoModel();
                String str = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.SIGNINOUTSTATUS, "1");
                str.hashCode();
                if (str.equals("0")) {
                    MainActivity.this.dialogDriverSignOutView = new DialogDriverSignOutView(MainActivity.this);
                    MainActivity.this.dialogDriverSignOutView.setOnOKClickListener(new DialogDriverSignOutView.OnOKClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.1.2
                        @Override // com.lianhexinye.m90.common.widget.DialogDriverSignOutView.OnOKClickListener
                        public void onSuccessClick(String str2, String str3) {
                            reportInfoModel.setdStatus(1);
                            try {
                                byte[] bytes = str2.getBytes("gb2312");
                                reportInfoModel.setCardNo(JavaUtils.bytesToHexString(bytes, bytes.length));
                                reportInfoModel.setDeviceSignMode(1);
                                reportInfoModel.setTerminalDate(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
                                reportInfoModel.setTransmissionType("09");
                                reportInfoModel.setDeviceVersion(String.valueOf(AndroidUtils.getLocalVersion()));
                                reportInfoModel.setDevicePassword(str3);
                                if (GPSMonitor.bValidData && GPSMonitor.wszLatitude != null && !GPSMonitor.wszLatitude.trim().equals("") && GPSMonitor.wszLongitude != null && !GPSMonitor.wszLongitude.trim().equals("")) {
                                    MainActivity.this.latitudeTmp = GPSMonitor.wszLatitude;
                                    LogUtils.d("MainActivity", "langitudeTmp:" + MainActivity.this.latitudeTmp);
                                    String[] strArrSplit = String.valueOf(Double.valueOf(MainActivity.this.latitudeTmp).doubleValue() / 100.0d).split("\\.");
                                    if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                                        reportInfoModel.setLatitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue())));
                                        LogUtils.d("MainActivity", "latValue:" + reportInfoModel.getLatitude());
                                        MainActivity.this.longitudeTmp = GPSMonitor.wszLongitude;
                                        LogUtils.d("MainActivity", "longitudeTmp:" + MainActivity.this.longitudeTmp);
                                        String[] strArrSplit2 = String.valueOf(Double.valueOf(MainActivity.this.longitudeTmp).doubleValue() / 100.0d).split("\\.");
                                        if (strArrSplit2.length > 1 && strArrSplit2[1].length() > 2) {
                                            reportInfoModel.setLongitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue())));
                                            LogUtils.d("MainActivity", "longValue:" + reportInfoModel.getLongitude());
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
                            MainActivity.this.dialogDriverSignOutView.dismiss();
                        }

                        @Override // com.lianhexinye.m90.common.widget.DialogDriverSignOutView.OnOKClickListener
                        public void onCancelClick() {
                            MainActivity.this.dialogDriverSignOutView.dismiss();
                        }
                    });
                    MainActivity.this.dialogDriverSignOutView.show();
                    return;
                }
                if (str.equals("1")) {
                    MainActivity.this.dialogDriverSignInView = new DialogDriverSignInView(MainActivity.this);
                    MainActivity.this.dialogDriverSignInView.setOnOKClickListener(new DialogDriverSignInView.OnOKClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.1.1
                        @Override // com.lianhexinye.m90.common.widget.DialogDriverSignInView.OnOKClickListener
                        public void onSuccessClick(String str2, String str3) {
                            reportInfoModel.setdStatus(0);
                            SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DRIVERCARDID, "38383838" + JavaUtils.stringToHexadecimal(str2) + "00000000000000");
                            try {
                                byte[] bytes = str2.getBytes("gb2312");
                                reportInfoModel.setCardNo(JavaUtils.bytesToHexString(bytes, bytes.length));
                                reportInfoModel.setDeviceSignMode(1);
                                reportInfoModel.setTerminalDate(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
                                reportInfoModel.setTransmissionType("09");
                                reportInfoModel.setDeviceVersion(String.valueOf(AndroidUtils.getLocalVersion()));
                                reportInfoModel.setDevicePassword(str3);
                                if (GPSMonitor.bValidData && GPSMonitor.wszLatitude != null && !GPSMonitor.wszLatitude.trim().equals("") && GPSMonitor.wszLongitude != null && !GPSMonitor.wszLongitude.trim().equals("")) {
                                    MainActivity.this.latitudeTmp = GPSMonitor.wszLatitude;
                                    LogUtils.d("MainActivity", "langitudeTmp:" + MainActivity.this.latitudeTmp);
                                    String[] strArrSplit = String.valueOf(Double.valueOf(MainActivity.this.latitudeTmp).doubleValue() / 100.0d).split("\\.");
                                    if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                                        reportInfoModel.setLatitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue())));
                                        LogUtils.d("MainActivity", "latValue:" + reportInfoModel.getLatitude());
                                        MainActivity.this.longitudeTmp = GPSMonitor.wszLongitude;
                                        LogUtils.d("MainActivity", "longitudeTmp:" + MainActivity.this.longitudeTmp);
                                        String[] strArrSplit2 = String.valueOf(Double.valueOf(MainActivity.this.longitudeTmp).doubleValue() / 100.0d).split("\\.");
                                        if (strArrSplit2.length > 1 && strArrSplit2[1].length() > 2) {
                                            reportInfoModel.setLongitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue())));
                                            LogUtils.d("MainActivity", "longValue:" + reportInfoModel.getLongitude());
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
                            MainActivity.this.dialogDriverSignInView.dismiss();
                        }

                        @Override // com.lianhexinye.m90.common.widget.DialogDriverSignInView.OnOKClickListener
                        public void onCancelClick() {
                            MainActivity.this.dialogDriverSignInView.dismiss();
                        }
                    });
                    MainActivity.this.dialogDriverSignInView.show();
                }
            }
        });
    }

    private void initNetwork() {
        final String string = getResources().getString(R.string.main_wifi_value);
        if (this.netWorkStateReceiver == null) {
            NetWorkStateReceiver netWorkStateReceiver = new NetWorkStateReceiver();
            this.netWorkStateReceiver = netWorkStateReceiver;
            netWorkStateReceiver.setUpdateActivityUI(new NetWorkStateReceiver.wifiUpdateActivityUI() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.2
                @Override // com.lianhexinye.m90.common.service.NetWorkStateReceiver.wifiUpdateActivityUI
                public void updateUI(String str) {
                    if (AppApplication.wifiState.equals("UNCON") && str.equals("OK") && !GPSMonitor.bValidData) {
                        LogUtils.d("NetWorkStateReceiver", "发送时间：" + str);
                        if (AppApplication.mSerial485Control != null && AppApplication.mSerial485Control.isOpen()) {
                            AppApplication.mSerial485Control.send(MainActivity.this.serialPortManager.crateTongdaProtocol().createLineState());
                        }
                    }
                    AppApplication.wifiState = str;
                    MainActivity.this.tvWifi.setText(String.format(string, str));
                }
            });
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.netWorkStateReceiver, intentFilter);
        this.telephoneyManager = (TelephonyManager) getSystemService("phone");
        this.phoneSignalStateListener = new GSMPhoneStatListener();
        final String string2 = getResources().getString(R.string.main_4g_value);
        this.phoneSignalStateListener.setUpdateActivityUI(new GSMPhoneStatListener.GSMUpdateActivityUI() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.3
            @Override // com.lianhexinye.m90.common.service.GSMPhoneStatListener.GSMUpdateActivityUI
            public void updateUI(String str) {
                if (MainActivity.this.tv4G != null) {
                    if (AppApplication.simState.equals("UNCON") && str.equals("OK") && !GPSMonitor.bValidData) {
                        LogUtils.d("GSMPhoneStatListener", "发送时间：" + str);
                        AppApplication.mSerial485Control.send(MainActivity.this.serialPortManager.crateTongdaProtocol().createLineState());
                    }
                    AppApplication.simState = str;
                    MainActivity.this.tv4G.setText(String.format(string2, str));
                    LogUtils.d("GSMPhoneStatListener", "更新simUI:" + str);
                }
            }
        });
        this.telephoneyManager.listen(this.phoneSignalStateListener, 256);
    }

    private void initBusView() {
        if (AppApplication.listBusLine == null || AppApplication.listBusLine.size() <= 0) {
            return;
        }
        BusLineModel busLineModel = AppApplication.listBusLine.get(0);
        this.lyPlannedTime.setVisibility(0);
        this.lySwitchInfo.setVisibility(0);
        this.tvHomeNextStation.setText(R.string.main_home_station);
        this.tvHomeNextStationName.setText(busLineModel.getBusName());
        if (busLineModel.getSpeedLimit() != null && !busLineModel.getSpeedLimit().trim().equals("")) {
            this.tvSpeedLimit.setText(busLineModel.getSpeedLimit().trim());
            this.speedLimit = busLineModel.getSpeedLimit().trim();
        } else {
            this.tvSpeedLimit.setText(TarConstants.VERSION_POSIX);
            this.speedLimit = "0";
        }
        this.tvEndBusName.setText(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
        this.tvLineName.setText(String.format(getResources().getString(R.string.main_line_en), busLineModel.getBusLineName()));
        modifyLineNO(busLineModel.getBusNo());
        controlBusLine(0, false);
    }

    private void startListenHeadsetOn() {
        this.intentHeadsetFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(this.mHeadsetOnReceiver, this.intentHeadsetFilter);
    }

    private void startTimer() {
        if (((String) SPUserInfoUtils.get(this, "IfIntegralPoint", "否")).trim().equals("是")) {
            this.timeFilter.addAction(Intent.ACTION_TIME_TICK);
            registerReceiver(this.mTimeReceiver, this.timeFilter);
        }
    }

    public class MsgReceiver extends BroadcastReceiver {
        public MsgReceiver() {
        }

        /* JADX WARN: Type inference failed for: r7v11, types: [com.lianhexinye.m90.ui.activity.MainActivity$MsgReceiver$1] */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("operation", 0);
            LogUtils.d("MsgReceiver", "Time:" + System.currentTimeMillis() + ",operation:" + intExtra);
            switch (intExtra) {
                case 0:
                    MainActivity.this.Running = false;
                    return;
                case 1:
                    boolean zBooleanValue = ((Boolean) SPUserInfoUtils.get(AppApplication.getInstance(), SPUserInfoUtils.ISCONFIGVALID, true)).booleanValue();
                    LogUtils.d("MsgReceiver", "isConfigValid:" + zBooleanValue);
                    MainActivity.this.tvInfoTips.setText("");
                    if (zBooleanValue) {
                        MainActivity.this.closeFTPDownloadService();
                        SocketManage.getInstance().closeAllSocket();
                        new Thread() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.MsgReceiver.1
                            @Override // java.lang.Thread, java.lang.Runnable
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(1000L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.MsgReceiver.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        ((PowerManager) MainActivity.this.getSystemService(Context.POWER_SERVICE)).reboot("重启");
                                    }
                                });
                            }
                        }.start();
                        return;
                    }
                    MainActivity.this.openGPSPort();
                    return;
                case 2:
                    MainActivity.this.tvInfoTips.setText("");
                    try {
                        MainActivity.this.closeFTPDownloadService();
                        SocketManage.getInstance().closeAllSocket();
                        Intent launchIntentForPackage = MainActivity.this.getPackageManager().getLaunchIntentForPackage("com.lianhexinye.rebootm90");
                        if (launchIntentForPackage != null) {
                            int selectLanguage = SPUtil.getInstance(AppApplication.getContext()).getSelectLanguage();
                            if (selectLanguage > 2) {
                                selectLanguage = 3;
                            }
                            launchIntentForPackage.putExtra("seleLanguage", "" + selectLanguage);
                            launchIntentForPackage.addFlags(268435456);
                            MainActivity.this.startActivity(launchIntentForPackage);
                        } else {
                            LogUtils.d("packageManager", "没有对应的intentReboot");
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                case 3:
                    MainActivity.this.tvInfoTips.setText("");
                    MainActivity.this.closeFTPDownloadService();
                    return;
                case 4:
                    MainActivity.this.tvInfoTips.setText("APK is upgrading...");
                    return;
                case 5:
                case 6:
                case 10:
                case 11:
                default:
                    return;
                case 7:
                    MainActivity.this.openGPSPort();
                    MainActivity.this.tvInfoTips.setText("网络更新的资源解析失败，请检查更新资源");
                    return;
                case 8:
                    MainActivity.this.tvInfoTips.setText("正在下载更新资源...");
                    return;
                case 9:
                    MainActivity.this.closeFTPDownloadService();
                    MainActivity.this.tvInfoTips.setText("");
                    return;
                case 12:
                    MainActivity.this.tvInfoTips.setText("Download M90 APK,process：" + intent.getIntExtra("operationmsg", 0) + "%");
                    return;
                case 13:
                    MainActivity.this.tvInfoTips.setText("Download SourceFile,process：" + intent.getIntExtra("operationmsg", 0) + "%");
                    break;
                case 14:
                    break;
                case 15:
                    MainActivity.this.tvInfoTips.setText("");
                    MainActivity.this.closeFTPTCPDownloadService();
                    return;
                case 16:
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.MsgReceiver.2
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.toastShow("Log without。。。");
                        }
                    });
                    return;
            }
            MainActivity.this.tvInfoTips.setText("UPLOAD LOG procerr：" + intent.getIntExtra("operationmsg", 0) + "%");
        }
    }

    @OnClick({R.id.butNewspaperStation, R.id.butRepeat, R.id.butSwitch, R.id.butCease, R.id.butVideo, R.id.butMenu, R.id.butKeyboard, R.id.butESC, R.id.butNumberF1, R.id.butNumberF3, R.id.butNumberF5, R.id.butNumberF7, R.id.butNumberF9, R.id.butMainUp, R.id.butMainMsg, R.id.butNumberF2, R.id.butNumberF4, R.id.butNumberF6, R.id.butNumberF8, R.id.butNumberF0, R.id.butMainDown, R.id.butMainQuery})
    public void onViewClicked(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.butCease /* 2131296334 */:
                stopNewspaper();
                break;
            case R.id.butESC /* 2131296340 */:
                this.lyNumberkeyboard.setVisibility(8);
                this.rlOperation.setVisibility(0);
                break;
            case R.id.butKeyboard /* 2131296346 */:
                this.lyNumberkeyboard.setVisibility(0);
                this.rlOperation.setVisibility(8);
                break;
            case R.id.butMainDown /* 2131296348 */:
                if (!this.isCharteredBus) {
                    backwardNewspaper();
                }
                break;
            case R.id.butMainUp /* 2131296351 */:
                if (!this.isCharteredBus) {
                    forwardNewspaper();
                }
                break;
            case R.id.butMenu /* 2131296354 */:
                sendLCDOpenSound();
                showIntent(this, LoginActivity.class);
                break;
            case R.id.butRepeat /* 2131296370 */:
                if (!this.isCharteredBus && AppApplication.listBusLine != null && AppApplication.listBusLine.size() > 0) {
                    sendLCDQuiet();
                    repeatNewspaper();
                    break;
                }
                break;
            case R.id.butSwitch /* 2131296380 */:
                if (!this.isCharteredBus) {
                    this.isNextStation = false;
                    switchDirection();
                }
                break;
            case R.id.butVideo /* 2131296382 */:
                sendLCDOpenSound();
                showIntent(this, LineChoiceActivity.class);
                break;
            default:
                switch (id) {
                    case R.id.butNewspaperStation /* 2131296356 */:
                        if (!this.isCharteredBus && AppApplication.listBusLine != null && AppApplication.listBusLine.size() > 0) {
                            switchCurrentStation();
                            break;
                        }
                        break;
                    case R.id.butNumberF0 /* 2131296357 */:
                        serviceVoice(0);
                        sendServiceTone((byte) 16);
                        sendLCDQuiet();
                        break;
                    case R.id.butNumberF1 /* 2131296358 */:
                        serviceVoice(1);
                        sendServiceTone((byte) 17);
                        sendLCDQuiet();
                        break;
                    case R.id.butNumberF2 /* 2131296359 */:
                        serviceVoice(2);
                        sendServiceTone(UnaryPlusPtg.sid);
                        sendLCDQuiet();
                        break;
                    case R.id.butNumberF3 /* 2131296360 */:
                        serviceVoice(3);
                        sendServiceTone(UnaryMinusPtg.sid);
                        sendLCDQuiet();
                        break;
                    case R.id.butNumberF4 /* 2131296361 */:
                        serviceVoice(4);
                        sendServiceTone((byte) 20);
                        sendLCDQuiet();
                        break;
                    case R.id.butNumberF5 /* 2131296362 */:
                        serviceVoice(5);
                        sendServiceTone(ParenthesisPtg.sid);
                        sendLCDQuiet();
                        break;
                    case R.id.butNumberF6 /* 2131296363 */:
                        serviceVoice(6);
                        sendServiceTone(MissingArgPtg.sid);
                        break;
                    case R.id.butNumberF7 /* 2131296364 */:
                        serviceVoice(7);
                        sendServiceTone((byte) 23);
                        sendLCDQuiet();
                        break;
                    case R.id.butNumberF8 /* 2131296365 */:
                        serviceVoice(8);
                        sendServiceTone((byte) 24);
                        sendLCDQuiet();
                        break;
                    case R.id.butNumberF9 /* 2131296366 */:
                        serviceVoice(9);
                        sendServiceTone(AttrPtg.sid);
                        sendLCDQuiet();
                        break;
                }
                break;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        closeAllCamera();
        LogUtils.d("MainActivity", "onPause--------");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeDVR2Camera() {
        LogUtils.d("MainActivity", "关闭DVR摄像头--------");
        if (this.cameraDVR2 != null) {
            LogUtils.d("MainActivity", "DVR camera 不为空---，关闭DVR摄像头--------");
            this.cameraDVR2.setPreviewCallback(null);
            this.cameraDVR2.stopPreview();
            this.previewCameraDVR2 = false;
            this.cameraDVR2.release();
            this.paramCameraDVR2 = null;
            this.cameraDVR2 = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void InitCameraDVR2(SurfaceHolder surfaceHolder) {
        LogUtils.d("MainActivity", "初始化并打开DVR摄像头--------");
        try {
            if (this.isVIN1AHD) {
                this.cameraDVR2 = Camera.open(0);
                LogUtils.d("MainActivity", "初始化并打开DVR摄像头0--------");
            } else {
                this.cameraDVR2 = Camera.open(3);
                LogUtils.d("MainActivity", "初始化并打开DVR摄像头3--------");
            }
            Camera.Parameters parameters = this.cameraDVR2.getParameters();
            this.paramCameraDVR2 = parameters;
            parameters.setPreviewSize(1920, 1080);
            if (this.paramCameraDVR2.getSupportedFocusModes().contains("auto")) {
                this.paramCameraDVR2.setFocusMode("auto");
                this.cameraDVR2.setParameters(this.paramCameraDVR2);
            }
            this.cameraDVR2.setPreviewDisplay(surfaceHolder);
            this.cameraDVR2.startPreview();
            this.previewCameraDVR2 = true;
        } catch (IOException | RuntimeException e) {
            LogUtils.e("CameraError", "Fail to connect to camera service:" + e.getMessage());
            this.previewCameraDVR2 = false;
            Camera camera = this.cameraDVR2;
            if (camera != null) {
                camera.release();
                this.cameraDVR2 = null;
            }
            e.printStackTrace();
        }
    }

    private class surfaceViewDVR2Callback implements SurfaceHolder.Callback {
        private surfaceViewDVR2Callback() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            LogUtils.d("MainActivity", "------surfaceChangedDVR2------，operationStep=" + MainActivity.this.operationStep);
        }

        /* JADX WARN: Type inference failed for: r0v4, types: [com.lianhexinye.m90.ui.activity.MainActivity$surfaceViewDVR2Callback$1] */
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(final SurfaceHolder surfaceHolder) {
            LogUtils.d("MainActivity", "------surfaceCreatedDVR2------，operationStep=" + MainActivity.this.operationStep);
            new Thread() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.surfaceViewDVR2Callback.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    if (!MainActivity.this.previewCameraDVR2 && MainActivity.this.cameraDVR2 == null && MainActivity.this.operationStep != 0) {
                        MainActivity.this.InitCameraDVR2(surfaceHolder);
                    } else {
                        LogUtils.d("MainActivity", "previewCameraDVR2不为空，关闭DVR摄像头--------，operationStep=" + MainActivity.this.operationStep + "；previewCameraDVR2:" + MainActivity.this.previewCameraDVR2);
                        MainActivity.this.closeDVR2Camera();
                    }
                }
            }.start();
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            LogUtils.d("MainActivity", "------surfaceDestroyedDVR2------，operationStep=" + MainActivity.this.operationStep);
        }
    }

    private RelativeLayout addSurfaceViewDVR2() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        this.tvDVR2Camera = textView;
        textView.setText("DVR");
        this.tvDVR2Camera.setTextAppearance(this, R.style.monitorTextStyle);
        this.tvDVR2Camera.setTextColor(getResources().getColor(R.color.c_ffffff));
        this.tvDVR2Camera.setPadding(0, 0, 0, 0);
        SurfaceView surfaceView = new SurfaceView(this);
        this.surfaceViewDVR2Camera = surfaceView;
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        this.surfaceViewDVR2Camera.getHolder().addCallback(new surfaceViewDVR2Callback());
        SurfaceHolder holder = this.surfaceViewDVR2Camera.getHolder();
        this.surfaceViewDVR2Camera.getHolder();
        holder.setType(3);
        relativeLayout.addView(this.surfaceViewDVR2Camera);
        relativeLayout.addView(this.tvDVR2Camera);
        return relativeLayout;
    }

    private class MonitorThread extends Thread {
        private boolean suspend;

        private MonitorThread() {
            this.suspend = false;
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            while (!this.suspend) {
                int iGpioRead = GpioOperation.gpioRead("gpio1d1");
                int iGpioRead2 = GpioOperation.gpioRead("gpio1d0");
                for (int i = 0; i < 5; i++) {
                    try {
                        if (!this.suspend) {
                            Thread.sleep(10L);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                int iGpioRead3 = GpioOperation.gpioRead("gpio1d1");
                int iGpioRead4 = GpioOperation.gpioRead("gpio1d0");
                if (iGpioRead3 != iGpioRead || iGpioRead4 != iGpioRead2) {
                    MainActivity.this.isCamera = false;
                } else {
                    MainActivity.this.isCamera = true;
                }
                if (MainActivity.this.isCamera && MainActivity.this.isScanCamera) {
                    if (iGpioRead3 == 1 && iGpioRead4 == 0) {
                        if (MainActivity.this.operationStep != 1) {
                            MainActivity.this.operationStep = 1;
                            LogUtils.d("MainActivity", " operationStep = 1，关闭DVR摄像头--------");
                            MainActivity.this.closeDVR2Camera();
                            MainActivity.this.closeBackingupCamera();
                            MainActivity.this.closeMittertorCamera();
                            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.MonitorThread.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    MainActivity.this.updateMonitor(2);
                                }
                            });
                        }
                    } else if (iGpioRead3 == 0 && iGpioRead4 == 1) {
                        if (MainActivity.this.operationStep != 2) {
                            MainActivity.this.operationStep = 2;
                            LogUtils.d("MainActivity", " operationStep = 2，关闭DVR摄像头--------");
                            MainActivity.this.closeDVR2Camera();
                            MainActivity.this.closeBackingupCamera();
                            MainActivity.this.closeMittertorCamera();
                            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.MonitorThread.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    MainActivity.this.updateMonitor(1);
                                }
                            });
                        }
                    } else if (iGpioRead3 != 1 || iGpioRead4 != 1) {
                        if (iGpioRead3 == 0 && iGpioRead4 == 0 && MainActivity.this.operationStep != 4) {
                            MainActivity.this.operationStep = 4;
                            LogUtils.d("MainActivity", " operationStep = 4，关闭DVR摄像头--------");
                            MainActivity.this.closeDVR2Camera();
                            MainActivity.this.closeBackingupCamera();
                            MainActivity.this.closeMittertorCamera();
                            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.MonitorThread.4
                                @Override // java.lang.Runnable
                                public void run() {
                                    MainActivity.this.updateMonitor(4);
                                }
                            });
                        }
                    } else if (MainActivity.this.operationStep != 3) {
                        MainActivity.this.operationStep = 3;
                        LogUtils.d("MainActivity", " operationStep = 3，关闭DVR摄像头--------");
                        MainActivity.this.closeDVR2Camera();
                        MainActivity.this.closeBackingupCamera();
                        MainActivity.this.closeMittertorCamera();
                        MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.MonitorThread.3
                            @Override // java.lang.Runnable
                            public void run() {
                                MainActivity.this.updateMonitor(3);
                            }
                        });
                    }
                }
                try {
                    Thread.sleep(350L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMonitor(int i) {
        if (this.lyVideoImage != null) {
            if (i == 1) {
                LogUtils.d("MainActivity", "中门视频");
                this.lyVideoImage.setVisibility(0);
                this.rlLineDriver.setVisibility(8);
                this.rlBusInfo.setVisibility(8);
                this.lyVideoDVRImage.setVisibility(8);
                this.surfaceViewMittertor = null;
                this.surfaceViewBackingup = null;
                this.surfaceViewDVR2Camera = null;
                this.tvBackingup = null;
                this.tvMittertor = null;
                this.tvDVR2Camera = null;
                this.lyVideoImage.removeAllViews();
                this.lyVideoDVRImage.removeAllViews();
                this.lyVideoImage.addView(addSurfaceViewMittertor());
                return;
            }
            if (i == 2) {
                LogUtils.d("MainActivity", "倒车视频");
                this.lyVideoImage.setVisibility(0);
                this.rlLineDriver.setVisibility(8);
                this.rlBusInfo.setVisibility(8);
                this.lyVideoDVRImage.setVisibility(8);
                this.surfaceViewMittertor = null;
                this.surfaceViewBackingup = null;
                this.surfaceViewDVR2Camera = null;
                this.tvBackingup = null;
                this.tvMittertor = null;
                this.tvDVR2Camera = null;
                this.lyVideoImage.removeAllViews();
                this.lyVideoDVRImage.removeAllViews();
                this.lyVideoImage.addView(addSurfaceViewBackingup());
                return;
            }
            if (i == 3) {
                LogUtils.d("MainActivity", "DVR视频");
                this.lyVideoImage.setVisibility(8);
                this.rlLineDriver.setVisibility(0);
                this.rlBusInfo.setVisibility(0);
                this.lyVideoDVRImage.setVisibility(0);
                this.surfaceViewMittertor = null;
                this.surfaceViewBackingup = null;
                this.surfaceViewDVR2Camera = null;
                this.tvBackingup = null;
                this.tvMittertor = null;
                this.tvDVR2Camera = null;
                this.lyVideoImage.removeAllViews();
                this.lyVideoDVRImage.removeAllViews();
                this.lyVideoDVRImage.addView(addSurfaceViewDVR2());
                return;
            }
            if (i == 4) {
                LogUtils.d("MainActivity", "中门视频和倒车视频，打开倒车，倒车优先");
                this.lyVideoImage.setVisibility(0);
                this.rlLineDriver.setVisibility(8);
                this.rlBusInfo.setVisibility(8);
                this.lyVideoDVRImage.setVisibility(8);
                this.surfaceViewMittertor = null;
                this.surfaceViewBackingup = null;
                this.surfaceViewDVR2Camera = null;
                this.tvBackingup = null;
                this.tvMittertor = null;
                this.tvDVR2Camera = null;
                this.lyVideoImage.removeAllViews();
                this.lyVideoDVRImage.removeAllViews();
                this.lyVideoImage.addView(addSurfaceViewBackingup());
                return;
            }
            if (i != 5) {
                return;
            }
            LogUtils.d("MainActivity", "关闭全部视频");
            this.lyVideoImage.setVisibility(8);
            this.rlLineDriver.setVisibility(0);
            this.rlBusInfo.setVisibility(0);
            this.lyVideoDVRImage.setVisibility(0);
            this.surfaceViewMittertor = null;
            this.surfaceViewBackingup = null;
            this.surfaceViewDVR2Camera = null;
            this.tvBackingup = null;
            this.tvMittertor = null;
            this.tvDVR2Camera = null;
            this.lyVideoImage.removeAllViews();
            this.lyVideoDVRImage.removeAllViews();
        }
    }

    private void openCameraThread() {
        closeAllCamera();
        this.operationStep = 0;
        if (this.monitorThread != null) {
            LogUtils.d("MainActivity", "monitorThread不为空--------");
            this.monitorThread.setSuspend(true);
            try {
                this.monitorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.monitorThread = null;
        }
        MonitorThread monitorThread = new MonitorThread();
        this.monitorThread = monitorThread;
        monitorThread.start();
    }

    private void closeAllCamera() {
        LogUtils.d("MainActivity", "开机关闭所有摄像头开始--------");
        this.isScanCamera = false;
        closeDVR2Camera();
        closeBackingupCamera();
        closeMittertorCamera();
        LogUtils.d("MainActivity", "关闭所有摄像头结束--------");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeBackingupCamera() {
        LogUtils.d("MainActivity", "关闭Backingup摄像头--------");
        if (this.cameraBackingup != null) {
            LogUtils.d("MainActivity", "关闭Backingup摄像头 OK--------");
            this.cameraBackingup.setPreviewCallback(null);
            if (this.previewRunningBackingup) {
                this.cameraBackingup.stopPreview();
                this.previewRunningBackingup = false;
            }
            this.cameraBackingup.release();
            this.paramBackingup = null;
            this.cameraBackingup = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeMittertorCamera() {
        LogUtils.d("MainActivity", "关闭Mittertor摄像头--------");
        if (this.cameraMittertor != null) {
            LogUtils.d("MainActivity", "关闭Mittertor摄像头 ok--------");
            this.cameraMittertor.setPreviewCallback(null);
            if (this.previewRunningMittertor) {
                this.cameraMittertor.stopPreview();
                this.previewRunningMittertor = false;
            }
            this.cameraMittertor.release();
            this.paramMittertor = null;
            this.cameraMittertor = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void InitCameraBackingup(SurfaceHolder surfaceHolder) {
        LogUtils.d("MainActivity", "初始化并打开Backingup摄像头--------");
        try {
            Camera cameraOpen = Camera.open(1);
            this.cameraBackingup = cameraOpen;
            Camera.Parameters parameters = cameraOpen.getParameters();
            this.paramBackingup = parameters;
            parameters.setPreviewSize(1920, 1080);
            if (this.paramBackingup.getSupportedFocusModes().contains("auto")) {
                this.paramBackingup.setFocusMode("auto");
                this.cameraBackingup.setParameters(this.paramBackingup);
            }
            this.cameraBackingup.setPreviewDisplay(surfaceHolder);
            this.cameraBackingup.startPreview();
            this.previewRunningBackingup = true;
        } catch (IOException | RuntimeException e) {
            LogUtils.e("CameraError", "Fail to connect to camera service:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void InitCameraMittertor(SurfaceHolder surfaceHolder) {
        try {
            LogUtils.d("MainActivity", "初始化并打开Mittertor摄像头--------");
            Camera cameraOpen = Camera.open(2);
            this.cameraMittertor = cameraOpen;
            Camera.Parameters parameters = cameraOpen.getParameters();
            this.paramMittertor = parameters;
            parameters.setPreviewSize(MediaProperties.HEIGHT_720, 576);
            if (this.paramMittertor.getSupportedFocusModes().contains("auto")) {
                this.paramMittertor.setFocusMode("auto");
                this.cameraMittertor.setParameters(this.paramMittertor);
            }
            this.cameraMittertor.setPreviewDisplay(surfaceHolder);
            this.cameraMittertor.startPreview();
            this.previewRunningMittertor = true;
        } catch (IOException | RuntimeException e) {
            LogUtils.e("CameraError", "Fail to connect to camera service:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private RelativeLayout addSurfaceViewBackingup() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        this.tvBackingup = textView;
        textView.setText(R.string.main_backingup);
        this.tvBackingup.setTextAppearance(this, R.style.monitorTextStyle);
        this.tvBackingup.setTextColor(getResources().getColor(R.color.c_ffffff));
        this.tvBackingup.setPadding(0, 0, 0, 0);
        SurfaceView surfaceView = new SurfaceView(this);
        this.surfaceViewDVR2Camera = surfaceView;
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        this.surfaceViewDVR2Camera.getHolder().addCallback(new surfaceViewDVR2Callback());
        SurfaceHolder holder = this.surfaceViewDVR2Camera.getHolder();
        this.surfaceViewDVR2Camera.getHolder();
        holder.setType(3);
        relativeLayout.addView(this.surfaceViewDVR2Camera);
        relativeLayout.addView(this.tvBackingup);
        return relativeLayout;
    }

    private class surfaceViewBackingupCallback implements SurfaceHolder.Callback {
        private surfaceViewBackingupCallback() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            LogUtils.d("MainActivity", "------surfaceChangedBackingup------");
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [com.lianhexinye.m90.ui.activity.MainActivity$surfaceViewBackingupCallback$1] */
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(final SurfaceHolder surfaceHolder) {
            LogUtils.d("MainActivity", "------surfaceCreatedBackingup------");
            new Thread() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.surfaceViewBackingupCallback.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    if (MainActivity.this.cameraBackingup != null || MainActivity.this.operationStep == 0) {
                        return;
                    }
                    MainActivity.this.InitCameraBackingup(surfaceHolder);
                }
            }.start();
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            LogUtils.d("MainActivity", "------surfaceDestroyedBackingup------");
        }
    }

    private class surfaceViewMittertorCallback implements SurfaceHolder.Callback {
        private surfaceViewMittertorCallback() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            LogUtils.d("MainActivity", "------surfaceChangedMittertor------");
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [com.lianhexinye.m90.ui.activity.MainActivity$surfaceViewMittertorCallback$1] */
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(final SurfaceHolder surfaceHolder) {
            LogUtils.d("MainActivity", "------surfaceCreatedMittertor------");
            new Thread() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.surfaceViewMittertorCallback.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    if (MainActivity.this.cameraMittertor != null || MainActivity.this.operationStep == 0) {
                        return;
                    }
                    MainActivity.this.InitCameraMittertor(surfaceHolder);
                }
            }.start();
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            LogUtils.d("MainActivity", "------surfaceDestroyedMittertor------");
        }
    }

    private RelativeLayout addSurfaceViewMittertor() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        this.tvMittertor = textView;
        textView.setText(R.string.main_mittertor);
        this.tvMittertor.setTextAppearance(this, R.style.monitorTextStyle);
        this.tvMittertor.setTextColor(getResources().getColor(R.color.c_ffffff));
        this.tvMittertor.setPadding(0, 0, 0, 0);
        SurfaceView surfaceView = new SurfaceView(this);
        this.surfaceViewDVR2Camera = surfaceView;
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        this.surfaceViewDVR2Camera.getHolder().addCallback(new surfaceViewDVR2Callback());
        SurfaceHolder holder = this.surfaceViewDVR2Camera.getHolder();
        this.surfaceViewDVR2Camera.getHolder();
        holder.setType(3);
        relativeLayout.addView(this.surfaceViewDVR2Camera);
        relativeLayout.addView(this.tvMittertor);
        return relativeLayout;
    }

    private void stopNewspaper() {
        stopBusVoicePlay();
        stopTTS();
        sendLCDOpenSound();
    }

    private void switchDirection() {
        String strValueOf = String.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.BUSDIRECTIONNAME, ""));
        if ("".equals(strValueOf.trim())) {
            return;
        }
        this.switchType = 1;
        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHEND, false);
        this.isDispatchEnd = false;
        if (!strValueOf.endsWith("S")) {
            ((MainPresenter) this.mvpPresenter).getBusLineList(strValueOf.trim().substring(0, strValueOf.length() - 1) + "S");
        } else {
            ((MainPresenter) this.mvpPresenter).getBusLineList(strValueOf.trim().substring(0, strValueOf.length() - 1) + "X");
        }
    }

    private void repeatNewspaper() {
        BusLineModel busLineModel = AppApplication.listBusLine.get(this.currentStation);
        if (!this.isNextStation) {
            if (this.currentStation >= AppApplication.listBusLine.size() - 1) {
                if ("是".equals(this.ttsSwitch)) {
                    systemDisposeVoice(4, busLineModel);
                    return;
                } else {
                    disposeVoice(4, busLineModel);
                    return;
                }
            }
            if ("是".equals(this.ttsSwitch)) {
                systemDisposeVoice(1, busLineModel);
                return;
            } else {
                disposeVoice(1, busLineModel);
                return;
            }
        }
        int i = this.currentStation;
        if (i == 1) {
            if ("是".equals(this.ttsSwitch)) {
                systemDisposeVoice(0, busLineModel);
                return;
            } else {
                disposeVoice(0, busLineModel);
                return;
            }
        }
        if (i >= AppApplication.listBusLine.size() - 1) {
            if ("是".equals(this.ttsSwitch)) {
                systemDisposeVoice(3, busLineModel);
                return;
            } else {
                disposeVoice(3, busLineModel);
                return;
            }
        }
        if ("是".equals(this.ttsSwitch)) {
            systemDisposeVoice(2, busLineModel);
        } else {
            disposeVoice(2, busLineModel);
        }
    }

    private void switchCurrentStation() {
        if (this.currentStation < AppApplication.listBusLine.size() - 1 || (this.currentStation < AppApplication.listBusLine.size() && this.isNextStation)) {
            if (!this.isNextStation) {
                LogUtils.d("MainActivity", "出站：" + this.currentStation + "；isDispatchEnd" + this.isDispatchEnd);
                this.isNextStation = true;
                this.currentStation++;
                BusLineModel busLineModel = AppApplication.listBusLine.get(this.currentStation);
                if (!this.isDispatchEnd) {
                    int i = this.currentStation;
                    if (i == 1) {
                        if ("是".equals(this.ttsSwitch)) {
                            systemDisposeVoice(0, busLineModel);
                        } else {
                            disposeVoice(0, busLineModel);
                        }
                    } else if (i >= AppApplication.listBusLine.size() - 1) {
                        if ("是".equals(this.ttsSwitch)) {
                            systemDisposeVoice(3, busLineModel);
                        } else {
                            disposeVoice(3, busLineModel);
                        }
                    } else if ("是".equals(this.ttsSwitch)) {
                        systemDisposeVoice(2, busLineModel);
                    } else {
                        disposeVoice(2, busLineModel);
                    }
                }
                this.tvHomeNextStation.setText(R.string.main_next_station);
                this.tvHomeNextStationName.setText(AppApplication.listBusLine.get(this.currentStation).getBusName());
                this.tvEndBusName.setText(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
            } else {
                this.isNextStation = false;
                BusLineModel busLineModel2 = AppApplication.listBusLine.get(this.currentStation);
                LogUtils.d("MainActivity", "进站：" + this.currentStation + "；isDispatchEnd" + this.isDispatchEnd);
                if (!this.isDispatchEnd) {
                    this.isDispatchEndRelevance = true;
                    if (this.currentStation >= AppApplication.listBusLine.size() - 1) {
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHEND, true);
                        this.isDispatchEnd = true;
                        if ("是".equals(this.ttsSwitch)) {
                            systemDisposeVoice(4, busLineModel2);
                        } else {
                            disposeVoice(4, busLineModel2);
                        }
                    } else if (this.currentStation > 0) {
                        if ("是".equals(this.ttsSwitch)) {
                            systemDisposeVoice(1, busLineModel2);
                        } else {
                            disposeVoice(1, busLineModel2);
                        }
                    }
                }
                this.tvHomeNextStation.setText(R.string.main_home_station);
                this.tvHomeNextStationName.setText(AppApplication.listBusLine.get(this.currentStation).getBusName());
                this.tvEndBusName.setText(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
            }
            modifyLineNO(this.currentStation);
            this.reportType = 1;
            controlBusLine(this.currentStation, true);
            if (this.isNextStation || this.currentStation < AppApplication.listBusLine.size() - 1) {
                return;
            }
            switchDirection();
        }
    }

    private void disposeVoice(int i, BusLineModel busLineModel) {
        String strSubstring = busLineModel.getBusFilePath().substring(0, busLineModel.getBusFilePath().lastIndexOf("/Bus"));
        String str = strSubstring + "/CommonSounds/";
        String str2 = strSubstring + "/Bus/" + busLineModel.getBusLineName();
        String str3 = (String) SPUserInfoUtils.get(this, "IfSound", "否");
        stopBusVoicePlay();
        this.busVoiceList.clear();
        this.busExternalVoiceList.clear();
        addVoice(i, str, busLineModel, str2, "voiceE", str3);
        if (((String) SPUserInfoUtils.get(this, "IfEnglish", "否")).trim().equals("是")) {
            addVoice(i, str, busLineModel, str2, "voiceD", str3);
        }
        if (((String) SPUserInfoUtils.get(this, "IfDialect", "否")).trim().equals("是")) {
            addVoice(i, str, busLineModel, str2, "voiceF", str3);
        }
        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
        voicePlayOperationParam.setOperation(0);
        initMediaPlayer(voicePlayOperationParam);
    }

    private void addVoice(int i, String str, BusLineModel busLineModel, String str2, String str3, String str4) {
        if (i == 0) {
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/起点音1.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/起点音1.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Start1.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Start1.mp3");
            }
            if (new File(str2 + "/" + str3 + "/" + busLineModel.getBusLineName() + ".mp3").exists()) {
                this.busVoiceList.add(str2 + "/" + str3 + "/" + busLineModel.getBusLineName() + ".mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/起点音2.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/起点音2.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Start2.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Start2.mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/起点音3.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/起点音3.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Start3.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Start3.mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/起点音4.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/起点音4.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Start4.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Start4.mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/起点音5.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/起点音5.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Start5.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Start5.mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/起点音6.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/起点音6.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Start6.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Start6.mp3");
            }
            if (new File(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3").exists()) {
                this.busVoiceList.add(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3");
            }
            if (new File(str + "Extend/" + str3 + "/" + busLineModel.getDepartureExpansion() + ".mp3").exists()) {
                this.busVoiceList.add(str + "Extend/" + str3 + "/" + busLineModel.getDepartureExpansion() + ".mp3");
                return;
            }
            return;
        }
        if (i == 1) {
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/进站音1.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/进站音1.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Pitted1.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Pitted1.mp3");
            }
            if (new File(str + "Adv/" + str3 + "/" + busLineModel.getStationAdvert() + ".mp3").exists()) {
                this.busVoiceList.add(str + "Adv/" + str3 + "/" + busLineModel.getStationAdvert() + ".mp3");
            }
            if (new File(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3").exists()) {
                this.busVoiceList.add(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/进站音2.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/进站音2.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Pitted2.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Pitted2.mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/进站音3.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/进站音3.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Pitted3.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Pitted3.mp3");
            }
            if (new File(str + "Other/" + str3 + "/" + busLineModel.getStationPrompt() + ".mp3").exists()) {
                this.busVoiceList.add(str + "Other/" + str3 + "/" + busLineModel.getStationPrompt() + ".mp3");
            }
            if (str4.trim().equals("是")) {
                addExternalVoice(str, busLineModel, str2, str3);
                return;
            }
            return;
        }
        if (i == 2) {
            if (new File(str + "Adv/" + str3 + "/" + busLineModel.getDepartureAdvert() + ".mp3").exists()) {
                this.busVoiceList.add(str + "Adv/" + str3 + "/" + busLineModel.getDepartureAdvert() + ".mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/出站音1.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/出站音1.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Out1.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Out1.mp3");
            }
            if (new File(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3").exists()) {
                this.busVoiceList.add(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3");
            }
            if (new File(str + "Other/" + str3 + "/" + busLineModel.getDeparturePrompt() + ".mp3").exists()) {
                this.busVoiceList.add(str + "Other/" + str3 + "/" + busLineModel.getDeparturePrompt() + ".mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/出站音2.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/出站音2.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/Out2.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/Out2.mp3");
            }
            if (new File(str + "Extend/" + str3 + "/" + busLineModel.getDepartureExpansion() + ".mp3").exists()) {
                this.busVoiceList.add(str + "Extend/" + str3 + "/" + busLineModel.getDepartureExpansion() + ".mp3");
                return;
            }
            return;
        }
        if (i == 3) {
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/终点音1.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/终点音1.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/End1.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/End1.mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/终点音2.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/终点音2.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/End2.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/End2.mp3");
            }
            if (new File(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3").exists()) {
                this.busVoiceList.add(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/终点音3.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/终点音3.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/End3.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/End3.mp3");
            }
            if (new File(str + "Extend/" + str3 + "/" + busLineModel.getDepartureExpansion() + ".mp3").exists()) {
                this.busVoiceList.add(str + "Extend/" + str3 + "/" + busLineModel.getDepartureExpansion() + ".mp3");
                return;
            }
            return;
        }
        if (i == 4) {
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/终点音2.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/终点音2.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/End2.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/End2.mp3");
            }
            if (!str3.trim().equals("voiceE")) {
                if (new File(str + "Common/" + str3 + "/终点音4.mp3").exists()) {
                    this.busVoiceList.add(str + "Common/" + str3 + "/终点音4.mp3");
                }
            } else if (new File(str + "Common/" + str3 + "/End4.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/End4.mp3");
            }
            if (new File(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3").exists()) {
                this.busVoiceList.add(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3");
                return;
            }
            return;
        }
        if (i != 5) {
            return;
        }
        if (!str3.trim().equals("voiceE")) {
            if (new File(str + "Common/" + str3 + "/终点音2.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/终点音2.mp3");
            }
        } else if (new File(str + "Common/" + str3 + "/End2.mp3").exists()) {
            this.busVoiceList.add(str + "Common/" + str3 + "/End2.mp3");
        }
        if (new File(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3").exists()) {
            this.busVoiceList.add(busLineModel.getBusFilePath() + "/" + str3 + "/" + busLineModel.getBusSound() + ".mp3");
        }
        if (!str3.trim().equals("voiceE")) {
            if (new File(str + "Common/" + str3 + "/终点音4.mp3").exists()) {
                this.busVoiceList.add(str + "Common/" + str3 + "/终点音4.mp3");
            }
        } else if (new File(str + "Common/" + str3 + "/End4.mp3").exists()) {
            this.busVoiceList.add(str + "Common/" + str3 + "/End4.mp3");
        }
    }

    private void addExternalVoice(String str, BusLineModel busLineModel, String str2, String str3) {
        if (!str3.trim().equals("voiceE")) {
            if (new File(str + "Common/" + str3 + "/外音1.mp3").exists()) {
                this.busExternalVoiceList.add(str + "Common/" + str3 + "/外音1.mp3");
            }
        } else if (new File(str + "Common/" + str3 + "/External1.mp3").exists()) {
            this.busExternalVoiceList.add(str + "Common/" + str3 + "/External1.mp3");
        }
        if (new File(str2 + "/" + str3 + "/" + busLineModel.getBusLineName() + ".mp3").exists()) {
            this.busExternalVoiceList.add(str2 + "/" + str3 + "/" + busLineModel.getBusLineName() + ".mp3");
        }
        if (!str3.trim().equals("voiceE")) {
            if (new File(str + "Common/" + str3 + "/外音2.mp3").exists()) {
                this.busExternalVoiceList.add(str + "Common/" + str3 + "/外音2.mp3");
            }
        } else if (new File(str + "Common/" + str3 + "/External2.mp3").exists()) {
            this.busExternalVoiceList.add(str + "Common/" + str3 + "/External2.mp3");
        }
        if (!str3.trim().equals("voiceE")) {
            if (new File(str + "Common/" + str3 + "/外音3.mp3").exists()) {
                this.busExternalVoiceList.add(str + "Common/" + str3 + "/外音3.mp3");
            }
        } else if (new File(str + "Common/" + str3 + "/External3.mp3").exists()) {
            this.busExternalVoiceList.add(str + "Common/" + str3 + "/External3.mp3");
        }
        if (new File(busLineModel.getBusFilePath() + "/" + str3 + "/" + AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusSound() + ".mp3").exists()) {
            this.busExternalVoiceList.add(busLineModel.getBusFilePath() + "/" + str3 + "/" + AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusSound() + ".mp3");
        }
        if (!str3.trim().equals("voiceE")) {
            if (new File(str + "Common/" + str3 + "/外音4.mp3").exists()) {
                this.busExternalVoiceList.add(str + "Common/" + str3 + "/外音4.mp3");
            }
        } else if (new File(str + "Common/" + str3 + "/External4.mp3").exists()) {
            this.busExternalVoiceList.add(str + "Common/" + str3 + "/External4.mp3");
        }
    }

    private void systemDisposeVoice(int i, BusLineModel busLineModel) {
        this.busTTSContentList.clear();
        this.busVoiceContentList.clear();
        this.playControlFlow.clear();
        String strSubstring = busLineModel.getBusFilePath().substring(0, busLineModel.getBusFilePath().lastIndexOf("/Bus"));
        String str = strSubstring + "/CommonSounds/";
        String str2 = strSubstring + "/Bus/" + busLineModel.getBusLineName();
        stopTTS();
        stopBusVoicePlay();
        if (i == 0) {
            this.busTTSContentList.add("乘客朋友们，欢迎您乘坐");
            this.playControlFlow.add(0);
            if (new File(str2 + "/voiceD/" + busLineModel.getBusLineName() + ".mp3").exists()) {
                this.busVoiceContentList.add(str2 + "/voiceD/" + busLineModel.getBusLineName() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getBusLineName())) {
                this.busTTSContentList.add(busLineModel.getBusLineName());
                this.playControlFlow.add(0);
            }
            this.busTTSContentList.add("路公共汽车，本车由");
            this.playControlFlow.add(0);
            if (new File(busLineModel.getBusFilePath() + "/voiceD/" + AppApplication.listBusLine.get(0).getBusSound() + ".mp3").exists()) {
                this.busVoiceContentList.add(busLineModel.getBusFilePath() + "/voiceD/" + AppApplication.listBusLine.get(0).getBusSound() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(AppApplication.listBusLine.get(0).getBusName())) {
                this.busTTSContentList.add(AppApplication.listBusLine.get(0).getBusName());
                this.playControlFlow.add(0);
            }
            this.busTTSContentList.add("，开往");
            this.playControlFlow.add(0);
            if (new File(busLineModel.getBusFilePath() + "/voiceD/" + AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusSound() + ".mp3").exists()) {
                this.busVoiceContentList.add(busLineModel.getBusFilePath() + "/voiceD/" + AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusSound() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName())) {
                this.busTTSContentList.add(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
                this.playControlFlow.add(0);
            }
            this.busTTSContentList.add("，途径");
            this.playControlFlow.add(0);
            for (BusLineModel busLineModel2 : AppApplication.listBusLine) {
                if (busLineModel2.getIMajorStation() != null && !busLineModel2.getIMajorStation().trim().equals("") && (busLineModel2.getIMajorStation().trim().equals("大站") || busLineModel2.getIMajorStation().trim().equals("big"))) {
                    if (new File(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel2.getBusSound() + ".mp3").exists()) {
                        this.busVoiceContentList.add(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel2.getBusSound() + ".mp3");
                        this.playControlFlow.add(1);
                    } else if (!JavaUtils.isEmpty(busLineModel2.getBusName())) {
                        this.busTTSContentList.add(busLineModel2.getBusName());
                        this.playControlFlow.add(0);
                    }
                }
            }
            this.busTTSContentList.add("等站，请您按次序乘车，谢谢合作,下一站是");
            this.playControlFlow.add(0);
            if (new File(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusName() + ".mp3").exists()) {
                LogUtils.d("MainActivity", "下一站是:" + busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusName() + ".mp3");
                this.busVoiceContentList.add(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusName() + ".mp3");
                this.playControlFlow.add(1);
            } else {
                LogUtils.d("MainActivity", "下一站是:" + busLineModel.getBusName());
                if (!JavaUtils.isEmpty(busLineModel.getBusName())) {
                    this.busTTSContentList.add(busLineModel.getBusName());
                    this.playControlFlow.add(0);
                }
            }
            if (new File(str + "Extend/voiceD/" + busLineModel.getDepartureExpansion() + ".mp3").exists()) {
                this.busVoiceContentList.add(str + "Extend/voiceD/" + busLineModel.getDepartureExpansion() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getDepartureExpansion())) {
                this.busTTSContentList.add(busLineModel.getDepartureExpansion());
                this.playControlFlow.add(0);
            }
        } else if (i == 1) {
            this.busTTSContentList.add("乘客朋友们，");
            this.playControlFlow.add(0);
            if (new File(str + "Adv/voiceD/" + busLineModel.getStationAdvert() + ".mp3").exists()) {
                this.busVoiceContentList.add(str + "Adv/voiceD/" + busLineModel.getStationAdvert() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getStationAdvert())) {
                this.busTTSContentList.add(busLineModel.getStationAdvert());
                this.playControlFlow.add(0);
            }
            if (new File(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusName() + ".mp3").exists()) {
                this.busVoiceContentList.add(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusName() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getBusName())) {
                this.busTTSContentList.add(busLineModel.getBusName());
                this.playControlFlow.add(0);
            }
            this.busTTSContentList.add("到了,");
            this.playControlFlow.add(0);
            if (new File(str + "Other/voiceD/" + busLineModel.getStationPrompt() + ".mp3").exists()) {
                this.busVoiceContentList.add(str + "Other/voiceD/" + busLineModel.getStationPrompt() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getStationPrompt())) {
                this.busTTSContentList.add(busLineModel.getStationPrompt());
                this.playControlFlow.add(0);
            }
            this.busTTSContentList.add(",需要下车的乘客,请从后门下车");
            this.playControlFlow.add(0);
            if (new File(str + "Extend/voiceD/" + busLineModel.getStationExpansion() + ".mp3").exists()) {
                this.busVoiceContentList.add(str + "Extend/voiceD/" + busLineModel.getStationExpansion() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getStationExpansion())) {
                this.busTTSContentList.add(busLineModel.getStationExpansion());
                this.playControlFlow.add(0);
            }
        } else if (i == 2) {
            if (new File(str + "Adv/voiceD/" + busLineModel.getDepartureAdvert() + ".mp3").exists()) {
                this.busVoiceContentList.add(str + "Adv/voiceD/" + busLineModel.getDepartureAdvert() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getDepartureAdvert())) {
                this.busTTSContentList.add(busLineModel.getDepartureAdvert());
                this.playControlFlow.add(0);
            }
            this.busTTSContentList.add("刚上车的乘客，请您往里走，请坐好站稳，下一站是");
            this.playControlFlow.add(0);
            if (new File(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusSound() + ".mp3").exists()) {
                this.busVoiceContentList.add(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusSound() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getBusName())) {
                this.busTTSContentList.add(busLineModel.getBusName());
                this.playControlFlow.add(0);
            }
            if (new File(str + "Other/voiceD/" + busLineModel.getDeparturePrompt() + ".mp3").exists()) {
                this.busVoiceContentList.add(str + "Other/voiceD/" + busLineModel.getDeparturePrompt() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getDeparturePrompt())) {
                this.busTTSContentList.add(busLineModel.getDeparturePrompt());
                this.playControlFlow.add(0);
            }
            this.busTTSContentList.add("，需要下车的乘客，请做好下车准备");
            this.playControlFlow.add(0);
            if (new File(str + "Extend/voiceD/" + busLineModel.getDepartureExpansion() + ".mp3").exists()) {
                this.busVoiceContentList.add(str + "Extend/voiceD/" + busLineModel.getDepartureExpansion() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getDepartureExpansion())) {
                this.busTTSContentList.add(busLineModel.getDepartureExpansion());
                this.playControlFlow.add(0);
            }
        } else if (i == 3) {
            this.busTTSContentList.add("下一站是终点站，");
            this.playControlFlow.add(0);
            if (new File(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusSound() + ".mp3").exists()) {
                this.busVoiceContentList.add(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusSound() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getBusName())) {
                this.busTTSContentList.add(busLineModel.getBusName());
                this.playControlFlow.add(0);
            }
            this.busTTSContentList.add("，请您携带好自己行李物品，准备下车");
            this.playControlFlow.add(0);
        } else if (i == 4) {
            this.busTTSContentList.add("终点站");
            this.playControlFlow.add(0);
            if (new File(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusSound() + ".mp3").exists()) {
                this.busVoiceList.add(busLineModel.getBusFilePath() + "/voiceD/" + busLineModel.getBusSound() + ".mp3");
                this.playControlFlow.add(1);
            } else if (!JavaUtils.isEmpty(busLineModel.getBusName())) {
                this.busTTSContentList.add(busLineModel.getBusName());
                this.playControlFlow.add(0);
            }
            this.busTTSContentList.add("到了，请您携带好自己行李物品，准备下车");
            this.playControlFlow.add(0);
        }
        if (this.playControlFlow.size() > 0) {
            if (this.busTTSContentList.size() > 0) {
                Iterator<String> it = this.busTTSContentList.iterator();
                while (it.hasNext()) {
                    LogUtils.d("MainActivity", "tts:" + it.next());
                }
            }
            if (this.busVoiceContentList.size() > 0) {
                Iterator<String> it2 = this.busVoiceContentList.iterator();
                while (it2.hasNext()) {
                    LogUtils.d("MainActivity", "voice:" + it2.next());
                }
            }
            VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
            voicePlayOperationParam.setOperation(4);
            initMediaPlayer(voicePlayOperationParam);
        }
    }

    private void backwardNewspaper() {
        if (AppApplication.listBusLine == null || AppApplication.listBusLine.size() <= 0) {
            return;
        }
        if (this.isNextStation) {
            this.isNextStation = false;
            this.currentStation--;
            this.tvHomeNextStation.setText(R.string.main_home_station);
            this.tvHomeNextStationName.setText(AppApplication.listBusLine.get(this.currentStation).getBusName());
            this.tvEndBusName.setText(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
            if (this.currentStation == 0) {
                this.gpsOperationParam.setiInitSite(true);
            }
        } else {
            if (this.currentStation - 1 < 0) {
                return;
            }
            this.isNextStation = true;
            this.tvHomeNextStation.setText(R.string.main_next_station);
            this.tvHomeNextStationName.setText(AppApplication.listBusLine.get(this.currentStation).getBusName());
            this.tvEndBusName.setText(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
        }
        this.reportType = 1;
        modifyLineNO(this.currentStation);
        controlBusLine(this.currentStation, false);
    }

    private void forwardNewspaper() {
        if (AppApplication.listBusLine == null || AppApplication.listBusLine.size() <= 0) {
            return;
        }
        if (this.currentStation < AppApplication.listBusLine.size() - 1 || (this.currentStation < AppApplication.listBusLine.size() && this.isNextStation)) {
            if (!this.isNextStation) {
                this.isNextStation = true;
                this.currentStation++;
                this.tvHomeNextStation.setText(R.string.main_next_station);
                this.tvHomeNextStationName.setText(AppApplication.listBusLine.get(this.currentStation).getBusName());
                this.tvEndBusName.setText(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
            } else {
                this.isNextStation = false;
                this.tvHomeNextStation.setText(R.string.main_home_station);
                this.tvHomeNextStationName.setText(AppApplication.listBusLine.get(this.currentStation).getBusName());
                this.tvEndBusName.setText(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
            }
            this.reportType = 1;
            modifyLineNO(this.currentStation);
            controlBusLine(this.currentStation, false);
        }
    }

    private void refreshBus() {
        LogUtils.d("MainActivity", "refreshBus");
        this.switchType = 1;
        if (this.isFirstPage) {
            String strValueOf = String.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.BUSDIRECTIONNAME, ""));
            LogUtils.d("MainActivity", "directionName:" + strValueOf);
            if (!"".equals(strValueOf.trim())) {
                this.isSwitchLine = true;
                ((MainPresenter) this.mvpPresenter).getBusLineList(strValueOf.trim());
            }
            this.isFirstPage = false;
            this.isNextStation = false;
        } else if (((Boolean) SPUserInfoUtils.get(this, SPUserInfoUtils.ISSWITCHBUSLINE, false)).booleanValue()) {
            String strValueOf2 = String.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.BUSDIRECTIONNAME, ""));
            if (!"".equals(strValueOf2.trim())) {
                this.isSwitchLine = true;
                ((MainPresenter) this.mvpPresenter).getBusLineList(strValueOf2.trim());
            }
            SPUserInfoUtils.put(this, SPUserInfoUtils.ISSWITCHBUSLINE, false);
            this.isNextStation = false;
        }
        this.ttsSwitch = (String) SPUserInfoUtils.get(this, "IfTTSReportStation", "否");
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0069  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x00a5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void controlBusLine(int r12, boolean r13) {
        /*
            Method dump skipped, instruction units count: 227
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.ui.activity.MainActivity.controlBusLine(int, boolean):void");
    }

    private void showBusLine(int i, int i2, int i3, int i4, final int i5, boolean z, boolean z2) {
        BusLineModel busLineModel;
        if (i5 != AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusNo()) {
            busLineModel = null;
        } else if (this.isNextStation) {
            busLineModel = AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1);
            busLineModel.setStationType(1);
        } else {
            busLineModel = AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1);
            busLineModel.setStationType(0);
        }
        for (int i6 = 1; i6 < (i4 - i3) + 1; i6++) {
            int i7 = i4 - i6;
            if (AppApplication.listBusLine.get(i7).getBusNo() == i5) {
                if (this.isNextStation) {
                    busLineModel = AppApplication.listBusLine.get(i7);
                    busLineModel.setStationType(1);
                } else {
                    busLineModel = AppApplication.listBusLine.get(i7);
                    busLineModel.setStationType(0);
                }
            }
        }
        if (i5 == AppApplication.listBusLine.get(0).getBusNo()) {
            if (this.isNextStation) {
                busLineModel = AppApplication.listBusLine.get(1);
                busLineModel.setStationType(1);
            } else {
                busLineModel = AppApplication.listBusLine.get(0);
                busLineModel.setStationType(0);
            }
        }
        busLineModel.setStartBusName(AppApplication.listBusLine.get(0).getBusName());
        busLineModel.setEndBusName(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
        busLineModel.setCountStation(AppApplication.listBusLine.size());
        sendReportStationCommand(busLineModel);
        if (!this.isDispatchEnd || this.isDispatchEndRelevance) {
            this.isDispatchEndRelevance = false;
            reportDispatchStation(busLineModel);
        }
        Timer timer = this.authorTimer;
        if (timer != null) {
            timer.cancel();
        }
        Timer timer2 = new Timer();
        this.authorTimer = timer2;
        timer2.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.6
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.6.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (AppApplication.jhyopd == null || i5 < 0 || !"JHY".equals(MainActivity.this.protocol4852Type)) {
                            return;
                        }
                        AppApplication.jhyopd.write(JHYProtocol.createCurrentCount());
                    }
                });
            }
        }, 800L);
    }

    private void reportDispatchStation(BusLineModel busLineModel) {
        if (this.dispatchProtocol.trim().equals("DVR")) {
            assembleSiteInfoReport(1, busLineModel);
            if (this.currentStation == 1 && this.isNextStation && ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue() == 2) {
                assembleFirstStationReport(1, busLineModel);
                return;
            }
            return;
        }
        if (this.dispatchProtocol.trim().equals("ALINK")) {
            if (SocketManage.getInstance().iHeartbeat) {
                assembleSiteInfoReport(2, busLineModel);
                if (this.currentStation == 1 && this.isNextStation && ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue() == 2) {
                    assembleFirstStationReport(2, busLineModel);
                    return;
                }
                return;
            }
            return;
        }
        if ((this.dispatchProtocol.trim().equals("808") || this.dispatchProtocol.trim().equals("CC808")) && SocketManage.getInstance().iHeartbeat) {
            assembleSiteInfoReport(3, busLineModel);
            if (this.currentStation == 1 && this.isNextStation && ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue() == 2) {
                reverseDispatchTimer();
                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 1);
            }
        }
    }

    private void modifyLineNO(int i) {
        if (i == 0) {
            this.tvLineNO.setText(R.string.main_no_start);
        } else if (i == AppApplication.listBusLine.size() - 1) {
            this.tvLineNO.setText(R.string.main_no_end);
        } else {
            this.tvLineNO.setText(String.format(getResources().getString(R.string.main_no), "" + (AppApplication.listBusLine.get(i).getBusNo() + 1)));
        }
    }

    private void assembleSiteInfoReport(int i, BusLineModel busLineModel) {
        ReportInfoModel reportInfoModel = new ReportInfoModel();
        reportInfoModel.setBusLineName(busLineModel.getBusLineName());
        reportInfoModel.setDirection(busLineModel.getDirection());
        reportInfoModel.setBusNo(busLineModel.getBusNo());
        if (i == 3) {
            if (GPSMonitor.bValidData && GPSMonitor.wszLatitude != null && !GPSMonitor.wszLatitude.trim().equals("") && GPSMonitor.wszLongitude != null && !GPSMonitor.wszLongitude.trim().equals("")) {
                this.latitudeTmp = GPSMonitor.wszLatitude;
                LogUtils.d("MainActivity", "langitudeTmp:" + this.latitudeTmp);
                String[] strArrSplit = String.valueOf(Double.valueOf(this.latitudeTmp).doubleValue() / 100.0d).split("\\.");
                if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                    reportInfoModel.setLatitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue())));
                    LogUtils.d("MainActivity", "latValue:" + reportInfoModel.getLatitude());
                    this.longitudeTmp = GPSMonitor.wszLongitude;
                    LogUtils.d("MainActivity", "longitudeTmp:" + this.longitudeTmp);
                    String[] strArrSplit2 = String.valueOf(Double.valueOf(this.longitudeTmp).doubleValue() / 100.0d).split("\\.");
                    if (strArrSplit2.length > 1 && strArrSplit2[1].length() > 2) {
                        reportInfoModel.setLongitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue())));
                        LogUtils.d("MainActivity", "longValue:" + reportInfoModel.getLongitude());
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
            reportInfoModel.setLineNumber(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, "0").toString());
            reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
            if (this.isNextStation) {
                if (this.currentStation > 0) {
                    reportInfoModel.setSiteCode(AppApplication.listBusLine.get(this.currentStation - 1).getSiteCode());
                } else {
                    reportInfoModel.setSiteCode(AppApplication.listBusLine.get(0).getSiteCode());
                }
            } else {
                reportInfoModel.setSiteCode(busLineModel.getSiteCode());
            }
            reportInfoModel.setPlannedTrips(Integer.valueOf(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TIMESNO, "0").toString()).intValue());
            reportInfoModel.setVehicleStatus(this.vehicleStatusId);
            reportInfoModel.setReportType(this.reportType);
            if (!this.isNextStation) {
                reportInfoModel.setStatus(0);
                reportInfoModel.setBusNo(busLineModel.getBusNo() + 1);
                String strDateToString = JavaUtils.dateToString(new Date(), "yyMMddHHmmss");
                this.arrivalTime = strDateToString;
                reportInfoModel.setArrivalTime(strDateToString);
                reportInfoModel.setOutboundTime("000000000000");
            } else {
                if (!JavaUtils.isEmpty(this.arrivalTime)) {
                    reportInfoModel.setArrivalTime(this.arrivalTime);
                    reportInfoModel.setOutboundTime(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
                } else {
                    reportInfoModel.setArrivalTime(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
                    reportInfoModel.setOutboundTime(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
                }
                reportInfoModel.setStatus(1);
                reportInfoModel.setBusNo(busLineModel.getBusNo());
            }
            if (GPSMonitor.wCourse != null && GPSMonitor.wCourse.trim().length() > 0 && Float.valueOf(GPSMonitor.wCourse.trim()).intValue() > 0) {
                reportInfoModel.setAngle(Float.valueOf(GPSMonitor.wCourse.trim()).intValue());
            } else {
                reportInfoModel.setAngle(0);
            }
        } else if (!this.isNextStation) {
            reportInfoModel.setStatus(0);
        } else {
            reportInfoModel.setStatus(1);
        }
        reportInfoModel.setBusName(busLineModel.getBusName());
        if (busLineModel.getSpeedLimit() != null && !busLineModel.getSpeedLimit().trim().equals("")) {
            reportInfoModel.setSpeedLimit(Integer.valueOf(busLineModel.getSpeedLimit()).intValue());
        } else {
            reportInfoModel.setSpeedLimit(0);
        }
        if (GPSMonitor.wSpeed != null && GPSMonitor.wSpeed.trim().length() > 0 && Float.valueOf(GPSMonitor.wSpeed.trim()).intValue() >= 1) {
            if (i == 3) {
                this.speedStationTmp = String.valueOf(Float.valueOf(JavaUtils.toNumberFormat(Double.valueOf(GPSMonitor.wSpeed.trim()).doubleValue() * 1.852d, 1)).floatValue() * 10.0f);
            } else {
                this.speedStationTmp = String.valueOf(Double.valueOf(GPSMonitor.wSpeed.trim()).doubleValue() * 1.852d);
            }
            reportInfoModel.setSpeed(Float.valueOf(this.speedStationTmp).intValue());
        } else {
            reportInfoModel.setSpeed(0);
        }
        if (i == 1) {
            DVRProtocol.getInstance().sendSiteInfo(reportInfoModel);
        } else if (i == 2) {
            SocketManage.getInstance().sendReq(GenerateReqPackage.siteInfo(reportInfoModel));
        } else if (i == 3) {
            SocketManage.getInstance().sendReq(Generate808ReqPackage.generateReportStation(reportInfoModel));
        }
    }

    private void assembleCrossInfoReport(int i, BusLineFriendRemindModel busLineFriendRemindModel) {
        ReportInfoModel reportInfoModel = new ReportInfoModel();
        if (i == 3) {
            reportInfoModel.setTransmissionType("32");
            reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
            reportInfoModel.setLineNumber(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, "0").toString());
            if (this.isNextCross) {
                if (busLineFriendRemindModel.getFrNo() > 0) {
                    reportInfoModel.setCrossNumber(AppApplication.listBusLineFriendRemindModel.get(busLineFriendRemindModel.getFrNo()).getCrossCode());
                } else {
                    reportInfoModel.setCrossNumber(AppApplication.listBusLineFriendRemindModel.get(0).getCrossCode());
                }
            } else {
                reportInfoModel.setCrossNumber(busLineFriendRemindModel.getCrossCode());
            }
            if (!this.isNextCross) {
                String strDateToString = JavaUtils.dateToString(new Date(), "yyMMddHHmmss");
                this.arrivalCrossTime = strDateToString;
                reportInfoModel.setArrivalTime(strDateToString);
                reportInfoModel.setOutboundTime("000000000000");
            } else if (!JavaUtils.isEmpty(this.arrivalCrossTime)) {
                reportInfoModel.setArrivalTime(this.arrivalCrossTime);
                reportInfoModel.setOutboundTime(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
            } else {
                reportInfoModel.setArrivalTime(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
                reportInfoModel.setOutboundTime(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
            }
            if (GPSMonitor.wCourse != null && GPSMonitor.wCourse.trim().length() > 0 && Float.valueOf(GPSMonitor.wCourse.trim()).intValue() > 0) {
                reportInfoModel.setAngle(Float.valueOf(GPSMonitor.wCourse.trim()).intValue());
            } else {
                reportInfoModel.setAngle(0);
            }
            if (GPSMonitor.bValidData && GPSMonitor.wszLatitude != null && !GPSMonitor.wszLatitude.trim().equals("") && GPSMonitor.wszLongitude != null && !GPSMonitor.wszLongitude.trim().equals("")) {
                this.latitudeTmp = GPSMonitor.wszLatitude;
                LogUtils.d("MainActivity", "langitudeTmp:" + this.latitudeTmp);
                String[] strArrSplit = String.valueOf(Double.valueOf(this.latitudeTmp).doubleValue() / 100.0d).split("\\.");
                if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                    reportInfoModel.setLatitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue())));
                    LogUtils.d("MainActivity", "latValue:" + reportInfoModel.getLatitude());
                    this.longitudeTmp = GPSMonitor.wszLongitude;
                    LogUtils.d("MainActivity", "longitudeTmp:" + this.longitudeTmp);
                    String[] strArrSplit2 = String.valueOf(Double.valueOf(this.longitudeTmp).doubleValue() / 100.0d).split("\\.");
                    if (strArrSplit2.length > 1 && strArrSplit2[1].length() > 2) {
                        reportInfoModel.setLongitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue())));
                        LogUtils.d("MainActivity", "longValue:" + reportInfoModel.getLongitude());
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
            SocketManage.getInstance().sendReq(Generate808ReqPackage.generateCrossInfo(reportInfoModel));
        }
    }

    private void sendSwitchLine() {
        List<BusLineModel> listQueryArrayBusLineName;
        LogUtils.d("MainActivity", "sendSwitchLine");
        if (AppApplication.mSerial485Control == null || !AppApplication.mSerial485Control.isOpen()) {
            return;
        }
        LogUtils.d("MainActivity", "protocolType:" + this.protocolType);
        BusLineModel busLineModel = AppApplication.listBusLine.get(0);
        busLineModel.setStartBusName(AppApplication.listBusLine.get(0).getBusName());
        busLineModel.setEndBusName(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
        busLineModel.setCountStation(AppApplication.listBusLine.size());
        if ("通达".equals(this.protocolType) || "TD".equals(this.protocolType)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateTongdaProtocol().createLineName(busLineModel));
            AppApplication.listBusLine.get(0).setCountStation(busLineModel.getCountStation());
            ProtocolResult protocolResultCreateSiteInfo = this.serialPortManager.crateTongdaProtocol().createSiteInfo(AppApplication.listBusLine);
            AppApplication.send485Thread.send(protocolResultCreateSiteInfo.getmBufferBreak());
            while (protocolResultCreateSiteInfo.getBusLineModel().getBusNo() < AppApplication.listBusLine.size() - 1 && (listQueryArrayBusLineName = ((MainPresenter) this.mvpPresenter).queryArrayBusLineName(AppApplication.listBusLine.get(0).getDirectionName(), protocolResultCreateSiteInfo.getBusLineModel().getBusNo())) != null && listQueryArrayBusLineName.size() > 0) {
                listQueryArrayBusLineName.get(0).setCountStation(busLineModel.getCountStation());
                protocolResultCreateSiteInfo = this.serialPortManager.crateTongdaProtocol().createSiteInfo(listQueryArrayBusLineName);
                AppApplication.send485Thread.send(protocolResultCreateSiteInfo.getmBufferBreak());
                if (protocolResultCreateSiteInfo.getBusLineModel().getBusNo() >= AppApplication.listBusLine.size() - 1) {
                    return;
                }
            }
            return;
        }
        if (this.isSwitchLine && "LHXY".equals(this.protocolType)) {
            List<BusLineModel> listQueryArrayBusLine = ((MainPresenter) this.mvpPresenter).queryArrayBusLine(busLineModel.getBusLineName() + "S");
            if (listQueryArrayBusLine != null && listQueryArrayBusLine.size() > 0) {
                AppApplication.send485Thread.send(this.serialPortManager.crateLHXYProtocol().createSiteInfo(listQueryArrayBusLine).getmBufferBreak());
                List<BusLineModel> listQueryArrayBusLine2 = ((MainPresenter) this.mvpPresenter).queryArrayBusLine(busLineModel.getBusLineName() + "X");
                if (listQueryArrayBusLine2 != null && listQueryArrayBusLine2.size() > 0) {
                    AppApplication.send485Thread.send(this.serialPortManager.crateLHXYProtocol().createSiteInfo(listQueryArrayBusLine2).getmBufferBreak());
                }
                AppApplication.send485Thread.send(this.serialPortManager.crateLHXYProtocol().createLineName(busLineModel));
            }
            this.isSwitchLine = false;
            return;
        }
        if (this.isSwitchLine && "HW3".equals(this.protocolType)) {
            NiKeProtocol niKeProtocol = new NiKeProtocol();
            for (int i = 1; i <= 3; i++) {
                File file = new File(busLineModel.getBusFilePath() + "/KBGNINFO0" + i + ".LED");
                LogUtils.d("MainActivity", "hw3 File:" + file.getPath());
                if (file.exists()) {
                    for (ProtocolResult protocolResult : niKeProtocol.createLEDFileData(file)) {
                        LogUtils.d("MainActivity", "hw3 BufferBreak:" + JavaUtils.bytesToHexString(protocolResult.getmBufferBreak(), protocolResult.getmBufferBreak().length));
                        AppApplication.send485Thread.send(protocolResult.getmBufferBreak());
                    }
                }
            }
            this.isSwitchLine = false;
            return;
        }
        if ("HW3".equals(this.protocolType)) {
            AppApplication.send485Thread.send(new NiKeProtocol().createLineState(busLineModel));
        } else if ("恒舞".equals(this.protocolType)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateHengWuProtocol().createLineName(busLineModel));
        } else if ("武汉乐的".equals(this.protocolType)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateHengWuProtocol().createLineName(busLineModel));
        }
    }

    private void sendReportStationCommand(BusLineModel busLineModel) {
        if (AppApplication.mSerial485Control == null || !AppApplication.mSerial485Control.isOpen()) {
            return;
        }
        if ("通达".equals(this.protocolType) || "TD".equals(this.protocolType)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateTongdaProtocol().createNewspaperStation(busLineModel));
            AppApplication.send485Thread.send(this.serialPortManager.crateTongdaProtocol().createInternalScreen(busLineModel));
            if (GPSMonitor.bValidData) {
                AppApplication.mSerial485Control.send(this.serialPortManager.crateTongdaProtocol().createLineState(busLineModel));
            }
        } else if ("恒舞".equals(this.protocolType) && ((busLineModel.getBusNo() == 0 && busLineModel.getStationType() == 1) || busLineModel.getBusNo() > 0)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateHengWuProtocol().createNewspaperStation(busLineModel));
        } else if ("武汉乐的".equals(this.protocolType) && ((busLineModel.getBusNo() == 0 && busLineModel.getStationType() == 1) || busLineModel.getBusNo() > 0)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateHengWuProtocol().createNewspaperStation(busLineModel));
        } else if ("LED导程牌".equals(this.protocolType)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateLEDProtocol().createNewspaperStation(busLineModel));
        } else if (!this.isSwitchLine && "LHXY".equals(this.protocolType)) {
            if (busLineModel.getBusNo() == 0 && busLineModel.getStationType() == 0) {
                return;
            } else {
                AppApplication.send485Thread.send(this.serialPortManager.crateLHXYProtocol().createNewspaperStation(busLineModel));
            }
        }
        if (busLineModel.getStationType() == 0 && busLineModel.getBusNo() == 0) {
            LogUtils.d("MainActivity", "switch direction。。。");
        } else if (busLineModel != null) {
            sendLCDQuiet();
        }
    }

    private void serviceVoice(int i) {
        if (AppApplication.listBusLine == null || AppApplication.listBusLine.size() <= 0) {
            return;
        }
        stopBusVoicePlay();
        this.busVoiceList.clear();
        this.busExternalVoiceList.clear();
        addServiceVoice(i, "voiceD");
        if (((String) SPUserInfoUtils.get(this, "IfEnglish", "否")).trim().equals("是")) {
            addServiceVoice(i, "voiceE");
        }
        if (((String) SPUserInfoUtils.get(this, "IfDialect", "否")).trim().equals("是")) {
            addServiceVoice(i, "voiceF");
        }
        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
        voicePlayOperationParam.setOperation(1);
        if (i == 0 || i == 9) {
            voicePlayOperationParam.setiInner(false);
            initMediaPlayer(voicePlayOperationParam);
        } else {
            voicePlayOperationParam.setiInner(true);
            initMediaPlayer(voicePlayOperationParam);
        }
    }

    private void sendServiceTone(byte b) {
        if (AppApplication.mSerial485Control != null && AppApplication.mSerial485Control.isOpen() && "LHXY".equals(this.protocolType)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateLHXYProtocol().createServiceTone(b));
        }
    }

    private void sendLCDQuiet() {
        if (AppApplication.mSerial485Control == null || !AppApplication.mSerial485Control.isOpen()) {
            return;
        }
        if ("通达".equals(this.protocolType) || "TD".equals(this.protocolType)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateTongdaProtocol().createStopVol());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendLCDOpenSound() {
        if (AppApplication.mSerial485Control == null || !AppApplication.mSerial485Control.isOpen()) {
            return;
        }
        if ("通达".equals(this.protocolType) || "TD".equals(this.protocolType)) {
            AppApplication.send485Thread.send(this.serialPortManager.crateTongdaProtocol().createOpenVol());
        }
    }

    private void addServiceVoice(int i, String str) {
        if (!str.trim().equals("voiceE")) {
            if (new File(AppApplication.listBusLine.get(0).getBusFilePath().substring(0, AppApplication.listBusLine.get(0).getBusFilePath().lastIndexOf("/Bus")) + "/CommonSounds/Common/" + str + "/服务音" + i + ".mp3").exists()) {
                this.busVoiceList.add(AppApplication.listBusLine.get(0).getBusFilePath().substring(0, AppApplication.listBusLine.get(0).getBusFilePath().lastIndexOf("/Bus")) + "/CommonSounds/Common/" + str + "/服务音" + i + ".mp3");
            }
        } else if (new File(AppApplication.listBusLine.get(0).getBusFilePath().substring(0, AppApplication.listBusLine.get(0).getBusFilePath().lastIndexOf("/Bus")) + "/CommonSounds/Common/" + str + "/Service" + i + ".mp3").exists()) {
            this.busVoiceList.add(AppApplication.listBusLine.get(0).getBusFilePath().substring(0, AppApplication.listBusLine.get(0).getBusFilePath().lastIndexOf("/Bus")) + "/CommonSounds/Common/" + str + "/Service" + i + ".mp3");
        }
    }

    private void readConfiguration() {
        if (((Boolean) SPUserInfoUtils.get(AppApplication.getInstance(), SPUserInfoUtils.ISCONFIGVALID, true)).booleanValue()) {
            return;
        }
        List<ConfigInfoModel> listQueryArrayConfigInfo = ((MainPresenter) this.mvpPresenter).queryArrayConfigInfo();
        if (listQueryArrayConfigInfo != null && listQueryArrayConfigInfo.size() > 0) {
            for (ConfigInfoModel configInfoModel : listQueryArrayConfigInfo) {
                if (!configInfoModel.getConfigItem().trim().equals("LanguageSettings")) {
                    SPUserInfoUtils.put(AppApplication.getContext(), configInfoModel.getConfigItem(), configInfoModel.getConfigValue());
                }
            }
        }
        ((MainPresenter) this.mvpPresenter).queryArraySelectLineInfo();
        SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.ISCONFIGVALID, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initService() {
        this.msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.lianhexinye.m90.ui.activity.MsgReceiver");
        registerReceiver(this.msgReceiver, intentFilter);
        this.netWorkReceiver = new NetWorkReceiver();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter2.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.netWorkReceiver.setLanConnectionStatus(new NetWorkReceiver.LanConnectionStatus() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.8
            @Override // com.lianhexinye.m90.common.service.NetWorkReceiver.LanConnectionStatus
            public void setText(String str) {
                String string = MainActivity.this.getResources().getString(R.string.main_lan_value);
                if (MainActivity.this.tvLanState != null) {
                    MainActivity.this.tvLanState.setText(String.format(string, str));
                }
            }
        });
        registerReceiver(this.netWorkReceiver, intentFilter2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFTPDownloadService() {
        closeFTPDownloadService();
        Intent intent = new Intent(this, (Class<?>) FTPDownloadService.class);
        this.intentFTPDownLoad = intent;
        bindService(intent, this.connFTPDownLoad, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeFTPDownloadService() {
        if (this.intentFTPDownLoad != null) {
            unbindService(this.connFTPDownLoad);
            this.intentFTPDownLoad = null;
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            finish();
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getLineNameDataSuccess2(List<LineNameModel> list) {
        this.lineName = String.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.BUSLINENAME, ""));
        this.lineAttribute = ((Integer) SPUserInfoUtils.get(this, SPUserInfoUtils.LINEATTRIBUTE, 1)).intValue();
        this.lineDirectionName = String.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.BUSDIRECTIONNAME, ""));
        this.lineNumber = String.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.LINENUMBER, ""));
        if (list != null && list.size() > 0 && !"".equals(this.lineName.trim())) {
            for (LineNameModel lineNameModel : list) {
                if (this.lineName.trim().equals(lineNameModel.getBusName().trim())) {
                    lineNameModel.setChecked(true);
                    AppApplication.currentLineId = lineNameModel.getId();
                } else {
                    lineNameModel.setChecked(false);
                }
            }
        }
        AppApplication.lineNameModels2 = list;
        LogUtils.d("getLineNameDataSuccess2", "线路切换完成");
        this.butVideo.setEnabled(true);
        this.butVideo.setBackgroundResource(R.drawable.txt_key_bck);
    }

    private class InitThread extends Thread {
        private boolean suspend;

        private InitThread() {
            this.suspend = false;
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            ((MainPresenter) MainActivity.this.mvpPresenter).getLineList2();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.suspend) {
                return;
            }
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.InitThread.1
                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.this.initService();
                }
            });
            if (this.suspend) {
                return;
            }
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.InitThread.2
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.tvLanState != null) {
                        if (AndroidUtils.isNetworkAvailable() == 1) {
                            MainActivity.this.tvLanState.setText(String.format(MainActivity.this.getResources().getString(R.string.main_lan_value), MainActivity.this.getResources().getString(R.string.connected)));
                        } else {
                            MainActivity.this.tvLanState.setText(String.format(MainActivity.this.getResources().getString(R.string.main_lan_value), MainActivity.this.getResources().getString(R.string.unconnected)));
                        }
                    }
                }
            });
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            if (this.suspend) {
                return;
            }
            MainActivity.this.startFTPDownloadService();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
            if (this.suspend) {
                return;
            }
            if (((String) SPUserInfoUtils.get(AppApplication.getContext(), "RS232-1Protocol", "无")).trim().equals("DVR")) {
                DVRProtocol.getInstance().open232SerialPort();
            } else if (((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchProtocol", "无")).trim().equals("ALINK")) {
                MainActivity.this.dispatchProtocol = "ALINK";
                SocketManage.getInstance().connect();
                SocketManage.getInstance().registerCallback(MainActivity.this.socketActionCallback);
                MainActivity.this.openGpsReport();
            } else if (((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchProtocol", "无")).trim().equals("808")) {
                MainActivity.this.dispatchProtocol = "808";
                SocketManage.getInstance().connect();
                SocketManage.getInstance().registerCallback(MainActivity.this.socketActionCallback);
                MainActivity.this.openGpsReport();
            }
            if (((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchProtocol", "无")).trim().equals("CC808")) {
                MainActivity.this.dispatchProtocol = "CC808";
                SocketManage.getInstance().connect();
                SocketManage.getInstance().registerCallback(MainActivity.this.socketActionCallback);
                MainActivity.this.openGpsReport();
            }
            MainActivity.this.departRemindTwo();
        }
    }

    /* JADX INFO: renamed from: com.lianhexinye.m90.ui.activity.MainActivity$9, reason: invalid class name */
    class AnonymousClass9 implements SocketManage.SocketActionCallback {
        private String singInTip = "";

        AnonymousClass9() {
        }

        @Override // com.lianhexinye.m90.socket.SocketManage.SocketActionCallback
        public void onSocketConnectionSuccess() {
            MainActivity mainActivity;
            LogUtils.d("MainActivity", "CMS注册连接成功");
            if (MainActivity.this.getResources().getString(R.string.connected).equals(AppApplication.cmsState) || (mainActivity = MainActivity.this) == null) {
                return;
            }
            mainActivity.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.9.1
                @Override // java.lang.Runnable
                public void run() {
                    AppApplication.cmsState = MainActivity.this.getResources().getString(R.string.connected);
                    if (MainActivity.this.tvCMS != null) {
                        MainActivity.this.tvCMS.setText(String.format(MainActivity.this.cmsValue, AppApplication.cmsState));
                    }
                    MainActivity.this.assembleTerminalSelfCheck(3);
                }
            });
        }

        @Override // com.lianhexinye.m90.socket.SocketManage.SocketActionCallback
        public void onSocketDisconnection(String str) {
            LogUtils.d("MainActivity", "AlinkonSocketDisconnection" + str);
        }

        @Override // com.lianhexinye.m90.socket.SocketManage.SocketActionCallback
        public void onSocketConnectionFailed(String str) {
            LogUtils.d("MainActivity", "AlinkonSocketConnectionFailed" + str);
        }

        @Override // com.lianhexinye.m90.socket.SocketManage.SocketActionCallback
        public void onSocketClose(String str) {
            LogUtils.d("MainActivity", "AlinkononSocketClose" + str);
        }

        @Override // com.lianhexinye.m90.socket.SocketManage.SocketActionCallback
        public void onSocketTimeoutException(String str) {
            LogUtils.d("MainActivity", "AlinkononSocketClose" + str);
        }

        @Override // com.lianhexinye.m90.socket.SocketManage.SocketActionCallback
        public void onSocketReadResponse(int i, String str) {
            if (i != 1) {
                if (i == 2) {
                    try {
                        String strSubstring = str.trim().substring(16, 24);
                        final String strTrim = JavaUtils.stringToGBK(str.trim().substring(32, MediaProperties.HEIGHT_288)).trim();
                        MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.9.3
                            @Override // java.lang.Runnable
                            public void run() {
                                VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                voicePlayOperationParam.setOperation(3);
                                voicePlayOperationParam.setContent(strTrim);
                                MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                                if (MainActivity.this.dialogSingleView != null) {
                                    MainActivity.this.dialogSingleView.dismiss();
                                    MainActivity.this.dialogSingleView = null;
                                }
                                MainActivity.this.reverseCloseTip();
                                MainActivity.this.dialogSingleView = new DialogSingleView.Builder(MainActivity.this).setContent(strTrim).setOnOKClickListener(new DialogSingleView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.9.3.1
                                    @Override // com.lianhexinye.m90.common.widget.DialogSingleView.OnOKCancelClickListener
                                    public void onOKClick() {
                                        MainActivity.this.dialogSingleView.dismiss();
                                    }
                                }).build();
                                MainActivity.this.dialogSingleView.show();
                            }
                        });
                        ReportInfoModel reportInfoModel = new ReportInfoModel();
                        reportInfoModel.setResult(1);
                        reportInfoModel.setSerialNumber(strSubstring);
                        MainActivity.this.assembleNoticeReply(2, reportInfoModel);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                if (i != 5) {
                    return;
                }
                if (JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.trim().substring(32, 40)), 0) == 1) {
                    try {
                        long jByteToInt_HL = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.trim().substring(40, 48)), 0);
                        final String strTrim2 = JavaUtils.stringToGBK(str.trim().substring(48, 112)).trim();
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DRIVERID, String.valueOf(jByteToInt_HL));
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DRIVERNAME, strTrim2);
                        MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.9.4
                            @Override // java.lang.Runnable
                            public void run() {
                                MainActivity.this.tvDriver.setText(String.format(MainActivity.this.getResources().getString(R.string.main_driver), strTrim2));
                                VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                voicePlayOperationParam.setOperation(3);
                                voicePlayOperationParam.setContent(MainActivity.this.getResources().getString(R.string.paycard_suss));
                                MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                            }
                        });
                        return;
                    } catch (UnsupportedEncodingException e2) {
                        e2.printStackTrace();
                        return;
                    }
                }
                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.9.5
                    @Override // java.lang.Runnable
                    public void run() {
                        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                        voicePlayOperationParam.setOperation(3);
                        voicePlayOperationParam.setContent(MainActivity.this.getResources().getString(R.string.paycard_fail));
                        MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                    }
                });
                return;
            }
            try {
                String strSubstring2 = str.trim().substring(16, 24);
                JavaUtils.stringToGBK(str.trim().substring(32, 72)).trim();
                int iByteToInt_HL = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.trim().substring(72, 80)), 0);
                int iHexToInt = JavaUtils.HexToInt(str.trim().substring(80, 82));
                int iHexToInt2 = JavaUtils.HexToInt(str.trim().substring(82, 84));
                final String strValueOf = String.valueOf(JavaUtils.HexToInt(str.trim().substring(84, 86)));
                JavaUtils.stringToGBK(str.trim().substring(86, 152)).trim();
                final String strTrim3 = JavaUtils.stringToGBK(str.trim().substring(152, 160)).trim();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(11, Integer.valueOf(strTrim3.substring(0, 2)).intValue());
                calendar.set(12, Integer.valueOf(strTrim3.substring(2, 4)).intValue());
                calendar.set(13, 0);
                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DEPARTURETIME, JavaUtils.dateToString(calendar.getTime(), new String[0]));
                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.UPLINK, Integer.valueOf(iHexToInt));
                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.LINEGUID, Integer.valueOf(iByteToInt_HL));
                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.SCHEDULENO, Integer.valueOf(iHexToInt2));
                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.TIMESNO, strValueOf);
                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 2);
                if (strTrim3 != null && strTrim3.trim().length() >= 4) {
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.9.2
                        @Override // java.lang.Runnable
                        public void run() {
                            VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                            voicePlayOperationParam.setOperation(3);
                            voicePlayOperationParam.setContent("收到新的调度信息，请按计划时间发车");
                            MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                            MainActivity.this.tvPlannedTime1.setText(Html.fromHtml(MainActivity.this.getString(R.string.main_planned_time, "" + strValueOf, strTrim3.substring(0, 2) + ":" + strTrim3.substring(2, 4), "-- : --")));
                        }
                    });
                }
                ReportInfoModel reportInfoModel2 = new ReportInfoModel();
                reportInfoModel2.setLineGuid(iByteToInt_HL);
                reportInfoModel2.setDirection(iHexToInt);
                reportInfoModel2.setScheduleNo(iHexToInt2);
                reportInfoModel2.setTimesNo(Integer.valueOf(strValueOf).intValue());
                reportInfoModel2.setResult(1);
                reportInfoModel2.setSerialNumber(strSubstring2);
                MainActivity.this.assembleDispatchReply(2, reportInfoModel2);
                MainActivity.this.departRemind();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:100:0x06f1 A[Catch: Exception -> 0x0739, TryCatch #5 {Exception -> 0x0739, blocks: (B:30:0x011f, B:32:0x0201, B:34:0x0233, B:36:0x023b, B:38:0x0252, B:39:0x0264, B:41:0x0283, B:48:0x02d1, B:50:0x02e7, B:52:0x02ef, B:54:0x034b, B:56:0x03a1, B:58:0x03a9, B:60:0x0407, B:62:0x0434, B:64:0x043c, B:65:0x0446, B:67:0x04fa, B:88:0x0677, B:78:0x053b, B:79:0x0560, B:80:0x0585, B:81:0x05b3, B:83:0x05e6, B:84:0x05f2, B:86:0x0638, B:89:0x067f, B:92:0x06bc, B:94:0x06c4, B:98:0x06cf, B:100:0x06f1, B:101:0x0715, B:43:0x02a8), top: B:385:0x011f, outer: #3 }] */
        /* JADX WARN: Removed duplicated region for block: B:101:0x0715 A[Catch: Exception -> 0x0739, TRY_LEAVE, TryCatch #5 {Exception -> 0x0739, blocks: (B:30:0x011f, B:32:0x0201, B:34:0x0233, B:36:0x023b, B:38:0x0252, B:39:0x0264, B:41:0x0283, B:48:0x02d1, B:50:0x02e7, B:52:0x02ef, B:54:0x034b, B:56:0x03a1, B:58:0x03a9, B:60:0x0407, B:62:0x0434, B:64:0x043c, B:65:0x0446, B:67:0x04fa, B:88:0x0677, B:78:0x053b, B:79:0x0560, B:80:0x0585, B:81:0x05b3, B:83:0x05e6, B:84:0x05f2, B:86:0x0638, B:89:0x067f, B:92:0x06bc, B:94:0x06c4, B:98:0x06cf, B:100:0x06f1, B:101:0x0715, B:43:0x02a8), top: B:385:0x011f, outer: #3 }] */
        /* JADX WARN: Removed duplicated region for block: B:317:0x15ba A[Catch: Exception -> 0x17ab, TRY_LEAVE, TryCatch #2 {Exception -> 0x17ab, blocks: (B:149:0x09fb, B:151:0x0a35, B:160:0x0b01, B:239:0x0f2d, B:241:0x0f33, B:243:0x0f40, B:244:0x0f43, B:246:0x0f49, B:247:0x0f83, B:249:0x0f91, B:251:0x0f97, B:253:0x0fa7, B:255:0x0fb2, B:256:0x0fb5, B:273:0x10e7, B:257:0x0fbb, B:259:0x1046, B:261:0x1075, B:265:0x10c8, B:262:0x108f, B:264:0x109c, B:267:0x10ce, B:269:0x10db, B:270:0x10de, B:155:0x0a7f, B:157:0x0abd, B:274:0x1120, B:275:0x115f, B:276:0x11d2, B:277:0x12a9, B:279:0x12f9, B:285:0x1322, B:286:0x13ce, B:287:0x1409, B:288:0x141d, B:299:0x148e, B:301:0x14d7, B:303:0x14e7, B:304:0x1505, B:306:0x150b, B:308:0x151b, B:309:0x1539, B:311:0x153f, B:313:0x154f, B:314:0x156d, B:315:0x157e, B:316:0x15b1, B:317:0x15ba, B:339:0x16d5, B:340:0x16d9, B:347:0x16f0, B:349:0x1725, B:351:0x1750, B:353:0x1764, B:354:0x178a, B:356:0x178e, B:358:0x1796, B:359:0x179f, B:350:0x173b, B:320:0x15ca, B:322:0x1613, B:325:0x161c, B:327:0x1624, B:329:0x1636, B:330:0x163f, B:335:0x16a5, B:331:0x165a, B:333:0x1682, B:334:0x168b), top: B:382:0x001f, inners: #7 }] */
        /* JADX WARN: Removed duplicated region for block: B:53:0x0349  */
        /* JADX WARN: Removed duplicated region for block: B:59:0x0405  */
        /* JADX WARN: Removed duplicated region for block: B:67:0x04fa A[Catch: Exception -> 0x0739, TryCatch #5 {Exception -> 0x0739, blocks: (B:30:0x011f, B:32:0x0201, B:34:0x0233, B:36:0x023b, B:38:0x0252, B:39:0x0264, B:41:0x0283, B:48:0x02d1, B:50:0x02e7, B:52:0x02ef, B:54:0x034b, B:56:0x03a1, B:58:0x03a9, B:60:0x0407, B:62:0x0434, B:64:0x043c, B:65:0x0446, B:67:0x04fa, B:88:0x0677, B:78:0x053b, B:79:0x0560, B:80:0x0585, B:81:0x05b3, B:83:0x05e6, B:84:0x05f2, B:86:0x0638, B:89:0x067f, B:92:0x06bc, B:94:0x06c4, B:98:0x06cf, B:100:0x06f1, B:101:0x0715, B:43:0x02a8), top: B:385:0x011f, outer: #3 }] */
        /* JADX WARN: Removed duplicated region for block: B:96:0x06cc  */
        /* JADX WARN: Removed duplicated region for block: B:97:0x06ce  */
        /* JADX WARN: Type inference failed for: r11v0, types: [java.lang.String] */
        /* JADX WARN: Type inference failed for: r11v107 */
        /* JADX WARN: Type inference failed for: r11v108 */
        /* JADX WARN: Type inference failed for: r11v109 */
        /* JADX WARN: Type inference failed for: r11v110 */
        /* JADX WARN: Type inference failed for: r11v111 */
        /* JADX WARN: Type inference failed for: r11v112 */
        /* JADX WARN: Type inference failed for: r11v113 */
        /* JADX WARN: Type inference failed for: r11v114 */
        /* JADX WARN: Type inference failed for: r11v115 */
        /* JADX WARN: Type inference failed for: r11v116 */
        /* JADX WARN: Type inference failed for: r11v117 */
        /* JADX WARN: Type inference failed for: r11v118 */
        /* JADX WARN: Type inference failed for: r11v119 */
        /* JADX WARN: Type inference failed for: r11v12, types: [com.lianhexinye.m90.ui.activity.MainActivity$9] */
        /* JADX WARN: Type inference failed for: r11v120 */
        /* JADX WARN: Type inference failed for: r11v121 */
        /* JADX WARN: Type inference failed for: r11v122 */
        /* JADX WARN: Type inference failed for: r11v18 */
        /* JADX WARN: Type inference failed for: r11v6 */
        /* JADX WARN: Type inference failed for: r11v60 */
        /* JADX WARN: Type inference failed for: r11v77 */
        /* JADX WARN: Type inference failed for: r1v156 */
        /* JADX WARN: Type inference failed for: r1v22 */
        @Override // com.lianhexinye.m90.socket.SocketManage.SocketActionCallback
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onSocketRead808Response(int r28, final java.lang.String r29) {
            /*
                Method dump skipped, instruction units count: 6522
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.ui.activity.MainActivity.AnonymousClass9.onSocketRead808Response(int, java.lang.String):void");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFTPTCPDownloadService() {
        closeFTPTCPDownloadService();
        Intent intent = new Intent(this, (Class<?>) FTPSendFilesService.class);
        this.intentFTPSendFiles = intent;
        bindService(intent, this.connFTPSendFiles, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeFTPTCPDownloadService() {
        if (this.intentFTPSendFiles != null) {
            unbindService(this.connFTPSendFiles);
            this.intentFTPSendFiles = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void departRemindTwo() {
        if (((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue() == 2) {
            String str = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DEPARTURETIME, "");
            if (JavaUtils.isEmpty(str)) {
                return;
            }
            try {
                String strCompareDifferTimeSize = JavaUtils.compareDifferTimeSize(str, JavaUtils.dateToString(new Date(), new String[0]));
                String[] strArrSplit = strCompareDifferTimeSize.trim().split(",");
                LogUtils.d("MainActivity", "dataSize:" + strCompareDifferTimeSize);
                if (strArrSplit[0].equals("0")) {
                    if (Integer.valueOf(strArrSplit[1]).intValue() >= 0 && Integer.valueOf(strArrSplit[2]).intValue() >= 0 && Integer.valueOf(strArrSplit[3]).intValue() >= 0) {
                        int iIntValue = (Integer.valueOf(strArrSplit[1]).intValue() * 60 * 60) + (Integer.valueOf(strArrSplit[2]).intValue() * 60) + Integer.valueOf(strArrSplit[3]).intValue();
                        String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TS, "").toString();
                        if (Math.abs(iIntValue) - (Integer.parseInt(string) * 60) > 0) {
                            startDispatchTimerTwo(Integer.parseInt(string) * 60 * 1000, iIntValue * 1000);
                        } else {
                            int i = (Integer.parseInt(string) * 60) - Math.abs(iIntValue);
                            if (i > 1) {
                                startDispatchTimerTwo(i * 1000, iIntValue * 1000);
                            } else {
                                startDispatchTimerTwo(0, iIntValue * 1000);
                            }
                        }
                    } else {
                        String string2 = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.OT, "").toString();
                        if (string2.length() > 0) {
                            int i2 = (Integer.parseInt(string2) * 60) - Math.abs((((Integer.valueOf(strArrSplit[1]).intValue() * 60) * 60) + (Integer.valueOf(strArrSplit[2]).intValue() * 60)) + Integer.valueOf(strArrSplit[3]).intValue());
                            if (i2 > 0) {
                                startDispatchTimerTwo(i2);
                            } else {
                                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 1);
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void startDispatchTimerTwo(int i, int i2) {
        reverseDispatchTimer();
        long j = Long.parseLong(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TS, "").toString()) * 60 * 1000;
        Timer timer = new Timer();
        this.timer = timer;
        timer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.11
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (MainActivity.this == null || 2 != ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue()) {
                    return;
                }
                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.11.1
                    @Override // java.lang.Runnable
                    public void run() {
                        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                        voicePlayOperationParam.setOperation(3);
                        voicePlayOperationParam.setContent(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TTSNOTICECONTENT, "调度已下发请做好发车准备").toString());
                        MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                    }
                });
            }
        }, i, j);
        Timer timer2 = new Timer();
        this.timer1 = timer2;
        long j2 = i2;
        timer2.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.12
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                MainActivity mainActivity = MainActivity.this;
                if (mainActivity != null && mainActivity.timer != null) {
                    MainActivity.this.timer.cancel();
                    MainActivity.this.timer = null;
                }
                if (MainActivity.this == null || 2 != ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue()) {
                    return;
                }
                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.12.1
                    @Override // java.lang.Runnable
                    public void run() {
                        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                        voicePlayOperationParam.setOperation(3);
                        voicePlayOperationParam.setContent(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TTSNOTICECONTENTTWO, "发车时间已到请发车").toString());
                        MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                    }
                });
            }
        }, j2);
        String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.OTS, "").toString();
        if (string.length() > 0) {
            long j3 = Long.parseLong(string) * 60 * 1000;
            Timer timer3 = new Timer();
            this.timer2 = timer3;
            timer3.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.13
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (MainActivity.this == null || 2 != ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue()) {
                        return;
                    }
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.13.1
                        @Override // java.lang.Runnable
                        public void run() {
                            VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                            voicePlayOperationParam.setOperation(3);
                            voicePlayOperationParam.setContent(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TTSNOTICECONTENTTWO, "发车时间已到请发车").toString());
                            MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                        }
                    });
                }
            }, j2 + j3, j3);
        }
        String string2 = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.OT, "").toString();
        if (string2.length() > 0) {
            long j4 = Long.parseLong(string2) * 60 * 1000;
            Timer timer4 = new Timer();
            this.timer3 = timer4;
            timer4.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.14
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (MainActivity.this != null) {
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 1);
                        if (MainActivity.this.timer2 != null) {
                            MainActivity.this.timer2.cancel();
                            MainActivity.this.timer2 = null;
                        }
                    }
                }
            }, j4 + j2);
        }
    }

    private void startDispatchTimerTwo(long j) {
        reverseDispatchTimer();
        String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.OTS, "").toString();
        if (string.length() > 0) {
            long j2 = Long.parseLong(string) * 60 * 1000;
            Timer timer = new Timer();
            this.timer2 = timer;
            long j3 = j * 1000;
            if (j2 > j3) {
                timer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.15
                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        if (MainActivity.this == null || 2 != ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue()) {
                            MainActivity.this.timer2.cancel();
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.15.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                    voicePlayOperationParam.setOperation(3);
                                    voicePlayOperationParam.setContent(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TTSNOTICECONTENTTWO, "发车时间已到请发车").toString());
                                    MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                                }
                            });
                        }
                    }
                }, 0L, j2);
            } else {
                timer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.16
                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        if (MainActivity.this == null || 2 != ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue()) {
                            MainActivity.this.timer2.cancel();
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.16.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                    voicePlayOperationParam.setOperation(3);
                                    voicePlayOperationParam.setContent(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TTSNOTICECONTENTTWO, "发车时间已到请发车").toString());
                                    MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                                }
                            });
                        }
                    }
                }, j2, j2);
            }
            Timer timer2 = new Timer();
            this.timer3 = timer2;
            timer2.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.17
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (MainActivity.this != null) {
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 1);
                        if (MainActivity.this.timer2 != null) {
                            MainActivity.this.timer2.cancel();
                            MainActivity.this.timer2 = null;
                        }
                    }
                }
            }, j3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeIntercomBagThread() {
        IntercomBagThread intercomBagThread = this.intercomBagThread;
        if (intercomBagThread != null) {
            intercomBagThread.setSuspend(true);
            synchronized (this.synchronizationByte) {
                this.synchronizationByte.notifyAll();
            }
            try {
                try {
                    this.intercomBagThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                this.intercomBagThread = null;
            }
        }
    }

    private class IntercomBagThread extends Thread {
        private int contentCursor;
        private final byte[] intercomBodyBytes;
        private final LinkedList<byte[]> paramArrayByte;
        private boolean suspend;

        private IntercomBagThread() {
            this.suspend = false;
            this.contentCursor = 0;
            this.intercomBodyBytes = new byte[320];
            this.paramArrayByte = new LinkedList<>();
        }

        public synchronized void send(byte[] bArr) {
            LogUtils.d("MainActivity", "IntercomBagThread messageByte:" + bArr.length);
            this.paramArrayByte.add(bArr);
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            ReportInfoModel reportInfoModel = new ReportInfoModel();
            reportInfoModel.setChannelNumber(MainActivity.this.channelNumber);
            reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
            while (!this.suspend) {
                synchronized (MainActivity.this.synchronizationByte) {
                    while (this.paramArrayByte.size() > 0) {
                        for (byte b : this.paramArrayByte.removeFirst()) {
                            int i = this.contentCursor;
                            if (i < 320) {
                                this.intercomBodyBytes[i] = b;
                                this.contentCursor = i + 1;
                            }
                            if (this.contentCursor >= 320) {
                                this.contentCursor = 0;
                                MainActivity.this.sendData = new G711a.Result();
                                G711a.G711aEncode(this.intercomBodyBytes, MainActivity.this.sendData);
                                reportInfoModel.setContentByte(MainActivity.this.sendData.getDataArr());
                            }
                        }
                    }
                    MainActivity.this.atomicBoolean.set(true);
                    try {
                        MainActivity.this.synchronizationByte.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class IntercomRecordThread extends Thread {
        IntercomRecordThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                LogUtils.d("MainActivity", "IntercomRecordThread 初始录音开始");
                if (MainActivity.this.recBufSize == 0) {
                    MainActivity.this.recBufSize = AudioRecord.getMinBufferSize(8000, 2, 2);
                }
                if (MainActivity.this.playBufSize == 0) {
                    MainActivity.this.playBufSize = AudioTrack.getMinBufferSize(8000, 2, 2);
                }
                if (MainActivity.this.audioRecord == null || MainActivity.this.audioTrack == null) {
                    if (ActivityCompat.checkSelfPermission(AppApplication.getContext(), Manifest.permission.RECORD_AUDIO) != 0) {
                        return;
                    }
                    MainActivity.this.audioRecord = new AudioRecord(1, 8000, 2, 2, MainActivity.this.recBufSize);
                    MainActivity.this.audioTrack = new AudioTrack(3, 8000, 2, 2, MainActivity.this.playBufSize, 1);
                }
                float fFloatValue = Float.valueOf((String) SPUserInfoUtils.get(AppApplication.getContext(), "ShoutingVolume", "50")).floatValue() / 100.0f;
                MainActivity.this.audioTrack.setStereoVolume(fFloatValue, fFloatValue);
                MainActivity.this.terminalID = (String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "");
                LogUtils.d("MainActivity", "IntercomRecordThread 初始录音结束");
                byte[] bArr = new byte[MainActivity.this.recBufSize / 2];
                MainActivity.this.audioRecord.startRecording();
                MainActivity.this.audioTrack.play();
                MainActivity.this.controlHorn(false, false, true, 1);
                Generate808ReqPackage.intercomBagSerialNumber = 0;
                while (MainActivity.this.isIntercomRecording) {
                    int i = MainActivity.this.audioRecord.read(bArr, 0, MainActivity.this.recBufSize / 2);
                    byte[] bArr2 = new byte[i];
                    System.arraycopy(bArr, 0, bArr2, 0, i);
                    MainActivity.this.sendIntercomBag(bArr2);
                }
                MainActivity.this.controlHorn(false, false, false, 1);
                MainActivity.this.audioTrack.stop();
                MainActivity.this.audioRecord.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startIntercomBagThread() {
        closeIntercomBagThread();
        IntercomBagThread intercomBagThread = new IntercomBagThread();
        this.intercomBagThread = intercomBagThread;
        intercomBagThread.start();
        LogUtils.d("MainActivity", "启动startIntercomBagThread");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendIntercomBag(byte[] bArr) {
        G711a.Result result = new G711a.Result();
        this.sendData = result;
        G711a.G711aEncode(bArr, result);
        SocketIntercomManage.getInstance().sendReq(Generate808ReqPackage.generateIntercomBag(this.sendData.getDataArr(), this.terminalID, this.channelNumber));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void platformSwitch(String str, ReportInfoModel reportInfoModel, int i) {
        BusLineInfoModel busLineInfoModelQueryLineInfo = ((MainPresenter) this.mvpPresenter).queryLineInfo(String.valueOf(i));
        if (busLineInfoModelQueryLineInfo != null) {
            runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.18
                @Override // java.lang.Runnable
                public void run() {
                    VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                    voicePlayOperationParam.setOperation(3);
                    voicePlayOperationParam.setContent("线路切换成功");
                    MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                }
            });
            reportInfoModel.setResult(0);
            SocketManage.getInstance().sendReq(Generate808ReqPackage.generateGeneralResponse(reportInfoModel));
            if (AppApplication.listBusLine.size() > 0 && !AppApplication.listBusLine.get(0).getBusLineName().equals(busLineInfoModelQueryLineInfo.getLineName())) {
                this.isSwitchLine = true;
                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, busLineInfoModelQueryLineInfo.getLineNumber());
            }
            this.switchType = 2;
            ((MainPresenter) this.mvpPresenter).getBusLineList(busLineInfoModelQueryLineInfo.getLineName() + str);
            return;
        }
        reportInfoModel.setResult(2);
        SocketManage.getInstance().sendReq(Generate808ReqPackage.generateGeneralResponse(reportInfoModel));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openShouting() {
        closeShouting();
        initShouting();
        ShoutingThread shoutingThread = new ShoutingThread();
        this.shoutingThread = shoutingThread;
        shoutingThread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeShouting() {
        ShoutingThread shoutingThread = this.shoutingThread;
        if (shoutingThread != null) {
            this.isRecording = false;
            shoutingThread.setSuspend(true);
            try {
                this.shoutingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.shoutingThread = null;
        }
    }

    private void initShouting() {
        this.shoutingOperationStep = 0;
        if (this.recBufSize == 0) {
            this.recBufSize = AudioRecord.getMinBufferSize(8000, 2, 2);
        }
        if (this.playBufSize == 0) {
            this.playBufSize = AudioTrack.getMinBufferSize(8000, 2, 2);
        }
        if (this.audioRecord == null || this.audioTrack == null) {
            if (ActivityCompat.checkSelfPermission(AppApplication.getContext(), Manifest.permission.RECORD_AUDIO) != 0) {
                return;
            }
            this.audioRecord = new AudioRecord(1, 8000, 2, 2, this.recBufSize);
            this.audioTrack = new AudioTrack(3, 8000, 2, 2, this.playBufSize, 1);
        }
        float fFloatValue = Float.valueOf((String) SPUserInfoUtils.get(this, "ShoutingVolume", "50")).floatValue() / 100.0f;
        this.audioTrack.setStereoVolume(fFloatValue, fFloatValue);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 1) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            Toast.makeText(this, "拒绝权限，无法使用程序。", 1).show();
            finish();
        }
    }

    private class ShoutingThread extends Thread {
        private boolean suspend;

        private ShoutingThread() {
            this.suspend = false;
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            while (!this.suspend) {
                int iGpioRead = GpioOperation.gpioRead("gpio3a4");
                int iGpioRead2 = GpioOperation.gpioRead("gpio3a3");
                if (iGpioRead == 1 && iGpioRead2 == 0) {
                    if (MainActivity.this.shoutingOperationStep != 1) {
                        if (!MainActivity.this.isRecording) {
                            MainActivity.this.isRecording = true;
                            MainActivity.this.new RecordPlayThread().start();
                        }
                        MainActivity.this.shoutingOperationStep = 1;
                        MainActivity.this.controlHorn(false, true, false, 1);
                        Message messageObtainMessage = MainActivity.this.shoutingHandle.obtainMessage();
                        messageObtainMessage.what = 1;
                        MainActivity.this.shoutingHandle.sendMessage(messageObtainMessage);
                    }
                } else if (iGpioRead == 0 && iGpioRead2 == 1) {
                    if (MainActivity.this.shoutingOperationStep != 2) {
                        if (!MainActivity.this.isRecording) {
                            MainActivity.this.isRecording = true;
                            MainActivity.this.new RecordPlayThread().start();
                        }
                        MainActivity.this.shoutingOperationStep = 2;
                        MainActivity.this.controlHorn(true, false, false, 1);
                        Message messageObtainMessage2 = MainActivity.this.shoutingHandle.obtainMessage();
                        messageObtainMessage2.what = 2;
                        MainActivity.this.shoutingHandle.sendMessage(messageObtainMessage2);
                    }
                } else if (iGpioRead != 0 || iGpioRead2 != 0) {
                    if (iGpioRead == 1 && iGpioRead2 == 1 && MainActivity.this.shoutingOperationStep != 4) {
                        MainActivity.this.isRecording = false;
                        MainActivity.this.shoutingOperationStep = 4;
                        MainActivity.this.controlHorn(false, false, false, 1);
                        Message messageObtainMessage3 = MainActivity.this.shoutingHandle.obtainMessage();
                        messageObtainMessage3.what = 4;
                        MainActivity.this.shoutingHandle.sendMessage(messageObtainMessage3);
                    }
                } else if (MainActivity.this.shoutingOperationStep != 3) {
                    if (!MainActivity.this.isRecording) {
                        MainActivity.this.isRecording = true;
                        MainActivity.this.new RecordPlayThread().start();
                    }
                    MainActivity.this.shoutingOperationStep = 3;
                    MainActivity.this.controlHorn(true, true, false, 1);
                    Message messageObtainMessage4 = MainActivity.this.shoutingHandle.obtainMessage();
                    messageObtainMessage4.what = 3;
                    MainActivity.this.shoutingHandle.sendMessage(messageObtainMessage4);
                }
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class RecordPlayThread extends Thread {
        RecordPlayThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                short[] sArr = new short[MainActivity.this.recBufSize / 2];
                MainActivity.this.audioRecord.startRecording();
                MainActivity.this.audioTrack.play();
                while (MainActivity.this.isRecording) {
                    int i = MainActivity.this.audioRecord.read(sArr, 0, MainActivity.this.recBufSize / 2);
                    short[] sArr2 = new short[i];
                    System.arraycopy(sArr, 0, sArr2, 0, i);
                    MainActivity.this.audioTrack.write(sArr2, 0, i);
                }
                MainActivity.this.audioTrack.stop();
                MainActivity.this.audioRecord.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openGpsReport() {
        closeGpsReport();
        GPSReport gPSReport = new GPSReport();
        this.gpsReport = gPSReport;
        gPSReport.start();
    }

    private void closeGpsReport() {
        GPSReport gPSReport = this.gpsReport;
        if (gPSReport != null) {
            gPSReport.setSuspend(true);
            try {
                this.gpsReport.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.gpsReport = null;
        }
    }

    private class GPSReport extends Thread {
        private boolean suspend;

        private GPSReport() {
            this.suspend = false;
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            String str = (String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchInfoInterval", "5");
            while (!this.suspend) {
                if (MainActivity.this.dispatchProtocol.trim().equals("DVR")) {
                    MainActivity.this.assembleGPSData(1);
                } else if (MainActivity.this.dispatchProtocol.trim().equals("ALINK")) {
                    MainActivity.this.assembleGPSData(2);
                } else if (MainActivity.this.dispatchProtocol.trim().equals("808") || MainActivity.this.dispatchProtocol.trim().equals("CC808")) {
                    MainActivity.this.assembleGPSData(3);
                }
                try {
                    Thread.sleep(Integer.valueOf(str).intValue() * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void assembleGPSData(int i) {
        if (AppApplication.listBusLine != null && AppApplication.listBusLine.size() > 0) {
            if (GPSMonitor.wSpeed != null && GPSMonitor.wSpeed.trim().length() > 0) {
                String strValueOf = String.valueOf(Double.valueOf(GPSMonitor.wSpeed.trim()).doubleValue() * 1.852d);
                this.speedTmp = strValueOf;
                if (strValueOf == null || strValueOf.trim().equals("") || Float.valueOf(this.speedTmp).intValue() > 90) {
                    return;
                }
                int i2 = this.assembleGPSDataCount;
                if (i2 > 2) {
                    ReportInfoModel reportInfoModel = new ReportInfoModel();
                    reportInfoModel.setBusLineName(AppApplication.listBusLine.get(0).getBusLineName());
                    if (GPSMonitor.wszLatitudeE != null && !GPSMonitor.wszLatitudeE.trim().equals("")) {
                        reportInfoModel.setLongitudeE(GPSMonitor.wszLatitudeE);
                        reportInfoModel.setLatitudeN(GPSMonitor.wszLatitudeN);
                    } else {
                        reportInfoModel.setLongitudeE("0");
                        reportInfoModel.setLatitudeN("0");
                    }
                    if (GPSMonitor.wszLatitude != null && !GPSMonitor.wszLatitude.trim().equals("") && GPSMonitor.wszLongitude != null && !GPSMonitor.wszLongitude.trim().equals("")) {
                        this.latitudeTmp = GPSMonitor.wszLatitude;
                        LogUtils.d("MainActivity", "latitudeTmp:" + this.latitudeTmp);
                        String[] strArrSplit = String.valueOf(Double.valueOf(this.latitudeTmp).doubleValue() / 100.0d).split("\\.");
                        if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                            reportInfoModel.setLatitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue())));
                            LogUtils.d("MainActivity", "latValue:" + reportInfoModel.getLatitude());
                            this.longitudeTmp = GPSMonitor.wszLongitude;
                            LogUtils.d("MainActivity", "longitudeTmp:" + this.longitudeTmp);
                            String[] strArrSplit2 = String.valueOf(Double.valueOf(this.longitudeTmp).doubleValue() / 100.0d).split("\\.");
                            if (strArrSplit2.length > 1 && strArrSplit2[1].length() > 2) {
                                reportInfoModel.setLongitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue())));
                                LogUtils.d("MainActivity", "longValue:" + reportInfoModel.getLongitude());
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
                    LogUtils.d("MainActivity", "速度:" + GPSMonitor.wSpeed);
                    if (GPSMonitor.wSpeed != null && !GPSMonitor.wSpeed.trim().equals("") && Float.valueOf(GPSMonitor.wSpeed.trim()).intValue() >= 1) {
                        if (i == 3) {
                            this.speedPositionTmp = String.valueOf(Float.valueOf(JavaUtils.toNumberFormat(Double.valueOf(GPSMonitor.wSpeed.trim()).doubleValue() * 1.852d, 1)).floatValue() * 10.0f);
                        } else {
                            this.speedPositionTmp = String.valueOf(Double.valueOf(GPSMonitor.wSpeed.trim()).doubleValue() * 1.852d);
                        }
                        reportInfoModel.setSpeed(Float.valueOf(this.speedPositionTmp).intValue());
                    } else {
                        reportInfoModel.setSpeed(0);
                    }
                    if (GPSMonitor.wCourse != null && GPSMonitor.wCourse.trim().length() > 0) {
                        reportInfoModel.setAngle(Float.valueOf(GPSMonitor.wCourse.trim()).intValue());
                    } else {
                        reportInfoModel.setAngle(0);
                    }
                    reportInfoModel.setDirection(AppApplication.listBusLine.get(0).getDirection());
                    reportInfoModel.setBusNo(this.currentStation);
                    if (!this.isNextStation) {
                        reportInfoModel.setStatus(0);
                    } else {
                        reportInfoModel.setStatus(1);
                    }
                    reportInfoModel.setOverSpeed(1);
                    if (i == 1) {
                        DVRProtocol.getInstance().sendGPSReport(reportInfoModel);
                        return;
                    }
                    if (i == 2 || i == 3) {
                        if (SocketManage.getInstance().iHeartbeat) {
                            if (SocketManage.getInstance().isClosed() || !SocketManage.getInstance().isConnected()) {
                                SocketManage.getInstance().iHeartbeat = false;
                            } else if (i == 2) {
                                SocketManage.getInstance().sendReq(GenerateReqPackage.gpsReport(reportInfoModel));
                            } else {
                                LogUtils.d("MainActivity", "assembleGPSData TotalMileage:" + reportInfoModel.getTotalMileage());
                                reportInfoModel.setLineNumber(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, "0").toString());
                                reportInfoModel.setVehicleStatus(this.vehicleStatusId);
                                reportInfoModel.setSatellites(GPSMonitor.wUsedSatellites_gps);
                                reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
                                reportInfoModel.setTerminalDate(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
                                SocketManage.getInstance().sendReq(Generate808ReqPackage.positionInfoReport(reportInfoModel));
                            }
                        } else {
                            runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.19
                                @Override // java.lang.Runnable
                                public void run() {
                                    AppApplication.cmsState = MainActivity.this.getResources().getString(R.string.unconnected);
                                    if (MainActivity.this.tvCMS != null) {
                                        MainActivity.this.tvCMS.setText(String.format(MainActivity.this.cmsValue, AppApplication.cmsState));
                                    }
                                }
                            });
                        }
                        if (SocketManage.getInstance().heartbeatTime <= 0 || (System.currentTimeMillis() / 1000) - SocketManage.getInstance().heartbeatTime <= 45) {
                            return;
                        }
                        LogUtils.d("MainActivity", "assembleGPSData Main进入TCP重连.");
                        SocketManage.getInstance().closeAllSocket();
                        SocketManage.getInstance().connect();
                        return;
                    }
                    return;
                }
                this.assembleGPSDataCount = i2 + 1;
                return;
            }
            cmsCheck(i);
            return;
        }
        cmsCheck(i);
    }

    private void cmsCheck(int i) {
        if (i == 2 || i == 3) {
            if (SocketManage.getInstance().iHeartbeat) {
                if (SocketManage.getInstance().isClosed() || !SocketManage.getInstance().isConnected()) {
                    LogUtils.d("MainActivity", "cmsCheck iHeartbeat=true 未连接");
                    SocketManage.getInstance().iHeartbeat = false;
                }
            } else {
                LogUtils.d("MainActivity", "cmsCheck iHeartbeat=false 未连接");
                runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.20
                    @Override // java.lang.Runnable
                    public void run() {
                        LogUtils.d("MainActivity", "cmsCheck iHeartbeat=false 更新UI");
                        AppApplication.cmsState = MainActivity.this.getResources().getString(R.string.unconnected);
                        if (MainActivity.this.tvCMS != null) {
                            MainActivity.this.tvCMS.setText(String.format(MainActivity.this.cmsValue, AppApplication.cmsState));
                        }
                    }
                });
            }
            if (SocketManage.getInstance().heartbeatTime <= 0 || (System.currentTimeMillis() / 1000) - SocketManage.getInstance().heartbeatTime <= 45) {
                return;
            }
            LogUtils.d("MainActivity", "cmsCheck进入 Main进入TCP重连.");
            SocketManage.getInstance().closeAllSocket();
            SocketManage.getInstance().connect();
        }
    }

    private void assembleMileage(String str, String str2) {
        if (str.length() <= 2 || str2.length() <= 2) {
            return;
        }
        if (!JavaUtils.isEmpty(this.lastTimeLatitude) && !JavaUtils.isEmpty(this.lastTimeLongitude)) {
            double distance = AndroidUtils.getDistance(Double.valueOf(str2).doubleValue(), Double.valueOf(str).doubleValue(), Double.valueOf(this.lastTimeLongitude).doubleValue(), Double.valueOf(this.lastTimeLatitude).doubleValue());
            LogUtils.d("MainActivity", "distance:" + distance);
            if (distance > 0.0d) {
                if (distance < 600.0d) {
                    this.deviationCount = 0;
                    this.totalMileage += distance / 1000.0d;
                    this.lastTimeLatitude = str;
                    this.lastTimeLongitude = str2;
                    LogUtils.d("MainActivity", "totalMileage:" + this.totalMileage);
                    SPUserInfoUtils.put(this, SPUserInfoUtils.LASTTIMELATITUDE, this.lastTimeLatitude);
                    SPUserInfoUtils.put(this, SPUserInfoUtils.LASTTIMELONGITUDE, this.lastTimeLongitude);
                    SPUserInfoUtils.put(this, SPUserInfoUtils.TOTALMILEAGE, String.valueOf(this.totalMileage));
                    return;
                }
                int i = this.deviationCount + 1;
                this.deviationCount = i;
                if (i >= 3) {
                    this.deviationCount = 0;
                    this.lastTimeLatitude = str;
                    this.lastTimeLongitude = str2;
                    SPUserInfoUtils.put(this, SPUserInfoUtils.LASTTIMELATITUDE, str);
                    SPUserInfoUtils.put(this, SPUserInfoUtils.LASTTIMELONGITUDE, this.lastTimeLongitude);
                    return;
                }
                return;
            }
            return;
        }
        this.lastTimeLatitude = str;
        this.lastTimeLongitude = str2;
        SPUserInfoUtils.put(this, SPUserInfoUtils.LASTTIMELATITUDE, str);
        SPUserInfoUtils.put(this, SPUserInfoUtils.LASTTIMELONGITUDE, this.lastTimeLongitude);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void assembleDispatchReply(int i, ReportInfoModel reportInfoModel) {
        if (i == 1) {
            DVRProtocol.getInstance().sendDispatchReply(reportInfoModel);
            return;
        }
        if (i == 2) {
            SocketManage.getInstance().sendReq(GenerateReqPackage.dispatchReply(reportInfoModel));
        } else if (i == 3) {
            reportInfoModel.setResponseID("0801");
            SocketManage.getInstance().sendReq(Generate808ReqPackage.generateGeneralResponse(reportInfoModel));
        }
    }

    /* JADX INFO: renamed from: com.lianhexinye.m90.ui.activity.MainActivity$21, reason: invalid class name */
    class AnonymousClass21 implements DVRProtocol.DVRCallback {
        AnonymousClass21() {
        }

        @Override // com.lianhexinye.m90.serialport.DVRProtocol.DVRCallback
        public void onResult(String str) {
            if (str.length() == 2 && (str.trim().equals("有效") || str.trim().equals("无效"))) {
                if (str.equals(MainActivity.this.dvrState)) {
                    return;
                }
                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.21.1
                    @Override // java.lang.Runnable
                    public void run() {
                    }
                });
                return;
            }
            if (str.length() < 12 || !str.toLowerCase().substring(0, 8).trim().equals("55070800")) {
                if (str.length() >= 156 && str.toLowerCase().substring(0, 8).trim().equals("550e4800")) {
                    if (JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.trim().substring(8, 16)), 0) >= 0) {
                        try {
                            long jByteToInt_HL = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.trim().substring(16, 24)), 0);
                            final String strTrim = JavaUtils.stringToGBK(str.trim().substring(24, 88)).trim();
                            SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DRIVERID, String.valueOf(jByteToInt_HL));
                            SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DRIVERNAME, strTrim);
                            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.21.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    MainActivity.this.tvDriver.setText(String.format(MainActivity.this.getResources().getString(R.string.main_driver), strTrim));
                                    VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                    voicePlayOperationParam.setOperation(3);
                                    voicePlayOperationParam.setContent(MainActivity.this.getResources().getString(R.string.paycard_suss));
                                    MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                                }
                            });
                            return;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.21.3
                        @Override // java.lang.Runnable
                        public void run() {
                            VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                            voicePlayOperationParam.setOperation(3);
                            voicePlayOperationParam.setContent(MainActivity.this.getResources().getString(R.string.paycard_fail));
                            MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                        }
                    });
                    return;
                }
                if (str.length() >= 148 && str.toLowerCase().substring(0, 8).trim().equals("550f4400")) {
                    try {
                        long jByteToInt_HL2 = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.trim().substring(8, 16)), 0);
                        JavaUtils.stringToGBK(str.trim().substring(16, 56)).trim();
                        int iByteToInt_HL = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.trim().substring(56, 64)), 0);
                        int iHexToInt = JavaUtils.HexToInt(str.trim().substring(64, 66));
                        int iHexToInt2 = JavaUtils.HexToInt(str.trim().substring(66, 68));
                        final String strValueOf = String.valueOf(JavaUtils.HexToInt(str.trim().substring(68, 70)));
                        JavaUtils.stringToGBK(str.trim().substring(70, 136)).trim();
                        final String strTrim2 = JavaUtils.stringToGBK(str.trim().substring(136, 144)).trim();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.set(11, Integer.valueOf(strTrim2.substring(0, 2)).intValue());
                        calendar.set(12, Integer.valueOf(strTrim2.substring(2, 4)).intValue());
                        calendar.set(13, 0);
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DEPARTURETIME, JavaUtils.dateToString(calendar.getTime(), new String[0]));
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.UPLINK, Integer.valueOf(iHexToInt));
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.LINEGUID, Integer.valueOf(iByteToInt_HL));
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.SCHEDULENO, Integer.valueOf(iHexToInt2));
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.TIMESNO, strValueOf);
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 2);
                        if (strTrim2 != null && strTrim2.trim().length() >= 4) {
                            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.21.4
                                @Override // java.lang.Runnable
                                public void run() {
                                    VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                    voicePlayOperationParam.setOperation(3);
                                    voicePlayOperationParam.setContent("收到新的调度信息，请按计划时间发车");
                                    MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                                    MainActivity.this.tvPlannedTime1.setText(Html.fromHtml(MainActivity.this.getString(R.string.main_planned_time, strValueOf, strTrim2.substring(0, 2) + ":" + strTrim2.substring(2, 4), "-- : --")));
                                }
                            });
                        }
                        ReportInfoModel reportInfoModel = new ReportInfoModel();
                        reportInfoModel.setMsgSn(jByteToInt_HL2);
                        reportInfoModel.setLineGuid(iByteToInt_HL);
                        reportInfoModel.setDirection(iHexToInt);
                        reportInfoModel.setScheduleNo(iHexToInt2);
                        reportInfoModel.setTimesNo(Integer.valueOf(strValueOf).intValue());
                        reportInfoModel.setResult(1);
                        MainActivity.this.assembleDispatchReply(1, reportInfoModel);
                        MainActivity.this.departRemind();
                        return;
                    } catch (UnsupportedEncodingException e2) {
                        e2.printStackTrace();
                        return;
                    }
                }
                if (str.length() >= 276 && str.toLowerCase().substring(0, 8).trim().equals("55128400")) {
                    try {
                        long jByteToInt_HL3 = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.trim().substring(8, 16)), 0);
                        final String strTrim3 = JavaUtils.stringToGBK(str.trim().substring(16, BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA)).trim();
                        MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.21.5
                            @Override // java.lang.Runnable
                            public void run() {
                                VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                voicePlayOperationParam.setOperation(3);
                                voicePlayOperationParam.setContent(strTrim3);
                                MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                                if (MainActivity.this.dialogSingleView != null) {
                                    MainActivity.this.dialogSingleView.dismiss();
                                    MainActivity.this.dialogSingleView = null;
                                }
                                MainActivity.this.reverseCloseTip();
                                MainActivity.this.dialogSingleView = new DialogSingleView.Builder(MainActivity.this).setContent(strTrim3).setOnOKClickListener(new DialogSingleView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.21.5.1
                                    @Override // com.lianhexinye.m90.common.widget.DialogSingleView.OnOKCancelClickListener
                                    public void onOKClick() {
                                        MainActivity.this.dialogSingleView.dismiss();
                                    }
                                }).build();
                                MainActivity.this.dialogSingleView.show();
                            }
                        });
                        ReportInfoModel reportInfoModel2 = new ReportInfoModel();
                        reportInfoModel2.setResult(1);
                        reportInfoModel2.setMsgSn(jByteToInt_HL3);
                        MainActivity.this.assembleNoticeReply(2, reportInfoModel2);
                        return;
                    } catch (UnsupportedEncodingException e3) {
                        e3.printStackTrace();
                        return;
                    }
                }
                if (str.length() >= 28) {
                    str.toLowerCase().substring(0, 8).trim().equals("550c0800");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void assembleNoticeReply(int i, ReportInfoModel reportInfoModel) {
        if (i == 1) {
            DVRProtocol.getInstance().sendLowerReply(reportInfoModel);
        } else if (i == 2) {
            SocketManage.getInstance().sendReq(GenerateReqPackage.lowerReply(reportInfoModel));
        }
    }

    private void assembleSignInOut(int i, ReportInfoModel reportInfoModel) {
        reportInfoModel.setBusLineName("");
        reportInfoModel.setCardNo("");
        reportInfoModel.setDriverId(12L);
        reportInfoModel.setStatus(1);
        if (i == 1) {
            DVRProtocol.getInstance().sendDriversSignInOut(reportInfoModel);
        } else if (i == 2) {
            SocketManage.getInstance().sendReq(GenerateReqPackage.driversSignInOut(reportInfoModel));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void departRemind() {
        if (((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue() == 2) {
            String str = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DEPARTURETIME, "");
            if (JavaUtils.isEmpty(str)) {
                return;
            }
            try {
                String[] strArrSplit = JavaUtils.compareDifferTimeSize(str, JavaUtils.dateToString(new Date(), new String[0])).trim().split(",");
                if (strArrSplit[0].equals("0")) {
                    if (Integer.valueOf(strArrSplit[1]).intValue() >= 0 && Integer.valueOf(strArrSplit[2]).intValue() >= 0 && Integer.valueOf(strArrSplit[3]).intValue() >= 0) {
                        int iIntValue = (Integer.valueOf(strArrSplit[1]).intValue() * 60 * 60) + (Integer.valueOf(strArrSplit[2]).intValue() * 60) + Integer.valueOf(strArrSplit[3]).intValue();
                        if (iIntValue > 0) {
                            int i = (iIntValue - 60) * 1000;
                            if (i <= 0) {
                                startDispatchTimer(0, iIntValue * 1000);
                            } else {
                                startDispatchTimer(i, iIntValue * 1000);
                            }
                        }
                    } else {
                        String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.OT, "").toString();
                        if (string.length() > 0) {
                            int i2 = (Integer.parseInt(string) * 60) - Math.abs((((Integer.valueOf(strArrSplit[1]).intValue() * 60) * 60) + (Integer.valueOf(strArrSplit[2]).intValue() * 60)) + Integer.valueOf(strArrSplit[3]).intValue());
                            if (i2 > 0) {
                                startDispatchTimer(i2);
                            } else {
                                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 1);
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void startDispatchTimer(long j) {
        reverseDispatchTimer();
        String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.OTS, "").toString();
        if (string.length() > 0) {
            long j2 = Long.parseLong(string) * 60 * 1000;
            Timer timer = new Timer();
            this.timer2 = timer;
            timer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.22
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (MainActivity.this == null || 2 != ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue()) {
                        MainActivity.this.timer2.cancel();
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.22.1
                            @Override // java.lang.Runnable
                            public void run() {
                                VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                voicePlayOperationParam.setOperation(3);
                                voicePlayOperationParam.setContent("请发车");
                                MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                            }
                        });
                    }
                }
            }, j2, j2);
            Timer timer2 = new Timer();
            this.timer3 = timer2;
            timer2.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.23
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (MainActivity.this != null) {
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 1);
                        if (MainActivity.this.timer2 != null) {
                            MainActivity.this.timer2.cancel();
                            MainActivity.this.timer2 = null;
                        }
                    }
                }
            }, j * 1000);
        }
    }

    private void startDispatchTimer(int i, int i2) {
        reverseDispatchTimer();
        Timer timer = new Timer();
        this.timer = timer;
        timer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.24
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (MainActivity.this == null || 2 != ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue()) {
                    return;
                }
                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.24.1
                    @Override // java.lang.Runnable
                    public void run() {
                        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                        voicePlayOperationParam.setOperation(3);
                        voicePlayOperationParam.setContent("请于1分钟后发车");
                        MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                    }
                });
            }
        }, i);
        Timer timer2 = new Timer();
        this.timer1 = timer2;
        long j = i2;
        timer2.schedule(new AnonymousClass25(), j);
        String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.OTS, "").toString();
        if (string.length() > 0) {
            long j2 = Long.parseLong(string) * 60 * 1000;
            Timer timer3 = new Timer();
            this.timer2 = timer3;
            timer3.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.26
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (MainActivity.this == null || 2 != ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue()) {
                        MainActivity.this.timer2.cancel();
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.26.1
                            @Override // java.lang.Runnable
                            public void run() {
                                VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                voicePlayOperationParam.setOperation(3);
                                voicePlayOperationParam.setContent("请发车");
                                MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                            }
                        });
                    }
                }
            }, j + j2, j2);
        }
        String string2 = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.OT, "").toString();
        if (string2.length() > 0) {
            long j3 = Long.parseLong(string2) * 60 * 1000;
            Timer timer4 = new Timer();
            this.timer3 = timer4;
            timer4.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.27
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (MainActivity.this != null) {
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 1);
                        if (MainActivity.this.timer2 != null) {
                            MainActivity.this.timer2.cancel();
                            MainActivity.this.timer2 = null;
                        }
                    }
                }
            }, j3 + j);
        }
    }

    /* JADX INFO: renamed from: com.lianhexinye.m90.ui.activity.MainActivity$25, reason: invalid class name */
    class AnonymousClass25 extends TimerTask {
        AnonymousClass25() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            if (MainActivity.this == null || 2 != ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue()) {
                return;
            }
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.25.1
                @Override // java.lang.Runnable
                public void run() {
                    VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                    voicePlayOperationParam.setOperation(3);
                    voicePlayOperationParam.setContent(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TTSNOTICECONTENTTWO, "发车时间到了,请确认").toString());
                    MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                    if (MainActivity.this.dialogSingleView != null) {
                        MainActivity.this.dialogSingleView.dismiss();
                        MainActivity.this.dialogSingleView = null;
                    }
                    MainActivity.this.reverseCloseTip();
                    MainActivity.this.dialogSingleView = new DialogSingleView.Builder(MainActivity.this).setContent("发车时间到了，请发车.").setOnOKClickListener(new DialogSingleView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.25.1.1
                        @Override // com.lianhexinye.m90.common.widget.DialogSingleView.OnOKCancelClickListener
                        public void onOKClick() {
                            if (2 == ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 0)).intValue() && AppApplication.currentBusLineModel != null) {
                                if (MainActivity.this.dispatchProtocol.trim().equals("DVR")) {
                                    MainActivity.this.assembleFirstStationReport(1, AppApplication.currentBusLineModel);
                                } else if (MainActivity.this.dispatchProtocol.trim().equals("ALINK")) {
                                    MainActivity.this.assembleFirstStationReport(2, AppApplication.currentBusLineModel);
                                }
                            }
                            MainActivity.this.dialogSingleView.dismiss();
                        }
                    }).build();
                    MainActivity.this.dialogSingleView.show();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void assembleFirstStationReport(int i, BusLineModel busLineModel) {
        reverseDispatchTimer();
        releaseCloseTipTimer();
        ReportInfoModel reportInfoModel = new ReportInfoModel();
        reportInfoModel.setBusLineName(busLineModel.getBusLineName());
        reportInfoModel.setDirection(((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.UPLINK, 0)).intValue());
        reportInfoModel.setLineGuid(((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINEGUID, 0)).intValue());
        reportInfoModel.setScheduleNo(((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.SCHEDULENO, 0)).intValue());
        reportInfoModel.setTimesNo(Integer.valueOf(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.TIMESNO, "0").toString()).intValue());
        reportInfoModel.setBusName(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
        if (i == 1) {
            DVRProtocol.getInstance().sendStartBusReport(reportInfoModel);
        } else if (i == 2) {
            SocketManage.getInstance().sendReq(GenerateReqPackage.startBusReport(reportInfoModel));
        }
        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHSTATE, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reverseCloseTip() {
        releaseCloseTipTimer();
        Timer timer = new Timer();
        this.closeTimerTip = timer;
        timer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.28
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.28.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (MainActivity.this.dialogSingleView != null) {
                            MainActivity.this.dialogSingleView.dismiss();
                            MainActivity.this.dialogSingleView = null;
                        }
                    }
                });
            }
        }, 30000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseCloseTipTimer() {
        Timer timer = this.closeTimerTip;
        if (timer != null) {
            timer.cancel();
            this.closeTimerTip = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reverseDispatchTimer() {
        Timer timer = this.timer;
        if (timer != null) {
            timer.cancel();
            this.timer = null;
        }
        Timer timer2 = this.timer1;
        if (timer2 != null) {
            timer2.cancel();
            this.timer1 = null;
        }
        Timer timer3 = this.timer2;
        if (timer3 != null) {
            timer3.cancel();
            this.timer2 = null;
        }
        Timer timer4 = this.timer3;
        if (timer4 != null) {
            timer4.cancel();
            this.timer3 = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean controlHorn(boolean z, boolean z2, boolean z3, int i) {
        GpioOperation.gpioWrite("gpio3a6", z3 ? "0" : "1");
        GpioOperation.gpioWrite("gpio1b1", z ? "1" : "0");
        GpioOperation.gpioWrite("gpio1b2", z2 ? "1" : "0");
        GpioOperation.gpioWrite("gpio0d6", z3 ? "1" : "0");
        if (i == 0) {
            if (z) {
                this.audioManager.setStreamVolume(3, Integer.valueOf((String) SPUserInfoUtils.get(this, "InnerVolume", "7")).intValue(), 0);
            }
            if (!z2) {
                return true;
            }
            this.audioManager.setStreamVolume(3, Integer.valueOf((String) SPUserInfoUtils.get(this, "ExternalVolume", "7")).intValue(), 0);
            return true;
        }
        if (i != 2) {
            return true;
        }
        if (z) {
            this.audioManager.setStreamVolume(3, Integer.valueOf((String) SPUserInfoUtils.get(this, "TTSInnerVolume", "7")).intValue(), 0);
        }
        if (!z2) {
            return true;
        }
        this.audioManager.setStreamVolume(3, Integer.valueOf((String) SPUserInfoUtils.get(this, "TTSExternalVolume", "7")).intValue(), 0);
        return true;
    }

    private static class ShoutingHandle extends Handler {
        private final WeakReference<MainActivity> activityWeakReference;

        public ShoutingHandle(MainActivity mainActivity) {
            this.activityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (this.activityWeakReference.get() == null) {
                return;
            }
            MainActivity mainActivity = this.activityWeakReference.get();
            int i = message.what;
            if (i == 1) {
                mainActivity.tvShouting.setText("SPK_OUT...");
                return;
            }
            if (i == 2) {
                mainActivity.tvShouting.setText("SPK_IN...");
                return;
            }
            if (i == 3) {
                mainActivity.tvShouting.setText("SPK_IN_OUT...");
            } else if (i == 4) {
                mainActivity.tvShouting.setText("");
            } else {
                if (i != 5) {
                    return;
                }
                mainActivity.tvShouting.setText("IP PHONE...");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initMediaPlayer(final VoicePlayOperationParam voicePlayOperationParam) {
        LogUtils.d("MainActivity", "initMediaPlayer进入");
        releaseMediaPlayer();
        MediaPlayer mediaPlayerCreate = MediaPlayer.create(this, Uri.parse("/system/product/media/audio/notifications/Altair.ogg"));
        this.musicPlayer = mediaPlayerCreate;
        mediaPlayerCreate.start();
        this.musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.29
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                MainActivity.this.musicPlayer.start();
            }
        });
        this.musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.30
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                LogUtils.d("MainActivity", "initMediaPlayer  voicePlayOperationParam.getOperation()：" + voicePlayOperationParam.getOperation());
                MainActivity.this.stopBusVoicePlay();
                MainActivity.this.stopTTS();
                int operation = voicePlayOperationParam.getOperation();
                if (operation == 0) {
                    MainActivity.this.playBusVoice();
                    return;
                }
                if (operation == 1) {
                    MainActivity.this.playServerVoice(voicePlayOperationParam.isiInner());
                    return;
                }
                if (operation == 2) {
                    MainActivity.this.playReportStationTTS(voicePlayOperationParam.getContent());
                    return;
                }
                if (operation == 3) {
                    MainActivity.this.playDispatchTTS(voicePlayOperationParam.getContent());
                } else if (operation == 4) {
                    MainActivity.this.mixedSeedingHandle();
                } else {
                    if (operation != 5) {
                        return;
                    }
                    MainActivity.this.playInsideAndOutsideTTSSwitch(voicePlayOperationParam.getContent(), voicePlayOperationParam.isiInner());
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mixedSeedingHandle() {
        this.busTTSContentIndex = -1;
        this.busVoiceContentIndex = -1;
        this.playControlFlowIndex = 0;
        this.istTTSMixture = true;
        controlHorn(true, true, false, 2);
        if (this.playControlFlow.get(this.playControlFlowIndex).intValue() == 0) {
            int i = this.busTTSContentIndex + 1;
            this.busTTSContentIndex = i;
            playMixtureTTS(this.busTTSContentList.get(i));
        } else {
            int i2 = this.busVoiceContentIndex + 1;
            this.busVoiceContentIndex = i2;
            playMixedSeeding(this.busVoiceContentList.get(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playMixedSeeding(String str) {
        LogUtils.d("MainActivity", "playMixedSeeding strPath:" + str);
        releaseMediaPlayer();
        MediaPlayer mediaPlayerCreate = MediaPlayer.create(this, Uri.parse(str));
        this.musicPlayer = mediaPlayerCreate;
        mediaPlayerCreate.start();
        this.musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.31
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                LogUtils.d("MainActivity", "playMixedSeeding start");
                MainActivity.this.musicPlayer.start();
            }
        });
        this.musicPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.32
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                LogUtils.d("MainActivity", "onError what:" + i + ",extra:" + i2);
                return false;
            }
        });
        this.musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.33
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                LogUtils.d("MainActivity", "mp3播放完成");
                if (MainActivity.this.playControlFlow.size() > MainActivity.this.playControlFlowIndex + 1) {
                    MainActivity.access$10608(MainActivity.this);
                    LogUtils.d("MainActivity", "playControlFlowIndex:" + MainActivity.this.playControlFlowIndex);
                    if (((Integer) MainActivity.this.playControlFlow.get(MainActivity.this.playControlFlowIndex)).intValue() == 0) {
                        MainActivity.access$10708(MainActivity.this);
                        LogUtils.d("MainActivity", "busTTSContentIndex:" + MainActivity.this.busTTSContentIndex);
                        MainActivity mainActivity = MainActivity.this;
                        mainActivity.playMixtureTTS((String) mainActivity.busTTSContentList.get(MainActivity.this.busTTSContentIndex));
                        return;
                    }
                    MainActivity.access$11008(MainActivity.this);
                    LogUtils.d("MainActivity", "busVoiceContentIndex:" + MainActivity.this.busVoiceContentIndex);
                    MainActivity mainActivity2 = MainActivity.this;
                    mainActivity2.playMixedSeeding((String) mainActivity2.busVoiceContentList.get(MainActivity.this.busVoiceContentIndex));
                    return;
                }
                LogUtils.d("MainActivity", "mp3播放结束");
                if (MainActivity.this.timerPlayer == null) {
                    MainActivity.this.timerPlayer = new Timer();
                }
                MainActivity.this.timerPlayer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.33.1
                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        MainActivity.this.controlHorn(false, false, false, 0);
                        MainActivity.this.sendLCDOpenSound();
                    }
                }, 350L);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playServerVoice(boolean z) {
        this.plaIndex = 0;
        if (z) {
            controlHorn(true, false, false, 0);
        } else {
            controlHorn(false, true, false, 0);
        }
        if (this.busVoiceList.size() > 0) {
            createNextMediaPlayer(this.busVoiceList.get(this.plaIndex));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopBusVoicePlay() {
        controlHorn(false, false, false, 0);
        Timer timer = this.timerPlayer;
        if (timer != null) {
            timer.cancel();
            this.timerPlayer = null;
        }
        try {
            MediaPlayer mediaPlayer = this.musicPlayer;
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                return;
            }
            this.musicPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playBusVoice() {
        this.plaIndex = 0;
        controlHorn(true, false, false, 0);
        if (this.busVoiceList.size() > 0) {
            createNextMediaPlayer(this.busVoiceList.get(this.plaIndex));
        }
    }

    private void addPlayDataSource(String str) {
        try {
            Uri uri = Uri.parse(str);
            this.musicPlayer.reset();
            this.musicPlayer.setDataSource(this, uri);
            this.musicPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            e3.printStackTrace();
        } catch (SecurityException e4) {
            e4.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createNextMediaPlayer(String str) {
        LogUtils.d("MainActivity", "Inner MediaPath:" + str);
        releaseMediaPlayer();
        MediaPlayer mediaPlayerCreate = MediaPlayer.create(this, Uri.parse(str));
        this.musicPlayer = mediaPlayerCreate;
        mediaPlayerCreate.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.34
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                MainActivity.this.musicPlayer.start();
            }
        });
        this.musicPlayer.setOnCompletionListener(new AnonymousClass35());
    }

    /* JADX INFO: renamed from: com.lianhexinye.m90.ui.activity.MainActivity$35, reason: invalid class name */
    class AnonymousClass35 implements MediaPlayer.OnCompletionListener {
        AnonymousClass35() {
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mediaPlayer) {
            MainActivity.access$11508(MainActivity.this);
            if (MainActivity.this.busVoiceList.size() <= 0 || MainActivity.this.busVoiceList.size() <= MainActivity.this.plaIndex) {
                if (MainActivity.this.busExternalVoiceList.size() > 0) {
                    if (MainActivity.this.busVoiceList.size() > 0) {
                        MainActivity.this.plaIndex = 0;
                        MainActivity.this.busVoiceList.clear();
                    }
                    if (MainActivity.this.busExternalVoiceList.size() <= 0 || MainActivity.this.busExternalVoiceList.size() <= MainActivity.this.plaIndex) {
                        if (MainActivity.this.timerPlayer == null) {
                            MainActivity.this.timerPlayer = new Timer();
                        }
                        MainActivity.this.timerPlayer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.35.3
                            @Override // java.util.TimerTask, java.lang.Runnable
                            public void run() {
                                MainActivity.this.controlHorn(false, false, false, 0);
                                MainActivity.this.sendLCDOpenSound();
                            }
                        }, 150L);
                        return;
                    }
                    if (MainActivity.this.timerPlayer == null) {
                        MainActivity.this.timerPlayer = new Timer();
                    }
                    MainActivity.this.timerPlayer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.35.2
                        @Override // java.util.TimerTask, java.lang.Runnable
                        public void run() {
                            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.35.2.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    if (MainActivity.this.plaIndex == 0) {
                                        MainActivity.this.controlHorn(false, true, false, 0);
                                    }
                                    LogUtils.d("MainActivity", "External MediaPath:" + ((String) MainActivity.this.busExternalVoiceList.get(MainActivity.this.plaIndex)));
                                    MainActivity.this.createNextMediaPlayer((String) MainActivity.this.busExternalVoiceList.get(MainActivity.this.plaIndex));
                                }
                            });
                        }
                    }, 150L);
                    return;
                }
                if (MainActivity.this.timerPlayer == null) {
                    MainActivity.this.timerPlayer = new Timer();
                }
                MainActivity.this.timerPlayer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.35.4
                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        MainActivity.this.controlHorn(false, false, false, 0);
                        MainActivity.this.sendLCDOpenSound();
                    }
                }, 350L);
                return;
            }
            if (MainActivity.this.timerPlayer == null) {
                MainActivity.this.timerPlayer = new Timer();
            }
            MainActivity.this.timerPlayer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.35.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    LogUtils.d("MainActivity", "Inner MediaPath:" + ((String) MainActivity.this.busVoiceList.get(MainActivity.this.plaIndex)));
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.35.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.createNextMediaPlayer((String) MainActivity.this.busVoiceList.get(MainActivity.this.plaIndex));
                        }
                    });
                }
            }, 100L);
        }
    }

    private void releaseMediaPlayer() {
        MediaPlayer mediaPlayer = this.musicPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            this.musicPlayer = null;
        }
    }

    private void open485SerialPort() {
        try {
            String str = (String) SPUserInfoUtils.get(this, "RS485Baud", "9600");
            LogUtils.d("open485SerialPort", "485baudRate :" + str);
            AppApplication.mSerial485Control = new Serial485Control(str);
            AppApplication.mSerial485Control.open();
            AppApplication.send485Thread = new Send485Thread();
            AppApplication.send485Thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Serial485Control extends SerialHelper {
        @Override // com.lianhexinye.serialprot.SerialHelper
        public void onDataReceived(ComBean comBean) {
        }

        public Serial485Control(String str) {
            super("/dev/ttyS7", str);
        }
    }

    public class Send485Thread extends Thread {
        private boolean suspend = false;
        private final LinkedList<byte[]> paramArrayByte = new LinkedList<>();

        public Send485Thread() {
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        public synchronized void send(byte[] bArr) {
            this.paramArrayByte.add(bArr);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            while (!this.suspend) {
                if (this.paramArrayByte.size() > 0) {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (AppApplication.mSerial485Control != null && AppApplication.mSerial485Control.isOpen() && !this.paramArrayByte.isEmpty()) {
                        byte[] bArrRemoveFirst = this.paramArrayByte.removeFirst();
                        LogUtils.d("MainActivity", "AppApplication.send485Thread send:" + JavaUtils.bytesToHexString(bArrRemoveFirst, bArrRemoveFirst.length));
                        AppApplication.mSerial485Control.send(bArrRemoveFirst);
                    }
                }
            }
        }
    }

    private void close485SerialPort() {
        if (AppApplication.send485Thread != null) {
            AppApplication.send485Thread.setSuspend(true);
            try {
                AppApplication.send485Thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (AppApplication.mSerial485Control == null || !AppApplication.mSerial485Control.isOpen()) {
            return;
        }
        AppApplication.mSerial485Control.close();
    }

    private void openJHYPort() {
        JHYThread jHYThread = this.jhyThread;
        if (jHYThread != null) {
            try {
                jHYThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.jhyThread = null;
        }
        JHYThread jHYThread2 = new JHYThread();
        this.jhyThread = jHYThread2;
        jHYThread2.start();
    }

    public class JHYThread extends Thread {
        byte[] jhyTmp = null;
        private boolean suspend = false;

        public JHYThread() {
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            while (!this.suspend) {
                if (AppApplication.jhyopd != null) {
                    byte[] bArr = AppApplication.jhyopd.read();
                    this.jhyTmp = bArr;
                    if (bArr != null && bArr.length > 0) {
                        JHYMonitor.getJHYPortData(bArr, bArr.length, MainActivity.this.jhySerialPortResultListener);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openGPSPort() {
        this.iOpenSpeeding = (String) SPUserInfoUtils.get(this, "IfSpeed", "否");
        Timer timer = this.gpsPortTimer;
        if (timer != null) {
            timer.cancel();
            this.gpsPortTimer = null;
        }
        Timer timer2 = new Timer();
        this.gpsPortTimer = timer2;
        timer2.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.37
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (MainActivity.this.gpsThread != null) {
                    MainActivity.this.Running = false;
                    try {
                        MainActivity.this.gpsThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MainActivity.this.gpsThread = null;
                }
                MainActivity.this.Running = true;
                MainActivity.this.gpsThread = MainActivity.this.new GPSThread();
                MainActivity.this.gpsThread.start();
            }
        }, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
    }

    public class GPSThread extends Thread {
        String strGPSMsg;
        byte[] temp = null;

        public GPSThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                Thread.sleep(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MainActivity.this.speedingInterval = (String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchSpeedingInterval", "10");
            while (MainActivity.this.Running) {
                if (AppApplication.opd != null) {
                    String str = AppApplication.opd.read();
                    this.strGPSMsg = str;
                    if (str != null && str.length() > 0) {
                        this.temp = null;
                        byte[] bArrStringToByte2 = Function.StringToByte2(this.strGPSMsg, "GB2312");
                        this.temp = bArrStringToByte2;
                        GPSMonitor.getPortData(bArrStringToByte2, bArrStringToByte2.length, MainActivity.this.gpsSerialPortResultListener);
                    } else {
                        MainActivity mainActivity = MainActivity.this;
                        if (mainActivity != null) {
                            mainActivity.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.GPSThread.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    AppApplication.gpsState = MainActivity.this.invalid;
                                    if (MainActivity.this.tvLocationGps != null) {
                                        MainActivity.this.tvLocationGps.setText(String.format(MainActivity.this.gpsValue, AppApplication.gpsState));
                                        MainActivity.this.tvMileage.setText("0");
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: renamed from: com.lianhexinye.m90.ui.activity.MainActivity$38, reason: invalid class name */
    class AnonymousClass38 implements GPSSerialPortResultListener {
        String speedTmp;
        String longitudeTmp = "";
        String langitudeTmp = "";
        private BusLineModel busLineModel = null;
        private BusLineFriendRemindModel busLineFriendRemindModel = null;
        int highSpeed = 0;

        AnonymousClass38() {
        }

        @Override // com.lianhexinye.m90.gps.GPSSerialPortResultListener
        public void onSuccessResult() {
            if (!GPSMonitor.bValidData) {
                MainActivity.this.validDataTime = System.currentTimeMillis();
            } else if (AppApplication.listBusLine != null && AppApplication.listBusLine.size() > 0 && !MainActivity.this.isCharteredBus && GPSMonitor.wSpeed != null && GPSMonitor.wSpeed.trim().length() > 0) {
                String strValueOf = String.valueOf(Double.valueOf(GPSMonitor.wSpeed.trim()).doubleValue() * 1.852d);
                this.speedTmp = strValueOf;
                if (strValueOf != null && !strValueOf.trim().equals("") && Float.valueOf(this.speedTmp).intValue() <= 90 && GPSMonitor.wszLongitude != null && !GPSMonitor.wszLongitude.trim().equals("") && GPSMonitor.wszLatitude != null && !GPSMonitor.wszLatitude.trim().equals("")) {
                    String str = GPSMonitor.wszLatitude;
                    this.langitudeTmp = str;
                    String[] strArrSplit = String.valueOf(Double.valueOf(str).doubleValue() / 100.0d).split("\\.");
                    if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                        double dDoubleValue = (Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue();
                        String str2 = GPSMonitor.wszLongitude;
                        this.longitudeTmp = str2;
                        String[] strArrSplit2 = String.valueOf(Double.valueOf(str2).doubleValue() / 100.0d).split("\\.");
                        if (strArrSplit2.length > 1 && strArrSplit2[1].length() > 2) {
                            MainActivity.this.gpsOperationParam.setcLong(Double.valueOf(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue()))).doubleValue());
                            MainActivity.this.gpsOperationParam.setcLat(Double.valueOf(String.format(Locale.CHINA, "%.6f", Double.valueOf(dDoubleValue))).doubleValue());
                            if ("是".equals(MainActivity.this.iOpenSpeeding) && !MainActivity.this.isDispatchEnd) {
                                if (System.currentTimeMillis() - MainActivity.this.validDataTime <= 3000) {
                                    if (!MainActivity.this.iValidDataTime) {
                                        MainActivity.this.iValidDataTime = true;
                                        MainActivity.this.validDataTime = System.currentTimeMillis();
                                    }
                                } else {
                                    String str3 = this.speedTmp;
                                    if (str3 == null || str3.trim().length() <= 0 || MainActivity.this.speedLimit.trim().equals("") || !JavaUtils.isNumeric(MainActivity.this.speedLimit)) {
                                        MainActivity.this.iSpeed = false;
                                    } else if (Float.valueOf(this.speedTmp).floatValue() <= Float.valueOf(MainActivity.this.speedLimit.trim()).floatValue()) {
                                        MainActivity.this.iSpeed = false;
                                        MainActivity.this.speedTime = System.currentTimeMillis();
                                    } else {
                                        if (this.highSpeed < Float.valueOf(this.speedTmp).intValue()) {
                                            this.highSpeed = Float.valueOf(this.speedTmp).intValue();
                                        }
                                        if (MainActivity.this.iSpeed) {
                                            long jCurrentTimeMillis = System.currentTimeMillis() - MainActivity.this.speedTime;
                                            if (jCurrentTimeMillis > FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY && System.currentTimeMillis() - MainActivity.this.intervalTime > 2500) {
                                                this.highSpeed = 0;
                                                MainActivity.this.intervalTime = System.currentTimeMillis();
                                                ReportInfoModel reportInfoModel = new ReportInfoModel();
                                                reportInfoModel.setTransmissionType("C8");
                                                reportInfoModel.setOverSpeedInfoType(65320);
                                                if (!MainActivity.this.isNextCross) {
                                                    this.busLineFriendRemindModel = AppApplication.listBusLineFriendRemindModel.get(MainActivity.this.currentCross);
                                                    reportInfoModel.setOverSpeedType(22);
                                                    reportInfoModel.setDataLength(148);
                                                    if (this.busLineFriendRemindModel.getFrNo() > 0) {
                                                        reportInfoModel.setCrossNumber(AppApplication.listBusLineFriendRemindModel.get(this.busLineFriendRemindModel.getFrNo() - 1).getCrossCode());
                                                    } else {
                                                        reportInfoModel.setCrossNumber(AppApplication.listBusLineFriendRemindModel.get(0).getCrossCode());
                                                    }
                                                    reportInfoModel.setCrossType(Integer.valueOf(this.busLineFriendRemindModel.getCrossType().trim()).intValue());
                                                    reportInfoModel.setCrossNo(MainActivity.this.currentCross);
                                                } else {
                                                    if (MainActivity.this.isNextStation) {
                                                        this.busLineModel = AppApplication.listBusLine.get(MainActivity.this.currentStation - 1);
                                                        reportInfoModel.setOverSpeedType(21);
                                                    } else {
                                                        this.busLineModel = AppApplication.listBusLine.get(MainActivity.this.currentStation);
                                                        reportInfoModel.setOverSpeedType(20);
                                                    }
                                                    reportInfoModel.setDataLength(149);
                                                    if (MainActivity.this.isNextStation) {
                                                        if (MainActivity.this.currentStation > 0) {
                                                            reportInfoModel.setSiteCode(AppApplication.listBusLine.get(MainActivity.this.currentStation - 1).getSiteCode());
                                                        } else {
                                                            reportInfoModel.setSiteCode(AppApplication.listBusLine.get(0).getSiteCode());
                                                        }
                                                    } else {
                                                        reportInfoModel.setSiteCode(this.busLineModel.getSiteCode());
                                                    }
                                                    reportInfoModel.setInLimitSpeed(Integer.valueOf(this.busLineModel.getSpeedLimitInStation()).intValue());
                                                    reportInfoModel.setOutLimitSpeed(Integer.valueOf(this.busLineModel.getSpeedLimit()).intValue());
                                                    reportInfoModel.setBusNo(MainActivity.this.currentStation);
                                                }
                                                reportInfoModel.setHighSpeed(this.highSpeed);
                                                reportInfoModel.setAverageSpeed(Float.valueOf(Float.valueOf(JavaUtils.toNumberFormat(Double.valueOf(this.speedTmp.trim()).doubleValue(), 2)).floatValue() * 100.0f).intValue());
                                                String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DRIVERCARDID, "").toString();
                                                LogUtils.v("MainActivity", "read driverCardId：" + string);
                                                if (string.length() > 0) {
                                                    reportInfoModel.setCardNo(string.substring(8, 16));
                                                } else {
                                                    reportInfoModel.setCardNo("00000000");
                                                }
                                                reportInfoModel.setContinueTime(jCurrentTimeMillis / 1000);
                                                reportInfoModel.setLatitude(String.valueOf(MainActivity.this.gpsOperationParam.getcLat()));
                                                reportInfoModel.setLongitude(String.valueOf(MainActivity.this.gpsOperationParam.getcLong()));
                                                reportInfoModel.setLineNumber(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, "0").toString());
                                                reportInfoModel.setTerminalDate(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
                                                reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
                                                MainActivity.this.assembleSpeedReport(3, reportInfoModel);
                                                MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.38.1
                                                    @Override // java.lang.Runnable
                                                    public void run() {
                                                        LogUtils.d("MainActivity", "播放超速音");
                                                        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
                                                        voicePlayOperationParam.setOperation(3);
                                                        voicePlayOperationParam.setContent("你已超速，请减速");
                                                        MainActivity.this.initMediaPlayer(voicePlayOperationParam);
                                                    }
                                                });
                                            }
                                        } else {
                                            MainActivity.this.speedTime = System.currentTimeMillis();
                                        }
                                        MainActivity.this.iSpeed = true;
                                    }
                                }
                            }
                            MainActivity.this.gpsOperationParam.setAngle(0.0d);
                            if (GPSMonitor.wCourse != null && !GPSMonitor.wCourse.trim().equals("")) {
                                MainActivity.this.gpsOperationParam.setAngle(Double.valueOf(GPSMonitor.wCourse).doubleValue());
                            }
                            if (MainActivity.this.isNextStation) {
                                MainActivity.this.gpsOperationParam.setiStationInner(false);
                            } else {
                                MainActivity.this.gpsOperationParam.setiStationInner(true);
                            }
                            if (MainActivity.this.isNextCross) {
                                MainActivity.this.gpsOperationParam.setiCrossInner(false);
                            } else {
                                MainActivity.this.gpsOperationParam.setiCrossInner(true);
                            }
                            if (MainActivity.this.gpsReportStationResult != null) {
                                if (MainActivity.this.gpsReportStationResult.getBusLineModel() != null) {
                                    MainActivity.this.gpsOperationParam.setBusLineModel(MainActivity.this.gpsReportStationResult.getBusLineModel());
                                }
                                if (MainActivity.this.gpsReportStationResult.getBusLineFriendRemindModel() != null) {
                                    MainActivity.this.gpsOperationParam.setBusLineFriendRemindModel(MainActivity.this.gpsReportStationResult.getBusLineFriendRemindModel());
                                }
                            } else {
                                MainActivity.this.gpsOperationParam.setBusLineModel(AppApplication.listBusLine.get(0));
                                MainActivity.this.gpsOperationParam.setBusLineFriendRemindModel(AppApplication.listBusLineFriendRemindModel.get(0));
                            }
                            MainActivity.this.gpsReportStationResult = null;
                            MainActivity mainActivity = MainActivity.this;
                            mainActivity.gpsReportStationResult = ((MainPresenter) mainActivity.mvpPresenter).getGpsRStationInfo(MainActivity.this.gpsOperationParam);
                            if (MainActivity.this.gpsReportStationResult.getOperationType() == 0) {
                                if (MainActivity.this.gpsReportStationResult.getBusLineModel().getBusNo() == AppApplication.listBusLine.size() - 1) {
                                    MainActivity.this.gpsOperationParam.setiInitSite(true);
                                }
                            } else if (MainActivity.this.gpsReportStationResult.getOperationType() == 2) {
                                MainActivity.this.gpsReportStationResult.setBusLineModel(null);
                            }
                        }
                    }
                }
            }
            if (MainActivity.this.tvLocationGps != null) {
                if (GPSMonitor.bValidData) {
                    if (!MainActivity.this.effective.equals(AppApplication.gpsState) || AndroidUtils.isEmpty(MainActivity.this.tvLocationGps.getText()) || -1 == MainActivity.this.tvLocationGps.getText().toString().indexOf(MainActivity.this.effective)) {
                        AppApplication.gpsState = MainActivity.this.effective;
                        MainActivity.this.assembleTerminalSelfCheck(3);
                        MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.38.2
                            @Override // java.lang.Runnable
                            public void run() {
                                MainActivity.this.tvLocationGps.setText(String.format(MainActivity.this.gpsValue, AppApplication.gpsState));
                            }
                        });
                    }
                    if (MainActivity.this.gpsReportStationResult != null) {
                        MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.38.3
                            @Override // java.lang.Runnable
                            public void run() {
                                if (GPSMonitor.wSpeed != null && GPSMonitor.wSpeed.trim().length() > 0) {
                                    AnonymousClass38.this.speedTmp = String.valueOf(Double.valueOf(GPSMonitor.wSpeed.trim()).doubleValue() * 1.852d);
                                    if (AnonymousClass38.this.speedTmp != null && !AnonymousClass38.this.speedTmp.trim().equals("")) {
                                        int iIntValue = Float.valueOf(AnonymousClass38.this.speedTmp).intValue();
                                        if (iIntValue <= 2) {
                                            MainActivity.this.tvMileage.setText("0");
                                        } else if (iIntValue <= 90) {
                                            MainActivity.this.tvMileage.setText(String.valueOf(iIntValue));
                                        }
                                    }
                                }
                                MainActivity.this.automaticSwitchCurrentStation(MainActivity.this.gpsReportStationResult);
                            }
                        });
                    }
                } else if (!MainActivity.this.invalid.equals(AppApplication.gpsState) || AndroidUtils.isEmpty(MainActivity.this.tvLocationGps.getText()) || -1 == MainActivity.this.tvLocationGps.getText().toString().indexOf(MainActivity.this.invalid)) {
                    AppApplication.gpsState = MainActivity.this.invalid;
                    MainActivity.this.assembleTerminalSelfCheck(3);
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.38.4
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.tvLocationGps.setText(String.format(MainActivity.this.gpsValue, AppApplication.gpsState));
                        }
                    });
                }
            }
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.38.5
                @Override // java.lang.Runnable
                public void run() {
                    if (MainActivity.this.iGpsInit || !GPSMonitor.bValidData || GPSMonitor.wDate == null || GPSMonitor.wDate.trim().equals("") || GPSMonitor.wTime == null || GPSMonitor.wTime.trim().equals("") || GPSMonitor.wDate.length() < 6 || GPSMonitor.wTime.length() < 6 || Integer.valueOf(GPSMonitor.wDate.substring(4, 6)).intValue() < 19) {
                        return;
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Integer.valueOf("20" + GPSMonitor.wDate.substring(4, 6)).intValue(), Integer.valueOf(GPSMonitor.wDate.substring(2, 4)).intValue() - 1, Integer.valueOf(GPSMonitor.wDate.substring(0, 2)).intValue(), Integer.valueOf(GPSMonitor.wTime.substring(0, 2)).intValue() + 8, Integer.valueOf(GPSMonitor.wTime.substring(2, 4)).intValue(), Integer.valueOf(GPSMonitor.wTime.substring(4, 6)).intValue());
                    int i = calendar.get(5);
                    if (i == 1 || i == 6 || i == 12 || i == 18 || i == 25) {
                        File file = new File(Constants.SD_ROOT + Constants.LOG_RES_PATH + "/info.log");
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    ((AlarmManager) AppApplication.getContext().getSystemService("alarm")).setTime(calendar.getTimeInMillis());
                    if (AppApplication.mSerial485Control != null && AppApplication.mSerial485Control.isOpen()) {
                        AppApplication.mSerial485Control.send(MainActivity.this.serialPortManager.crateTongdaProtocol().createLineState());
                        LogUtils.d("MainActivity", "LCD时间发送" + i);
                    }
                    MainActivity.this.iGpsInit = true;
                }
            });
        }

        @Override // com.lianhexinye.m90.gps.GPSSerialPortResultListener
        public void onFailResult() {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.38.6
                @Override // java.lang.Runnable
                public void run() {
                    AppApplication.gpsState = MainActivity.this.invalid;
                    MainActivity.this.tvLocationGps.setText(String.format(MainActivity.this.gpsValue, AppApplication.gpsState));
                    MainActivity.this.tvMileage.setText("0");
                }
            });
        }
    }

    private void speedVoice(BusLineModel busLineModel) {
        String str = busLineModel.getBusFilePath().substring(0, busLineModel.getBusFilePath().lastIndexOf("/Bus")) + "/CommonSounds/";
        stopBusVoicePlay();
        this.busVoiceList.clear();
        this.busExternalVoiceList.clear();
        if (new File(str + "Common/voiceD/超速语音.mp3").exists()) {
            this.busVoiceList.add(str + "Common/voiceD/超速语音.mp3");
        }
        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
        voicePlayOperationParam.setOperation(0);
        initMediaPlayer(voicePlayOperationParam);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void assembleSpeedReport(int i, ReportInfoModel reportInfoModel) {
        if (!this.isNextStation) {
            reportInfoModel.setStatus(0);
        } else {
            reportInfoModel.setStatus(1);
        }
        if (i == 1) {
            DVRProtocol.getInstance().sendSpeedAlarm(reportInfoModel);
            return;
        }
        if (i == 2) {
            SocketManage.getInstance().sendReq(GenerateReqPackage.speedAlarm(reportInfoModel));
        } else if (i == 3) {
            SocketManage.getInstance().sendReq(Generate808ReqPackage.generateOverspeedInfo(reportInfoModel));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void nowTime(int i) {
        if (AppApplication.listBusLine == null || AppApplication.listBusLine.get(0) == null) {
            return;
        }
        stopBusVoicePlay();
        this.busVoiceList.clear();
        this.busExternalVoiceList.clear();
        addNowTime(i, "voiceD");
        if (((String) SPUserInfoUtils.get(this, "IfEnglish", "否")).trim().equals("是")) {
            addNowTime(i, "voiceE");
        }
        if (((String) SPUserInfoUtils.get(this, "IfDialect", "否")).trim().equals("是")) {
            addNowTime(i, "voiceF");
        }
        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
        voicePlayOperationParam.setOperation(0);
        initMediaPlayer(voicePlayOperationParam);
    }

    private void addNowTime(int i, String str) {
        if (new File(AppApplication.listBusLine.get(0).getBusFilePath().substring(0, AppApplication.listBusLine.get(0).getBusFilePath().lastIndexOf("/Bus")) + "/CommonSounds/Clock/" + str + "/" + i + ".mp3").exists()) {
            this.busVoiceList.add(AppApplication.listBusLine.get(0).getBusFilePath().substring(0, AppApplication.listBusLine.get(0).getBusFilePath().lastIndexOf("/Bus")) + "/CommonSounds/Clock/" + str + "/" + i + ".mp3");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void automaticSwitchCurrentStation(GPSReportStationResult gPSReportStationResult) {
        int operationType = gPSReportStationResult.getOperationType();
        if (operationType != 0) {
            if (operationType != 1) {
                return;
            }
            if (gPSReportStationResult.getBusLineFriendRemindModel().getFrNo() < AppApplication.listBusLineFriendRemindModel.size()) {
                if (gPSReportStationResult.getCrossType() == 1) {
                    this.isNextCross = true;
                    this.currentCross = gPSReportStationResult.getBusLineFriendRemindModel().getFrNo();
                } else {
                    LogUtils.d("MainActivity", "automaticSwitchCurrentStation:进路口 准备播报");
                    this.isNextCross = false;
                    this.currentCross = gPSReportStationResult.getBusLineFriendRemindModel().getFrNo();
                    friendshipVoice(gPSReportStationResult.getBusLineFriendRemindModel());
                }
                assembleCrossInfoReport(3, gPSReportStationResult.getBusLineFriendRemindModel());
            }
            if (!this.isNextCross) {
                LogUtils.d("MainActivity", "automaticSwitchCurrentStation:路口限速");
                BusLineFriendRemindModel busLineFriendRemindModel = AppApplication.listBusLineFriendRemindModel.get(this.currentCross);
                if (busLineFriendRemindModel.getCrossSpeedLimit() != null && !busLineFriendRemindModel.getCrossSpeedLimit().trim().equals("")) {
                    this.tvSpeedLimit.setText(busLineFriendRemindModel.getCrossSpeedLimit().trim());
                    this.speedLimit = busLineFriendRemindModel.getCrossSpeedLimit().trim();
                    return;
                } else {
                    this.tvSpeedLimit.setText(TarConstants.VERSION_POSIX);
                    this.speedLimit = "0";
                    return;
                }
            }
            BusLineModel busLineModel = AppApplication.listBusLine.get(this.currentStation);
            if (busLineModel.getSpeedLimit() != null && !busLineModel.getSpeedLimit().trim().equals("")) {
                this.tvSpeedLimit.setText(busLineModel.getSpeedLimit().trim());
                this.speedLimit = busLineModel.getSpeedLimit().trim();
                return;
            } else {
                this.tvSpeedLimit.setText(TarConstants.VERSION_POSIX);
                this.speedLimit = "0";
                return;
            }
        }
        if (gPSReportStationResult.getBusLineModel().getBusNo() < AppApplication.listBusLine.size()) {
            if (gPSReportStationResult.getStationType() == 1) {
                this.isNextStation = true;
                this.currentStation = gPSReportStationResult.getBusLineModel().getBusNo();
                BusLineModel busLineModel2 = gPSReportStationResult.getBusLineModel();
                if (!this.isDispatchEnd) {
                    int i = this.currentStation;
                    if (i == 1) {
                        if ("是".equals(this.ttsSwitch)) {
                            systemDisposeVoice(0, busLineModel2);
                        } else {
                            disposeVoice(0, busLineModel2);
                        }
                    } else if (i >= AppApplication.listBusLine.size() - 1) {
                        if ("是".equals(this.ttsSwitch)) {
                            systemDisposeVoice(3, busLineModel2);
                        } else {
                            disposeVoice(3, busLineModel2);
                        }
                    } else if ("是".equals(this.ttsSwitch)) {
                        systemDisposeVoice(2, busLineModel2);
                    } else {
                        disposeVoice(2, busLineModel2);
                    }
                }
                this.tvHomeNextStation.setText(R.string.main_next_station);
                this.tvHomeNextStationName.setText(busLineModel2.getBusName());
                this.tvEndBusName.setText(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
            } else {
                this.isNextStation = false;
                this.currentStation = gPSReportStationResult.getBusLineModel().getBusNo();
                BusLineModel busLineModel3 = gPSReportStationResult.getBusLineModel();
                if (!this.isDispatchEnd) {
                    this.isDispatchEndRelevance = true;
                    if (this.currentStation >= AppApplication.listBusLine.size() - 1) {
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DISPATCHEND, true);
                        this.isDispatchEnd = true;
                        if ("是".equals(this.ttsSwitch)) {
                            systemDisposeVoice(4, busLineModel3);
                        } else {
                            disposeVoice(4, busLineModel3);
                        }
                    } else if (this.currentStation > 0) {
                        if ("是".equals(this.ttsSwitch)) {
                            systemDisposeVoice(1, busLineModel3);
                        } else {
                            disposeVoice(1, busLineModel3);
                        }
                    }
                }
                this.tvHomeNextStation.setText(R.string.main_home_station);
                this.tvHomeNextStationName.setText(gPSReportStationResult.getBusLineModel().getBusName());
                this.tvEndBusName.setText(AppApplication.listBusLine.get(AppApplication.listBusLine.size() - 1).getBusName());
            }
            LogUtils.d("MainActivity", "GPS自动报站 isNextStation:" + this.isNextStation + ",currentStation:" + this.currentStation);
            this.reportType = 0;
            modifyLineNO(this.currentStation);
            controlBusLine(this.currentStation, true);
            if (this.isNextStation || this.currentStation < AppApplication.listBusLine.size() - 1) {
                return;
            }
            switchDirection();
        }
    }

    private void friendshipVoice(BusLineFriendRemindModel busLineFriendRemindModel) {
        String str = busLineFriendRemindModel.getFilePath().substring(0, busLineFriendRemindModel.getFilePath().lastIndexOf("/Bus")) + "/CommonSounds/";
        stopBusVoicePlay();
        this.busVoiceList.clear();
        this.busExternalVoiceList.clear();
        if (new File(str + "Common/voiceD/" + busLineFriendRemindModel.getFrVoice() + ".mp3").exists()) {
            this.busVoiceList.add(str + "Common/voiceD/" + busLineFriendRemindModel.getFrVoice() + ".mp3");
        }
        if (((String) SPUserInfoUtils.get(this, "IfEnglish", "否")).trim().equals("是") && new File(str + "Common/voiceE/" + busLineFriendRemindModel.getFrVoice() + ".mp3").exists()) {
            this.busVoiceList.add(str + "Common/voiceE/" + busLineFriendRemindModel.getFrVoice() + ".mp3");
        }
        if (((String) SPUserInfoUtils.get(this, "IfDialect", "否")).trim().equals("是") && new File(str + "Common/voiceF/" + busLineFriendRemindModel.getFrVoice() + ".mp3").exists()) {
            this.busVoiceList.add(str + "Common/voiceF/" + busLineFriendRemindModel.getFrVoice() + ".mp3");
        }
        VoicePlayOperationParam voicePlayOperationParam = new VoicePlayOperationParam();
        voicePlayOperationParam.setOperation(0);
        initMediaPlayer(voicePlayOperationParam);
    }

    private void initTTS() {
        SystemTTS systemTTS = SystemTTS.getInstance(getApplicationContext());
        this.systemTTS = systemTTS;
        systemTTS.setCallback(this.iCallBack);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopTTS() {
        controlHorn(false, false, false, 0);
        SystemTTS systemTTS = this.systemTTS;
        if (systemTTS == null || !systemTTS.isPlaying()) {
            return;
        }
        this.systemTTS.stopSpeak();
    }

    private void closeTTS() {
        SystemTTS systemTTS = this.systemTTS;
        if (systemTTS != null) {
            systemTTS.destroy();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playReportStationTTS(String str) {
        this.istTTSMixture = false;
        stopTTS();
        controlHorn(true, true, false, 2);
        SystemTTS systemTTS = this.systemTTS;
        if (systemTTS != null) {
            systemTTS.playText(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playMixtureTTS(String str) {
        SystemTTS systemTTS = this.systemTTS;
        if (systemTTS != null) {
            systemTTS.playText(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playDispatchTTS(final String str) {
        LogUtils.d("MainActivity", "playDispatchTTS txtContent:" + str);
        this.istTTSMixture = false;
        stopTTS();
        controlHorn(false, false, true, 0);
        if (this.timerPlayer == null) {
            this.timerPlayer = new Timer();
        }
        this.timerPlayer.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.activity.MainActivity.40
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (MainActivity.this.systemTTS != null) {
                    LogUtils.d("MainActivity", "playDispatchTTS systemTTS开始");
                    MainActivity.this.systemTTS.playText(str);
                }
            }
        }, 300L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playInsideAndOutsideTTSSwitch(String str, boolean z) {
        LogUtils.d("MainActivity", "playInsideAndOutsideTTSSwitch txtContent:" + str);
        stopTTS();
        if (z) {
            controlHorn(true, false, false, 0);
        } else {
            controlHorn(false, true, false, 0);
        }
        if (this.systemTTS != null) {
            LogUtils.d("MainActivity", "playInsideAndOutsideTTSSwitch systemTTS开始");
            this.systemTTS.playText(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0069  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0072  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void assembleDriversSignInOut(int r21, java.lang.String r22) {
        /*
            Method dump skipped, instruction units count: 845
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.ui.activity.MainActivity.assembleDriversSignInOut(int, java.lang.String):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void assembleTerminalSelfCheck(int i) {
        if (getResources().getString(R.string.connected).equals(AppApplication.cmsState) && i == 3) {
            ReportInfoModel reportInfoModel = new ReportInfoModel();
            reportInfoModel.setPOSStatus("FF");
            if (GPSMonitor.bValidData) {
                reportInfoModel.setGPSDeviceStatus(TarConstants.VERSION_POSIX);
            } else {
                reportInfoModel.setGPSDeviceStatus("FF");
            }
            reportInfoModel.setLEDOutStatus("FF");
            reportInfoModel.setLEDInnerStatus("FF");
            reportInfoModel.setTransmissionType("29");
            reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
            reportInfoModel.setTerminalDate(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
            SocketManage.getInstance().sendReq(Generate808ReqPackage.generateTerminalSelfCheck(reportInfoModel));
        }
    }

    private void initRfid() {
        Log.v("MainActivity", "read rfid init");
        I2CPort i2CPort = new I2CPort();
        this.mDrvRfid = i2CPort;
        i2CPort.RfidInit();
        RfidThread rfidThread = new RfidThread();
        this.rfidThread = rfidThread;
        rfidThread.start();
    }

    private class RfidThread extends Thread {
        private byte[] rfidID;
        private boolean suspend;

        private RfidThread() {
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
                byte[] bArrRfidGetId = MainActivity.this.mDrvRfid.RfidGetId((byte) 1);
                this.rfidID = bArrRfidGetId;
                if (bArrRfidGetId[0] == 56) {
                    String strBytesToHexString = JavaUtils.bytesToHexString(bArrRfidGetId, bArrRfidGetId.length);
                    LogUtils.d("MainActivity", "read rfid success" + strBytesToHexString);
                    SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.DRIVERCARDID, strBytesToHexString);
                    if (MainActivity.this.dispatchProtocol.trim().equals("DVR")) {
                        MainActivity.this.assembleDriversSignInOut(1, strBytesToHexString);
                    } else if (!MainActivity.this.dispatchProtocol.trim().equals("ALINK")) {
                        if ((MainActivity.this.dispatchProtocol.trim().equals("808") || MainActivity.this.dispatchProtocol.trim().equals("CC808")) && SocketManage.getInstance().iHeartbeat) {
                            MainActivity.this.assembleDriversSignInOut(3, strBytesToHexString);
                        }
                    } else if (SocketManage.getInstance().iHeartbeat) {
                        MainActivity.this.assembleDriversSignInOut(2, strBytesToHexString);
                    }
                    MainActivity.this.mDrvRfid.RfidWaitCardOff();
                }
                try {
                    Thread.sleep(400L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.rfidID = null;
        }
    }

    private void closeRfid() {
        RfidThread rfidThread = this.rfidThread;
        if (rfidThread != null) {
            rfidThread.setSuspend(true);
            this.rfidThread = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void audioRecordPay(byte[] bArr) {
        if (this.audioTrack != null) {
            G711a.Result result = new G711a.Result();
            this.acceptResult = result;
            G711a.G711aDecode(bArr, result);
            this.audioTrack.write(this.acceptResult.getDataArr(), 0, this.acceptResult.getDataArr().length);
        }
    }

    private void closeService() {
        NetWorkReceiver netWorkReceiver = this.netWorkReceiver;
        if (netWorkReceiver != null) {
            unregisterReceiver(netWorkReceiver);
        }
        MsgReceiver msgReceiver = this.msgReceiver;
        if (msgReceiver != null) {
            unregisterReceiver(msgReceiver);
        }
    }

    private void clearTimer() {
        if (((String) SPUserInfoUtils.get(this, "IfIntegralPoint", "否")).trim().equals("是")) {
            unregisterReceiver(this.mTimeReceiver);
        }
    }

    private void assembleLineSwitching(BusLineModel busLineModel) {
        ReportInfoModel reportInfoModel = new ReportInfoModel();
        String str = this.firstLineNumber;
        if (str != null) {
            reportInfoModel.setFirstLineNumber(str);
            if (!this.isNextStation) {
                reportInfoModel.setFirstBusNo(this.currentStation + 1);
            } else {
                reportInfoModel.setFirstBusNo(this.currentStation);
            }
            reportInfoModel.setFirstDirection(this.firstDirection);
        } else {
            reportInfoModel.setFirstLineNumber(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, "").toString());
            reportInfoModel.setFirstBusNo(1);
            reportInfoModel.setFirstDirection(busLineModel.getDirection());
        }
        reportInfoModel.setLineNumber(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, "").toString());
        reportInfoModel.setBusNo(1);
        reportInfoModel.setDirection(busLineModel.getDirection());
        reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
        reportInfoModel.setTerminalDate(JavaUtils.dateToString(new Date(), "yyMMddHHmmss"));
        reportInfoModel.setType(this.switchType);
        SocketManage.getInstance().sendReq(Generate808ReqPackage.generateLineSwitchInfo(reportInfoModel));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpActivity
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getDataSuccess() {
        if (AppApplication.listBusLine == null || AppApplication.listBusLine.size() <= 0) {
            return;
        }
        this.gpsReportStationResult = null;
        if ((this.dispatchProtocol.trim().equals("808") || this.dispatchProtocol.trim().equals("CC808")) && SocketManage.getInstance().iHeartbeat) {
            assembleLineSwitching(AppApplication.listBusLine.get(0));
        }
        this.firstLineNumber = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINENUMBER, "").toString();
        this.firstDirection = AppApplication.listBusLine.get(0).getDirection();
        this.currentStation = 0;
        this.isNextStation = false;
        int iIntValue = ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINEATTRIBUTE, 1)).intValue();
        if (AppApplication.listBusLine.get(0).getDirection() == 1) {
            if (iIntValue != 2) {
                this.tvLineDirection.setText(R.string.upstream);
            } else {
                this.tvLineDirection.setText(R.string.basic_newspaper_attribute_loop);
            }
            SPUserInfoUtils.put(this, SPUserInfoUtils.BUSDIRECTIONNAME, AppApplication.listBusLine.get(0).getBusLineName() + "S");
        } else {
            if (iIntValue != 2) {
                this.tvLineDirection.setText(R.string.down);
            } else {
                this.tvLineDirection.setText(R.string.basic_newspaper_attribute_loop);
            }
            SPUserInfoUtils.put(this, SPUserInfoUtils.BUSDIRECTIONNAME, AppApplication.listBusLine.get(0).getBusLineName() + "X");
        }
        this.gpsOperationParam.setiInitSite(true);
        LogUtils.d("MainActivity", "getDataSuccess");
        sendSwitchLine();
        initBusView();
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void authorizationResult(int i, String str) {
        if (i == 1) {
            SPUserInfoUtils.put(this, SPUserInfoUtils.ISAUTHORIZATION, true);
            toastShow(R.string.main_dlg_author_suss);
        } else if (i == 0) {
            SPUserInfoUtils.put(this, SPUserInfoUtils.ISAUTHORIZATION, false);
            toastShow(R.string.main_dlg_author_errer_tip);
        } else {
            SPUserInfoUtils.put(this, SPUserInfoUtils.ISAUTHORIZATION, false);
            toastShow("code:" + i + ",msg:" + str);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        LogUtils.d("MainActivity", "onStop");
        Timer timer = this.gpsPortTimer;
        if (timer != null) {
            timer.cancel();
        }
        this.Running = false;
        controlHorn(false, false, false, 0);
        stopTTS();
        clearTimer();
        stopBusVoicePlay();
        closeShouting();
        closeRfid();
        releaseCloseTipTimer();
        unregisterReceiver(this.mHeadsetOnReceiver);
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("MainActivity", "onDestroy");
        releaseMediaPlayer();
        this.isIntercomRecording = false;
        closeIntercomBagThread();
        SocketIntercomManage.getInstance().closeAllSocket();
        AppApplication.jhyopd.close();
        closeFTPTCPDownloadService();
        Timer timer = this.authorTimer;
        if (timer != null) {
            timer.cancel();
        }
        Timer timer2 = this.messageTimer;
        if (timer2 != null) {
            timer2.cancel();
        }
        InitThread initThread = this.initThread;
        if (initThread != null) {
            initThread.setSuspend(true);
            try {
                this.initThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.initThread = null;
        }
        MonitorThread monitorThread = this.monitorThread;
        if (monitorThread != null) {
            monitorThread.setSuspend(true);
            try {
                this.monitorThread.join();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            this.monitorThread = null;
        }
        JHYThread jHYThread = this.jhyThread;
        if (jHYThread != null) {
            jHYThread.setSuspend(true);
            try {
                this.jhyThread.join();
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
            this.jhyThread = null;
        }
        Timer timer3 = this.gpsPortTimer;
        if (timer3 != null) {
            timer3.cancel();
        }
        this.Running = false;
        this.gpsSerialPortResultListener = null;
        this.jhySerialPortResultListener = null;
        unregisterReceiver(this.netWorkStateReceiver);
        this.telephoneyManager.listen(this.phoneSignalStateListener, 0);
        this.phoneSignalStateListener = null;
        this.telephoneyManager = null;
        closeService();
        closeTTS();
        closeShouting();
        close485SerialPort();
        closeGpsReport();
        reverseDispatchTimer();
        DVRProtocol.getInstance().close232SerialPort();
        SocketManage.getInstance().closeAllSocket();
        ((MainPresenter) this.mvpPresenter).detachView();
        this.shoutingHandle.removeCallbacksAndMessages(null);
    }

    private void checkNeedPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.SET_TIME) == 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS) == 0) {
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.SET_TIME}, 1);
        }
    }
}
