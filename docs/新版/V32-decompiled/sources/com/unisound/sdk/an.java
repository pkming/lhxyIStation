package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
public class an extends cn.yunzhisheng.asr.a {
    public static final int aB = 1;
    public static final int aC = 2;
    public static final int aD = 3;
    public static final int aE = 4;
    public static final int aF = 1;
    public static final int aG = 4;
    public static final int aH = 8;
    public static final int aI = 5;
    public static final int aJ = 9;
    public static final int aK = 12;
    public static final int aL = 13;
    private static com.unisound.common.a aV = new com.unisound.common.a(al.a);
    public static String ao = "http://u.hivoice.cn:8081/casr/upload";
    public static String ap = "general";
    public static String aq = "poi";
    public static final String ar = "en";
    public static final String as = "co";
    public static final String at = "cn";
    public static final String au = "oral";
    public static final String av = "cn_en_mix";
    public final String an = "/USCService/WebApi";
    public boolean aw = false;
    public boolean ax = false;
    public boolean ay = true;
    public boolean az = true;
    public boolean aA = true;
    private boolean aW = true;
    public int aM = 0;
    public boolean aN = false;
    public int aO = 0;
    private String aX = "";
    private String aY = "";
    private String aZ = "";
    private long ba = 0;
    private long bb = 0;
    private int bc = 0;
    private String bd = "";
    int aP = 0;
    int aQ = 8;
    int aR = 1;
    int aS = 3000;
    int aT = 20;
    private String be = null;
    private String bf = "";
    private g bg = new g();
    private boolean bh = false;
    private com.unisound.common.ac bi = new com.unisound.common.ac(-1, "");
    private com.unisound.common.ac bj = new com.unisound.common.ac(-1, "");
    private bz bk = new bz();
    private ch bl = new ch();
    public int aU = 1;

    public int U() {
        return this.aU;
    }

    public boolean V() {
        return this.bh;
    }

    public String W() {
        return this.bf;
    }

    public String X() {
        return this.be;
    }

    public ch Y() {
        return this.bl;
    }

    public bz Z() {
        return this.bk;
    }

    public void a(long j) {
        this.ba = j;
    }

    public void a(com.unisound.common.ac acVar) {
        this.bi.a(acVar);
        this.bj.a(acVar);
    }

    public void a(ao aoVar) {
        if (aoVar == ao.VPR) {
            this.bg.a(false);
            this.bk.a(false);
            this.bl.b(true);
        } else if (aoVar == ao.ASR_NLU) {
            this.bg.a(true);
            this.bl.b(false);
            this.bk.a(true);
        } else {
            this.bg.a(false);
            this.bk.a(false);
            this.bl.b(false);
        }
    }

    void a(String str, int i) {
        aV.b(str);
        aV.b(i);
    }

    public com.unisound.common.ac aa() {
        return this.bi;
    }

    public String ab() {
        return this.aX;
    }

    public g ac() {
        return this.bg;
    }

    com.unisound.common.a ad() {
        return aV;
    }

    void ae() {
        aV.e();
    }

    public boolean af() {
        return this.aW;
    }

    public boolean ag() {
        return this.ax;
    }

    public com.unisound.common.ac ah() {
        return this.bj;
    }

    public boolean ai() {
        return this.ay;
    }

    public String aj() {
        return this.aY;
    }

    public String ak() {
        return this.aZ;
    }

    public long al() {
        return this.ba;
    }

    public long am() {
        return this.bb;
    }

    public long an() {
        long j = this.bb - this.ba;
        if (j >= 3600000 || j <= 0) {
            return 0L;
        }
        return j;
    }

    public int ao() {
        return this.bc;
    }

    public String ap() {
        return this.bd;
    }

    public String aq() {
        return cd.a;
    }

    public void b(long j) {
        this.bb = j;
    }

    void b(String str, int i) {
        aV.a(str);
        aV.a(i);
        com.unisound.common.r.c("RecognizerParams:setDefaultServer server " + str + ",port " + i);
    }

    @Override // cn.yunzhisheng.asr.a
    public void c(String str) {
        super.c(str);
        this.bg.f(str);
    }

    public void f(String str) {
        this.bf = str;
    }

    public void g(String str) {
        this.be = str;
    }

    public void h(String str) {
        this.aX = str;
    }

    public void i(String str) {
        if (str == null) {
            com.unisound.common.r.e("RecognizerParams:setLanguage error language == null ");
            return;
        }
        com.unisound.common.r.c("RecognizerParams:setLanguage in " + str);
        aV.a(al.a(str));
        this.ad = false;
        cd.a = ao;
        if (str.equals("en") || str.equals("co") || str.equals(au)) {
            return;
        }
        if (str.equals(av)) {
            cd.a = "http://" + aV.b() + ":9006/casr/upload";
        } else {
            com.unisound.common.r.c("RecognizerParams:setLanguage do cn");
        }
    }

    public void j(String str) {
        this.bg.a(str);
        com.unisound.common.r.c("RecognizerParams:setLanguage do " + str);
    }

    public boolean k(String str) {
        return this.bg.b(str);
    }

    public void l(boolean z) {
        this.bh = z;
    }

    public boolean l(String str) {
        return this.bg.c(str);
    }

    public void m(boolean z) {
        this.aN = z;
    }

    public boolean m(String str) {
        if (str == null) {
            return false;
        }
        String[] strArrSplit = str.split(":");
        if (strArrSplit.length != 2 || strArrSplit[0].length() == 0) {
            return false;
        }
        try {
            short sIntValue = (short) Integer.valueOf(strArrSplit[1]).intValue();
            a(strArrSplit[0], sIntValue);
            b(strArrSplit[0], sIntValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void n(boolean z) {
        this.aW = z;
    }

    public boolean n(String str) {
        if (str == null) {
            return false;
        }
        String[] strArrSplit = str.split(":");
        if (strArrSplit.length != 2 || strArrSplit[0].length() == 0) {
            return false;
        }
        try {
            cc.a = "http://" + strArrSplit[0] + ":" + ((int) ((short) Integer.valueOf(strArrSplit[1]).intValue()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void o(String str) {
        this.bg.d(str);
    }

    public void o(boolean z) {
        this.bg.d(z ? "far" : "near");
    }

    public void p(String str) {
        this.aY = str;
    }

    public void p(boolean z) {
        f(z);
    }

    public void q(int i) {
        this.aU = i;
        if (i == 8 || i == 12 || i == 9 || i == 13) {
            this.bl.b(true);
        } else {
            this.bl.b(false);
        }
        this.bg.a(true);
    }

    public void q(String str) {
        if (str.equals("")) {
            str = com.unisound.common.k.q + System.currentTimeMillis();
        }
        this.aZ = str;
    }

    public void q(boolean z) {
        this.ax = z;
    }

    public void r(String str) {
        this.bd = str;
    }

    public void r(boolean z) {
        this.bg.d(z);
    }

    public boolean r(int i) {
        return this.bg.a(i);
    }

    public void s(int i) {
        this.bc = i;
    }

    public void s(String str) {
        cd.a = "http://" + str + "/casr/upload";
    }

    public void s(boolean z) {
        this.bg.e(z);
    }

    public void t(String str) {
        cd.a = str;
    }
}
