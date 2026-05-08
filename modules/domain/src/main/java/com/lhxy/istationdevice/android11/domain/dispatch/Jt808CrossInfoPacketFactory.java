package com.lhxy.istationdevice.android11.domain.dispatch;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;

import java.nio.charset.Charset;
import java.util.Random;

/**
 * 按 M90 的 Generate808ReqPackage.generateCrossInfo 构造路口信息上报帧。
 */
public final class Jt808CrossInfoPacketFactory {
    private static final Charset GB2312 = Charset.forName("GB2312");
    private static final Random RANDOM = new Random();

    public byte[] build(
            ShellConfig shellConfig,
            StationState stationState,
            LegacyGpsRouteResource.ReminderPoint reminderPoint,
            String arrivalTime,
            String outboundTime,
            int angle,
            String longitude,
            String latitude
    ) {
        byte[] body = new byte[95];
        body[0] = 0x32;
        writeFixedText(body, 1, 36, resolveLineNumber(shellConfig, stationState));
        writeFixedText(body, 37, 36, resolveCrossNumber(reminderPoint));
        writeBcdTime(body, 73, arrivalTime);
        writeBcdTime(body, 79, outboundTime);
        writeShort(body, 85, angle);
        writeCoordinate(body, 87, longitude);
        writeCoordinate(body, 91, latitude);

        byte[] header = buildHeader(
                body.length,
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

    private void writeBcdTime(byte[] target, int offset, String compactTime) {
        byte[] bcd = toBcd(compactTime);
        for (int index = 0; index < 6; index++) {
            target[offset + index] = index < bcd.length ? bcd[index] : 0;
        }
    }

    private void writeShort(byte[] target, int offset, int value) {
        int normalized = Math.max(value, 0);
        target[offset] = (byte) ((normalized >> 8) & 0xFF);
        target[offset + 1] = (byte) (normalized & 0xFF);
    }

    private void writeCoordinate(byte[] target, int offset, String value) {
        int coordinate = parseCoordinate(value);
        target[offset] = (byte) ((coordinate >> 24) & 0xFF);
        target[offset + 1] = (byte) ((coordinate >> 16) & 0xFF);
        target[offset + 2] = (byte) ((coordinate >> 8) & 0xFF);
        target[offset + 3] = (byte) (coordinate & 0xFF);
    }

    private int parseCoordinate(String value) {
        String normalized = safeText(value).replaceAll("[^0-9.]", "");
        int dot = normalized.indexOf('.');
        String digits = dot >= 0
                ? normalized.substring(0, dot) + normalized.substring(dot + 1)
                : normalized;
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

    private String resolveLineNumber(ShellConfig shellConfig, StationState stationState) {
        String lineName = stationState == null ? "" : safeText(stationState.getLineName());
        if (!lineName.isEmpty() && !"-".equals(lineName)) {
            return lineName;
        }
        String importedLineName = safeText(shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName());
        return importedLineName.isEmpty() ? "0" : importedLineName;
    }

    private String resolveCrossNumber(LegacyGpsRouteResource.ReminderPoint reminderPoint) {
        if (reminderPoint == null) {
            return "0";
        }
        String crossCode = safeText(reminderPoint.getCrossCode());
        if (!crossCode.isEmpty()) {
            return crossCode;
        }
        if (reminderPoint.getReminderNo() >= 0) {
            return String.valueOf(reminderPoint.getReminderNo());
        }
        String reminderName = safeText(reminderPoint.getReminderName());
        return reminderName.isEmpty() ? "0" : reminderName;
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}