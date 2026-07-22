package com.lianhexinye.m90.ui.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class SiteCollectionActivity_ViewBinding implements Unbinder {
    private SiteCollectionActivity target;

    public SiteCollectionActivity_ViewBinding(SiteCollectionActivity siteCollectionActivity) {
        this(siteCollectionActivity, siteCollectionActivity.getWindow().getDecorView());
    }

    public SiteCollectionActivity_ViewBinding(SiteCollectionActivity siteCollectionActivity, View view) {
        this.target = siteCollectionActivity;
        siteCollectionActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        siteCollectionActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        siteCollectionActivity.rlToolbar = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlToolbar, "field 'rlToolbar'", RelativeLayout.class);
        siteCollectionActivity.rbRadioSiteLearn = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_site_learn, "field 'rbRadioSiteLearn'", RadioButton.class);
        siteCollectionActivity.rbRadioOther = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_radio_other, "field 'rbRadioOther'", RadioButton.class);
        siteCollectionActivity.flSiteContext = (FrameLayout) Utils.findRequiredViewAsType(view, R.id.flSiteContext, "field 'flSiteContext'", FrameLayout.class);
        siteCollectionActivity.rgRadioLearnNavigation = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.rg_radio_learn_navigation, "field 'rgRadioLearnNavigation'", RadioGroup.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        SiteCollectionActivity siteCollectionActivity = this.target;
        if (siteCollectionActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        siteCollectionActivity.toolbarTitle = null;
        siteCollectionActivity.toolbar = null;
        siteCollectionActivity.rlToolbar = null;
        siteCollectionActivity.rbRadioSiteLearn = null;
        siteCollectionActivity.rbRadioOther = null;
        siteCollectionActivity.flSiteContext = null;
        siteCollectionActivity.rgRadioLearnNavigation = null;
    }
}
