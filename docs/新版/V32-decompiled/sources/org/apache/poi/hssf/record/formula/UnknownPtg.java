package org.apache.poi.hssf.record.formula;

import android.media.MediaPlayer;
import org.apache.poi.hssf.model.Workbook;

/* JADX INFO: loaded from: classes3.dex */
public class UnknownPtg extends Ptg {
    private short size;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return MediaPlayer.CHARSET_UNKNOWN;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
    }

    public UnknownPtg() {
    }

    public UnknownPtg(byte[] bArr, int i) {
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return this.size;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        return new UnknownPtg();
    }
}
