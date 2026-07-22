package com.amap.api.col.p0003sl;

import android.app.Activity;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.maps.offlinemap.OfflineMapActivity;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: compiled from: SearchListAdapter.java */
/* JADX INFO: loaded from: classes2.dex */
public final class eu extends BaseAdapter {
    private List<OfflineMapCity> a = new ArrayList();
    private OfflineMapManager b;
    private Activity c;

    @Override // android.widget.Adapter
    public final long getItemId(int i) {
        return i;
    }

    public eu(OfflineMapManager offlineMapManager, OfflineMapActivity offlineMapActivity) {
        this.b = offlineMapManager;
        this.c = offlineMapActivity;
    }

    public final void a(List<OfflineMapCity> list) {
        this.a = list;
    }

    @Override // android.widget.Adapter
    public final int getCount() {
        return this.a.size();
    }

    @Override // android.widget.Adapter
    public final Object getItem(int i) {
        return this.a.get(i);
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x00ef A[Catch: Exception -> 0x00fc, TRY_LEAVE, TryCatch #0 {Exception -> 0x00fc, blocks: (B:2:0x0000, B:4:0x000a, B:6:0x004e, B:19:0x00ac, B:21:0x00b0, B:22:0x00bb, B:23:0x00c8, B:24:0x00d5, B:25:0x00e2, B:26:0x00ef, B:5:0x0048), top: B:31:0x0000 }] */
    @Override // android.widget.Adapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final android.view.View getView(int r7, android.view.View r8, android.view.ViewGroup r9) {
        /*
            Method dump skipped, instruction units count: 268
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.eu.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    /* JADX INFO: compiled from: SearchListAdapter.java */
    public final class a {
        public TextView a;
        public TextView b;
        public TextView c;
        public ImageView d;

        public a() {
        }
    }
}
