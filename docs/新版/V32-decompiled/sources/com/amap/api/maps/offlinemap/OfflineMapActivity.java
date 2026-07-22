package com.amap.api.maps.offlinemap;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.amap.api.col.p0003sl.ey;
import com.amap.api.col.p0003sl.ez;
import com.amap.api.col.p0003sl.fb;
import com.amap.api.offlineservice.AMapPermissionActivity;
import com.amap.api.offlineservice.a;

/* JADX INFO: loaded from: classes2.dex */
public class OfflineMapActivity extends AMapPermissionActivity implements View.OnClickListener {
    private static int a;
    private a b;
    private ey c;
    private ey[] d = new ey[32];
    private int e = -1;
    private ez f;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            getWindow().setSoftInputMode(32);
            getWindow().setFormat(-3);
            requestWindowFeature(1);
            fb.a(getApplicationContext());
            this.e = -1;
            a = 0;
            b(new ey());
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void showScr() {
        try {
            setContentView(this.b.d());
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void a(ey eyVar) {
        try {
            a aVar = this.b;
            if (aVar != null) {
                aVar.e();
                this.b = null;
            }
            a aVarC = c(eyVar);
            this.b = aVarC;
            if (aVarC != null) {
                this.c = eyVar;
                aVarC.a(this);
                this.b.a();
                this.b.b();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void b(ey eyVar) {
        try {
            a++;
            a(eyVar);
            int i = (this.e + 1) % 32;
            this.e = i;
            this.d[i] = eyVar;
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private a c(ey eyVar) {
        try {
            if (eyVar.a != 1) {
                return null;
            }
            if (this.f == null) {
                this.f = new ez();
            }
            return this.f;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        try {
            super.onStart();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.amap.api.offlineservice.AMapPermissionActivity, android.app.Activity
    protected void onResume() {
        try {
            super.onResume();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        try {
            super.onPause();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        try {
            super.onStop();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        try {
            super.onConfigurationChanged(configuration);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void closeScr() {
        try {
            if (a((Bundle) null)) {
                return;
            }
            a aVar = this.b;
            if (aVar != null) {
                aVar.e();
            }
            finish();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void closeScr(Bundle bundle) {
        try {
            if (a(bundle)) {
                return;
            }
            a aVar = this.b;
            if (aVar != null) {
                aVar.e();
            }
            finish();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private boolean a(Bundle bundle) {
        try {
            int i = a;
            if ((i != 1 || this.b == null) && i > 1) {
                a = i - 1;
                int i2 = ((this.e - 1) + 32) % 32;
                this.e = i2;
                ey eyVar = this.d[i2];
                eyVar.b = bundle;
                a(eyVar);
                return true;
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return false;
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        try {
            super.onDestroy();
            a aVar = this.b;
            if (aVar != null) {
                aVar.e();
                this.b = null;
            }
            this.c = null;
            this.d = null;
            ez ezVar = this.f;
            if (ezVar != null) {
                ezVar.e();
                this.f = null;
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        try {
            a aVar = this.b;
            if (aVar != null) {
                aVar.a(view);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            try {
                a aVar = this.b;
                if (aVar != null && !aVar.c()) {
                    return true;
                }
                if (a((Bundle) null)) {
                    return false;
                }
                if (keyEvent == null) {
                    if (a == 1) {
                        finish();
                    }
                    return false;
                }
                this.e = -1;
                a = 0;
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }
}
