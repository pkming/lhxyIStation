package com.lhxy.istationdevice.android11.protocol.jt808;

import java.nio.charset.Charset;

/**
 * 解析旧 M90 自定义 8B0A 下载命令体。
 */
public final class Jt808UpgradeCommandParser {
    private static final int MSG_UPGRADE_COMMAND = 0x8B0A;
    private static final Charset GBK = Jt808CodecSupport.GB2312;

    private Jt808UpgradeCommandParser() {
    }

    public static Jt808UpgradeCommand parse(byte[] rawFrame) {
        return parse(Jt808FrameDecoder.decode(rawFrame));
    }

    public static Jt808UpgradeCommand parse(Jt808Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("升级命令帧为空");
        }
        if (frame.getMessageId() != MSG_UPGRADE_COMMAND) {
            throw new IllegalArgumentException("不是 8B0A 升级命令帧");
        }

        byte[] body = frame.getBody();
        int offset = 0;
        int addressLength = readByte(body, offset++);
        String serverAddress = readText(body, offset, addressLength);
        offset += addressLength;

        int port = readWord(body, offset);
        offset += 2;

        int protocolType = readByte(body, offset++);

        int loginNameLength = readByte(body, offset++);
        String loginName = readText(body, offset, loginNameLength);
        offset += loginNameLength;

        int loginPwdLength = readByte(body, offset++);
        String loginPwd = readText(body, offset, loginPwdLength);
        offset += loginPwdLength;

        int versionUrlLength = readByte(body, offset++);
        String versionUrl = readText(body, offset, versionUrlLength);
        offset += versionUrlLength;

        int upgradeType = readByte(body, offset++);
        String scheduleTimeBcd = "";
        String cancelSerialHex = "";
        if (upgradeType == 1) {
            ensureRemaining(body, offset, 6, "计划升级时间");
            scheduleTimeBcd = bcdString(body, offset, 6);
        } else if (upgradeType == 2) {
            ensureRemaining(body, offset, 2, "取消流水号");
            cancelSerialHex = String.format("%02X%02X", body[offset] & 0xFF, body[offset + 1] & 0xFF);
        }

        return new Jt808UpgradeCommand(
                frame.getTerminalId(),
                frame.getSerialNumber(),
                serverAddress,
                port,
                protocolType,
                loginName,
                loginPwd,
                versionUrl,
                upgradeType,
                scheduleTimeBcd,
                cancelSerialHex
        );
    }

    private static int readByte(byte[] source, int offset) {
        ensureRemaining(source, offset, 1, "字段");
        return source[offset] & 0xFF;
    }

    private static int readWord(byte[] source, int offset) {
        ensureRemaining(source, offset, 2, "字段");
        return ((source[offset] & 0xFF) << 8) | (source[offset + 1] & 0xFF);
    }

    private static String readText(byte[] source, int offset, int length) {
        if (length <= 0) {
            return "";
        }
        ensureRemaining(source, offset, length, "字符串字段");
        return new String(source, offset, length, GBK).trim();
    }

    private static void ensureRemaining(byte[] source, int offset, int expectedLength, String fieldLabel) {
        if (source == null || offset < 0 || expectedLength < 0 || offset + expectedLength > source.length) {
            throw new IllegalArgumentException("8B0A 升级命令体长度不足，缺少" + fieldLabel);
        }
    }

    private static String bcdString(byte[] source, int offset, int length) {
        ensureRemaining(source, offset, length, "BCD 时间字段");
        StringBuilder builder = new StringBuilder(length * 2);
        for (int index = 0; index < length; index++) {
            builder.append(String.format("%02X", source[offset + index] & 0xFF));
        }
        return builder.toString();
    }
}