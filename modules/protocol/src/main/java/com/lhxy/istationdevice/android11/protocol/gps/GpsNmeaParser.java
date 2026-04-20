package com.lhxy.istationdevice.android11.protocol.gps;

import java.util.Locale;

/**
 * GPS NMEA 纯解析器
 * <p>
 * 先兼容旧项目里已经在用的 RMC / GGA 两类语句。
 */
public final class GpsNmeaParser {
    /**
     * 解析单条 NMEA 语句。
     * <p>
     * 如果当前语句字段不全，会尽量保留上一条快照里已有的值。
     */
    public GpsFixSnapshot parseSentence(String sentence, GpsFixSnapshot previousSnapshot) {
        if (sentence == null) {
            return previousSnapshot;
        }
        String trimmed = sentence.trim();
        if (trimmed.isEmpty()) {
            return previousSnapshot;
        }
        if (trimmed.startsWith("$GPRMC") || trimmed.startsWith("$GNRMC") || trimmed.startsWith("$BDRMC")) {
            return parseRmc(trimmed, previousSnapshot);
        }
        if (trimmed.startsWith("$GNGGA") || trimmed.startsWith("$BDGGA")) {
            return parseGga(trimmed, previousSnapshot);
        }
        return previousSnapshot;
    }

    /**
     * 解析多行 NMEA 文本。
     */
    public GpsFixSnapshot parseBlock(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return null;
        }
        GpsFixSnapshot snapshot = null;
        String[] lines = rawText.replace("\r", "\n").split("\n");
        for (String line : lines) {
            snapshot = parseSentence(line, snapshot);
        }
        return snapshot;
    }

    private GpsFixSnapshot parseRmc(String sentence, GpsFixSnapshot previousSnapshot) {
        String[] fields = sentence.split(",", -1);
        if (fields.length < 10) {
            return previousSnapshot;
        }

        String latitudeRaw = safeField(fields, 3);
        String latitudeHemisphere = safeField(fields, 4);
        String longitudeRaw = safeField(fields, 5);
        String longitudeHemisphere = safeField(fields, 6);

        return new GpsFixSnapshot(
                sentence,
                "A".equalsIgnoreCase(safeField(fields, 2)),
                safeField(fields, 1),
                safeField(fields, 9),
                latitudeRaw,
                latitudeHemisphere,
                convertToDecimal(latitudeRaw, latitudeHemisphere, 2),
                longitudeRaw,
                longitudeHemisphere,
                convertToDecimal(longitudeRaw, longitudeHemisphere, 3),
                safeField(fields, 7),
                safeField(fields, 8),
                previousSnapshot == null ? "0" : previousSnapshot.getAltitudeMeters(),
                previousSnapshot == null ? 0 : previousSnapshot.getUsedSatellites()
        );
    }

    private GpsFixSnapshot parseGga(String sentence, GpsFixSnapshot previousSnapshot) {
        String[] fields = sentence.split(",", -1);
        if (fields.length < 10) {
            return previousSnapshot;
        }

        String latitudeRaw = safeField(fields, 2);
        String latitudeHemisphere = safeField(fields, 3);
        String longitudeRaw = safeField(fields, 4);
        String longitudeHemisphere = safeField(fields, 5);

        int usedSatellites = parseInt(safeField(fields, 7));
        String altitudeMeters = safeField(fields, 9);

        return new GpsFixSnapshot(
                sentence,
                previousSnapshot != null && previousSnapshot.isValid(),
                previousSnapshot == null ? safeField(fields, 1) : valueOrPrevious(safeField(fields, 1), previousSnapshot.getTime()),
                previousSnapshot == null ? "" : previousSnapshot.getDate(),
                valueOrPrevious(latitudeRaw, previousSnapshot == null ? "" : previousSnapshot.getLatitudeRaw()),
                valueOrPrevious(latitudeHemisphere, previousSnapshot == null ? "" : previousSnapshot.getLatitudeHemisphere()),
                valueOrPrevious(convertToDecimal(latitudeRaw, latitudeHemisphere, 2), previousSnapshot == null ? "" : previousSnapshot.getLatitudeDecimal()),
                valueOrPrevious(longitudeRaw, previousSnapshot == null ? "" : previousSnapshot.getLongitudeRaw()),
                valueOrPrevious(longitudeHemisphere, previousSnapshot == null ? "" : previousSnapshot.getLongitudeHemisphere()),
                valueOrPrevious(convertToDecimal(longitudeRaw, longitudeHemisphere, 3), previousSnapshot == null ? "" : previousSnapshot.getLongitudeDecimal()),
                previousSnapshot == null ? "" : previousSnapshot.getSpeedKnots(),
                previousSnapshot == null ? "" : previousSnapshot.getCourse(),
                altitudeMeters.isEmpty() ? previousSnapshot == null ? "0" : previousSnapshot.getAltitudeMeters() : altitudeMeters,
                usedSatellites <= 0 ? previousSnapshot == null ? 0 : previousSnapshot.getUsedSatellites() : usedSatellites
        );
    }

    private String convertToDecimal(String rawCoordinate, String hemisphere, int degreeDigits) {
        if (rawCoordinate == null || rawCoordinate.trim().isEmpty()) {
            return "";
        }
        String coordinate = rawCoordinate.trim();
        if (coordinate.length() <= degreeDigits) {
            return "";
        }
        try {
            double degrees = Double.parseDouble(coordinate.substring(0, degreeDigits));
            double minutes = Double.parseDouble(coordinate.substring(degreeDigits));
            double decimal = degrees + minutes / 60d;
            if ("S".equalsIgnoreCase(hemisphere) || "W".equalsIgnoreCase(hemisphere)) {
                decimal = -decimal;
            }
            return String.format(Locale.US, "%.6f", decimal);
        } catch (Exception ignored) {
            return "";
        }
    }

    private int parseInt(String text) {
        try {
            return Integer.parseInt(text == null ? "" : text.trim());
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String safeField(String[] fields, int index) {
        if (fields == null || index < 0 || index >= fields.length) {
            return "";
        }
        String value = fields[index];
        if (value == null) {
            return "";
        }
        int checksumIndex = value.indexOf('*');
        return checksumIndex >= 0 ? value.substring(0, checksumIndex).trim() : value.trim();
    }

    private String valueOrPrevious(String value, String previous) {
        return value == null || value.trim().isEmpty() ? (previous == null ? "" : previous) : value.trim();
    }
}
