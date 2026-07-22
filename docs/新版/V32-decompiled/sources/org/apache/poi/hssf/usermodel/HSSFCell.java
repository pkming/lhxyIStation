package org.apache.poi.hssf.usermodel;

import java.util.Calendar;
import java.util.Date;
import org.apache.poi.hssf.model.FormulaParser;
import org.apache.poi.hssf.model.Sheet;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.record.formula.Ptg;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFCell {
    public static final int CELL_TYPE_BLANK = 3;
    public static final int CELL_TYPE_BOOLEAN = 4;
    public static final int CELL_TYPE_ERROR = 5;
    public static final int CELL_TYPE_FORMULA = 2;
    public static final int CELL_TYPE_NUMERIC = 0;
    public static final int CELL_TYPE_STRING = 1;
    public static final short ENCODING_COMPRESSED_UNICODE = 0;
    public static final short ENCODING_UTF_16 = 1;
    private Workbook book;
    private boolean booleanValue;
    private short cellNum;
    private HSSFCellStyle cellStyle;
    private int cellType;
    private double cellValue;
    private short encoding = 0;
    private byte errorValue;
    private CellValueRecordInterface record;
    private int row;
    private Sheet sheet;
    private String stringValue;

    protected HSSFCell(Workbook workbook, Sheet sheet, int i, short s) {
        checkBounds(s);
        this.cellNum = s;
        this.row = i;
        this.cellStyle = null;
        this.cellValue = 0.0d;
        this.stringValue = null;
        this.booleanValue = false;
        this.errorValue = (byte) 0;
        this.book = workbook;
        this.sheet = sheet;
        setCellType(3, false);
        setCellStyle(new HSSFCellStyle((short) 15, workbook.getExFormatAt(15)));
    }

    protected HSSFCell(Workbook workbook, Sheet sheet, int i, short s, int i2) {
        checkBounds(s);
        this.cellNum = s;
        this.row = i;
        this.cellType = i2;
        this.cellStyle = null;
        this.cellValue = 0.0d;
        this.stringValue = null;
        this.booleanValue = false;
        this.errorValue = (byte) 0;
        this.book = workbook;
        this.sheet = sheet;
        if (i2 == 0) {
            NumberRecord numberRecord = new NumberRecord();
            this.record = numberRecord;
            numberRecord.setColumn(s);
            ((NumberRecord) this.record).setRow(i);
            ((NumberRecord) this.record).setValue(0.0d);
            ((NumberRecord) this.record).setXFIndex((short) 0);
        } else if (i2 == 1) {
            LabelSSTRecord labelSSTRecord = new LabelSSTRecord();
            this.record = labelSSTRecord;
            labelSSTRecord.setColumn(s);
            ((LabelSSTRecord) this.record).setRow(i);
            ((LabelSSTRecord) this.record).setXFIndex((short) 0);
        } else {
            if (i2 == 2) {
                FormulaRecord formulaRecord = new FormulaRecord();
                this.record = new FormulaRecordAggregate(formulaRecord, null);
                formulaRecord.setColumn(s);
                formulaRecord.setRow(i);
                formulaRecord.setXFIndex((short) 0);
            } else if (i2 == 3) {
                BlankRecord blankRecord = new BlankRecord();
                this.record = blankRecord;
                blankRecord.setColumn(s);
                ((BlankRecord) this.record).setRow(i);
                ((BlankRecord) this.record).setXFIndex((short) 0);
            } else if (i2 != 4) {
                if (i2 == 5) {
                    BoolErrRecord boolErrRecord = new BoolErrRecord();
                    this.record = boolErrRecord;
                    boolErrRecord.setColumn(s);
                    ((BoolErrRecord) this.record).setRow(i);
                    ((BoolErrRecord) this.record).setXFIndex((short) 0);
                    ((BoolErrRecord) this.record).setValue((byte) 0);
                }
            }
            BoolErrRecord boolErrRecord2 = new BoolErrRecord();
            this.record = boolErrRecord2;
            boolErrRecord2.setColumn(s);
            ((BoolErrRecord) this.record).setRow(i);
            ((BoolErrRecord) this.record).setXFIndex((short) 0);
            ((BoolErrRecord) this.record).setValue(false);
        }
        setCellStyle(new HSSFCellStyle((short) 15, workbook.getExFormatAt(15)));
    }

    protected HSSFCell(Workbook workbook, Sheet sheet, int i, CellValueRecordInterface cellValueRecordInterface) {
        this.cellNum = cellValueRecordInterface.getColumn();
        this.record = cellValueRecordInterface;
        this.row = i;
        int iDetermineType = determineType(cellValueRecordInterface);
        this.cellType = iDetermineType;
        this.cellStyle = null;
        this.stringValue = null;
        this.book = workbook;
        this.sheet = sheet;
        if (iDetermineType == 0) {
            this.cellValue = ((NumberRecord) cellValueRecordInterface).getValue();
        } else if (iDetermineType == 1) {
            this.stringValue = workbook.getSSTString(((LabelSSTRecord) cellValueRecordInterface).getSSTIndex());
        } else if (iDetermineType == 2) {
            FormulaRecordAggregate formulaRecordAggregate = (FormulaRecordAggregate) cellValueRecordInterface;
            this.cellValue = formulaRecordAggregate.getFormulaRecord().getValue();
            this.stringValue = formulaRecordAggregate.getStringValue();
        } else if (iDetermineType == 4) {
            this.booleanValue = ((BoolErrRecord) cellValueRecordInterface).getBooleanValue();
        } else if (iDetermineType == 5) {
            this.errorValue = ((BoolErrRecord) cellValueRecordInterface).getErrorValue();
        }
        setCellStyle(new HSSFCellStyle(cellValueRecordInterface.getXFIndex(), workbook.getExFormatAt(cellValueRecordInterface.getXFIndex())));
    }

    private HSSFCell() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    private int determineType(CellValueRecordInterface cellValueRecordInterface) {
        Record record = (Record) cellValueRecordInterface;
        short sid = record.getSid();
        if (sid == -2000) {
            return 2;
        }
        if (sid == 253) {
            return 1;
        }
        if (sid == 513) {
            return 3;
        }
        if (sid == 515 || sid != 517) {
            return 0;
        }
        return ((BoolErrRecord) record).isBoolean() ? 4 : 5;
    }

    public void setCellNum(short s) {
        this.cellNum = s;
        this.record.setColumn(s);
    }

    public short getCellNum() {
        return this.cellNum;
    }

    public void setCellType(int i) {
        setCellType(i, true);
    }

    private void setCellType(int i, boolean z) {
        NumberRecord numberRecord;
        LabelSSTRecord labelSSTRecord;
        FormulaRecordAggregate formulaRecordAggregate;
        BlankRecord blankRecord;
        BoolErrRecord boolErrRecord;
        BoolErrRecord boolErrRecord2;
        if (i > 5) {
            throw new RuntimeException("I have no idea what type that is!");
        }
        if (i != 0) {
            if (i == 1) {
                if (i != this.cellType) {
                    labelSSTRecord = new LabelSSTRecord();
                } else {
                    labelSSTRecord = (LabelSSTRecord) this.record;
                }
                labelSSTRecord.setColumn(getCellNum());
                labelSSTRecord.setRow(this.row);
                labelSSTRecord.setXFIndex(this.cellStyle.getIndex());
                if (z && getStringCellValue() != null && !getStringCellValue().equals("")) {
                    int iAddSSTString = this.encoding == 0 ? this.book.addSSTString(getStringCellValue()) : 0;
                    if (this.encoding == 1) {
                        iAddSSTString = this.book.addSSTString(getStringCellValue(), true);
                    }
                    labelSSTRecord.setSSTIndex(iAddSSTString);
                }
                this.record = labelSSTRecord;
            } else if (i == 2) {
                if (i != this.cellType) {
                    formulaRecordAggregate = new FormulaRecordAggregate(new FormulaRecord(), null);
                } else {
                    formulaRecordAggregate = (FormulaRecordAggregate) this.record;
                }
                formulaRecordAggregate.setColumn(getCellNum());
                if (z) {
                    formulaRecordAggregate.getFormulaRecord().setValue(getNumericCellValue());
                }
                formulaRecordAggregate.setXFIndex(this.cellStyle.getIndex());
                formulaRecordAggregate.setRow(this.row);
                this.record = formulaRecordAggregate;
            } else if (i == 3) {
                if (i != this.cellType) {
                    blankRecord = new BlankRecord();
                } else {
                    blankRecord = (BlankRecord) this.record;
                }
                blankRecord.setColumn(getCellNum());
                HSSFCellStyle hSSFCellStyle = this.cellStyle;
                if (hSSFCellStyle != null) {
                    blankRecord.setXFIndex(hSSFCellStyle.getIndex());
                } else {
                    blankRecord.setXFIndex((short) 0);
                }
                blankRecord.setRow(this.row);
                this.record = blankRecord;
            } else if (i == 4) {
                if (i != this.cellType) {
                    boolErrRecord = new BoolErrRecord();
                } else {
                    boolErrRecord = (BoolErrRecord) this.record;
                }
                boolErrRecord.setColumn(getCellNum());
                if (z) {
                    boolErrRecord.setValue(getBooleanCellValue());
                }
                boolErrRecord.setXFIndex(this.cellStyle.getIndex());
                boolErrRecord.setRow(this.row);
                this.record = boolErrRecord;
            } else if (i == 5) {
                if (i != this.cellType) {
                    boolErrRecord2 = new BoolErrRecord();
                } else {
                    boolErrRecord2 = (BoolErrRecord) this.record;
                }
                boolErrRecord2.setColumn(getCellNum());
                if (z) {
                    boolErrRecord2.setValue(getErrorCellValue());
                }
                boolErrRecord2.setXFIndex(this.cellStyle.getIndex());
                boolErrRecord2.setRow(this.row);
                this.record = boolErrRecord2;
            }
        } else {
            if (i != this.cellType) {
                numberRecord = new NumberRecord();
            } else {
                numberRecord = (NumberRecord) this.record;
            }
            numberRecord.setColumn(getCellNum());
            if (z) {
                numberRecord.setValue(getNumericCellValue());
            }
            numberRecord.setXFIndex(this.cellStyle.getIndex());
            numberRecord.setRow(this.row);
            this.record = numberRecord;
        }
        if (i != this.cellType) {
            int loc = this.sheet.getLoc();
            this.sheet.replaceValueRecord(this.record);
            this.sheet.setLoc(loc);
        }
        this.cellType = i;
    }

    public int getCellType() {
        return this.cellType;
    }

    public void setCellValue(double d) {
        int i = this.cellType;
        if (i != 0 && i != 2) {
            setCellType(0, false);
        }
        ((NumberRecord) this.record).setValue(d);
        this.cellValue = d;
    }

    public void setCellValue(Date date) {
        setCellValue(HSSFDateUtil.getExcelDate(date));
    }

    public void setCellValue(Calendar calendar) {
        setCellValue(calendar.getTime());
    }

    public void setCellValue(String str) {
        if (str == null) {
            setCellType(3, false);
            return;
        }
        int i = this.cellType;
        if (i != 1 && i != 2) {
            setCellType(1, false);
        }
        int iAddSSTString = this.encoding == 0 ? this.book.addSSTString(str) : 0;
        if (this.encoding == 1) {
            iAddSSTString = this.book.addSSTString(str, true);
        }
        ((LabelSSTRecord) this.record).setSSTIndex(iAddSSTString);
        this.stringValue = str;
    }

    public void setCellFormula(String str) {
        if (str == null) {
            setCellType(3, false);
            return;
        }
        setCellType(2, false);
        FormulaRecordAggregate formulaRecordAggregate = (FormulaRecordAggregate) this.record;
        formulaRecordAggregate.getFormulaRecord().setOptions((short) 2);
        formulaRecordAggregate.getFormulaRecord().setValue(0.0d);
        if (formulaRecordAggregate.getXFIndex() == 0) {
            formulaRecordAggregate.setXFIndex((short) 15);
        }
        FormulaParser formulaParser = new FormulaParser(new StringBuffer().append(str).append(";").toString(), this.book);
        formulaParser.parse();
        Ptg[] rPNPtg = formulaParser.getRPNPtg();
        int size = 0;
        for (int i = 0; i < rPNPtg.length; i++) {
            size += rPNPtg[i].getSize();
            formulaRecordAggregate.getFormulaRecord().pushExpressionToken(rPNPtg[i]);
        }
        formulaRecordAggregate.getFormulaRecord().setExpressionLength((short) size);
    }

    public String getCellFormula() {
        return FormulaParser.toFormulaString(this.book, ((FormulaRecordAggregate) this.record).getFormulaRecord().getParsedExpression());
    }

    public double getNumericCellValue() {
        int i = this.cellType;
        if (i == 3) {
            return 0.0d;
        }
        if (i == 1) {
            throw new NumberFormatException("You cannot get a numeric value from a String based cell");
        }
        if (i == 4) {
            throw new NumberFormatException("You cannot get a numeric value from a boolean cell");
        }
        if (i == 5) {
            throw new NumberFormatException("You cannot get a numeric value from an error cell");
        }
        return this.cellValue;
    }

    public Date getDateCellValue() {
        int i = this.cellType;
        if (i == 3) {
            return null;
        }
        if (i == 1) {
            throw new NumberFormatException("You cannot get a date value from a String based cell");
        }
        if (i == 4) {
            throw new NumberFormatException("You cannot get a date value from a boolean cell");
        }
        if (i == 5) {
            throw new NumberFormatException("You cannot get a date value from an error cell");
        }
        if (this.book.isUsing1904DateWindowing()) {
            return HSSFDateUtil.getJavaDate(this.cellValue, true);
        }
        return HSSFDateUtil.getJavaDate(this.cellValue, false);
    }

    public String getStringCellValue() {
        int i = this.cellType;
        if (i == 3) {
            return "";
        }
        if (i == 0) {
            throw new NumberFormatException("You cannot get a string value from a numeric cell");
        }
        if (i == 4) {
            throw new NumberFormatException("You cannot get a string value from a boolean cell");
        }
        if (i != 5) {
            return (i == 2 && this.stringValue == null) ? "" : this.stringValue;
        }
        throw new NumberFormatException("You cannot get a string value from an error cell");
    }

    public void setCellValue(boolean z) {
        int i = this.cellType;
        if (i != 4 && i != 2) {
            setCellType(4, false);
        }
        ((BoolErrRecord) this.record).setValue(z);
        this.booleanValue = z;
    }

    public void setCellErrorValue(byte b) {
        int i = this.cellType;
        if (i != 5 && i != 2) {
            setCellType(5, false);
        }
        ((BoolErrRecord) this.record).setValue(b);
        this.errorValue = b;
    }

    public boolean getBooleanCellValue() {
        int i = this.cellType;
        if (i == 4) {
            return this.booleanValue;
        }
        if (i == 3) {
            return false;
        }
        throw new NumberFormatException("You cannot get a boolean value from a non-boolean cell");
    }

    public byte getErrorCellValue() {
        int i = this.cellType;
        if (i == 5) {
            return this.errorValue;
        }
        if (i == 3) {
            return (byte) 0;
        }
        throw new NumberFormatException("You cannot get an error value from a non-error cell");
    }

    public void setCellStyle(HSSFCellStyle hSSFCellStyle) {
        this.cellStyle = hSSFCellStyle;
        this.record.setXFIndex(hSSFCellStyle.getIndex());
    }

    public HSSFCellStyle getCellStyle() {
        return this.cellStyle;
    }

    public short getEncoding() {
        return this.encoding;
    }

    public void setEncoding(short s) {
        this.encoding = s;
    }

    protected CellValueRecordInterface getCellValueRecord() {
        return this.record;
    }

    private void checkBounds(int i) {
        if (i > 255) {
            throw new RuntimeException("You cannot have more than 255 columns in a given row (IV).  Because Excel can't handle it");
        }
        if (i < 0) {
            throw new RuntimeException("You cannot reference columns with an index of less then 0.");
        }
    }

    public void setAsActiveCell() {
        this.sheet.setActiveCellRow(this.row);
        this.sheet.setActiveCellCol(this.cellNum);
    }
}
