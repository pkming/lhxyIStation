package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * JT808 帧解码器。
 */
public final class Jt808FrameDecoder {
    /**
     * 宽松校验模式（对标现场版M90行为）。
     * true: 校验失败时记录警告日志但继续解析（兼容缺少校验码的平台）
     * false: 校验失败时抛出异常（严格遵循JT808协议规范）
     * 
     * 注意：M90不验证校验码，导致可以接收无校验码/错误校验码的消息。
     * 本宽松模式仅作为临时兼容措施，生产环境建议修复平台端后关闭。
     */
    private static final boolean LENIENT_CHECKSUM_MODE = true;

    private Jt808FrameDecoder() {
    }

    public static Jt808Frame decode(byte[] rawFrame) {
        return decode(rawFrame, null);
    }

    public static Jt808Frame decode(byte[] rawFrame, String traceId) {
        if (rawFrame == null || rawFrame.length < 2) {
            throw new IllegalArgumentException("原始帧为空或长度不足");
        }
        if ((rawFrame[0] & 0xFF) != 0x7E || (rawFrame[rawFrame.length - 1] & 0xFF) != 0x7E) {
            throw new IllegalArgumentException("不是标准 JT808 0x7E 包裹帧");
        }

        byte[] escapedPayload = new byte[rawFrame.length - 2];
        System.arraycopy(rawFrame, 1, escapedPayload, 0, escapedPayload.length);
        byte[] payload = Jt808CodecSupport.unescape(escapedPayload);
        if (payload.length < 13) {
            throw new IllegalArgumentException("去转义后的 JT808 帧长度不足");
        }

        byte checksum = 0x00;
        for (int index = 0; index < payload.length - 1; index++) {
            checksum ^= payload[index];
        }
        if (checksum != payload[payload.length - 1]) {
            if (LENIENT_CHECKSUM_MODE) {
                // 宽松模式：输出警告但继续解析（对标M90行为）
                System.err.println("[WARN][Jt808FrameDecoder] JT808 校验失败，宽松模式允许继续解析 / " +
                    "calc=0x" + String.format("%02X", checksum & 0xFF) + 
                    " / frame=0x" + String.format("%02X", payload[payload.length - 1] & 0xFF) +
                    " / traceId=" + (traceId != null ? traceId : "null") +
                    " / 对标M90不验证校验码");
            } else {
                // 严格模式：抛出异常（符合JT808协议规范）
                throw new IllegalArgumentException("JT808 校验失败");
            }
        }

        int messageId = readWord(payload, 0);
        int bodyProps = readWord(payload, 2);
        int bodyLength = bodyProps & 0x03FF;
        String terminalId = bcdString(payload, 4, 6);
        int serialNumber = readWord(payload, 10);
        int bodyStart = 12;
        int bodyEnd = Math.min(payload.length - 1, bodyStart + bodyLength);
        if (bodyEnd < bodyStart) {
            throw new IllegalArgumentException("JT808 消息体长度非法");
        }

        byte[] body = new byte[bodyEnd - bodyStart];
        if (body.length > 0) {
            System.arraycopy(payload, bodyStart, body, 0, body.length);
        }
        return new Jt808Frame(Jt808Variant.JT808, messageId, terminalId, serialNumber, body);
    }

    private static int readWord(byte[] source, int offset) {
        return ((source[offset] & 0xFF) << 8) | (source[offset + 1] & 0xFF);
    }

    private static String bcdString(byte[] source, int offset, int length) {
        byte[] digits = new byte[length];
        System.arraycopy(source, offset, digits, 0, length);
        StringBuilder builder = new StringBuilder(length * 2);
        for (byte digit : digits) {
            builder.append(String.format("%02X", digit & 0xFF));
        }
        return builder.toString();
    }
}