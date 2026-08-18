package com.lianhexinye.m90.common.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.log.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class NetWorkStateReceiver extends BroadcastReceiver {
    private wifiUpdateActivityUI updateActivityUI;

    public interface wifiUpdateActivityUI {
        void updateUI(String str);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int rssi = ((WifiManager) context.getApplicationContext().getSystemService("wifi")).getConnectionInfo().getRssi();
        LogUtils.d("NetWorkStateReceiver", "Time:" + System.currentTimeMillis() + ",Wifi:" + rssi);
        if (rssi > -50 && rssi < 0) {
            this.updateActivityUI.updateUI(AppApplication.getContext().getResources().getString(R.string.wtrongest));
            return;
        }
        if (rssi > -70 && rssi < -50) {
            this.updateActivityUI.updateUI(AppApplication.getContext().getResources().getString(R.string.stronger));
            return;
        }
        if (rssi > -80 && rssi < -70) {
            this.updateActivityUI.updateUI(AppApplication.getContext().getResources().getString(R.string.weaker));
        } else if (rssi > -100 && rssi < -80) {
            this.updateActivityUI.updateUI(AppApplication.getContext().getResources().getString(R.string.weak));
        } else {
            this.updateActivityUI.updateUI(AppApplication.getContext().getResources().getString(R.string.unconnected));
        }
    }

    public void setUpdateActivityUI(wifiUpdateActivityUI wifiupdateactivityui) {
        this.updateActivityUI = wifiupdateactivityui;
    }
}
