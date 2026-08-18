package com.lianhexinye.m90.greendao.gen;

import com.lianhexinye.m90.mvp.BaseModel;

/* JADX INFO: loaded from: classes2.dex */
public class BusLineFriendRemindModel extends BaseModel {
    private Long _id;
    private String busLineName;
    private String crossCode;
    private String crossDepartureExpansion;
    private String crossDeparturePrompt;
    private String crossExpansion;
    private String crossPrompt;
    private String crossSpeedLimit;
    private String crossType;
    private int direction;
    private String directionName;
    private String filePath;
    private int frNo;
    private String frVoice;
    private String latitude;
    private String longitude;
    private String mileage;
    private String voiceNot;

    public BusLineFriendRemindModel(Long l, int i, String str, int i2, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15) {
        this._id = l;
        this.frNo = i;
        this.busLineName = str;
        this.direction = i2;
        this.frVoice = str2;
        this.longitude = str3;
        this.latitude = str4;
        this.filePath = str5;
        this.directionName = str6;
        this.mileage = str7;
        this.crossCode = str8;
        this.crossPrompt = str9;
        this.crossDeparturePrompt = str10;
        this.crossExpansion = str11;
        this.crossDepartureExpansion = str12;
        this.crossSpeedLimit = str13;
        this.crossType = str14;
        this.voiceNot = str15;
    }

    public BusLineFriendRemindModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public int getFrNo() {
        return this.frNo;
    }

    public void setFrNo(int i) {
        this.frNo = i;
    }

    public String getBusLineName() {
        return this.busLineName;
    }

    public void setBusLineName(String str) {
        this.busLineName = str;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int i) {
        this.direction = i;
    }

    public String getFrVoice() {
        return this.frVoice;
    }

    public void setFrVoice(String str) {
        this.frVoice = str;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String str) {
        this.longitude = str;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String str) {
        this.latitude = str;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public String getDirectionName() {
        return this.directionName;
    }

    public void setDirectionName(String str) {
        this.directionName = str;
    }

    public String getMileage() {
        return this.mileage;
    }

    public void setMileage(String str) {
        this.mileage = str;
    }

    public String getVoiceNot() {
        return this.voiceNot;
    }

    public void setVoiceNot(String str) {
        this.voiceNot = str;
    }

    public String getCrossCode() {
        return this.crossCode;
    }

    public void setCrossCode(String str) {
        this.crossCode = str;
    }

    public String getCrossPrompt() {
        return this.crossPrompt;
    }

    public void setCrossPrompt(String str) {
        this.crossPrompt = str;
    }

    public String getCrossDeparturePrompt() {
        return this.crossDeparturePrompt;
    }

    public void setCrossDeparturePrompt(String str) {
        this.crossDeparturePrompt = str;
    }

    public String getCrossExpansion() {
        return this.crossExpansion;
    }

    public void setCrossExpansion(String str) {
        this.crossExpansion = str;
    }

    public String getCrossDepartureExpansion() {
        return this.crossDepartureExpansion;
    }

    public void setCrossDepartureExpansion(String str) {
        this.crossDepartureExpansion = str;
    }

    public String getCrossSpeedLimit() {
        return this.crossSpeedLimit;
    }

    public void setCrossSpeedLimit(String str) {
        this.crossSpeedLimit = str;
    }

    public String getCrossType() {
        return this.crossType;
    }

    public void setCrossType(String str) {
        this.crossType = str;
    }
}
