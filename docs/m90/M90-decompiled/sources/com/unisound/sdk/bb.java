package com.unisound.sdk;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Base64;
import cn.yunzhisheng.asr.JniUscClient;
import cn.yunzhisheng.asr.VAD;
import com.unisound.client.ErrorCode;
import com.unisound.client.IAudioSource;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechUnderstanderListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class bb extends m {
    private static final String C = "asr_recongize";
    private static final int Q = 1;
    private static final int R = 5;
    private static final int S = 6;
    private static final int T = 7;
    private static final int U = 8;
    private static final int V = 11;
    private static final int W = 12;
    private static final int X = 13;
    private static final int Y = 14;
    private static final int Z = 15;
    private static final int aa = 16;
    private static final int ab = 17;
    private static final int ac = 18;
    private static final int ad = 20;
    private static final int aq = 0;
    private static final int ar = 1;
    private static final int as = 2;
    private static final int at = 3;
    private SpeechUnderstanderListener A;
    private bz B;
    private v D;
    private boolean E;
    private boolean F;
    private String G;
    private int H;
    private ar I;
    private String J;
    private String K;
    private boolean L;
    private int M;
    private boolean N;
    private boolean O;
    private StringBuffer P;
    private com.unisound.common.ag ae;
    private Context af;
    private HandlerThread ag;
    private Handler ah;
    private int ai;
    private boolean aj;
    private String ak;
    private boolean al;
    private boolean am;
    private boolean an;
    private boolean ao;
    private z ap;
    protected aj o;
    protected cn p;
    protected final int q;
    protected final int r;
    protected final int s;
    protected final int t;
    protected final int u;
    ArrayList<String> v;
    ArrayList<String> w;
    ArrayList<String> x;
    String y;
    String z;

    protected bb(Context context, String str, String str2) {
        super(context, str);
        this.D = null;
        this.E = true;
        this.F = true;
        this.G = "";
        this.H = 1;
        this.I = new ar();
        this.p = new cn();
        this.J = "main";
        this.K = cn.a;
        this.q = 51;
        this.r = 52;
        this.s = 53;
        this.t = 54;
        this.u = 55;
        this.L = false;
        this.M = 0;
        this.N = false;
        this.O = false;
        this.P = new StringBuffer();
        this.aj = false;
        this.ak = "";
        this.al = false;
        this.am = false;
        this.an = false;
        this.ao = false;
        this.ap = new be(this);
        this.af = context;
        this.y = str;
        this.z = str2;
        this.p.a(this.f.a);
        bz bzVarZ = this.b.Z();
        this.B = bzVarZ;
        bzVarZ.b(str);
        this.B.c(str2);
        this.B.e(com.unisound.common.k.x);
        aj ajVar = new aj(context, this.b, this.mLooper);
        this.o = ajVar;
        ajVar.a(this.ap);
        this.b.h(str);
        this.b.q(1);
        this.N = false;
        this.v = new ArrayList<>();
        this.w = new ArrayList<>();
        this.x = new ArrayList<>();
        com.unisound.common.ag agVar = new com.unisound.common.ag(new bc(this), this.mLooper);
        this.ae = agVar;
        agVar.c();
    }

    private boolean E() {
        return this.E;
    }

    private boolean F() {
        return this.F;
    }

    private com.unisound.common.an G() {
        return this.o.p();
    }

    private com.unisound.common.am H() {
        return this.o.o();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0056  */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void I() {
        /*
            r6 = this;
            boolean r0 = r6.E()
            if (r0 == 0) goto L5c
            boolean r0 = r6.F()
            if (r0 == 0) goto L5c
            int r0 = r6.H
            r1 = 1
            r2 = 1210(0x4ba, float:1.696E-42)
            if (r0 != 0) goto L27
            android.os.Handler r0 = r6.ah
            java.util.ArrayList<java.lang.String> r3 = r6.v
            java.util.ArrayList<java.lang.String> r4 = r6.w
            java.util.ArrayList<java.lang.String> r5 = r6.x
            java.lang.String r3 = com.unisound.common.o.a(r3, r4, r5)
        L1f:
            android.os.Message r0 = r0.obtainMessage(r2, r3)
            r0.sendToTarget()
            goto L4d
        L27:
            r3 = 2
            r4 = 0
            if (r0 != r3) goto L3e
            java.lang.String r0 = r6.J
            java.lang.String r3 = r6.K
            boolean r0 = r0.equals(r3)
            if (r0 != 0) goto L3e
            android.os.Handler r0 = r6.ah
            java.util.ArrayList<java.lang.String> r3 = r6.v
            java.lang.String r3 = com.unisound.common.o.a(r3, r4, r4)
            goto L1f
        L3e:
            int r0 = r6.H
            if (r0 != r1) goto L4d
            android.os.Handler r0 = r6.ah
            java.util.ArrayList<java.lang.String> r3 = r6.w
            java.util.ArrayList<java.lang.String> r5 = r6.x
            java.lang.String r3 = com.unisound.common.o.a(r4, r3, r5)
            goto L1f
        L4d:
            r6.am = r1
            r6.stop()
            boolean r0 = r6.an
            if (r0 == 0) goto L5c
            android.os.Handler r0 = r6.ah
            r1 = 5
            r0.sendEmptyMessage(r1)
        L5c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.sdk.bb.I():void");
    }

    private void J() {
        if (this.w.size() == 0) {
            this.w.add(com.unisound.common.o.a(this.H, "full", "", "", true, null, null));
        }
        if (this.B.v() && this.x.size() == 0) {
            this.x.add(com.unisound.common.o.a(-1, null, "", null, null, null, null));
        }
        if (this.v.size() == 0) {
            this.v.add(com.unisound.common.o.a(this.H, "full", "", null, null, Float.valueOf(-20.0f), null));
        }
    }

    private int K() {
        if (this.N) {
            return 0;
        }
        com.unisound.common.r.e("init error " + ErrorCode.toJsonMessage(ErrorCode.GENERAL_INIT_ERROR));
        SpeechUnderstanderListener speechUnderstanderListener = this.A;
        if (speechUnderstanderListener == null) {
            return -1;
        }
        speechUnderstanderListener.onError(SpeechConstants.ASR_ERROR, ErrorCode.toJsonMessage(ErrorCode.GENERAL_INIT_ERROR));
        return -1;
    }

    private String L() {
        int iW = w();
        String strX = "";
        if (iW == 1) {
            strX = x();
        } else if (iW == 2) {
            strX = y();
        } else if (iW == 3) {
            strX = ("" + x() + "|") + y();
        }
        return "commit=" + h() + ";authorized_status=" + iW + ":" + strX;
    }

    private void a(String str, int i) {
        this.B.a(str, i);
    }

    private void c(boolean z) {
        this.B.a(z);
    }

    private void d(boolean z) {
        if (this.f.g || a(z)) {
            a(this.p.a(), false, cn.a, cn.a);
        } else {
            com.unisound.common.r.e("loadModel::isInit=false");
            g(ErrorCode.ASR_SDK_FIX_COMPILE_NO_INIT);
        }
    }

    private int e(boolean z) {
        if (!this.f.g && !a(z)) {
            com.unisound.common.r.e("loadModel::isInit=false");
            return -1;
        }
        return a((this.f.a + "wakeup.dat") + "," + (this.f.a + this.f.d + ".dat"), false, "init_asr", "wakeup," + this.f.d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String g(String str, String str2) {
        try {
            if (str.contains(str2)) {
                JSONObject jSONObject = new JSONObject(str);
                str = jSONObject.has(str2) ? jSONObject.getString(str2) : "";
            }
            return str;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void g(String str) {
        this.B.l(str);
    }

    private void h(int i) {
        this.b.r(i);
    }

    private void h(String str) {
        this.B.i(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void h(String str, String str2) {
        JniUscClient jniUscClient = new JniUscClient();
        com.unisound.common.a aVarAd = this.b.ad();
        long jA = jniUscClient.a(aVarAd.a(), aVarAd.c());
        jniUscClient.a(9, str);
        jniUscClient.a(204, str2);
        com.unisound.common.r.c("SpeechUnderstanderInterface", "server :" + aVarAd.a() + " port: " + aVarAd.c());
        com.unisound.common.r.c("SpeechUnderstanderInterface", "juc.create() returns " + jA);
        int iG = jniUscClient.g();
        com.unisound.common.r.c("SpeechUnderstanderInterface", "initUscClient : loginstate = " + iG);
        if (iG == 0) {
            this.ak = jniUscClient.c(206);
        } else {
            k(iG);
        }
        jniUscClient.e();
        com.unisound.common.r.c("SpeechUnderstanderInterface : initUscClient -> mLoginToken = " + this.ak);
    }

    private void i(int i) {
        this.o.c(i);
    }

    private void i(String str) {
        this.B.e(str);
    }

    private void j(int i) {
        if (i == 0) {
            return;
        }
        Message message = new Message();
        message.what = 54;
        message.obj = Integer.valueOf(i);
        this.ah.sendMessage(message);
    }

    private void j(String str) {
        this.B.j(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void k(int i) {
        Message message = new Message();
        message.what = 55;
        message.obj = Integer.valueOf(i);
        this.ah.sendMessage(message);
    }

    private void k(String str) {
        this.B.k(str);
    }

    private void l(String str) {
        this.B.f(str);
    }

    private void m(String str) {
        this.B.d(str);
    }

    private void n(String str) {
        this.B.g(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public cb o(String str) {
        try {
            if (str.contains(C)) {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has(C)) {
                    jSONObject.remove(C);
                    if (jSONObject.has("nluProcessTime")) {
                        this.b.r(jSONObject.getString("nluProcessTime"));
                    } else {
                        this.b.r("0");
                    }
                    return new cb(jSONObject.toString().replace("\\/", "/"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new cb(null);
    }

    private boolean p(String str) {
        if (this.b.k(str)) {
            return true;
        }
        com.unisound.common.r.e("setNetEngine::error: unkown param " + str);
        return false;
    }

    private boolean q(String str) {
        if (this.b.l(str)) {
            return true;
        }
        com.unisound.common.r.e("setNetEngineSubModel::error: unkown param " + this.H);
        return false;
    }

    private void r(String str) {
        this.b.i(str);
    }

    private String s(String str) {
        if (str == null || str.equals("")) {
            return "";
        }
        String str2 = new String(Base64.decode(str, 0));
        com.unisound.common.r.c("SpeechUnderstanderInterface -> getversion : SDK_Version = " + str2);
        return str2;
    }

    @Override // com.unisound.sdk.m
    protected int A() {
        return super.A();
    }

    protected int B() {
        if (this.H != 2) {
            new bd(this).start();
        }
        if (this.N) {
            return -1;
        }
        int i = this.H;
        this.N = true;
        if (i == 1) {
            return 0;
        }
        t();
        int iE = e(true);
        this.O = true;
        com.unisound.common.r.c("SpeechUnderstanderInterface : loadResult = " + iE);
        return iE;
    }

    protected int C() {
        return this.M;
    }

    protected String D() {
        return this.p.a();
    }

    protected int a(String str, String str2, Map<String, List<String>> map) {
        map.entrySet();
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            stringBuffer.append(this.f.a(entry.getKey(), entry.getValue()) + "\n");
        }
        String string = stringBuffer.toString();
        com.unisound.common.r.c("SpeechUnderstanderInterface --> insertVocab_ext2 : vocabContent = " + string);
        return c(str, str2, string);
    }

    protected int a(List<String> list) {
        if (K() != 0) {
            return ErrorCode.GENERAL_INIT_ERROR;
        }
        if (list == null || list.size() <= 0) {
            return -1;
        }
        this.p.b(list);
        this.p.a(true);
        if (!this.p.c()) {
            return -1;
        }
        if (a(this.f.b(cn.a), this.p.b(), this.p.d(), this.p.a()) != 0) {
            com.unisound.common.r.e(toString() + ErrorCode.toJsonMessage(ErrorCode.ASR_SDK_WAKEUP_ERROR));
            return ErrorCode.ASR_SDK_WAKEUP_ERROR;
        }
        this.L = true;
        this.p.a(false);
        d(true);
        return 0;
    }

    protected int a(List<String> list, String str) {
        if (K() != 0) {
            return ErrorCode.GENERAL_INIT_ERROR;
        }
        if (list == null || list.size() < 1) {
            return ErrorCode.ASR_INSERT_VOCABCONTENT_ERROR;
        }
        if (str == "" || !str.contains("#") || str.split("#").length != 2) {
            return ErrorCode.ASR_INSERT_VOCABNAME_ERROR;
        }
        String[] strArrSplit = str.split("#");
        String str2 = strArrSplit[0];
        String str3 = strArrSplit[1];
        com.unisound.common.r.c("SpeechUnderstanderInterface :", "inserVocab --> modelTag = " + str2 + ", tagName = " + str3);
        int iA = a(str2, str3, list);
        this.A.onEvent(SpeechConstants.ASR_EVENT_COMPILE_DONE, (int) System.currentTimeMillis());
        if (iA == 0) {
            b(this.f.g(str2), str2);
        } else {
            this.b.a(str2, false);
            this.A.onEvent(SpeechConstants.ASR_EVENT_LOADGRAMMAR_DONE, (int) System.currentTimeMillis());
            com.unisound.common.r.e("Compile vocab error: " + iA);
        }
        return iA;
    }

    protected int a(Map<String, List<String>> map, String str) {
        if (K() != 0) {
            return ErrorCode.GENERAL_INIT_ERROR;
        }
        if (map == null || str == "") {
            com.unisound.common.r.e("SpeechUnderstanderInterface : insertVocab parmas error!");
            return -1;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            stringBuffer.append(this.f.a(entry.getKey(), entry.getValue()) + "\n");
        }
        int iA = a(this.f.b(str), this.f.f(str), stringBuffer.toString(), this.f.g(str));
        this.A.onEvent(SpeechConstants.ASR_EVENT_COMPILE_DONE, (int) System.currentTimeMillis());
        if (iA == 0) {
            com.unisound.common.r.c("SpeechUnderstanderInterface : ", "loadModel path = " + this.f.g(str) + " , code = " + iA);
            b(this.f.g(str), str);
        } else {
            this.b.a(str, false);
            this.A.onEvent(SpeechConstants.ASR_EVENT_LOADGRAMMAR_DONE, (int) System.currentTimeMillis());
            com.unisound.common.r.e("SpeechUnderstanderInterface : Compile vocab error: " + iA);
        }
        return iA;
    }

    @Override // com.unisound.sdk.m
    protected void a(int i) {
        this.ao = true;
        if (this.A != null) {
            if (i != 0) {
                k(i);
            }
            this.F = true;
            this.ah.sendEmptyMessage(6);
            I();
        }
        this.ao = false;
    }

    @Override // com.unisound.sdk.m
    protected void a(int i, String str) {
        super.a(i, str);
        SpeechUnderstanderListener speechUnderstanderListener = this.A;
        if (speechUnderstanderListener != null) {
            speechUnderstanderListener.onError(i, str);
        }
    }

    @Override // com.unisound.sdk.m
    protected void a(VAD vad) {
        if (this.J.equals(this.K)) {
            return;
        }
        this.ah.sendEmptyMessage(18);
    }

    protected void a(at atVar) {
        this.o.a(atVar);
    }

    @Override // com.unisound.sdk.m
    protected void a(String str, boolean z, int i) {
        super.a(str, z, i);
        com.unisound.common.r.c("SpeechUnderstandInterface doWakeupResult => " + str);
        this.ao = true;
        if (this.A != null && this.a.a(str, true)) {
            if (this.b.y() && this.H != 2 && this.b.I()) {
                this.o.c();
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(com.unisound.common.o.a(1000, "full", this.a.a.get(0), null, null, Float.valueOf(this.a.e), Integer.valueOf(i)));
            this.ah.obtainMessage(SpeechConstants.WAKEUP_RESULT, com.unisound.common.o.a(arrayList, null, null)).sendToTarget();
            this.ah.sendEmptyMessage(1);
        }
        this.ao = false;
    }

    @Override // com.unisound.sdk.m
    protected void a(boolean z, byte[] bArr, int i, int i2) throws Throwable {
        super.a(z, bArr, i, i2);
        if (this.D != null) {
            this.o.b(z, bArr, i, i2);
        }
        this.I.a(z, i2);
    }

    protected int b(int i, String str) {
        if (i != 1401 || !this.O) {
            return -1;
        }
        this.O = false;
        super.r();
        return 0;
    }

    @Override // com.unisound.sdk.m
    protected int b(String str, String str2) {
        return super.b(str, str2);
    }

    protected int b(String str, String str2, String str3) {
        return 0;
    }

    @Override // com.unisound.sdk.m
    protected void b(int i) {
        this.ah.sendEmptyMessage(17);
        this.mSpeechUnderstanderParams.b(i);
    }

    @Override // com.unisound.sdk.m
    protected void b(int i, int i2) {
        this.ah.sendEmptyMessage(i);
    }

    @Override // com.unisound.sdk.m
    protected void b(String str) {
        u uVar;
        int iQ;
        int i;
        this.al = false;
        this.b.r(false);
        d(this.b.N());
        Handler handler = this.ah;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (!this.N) {
            com.unisound.common.r.e("init error " + ErrorCode.toJsonMessage(ErrorCode.GENERAL_INIT_ERROR));
            this.A.onError(SpeechConstants.ASR_ERROR, ErrorCode.toJsonMessage(ErrorCode.GENERAL_INIT_ERROR));
            return;
        }
        this.v.clear();
        this.w.clear();
        this.x.clear();
        this.am = false;
        this.an = false;
        com.unisound.common.r.a();
        if (str.contains("oneshot:")) {
            this.b.i(true);
            str = str.split(":")[1];
        } else {
            this.b.i(false);
        }
        this.J = str;
        if (this.b.y()) {
            this.b.r(true);
            if (this.mSpeechUnderstanderParams.j() != 2) {
                this.H = 0;
            } else {
                this.H = 2;
            }
            if (this.b.N() == 0) {
                i = 100;
                e(i);
                this.b.p(false);
                this.b.g(true);
            }
            this.b.c(true);
            this.b.p(true);
            this.b.g(true);
            uVar = this.b;
            iQ = this.b.S();
            uVar.p(iQ);
        } else if (str.equals(this.K)) {
            this.H = 2;
            if (this.b.N() == 0) {
                i = 300;
                e(i);
                this.b.p(false);
                this.b.g(true);
            }
            this.b.c(true);
            this.b.p(true);
            this.b.g(true);
            uVar = this.b;
            iQ = this.b.S();
            uVar.p(iQ);
        } else {
            this.H = this.mSpeechUnderstanderParams.j();
            d(1);
            this.b.c(this.b.i());
            this.b.p(this.mSpeechUnderstanderParams.f());
            this.b.g(false);
            uVar = this.b;
            iQ = this.b.Q();
            uVar.p(iQ);
        }
        this.I.a();
        this.G = "";
        this.E = false;
        this.F = false;
        this.D = null;
        int i2 = this.H;
        if (i2 == 0) {
            this.D = new v(this.b, this.o);
            this.e.c(true);
            this.e.d(this.O);
            this.o.a((w) this.D, false, this.ak, (x) null);
        } else if (i2 != 1) {
            this.E = true;
            this.e.c(true);
            this.e.d(this.O);
        } else {
            this.F = true;
            this.e.c(false);
            v vVar = new v(this.b, this.o);
            this.D = vVar;
            this.o.a((w) vVar, false, this.ak, (x) null);
        }
        super.b(str);
    }

    @Override // com.unisound.sdk.m
    protected void b(String str, boolean z) {
        this.ao = true;
        if (this.A != null && this.a.a(str, false) && this.mSpeechUnderstanderParams.j() != 1) {
            for (int i = 0; i < this.a.a.size(); i++) {
                com.unisound.common.r.c("SpeechUnderstanderInterface : recognizeResult.item = " + this.a.a.get(i) + " , times=  " + i);
                this.v.add(com.unisound.common.o.a(this.H, "full", this.a.a.get(i), null, null, Float.valueOf(this.a.e), null));
            }
            this.ah.obtainMessage(1202, com.unisound.common.o.a(this.v, null, null)).sendToTarget();
        }
        this.ao = false;
    }

    protected boolean b(boolean z) {
        this.b.o(z);
        return true;
    }

    protected int c(String str, String str2) {
        return 0;
    }

    protected int c(String str, String str2, String str3) {
        if (K() != 0) {
            return ErrorCode.GENERAL_INIT_ERROR;
        }
        int iB = this.f.b(str, str2, str3);
        com.unisound.common.r.c("SpeechUnderstanderInterface : insertVocab_ext -> inserVocabResult = " + iB);
        if (iB != 0) {
            com.unisound.common.r.e("SpeechUnderstanderInterface : insertVocab_ext error ");
            j(ErrorCode.ASR_SDK_FIX_INSERTVOCAB_EXT_ERROR);
        }
        return iB;
    }

    @Override // com.unisound.sdk.m
    protected void c(int i) {
        super.c(i);
        this.M = i;
    }

    @Override // com.unisound.sdk.m
    protected void cancel() {
        synchronized (this.mLooper) {
            this.al = true;
            this.E = true;
            this.F = true;
            this.o.c(true);
            super.cancel();
            while (this.ao) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.ah.removeCallbacksAndMessages(null);
            this.A.onEvent(SpeechConstants.ASR_EVENT_CANCEL, (int) System.currentTimeMillis());
            com.unisound.common.r.c("SpeechunderStanderInterface: cancel called");
        }
    }

    @Override // com.unisound.sdk.m
    protected int d(int i) {
        return super.d(i);
    }

    protected int d(String str) {
        return 0;
    }

    protected int d(String str, String str2) {
        return 0;
    }

    @Override // com.unisound.sdk.m
    protected int e(int i) {
        return super.e(i);
    }

    protected int e(String str) {
        return K() != 0 ? ErrorCode.GENERAL_INIT_ERROR : this.f.h(str);
    }

    protected int e(String str, String str2) {
        return K() != 0 ? ErrorCode.GENERAL_INIT_ERROR : b(str2, str);
    }

    protected int f(String str) {
        return 0;
    }

    protected int f(String str, String str2) {
        return K() != 0 ? ErrorCode.GENERAL_INIT_ERROR : this.f.a(str, str2);
    }

    protected void f(int i) {
        Handler handler = this.ah;
        if (handler == null) {
            com.unisound.common.r.e("SpeechUnderstander -> doUploadUserData handler is null");
        } else if (i == 0) {
            handler.sendEmptyMessage(16);
        } else {
            k(i);
        }
    }

    protected void g(int i) {
        if (i == 0) {
            return;
        }
        Message message = new Message();
        message.what = 53;
        message.obj = Integer.valueOf(i);
        this.ah.sendMessage(message);
    }

    @Override // com.unisound.sdk.m
    protected Object getOption(int i) {
        if (i == 1001) {
            return Integer.valueOf(this.mSpeechUnderstanderParams.j());
        }
        if (i == 1014) {
            return Integer.valueOf(this.o.b());
        }
        if (i == 1023) {
            return this.B.w();
        }
        if (i == 1036) {
            return com.unisound.common.k.b(this.af);
        }
        if (i == 1069) {
            return Integer.valueOf(this.b.E());
        }
        if (i == 1090) {
            return Integer.valueOf(z());
        }
        if (i == 3150) {
            return Float.valueOf(this.b.A());
        }
        if (i == 1003) {
            return Boolean.valueOf(this.mSpeechUnderstanderParams.c());
        }
        if (i == 1004) {
            return this.mSpeechUnderstanderParams.b();
        }
        if (i == 1020) {
            return Boolean.valueOf(this.B.v());
        }
        if (i == 1021) {
            return this.B.n();
        }
        if (i == 1044) {
            return Integer.valueOf(this.b.d());
        }
        if (i == 1045) {
            return Integer.valueOf(this.mSpeechUnderstanderParams.i());
        }
        switch (i) {
            case 1008:
                return this.mSpeechUnderstanderParams.d();
            case 1009:
                return this.mSpeechUnderstanderParams.a();
            case 1010:
                return Integer.valueOf(this.b.s());
            case 1011:
                return Integer.valueOf(this.b.t());
            case 1012:
                return this.G;
            default:
                switch (i) {
                    case 1030:
                        return this.B.i();
                    case SpeechConstants.GENERAL_CITY /* 1031 */:
                        return this.B.j();
                    case 1032:
                        return this.B.m();
                    default:
                        switch (i) {
                            case 1076:
                                return Boolean.valueOf(this.b.ax);
                            case SpeechConstants.ASR_OPT_LOADGRAMMA_TAG_AND_STATUS /* 1077 */:
                                return this.b.P();
                            case SpeechConstants.ASR_OPT_TIMEOUT_STATUS /* 1078 */:
                                return this.b.G();
                            default:
                                switch (i) {
                                    case SpeechConstants.ASR_OPT_RECOGNIZE_SCENE /* 1082 */:
                                        return Integer.valueOf(this.b.U());
                                    case SpeechConstants.ASR_OPT_RECOGNIZE_MODEL_ID /* 1083 */:
                                        return Integer.valueOf(this.b.Q());
                                    case 1084:
                                        return Integer.valueOf(this.b.S());
                                    default:
                                        switch (i) {
                                            case SpeechConstants.ASR_OPT_MODEL_LIST /* 1086 */:
                                                return this.b.R();
                                            case SpeechConstants.ASR_SUBDOMAIN /* 1087 */:
                                                return this.mSpeechUnderstanderParams.l();
                                            case 1088:
                                                return L();
                                            default:
                                                return super.getOption(i);
                                        }
                                }
                        }
                }
        }
    }

    @Override // com.unisound.sdk.m
    protected String getVersion() {
        AssetManager assets = this.l.getAssets();
        StringBuffer stringBuffer = new StringBuffer();
        try {
            InputStream inputStreamOpen = assets.open("version/data");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamOpen));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    String strS = s(stringBuffer.toString());
                    inputStreamOpen.close();
                    bufferedReader.close();
                    return strS;
                }
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return super.getVersion();
        }
    }

    @Override // com.unisound.sdk.m
    protected String h() {
        return this.H != 1 ? super.h() : "";
    }

    protected int init(String str) {
        bf bfVar;
        if (str == null) {
            str = "";
        }
        if (str != "") {
            Map<Integer, Object> mapA = com.unisound.common.o.a(str, this.mSpeechUnderstanderParams.m());
            Iterator<Integer> it = mapA.keySet().iterator();
            while (it.hasNext()) {
                int iIntValue = it.next().intValue();
                if (mapA.get(Integer.valueOf(iIntValue)) != null) {
                    setOption(iIntValue, mapA.get(Integer.valueOf(iIntValue)));
                }
            }
        } else {
            com.unisound.common.r.c("SpeechUnderStanderInterface : init json is an empty string!");
        }
        if (this.aj) {
            HandlerThread handlerThread = new HandlerThread("ht_outer");
            this.ag = handlerThread;
            handlerThread.start();
            bfVar = new bf(this, this.ag.getLooper());
        } else {
            bfVar = new bf(this, this.af.getMainLooper());
        }
        this.ah = bfVar;
        return B();
    }

    @Override // com.unisound.sdk.m
    protected void j() {
        this.ah.sendEmptyMessage(13);
    }

    @Override // com.unisound.sdk.m
    protected void k() {
        this.ah.sendEmptyMessage(20);
    }

    @Override // com.unisound.sdk.m
    protected void m() {
        this.o.d();
        this.ah.sendEmptyMessage(12);
    }

    @Override // com.unisound.sdk.m
    protected void o() {
        if (this.F && this.E) {
            this.e.a(false);
            this.o.c(false);
            com.unisound.common.r.e("SpeechUnderstander fixend&netend doRecordingStart cancel");
        }
    }

    @Override // com.unisound.sdk.m
    protected void p() {
    }

    @Override // com.unisound.sdk.m
    public void postRecordingStartStatus() {
        super.postRecordingStartStatus();
        this.ah.sendEmptyMessage(11);
    }

    @Override // com.unisound.sdk.m
    protected void q() {
        this.ah.sendEmptyMessage(14);
    }

    @Override // com.unisound.sdk.m
    protected void s() {
        this.ah.sendEmptyMessage(8);
    }

    @Override // com.unisound.sdk.m
    protected int setAudioSource(IAudioSource iAudioSource) {
        return super.setAudioSource(iAudioSource);
    }

    protected void setListener(SpeechUnderstanderListener speechUnderstanderListener) {
        this.A = speechUnderstanderListener;
    }

    protected String setOnlineWakeupWord(List<String> list) {
        int i;
        cc ccVar = new cc(this.l, this.y);
        if (K() != 0) {
            i = ErrorCode.GENERAL_INIT_ERROR;
        } else {
            int size = list.size();
            if (size <= 5) {
                int i2 = -1;
                if (list == null || size <= 0) {
                    return ccVar.a(-1);
                }
                for (int i3 = 0; i3 < size; i3++) {
                    if (cc.a(list.get(i3))) {
                        i = -63601;
                    }
                }
                String strA = ccVar.a(com.unisound.common.k.x, ccVar.a(list));
                if (strA.contains("status")) {
                    try {
                        i2 = Integer.parseInt(new JSONObject(strA).getString("status"));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }
                if (i2 == 0) {
                    HashSet hashSet = new HashSet();
                    hashSet.addAll(list);
                    ccVar.a(hashSet);
                }
                return strA;
            }
            i = -63602;
        }
        return ccVar.a(i);
    }

    @Override // com.unisound.sdk.m
    protected void setOption(int i, Object obj) {
        String str;
        if (i == 1003) {
            try {
                boolean zBooleanValue = ((Boolean) obj).booleanValue();
                b(zBooleanValue);
                this.mSpeechUnderstanderParams.a(zBooleanValue);
                return;
            } catch (Exception unused) {
                str = "set asr_voice_field Error.";
            }
        } else if (i == 1004) {
            try {
                String str2 = (String) obj;
                r(str2);
                this.mSpeechUnderstanderParams.b(str2);
                return;
            } catch (Exception unused2) {
                str = "set asr_language Error.";
            }
        } else if (i == 1024) {
            try {
                m(String.valueOf(obj));
                return;
            } catch (Exception unused3) {
                str = "set nlu_ver Error.";
            }
        } else if (i == 1025) {
            try {
                n(String.valueOf(obj));
                return;
            } catch (Exception unused4) {
                str = "set nlu_appver Error.";
            }
        } else if (i == 1060) {
            try {
                this.b.f(((Integer) obj).intValue());
                this.mSpeechUnderstanderParams.a(((Integer) obj).intValue());
                return;
            } catch (Exception unused5) {
                str = "set asr_front_cache_time Error.";
            }
        } else if (i == 1061) {
            try {
                this.b.f(((Boolean) obj).booleanValue());
                this.mSpeechUnderstanderParams.b(((Boolean) obj).booleanValue());
                return;
            } catch (Exception unused6) {
                str = "set asr_vad_enable Error.";
            }
        } else if (i == 1089) {
            try {
                this.b.f((String) obj);
                return;
            } catch (Exception unused7) {
                str = "set ASR_OPT_ACTIVATE_MEMO Error.";
            }
        } else if (i == 1090) {
            try {
                int iIntValue = ((Integer) obj).intValue();
                if (A() <= 1 || d(iIntValue) != 0) {
                    return;
                }
                this.b.m(iIntValue);
                return;
            } catch (Exception unused8) {
                str = "set WAKEUP_WORK_ENGINE Error.";
            }
        } else if (i == 6000) {
            try {
                this.b.s((String) obj);
                return;
            } catch (Exception unused9) {
                str = "set UploadUserDataServer Error.";
            }
        } else {
            if (i != 6001) {
                switch (i) {
                    case 1001:
                        if (this.mSpeechUnderstanderParams.j() != 1) {
                            try {
                                int iIntValue2 = ((Integer) obj).intValue();
                                if (iIntValue2 == 0 || iIntValue2 == 1 || iIntValue2 == 2) {
                                    if (this.H != iIntValue2) {
                                        this.N = false;
                                        this.H = iIntValue2;
                                    }
                                    this.mSpeechUnderstanderParams.c(iIntValue2);
                                } else {
                                    com.unisound.common.r.e("USCMixRecognizer.setOption unkown value " + iIntValue2);
                                }
                            } catch (Exception unused10) {
                                str = "set asr_service_mode Error.";
                            }
                        } else {
                            str = "SpeechUnderstanderInterface: current SDK didn't support local function,Please contanct unisound for more function!";
                        }
                        break;
                    case 1014:
                        try {
                            i(((Integer) obj).intValue());
                        } catch (Exception unused11) {
                            str = "set asr_net_timeOut Error.";
                        }
                        break;
                    case 1044:
                        try {
                            h(((Integer) obj).intValue());
                        } catch (Exception unused12) {
                            str = "set asr_sampling_rate Error.";
                        }
                        break;
                    case SpeechConstants.ASR_OPT_ONESHOT_CACHE_TIME /* 1069 */:
                        try {
                            this.b.i(((Integer) obj).intValue());
                        } catch (Exception unused13) {
                            str = "set OneShot cache time Error.";
                        }
                        break;
                    case SpeechConstants.ASR_OPT_FRONT_RESET_CACHE_BYTE_TIME /* 1070 */:
                        try {
                            this.b.g(((Integer) obj).intValue());
                        } catch (Exception unused14) {
                            str = "set Front_reset_cache_byte_time Error.";
                        }
                        break;
                    case SpeechConstants.ASR_OPT_DEBUG_SAVELOG /* 1071 */:
                        try {
                            com.unisound.common.r.n = ((Boolean) obj).booleanValue();
                        } catch (Exception unused15) {
                            str = "set DEBUG_SAVELOG Error.";
                        }
                        break;
                    case 1072:
                        try {
                            com.unisound.common.r.o = ((Boolean) obj).booleanValue();
                            break;
                        } catch (Exception unused16) {
                            com.unisound.common.r.e("set DEBUG_POSTLOG Error.");
                            break;
                        }
                    case SpeechConstants.ASR_OPT_USE_HANDLERTHREAD /* 1073 */:
                        try {
                            this.aj = ((Boolean) obj).booleanValue();
                        } catch (Exception unused17) {
                            str = "set USE_HANDLERTHREAD Error.";
                        }
                        break;
                    case SpeechConstants.ASR_OPT_SAVE_AFTERVAD_RECORDING_DATA /* 1074 */:
                        try {
                            this.b.e((String) obj);
                        } catch (Exception unused18) {
                            str = "set SAVE_AFTERVAD_RECORDING_DATA Error.";
                        }
                        break;
                    case SpeechConstants.ASR_OPT_MARK_VAD /* 1075 */:
                        try {
                            this.b.h(((Boolean) obj).booleanValue());
                        } catch (Exception unused19) {
                            str = "set MARK_VAD Error.";
                        }
                        break;
                    case 1076:
                        try {
                            this.b.q(((Boolean) obj).booleanValue());
                        } catch (Exception unused20) {
                            str = "set TEMP_RESULT Error.";
                        }
                        break;
                    case SpeechConstants.ASR_OPT_RECOGNITION_FRONT_VAD /* 1079 */:
                        try {
                            this.b.d(((Boolean) obj).booleanValue());
                        } catch (Exception unused21) {
                            com.unisound.common.r.e("set setRecognizeFrontVADEnable Error.");
                        }
                        super.setOption(i, obj);
                        break;
                    case SpeechConstants.ASR_SUBDOMAIN /* 1087 */:
                        try {
                            String str3 = (String) obj;
                            q(str3);
                            this.mSpeechUnderstanderParams.g(str3);
                        } catch (Exception unused22) {
                            str = "set asr_subdomain Error.";
                        }
                        break;
                    case SpeechConstants.ASR_OPT_WX_SERVICE /* 1095 */:
                        try {
                            this.b.l(((Boolean) obj).booleanValue());
                        } catch (Exception unused23) {
                            str = "set setWxServiceEnabled Error.";
                        }
                        break;
                    case SpeechConstants.WAKEUP_OPT_THRESHOLD_VALUE /* 3150 */:
                        try {
                            this.b.a(((Float) obj).floatValue());
                        } catch (Exception unused24) {
                            str = "set wakeup_threshold_value Error.";
                        }
                        break;
                    default:
                        switch (i) {
                            case 1007:
                                try {
                                    String str4 = (String) obj;
                                    this.b.n(str4);
                                    this.mSpeechUnderstanderParams.f(str4);
                                } catch (Exception unused25) {
                                    str = "set asr_online_oneshot_server_address Error.";
                                }
                                break;
                            case 1008:
                                try {
                                    String str5 = (String) obj;
                                    p(str5);
                                    this.mSpeechUnderstanderParams.c(str5);
                                } catch (Exception unused26) {
                                    str = "set asr_domain Error.";
                                }
                                break;
                            case 1009:
                                try {
                                    String str6 = (String) obj;
                                    if (this.b.m(str6)) {
                                        this.mSpeechUnderstanderParams.a(str6);
                                    } else {
                                        k(ErrorCode.ASR_SDK_SET_ASR_SERVER_ADDR_ERROR);
                                    }
                                } catch (Exception unused27) {
                                    str = "set asr_server_address Error.";
                                }
                                break;
                            default:
                                switch (i) {
                                    case 1020:
                                        try {
                                            c(((Boolean) obj).booleanValue());
                                        } catch (Exception unused28) {
                                            str = "set nlu_enable Error.";
                                        }
                                        break;
                                    case 1021:
                                        try {
                                            g((String) obj);
                                        } catch (Exception unused29) {
                                            str = "set nlu_scenario Error.";
                                        }
                                        break;
                                    case 1022:
                                        try {
                                            String str7 = (String) obj;
                                            if (str7 != null && str7.contains(":")) {
                                                String[] strArrSplit = str7.split(":");
                                                try {
                                                    a(strArrSplit[0], Integer.parseInt(strArrSplit[1]));
                                                    this.mSpeechUnderstanderParams.d(str7);
                                                    break;
                                                } catch (NumberFormatException unused30) {
                                                }
                                            }
                                            com.unisound.common.r.e("nlu server set Error.");
                                        } catch (Exception unused31) {
                                            str = "set nlu_server_address Error.";
                                        }
                                        break;
                                    default:
                                        switch (i) {
                                            case 1030:
                                                try {
                                                    h((String) obj);
                                                } catch (Exception unused32) {
                                                    str = "set history Error.";
                                                }
                                                break;
                                            case SpeechConstants.GENERAL_CITY /* 1031 */:
                                                try {
                                                    j((String) obj);
                                                } catch (Exception unused33) {
                                                    str = "set city Error.";
                                                }
                                                break;
                                            case 1032:
                                                try {
                                                    k((String) obj);
                                                } catch (Exception unused34) {
                                                    str = "set voiceID Error.";
                                                }
                                                break;
                                            case SpeechConstants.GENERAL_GPS /* 1033 */:
                                                try {
                                                    l((String) obj);
                                                } catch (Exception unused35) {
                                                    str = "set gps Error.";
                                                }
                                                break;
                                            default:
                                                switch (i) {
                                                    case SpeechConstants.ASR_OPT_ONESHOT_VADBACKSIL_TIME /* 1081 */:
                                                        try {
                                                            this.b.h(((Integer) obj).intValue());
                                                        } catch (Exception unused36) {
                                                            str = "set OneShot VAD back sil time Error.";
                                                        }
                                                        break;
                                                    case SpeechConstants.ASR_OPT_RECOGNIZE_SCENE /* 1082 */:
                                                        try {
                                                            this.b.q(((Integer) obj).intValue());
                                                        } catch (Exception unused37) {
                                                            str = "set RECOGNIZE_SCENE Error.";
                                                        }
                                                        break;
                                                    case SpeechConstants.ASR_OPT_RECOGNIZE_MODEL_ID /* 1083 */:
                                                        try {
                                                            boolean zN = this.b.n(((Integer) obj).intValue());
                                                            if (!zN) {
                                                                k(ErrorCode.ASR_SDK_SET_RECOGNIZE_MODEL_ID_ERROR);
                                                            }
                                                            com.unisound.common.r.c("SpeechUnderstanderInterface", "set recognize modelId " + obj + ", success = " + zN);
                                                        } catch (Exception unused38) {
                                                            str = "set RECOGNIZE_MODEL_ID Error.";
                                                        }
                                                        break;
                                                    case 1084:
                                                        try {
                                                            boolean zO = this.b.o(((Integer) obj).intValue());
                                                            if (!zO) {
                                                                k(ErrorCode.ASR_SDK_SET_WAKEUP_MODEL_ID_ERROR);
                                                            }
                                                            com.unisound.common.r.c("SpeechUnderstanderInterface", "set wakeup modelId " + obj + ", success = " + zO);
                                                        } catch (Exception unused39) {
                                                            str = "set WAKEUP_MODEL_ID Error.";
                                                        }
                                                        break;
                                                    case SpeechConstants.ASR_OPT_ALREAD_AWPE /* 1085 */:
                                                        try {
                                                            this.b.s(((Boolean) obj).booleanValue());
                                                        } catch (Exception unused40) {
                                                            str = "set ALREAD_AWPE Error.";
                                                        }
                                                        break;
                                                    default:
                                                        super.setOption(i, obj);
                                                        break;
                                                }
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                        break;
                }
                return;
            }
            try {
                this.b.t((String) obj);
                return;
            } catch (Exception unused41) {
                str = "set setUploadUserDataServerUrl Error.";
            }
        }
        com.unisound.common.r.e(str);
    }

    @Override // com.unisound.sdk.m
    protected void start() {
        b(this.f.e);
    }

    @Override // com.unisound.sdk.m
    protected void stop() {
        super.stop();
        this.D = null;
        this.o.d();
    }

    protected void uploadUserData(Map<Integer, List<String>> map) {
        this.o.a(map);
    }

    @Override // com.unisound.sdk.m
    protected int w() {
        return super.w();
    }

    @Override // com.unisound.sdk.m
    protected String x() {
        return super.x();
    }

    @Override // com.unisound.sdk.m
    protected String y() {
        return super.y();
    }

    @Override // com.unisound.sdk.m
    protected int z() {
        return super.z();
    }
}
