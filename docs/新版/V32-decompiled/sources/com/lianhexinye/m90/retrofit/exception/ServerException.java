package com.lianhexinye.m90.retrofit.exception;

/* JADX INFO: loaded from: classes2.dex */
public class ServerException extends RuntimeException {
    private String code;
    private String msg;

    public ServerException(String str, String str2) {
        this.code = str;
        this.msg = str2;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
