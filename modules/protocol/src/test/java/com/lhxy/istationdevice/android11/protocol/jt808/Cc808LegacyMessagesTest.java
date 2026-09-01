package com.lhxy.istationdevice.android11.protocol.jt808;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import java.time.LocalDateTime;

public class Cc808LegacyMessagesTest {
    private static final String TERMINAL_ID = "018612345678";
    private final Cc808LegacyMessages messages = new Cc808LegacyMessages();
    private final Jt808TerminalProfile profile = new Jt808TerminalProfile(
            TERMINAL_ID,
            "K80V0101",
            "粤B00000",
            "",
            0xABE0,
            0xAD0C,
            0
    );

    @Test
    public void legacyBodiesAndMessageIdsMatchCc808() {
        Jt808Frame register = messages.createRegister(profile);
        assertMessage(0x0100, 46, register);
        byte[] registerBody = register.getBody();
        assertEquals((byte) 0x34, registerBody[13]);
        assertEquals((byte) 0x30, registerBody[40]);
        assertEquals((byte) 0x35, registerBody[44]);
        assertMessage(0x0002, 0, messages.createHeartbeat(TERMINAL_ID));
        Jt808Frame position = messages.createPositionReport(
                profile,
                new Jt808PositionSnapshot(0, 8, "22.543096", "114.057865", 36, 90, 0,
                        LocalDateTime.of(2026, 4, 13, 9, 30, 15)),
                "101", 1, 1, 1, 8);
        assertMessage(0x0200, 78, position);
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 8, 0, 0x13},
                slice(position.getBody(), 0, 8));
        assertArrayEquals(new byte[]{0x01, 0x57, (byte) 0xFA, (byte) 0xF8},
                slice(position.getBody(), 8, 12));
        assertEquals((byte) 0, position.getBody()[70]);
        assertEquals((byte) 1, position.getBody()[71]);
        assertEquals((byte) 1, position.getBody()[72]);
        assertEquals((byte) 8, position.getBody()[77]);
        assertMessage(0x0900, 104, messages.createReportStation(
                TERMINAL_ID, 0, "站点A", 1, 2, 1, "101",
                "260413093015", "000000000000", 0, 90,
                "114.057865", "22.543096", 0));
        assertMessage(0x0900, 112, messages.createReportStation(
                TERMINAL_ID, 0, "站点A", 1, 2, 1, "101",
                "260413093015", "260413093046", 0, 90,
                "114.057865", "22.543096", 1));
        assertMessage(0x0B0B, 20, messages.createLineSwitchInfo(
                TERMINAL_ID, new Jt808LineSwitchSnapshot(1, 101, 0, 1, 202, 1, 2,
                        LocalDateTime.of(2026, 4, 13, 9, 35, 20), 0)));
    }

    @Test
    public void legacyEnvelopeUsesStandardMessageIdBeforeBodyProperties() {
        Jt808Frame source = messages.createAuthority(TERMINAL_ID, new byte[]{0x7E, 0x7D, 0x01});
        byte[] encoded = messages.encode(source);
        byte[] payload = Jt808CodecSupport.unescape(slice(encoded, 1, encoded.length - 1));

        assertEquals(Jt808Variant.CC808, source.getVariant());
        assertArrayEquals(new byte[]{0x01, 0x02, 0x00, 0x03}, slice(payload, 0, 4));
        assertArrayEquals(new byte[]{0x01, (byte) 0x86, 0x12, 0x34, 0x56, 0x78}, slice(payload, 4, 10));
        assertArrayEquals(new byte[]{0x7E, 0x7D, 0x01}, slice(payload, 12, 15));
    }

    @Test
    public void reportStationCarriesLegacySiteCodeAtOffsetFive() {
        Jt808Frame frame = messages.createReportStation(
                TERMINAL_ID, 1, "UID-A", 0, 1, 1, "101",
                "260413093015", "000000000000", 0, 0,
                "114.057865", "22.543096", 0);

        assertArrayEquals(new byte[]{0, 0, 0, 1},
                slice(frame.getBody(), 1, 5));
        assertArrayEquals(new byte[]{'U', 'I', 'D', '-', 'A'},
                slice(frame.getBody(), 5, 10));
    }

    @Test
    public void reportStationZeroFillsMissingSiteCode() {
        Jt808Frame frame = messages.createReportStation(
                TERMINAL_ID, 1, "", 0, 1, 1, "101",
                "260413093015", "000000000000", 0, 0,
                "114.057865", "22.543096", 0);

        assertArrayEquals(new byte[36], slice(frame.getBody(), 5, 41));
    }

    @Test
    public void legacySpecialBodiesHaveExpectedLengths() {
        assertMessage(0x0900, 87, messages.createDriverLoginLogout(
                TERMINAL_ID, "0.1.13", "11223344", "1234", "114.057865", "22.543096",
                "260413093015", 0, 1, TERMINAL_ID));
        assertMessage(0x0900, 88, messages.createProfessionRequest(
                TERMINAL_ID, "101", "11223344", 5, "260413093015", "114.057865", "22.543096"));
        assertMessage(0x0900, 95, messages.createCrossInfo(
                TERMINAL_ID, "101", "CROSS-1", "260413093015", "000000000000", 90,
                "114.057865", "22.543096"));
        assertMessage(0x0900, 154, messages.createOverspeedInfo(
                TERMINAL_ID, 22, 65320, 148, "101", "CROSS-1", "260413093015", 12,
                68, "114.057865", "22.543096", 0, 0, 0, 60, "11223344", 1, 2, 40));
        assertMessage(0x0B05, 16, messages.createDriverAttendance(
                TERMINAL_ID, 101, "11223344", "260413093015", 0, 1));
    }

    private void assertMessage(int messageId, int bodyLength, Jt808Frame frame) {
        assertEquals(Jt808Variant.CC808, frame.getVariant());
        assertEquals(messageId, frame.getMessageId());
        assertEquals(TERMINAL_ID, frame.getTerminalId());
        assertEquals(bodyLength, frame.getBody().length);
    }

    private byte[] slice(byte[] source, int from, int to) {
        byte[] result = new byte[to - from];
        System.arraycopy(source, from, result, 0, result.length);
        return result;
    }
}
