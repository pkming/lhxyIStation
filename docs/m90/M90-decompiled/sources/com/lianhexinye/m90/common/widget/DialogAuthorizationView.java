package com.lianhexinye.m90.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;

/* JADX INFO: loaded from: classes2.dex */
public class DialogAuthorizationView extends Dialog {
    private Button butCancel;
    private Button butOk;
    private String content;
    private Context context;
    private EditText etAuthorIp;
    private EditText etAuthorName;
    private EditText etAuthorUser;
    private OnOKClickListener onOKclickListener;
    private String strVersionContent;
    private String strVersionName;

    public interface OnOKClickListener {
        void onCancelClick();

        void onSuccessClick(String str, String str2, String str3);
    }

    public DialogAuthorizationView(Context context) {
        super(context, R.style.dialogPrompt);
        this.context = context;
    }

    public DialogAuthorizationView(Context context, int i) {
        super(context, i);
        this.context = context;
    }

    protected DialogAuthorizationView(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.context = context;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dlg_authorization);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
    }

    private void initView() {
        this.butOk = (Button) findViewById(R.id.butOk);
        this.butCancel = (Button) findViewById(R.id.butCancel);
        this.etAuthorName = (EditText) findViewById(R.id.etAuthorName);
        this.etAuthorUser = (EditText) findViewById(R.id.etAuthorUser);
        this.etAuthorIp = (EditText) findViewById(R.id.etAuthorIp);
        this.etAuthorUser.setText(String.valueOf(SPUserInfoUtils.get(AppApplication.getContext(), "NetAdvertUser", "admin")));
        this.etAuthorName.setText(String.valueOf(SPUserInfoUtils.get(AppApplication.getContext(), "NetAdvertID", "")));
        this.etAuthorIp.setText(String.format(this.context.getResources().getString(R.string.main_dlg_author_ip_value), String.valueOf(SPUserInfoUtils.get(AppApplication.getContext(), "netAdvertIP", "39.104.66.194")), String.valueOf(SPUserInfoUtils.get(AppApplication.getContext(), "netAdvertPort", "8081"))));
    }

    private void initEvent() {
        this.butOk.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogAuthorizationView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogAuthorizationView.this.etAuthorName.getText().toString().trim().length() == 0 || DialogAuthorizationView.this.etAuthorIp.getText().toString().trim().length() == 0) {
                    Toast.makeText(DialogAuthorizationView.this.context, R.string.main_dlg_author_user_ip_tip, 1).show();
                } else if (DialogAuthorizationView.this.etAuthorIp.getText().toString().split(":").length != 2) {
                    Toast.makeText(DialogAuthorizationView.this.context, R.string.main_dlg_author_ip_errer_tip, 1).show();
                } else if (DialogAuthorizationView.this.onOKclickListener != null) {
                    DialogAuthorizationView.this.onOKclickListener.onSuccessClick(DialogAuthorizationView.this.etAuthorName.getText().toString(), DialogAuthorizationView.this.etAuthorIp.getText().toString(), DialogAuthorizationView.this.etAuthorUser.getText().toString());
                }
            }
        });
        this.butCancel.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.common.widget.DialogAuthorizationView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DialogAuthorizationView.this.onOKclickListener != null) {
                    DialogAuthorizationView.this.onOKclickListener.onCancelClick();
                }
            }
        });
    }

    public void setOnOKClickListener(OnOKClickListener onOKClickListener) {
        this.onOKclickListener = onOKClickListener;
    }
}
