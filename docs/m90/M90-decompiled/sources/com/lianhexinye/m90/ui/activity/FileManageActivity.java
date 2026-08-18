package com.lianhexinye.m90.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.Constants;
import com.lianhexinye.m90.common.utils.language.SPUtil;
import com.lianhexinye.m90.common.widget.DialogConfirmView;
import com.lianhexinye.m90.common.widget.DialogloadView;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.lianhexinye.m90.mvp.file.FilePresenter;
import com.lianhexinye.m90.mvp.file.FileView;
import com.lianhexinye.m90.socket.SocketManage;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class FileManageActivity extends MvpActivity<FileView, FilePresenter> implements FileView {

    @BindView(R.id.butExport)
    Button butExport;

    @BindView(R.id.butExportLog)
    Button butExportLog;

    @BindView(R.id.butImport)
    Button butImport;

    @BindView(R.id.butUpgrade)
    Button butUpgrade;
    private volatile DialogConfirmView dialogConfirmView;
    private DialogloadView dialogloadView;

    @BindView(R.id.rlExportLog)
    RelativeLayout rlExportLog;
    private SDReceiver sdReceiver;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.tvFileTips)
    TextView tvFileTips;

    @BindView(R.id.tvUpgradeFile)
    TextView tvUpgradeFile;
    private final String TAG = "SiteLearnActivity";
    private String upgradeFileName = "";

    @Override // com.lianhexinye.m90.mvp.file.FileView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_file_manage);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initToolBarBack(getResources().getString(R.string.filemanage_title));
        String apk = ((FilePresenter) this.mvpPresenter).readApk();
        this.upgradeFileName = getResources().getString(R.string.file_upgrade_find);
        if (!apk.trim().equals("")) {
            this.tvUpgradeFile.setText(String.format(this.upgradeFileName, apk));
            this.butUpgrade.setTextColor(getResources().getColorStateList(R.color.c_000000));
            this.butUpgrade.setBackgroundResource(R.drawable.btn_basic_setup_bg);
            this.butUpgrade.setEnabled(true);
        }
        if (((FilePresenter) this.mvpPresenter).checkTFLogCatalogue()) {
            this.butExportLog.setTextColor(getResources().getColorStateList(R.color.c_000000));
            this.butExportLog.setBackgroundResource(R.drawable.btn_basic_setup_bg);
            this.butExportLog.setEnabled(true);
        }
        this.sdReceiver = new SDReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        registerReceiver(this.sdReceiver, intentFilter);
        this.rlExportLog.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpActivity
    public FilePresenter createPresenter() {
        return new FilePresenter(this);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override // com.lianhexinye.m90.mvp.BaseActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.index) {
            return true;
        }
        setResult(-1);
        finish();
        return true;
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [com.lianhexinye.m90.ui.activity.FileManageActivity$1] */
    @Override // com.lianhexinye.m90.mvp.file.FileView
    public void getDataSuccess(int i, int i2, String str) {
        this.tvFileTips.setText(str);
        this.tvFileTips.setVisibility(0);
        if (i == 1 && i2 == 0) {
            new Thread() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    super.run();
                    SocketManage.getInstance().closeAllSocket();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    FileManageActivity.this.runOnUiThread(new Runnable() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ((PowerManager) FileManageActivity.this.getSystemService(Context.POWER_SERVICE)).reboot("重启");
                        }
                    });
                }
            }.start();
        }
    }

    @Override // com.lianhexinye.m90.mvp.file.FileView
    public void showLoading(String str) {
        DialogloadView dialogloadView = new DialogloadView(this);
        this.dialogloadView = dialogloadView;
        dialogloadView.setTipTextView(str);
        this.dialogloadView.show();
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.file.FileView
    public void hideLoading() {
        DialogloadView dialogloadView = this.dialogloadView;
        if (dialogloadView != null) {
            dialogloadView.dismiss();
        }
    }

    @OnClick({R.id.butImport, R.id.butExport, R.id.butUpgrade, R.id.butExportLog, R.id.butDeleteLog})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.butDeleteLog) {
            this.tvFileTips.setVisibility(8);
            final File file = new File(Constants.SD_ROOT + Constants.LOG_RES_PATH + "/info.log");
            if (file.exists()) {
                this.dialogConfirmView = new DialogConfirmView.Builder(this).setContent("确定删除本地日志文件？").setButOkTxt(getResources().getString(R.string.confirm)).setOnOKClickListener(new DialogConfirmView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity.6
                    @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                    public void onOKClick() {
                        FileManageActivity.this.dialogConfirmView.dismiss();
                        file.delete();
                        FileManageActivity.this.tvFileTips.setText("删除成功");
                        FileManageActivity.this.tvFileTips.setVisibility(0);
                    }

                    @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                    public void onCancelClick() {
                        FileManageActivity.this.dialogConfirmView.dismiss();
                    }
                }).build();
                this.dialogConfirmView.show();
                return;
            } else {
                this.tvFileTips.setText("暂无日志文件");
                this.tvFileTips.setVisibility(0);
                return;
            }
        }
        if (id != R.id.butUpgrade) {
            switch (id) {
                case R.id.butExport /* 2131296342 */:
                    this.tvFileTips.setVisibility(8);
                    this.dialogConfirmView = new DialogConfirmView.Builder(this).setContent(getResources().getString(R.string.file_export_tip)).setButOkTxt(getResources().getString(R.string.confirm)).setOnOKClickListener(new DialogConfirmView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity.3
                        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                        public void onOKClick() {
                            FileManageActivity.this.dialogConfirmView.dismiss();
                            ((FilePresenter) FileManageActivity.this.mvpPresenter).fileExport();
                        }

                        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                        public void onCancelClick() {
                            FileManageActivity.this.dialogConfirmView.dismiss();
                        }
                    }).build();
                    this.dialogConfirmView.show();
                    break;
                case R.id.butExportLog /* 2131296343 */:
                    this.tvFileTips.setVisibility(8);
                    this.dialogConfirmView = new DialogConfirmView.Builder(this).setContent(getResources().getString(R.string.file_export_log_tip)).setButOkTxt(getResources().getString(R.string.confirm)).setOnOKClickListener(new DialogConfirmView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity.5
                        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                        public void onOKClick() {
                            FileManageActivity.this.dialogConfirmView.dismiss();
                            ((FilePresenter) FileManageActivity.this.mvpPresenter).fileExportLog();
                        }

                        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                        public void onCancelClick() {
                            FileManageActivity.this.dialogConfirmView.dismiss();
                        }
                    }).build();
                    this.dialogConfirmView.show();
                    break;
                case R.id.butImport /* 2131296344 */:
                    this.tvFileTips.setVisibility(8);
                    this.dialogConfirmView = new DialogConfirmView.Builder(this).setContent(getResources().getString(R.string.file_import_tip)).setButOkTxt(getResources().getString(R.string.confirm)).setOnOKClickListener(new DialogConfirmView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity.2
                        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                        public void onOKClick() {
                            FileManageActivity.this.dialogConfirmView.dismiss();
                            ((FilePresenter) FileManageActivity.this.mvpPresenter).fileImport();
                        }

                        @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
                        public void onCancelClick() {
                            FileManageActivity.this.dialogConfirmView.dismiss();
                        }
                    }).build();
                    this.dialogConfirmView.show();
                    break;
            }
            return;
        }
        this.tvFileTips.setVisibility(8);
        this.dialogConfirmView = new DialogConfirmView.Builder(this).setContent(getResources().getString(R.string.file_upgrade_tip)).setButOkTxt(getResources().getString(R.string.confirm)).setOnOKClickListener(new DialogConfirmView.OnOKCancelClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity.4
            @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
            public void onOKClick() {
                FileManageActivity.this.dialogConfirmView.dismiss();
                try {
                    SocketManage.getInstance().closeAllSocket();
                    Intent launchIntentForPackage = FileManageActivity.this.getPackageManager().getLaunchIntentForPackage("com.lianhexinye.rebootm90");
                    int selectLanguage = SPUtil.getInstance(AppApplication.getContext()).getSelectLanguage();
                    if (selectLanguage > 2) {
                        selectLanguage = 3;
                    }
                    launchIntentForPackage.putExtra("seleLanguage", "" + selectLanguage);
                    FileManageActivity.this.startActivity(launchIntentForPackage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((FilePresenter) FileManageActivity.this.mvpPresenter).apkUpgrade();
            }

            @Override // com.lianhexinye.m90.common.widget.DialogConfirmView.OnOKCancelClickListener
            public void onCancelClick() {
                FileManageActivity.this.dialogConfirmView.dismiss();
            }
        }).build();
        this.dialogConfirmView.show();
    }

    private class SDReceiver extends BroadcastReceiver {
        private SDReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String apk = ((FilePresenter) FileManageActivity.this.mvpPresenter).readApk();
            if (!apk.trim().equals("")) {
                FileManageActivity.this.tvUpgradeFile.setText(String.format(FileManageActivity.this.upgradeFileName, apk));
                FileManageActivity.this.butUpgrade.setTextColor(FileManageActivity.this.getResources().getColorStateList(R.color.c_000000));
                FileManageActivity.this.butUpgrade.setBackgroundResource(R.drawable.btn_basic_setup_bg);
                FileManageActivity.this.butUpgrade.setEnabled(true);
            } else {
                FileManageActivity.this.tvUpgradeFile.setText("");
                FileManageActivity.this.butUpgrade.setTextColor(FileManageActivity.this.getResources().getColorStateList(R.color.c_414141));
                FileManageActivity.this.butUpgrade.setBackgroundResource(R.drawable.btn_upgrade_bg);
                FileManageActivity.this.butUpgrade.setEnabled(false);
            }
            if (((FilePresenter) FileManageActivity.this.mvpPresenter).checkTFLogCatalogue()) {
                FileManageActivity.this.butExportLog.setTextColor(FileManageActivity.this.getResources().getColorStateList(R.color.c_000000));
                FileManageActivity.this.butExportLog.setBackgroundResource(R.drawable.btn_basic_setup_bg);
                FileManageActivity.this.butExportLog.setEnabled(true);
            } else {
                FileManageActivity.this.butExportLog.setTextColor(FileManageActivity.this.getResources().getColorStateList(R.color.c_414141));
                FileManageActivity.this.butExportLog.setBackgroundResource(R.drawable.btn_upgrade_bg);
                FileManageActivity.this.butExportLog.setEnabled(false);
            }
        }
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        SDReceiver sDReceiver = this.sdReceiver;
        if (sDReceiver != null) {
            unregisterReceiver(sDReceiver);
            this.sdReceiver = null;
        }
        this.dialogConfirmView = null;
    }
}
