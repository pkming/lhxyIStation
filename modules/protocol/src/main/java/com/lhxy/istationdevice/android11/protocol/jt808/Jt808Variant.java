package com.lhxy.istationdevice.android11.protocol.jt808;

public enum Jt808Variant {
    JT808("JT808", "JT808_SOCKET", "CTTIT", 20),
    AL808("AL808", "AL808_SOCKET", "ALINK", 8);

    private final String protocolName;
    private final String channelName;
    private final String manufacturerId;
    private final int terminalModelLength;

    Jt808Variant(String protocolName, String channelName, String manufacturerId, int terminalModelLength) {
        this.protocolName = protocolName;
        this.channelName = channelName;
        this.manufacturerId = manufacturerId;
        this.terminalModelLength = terminalModelLength;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public int getTerminalModelLength() {
        return terminalModelLength;
    }
}
