package com.lhxy.istationdevice.android11.domain.gps;

import android.content.Context;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
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
 * 旧版 GPS 线路资源目录。
 * <p>
 * 负责加载旧 CSV 线路文件，并把旧坐标格式转换成十进制度数。
 * <p>
 * 查找关键字：CSV 线路加载、坐标转换、lineInfo、提醒点解析。
 */
public final class LegacyGpsRouteCatalog {
    private static final Charset LEGACY_CSV_CHARSET = Charset.forName("GB18030");

    private final Map<String, LegacyGpsRouteResource> cache = new LinkedHashMap<>();

    /**
     * 加载指定线路和方向的资源。
     */
    public synchronized LegacyGpsRouteResource load(
            Context context,
            String preferredLineName,
            String preferredDirectionText
    ) {
        File busDir = resolveBusDir(context);
        if (busDir == null || !busDir.exists()) {
            logResolution("线路资源目录不存在 line=" + preferredLineName);
            return null;
        }
        LineInfo lineInfo = resolveLineInfo(busDir, preferredLineName);
        if (lineInfo == null) {
            logResolution("lineInfo.csv 未匹配 line=" + preferredLineName
                    + " / file=" + new File(busDir, "lineInfo.csv").getAbsolutePath());
            return null;
        }
        String directionText = normalizeDirectionText(preferredDirectionText);
        String cacheKey = normalize(lineInfo.lineName) + "|" + directionText;
        LegacyGpsRouteResource cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        LegacyGpsRouteResource loaded = loadRoute(
                busDir, lineInfo.lineName, lineInfo.lineNumber, lineInfo.attribute, directionText);
        if (loaded != null) {
            cache.put(cacheKey, loaded);
            logResolution("线路资源已加载 line=" + lineInfo.lineName
                    + " / lineNumber=" + lineInfo.lineNumber
                    + " / direction=" + directionText
                    + " / stations=" + loaded.getStations().size());
        } else {
            logResolution("线路站点文件加载失败 line=" + lineInfo.lineName
                    + " / lineNumber=" + lineInfo.lineNumber
                    + " / direction=" + directionText
                    + " / busDir=" + busDir.getAbsolutePath());
        }
        return loaded;
    }

    /**
     * Reads the platform line serial independently of station CSV parsing.
     * This prevents a valid lineInfo.csv serial from being replaced by a
     * numeric suffix parsed from a display name such as L1.
     */
    public synchronized int resolveLineNumber(Context context, String preferredLineName) {
        File busDir = resolveBusDir(context);
        if (busDir == null || !busDir.exists()) {
            return 0;
        }
        LineInfo lineInfo = resolveLineInfo(busDir, preferredLineName);
        int lineNumber = lineInfo == null ? 0 : lineInfo.lineNumber;
        logResolution("线路号解析 line=" + preferredLineName
                + " / lineNumber=" + lineNumber
                + " / lineInfo=" + new File(busDir, "lineInfo.csv").getAbsolutePath());
        return lineNumber;
    }

    /** 严格按旧 lineInfo 的“Line serial”匹配平台下发线路号。 */
    public synchronized LegacyGpsRouteResource loadByLineNumber(
            Context context,
            String lineNumber,
            String preferredDirectionText
    ) {
        File busDir = resolveBusDir(context);
        if (busDir == null || !busDir.exists()) {
            return null;
        }
        LineInfo lineInfo = resolveLineInfoByNumber(busDir, lineNumber);
        if (lineInfo == null) {
            return null;
        }
        String directionText = normalizeDirectionText(preferredDirectionText);
        String cacheKey = normalize(lineInfo.lineName) + "|" + directionText;
        LegacyGpsRouteResource cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        LegacyGpsRouteResource loaded = loadRoute(
                busDir, lineInfo.lineName, lineInfo.lineNumber, lineInfo.attribute, directionText);
        if (loaded != null) {
            cache.put(cacheKey, loaded);
        }
        return loaded;
    }

    /**
     * 清空已解析线路缓存。
     */
    public synchronized void clearCache() {
        cache.clear();
    }

    /**
     * 读取某条线路的站点和提醒点 CSV。
     */
    private LegacyGpsRouteResource loadRoute(File busDir, String lineName, int attribute, String directionText) {
        return loadRoute(busDir, lineName, 0, attribute, directionText);
    }

    private LegacyGpsRouteResource loadRoute(
            File busDir,
            String lineName,
            int lineNumber,
            int attribute,
            String directionText
    ) {
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
            String stationSound = cell(row, 1);
            String stationName = cell(row, 2);
            if (stationName.isEmpty()) {
                stationName = stationSound;
            }
            Coordinate longitude = parseCoordinate(cell(row, 3), true);
            Coordinate latitude = parseCoordinate(cell(row, 4), false);
            // The field-exported legacy CSV has 18 columns and puts UID at index 6.
            // Some later exports add Altitude, making the same layout 19 columns with
            // UID at index 7. The older Chinese 16-column layout has no UID column.
            boolean legacyUidLayout = row.size() >= 18;
            boolean legacyDetailedLayout = row.size() >= 19;
            String altitude = legacyDetailedLayout ? cell(row, 6) : "";
            String siteCode = legacyUidLayout ? cell(row, legacyDetailedLayout ? 7 : 6) : "";
            int adOffset = legacyDetailedLayout ? 2 : legacyUidLayout ? 1 : 0;
            int speedLimitIndex = legacyDetailedLayout ? 14 : legacyUidLayout ? 13 : 12;
            int mileageIndex = legacyDetailedLayout ? 16 : legacyUidLayout ? 15 : 13;
            int majorStationIndex = legacyDetailedLayout ? 17 : legacyUidLayout ? 16 : 14;
            int voiceNotIndex = legacyDetailedLayout ? 18 : legacyUidLayout ? 17 : 15;
            stations.add(new LegacyGpsRouteResource.StationPoint(
                    stationNo,
                    stationSound,
                    stationName,
                    longitude.raw,
                    latitude.raw,
                    longitude.decimal,
                    latitude.decimal,
                    cell(row, 5),
                    altitude,
                    siteCode,
                    cell(row, 6 + adOffset),
                    cell(row, 7 + adOffset),
                    cell(row, 8 + adOffset),
                    cell(row, 9 + adOffset),
                    cell(row, 10 + adOffset),
                    cell(row, 11 + adOffset),
                    cell(row, speedLimitIndex),
                    parseDouble(cell(row, mileageIndex), 0d),
                    cell(row, majorStationIndex),
                    cell(row, voiceNotIndex)
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
                    "",
                    "",
                    parseDouble(cell(row, 4), 0d),
                    hasExtendedReminderColumns(row) ? cell(row, 5) : "",
                    hasExtendedReminderColumns(row) ? cell(row, 6) : "",
                    hasExtendedReminderColumns(row) ? cell(row, 7) : "",
                    hasExtendedReminderColumns(row) ? cell(row, 8) : "",
                    hasExtendedReminderColumns(row) ? cell(row, 9) : "",
                    hasExtendedReminderColumns(row) ? cell(row, 10) : "",
                    hasExtendedReminderColumns(row) ? cell(row, 11) : "",
                    hasExtendedReminderColumns(row) ? cell(row, 12) : cell(row, 5)
            ));
        }

        return new LegacyGpsRouteResource(
                lineName, lineNumber, attribute, directionText, stations, reminders);
    }

    /**
     * 定位托管后的 Bus 资源目录。
     */
    private File resolveBusDir(Context context) {
        if (context == null) {
            return null;
        }
        StationResourceArchiveUseCase stationResourceArchiveUseCase = new StationResourceArchiveUseCase();
        File sourceRoot = stationResourceArchiveUseCase.resolveManagedSourceRoot(context.getApplicationContext());
        File busDir = new File(sourceRoot, "Bus");
        return busDir.exists() && busDir.isDirectory() ? busDir : null;
    }

    private void logResolution(String message) {
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, "LegacyGpsRouteCatalog", message, "gps-route-resolution");
    }

    /**
     * 从 lineInfo.csv 里找出首选线路的基础信息。
     */
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
            LineInfo info = new LineInfo(
                    lineName,
                    parseInt(cell(row, 4), 0),
                    parseInt(cell(row, 3), LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN));
            if (first == null) {
                first = info;
            }
            if (normalize(lineName).equals(normalize(preferredLineName))) {
                return info;
            }
        }
        return first;
    }

    private LineInfo resolveLineInfoByNumber(File busDir, String preferredLineNumber) {
        File lineInfoFile = new File(busDir, "lineInfo.csv");
        List<List<String>> rows = readCsvRows(lineInfoFile);
        if (rows.size() <= 1) {
            return null;
        }
        String expected = normalize(preferredLineNumber);
        for (int index = 1; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            if (row.size() < 2) {
                continue;
            }
            String lineName = cell(row, 1);
            String lineNumber = cell(row, 4);
            if (lineName.isEmpty() || !normalize(lineNumber).equals(expected)) {
                continue;
            }
            return new LineInfo(
                    lineName,
                    parseInt(cell(row, 4), 0),
                    parseInt(cell(row, 3), LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN));
        }
        return null;
    }

    /**
     * 读取 CSV 所有行，并处理 BOM 与空行。
     */
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
        } catch (Exception e) {
            // 真实读文件/解码异常(非空文件)被吞成空列表 → 线路解析不出来却查不到原因。
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, "LegacyGpsRouteCatalog",
                    "线路CSV读取失败 file=" + csvFile.getAbsolutePath() + " / err=" + e, "gps-route-csv");
            return Collections.emptyList();
        }
        return rows;
    }

    /**
     * 把一行 CSV 安全拆成列。
     */
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

    /**
     * 解析旧坐标字段，必要时把 NMEA 风格值转成十进制度。
     */
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

    private boolean hasExtendedReminderColumns(List<String> row) {
        return row != null && row.size() >= 13;
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
        private final int lineNumber;
        private final int attribute;

        private LineInfo(String lineName, int lineNumber, int attribute) {
            this.lineName = lineName;
            this.lineNumber = lineNumber;
            this.attribute = attribute;
        }
    }
}
