package com.amap.api.col.p0003sl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.LBSTraceClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: compiled from: TraceResultPool.java */
/* JADX INFO: loaded from: classes2.dex */
public class id {
    private static volatile id b;
    private Map<String, a> a;

    public id() {
        this.a = null;
        this.a = Collections.synchronizedMap(new HashMap());
    }

    public static id a() {
        if (b == null) {
            synchronized (id.class) {
                if (b == null) {
                    b = new id();
                }
            }
        }
        return b;
    }

    public final synchronized void a(String str, int i, List<LatLng> list) {
        Map<String, a> map = this.a;
        if (map != null) {
            map.get(str).a().put(Integer.valueOf(i), list);
        }
    }

    public final synchronized void a(String str, int i, int i2, int i3) {
        Map<String, a> map = this.a;
        if (map != null) {
            map.put(str, new a(i, i2, i3, new HashMap(16)));
        }
    }

    public final synchronized a a(String str) {
        Map<String, a> map = this.a;
        if (map == null) {
            return null;
        }
        return map.get(str);
    }

    public static void a(Handler handler, int i, String str) {
        Message messageObtainMessage = handler.obtainMessage();
        messageObtainMessage.obj = str;
        messageObtainMessage.what = 102;
        Bundle bundle = new Bundle();
        bundle.putInt("lineID", i);
        messageObtainMessage.setData(bundle);
        handler.sendMessage(messageObtainMessage);
    }

    /* JADX INFO: compiled from: TraceResultPool.java */
    class a {
        private int b;
        private int c;
        private int e;
        private HashMap<Integer, List<LatLng>> g;
        private int d = 0;
        private int f = 0;
        private List<LatLng> h = new ArrayList();

        public a(int i, int i2, int i3, HashMap<Integer, List<LatLng>> map) {
            this.b = 0;
            this.c = 0;
            this.e = 0;
            this.b = i2;
            this.g = map;
            this.c = i;
            this.e = i3;
        }

        public final HashMap<Integer, List<LatLng>> a() {
            return this.g;
        }

        public final void a(Handler handler) {
            List<LatLng> list;
            for (int i = this.d; i <= this.b && (list = this.g.get(Integer.valueOf(i))) != null; i++) {
                this.h.addAll(list);
                a(handler, list);
            }
            if (this.d == this.b + 1) {
                b(handler);
            }
        }

        private void a(Handler handler, List<LatLng> list) {
            Message messageObtainMessage = handler.obtainMessage();
            messageObtainMessage.obj = list;
            messageObtainMessage.what = 100;
            messageObtainMessage.arg1 = this.d;
            Bundle bundle = new Bundle();
            bundle.putInt("lineID", this.c);
            messageObtainMessage.setData(bundle);
            handler.sendMessage(messageObtainMessage);
            this.d++;
            this.f++;
        }

        private void b(Handler handler) {
            if (this.f > 0) {
                int iA = ia.a(this.h);
                Message messageObtainMessage = handler.obtainMessage();
                messageObtainMessage.obj = this.h;
                messageObtainMessage.what = 101;
                messageObtainMessage.arg1 = iA;
                messageObtainMessage.arg2 = this.e;
                Bundle bundle = new Bundle();
                bundle.putInt("lineID", this.c);
                messageObtainMessage.setData(bundle);
                handler.sendMessage(messageObtainMessage);
                return;
            }
            id.a(handler, this.c, LBSTraceClient.MIN_GRASP_POINT_ERROR);
        }
    }
}
