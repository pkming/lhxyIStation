package com.amap.api.col.p0003sl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: compiled from: WifiCollector.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mm {
    private nq b;
    private List<nr> a = new ArrayList();
    private ArrayList<nr> c = new ArrayList<>();

    final List<nr> a(nq nqVar, List<nr> list, boolean z, long j, long j2) {
        if (!b(nqVar, list, z, j, j2)) {
            return null;
        }
        b(this.c, list);
        this.a.clear();
        this.a.addAll(list);
        this.b = nqVar;
        return this.c;
    }

    private boolean b(nq nqVar, List<nr> list, boolean z, long j, long j2) {
        if (!z || !a(nqVar, j, j2) || list == null || list.size() <= 0) {
            return false;
        }
        if (this.b == null) {
            return true;
        }
        boolean zA = a(nqVar);
        return !zA ? !a(list, this.a) : zA;
    }

    private static boolean a(nq nqVar, long j, long j2) {
        return j > 0 && j2 - j < ((long) ((nqVar.g > 10.0f ? 1 : (nqVar.g == 10.0f ? 0 : -1)) >= 0 ? 2000 : 3500));
    }

    private boolean a(nq nqVar) {
        float f = 10.0f;
        if (nqVar.g > 10.0f) {
            f = 200.0f;
        } else if (nqVar.g > 2.0f) {
            f = 50.0f;
        }
        return nqVar.a(this.b) > ((double) f);
    }

    private static boolean a(List<nr> list, List<nr> list2) {
        if (list != null && list2 != null) {
            int size = list.size();
            int size2 = list2.size();
            int i = size + size2;
            if (size <= size2) {
                list2 = list;
                list = list2;
            }
            HashMap map = new HashMap(list.size());
            Iterator<nr> it = list.iterator();
            while (it.hasNext()) {
                map.put(Long.valueOf(it.next().a), 1);
            }
            Iterator<nr> it2 = list2.iterator();
            int i2 = 0;
            while (it2.hasNext()) {
                if (((Integer) map.get(Long.valueOf(it2.next().a))) != null) {
                    i2++;
                }
            }
            if (((double) i2) * 2.0d >= ((double) i) * 0.5d) {
                return true;
            }
        }
        return false;
    }

    private void b(List<nr> list, List<nr> list2) {
        list.clear();
        if (list2 != null) {
            List<nr> listB = b(a(list2));
            int size = listB.size();
            if (size > 40) {
                size = 40;
            }
            for (int i = 0; i < size; i++) {
                list.add(listB.get(i));
            }
        }
    }

    private static List<nr> a(List<nr> list) {
        ArrayList arrayList = new ArrayList();
        HashMap map = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            nr nrVar = list.get(i);
            map.put(Integer.valueOf(nrVar.c), nrVar);
        }
        arrayList.addAll(map.values());
        return arrayList;
    }

    private List<nr> b(List<nr> list) {
        Collections.sort(list, new Comparator<nr>() { // from class: com.amap.api.col.3sl.mm.1
            @Override // java.util.Comparator
            public final /* synthetic */ int compare(nr nrVar, nr nrVar2) {
                return a(nrVar, nrVar2);
            }

            private static int a(nr nrVar, nr nrVar2) {
                return nrVar2.c - nrVar.c;
            }
        });
        return list;
    }
}
