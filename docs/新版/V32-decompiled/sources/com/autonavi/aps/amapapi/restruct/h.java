package com.autonavi.aps.amapapi.restruct;

import android.net.wifi.WifiInfo;

/* JADX INFO: compiled from: SystemApiWrapper.java */
/* JADX INFO: loaded from: classes2.dex */
public final class h {
    public static String a(WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return null;
        }
        return wifiInfo.getBSSID();
    }

    public static String b(WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return null;
        }
        return wifiInfo.getSSID();
    }

    public static int c(WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return -1;
        }
        return wifiInfo.getRssi();
    }
}
