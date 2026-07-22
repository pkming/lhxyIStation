package org.apache.poi.ddf;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.record.RecordFormatException;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultEscherRecordFactory implements EscherRecordFactory {
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherBSERecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherChildAnchorRecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherClientAnchorRecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherClientDataRecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherDgRecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherDggRecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherOptRecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherSpRecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherSpgrRecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherSplitMenuColorsRecord;
    static /* synthetic */ Class class$org$apache$poi$ddf$EscherTextboxRecord;
    private static Class[] escherRecordClasses;
    private static Map recordsMap;

    static {
        Class[] clsArr = new Class[11];
        Class clsClass$ = class$org$apache$poi$ddf$EscherBSERecord;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.ddf.EscherBSERecord");
            class$org$apache$poi$ddf$EscherBSERecord = clsClass$;
        }
        clsArr[0] = clsClass$;
        Class clsClass$2 = class$org$apache$poi$ddf$EscherOptRecord;
        if (clsClass$2 == null) {
            clsClass$2 = class$("org.apache.poi.ddf.EscherOptRecord");
            class$org$apache$poi$ddf$EscherOptRecord = clsClass$2;
        }
        clsArr[1] = clsClass$2;
        Class clsClass$3 = class$org$apache$poi$ddf$EscherClientAnchorRecord;
        if (clsClass$3 == null) {
            clsClass$3 = class$("org.apache.poi.ddf.EscherClientAnchorRecord");
            class$org$apache$poi$ddf$EscherClientAnchorRecord = clsClass$3;
        }
        clsArr[2] = clsClass$3;
        Class clsClass$4 = class$org$apache$poi$ddf$EscherDgRecord;
        if (clsClass$4 == null) {
            clsClass$4 = class$("org.apache.poi.ddf.EscherDgRecord");
            class$org$apache$poi$ddf$EscherDgRecord = clsClass$4;
        }
        clsArr[3] = clsClass$4;
        Class clsClass$5 = class$org$apache$poi$ddf$EscherSpgrRecord;
        if (clsClass$5 == null) {
            clsClass$5 = class$("org.apache.poi.ddf.EscherSpgrRecord");
            class$org$apache$poi$ddf$EscherSpgrRecord = clsClass$5;
        }
        clsArr[4] = clsClass$5;
        Class clsClass$6 = class$org$apache$poi$ddf$EscherSpRecord;
        if (clsClass$6 == null) {
            clsClass$6 = class$("org.apache.poi.ddf.EscherSpRecord");
            class$org$apache$poi$ddf$EscherSpRecord = clsClass$6;
        }
        clsArr[5] = clsClass$6;
        Class clsClass$7 = class$org$apache$poi$ddf$EscherClientDataRecord;
        if (clsClass$7 == null) {
            clsClass$7 = class$("org.apache.poi.ddf.EscherClientDataRecord");
            class$org$apache$poi$ddf$EscherClientDataRecord = clsClass$7;
        }
        clsArr[6] = clsClass$7;
        Class clsClass$8 = class$org$apache$poi$ddf$EscherDggRecord;
        if (clsClass$8 == null) {
            clsClass$8 = class$("org.apache.poi.ddf.EscherDggRecord");
            class$org$apache$poi$ddf$EscherDggRecord = clsClass$8;
        }
        clsArr[7] = clsClass$8;
        Class clsClass$9 = class$org$apache$poi$ddf$EscherSplitMenuColorsRecord;
        if (clsClass$9 == null) {
            clsClass$9 = class$("org.apache.poi.ddf.EscherSplitMenuColorsRecord");
            class$org$apache$poi$ddf$EscherSplitMenuColorsRecord = clsClass$9;
        }
        clsArr[8] = clsClass$9;
        Class clsClass$10 = class$org$apache$poi$ddf$EscherChildAnchorRecord;
        if (clsClass$10 == null) {
            clsClass$10 = class$("org.apache.poi.ddf.EscherChildAnchorRecord");
            class$org$apache$poi$ddf$EscherChildAnchorRecord = clsClass$10;
        }
        clsArr[9] = clsClass$10;
        Class clsClass$11 = class$org$apache$poi$ddf$EscherTextboxRecord;
        if (clsClass$11 == null) {
            clsClass$11 = class$("org.apache.poi.ddf.EscherTextboxRecord");
            class$org$apache$poi$ddf$EscherTextboxRecord = clsClass$11;
        }
        clsArr[10] = clsClass$11;
        escherRecordClasses = clsArr;
        recordsMap = recordsToMap(clsArr);
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    @Override // org.apache.poi.ddf.EscherRecordFactory
    public EscherRecord createRecord(byte[] bArr, int i) {
        EscherRecord.EscherRecordHeader header = EscherRecord.EscherRecordHeader.readHeader(bArr, i);
        if ((header.getOptions() & 15) == 15) {
            EscherContainerRecord escherContainerRecord = new EscherContainerRecord();
            escherContainerRecord.setRecordId(header.getRecordId());
            escherContainerRecord.setOptions(header.getOptions());
            return escherContainerRecord;
        }
        if (header.getRecordId() >= -4072 && header.getRecordId() <= -3817) {
            EscherBlipRecord escherBlipRecord = new EscherBlipRecord();
            escherBlipRecord.setRecordId(header.getRecordId());
            escherBlipRecord.setOptions(header.getOptions());
            return escherBlipRecord;
        }
        Constructor constructor = (Constructor) recordsMap.get(new Short(header.getRecordId()));
        EscherRecord escherRecord = null;
        if (constructor != null) {
            try {
                EscherRecord escherRecord2 = (EscherRecord) constructor.newInstance(new Object[0]);
                escherRecord2.setRecordId(header.getRecordId());
                escherRecord2.setOptions(header.getOptions());
                escherRecord = escherRecord2;
            } catch (Exception unused) {
            }
        }
        return escherRecord == null ? new UnknownEscherRecord() : escherRecord;
    }

    private static Map recordsToMap(Class[] clsArr) {
        HashMap map = new HashMap();
        for (Class cls : clsArr) {
            try {
                map.put(new Short(cls.getField("RECORD_ID").getShort(null)), cls.getConstructor(new Class[0]));
            } catch (Exception unused) {
                throw new RecordFormatException("Unable to determine record types");
            }
        }
        return map;
    }
}
