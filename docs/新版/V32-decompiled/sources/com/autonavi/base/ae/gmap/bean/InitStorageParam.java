package com.autonavi.base.ae.gmap.bean;

import android.text.format.DateFormat;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class InitStorageParam {
    private static final String DIR_NAME = "terrain_dem_files";
    private static final String INIT_STORAGE_DIR = "mapcache";
    private int maxFileCount;
    private long maxFileSize;
    private String path;
    private int version;

    public static class Holder {
        private static String sPath;

        public static void initPath(String str) {
            File file = new File(str);
            File file2 = new File(file, InitStorageParam.INIT_STORAGE_DIR);
            if (file.isDirectory()) {
                if (!file2.exists()) {
                    file2.mkdir();
                }
                File file3 = new File(file2, InitStorageParam.DIR_NAME);
                if (!file3.exists()) {
                    file3.mkdir();
                }
                sPath = file3.getAbsolutePath();
            }
        }
    }

    public static InitStorageParam obtain() {
        return new InitStorageParam();
    }

    public static InitStorageParam obtain(int i, String str, int i2, long j) {
        return new InitStorageParam(i, str, i2, j);
    }

    private InitStorageParam(int i, String str, int i2, long j) {
        this.version = i;
        this.path = str;
        this.maxFileCount = i2;
        this.maxFileSize = j;
    }

    private InitStorageParam() {
        this(1, Holder.sPath, 1000, 300000L);
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int i) {
        this.version = i;
    }

    public int getMaxFileCount() {
        return this.maxFileCount;
    }

    public void setMaxFileCount(int i) {
        this.maxFileCount = i;
    }

    public long getMaxFileSize() {
        return this.maxFileSize;
    }

    public void setMaxFileSize(long j) {
        this.maxFileSize = j;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    public String toString() {
        return "InitStorageParam{version=" + this.version + ", maxFileCount=" + this.maxFileCount + ", maxFileSize=" + this.maxFileSize + ", path='" + this.path + DateFormat.QUOTE + '}';
    }
}
