package org.apache.poi.hssf.dev;

import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.Record;

/* JADX INFO: compiled from: EFHSSF.java */
/* JADX INFO: loaded from: classes3.dex */
class EFHSSFListener implements HSSFListener {
    EFHSSF efhssf;

    public EFHSSFListener(EFHSSF efhssf) {
        this.efhssf = efhssf;
    }

    @Override // org.apache.poi.hssf.eventusermodel.HSSFListener
    public void processRecord(Record record) {
        this.efhssf.recordHandler(record);
    }
}
