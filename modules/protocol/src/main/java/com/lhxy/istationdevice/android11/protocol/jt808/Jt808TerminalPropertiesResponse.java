package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * 查询终端属性应答参数
 */
public final class Jt808TerminalPropertiesResponse {
    private final String terminalModel;
    private final String hardwareVersion;
    private final String softwareVersion;
    private final int communicationCapability;
    private final int gnssCapability;

    public Jt808TerminalPropertiesResponse(
            String terminalModel,
            String hardwareVersion,
            String softwareVersion,
            int communicationCapability,
            int gnssCapability
    ) {
        this.terminalModel = terminalModel;
        this.hardwareVersion = hardwareVersion;
        this.softwareVersion = softwareVersion;
        this.communicationCapability = communicationCapability;
        this.gnssCapability = gnssCapability;
    }

    public String getTerminalModel() {
        return terminalModel;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public int getCommunicationCapability() {
        return communicationCapability;
    }

    public int getGnssCapability() {
        return gnssCapability;
    }
}
