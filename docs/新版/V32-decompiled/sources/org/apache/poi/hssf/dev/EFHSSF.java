package org.apache.poi.hssf.dev;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/* JADX INFO: loaded from: classes3.dex */
public class EFHSSF {
    String infile;
    String outfile;
    HSSFWorkbook workbook = null;
    HSSFSheet cursheet = null;

    public void setInputFile(String str) {
        this.infile = str;
    }

    public void setOutputFile(String str) {
        this.outfile = str;
    }

    public void run() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(this.infile);
        DocumentInputStream documentInputStreamCreateDocumentInputStream = new POIFSFileSystem(fileInputStream).createDocumentInputStream("Workbook");
        HSSFRequest hSSFRequest = new HSSFRequest();
        hSSFRequest.addListenerForAllRecords(new EFHSSFListener(this));
        new HSSFEventFactory().processEvents(hSSFRequest, documentInputStreamCreateDocumentInputStream);
        fileInputStream.close();
        documentInputStreamCreateDocumentInputStream.close();
        FileOutputStream fileOutputStream = new FileOutputStream(this.outfile);
        this.workbook.write(fileOutputStream);
        fileOutputStream.close();
        System.out.println("done.");
    }

    public void recordHandler(Record record) {
        short sid = record.getSid();
        if (sid == 133) {
            this.workbook.createSheet(((BoundSheetRecord) record).getSheetname());
            return;
        }
        if (sid == 515) {
            NumberRecord numberRecord = (NumberRecord) record;
            this.cursheet.getRow(numberRecord.getRow()).createCell(numberRecord.getColumn(), 0).setCellValue(numberRecord.getValue());
            return;
        }
        if (sid == 520) {
            this.cursheet.createRow(((RowRecord) record).getRowNumber());
            return;
        }
        if (sid == 2057) {
            BOFRecord bOFRecord = (BOFRecord) record;
            if (bOFRecord.getType() == 5) {
                this.workbook = new HSSFWorkbook();
                return;
            } else {
                if (bOFRecord.getType() == 16) {
                    this.cursheet = this.workbook.getSheetAt(0);
                    return;
                }
                return;
            }
        }
        if (sid != 252) {
            if (sid != 253) {
                return;
            }
            LabelSSTRecord labelSSTRecord = (LabelSSTRecord) record;
            this.cursheet.getRow(labelSSTRecord.getRow()).createCell(labelSSTRecord.getColumn(), 1).setCellValue(this.workbook.getSSTString(labelSSTRecord.getSSTIndex()));
            return;
        }
        SSTRecord sSTRecord = (SSTRecord) record;
        for (int i = 0; i < sSTRecord.getNumUniqueStrings(); i++) {
            this.workbook.addSSTString(sSTRecord.getString(i));
        }
    }

    public static void main(String[] strArr) {
        if (strArr.length < 2 || !strArr[0].equals("--help")) {
            try {
                EFHSSF efhssf = new EFHSSF();
                efhssf.setInputFile(strArr[0]);
                efhssf.setOutputFile(strArr[1]);
                efhssf.run();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        System.out.println("EFHSSF");
        System.out.println("General testbed for HSSFEventFactory based testing and Code examples");
        System.out.println("Usage: java org.apache.poi.hssf.dev.EFHSSF file1 file2");
        System.out.println("   --will rewrite the file reading with the event api");
        System.out.println("and writing with the standard API");
    }
}
