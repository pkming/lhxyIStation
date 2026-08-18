package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;

/* JADX INFO: compiled from: AutoTCommonParam.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fc {
    private Context a;

    private static String b() {
        return "off";
    }

    private static String c() {
        return "ANDA0605000";
    }

    private static String d() {
        return "";
    }

    private static String f() {
        return "9.7.1";
    }

    private static String g() {
        return "";
    }

    private static String i() {
        return "";
    }

    private static String j() {
        return "";
    }

    private static String k() {
        return "";
    }

    private static String l() {
        return "";
    }

    private static String m() {
        return "";
    }

    private static String n() {
        return "";
    }

    private static String o() {
        return "";
    }

    private static String p() {
        return "ANDH070308";
    }

    private static String q() {
        return "android";
    }

    private static String r() {
        return "";
    }

    private static String s() {
        return "";
    }

    public fc(Context context) {
        this.a = context.getApplicationContext();
    }

    private String e() {
        return ik.a(this.a);
    }

    private static String h() {
        return ik.k();
    }

    public final String a() {
        StringBuilder sb = new StringBuilder("");
        sb.append("personal_switch=").append(b());
        sb.append("&autodiv=").append(c());
        String strD = d();
        if (!TextUtils.isEmpty(strD)) {
            sb.append("&tid=").append(strD);
        }
        String strE = e();
        if (!TextUtils.isEmpty(strE)) {
            sb.append("&adiu=").append(strE);
        }
        String strF = f();
        if (!TextUtils.isEmpty(strF)) {
            sb.append("&app_version=").append(strF);
        }
        String strG = g();
        if (!TextUtils.isEmpty(strG)) {
            sb.append("&cifa=").append(strG);
        }
        String strH = h();
        if (!TextUtils.isEmpty(strH)) {
            sb.append("&deviceid=").append(strH);
        }
        String strI = i();
        if (!TextUtils.isEmpty(strI)) {
            sb.append("&did=").append(strI);
        }
        String strJ = j();
        if (!TextUtils.isEmpty(strJ)) {
            sb.append("&didv=").append(strJ);
        }
        String strK = k();
        if (!TextUtils.isEmpty(strK)) {
            sb.append("&dic=").append(strK);
        }
        String strL = l();
        if (!TextUtils.isEmpty(strL)) {
            sb.append("&dip=").append(strL);
        }
        String strM = m();
        if (!TextUtils.isEmpty(strM)) {
            sb.append("&diu=").append(strM);
        }
        String strN = n();
        if (!TextUtils.isEmpty(strN)) {
            sb.append("&diu2=").append(strN);
        }
        String strO = o();
        if (!TextUtils.isEmpty(strO)) {
            sb.append("&diu3=").append(strO);
        }
        String strP = p();
        if (!TextUtils.isEmpty(strP)) {
            sb.append("&div=").append(strP);
        }
        String strQ = q();
        if (!TextUtils.isEmpty(strQ)) {
            sb.append("&os=").append(strQ);
        }
        String strR = r();
        if (!TextUtils.isEmpty(strR)) {
            sb.append("&stepid=").append(strR);
        }
        String strS = s();
        if (!TextUtils.isEmpty(strS)) {
            sb.append("&session=").append(strS);
        }
        return sb.toString();
    }
}
