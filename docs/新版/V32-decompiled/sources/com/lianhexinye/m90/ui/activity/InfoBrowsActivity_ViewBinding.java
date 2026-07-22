package com.lianhexinye.m90.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class InfoBrowsActivity_ViewBinding implements Unbinder {
    private InfoBrowsActivity target;

    public InfoBrowsActivity_ViewBinding(InfoBrowsActivity infoBrowsActivity) {
        this(infoBrowsActivity, infoBrowsActivity.getWindow().getDecorView());
    }

    public InfoBrowsActivity_ViewBinding(InfoBrowsActivity infoBrowsActivity, View view) {
        this.target = infoBrowsActivity;
        infoBrowsActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        infoBrowsActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        infoBrowsActivity.rlToolbar = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlToolbar, "field 'rlToolbar'", RelativeLayout.class);
        infoBrowsActivity.vInfo = Utils.findRequiredView(view, R.id.vInfo, "field 'vInfo'");
        infoBrowsActivity.lvInfoMessage = (ListView) Utils.findRequiredViewAsType(view, R.id.lvInfoMessage, "field 'lvInfoMessage'", ListView.class);
        infoBrowsActivity.tvInfoMessageTip = (TextView) Utils.findRequiredViewAsType(view, R.id.tvInfoMessageTip, "field 'tvInfoMessageTip'", TextView.class);
        infoBrowsActivity.lyNotInfoMessage = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyNotInfoMessage, "field 'lyNotInfoMessage'", LinearLayout.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        InfoBrowsActivity infoBrowsActivity = this.target;
        if (infoBrowsActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        infoBrowsActivity.toolbarTitle = null;
        infoBrowsActivity.toolbar = null;
        infoBrowsActivity.rlToolbar = null;
        infoBrowsActivity.vInfo = null;
        infoBrowsActivity.lvInfoMessage = null;
        infoBrowsActivity.tvInfoMessageTip = null;
        infoBrowsActivity.lyNotInfoMessage = null;
    }
}
