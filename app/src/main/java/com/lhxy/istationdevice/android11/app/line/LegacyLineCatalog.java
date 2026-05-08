package com.lhxy.istationdevice.android11.app.line;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lhxy.istationdevice.android11.domain.file.StationResourceArchiveUseCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 旧终端线路目录。
 * <p>
 * 优先读取导入后的 SourceFile/Bus 真实线路资源，
 * 让首页、线路选择和站点学习尽量对齐旧资源包。
 * 如果当前还没有导入成功的资源，再回退到稳定样例目录。
 */
public final class LegacyLineCatalog {
    private static final Charset LEGACY_CSV_CHARSET = Charset.forName("GB18030");
    private static final List<String> SAMPLE_ATTRIBUTES = Collections.unmodifiableList(
            Arrays.asList("到站提醒", "转弯提醒", "限速提醒", "进站提醒")
    );
        private static final List<String> SAMPLE_SPEED_LIMITS = Collections.unmodifiableList(
            Arrays.asList("35", "25", "25", "30")
        );
    private static final List<LineProfile> SAMPLE_PROFILES = Collections.unmodifiableList(Arrays.asList(
            new LineProfile(
                    "101路",
                    "火车站 → 科技园",
                    "对开",
                    Arrays.asList("火车站", "市政府", "人民广场", "科技园"),
                    Arrays.asList("科技园", "人民广场", "市政府", "火车站"),
                SAMPLE_SPEED_LIMITS,
                SAMPLE_SPEED_LIMITS,
                    SAMPLE_ATTRIBUTES,
                    SAMPLE_ATTRIBUTES
            ),
            new LineProfile(
                    "102路",
                    "汽车东站 → 软件园",
                    "对开",
                    Arrays.asList("汽车东站", "人民医院", "会展中心", "软件园"),
                    Arrays.asList("软件园", "会展中心", "人民医院", "汽车东站"),
                SAMPLE_SPEED_LIMITS,
                SAMPLE_SPEED_LIMITS,
                    SAMPLE_ATTRIBUTES,
                    SAMPLE_ATTRIBUTES
            ),
            new LineProfile(
                    "K7支线",
                    "高铁站 ↔ 古城北门",
                    "环线",
                    Arrays.asList("高铁站", "市民中心", "古城北门", "高铁站"),
                    Arrays.asList("高铁站", "市民中心", "古城北门", "高铁站"),
                SAMPLE_SPEED_LIMITS,
                SAMPLE_SPEED_LIMITS,
                    SAMPLE_ATTRIBUTES,
                    SAMPLE_ATTRIBUTES
            )
    ));

    private LegacyLineCatalog() {
    }

    @NonNull
    public static List<LineProfile> all(@Nullable Context context) {
        List<LineProfile> profiles = loadFromImportedResources(context);
        return profiles.isEmpty() ? SAMPLE_PROFILES : profiles;
    }

    @NonNull
    public static LineProfile first(@Nullable Context context) {
        List<LineProfile> profiles = all(context);
        return profiles.get(0);
    }

    @NonNull
    public static LineProfile findByName(@Nullable Context context, @Nullable String lineName) {
        List<LineProfile> profiles = all(context);
        for (LineProfile profile : profiles) {
            if (profile.matchesLineName(lineName)) {
                return profile;
            }
        }
        return profiles.get(0);
    }

    @NonNull
    public static LineProfile nextOf(@Nullable Context context, @Nullable String currentLineName) {
        List<LineProfile> profiles = all(context);
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).matchesLineName(currentLineName)) {
                return profiles.get((i + 1) % profiles.size());
            }
        }
        return profiles.get(0);
    }

    @NonNull
    private static List<LineProfile> loadFromImportedResources(@Nullable Context context) {
        File busDir = resolveBusDir(context);
        if (busDir == null || !busDir.exists()) {
            return Collections.emptyList();
        }

        File lineInfoFile = new File(busDir, "lineInfo.csv");
        List<List<String>> rows = readCsvRows(lineInfoFile);
        if (rows.size() <= 1) {
            return Collections.emptyList();
        }

        List<LineProfile> profiles = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() < 2) {
                continue;
            }
            String lineName = cleanCell(row.get(1));
            if (lineName.isEmpty()) {
                continue;
            }
            File lineDir = new File(busDir, lineName);
                DirectionProfile upstreamProfile = parseDirectionProfile(new File(lineDir, lineName + "S.csv"));
                DirectionProfile downstreamProfile = parseDirectionProfile(new File(lineDir, lineName + "X.csv"));
                if (upstreamProfile.stations.isEmpty() && downstreamProfile.stations.isEmpty()) {
                continue;
            }
            String attributeCode = row.size() > 3 ? cleanCell(row.get(3)) : "";
            String lineAttribute = mapLineAttribute(attributeCode);
            List<String> upstreamReminders = parseReminders(new File(lineDir, lineName + "SRemind.csv"));
            List<String> downstreamReminders = parseReminders(new File(lineDir, lineName + "XRemind.csv"));
            profiles.add(new LineProfile(
                    lineName,
                    buildLineInfo(lineAttribute, upstreamProfile.stations, downstreamProfile.stations),
                    lineAttribute,
                    upstreamProfile.stations,
                    downstreamProfile.stations,
                    upstreamProfile.speedLimits,
                    downstreamProfile.speedLimits,
                    upstreamReminders,
                    downstreamReminders
            ));
        }
        return profiles;
    }

    @Nullable
    private static File resolveBusDir(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        File managedRoot = new StationResourceArchiveUseCase().resolveManagedResourceRoot(context.getApplicationContext());
        File busDir = new File(managedRoot, "SourceFile/Bus");
        return busDir.exists() && busDir.isDirectory() ? busDir : null;
    }

    @NonNull
    private static DirectionProfile parseDirectionProfile(@NonNull File csvFile) {
        List<List<String>> rows = readCsvRows(csvFile);
        List<String> stations = new ArrayList<>();
        List<String> speedLimits = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() < 2) {
                continue;
            }
            String stationName = cleanCell(row.get(1));
            if (!stationName.isEmpty()) {
                stations.add(stationName);
                speedLimits.add(row.size() > 12 ? cleanCell(row.get(12)) : "");
            }
        }
        return new DirectionProfile(stations, speedLimits);
    }

    @NonNull
    private static List<String> parseReminders(@NonNull File csvFile) {
        List<List<String>> rows = readCsvRows(csvFile);
        List<String> reminders = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() < 2) {
                continue;
            }
            String value = cleanCell(row.get(1));
            if (!value.isEmpty()) {
                reminders.add(value);
            }
        }
        return reminders;
    }

    @NonNull
    private static List<List<String>> readCsvRows(@NonNull File csvFile) {
        if (!csvFile.exists() || !csvFile.isFile()) {
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

    @NonNull
    private static List<String> parseCsvRow(@NonNull String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
                continue;
            }
            if (ch == ',' && !quoted) {
                columns.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        columns.add(current.toString());
        return columns;
    }

    @NonNull
    private static String buildLineInfo(
            @NonNull String lineAttribute,
            @NonNull List<String> upstreamStations,
            @NonNull List<String> downstreamStations
    ) {
        List<String> baseStations = upstreamStations.isEmpty() ? downstreamStations : upstreamStations;
        if (baseStations.isEmpty()) {
            return "-";
        }
        String start = baseStations.get(0);
        String end = baseStations.get(baseStations.size() - 1);
        if ("环线".equals(lineAttribute) || start.equals(end)) {
            return start + " ↻ " + end;
        }
        return start + " → " + end;
    }

    @NonNull
    private static String mapLineAttribute(@Nullable String code) {
        if ("2".equals(code)) {
            return "环线";
        }
        if ("3".equals(code)) {
            return "防反";
        }
        return "对开";
    }

    @NonNull
    private static String cleanCell(@Nullable String value) {
        if (value == null) {
            return "";
        }
        String normalized = stripBom(value).trim();
        if (normalized.startsWith("\"") && normalized.endsWith("\"") && normalized.length() > 1) {
            normalized = normalized.substring(1, normalized.length() - 1).trim();
        }
        return normalized;
    }

    @NonNull
    private static String stripBom(@NonNull String value) {
        return value.startsWith("\uFEFF") ? value.substring(1) : value;
    }

    @NonNull
    private static String normalizeLineName(@Nullable String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim().replace(" ", "");
        if (normalized.endsWith("路")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized.toUpperCase(Locale.ROOT);
    }

    public static final class LineProfile {
        private final String lineName;
        private final String lineInfo;
        private final String lineAttribute;
        private final List<String> upstreamStations;
        private final List<String> downstreamStations;
        private final List<String> upstreamSpeedLimits;
        private final List<String> downstreamSpeedLimits;
        private final List<String> upstreamReminders;
        private final List<String> downstreamReminders;

        LineProfile(
                String lineName,
                String lineInfo,
                String lineAttribute,
                List<String> upstreamStations,
                List<String> downstreamStations,
                List<String> upstreamSpeedLimits,
                List<String> downstreamSpeedLimits,
                List<String> upstreamReminders,
                List<String> downstreamReminders
        ) {
            this.lineName = lineName;
            this.lineInfo = lineInfo;
            this.lineAttribute = lineAttribute;
            this.upstreamStations = Collections.unmodifiableList(new ArrayList<>(upstreamStations));
            this.downstreamStations = Collections.unmodifiableList(new ArrayList<>(downstreamStations));
            this.upstreamSpeedLimits = Collections.unmodifiableList(new ArrayList<>(upstreamSpeedLimits));
            this.downstreamSpeedLimits = Collections.unmodifiableList(new ArrayList<>(downstreamSpeedLimits));
            this.upstreamReminders = Collections.unmodifiableList(new ArrayList<>(upstreamReminders));
            this.downstreamReminders = Collections.unmodifiableList(new ArrayList<>(downstreamReminders));
        }

        @NonNull
        public String getLineName() {
            return lineName;
        }

        @NonNull
        public String getLineInfo() {
            return lineInfo;
        }

        @NonNull
        public String getLineNumber() {
            return normalizeLineName(lineName);
        }

        @NonNull
        public String getLineAttribute() {
            return lineAttribute;
        }

        @NonNull
        public String getUpstreamStartName() {
            return terminalName(upstreamStations, 0);
        }

        @NonNull
        public String getUpstreamEndName() {
            return terminalName(upstreamStations, upstreamStations.size() - 1);
        }

        @NonNull
        public String getDownstreamStartName() {
            return terminalName(downstreamStations, 0);
        }

        @NonNull
        public String getDownstreamEndName() {
            return terminalName(downstreamStations, downstreamStations.size() - 1);
        }

        public boolean matchesLineName(@Nullable String candidate) {
            return normalizeLineName(lineName).equals(normalizeLineName(candidate));
        }

        @NonNull
        public List<String> stationsForDirection(@Nullable String directionText) {
            if (directionText != null && directionText.contains("下") && !downstreamStations.isEmpty()) {
                return downstreamStations;
            }
            if (!upstreamStations.isEmpty()) {
                return upstreamStations;
            }
            return downstreamStations;
        }

        @NonNull
        public List<String> remindersForDirection(@Nullable String directionText) {
            if (directionText != null && directionText.contains("下") && !downstreamReminders.isEmpty()) {
                return downstreamReminders;
            }
            if (!upstreamReminders.isEmpty()) {
                return upstreamReminders;
            }
            return SAMPLE_ATTRIBUTES;
        }

        @NonNull
        public String speedLimitForDirection(@Nullable String directionText, int stationIndex) {
            List<String> speedLimits = directionText != null && directionText.contains("下") && !downstreamSpeedLimits.isEmpty()
                    ? downstreamSpeedLimits
                    : upstreamSpeedLimits;
            if (speedLimits.isEmpty() || stationIndex < 0 || stationIndex >= speedLimits.size()) {
                return "00";
            }
            String value = cleanCell(speedLimits.get(stationIndex));
            return value.isEmpty() ? "00" : value;
        }

        @NonNull
        public String describeForList() {
            return lineName + "    " + lineAttribute + "    " + lineInfo;
        }

        @NonNull
        private String terminalName(@NonNull List<String> stations, int index) {
            if (stations.isEmpty() || index < 0 || index >= stations.size()) {
                return "-";
            }
            String station = stations.get(index);
            return station == null || station.trim().isEmpty() ? "-" : station.trim();
        }
    }

    private static final class DirectionProfile {
        private final List<String> stations;
        private final List<String> speedLimits;

        private DirectionProfile(List<String> stations, List<String> speedLimits) {
            this.stations = Collections.unmodifiableList(new ArrayList<>(stations));
            this.speedLimits = Collections.unmodifiableList(new ArrayList<>(speedLimits));
        }
    }
}
