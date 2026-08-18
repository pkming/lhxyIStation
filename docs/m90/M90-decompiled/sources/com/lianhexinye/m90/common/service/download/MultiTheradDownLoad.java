package com.lianhexinye.m90.common.service.download;

import android.util.Log;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.FileUtils;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.common.utils.language.SPUtil;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModelDao;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModelDao;
import com.lianhexinye.m90.greendao.gen.DownLoadInfoModel;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.greendao.query.WhereCondition;

/* JADX INFO: loaded from: classes2.dex */
public class MultiTheradDownLoad {
    private long busMediaId;
    private DownLoadCallback downLoadCallback;
    private String downLoadPath;
    private CountDownLatch latch;
    private String localFilePrefixPath;
    private int threadNum;
    private String localFilePath = null;
    private long fileLength = 0;
    private URL url = null;
    private DownLoadManage downLoadManage = DownLoadManage.getInstance();

    public MultiTheradDownLoad(String str, CountDownLatch countDownLatch, int i, long j, DownLoadCallback downLoadCallback, String str2) {
        this.downLoadPath = null;
        this.threadNum = 0;
        this.latch = null;
        this.localFilePrefixPath = null;
        this.downLoadPath = str;
        this.threadNum = i;
        this.latch = countDownLatch;
        this.busMediaId = j;
        this.downLoadCallback = downLoadCallback;
        this.localFilePrefixPath = str2;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:141:0x0186 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:142:0x01b0 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:147:0x010a A[SYNTHETIC] */
    /*  JADX ERROR: UnsupportedOperationException in pass: RegionMakerVisitor
        java.lang.UnsupportedOperationException
        	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1068)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker$1.leaveRegion(SwitchRegionMaker.java:390)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:23)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaksForCase(SwitchRegionMaker.java:370)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaks(SwitchRegionMaker.java:85)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.leaveRegion(PostProcessRegions.java:33)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:19)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.process(PostProcessRegions.java:23)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:31)
        */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.util.List<com.lianhexinye.m90.greendao.gen.DownLoadInfoModel> checkUpFile(int r11) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 780
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.common.service.download.MultiTheradDownLoad.checkUpFile(int):java.util.List");
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

    private List<DownLoadInfoModel> initDownLoadInfo() {
        long j = this.fileLength / ((long) this.threadNum);
        ArrayList arrayList = new ArrayList();
        String strValueOf = String.valueOf(System.currentTimeMillis());
        int i = 0;
        while (i < this.threadNum - 1) {
            DownLoadInfoModel downLoadInfoModel = new DownLoadInfoModel();
            downLoadInfoModel.setThread_id(i);
            downLoadInfoModel.setStart_pos(Long.valueOf(((long) i) * j));
            i++;
            downLoadInfoModel.setEnd_pos(Long.valueOf((((long) i) * j) - 1));
            downLoadInfoModel.setCompelete_size(0L);
            downLoadInfoModel.setUrl(this.downLoadPath);
            downLoadInfoModel.setFile_createtime(strValueOf);
            arrayList.add(downLoadInfoModel);
        }
        DownLoadInfoModel downLoadInfoModel2 = new DownLoadInfoModel();
        downLoadInfoModel2.setThread_id(this.threadNum - 1);
        downLoadInfoModel2.setStart_pos(Long.valueOf(((long) (this.threadNum - 1)) * j));
        downLoadInfoModel2.setEnd_pos(Long.valueOf(this.fileLength));
        downLoadInfoModel2.setCompelete_size(0L);
        downLoadInfoModel2.setUrl(this.downLoadPath);
        downLoadInfoModel2.setFile_createtime(strValueOf);
        arrayList.add(downLoadInfoModel2);
        this.downLoadManage.saveInfos(arrayList);
        FileUtils.deleteByPath(this.localFilePath);
        initDownloadFile();
        return arrayList;
    }

    private void queryArraySelectLineInfo() {
        BusLineInfoModel busLineInfoModelUnique = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao().queryBuilder().where(BusLineInfoModelDao.Properties.ISelect.in(1), new WhereCondition[0]).unique();
        if (busLineInfoModelUnique != null) {
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSLINENAME, busLineInfoModelUnique.getLineName());
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSDIRECTIONNAME, busLineInfoModelUnique.getLineName() + "S");
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.LINEATTRIBUTE, Integer.valueOf(busLineInfoModelUnique.getAttribute()));
        }
    }

    private List<ConfigInfoModel> queryArrayConfigInfo() {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getConfigInfoModelDao().loadAll();
    }

    private ConfigInfoModel queryConfigInfoModel(String str) {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getConfigInfoModelDao().queryBuilder().where(ConfigInfoModelDao.Properties.ConfigItem.eq(str), new WhereCondition[0]).unique();
    }

    private void initDownloadFile() {
        try {
            if (!new File(this.localFilePrefixPath).exists()) {
                new File(this.localFilePrefixPath).mkdirs();
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(this.localFilePath), "rwd");
            randomAccessFile.setLength(this.fileLength);
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadPart() throws Throwable {
        String str = this.downLoadPath;
        this.localFilePath = this.localFilePrefixPath + "/" + str.substring(str.lastIndexOf(47) + 1, this.downLoadPath.contains("?") ? this.downLoadPath.lastIndexOf(63) : this.downLoadPath.length());
        try {
            URL url = new URL(URLEncoder.encode(this.downLoadPath, "UTF-8").replaceAll("\\+", "%20").replaceAll("%3A", ":").replaceAll("%2F", "/"));
            this.url = url;
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setRequestMethod("GET");
            this.fileLength = httpURLConnection.getContentLength();
            httpURLConnection.disconnect();
            List<DownLoadInfoModel> listCheckUpFile = checkUpFile(0);
            if (listCheckUpFile != null && listCheckUpFile.size() > 0) {
                this.downLoadCallback.onDownLoadComplete(8);
                ExecutorService executorServiceNewCachedThreadPool = Executors.newCachedThreadPool();
                for (DownLoadInfoModel downLoadInfoModel : listCheckUpFile) {
                    if (downLoadInfoModel.getStart_pos().longValue() + downLoadInfoModel.getCompelete_size().longValue() < downLoadInfoModel.getEnd_pos().longValue()) {
                        executorServiceNewCachedThreadPool.execute(new DownLoadThread(this.localFilePath, this.downLoadPath, this.latch, downLoadInfoModel, "1"));
                    } else {
                        this.latch.countDown();
                    }
                }
                this.latch.await();
                executorServiceNewCachedThreadPool.shutdown();
            }
            checkUpFile(1);
            this.downLoadManage = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    public void downloadAPK() throws Throwable {
        String str = this.downLoadPath;
        this.localFilePath = this.localFilePrefixPath + "/" + str.substring(str.lastIndexOf(47) + 1, this.downLoadPath.contains("?") ? this.downLoadPath.lastIndexOf(63) : this.downLoadPath.length());
        try {
            String strReplaceAll = URLEncoder.encode(this.downLoadPath, "UTF-8").replaceAll("\\+", "%20").replaceAll("%3A", ":").replaceAll("%2F", "/");
            Log.d("UpAppService", "localFilePath:" + this.localFilePath);
            Log.d("UpAppService", "urlPath:" + strReplaceAll);
            URL url = new URL(strReplaceAll);
            this.url = url;
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setRequestMethod("GET");
            this.fileLength = httpURLConnection.getContentLength();
            httpURLConnection.disconnect();
            List<DownLoadInfoModel> listCheckUpFile = checkUpFile(0);
            if (listCheckUpFile != null && listCheckUpFile.size() > 0) {
                ExecutorService executorServiceNewCachedThreadPool = Executors.newCachedThreadPool();
                for (DownLoadInfoModel downLoadInfoModel : listCheckUpFile) {
                    Log.d("UpAppService", "Compelete:" + downLoadInfoModel.getCompelete_size() + ",End:" + downLoadInfoModel.getEnd_pos());
                    executorServiceNewCachedThreadPool.execute(new DownLoadThread(this.localFilePath, this.downLoadPath, this.latch, downLoadInfoModel, "2"));
                }
                this.latch.await();
                executorServiceNewCachedThreadPool.shutdown();
            }
            checkUpFile(2);
            this.downLoadManage = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }
}
