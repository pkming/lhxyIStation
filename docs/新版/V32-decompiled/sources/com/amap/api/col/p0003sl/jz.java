package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.col.p0003sl.is;
import com.amap.api.col.p0003sl.ka;
import java.util.List;

/* JADX INFO: compiled from: MsgProcessorDelegate.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jz {
    private Context a;
    private is b;
    private boolean c = true;
    private String d = "40C27E38DCAD404B5465362914090908";
    private kc e = new kc(this.d);

    public final void a(Context context, boolean z, String str, String str2, String str3, String[] strArr) {
        try {
            is isVarA = new is.a(str, str2, str).a(strArr).a(str3).a();
            if (context != null) {
                Context applicationContext = context.getApplicationContext();
                this.a = applicationContext;
                this.b = isVarA;
                this.c = z;
                this.e.a(applicationContext, isVarA);
            }
        } catch (Cif unused) {
        }
    }

    public final void a(String str, String str2) {
        List<is> listA = this.e.a(this.a);
        ka kaVar = ka.a.a;
        ka.a(this.a, str, str2, listA, this.c, this.b);
    }
}
