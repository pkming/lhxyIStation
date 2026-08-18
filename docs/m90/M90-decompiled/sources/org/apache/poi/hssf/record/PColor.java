package org.apache.poi.hssf.record;

/* JADX INFO: compiled from: PaletteRecord.java */
/* JADX INFO: loaded from: classes3.dex */
class PColor {
    public byte blue;
    public byte green;
    public byte red;

    public PColor(byte b, byte b2, byte b3) {
        this.red = b;
        this.green = b2;
        this.blue = b3;
    }

    public void serialize(byte[] bArr, int i) {
        bArr[i + 0] = this.red;
        bArr[i + 1] = this.green;
        bArr[i + 2] = this.blue;
        bArr[i + 3] = 0;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("  red           = ").append(this.red & 255).append('\n');
        stringBuffer.append("  green         = ").append(this.green & 255).append('\n');
        stringBuffer.append("  blue          = ").append(this.blue & 255).append('\n');
        return stringBuffer.toString();
    }
}
