package com.lhxy.istationdevice.android11.protocol.jt808;

import com.lhxy.istationdevice.android11.protocol.ProtocolEnvelope;

import java.util.ArrayList;
import java.util.List;

public final class Jt808ProtocolDemo {
    private Jt808ProtocolDemo() {
    }

    /**
     * 生成完整调度样例。
     */
    public static List<ProtocolEnvelope> generateDemoEnvelopes() {
        Jt808LegacyMessages messages = new Jt808LegacyMessages();
        Jt808TerminalProfile jt808Profile = new Jt808TerminalProfile(
                "1356001",
                "K80V0101",
                "粤B00235",
                "313233343536",
                0xABE0,
                0xAD0C,
                0x00
        );
        Jt808TerminalProfile al808Profile = new Jt808TerminalProfile(
                "1356002",
                "M90A1101",
                "粤B10235",
                "414C31313031",
                0xABE0,
                0xAD0C,
                0x00
        );

        Jt808PositionSnapshot snapshot = Jt808LegacyMessages.defaultPositionSnapshot();
        Jt808ReportStationSnapshot reportStationSnapshot = Jt808LegacyMessages.defaultReportStationSnapshot();
        Jt808GeneralResponse generalResponse = Jt808LegacyMessages.defaultGeneralResponse();
        Jt808DriverAttendance driverAttendance = Jt808LegacyMessages.defaultDriverAttendance();
        Jt808RequestMessage requestMessage = Jt808LegacyMessages.defaultRequestMessage();
        Jt808TimingResponse timingResponse = Jt808LegacyMessages.defaultTimingResponse();
        Jt808TerminalPropertiesResponse terminalPropertiesResponse = Jt808LegacyMessages.defaultTerminalPropertiesResponse();
        Jt808ViolationSnapshot violationSnapshot = Jt808LegacyMessages.defaultViolationSnapshot();
        Jt808UpgradeNotification upgradeNotification = Jt808LegacyMessages.defaultUpgradeNotification();
        Jt808LineSwitchSnapshot lineSwitchSnapshot = Jt808LegacyMessages.defaultLineSwitchSnapshot();
        List<ProtocolEnvelope> envelopes = new ArrayList<>();
        add(envelopes, messages, Jt808Variant.JT808, "REGISTER", messages.createRegister(Jt808Variant.JT808, jt808Profile));
        add(envelopes, messages, Jt808Variant.JT808, "AUTHORITY", messages.createAuthority(Jt808Variant.JT808, jt808Profile));
        add(envelopes, messages, Jt808Variant.JT808, "HEARTBEAT", messages.createHeartbeat(Jt808Variant.JT808, jt808Profile.getTerminalId()));
        add(envelopes, messages, Jt808Variant.JT808, "POSITION", messages.createPositionReport(Jt808Variant.JT808, jt808Profile, snapshot));
        add(envelopes, messages, Jt808Variant.JT808, "REPORT_STATION", messages.createReportStation(Jt808Variant.JT808, jt808Profile.getTerminalId(), reportStationSnapshot));
        add(envelopes, messages, Jt808Variant.JT808, "GENERAL_RESPONSE", messages.createGeneralResponse(Jt808Variant.JT808, jt808Profile.getTerminalId(), generalResponse));
        add(envelopes, messages, Jt808Variant.JT808, "DRIVER_ATTENDANCE", messages.createDriverAttendance(Jt808Variant.JT808, jt808Profile.getTerminalId(), driverAttendance));
        add(envelopes, messages, Jt808Variant.JT808, "REQUEST_MESSAGE", messages.createRequestMessage(Jt808Variant.JT808, jt808Profile.getTerminalId(), requestMessage));
        add(envelopes, messages, Jt808Variant.JT808, "UPGRADE_NOTIFICATION", messages.createUpgradeNotification(jt808Profile.getTerminalId(), upgradeNotification));
        add(envelopes, messages, Jt808Variant.JT808, "VIOLATION", messages.createViolationInfo(jt808Profile.getTerminalId(), violationSnapshot));
        add(envelopes, messages, Jt808Variant.JT808, "LINE_SWITCH", messages.createLineSwitchInfo(Jt808Variant.JT808, jt808Profile.getTerminalId(), lineSwitchSnapshot));

        add(envelopes, messages, Jt808Variant.AL808, "REGISTER", messages.createRegister(Jt808Variant.AL808, al808Profile));
        add(envelopes, messages, Jt808Variant.AL808, "AUTHORITY", messages.createAuthority(Jt808Variant.AL808, al808Profile));
        add(envelopes, messages, Jt808Variant.AL808, "HEARTBEAT", messages.createHeartbeat(Jt808Variant.AL808, al808Profile.getTerminalId()));
        add(envelopes, messages, Jt808Variant.AL808, "POSITION", messages.createPositionReport(Jt808Variant.AL808, al808Profile, snapshot));
        add(envelopes, messages, Jt808Variant.AL808, "REPORT_STATION", messages.createReportStation(Jt808Variant.AL808, al808Profile.getTerminalId(), reportStationSnapshot));
        add(envelopes, messages, Jt808Variant.AL808, "GENERAL_RESPONSE", messages.createGeneralResponse(Jt808Variant.AL808, al808Profile.getTerminalId(), generalResponse));
        add(envelopes, messages, Jt808Variant.AL808, "DRIVER_ATTENDANCE", messages.createDriverAttendance(Jt808Variant.AL808, al808Profile.getTerminalId(), driverAttendance));
        add(envelopes, messages, Jt808Variant.AL808, "TIMING_RESPONSE", messages.createTimingResponse(Jt808Variant.AL808, al808Profile.getTerminalId(), timingResponse));
        add(envelopes, messages, Jt808Variant.AL808, "TERMINAL_PROPERTIES", messages.createTerminalPropertiesResponse(Jt808Variant.AL808, al808Profile.getTerminalId(), terminalPropertiesResponse));
        add(envelopes, messages, Jt808Variant.AL808, "REQUEST_MESSAGE", messages.createRequestMessage(Jt808Variant.AL808, al808Profile.getTerminalId(), requestMessage));
        add(envelopes, messages, Jt808Variant.AL808, "LINE_SWITCH", messages.createLineSwitchInfo(Jt808Variant.AL808, al808Profile.getTerminalId(), lineSwitchSnapshot));
        add(envelopes, messages, Jt808Variant.AL808, "SWITCH_LINE_RESPONSE", messages.createAl808SwitchingLineResponse(al808Profile.getTerminalId(), lineSwitchSnapshot));

        return envelopes;
    }

    /**
     * 生成调度主链样例。
     */
    public static List<ProtocolEnvelope> generateDispatchDemoEnvelopes() {
        return filterByNames(
                generateDemoEnvelopes(),
                "REGISTER",
                "AUTHORITY",
                "HEARTBEAT",
                "POSITION",
                "REQUEST_MESSAGE",
                "LINE_SWITCH",
                "GENERAL_RESPONSE"
        );
    }

    /**
     * 生成签到样例。
     */
    public static List<ProtocolEnvelope> generateSignInDemoEnvelopes() {
        return filterByNames(
                generateDemoEnvelopes(),
                "DRIVER_ATTENDANCE",
                "GENERAL_RESPONSE"
        );
    }

    /**
     * 生成升级样例。
     */
    public static List<ProtocolEnvelope> generateUpgradeDemoEnvelopes() {
        return filterByNames(
                generateDemoEnvelopes(),
                "UPGRADE_NOTIFICATION",
                "TERMINAL_PROPERTIES"
        );
    }

    private static void add(
            List<ProtocolEnvelope> envelopes,
            Jt808LegacyMessages messages,
            Jt808Variant variant,
            String suffix,
            Jt808Frame frame
    ) {
        envelopes.add(new ProtocolEnvelope(
                variant.getProtocolName() + "_" + suffix,
                variant.getChannelName(),
                messages.encode(frame)
        ));
    }

    private static List<ProtocolEnvelope> filterByNames(List<ProtocolEnvelope> source, String... keywords) {
        List<ProtocolEnvelope> result = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return result;
        }
        for (ProtocolEnvelope envelope : source) {
            String name = envelope.getProtocolName();
            if (name == null) {
                continue;
            }
            for (String keyword : keywords) {
                if (name.contains(keyword)) {
                    result.add(envelope);
                    break;
                }
            }
        }
        return result;
    }
}
