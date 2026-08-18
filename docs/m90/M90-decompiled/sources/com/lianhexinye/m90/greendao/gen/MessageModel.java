package com.lianhexinye.m90.greendao.gen;

import com.lianhexinye.m90.mvp.BaseModel;

/* JADX INFO: loaded from: classes2.dex */
public class MessageModel extends BaseModel {
    private Long _id;
    private String fileFormat;
    private String filePath;
    private boolean isFinally;
    private String messageContent;
    private int messageNo;
    private String messageTime;

    public MessageModel(Long l, int i, String str, String str2, String str3, String str4) {
        this._id = l;
        this.messageNo = i;
        this.messageTime = str;
        this.messageContent = str2;
        this.filePath = str3;
        this.fileFormat = str4;
    }

    public MessageModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public int getMessageNo() {
        return this.messageNo;
    }

    public void setMessageNo(int i) {
        this.messageNo = i;
    }

    public String getMessageTime() {
        return this.messageTime;
    }

    public void setMessageTime(String str) {
        this.messageTime = str;
    }

    public String getMessageContent() {
        return this.messageContent;
    }

    public void setMessageContent(String str) {
        this.messageContent = str;
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

    public boolean isFinally() {
        return this.isFinally;
    }

    public void setFinally(boolean z) {
        this.isFinally = z;
    }
}
