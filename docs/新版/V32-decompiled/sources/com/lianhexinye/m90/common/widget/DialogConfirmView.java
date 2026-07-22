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
public class DialogConfirmView extends Dialog {
    private Button butCancel;
    private Button butOk;
    private String butOkTxt;
    private String content;
    private Context context;
    private OnOKCancelClickListener onOKCancelClickListener;

    public interface OnOKCancelClickListener {
        void onCancelClick();

        void onOKClick();
    }

    public DialogConfirmView(Builder builder) {
        super(builder.context, R.style.dialogPrompt);
        this.context = builder.context;
        this.content = builder.content;
        this.butOkTxt = builder.butOkTxt;
        this.onOKCancelClickListener = builder.onOKCancelClickListener;
    }

    public DialogConfirmView(Context context, int i) {
        super(context, i);
        this.context = context;
    }

    protected DialogConfirmView(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.context = context;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dlg_confirm);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        initView();
        initEvent();
    }

    private void initView() {
        this.butCancel = (Button) findViewById(R.id.butCancel);
        this.butOk = (Button) findViewById(R.id.butOk);
        ((TextView) findViewById(R.id.tvTip)).setText(this.content);
        this.butOk.setText(this.butOkTxt);
    }

    private void initEvent() {
        this.butOk.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogConfirmView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogConfirmView.this.onOKCancelClickListener != null) {
                    DialogConfirmView.this.onOKCancelClickListener.onOKClick();
                }
            }
        });
        this.butCancel.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogConfirmView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogConfirmView.this.onOKCancelClickListener != null) {
                    DialogConfirmView.this.onOKCancelClickListener.onCancelClick();
                }
            }
        });
    }

    public void setContent(String str) {
        this.content = str;
    }

    public static final class Builder {
        private String butOkTxt = "SURE";
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

        public Builder setButOkTxt(String str) {
            this.butOkTxt = str;
            return this;
        }

        public DialogConfirmView build() {
            return new DialogConfirmView(this);
        }
    }
}
