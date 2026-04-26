package com.lhxy.istationdevice.android11.domain.module.state;

import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Station business state shared by UI, DVR dispatch, and GPS auto-report.
 */
public final class StationState {
    private final List<String> routeStations = new ArrayList<>();

    private String lineName = "101路";
    private String lineAttribute = "对开";
    private String directionText = "上行";
    private String currentStation = "-";
    private String nextStation = "-";
    private String terminalStation = "-";
    private String reportPhase = "待发";
    private String gpsChannelKey = "-";
    private String latitude = "-";
    private String longitude = "-";
    private String lastReminder = "-";
    private int satellites;
    private int stationCursor = -1;
    private int currentStationNo = -1;
    private int currentStationType;
    private int reportCount;

    public StationState() {
        routeStations.addAll(Arrays.asList("火车站", "市政府", "人民广场", "科技园"));
        terminalStation = routeStations.get(routeStations.size() - 1);
        nextStation = routeStations.get(0);
    }

    public void advanceStation() {
        reportCount++;
        if (routeStations.isEmpty()) {
            currentStation = "-";
            nextStation = "-";
            currentStationNo = -1;
            reportPhase = "待发";
            return;
        }

        if (stationCursor < 0) {
            stationCursor = 0;
        } else if (stationCursor < routeStations.size() - 1) {
            stationCursor++;
        }
        currentStationNo = stationCursor;
        currentStationType = 0;
        currentStation = routeStations.get(stationCursor);
        nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
        reportPhase = stationCursor == 0 ? "起点发车" : stationCursor == routeStations.size() - 1 ? "终点到站" : "进站播报";
        lastReminder = "-";
    }

    public void repeatCurrentStation() {
        reportCount++;
        if ("-".equals(currentStation)) {
            advanceStation();
            return;
        }
        reportPhase = "重复报站";
    }

    public void stopReport() {
        reportPhase = "停止报站";
    }

    public void bindGps(String channelKey) {
        gpsChannelKey = emptyAsDash(channelKey);
    }

    public void updateGps(GpsFixSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        latitude = emptyAsDash(snapshot.getLatitudeDecimal());
        longitude = emptyAsDash(snapshot.getLongitudeDecimal());
        satellites = snapshot.getUsedSatellites();
    }

    public void setLineAttribute(String lineAttribute) {
        this.lineAttribute = emptyAsDash(lineAttribute);
    }

    public void recordAutoStation(int stationNo, String stationName, int stationType) {
        reportCount++;
        currentStationNo = Math.max(stationNo, -1);
        currentStationType = stationType;
        stationCursor = currentStationNo;
        currentStation = emptyAsDash(stationName);
        nextStation = stationCursor + 1 >= 0 && stationCursor + 1 < routeStations.size()
                ? routeStations.get(stationCursor + 1)
                : "-";
        if (stationType == 1) {
            reportPhase = stationCursor >= routeStations.size() - 1 ? "终点预报" : "出站预报";
        } else if (stationCursor == 0) {
            reportPhase = "起点发车";
        } else if (stationCursor >= routeStations.size() - 1) {
            reportPhase = "终点到站";
        } else {
            reportPhase = "进站播报";
        }
        if (stationCursor >= 0 && stationCursor < routeStations.size()) {
            terminalStation = routeStations.get(routeStations.size() - 1);
        }
        lastReminder = "-";
    }

    public void recordReminder(String reminderName) {
        lastReminder = emptyAsDash(reminderName);
        reportPhase = "友情提醒";
    }

    public void recordDirectionSwitch(String directionText, List<String> stations) {
        applyLineProfile(lineName, directionText, stations);
        reportPhase = "自动切向";
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

    public String getLineAttribute() {
        return lineAttribute;
    }

    public String getDirectionText() {
        return directionText;
    }

    public void setDirectionText(String directionText) {
        if (directionText == null || directionText.trim().isEmpty()) {
            return;
        }
        this.directionText = directionText.trim();
    }

    public String getCurrentStation() {
        return currentStation;
    }

    public String getNextStation() {
        return nextStation;
    }

    public String getPreviousStation() {
        if (stationCursor <= 0 || stationCursor > routeStations.size() - 1) {
            return "-";
        }
        return routeStations.get(stationCursor - 1);
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

    public int getCurrentStationNo() {
        return currentStationNo;
    }

    public int getCurrentStationType() {
        return currentStationType;
    }

    public String getLastReminder() {
        return lastReminder;
    }

    public void applyLineProfile(String lineName, String directionText, List<String> stations) {
        if (lineName != null && !lineName.trim().isEmpty()) {
            this.lineName = lineName.trim();
        }
        if (directionText != null && !directionText.trim().isEmpty()) {
            this.directionText = directionText.trim();
        }
        routeStations.clear();
        if (stations != null) {
            for (String station : stations) {
                if (station != null && !station.trim().isEmpty()) {
                    routeStations.add(station.trim());
                }
            }
        }
        stationCursor = -1;
        currentStationNo = -1;
        currentStationType = 0;
        reportCount = 0;
        currentStation = "-";
        lastReminder = "-";
        reportPhase = "待发";
        if (routeStations.isEmpty()) {
            nextStation = "-";
            terminalStation = "-";
            return;
        }
        nextStation = routeStations.get(0);
        terminalStation = routeStations.get(routeStations.size() - 1);
    }

    public String describe() {
        return "line=" + emptyAsDash(lineName)
                + "\n- attribute=" + emptyAsDash(lineAttribute)
                + "\n- direction=" + emptyAsDash(directionText)
                + "\n- currentStation=" + emptyAsDash(currentStation)
                + "\n- nextStation=" + emptyAsDash(nextStation)
                + "\n- stationNo=" + currentStationNo
                + "\n- stationType=" + currentStationType
                + "\n- phase=" + emptyAsDash(reportPhase)
                + "\n- reminder=" + emptyAsDash(lastReminder)
                + "\n- gpsChannel=" + emptyAsDash(gpsChannelKey)
                + "\n- gpsLatLng=" + emptyAsDash(latitude) + "," + emptyAsDash(longitude)
                + "\n- satellites=" + satellites;
    }

    private String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
