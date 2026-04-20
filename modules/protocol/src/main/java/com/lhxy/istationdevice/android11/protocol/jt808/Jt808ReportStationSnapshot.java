package com.lhxy.istationdevice.android11.protocol.jt808;

import java.time.LocalDateTime;

/**
 * JT808 / AL808 报站参数
 */
public final class Jt808ReportStationSnapshot {
    private final int lineNumber;
    private final int status;
    private final int direction;
    private final int busNo;
    private final String latitude;
    private final String longitude;
    private final int speed;
    private final int angle;
    private final LocalDateTime terminalTime;

    public Jt808ReportStationSnapshot(
            int lineNumber,
            int status,
            int direction,
            int busNo,
            String latitude,
            String longitude,
            int speed,
            int angle,
            LocalDateTime terminalTime
    ) {
        this.lineNumber = lineNumber;
        this.status = status;
        this.direction = direction;
        this.busNo = busNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.angle = angle;
        this.terminalTime = terminalTime;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getStatus() {
        return status;
    }

    public int getDirection() {
        return direction;
    }

    public int getBusNo() {
        return busNo;
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

    public int getAngle() {
        return angle;
    }

    public LocalDateTime getTerminalTime() {
        return terminalTime;
    }
}
