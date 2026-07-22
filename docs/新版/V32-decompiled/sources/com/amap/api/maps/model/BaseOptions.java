package com.amap.api.maps.model;

/* JADX INFO: loaded from: classes2.dex */
public class BaseOptions {
    protected Object Field1;
    protected Object Field2;
    protected String type;

    protected BaseUpdateFlags getUpdateFlags() {
        return null;
    }

    protected Object method1(Object... objArr) {
        return null;
    }

    protected Object method2(Object... objArr) {
        return null;
    }

    public void resetUpdateFlags() {
    }

    public static class BaseUpdateFlags {
        protected boolean zIndexUpdate = false;

        public void reset() {
            this.zIndexUpdate = false;
        }
    }
}
