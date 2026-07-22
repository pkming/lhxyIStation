package com.lianhexinye.m90.common.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class GSMPhoneStatListener extends PhoneStateListener {
    private GSMUpdateActivityUI updateActivityUI;

    public interface GSMUpdateActivityUI {
        void updateUI(String str);
    }

    @Override // android.telephony.PhoneStateListener
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        int gsmSignalStrength = (signalStrength.getGsmSignalStrength() * 2) - 113;
        ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        LogUtils.d("GSMPhoneStatListener", "Time:" + System.currentTimeMillis() + ",dbm:" + gsmSignalStrength);
        if (connectivityManager == null) {
            GSMUpdateActivityUI gSMUpdateActivityUI = this.updateActivityUI;
            if (gSMUpdateActivityUI != null) {
                gSMUpdateActivityUI.updateUI(AppApplication.getContext().getResources().getString(R.string.nosignal));
                return;
            }
            return;
        }
        if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable() || !AndroidUtils.isMobileEnableReflex()) {
            GSMUpdateActivityUI gSMUpdateActivityUI2 = this.updateActivityUI;
            if (gSMUpdateActivityUI2 != null) {
                gSMUpdateActivityUI2.updateUI(AppApplication.getContext().getResources().getString(R.string.nosignal));
                return;
            }
            return;
        }
        if (gsmSignalStrength <= 0 && gsmSignalStrength >= -50) {
            GSMUpdateActivityUI gSMUpdateActivityUI3 = this.updateActivityUI;
            if (gSMUpdateActivityUI3 != null) {
                gSMUpdateActivityUI3.updateUI(AppApplication.getContext().getResources().getString(R.string.wtrongest));
                return;
            }
            return;
        }
        if (gsmSignalStrength < -50 && gsmSignalStrength >= -70) {
            GSMUpdateActivityUI gSMUpdateActivityUI4 = this.updateActivityUI;
            if (gSMUpdateActivityUI4 != null) {
                gSMUpdateActivityUI4.updateUI(AppApplication.getContext().getResources().getString(R.string.stronger));
                return;
            }
            return;
        }
        if (gsmSignalStrength < -70 && gsmSignalStrength >= -80) {
            GSMUpdateActivityUI gSMUpdateActivityUI5 = this.updateActivityUI;
            if (gSMUpdateActivityUI5 != null) {
                gSMUpdateActivityUI5.updateUI(AppApplication.getContext().getResources().getString(R.string.weaker));
                return;
            }
            return;
        }
        if (gsmSignalStrength < -80 && gsmSignalStrength >= -100) {
            GSMUpdateActivityUI gSMUpdateActivityUI6 = this.updateActivityUI;
            if (gSMUpdateActivityUI6 != null) {
                gSMUpdateActivityUI6.updateUI(AppApplication.getContext().getResources().getString(R.string.weak));
                return;
            }
            return;
        }
        GSMUpdateActivityUI gSMUpdateActivityUI7 = this.updateActivityUI;
        if (gSMUpdateActivityUI7 != null) {
            gSMUpdateActivityUI7.updateUI(AppApplication.getContext().getResources().getString(R.string.nosignal));
        }
    }

    public void setUpdateActivityUI(GSMUpdateActivityUI gSMUpdateActivityUI) {
        this.updateActivityUI = gSMUpdateActivityUI;
    }
}
