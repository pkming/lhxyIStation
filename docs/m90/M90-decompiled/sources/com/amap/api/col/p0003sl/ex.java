package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.DownloadProgressView;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.lianhexinye.m90.R;

/* JADX INFO: compiled from: OfflineChild.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ex implements View.OnClickListener {
    private Context b;
    private TextView c;
    private TextView d;
    private ImageView e;
    private TextView f;
    private OfflineMapManager g;
    private OfflineMapCity h;
    private View k;
    private DownloadProgressView l;
    private int a = 0;
    private boolean i = false;
    private Handler j = new Handler() { // from class: com.amap.api.col.3sl.ex.1
        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            super.handleMessage(message);
            try {
                ex.this.a(message.arg1, message.arg2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i, int i2) throws Exception {
        if (this.a == 2 && i2 > 3 && i2 < 100) {
            this.l.setVisibility(0);
            this.l.setProgress(i2);
        } else {
            this.l.setVisibility(8);
        }
        if (i == -1) {
            f();
            return;
        }
        if (i == 0) {
            if (this.a == 1) {
                this.e.setVisibility(8);
                this.f.setText("下载中");
                this.f.setTextColor(Color.parseColor("#4287ff"));
                return;
            }
            j();
            return;
        }
        if (i == 1) {
            i();
            return;
        }
        if (i == 2) {
            e();
            return;
        }
        if (i == 3) {
            g();
            return;
        }
        if (i == 4) {
            h();
            return;
        }
        if (i == 6) {
            c();
        } else {
            if (i != 7) {
                switch (i) {
                    case 101:
                    case 102:
                    case 103:
                        f();
                        break;
                }
                return;
            }
            d();
        }
    }

    public ex(Context context, OfflineMapManager offlineMapManager) {
        this.b = context;
        b();
        this.g = offlineMapManager;
    }

    public final void a(int i) {
        this.a = i;
    }

    public final View a() {
        return this.k;
    }

    private void b() {
        View viewA = fb.a(this.b, R.array.arraybaud);
        this.k = viewA;
        this.l = (DownloadProgressView) viewA.findViewById(R.dimen.abc_action_bar_subtitle_top_margin_material);
        this.c = (TextView) this.k.findViewById(R.dimen.abc_action_bar_overflow_padding_end_material);
        this.d = (TextView) this.k.findViewById(R.dimen.abc_action_bar_subtitle_bottom_margin_material);
        this.e = (ImageView) this.k.findViewById(R.dimen.abc_action_bar_stacked_tab_max_width);
        this.f = (TextView) this.k.findViewById(R.dimen.abc_action_bar_stacked_max_height);
        this.e.setOnClickListener(this);
    }

    public final void a(OfflineMapCity offlineMapCity) {
        if (offlineMapCity != null) {
            this.h = offlineMapCity;
            this.c.setText(offlineMapCity.getCity());
            this.d.setText(String.valueOf(((double) ((int) (((offlineMapCity.getSize() / 1024.0d) / 1024.0d) * 100.0d))) / 100.0d) + " M");
            b(this.h.getState(), this.h.getcompleteCode());
        }
    }

    private void b(int i, int i2) {
        OfflineMapCity offlineMapCity = this.h;
        if (offlineMapCity != null) {
            offlineMapCity.setState(i);
            this.h.setCompleteCode(i2);
        }
        Message message = new Message();
        message.arg1 = i;
        message.arg2 = i2;
        this.j.sendMessage(message);
    }

    private void c() throws Throwable {
        this.f.setVisibility(8);
        this.e.setVisibility(0);
        this.e.setImageResource(R.animator.design_fab_show_motion_spec);
    }

    private void d() throws Throwable {
        this.f.setVisibility(0);
        this.e.setVisibility(0);
        this.e.setImageResource(R.animator.design_fab_show_motion_spec);
        this.f.setText("已下载-有更新");
    }

    private void e() {
        if (this.a == 1) {
            this.e.setVisibility(8);
            this.f.setVisibility(0);
            this.f.setText("等待中");
            this.f.setTextColor(Color.parseColor("#4287ff"));
            return;
        }
        this.f.setVisibility(0);
        this.e.setVisibility(8);
        this.f.setTextColor(Color.parseColor("#4287ff"));
        this.f.setText("等待中");
    }

    private void f() {
        this.f.setVisibility(0);
        this.e.setVisibility(8);
        this.f.setTextColor(-65536);
        this.f.setText("下载出现异常");
    }

    private void g() {
        this.f.setVisibility(0);
        this.e.setVisibility(8);
        this.f.setTextColor(Color.GRAY);
        this.f.setText("暂停");
    }

    private void h() {
        this.f.setVisibility(0);
        this.e.setVisibility(8);
        this.f.setText("已下载");
        this.f.setTextColor(Color.parseColor("#898989"));
    }

    private void i() {
        if (this.a == 1) {
            return;
        }
        this.f.setVisibility(0);
        this.e.setVisibility(8);
        this.f.setText("解压中");
        this.f.setTextColor(Color.parseColor("#898989"));
    }

    private void j() {
        if (this.h == null) {
            return;
        }
        this.f.setVisibility(0);
        this.f.setText("下载中");
        this.e.setVisibility(8);
        this.f.setTextColor(Color.parseColor("#4287ff"));
    }

    private synchronized void k() {
        this.g.pause();
        this.g.restart();
    }

    private synchronized boolean l() {
        try {
            this.g.downloadByCityName(this.h.getCity());
        } catch (AMapException e) {
            e.printStackTrace();
            Toast.makeText(this.b, e.getErrorMessage(), 0).show();
            return false;
        }
        return true;
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        try {
            if (!dx.d(this.b)) {
                Toast.makeText(this.b, "无网络连接", 0).show();
                return;
            }
            OfflineMapCity offlineMapCity = this.h;
            if (offlineMapCity != null) {
                int state = offlineMapCity.getState();
                this.h.getcompleteCode();
                if (state == 0) {
                    k();
                    g();
                } else {
                    if (state == 1 || state == 4) {
                        return;
                    }
                    if (l()) {
                        e();
                    } else {
                        f();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
