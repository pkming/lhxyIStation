package com.lianhexinye.m90.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes2.dex */
public class ConnectivityUtil {
    private static final String TAG = "ConnectivityUtil";
    private static ConnectivityUtil connectivityUtil;
    public EthernetManager ethernetManager;
    public ConnectivityManager mConnectivityManager;
    public NetworkInfo mNetworkInfo;

    public static ConnectivityUtil getSingleTon(Context context) {
        if (connectivityUtil == null) {
            connectivityUtil = new ConnectivityUtil(context);
        }
        return connectivityUtil;
    }

    public ConnectivityUtil(Context context) {
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.ethernetManager = (EthernetManager) context.getSystemService(Context.ETHERNET_SERVICE);
        this.mNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
    }

    public boolean isNetworkConnected() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    public boolean isMobileConnected() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        return networkInfo != null && networkInfo.isConnected() && this.mNetworkInfo.getType() == 0;
    }

    public void setMobileDataEnabled(boolean z) {
        try {
            Method declaredMethod = this.mConnectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            if (declaredMethod != null) {
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(this.mConnectivityManager, Boolean.valueOf(z));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
    }

    public void setEthernetTethering(boolean z) {
        try {
            Method declaredMethod = this.mConnectivityManager.getClass().getDeclaredMethod("setEthernetTethering", Boolean.TYPE);
            if (declaredMethod != null) {
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(this.mConnectivityManager, Boolean.valueOf(z));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
    }

    public void setEthernetEnabled(boolean z) {
        try {
            Method method = this.ethernetManager.getClass().getMethod("setEthernetEnabled", Boolean.TYPE);
            if (method != null) {
                method.setAccessible(true);
                method.invoke(this.ethernetManager, Boolean.valueOf(z));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
    }

    public String getEthernetState() {
        try {
            Method method = this.ethernetManager.getClass().getMethod("getEthernetState", new Class[0]);
            if (method == null) {
                return "0";
            }
            method.setAccessible(true);
            return String.valueOf(method.invoke(this.ethernetManager, new Object[0]));
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}
