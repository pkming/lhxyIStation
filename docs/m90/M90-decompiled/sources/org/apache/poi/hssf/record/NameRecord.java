package org.apache.poi.hssf.record;

import java.util.List;
import java.util.Stack;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.record.formula.UnaryMinusPtg;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.poi.hssf.util.RangeAddress;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class NameRecord extends Record {
    public static final byte BUILTIN_AUTO_ACTIVATE = 10;
    public static final byte BUILTIN_AUTO_CLOSE = 3;
    public static final byte BUILTIN_AUTO_DEACTIVATE = 11;
    public static final byte BUILTIN_AUTO_OPEN = 2;
    public static final byte BUILTIN_CONSOLIDATE_AREA = 1;
    public static final byte BUILTIN_CRITERIA = 5;
    public static final byte BUILTIN_DATABASE = 4;
    public static final byte BUILTIN_DATA_FORM = 9;
    public static final byte BUILTIN_PRINT_AREA = 6;
    public static final byte BUILTIN_PRINT_TITLE = 7;
    public static final byte BUILTIN_RECORDER = 8;
    public static final byte BUILTIN_SHEET_TITLE = 12;
    public static final short OPT_BINDATA = 4096;
    public static final short OPT_BUILTIN = 32;
    public static final short OPT_COMMAND_NAME = 4;
    public static final short OPT_COMPLEX = 16;
    public static final short OPT_FUNCTION_NAME = 2;
    public static final short OPT_HIDDEN_NAME = 1;
    public static final short OPT_MACRO = 8;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$formula$Area3DPtg = null;
    static /* synthetic */ Class class$org$apache$poi$hssf$record$formula$Ref3DPtg = null;
    public static final short sid = 24;
    private byte field_10_length_status_bar_text;
    private byte field_11_compressed_unicode_flag;
    private byte field_12_builtIn_name;
    private String field_12_name_text;
    private Stack field_13_name_definition;
    private byte[] field_13_raw_name_definition;
    private String field_14_custom_menu_text;
    private String field_15_description_text;
    private String field_16_help_topic_text;
    private String field_17_status_bar_text;
    private short field_1_option_flag;
    private byte field_2_keyboard_shortcut;
    private byte field_3_length_name_text;
    private short field_4_length_name_definition;
    private short field_5_index_to_sheet;
    private short field_6_equals_to_index_to_sheet;
    private byte field_7_length_custom_menu;
    private byte field_8_length_description_text;
    private byte field_9_length_help_topic_text;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 24;
    }

    protected String translateBuiltInName(byte b) {
        switch (b) {
            case 1:
                return "Consolidate_Area";
            case 2:
                return "Auto_Open";
            case 3:
                return "Auto_Close";
            case 4:
                return "Database";
            case 5:
                return "Criteria";
            case 6:
                return "Print_Area";
            case 7:
                return "Print_Titles";
            case 8:
                return "Recorder";
            case 9:
                return "Data_Form";
            case 10:
                return "Auto_Activate";
            case 11:
                return "Auto_Deactivate";
            case 12:
                return "Sheet_Title";
            default:
                return "Unknown";
        }
    }

    public NameRecord() {
        this.field_13_name_definition = new Stack();
        this.field_12_name_text = new String();
        this.field_14_custom_menu_text = new String();
        this.field_15_description_text = new String();
        this.field_16_help_topic_text = new String();
        this.field_17_status_bar_text = new String();
    }

    public NameRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public NameRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    public NameRecord(byte b, short s) {
        this();
        this.field_12_builtIn_name = b;
        setOptionFlag((short) (getOptionFlag() | 32));
        setNameTextLength((byte) 1);
        setEqualsToIndexToSheet(s);
        setCustomMenuLength((byte) 0);
        setDescriptionTextLength((byte) 0);
        setHelpTopicLength((byte) 0);
        setStatusBarLength((byte) 0);
    }

    public void setOptionFlag(short s) {
        this.field_1_option_flag = s;
    }

    public void setKeyboardShortcut(byte b) {
        this.field_2_keyboard_shortcut = b;
    }

    public void setNameTextLength(byte b) {
        this.field_3_length_name_text = b;
    }

    public void setDefinitionTextLength(short s) {
        this.field_4_length_name_definition = s;
    }

    public void setUnused(short s) {
        this.field_5_index_to_sheet = s;
    }

    public short getEqualsToIndexToSheet() {
        return this.field_6_equals_to_index_to_sheet;
    }

    public short getIndexToSheet() {
        return getEqualsToIndexToSheet();
    }

    public byte getFnGroup() {
        return (byte) ((this.field_1_option_flag & 4032) >> 4);
    }

    public void setEqualsToIndexToSheet(short s) {
        this.field_6_equals_to_index_to_sheet = s;
    }

    public void setCustomMenuLength(byte b) {
        this.field_7_length_custom_menu = b;
    }

    public void setDescriptionTextLength(byte b) {
        this.field_8_length_description_text = b;
    }

    public void setHelpTopicLength(byte b) {
        this.field_9_length_help_topic_text = b;
    }

    public void setStatusBarLength(byte b) {
        this.field_10_length_status_bar_text = b;
    }

    public void setCompressedUnicodeFlag(byte b) {
        this.field_11_compressed_unicode_flag = b;
    }

    public void setNameText(String str) {
        this.field_12_name_text = str;
    }

    public void setCustomMenuText(String str) {
        this.field_14_custom_menu_text = str;
    }

    public void setDescriptionText(String str) {
        this.field_15_description_text = str;
    }

    public void setHelpTopicText(String str) {
        this.field_16_help_topic_text = str;
    }

    public void setStatusBarText(String str) {
        this.field_17_status_bar_text = str;
    }

    public short getOptionFlag() {
        return this.field_1_option_flag;
    }

    public byte getKeyboardShortcut() {
        return this.field_2_keyboard_shortcut;
    }

    public byte getNameTextLength() {
        return this.field_3_length_name_text;
    }

    public short getDefinitionTextLength() {
        return this.field_4_length_name_definition;
    }

    public short getUnused() {
        return this.field_5_index_to_sheet;
    }

    public byte getCustomMenuLength() {
        return this.field_7_length_custom_menu;
    }

    public byte getDescriptionTextLength() {
        return this.field_8_length_description_text;
    }

    public byte getHelpTopicLength() {
        return this.field_9_length_help_topic_text;
    }

    public byte getStatusBarLength() {
        return this.field_10_length_status_bar_text;
    }

    public byte getCompressedUnicodeFlag() {
        return this.field_11_compressed_unicode_flag;
    }

    public boolean isHiddenName() {
        return (this.field_1_option_flag & 1) != 0;
    }

    public boolean isFunctionName() {
        return (this.field_1_option_flag & 2) != 0;
    }

    public boolean isCommandName() {
        return (this.field_1_option_flag & 4) != 0;
    }

    public boolean isMacro() {
        return (this.field_1_option_flag & 8) != 0;
    }

    public boolean isComplexFunction() {
        return (this.field_1_option_flag & 16) != 0;
    }

    public boolean isBuiltInName() {
        return (getOptionFlag() & 32) != 0;
    }

    public String getNameText() {
        return isBuiltInName() ? translateBuiltInName(getBuiltInName()) : this.field_12_name_text;
    }

    public byte getBuiltInName() {
        return this.field_12_builtIn_name;
    }

    public List getNameDefinition() {
        return this.field_13_name_definition;
    }

    public void setNameDefinition(Stack stack) {
        this.field_13_name_definition = stack;
    }

    public String getCustomMenuText() {
        return this.field_14_custom_menu_text;
    }

    public String getDescriptionText() {
        return this.field_15_description_text;
    }

    public String getHelpTopicText() {
        return this.field_16_help_topic_text;
    }

    public String getStatusBarText() {
        return this.field_17_status_bar_text;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 24) {
            throw new RecordFormatException("NOT A valid Name RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 24);
        LittleEndian.putShort(bArr, i + 4, getOptionFlag());
        bArr[i + 6] = getKeyboardShortcut();
        bArr[i + 7] = getNameTextLength();
        LittleEndian.putShort(bArr, i + 8, getDefinitionTextLength());
        LittleEndian.putShort(bArr, i + 10, getUnused());
        LittleEndian.putShort(bArr, i + 12, getEqualsToIndexToSheet());
        bArr[i + 14] = getCustomMenuLength();
        bArr[i + 15] = getDescriptionTextLength();
        bArr[i + 16] = getHelpTopicLength();
        bArr[i + 17] = getStatusBarLength();
        bArr[i + 18] = getCompressedUnicodeFlag();
        LittleEndian.putShort(bArr, i + 2, (short) (getTextsLength() + 15));
        int i2 = this.field_3_length_name_text + UnaryMinusPtg.sid;
        if (isBuiltInName()) {
            bArr[i + 19] = getBuiltInName();
        } else {
            StringUtil.putCompressedUnicode(getNameText(), bArr, i + 19);
        }
        if (this.field_13_name_definition != null) {
            serializePtgs(bArr, i2 + i);
        } else {
            byte[] bArr2 = this.field_13_raw_name_definition;
            System.arraycopy(bArr2, 0, bArr, i2 + i, bArr2.length);
        }
        int i3 = i2 + this.field_4_length_name_definition;
        StringUtil.putCompressedUnicode(getCustomMenuText(), bArr, i3 + i);
        int i4 = i3 + this.field_7_length_custom_menu;
        StringUtil.putCompressedUnicode(getDescriptionText(), bArr, i4 + i);
        int i5 = i4 + this.field_8_length_description_text;
        StringUtil.putCompressedUnicode(getHelpTopicText(), bArr, i5 + i);
        StringUtil.putCompressedUnicode(getStatusBarText(), bArr, i5 + this.field_9_length_help_topic_text + i);
        return getRecordSize();
    }

    private void serializePtgs(byte[] bArr, int i) {
        for (int i2 = 0; i2 < this.field_13_name_definition.size(); i2++) {
            Ptg ptg = (Ptg) this.field_13_name_definition.get(i2);
            ptg.writeBytes(bArr, i);
            i += ptg.getSize();
        }
    }

    public int getTextsLength() {
        return getNameTextLength() + getDefinitionTextLength() + getDescriptionTextLength() + getHelpTopicLength() + getStatusBarLength();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return getTextsLength() + 19;
    }

    public short getExternSheetNumber() {
        Stack stack = this.field_13_name_definition;
        if (stack == null) {
            return (short) 0;
        }
        Ptg ptg = (Ptg) stack.peek();
        Class<?> cls = ptg.getClass();
        Class<?> clsClass$ = class$org$apache$poi$hssf$record$formula$Area3DPtg;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.hssf.record.formula.Area3DPtg");
            class$org$apache$poi$hssf$record$formula$Area3DPtg = clsClass$;
        }
        if (cls == clsClass$) {
            return ((Area3DPtg) ptg).getExternSheetIndex();
        }
        Class<?> cls2 = ptg.getClass();
        Class<?> clsClass$2 = class$org$apache$poi$hssf$record$formula$Ref3DPtg;
        if (clsClass$2 == null) {
            clsClass$2 = class$("org.apache.poi.hssf.record.formula.Ref3DPtg");
            class$org$apache$poi$hssf$record$formula$Ref3DPtg = clsClass$2;
        }
        if (cls2 == clsClass$2) {
            return ((Ref3DPtg) ptg).getExternSheetIndex();
        }
        return (short) 0;
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public void setExternSheetNumber(short s) {
        Ptg ptgCreateNewPtg;
        Stack stack = this.field_13_name_definition;
        if (stack == null || stack.isEmpty()) {
            this.field_13_name_definition = new Stack();
            ptgCreateNewPtg = createNewPtg();
        } else {
            ptgCreateNewPtg = (Ptg) this.field_13_name_definition.peek();
        }
        Class<?> cls = ptgCreateNewPtg.getClass();
        Class<?> clsClass$ = class$org$apache$poi$hssf$record$formula$Area3DPtg;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.hssf.record.formula.Area3DPtg");
            class$org$apache$poi$hssf$record$formula$Area3DPtg = clsClass$;
        }
        if (cls == clsClass$) {
            ((Area3DPtg) ptgCreateNewPtg).setExternSheetIndex(s);
            return;
        }
        Class<?> cls2 = ptgCreateNewPtg.getClass();
        Class<?> clsClass$2 = class$org$apache$poi$hssf$record$formula$Ref3DPtg;
        if (clsClass$2 == null) {
            clsClass$2 = class$("org.apache.poi.hssf.record.formula.Ref3DPtg");
            class$org$apache$poi$hssf$record$formula$Ref3DPtg = clsClass$2;
        }
        if (cls2 == clsClass$2) {
            ((Ref3DPtg) ptgCreateNewPtg).setExternSheetIndex(s);
        }
    }

    private Ptg createNewPtg() {
        Area3DPtg area3DPtg = new Area3DPtg();
        this.field_13_name_definition.push(area3DPtg);
        return area3DPtg;
    }

    public String getAreaReference(Workbook workbook) {
        Stack stack = this.field_13_name_definition;
        if (stack == null) {
            return "#REF!";
        }
        Ptg ptg = (Ptg) stack.peek();
        Class<?> cls = ptg.getClass();
        Class<?> clsClass$ = class$org$apache$poi$hssf$record$formula$Area3DPtg;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.hssf.record.formula.Area3DPtg");
            class$org$apache$poi$hssf$record$formula$Area3DPtg = clsClass$;
        }
        if (cls == clsClass$) {
            return ptg.toFormulaString(workbook);
        }
        Class<?> cls2 = ptg.getClass();
        Class<?> clsClass$2 = class$org$apache$poi$hssf$record$formula$Ref3DPtg;
        if (clsClass$2 == null) {
            clsClass$2 = class$("org.apache.poi.hssf.record.formula.Ref3DPtg");
            class$org$apache$poi$hssf$record$formula$Ref3DPtg = clsClass$2;
        }
        return cls2 == clsClass$2 ? ptg.toFormulaString(workbook) : "";
    }

    public void setAreaReference(String str) {
        Ptg ptgCreateNewPtg;
        Ptg ref3DPtg;
        RangeAddress rangeAddress = new RangeAddress(str);
        Stack stack = this.field_13_name_definition;
        if (stack == null || stack.isEmpty()) {
            this.field_13_name_definition = new Stack();
            ptgCreateNewPtg = createNewPtg();
        } else {
            ptgCreateNewPtg = (Ptg) this.field_13_name_definition.pop();
        }
        short externSheetIndex = 0;
        Class<?> cls = ptgCreateNewPtg.getClass();
        Class<?> clsClass$ = class$org$apache$poi$hssf$record$formula$Area3DPtg;
        if (clsClass$ == null) {
            clsClass$ = class$("org.apache.poi.hssf.record.formula.Area3DPtg");
            class$org$apache$poi$hssf$record$formula$Area3DPtg = clsClass$;
        }
        if (cls == clsClass$) {
            externSheetIndex = ((Area3DPtg) ptgCreateNewPtg).getExternSheetIndex();
        } else {
            Class<?> cls2 = ptgCreateNewPtg.getClass();
            Class<?> clsClass$2 = class$org$apache$poi$hssf$record$formula$Ref3DPtg;
            if (clsClass$2 == null) {
                clsClass$2 = class$("org.apache.poi.hssf.record.formula.Ref3DPtg");
                class$org$apache$poi$hssf$record$formula$Ref3DPtg = clsClass$2;
            }
            if (cls2 == clsClass$2) {
                externSheetIndex = ((Ref3DPtg) ptgCreateNewPtg).getExternSheetIndex();
            }
        }
        if (rangeAddress.hasRange()) {
            ref3DPtg = new Area3DPtg();
            Area3DPtg area3DPtg = (Area3DPtg) ref3DPtg;
            area3DPtg.setExternSheetIndex(externSheetIndex);
            area3DPtg.setArea(str);
            setDefinitionTextLength((short) ref3DPtg.getSize());
        } else {
            ref3DPtg = new Ref3DPtg();
            Ref3DPtg ref3DPtg2 = (Ref3DPtg) ref3DPtg;
            ref3DPtg2.setExternSheetIndex(externSheetIndex);
            ref3DPtg2.setArea(str);
            setDefinitionTextLength((short) ref3DPtg.getSize());
        }
        this.field_13_name_definition.push(ref3DPtg);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_option_flag = LittleEndian.getShort(bArr, i + 0);
        this.field_2_keyboard_shortcut = bArr[i + 2];
        this.field_3_length_name_text = bArr[i + 3];
        this.field_4_length_name_definition = LittleEndian.getShort(bArr, i + 4);
        this.field_5_index_to_sheet = LittleEndian.getShort(bArr, i + 6);
        this.field_6_equals_to_index_to_sheet = LittleEndian.getShort(bArr, i + 8);
        this.field_7_length_custom_menu = bArr[i + 10];
        this.field_8_length_description_text = bArr[i + 11];
        this.field_9_length_help_topic_text = bArr[i + 12];
        this.field_10_length_status_bar_text = bArr[i + 13];
        this.field_11_compressed_unicode_flag = bArr[i + 14];
        if (isBuiltInName()) {
            this.field_12_builtIn_name = bArr[i + 15];
        }
        this.field_12_name_text = StringUtil.getFromCompressedUnicode(bArr, i + 15, LittleEndian.ubyteToInt(this.field_3_length_name_text));
        int i2 = this.field_3_length_name_text + HSSFErrorConstants.ERROR_VALUE;
        this.field_13_name_definition = getParsedExpressionTokens(bArr, this.field_4_length_name_definition, i, i2);
        int i3 = i2 + this.field_4_length_name_definition;
        this.field_14_custom_menu_text = StringUtil.getFromCompressedUnicode(bArr, i3 + i, LittleEndian.ubyteToInt(this.field_7_length_custom_menu));
        int i4 = i3 + this.field_7_length_custom_menu;
        this.field_15_description_text = StringUtil.getFromCompressedUnicode(bArr, i4 + i, LittleEndian.ubyteToInt(this.field_8_length_description_text));
        int i5 = i4 + this.field_8_length_description_text;
        this.field_16_help_topic_text = StringUtil.getFromCompressedUnicode(bArr, i5 + i, LittleEndian.ubyteToInt(this.field_9_length_help_topic_text));
        this.field_17_status_bar_text = StringUtil.getFromCompressedUnicode(bArr, i5 + this.field_9_length_help_topic_text + i, LittleEndian.ubyteToInt(this.field_10_length_status_bar_text));
    }

    private Stack getParsedExpressionTokens(byte[] bArr, short s, int i, int i2) {
        Stack stack = new Stack();
        int size = i2 + i;
        int size2 = 0;
        while (size2 < s) {
            try {
                Ptg ptgCreatePtg = Ptg.createPtg(bArr, size);
                size += ptgCreatePtg.getSize();
                size2 += ptgCreatePtg.getSize();
                stack.push(ptgCreatePtg);
                byte[] bArr2 = new byte[s];
                this.field_13_raw_name_definition = bArr2;
                System.arraycopy(bArr, i, bArr2, 0, s);
            } catch (UnsupportedOperationException e) {
                System.err.println(new StringBuffer().append("[WARNING] Unknown Ptg ").append(e.getMessage()).toString());
                byte[] bArr3 = new byte[s];
                this.field_13_raw_name_definition = bArr3;
                System.arraycopy(bArr, i, bArr3, 0, s);
                return null;
            }
        }
        return stack;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[NAME]\n");
        stringBuffer.append("    .option flags         = ").append(HexDump.toHex(this.field_1_option_flag)).append("\n");
        stringBuffer.append("    .keyboard shortcut    = ").append(HexDump.toHex(this.field_2_keyboard_shortcut)).append("\n");
        stringBuffer.append("    .length of the name   = ").append((int) this.field_3_length_name_text).append("\n");
        stringBuffer.append("    .size of the formula data = ").append((int) this.field_4_length_name_definition).append("\n");
        stringBuffer.append("    .unused                   = ").append((int) this.field_5_index_to_sheet).append("\n");
        stringBuffer.append("    .index to sheet (1-based, 0=Global)           = ").append((int) this.field_6_equals_to_index_to_sheet).append("\n");
        stringBuffer.append("    .Length of menu text (character count)        = ").append((int) this.field_7_length_custom_menu).append("\n");
        stringBuffer.append("    .Length of description text (character count) = ").append((int) this.field_8_length_description_text).append("\n");
        stringBuffer.append("    .Length of help topic text (character count)  = ").append((int) this.field_9_length_help_topic_text).append("\n");
        stringBuffer.append("    .Length of status bar text (character count)  = ").append((int) this.field_10_length_status_bar_text).append("\n");
        stringBuffer.append("    .Name (Unicode flag)  = ").append((int) this.field_11_compressed_unicode_flag).append("\n");
        stringBuffer.append("    .Name (Unicode text)  = ").append(getNameText()).append("\n");
        StringBuffer stringBufferAppend = stringBuffer.append("    .Formula data (RPN token array without size field)      = ");
        byte[] bArr = this.field_13_raw_name_definition;
        if (bArr == null) {
            bArr = new byte[0];
        }
        stringBufferAppend.append(HexDump.toHex(bArr)).append("\n");
        stringBuffer.append("    .Menu text (Unicode string without length field)        = ").append(this.field_14_custom_menu_text).append("\n");
        stringBuffer.append("    .Description text (Unicode string without length field) = ").append(this.field_15_description_text).append("\n");
        stringBuffer.append("    .Help topic text (Unicode string without length field)  = ").append(this.field_16_help_topic_text).append("\n");
        stringBuffer.append("    .Status bar text (Unicode string without length field)  = ").append(this.field_17_status_bar_text).append("\n");
        byte[] bArr2 = this.field_13_raw_name_definition;
        if (bArr2 != null) {
            stringBuffer.append(HexDump.dump(bArr2, 0L, 0));
        }
        stringBuffer.append("[/NAME]\n");
        return stringBuffer.toString();
    }
}
