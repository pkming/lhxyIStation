package org.apache.poi.hssf.usermodel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.apache.poi.hssf.eventmodel.EventRecordFactory;
import org.apache.poi.hssf.model.Sheet;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.FilePassRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.record.WindowTwoRecord;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;
import org.apache.poi.hssf.record.formula.IntPtg;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.UnaryPlusPtg;
import org.apache.poi.hssf.record.formula.UnionPtg;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.PwdUtils;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFWorkbook {
    private static final int DEBUG = 1;
    public static final byte ENCODING_COMPRESSED_UNICODE = 0;
    public static final byte ENCODING_UTF_16 = 1;
    public static final int INITIAL_CAPACITY = 3;
    static /* synthetic */ Class class$org$apache$poi$hssf$usermodel$HSSFWorkbook;
    private static POILogger log;
    private HSSFDataFormat formatter;
    private ArrayList names;
    private POIFSFileSystem poifs;
    private boolean preserveNodes;
    private ArrayList sheets;
    private Workbook workbook;

    static {
        Class clsClass$ = class$org$apache$poi$hssf$usermodel$HSSFWorkbook;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.hssf.usermodel.HSSFWorkbook");
            class$org$apache$poi$hssf$usermodel$HSSFWorkbook = clsClass$;
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

    public HSSFWorkbook() {
        this.workbook = Workbook.createWorkbook();
        this.sheets = new ArrayList(3);
        this.names = new ArrayList(3);
    }

    public HSSFWorkbook(POIFSFileSystem pOIFSFileSystem) throws IOException {
        this(pOIFSFileSystem, true);
    }

    public HSSFWorkbook(POIFSFileSystem pOIFSFileSystem, boolean z) throws IOException {
        this.preserveNodes = z;
        if (z) {
            this.poifs = pOIFSFileSystem;
        }
        this.sheets = new ArrayList(3);
        this.names = new ArrayList(3);
        DocumentInputStream documentInputStreamCreateDocumentInputStream = pOIFSFileSystem.createDocumentInputStream("Workbook");
        new EventRecordFactory();
        List listCreateRecords = RecordFactory.createRecords(documentInputStreamCreateDocumentInputStream);
        Workbook workbookCreateWorkbook = Workbook.createWorkbook(listCreateRecords);
        this.workbook = workbookCreateWorkbook;
        setPropertiesFromWorkbook(workbookCreateWorkbook);
        int numRecords = this.workbook.getNumRecords();
        int i = 0;
        while (numRecords < listCreateRecords.size()) {
            int i2 = i + 1;
            Sheet sheetCreateSheet = Sheet.createSheet(listCreateRecords, i, numRecords);
            int eofLoc = sheetCreateSheet.getEofLoc() + 1;
            sheetCreateSheet.convertLabelRecords(this.workbook);
            this.sheets.add(new HSSFSheet(this.workbook, sheetCreateSheet));
            numRecords = eofLoc;
            i = i2;
        }
        for (int i3 = 0; i3 < this.workbook.getNumNames(); i3++) {
            Workbook workbook = this.workbook;
            this.names.add(new HSSFName(workbook, workbook.getNameRecord(i3)));
        }
    }

    public HSSFWorkbook(InputStream inputStream) throws IOException {
        this(inputStream, true);
    }

    public HSSFWorkbook(InputStream inputStream, boolean z) throws IOException {
        this(new POIFSFileSystem(inputStream), z);
    }

    private void setPropertiesFromWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public void setProtect(boolean z) {
        this.workbook.setProtect(z);
    }

    public void setPassword(String str) {
        this.workbook.setPassword(str);
    }

    public void setFilePassword(String str) {
        this.workbook.setFilePassword(str);
    }

    public void setSheetOrder(String str, int i) {
        this.workbook.setSheetOrder(str, i);
    }

    public void setSheetName(int i, String str) {
        this.workbook.setSheetName(i, str, (short) 0);
    }

    public void setSheetName(int i, String str, short s) {
        if (i > this.sheets.size() - 1) {
            throw new RuntimeException("Sheet out of bounds");
        }
        if (s != 0 && s != 1) {
            throw new RuntimeException("Unsupported encoding");
        }
        this.workbook.setSheetName(i, str, s);
    }

    public String getSheetName(int i) {
        if (i > this.sheets.size() - 1) {
            throw new RuntimeException("Sheet out of bounds");
        }
        return this.workbook.getSheetName(i);
    }

    public int getSheetIndex(String str) {
        return this.workbook.getSheetIndex(str);
    }

    public HSSFSheet createSheet() {
        HSSFSheet hSSFSheet = new HSSFSheet(this.workbook);
        this.sheets.add(hSSFSheet);
        this.workbook.setSheetName(this.sheets.size() - 1, new StringBuffer().append("Sheet").append(this.sheets.size() - 1).toString());
        WindowTwoRecord windowTwoRecord = (WindowTwoRecord) hSSFSheet.getSheet().findFirstRecordBySid((short) 574);
        windowTwoRecord.setSelected(this.sheets.size() == 1);
        windowTwoRecord.setPaged(this.sheets.size() == 1);
        return hSSFSheet;
    }

    public HSSFSheet cloneSheet(int i) {
        HSSFSheet hSSFSheet = (HSSFSheet) this.sheets.get(i);
        String sheetName = this.workbook.getSheetName(i);
        if (hSSFSheet == null) {
            return null;
        }
        HSSFSheet hSSFSheetCloneSheet = hSSFSheet.cloneSheet(this.workbook);
        WindowTwoRecord windowTwoRecord = (WindowTwoRecord) hSSFSheetCloneSheet.getSheet().findFirstRecordBySid((short) 574);
        windowTwoRecord.setSelected(this.sheets.size() == 1);
        windowTwoRecord.setPaged(this.sheets.size() == 1);
        this.sheets.add(hSSFSheetCloneSheet);
        if (sheetName.length() < 28) {
            this.workbook.setSheetName(this.sheets.size() - 1, new StringBuffer().append(sheetName).append("(2)").toString());
        } else {
            this.workbook.setSheetName(this.sheets.size() - 1, new StringBuffer().append(sheetName.substring(0, 28)).append("(2)").toString());
        }
        return hSSFSheetCloneSheet;
    }

    public HSSFSheet createSheet(String str) {
        HSSFSheet hSSFSheet = new HSSFSheet(this.workbook);
        this.sheets.add(hSSFSheet);
        this.workbook.setSheetName(this.sheets.size() - 1, str);
        WindowTwoRecord windowTwoRecord = (WindowTwoRecord) hSSFSheet.getSheet().findFirstRecordBySid((short) 574);
        windowTwoRecord.setSelected(this.sheets.size() == 1);
        windowTwoRecord.setPaged(this.sheets.size() == 1);
        return hSSFSheet;
    }

    public int getNumberOfSheets() {
        return this.sheets.size();
    }

    public HSSFSheet getSheetAt(int i) {
        return (HSSFSheet) this.sheets.get(i);
    }

    public HSSFSheet getSheet(String str) {
        HSSFSheet hSSFSheet = null;
        for (int i = 0; i < this.sheets.size(); i++) {
            if (this.workbook.getSheetName(i).equals(str)) {
                hSSFSheet = (HSSFSheet) this.sheets.get(i);
            }
        }
        return hSSFSheet;
    }

    public void removeSheetAt(int i) {
        this.sheets.remove(i);
        this.workbook.removeSheet(i);
    }

    public void setBackupFlag(boolean z) {
        this.workbook.getBackupRecord().setBackup(z ? (short) 1 : (short) 0);
    }

    public boolean getBackupFlag() {
        return this.workbook.getBackupRecord().getBackup() != 0;
    }

    public void setRepeatingRowsAndColumns(int i, int i2, int i3, int i4, int i5) {
        boolean z;
        if (i2 == -1 && i3 != -1) {
            throw new IllegalArgumentException("Invalid column range specification");
        }
        if (i4 == -1 && i5 != -1) {
            throw new IllegalArgumentException("Invalid row range specification");
        }
        if (i2 < -1 || i2 >= 255) {
            throw new IllegalArgumentException("Invalid column range specification");
        }
        if (i3 < -1 || i3 >= 255) {
            throw new IllegalArgumentException("Invalid column range specification");
        }
        if (i4 < -1 || i4 > 65535) {
            throw new IllegalArgumentException("Invalid row range specification");
        }
        if (i5 < -1 || i5 > 65535) {
            throw new IllegalArgumentException("Invalid row range specification");
        }
        if (i2 > i3) {
            throw new IllegalArgumentException("Invalid column range specification");
        }
        if (i4 > i5) {
            throw new IllegalArgumentException("Invalid row range specification");
        }
        HSSFSheet sheetAt = getSheetAt(i);
        short sCheckExternSheet = getWorkbook().checkExternSheet(i);
        boolean z2 = (i2 == -1 || i3 == -1 || i4 == -1 || i5 == -1) ? false : true;
        boolean z3 = i2 == -1 && i3 == -1 && i4 == -1 && i5 == -1;
        NameRecord nameRecordFindExistingRowColHeaderNameRecord = findExistingRowColHeaderNameRecord(i);
        if (z3) {
            if (nameRecordFindExistingRowColHeaderNameRecord != null) {
                this.workbook.removeName(findExistingRowColHeaderNameRecordIdx(i + 1));
                return;
            }
            return;
        }
        if (nameRecordFindExistingRowColHeaderNameRecord == null) {
            nameRecordFindExistingRowColHeaderNameRecord = this.workbook.createBuiltInName((byte) 7, i + 1);
            z = true;
        } else {
            z = false;
        }
        nameRecordFindExistingRowColHeaderNameRecord.setDefinitionTextLength(z2 ? (short) 26 : (short) 11);
        Stack stack = new Stack();
        if (z2) {
            MemFuncPtg memFuncPtg = new MemFuncPtg();
            memFuncPtg.setLenRefSubexpression(23);
            stack.add(memFuncPtg);
        }
        if (i2 >= 0) {
            Area3DPtg area3DPtg = new Area3DPtg();
            area3DPtg.setExternSheetIndex(sCheckExternSheet);
            area3DPtg.setFirstColumn((short) i2);
            area3DPtg.setLastColumn((short) i3);
            area3DPtg.setFirstRow((short) 0);
            area3DPtg.setLastRow((short) -1);
            stack.add(area3DPtg);
        }
        if (i4 >= 0) {
            Area3DPtg area3DPtg2 = new Area3DPtg();
            area3DPtg2.setExternSheetIndex(sCheckExternSheet);
            area3DPtg2.setFirstColumn((short) 0);
            area3DPtg2.setLastColumn((short) 255);
            area3DPtg2.setFirstRow((short) i4);
            area3DPtg2.setLastRow((short) i5);
            stack.add(area3DPtg2);
        }
        if (z2) {
            stack.add(new UnionPtg());
        }
        nameRecordFindExistingRowColHeaderNameRecord.setNameDefinition(stack);
        if (z) {
            this.names.add(new HSSFName(this.workbook, nameRecordFindExistingRowColHeaderNameRecord));
        }
        sheetAt.getPrintSetup().setValidSettings(false);
        ((WindowTwoRecord) sheetAt.getSheet().findFirstRecordBySid((short) 574)).setPaged(true);
    }

    private NameRecord findExistingRowColHeaderNameRecord(int i) {
        int iFindExistingRowColHeaderNameRecordIdx = findExistingRowColHeaderNameRecordIdx(i);
        if (iFindExistingRowColHeaderNameRecordIdx == -1) {
            return null;
        }
        return (NameRecord) this.workbook.findNextRecordBySid((short) 24, iFindExistingRowColHeaderNameRecordIdx);
    }

    private int findExistingRowColHeaderNameRecordIdx(int i) {
        int i2 = 0;
        while (true) {
            NameRecord nameRecord = (NameRecord) this.workbook.findNextRecordBySid((short) 24, i2);
            if (nameRecord == null) {
                return -1;
            }
            int sheetIndexFromExternSheetIndex = this.workbook.getSheetIndexFromExternSheetIndex(nameRecord.getEqualsToIndexToSheet() - 1);
            if (isRowColHeaderRecord(nameRecord) && sheetIndexFromExternSheetIndex == i) {
                return i2;
            }
            i2++;
        }
    }

    private boolean isRowColHeaderRecord(NameRecord nameRecord) {
        return nameRecord.getOptionFlag() == 32 && "\u0007".equals(nameRecord.getNameText());
    }

    public HSSFFont createFont() {
        FontRecord fontRecordCreateNewFont = this.workbook.createNewFont();
        short numberOfFonts = (short) (getNumberOfFonts() - 1);
        if (numberOfFonts > 3) {
            numberOfFonts = (short) (numberOfFonts + 1);
        }
        return new HSSFFont(numberOfFonts, fontRecordCreateNewFont);
    }

    public HSSFFont findFont(short s, short s2, short s3, String str, boolean z, boolean z2, short s4, byte b) {
        for (short s5 = 0; s5 < this.workbook.getNumberOfFontRecords(); s5 = (short) (s5 + 1)) {
            if (s5 != 4) {
                HSSFFont hSSFFont = new HSSFFont(s5, this.workbook.getFontRecordAt(s5));
                if (hSSFFont.getBoldweight() == s && hSSFFont.getColor() == s2 && hSSFFont.getFontHeight() == s3 && hSSFFont.getFontName().equals(str) && hSSFFont.getItalic() == z && hSSFFont.getStrikeout() == z2 && hSSFFont.getTypeOffset() == s4 && hSSFFont.getUnderline() == b) {
                    return hSSFFont;
                }
            }
        }
        return null;
    }

    public short getNumberOfFonts() {
        return (short) this.workbook.getNumberOfFontRecords();
    }

    public HSSFFont getFontAt(short s) {
        return new HSSFFont(s, this.workbook.getFontRecordAt(s));
    }

    public HSSFCellStyle createCellStyle() {
        return new HSSFCellStyle((short) (getNumCellStyles() - 1), this.workbook.createCellXF());
    }

    public short getNumCellStyles() {
        return (short) this.workbook.getNumExFormats();
    }

    public HSSFCellStyle getCellStyleAt(short s) {
        return new HSSFCellStyle(s, this.workbook.getExFormatAt(s));
    }

    public void write(OutputStream outputStream) throws IOException {
        byte[] bytes = getBytes();
        FilePassRecord filePassRecord = this.workbook.getFilePassRecord();
        if (filePassRecord != null) {
            PwdUtils.encrypt(bytes, filePassRecord);
        }
        POIFSFileSystem pOIFSFileSystem = new POIFSFileSystem();
        pOIFSFileSystem.createDocument(new ByteArrayInputStream(bytes), "Workbook");
        if (this.preserveNodes) {
            ArrayList arrayList = new ArrayList(1);
            arrayList.add("Workbook");
            copyNodes(this.poifs, pOIFSFileSystem, arrayList);
        }
        pOIFSFileSystem.writeFilesystem(outputStream);
    }

    public byte[] getBytes() {
        if (log.check(1)) {
            log.log(1, "HSSFWorkbook.getBytes()");
        }
        for (int i = 0; i < this.sheets.size(); i++) {
            ((HSSFSheet) this.sheets.get(i)).getSheet().preSerialize();
        }
        int size = this.workbook.getSize();
        for (int i2 = 0; i2 < this.sheets.size(); i2++) {
            this.workbook.setSheetBof(i2, size);
            size += ((HSSFSheet) this.sheets.get(i2)).getSheet().getSize();
        }
        byte[] bArr = new byte[size];
        int iSerialize = this.workbook.serialize(0, bArr);
        for (int i3 = 0; i3 < this.sheets.size(); i3++) {
            iSerialize += ((HSSFSheet) this.sheets.get(i3)).getSheet().serialize(iSerialize, bArr);
        }
        return bArr;
    }

    public int addSSTString(String str) {
        return this.workbook.addSSTString(str);
    }

    public String getSSTString(int i) {
        return this.workbook.getSSTString(i);
    }

    Workbook getWorkbook() {
        return this.workbook;
    }

    public int getNumberOfNames() {
        return this.names.size();
    }

    public HSSFName getNameAt(int i) {
        return (HSSFName) this.names.get(i);
    }

    public String getNameName(int i) {
        return getNameAt(i).getNameName();
    }

    public void setPrintArea(int i, String str) {
        int i2 = i + 1;
        NameRecord specificBuiltinRecord = this.workbook.getSpecificBuiltinRecord((byte) 6, i2);
        if (specificBuiltinRecord == null) {
            specificBuiltinRecord = this.workbook.createBuiltInName((byte) 6, i2);
        }
        specificBuiltinRecord.setExternSheetNumber(getWorkbook().checkExternSheet(i));
        specificBuiltinRecord.setAreaReference(str);
    }

    public void setPrintArea(int i, int i2, int i3, int i4, int i5) {
        setPrintArea(i, new StringBuffer().append(new CellReference(i4, i2, true, true).toString()).append(":").append(new CellReference(i5, i3, true, true).toString()).toString());
    }

    public String getPrintArea(int i) {
        NameRecord specificBuiltinRecord = this.workbook.getSpecificBuiltinRecord((byte) 6, i + 1);
        if (specificBuiltinRecord == null) {
            return null;
        }
        return specificBuiltinRecord.getAreaReference(this.workbook);
    }

    public void removePrintArea(int i) {
        getWorkbook().removeBuiltinRecord((byte) 6, i + 1);
    }

    public HSSFName createName() {
        HSSFName hSSFName = new HSSFName(this.workbook, this.workbook.createName());
        this.names.add(hSSFName);
        return hSSFName;
    }

    public int getNameIndex(String str) {
        for (int i = 0; i < this.names.size(); i++) {
            if (getNameName(i).equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public void removeName(int i) {
        this.names.remove(i);
        this.workbook.removeName(i);
    }

    public HSSFDataFormat createDataFormat() {
        if (this.formatter == null) {
            this.formatter = new HSSFDataFormat(this.workbook);
        }
        return this.formatter;
    }

    public void removeName(String str) {
        removeName(getNameIndex(str));
    }

    public HSSFPalette getCustomPalette() {
        return new HSSFPalette(this.workbook.getCustomPalette());
    }

    private void copyNodes(POIFSFileSystem pOIFSFileSystem, POIFSFileSystem pOIFSFileSystem2, List list) throws IOException {
        DirectoryEntry root = pOIFSFileSystem.getRoot();
        DirectoryEntry root2 = pOIFSFileSystem2.getRoot();
        Iterator entries = root.getEntries();
        while (entries.hasNext()) {
            Entry entry = (Entry) entries.next();
            if (!isInList(entry.getName(), list)) {
                copyNodeRecursively(entry, root2);
            }
        }
    }

    private boolean isInList(String str, List list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(str)) {
                return true;
            }
        }
        return false;
    }

    private void copyNodeRecursively(Entry entry, DirectoryEntry directoryEntry) throws IOException {
        if (entry.isDirectoryEntry()) {
            DirectoryEntry directoryEntryCreateDirectory = directoryEntry.createDirectory(entry.getName());
            Iterator entries = ((DirectoryEntry) entry).getEntries();
            while (entries.hasNext()) {
                copyNodeRecursively((Entry) entries.next(), directoryEntryCreateDirectory);
            }
            return;
        }
        DocumentEntry documentEntry = (DocumentEntry) entry;
        DocumentInputStream documentInputStream = new DocumentInputStream(documentEntry);
        directoryEntry.createDocument(documentEntry.getName(), documentInputStream);
        documentInputStream.close();
    }

    public void insertChartRecord() {
        this.workbook.getRecords().add(this.workbook.findFirstRecordLocBySid((short) 252), new UnknownRecord(DrawingGroupRecord.sid, (short) 90, new byte[]{HSSFErrorConstants.ERROR_VALUE, 0, 0, -16, 82, 0, 0, 0, 0, 0, 6, -16, 24, 0, 0, 0, 1, 8, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, TarConstants.LF_CHR, 0, 11, -16, UnaryPlusPtg.sid, 0, 0, 0, -65, 0, 8, 0, 8, 0, -127, 1, 9, 0, 0, 8, -64, 1, 64, 0, 0, 8, 64, 0, IntPtg.sid, -15, 16, 0, 0, 0, GreaterThanPtg.sid, 0, 0, 8, 12, 0, 0, 8, 23, 0, 0, 8, -9, 0, 0, 16}));
    }
}
