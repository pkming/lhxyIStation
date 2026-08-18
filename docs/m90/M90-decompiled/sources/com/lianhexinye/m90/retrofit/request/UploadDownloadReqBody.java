package com.lianhexinye.m90.retrofit.request;

/* JADX INFO: loaded from: classes2.dex */
public class UploadDownloadReqBody {
    private String channel;
    private String fileName;
    private String filePercent;
    private String fileSize;
    private String key;
    private String playProgram;
    private String slots;
    private String url;

    public String getKey() {
        return this.key;
    }

    public void setKey(String str) {
        this.key = str;
    }

    public String getPlayProgram() {
        return this.playProgram;
    }

    public void setPlayProgram(String str) {
        this.playProgram = str;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String str) {
        this.channel = str;
    }

    public String getSlots() {
        return this.slots;
    }

    public void setSlots(String str) {
        this.slots = str;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public String getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(String str) {
        this.fileSize = str;
    }

    public String getFilePercent() {
        return this.filePercent;
    }

    public void setFilePercent(String str) {
        this.filePercent = str;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }
}
