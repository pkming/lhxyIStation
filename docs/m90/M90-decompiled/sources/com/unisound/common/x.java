package com.unisound.common;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/* JADX INFO: loaded from: classes2.dex */
public class x {
    public static final String a = "Network";
    public static final String b = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String c = "cn.yunzhisheng.intent.action.CONNECTIVITY_CHANGE";
    public static final String d = "TYPE_NULL_POINT";
    public static final String e = "TYPE_UNCONNECT";
    public static final String f = "TYPE_WIFI";
    public static final String g = "TYPE_GPRS";
    private static final int h = 0;
    private static final int i = 1;
    private static int j = 1;

    private static String a(int i2) {
        return (i2 & 255) + "." + ((i2 >> 8) & 255) + "." + ((i2 >> 16) & 255) + "." + ((i2 >> 24) & 255);
    }

    public static String a(Context context) {
        int ipAddress = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getIpAddress();
        return ipAddress == 0 ? b() : a(ipAddress);
    }

    public static boolean a() {
        return j > 0;
    }

    public static String b() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddressNextElement = inetAddresses.nextElement();
                    if (!inetAddressNextElement.isLoopbackAddress() && (inetAddressNextElement instanceof Inet4Address)) {
                        return inetAddressNextElement.getHostAddress().toString();
                    }
                }
            }
            return "0.0.0.0";
        } catch (Exception e2) {
            e2.printStackTrace();
            return "0.0.0.0";
        }
    }

    public static boolean b(Context context) {
        int i2 = j;
        if (c(context)) {
            j = 1;
        } else {
            j = 0;
        }
        if (i2 != j && context != null) {
            context.sendBroadcast(new Intent(c));
        }
        return j > 0;
    }

    public static boolean c(Context context) {
        NetworkInfo activeNetworkInfo;
        return (context == null || (activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo()) == null || !activeNetworkInfo.isConnected()) ? false : true;
    }

    public static String d(Context context) {
        NetworkInfo activeNetworkInfo;
        String str;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) {
            return d;
        }
        if (!activeNetworkInfo.isConnected()) {
            return e;
        }
        int type = activeNetworkInfo.getType();
        if (type == 0) {
            str = g;
        } else {
            if (type != 1) {
                return d;
            }
            str = f;
        }
        return str;
    }
}
