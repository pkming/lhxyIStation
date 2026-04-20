package com.lhxy.istationdevice.android11.protocol;

public final class ProtocolEnvelope {
    private final String protocolName;
    private final String channelName;
    private final byte[] payload;

    public ProtocolEnvelope(String protocolName, String channelName, byte[] payload) {
        this.protocolName = protocolName;
        this.channelName = channelName;
        this.payload = payload == null ? new byte[0] : payload.clone();
    }

    public String getProtocolName() {
        return protocolName;
    }

    public String getChannelName() {
        return channelName;
    }

    public byte[] getPayload() {
        return payload.clone();
    }
}

