package com.lianhexinye.m90.greendao.gen;

import com.lianhexinye.m90.mvp.BaseModel;

/* JADX INFO: loaded from: classes2.dex */
public class BusLineModel extends BaseModel {
    private Long _id;
    private String angle;
    private String busFilePath;
    private String busLineName;
    private String busName;
    private String busNameE;
    private int busNo;
    private String busPrice;
    private String busSound;
    private long continueTime;
    private int countStation;
    private String departureAdvert;
    private String departureExpansion;
    private String departurePrompt;
    private int direction;
    private String directionName;
    private String endBusName;
    private String endBusNameE;
    private String iMajorStation;
    private boolean isStationWithin;
    private String latitude;
    private String longitude;
    private String mileage;
    private String siteCode;
    private String speed;
    private String speedLimit;
    private String speedLimitInStation;
    private String startBusName;
    private String startBusNameE;
    private long startTime;
    private String stationAdvert;
    private String stationExpansion;
    private String stationPrompt;
    private int stationType;
    private String voiceNot;

    public BusLineModel(Long l, int i, String str, String str2, String str3, String str4, int i2, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22, String str23) {
        this._id = l;
        this.busNo = i;
        this.busSound = str;
        this.busFilePath = str2;
        this.busName = str3;
        this.busLineName = str4;
        this.direction = i2;
        this.longitude = str5;
        this.latitude = str6;
        this.angle = str7;
        this.siteCode = str8;
        this.stationAdvert = str9;
        this.departureAdvert = str10;
        this.stationPrompt = str11;
        this.departurePrompt = str12;
        this.stationExpansion = str13;
        this.departureExpansion = str14;
        this.speedLimit = str15;
        this.speedLimitInStation = str16;
        this.mileage = str17;
        this.iMajorStation = str18;
        this.voiceNot = str19;
        this.directionName = str20;
        this.speed = str21;
        this.busNameE = str22;
        this.busPrice = str23;
    }

    public BusLineModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public int getBusNo() {
        return this.busNo;
    }

    public void setBusNo(int i) {
        this.busNo = i;
    }

    public String getBusSound() {
        return this.busSound;
    }

    public void setBusSound(String str) {
        this.busSound = str;
    }

    public String getBusFilePath() {
        return this.busFilePath;
    }

    public void setBusFilePath(String str) {
        this.busFilePath = str;
    }

    public String getBusName() {
        return this.busName;
    }

    public void setBusName(String str) {
        this.busName = str;
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

    public String getAngle() {
        return this.angle;
    }

    public void setAngle(String str) {
        this.angle = str;
    }

    public String getSiteCode() {
        return this.siteCode;
    }

    public void setSiteCode(String str) {
        this.siteCode = str;
    }

    public String getStationAdvert() {
        return this.stationAdvert;
    }

    public void setStationAdvert(String str) {
        this.stationAdvert = str;
    }

    public String getDepartureAdvert() {
        return this.departureAdvert;
    }

    public void setDepartureAdvert(String str) {
        this.departureAdvert = str;
    }

    public String getStationPrompt() {
        return this.stationPrompt;
    }

    public void setStationPrompt(String str) {
        this.stationPrompt = str;
    }

    public String getDeparturePrompt() {
        return this.departurePrompt;
    }

    public void setDeparturePrompt(String str) {
        this.departurePrompt = str;
    }

    public String getStationExpansion() {
        return this.stationExpansion;
    }

    public void setStationExpansion(String str) {
        this.stationExpansion = str;
    }

    public String getDepartureExpansion() {
        return this.departureExpansion;
    }

    public void setDepartureExpansion(String str) {
        this.departureExpansion = str;
    }

    public String getSpeedLimit() {
        return this.speedLimit;
    }

    public void setSpeedLimit(String str) {
        this.speedLimit = str;
    }

    public String getMileage() {
        return this.mileage;
    }

    public void setMileage(String str) {
        this.mileage = str;
    }

    public String getIMajorStation() {
        return this.iMajorStation;
    }

    public void setIMajorStation(String str) {
        this.iMajorStation = str;
    }

    public String getVoiceNot() {
        return this.voiceNot;
    }

    public void setVoiceNot(String str) {
        this.voiceNot = str;
    }

    public String getDirectionName() {
        return this.directionName;
    }

    public void setDirectionName(String str) {
        this.directionName = str;
    }

    public String getSpeed() {
        return this.speed;
    }

    public void setSpeed(String str) {
        this.speed = str;
    }

    public String getBusNameE() {
        return this.busNameE;
    }

    public void setBusNameE(String str) {
        this.busNameE = str;
    }

    public String getiMajorStation() {
        return this.iMajorStation;
    }

    public void setiMajorStation(String str) {
        this.iMajorStation = str;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long j) {
        this.startTime = j;
    }

    public long getContinueTime() {
        return this.continueTime;
    }

    public void setContinueTime(long j) {
        this.continueTime = j;
    }

    public boolean isStationWithin() {
        return this.isStationWithin;
    }

    public void setStationWithin(boolean z) {
        this.isStationWithin = z;
    }

    public String getStartBusName() {
        return this.startBusName;
    }

    public void setStartBusName(String str) {
        this.startBusName = str;
    }

    public String getEndBusName() {
        return this.endBusName;
    }

    public void setEndBusName(String str) {
        this.endBusName = str;
    }

    public String getStartBusNameE() {
        return this.startBusNameE;
    }

    public void setStartBusNameE(String str) {
        this.startBusNameE = str;
    }

    public String getEndBusNameE() {
        return this.endBusNameE;
    }

    public void setEndBusNameE(String str) {
        this.endBusNameE = str;
    }

    public int getStationType() {
        return this.stationType;
    }

    public void setStationType(int i) {
        this.stationType = i;
    }

    public int getCountStation() {
        return this.countStation;
    }

    public void setCountStation(int i) {
        this.countStation = i;
    }

    public String getBusPrice() {
        return this.busPrice;
    }

    public void setBusPrice(String str) {
        this.busPrice = str;
    }

    public String getSpeedLimitInStation() {
        return this.speedLimitInStation;
    }

    public void setSpeedLimitInStation(String str) {
        this.speedLimitInStation = str;
    }
}
