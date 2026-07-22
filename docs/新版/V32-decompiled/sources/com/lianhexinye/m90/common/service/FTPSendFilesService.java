package com.lianhexinye.m90.common.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.TimedRemoteCaller;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.Constants;
import com.lianhexinye.m90.common.utils.FtpUtil;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import com.lianhexinye.m90.socket.SocketManage;
import com.lianhexinye.m90.socket.request.Generate808ReqPackage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class FTPSendFilesService extends BaseService {
    private FtpUtil ftpUtil;
    private ReportInfoModel reportInfoModel;
    private final String TAG = "FTPSendFilesService";
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private CountDownLatch latch = null;
    private Intent intent = new Intent("com.lianhexinye.m90.ui.activity.MsgReceiver");
    private long noticeTime = 0;

    @Override // com.lianhexinye.m90.common.service.BaseService, android.app.Service
    public IBinder onBind(final Intent intent) {
        LogUtils.d("FTPSendFilesService", "onBind");
        ReportInfoModel reportInfoModel = new ReportInfoModel();
        this.reportInfoModel = reportInfoModel;
        reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
        this.executorService.scheduleWithFixedDelay(new Runnable() { // from class: com.lianhexinye.m90.common.service.FTPSendFilesService.1
            @Override // java.lang.Runnable
            public void run() {
                String str = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGSENDSTATUS, "0");
                String str2 = Constants.SD_ROOT + Constants.LOG_RES_PATH + "/info.log";
                if (str2 != null && str2.length() > 0 && new File(str2).exists()) {
                    if (str.equals("1")) {
                        LogUtils.d("FTPSendFilesService", "准备上传日志。。。。。");
                        FTPSendFilesService.this.upFile(str2);
                        return;
                    }
                    return;
                }
                LogUtils.d("FTPSendFilesService", "日志文件不存在。。。。。");
                SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.LOGSENDSTATUS, "0");
                intent.putExtra("operation", 16);
                FTPSendFilesService.this.sendBroadcast(intent);
                String str3 = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGTASK, "000000000000000a");
                String str4 = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGTIME, "240906");
                FTPSendFilesService.this.reportInfoModel.setLogTask(str3);
                FTPSendFilesService.this.reportInfoModel.setLogTime(str4);
                FTPSendFilesService.this.reportInfoModel.setLogStatus(7);
                FTPSendFilesService.this.reportInfoModel.setLogProgress(0);
                SocketManage.getInstance().sendReq(Generate808ReqPackage.generateLogNotification(FTPSendFilesService.this.reportInfoModel));
            }
        }, 10L, 50L, TimeUnit.SECONDS);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void upFile(String str) {
        String str2 = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGIP, "123.60.161.161");
        int i = Integer.parseInt(SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGPORT, "40000").toString());
        String str3 = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGPATH, "/rizhi");
        String str4 = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGUSERNAME, "test");
        String str5 = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGPASSWORD, "0f17rvSLsHl5ZjkmQbr123Sb");
        final String str6 = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGTASK, "000000000000000a");
        final String str7 = (String) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LOGTIME, "240906");
        this.ftpUtil = new FtpUtil(str2, i, str4, str5);
        this.latch = new CountDownLatch(1);
        try {
            this.ftpUtil.uploadFtpSingleFile(new File(str), str3, new FtpUtil.FtpTcpProgressListener() { // from class: com.lianhexinye.m90.common.service.FTPSendFilesService.2
                @Override // com.lianhexinye.m90.common.utils.FtpUtil.FtpTcpProgressListener
                public void onFtpTcpProgress(int i2, long j, File file) {
                    if (i2 == 4) {
                        LogUtils.d("FTP_UPLOAD_SUCCESS", "上传成功。。。。。进度：" + j);
                        SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.LOGSENDSTATUS, "0");
                        FTPSendFilesService.this.reportInfoModel.setLogTask(str6);
                        FTPSendFilesService.this.reportInfoModel.setLogTime(str7);
                        FTPSendFilesService.this.reportInfoModel.setLogStatus(1);
                        FTPSendFilesService.this.reportInfoModel.setLogProgress(100);
                        SocketManage.getInstance().sendReq(Generate808ReqPackage.generateLogNotification(FTPSendFilesService.this.reportInfoModel));
                        if (FTPSendFilesService.this.latch == null || FTPSendFilesService.this.latch.getCount() <= 0) {
                            return;
                        }
                        FTPSendFilesService.this.latch.countDown();
                        return;
                    }
                    if (i2 == 6) {
                        LogUtils.d("FTP_UPLOAD_LOADING", "进度:" + j);
                        if (System.currentTimeMillis() - FTPSendFilesService.this.noticeTime >= TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS) {
                            FTPSendFilesService.this.noticeTime = System.currentTimeMillis();
                            FTPSendFilesService.this.reportInfoModel.setLogTask(str6);
                            FTPSendFilesService.this.reportInfoModel.setLogTime(str7);
                            FTPSendFilesService.this.reportInfoModel.setLogStatus(6);
                            FTPSendFilesService.this.reportInfoModel.setLogProgress((int) j);
                            SocketManage.getInstance().sendReq(Generate808ReqPackage.generateLogNotification(FTPSendFilesService.this.reportInfoModel));
                            return;
                        }
                        return;
                    }
                    if (i2 == 5 || i2 == 1 || i2 == 3) {
                        FTPSendFilesService.this.reportInfoModel.setLogTask(str6);
                        FTPSendFilesService.this.reportInfoModel.setLogTime(str7);
                        FTPSendFilesService.this.reportInfoModel.setLogStatus(4);
                        FTPSendFilesService.this.reportInfoModel.setLogProgress((int) j);
                        SocketManage.getInstance().sendReq(Generate808ReqPackage.generateLogNotification(FTPSendFilesService.this.reportInfoModel));
                        LogUtils.d("FTPSendFilesService", "currentStatus：" + i2);
                        if (FTPSendFilesService.this.latch == null || FTPSendFilesService.this.latch.getCount() <= 0) {
                            return;
                        }
                        FTPSendFilesService.this.latch.countDown();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.d("FTPSendFilesService", "ftp上传异常：" + e.getMessage());
            CountDownLatch countDownLatch = this.latch;
            if (countDownLatch != null && countDownLatch.getCount() > 0) {
                this.latch.countDown();
            }
        }
        try {
            this.latch.await();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
            CountDownLatch countDownLatch2 = this.latch;
            if (countDownLatch2 == null || countDownLatch2.getCount() <= 0) {
                return;
            }
            this.latch.countDown();
        }
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        LogUtils.d("FTPSendFilesService", "onUnbind");
        if (this.latch != null) {
            while (this.latch.getCount() > 0) {
                this.latch.countDown();
            }
            this.latch = null;
        }
        FtpUtil ftpUtil = this.ftpUtil;
        if (ftpUtil != null) {
            try {
                ftpUtil.closeConnect();
                this.ftpUtil = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        FtpUtil ftpUtil = this.ftpUtil;
        if (ftpUtil != null) {
            try {
                ftpUtil.closeConnect();
                this.ftpUtil = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ScheduledExecutorService scheduledExecutorService = this.executorService;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            this.executorService = null;
        }
    }
}
