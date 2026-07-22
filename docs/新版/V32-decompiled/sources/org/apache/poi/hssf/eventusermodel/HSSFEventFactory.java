package org.apache.poi.hssf.eventusermodel;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFEventFactory {
    public void processWorkbookEvents(HSSFRequest hSSFRequest, POIFSFileSystem pOIFSFileSystem) throws IOException {
        processEvents(hSSFRequest, pOIFSFileSystem.createDocumentInputStream("Workbook"));
    }

    public short abortableProcessWorkbookEvents(HSSFRequest hSSFRequest, POIFSFileSystem pOIFSFileSystem) throws HSSFUserException, IOException {
        return abortableProcessEvents(hSSFRequest, pOIFSFileSystem.createDocumentInputStream("Workbook"));
    }

    public void processEvents(HSSFRequest hSSFRequest, InputStream inputStream) throws IOException {
        try {
            genericProcessEvents(hSSFRequest, inputStream);
        } catch (HSSFUserException unused) {
        }
    }

    public short abortableProcessEvents(HSSFRequest hSSFRequest, InputStream inputStream) throws HSSFUserException, IOException {
        return genericProcessEvents(hSSFRequest, inputStream);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected short genericProcessEvents(HSSFRequest hSSFRequest, InputStream inputStream) throws HSSFUserException, IOException {
        short s = 0;
        try {
            byte[] bArr = new byte[2];
            int i = inputStream.read(bArr);
            Record record = null;
            short s2 = 0;
            short sProcessRecord = 0;
            while (i > 0) {
                try {
                    s2 = LittleEndian.getShort(bArr);
                    if (s2 == 0) {
                        break;
                    }
                    if (record != null && s2 != 60 && (sProcessRecord = hSSFRequest.processRecord(record)) != 0) {
                        return sProcessRecord;
                    }
                    if (s2 != 60) {
                        int i2 = LittleEndian.readShort(inputStream);
                        byte[] bArr2 = new byte[i2];
                        if (i2 > 0) {
                            inputStream.read(bArr2);
                        }
                        Record[] recordArrCreateRecord = RecordFactory.createRecord(s2, i2, bArr2);
                        if (recordArrCreateRecord.length > 1) {
                            for (int i3 = 0; i3 < recordArrCreateRecord.length - 1; i3++) {
                                sProcessRecord = hSSFRequest.processRecord(recordArrCreateRecord[i3]);
                                if (sProcessRecord != 0) {
                                    return sProcessRecord;
                                }
                            }
                        }
                        record = recordArrCreateRecord[recordArrCreateRecord.length - 1];
                    } else {
                        int i4 = LittleEndian.readShort(inputStream);
                        byte[] bArr3 = new byte[i4];
                        if (i4 > 0) {
                            inputStream.read(bArr3);
                        }
                        record.processContinueRecord(bArr3);
                    }
                    i = inputStream.read(bArr);
                } catch (IOException unused) {
                    s = s2;
                }
            }
            s = s2;
            return record != null ? hSSFRequest.processRecord(record) : sProcessRecord;
        } catch (IOException unused2) {
        }
        throw new RecordFormatException(new StringBuffer().append("Error reading byteswhile processing record sid=").append((int) s).toString());
    }
}
