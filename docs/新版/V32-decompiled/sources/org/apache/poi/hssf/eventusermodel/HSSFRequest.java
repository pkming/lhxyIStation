package org.apache.poi.hssf.eventusermodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFRequest {
    private HashMap records = new HashMap(50);

    public void addListener(HSSFListener hSSFListener, short s) {
        Object obj = this.records.get(new Short(s));
        if (obj != null) {
            return;
        }
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(hSSFListener);
        this.records.put(new Short(s), arrayList);
    }

    public void addListenerForAllRecords(HSSFListener hSSFListener) {
        for (short s : RecordFactory.getAllKnownRecordSIDs()) {
            addListener(hSSFListener, s);
        }
    }

    protected short processRecord(Record record) throws HSSFUserException {
        Object obj = this.records.get(new Short(record.getSid()));
        if (obj == null) {
            return (short) 0;
        }
        List list = (List) obj;
        short sAbortableProcessRecord = 0;
        for (int i = 0; i < list.size(); i++) {
            Object obj2 = list.get(i);
            if (obj2 instanceof AbortableHSSFListener) {
                sAbortableProcessRecord = ((AbortableHSSFListener) obj2).abortableProcessRecord(record);
                if (sAbortableProcessRecord != 0) {
                    break;
                }
            } else {
                ((HSSFListener) obj2).processRecord(record);
            }
        }
        return sAbortableProcessRecord;
    }
}
