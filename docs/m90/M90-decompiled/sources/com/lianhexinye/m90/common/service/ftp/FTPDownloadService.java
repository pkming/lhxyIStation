package com.lianhexinye.m90.common.service.ftp;

import android.content.Intent;
import android.os.IBinder;
import android.util.TimedRemoteCaller;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.service.BaseService;
import com.lianhexinye.m90.common.service.download.DownLoadManage;
import com.lianhexinye.m90.common.utils.FileUtils;
import com.lianhexinye.m90.common.utils.FtpUtil;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.SilentInstall;
import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.common.utils.language.SPUtil;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModelDao;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModelDao;
import com.lianhexinye.m90.greendao.gen.DownLoadFTPModel;
import com.lianhexinye.m90.greendao.gen.DownLoadFTPModelDao;
import com.lianhexinye.m90.greendao.gen.DownLoadInfoModel;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import com.lianhexinye.m90.socket.SocketManage;
import com.lianhexinye.m90.socket.request.Generate808ReqPackage;
import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.greenrobot.greendao.query.WhereCondition;

/* JADX INFO: loaded from: classes2.dex */
public class FTPDownloadService extends BaseService {
    private ReportInfoModel reportInfoModel;
    private final String TAG = "FTPDownloadService";
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private Intent intent = new Intent("com.lianhexinye.m90.ui.activity.MsgReceiver");
    private DownLoadFTPModelDao downLoadFTPModelDao = null;
    private CountDownLatch latch = null;
    private DownLoadFTPModel downLoadFTPModel = null;
    private DownLoadManage downLoadManage = null;
    private DownLoadInfoModel downLoadInfoModel = null;
    private FtpUtil ftpUtil = null;
    private int apkReconnection = 0;
    private int busReconnection = 0;
    private long noticeTime = 0;
    private FtpUtil.FtpProgressListener ftpProgressListener = new FtpUtil.FtpProgressListener() { // from class: com.lianhexinye.m90.common.service.ftp.FTPDownloadService.1
        @Override // com.lianhexinye.m90.common.utils.FtpUtil.FtpProgressListener
        public void onFtpProgress(int i, long j, long j2, long j3, File file) {
            FTPDownloadService.this.reportInfoModel.setSerialNumber(FTPDownloadService.this.downLoadFTPModel.getMsgSerialNumber());
            if (i == 0) {
                FTPDownloadService.this.reportInfoModel.setResponseID("8b0a");
                FTPDownloadService.this.reportInfoModel.setResult(0);
                SocketManage.getInstance().sendReq(Generate808ReqPackage.generateGeneralResponse(FTPDownloadService.this.reportInfoModel));
                if (FTPDownloadService.this.downLoadFTPModel.getType() == 2) {
                    FTPDownloadService.this.intent.putExtra("operation", 4);
                    FTPDownloadService fTPDownloadService = FTPDownloadService.this;
                    fTPDownloadService.sendBroadcast(fTPDownloadService.intent);
                    return;
                }
                return;
            }
            if (i == 1) {
                FTPDownloadService.this.connectFailAnalysis();
                FTPDownloadService.this.latch.countDown();
                return;
            }
            if (i == 2) {
                FTPDownloadService.this.latch.countDown();
                return;
            }
            if (i == 3) {
                FTPDownloadService.this.reportInfoModel.setResponseID("8b0a");
                FTPDownloadService.this.reportInfoModel.setResult(1);
                SocketManage.getInstance().sendReq(Generate808ReqPackage.generateGeneralResponse(FTPDownloadService.this.reportInfoModel));
                FTPDownloadService.this.intent.putExtra("operation", 3);
                FTPDownloadService fTPDownloadService2 = FTPDownloadService.this;
                fTPDownloadService2.sendBroadcast(fTPDownloadService2.intent);
                FTPDownloadService.this.latch.countDown();
                return;
            }
            if (i != 7) {
                if (i != 8) {
                    if (i == 9) {
                        FTPDownloadService.this.latch.countDown();
                        return;
                    } else {
                        if (i != 20) {
                            return;
                        }
                        FTPDownloadService.this.initDownLoadInfo(j2);
                        return;
                    }
                }
                FTPDownloadService.this.reportInfoModel.setUpgradeStatus(8);
                FTPDownloadService.this.reportInfoModel.setUpgradeProgress(100);
                SocketManage.getInstance().sendReq(Generate808ReqPackage.generateUpgradeNotification(FTPDownloadService.this.reportInfoModel));
                FTPDownloadService.this.downLoadFTPModel.setStatus(1);
                FTPDownloadService.this.downLoadFTPModelDao.update(FTPDownloadService.this.downLoadFTPModel);
                FTPDownloadService.this.latch.countDown();
                return;
            }
            if (System.currentTimeMillis() - FTPDownloadService.this.noticeTime >= TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS) {
                FTPDownloadService.this.noticeTime = System.currentTimeMillis();
                FTPDownloadService.this.reportInfoModel.setUpgradeStatus(8);
                FTPDownloadService.this.reportInfoModel.setUpgradeProgress((int) j);
                SocketManage.getInstance().sendReq(Generate808ReqPackage.generateUpgradeNotification(FTPDownloadService.this.reportInfoModel));
            }
            if (FTPDownloadService.this.downLoadInfoModel != null) {
                FTPDownloadService.this.downLoadInfoModel.setCompelete_size(Long.valueOf(j3));
                FTPDownloadService.this.downLoadManage.updataInfos(FTPDownloadService.this.downLoadInfoModel);
            }
            if (FTPDownloadService.this.downLoadFTPModel.getType() == 2) {
                FTPDownloadService.this.intent.putExtra("operation", 12);
                FTPDownloadService.this.intent.putExtra("operationmsg", (int) j);
            } else {
                FTPDownloadService.this.intent.putExtra("operation", 13);
                FTPDownloadService.this.intent.putExtra("operationmsg", (int) j);
            }
            FTPDownloadService fTPDownloadService3 = FTPDownloadService.this;
            fTPDownloadService3.sendBroadcast(fTPDownloadService3.intent);
        }
    };

    @Override // com.lianhexinye.m90.common.service.BaseService, android.app.Service
    public IBinder onBind(Intent intent) {
        LogUtils.d("FTPDownloadService", "进入FTPDownloadService");
        this.apkReconnection = 0;
        this.busReconnection = 0;
        this.downLoadManage = DownLoadManage.getInstance();
        this.downLoadFTPModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getDownLoadFTPModelDao();
        ReportInfoModel reportInfoModel = new ReportInfoModel();
        this.reportInfoModel = reportInfoModel;
        reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
        this.executorService.scheduleWithFixedDelay(new TaskHandleRun(), 4000L, 4000L, TimeUnit.MILLISECONDS);
        return null;
    }

    class TaskHandleRun implements Runnable {
        TaskHandleRun() {
        }

        @Override // java.lang.Runnable
        public void run() throws Throwable {
            LogUtils.d("FTPDownloadService", "进入TaskHandleRun");
            FTPDownloadService.this.downLoadInfoModel = null;
            List<DownLoadFTPModel> list = FTPDownloadService.this.downLoadFTPModelDao.queryBuilder().where(DownLoadFTPModelDao.Properties.Type.eq(2), DownLoadFTPModelDao.Properties.Status.lt(3)).list();
            if (list != null && list.size() > 0) {
                LogUtils.d("FTPDownloadService", "有apk要处理");
                FTPDownloadService.this.downLoadFTPModel = list.get(0);
                FTPDownloadService.this.reportInfoModel.setSerialNumber(FTPDownloadService.this.downLoadFTPModel.getMsgSerialNumber());
                LogUtils.d("FTPDownloadService", "apk Status:" + FTPDownloadService.this.downLoadFTPModel.getStatus());
                int status = FTPDownloadService.this.downLoadFTPModel.getStatus();
                if (status == 0) {
                    LogUtils.d("FTPDownloadService", "apk未下载");
                    LogUtils.d("FTPDownloadService", "服务器信息:" + FTPDownloadService.this.downLoadFTPModel.getUrl() + FTPDownloadService.this.downLoadFTPModel.getLoginName() + FTPDownloadService.this.downLoadFTPModel.getLoginPwd() + FTPDownloadService.this.downLoadFTPModel.getServerAddress() + FTPDownloadService.this.downLoadFTPModel.getServerAddressPort());
                    try {
                        List<DownLoadInfoModel> infos = FTPDownloadService.this.downLoadManage.getInfos(FTPDownloadService.this.downLoadFTPModel.getServerAddress() + FTPDownloadService.this.downLoadFTPModel.getServerAddressPort() + FTPDownloadService.this.downLoadFTPModel.getUrl());
                        if (infos != null && infos.size() > 0) {
                            FTPDownloadService.this.downLoadInfoModel = infos.get(0);
                        }
                        FTPDownloadService.this.createCountDownLatch();
                        FTPDownloadService.this.ftpUtil = new FtpUtil(FTPDownloadService.this.downLoadFTPModel.getServerAddress(), FTPDownloadService.this.downLoadFTPModel.getServerAddressPort(), FTPDownloadService.this.downLoadFTPModel.getLoginName(), FTPDownloadService.this.downLoadFTPModel.getLoginPwd());
                        FTPDownloadService.this.ftpUtil.downloadSingleFile(FTPDownloadService.this.downLoadFTPModel.getUrl(), FTPDownloadService.this.downLoadFTPModel.getLocalPath(), FTPDownloadService.this.downLoadInfoModel != null ? FTPDownloadService.this.downLoadInfoModel.getEnd_pos().longValue() : 0L, FTPDownloadService.this.downLoadInfoModel != null ? FTPDownloadService.this.downLoadInfoModel.getCompelete_size().longValue() : 0L, FTPDownloadService.this.ftpProgressListener);
                        FTPDownloadService.this.latch.await();
                        return;
                    } catch (Exception e) {
                        LogUtils.d("FTPDownloadService", "服务器：" + e.getMessage());
                        e.printStackTrace();
                        FTPDownloadService.this.latch.countDown();
                        return;
                    }
                }
                if (status != 1) {
                    if (status != 2) {
                        return;
                    }
                    LogUtils.d("FTPDownloadService", "未应答成功给平台");
                    if (SocketManage.getInstance().iHeartbeat) {
                        FTPDownloadService.this.reportInfoModel.setUpgradeStatus(1);
                        FTPDownloadService.this.reportInfoModel.setUpgradeProgress(0);
                        if (SocketManage.getInstance().sendReq(Generate808ReqPackage.generateUpgradeNotification(FTPDownloadService.this.reportInfoModel))) {
                            FTPDownloadService.this.downLoadFTPModel.setStatus(3);
                            FTPDownloadService.this.downLoadFTPModelDao.update(FTPDownloadService.this.downLoadFTPModel);
                            return;
                        }
                        return;
                    }
                    return;
                }
                LogUtils.d("FTPDownloadService", "apk未安装");
                FTPDownloadService.this.downLoadFTPModel.setStatus(2);
                FTPDownloadService.this.downLoadFTPModelDao.update(FTPDownloadService.this.downLoadFTPModel);
                FTPDownloadService.this.intent.putExtra("operation", 2);
                FTPDownloadService fTPDownloadService = FTPDownloadService.this;
                fTPDownloadService.sendBroadcast(fTPDownloadService.intent);
                if (AppApplication.iLocalUpdateApp) {
                    return;
                }
                new SilentInstall();
                if (new File(FTPDownloadService.this.downLoadFTPModel.getLocalPath()).exists()) {
                    SilentInstall.installSilent(FTPDownloadService.this.downLoadFTPModel.getLocalPath());
                    return;
                }
                return;
            }
            List<DownLoadFTPModel> list2 = FTPDownloadService.this.downLoadFTPModelDao.queryBuilder().where(DownLoadFTPModelDao.Properties.Type.eq(1), DownLoadFTPModelDao.Properties.Status.lt(3)).list();
            if (list2 == null || list2.size() <= 0) {
                FTPDownloadService.this.intent.putExtra("operation", 9);
                FTPDownloadService fTPDownloadService2 = FTPDownloadService.this;
                fTPDownloadService2.sendBroadcast(fTPDownloadService2.intent);
                return;
            }
            LogUtils.d("FTPDownloadService", "有Bus要处理");
            FTPDownloadService.this.downLoadFTPModel = list2.get(0);
            FTPDownloadService.this.reportInfoModel.setSerialNumber(FTPDownloadService.this.downLoadFTPModel.getMsgSerialNumber());
            LogUtils.d("FTPDownloadService", "Bus Status:" + FTPDownloadService.this.downLoadFTPModel.getStatus());
            int status2 = FTPDownloadService.this.downLoadFTPModel.getStatus();
            if (status2 == 0) {
                LogUtils.d("FTPDownloadService", "Bus未下载");
                try {
                    List<DownLoadInfoModel> infos2 = FTPDownloadService.this.downLoadManage.getInfos(FTPDownloadService.this.downLoadFTPModel.getServerAddress() + FTPDownloadService.this.downLoadFTPModel.getServerAddressPort() + FTPDownloadService.this.downLoadFTPModel.getUrl());
                    if (infos2 != null && infos2.size() > 0) {
                        FTPDownloadService.this.downLoadInfoModel = infos2.get(0);
                    }
                    FTPDownloadService.this.createCountDownLatch();
                    FTPDownloadService.this.ftpUtil = new FtpUtil(FTPDownloadService.this.downLoadFTPModel.getServerAddress(), FTPDownloadService.this.downLoadFTPModel.getServerAddressPort(), FTPDownloadService.this.downLoadFTPModel.getLoginName(), FTPDownloadService.this.downLoadFTPModel.getLoginPwd());
                    FTPDownloadService.this.ftpUtil.downloadSingleFile(FTPDownloadService.this.downLoadFTPModel.getUrl(), FTPDownloadService.this.downLoadFTPModel.getLocalPath(), FTPDownloadService.this.downLoadInfoModel != null ? FTPDownloadService.this.downLoadInfoModel.getEnd_pos().longValue() : 0L, FTPDownloadService.this.downLoadInfoModel != null ? FTPDownloadService.this.downLoadInfoModel.getCompelete_size().longValue() : 0L, FTPDownloadService.this.ftpProgressListener);
                    FTPDownloadService.this.latch.await();
                    return;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    FTPDownloadService.this.latch.countDown();
                    return;
                }
            }
            if (status2 == 1) {
                LogUtils.d("FTPDownloadService", "Bus未解析");
                if (!AppApplication.iLocalUpdate) {
                    FTPDownloadService.this.analysisBusFile();
                    return;
                } else {
                    FTPDownloadService.this.downLoadFTPModel.setStatus(2);
                    FTPDownloadService.this.downLoadFTPModelDao.update(FTPDownloadService.this.downLoadFTPModel);
                    return;
                }
            }
            if (status2 != 2) {
                return;
            }
            LogUtils.d("FTPDownloadService", "未应答成功给平台");
            if (SocketManage.getInstance().iHeartbeat) {
                FTPDownloadService.this.reportInfoModel.setUpgradeStatus(1);
                FTPDownloadService.this.reportInfoModel.setUpgradeProgress(0);
                if (SocketManage.getInstance().sendReq(Generate808ReqPackage.generateUpgradeNotification(FTPDownloadService.this.reportInfoModel))) {
                    FTPDownloadService.this.downLoadFTPModel.setStatus(3);
                    FTPDownloadService.this.downLoadFTPModelDao.update(FTPDownloadService.this.downLoadFTPModel);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void analysisBusFile() {
        this.intent.putExtra("operation", 0);
        sendBroadcast(this.intent);
        if (FileUtils.analysisBusFile(this.downLoadFTPModel.getLocalPath())) {
            ConfigInfoModel configInfoModelQueryConfigInfoModel = queryConfigInfoModel("IfConfigEffectiveImmediately");
            if (configInfoModelQueryConfigInfoModel == null || configInfoModelQueryConfigInfoModel.getConfigValue().trim().equals("是")) {
                effectiveImmediately();
            } else {
                List<ConfigInfoModel> listQueryArrayConfigInfo = queryArrayConfigInfo();
                if (listQueryArrayConfigInfo != null && listQueryArrayConfigInfo.size() > 0) {
                    for (ConfigInfoModel configInfoModel : listQueryArrayConfigInfo) {
                        String strTrim = configInfoModel.getConfigItem().trim();
                        strTrim.hashCode();
                        switch (strTrim) {
                            case "NetAdvertPort":
                            case "NetAdvertUser":
                            case "NetAdvertID":
                            case "NetAdvertIP":
                            case "NetDispatchPort":
                            case "NetDispatchIP":
                            case "IfNetAdvert":
                                SPUtil.getInstance(AppApplication.getContext()).saveLanguage(configInfoModel.getConfigValue());
                                break;
                            case "LanguageSettings":
                                if (!JavaUtils.isEmpty(configInfoModel.getConfigValue())) {
                                    SPUtil.getInstance(AppApplication.getContext()).saveLanguage(configInfoModel.getConfigValue());
                                    break;
                                } else {
                                    SPUtil.getInstance(AppApplication.getContext()).saveLanguage("0");
                                    break;
                                }
                                break;
                        }
                    }
                }
                SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.ISCONFIGVALID, false);
            }
            this.downLoadFTPModel.setStatus(2);
            this.downLoadFTPModelDao.update(this.downLoadFTPModel);
            this.intent.putExtra("operation", 1);
            sendBroadcast(this.intent);
            return;
        }
        String str = this.downLoadFTPModel.getServerAddress() + this.downLoadFTPModel.getServerAddressPort() + this.downLoadFTPModel.getUrl();
        String str2 = AppApplication.fileAnalyticStatistics.get(str);
        if (str2 != null) {
            int i = Integer.parseInt(str2) + 1;
            AppApplication.fileAnalyticStatistics.put(str, String.valueOf(i));
            if (i > 2) {
                this.downLoadFTPModel.setStatus(5);
                this.downLoadFTPModelDao.update(this.downLoadFTPModel);
                this.reportInfoModel.setUpgradeStatus(5);
                this.reportInfoModel.setUpgradeProgress(0);
                SocketManage.getInstance().sendReq(Generate808ReqPackage.generateUpgradeNotification(this.reportInfoModel));
                this.intent.putExtra("operation", 7);
                sendBroadcast(this.intent);
                return;
            }
            return;
        }
        AppApplication.fileAnalyticStatistics.put(str, "1");
    }

    private ConfigInfoModel queryConfigInfoModel(String str) {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getConfigInfoModelDao().queryBuilder().where(ConfigInfoModelDao.Properties.ConfigItem.eq(str), new WhereCondition[0]).unique();
    }

    private void effectiveImmediately() {
        List<ConfigInfoModel> listQueryArrayConfigInfo = queryArrayConfigInfo();
        if (listQueryArrayConfigInfo != null && listQueryArrayConfigInfo.size() > 0) {
            for (ConfigInfoModel configInfoModel : listQueryArrayConfigInfo) {
                if (!configInfoModel.getConfigItem().trim().equals("LanguageSettings")) {
                    SPUserInfoUtils.put(AppApplication.getContext(), configInfoModel.getConfigItem(), configInfoModel.getConfigValue());
                } else if (!JavaUtils.isEmpty(configInfoModel.getConfigValue())) {
                    SPUtil.getInstance(AppApplication.getContext()).saveLanguage(configInfoModel.getConfigValue());
                } else {
                    SPUtil.getInstance(AppApplication.getContext()).saveLanguage("0");
                }
            }
        }
        queryArraySelectLineInfo();
        SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.ISCONFIGVALID, true);
    }

    private List<ConfigInfoModel> queryArrayConfigInfo() {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getConfigInfoModelDao().loadAll();
    }

    private void queryArraySelectLineInfo() {
        BusLineInfoModel busLineInfoModelUnique = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao().queryBuilder().where(BusLineInfoModelDao.Properties.ISelect.in(1), new WhereCondition[0]).unique();
        if (busLineInfoModelUnique != null) {
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSLINENAME, busLineInfoModelUnique.getLineName());
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSDIRECTIONNAME, busLineInfoModelUnique.getLineName() + "S");
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.LINEATTRIBUTE, Integer.valueOf(busLineInfoModelUnique.getAttribute()));
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.LINENUMBER, busLineInfoModelUnique.getLineNumber());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createCountDownLatch() {
        CountDownLatch countDownLatch = this.latch;
        if (countDownLatch != null && countDownLatch.getCount() > 0) {
            this.latch.countDown();
        }
        this.latch = new CountDownLatch(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectFailAnalysis() {
        if (this.downLoadFTPModel.getType() == 2) {
            int i = this.apkReconnection;
            if (i >= 4) {
                this.apkReconnection = 0;
                this.downLoadFTPModel.setStatus(4);
                this.downLoadFTPModelDao.update(this.downLoadFTPModel);
                this.reportInfoModel.setResponseID("8b0a");
                this.reportInfoModel.setResult(1);
                SocketManage.getInstance().sendReq(Generate808ReqPackage.generateGeneralResponse(this.reportInfoModel));
                this.intent.putExtra("operation", 3);
                sendBroadcast(this.intent);
                return;
            }
            this.apkReconnection = i + 1;
            return;
        }
        int i2 = this.busReconnection;
        if (i2 >= 4) {
            this.busReconnection = 0;
            this.downLoadFTPModel.setStatus(4);
            this.downLoadFTPModelDao.update(this.downLoadFTPModel);
            this.reportInfoModel.setResponseID("8b0a");
            this.reportInfoModel.setResult(1);
            SocketManage.getInstance().sendReq(Generate808ReqPackage.generateGeneralResponse(this.reportInfoModel));
            return;
        }
        this.busReconnection = i2 + 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initDownLoadInfo(long j) {
        DownLoadInfoModel downLoadInfoModel = this.downLoadInfoModel;
        if (downLoadInfoModel != null) {
            downLoadInfoModel.setStart_pos(0L);
            this.downLoadInfoModel.setCompelete_size(0L);
            this.downLoadInfoModel.setEnd_pos(Long.valueOf(j));
            this.downLoadManage.updataInfos(this.downLoadInfoModel);
            return;
        }
        DownLoadInfoModel downLoadInfoModel2 = new DownLoadInfoModel();
        downLoadInfoModel2.setThread_id(1);
        downLoadInfoModel2.setStart_pos(0L);
        downLoadInfoModel2.setEnd_pos(Long.valueOf(j));
        downLoadInfoModel2.setCompelete_size(0L);
        downLoadInfoModel2.setUrl(this.downLoadFTPModel.getServerAddress() + this.downLoadFTPModel.getServerAddressPort() + this.downLoadFTPModel.getUrl());
        downLoadInfoModel2.setFile_createtime(String.valueOf(System.currentTimeMillis()));
        this.downLoadManage.saveInfo(downLoadInfoModel2);
        List<DownLoadInfoModel> infos = this.downLoadManage.getInfos(downLoadInfoModel2.getUrl());
        if (infos == null || infos.size() <= 0) {
            return;
        }
        this.downLoadInfoModel = infos.get(0);
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        FtpUtil ftpUtil = this.ftpUtil;
        if (ftpUtil != null) {
            ftpUtil.isFtpClose = true;
            this.ftpUtil = null;
        }
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
        FtpUtil ftpUtil = this.ftpUtil;
        if (ftpUtil != null) {
            ftpUtil.isFtpClose = true;
            this.ftpUtil = null;
        }
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
