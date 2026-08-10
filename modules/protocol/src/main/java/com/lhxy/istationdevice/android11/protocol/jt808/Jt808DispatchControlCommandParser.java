package com.lhxy.istationdevice.android11.protocol.jt808;

import java.nio.charset.Charset;

/** 解析旧 M90 自定义 0x8B02 调度控制消息。 */
public final class Jt808DispatchControlCommandParser {
    private static final Charset GBK = Charset.forName("GBK");

    private Jt808DispatchControlCommandParser() {
    }

    public static Jt808DispatchControlCommand parse(Jt808Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("8B02 调度控制帧为空");
        }
        if (frame.getMessageId() != Jt808DispatchControlCommand.MESSAGE_ID) {
            throw new IllegalArgumentException("不是 8B02 调度控制帧");
        }
        byte[] body = frame.getBody();
        if (body.length < 5) {
            throw new IllegalArgumentException("8B02 调度控制消息体长度不足");
        }
        long lineNumber = readUnsignedInt(body, 0);
        int type = body[4] & 0xFF;
        String nextTrip = null;
        String thisTrip = null;
        String tomorrow = null;
        if (type == Jt808DispatchControlCommand.TYPE_UPDATE_TRIPS) {
            String decoded = new String(body, 5, body.length - 5, GBK);
            int jsonStart = decoded.indexOf('{');
            int jsonEnd = Jt808DispatchPlanCommandParser.findJsonEnd(decoded, jsonStart);
            if (jsonStart < 0 || jsonEnd < jsonStart) {
                throw new IllegalArgumentException("8B02 班次更新缺少完整 JSON");
            }
            String json = decoded.substring(jsonStart, jsonEnd + 1);
            nextTrip = Jt808DispatchPlanCommandParser.findJsonString(json, "nextTrip");
            thisTrip = Jt808DispatchPlanCommandParser.findJsonString(json, "thisTrip");
            tomorrow = Jt808DispatchPlanCommandParser.findJsonString(json, "tomorrow");
        }
        return new Jt808DispatchControlCommand(
                frame.getVariant(),
                frame.getTerminalId(),
                frame.getSerialNumber(),
                lineNumber,
                type,
                nextTrip,
                thisTrip,
                tomorrow
        );
    }

    private static long readUnsignedInt(byte[] source, int offset) {
        return ((long) (source[offset] & 0xFF) << 24)
                | ((long) (source[offset + 1] & 0xFF) << 16)
                | ((long) (source[offset + 2] & 0xFF) << 8)
                | (long) (source[offset + 3] & 0xFF);
    }
}
