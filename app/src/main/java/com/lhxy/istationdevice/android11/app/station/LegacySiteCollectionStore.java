package com.lhxy.istationdevice.android11.app.station;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.util.Locale;

final class LegacySiteCollectionStore {
    private static final String PREFS_NAME = "legacy_site_collection";
    private static final String VALUE_SEPARATOR = "\u001F";

    private LegacySiteCollectionStore() {
    }

    static void save(
            @NonNull Context context,
            @NonNull String section,
            @NonNull String lineName,
            @NonNull String direction,
            @NonNull String itemName,
            int itemIndex,
            @NonNull GpsFixSnapshot snapshot
    ) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(buildKey(section, lineName, direction, itemName, itemIndex), encode(snapshot))
                .apply();
    }

    @Nullable
    static LearnedValue load(
            @NonNull Context context,
            @NonNull String section,
            @NonNull String lineName,
            @NonNull String direction,
            @NonNull String itemName,
            int itemIndex
    ) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String raw = prefs.getString(buildKey(section, lineName, direction, itemName, itemIndex), null);
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        String[] parts = raw.split(VALUE_SEPARATOR, -1);
        if (parts.length < 5) {
            return null;
        }
        return new LearnedValue(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    @NonNull
    private static String buildKey(
            @NonNull String section,
            @NonNull String lineName,
            @NonNull String direction,
            @NonNull String itemName,
            int itemIndex
    ) {
        return section + "|"
                + normalize(lineName) + "|"
                + normalize(direction) + "|"
                + itemIndex + "|"
                + normalize(itemName);
    }

    @NonNull
    private static String encode(@NonNull GpsFixSnapshot snapshot) {
        return valueOrDash(snapshot.getLongitudeDecimal()) + VALUE_SEPARATOR
                + valueOrDash(snapshot.getLatitudeDecimal()) + VALUE_SEPARATOR
                + speedKmh(snapshot) + VALUE_SEPARATOR
                + valueOrDash(snapshot.getCourse()) + VALUE_SEPARATOR
                + valueOrDash(snapshot.getAltitudeMeters());
    }

    @NonNull
    private static String normalize(@Nullable String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replace(" ", "").toUpperCase(Locale.ROOT);
    }

    @NonNull
    private static String valueOrDash(@Nullable String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    @NonNull
    private static String speedKmh(@NonNull GpsFixSnapshot snapshot) {
        String knots = snapshot.getSpeedKnots();
        if (knots == null || knots.trim().isEmpty()) {
            return "-";
        }
        try {
            return String.format(Locale.US, "%.2f", Double.parseDouble(knots.trim()) * 1.852d);
        } catch (Exception ignore) {
            return "-";
        }
    }

    static final class LearnedValue {
        private final String longitude;
        private final String latitude;
        private final String speedKmh;
        private final String angle;
        private final String altitude;

        LearnedValue(String longitude, String latitude, String speedKmh, String angle, String altitude) {
            this.longitude = valueOrDash(longitude);
            this.latitude = valueOrDash(latitude);
            this.speedKmh = valueOrDash(speedKmh);
            this.angle = valueOrDash(angle);
            this.altitude = valueOrDash(altitude);
        }

        @NonNull
        String getLongitude() {
            return longitude;
        }

        @NonNull
        String getLatitude() {
            return latitude;
        }

        @NonNull
        String getSpeedKmh() {
            return speedKmh;
        }

        @NonNull
        String getAngle() {
            return angle;
        }

        @NonNull
        String getAltitude() {
            return altitude;
        }
    }
}