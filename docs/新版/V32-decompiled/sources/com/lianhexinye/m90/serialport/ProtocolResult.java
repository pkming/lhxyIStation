package com.lianhexinye.m90.serialport;

import com.lianhexinye.m90.greendao.gen.BusLineModel;

/* JADX INFO: loaded from: classes2.dex */
public class ProtocolResult {
    private BusLineModel busLineModel;
    private byte[] mBufferBreak;

    public BusLineModel getBusLineModel() {
        return this.busLineModel;
    }

    public void setBusLineModel(BusLineModel busLineModel) {
        this.busLineModel = busLineModel;
    }

    public byte[] getmBufferBreak() {
        return this.mBufferBreak;
    }

    public void setmBufferBreak(byte[] bArr) {
        this.mBufferBreak = bArr;
    }
}
