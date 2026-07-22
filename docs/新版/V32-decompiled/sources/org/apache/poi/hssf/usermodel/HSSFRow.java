package org.apache.poi.hssf.usermodel;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.hssf.model.Sheet;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.RowRecord;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFRow implements Comparable {
    public static final int INITIAL_CAPACITY = 5;
    private Workbook book;
    private HashMap cells;
    private RowRecord row;
    private int rowNum;
    private Sheet sheet;

    protected HSSFRow() {
    }

    protected HSSFRow(Workbook workbook, Sheet sheet, int i) {
        this.rowNum = i;
        this.cells = new HashMap(10);
        this.book = workbook;
        this.sheet = sheet;
        RowRecord rowRecord = new RowRecord();
        this.row = rowRecord;
        rowRecord.setOptionFlags((short) 256);
        this.row.setHeight((short) 255);
        this.row.setLastCol((short) -1);
        this.row.setFirstCol((short) -1);
        setRowNum(i);
    }

    protected HSSFRow(Workbook workbook, Sheet sheet, RowRecord rowRecord) {
        this.cells = new HashMap();
        this.book = workbook;
        this.sheet = sheet;
        this.row = rowRecord;
        setRowNum(rowRecord.getRowNumber());
    }

    public HSSFCell createCell(short s) {
        HSSFCell hSSFCell = new HSSFCell(this.book, this.sheet, getRowNum(), s);
        addCell(hSSFCell);
        this.sheet.addValueRecord(getRowNum(), hSSFCell.getCellValueRecord());
        return hSSFCell;
    }

    public HSSFCell createCell(short s, int i) {
        HSSFCell hSSFCell = new HSSFCell(this.book, this.sheet, getRowNum(), s, i);
        addCell(hSSFCell);
        this.sheet.addValueRecord(getRowNum(), hSSFCell.getCellValueRecord());
        return hSSFCell;
    }

    public void removeCell(HSSFCell hSSFCell) {
        this.sheet.removeValueRecord(getRowNum(), hSSFCell.getCellValueRecord());
        this.cells.remove(new Integer(hSSFCell.getCellNum()));
        if (hSSFCell.getCellNum() == this.row.getLastCol()) {
            RowRecord rowRecord = this.row;
            rowRecord.setLastCol(findLastCell(rowRecord.getLastCol()));
        }
        if (hSSFCell.getCellNum() == this.row.getFirstCol()) {
            RowRecord rowRecord2 = this.row;
            rowRecord2.setFirstCol(findFirstCell(rowRecord2.getFirstCol()));
        }
    }

    protected HSSFCell createCellFromRecord(CellValueRecordInterface cellValueRecordInterface) {
        HSSFCell hSSFCell = new HSSFCell(this.book, this.sheet, getRowNum(), cellValueRecordInterface);
        addCell(hSSFCell);
        return hSSFCell;
    }

    public void setRowNum(int i) {
        this.rowNum = i;
        RowRecord rowRecord = this.row;
        if (rowRecord != null) {
            rowRecord.setRowNumber(i);
        }
    }

    public int getRowNum() {
        return this.rowNum;
    }

    private void addCell(HSSFCell hSSFCell) {
        if (this.row.getFirstCol() == -1) {
            this.row.setFirstCol(hSSFCell.getCellNum());
        }
        if (this.row.getLastCol() == -1) {
            this.row.setLastCol(hSSFCell.getCellNum());
        }
        this.cells.put(new Integer(hSSFCell.getCellNum()), hSSFCell);
        if (hSSFCell.getCellNum() < this.row.getFirstCol()) {
            this.row.setFirstCol(hSSFCell.getCellNum());
        }
        if (hSSFCell.getCellNum() > this.row.getLastCol()) {
            this.row.setLastCol(hSSFCell.getCellNum());
        }
    }

    public HSSFCell getCell(short s) {
        return (HSSFCell) this.cells.get(new Integer(s));
    }

    public short getFirstCellNum() {
        if (getPhysicalNumberOfCells() == 0) {
            return (short) -1;
        }
        return this.row.getFirstCol();
    }

    public short getLastCellNum() {
        if (getPhysicalNumberOfCells() == 0) {
            return (short) -1;
        }
        return this.row.getLastCol();
    }

    public int getPhysicalNumberOfCells() {
        HashMap map = this.cells;
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public void setHeight(short s) {
        this.row.setBadFontHeight(true);
        this.row.setHeight(s);
    }

    public void setHeightInPoints(float f) {
        this.row.setBadFontHeight(true);
        this.row.setHeight((short) (f * 20.0f));
    }

    public short getHeight() {
        return this.row.getHeight();
    }

    public float getHeightInPoints() {
        return this.row.getHeight() / 20;
    }

    protected RowRecord getRowRecord() {
        return this.row;
    }

    private short findLastCell(short s) {
        short s2 = (short) (s - 1);
        HSSFCell cell = getCell(s2);
        while (cell == null && s2 >= 0) {
            s2 = (short) (s2 - 1);
            cell = getCell(s2);
        }
        return s2;
    }

    private short findFirstCell(short s) {
        short s2 = (short) (s + 1);
        HSSFCell cell = getCell(s2);
        while (cell == null && s2 <= getLastCellNum()) {
            s2 = (short) (s2 + 1);
            cell = getCell(s2);
        }
        if (s2 > getLastCellNum()) {
            return (short) -1;
        }
        return s2;
    }

    public Iterator cellIterator() {
        return this.cells.values().iterator();
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        HSSFRow hSSFRow = (HSSFRow) obj;
        if (getRowNum() == hSSFRow.getRowNum()) {
            return 0;
        }
        return (getRowNum() >= hSSFRow.getRowNum() && getRowNum() > hSSFRow.getRowNum()) ? 1 : -1;
    }

    public boolean equals(Object obj) {
        return (obj instanceof HSSFRow) && getRowNum() == ((HSSFRow) obj).getRowNum();
    }
}
