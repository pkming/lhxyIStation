package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BottomMarginRecord;
import org.apache.poi.hssf.record.CalcCountRecord;
import org.apache.poi.hssf.record.CalcModeRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.ColumnInfoRecord;
import org.apache.poi.hssf.record.DBCellRecord;
import org.apache.poi.hssf.record.DefaultColWidthRecord;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.hssf.record.DeltaRecord;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.DrawingRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.GridsetRecord;
import org.apache.poi.hssf.record.GutsRecord;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.IndexRecord;
import org.apache.poi.hssf.record.IterationRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.LeftMarginRecord;
import org.apache.poi.hssf.record.Margin;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.poi.hssf.record.PaneRecord;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.PrintGridlinesRecord;
import org.apache.poi.hssf.record.PrintHeadersRecord;
import org.apache.poi.hssf.record.PrintSetupRecord;
import org.apache.poi.hssf.record.ProtectRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RefModeRecord;
import org.apache.poi.hssf.record.RightMarginRecord;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.SaveRecalcRecord;
import org.apache.poi.hssf.record.SelectionRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.TopMarginRecord;
import org.apache.poi.hssf.record.VCenterRecord;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.hssf.record.WindowTwoRecord;
import org.apache.poi.hssf.record.aggregates.ColumnInfoRecordsAggregate;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.record.aggregates.RowRecordsAggregate;
import org.apache.poi.hssf.record.aggregates.ValueRecordsAggregate;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.util.IntList;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes3.dex */
public class Sheet implements Model {
    public static final short BottomMargin = 3;
    public static final short LeftMargin = 0;
    public static final byte PANE_LOWER_LEFT = 2;
    public static final byte PANE_LOWER_RIGHT = 0;
    public static final byte PANE_UPPER_LEFT = 3;
    public static final byte PANE_UPPER_RIGHT = 1;
    public static final short RightMargin = 1;
    public static final short TopMargin = 2;
    static /* synthetic */ Class class$org$apache$poi$hssf$model$Sheet;
    private static POILogger log;
    protected DimensionsRecord dims;
    protected ArrayList records = null;
    int preoffset = 0;
    int loc = 0;
    protected boolean containsLabels = false;
    protected int dimsloc = 0;
    protected DefaultColWidthRecord defaultcolwidth = null;
    protected DefaultRowHeightRecord defaultrowheight = null;
    protected GridsetRecord gridset = null;
    protected PrintSetupRecord printSetup = null;
    protected HeaderRecord header = null;
    protected FooterRecord footer = null;
    protected PrintGridlinesRecord printGridlines = null;
    protected WindowTwoRecord windowTwo = null;
    protected MergeCellsRecord merged = null;
    protected Margin[] margins = null;
    protected List mergedRecords = new ArrayList();
    protected int numMergedRegions = 0;
    protected SelectionRecord selection = null;
    protected ColumnInfoRecordsAggregate columns = null;
    protected ValueRecordsAggregate cells = null;
    protected RowRecordsAggregate rows = null;
    private Iterator valueRecIterator = null;
    private Iterator rowRecIterator = null;
    protected int eofLoc = 0;
    protected ProtectRecord protect = null;
    protected PageBreakRecord rowBreaks = null;
    protected PageBreakRecord colBreaks = null;
    protected PasswordRecord password = null;

    static {
        Class clsClass$ = class$org$apache$poi$hssf$model$Sheet;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.hssf.model.Sheet");
            class$org$apache$poi$hssf$model$Sheet = clsClass$;
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

    /* JADX WARN: Removed duplicated region for block: B:60:0x0169  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static org.apache.poi.hssf.model.Sheet createSheet(java.util.List r10, int r11, int r12) {
        /*
            Method dump skipped, instruction units count: 630
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.model.Sheet.createSheet(java.util.List, int, int):org.apache.poi.hssf.model.Sheet");
    }

    public Sheet cloneSheet() {
        ArrayList arrayList = new ArrayList(this.records.size());
        for (int i = 0; i < this.records.size(); i++) {
            Record record = (Record) ((Record) this.records.get(i)).clone();
            if (record instanceof RowRecordsAggregate) {
                Iterator iterator = ((RowRecordsAggregate) record).getIterator();
                while (iterator.hasNext()) {
                    arrayList.add((Record) iterator.next());
                }
            } else if (record instanceof ValueRecordsAggregate) {
                Iterator iterator2 = ((ValueRecordsAggregate) record).getIterator();
                while (iterator2.hasNext()) {
                    arrayList.add((Record) iterator2.next());
                }
            } else if (record instanceof FormulaRecordAggregate) {
                FormulaRecordAggregate formulaRecordAggregate = (FormulaRecordAggregate) record;
                FormulaRecord formulaRecord = formulaRecordAggregate.getFormulaRecord();
                if (formulaRecord != null) {
                    arrayList.add(formulaRecord);
                }
                StringRecord stringRecord = formulaRecordAggregate.getStringRecord();
                if (stringRecord != null) {
                    arrayList.add(stringRecord);
                }
            } else {
                arrayList.add(record);
            }
        }
        return createSheet(arrayList, 0, 0);
    }

    public static Sheet createSheet(List list, int i) {
        if (log.check(1)) {
            log.log(1, "Sheet createSheet (exisiting file) assumed offset 0");
        }
        return createSheet(list, i, 0);
    }

    public static Sheet createSheet() {
        if (log.check(1)) {
            log.log(1, "Sheet createsheet from scratch called");
        }
        Sheet sheet = new Sheet();
        ArrayList arrayList = new ArrayList(30);
        arrayList.add(sheet.createBOF());
        arrayList.add(sheet.createCalcMode());
        arrayList.add(sheet.createCalcCount());
        arrayList.add(sheet.createRefMode());
        arrayList.add(sheet.createIteration());
        arrayList.add(sheet.createDelta());
        arrayList.add(sheet.createSaveRecalc());
        arrayList.add(sheet.createPrintHeaders());
        PrintGridlinesRecord printGridlinesRecord = (PrintGridlinesRecord) sheet.createPrintGridlines();
        sheet.printGridlines = printGridlinesRecord;
        arrayList.add(printGridlinesRecord);
        GridsetRecord gridsetRecord = (GridsetRecord) sheet.createGridset();
        sheet.gridset = gridsetRecord;
        arrayList.add(gridsetRecord);
        arrayList.add(sheet.createGuts());
        DefaultRowHeightRecord defaultRowHeightRecord = (DefaultRowHeightRecord) sheet.createDefaultRowHeight();
        sheet.defaultrowheight = defaultRowHeightRecord;
        arrayList.add(defaultRowHeightRecord);
        arrayList.add(sheet.createWSBool());
        PageBreakRecord pageBreakRecord = new PageBreakRecord((short) 27);
        sheet.rowBreaks = pageBreakRecord;
        arrayList.add(pageBreakRecord);
        PageBreakRecord pageBreakRecord2 = new PageBreakRecord((short) 26);
        sheet.colBreaks = pageBreakRecord2;
        arrayList.add(pageBreakRecord2);
        HeaderRecord headerRecord = (HeaderRecord) sheet.createHeader();
        sheet.header = headerRecord;
        arrayList.add(headerRecord);
        FooterRecord footerRecord = (FooterRecord) sheet.createFooter();
        sheet.footer = footerRecord;
        arrayList.add(footerRecord);
        arrayList.add(sheet.createHCenter());
        arrayList.add(sheet.createVCenter());
        PrintSetupRecord printSetupRecord = (PrintSetupRecord) sheet.createPrintSetup();
        sheet.printSetup = printSetupRecord;
        arrayList.add(printSetupRecord);
        DefaultColWidthRecord defaultColWidthRecord = (DefaultColWidthRecord) sheet.createDefaultColWidth();
        sheet.defaultcolwidth = defaultColWidthRecord;
        arrayList.add(defaultColWidthRecord);
        ColumnInfoRecordsAggregate columnInfoRecordsAggregate = new ColumnInfoRecordsAggregate();
        arrayList.add(columnInfoRecordsAggregate);
        sheet.columns = columnInfoRecordsAggregate;
        DimensionsRecord dimensionsRecord = (DimensionsRecord) sheet.createDimensions();
        sheet.dims = dimensionsRecord;
        arrayList.add(dimensionsRecord);
        sheet.dimsloc = arrayList.size() - 1;
        WindowTwoRecord windowTwoRecordCreateWindowTwo = sheet.createWindowTwo();
        sheet.windowTwo = windowTwoRecordCreateWindowTwo;
        arrayList.add(windowTwoRecordCreateWindowTwo);
        sheet.setLoc(arrayList.size() - 1);
        SelectionRecord selectionRecord = (SelectionRecord) sheet.createSelection();
        sheet.selection = selectionRecord;
        arrayList.add(selectionRecord);
        ProtectRecord protectRecord = (ProtectRecord) sheet.createProtect();
        sheet.protect = protectRecord;
        arrayList.add(protectRecord);
        PasswordRecord passwordRecord = (PasswordRecord) sheet.createPassword();
        sheet.password = passwordRecord;
        arrayList.add(passwordRecord);
        arrayList.add(sheet.createEOF());
        sheet.records = arrayList;
        if (log.check(1)) {
            log.log(1, "Sheet createsheet from scratch exit");
        }
        return sheet;
    }

    protected Record createPassword() {
        if (log.check(1)) {
            log.log(1, "create password record");
        }
        return new PasswordRecord();
    }

    public PasswordRecord getPassword() {
        return this.password;
    }

    public void setPassword(String str) {
        if (str == null) {
            return;
        }
        this.password.setPassword(str);
    }

    private void checkCells() {
        if (this.cells == null) {
            this.cells = new ValueRecordsAggregate();
            this.records.add(getDimsLoc() + 1, this.cells);
        }
    }

    private void checkRows() {
        if (this.rows == null) {
            this.rows = new RowRecordsAggregate();
            this.records.add(getDimsLoc() + 1, this.rows);
        }
    }

    public int addMergedRegion(int i, short s, int i2, short s2) {
        MergeCellsRecord mergeCellsRecord = this.merged;
        if (mergeCellsRecord == null || mergeCellsRecord.getNumAreas() == 1027) {
            MergeCellsRecord mergeCellsRecord2 = (MergeCellsRecord) createMergedCells();
            this.merged = mergeCellsRecord2;
            this.mergedRecords.add(mergeCellsRecord2);
            this.records.add(r0.size() - 1, this.merged);
        }
        this.merged.addArea(i, s, i2, s2);
        int i3 = this.numMergedRegions;
        this.numMergedRegions = i3 + 1;
        return i3;
    }

    public void removeMergedRegion(int i) {
        int size;
        int numAreas;
        if (i >= this.numMergedRegions || this.mergedRecords.size() == 0) {
            return;
        }
        if (this.numMergedRegions - i < this.merged.getNumAreas()) {
            size = this.mergedRecords.size() - 1;
            numAreas = this.numMergedRegions - this.merged.getNumAreas();
        } else {
            size = 0;
            numAreas = 0;
            int i2 = 0;
            while (true) {
                if (i2 >= this.mergedRecords.size()) {
                    break;
                }
                MergeCellsRecord mergeCellsRecord = (MergeCellsRecord) this.mergedRecords.get(i2);
                if (mergeCellsRecord.getNumAreas() + numAreas > i) {
                    size = i2;
                    break;
                } else {
                    numAreas += mergeCellsRecord.getNumAreas();
                    i2++;
                }
            }
        }
        MergeCellsRecord mergeCellsRecord2 = (MergeCellsRecord) this.mergedRecords.get(size);
        mergeCellsRecord2.removeAreaAt(i - numAreas);
        this.numMergedRegions--;
        if (mergeCellsRecord2.getNumAreas() == 0) {
            this.mergedRecords.remove(size);
            this.records.remove(this.merged);
            if (this.merged == mergeCellsRecord2) {
                if (this.mergedRecords.size() > 0) {
                    this.merged = (MergeCellsRecord) this.mergedRecords.get(r6.size() - 1);
                } else {
                    this.merged = null;
                }
            }
        }
    }

    public MergeCellsRecord.MergedRegion getMergedRegionAt(int i) {
        int size;
        int numAreas;
        if (i >= this.numMergedRegions || this.mergedRecords.size() == 0) {
            return null;
        }
        if (this.numMergedRegions - i < this.merged.getNumAreas()) {
            size = this.mergedRecords.size() - 1;
            numAreas = this.numMergedRegions - this.merged.getNumAreas();
        } else {
            size = 0;
            numAreas = 0;
            int i2 = 0;
            while (true) {
                if (i2 >= this.mergedRecords.size()) {
                    break;
                }
                MergeCellsRecord mergeCellsRecord = (MergeCellsRecord) this.mergedRecords.get(i2);
                if (mergeCellsRecord.getNumAreas() + numAreas > i) {
                    size = i2;
                    break;
                }
                numAreas += mergeCellsRecord.getNumAreas();
                i2++;
            }
        }
        return ((MergeCellsRecord) this.mergedRecords.get(size)).getAreaAt(i - numAreas);
    }

    public int getNumMergedRegions() {
        return this.numMergedRegions;
    }

    public void convertLabelRecords(Workbook workbook) {
        if (log.check(1)) {
            log.log(1, "convertLabelRecords called");
        }
        if (this.containsLabels) {
            for (int i = 0; i < this.records.size(); i++) {
                Record record = (Record) this.records.get(i);
                if (record.getSid() == 516) {
                    LabelRecord labelRecord = (LabelRecord) record;
                    this.records.remove(i);
                    LabelSSTRecord labelSSTRecord = new LabelSSTRecord();
                    int iAddSSTString = workbook.addSSTString(labelRecord.getValue());
                    labelSSTRecord.setRow(labelRecord.getRow());
                    labelSSTRecord.setColumn(labelRecord.getColumn());
                    labelSSTRecord.setXFIndex(labelRecord.getXFIndex());
                    labelSSTRecord.setSSTIndex(iAddSSTString);
                    this.records.add(i, labelSSTRecord);
                }
            }
        }
        if (log.check(1)) {
            log.log(1, "convertLabelRecords exit");
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public int getNumRecords() {
        checkCells();
        checkRows();
        if (log.check(1)) {
            log.log(1, "Sheet.getNumRecords");
            log.logFormatted(1, "returning % + % + % - 2 = %", new int[]{this.records.size(), this.cells.getPhysicalNumberOfCells(), this.rows.getPhysicalNumberOfRows(), ((this.records.size() + this.cells.getPhysicalNumberOfCells()) + this.rows.getPhysicalNumberOfRows()) - 2});
        }
        return ((this.records.size() + this.cells.getPhysicalNumberOfCells()) + this.rows.getPhysicalNumberOfRows()) - 2;
    }

    public void setDimensions(int i, short s, int i2, short s2) {
        if (log.check(1)) {
            log.log(1, "Sheet.setDimensions");
            log.log(1, new StringBuffer("firstrow").append(i).append("firstcol").append((int) s).append("lastrow").append(i2).append("lastcol").append((int) s2).toString());
        }
        this.dims.setFirstCol(s);
        this.dims.setFirstRow(i);
        this.dims.setLastCol(s2);
        this.dims.setLastRow(i2);
        if (log.check(1)) {
            log.log(1, "Sheet.setDimensions exiting");
        }
    }

    public void setLoc(int i) {
        this.valueRecIterator = null;
        if (log.check(1)) {
            log.log(1, new StringBuffer().append("sheet.setLoc(): ").append(i).toString());
        }
        this.loc = i;
    }

    public int getLoc() {
        if (log.check(1)) {
            log.log(1, new StringBuffer().append("sheet.getLoc():").append(this.loc).toString());
        }
        return this.loc;
    }

    public void setPreOffset(int i) {
        this.preoffset = i;
    }

    public int getPreOffset() {
        return this.preoffset;
    }

    public byte[] serialize() {
        if (log.check(1)) {
            log.log(1, "Sheet.serialize");
        }
        byte[] bArr = new byte[getSize()];
        int iSerialize = 0;
        for (int i = 0; i < this.records.size(); i++) {
            iSerialize += ((Record) this.records.get(i)).serialize(iSerialize, bArr);
        }
        if (log.check(1)) {
            log.log(1, new StringBuffer().append("Sheet.serialize returning ").append(bArr).toString());
        }
        return bArr;
    }

    public int serialize(int i, byte[] bArr) {
        if (log.check(1)) {
            log.log(1, "Sheet.serialize using offsets");
        }
        int iSerialize = 0;
        for (int i2 = 0; i2 < this.records.size(); i2++) {
            iSerialize += ((Record) this.records.get(i2)).serialize(iSerialize + i, bArr);
        }
        if (log.check(1)) {
            log.log(1, "Sheet.serialize returning ");
        }
        return iSerialize;
    }

    public RowRecord createRow(int i) {
        return RowRecordsAggregate.createRow(i);
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public LabelSSTRecord createLabelSST(int i, short s, int i2) {
        log.logFormatted(1, "create labelsst row,col,index %,%,%", new int[]{i, s, i2});
        LabelSSTRecord labelSSTRecord = new LabelSSTRecord();
        labelSSTRecord.setRow(i);
        labelSSTRecord.setColumn(s);
        labelSSTRecord.setSSTIndex(i2);
        labelSSTRecord.setXFIndex((short) 15);
        return labelSSTRecord;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public NumberRecord createNumber(int i, short s, double d) {
        log.logFormatted(1, "create number row,col,value %,%,%", new double[]{i, s, d});
        NumberRecord numberRecord = new NumberRecord();
        numberRecord.setRow(i);
        numberRecord.setColumn(s);
        numberRecord.setValue(d);
        numberRecord.setXFIndex((short) 15);
        return numberRecord;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public BlankRecord createBlank(int i, short s) {
        log.logFormatted(1, "create blank row,col %,%", new int[]{i, s});
        BlankRecord blankRecord = new BlankRecord();
        blankRecord.setRow(i);
        blankRecord.setColumn(s);
        blankRecord.setXFIndex((short) 15);
        return blankRecord;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public FormulaRecord createFormula(int i, short s, String str) {
        log.logFormatted(1, "create formula row,col,formula %,%,%", new int[]{i, s}, str);
        FormulaRecord formulaRecord = new FormulaRecord();
        formulaRecord.setRow(i);
        formulaRecord.setColumn(s);
        formulaRecord.setOptions((short) 2);
        formulaRecord.setValue(0.0d);
        formulaRecord.setXFIndex((short) 15);
        FormulaParser formulaParser = new FormulaParser(str, null);
        formulaParser.parse();
        Ptg[] rPNPtg = formulaParser.getRPNPtg();
        int size = 0;
        for (int i2 = 0; i2 < rPNPtg.length; i2++) {
            size += rPNPtg[i2].getSize();
            formulaRecord.pushExpressionToken(rPNPtg[i2]);
        }
        formulaRecord.setExpressionLength((short) size);
        return formulaRecord;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void addValueRecord(int i, CellValueRecordInterface cellValueRecordInterface) {
        checkCells();
        log.logFormatted(1, "add value record  row,loc %,%", new int[]{i, this.loc});
        DimensionsRecord dimensionsRecord = (DimensionsRecord) this.records.get(getDimsLoc());
        if (cellValueRecordInterface.getColumn() > dimensionsRecord.getLastCol()) {
            dimensionsRecord.setLastCol((short) (cellValueRecordInterface.getColumn() + 1));
        }
        if (cellValueRecordInterface.getColumn() < dimensionsRecord.getFirstCol()) {
            dimensionsRecord.setFirstCol(cellValueRecordInterface.getColumn());
        }
        this.cells.insertCell(cellValueRecordInterface);
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void removeValueRecord(int i, CellValueRecordInterface cellValueRecordInterface) {
        checkCells();
        log.logFormatted(1, "remove value record row,dimsloc %,%", new int[]{i, this.dimsloc});
        this.loc = this.dimsloc;
        this.cells.removeCell(cellValueRecordInterface);
    }

    public void replaceValueRecord(CellValueRecordInterface cellValueRecordInterface) {
        checkCells();
        setLoc(this.dimsloc);
        if (log.check(1)) {
            log.log(1, "replaceValueRecord ");
        }
        this.cells.insertCell(cellValueRecordInterface);
    }

    public void addRow(RowRecord rowRecord) {
        checkRows();
        if (log.check(1)) {
            log.log(1, "addRow ");
        }
        DimensionsRecord dimensionsRecord = (DimensionsRecord) this.records.get(getDimsLoc());
        if (rowRecord.getRowNumber() >= dimensionsRecord.getLastRow()) {
            dimensionsRecord.setLastRow(rowRecord.getRowNumber() + 1);
        }
        if (rowRecord.getRowNumber() < dimensionsRecord.getFirstRow()) {
            dimensionsRecord.setFirstRow(rowRecord.getRowNumber());
        }
        RowRecord row = this.rows.getRow(rowRecord.getRowNumber());
        if (row != null) {
            this.rows.removeRow(row);
        }
        this.rows.insertRow(rowRecord);
        if (log.check(1)) {
            log.log(1, "exit addRow");
        }
    }

    public void removeRow(RowRecord rowRecord) {
        checkRows();
        setLoc(getDimsLoc());
        this.rows.removeRow(rowRecord);
    }

    public CellValueRecordInterface getNextValueRecord() {
        if (log.check(1)) {
            log.log(1, new StringBuffer().append("getNextValue loc= ").append(this.loc).toString());
        }
        if (this.valueRecIterator == null) {
            this.valueRecIterator = this.cells.getIterator();
        }
        if (this.valueRecIterator.hasNext()) {
            return (CellValueRecordInterface) this.valueRecIterator.next();
        }
        return null;
    }

    public RowRecord getNextRow() {
        if (log.check(1)) {
            log.log(1, new StringBuffer().append("getNextRow loc= ").append(this.loc).toString());
        }
        if (this.rowRecIterator == null) {
            this.rowRecIterator = this.rows.getIterator();
        }
        if (this.rowRecIterator.hasNext()) {
            return (RowRecord) this.rowRecIterator.next();
        }
        return null;
    }

    public RowRecord getRow(int i) {
        if (log.check(1)) {
            log.log(1, new StringBuffer().append("getNextRow loc= ").append(this.loc).toString());
        }
        return this.rows.getRow(i);
    }

    public void addDBCellRecords() {
        IntList intList = new IntList();
        int i = 0;
        IndexRecord indexRecord = null;
        int i2 = 0;
        int length = 0;
        while (i2 < this.records.size()) {
            Record record = (Record) this.records.get(i2);
            if (record.getSid() == 523) {
                indexRecord = (IndexRecord) record;
            }
            if (record.getSid() == 520) {
                break;
            }
            length += record.serialize().length;
            i2++;
        }
        while (i2 < this.records.size()) {
            Record record2 = (Record) this.records.get(i2);
            if (record2.getSid() == 520) {
                i++;
                intList.add(length);
                if (i % 32 == 0) {
                    for (int i3 = i2; i3 < this.records.size(); i3++) {
                        record2 = (Record) this.records.get(i3);
                        if (!record2.isInValueSection() || record2.getSid() == 520) {
                            this.records.add(i3, createDBCell(length, intList, indexRecord));
                            i2 = i3;
                            break;
                        }
                    }
                }
            }
            if (!record2.isInValueSection()) {
                this.records.add(i2, createDBCell(length, intList, indexRecord));
                return;
            } else {
                length += record2.serialize().length;
                i2++;
            }
        }
    }

    private DBCellRecord createDBCell(int i, IntList intList, IndexRecord indexRecord) {
        DBCellRecord dBCellRecord = new DBCellRecord();
        dBCellRecord.setRowOffset(i - intList.get(0));
        dBCellRecord.addCellOffset((short) 0);
        addDbCellToIndex(i, indexRecord);
        return dBCellRecord;
    }

    private void addDbCellToIndex(int i, IndexRecord indexRecord) {
        int numDbcells = indexRecord.getNumDbcells() + 1;
        indexRecord.addDbcell(i + this.preoffset);
        for (int i2 = 0; i2 < numDbcells; i2++) {
            indexRecord.setDbcell(i2, indexRecord.getDbcellAt(i2) + 4);
        }
    }

    protected Record createBOF() {
        BOFRecord bOFRecord = new BOFRecord();
        bOFRecord.setVersion((short) 1536);
        bOFRecord.setType((short) 16);
        bOFRecord.setBuild((short) 3515);
        bOFRecord.setBuildYear(BOFRecord.BUILD_YEAR);
        bOFRecord.setHistoryBitMask(193);
        bOFRecord.setRequiredVersion(6);
        return bOFRecord;
    }

    protected Record createIndex() {
        IndexRecord indexRecord = new IndexRecord();
        indexRecord.setFirstRow(0);
        indexRecord.setLastRowAdd1(0);
        return indexRecord;
    }

    protected Record createCalcMode() {
        CalcModeRecord calcModeRecord = new CalcModeRecord();
        calcModeRecord.setCalcMode((short) 1);
        return calcModeRecord;
    }

    protected Record createCalcCount() {
        CalcCountRecord calcCountRecord = new CalcCountRecord();
        calcCountRecord.setIterations((short) 100);
        return calcCountRecord;
    }

    protected Record createRefMode() {
        RefModeRecord refModeRecord = new RefModeRecord();
        refModeRecord.setMode((short) 1);
        return refModeRecord;
    }

    protected Record createIteration() {
        IterationRecord iterationRecord = new IterationRecord();
        iterationRecord.setIteration(false);
        return iterationRecord;
    }

    protected Record createDelta() {
        DeltaRecord deltaRecord = new DeltaRecord();
        deltaRecord.setMaxChange(0.001d);
        return deltaRecord;
    }

    protected Record createSaveRecalc() {
        SaveRecalcRecord saveRecalcRecord = new SaveRecalcRecord();
        saveRecalcRecord.setRecalc(true);
        return saveRecalcRecord;
    }

    protected Record createPrintHeaders() {
        PrintHeadersRecord printHeadersRecord = new PrintHeadersRecord();
        printHeadersRecord.setPrintHeaders(false);
        return printHeadersRecord;
    }

    protected Record createPrintGridlines() {
        PrintGridlinesRecord printGridlinesRecord = new PrintGridlinesRecord();
        printGridlinesRecord.setPrintGridlines(false);
        return printGridlinesRecord;
    }

    protected Record createGridset() {
        GridsetRecord gridsetRecord = new GridsetRecord();
        gridsetRecord.setGridset(true);
        return gridsetRecord;
    }

    protected Record createGuts() {
        GutsRecord gutsRecord = new GutsRecord();
        gutsRecord.setLeftRowGutter((short) 0);
        gutsRecord.setTopColGutter((short) 0);
        gutsRecord.setRowLevelMax((short) 0);
        gutsRecord.setColLevelMax((short) 0);
        return gutsRecord;
    }

    protected Record createDefaultRowHeight() {
        DefaultRowHeightRecord defaultRowHeightRecord = new DefaultRowHeightRecord();
        defaultRowHeightRecord.setOptionFlags((short) 0);
        defaultRowHeightRecord.setRowHeight((short) 255);
        return defaultRowHeightRecord;
    }

    protected Record createWSBool() {
        WSBoolRecord wSBoolRecord = new WSBoolRecord();
        wSBoolRecord.setWSBool1((byte) 4);
        wSBoolRecord.setWSBool2((byte) -63);
        return wSBoolRecord;
    }

    protected Record createHeader() {
        HeaderRecord headerRecord = new HeaderRecord();
        headerRecord.setHeaderLength((byte) 0);
        headerRecord.setHeader(null);
        return headerRecord;
    }

    protected Record createFooter() {
        FooterRecord footerRecord = new FooterRecord();
        footerRecord.setFooterLength((byte) 0);
        footerRecord.setFooter(null);
        return footerRecord;
    }

    protected Record createHCenter() {
        HCenterRecord hCenterRecord = new HCenterRecord();
        hCenterRecord.setHCenter(false);
        return hCenterRecord;
    }

    protected Record createVCenter() {
        VCenterRecord vCenterRecord = new VCenterRecord();
        vCenterRecord.setVCenter(false);
        return vCenterRecord;
    }

    protected Record createPrintSetup() {
        PrintSetupRecord printSetupRecord = new PrintSetupRecord();
        printSetupRecord.setPaperSize((short) 1);
        printSetupRecord.setScale((short) 100);
        printSetupRecord.setPageStart((short) 1);
        printSetupRecord.setFitWidth((short) 1);
        printSetupRecord.setFitHeight((short) 1);
        printSetupRecord.setOptions((short) 2);
        printSetupRecord.setHResolution((short) 300);
        printSetupRecord.setVResolution((short) 300);
        printSetupRecord.setHeaderMargin(0.5d);
        printSetupRecord.setFooterMargin(0.5d);
        printSetupRecord.setCopies((short) 0);
        return printSetupRecord;
    }

    protected Record createDefaultColWidth() {
        DefaultColWidthRecord defaultColWidthRecord = new DefaultColWidthRecord();
        defaultColWidthRecord.setColWidth((short) 8);
        return defaultColWidthRecord;
    }

    protected Record createColInfo() {
        return ColumnInfoRecordsAggregate.createColInfo();
    }

    public short getDefaultColumnWidth() {
        return this.defaultcolwidth.getColWidth();
    }

    public boolean isGridsPrinted() {
        return !this.gridset.getGridset();
    }

    public void setGridsPrinted(boolean z) {
        this.gridset.setGridset(!z);
    }

    public void setDefaultColumnWidth(short s) {
        this.defaultcolwidth.setColWidth(s);
    }

    public void setDefaultRowHeight(short s) {
        this.defaultrowheight.setRowHeight(s);
    }

    public short getDefaultRowHeight() {
        return this.defaultrowheight.getRowHeight();
    }

    public short getColumnWidth(short s) {
        ColumnInfoRecordsAggregate columnInfoRecordsAggregate = this.columns;
        ColumnInfoRecord columnInfoRecord = null;
        if (columnInfoRecordsAggregate != null) {
            Iterator iterator = columnInfoRecordsAggregate.getIterator();
            while (true) {
                if (!iterator.hasNext()) {
                    break;
                }
                ColumnInfoRecord columnInfoRecord2 = (ColumnInfoRecord) iterator.next();
                if (columnInfoRecord2.getFirstColumn() <= s && s <= columnInfoRecord2.getLastColumn()) {
                    columnInfoRecord = columnInfoRecord2;
                    break;
                }
            }
        }
        if (columnInfoRecord != null) {
            return columnInfoRecord.getColumnWidth();
        }
        return this.defaultcolwidth.getColWidth();
    }

    public void setColumnWidth(short s, short s2) {
        setColumn(s, new Short(s2), null, null, null);
    }

    public void setColumn(short s, Short sh, Integer num, Boolean bool, Boolean bool2) {
        if (this.columns == null) {
            this.columns = new ColumnInfoRecordsAggregate();
        }
        this.columns.setColumn(s, sh, num, bool, bool2);
    }

    public void groupColumnRange(short s, short s2, boolean z) {
        this.columns.groupColumnRange(s, s2, z);
        Iterator iterator = this.columns.getIterator();
        int iMax = 0;
        while (iterator.hasNext()) {
            iMax = Math.max((int) ((ColumnInfoRecord) iterator.next()).getOutlineLevel(), iMax);
        }
        GutsRecord gutsRecord = (GutsRecord) findFirstRecordBySid((short) 128);
        gutsRecord.setColLevelMax((short) (iMax + 1));
        if (iMax == 0) {
            gutsRecord.setTopColGutter((short) 0);
        } else {
            gutsRecord.setTopColGutter((short) (((iMax - 1) * 12) + 29));
        }
    }

    protected Record createDimensions() {
        DimensionsRecord dimensionsRecord = new DimensionsRecord();
        dimensionsRecord.setFirstCol((short) 0);
        dimensionsRecord.setLastRow(1);
        dimensionsRecord.setFirstRow(0);
        dimensionsRecord.setLastCol((short) 1);
        return dimensionsRecord;
    }

    protected WindowTwoRecord createWindowTwo() {
        WindowTwoRecord windowTwoRecord = new WindowTwoRecord();
        windowTwoRecord.setOptions((short) 1718);
        windowTwoRecord.setTopRow((short) 0);
        windowTwoRecord.setLeftCol((short) 0);
        windowTwoRecord.setHeaderColor(64);
        windowTwoRecord.setPageBreakZoom((short) 0);
        windowTwoRecord.setNormalZoom((short) 0);
        return windowTwoRecord;
    }

    protected Record createSelection() {
        SelectionRecord selectionRecord = new SelectionRecord();
        selectionRecord.setPane((byte) 3);
        selectionRecord.setActiveCellCol((short) 0);
        selectionRecord.setActiveCellRow(0);
        selectionRecord.setNumRefs((short) 0);
        return selectionRecord;
    }

    public int getActiveCellRow() {
        SelectionRecord selectionRecord = this.selection;
        if (selectionRecord == null) {
            return 0;
        }
        return selectionRecord.getActiveCellRow();
    }

    public void setActiveCellRow(int i) {
        SelectionRecord selectionRecord = this.selection;
        if (selectionRecord != null) {
            selectionRecord.setActiveCellRow(i);
        }
    }

    public short getActiveCellCol() {
        SelectionRecord selectionRecord = this.selection;
        if (selectionRecord == null) {
            return (short) 0;
        }
        return selectionRecord.getActiveCellCol();
    }

    public void setActiveCellCol(short s) {
        SelectionRecord selectionRecord = this.selection;
        if (selectionRecord != null) {
            selectionRecord.setActiveCellCol(s);
        }
    }

    protected Record createMergedCells() {
        MergeCellsRecord mergeCellsRecord = new MergeCellsRecord();
        mergeCellsRecord.setNumAreas((short) 0);
        return mergeCellsRecord;
    }

    protected Record createEOF() {
        return new EOFRecord();
    }

    public int getDimsLoc() {
        if (log.check(1)) {
            log.log(1, new StringBuffer().append("getDimsLoc dimsloc= ").append(this.dimsloc).toString());
        }
        return this.dimsloc;
    }

    public void checkDimsLoc(Record record, int i) {
        if (record.getSid() == 512) {
            this.loc = i;
            this.dimsloc = i;
        }
    }

    public int getSize() {
        int recordSize = 0;
        for (int i = 0; i < this.records.size(); i++) {
            recordSize += ((Record) this.records.get(i)).getRecordSize();
        }
        return recordSize;
    }

    public List getRecords() {
        return this.records;
    }

    public GridsetRecord getGridsetRecord() {
        return this.gridset;
    }

    public Record findFirstRecordBySid(short s) {
        for (Record record : this.records) {
            if (record.getSid() == s) {
                return record;
            }
        }
        return null;
    }

    public void setSCLRecord(SCLRecord sCLRecord) {
        int iFindFirstRecordLocBySid = findFirstRecordLocBySid((short) 160);
        if (iFindFirstRecordLocBySid == -1) {
            this.records.add(findFirstRecordLocBySid((short) 574) + 1, sCLRecord);
        } else {
            this.records.set(iFindFirstRecordLocBySid, sCLRecord);
        }
    }

    public int findFirstRecordLocBySid(short s) {
        Iterator it = this.records.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (((Record) it.next()).getSid() == s) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public HeaderRecord getHeader() {
        return this.header;
    }

    public void setHeader(HeaderRecord headerRecord) {
        this.header = headerRecord;
    }

    public FooterRecord getFooter() {
        return this.footer;
    }

    public void setFooter(FooterRecord footerRecord) {
        this.footer = footerRecord;
    }

    public PrintSetupRecord getPrintSetup() {
        return this.printSetup;
    }

    public void setPrintSetup(PrintSetupRecord printSetupRecord) {
        this.printSetup = printSetupRecord;
    }

    public PrintGridlinesRecord getPrintGridlines() {
        return this.printGridlines;
    }

    public void setPrintGridlines(PrintGridlinesRecord printGridlinesRecord) {
        this.printGridlines = printGridlinesRecord;
    }

    public void setSelected(boolean z) {
        this.windowTwo.setSelected(z);
    }

    public double getMargin(short s) {
        if (getMargins()[s] != null) {
            return this.margins[s].getMargin();
        }
        if (s == 0 || s == 1) {
            return 0.75d;
        }
        if (s == 2 || s == 3) {
            return 1.0d;
        }
        throw new RuntimeException(new StringBuffer().append("Unknown margin constant:  ").append((int) s).toString());
    }

    public void setMargin(short s, double d) {
        Margin leftMarginRecord;
        Margin margin = getMargins()[s];
        if (margin == null) {
            if (s == 0) {
                leftMarginRecord = new LeftMarginRecord();
                this.records.add(getDimsLoc() + 1, leftMarginRecord);
            } else if (s == 1) {
                leftMarginRecord = new RightMarginRecord();
                this.records.add(getDimsLoc() + 1, leftMarginRecord);
            } else if (s == 2) {
                leftMarginRecord = new TopMarginRecord();
                this.records.add(getDimsLoc() + 1, leftMarginRecord);
            } else if (s == 3) {
                leftMarginRecord = new BottomMarginRecord();
                this.records.add(getDimsLoc() + 1, leftMarginRecord);
            } else {
                throw new RuntimeException(new StringBuffer().append("Unknown margin constant:  ").append((int) s).toString());
            }
            margin = leftMarginRecord;
            this.margins[s] = margin;
        }
        margin.setMargin(d);
    }

    public int getEofLoc() {
        return this.eofLoc;
    }

    public void createFreezePane(int i, int i2, int i3, int i4) {
        int iFindFirstRecordLocBySid = findFirstRecordLocBySid((short) 574);
        PaneRecord paneRecord = new PaneRecord();
        paneRecord.setX((short) i);
        paneRecord.setY((short) i2);
        paneRecord.setTopRow((short) i3);
        paneRecord.setLeftColumn((short) i4);
        if (i2 == 0) {
            paneRecord.setTopRow((short) 0);
            paneRecord.setActivePane((short) 1);
        } else if (i == 0) {
            paneRecord.setLeftColumn((short) 64);
            paneRecord.setActivePane((short) 2);
        } else {
            paneRecord.setActivePane((short) 0);
        }
        this.records.add(iFindFirstRecordLocBySid + 1, paneRecord);
        this.windowTwo.setFreezePanes(true);
        this.windowTwo.setFreezePanesNoSplit(true);
        ((SelectionRecord) findFirstRecordBySid((short) 29)).setPane((byte) paneRecord.getActivePane());
    }

    public void createSplitPane(int i, int i2, int i3, int i4, int i5) {
        int iFindFirstRecordLocBySid = findFirstRecordLocBySid((short) 574);
        PaneRecord paneRecord = new PaneRecord();
        paneRecord.setX((short) i);
        paneRecord.setY((short) i2);
        paneRecord.setTopRow((short) i3);
        paneRecord.setLeftColumn((short) i4);
        paneRecord.setActivePane((short) i5);
        this.records.add(iFindFirstRecordLocBySid + 1, paneRecord);
        this.windowTwo.setFreezePanes(false);
        this.windowTwo.setFreezePanesNoSplit(false);
        ((SelectionRecord) findFirstRecordBySid((short) 29)).setPane((byte) 0);
    }

    public SelectionRecord getSelection() {
        return this.selection;
    }

    public void setSelection(SelectionRecord selectionRecord) {
        this.selection = selectionRecord;
    }

    protected Record createProtect() {
        if (log.check(1)) {
            log.log(1, "create protect record with protection disabled");
        }
        ProtectRecord protectRecord = new ProtectRecord();
        protectRecord.setProtect(false);
        return protectRecord;
    }

    public ProtectRecord getProtect() {
        return this.protect;
    }

    public void setDisplayGridlines(boolean z) {
        this.windowTwo.setDisplayGridlines(z);
    }

    public boolean isDisplayGridlines() {
        return this.windowTwo.getDisplayGridlines();
    }

    public void setDisplayFormulas(boolean z) {
        this.windowTwo.setDisplayFormulas(z);
    }

    public boolean isDisplayFormulas() {
        return this.windowTwo.getDisplayFormulas();
    }

    public void setDisplayRowColHeadings(boolean z) {
        this.windowTwo.setDisplayRowColHeadings(z);
    }

    public boolean isDisplayRowColHeadings() {
        return this.windowTwo.getDisplayRowColHeadings();
    }

    protected Margin[] getMargins() {
        if (this.margins == null) {
            this.margins = new Margin[4];
        }
        return this.margins;
    }

    public int aggregateDrawingRecords(DrawingManager drawingManager) {
        int iFindFirstRecordLocBySid = findFirstRecordLocBySid((short) 236);
        if (iFindFirstRecordLocBySid == -1) {
            EscherAggregate escherAggregate = new EscherAggregate(drawingManager);
            int iFindFirstRecordLocBySid2 = findFirstRecordLocBySid(EscherAggregate.sid);
            if (iFindFirstRecordLocBySid2 == -1) {
                iFindFirstRecordLocBySid2 = findFirstRecordLocBySid((short) 574);
            } else {
                getRecords().remove(iFindFirstRecordLocBySid2);
            }
            getRecords().add(iFindFirstRecordLocBySid2, escherAggregate);
            return iFindFirstRecordLocBySid2;
        }
        List records = getRecords();
        EscherAggregate escherAggregateCreateAggregate = EscherAggregate.createAggregate(records, iFindFirstRecordLocBySid, drawingManager);
        int i = iFindFirstRecordLocBySid;
        while (true) {
            int i2 = i + 1;
            if (i2 >= records.size() || !(records.get(i) instanceof DrawingRecord) || !(records.get(i2) instanceof ObjRecord)) {
                break;
            }
            i += 2;
        }
        int i3 = i - 1;
        for (int i4 = 0; i4 < (i3 - iFindFirstRecordLocBySid) + 1; i4++) {
            records.remove(iFindFirstRecordLocBySid);
        }
        records.add(iFindFirstRecordLocBySid, escherAggregateCreateAggregate);
        return iFindFirstRecordLocBySid;
    }

    public void preSerialize() {
        for (Record record : getRecords()) {
            if (record instanceof EscherAggregate) {
                record.getRecordSize();
            }
        }
    }

    public void shiftBreaks(PageBreakRecord pageBreakRecord, short s, short s2, int i) {
        if (this.rowBreaks == null) {
            return;
        }
        Iterator breaksIterator = pageBreakRecord.getBreaksIterator();
        ArrayList<PageBreakRecord.Break> arrayList = new ArrayList();
        while (breaksIterator.hasNext()) {
            PageBreakRecord.Break r2 = (PageBreakRecord.Break) breaksIterator.next();
            short s3 = r2.main;
            boolean z = s3 >= s;
            boolean z2 = s3 <= s2;
            if (z && z2) {
                arrayList.add(r2);
            }
        }
        for (PageBreakRecord.Break r9 : arrayList) {
            pageBreakRecord.removeBreak(r9.main);
            pageBreakRecord.addBreak((short) (r9.main + i), r9.subFrom, r9.subTo);
        }
    }

    public void setRowBreak(int i, short s, short s2) {
        this.rowBreaks.addBreak((short) i, s, s2);
    }

    public void removeRowBreak(int i) {
        this.rowBreaks.removeBreak((short) i);
    }

    public boolean isRowBroken(int i) {
        return this.rowBreaks.getBreak((short) i) != null;
    }

    public void setColumnBreak(short s, short s2, short s3) {
        this.colBreaks.addBreak(s, s2, s3);
    }

    public void removeColumnBreak(short s) {
        this.colBreaks.removeBreak(s);
    }

    public boolean isColumnBroken(short s) {
        return this.colBreaks.getBreak(s) != null;
    }

    public void shiftRowBreaks(int i, int i2, int i3) {
        shiftBreaks(this.rowBreaks, (short) i, (short) i2, (short) i3);
    }

    public void shiftColumnBreaks(short s, short s2, short s3) {
        shiftBreaks(this.colBreaks, s, s2, s3);
    }

    public Iterator getRowBreaks() {
        return this.rowBreaks.getBreaksIterator();
    }

    public int getNumRowBreaks() {
        return this.rowBreaks.getNumBreaks();
    }

    public Iterator getColumnBreaks() {
        return this.colBreaks.getBreaksIterator();
    }

    public int getNumColumnBreaks() {
        return this.colBreaks.getNumBreaks();
    }

    public void setColumnGroupCollapsed(short s, boolean z) {
        if (z) {
            this.columns.collapseColumn(s);
        } else {
            this.columns.expandColumn(s);
        }
    }

    public void groupRowRange(int i, int i2, boolean z) {
        checkRows();
        while (i <= i2) {
            RowRecord row = getRow(i);
            if (row == null) {
                row = createRow(i);
                addRow(row);
            }
            short outlineLevel = row.getOutlineLevel();
            row.setOutlineLevel((short) Math.min(7, Math.max(0, z ? outlineLevel + 1 : outlineLevel - 1)));
            i++;
        }
        recalcRowGutter();
    }

    private void recalcRowGutter() {
        Iterator iterator = this.rows.getIterator();
        int iMax = 0;
        while (iterator.hasNext()) {
            iMax = Math.max((int) ((RowRecord) iterator.next()).getOutlineLevel(), iMax);
        }
        GutsRecord gutsRecord = (GutsRecord) findFirstRecordBySid((short) 128);
        gutsRecord.setRowLevelMax((short) (iMax + 1));
        gutsRecord.setLeftRowGutter((short) ((iMax * 12) + 29));
    }

    public void setRowGroupCollapsed(int i, boolean z) {
        if (z) {
            this.rows.collapseRow(i);
        } else {
            this.rows.expandRow(i);
        }
    }
}
