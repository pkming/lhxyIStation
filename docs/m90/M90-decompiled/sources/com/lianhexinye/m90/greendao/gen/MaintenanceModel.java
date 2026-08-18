package com.lianhexinye.m90.greendao.gen;

import com.lianhexinye.m90.mvp.BaseModel;

/* JADX INFO: loaded from: classes2.dex */
public class MaintenanceModel extends BaseModel {
    private Long _id;
    private String content;
    private String fileFormat;
    private String filePath;
    private boolean isChecked;
    private int mId;
    private String remarks;

    public MaintenanceModel(Long l, int i, String str, String str2, String str3, String str4) {
        this._id = l;
        this.mId = i;
        this.content = str;
        this.remarks = str2;
        this.filePath = str3;
        this.fileFormat = str4;
    }

    public MaintenanceModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public int getMId() {
        return this.mId;
    }

    public void setMId(int i) {
        this.mId = i;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String str) {
        this.content = str;
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

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String str) {
        this.remarks = str;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }
}
