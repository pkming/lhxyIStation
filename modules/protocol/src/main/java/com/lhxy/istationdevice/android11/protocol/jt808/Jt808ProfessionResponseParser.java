package com.lhxy.istationdevice.android11.protocol.jt808;

/** 解析旧 M90 自定义 0x8B09 职业请求应答。 */
public final class Jt808ProfessionResponseParser {
    private Jt808ProfessionResponseParser() {
    }

    public static Jt808ProfessionResponse parse(Jt808Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("8B09 请求应答帧为空");
        }
        if (frame.getMessageId() != Jt808ProfessionResponse.MESSAGE_ID) {
            throw new IllegalArgumentException("不是 8B09 请求应答帧");
        }
        byte[] body = frame.getBody();
        if (body.length < 3) {
            throw new IllegalArgumentException("8B09 请求应答消息体长度不足");
        }
        int requestSerialNumber = ((body[0] & 0xFF) << 8) | (body[1] & 0xFF);
        return new Jt808ProfessionResponse(requestSerialNumber, body[2] & 0xFF);
    }
}
