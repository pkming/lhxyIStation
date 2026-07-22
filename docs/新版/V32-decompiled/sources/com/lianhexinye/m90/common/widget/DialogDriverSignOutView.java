package com.lianhexinye.m90.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: classes2.dex */
public class DialogDriverSignOutView extends Dialog {
    private Button butCancel;
    private Button butOk;
    private Context context;
    private EditText etDriverNumber;
    private OnOKClickListener onOKclickListener;

    public interface OnOKClickListener {
        void onCancelClick();

        void onSuccessClick(String str, String str2);
    }

    public DialogDriverSignOutView(Context context) {
        super(context, R.style.dialogPrompt);
        this.context = context;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dlg_driver_signout);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
    }

    private void initView() {
        this.butOk = (Button) findViewById(R.id.butDlgDriverOK);
        this.butCancel = (Button) findViewById(R.id.butDlgDriverCancel);
        this.etDriverNumber = (EditText) findViewById(R.id.etDlgDriverNumber);
        String string = SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.DRIVERCARDID, "").toString();
        if (string.length() > 0) {
            try {
                this.etDriverNumber.setText(JavaUtils.stringToGBK(string.substring(8, 16)));
                return;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
        }
        this.etDriverNumber.setText("");
    }

    private void initEvent() {
        this.butOk.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogDriverSignOutView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogDriverSignOutView.this.etDriverNumber.getText().toString().trim().length() == 0) {
                    Toast.makeText(DialogDriverSignOutView.this.context, "司机工号不能为空", 1).show();
                }
                if (DialogDriverSignOutView.this.onOKclickListener != null) {
                    DialogDriverSignOutView.this.onOKclickListener.onSuccessClick(DialogDriverSignOutView.this.etDriverNumber.getText().toString(), "999999");
                }
            }
        });
        this.butCancel.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogDriverSignOutView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogDriverSignOutView.this.onOKclickListener != null) {
                    DialogDriverSignOutView.this.onOKclickListener.onCancelClick();
                }
            }
        });
    }

    public void setOnOKClickListener(OnOKClickListener onOKClickListener) {
        this.onOKclickListener = onOKClickListener;
    }
}
