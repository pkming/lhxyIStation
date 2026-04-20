package com.lhxy.istationdevice.android11.protocol.legacy;

import java.util.List;

public final class TongDaDisplayProtocol implements DisplayProtocolGenerator {
    @Override
    public byte[] createNewspaperStation(BusLineSnapshot snapshot, DisplayLanguage language) {
        byte[] busName = LegacyProtocolSupport.gb2312(buildStationText(snapshot, language));
        int stationType = snapshot.getStationType() == 0 ? (128 | snapshot.getBusNo()) : snapshot.getBusNo();
        int countLength = 10 + busName.length;

        byte[] buffer = new byte[12 + busName.length];
        buffer[0] = (byte) 0xBB;
        buffer[1] = (byte) 0x10;
        buffer[2] = (byte) countLength;
        buffer[3] = (byte) 0x08;
        buffer[4] = (byte) stationType;
        buffer[5] = (byte) (snapshot.getDirection() == 1 ? snapshot.getCountStation() : (128 | snapshot.getCountStation()));
        buffer[6] = (byte) 0x20;
        System.arraycopy(busName, 0, buffer, 7, busName.length);
        buffer[7 + busName.length] = (byte) 0x20;
        buffer[8 + busName.length] = (byte) 0x14;

        int checksum = LegacyProtocolSupport.sumCheckInt(buffer, 1, 9 + busName.length);
        byte[] checksumBytes = LegacyProtocolSupport.longToBytesLittle(checksum);
        buffer[9 + busName.length] = checksumBytes[0];
        buffer[10 + busName.length] = checksumBytes[1];
        buffer[11 + busName.length] = (byte) 0x55;
        return buffer;
    }

    @Override
    public byte[] createLineName(BusLineSnapshot snapshot, DisplayLanguage language) {
        byte[] lineName = LegacyProtocolSupport.gb2312(snapshot.getBusLineName());
        byte[] startName = LegacyProtocolSupport.gb2312(snapshot.getStartBusName());
        byte[] endName = LegacyProtocolSupport.gb2312(snapshot.getEndBusName());

        int countLength = 8 + lineName.length + startName.length + endName.length;
        byte[] buffer = new byte[10 + lineName.length + startName.length + endName.length];
        buffer[0] = (byte) 0xBB;
        buffer[1] = (byte) 0x10;
        buffer[2] = (byte) countLength;
        buffer[3] = (byte) 0x02;
        buffer[4] = (byte) lineName.length;
        System.arraycopy(lineName, 0, buffer, 5, lineName.length);

        int startLenIndex = 5 + lineName.length;
        buffer[startLenIndex] = (byte) startName.length;
        System.arraycopy(startName, 0, buffer, startLenIndex + 1, startName.length);

        int endLenIndex = startLenIndex + 1 + startName.length;
        buffer[endLenIndex] = (byte) endName.length;
        System.arraycopy(endName, 0, buffer, endLenIndex + 1, endName.length);

        int checksumIndex = endLenIndex + 1 + endName.length;
        int checksum = LegacyProtocolSupport.sumCheckInt(buffer, 1, checksumIndex);
        byte[] checksumBytes = LegacyProtocolSupport.longToBytesLittle(checksum);
        buffer[checksumIndex] = checksumBytes[0];
        buffer[checksumIndex + 1] = checksumBytes[1];
        buffer[checksumIndex + 2] = (byte) 0x55;
        return buffer;
    }

    @Override
    public byte[] createLineState(BusLineSnapshot snapshot) {
        byte[] buffer = new byte[17];
        buffer[0] = (byte) 0xBB;
        buffer[1] = (byte) 0x10;
        buffer[2] = (byte) 0x0F;
        buffer[3] = (byte) 0x01;
        buffer[4] = (byte) LegacyProtocolSupport.minute();
        buffer[5] = (byte) LegacyProtocolSupport.hour24();
        buffer[6] = (byte) LegacyProtocolSupport.dayOfMonth();
        buffer[7] = (byte) LegacyProtocolSupport.dayOfWeekValue();
        buffer[8] = (byte) LegacyProtocolSupport.month1Based();
        buffer[9] = (byte) LegacyProtocolSupport.year2();
        buffer[10] = (byte) (snapshot.getDirection() == 1 ? 0x00 : 0x80);
        buffer[11] = (byte) (snapshot.getStationType() == 0 ? (128 | snapshot.getBusNo()) : snapshot.getBusNo());
        buffer[12] = (byte) 0x00;
        buffer[13] = (byte) 0x00;

        int crc = LegacyCrc16.calc(buffer, 1, 13);
        buffer[14] = (byte) (crc & 0xFF);
        buffer[15] = (byte) ((crc >> 8) & 0xFF);
        buffer[16] = (byte) 0x55;
        return buffer;
    }

    @Override
    public ProtocolBatchResult createSiteInfo(List<BusLineSnapshot> snapshots) {
        byte[] tmp = new byte[240];
        int count = 0;
        BusLineSnapshot lastLine = null;

        for (int i = 0; i < snapshots.size(); i++) {
            BusLineSnapshot snapshot = snapshots.get(i);
            byte[] busName = LegacyProtocolSupport.gb2312(snapshot.getBusName());
            tmp[count] = (byte) snapshot.getBusNo();
            tmp[count + 1] = (byte) busName.length;
            System.arraycopy(busName, 0, tmp, count + 2, busName.length);
            count += busName.length + 2;
            lastLine = snapshot;

            if (count > 200) {
                tmp[count] = (byte) 0xFF;
                break;
            }
            if (i >= snapshots.size() - 1) {
                tmp[count] = (byte) 0xFF;
            }
        }

        byte[] buffer = new byte[count + 8];
        buffer[0] = (byte) 0xBB;
        buffer[1] = (byte) 0x10;
        buffer[2] = (byte) (count + 6);
        buffer[3] = (byte) 0x11;
        buffer[4] = (byte) snapshots.get(0).getCountStation();
        System.arraycopy(tmp, 0, buffer, 5, count);

        int checksum = LegacyProtocolSupport.sumCheckInt(buffer, 1, count + 5);
        byte[] checksumBytes = LegacyProtocolSupport.longToBytesLittle(checksum);
        buffer[count + 5] = checksumBytes[0];
        buffer[count + 6] = checksumBytes[1];
        buffer[count + 7] = (byte) 0x55;
        return new ProtocolBatchResult(lastLine, buffer);
    }

    @Override
    public byte[] createInternalScreen(BusLineSnapshot snapshot, DisplayLanguage language) {
        byte[] busName = LegacyProtocolSupport.gb2312(buildStationText(snapshot, language));
        int countLength = 6 + busName.length;

        byte[] buffer = new byte[8 + busName.length];
        buffer[0] = (byte) 0xBB;
        buffer[1] = (byte) 0x10;
        buffer[2] = (byte) countLength;
        buffer[3] = (byte) 0x03;
        System.arraycopy(busName, 0, buffer, 4, busName.length);
        buffer[4 + busName.length] = (byte) 0x14;

        int checksum = LegacyProtocolSupport.sumCheckInt(buffer, 1, 5 + busName.length);
        byte[] checksumBytes = LegacyProtocolSupport.longToBytesLittle(checksum);
        buffer[5 + busName.length] = checksumBytes[0];
        buffer[6 + busName.length] = checksumBytes[1];
        buffer[7 + busName.length] = (byte) 0x55;
        return buffer;
    }

    @Override
    public byte[] createServiceTone(byte value) {
        return new byte[0];
    }

    private String buildStationText(BusLineSnapshot snapshot, DisplayLanguage language) {
        if (snapshot.getStationType() == 0) {
            return language == DisplayLanguage.ENGLISH
                    ? "ARRIVING:" + snapshot.getBusName()
                    : "本站:" + snapshot.getBusName();
        }
        return language == DisplayLanguage.ENGLISH
                ? "NEXT STOP:" + snapshot.getBusName()
                : "下一站:" + snapshot.getBusName();
    }
}

