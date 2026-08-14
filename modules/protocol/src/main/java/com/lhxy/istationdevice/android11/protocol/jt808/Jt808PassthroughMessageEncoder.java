package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * 编码 0x0900 数据上行透传消息。
 */
public final class Jt808PassthroughMessageEncoder {

    private Jt808PassthroughMessageEncoder() {
    }

    /**
     * 编码为 JT808 帧
     */
    public static Jt808Frame encode(Jt808PassthroughMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }

        // 消息体 = 透传类型(1字节) + 透传内容(N字节)
        byte[] content = message.getContent();
        byte[] body = new byte[1 + content.length];
        body[0] = message.getPassthroughType();
        System.arraycopy(content, 0, body, 1, content.length);

        return new Jt808Frame(
                message.getVariant(),
                Jt808PassthroughMessage.MESSAGE_ID,
                message.getTerminalId(),
                message.getSerialNumber(),
                body
        );
    }
}
