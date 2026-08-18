package com.lianhexinye.m90.ui.fragment.dispatch;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.iicport.I2CPort;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.gpio.GpioOperation;
import com.lianhexinye.m90.mvp.MvpFragment;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.tts.SystemTTS;
import com.lianhexinye.m90.ui.activity.DispatchCenterActivity;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes2.dex */
public class PaycardTestFragment extends MvpFragment<MainView, MainPresenter> implements MainView {
    private AudioManager audioManager;

    @BindView(R.id.butBroadcastTest)
    Button butBroadcastTest;
    private String cardId;
    private DispatchCenterActivity dispatchCenterActivity;
    private I2CPort mDrvRfid;
    private RfidThread rfidThread;
    private SystemTTS systemTTS;

    @BindView(R.id.tvPaycard)
    TextView tvPaycard;
    private final String TAG = "PaycardTestFragment";
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
        Log.d("PaycardTestFragment", "onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.f_paycard_test, viewGroup, false);
        ButterKnife.bind(this, viewInflate);
        Log.d("PaycardTestFragment", "onCreateView()");
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
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.MvpFragment
    public void initView() {
        this.audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        this.tvPaycard.setText("NO cardID......");
        initRfid();
        initTTS();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    private void initRfid() {
        Log.v("PaycardTestFragment", "read rfid init");
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
                byte[] bArrRfidGetId = PaycardTestFragment.this.mDrvRfid.RfidGetId((byte) 1);
                this.rfidID = bArrRfidGetId;
                if (bArrRfidGetId[0] == 56) {
                    PaycardTestFragment.this.cardId = JavaUtils.bytesToHexString(bArrRfidGetId, bArrRfidGetId.length);
                    LogUtils.v("PaycardTestFragment", "read rfid success" + PaycardTestFragment.this.cardId);
                    PaycardTestFragment.this.setTvPaycard();
                    PaycardTestFragment.this.mDrvRfid.RfidWaitCardOff();
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

    /* JADX INFO: Access modifiers changed from: private */
    public void setTvPaycard() {
        getActivity().runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.PaycardTestFragment.1
            @Override // java.lang.Runnable
            public void run() {
                PaycardTestFragment.this.tvPaycard.setText(PaycardTestFragment.this.cardId);
                PaycardTestFragment.this.toastShow("setTvPaycard:" + PaycardTestFragment.this.cardId);
            }
        });
    }

    private void closeRfid() {
        RfidThread rfidThread = this.rfidThread;
        if (rfidThread != null) {
            rfidThread.setSuspend(true);
            this.rfidThread = null;
        }
    }

    @OnClick({R.id.butBroadcastTest})
    public void onViewClicked(View view) {
        if (view.getId() != R.id.butBroadcastTest) {
            return;
        }
        if (!JavaUtils.isEmpty(this.cardId)) {
            playTTSTest(this.cardId);
        } else {
            toastShow("no cardID~");
        }
    }

    private void playTTSTest(final String str) {
        stopTTS();
        controlHorn(false, false, true, 0);
        SystemTTS systemTTS = this.systemTTS;
        if (systemTTS != null) {
            systemTTS.playText(str);
        }
        Timer timer = this.cardTestTimer;
        if (timer != null) {
            timer.cancel();
        }
        Timer timer2 = new Timer();
        this.cardTestTimer = timer2;
        timer2.schedule(new TimerTask() { // from class: com.lianhexinye.m90.ui.fragment.dispatch.PaycardTestFragment.2
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                int iIntValue = Integer.valueOf((String) SPUserInfoUtils.get(AppApplication.getContext(), "DispatchVolume", "7")).intValue();
                PaycardTestFragment.this.audioManager.setStreamVolume(3, iIntValue, 0);
                LogUtils.d("controlHorn", "iTTS dispatchVolume:" + iIntValue);
                if (PaycardTestFragment.this.systemTTS != null) {
                    LogUtils.d("PaycardTestFragment", "playDispatchTTS systemTTS开始");
                    PaycardTestFragment.this.systemTTS.playText(str);
                }
            }
        }, FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
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

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
    }
}
