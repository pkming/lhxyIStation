package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.nearby.UploadInfo;

/* JADX INFO: compiled from: NearbyUpdateHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gd extends fh<UploadInfo, Integer> {
    private Context g;
    private UploadInfo h;

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return f();
    }

    public gd(Context context, UploadInfo uploadInfo) {
        super(context, uploadInfo);
        this.g = context;
        this.h = uploadInfo;
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.g));
        stringBuffer.append("&userid=").append(this.h.getUserID());
        LatLonPoint point = this.h.getPoint();
        stringBuffer.append("&location=").append(((int) (point.getLongitude() * 1000000.0d)) / 1000000.0f).append(",").append(((int) (point.getLatitude() * 1000000.0d)) / 1000000.0f);
        stringBuffer.append("&coordtype=").append(this.h.getCoordType());
        return stringBuffer.toString();
    }

    private static Integer f() throws AMapException {
        return 0;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.d() + "/nearby/data/create";
    }
}
