package com.autonavi.aps.amapapi.trans;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.LinkQualityInfo;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.ig;
import com.amap.api.col.p0003sl.ik;
import com.amap.api.col.p0003sl.nr;
import com.autonavi.aps.amapapi.restruct.g;
import com.autonavi.aps.amapapi.restruct.k;
import com.autonavi.aps.amapapi.utils.j;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: compiled from: Req.java */
/* JADX INFO: loaded from: classes2.dex */
public final class f {
    protected static String I;
    protected static String K;
    public String a = "1";
    protected short b = 0;
    protected String c = null;
    protected String d = null;
    protected String e = null;
    protected String f = null;
    protected String g = null;
    public String h = null;
    public String i = null;
    protected String j = null;
    protected String k = null;
    protected String l = null;
    protected String m = null;
    protected String n = null;
    protected String o = null;
    protected String p = null;
    protected String q = null;
    protected String r = null;
    protected String s = null;
    protected String t = null;
    protected String u = null;
    protected String v = null;
    protected String w = null;
    protected String x = null;
    protected String y = null;
    protected int z = 0;
    protected ArrayList<com.autonavi.aps.amapapi.restruct.d> A = new ArrayList<>();
    protected ArrayList<com.autonavi.aps.amapapi.restruct.d> B = new ArrayList<>();
    protected String C = null;
    protected String D = null;
    protected ArrayList<nr> E = new ArrayList<>();
    protected String F = null;
    protected String G = null;
    protected byte[] H = null;
    private byte[] Q = null;
    private int R = 0;
    protected String J = null;
    protected String L = null;
    protected String M = null;
    protected String N = null;
    protected int O = 0;
    private List<com.autonavi.aps.amapapi.restruct.f> S = null;
    private List<com.autonavi.aps.amapapi.restruct.d> T = Collections.synchronizedList(new ArrayList());
    final int P = 3;

    /* JADX WARN: Can't wrap try/catch for region: R(44:13|18|19|(8:24|(1:26)(1:27)|28|(7:30|(1:32)(1:33)|34|(1:36)(1:37)|38|(1:40)(1:41)|42)(12:(11:45|(1:47)(1:48)|49|(1:51)(1:52)|53|(1:55)(1:56)|57|(1:59)(1:60)|61|(1:63)(1:64)|65)|66|(1:68)(1:69)|(1:71)|74|(1:76)(1:77)|78|(1:80)|81|(1:83)|84|(2:86|(2:88|127)(3:89|(11:91|(1:93)(1:94)|95|(1:97)(1:98)|99|(1:101)(1:102)|103|(1:105)|109|(8:111|(1:113)(1:114)|115|(1:117)|118|(1:120)|121|424)(2:122|423)|123)|422))(2:124|(2:126|127)))|43|66|(0)(0)|(9:71|74|(0)(0)|78|(0)|81|(0)|84|(0)(0))(0))(1:23)|128|(1:134)(3:399|132|133)|135|(1:202)(4:138|(7:140|(3:160|(1:162)|163)(3:147|(3:149|(1:151)|152)(2:153|(3:155|(1:157)|158))|159)|164|(1:166)|170|(2:189|(1:413)(5:191|(1:193)|(1:196)|197|(3:199|188|412)(1:414)))(2:177|(1:411)(6:181|(1:183)|(1:186)|187|188|412))|200)|409|201)|203|(1:231)(14:208|403|209|210|390|211|(1:213)|214|215|218|(1:220)|224|(2:226|227)|228)|232|(1:234)(7:235|(1:237)(1:238)|(1:240)|241|(11:243|392|244|245|247|(1:252)|253|(1:257)|258|(2:260|420)(1:421)|261)|419|262)|263|(2:387|264)|(1:266)(3:267|(1:269)|(1:271)(30:272|273|276|407|277|(1:279)|280|405|281|282|(3:401|284|285)|286|290|383|291|292|394|293|294|(1:296)(1:297)|298|(1:300)|301|(5:303|(1:305)(1:306)|307|(5:388|309|(9:312|(7:338|339|(1:341)|342|343|344|345)(2:319|(7:321|(1:323)|324|325|396|326|327)(2:329|(6:331|(1:333)|334|335|336|337)))|346|347|385|348|349|350|310)|415|352)|355)|356|(4:358|(1:360)(1:361)|362|(3:364|(6:367|(1:369)|370|(2:372|417)(1:418)|373|365)|416))|374|(1:376)|377|378))|275|276|407|277|(0)|280|405|281|282|(0)|286|290|383|291|292|394|293|294|(0)(0)|298|(0)|301|(0)|356|(0)|374|(0)|377|378) */
    /* JADX WARN: Can't wrap try/catch for region: R(45:13|18|19|(8:24|(1:26)(1:27)|28|(7:30|(1:32)(1:33)|34|(1:36)(1:37)|38|(1:40)(1:41)|42)(12:(11:45|(1:47)(1:48)|49|(1:51)(1:52)|53|(1:55)(1:56)|57|(1:59)(1:60)|61|(1:63)(1:64)|65)|66|(1:68)(1:69)|(1:71)|74|(1:76)(1:77)|78|(1:80)|81|(1:83)|84|(2:86|(2:88|127)(3:89|(11:91|(1:93)(1:94)|95|(1:97)(1:98)|99|(1:101)(1:102)|103|(1:105)|109|(8:111|(1:113)(1:114)|115|(1:117)|118|(1:120)|121|424)(2:122|423)|123)|422))(2:124|(2:126|127)))|43|66|(0)(0)|(9:71|74|(0)(0)|78|(0)|81|(0)|84|(0)(0))(0))(1:23)|128|(1:134)(3:399|132|133)|135|(1:202)(4:138|(7:140|(3:160|(1:162)|163)(3:147|(3:149|(1:151)|152)(2:153|(3:155|(1:157)|158))|159)|164|(1:166)|170|(2:189|(1:413)(5:191|(1:193)|(1:196)|197|(3:199|188|412)(1:414)))(2:177|(1:411)(6:181|(1:183)|(1:186)|187|188|412))|200)|409|201)|203|(1:231)(14:208|403|209|210|390|211|(1:213)|214|215|218|(1:220)|224|(2:226|227)|228)|232|(1:234)(7:235|(1:237)(1:238)|(1:240)|241|(11:243|392|244|245|247|(1:252)|253|(1:257)|258|(2:260|420)(1:421)|261)|419|262)|263|387|264|(1:266)(3:267|(1:269)|(1:271)(30:272|273|276|407|277|(1:279)|280|405|281|282|(3:401|284|285)|286|290|383|291|292|394|293|294|(1:296)(1:297)|298|(1:300)|301|(5:303|(1:305)(1:306)|307|(5:388|309|(9:312|(7:338|339|(1:341)|342|343|344|345)(2:319|(7:321|(1:323)|324|325|396|326|327)(2:329|(6:331|(1:333)|334|335|336|337)))|346|347|385|348|349|350|310)|415|352)|355)|356|(4:358|(1:360)(1:361)|362|(3:364|(6:367|(1:369)|370|(2:372|417)(1:418)|373|365)|416))|374|(1:376)|377|378))|275|276|407|277|(0)|280|405|281|282|(0)|286|290|383|291|292|394|293|294|(0)(0)|298|(0)|301|(0)|356|(0)|374|(0)|377|378) */
    /* JADX WARN: Code restructure failed: missing block: B:287:0x071d, code lost:
    
        r2 = 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:288:0x071f, code lost:
    
        r2 = 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:289:0x0720, code lost:
    
        r8 = r8 + r2;
     */
    /* JADX WARN: Removed duplicated region for block: B:124:0x031c  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x0346 A[PHI: r0
      0x0346: PHI (r0v50 int) = (r0v49 int), (r0v49 int), (r0v162 int) binds: [B:129:0x0329, B:131:0x032e, B:382:0x0346] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:202:0x0544  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x05fc  */
    /* JADX WARN: Removed duplicated region for block: B:234:0x0610  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x0616  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00e8  */
    /* JADX WARN: Removed duplicated region for block: B:266:0x06c8 A[Catch: all -> 0x06ea, TryCatch #2 {all -> 0x06ea, blocks: (B:264:0x06c4, B:266:0x06c8, B:267:0x06cb, B:271:0x06d7, B:272:0x06dc), top: B:387:0x06c4 }] */
    /* JADX WARN: Removed duplicated region for block: B:267:0x06cb A[Catch: all -> 0x06ea, TryCatch #2 {all -> 0x06ea, blocks: (B:264:0x06c4, B:266:0x06c8, B:267:0x06cb, B:271:0x06d7, B:272:0x06dc), top: B:387:0x06c4 }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00f0  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x06fd A[Catch: all -> 0x071f, TRY_LEAVE, TryCatch #13 {all -> 0x071f, blocks: (B:277:0x06f5, B:279:0x06fd), top: B:407:0x06f5 }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x00fb  */
    /* JADX WARN: Removed duplicated region for block: B:296:0x0737  */
    /* JADX WARN: Removed duplicated region for block: B:297:0x0739  */
    /* JADX WARN: Removed duplicated region for block: B:300:0x0748  */
    /* JADX WARN: Removed duplicated region for block: B:303:0x0762  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0108  */
    /* JADX WARN: Removed duplicated region for block: B:358:0x086f  */
    /* JADX WARN: Removed duplicated region for block: B:376:0x091a  */
    /* JADX WARN: Removed duplicated region for block: B:401:0x070f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0164  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0205  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0210  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0213  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0215 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0224  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0237  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0242  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0247  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0255  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final byte[] a() {
        /*
            Method dump skipped, instruction units count: 2383
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.trans.f.a():byte[]");
    }

    /* JADX WARN: Removed duplicated region for block: B:6:0x000f A[Catch: all -> 0x003e, TryCatch #0 {all -> 0x003e, blocks: (B:4:0x000c, B:10:0x001c, B:12:0x001f, B:14:0x0028, B:15:0x0030, B:6:0x000f, B:8:0x0014), top: B:20:0x000c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private byte[] a(java.lang.String r7) {
        /*
            r6 = this;
            java.lang.String r0 = ":"
            java.lang.String[] r0 = r7.split(r0)
            r1 = 6
            byte[] r2 = new byte[r1]
            r3 = 0
            if (r0 == 0) goto Lf
            int r4 = r0.length     // Catch: java.lang.Throwable -> L3e
            if (r4 == r1) goto L1b
        Lf:
            java.lang.String[] r0 = new java.lang.String[r1]     // Catch: java.lang.Throwable -> L3e
            r4 = r3
        L12:
            if (r4 >= r1) goto L1b
            java.lang.String r5 = "0"
            r0[r4] = r5     // Catch: java.lang.Throwable -> L3e
            int r4 = r4 + 1
            goto L12
        L1b:
            r1 = r3
        L1c:
            int r4 = r0.length     // Catch: java.lang.Throwable -> L3e
            if (r1 >= r4) goto L54
            r4 = r0[r1]     // Catch: java.lang.Throwable -> L3e
            int r4 = r4.length()     // Catch: java.lang.Throwable -> L3e
            r5 = 2
            if (r4 <= r5) goto L30
            r4 = r0[r1]     // Catch: java.lang.Throwable -> L3e
            java.lang.String r4 = r4.substring(r3, r5)     // Catch: java.lang.Throwable -> L3e
            r0[r1] = r4     // Catch: java.lang.Throwable -> L3e
        L30:
            r4 = r0[r1]     // Catch: java.lang.Throwable -> L3e
            r5 = 16
            int r4 = java.lang.Integer.parseInt(r4, r5)     // Catch: java.lang.Throwable -> L3e
            byte r4 = (byte) r4     // Catch: java.lang.Throwable -> L3e
            r2[r1] = r4     // Catch: java.lang.Throwable -> L3e
            int r1 = r1 + 1
            goto L1c
        L3e:
            r0 = move-exception
            java.lang.String r7 = java.lang.String.valueOf(r7)
            java.lang.String r1 = "getMacBa "
            java.lang.String r7 = r1.concat(r7)
            java.lang.String r1 = "Req"
            com.autonavi.aps.amapapi.utils.b.a(r0, r1, r7)
            java.lang.String r7 = "00:00:00:00:00:00"
            byte[] r2 = r6.a(r7)
        L54:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.trans.f.a(java.lang.String):byte[]");
    }

    private void b() {
        String[] strArr = new String[27];
        strArr[0] = this.a;
        strArr[1] = this.c;
        strArr[2] = this.d;
        strArr[3] = this.e;
        strArr[4] = this.f;
        strArr[5] = this.g;
        strArr[6] = this.h;
        strArr[7] = this.i;
        strArr[8] = this.l;
        strArr[9] = this.m;
        strArr[10] = this.n;
        strArr[11] = this.o;
        strArr[12] = this.p;
        strArr[13] = this.q;
        strArr[14] = this.r;
        strArr[15] = this.s;
        strArr[16] = this.t;
        strArr[17] = this.u;
        strArr[18] = this.v;
        strArr[19] = this.w;
        strArr[20] = this.x;
        strArr[21] = this.D;
        strArr[22] = this.F;
        strArr[23] = this.G;
        strArr[24] = I;
        strArr[25] = this.M;
        strArr[26] = this.N;
        for (int i = 0; i < 27; i++) {
            if (TextUtils.isEmpty(strArr[i])) {
                strArr[i] = "";
            }
        }
        if (TextUtils.isEmpty(this.j)) {
            this.j = "0";
        } else if (!"0".equals(this.j) && !"2".equals(this.j)) {
            this.j = "0";
        }
        if (TextUtils.isEmpty(this.k)) {
            this.k = "0";
        } else if (!"0".equals(this.k) && !"1".equals(this.k)) {
            this.k = "0";
        }
        if (TextUtils.isEmpty(this.y)) {
            this.y = "0";
        } else if (!"1".equals(this.y) && !"2".equals(this.y)) {
            this.y = "0";
        }
        if (!com.autonavi.aps.amapapi.restruct.e.a(this.z)) {
            this.z = 0;
        }
        if (this.H == null) {
            this.H = new byte[0];
        }
    }

    public final void a(Context context, boolean z, boolean z2, com.autonavi.aps.amapapi.restruct.e eVar, k kVar, ConnectivityManager connectivityManager, String str, g gVar) {
        String str2;
        String str3;
        String str4;
        NetworkInfo activeNetworkInfo;
        String strA;
        String str5;
        ArrayList<nr> arrayList;
        int length;
        String strF = ig.f(context);
        int iD = j.d();
        this.J = str;
        this.S = null;
        if (z2) {
            str2 = "api_serverSDK_130905";
            str3 = "S128DF1572465B890OE3F7A13167KLEI";
        } else {
            str2 = "UC_nlp_20131029";
            str3 = "BKZCHMBBSSUK7U8GLUKHBB56CCFF78U";
        }
        String str6 = str3;
        String str7 = str2;
        StringBuilder sb = new StringBuilder();
        int iG = eVar.g();
        int iH = eVar.h();
        TelephonyManager telephonyManagerI = eVar.i();
        ArrayList<com.autonavi.aps.amapapi.restruct.d> arrayListC = eVar.c();
        ArrayList<com.autonavi.aps.amapapi.restruct.d> arrayListD = eVar.d();
        ArrayList<nr> arrayListE = kVar.e();
        String str8 = iH == 2 ? "1" : "0";
        if (telephonyManagerI != null) {
            if (TextUtils.isEmpty(com.autonavi.aps.amapapi.utils.b.g)) {
                try {
                    com.autonavi.aps.amapapi.utils.b.g = ik.k();
                } catch (Throwable th) {
                    com.autonavi.aps.amapapi.utils.b.a(th, "Aps", "getApsReq part4");
                }
            }
            str4 = "1";
            if (TextUtils.isEmpty(com.autonavi.aps.amapapi.utils.b.g) && Build.VERSION.SDK_INT < 29) {
                com.autonavi.aps.amapapi.utils.b.g = "888888888888888";
            }
            if (TextUtils.isEmpty(com.autonavi.aps.amapapi.utils.b.h)) {
                try {
                    com.autonavi.aps.amapapi.utils.b.h = ik.n();
                } catch (SecurityException unused) {
                } catch (Throwable th2) {
                    com.autonavi.aps.amapapi.utils.b.a(th2, "Aps", "getApsReq part2");
                }
            }
            if (TextUtils.isEmpty(com.autonavi.aps.amapapi.utils.b.h) && Build.VERSION.SDK_INT < 29) {
                com.autonavi.aps.amapapi.utils.b.h = "888888888888888";
            }
        } else {
            str4 = "1";
        }
        try {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        } catch (Throwable th3) {
            com.autonavi.aps.amapapi.utils.b.a(th3, "Aps", "getApsReq part");
            activeNetworkInfo = null;
        }
        boolean zA = kVar.a(connectivityManager);
        if (j.a(activeNetworkInfo) != -1) {
            strA = j.a(connectivityManager);
            str5 = zA ? "2" : str4;
        } else {
            strA = "";
            str5 = strA;
        }
        if ((iG & 4) == 4 && !arrayListD.isEmpty()) {
            this.B.clear();
            this.B.addAll(arrayListD);
        } else {
            this.B.clear();
        }
        this.A.clear();
        this.A.addAll(arrayListC);
        StringBuilder sb2 = new StringBuilder();
        if (kVar.k()) {
            if (zA) {
                com.autonavi.aps.amapapi.restruct.j jVarM = kVar.m();
                if (kVar.a(jVarM)) {
                    sb2.append(jVarM.a()).append(",");
                    int iC = jVarM.c();
                    if (iC < -128 || iC > 127) {
                        iC = 0;
                    }
                    sb2.append(iC).append(",");
                    String strB = jVarM.b();
                    try {
                        length = strB.getBytes("UTF-8").length;
                    } catch (Exception unused2) {
                        length = 32;
                    }
                    if (length >= 32) {
                        strB = "unkwn";
                    }
                    sb2.append(strB.replace("*", "."));
                }
            }
            if (arrayListE != null && (arrayList = this.E) != null) {
                arrayList.clear();
                this.E.addAll(arrayListE);
            }
        } else {
            kVar.g();
            ArrayList<nr> arrayList2 = this.E;
            if (arrayList2 != null) {
                arrayList2.clear();
            }
        }
        this.b = (short) 0;
        if (!z) {
            this.b = (short) (2 | 0);
        }
        this.c = str7;
        this.d = str6;
        this.f = Build.MODEL;
        this.g = "android" + Build.VERSION.RELEASE;
        this.h = j.b(context);
        this.i = str8;
        this.j = "0";
        this.k = "0";
        this.l = "0";
        this.m = "0";
        this.n = "0";
        this.o = strF;
        this.p = com.autonavi.aps.amapapi.utils.b.g;
        this.q = com.autonavi.aps.amapapi.utils.b.h;
        this.s = String.valueOf(iD);
        this.t = j.i(context);
        this.v = "6.4.3";
        this.w = null;
        this.u = "";
        this.x = strA;
        this.y = str5;
        this.z = iG;
        this.C = eVar.l();
        this.F = k.p();
        this.D = sb2.toString();
        this.O = (int) ((j.b() - kVar.q()) / 1000);
        try {
            if (TextUtils.isEmpty(I)) {
                I = ik.f(context);
            }
        } catch (Throwable unused3) {
        }
        try {
            if (TextUtils.isEmpty(K)) {
                K = ik.a(context);
            }
        } catch (Throwable unused4) {
        }
        try {
            if (TextUtils.isEmpty(this.M)) {
                this.M = ik.f();
            }
        } catch (Throwable unused5) {
        }
        try {
            if (TextUtils.isEmpty(this.N)) {
                this.N = ik.e(context);
            }
        } catch (Throwable unused6) {
        }
        try {
            this.S = gVar.a(this.B, this.E);
            a(this.A, this.B);
        } catch (Throwable th4) {
            th4.printStackTrace();
        }
        sb.delete(0, sb.length());
        sb2.delete(0, sb2.length());
    }

    private void a(ArrayList<com.autonavi.aps.amapapi.restruct.d> arrayList, ArrayList<com.autonavi.aps.amapapi.restruct.d> arrayList2) {
        if (arrayList2 != null && arrayList2.size() > 0) {
            for (com.autonavi.aps.amapapi.restruct.d dVar : arrayList2) {
                if (dVar.r && dVar.n) {
                    a(dVar, this.T);
                    return;
                }
            }
        }
        if (arrayList == null || arrayList.size() <= 0) {
            return;
        }
        a(arrayList.get(0), this.T);
    }

    private static void a(com.autonavi.aps.amapapi.restruct.d dVar, List<com.autonavi.aps.amapapi.restruct.d> list) {
        if (dVar == null || list == null) {
            return;
        }
        int size = list.size();
        if (size == 0) {
            list.add(dVar);
            return;
        }
        long jMin = LinkQualityInfo.UNKNOWN_LONG;
        int i = 0;
        int i2 = -1;
        int i3 = -1;
        while (true) {
            if (i >= size) {
                i2 = i3;
                break;
            }
            com.autonavi.aps.amapapi.restruct.d dVar2 = list.get(i);
            if (dVar.c() != null && dVar.c().equals(dVar2.c())) {
                if (dVar.s != dVar2.s) {
                    dVar2.t = dVar.t;
                    dVar2.s = dVar.s;
                }
            } else {
                jMin = Math.min(jMin, dVar2.t);
                if (jMin == dVar2.t) {
                    i3 = i;
                }
                i++;
            }
        }
        if (i2 >= 0) {
            if (size < 3) {
                list.add(dVar);
            } else {
                if (dVar.t <= jMin || i2 >= size) {
                    return;
                }
                list.remove(i2);
                list.add(dVar);
            }
        }
    }

    private static int a(String str, byte[] bArr, int i) {
        try {
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "Req", "copyContentWithByteLen");
            bArr[i] = 0;
        }
        if (TextUtils.isEmpty(str)) {
            bArr[i] = 0;
            return i + 1;
        }
        byte[] bytes = str.getBytes(MediaPlayer.CHARSET_GBK);
        int length = bytes.length;
        if (length > 127) {
            length = 127;
        }
        bArr[i] = (byte) length;
        int i2 = i + 1;
        System.arraycopy(bytes, 0, bArr, i2, length);
        return i2 + length;
    }
}
