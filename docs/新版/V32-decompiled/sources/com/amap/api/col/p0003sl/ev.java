package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.lianhexinye.m90.R;

/* JADX INFO: compiled from: BottomDialog.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ev extends ew implements View.OnClickListener {
    private OfflineMapManager a;
    private View b;
    private TextView c;
    private TextView d;
    private TextView e;
    private TextView f;
    private int g;
    private String h;

    public ev(Context context, OfflineMapManager offlineMapManager) {
        super(context);
        this.a = offlineMapManager;
    }

    @Override // com.amap.api.col.p0003sl.ew
    protected final void a() {
        View viewA = fb.a(getContext(), R.array.WheelArrayWeek);
        this.b = viewA;
        setContentView(viewA);
        this.b.setOnClickListener(new View.OnClickListener() { // from class: com.amap.api.col.3sl.ev.1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ev.this.dismiss();
            }
        });
        this.c = (TextView) this.b.findViewById(R.dimen.abc_action_bar_default_padding_end_material);
        TextView textView = (TextView) this.b.findViewById(R.dimen.abc_action_bar_default_padding_start_material);
        this.d = textView;
        textView.setText("暂停下载");
        this.e = (TextView) this.b.findViewById(R.dimen.abc_action_bar_elevation_material);
        this.f = (TextView) this.b.findViewById(R.dimen.abc_action_bar_icon_vertical_padding_material);
        this.d.setOnClickListener(this);
        this.e.setOnClickListener(this);
        this.f.setOnClickListener(this);
    }

    public final void a(int i, String str) {
        this.c.setText(str);
        if (i == 0) {
            this.d.setText("暂停下载");
            this.d.setVisibility(0);
            this.e.setText("取消下载");
        }
        if (i == 2) {
            this.d.setVisibility(8);
            this.e.setText("取消下载");
        } else if (i == -1 || i == 101 || i == 102 || i == 103) {
            this.d.setText("继续下载");
            this.d.setVisibility(0);
        } else if (i == 3) {
            this.d.setVisibility(0);
            this.d.setText("继续下载");
            this.e.setText("取消下载");
        } else if (i == 4) {
            this.e.setText("删除");
            this.d.setVisibility(8);
        }
        this.g = i;
        this.h = str;
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        try {
            int id = view.getId();
            if (id != R.dimen.abc_action_bar_default_padding_start_material) {
                if (id != R.dimen.abc_action_bar_elevation_material) {
                    if (id == R.dimen.abc_action_bar_icon_vertical_padding_material) {
                        dismiss();
                        return;
                    }
                    return;
                } else {
                    if (TextUtils.isEmpty(this.h)) {
                        return;
                    }
                    this.a.remove(this.h);
                    dismiss();
                    return;
                }
            }
            int i = this.g;
            if (i == 0) {
                this.d.setText("继续下载");
                this.a.pauseByName(this.h);
            } else if (i == 3 || i == -1 || i == 101 || i == 102 || i == 103) {
                this.d.setText("暂停下载");
                this.a.downloadByCityName(this.h);
            }
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
