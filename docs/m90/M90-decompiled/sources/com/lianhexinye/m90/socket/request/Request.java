package com.lianhexinye.m90.socket.request;

/* JADX INFO: loaded from: classes2.dex */
public class Request<T> {
    private transient String action;
    private T req;
    private transient int reqCount;
    private transient int reqEvent;
    private transient long seqId;

    public Request() {
    }

    public Request(String str, int i, long j, T t, int i2) {
        this.action = str;
        this.reqEvent = i;
        this.seqId = j;
        this.req = t;
        this.reqCount = i2;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String str) {
        this.action = str;
    }

    public int getReqEvent() {
        return this.reqEvent;
    }

    public void setReqEvent(int i) {
        this.reqEvent = i;
    }

    public long getSeqId() {
        return this.seqId;
    }

    public void setSeqId(long j) {
        this.seqId = j;
    }

    public T getReq() {
        return this.req;
    }

    public void setReq(T t) {
        this.req = t;
    }

    public int getReqCount() {
        return this.reqCount;
    }

    public void setReqCount(int i) {
        this.reqCount = i;
    }

    public static class Builder<T> {
        private String action;
        private String channel;
        private boolean isZip;
        private T req;
        private int reqCount;
        private int reqEvent;
        private long seqId;

        public Builder action(String str) {
            this.action = str;
            return this;
        }

        public Builder reqEvent(int i) {
            this.reqEvent = i;
            return this;
        }

        public Builder seqId(long j) {
            this.seqId = j;
            return this;
        }

        public Builder req(T t) {
            this.req = t;
            return this;
        }

        public Builder reqCount(int i) {
            this.reqCount = i;
            return this;
        }

        public Request build() {
            return new Request(this.action, this.reqEvent, this.seqId, this.req, this.reqCount);
        }
    }
}
