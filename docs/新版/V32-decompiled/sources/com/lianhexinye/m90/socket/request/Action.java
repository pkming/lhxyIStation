package com.lianhexinye.m90.socket.request;

/* JADX INFO: loaded from: classes2.dex */
public enum Action {
    HEARTBEAT("heartbeat", "heartbeat", null);

    private String action;
    private String reqEvent;
    private Class respClazz;

    Action(String str, String str2, Class cls) {
        this.action = str;
        this.reqEvent = str2;
        this.respClazz = cls;
    }

    public String getAction() {
        return this.action;
    }

    public String getReqEvent() {
        return this.reqEvent;
    }

    public Class getRespClazz() {
        return this.respClazz;
    }
}
