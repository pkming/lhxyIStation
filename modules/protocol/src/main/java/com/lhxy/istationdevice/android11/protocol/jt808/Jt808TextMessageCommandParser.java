package com.lhxy.istationdevice.android11.protocol.jt808;

import java.nio.charset.Charset;

/** 解析旧 M90 自定义 0x8300 平台文本消息。 */
public final class Jt808TextMessageCommandParser {
    private static final Charset GBK = Charset.forName("GBK");

    private Jt808TextMessageCommandParser() {
    }

    public static Jt808TextMessageCommand parse(Jt808Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("8300 文本消息帧为空");
        }
        if (frame.getMessageId() != Jt808TextMessageCommand.MESSAGE_ID) {
            throw new IllegalArgumentException("不是 8300 文本消息帧");
        }
        byte[] body = frame.getBody();
        if (body.length < 2) {
            throw new IllegalArgumentException("8300 文本消息体长度不足");
        }
        String content = new String(body, 1, body.length - 1, GBK).trim();
        if (content.isEmpty()) {
            throw new IllegalArgumentException("8300 文本内容为空");
        }
        return new Jt808TextMessageCommand(
                frame.getVariant(),
                frame.getTerminalId(),
                frame.getSerialNumber(),
                body[0] & 0xFF,
                content
        );
    }
}
