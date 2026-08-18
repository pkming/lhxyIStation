package com.lianhexinye.m90.common.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.DropBoxManager;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.unisound.common.r;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidUtils {
    private static double EARTH_RADIUS = 6378.137d;
    private static int NET_ETHERNET = 1;
    private static int NET_NOCONNECT = 0;
    private static int NET_WIFI = 2;

    private static double rad(double d) {
        return (d * 3.141592653589793d) / 180.0d;
    }

    public static double getDistance(double d, double d2, double d3, double d4) {
        double dRad = rad(d2);
        double dRad2 = rad(d4);
        return (Math.round(((Math.asin(Math.sqrt(Math.pow(Math.sin((dRad - dRad2) / 2.0d), 2.0d) + ((Math.cos(dRad) * Math.cos(dRad2)) * Math.pow(Math.sin((rad(d) - rad(d3)) / 2.0d), 2.0d)))) * 2.0d) * EARTH_RADIUS) * 10000.0d) / 10000.0d) * 1000.0d;
    }

    public static String[] getPrimaryStoragePath() {
        try {
            Context context = AppApplication.getContext();
            AppApplication.getContext();
            return (String[]) StorageManager.class.getMethod("getVolumePaths", new Class[0]).invoke((StorageManager) context.getSystemService(Context.STORAGE_SERVICE), new Object[0]);
        } catch (Exception unused) {
            return null;
        }
    }

    public static String checkSD(String[] strArr, String str, String str2, boolean z) {
        if (strArr == null || strArr.length <= 0) {
            return null;
        }
        for (String str3 : strArr) {
            if (str3 != null && "/storage/emulated" != str3.trim() && "/storage/self" != str3.trim()) {
                String str4 = str3 + str;
                FileFilter fileFilter = new FileFilter(str2);
                String strCheckResourcesFirstFile = checkResourcesFirstFile(str4, fileFilter, z);
                if (strCheckResourcesFirstFile != null && !strCheckResourcesFirstFile.trim().equals("")) {
                    return strCheckResourcesFirstFile;
                }
                if (-1 != str4.trim().indexOf("/mnt/usb_storage")) {
                    String strCheckResourcesFirstFile2 = checkResourcesFirstFile(str4 + "/udisk0", fileFilter, z);
                    String strSubstring = str2.substring(str2.lastIndexOf("."));
                    if (strCheckResourcesFirstFile2 != null && !strCheckResourcesFirstFile2.trim().equals("") && -1 != strCheckResourcesFirstFile2.lastIndexOf(strSubstring)) {
                        return strCheckResourcesFirstFile2;
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    public static String checkResourcesFirstFile(String str, FileFilter fileFilter, boolean z) {
        File[] fileArrListFiles = new File(str).listFiles(fileFilter);
        if (fileArrListFiles == null || fileArrListFiles.length <= 0) {
            return null;
        }
        for (File file : fileArrListFiles) {
            String absolutePath = file.getAbsolutePath();
            if (z || absolutePath.endsWith("/" + fileFilter.filterString)) {
                return absolutePath;
            }
        }
        return "";
    }

    public static boolean isEmpty(Object obj) {
        return obj == null || "".equals(obj.toString().trim()) || obj.toString().trim().length() == 0;
    }

    public static int dip2px(float f) {
        return (int) ((f * AppApplication.getInstance().getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static boolean isEnabledGps() {
        LocationManager locationManager = (LocationManager) AppApplication.getContext().getSystemService("location");
        return locationManager == null || locationManager.isProviderEnabled("gps");
    }

    public static boolean isEnabledSIMCard() {
        Context context = AppApplication.getContext();
        AppApplication.getContext();
        int simState = ((TelephonyManager) context.getSystemService("phone")).getSimState();
        return (simState == 0 || simState == 1 || simState == 2 || simState == 3 || simState == 4) ? false : true;
    }

    public static boolean isWifiConnect() {
        return ((ConnectivityManager) AppApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(1).isConnected();
    }

    public static double distanceByLongNLat(double d, double d2, double d3, double d4) {
        double d5 = (d2 * 3.141592653589793d) / 180.0d;
        double d6 = (d4 * 3.141592653589793d) / 180.0d;
        double dSin = Math.sin((d5 - d6) / 2.0d);
        double dSin2 = Math.sin((((d - d3) * 3.141592653589793d) / 180.0d) / 2.0d);
        return 12756.274d * Math.asin(Math.sqrt((dSin * dSin) + (Math.cos(d5) * Math.cos(d6) * dSin2 * dSin2)));
    }

    public static int getNetWorkType() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) AppApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return 3;
        }
        String typeName = activeNetworkInfo.getTypeName();
        if (typeName.equalsIgnoreCase("WIFI")) {
            return 0;
        }
        if (typeName.equalsIgnoreCase("MOBILE")) {
            return isFastMobileNetwork() ? 1 : 2;
        }
        return -1;
    }

    public static boolean isFastMobileNetwork() {
        return false;
    }

    public static boolean isNetConnected() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) AppApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isAvailable();
        }
        return false;
    }

    public static String getMacAddress() {
        try {
            String strLoadFileAsString = loadFileAsString("/sys/class/sunxi_info/sys_info");
            if (strLoadFileAsString == null || strLoadFileAsString.trim().equals("") || strLoadFileAsString.split("sunxi_chipid").length <= 1) {
                return "piderror";
            }
            String strTrim = strLoadFileAsString.split("sunxi_chipid")[1].trim();
            return strTrim.substring(1, strTrim.indexOf("sunxi_chiptype")).trim();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String loadFileAsString(String str) throws IOException {
        StringBuffer stringBuffer = new StringBuffer(1000);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(str));
        char[] cArr = new char[1024];
        while (true) {
            int i = bufferedReader.read(cArr);
            if (i != -1) {
                stringBuffer.append(String.valueOf(cArr, 0, i));
            } else {
                bufferedReader.close();
                return stringBuffer.toString();
            }
        }
    }

    public static String getValueByName(JSONObject jSONObject, String str) throws JSONException {
        if (jSONObject.has(str)) {
            return jSONObject.getString(str);
        }
        return null;
    }

    public static int getLocalVersion() {
        int i = 0;
        try {
            i = AppApplication.getContext().getApplicationContext().getPackageManager().getPackageInfo(AppApplication.getContext().getPackageName(), 0).versionCode;
            Log.d("TAG", "本软件的版本号。。" + i);
            return i;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return i;
        }
    }

    public static String getLocalVersionName() {
        String str = "";
        try {
            str = AppApplication.getContext().getApplicationContext().getPackageManager().getPackageInfo(AppApplication.getContext().getPackageName(), 0).versionName;
            Log.d("TAG", "本软件的版本号。。" + str);
            return str;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return str;
        }
    }

    public static boolean isMobileEnableReflex() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            Method declaredMethod = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled", new Class[0]);
            declaredMethod.setAccessible(true);
            return ((Boolean) declaredMethod.invoke(connectivityManager, new Object[0])).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getIP() {
        StringBuilder sb = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddressNextElement = inetAddresses.nextElement();
                    if (!inetAddressNextElement.isLoopbackAddress() && !inetAddressNextElement.isLinkLocalAddress() && inetAddressNextElement.isSiteLocalAddress()) {
                        sb.append(inetAddressNextElement.getHostAddress().toString() + "\n");
                    }
                }
            }
        } catch (SocketException unused) {
        }
        return sb.toString();
    }

    public static boolean pingWired() {
        String str;
        StringBuilder sb;
        Process processExec;
        BufferedReader bufferedReader;
        StringBuffer stringBuffer;
        try {
            processExec = Runtime.getRuntime().exec("ping -c 3 -w 100 www.baidu.com");
            bufferedReader = new BufferedReader(new InputStreamReader(processExec.getInputStream()));
            stringBuffer = new StringBuffer();
        } catch (IOException unused) {
            str = "IOException";
            sb = new StringBuilder();
        } catch (InterruptedException unused2) {
            str = "InterruptedException";
            sb = new StringBuilder();
        } catch (Throwable th) {
            LogUtils.d("----result---", "result = " + ((String) null));
            throw th;
        }
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            stringBuffer.append(line);
            LogUtils.d("----result---", sb.append("result = ").append(str).toString());
            return false;
        }
        LogUtils.d("------ping-----", "result content : " + stringBuffer.toString());
        if (processExec.waitFor() == 0) {
            LogUtils.d("----result---", "result = " + r.C);
            return true;
        }
        str = "failed";
        sb = new StringBuilder();
        LogUtils.d("----result---", sb.append("result = ").append(str).toString());
        return false;
    }

    public static int isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(9);
        NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(1);
        if (networkInfo != null && networkInfo.isConnected()) {
            return NET_ETHERNET;
        }
        if (networkInfo2 != null && networkInfo2.isConnected()) {
            return NET_WIFI;
        }
        return NET_NOCONNECT;
    }

    public static String getEtherNetIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterfaceNextElement = networkInterfaces.nextElement();
                String displayName = networkInterfaceNextElement.getDisplayName();
                Log.i(DropBoxManager.EXTRA_TAG, "网络名字" + displayName);
                if (displayName.equals("eth0")) {
                    Enumeration<InetAddress> inetAddresses = networkInterfaceNextElement.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddressNextElement = inetAddresses.nextElement();
                        if (!inetAddressNextElement.isLoopbackAddress() && (inetAddressNextElement instanceof Inet4Address)) {
                            Log.i(DropBoxManager.EXTRA_TAG, inetAddressNextElement.getHostAddress() + "   ");
                            return inetAddressNextElement.getHostAddress();
                        }
                    }
                }
            }
            return "";
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDeviceBoand() {
        return Build.BOARD;
    }

    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    public static String getSystemModel() {
        return Build.MODEL;
    }

    public static String getSystemDevice() {
        return Build.DEVICE;
    }
}
