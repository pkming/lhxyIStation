package com.lhxy.istationdevice.android11.protocol.jt808;

import java.time.LocalDateTime;

/**
 * JT808 违规信息参数
 */
public final class Jt808ViolationSnapshot {
    private final int lineNumber;
    private final int overSpeed;
    private final int speed;
    private final int speedLimit;
    private final String latitude;
    private final String longitude;
    private final int angle;
    private final LocalDateTime terminalTime;
    private final String promptInfo;

    public Jt808ViolationSnapshot(
            int lineNumber,
            int overSpeed,
            int speed,
            int speedLimit,
            String latitude,
            String longitude,
            int angle,
            LocalDateTime terminalTime,
            String promptInfo
    ) {
        this.lineNumber = lineNumber;
        this.overSpeed = overSpeed;
        this.speed = speed;
        this.speedLimit = speedLimit;
        this.latitude = latitude;
        this.longitude = longitude;
        this.angle = angle;
        this.terminalTime = terminalTime;
        this.promptInfo = promptInfo;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getOverSpeed() {
        return overSpeed;
    }

    public int getSpeed() {
        return speed;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getAngle() {
        return angle;
    }

    public LocalDateTime getTerminalTime() {
        return terminalTime;
    }

    public String getPromptInfo() {
        return promptInfo;
    }
}
