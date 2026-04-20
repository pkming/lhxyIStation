package com.lhxy.istationdevice.android11.protocol.legacy;

import java.util.List;

public final class HengWuDisplayProtocol implements DisplayProtocolGenerator {
    @Override
    public byte[] createNewspaperStation(BusLineSnapshot snapshot, DisplayLanguage language) {
        byte[] busName = LegacyProtocolSupport.escape7e7d(LegacyProtocolSupport.gb2312(buildStationText(snapshot, language)));
        byte[] instructions = LegacyProtocolSupport.gb2312("SHOW");
        byte[] lengthBytes = LegacyProtocolSupport.intToBytesBig(36 + busName.length);

        byte[] buffer = new byte[40 + busName.length];
        buffer[0] = (byte) 0x7E;
        buffer[1] = (byte) 0x43;
        buffer[2] = lengthBytes[2];
        buffer[3] = lengthBytes[3];
        buffer[4] = (byte) 0x00;
        buffer[5] = (byte) 0x00;
        System.arraycopy(instructions, 0, buffer, 6, instructions.length);
        buffer[6 + instructions.length] = (byte) 0x01;
        buffer[7 + instructions.length] = (byte) 0x03;
        buffer[8 + instructions.length] = (byte) 0x00;
        buffer[9 + instructions.length] = (byte) 0x00;
        buffer[10 + instructions.length] = (byte) 0x00;
        buffer[11 + instructions.length] = (byte) 0x00;
        buffer[12 + instructions.length] = (byte) 0x00;
        buffer[13 + instructions.length] = (byte) 0x00;
        buffer[14 + instructions.length] = (byte) 0x03;
        buffer[15 + instructions.length] = (byte) 0x01;
        buffer[16 + instructions.length] = (byte) 0x00;
        buffer[17 + instructions.length] = (byte) 0x00;
        buffer[18 + instructions.length] = (byte) 0x00;
        buffer[19 + instructions.length] = (byte) 0x00;
        buffer[20 + instructions.length] = (byte) 0x00;
        buffer[21 + instructions.length] = (byte) 0x10;
        buffer[22 + instructions.length] = (byte) 0x02;
        buffer[23 + instructions.length] = (byte) 0x00;
        buffer[24 + instructions.length] = (byte) 0x00;
        buffer[25 + instructions.length] = (byte) 0x01;
        buffer[26 + instructions.length] = (byte) 0x00;
        buffer[27 + instructions.length] = (byte) 0x05;
        buffer[28 + instructions.length] = (byte) 0x01;
        buffer[29 + instructions.length] = (byte) 0x00;
        buffer[30 + instructions.length] = (byte) 0x00;
        buffer[31 + instructions.length] = (byte) 0x00;
        buffer[32 + instructions.length] = (byte) busName.length;
        System.arraycopy(busName, 0, buffer, 33 + instructions.length, busName.length);

        int crc = LegacyProtocolSupport.crcXModem(buffer, 1, 32 + instructions.length + busName.length);
        buffer[33 + instructions.length + busName.length] = (byte) ((crc >> 8) & 0xFF);
        buffer[34 + instructions.length + busName.length] = (byte) (crc & 0xFF);
        buffer[35 + instructions.length + busName.length] = (byte) 0x7E;
        return buffer;
    }

    @Override
    public byte[] createLineName(BusLineSnapshot snapshot, DisplayLanguage language) {
        byte[] lineName = LegacyProtocolSupport.escape7e7d(LegacyProtocolSupport.gb2312(snapshot.getBusLineName()));
        byte[] startName = LegacyProtocolSupport.escape7e7d(LegacyProtocolSupport.gb2312(snapshot.getStartBusName()));
        byte[] endName = LegacyProtocolSupport.escape7e7d(LegacyProtocolSupport.gb2312(snapshot.getEndBusName()));
        byte[] instructions = LegacyProtocolSupport.gb2312("LIN1");
        byte[] lengthBytes = LegacyProtocolSupport.intToBytesBig(15 + lineName.length + startName.length + endName.length);

        byte[] buffer = new byte[19 + lineName.length + startName.length + endName.length];
        buffer[0] = (byte) 0x7E;
        buffer[1] = (byte) 0x43;
        buffer[2] = lengthBytes[2];
        buffer[3] = lengthBytes[3];
        buffer[4] = (byte) 0x00;
        buffer[5] = (byte) 0x00;
        System.arraycopy(instructions, 0, buffer, 6, instructions.length);
        buffer[10] = (byte) 0x80;
        buffer[11] = (byte) lineName.length;
        System.arraycopy(lineName, 0, buffer, 12, lineName.length);

        int startIndex = 12 + lineName.length;
        buffer[startIndex] = (byte) startName.length;
        System.arraycopy(startName, 0, buffer, startIndex + 1, startName.length);
        buffer[startIndex + 1 + startName.length] = (byte) 0x00;
        buffer[startIndex + 2 + startName.length] = (byte) endName.length;
        System.arraycopy(endName, 0, buffer, startIndex + 3 + startName.length, endName.length);
        buffer[startIndex + 3 + startName.length + endName.length] = (byte) 0x00;

        int crcIndex = startIndex + 4 + startName.length + endName.length;
        int crc = LegacyProtocolSupport.crcXModem(buffer, 1, crcIndex - 1);
        buffer[crcIndex] = (byte) ((crc >> 8) & 0xFF);
        buffer[crcIndex + 1] = (byte) (crc & 0xFF);
        buffer[crcIndex + 2] = (byte) 0x7E;
        return buffer;
    }

    @Override
    public byte[] createLineState(BusLineSnapshot snapshot) {
        return new byte[0];
    }

    @Override
    public ProtocolBatchResult createSiteInfo(List<BusLineSnapshot> snapshots) {
        return new ProtocolBatchResult(null, new byte[0]);
    }

    @Override
    public byte[] createInternalScreen(BusLineSnapshot snapshot, DisplayLanguage language) {
        return new byte[0];
    }

    @Override
    public byte[] createServiceTone(byte value) {
        return new byte[0];
    }

    private String buildStationText(BusLineSnapshot snapshot, DisplayLanguage language) {
        if (snapshot.getStationType() == 0) {
            if (language == DisplayLanguage.SPANISH) {
                return "Llegar:" + snapshot.getBusName();
            }
            if (language == DisplayLanguage.ENGLISH) {
                return "ARRIVING:" + snapshot.getBusName();
            }
            return "本站:" + snapshot.getBusName();
        }
        if (language == DisplayLanguage.SPANISH) {
            return "Siguiente parada:" + snapshot.getBusName();
        }
        if (language == DisplayLanguage.ENGLISH) {
            return "NEXT STOP:" + snapshot.getBusName();
        }
        return "下一站:" + snapshot.getBusName();
    }
}

