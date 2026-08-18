package com.amap.api.col.p0003sl;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.interfaces.ICloudSearch;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: compiled from: CloudSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hk implements ICloudSearch {
    private Context a;
    private CloudSearch.OnCloudSearchListener b;
    private CloudSearch.Query c;
    private int d;
    private HashMap<Integer, CloudResult> e;
    private Handler f;

    public hk(Context context) throws AMapException {
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.a = context.getApplicationContext();
        this.f = ga.a();
    }

    @Override // com.amap.api.services.interfaces.ICloudSearch
    public final void setOnCloudSearchListener(CloudSearch.OnCloudSearchListener onCloudSearchListener) {
        this.b = onCloudSearchListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v2 */
    /* JADX WARN: Type inference failed for: r0v3 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7 */
    /* JADX WARN: Type inference failed for: r1v13, types: [com.amap.api.services.cloud.CloudResult] */
    /* JADX WARN: Type inference failed for: r1v2, types: [com.amap.api.services.cloud.CloudResult] */
    /* JADX WARN: Type inference failed for: r1v21 */
    /* JADX WARN: Type inference failed for: r1v22 */
    /* JADX WARN: Type inference failed for: r1v7, types: [int] */
    /* JADX WARN: Type inference failed for: r1v8 */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public CloudResult a(CloudSearch.Query query) throws AMapException {
        ?? r0;
        ?? r02 = 0;
        try {
        } catch (Throwable th) {
            th = th;
        }
        if (!b(query)) {
            throw new AMapException("无效的参数 - IllegalArgumentException");
        }
        if (!query.queryEquals(this.c)) {
            this.d = 0;
            this.c = query.m49clone();
            HashMap<Integer, CloudResult> map = this.e;
            if (map != null) {
                map.clear();
            }
        }
        ?? r1 = this.d;
        try {
        } catch (Throwable th2) {
            th = th2;
            r02 = r1;
            fp.a(th, "CloudSearch", "searchCloud");
            if (th instanceof AMapException) {
                throw th;
            }
            th.printStackTrace();
            r0 = r02;
        }
        if (r1 == 0) {
            CloudResult cloudResultD = new fn(this.a, query).d();
            a(cloudResultD, query);
            r1 = cloudResultD;
        } else {
            CloudResult cloudResultA = a(query.getPageNum());
            r0 = cloudResultA;
            if (cloudResultA == null) {
                CloudResult cloudResultD2 = new fn(this.a, query).d();
                this.e.put(Integer.valueOf(query.getPageNum()), cloudResultD2);
                r1 = cloudResultD2;
            }
            return r0;
        }
        return r1;
    }

    @Override // com.amap.api.services.interfaces.ICloudSearch
    public final void searchCloudAsyn(final CloudSearch.Query query) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hk.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    try {
                        try {
                            messageObtainMessage.arg1 = 12;
                            messageObtainMessage.what = 700;
                            ga.e eVar = new ga.e();
                            eVar.b = hk.this.b;
                            messageObtainMessage.obj = eVar;
                            eVar.a = hk.this.a(query);
                            messageObtainMessage.arg2 = 1000;
                        } catch (AMapException e) {
                            messageObtainMessage.arg2 = e.getErrorCode();
                        }
                    } finally {
                        hk.this.f.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CloudItemDetail a(String str, String str2) throws AMapException {
        if (str == null || str.trim().equals("")) {
            throw new AMapException("无效的参数 - IllegalArgumentException");
        }
        if (str2 == null || str2.trim().equals("")) {
            throw new AMapException("无效的参数 - IllegalArgumentException");
        }
        try {
            return new fm(this.a, new gk(str, str2)).d();
        } catch (Throwable th) {
            fp.a(th, "CloudSearch", "searchCloudDetail");
            if (th instanceof AMapException) {
                throw th;
            }
            th.printStackTrace();
            return null;
        }
    }

    @Override // com.amap.api.services.interfaces.ICloudSearch
    public final void searchCloudDetailAsyn(final String str, final String str2) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hk.2
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    try {
                        try {
                            messageObtainMessage.arg1 = 12;
                            messageObtainMessage.what = MediaPlayer.MEDIA_INFO_BUFFERING_START;
                            ga.d dVar = new ga.d();
                            dVar.b = hk.this.b;
                            messageObtainMessage.obj = dVar;
                            dVar.a = hk.this.a(str, str2);
                            messageObtainMessage.arg2 = 1000;
                        } catch (AMapException e) {
                            messageObtainMessage.arg2 = e.getErrorCode();
                        }
                    } finally {
                        hk.this.f.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void a(CloudResult cloudResult, CloudSearch.Query query) {
        HashMap<Integer, CloudResult> map = new HashMap<>();
        this.e = map;
        if (this.d > 0) {
            map.put(Integer.valueOf(query.getPageNum()), cloudResult);
        }
    }

    private CloudResult a(int i) {
        if (!b(i)) {
            throw new IllegalArgumentException("page out of range");
        }
        return this.e.get(Integer.valueOf(i));
    }

    private boolean b(int i) {
        return i <= this.d && i > 0;
    }

    private static boolean b(CloudSearch.Query query) {
        if (query == null || fp.a(query.getTableID()) || query.getBound() == null) {
            return false;
        }
        if (query.getBound() != null && query.getBound().getShape().equals("Bound") && query.getBound().getCenter() == null) {
            return false;
        }
        if (query.getBound() != null && query.getBound().getShape().equals("Rectangle")) {
            LatLonPoint lowerLeft = query.getBound().getLowerLeft();
            LatLonPoint upperRight = query.getBound().getUpperRight();
            if (lowerLeft == null || upperRight == null || lowerLeft.getLatitude() >= upperRight.getLatitude() || lowerLeft.getLongitude() >= upperRight.getLongitude()) {
                return false;
            }
        }
        if (query.getBound() == null || !query.getBound().getShape().equals("Polygon")) {
            return true;
        }
        List<LatLonPoint> polyGonList = query.getBound().getPolyGonList();
        for (int i = 0; i < polyGonList.size(); i++) {
            if (polyGonList.get(i) == null) {
                return false;
            }
        }
        return true;
    }
}
