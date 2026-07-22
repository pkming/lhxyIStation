package com.amap.api.col.p0003sl;

import android.os.SystemClock;
import com.amap.api.col.p0003sl.mk;
import java.util.List;

/* JADX INFO: compiled from: FpsBufferBuilder.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mh extends mg {
    public mh() {
        super(2048);
    }

    public final byte[] a(nq nqVar, mk.a aVar, long j, List<nr> list) {
        super.a();
        try {
            int iA = a(nqVar);
            int iA2 = -1;
            int iA3 = (aVar == null || aVar.f == null || aVar.f.size() <= 0) ? -1 : a(aVar);
            if (list != null && list.size() > 0) {
                iA2 = a(j, list);
            }
            mp.a(this.a);
            mp.a(this.a, iA);
            if (iA3 > 0) {
                mp.c(this.a, iA3);
            }
            if (iA2 > 0) {
                mp.b(this.a, iA2);
            }
            this.a.c(mp.b(this.a));
            return this.a.c();
        } catch (Throwable th) {
            nu.a(th);
            return null;
        }
    }

    private int a(nq nqVar) {
        return mw.a(this.a, nqVar.c, nqVar.k, (int) (nqVar.e * 1000000.0d), (int) (nqVar.d * 1000000.0d), (int) nqVar.f, (int) nqVar.i, (int) nqVar.g, (short) nqVar.h, nqVar.l);
    }

    private int a(mk.a aVar) {
        int iA;
        int i;
        int i2;
        byte b;
        int i3;
        int iA2;
        int iA3;
        a(aVar.f);
        int size = aVar.f.size();
        int[] iArr = new int[size];
        int i4 = 0;
        while (true) {
            byte b2 = 2;
            if (i4 < size) {
                nk nkVar = aVar.f.get(i4);
                if (nkVar instanceof nm) {
                    nm nmVar = (nm) nkVar;
                    if (!nmVar.i) {
                        iA3 = mx.a(this.a, nmVar.j, nmVar.k, nmVar.c, nmVar.l);
                    } else {
                        iA3 = mx.a(this.a, nmVar.b(), nmVar.c(), nmVar.j, nmVar.k, nmVar.c, nmVar.m, nmVar.n, nmVar.d, nmVar.l);
                    }
                    i3 = iA3;
                    i2 = -1;
                    b = 1;
                } else {
                    if (nkVar instanceof nn) {
                        b2 = 3;
                        nn nnVar = (nn) nkVar;
                        iA2 = my.a(this.a, nnVar.b(), nnVar.c(), nnVar.j, nnVar.k, nnVar.l, nnVar.c, nnVar.m, nnVar.d);
                    } else if (nkVar instanceof nl) {
                        nl nlVar = (nl) nkVar;
                        if (!nlVar.i) {
                            iA2 = mr.a(this.a, nlVar.j, nlVar.k, nlVar.l, nlVar.m, nlVar.n, nlVar.c);
                        } else {
                            iA2 = mr.a(this.a, nlVar.j, nlVar.k, nlVar.l, nlVar.m, nlVar.n, nlVar.c, nlVar.d);
                        }
                    } else if (nkVar instanceof no) {
                        b2 = 4;
                        no noVar = (no) nkVar;
                        iA2 = nb.a(this.a, noVar.b(), noVar.c(), noVar.j, noVar.k, noVar.l, noVar.c, noVar.m, noVar.d);
                    } else {
                        i2 = -1;
                        b = 0;
                        i3 = -1;
                    }
                    i3 = iA2;
                    b = b2;
                    i2 = -1;
                }
                if (i3 == i2) {
                    return i2;
                }
                iArr[i4] = mu.a(this.a, nkVar.h ? (byte) 1 : (byte) 0, nkVar.i ? (byte) 1 : (byte) 0, (short) nkVar.g, b, i3);
                i4++;
            } else {
                int iA4 = this.a.a(aVar.b);
                int iA5 = ms.a(this.a, iArr);
                int size2 = aVar.g.size();
                int[] iArr2 = new int[size2];
                for (int i5 = 0; i5 < size2; i5++) {
                    nk nkVar2 = aVar.g.get(i5);
                    long jElapsedRealtime = (SystemClock.elapsedRealtime() - nkVar2.e) / 1000;
                    if (jElapsedRealtime > 32767 || jElapsedRealtime < 0) {
                        jElapsedRealtime = 32767;
                    }
                    if (nkVar2 instanceof nm) {
                        nm nmVar2 = (nm) nkVar2;
                        iA = na.a(this.a, nmVar2.j, nmVar2.k, (short) jElapsedRealtime);
                    } else if (nkVar2 instanceof nn) {
                        nn nnVar2 = (nn) nkVar2;
                        iA = na.a(this.a, nnVar2.j, nnVar2.k, (short) jElapsedRealtime);
                    } else {
                        if (nkVar2 instanceof nl) {
                            nl nlVar2 = (nl) nkVar2;
                            iA = mz.a(this.a, nlVar2.j, nlVar2.k, nlVar2.l, (short) jElapsedRealtime);
                            i = 2;
                        } else if (nkVar2 instanceof no) {
                            no noVar2 = (no) nkVar2;
                            iA = na.a(this.a, noVar2.j, noVar2.k, (short) jElapsedRealtime);
                        } else {
                            iA = 0;
                            i = 0;
                        }
                        iArr2[i5] = mt.a(this.a, (byte) i, iA);
                    }
                    i = 1;
                    iArr2[i5] = mt.a(this.a, (byte) i, iA);
                }
                return ms.a(this.a, iA4, aVar.a, iA5, ms.b(this.a, iArr2));
            }
        }
    }

    private int a(long j, List<nr> list) {
        b(list);
        int size = list.size();
        if (size <= 0) {
            return -1;
        }
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            nr nrVar = list.get(i);
            iArr[i] = nd.a(this.a, nrVar.a == j && nrVar.a != -1, nrVar.a, (short) nrVar.c, this.a.a(nrVar.b), nrVar.g, (short) nrVar.d);
        }
        return nc.a(this.a, nc.a(this.a, iArr));
    }

    private static void a(List<nk> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (nk nkVar : list) {
            if (nkVar instanceof nm) {
                nm nmVar = (nm) nkVar;
                nkVar.g = ng.a(ng.a(nmVar.j, nmVar.k));
            } else if (nkVar instanceof nn) {
                nn nnVar = (nn) nkVar;
                nkVar.g = ng.a(ng.a(nnVar.j, nnVar.k));
            } else if (nkVar instanceof no) {
                no noVar = (no) nkVar;
                nkVar.g = ng.a(ng.a(noVar.j, noVar.k));
            } else if (nkVar instanceof nl) {
                nl nlVar = (nl) nkVar;
                nkVar.g = ng.a(ng.a(nlVar.k, nlVar.l));
            }
        }
    }

    private static void b(List<nr> list) {
        for (nr nrVar : list) {
            nrVar.g = ng.b(nrVar.a);
        }
    }
}
