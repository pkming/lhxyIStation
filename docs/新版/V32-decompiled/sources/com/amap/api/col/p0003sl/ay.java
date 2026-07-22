package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.json.JSONException;

/* JADX INFO: compiled from: OfflineDownloadManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class ay {
    public static String a = "";
    public static boolean b = false;
    public static String d = "";
    private static volatile ay k;
    public bc f;
    be g;
    private Context i;
    private a l;
    private bh m;
    private bn n;
    private boolean j = true;
    List<ax> c = new Vector();
    private mc o = null;
    private mc p = null;
    private mc q = null;
    b e = null;
    bb h = null;
    private boolean r = true;

    /* JADX INFO: compiled from: OfflineDownloadManager.java */
    public interface a {
        void a();

        void a(ax axVar);

        void b(ax axVar);

        void c(ax axVar);
    }

    static /* synthetic */ boolean f(ay ayVar) {
        ayVar.j = false;
        return false;
    }

    private ay(Context context) {
        this.i = context;
    }

    public static ay a(Context context) {
        if (k == null) {
            synchronized (ay.class) {
                if (k == null && !b) {
                    k = new ay(context.getApplicationContext());
                }
            }
        }
        return k;
    }

    public final void a() {
        this.n = bn.a(this.i.getApplicationContext());
        g();
        this.e = new b(this.i.getMainLooper());
        this.f = new bc(this.i);
        this.m = bh.a();
        k(dx.c(this.i));
        try {
            h();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        synchronized (this.c) {
            Iterator<OfflineMapProvince> it = this.f.a().iterator();
            while (it.hasNext()) {
                for (OfflineMapCity offlineMapCity : it.next().getCityList()) {
                    if (offlineMapCity != null) {
                        this.c.add(new ax(this.i, offlineMapCity));
                    }
                }
            }
        }
        bb bbVar = new bb(this.i);
        this.h = bbVar;
        bbVar.start();
    }

    private void g() {
        try {
            bi biVarA = this.n.a("000001");
            if (biVarA != null) {
                this.n.c("000001");
                biVarA.c("100000");
                this.n.a(biVarA);
            }
        } catch (Throwable th) {
            jw.c(th, "OfflineDownloadManager", "changeBadCase");
        }
    }

    private void h() {
        String strC;
        if ("".equals(dx.c(this.i))) {
            return;
        }
        File file = new File(dx.c(this.i) + "offlinemapv4.png");
        if (!file.exists()) {
            strC = bv.a(this.i, "offlinemapv4.png");
        } else {
            strC = bv.c(file);
        }
        if (strC != null) {
            try {
                h(strC);
            } catch (JSONException e) {
                if (file.exists()) {
                    file.delete();
                }
                jw.c(e, "MapDownloadManager", "paseJson io");
                e.printStackTrace();
            }
        }
    }

    private void h(String str) throws JSONException {
        bc bcVar;
        List<OfflineMapProvince> listA = bv.a(str, this.i.getApplicationContext());
        if (listA == null || listA.size() == 0 || (bcVar = this.f) == null) {
            return;
        }
        bcVar.a(listA);
    }

    private void i() {
        for (bi biVar : this.n.a()) {
            if (biVar != null && biVar.c() != null && biVar.e().length() > 0) {
                if (biVar.l != 4 && biVar.l != 7 && biVar.l >= 0) {
                    biVar.l = 3;
                }
                ax axVarI = i(biVar.c());
                if (axVarI != null) {
                    String strD = biVar.d();
                    if (strD != null && b(d, strD)) {
                        axVarI.a(7);
                    } else {
                        axVarI.a(biVar.l);
                        axVarI.setCompleteCode(biVar.g());
                    }
                    if (biVar.d().length() > 0) {
                        axVarI.setVersion(biVar.d());
                    }
                    List<String> listB = this.n.b(biVar.e());
                    StringBuffer stringBuffer = new StringBuffer();
                    Iterator<String> it = listB.iterator();
                    while (it.hasNext()) {
                        stringBuffer.append(it.next());
                        stringBuffer.append(";");
                    }
                    axVarI.a(stringBuffer.toString());
                    bc bcVar = this.f;
                    if (bcVar != null) {
                        bcVar.a(axVarI);
                    }
                }
            }
        }
    }

    public final void b() {
        i();
        a aVar = this.l;
        if (aVar != null) {
            try {
                aVar.a();
            } catch (Throwable th) {
                jw.c(th, "OfflineDownloadManager", "verifyCallBack");
            }
        }
    }

    public final void a(final String str) {
        try {
            if (str == null) {
                a aVar = this.l;
                if (aVar != null) {
                    aVar.b(null);
                    return;
                }
                return;
            }
            if (this.o == null) {
                this.o = dw.a("AMapOfflineCheckUpdate");
            }
            this.o.a(new md() { // from class: com.amap.api.col.3sl.ay.1
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    ax axVarI = ay.this.i(str);
                    if (axVarI != null) {
                        try {
                            if (!axVarI.c().equals(axVarI.c) && !axVarI.c().equals(axVarI.e)) {
                                String pinyin = axVarI.getPinyin();
                                if (pinyin.length() > 0) {
                                    String strD = ay.this.n.d(pinyin);
                                    if (strD == null) {
                                        strD = axVarI.getVersion();
                                    }
                                    if (ay.d.length() > 0 && strD != null && ay.b(ay.d, strD)) {
                                        axVarI.j();
                                    }
                                }
                            }
                            if (ay.this.l != null) {
                                synchronized (ay.this) {
                                    try {
                                        ay.this.l.b(axVarI);
                                    } finally {
                                    }
                                }
                                return;
                            }
                            return;
                        } catch (Exception unused) {
                            if (ay.this.l != null) {
                                synchronized (ay.this) {
                                    try {
                                        ay.this.l.b(axVarI);
                                    } finally {
                                        return;
                                    }
                                    return;
                                }
                            }
                            return;
                        } catch (Throwable th) {
                            if (ay.this.l != null) {
                                synchronized (ay.this) {
                                    try {
                                        ay.this.l.b(axVarI);
                                    } finally {
                                        throw th;
                                    }
                                }
                            }
                            throw th;
                        }
                    }
                    ay.this.j();
                    az azVarC = new ba(ay.this.i, ay.d).c();
                    if (ay.this.l != null) {
                        if (azVarC == null) {
                            if (ay.this.l != null) {
                                synchronized (ay.this) {
                                    try {
                                        ay.this.l.b(axVarI);
                                    } finally {
                                    }
                                }
                                return;
                            }
                            return;
                        }
                        if (azVarC.a()) {
                            ay.this.c();
                        }
                    }
                    if (ay.this.l != null) {
                        synchronized (ay.this) {
                            try {
                                ay.this.l.b(axVarI);
                            } finally {
                            }
                        }
                    }
                }
            });
        } catch (Throwable th) {
            jw.c(th, "OfflineDownloadManager", "checkUpdate");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void j() throws AMapException {
        if (!dx.d(this.i)) {
            throw new AMapException(AMapException.ERROR_CONNECTION);
        }
    }

    protected final void c() throws AMapException {
        if (this.f == null) {
            return;
        }
        bf bfVar = new bf(this.i, "");
        bfVar.a(this.i);
        List<OfflineMapProvince> listC = bfVar.c();
        if (this.c != null) {
            this.f.a(listC);
        }
        List<ax> list = this.c;
        if (list != null) {
            synchronized (list) {
                Iterator<OfflineMapProvince> it = this.f.a().iterator();
                while (it.hasNext()) {
                    for (OfflineMapCity offlineMapCity : it.next().getCityList()) {
                        for (ax axVar : this.c) {
                            if (offlineMapCity.getPinyin().equals(axVar.getPinyin())) {
                                String version = axVar.getVersion();
                                if (axVar.getState() == 4 && d.length() > 0 && b(d, version)) {
                                    axVar.j();
                                    axVar.setUrl(offlineMapCity.getUrl());
                                    axVar.s();
                                } else {
                                    axVar.setCity(offlineMapCity.getCity());
                                    axVar.setUrl(offlineMapCity.getUrl());
                                    axVar.s();
                                    axVar.setAdcode(offlineMapCity.getAdcode());
                                    axVar.setVersion(offlineMapCity.getVersion());
                                    axVar.setSize(offlineMapCity.getSize());
                                    axVar.setCode(offlineMapCity.getCode());
                                    axVar.setJianpin(offlineMapCity.getJianpin());
                                    axVar.setPinyin(offlineMapCity.getPinyin());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean b(String str, String str2) {
        for (int i = 0; i < str2.length(); i++) {
            try {
                if (str.charAt(i) > str2.charAt(i)) {
                    return true;
                }
                if (str.charAt(i) < str2.charAt(i)) {
                    return false;
                }
            } catch (Throwable unused) {
            }
        }
        return false;
    }

    public final boolean b(String str) {
        return i(str) != null;
    }

    public final void c(String str) {
        ax axVarI = i(str);
        if (axVarI == null) {
            a aVar = this.l;
            if (aVar != null) {
                try {
                    aVar.c(axVarI);
                    return;
                } catch (Throwable th) {
                    jw.c(th, "OfflineDownloadManager", "remove");
                    return;
                }
            }
            return;
        }
        d(axVarI);
        a(axVarI, true);
    }

    public final void a(ax axVar) {
        a(axVar, false);
    }

    private void a(final ax axVar, final boolean z) {
        if (this.g == null) {
            this.g = new be(this.i);
        }
        if (this.p == null) {
            this.p = dw.a("AMapOfflineRemove");
        }
        try {
            this.p.a(new md() { // from class: com.amap.api.col.3sl.ay.2
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    try {
                        if (axVar.c().equals(axVar.a)) {
                            if (ay.this.l != null) {
                                ay.this.l.c(axVar);
                                return;
                            }
                            return;
                        }
                        if (axVar.getState() != 7 && axVar.getState() != -1) {
                            ay.this.g.a(axVar);
                            if (ay.this.l != null) {
                                ay.this.l.c(axVar);
                                return;
                            }
                            return;
                        }
                        ay.this.g.a(axVar);
                        if (!z || ay.this.l == null) {
                            return;
                        }
                        ay.this.l.c(axVar);
                    } catch (Throwable th) {
                        jw.c(th, "requestDelete", "removeExcecRunnable");
                    }
                }
            });
        } catch (Throwable th) {
            jw.c(th, "requestDelete", "removeExcecRunnable");
        }
    }

    public final void b(ax axVar) {
        try {
            bh bhVar = this.m;
            if (bhVar != null) {
                bhVar.a(axVar, this.i);
            }
        } catch (Cif e) {
            e.printStackTrace();
        }
    }

    public final void c(ax axVar) {
        bc bcVar = this.f;
        if (bcVar != null) {
            bcVar.a(axVar);
        }
        b bVar = this.e;
        if (bVar != null) {
            Message messageObtainMessage = bVar.obtainMessage();
            messageObtainMessage.obj = axVar;
            this.e.sendMessage(messageObtainMessage);
        }
    }

    public final void d() {
        synchronized (this.c) {
            for (ax axVar : this.c) {
                if (axVar.c().equals(axVar.c) || axVar.c().equals(axVar.b)) {
                    d(axVar);
                    axVar.g();
                }
            }
        }
    }

    public final void e() {
        synchronized (this.c) {
            Iterator<ax> it = this.c.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ax next = it.next();
                if (next.c().equals(next.c)) {
                    next.g();
                    break;
                }
            }
        }
    }

    public final void d(String str) {
        ax axVarI = i(str);
        if (axVarI != null) {
            axVarI.f();
        }
    }

    public final void f() {
        mc mcVar = this.o;
        if (mcVar != null) {
            mcVar.e();
        }
        mc mcVar2 = this.q;
        if (mcVar2 != null) {
            mcVar2.e();
            this.q = null;
        }
        bb bbVar = this.h;
        if (bbVar != null) {
            if (bbVar.isAlive()) {
                this.h.interrupt();
            }
            this.h = null;
        }
        b bVar = this.e;
        if (bVar != null) {
            bVar.removeCallbacksAndMessages(null);
            this.e = null;
        }
        bh bhVar = this.m;
        if (bhVar != null) {
            bhVar.b();
            this.m = null;
        }
        bc bcVar = this.f;
        if (bcVar != null) {
            bcVar.g();
        }
        k();
        this.j = true;
        l();
    }

    private static void k() {
        k = null;
        b = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ax i(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        synchronized (this.c) {
            for (ax axVar : this.c) {
                if (str.equals(axVar.getCity()) || str.equals(axVar.getPinyin())) {
                    return axVar;
                }
            }
            return null;
        }
    }

    private ax j(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        synchronized (this.c) {
            for (ax axVar : this.c) {
                if (str.equals(axVar.getCode())) {
                    return axVar;
                }
            }
            return null;
        }
    }

    public final void e(String str) throws AMapException {
        ax axVarI = i(str);
        if (str == null || str.length() <= 0 || axVarI == null) {
            throw new AMapException("无效的参数 - IllegalArgumentException");
        }
        f(axVarI);
    }

    public final void f(String str) throws AMapException {
        ax axVarJ = j(str);
        if (axVarJ != null) {
            f(axVarJ);
            return;
        }
        throw new AMapException("无效的参数 - IllegalArgumentException");
    }

    private void f(final ax axVar) throws AMapException {
        j();
        if (axVar == null) {
            throw new AMapException("无效的参数 - IllegalArgumentException");
        }
        if (this.q == null) {
            this.q = dw.a("AMapOfflineDownload");
        }
        try {
            this.q.a(new md() { // from class: com.amap.api.col.3sl.ay.3
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    try {
                        if (ay.this.j) {
                            ay.this.j();
                            az azVarC = new ba(ay.this.i, ay.d).c();
                            if (azVarC != null) {
                                ay.f(ay.this);
                                if (azVarC.a()) {
                                    ay.this.c();
                                }
                            }
                        }
                        axVar.setVersion(ay.d);
                        axVar.f();
                    } catch (AMapException e) {
                        e.printStackTrace();
                    } catch (Throwable th) {
                        jw.c(th, "OfflineDownloadManager", "startDownloadRunnable");
                    }
                }
            });
        } catch (Throwable th) {
            jw.c(th, "startDownload", "downloadExcecRunnable");
        }
    }

    public final void d(ax axVar) {
        bh bhVar = this.m;
        if (bhVar != null) {
            bhVar.a(axVar);
        }
    }

    public final void e(ax axVar) {
        bh bhVar = this.m;
        if (bhVar != null) {
            bhVar.b(axVar);
        }
    }

    public final void a(a aVar) {
        this.l = aVar;
    }

    private void l() {
        synchronized (this) {
            this.l = null;
        }
    }

    /* JADX INFO: compiled from: OfflineDownloadManager.java */
    class b extends Handler {
        public b(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            try {
                message.getData();
                Object obj = message.obj;
                if (obj instanceof ax) {
                    ax axVar = (ax) obj;
                    new StringBuilder("OfflineMapHandler handleMessage CitObj  name: ").append(axVar.getCity()).append(" complete: ").append(axVar.getcompleteCode()).append(" status: ").append(axVar.getState());
                    if (ay.this.l != null) {
                        ay.this.l.a(axVar);
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public final String g(String str) {
        ax axVarI;
        return (str == null || (axVarI = i(str)) == null) ? "" : axVarI.getAdcode();
    }

    private static void k(String str) {
        a = str;
    }
}
