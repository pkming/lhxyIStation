package com.lhxy.istationdevice.android11.domain.file;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 报站资源导入/导出用例。
 * <p>
 * 先承接旧项目固定目录扫描语义，同时补一条 app 专用导入目录，
 * 让 Android 11 下在没有完整外部存储权限时仍能完成离线导入验证。
 */
public final class StationResourceArchiveUseCase {
    private static final Charset LEGACY_CSV_CHARSET = Charset.forName("GB18030");
    private static final String LEGACY_IMPORT_RELATIVE_DIR = "BusRes/BusImport";
    private static final String LEGACY_EXPORT_RELATIVE_DIR = "BusRes/BusExport";
    private static final String ARCHIVE_ZIP_NAME = "SourceFile.zip";
    private static final String ARCHIVE_RAR_NAME = "SourceFile.rar";
    private static final String APP_IMPORT_RELATIVE_DIR = "imports/BusRes/BusImport";
    private static final String APP_EXPORT_RELATIVE_DIR = "exports/BusRes/BusExport";
    private static final String MANAGED_RESOURCE_DIR = "station-resources/current";
    private static final String SUMMARY_FILE_NAME = "import-summary.txt";
    private static final String BUNDLED_RESOURCE_ASSET_PATH = "station-resources/SourceFile.zip";

    public OperationResult importStationResources(Context context) throws Exception {
        if (context == null) {
            return OperationResult.failure("导入报站资源失败", "当前没有可用上下文");
        }
        CandidateScan scan = scanCandidates(context);
        if (scan.zipCandidate == null) {
            if (scan.rarCandidate != null) {
                return OperationResult.failure(
                        "找到旧格式资源包，但当前只支持 ZIP",
                        scan.rarCandidate.getAbsolutePath() + "\n可改为提供 SourceFile.zip，或先放入 app 专用导入目录。"
                );
            }
            return OperationResult.failure(
                    "没有检查到可导入报站文件",
                    "已扫描:\n" + describePaths(scan.allCandidatePaths)
            );
        }

        File managedRoot = resolveManagedResourceRoot(context);
        recreateDirectory(managedRoot);
        File extractedDir = new File(managedRoot, "SourceFile");
    unzip(scan.zipCandidate, extractedDir);

        List<File> extractedFiles = new ArrayList<>();
        collectFiles(extractedDir, extractedFiles);
        LinkedHashSet<String> lineCandidates = deriveLineCandidates(extractedFiles);
        String lineName = lineCandidates.isEmpty() ? "-" : lineCandidates.iterator().next();
        File summaryFile = new File(managedRoot, SUMMARY_FILE_NAME);
    writeSummary(summaryFile, scan.zipCandidate, extractedDir, extractedFiles, lineCandidates, scan.allCandidatePaths);

        return OperationResult.success(
                "已导入报站资源",
        "资源包=" + scan.zipCandidate.getAbsolutePath()
                        + "\n解压目录=" + extractedDir.getAbsolutePath()
                        + "\n文件数=" + extractedFiles.size()
                        + "\n线路候选=" + (lineCandidates.isEmpty() ? "-" : join(lineCandidates)),
        scan.zipCandidate,
                extractedDir,
                lineName,
                scan.allCandidatePaths
        );
    }

    public OperationResult exportStationResources(Context context, File exportDir) throws Exception {
        if (context == null) {
            return OperationResult.failure("导出报站资源失败", "当前没有可用上下文");
        }
        File managedRoot = resolveManagedResourceRoot(context);
        File extractedDir = new File(managedRoot, "SourceFile");
        if (!extractedDir.exists()) {
            return OperationResult.failure("没有可导出的报站资源", "当前还没有导入成功的资源目录: " + extractedDir.getAbsolutePath());
        }

        File targetZip = resolveExportTarget(context, exportDir);
        File parent = targetZip.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("无法创建导出目录: " + parent.getAbsolutePath());
        }
        if (targetZip.exists() && !targetZip.delete()) {
            throw new IllegalStateException("无法覆盖旧导出文件: " + targetZip.getAbsolutePath());
        }
        zipDirectory(extractedDir, targetZip);

        List<File> extractedFiles = new ArrayList<>();
        collectFiles(extractedDir, extractedFiles);
        LinkedHashSet<String> lineCandidates = deriveLineCandidates(extractedFiles);
        String lineName = lineCandidates.isEmpty() ? "-" : lineCandidates.iterator().next();
        return OperationResult.success(
                "已导出报站资源",
                "导出文件=" + targetZip.getAbsolutePath() + "\n文件数=" + extractedFiles.size(),
                targetZip,
                extractedDir,
                lineName,
                scanCandidates(context).allCandidatePaths
        );
    }

    public String describeImportLocations(Context context) {
        if (context == null) {
            return "-";
        }
        try {
            return describePaths(scanCandidates(context).allCandidatePaths);
        } catch (Exception e) {
            return "扫描失败: " + safe(e.getMessage());
        }
    }

    public File resolveManagedResourceRoot(Context context) {
        File base = context.getExternalFilesDir("station-resources");
        if (base == null) {
            base = new File(context.getFilesDir(), "station-resources");
        }
        return new File(base, "current");
    }

    private CandidateScan scanCandidates(Context context) {
        List<String> allCandidatePaths = new ArrayList<>();
        File zipCandidate = null;
        File rarCandidate = null;
        for (File baseDir : uniqueBaseDirs(context)) {
            File appZip = new File(baseDir, APP_IMPORT_RELATIVE_DIR + "/" + ARCHIVE_ZIP_NAME);
            File appRar = new File(baseDir, APP_IMPORT_RELATIVE_DIR + "/" + ARCHIVE_RAR_NAME);
            allCandidatePaths.add(appZip.getAbsolutePath());
            allCandidatePaths.add(appRar.getAbsolutePath());
            if (zipCandidate == null && appZip.exists() && appZip.isFile()) {
                zipCandidate = appZip;
            }
            if (rarCandidate == null && appRar.exists() && appRar.isFile()) {
                rarCandidate = appRar;
            }

            File sharedRoot = resolveStorageRoot(baseDir);
            if (sharedRoot != null) {
                File legacyZip = new File(sharedRoot, LEGACY_IMPORT_RELATIVE_DIR + "/" + ARCHIVE_ZIP_NAME);
                File legacyRar = new File(sharedRoot, LEGACY_IMPORT_RELATIVE_DIR + "/" + ARCHIVE_RAR_NAME);
                allCandidatePaths.add(legacyZip.getAbsolutePath());
                allCandidatePaths.add(legacyRar.getAbsolutePath());
                if (zipCandidate == null && legacyZip.exists() && legacyZip.isFile()) {
                    zipCandidate = legacyZip;
                }
                if (rarCandidate == null && legacyRar.exists() && legacyRar.isFile()) {
                    rarCandidate = legacyRar;
                }
            }
        }
        if (zipCandidate == null) {
            File bundledZip = copyBundledAssetIfPresent(context, BUNDLED_RESOURCE_ASSET_PATH, ARCHIVE_ZIP_NAME);
            if (bundledZip != null) {
                zipCandidate = bundledZip;
                allCandidatePaths.add(0, bundledZip.getAbsolutePath() + " (bundled asset)");
            }
        }
        return new CandidateScan(zipCandidate, rarCandidate, allCandidatePaths);
    }

    private List<File> uniqueBaseDirs(Context context) {
        Set<String> paths = new LinkedHashSet<>();
        List<File> result = new ArrayList<>();
        File[] externalFilesDirs = context.getExternalFilesDirs(null);
        if (externalFilesDirs != null) {
            for (File dir : externalFilesDirs) {
                if (dir == null) {
                    continue;
                }
                if (paths.add(dir.getAbsolutePath())) {
                    result.add(dir);
                }
            }
        }
        File filesDir = context.getFilesDir();
        if (filesDir != null && paths.add(filesDir.getAbsolutePath())) {
            result.add(filesDir);
        }
        return result;
    }

    private File resolveStorageRoot(File baseDir) {
        File current = baseDir;
        while (current != null) {
            if ("Android".equals(current.getName())) {
                return current.getParentFile();
            }
            current = current.getParentFile();
        }
        return null;
    }

    private File resolveExportTarget(Context context, File exportDir) {
        for (File baseDir : uniqueBaseDirs(context)) {
            File sharedRoot = resolveStorageRoot(baseDir);
            if (sharedRoot != null) {
                File target = new File(sharedRoot, LEGACY_EXPORT_RELATIVE_DIR + "/" + ARCHIVE_ZIP_NAME);
                File parent = target.getParentFile();
                if (parent != null && (parent.exists() || parent.mkdirs())) {
                    return target;
                }
            }
        }
        File base = exportDir != null ? exportDir : context.getExternalFilesDir("exports");
        if (base == null) {
            base = new File(context.getFilesDir(), "exports");
        }
        return new File(base, APP_EXPORT_RELATIVE_DIR + "/" + ARCHIVE_ZIP_NAME);
    }

    private void recreateDirectory(File dir) {
        deleteRecursively(dir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("无法创建目录: " + dir.getAbsolutePath());
        }
    }

    private void unzip(File zipFile, File targetDir) throws Exception {
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IllegalStateException("无法创建解压目录: " + targetDir.getAbsolutePath());
        }
        String canonicalTargetPath = targetDir.getCanonicalPath() + File.separator;
        try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            byte[] buffer = new byte[4096];
            while ((entry = inputStream.getNextEntry()) != null) {
                File outFile = new File(targetDir, entry.getName());
                String canonicalOutPath = outFile.getCanonicalPath();
                if (!canonicalOutPath.startsWith(canonicalTargetPath) && !canonicalOutPath.equals(targetDir.getCanonicalPath())) {
                    throw new IllegalStateException("非法压缩包路径: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    if (!outFile.exists() && !outFile.mkdirs()) {
                        throw new IllegalStateException("无法创建目录: " + outFile.getAbsolutePath());
                    }
                } else {
                    File parent = outFile.getParentFile();
                    if (parent != null && !parent.exists() && !parent.mkdirs()) {
                        throw new IllegalStateException("无法创建目录: " + parent.getAbsolutePath());
                    }
                    try (FileOutputStream outputStream = new FileOutputStream(outFile, false)) {
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                        outputStream.flush();
                    }
                }
                inputStream.closeEntry();
            }
        }
    }

    private File copyBundledAssetIfPresent(Context context, String assetPath, String fileName) {
        if (context == null) {
            return null;
        }
        File importDir = new File(resolveManagedResourceRoot(context).getParentFile(), "bundled-imports");
        File target = new File(importDir, fileName);
        try (InputStream inputStream = context.getAssets().open(assetPath)) {
            if (!importDir.exists() && !importDir.mkdirs()) {
                throw new IllegalStateException("无法创建内置资源目录: " + importDir.getAbsolutePath());
            }
            try (FileOutputStream outputStream = new FileOutputStream(target, false)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
            }
            return target;
        } catch (Exception ignore) {
            return null;
        }
    }

    private void zipDirectory(File sourceDir, File zipFile) throws Exception {
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            List<File> files = new ArrayList<>();
            collectFiles(sourceDir, files);
            byte[] buffer = new byte[4096];
            for (File file : files) {
                String entryName = relativize(sourceDir, file);
                outputStream.putNextEntry(new ZipEntry(entryName));
                try (InputStream inputStream = new FileInputStream(file)) {
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                }
                outputStream.closeEntry();
            }
        }
    }

    private void collectFiles(File dir, List<File> files) {
        if (dir == null || !dir.exists()) {
            return;
        }
        if (dir.isFile()) {
            files.add(dir);
            return;
        }
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            collectFiles(child, files);
        }
    }

    private LinkedHashSet<String> deriveLineCandidates(List<File> extractedFiles) {
        LinkedHashSet<String> values = new LinkedHashSet<>();
        File lineInfoFile = findExtractedFile(extractedFiles, "lineInfo.csv");
        if (lineInfoFile != null) {
            values.addAll(parseLineNamesFromLineInfo(lineInfoFile));
        }
        if (!values.isEmpty()) {
            return values;
        }
        for (File file : extractedFiles) {
            String name = file.getName();
            String lowerName = name.toLowerCase(Locale.ROOT);
            if (!(lowerName.endsWith(".csv") || lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx"))) {
                continue;
            }
            String baseName = stripSuffix(name);
            if (baseName.endsWith("Remind")) {
                baseName = baseName.substring(0, baseName.length() - "Remind".length());
            }
            if ((baseName.endsWith("S") || baseName.endsWith("X")) && baseName.length() > 1) {
                baseName = baseName.substring(0, baseName.length() - 1);
            }
            if (baseName.equalsIgnoreCase("config") || baseName.equalsIgnoreCase("lineInfo") || baseName.trim().isEmpty()) {
                continue;
            }
            values.add(baseName.trim());
        }
        return values;
    }

    private File findExtractedFile(List<File> extractedFiles, String fileName) {
        for (File file : extractedFiles) {
            if (fileName.equalsIgnoreCase(file.getName())) {
                return file;
            }
        }
        return null;
    }

    private LinkedHashSet<String> parseLineNamesFromLineInfo(File file) {
        LinkedHashSet<String> values = new LinkedHashSet<>();
        if (file == null || !file.exists()) {
            return values;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), LEGACY_CSV_CHARSET))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                String normalized = stripBom(line).trim();
                if (normalized.isEmpty()) {
                    continue;
                }
                if (header) {
                    header = false;
                    continue;
                }
                String[] columns = normalized.split(",");
                if (columns.length > 1) {
                    String lineName = stripBom(columns[1]).replace("\"", "").trim();
                    if (!lineName.isEmpty()) {
                        values.add(lineName);
                    }
                }
            }
        } catch (Exception ignore) {
            return values;
        }
        return values;
    }

    private String stripSuffix(String value) {
        int index = value.lastIndexOf('.');
        return index > 0 ? value.substring(0, index) : value;
    }

    private String stripBom(String value) {
        if (value == null) {
            return "";
        }
        return value.startsWith("\uFEFF") ? value.substring(1) : value;
    }

    private void writeSummary(
            File summaryFile,
            File archiveFile,
            File extractedDir,
            List<File> extractedFiles,
            LinkedHashSet<String> lineCandidates,
            List<String> scanPaths
    ) throws Exception {
        File parent = summaryFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("无法创建摘要目录: " + parent.getAbsolutePath());
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(summaryFile, false), StandardCharsets.UTF_8)) {
            writer.write("importedAt=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n");
            writer.write("archive=" + archiveFile.getAbsolutePath() + "\n");
            writer.write("extractedDir=" + extractedDir.getAbsolutePath() + "\n");
            writer.write("lineCandidates=" + (lineCandidates.isEmpty() ? "-" : join(lineCandidates)) + "\n");
            writer.write("scannedPaths=\n" + describePaths(scanPaths) + "\n");
            writer.write("files=\n");
            for (File file : extractedFiles) {
                writer.write("- " + relativize(extractedDir, file) + "\n");
            }
            writer.flush();
        }
    }

    private String relativize(File baseDir, File file) {
        String basePath = baseDir.getAbsolutePath();
        String filePath = file.getAbsolutePath();
        if (filePath.startsWith(basePath)) {
            String relative = filePath.substring(basePath.length());
            if (relative.startsWith(File.separator)) {
                relative = relative.substring(1);
            }
            return relative.isEmpty() ? file.getName() : relative;
        }
        return file.getName();
    }

    private void deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        if (!file.delete()) {
            throw new IllegalStateException("无法删除旧目录: " + file.getAbsolutePath());
        }
    }

    private String describePaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return "-";
        }
        StringBuilder builder = new StringBuilder();
        for (String path : paths) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append("- ").append(path);
        }
        return builder.toString();
    }

    private String join(Set<String> values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
        return builder.toString();
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "未知错误" : value.trim();
    }

    private static final class CandidateScan {
        private final File zipCandidate;
        private final File rarCandidate;
        private final List<String> allCandidatePaths;

        private CandidateScan(File zipCandidate, File rarCandidate, List<String> allCandidatePaths) {
            this.zipCandidate = zipCandidate;
            this.rarCandidate = rarCandidate;
            this.allCandidatePaths = allCandidatePaths;
        }
    }

    public static final class OperationResult {
        private final boolean success;
        private final String summary;
        private final String detail;
        private final File archiveFile;
        private final File workingDir;
        private final String lineName;
        private final List<String> candidatePaths;

        private OperationResult(boolean success, String summary, String detail, File archiveFile, File workingDir, String lineName, List<String> candidatePaths) {
            this.success = success;
            this.summary = summary == null ? "" : summary.trim();
            this.detail = detail == null ? "" : detail.trim();
            this.archiveFile = archiveFile;
            this.workingDir = workingDir;
            this.lineName = lineName == null || lineName.trim().isEmpty() ? "-" : lineName.trim();
            this.candidatePaths = candidatePaths == null ? new ArrayList<>() : new ArrayList<>(candidatePaths);
        }

        public static OperationResult success(String summary, String detail, File archiveFile, File workingDir, String lineName, List<String> candidatePaths) {
            return new OperationResult(true, summary, detail, archiveFile, workingDir, lineName, candidatePaths);
        }

        public static OperationResult failure(String summary, String detail) {
            return new OperationResult(false, summary, detail, null, null, "-", null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getSummary() {
            return summary;
        }

        public String getDetail() {
            return detail;
        }

        public File getArchiveFile() {
            return archiveFile;
        }

        public File getWorkingDir() {
            return workingDir;
        }

        public String getLineName() {
            return lineName;
        }

        public List<String> getCandidatePaths() {
            return new ArrayList<>(candidatePaths);
        }
    }
}
