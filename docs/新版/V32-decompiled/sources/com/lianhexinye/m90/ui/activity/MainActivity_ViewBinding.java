package com.lianhexinye.m90.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class MainActivity_ViewBinding implements Unbinder {
    private MainActivity target;
    private View view7f09004e;
    private View view7f090054;
    private View view7f09005a;
    private View view7f09005c;
    private View view7f09005d;
    private View view7f09005e;
    private View view7f09005f;
    private View view7f090062;
    private View view7f090064;
    private View view7f090065;
    private View view7f090066;
    private View view7f090067;
    private View view7f090068;
    private View view7f090069;
    private View view7f09006a;
    private View view7f09006b;
    private View view7f09006c;
    private View view7f09006d;
    private View view7f09006e;
    private View view7f090072;
    private View view7f09007c;
    private View view7f09007e;

    public MainActivity_ViewBinding(MainActivity mainActivity) {
        this(mainActivity, mainActivity.getWindow().getDecorView());
    }

    public MainActivity_ViewBinding(final MainActivity mainActivity, View view) {
        this.target = mainActivity;
        mainActivity.rlToolbar = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.rlToolbar, "field 'rlToolbar'", LinearLayout.class);
        mainActivity.imageInformation = (ImageView) Utils.findRequiredViewAsType(view, R.id.imageInformation, "field 'imageInformation'", ImageView.class);
        mainActivity.tvInformation = (TextView) Utils.findRequiredViewAsType(view, R.id.tvInformation, "field 'tvInformation'", TextView.class);
        mainActivity.tvCMS = (TextView) Utils.findRequiredViewAsType(view, R.id.tvCMS, "field 'tvCMS'", TextView.class);
        mainActivity.tv4G = (TextView) Utils.findRequiredViewAsType(view, R.id.tv4G, "field 'tv4G'", TextView.class);
        mainActivity.tvWifi = (TextView) Utils.findRequiredViewAsType(view, R.id.tvWifi, "field 'tvWifi'", TextView.class);
        mainActivity.tvLocationGps = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLocationGps, "field 'tvLocationGps'", TextView.class);
        mainActivity.lyToolbarRight = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyToolbarRight, "field 'lyToolbarRight'", LinearLayout.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butNewspaperStation, "field 'butNewspaperStation' and method 'onViewClicked'");
        mainActivity.butNewspaperStation = (Button) Utils.castView(viewFindRequiredView, R.id.butNewspaperStation, "field 'butNewspaperStation'", Button.class);
        this.view7f090064 = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView2 = Utils.findRequiredView(view, R.id.butRepeat, "field 'butRepeat' and method 'onViewClicked'");
        mainActivity.butRepeat = (Button) Utils.castView(viewFindRequiredView2, R.id.butRepeat, "field 'butRepeat'", Button.class);
        this.view7f090072 = viewFindRequiredView2;
        viewFindRequiredView2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView3 = Utils.findRequiredView(view, R.id.butSwitch, "field 'butSwitch' and method 'onViewClicked'");
        mainActivity.butSwitch = (Button) Utils.castView(viewFindRequiredView3, R.id.butSwitch, "field 'butSwitch'", Button.class);
        this.view7f09007c = viewFindRequiredView3;
        viewFindRequiredView3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView4 = Utils.findRequiredView(view, R.id.butKeyboard, "field 'butKeyboard' and method 'onViewClicked'");
        mainActivity.butKeyboard = (Button) Utils.castView(viewFindRequiredView4, R.id.butKeyboard, "field 'butKeyboard'", Button.class);
        this.view7f09005a = viewFindRequiredView4;
        viewFindRequiredView4.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.4
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView5 = Utils.findRequiredView(view, R.id.butCease, "field 'butCease' and method 'onViewClicked'");
        mainActivity.butCease = (Button) Utils.castView(viewFindRequiredView5, R.id.butCease, "field 'butCease'", Button.class);
        this.view7f09004e = viewFindRequiredView5;
        viewFindRequiredView5.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.5
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView6 = Utils.findRequiredView(view, R.id.butVideo, "field 'butVideo' and method 'onViewClicked'");
        mainActivity.butVideo = (Button) Utils.castView(viewFindRequiredView6, R.id.butVideo, "field 'butVideo'", Button.class);
        this.view7f09007e = viewFindRequiredView6;
        viewFindRequiredView6.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.6
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView7 = Utils.findRequiredView(view, R.id.butMenu, "field 'butMenu' and method 'onViewClicked'");
        mainActivity.butMenu = (Button) Utils.castView(viewFindRequiredView7, R.id.butMenu, "field 'butMenu'", Button.class);
        this.view7f090062 = viewFindRequiredView7;
        viewFindRequiredView7.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.7
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        mainActivity.rlOperation = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.rlOperation, "field 'rlOperation'", LinearLayout.class);
        View viewFindRequiredView8 = Utils.findRequiredView(view, R.id.butNumberF1, "field 'butNumberF1' and method 'onViewClicked'");
        mainActivity.butNumberF1 = (Button) Utils.castView(viewFindRequiredView8, R.id.butNumberF1, "field 'butNumberF1'", Button.class);
        this.view7f090066 = viewFindRequiredView8;
        viewFindRequiredView8.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.8
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView9 = Utils.findRequiredView(view, R.id.butNumberF3, "field 'butNumberF3' and method 'onViewClicked'");
        mainActivity.butNumberF3 = (Button) Utils.castView(viewFindRequiredView9, R.id.butNumberF3, "field 'butNumberF3'", Button.class);
        this.view7f090068 = viewFindRequiredView9;
        viewFindRequiredView9.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.9
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView10 = Utils.findRequiredView(view, R.id.butNumberF5, "field 'butNumberF5' and method 'onViewClicked'");
        mainActivity.butNumberF5 = (Button) Utils.castView(viewFindRequiredView10, R.id.butNumberF5, "field 'butNumberF5'", Button.class);
        this.view7f09006a = viewFindRequiredView10;
        viewFindRequiredView10.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.10
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView11 = Utils.findRequiredView(view, R.id.butNumberF7, "field 'butNumberF7' and method 'onViewClicked'");
        mainActivity.butNumberF7 = (Button) Utils.castView(viewFindRequiredView11, R.id.butNumberF7, "field 'butNumberF7'", Button.class);
        this.view7f09006c = viewFindRequiredView11;
        viewFindRequiredView11.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.11
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView12 = Utils.findRequiredView(view, R.id.butNumberF9, "field 'butNumberF9' and method 'onViewClicked'");
        mainActivity.butNumberF9 = (Button) Utils.castView(viewFindRequiredView12, R.id.butNumberF9, "field 'butNumberF9'", Button.class);
        this.view7f09006e = viewFindRequiredView12;
        viewFindRequiredView12.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.12
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView13 = Utils.findRequiredView(view, R.id.butMainUp, "field 'butMainUp' and method 'onViewClicked'");
        mainActivity.butMainUp = (Button) Utils.castView(viewFindRequiredView13, R.id.butMainUp, "field 'butMainUp'", Button.class);
        this.view7f09005f = viewFindRequiredView13;
        viewFindRequiredView13.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.13
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView14 = Utils.findRequiredView(view, R.id.butMainMsg, "field 'butMainMsg' and method 'onViewClicked'");
        mainActivity.butMainMsg = (Button) Utils.castView(viewFindRequiredView14, R.id.butMainMsg, "field 'butMainMsg'", Button.class);
        this.view7f09005d = viewFindRequiredView14;
        viewFindRequiredView14.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.14
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView15 = Utils.findRequiredView(view, R.id.butNumberF2, "field 'butNumberF2' and method 'onViewClicked'");
        mainActivity.butNumberF2 = (Button) Utils.castView(viewFindRequiredView15, R.id.butNumberF2, "field 'butNumberF2'", Button.class);
        this.view7f090067 = viewFindRequiredView15;
        viewFindRequiredView15.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.15
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView16 = Utils.findRequiredView(view, R.id.butNumberF4, "field 'butNumberF4' and method 'onViewClicked'");
        mainActivity.butNumberF4 = (Button) Utils.castView(viewFindRequiredView16, R.id.butNumberF4, "field 'butNumberF4'", Button.class);
        this.view7f090069 = viewFindRequiredView16;
        viewFindRequiredView16.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.16
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView17 = Utils.findRequiredView(view, R.id.butNumberF6, "field 'butNumberF6' and method 'onViewClicked'");
        mainActivity.butNumberF6 = (Button) Utils.castView(viewFindRequiredView17, R.id.butNumberF6, "field 'butNumberF6'", Button.class);
        this.view7f09006b = viewFindRequiredView17;
        viewFindRequiredView17.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.17
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView18 = Utils.findRequiredView(view, R.id.butNumberF8, "field 'butNumberF8' and method 'onViewClicked'");
        mainActivity.butNumberF8 = (Button) Utils.castView(viewFindRequiredView18, R.id.butNumberF8, "field 'butNumberF8'", Button.class);
        this.view7f09006d = viewFindRequiredView18;
        viewFindRequiredView18.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.18
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView19 = Utils.findRequiredView(view, R.id.butNumberF0, "field 'butNumberF0' and method 'onViewClicked'");
        mainActivity.butNumberF0 = (Button) Utils.castView(viewFindRequiredView19, R.id.butNumberF0, "field 'butNumberF0'", Button.class);
        this.view7f090065 = viewFindRequiredView19;
        viewFindRequiredView19.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.19
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView20 = Utils.findRequiredView(view, R.id.butMainDown, "field 'butMainDown' and method 'onViewClicked'");
        mainActivity.butMainDown = (Button) Utils.castView(viewFindRequiredView20, R.id.butMainDown, "field 'butMainDown'", Button.class);
        this.view7f09005c = viewFindRequiredView20;
        viewFindRequiredView20.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.20
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView21 = Utils.findRequiredView(view, R.id.butMainQuery, "field 'butMainQuery' and method 'onViewClicked'");
        mainActivity.butMainQuery = (Button) Utils.castView(viewFindRequiredView21, R.id.butMainQuery, "field 'butMainQuery'", Button.class);
        this.view7f09005e = viewFindRequiredView21;
        viewFindRequiredView21.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.21
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        View viewFindRequiredView22 = Utils.findRequiredView(view, R.id.butESC, "field 'butESC' and method 'onViewClicked'");
        mainActivity.butESC = (Button) Utils.castView(viewFindRequiredView22, R.id.butESC, "field 'butESC'", Button.class);
        this.view7f090054 = viewFindRequiredView22;
        viewFindRequiredView22.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.MainActivity_ViewBinding.22
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                mainActivity.onViewClicked(view2);
            }
        });
        mainActivity.lyNumberkeyboard = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyNumberkeyboard, "field 'lyNumberkeyboard'", LinearLayout.class);
        mainActivity.tvDatetime = (TextClock) Utils.findRequiredViewAsType(view, R.id.tvDatetime, "field 'tvDatetime'", TextClock.class);
        mainActivity.tvSpeedLimit = (TextView) Utils.findRequiredViewAsType(view, R.id.tvSpeedLimit, "field 'tvSpeedLimit'", TextView.class);
        mainActivity.tvMileage = (TextView) Utils.findRequiredViewAsType(view, R.id.tvMileage, "field 'tvMileage'", TextView.class);
        mainActivity.rlSpeedLimit = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlSpeedLimit, "field 'rlSpeedLimit'", RelativeLayout.class);
        mainActivity.rlPcs001 = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlPcs001, "field 'rlPcs001'", RelativeLayout.class);
        mainActivity.tvLineName = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLineName, "field 'tvLineName'", TextView.class);
        mainActivity.tvLineDirection = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLineDirection, "field 'tvLineDirection'", TextView.class);
        mainActivity.rlLineInfo = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlLineInfo, "field 'rlLineInfo'", RelativeLayout.class);
        mainActivity.tvHomeNextStation = (TextView) Utils.findRequiredViewAsType(view, R.id.tvHomeNextStation, "field 'tvHomeNextStation'", TextView.class);
        mainActivity.tvEndBus = (TextView) Utils.findRequiredViewAsType(view, R.id.tvEndBus, "field 'tvEndBus'", TextView.class);
        mainActivity.tvJobNumber = (TextView) Utils.findRequiredViewAsType(view, R.id.tvJobNumber, "field 'tvJobNumber'", TextView.class);
        mainActivity.tvCarNumber = (TextView) Utils.findRequiredViewAsType(view, R.id.tvCarNumber, "field 'tvCarNumber'", TextView.class);
        mainActivity.tvVehicleStatus = (TextView) Utils.findRequiredViewAsType(view, R.id.tvVehicleStatus, "field 'tvVehicleStatus'", TextView.class);
        mainActivity.tvNextTrip = (TextView) Utils.findRequiredViewAsType(view, R.id.tvNextTrip, "field 'tvNextTrip'", TextView.class);
        mainActivity.tvThisTrip = (TextView) Utils.findRequiredViewAsType(view, R.id.tvThisTrip, "field 'tvThisTrip'", TextView.class);
        mainActivity.tvTomorrow = (TextView) Utils.findRequiredViewAsType(view, R.id.tvTomorrow, "field 'tvTomorrow'", TextView.class);
        mainActivity.lySwitchInfo = (ConstraintLayout) Utils.findRequiredViewAsType(view, R.id.lySwitchInfo, "field 'lySwitchInfo'", ConstraintLayout.class);
        mainActivity.tvPlannedTime1 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvPlannedTime1, "field 'tvPlannedTime1'", TextView.class);
        mainActivity.lyPlannedTime = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.lyPlannedTime, "field 'lyPlannedTime'", RelativeLayout.class);
        mainActivity.tvDriver = (Button) Utils.findRequiredViewAsType(view, R.id.tvDriver, "field 'tvDriver'", Button.class);
        mainActivity.lyVideoImage = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyVideoImage, "field 'lyVideoImage'", LinearLayout.class);
        mainActivity.tvShouting = (TextView) Utils.findRequiredViewAsType(view, R.id.tvShouting, "field 'tvShouting'", TextView.class);
        mainActivity.tvLineNO = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLineNO, "field 'tvLineNO'", TextView.class);
        mainActivity.tvLanState = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLanState, "field 'tvLanState'", TextView.class);
        mainActivity.tvInfoTips = (TextView) Utils.findRequiredViewAsType(view, R.id.tvInfoTips, "field 'tvInfoTips'", TextView.class);
        mainActivity.tvHomeNextStationName = (TextView) Utils.findRequiredViewAsType(view, R.id.tvHomeNextStationName, "field 'tvHomeNextStationName'", TextView.class);
        mainActivity.tvEndBusName = (TextView) Utils.findRequiredViewAsType(view, R.id.tvEndBusName, "field 'tvEndBusName'", TextView.class);
        mainActivity.rlLineDriver = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlLineDriver, "field 'rlLineDriver'", RelativeLayout.class);
        mainActivity.rlBusInfo = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlBusInfo, "field 'rlBusInfo'", RelativeLayout.class);
        mainActivity.lyVideoDVRImage = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyVideoDVRImage, "field 'lyVideoDVRImage'", LinearLayout.class);
        mainActivity.tvFin001 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvFin001, "field 'tvFin001'", TextView.class);
        mainActivity.tvFout001 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvFout001, "field 'tvFout001'", TextView.class);
        mainActivity.tvBin001 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvBin001, "field 'tvBin001'", TextView.class);
        mainActivity.tvBout001 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvBout001, "field 'tvBout001'", TextView.class);
        mainActivity.tvAll001 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvAll001, "field 'tvAll001'", TextView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        MainActivity mainActivity = this.target;
        if (mainActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        mainActivity.rlToolbar = null;
        mainActivity.imageInformation = null;
        mainActivity.tvInformation = null;
        mainActivity.tvCMS = null;
        mainActivity.tv4G = null;
        mainActivity.tvWifi = null;
        mainActivity.tvLocationGps = null;
        mainActivity.lyToolbarRight = null;
        mainActivity.butNewspaperStation = null;
        mainActivity.butRepeat = null;
        mainActivity.butSwitch = null;
        mainActivity.butKeyboard = null;
        mainActivity.butCease = null;
        mainActivity.butVideo = null;
        mainActivity.butMenu = null;
        mainActivity.rlOperation = null;
        mainActivity.butNumberF1 = null;
        mainActivity.butNumberF3 = null;
        mainActivity.butNumberF5 = null;
        mainActivity.butNumberF7 = null;
        mainActivity.butNumberF9 = null;
        mainActivity.butMainUp = null;
        mainActivity.butMainMsg = null;
        mainActivity.butNumberF2 = null;
        mainActivity.butNumberF4 = null;
        mainActivity.butNumberF6 = null;
        mainActivity.butNumberF8 = null;
        mainActivity.butNumberF0 = null;
        mainActivity.butMainDown = null;
        mainActivity.butMainQuery = null;
        mainActivity.butESC = null;
        mainActivity.lyNumberkeyboard = null;
        mainActivity.tvDatetime = null;
        mainActivity.tvSpeedLimit = null;
        mainActivity.tvMileage = null;
        mainActivity.rlSpeedLimit = null;
        mainActivity.rlPcs001 = null;
        mainActivity.tvLineName = null;
        mainActivity.tvLineDirection = null;
        mainActivity.rlLineInfo = null;
        mainActivity.tvHomeNextStation = null;
        mainActivity.tvEndBus = null;
        mainActivity.tvJobNumber = null;
        mainActivity.tvCarNumber = null;
        mainActivity.tvVehicleStatus = null;
        mainActivity.tvNextTrip = null;
        mainActivity.tvThisTrip = null;
        mainActivity.tvTomorrow = null;
        mainActivity.lySwitchInfo = null;
        mainActivity.tvPlannedTime1 = null;
        mainActivity.lyPlannedTime = null;
        mainActivity.tvDriver = null;
        mainActivity.lyVideoImage = null;
        mainActivity.tvShouting = null;
        mainActivity.tvLineNO = null;
        mainActivity.tvLanState = null;
        mainActivity.tvInfoTips = null;
        mainActivity.tvHomeNextStationName = null;
        mainActivity.tvEndBusName = null;
        mainActivity.rlLineDriver = null;
        mainActivity.rlBusInfo = null;
        mainActivity.lyVideoDVRImage = null;
        mainActivity.tvFin001 = null;
        mainActivity.tvFout001 = null;
        mainActivity.tvBin001 = null;
        mainActivity.tvBout001 = null;
        mainActivity.tvAll001 = null;
        this.view7f090064.setOnClickListener(null);
        this.view7f090064 = null;
        this.view7f090072.setOnClickListener(null);
        this.view7f090072 = null;
        this.view7f09007c.setOnClickListener(null);
        this.view7f09007c = null;
        this.view7f09005a.setOnClickListener(null);
        this.view7f09005a = null;
        this.view7f09004e.setOnClickListener(null);
        this.view7f09004e = null;
        this.view7f09007e.setOnClickListener(null);
        this.view7f09007e = null;
        this.view7f090062.setOnClickListener(null);
        this.view7f090062 = null;
        this.view7f090066.setOnClickListener(null);
        this.view7f090066 = null;
        this.view7f090068.setOnClickListener(null);
        this.view7f090068 = null;
        this.view7f09006a.setOnClickListener(null);
        this.view7f09006a = null;
        this.view7f09006c.setOnClickListener(null);
        this.view7f09006c = null;
        this.view7f09006e.setOnClickListener(null);
        this.view7f09006e = null;
        this.view7f09005f.setOnClickListener(null);
        this.view7f09005f = null;
        this.view7f09005d.setOnClickListener(null);
        this.view7f09005d = null;
        this.view7f090067.setOnClickListener(null);
        this.view7f090067 = null;
        this.view7f090069.setOnClickListener(null);
        this.view7f090069 = null;
        this.view7f09006b.setOnClickListener(null);
        this.view7f09006b = null;
        this.view7f09006d.setOnClickListener(null);
        this.view7f09006d = null;
        this.view7f090065.setOnClickListener(null);
        this.view7f090065 = null;
        this.view7f09005c.setOnClickListener(null);
        this.view7f09005c = null;
        this.view7f09005e.setOnClickListener(null);
        this.view7f09005e = null;
        this.view7f090054.setOnClickListener(null);
        this.view7f090054 = null;
    }
}
