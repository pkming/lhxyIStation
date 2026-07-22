package com.lianhexinye.m90.mvp.main;

import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel;
import com.lianhexinye.m90.greendao.gen.BusLineModel;

/* JADX INFO: loaded from: classes2.dex */
public class GPSOperationParam {
    private double angle;
    private BusLineFriendRemindModel busLineFriendRemindModel;
    private BusLineModel busLineModel;
    private double cLat;
    private double cLong;
    private boolean iCrossInner;
    private boolean iInitSite;
    private boolean iStationInner;
    private int operationType;

    public int getOperationType() {
        return this.operationType;
    }

    public void setOperationType(int i) {
        this.operationType = i;
    }

    public double getcLong() {
        return this.cLong;
    }

    public void setcLong(double d) {
        this.cLong = d;
    }

    public double getcLat() {
        return this.cLat;
    }

    public void setcLat(double d) {
        this.cLat = d;
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double d) {
        this.angle = d;
    }

    public BusLineModel getBusLineModel() {
        return this.busLineModel;
    }

    public void setBusLineModel(BusLineModel busLineModel) {
        this.busLineModel = busLineModel;
    }

    public boolean isiStationInner() {
        return this.iStationInner;
    }

    public void setiStationInner(boolean z) {
        this.iStationInner = z;
    }

    public BusLineFriendRemindModel getBusLineFriendRemindModel() {
        return this.busLineFriendRemindModel;
    }

    public void setBusLineFriendRemindModel(BusLineFriendRemindModel busLineFriendRemindModel) {
        this.busLineFriendRemindModel = busLineFriendRemindModel;
    }

    public boolean isiInitSite() {
        return this.iInitSite;
    }

    public void setiInitSite(boolean z) {
        this.iInitSite = z;
    }

    public boolean isiCrossInner() {
        return this.iCrossInner;
    }

    public void setiCrossInner(boolean z) {
        this.iCrossInner = z;
    }
}
