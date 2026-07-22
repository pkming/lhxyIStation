package android.net;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;

/* JADX INFO: loaded from: classes.dex */
public class DhcpStateMachine extends StateMachine {
    private static final String ACTION_DHCP_RENEW = "android.net.wifi.DHCP_RENEW";
    private static final int BASE = 196608;
    public static final int CMD_ON_QUIT = 196614;
    public static final int CMD_POST_DHCP_ACTION = 196613;
    public static final int CMD_PRE_DHCP_ACTION = 196612;
    public static final int CMD_PRE_DHCP_ACTION_COMPLETE = 196615;
    public static final int CMD_RENEW_DHCP = 196611;
    public static final int CMD_START_DHCP = 196609;
    public static final int CMD_STOP_DHCP = 196610;
    private static final boolean DBG = false;
    public static final int DHCP_FAILURE = 2;
    private static final int DHCP_RENEW = 0;
    public static final int DHCP_SUCCESS = 1;
    private static final int MIN_RENEWAL_TIME_SECS = 300;
    private static final String TAG = "DhcpStateMachine";
    private static final String WAKELOCK_TAG = "DHCP";
    private AlarmManager mAlarmManager;
    private BroadcastReceiver mBroadcastReceiver;
    private Context mContext;
    private StateMachine mController;
    private State mDefaultState;
    private PowerManager.WakeLock mDhcpRenewWakeLock;
    private PendingIntent mDhcpRenewalIntent;
    private DhcpResults mDhcpResults;
    private final String mInterfaceName;
    private boolean mRegisteredForPreDhcpNotification;
    private State mRunningState;
    private State mStoppedState;
    private State mWaitBeforeRenewalState;
    private State mWaitBeforeStartState;

    private enum DhcpAction {
        START,
        RENEW
    }

    private DhcpStateMachine(Context context, StateMachine stateMachine, String str) {
        super(TAG);
        this.mRegisteredForPreDhcpNotification = false;
        this.mDefaultState = new DefaultState();
        this.mStoppedState = new StoppedState();
        this.mWaitBeforeStartState = new WaitBeforeStartState();
        this.mRunningState = new RunningState();
        this.mWaitBeforeRenewalState = new WaitBeforeRenewalState();
        this.mContext = context;
        this.mController = stateMachine;
        this.mInterfaceName = str;
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        this.mDhcpRenewalIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_DHCP_RENEW, (Uri) null), 0);
        PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE)).newWakeLock(1, WAKELOCK_TAG);
        this.mDhcpRenewWakeLock = wakeLockNewWakeLock;
        wakeLockNewWakeLock.setReferenceCounted(false);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: android.net.DhcpStateMachine.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                DhcpStateMachine.this.mDhcpRenewWakeLock.acquire(40000L);
                DhcpStateMachine.this.sendMessage(DhcpStateMachine.CMD_RENEW_DHCP);
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mContext.registerReceiver(broadcastReceiver, new IntentFilter(ACTION_DHCP_RENEW));
        addState(this.mDefaultState);
        addState(this.mStoppedState, this.mDefaultState);
        addState(this.mWaitBeforeStartState, this.mDefaultState);
        addState(this.mRunningState, this.mDefaultState);
        addState(this.mWaitBeforeRenewalState, this.mDefaultState);
        setInitialState(this.mStoppedState);
    }

    public static DhcpStateMachine makeDhcpStateMachine(Context context, StateMachine stateMachine, String str) {
        DhcpStateMachine dhcpStateMachine = new DhcpStateMachine(context, stateMachine, str);
        dhcpStateMachine.start();
        return dhcpStateMachine;
    }

    public void registerForPreDhcpNotification() {
        this.mRegisteredForPreDhcpNotification = true;
    }

    public void doQuit() {
        quit();
    }

    protected void onQuitting() {
        this.mController.sendMessage(CMD_ON_QUIT);
    }

    class DefaultState extends State {
        DefaultState() {
        }

        public void exit() {
            DhcpStateMachine.this.mContext.unregisterReceiver(DhcpStateMachine.this.mBroadcastReceiver);
        }

        public boolean processMessage(Message message) {
            if (message.what == 196611) {
                Log.e(DhcpStateMachine.TAG, "Error! Failed to handle a DHCP renewal on " + DhcpStateMachine.this.mInterfaceName);
                DhcpStateMachine.this.mDhcpRenewWakeLock.release();
                return true;
            }
            Log.e(DhcpStateMachine.TAG, "Error! unhandled message  " + message);
            return true;
        }
    }

    class StoppedState extends State {
        public void enter() {
        }

        StoppedState() {
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case DhcpStateMachine.CMD_START_DHCP /* 196609 */:
                    if (DhcpStateMachine.this.mRegisteredForPreDhcpNotification) {
                        DhcpStateMachine.this.mController.sendMessage(DhcpStateMachine.CMD_PRE_DHCP_ACTION);
                        DhcpStateMachine dhcpStateMachine = DhcpStateMachine.this;
                        dhcpStateMachine.transitionTo(dhcpStateMachine.mWaitBeforeStartState);
                        break;
                    } else {
                        if (DhcpStateMachine.this.runDhcp(DhcpAction.START)) {
                            DhcpStateMachine dhcpStateMachine2 = DhcpStateMachine.this;
                            dhcpStateMachine2.transitionTo(dhcpStateMachine2.mRunningState);
                        }
                        break;
                    }
                case DhcpStateMachine.CMD_STOP_DHCP /* 196610 */:
                    return true;
                default:
                    return false;
            }
        }
    }

    class WaitBeforeStartState extends State {
        public void enter() {
        }

        WaitBeforeStartState() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public boolean processMessage(Message message) {
            switch (message.what) {
                case DhcpStateMachine.CMD_START_DHCP /* 196609 */:
                    return true;
                case DhcpStateMachine.CMD_STOP_DHCP /* 196610 */:
                    DhcpStateMachine dhcpStateMachine = DhcpStateMachine.this;
                    dhcpStateMachine.transitionTo(dhcpStateMachine.mStoppedState);
                    return true;
                case DhcpStateMachine.CMD_PRE_DHCP_ACTION_COMPLETE /* 196615 */:
                    if (DhcpStateMachine.this.runDhcp(DhcpAction.START)) {
                        DhcpStateMachine dhcpStateMachine2 = DhcpStateMachine.this;
                        dhcpStateMachine2.transitionTo(dhcpStateMachine2.mRunningState);
                    } else {
                        DhcpStateMachine dhcpStateMachine3 = DhcpStateMachine.this;
                        dhcpStateMachine3.transitionTo(dhcpStateMachine3.mStoppedState);
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    class RunningState extends State {
        public void enter() {
        }

        RunningState() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public boolean processMessage(Message message) {
            switch (message.what) {
                case DhcpStateMachine.CMD_START_DHCP /* 196609 */:
                    return true;
                case DhcpStateMachine.CMD_STOP_DHCP /* 196610 */:
                    DhcpStateMachine.this.mAlarmManager.cancel(DhcpStateMachine.this.mDhcpRenewalIntent);
                    if (!NetworkUtils.stopDhcp(DhcpStateMachine.this.mInterfaceName)) {
                        Log.e(DhcpStateMachine.TAG, "Failed to stop Dhcp on " + DhcpStateMachine.this.mInterfaceName);
                    }
                    DhcpStateMachine dhcpStateMachine = DhcpStateMachine.this;
                    dhcpStateMachine.transitionTo(dhcpStateMachine.mStoppedState);
                    return true;
                case DhcpStateMachine.CMD_RENEW_DHCP /* 196611 */:
                    if (DhcpStateMachine.this.mRegisteredForPreDhcpNotification) {
                        DhcpStateMachine.this.mController.sendMessage(DhcpStateMachine.CMD_PRE_DHCP_ACTION);
                        DhcpStateMachine dhcpStateMachine2 = DhcpStateMachine.this;
                        dhcpStateMachine2.transitionTo(dhcpStateMachine2.mWaitBeforeRenewalState);
                    } else {
                        if (!DhcpStateMachine.this.runDhcp(DhcpAction.RENEW)) {
                            DhcpStateMachine dhcpStateMachine3 = DhcpStateMachine.this;
                            dhcpStateMachine3.transitionTo(dhcpStateMachine3.mStoppedState);
                        }
                        DhcpStateMachine.this.mDhcpRenewWakeLock.release();
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    class WaitBeforeRenewalState extends State {
        public void enter() {
        }

        WaitBeforeRenewalState() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public boolean processMessage(Message message) {
            switch (message.what) {
                case DhcpStateMachine.CMD_START_DHCP /* 196609 */:
                    return true;
                case DhcpStateMachine.CMD_STOP_DHCP /* 196610 */:
                    DhcpStateMachine.this.mAlarmManager.cancel(DhcpStateMachine.this.mDhcpRenewalIntent);
                    if (!NetworkUtils.stopDhcp(DhcpStateMachine.this.mInterfaceName)) {
                        Log.e(DhcpStateMachine.TAG, "Failed to stop Dhcp on " + DhcpStateMachine.this.mInterfaceName);
                    }
                    DhcpStateMachine dhcpStateMachine = DhcpStateMachine.this;
                    dhcpStateMachine.transitionTo(dhcpStateMachine.mStoppedState);
                    return true;
                case DhcpStateMachine.CMD_PRE_DHCP_ACTION_COMPLETE /* 196615 */:
                    if (DhcpStateMachine.this.runDhcp(DhcpAction.RENEW)) {
                        DhcpStateMachine dhcpStateMachine2 = DhcpStateMachine.this;
                        dhcpStateMachine2.transitionTo(dhcpStateMachine2.mRunningState);
                    } else {
                        DhcpStateMachine dhcpStateMachine3 = DhcpStateMachine.this;
                        dhcpStateMachine3.transitionTo(dhcpStateMachine3.mStoppedState);
                    }
                    return true;
                default:
                    return false;
            }
        }

        public void exit() {
            DhcpStateMachine.this.mDhcpRenewWakeLock.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean runDhcp(DhcpAction dhcpAction) {
        boolean zRunDhcpRenew;
        DhcpResults dhcpResults = new DhcpResults();
        if (dhcpAction == DhcpAction.START) {
            NetworkUtils.stopDhcp(this.mInterfaceName);
            zRunDhcpRenew = NetworkUtils.runDhcp(this.mInterfaceName, dhcpResults);
        } else if (dhcpAction == DhcpAction.RENEW) {
            zRunDhcpRenew = NetworkUtils.runDhcpRenew(this.mInterfaceName, dhcpResults);
            if (zRunDhcpRenew) {
                dhcpResults.updateFromDhcpRequest(this.mDhcpResults);
            }
        } else {
            zRunDhcpRenew = false;
        }
        if (zRunDhcpRenew) {
            long j = dhcpResults.leaseDuration;
            if (j >= 0) {
                if (j < 300) {
                    j = 300;
                }
                this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + (j * 480), this.mDhcpRenewalIntent);
            }
            this.mDhcpResults = dhcpResults;
            this.mController.obtainMessage(CMD_POST_DHCP_ACTION, 1, 0, dhcpResults).sendToTarget();
        } else {
            Log.e(TAG, "DHCP failed on " + this.mInterfaceName + ": " + NetworkUtils.getDhcpError());
            NetworkUtils.stopDhcp(this.mInterfaceName);
            this.mController.obtainMessage(CMD_POST_DHCP_ACTION, 2, 0).sendToTarget();
        }
        return zRunDhcpRenew;
    }
}
