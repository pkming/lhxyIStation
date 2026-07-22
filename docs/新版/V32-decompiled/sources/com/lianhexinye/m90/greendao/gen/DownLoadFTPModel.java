package com.lianhexinye.m90.greendao.gen;

/* JADX INFO: loaded from: classes2.dex */
public class DownLoadFTPModel {
    private Long _id;
    private String fileUpgradeTime;
    private String localPath;
    private String loginName;
    private String loginPwd;
    private String msgSerialNumber;
    private int protocolType;
    private String serverAddress;
    private int serverAddressPort;
    private int status;
    private int type;
    private int upgradeType;
    private String url;

    public DownLoadFTPModel(Long l, String str, int i, int i2, String str2, String str3, String str4, String str5, int i3, int i4, String str6, String str7, int i5) {
        this._id = l;
        this.url = str;
        this.type = i;
        this.status = i2;
        this.msgSerialNumber = str2;
        this.fileUpgradeTime = str3;
        this.localPath = str4;
        this.serverAddress = str5;
        this.serverAddressPort = i3;
        this.protocolType = i4;
        this.loginName = str6;
        this.loginPwd = str7;
        this.upgradeType = i5;
    }

    public DownLoadFTPModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public String getMsgSerialNumber() {
        return this.msgSerialNumber;
    }

    public void setMsgSerialNumber(String str) {
        this.msgSerialNumber = str;
    }

    public String getFileUpgradeTime() {
        return this.fileUpgradeTime;
    }

    public void setFileUpgradeTime(String str) {
        this.fileUpgradeTime = str;
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void setLocalPath(String str) {
        this.localPath = str;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public void setServerAddress(String str) {
        this.serverAddress = str;
    }

    public int getServerAddressPort() {
        return this.serverAddressPort;
    }

    public void setServerAddressPort(int i) {
        this.serverAddressPort = i;
    }

    public int getProtocolType() {
        return this.protocolType;
    }

    public void setProtocolType(int i) {
        this.protocolType = i;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setLoginName(String str) {
        this.loginName = str;
    }

    public String getLoginPwd() {
        return this.loginPwd;
    }

    public void setLoginPwd(String str) {
        this.loginPwd = str;
    }

    public int getUpgradeType() {
        return this.upgradeType;
    }

    public void setUpgradeType(int i) {
        this.upgradeType = i;
    }
}
