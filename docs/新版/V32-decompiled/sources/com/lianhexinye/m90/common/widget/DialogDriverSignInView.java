package com.lianhexinye.m90.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class DialogDriverSignInView extends Dialog {
    private Button butCancel;
    private Button butOk;
    private Context context;
    private EditText etDriverNumber;
    private OnOKClickListener onOKclickListener;

    public interface OnOKClickListener {
        void onCancelClick();

        void onSuccessClick(String str, String str2);
    }

    public DialogDriverSignInView(Context context) {
        super(context, R.style.dialogPrompt);
        this.context = context;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dlg_driver_signin);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
    }

    private void initView() {
        this.butOk = (Button) findViewById(R.id.butDlgDriverOK);
        this.butCancel = (Button) findViewById(R.id.butDlgDriverCancel);
        EditText editText = (EditText) findViewById(R.id.etDlgDriverNumber);
        this.etDriverNumber = editText;
        editText.setText("");
    }

    private void initEvent() {
        this.butOk.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogDriverSignInView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogDriverSignInView.this.etDriverNumber.getText().toString().trim().length() == 0) {
                    Toast.makeText(DialogDriverSignInView.this.context, "司机工号或者密码不能为空", 1).show();
                }
                if (DialogDriverSignInView.this.etDriverNumber.getText().toString().trim().length() == 4) {
                    if (DialogDriverSignInView.this.onOKclickListener != null) {
                        DialogDriverSignInView.this.onOKclickListener.onSuccessClick(DialogDriverSignInView.this.etDriverNumber.getText().toString(), "999999");
                        return;
                    }
                    return;
                }
                Toast.makeText(DialogDriverSignInView.this.context, "司机工号格式错误", 1).show();
            }
        });
        this.butCancel.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogDriverSignInView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogDriverSignInView.this.onOKclickListener != null) {
                    DialogDriverSignInView.this.onOKclickListener.onCancelClick();
                }
            }
        });
    }

    public void setOnOKClickListener(OnOKClickListener onOKClickListener) {
        this.onOKclickListener = onOKClickListener;
    }
}
