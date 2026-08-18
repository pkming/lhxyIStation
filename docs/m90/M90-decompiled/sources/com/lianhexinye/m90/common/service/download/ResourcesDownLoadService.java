package com.lianhexinye.m90.common.service.download;

import android.content.Intent;
import android.os.IBinder;
import com.lianhexinye.m90.common.Constants;
import com.lianhexinye.m90.common.service.BaseService;
import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusMediaModel;
import com.lianhexinye.m90.greendao.gen.BusMediaModelDao;
import com.lianhexinye.m90.greendao.gen.DownLoadInfoModel;
import com.lianhexinye.m90.retrofit.ApiManager;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class ResourcesDownLoadService extends BaseService {
    private CountDownLatch latch;
    private MultiTheradDownLoad multiTheradDownLoad;
    private final String TAG = "ResourcesDownLoadService";
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private Intent intent = new Intent("com.lianhexinye.m90.ui.activity.MsgReceiver");
    private BusMediaModelDao busMediaModelDao = null;
    private DownLoadCallback downLoadCallback = new DownLoadCallback() { // from class: com.lianhexinye.m90.common.service.download.ResourcesDownLoadService.1
        @Override // com.lianhexinye.m90.common.service.download.DownLoadCallback
        public void onDownLoadComplete(int i) {
            if (i > 0) {
                ResourcesDownLoadService.this.multiTheradDownLoad = null;
            }
            ResourcesDownLoadService.this.intent.putExtra("operation", i);
            ResourcesDownLoadService resourcesDownLoadService = ResourcesDownLoadService.this;
            resourcesDownLoadService.sendBroadcast(resourcesDownLoadService.intent);
        }
    };

    @Override // com.lianhexinye.m90.common.service.BaseService, android.app.Service
    public IBinder onBind(Intent intent) {
        LogUtils.d("ResourcesDownLoadService", "进入ResourcesDownLoadService");
        this.busMediaModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusMediaModelDao();
        this.executorService.scheduleWithFixedDelay(new TaskHandleRun(), 7000L, 40000L, TimeUnit.MILLISECONDS);
        return null;
    }

    class TaskHandleRun implements Runnable {
        TaskHandleRun() {
        }

        @Override // java.lang.Runnable
        public void run() throws Throwable {
            LogUtils.d("ResourcesDownLoadService", "查找是否有下载文件");
            List<BusMediaModel> list = ResourcesDownLoadService.this.busMediaModelDao.queryBuilder().where(BusMediaModelDao.Properties.DownloadState.eq(0), BusMediaModelDao.Properties.FileResult.eq(0)).list();
            if (list == null || list.size() <= 0) {
                return;
            }
            BusMediaModel busMediaModel = list.get(0);
            String str = ApiManager.SERVER_PRODUCT + busMediaModel.getDownloadUrls().split(",")[0];
            if (busMediaModel.getDataState() == 0) {
                List<DownLoadInfoModel> infos = DownLoadManage.getInstance().getInfos(str);
                if (infos != null && infos.size() > 0) {
                    Iterator<DownLoadInfoModel> it = infos.iterator();
                    while (it.hasNext()) {
                        DownLoadManage.getInstance().deleInfos(it.next());
                    }
                }
                busMediaModel.setDataState(1);
                ResourcesDownLoadService.this.busMediaModelDao.update(busMediaModel);
            }
            LogUtils.d("ResourcesDownLoadService", "未下载文件：" + str);
            ResourcesDownLoadService.this.latch = new CountDownLatch(4);
            ResourcesDownLoadService.this.multiTheradDownLoad = new MultiTheradDownLoad(str, ResourcesDownLoadService.this.latch, 4, busMediaModel.get_id().longValue(), ResourcesDownLoadService.this.downLoadCallback, Constants.SD_ROOT + Constants.TMP_NETWORK_RES_PATH);
            ResourcesDownLoadService.this.multiTheradDownLoad.downloadPart();
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
            scheduledExecutorService.shutdownNow();
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
            scheduledExecutorService.shutdownNow();
            this.executorService = null;
        }
    }
}
