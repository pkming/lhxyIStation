package org.apache.poi.hssf.model;

import android.text.format.DateUtils;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.usermodel.HSSFTextbox;

/* JADX INFO: loaded from: classes3.dex */
public class TextboxShape extends AbstractShape {
    private EscherTextboxRecord escherTextbox;
    private ObjRecord objRecord;
    private EscherContainerRecord spContainer;
    private TextObjectRecord textObjectRecord;

    TextboxShape(HSSFTextbox hSSFTextbox, int i) {
        this.spContainer = createSpContainer(hSSFTextbox, i);
        this.objRecord = createObjRecord(hSSFTextbox, i);
        this.textObjectRecord = createTextObjectRecord(hSSFTextbox, i);
    }

    private ObjRecord createObjRecord(HSSFTextbox hSSFTextbox, int i) {
        ObjRecord objRecord = new ObjRecord();
        CommonObjectDataSubRecord commonObjectDataSubRecord = new CommonObjectDataSubRecord();
        commonObjectDataSubRecord.setObjectType((short) hSSFTextbox.getShapeType());
        commonObjectDataSubRecord.setObjectId((short) i);
        commonObjectDataSubRecord.setLocked(true);
        commonObjectDataSubRecord.setPrintable(true);
        commonObjectDataSubRecord.setAutofill(true);
        commonObjectDataSubRecord.setAutoline(true);
        EndSubRecord endSubRecord = new EndSubRecord();
        objRecord.addSubRecord(commonObjectDataSubRecord);
        objRecord.addSubRecord(endSubRecord);
        return objRecord;
    }

    private EscherContainerRecord createSpContainer(HSSFTextbox hSSFTextbox, int i) {
        EscherContainerRecord escherContainerRecord = new EscherContainerRecord();
        EscherSpRecord escherSpRecord = new EscherSpRecord();
        EscherOptRecord escherOptRecord = new EscherOptRecord();
        new EscherClientAnchorRecord();
        EscherClientDataRecord escherClientDataRecord = new EscherClientDataRecord();
        this.escherTextbox = new EscherTextboxRecord();
        escherContainerRecord.setRecordId(EscherContainerRecord.SP_CONTAINER);
        escherContainerRecord.setOptions((short) 15);
        escherSpRecord.setRecordId(EscherSpRecord.RECORD_ID);
        escherSpRecord.setOptions((short) 3234);
        escherSpRecord.setShapeId(i);
        escherSpRecord.setFlags(DateUtils.FORMAT_NO_NOON_MIDNIGHT);
        escherOptRecord.setRecordId(EscherOptRecord.RECORD_ID);
        escherOptRecord.addEscherProperty(new EscherSimpleProperty((short) 128, 0));
        escherOptRecord.addEscherProperty(new EscherSimpleProperty((short) 129, hSSFTextbox.getMarginLeft()));
        escherOptRecord.addEscherProperty(new EscherSimpleProperty((short) 131, hSSFTextbox.getMarginRight()));
        escherOptRecord.addEscherProperty(new EscherSimpleProperty((short) 132, hSSFTextbox.getMarginBottom()));
        escherOptRecord.addEscherProperty(new EscherSimpleProperty((short) 130, hSSFTextbox.getMarginTop()));
        addStandardOptions(hSSFTextbox, escherOptRecord);
        EscherRecord escherRecordCreateAnchor = createAnchor(hSSFTextbox.getAnchor());
        escherClientDataRecord.setRecordId(EscherClientDataRecord.RECORD_ID);
        escherClientDataRecord.setOptions((short) 0);
        this.escherTextbox.setRecordId(EscherTextboxRecord.RECORD_ID);
        this.escherTextbox.setOptions((short) 0);
        escherContainerRecord.addChildRecord(escherSpRecord);
        escherContainerRecord.addChildRecord(escherOptRecord);
        escherContainerRecord.addChildRecord(escherRecordCreateAnchor);
        escherContainerRecord.addChildRecord(escherClientDataRecord);
        escherContainerRecord.addChildRecord(this.escherTextbox);
        return escherContainerRecord;
    }

    private TextObjectRecord createTextObjectRecord(HSSFTextbox hSSFTextbox, int i) {
        TextObjectRecord textObjectRecord = new TextObjectRecord();
        textObjectRecord.setHorizontalTextAlignment((short) 1);
        textObjectRecord.setVerticalTextAlignment((short) 1);
        textObjectRecord.setTextLocked(true);
        textObjectRecord.setTextOrientation((short) 0);
        textObjectRecord.setFormattingRunLength((short) ((hSSFTextbox.getString().numFormattingRuns() + 1) * 8));
        textObjectRecord.setTextLength((short) hSSFTextbox.getString().length());
        textObjectRecord.setStr(hSSFTextbox.getString());
        textObjectRecord.setReserved7(0);
        return textObjectRecord;
    }

    @Override // org.apache.poi.hssf.model.AbstractShape
    public EscherContainerRecord getSpContainer() {
        return this.spContainer;
    }

    @Override // org.apache.poi.hssf.model.AbstractShape
    public ObjRecord getObjRecord() {
        return this.objRecord;
    }

    public TextObjectRecord getTextObjectRecord() {
        return this.textObjectRecord;
    }

    public EscherRecord getEscherTextbox() {
        return this.escherTextbox;
    }
}
