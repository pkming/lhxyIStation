package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherArrayProperty extends EscherComplexProperty {
    private static final int FIXED_SIZE = 6;

    public static int getActualSizeOfElements(short s) {
        return s < 0 ? (short) ((-s) >> 2) : s;
    }

    public EscherArrayProperty(short s, byte[] bArr) {
        super(s, checkComplexData(bArr));
    }

    public EscherArrayProperty(short s, boolean z, byte[] bArr) {
        super(s, z, checkComplexData(bArr));
    }

    private static byte[] checkComplexData(byte[] bArr) {
        return (bArr == null || bArr.length == 0) ? new byte[6] : bArr;
    }

    public int getNumberOfElementsInArray() {
        return LittleEndian.getUShort(this.complexData, 0);
    }

    public void setNumberOfElementsInArray(int i) {
        int actualSizeOfElements = (getActualSizeOfElements(getSizeOfElements()) * i) + 6;
        if (actualSizeOfElements != this.complexData.length) {
            byte[] bArr = new byte[actualSizeOfElements];
            System.arraycopy(this.complexData, 0, bArr, 0, this.complexData.length);
            this.complexData = bArr;
        }
        LittleEndian.putShort(this.complexData, 0, (short) i);
    }

    public int getNumberOfElementsInMemory() {
        return LittleEndian.getUShort(this.complexData, 2);
    }

    public void setNumberOfElementsInMemory(int i) {
        int actualSizeOfElements = (getActualSizeOfElements(getSizeOfElements()) * i) + 6;
        if (actualSizeOfElements != this.complexData.length) {
            byte[] bArr = new byte[actualSizeOfElements];
            System.arraycopy(this.complexData, 0, bArr, 0, actualSizeOfElements);
            this.complexData = bArr;
        }
        LittleEndian.putShort(this.complexData, 2, (short) i);
    }

    public short getSizeOfElements() {
        return LittleEndian.getShort(this.complexData, 4);
    }

    public void setSizeOfElements(int i) {
        LittleEndian.putShort(this.complexData, 4, (short) i);
        int numberOfElementsInArray = (getNumberOfElementsInArray() * getActualSizeOfElements(getSizeOfElements())) + 6;
        if (numberOfElementsInArray != this.complexData.length) {
            byte[] bArr = new byte[numberOfElementsInArray];
            System.arraycopy(this.complexData, 0, bArr, 0, 6);
            this.complexData = bArr;
        }
    }

    public byte[] getElement(int i) {
        int actualSizeOfElements = getActualSizeOfElements(getSizeOfElements());
        byte[] bArr = new byte[actualSizeOfElements];
        System.arraycopy(this.complexData, (i * actualSizeOfElements) + 6, bArr, 0, actualSizeOfElements);
        return bArr;
    }

    public void setElement(int i, byte[] bArr) {
        int actualSizeOfElements = getActualSizeOfElements(getSizeOfElements());
        System.arraycopy(bArr, 0, this.complexData, (i * actualSizeOfElements) + 6, actualSizeOfElements);
    }

    @Override // org.apache.poi.ddf.EscherComplexProperty
    public String toString() {
        String property = System.getProperty("line.separator");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(new StringBuffer().append("    {EscherArrayProperty:").append(property).toString());
        stringBuffer.append(new StringBuffer().append("     Num Elements: ").append(getNumberOfElementsInArray()).append(property).toString());
        stringBuffer.append(new StringBuffer().append("     Num Elements In Memory: ").append(getNumberOfElementsInMemory()).append(property).toString());
        stringBuffer.append(new StringBuffer().append("     Size of elements: ").append((int) getSizeOfElements()).append(property).toString());
        for (int i = 0; i < getNumberOfElementsInArray(); i++) {
            stringBuffer.append(new StringBuffer().append("     Element ").append(i).append(": ").append(HexDump.toHex(getElement(i))).append(property).toString());
        }
        stringBuffer.append(new StringBuffer().append("}").append(property).toString());
        return new StringBuffer().append("propNum: ").append((int) getPropertyNumber()).append(", propName: ").append(EscherProperties.getPropertyName(getPropertyNumber())).append(", complex: ").append(isComplex()).append(", blipId: ").append(isBlipId()).append(", data: ").append(property).append(stringBuffer.toString()).toString();
    }

    public int setArrayData(byte[] bArr, int i) {
        short s = LittleEndian.getShort(bArr, i);
        LittleEndian.getShort(bArr, i + 2);
        int actualSizeOfElements = getActualSizeOfElements(LittleEndian.getShort(bArr, i + 4)) * s;
        if (actualSizeOfElements == this.complexData.length) {
            this.complexData = new byte[actualSizeOfElements + 6];
        }
        System.arraycopy(bArr, i, this.complexData, 0, this.complexData.length);
        return this.complexData.length;
    }
}
