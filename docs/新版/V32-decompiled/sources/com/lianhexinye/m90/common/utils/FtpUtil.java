package com.lianhexinye.m90.common.utils;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

/* JADX INFO: loaded from: classes2.dex */
public class FtpUtil {
    private static final boolean DEBUG = true;
    private static final String TAG = "FtpUtil";
    private FTPClient ftpClient;
    private String hostName;
    public boolean isFtpClose;
    private String password;
    private int serverPort;
    private String userName;

    public interface FtpDeleteFileListener {
        void onFtpDelete(int i);
    }

    public interface FtpListFileListener {
        void onFtpListFile(int i, List<FTPFile> list);
    }

    public interface FtpProgressListener {
        void onFtpProgress(int i, long j, long j2, long j3, File file);
    }

    public interface FtpTcpProgressListener {
        void onFtpTcpProgress(int i, long j, File file);
    }

    public FtpUtil(String str) {
        this.serverPort = 8090;
        this.userName = "root";
        this.password = "xixunled";
        this.isFtpClose = false;
        this.hostName = str;
        this.ftpClient = new FTPClient();
    }

    public FtpUtil(String str, int i, String str2, String str3) {
        this.serverPort = 8090;
        this.userName = "root";
        this.password = "xixunled";
        this.isFtpClose = false;
        this.hostName = str;
        this.serverPort = i;
        this.userName = str2;
        this.password = str3;
        this.ftpClient = new FTPClient();
    }

    public void uploadSingleFile(File file, String str, FtpProgressListener ftpProgressListener) throws IOException {
        uploadBeforeOperate(str, ftpProgressListener);
        if (uploadingSingle(file, ftpProgressListener)) {
            ftpProgressListener.onFtpProgress(4, 0L, 0L, 0L, file);
        } else {
            ftpProgressListener.onFtpProgress(5, 0L, 0L, 0L, file);
        }
        uploadAfterOperate(ftpProgressListener);
    }

    public void uploadFtpSingleFile(File file, String str, FtpTcpProgressListener ftpTcpProgressListener) throws IOException {
        uploadFtpBeforeOperate(str, ftpTcpProgressListener);
        if (uploadingFtpSingle(file, ftpTcpProgressListener)) {
            ftpTcpProgressListener.onFtpTcpProgress(4, 100L, file);
        } else {
            ftpTcpProgressListener.onFtpTcpProgress(5, 0L, file);
        }
        uploadFtpAfterOperate(ftpTcpProgressListener);
    }

    public void uploadMultiFile(LinkedList<File> linkedList, String str, FtpProgressListener ftpProgressListener) throws IOException {
        uploadBeforeOperate(str, ftpProgressListener);
        for (File file : linkedList) {
            if (uploadingSingle(file, ftpProgressListener)) {
                ftpProgressListener.onFtpProgress(4, 0L, 0L, 0L, file);
            } else {
                ftpProgressListener.onFtpProgress(5, 0L, 0L, 0L, file);
            }
        }
        uploadAfterOperate(ftpProgressListener);
    }

    private boolean uploadingSingle(File file, FtpProgressListener ftpProgressListener) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        boolean zStoreFile = this.ftpClient.storeFile(file.getName(), new ProgressInputStream(bufferedInputStream, ftpProgressListener, file));
        bufferedInputStream.close();
        return zStoreFile;
    }

    private boolean uploadingFtpSingle(File file, FtpTcpProgressListener ftpTcpProgressListener) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        boolean zStoreFile = this.ftpClient.storeFile(file.getName(), new ProgressFtpInputStream(bufferedInputStream, ftpTcpProgressListener, file));
        bufferedInputStream.close();
        return zStoreFile;
    }

    private void uploadBeforeOperate(String str, FtpProgressListener ftpProgressListener) throws IOException {
        try {
            openConnect();
            ftpProgressListener.onFtpProgress(0, 0L, 0L, 0L, null);
            this.ftpClient.setFileTransferMode(10);
            this.ftpClient.makeDirectory(str);
            this.ftpClient.changeWorkingDirectory(str);
        } catch (IOException e) {
            e.printStackTrace();
            ftpProgressListener.onFtpProgress(1, 0L, 0L, 0L, null);
        }
    }

    private void uploadFtpBeforeOperate(String str, FtpTcpProgressListener ftpTcpProgressListener) throws IOException {
        try {
            openConnect();
            ftpTcpProgressListener.onFtpTcpProgress(0, 0L, null);
            this.ftpClient.setFileTransferMode(10);
            this.ftpClient.makeDirectory(str);
            this.ftpClient.changeWorkingDirectory(str);
        } catch (IOException e) {
            e.printStackTrace();
            ftpTcpProgressListener.onFtpTcpProgress(1, 0L, null);
        }
    }

    private void uploadAfterOperate(FtpProgressListener ftpProgressListener) throws IOException {
        closeConnect();
        ftpProgressListener.onFtpProgress(2, 0L, 0L, 0L, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x010d  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x01bd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void downloadSingleFile(java.lang.String r23, java.lang.String r24, long r25, long r27, com.lianhexinye.m90.common.utils.FtpUtil.FtpProgressListener r29) throws java.lang.Exception {
        /*
            Method dump skipped, instruction units count: 544
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.common.utils.FtpUtil.downloadSingleFile(java.lang.String, java.lang.String, long, long, com.lianhexinye.m90.common.utils.FtpUtil$FtpProgressListener):void");
    }

    public void deleteSingleFile(String str, FtpDeleteFileListener ftpDeleteFileListener) throws Exception {
        try {
            openConnect();
            ftpDeleteFileListener.onFtpDelete(0);
            if (this.ftpClient.listFiles(str).length == 0) {
                ftpDeleteFileListener.onFtpDelete(3);
                return;
            }
            if (this.ftpClient.deleteFile(str)) {
                ftpDeleteFileListener.onFtpDelete(16);
            } else {
                ftpDeleteFileListener.onFtpDelete(17);
            }
            closeConnect();
            ftpDeleteFileListener.onFtpDelete(2);
        } catch (IOException e) {
            e.printStackTrace();
            ftpDeleteFileListener.onFtpDelete(1);
        }
    }

    public List<FTPFile> listsFiles(String str, FTPFileFilter fTPFileFilter, FtpListFileListener ftpListFileListener) throws Exception {
        ArrayList arrayList = new ArrayList();
        try {
            openConnect();
            ftpListFileListener.onFtpListFile(0, arrayList);
            Log.d(TAG, "--FTP_CONNECT_SUCCESS--");
            FTPFile[] fTPFileArrListFiles = this.ftpClient.listFiles(str, fTPFileFilter);
            if (fTPFileArrListFiles.length == 0) {
                ftpListFileListener.onFtpListFile(19, arrayList);
            } else {
                Log.d(TAG, "--FTP_LISTFILE_SUCCESS--");
                for (FTPFile fTPFile : fTPFileArrListFiles) {
                    Log.d(TAG, "ftpfile = " + fTPFile.getName());
                }
                for (FTPFile fTPFile2 : fTPFileArrListFiles) {
                    arrayList.add(fTPFile2);
                }
                ftpListFileListener.onFtpListFile(18, arrayList);
            }
            closeConnect();
            ftpListFileListener.onFtpListFile(2, arrayList);
            return arrayList;
        } catch (IOException e) {
            e.printStackTrace();
            ftpListFileListener.onFtpListFile(1, arrayList);
            return null;
        }
    }

    public void openConnect() throws IOException {
        this.ftpClient.setDataTimeout(20000);
        this.ftpClient.setDefaultTimeout(20000);
        this.ftpClient.setConnectTimeout(20000);
        this.ftpClient.setBufferSize(20971520);
        this.ftpClient.setControlEncoding("UTF-8");
        this.ftpClient.connect(this.hostName, this.serverPort);
        int replyCode = this.ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            this.ftpClient.disconnect();
            throw new IOException("connect fail: " + replyCode);
        }
        this.ftpClient.login(this.userName, this.password);
        int replyCode2 = this.ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode2)) {
            this.ftpClient.disconnect();
            throw new IOException("connect fail: " + replyCode2);
        }
        FTPClientConfig fTPClientConfig = new FTPClientConfig(this.ftpClient.getSystemType().split(" ")[0]);
        fTPClientConfig.setServerLanguageCode("zh");
        this.ftpClient.configure(fTPClientConfig);
        this.ftpClient.enterLocalPassiveMode();
        this.ftpClient.setFileType(2);
    }

    public void closeConnect() throws IOException {
        FTPClient fTPClient = this.ftpClient;
        if (fTPClient != null) {
            fTPClient.logout();
            this.ftpClient.disconnect();
        }
    }

    private void uploadFtpAfterOperate(FtpTcpProgressListener ftpTcpProgressListener) throws IOException {
        closeConnect();
        ftpTcpProgressListener.onFtpTcpProgress(2, 0L, null);
    }
}
