package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes3.dex */
public abstract class Record {
    protected abstract void fillFields(byte[] bArr, short s, int i);

    public abstract short getSid();

    public boolean isInValueSection() {
        return false;
    }

    public boolean isValue() {
        return false;
    }

    public void processContinueRecord(byte[] bArr) {
    }

    public abstract int serialize(int i, byte[] bArr);

    protected abstract void validateSid(short s);

    public Record() {
    }

    public Record(short s, short s2, byte[] bArr) {
        validateSid(s);
        fillFields(bArr, s2);
    }

    public Record(short s, short s2, byte[] bArr, int i) {
        validateSid(s);
        fillFields(bArr, s2, i);
    }

    protected void fillFields(byte[] bArr, short s) {
        fillFields(bArr, s, 0);
    }

    public byte[] serialize() {
        byte[] bArr = new byte[getRecordSize()];
        serialize(0, bArr);
        return bArr;
    }

    public int getRecordSize() {
        return serialize().length;
    }

    public String toString() {
        return super.toString();
    }

    public Object clone() {
        throw new RuntimeException(new StringBuffer().append("The class ").append(getClass().getName()).append(" needs to define a clone method").toString());
    }
}
