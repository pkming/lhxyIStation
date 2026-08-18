package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: compiled from: OfflineMapDownloadList.java */
/* JADX INFO: loaded from: classes2.dex */
public final class bc {
    public ArrayList<OfflineMapProvince> a = new ArrayList<>();
    private bn b;
    private Context c;

    private static boolean a(int i) {
        return i == 4;
    }

    private static boolean a(int i, int i2) {
        return i2 != 1 || i <= 2 || i >= 98;
    }

    private static boolean b(int i) {
        return i == 0 || i == 2 || i == 3 || i == 1 || i == 102 || i == 101 || i == 103 || i == -1;
    }

    public bc(Context context) {
        this.c = context;
        this.b = bn.a(context);
    }

    private void a(bi biVar) {
        bn bnVar = this.b;
        if (bnVar == null || biVar == null) {
            return;
        }
        bnVar.a(biVar);
    }

    private void b(bi biVar) {
        bn bnVar = this.b;
        if (bnVar != null) {
            bnVar.b(biVar);
        }
    }

    private static boolean a(OfflineMapProvince offlineMapProvince) {
        if (offlineMapProvince == null) {
            return false;
        }
        Iterator<OfflineMapCity> it = offlineMapProvince.getCityList().iterator();
        while (it.hasNext()) {
            if (it.next().getState() != 4) {
                return false;
            }
        }
        return true;
    }

    public final ArrayList<OfflineMapProvince> a() {
        ArrayList<OfflineMapProvince> arrayList = new ArrayList<>();
        synchronized (this.a) {
            Iterator<OfflineMapProvince> it = this.a.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
        }
        return arrayList;
    }

    public final OfflineMapCity a(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        synchronized (this.a) {
            Iterator<OfflineMapProvince> it = this.a.iterator();
            while (it.hasNext()) {
                for (OfflineMapCity offlineMapCity : it.next().getCityList()) {
                    if (offlineMapCity.getCode().equals(str)) {
                        return offlineMapCity;
                    }
                }
            }
            return null;
        }
    }

    public final OfflineMapCity b(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        synchronized (this.a) {
            Iterator<OfflineMapProvince> it = this.a.iterator();
            while (it.hasNext()) {
                for (OfflineMapCity offlineMapCity : it.next().getCityList()) {
                    if (offlineMapCity.getCity().trim().equalsIgnoreCase(str.trim())) {
                        return offlineMapCity;
                    }
                }
            }
            return null;
        }
    }

    public final OfflineMapProvince c(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        synchronized (this.a) {
            for (OfflineMapProvince offlineMapProvince : this.a) {
                if (offlineMapProvince.getProvinceName().trim().equalsIgnoreCase(str.trim())) {
                    return offlineMapProvince;
                }
            }
            return null;
        }
    }

    public final ArrayList<OfflineMapCity> b() {
        ArrayList<OfflineMapCity> arrayList = new ArrayList<>();
        synchronized (this.a) {
            Iterator<OfflineMapProvince> it = this.a.iterator();
            while (it.hasNext()) {
                Iterator<OfflineMapCity> it2 = it.next().getCityList().iterator();
                while (it2.hasNext()) {
                    arrayList.add(it2.next());
                }
            }
        }
        return arrayList;
    }

    public final void a(List<OfflineMapProvince> list) {
        OfflineMapProvince next;
        OfflineMapCity next2;
        synchronized (this.a) {
            if (this.a.size() > 0) {
                for (int i = 0; i < this.a.size(); i++) {
                    OfflineMapProvince offlineMapProvince = this.a.get(i);
                    Iterator<OfflineMapProvince> it = list.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            next = null;
                            break;
                        }
                        next = it.next();
                        if (offlineMapProvince.getPinyin().equals(next.getPinyin())) {
                            break;
                        }
                        if (offlineMapProvince.getPinyin().equals("quanguogaiyaotu") || offlineMapProvince.getProvinceCode().equals("000001") || offlineMapProvince.getProvinceCode().equals("100000")) {
                            if (next.getPinyin().equals("quanguogaiyaotu")) {
                                break;
                            }
                        }
                    }
                    if (next != null) {
                        a(offlineMapProvince, next);
                        ArrayList<OfflineMapCity> cityList = offlineMapProvince.getCityList();
                        ArrayList<OfflineMapCity> cityList2 = next.getCityList();
                        for (int i2 = 0; i2 < cityList.size(); i2++) {
                            OfflineMapCity offlineMapCity = cityList.get(i2);
                            Iterator<OfflineMapCity> it2 = cityList2.iterator();
                            while (true) {
                                if (it2.hasNext()) {
                                    next2 = it2.next();
                                    if (offlineMapCity.getPinyin().equals(next2.getPinyin())) {
                                        break;
                                    }
                                } else {
                                    next2 = null;
                                    break;
                                }
                            }
                            if (next2 != null) {
                                a(offlineMapCity, next2);
                            }
                        }
                    }
                }
            } else {
                Iterator<OfflineMapProvince> it3 = list.iterator();
                while (it3.hasNext()) {
                    this.a.add(it3.next());
                }
            }
        }
    }

    private static void a(OfflineMapCity offlineMapCity, OfflineMapCity offlineMapCity2) {
        offlineMapCity.setUrl(offlineMapCity2.getUrl());
        offlineMapCity.setVersion(offlineMapCity2.getVersion());
        offlineMapCity.setSize(offlineMapCity2.getSize());
        offlineMapCity.setCode(offlineMapCity2.getCode());
        offlineMapCity.setPinyin(offlineMapCity2.getPinyin());
        offlineMapCity.setJianpin(offlineMapCity2.getJianpin());
    }

    private static void a(OfflineMapProvince offlineMapProvince, OfflineMapProvince offlineMapProvince2) {
        offlineMapProvince.setUrl(offlineMapProvince2.getUrl());
        offlineMapProvince.setVersion(offlineMapProvince2.getVersion());
        offlineMapProvince.setSize(offlineMapProvince2.getSize());
        offlineMapProvince.setPinyin(offlineMapProvince2.getPinyin());
        offlineMapProvince.setJianpin(offlineMapProvince2.getJianpin());
    }

    public final ArrayList<OfflineMapCity> c() {
        ArrayList<OfflineMapCity> arrayList;
        synchronized (this.a) {
            arrayList = new ArrayList<>();
            for (OfflineMapProvince offlineMapProvince : this.a) {
                if (offlineMapProvince != null) {
                    for (OfflineMapCity offlineMapCity : offlineMapProvince.getCityList()) {
                        if (offlineMapCity.getState() == 4 || offlineMapCity.getState() == 7) {
                            arrayList.add(offlineMapCity);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public final ArrayList<OfflineMapProvince> d() {
        ArrayList<OfflineMapProvince> arrayList;
        synchronized (this.a) {
            arrayList = new ArrayList<>();
            for (OfflineMapProvince offlineMapProvince : this.a) {
                if (offlineMapProvince != null && (offlineMapProvince.getState() == 4 || offlineMapProvince.getState() == 7)) {
                    arrayList.add(offlineMapProvince);
                }
            }
        }
        return arrayList;
    }

    public final ArrayList<OfflineMapCity> e() {
        ArrayList<OfflineMapCity> arrayList;
        synchronized (this.a) {
            arrayList = new ArrayList<>();
            for (OfflineMapProvince offlineMapProvince : this.a) {
                if (offlineMapProvince != null) {
                    for (OfflineMapCity offlineMapCity : offlineMapProvince.getCityList()) {
                        if (b(offlineMapCity.getState())) {
                            arrayList.add(offlineMapCity);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public final ArrayList<OfflineMapProvince> f() {
        ArrayList<OfflineMapProvince> arrayList;
        synchronized (this.a) {
            arrayList = new ArrayList<>();
            for (OfflineMapProvince offlineMapProvince : this.a) {
                if (offlineMapProvince != null && b(offlineMapProvince.getState())) {
                    arrayList.add(offlineMapProvince);
                }
            }
        }
        return arrayList;
    }

    public final void a(ax axVar) {
        String pinyin = axVar.getPinyin();
        synchronized (this.a) {
            Iterator<OfflineMapProvince> it = this.a.iterator();
            loop0: while (true) {
                if (!it.hasNext()) {
                    break;
                }
                OfflineMapProvince next = it.next();
                if (next != null) {
                    for (OfflineMapCity offlineMapCity : next.getCityList()) {
                        if (offlineMapCity.getPinyin().trim().equals(pinyin.trim())) {
                            a(axVar, offlineMapCity);
                            a(axVar, next);
                            break loop0;
                        }
                    }
                }
            }
        }
    }

    private void a(ax axVar, OfflineMapCity offlineMapCity) {
        int iB = axVar.c().b();
        if (axVar.c().equals(axVar.a)) {
            b(axVar.t());
        } else {
            if (axVar.c().equals(axVar.f)) {
                new StringBuilder("saveJSONObjectToFile  CITY ").append(axVar.getCity());
                b(axVar);
                axVar.t().b();
            }
            if (a(axVar.getcompleteCode(), axVar.c().b())) {
                a(axVar.t());
            }
        }
        offlineMapCity.setState(iB);
        offlineMapCity.setCompleteCode(axVar.getcompleteCode());
    }

    private void b(ax axVar) {
        File[] fileArrListFiles = new File(dx.c(this.c)).listFiles();
        if (fileArrListFiles == null) {
            return;
        }
        for (File file : fileArrListFiles) {
            if (file.isFile() && file.exists() && file.getName().contains(axVar.getAdcode()) && file.getName().endsWith(".zip.tmp.dt")) {
                file.delete();
            }
        }
    }

    private void a(ax axVar, OfflineMapProvince offlineMapProvince) {
        bi biVar;
        int iB = axVar.c().b();
        if (iB == 6) {
            offlineMapProvince.setState(iB);
            offlineMapProvince.setCompleteCode(0);
            b(new bi(offlineMapProvince, this.c));
            try {
                bv.b(offlineMapProvince.getProvinceCode(), this.c);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (Exception e2) {
                e2.printStackTrace();
                return;
            }
        }
        if (a(iB) && a(offlineMapProvince)) {
            if (axVar.getPinyin().equals(offlineMapProvince.getPinyin())) {
                offlineMapProvince.setState(iB);
                offlineMapProvince.setCompleteCode(axVar.getcompleteCode());
                offlineMapProvince.setVersion(axVar.getVersion());
                offlineMapProvince.setUrl(axVar.getUrl());
                biVar = new bi(offlineMapProvince, this.c);
                biVar.a(axVar.a());
                biVar.d(axVar.getCode());
            } else {
                offlineMapProvince.setState(iB);
                offlineMapProvince.setCompleteCode(100);
                biVar = new bi(offlineMapProvince, this.c);
            }
            biVar.b();
            a(biVar);
            new StringBuilder("saveJSONObjectToFile  province ").append(biVar.c());
        }
    }

    public final void g() {
        h();
        this.b = null;
        this.c = null;
    }

    private void h() {
        ArrayList<OfflineMapProvince> arrayList = this.a;
        if (arrayList != null) {
            synchronized (arrayList) {
                this.a.clear();
            }
        }
    }
}
