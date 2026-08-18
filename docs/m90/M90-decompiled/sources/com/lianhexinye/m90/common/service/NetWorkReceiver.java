package com.lianhexinye.m90.common.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class NetWorkReceiver extends BroadcastReceiver {
    private LanConnectionStatus lanConnectionStatus;

    public interface LanConnectionStatus {
        void setText(String str);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d("NetWorkReceiver", "Time:" + System.currentTimeMillis() + ",action:" + action);
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE") || action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (AndroidUtils.isNetworkAvailable() == 1) {
                this.lanConnectionStatus.setText(context.getResources().getString(R.string.connected));
            } else {
                this.lanConnectionStatus.setText(context.getResources().getString(R.string.unconnected));
            }
        }
    }

    public void setLanConnectionStatus(LanConnectionStatus lanConnectionStatus) {
        this.lanConnectionStatus = lanConnectionStatus;
    }
}
