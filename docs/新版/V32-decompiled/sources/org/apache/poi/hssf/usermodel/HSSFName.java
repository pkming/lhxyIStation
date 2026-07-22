package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.util.RangeAddress;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFName {
    private Workbook book;
    private NameRecord name;

    protected HSSFName(Workbook workbook, NameRecord nameRecord) {
        this.book = workbook;
        this.name = nameRecord;
    }

    public String getSheetName() {
        return this.book.findSheetNameFromExternSheet(this.name.getExternSheetNumber());
    }

    public String getNameName() {
        return this.name.getNameText();
    }

    public void setNameName(String str) {
        this.name.setNameText(str);
        this.name.setNameTextLength((byte) str.length());
    }

    public String getReference() {
        return this.name.getAreaReference(this.book);
    }

    private void setSheetName(String str) {
        this.name.setExternSheetNumber(this.book.checkExternSheet(this.book.getSheetIndex(str)));
    }

    public void setReference(String str) {
        RangeAddress rangeAddress = new RangeAddress(str);
        String sheetName = rangeAddress.getSheetName();
        if (rangeAddress.hasSheetName()) {
            setSheetName(sheetName);
        }
        this.name.setAreaReference(str);
    }
}
