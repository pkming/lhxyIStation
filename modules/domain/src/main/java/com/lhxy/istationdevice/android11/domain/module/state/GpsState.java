package com.lhxy.istationdevice.android11.domain.module.state;

import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * GPS 业务状态。
 */
public final class GpsState {
    private String gpsChannelKey = "-";
    private String gpsPortName = "-";
    private boolean attached;
    private boolean valid;
    private int fixQuality;
    private int fixType;
    private int usedSatellites;
    private String latitude = "-";
    private String longitude = "-";
    private String altitude = "-";
    private String speedKnots = "-";
    private String course = "-";
    private String lineName = "-";
    private String directionText = "上行";
    private String lineAttribute = "-";
    private int stationCount;
    private int stationCoordinateCount;
    private int reminderCount;
    private int reminderCoordinateCount;
    private String baselineSummary = "未扫描 L1 基线";
    private long lastFixTimeMillis;
    private long lastRouteSyncTimeMillis;
    private long lastBaselineScanTimeMillis;

    public void bindMonitor(String channelKey, String portName, boolean attached) {
        gpsChannelKey = emptyAsDash(channelKey);
        gpsPortName = emptyAsDash(portName);
        this.attached = attached;
    }

    public void setPreferredLineName(String lineName) {
        if (lineName != null && !lineName.trim().isEmpty() && !"-".equals(lineName.trim())) {
            this.lineName = lineName.trim();
        }
    }

    public void setPreferredDirectionText(String directionText) {
        if (directionText != null && !directionText.trim().isEmpty()) {
            this.directionText = directionText.trim();
        }
    }

    public void toggleDirection() {
        directionText = directionText != null && directionText.contains("下") ? "上行" : "下行";
    }

    public void applySnapshot(GpsFixSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        valid = snapshot.isValid();
        fixQuality = snapshot.getFixQuality();
        fixType = snapshot.getFixType();
        usedSatellites = snapshot.getUsedSatellites();
        latitude = emptyAsDash(snapshot.getLatitudeDecimal());
        longitude = emptyAsDash(snapshot.getLongitudeDecimal());
        altitude = emptyAsDash(snapshot.getAltitudeMeters());
        speedKnots = emptyAsDash(snapshot.getSpeedKnots());
        course = emptyAsDash(snapshot.getCourse());
        lastFixTimeMillis = System.currentTimeMillis();
    }

    public void applyRoute(LegacyGpsRouteResource route) {
        if (route == null) {
            return;
        }
        lineName = emptyAsDash(route.getLineName());
        directionText = emptyAsDash(route.getDirectionText());
        lineAttribute = emptyAsDash(route.getAttributeLabel());
        stationCount = route.getStations().size();
        reminderCount = route.getReminders().size();
        stationCoordinateCount = countStationCoordinates(route);
        reminderCoordinateCount = countReminderCoordinates(route);
        lastRouteSyncTimeMillis = System.currentTimeMillis();
    }

    public void markMissingRoute(String lineName, String directionText) {
        this.lineName = emptyAsDash(lineName);
        this.directionText = emptyAsDash(directionText);
        lineAttribute = "-";
        stationCount = 0;
        stationCoordinateCount = 0;
        reminderCount = 0;
        reminderCoordinateCount = 0;
        lastRouteSyncTimeMillis = System.currentTimeMillis();
    }

    public void markBaselineSummary(String summary) {
        baselineSummary = emptyAsDash(summary);
        lastBaselineScanTimeMillis = System.currentTimeMillis();
    }

    public String getLineName() {
        return lineName;
    }

    public String getDirectionText() {
        return directionText;
    }

    public String describe() {
        return "attached=" + yesNo(attached)
                + "\n- channel=" + gpsChannelKey + " / " + gpsPortName
                + "\n- fixValid=" + yesNo(valid)
                + "\n- fixQuality=" + fixQuality
                + "\n- fixType=" + describeFixType()
                + "\n- usedSatellites=" + usedSatellites
                + "\n- latLng=" + latitude + "," + longitude
                + "\n- altitude=" + altitude
                + "\n- speedKnots=" + speedKnots
                + "\n- course=" + course
                + "\n- route=" + lineName + " / " + directionText + " / " + lineAttribute
                + "\n- stations(coord/total)=" + stationCoordinateCount + "/" + stationCount
                + "\n- reminders(coord/total)=" + reminderCoordinateCount + "/" + reminderCount
                + "\n- baseline=" + baselineSummary
                + "\n- migrationTodo=自动报站 / 友情提醒 / GPS 校时 / 站点学习落库 / 网络上报对齐"
                + "\n- lastFixTime=" + formatTime(lastFixTimeMillis)
                + "\n- lastRouteSync=" + formatTime(lastRouteSyncTimeMillis)
                + "\n- lastBaselineScan=" + formatTime(lastBaselineScanTimeMillis);
    }

    private int countStationCoordinates(LegacyGpsRouteResource route) {
        int count = 0;
        for (LegacyGpsRouteResource.StationPoint point : route.getStations()) {
            if (hasCoordinate(point.getLongitudeDecimal(), point.getLatitudeDecimal())) {
                count++;
            }
        }
        return count;
    }

    private int countReminderCoordinates(LegacyGpsRouteResource route) {
        int count = 0;
        for (LegacyGpsRouteResource.ReminderPoint point : route.getReminders()) {
            if (hasCoordinate(point.getLongitudeDecimal(), point.getLatitudeDecimal())) {
                count++;
            }
        }
        return count;
    }

    private boolean hasCoordinate(double longitude, double latitude) {
        return Math.abs(longitude) > 0.000001d && Math.abs(latitude) > 0.000001d;
    }

    private String describeFixType() {
        if (fixType == 1) {
            return "1(no-fix)";
        }
        if (fixType == 2) {
            return "2(2D)";
        }
        if (fixType == 3) {
            return "3(3D)";
        }
        return String.valueOf(fixType);
    }

    private String yesNo(boolean value) {
        return value ? "是" : "否";
    }

    private String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private String formatTime(long timeMillis) {
        if (timeMillis <= 0) {
            return "-";
        }
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(timeMillis));
    }
}