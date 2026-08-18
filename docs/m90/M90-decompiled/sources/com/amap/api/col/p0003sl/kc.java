package com.amap.api.col.p0003sl;

import android.app.backup.FullBackup;
import android.content.Context;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.is;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: SDKSPUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class kc {
    private is a;

    public kc(String str) {
        this.a = null;
        try {
            this.a = new is.a(str, "1.0", "1.0.0").a(new String[]{DocumentsContract.EXTRA_INFO}).a();
        } catch (Cif unused) {
        }
    }

    public final void a(Context context, is isVar) {
        JSONArray jSONArray;
        if (isVar == null) {
            return;
        }
        ArrayList<is> arrayList = new ArrayList();
        arrayList.add(isVar);
        if (arrayList.size() == 0) {
            jSONArray = new JSONArray();
        } else {
            jSONArray = new JSONArray();
            for (is isVar2 : arrayList) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put(FullBackup.APK_TREE_TOKEN, isVar2.a());
                    jSONObject.put("b", isVar2.b());
                    jSONObject.put(FullBackup.CACHE_TREE_TOKEN, isVar2.c());
                    JSONArray jSONArray2 = new JSONArray();
                    for (int i = 0; isVar2.f() != null && i < isVar2.f().length; i++) {
                        jSONArray2.put(isVar2.f()[i]);
                    }
                    jSONObject.put("d", jSONArray2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jSONArray.put(jSONObject);
            }
        }
        String string = jSONArray.toString();
        if (TextUtils.isEmpty(string)) {
            return;
        }
        kb.a(context, this.a, "rbck", string);
    }

    public final List<is> a(Context context) {
        try {
            JSONArray jSONArray = new JSONArray(kb.a(context, this.a, "rbck"));
            if (jSONArray.length() == 0) {
                return new ArrayList();
            }
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < jSONArray.length(); i++) {
                is isVarA = null;
                try {
                    isVarA = a(jSONArray.getJSONObject(i));
                } catch (JSONException unused) {
                }
                if (isVarA != null) {
                    arrayList.add(isVarA);
                }
            }
            return arrayList;
        } catch (JSONException unused2) {
            return new ArrayList();
        }
    }

    private static is a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        try {
            String strOptString = jSONObject.optString(FullBackup.APK_TREE_TOKEN);
            String strOptString2 = jSONObject.optString("b");
            String strOptString3 = jSONObject.optString(FullBackup.CACHE_TREE_TOKEN);
            ArrayList arrayList = new ArrayList();
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("d");
            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                arrayList.add(jSONArrayOptJSONArray.getString(i));
            }
            return new is.a(strOptString, strOptString2, strOptString).a(strOptString3).a((String[]) arrayList.toArray(new String[0])).a();
        } catch (Throwable unused) {
            return null;
        }
    }
}
