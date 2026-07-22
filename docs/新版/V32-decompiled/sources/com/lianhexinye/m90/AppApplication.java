package com.lianhexinye.m90;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import com.lianhexinye.gpsserialport.GPSSerialPort;
import com.lianhexinye.jhyserialport.JHYSerialPort;
import com.lianhexinye.m90.common.utils.language.LocalManageUtil;
import com.lianhexinye.m90.common.utils.log.Level;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.ui.activity.MainActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class AppApplication extends Application {
    public static String cmsState = "UNCON";
    public static Context context = null;
    public static BusLineModel currentBusLineModel = null;
    public static boolean gotoSiteCollection = false;
    public static String gpsState = "";
    private static AppApplication instance = null;
    public static boolean isUserPassword = false;
    public static JHYSerialPort jhyopd = null;
    private static List<Activity> mActivitys = null;
    public static GPSSerialPort opd = null;
    public static MainActivity.Send485Thread send485Thread = null;
    public static String simState = "UNCON";
    public static String wifiState = "UNCON";
    public static List<LineNameModel> lineNameModels2 = new ArrayList();
    public static String currentLineId = null;
    public static List<BusLineModel> listBusLine = new ArrayList();
    public static List<BusLineFriendRemindModel> listBusLineFriendRemindModel = new ArrayList();
    public static Map<String, BusLineModel> mapBusLine = new HashMap();
    public static Map<String, String> mapDispatch = new HashMap();
    public static Map<String, String> fileAnalyticStatistics = new HashMap();
    public static MainActivity.Serial485Control mSerial485Control = null;
    public static boolean iLocalUpdate = false;
    public static boolean iLocalUpdateApp = false;
    public static String downloadUrl = "";

    static {
        System.loadLibrary("gps_serial_port");
        System.loadLibrary("jhy_serial_port");
        mActivitys = Collections.synchronizedList(new LinkedList());
    }

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context context2) {
        LocalManageUtil.saveSystemCurrentLanguage(context2);
        super.attachBaseContext(LocalManageUtil.setLocal(context2));
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
        registerActivityListener();
        LogUtils.initialize(context, false, Level.VERBOSE);
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        LocalManageUtil.onConfigurationChanged(getApplicationContext());
    }

    @Override // android.app.Application
    public void onTerminate() {
        super.onTerminate();
    }

    public static Context getContext() {
        return context;
    }

    public static AppApplication getInstance() {
        return instance;
    }

    public void pushActivity(Activity activity) {
        mActivitys.add(activity);
    }

    public void popActivity(Activity activity) {
        mActivitys.remove(activity);
    }

    public static Activity currentActivity() {
        List<Activity> list = mActivitys;
        if (list == null || list.isEmpty()) {
            return null;
        }
        return mActivitys.get(r0.size() - 1);
    }

    public static void finishCurrentActivity() {
        List<Activity> list = mActivitys;
        if (list == null || list.isEmpty()) {
            return;
        }
        finishActivity(mActivitys.get(r0.size() - 1));
    }

    public static void finishActivity(Activity activity) {
        List<Activity> list = mActivitys;
        if (list == null || list.isEmpty() || activity == null) {
            return;
        }
        mActivitys.remove(activity);
        activity.finish();
    }

    public static void finishAllActivity() {
        List<Activity> list = mActivitys;
        if (list == null) {
            return;
        }
        Iterator<Activity> it = list.iterator();
        while (it.hasNext()) {
            it.next().finish();
        }
        mActivitys.clear();
    }

    public static void appExit() {
        try {
            finishAllActivity();
        } catch (Exception unused) {
        }
    }

    public static void appExit(Context context2) {
        try {
            finishAllActivity();
            ((ActivityManager) context2.getSystemService(Context.ACTIVITY_SERVICE)).killBackgroundProcesses(context2.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void finishActivity(Class<?> cls) {
        List<Activity> list = mActivitys;
        if (list == null || list.isEmpty()) {
            return;
        }
        for (Activity activity : mActivitys) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    public static List<Activity> findActivitys() {
        return mActivitys;
    }

    public static Activity findActivity(Class<?> cls) {
        List<Activity> list = mActivitys;
        if (list != null) {
            for (Activity activity : list) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        }
        return null;
    }

    public Activity getTopActivity() {
        synchronized (mActivitys) {
            int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            return mActivitys.get(size);
        }
    }

    public String getTopActivityName() {
        synchronized (mActivitys) {
            int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            return mActivitys.get(size).getClass().getName();
        }
    }

    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() { // from class: com.lianhexinye.m90.AppApplication.1
            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityPaused(Activity activity) {
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityResumed(Activity activity) {
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityStarted(Activity activity) {
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityStopped(Activity activity) {
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityCreated(Activity activity, Bundle bundle) {
                AppApplication.this.pushActivity(activity);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityDestroyed(Activity activity) {
                if (!(AppApplication.mActivitys == null && AppApplication.mActivitys.isEmpty()) && AppApplication.mActivitys.contains(activity)) {
                    AppApplication.this.popActivity(activity);
                }
            }
        });
    }
}
