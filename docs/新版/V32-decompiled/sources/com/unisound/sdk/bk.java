package com.unisound.sdk;

import android.text.TextUtils;
import com.unisound.client.SpeechConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class bk {
    public static final boolean d = false;
    public static final int e = 0;
    public static final int f = 300;
    public static final int g = 50;
    public static final int h = 50;
    public static final int i = 50;
    public static final String j = "xiaoli";
    public static final int k = 16000;
    public static final boolean l = false;
    public static final int m = 100;
    public static final int n = 100;
    public static final int o = 2;
    public static final boolean p = false;
    public static final boolean q = false;
    private static bk s;
    private String D;
    private String E;
    private String u;
    private com.unisound.common.a r = new com.unisound.common.a("ttsv3.hivoice.cn", 80, "117.121.49.41", 80);
    public int a = 0;
    public String b = null;
    public b c = new b();
    private Boolean t = false;
    private int v = 300;
    private int w = 50;
    private int x = 50;
    private int y = 50;
    private String z = j;
    private int A = 16000;
    private List<Integer> B = new ArrayList();
    private int C = 3;
    private Integer F = 2;
    private int G = 0;
    private Boolean H = false;
    private int I = 100;
    private int J = 100;
    private boolean K = false;
    private boolean L = false;
    private String M = "";
    private String N = "";
    private String O = "";

    private bk() {
    }

    private int a(Integer num) {
        int iIntValue = Integer.valueOf(num.intValue()).intValue();
        if (iIntValue < 0) {
            com.unisound.common.r.d("TTSParams checkParams: value < 0");
            return 1;
        }
        if (iIntValue <= 100) {
            return iIntValue;
        }
        com.unisound.common.r.d("TTSParams checkParams: value > 100");
        return 100;
    }

    public static bk a() {
        if (s == null) {
            s = new bk();
        }
        return s;
    }

    private Integer b(int i2) {
        if (i2 > 1000) {
            com.unisound.common.r.d("TTSParams checkSilenceTime: silence > 1000 invoked silence = 1000");
            i2 = 1000;
        }
        if (i2 < 0) {
            com.unisound.common.r.d("TTSParams checkSilenceTime: silence < 0 invoked silence = 0");
            i2 = 0;
        }
        return Integer.valueOf(i2);
    }

    private boolean d(String str) {
        return this.r.c(str);
    }

    private boolean e(String str) {
        if (!TextUtils.isEmpty(str) && TextUtils.isDigitsOnly(str)) {
            return true;
        }
        com.unisound.common.r.e("TTSParams isNumberUseable: value is empty or unusable");
        return false;
    }

    private boolean f(String str) {
        if (!TextUtils.isEmpty(str)) {
            return true;
        }
        com.unisound.common.r.e("TTSParams isStringUseable: value is empty");
        return false;
    }

    private boolean g(String str) {
        String str2;
        String[] strArrSplit = str.split(":");
        if (strArrSplit.length != 2) {
            return false;
        }
        if (!h(strArrSplit[0])) {
            str2 = "TTSParams checkAddress: ip unusable";
        } else {
            if (i(strArrSplit[1])) {
                return true;
            }
            str2 = "TTSParams checkAddress: port unusable";
        }
        com.unisound.common.r.e(str2);
        return false;
    }

    private boolean h(String str) {
        return Pattern.compile("(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})").matcher(str).matches();
    }

    private boolean i(String str) {
        try {
            int iIntValue = Integer.valueOf(str).intValue();
            if (iIntValue > 0 && iIntValue < 65535) {
                return true;
            }
            com.unisound.common.r.e("TTSParams checkPort: port Illegal");
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            com.unisound.common.r.e("TTSParams checkPort: port changeTo integer exception");
            return false;
        }
    }

    private boolean x(Object obj) {
        String str;
        if (obj == null) {
            str = "TTSParams object2Boolean: obj is null";
        } else {
            if (obj instanceof Boolean) {
                return ((Boolean) obj).booleanValue();
            }
            str = "TTSParams object2Boolean: obj is not change to boolean";
        }
        com.unisound.common.r.e(str);
        return false;
    }

    public String A() {
        return this.D;
    }

    public String B() {
        return this.E;
    }

    public Integer C() {
        return this.F;
    }

    public Map<String, Integer> D() {
        HashMap map = new HashMap();
        map.put(SpeechConstants.TTS_KEY_VOICE_SPEED_JSONKEY, 2001);
        map.put(SpeechConstants.TTS_KEY_VOICE_PITCH_JSONKEY, 2002);
        map.put(SpeechConstants.TTS_KEY_VOICE_VOLUME_JSONKEY, 2003);
        map.put(SpeechConstants.TTS_KEY_SAMPLE_RATE_JSONKEY, 2004);
        map.put(SpeechConstants.TTS_KEY_VOICE_NAME_JSONKEY, 2005);
        map.put(SpeechConstants.TTS_KEY_SERVER_ADDR_JSONKEY, 2011);
        map.put(SpeechConstants.TTS_KEY_PLAY_START_BUFFER_TIME_JSONKEY, 2012);
        map.put(SpeechConstants.TTS_KEY_STREAM_TYPE_JSONKEY, 2013);
        map.put(SpeechConstants.TTS_KEY_IS_DEBUG_JSONKEY, 2014);
        map.put(SpeechConstants.TTS_SERVICE_MODE_JSONKEY, 2020);
        return map;
    }

    public Object a(int i2) {
        return null;
    }

    public void a(Object obj) {
        this.N = q(obj);
    }

    public void a(String str) {
        this.b = str;
    }

    public void a(String str, int i2) {
        this.r.a(str);
        this.r.a(i2);
        this.r.b(str);
        this.r.b(i2);
    }

    public boolean a(int i2, Object obj) {
        return false;
    }

    public b b() {
        return this.c;
    }

    public void b(Object obj) {
        this.M = q(obj);
    }

    public void b(String str) {
        this.D = str;
    }

    public String c() {
        return this.r.a();
    }

    public void c(Object obj) {
        this.O = q(obj);
    }

    public void c(String str) {
        this.E = str;
    }

    public int d() {
        return this.r.c();
    }

    public void d(Object obj) {
        this.L = x(obj);
    }

    public void e() {
        this.r.e();
    }

    public void e(Object obj) {
        this.K = x(obj);
    }

    public com.unisound.common.a f() {
        return this.r;
    }

    public void f(Object obj) {
        this.H = Boolean.valueOf(x(obj));
    }

    public String g() {
        return this.N;
    }

    public void g(Object obj) {
        this.I = b(v(obj).intValue()).intValue();
    }

    public String h() {
        return this.M;
    }

    public void h(Object obj) {
        this.J = b(v(obj).intValue()).intValue();
    }

    public String i() {
        return this.O;
    }

    public void i(Object obj) {
        this.G = v(obj).intValue();
    }

    public String j() {
        return this.b;
    }

    public void j(Object obj) {
        this.t = Boolean.valueOf(x(obj));
    }

    public void k(Object obj) {
        String strQ = q(obj);
        if (strQ == null) {
            com.unisound.common.r.e("TTSParams setDebugDir: mDebugDir is null");
        } else {
            this.u = strQ;
        }
    }

    public boolean k() {
        return this.L;
    }

    public void l(Object obj) {
        String str;
        String strQ = q(obj);
        if (strQ == null) {
            str = "TTSParams setVoiceName: voiceName is null";
        } else {
            if (f(strQ)) {
                this.z = strQ;
                return;
            }
            str = "TTSParams setVoiceName: voiceName unusable";
        }
        com.unisound.common.r.e(str);
    }

    public boolean l() {
        return this.K;
    }

    public Boolean m() {
        return this.H;
    }

    public void m(Object obj) {
        this.w = a(v(obj));
    }

    public int n() {
        return this.I;
    }

    public void n(Object obj) {
        this.x = a(v(obj));
    }

    public int o() {
        return this.J;
    }

    public void o(Object obj) {
        this.y = a(v(obj));
    }

    public int p() {
        return this.G;
    }

    public void p(Object obj) {
        Integer numV = v(obj);
        if (numV.intValue() > 15000) {
            numV = 15000;
            com.unisound.common.r.d("TTSParams setPlayStartBufferTime: playStartBufferTime > 15000 " + Integer.valueOf(numV.intValue()));
        }
        if (numV.intValue() <= 0) {
            numV = 1;
            com.unisound.common.r.d("TTSParams setPlayStartBufferTime: playStartBufferTime < 0 " + Integer.valueOf(numV.intValue()));
        }
        this.v = numV.intValue();
    }

    public Boolean q() {
        return this.t;
    }

    public String q(Object obj) {
        String str;
        if (obj == null) {
            str = "TTSParams object2String: obj is null";
        } else {
            if (obj instanceof String) {
                return (String) obj;
            }
            str = "TTSParams object2String: obj can not change to String";
        }
        com.unisound.common.r.e(str);
        return null;
    }

    public String r() {
        return this.u;
    }

    public void r(Object obj) {
        String str;
        String strQ = q(obj);
        if (strQ == null) {
            str = "TTSParams setSampleRate: mSampleRate is null";
        } else {
            if (e(strQ)) {
                int iIntValue = Integer.valueOf(strQ).intValue();
                if (iIntValue != 48000) {
                    return;
                }
                this.A = iIntValue;
                return;
            }
            str = "TTSParams setSampleRate: mSampleRate unusable";
        }
        com.unisound.common.r.e(str);
    }

    public String s() {
        return this.z;
    }

    public void s(Object obj) {
        String strQ = q(obj);
        if (strQ == null) {
            com.unisound.common.r.e("TTSParams addField: value is null");
            return;
        }
        try {
            this.B.add(Integer.valueOf(Integer.valueOf(strQ).intValue()));
        } catch (Exception e2) {
            e2.printStackTrace();
            com.unisound.common.r.e("TTSParams addField: value can not change to integer");
        }
    }

    public int t() {
        return this.w;
    }

    public boolean t(Object obj) {
        String strQ = q(obj);
        if (g(strQ)) {
            return d(strQ);
        }
        com.unisound.common.r.e("TTSParams setServerAddress: address unusable");
        return false;
    }

    public int u() {
        return this.x;
    }

    public void u(Object obj) {
        Integer numV = v(obj);
        if (numV.intValue() < 0 || numV.intValue() > 8) {
            com.unisound.common.r.e("TTSParams setStreamType: mStreamType unusable");
        } else {
            this.C = numV.intValue();
        }
    }

    public int v() {
        return this.y;
    }

    public Integer v(Object obj) {
        String str;
        if (obj == null) {
            str = "TTSParams object2Integer: obj is null";
        } else {
            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            str = "TTSParams object2Integer: obj can not change to integer";
        }
        com.unisound.common.r.e(str);
        return 1;
    }

    public int w() {
        return this.v;
    }

    public void w(Object obj) {
        Integer numV = v(obj);
        if (numV.intValue() == 1 || numV.intValue() == 2) {
            this.F = numV;
        } else {
            com.unisound.common.r.e("TTSParams setMode: mMode unusable");
        }
    }

    public int x() {
        return this.A;
    }

    public List<Integer> y() {
        return this.B;
    }

    public int z() {
        return this.C;
    }
}
