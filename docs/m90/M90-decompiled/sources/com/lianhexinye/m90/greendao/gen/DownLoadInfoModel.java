package com.lianhexinye.m90.greendao.gen;

/* JADX INFO: loaded from: classes2.dex */
public class DownLoadInfoModel {
    private Long _id;
    private Long compelete_size;
    private Long end_pos;
    private String file_createtime;
    private Long start_pos;
    private int thread_id;
    private String url;

    public DownLoadInfoModel(Long l, int i, Long l2, Long l3, Long l4, String str, String str2) {
        this._id = l;
        this.thread_id = i;
        this.start_pos = l2;
        this.end_pos = l3;
        this.compelete_size = l4;
        this.url = str;
        this.file_createtime = str2;
    }

    public DownLoadInfoModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public int getThread_id() {
        return this.thread_id;
    }

    public void setThread_id(int i) {
        this.thread_id = i;
    }

    public Long getStart_pos() {
        return this.start_pos;
    }

    public void setStart_pos(Long l) {
        this.start_pos = l;
    }

    public Long getEnd_pos() {
        return this.end_pos;
    }

    public void setEnd_pos(Long l) {
        this.end_pos = l;
    }

    public Long getCompelete_size() {
        return this.compelete_size;
    }

    public void setCompelete_size(Long l) {
        this.compelete_size = l;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public String getFile_createtime() {
        return this.file_createtime;
    }

    public void setFile_createtime(String str) {
        this.file_createtime = str;
    }
}
