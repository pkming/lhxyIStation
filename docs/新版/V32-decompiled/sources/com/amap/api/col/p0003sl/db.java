package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.ip;
import com.amap.api.col.p0003sl.lb;
import com.amap.api.maps.MapsInitializer;
import java.util.Map;

/* JADX INFO: compiled from: AbstractAMapRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class db extends lb {
    protected boolean isPostFlag = true;

    @Override // com.amap.api.col.p0003sl.lb
    public Map<String, String> getParams() {
        return null;
    }

    protected lc makeHttpRequestNeedHeader() throws Cif {
        if (ab.a != null && ip.a(ab.a, dx.a()).a != ip.c.SuccessCode) {
            return null;
        }
        setHttpProtocol(MapsInitializer.getProtocol() == 1 ? lb.c.HTTP : lb.c.HTTPS);
        la.c();
        if (this.isPostFlag) {
            return la.a(this);
        }
        return la.e(this);
    }

    protected byte[] makeHttpRequest() throws Cif {
        lc lcVarMakeHttpRequestNeedHeader = makeHttpRequestNeedHeader();
        if (lcVarMakeHttpRequestNeedHeader != null) {
            return lcVarMakeHttpRequestNeedHeader.a;
        }
        return null;
    }

    public byte[] makeHttpRequestWithInterrupted() throws Cif {
        setDegradeAbility(lb.a.INTERRUPT_IO);
        return makeHttpRequest();
    }
}
