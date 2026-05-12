package com.lhxy.istationdevice.android11.domain.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class StationResourceArchiveUseCaseTest {
    @Test
    public void inspectExtractedArchive_rejectsPackageWithoutBusDirectory() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-invalid").toFile();
        File otherDir = new File(extractedDir, "OtherFolder");
        if (!otherDir.mkdirs() && !otherDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }
        Files.write(new File(otherDir, "readme.txt").toPath(), "bad package".getBytes(StandardCharsets.UTF_8));

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        StationResourceArchiveUseCase.ArchiveSelfCheck selfCheck = useCase.inspectExtractedArchive(extractedDir, extractedFiles);

        assertFalse(selfCheck.isValid());
    }

    @Test
    public void inspectExtractedArchive_acceptsNestedSourceFileBusStructure() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-valid").toFile();
        File busDir = new File(extractedDir, "SourceFile/Bus/101");
        if (!busDir.mkdirs() && !busDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }
        Files.write(new File(extractedDir, "SourceFile/Bus/lineInfo.csv").toPath(), "id,lineName\n1,101路".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(busDir, "101S.csv").toPath(), "站序,站名\n1,测试站".getBytes(StandardCharsets.UTF_8));

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        StationResourceArchiveUseCase.ArchiveSelfCheck selfCheck = useCase.inspectExtractedArchive(extractedDir, extractedFiles);

        assertTrue(selfCheck.isValid());
    }

    @Test
    public void inspectExtractedArchive_acceptsLegacyBusImportNestedStructure() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-legacy-rar").toFile();
        File busDir = new File(extractedDir, "BusResLegacy/BusImport/SourceFile/Bus/101");
        if (!busDir.mkdirs() && !busDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }
        Files.write(new File(extractedDir, "BusResLegacy/BusImport/SourceFile/Bus/lineInfo.csv").toPath(), "id,lineName\n1,101路".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(busDir, "101S.csv").toPath(), "站序,站名\n1,测试站".getBytes(StandardCharsets.UTF_8));

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        StationResourceArchiveUseCase.ArchiveSelfCheck selfCheck = useCase.inspectExtractedArchive(extractedDir, extractedFiles);

        assertTrue(selfCheck.isValid());
    }

    @Test
    public void buildImportDiagnostics_marksMalformedStationFileAsFailure() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-diagnostics").toFile();
        File lineDir = new File(extractedDir, "SourceFile/Bus/101");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }
        Files.write(new File(extractedDir, "SourceFile/Bus/lineInfo.csv").toPath(), "id,lineName,flag,attr\n1,101,是,1".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(extractedDir, "SourceFile/Bus/config.csv").toPath(), "key,value\nDispatch,1".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(lineDir, "101S.csv").toPath(), "站序,站名\n1,测试站".getBytes(StandardCharsets.UTF_8));

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasStationFailure = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_FAIL.equals(item.getLevel())
                    && item.getTarget().endsWith("101/101S.csv")) {
                hasStationFailure = true;
                break;
            }
        }
        assertTrue(hasStationFailure);
    }

    @Test
    public void buildImportDiagnostics_acceptsCommonExcelFormats() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-excel-formats").toFile();
        File busDir = new File(extractedDir, "SourceFile/Bus");
        File lineDir = new File(busDir, "A1");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }

        writeXlsx(new File(busDir, "lineInfo.xlsx"), new String[][]{
                {"id", "lineName", "flag", "attr"},
                {"1", "A1", "是", "1"}
        });
        writeExcelXml(new File(busDir, "config.xml"), new String[][]{
                {"key", "value"},
                {"Dispatch", "1"}
        });
        writeXls(new File(busDir, "Vchinfo.xls"), new String[][]{
                {"key", "value"},
                {"Volume", "8"}
        });
        writeXlsx(new File(lineDir, "A1S.xlsx"), new String[][]{
            {"站序", "报站语音", "站名", "经度", "纬度", "角度"},
            {"1", "测试上行站", "测试上行站", "104.1", "30.6", "280"}
        });
        writeExcelXml(new File(lineDir, "A1X.xml"), new String[][]{
            {"站序", "报站语音", "站名", "经度", "纬度", "角度"},
            {"1", "测试下行站", "测试下行站", "104.1", "30.6", "N"}
        });

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasFailure = false;
        boolean hasLineInfoOk = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_FAIL.equals(item.getLevel())) {
                hasFailure = true;
            }
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_OK.equals(item.getLevel())
                    && item.getTarget().endsWith("lineInfo.xlsx")) {
                hasLineInfoOk = true;
            }
        }

        assertFalse(hasFailure);
        assertTrue(hasLineInfoOk);
    }

    @Test
    public void buildImportDiagnostics_reportsDetailedStationRowIssues() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-row-issues").toFile();
        File lineDir = new File(extractedDir, "SourceFile/Bus/A1");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }

        writeXlsx(new File(new File(extractedDir, "SourceFile/Bus"), "lineInfo.xlsx"), new String[][]{
                {"编号", "线路名称", "是否当前线路", "属性", "线路编号"},
                {"1", "A1", "是", "1", "12"}
        });
        Files.write(new File(extractedDir, "SourceFile/Bus/config.csv").toPath(), "key,value\nDispatch,1".getBytes(StandardCharsets.UTF_8));
        writeXlsx(new File(lineDir, "A1S.xlsx"), new String[][]{
            {"站台编号", "报站语音", "站名", "经度", "纬度", "角度"},
                {"1", "", "", "", "30.6", "bad-angle"},
                {"2", "测试站", "测试站", "104.1", "", "361"}
        });

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasDetailedFailure = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_FAIL.equals(item.getLevel())
                    && item.getTarget().endsWith("A1/A1S.xlsx")) {
                hasDetailedFailure = item.getMessage().contains("1. 第2行 [站台编号=1] 第2列[报站语音]为空、第3列[站名]为空、第4列[经度]为空、第6列[角度]格式不对，应为 N/S/E/W 或 0-360 数值")
                        && item.getMessage().contains("2. 第3行 [站台编号=2] 第5列[纬度]为空、第6列[角度]格式不对，应为 N/S/E/W 或 0-360 数值");
                break;
            }
        }

        assertTrue(hasDetailedFailure);
    }

    @Test
    public void buildImportDiagnostics_reportsDetailedLineInfoRowIssues() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-lineinfo-row-issues").toFile();
        File lineDir = new File(extractedDir, "SourceFile/Bus/A1");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }

        writeXlsx(new File(new File(extractedDir, "SourceFile/Bus"), "lineInfo.xlsx"), new String[][]{
                {"编号", "线路名称", "是否当前线路", "属性", "线路编号"},
                {"1", "", "是", "1", "12"},
                {"2", "A1", "否", "1", "13"}
        });
        Files.write(new File(extractedDir, "SourceFile/Bus/config.csv").toPath(), "key,value\nDispatch,1".getBytes(StandardCharsets.UTF_8));
        writeXlsx(new File(lineDir, "A1S.xlsx"), new String[][]{
                {"站序", "报站语音", "站名", "经度", "纬度", "角度"},
                {"1", "测试站", "测试站", "104.1", "30.6", "280"}
        });

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasDetailedWarning = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_WARN.equals(item.getLevel())
                    && item.getTarget().endsWith("lineInfo.xlsx")) {
                hasDetailedWarning = item.getMessage().contains("第2行 [编号=1] 第2列[线路名称]为空");
                break;
            }
        }

        assertTrue(hasDetailedWarning);
    }

    @Test
    public void buildImportDiagnostics_readsUtf8CsvHeadersWithoutGarbling() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-utf8-csv").toFile();
        File lineDir = new File(extractedDir, "SourceFile/Bus/A1");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }

        Files.write(new File(extractedDir, "SourceFile/Bus/lineInfo.csv").toPath(), (
                "编号,线路名称,是否当前线路,属性,线路编号\n"
                        + "1,A1,是,1,12\n").getBytes(StandardCharsets.UTF_8));
        Files.write(new File(extractedDir, "SourceFile/Bus/config.csv").toPath(), "key,value\nDispatch,1".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(lineDir, "A1S.csv").toPath(), (
                "站台编号,报站语音,站名,经度,纬度,角度\n"
                        + "0,测试语音,测试站,104.1,30.6,N\n"
                        + "1,测试语音,测试站,,30.7,N\n").getBytes(StandardCharsets.UTF_8));

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasReadableWarn = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_WARN.equals(item.getLevel())
                    && item.getTarget().endsWith("A1/A1S.csv")) {
                hasReadableWarn = item.getMessage().contains("第3行 [站台编号=1] 第4列[经度]为空");
                break;
            }
        }

        assertTrue(hasReadableWarn);
    }

    @Test
    public void buildImportDiagnostics_acceptsReminderFilesWithoutAngleColumn() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-remind-without-angle").toFile();
        File lineDir = new File(extractedDir, "SourceFile/Bus/BTC");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }

        writeXlsx(new File(new File(extractedDir, "SourceFile/Bus"), "lineInfo.xlsx"), new String[][]{
                {"编号", "线路名称", "是否当前线路", "属性", "线路编号"},
                {"1", "BTC", "是", "1", "12"}
        });
        Files.write(new File(extractedDir, "SourceFile/Bus/config.csv").toPath(), "key,value\nDispatch,1".getBytes(StandardCharsets.UTF_8));
        writeXlsx(new File(lineDir, "BTCS.xlsx"), new String[][]{
                {"站序", "报站语音", "站名", "经度", "纬度", "角度"},
                {"1", "测试站", "测试站", "104.1", "30.6", "280"}
        });
        writeXlsx(new File(lineDir, "BTCSRemind.xlsx"), new String[][]{
                {"编号", "提醒语音", "经度", "纬度", "站前里程", "外音开否"},
                {"0", "remind", "", "3417.1739", "30", "是"},
                {"1", "remind", "10855.6868", "3417.1682", "30", "是"}
        });

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasReminderWarn = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_WARN.equals(item.getLevel())
                    && item.getTarget().endsWith("BTC/BTCSRemind.xlsx")) {
                hasReminderWarn = item.getMessage().contains("第2行 [编号=0] 第3列[经度]为空")
                        && !item.getMessage().contains("角度");
                break;
            }
        }

        assertTrue(hasReminderWarn);
    }

    @Test
    public void buildImportDiagnostics_reportsInvalidIdentifierWhenFirstColumnContainsNumber() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-invalid-identifier").toFile();
        File lineDir = new File(extractedDir, "SourceFile/Bus/A1");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }

        Files.write(new File(extractedDir, "SourceFile/Bus/lineInfo.csv").toPath(), (
                "编号,线路名称,是否当前线路,属性,线路编号\n"
                        + "A1,A1,是,1,12\n").getBytes(StandardCharsets.UTF_8));
        Files.write(new File(extractedDir, "SourceFile/Bus/config.csv").toPath(), "key,value\nDispatch,1".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(lineDir, "A1S.csv").toPath(), (
                "站台编号,报站语音,站名,经度,纬度,角度\n"
                        + "0,测试语音,测试站,104.1,30.6,N\n").getBytes(StandardCharsets.UTF_8));

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasIdentifierFailure = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_FAIL.equals(item.getLevel())
                    && item.getTarget().endsWith("lineInfo.csv")) {
                hasIdentifierFailure = item.getMessage().contains("第2行 [编号=A1] 第1列[编号]格式不对，应为整数");
                break;
            }
        }

        assertTrue(hasIdentifierFailure);
    }

    @Test
    public void buildImportDiagnostics_warnsOnDuplicateLineNamesInLineInfo() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-duplicate-line-name").toFile();
        File lineDir = new File(extractedDir, "SourceFile/Bus/A1");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }

        Files.write(new File(extractedDir, "SourceFile/Bus/lineInfo.csv").toPath(), (
                "编号,线路名称,是否当前线路,属性,线路编号\n"
                        + "1,A1,是,1,12\n"
                        + "2,A1,否,1,13\n").getBytes(StandardCharsets.UTF_8));
        Files.write(new File(extractedDir, "SourceFile/Bus/config.csv").toPath(), "key,value\nDispatch,1".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(lineDir, "A1S.csv").toPath(), (
                "站台编号,报站语音,站名,经度,纬度,角度\n"
                        + "0,测试语音,测试站,104.1,30.6,N\n").getBytes(StandardCharsets.UTF_8));

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasDuplicateWarning = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_WARN.equals(item.getLevel())
                    && item.getTarget().endsWith("lineInfo.csv")) {
                hasDuplicateWarning = item.getMessage().contains("第3行 [编号=2] 第2列[线路名称]重复，与第2行重复");
                break;
            }
        }

        assertTrue(hasDuplicateWarning);
    }

    @Test
    public void buildImportDiagnostics_suggestsNearMatchWhenLineInfoAndDirectoryDifferByWhitespace() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-near-line-name-match").toFile();
        File lineDir = new File(extractedDir, "SourceFile/Bus/A1");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }

        Files.write(new File(extractedDir, "SourceFile/Bus/lineInfo.csv").toPath(), (
                "编号,线路名称,是否当前线路,属性,线路编号\n"
                        + "1,A 1,是,1,12\n").getBytes(StandardCharsets.UTF_8));
        Files.write(new File(extractedDir, "SourceFile/Bus/config.csv").toPath(), "key,value\nDispatch,1".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(lineDir, "A1S.csv").toPath(), (
                "站台编号,报站语音,站名,经度,纬度,角度\n"
                        + "0,测试语音,测试站,104.1,30.6,N\n").getBytes(StandardCharsets.UTF_8));

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasMissingDirHint = false;
        boolean hasExtraDirHint = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_FAIL.equals(item.getLevel())) {
                hasMissingDirHint = hasMissingDirHint || item.getMessage().contains("疑似目录名为 A1");
            }
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_WARN.equals(item.getLevel())) {
                hasExtraDirHint = hasExtraDirHint || item.getMessage().contains("疑似对应 lineInfo 里的 A 1");
            }
        }

        assertTrue(describeDiagnostics(diagnostics), hasMissingDirHint);
        assertTrue(describeDiagnostics(diagnostics), hasExtraDirHint);
    }

    @Test
    public void buildImportDiagnostics_exposesExpectedMissingStationFileNames() throws Exception {
        File extractedDir = Files.createTempDirectory("station-archive-missing-station-files").toFile();
        File onlyDownLineDir = new File(extractedDir, "SourceFile/Bus/5");
        if (!onlyDownLineDir.mkdirs() && !onlyDownLineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试目录");
        }

        Files.write(new File(extractedDir, "SourceFile/Bus/lineInfo.csv").toPath(), (
                "编号,线路名称,是否当前线路,属性,线路编号\n"
                        + "1,2,是,1,12\n"
                        + "2,5,否,1,13\n").getBytes(StandardCharsets.UTF_8));
        Files.write(new File(extractedDir, "SourceFile/Bus/config.csv").toPath(), "key,value\nDispatch,1".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(onlyDownLineDir, "5X.csv").toPath(), (
                "站台编号,报站语音,站名,经度,纬度,角度\n"
                        + "0,测试语音,测试站,104.1,30.6,N\n").getBytes(StandardCharsets.UTF_8));

        StationResourceArchiveUseCase useCase = new StationResourceArchiveUseCase();
        List<File> extractedFiles = collectFiles(extractedDir);

        List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics = useCase.buildImportDiagnostics(new File(extractedDir, "SourceFile"), extractedFiles);

        boolean hasMissingDirFileNames = false;
        boolean hasMissingUpFileName = false;
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_FAIL.equals(item.getLevel())
                    && item.getTarget().endsWith("Bus/2")) {
                hasMissingDirFileNames = item.getMessage().contains("2S.csv")
                        && item.getMessage().contains("2X.xlsx");
            }
            if (StationResourceArchiveUseCase.DiagnosticItem.LEVEL_WARN.equals(item.getLevel())
                    && item.getTarget().endsWith("Bus/5")) {
                hasMissingUpFileName = item.getMessage().contains("5S.csv")
                        && item.getMessage().contains("期望文件");
            }
        }

        assertTrue(describeDiagnostics(diagnostics), hasMissingDirFileNames);
        assertTrue(describeDiagnostics(diagnostics), hasMissingUpFileName);
    }

    private void writeXls(File file, String[][] rows) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            org.apache.poi.hssf.usermodel.HSSFSheet sheet = workbook.createSheet("Sheet1");
            for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
                org.apache.poi.hssf.usermodel.HSSFRow row = sheet.createRow(rowIndex);
                for (int cellIndex = 0; cellIndex < rows[rowIndex].length; cellIndex++) {
                    row.createCell(cellIndex).setCellValue(rows[rowIndex][cellIndex]);
                }
            }
            try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
                workbook.write(outputStream);
                outputStream.flush();
            }
        } finally {
            workbook.close();
        }
    }

    private String describeDiagnostics(List<StationResourceArchiveUseCase.DiagnosticItem> diagnostics) {
        StringBuilder builder = new StringBuilder();
        if (diagnostics == null) {
            return "<null diagnostics>";
        }
        for (StationResourceArchiveUseCase.DiagnosticItem item : diagnostics) {
            builder.append(item.getLevel())
                    .append(" | ")
                    .append(item.getTarget())
                    .append(" | ")
                    .append(item.getMessage())
                    .append('\n');
        }
        return builder.toString();
    }

    private void writeXlsx(File file, String[][] rows) throws Exception {
        List<String> sharedStrings = new ArrayList<>();
        List<List<Integer>> indexes = new ArrayList<>();
        for (String[] row : rows) {
            List<Integer> rowIndexes = new ArrayList<>();
            for (String value : row) {
                int sharedIndex = sharedStrings.indexOf(value);
                if (sharedIndex < 0) {
                    sharedStrings.add(value);
                    sharedIndex = sharedStrings.size() - 1;
                }
                rowIndexes.add(sharedIndex);
            }
            indexes.add(rowIndexes);
        }
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file, false), StandardCharsets.UTF_8)) {
            writeZipEntry(outputStream, "[Content_Types].xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n"
                            + "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n"
                            + "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n"
                            + "  <Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>\n"
                            + "  <Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n"
                            + "  <Override PartName=\"/xl/sharedStrings.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml\"/>\n"
                            + "</Types>\n");
            writeZipEntry(outputStream, "_rels/.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n"
                            + "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>\n"
                            + "</Relationships>\n");
            writeZipEntry(outputStream, "xl/workbook.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n"
                            + "  <sheets>\n"
                            + "    <sheet name=\"Sheet1\" sheetId=\"1\" r:id=\"rId1\"/>\n"
                            + "  </sheets>\n"
                            + "</workbook>\n");
            writeZipEntry(outputStream, "xl/_rels/workbook.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n"
                            + "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>\n"
                            + "  <Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings\" Target=\"sharedStrings.xml\"/>\n"
                            + "</Relationships>\n");
            writeZipEntry(outputStream, "xl/sharedStrings.xml", buildXlsxSharedStringsXml(sharedStrings));
            writeZipEntry(outputStream, "xl/worksheets/sheet1.xml", buildXlsxSheetXml(indexes));
            outputStream.flush();
        }
    }

    private void writeZipEntry(ZipOutputStream outputStream, String name, String content) throws Exception {
        outputStream.putNextEntry(new ZipEntry(name));
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        outputStream.closeEntry();
    }

    private String buildXlsxSheetXml(List<List<Integer>> rows) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");
        builder.append("  <sheetData>\n");
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            builder.append("    <row r=\"").append(rowIndex + 1).append("\">\n");
            for (int cellIndex = 0; cellIndex < rows.get(rowIndex).size(); cellIndex++) {
                builder.append("      <c r=\"")
                        .append(columnRef(cellIndex + 1))
                        .append(rowIndex + 1)
                        .append("\" t=\"s\"><v>")
                        .append(rows.get(rowIndex).get(cellIndex))
                        .append("</v></c>\n");
            }
            builder.append("    </row>\n");
        }
        builder.append("  </sheetData>\n");
        builder.append("</worksheet>\n");
        return builder.toString();
    }

    private String buildXlsxSharedStringsXml(List<String> values) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" count=\"")
                .append(values.size())
                .append("\" uniqueCount=\"")
                .append(values.size())
                .append("\">\n");
        for (String value : values) {
            builder.append("  <si><t>")
                    .append(escapeXml(value))
                    .append("</t></si>\n");
        }
        builder.append("</sst>\n");
        return builder.toString();
    }

    private String columnRef(int index) {
        StringBuilder builder = new StringBuilder();
        int current = index;
        while (current > 0) {
            int remainder = (current - 1) % 26;
            builder.insert(0, (char) ('A' + remainder));
            current = (current - 1) / 26;
        }
        return builder.toString();
    }

    private void writeExcelXml(File file, String[][] rows) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n");
        builder.append("  <Worksheet ss:Name=\"Sheet1\">\n");
        builder.append("    <Table>\n");
        for (String[] row : rows) {
            builder.append("      <Row>\n");
            for (String value : row) {
                builder.append("        <Cell><Data ss:Type=\"String\">")
                        .append(escapeXml(value))
                        .append("</Data></Cell>\n");
            }
            builder.append("      </Row>\n");
        }
        builder.append("    </Table>\n");
        builder.append("  </Worksheet>\n");
        builder.append("</Workbook>\n");
        Files.write(file.toPath(), builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private List<File> collectFiles(File dir) {
        List<File> files = new ArrayList<>();
        collectFiles(dir, files);
        return files;
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
}