package com.lhxy.istationdevice.android11.protocol.jt808;

import java.util.HashMap;
import java.util.Map;

/**
 * 解析 0x8103 设置终端参数命令。
 * 
 * <p>消息体格式：参数总数(BYTE) + 参数列表</p>
 * <p>每个参数：参数ID(DWORD) + 参数长度(BYTE) + 参数值(N字节)</p>
 */
public final class Jt808SetTerminalParametersCommandParser {

    private Jt808SetTerminalParametersCommandParser() {
    }

    /**
     * 解析 0x8103 帧
     */
    public static Jt808SetTerminalParametersCommand parse(Jt808Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("frame is null");
        }
        if (frame.getMessageId() != Jt808SetTerminalParametersCommand.MESSAGE_ID) {
            throw new IllegalArgumentException(
                    "Not a 0x8103 frame: messageId=0x" + Integer.toHexString(frame.getMessageId())
            );
        }

        byte[] body = frame.getBody();
        if (body == null || body.length < 1) {
            throw new IllegalArgumentException("0x8103 body is empty");
        }

        int totalParams = body[0] & 0xFF;
        Map<Integer, byte[]> parameters = new HashMap<>();

        int offset = 1;
        for (int i = 0; i < totalParams; i++) {
            if (offset + 5 > body.length) {
                break;  // 数据不足，停止解析
            }

            // 读取参数ID（DWORD，4字节）
            int parameterId = ((body[offset] & 0xFF) << 24)
                    | ((body[offset + 1] & 0xFF) << 16)
                    | ((body[offset + 2] & 0xFF) << 8)
                    | (body[offset + 3] & 0xFF);
            offset += 4;

            // 读取参数长度（BYTE，1字节）
            int paramLength = body[offset] & 0xFF;
            offset++;

            // 读取参数值
            if (offset + paramLength > body.length) {
                break;  // 数据不足，停止解析
            }

            byte[] paramValue = new byte[paramLength];
            System.arraycopy(body, offset, paramValue, 0, paramLength);
            offset += paramLength;

            parameters.put(parameterId, paramValue);
        }

        return new Jt808SetTerminalParametersCommand(
                frame.getVariant(),
                frame.getTerminalId(),
                frame.getSerialNumber(),
                parameters
        );
    }
}
