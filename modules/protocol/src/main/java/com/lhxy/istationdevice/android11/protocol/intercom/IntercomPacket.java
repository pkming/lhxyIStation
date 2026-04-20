package com.lhxy.istationdevice.android11.protocol.intercom;

/**
 * 对讲包模型
 */
public final class IntercomPacket {
    private final int serialNumber;
    private final String terminalId;
    private final int channelNumber;
    private final long timestampMillis;
    private final byte[] content;

    public IntercomPacket(
            int serialNumber,
            String terminalId,
            int channelNumber,
            long timestampMillis,
            byte[] content
    ) {
        this.serialNumber = serialNumber;
        this.terminalId = terminalId;
        this.channelNumber = channelNumber;
        this.timestampMillis = timestampMillis;
        this.content = content == null ? new byte[0] : content.clone();
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public byte[] getContent() {
        return content.clone();
    }
}
