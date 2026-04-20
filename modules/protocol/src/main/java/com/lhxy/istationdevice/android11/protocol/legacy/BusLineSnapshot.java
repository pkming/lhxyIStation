package com.lhxy.istationdevice.android11.protocol.legacy;

public final class BusLineSnapshot {
    private final int busNo;
    private final int stationType;
    private final int direction;
    private final int countStation;
    private final String busName;
    private final String busLineName;
    private final String startBusName;
    private final String endBusName;

    public BusLineSnapshot(
            int busNo,
            int stationType,
            int direction,
            int countStation,
            String busName,
            String busLineName,
            String startBusName,
            String endBusName
    ) {
        this.busNo = busNo;
        this.stationType = stationType;
        this.direction = direction;
        this.countStation = countStation;
        this.busName = busName;
        this.busLineName = busLineName;
        this.startBusName = startBusName;
        this.endBusName = endBusName;
    }

    public int getBusNo() {
        return busNo;
    }

    public int getStationType() {
        return stationType;
    }

    public int getDirection() {
        return direction;
    }

    public int getCountStation() {
        return countStation;
    }

    public String getBusName() {
        return busName;
    }

    public String getBusLineName() {
        return busLineName;
    }

    public String getStartBusName() {
        return startBusName;
    }

    public String getEndBusName() {
        return endBusName;
    }
}

