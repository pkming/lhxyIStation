package com.unisound.common;

import android.content.Context;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class g {
    private static String a = "asr_start_beep.mp3";
    private static String b = "wake_up_success.wav";
    private ak c;
    private String d;
    private String e;

    private boolean a(String str) {
        if (str == null) {
            return false;
        }
        this.c.a(str);
        this.c.d();
        return true;
    }

    public void a(Context context) {
        this.c = new ak(context);
        this.d = (context.getFilesDir().toString() + File.separator) + a;
        i.a(context, "usc" + File.separatorChar + a, this.d, b);
    }

    public boolean a() {
        return a(this.d);
    }

    public boolean b() {
        return a(this.e);
    }
}
