package de.innosystec.unrar.unpack.vm;

import android.os.Trace;
import de.innosystec.unrar.crc.RarCRC;
import de.innosystec.unrar.io.Raw;
import java.util.List;
import java.util.Vector;
import org.apache.poi.hssf.record.PaletteRecord;

/* JADX INFO: loaded from: classes2.dex */
public class RarVM extends BitInput {
    private static /* synthetic */ int[] $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMCommands = null;
    private static /* synthetic */ int[] $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMStandardFilters = null;
    private static final long UINT_MASK = -1;
    public static final int VM_FIXEDGLOBALSIZE = 64;
    public static final int VM_GLOBALMEMADDR = 245760;
    public static final int VM_GLOBALMEMSIZE = 8192;
    public static final int VM_MEMMASK = 262143;
    public static final int VM_MEMSIZE = 262144;
    private static final int regCount = 8;
    private int IP;
    private int codeSize;
    private int flags;
    private int[] R = new int[8];
    private int maxOpCount = 25000000;
    private byte[] mem = null;

    static /* synthetic */ int[] $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMCommands() {
        int[] iArr = $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMCommands;
        if (iArr != null) {
            return iArr;
        }
        int[] iArr2 = new int[VMCommands.valuesCustom().length];
        try {
            iArr2[VMCommands.VM_ADC.ordinal()] = 38;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr2[VMCommands.VM_ADD.ordinal()] = 3;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr2[VMCommands.VM_ADDB.ordinal()] = 45;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr2[VMCommands.VM_ADDD.ordinal()] = 46;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[VMCommands.VM_AND.ordinal()] = 11;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr2[VMCommands.VM_CALL.ordinal()] = 22;
        } catch (NoSuchFieldError unused6) {
        }
        try {
            iArr2[VMCommands.VM_CMP.ordinal()] = 2;
        } catch (NoSuchFieldError unused7) {
        }
        try {
            iArr2[VMCommands.VM_CMPB.ordinal()] = 43;
        } catch (NoSuchFieldError unused8) {
        }
        try {
            iArr2[VMCommands.VM_CMPD.ordinal()] = 44;
        } catch (NoSuchFieldError unused9) {
        }
        try {
            iArr2[VMCommands.VM_DEC.ordinal()] = 8;
        } catch (NoSuchFieldError unused10) {
        }
        try {
            iArr2[VMCommands.VM_DECB.ordinal()] = 51;
        } catch (NoSuchFieldError unused11) {
        }
        try {
            iArr2[VMCommands.VM_DECD.ordinal()] = 52;
        } catch (NoSuchFieldError unused12) {
        }
        try {
            iArr2[VMCommands.VM_DIV.ordinal()] = 37;
        } catch (NoSuchFieldError unused13) {
        }
        try {
            iArr2[VMCommands.VM_INC.ordinal()] = 7;
        } catch (NoSuchFieldError unused14) {
        }
        try {
            iArr2[VMCommands.VM_INCB.ordinal()] = 49;
        } catch (NoSuchFieldError unused15) {
        }
        try {
            iArr2[VMCommands.VM_INCD.ordinal()] = 50;
        } catch (NoSuchFieldError unused16) {
        }
        try {
            iArr2[VMCommands.VM_JA.ordinal()] = 18;
        } catch (NoSuchFieldError unused17) {
        }
        try {
            iArr2[VMCommands.VM_JAE.ordinal()] = 19;
        } catch (NoSuchFieldError unused18) {
        }
        try {
            iArr2[VMCommands.VM_JB.ordinal()] = 16;
        } catch (NoSuchFieldError unused19) {
        }
        try {
            iArr2[VMCommands.VM_JBE.ordinal()] = 17;
        } catch (NoSuchFieldError unused20) {
        }
        try {
            iArr2[VMCommands.VM_JMP.ordinal()] = 9;
        } catch (NoSuchFieldError unused21) {
        }
        try {
            iArr2[VMCommands.VM_JNS.ordinal()] = 15;
        } catch (NoSuchFieldError unused22) {
        }
        try {
            iArr2[VMCommands.VM_JNZ.ordinal()] = 6;
        } catch (NoSuchFieldError unused23) {
        }
        try {
            iArr2[VMCommands.VM_JS.ordinal()] = 14;
        } catch (NoSuchFieldError unused24) {
        }
        try {
            iArr2[VMCommands.VM_JZ.ordinal()] = 5;
        } catch (NoSuchFieldError unused25) {
        }
        try {
            iArr2[VMCommands.VM_MOV.ordinal()] = 1;
        } catch (NoSuchFieldError unused26) {
        }
        try {
            iArr2[VMCommands.VM_MOVB.ordinal()] = 41;
        } catch (NoSuchFieldError unused27) {
        }
        try {
            iArr2[VMCommands.VM_MOVD.ordinal()] = 42;
        } catch (NoSuchFieldError unused28) {
        }
        try {
            iArr2[VMCommands.VM_MOVSX.ordinal()] = 34;
        } catch (NoSuchFieldError unused29) {
        }
        try {
            iArr2[VMCommands.VM_MOVZX.ordinal()] = 33;
        } catch (NoSuchFieldError unused30) {
        }
        try {
            iArr2[VMCommands.VM_MUL.ordinal()] = 36;
        } catch (NoSuchFieldError unused31) {
        }
        try {
            iArr2[VMCommands.VM_NEG.ordinal()] = 28;
        } catch (NoSuchFieldError unused32) {
        }
        try {
            iArr2[VMCommands.VM_NEGB.ordinal()] = 53;
        } catch (NoSuchFieldError unused33) {
        }
        try {
            iArr2[VMCommands.VM_NEGD.ordinal()] = 54;
        } catch (NoSuchFieldError unused34) {
        }
        try {
            iArr2[VMCommands.VM_NOT.ordinal()] = 24;
        } catch (NoSuchFieldError unused35) {
        }
        try {
            iArr2[VMCommands.VM_OR.ordinal()] = 12;
        } catch (NoSuchFieldError unused36) {
        }
        try {
            iArr2[VMCommands.VM_POP.ordinal()] = 21;
        } catch (NoSuchFieldError unused37) {
        }
        try {
            iArr2[VMCommands.VM_POPA.ordinal()] = 30;
        } catch (NoSuchFieldError unused38) {
        }
        try {
            iArr2[VMCommands.VM_POPF.ordinal()] = 32;
        } catch (NoSuchFieldError unused39) {
        }
        try {
            iArr2[VMCommands.VM_PRINT.ordinal()] = 40;
        } catch (NoSuchFieldError unused40) {
        }
        try {
            iArr2[VMCommands.VM_PUSH.ordinal()] = 20;
        } catch (NoSuchFieldError unused41) {
        }
        try {
            iArr2[VMCommands.VM_PUSHA.ordinal()] = 29;
        } catch (NoSuchFieldError unused42) {
        }
        try {
            iArr2[VMCommands.VM_PUSHF.ordinal()] = 31;
        } catch (NoSuchFieldError unused43) {
        }
        try {
            iArr2[VMCommands.VM_RET.ordinal()] = 23;
        } catch (NoSuchFieldError unused44) {
        }
        try {
            iArr2[VMCommands.VM_SAR.ordinal()] = 27;
        } catch (NoSuchFieldError unused45) {
        }
        try {
            iArr2[VMCommands.VM_SBB.ordinal()] = 39;
        } catch (NoSuchFieldError unused46) {
        }
        try {
            iArr2[VMCommands.VM_SHL.ordinal()] = 25;
        } catch (NoSuchFieldError unused47) {
        }
        try {
            iArr2[VMCommands.VM_SHR.ordinal()] = 26;
        } catch (NoSuchFieldError unused48) {
        }
        try {
            iArr2[VMCommands.VM_STANDARD.ordinal()] = 55;
        } catch (NoSuchFieldError unused49) {
        }
        try {
            iArr2[VMCommands.VM_SUB.ordinal()] = 4;
        } catch (NoSuchFieldError unused50) {
        }
        try {
            iArr2[VMCommands.VM_SUBB.ordinal()] = 47;
        } catch (NoSuchFieldError unused51) {
        }
        try {
            iArr2[VMCommands.VM_SUBD.ordinal()] = 48;
        } catch (NoSuchFieldError unused52) {
        }
        try {
            iArr2[VMCommands.VM_TEST.ordinal()] = 13;
        } catch (NoSuchFieldError unused53) {
        }
        try {
            iArr2[VMCommands.VM_XCHG.ordinal()] = 35;
        } catch (NoSuchFieldError unused54) {
        }
        try {
            iArr2[VMCommands.VM_XOR.ordinal()] = 10;
        } catch (NoSuchFieldError unused55) {
        }
        $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMCommands = iArr2;
        return iArr2;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMStandardFilters() {
        int[] iArr = $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMStandardFilters;
        if (iArr != null) {
            return iArr;
        }
        int[] iArr2 = new int[VMStandardFilters.valuesCustom().length];
        try {
            iArr2[VMStandardFilters.VMSF_AUDIO.ordinal()] = 6;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr2[VMStandardFilters.VMSF_DELTA.ordinal()] = 7;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr2[VMStandardFilters.VMSF_E8.ordinal()] = 2;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr2[VMStandardFilters.VMSF_E8E9.ordinal()] = 3;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[VMStandardFilters.VMSF_ITANIUM.ordinal()] = 4;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr2[VMStandardFilters.VMSF_NONE.ordinal()] = 1;
        } catch (NoSuchFieldError unused6) {
        }
        try {
            iArr2[VMStandardFilters.VMSF_RGB.ordinal()] = 5;
        } catch (NoSuchFieldError unused7) {
        }
        try {
            iArr2[VMStandardFilters.VMSF_UPCASE.ordinal()] = 8;
        } catch (NoSuchFieldError unused8) {
        }
        $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMStandardFilters = iArr2;
        return iArr2;
    }

    public void init() {
        if (this.mem == null) {
            this.mem = new byte[262148];
        }
    }

    private boolean isVMMem(byte[] bArr) {
        return this.mem == bArr;
    }

    private int getValue(boolean z, byte[] bArr, int i) {
        if (z) {
            if (isVMMem(bArr)) {
                return bArr[i];
            }
            return bArr[i] & 255;
        }
        if (isVMMem(bArr)) {
            return Raw.readIntLittleEndian(bArr, i);
        }
        return Raw.readIntBigEndian(bArr, i);
    }

    private void setValue(boolean z, byte[] bArr, int i, int i2) {
        if (z) {
            if (isVMMem(bArr)) {
                bArr[i] = (byte) i2;
                return;
            } else {
                byte b = bArr[i];
                bArr[i] = (byte) (((byte) (i2 & 255)) | 0);
                return;
            }
        }
        if (isVMMem(bArr)) {
            Raw.writeIntLittleEndian(bArr, i, i2);
        } else {
            Raw.writeIntBigEndian(bArr, i, i2);
        }
    }

    public void setLowEndianValue(byte[] bArr, int i, int i2) {
        Raw.writeIntLittleEndian(bArr, i, i2);
    }

    public void setLowEndianValue(Vector<Byte> vector, int i, int i2) {
        vector.set(i + 0, Byte.valueOf((byte) (i2 & 255)));
        vector.set(i + 1, Byte.valueOf((byte) ((i2 >>> 8) & 255)));
        vector.set(i + 2, Byte.valueOf((byte) ((i2 >>> 16) & 255)));
        vector.set(i + 3, Byte.valueOf((byte) ((i2 >>> 24) & 255)));
    }

    private int getOperand(VMPreparedOperand vMPreparedOperand) {
        if (vMPreparedOperand.getType() == VMOpType.VM_OPREGMEM) {
            return Raw.readIntLittleEndian(this.mem, 262143 & (vMPreparedOperand.getOffset() + vMPreparedOperand.getBase()));
        }
        return Raw.readIntLittleEndian(this.mem, vMPreparedOperand.getOffset());
    }

    public void execute(VMPreparedProgram vMPreparedProgram) {
        List<VMPreparedCommand> cmd;
        for (int i = 0; i < vMPreparedProgram.getInitR().length; i++) {
            this.R[i] = vMPreparedProgram.getInitR()[i];
        }
        long jMin = Math.min(vMPreparedProgram.getGlobalData().size(), 8192) & (-1);
        if (jMin != 0) {
            for (int i2 = 0; i2 < jMin; i2++) {
                this.mem[i2 + VM_GLOBALMEMADDR] = vMPreparedProgram.getGlobalData().get(i2).byteValue();
            }
        }
        long jMin2 = Math.min(vMPreparedProgram.getStaticData().size(), Trace.TRACE_TAG_RESOURCES - jMin) & (-1);
        if (jMin2 != 0) {
            for (int i3 = 0; i3 < jMin2; i3++) {
                this.mem[((int) jMin) + VM_GLOBALMEMADDR + i3] = vMPreparedProgram.getStaticData().get(i3).byteValue();
            }
        }
        this.R[7] = 262144;
        this.flags = 0;
        if (vMPreparedProgram.getAltCmd().size() != 0) {
            cmd = vMPreparedProgram.getAltCmd();
        } else {
            cmd = vMPreparedProgram.getCmd();
        }
        if (!ExecuteCode(cmd, vMPreparedProgram.getCmdCount())) {
            cmd.get(0).setOpCode(VMCommands.VM_RET);
        }
        int value = getValue(false, this.mem, 245792) & VM_MEMMASK;
        int value2 = 262143 & getValue(false, this.mem, 245788);
        if (value + value2 >= 262144) {
            value = 0;
            value2 = 0;
        }
        vMPreparedProgram.setFilteredDataOffset(value);
        vMPreparedProgram.setFilteredDataSize(value2);
        vMPreparedProgram.getGlobalData().clear();
        int iMin = Math.min(getValue(false, this.mem, 245808), 8128);
        if (iMin != 0) {
            int i4 = iMin + 64;
            vMPreparedProgram.getGlobalData().setSize(i4);
            for (int i5 = 0; i5 < i4; i5++) {
                vMPreparedProgram.getGlobalData().set(i5, Byte.valueOf(this.mem[i5 + VM_GLOBALMEMADDR]));
            }
        }
    }

    public byte[] getMem() {
        return this.mem;
    }

    private boolean setIP(int i) {
        if (i >= this.codeSize) {
            return true;
        }
        int i2 = this.maxOpCount - 1;
        this.maxOpCount = i2;
        if (i2 <= 0) {
            return false;
        }
        this.IP = i;
        return true;
    }

    private boolean ExecuteCode(List<VMPreparedCommand> list, int i) {
        int flag;
        int flag2;
        int flag3;
        int flag4;
        int flag5;
        int flag6;
        int flag7;
        int flag8;
        int flag9;
        int flag10;
        int flag11;
        int flag12;
        int flag13;
        int flag14;
        int flag15;
        this.maxOpCount = 25000000;
        this.codeSize = i;
        this.IP = 0;
        while (true) {
            VMPreparedCommand vMPreparedCommand = list.get(this.IP);
            int operand = getOperand(vMPreparedCommand.getOp1());
            int operand2 = getOperand(vMPreparedCommand.getOp2());
            switch ($SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMCommands()[vMPreparedCommand.getOpCode().ordinal()]) {
                case 1:
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, getValue(vMPreparedCommand.isByteMode(), this.mem, operand2));
                    break;
                case 2:
                    int value = getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    int value2 = value - getValue(vMPreparedCommand.isByteMode(), this.mem, operand2);
                    if (value2 == 0) {
                        this.flags = VMFlags.VM_FZ.getFlag();
                    } else {
                        this.flags = value2 > value ? 1 : value2 & VMFlags.VM_FS.getFlag();
                    }
                    break;
                case 3:
                    int value3 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    int value4 = (int) ((((long) value3) + ((long) getValue(vMPreparedCommand.isByteMode(), this.mem, operand2))) & (-1));
                    if (vMPreparedCommand.isByteMode()) {
                        value4 &= 255;
                        if (value4 < value3) {
                            flag2 = 1;
                        } else if (value4 == 0) {
                            flag2 = VMFlags.VM_FZ.getFlag();
                        } else {
                            flag2 = (value4 & 128) != 0 ? VMFlags.VM_FS.getFlag() : 0;
                        }
                        this.flags = flag2;
                    } else {
                        if (value4 < value3) {
                            flag = 1;
                        } else if (value4 == 0) {
                            flag = VMFlags.VM_FZ.getFlag();
                        } else {
                            flag = VMFlags.VM_FS.getFlag() & value4;
                        }
                        this.flags = flag;
                    }
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, value4);
                    break;
                case 4:
                    int value5 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    int value6 = (int) (((long) value5) & ((-1) - ((long) getValue(vMPreparedCommand.isByteMode(), this.mem, operand2))) & (-1));
                    if (value6 == 0) {
                        flag3 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag3 = value6 > value5 ? 1 : VMFlags.VM_FS.getFlag() & value6;
                    }
                    this.flags = flag3;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, value6);
                    break;
                case 5:
                    if ((this.flags & VMFlags.VM_FZ.getFlag()) != 0) {
                        setIP(getValue(false, this.mem, operand));
                    }
                    break;
                case 6:
                    if ((this.flags & VMFlags.VM_FZ.getFlag()) == 0) {
                        setIP(getValue(false, this.mem, operand));
                    }
                    break;
                case 7:
                    int value7 = (int) (((long) getValue(vMPreparedCommand.isByteMode(), this.mem, operand)) & 0);
                    if (vMPreparedCommand.isByteMode()) {
                        value7 &= 255;
                    }
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, value7);
                    if (value7 == 0) {
                        flag4 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag4 = VMFlags.VM_FS.getFlag() & value7;
                    }
                    this.flags = flag4;
                    break;
                case 8:
                    int value8 = (int) (((long) getValue(vMPreparedCommand.isByteMode(), this.mem, operand)) & (-2));
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, value8);
                    if (value8 == 0) {
                        flag5 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag5 = VMFlags.VM_FS.getFlag() & value8;
                    }
                    this.flags = flag5;
                    break;
                case 9:
                    setIP(getValue(false, this.mem, operand));
                    continue;
                case 10:
                    int value9 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand2) ^ getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    if (value9 == 0) {
                        flag6 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag6 = VMFlags.VM_FS.getFlag() & value9;
                    }
                    this.flags = flag6;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, value9);
                    break;
                case 11:
                    int value10 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand2) & getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    if (value10 == 0) {
                        flag7 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag7 = VMFlags.VM_FS.getFlag() & value10;
                    }
                    this.flags = flag7;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, value10);
                    break;
                case 12:
                    int value11 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand2) | getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    if (value11 == 0) {
                        flag8 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag8 = VMFlags.VM_FS.getFlag() & value11;
                    }
                    this.flags = flag8;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, value11);
                    break;
                case 13:
                    int value12 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand2) & getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    if (value12 == 0) {
                        flag9 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag9 = value12 & VMFlags.VM_FS.getFlag();
                    }
                    this.flags = flag9;
                    break;
                case 14:
                    if ((this.flags & VMFlags.VM_FS.getFlag()) != 0) {
                        setIP(getValue(false, this.mem, operand));
                    }
                    break;
                case 15:
                    if ((this.flags & VMFlags.VM_FS.getFlag()) == 0) {
                        setIP(getValue(false, this.mem, operand));
                    }
                    break;
                case 16:
                    if ((this.flags & VMFlags.VM_FC.getFlag()) != 0) {
                        setIP(getValue(false, this.mem, operand));
                    }
                    break;
                case 17:
                    if ((this.flags & (VMFlags.VM_FC.getFlag() | VMFlags.VM_FZ.getFlag())) != 0) {
                        setIP(getValue(false, this.mem, operand));
                    }
                    break;
                case 18:
                    if ((this.flags & (VMFlags.VM_FC.getFlag() | VMFlags.VM_FZ.getFlag())) == 0) {
                        setIP(getValue(false, this.mem, operand));
                    }
                    break;
                case 19:
                    if ((this.flags & VMFlags.VM_FC.getFlag()) == 0) {
                        setIP(getValue(false, this.mem, operand));
                    }
                    break;
                case 20:
                    int[] iArr = this.R;
                    iArr[7] = iArr[7] - 4;
                    byte[] bArr = this.mem;
                    setValue(false, bArr, iArr[7] & VM_MEMMASK, getValue(false, bArr, operand));
                    break;
                case 21:
                    byte[] bArr2 = this.mem;
                    setValue(false, bArr2, operand, getValue(false, bArr2, this.R[7] & VM_MEMMASK));
                    int[] iArr2 = this.R;
                    iArr2[7] = iArr2[7] + 4;
                    break;
                case 22:
                    int[] iArr3 = this.R;
                    iArr3[7] = iArr3[7] - 4;
                    setValue(false, this.mem, iArr3[7] & VM_MEMMASK, this.IP + 1);
                    setIP(getValue(false, this.mem, operand));
                    continue;
                case 23:
                    int[] iArr4 = this.R;
                    if (iArr4[7] >= 262144) {
                        return true;
                    }
                    setIP(getValue(false, this.mem, iArr4[7] & VM_MEMMASK));
                    int[] iArr5 = this.R;
                    iArr5[7] = iArr5[7] + 4;
                    continue;
                    break;
                case 24:
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, ~getValue(vMPreparedCommand.isByteMode(), this.mem, operand));
                    break;
                case 25:
                    int value13 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    int value14 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand2);
                    int i2 = value13 << value14;
                    if (i2 == 0) {
                        flag10 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag10 = VMFlags.VM_FS.getFlag() & i2;
                    }
                    this.flags = (((value13 << (value14 + (-1))) & Integer.MIN_VALUE) != 0 ? VMFlags.VM_FC.getFlag() : 0) | flag10;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, i2);
                    break;
                case 26:
                    int value15 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    int value16 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand2);
                    int i3 = value15 >>> value16;
                    if (i3 == 0) {
                        flag11 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag11 = VMFlags.VM_FS.getFlag() & i3;
                    }
                    this.flags = ((value15 >>> (value16 - 1)) & VMFlags.VM_FC.getFlag()) | flag11;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, i3);
                    break;
                case 27:
                    int value17 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    int value18 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand2);
                    int i4 = value17 >> value18;
                    if (i4 == 0) {
                        flag12 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag12 = VMFlags.VM_FS.getFlag() & i4;
                    }
                    this.flags = ((value17 >> (value18 - 1)) & VMFlags.VM_FC.getFlag()) | flag12;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, i4);
                    break;
                case 28:
                    int i5 = -getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    if (i5 == 0) {
                        flag13 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag13 = VMFlags.VM_FC.getFlag() | (VMFlags.VM_FS.getFlag() & i5);
                    }
                    this.flags = flag13;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, i5);
                    break;
                case 29:
                    int i6 = this.R[7] - 4;
                    int i7 = 0;
                    while (i7 < 8) {
                        setValue(false, this.mem, i6 & VM_MEMMASK, this.R[i7]);
                        i7++;
                        i6 -= 4;
                    }
                    this.R[7] = r2[7] - 32;
                    break;
                case 30:
                    int i8 = this.R[7];
                    int i9 = 0;
                    while (i9 < 8) {
                        this.R[7 - i9] = getValue(false, this.mem, i8 & VM_MEMMASK);
                        i9++;
                        i8 += 4;
                    }
                    break;
                case 31:
                    int[] iArr6 = this.R;
                    iArr6[7] = iArr6[7] - 4;
                    setValue(false, this.mem, iArr6[7] & VM_MEMMASK, this.flags);
                    break;
                case 32:
                    this.flags = getValue(false, this.mem, this.R[7] & VM_MEMMASK);
                    int[] iArr7 = this.R;
                    iArr7[7] = iArr7[7] + 4;
                    break;
                case 33:
                    byte[] bArr3 = this.mem;
                    setValue(false, bArr3, operand, getValue(true, bArr3, operand2));
                    break;
                case 34:
                    byte[] bArr4 = this.mem;
                    setValue(false, bArr4, operand, (byte) getValue(true, bArr4, operand2));
                    break;
                case 35:
                    int value19 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, getValue(vMPreparedCommand.isByteMode(), this.mem, operand2));
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand2, value19);
                    break;
                case 36:
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, (int) ((((long) getValue(vMPreparedCommand.isByteMode(), this.mem, operand2)) * (-1)) & ((long) getValue(vMPreparedCommand.isByteMode(), this.mem, operand)) & (-1) & (-1)));
                    break;
                case 37:
                    int value20 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand2);
                    if (value20 != 0) {
                        setValue(vMPreparedCommand.isByteMode(), this.mem, operand, getValue(vMPreparedCommand.isByteMode(), this.mem, operand) / value20);
                    }
                    break;
                case 38:
                    int value21 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    int flag16 = this.flags & VMFlags.VM_FC.getFlag();
                    int value22 = (int) (((long) value21) & (((long) getValue(vMPreparedCommand.isByteMode(), this.mem, operand2)) - 1) & (((long) flag16) - 1) & (-1));
                    if (vMPreparedCommand.isByteMode()) {
                        value22 &= 255;
                    }
                    if (value22 < value21 || (value22 == value21 && flag16 != 0)) {
                        flag14 = 1;
                    } else if (value22 == 0) {
                        flag14 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag14 = VMFlags.VM_FS.getFlag() & value22;
                    }
                    this.flags = flag14;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, value22);
                    break;
                case 39:
                    int value23 = getValue(vMPreparedCommand.isByteMode(), this.mem, operand);
                    int flag17 = this.flags & VMFlags.VM_FC.getFlag();
                    int value24 = (int) (((long) value23) & ((-1) - ((long) getValue(vMPreparedCommand.isByteMode(), this.mem, operand2))) & ((-1) - ((long) flag17)) & (-1));
                    if (vMPreparedCommand.isByteMode()) {
                        value24 &= 255;
                    }
                    if (value24 > value23 || (value24 == value23 && flag17 != 0)) {
                        flag15 = 1;
                    } else if (value24 == 0) {
                        flag15 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag15 = VMFlags.VM_FS.getFlag() & value24;
                    }
                    this.flags = flag15;
                    setValue(vMPreparedCommand.isByteMode(), this.mem, operand, value24);
                    break;
                case 41:
                    byte[] bArr5 = this.mem;
                    setValue(true, bArr5, operand, getValue(true, bArr5, operand2));
                    break;
                case 42:
                    byte[] bArr6 = this.mem;
                    setValue(false, bArr6, operand, getValue(false, bArr6, operand2));
                    break;
                case 43:
                    int value25 = getValue(true, this.mem, operand);
                    int value26 = value25 - getValue(true, this.mem, operand2);
                    if (value26 == 0) {
                        this.flags = VMFlags.VM_FZ.getFlag();
                    } else {
                        this.flags = value26 > value25 ? 1 : VMFlags.VM_FS.getFlag() & value26;
                    }
                    break;
                case 44:
                    int value27 = getValue(false, this.mem, operand);
                    int value28 = value27 - getValue(false, this.mem, operand2);
                    if (value28 == 0) {
                        this.flags = VMFlags.VM_FZ.getFlag();
                    } else {
                        this.flags = value28 > value27 ? 1 : VMFlags.VM_FS.getFlag() & value28;
                    }
                    break;
                case 45:
                    byte[] bArr7 = this.mem;
                    setValue(true, bArr7, operand, (int) ((((long) getValue(true, this.mem, operand2)) - 1) & ((long) getValue(true, bArr7, operand)) & (-1)));
                    break;
                case 46:
                    byte[] bArr8 = this.mem;
                    setValue(false, bArr8, operand, (int) ((((long) getValue(false, this.mem, operand2)) - 1) & ((long) getValue(false, bArr8, operand)) & (-1)));
                    break;
                case 47:
                    byte[] bArr9 = this.mem;
                    setValue(true, bArr9, operand, (int) (((-1) - ((long) getValue(true, this.mem, operand2))) & ((long) getValue(true, bArr9, operand)) & (-1)));
                    break;
                case 48:
                    byte[] bArr10 = this.mem;
                    setValue(false, bArr10, operand, (int) (((-1) - ((long) getValue(false, this.mem, operand2))) & ((long) getValue(false, bArr10, operand)) & (-1)));
                    break;
                case 49:
                    byte[] bArr11 = this.mem;
                    setValue(true, bArr11, operand, (int) (((long) getValue(true, bArr11, operand)) & 0));
                    break;
                case 50:
                    byte[] bArr12 = this.mem;
                    setValue(false, bArr12, operand, (int) (((long) getValue(false, bArr12, operand)) & 0));
                    break;
                case 51:
                    byte[] bArr13 = this.mem;
                    setValue(true, bArr13, operand, (int) (((long) getValue(true, bArr13, operand)) & (-2)));
                    break;
                case 52:
                    byte[] bArr14 = this.mem;
                    setValue(false, bArr14, operand, (int) (((long) getValue(false, bArr14, operand)) & (-2)));
                    break;
                case 53:
                    byte[] bArr15 = this.mem;
                    setValue(true, bArr15, operand, -getValue(true, bArr15, operand));
                    break;
                case 54:
                    byte[] bArr16 = this.mem;
                    setValue(false, bArr16, operand, -getValue(false, bArr16, operand));
                    break;
                case 55:
                    ExecuteStandardFilter(VMStandardFilters.findFilter(vMPreparedCommand.getOp1().getData()));
                    break;
            }
            this.IP++;
            this.maxOpCount--;
        }
    }

    public void prepare(byte[] bArr, int i, VMPreparedProgram vMPreparedProgram) {
        int i2;
        int cmdCount;
        InitBitInput();
        int iMin = Math.min(32768, i);
        for (int i3 = 0; i3 < iMin; i3++) {
            byte[] bArr2 = this.inBuf;
            bArr2[i3] = (byte) (bArr2[i3] | bArr[i3]);
        }
        byte b = 0;
        for (int i4 = 1; i4 < i; i4++) {
            b = (byte) (b ^ bArr[i4]);
        }
        faddbits(8);
        vMPreparedProgram.setCmdCount(0);
        if (b == bArr[0]) {
            VMStandardFilters vMStandardFiltersIsStandardFilter = IsStandardFilter(bArr, i);
            if (vMStandardFiltersIsStandardFilter != VMStandardFilters.VMSF_NONE) {
                VMPreparedCommand vMPreparedCommand = new VMPreparedCommand();
                vMPreparedCommand.setOpCode(VMCommands.VM_STANDARD);
                vMPreparedCommand.getOp1().setData(vMStandardFiltersIsStandardFilter.getFilter());
                vMPreparedCommand.getOp1().setType(VMOpType.VM_OPNONE);
                vMPreparedCommand.getOp2().setType(VMOpType.VM_OPNONE);
                vMPreparedProgram.getCmd().add(vMPreparedCommand);
                vMPreparedProgram.setCmdCount(vMPreparedProgram.getCmdCount() + 1);
                i2 = 0;
            } else {
                i2 = i;
            }
            int iFgetbits = fgetbits();
            faddbits(1);
            if ((iFgetbits & 32768) != 0) {
                long jReadData = ((long) ReadData(this)) & 0;
                for (int i5 = 0; this.inAddr < i2 && i5 < jReadData; i5++) {
                    vMPreparedProgram.getStaticData().add(Byte.valueOf((byte) (fgetbits() >> 8)));
                    faddbits(8);
                }
            }
            while (this.inAddr < i2) {
                VMPreparedCommand vMPreparedCommand2 = new VMPreparedCommand();
                int iFgetbits2 = fgetbits();
                if ((iFgetbits2 & 32768) == 0) {
                    vMPreparedCommand2.setOpCode(VMCommands.findVMCommand(iFgetbits2 >> 12));
                    faddbits(4);
                } else {
                    vMPreparedCommand2.setOpCode(VMCommands.findVMCommand((iFgetbits2 >> 10) - 24));
                    faddbits(6);
                }
                if ((VMCmdFlags.VM_CmdFlags[vMPreparedCommand2.getOpCode().getVMCommand()] & 4) != 0) {
                    vMPreparedCommand2.setByteMode((fgetbits() >> 15) == 1);
                    faddbits(1);
                } else {
                    vMPreparedCommand2.setByteMode(false);
                }
                vMPreparedCommand2.getOp1().setType(VMOpType.VM_OPNONE);
                vMPreparedCommand2.getOp2().setType(VMOpType.VM_OPNONE);
                int i6 = VMCmdFlags.VM_CmdFlags[vMPreparedCommand2.getOpCode().getVMCommand()] & 3;
                if (i6 > 0) {
                    decodeArg(vMPreparedCommand2.getOp1(), vMPreparedCommand2.isByteMode());
                    if (i6 == 2) {
                        decodeArg(vMPreparedCommand2.getOp2(), vMPreparedCommand2.isByteMode());
                    } else if (vMPreparedCommand2.getOp1().getType() == VMOpType.VM_OPINT && (VMCmdFlags.VM_CmdFlags[vMPreparedCommand2.getOpCode().getVMCommand()] & 24) != 0) {
                        int data = vMPreparedCommand2.getOp1().getData();
                        if (data >= 256) {
                            cmdCount = data - 256;
                        } else {
                            if (data >= 136) {
                                data -= 264;
                            } else if (data >= 16) {
                                data -= 8;
                            } else if (data >= 8) {
                                data -= 16;
                            }
                            cmdCount = data + vMPreparedProgram.getCmdCount();
                        }
                        vMPreparedCommand2.getOp1().setData(cmdCount);
                    }
                }
                vMPreparedProgram.setCmdCount(vMPreparedProgram.getCmdCount() + 1);
                vMPreparedProgram.getCmd().add(vMPreparedCommand2);
            }
            i = i2;
        }
        VMPreparedCommand vMPreparedCommand3 = new VMPreparedCommand();
        vMPreparedCommand3.setOpCode(VMCommands.VM_RET);
        vMPreparedCommand3.getOp1().setType(VMOpType.VM_OPNONE);
        vMPreparedCommand3.getOp2().setType(VMOpType.VM_OPNONE);
        vMPreparedProgram.getCmd().add(vMPreparedCommand3);
        vMPreparedProgram.setCmdCount(vMPreparedProgram.getCmdCount() + 1);
        if (i != 0) {
            optimize(vMPreparedProgram);
        }
    }

    private void decodeArg(VMPreparedOperand vMPreparedOperand, boolean z) {
        int iFgetbits = fgetbits();
        if ((32768 & iFgetbits) != 0) {
            vMPreparedOperand.setType(VMOpType.VM_OPREG);
            vMPreparedOperand.setData((iFgetbits >> 12) & 7);
            vMPreparedOperand.setOffset(vMPreparedOperand.getData());
            faddbits(4);
            return;
        }
        if ((49152 & iFgetbits) == 0) {
            vMPreparedOperand.setType(VMOpType.VM_OPINT);
            if (z) {
                vMPreparedOperand.setData((iFgetbits >> 6) & 255);
                faddbits(10);
                return;
            } else {
                faddbits(2);
                vMPreparedOperand.setData(ReadData(this));
                return;
            }
        }
        vMPreparedOperand.setType(VMOpType.VM_OPREGMEM);
        if ((iFgetbits & 8192) == 0) {
            vMPreparedOperand.setData((iFgetbits >> 10) & 7);
            vMPreparedOperand.setOffset(vMPreparedOperand.getData());
            vMPreparedOperand.setBase(0);
            faddbits(6);
            return;
        }
        if ((iFgetbits & 4096) == 0) {
            vMPreparedOperand.setData((iFgetbits >> 9) & 7);
            vMPreparedOperand.setOffset(vMPreparedOperand.getData());
            faddbits(7);
        } else {
            vMPreparedOperand.setData(0);
            faddbits(4);
        }
        vMPreparedOperand.setBase(ReadData(this));
    }

    private void optimize(VMPreparedProgram vMPreparedProgram) {
        List<VMPreparedCommand> cmd = vMPreparedProgram.getCmd();
        for (VMPreparedCommand vMPreparedCommand : cmd) {
            int i = $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMCommands()[vMPreparedCommand.getOpCode().ordinal()];
            boolean z = true;
            if (i == 1) {
                vMPreparedCommand.setOpCode(vMPreparedCommand.isByteMode() ? VMCommands.VM_MOVB : VMCommands.VM_MOVD);
            } else if (i == 2) {
                vMPreparedCommand.setOpCode(vMPreparedCommand.isByteMode() ? VMCommands.VM_CMPB : VMCommands.VM_CMPD);
            } else if ((VMCmdFlags.VM_CmdFlags[vMPreparedCommand.getOpCode().getVMCommand()] & 64) != 0) {
                for (int iIndexOf = cmd.indexOf(vMPreparedCommand) + 1; iIndexOf < cmd.size(); iIndexOf++) {
                    byte b = VMCmdFlags.VM_CmdFlags[cmd.get(iIndexOf).getOpCode().getVMCommand()];
                    if ((b & PaletteRecord.STANDARD_PALETTE_SIZE) != 0) {
                        break;
                    } else {
                        if ((b & 64) != 0) {
                            break;
                        }
                    }
                }
                z = false;
                if (!z) {
                    int i2 = $SWITCH_TABLE$de$innosystec$unrar$unpack$vm$VMCommands()[vMPreparedCommand.getOpCode().ordinal()];
                    if (i2 == 3) {
                        vMPreparedCommand.setOpCode(vMPreparedCommand.isByteMode() ? VMCommands.VM_ADDB : VMCommands.VM_ADDD);
                    } else if (i2 == 4) {
                        vMPreparedCommand.setOpCode(vMPreparedCommand.isByteMode() ? VMCommands.VM_SUBB : VMCommands.VM_SUBD);
                    } else if (i2 == 7) {
                        vMPreparedCommand.setOpCode(vMPreparedCommand.isByteMode() ? VMCommands.VM_INCB : VMCommands.VM_INCD);
                    } else if (i2 == 8) {
                        vMPreparedCommand.setOpCode(vMPreparedCommand.isByteMode() ? VMCommands.VM_DECB : VMCommands.VM_DECD);
                    } else if (i2 == 28) {
                        vMPreparedCommand.setOpCode(vMPreparedCommand.isByteMode() ? VMCommands.VM_NEGB : VMCommands.VM_NEGD);
                    }
                }
            }
        }
    }

    public static int ReadData(BitInput bitInput) {
        int iFgetbits = bitInput.fgetbits();
        int i = 49152 & iFgetbits;
        if (i == 0) {
            bitInput.faddbits(6);
            return (iFgetbits >> 10) & 15;
        }
        if (i == 16384) {
            if ((iFgetbits & 15360) == 0) {
                int i2 = ((iFgetbits >> 2) & 255) | (-256);
                bitInput.faddbits(14);
                return i2;
            }
            int i3 = (iFgetbits >> 6) & 255;
            bitInput.faddbits(10);
            return i3;
        }
        if (i == 32768) {
            bitInput.faddbits(2);
            int iFgetbits2 = bitInput.fgetbits();
            bitInput.faddbits(16);
            return iFgetbits2;
        }
        bitInput.faddbits(2);
        int iFgetbits3 = bitInput.fgetbits() << 16;
        bitInput.faddbits(16);
        int iFgetbits4 = iFgetbits3 | bitInput.fgetbits();
        bitInput.faddbits(16);
        return iFgetbits4;
    }

    private VMStandardFilters IsStandardFilter(byte[] bArr, int i) {
        VMStandardFilterSignature[] vMStandardFilterSignatureArr = {new VMStandardFilterSignature(53, -1386780537, VMStandardFilters.VMSF_E8), new VMStandardFilterSignature(57, 1020781950, VMStandardFilters.VMSF_E8E9), new VMStandardFilterSignature(120, 929663295, VMStandardFilters.VMSF_ITANIUM), new VMStandardFilterSignature(29, 235276157, VMStandardFilters.VMSF_DELTA), new VMStandardFilterSignature(149, 472669640, VMStandardFilters.VMSF_RGB), new VMStandardFilterSignature(216, -1132075263, VMStandardFilters.VMSF_AUDIO), new VMStandardFilterSignature(40, 1186579808, VMStandardFilters.VMSF_UPCASE)};
        int i2 = ~RarCRC.checkCrc(-1, bArr, 0, bArr.length);
        for (int i3 = 0; i3 < 7; i3++) {
            if (vMStandardFilterSignatureArr[i3].getCRC() == i2 && vMStandardFilterSignatureArr[i3].getLength() == bArr.length) {
                return vMStandardFilterSignatureArr[i3].getType();
            }
        }
        return VMStandardFilters.VMSF_NONE;
    }

    /* JADX WARN: Removed duplicated region for block: B:68:0x01ba A[PHI: r33
      0x01ba: PHI (r33v6 int) = (r33v5 int), (r33v7 int) binds: [B:38:0x0153, B:43:0x0166] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x01bc A[PHI: r3 r6 r9
      0x01bc: PHI (r3v15 int) = 
      (r3v14 int)
      (r3v14 int)
      (r3v14 int)
      (r3v14 int)
      (r3v14 int)
      (r3v14 int)
      (r3v17 int)
      (r3v14 int)
      (r3v18 int)
      (r3v14 int)
      (r3v14 int)
     binds: [B:68:0x01ba, B:61:0x0199, B:62:0x019b, B:58:0x0192, B:59:0x0194, B:55:0x0189, B:56:0x018b, B:52:0x0180, B:53:0x0182, B:49:0x0177, B:46:0x016e] A[DONT_GENERATE, DONT_INLINE]
      0x01bc: PHI (r6v20 int) = 
      (r6v18 int)
      (r6v18 int)
      (r6v22 int)
      (r6v18 int)
      (r6v23 int)
      (r6v18 int)
      (r6v18 int)
      (r6v18 int)
      (r6v18 int)
      (r6v18 int)
      (r6v18 int)
     binds: [B:68:0x01ba, B:61:0x0199, B:62:0x019b, B:58:0x0192, B:59:0x0194, B:55:0x0189, B:56:0x018b, B:52:0x0180, B:53:0x0182, B:49:0x0177, B:46:0x016e] A[DONT_GENERATE, DONT_INLINE]
      0x01bc: PHI (r9v28 int) = 
      (r9v27 int)
      (r9v31 int)
      (r9v31 int)
      (r9v32 int)
      (r9v32 int)
      (r9v33 int)
      (r9v33 int)
      (r9v34 int)
      (r9v34 int)
      (r9v35 int)
      (r9v36 int)
     binds: [B:68:0x01ba, B:61:0x0199, B:62:0x019b, B:58:0x0192, B:59:0x0194, B:55:0x0189, B:56:0x018b, B:52:0x0180, B:53:0x0182, B:49:0x0177, B:46:0x016e] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void ExecuteStandardFilter(de.innosystec.unrar.unpack.vm.VMStandardFilters r33) {
        /*
            Method dump skipped, instruction units count: 906
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: de.innosystec.unrar.unpack.vm.RarVM.ExecuteStandardFilter(de.innosystec.unrar.unpack.vm.VMStandardFilters):void");
    }

    private void filterItanium_SetBits(int i, int i2, int i3, int i4) {
        int i5 = i3 / 8;
        int i6 = i3 & 7;
        int i7 = ~(((-1) >>> (32 - i4)) << i6);
        int i8 = i2 << i6;
        for (int i9 = 0; i9 < 4; i9++) {
            byte[] bArr = this.mem;
            int i10 = i + i5 + i9;
            bArr[i10] = (byte) (bArr[i10] & i7);
            bArr[i10] = (byte) (bArr[i10] | i8);
            i7 = (i7 >>> 8) | (-16777216);
            i8 >>>= 8;
        }
    }

    private int filterItanium_GetBits(int i, int i2, int i3) {
        int i4 = i2 / 8;
        byte[] bArr = this.mem;
        int i5 = i4 + 1;
        int i6 = i5 + 1;
        int i7 = (bArr[i4 + i] & 255) | ((bArr[i5 + i] & 255) << 8);
        int i8 = i7 | ((bArr[i6 + i] & 255) << 16);
        return ((((bArr[i + (i6 + 1)] & 255) << 24) | i8) >>> (i2 & 7)) & ((-1) >>> (32 - i3));
    }

    public void setMemory(int i, byte[] bArr, int i2, int i3) {
        if (i < 262144) {
            for (int i4 = 0; i4 < Math.min(bArr.length - i2, i3) && 262144 - i >= i4; i4++) {
                this.mem[i + i4] = bArr[i2 + i4];
            }
        }
    }
}
