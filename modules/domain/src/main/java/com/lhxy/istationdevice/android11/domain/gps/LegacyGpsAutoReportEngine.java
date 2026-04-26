package com.lhxy.istationdevice.android11.domain.gps;

import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Stateful port of the legacy GPS auto-report decision flow.
 */
public final class LegacyGpsAutoReportEngine {
    public static final int OP_STATION = 0;
    public static final int OP_REMINDER = 1;
    public static final int OP_INVALID = 2;
    public static final int OP_SWITCH_DIRECTION = 3;

    public static final int STATION_TYPE_ENTER = 0;
    public static final int STATION_TYPE_LEAVE = 1;

    private String activeRouteKey = "";
    private int lastStationNo;
    private boolean stationInner = true;
    private int lastReminderNo = -1;
    private boolean initSite = true;
    private final List<String> directionSwitchVotes = new ArrayList<>();

    public synchronized void reset(String routeKey) {
        activeRouteKey = routeKey == null ? "" : routeKey;
        lastStationNo = 0;
        stationInner = true;
        lastReminderNo = -1;
        initSite = true;
        directionSwitchVotes.clear();
    }

    public synchronized AutoReportEvent evaluate(
            LegacyGpsRouteResource route,
            GpsFixSnapshot snapshot,
            boolean angleEnabled
    ) {
        if (route == null || snapshot == null || !snapshot.isValid()) {
            return AutoReportEvent.none();
        }
        String routeKey = route.getLineName() + "|" + route.getDirectionText();
        if (!routeKey.equals(activeRouteKey)) {
            reset(routeKey);
        }

        MatchResult match = findNearest(route, snapshot);
        if (match.operationType == OP_INVALID) {
            return AutoReportEvent.none();
        }
        if (match.operationType == OP_REMINDER) {
            return handleReminder(match, snapshot, angleEnabled);
        }
        return handleStation(route, match, snapshot, angleEnabled);
    }

    private AutoReportEvent handleReminder(MatchResult match, GpsFixSnapshot snapshot, boolean angleEnabled) {
        LegacyGpsRouteResource.ReminderPoint reminder = match.reminderPoint;
        if (reminder == null) {
            return AutoReportEvent.none();
        }
        if (lastReminderNo == reminder.getReminderNo()) {
            return AutoReportEvent.none();
        }
        if (match.distanceMeters > reminder.getMileage() + 20d) {
            return AutoReportEvent.none();
        }
        if (angleEnabled && angleMismatch(reminder.getAngle(), snapshot.getCourse())) {
            return AutoReportEvent.none();
        }
        lastReminderNo = reminder.getReminderNo();
        return AutoReportEvent.reminder(reminder, match.distanceMeters);
    }

    private AutoReportEvent handleStation(
            LegacyGpsRouteResource route,
            MatchResult match,
            GpsFixSnapshot snapshot,
            boolean angleEnabled
    ) {
        LegacyGpsRouteResource.StationPoint station = match.stationPoint;
        if (station == null) {
            return AutoReportEvent.none();
        }
        double distance = match.distanceMeters;
        int attribute = route.getLineAttribute();
        int operationType = OP_STATION;
        int stationType;

        if (distance >= station.getMileage() + 20d) {
            stationType = STATION_TYPE_LEAVE;
            if (lastStationNo == station.getStationNo()) {
                if (station.getStationNo() == 0 && initSite) {
                    operationType = OP_INVALID;
                } else if (stationInner) {
                    if (station.getStationNo() + 1 < route.getStations().size()) {
                        station = route.getStations().get(station.getStationNo() + 1);
                        clearDirectionVotes();
                    } else {
                        operationType = OP_INVALID;
                    }
                } else {
                    operationType = OP_INVALID;
                }
            } else {
                if (attribute == LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN) {
                    addDirectionVote(station.getStationNo());
                    if (directionSwitchVotes.size() < 3) {
                        operationType = OP_INVALID;
                    } else {
                        clearDirectionVotes();
                        operationType = OP_SWITCH_DIRECTION;
                    }
                } else {
                    operationType = OP_INVALID;
                }
            }
        } else {
            stationType = STATION_TYPE_ENTER;
            if (station.getStationNo() == 0) {
                initSite = false;
            }
            if (stationInner) {
                if (lastStationNo == station.getStationNo()) {
                    operationType = OP_INVALID;
                } else if (attribute != LegacyGpsRouteResource.ATTRIBUTE_LOOP && (lastStationNo + 1) == station.getStationNo()) {
                    operationType = OP_INVALID;
                }
            } else if (attribute == LegacyGpsRouteResource.ATTRIBUTE_ANTI_REVERSE && lastStationNo != station.getStationNo()) {
                operationType = OP_INVALID;
            }

            if (operationType == OP_STATION && angleEnabled && station.getStationNo() < route.getStations().size() - 1) {
                if (angleMismatch(station.getAngle(), snapshot.getCourse())) {
                    operationType = OP_INVALID;
                }
            }

            if (operationType == OP_STATION && station.getStationNo() < route.getStations().size() - 1) {
                if (altitudeMismatch(station.getAltitude(), snapshot.getAltitudeMeters())) {
                    operationType = OP_INVALID;
                }
            }

            if (operationType == OP_STATION) {
                clearDirectionVotes();
            }
        }

        if (operationType == OP_INVALID) {
            return AutoReportEvent.none();
        }
        if (operationType == OP_SWITCH_DIRECTION) {
            return AutoReportEvent.switchDirection(station, distance);
        }

        lastStationNo = station.getStationNo();
        stationInner = stationType == STATION_TYPE_ENTER;
        lastReminderNo = -1;
        return AutoReportEvent.station(station, stationType, distance);
    }

    private MatchResult findNearest(LegacyGpsRouteResource route, GpsFixSnapshot snapshot) {
        double currentLongitude = parseDouble(snapshot.getLongitudeDecimal(), 0d);
        double currentLatitude = parseDouble(snapshot.getLatitudeDecimal(), 0d);
        if (currentLongitude == 0d && currentLatitude == 0d) {
            return MatchResult.none();
        }

        LegacyGpsRouteResource.StationPoint nearestStation = null;
        double minDistance = Double.MAX_VALUE;
        for (LegacyGpsRouteResource.StationPoint station : route.getStations()) {
            if (station == null) {
                continue;
            }
            if (!hasCoordinate(station.getLongitudeRaw(), station.getLongitudeDecimal())
                    || !hasCoordinate(station.getLatitudeRaw(), station.getLatitudeDecimal())) {
                continue;
            }
            double distance = distanceMeters(
                    currentLongitude,
                    currentLatitude,
                    station.getLongitudeDecimal(),
                    station.getLatitudeDecimal()
            );
            if (nearestStation == null || distance < minDistance) {
                minDistance = distance;
                nearestStation = station;
            }
        }

        if (nearestStation == null) {
            return MatchResult.none();
        }

        LegacyGpsRouteResource.ReminderPoint nearestReminder = null;
        for (LegacyGpsRouteResource.ReminderPoint reminder : route.getReminders()) {
            if (reminder == null) {
                continue;
            }
            if (!hasCoordinate(reminder.getLongitudeRaw(), reminder.getLongitudeDecimal())
                    || !hasCoordinate(reminder.getLatitudeRaw(), reminder.getLatitudeDecimal())) {
                continue;
            }
            double distance = distanceMeters(
                    currentLongitude,
                    currentLatitude,
                    reminder.getLongitudeDecimal(),
                    reminder.getLatitudeDecimal()
            );
            if (distance < minDistance) {
                minDistance = distance;
                nearestReminder = reminder;
            }
        }

        if (nearestReminder != null) {
            return MatchResult.reminder(nearestReminder, minDistance);
        }
        return MatchResult.station(nearestStation, minDistance);
    }

    private boolean angleMismatch(String expectedAngle, String currentAngle) {
        if (expectedAngle == null || expectedAngle.trim().isEmpty()) {
            return false;
        }
        if ("N".equalsIgnoreCase(expectedAngle.trim())) {
            return false;
        }
        double expected = parseDouble(expectedAngle, 0d);
        double current = parseDouble(currentAngle, 0d);
        int diff = (int) Math.abs(expected - current);
        return diff > 60 && diff < 300;
    }

    /**
     * Mirrors the legacy implementation exactly. The original condition uses ||,
     * so any non-empty altitude pair becomes a mismatch.
     */
    private boolean altitudeMismatch(String stationAltitude, String currentAltitude) {
        if (stationAltitude == null || stationAltitude.trim().isEmpty()) {
            return false;
        }
        if (currentAltitude == null || currentAltitude.trim().isEmpty()) {
            return false;
        }
        double delta = parseDouble(currentAltitude, 0d) - parseDouble(stationAltitude, 0d);
        return delta >= -100d || delta <= 100d;
    }

    private void addDirectionVote(int stationNo) {
        String value = String.valueOf(stationNo);
        if (!directionSwitchVotes.contains(value)) {
            directionSwitchVotes.add(value);
        }
    }

    private void clearDirectionVotes() {
        directionSwitchVotes.clear();
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    private boolean hasCoordinate(String raw, double decimal) {
        return raw != null && !raw.trim().isEmpty() && decimal != 0d;
    }

    private double distanceMeters(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLat = radLat1 - radLat2;
        double deltaLng = Math.toRadians(lng1) - Math.toRadians(lng2);
        double value = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(deltaLat / 2), 2)
                        + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(deltaLng / 2), 2)
        ));
        return value * 6378.137d * 1000d;
    }

    public static final class AutoReportEvent {
        private final int operationType;
        private final int stationType;
        private final LegacyGpsRouteResource.StationPoint stationPoint;
        private final LegacyGpsRouteResource.ReminderPoint reminderPoint;
        private final double distanceMeters;

        private AutoReportEvent(
                int operationType,
                int stationType,
                LegacyGpsRouteResource.StationPoint stationPoint,
                LegacyGpsRouteResource.ReminderPoint reminderPoint,
                double distanceMeters
        ) {
            this.operationType = operationType;
            this.stationType = stationType;
            this.stationPoint = stationPoint;
            this.reminderPoint = reminderPoint;
            this.distanceMeters = distanceMeters;
        }

        public static AutoReportEvent none() {
            return new AutoReportEvent(OP_INVALID, STATION_TYPE_ENTER, null, null, 0d);
        }

        public static AutoReportEvent station(
                LegacyGpsRouteResource.StationPoint stationPoint,
                int stationType,
                double distanceMeters
        ) {
            return new AutoReportEvent(OP_STATION, stationType, stationPoint, null, distanceMeters);
        }

        public static AutoReportEvent reminder(
                LegacyGpsRouteResource.ReminderPoint reminderPoint,
                double distanceMeters
        ) {
            return new AutoReportEvent(OP_REMINDER, STATION_TYPE_ENTER, null, reminderPoint, distanceMeters);
        }

        public static AutoReportEvent switchDirection(
                LegacyGpsRouteResource.StationPoint stationPoint,
                double distanceMeters
        ) {
            return new AutoReportEvent(OP_SWITCH_DIRECTION, STATION_TYPE_ENTER, stationPoint, null, distanceMeters);
        }

        public int getOperationType() {
            return operationType;
        }

        public int getStationType() {
            return stationType;
        }

        public LegacyGpsRouteResource.StationPoint getStationPoint() {
            return stationPoint;
        }

        public LegacyGpsRouteResource.ReminderPoint getReminderPoint() {
            return reminderPoint;
        }

        public double getDistanceMeters() {
            return distanceMeters;
        }

        public boolean isNone() {
            return operationType == OP_INVALID;
        }

        public String describe() {
            if (operationType == OP_STATION && stationPoint != null) {
                return "station "
                        + (stationType == STATION_TYPE_ENTER ? "enter" : "leave")
                        + " no=" + stationPoint.getStationNo()
                        + " name=" + stationPoint.getStationName()
                        + " distance=" + String.format(Locale.US, "%.1f", distanceMeters);
            }
            if (operationType == OP_REMINDER && reminderPoint != null) {
                return "reminder no=" + reminderPoint.getReminderNo()
                        + " name=" + reminderPoint.getReminderName()
                        + " distance=" + String.format(Locale.US, "%.1f", distanceMeters);
            }
            if (operationType == OP_SWITCH_DIRECTION && stationPoint != null) {
                return "switch-direction near no=" + stationPoint.getStationNo()
                        + " name=" + stationPoint.getStationName();
            }
            return "none";
        }
    }

    private static final class MatchResult {
        private final int operationType;
        private final LegacyGpsRouteResource.StationPoint stationPoint;
        private final LegacyGpsRouteResource.ReminderPoint reminderPoint;
        private final double distanceMeters;

        private MatchResult(
                int operationType,
                LegacyGpsRouteResource.StationPoint stationPoint,
                LegacyGpsRouteResource.ReminderPoint reminderPoint,
                double distanceMeters
        ) {
            this.operationType = operationType;
            this.stationPoint = stationPoint;
            this.reminderPoint = reminderPoint;
            this.distanceMeters = distanceMeters;
        }

        private static MatchResult none() {
            return new MatchResult(OP_INVALID, null, null, 0d);
        }

        private static MatchResult station(LegacyGpsRouteResource.StationPoint stationPoint, double distanceMeters) {
            return new MatchResult(OP_STATION, stationPoint, null, distanceMeters);
        }

        private static MatchResult reminder(LegacyGpsRouteResource.ReminderPoint reminderPoint, double distanceMeters) {
            return new MatchResult(OP_REMINDER, null, reminderPoint, distanceMeters);
        }
    }
}
