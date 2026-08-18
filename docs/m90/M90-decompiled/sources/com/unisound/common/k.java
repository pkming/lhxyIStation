package com.unisound.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.UUID;

/* JADX INFO: loaded from: classes2.dex */
public class k {
    private static String A = "";
    public static final String a = "PN";
    public static final String b = "OS";
    public static final String c = "CR";
    public static final String d = "NT";
    public static final String e = "MD";
    public static final String f = "SV";
    public static final String g = "SID";
    public static final String h = "RPT";
    public static final String i = "EC";
    public static final String j = "NPT";
    public static final String k = "IP";
    public static final int l = 0;
    public static final int m = 1;
    public static final int n = 2;
    public static final int o = 3;
    public static final int p = 4;
    public static String q = "";
    public static String r = "";
    public static String s = "";
    public static String t = "";
    public static TelephonyManager u = null;
    public static NetworkInfo v = null;
    public static ConnectivityManager w = null;
    public static String x = "";
    private static final String y = "000000000000000";
    private static boolean z = false;

    public static String a() {
        return Build.MODEL;
    }

    private static String a(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b2 : bArr) {
            String hexString = Integer.toHexString(b2 & 255);
            if (hexString.length() == 1) {
                sb.append("0");
            }
            sb.append(hexString.toUpperCase());
        }
        return sb.toString();
    }

    public static void a(Context context) {
        if (z) {
            return;
        }
        w = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        u = (TelephonyManager) context.getSystemService("phone");
        ConnectivityManager connectivityManager = w;
        if (connectivityManager != null) {
            v = connectivityManager.getNetworkInfo(0);
        }
        s = context.getPackageName();
        r = d(context);
        q = c(context);
        t = Build.MODEL;
        z = true;
        x = b(context);
    }

    private static void a(String str) throws Throwable {
        RandomAccessFile randomAccessFile = null;
        try {
            try {
                String strE = e();
                File file = new File(strE);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File file2 = new File(strE + File.separator + A);
                if (!file2.exists()) {
                    file2.createNewFile();
                }
                RandomAccessFile randomAccessFile2 = new RandomAccessFile(strE + File.separator + A, "rw");
                try {
                    randomAccessFile2.write(("UDID=" + str).getBytes());
                    randomAccessFile2.close();
                } catch (Exception unused) {
                    randomAccessFile = randomAccessFile2;
                    if (randomAccessFile == null) {
                    } else {
                        randomAccessFile.close();
                    }
                } catch (Throwable th) {
                    th = th;
                    randomAccessFile = randomAccessFile2;
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        } catch (Exception unused2) {
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static int b() {
        NetworkInfo networkInfo = w.getNetworkInfo(1);
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return c();
        }
        return 1;
    }

    public static String b(Context context) throws Throwable {
        f(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(A, 0);
        String string = sharedPreferences.getString("UDID", "");
        if (!string.equals("")) {
            r.b("DeviceInfoUtil getUDID from sharedPreferences= " + string);
            a(string);
            return string;
        }
        String strD = d();
        if (!strD.equals("")) {
            r.b("DeviceInfoUtil getUDID from sdcard= " + strD);
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            editorEdit.putString("UDID", strD);
            editorEdit.commit();
            return strD;
        }
        String string2 = UUID.randomUUID().toString();
        SharedPreferences.Editor editorEdit2 = sharedPreferences.edit();
        editorEdit2.putString("UDID", string2);
        editorEdit2.commit();
        a(string2);
        r.b("DeviceInfoUtil first getUDID= " + string2);
        return string2;
    }

    private static String b(String str) {
        try {
            return a(MessageDigest.getInstance("SHA-1").digest(str.getBytes("UTF-8")));
        } catch (Exception unused) {
            return null;
        }
    }

    public static int c() {
        NetworkInfo networkInfo = v;
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return 0;
        }
        switch (u.getNetworkType()) {
            case 1:
            case 2:
            case 4:
                return 3;
            case 3:
            case 5:
            case 6:
            case 8:
                return 2;
            case 7:
            default:
                return 4;
        }
    }

    public static String c(Context context) {
        String deviceId = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        if (deviceId != null && !"".equals(deviceId) && !deviceId.equals(y)) {
            return deviceId;
        }
        String string = Settings.Secure.getString(context.getContentResolver(), "android_id");
        if (string != null && !"".equals(string) && !string.equals(y)) {
            return string;
        }
        WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        return connectionInfo != null ? connectionInfo.getMacAddress() : "unknow android device";
    }

    private static String d() {
        try {
            return new RandomAccessFile(e() + File.separator + A, "rw").readLine().split("=")[1];
        } catch (Exception unused) {
            return "";
        }
    }

    public static String d(Context context) {
        String networkOperator = ((TelephonyManager) context.getSystemService("phone")).getNetworkOperator();
        return (networkOperator == null || "".equals(networkOperator)) ? "0" : networkOperator;
    }

    private static String e() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getPath() + File.separator + "unisound/sdk" : "/mnt/sdcard/unisound/sdk";
    }

    public static boolean e(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
    }

    private static String f() {
        InputStream inputStream;
        String strTrim = "00000000000000000000000000000000";
        try {
            Process processExec = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            if (processExec != null && (inputStream = processExec.getInputStream()) != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                loop0: while (true) {
                    String line = bufferedReader.readLine();
                    while (true) {
                        if (line != null) {
                            if (line.indexOf("Serial") > -1) {
                                if (line.length() > 1 && line.contains(":")) {
                                    strTrim = line.substring(line.indexOf(":") + 1, line.length()).trim();
                                    break loop0;
                                }
                            }
                        } else {
                            break loop0;
                        }
                    }
                }
                bufferedReader.close();
                inputStream.close();
            }
        } catch (IOException unused) {
        }
        return strTrim;
    }

    private static String f(Context context) {
        if (A.equals("")) {
            A = b(c(context) + f());
        }
        return A;
    }
}
