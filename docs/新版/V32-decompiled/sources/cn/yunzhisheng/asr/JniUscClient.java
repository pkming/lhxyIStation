package cn.yunzhisheng.asr;

/* JADX INFO: loaded from: classes2.dex */
public class JniUscClient {
    public static final int A = 8;
    public static final int B = 9;
    public static final int C = 10;
    public static final int D = 11;
    public static final int E = 12;
    public static final int F = 13;
    public static final int G = 14;
    public static final int H = 15;
    public static final int I = 16;
    public static final int J = 17;
    public static final int K = 18;
    public static final int L = 19;
    public static final int M = 31;
    public static final int N = 21;
    public static final int O = 22;
    public static final int P = 25;
    public static final int Q = 26;
    public static final int R = 0;
    public static final int S = 1;
    public static final int T = 20;
    public static final int U = 21;
    public static final int V = 22;
    public static final int W = 32;
    public static final int X = 33;
    public static final int Y = 34;
    public static final int Z = 35;
    public static final int a = 0;
    public static final int aa = 23;
    public static final int ab = 26;
    public static final int ac = 27;
    public static final int ad = 28;
    public static final int ae = 34;
    public static final int af = 201;
    public static final int ag = 206;
    public static final int ah = 204;
    public static final int ai = 0;
    public static final int aj = 1;
    public static final int ak = 0;
    public static final int al = 1;
    public static final int am = 2;
    public static final int an = 3;
    public static final int ao = 4;
    public static final int ap = 1015;
    public static final int b = 0;
    public static final int c = 0;
    public static final int d = 1;
    public static final int e = 2;
    public static final int f = 1002;
    public static final int g = 1015;
    public static final int h = 201;
    public static final int i = 1020;
    public static final int j = 1019;
    public static int k = 0;
    public static int l = 0;
    public static final String m = "opus";
    public static final String n = "opus-nb";
    public static final String o = "req_audio_url";
    public static final String p = "get_variable";
    public static final String q = "open";
    public static final String r = "close";
    public static final int s = 0;
    public static final int t = 1;
    public static final int u = 2;
    public static final int v = 3;
    public static final int w = 4;
    public static final int x = 5;
    public static final int y = 6;
    public static final int z = 7;
    private long aq = 0;

    public static String b(int i2) {
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? "NETWORK_TYPE_NONE" : "NETWORK_TYPE_MOBILE" : "NETWORK_TYPE_2G" : "NETWORK_TYPE_3G" : "NETWORK_TYPE_WIFI";
    }

    private native int cancel(long j2);

    private native long createNative(String str, int i2);

    private native void destroyNative(long j2);

    private native int getLastErrno(long j2);

    private native String getOptionValue(long j2, int i2);

    private native String getResult(long j2);

    private native int login(long j2);

    private native int recognize(long j2, byte[] bArr, int i2);

    private native int setOptionInt(long j2, int i2, int i3);

    private native int setOptionString(long j2, int i2, String str);

    private native int start(long j2);

    private native int stop(long j2);

    public int a() {
        long j2 = this.aq;
        if (j2 == 0) {
            return -1;
        }
        int iStart = start(j2);
        a(iStart);
        return iStart;
    }

    public int a(int i2, int i3) {
        long j2 = this.aq;
        if (j2 != 0) {
            return setOptionInt(j2, i2, i3);
        }
        return -1;
    }

    public int a(int i2, String str) {
        long j2 = this.aq;
        if (j2 != 0) {
            return setOptionString(j2, i2, str);
        }
        return -1;
    }

    public int a(boolean z2) {
        if (z2) {
            return a(35, p);
        }
        return 0;
    }

    public int a(byte[] bArr, int i2) {
        long j2 = this.aq;
        if (j2 == 0) {
            return -1;
        }
        int iRecognize = recognize(j2, bArr, i2);
        a(iRecognize);
        return iRecognize;
    }

    public long a(String str, int i2) {
        if (this.aq == 0) {
            this.aq = createNative(str, i2);
        }
        return this.aq;
    }

    public void a(int i2) {
        k = i2;
        l = i2 < 0 ? f() : 0;
    }

    public int b() {
        long j2 = this.aq;
        if (j2 == 0) {
            return -1;
        }
        int iStop = stop(j2);
        a(iStop);
        return iStop;
    }

    public String c() {
        long j2 = this.aq;
        return j2 != 0 ? getResult(j2) : "";
    }

    public String c(int i2) {
        long j2 = this.aq;
        return j2 != 0 ? getOptionValue(j2, i2) : "";
    }

    public int d() {
        long j2 = this.aq;
        if (j2 != 0) {
            return cancel(j2);
        }
        return 0;
    }

    public void e() {
        long j2 = this.aq;
        if (j2 != 0) {
            destroyNative(j2);
            this.aq = 0L;
        }
    }

    public int f() {
        long j2 = this.aq;
        if (j2 != 0) {
            return getLastErrno(j2);
        }
        return 0;
    }

    public int g() {
        long j2 = this.aq;
        if (j2 != 0) {
            return login(j2);
        }
        return -1;
    }
}
