package com.unisound.sdk;

import android.content.Context;
import android.media.AudioManager;
import cn.yunzhisheng.asr.JniUscClient;
import com.unisound.client.ErrorCode;
import com.unisound.client.IAudioSource;
import com.unisound.client.SpeechConstants;
import com.unisound.client.VoicePrintRecognizerListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class ci {
    public static final int OPTION_VPR_TYPE_REGISTERED = 1;
    public static final int OPTION_VPR_TYPE_VERIFY = 2;
    private Context F;
    private VoicePrintRecognizerListener h;
    private com.unisound.common.aj j;
    private aj k;
    private AudioManager n;
    private String p;
    private String q;
    private static com.unisound.common.ad i = new com.unisound.common.ad();
    protected static boolean g = false;
    protected an a = new an();
    protected ErrorCode b = new ErrorCode();
    protected aq c = new aq();
    protected cg d = new cg();
    protected List<byte[]> e = new ArrayList();
    private com.unisound.common.al l = com.unisound.common.al.idle;
    protected v f = null;
    private a m = new a();
    private String o = "117.121.49.3:10000";
    private String r = "";
    private String s = "";
    private boolean t = true;
    private boolean u = false;
    private boolean v = false;
    private boolean w = true;
    private boolean x = false;
    private boolean y = false;
    private boolean z = false;
    private boolean A = false;
    private boolean B = false;
    private int C = 0;
    private IAudioSource D = null;
    private cm E = new cm();
    private z G = new ck(this);
    private com.unisound.common.d H = new cl(this);

    protected ci(Context context, String str, String str2) {
        this.F = context;
        this.p = str;
        this.q = str2;
        this.a.p(str2);
        this.a.m(ch.f);
        this.a.e(false);
        this.a.q(12);
        aj ajVar = new aj(context, this.a, context.getMainLooper());
        this.k = ajVar;
        ajVar.a(this.G);
        if (str != null) {
            this.a.h(str);
        }
        this.m.a(this.H);
        i.a(this.a);
        this.n = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String a(String str) {
        String[] strArrSplit;
        if (str.contains("}{")) {
            strArrSplit = str.split("\\}\\{");
            for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                if (i2 == 0) {
                    strArrSplit[i2] = strArrSplit[i2] + "}";
                } else if (i2 == strArrSplit.length - 1) {
                    strArrSplit[i2] = "{" + strArrSplit[i2];
                } else {
                    strArrSplit[i2] = "{" + strArrSplit[i2] + "}";
                }
            }
        } else {
            strArrSplit = new String[]{str};
        }
        String str2 = strArrSplit[strArrSplit.length - 1];
        com.unisound.common.r.c("VoicePrintRecognizerInterface", "rmUselessResult : results = " + strArrSplit.toString() + " , length = " + strArrSplit.length + " , result = " + str2);
        return str2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str, String str2) {
        JniUscClient jniUscClient = new JniUscClient();
        com.unisound.common.a aVarAd = this.a.ad();
        long jA = jniUscClient.a(aVarAd.a(), aVarAd.c());
        jniUscClient.a(9, str);
        jniUscClient.a(204, str2);
        com.unisound.common.r.c("SpeechUnderstanderInterface", "server :" + aVarAd.a() + " port: " + aVarAd.c());
        com.unisound.common.r.c("SpeechUnderstanderInterface", "juc.create() returns " + jA);
        int iG = jniUscClient.g();
        com.unisound.common.r.c("SpeechUnderstanderInterface", "initUscClient : loginstate = " + iG);
        if (iG == 0) {
            this.r = jniUscClient.c(206);
        } else {
            VoicePrintRecognizerListener voicePrintRecognizerListener = this.h;
            if (voicePrintRecognizerListener != null) {
                voicePrintRecognizerListener.onError(SpeechConstants.VPR_ERROR, ErrorCode.toMessage(iG));
            }
        }
        jniUscClient.e();
        com.unisound.common.r.e("SpeechUnderstanderInterface : initUscClient -> mLoginToken = " + this.r);
    }

    private void f() {
        this.s = "";
        this.x = false;
        this.y = false;
    }

    private void g() {
        byte[] bArrA;
        if (this.v && this.w && !this.A) {
            this.A = true;
            Iterator<byte[]> it = this.e.iterator();
            int length = 0;
            while (it.hasNext()) {
                length += it.next().length;
            }
            if (length <= 0 || (bArrA = com.unisound.common.as.a(length, 1, 16000)) == null) {
                return;
            }
            this.e.add(0, bArrA);
        }
    }

    protected void a() {
        this.l = com.unisound.common.al.recording;
        this.a.m(this.u && !this.t);
        this.f = null;
        if (this.z) {
            this.n.setBluetoothScoOn(true);
            this.n.startBluetoothSco();
        }
        x xVar = new x(this.F, this.a, this.k);
        xVar.setName("usc_vad_thread");
        xVar.setPriority(10);
        xVar.start();
        if (this.t) {
            as.n();
            if (this.D == null) {
                this.D = new com.unisound.common.e(this.a);
            }
            this.k.a((w) new as(this.a, this.k, this.D), true, this.r, xVar);
        } else {
            v vVar = new v(this.a, this.k);
            this.f = vVar;
            this.k.a((w) vVar, true, this.r, xVar);
        }
        this.A = false;
        if (this.v) {
            this.e = new ArrayList();
        }
        boolean zB = com.unisound.common.i.b(this.s);
        this.x = zB;
        if (zB) {
            this.y = com.unisound.common.i.c(this.s);
        }
    }

    protected void a(int i2, int i3, Object obj) {
        com.unisound.common.aj ajVar = this.j;
        if (ajVar != null) {
            ajVar.a(i2, i3, obj);
        }
    }

    protected void a(boolean z, byte[] bArr, int i2, int i3) throws Throwable {
        if ((this.v && z) || g) {
            this.e.add(bArr);
        }
        if (this.x) {
            com.unisound.common.i.a(bArr, this.s);
        }
    }

    protected boolean a(Context context) {
        return com.unisound.common.h.a(context);
    }

    protected void b() {
        this.l = com.unisound.common.al.idle;
        this.k.c(true);
    }

    protected boolean b(Context context) {
        return com.unisound.common.h.c(context);
    }

    protected void c() {
        this.l = com.unisound.common.al.recognizing;
        this.f = null;
        this.k.d();
        if (this.n.isBluetoothScoOn()) {
            this.n.setBluetoothScoOn(false);
            this.n.stopBluetoothSco();
        }
    }

    protected void cancel() {
        this.m.f();
        b();
    }

    protected void d() {
        this.m.g();
        a();
    }

    protected void e() throws Throwable {
        g();
        if (this.x && this.y) {
            com.unisound.common.i.a(this.s);
        }
        f();
    }

    protected Object getOption(int i2) {
        if (i2 == 1036) {
            return com.unisound.common.k.b(this.F);
        }
        if (i2 == 1082) {
            return Integer.valueOf(this.a.U());
        }
        if (i2 == 4013) {
            return Integer.valueOf(this.k.b());
        }
        if (i2 == 4017) {
            return this.a.Y().b();
        }
        if (i2 == 4020) {
            return this.a.ak();
        }
        if (i2 != 4103) {
            return null;
        }
        return Integer.valueOf(this.C);
    }

    public boolean getRequestAudio(String str, String str2) {
        return com.unisound.common.ar.a("http://" + this.o + "/s/get.do?appKey=" + this.p + "&&rid=" + str, str2);
    }

    protected int init(String str) {
        this.B = true;
        if (str == null || str == "") {
            com.unisound.common.r.c("SpeechUnderStanderInterface : init json is an empty string!");
        } else {
            Map<Integer, Object> mapA = com.unisound.common.o.a(str, this.E.a());
            Iterator<Integer> it = mapA.keySet().iterator();
            while (it.hasNext()) {
                int iIntValue = it.next().intValue();
                if (mapA.get(Integer.valueOf(iIntValue)) != null) {
                    setOption(iIntValue, mapA.get(Integer.valueOf(iIntValue)));
                }
            }
        }
        new cj(this).start();
        return 0;
    }

    protected int setAudioSource(IAudioSource iAudioSource) {
        this.D = iAudioSource;
        if (iAudioSource != null) {
            return 0;
        }
        this.D = new com.unisound.common.e(this.a);
        return 0;
    }

    protected void setListener(VoicePrintRecognizerListener voicePrintRecognizerListener) {
        this.h = voicePrintRecognizerListener;
    }

    protected void setOption(int i2, Object obj) {
        if (i2 == 1082) {
            this.a.q(((Integer) obj).intValue());
        }
        if (i2 == 4001) {
            boolean zBooleanValue = ((Boolean) obj).booleanValue();
            this.t = zBooleanValue;
            if (zBooleanValue) {
                return;
            }
            this.a.e(false);
            return;
        }
        if (i2 == 4021) {
            this.a.Y().a(((Boolean) obj).booleanValue());
            return;
        }
        if (i2 == 4003) {
            this.a.m((String) obj);
            return;
        }
        if (i2 == 4004) {
            this.u = ((Boolean) obj).booleanValue();
            return;
        }
        switch (i2) {
            case SpeechConstants.VPR_SCENE_ID /* 4006 */:
                this.a.a(i.a((String) obj));
                break;
            case SpeechConstants.VPR_LOG_LISTNER /* 4007 */:
                this.j = (com.unisound.common.aj) obj;
                break;
            case SpeechConstants.VPR_SAVE_RECORDING_DATA /* 4008 */:
                this.s = (String) obj;
                break;
            case SpeechConstants.VPR_FRONT_VAD_ENABLED /* 4009 */:
                this.a.c(((Boolean) obj).booleanValue());
                break;
            case SpeechConstants.VPR_SAMPLE_RATE /* 4010 */:
                this.a.r(((Integer) obj).intValue());
                break;
            case SpeechConstants.VPR_BLUETOOTH_ENABLED /* 4011 */:
                this.z = ((Boolean) obj).booleanValue();
                break;
            case SpeechConstants.VPR_VAD_TIMEOUT /* 4012 */:
                int[] iArr = (int[]) obj;
                this.a.a(iArr[0], iArr[1]);
                break;
            case SpeechConstants.VPR_STOP_TIMEOUT /* 4013 */:
                this.k.c(((Integer) obj).intValue());
                break;
            case SpeechConstants.VPR_FARFILED_ENABLED /* 4014 */:
                this.a.o(((Boolean) obj).booleanValue());
                break;
            case SpeechConstants.VPR_REQUEST_AUDIO_SERVER /* 4015 */:
                this.o = (String) obj;
                break;
            case SpeechConstants.VPR_TYPE /* 4016 */:
                this.a.Y().a(((Integer) obj).intValue());
                break;
            case SpeechConstants.VPR_USERNAME /* 4017 */:
                this.a.Y().a((String) obj);
                break;
        }
    }

    protected void start(String str, int i2) {
        if (!this.B) {
            com.unisound.common.r.e("init error " + ErrorCode.toJsonMessage(ErrorCode.GENERAL_INIT_ERROR));
            this.h.onError(SpeechConstants.VPR_ERROR, ErrorCode.toJsonMessage(ErrorCode.GENERAL_INIT_ERROR));
        } else {
            this.a.Y().a(str);
            this.a.Y().a(i2);
            d();
        }
    }

    protected void stop() {
        c();
    }
}
