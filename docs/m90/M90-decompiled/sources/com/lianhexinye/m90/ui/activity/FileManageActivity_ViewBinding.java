package com.lianhexinye.m90.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class FileManageActivity_ViewBinding implements Unbinder {
    private FileManageActivity target;
    private View view7f090051;
    private View view7f090056;
    private View view7f090057;
    private View view7f090058;
    private View view7f09007d;

    public FileManageActivity_ViewBinding(FileManageActivity fileManageActivity) {
        this(fileManageActivity, fileManageActivity.getWindow().getDecorView());
    }

    public FileManageActivity_ViewBinding(final FileManageActivity fileManageActivity, View view) {
        this.target = fileManageActivity;
        fileManageActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        fileManageActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butImport, "field 'butImport' and method 'onViewClicked'");
        fileManageActivity.butImport = (Button) Utils.castView(viewFindRequiredView, R.id.butImport, "field 'butImport'", Button.class);
        this.view7f090058 = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                fileManageActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView2 = Utils.findRequiredView(view, R.id.butExport, "field 'butExport' and method 'onViewClicked'");
        fileManageActivity.butExport = (Button) Utils.castView(viewFindRequiredView2, R.id.butExport, "field 'butExport'", Button.class);
        this.view7f090056 = viewFindRequiredView2;
        viewFindRequiredView2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                fileManageActivity.onViewClicked(view2);
            }
        });
        fileManageActivity.tvFileTips = (TextView) Utils.findRequiredViewAsType(view, R.id.tvFileTips, "field 'tvFileTips'", TextView.class);
        View viewFindRequiredView3 = Utils.findRequiredView(view, R.id.butUpgrade, "field 'butUpgrade' and method 'onViewClicked'");
        fileManageActivity.butUpgrade = (Button) Utils.castView(viewFindRequiredView3, R.id.butUpgrade, "field 'butUpgrade'", Button.class);
        this.view7f09007d = viewFindRequiredView3;
        viewFindRequiredView3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity_ViewBinding.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                fileManageActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView4 = Utils.findRequiredView(view, R.id.butExportLog, "field 'butExportLog' and method 'onViewClicked'");
        fileManageActivity.butExportLog = (Button) Utils.castView(viewFindRequiredView4, R.id.butExportLog, "field 'butExportLog'", Button.class);
        this.view7f090057 = viewFindRequiredView4;
        viewFindRequiredView4.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity_ViewBinding.4
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                fileManageActivity.onViewClicked(view2);
            }
        });
        fileManageActivity.tvUpgradeFile = (TextView) Utils.findRequiredViewAsType(view, R.id.tvUpgradeFile, "field 'tvUpgradeFile'", TextView.class);
        fileManageActivity.rlExportLog = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlExportLog, "field 'rlExportLog'", RelativeLayout.class);
        View viewFindRequiredView5 = Utils.findRequiredView(view, R.id.butDeleteLog, "method 'onViewClicked'");
        this.view7f090051 = viewFindRequiredView5;
        viewFindRequiredView5.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.FileManageActivity_ViewBinding.5
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                fileManageActivity.onViewClicked(view2);
            }
        });
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        FileManageActivity fileManageActivity = this.target;
        if (fileManageActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        fileManageActivity.toolbarTitle = null;
        fileManageActivity.toolbar = null;
        fileManageActivity.butImport = null;
        fileManageActivity.butExport = null;
        fileManageActivity.tvFileTips = null;
        fileManageActivity.butUpgrade = null;
        fileManageActivity.butExportLog = null;
        fileManageActivity.tvUpgradeFile = null;
        fileManageActivity.rlExportLog = null;
        this.view7f090058.setOnClickListener(null);
        this.view7f090058 = null;
        this.view7f090056.setOnClickListener(null);
        this.view7f090056 = null;
        this.view7f09007d.setOnClickListener(null);
        this.view7f09007d = null;
        this.view7f090057.setOnClickListener(null);
        this.view7f090057 = null;
        this.view7f090051.setOnClickListener(null);
        this.view7f090051 = null;
    }
}
