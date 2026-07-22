package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.PrintSetupRecord;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFPrintSetup {
    public static final short A4_PAPERSIZE = 9;
    public static final short A5_PAPERSIZE = 11;
    public static final short ENVELOPE_10_PAPERSIZE = 20;
    public static final short ENVELOPE_CS_PAPERSIZE = 28;
    public static final short ENVELOPE_DL_PAPERSIZE = 27;
    public static final short ENVELOPE_MONARCH_PAPERSIZE = 37;
    public static final short EXECUTIVE_PAPERSIZE = 7;
    public static final short LEGAL_PAPERSIZE = 5;
    public static final short LETTER_PAPERSIZE = 1;
    public final short A2_PAPERSIZE = 66;
    public final short A3_PAPERSIZE = 8;
    public final short A6_PAPERSIZE = 70;
    public final short B4_PAPERSIZE = 32;
    public final short B5_PAPERSIZE = 34;
    public final short B6_PAPERSIZE = 35;
    PrintSetupRecord printSetupRecord;

    protected HSSFPrintSetup(PrintSetupRecord printSetupRecord) {
        this.printSetupRecord = printSetupRecord;
    }

    public void setPaperSize(short s) {
        this.printSetupRecord.setPaperSize(s);
    }

    public void setScale(short s) {
        this.printSetupRecord.setScale(s);
    }

    public void setPageStart(short s) {
        this.printSetupRecord.setPageStart(s);
    }

    public void setFitWidth(short s) {
        this.printSetupRecord.setFitWidth(s);
    }

    public void setFitHeight(short s) {
        this.printSetupRecord.setFitHeight(s);
    }

    public void setOptions(short s) {
        this.printSetupRecord.setOptions(s);
    }

    public void setLeftToRight(boolean z) {
        this.printSetupRecord.setLeftToRight(z);
    }

    public void setLandscape(boolean z) {
        this.printSetupRecord.setLandscape(!z);
    }

    public void setValidSettings(boolean z) {
        this.printSetupRecord.setValidSettings(z);
    }

    public void setNoColor(boolean z) {
        this.printSetupRecord.setNoColor(z);
    }

    public void setDraft(boolean z) {
        this.printSetupRecord.setDraft(z);
    }

    public void setNotes(boolean z) {
        this.printSetupRecord.setNotes(z);
    }

    public void setNoOrientation(boolean z) {
        this.printSetupRecord.setNoOrientation(z);
    }

    public void setUsePage(boolean z) {
        this.printSetupRecord.setUsePage(z);
    }

    public void setHResolution(short s) {
        this.printSetupRecord.setHResolution(s);
    }

    public void setVResolution(short s) {
        this.printSetupRecord.setVResolution(s);
    }

    public void setHeaderMargin(double d) {
        this.printSetupRecord.setHeaderMargin(d);
    }

    public void setFooterMargin(double d) {
        this.printSetupRecord.setFooterMargin(d);
    }

    public void setCopies(short s) {
        this.printSetupRecord.setCopies(s);
    }

    public short getPaperSize() {
        return this.printSetupRecord.getPaperSize();
    }

    public short getScale() {
        return this.printSetupRecord.getScale();
    }

    public short getPageStart() {
        return this.printSetupRecord.getPageStart();
    }

    public short getFitWidth() {
        return this.printSetupRecord.getFitWidth();
    }

    public short getFitHeight() {
        return this.printSetupRecord.getFitHeight();
    }

    public short getOptions() {
        return this.printSetupRecord.getOptions();
    }

    public boolean getLeftToRight() {
        return this.printSetupRecord.getLeftToRight();
    }

    public boolean getLandscape() {
        return !this.printSetupRecord.getLandscape();
    }

    public boolean getValidSettings() {
        return this.printSetupRecord.getValidSettings();
    }

    public boolean getNoColor() {
        return this.printSetupRecord.getNoColor();
    }

    public boolean getDraft() {
        return this.printSetupRecord.getDraft();
    }

    public boolean getNotes() {
        return this.printSetupRecord.getNotes();
    }

    public boolean getNoOrientation() {
        return this.printSetupRecord.getNoOrientation();
    }

    public boolean getUsePage() {
        return this.printSetupRecord.getUsePage();
    }

    public short getHResolution() {
        return this.printSetupRecord.getHResolution();
    }

    public short getVResolution() {
        return this.printSetupRecord.getVResolution();
    }

    public double getHeaderMargin() {
        return this.printSetupRecord.getHeaderMargin();
    }

    public double getFooterMargin() {
        return this.printSetupRecord.getFooterMargin();
    }

    public short getCopies() {
        return this.printSetupRecord.getCopies();
    }
}
