package com.amap.api.location;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.amap.api.col.p0003sl.f;
import com.amap.api.col.p0003sl.ju;
import com.autonavi.aps.amapapi.utils.b;

/* JADX INFO: loaded from: classes2.dex */
public class APSService extends Service {
    f a;
    int b = 0;
    boolean c = false;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        try {
            return this.a.a(intent);
        } catch (Throwable th) {
            b.a(th, "APSService", "onBind");
            return null;
        }
    }

    private void a(Context context) {
        try {
            if (this.a == null) {
                this.a = new f(context);
            }
            this.a.a();
        } catch (Throwable th) {
            b.a(th, "APSService", "onCreate");
        }
        super.onCreate();
    }

    @Override // android.app.Service
    public void onCreate() {
        a(this);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        int i3;
        if (intent != null) {
            try {
                int intExtra = intent.getIntExtra(ju.f, 0);
                if (intExtra == 1) {
                    int intExtra2 = intent.getIntExtra("i", 0);
                    Notification notification = (Notification) intent.getParcelableExtra(ju.g);
                    if (intExtra2 != 0 && notification != null) {
                        startForeground(intExtra2, notification);
                        this.c = true;
                        this.b++;
                    }
                } else if (intExtra == 2) {
                    if (intent.getBooleanExtra("j", true) && (i3 = this.b) > 0) {
                        this.b = i3 - 1;
                    }
                    if (this.b <= 0) {
                        stopForeground(true);
                        this.c = false;
                    } else {
                        stopForeground(false);
                    }
                }
            } catch (Throwable unused) {
            }
        }
        try {
            return this.a.b();
        } catch (Throwable th) {
            b.a(th, "APSService", "onStartCommand");
            return super.onStartCommand(intent, i, i2);
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        try {
            this.a.c();
            if (this.c) {
                stopForeground(true);
            }
        } catch (Throwable th) {
            b.a(th, "APSService", "onDestroy");
        }
        super.onDestroy();
    }
}
