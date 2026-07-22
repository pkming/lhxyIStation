package org.apache.poi.hssf.record.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public abstract class OBJBiffElement extends SeriesBiffElement {
    public static int CHART = 5;
    public static int PICTURE = 8;
    private int cbMacro;
    private int colL;
    private int colR;
    private int dxL;
    private int dxR;
    private int dyB;
    private int dyT;
    private int grbit;
    private int objectType;
    private int rwB;
    private int rwT;

    protected OBJBiffElement(int i, int i2) {
        super(i, i2);
        this.cbMacro = 0;
        this.grbit = 1558;
    }

    public void setRowTop(int i) {
        this.rwT = i;
    }

    public void setRowBottom(int i) {
        this.rwB = i;
    }

    public void setColLeft(int i) {
        this.colL = i;
    }

    public void setColRight(int i) {
        this.colR = i;
    }

    public void setDxL(int i) {
        this.dxL = i;
    }

    public void setDxR(int i) {
        this.dxR = i;
    }

    public void setDyT(int i) {
        this.dyT = i;
    }

    public void setDyB(int i) {
        this.dyB = i;
    }

    public void setGrbit(int i) {
        this.grbit = i;
    }

    public void setObjectType(int i) {
        this.objectType = i;
    }

    public int getRowTop() {
        return this.rwT;
    }

    public int getRowBottom() {
        return this.rwB;
    }

    public int getColLeft() {
        return this.colL;
    }

    public int getColRight() {
        return this.colR;
    }

    public int getDxL() {
        return this.dxL;
    }

    public int getDxR() {
        return this.dxR;
    }

    public int getDyT() {
        return this.dyT;
    }

    public int getDyB() {
        return this.dyB;
    }

    public int getGrbit() {
        return this.grbit;
    }

    public int getObjectType() {
        return this.objectType;
    }

    protected byte[] toHeadBinary() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStreamLfirst dataOutputStreamLfirst = new DataOutputStreamLfirst(byteArrayOutputStream);
        dataOutputStreamLfirst.writeInt(getIndex());
        dataOutputStreamLfirst.writeShort(this.objectType);
        dataOutputStreamLfirst.writeShort(getIndex());
        dataOutputStreamLfirst.writeShort(this.grbit);
        dataOutputStreamLfirst.writeShort(this.colL);
        dataOutputStreamLfirst.writeShort(this.dxL);
        dataOutputStreamLfirst.writeShort(this.rwT);
        dataOutputStreamLfirst.writeShort(this.dyT);
        dataOutputStreamLfirst.writeShort(this.colR);
        dataOutputStreamLfirst.writeShort(this.dxR);
        dataOutputStreamLfirst.writeShort(this.rwB);
        dataOutputStreamLfirst.writeShort(this.dyB);
        dataOutputStreamLfirst.writeShort(this.cbMacro);
        dataOutputStreamLfirst.writeInt(0);
        dataOutputStreamLfirst.writeShort(0);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        dataOutputStreamLfirst.close();
        byteArrayOutputStream.close();
        return byteArray;
    }
}
