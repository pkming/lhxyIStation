package org.apache.poi.hssf.dev;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/* JADX INFO: loaded from: classes3.dex */
public class HSSF {
    private String filename;
    protected HSSFWorkbook hssfworkbook;
    private Record[] records;
    private InputStream stream;

    public HSSF(String str) throws IOException {
        this.filename = null;
        this.stream = null;
        this.records = null;
        this.hssfworkbook = null;
        this.filename = str;
        this.hssfworkbook = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(str)));
    }

    public HSSF(String str, boolean z) throws IOException {
        this.filename = null;
        this.stream = null;
        this.records = null;
        this.hssfworkbook = null;
        FileOutputStream fileOutputStream = new FileOutputStream(str);
        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();
        HSSFSheet hSSFSheetCreateSheet = hSSFWorkbook.createSheet();
        HSSFCellStyle hSSFCellStyleCreateCellStyle = hSSFWorkbook.createCellStyle();
        HSSFCellStyle hSSFCellStyleCreateCellStyle2 = hSSFWorkbook.createCellStyle();
        HSSFCellStyle hSSFCellStyleCreateCellStyle3 = hSSFWorkbook.createCellStyle();
        HSSFFont hSSFFontCreateFont = hSSFWorkbook.createFont();
        HSSFFont hSSFFontCreateFont2 = hSSFWorkbook.createFont();
        hSSFFontCreateFont.setFontHeightInPoints((short) 12);
        hSSFFontCreateFont.setColor((short) 10);
        hSSFFontCreateFont.setBoldweight((short) 700);
        hSSFFontCreateFont2.setFontHeightInPoints((short) 10);
        hSSFFontCreateFont2.setColor((short) 15);
        hSSFFontCreateFont2.setBoldweight((short) 700);
        hSSFCellStyleCreateCellStyle.setFont(hSSFFontCreateFont);
        hSSFCellStyleCreateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
        short s = 1;
        hSSFCellStyleCreateCellStyle2.setBorderBottom((short) 1);
        hSSFCellStyleCreateCellStyle2.setFillPattern((short) 1);
        hSSFCellStyleCreateCellStyle2.setFillForegroundColor((short) 10);
        hSSFCellStyleCreateCellStyle2.setFont(hSSFFontCreateFont2);
        short s2 = 0;
        hSSFWorkbook.setSheetName(0, "HSSF Test");
        short s3 = 0;
        while (true) {
            if (s3 >= 300) {
                break;
            }
            HSSFRow hSSFRowCreateRow = hSSFSheetCreateSheet.createRow(s3);
            int i = s3 % 2;
            if (i == 0) {
                hSSFRowCreateRow.setHeight(EscherProperties.PERSPECTIVE__WEIGHT);
            }
            short s4 = s2;
            for (short s5 = 50; s4 < s5; s5 = 50) {
                HSSFCell hSSFCellCreateCell = hSSFRowCreateRow.createCell(s4, s2);
                int i2 = i;
                FileOutputStream fileOutputStream2 = fileOutputStream;
                hSSFCellCreateCell.setCellValue(((double) ((s3 * 10000) + s4)) + (((double) s3) / 1000.0d) + (((double) s4) / 10000.0d));
                if (i2 == 0) {
                    hSSFCellCreateCell.setCellStyle(hSSFCellStyleCreateCellStyle);
                }
                short s6 = (short) (s4 + 1);
                HSSFCell hSSFCellCreateCell2 = hSSFRowCreateRow.createCell(s6, 1);
                hSSFCellCreateCell2.setCellValue("TEST");
                hSSFSheetCreateSheet.setColumnWidth(s6, (short) 8000);
                if (i2 == 0) {
                    hSSFCellCreateCell2.setCellStyle(hSSFCellStyleCreateCellStyle2);
                }
                s4 = (short) (s4 + 2);
                s = 1;
                i = i2;
                fileOutputStream = fileOutputStream2;
                s2 = 0;
            }
            s3 = (short) (s3 + 1);
        }
        HSSFRow hSSFRowCreateRow2 = hSSFSheetCreateSheet.createRow((short) (((short) (s3 + s)) + s));
        hSSFCellStyleCreateCellStyle3.setBorderBottom((short) 5);
        for (short s7 = s2; s7 < 50; s7 = (short) (s7 + 1)) {
            hSSFRowCreateRow2.createCell(s7, 3).setCellStyle(hSSFCellStyleCreateCellStyle3);
        }
        hSSFSheetCreateSheet.addMergedRegion(new Region(s2, s2, 3, (short) 3));
        hSSFSheetCreateSheet.addMergedRegion(new Region(100, (short) 100, 110, EscherAggregate.ST_FLOWCHARTDECISION));
        hSSFWorkbook.createSheet();
        hSSFWorkbook.setSheetName(s, "DeletedSheet");
        hSSFWorkbook.removeSheetAt(s);
        hSSFWorkbook.write(fileOutputStream);
        fileOutputStream.close();
    }

    public HSSF(String str, String str2, boolean z) throws IOException {
        this.filename = null;
        this.stream = null;
        this.records = null;
        this.hssfworkbook = null;
        this.filename = null;
        this.hssfworkbook = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(this.filename)));
    }

    public static void main(String[] strArr) {
        if (strArr.length < 2) {
            return;
        }
        if (strArr.length == 2) {
            if (strArr[1].toLowerCase().equals("write")) {
                System.out.println("Write mode");
                try {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    new HSSF(strArr[0], true);
                    System.out.println(new StringBuffer().append("").append(System.currentTimeMillis() - jCurrentTimeMillis).append(" ms generation time").toString());
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            System.out.println("readwrite test");
            try {
                HSSFWorkbook hSSFWorkbook = new HSSF(strArr[0]).hssfworkbook;
                FileOutputStream fileOutputStream = new FileOutputStream(strArr[1]);
                hSSFWorkbook.write(fileOutputStream);
                fileOutputStream.close();
                return;
            } catch (Exception e2) {
                e2.printStackTrace();
                return;
            }
        }
        if (strArr.length == 3 && strArr[2].toLowerCase().equals("modify1")) {
            try {
                HSSFWorkbook hSSFWorkbook2 = new HSSF(strArr[0]).hssfworkbook;
                FileOutputStream fileOutputStream2 = new FileOutputStream(strArr[1]);
                HSSFSheet sheetAt = hSSFWorkbook2.getSheetAt(0);
                for (int i = 0; i < 25; i++) {
                    sheetAt.removeRow(sheetAt.getRow(i));
                }
                for (int i2 = 74; i2 < 100; i2++) {
                    sheetAt.removeRow(sheetAt.getRow(i2));
                }
                HSSFCell cell = sheetAt.getRow(39).getCell((short) 3);
                cell.setCellType(1);
                cell.setCellValue("MODIFIED CELL!!!!!");
                hSSFWorkbook2.write(fileOutputStream2);
                fileOutputStream2.close();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }
}
