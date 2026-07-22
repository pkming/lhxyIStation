package de.innosystec.unrar.unpack.vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/* JADX INFO: loaded from: classes2.dex */
public class VMPreparedProgram {
    private List<VMPreparedCommand> AltCmd;
    private int CmdCount;
    private int FilteredDataOffset;
    private int FilteredDataSize;
    private List<VMPreparedCommand> Cmd = new ArrayList();
    private Vector<Byte> GlobalData = new Vector<>();
    private Vector<Byte> StaticData = new Vector<>();
    private int[] InitR = new int[7];

    public VMPreparedProgram() {
        this.AltCmd = new ArrayList();
        this.AltCmd = null;
    }

    public List<VMPreparedCommand> getAltCmd() {
        return this.AltCmd;
    }

    public void setAltCmd(List<VMPreparedCommand> list) {
        this.AltCmd = list;
    }

    public List<VMPreparedCommand> getCmd() {
        return this.Cmd;
    }

    public void setCmd(List<VMPreparedCommand> list) {
        this.Cmd = list;
    }

    public int getCmdCount() {
        return this.CmdCount;
    }

    public void setCmdCount(int i) {
        this.CmdCount = i;
    }

    public int getFilteredDataOffset() {
        return this.FilteredDataOffset;
    }

    public void setFilteredDataOffset(int i) {
        this.FilteredDataOffset = i;
    }

    public int getFilteredDataSize() {
        return this.FilteredDataSize;
    }

    public void setFilteredDataSize(int i) {
        this.FilteredDataSize = i;
    }

    public Vector<Byte> getGlobalData() {
        return this.GlobalData;
    }

    public void setGlobalData(Vector<Byte> vector) {
        this.GlobalData = vector;
    }

    public int[] getInitR() {
        return this.InitR;
    }

    public void setInitR(int[] iArr) {
        this.InitR = iArr;
    }

    public Vector<Byte> getStaticData() {
        return this.StaticData;
    }

    public void setStaticData(Vector<Byte> vector) {
        this.StaticData = vector;
    }
}
