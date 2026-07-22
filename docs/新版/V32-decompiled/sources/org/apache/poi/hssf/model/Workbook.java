package org.apache.poi.hssf.model;

import com.unisound.client.SpeechConstants;
import de.innosystec.unrar.rarfile.BaseBlock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.net.nntp.NNTPReply;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherDggRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherSplitMenuColorsRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BackupRecord;
import org.apache.poi.hssf.record.BookBoolRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.CountryRecord;
import org.apache.poi.hssf.record.DSFRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.ExtSSTRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.ExternSheetRecord;
import org.apache.poi.hssf.record.ExternSheetSubRecord;
import org.apache.poi.hssf.record.FilePassRecord;
import org.apache.poi.hssf.record.FnGroupCountRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.HideObjRecord;
import org.apache.poi.hssf.record.InterfaceEndRecord;
import org.apache.poi.hssf.record.InterfaceHdrRecord;
import org.apache.poi.hssf.record.MMSRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.PasswordRev4Record;
import org.apache.poi.hssf.record.PrecisionRecord;
import org.apache.poi.hssf.record.ProtectRecord;
import org.apache.poi.hssf.record.ProtectionRev4Record;
import org.apache.poi.hssf.record.RecalcIdRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RefreshAllRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.record.SupBookRecord;
import org.apache.poi.hssf.record.TabIdRecord;
import org.apache.poi.hssf.record.UseSelFSRecord;
import org.apache.poi.hssf.record.WindowOneRecord;
import org.apache.poi.hssf.record.WindowProtectRecord;
import org.apache.poi.hssf.record.WriteAccessRecord;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.SheetReferences;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes3.dex */
public class Workbook implements Model {
    private static final short CODEPAGE = 1200;
    private static final int DEBUG = 1;
    static /* synthetic */ Class class$org$apache$poi$hssf$model$Workbook;
    private static POILogger log;
    private DrawingManager drawingManager;
    protected WorkbookRecordList records = new WorkbookRecordList();
    protected SSTRecord sst = null;
    protected FilePassRecord filepass = null;
    protected ExternSheetRecord externSheet = null;
    protected ArrayList boundsheets = new ArrayList();
    protected ArrayList formats = new ArrayList();
    protected ArrayList names = new ArrayList();
    protected int numxfs = 0;
    protected int numfonts = 0;
    private short maxformatid = -1;
    private boolean uses1904datewindowing = false;

    static {
        Class clsClass$ = class$org$apache$poi$hssf$model$Workbook;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.hssf.model.Workbook");
            class$org$apache$poi$hssf$model$Workbook = clsClass$;
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

    public static Workbook createWorkbook(List list) {
        if (log.check(1)) {
            log.log(1, "Workbook (readfile) created with reclen=", new Integer(list.size()));
        }
        Workbook workbook = new Workbook();
        ArrayList arrayList = new ArrayList(list.size() / 3);
        int i = 0;
        while (true) {
            if (i < list.size()) {
                Record record = (Record) list.get(i);
                if (record.getSid() == 10) {
                    arrayList.add(record);
                    if (log.check(1)) {
                        log.log(1, new StringBuffer().append("found workbook eof record at ").append(i).toString());
                    }
                } else {
                    switch (record.getSid()) {
                        case 18:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found protect record at ").append(i).toString());
                            }
                            workbook.records.setProtpos(i);
                            break;
                        case 23:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found extern sheet record at ").append(i).toString());
                            }
                            workbook.externSheet = (ExternSheetRecord) record;
                            break;
                        case 24:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found name record at ").append(i).toString());
                            }
                            workbook.names.add(record);
                            break;
                        case 34:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found datewindow1904 record at ").append(i).toString());
                            }
                            workbook.uses1904datewindowing = ((DateWindow1904Record) record).getWindowing() == 1;
                            break;
                        case 49:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found font record at ").append(i).toString());
                            }
                            workbook.records.setFontpos(i);
                            workbook.numfonts++;
                            break;
                        case 64:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found backup record at ").append(i).toString());
                            }
                            workbook.records.setBackuppos(i);
                            break;
                        case 133:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found boundsheet record at ").append(i).toString());
                            }
                            workbook.boundsheets.add(record);
                            workbook.records.setBspos(i);
                            break;
                        case 146:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found palette record at ").append(i).toString());
                            }
                            workbook.records.setPalettepos(i);
                            break;
                        case 224:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found XF record at ").append(i).toString());
                            }
                            workbook.records.setXfpos(i);
                            workbook.numxfs++;
                            break;
                        case 252:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found sst record at ").append(i).toString());
                            }
                            workbook.sst = (SSTRecord) record;
                            break;
                        case 317:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found tabid record at ").append(i).toString());
                            }
                            workbook.records.setTabpos(i);
                            break;
                        case NNTPReply.NO_SUCH_ARTICLE_FOUND /* 430 */:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found SupBook record at ").append(i).toString());
                            }
                            break;
                        case SpeechConstants.ASR_OPT_PRINT_LOG /* 1054 */:
                            if (log.check(1)) {
                                log.log(1, new StringBuffer().append("found format record at ").append(i).toString());
                            }
                            workbook.formats.add(record);
                            FormatRecord formatRecord = (FormatRecord) record;
                            workbook.maxformatid = workbook.maxformatid >= formatRecord.getIndexCode() ? workbook.maxformatid : formatRecord.getIndexCode();
                            break;
                    }
                    arrayList.add(record);
                    i++;
                }
            }
        }
        workbook.records.setRecords(arrayList);
        if (log.check(1)) {
            log.log(1, "exit create workbook from existing file function");
        }
        return workbook;
    }

    public static Workbook createWorkbook() {
        if (log.check(1)) {
            log.log(1, "creating new workbook from scratch");
        }
        Workbook workbook = new Workbook();
        ArrayList arrayList = new ArrayList(30);
        ArrayList arrayList2 = new ArrayList(8);
        arrayList.add(workbook.createBOF());
        arrayList.add(workbook.createInterfaceHdr());
        arrayList.add(workbook.createMMS());
        arrayList.add(workbook.createInterfaceEnd());
        arrayList.add(workbook.createWriteAccess());
        arrayList.add(workbook.createCodepage());
        arrayList.add(workbook.createDSF());
        arrayList.add(workbook.createTabId());
        workbook.records.setTabpos(arrayList.size() - 1);
        arrayList.add(workbook.createFnGroupCount());
        arrayList.add(workbook.createWindowProtect());
        arrayList.add(workbook.createProtect());
        workbook.records.setProtpos(arrayList.size() - 1);
        arrayList.add(workbook.createPassword());
        arrayList.add(workbook.createProtectionRev4());
        arrayList.add(workbook.createPasswordRev4());
        arrayList.add(workbook.createWindowOne());
        arrayList.add(workbook.createBackup());
        workbook.records.setBackuppos(arrayList.size() - 1);
        arrayList.add(workbook.createHideObj());
        arrayList.add(workbook.createDateWindow1904());
        arrayList.add(workbook.createPrecision());
        arrayList.add(workbook.createRefreshAll());
        arrayList.add(workbook.createBookBool());
        arrayList.add(workbook.createFont());
        arrayList.add(workbook.createFont());
        arrayList.add(workbook.createFont());
        arrayList.add(workbook.createFont());
        workbook.records.setFontpos(arrayList.size() - 1);
        workbook.numfonts = 4;
        for (int i = 0; i <= 7; i++) {
            Record recordCreateFormat = workbook.createFormat(i);
            FormatRecord formatRecord = (FormatRecord) recordCreateFormat;
            workbook.maxformatid = workbook.maxformatid >= formatRecord.getIndexCode() ? workbook.maxformatid : formatRecord.getIndexCode();
            arrayList2.add(recordCreateFormat);
            arrayList.add(recordCreateFormat);
        }
        workbook.formats = arrayList2;
        for (int i2 = 0; i2 < 21; i2++) {
            arrayList.add(workbook.createExtendedFormat(i2));
            workbook.numxfs++;
        }
        workbook.records.setXfpos(arrayList.size() - 1);
        for (int i3 = 0; i3 < 6; i3++) {
            arrayList.add(workbook.createStyle(i3));
        }
        arrayList.add(workbook.createUseSelFS());
        for (int i4 = 0; i4 < 1; i4++) {
            BoundSheetRecord boundSheetRecord = (BoundSheetRecord) workbook.createBoundSheet(i4);
            arrayList.add(boundSheetRecord);
            workbook.boundsheets.add(boundSheetRecord);
            workbook.records.setBspos(arrayList.size() - 1);
        }
        arrayList.add(workbook.createCountry());
        SSTRecord sSTRecord = (SSTRecord) workbook.createSST();
        workbook.sst = sSTRecord;
        arrayList.add(sSTRecord);
        arrayList.add(workbook.createExtendedSST());
        arrayList.add(workbook.createEOF());
        workbook.records.setRecords(arrayList);
        if (log.check(1)) {
            log.log(1, "exit create new workbook from scratch");
        }
        return workbook;
    }

    public NameRecord getSpecificBuiltinRecord(byte b, int i) {
        for (NameRecord nameRecord : this.names) {
            if (nameRecord.getBuiltInName() == b && nameRecord.getIndexToSheet() == i) {
                return nameRecord;
            }
        }
        return null;
    }

    public void removeBuiltinRecord(byte b, int i) {
        NameRecord specificBuiltinRecord = getSpecificBuiltinRecord(b, i);
        if (specificBuiltinRecord != null) {
            this.names.remove(specificBuiltinRecord);
        }
    }

    public int getNumRecords() {
        return this.records.size();
    }

    public FontRecord getFontRecordAt(int i) {
        int i2 = i > 4 ? i - 1 : i;
        if (i2 > this.numfonts - 1) {
            throw new ArrayIndexOutOfBoundsException(new StringBuffer().append("There are only ").append(this.numfonts).append(" font records, you asked for ").append(i).toString());
        }
        WorkbookRecordList workbookRecordList = this.records;
        return (FontRecord) workbookRecordList.get((workbookRecordList.getFontpos() - (this.numfonts - 1)) + i2);
    }

    public FontRecord createNewFont() {
        FontRecord fontRecord = (FontRecord) createFont();
        WorkbookRecordList workbookRecordList = this.records;
        workbookRecordList.add(workbookRecordList.getFontpos() + 1, fontRecord);
        WorkbookRecordList workbookRecordList2 = this.records;
        workbookRecordList2.setFontpos(workbookRecordList2.getFontpos() + 1);
        this.numfonts++;
        return fontRecord;
    }

    public int getNumberOfFontRecords() {
        return this.numfonts;
    }

    public void setSheetBof(int i, int i2) {
        if (log.check(1)) {
            log.log(1, "setting bof for sheetnum =", new Integer(i), " at pos=", new Integer(i2));
        }
        checkSheets(i);
        ((BoundSheetRecord) this.boundsheets.get(i)).setPositionOfBof(i2);
    }

    public BackupRecord getBackupRecord() {
        WorkbookRecordList workbookRecordList = this.records;
        return (BackupRecord) workbookRecordList.get(workbookRecordList.getBackuppos());
    }

    public void setSheetName(int i, String str) {
        setSheetName(i, str, (short) 0);
    }

    public void setSheetName(int i, String str, short s) {
        checkSheets(i);
        BoundSheetRecord boundSheetRecord = (BoundSheetRecord) this.boundsheets.get(i);
        boundSheetRecord.setSheetname(str);
        boundSheetRecord.setSheetnameLength((byte) str.length());
        boundSheetRecord.setCompressedUnicodeFlag((byte) s);
    }

    public void setSheetOrder(String str, int i) {
        int sheetIndex = getSheetIndex(str);
        ArrayList arrayList = this.boundsheets;
        arrayList.add(i, arrayList.remove(sheetIndex));
    }

    public String getSheetName(int i) {
        return ((BoundSheetRecord) this.boundsheets.get(i)).getSheetname();
    }

    public int getSheetIndex(String str) {
        for (int i = 0; i < this.boundsheets.size(); i++) {
            if (getSheetName(i).equalsIgnoreCase(str)) {
                return i;
            }
        }
        return -1;
    }

    private void checkSheets(int i) {
        if (this.boundsheets.size() <= i) {
            if (this.boundsheets.size() + 1 <= i) {
                throw new RuntimeException("Sheet number out of bounds!");
            }
            BoundSheetRecord boundSheetRecord = (BoundSheetRecord) createBoundSheet(i);
            WorkbookRecordList workbookRecordList = this.records;
            workbookRecordList.add(workbookRecordList.getBspos() + 1, boundSheetRecord);
            WorkbookRecordList workbookRecordList2 = this.records;
            workbookRecordList2.setBspos(workbookRecordList2.getBspos() + 1);
            this.boundsheets.add(boundSheetRecord);
            fixTabIdRecord();
        }
    }

    public void removeSheet(int i) {
        if (this.boundsheets.size() > i) {
            WorkbookRecordList workbookRecordList = this.records;
            workbookRecordList.remove((workbookRecordList.getBspos() - (this.boundsheets.size() - 1)) + i);
            this.boundsheets.remove(i);
            fixTabIdRecord();
        }
    }

    private void fixTabIdRecord() {
        WorkbookRecordList workbookRecordList = this.records;
        TabIdRecord tabIdRecord = (TabIdRecord) workbookRecordList.get(workbookRecordList.getTabpos());
        int size = this.boundsheets.size();
        short[] sArr = new short[size];
        for (short s = 0; s < size; s = (short) (s + 1)) {
            sArr[s] = s;
        }
        tabIdRecord.setTabIdArray(sArr);
    }

    public int getNumSheets() {
        if (log.check(1)) {
            log.log(1, "getNumSheets=", new Integer(this.boundsheets.size()));
        }
        return this.boundsheets.size();
    }

    public int getNumExFormats() {
        if (log.check(1)) {
            log.log(1, "getXF=", new Integer(this.numxfs));
        }
        return this.numxfs;
    }

    public ExtendedFormatRecord getExFormatAt(int i) {
        return (ExtendedFormatRecord) this.records.get((this.records.getXfpos() - (this.numxfs - 1)) + i);
    }

    public ExtendedFormatRecord createCellXF() {
        ExtendedFormatRecord extendedFormatRecordCreateExtendedFormat = createExtendedFormat();
        WorkbookRecordList workbookRecordList = this.records;
        workbookRecordList.add(workbookRecordList.getXfpos() + 1, extendedFormatRecordCreateExtendedFormat);
        WorkbookRecordList workbookRecordList2 = this.records;
        workbookRecordList2.setXfpos(workbookRecordList2.getXfpos() + 1);
        this.numxfs++;
        return extendedFormatRecordCreateExtendedFormat;
    }

    public int addSSTString(String str, boolean z) {
        if (log.check(1)) {
            log.log(1, "insert to sst string='", str, "' and use16bits= ", new Boolean(z));
        }
        if (this.sst == null) {
            insertSST();
        }
        return this.sst.addString(str, z);
    }

    public int addSSTString(String str) {
        return addSSTString(str, false);
    }

    public String getSSTString(int i) {
        if (this.sst == null) {
            insertSST();
        }
        String string = this.sst.getString(i);
        if (log.check(1)) {
            log.log(1, "Returning SST for index=", new Integer(i), " String= ", string);
        }
        return string;
    }

    public void insertSST() {
        if (log.check(1)) {
            log.log(1, "creating new SST via insertSST!");
        }
        this.sst = (SSTRecord) createSST();
        WorkbookRecordList workbookRecordList = this.records;
        workbookRecordList.add(workbookRecordList.size() - 1, createExtendedSST());
        this.records.add(r0.size() - 2, this.sst);
    }

    public int serialize(int i, byte[] bArr) {
        if (log.check(1)) {
            log.log(1, "Serializing Workbook with offsets");
        }
        SSTRecord sSTRecord = null;
        int iSerialize = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.records.size(); i3++) {
            Record recordCreateExtSSTRecord = this.records.get(i3);
            if (recordCreateExtSSTRecord.getSid() != 449 || ((RecalcIdRecord) recordCreateExtSSTRecord).isNeeded()) {
                if (recordCreateExtSSTRecord instanceof SSTRecord) {
                    sSTRecord = (SSTRecord) recordCreateExtSSTRecord;
                    i2 = iSerialize;
                }
                if (recordCreateExtSSTRecord.getSid() == 255 && sSTRecord != null) {
                    recordCreateExtSSTRecord = sSTRecord.createExtSSTRecord(i2 + i);
                }
                iSerialize += recordCreateExtSSTRecord.serialize(iSerialize + i, bArr);
            }
        }
        if (log.check(1)) {
            log.log(1, "Exiting serialize workbook");
        }
        return iSerialize;
    }

    public int getSize() {
        int recordSize;
        SSTRecord sSTRecord = null;
        int i = 0;
        for (int i2 = 0; i2 < this.records.size(); i2++) {
            Record record = this.records.get(i2);
            if (record.getSid() != 449 || ((RecalcIdRecord) record).isNeeded()) {
                if (record instanceof SSTRecord) {
                    sSTRecord = (SSTRecord) record;
                }
                if (record.getSid() == 255 && sSTRecord != null) {
                    recordSize = sSTRecord.calcExtSSTRecordSize();
                } else {
                    recordSize = record.getRecordSize();
                }
                i += recordSize;
            }
        }
        return i;
    }

    protected Record createBOF() {
        BOFRecord bOFRecord = new BOFRecord();
        bOFRecord.setVersion((short) 1536);
        bOFRecord.setType((short) 5);
        bOFRecord.setBuild(BOFRecord.BUILD);
        bOFRecord.setBuildYear(BOFRecord.BUILD_YEAR);
        bOFRecord.setHistoryBitMask(65);
        bOFRecord.setRequiredVersion(6);
        return bOFRecord;
    }

    protected Record createInterfaceHdr() {
        InterfaceHdrRecord interfaceHdrRecord = new InterfaceHdrRecord();
        interfaceHdrRecord.setCodepage((short) 1200);
        return interfaceHdrRecord;
    }

    protected Record createMMS() {
        MMSRecord mMSRecord = new MMSRecord();
        mMSRecord.setAddMenuCount((byte) 0);
        mMSRecord.setDelMenuCount((byte) 0);
        return mMSRecord;
    }

    protected Record createInterfaceEnd() {
        return new InterfaceEndRecord();
    }

    protected Record createWriteAccess() {
        WriteAccessRecord writeAccessRecord = new WriteAccessRecord();
        writeAccessRecord.setUsername(System.getProperty("user.name"));
        return writeAccessRecord;
    }

    protected Record createCodepage() {
        CodepageRecord codepageRecord = new CodepageRecord();
        codepageRecord.setCodepage((short) 1200);
        return codepageRecord;
    }

    protected Record createDSF() {
        DSFRecord dSFRecord = new DSFRecord();
        dSFRecord.setDsf((short) 0);
        return dSFRecord;
    }

    protected Record createTabId() {
        TabIdRecord tabIdRecord = new TabIdRecord();
        tabIdRecord.setTabIdArray(new short[]{0});
        return tabIdRecord;
    }

    protected Record createFnGroupCount() {
        FnGroupCountRecord fnGroupCountRecord = new FnGroupCountRecord();
        fnGroupCountRecord.setCount((short) 14);
        return fnGroupCountRecord;
    }

    protected Record createWindowProtect() {
        WindowProtectRecord windowProtectRecord = new WindowProtectRecord();
        windowProtectRecord.setProtect(false);
        return windowProtectRecord;
    }

    protected Record createProtect() {
        ProtectRecord protectRecord = new ProtectRecord();
        protectRecord.setProtect(false);
        return protectRecord;
    }

    public void setProtect(boolean z) {
        ProtectRecord protectRecord = (ProtectRecord) findFirstRecordBySid((short) 18);
        if (protectRecord == null) {
            ProtectRecord protectRecord2 = (ProtectRecord) createProtect();
            protectRecord2.setProtect(z);
            WorkbookRecordList workbookRecordList = this.records;
            workbookRecordList.add(workbookRecordList.getProtpos(), protectRecord2);
            return;
        }
        protectRecord.setProtect(z);
    }

    protected Record createPassword() {
        PasswordRecord passwordRecord = new PasswordRecord();
        passwordRecord.setPassword((short) 0);
        return passwordRecord;
    }

    public void setPassword(String str) {
        PasswordRecord passwordRecord = (PasswordRecord) findFirstRecordBySid((short) 19);
        if (passwordRecord == null) {
            PasswordRecord passwordRecord2 = (PasswordRecord) createPassword();
            passwordRecord2.setPassword(str);
            WorkbookRecordList workbookRecordList = this.records;
            workbookRecordList.add(workbookRecordList.getProtpos(), passwordRecord2);
            return;
        }
        passwordRecord.setPassword(str);
    }

    public void setFilePassword(String str) {
        FilePassRecord filePassRecord = this.filepass;
        if (filePassRecord == null) {
            FilePassRecord filePassRecord2 = new FilePassRecord();
            this.filepass = filePassRecord2;
            filePassRecord2.setPassword(str);
            this.records.add(1, this.filepass);
            return;
        }
        filePassRecord.setPassword(str);
    }

    public FilePassRecord getFilePassRecord() {
        return this.filepass;
    }

    protected Record createProtectionRev4() {
        ProtectionRev4Record protectionRev4Record = new ProtectionRev4Record();
        protectionRev4Record.setProtect(false);
        return protectionRev4Record;
    }

    protected Record createPasswordRev4() {
        PasswordRev4Record passwordRev4Record = new PasswordRev4Record();
        passwordRev4Record.setPassword((short) 0);
        return passwordRev4Record;
    }

    protected Record createWindowOne() {
        WindowOneRecord windowOneRecord = new WindowOneRecord();
        windowOneRecord.setHorizontalHold((short) 360);
        windowOneRecord.setVerticalHold(EscherProperties.BLIP__PICTURELINE);
        windowOneRecord.setWidth((short) 14940);
        windowOneRecord.setHeight((short) 9150);
        windowOneRecord.setOptions((short) 56);
        windowOneRecord.setSelectedTab((short) 0);
        windowOneRecord.setDisplayedTab((short) 0);
        windowOneRecord.setNumSelectedTabs((short) 1);
        windowOneRecord.setTabWidthRatio((short) 600);
        return windowOneRecord;
    }

    protected Record createBackup() {
        BackupRecord backupRecord = new BackupRecord();
        backupRecord.setBackup((short) 0);
        return backupRecord;
    }

    protected Record createHideObj() {
        HideObjRecord hideObjRecord = new HideObjRecord();
        hideObjRecord.setHideObj((short) 0);
        return hideObjRecord;
    }

    protected Record createDateWindow1904() {
        DateWindow1904Record dateWindow1904Record = new DateWindow1904Record();
        dateWindow1904Record.setWindowing((short) 0);
        return dateWindow1904Record;
    }

    protected Record createPrecision() {
        PrecisionRecord precisionRecord = new PrecisionRecord();
        precisionRecord.setFullPrecision(true);
        return precisionRecord;
    }

    protected Record createRefreshAll() {
        RefreshAllRecord refreshAllRecord = new RefreshAllRecord();
        refreshAllRecord.setRefreshAll(false);
        return refreshAllRecord;
    }

    protected Record createBookBool() {
        BookBoolRecord bookBoolRecord = new BookBoolRecord();
        bookBoolRecord.setSaveLinkValues((short) 0);
        return bookBoolRecord;
    }

    protected Record createFont() {
        FontRecord fontRecord = new FontRecord();
        fontRecord.setFontHeight(EscherAggregate.ST_ACTIONBUTTONMOVIE);
        fontRecord.setAttributes((short) 0);
        fontRecord.setColorPaletteIndex(HSSFFont.COLOR_NORMAL);
        fontRecord.setBoldWeight((short) 400);
        fontRecord.setFontNameLength((byte) 5);
        fontRecord.setFontName(HSSFFont.FONT_ARIAL);
        return fontRecord;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    protected Record createFormat(int i) {
        FormatRecord formatRecord = new FormatRecord();
        switch (i) {
            case 0:
                formatRecord.setIndexCode((short) 5);
                formatRecord.setFormatStringLength((byte) 23);
                formatRecord.setFormatString("\"$\"#,##0_);\\(\"$\"#,##0\\)");
                return formatRecord;
            case 1:
                formatRecord.setIndexCode((short) 6);
                formatRecord.setFormatStringLength((byte) 28);
                formatRecord.setFormatString("\"$\"#,##0_);[Red]\\(\"$\"#,##0\\)");
                return formatRecord;
            case 2:
                formatRecord.setIndexCode((short) 7);
                formatRecord.setFormatStringLength((byte) 29);
                formatRecord.setFormatString("\"$\"#,##0.00_);\\(\"$\"#,##0.00\\)");
                return formatRecord;
            case 3:
                formatRecord.setIndexCode((short) 8);
                formatRecord.setFormatStringLength((byte) 34);
                formatRecord.setFormatString("\"$\"#,##0.00_);[Red]\\(\"$\"#,##0.00\\)");
                return formatRecord;
            case 4:
                formatRecord.setIndexCode((short) 42);
                formatRecord.setFormatStringLength(TarConstants.LF_SYMLINK);
                formatRecord.setFormatString("_(\"$\"* #,##0_);_(\"$\"* \\(#,##0\\);_(\"$\"* \"-\"_);_(@_)");
                return formatRecord;
            case 5:
                formatRecord.setIndexCode((short) 41);
                formatRecord.setFormatStringLength(MemFuncPtg.sid);
                formatRecord.setFormatString("_(* #,##0_);_(* \\(#,##0\\);_(* \"-\"_);_(@_)");
                return formatRecord;
            case 6:
                formatRecord.setIndexCode((short) 44);
                formatRecord.setFormatStringLength(Ref3DPtg.sid);
                formatRecord.setFormatString("_(\"$\"* #,##0.00_);_(\"$\"* \\(#,##0.00\\);_(\"$\"* \"-\"??_);_(@_)");
                return formatRecord;
            case 7:
                formatRecord.setIndexCode((short) 43);
                formatRecord.setFormatStringLength(TarConstants.LF_LINK);
                formatRecord.setFormatString("_(* #,##0.00_);_(* \\(#,##0.00\\);_(* \"-\"??_);_(@_)");
                return formatRecord;
            default:
                return formatRecord;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    protected Record createExtendedFormat(int i) {
        ExtendedFormatRecord extendedFormatRecord = new ExtendedFormatRecord();
        switch (i) {
            case 0:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) 0);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 1:
                extendedFormatRecord.setFontIndex((short) 1);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 2:
                extendedFormatRecord.setFontIndex((short) 1);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 3:
                extendedFormatRecord.setFontIndex((short) 2);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 4:
                extendedFormatRecord.setFontIndex((short) 2);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 5:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 6:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 7:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 8:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 9:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 10:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 11:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 12:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 13:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 14:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -3072);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 15:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) 1);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) 0);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 16:
                extendedFormatRecord.setFontIndex((short) 1);
                extendedFormatRecord.setFormatIndex((short) 43);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -2048);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 17:
                extendedFormatRecord.setFontIndex((short) 1);
                extendedFormatRecord.setFormatIndex((short) 41);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -2048);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 18:
                extendedFormatRecord.setFontIndex((short) 1);
                extendedFormatRecord.setFormatIndex((short) 44);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -2048);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 19:
                extendedFormatRecord.setFontIndex((short) 1);
                extendedFormatRecord.setFormatIndex((short) 42);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -2048);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 20:
                extendedFormatRecord.setFontIndex((short) 1);
                extendedFormatRecord.setFormatIndex((short) 9);
                extendedFormatRecord.setCellOptions((short) -11);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) -2048);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 21:
                extendedFormatRecord.setFontIndex((short) 5);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) 1);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions(BaseBlock.LHD_VERSION);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 22:
                extendedFormatRecord.setFontIndex((short) 6);
                extendedFormatRecord.setFormatIndex((short) 0);
                extendedFormatRecord.setCellOptions((short) 1);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) 23552);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 23:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 49);
                extendedFormatRecord.setCellOptions((short) 1);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) 23552);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 24:
                extendedFormatRecord.setFontIndex((short) 0);
                extendedFormatRecord.setFormatIndex((short) 8);
                extendedFormatRecord.setCellOptions((short) 1);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) 23552);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            case 25:
                extendedFormatRecord.setFontIndex((short) 6);
                extendedFormatRecord.setFormatIndex((short) 8);
                extendedFormatRecord.setCellOptions((short) 1);
                extendedFormatRecord.setAlignmentOptions((short) 32);
                extendedFormatRecord.setIndentionOptions((short) 23552);
                extendedFormatRecord.setBorderOptions((short) 0);
                extendedFormatRecord.setPaletteOptions((short) 0);
                extendedFormatRecord.setAdtlPaletteOptions((short) 0);
                extendedFormatRecord.setFillPaletteOptions((short) 8384);
                return extendedFormatRecord;
            default:
                return extendedFormatRecord;
        }
    }

    protected ExtendedFormatRecord createExtendedFormat() {
        ExtendedFormatRecord extendedFormatRecord = new ExtendedFormatRecord();
        extendedFormatRecord.setFontIndex((short) 0);
        extendedFormatRecord.setFormatIndex((short) 0);
        extendedFormatRecord.setCellOptions((short) 1);
        extendedFormatRecord.setAlignmentOptions((short) 32);
        extendedFormatRecord.setIndentionOptions((short) 0);
        extendedFormatRecord.setBorderOptions((short) 0);
        extendedFormatRecord.setPaletteOptions((short) 0);
        extendedFormatRecord.setAdtlPaletteOptions((short) 0);
        extendedFormatRecord.setFillPaletteOptions((short) 8384);
        extendedFormatRecord.setTopBorderPaletteIdx((short) 8);
        extendedFormatRecord.setBottomBorderPaletteIdx((short) 8);
        extendedFormatRecord.setLeftBorderPaletteIdx((short) 8);
        extendedFormatRecord.setRightBorderPaletteIdx((short) 8);
        return extendedFormatRecord;
    }

    protected Record createStyle(int i) {
        StyleRecord styleRecord = new StyleRecord();
        if (i == 0) {
            styleRecord.setIndex((short) -32752);
            styleRecord.setBuiltin((byte) 3);
            styleRecord.setOutlineStyleLevel((byte) -1);
        } else if (i == 1) {
            styleRecord.setIndex((short) -32751);
            styleRecord.setBuiltin((byte) 6);
            styleRecord.setOutlineStyleLevel((byte) -1);
        } else if (i == 2) {
            styleRecord.setIndex((short) -32750);
            styleRecord.setBuiltin((byte) 4);
            styleRecord.setOutlineStyleLevel((byte) -1);
        } else if (i == 3) {
            styleRecord.setIndex((short) -32749);
            styleRecord.setBuiltin((byte) 7);
            styleRecord.setOutlineStyleLevel((byte) -1);
        } else if (i == 4) {
            styleRecord.setIndex(BaseBlock.LONG_BLOCK);
            styleRecord.setBuiltin((byte) 0);
            styleRecord.setOutlineStyleLevel((byte) -1);
        } else if (i == 5) {
            styleRecord.setIndex((short) -32748);
            styleRecord.setBuiltin((byte) 5);
            styleRecord.setOutlineStyleLevel((byte) -1);
        }
        return styleRecord;
    }

    protected PaletteRecord createPalette() {
        return new PaletteRecord((short) 146);
    }

    protected Record createUseSelFS() {
        UseSelFSRecord useSelFSRecord = new UseSelFSRecord();
        useSelFSRecord.setFlag((short) 0);
        return useSelFSRecord;
    }

    protected Record createBoundSheet(int i) {
        BoundSheetRecord boundSheetRecord = new BoundSheetRecord();
        if (i == 0) {
            boundSheetRecord.setPositionOfBof(0);
            boundSheetRecord.setOptionFlags((short) 0);
            boundSheetRecord.setSheetnameLength((byte) 6);
            boundSheetRecord.setCompressedUnicodeFlag((byte) 0);
            boundSheetRecord.setSheetname("Sheet1");
        } else if (i == 1) {
            boundSheetRecord.setPositionOfBof(0);
            boundSheetRecord.setOptionFlags((short) 0);
            boundSheetRecord.setSheetnameLength((byte) 6);
            boundSheetRecord.setCompressedUnicodeFlag((byte) 0);
            boundSheetRecord.setSheetname("Sheet2");
        } else if (i == 2) {
            boundSheetRecord.setPositionOfBof(0);
            boundSheetRecord.setOptionFlags((short) 0);
            boundSheetRecord.setSheetnameLength((byte) 6);
            boundSheetRecord.setCompressedUnicodeFlag((byte) 0);
            boundSheetRecord.setSheetname("Sheet3");
        }
        return boundSheetRecord;
    }

    protected Record createCountry() {
        CountryRecord countryRecord = new CountryRecord();
        countryRecord.setDefaultCountry((short) 1);
        if (Locale.getDefault().toString().equals("ru_RU")) {
            countryRecord.setCurrentCountry((short) 7);
        } else {
            countryRecord.setCurrentCountry((short) 1);
        }
        return countryRecord;
    }

    protected Record createSST() {
        return new SSTRecord();
    }

    protected Record createExtendedSST() {
        ExtSSTRecord extSSTRecord = new ExtSSTRecord();
        extSSTRecord.setNumStringsPerBucket((short) 8);
        return extSSTRecord;
    }

    protected Record createEOF() {
        return new EOFRecord();
    }

    public SheetReferences getSheetReferences() {
        SheetReferences sheetReferences = new SheetReferences();
        if (this.externSheet != null) {
            for (int i = 0; i < this.externSheet.getNumOfREFStructures(); i++) {
                sheetReferences.addSheetReference(findSheetNameFromExternSheet((short) i), i);
            }
        }
        return sheetReferences;
    }

    public String findSheetNameFromExternSheet(short s) {
        short indexToFirstSupBook = this.externSheet.getREFRecordAt(s).getIndexToFirstSupBook();
        return indexToFirstSupBook > -1 ? getSheetName(indexToFirstSupBook) : "";
    }

    public int getSheetIndexFromExternSheetIndex(int i) {
        if (i >= this.externSheet.getNumOfREFStructures()) {
            return -1;
        }
        return this.externSheet.getREFRecordAt(i).getIndexToFirstSupBook();
    }

    public short checkExternSheet(int i) {
        if (this.externSheet == null) {
            this.externSheet = createExternSheet();
        }
        boolean z = false;
        short s = 0;
        for (int i2 = 0; i2 < this.externSheet.getNumOfREFStructures() && !z; i2++) {
            ExternSheetSubRecord rEFRecordAt = this.externSheet.getREFRecordAt(i2);
            if (rEFRecordAt.getIndexToFirstSupBook() == i && rEFRecordAt.getIndexToLastSupBook() == i) {
                s = (short) i2;
                z = true;
            }
        }
        return !z ? addSheetIndexToExternSheet((short) i) : s;
    }

    private short addSheetIndexToExternSheet(short s) {
        ExternSheetSubRecord externSheetSubRecord = new ExternSheetSubRecord();
        externSheetSubRecord.setIndexToFirstSupBook(s);
        externSheetSubRecord.setIndexToLastSupBook(s);
        this.externSheet.addREFRecord(externSheetSubRecord);
        ExternSheetRecord externSheetRecord = this.externSheet;
        externSheetRecord.setNumOfREFStructures((short) (externSheetRecord.getNumOfREFStructures() + 1));
        return (short) (this.externSheet.getNumOfREFStructures() - 1);
    }

    public int getNumNames() {
        return this.names.size();
    }

    public NameRecord getNameRecord(int i) {
        return (NameRecord) this.names.get(i);
    }

    public NameRecord createName() {
        NameRecord nameRecord = new NameRecord();
        int iFindFirstRecordLocBySid = findFirstRecordLocBySid((short) 23);
        if (iFindFirstRecordLocBySid == -1) {
            iFindFirstRecordLocBySid = findFirstRecordLocBySid(SupBookRecord.sid);
        }
        if (iFindFirstRecordLocBySid == -1) {
            iFindFirstRecordLocBySid = findFirstRecordLocBySid((short) 140);
        }
        this.records.add(iFindFirstRecordLocBySid + this.names.size() + 1, nameRecord);
        this.names.add(nameRecord);
        return nameRecord;
    }

    public NameRecord addName(NameRecord nameRecord) {
        int iFindFirstRecordLocBySid = findFirstRecordLocBySid((short) 23);
        if (iFindFirstRecordLocBySid == -1) {
            iFindFirstRecordLocBySid = findFirstRecordLocBySid(SupBookRecord.sid);
        }
        if (iFindFirstRecordLocBySid == -1) {
            iFindFirstRecordLocBySid = findFirstRecordLocBySid((short) 140);
        }
        this.records.add(iFindFirstRecordLocBySid + this.names.size() + 1, nameRecord);
        this.names.add(nameRecord);
        return nameRecord;
    }

    public NameRecord createBuiltInName(byte b, int i) {
        if (i == -1 || i + 1 > 32767) {
            throw new IllegalArgumentException(new StringBuffer().append("Index is not valid [").append(i).append("]").toString());
        }
        NameRecord nameRecord = new NameRecord(b, (short) i);
        addName(nameRecord);
        return nameRecord;
    }

    public void removeName(int i) {
        if (this.names.size() > i) {
            this.records.remove(findFirstRecordLocBySid((short) 24) + i);
            this.names.remove(i);
        }
    }

    protected ExternSheetRecord createExternSheet() {
        ExternSheetRecord externSheetRecord = new ExternSheetRecord();
        int iFindFirstRecordLocBySid = findFirstRecordLocBySid((short) 140) + 1;
        this.records.add(iFindFirstRecordLocBySid, externSheetRecord);
        SupBookRecord supBookRecord = new SupBookRecord();
        supBookRecord.setNumberOfSheets((short) getNumSheets());
        this.records.add(iFindFirstRecordLocBySid, supBookRecord);
        return externSheetRecord;
    }

    public short getFormat(String str, boolean z) {
        for (FormatRecord formatRecord : this.formats) {
            if (formatRecord.getFormatString().equals(str)) {
                return formatRecord.getIndexCode();
            }
        }
        if (z) {
            return createFormat(str);
        }
        return (short) -1;
    }

    public ArrayList getFormats() {
        return this.formats;
    }

    public short createFormat(String str) {
        FormatRecord formatRecord = new FormatRecord();
        short s = this.maxformatid;
        short s2 = EscherAggregate.ST_TEXTINFLATETOP;
        if (s >= 164) {
            s2 = (short) (s + 1);
        }
        this.maxformatid = s2;
        formatRecord.setIndexCode(s2);
        formatRecord.setUnicodeFlag(true);
        formatRecord.setFormatStringLength((byte) str.length());
        formatRecord.setFormatString(str);
        int i = 0;
        while (i < this.records.size() && this.records.get(i).getSid() != 1054) {
            i++;
        }
        int size = i + this.formats.size();
        this.formats.add(formatRecord);
        this.records.add(size, formatRecord);
        return this.maxformatid;
    }

    public Record findFirstRecordBySid(short s) {
        for (Record record : this.records) {
            if (record.getSid() == s) {
                return record;
            }
        }
        return null;
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

    public Record findNextRecordBySid(short s, int i) {
        int i2 = 0;
        for (Record record : this.records) {
            if (record.getSid() == s) {
                int i3 = i2 + 1;
                if (i2 == i) {
                    return record;
                }
                i2 = i3;
            }
        }
        return null;
    }

    public List getRecords() {
        return this.records.getRecords();
    }

    public boolean isUsing1904DateWindowing() {
        return this.uses1904datewindowing;
    }

    public PaletteRecord getCustomPalette() {
        int palettepos = this.records.getPalettepos();
        if (palettepos != -1) {
            Record record = this.records.get(palettepos);
            if (record instanceof PaletteRecord) {
                return (PaletteRecord) record;
            }
            throw new RuntimeException(new StringBuffer().append("InternalError: Expected PaletteRecord but got a '").append(record).append("'").toString());
        }
        PaletteRecord paletteRecordCreatePalette = createPalette();
        this.records.add(1, paletteRecordCreatePalette);
        this.records.setPalettepos(1);
        return paletteRecordCreatePalette;
    }

    public void createDrawingGroup() {
        if (findFirstRecordLocBySid(EscherContainerRecord.DGG_CONTAINER) == -1) {
            EscherContainerRecord escherContainerRecord = new EscherContainerRecord();
            EscherDggRecord escherDggRecord = new EscherDggRecord();
            EscherOptRecord escherOptRecord = new EscherOptRecord();
            EscherSplitMenuColorsRecord escherSplitMenuColorsRecord = new EscherSplitMenuColorsRecord();
            escherContainerRecord.setRecordId(EscherContainerRecord.DGG_CONTAINER);
            escherContainerRecord.setOptions((short) 15);
            escherDggRecord.setRecordId(EscherDggRecord.RECORD_ID);
            escherDggRecord.setOptions((short) 0);
            escherDggRecord.setShapeIdMax(1024);
            escherDggRecord.setNumShapesSaved(0);
            escherDggRecord.setDrawingsSaved(0);
            escherDggRecord.setFileIdClusters(new EscherDggRecord.FileIdCluster[0]);
            this.drawingManager = new DrawingManager(escherDggRecord);
            escherOptRecord.setRecordId(EscherOptRecord.RECORD_ID);
            escherOptRecord.setOptions((short) 51);
            escherOptRecord.addEscherProperty(new EscherBoolProperty((short) 191, 524296));
            escherOptRecord.addEscherProperty(new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR, 134217737));
            escherOptRecord.addEscherProperty(new EscherRGBProperty(EscherProperties.LINESTYLE__COLOR, 134217792));
            escherSplitMenuColorsRecord.setRecordId(EscherSplitMenuColorsRecord.RECORD_ID);
            escherSplitMenuColorsRecord.setOptions((short) 64);
            escherSplitMenuColorsRecord.setColor1(134217741);
            escherSplitMenuColorsRecord.setColor2(134217740);
            escherSplitMenuColorsRecord.setColor3(134217751);
            escherSplitMenuColorsRecord.setColor4(268435703);
            escherContainerRecord.addChildRecord(escherDggRecord);
            escherContainerRecord.addChildRecord(escherOptRecord);
            escherContainerRecord.addChildRecord(escherSplitMenuColorsRecord);
            DrawingGroupRecord drawingGroupRecord = new DrawingGroupRecord();
            drawingGroupRecord.addEscherRecord(escherContainerRecord);
            getRecords().add(findFirstRecordLocBySid((short) 140) + 1, drawingGroupRecord);
        }
    }

    public DrawingManager getDrawingManager() {
        return this.drawingManager;
    }
}
