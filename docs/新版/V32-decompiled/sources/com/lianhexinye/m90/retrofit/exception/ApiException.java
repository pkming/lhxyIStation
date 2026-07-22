package com.lianhexinye.m90.retrofit.exception;

/* JADX INFO: loaded from: classes2.dex */
public class ApiException extends Exception {
    private int code;
    private String displayMessage;

    public ApiException(Throwable th, int i) {
        super(th);
        this.code = i;
    }

    public void setDisplayMessage(String str) {
        this.displayMessage = str;
    }

    public String getDisplayMessage() {
        return this.displayMessage;
    }

    public int getCode() {
        return this.code;
    }
}
