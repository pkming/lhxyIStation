package com.lhxy.istationdevice.android11.domain.passenger;

public final class JhyPassengerCounterProtocol {
    public static final int CURRENT_COUNT_FRAME_SIZE = 22;
    public static final int REAL_CURRENT_COUNT_FRAME_SIZE = 21;
    public static final int MIN_CURRENT_COUNT_FRAME_SIZE = REAL_CURRENT_COUNT_FRAME_SIZE;

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
        if (isRealCurrentCountFrame(frame)) {
            return JhyPassengerCounterState.of(
                    bigEndianInt(frame, 4),
                    bigEndianInt(frame, 8),
                    bigEndianInt(frame, 12),
                    0
            );
        }
        int frontIn = littleEndianInt(frame, 4);
        int frontOut = littleEndianInt(frame, 8);
        int backIn = littleEndianInt(frame, 12);
        int backOut = littleEndianInt(frame, 16);
        return JhyPassengerCounterState.of(frontIn, frontOut, backIn, backOut);
    }

    public static boolean isCurrentCountFrame(byte[] frame) {
        if (frame == null
                || frame.length < MIN_CURRENT_COUNT_FRAME_SIZE
                || unsigned(frame[0]) != 0x63
                || unsigned(frame[1]) != 0x00) {
            return false;
        }
        return isDocumentedCurrentCountFrame(frame)
                || isRealCurrentCountFrame(frame);
    }

    public static int currentCountFrameSize(byte[] buffer, int start) {
        if (buffer == null || start < 0 || start + 3 >= buffer.length) {
            return -1;
        }
        if (unsigned(buffer[start]) != 0x63 || unsigned(buffer[start + 1]) != 0x00) {
            return -1;
        }
        if (unsigned(buffer[start + 2]) == 0x11 && unsigned(buffer[start + 3]) == 0x28) {
            return CURRENT_COUNT_FRAME_SIZE;
        }
        if (unsigned(buffer[start + 2]) == 0x28 && unsigned(buffer[start + 3]) == 0x06) {
            return REAL_CURRENT_COUNT_FRAME_SIZE;
        }
        return -1;
    }

    private static boolean isDocumentedCurrentCountFrame(byte[] frame) {
        return frame.length >= CURRENT_COUNT_FRAME_SIZE
                && unsigned(frame[2]) == 0x11
                && unsigned(frame[3]) == 0x28;
    }

    private static boolean isRealCurrentCountFrame(byte[] frame) {
        return frame.length >= REAL_CURRENT_COUNT_FRAME_SIZE
                && unsigned(frame[2]) == 0x28
                && unsigned(frame[3]) == 0x06
                && unsigned(frame[REAL_CURRENT_COUNT_FRAME_SIZE - 1]) == 0x0A;
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

    private static int bigEndianInt(byte[] frame, int offset) {
        return (unsigned(frame[offset]) << 24)
                | (unsigned(frame[offset + 1]) << 16)
                | (unsigned(frame[offset + 2]) << 8)
                | unsigned(frame[offset + 3]);
    }

    private static int unsigned(byte value) {
        return value & 0xFF;
    }
}
