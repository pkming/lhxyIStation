package com.lhxy.istationdevice.android11.protocol.jt808;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

/**
 * Message builder for the legacy CC808 implementation used by the old M90 app.
 *
 * <p>CC808 uses the normal JT808 envelope, but its application bodies are the
 * fixed layouts from the old {@code Generate808ReqPackage} class.</p>
 */
public final class Cc808LegacyMessages {
    private static final int MSG_GENERAL_RESPONSE = 0x0001;
    private static final int MSG_REGISTER = 0x0100;
    private static final int MSG_AUTHORITY = 0x0102;
    private static final int MSG_HEARTBEAT = 0x0002;
    private static final int MSG_POSITION_REPORT = 0x0200;
    private static final int MSG_REPORT_STATION = 0x0900;
    private static final int MSG_LINE_SWITCH_INFO = 0x0B0B;

    private final Jt808Codec codec = new Jt808Codec();

    public Jt808Frame createHeartbeat(String terminalId) {
        return frame(MSG_HEARTBEAT, terminalId, new byte[0]);
    }

    public Jt808Frame createRegister(Jt808TerminalProfile profile) {
        // The old app used a vendor-specific 46-byte registration body.
        byte[] body = new byte[46];
        body[0] = (byte) 0xAB;
        body[1] = (byte) 0xE0;
        body[2] = (byte) 0xAD;
        body[3] = 0x0C;
        byte[] prefix = new byte[]{
                0x43, 0x54, 0x54, 0x49, 0x54, 0x4D, 0x44, 0x56, 0x52, 0x34, 0x47
        };
        System.arraycopy(prefix, 0, body, 4, prefix.length);
        body[36] = 0x00;
        body[37] = (byte) 0xD4;
        body[38] = (byte) 0xC1;
        body[39] = 0x42;
        body[40] = 0x30;
        body[41] = 0x30;
        body[42] = 0x32;
        body[43] = 0x33;
        body[44] = 0x35;
        body[45] = 0x00;
        byte[] terminal = Jt808CodecSupport.fixedBytes(profile.getTerminalId(), 7, Jt808CodecSupport.GB2312);
        System.arraycopy(terminal, 0, body, 29, terminal.length);
        return frame(MSG_REGISTER, profile.getTerminalId(), body);
    }

    public Jt808Frame createAuthority(String terminalId, byte[] authorityCode) {
        return frame(MSG_AUTHORITY, terminalId, authorityCode == null ? new byte[0] : authorityCode);
    }

    /** Exact 78-byte body produced by the old CC808 positionInfoReport(). */
    public Jt808Frame createPositionReport(
            Jt808TerminalProfile profile,
            Jt808PositionSnapshot snapshot,
            String lineNumber,
            int direction,
            int busNo,
            int vehicleStatus,
            int satellites
    ) {
        byte[] body = new byte[78];
        // The legacy implementation writes 00 00 00 00 00 08 00 13 here.
        body[5] = 0x08;
        body[7] = 0x13;
        copy(body, 8, Jt808CodecSupport.toDword(
                Jt808CodecSupport.decimalStringToPackedInt(snapshot.getLatitude())));
        copy(body, 12, Jt808CodecSupport.toDword(
                Jt808CodecSupport.decimalStringToPackedInt(snapshot.getLongitude())));
        copy(body, 16, Jt808CodecSupport.toWord(0));
        copy(body, 18, Jt808CodecSupport.toWord(snapshot.getSpeed()));
        copy(body, 20, Jt808CodecSupport.toWord(snapshot.getDirection()));
        LocalDateTime time = snapshot.getTerminalTime() == null ? LocalDateTime.now() : snapshot.getTerminalTime();
        copy(body, 22, Jt808CodecSupport.bcdDate(time));
        body[28] = 0x14;
        body[29] = 0x30;
        copy(body, 34, Jt808CodecSupport.fixedBytes(lineNumber, 36, Jt808CodecSupport.GB2312));
        body[70] = (byte) (direction == 1 ? 0 : 1);
        body[71] = (byte) busNo;
        body[72] = (byte) vehicleStatus;
        body[77] = (byte) satellites;
        return frame(MSG_POSITION_REPORT, profile.getTerminalId(), body);
    }

    public Jt808Frame createPositionReport(Jt808TerminalProfile profile, Jt808PositionSnapshot snapshot) {
        return createPositionReport(profile, snapshot, "", 1, 0, 0, 0);
    }

    /** Exact 104/112-byte CC808 report-station body. status=0 means arrival. */
    public Jt808Frame createReportStation(
            String terminalId,
            int vehicleStatus,
            String stationCode,
            int plannedTrips,
            int busNo,
            int direction,
            String lineNumber,
            String arrivalTime,
            String outboundTime,
            int reportType,
            int angle,
            String longitude,
            String latitude,
            int status
    ) {
        int bodyLength = status == 0 ? 104 : 112;
        byte[] body = new byte[bodyLength];
        body[0] = (byte) (status == 0 ? 0x06 : 0x07);
        copy(body, 1, Jt808CodecSupport.toDword(vehicleStatus));
        copy(body, 5, Jt808CodecSupport.fixedBytes(stationCode, 36, Jt808CodecSupport.GB2312));
        body[41] = (byte) plannedTrips;
        body[42] = (byte) busNo;
        body[43] = (byte) (direction == 1 ? 0 : 1);
        copy(body, 44, Jt808CodecSupport.fixedBytes(lineNumber, 36, Jt808CodecSupport.GB2312));
        copy(body, 80, bcdTime(arrivalTime));
        copy(body, 86, bcdTime(outboundTime));
        copy(body, 92, Jt808CodecSupport.toWord(reportType));
        copy(body, 94, Jt808CodecSupport.toWord(angle));
        if (status == 0) {
            copy(body, 96, coordinate(longitude));
            copy(body, 100, coordinate(latitude));
        } else {
            copy(body, 102, coordinate(longitude));
            copy(body, 106, coordinate(latitude));
        }
        return frame(MSG_REPORT_STATION, terminalId, body);
    }

    public Jt808Frame createGeneralResponse(String terminalId, Jt808GeneralResponse response) {
        ByteArrayOutputStream body = new ByteArrayOutputStream(5);
        write(body, Jt808CodecSupport.toWord(response.getResponseSerialNumber()));
        write(body, Jt808CodecSupport.toWord(response.getResponseMessageId()));
        body.write(response.getResult() & 0xFF);
        return frame(MSG_GENERAL_RESPONSE, terminalId, body.toByteArray());
    }

    public Jt808Frame createDriverLoginLogout(
            String terminalId,
            String deviceVersion,
            String cardNoHex,
            String devicePassword,
            String longitude,
            String latitude,
            String terminalTime,
            int driverStatus,
            int deviceSignMode,
            String simCardIccid
    ) {
        byte[] body = new byte[87];
        body[0] = 0x09;
        copy(body, 1, Jt808CodecSupport.fixedBytes(deviceVersion, 4, Jt808CodecSupport.GB2312));
        copy(body, 5, fixedHexBytes(cardNoHex, 36));
        copy(body, 41, Jt808CodecSupport.fixedBytes(devicePassword, 10, Jt808CodecSupport.GB2312));
        copy(body, 51, coordinate(longitude));
        copy(body, 55, coordinate(latitude));
        copy(body, 59, bcdTime(terminalTime));
        body[65] = (byte) driverStatus;
        body[66] = (byte) deviceSignMode;
        copy(body, 67, Jt808CodecSupport.fixedBytes(simCardIccid, 20, Jt808CodecSupport.GB2312));
        return frame(MSG_REPORT_STATION, terminalId, body);
    }

    public Jt808Frame createProfessionRequest(
            String terminalId,
            String lineNumber,
            String cardNoHex,
            int requestType,
            String terminalTime,
            String longitude,
            String latitude
    ) {
        byte[] body = new byte[88];
        body[0] = (byte) 0x81;
        copy(body, 1, Jt808CodecSupport.fixedBytes(lineNumber, 36, Jt808CodecSupport.GB2312));
        copy(body, 37, fixedHexBytes(cardNoHex, 36));
        body[73] = (byte) requestType;
        copy(body, 74, bcdTime(terminalTime));
        copy(body, 80, coordinate(longitude));
        copy(body, 84, coordinate(latitude));
        return frame(MSG_REPORT_STATION, terminalId, body);
    }

    public Jt808Frame createCrossInfo(
            String terminalId,
            String lineNumber,
            String crossNumber,
            String arrivalTime,
            String outboundTime,
            int angle,
            String longitude,
            String latitude
    ) {
        byte[] body = new byte[95];
        body[0] = 0x32;
        copy(body, 1, Jt808CodecSupport.fixedBytes(lineNumber, 36, Jt808CodecSupport.GB2312));
        copy(body, 37, Jt808CodecSupport.fixedBytes(crossNumber, 36, Jt808CodecSupport.GB2312));
        copy(body, 73, bcdTime(arrivalTime));
        copy(body, 79, bcdTime(outboundTime));
        copy(body, 85, Jt808CodecSupport.toWord(angle));
        copy(body, 87, coordinate(longitude));
        copy(body, 91, coordinate(latitude));
        return frame(MSG_REPORT_STATION, terminalId, body);
    }

    public Jt808Frame createOverspeedInfo(
            String terminalId,
            int overspeedType,
            int overspeedInfoType,
            int dataLength,
            String lineNumber,
            String siteOrCrossNumber,
            String terminalTime,
            long continueSeconds,
            int highSpeed,
            String longitude,
            String latitude,
            int inLimitSpeed,
            int outLimitSpeed,
            int busNo,
            int averageSpeed,
            String cardNoHex,
            int crossType,
            int crossNo,
            int crossLimitSpeed
    ) {
        int bodyLength = overspeedType == 20 || overspeedType == 21 ? 155 : 154;
        byte[] body = new byte[bodyLength];
        body[0] = (byte) overspeedType;
        copy(body, 1, Jt808CodecSupport.toWord(overspeedInfoType));
        body[3] = (byte) overspeedType;
        copy(body, 4, Jt808CodecSupport.toWord(dataLength));
        copy(body, 6, Jt808CodecSupport.fixedBytes(lineNumber, 36, Jt808CodecSupport.GB2312));
        copy(body, 42, Jt808CodecSupport.fixedBytes(siteOrCrossNumber, 36, Jt808CodecSupport.GB2312));
        copy(body, 78, bcdTime(terminalTime));
        copy(body, 84, new byte[]{(byte) (continueSeconds & 0xFF), (byte) ((continueSeconds >> 8) & 0xFF)});
        copy(body, 86, Jt808CodecSupport.toWord(highSpeed));
        copy(body, 88, coordinate(longitude));
        copy(body, 92, coordinate(latitude));
        if (overspeedType == 20 || overspeedType == 21) {
            copy(body, 96, Jt808CodecSupport.toWord(inLimitSpeed));
            copy(body, 98, Jt808CodecSupport.toWord(outLimitSpeed));
            body[100] = (byte) busNo;
            copy(body, 101, Jt808CodecSupport.toWord(averageSpeed));
            copy(body, 103, fixedHexBytes(cardNoHex, 36));
        } else {
            copy(body, 96, Jt808CodecSupport.toWord(crossLimitSpeed));
            body[98] = (byte) crossType;
            body[99] = (byte) crossNo;
            copy(body, 100, Jt808CodecSupport.toWord(averageSpeed));
            copy(body, 102, fixedHexBytes(cardNoHex, 36));
        }
        return frame(MSG_REPORT_STATION, terminalId, body);
    }

    public Jt808Frame createDriverAttendance(
            String terminalId,
            int lineNumber,
            String cardNoHex,
            String terminalTime,
            int driverStatus,
            int type
    ) {
        byte[] body = new byte[16];
        copy(body, 0, Jt808CodecSupport.toDword(lineNumber));
        copy(body, 4, fixedHexBytes(cardNoHex, 4));
        copy(body, 8, bcdTime(terminalTime));
        body[14] = (byte) driverStatus;
        body[15] = (byte) type;
        return frame(0x0B05, terminalId, body);
    }

    /** Legacy CC808 uses 0x0B0B, while the current generic JT808 path uses 0x0B0E. */
    public Jt808Frame createLineSwitchInfo(String terminalId, Jt808LineSwitchSnapshot snapshot) {
        ByteArrayOutputStream body = new ByteArrayOutputStream(20);
        body.write(snapshot.getType() & 0xFF);
        write(body, Jt808CodecSupport.toDword(snapshot.getFirstLineNumber()));
        body.write(snapshot.getFirstDirection() & 0xFF);
        body.write(snapshot.getFirstBusNo() & 0xFF);
        write(body, Jt808CodecSupport.toDword(snapshot.getLineNumber()));
        body.write(snapshot.getDirection() & 0xFF);
        body.write(snapshot.getBusNo() & 0xFF);
        write(body, Jt808CodecSupport.bcdDate(snapshot.getTerminalTime()));
        body.write(snapshot.getReserved() & 0xFF);
        return frame(MSG_LINE_SWITCH_INFO, terminalId, body.toByteArray());
    }

    public byte[] encode(Jt808Frame frame) {
        // CC808 keeps the standard JT808 envelope. Its legacy differences are
        // in the message bodies, especially 0x0900, not in the frame header.
        byte[] body = frame.getBody();
        ByteArrayOutputStream message = new ByteArrayOutputStream(12 + body.length + 1);
        write(message, Jt808CodecSupport.toWord(frame.getMessageId()));
        write(message, Jt808CodecSupport.toWord(body.length));
        write(message, Jt808CodecSupport.terminalIdBytes(frame.getTerminalId()));
        write(message, Jt808CodecSupport.toWord(frame.getSerialNumber()));
        write(message, body);

        byte checksum = 0x00;
        byte[] payload = message.toByteArray();
        for (byte item : payload) {
            checksum ^= item;
        }
        message.write(checksum);

        byte[] escaped = Jt808CodecSupport.escape(message.toByteArray());
        ByteArrayOutputStream framed = new ByteArrayOutputStream(escaped.length + 2);
        framed.write(0x7E);
        write(framed, escaped);
        framed.write(0x7E);
        return framed.toByteArray();
    }

    private Jt808Frame frame(int messageId, String terminalId, byte[] body) {
        return new Jt808Frame(Jt808Variant.CC808, messageId, terminalId, codec.nextSerialNumber(), body);
    }

    private byte[] coordinate(String value) {
        return Jt808CodecSupport.toDword(Jt808CodecSupport.decimalStringToPackedInt(value));
    }

    private byte[] bcdTime(String value) {
        byte[] bcd = Jt808CodecSupport.bcdFromDigits(value);
        byte[] result = new byte[6];
        System.arraycopy(bcd, 0, result, Math.max(0, 6 - Math.min(6, bcd.length)), Math.min(6, bcd.length));
        return result;
    }

    private byte[] fixedHexBytes(String value, int length) {
        byte[] result = new byte[length];
        byte[] raw = parseHex(value);
        System.arraycopy(raw, 0, result, 0, Math.min(raw.length, length));
        return result;
    }

    private byte[] parseHex(String value) {
        String normalized = value == null ? "" : value.replaceAll("[^0-9A-Fa-f]", "");
        if ((normalized.length() & 1) == 1) {
            normalized = "0" + normalized;
        }
        byte[] result = new byte[normalized.length() / 2];
        for (int index = 0; index < result.length; index++) {
            result[index] = (byte) Integer.parseInt(normalized.substring(index * 2, index * 2 + 2), 16);
        }
        return result;
    }

    private static void copy(byte[] target, int offset, byte[] source) {
        System.arraycopy(source, 0, target, offset, Math.min(source.length, target.length - offset));
    }

    private static void write(ByteArrayOutputStream output, byte[] bytes) {
        output.write(bytes, 0, bytes.length);
    }
}
