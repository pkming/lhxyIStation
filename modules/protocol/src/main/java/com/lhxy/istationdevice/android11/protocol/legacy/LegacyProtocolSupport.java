package com.lhxy.istationdevice.android11.protocol.legacy;

import java.nio.charset.Charset;
import java.util.Calendar;

final class LegacyProtocolSupport {
    private static final Charset GB2312 = Charset.forName("GB2312");

    private LegacyProtocolSupport() {
    }

    static byte[] gb2312(String value) {
        return value == null ? new byte[0] : value.getBytes(GB2312);
    }

    static int sumCheckInt(byte[] buf, int start, int endExclusive) {
        int checkSum = 0;
        for (int i = start; i < endExclusive; i++) {
            checkSum += buf[i] & 0xFF;
        }
        return checkSum;
    }

    static byte[] longToBytesLittle(long value) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (value & 0xFF);
        bytes[1] = (byte) ((value >> 8) & 0xFF);
        bytes[2] = (byte) ((value >> 16) & 0xFF);
        bytes[3] = (byte) ((value >> 24) & 0xFF);
        bytes[4] = (byte) ((value >> 32) & 0xFF);
        bytes[5] = (byte) ((value >> 40) & 0xFF);
        bytes[6] = (byte) ((value >> 48) & 0xFF);
        bytes[7] = (byte) ((value >> 56) & 0xFF);
        return bytes;
    }

    static byte[] intToBytesBig(int value) {
        return new byte[]{
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    static int crcXModem(byte[] bytes, int startIndex, int endInclusive) {
        int crc = 0x00;
        int polynomial = 0x1021;
        for (int index = startIndex; index <= endInclusive; index++) {
            byte value = bytes[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((value >> (7 - i)) & 1) == 1;
                boolean c15 = ((crc >> 15) & 1) == 1;
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }
        return crc & 0xFFFF;
    }

    static int dayOfWeekValue() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }

    static int year2() {
        return Calendar.getInstance().get(Calendar.YEAR) % 100;
    }

    static int month1Based() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    static int dayOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    static int hour24() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    static int minute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    static byte[] escape7e7d(byte[] source) {
        byte[] tmp = new byte[source.length * 2];
        int count = 0;
        for (byte value : source) {
            int unsigned = value & 0xFF;
            if (unsigned == 0x7E) {
                tmp[count++] = (byte) 0x7D;
                tmp[count++] = (byte) 0x01;
            } else if (unsigned == 0x7D) {
                tmp[count++] = (byte) 0x7D;
                tmp[count++] = (byte) 0x00;
            } else {
                tmp[count++] = value;
            }
        }
        byte[] result = new byte[count];
        System.arraycopy(tmp, 0, result, 0, count);
        return result;
    }
}

