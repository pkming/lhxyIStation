package com.lhxy.istationdevice.android11.protocol.jt808;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDateTime;

public class Jt808LegacyMessagesTest {
    private static final String TERMINAL_ID = "018612345678";

    @Test
    public void reportStationUsesLegacyJq808ThirtyFiveByteLayout() {
        Jt808Frame frame = new Jt808LegacyMessages().createReportStation(
                Jt808Variant.JT808,
                TERMINAL_ID,
                new Jt808ReportStationSnapshot(
                        281,
                        0,
                        1,
                        6,
                        "22.691517",
                        "114.097603",
                        0,
                        0,
                        LocalDateTime.of(2026, 8, 29, 19, 39, 54)
                )
        );

        assertEquals(0x0B02, frame.getMessageId());
        assertEquals(35, frame.getBody().length);
        assertArrayEquals(new byte[]{0, 0, 0x01, 0x19, 0, 1}, slice(frame.getBody(), 0, 6));
        assertArrayEquals(new byte[]{0, 0, 0, 0, 6, 0}, slice(frame.getBody(), 6, 12));
        assertEquals(1, frame.getBody()[5] & 0xFF);
        assertEquals(6, frame.getBody()[10] & 0xFF);
        assertArrayEquals(new byte[]{0x01, 0x5A, 0x3E, (byte) 0xBD}, slice(frame.getBody(), 12, 16));
        assertArrayEquals(new byte[]{0x06, (byte) 0xCC, (byte) 0xFD, (byte) 0xC3}, slice(frame.getBody(), 16, 20));
        assertArrayEquals(new byte[]{0x26, 0x08, 0x29, 0x19, 0x39, 0x54}, slice(frame.getBody(), 26, 32));
    }

    private byte[] slice(byte[] source, int from, int to) {
        byte[] result = new byte[to - from];
        System.arraycopy(source, from, result, 0, result.length);
        return result;
    }
}
