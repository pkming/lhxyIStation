package org.apache.poi.ddf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherComplexProperty extends EscherProperty {
    byte[] complexData;

    public EscherComplexProperty(short s, byte[] bArr) {
        super(s);
        this.complexData = new byte[0];
        this.complexData = bArr;
    }

    public EscherComplexProperty(short s, boolean z, byte[] bArr) {
        super(s, true, z);
        this.complexData = new byte[0];
        this.complexData = bArr;
    }

    @Override // org.apache.poi.ddf.EscherProperty
    public int serializeSimplePart(byte[] bArr, int i) {
        LittleEndian.putShort(bArr, i, getId());
        LittleEndian.putInt(bArr, i + 2, this.complexData.length);
        return 6;
    }

    @Override // org.apache.poi.ddf.EscherProperty
    public int serializeComplexPart(byte[] bArr, int i) {
        byte[] bArr2 = this.complexData;
        System.arraycopy(bArr2, 0, bArr, i, bArr2.length);
        return this.complexData.length;
    }

    public byte[] getComplexData() {
        return this.complexData;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof EscherComplexProperty) && Arrays.equals(this.complexData, ((EscherComplexProperty) obj).complexData);
    }

    @Override // org.apache.poi.ddf.EscherProperty
    public int getPropertySize() {
        return this.complexData.length + 6;
    }

    public int hashCode() {
        return getId() * 11;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:7:0x0016 -> B:13:0x0024). Please report as a decompilation issue!!! */
    public String toString() {
        String string;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            try {
                try {
                    HexDump.dump(this.complexData, 0L, byteArrayOutputStream, 0);
                    string = byteArrayOutputStream.toString();
                    byteArrayOutputStream.close();
                } catch (Exception e) {
                    string = e.toString();
                    byteArrayOutputStream.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return new StringBuffer().append("propNum: ").append((int) getPropertyNumber()).append(", propName: ").append(EscherProperties.getPropertyName(getPropertyNumber())).append(", complex: ").append(isComplex()).append(", blipId: ").append(isBlipId()).append(", data: ").append(System.getProperty("line.separator")).append(string).toString();
        } catch (Throwable th) {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            throw th;
        }
    }
}
