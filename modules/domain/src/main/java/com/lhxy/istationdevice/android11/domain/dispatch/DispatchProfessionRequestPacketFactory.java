package com.lhxy.istationdevice.android11.domain.dispatch;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;

import java.nio.charset.Charset;
import java.util.Random;

/**
 * 按 M90 的 0x0900 业务请求格式构造职业请求报文。
 */
public final class DispatchProfessionRequestPacketFactory {
    private static final Charset GB2312 = Charset.forName("GB2312");
    private static final Random RANDOM = new Random();

    public byte[] build(
            ShellConfig shellConfig,
            DispatchState dispatchState,
            SignInState signInState,
            StationState stationState,
            int professionRequestType
    ) {
        byte[] body = new byte[88];
        body[0] = (byte) 0x81;
        writeFixedText(body, 1, 36, resolveLineName(shellConfig, stationState));
        writeCardNo(body, 37, 36, signInState == null ? "-" : signInState.getCardNo());
        body[73] = (byte) (professionRequestType & 0xFF);
        writeBcdTime(body, 74, compactNowTime());
        writeCoordinate(body, 80, stationState == null ? "0" : stationState.getLongitude());
        writeCoordinate(body, 84, stationState == null ? "0" : stationState.getLatitude());

        byte[] header = buildHeader(
                88,
                0x0900,
                normalizeTerminalId(shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchId()),
                RANDOM.nextInt(60000)
        );
        return wrapFrame(header, body);
    }

    private byte[] buildHeader(int bodyLength, int messageId, String terminalId, int sequence) {
        byte[] header = new byte[12];
        header[0] = (byte) ((messageId >> 8) & 0xFF);
        header[1] = (byte) (messageId & 0xFF);
        header[2] = (byte) ((bodyLength >> 8) & 0xFF);
        header[3] = (byte) (bodyLength & 0xFF);
        byte[] terminalBcd = toBcd(terminalId);
        for (int index = 0; index < 6; index++) {
            header[4 + index] = index < terminalBcd.length ? terminalBcd[index] : 0;
        }
        header[10] = (byte) ((sequence >> 8) & 0xFF);
        header[11] = (byte) (sequence & 0xFF);
        return header;
    }

    private byte[] wrapFrame(byte[] header, byte[] body) {
        byte checksum = 0;
        for (byte value : header) {
            checksum ^= value;
        }
        for (byte value : body) {
            checksum ^= value;
        }

        byte[] raw = new byte[header.length + body.length + 2];
        raw[0] = 0x7E;
        System.arraycopy(header, 0, raw, 1, header.length);
        System.arraycopy(body, 0, raw, 1 + header.length, body.length);
        raw[raw.length - 2] = checksum;
        raw[raw.length - 1] = 0x7E;
        return escape(raw);
    }

    private byte[] escape(byte[] source) {
        byte[] buffer = new byte[source.length * 2];
        int offset = 0;
        for (int index = 0; index < source.length; index++) {
            byte current = source[index];
            boolean guard = index == 0 || index == source.length - 1;
            if (!guard && current == 0x7E) {
                buffer[offset++] = 0x7D;
                buffer[offset++] = 0x02;
            } else if (!guard && current == 0x7D) {
                buffer[offset++] = 0x7D;
                buffer[offset++] = 0x01;
            } else {
                buffer[offset++] = current;
            }
        }
        byte[] escaped = new byte[offset];
        System.arraycopy(buffer, 0, escaped, 0, offset);
        return escaped;
    }

    private void writeFixedText(byte[] target, int offset, int maxLength, String value) {
        byte[] textBytes = safeText(value).getBytes(GB2312);
        int copyLength = Math.min(textBytes.length, maxLength);
        System.arraycopy(textBytes, 0, target, offset, copyLength);
        for (int index = copyLength; index < maxLength; index++) {
            target[offset + index] = 0;
        }
    }

    private void writeCardNo(byte[] target, int offset, int maxLength, String cardNo) {
        byte[] cardBytes = hexToBytes(normalizeCardNo(cardNo));
        int copyLength = Math.min(cardBytes.length, maxLength);
        System.arraycopy(cardBytes, 0, target, offset, copyLength);
        for (int index = copyLength; index < maxLength; index++) {
            target[offset + index] = 0;
        }
    }

    private void writeBcdTime(byte[] target, int offset, String compactTime) {
        byte[] bcd = toBcd(compactTime);
        for (int index = 0; index < 6; index++) {
            target[offset + index] = index < bcd.length ? bcd[index] : 0;
        }
    }

    private void writeCoordinate(byte[] target, int offset, String value) {
        int coordinate = parseCoordinate(value);
        target[offset] = (byte) ((coordinate >> 24) & 0xFF);
        target[offset + 1] = (byte) ((coordinate >> 16) & 0xFF);
        target[offset + 2] = (byte) ((coordinate >> 8) & 0xFF);
        target[offset + 3] = (byte) (coordinate & 0xFF);
    }

    private int parseCoordinate(String value) {
        String normalized = safeText(value).replace("-", "0");
        int dot = normalized.indexOf('.');
        String digits = dot >= 0
                ? normalized.substring(0, dot) + normalized.substring(dot + 1)
                : normalized;
        digits = digits.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(digits);
        } catch (Exception ignore) {
            return 0;
        }
    }

    private byte[] toBcd(String digits) {
        String normalized = safeText(digits).replaceAll("[^0-9]", "");
        if ((normalized.length() & 1) == 1) {
            normalized = "0" + normalized;
        }
        byte[] result = new byte[normalized.length() / 2];
        for (int index = 0; index < normalized.length(); index += 2) {
            int high = Character.digit(normalized.charAt(index), 10);
            int low = Character.digit(normalized.charAt(index + 1), 10);
            result[index / 2] = (byte) ((high << 4) | low);
        }
        return result;
    }

    private byte[] hexToBytes(String value) {
        String normalized = safeText(value).replaceAll("[^0-9A-Fa-f]", "");
        if ((normalized.length() & 1) == 1) {
            normalized = "0" + normalized;
        }
        byte[] result = new byte[normalized.length() / 2];
        for (int index = 0; index < normalized.length(); index += 2) {
            int high = Character.digit(normalized.charAt(index), 16);
            int low = Character.digit(normalized.charAt(index + 1), 16);
            result[index / 2] = (byte) ((high << 4) | low);
        }
        return result;
    }

    private String compactNowTime() {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyMMddHHmmss", java.util.Locale.getDefault());
        return format.format(new java.util.Date());
    }

    private String normalizeTerminalId(String dispatchId) {
        String normalized = safeText(dispatchId).replaceAll("[^0-9]", "");
        if (normalized.isEmpty()) {
            return "000001";
        }
        if (normalized.length() > 12) {
            return normalized.substring(0, 12);
        }
        return normalized;
    }

    private String normalizeCardNo(String cardNo) {
        String normalized = safeText(cardNo).replaceAll("[^0-9A-Fa-f]", "");
        if (normalized.isEmpty()) {
            return "00000000";
        }
        if (normalized.length() > 8) {
            return normalized.substring(normalized.length() - 8);
        }
        return normalized;
    }

    private String resolveLineName(ShellConfig shellConfig, StationState stationState) {
        String stationLineName = stationState == null ? "" : safeText(stationState.getLineName());
        if (!stationLineName.isEmpty() && !"-".equals(stationLineName)) {
            return stationLineName;
        }
        String importedLineName = safeText(shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName());
        return importedLineName.isEmpty() ? "-" : importedLineName;
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}