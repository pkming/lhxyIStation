package com.unisound.sdk;

import android.content.Context;
import android.os.HandlerThread;
import android.text.TextUtils;
import cn.yunzhisheng.tts.offline.lib.YzsTts;
import com.unisound.client.ErrorCode;
import com.unisound.client.IAudioSource;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizerListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class au {
    private static Object q = new Object();
    private YzsTts b;
    private bi c;
    private bj d;
    private bl e;
    private SpeechSynthesizerListener f;
    private Context g;
    private IAudioSource k;
    private HandlerThread m;
    private com.unisound.common.u n;
    private int h = 0;
    private bk i = bk.a();
    private String j = "";
    private Integer l = 2;
    private bn o = new av(this);
    private bm p = new aw(this);
    bt a = new bt(new ax(this));

    public au(Context context, String str, String str2) {
        this.g = context;
        this.i.b(str);
        this.i.c(str2);
        com.unisound.common.k.a(context);
        this.n = new ay(this, context.getMainLooper());
    }

    private int a() {
        bi biVar = this.c;
        if (biVar == null) {
            return -1;
        }
        biVar.h();
        return 0;
    }

    private int a(String str, az azVar) {
        String str2;
        if (this.n == null) {
            this.f.onError(2301, ErrorCode.toJsonMessage(ErrorCode.GENERAL_INIT_ERROR));
            return -1;
        }
        if (TextUtils.isEmpty(str)) {
            com.unisound.common.r.e("SpeechSynthesizerInterface beginTts: text is unusable");
            sendMsg(this.n, 202, ErrorCode.toJsonMessage(ErrorCode.TTS_ERROR_TEXT_UNUSEABLE));
            return -1;
        }
        f();
        a();
        d();
        g();
        b();
        e();
        bi biVar = this.c;
        if (biVar != null) {
            biVar.b(10000);
        }
        bl blVar = this.e;
        if (blVar != null) {
            blVar.c(10000);
        }
        if (this.l.intValue() == 1) {
            this.c = null;
            bi biVar2 = new bi(str, this.i);
            this.c = biVar2;
            biVar2.setName("TTSOfflineSynthesizerThread");
            this.c.a(this.o);
        } else {
            this.i.a = com.unisound.common.k.b();
            this.d = null;
            bj bjVar = new bj(str, this.i);
            this.d = bjVar;
            bjVar.setName("TTSOnlineSynthesizerThread");
            this.d.a(this.o);
        }
        this.e = null;
        bl blVar2 = new bl(this.i);
        this.e = blVar2;
        blVar2.setName("TTSPlayThread");
        this.e.a(this.p);
        this.e.a(this.k);
        if (azVar == az.onlySyn) {
            this.e.a((Boolean) false);
            com.unisound.common.r.b("SpeechSynthesizerInterface beginTts: onlySynthesize executed");
        }
        this.e.start();
        com.unisound.common.r.b("SpeechSynthesizerInterface beginTts: mTTSPlayThread.start()");
        if (this.l.intValue() == 1) {
            this.c.start();
            str2 = "SpeechSynthesizerInterface beginTts: mOfflineSynthesizeThread.start()";
        } else {
            this.d.start();
            str2 = "SpeechSynthesizerInterface beginTts: mOnlineSynthesizerThread.start(text)";
        }
        com.unisound.common.r.b(str2);
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object a(Object... objArr) {
        if (this.b.a((String) objArr[0], (String) objArr[1], (String) objArr[2])) {
            sendEmptyMsg(this.n, 101);
            return true;
        }
        sendMsg(this.n, 201, ErrorCode.toJsonMessage(ErrorCode.TTS_ERROR_LOAD_MODEL));
        return false;
    }

    private int b() {
        bi biVar = this.c;
        if (biVar == null) {
            return -1;
        }
        biVar.b();
        return 0;
    }

    private int c() {
        bi biVar = this.c;
        if (biVar == null) {
            return -1;
        }
        biVar.g();
        return 0;
    }

    private int d() {
        bj bjVar = this.d;
        if (bjVar == null) {
            return -1;
        }
        bjVar.g();
        return 0;
    }

    private int e() {
        bj bjVar = this.d;
        if (bjVar == null) {
            return -1;
        }
        bjVar.b();
        return 0;
    }

    private int f() {
        bl blVar = this.e;
        if (blVar == null) {
            return -1;
        }
        blVar.j();
        return 0;
    }

    private int g() {
        bl blVar = this.e;
        if (blVar == null) {
            return -1;
        }
        blVar.b();
        return 0;
    }

    private void h() {
        if (this.n.hasMessages(107)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 107");
            this.n.removeMessages(107);
        }
        if (this.n.hasMessages(103)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 103");
            this.n.removeMessages(103);
        }
        if (this.n.hasMessages(102)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 102");
            this.n.removeMessages(102);
        }
        if (this.n.hasMessages(104)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 104");
            this.n.removeMessages(104);
        }
        if (this.n.hasMessages(105)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 105");
            this.n.removeMessages(105);
        }
        if (this.n.hasMessages(106)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 106");
            this.n.removeMessages(106);
        }
        if (this.n.hasMessages(101)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 101");
            this.n.removeMessages(101);
        }
        if (this.n.hasMessages(108)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 108");
            this.n.removeMessages(108);
        }
        if (this.n.hasMessages(109)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 109");
            this.n.removeMessages(109);
        }
        if (this.n.hasMessages(111)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 111");
            this.n.removeMessages(111);
        }
        if (this.n.hasMessages(112)) {
            com.unisound.common.r.b("SpeechSynthesizerInterface removeMessage : remvoeMessage = 112");
            this.n.removeMessages(112);
        }
    }

    private void i() {
        com.unisound.common.r.b("SpeechSynthesizerInterface switchSpeeker begin");
        YzsTts yzsTts = this.b;
        if (yzsTts == null) {
            sendMsg(this.n, 225, ErrorCode.toJsonMessage(ErrorCode.TTS_ERROR_OFFLINE_ENGINE_NOT_INIT));
            return;
        }
        int iA = yzsTts.a(this.i.g());
        if (iA != 114) {
            switch (iA) {
                case ErrorCode.TTS_ERROR_OFFLINE_CHANGE_SPEAKER_FAIL /* -91105 */:
                case ErrorCode.TTS_ERROR_OFFLINE_ENGINE_IS_PROCESSING /* -91104 */:
                case ErrorCode.TTS_ERROR_OFFLINE_ENGINE_NOT_INIT /* -91103 */:
                    sendMsg(this.n, 225, ErrorCode.toJsonMessage(iA));
                    break;
            }
        } else {
            sendEmptyMsg(this.n, iA);
        }
        com.unisound.common.r.b("SpeechSynthesizerInterface switchSpeeker end");
    }

    private void j() {
        List<Integer> listY = this.i.y();
        if (listY.size() > 0) {
            for (Integer num : listY) {
                this.b.d(num.intValue());
                com.unisound.common.r.c("setTtsField...." + num);
            }
            listY.clear();
        }
    }

    public static void sendEmptyMsg(com.unisound.common.u uVar, int i) {
        synchronized (q) {
            uVar.sendEmptyMessage(i);
        }
    }

    public static void sendMsg(com.unisound.common.u uVar, int i, String str) {
        synchronized (q) {
            uVar.sendMessage(i, str);
        }
    }

    protected int a(String str) {
        return 0;
    }

    protected int a(String str, String str2) {
        return 0;
    }

    protected int cancel() {
        int iF;
        int iD;
        int iA;
        com.unisound.common.r.b("SpeechSynthesizerInterface cancel begin");
        synchronized (q) {
            iF = f();
            if (this.l.intValue() == 1) {
                iA = a();
                iD = -1;
            } else {
                iD = d();
                iA = -1;
            }
            h();
        }
        stop();
        if (iD == iA || iF == -1) {
            return -1;
        }
        com.unisound.common.r.b("SpeechSynthesizerInterface cancel end");
        return 0;
    }

    protected Object getOption(int i) {
        if (i == 1036) {
            return com.unisound.common.k.b(this.g);
        }
        if (i == 2020) {
            return this.i.C();
        }
        if (i == 2033) {
            YzsTts yzsTts = this.b;
            if (yzsTts != null) {
                return yzsTts.f();
            }
            sendMsg(this.n, 225, ErrorCode.toJsonMessage(ErrorCode.TTS_ERROR_GET_ENGINE_INFO));
            return "";
        }
        switch (i) {
            case 2001:
                return Integer.valueOf(this.i.t());
            case 2002:
                return Integer.valueOf(this.i.u());
            case 2003:
                return Integer.valueOf(this.i.v());
            case 2004:
                return Integer.valueOf(this.i.x());
            case 2005:
                return this.i.s();
            default:
                switch (i) {
                    case 2011:
                        return this.i.f().a();
                    case 2012:
                        return Integer.valueOf(this.i.w());
                    case 2013:
                        return Integer.valueOf(this.i.z());
                    case 2014:
                        return this.i.q();
                    default:
                        return null;
                }
        }
    }

    public bk getParams() {
        return this.i;
    }

    public String getSessionId() {
        return this.j;
    }

    protected int getStatus() {
        return this.h;
    }

    protected String getVersion() {
        return com.unisound.common.af.a;
    }

    protected int init(String str) {
        String str2;
        if (str != null && !str.equals("")) {
            Map<Integer, Object> mapA = com.unisound.common.o.a(str, this.i.D());
            Iterator<Integer> it = mapA.keySet().iterator();
            while (it.hasNext()) {
                int iIntValue = it.next().intValue();
                if (mapA.get(Integer.valueOf(iIntValue)) != null) {
                    setOption(iIntValue, mapA.get(Integer.valueOf(iIntValue)));
                }
            }
            com.unisound.common.r.b("SpeechSynthesizerInterface init: jsonString init param executed");
        }
        if (this.i.k()) {
            HandlerThread handlerThread = new HandlerThread("ht_outer");
            this.m = handlerThread;
            handlerThread.start();
            this.n = new ay(this, this.m.getLooper());
        }
        if (this.l.intValue() == 1) {
            com.unisound.common.r.b("SpeechSynthesizerInterface init: TTS_SERVICE_MODE_LOCAL");
            this.b = YzsTts.b();
            com.unisound.common.r.b("getDicModelPath= " + this.i.h() + " getSpeakerModelPath= " + this.i.g() + " getAnnotationFilePath = " + this.i.i());
            if (this.a.c() || this.b.d()) {
                if (this.b.d()) {
                    sendEmptyMsg(this.n, 101);
                    str2 = "SpeechSynthesizerInterface init: mTts.isInit()";
                }
                return 0;
            }
            this.a.a(this.i.h(), this.i.g(), this.i.i());
            this.a.a();
            str2 = "SpeechSynthesizerInterface init: asyncTask.start()";
        } else {
            str2 = "SpeechSynthesizerInterface init: TTS_SERVICE_MODE_NET";
        }
        com.unisound.common.r.b(str2);
        return 0;
    }

    public boolean isPlaying() {
        bl blVar = this.e;
        if (blVar != null) {
            return blVar.k();
        }
        return false;
    }

    protected void pause() {
        com.unisound.common.r.b("SpeechSynthesizerInterface pause begin");
        bl blVar = this.e;
        if (blVar != null) {
            blVar.d();
        }
        com.unisound.common.r.b("SpeechSynthesizerInterface pause end");
    }

    protected void playSynWav() {
        bl blVar = this.e;
        if (blVar != null) {
            blVar.a((Boolean) true);
        }
    }

    protected int playText(String str) {
        return a(str, az.synAndPlay);
    }

    protected int release(int i, String str) {
        com.unisound.common.r.b("SpeechSynthesizerInterface release begin");
        int iC = 0;
        if (i != 2401) {
            com.unisound.common.r.e("SpeechSynthesizerInterface release : release type error");
        } else {
            stop();
            if (this.c != null) {
                iC = c();
            } else {
                YzsTts yzsTts = this.b;
                if (yzsTts != null) {
                    yzsTts.c();
                    this.b = null;
                    sendEmptyMsg(this.n, 112);
                }
            }
            bi biVar = this.c;
            if (biVar != null) {
                biVar.b(10000);
            }
            bl blVar = this.e;
            if (blVar != null) {
                blVar.c(10000);
            }
        }
        com.unisound.common.r.b("SpeechSynthesizerInterface release end");
        return iC;
    }

    protected void resume() {
        com.unisound.common.r.b("SpeechSynthesizerInterface resume begin");
        bl blVar = this.e;
        if (blVar != null) {
            blVar.f();
        }
        com.unisound.common.r.b("SpeechSynthesizerInterface resume end");
    }

    protected int setAudioSource(IAudioSource iAudioSource) {
        this.k = iAudioSource;
        return 0;
    }

    protected void setOption(int i, Object obj) {
        if (i == 2034) {
            this.i.c(obj);
        }
        switch (i) {
            case 2001:
                this.i.m(obj);
                break;
            case 2002:
                this.i.n(obj);
                break;
            case 2003:
                this.i.o(obj);
                break;
            case 2004:
                this.i.r(obj);
                break;
            case 2005:
                this.i.l(obj);
                break;
            default:
                switch (i) {
                    case 2011:
                        this.i.t(obj);
                        break;
                    case 2012:
                        this.i.p(obj);
                        break;
                    case 2013:
                        this.i.u(obj);
                        break;
                    case 2014:
                        this.i.j(obj);
                        com.unisound.common.r.k = this.i.q().booleanValue();
                        break;
                    case 2015:
                        this.i.k(obj);
                        break;
                    case 2016:
                        this.i.i(obj);
                        break;
                    default:
                        switch (i) {
                            case 2020:
                                this.l = (Integer) obj;
                                break;
                            case 2021:
                                this.i.f(obj);
                                break;
                            case 2022:
                                this.i.g(obj);
                                break;
                            case 2023:
                                this.i.h(obj);
                                break;
                            case 2024:
                                this.i.e(obj);
                                break;
                            case 2025:
                                this.i.d(obj);
                                break;
                            default:
                                switch (i) {
                                    case 2030:
                                        this.i.b(obj);
                                        break;
                                    case SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH /* 2031 */:
                                        this.i.a(obj);
                                        break;
                                    case SpeechConstants.TTS_KEY_SWITCH_BACKEND_MODEL_PATH /* 2032 */:
                                        cancel();
                                        com.unisound.common.r.b("SpeechSynthesizerInterface setOption switch backend_model: " + obj);
                                        this.i.a(obj);
                                        i();
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
        }
    }

    public void setServer(String str, short s) {
        this.i.a(new String(str), s);
    }

    protected void setTTSListener(SpeechSynthesizerListener speechSynthesizerListener) {
        this.f = speechSynthesizerListener;
    }

    protected void stop() {
        com.unisound.common.r.b("SpeechSynthesizerInterface stop begin");
        g();
        if (this.l.intValue() == 1) {
            b();
        } else {
            e();
        }
        com.unisound.common.r.b("SpeechSynthesizerInterface stop end");
    }

    protected void synthesizeText(String str) {
        a(str, az.onlySyn);
    }
}
