package com.amap.api.col.p0003sl;

import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: compiled from: RssiManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ng {
    public static long a(int i, int i2) {
        return (((long) i2) & ExpandableListView.PACKED_POSITION_VALUE_NULL) | ((((long) i) & ExpandableListView.PACKED_POSITION_VALUE_NULL) << 32);
    }

    public static synchronized void a(List<nk> list) {
        if (list != null) {
            if (!list.isEmpty()) {
                ArrayList arrayList = new ArrayList(list.size());
                for (nk nkVar : list) {
                    if (nkVar instanceof nm) {
                        nm nmVar = (nm) nkVar;
                        arrayList.add(new a(nmVar.j, nmVar.k, nmVar.c));
                    } else if (nkVar instanceof nn) {
                        nn nnVar = (nn) nkVar;
                        arrayList.add(new a(nnVar.j, nnVar.k, nnVar.c));
                    } else if (nkVar instanceof no) {
                        no noVar = (no) nkVar;
                        arrayList.add(new a(noVar.j, noVar.k, noVar.c));
                    } else if (nkVar instanceof nl) {
                        nl nlVar = (nl) nkVar;
                        arrayList.add(new a(nlVar.k, nlVar.l, nlVar.c));
                    }
                }
                nf.a().a(arrayList);
            }
        }
    }

    public static synchronized void b(List<nr> list) {
        if (list != null) {
            if (!list.isEmpty()) {
                ArrayList arrayList = new ArrayList(list.size());
                for (nr nrVar : list) {
                    arrayList.add(new b(nrVar.a, nrVar.c));
                }
                nf.a().b(arrayList);
            }
        }
    }

    public static synchronized short a(long j) {
        return nf.a().a(j);
    }

    public static synchronized short b(long j) {
        return nf.a().b(j);
    }

    /* JADX INFO: compiled from: RssiManager.java */
    public static class a implements ne {
        private int a;
        private int b;
        private int c;

        a(int i, int i2, int i3) {
            this.a = i;
            this.b = i2;
            this.c = i3;
        }

        @Override // com.amap.api.col.p0003sl.ne
        public final long a() {
            return ng.a(this.a, this.b);
        }

        @Override // com.amap.api.col.p0003sl.ne
        public final int b() {
            return this.c;
        }
    }

    /* JADX INFO: compiled from: RssiManager.java */
    public static class b implements ne {
        private long a;
        private int b;

        b(long j, int i) {
            this.a = j;
            this.b = i;
        }

        @Override // com.amap.api.col.p0003sl.ne
        public final long a() {
            return this.a;
        }

        @Override // com.amap.api.col.p0003sl.ne
        public final int b() {
            return this.b;
        }
    }
}
