package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.List;

/* JADX INFO: compiled from: AdiuStorageModel.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jj {
    public static final String a = it.c("SU2hhcmVkUHJlZmVyZW5jZUFkaXU");
    private static jj f;
    private List<String> b;
    private String c;
    private final Context d;
    private final Handler e;

    public static jj a(Context context) {
        if (f == null) {
            synchronized (jj.class) {
                if (f == null) {
                    f = new jj(context);
                }
            }
        }
        return f;
    }

    private jj(Context context) {
        this.d = context.getApplicationContext();
        if (Looper.myLooper() == null) {
            this.e = new a(Looper.getMainLooper(), this);
        } else {
            this.e = new a(this);
        }
    }

    public final void a(String str) {
        this.c = str;
    }

    public final void b(String str) {
        List<String> list = this.b;
        if (list != null) {
            list.clear();
            this.b.add(str);
        }
        a(str, 273);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v13, types: [com.amap.api.col.3sl.jj$1] */
    public synchronized void a(final String str, final int i) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread() { // from class: com.amap.api.col.3sl.jj.1
                @Override // java.lang.Thread, java.lang.Runnable
                public final void run() {
                    String strB = jp.b(str);
                    if (TextUtils.isEmpty(strB)) {
                        return;
                    }
                    if ((i & 1) > 0) {
                        try {
                            if (Build.VERSION.SDK_INT < 23 || Settings.System.canWrite(jj.this.d)) {
                                Settings.System.putString(jj.this.d.getContentResolver(), jj.this.c, strB);
                            }
                        } catch (Exception unused) {
                        }
                    }
                    if ((i & 16) > 0) {
                        jl.a(jj.this.d, jj.this.c, strB);
                    }
                    if ((i & 256) > 0) {
                        SharedPreferences.Editor editorEdit = jj.this.d.getSharedPreferences(jj.a, 0).edit();
                        editorEdit.putString(jj.this.c, strB);
                        if (Build.VERSION.SDK_INT >= 9) {
                            editorEdit.apply();
                        } else {
                            editorEdit.commit();
                        }
                    }
                }
            }.start();
            return;
        }
        String strB = jp.b(str);
        if (!TextUtils.isEmpty(strB)) {
            if ((i & 1) > 0) {
                try {
                    if (Build.VERSION.SDK_INT >= 23) {
                        Settings.System.putString(this.d.getContentResolver(), this.c, strB);
                    } else {
                        Settings.System.putString(this.d.getContentResolver(), this.c, strB);
                    }
                } catch (Exception unused) {
                }
            }
            if ((i & 16) > 0) {
                jl.a(this.d, this.c, strB);
            }
            if ((i & 256) > 0) {
                SharedPreferences.Editor editorEdit = this.d.getSharedPreferences(a, 0).edit();
                editorEdit.putString(this.c, strB);
                if (Build.VERSION.SDK_INT >= 9) {
                    editorEdit.apply();
                    return;
                }
                editorEdit.commit();
            }
        }
    }

    /* JADX INFO: compiled from: AdiuStorageModel.java */
    private static final class a extends Handler {
        private final WeakReference<jj> a;

        a(jj jjVar) {
            this.a = new WeakReference<>(jjVar);
        }

        a(Looper looper, jj jjVar) {
            super(looper);
            this.a = new WeakReference<>(jjVar);
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            jj jjVar = this.a.get();
            if (jjVar == null || message == null || message.obj == null) {
                return;
            }
            jjVar.a((String) message.obj, message.what);
        }
    }
}
