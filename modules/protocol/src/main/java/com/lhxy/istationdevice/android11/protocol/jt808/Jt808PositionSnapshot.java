package com.lhxy.istationdevice.android11.protocol.jt808;

import java.time.LocalDateTime;

public final class Jt808PositionSnapshot {
    private final long alarmFlag;
    private final long statusFlag;
    private final String latitude;
    private final String longitude;
    private final int speed;
    private final int direction;
    private final int totalMileage;
    private final LocalDateTime terminalTime;

    public Jt808PositionSnapshot(
            long alarmFlag,
            long statusFlag,
            String latitude,
            String longitude,
            int speed,
            int direction,
            int totalMileage,
            LocalDateTime terminalTime
    ) {
        this.alarmFlag = alarmFlag;
        this.statusFlag = statusFlag;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.direction = direction;
        this.totalMileage = totalMileage;
        this.terminalTime = terminalTime;
    }

    public long getAlarmFlag() {
        return alarmFlag;
    }

    public long getStatusFlag() {
        return statusFlag;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getSpeed() {
        return speed;
    }

    public int getDirection() {
        return direction;
    }

    public int getTotalMileage() {
        return totalMileage;
    }

    public LocalDateTime getTerminalTime() {
        return terminalTime;
    }
}
