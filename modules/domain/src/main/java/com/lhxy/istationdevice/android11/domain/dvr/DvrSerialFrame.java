package com.lhxy.istationdevice.android11.domain.dvr;

/**
 * DVR 串口完整帧。
 */
public final class DvrSerialFrame {
    private final int command;
    private final int payloadLength;
    private final byte checksum;
    private final byte[] payload;
    private final byte[] rawFrame;

    public DvrSerialFrame(int command, int payloadLength, byte checksum, byte[] payload, byte[] rawFrame) {
        this.command = command;
        this.payloadLength = payloadLength;
        this.checksum = checksum;
        this.payload = payload == null ? new byte[0] : payload.clone();
        this.rawFrame = rawFrame == null ? new byte[0] : rawFrame.clone();
    }

    public int getCommand() {
        return command;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public byte getChecksum() {
        return checksum;
    }

    public byte[] getPayload() {
        return payload.clone();
    }

    public byte[] getRawFrame() {
        return rawFrame.clone();
    }
}
