package org.apache.poi.hssf.usermodel;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.model.Sheet;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.record.ImageRecord;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.VCenterRecord;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFSheet {
    public static final short BottomMargin = 3;
    private static final int DEBUG = 1;
    public static final int INITIAL_CAPACITY = 20;
    public static final short LeftMargin = 0;
    public static final byte PANE_LOWER_LEFT = 2;
    public static final byte PANE_LOWER_RIGHT = 0;
    public static final byte PANE_UPPER_LEFT = 3;
    public static final byte PANE_UPPER_RIGHT = 1;
    public static final short RightMargin = 1;
    public static final short TopMargin = 2;
    static /* synthetic */ Class class$org$apache$poi$hssf$usermodel$HSSFSheet;
    private static POILogger log;
    private Workbook book;
    private int firstrow;
    private int lastrow;
    private TreeMap rows;
    private Sheet sheet;

    static {
        Class clsClass$ = class$org$apache$poi$hssf$usermodel$HSSFSheet;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.hssf.usermodel.HSSFSheet");
            class$org$apache$poi$hssf$usermodel$HSSFSheet = clsClass$;
        }
        log = POILogFactory.getLogger(clsClass$);
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    protected HSSFSheet(Workbook workbook) {
        this.sheet = Sheet.createSheet();
        this.rows = new TreeMap();
        this.book = workbook;
    }

    protected HSSFSheet(Workbook workbook, Sheet sheet) {
        this.sheet = sheet;
        this.rows = new TreeMap();
        this.book = workbook;
        setPropertiesFromSheet(sheet);
    }

    HSSFSheet cloneSheet(Workbook workbook) {
        return new HSSFSheet(workbook, this.sheet.cloneSheet());
    }

    public void setPassword(String str) {
        this.sheet.getPassword().setPassword(str);
    }

    private void setPropertiesFromSheet(Sheet sheet) {
        int loc = sheet.getLoc();
        RowRecord nextRow = sheet.getNextRow();
        while (nextRow != null) {
            createRowFromRecord(nextRow);
            nextRow = sheet.getNextRow();
        }
        sheet.setLoc(loc);
        CellValueRecordInterface nextValueRecord = sheet.getNextValueRecord();
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (log.check(1)) {
            log.log(1, "Time at start of cell creating in HSSF sheet = ", new Long(jCurrentTimeMillis));
        }
        HSSFRow hSSFRow = null;
        while (nextValueRecord != null) {
            long jCurrentTimeMillis2 = System.currentTimeMillis();
            HSSFRow row = (hSSFRow == null || hSSFRow.getRowNum() != nextValueRecord.getRow()) ? getRow(nextValueRecord.getRow()) : hSSFRow;
            if (row != null) {
                if (log.check(1)) {
                    log.log(1, new StringBuffer().append("record id = ").append(Integer.toHexString(((Record) nextValueRecord).getSid())).toString());
                }
                row.createCellFromRecord(nextValueRecord);
                nextValueRecord = sheet.getNextValueRecord();
                if (log.check(1)) {
                    log.log(1, "record took ", new Long(System.currentTimeMillis() - jCurrentTimeMillis2));
                }
                hSSFRow = row;
            } else {
                nextValueRecord = null;
            }
        }
        if (log.check(1)) {
            log.log(1, "total sheet cell creation took ", new Long(System.currentTimeMillis() - jCurrentTimeMillis));
        }
    }

    public HSSFRow createRow(int i) {
        HSSFRow hSSFRow = new HSSFRow(this.book, this.sheet, i);
        addRow(hSSFRow, true);
        return hSSFRow;
    }

    private HSSFRow createRowFromRecord(RowRecord rowRecord) {
        HSSFRow hSSFRow = new HSSFRow(this.book, this.sheet, rowRecord);
        addRow(hSSFRow, false);
        return hSSFRow;
    }

    public void removeRow(HSSFRow hSSFRow) {
        Sheet sheet = this.sheet;
        sheet.setLoc(sheet.getDimsLoc());
        if (this.rows.size() > 0) {
            this.rows.remove(hSSFRow);
            if (hSSFRow.getRowNum() == getLastRowNum()) {
                this.lastrow = findLastRow(this.lastrow);
            }
            if (hSSFRow.getRowNum() == getFirstRowNum()) {
                this.firstrow = findFirstRow(this.firstrow);
            }
            Iterator itCellIterator = hSSFRow.cellIterator();
            while (itCellIterator.hasNext()) {
                this.sheet.removeValueRecord(hSSFRow.getRowNum(), ((HSSFCell) itCellIterator.next()).getCellValueRecord());
            }
            this.sheet.removeRow(hSSFRow.getRowRecord());
        }
    }

    private int findLastRow(int i) {
        int i2 = i - 1;
        HSSFRow row = getRow(i2);
        while (row == null && i2 >= 0) {
            i2--;
            row = getRow(i2);
        }
        return i2;
    }

    private int findFirstRow(int i) {
        int i2 = i + 1;
        HSSFRow row = getRow(i2);
        while (row == null && i2 <= getLastRowNum()) {
            i2++;
            row = getRow(i2);
        }
        if (i2 > getLastRowNum()) {
            return -1;
        }
        return i2;
    }

    private void addRow(HSSFRow hSSFRow, boolean z) {
        this.rows.put(hSSFRow, hSSFRow);
        if (z) {
            this.sheet.addRow(hSSFRow.getRowRecord());
        }
        if (hSSFRow.getRowNum() > getLastRowNum()) {
            this.lastrow = hSSFRow.getRowNum();
        }
        if (hSSFRow.getRowNum() < getFirstRowNum()) {
            this.firstrow = hSSFRow.getRowNum();
        }
    }

    public HSSFRow getRow(int i) {
        HSSFRow hSSFRow = new HSSFRow();
        hSSFRow.setRowNum(i);
        return (HSSFRow) this.rows.get(hSSFRow);
    }

    public int getPhysicalNumberOfRows() {
        return this.rows.size();
    }

    public int getFirstRowNum() {
        return this.firstrow;
    }

    public int getLastRowNum() {
        return this.lastrow;
    }

    public void setColumnWidth(short s, short s2) {
        this.sheet.setColumnWidth(s, s2);
    }

    public short getColumnWidth(short s) {
        return this.sheet.getColumnWidth(s);
    }

    public short getDefaultColumnWidth() {
        return this.sheet.getDefaultColumnWidth();
    }

    public short getDefaultRowHeight() {
        return this.sheet.getDefaultRowHeight();
    }

    public float getDefaultRowHeightInPoints() {
        return this.sheet.getDefaultRowHeight() / 20;
    }

    public void setDefaultColumnWidth(short s) {
        this.sheet.setDefaultColumnWidth(s);
    }

    public void setDefaultRowHeight(short s) {
        this.sheet.setDefaultRowHeight(s);
    }

    public void setDefaultRowHeightInPoints(float f) {
        this.sheet.setDefaultRowHeight((short) (f * 20.0f));
    }

    public boolean isGridsPrinted() {
        return this.sheet.isGridsPrinted();
    }

    public void setGridsPrinted(boolean z) {
        this.sheet.setGridsPrinted(z);
    }

    public int addMergedRegion(Region region) {
        return this.sheet.addMergedRegion(region.getRowFrom(), region.getColumnFrom(), region.getRowTo(), region.getColumnTo());
    }

    public void setVerticallyCenter(boolean z) {
        ((VCenterRecord) this.sheet.findFirstRecordBySid((short) 132)).setVCenter(z);
    }

    public boolean getVerticallyCenter(boolean z) {
        return ((VCenterRecord) this.sheet.findFirstRecordBySid((short) 132)).getVCenter();
    }

    public void setHorizontallyCenter(boolean z) {
        ((HCenterRecord) this.sheet.findFirstRecordBySid((short) 131)).setHCenter(z);
    }

    public boolean getHorizontallyCenter() {
        return ((HCenterRecord) this.sheet.findFirstRecordBySid((short) 131)).getHCenter();
    }

    public void removeMergedRegion(int i) {
        this.sheet.removeMergedRegion(i);
    }

    public int getNumMergedRegions() {
        return this.sheet.getNumMergedRegions();
    }

    public Region getMergedRegionAt(int i) {
        return new Region(this.sheet.getMergedRegionAt(i));
    }

    public Iterator rowIterator() {
        return this.rows.values().iterator();
    }

    protected Sheet getSheet() {
        return this.sheet;
    }

    public void setAlternativeExpression(boolean z) {
        ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).setAlternateExpression(z);
    }

    public void setAlternativeFormula(boolean z) {
        ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).setAlternateFormula(z);
    }

    public void setAutobreaks(boolean z) {
        ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).setAutobreaks(z);
    }

    public void setDialog(boolean z) {
        ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).setDialog(z);
    }

    public void setDisplayGuts(boolean z) {
        ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).setDisplayGuts(z);
    }

    public void setFitToPage(boolean z) {
        ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).setFitToPage(z);
    }

    public void setRowSumsBelow(boolean z) {
        ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).setRowSumsBelow(z);
    }

    public void setRowSumsRight(boolean z) {
        ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).setRowSumsRight(z);
    }

    public boolean getAlternateExpression() {
        return ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).getAlternateExpression();
    }

    public boolean getAlternateFormula() {
        return ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).getAlternateFormula();
    }

    public boolean getAutobreaks() {
        return ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).getAutobreaks();
    }

    public boolean getDialog() {
        return ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).getDialog();
    }

    public boolean getDisplayGuts() {
        return ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).getDisplayGuts();
    }

    public boolean getFitToPage() {
        return ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).getFitToPage();
    }

    public boolean getRowSumsBelow() {
        return ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).getRowSumsBelow();
    }

    public boolean getRowSumsRight() {
        return ((WSBoolRecord) this.sheet.findFirstRecordBySid((short) 129)).getRowSumsRight();
    }

    public boolean isPrintGridlines() {
        return getSheet().getPrintGridlines().getPrintGridlines();
    }

    public void setPrintGridlines(boolean z) {
        getSheet().getPrintGridlines().setPrintGridlines(z);
    }

    public HSSFPrintSetup getPrintSetup() {
        return new HSSFPrintSetup(getSheet().getPrintSetup());
    }

    public HSSFHeader getHeader() {
        return new HSSFHeader(getSheet().getHeader());
    }

    public HSSFFooter getFooter() {
        return new HSSFFooter(getSheet().getFooter());
    }

    public void setSelected(boolean z) {
        getSheet().setSelected(z);
    }

    public double getMargin(short s) {
        return getSheet().getMargin(s);
    }

    public void setMargin(short s, double d) {
        getSheet().setMargin(s, d);
    }

    public boolean getProtect() {
        return getSheet().getProtect().getProtect();
    }

    public void setProtect(boolean z) {
        getSheet().getProtect().setProtect(z);
    }

    public void setZoom(int i, int i2) {
        if (i < 1 || i > 65535) {
            throw new IllegalArgumentException("Numerator must be greater than 1 and less than 65536");
        }
        if (i2 < 1 || i2 > 65535) {
            throw new IllegalArgumentException("Denominator must be greater than 1 and less than 65536");
        }
        SCLRecord sCLRecord = new SCLRecord();
        sCLRecord.setNumerator((short) i);
        sCLRecord.setDenominator((short) i2);
        getSheet().setSCLRecord(sCLRecord);
    }

    protected void shiftMerged(int i, int i2, int i3, boolean z) {
        ArrayList arrayList = new ArrayList();
        int i4 = 0;
        while (i4 < getNumMergedRegions()) {
            Region mergedRegionAt = getMergedRegionAt(i4);
            boolean z2 = mergedRegionAt.getRowFrom() >= i || mergedRegionAt.getRowTo() >= i;
            boolean z3 = mergedRegionAt.getRowTo() <= i2 || mergedRegionAt.getRowFrom() <= i2;
            if (z2 && z3 && !mergedRegionAt.contains(i - 1, (short) 0) && !mergedRegionAt.contains(i2 + 1, (short) 0)) {
                mergedRegionAt.setRowFrom(mergedRegionAt.getRowFrom() + i3);
                mergedRegionAt.setRowTo(mergedRegionAt.getRowTo() + i3);
                arrayList.add(mergedRegionAt);
                removeMergedRegion(i4);
                i4--;
            }
            i4++;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            addMergedRegion((Region) it.next());
        }
    }

    public void shiftRows(int i, int i2, int i3) {
        shiftRows(i, i2, i3, false, false);
    }

    public void shiftRows(int i, int i2, int i3, boolean z, boolean z2) {
        int i4;
        int i5;
        if (i3 < 0) {
            i5 = i;
            i4 = 1;
        } else {
            i4 = -1;
            i5 = i2;
        }
        shiftMerged(i, i2, i3, true);
        this.sheet.shiftRowBreaks(i, i2, i3);
        while (i5 >= i && i5 <= i2 && i5 >= 0 && i5 < 65536) {
            HSSFRow row = getRow(i5);
            int i6 = i5 + i3;
            HSSFRow row2 = getRow(i6);
            if (row2 == null) {
                row2 = createRow(i6);
            }
            for (short firstCellNum = row2.getFirstCellNum(); firstCellNum <= row2.getLastCellNum(); firstCellNum = (short) (firstCellNum + 1)) {
                HSSFCell cell = row2.getCell(firstCellNum);
                if (cell != null) {
                    row2.removeCell(cell);
                }
            }
            if (row != null) {
                if (z) {
                    row2.setHeight(row.getHeight());
                }
                if (z2) {
                    row.setHeight((short) 255);
                }
                for (short firstCellNum2 = row.getFirstCellNum(); firstCellNum2 <= row.getLastCellNum(); firstCellNum2 = (short) (firstCellNum2 + 1)) {
                    HSSFCell cell2 = row.getCell(firstCellNum2);
                    if (cell2 != null) {
                        row.removeCell(cell2);
                        CellValueRecordInterface cellValueRecord = cell2.getCellValueRecord();
                        cellValueRecord.setRow(i6);
                        row2.createCellFromRecord(cellValueRecord);
                        this.sheet.addValueRecord(i6, cellValueRecord);
                    }
                }
            }
            i5 += i4;
        }
        int i7 = this.lastrow;
        if (i2 == i7 || i2 + i3 > i7) {
            this.lastrow = Math.min(i2 + i3, 65535);
        }
        int i8 = this.firstrow;
        if (i == i8 || i + i3 < i8) {
            this.firstrow = Math.max(i + i3, 0);
        }
    }

    protected void insertChartRecords(List list) {
        this.sheet.getRecords().addAll(this.sheet.findFirstRecordLocBySid((short) 574), list);
    }

    public void insertImageRecords(ImageRecord imageRecord) {
        this.sheet.getRecords().add(this.sheet.findFirstRecordLocBySid((short) 574), imageRecord);
    }

    public void createFreezePane(int i, int i2, int i3, int i4) {
        if (i < 0 || i > 255) {
            throw new IllegalArgumentException("Column must be between 0 and 255");
        }
        if (i2 < 0 || i2 > 65535) {
            throw new IllegalArgumentException("Row must be between 0 and 65535");
        }
        if (i3 < i) {
            throw new IllegalArgumentException("leftmostColumn parameter must not be less than colSplit parameter");
        }
        if (i4 < i2) {
            throw new IllegalArgumentException("topRow parameter must not be less than leftmostColumn parameter");
        }
        getSheet().createFreezePane(i, i2, i4, i3);
    }

    public void createFreezePane(int i, int i2) {
        createFreezePane(i, i2, i, i2);
    }

    public void createSplitPane(int i, int i2, int i3, int i4, int i5) {
        getSheet().createSplitPane(i, i2, i4, i3, i5);
    }

    public void setDisplayGridlines(boolean z) {
        this.sheet.setDisplayGridlines(z);
    }

    public boolean isDisplayGridlines() {
        return this.sheet.isDisplayGridlines();
    }

    public void setDisplayFormulas(boolean z) {
        this.sheet.setDisplayFormulas(z);
    }

    public boolean isDisplayFormulas() {
        return this.sheet.isDisplayFormulas();
    }

    public void setDisplayRowColHeadings(boolean z) {
        this.sheet.setDisplayRowColHeadings(z);
    }

    public boolean isDisplayRowColHeadings() {
        return this.sheet.isDisplayRowColHeadings();
    }

    public void setRowBreak(int i) {
        validateRow(i);
        this.sheet.setRowBreak(i, (short) 0, (short) 255);
    }

    public boolean isRowBroken(int i) {
        return this.sheet.isRowBroken(i);
    }

    public void removeRowBreak(int i) {
        this.sheet.removeRowBreak(i);
    }

    public int[] getRowBreaks() {
        int[] iArr = new int[this.sheet.getNumRowBreaks()];
        Iterator rowBreaks = this.sheet.getRowBreaks();
        int i = 0;
        while (rowBreaks.hasNext()) {
            iArr[i] = ((PageBreakRecord.Break) rowBreaks.next()).main;
            i++;
        }
        return iArr;
    }

    public short[] getColumnBreaks() {
        short[] sArr = new short[this.sheet.getNumColumnBreaks()];
        Iterator columnBreaks = this.sheet.getColumnBreaks();
        int i = 0;
        while (columnBreaks.hasNext()) {
            sArr[i] = ((PageBreakRecord.Break) columnBreaks.next()).main;
            i++;
        }
        return sArr;
    }

    public void setColumnBreak(short s) {
        validateColumn(s);
        this.sheet.setColumnBreak(s, (short) 0, (short) -1);
    }

    public boolean isColumnBroken(short s) {
        return this.sheet.isColumnBroken(s);
    }

    public void removeColumnBreak(short s) {
        this.sheet.removeColumnBreak(s);
    }

    protected void validateRow(int i) {
        if (i > 65535) {
            throw new IllegalArgumentException("Maximum row number is 65535");
        }
        if (i < 0) {
            throw new IllegalArgumentException("Minumum row number is 0");
        }
    }

    protected void validateColumn(short s) {
        if (s > 255) {
            throw new IllegalArgumentException("Maximum column number is 255");
        }
        if (s < 0) {
            throw new IllegalArgumentException("Minimum column number is 0");
        }
    }

    public void dumpDrawingRecords() {
        this.sheet.aggregateDrawingRecords(this.book.getDrawingManager());
        for (EscherRecord escherRecord : ((EscherAggregate) getSheet().findFirstRecordBySid(EscherAggregate.sid)).getEscherRecords()) {
            PrintWriter printWriter = new PrintWriter(System.out);
            escherRecord.display(printWriter, 0);
            printWriter.close();
        }
    }

    public HSSFPatriarch createDrawingPatriarch() {
        this.book.createDrawingGroup();
        this.sheet.aggregateDrawingRecords(this.book.getDrawingManager());
        EscherAggregate escherAggregate = (EscherAggregate) this.sheet.findFirstRecordBySid(EscherAggregate.sid);
        HSSFPatriarch hSSFPatriarch = new HSSFPatriarch(this);
        escherAggregate.clear();
        escherAggregate.setPatriarch(hSSFPatriarch);
        return hSSFPatriarch;
    }

    public void setColumnGroupCollapsed(short s, boolean z) {
        this.sheet.setColumnGroupCollapsed(s, z);
    }

    public void groupColumn(short s, short s2) {
        this.sheet.groupColumnRange(s, s2, true);
    }

    public void ungroupColumn(short s, short s2) {
        this.sheet.groupColumnRange(s, s2, false);
    }

    public void groupRow(int i, int i2) {
        this.sheet.groupRowRange(i, i2, true);
    }

    public void ungroupRow(int i, int i2) {
        this.sheet.groupRowRange(i, i2, false);
    }

    public void setRowGroupCollapsed(int i, boolean z) {
        this.sheet.setRowGroupCollapsed(i, z);
    }
}
