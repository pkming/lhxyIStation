package com.lhxy.istationdevice.android11.protocol.jt808;

public final class Jt808Frame {
    private final Jt808Variant variant;
    private final int messageId;
    private final String terminalId;
    private final int serialNumber;
    private final byte[] body;

    public Jt808Frame(Jt808Variant variant, int messageId, String terminalId, int serialNumber, byte[] body) {
        this.variant = variant;
        this.messageId = messageId;
        this.terminalId = terminalId;
        this.serialNumber = serialNumber;
        this.body = body == null ? new byte[0] : body.clone();
    }

    public Jt808Variant getVariant() {
        return variant;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public byte[] getBody() {
        return body.clone();
    }
}
