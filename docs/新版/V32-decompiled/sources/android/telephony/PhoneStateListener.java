package android.telephony;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.android.internal.telephony.IPhoneStateListener;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PhoneStateListener {
    public static final int LISTEN_CALL_FORWARDING_INDICATOR = 8;
    public static final int LISTEN_CALL_STATE = 32;
    public static final int LISTEN_CELL_INFO = 1024;
    public static final int LISTEN_CELL_LOCATION = 16;
    public static final int LISTEN_DATA_ACTIVITY = 128;
    public static final int LISTEN_DATA_CONNECTION_STATE = 64;
    public static final int LISTEN_MESSAGE_WAITING_INDICATOR = 4;
    public static final int LISTEN_NONE = 0;
    public static final int LISTEN_OTASP_CHANGED = 512;
    public static final int LISTEN_SERVICE_STATE = 1;

    @Deprecated
    public static final int LISTEN_SIGNAL_STRENGTH = 2;
    public static final int LISTEN_SIGNAL_STRENGTHS = 256;
    IPhoneStateListener callback = new IPhoneStateListener.Stub() { // from class: android.telephony.PhoneStateListener.1
        public void onServiceStateChanged(ServiceState serviceState) {
            Message.obtain(PhoneStateListener.this.mHandler, 1, 0, 0, serviceState).sendToTarget();
        }

        public void onSignalStrengthChanged(int i) {
            Message.obtain(PhoneStateListener.this.mHandler, 2, i, 0, null).sendToTarget();
        }

        public void onMessageWaitingIndicatorChanged(boolean z) {
            Message.obtain(PhoneStateListener.this.mHandler, 4, z ? 1 : 0, 0, null).sendToTarget();
        }

        public void onCallForwardingIndicatorChanged(boolean z) {
            Message.obtain(PhoneStateListener.this.mHandler, 8, z ? 1 : 0, 0, null).sendToTarget();
        }

        public void onCellLocationChanged(Bundle bundle) {
            Message.obtain(PhoneStateListener.this.mHandler, 16, 0, 0, CellLocation.newFromBundle(bundle)).sendToTarget();
        }

        public void onCallStateChanged(int i, String str) {
            Message.obtain(PhoneStateListener.this.mHandler, 32, i, 0, str).sendToTarget();
        }

        public void onDataConnectionStateChanged(int i, int i2) {
            Message.obtain(PhoneStateListener.this.mHandler, 64, i, i2).sendToTarget();
        }

        public void onDataActivity(int i) {
            Message.obtain(PhoneStateListener.this.mHandler, 128, i, 0, null).sendToTarget();
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Message.obtain(PhoneStateListener.this.mHandler, 256, 0, 0, signalStrength).sendToTarget();
        }

        public void onOtaspChanged(int i) {
            Message.obtain(PhoneStateListener.this.mHandler, 512, i, 0).sendToTarget();
        }

        public void onCellInfoChanged(List<CellInfo> list) {
            Message.obtain(PhoneStateListener.this.mHandler, 1024, 0, 0, list).sendToTarget();
        }
    };
    Handler mHandler = new Handler() { // from class: android.telephony.PhoneStateListener.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                PhoneStateListener.this.onServiceStateChanged((ServiceState) message.obj);
                return;
            }
            if (i == 2) {
                PhoneStateListener.this.onSignalStrengthChanged(message.arg1);
                return;
            }
            if (i == 4) {
                PhoneStateListener.this.onMessageWaitingIndicatorChanged(message.arg1 != 0);
                return;
            }
            if (i == 8) {
                PhoneStateListener.this.onCallForwardingIndicatorChanged(message.arg1 != 0);
                return;
            }
            if (i == 16) {
                PhoneStateListener.this.onCellLocationChanged((CellLocation) message.obj);
                return;
            }
            if (i == 32) {
                PhoneStateListener.this.onCallStateChanged(message.arg1, (String) message.obj);
                return;
            }
            if (i == 64) {
                PhoneStateListener.this.onDataConnectionStateChanged(message.arg1, message.arg2);
                PhoneStateListener.this.onDataConnectionStateChanged(message.arg1);
                return;
            }
            if (i == 128) {
                PhoneStateListener.this.onDataActivity(message.arg1);
                return;
            }
            if (i == 256) {
                PhoneStateListener.this.onSignalStrengthsChanged((SignalStrength) message.obj);
            } else if (i == 512) {
                PhoneStateListener.this.onOtaspChanged(message.arg1);
            } else {
                if (i != 1024) {
                    return;
                }
                PhoneStateListener.this.onCellInfoChanged((List) message.obj);
            }
        }
    };

    public void onCallForwardingIndicatorChanged(boolean z) {
    }

    public void onCallStateChanged(int i, String str) {
    }

    public void onCellInfoChanged(List<CellInfo> list) {
    }

    public void onCellLocationChanged(CellLocation cellLocation) {
    }

    public void onDataActivity(int i) {
    }

    public void onDataConnectionStateChanged(int i) {
    }

    public void onDataConnectionStateChanged(int i, int i2) {
    }

    public void onMessageWaitingIndicatorChanged(boolean z) {
    }

    public void onOtaspChanged(int i) {
    }

    public void onServiceStateChanged(ServiceState serviceState) {
    }

    @Deprecated
    public void onSignalStrengthChanged(int i) {
    }

    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
    }
}
