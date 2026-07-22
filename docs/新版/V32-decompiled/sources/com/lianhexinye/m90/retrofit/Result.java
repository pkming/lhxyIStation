package com.lianhexinye.m90.retrofit;

import android.text.format.DateFormat;

/* JADX INFO: loaded from: classes2.dex */
public class Result<T> {
    private String $id;
    private String contentId;
    private T data;
    private String message;
    private String messageType;
    private String nofityId;
    private String result;

    public String get$id() {
        return this.$id;
    }

    public void set$id(String str) {
        this.$id = str;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String str) {
        this.result = str;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String str) {
        this.messageType = str;
    }

    public String getNofityId() {
        return this.nofityId;
    }

    public void setNofityId(String str) {
        this.nofityId = str;
    }

    public String getContentId() {
        return this.contentId;
    }

    public void setContentId(String str) {
        this.contentId = str;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T t) {
        this.data = t;
    }

    public String toString() {
        return "Result{result='" + this.result + DateFormat.QUOTE + ", message='" + this.message + DateFormat.QUOTE + ", messageType='" + this.messageType + DateFormat.QUOTE + ", nofityId='" + this.nofityId + DateFormat.QUOTE + ", contentId='" + this.contentId + DateFormat.QUOTE + ", data=" + this.data + '}';
    }
}
