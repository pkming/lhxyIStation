package com.lhxy.istationdevice.android11.protocol.legacy;

import java.util.List;

/**
 * M90 LED 导程牌协议。
 * 旧机主链只有当前站切换帧，其他能力保持空实现。
 */
public final class LedGuideDisplayProtocol implements DisplayProtocolGenerator {
    @Override
    public byte[] createNewspaperStation(BusLineSnapshot snapshot, DisplayLanguage language) {
        if (snapshot == null) {
            return new byte[0];
        }
        byte[] payload = new byte[6];
        payload[0] = 0x7E;
        payload[1] = (byte) (snapshot.getBusNo() & 0xFF);
        payload[2] = (byte) (snapshot.getStationType() & 0xFF);
        payload[3] = (byte) (snapshot.getCountStation() & 0xFF);
        payload[4] = (byte) (snapshot.getDirection() == 1 ? 0x00 : 0x01);
        payload[5] = 0x7F;
        return payload;
    }

    @Override
    public byte[] createLineName(BusLineSnapshot snapshot, DisplayLanguage language) {
        return new byte[0];
    }

    @Override
    public byte[] createLineState(BusLineSnapshot snapshot) {
        return new byte[0];
    }

    @Override
    public ProtocolBatchResult createSiteInfo(List<BusLineSnapshot> snapshots) {
        return null;
    }

    @Override
    public byte[] createInternalScreen(BusLineSnapshot snapshot, DisplayLanguage language) {
        return new byte[0];
    }

    @Override
    public byte[] createServiceTone(byte value) {
        return new byte[0];
    }
}