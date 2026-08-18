package org.apache.poi.hssf.eventmodel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.model.Model;
import org.apache.poi.hssf.model.Sheet;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.Record;

/* JADX INFO: loaded from: classes3.dex */
public class ModelFactory implements ERFListener {
    Model currentmodel;
    boolean lastEOF;
    List listeners = new ArrayList(1);

    public void registerListener(ModelFactoryListener modelFactoryListener) {
        this.listeners.add(modelFactoryListener);
    }

    public void run(InputStream inputStream) {
        EventRecordFactory eventRecordFactory = new EventRecordFactory(true);
        eventRecordFactory.registerListener(this, null);
        this.lastEOF = true;
        eventRecordFactory.processRecords(inputStream);
    }

    @Override // org.apache.poi.hssf.eventmodel.ERFListener
    public boolean processRecord(Record record) {
        if (record.getSid() == 2057) {
            if (!this.lastEOF) {
                throw new RuntimeException("Not yet handled embedded models");
            }
            BOFRecord bOFRecord = (BOFRecord) record;
            short type = bOFRecord.getType();
            if (type == 5) {
                this.currentmodel = new Workbook();
            } else if (type == 16) {
                this.currentmodel = new Sheet();
            } else {
                throw new RuntimeException(new StringBuffer().append("Unsupported model type ").append((int) bOFRecord.getType()).toString());
            }
        }
        if (record.getSid() == 10) {
            this.lastEOF = true;
            throwEvent(this.currentmodel);
        } else {
            this.lastEOF = false;
        }
        return true;
    }

    private void throwEvent(Model model) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((ModelFactoryListener) it.next()).process(model);
        }
    }
}
