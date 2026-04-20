package com.lhxy.istationdevice.android11.protocol.jt808;

import java.time.LocalDateTime;

/**
 * JT808 / AL808 请求消息参数
 * <p>
 * content 目前不参与报文编码，先保留给业务层做请求流水和内容映射。
 */
public final class Jt808RequestMessage {
    private final int lineNumber;
    private final String cardNumberHex;
    private final int type;
    private final LocalDateTime terminalTime;
    private final String content;

    public Jt808RequestMessage(
            int lineNumber,
            String cardNumberHex,
            int type,
            LocalDateTime terminalTime,
            String content
    ) {
        this.lineNumber = lineNumber;
        this.cardNumberHex = cardNumberHex;
        this.type = type;
        this.terminalTime = terminalTime;
        this.content = content;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getCardNumberHex() {
        return cardNumberHex;
    }

    public int getType() {
        return type;
    }

    public LocalDateTime getTerminalTime() {
        return terminalTime;
    }

    public String getContent() {
        return content;
    }
}
