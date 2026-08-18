package com.unisound.sdk;

import android.content.Context;
import android.security.KeyChain;
import cn.yunzhisheng.asr.JniUscClient;
import com.unisound.client.ErrorCode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class ai extends Thread {
    private static final int a = 30;
    private an e;
    private String i;
    private Context l;
    private ag b = null;
    private BlockingQueue<byte[]> c = new LinkedBlockingQueue();
    private ae d = new ae();
    private String f = "";
    private volatile boolean g = false;
    private String h = "";
    private String j = "";
    private String k = "";

    public ai(an anVar, Context context, String str) {
        this.i = "";
        this.l = context;
        this.e = anVar;
        ae.a = true;
        this.i = str;
    }

    private void a(int i, JniUscClient jniUscClient) {
        ag agVar = this.b;
        b();
        com.unisound.common.r.a(jniUscClient);
        if (agVar != null) {
            agVar.a(i);
        }
    }

    private void a(JniUscClient jniUscClient) {
        String str;
        ch chVarY = this.e.Y();
        if (chVarY.d()) {
            jniUscClient.a(1015, 8);
            jniUscClient.a(1020, 1);
            jniUscClient.a(1019, this.e.aj());
            StringBuilder sb = new StringBuilder();
            sb.append("type=");
            if (chVarY.e() != 1) {
                str = chVarY.e() == 2 ? "matchSingle" : "register";
                sb.append(";");
                sb.append("userName=").append(chVarY.b()).append(";");
                sb.append("appkey=").append(this.e.ab()).append(";");
                sb.append("returnType=").append("json").append(";");
                sb.append("scene=").append(chVarY.a()).append(";");
                com.unisound.common.r.c("vpr params:  " + sb.toString());
                this.j = sb.toString();
                HashMap map = new HashMap();
                map.put("vpr_init", String.valueOf(8));
                map.put("vpr_md5_check", String.valueOf(1));
                map.put("vpr_secret", this.e.aj());
                map.put("vpr_sendParams", sb.toString());
                com.unisound.common.r.a(com.unisound.common.r.t, (String) null, map, (String) null, (String) null, (String) null);
            }
            sb.append(str);
            sb.append(";");
            sb.append("userName=").append(chVarY.b()).append(";");
            sb.append("appkey=").append(this.e.ab()).append(";");
            sb.append("returnType=").append("json").append(";");
            sb.append("scene=").append(chVarY.a()).append(";");
            com.unisound.common.r.c("vpr params:  " + sb.toString());
            this.j = sb.toString();
            HashMap map2 = new HashMap();
            map2.put("vpr_init", String.valueOf(8));
            map2.put("vpr_md5_check", String.valueOf(1));
            map2.put("vpr_secret", this.e.aj());
            map2.put("vpr_sendParams", sb.toString());
            com.unisound.common.r.a(com.unisound.common.r.t, (String) null, map2, (String) null, (String) null, (String) null);
        }
    }

    private void a(JniUscClient jniUscClient, com.unisound.common.ac acVar) {
        if (acVar == null || !acVar.a()) {
            return;
        }
        com.unisound.common.r.e("updateAsrScene " + acVar.c() + " res : " + jniUscClient.a(31, acVar.c()));
        acVar.a(false);
    }

    private void a(JniUscClient jniUscClient, String str) {
        bz bzVarZ = this.e.Z();
        bzVarZ.b(System.currentTimeMillis());
        if (bzVarZ == null || !bzVarZ.v()) {
            return;
        }
        this.k = bzVarZ.x();
        com.unisound.common.r.c("NetRecognition --> " + str + " NluParams : " + bzVarZ.x());
        HashMap map = new HashMap();
        map.put("nlu_sendParams", bzVarZ.x());
        com.unisound.common.r.a(com.unisound.common.r.u, (String) null, map, (String) null, (String) null, str);
    }

    private void a(String str, boolean z) {
        ag agVar = this.b;
        if (agVar != null) {
            agVar.a(str, z);
        }
    }

    private String[] a(String str) {
        int i = 0;
        if (str.contains("}{\"asr_recongize\"")) {
            String[] strArrSplit = str.split("\\}\\{\"asr_recongize\"");
            while (i < strArrSplit.length) {
                if (i == 0) {
                    strArrSplit[i] = strArrSplit[i] + "}";
                } else if (i == strArrSplit.length - 1) {
                    strArrSplit[i] = "{\"asr_recongize\"" + strArrSplit[i];
                } else {
                    strArrSplit[i] = "{\"asr_recongize\"" + strArrSplit[i] + "}";
                }
                i++;
            }
            return strArrSplit;
        }
        if (!str.contains("}{\"gender\"")) {
            return new String[]{str};
        }
        String[] strArrSplit2 = str.split("\\}\\{\"gender\"");
        while (i < strArrSplit2.length) {
            if (i == 0) {
                strArrSplit2[i] = strArrSplit2[i] + "}";
            } else if (i == strArrSplit2.length - 1) {
                strArrSplit2[i] = "{\"gender\"" + strArrSplit2[i];
            } else {
                strArrSplit2[i] = "{\"gender\"" + strArrSplit2[i] + "}";
            }
            i++;
        }
        return strArrSplit2;
    }

    private void b(JniUscClient jniUscClient) {
        StringBuilder sb = new StringBuilder();
        sb.append("PN=" + com.unisound.common.k.s).append(":");
        sb.append("OS=0").append(":");
        sb.append("CR=" + com.unisound.common.k.r).append(":");
        sb.append("NT=" + this.e.aP).append(":");
        sb.append("MD=" + com.unisound.common.k.t).append(":");
        sb.append("SV=" + com.unisound.common.af.a()).append(":");
        sb.append("RPT=" + this.e.an()).append(":");
        sb.append("SID=" + this.e.ak()).append(":");
        sb.append("NPT=" + this.e.ap()).append(":");
        sb.append("IP=" + com.unisound.common.x.a(this.l)).append(":");
        sb.append("EC=" + this.e.ao());
        sb.append("\t" + com.unisound.common.r.q + ":" + JniUscClient.k + ":" + JniUscClient.l);
        jniUscClient.a(15, sb.toString());
        this.e.s(0);
        this.e.b(0L);
        this.e.q("");
        HashMap map = new HashMap();
        map.put("nlu_sendParams", sb.toString());
        com.unisound.common.r.c("collected_info = " + sb.toString());
        com.unisound.common.r.a(com.unisound.common.r.v, (String) null, map, (String) null, (String) null, (String) null);
    }

    private void c(JniUscClient jniUscClient) {
        g gVarAc = this.e.ac();
        if (gVarAc.a()) {
            HashMap map = new HashMap();
            jniUscClient.a(34, gVarAc.toString());
            map.put("engine_parameter", gVarAc.toString());
            com.unisound.common.r.c("NetRecognition --> AsrParams : " + gVarAc.toString().replaceAll(":", "=").replaceAll("\\n", ";"));
            a(jniUscClient, this.e.aa());
            if (this.e.aw) {
                jniUscClient.a(20, this.e.az ? JniUscClient.q : JniUscClient.r);
            }
            if (this.e.aM != 0) {
                jniUscClient.a(32, this.e.aM);
            }
            jniUscClient.a(this.e.ag());
        }
        jniUscClient.a(9, this.e.ab());
        jniUscClient.a(8, com.unisound.common.k.q);
        jniUscClient.a(14, com.unisound.common.k.x);
        jniUscClient.a(22, com.unisound.common.k.x);
        com.unisound.common.r.c("NetRecognition --> appkey = " + this.e.ab() + ", imei = " + com.unisound.common.k.q + ", userId = " + com.unisound.common.k.x + ", udid = " + com.unisound.common.k.x);
        if (this.e.ai()) {
            jniUscClient.a(17, JniUscClient.o);
        }
    }

    private void d(JniUscClient jniUscClient) {
        ag agVar = this.b;
        b();
        com.unisound.common.r.a(jniUscClient);
        if (agVar != null) {
            agVar.h();
        }
    }

    private void e(JniUscClient jniUscClient) {
        ag agVar = this.b;
        b();
        com.unisound.common.r.a(jniUscClient);
        if (agVar != null) {
            agVar.l();
        }
    }

    private JniUscClient h() {
        StringBuilder sbAppend;
        String str;
        JniUscClient jniUscClient = new JniUscClient();
        com.unisound.common.r.a(com.unisound.common.r.r, (String) null, (Map<String, String>) null, (String) null, (String) null, (String) null);
        com.unisound.common.a aVarAd = this.e.ad();
        long jA = jniUscClient.a(aVarAd.a(), aVarAd.c());
        com.unisound.common.r.c("NetRecognition -- > server = " + aVarAd.a() + " port = " + aVarAd.c());
        HashMap map = new HashMap();
        map.put("server", aVarAd.a());
        map.put(KeyChain.EXTRA_PORT, String.valueOf(aVarAd.c()));
        com.unisound.common.r.a(com.unisound.common.r.s, (String) null, map, (String) null, (String) null, (String) null);
        if (jA == 0) {
            com.unisound.common.r.a(com.unisound.common.r.s, "error", map, (String) null, (String) null, "handle=0");
            com.unisound.common.r.c(toString() + "juc.create() returns " + jA);
        }
        jniUscClient.a(0, this.e.aR);
        jniUscClient.a(1, this.e.aS);
        jniUscClient.a(6, this.e.aQ);
        jniUscClient.a(4, this.e.aT);
        HashMap map2 = new HashMap();
        map2.put("enable_vad", String.valueOf(this.e.aR));
        map2.put("vad_timeout", String.valueOf(this.e.aS));
        map2.put("pcm_compress", String.valueOf(this.e.aQ));
        map2.put("result_timeout", String.valueOf(this.e.aT));
        com.unisound.common.r.a(com.unisound.common.r.s, (String) null, map2, (String) null, (String) null, (String) null);
        a(jniUscClient);
        a(jniUscClient, "Start");
        c(jniUscClient);
        b(jniUscClient);
        if (this.e.Y().d() && !this.e.Z().v() && !this.e.V()) {
            sbAppend = new StringBuilder().append(this.j);
            str = "filterName=vpr;";
        } else if (!this.e.Y().d() && this.e.Z().v() && !this.e.V()) {
            sbAppend = new StringBuilder().append(this.k);
            str = "filterName=search;";
        } else if (this.e.Y().d() && this.e.Z().v() && !this.e.V()) {
            sbAppend = new StringBuilder().append(this.j).append(this.k);
            str = "filterName=vpr,search;";
        } else if (this.e.Y().d() && !this.e.Z().v() && this.e.V()) {
            sbAppend = new StringBuilder().append(this.j);
            str = "additionalService=wx_adapt;filterName=vpr;";
        } else {
            if (this.e.Y().d() || !this.e.Z().v() || !this.e.V()) {
                if (this.e.Y().d() && this.e.Z().v() && this.e.V()) {
                    sbAppend = new StringBuilder().append(this.j).append(this.k);
                    str = "additionalService=wx_adapt;filterName=vpr,search;";
                }
                com.unisound.common.r.q = 0;
                com.unisound.common.r.c("juc init success");
                return jniUscClient;
            }
            sbAppend = new StringBuilder().append(this.k);
            str = "additionalService=wx_adapt;filterName=search;";
        }
        jniUscClient.a(201, sbAppend.append(str).toString());
        com.unisound.common.r.q = 0;
        com.unisound.common.r.c("juc init success");
        return jniUscClient;
    }

    public void a(ag agVar) {
        this.b = agVar;
    }

    public void a(List<byte[]> list) {
        Iterator<byte[]> it = list.iterator();
        while (it.hasNext()) {
            this.c.add(it.next());
        }
    }

    public void a(byte[] bArr) {
        this.d.a(bArr, 0, bArr.length);
        if (this.d.b()) {
            this.c.add(bArr);
        }
    }

    public boolean a() {
        return this.g;
    }

    public void b() {
        this.g = true;
    }

    public void c() {
        this.b = null;
        this.g = true;
    }

    public boolean d() {
        return this.b == null;
    }

    public String e() {
        return this.f;
    }

    public void f() {
        c();
        if (isAlive()) {
            try {
                join(39000L);
                com.unisound.common.r.c("RecognitionThread::waitEnd()");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String g() {
        return this.h;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        String strC;
        com.unisound.common.r.c("NetRecogniton -> run start");
        com.unisound.common.r.g("RecognitionThread start");
        JniUscClient jniUscClientH = h();
        com.unisound.common.r.c("RecognitionThread : Service Mode =  " + this.e.U());
        jniUscClientH.a(1015, this.e.U());
        if (this.i.equals("")) {
            jniUscClientH.a(9, this.e.ab());
            jniUscClientH.a(204, this.e.aj());
            int iG = jniUscClientH.g();
            com.unisound.common.r.c("NetRecognition --> loginstate = " + iG);
            if (iG != 0) {
                com.unisound.common.r.e("NetRecognition --> Login Error , login = " + iG);
                a(iG, jniUscClientH);
                jniUscClientH.e();
                this.e.ae();
                return;
            }
            strC = jniUscClientH.c(206);
            this.i = strC;
        } else {
            strC = this.i;
        }
        jniUscClientH.a(206, strC);
        com.unisound.common.r.c("NetRecognition --> loginToken = " + this.i);
        this.f = "";
        com.unisound.common.r.c("NetRecognition --> start called ");
        int iA = jniUscClientH.a();
        String strC2 = jniUscClientH.c(21);
        this.f = strC2;
        this.e.q(strC2);
        com.unisound.common.r.c("NetRecognition --> sessionId = " + this.f);
        String str = this.f;
        String strSubstring = (str == null || str.length() <= 10) ? "" : this.f.substring(0, 10);
        if (iA != 0) {
            com.unisound.common.r.a(com.unisound.common.r.w, "error", (Map<String, String>) null, (String) null, String.valueOf(iA), (String) null);
            com.unisound.common.r.c("NetRecognition --> start error occured! , startCode = " + iA + ", sessionId = " + this.f);
            a(iA, jniUscClientH);
            jniUscClientH.e();
            this.e.ae();
            return;
        }
        com.unisound.common.r.a(com.unisound.common.r.w, com.unisound.common.r.C, (Map<String, String>) null, (String) null, (String) null, (String) null);
        if (d()) {
            jniUscClientH.d();
            com.unisound.common.r.a(com.unisound.common.r.z, (String) null, (Map<String, String>) null, (String) null, (String) null, "cancel(start)");
            com.unisound.common.r.c("NetRecognition --> cancel(start)");
            jniUscClientH.e();
            return;
        }
        long length = 0;
        do {
            try {
                byte[] bArrPoll = this.c.poll(30L, TimeUnit.MILLISECONDS);
                if (bArrPoll == null) {
                    if (!this.g && this.c.isEmpty()) {
                        com.unisound.common.r.c("NetRecognition --> break");
                    }
                } else if (bArrPoll.length != 1 || (bArrPoll[0] != 100 && bArrPoll[0] != 99)) {
                    int iA2 = jniUscClientH.a(bArrPoll, bArrPoll.length);
                    length += (long) bArrPoll.length;
                    if (iA2 == 0 || iA2 == 1) {
                        if (this.e.ag()) {
                            String strC3 = jniUscClientH.c(34);
                            this.h = strC3;
                            if (!strC3.equals("")) {
                                StringBuffer stringBuffer = new StringBuffer();
                                stringBuffer.append("-changeable-").append(this.h);
                                com.unisound.common.r.c("NetRecognition --> tempResult = " + this.h);
                                a(stringBuffer.toString(), false);
                            }
                        }
                    } else if (iA2 == 2) {
                        String strC4 = jniUscClientH.c();
                        if ((strC4 != null && !"".equals(strC4)) || this.e.ag()) {
                            com.unisound.common.r.a(com.unisound.common.r.A, "partial", (Map<String, String>) null, "partial", (String) null, (String) null);
                            com.unisound.common.r.b("NetRecognition --> partial=" + strC4);
                            for (String str2 : a(strC4)) {
                                a(str2, false);
                            }
                        }
                    } else if (iA2 == -30002) {
                        com.unisound.common.r.a(com.unisound.common.r.x, (String) null, (Map<String, String>) null, (String) null, (String) null, "max speech timeout");
                        com.unisound.common.r.c("NetRecognition --> max speech timeout");
                        e(jniUscClientH);
                    } else {
                        if (iA2 != -30001) {
                            com.unisound.common.r.a(com.unisound.common.r.x, "error", (Map<String, String>) null, (String) null, String.valueOf(iA2), (String) null);
                            com.unisound.common.r.c("NetRecognition --> error:" + iA2);
                            a(iA2, jniUscClientH);
                            jniUscClientH.e();
                            this.e.ae();
                            return;
                        }
                        com.unisound.common.r.a(com.unisound.common.r.x, (String) null, (Map<String, String>) null, (String) null, (String) null, "vad timeout");
                        com.unisound.common.r.c("NetRecognition --> vad timeout");
                    }
                    if (!this.g) {
                    }
                }
                this.e.a(System.currentTimeMillis());
                this.h = "";
                a(jniUscClientH, "Stop");
                com.unisound.common.r.c("NetRecognition --> stop called , bufferLength = " + length + ", sessionId = " + strSubstring);
                int iB = jniUscClientH.b();
                if (iB < 0) {
                    com.unisound.common.r.c("NetRecognition --> stop error occured! , stopCode = " + iB + ", sessionId = " + this.f);
                    com.unisound.common.r.a(com.unisound.common.r.y, "error", (Map<String, String>) null, (String) null, String.valueOf(iB), (String) null);
                    a(iB, jniUscClientH);
                    jniUscClientH.e();
                    return;
                }
                com.unisound.common.r.a(com.unisound.common.r.y, com.unisound.common.r.C, (Map<String, String>) null, (String) null, (String) null, (String) null);
                if (this.e.aM != 0) {
                    this.e.aO = com.unisound.common.m.a(jniUscClientH.c(25));
                    com.unisound.common.r.c("NetRecognition --> asrRspSpeakerInfo=" + this.e.aO);
                }
                String strC5 = jniUscClientH.c();
                com.unisound.common.r.a(com.unisound.common.r.A, com.unisound.common.r.E, (Map<String, String>) null, strC5, (String) null, (String) null);
                com.unisound.common.r.c("NetRecognition --> lastResult = " + strC5 + ", sessionId = " + strSubstring);
                String[] strArrA = a(strC5);
                for (int i = 0; i < strArrA.length; i++) {
                    if (i != strArrA.length - 1) {
                        a(strArrA[i], false);
                    } else {
                        a(strArrA[i], true);
                    }
                }
                d(jniUscClientH);
                com.unisound.common.r.c("NetRecognition --> released");
                jniUscClientH.e();
                this.c.clear();
                com.unisound.common.r.g("RecognitionThread stop");
                com.unisound.common.r.c("NetRecognition --> run stop");
                return;
            } catch (Exception unused) {
                com.unisound.common.r.e("NetRecognition --> exception");
                com.unisound.common.r.a(com.unisound.common.r.x, "error", (Map<String, String>) null, (String) null, String.valueOf(ErrorCode.RECOGNITION_EXCEPTION), "recognition exception");
                a(ErrorCode.RECOGNITION_EXCEPTION, jniUscClientH);
                JniUscClient.k = ErrorCode.RECOGNITION_EXCEPTION;
                JniUscClient.l = 0;
                jniUscClientH.e();
                return;
            }
        } while (!d());
        jniUscClientH.d();
        com.unisound.common.r.a(com.unisound.common.r.x, (String) null, (Map<String, String>) null, (String) null, (String) null, "cancel(recognizer)");
        com.unisound.common.r.c("NetRecognition --> cancel(recognizer)");
        jniUscClientH.e();
    }
}
