package com.lhxy.istationdevice.android11.domain.file;

import android.content.Context;

import com.lhxy.istationdevice.android11.core.LegacyInfoMessageRepository;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 报站资源导入/导出用例。
 * <p>
 * 先承接旧项目固定目录扫描语义，同时补一条 app 专用导入目录，
 * 让 Android 11 下在没有完整外部存储权限时仍能完成离线导入验证。
 * <p>
 * 查找关键字：资源导入、资源导出、SourceFile.zip、托管资源目录。
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
    private static final String BUNDLED_IMPORT_ASSET_DIR = "station-resources/bundled-imports";
    private static final String[] SUPPORTED_TABLE_EXTENSIONS = {".csv", ".xls", ".xlx", ".xlsx", ".xml"};
    private static final String SOURCE_LABEL_LEGACY_IMPORT = "U盘导入目录";
    private static final String SOURCE_LABEL_ROOT_COMPAT = "U盘根目录兼容包";
    private static final String SOURCE_LABEL_APP_IMPORT = "app 导入目录";
    private static final String SOURCE_LABEL_BUNDLED = "内置测试包";

    /**
     * 导入报站资源包。
     * <p>
     * 会扫描旧路径和 app 专用路径，把 SourceFile.zip 解到托管目录。
     */
    public OperationResult importStationResources(Context context) throws Exception {
        return importStationResources(context, null);
    }

    /**
     * 按指定候选包导入报站资源。
     */
    public OperationResult importStationResources(Context context, String preferredCandidatePath) throws Exception {
        if (context == null) {
            return OperationResult.failure("导入报站资源失败", "当前没有可用上下文");
        }
        CandidateScan scan = scanCandidates(context);
        ImportCandidate selectedCandidate = resolveImportCandidate(scan, preferredCandidatePath);
        if (selectedCandidate == null) {
            return OperationResult.failure(
                    "没有检查到可导入报站文件",
                    "已扫描:\n" + describePaths(scan.allCandidatePaths)
            );
        }
        if (!selectedCandidate.isSupported()) {
            return OperationResult.failure(
                    "导入报站资源失败",
                    selectedCandidate.getUnsupportedReason() + ": " + selectedCandidate.getFileName()
            );
        }

        File managedRoot = resolveManagedResourceRoot(context);
        File stagingRoot = resolveImportStagingRoot(managedRoot);
        recreateDirectory(stagingRoot);
        File extractedDir = new File(stagingRoot, "SourceFile");
        extractArchive(selectedCandidate.getFile(), extractedDir);

        List<File> extractedFiles = new ArrayList<>();
        collectFiles(extractedDir, extractedFiles);
        ArchiveSelfCheck selfCheck = inspectExtractedArchive(extractedDir, extractedFiles);
        if (!selfCheck.isValid()) {
            deleteRecursively(stagingRoot);
            return OperationResult.failure(
                "导入报站资源失败",
                selfCheck.describeFailure(selectedCandidate.getFile(), scan.allCandidatePaths),
                selfCheck.toDiagnostics()
            );
        }

        List<DiagnosticItem> diagnostics = buildImportDiagnostics(extractedDir, extractedFiles);
        if (hasFailureDiagnostics(diagnostics)) {
            deleteRecursively(stagingRoot);
            return OperationResult.failure(
                    "导入报站资源失败",
                    "资源包=" + selectedCandidate.getAbsolutePath()
                            + "\n" + buildDiagnosticSummary(diagnostics),
                    diagnostics
            );
        }

        replaceDirectory(stagingRoot, managedRoot);

        File finalExtractedDir = new File(managedRoot, "SourceFile");
        List<File> finalExtractedFiles = new ArrayList<>();
        collectFiles(finalExtractedDir, finalExtractedFiles);
        LinkedHashSet<String> lineCandidates = deriveLineCandidates(finalExtractedFiles);
        int importedMessageCount = importMessageCatalog(context, finalExtractedFiles);
        String lineName = lineCandidates.isEmpty() ? "-" : lineCandidates.iterator().next();
        File summaryFile = new File(managedRoot, SUMMARY_FILE_NAME);
        writeSummary(summaryFile, selectedCandidate.getFile(), finalExtractedDir, finalExtractedFiles, lineCandidates, scan.allCandidatePaths, importedMessageCount);

        return OperationResult.success(
                "已导入报站资源",
            "资源包=" + selectedCandidate.getAbsolutePath()
                + "\n解压目录=" + finalExtractedDir.getAbsolutePath()
                + "\n文件数=" + finalExtractedFiles.size()
                + "\n消息数=" + importedMessageCount
                + "\n线路候选=" + (lineCandidates.isEmpty() ? "-" : join(lineCandidates))
                + "\n" + buildDiagnosticSummary(diagnostics),
            selectedCandidate.getFile(),
            finalExtractedDir,
                lineName,
            scan.allCandidatePaths,
            diagnostics
        );
    }

    /**
     * 列出当前可导入的候选资源包。
     */
    public List<ImportCandidate> listImportCandidates(Context context) {
        if (context == null) {
            return new ArrayList<>();
        }
        return scanCandidates(context).getImportCandidates();
    }

    /**
     * 导出当前托管的报站资源包。
     */
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

    /**
     * 输出当前资源导入扫描路径摘要。
     */
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

    /**
     * 返回托管资源根目录。
     */
    public File resolveManagedResourceRoot(Context context) {
        File base = context.getExternalFilesDir("station-resources");
        if (base == null) {
            base = new File(context.getFilesDir(), "station-resources");
        }
        return new File(base, "current");
    }

    /**
     * 返回真正可供业务读取的 SourceFile 根目录。
     */
    public File resolveManagedSourceRoot(Context context) {
        File managedRoot = resolveManagedResourceRoot(context);
        return resolveExtractedSourceRoot(new File(managedRoot, "SourceFile"));
    }

    File resolveExtractedSourceRoot(File extractedDir) {
        if (extractedDir == null) {
            return null;
        }
        File standardBusDir = new File(extractedDir, "Bus");
        if (standardBusDir.exists() && standardBusDir.isDirectory()) {
            return extractedDir;
        }
        File nestedSourceRoot = new File(extractedDir, "SourceFile");
        File nestedBusDir = new File(nestedSourceRoot, "Bus");
        if (nestedBusDir.exists() && nestedBusDir.isDirectory()) {
            return nestedSourceRoot;
        }
        File discoveredSourceRoot = findNestedSourceRoot(extractedDir, 4);
        if (discoveredSourceRoot != null) {
            return discoveredSourceRoot;
        }
        return extractedDir;
    }

    private File findNestedSourceRoot(File dir, int remainingDepth) {
        if (dir == null || remainingDepth < 0 || !dir.exists() || !dir.isDirectory()) {
            return null;
        }
        File directBusDir = new File(dir, "Bus");
        if (directBusDir.exists() && directBusDir.isDirectory()) {
            return dir;
        }
        File[] children = dir.listFiles();
        if (children == null || children.length == 0) {
            return null;
        }
        for (File child : children) {
            if (child == null || !child.isDirectory()) {
                continue;
            }
            File result = findNestedSourceRoot(child, remainingDepth - 1);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * 扫描旧项目和 app 专用导入目录里的候选资源包。
     */
    private CandidateScan scanCandidates(Context context) {
        return scanCandidates(uniqueBaseDirs(context), copyBundledAssetsIfPresent(context));
    }

    List<ImportCandidate> scanImportCandidatesForTest(List<File> baseDirs) {
        return scanCandidates(baseDirs, Collections.emptyList()).getImportCandidates();
    }

    private CandidateScan scanCandidates(List<File> baseDirs, List<File> bundledFiles) {
        List<String> allCandidatePaths = new ArrayList<>();
        List<ImportCandidate> importCandidates = new ArrayList<>();
        LinkedHashSet<String> seenCandidatePaths = new LinkedHashSet<>();
        if (baseDirs == null) {
            baseDirs = Collections.emptyList();
        }
        for (File baseDir : baseDirs) {
            File sharedRoot = resolveStorageRoot(baseDir);
            if (sharedRoot != null) {
                File legacyImportDir = new File(sharedRoot, LEGACY_IMPORT_RELATIVE_DIR);
                collectArchiveCandidates(legacyImportDir, SOURCE_LABEL_LEGACY_IMPORT, false, importCandidates, allCandidatePaths, seenCandidatePaths);
                collectNamedArchiveCandidates(sharedRoot, SOURCE_LABEL_ROOT_COMPAT, false, importCandidates, allCandidatePaths, seenCandidatePaths, ARCHIVE_ZIP_NAME, ARCHIVE_RAR_NAME);
            }

            File appImportDir = new File(baseDir, APP_IMPORT_RELATIVE_DIR);
            collectArchiveCandidates(appImportDir, SOURCE_LABEL_APP_IMPORT, false, importCandidates, allCandidatePaths, seenCandidatePaths);
        }
        if (bundledFiles == null) {
            bundledFiles = Collections.emptyList();
        }
        for (File bundledFile : bundledFiles) {
            addImportCandidate(importCandidates, bundledFile, SOURCE_LABEL_BUNDLED, true, allCandidatePaths, seenCandidatePaths);
        }
        sortImportCandidates(importCandidates);
        ImportCandidate zipCandidate = null;
        ImportCandidate rarCandidate = null;
        for (ImportCandidate candidate : importCandidates) {
            if (zipCandidate == null && candidate.isZip()) {
                zipCandidate = candidate;
            }
            if (rarCandidate == null && candidate.isRar()) {
                rarCandidate = candidate;
            }
        }
        if (importCandidates.isEmpty()) {
            allCandidatePaths.add(0, "内置资源目录=" + BUNDLED_RESOURCE_ASSET_PATH);
            allCandidatePaths.add(1, "内置多包目录=" + BUNDLED_IMPORT_ASSET_DIR);
        }
        return new CandidateScan(importCandidates, zipCandidate, rarCandidate, allCandidatePaths);
    }

    private ImportCandidate resolveImportCandidate(CandidateScan scan, String preferredCandidatePath) {
        if (scan == null) {
            return null;
        }
        if (preferredCandidatePath != null && !preferredCandidatePath.trim().isEmpty()) {
            for (ImportCandidate candidate : scan.importCandidates) {
                if (preferredCandidatePath.trim().equals(candidate.getAbsolutePath())) {
                    return candidate;
                }
            }
            return null;
        }
        return scan.importCandidates.isEmpty() ? null : scan.importCandidates.get(0);
    }

    private void collectNamedArchiveCandidates(
            File dir,
            String sourceLabel,
            boolean bundledAsset,
            List<ImportCandidate> importCandidates,
            List<String> allCandidatePaths,
            Set<String> seenCandidatePaths,
            String... fileNames
    ) {
        if (dir == null || fileNames == null || fileNames.length == 0) {
            return;
        }
        for (String fileName : fileNames) {
            if (fileName == null || fileName.trim().isEmpty()) {
                continue;
            }
            File candidate = new File(dir, fileName);
            allCandidatePaths.add(candidate.getAbsolutePath());
            if (isArchiveCandidate(candidate)) {
                addImportCandidate(importCandidates, candidate, sourceLabel, bundledAsset, allCandidatePaths, seenCandidatePaths);
            }
        }
    }

    private void collectArchiveCandidates(
            File dir,
            String sourceLabel,
            boolean bundledAsset,
            List<ImportCandidate> importCandidates,
            List<String> allCandidatePaths,
            Set<String> seenCandidatePaths
    ) {
        if (dir == null) {
            return;
        }
        allCandidatePaths.add(dir.getAbsolutePath() + "/*.zip");
        allCandidatePaths.add(dir.getAbsolutePath() + "/*.rar");
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] children = dir.listFiles();
        if (children == null || children.length == 0) {
            return;
        }
        List<File> files = new ArrayList<>();
        for (File child : children) {
            if (isArchiveCandidate(child)) {
                files.add(child);
            }
        }
        Collections.sort(files, archiveFileComparator());
        for (File file : files) {
            addImportCandidate(importCandidates, file, sourceLabel, bundledAsset, allCandidatePaths, seenCandidatePaths);
        }
    }

    private void addImportCandidate(
            List<ImportCandidate> importCandidates,
            File file,
            String sourceLabel,
            boolean bundledAsset,
            List<String> allCandidatePaths,
            Set<String> seenCandidatePaths
    ) {
        if (!isArchiveCandidate(file)) {
            return;
        }
        String absolutePath = file.getAbsolutePath();
        if (!seenCandidatePaths.add(absolutePath)) {
            return;
        }
        importCandidates.add(new ImportCandidate(file, sourceLabel, bundledAsset));
        allCandidatePaths.add(absolutePath);
    }

    private static int detectRarMajorVersion(File file) {
        if (file == null || !file.isFile()) {
            return -1;
        }
        byte[] header = new byte[8];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int read = inputStream.read(header);
            if (read >= 8
                    && header[0] == 0x52
                    && header[1] == 0x61
                    && header[2] == 0x72
                    && header[3] == 0x21
                    && header[4] == 0x1A
                    && header[5] == 0x07
                    && header[6] == 0x01
                    && header[7] == 0x00) {
                return 5;
            }
            if (read >= 7
                    && header[0] == 0x52
                    && header[1] == 0x61
                    && header[2] == 0x72
                    && header[3] == 0x21
                    && header[4] == 0x1A
                    && header[5] == 0x07
                    && header[6] == 0x00) {
                return 4;
            }
        } catch (Exception ignore) {
            return -1;
        }
        return -1;
    }

    private Comparator<File> archiveFileComparator() {
        return (left, right) -> {
            int modifiedCompare = Long.compare(right.lastModified(), left.lastModified());
            if (modifiedCompare != 0) {
                return modifiedCompare;
            }
            return left.getName().compareToIgnoreCase(right.getName());
        };
    }

    private void sortImportCandidates(List<ImportCandidate> importCandidates) {
        if (importCandidates == null || importCandidates.size() <= 1) {
            return;
        }
        Collections.sort(importCandidates, (left, right) -> {
            int sourceCompare = Integer.compare(sourceRank(left), sourceRank(right));
            if (sourceCompare != 0) {
                return sourceCompare;
            }
            int typeCompare = Boolean.compare(!left.isZip(), !right.isZip());
            if (typeCompare != 0) {
                return typeCompare;
            }
            int modifiedCompare = Long.compare(right.getLastModified(), left.getLastModified());
            if (modifiedCompare != 0) {
                return modifiedCompare;
            }
            return left.getFileName().compareToIgnoreCase(right.getFileName());
        });
    }

    private int sourceRank(ImportCandidate candidate) {
        if (candidate == null) {
            return Integer.MAX_VALUE;
        }
        if (SOURCE_LABEL_LEGACY_IMPORT.equals(candidate.getSourceLabel())) {
            return 0;
        }
        if (SOURCE_LABEL_ROOT_COMPAT.equals(candidate.getSourceLabel())) {
            return 1;
        }
        if (SOURCE_LABEL_APP_IMPORT.equals(candidate.getSourceLabel())) {
            return 2;
        }
        if (candidate.isBundledAsset() || SOURCE_LABEL_BUNDLED.equals(candidate.getSourceLabel())) {
            return 3;
        }
        return 4;
    }

    private boolean isArchiveCandidate(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        String lowerName = file.getName().toLowerCase(Locale.ROOT);
        return lowerName.endsWith(".zip") || lowerName.endsWith(".rar");
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

    private File resolveImportStagingRoot(File managedRoot) {
        File parent = managedRoot == null ? null : managedRoot.getParentFile();
        if (parent == null) {
            return new File("import-staging");
        }
        return new File(parent, "import-staging");
    }

    private void recreateDirectory(File dir) {
        deleteRecursively(dir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("无法创建目录: " + dir.getAbsolutePath());
        }
    }

    private void replaceDirectory(File sourceDir, File targetDir) throws Exception {
        deleteRecursively(targetDir);
        File parent = targetDir == null ? null : targetDir.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("无法创建目录: " + parent.getAbsolutePath());
        }
        if (sourceDir.renameTo(targetDir)) {
            return;
        }
        copyRecursively(sourceDir, targetDir);
        deleteRecursively(sourceDir);
    }

    private void copyRecursively(File source, File target) throws Exception {
        if (source == null || !source.exists()) {
            return;
        }
        if (source.isDirectory()) {
            if (!target.exists() && !target.mkdirs()) {
                throw new IllegalStateException("无法创建目录: " + target.getAbsolutePath());
            }
            File[] children = source.listFiles();
            if (children == null) {
                return;
            }
            for (File child : children) {
                copyRecursively(child, new File(target, child.getName()));
            }
            return;
        }
        File parent = target.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("无法创建目录: " + parent.getAbsolutePath());
        }
        try (InputStream inputStream = new FileInputStream(source);
             FileOutputStream outputStream = new FileOutputStream(target, false)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
        }
    }

    /**
     * 安全解压 ZIP 到指定目录，并阻止越界路径。
     */
    private void extractArchive(File archiveFile, File targetDir) throws Exception {
        if (archiveFile == null) {
            throw new IllegalArgumentException("archiveFile == null");
        }
        String lowerName = archiveFile.getName().toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".rar")) {
            unrar(archiveFile, targetDir);
            return;
        }
        unzip(archiveFile, targetDir);
    }

    private void unzip(File zipFile, File targetDir) throws Exception {
        List<Charset> candidateCharsets = new ArrayList<>();
        candidateCharsets.add(StandardCharsets.UTF_8);
        if (!StandardCharsets.UTF_8.equals(LEGACY_CSV_CHARSET)) {
            candidateCharsets.add(LEGACY_CSV_CHARSET);
        }
        Exception lastError = null;
        for (Charset charset : candidateCharsets) {
            recreateDirectory(targetDir);
            try {
                unzipWithCharset(zipFile, targetDir, charset);
                return;
            } catch (Exception e) {
                lastError = e;
                if (!shouldRetryWithLegacyZipCharset(e, charset, candidateCharsets)) {
                    throw e;
                }
            }
        }
        if (lastError != null) {
            throw lastError;
        }
    }

    private void unzipWithCharset(File zipFile, File targetDir, Charset charset) throws Exception {
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IllegalStateException("无法创建解压目录: " + targetDir.getAbsolutePath());
        }
        String canonicalTargetPath = targetDir.getCanonicalPath() + File.separator;
        String canonicalTargetDir = targetDir.getCanonicalPath();
        try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile), charset)) {
            ZipEntry entry;
            byte[] buffer = new byte[4096];
            while ((entry = inputStream.getNextEntry()) != null) {
                File outFile = new File(targetDir, entry.getName());
                String canonicalOutPath = outFile.getCanonicalPath();
                if (!canonicalOutPath.startsWith(canonicalTargetPath) && !canonicalOutPath.equals(canonicalTargetDir)) {
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

    private boolean shouldRetryWithLegacyZipCharset(Exception error, Charset attemptedCharset, List<Charset> candidateCharsets) {
        if (error == null || attemptedCharset == null || candidateCharsets == null || candidateCharsets.isEmpty()) {
            return false;
        }
        if (candidateCharsets.indexOf(attemptedCharset) >= candidateCharsets.size() - 1) {
            return false;
        }
        String message = error.getMessage();
        if (message == null) {
            return false;
        }
        String normalized = message.trim().toLowerCase(Locale.ROOT);
        return normalized.contains("malformed") || normalized.contains("invalid cen header") || normalized.contains("illegal") || normalized.contains("mismatched");
    }

    private void unrar(File rarFile, File targetDir) throws Exception {
        recreateDirectory(targetDir);
        ArchiveFormat archiveFormat = resolveRarArchiveFormat(rarFile);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(rarFile, "r");
             RandomAccessFileInStream inStream = new RandomAccessFileInStream(randomAccessFile);
             IInArchive inArchive = SevenZip.openInArchive(archiveFormat, inStream)) {
            for (ISimpleInArchiveItem item : inArchive.getSimpleInterface().getArchiveItems()) {
                String entryName = normalizeArchiveEntryName(item.getPath());
                if (entryName.isEmpty()) {
                    continue;
                }
                File outFile = resolveArchiveOutputFile(targetDir, entryName);
                if (item.isFolder()) {
                    if (!outFile.exists() && !outFile.mkdirs()) {
                        throw new IllegalStateException("无法创建目录: " + outFile.getAbsolutePath());
                    }
                    continue;
                }
                File parent = outFile.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new IllegalStateException("无法创建目录: " + parent.getAbsolutePath());
                }
                try (FileOutputStream outputStream = new FileOutputStream(outFile, false)) {
                    ExtractOperationResult result = item.extractSlow(new OutputStreamSequentialOutStream(outputStream));
                    if (result != ExtractOperationResult.OK) {
                        throw new IllegalStateException("解压失败: " + entryName + " -> " + result);
                    }
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("解压 RAR 资源包失败: " + rarFile.getName() + " - " + safe(e.getMessage()), e);
        }
    }

    private ArchiveFormat resolveRarArchiveFormat(File rarFile) {
        return detectRarMajorVersion(rarFile) == 5 ? ArchiveFormat.RAR5 : ArchiveFormat.RAR;
    }

    private File resolveArchiveOutputFile(File targetDir, String entryName) throws Exception {
        String canonicalTargetPath = targetDir.getCanonicalPath() + File.separator;
        String canonicalTargetDir = targetDir.getCanonicalPath();
        File outFile = new File(targetDir, entryName);
        String canonicalOutPath = outFile.getCanonicalPath();
        if (!canonicalOutPath.startsWith(canonicalTargetPath) && !canonicalOutPath.equals(canonicalTargetDir)) {
            throw new IllegalStateException("非法压缩包路径: " + entryName);
        }
        return outFile;
    }

    private String normalizeArchiveEntryName(String fileName) {
        if (fileName == null) {
            return "";
        }
        String normalized = fileName.trim().replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    private static final class OutputStreamSequentialOutStream implements ISequentialOutStream {
        private final FileOutputStream outputStream;

        private OutputStreamSequentialOutStream(FileOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public int write(byte[] data) throws SevenZipException {
            if (data == null || data.length == 0) {
                return 0;
            }
            try {
                outputStream.write(data);
                return data.length;
            } catch (Exception e) {
                throw new SevenZipException("写入解压文件失败", e);
            }
        }
    }

    /**
     * 把内置资源包拷到可导入目录，作为兜底资源来源。
     */
    private List<File> copyBundledAssetsIfPresent(Context context) {
        List<File> candidates = new ArrayList<>();
        if (context == null) {
            return candidates;
        }
        File importDir = new File(resolveManagedResourceRoot(context).getParentFile(), "bundled-imports");
        File defaultZip = copyBundledAssetIfPresent(context, BUNDLED_RESOURCE_ASSET_PATH, ARCHIVE_ZIP_NAME, importDir);
        if (defaultZip != null) {
            candidates.add(defaultZip);
        }
        try {
            String[] assetFiles = context.getAssets().list(BUNDLED_IMPORT_ASSET_DIR);
            if (assetFiles == null) {
                return candidates;
            }
            for (String assetFile : assetFiles) {
                if (assetFile == null || assetFile.trim().isEmpty()) {
                    continue;
                }
                String lowerName = assetFile.toLowerCase(Locale.ROOT);
                if (!(lowerName.endsWith(".zip") || lowerName.endsWith(".rar"))) {
                    continue;
                }
                File copied = copyBundledAssetIfPresent(context, BUNDLED_IMPORT_ASSET_DIR + "/" + assetFile, assetFile, importDir);
                if (copied != null) {
                    candidates.add(copied);
                }
            }
        } catch (Exception ignore) {
            return candidates;
        }
        return candidates;
    }

    private File copyBundledAssetIfPresent(Context context, String assetPath, String fileName, File importDir) {
        if (context == null || importDir == null) {
            return null;
        }
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

    ArchiveSelfCheck inspectExtractedArchive(File extractedDir, List<File> extractedFiles) {
        List<String> problems = new ArrayList<>();
        if (extractedDir == null || !extractedDir.exists()) {
            problems.add("压缩包没有解出 SourceFile 目录");
            return new ArchiveSelfCheck(false, problems, new ArrayList<>());
        }
        if (extractedFiles == null || extractedFiles.isEmpty()) {
            problems.add("压缩包已解压，但里面没有任何文件");
            return new ArchiveSelfCheck(false, problems, describeArchiveEntries(extractedDir, extractedFiles));
        }

        File sourceRoot = resolveExtractedSourceRoot(extractedDir);
        File busDir = sourceRoot == null ? null : new File(sourceRoot, "Bus");
        if (busDir == null || !busDir.exists() || !busDir.isDirectory()) {
            problems.add("缺少 SourceFile/Bus 目录");
        } else if (!hasBusPayload(busDir)) {
            problems.add("Bus 目录下未发现线路目录，也没有 lineInfo/config/Vchinfo 表格文件（支持 csv/xls/xlx/xlsx/xml）");
        }
        return new ArchiveSelfCheck(problems.isEmpty(), problems, describeArchiveEntries(extractedDir, extractedFiles));
    }

    List<DiagnosticItem> buildImportDiagnostics(File extractedDir, List<File> extractedFiles) {
        List<DiagnosticItem> diagnostics = new ArrayList<>();
        if (extractedDir == null || !extractedDir.exists()) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, "SourceFile", "压缩包没有解出 SourceFile 目录"));
            return diagnostics;
        }
        File sourceRoot = resolveExtractedSourceRoot(extractedDir);
        if (sourceRoot == null || !sourceRoot.exists()) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, "SourceFile", "没有识别到可用资源根目录"));
            return diagnostics;
        }
        diagnostics.add(new DiagnosticItem(
                DiagnosticItem.LEVEL_OK,
                relativize(extractedDir, sourceRoot),
                "已识别为资源根目录，文件数=" + (extractedFiles == null ? 0 : extractedFiles.size())
        ));

        File busDir = new File(sourceRoot, "Bus");
        if (!busDir.exists() || !busDir.isDirectory()) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, relativize(extractedDir, busDir), "缺少 Bus 目录"));
            return diagnostics;
        }
        diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_OK, relativize(extractedDir, busDir), "Bus 目录存在"));

        LineInfoValidation lineInfoValidation = validateLineInfoFile(extractedDir, busDir, diagnostics);
        validateBasicTableFile(extractedDir, busDir, "config", false, 2, "基础配置", diagnostics);
        validateBasicTableFile(extractedDir, busDir, "Vchinfo", false, 2, "车载语音配置", diagnostics);

        List<String> lineNames = new ArrayList<>(lineInfoValidation.lineNames);
        File[] children = busDir.listFiles();
        List<File> lineDirs = new ArrayList<>();
        if (children != null) {
            for (File child : children) {
                if (child != null && child.isDirectory()) {
                    lineDirs.add(child);
                    if (!lineNames.contains(child.getName())) {
                        lineNames.add(child.getName());
                    }
                }
            }
        }
        lineDirs.sort((left, right) -> left.getName().compareToIgnoreCase(right.getName()));
        for (File lineDir : lineDirs) {
            if (!lineInfoValidation.lineNames.contains(lineDir.getName())) {
                String similarLineName = findSimilarLineName(lineDir.getName(), lineInfoValidation.lineNames);
                diagnostics.add(new DiagnosticItem(
                        DiagnosticItem.LEVEL_WARN,
                        relativize(extractedDir, lineDir),
                        similarLineName == null
                                ? "目录存在，但 lineInfo 表格里没有这条线路"
                                : "目录存在，但 lineInfo 表格里没有这条线路；疑似对应 lineInfo 里的 " + similarLineName
                ));
            }
        }
        for (String lineName : lineNames) {
            validateLineDirectory(extractedDir, busDir, lineName, diagnostics);
        }
        return diagnostics;
    }

    private LineInfoValidation validateLineInfoFile(File extractedDir, File busDir, List<DiagnosticItem> diagnostics) {
        File lineInfoFile = resolveTabularFile(busDir, "lineInfo");
        String target = relativize(extractedDir, lineInfoFile != null ? lineInfoFile : new File(busDir, "lineInfo"));
        List<List<String>> rows = readTableRows(lineInfoFile);
        LinkedHashSet<String> lineNames = new LinkedHashSet<>();
        if (lineInfoFile == null || !lineInfoFile.exists()) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, "缺少线路索引文件，支持 csv/xls/xlsx/xml"));
            return new LineInfoValidation(lineNames);
        }
        if (rows.isEmpty()) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, "文件为空或表格格式无法识别"));
            return new LineInfoValidation(lineNames);
        }
        List<String> header = rows.get(0);
        if (header.size() < 4) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, "表头列数不足，期望至少 4 列"));
            return new LineInfoValidation(lineNames);
        }
        int badRowCount = 0;
        List<String> rowIssues = new ArrayList<>();
        Map<String, Integer> firstLineNameRows = new LinkedHashMap<>();
        for (int index = 1; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            List<String> issues = validateLineInfoRow(header, row);
            String lineName = cell(row, 1);
            if (issues.isEmpty() && !lineName.isEmpty()) {
                String duplicateKey = normalizeLineName(lineName);
                Integer firstRow = firstLineNameRows.get(duplicateKey);
                if (firstRow != null) {
                    issues.add("第2列[" + resolveColumnLabel(header, 1, "线路名称") + "]重复，与第" + firstRow + "行重复");
                } else {
                    firstLineNameRows.put(duplicateKey, index + 1);
                }
            }
            if (!issues.isEmpty()) {
                badRowCount++;
                if (rowIssues.size() < 12) {
                    rowIssues.add(buildRowIssue(header, row, index + 1, issues));
                }
                continue;
            }
            lineNames.add(lineName);
        }
        if (lineNames.isEmpty()) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, formatIssueBlock("没有可用线路记录，异常行=" + badRowCount, rowIssues)));
            return new LineInfoValidation(lineNames);
        }
        if (badRowCount > 0) {
            diagnostics.add(new DiagnosticItem(
                    DiagnosticItem.LEVEL_WARN,
                    target,
                    formatIssueBlock("可用线路=" + lineNames.size() + "，异常行=" + badRowCount, rowIssues)
            ));
        } else {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_OK, target, "线路索引正常，可用线路=" + lineNames.size()));
        }
        return new LineInfoValidation(lineNames);
    }

    private void validateBasicTableFile(File extractedDir, File dir, String baseName, boolean required, int minColumns, String label, List<DiagnosticItem> diagnostics) {
        File file = resolveTabularFile(dir, baseName);
        String target = relativize(extractedDir, file != null ? file : new File(dir, baseName));
        if (file == null || !file.exists()) {
            diagnostics.add(new DiagnosticItem(required ? DiagnosticItem.LEVEL_FAIL : DiagnosticItem.LEVEL_WARN, target, "缺少" + label + "文件"));
            return;
        }
        List<List<String>> rows = readTableRows(file);
        if (rows.isEmpty()) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, label + "文件为空或格式无法识别"));
            return;
        }
        int badRowCount = 0;
        for (int index = 0; index < rows.size(); index++) {
            if (rows.get(index).size() < minColumns) {
                badRowCount++;
            }
        }
        if (badRowCount > 0) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_WARN, target, label + "存在 " + badRowCount + " 行列数不足"));
            return;
        }
        diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_OK, target, label + "格式正常，记录数=" + Math.max(0, rows.size() - 1)));
    }

    private void validateLineDirectory(File extractedDir, File busDir, String lineName, List<DiagnosticItem> diagnostics) {
        File lineDir = new File(busDir, lineName);
        String lineDirTarget = relativize(extractedDir, lineDir);
        if (!lineDir.exists() || !lineDir.isDirectory()) {
            List<String> existingDirNames = new ArrayList<>();
            File[] children = busDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child != null && child.isDirectory()) {
                        existingDirNames.add(child.getName());
                    }
                }
            }
            String similarDirName = findSimilarLineName(lineName, existingDirNames);
            diagnostics.add(new DiagnosticItem(
                    DiagnosticItem.LEVEL_FAIL,
                    lineDirTarget,
                    similarDirName == null
                        ? "lineInfo 表格中存在该线路，但目录缺失；期望站点文件：" + formatExpectedStationFileNames(lineName)
                        : "lineInfo 表格中存在该线路，但目录缺失；疑似目录名为 " + similarDirName + "；期望站点文件：" + formatExpectedStationFileNames(lineName)
            ));
            return;
        }

        File upFile = resolveTabularFile(lineDir, lineName + "S");
        File downFile = resolveTabularFile(lineDir, lineName + "X");
        boolean hasUp = upFile != null && upFile.exists() && upFile.isFile();
        boolean hasDown = downFile != null && downFile.exists() && downFile.isFile();
        if (!hasUp && !hasDown) {
            diagnostics.add(new DiagnosticItem(
                    DiagnosticItem.LEVEL_FAIL,
                    lineDirTarget,
                    "上下行站点文件都不存在；期望站点文件：" + formatExpectedStationFileNames(lineName)
            ));
            return;
        }
        if (!hasUp || !hasDown) {
            diagnostics.add(new DiagnosticItem(
                    DiagnosticItem.LEVEL_WARN,
                    lineDirTarget,
                    !hasUp
                            ? "缺少上行站点文件；期望文件：" + formatExpectedTabularFileNames(lineName + "S")
                            : "缺少下行站点文件；期望文件：" + formatExpectedTabularFileNames(lineName + "X")
            ));
        } else {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_OK, lineDirTarget, "线路目录正常"));
        }

        if (hasUp) {
            validateStationCsvFile(extractedDir, upFile, diagnostics);
        }
        if (hasDown) {
            validateStationCsvFile(extractedDir, downFile, diagnostics);
        }

        File upRemind = resolveTabularFile(lineDir, lineName + "SRemind");
        File downRemind = resolveTabularFile(lineDir, lineName + "XRemind");
        if (upRemind != null && upRemind.exists() && upRemind.isFile()) {
            validateReminderCsvFile(extractedDir, upRemind, diagnostics);
        }
        if (downRemind != null && downRemind.exists() && downRemind.isFile()) {
            validateReminderCsvFile(extractedDir, downRemind, diagnostics);
        }
    }

    private void validateStationCsvFile(File extractedDir, File file, List<DiagnosticItem> diagnostics) {
        String target = relativize(extractedDir, file);
        List<List<String>> rows = readTableRows(file);
        if (rows.isEmpty()) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, "文件为空或格式无法识别"));
            return;
        }
        List<String> header = rows.get(0);
        if (header.size() < 6) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, "表头列数不足，期望至少 6 列（站台编号/报站语音/站名/经度/纬度/角度）"));
            return;
        }
        int usableRows = 0;
        int badRowCount = 0;
        List<String> rowIssues = new ArrayList<>();
        for (int index = 1; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            List<String> issues = validateStationRow(header, row);
            if (!issues.isEmpty()) {
                badRowCount++;
                if (rowIssues.size() < 12) {
                    rowIssues.add(buildRowIssue(header, row, index + 1, issues));
                }
                continue;
            }
            usableRows++;
        }
        if (usableRows == 0) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, formatIssueBlock("没有有效站点数据，异常行=" + badRowCount, rowIssues)));
            return;
        }
        if (badRowCount > 0) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_WARN, target, formatIssueBlock("有效站点=" + usableRows + "，异常行=" + badRowCount, rowIssues)));
            return;
        }
        diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_OK, target, "站点文件正常，站点数=" + usableRows));
    }

    private void validateReminderCsvFile(File extractedDir, File file, List<DiagnosticItem> diagnostics) {
        String target = relativize(extractedDir, file);
        List<List<String>> rows = readTableRows(file);
        if (rows.isEmpty()) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, "提醒文件为空或格式无法识别"));
            return;
        }
        List<String> header = rows.get(0);
        if (header.size() < 4) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, "提醒文件表头列数不足，期望至少 4 列（编号/提醒语音/经度/纬度）"));
            return;
        }
        int usableRows = 0;
        int badRowCount = 0;
        List<String> rowIssues = new ArrayList<>();
        for (int index = 1; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            List<String> issues = validateReminderRow(header, row);
            if (!issues.isEmpty()) {
                badRowCount++;
                if (rowIssues.size() < 12) {
                    rowIssues.add(buildRowIssue(header, row, index + 1, issues));
                }
                continue;
            }
            usableRows++;
        }
        if (usableRows == 0) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, target, formatIssueBlock("没有有效提醒数据，异常行=" + badRowCount, rowIssues)));
            return;
        }
        if (badRowCount > 0) {
            diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_WARN, target, formatIssueBlock("有效提醒=" + usableRows + "，异常行=" + badRowCount, rowIssues)));
            return;
        }
        diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_OK, target, "提醒文件正常，提醒数=" + usableRows));
    }

    private List<String> validateLineInfoRow(List<String> header, List<String> row) {
        List<String> issues = new ArrayList<>();
        collectIdentifierIssues(issues, header, row);
        collectRequiredTextIssue(issues, header, row, 1, "线路名称");
        return issues;
    }

    private List<String> validateStationRow(List<String> header, List<String> row) {
        List<String> issues = new ArrayList<>();
        collectIdentifierIssues(issues, header, row);
        if (isBlankCell(row, 1) && isBlankCell(row, 2)) {
            collectRequiredTextIssue(issues, header, row, 1, "报站语音");
            collectRequiredTextIssue(issues, header, row, 2, "站名");
        }
        collectCoordinateIssues(issues, header, row, 3, "经度");
        collectCoordinateIssues(issues, header, row, 4, "纬度");
        collectAngleIssues(issues, header, row, 5, "角度");
        return issues;
    }

    private List<String> validateReminderRow(List<String> header, List<String> row) {
        List<String> issues = new ArrayList<>();
        collectIdentifierIssues(issues, header, row);
        collectRequiredTextIssue(issues, header, row, 1, "提醒语音");
        collectCoordinateIssues(issues, header, row, 2, "经度");
        collectCoordinateIssues(issues, header, row, 3, "纬度");
        return issues;
    }

    private void collectIdentifierIssues(List<String> issues, List<String> header, List<String> row) {
        if (issues == null || !hasIdentifierColumn(header)) {
            return;
        }
        if (!hasColumn(row, 0)) {
            issues.add(buildColumnIssue(header, 0, "编号", "缺失"));
            return;
        }
        String value = cell(row, 0);
        if (value.isEmpty()) {
            issues.add(buildColumnIssue(header, 0, "编号", "为空"));
            return;
        }
        if (parseInteger(value) == null) {
            issues.add(buildColumnIssue(header, 0, "编号", "格式不对，应为整数"));
        }
    }

    private void collectRequiredTextIssue(List<String> issues, List<String> header, List<String> row, int columnIndex, String fallbackLabel) {
        if (issues == null) {
            return;
        }
        if (!hasColumn(row, columnIndex)) {
            issues.add(buildColumnIssue(header, columnIndex, fallbackLabel, "缺失"));
            return;
        }
        if (cell(row, columnIndex).isEmpty()) {
            issues.add(buildColumnIssue(header, columnIndex, fallbackLabel, "为空"));
        }
    }

    private void collectCoordinateIssues(List<String> issues, List<String> header, List<String> row, int columnIndex, String fallbackLabel) {
        if (issues == null) {
            return;
        }
        if (!hasColumn(row, columnIndex)) {
            issues.add(buildColumnIssue(header, columnIndex, fallbackLabel, "缺失"));
            return;
        }
        String value = cell(row, columnIndex);
        if (value.isEmpty()) {
            issues.add(buildColumnIssue(header, columnIndex, fallbackLabel, "为空"));
            return;
        }
        if (parseDouble(value) == null) {
            issues.add(buildColumnIssue(header, columnIndex, fallbackLabel, "格式不对，应为数字"));
        }
    }

    private void collectAngleIssues(List<String> issues, List<String> header, List<String> row, int columnIndex, String fallbackLabel) {
        if (issues == null) {
            return;
        }
        if (!hasColumn(row, columnIndex)) {
            issues.add(buildColumnIssue(header, columnIndex, fallbackLabel, "缺失"));
            return;
        }
        String value = cell(row, columnIndex);
        if (value.isEmpty()) {
            issues.add(buildColumnIssue(header, columnIndex, fallbackLabel, "为空"));
            return;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if ("N".equals(normalized) || "S".equals(normalized) || "E".equals(normalized) || "W".equals(normalized)
                || "NE".equals(normalized) || "NW".equals(normalized) || "SE".equals(normalized) || "SW".equals(normalized)) {
            return;
        }
        Double numeric = parseDouble(normalized);
        if (numeric == null || numeric < 0d || numeric > 360d) {
            issues.add(buildColumnIssue(header, columnIndex, fallbackLabel, "格式不对，应为 N/S/E/W 或 0-360 数值"));
        }
    }

    private boolean isBlankCell(List<String> row, int columnIndex) {
        return !hasColumn(row, columnIndex) || cell(row, columnIndex).isEmpty();
    }

    private boolean hasColumn(List<String> row, int columnIndex) {
        return row != null && columnIndex >= 0 && columnIndex < row.size();
    }

    private boolean hasIdentifierColumn(List<String> header) {
        return header != null && !header.isEmpty() && cell(header, 0).contains("编号");
    }

    private String buildRowIssue(List<String> header, List<String> row, int rowNumber, List<String> issues) {
        StringBuilder builder = new StringBuilder();
        builder.append("第").append(rowNumber).append("行");
        String identifierTitle = hasIdentifierColumn(header) ? cell(header, 0) : "";
        if (!identifierTitle.isEmpty()) {
            String identifierValue = hasColumn(row, 0) ? cell(row, 0) : "";
            builder.append(" [")
                    .append(identifierTitle)
                    .append("=")
                    .append(identifierValue.isEmpty() ? "空" : identifierValue)
                    .append("]");
        }
        builder.append(" ").append(joinIssues(issues));
        return builder.toString();
    }

    private String buildColumnIssue(List<String> header, int columnIndex, String fallbackLabel, String problem) {
        return "第" + (columnIndex + 1) + "列[" + resolveColumnLabel(header, columnIndex, fallbackLabel) + "]" + problem;
    }

    private String findSimilarLineName(String target, Collection<String> candidates) {
        if (target == null || target.trim().isEmpty() || candidates == null || candidates.isEmpty()) {
            return null;
        }
        String normalizedTarget = normalizeLineName(target);
        if (normalizedTarget.isEmpty()) {
            return null;
        }
        for (String candidate : candidates) {
            if (candidate == null || candidate.trim().isEmpty()) {
                continue;
            }
            if (!target.equals(candidate) && normalizedTarget.equals(normalizeLineName(candidate))) {
                return candidate;
            }
        }
        return null;
    }

    private String normalizeLineName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return value.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    private String formatExpectedStationFileNames(String lineName) {
        return formatExpectedTabularFileNames(lineName + "S") + "、" + formatExpectedTabularFileNames(lineName + "X");
    }

    private String formatExpectedTabularFileNames(String baseName) {
        List<String> fileNames = new ArrayList<>();
        for (String extension : SUPPORTED_TABLE_EXTENSIONS) {
            fileNames.add(baseName + extension);
        }
        return join(new LinkedHashSet<>(fileNames));
    }

    private String resolveColumnLabel(List<String> header, int columnIndex, String fallbackLabel) {
        if (header != null && columnIndex >= 0 && columnIndex < header.size()) {
            String value = cell(header, columnIndex);
            if (!value.isEmpty()) {
                return value;
            }
        }
        return fallbackLabel;
    }

    private String joinIssues(List<String> issues) {
        if (issues == null || issues.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String issue : new LinkedHashSet<>(issues)) {
            if (builder.length() > 0) {
                builder.append('、');
            }
            builder.append(issue);
        }
        return builder.toString();
    }

    private String formatIssueList(List<String> issues) {
        if (issues == null || issues.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < issues.size(); index++) {
            builder.append("\n")
                    .append(index + 1)
                    .append(". ")
                    .append(issues.get(index));
        }
        return builder.toString();
    }

    private String formatIssueBlock(String summary, List<String> issues) {
        String normalizedSummary = summary == null ? "" : summary.trim();
        if (issues == null || issues.isEmpty()) {
            return normalizedSummary;
        }
        if (normalizedSummary.isEmpty()) {
            return formatIssueList(issues).trim();
        }
        return normalizedSummary + formatIssueList(issues);
    }

    private boolean hasFailureDiagnostics(List<DiagnosticItem> diagnostics) {
        if (diagnostics == null || diagnostics.isEmpty()) {
            return false;
        }
        for (DiagnosticItem item : diagnostics) {
            if (item != null && DiagnosticItem.LEVEL_FAIL.equals(item.getLevel())) {
                return true;
            }
        }
        return false;
    }

    private String buildDiagnosticSummary(List<DiagnosticItem> diagnostics) {
        int okCount = 0;
        int warnCount = 0;
        int failCount = 0;
        if (diagnostics != null) {
            for (DiagnosticItem item : diagnostics) {
                if (item == null) {
                    continue;
                }
                if (DiagnosticItem.LEVEL_OK.equals(item.getLevel())) {
                    okCount++;
                } else if (DiagnosticItem.LEVEL_FAIL.equals(item.getLevel())) {
                    failCount++;
                } else {
                    warnCount++;
                }
            }
        }
        return "诊断结果：OK " + okCount + " 项，警告 " + warnCount + " 项，失败 " + failCount + " 项";
    }

    private String formatExamples(List<String> examples) {
        if (examples == null || examples.isEmpty()) {
            return "";
        }
        return "，例如：" + join(new LinkedHashSet<>(examples));
    }

    private boolean hasBusPayload(File busDir) {
        if (busDir == null || !busDir.exists() || !busDir.isDirectory()) {
            return false;
        }
        File[] children = busDir.listFiles();
        if (children == null || children.length == 0) {
            return false;
        }
        for (File child : children) {
            if (child.isDirectory()) {
                return true;
            }
            String name = child.getName();
            if (isNamedTableFile(name, "lineInfo")
                    || isNamedTableFile(name, "config")
                    || isNamedTableFile(name, "Vchinfo")) {
                return true;
            }
        }
        return false;
    }

    private List<String> describeArchiveEntries(File extractedDir, List<File> extractedFiles) {
        List<String> entries = new ArrayList<>();
        if (extractedFiles == null || extractedFiles.isEmpty()) {
            return entries;
        }
        for (File file : extractedFiles) {
            String relative = relativize(extractedDir, file);
            if (!entries.contains(relative)) {
                entries.add(relative);
            }
            if (entries.size() >= 12) {
                break;
            }
        }
        return entries;
    }

    private File findExtractedFile(List<File> extractedFiles, String fileName) {
        for (File file : extractedFiles) {
            if (fileName.equalsIgnoreCase(file.getName())) {
                return file;
            }
        }
        return null;
    }

    private File findExtractedTableFile(List<File> extractedFiles, String baseName) {
        if (extractedFiles == null || extractedFiles.isEmpty()) {
            return null;
        }
        for (String extension : SUPPORTED_TABLE_EXTENSIONS) {
            for (File file : extractedFiles) {
                if (file != null && file.isFile() && (baseName + extension).equalsIgnoreCase(file.getName())) {
                    return file;
                }
            }
        }
        return null;
    }

    private File resolveTabularFile(File dir, String baseName) {
        if (dir == null || baseName == null || !dir.exists() || !dir.isDirectory()) {
            return null;
        }
        File[] children = dir.listFiles();
        if (children == null || children.length == 0) {
            return null;
        }
        for (String extension : SUPPORTED_TABLE_EXTENSIONS) {
            for (File child : children) {
                if (child != null && child.isFile() && (baseName + extension).equalsIgnoreCase(child.getName())) {
                    return child;
                }
            }
        }
        return null;
    }

    private boolean isNamedTableFile(String fileName, String baseName) {
        if (fileName == null || baseName == null) {
            return false;
        }
        for (String extension : SUPPORTED_TABLE_EXTENSIONS) {
            if ((baseName + extension).equalsIgnoreCase(fileName)) {
                return true;
            }
        }
        return false;
    }

    private LinkedHashSet<String> parseLineNamesFromLineInfo(File file) {
        LinkedHashSet<String> values = new LinkedHashSet<>();
        if (file == null || !file.exists()) {
            return values;
        }
        List<List<String>> rows = readTableRows(file);
        for (int index = 1; index < rows.size(); index++) {
            String lineName = cell(rows.get(index), 1);
            if (!lineName.isEmpty()) {
                values.add(lineName);
            }
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
            List<String> scanPaths,
            int importedMessageCount
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
            writer.write("messageCount=" + importedMessageCount + "\n");
            writer.write("scannedPaths=\n" + describePaths(scanPaths) + "\n");
            writer.write("files=\n");
            for (File file : extractedFiles) {
                writer.write("- " + relativize(extractedDir, file) + "\n");
            }
            writer.flush();
        }
    }

    private int importMessageCatalog(Context context, List<File> extractedFiles) {
        if (context == null || extractedFiles == null || extractedFiles.isEmpty()) {
            return 0;
        }
        File messageFile = findExtractedTableFile(extractedFiles, "Message");
        if (isMessageTableFile(messageFile)) {
            return importMessageTable(context, messageFile);
        }
        return 0;
    }

    private boolean isMessageTableFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        List<List<String>> rows = readTableRows(file);
        if (rows.isEmpty()) {
            return false;
        }
        return isMessageHeader(rows.get(0));
    }

    private int importMessageTable(Context context, File file) {
        List<List<String>> rows = readTableRows(file);
        if (rows.isEmpty() || !isMessageHeader(rows.get(0))) {
            return 0;
        }
        LegacyInfoMessageRepository.clear(context);
        String sourceType = resolveTableFileType(file);
        int importedCount = 0;
        for (int index = 1; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            Integer messageNo = parseInteger(cell(row, 0));
            String messageTime = cell(row, 1);
            String messageContent = cell(row, 2);
            if (messageNo == null || messageContent.isEmpty()) {
                continue;
            }
            LegacyInfoMessageRepository.upsert(context, messageNo, messageTime, messageContent, file.getAbsolutePath(), sourceType);
            importedCount++;
        }
        return importedCount;
    }

    private String resolveTableFileType(File file) {
        if (file == null) {
            return "unknown";
        }
        String name = file.getName().toLowerCase(Locale.ROOT);
        int index = name.lastIndexOf('.');
        if (index < 0 || index >= name.length() - 1) {
            return "unknown";
        }
        return name.substring(index + 1);
    }

    private List<List<String>> readTableRows(File file) {
        List<List<String>> rows = new ArrayList<>();
        if (file == null || !file.exists() || !file.isFile()) {
            return rows;
        }
        String lowerName = file.getName().toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".csv")) {
            return readCsvRows(file);
        }
        if (lowerName.endsWith(".xls")) {
            return readXlsRows(file, 0);
        }
        if (lowerName.endsWith(".xlsx")) {
            return readXlsxRows(file, 0);
        }
        if (lowerName.endsWith(".xml")) {
            return readExcelXmlRows(file, 0);
        }
        if (lowerName.endsWith(".xlx")) {
            rows = readXlsRows(file, 0);
            if (!rows.isEmpty()) {
                return rows;
            }
            rows = readXlsxRows(file, 0);
            if (!rows.isEmpty()) {
                return rows;
            }
            return readExcelXmlRows(file, 0);
        }
        return rows;
    }

    private List<List<String>> readCsvRows(File file) {
        List<List<String>> rows = new ArrayList<>();
        if (file == null || !file.exists() || !file.isFile()) {
            return rows;
        }
        try {
            String content = decodeCsvText(Files.readAllBytes(file.toPath()));
            BufferedReader reader = new BufferedReader(new StringReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = stripBom(line).trim();
                if (normalized.isEmpty()) {
                    continue;
                }
                rows.add(parseCsvRow(normalized));
            }
        } catch (Exception ignore) {
            return new ArrayList<>();
        }
        return rows;
    }

    private String decodeCsvText(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        if (hasUtf8Bom(bytes)) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        if (canDecodeUtf8(bytes)) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return new String(bytes, LEGACY_CSV_CHARSET);
    }

    private boolean hasUtf8Bom(byte[] bytes) {
        return bytes != null
                && bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xEF
                && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF;
    }

    private boolean canDecodeUtf8(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return false;
        }
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            decoder.decode(ByteBuffer.wrap(bytes));
            return true;
        } catch (CharacterCodingException ignore) {
            return false;
        }
    }

    private List<List<String>> readXlsRows(File file, int startRow) {
        List<List<String>> rows = new ArrayList<>();
        if (file == null || !file.exists() || !file.isFile()) {
            return rows;
        }
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
             HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(inputStream))) {
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
                for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    HSSFRow row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;
                    }
                    List<String> values = new ArrayList<>();
                    boolean hasContent = false;
                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        HSSFCell cell = row.getCell(cellIndex);
                        String value = readSpreadsheetCell(cell);
                        if (cellIndex > 0 && value.isEmpty()) {
                            break;
                        }
                        if (!value.isEmpty()) {
                            hasContent = true;
                        }
                        values.add(value);
                    }
                    if (hasContent) {
                        rows.add(values);
                    }
                }
            }
        } catch (Exception ignore) {
            return new ArrayList<>();
        }
        return rows;
    }

    private List<List<String>> readXlsxRows(File file, int startRow) {
        List<List<String>> rows = new ArrayList<>();
        if (file == null || !file.exists() || !file.isFile()) {
            return rows;
        }
        try (ZipFile zipFile = new ZipFile(file)) {
            List<String> sharedStrings = loadXlsxSharedStrings(zipFile);
            List<String> sheetEntryNames = listXlsxSheetEntryNames(zipFile);
            for (String sheetEntryName : sheetEntryNames) {
                ZipEntry sheetEntry = zipFile.getEntry(sheetEntryName);
                if (sheetEntry == null) {
                    continue;
                }
                try (InputStream inputStream = zipFile.getInputStream(sheetEntry)) {
                    Document document = parseXmlDocument(inputStream);
                    NodeList rowNodes = document.getElementsByTagNameNS("*", "row");
                    for (int rowIndex = startRow; rowIndex < rowNodes.getLength(); rowIndex++) {
                        Node rowNode = rowNodes.item(rowIndex);
                        if (!(rowNode instanceof Element)) {
                            continue;
                        }
                        List<String> values = new ArrayList<>();
                        boolean hasContent = false;
                        int expectedIndex = 1;
                        NodeList children = rowNode.getChildNodes();
                        for (int childIndex = 0; childIndex < children.getLength(); childIndex++) {
                            Node child = children.item(childIndex);
                            if (!(child instanceof Element) || !"c".equals(child.getLocalName())) {
                                continue;
                            }
                            Element cellElement = (Element) child;
                            Integer declaredIndex = parseXlsxColumnIndex(attributeIgnoreNamespace(cellElement, "r"));
                            if (declaredIndex == null || declaredIndex <= 0) {
                                declaredIndex = expectedIndex;
                            }
                            while (expectedIndex < declaredIndex) {
                                values.add("");
                                expectedIndex++;
                            }
                            String value = rightTrim(extractXlsxCellValue(cellElement, sharedStrings));
                            if (!value.isEmpty()) {
                                hasContent = true;
                            }
                            values.add(value);
                            expectedIndex++;
                        }
                        trimTrailingEmpty(values);
                        if (hasContent) {
                            rows.add(values);
                        }
                    }
                }
            }
        } catch (Exception ignore) {
            return new ArrayList<>();
        }
        return rows;
    }

    private List<String> loadXlsxSharedStrings(ZipFile zipFile) throws Exception {
        List<String> sharedStrings = new ArrayList<>();
        if (zipFile == null) {
            return sharedStrings;
        }
        ZipEntry entry = zipFile.getEntry("xl/sharedStrings.xml");
        if (entry == null) {
            return sharedStrings;
        }
        try (InputStream inputStream = zipFile.getInputStream(entry)) {
            Document document = parseXmlDocument(inputStream);
            NodeList stringNodes = document.getElementsByTagNameNS("*", "si");
            for (int index = 0; index < stringNodes.getLength(); index++) {
                Node stringNode = stringNodes.item(index);
                sharedStrings.add(extractXlsxSharedString(stringNode));
            }
        }
        return sharedStrings;
    }

    private String extractXlsxSharedString(Node stringNode) {
        if (stringNode == null) {
            return "";
        }
        if (!(stringNode instanceof Element)) {
            return stripBom(stringNode.getTextContent()).trim();
        }
        Element element = (Element) stringNode;
        NodeList textNodes = element.getElementsByTagNameNS("*", "t");
        if (textNodes == null || textNodes.getLength() == 0) {
            return stripBom(element.getTextContent()).trim();
        }
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < textNodes.getLength(); index++) {
            Node textNode = textNodes.item(index);
            if (textNode != null && textNode.getTextContent() != null) {
                builder.append(textNode.getTextContent());
            }
        }
        return stripBom(builder.toString()).trim();
    }

    private List<String> listXlsxSheetEntryNames(ZipFile zipFile) {
        List<String> sheetEntryNames = new ArrayList<>();
        if (zipFile == null) {
            return sheetEntryNames;
        }
        java.util.Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry == null || entry.isDirectory()) {
                continue;
            }
            String name = entry.getName();
            if (name != null && name.startsWith("xl/worksheets/") && name.endsWith(".xml")) {
                sheetEntryNames.add(name);
            }
        }
        Collections.sort(sheetEntryNames);
        return sheetEntryNames;
    }

    private Integer parseXlsxColumnIndex(String cellRef) {
        if (cellRef == null || cellRef.trim().isEmpty()) {
            return null;
        }
        int value = 0;
        boolean foundLetter = false;
        for (int index = 0; index < cellRef.length(); index++) {
            char c = cellRef.charAt(index);
            if (Character.isLetter(c)) {
                foundLetter = true;
                value = (value * 26) + (Character.toUpperCase(c) - 'A' + 1);
            } else if (foundLetter) {
                break;
            }
        }
        return foundLetter ? value : null;
    }

    private String extractXlsxCellValue(Element cellElement, List<String> sharedStrings) {
        if (cellElement == null) {
            return "";
        }
        String type = attributeIgnoreNamespace(cellElement, "t");
        if ("inlineStr".equalsIgnoreCase(type)) {
            NodeList inlineNodes = cellElement.getElementsByTagNameNS("*", "t");
            StringBuilder builder = new StringBuilder();
            for (int index = 0; inlineNodes != null && index < inlineNodes.getLength(); index++) {
                Node inlineNode = inlineNodes.item(index);
                if (inlineNode != null && inlineNode.getTextContent() != null) {
                    builder.append(inlineNode.getTextContent());
                }
            }
            return stripBom(builder.toString()).trim();
        }
        NodeList valueNodes = cellElement.getElementsByTagNameNS("*", "v");
        String rawValue = valueNodes != null && valueNodes.getLength() > 0 && valueNodes.item(0) != null
                ? stripBom(valueNodes.item(0).getTextContent()).trim()
                : "";
        if (rawValue.isEmpty()) {
            return "";
        }
        if ("s".equalsIgnoreCase(type)) {
            Integer stringIndex = parseInteger(rawValue);
            if (stringIndex != null && stringIndex >= 0 && sharedStrings != null && stringIndex < sharedStrings.size()) {
                return rightTrim(sharedStrings.get(stringIndex));
            }
            return "";
        }
        if ("b".equalsIgnoreCase(type)) {
            return "1".equals(rawValue) ? "Y" : "N";
        }
        if ("str".equalsIgnoreCase(type)) {
            return rightTrim(rawValue);
        }
        Double numeric = parseDouble(rawValue);
        if (numeric == null) {
            return rightTrim(rawValue);
        }
        if (numeric == Math.rint(numeric)) {
            return new DecimalFormat("0").format(numeric).trim();
        }
        return new DecimalFormat("0.########").format(numeric).trim();
    }

    private List<List<String>> readExcelXmlRows(File file, int startRow) {
        List<List<String>> rows = new ArrayList<>();
        if (file == null || !file.exists() || !file.isFile()) {
            return rows;
        }
        try {
            Document document = parseXmlDocument(new FileInputStream(file));
            NodeList rowNodes = document.getElementsByTagNameNS("*", "Row");
            for (int rowIndex = startRow; rowIndex < rowNodes.getLength(); rowIndex++) {
                Node rowNode = rowNodes.item(rowIndex);
                if (!(rowNode instanceof Element)) {
                    continue;
                }
                List<String> values = new ArrayList<>();
                boolean hasContent = false;
                int expectedIndex = 1;
                NodeList children = rowNode.getChildNodes();
                for (int childIndex = 0; childIndex < children.getLength(); childIndex++) {
                    Node child = children.item(childIndex);
                    if (!(child instanceof Element) || !"Cell".equals(child.getLocalName())) {
                        continue;
                    }
                    Element cellElement = (Element) child;
                    int declaredIndex = parseInteger(attributeIgnoreNamespace(cellElement, "Index")) == null
                            ? expectedIndex
                            : parseInteger(attributeIgnoreNamespace(cellElement, "Index"));
                    while (expectedIndex < declaredIndex) {
                        values.add("");
                        expectedIndex++;
                    }
                    String value = rightTrim(extractXmlCellValue(cellElement));
                    if (!value.isEmpty()) {
                        hasContent = true;
                    }
                    values.add(value);
                    expectedIndex++;
                }
                trimTrailingEmpty(values);
                if (hasContent) {
                    rows.add(values);
                }
            }
        } catch (Exception ignore) {
            return new ArrayList<>();
        }
        return rows;
    }

    private Document parseXmlDocument(InputStream inputStream) throws Exception {
        try (InputStream stream = inputStream) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            trySetXmlFeature(factory, XMLConstants.FEATURE_SECURE_PROCESSING, true);
            trySetXmlFeature(factory, "http://apache.org/xml/features/disallow-doctype-decl", true);
            trySetXmlFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
            trySetXmlFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
            return factory.newDocumentBuilder().parse(stream);
        }
    }

    private void trySetXmlFeature(DocumentBuilderFactory factory, String feature, boolean enabled) {
        if (factory == null || feature == null || feature.trim().isEmpty()) {
            return;
        }
        try {
            factory.setFeature(feature, enabled);
        } catch (Exception ignore) {
            // Some Android XML factories do not expose the same feature set as desktop JDKs.
        }
    }

    private String readSpreadsheetCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return date == null ? "" : new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
                }
                double numeric = cell.getNumericCellValue();
                if (numeric == Math.rint(numeric)) {
                    return new DecimalFormat("0").format(numeric).trim();
                }
                return new DecimalFormat("0.########").format(numeric).trim();
            case HSSFCell.CELL_TYPE_STRING:
                return rightTrim(cell.getStringCellValue());
            case HSSFCell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? "Y" : "N";
            case HSSFCell.CELL_TYPE_FORMULA:
                try {
                    return rightTrim(cell.getStringCellValue());
                } catch (Exception ignore) {
                    return new DecimalFormat("0").format(cell.getNumericCellValue()).trim();
                }
            default:
                return "";
        }
    }

    private String attributeIgnoreNamespace(Element element, String localName) {
        if (element == null || localName == null) {
            return "";
        }
        String direct = element.getAttribute(localName);
        if (direct != null && !direct.trim().isEmpty()) {
            return direct.trim();
        }
        Node node = element.getAttributes() == null ? null : element.getAttributes().getNamedItem("ss:" + localName);
        if (node != null && node.getNodeValue() != null && !node.getNodeValue().trim().isEmpty()) {
            return node.getNodeValue().trim();
        }
        for (int index = 0; element.getAttributes() != null && index < element.getAttributes().getLength(); index++) {
            Node attribute = element.getAttributes().item(index);
            if (attribute == null) {
                continue;
            }
            String nodeName = attribute.getNodeName();
            String attrLocalName = attribute.getLocalName();
            if (localName.equalsIgnoreCase(nodeName) || localName.equalsIgnoreCase(attrLocalName) || (nodeName != null && nodeName.endsWith(":" + localName))) {
                return attribute.getNodeValue() == null ? "" : attribute.getNodeValue().trim();
            }
        }
        return "";
    }

    private String extractXmlCellValue(Element cellElement) {
        if (cellElement == null) {
            return "";
        }
        NodeList dataNodes = cellElement.getElementsByTagNameNS("*", "Data");
        if (dataNodes != null && dataNodes.getLength() > 0) {
            Node dataNode = dataNodes.item(0);
            return dataNode == null ? "" : stripBom(dataNode.getTextContent()).trim();
        }
        return stripBom(cellElement.getTextContent()).trim();
    }

    private void trimTrailingEmpty(List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        int index = values.size() - 1;
        while (index >= 0) {
            String value = values.get(index);
            if (value != null && !value.trim().isEmpty()) {
                break;
            }
            values.remove(index);
            index--;
        }
    }

    private boolean isMessageHeader(List<String> row) {
        return equalsIgnoreCase(cell(row, 0), "NO.")
                && equalsIgnoreCase(cell(row, 1), "Date")
                && equalsIgnoreCase(cell(row, 2), "Content");
    }

    private List<String> parseCsvRow(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int index = 0; index < line.length(); index++) {
            char value = line.charAt(index);
            if (value == '"') {
                if (inQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (value == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(value);
            }
        }
        values.add(current.toString().trim());
        return values;
    }

    private String cell(List<String> row, int index) {
        if (row == null || index < 0 || index >= row.size()) {
            return "";
        }
        String value = row.get(index);
        if (value == null) {
            return "";
        }
        String trimmed = stripBom(value).trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    private boolean equalsIgnoreCase(String left, String right) {
        return left != null && right != null && left.trim().equalsIgnoreCase(right.trim());
    }

    private String rightTrim(String value) {
        if (value == null) {
            return "";
        }
        int end = value.length();
        while (end > 0 && Character.isWhitespace(value.charAt(end - 1))) {
            end--;
        }
        return value.substring(0, end);
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
        private final List<ImportCandidate> importCandidates;
        private final ImportCandidate zipCandidate;
        private final ImportCandidate rarCandidate;
        private final List<String> allCandidatePaths;

        private CandidateScan(List<ImportCandidate> importCandidates, ImportCandidate zipCandidate, ImportCandidate rarCandidate, List<String> allCandidatePaths) {
            this.importCandidates = importCandidates == null ? new ArrayList<>() : new ArrayList<>(importCandidates);
            this.zipCandidate = zipCandidate;
            this.rarCandidate = rarCandidate;
            this.allCandidatePaths = allCandidatePaths == null ? new ArrayList<>() : new ArrayList<>(allCandidatePaths);
        }

        private List<ImportCandidate> getImportCandidates() {
            return new ArrayList<>(importCandidates);
        }
    }

    public static final class ImportCandidate {
        private final String absolutePath;
        private final String fileName;
        private final String sourceLabel;
        private final long fileSize;
        private final long lastModified;
        private final boolean bundledAsset;
        private final boolean supported;
        private final String archiveTypeLabel;
        private final String unsupportedReason;

        private ImportCandidate(File file, String sourceLabel, boolean bundledAsset) {
            this.absolutePath = file == null ? "" : file.getAbsolutePath();
            this.fileName = file == null ? "" : file.getName();
            this.sourceLabel = sourceLabel == null ? "" : sourceLabel.trim();
            this.fileSize = file == null ? 0L : file.length();
            this.lastModified = file == null ? 0L : file.lastModified();
            this.bundledAsset = bundledAsset;
            int rarVersion = isRarFileName(this.fileName) ? detectRarMajorVersion(file) : -1;
            if (isZipFileName(this.fileName)) {
                this.supported = true;
                this.archiveTypeLabel = "ZIP";
                this.unsupportedReason = "";
            } else if (isRarFileName(this.fileName)) {
                this.archiveTypeLabel = rarVersion > 0 ? ("RAR" + rarVersion) : "RAR";
                this.supported = true;
                this.unsupportedReason = "";
            } else {
                this.supported = false;
                this.archiveTypeLabel = "未知格式";
                this.unsupportedReason = "不支持的资源包格式";
            }
        }

        private static boolean isZipFileName(String fileName) {
            return fileName != null && fileName.toLowerCase(Locale.ROOT).endsWith(".zip");
        }

        private static boolean isRarFileName(String fileName) {
            return fileName != null && fileName.toLowerCase(Locale.ROOT).endsWith(".rar");
        }

        public File getFile() {
            return new File(absolutePath);
        }

        public String getAbsolutePath() {
            return absolutePath;
        }

        public String getFileName() {
            return fileName;
        }

        public String getSourceLabel() {
            return sourceLabel;
        }

        public long getFileSize() {
            return fileSize;
        }

        public long getLastModified() {
            return lastModified;
        }

        public boolean isBundledAsset() {
            return bundledAsset;
        }

        public boolean isZip() {
            return isZipFileName(fileName);
        }

        public boolean isRar() {
            return isRarFileName(fileName);
        }

        public boolean isSupported() {
            return supported;
        }

        public String getArchiveTypeLabel() {
            return archiveTypeLabel;
        }

        public String getUnsupportedReason() {
            return unsupportedReason;
        }
    }

    static final class ArchiveSelfCheck {
        private final boolean valid;
        private final List<String> problems;
        private final List<String> entries;

        private ArchiveSelfCheck(boolean valid, List<String> problems, List<String> entries) {
            this.valid = valid;
            this.problems = problems == null ? new ArrayList<>() : new ArrayList<>(problems);
            this.entries = entries == null ? new ArrayList<>() : new ArrayList<>(entries);
        }

        boolean isValid() {
            return valid;
        }

        List<DiagnosticItem> toDiagnostics() {
            List<DiagnosticItem> diagnostics = new ArrayList<>();
            for (String problem : problems) {
                diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_FAIL, "SourceFile", problem));
            }
            for (String entry : entries) {
                diagnostics.add(new DiagnosticItem(DiagnosticItem.LEVEL_WARN, entry, "压缩包里检测到该文件，但整体结构仍不完整"));
            }
            return diagnostics;
        }

        String describeFailure(File archiveFile, List<String> scanPaths) {
            StringBuilder builder = new StringBuilder();
            if (archiveFile != null) {
                builder.append("资源包=").append(archiveFile.getAbsolutePath()).append('\n');
            }
            builder.append("自检未通过:");
            for (String problem : problems) {
                builder.append("\n- ").append(problem);
            }
            if (!entries.isEmpty()) {
                builder.append("\n解压内容示例:");
                for (String entry : entries) {
                    builder.append("\n- ").append(entry);
                }
            }
            builder.append("\n期望至少包含:")
                    .append("\n- SourceFile/Bus/lineInfo.(csv/xls/xlx/xlsx/xml)")
                    .append("\n- SourceFile/Bus/config.(csv/xls/xlx/xlsx/xml)")
                    .append("\n- SourceFile/Bus/<线路目录>/...");
            if (scanPaths != null && !scanPaths.isEmpty()) {
                builder.append("\n已扫描导入路径:");
                for (String path : scanPaths) {
                    builder.append("\n- ").append(path);
                }
            }
            return builder.toString().trim();
        }
    }

    private static final class LineInfoValidation {
        private final LinkedHashSet<String> lineNames;

        private LineInfoValidation(LinkedHashSet<String> lineNames) {
            this.lineNames = lineNames == null ? new LinkedHashSet<>() : lineNames;
        }
    }

    public static final class DiagnosticItem {
        public static final String LEVEL_OK = "OK";
        public static final String LEVEL_WARN = "WARN";
        public static final String LEVEL_FAIL = "FAIL";

        private final String level;
        private final String target;
        private final String message;

        private DiagnosticItem(String level, String target, String message) {
            this.level = level == null || level.trim().isEmpty() ? LEVEL_WARN : level.trim();
            this.target = target == null ? "" : target.trim();
            this.message = message == null ? "" : message.trim();
        }

        public String getLevel() {
            return level;
        }

        public String getTarget() {
            return target;
        }

        public String getMessage() {
            return message;
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
        private final List<DiagnosticItem> diagnostics;

        private OperationResult(boolean success, String summary, String detail, File archiveFile, File workingDir, String lineName, List<String> candidatePaths, List<DiagnosticItem> diagnostics) {
            this.success = success;
            this.summary = summary == null ? "" : summary.trim();
            this.detail = detail == null ? "" : detail.trim();
            this.archiveFile = archiveFile;
            this.workingDir = workingDir;
            this.lineName = lineName == null || lineName.trim().isEmpty() ? "-" : lineName.trim();
            this.candidatePaths = candidatePaths == null ? new ArrayList<>() : new ArrayList<>(candidatePaths);
            this.diagnostics = diagnostics == null ? new ArrayList<>() : new ArrayList<>(diagnostics);
        }

        public static OperationResult success(String summary, String detail, File archiveFile, File workingDir, String lineName, List<String> candidatePaths) {
            return success(summary, detail, archiveFile, workingDir, lineName, candidatePaths, null);
        }

        public static OperationResult success(String summary, String detail, File archiveFile, File workingDir, String lineName, List<String> candidatePaths, List<DiagnosticItem> diagnostics) {
            return new OperationResult(true, summary, detail, archiveFile, workingDir, lineName, candidatePaths, diagnostics);
        }

        public static OperationResult failure(String summary, String detail) {
            return failure(summary, detail, null);
        }

        public static OperationResult failure(String summary, String detail, List<DiagnosticItem> diagnostics) {
            return new OperationResult(false, summary, detail, null, null, "-", null, diagnostics);
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

        public List<DiagnosticItem> getDiagnostics() {
            return new ArrayList<>(diagnostics);
        }
    }
}
