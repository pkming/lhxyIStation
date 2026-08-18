package com.unisound.common;

import java.io.File;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
final class s implements Runnable {
    final /* synthetic */ String a;

    s(String str) {
        this.a = str;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        String strB;
        synchronized (r.M) {
            try {
                boolean unused = r.K = true;
            } catch (Throwable th) {
                th = th;
            }
            try {
                File file = new File(this.a);
                if (file.exists() && file.isDirectory()) {
                    File[] fileArrListFiles = file.listFiles();
                    if (fileArrListFiles.length > 0) {
                        for (int i = 0; i < fileArrListFiles.length; i++) {
                            if (r.K && (strB = r.b(fileArrListFiles[i])) != null) {
                                try {
                                    JSONObject jSONObject = new JSONObject(strB);
                                    if (new p().a(new q(jSONObject.getString(q.a), jSONObject.getString(q.b), jSONObject.getString(q.c), jSONObject.getString(q.e), jSONObject.getString("packageName"), jSONObject.getInt("status"), jSONObject.getString(q.h), jSONObject.getString(q.i), jSONObject.getString(q.j))) == 0) {
                                        int unused2 = r.L = 0;
                                        fileArrListFiles[i].delete();
                                        boolean unused3 = r.K = true;
                                    } else {
                                        r.d();
                                        r.e("postLogError " + r.L + " times");
                                        boolean unused4 = r.K = false;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }
}
