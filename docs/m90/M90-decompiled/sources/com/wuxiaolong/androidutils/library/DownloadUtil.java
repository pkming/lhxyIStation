package com.wuxiaolong.androidutils.library;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

/* JADX INFO: loaded from: classes2.dex */
public class DownloadUtil {
    private static DownloadManager downloadManager;
    private static long myReference;
    private String downloadFileName = "weiyan.apk";
    private DownloadManager.Request downloadRequest;
    private Context mContext;

    public DownloadUtil(Context context, String str) {
        this.mContext = context;
        initDownload(str);
    }

    private void initDownload(String str) {
        downloadManager = (DownloadManager) this.mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
        this.downloadRequest = request;
        request.setDestinationInExternalFilesDir(this.mContext, Environment.DIRECTORY_DOWNLOADS, this.downloadFileName);
        this.downloadRequest.allowScanningByMediaScanner();
        this.downloadRequest.setVisibleInDownloadsUi(true);
        this.downloadRequest.setAllowedNetworkTypes(2);
        this.downloadRequest.setMimeType("application/vnd.android.package-archive");
        this.downloadRequest.setNotificationVisibility(2);
        this.downloadRequest.setTitle("");
        this.downloadRequest.setDescription("");
    }

    public void start() {
        myReference = downloadManager.enqueue(this.downloadRequest);
    }

    public static class DownloadManagerReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED")) {
                for (long j : intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS)) {
                    long unused = DownloadUtil.myReference;
                }
            }
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long longExtra = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
                if (longExtra == DownloadUtil.myReference) {
                    Cursor cursorQuery = DownloadUtil.downloadManager.query(new DownloadManager.Query().setFilterById(longExtra));
                    cursorQuery.moveToFirst();
                    String string = cursorQuery.getString(cursorQuery.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    cursorQuery.close();
                    if (string != null) {
                        if (string.contains(context.getPackageName())) {
                            AppUtils.installAPK(context, string.trim().substring(7));
                            return;
                        }
                        return;
                    }
                    Toast.makeText(context, "网络不给力", 0).show();
                }
            }
        }
    }

    public void setDownloadFileName(String str) {
        this.downloadRequest.setDestinationInExternalFilesDir(this.mContext, Environment.DIRECTORY_DOWNLOADS, str);
    }

    public void setNotificationTitle(CharSequence charSequence) {
        this.downloadRequest.setTitle(charSequence);
    }

    public void setNotificationDescription(CharSequence charSequence) {
        this.downloadRequest.setDescription(charSequence);
    }

    public void setNotificationVisibility(int i) {
        this.downloadRequest.setNotificationVisibility(i);
    }

    public DownloadManager.Request getDownloadRequest() {
        return this.downloadRequest;
    }
}
