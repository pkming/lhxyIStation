package com.lianhexinye.m90.common.service.upapp;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.Constants;
import com.lianhexinye.m90.common.service.BaseService;
import com.lianhexinye.m90.common.service.advert.NotifyResult;
import com.lianhexinye.m90.common.service.download.DownLoadCallback;
import com.lianhexinye.m90.common.service.download.MultiTheradDownLoad;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.retrofit.ApiManager;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes2.dex */
public class UpAppService extends BaseService {
    private CountDownLatch latch;
    private final String TAG = "UpAppService";
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private Intent intent = new Intent("com.lianhexinye.m90.ui.activity.MsgReceiver");
    private String strApkUrl = "";
    MultiTheradDownLoad multiTheradDownLoad = null;
    private DownLoadCallback downLoadCallback = new DownLoadCallback() { // from class: com.lianhexinye.m90.common.service.upapp.UpAppService.2
        @Override // com.lianhexinye.m90.common.service.download.DownLoadCallback
        public void onDownLoadComplete(int i) {
            if (i > 0) {
                UpAppService.this.multiTheradDownLoad = null;
            }
            UpAppService.this.intent.putExtra("operation", i);
            UpAppService upAppService = UpAppService.this;
            upAppService.sendBroadcast(upAppService.intent);
        }
    };

    /* JADX WARN: Type inference failed for: r1v1, types: [com.lianhexinye.m90.common.service.upapp.UpAppService$1] */
    @Override // com.lianhexinye.m90.common.service.BaseService, android.app.Service
    public IBinder onBind(Intent intent) {
        new Thread() { // from class: com.lianhexinye.m90.common.service.upapp.UpAppService.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                super.run();
                if (!AndroidUtils.isNetConnected()) {
                    try {
                        Thread.sleep(6000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (AndroidUtils.isNetConnected()) {
                    ApiManager.apiManager.getUpVersionInfo(String.valueOf(SPUserInfoUtils.get(AppApplication.getContext(), "NetAdvertUser", "admin"))).enqueue(new Callback<NotifyResult>() { // from class: com.lianhexinye.m90.common.service.upapp.UpAppService.1.1
                        @Override // retrofit2.Callback
                        public void onResponse(Call<NotifyResult> call, Response<NotifyResult> response) {
                            String message;
                            if (response.body() == null || response.body().getResult() != 1 || (message = response.body().getMessage()) == null || message.trim().equals("") || -1 == message.trim().toLowerCase().lastIndexOf(".apk") || -1 == message.trim().toLowerCase().lastIndexOf("v")) {
                                return;
                            }
                            UpAppService.this.strApkUrl = message;
                            String strSubstring = message.substring(message.trim().toLowerCase().lastIndexOf("v") + 1, message.trim().toLowerCase().lastIndexOf(".apk"));
                            if (strSubstring == null || strSubstring.trim().equals("")) {
                                return;
                            }
                            LogUtils.d("UpAppService", "Message:" + strSubstring);
                            LogUtils.d("UpAppService", "LocalVersion:" + AndroidUtils.getLocalVersion());
                            if (!JavaUtils.isNumeric(strSubstring) || AndroidUtils.getLocalVersion() >= Integer.valueOf(strSubstring).intValue()) {
                                UpAppService.this.downLoadCallback.onDownLoadComplete(3);
                            } else {
                                UpAppService.this.downLoadCallback.onDownLoadComplete(4);
                                UpAppService.this.executorService.schedule(UpAppService.this.new TaskHandleRun(), 0L, TimeUnit.MILLISECONDS);
                            }
                        }

                        @Override // retrofit2.Callback
                        public void onFailure(Call<NotifyResult> call, Throwable th) {
                            Log.d("AdvertPlatformService", "数据请求失败");
                        }
                    });
                }
            }
        }.start();
        return null;
    }

    class TaskHandleRun implements Runnable {
        TaskHandleRun() {
        }

        @Override // java.lang.Runnable
        public void run() throws Throwable {
            LogUtils.d("UpAppService", "downLoadAPKPath：" + UpAppService.this.strApkUrl);
            UpAppService.this.latch = new CountDownLatch(4);
            UpAppService.this.multiTheradDownLoad = new MultiTheradDownLoad(UpAppService.this.strApkUrl, UpAppService.this.latch, 4, 0L, UpAppService.this.downLoadCallback, Constants.SD_ROOT + Constants.APK_RES_PATH);
            UpAppService.this.multiTheradDownLoad.downloadAPK();
        }
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        if (this.latch != null) {
            while (this.latch.getCount() > 0) {
                this.latch.countDown();
            }
            this.latch = null;
        }
        ScheduledExecutorService scheduledExecutorService = this.executorService;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            this.executorService = null;
        }
        return super.onUnbind(intent);
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (this.latch != null) {
            while (this.latch.getCount() > 0) {
                this.latch.countDown();
            }
            this.latch = null;
        }
        ScheduledExecutorService scheduledExecutorService = this.executorService;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            this.executorService = null;
        }
    }
}
