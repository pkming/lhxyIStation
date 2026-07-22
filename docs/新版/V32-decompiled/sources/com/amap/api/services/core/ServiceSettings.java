package com.amap.api.services.core;

import android.content.Context;
import android.view.Window;
import com.amap.api.col.p0003sl.fo;
import com.amap.api.col.p0003sl.fp;
import com.amap.api.col.p0003sl.gz;
import com.amap.api.col.p0003sl.ii;
import com.amap.api.col.p0003sl.im;
import com.amap.api.col.p0003sl.ip;

/* JADX INFO: loaded from: classes2.dex */
public class ServiceSettings {
    public static final String CHINESE = "zh-CN";
    public static final String ENGLISH = "en";
    public static final int HTTP = 1;
    public static final int HTTPS = 2;
    private static ServiceSettings c;
    private String a = "zh-CN";
    private int b = 1;
    private int d = 20000;
    private int e = 20000;

    public int getConnectionTimeOut() {
        return this.d;
    }

    public int getSoTimeOut() {
        return this.e;
    }

    public void setConnectionTimeOut(int i) {
        if (i < 5000) {
            this.d = 5000;
        } else if (i > 30000) {
            this.d = Window.PROGRESS_SECONDARY_END;
        } else {
            this.d = i;
        }
    }

    public void setSoTimeOut(int i) {
        if (i < 5000) {
            this.e = 5000;
        } else if (i > 30000) {
            this.e = Window.PROGRESS_SECONDARY_END;
        } else {
            this.e = i;
        }
    }

    private ServiceSettings() {
    }

    public static ServiceSettings getInstance() {
        if (c == null) {
            c = new ServiceSettings();
        }
        return c;
    }

    public void setLanguage(String str) {
        this.a = str;
    }

    public void setProtocol(int i) {
        this.b = i;
        im.a().a(this.b == 2);
    }

    public String getLanguage() {
        return this.a;
    }

    public int getProtocol() {
        return this.b;
    }

    public void setApiKey(String str) {
        ii.a(str);
    }

    public void destroyInnerAsynThreadPool() {
        try {
            gz.b();
        } catch (Throwable th) {
            fp.a(th, "ServiceSettings", "destroyInnerAsynThreadPool");
        }
    }

    public static synchronized void updatePrivacyShow(Context context, boolean z, boolean z2) {
        ip.a(context, z, z2, fo.a(false));
    }

    public static synchronized void updatePrivacyAgree(Context context, boolean z) {
        ip.a(context, z, fo.a(false));
    }
}
