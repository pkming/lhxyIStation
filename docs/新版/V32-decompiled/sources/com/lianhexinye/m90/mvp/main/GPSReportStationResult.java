package com.lianhexinye.m90.mvp.main;

import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel;
import com.lianhexinye.m90.greendao.gen.BusLineModel;

/* JADX INFO: loaded from: classes2.dex */
public class GPSReportStationResult {
    private BusLineFriendRemindModel busLineFriendRemindModel;
    private BusLineModel busLineModel;
    private int crossType;
    private int operationType;
    private int stationType;

    public int getStationType() {
        return this.stationType;
    }

    public void setStationType(int i) {
        this.stationType = i;
    }

    public int getOperationType() {
        return this.operationType;
    }

    public void setOperationType(int i) {
        this.operationType = i;
    }

    public int getCrossType() {
        return this.crossType;
    }

    public void setCrossType(int i) {
        this.crossType = i;
    }

    public BusLineModel getBusLineModel() {
        return this.busLineModel;
    }

    public void setBusLineModel(BusLineModel busLineModel) {
        this.busLineModel = busLineModel;
    }

    public BusLineFriendRemindModel getBusLineFriendRemindModel() {
        return this.busLineFriendRemindModel;
    }

    public void setBusLineFriendRemindModel(BusLineFriendRemindModel busLineFriendRemindModel) {
        this.busLineFriendRemindModel = busLineFriendRemindModel;
    }
}
