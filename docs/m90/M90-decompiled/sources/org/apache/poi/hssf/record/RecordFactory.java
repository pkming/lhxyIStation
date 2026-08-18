package org.apache.poi.hssf.record;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class RecordFactory {
    private static int NUM_RECORDS = 10000;
    static /* synthetic */ Class array$B;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$BOFRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$BackupRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$BlankRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$BookBoolRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$BoolErrRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$BottomMarginRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$BoundSheetRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$CalcCountRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$CalcModeRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$CodepageRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$ColumnInfoRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$ContinueRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$CountryRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DBCellRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DSFRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DateWindow1904Record;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DefaultColWidthRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DefaultRowHeightRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DeltaRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DimensionsRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DrawingGroupRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DrawingRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$DrawingSelectionRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$EOFRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$ExtSSTRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$ExtendedFormatRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$ExternSheetRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$FnGroupCountRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$FontRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$FooterRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$FormatRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$FormulaRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$GridsetRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$GutsRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$HCenterRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$HeaderRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$HideObjRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$HorizontalPageBreakRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$IndexRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$InterfaceEndRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$InterfaceHdrRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$IterationRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$LabelRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$LabelSSTRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$LeftMarginRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$MMSRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$MergeCellsRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$MulBlankRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$MulRKRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$NameRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$NumberRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$ObjRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$PaletteRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$PasswordRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$PasswordRev4Record;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$PrecisionRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$PrintGridlinesRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$PrintHeadersRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$PrintSetupRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$ProtectRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$ProtectionRev4Record;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$RKRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$RecalcIdRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$RefModeRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$RefreshAllRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$RightMarginRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$RowRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$SSTRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$SaveRecalcRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$SelectionRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$SharedFormulaRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$StringRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$StyleRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$TabIdRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$TextObjectRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$TopMarginRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$UseSelFSRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$VCenterRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$VerticalPageBreakRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$WSBoolRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$WindowOneRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$WindowProtectRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$WindowTwoRecord;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$WriteAccessRecord;
    private static final Class[] records;
    private static Map recordsMap;

    static {
        Class[] clsArr = new Class[84];
        Class clsClass$ = class$org$apache$poi$hssf$record$BOFRecord;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.hssf.record.BOFRecord");
            class$org$apache$poi$hssf$record$BOFRecord = clsClass$;
        }
        clsArr[0] = clsClass$;
        Class clsClass$2 = class$org$apache$poi$hssf$record$InterfaceHdrRecord;
        if (clsClass$2 == null) {
            clsClass$2 = class$("org.apache.poi.hssf.record.InterfaceHdrRecord");
            class$org$apache$poi$hssf$record$InterfaceHdrRecord = clsClass$2;
        }
        clsArr[1] = clsClass$2;
        Class clsClass$3 = class$org$apache$poi$hssf$record$MMSRecord;
        if (clsClass$3 == null) {
            clsClass$3 = class$("org.apache.poi.hssf.record.MMSRecord");
            class$org$apache$poi$hssf$record$MMSRecord = clsClass$3;
        }
        clsArr[2] = clsClass$3;
        Class clsClass$4 = class$org$apache$poi$hssf$record$InterfaceEndRecord;
        if (clsClass$4 == null) {
            clsClass$4 = class$("org.apache.poi.hssf.record.InterfaceEndRecord");
            class$org$apache$poi$hssf$record$InterfaceEndRecord = clsClass$4;
        }
        clsArr[3] = clsClass$4;
        Class clsClass$5 = class$org$apache$poi$hssf$record$WriteAccessRecord;
        if (clsClass$5 == null) {
            clsClass$5 = class$("org.apache.poi.hssf.record.WriteAccessRecord");
            class$org$apache$poi$hssf$record$WriteAccessRecord = clsClass$5;
        }
        clsArr[4] = clsClass$5;
        Class clsClass$6 = class$org$apache$poi$hssf$record$CodepageRecord;
        if (clsClass$6 == null) {
            clsClass$6 = class$("org.apache.poi.hssf.record.CodepageRecord");
            class$org$apache$poi$hssf$record$CodepageRecord = clsClass$6;
        }
        clsArr[5] = clsClass$6;
        Class clsClass$7 = class$org$apache$poi$hssf$record$DSFRecord;
        if (clsClass$7 == null) {
            clsClass$7 = class$("org.apache.poi.hssf.record.DSFRecord");
            class$org$apache$poi$hssf$record$DSFRecord = clsClass$7;
        }
        clsArr[6] = clsClass$7;
        Class clsClass$8 = class$org$apache$poi$hssf$record$TabIdRecord;
        if (clsClass$8 == null) {
            clsClass$8 = class$("org.apache.poi.hssf.record.TabIdRecord");
            class$org$apache$poi$hssf$record$TabIdRecord = clsClass$8;
        }
        clsArr[7] = clsClass$8;
        Class clsClass$9 = class$org$apache$poi$hssf$record$FnGroupCountRecord;
        if (clsClass$9 == null) {
            clsClass$9 = class$("org.apache.poi.hssf.record.FnGroupCountRecord");
            class$org$apache$poi$hssf$record$FnGroupCountRecord = clsClass$9;
        }
        clsArr[8] = clsClass$9;
        Class clsClass$10 = class$org$apache$poi$hssf$record$WindowProtectRecord;
        if (clsClass$10 == null) {
            clsClass$10 = class$("org.apache.poi.hssf.record.WindowProtectRecord");
            class$org$apache$poi$hssf$record$WindowProtectRecord = clsClass$10;
        }
        clsArr[9] = clsClass$10;
        Class clsClass$11 = class$org$apache$poi$hssf$record$ProtectRecord;
        if (clsClass$11 == null) {
            clsClass$11 = class$("org.apache.poi.hssf.record.ProtectRecord");
            class$org$apache$poi$hssf$record$ProtectRecord = clsClass$11;
        }
        clsArr[10] = clsClass$11;
        Class clsClass$12 = class$org$apache$poi$hssf$record$PasswordRecord;
        if (clsClass$12 == null) {
            clsClass$12 = class$("org.apache.poi.hssf.record.PasswordRecord");
            class$org$apache$poi$hssf$record$PasswordRecord = clsClass$12;
        }
        clsArr[11] = clsClass$12;
        Class clsClass$13 = class$org$apache$poi$hssf$record$ProtectionRev4Record;
        if (clsClass$13 == null) {
            clsClass$13 = class$("org.apache.poi.hssf.record.ProtectionRev4Record");
            class$org$apache$poi$hssf$record$ProtectionRev4Record = clsClass$13;
        }
        clsArr[12] = clsClass$13;
        Class clsClass$14 = class$org$apache$poi$hssf$record$PasswordRev4Record;
        if (clsClass$14 == null) {
            clsClass$14 = class$("org.apache.poi.hssf.record.PasswordRev4Record");
            class$org$apache$poi$hssf$record$PasswordRev4Record = clsClass$14;
        }
        clsArr[13] = clsClass$14;
        Class clsClass$15 = class$org$apache$poi$hssf$record$WindowOneRecord;
        if (clsClass$15 == null) {
            clsClass$15 = class$("org.apache.poi.hssf.record.WindowOneRecord");
            class$org$apache$poi$hssf$record$WindowOneRecord = clsClass$15;
        }
        clsArr[14] = clsClass$15;
        Class clsClass$16 = class$org$apache$poi$hssf$record$BackupRecord;
        if (clsClass$16 == null) {
            clsClass$16 = class$("org.apache.poi.hssf.record.BackupRecord");
            class$org$apache$poi$hssf$record$BackupRecord = clsClass$16;
        }
        clsArr[15] = clsClass$16;
        Class clsClass$17 = class$org$apache$poi$hssf$record$HideObjRecord;
        if (clsClass$17 == null) {
            clsClass$17 = class$("org.apache.poi.hssf.record.HideObjRecord");
            class$org$apache$poi$hssf$record$HideObjRecord = clsClass$17;
        }
        clsArr[16] = clsClass$17;
        Class clsClass$18 = class$org$apache$poi$hssf$record$DateWindow1904Record;
        if (clsClass$18 == null) {
            clsClass$18 = class$("org.apache.poi.hssf.record.DateWindow1904Record");
            class$org$apache$poi$hssf$record$DateWindow1904Record = clsClass$18;
        }
        clsArr[17] = clsClass$18;
        Class clsClass$19 = class$org$apache$poi$hssf$record$PrecisionRecord;
        if (clsClass$19 == null) {
            clsClass$19 = class$("org.apache.poi.hssf.record.PrecisionRecord");
            class$org$apache$poi$hssf$record$PrecisionRecord = clsClass$19;
        }
        clsArr[18] = clsClass$19;
        Class clsClass$20 = class$org$apache$poi$hssf$record$RefreshAllRecord;
        if (clsClass$20 == null) {
            clsClass$20 = class$("org.apache.poi.hssf.record.RefreshAllRecord");
            class$org$apache$poi$hssf$record$RefreshAllRecord = clsClass$20;
        }
        clsArr[19] = clsClass$20;
        Class clsClass$21 = class$org$apache$poi$hssf$record$BookBoolRecord;
        if (clsClass$21 == null) {
            clsClass$21 = class$("org.apache.poi.hssf.record.BookBoolRecord");
            class$org$apache$poi$hssf$record$BookBoolRecord = clsClass$21;
        }
        clsArr[20] = clsClass$21;
        Class clsClass$22 = class$org$apache$poi$hssf$record$FontRecord;
        if (clsClass$22 == null) {
            clsClass$22 = class$("org.apache.poi.hssf.record.FontRecord");
            class$org$apache$poi$hssf$record$FontRecord = clsClass$22;
        }
        clsArr[21] = clsClass$22;
        Class clsClass$23 = class$org$apache$poi$hssf$record$FormatRecord;
        if (clsClass$23 == null) {
            clsClass$23 = class$("org.apache.poi.hssf.record.FormatRecord");
            class$org$apache$poi$hssf$record$FormatRecord = clsClass$23;
        }
        clsArr[22] = clsClass$23;
        Class clsClass$24 = class$org$apache$poi$hssf$record$ExtendedFormatRecord;
        if (clsClass$24 == null) {
            clsClass$24 = class$("org.apache.poi.hssf.record.ExtendedFormatRecord");
            class$org$apache$poi$hssf$record$ExtendedFormatRecord = clsClass$24;
        }
        clsArr[23] = clsClass$24;
        Class clsClass$25 = class$org$apache$poi$hssf$record$StyleRecord;
        if (clsClass$25 == null) {
            clsClass$25 = class$("org.apache.poi.hssf.record.StyleRecord");
            class$org$apache$poi$hssf$record$StyleRecord = clsClass$25;
        }
        clsArr[24] = clsClass$25;
        Class clsClass$26 = class$org$apache$poi$hssf$record$UseSelFSRecord;
        if (clsClass$26 == null) {
            clsClass$26 = class$("org.apache.poi.hssf.record.UseSelFSRecord");
            class$org$apache$poi$hssf$record$UseSelFSRecord = clsClass$26;
        }
        clsArr[25] = clsClass$26;
        Class clsClass$27 = class$org$apache$poi$hssf$record$BoundSheetRecord;
        if (clsClass$27 == null) {
            clsClass$27 = class$("org.apache.poi.hssf.record.BoundSheetRecord");
            class$org$apache$poi$hssf$record$BoundSheetRecord = clsClass$27;
        }
        clsArr[26] = clsClass$27;
        Class clsClass$28 = class$org$apache$poi$hssf$record$CountryRecord;
        if (clsClass$28 == null) {
            clsClass$28 = class$("org.apache.poi.hssf.record.CountryRecord");
            class$org$apache$poi$hssf$record$CountryRecord = clsClass$28;
        }
        clsArr[27] = clsClass$28;
        Class clsClass$29 = class$org$apache$poi$hssf$record$SSTRecord;
        if (clsClass$29 == null) {
            clsClass$29 = class$("org.apache.poi.hssf.record.SSTRecord");
            class$org$apache$poi$hssf$record$SSTRecord = clsClass$29;
        }
        clsArr[28] = clsClass$29;
        Class clsClass$30 = class$org$apache$poi$hssf$record$ExtSSTRecord;
        if (clsClass$30 == null) {
            clsClass$30 = class$("org.apache.poi.hssf.record.ExtSSTRecord");
            class$org$apache$poi$hssf$record$ExtSSTRecord = clsClass$30;
        }
        clsArr[29] = clsClass$30;
        Class clsClass$31 = class$org$apache$poi$hssf$record$EOFRecord;
        if (clsClass$31 == null) {
            clsClass$31 = class$("org.apache.poi.hssf.record.EOFRecord");
            class$org$apache$poi$hssf$record$EOFRecord = clsClass$31;
        }
        clsArr[30] = clsClass$31;
        Class clsClass$32 = class$org$apache$poi$hssf$record$IndexRecord;
        if (clsClass$32 == null) {
            clsClass$32 = class$("org.apache.poi.hssf.record.IndexRecord");
            class$org$apache$poi$hssf$record$IndexRecord = clsClass$32;
        }
        clsArr[31] = clsClass$32;
        Class clsClass$33 = class$org$apache$poi$hssf$record$CalcModeRecord;
        if (clsClass$33 == null) {
            clsClass$33 = class$("org.apache.poi.hssf.record.CalcModeRecord");
            class$org$apache$poi$hssf$record$CalcModeRecord = clsClass$33;
        }
        clsArr[32] = clsClass$33;
        Class clsClass$34 = class$org$apache$poi$hssf$record$CalcCountRecord;
        if (clsClass$34 == null) {
            clsClass$34 = class$("org.apache.poi.hssf.record.CalcCountRecord");
            class$org$apache$poi$hssf$record$CalcCountRecord = clsClass$34;
        }
        clsArr[33] = clsClass$34;
        Class clsClass$35 = class$org$apache$poi$hssf$record$RefModeRecord;
        if (clsClass$35 == null) {
            clsClass$35 = class$("org.apache.poi.hssf.record.RefModeRecord");
            class$org$apache$poi$hssf$record$RefModeRecord = clsClass$35;
        }
        clsArr[34] = clsClass$35;
        Class clsClass$36 = class$org$apache$poi$hssf$record$IterationRecord;
        if (clsClass$36 == null) {
            clsClass$36 = class$("org.apache.poi.hssf.record.IterationRecord");
            class$org$apache$poi$hssf$record$IterationRecord = clsClass$36;
        }
        clsArr[35] = clsClass$36;
        Class clsClass$37 = class$org$apache$poi$hssf$record$DeltaRecord;
        if (clsClass$37 == null) {
            clsClass$37 = class$("org.apache.poi.hssf.record.DeltaRecord");
            class$org$apache$poi$hssf$record$DeltaRecord = clsClass$37;
        }
        clsArr[36] = clsClass$37;
        Class clsClass$38 = class$org$apache$poi$hssf$record$SaveRecalcRecord;
        if (clsClass$38 == null) {
            clsClass$38 = class$("org.apache.poi.hssf.record.SaveRecalcRecord");
            class$org$apache$poi$hssf$record$SaveRecalcRecord = clsClass$38;
        }
        clsArr[37] = clsClass$38;
        Class clsClass$39 = class$org$apache$poi$hssf$record$PrintHeadersRecord;
        if (clsClass$39 == null) {
            clsClass$39 = class$("org.apache.poi.hssf.record.PrintHeadersRecord");
            class$org$apache$poi$hssf$record$PrintHeadersRecord = clsClass$39;
        }
        clsArr[38] = clsClass$39;
        Class clsClass$40 = class$org$apache$poi$hssf$record$PrintGridlinesRecord;
        if (clsClass$40 == null) {
            clsClass$40 = class$("org.apache.poi.hssf.record.PrintGridlinesRecord");
            class$org$apache$poi$hssf$record$PrintGridlinesRecord = clsClass$40;
        }
        clsArr[39] = clsClass$40;
        Class clsClass$41 = class$org$apache$poi$hssf$record$GridsetRecord;
        if (clsClass$41 == null) {
            clsClass$41 = class$("org.apache.poi.hssf.record.GridsetRecord");
            class$org$apache$poi$hssf$record$GridsetRecord = clsClass$41;
        }
        clsArr[40] = clsClass$41;
        Class clsClass$42 = class$org$apache$poi$hssf$record$GutsRecord;
        if (clsClass$42 == null) {
            clsClass$42 = class$("org.apache.poi.hssf.record.GutsRecord");
            class$org$apache$poi$hssf$record$GutsRecord = clsClass$42;
        }
        clsArr[41] = clsClass$42;
        Class clsClass$43 = class$org$apache$poi$hssf$record$DefaultRowHeightRecord;
        if (clsClass$43 == null) {
            clsClass$43 = class$("org.apache.poi.hssf.record.DefaultRowHeightRecord");
            class$org$apache$poi$hssf$record$DefaultRowHeightRecord = clsClass$43;
        }
        clsArr[42] = clsClass$43;
        Class clsClass$44 = class$org$apache$poi$hssf$record$WSBoolRecord;
        if (clsClass$44 == null) {
            clsClass$44 = class$("org.apache.poi.hssf.record.WSBoolRecord");
            class$org$apache$poi$hssf$record$WSBoolRecord = clsClass$44;
        }
        clsArr[43] = clsClass$44;
        Class clsClass$45 = class$org$apache$poi$hssf$record$HeaderRecord;
        if (clsClass$45 == null) {
            clsClass$45 = class$("org.apache.poi.hssf.record.HeaderRecord");
            class$org$apache$poi$hssf$record$HeaderRecord = clsClass$45;
        }
        clsArr[44] = clsClass$45;
        Class clsClass$46 = class$org$apache$poi$hssf$record$FooterRecord;
        if (clsClass$46 == null) {
            clsClass$46 = class$("org.apache.poi.hssf.record.FooterRecord");
            class$org$apache$poi$hssf$record$FooterRecord = clsClass$46;
        }
        clsArr[45] = clsClass$46;
        Class clsClass$47 = class$org$apache$poi$hssf$record$HCenterRecord;
        if (clsClass$47 == null) {
            clsClass$47 = class$("org.apache.poi.hssf.record.HCenterRecord");
            class$org$apache$poi$hssf$record$HCenterRecord = clsClass$47;
        }
        clsArr[46] = clsClass$47;
        Class clsClass$48 = class$org$apache$poi$hssf$record$VCenterRecord;
        if (clsClass$48 == null) {
            clsClass$48 = class$("org.apache.poi.hssf.record.VCenterRecord");
            class$org$apache$poi$hssf$record$VCenterRecord = clsClass$48;
        }
        clsArr[47] = clsClass$48;
        Class clsClass$49 = class$org$apache$poi$hssf$record$PrintSetupRecord;
        if (clsClass$49 == null) {
            clsClass$49 = class$("org.apache.poi.hssf.record.PrintSetupRecord");
            class$org$apache$poi$hssf$record$PrintSetupRecord = clsClass$49;
        }
        clsArr[48] = clsClass$49;
        Class clsClass$50 = class$org$apache$poi$hssf$record$DefaultColWidthRecord;
        if (clsClass$50 == null) {
            clsClass$50 = class$("org.apache.poi.hssf.record.DefaultColWidthRecord");
            class$org$apache$poi$hssf$record$DefaultColWidthRecord = clsClass$50;
        }
        clsArr[49] = clsClass$50;
        Class clsClass$51 = class$org$apache$poi$hssf$record$DimensionsRecord;
        if (clsClass$51 == null) {
            clsClass$51 = class$("org.apache.poi.hssf.record.DimensionsRecord");
            class$org$apache$poi$hssf$record$DimensionsRecord = clsClass$51;
        }
        clsArr[50] = clsClass$51;
        Class clsClass$52 = class$org$apache$poi$hssf$record$RowRecord;
        if (clsClass$52 == null) {
            clsClass$52 = class$("org.apache.poi.hssf.record.RowRecord");
            class$org$apache$poi$hssf$record$RowRecord = clsClass$52;
        }
        clsArr[51] = clsClass$52;
        Class clsClass$53 = class$org$apache$poi$hssf$record$LabelSSTRecord;
        if (clsClass$53 == null) {
            clsClass$53 = class$("org.apache.poi.hssf.record.LabelSSTRecord");
            class$org$apache$poi$hssf$record$LabelSSTRecord = clsClass$53;
        }
        clsArr[52] = clsClass$53;
        Class clsClass$54 = class$org$apache$poi$hssf$record$RKRecord;
        if (clsClass$54 == null) {
            clsClass$54 = class$("org.apache.poi.hssf.record.RKRecord");
            class$org$apache$poi$hssf$record$RKRecord = clsClass$54;
        }
        clsArr[53] = clsClass$54;
        Class clsClass$55 = class$org$apache$poi$hssf$record$NumberRecord;
        if (clsClass$55 == null) {
            clsClass$55 = class$("org.apache.poi.hssf.record.NumberRecord");
            class$org$apache$poi$hssf$record$NumberRecord = clsClass$55;
        }
        clsArr[54] = clsClass$55;
        Class clsClass$56 = class$org$apache$poi$hssf$record$DBCellRecord;
        if (clsClass$56 == null) {
            clsClass$56 = class$("org.apache.poi.hssf.record.DBCellRecord");
            class$org$apache$poi$hssf$record$DBCellRecord = clsClass$56;
        }
        clsArr[55] = clsClass$56;
        Class clsClass$57 = class$org$apache$poi$hssf$record$WindowTwoRecord;
        if (clsClass$57 == null) {
            clsClass$57 = class$("org.apache.poi.hssf.record.WindowTwoRecord");
            class$org$apache$poi$hssf$record$WindowTwoRecord = clsClass$57;
        }
        clsArr[56] = clsClass$57;
        Class clsClass$58 = class$org$apache$poi$hssf$record$SelectionRecord;
        if (clsClass$58 == null) {
            clsClass$58 = class$("org.apache.poi.hssf.record.SelectionRecord");
            class$org$apache$poi$hssf$record$SelectionRecord = clsClass$58;
        }
        clsArr[57] = clsClass$58;
        Class clsClass$59 = class$org$apache$poi$hssf$record$ContinueRecord;
        if (clsClass$59 == null) {
            clsClass$59 = class$("org.apache.poi.hssf.record.ContinueRecord");
            class$org$apache$poi$hssf$record$ContinueRecord = clsClass$59;
        }
        clsArr[58] = clsClass$59;
        Class clsClass$60 = class$org$apache$poi$hssf$record$LabelRecord;
        if (clsClass$60 == null) {
            clsClass$60 = class$("org.apache.poi.hssf.record.LabelRecord");
            class$org$apache$poi$hssf$record$LabelRecord = clsClass$60;
        }
        clsArr[59] = clsClass$60;
        Class clsClass$61 = class$org$apache$poi$hssf$record$BlankRecord;
        if (clsClass$61 == null) {
            clsClass$61 = class$("org.apache.poi.hssf.record.BlankRecord");
            class$org$apache$poi$hssf$record$BlankRecord = clsClass$61;
        }
        clsArr[60] = clsClass$61;
        Class clsClass$62 = class$org$apache$poi$hssf$record$ColumnInfoRecord;
        if (clsClass$62 == null) {
            clsClass$62 = class$("org.apache.poi.hssf.record.ColumnInfoRecord");
            class$org$apache$poi$hssf$record$ColumnInfoRecord = clsClass$62;
        }
        clsArr[61] = clsClass$62;
        Class clsClass$63 = class$org$apache$poi$hssf$record$MulRKRecord;
        if (clsClass$63 == null) {
            clsClass$63 = class$("org.apache.poi.hssf.record.MulRKRecord");
            class$org$apache$poi$hssf$record$MulRKRecord = clsClass$63;
        }
        clsArr[62] = clsClass$63;
        Class clsClass$64 = class$org$apache$poi$hssf$record$MulBlankRecord;
        if (clsClass$64 == null) {
            clsClass$64 = class$("org.apache.poi.hssf.record.MulBlankRecord");
            class$org$apache$poi$hssf$record$MulBlankRecord = clsClass$64;
        }
        clsArr[63] = clsClass$64;
        Class clsClass$65 = class$org$apache$poi$hssf$record$MergeCellsRecord;
        if (clsClass$65 == null) {
            clsClass$65 = class$("org.apache.poi.hssf.record.MergeCellsRecord");
            class$org$apache$poi$hssf$record$MergeCellsRecord = clsClass$65;
        }
        clsArr[64] = clsClass$65;
        Class clsClass$66 = class$org$apache$poi$hssf$record$FormulaRecord;
        if (clsClass$66 == null) {
            clsClass$66 = class$("org.apache.poi.hssf.record.FormulaRecord");
            class$org$apache$poi$hssf$record$FormulaRecord = clsClass$66;
        }
        clsArr[65] = clsClass$66;
        Class clsClass$67 = class$org$apache$poi$hssf$record$BoolErrRecord;
        if (clsClass$67 == null) {
            clsClass$67 = class$("org.apache.poi.hssf.record.BoolErrRecord");
            class$org$apache$poi$hssf$record$BoolErrRecord = clsClass$67;
        }
        clsArr[66] = clsClass$67;
        Class clsClass$68 = class$org$apache$poi$hssf$record$ExternSheetRecord;
        if (clsClass$68 == null) {
            clsClass$68 = class$("org.apache.poi.hssf.record.ExternSheetRecord");
            class$org$apache$poi$hssf$record$ExternSheetRecord = clsClass$68;
        }
        clsArr[67] = clsClass$68;
        Class clsClass$69 = class$org$apache$poi$hssf$record$NameRecord;
        if (clsClass$69 == null) {
            clsClass$69 = class$("org.apache.poi.hssf.record.NameRecord");
            class$org$apache$poi$hssf$record$NameRecord = clsClass$69;
        }
        clsArr[68] = clsClass$69;
        Class clsClass$70 = class$org$apache$poi$hssf$record$LeftMarginRecord;
        if (clsClass$70 == null) {
            clsClass$70 = class$("org.apache.poi.hssf.record.LeftMarginRecord");
            class$org$apache$poi$hssf$record$LeftMarginRecord = clsClass$70;
        }
        clsArr[69] = clsClass$70;
        Class clsClass$71 = class$org$apache$poi$hssf$record$RightMarginRecord;
        if (clsClass$71 == null) {
            clsClass$71 = class$("org.apache.poi.hssf.record.RightMarginRecord");
            class$org$apache$poi$hssf$record$RightMarginRecord = clsClass$71;
        }
        clsArr[70] = clsClass$71;
        Class clsClass$72 = class$org$apache$poi$hssf$record$TopMarginRecord;
        if (clsClass$72 == null) {
            clsClass$72 = class$("org.apache.poi.hssf.record.TopMarginRecord");
            class$org$apache$poi$hssf$record$TopMarginRecord = clsClass$72;
        }
        clsArr[71] = clsClass$72;
        Class clsClass$73 = class$org$apache$poi$hssf$record$BottomMarginRecord;
        if (clsClass$73 == null) {
            clsClass$73 = class$("org.apache.poi.hssf.record.BottomMarginRecord");
            class$org$apache$poi$hssf$record$BottomMarginRecord = clsClass$73;
        }
        clsArr[72] = clsClass$73;
        Class clsClass$74 = class$org$apache$poi$hssf$record$DrawingRecord;
        if (clsClass$74 == null) {
            clsClass$74 = class$("org.apache.poi.hssf.record.DrawingRecord");
            class$org$apache$poi$hssf$record$DrawingRecord = clsClass$74;
        }
        clsArr[73] = clsClass$74;
        Class clsClass$75 = class$org$apache$poi$hssf$record$DrawingGroupRecord;
        if (clsClass$75 == null) {
            clsClass$75 = class$("org.apache.poi.hssf.record.DrawingGroupRecord");
            class$org$apache$poi$hssf$record$DrawingGroupRecord = clsClass$75;
        }
        clsArr[74] = clsClass$75;
        Class clsClass$76 = class$org$apache$poi$hssf$record$DrawingSelectionRecord;
        if (clsClass$76 == null) {
            clsClass$76 = class$("org.apache.poi.hssf.record.DrawingSelectionRecord");
            class$org$apache$poi$hssf$record$DrawingSelectionRecord = clsClass$76;
        }
        clsArr[75] = clsClass$76;
        Class clsClass$77 = class$org$apache$poi$hssf$record$ObjRecord;
        if (clsClass$77 == null) {
            clsClass$77 = class$("org.apache.poi.hssf.record.ObjRecord");
            class$org$apache$poi$hssf$record$ObjRecord = clsClass$77;
        }
        clsArr[76] = clsClass$77;
        Class clsClass$78 = class$org$apache$poi$hssf$record$TextObjectRecord;
        if (clsClass$78 == null) {
            clsClass$78 = class$("org.apache.poi.hssf.record.TextObjectRecord");
            class$org$apache$poi$hssf$record$TextObjectRecord = clsClass$78;
        }
        clsArr[77] = clsClass$78;
        Class clsClass$79 = class$org$apache$poi$hssf$record$PaletteRecord;
        if (clsClass$79 == null) {
            clsClass$79 = class$("org.apache.poi.hssf.record.PaletteRecord");
            class$org$apache$poi$hssf$record$PaletteRecord = clsClass$79;
        }
        clsArr[78] = clsClass$79;
        Class clsClass$80 = class$org$apache$poi$hssf$record$StringRecord;
        if (clsClass$80 == null) {
            clsClass$80 = class$("org.apache.poi.hssf.record.StringRecord");
            class$org$apache$poi$hssf$record$StringRecord = clsClass$80;
        }
        clsArr[79] = clsClass$80;
        Class clsClass$81 = class$org$apache$poi$hssf$record$RecalcIdRecord;
        if (clsClass$81 == null) {
            clsClass$81 = class$("org.apache.poi.hssf.record.RecalcIdRecord");
            class$org$apache$poi$hssf$record$RecalcIdRecord = clsClass$81;
        }
        clsArr[80] = clsClass$81;
        Class clsClass$82 = class$org$apache$poi$hssf$record$SharedFormulaRecord;
        if (clsClass$82 == null) {
            clsClass$82 = class$("org.apache.poi.hssf.record.SharedFormulaRecord");
            class$org$apache$poi$hssf$record$SharedFormulaRecord = clsClass$82;
        }
        clsArr[81] = clsClass$82;
        Class clsClass$83 = class$org$apache$poi$hssf$record$HorizontalPageBreakRecord;
        if (clsClass$83 == null) {
            clsClass$83 = class$("org.apache.poi.hssf.record.HorizontalPageBreakRecord");
            class$org$apache$poi$hssf$record$HorizontalPageBreakRecord = clsClass$83;
        }
        clsArr[82] = clsClass$83;
        Class clsClass$84 = class$org$apache$poi$hssf$record$VerticalPageBreakRecord;
        if (clsClass$84 == null) {
            clsClass$84 = class$("org.apache.poi.hssf.record.VerticalPageBreakRecord");
            class$org$apache$poi$hssf$record$VerticalPageBreakRecord = clsClass$84;
        }
        clsArr[83] = clsClass$84;
        records = clsArr;
        recordsMap = recordsToMap(clsArr);
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public static void setCapacity(int i) {
        NUM_RECORDS = i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static List createRecords(InputStream inputStream) throws RecordFormatException {
        short s;
        ArrayList arrayList = new ArrayList(NUM_RECORDS);
        Record record = null;
        do {
            try {
                s = LittleEndian.readShort(inputStream);
                if (s != 0) {
                    int i = LittleEndian.readShort(inputStream);
                    byte[] bArr = new byte[i];
                    inputStream.read(bArr);
                    Record[] recordArrCreateRecord = createRecord(s, i, bArr);
                    if (recordArrCreateRecord.length > 1) {
                        for (int i2 = 0; i2 < recordArrCreateRecord.length; i2++) {
                            arrayList.add(recordArrCreateRecord[i2]);
                            record = recordArrCreateRecord[i2];
                        }
                    } else {
                        Record record2 = recordArrCreateRecord[0];
                        if (record2 != null) {
                            if (s == 60 && !(record instanceof ContinueRecord) && !(record instanceof UnknownRecord)) {
                                if (record == null) {
                                    throw new RecordFormatException("First record is a ContinueRecord??");
                                }
                                record.processContinueRecord(bArr);
                            } else {
                                arrayList.add(record2);
                                record = record2;
                            }
                        }
                    }
                }
            } catch (IOException unused) {
                throw new RecordFormatException("Error reading bytes");
            }
        } while (s != 0);
        return arrayList;
    }

    public static Record[] createRecord(short s, short s2, byte[] bArr) {
        Record[] recordArr;
        try {
            Constructor constructor = (Constructor) recordsMap.get(new Short(s));
            Record unknownRecord = constructor != null ? (Record) constructor.newInstance(new Short(s), new Short(s2), bArr) : new UnknownRecord(s, s2, bArr);
            Record[] recordArr2 = null;
            if (unknownRecord instanceof RKRecord) {
                RKRecord rKRecord = (RKRecord) unknownRecord;
                NumberRecord numberRecord = new NumberRecord();
                numberRecord.setColumn(rKRecord.getColumn());
                numberRecord.setRow(rKRecord.getRow());
                numberRecord.setXFIndex(rKRecord.getXFIndex());
                numberRecord.setValue(rKRecord.getRKNumber());
                unknownRecord = numberRecord;
            } else if (unknownRecord instanceof DBCellRecord) {
                unknownRecord = null;
            } else {
                if (unknownRecord instanceof MulRKRecord) {
                    MulRKRecord mulRKRecord = (MulRKRecord) unknownRecord;
                    recordArr = new Record[mulRKRecord.getNumColumns()];
                    for (int i = 0; i < mulRKRecord.getNumColumns(); i++) {
                        NumberRecord numberRecord2 = new NumberRecord();
                        numberRecord2.setColumn((short) (mulRKRecord.getFirstColumn() + i));
                        numberRecord2.setRow(mulRKRecord.getRow());
                        numberRecord2.setXFIndex(mulRKRecord.getXFAt(i));
                        numberRecord2.setValue(mulRKRecord.getRKNumberAt(i));
                        recordArr[i] = numberRecord2;
                    }
                } else if (unknownRecord instanceof MulBlankRecord) {
                    MulBlankRecord mulBlankRecord = (MulBlankRecord) unknownRecord;
                    recordArr = new Record[mulBlankRecord.getNumColumns()];
                    for (int i2 = 0; i2 < mulBlankRecord.getNumColumns(); i2++) {
                        BlankRecord blankRecord = new BlankRecord();
                        blankRecord.setColumn((short) (mulBlankRecord.getFirstColumn() + i2));
                        blankRecord.setRow(mulBlankRecord.getRow());
                        blankRecord.setXFIndex(mulBlankRecord.getXFAt(i2));
                        recordArr[i2] = blankRecord;
                    }
                }
                recordArr2 = recordArr;
            }
            return recordArr2 == null ? new Record[]{unknownRecord} : recordArr2;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RecordFormatException(new StringBuffer().append("Unable to construct record instance, the following exception occured: ").append(e.getMessage()).toString());
        }
    }

    public static short[] getAllKnownRecordSIDs() {
        short[] sArr = new short[recordsMap.size()];
        Iterator it = recordsMap.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            sArr[i] = ((Short) it.next()).shortValue();
            i++;
        }
        return sArr;
    }

    private static Map recordsToMap(Class[] clsArr) {
        HashMap map = new HashMap();
        for (Class cls : clsArr) {
            try {
                short s = cls.getField("sid").getShort(null);
                Class<?>[] clsArr2 = new Class[3];
                clsArr2[0] = Short.TYPE;
                clsArr2[1] = Short.TYPE;
                Class<?> clsClass$ = array$B;
                if (clsClass$ == null) {
                    clsClass$ = class$("[B");
                    array$B = clsClass$;
                }
                clsArr2[2] = clsClass$;
                map.put(new Short(s), cls.getConstructor(clsArr2));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RecordFormatException("Unable to determine record types");
            }
        }
        return map;
    }
}
