package com.unisound.common;

import android.text.TextUtils;
import com.unisound.client.ErrorCode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class ad {
    public static String a = "http://10.30.2.13:8089/data-process-service/rtc";
    private static int b = 6;
    private List<ac> c = new ArrayList();
    private com.unisound.sdk.an d;

    private int b() {
        for (int i = 1; i < b; i++) {
            if (b(i) == null) {
                return i;
            }
        }
        return -1;
    }

    private ac b(int i) {
        if (i == -1) {
            return null;
        }
        for (ac acVar : this.c) {
            if (acVar.c() == i) {
                return acVar;
            }
        }
        return null;
    }

    private ac c(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ac acVarA = a(str);
        if (acVarA != null) {
            return acVarA;
        }
        int iB = b();
        if (iB == -1) {
            return null;
        }
        ac acVar = new ac(iB, str);
        this.c.add(acVar);
        return acVar;
    }

    public int a() {
        return this.c.size();
    }

    public ac a(int i) {
        if (this.c.size() <= i || i <= -1) {
            return null;
        }
        return this.c.get(i);
    }

    public ac a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        for (ac acVar : this.c) {
            if (acVar.b().equals(str)) {
                return acVar;
            }
        }
        return null;
    }

    public ap a(String str, List<String> list, ae aeVar) {
        ap apVar = new ap();
        apVar.a(a);
        ac acVarA = a(str);
        ErrorCode errorCode = new ErrorCode();
        if (acVarA == null) {
            if (a() >= b) {
                aeVar.a(apVar, errorCode.createProfessionError(ErrorCode.UPLOAD_SCENE_OUT_MAX_COUNT));
                return apVar;
            }
            acVarA = c(str);
            if (acVarA == null) {
                aeVar.a(apVar, errorCode.createProfessionError(ErrorCode.UPLOAD_SCENE_OUT_MAX_COUNT));
                return apVar;
            }
        }
        apVar.a(this.d.ab(), acVarA, list);
        apVar.a(aeVar);
        return apVar;
    }

    public void a(ac acVar) {
        Iterator<ac> it = this.c.iterator();
        while (it.hasNext()) {
            if (it.next() == acVar) {
                this.c.remove(acVar);
            }
        }
    }

    public void a(com.unisound.sdk.an anVar) {
        this.d = anVar;
    }

    public void a(String str, int i) {
        a = "http://" + str + ":" + i + "/data-process-service/rtc";
    }

    public void b(String str) {
        ac acVarA = a(str);
        if (acVarA != null) {
            a(acVarA);
        }
    }
}
