package com.lianhexinye.m90.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class DialogSingleView extends Dialog {
    private Button butOk;
    private String content;
    private Context context;
    private OnOKCancelClickListener onOKCancelClickListener;

    public interface OnOKCancelClickListener {
        void onOKClick();
    }

    public DialogSingleView(Builder builder) {
        super(builder.context, R.style.dialogPrompt);
        this.context = builder.context;
        this.content = builder.content;
        this.onOKCancelClickListener = builder.onOKCancelClickListener;
    }

    public DialogSingleView(Context context, int i) {
        super(context, i);
        this.context = context;
    }

    protected DialogSingleView(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.context = context;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dlg_single);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
    }

    private void initView() {
        this.butOk = (Button) findViewById(R.id.butOk);
        ((TextView) findViewById(R.id.tvTip)).setText(this.content);
    }

    private void initEvent() {
        this.butOk.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogSingleView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogSingleView.this.onOKCancelClickListener != null) {
                    DialogSingleView.this.onOKCancelClickListener.onOKClick();
                }
            }
        });
    }

    public void setContent(String str) {
        this.content = str;
    }

    public static final class Builder {
        private String content;
        private Context context;
        private OnOKCancelClickListener onOKCancelClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setContent(String str) {
            this.content = str;
            return this;
        }

        public Builder setOnOKClickListener(OnOKCancelClickListener onOKCancelClickListener) {
            this.onOKCancelClickListener = onOKCancelClickListener;
            return this;
        }

        public DialogSingleView build() {
            return new DialogSingleView(this);
        }
    }
}
