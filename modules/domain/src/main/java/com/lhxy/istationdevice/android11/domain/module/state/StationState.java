package com.lhxy.istationdevice.android11.domain.module.state;

import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private String activeCrossSpeedLimit = "-";
    private String activeCrossArrivalTime = "-";
    private String activeCrossCode = "-";
    private String activeCrossType = "-";
    private int activeReminderNo = -1;
    private int satellites;
    private int stationCursor = -1;
    private int displayStationNo = -1;
    private int currentStationNo = -1;
    private int currentStationType;
    private int reportCount;
    private boolean previewingNext;
    private boolean crossingReminderActive;

    public StationState() {
        applyLineProfile("101路", "上行", Arrays.asList("火车站", "市政府", "人民广场", "科技园"));
    }

    public void advanceStation() {
        reportCount++;
        if (routeStations.isEmpty()) {
            currentStation = "-";
            nextStation = "-";
            displayStationNo = -1;
            currentStationNo = -1;
            reportPhase = "待发";
            return;
        }

        if (!previewingNext) {
            if (stationCursor >= routeStations.size() - 1) {
                stationCursor = routeStations.size() - 1;
                displayStationNo = stationCursor;
                currentStationNo = stationCursor;
                currentStationType = 0;
                currentStation = routeStations.get(stationCursor);
                nextStation = "-";
                reportPhase = "终点到站";
                clearReminderState();
                return;
            }
            if (stationCursor < routeStations.size() - 1) {
                stationCursor++;
            }
            previewingNext = true;
            displayStationNo = stationCursor > 0 ? stationCursor - 1 : 0;
            currentStationNo = stationCursor;
            currentStationType = 1;
            currentStation = routeStations.get(stationCursor);
            nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
            if (stationCursor == 1) {
                reportPhase = "起点发车";
            } else if (stationCursor >= routeStations.size() - 1) {
                reportPhase = "终点预报";
            } else {
                reportPhase = "出站预报";
            }
        } else {
            previewingNext = false;
            displayStationNo = stationCursor;
            currentStationNo = stationCursor;
            currentStationType = 0;
            currentStation = routeStations.get(stationCursor);
            nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
            reportPhase = stationCursor >= routeStations.size() - 1 ? "终点到站" : "进站播报";
        }
        clearReminderState();
    }

    public void retreatStation() {
        if (routeStations.isEmpty()) {
            return;
        }
        reportCount++;
        if (previewingNext) {
            previewingNext = false;
            if (stationCursor > 0) {
                stationCursor--;
            }
            displayStationNo = stationCursor;
            currentStationNo = stationCursor;
            currentStationType = 0;
            currentStation = routeStations.get(stationCursor);
            nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
            reportPhase = stationCursor == 0 ? "回到起点" : "回退到站";
        } else {
            if (stationCursor <= 0) {
                return;
            }
            previewingNext = true;
            displayStationNo = stationCursor;
            currentStationNo = stationCursor;
            currentStationType = 1;
            currentStation = routeStations.get(stationCursor);
            nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
            reportPhase = "回退预报";
        }
        clearReminderState();
    }

    public boolean quickStepForward() {
        if (routeStations.isEmpty()) {
            return false;
        }
        if (!previewingNext && stationCursor >= routeStations.size() - 1) {
            return false;
        }
        if (!previewingNext) {
            stationCursor++;
            previewingNext = true;
            displayStationNo = stationCursor > 0 ? stationCursor - 1 : 0;
            currentStationNo = stationCursor;
            currentStationType = 1;
            currentStation = routeStations.get(stationCursor);
            nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
            if (stationCursor == 1) {
                reportPhase = "起点发车";
            } else if (stationCursor >= routeStations.size() - 1) {
                reportPhase = "终点预报";
            } else {
                reportPhase = "出站预报";
            }
        } else {
            previewingNext = false;
            displayStationNo = stationCursor;
            currentStationNo = stationCursor;
            currentStationType = 0;
            currentStation = routeStations.get(stationCursor);
            nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
            reportPhase = stationCursor >= routeStations.size() - 1 ? "终点到站" : "进站播报";
        }
        clearReminderState();
        return true;
    }

    public boolean quickStepBackward() {
        if (routeStations.isEmpty()) {
            return false;
        }
        if (previewingNext) {
            previewingNext = false;
            if (stationCursor > 0) {
                stationCursor--;
            }
            displayStationNo = stationCursor;
            currentStationNo = stationCursor;
            currentStationType = 0;
            currentStation = routeStations.get(stationCursor);
            nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
            reportPhase = stationCursor == 0 ? "回到起点" : "回退到站";
            clearReminderState();
            return true;
        }
        if (stationCursor - 1 < 0) {
            return false;
        }
        previewingNext = true;
        displayStationNo = stationCursor;
        currentStationNo = stationCursor;
        currentStationType = 1;
        currentStation = routeStations.get(stationCursor);
        nextStation = stationCursor + 1 < routeStations.size() ? routeStations.get(stationCursor + 1) : "-";
        reportPhase = "回退预报";
        clearReminderState();
        return true;
    }

    public boolean repeatCurrentStation() {
        if (previewingNext) {
            return false;
        }
        reportCount++;
        if ("-".equals(currentStation)) {
            advanceStation();
            return true;
        }
        currentStationType = 0;
        reportPhase = "重复报站";
        clearReminderState();
        return true;
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
        previewingNext = stationType == 1;
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
        clearReminderState();
    }

    public void recordReminder(String reminderName, String crossCode, String crossType, int reminderNo, String crossSpeedLimit, int reminderType) {
        lastReminder = emptyAsDash(reminderName);
        if (reminderType == 1) {
            crossingReminderActive = false;
            activeCrossSpeedLimit = "-";
            activeCrossArrivalTime = "-";
            activeCrossCode = "-";
            activeCrossType = "-";
            activeReminderNo = -1;
        } else {
            crossingReminderActive = true;
            activeCrossSpeedLimit = normalizeSpeedLimit(crossSpeedLimit);
            activeCrossArrivalTime = compactNowTime();
            activeCrossCode = emptyAsDash(crossCode);
            activeCrossType = emptyAsDash(crossType);
            activeReminderNo = reminderNo;
        }
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

    public boolean isPreviewingNext() {
        return previewingNext;
    }

    public boolean isFirstDeparturePreview() {
        return previewingNext && currentStationNo == 1;
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

    public int getStationCount() {
        return routeStations.size();
    }

    public int getCurrentStationNo() {
        return currentStationNo;
    }

    public int getDisplayStationNo() {
        return displayStationNo;
    }

    public int getCurrentStationType() {
        return currentStationType;
    }

    public String getLastReminder() {
        return lastReminder;
    }

    public boolean isCrossingReminderActive() {
        return crossingReminderActive;
    }

    public String getActiveCrossSpeedLimit() {
        return activeCrossSpeedLimit;
    }

    public String getActiveCrossArrivalTime() {
        return activeCrossArrivalTime;
    }

    public String getActiveCrossCode() {
        return activeCrossCode;
    }

    public String getActiveCrossType() {
        return activeCrossType;
    }

    public int getActiveReminderNo() {
        return activeReminderNo;
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
        displayStationNo = -1;
        currentStationNo = -1;
        currentStationType = 0;
        reportCount = 0;
        previewingNext = false;
        currentStation = "-";
        clearReminderState();
        reportPhase = "待发";
        if (routeStations.isEmpty()) {
            nextStation = "-";
            terminalStation = "-";
            return;
        }
        stationCursor = 0;
        displayStationNo = 0;
        currentStationNo = 0;
        currentStationType = 0;
        currentStation = routeStations.get(0);
        nextStation = routeStations.size() > 1 ? routeStations.get(1) : "-";
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
                + "\n- previewingNext=" + previewingNext
                + "\n- phase=" + emptyAsDash(reportPhase)
                + "\n- reminder=" + emptyAsDash(lastReminder)
                + "\n- crossSpeedLimit=" + emptyAsDash(activeCrossSpeedLimit)
                + "\n- crossArrivalTime=" + emptyAsDash(activeCrossArrivalTime)
                + "\n- crossCode=" + emptyAsDash(activeCrossCode)
                + "\n- crossType=" + emptyAsDash(activeCrossType)
                + "\n- reminderNo=" + activeReminderNo
                + "\n- crossingReminderActive=" + crossingReminderActive
                + "\n- gpsChannel=" + emptyAsDash(gpsChannelKey)
                + "\n- gpsLatLng=" + emptyAsDash(latitude) + "," + emptyAsDash(longitude)
                + "\n- satellites=" + satellites;
    }

    private void clearReminderState() {
        lastReminder = "-";
        activeCrossSpeedLimit = "-";
        activeCrossArrivalTime = "-";
        activeCrossCode = "-";
        activeCrossType = "-";
        activeReminderNo = -1;
        crossingReminderActive = false;
    }

    private String compactNowTime() {
        return new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    private String normalizeSpeedLimit(String speedLimit) {
        String value = emptyAsDash(speedLimit);
        return "-".equals(value) ? "0" : value;
    }

    private String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
