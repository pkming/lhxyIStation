package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.col.p0003sl.ar;

/* JADX INFO: compiled from: ScaleRotateGestureDetector.java */
/* JADX INFO: loaded from: classes2.dex */
public final class as extends ar {

    /* JADX INFO: compiled from: ScaleRotateGestureDetector.java */
    public static abstract class a implements ar.a {
        public abstract boolean a(as asVar);

        public abstract boolean b(as asVar);

        public abstract void c(as asVar);

        @Override // com.amap.api.col.3sl.ar.a
        public final boolean a(ar arVar) {
            return a((as) arVar);
        }

        @Override // com.amap.api.col.3sl.ar.a
        public final boolean b(ar arVar) {
            return b((as) arVar);
        }

        @Override // com.amap.api.col.3sl.ar.a
        public final void c(ar arVar) {
            c((as) arVar);
        }
    }

    public as(Context context, a aVar) {
        super(context, aVar);
    }

    public final float j() {
        return (float) (((Math.atan2(g(), f()) - Math.atan2(e(), d())) * 180.0d) / 3.141592653589793d);
    }
}
