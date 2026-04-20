package com.lhxy.istationdevice.android11.protocol.jt808;

import com.lhxy.istationdevice.android11.core.Hexs;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public final class Jt808LegacyMessages {
    private static final int MSG_GENERAL_RESPONSE = 0x0001;
    private static final int MSG_REGISTER = 0x0100;
    private static final int MSG_AUTHORITY = 0x0102;
    private static final int MSG_HEARTBEAT = 0x0002;
    private static final int MSG_POSITION_REPORT = 0x0200;
    private static final int MSG_REPORT_STATION = 0x0B02;
    private static final int MSG_VIOLATION_INFO = 0x0B04;
    private static final int MSG_DRIVER_ATTENDANCE = 0x0B05;
    private static final int MSG_TIMING_RESPONSE = 0x0B06;
    private static final int MSG_TERMINAL_PROPERTIES_RESPONSE = 0x0107;
    private static final int MSG_REQUEST_MESSAGE = 0x0B09;
    private static final int MSG_UPGRADE_NOTIFICATION = 0x0B0A;
    private static final int MSG_LINE_SWITCH_INFO = 0x0B0E;
    private static final int MSG_AL808_SWITCH_LINE_RESPONSE = 0xDB01;
    private static final int MSG_AL808_LINE_SWITCH_INFO = 0xDB0E;
    private static final int DEFAULT_STATUS_FLAG = 0x00080013;

    private final Jt808Codec codec = new Jt808Codec();

    public Jt808Frame createHeartbeat(Jt808Variant variant, String terminalId) {
        return new Jt808Frame(variant, MSG_HEARTBEAT, terminalId, codec.nextSerialNumber(), new byte[0]);
    }

    public Jt808Frame createGeneralResponse(Jt808Variant variant, String terminalId, Jt808GeneralResponse response) {
        ByteArrayOutputStream body = new ByteArrayOutputStream(5);
        write(body, Jt808CodecSupport.toWord(response.getResponseSerialNumber()));
        write(body, Jt808CodecSupport.toWord(response.getResponseMessageId()));
        body.write(response.getResult() & 0xFF);
        return new Jt808Frame(
                variant,
                MSG_GENERAL_RESPONSE,
                terminalId,
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createAuthority(Jt808Variant variant, Jt808TerminalProfile profile) {
        return new Jt808Frame(
                variant,
                MSG_AUTHORITY,
                profile.getTerminalId(),
                codec.nextSerialNumber(),
                Hexs.fromHex(profile.getAuthorityHex())
        );
    }

    public Jt808Frame createRegister(Jt808Variant variant, Jt808TerminalProfile profile) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        write(body, Jt808CodecSupport.toWord(profile.getProvinceId()));
        write(body, Jt808CodecSupport.toWord(profile.getCityId()));
        write(body, Jt808CodecSupport.fixedBytes(variant.getManufacturerId(), 5, Jt808CodecSupport.GB2312));
        write(body, Jt808CodecSupport.fixedBytes(profile.getTerminalModel(), variant.getTerminalModelLength(), Jt808CodecSupport.GB2312));
        write(body, Jt808CodecSupport.fixedBytes(profile.getTerminalId(), 7, Jt808CodecSupport.GB2312));
        body.write(profile.getPlateColor() & 0xFF);
        write(body, Jt808CodecSupport.fixedBytes(profile.getVehicleId(), 9, Jt808CodecSupport.GB2312));

        return new Jt808Frame(
                variant,
                MSG_REGISTER,
                profile.getTerminalId(),
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createPositionReport(Jt808Variant variant, Jt808TerminalProfile profile, Jt808PositionSnapshot snapshot) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        write(body, Jt808CodecSupport.toDword(snapshot.getAlarmFlag()));
        write(body, Jt808CodecSupport.toDword(snapshot.getStatusFlag()));
        write(body, Jt808CodecSupport.toDword(Jt808CodecSupport.decimalStringToPackedInt(snapshot.getLatitude())));
        write(body, Jt808CodecSupport.toDword(Jt808CodecSupport.decimalStringToPackedInt(snapshot.getLongitude())));
        write(body, Jt808CodecSupport.toWord(0));
        write(body, Jt808CodecSupport.toWord(snapshot.getSpeed()));
        write(body, Jt808CodecSupport.toWord(snapshot.getDirection()));
        LocalDateTime time = snapshot.getTerminalTime() == null ? LocalDateTime.now() : snapshot.getTerminalTime();
        write(body, Jt808CodecSupport.bcdDate(time));
        body.write(0x01);
        body.write(0x04);
        write(body, Jt808CodecSupport.toDword(snapshot.getTotalMileage()));

        return new Jt808Frame(
                variant,
                MSG_POSITION_REPORT,
                profile.getTerminalId(),
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createReportStation(Jt808Variant variant, String terminalId, Jt808ReportStationSnapshot snapshot) {
        ByteArrayOutputStream body = new ByteArrayOutputStream(variant == Jt808Variant.AL808 ? 35 : 39);
        write(body, Jt808CodecSupport.toDword(snapshot.getLineNumber()));
        body.write(snapshot.getStatus() & 0xFF);
        body.write(snapshot.getDirection() & 0xFF);

        if (variant == Jt808Variant.AL808) {
            write(body, new byte[]{0x00, 0x00, 0x00, 0x00});
            body.write(snapshot.getBusNo() & 0xFF);
            body.write(0x00);
        } else {
            byte[] busNoBytes = Jt808CodecSupport.toDword(snapshot.getBusNo());
            body.write(0x00);
            body.write(0x00);
            body.write(busNoBytes[2]);
            body.write(busNoBytes[3]);
            body.write(snapshot.getBusNo() & 0xFF);
            body.write(0x00);
        }

        write(body, Jt808CodecSupport.toDword(Jt808CodecSupport.decimalStringToPackedInt(snapshot.getLatitude())));
        write(body, Jt808CodecSupport.toDword(Jt808CodecSupport.decimalStringToPackedInt(snapshot.getLongitude())));
        write(body, Jt808CodecSupport.toWord(0));
        write(body, Jt808CodecSupport.toWord(snapshot.getSpeed()));
        write(body, Jt808CodecSupport.toWord(snapshot.getAngle()));
        write(body, Jt808CodecSupport.bcdDate(snapshot.getTerminalTime()));
        if (variant == Jt808Variant.AL808) {
            write(body, new byte[]{0x00, 0x00, 0x00});
        } else {
            write(body, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        }
        return new Jt808Frame(
                variant,
                MSG_REPORT_STATION,
                terminalId,
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createDriverAttendance(Jt808Variant variant, String terminalId, Jt808DriverAttendance attendance) {
        ByteArrayOutputStream body = new ByteArrayOutputStream(24);
        write(body, Jt808CodecSupport.toDword(attendance.getLineNumber()));
        writeDriverCard(body, variant, attendance.getCardNumberHex());
        write(body, Jt808CodecSupport.bcdDate(attendance.getTerminalTime()));
        body.write(attendance.getDriverStatus() & 0xFF);
        body.write(attendance.getType() & 0xFF);
        return new Jt808Frame(
                variant,
                MSG_DRIVER_ATTENDANCE,
                terminalId,
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createTimingResponse(Jt808Variant variant, String terminalId, Jt808TimingResponse timingResponse) {
        LocalDateTime time = timingResponse.getTerminalTime() == null ? LocalDateTime.now() : timingResponse.getTerminalTime();
        return new Jt808Frame(
                variant,
                MSG_TIMING_RESPONSE,
                terminalId,
                codec.nextSerialNumber(),
                Jt808CodecSupport.bcdDate(time)
        );
    }

    public Jt808Frame createTerminalPropertiesResponse(
            Jt808Variant variant,
            String terminalId,
            Jt808TerminalPropertiesResponse response
    ) {
        byte[] hardwareVersion = response.getHardwareVersion().getBytes(Jt808CodecSupport.GB2312);
        byte[] softwareVersion = response.getSoftwareVersion().getBytes(Jt808CodecSupport.GB2312);

        ByteArrayOutputStream body = new ByteArrayOutputStream(64);
        write(body, new byte[]{0x00, (byte) 0xFF});
        write(body, Jt808CodecSupport.fixedBytes(variant.getManufacturerId(), 5, StandardCharsets.US_ASCII));
        write(body, Jt808CodecSupport.fixedBytes(response.getTerminalModel(), 20, Jt808CodecSupport.GB2312));
        write(body, new byte[7]);
        write(body, new byte[10]);
        body.write(hardwareVersion.length & 0xFF);
        write(body, hardwareVersion);
        body.write(softwareVersion.length & 0xFF);
        write(body, softwareVersion);
        body.write(response.getCommunicationCapability() & 0xFF);
        body.write(response.getGnssCapability() & 0xFF);
        return new Jt808Frame(
                variant,
                MSG_TERMINAL_PROPERTIES_RESPONSE,
                terminalId,
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createRequestMessage(Jt808Variant variant, String terminalId, Jt808RequestMessage requestMessage) {
        ByteArrayOutputStream body = new ByteArrayOutputStream(24);
        write(body, Jt808CodecSupport.toDword(requestMessage.getLineNumber()));
        writeDriverCard(body, variant, requestMessage.getCardNumberHex());
        body.write(requestMessage.getType() & 0xFF);
        write(body, Jt808CodecSupport.bcdDate(requestMessage.getTerminalTime()));
        return new Jt808Frame(
                variant,
                MSG_REQUEST_MESSAGE,
                terminalId,
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createUpgradeNotification(String terminalId, Jt808UpgradeNotification notification) {
        ByteArrayOutputStream body = new ByteArrayOutputStream(4);
        write(body, Jt808CodecSupport.toWord(notification.getResponseSerialNumber()));
        body.write(notification.getUpgradeStatus() & 0xFF);
        body.write(notification.getUpgradeProgress() & 0xFF);
        return new Jt808Frame(
                Jt808Variant.JT808,
                MSG_UPGRADE_NOTIFICATION,
                terminalId,
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createViolationInfo(String terminalId, Jt808ViolationSnapshot snapshot) {
        byte[] promptInfoBytes = snapshot.getPromptInfo().getBytes(Jt808CodecSupport.GB2312);
        ByteArrayOutputStream body = new ByteArrayOutputStream(31 + promptInfoBytes.length + 1);
        write(body, Jt808CodecSupport.toDword(snapshot.getLineNumber()));
        body.write(snapshot.getOverSpeed() & 0xFF);
        write(body, Jt808CodecSupport.toWord(snapshot.getSpeed()));
        write(body, Jt808CodecSupport.toWord(snapshot.getSpeedLimit()));
        write(body, Jt808CodecSupport.toDword(Jt808CodecSupport.decimalStringToPackedInt(snapshot.getLatitude())));
        write(body, Jt808CodecSupport.toDword(Jt808CodecSupport.decimalStringToPackedInt(snapshot.getLongitude())));
        write(body, Jt808CodecSupport.toWord(0));
        write(body, Jt808CodecSupport.toWord(snapshot.getSpeed()));
        write(body, Jt808CodecSupport.toWord(snapshot.getAngle()));
        write(body, Jt808CodecSupport.bcdDate(snapshot.getTerminalTime()));
        body.write(0x00);
        write(body, promptInfoBytes);
        body.write(0x00);
        return new Jt808Frame(
                Jt808Variant.JT808,
                MSG_VIOLATION_INFO,
                terminalId,
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createLineSwitchInfo(Jt808Variant variant, String terminalId, Jt808LineSwitchSnapshot snapshot) {
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
        return new Jt808Frame(
                variant,
                variant == Jt808Variant.AL808 ? MSG_AL808_LINE_SWITCH_INFO : MSG_LINE_SWITCH_INFO,
                terminalId,
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public Jt808Frame createAl808SwitchingLineResponse(String terminalId, Jt808LineSwitchSnapshot snapshot) {
        ByteArrayOutputStream body = new ByteArrayOutputStream(13);
        body.write(snapshot.getType() & 0xFF);
        write(body, Jt808CodecSupport.toDword(snapshot.getFirstLineNumber()));
        body.write(snapshot.getFirstDirection() & 0xFF);
        body.write(snapshot.getFirstBusNo() & 0xFF);
        write(body, Jt808CodecSupport.toDword(snapshot.getLineNumber()));
        body.write(snapshot.getDirection() & 0xFF);
        body.write(snapshot.getBusNo() & 0xFF);
        return new Jt808Frame(
                Jt808Variant.AL808,
                MSG_AL808_SWITCH_LINE_RESPONSE,
                terminalId,
                codec.nextSerialNumber(),
                body.toByteArray()
        );
    }

    public byte[] encode(Jt808Frame frame) {
        return codec.encode(frame);
    }

    public static Jt808PositionSnapshot defaultPositionSnapshot() {
        return new Jt808PositionSnapshot(
                0,
                DEFAULT_STATUS_FLAG,
                "22.543096",
                "114.057865",
                36,
                90,
                125430,
                LocalDateTime.of(2026, 4, 13, 9, 30, 15)
        );
    }

    public static Jt808ReportStationSnapshot defaultReportStationSnapshot() {
        return new Jt808ReportStationSnapshot(
                202,
                0x02,
                0x01,
                0x08,
                "22.543096",
                "114.057865",
                34,
                91,
                LocalDateTime.of(2026, 4, 13, 9, 32, 46)
        );
    }

    public static Jt808GeneralResponse defaultGeneralResponse() {
        return new Jt808GeneralResponse(0x1024, MSG_REGISTER, 0x00);
    }

    public static Jt808DriverAttendance defaultDriverAttendance() {
        return new Jt808DriverAttendance(
                202,
                "1122334455",
                LocalDateTime.of(2026, 4, 13, 9, 38, 12),
                0x01,
                0x00
        );
    }

    public static Jt808RequestMessage defaultRequestMessage() {
        return new Jt808RequestMessage(
                202,
                "1122334455",
                0x02,
                LocalDateTime.of(2026, 4, 13, 9, 39, 28),
                "司机请求调度确认"
        );
    }

    public static Jt808TimingResponse defaultTimingResponse() {
        return new Jt808TimingResponse(LocalDateTime.of(2026, 4, 13, 9, 40, 56));
    }

    public static Jt808TerminalPropertiesResponse defaultTerminalPropertiesResponse() {
        return new Jt808TerminalPropertiesResponse(
                "K80",
                "618V5",
                "K80V0603",
                0x03,
                0x3F
        );
    }

    public static Jt808ViolationSnapshot defaultViolationSnapshot() {
        return new Jt808ViolationSnapshot(
                202,
                0x01,
                68,
                60,
                "22.543096",
                "114.057865",
                90,
                LocalDateTime.of(2026, 4, 13, 9, 36, 18),
                "超速提醒"
        );
    }

    public static Jt808UpgradeNotification defaultUpgradeNotification() {
        return new Jt808UpgradeNotification(0x1025, 0x01, 80);
    }

    public static Jt808LineSwitchSnapshot defaultLineSwitchSnapshot() {
        return new Jt808LineSwitchSnapshot(
                0x01,
                101,
                0x00,
                0x01,
                202,
                0x01,
                0x02,
                LocalDateTime.of(2026, 4, 13, 9, 35, 20),
                0x00
        );
    }

    private static void write(ByteArrayOutputStream output, byte[] bytes) {
        output.write(bytes, 0, bytes.length);
    }

    /**
     * 旧项目这里存在两套写法：
     * JT808 固定 4 字节卡号，AL808 则保留完整卡号并补 0 结尾。
     */
    private static void writeDriverCard(ByteArrayOutputStream body, Jt808Variant variant, String cardNumberHex) {
        byte[] cardBytes = Hexs.fromHex(cardNumberHex);
        if (variant == Jt808Variant.AL808) {
            write(body, cardBytes);
            body.write(0x00);
            return;
        }

        byte[] fixedCard = new byte[4];
        int limit = Math.min(cardBytes.length, fixedCard.length);
        System.arraycopy(cardBytes, 0, fixedCard, 0, limit);
        write(body, fixedCard);
    }
}
