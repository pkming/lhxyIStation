package com.lhxy.istationdevice.android11.app.station;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lhxy.istationdevice.android11.domain.file.StationResourceArchiveUseCase;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

final class LegacySiteCollectionResourceStore {
    private static final Charset LEGACY_CSV_CHARSET = Charset.forName("GB18030");

    private LegacySiteCollectionResourceStore() {
    }

    static void saveSite(
            @NonNull Context context,
            @NonNull String lineName,
            @NonNull String directionText,
            @NonNull String siteName,
            int siteIndex,
            @NonNull GpsFixSnapshot snapshot
    ) {
        save(context, lineName, directionText, siteName, siteIndex, snapshot, false);
    }

    static void saveReminder(
            @NonNull Context context,
            @NonNull String lineName,
            @NonNull String directionText,
            @NonNull String reminderName,
            int reminderIndex,
            @NonNull GpsFixSnapshot snapshot
    ) {
        save(context, lineName, directionText, reminderName, reminderIndex, snapshot, true);
    }

    static LegacySiteCollectionStore.LearnedValue loadSite(
            @NonNull Context context,
            @NonNull String lineName,
            @NonNull String directionText,
            @NonNull String siteName,
            int siteIndex
    ) {
        return load(context, lineName, directionText, siteName, siteIndex, false);
    }

    static LegacySiteCollectionStore.LearnedValue loadReminder(
            @NonNull Context context,
            @NonNull String lineName,
            @NonNull String directionText,
            @NonNull String reminderName,
            int reminderIndex
    ) {
        return load(context, lineName, directionText, reminderName, reminderIndex, true);
    }

    private static void save(
            @NonNull Context context,
            @NonNull String lineName,
            @NonNull String directionText,
            @NonNull String itemName,
            int itemIndex,
            @NonNull GpsFixSnapshot snapshot,
            boolean reminderSection
    ) {
        File busDir = resolveBusDir(context);
        if (busDir == null) {
            throw new IllegalStateException("当前没有可写的报站资源目录");
        }
        String resolvedLineName = resolveLineName(busDir, lineName);
        if (resolvedLineName.isEmpty()) {
            throw new IllegalStateException("未找到线路资源: " + lineName);
        }

        int attribute = resolveLineAttribute(busDir, resolvedLineName);
        List<String> suffixes = resolveSuffixes(directionText, attribute);
        if (suffixes.isEmpty()) {
            throw new IllegalStateException("未解析到可写方向");
        }

        for (String suffix : suffixes) {
            File lineDir = new File(busDir, resolvedLineName);
            File csvFile = new File(lineDir, resolvedLineName + suffix + (reminderSection ? "Remind" : "") + ".csv");
            updateCsv(csvFile, itemName, itemIndex, snapshot, reminderSection);
        }
    }

    private static LegacySiteCollectionStore.LearnedValue load(
            @NonNull Context context,
            @NonNull String lineName,
            @NonNull String directionText,
            @NonNull String itemName,
            int itemIndex,
            boolean reminderSection
    ) {
        File busDir = resolveBusDir(context);
        if (busDir == null) {
            return null;
        }
        String resolvedLineName = resolveLineName(busDir, lineName);
        if (resolvedLineName.isEmpty()) {
            return null;
        }
        List<String> suffixes = resolveSuffixes(directionText, resolveLineAttribute(busDir, resolvedLineName));
        for (String suffix : suffixes) {
            File lineDir = new File(busDir, resolvedLineName);
            File csvFile = new File(lineDir, resolvedLineName + suffix + (reminderSection ? "Remind" : "") + ".csv");
            LegacySiteCollectionStore.LearnedValue value = loadFromCsv(csvFile, itemName, itemIndex, reminderSection);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static LegacySiteCollectionStore.LearnedValue loadFromCsv(
            @NonNull File csvFile,
            @NonNull String itemName,
            int itemIndex,
            boolean reminderSection
    ) {
        List<List<String>> rows = readCsvRows(csvFile);
        if (rows.size() <= 1) {
            return null;
        }
        int rowIndex = resolveRowIndex(rows, itemName, itemIndex, reminderSection);
        if (rowIndex <= 0 || rowIndex >= rows.size()) {
            return null;
        }
        List<String> row = rows.get(rowIndex);
        if (reminderSection) {
            return new LegacySiteCollectionStore.LearnedValue(
                    cell(row, 2),
                    cell(row, 3),
                    "-",
                    cell(row, 4),
                    cell(row, 5)
            );
        }
        return new LegacySiteCollectionStore.LearnedValue(
                cell(row, 3),
                cell(row, 4),
                "-",
                cell(row, 5),
                cell(row, 6)
        );
    }

    private static void updateCsv(
            @NonNull File csvFile,
            @NonNull String itemName,
            int itemIndex,
            @NonNull GpsFixSnapshot snapshot,
            boolean reminderSection
    ) {
        List<List<String>> rows = readCsvRows(csvFile);
        if (rows.size() <= 1) {
            throw new IllegalStateException("资源文件为空: " + csvFile.getName());
        }

        int rowIndex = resolveRowIndex(rows, itemName, itemIndex, reminderSection);
        if (rowIndex <= 0 || rowIndex >= rows.size()) {
            throw new IllegalStateException("未定位到学习项: " + itemName);
        }

        List<String> row = new ArrayList<>(rows.get(rowIndex));
        ensureSize(row, reminderSection ? 8 : 17);
        if (reminderSection) {
            row.set(2, valueOrEmpty(snapshot.getLongitudeDecimal()));
            row.set(3, valueOrEmpty(snapshot.getLatitudeDecimal()));
            row.set(4, valueOrEmpty(snapshot.getCourse()));
            row.set(5, valueOrEmpty(snapshot.getAltitudeMeters()));
        } else {
            row.set(3, valueOrEmpty(snapshot.getLongitudeDecimal()));
            row.set(4, valueOrEmpty(snapshot.getLatitudeDecimal()));
            row.set(5, valueOrEmpty(snapshot.getCourse()));
            row.set(6, valueOrEmpty(snapshot.getAltitudeMeters()));
        }
        rows.set(rowIndex, row);
        writeCsvRows(csvFile, rows);
    }

    private static int resolveRowIndex(
            @NonNull List<List<String>> rows,
            @NonNull String itemName,
            int itemIndex,
            boolean reminderSection
    ) {
        int directIndex = itemIndex + 1;
        if (directIndex > 0 && directIndex < rows.size() && rowMatches(rows.get(directIndex), itemName, reminderSection)) {
            return directIndex;
        }
        for (int index = 1; index < rows.size(); index++) {
            if (rowMatches(rows.get(index), itemName, reminderSection)) {
                return index;
            }
        }
        return directIndex;
    }

    private static boolean rowMatches(@NonNull List<String> row, @NonNull String itemName, boolean reminderSection) {
        String normalizedItem = normalize(itemName);
        if (normalizedItem.isEmpty()) {
            return false;
        }
        int primaryIndex = reminderSection ? 1 : 2;
        int secondaryIndex = reminderSection ? 1 : 1;
        return normalizedItem.equals(normalize(cell(row, primaryIndex)))
                || normalizedItem.equals(normalize(cell(row, secondaryIndex)));
    }

    @NonNull
    private static List<String> resolveSuffixes(@NonNull String directionText, int attribute) {
        if (attribute == 2) {
            List<String> suffixes = new ArrayList<>();
            suffixes.add("S");
            suffixes.add("X");
            return suffixes;
        }
        return Collections.singletonList(directionText.contains("下") ? "X" : "S");
    }

    private static int resolveLineAttribute(@NonNull File busDir, @NonNull String lineName) {
        File lineInfoFile = new File(busDir, "lineInfo.csv");
        List<List<String>> rows = readCsvRows(lineInfoFile);
        for (int index = 1; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            if (normalize(cell(row, 1)).equals(normalize(lineName))) {
                try {
                    return Integer.parseInt(cell(row, 3));
                } catch (Exception ignore) {
                    return 1;
                }
            }
        }
        return 1;
    }

    @NonNull
    private static String resolveLineName(@NonNull File busDir, @NonNull String lineName) {
        String normalizedTarget = normalize(lineName);
        File[] children = busDir.listFiles();
        if (children == null) {
            return "";
        }
        for (File child : children) {
            if (child != null && child.isDirectory() && normalize(child.getName()).equals(normalizedTarget)) {
                return child.getName();
            }
        }
        return "";
    }

    private static File resolveBusDir(@NonNull Context context) {
        File sourceRoot = new StationResourceArchiveUseCase().resolveManagedSourceRoot(context.getApplicationContext());
        File busDir = new File(sourceRoot, "Bus");
        return busDir.exists() && busDir.isDirectory() ? busDir : null;
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
        } catch (Exception e) {
            throw new IllegalStateException("读取资源文件失败: " + csvFile.getName(), e);
        }
        return rows;
    }

    private static void writeCsvRows(@NonNull File csvFile, @NonNull List<List<String>> rows) {
        File parent = csvFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("无法创建资源目录: " + parent.getAbsolutePath());
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(csvFile, false), LEGACY_CSV_CHARSET)) {
            for (int index = 0; index < rows.size(); index++) {
                if (index > 0) {
                    writer.write("\r\n");
                }
                writer.write(joinCsvRow(rows.get(index)));
            }
        } catch (Exception e) {
            throw new IllegalStateException("写入资源文件失败: " + csvFile.getName(), e);
        }
    }

    @NonNull
    private static List<String> parseCsvRow(@NonNull String line) {
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
    private static String joinCsvRow(@NonNull List<String> row) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < row.size(); index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(escapeCsv(cell(row, index)));
        }
        return builder.toString();
    }

    @NonNull
    private static String escapeCsv(@NonNull String value) {
        if (value.indexOf(',') < 0 && value.indexOf('"') < 0 && value.indexOf('\n') < 0 && value.indexOf('\r') < 0) {
            return value;
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    private static void ensureSize(@NonNull List<String> row, int size) {
        while (row.size() < size) {
            row.add("");
        }
    }

    @NonNull
    private static String cell(@NonNull List<String> row, int index) {
        if (index < 0 || index >= row.size()) {
            return "";
        }
        return row.get(index) == null ? "" : row.get(index);
    }

    @NonNull
    private static String normalize(@NonNull String value) {
        String normalized = value.trim().replace(" ", "");
        if (normalized.endsWith("路")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized.toUpperCase(Locale.ROOT);
    }

    @NonNull
    private static String stripBom(@NonNull String value) {
        return value.startsWith("\uFEFF") ? value.substring(1) : value;
    }

    @NonNull
    private static String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}