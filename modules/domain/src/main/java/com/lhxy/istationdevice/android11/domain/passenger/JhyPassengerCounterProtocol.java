package com.lhxy.istationdevice.android11.domain.passenger;

public final class JhyPassengerCounterProtocol {
    public static final int CURRENT_COUNT_FRAME_SIZE = 22;

    private JhyPassengerCounterProtocol() {
    }

    public static byte[] createEmptyCount() {
        byte[] payload = new byte[] {
                0x63,
                0x00,
                0x02,
                0x07,
                0x00,
                0x00,
                0x0D
        };
        payload[5] = checksum(payload, 1, 5);
        return payload;
    }

    public static byte[] createCurrentCount() {
        byte[] payload = new byte[] {
                0x63,
                0x00,
                0x01,
                0x28,
                0x00,
                0x0D
        };
        payload[4] = checksum(payload, 1, 4);
        return payload;
    }

    public static byte[] createCountState(int state) {
        byte[] payload = new byte[] {
                0x63,
                state == 1 ? (byte) 0xA0 : (byte) 0xA1,
                0x01,
                0x06,
                0x00,
                0x0D
        };
        payload[4] = checksum(payload, 1, 4);
        return payload;
    }

    public static JhyPassengerCounterState parseCurrentCountFrame(byte[] frame) {
        if (!isCurrentCountFrame(frame)) {
            return null;
        }
        int frontIn = littleEndianInt(frame, 4);
        int frontOut = littleEndianInt(frame, 8);
        int backIn = littleEndianInt(frame, 12);
        int backOut = littleEndianInt(frame, 16);
        return JhyPassengerCounterState.of(frontIn, frontOut, backIn, backOut);
    }

    public static boolean isCurrentCountFrame(byte[] frame) {
        return frame != null
                && frame.length >= CURRENT_COUNT_FRAME_SIZE
                && unsigned(frame[0]) == 0x63
                && unsigned(frame[1]) == 0x00
                && unsigned(frame[2]) == 0x11
                && unsigned(frame[3]) == 0x28;
    }

    private static byte checksum(byte[] payload, int startInclusive, int endExclusive) {
        int sum = 0;
        for (int i = startInclusive; i < endExclusive && i < payload.length; i++) {
            sum += unsigned(payload[i]);
        }
        return (byte) ((~sum + 1) & 0xFF);
    }

    private static int littleEndianInt(byte[] frame, int offset) {
        return unsigned(frame[offset])
                | (unsigned(frame[offset + 1]) << 8)
                | (unsigned(frame[offset + 2]) << 16)
                | (unsigned(frame[offset + 3]) << 24);
    }

    private static int unsigned(byte value) {
        return value & 0xFF;
    }
}