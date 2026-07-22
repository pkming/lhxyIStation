package com.lianhexinye.m90.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.JavaUtils;

/* JADX INFO: loaded from: classes2.dex */
public class DialogBusDirectionSeleView extends Dialog {
    private Button butCancel;
    private Button butOk;
    private String content;
    private Context context;
    private String lineName;
    private OnOKClickListener onOKclickListener;
    private RadioButton rbDirectionDown;
    private RadioButton rbDirectionUpstream;
    private RadioGroup rgDirection;
    private String strDirection;
    private String strVersionContent;
    private String strVersionName;

    public interface OnOKClickListener {
        void onCancelClick();

        void onOKClick(String str);
    }

    public DialogBusDirectionSeleView(Context context, String str, String str2) {
        super(context, R.style.dialogPrompt);
        this.strDirection = "S";
        this.context = context;
        this.strDirection = str;
        this.lineName = str2;
    }

    public DialogBusDirectionSeleView(Context context, int i) {
        super(context, i);
        this.strDirection = "S";
        this.context = context;
    }

    protected DialogBusDirectionSeleView(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.strDirection = "S";
        this.context = context;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dlg_bus_direction);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        initView();
        initEvent();
    }

    private void initView() {
        this.butCancel = (Button) findViewById(R.id.butCancel);
        this.butOk = (Button) findViewById(R.id.butOk);
        this.rgDirection = (RadioGroup) findViewById(R.id.rgDirection);
        this.rbDirectionUpstream = (RadioButton) findViewById(R.id.rbDirectionUpstream);
        this.rbDirectionDown = (RadioButton) findViewById(R.id.rbDirectionDown);
        if (!JavaUtils.isEmpty(this.lineName)) {
            if (!this.lineName.trim().equals(this.strDirection.trim().substring(0, this.strDirection.length() - 1))) {
                this.rgDirection.check(R.id.rbDirectionUpstream);
                this.strDirection = "S";
                return;
            } else if (this.strDirection.endsWith("S")) {
                this.rgDirection.check(R.id.rbDirectionUpstream);
                this.strDirection = "S";
                return;
            } else {
                this.rgDirection.check(R.id.rbDirectionDown);
                this.strDirection = "X";
                return;
            }
        }
        this.rgDirection.check(R.id.rbDirectionUpstream);
        this.strDirection = "S";
    }

    private void initEvent() {
        this.rgDirection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.common.widget.DialogBusDirectionSeleView.1
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbDirectionDown /* 2131296599 */:
                        DialogBusDirectionSeleView.this.strDirection = "X";
                        break;
                    case R.id.rbDirectionUpstream /* 2131296600 */:
                        DialogBusDirectionSeleView.this.strDirection = "S";
                        break;
                }
            }
        });
        this.butOk.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogBusDirectionSeleView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogBusDirectionSeleView.this.onOKclickListener != null) {
                    DialogBusDirectionSeleView.this.onOKclickListener.onOKClick(DialogBusDirectionSeleView.this.strDirection);
                }
            }
        });
        this.butCancel.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogBusDirectionSeleView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogBusDirectionSeleView.this.onOKclickListener != null) {
                    DialogBusDirectionSeleView.this.onOKclickListener.onCancelClick();
                }
            }
        });
    }

    public void setOnOKClickListener(OnOKClickListener onOKClickListener) {
        this.onOKclickListener = onOKClickListener;
    }

    public void setContext(String str) {
        this.content = str;
    }
}
