package com.lianhexinye.m90.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class DialogloadView extends Dialog {
    private Context context;
    private ImageView imageView;
    private TextView textView;

    public DialogloadView(Context context) {
        super(context, R.style.dialogLoading);
        this.context = context;
        initView();
    }

    public DialogloadView(Context context, int i) {
        super(context, i);
        this.context = context;
        initView();
    }

    protected DialogloadView(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dlg_loading);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        this.imageView = (ImageView) findViewById(R.id.ivDloLoading);
        this.textView = (TextView) findViewById(R.id.tvText);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!z || this.imageView == null) {
            return;
        }
        this.imageView.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.dialog_load_rotate));
    }

    public void setTipTextView(String str) {
        TextView textView = this.textView;
        if (textView != null) {
            textView.setText(str);
        }
    }
}
