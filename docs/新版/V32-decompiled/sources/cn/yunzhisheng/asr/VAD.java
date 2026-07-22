package cn.yunzhisheng.asr;

import android.mtp.MtpConstants;
import com.unisound.common.i;
import com.unisound.common.r;
import com.unisound.sdk.cf;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class VAD {
    public static final int a = -1;
    public static final int b = 0;
    public static final int c = 1;
    public static final int d = 2;
    public static final int e = 3;
    public static int f = 0;
    private static final int o = 0;
    private static final int p = 1;
    private static final int q = -1001;
    private double D;
    public List<byte[]> h;
    protected long j;
    private a s;
    private cf t;
    private ByteArrayOutputStream r = new ByteArrayOutputStream(MtpConstants.DEVICE_PROPERTY_UNDEFINED);
    public List<byte[]> g = new LinkedList();
    private boolean u = false;
    private boolean v = false;
    public boolean i = false;
    public boolean k = true;
    public boolean l = true;
    public boolean m = false;
    public boolean n = false;
    private boolean w = false;
    private boolean x = false;
    private byte[] y = {99};
    private ArrayList<byte[]> z = new ArrayList<>();
    private boolean A = false;
    private ArrayList<byte[]> B = new ArrayList<>();
    private boolean C = false;

    public VAD(a aVar, cf cfVar) {
        this.j = 0L;
        this.s = aVar;
        this.t = cfVar;
        long jCreate = create();
        this.j = jCreate;
        if (jCreate == 0) {
            r.e("jni VAD create fail!");
            return;
        }
        this.h = new LinkedList();
        a(this.s.l());
        init(this.j);
    }

    private double a(double d2) {
        return d2 / 32.0d;
    }

    private int a(byte[] bArr, int i, byte[] bArr2, int i2) {
        int i3 = 0;
        int i4 = 0;
        while (i3 < i - 1) {
            int i5 = i3 + 1;
            byte b2 = bArr[i3];
            int i6 = i5 + 1;
            byte b3 = bArr[i5];
            int i7 = i4 + 1;
            bArr2[i4] = b2;
            int i8 = i7 + 1;
            bArr2[i7] = b3;
            int i9 = i8 + 1;
            bArr2[i8] = b2;
            i4 = i9 + 1;
            bArr2[i9] = b3;
            i3 = i6;
        }
        return i4;
    }

    private void a(String str) {
        r.a("VAD >>" + str);
    }

    private String b(int i, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("timeout", i);
            jSONObject.put("afterTimeoutVoice", str);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return jSONObject.toString();
    }

    private void b(boolean z) {
        if (z != this.v && a()) {
            this.v = z;
            setTime(this.j, q, z ? 1 : 0);
        }
    }

    private void c(int i) {
        cf cfVar = this.t;
        if (cfVar != null) {
            cfVar.b(i);
        }
    }

    private void d(int i) {
        if (this.s.w()) {
            a(i);
        }
        if (this.s.a() && this.s.x()) {
            i.a(false, this.s.b());
        }
        cf cfVar = this.t;
        if (cfVar != null) {
            cfVar.a(this);
        }
        if (i != 2) {
            a("TimeOut");
        }
    }

    private synchronized void d(byte[] bArr) {
        this.g.add(bArr);
        int size = this.g.size() - 1;
        int length = 0;
        while (true) {
            if (size < 0) {
                size = 0;
                break;
            }
            length += this.g.get(size).length;
            if (length >= this.s.ab) {
                break;
            } else {
                size--;
            }
        }
        for (int i = 0; i < size; i++) {
            byte[] bArrRemove = this.g.remove(0);
            a(false, bArrRemove, 0, bArrRemove.length);
        }
    }

    private void e(int i) {
        this.D += (double) i;
    }

    private synchronized void e(byte[] bArr) {
        this.h.add(bArr);
        int size = this.h.size() - 1;
        int length = 0;
        while (true) {
            if (size < 0) {
                size = 0;
                break;
            }
            length += this.h.get(size).length;
            if (length >= this.s.F()) {
                break;
            } else {
                size--;
            }
        }
        for (int i = 0; i < size; i++) {
            this.h.remove(0);
        }
    }

    private void f() {
        cf cfVar = this.t;
        if (cfVar != null) {
            cfVar.m();
        }
    }

    private void g() {
        cf cfVar = this.t;
        if (cfVar != null) {
            cfVar.n();
        }
    }

    private void h() {
        this.z.clear();
        this.A = false;
    }

    private void i() {
        this.B.clear();
        this.C = false;
    }

    private void j() {
        this.D = 0.0d;
    }

    private double k() {
        return this.D;
    }

    public int a(int i, String str) {
        if (this.j == 0) {
            return -1;
        }
        if (str == null || str.length() == 0) {
            return 0;
        }
        return nativeSetOption(this.j, i, str);
    }

    public int a(b bVar) {
        if (bVar.c()) {
            return a(bVar.b, bVar.toString());
        }
        return 0;
    }

    public int a(byte[] bArr, int i) {
        if (a()) {
            return checkPitchOffset(this.j, bArr, i);
        }
        return 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:60:0x00e4 A[Catch: all -> 0x01e8, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x0006, B:7:0x000c, B:9:0x0012, B:12:0x0017, B:16:0x001d, B:18:0x0021, B:21:0x002d, B:25:0x003d, B:27:0x0045, B:29:0x0049, B:30:0x0055, B:67:0x011d, B:69:0x0121, B:71:0x0127, B:73:0x012f, B:82:0x0152, B:84:0x0166, B:86:0x016e, B:87:0x0172, B:89:0x017a, B:91:0x017e, B:93:0x0184, B:94:0x01b2, B:96:0x01b8, B:75:0x0137, B:76:0x013b, B:78:0x0141, B:80:0x0145, B:81:0x014f, B:34:0x0060, B:36:0x0068, B:38:0x006c, B:40:0x0071, B:42:0x0079, B:44:0x0081, B:45:0x008a, B:39:0x006f, B:47:0x00a9, B:49:0x00b1, B:52:0x00ca, B:54:0x00d2, B:56:0x00da, B:57:0x00dc, B:59:0x00e0, B:61:0x00e7, B:63:0x00ef, B:65:0x00f7, B:66:0x0100, B:60:0x00e4, B:24:0x0035), top: B:103:0x0001 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized int a(byte[] r7, int r8, int r9) {
        /*
            Method dump skipped, instruction units count: 491
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.yunzhisheng.asr.VAD.a(byte[], int, int):int");
    }

    public synchronized void a(int i) {
        this.i = false;
        this.u = false;
        this.n = false;
        this.w = false;
        this.g.clear();
        this.r.reset();
        if (this.s.y() && !this.s.z()) {
            this.h.clear();
        }
        if (this.s.y()) {
            this.x = false;
        }
        if (a()) {
            if (i == 2) {
                reset(this.j);
            }
        }
    }

    public void a(int i, int i2) {
        if (a()) {
            setTime(this.j, i / 10, i2 / 10);
        }
    }

    public void a(boolean z) {
        this.k = z;
    }

    public void a(boolean z, byte[] bArr, int i, int i2) {
        cf cfVar = this.t;
        if (this.s.N() == 0) {
            if (z && this.s.y() && !this.s.z() && (bArr.length != 1 || (bArr[0] != 100 && bArr[0] != 99))) {
                e(bArr);
            }
        } else if (z && this.s.y() && !this.s.z() && (bArr.length != 1 || (bArr[0] != 100 && bArr[0] != 99))) {
            this.h.add(bArr);
        }
        if (cfVar != null) {
            cfVar.b(z, bArr, i, i2);
        }
    }

    protected synchronized void a(byte[] bArr) {
        this.r.write(bArr, 0, bArr.length);
        if (this.r.size() >= this.s.ac) {
            byte[] byteArray = this.r.toByteArray();
            this.r.reset();
            int iCheckPitchOffset = checkPitchOffset(this.j, byteArray, byteArray.length);
            if (iCheckPitchOffset > 0) {
                byte[] bArr2 = new byte[iCheckPitchOffset];
                System.arraycopy(byteArray, 0, bArr2, 0, iCheckPitchOffset);
                a(false, bArr2, 0, iCheckPitchOffset);
                this.r.write(byteArray, iCheckPitchOffset, byteArray.length - iCheckPitchOffset);
                byteArray = this.r.toByteArray();
                this.r.reset();
            }
            f = iCheckPitchOffset;
            if (byteArray.length > 0) {
                a(true, byteArray, 0, byteArray.length);
                b(byteArray, byteArray.length);
            }
            this.u = true;
            this.i = true;
        }
    }

    public boolean a() {
        return this.j != 0;
    }

    public int b(byte[] bArr, int i) {
        long j = this.j;
        if (j == 0) {
            return 0;
        }
        return isVADTimeout(j, bArr, i);
    }

    public void b() {
        r.b("frontSil = " + this.s.Z + " backSil= " + this.s.aa);
        a(this.s.Z, this.s.aa);
        if (this.s.f()) {
            r.b("mParams.isFarFeildEnabled() = " + this.s.f());
            b(this.s.f());
        }
        if (this.s.x.a != null && !this.s.x.a.equals("")) {
            r.b("mParams.MINBACKENG = " + this.s.x.a);
            a(this.s.x);
        }
        if (this.s.y.a != null && !this.s.y.a.equals("")) {
            r.b("mParams.MINBACKENGH = " + this.s.y.a);
            a(this.s.y);
        }
        if (this.s.z.a != null && !this.s.z.a.equals("")) {
            r.b("mParams.PITCHTH = " + this.s.z.a);
            a(this.s.z);
        }
        if (this.s.A.a != null && !this.s.A.a.equals("")) {
            r.b("mParams.PITCHSTNUMTH = " + this.s.A.a);
            a(this.s.A);
        }
        if (this.s.B.a != null && !this.s.B.a.equals("")) {
            r.b("mParams.PITCHENDNUMTH = " + this.s.B.a);
            a(this.s.B);
        }
        if (this.s.C.a != null && !this.s.C.a.equals("")) {
            r.b("mParams.LOWHIGHTH = " + this.s.C.a);
            a(this.s.C);
        }
        if (this.s.D.a != null && !this.s.D.a.equals("")) {
            r.b("mParams.MINSIGLEN = " + this.s.D.a);
            a(this.s.D);
        }
        if (this.s.E.a != null && !this.s.E.a.equals("") && !this.s.E.a.equals((this.s.aa / 10) + "")) {
            r.b("mParams.MAXSILLEN = " + this.s.E.a);
            a(this.s.E);
        }
        if (this.s.F.a != null && !this.s.F.a.equals("")) {
            r.b("mParams.SINGLEMAX = " + this.s.F.a);
            a(this.s.F);
        }
        if (this.s.G.a != null && !this.s.G.a.equals("")) {
            r.b("mParams.NOISE2YTH = " + this.s.G.a);
            a(this.s.G);
        }
        if (this.s.H.a != null && !this.s.H.a.equals("")) {
            r.b("mParams.NOISE2YTHVOWEL = " + this.s.H.a);
            a(this.s.H);
        }
        if (this.s.I.a != null && !this.s.I.a.equals("")) {
            r.b("mParams.VOICEPROBTH = " + this.s.I.a);
            a(this.s.I);
        }
        if (this.s.J.a != null && !this.s.J.a.equals("")) {
            r.b("mParams.USEPEAK = " + this.s.J.a);
            a(this.s.J);
        }
        if (this.s.K.a != null && !this.s.K.a.equals("")) {
            r.b("mParams.NOISE2YST = " + this.s.K.a);
            a(this.s.K);
        }
        if (this.s.L.a != null && !this.s.L.a.equals("")) {
            r.b("mParams.PITCHLASTTH = " + this.s.L.a);
            a(this.s.L);
        }
        if (this.s.M.a != null && !this.s.M.a.equals("")) {
            r.b("mParams.DETECTMUSIC = " + this.s.M.a);
            a(this.s.M);
        }
        if (this.s.N.a != null && !this.s.N.a.equals("")) {
            r.b("mParams.MUSICTH = " + this.s.N.a);
            a(this.s.N);
        }
        j();
    }

    public synchronized void b(int i) {
        r.c("dropTime =>" + i);
        int iJ = this.s.j(i);
        r.c("dropCacheByteLength =>" + iJ);
        int i2 = 0;
        int length = 0;
        while (true) {
            if (i2 >= this.h.size()) {
                i2 = 0;
                break;
            }
            length += this.h.get(i2).length;
            if (length >= iJ) {
                break;
            } else {
                i2++;
            }
        }
        for (int i3 = 0; i3 < i2; i3++) {
            this.h.remove(0);
        }
    }

    protected boolean b(byte[] bArr) {
        if (this.A) {
            this.z.add(bArr);
            int iC = this.s.C();
            int length = 0;
            for (int size = this.z.size() - 1; size >= 0; size--) {
                length += this.z.get(size).length;
                if (length >= iC) {
                    return true;
                }
            }
        }
        return false;
    }

    public int c() {
        if (a()) {
            return getVolume(this.j);
        }
        return 0;
    }

    protected int c(byte[] bArr, int i) {
        float fAbs = 0.0f;
        for (int i2 = 0; i2 < i; i2 += 2) {
            int i3 = (bArr[i2] & 255) + ((bArr[i2 + 1] & 255) << 8);
            if (i3 >= 32768) {
                i3 = 65535 - i3;
            }
            fAbs += Math.abs(i3);
        }
        int iLog10 = (int) (((Math.log10(((fAbs * 2.0f) / i) + 1.0f) * 10.0d) - 20.0d) * 5.0d);
        int i4 = iLog10 >= 0 ? iLog10 : 0;
        if (i4 > 100) {
            return 100;
        }
        return i4;
    }

    protected boolean c(byte[] bArr) {
        if (this.C) {
            this.B.add(bArr);
            int iD = this.s.D();
            int length = 0;
            for (int size = this.B.size() - 1; size >= 0; size--) {
                length += this.B.get(size).length;
                if (length >= iD) {
                    return true;
                }
            }
        }
        return false;
    }

    protected native int checkPitchOffset(long j, byte[] bArr, int i);

    protected native long create();

    public synchronized void d() {
        if (a()) {
            try {
                this.r.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            destory(this.j);
            this.j = 0L;
        }
    }

    protected native void destory(long j);

    public synchronized void e() {
        if (this.r.size() > 0) {
            a(this.i, this.r.toByteArray(), 0, this.r.size());
            this.r.reset();
        }
        int size = this.g.size();
        for (int i = 0; i < size; i++) {
            byte[] bArrRemove = this.g.remove(0);
            a(this.i, bArrRemove, 0, bArrRemove.length);
        }
        c(0);
    }

    protected native int getVolume(long j);

    protected native void init(long j);

    protected native int isVADTimeout(long j, byte[] bArr, int i);

    protected native int nativeSetOption(long j, int i, String str);

    protected native void reset(long j);

    protected native void setTime(long j, int i, int i2);
}
