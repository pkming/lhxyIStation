package org.apache.poi.hssf.eventmodel;

import org.apache.poi.hssf.record.Record;

/* JADX INFO: compiled from: EventRecordFactory.java */
/* JADX INFO: loaded from: classes3.dex */
class ListenerWrapper implements ERFListener {
    private boolean abortable;
    private ERFListener listener;
    private short[] sids;

    ListenerWrapper(ERFListener eRFListener, short[] sArr, boolean z) {
        this.listener = eRFListener;
        this.sids = sArr;
        this.abortable = z;
    }

    @Override // org.apache.poi.hssf.eventmodel.ERFListener
    public boolean processRecord(Record record) {
        int i = 0;
        boolean zProcessRecord = true;
        while (true) {
            short[] sArr = this.sids;
            if (i >= sArr.length) {
                break;
            }
            if (sArr[i] == record.getSid()) {
                zProcessRecord = this.listener.processRecord(record);
                if (this.abortable && !zProcessRecord) {
                    break;
                }
            }
            i++;
        }
        return zProcessRecord;
    }
}
