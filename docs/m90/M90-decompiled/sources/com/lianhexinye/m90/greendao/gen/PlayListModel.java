package com.lianhexinye.m90.greendao.gen;

/* JADX INFO: loaded from: classes2.dex */
public class PlayListModel {
    private Long _id;
    private int channelId;
    private String name;
    private int playlistId;
    private String startTime;

    public PlayListModel(Long l, int i, String str, String str2, int i2) {
        this._id = l;
        this.channelId = i;
        this.name = str;
        this.startTime = str2;
        this.playlistId = i2;
    }

    public PlayListModel() {
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

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String str) {
        this.startTime = str;
    }

    public int getPlaylistId() {
        return this.playlistId;
    }

    public void setPlaylistId(int i) {
        this.playlistId = i;
    }
}
