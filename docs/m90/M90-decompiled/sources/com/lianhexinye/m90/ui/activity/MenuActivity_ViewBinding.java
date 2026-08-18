package com.lianhexinye.m90.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class MenuActivity_ViewBinding implements Unbinder {
    private MenuActivity target;
    private View view7f090103;
    private View view7f090106;
    private View view7f090108;
    private View view7f09010b;
    private View view7f090113;
    private View view7f090115;
    private View view7f090116;
    private View view7f09011d;

    public MenuActivity_ViewBinding(MenuActivity menuActivity) {
        this(menuActivity, menuActivity.getWindow().getDecorView());
    }

    public MenuActivity_ViewBinding(final MenuActivity menuActivity, View view) {
        this.target = menuActivity;
        menuActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        menuActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        menuActivity.tvLineSele = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLineSele, "field 'tvLineSele'", TextView.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.lyLineSele, "field 'lyLineSele' and method 'onViewClicked'");
        menuActivity.lyLineSele = (RelativeLayout) Utils.castView(viewFindRequiredView, R.id.lyLineSele, "field 'lyLineSele'", RelativeLayout.class);
        this.view7f09010b = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MenuActivity_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                menuActivity.onViewClicked(view2);
            }
        });
        menuActivity.tvSiteLearn = (TextView) Utils.findRequiredViewAsType(view, R.id.tvSiteLearn, "field 'tvSiteLearn'", TextView.class);
        View viewFindRequiredView2 = Utils.findRequiredView(view, R.id.lySiteLearn, "field 'lySiteLearn' and method 'onViewClicked'");
        menuActivity.lySiteLearn = (RelativeLayout) Utils.castView(viewFindRequiredView2, R.id.lySiteLearn, "field 'lySiteLearn'", RelativeLayout.class);
        this.view7f090113 = viewFindRequiredView2;
        viewFindRequiredView2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MenuActivity_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                menuActivity.onViewClicked(view2);
            }
        });
        menuActivity.tvFileManage = (TextView) Utils.findRequiredViewAsType(view, R.id.tvFileManage, "field 'tvFileManage'", TextView.class);
        View viewFindRequiredView3 = Utils.findRequiredView(view, R.id.lyFileManage, "field 'lyFileManage' and method 'onViewClicked'");
        menuActivity.lyFileManage = (RelativeLayout) Utils.castView(viewFindRequiredView3, R.id.lyFileManage, "field 'lyFileManage'", RelativeLayout.class);
        this.view7f090106 = viewFindRequiredView3;
        viewFindRequiredView3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MenuActivity_ViewBinding.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                menuActivity.onViewClicked(view2);
            }
        });
        menuActivity.tvSystemSet = (TextView) Utils.findRequiredViewAsType(view, R.id.tvSystemSet, "field 'tvSystemSet'", TextView.class);
        View viewFindRequiredView4 = Utils.findRequiredView(view, R.id.lySystemSet, "field 'lySystemSet' and method 'onViewClicked'");
        menuActivity.lySystemSet = (RelativeLayout) Utils.castView(viewFindRequiredView4, R.id.lySystemSet, "field 'lySystemSet'", RelativeLayout.class);
        this.view7f090116 = viewFindRequiredView4;
        viewFindRequiredView4.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MenuActivity_ViewBinding.4
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                menuActivity.onViewClicked(view2);
            }
        });
        menuActivity.tvVoiceCall = (TextView) Utils.findRequiredViewAsType(view, R.id.tvVoiceCall, "field 'tvVoiceCall'", TextView.class);
        View viewFindRequiredView5 = Utils.findRequiredView(view, R.id.lyVoiceCall, "field 'lyVoiceCall' and method 'onViewClicked'");
        menuActivity.lyVoiceCall = (RelativeLayout) Utils.castView(viewFindRequiredView5, R.id.lyVoiceCall, "field 'lyVoiceCall'", RelativeLayout.class);
        this.view7f09011d = viewFindRequiredView5;
        viewFindRequiredView5.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MenuActivity_ViewBinding.5
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                menuActivity.onViewClicked(view2);
            }
        });
        menuActivity.tvDispatchingCenter = (TextView) Utils.findRequiredViewAsType(view, R.id.tvDispatchingCenter, "field 'tvDispatchingCenter'", TextView.class);
        View viewFindRequiredView6 = Utils.findRequiredView(view, R.id.lyDispatchingCenter, "field 'lyDispatchingCenter' and method 'onViewClicked'");
        menuActivity.lyDispatchingCenter = (RelativeLayout) Utils.castView(viewFindRequiredView6, R.id.lyDispatchingCenter, "field 'lyDispatchingCenter'", RelativeLayout.class);
        this.view7f090103 = viewFindRequiredView6;
        viewFindRequiredView6.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MenuActivity_ViewBinding.6
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                menuActivity.onViewClicked(view2);
            }
        });
        menuActivity.tvInfoBrowsing = (TextView) Utils.findRequiredViewAsType(view, R.id.tvInfoBrowsing, "field 'tvInfoBrowsing'", TextView.class);
        View viewFindRequiredView7 = Utils.findRequiredView(view, R.id.lyInfoBrowsing, "field 'lyInfoBrowsing' and method 'onViewClicked'");
        menuActivity.lyInfoBrowsing = (RelativeLayout) Utils.castView(viewFindRequiredView7, R.id.lyInfoBrowsing, "field 'lyInfoBrowsing'", RelativeLayout.class);
        this.view7f090108 = viewFindRequiredView7;
        viewFindRequiredView7.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MenuActivity_ViewBinding.7
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                menuActivity.onViewClicked(view2);
            }
        });
        menuActivity.tvSysInfo = (TextView) Utils.findRequiredViewAsType(view, R.id.tvSysInfo, "field 'tvSysInfo'", TextView.class);
        View viewFindRequiredView8 = Utils.findRequiredView(view, R.id.lySysInfo, "field 'lySysInfo' and method 'onViewClicked'");
        menuActivity.lySysInfo = (RelativeLayout) Utils.castView(viewFindRequiredView8, R.id.lySysInfo, "field 'lySysInfo'", RelativeLayout.class);
        this.view7f090115 = viewFindRequiredView8;
        viewFindRequiredView8.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MenuActivity_ViewBinding.8
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                menuActivity.onViewClicked(view2);
            }
        });
        menuActivity.lyFinance1 = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyFinance1, "field 'lyFinance1'", LinearLayout.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        MenuActivity menuActivity = this.target;
        if (menuActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        menuActivity.toolbarTitle = null;
        menuActivity.toolbar = null;
        menuActivity.tvLineSele = null;
        menuActivity.lyLineSele = null;
        menuActivity.tvSiteLearn = null;
        menuActivity.lySiteLearn = null;
        menuActivity.tvFileManage = null;
        menuActivity.lyFileManage = null;
        menuActivity.tvSystemSet = null;
        menuActivity.lySystemSet = null;
        menuActivity.tvVoiceCall = null;
        menuActivity.lyVoiceCall = null;
        menuActivity.tvDispatchingCenter = null;
        menuActivity.lyDispatchingCenter = null;
        menuActivity.tvInfoBrowsing = null;
        menuActivity.lyInfoBrowsing = null;
        menuActivity.tvSysInfo = null;
        menuActivity.lySysInfo = null;
        menuActivity.lyFinance1 = null;
        this.view7f09010b.setOnClickListener(null);
        this.view7f09010b = null;
        this.view7f090113.setOnClickListener(null);
        this.view7f090113 = null;
        this.view7f090106.setOnClickListener(null);
        this.view7f090106 = null;
        this.view7f090116.setOnClickListener(null);
        this.view7f090116 = null;
        this.view7f09011d.setOnClickListener(null);
        this.view7f09011d = null;
        this.view7f090103.setOnClickListener(null);
        this.view7f090103 = null;
        this.view7f090108.setOnClickListener(null);
        this.view7f090108 = null;
        this.view7f090115.setOnClickListener(null);
        this.view7f090115 = null;
    }
}
