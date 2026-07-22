package com.lianhexinye.m90.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.lianhexinye.m90.AppApplication;

/* JADX INFO: loaded from: classes2.dex */
public class LiveNetworkMonitor {
    private static final Context applicationContext = AppApplication.getContext().getApplicationContext();

    public static boolean isConnected() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
