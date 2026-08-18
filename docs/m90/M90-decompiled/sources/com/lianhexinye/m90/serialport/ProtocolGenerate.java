package com.lianhexinye.m90.serialport;

import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.MaintenanceModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public interface ProtocolGenerate {
    byte[] createInternalScreen(BusLineModel busLineModel);

    byte[] createLedAdvInfo(List<MaintenanceModel> list);

    byte[] createLineName(BusLineModel busLineModel);

    byte[] createLineState();

    byte[] createLineState(BusLineModel busLineModel);

    byte[] createMaintenanceMsg(MaintenanceModel maintenanceModel);

    byte[] createNewspaperStation(BusLineModel busLineModel);

    byte[] createOpenVol();

    byte[] createServiceTone(byte b);

    ProtocolResult createSiteInfo(List<BusLineModel> list);

    byte[] createStopVol();
}
