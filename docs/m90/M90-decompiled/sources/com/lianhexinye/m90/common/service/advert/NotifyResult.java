package com.lianhexinye.m90.common.service.advert;

import android.text.format.DateFormat;

/* JADX INFO: loaded from: classes2.dex */
public class NotifyResult {
    private String content;
    private int contentId;
    private String message;
    private int messageType;
    private int nofityId;
    private int result;

    public int getResult() {
        return this.result;
    }

    public void setResult(int i) {
        this.result = i;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public int getMessageType() {
        return this.messageType;
    }

    public void setMessageType(int i) {
        this.messageType = i;
    }

    public int getNofityId() {
        return this.nofityId;
    }

    public void setNofityId(int i) {
        this.nofityId = i;
    }

    public int getContentId() {
        return this.contentId;
    }

    public void setContentId(int i) {
        this.contentId = i;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public String toString() {
        return "NotifyResult{result=" + this.result + ", message='" + this.message + DateFormat.QUOTE + ", messageType=" + this.messageType + ", nofityId=" + this.nofityId + ", contentId=" + this.contentId + ", content='" + this.content + DateFormat.QUOTE + '}';
    }
}
