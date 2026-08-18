package com.unisound.sdk;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import cn.yunzhisheng.asr.VAD;
import cn.yunzhisheng.asrfix.JniAsrFix;
import cn.yunzhisheng.nlu.OfflineNlu;
import com.unisound.client.IAudioSource;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class m {
    public static final int OPT_SET_FIX_RESULT_NLU = 5;
    public static final int OPT_SET_FIX_RESULT_NLU_CONFIGPATH = 6;
    public static final int SET_USER_DATA_ERROR = -100;
    public static final int SET_USER_DATA_OK = 0;
    public static final int SET_USER_DATA_WARNING = -200;
    protected k e;
    protected com.unisound.common.aj g;
    protected Context l;
    public Looper mLooper;
    public bg mSpeechUnderstanderParams;
    protected HandlerThread n;
    private r w;
    private ArrayList<byte[]> o = new ArrayList<>();
    private boolean p = false;
    private boolean q = true;
    protected j a = new j();
    protected u b = new u();
    protected v c = null;
    protected x d = null;
    private com.unisound.common.g r = new com.unisound.common.g();
    protected y f = new y();
    protected l h = new l();
    protected a i = new a();
    protected cg j = new cg();
    protected aq k = new aq();
    private boolean s = false;
    protected float m = -8.0f;
    private String t = "";
    private IAudioSource u = null;
    private boolean v = false;
    private com.unisound.common.d x = new o(this);
    private af y = new p(this);

    protected m(Context context, String str) {
        HandlerThread handlerThread = new HandlerThread("ht_NetAndFix");
        this.n = handlerThread;
        handlerThread.start();
        this.mLooper = this.n.getLooper();
        this.e = new k(this.mLooper);
        this.i.a(this.x);
        this.l = context;
        this.mSpeechUnderstanderParams = new bg();
        this.f.a(context.getApplicationContext().getFilesDir() + "/YunZhiSheng/asrfix");
        this.a.c(context.getApplicationContext().getFilesDir() + "/YunZhiSheng/");
        this.r.a(context);
        this.b.e(false);
        this.b.a(3000, 1000);
        this.b.h(str);
        this.e.a(this.b);
        r rVar = new r(this, null);
        this.w = rVar;
        this.e.a(rVar);
        y.m = 1;
        a(new n(this));
    }

    private void B() {
        this.b.a("");
        this.b.a(false);
        this.s = false;
    }

    protected static boolean c(String str) {
        return y.d(str);
    }

    protected int A() {
        return this.e.y();
    }

    protected int a(String str, String str2, String str3) {
        return this.e.a(str, str2, str3);
    }

    protected int a(String str, String str2, String str3, String str4) {
        if (this.f.d(this.l)) {
            return this.f.a(str, str2, str3, str4);
        }
        return -100;
    }

    protected int a(String str, String str2, List<String> list) {
        int iA = a(this.f.b(str), this.f.f(str), this.f.a(str2, list), this.f.g(str));
        com.unisound.common.r.c("FixRecognizerInterface", "loadModel path = " + this.f.g(str) + " , code = " + iA);
        return iA;
    }

    protected int a(String str, boolean z, String str2, String str3) throws Throwable {
        this.f.a(this.l);
        com.unisound.common.r.c("safeLoadMode " + this.f.a);
        if (!this.e.a(this.f.a, str, z, str2, str3)) {
            return -1;
        }
        this.f.b(this.l);
        return 0;
    }

    protected an a() {
        return this.b;
    }

    protected String a(String str, String str2) {
        return this.e.a(str, str2);
    }

    protected void a(int i) {
        if (i != 0) {
            this.e.a(false);
        }
    }

    protected void a(int i, int i2) {
        this.b.a(i, i2);
    }

    protected void a(int i, int i2, Object obj) {
        com.unisound.common.aj ajVar = this.g;
        if (ajVar != null) {
            ajVar.a(i, i2, obj);
        }
    }

    protected void a(int i, String str) {
    }

    protected void a(VAD vad) {
        this.h.c();
    }

    protected void a(com.unisound.common.b bVar) {
        this.i.a(bVar);
    }

    protected void a(com.unisound.common.z zVar) {
        this.k.a(zVar);
    }

    protected void a(t tVar) {
        this.h.a(tVar);
    }

    protected void a(String str, boolean z, int i) {
    }

    protected void a(boolean z, byte[] bArr, int i, int i2) throws Throwable {
        if (this.p) {
            this.o.add(bArr);
        }
        if (this.b.a()) {
            if (bArr.length == 1 && bArr[0] == 99) {
                return;
            }
            com.unisound.common.i.a(bArr, this.b.b());
        }
    }

    protected boolean a(Context context) {
        return com.unisound.common.h.a(context);
    }

    protected boolean a(Message message) {
        return false;
    }

    protected boolean a(String str) {
        return false;
    }

    protected boolean a(String str, boolean z) {
        this.f.a(((str == null || str.length() == 0) ? new StringBuilder().append(this.l.getApplicationContext().getFilesDir()) : new StringBuilder().append(str)).append("/YunZhiSheng/asrfix").toString());
        this.f.a(this.l, y.f);
        if (this.f.a(this.l, z)) {
            return true;
        }
        com.unisound.common.r.e("USCFixRecognizer.initByModelDir init data fail!");
        return false;
    }

    protected boolean a(boolean z) {
        return a((String) null, z);
    }

    protected int b(String str, String str2) {
        return a(str, false, "command", str2);
    }

    protected void b() {
        this.w = new r(this, null);
        this.e.a(this.y);
        this.e.a(this.w);
        this.e.a(this.b);
    }

    protected void b(int i) {
        this.h.a(i);
    }

    protected void b(int i, int i2) {
    }

    protected void b(String str) {
        b();
        this.c = null;
        this.d = null;
        if (this.q) {
            as.n();
            if (this.u == null) {
                this.u = new com.unisound.common.e(this.b);
            }
            com.unisound.common.r.g("FixRecognizerInterface recognizer start");
            this.e.a(str, new as(this.b, this.e, this.u), new x(this.l, this.b, this.e));
        }
        if (this.p) {
            this.o = new ArrayList<>();
        }
        u uVar = this.b;
        uVar.a(com.unisound.common.i.b(uVar.b()));
        if (this.b.a()) {
            this.s = com.unisound.common.i.c(this.b.b());
        }
    }

    protected void b(String str, boolean z) {
    }

    protected boolean b(Context context) {
        return com.unisound.common.h.c(context);
    }

    protected void c(int i) {
    }

    protected boolean c() {
        return this.e.q();
    }

    protected void cancel() {
        this.w = null;
        this.e.a((am) null);
        this.e.a(true);
    }

    protected int d(int i) {
        return this.e.c(i);
    }

    protected boolean d() {
        return a((String) null, false);
    }

    protected int e(int i) {
        return this.e.d(i);
    }

    protected boolean e() {
        return this.e.p();
    }

    protected boolean f() {
        return this.f.d();
    }

    protected boolean g() {
        return this.f.e(this.l);
    }

    protected Object getOption(int i) {
        if (1055 == i) {
            return Boolean.valueOf(this.b.w());
        }
        if (1056 == i) {
            return Boolean.valueOf(this.b.h());
        }
        if (1010 == i) {
            return Integer.valueOf(this.b.s());
        }
        if (1011 == i) {
            return Integer.valueOf(this.b.t());
        }
        if (1015 == i) {
            return Boolean.valueOf(this.b.L());
        }
        if (1016 == i) {
            return Float.valueOf(this.b.M());
        }
        return null;
    }

    protected String getVersion() {
        return com.unisound.common.af.a();
    }

    protected String h() {
        return JniAsrFix.getVersion();
    }

    protected int i() {
        return 0;
    }

    protected void j() {
    }

    protected void k() {
    }

    protected boolean l() {
        return false;
    }

    protected void m() throws Throwable {
        if (this.b.a() && this.s) {
            com.unisound.common.i.a(this.b.b());
        }
        B();
    }

    protected void n() {
    }

    protected void o() {
        if (this.b.j()) {
            this.r.a();
        }
        this.h.b();
    }

    protected void p() {
    }

    public void postRecordingStartStatus() {
    }

    protected void q() {
    }

    protected void r() {
        this.f.g();
        this.e.o();
    }

    protected void s() {
    }

    protected int setAudioSource(IAudioSource iAudioSource) {
        this.u = iAudioSource;
        if (iAudioSource != null) {
            return 0;
        }
        this.u = new com.unisound.common.e(this.b);
        return 0;
    }

    protected void setOption(int i, Object obj) {
        String str;
        if (1051 == i) {
            try {
                this.a.b = ((Boolean) obj).booleanValue();
                return;
            } catch (Exception unused) {
                str = "set asr_result_filter Error.";
            }
        } else {
            if (1053 != i) {
                try {
                    if (1054 == i) {
                        com.unisound.common.r.k = ((Boolean) obj).booleanValue();
                    } else if (1063 == i) {
                        com.unisound.common.r.l = ((Boolean) obj).booleanValue();
                    } else {
                        if (1055 == i) {
                            return;
                        }
                        if (1058 == i) {
                            try {
                                this.b.a((String) obj);
                                return;
                            } catch (Exception unused2) {
                                str = "set asr_save_recording_data Error.";
                            }
                        } else if (1059 == i) {
                            try {
                                this.a.c = ((Boolean) obj).booleanValue();
                                return;
                            } catch (Exception unused3) {
                                str = "set asr_result_json Error.";
                            }
                        } else if (1010 == i) {
                            try {
                                this.b.d(((Integer) obj).intValue());
                                return;
                            } catch (Exception unused4) {
                                str = "set asr_vad_timeout_frontsil Error.";
                            }
                        } else if (1011 == i) {
                            try {
                                this.b.e(((Integer) obj).intValue());
                                return;
                            } catch (Exception unused5) {
                                str = "set asr_vad_timeout_backsil Error.";
                            }
                        } else if (5 == i) {
                            try {
                                this.a.f = ((Boolean) obj).booleanValue();
                                if (this.a.f && this.a.h == null) {
                                    this.a.h = new OfflineNlu();
                                    return;
                                }
                                return;
                            } catch (Exception unused6) {
                                str = "set asr_fix_result_nlu Error.";
                            }
                        } else if (6 == i) {
                            try {
                                this.t = (String) obj;
                                this.a.g = (String) obj;
                                if (this.a.h == null || this.t.equals("")) {
                                    return;
                                }
                                this.a.h.b(this.t, "");
                                return;
                            } catch (Exception unused7) {
                                str = "set asr_fix_result_nlu_configpath Error.";
                            }
                        } else if (1062 == i) {
                            try {
                                this.e.a((Boolean) obj);
                                return;
                            } catch (Exception unused8) {
                                str = "set asr_print_engine_log Error.";
                            }
                        } else if (5000 == i) {
                            try {
                                this.b.b(((Boolean) obj).booleanValue());
                                return;
                            } catch (Exception unused9) {
                                str = "set setFarFeildEnabled Error. 5000 ";
                            }
                        } else if (5001 == i) {
                            try {
                                this.b.x.a(((Float) obj).floatValue());
                                return;
                            } catch (Exception unused10) {
                                str = "set min back energy Error. 5001 ";
                            }
                        } else if (5002 == i) {
                            try {
                                this.b.y.a(((Float) obj).floatValue());
                                return;
                            } catch (Exception unused11) {
                                str = "set min back energy higher TH Error. 5002 ";
                            }
                        } else if (5003 == i) {
                            try {
                                this.b.z.a(((Float) obj).floatValue());
                                return;
                            } catch (Exception unused12) {
                                str = "set pitch threshold Error. 5003 ";
                            }
                        } else if (5004 == i) {
                            try {
                                this.b.A.a(((Integer) obj).intValue());
                                return;
                            } catch (Exception unused13) {
                                str = "set pitch persist length for start usage Error. 5004 ";
                            }
                        } else if (5005 == i) {
                            try {
                                this.b.B.a(((Integer) obj).intValue());
                                return;
                            } catch (Exception unused14) {
                                str = "set pitch drop length for end usage Error. 5005 ";
                            }
                        } else if (5006 == i) {
                            try {
                                this.b.C.a(((Float) obj).floatValue());
                                return;
                            } catch (Exception unused15) {
                                str = "set high freq energy vs low freq energy Error. 5006 ";
                            }
                        } else if (5007 == i) {
                            try {
                                this.b.D.a(((Integer) obj).intValue());
                                return;
                            } catch (Exception unused16) {
                                str = "set min signal length for speech Error. 5007 ";
                            }
                        } else if (5008 == i) {
                            try {
                                this.b.E.a(((Integer) obj).intValue());
                                return;
                            } catch (Exception unused17) {
                                str = "set max silence length Error. 5008 ";
                            }
                        } else if (5009 == i) {
                            try {
                                this.b.F.a(((Float) obj).floatValue());
                                return;
                            } catch (Exception unused18) {
                                str = "set max single point max in spectral Error. 5009 ";
                            }
                        } else if (5010 == i) {
                            try {
                                this.b.G.a(((Float) obj).floatValue());
                                return;
                            } catch (Exception unused19) {
                                str = "set gloable noise to signal value threshold Error. 5010 ";
                            }
                        } else if (5011 == i) {
                            try {
                                this.b.H.a(((Float) obj).floatValue());
                                return;
                            } catch (Exception unused20) {
                                str = "set gloable noise to signal value threshold for vowel part Error. 5011 ";
                            }
                        } else if (5012 == i) {
                            try {
                                this.b.I.a(((Float) obj).floatValue());
                                return;
                            } catch (Exception unused21) {
                                str = "set voice freq domain prob Th Error. 5012 ";
                            }
                        } else if (5013 == i) {
                            try {
                                this.b.J.a(((Integer) obj).intValue());
                                return;
                            } catch (Exception unused22) {
                                str = "set use pitch or peak Error. 5013 ";
                            }
                        } else if (5014 == i) {
                            try {
                                this.b.K.a(((Integer) obj).intValue());
                                return;
                            } catch (Exception unused23) {
                                str = "set noise to y ratio, start point in freq domain Error. 5014 ";
                            }
                        } else if (5017 == i) {
                            try {
                                this.b.L.a(((Integer) obj).intValue());
                                return;
                            } catch (Exception unused24) {
                                str = "set PITCHLASTTH Error. 5017 ";
                            }
                        } else if (5021 == i) {
                            try {
                                this.b.g((String) obj);
                                return;
                            } catch (Exception unused25) {
                                str = "set activate info Error.";
                            }
                        } else if (1016 == i) {
                            try {
                                this.b.b(((Float) obj).floatValue());
                                return;
                            } catch (Exception unused26) {
                                str = "set vad musicth info Error!";
                            }
                        } else {
                            if (1015 != i) {
                                return;
                            }
                            try {
                                this.b.k(((Boolean) obj).booleanValue());
                                return;
                            } catch (Exception unused27) {
                                str = "set vad detectMusic Error!";
                            }
                        }
                    }
                    return;
                } catch (Exception unused28) {
                    com.unisound.common.r.e("set asr_print_log Error.");
                    return;
                }
            }
            try {
                this.q = ((Boolean) obj).booleanValue();
                return;
            } catch (Exception unused29) {
                str = "set asr_recording_enabled Error.";
            }
        }
        com.unisound.common.r.e(str);
    }

    protected void start() {
        b(this.f.e);
    }

    protected void stop() {
        this.c = null;
        this.e.b();
        if (this.q) {
            return;
        }
        this.e.f();
    }

    protected void t() {
        com.unisound.common.r.c("FixRecognizerInterFace : createJniAsrFix");
        this.f.a(this.l, y.f);
        this.e.r();
        this.e.a(new q(this));
    }

    protected int u() {
        return this.e.s();
    }

    protected List<Integer> v() {
        return this.e.t();
    }

    protected int w() {
        return this.e.u();
    }

    protected String x() {
        return this.e.v();
    }

    protected String y() {
        return this.e.w();
    }

    protected int z() {
        return this.e.x();
    }
}
