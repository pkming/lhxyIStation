package com.lianhexinye.m90.retrofit.request;

/* JADX INFO: loaded from: classes2.dex */
public class RequestBody<T> {
    private T data;
    private String sign;

    public T getData() {
        return this.data;
    }

    public void setData(T t) {
        this.data = t;
    }

    public String getSign() {
        return this.sign;
    }

    public void setSign(String str) {
        this.sign = str;
    }
}
