package com.amap.api.col.p0003sl;

import android.net.LinkQualityInfo;
import android.os.SystemClock;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: compiled from: CellCollector.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mk {
    private nk a;
    private nk b;
    private nq c;
    private a d = new a();
    private final List<nk> e = new ArrayList(3);

    final a a(nq nqVar, boolean z, byte b, String str, List<nk> list) {
        if (z) {
            this.d.a();
            return null;
        }
        this.d.a(b, str, list);
        if (this.d.c == null) {
            return null;
        }
        if (!(this.c == null || a(nqVar) || !a.a(this.d.d, this.a) || !a.a(this.d.e, this.b))) {
            return null;
        }
        this.a = this.d.d;
        this.b = this.d.e;
        this.c = nqVar;
        ng.a(this.d.f);
        a(this.d);
        return this.d;
    }

    private boolean a(nq nqVar) {
        return nqVar.a(this.c) > ((double) ((nqVar.g > 10.0f ? 1 : (nqVar.g == 10.0f ? 0 : -1)) > 0 ? 2000.0f : (nqVar.g > 2.0f ? 1 : (nqVar.g == 2.0f ? 0 : -1)) > 0 ? 500.0f : 100.0f));
    }

    private void a(a aVar) {
        synchronized (this.e) {
            for (nk nkVar : aVar.f) {
                if (nkVar != null && nkVar.h) {
                    nk nkVarClone = nkVar.clone();
                    nkVarClone.e = SystemClock.elapsedRealtime();
                    a(nkVarClone);
                }
            }
            this.d.g.clear();
            this.d.g.addAll(this.e);
        }
    }

    private void a(nk nkVar) {
        if (nkVar == null) {
            return;
        }
        int size = this.e.size();
        if (size == 0) {
            this.e.add(nkVar);
            return;
        }
        long jMin = LinkQualityInfo.UNKNOWN_LONG;
        int i = 0;
        int i2 = -1;
        int i3 = -1;
        while (true) {
            if (i >= size) {
                i2 = i3;
                break;
            }
            nk nkVar2 = this.e.get(i);
            if (nkVar.equals(nkVar2)) {
                if (nkVar.c != nkVar2.c) {
                    nkVar2.e = nkVar.c;
                    nkVar2.c = nkVar.c;
                }
            } else {
                jMin = Math.min(jMin, nkVar2.e);
                if (jMin == nkVar2.e) {
                    i3 = i;
                }
                i++;
            }
        }
        if (i2 >= 0) {
            if (size < 3) {
                this.e.add(nkVar);
            } else {
                if (nkVar.e <= jMin || i2 >= size) {
                    return;
                }
                this.e.remove(i2);
                this.e.add(nkVar);
            }
        }
    }

    /* JADX INFO: compiled from: CellCollector.java */
    public static class a {
        public byte a;
        public String b;
        public nk c;
        public nk d;
        public nk e;
        public List<nk> f = new ArrayList();
        public List<nk> g = new ArrayList();

        public final void a() {
            this.a = (byte) 0;
            this.b = "";
            this.c = null;
            this.d = null;
            this.e = null;
            this.f.clear();
            this.g.clear();
        }

        public final void a(byte b, String str, List<nk> list) {
            a();
            this.a = b;
            this.b = str;
            if (list != null) {
                this.f.addAll(list);
                for (nk nkVar : this.f) {
                    if (!nkVar.i && nkVar.h) {
                        this.d = nkVar;
                    } else if (nkVar.i && nkVar.h) {
                        this.e = nkVar;
                    }
                }
            }
            nk nkVar2 = this.d;
            if (nkVar2 == null) {
                nkVar2 = this.e;
            }
            this.c = nkVar2;
        }

        public static boolean a(nk nkVar, nk nkVar2) {
            if (nkVar == null || nkVar2 == null) {
                return (nkVar == null) == (nkVar2 == null);
            }
            if ((nkVar instanceof nm) && (nkVar2 instanceof nm)) {
                nm nmVar = (nm) nkVar;
                nm nmVar2 = (nm) nkVar2;
                return nmVar.j == nmVar2.j && nmVar.k == nmVar2.k;
            }
            if ((nkVar instanceof nl) && (nkVar2 instanceof nl)) {
                nl nlVar = (nl) nkVar;
                nl nlVar2 = (nl) nkVar2;
                return nlVar.l == nlVar2.l && nlVar.k == nlVar2.k && nlVar.j == nlVar2.j;
            }
            if ((nkVar instanceof nn) && (nkVar2 instanceof nn)) {
                nn nnVar = (nn) nkVar;
                nn nnVar2 = (nn) nkVar2;
                return nnVar.j == nnVar2.j && nnVar.k == nnVar2.k;
            }
            if ((nkVar instanceof no) && (nkVar2 instanceof no)) {
                no noVar = (no) nkVar;
                no noVar2 = (no) nkVar2;
                if (noVar.j == noVar2.j && noVar.k == noVar2.k) {
                    return true;
                }
            }
            return false;
        }

        public final String toString() {
            return "CellInfo{radio=" + ((int) this.a) + ", operator='" + this.b + DateFormat.QUOTE + ", mainCell=" + this.c + ", mainOldInterCell=" + this.d + ", mainNewInterCell=" + this.e + ", cells=" + this.f + ", historyMainCellList=" + this.g + '}';
        }
    }
}
