package de.innosystec.unrar.unpack.ppm;

import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.unpack.Unpack;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class ModelPPM {
    public static final int BIN_SCALE = 16384;
    public static final int INTERVAL = 128;
    public static final int INT_BITS = 7;
    private static int[] InitBinEsc = {15581, 7999, 22975, 18675, 25761, 23228, 26162, 24657};
    public static final int MAX_FREQ = 124;
    public static final int MAX_O = 64;
    public static final int PERIOD_BITS = 7;
    public static final int TOT_BITS = 14;
    private SEE2Context dummySEE2Cont;
    private int escCount;
    private State foundState;
    private int hiBitsFlag;
    private int initEsc;
    private int initRL;
    private int maxOrder;
    private int numMasked;
    private int orderFall;
    private int prevSuccess;
    private int runLength;
    private SEE2Context[][] SEE2Cont = (SEE2Context[][]) Array.newInstance((Class<?>) SEE2Context.class, 25, 16);
    private int[] charMask = new int[256];
    private int[] NS2Indx = new int[256];
    private int[] NS2BSIndx = new int[256];
    private int[] HB2Flag = new int[256];
    private int[][] binSumm = (int[][]) Array.newInstance((Class<?>) int.class, 128, 64);
    private RangeCoder coder = new RangeCoder();
    private SubAllocator subAlloc = new SubAllocator();
    private final State tempState1 = new State(null);
    private final State tempState2 = new State(null);
    private final State tempState3 = new State(null);
    private final State tempState4 = new State(null);
    private final StateRef tempStateRef1 = new StateRef();
    private final StateRef tempStateRef2 = new StateRef();
    private final PPMContext tempPPMContext1 = new PPMContext(null);
    private final PPMContext tempPPMContext2 = new PPMContext(null);
    private final PPMContext tempPPMContext3 = new PPMContext(null);
    private final PPMContext tempPPMContext4 = new PPMContext(null);
    private final int[] ps = new int[64];
    private PPMContext minContext = null;
    private PPMContext maxContext = null;
    private PPMContext medContext = null;

    public SubAllocator getSubAlloc() {
        return this.subAlloc;
    }

    private void restartModelRare() {
        Arrays.fill(this.charMask, 0);
        this.subAlloc.initSubAllocator();
        int i = this.maxOrder;
        if (i >= 12) {
            i = 12;
        }
        this.initRL = (-i) - 1;
        int iAllocContext = this.subAlloc.allocContext();
        this.minContext.setAddress(iAllocContext);
        this.maxContext.setAddress(iAllocContext);
        this.minContext.setSuffix(0);
        this.orderFall = this.maxOrder;
        this.minContext.setNumStats(256);
        this.minContext.getFreqData().setSummFreq(this.minContext.getNumStats() + 1);
        int iAllocUnits = this.subAlloc.allocUnits(128);
        this.foundState.setAddress(iAllocUnits);
        this.minContext.getFreqData().setStats(iAllocUnits);
        State state = new State(this.subAlloc.getHeap());
        int stats = this.minContext.getFreqData().getStats();
        this.runLength = this.initRL;
        this.prevSuccess = 0;
        for (int i2 = 0; i2 < 256; i2++) {
            state.setAddress((i2 * 6) + stats);
            state.setSymbol(i2);
            state.setFreq(1);
            state.setSuccessor(0);
        }
        for (int i3 = 0; i3 < 128; i3++) {
            for (int i4 = 0; i4 < 8; i4++) {
                for (int i5 = 0; i5 < 64; i5 += 8) {
                    this.binSumm[i3][i4 + i5] = 16384 - (InitBinEsc[i4] / (i3 + 2));
                }
            }
        }
        for (int i6 = 0; i6 < 25; i6++) {
            for (int i7 = 0; i7 < 16; i7++) {
                this.SEE2Cont[i6][i7].init((i6 * 5) + 10);
            }
        }
    }

    private void startModelRare(int i) {
        this.escCount = 1;
        this.maxOrder = i;
        restartModelRare();
        int[] iArr = this.NS2BSIndx;
        iArr[0] = 0;
        iArr[1] = 2;
        for (int i2 = 0; i2 < 9; i2++) {
            this.NS2BSIndx[i2 + 2] = 4;
        }
        for (int i3 = 0; i3 < 245; i3++) {
            this.NS2BSIndx[i3 + 11] = 6;
        }
        int i4 = 0;
        while (i4 < 3) {
            this.NS2Indx[i4] = i4;
            i4++;
        }
        int i5 = 1;
        int i6 = 1;
        int i7 = i4;
        while (i4 < 256) {
            this.NS2Indx[i4] = i7;
            i5--;
            if (i5 == 0) {
                i6++;
                i7++;
                i5 = i6;
            }
            i4++;
        }
        for (int i8 = 0; i8 < 64; i8++) {
            this.HB2Flag[i8] = 0;
        }
        for (int i9 = 0; i9 < 192; i9++) {
            this.HB2Flag[i9 + 64] = 8;
        }
        this.dummySEE2Cont.setShift(7);
    }

    private void clearMask() {
        this.escCount = 1;
        Arrays.fill(this.charMask, 0);
    }

    public boolean decodeInit(Unpack unpack, int i) throws RarException, IOException {
        int i2;
        int i3 = unpack.getChar() & 255;
        boolean z = (i3 & 32) != 0;
        if (z) {
            i2 = unpack.getChar();
        } else {
            if (this.subAlloc.GetAllocatedMemory() == 0) {
                return false;
            }
            i2 = 0;
        }
        if ((i3 & 64) != 0) {
            unpack.setPpmEscChar(unpack.getChar());
        }
        this.coder.initDecoder(unpack);
        if (z) {
            int i4 = (i3 & 31) + 1;
            if (i4 > 16) {
                i4 = ((i4 - 16) * 3) + 16;
            }
            if (i4 == 1) {
                this.subAlloc.stopSubAllocator();
                return false;
            }
            this.subAlloc.startSubAllocator(i2 + 1);
            this.minContext = new PPMContext(getHeap());
            this.medContext = new PPMContext(getHeap());
            this.maxContext = new PPMContext(getHeap());
            this.foundState = new State(getHeap());
            this.dummySEE2Cont = new SEE2Context();
            for (int i5 = 0; i5 < 25; i5++) {
                for (int i6 = 0; i6 < 16; i6++) {
                    this.SEE2Cont[i5][i6] = new SEE2Context();
                }
            }
            startModelRare(i4);
        }
        return this.minContext.getAddress() != 0;
    }

    public int decodeChar() throws RarException, IOException {
        if (this.minContext.getAddress() > this.subAlloc.getPText() && this.minContext.getAddress() <= this.subAlloc.getHeapEnd()) {
            if (this.minContext.getNumStats() != 1) {
                if (this.minContext.getFreqData().getStats() <= this.subAlloc.getPText() || this.minContext.getFreqData().getStats() > this.subAlloc.getHeapEnd() || !this.minContext.decodeSymbol1(this)) {
                    return -1;
                }
            } else {
                this.minContext.decodeBinSymbol(this);
            }
            this.coder.decode();
            while (this.foundState.getAddress() == 0) {
                this.coder.ariDecNormalize();
                do {
                    this.orderFall++;
                    PPMContext pPMContext = this.minContext;
                    pPMContext.setAddress(pPMContext.getSuffix());
                    if (this.minContext.getAddress() <= this.subAlloc.getPText() || this.minContext.getAddress() > this.subAlloc.getHeapEnd()) {
                    }
                } while (this.minContext.getNumStats() == this.numMasked);
                if (!this.minContext.decodeSymbol2(this)) {
                    return -1;
                }
                this.coder.decode();
            }
            int symbol = this.foundState.getSymbol();
            if (this.orderFall == 0 && this.foundState.getSuccessor() > this.subAlloc.getPText()) {
                int successor = this.foundState.getSuccessor();
                this.minContext.setAddress(successor);
                this.maxContext.setAddress(successor);
            } else {
                updateModel();
                if (this.escCount == 0) {
                    clearMask();
                }
            }
            this.coder.ariDecNormalize();
            return symbol;
        }
        return -1;
    }

    public SEE2Context[][] getSEE2Cont() {
        return this.SEE2Cont;
    }

    public SEE2Context getDummySEE2Cont() {
        return this.dummySEE2Cont;
    }

    public int getInitRL() {
        return this.initRL;
    }

    public void setEscCount(int i) {
        this.escCount = i & 255;
    }

    public int getEscCount() {
        return this.escCount;
    }

    public void incEscCount(int i) {
        setEscCount(getEscCount() + i);
    }

    public int[] getCharMask() {
        return this.charMask;
    }

    public int getNumMasked() {
        return this.numMasked;
    }

    public void setNumMasked(int i) {
        this.numMasked = i;
    }

    public void setPrevSuccess(int i) {
        this.prevSuccess = i & 255;
    }

    public int getInitEsc() {
        return this.initEsc;
    }

    public void setInitEsc(int i) {
        this.initEsc = i;
    }

    public void setRunLength(int i) {
        this.runLength = i;
    }

    public int getRunLength() {
        return this.runLength;
    }

    public void incRunLength(int i) {
        setRunLength(getRunLength() + i);
    }

    public int getPrevSuccess() {
        return this.prevSuccess;
    }

    public int getHiBitsFlag() {
        return this.hiBitsFlag;
    }

    public void setHiBitsFlag(int i) {
        this.hiBitsFlag = i & 255;
    }

    public int[][] getBinSumm() {
        return this.binSumm;
    }

    public RangeCoder getCoder() {
        return this.coder;
    }

    public int[] getHB2Flag() {
        return this.HB2Flag;
    }

    public int[] getNS2BSIndx() {
        return this.NS2BSIndx;
    }

    public int[] getNS2Indx() {
        return this.NS2Indx;
    }

    public State getFoundState() {
        return this.foundState;
    }

    public byte[] getHeap() {
        return this.subAlloc.getHeap();
    }

    public int getOrderFall() {
        return this.orderFall;
    }

    /* JADX WARN: Code restructure failed: missing block: B:56:0x0070, code lost:
    
        r10 = false;
     */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00d6  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00db  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int createSuccessors(boolean r10, de.innosystec.unrar.unpack.ppm.State r11) {
        /*
            Method dump skipped, instruction units count: 377
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: de.innosystec.unrar.unpack.ppm.ModelPPM.createSuccessors(boolean, de.innosystec.unrar.unpack.ppm.State):int");
    }

    private void updateModelRestart() {
        restartModelRare();
        this.escCount = 0;
    }

    private void updateModel() {
        int i;
        StateRef stateRef = this.tempStateRef1;
        stateRef.setValues(this.foundState);
        State stateInit = this.tempState3.init(getHeap());
        State stateInit2 = this.tempState4.init(getHeap());
        PPMContext pPMContextInit = this.tempPPMContext3.init(getHeap());
        PPMContext pPMContextInit2 = this.tempPPMContext4.init(getHeap());
        pPMContextInit.setAddress(this.minContext.getSuffix());
        if (stateRef.getFreq() < 31 && pPMContextInit.getAddress() != 0) {
            if (pPMContextInit.getNumStats() != 1) {
                stateInit.setAddress(pPMContextInit.getFreqData().getStats());
                if (stateInit.getSymbol() != stateRef.getSymbol()) {
                    do {
                        stateInit.incAddress();
                    } while (stateInit.getSymbol() != stateRef.getSymbol());
                    stateInit2.setAddress(stateInit.getAddress() - 6);
                    if (stateInit.getFreq() >= stateInit2.getFreq()) {
                        State.ppmdSwap(stateInit, stateInit2);
                        stateInit.decAddress();
                    }
                }
                if (stateInit.getFreq() < 115) {
                    stateInit.incFreq(2);
                    pPMContextInit.getFreqData().incSummFreq(2);
                }
            } else {
                stateInit.setAddress(pPMContextInit.getOneState().getAddress());
                if (stateInit.getFreq() < 32) {
                    stateInit.incFreq(1);
                }
            }
        }
        if (this.orderFall == 0) {
            this.foundState.setSuccessor(createSuccessors(true, stateInit));
            this.minContext.setAddress(this.foundState.getSuccessor());
            this.maxContext.setAddress(this.foundState.getSuccessor());
            if (this.minContext.getAddress() == 0) {
                updateModelRestart();
                return;
            }
            return;
        }
        this.subAlloc.getHeap()[this.subAlloc.getPText()] = (byte) stateRef.getSymbol();
        this.subAlloc.incPText();
        pPMContextInit2.setAddress(this.subAlloc.getPText());
        if (this.subAlloc.getPText() >= this.subAlloc.getFakeUnitsStart()) {
            updateModelRestart();
            return;
        }
        if (stateRef.getSuccessor() != 0) {
            if (stateRef.getSuccessor() <= this.subAlloc.getPText()) {
                stateRef.setSuccessor(createSuccessors(false, stateInit));
                if (stateRef.getSuccessor() == 0) {
                    updateModelRestart();
                    return;
                }
            }
            int i2 = this.orderFall - 1;
            this.orderFall = i2;
            if (i2 == 0) {
                pPMContextInit2.setAddress(stateRef.getSuccessor());
                if (this.maxContext.getAddress() != this.minContext.getAddress()) {
                    this.subAlloc.decPText(1);
                }
            }
        } else {
            this.foundState.setSuccessor(pPMContextInit2.getAddress());
            stateRef.setSuccessor(this.minContext);
        }
        int numStats = this.minContext.getNumStats();
        int summFreq = (this.minContext.getFreqData().getSummFreq() - numStats) - (stateRef.getFreq() - 1);
        pPMContextInit.setAddress(this.maxContext.getAddress());
        while (pPMContextInit.getAddress() != this.minContext.getAddress()) {
            int numStats2 = pPMContextInit.getNumStats();
            if (numStats2 != 1) {
                if ((numStats2 & 1) == 0) {
                    pPMContextInit.getFreqData().setStats(this.subAlloc.expandUnits(pPMContextInit.getFreqData().getStats(), numStats2 >>> 1));
                    if (pPMContextInit.getFreqData().getStats() == 0) {
                        updateModelRestart();
                        return;
                    }
                }
                pPMContextInit.getFreqData().incSummFreq((numStats2 * 2 < numStats ? 1 : 0) + (((numStats2 * 4 <= numStats ? 1 : 0) & (pPMContextInit.getFreqData().getSummFreq() <= numStats2 * 8 ? 1 : 0)) * 2));
            } else {
                stateInit.setAddress(this.subAlloc.allocUnits(1));
                if (stateInit.getAddress() == 0) {
                    updateModelRestart();
                    return;
                }
                stateInit.setValues(pPMContextInit.getOneState());
                pPMContextInit.getFreqData().setStats(stateInit);
                if (stateInit.getFreq() < 30) {
                    stateInit.incFreq(stateInit.getFreq());
                } else {
                    stateInit.setFreq(120);
                }
                pPMContextInit.getFreqData().setSummFreq(stateInit.getFreq() + this.initEsc + (numStats > 3 ? 1 : 0));
            }
            int freq = stateRef.getFreq() * 2 * (pPMContextInit.getFreqData().getSummFreq() + 6);
            int summFreq2 = pPMContextInit.getFreqData().getSummFreq() + summFreq;
            if (freq < summFreq2 * 6) {
                i = (freq > summFreq2 ? 1 : 0) + 1 + (freq >= summFreq2 * 4 ? 1 : 0);
                pPMContextInit.getFreqData().incSummFreq(3);
            } else {
                i = (freq >= summFreq2 * 9 ? 1 : 0) + 4 + (freq >= summFreq2 * 12 ? 1 : 0) + (freq >= summFreq2 * 15 ? 1 : 0);
                pPMContextInit.getFreqData().incSummFreq(i);
            }
            stateInit.setAddress(pPMContextInit.getFreqData().getStats() + (numStats2 * 6));
            stateInit.setSuccessor(pPMContextInit2);
            stateInit.setSymbol(stateRef.getSymbol());
            stateInit.setFreq(i);
            pPMContextInit.setNumStats(numStats2 + 1);
            pPMContextInit.setAddress(pPMContextInit.getSuffix());
        }
        int successor = stateRef.getSuccessor();
        this.maxContext.setAddress(successor);
        this.minContext.setAddress(successor);
    }

    public String toString() {
        return "ModelPPM[\n  numMasked=" + this.numMasked + "\n  initEsc=" + this.initEsc + "\n  orderFall=" + this.orderFall + "\n  maxOrder=" + this.maxOrder + "\n  runLength=" + this.runLength + "\n  initRL=" + this.initRL + "\n  escCount=" + this.escCount + "\n  prevSuccess=" + this.prevSuccess + "\n  foundState=" + this.foundState + "\n  coder=" + this.coder + "\n  subAlloc=" + this.subAlloc + "\n]";
    }
}
