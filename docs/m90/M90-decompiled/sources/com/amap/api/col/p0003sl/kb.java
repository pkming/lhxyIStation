package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import java.util.HashMap;

/* JADX INFO: compiled from: SPConfig.java */
/* JADX INFO: loaded from: classes2.dex */
public final class kb {
    private static HashMap<String, String> a = new HashMap<>();

    public static void a(Context context, is isVar, String str, String str2) {
        if (isVar == null || TextUtils.isEmpty(isVar.a())) {
            return;
        }
        String str3 = str + isVar.a();
        a.put(isVar.a() + str, str2);
        if (context == null || TextUtils.isEmpty(str3) || TextUtils.isEmpty("d7afbc6a38848a6801f6e449f3ec8e53") || TextUtils.isEmpty(str2)) {
            return;
        }
        String strG = it.g(ie.a(it.a(str2)));
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("d7afbc6a38848a6801f6e449f3ec8e53", 0).edit();
        editorEdit.putString(str3, strG);
        editorEdit.commit();
    }

    public static String a(Context context, is isVar, String str) {
        if (isVar == null || TextUtils.isEmpty(isVar.a())) {
            return null;
        }
        String str2 = a.get(isVar.a() + str);
        if (!TextUtils.isEmpty(str2)) {
            return str2;
        }
        String str3 = str + isVar.a();
        return (context == null || TextUtils.isEmpty(str3)) ? "" : it.a(ie.b(it.d(context.getSharedPreferences("d7afbc6a38848a6801f6e449f3ec8e53", 0).getString(str3, ""))));
    }
}
