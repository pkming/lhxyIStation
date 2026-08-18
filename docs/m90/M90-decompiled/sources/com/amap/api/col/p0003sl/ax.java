package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.bp;
import com.amap.api.col.p0003sl.by;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import java.io.File;

/* JADX INFO: compiled from: CityObject.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ax extends OfflineMapCity implements bg, bx {
    public static final Parcelable.Creator<ax> CREATOR = new Parcelable.Creator<ax>() { // from class: com.amap.api.col.3sl.ax.2
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ ax createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ ax[] newArray(int i) {
            return a(i);
        }

        private static ax a(Parcel parcel) {
            return new ax(parcel);
        }

        private static ax[] a(int i) {
            return new ax[i];
        }
    };
    public final cb a;
    public final cb b;
    public final cb c;
    public final cb d;
    public final cb e;
    public final cb f;
    public final cb g;
    public final cb h;
    public final cb i;
    public final cb j;
    public final cb k;
    cb l;
    Context m;
    boolean n;
    private String o;
    private String p;
    private long q;

    @Override // com.amap.api.maps.offlinemap.OfflineMapCity, com.amap.api.maps.offlinemap.City, android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public final void a(String str) {
        this.p = str;
    }

    public final String a() {
        return this.p;
    }

    @Override // com.amap.api.col.p0003sl.bg
    public final String b() {
        return getUrl();
    }

    public ax(Context context, OfflineMapCity offlineMapCity) {
        this(context, offlineMapCity.getState());
        setCity(offlineMapCity.getCity());
        setUrl(offlineMapCity.getUrl());
        setState(offlineMapCity.getState());
        setCompleteCode(offlineMapCity.getcompleteCode());
        setAdcode(offlineMapCity.getAdcode());
        setVersion(offlineMapCity.getVersion());
        setSize(offlineMapCity.getSize());
        setCode(offlineMapCity.getCode());
        setJianpin(offlineMapCity.getJianpin());
        setPinyin(offlineMapCity.getPinyin());
        s();
    }

    private ax(Context context, int i) {
        this.a = new cd(this);
        this.b = new ck(this);
        this.c = new cg(this);
        this.d = new ci(this);
        this.e = new cj(this);
        this.f = new cc(this);
        this.g = new ch(this);
        this.h = new ce(-1, this);
        this.i = new ce(101, this);
        this.j = new ce(102, this);
        this.k = new ce(103, this);
        this.o = null;
        this.p = "";
        this.n = false;
        this.q = 0L;
        this.m = context;
        a(i);
    }

    public final void a(int i) {
        if (i != -1) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        if (i != 3) {
                            if (i != 4) {
                                if (i != 6) {
                                    if (i == 7) {
                                        this.l = this.g;
                                    } else {
                                        switch (i) {
                                            case 101:
                                                this.l = this.i;
                                                break;
                                            case 102:
                                                this.l = this.j;
                                                break;
                                            case 103:
                                                this.l = this.k;
                                                break;
                                            default:
                                                if (i < 0) {
                                                    this.l = this.h;
                                                }
                                                break;
                                        }
                                    }
                                } else {
                                    this.l = this.a;
                                }
                            } else {
                                this.l = this.f;
                            }
                        } else {
                            this.l = this.d;
                        }
                    } else {
                        this.l = this.b;
                    }
                } else {
                    this.l = this.e;
                }
            } else {
                this.l = this.c;
            }
        } else {
            this.l = this.h;
        }
        setState(i);
    }

    public final void a(cb cbVar) {
        this.l = cbVar;
        setState(cbVar.b());
    }

    public final cb c() {
        return this.l;
    }

    public final void d() {
        ay ayVarA = ay.a(this.m);
        if (ayVarA != null) {
            ayVarA.c(this);
        }
    }

    public final void e() {
        ay ayVarA = ay.a(this.m);
        if (ayVarA != null) {
            ayVarA.e(this);
            d();
        }
    }

    public final void f() {
        new StringBuilder("CityOperation current State==>").append(c().b());
        if (this.l.equals(this.d)) {
            this.l.d();
            return;
        }
        if (this.l.equals(this.c)) {
            this.l.e();
            return;
        }
        if (this.l.equals(this.g) || this.l.equals(this.h)) {
            z();
            this.n = true;
        } else if (this.l.equals(this.j) || this.l.equals(this.i) || this.l.a(this.k)) {
            this.l.c();
        } else {
            c().h();
        }
    }

    public final void g() {
        this.l.e();
    }

    public final void h() {
        this.l.a(this.k.b());
    }

    public final void i() {
        this.l.a();
        if (this.n) {
            this.l.h();
        }
        this.n = false;
    }

    public final void j() {
        this.l.equals(this.f);
        this.l.f();
    }

    private void z() {
        ay ayVarA = ay.a(this.m);
        if (ayVarA != null) {
            ayVarA.a(this);
        }
    }

    public final void k() {
        ay ayVarA = ay.a(this.m);
        if (ayVarA != null) {
            ayVarA.b(this);
        }
    }

    public final void l() {
        ay ayVarA = ay.a(this.m);
        if (ayVarA != null) {
            ayVarA.d(this);
        }
    }

    @Override // com.amap.api.col.p0003sl.by
    public final void m() {
        this.q = 0L;
        this.l.equals(this.b);
        this.l.c();
    }

    @Override // com.amap.api.col.p0003sl.by
    public final void a(long j, long j2) {
        int i = (int) ((j2 * 100) / j);
        if (i != getcompleteCode()) {
            setCompleteCode(i);
            d();
        }
    }

    @Override // com.amap.api.col.p0003sl.by
    public final void n() {
        this.l.equals(this.c);
        this.l.g();
    }

    /* JADX INFO: renamed from: com.amap.api.col.3sl.ax$3, reason: invalid class name */
    /* JADX INFO: compiled from: CityObject.java */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] a;

        static {
            int[] iArr = new int[by.a.values().length];
            a = iArr;
            try {
                iArr[by.a.amap_exception.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                a[by.a.file_io_exception.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                a[by.a.network_exception.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    @Override // com.amap.api.col.p0003sl.by
    public final void a(by.a aVar) {
        int iB;
        int i = AnonymousClass3.a[aVar.ordinal()];
        if (i == 1) {
            iB = this.j.b();
        } else if (i == 2) {
            iB = this.k.b();
        } else {
            iB = i != 3 ? 6 : this.i.b();
        }
        if (this.l.equals(this.c) || this.l.equals(this.b)) {
            this.l.a(iB);
        }
    }

    @Override // com.amap.api.col.p0003sl.by
    public final void o() {
        e();
    }

    @Override // com.amap.api.col.p0003sl.bq
    public final void p() {
        this.q = 0L;
        setCompleteCode(0);
        this.l.equals(this.e);
        this.l.c();
    }

    @Override // com.amap.api.col.p0003sl.bq
    public final void q() {
        this.l.equals(this.e);
        this.l.a(this.h.b());
    }

    @Override // com.amap.api.col.p0003sl.bq
    public final void a(long j) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - this.q > 500) {
            int i = (int) j;
            if (i > getcompleteCode()) {
                setCompleteCode(i);
                d();
            }
            this.q = jCurrentTimeMillis;
        }
    }

    @Override // com.amap.api.col.p0003sl.bq
    public final void b(String str) {
        this.l.equals(this.e);
        this.p = str;
        String strA = A();
        String strB = B();
        if (TextUtils.isEmpty(strA) || TextUtils.isEmpty(strB)) {
            q();
            return;
        }
        File file = new File(strB + "/");
        File file2 = new File(dx.a(this.m) + File.separator + "map/");
        File file3 = new File(dx.a(this.m));
        if (file3.exists() || file3.mkdir()) {
            if (file2.exists() || file2.mkdir()) {
                a(file, file2, strA);
            }
        }
    }

    @Override // com.amap.api.col.p0003sl.bq
    public final void r() {
        e();
    }

    protected final void s() {
        String str = ay.a;
        String strB = bv.b(getUrl());
        if (strB != null) {
            this.o = str + strB + ".zip.tmp";
        } else {
            this.o = str + getPinyin() + ".zip.tmp";
        }
    }

    private String A() {
        if (TextUtils.isEmpty(this.o)) {
            return null;
        }
        String str = this.o;
        return str.substring(0, str.lastIndexOf("."));
    }

    private String B() {
        if (TextUtils.isEmpty(this.o)) {
            return null;
        }
        String strA = A();
        return strA.substring(0, strA.lastIndexOf(46));
    }

    private void a(final File file, File file2, final String str) {
        new bp().a(file, file2, -1L, bv.a(file), new bp.a() { // from class: com.amap.api.col.3sl.ax.1
            @Override // com.amap.api.col.3sl.bp.a
            public final void a(float f) {
                int i = (int) ((((double) f) * 0.39d) + 60.0d);
                if (i - ax.this.getcompleteCode() <= 0 || System.currentTimeMillis() - ax.this.q <= 1000) {
                    return;
                }
                ax.this.setCompleteCode(i);
                ax.this.q = System.currentTimeMillis();
            }

            @Override // com.amap.api.col.3sl.bp.a
            public final void a() {
                try {
                    if (new File(str).delete()) {
                        bv.b(file);
                        ax.this.setCompleteCode(100);
                        ax.this.l.g();
                    }
                } catch (Exception unused) {
                    ax.this.l.a(ax.this.k.b());
                }
            }

            @Override // com.amap.api.col.3sl.bp.a
            public final void b() {
                ax.this.l.a(ax.this.k.b());
            }
        });
    }

    private boolean C() {
        if (bv.a() < (getSize() * 2.5d) - (((long) getcompleteCode()) * getSize())) {
        }
        return false;
    }

    public final bi t() {
        setState(this.l.b());
        bi biVar = new bi(this, this.m);
        biVar.a(a());
        new StringBuilder("vMapFileNames: ").append(a());
        return biVar;
    }

    @Override // com.amap.api.maps.offlinemap.OfflineMapCity, com.amap.api.maps.offlinemap.City, android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.p);
    }

    public ax(Parcel parcel) {
        super(parcel);
        this.a = new cd(this);
        this.b = new ck(this);
        this.c = new cg(this);
        this.d = new ci(this);
        this.e = new cj(this);
        this.f = new cc(this);
        this.g = new ch(this);
        this.h = new ce(-1, this);
        this.i = new ce(101, this);
        this.j = new ce(102, this);
        this.k = new ce(103, this);
        this.o = null;
        this.p = "";
        this.n = false;
        this.q = 0L;
        this.p = parcel.readString();
    }

    @Override // com.amap.api.col.p0003sl.bx
    public final boolean u() {
        return C();
    }

    @Override // com.amap.api.col.p0003sl.bx
    public final String v() {
        StringBuffer stringBuffer = new StringBuffer();
        String strB = bv.b(getUrl());
        if (strB != null) {
            stringBuffer.append(strB);
        } else {
            stringBuffer.append(getPinyin());
        }
        stringBuffer.append(".zip");
        return stringBuffer.toString();
    }

    @Override // com.amap.api.col.p0003sl.bx
    public final String w() {
        return getAdcode();
    }

    @Override // com.amap.api.col.p0003sl.br
    public final String x() {
        return A();
    }

    @Override // com.amap.api.col.p0003sl.br
    public final String y() {
        return B();
    }

    public final cb b(int i) {
        switch (i) {
            case 101:
                return this.i;
            case 102:
                return this.j;
            case 103:
                return this.k;
            default:
                return this.h;
        }
    }
}
