package com.lianhexinye.m90.common.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lianhexinye.m90.common.Constants;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.FileFilter;
import com.lianhexinye.m90.common.utils.FileUtils;
import com.lianhexinye.m90.common.utils.ZipUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class SDStateReceiver extends BroadcastReceiver {
    private FileFilter fileFilter;
    private SDResourcesUIListenner sdResourcesUIListenner;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(this, "onReceive Start");
        String[] primaryStoragePath = AndroidUtils.getPrimaryStoragePath();
        LogUtils.d(this, "onReceive paths size:" + primaryStoragePath.length);
        String strCheckSD = AndroidUtils.checkSD(primaryStoragePath, "", "SourceFile.zip", false);
        LogUtils.d(this, "onReceive fromFile:" + strCheckSD);
        if (strCheckSD == null || strCheckSD.trim().equals("")) {
            return;
        }
        new AnalysisResourcesThread(strCheckSD).start();
    }

    public void setOnSDResourcesUIListenner(SDResourcesUIListenner sDResourcesUIListenner) {
        this.sdResourcesUIListenner = sDResourcesUIListenner;
    }

    private class AnalysisResourcesThread extends Thread {
        private String resFilePath;

        public AnalysisResourcesThread(String str) {
            this.resFilePath = "";
            this.resFilePath = str;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            LogUtils.d(this, "AnalysisResourcesThread Start");
            SDStateReceiver.this.sdResourcesUIListenner.startUpdateUI();
            SDStateReceiver.this.analysisSDDate(this.resFilePath);
            SDStateReceiver.this.sdResourcesUIListenner.endUpdateUI();
            LogUtils.d(this, "AnalysisResourcesThread end");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean analysisSDDate(String str) {
        LogUtils.d(this, "analysisSDDate Start");
        if (str != null && new File(str).exists()) {
            String str2 = Constants.SD_ROOT + Constants.TMP_LOCAL_RES_PATH + File.separator + str.substring(str.lastIndexOf("/") + 1, str.lastIndexOf(".")) + ".zip";
            FileUtils.delAllFile(Constants.SD_ROOT + Constants.TMP_LOCAL_RES_PATH);
            try {
                if (FileUtils.copySDToSD(str, str2)) {
                    try {
                        String str3 = Constants.SD_ROOT + Constants.BUS_RES_PATH;
                        FileUtils.delAllFile(str3);
                        ZipUtils.unManage(str2, str3);
                        File file = new File(str3 + "/SourceFile/Bus");
                        if (file.exists()) {
                            for (File file2 : file.listFiles()) {
                            }
                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return true;
                    }
                }
            } catch (Throwable unused) {
            }
        }
        LogUtils.d(this, "analysisSDDate End");
        return false;
    }
}
