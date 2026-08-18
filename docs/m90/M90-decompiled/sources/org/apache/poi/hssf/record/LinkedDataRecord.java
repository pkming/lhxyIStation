package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class LinkedDataRecord extends Record {
    public static final byte LINK_TYPE_CATEGORIES = 2;
    public static final byte LINK_TYPE_TITLE_OR_TEXT = 0;
    public static final byte LINK_TYPE_VALUES = 1;
    public static final byte REFERENCE_TYPE_DEFAULT_CATEGORIES = 0;
    public static final byte REFERENCE_TYPE_DIRECT = 1;
    public static final byte REFERENCE_TYPE_ERROR_REPORTED = 4;
    public static final byte REFERENCE_TYPE_NOT_USED = 3;
    public static final byte REFERENCE_TYPE_WORKSHEET = 2;
    public static final short sid = 4177;
    private BitField customNumberFormat;
    private byte field_1_linkType;
    private byte field_2_referenceType;
    private short field_3_options;
    private short field_4_indexNumberFmtRecord;
    private LinkedDataFormulaField field_5_formulaOfLink;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public LinkedDataRecord() {
        this.customNumberFormat = new BitField(1);
    }

    public LinkedDataRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.customNumberFormat = new BitField(1);
    }

    public LinkedDataRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.customNumberFormat = new BitField(1);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4177) {
            throw new RecordFormatException("Not a LinkedData record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_linkType = bArr[0 + i];
        this.field_2_referenceType = bArr[1 + i];
        this.field_3_options = LittleEndian.getShort(bArr, 2 + i);
        this.field_4_indexNumberFmtRecord = LittleEndian.getShort(bArr, 4 + i);
        LinkedDataFormulaField linkedDataFormulaField = new LinkedDataFormulaField();
        this.field_5_formulaOfLink = linkedDataFormulaField;
        linkedDataFormulaField.fillField(bArr, s, i + 0 + 6);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[AI]\n");
        stringBuffer.append("    .linkType             = ").append("0x").append(HexDump.toHex(getLinkType())).append(" (").append((int) getLinkType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .referenceType        = ").append("0x").append(HexDump.toHex(getReferenceType())).append(" (").append((int) getReferenceType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .customNumberFormat       = ").append(isCustomNumberFormat()).append('\n');
        stringBuffer.append("    .indexNumberFmtRecord = ").append("0x").append(HexDump.toHex(getIndexNumberFmtRecord())).append(" (").append((int) getIndexNumberFmtRecord()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .formulaOfLink        = ").append(" (").append(getFormulaOfLink()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/AI]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        bArr[i + 4 + 0] = this.field_1_linkType;
        bArr[i + 5 + 0] = this.field_2_referenceType;
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_3_options);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_4_indexNumberFmtRecord);
        this.field_5_formulaOfLink.serializeField(10 + i, bArr);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return this.field_5_formulaOfLink.getSize() + 10;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        LinkedDataRecord linkedDataRecord = new LinkedDataRecord();
        linkedDataRecord.field_1_linkType = this.field_1_linkType;
        linkedDataRecord.field_2_referenceType = this.field_2_referenceType;
        linkedDataRecord.field_3_options = this.field_3_options;
        linkedDataRecord.field_4_indexNumberFmtRecord = this.field_4_indexNumberFmtRecord;
        linkedDataRecord.field_5_formulaOfLink = (LinkedDataFormulaField) this.field_5_formulaOfLink.clone();
        return linkedDataRecord;
    }

    public byte getLinkType() {
        return this.field_1_linkType;
    }

    public void setLinkType(byte b) {
        this.field_1_linkType = b;
    }

    public byte getReferenceType() {
        return this.field_2_referenceType;
    }

    public void setReferenceType(byte b) {
        this.field_2_referenceType = b;
    }

    public short getOptions() {
        return this.field_3_options;
    }

    public void setOptions(short s) {
        this.field_3_options = s;
    }

    public short getIndexNumberFmtRecord() {
        return this.field_4_indexNumberFmtRecord;
    }

    public void setIndexNumberFmtRecord(short s) {
        this.field_4_indexNumberFmtRecord = s;
    }

    public LinkedDataFormulaField getFormulaOfLink() {
        return this.field_5_formulaOfLink;
    }

    public void setFormulaOfLink(LinkedDataFormulaField linkedDataFormulaField) {
        this.field_5_formulaOfLink = linkedDataFormulaField;
    }

    public void setCustomNumberFormat(boolean z) {
        this.field_3_options = this.customNumberFormat.setShortBoolean(this.field_3_options, z);
    }

    public boolean isCustomNumberFormat() {
        return this.customNumberFormat.isSet(this.field_3_options);
    }
}
