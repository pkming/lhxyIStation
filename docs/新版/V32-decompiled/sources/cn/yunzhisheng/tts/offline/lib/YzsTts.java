package cn.yunzhisheng.tts.offline.lib;

import com.unisound.client.ErrorCode;
import com.unisound.common.r;

/* JADX INFO: loaded from: classes2.dex */
public class YzsTts {
    private static YzsTts a;
    private static volatile boolean d;
    private long b = 0;
    private long c = 0;
    private Object e = new Object();

    static {
        System.loadLibrary("yzstts");
        a = null;
        d = false;
    }

    public static YzsTts b() {
        if (a == null) {
            a = new YzsTts();
        }
        return a;
    }

    private native void cancel(long j);

    private native int changeSpeaker(long j, String str);

    private native long create(long j);

    private native long createbase(String str, String str2, String str3);

    private native String getCheckInfo(Object obj);

    private native String getOption(long j, int i);

    private native void release(long j);

    private native void releasebase(long j);

    private native int setOption(long j, int i, String str);

    public int a(long j, byte[] bArr) {
        int iReceiveSamples;
        if (j == 0) {
            return 0;
        }
        synchronized (this.e) {
            iReceiveSamples = receiveSamples(j, bArr);
        }
        return iReceiveSamples;
    }

    public int a(String str) {
        if (!d()) {
            return ErrorCode.TTS_ERROR_OFFLINE_ENGINE_NOT_INIT;
        }
        if (d) {
            return ErrorCode.TTS_ERROR_OFFLINE_ENGINE_IS_PROCESSING;
        }
        synchronized (this.e) {
            if (changeSpeaker(this.c, str) == -1) {
                return ErrorCode.TTS_ERROR_OFFLINE_CHANGE_SPEAKER_FAIL;
            }
            return 114;
        }
    }

    public long a() {
        return this.c;
    }

    public void a(float f) {
        if (!d() || setOption(this.c, 0, String.format("%1$.1f", Float.valueOf(f))) == 0) {
            return;
        }
        r.e("YzsTts setLog : error");
    }

    public void a(int i) {
        if (!d() || setOption(this.c, 6, String.valueOf(i)) == 0) {
            return;
        }
        r.e("YzsTts setFrontSilence : error");
    }

    public void a(Boolean bool) {
        d = bool.booleanValue();
    }

    public boolean a(String str, String str2, String str3) {
        c();
        long jCreatebase = createbase(str, str2, str3);
        this.b = jCreatebase;
        if (jCreatebase == 0) {
            return false;
        }
        synchronized (this.e) {
            long jCreate = create(this.b);
            this.c = jCreate;
            return jCreate != 0;
        }
    }

    public void b(float f) {
        if (!d() || setOption(this.c, 1, String.format("%1$.1f", Float.valueOf(f))) == 0) {
            return;
        }
        r.e("YzsTts setVoiceSpeed : error");
    }

    public void b(int i) {
        if (!d() || setOption(this.c, 7, String.valueOf(i)) == 0) {
            return;
        }
        r.e("YzsTts setBackSilence : error");
    }

    public void b(Boolean bool) {
        if (!d() || setOption(this.c, 5, String.valueOf(bool)) == 0) {
            return;
        }
        r.e("YzsTts setIsReadEnglishInPinyin : error");
    }

    public void c() {
        if (d()) {
            synchronized (this.e) {
                release(this.c);
                this.c = 0L;
            }
            releasebase(this.b);
            this.b = 0L;
            d = false;
        }
    }

    public void c(float f) {
        if (!d() || setOption(this.c, 3, String.format("%1$.1f", Float.valueOf(f))) == 0) {
            return;
        }
        r.e("YzsTts setVoiceVolume : error");
    }

    public void c(int i) {
        if (d()) {
            setOption(this.c, 5, String.valueOf(i));
        }
    }

    public void d(float f) {
        if (!d() || setOption(this.c, 2, String.format("%1$.1f", Float.valueOf(f))) == 0) {
            return;
        }
        r.e("YzsTts setVoicePitch : error");
    }

    public boolean d() {
        return this.c != 0;
    }

    public boolean d(int i) {
        return d() && setOption(this.c, 6, String.valueOf(i)) == 0;
    }

    public void e() {
        if (d() && d) {
            synchronized (this.e) {
                cancel(this.c);
            }
            d = false;
        }
    }

    public String f() {
        String checkInfo = getCheckInfo(new Object());
        return checkInfo == null ? "" : checkInfo;
    }

    public native int receiveSamples(long j, byte[] bArr);

    public native int setText(long j, String str);
}
