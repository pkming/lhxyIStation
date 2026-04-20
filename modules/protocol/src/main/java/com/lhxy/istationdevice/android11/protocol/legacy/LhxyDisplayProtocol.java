package com.lhxy.istationdevice.android11.protocol.legacy;

import java.util.List;

public final class LhxyDisplayProtocol implements DisplayProtocolGenerator {
    @Override
    public byte[] createNewspaperStation(BusLineSnapshot snapshot, DisplayLanguage language) {
        byte[] busName = LegacyProtocolSupport.gb2312(snapshot.getBusName());
        byte[] buffer = new byte[32 + busName.length];
        buffer[0] = (byte) 0x7E;
        buffer[1] = (byte) 0xFF;
        buffer[2] = (byte) 0x01;
        buffer[3] = (byte) 0x03;
        buffer[4] = (byte) 0x01;
        byte[] lengthBytes = LegacyProtocolSupport.intToBytesBig(23 + busName.length);
        buffer[5] = lengthBytes[2];
        buffer[6] = lengthBytes[3];
        buffer[7] = (byte) 0x02;
        buffer[8] = (byte) 0x00;
        buffer[9] = (byte) 0x01;
        buffer[10] = (byte) (snapshot.getDirection() == 1 ? 0x01 : 0x02);
        buffer[11] = (byte) 0x03;
        buffer[12] = (byte) 0x00;
        buffer[13] = (byte) 0x01;
        buffer[14] = (byte) 0x05;
        buffer[15] = (byte) 0x04;
        buffer[16] = (byte) 0x00;
        buffer[17] = (byte) 0x01;
        buffer[18] = (byte) (snapshot.getStationType() == 0 ? 0x01 : 0x02);
        buffer[19] = (byte) 0x05;
        buffer[20] = (byte) 0x00;
        buffer[21] = (byte) 0x01;
        buffer[22] = (byte) (snapshot.getBusNo() + 1);
        buffer[23] = (byte) 0x08;
        buffer[24] = (byte) 0x00;
        buffer[25] = (byte) busName.length;
        System.arraycopy(busName, 0, buffer, 26, busName.length);
        buffer[26 + busName.length] = (byte) 0xA0;
        buffer[27 + busName.length] = (byte) 0x00;
        buffer[28 + busName.length] = (byte) 0x01;
        buffer[29 + busName.length] = (byte) 0x03;
        buffer[30 + busName.length] = (byte) 0x00;
        buffer[31 + busName.length] = (byte) 0x7F;
        return buffer;
    }

    @Override
    public byte[] createLineName(BusLineSnapshot snapshot, DisplayLanguage language) {
        byte[] lineName = LegacyProtocolSupport.gb2312(snapshot.getBusLineName());
        byte[] startName = LegacyProtocolSupport.gb2312(snapshot.getStartBusName());
        byte[] endName = LegacyProtocolSupport.gb2312(snapshot.getEndBusName());
        int dataSize = lineName.length + startName.length + endName.length + startName.length + endName.length;

        byte[] buffer = new byte[dataSize + 32];
        buffer[0] = (byte) 0x7E;
        buffer[1] = (byte) 0xFF;
        buffer[2] = (byte) 0x01;
        buffer[3] = (byte) 0x04;
        buffer[4] = (byte) 0x01;
        byte[] lengthBytes = LegacyProtocolSupport.intToBytesBig(dataSize + 23);
        buffer[5] = lengthBytes[2];
        buffer[6] = lengthBytes[3];

        buffer[7] = (byte) 0x31;
        buffer[8] = (byte) 0x00;
        buffer[9] = (byte) lineName.length;
        System.arraycopy(lineName, 0, buffer, 10, lineName.length);

        int start1Tag = 10 + lineName.length;
        buffer[start1Tag] = (byte) 0x32;
        buffer[start1Tag + 1] = (byte) 0x00;
        buffer[start1Tag + 2] = (byte) startName.length;
        System.arraycopy(startName, 0, buffer, start1Tag + 3, startName.length);

        int start2Tag = start1Tag + 3 + startName.length;
        buffer[start2Tag] = (byte) 0x33;
        buffer[start2Tag + 1] = (byte) 0x00;
        buffer[start2Tag + 2] = (byte) startName.length;
        System.arraycopy(startName, 0, buffer, start2Tag + 3, startName.length);

        int end1Tag = start2Tag + 3 + startName.length;
        buffer[end1Tag] = (byte) 0x34;
        buffer[end1Tag + 1] = (byte) 0x00;
        buffer[end1Tag + 2] = (byte) endName.length;
        System.arraycopy(endName, 0, buffer, end1Tag + 3, endName.length);

        int end2Tag = end1Tag + 3 + endName.length;
        buffer[end2Tag] = (byte) 0x35;
        buffer[end2Tag + 1] = (byte) 0x00;
        buffer[end2Tag + 2] = (byte) endName.length;
        System.arraycopy(endName, 0, buffer, end2Tag + 3, endName.length);

        int directionTag = end2Tag + 3 + endName.length;
        buffer[directionTag] = (byte) 0x02;
        buffer[directionTag + 1] = (byte) 0x00;
        buffer[directionTag + 2] = (byte) 0x01;
        buffer[directionTag + 3] = (byte) (snapshot.getDirection() == 1 ? 0x01 : 0x02);
        buffer[directionTag + 4] = (byte) 0xB9;
        buffer[directionTag + 5] = (byte) 0x00;
        buffer[directionTag + 6] = (byte) 0x01;
        buffer[directionTag + 7] = (byte) 0x03;
        buffer[directionTag + 8] = (byte) 0x00;
        buffer[directionTag + 9] = (byte) 0x7F;
        return buffer;
    }

    @Override
    public byte[] createLineState(BusLineSnapshot snapshot) {
        return new byte[0];
    }

    @Override
    public ProtocolBatchResult createSiteInfo(List<BusLineSnapshot> snapshots) {
        byte[] tmp = new byte[65535];
        int count = 0;
        for (BusLineSnapshot snapshot : snapshots) {
            byte[] busName = LegacyProtocolSupport.gb2312(snapshot.getBusName());
            tmp[count] = (byte) (snapshot.getBusNo() + 1);
            tmp[count + 1] = (byte) busName.length;
            System.arraycopy(busName, 0, tmp, count + 2, busName.length);
            tmp[count + 2 + busName.length] = (byte) 0x00;
            count += busName.length + 3;
        }

        byte[] buffer = new byte[count + 12];
        buffer[0] = (byte) 0x7E;
        buffer[1] = (byte) 0xFF;
        buffer[2] = (byte) 0x01;
        buffer[3] = (byte) 0x10;
        buffer[4] = (byte) 0x01;
        byte[] lengthBytes = LegacyProtocolSupport.intToBytesBig(count + 3);
        buffer[5] = lengthBytes[2];
        buffer[6] = lengthBytes[3];
        buffer[7] = (byte) (snapshots.get(0).getDirection() == 1 ? 0x21 : 0x22);
        byte[] dataLength = LegacyProtocolSupport.intToBytesBig(count);
        buffer[8] = dataLength[2];
        buffer[9] = dataLength[3];
        System.arraycopy(tmp, 0, buffer, 10, count);
        buffer[count + 10] = (byte) 0x00;
        buffer[count + 11] = (byte) 0x7F;
        return new ProtocolBatchResult(snapshots.get(snapshots.size() - 1), buffer);
    }

    @Override
    public byte[] createInternalScreen(BusLineSnapshot snapshot, DisplayLanguage language) {
        return new byte[0];
    }

    @Override
    public byte[] createServiceTone(byte value) {
        return new byte[]{
                (byte) 0xAA,
                (byte) 0x75,
                (byte) 0x05,
                (byte) 0x01,
                value,
                (byte) 0x0D,
                (byte) 0x0A
        };
    }
}

