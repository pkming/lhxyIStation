package com.lianhexinye.m90.greendao.gen;

/* JADX INFO: loaded from: classes2.dex */
public class ChannelModel {
    private Long _id;
    private Object chanels;
    private int channelId;
    private String downloadDate;
    private String downloadDays;
    private String name;
    private String publishDate;
    private String server;
    private String version;

    public ChannelModel(Long l, int i, String str, String str2, String str3, String str4, String str5, String str6) {
        this._id = l;
        this.channelId = i;
        this.server = str;
        this.name = str2;
        this.version = str3;
        this.publishDate = str4;
        this.downloadDate = str5;
        this.downloadDays = str6;
    }

    public ChannelModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public int getChannelId() {
        return this.channelId;
    }

    public void setChannelId(int i) {
        this.channelId = i;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String str) {
        this.server = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String str) {
        this.version = str;
    }

    public String getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(String str) {
        this.publishDate = str;
    }

    public String getDownloadDate() {
        return this.downloadDate;
    }

    public void setDownloadDate(String str) {
        this.downloadDate = str;
    }

    public String getDownloadDays() {
        return this.downloadDays;
    }

    public void setDownloadDays(String str) {
        this.downloadDays = str;
    }
}
