package com.lianhexinye.m90.common.service.download;

import android.mtp.MtpConstants;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.greendao.gen.DownLoadInfoModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;

/* JADX INFO: loaded from: classes2.dex */
public class DownLoadThread implements Runnable {
    private DownLoadInfoModel downLoadInfoModel;
    private URL downUrl;
    private String filePath;
    private InputStream is;
    private CountDownLatch latch;
    private String operationType;
    private String url;
    private RandomAccessFile randomAccessFile = null;
    private HttpURLConnection connection = null;
    private long compeleteSize = 0;

    public DownLoadThread(String str, String str2, CountDownLatch countDownLatch, DownLoadInfoModel downLoadInfoModel, String str3) {
        this.latch = null;
        this.url = str2;
        this.filePath = str;
        this.latch = countDownLatch;
        this.downLoadInfoModel = downLoadInfoModel;
        this.operationType = str3;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            try {
                URL url = new URL(URLEncoder.encode(this.url, "UTF-8").replaceAll("\\+", "%20").replaceAll("%3A", ":").replaceAll("%2F", "/"));
                this.downUrl = url;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                this.connection = httpURLConnection;
                httpURLConnection.setConnectTimeout(20000);
                this.connection.setReadTimeout(20000);
                this.connection.setRequestMethod("GET");
                this.connection.setRequestProperty("Range", "bytes=" + (this.downLoadInfoModel.getStart_pos().longValue() + this.downLoadInfoModel.getCompelete_size().longValue()) + "-" + this.downLoadInfoModel.getEnd_pos());
                this.connection.connect();
                RandomAccessFile randomAccessFile = new RandomAccessFile(this.filePath, "rwd");
                this.randomAccessFile = randomAccessFile;
                randomAccessFile.seek(this.downLoadInfoModel.getStart_pos().longValue() + this.downLoadInfoModel.getCompelete_size().longValue());
                this.compeleteSize = this.downLoadInfoModel.getCompelete_size().longValue();
                if (this.connection.getResponseCode() == 206) {
                    if (this.operationType.equals("1")) {
                        AppApplication.downloadUrl = this.url;
                    }
                    this.is = this.connection.getInputStream();
                    byte[] bArr = new byte[MtpConstants.DEVICE_PROPERTY_UNDEFINED];
                    while (true) {
                        int i = this.is.read(bArr);
                        if (i == -1) {
                            break;
                        }
                        this.randomAccessFile.write(bArr, 0, i);
                        long j = this.compeleteSize + ((long) i);
                        this.compeleteSize = j;
                        this.downLoadInfoModel.setCompelete_size(Long.valueOf(j));
                        DownLoadManage.getInstance().updataInfos(this.downLoadInfoModel);
                    }
                }
                if (this.randomAccessFile != null) {
                    try {
                        InputStream inputStream = this.is;
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        this.randomAccessFile.close();
                        this.connection.disconnect();
                    } catch (IOException e) {
                        e = e;
                        e.printStackTrace();
                    }
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                if (this.randomAccessFile != null) {
                    try {
                        InputStream inputStream2 = this.is;
                        if (inputStream2 != null) {
                            inputStream2.close();
                        }
                        this.randomAccessFile.close();
                        this.connection.disconnect();
                    } catch (IOException e3) {
                        e = e3;
                        e.printStackTrace();
                    }
                }
            }
            this.latch.countDown();
        } catch (Throwable th) {
            if (this.randomAccessFile != null) {
                try {
                    InputStream inputStream3 = this.is;
                    if (inputStream3 != null) {
                        inputStream3.close();
                    }
                    this.randomAccessFile.close();
                    this.connection.disconnect();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            this.latch.countDown();
            throw th;
        }
    }
}
