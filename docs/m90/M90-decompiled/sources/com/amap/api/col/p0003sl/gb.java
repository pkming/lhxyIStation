package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;

/* JADX INFO: compiled from: NearbyDeleteHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gb extends fh<String, Integer> {
    private Context g;
    private String h;

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return f();
    }

    public gb(Context context, String str) {
        super(context, str);
        this.g = context;
        this.h = str;
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.g));
        stringBuffer.append("&userid=").append(this.h);
        return stringBuffer.toString();
    }

    private static Integer f() throws AMapException {
        return 0;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.d() + "/nearby/data/delete";
    }
}
