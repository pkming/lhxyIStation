package com.lhxy.istationdevice.android11.domain.gps;

import android.content.Context;

import com.lhxy.istationdevice.android11.domain.file.StationResourceArchiveUseCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Loads legacy line CSV resources and converts NMEA-style coordinates to decimal degrees.
 */
public final class LegacyGpsRouteCatalog {
    private static final Charset LEGACY_CSV_CHARSET = Charset.forName("GB18030");

    private final Map<String, LegacyGpsRouteResource> cache = new LinkedHashMap<>();

    public synchronized LegacyGpsRouteResource load(
            Context context,
            String preferredLineName,
            String preferredDirectionText
    ) {
        File busDir = resolveBusDir(context);
        if (busDir == null || !busDir.exists()) {
            return null;
        }
        LineInfo lineInfo = resolveLineInfo(busDir, preferredLineName);
        if (lineInfo == null) {
            return null;
        }
        String directionText = normalizeDirectionText(preferredDirectionText);
        String cacheKey = normalize(lineInfo.lineName) + "|" + directionText;
        LegacyGpsRouteResource cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        LegacyGpsRouteResource loaded = loadRoute(busDir, lineInfo.lineName, lineInfo.attribute, directionText);
        if (loaded != null) {
            cache.put(cacheKey, loaded);
        }
        return loaded;
    }

    public synchronized void clearCache() {
        cache.clear();
    }

    private LegacyGpsRouteResource loadRoute(File busDir, String lineName, int attribute, String directionText) {
        File lineDir = new File(busDir, lineName);
        String suffix = directionText.contains("下") ? "X" : "S";
        File stationFile = new File(lineDir, lineName + suffix + ".csv");
        File reminderFile = new File(lineDir, lineName + suffix + "Remind.csv");
        List<List<String>> stationRows = readCsvRows(stationFile);
        if (stationRows.size() <= 1) {
            return null;
        }

        List<LegacyGpsRouteResource.StationPoint> stations = new ArrayList<>();
        for (int index = 1; index < stationRows.size(); index++) {
            List<String> row = stationRows.get(index);
            if (row.isEmpty()) {
                continue;
            }
            int stationNo = parseInt(cell(row, 0), stations.size());
            String stationName = cell(row, 2);
            if (stationName.isEmpty()) {
                stationName = cell(row, 1);
            }
            Coordinate longitude = parseCoordinate(cell(row, 3), true);
            Coordinate latitude = parseCoordinate(cell(row, 4), false);
            stations.add(new LegacyGpsRouteResource.StationPoint(
                    stationNo,
                    stationName,
                    longitude.raw,
                    latitude.raw,
                    longitude.decimal,
                    latitude.decimal,
                    cell(row, 5),
                    "",
                    parseDouble(cell(row, 14), 0d),
                    cell(row, 15),
                    cell(row, 16)
            ));
        }

        List<LegacyGpsRouteResource.ReminderPoint> reminders = new ArrayList<>();
        List<List<String>> reminderRows = readCsvRows(reminderFile);
        for (int index = 1; index < reminderRows.size(); index++) {
            List<String> row = reminderRows.get(index);
            if (row.isEmpty()) {
                continue;
            }
            int reminderNo = parseInt(cell(row, 0), reminders.size());
            String reminderName = cell(row, 1);
            Coordinate longitude = parseCoordinate(cell(row, 2), true);
            Coordinate latitude = parseCoordinate(cell(row, 3), false);
            reminders.add(new LegacyGpsRouteResource.ReminderPoint(
                    reminderNo,
                    reminderName,
                    longitude.raw,
                    latitude.raw,
                    longitude.decimal,
                    latitude.decimal,
                    cell(row, 4),
                    cell(row, 5),
                    parseDouble(cell(row, 6), 0d),
                    cell(row, 7)
            ));
        }

        return new LegacyGpsRouteResource(lineName, attribute, directionText, stations, reminders);
    }

    private File resolveBusDir(Context context) {
        if (context == null) {
            return null;
        }
        File managedRoot = new StationResourceArchiveUseCase().resolveManagedResourceRoot(context.getApplicationContext());
        File busDir = new File(managedRoot, "SourceFile/Bus");
        return busDir.exists() && busDir.isDirectory() ? busDir : null;
    }

    private LineInfo resolveLineInfo(File busDir, String preferredLineName) {
        File lineInfoFile = new File(busDir, "lineInfo.csv");
        List<List<String>> rows = readCsvRows(lineInfoFile);
        if (rows.size() <= 1) {
            return null;
        }
        LineInfo first = null;
        for (int index = 1; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            if (row.size() < 2) {
                continue;
            }
            String lineName = cell(row, 1);
            if (lineName.isEmpty()) {
                continue;
            }
            LineInfo info = new LineInfo(lineName, parseInt(cell(row, 3), LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN));
            if (first == null) {
                first = info;
            }
            if (normalize(lineName).equals(normalize(preferredLineName))) {
                return info;
            }
        }
        return first;
    }

    private List<List<String>> readCsvRows(File csvFile) {
        if (csvFile == null || !csvFile.exists() || !csvFile.isFile()) {
            return Collections.emptyList();
        }
        List<List<String>> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), LEGACY_CSV_CHARSET))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = stripBom(line);
                if (normalized.trim().isEmpty()) {
                    continue;
                }
                rows.add(parseCsvRow(normalized));
            }
        } catch (Exception ignore) {
            return Collections.emptyList();
        }
        return rows;
    }

    private List<String> parseCsvRow(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int index = 0; index < line.length(); index++) {
            char ch = line.charAt(index);
            if (ch == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
                continue;
            }
            if (ch == ',' && !quoted) {
                columns.add(cleanCell(current.toString()));
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        columns.add(cleanCell(current.toString()));
        return columns;
    }

    private Coordinate parseCoordinate(String rawValue, boolean longitude) {
        String raw = rawValue == null ? "" : rawValue.trim();
        if (raw.isEmpty()) {
            return new Coordinate(raw, 0d);
        }
        double numeric = parseDouble(raw, 0d);
        if (numeric == 0d) {
            return new Coordinate(raw, 0d);
        }
        double decimal;
        if (Math.abs(numeric) > (longitude ? 180d : 90d)) {
            decimal = nmeaToDecimal(numeric);
        } else {
            decimal = numeric;
        }
        return new Coordinate(raw, decimal);
    }

    private double nmeaToDecimal(double rawValue) {
        double scaled = rawValue / 100d;
        String text = String.format(Locale.US, "%.10f", scaled);
        int dot = text.indexOf('.');
        if (dot < 0 || dot >= text.length() - 1) {
            return scaled;
        }
        String degreePart = text.substring(0, dot);
        String minuteDigits = text.substring(dot + 1);
        if (minuteDigits.length() < 2) {
            return scaled;
        }
        String minuteText = minuteDigits.substring(0, 2) + "." + minuteDigits.substring(2);
        return parseDouble(degreePart, 0d) + (parseDouble(minuteText, 0d) / 60d);
    }

    private String normalizeDirectionText(String directionText) {
        if (directionText != null && directionText.contains("下")) {
            return "下行";
        }
        return "上行";
    }

    private String cell(List<String> row, int index) {
        if (row == null || index < 0 || index >= row.size()) {
            return "";
        }
        return cleanCell(row.get(index));
    }

    private String cleanCell(String value) {
        if (value == null) {
            return "";
        }
        String normalized = stripBom(value).trim();
        if (normalized.startsWith("\"") && normalized.endsWith("\"") && normalized.length() > 1) {
            normalized = normalized.substring(1, normalized.length() - 1).trim();
        }
        return normalized;
    }

    private String stripBom(String value) {
        return value != null && value.startsWith("\uFEFF") ? value.substring(1) : value == null ? "" : value;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim().replace(" ", "");
        if (normalized.endsWith("路")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized.toUpperCase(Locale.ROOT);
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    private static final class Coordinate {
        private final String raw;
        private final double decimal;

        private Coordinate(String raw, double decimal) {
            this.raw = raw == null ? "" : raw.trim();
            this.decimal = decimal;
        }
    }

    private static final class LineInfo {
        private final String lineName;
        private final int attribute;

        private LineInfo(String lineName, int attribute) {
            this.lineName = lineName;
            this.attribute = attribute;
        }
    }
}
