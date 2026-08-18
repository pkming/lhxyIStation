package com.lianhexinye.m90.greendao.gen;

/* JADX INFO: loaded from: classes2.dex */
public class ProgramModel {
    private Long _id;
    private String backgroundAudio;
    private String backgroundContent;
    private int backgroundType;
    private String contentTypes;
    private int duration;
    private String endTime;
    private boolean isTile;
    private boolean mute;
    private String name;
    private String playTime;
    private int playlistId;
    private int programid;

    public ProgramModel(Long l, int i, int i2, String str, String str2, String str3, int i3, int i4, String str4, String str5, boolean z, boolean z2, String str6) {
        this._id = l;
        this.playlistId = i;
        this.programid = i2;
        this.name = str;
        this.playTime = str2;
        this.endTime = str3;
        this.duration = i3;
        this.backgroundType = i4;
        this.backgroundContent = str4;
        this.backgroundAudio = str5;
        this.mute = z;
        this.isTile = z2;
        this.contentTypes = str6;
    }

    public ProgramModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public int getPlaylistId() {
        return this.playlistId;
    }

    public void setPlaylistId(int i) {
        this.playlistId = i;
    }

    public int getProgramid() {
        return this.programid;
    }

    public void setProgramid(int i) {
        this.programid = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(String str) {
        this.playTime = str;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String str) {
        this.endTime = str;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    public int getBackgroundType() {
        return this.backgroundType;
    }

    public void setBackgroundType(int i) {
        this.backgroundType = i;
    }

    public String getBackgroundContent() {
        return this.backgroundContent;
    }

    public void setBackgroundContent(String str) {
        this.backgroundContent = str;
    }

    public String getBackgroundAudio() {
        return this.backgroundAudio;
    }

    public void setBackgroundAudio(String str) {
        this.backgroundAudio = str;
    }

    public boolean getMute() {
        return this.mute;
    }

    public void setMute(boolean z) {
        this.mute = z;
    }

    public boolean getIsTile() {
        return this.isTile;
    }

    public void setIsTile(boolean z) {
        this.isTile = z;
    }

    public String getContentTypes() {
        return this.contentTypes;
    }

    public void setContentTypes(String str) {
        this.contentTypes = str;
    }
}
