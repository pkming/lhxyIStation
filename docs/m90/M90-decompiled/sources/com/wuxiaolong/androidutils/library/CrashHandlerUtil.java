package com.wuxiaolong.androidutils.library;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class CrashHandlerUtil implements Thread.UncaughtExceptionHandler {
    private static CrashHandlerUtil INSTANCE = new CrashHandlerUtil();
    public static final String TAG = "CrashHandlerUtil";
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> infos = new HashMap();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
    private String crashTip = "很抱歉，程序出现异常，即将退出！";

    public String getCrashTip() {
        return this.crashTip;
    }

    public void setCrashTip(String str) {
        this.crashTip = str;
    }

    private CrashHandlerUtil() {
    }

    public static CrashHandlerUtil getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
        if (!handleException(th) && (uncaughtExceptionHandler = this.mDefaultHandler) != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
            return;
        }
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "error : ", e);
            e.printStackTrace();
        }
        ActivityManagerUtil.getInstance().appExit();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.wuxiaolong.androidutils.library.CrashHandlerUtil$1] */
    private boolean handleException(final Throwable th) {
        if (th == null) {
            return false;
        }
        new Thread() { // from class: com.wuxiaolong.androidutils.library.CrashHandlerUtil.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Looper.prepare();
                th.printStackTrace();
                Toast.makeText(CrashHandlerUtil.this.mContext, CrashHandlerUtil.this.getCrashTip(), 1).show();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo(this.mContext);
        saveCrashInfo2File(th);
        return true;
    }

    public void collectDeviceInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 1);
            if (packageInfo != null) {
                String str = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String str2 = packageInfo.versionCode + "";
                this.infos.put("versionName", str);
                this.infos.put("versionCode", str2);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        for (Field field : Build.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                this.infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e2) {
                Log.e(TAG, "an error occured when collect crash info", e2);
            }
        }
    }

    private String saveCrashInfo2File(Throwable th) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : this.infos.entrySet()) {
            stringBuffer.append(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        for (Throwable cause = th.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        printWriter.close();
        stringBuffer.append(stringWriter.toString());
        try {
            String str = "crash-" + this.formatter.format(new Date()) + "-" + System.currentTimeMillis() + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String str2 = Environment.getExternalStorageDirectory().getPath() + "/crash/";
                LogUtil.d("path=" + str2);
                File file = new File(str2);
                if (!file.exists()) {
                    file.mkdirs();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(str2 + str);
                fileOutputStream.write(stringBuffer.toString().getBytes());
                fileOutputStream.close();
            }
            return str;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
            return null;
        }
    }
}
