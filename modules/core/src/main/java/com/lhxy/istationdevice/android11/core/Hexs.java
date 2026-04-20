package com.lhxy.istationdevice.android11.core;

public final class Hexs {
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    private Hexs() {
    }

    public static String toHex(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(data.length * 3);
        for (int i = 0; i < data.length; i++) {
            int value = data[i] & 0xFF;
            builder.append(HEX[value >>> 4]);
            builder.append(HEX[value & 0x0F]);
            if (i < data.length - 1) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    public static byte[] fromHex(String source) {
        if (source == null) {
            return new byte[0];
        }
        String clean = source.replaceAll("[^0-9A-Fa-f]", "");
        if (clean.isEmpty()) {
            return new byte[0];
        }
        if (clean.length() % 2 != 0) {
            clean = "0" + clean;
        }
        byte[] result = new byte[clean.length() / 2];
        for (int i = 0; i < clean.length(); i += 2) {
            result[i / 2] = (byte) Integer.parseInt(clean.substring(i, i + 2), 16);
        }
        return result;
    }
}

