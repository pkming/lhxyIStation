package com.autonavi.base.amap.mapcore;

import android.text.format.DateFormat;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class NativeFunctionRunnable {
    public static final String CLEAR_OVERLAY_FUNC = "clearOverlay";
    public static final String CREATE_OVERLAY_FUNC = "createOverlay";
    public static final String REMOVE_OVERLAY_FUNC = "removeOverlay";
    public static final String UPDATE_OPTIONS_FUNC = "updateOptions";
    private String name;
    private Runnable runnable;
    private String type;

    public NativeFunctionRunnable(String str, String str2, Runnable runnable) {
        this.type = str;
        this.name = str2;
        this.runnable = runnable;
    }

    public String getKey() {
        return this.type + "_" + this.name;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NativeFunctionRunnable nativeFunctionRunnable = (NativeFunctionRunnable) obj;
        if (Objects.equals(this.type, nativeFunctionRunnable.type)) {
            return Objects.equals(this.name, nativeFunctionRunnable.name);
        }
        return false;
    }

    public int hashCode() {
        String str = this.type;
        int iHashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.name;
        return iHashCode + (str2 != null ? str2.hashCode() : 0);
    }

    public String toString() {
        return "NativeFunctionRunnable{type='" + this.type + DateFormat.QUOTE + ", name='" + this.name + DateFormat.QUOTE + ", runnable=" + this.runnable + '}';
    }
}
