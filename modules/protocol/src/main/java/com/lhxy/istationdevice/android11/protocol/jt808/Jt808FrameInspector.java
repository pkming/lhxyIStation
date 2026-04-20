package com.lhxy.istationdevice.android11.protocol.jt808;

import com.lhxy.istationdevice.android11.core.Hexs;

/**
 * JT808 / AL808 帧检查器
 * <p>
 * 当前先提供最小反解能力，方便调试页快速看：
 * 帧头帧尾、消息 ID、消息体长度、终端号、流水号和校验结果。
 */
public final class Jt808FrameInspector {
    private Jt808FrameInspector() {
    }

    /**
     * 输出一段更适合现场看的解析文本。
     */
    public static String inspect(byte[] rawFrame) {
        if (rawFrame == null || rawFrame.length == 0) {
            return "解析结果:\n- 输入为空";
        }
        if (rawFrame.length < 2) {
            return "解析结果:\n- 长度不足，至少要有帧头帧尾";
        }
        if ((rawFrame[0] & 0xFF) != 0x7E || (rawFrame[rawFrame.length - 1] & 0xFF) != 0x7E) {
            return "解析结果:\n- 不是标准 0x7E 包裹帧\n- 原始 HEX: " + Hexs.toHex(rawFrame);
        }

        byte[] escapedPayload = new byte[rawFrame.length - 2];
        System.arraycopy(rawFrame, 1, escapedPayload, 0, escapedPayload.length);
        byte[] payload = Jt808CodecSupport.unescape(escapedPayload);
        if (payload.length < 13) {
            return "解析结果:\n- 去转义后长度不足\n- 去转义 HEX: " + Hexs.toHex(payload);
        }

        byte checksum = 0x00;
        for (int index = 0; index < payload.length - 1; index++) {
            checksum ^= payload[index];
        }
        boolean checksumPassed = checksum == payload[payload.length - 1];

        int messageId = readWord(payload, 0);
        int bodyProps = readWord(payload, 2);
        int bodyLength = bodyProps & 0x03FF;
        String terminalId = bcdString(payload, 4, 6);
        int serialNumber = readWord(payload, 10);

        int bodyStart = 12;
        int bodyEnd = Math.min(payload.length - 1, bodyStart + bodyLength);
        byte[] body = new byte[Math.max(0, bodyEnd - bodyStart)];
        if (body.length > 0) {
            System.arraycopy(payload, bodyStart, body, 0, body.length);
        }

        StringBuilder builder = new StringBuilder("解析结果:");
        builder.append("\n- 消息 ID: 0x").append(String.format("%04X", messageId)).append(" / ").append(describeMessageId(messageId));
        builder.append("\n- 消息体属性: 0x").append(String.format("%04X", bodyProps));
        builder.append("\n- 消息体长度: ").append(bodyLength);
        builder.append("\n- 终端号: ").append(terminalId);
        builder.append("\n- 流水号: ").append(serialNumber);
        builder.append("\n- 校验: ").append(checksumPassed ? "通过" : "失败")
                .append(" (calc=0x").append(String.format("%02X", checksum & 0xFF))
                .append(", frame=0x").append(String.format("%02X", payload[payload.length - 1] & 0xFF)).append(")");
        builder.append("\n- 消息体 HEX: ").append(body.length == 0 ? "(空)" : Hexs.toHex(body));
        builder.append("\n- 去转义 HEX: ").append(Hexs.toHex(payload));
        builder.append("\n- 原始 HEX: ").append(Hexs.toHex(rawFrame));
        return builder.toString();
    }

    private static int readWord(byte[] source, int offset) {
        return ((source[offset] & 0xFF) << 8) | (source[offset + 1] & 0xFF);
    }

    private static String bcdString(byte[] source, int offset, int length) {
        byte[] digits = new byte[length];
        System.arraycopy(source, offset, digits, 0, length);
        return Hexs.toHex(digits).replace(" ", "");
    }

    private static String describeMessageId(int messageId) {
        switch (messageId) {
            case 0x0001:
                return "终端通用应答";
            case 0x0002:
                return "终端心跳";
            case 0x0100:
                return "终端注册";
            case 0x0102:
                return "终端鉴权";
            case 0x0200:
                return "位置信息汇报";
            case 0x0107:
                return "查询终端属性应答";
            case 0x0B02:
                return "报站信息";
            case 0x0B04:
                return "违规信息";
            case 0x0B05:
                return "司机考勤";
            case 0x0B06:
                return "校时应答";
            case 0x0B09:
                return "请求消息";
            case 0x0B0A:
                return "升级通知";
            case 0x0B0E:
                return "线路切换信息";
            case 0x8001:
                return "平台通用应答";
            case 0x8100:
                return "终端注册应答";
            case 0x8103:
                return "设置终端参数";
            case 0xDB01:
                return "AL808 切换线路应答";
            case 0xDB0E:
                return "AL808 线路切换信息";
            default:
                return "未登记消息";
        }
    }
}
