package com.lhxy.istationdevice.android11.domain.gps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parsed legacy GPS route resource for one line and one direction.
 */
public final class LegacyGpsRouteResource {
    public static final int ATTRIBUTE_UP_DOWN = 1;
    public static final int ATTRIBUTE_LOOP = 2;
    public static final int ATTRIBUTE_ANTI_REVERSE = 3;

    private final String lineName;
    private final int lineAttribute;
    private final String directionText;
    private final List<StationPoint> stations;
    private final List<ReminderPoint> reminders;

    public LegacyGpsRouteResource(
            String lineName,
            int lineAttribute,
            String directionText,
            List<StationPoint> stations,
            List<ReminderPoint> reminders
    ) {
        this.lineName = lineName == null ? "-" : lineName.trim();
        this.lineAttribute = normalizeLineAttribute(lineAttribute);
        this.directionText = directionText == null ? "上行" : directionText.trim();
        this.stations = Collections.unmodifiableList(new ArrayList<>(stations == null ? Collections.emptyList() : stations));
        this.reminders = Collections.unmodifiableList(new ArrayList<>(reminders == null ? Collections.emptyList() : reminders));
    }

    public String getLineName() {
        return lineName;
    }

    public int getLineAttribute() {
        return lineAttribute;
    }

    public String getDirectionText() {
        return directionText;
    }

    public List<StationPoint> getStations() {
        return stations;
    }

    public List<ReminderPoint> getReminders() {
        return reminders;
    }

    public List<String> stationNames() {
        List<String> names = new ArrayList<>();
        for (StationPoint station : stations) {
            names.add(station.getStationName());
        }
        return names;
    }

    public StationPoint firstStation() {
        return stations.isEmpty() ? null : stations.get(0);
    }

    public StationPoint lastStation() {
        return stations.isEmpty() ? null : stations.get(stations.size() - 1);
    }

    public String getAttributeLabel() {
        if (lineAttribute == ATTRIBUTE_LOOP) {
            return "环线";
        }
        if (lineAttribute == ATTRIBUTE_ANTI_REVERSE) {
            return "防反";
        }
        return "对开";
    }

    private static int normalizeLineAttribute(int value) {
        if (value == ATTRIBUTE_LOOP || value == ATTRIBUTE_ANTI_REVERSE) {
            return value;
        }
        return ATTRIBUTE_UP_DOWN;
    }

    public static final class StationPoint {
        private final int stationNo;
        private final String stationName;
        private final String longitudeRaw;
        private final String latitudeRaw;
        private final double longitudeDecimal;
        private final double latitudeDecimal;
        private final String angle;
        private final String altitude;
        private final double mileage;
        private final String majorStation;
        private final String voiceNot;

        public StationPoint(
                int stationNo,
                String stationName,
                String longitudeRaw,
                String latitudeRaw,
                double longitudeDecimal,
                double latitudeDecimal,
                String angle,
                String altitude,
                double mileage,
                String majorStation,
                String voiceNot
        ) {
            this.stationNo = stationNo;
            this.stationName = stationName == null ? "-" : stationName.trim();
            this.longitudeRaw = longitudeRaw == null ? "" : longitudeRaw.trim();
            this.latitudeRaw = latitudeRaw == null ? "" : latitudeRaw.trim();
            this.longitudeDecimal = longitudeDecimal;
            this.latitudeDecimal = latitudeDecimal;
            this.angle = angle == null ? "" : angle.trim();
            this.altitude = altitude == null ? "" : altitude.trim();
            this.mileage = mileage;
            this.majorStation = majorStation == null ? "" : majorStation.trim();
            this.voiceNot = voiceNot == null ? "" : voiceNot.trim();
        }

        public int getStationNo() {
            return stationNo;
        }

        public String getStationName() {
            return stationName;
        }

        public String getLongitudeRaw() {
            return longitudeRaw;
        }

        public String getLatitudeRaw() {
            return latitudeRaw;
        }

        public double getLongitudeDecimal() {
            return longitudeDecimal;
        }

        public double getLatitudeDecimal() {
            return latitudeDecimal;
        }

        public String getAngle() {
            return angle;
        }

        public String getAltitude() {
            return altitude;
        }

        public double getMileage() {
            return mileage;
        }

        public String getMajorStation() {
            return majorStation;
        }

        public String getVoiceNot() {
            return voiceNot;
        }
    }

    public static final class ReminderPoint {
        private final int reminderNo;
        private final String reminderName;
        private final String longitudeRaw;
        private final String latitudeRaw;
        private final double longitudeDecimal;
        private final double latitudeDecimal;
        private final String angle;
        private final String altitude;
        private final double mileage;
        private final String voiceNot;

        public ReminderPoint(
                int reminderNo,
                String reminderName,
                String longitudeRaw,
                String latitudeRaw,
                double longitudeDecimal,
                double latitudeDecimal,
                String angle,
                String altitude,
                double mileage,
                String voiceNot
        ) {
            this.reminderNo = reminderNo;
            this.reminderName = reminderName == null ? "-" : reminderName.trim();
            this.longitudeRaw = longitudeRaw == null ? "" : longitudeRaw.trim();
            this.latitudeRaw = latitudeRaw == null ? "" : latitudeRaw.trim();
            this.longitudeDecimal = longitudeDecimal;
            this.latitudeDecimal = latitudeDecimal;
            this.angle = angle == null ? "" : angle.trim();
            this.altitude = altitude == null ? "" : altitude.trim();
            this.mileage = mileage;
            this.voiceNot = voiceNot == null ? "" : voiceNot.trim();
        }

        public int getReminderNo() {
            return reminderNo;
        }

        public String getReminderName() {
            return reminderName;
        }

        public String getLongitudeRaw() {
            return longitudeRaw;
        }

        public String getLatitudeRaw() {
            return latitudeRaw;
        }

        public double getLongitudeDecimal() {
            return longitudeDecimal;
        }

        public double getLatitudeDecimal() {
            return latitudeDecimal;
        }

        public String getAngle() {
            return angle;
        }

        public String getAltitude() {
            return altitude;
        }

        public double getMileage() {
            return mileage;
        }

        public String getVoiceNot() {
            return voiceNot;
        }
    }
}
