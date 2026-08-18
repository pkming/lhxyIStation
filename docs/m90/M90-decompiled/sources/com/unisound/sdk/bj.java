package com.unisound.sdk;

import cn.yunzhisheng.tts.JniClient;
import com.unisound.client.ErrorCode;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/* JADX INFO: loaded from: classes2.dex */
public class bj extends bh {
    private bn c;
    private bk d;
    private String e;

    public bj(String str, bk bkVar) {
        super(bkVar.q().booleanValue(), bkVar.l());
        this.c = null;
        this.e = str;
        this.d = bkVar;
    }

    private String a(bk bkVar) {
        StringBuilder sb = new StringBuilder();
        if (bkVar != null) {
            int iU = bkVar.u();
            if (iU != 50) {
                sb.append("pit=").append(iU).append(";");
            }
            int iT = bkVar.t();
            if (iT != 50) {
                sb.append("spd=").append(iT).append(";");
            }
            int iV = bkVar.v();
            if (iV != 50) {
                sb.append("vol=").append(iV).append(";");
            }
            String strS = bkVar.s();
            if (strS != bk.j) {
                sb.append("vcn=").append(strS).append(";");
            }
            int iN = bkVar.n();
            if (iN != 100) {
                sb.append("smt=").append(iN).append(";");
            }
            int iO = bkVar.o();
            if (iO != 100) {
                sb.append("emt=").append(iO).append(";");
            }
            boolean zBooleanValue = bkVar.m().booleanValue();
            if (zBooleanValue) {
                sb.append("e2c=").append(zBooleanValue).append(";");
            }
        }
        if (sb.length() > 0) {
            return sb.toString();
        }
        return null;
    }

    public static String a(String str) {
        if (str == null) {
            return "";
        }
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void a(byte[] bArr) {
        bn bnVar = this.c;
        if (bnVar != null) {
            bnVar.a(bArr, bArr.length);
        }
    }

    private void b(int i) {
        bn bnVar = this.c;
        if (bnVar != null) {
            bnVar.a(i);
        }
    }

    private void i() {
        bn bnVar = this.c;
        if (bnVar != null) {
            bnVar.a();
        }
    }

    private void j() {
        bn bnVar = this.c;
        if (bnVar != null) {
            bnVar.b();
        }
    }

    public void a(bn bnVar) {
        this.c = bnVar;
    }

    @Override // com.unisound.sdk.bh
    public void b() {
        super.b();
    }

    public void b(bn bnVar) {
        this.c = bnVar;
    }

    public void g() {
        this.c = null;
    }

    public String h() {
        return null;
    }

    @Override // com.unisound.common.f, java.lang.Thread, java.lang.Runnable
    public void run() {
        com.unisound.common.a aVarF;
        byte[] bArrD;
        super.run();
        com.unisound.common.r.b("TTSOnlineSynthesizerThread run()：synthesizer start");
        JniClient jniClient = new JniClient();
        try {
            try {
                aVarF = this.d.f();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!jniClient.a(this.d.A(), aVarF.a(), aVarF.c())) {
                com.unisound.common.r.e("TTSOnlineSynthesizerThread getTTSData: TTSThread:create error appkey: " + this.d.A() + " ip:" + aVarF.a() + " port: " + aVarF.c());
                b(ErrorCode.TTS_ERROR_ONLINE_SYNTHESIZER_INIT);
                return;
            }
            jniClient.a(8, com.unisound.common.k.q);
            jniClient.a(14, com.unisound.common.k.x);
            jniClient.a(22, com.unisound.common.k.x);
            StringBuilder sb = new StringBuilder();
            sb.append(com.unisound.common.k.s).append(":");
            sb.append(0).append(":");
            sb.append(com.unisound.common.k.r).append(":");
            sb.append(this.d.a).append(":");
            sb.append(com.unisound.common.k.t).append(":");
            sb.append(com.unisound.common.af.a);
            sb.append("\t" + com.unisound.common.r.q + ":" + JniClient.c + ":" + JniClient.d);
            com.unisound.common.r.b("TTSOnlineSynthesizerThread getTTSData: TTS_OPT_CLIENT_INFO: " + sb.toString());
            jniClient.a(15, sb.toString());
            String strA = a(this.d);
            if (strA != null) {
                com.unisound.common.r.b("TTSOnlineSynthesizerThread getTTSData: ParamString(): " + strA);
                jniClient.a(104, strA);
            }
            if (this.d.j() != null) {
                jniClient.a(203, this.d.j());
            }
            if (this.d.B() != null) {
                jniClient.a(204, this.d.B());
            }
            b bVarB = this.d.b();
            int iA = jniClient.a(bVarB.b(), bVarB.a());
            if (iA != 0) {
                com.unisound.common.r.e("TTSOnlineSynthesizerThread getTTSData: jni.start error " + iA + " audioFormat.toParamString(): " + bVarB.b() + " audioFormat.getEncode(): " + bVarB.a());
                b(iA);
                return;
            }
            int iB = jniClient.b(this.e);
            if (iB != 0) {
                com.unisound.common.r.e("TTSOnlineSynthesizerThread getTTSData: jni.textPut error " + iB);
                b(iB);
                return;
            }
            i();
            loop0: while (true) {
                int i = 0;
                while (!a() && jniClient.m.b != 2 && i < 10) {
                    com.unisound.common.r.f("TTSOnlineSynthesizerThread run : jni.getResult() before");
                    bArrD = jniClient.d();
                    com.unisound.common.r.f("TTSOnlineSynthesizerThread run : jni.getResult() after");
                    if (bArrD == null) {
                        i++;
                        int i2 = jniClient.m.c;
                        if (i2 != 0) {
                            com.unisound.common.r.e("TTSOnlineSynthesizerThread getTTSData: jni.getResult() error" + i2);
                        }
                    }
                }
                a(bArrD);
            }
            jniClient.c();
            j();
            jniClient.b();
            com.unisound.common.r.b("TTSOnlineSynthesizerThread run()：synthesizer end");
        } finally {
            jniClient.b();
        }
    }
}
