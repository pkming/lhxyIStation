package com.lhxy.istationdevice.android11.protocol.legacy;

public final class ProtocolBatchResult {
    private final BusLineSnapshot lastLine;
    private final byte[] payload;

    public ProtocolBatchResult(BusLineSnapshot lastLine, byte[] payload) {
        this.lastLine = lastLine;
        this.payload = payload == null ? new byte[0] : payload.clone();
    }

    public BusLineSnapshot getLastLine() {
        return lastLine;
    }

    public byte[] getPayload() {
        return payload.clone();
    }
}

