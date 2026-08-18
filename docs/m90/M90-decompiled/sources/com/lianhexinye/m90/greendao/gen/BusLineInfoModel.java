package com.lianhexinye.m90.greendao.gen;

import com.lianhexinye.m90.mvp.BaseModel;

/* JADX INFO: loaded from: classes2.dex */
public class BusLineInfoModel extends BaseModel {
    private Long _id;
    private int attribute;
    private String fileFormat;
    private String filePath;
    private boolean iSelect;
    private String lineName;
    private int lineNo;
    private String lineNumber;
    private String strSelect;

    public BusLineInfoModel(Long l, int i, String str, boolean z, int i2, String str2, String str3, String str4) {
        this._id = l;
        this.lineNo = i;
        this.lineName = str;
        this.iSelect = z;
        this.attribute = i2;
        this.lineNumber = str2;
        this.filePath = str3;
        this.fileFormat = str4;
    }

    public BusLineInfoModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public int getLineNo() {
        return this.lineNo;
    }

    public void setLineNo(int i) {
        this.lineNo = i;
    }

    public String getLineName() {
        return this.lineName;
    }

    public void setLineName(String str) {
        this.lineName = str;
    }

    public boolean getISelect() {
        return this.iSelect;
    }

    public void setISelect(boolean z) {
        this.iSelect = z;
    }

    public int getAttribute() {
        return this.attribute;
    }

    public void setAttribute(int i) {
        this.attribute = i;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public String getFileFormat() {
        return this.fileFormat;
    }

    public void setFileFormat(String str) {
        this.fileFormat = str;
    }

    public String getStrSelect() {
        return this.strSelect;
    }

    public void setStrSelect(String str) {
        this.strSelect = str;
    }

    public String getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(String str) {
        this.lineNumber = str;
    }
}
