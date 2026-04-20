package com.lhxy.istationdevice.android11.domain.module.state;

import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 报站业务状态。
 * <p>
 * 统一保存线路、方向、本站、下站和 GPS 关键状态。
 */
public final class StationState {
    private final List<String> routeStations = new ArrayList<>();
    private String lineName = "101路";
    private String directionText = "上行";
    private String currentStation = "-";
    private String nextStation = "-";
    private String terminalStation = "-";
    private String reportPhase = "待发";
    private String gpsChannelKey = "-";
    private String latitude = "-";
    private String longitude = "-";
    private int satellites;
    private int stationCursor = -1;
    private int reportCount;

    public StationState() {
        routeStations.addAll(Arrays.asList("火车站", "市政府", "人民广场", "科技园"));
        terminalStation = routeStations.get(routeStations.size() - 1);
        nextStation = routeStations.get(0);
    }

    /**
     * 按样例线路推进一站。
     */
    public void advanceStation() {
        reportCount++;
        if (routeStations.isEmpty()) {
            currentStation = "-";
            nextStation = "-";
            reportPhase = "待发";
            return;
        }

        if (stationCursor < 0) {
            stationCursor = 0;
            currentStation = routeStations.get(0);
            nextStation = routeStations.size() > 1 ? routeStations.get(1) : routeStations.get(0);
            reportPhase = "起点发车";
            return;
        }

        if (stationCursor >= routeStations.size() - 1) {
            currentStation = routeStations.get(routeStations.size() - 1);
            nextStation = "-";
            reportPhase = "终点到站";
            return;
        }

        stationCursor++;
        currentStation = routeStations.get(stationCursor);
        nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
        reportPhase = stationCursor == routeStations.size() - 1 ? "终点到站" : "进站播报";
    }

    /**
     * 重复播报当前站。
     */
    public void repeatCurrentStation() {
        reportCount++;
        if ("-".equals(currentStation)) {
            advanceStation();
            return;
        }
        reportPhase = "重复报站";
    }

    /**
     * 停止报站。
     */
    public void stopReport() {
        reportPhase = "停止报站";
    }

    /**
     * 记录 GPS 监听状态。
     */
    public void bindGps(String channelKey) {
        gpsChannelKey = emptyAsDash(channelKey);
    }

    /**
     * 用定位快照刷新状态。
     */
    public void updateGps(GpsFixSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        latitude = emptyAsDash(snapshot.getLatitudeDecimal());
        longitude = emptyAsDash(snapshot.getLongitudeDecimal());
        satellites = snapshot.getUsedSatellites();
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        if (lineName == null || lineName.trim().isEmpty()) {
            return;
        }
        this.lineName = lineName.trim();
    }

    public String getDirectionText() {
        return directionText;
    }

    public String getCurrentStation() {
        return currentStation;
    }

    public String getNextStation() {
        return nextStation;
    }

    public String getTerminalStation() {
        return terminalStation;
    }

    public String getReportPhase() {
        return reportPhase;
    }

    public String getGpsChannelKey() {
        return gpsChannelKey;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getSatellites() {
        return satellites;
    }

    public int getReportCount() {
        return reportCount;
    }

    public String describe() {
        return "line=" + emptyAsDash(lineName)
                + "\n- direction=" + emptyAsDash(directionText)
                + "\n- currentStation=" + emptyAsDash(currentStation)
                + "\n- nextStation=" + emptyAsDash(nextStation)
                + "\n- phase=" + emptyAsDash(reportPhase)
                + "\n- gpsChannel=" + emptyAsDash(gpsChannelKey)
                + "\n- gpsLatLng=" + emptyAsDash(latitude) + "," + emptyAsDash(longitude)
                + "\n- satellites=" + satellites;
    }

    private String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
