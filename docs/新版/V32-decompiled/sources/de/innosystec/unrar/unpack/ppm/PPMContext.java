package de.innosystec.unrar.unpack.ppm;

import android.os.Trace;
import de.innosystec.unrar.io.Raw;

/* JADX INFO: loaded from: classes2.dex */
public class PPMContext extends Pointer {
    public static final int[] ExpEscape;
    public static final int size;
    private static final int unionSize;
    private final FreqData freqData;
    private int numStats;
    private final State oneState;
    private final int[] ps;
    private int suffix;
    private PPMContext tempPPMContext;
    private final State tempState1;
    private final State tempState2;
    private final State tempState3;
    private final State tempState4;
    private final State tempState5;

    public int getMean(int i, int i2, int i3) {
        return (i + (1 << (i2 - i3))) >>> i2;
    }

    static {
        int iMax = Math.max(6, 6);
        unionSize = iMax;
        size = iMax + 2 + 4;
        ExpEscape = new int[]{25, 14, 9, 7, 5, 5, 4, 4, 4, 3, 3, 3, 2, 2, 2, 2};
    }

    public PPMContext(byte[] bArr) {
        super(bArr);
        this.tempState1 = new State(null);
        this.tempState2 = new State(null);
        this.tempState3 = new State(null);
        this.tempState4 = new State(null);
        this.tempState5 = new State(null);
        this.tempPPMContext = null;
        this.ps = new int[256];
        this.oneState = new State(bArr);
        this.freqData = new FreqData(bArr);
    }

    public PPMContext init(byte[] bArr) {
        this.mem = bArr;
        this.pos = 0;
        this.oneState.init(bArr);
        this.freqData.init(bArr);
        return this;
    }

    public FreqData getFreqData() {
        return this.freqData;
    }

    public void setFreqData(FreqData freqData) {
        this.freqData.setSummFreq(freqData.getSummFreq());
        this.freqData.setStats(freqData.getStats());
    }

    public final int getNumStats() {
        if (this.mem != null) {
            this.numStats = Raw.readShortLittleEndian(this.mem, this.pos) & 65535;
        }
        return this.numStats;
    }

    public final void setNumStats(int i) {
        this.numStats = 65535 & i;
        if (this.mem != null) {
            Raw.writeShortLittleEndian(this.mem, this.pos, (short) i);
        }
    }

    public State getOneState() {
        return this.oneState;
    }

    public void setOneState(StateRef stateRef) {
        this.oneState.setValues(stateRef);
    }

    public int getSuffix() {
        if (this.mem != null) {
            this.suffix = Raw.readIntLittleEndian(this.mem, this.pos + 8);
        }
        return this.suffix;
    }

    public void setSuffix(PPMContext pPMContext) {
        setSuffix(pPMContext.getAddress());
    }

    public void setSuffix(int i) {
        this.suffix = i;
        if (this.mem != null) {
            Raw.writeIntLittleEndian(this.mem, this.pos + 8, i);
        }
    }

    @Override // de.innosystec.unrar.unpack.ppm.Pointer
    public void setAddress(int i) {
        super.setAddress(i);
        int i2 = i + 2;
        this.oneState.setAddress(i2);
        this.freqData.setAddress(i2);
    }

    private PPMContext getTempPPMContext(byte[] bArr) {
        if (this.tempPPMContext == null) {
            this.tempPPMContext = new PPMContext(null);
        }
        return this.tempPPMContext.init(bArr);
    }

    public int createChild(ModelPPM modelPPM, State state, StateRef stateRef) {
        PPMContext tempPPMContext = getTempPPMContext(modelPPM.getSubAlloc().getHeap());
        tempPPMContext.setAddress(modelPPM.getSubAlloc().allocContext());
        if (tempPPMContext != null) {
            tempPPMContext.setNumStats(1);
            tempPPMContext.setOneState(stateRef);
            tempPPMContext.setSuffix(this);
            state.setSuccessor(tempPPMContext);
        }
        return tempPPMContext.getAddress();
    }

    public void rescale(ModelPPM modelPPM) {
        int numStats = getNumStats();
        int numStats2 = getNumStats() - 1;
        State state = new State(modelPPM.getHeap());
        State state2 = new State(modelPPM.getHeap());
        State state3 = new State(modelPPM.getHeap());
        state2.setAddress(modelPPM.getFoundState().getAddress());
        while (state2.getAddress() != this.freqData.getStats()) {
            state3.setAddress(state2.getAddress() - 6);
            State.ppmdSwap(state2, state3);
            state2.decAddress();
        }
        state3.setAddress(this.freqData.getStats());
        state3.incFreq(4);
        this.freqData.incSummFreq(4);
        int summFreq = this.freqData.getSummFreq() - state2.getFreq();
        int i = modelPPM.getOrderFall() != 0 ? 1 : 0;
        state2.setFreq((state2.getFreq() + i) >>> 1);
        this.freqData.setSummFreq(state2.getFreq());
        do {
            state2.incAddress();
            summFreq -= state2.getFreq();
            state2.setFreq((state2.getFreq() + i) >>> 1);
            this.freqData.incSummFreq(state2.getFreq());
            state3.setAddress(state2.getAddress() - 6);
            if (state2.getFreq() > state3.getFreq()) {
                state.setAddress(state2.getAddress());
                StateRef stateRef = new StateRef();
                stateRef.setValues(state);
                State state4 = new State(modelPPM.getHeap());
                State state5 = new State(modelPPM.getHeap());
                do {
                    state4.setAddress(state.getAddress() - 6);
                    state.setValues(state4);
                    state.decAddress();
                    state5.setAddress(state.getAddress() - 6);
                    if (state.getAddress() == this.freqData.getStats()) {
                        break;
                    }
                } while (stateRef.getFreq() > state5.getFreq());
                state.setValues(stateRef);
            }
            numStats2--;
        } while (numStats2 != 0);
        if (state2.getFreq() == 0) {
            do {
                numStats2++;
                state2.decAddress();
            } while (state2.getFreq() == 0);
            summFreq += numStats2;
            setNumStats(getNumStats() - numStats2);
            if (getNumStats() == 1) {
                StateRef stateRef2 = new StateRef();
                state3.setAddress(this.freqData.getStats());
                stateRef2.setValues(state3);
                do {
                    stateRef2.decFreq(stateRef2.getFreq() >>> 1);
                    summFreq >>>= 1;
                } while (summFreq > 1);
                modelPPM.getSubAlloc().freeUnits(this.freqData.getStats(), (numStats + 1) >>> 1);
                this.oneState.setValues(stateRef2);
                modelPPM.getFoundState().setAddress(this.oneState.getAddress());
                return;
            }
        }
        this.freqData.incSummFreq(summFreq - (summFreq >>> 1));
        int i2 = (numStats + 1) >>> 1;
        int numStats3 = (getNumStats() + 1) >>> 1;
        if (i2 != numStats3) {
            this.freqData.setStats(modelPPM.getSubAlloc().shrinkUnits(this.freqData.getStats(), i2, numStats3));
        }
        modelPPM.getFoundState().setAddress(this.freqData.getStats());
    }

    private int getArrayIndex(ModelPPM modelPPM, State state) {
        getTempPPMContext(modelPPM.getSubAlloc().getHeap()).setAddress(getSuffix());
        return modelPPM.getPrevSuccess() + 0 + modelPPM.getNS2BSIndx()[r0.getNumStats() - 1] + modelPPM.getHiBitsFlag() + (modelPPM.getHB2Flag()[state.getSymbol()] * 2) + ((modelPPM.getRunLength() >>> 26) & 32);
    }

    public void decodeBinSymbol(ModelPPM modelPPM) {
        State stateInit = this.tempState1.init(modelPPM.getHeap());
        stateInit.setAddress(this.oneState.getAddress());
        modelPPM.setHiBitsFlag(modelPPM.getHB2Flag()[modelPPM.getFoundState().getSymbol()]);
        int freq = stateInit.getFreq() - 1;
        int arrayIndex = getArrayIndex(modelPPM, stateInit);
        int i = modelPPM.getBinSumm()[freq][arrayIndex];
        long j = i;
        if (modelPPM.getCoder().getCurrentShiftCount(14) < j) {
            modelPPM.getFoundState().setAddress(stateInit.getAddress());
            stateInit.incFreq(stateInit.getFreq() < 128 ? 1 : 0);
            modelPPM.getCoder().getSubRange().setLowCount(0L);
            modelPPM.getCoder().getSubRange().setHighCount(j);
            modelPPM.getBinSumm()[freq][arrayIndex] = ((i + 128) - getMean(i, 7, 2)) & 65535;
            modelPPM.setPrevSuccess(1);
            modelPPM.incRunLength(1);
            return;
        }
        modelPPM.getCoder().getSubRange().setLowCount(j);
        int mean = (i - getMean(i, 7, 2)) & 65535;
        modelPPM.getBinSumm()[freq][arrayIndex] = mean;
        modelPPM.getCoder().getSubRange().setHighCount(Trace.TRACE_TAG_DALVIK);
        modelPPM.setInitEsc(ExpEscape[mean >>> 10]);
        modelPPM.setNumMasked(1);
        modelPPM.getCharMask()[stateInit.getSymbol()] = modelPPM.getEscCount();
        modelPPM.setPrevSuccess(0);
        modelPPM.getFoundState().setAddress(0);
    }

    public void update1(ModelPPM modelPPM, int i) {
        modelPPM.getFoundState().setAddress(i);
        modelPPM.getFoundState().incFreq(4);
        this.freqData.incSummFreq(4);
        State stateInit = this.tempState3.init(modelPPM.getHeap());
        State stateInit2 = this.tempState4.init(modelPPM.getHeap());
        stateInit.setAddress(i);
        stateInit2.setAddress(i - 6);
        if (stateInit.getFreq() > stateInit2.getFreq()) {
            State.ppmdSwap(stateInit, stateInit2);
            modelPPM.getFoundState().setAddress(stateInit2.getAddress());
            if (stateInit2.getFreq() > 124) {
                rescale(modelPPM);
            }
        }
    }

    public boolean decodeSymbol2(ModelPPM modelPPM) {
        long j;
        int numStats = getNumStats() - modelPPM.getNumMasked();
        SEE2Context sEE2ContextMakeEscFreq2 = makeEscFreq2(modelPPM, numStats);
        RangeCoder coder = modelPPM.getCoder();
        State stateInit = this.tempState1.init(modelPPM.getHeap());
        State stateInit2 = this.tempState2.init(modelPPM.getHeap());
        stateInit.setAddress(this.freqData.getStats() - 6);
        int freq = 0;
        int freq2 = 0;
        int i = 0;
        while (true) {
            stateInit.incAddress();
            if (modelPPM.getCharMask()[stateInit.getSymbol()] != modelPPM.getEscCount()) {
                freq2 += stateInit.getFreq();
                int i2 = i + 1;
                this.ps[i] = stateInit.getAddress();
                numStats--;
                if (numStats == 0) {
                    break;
                }
                i = i2;
            }
        }
        coder.getSubRange().incScale(freq2);
        long currentCount = coder.getCurrentCount();
        if (currentCount >= coder.getSubRange().getScale()) {
            return false;
        }
        stateInit.setAddress(this.ps[0]);
        long j2 = freq2;
        if (currentCount < j2) {
            int i3 = 0;
            while (true) {
                freq += stateInit.getFreq();
                j = freq;
                if (j > currentCount) {
                    break;
                }
                i3++;
                stateInit.setAddress(this.ps[i3]);
            }
            coder.getSubRange().setHighCount(j);
            coder.getSubRange().setLowCount(freq - stateInit.getFreq());
            sEE2ContextMakeEscFreq2.update();
            update2(modelPPM, stateInit.getAddress());
        } else {
            coder.getSubRange().setLowCount(j2);
            coder.getSubRange().setHighCount(coder.getSubRange().getScale());
            int numStats2 = getNumStats() - modelPPM.getNumMasked();
            int i4 = -1;
            do {
                i4++;
                stateInit2.setAddress(this.ps[i4]);
                modelPPM.getCharMask()[stateInit2.getSymbol()] = modelPPM.getEscCount();
                numStats2--;
            } while (numStats2 != 0);
            sEE2ContextMakeEscFreq2.incSumm((int) coder.getSubRange().getScale());
            modelPPM.setNumMasked(getNumStats());
        }
        return true;
    }

    public void update2(ModelPPM modelPPM, int i) {
        State stateInit = this.tempState5.init(modelPPM.getHeap());
        stateInit.setAddress(i);
        modelPPM.getFoundState().setAddress(i);
        modelPPM.getFoundState().incFreq(4);
        this.freqData.incSummFreq(4);
        if (stateInit.getFreq() > 124) {
            rescale(modelPPM);
        }
        modelPPM.incEscCount(1);
        modelPPM.setRunLength(modelPPM.getInitRL());
    }

    private SEE2Context makeEscFreq2(ModelPPM modelPPM, int i) {
        int numStats = getNumStats();
        if (numStats != 256) {
            PPMContext tempPPMContext = getTempPPMContext(modelPPM.getHeap());
            tempPPMContext.setAddress(getSuffix());
            SEE2Context sEE2Context = modelPPM.getSEE2Cont()[modelPPM.getNS2Indx()[i - 1]][(i < tempPPMContext.getNumStats() - numStats ? 1 : 0) + 0 + ((this.freqData.getSummFreq() < numStats * 11 ? 1 : 0) * 2) + ((modelPPM.getNumMasked() > i ? 1 : 0) * 4) + modelPPM.getHiBitsFlag()];
            modelPPM.getCoder().getSubRange().setScale(sEE2Context.getMean());
            return sEE2Context;
        }
        SEE2Context dummySEE2Cont = modelPPM.getDummySEE2Cont();
        modelPPM.getCoder().getSubRange().setScale(1L);
        return dummySEE2Cont;
    }

    public boolean decodeSymbol1(ModelPPM modelPPM) {
        long j;
        RangeCoder coder = modelPPM.getCoder();
        coder.getSubRange().setScale(this.freqData.getSummFreq());
        State state = new State(modelPPM.getHeap());
        state.setAddress(this.freqData.getStats());
        long currentCount = coder.getCurrentCount();
        if (currentCount >= coder.getSubRange().getScale()) {
            return false;
        }
        int freq = state.getFreq();
        long j2 = freq;
        if (currentCount < j2) {
            coder.getSubRange().setHighCount(j2);
            modelPPM.setPrevSuccess(((long) (freq * 2)) > coder.getSubRange().getScale() ? 1 : 0);
            modelPPM.incRunLength(modelPPM.getPrevSuccess());
            int i = freq + 4;
            modelPPM.getFoundState().setAddress(state.getAddress());
            modelPPM.getFoundState().setFreq(i);
            this.freqData.incSummFreq(4);
            if (i > 124) {
                rescale(modelPPM);
            }
            coder.getSubRange().setLowCount(0L);
            return true;
        }
        if (modelPPM.getFoundState().getAddress() == 0) {
            return false;
        }
        modelPPM.setPrevSuccess(0);
        int numStats = getNumStats();
        int i2 = numStats - 1;
        int i3 = i2;
        do {
            freq += state.incAddress().getFreq();
            j = freq;
            if (j > currentCount) {
                coder.getSubRange().setLowCount(freq - state.getFreq());
                coder.getSubRange().setHighCount(j);
                update1(modelPPM, state.getAddress());
                return true;
            }
            i3--;
        } while (i3 != 0);
        modelPPM.setHiBitsFlag(modelPPM.getHB2Flag()[modelPPM.getFoundState().getSymbol()]);
        coder.getSubRange().setLowCount(j);
        modelPPM.getCharMask()[state.getSymbol()] = modelPPM.getEscCount();
        modelPPM.setNumMasked(numStats);
        modelPPM.getFoundState().setAddress(0);
        do {
            modelPPM.getCharMask()[state.decAddress().getSymbol()] = modelPPM.getEscCount();
            i2--;
        } while (i2 != 0);
        coder.getSubRange().setHighCount(coder.getSubRange().getScale());
        return true;
    }

    public String toString() {
        return "PPMContext[\n  pos=" + this.pos + "\n  size=" + size + "\n  numStats=" + getNumStats() + "\n  Suffix=" + getSuffix() + "\n  freqData=" + this.freqData + "\n  oneState=" + this.oneState + "\n]";
    }
}
