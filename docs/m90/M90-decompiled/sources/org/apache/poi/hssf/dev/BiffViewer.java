package org.apache.poi.hssf.dev;

import android.bluetooth.BluetoothClass;
import android.hardware.Camera;
import com.unisound.client.SpeechConstants;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.nntp.NNTPReply;
import org.apache.commons.net.telnet.TelnetCommand;
import org.apache.poi.hssf.record.AreaFormatRecord;
import org.apache.poi.hssf.record.AreaRecord;
import org.apache.poi.hssf.record.AxisLineFormatRecord;
import org.apache.poi.hssf.record.AxisOptionsRecord;
import org.apache.poi.hssf.record.AxisParentRecord;
import org.apache.poi.hssf.record.AxisRecord;
import org.apache.poi.hssf.record.AxisUsedRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BackupRecord;
import org.apache.poi.hssf.record.BarRecord;
import org.apache.poi.hssf.record.BeginRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BookBoolRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BottomMarginRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CalcCountRecord;
import org.apache.poi.hssf.record.CalcModeRecord;
import org.apache.poi.hssf.record.CategorySeriesAxisRecord;
import org.apache.poi.hssf.record.ChartFormatRecord;
import org.apache.poi.hssf.record.ChartRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.ColumnInfoRecord;
import org.apache.poi.hssf.record.ContinueRecord;
import org.apache.poi.hssf.record.CountryRecord;
import org.apache.poi.hssf.record.DBCellRecord;
import org.apache.poi.hssf.record.DSFRecord;
import org.apache.poi.hssf.record.DatRecord;
import org.apache.poi.hssf.record.DataFormatRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.DefaultColWidthRecord;
import org.apache.poi.hssf.record.DefaultDataLabelTextPropertiesRecord;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.hssf.record.DeltaRecord;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.DrawingRecordForBiffViewer;
import org.apache.poi.hssf.record.DrawingSelectionRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.EndRecord;
import org.apache.poi.hssf.record.ExtSSTRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.ExternSheetRecord;
import org.apache.poi.hssf.record.FnGroupCountRecord;
import org.apache.poi.hssf.record.FontBasisRecord;
import org.apache.poi.hssf.record.FontIndexRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.FrameRecord;
import org.apache.poi.hssf.record.GridsetRecord;
import org.apache.poi.hssf.record.GutsRecord;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.HideObjRecord;
import org.apache.poi.hssf.record.HorizontalPageBreakRecord;
import org.apache.poi.hssf.record.IndexRecord;
import org.apache.poi.hssf.record.InterfaceEndRecord;
import org.apache.poi.hssf.record.InterfaceHdrRecord;
import org.apache.poi.hssf.record.IterationRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.LeftMarginRecord;
import org.apache.poi.hssf.record.LegendRecord;
import org.apache.poi.hssf.record.LineFormatRecord;
import org.apache.poi.hssf.record.LinkedDataRecord;
import org.apache.poi.hssf.record.MMSRecord;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.record.MulRKRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.ObjectLinkRecord;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.PaneRecord;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.PasswordRev4Record;
import org.apache.poi.hssf.record.PlotAreaRecord;
import org.apache.poi.hssf.record.PlotGrowthRecord;
import org.apache.poi.hssf.record.PrecisionRecord;
import org.apache.poi.hssf.record.PrintGridlinesRecord;
import org.apache.poi.hssf.record.PrintHeadersRecord;
import org.apache.poi.hssf.record.PrintSetupRecord;
import org.apache.poi.hssf.record.ProtectRecord;
import org.apache.poi.hssf.record.ProtectionRev4Record;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.hssf.record.RefModeRecord;
import org.apache.poi.hssf.record.RefreshAllRecord;
import org.apache.poi.hssf.record.RightMarginRecord;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.SaveRecalcRecord;
import org.apache.poi.hssf.record.SelectionRecord;
import org.apache.poi.hssf.record.SeriesIndexRecord;
import org.apache.poi.hssf.record.SeriesListRecord;
import org.apache.poi.hssf.record.SeriesRecord;
import org.apache.poi.hssf.record.SeriesTextRecord;
import org.apache.poi.hssf.record.SeriesToChartGroupRecord;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.SheetPropertiesRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.record.SupBookRecord;
import org.apache.poi.hssf.record.TabIdRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.record.TextRecord;
import org.apache.poi.hssf.record.TickRecord;
import org.apache.poi.hssf.record.TopMarginRecord;
import org.apache.poi.hssf.record.UnitsRecord;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.record.UseSelFSRecord;
import org.apache.poi.hssf.record.VCenterRecord;
import org.apache.poi.hssf.record.ValueRangeRecord;
import org.apache.poi.hssf.record.VerticalPageBreakRecord;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.hssf.record.WindowOneRecord;
import org.apache.poi.hssf.record.WindowProtectRecord;
import org.apache.poi.hssf.record.WindowTwoRecord;
import org.apache.poi.hssf.record.WriteAccessRecord;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class BiffViewer {
    private boolean dump;
    String filename;

    public BiffViewer(String[] strArr) {
        if (strArr.length > 0) {
            this.filename = strArr[0];
        } else {
            System.out.println("BIFFVIEWER REQUIRES A FILENAME***");
        }
    }

    public void run() {
        try {
            createRecords(new POIFSFileSystem(new FileInputStream(this.filename)).createDocumentInputStream("Workbook"), this.dump);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static Record[] createRecords(InputStream inputStream, boolean z) throws RecordFormatException {
        short s;
        ArrayList arrayList = new ArrayList();
        RecordDetails recordDetails = null;
        int i = 0;
        do {
            try {
                s = LittleEndian.readShort(inputStream);
                int i2 = i + 2;
                if (s != 0) {
                    int i3 = LittleEndian.readShort(inputStream);
                    byte[] bArr = new byte[i3];
                    inputStream.read(bArr);
                    int i4 = i2 + 2 + i3;
                    Record recordCreateRecord = createRecord(s, i3, bArr);
                    if (recordCreateRecord.getSid() != 60) {
                        arrayList.add(recordCreateRecord);
                        if (recordDetails != null) {
                            recordDetails.dump();
                        }
                        recordDetails = new RecordDetails(s, i3, i, bArr, recordCreateRecord);
                    } else {
                        recordDetails.getRecord().processContinueRecord(bArr);
                    }
                    if (z) {
                        dumpRaw(s, i3, bArr);
                    }
                    i = i4;
                } else {
                    i = i2;
                }
            } catch (IOException unused) {
                throw new RecordFormatException("Error reading bytes");
            }
        } while (s != 0);
        recordDetails.dump();
        return (Record[]) arrayList.toArray(new Record[arrayList.size()]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void dumpNormal(Record record, int i, short s, short s2) {
        System.out.println(new StringBuffer().append("Offset 0x").append(Integer.toHexString(i)).append(" (").append(i).append(")").toString());
        System.out.println(new StringBuffer().append("recordid = 0x").append(Integer.toHexString(s)).append(", size = ").append((int) s2).toString());
        System.out.println(record.toString());
    }

    private static void dumpContinueRecord(Record record, boolean z, byte[] bArr) throws IOException {
        if (record == null) {
            throw new RecordFormatException("First record is a ContinueRecord??");
        }
        if (z) {
            System.out.println("-----PRECONTINUED LAST RECORD WOULD SERIALIZE LIKE:");
            if (record.serialize() != null) {
                HexDump.dump(record.serialize(), 0L, System.out, 0);
            }
            System.out.println();
            System.out.println("-----PRECONTINUED----------------------------------");
        }
        record.processContinueRecord(bArr);
        if (z) {
            System.out.println("-----CONTINUED LAST RECORD WOULD SERIALIZE LIKE:");
            HexDump.dump(record.serialize(), 0L, System.out, 0);
            System.out.println();
            System.out.println("-----CONTINUED----------------------------------");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void dumpUnknownRecord(byte[] bArr) throws IOException {
        System.out.println("-----UNKNOWN----------------------------------");
        if (bArr.length > 0) {
            HexDump.dump(bArr, 0L, System.out, 0);
        } else {
            System.out.print("**NO RECORD DATA**");
        }
        System.out.println();
        System.out.println("-----UNKNOWN----------------------------------");
    }

    private static void dumpRaw(short s, short s2, byte[] bArr) throws IOException {
        System.out.println("============================================");
        System.out.print(new StringBuffer().append("rectype = 0x").append(Integer.toHexString(s)).toString());
        System.out.println(new StringBuffer().append(", recsize = 0x").append(Integer.toHexString(s2)).toString());
        System.out.println("-BEGIN DUMP---------------------------------");
        if (bArr.length > 0) {
            HexDump.dump(bArr, 0L, System.out, 0);
        } else {
            System.out.println("**NO RECORD DATA**");
        }
        System.out.println("-END DUMP-----------------------------------");
    }

    private static Record createRecord(short s, short s2, byte[] bArr) {
        if (s == 60) {
            return new ContinueRecord(s, s2, bArr);
        }
        if (s == 61) {
            return new WindowOneRecord(s, s2, bArr);
        }
        if (s == 92) {
            return new WriteAccessRecord(s, s2, bArr);
        }
        if (s == 93) {
            return new ObjRecord(s, s2, bArr);
        }
        if (s == 140) {
            return new CountryRecord(s, s2, bArr);
        }
        if (s == 141) {
            return new HideObjRecord(s, s2, bArr);
        }
        if (s == 160) {
            return new SCLRecord(s, s2, bArr);
        }
        if (s == 161) {
            return new PrintSetupRecord(s, s2, bArr);
        }
        if (s == 189) {
            return new MulRKRecord(s, s2, bArr);
        }
        if (s == 190) {
            return new MulBlankRecord(s, s2, bArr);
        }
        switch (s) {
            case 6:
                return new FormulaRecord(s, s2, bArr);
            case 10:
                return new EOFRecord(s, s2, bArr);
            case 12:
                return new CalcCountRecord(s, s2, bArr);
            case 13:
                return new CalcModeRecord(s, s2, bArr);
            case 14:
                return new PrecisionRecord(s, s2, bArr);
            case 15:
                return new RefModeRecord(s, s2, bArr);
            case 16:
                return new DeltaRecord(s, s2, bArr);
            case 17:
                return new IterationRecord(s, s2, bArr);
            case 18:
                return new ProtectRecord(s, s2, bArr);
            case 19:
                return new PasswordRecord(s, s2, bArr);
            case 20:
                return new HeaderRecord(s, s2, bArr);
            case 21:
                return new FooterRecord(s, s2, bArr);
            case 29:
                return new SelectionRecord(s, s2, bArr);
            case 34:
                return new DateWindow1904Record(s, s2, bArr);
            case 49:
                return new FontRecord(s, s2, bArr);
            case 85:
                return new DefaultColWidthRecord(s, s2, bArr);
            case 95:
                return new SaveRecalcRecord(s, s2, bArr);
            case 125:
                return new ColumnInfoRecord(s, s2, bArr);
            case 146:
                return new PaletteRecord(s, s2, bArr);
            case 156:
                return new FnGroupCountRecord(s, s2, bArr);
            case 193:
                return new MMSRecord(s, s2, bArr);
            case 215:
                return new DBCellRecord(s, s2, bArr);
            case 218:
                return new BookBoolRecord(s, s2, bArr);
            case 224:
                return new ExtendedFormatRecord(s, s2, bArr);
            case 225:
                return new InterfaceHdrRecord(s, s2, bArr);
            case 226:
                return new InterfaceEndRecord(s, s2, bArr);
            case FTPReply.ENTERING_EPSV_MODE /* 229 */:
                return new MergeCellsRecord(s, s2, bArr);
            case 235:
                return new DrawingGroupRecord(s, s2, bArr);
            case TelnetCommand.EOF /* 236 */:
                return new DrawingRecordForBiffViewer(s, s2, bArr);
            case TelnetCommand.SUSP /* 237 */:
                return new DrawingSelectionRecord(s, s2, bArr);
            case 252:
                return new SSTRecord(s, s2, bArr);
            case TelnetCommand.DO /* 253 */:
                return new LabelSSTRecord(s, s2, bArr);
            case 255:
                return new ExtSSTRecord(s, s2, bArr);
            case 317:
                return new TabIdRecord(s, s2, bArr);
            case 352:
                return new UseSelFSRecord(s, s2, bArr);
            case 353:
                return new DSFRecord(s, s2, bArr);
            case NNTPReply.NO_SUCH_ARTICLE_FOUND /* 430 */:
                return new SupBookRecord(s, s2, bArr);
            case FTPReply.UNAVAILABLE_RESOURCE /* 431 */:
                return new ProtectionRev4Record(s, s2, bArr);
            case 438:
                return new TextObjectRecord(s, s2, bArr);
            case 439:
                return new RefreshAllRecord(s, s2, bArr);
            case 444:
                return new PasswordRev4Record(s, s2, bArr);
            case 512:
                return new DimensionsRecord(s, s2, bArr);
            case 513:
                return new BlankRecord(s, s2, bArr);
            case 515:
                return new NumberRecord(s, s2, bArr);
            case 516:
                return new LabelRecord(s, s2, bArr);
            case 517:
                return new BoolErrRecord(s, s2, bArr);
            case 519:
                return new StringRecord(s, s2, bArr);
            case BluetoothClass.Device.PHONE_CORDLESS /* 520 */:
                return new RowRecord(s, s2, bArr);
            case 523:
                return new IndexRecord(s, s2, bArr);
            case 549:
                return new DefaultRowHeightRecord(s, s2, bArr);
            case 574:
                return new WindowTwoRecord(s, s2, bArr);
            case 638:
                return new RKRecord(s, s2, bArr);
            case 659:
                return new StyleRecord(s, s2, bArr);
            case SpeechConstants.ASR_OPT_PRINT_LOG /* 1054 */:
                return new FormatRecord(s, s2, bArr);
            case 1212:
                return new SharedFormulaRecord(s, s2, bArr);
            case 2057:
                return new BOFRecord(s, s2, bArr);
            case 4097:
                return new UnitsRecord(s, s2, bArr);
            case 4098:
                return new ChartRecord(s, s2, bArr);
            case 4099:
                return new SeriesRecord(s, s2, bArr);
            case SpeechConstants.VPR_EVENT_RECORDING_STOP /* 4102 */:
                return new DataFormatRecord(s, s2, bArr);
            case SpeechConstants.VPR_EVENT_VOLUME_UPDATED /* 4103 */:
                return new LineFormatRecord(s, s2, bArr);
            case SpeechConstants.VPR_EVENT_SPEECH_START /* 4106 */:
                return new AreaFormatRecord(s, s2, bArr);
            case 4109:
                return new SeriesTextRecord(s, s2, bArr);
            case 4116:
                return new ChartFormatRecord(s, s2, bArr);
            case 4117:
                return new LegendRecord(s, s2, bArr);
            case 4118:
                return new SeriesListRecord(s, s2, bArr);
            case 4119:
                return new BarRecord(s, s2, bArr);
            case 4122:
                return new AreaRecord(s, s2, bArr);
            case 4125:
                return new AxisRecord(s, s2, bArr);
            case 4126:
                return new TickRecord(s, s2, bArr);
            case 4127:
                return new ValueRangeRecord(s, s2, bArr);
            case 4128:
                return new CategorySeriesAxisRecord(s, s2, bArr);
            case 4129:
                return new AxisLineFormatRecord(s, s2, bArr);
            case 4132:
                return new DefaultDataLabelTextPropertiesRecord(s, s2, bArr);
            case 4133:
                return new TextRecord(s, s2, bArr);
            case 4134:
                return new FontIndexRecord(s, s2, bArr);
            case 4135:
                return new ObjectLinkRecord(s, s2, bArr);
            case 4146:
                return new FrameRecord(s, s2, bArr);
            case 4147:
                return new BeginRecord(s, s2, bArr);
            case 4148:
                return new EndRecord(s, s2, bArr);
            case 4149:
                return new PlotAreaRecord(s, s2, bArr);
            case 4161:
                return new AxisParentRecord(s, s2, bArr);
            case 4164:
                return new SheetPropertiesRecord(s, s2, bArr);
            case 4165:
                return new SeriesToChartGroupRecord(s, s2, bArr);
            case 4166:
                return new AxisUsedRecord(s, s2, bArr);
            case 4177:
                return new LinkedDataRecord(s, s2, bArr);
            case 4192:
                return new FontBasisRecord(s, s2, bArr);
            case 4194:
                return new AxisOptionsRecord(s, s2, bArr);
            case 4195:
                return new DatRecord(s, s2, bArr);
            case 4196:
                return new PlotGrowthRecord(s, s2, bArr);
            case 4197:
                return new SeriesIndexRecord(s, s2, bArr);
            default:
                switch (s) {
                    case 23:
                        return new ExternSheetRecord(s, s2, bArr);
                    case 24:
                        return new NameRecord(s, s2, bArr);
                    case 25:
                        return new WindowProtectRecord(s, s2, bArr);
                    case 26:
                        return new VerticalPageBreakRecord(s, s2, bArr);
                    case 27:
                        return new HorizontalPageBreakRecord(s, s2, bArr);
                    default:
                        switch (s) {
                            case 38:
                                return new LeftMarginRecord(s, s2, bArr);
                            case 39:
                                return new RightMarginRecord(s, s2, bArr);
                            case 40:
                                return new TopMarginRecord(s, s2, bArr);
                            case 41:
                                return new BottomMarginRecord(s, s2, bArr);
                            case 42:
                                return new PrintHeadersRecord(s, s2, bArr);
                            case 43:
                                return new PrintGridlinesRecord(s, s2, bArr);
                            default:
                                switch (s) {
                                    case 64:
                                        return new BackupRecord(s, s2, bArr);
                                    case 65:
                                        return new PaneRecord(s, s2, bArr);
                                    case 66:
                                        return new CodepageRecord(s, s2, bArr);
                                    default:
                                        switch (s) {
                                            case 128:
                                                return new GutsRecord(s, s2, bArr);
                                            case 129:
                                                return new WSBoolRecord(s, s2, bArr);
                                            case 130:
                                                return new GridsetRecord(s, s2, bArr);
                                            case 131:
                                                return new HCenterRecord(s, s2, bArr);
                                            case 132:
                                                return new VCenterRecord(s, s2, bArr);
                                            case 133:
                                                return new BoundSheetRecord(s, s2, bArr);
                                            default:
                                                return new UnknownRecord(s, s2, bArr);
                                        }
                                }
                        }
                }
        }
    }

    public void setDump(boolean z) {
        this.dump = z;
    }

    public static void main(String[] strArr) {
        try {
            System.setProperty("poi.deserialize.escher", "true");
            BiffViewer biffViewer = new BiffViewer(strArr);
            if (strArr.length > 1 && strArr[1].equals(Camera.Parameters.FLASH_MODE_ON)) {
                biffViewer.setDump(true);
            }
            if (strArr.length > 1 && strArr[1].equals("bfd")) {
                DocumentInputStream documentInputStreamCreateDocumentInputStream = new POIFSFileSystem(new FileInputStream(strArr[0])).createDocumentInputStream("Workbook");
                byte[] bArr = new byte[documentInputStreamCreateDocumentInputStream.available()];
                documentInputStreamCreateDocumentInputStream.read(bArr);
                HexDump.dump(bArr, 0L, System.out, 0);
                return;
            }
            biffViewer.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class RecordDetails {
        byte[] data;
        Record record;
        short recsize;
        short rectype;
        int startloc;

        public RecordDetails(short s, short s2, int i, byte[] bArr, Record record) {
            this.rectype = s;
            this.recsize = s2;
            this.startloc = i;
            this.data = bArr;
            this.record = record;
        }

        public short getRectype() {
            return this.rectype;
        }

        public short getRecsize() {
            return this.recsize;
        }

        public byte[] getData() {
            return this.data;
        }

        public Record getRecord() {
            return this.record;
        }

        public void dump() throws IOException {
            Record record = this.record;
            if (record instanceof UnknownRecord) {
                BiffViewer.dumpUnknownRecord(this.data);
            } else {
                BiffViewer.dumpNormal(record, this.startloc, this.rectype, this.recsize);
            }
        }
    }
}
