package com.lhxy.istationdevice.android11.protocol.jt808;

import com.lhxy.istationdevice.android11.core.Hexs;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

final class Jt808CodecSupport {
    static final Charset GB2312 = Charset.forName("GB2312");
    private static final DateTimeFormatter BCD_TIME = DateTimeFormatter.ofPattern("yyMMddHHmmss");
    private static final AtomicInteger NEXT_SERIAL = new AtomicInteger(1);

    private Jt808CodecSupport() {
    }

    static int nextSerialNumber() {
        return NEXT_SERIAL.getAndUpdate(current -> current >= 0xFFFF ? 1 : current + 1);
    }

    static byte[] toWord(int value) {
        return new byte[]{
                (byte) ((value >>> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    static byte[] toDword(long value) {
        return new byte[]{
                (byte) ((value >>> 24) & 0xFF),
                (byte) ((value >>> 16) & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    static byte[] terminalIdBytes(String terminalId) {
        byte[] raw = bcdFromDigits(terminalId);
        byte[] result = new byte[6];
        int copyLength = Math.min(result.length, raw.length);
        System.arraycopy(raw, 0, result, 0, copyLength);
        return result;
    }

    static byte[] bcdDate(LocalDateTime dateTime) {
        return bcdFromDigits(dateTime.format(BCD_TIME));
    }

    static byte[] bcdFromDigits(String value) {
        String digits = value == null ? "" : value.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return new byte[0];
        }
        if (digits.length() % 2 != 0) {
            digits = "0" + digits;
        }
        return Hexs.fromHex(digits);
    }

    static byte[] fixedBytes(String value, int length, Charset charset) {
        byte[] result = new byte[length];
        if (length == 0 || value == null || value.isEmpty()) {
            return result;
        }
        byte[] raw = value.getBytes(charset);
        System.arraycopy(raw, 0, result, 0, Math.min(length, raw.length));
        return result;
    }

    static int decimalStringToPackedInt(String value) {
        String digits = value == null ? "" : value.trim().replace(".", "");
        if (digits.isEmpty()) {
            return 0;
        }
        String clean = digits.replaceAll("\\D", "");
        if (clean.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(clean);
    }

    static byte[] escape(byte[] content) {
        ByteArrayOutputStream output = new ByteArrayOutputStream(content.length + 8);
        for (byte item : content) {
            if (item == 0x7E) {
                output.write(0x7D);
                output.write(0x02);
            } else if (item == 0x7D) {
                output.write(0x7D);
                output.write(0x01);
            } else {
                output.write(item);
            }
        }
        return output.toByteArray();
    }

    static byte[] unescape(byte[] content) {
        ByteArrayOutputStream output = new ByteArrayOutputStream(content.length);
        for (int index = 0; index < content.length; index++) {
            byte item = content[index];
            if (item == 0x7D && index + 1 < content.length) {
                byte next = content[index + 1];
                if (next == 0x01) {
                    output.write(0x7D);
                    index++;
                    continue;
                }
                if (next == 0x02) {
                    output.write(0x7E);
                    index++;
                    continue;
                }
            }
            output.write(item);
        }
        return output.toByteArray();
    }
}
