package cn.yunzhisheng.asr;

import com.unisound.sdk.y;

/* JADX INFO: loaded from: classes2.dex */
public class a {
    public static final int O = 3000;
    public static final int P = 300;
    public static final int Q = 1000;
    public static final int R = 16000;
    public static final int S = 8000;
    public static final int T = 500;
    public static final boolean U = false;
    public static final boolean V = true;
    public static final int a = 10000;
    public static final int al = 1;
    public static final int b = 20000;
    static final int c = 5;
    static final int d = 6;
    static final int e = 7;
    static final int f = 8;
    static final int g = 9;
    static final int h = 10;
    static final int i = 11;
    static final int j = 12;
    static final int k = 13;
    static final int l = 14;
    static final int m = 15;
    static final int n = 16;
    static final int o = 17;
    static final int p = 18;
    static final int q = 19;
    static final int r = 20;
    static final int s = 21;
    static final int t = 22;
    static final int u = 23;
    static final int v = 24;
    static final int w = 25;
    public c x = new c(this, 5);
    public c y = new c(this, 6);
    public c z = new c(this, 7);
    public d A = new d(this, 8);
    public d B = new d(this, 9);
    public c C = new c(this, 10);
    public d D = new d(this, 11);
    public d E = new d(this, 12);
    public c F = new c(this, 13);
    public c G = new c(this, 14);
    public c H = new c(this, 15);
    public c I = new c(this, 16);
    public d J = new d(this, 17);
    public d K = new d(this, 18);
    public d L = new d(this, 21);
    public d M = new d(this, 24);
    public c N = new c(this, 25);
    private int an = 0;
    boolean W = false;
    boolean X = false;
    int Y = 16000;
    int Z = 3000;
    int aa = 300;
    int ab = 16000;
    int ac = 38000;
    protected boolean ad = false;
    private int ao = 350;
    public boolean ae = false;
    private boolean ap = false;
    private String aq = null;
    private boolean ar = true;
    int af = 0;
    int ag = 16000;
    private int as = 700;
    public boolean ah = false;
    boolean ai = false;
    private boolean at = false;
    private boolean au = false;
    private boolean av = false;
    private float aw = -2.7f;
    private int ax = 1000;
    public int aj = 2000;
    public String ak = "";
    private String ay = "";
    private boolean az = false;
    private int aA = 0;
    private int aB = 2000;
    public int am = 1;

    public a() {
        f(500);
        p();
        this.M.a(0);
        this.N.a(-0.25f);
    }

    public float A() {
        return this.aw;
    }

    public int B() {
        return this.ax;
    }

    public int C() {
        return (this.Y / 1000) * this.ax * 2;
    }

    public int D() {
        return (this.Y / 1000) * this.as * 2;
    }

    public int E() {
        return this.aj;
    }

    public int F() {
        return (this.Y / 1000) * this.aj * 2;
    }

    public String G() {
        return this.ak;
    }

    public String H() {
        return this.ay;
    }

    public boolean I() {
        return this.az;
    }

    public int J() {
        return this.aA;
    }

    public int K() {
        return this.aB;
    }

    public boolean L() {
        try {
            return Integer.parseInt(this.M.a) == 1;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public float M() {
        try {
            return Float.parseFloat(this.N.a);
        } catch (Exception e2) {
            e2.printStackTrace();
            return 0.0f;
        }
    }

    public int N() {
        return this.am;
    }

    public void a(float f2) {
        this.aw = f2;
    }

    public void a(int i2) {
        this.af = i2;
    }

    public void a(int i2, int i3) {
        this.Z = i2;
        if (i3 > 300) {
            this.as = i3 + y.l;
            i3 = 300;
        } else {
            this.as = 0;
        }
        this.E.a(i3 / 10);
        this.aa = i3;
    }

    public void a(String str) {
        this.aq = str;
    }

    public void a(boolean z) {
        this.ap = z;
    }

    public boolean a() {
        return this.ap;
    }

    public String b() {
        return this.aq;
    }

    public void b(float f2) {
        this.N.a(f2);
    }

    public void b(int i2) {
        this.ag = i2;
    }

    public void b(String str) {
        this.ak = str;
    }

    public void b(boolean z) {
        this.ad = z;
    }

    public int c() {
        return this.af;
    }

    public void c(int i2) {
        this.ao = i2;
    }

    public void c(String str) {
        this.ay = str;
    }

    public void c(boolean z) {
        this.W = z;
    }

    public int d() {
        int i2 = this.ag;
        if (this.ae) {
            return 8000;
        }
        return i2;
    }

    public void d(int i2) {
        this.Z = i2;
    }

    public void d(String str) {
        this.az = str.equals(this.ay);
    }

    public void d(boolean z) {
        this.ah = z;
    }

    public int e() {
        return this.ao;
    }

    public void e(int i2) {
        if (i2 > 300) {
            this.as = i2 + y.l;
            i2 = 300;
        } else {
            this.as = 0;
        }
        this.E.a(i2 / 10);
    }

    public void e(boolean z) {
        this.X = z;
    }

    public void f(int i2) {
        if (i2 < 100) {
            i2 = 100;
        }
        this.ab = (this.Y / 1000) * i2 * 2;
    }

    public void f(boolean z) {
        this.ar = z;
    }

    public boolean f() {
        return this.ad;
    }

    public int g() {
        return this.as;
    }

    public void g(int i2) {
        this.an = i2;
    }

    public void g(boolean z) {
        this.ai = z;
    }

    public void h(int i2) {
        this.ax = i2;
    }

    public void h(boolean z) {
        this.at = z;
    }

    public boolean h() {
        return this.W;
    }

    public void i(int i2) {
        this.aj = i2;
    }

    public void i(boolean z) {
        this.au = z;
    }

    public boolean i() {
        return this.ah;
    }

    public int j(int i2) {
        return (this.Y / 1000) * i2 * 2;
    }

    public void j(boolean z) {
        this.av = z;
    }

    public boolean j() {
        return this.X;
    }

    public void k(int i2) {
        this.aA = i2;
    }

    public void k(boolean z) {
        d dVar;
        int i2;
        if (z) {
            dVar = this.M;
            i2 = 1;
        } else {
            dVar = this.M;
            i2 = 0;
        }
        dVar.a(i2);
    }

    public boolean k() {
        return this.X;
    }

    public void l(int i2) {
        this.aB = i2;
    }

    public boolean l() {
        return this.ar;
    }

    public void m() {
        this.x.a();
        this.y.a();
        this.z.a();
        this.A.a();
        this.B.a();
        this.C.a();
        this.D.a();
        this.E.a();
        this.F.a();
        this.G.a();
        this.H.a();
        this.I.a();
        this.J.a();
        this.K.a();
        this.L.a();
        this.M.a();
        this.N.a();
    }

    public void m(int i2) {
        this.am = i2;
    }

    public void n() {
        m();
        this.H.a(0.7f);
        this.G.a(1.0f);
        this.z.a(0.22f);
        this.A.a(5);
        this.I.a(0.6f);
        this.J.a(1);
        this.K.a(3);
        this.x.a(2.0E8f);
        this.F.a(80.0f);
        this.C.a(1000000.0f);
        this.L.a(1);
    }

    public void o() {
        m();
    }

    public void p() {
        m();
    }

    public void q() {
        m();
    }

    public void r() {
        m();
    }

    public int s() {
        return this.Z;
    }

    public int t() {
        return this.aa;
    }

    public int u() {
        return this.an;
    }

    public int v() {
        return (this.Y / 1000) * this.an * 2;
    }

    public boolean w() {
        return this.ai;
    }

    public boolean x() {
        return this.at;
    }

    public boolean y() {
        return this.au;
    }

    public boolean z() {
        return this.av;
    }
}
