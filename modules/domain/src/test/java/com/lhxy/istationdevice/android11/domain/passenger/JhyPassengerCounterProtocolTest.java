package com.lhxy.istationdevice.android11.domain.passenger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public final class JhyPassengerCounterProtocolTest {
    @Test
    public void createCurrentCount_matchesM90Command() {
        assertArrayEquals(new byte[] {0x63, 0x00, 0x01, 0x28, (byte) 0xD7, 0x0D}, JhyPassengerCounterProtocol.createCurrentCount());
    }

    @Test
    public void parseCurrentCountFrame_matchesM90Fields() {
        byte[] frame = new byte[] {
                0x63, 0x00, 0x11, 0x28,
                0x0A, 0x00, 0x00, 0x00,
                0x03, 0x00, 0x00, 0x00,
                0x05, 0x00, 0x00, 0x00,
                0x04, 0x00, 0x00, 0x00,
                0x00, 0x0D
        };

        JhyPassengerCounterState state = JhyPassengerCounterProtocol.parseCurrentCountFrame(frame);

        assertNotNull(state);
        assertEquals(10, state.getFrontIn());
        assertEquals(3, state.getFrontOut());
        assertEquals(5, state.getBackIn());
        assertEquals(4, state.getBackOut());
        assertEquals(8, state.getTotal());
    }
}