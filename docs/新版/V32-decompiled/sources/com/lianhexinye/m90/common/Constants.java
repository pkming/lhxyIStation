package com.lianhexinye.m90.common;

import android.os.Environment;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class Constants {
    public static final String ADVERT_RES_PATH = "M90/Advert";
    public static final String APK_RES_NAME = "M90.apk";
    public static final String APK_RES_PATH = "M90/ApkRes";
    public static final String APK_RES_PATH_NEW;
    public static final String APP_PATH = "M90";
    public static final String BUS_RES_PATH = "M90/BusRes";
    public static final String BUS_RES_SFile_PATH = "M90/BusRes/SourceFile";
    public static final int FTP_CONNECT_FAIL = 1;
    public static final int FTP_CONNECT_SUCCESS = 0;
    public static final int FTP_DELETEFILE_FAIL = 17;
    public static final int FTP_DELETEFILE_SUCCESS = 16;
    public static final int FTP_DISCONNECT_SUCCESS = 2;
    public static final int FTP_DOWN_FAIL = 9;
    public static final int FTP_DOWN_INIT = 20;
    public static final int FTP_DOWN_LOADING = 7;
    public static final int FTP_DOWN_SUCCESS = 8;
    public static final int FTP_FILE_NOTEXISTS = 3;
    public static final int FTP_LISTFILE_FAIL = 19;
    public static final int FTP_LISTFILE_SUCCESS = 18;
    public static final int FTP_UPLOAD_FAIL = 5;
    public static final int FTP_UPLOAD_LOADING = 6;
    public static final int FTP_UPLOAD_SUCCESS = 4;
    public static final String LOG_RES_PATH = "M90/Log";
    public static final int MIX_BDS_GPS_NO = 48;
    public static final int MIX_BDS_GPS_YES = 49;
    public static final String MONITOR_VIDEO_RES_PATH = "M90/MonitorVideo";
    public static final String SD_ROOT;
    public static final int SINGLE_BDS_NO = 16;
    public static final int SINGLE_BDS_YES = 17;
    public static final int SINGLE_GPS_NO = 32;
    public static final int SINGLE_GPS_YES = 33;
    public static final String TMP_LOCAL_RES_PATH = "M90/TmpLocalRes";
    public static final String TMP_NETWORK_RES_PATH = "M90/TmpNetWorkRes";
    public static final String TTS_KEY = "dwinvcmxonftxk6xr3x5qdkfmi6p3akqtyinfuye";
    public static final String TTS_PATH = "M90/TTSVoicePkt";
    public static final String TTS_SECRET = "5187bab7d633015ba4bd2c3d6297bebf";

    static {
        String str = Environment.getExternalStorageDirectory().getPath() + File.separator;
        SD_ROOT = str;
        APK_RES_PATH_NEW = str + APK_RES_PATH;
    }
}
