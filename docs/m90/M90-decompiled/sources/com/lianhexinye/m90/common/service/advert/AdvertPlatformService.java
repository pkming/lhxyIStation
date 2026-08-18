package com.lianhexinye.m90.common.service.advert;

import android.content.Intent;
import android.os.IBinder;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.service.BaseService;
import com.lianhexinye.m90.common.service.listenner.UpdateUIListenner;
import com.lianhexinye.m90.common.utils.AdvertAnalysisUtils;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.retrofit.ApiManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes2.dex */
public class AdvertPlatformService extends BaseService {
    private int nofityId;
    private final String TAG = "AdvertPlatformService";
    private Intent intent = new Intent("com.lianhexinye.m90.ui.activity.MsgReceiver");
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private Call<NotifyResult> resultCall = null;
    private UpdateUIListenner updateUIListenner = new UpdateUIListenner() { // from class: com.lianhexinye.m90.common.service.advert.AdvertPlatformService.3
        @Override // com.lianhexinye.m90.common.service.listenner.UpdateUIListenner
        public void startUpdateUI(int i, String str) {
            AdvertPlatformService.this.intent.putExtra("operation", i);
            AdvertPlatformService advertPlatformService = AdvertPlatformService.this;
            advertPlatformService.sendBroadcast(advertPlatformService.intent);
        }

        @Override // com.lianhexinye.m90.common.service.listenner.UpdateUIListenner
        public void errorUpdateUI(int i, String str) {
            AdvertPlatformService.this.intent.putExtra("operation", i);
            AdvertPlatformService advertPlatformService = AdvertPlatformService.this;
            advertPlatformService.sendBroadcast(advertPlatformService.intent);
        }

        @Override // com.lianhexinye.m90.common.service.listenner.UpdateUIListenner
        public void successUpdateUI(int i, String str) {
            AdvertPlatformService.this.intent.putExtra("operation", i);
            AdvertPlatformService advertPlatformService = AdvertPlatformService.this;
            advertPlatformService.sendBroadcast(advertPlatformService.intent);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void uploadDownloadInfo() {
    }

    @Override // com.lianhexinye.m90.common.service.BaseService, android.app.Service
    public IBinder onBind(Intent intent) {
        this.executorService.scheduleAtFixedRate(new TaskHandleRun(), 10L, Integer.valueOf((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetAdvertInterfaceInterval", "10")).intValue(), TimeUnit.SECONDS);
        return null;
    }

    class TaskHandleRun implements Runnable {
        TaskHandleRun() {
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (AndroidUtils.isNetConnected()) {
                    AdvertPlatformService.this.resultCall = null;
                    AdvertPlatformService.this.resultCall = ApiManager.apiManager.getNotify(AndroidUtils.getMacAddress());
                    AdvertPlatformService.this.resultCall.enqueue(new Callback<NotifyResult>() { // from class: com.lianhexinye.m90.common.service.advert.AdvertPlatformService.TaskHandleRun.1
                        @Override // retrofit2.Callback
                        public void onResponse(Call<NotifyResult> call, Response<NotifyResult> response) {
                            if (response.body() == null || response.body().getResult() != 1) {
                                return;
                            }
                            AdvertPlatformService.this.nofityId = response.body().getNofityId();
                            int messageType = response.body().getMessageType();
                            if (messageType == 1) {
                                AdvertPlatformService.this.getChannelContent("" + response.body().getContentId());
                                return;
                            }
                            if (messageType != 5) {
                                if (AdvertPlatformService.this.nofityId > 0) {
                                    AdvertPlatformService.this.updateNotify();
                                }
                            } else {
                                AdvertPlatformService.this.updateNotify();
                                AdvertPlatformService.this.uploadDownloadInfo();
                            }
                        }

                        @Override // retrofit2.Callback
                        public void onFailure(Call<NotifyResult> call, Throwable th) {
                            LogUtils.d("AdvertPlatformService", "getNotify数据请求失败");
                        }
                    });
                    AdvertPlatformService.this.resultCall = ApiManager.apiManager.myInformation(AndroidUtils.getMacAddress());
                    AdvertPlatformService.this.resultCall.enqueue(new Callback<NotifyResult>() { // from class: com.lianhexinye.m90.common.service.advert.AdvertPlatformService.TaskHandleRun.2
                        @Override // retrofit2.Callback
                        public void onResponse(Call<NotifyResult> call, Response<NotifyResult> response) {
                            LogUtils.d("AdvertPlatformService", "myInformation 数据请求成功");
                        }

                        @Override // retrofit2.Callback
                        public void onFailure(Call<NotifyResult> call, Throwable th) {
                            LogUtils.d("AdvertPlatformService", "myInformation 数据请求失败");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getChannelContent(String str) {
        ApiManager.apiManager.getChannelContent(str).enqueue(new Callback<NotifyResult>() { // from class: com.lianhexinye.m90.common.service.advert.AdvertPlatformService.1
            @Override // retrofit2.Callback
            public void onResponse(Call<NotifyResult> call, Response<NotifyResult> response) {
                LogUtils.d("AdvertPlatformService", "getChannelContent数据请求成功");
                if (response.body() == null || response.body().getResult() != 1) {
                    return;
                }
                AdvertPlatformService.this.updateUIListenner.startUpdateUI(5, "");
                if (AdvertAnalysisUtils.analyzeChannel(response.body().getContent())) {
                    AdvertPlatformService.this.updateUIListenner.successUpdateUI(6, "");
                    AdvertPlatformService.this.updateNotify();
                }
            }

            @Override // retrofit2.Callback
            public void onFailure(Call<NotifyResult> call, Throwable th) {
                LogUtils.d("AdvertPlatformService", "getChannelContent数据请求失败");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotify() {
        ApiManager.apiManager.postNotify(ApiManager.SERVER_PRODUCT + "/api/notify/" + this.nofityId).enqueue(new Callback<NotifyResult>() { // from class: com.lianhexinye.m90.common.service.advert.AdvertPlatformService.2
            @Override // retrofit2.Callback
            public void onResponse(Call<NotifyResult> call, Response<NotifyResult> response) {
                LogUtils.d("AdvertPlatformService", "updateNotify");
                if (response.body() == null || response.body().getResult() == 1) {
                    return;
                }
                LogUtils.d("AdvertPlatformService", "重新发确定接口");
                AdvertPlatformService.this.updateNotify();
            }

            @Override // retrofit2.Callback
            public void onFailure(Call<NotifyResult> call, Throwable th) {
                LogUtils.d("AdvertPlatformService", "数据请求失败");
            }
        });
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        ScheduledExecutorService scheduledExecutorService = this.executorService;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            this.executorService = null;
        }
        return super.onUnbind(intent);
    }

    @Override // android.app.Service
    public void onDestroy() {
        ScheduledExecutorService scheduledExecutorService = this.executorService;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            this.executorService = null;
        }
    }
}
