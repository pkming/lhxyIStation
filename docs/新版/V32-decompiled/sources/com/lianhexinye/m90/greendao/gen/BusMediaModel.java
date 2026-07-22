package com.lianhexinye.m90.greendao.gen;

import com.lianhexinye.m90.mvp.BaseModel;

/* JADX INFO: loaded from: classes2.dex */
public class BusMediaModel extends BaseModel {
    private Long _id;
    private int contentType;
    private int dataState;
    private int downloadState;
    private String downloadUrls;
    private int fileResult;
    private int programId;

    public BusMediaModel(Long l, String str, int i, int i2, int i3, int i4, int i5) {
        this._id = l;
        this.downloadUrls = str;
        this.downloadState = i;
        this.fileResult = i2;
        this.dataState = i3;
        this.programId = i4;
        this.contentType = i5;
    }

    public BusMediaModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public String getDownloadUrls() {
        return this.downloadUrls;
    }

    public void setDownloadUrls(String str) {
        this.downloadUrls = str;
    }

    public int getDownloadState() {
        return this.downloadState;
    }

    public void setDownloadState(int i) {
        this.downloadState = i;
    }

    public int getProgramId() {
        return this.programId;
    }

    public void setProgramId(int i) {
        this.programId = i;
    }

    public int getContentType() {
        return this.contentType;
    }

    public void setContentType(int i) {
        this.contentType = i;
    }

    public int getFileResult() {
        return this.fileResult;
    }

    public void setFileResult(int i) {
        this.fileResult = i;
    }

    public int getDataState() {
        return this.dataState;
    }

    public void setDataState(int i) {
        this.dataState = i;
    }
}
