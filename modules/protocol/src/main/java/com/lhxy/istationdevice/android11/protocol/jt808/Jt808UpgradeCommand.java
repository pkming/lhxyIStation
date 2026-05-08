package com.lhxy.istationdevice.android11.protocol.jt808;

import java.util.Locale;

/**
 * 旧 M90 8B0A 升级下载命令。
 */
public final class Jt808UpgradeCommand {
    public static final int DOWNLOAD_TYPE_SOURCE_FILE = 1;
    public static final int DOWNLOAD_TYPE_APK = 2;

    private final String terminalId;
    private final int requestSerialNumber;
    private final String requestSerialHex;
    private final String serverAddress;
    private final int serverAddressPort;
    private final int protocolType;
    private final String loginName;
    private final String loginPwd;
    private final String versionUrl;
    private final int upgradeType;
    private final String scheduleTimeBcd;
    private final String cancelSerialHex;

    public Jt808UpgradeCommand(
            String terminalId,
            int requestSerialNumber,
            String serverAddress,
            int serverAddressPort,
            int protocolType,
            String loginName,
            String loginPwd,
            String versionUrl,
            int upgradeType,
            String scheduleTimeBcd,
            String cancelSerialHex
    ) {
        this.terminalId = terminalId == null ? "" : terminalId.trim();
        this.requestSerialNumber = requestSerialNumber;
        this.requestSerialHex = String.format(Locale.US, "%04X", requestSerialNumber & 0xFFFF);
        this.serverAddress = serverAddress == null ? "" : serverAddress.trim();
        this.serverAddressPort = serverAddressPort;
        this.protocolType = protocolType;
        this.loginName = loginName == null ? "" : loginName.trim();
        this.loginPwd = loginPwd == null ? "" : loginPwd.trim();
        this.versionUrl = versionUrl == null ? "" : versionUrl.trim();
        this.upgradeType = upgradeType;
        this.scheduleTimeBcd = scheduleTimeBcd == null ? "" : scheduleTimeBcd.trim();
        this.cancelSerialHex = cancelSerialHex == null ? "" : cancelSerialHex.trim().toUpperCase(Locale.US);
    }

    public String getTerminalId() {
        return terminalId;
    }

    public int getRequestSerialNumber() {
        return requestSerialNumber;
    }

    public String getRequestSerialHex() {
        return requestSerialHex;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerAddressPort() {
        return serverAddressPort;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public String getVersionUrl() {
        return versionUrl;
    }

    public int getUpgradeType() {
        return upgradeType;
    }

    public String getScheduleTimeBcd() {
        return scheduleTimeBcd;
    }

    public String getCancelSerialHex() {
        return cancelSerialHex;
    }

    public boolean isCancelCommand() {
        return upgradeType == 2;
    }

    public boolean isScheduledCommand() {
        return upgradeType == 1;
    }

    public int resolveDownloadType() {
        String normalizedUrl = versionUrl.toLowerCase(Locale.US);
        if (normalizedUrl.endsWith(".apk")) {
            return DOWNLOAD_TYPE_APK;
        }
        if (normalizedUrl.endsWith(".zip")) {
            return DOWNLOAD_TYPE_SOURCE_FILE;
        }
        return 0;
    }

    public String buildTransferSignature() {
        return loginName + "\n"
                + loginPwd + "\n"
                + serverAddress + "\n"
                + serverAddressPort + "\n"
                + versionUrl;
    }
}