package com.lianhexinye.m90.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.PasswordManager;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class PasswordActivity extends MvpActivity<MainView, MainPresenter> implements MainView {
    private EditText etConfirmPwd;
    private EditText etPwd;
    private PasswordManager pwdManager;
    private TextView tvStatus;

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void authorizationResult(int i, String str) {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getDataSuccess() {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getLineNameDataSuccess2(List<LineNameModel> list) {
    }

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void showLoading(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_password);
        initView();
        this.etPwd = (EditText) findViewById(R.id.et_pwd);
        this.etConfirmPwd = (EditText) findViewById(R.id.et_confirm_pwd);
        Button button = (Button) findViewById(R.id.btn_verify);
        Button button2 = (Button) findViewById(R.id.btn_set);
        this.tvStatus = (TextView) findViewById(R.id.tv_status);
        this.pwdManager = new PasswordManager(this);
        updateStatus();
        button.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.ui.activity.PasswordActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String strTrim = PasswordActivity.this.etPwd.getText().toString().trim();
                if (strTrim.isEmpty()) {
                    PasswordActivity.this.showToast("Please enter password");
                    return;
                }
                if (PasswordActivity.this.pwdManager.verifyPassword(strTrim)) {
                    if (PasswordActivity.this.pwdManager.isSuperPassword(strTrim)) {
                        PasswordActivity.this.showToast("Success: Super password");
                        return;
                    } else {
                        PasswordActivity.this.showToast("Success: User password");
                        return;
                    }
                }
                PasswordActivity.this.showToast("Password error");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.ui.activity.PasswordActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String strTrim = PasswordActivity.this.etPwd.getText().toString().trim();
                String strTrim2 = PasswordActivity.this.etConfirmPwd.getText().toString().trim();
                if (strTrim.isEmpty() || strTrim2.isEmpty()) {
                    PasswordActivity.this.showToast("Please fill all fields");
                    return;
                }
                if (!strTrim.equals(strTrim2)) {
                    PasswordActivity.this.showToast("Password not match");
                    return;
                }
                PasswordActivity.this.pwdManager.setUserPassword(strTrim);
                PasswordActivity.this.showToast("Password saved successfully");
                PasswordActivity.this.etPwd.setText("");
                PasswordActivity.this.etConfirmPwd.setText("");
                PasswordActivity.this.updateStatus();
            }
        });
    }

    private void initView() {
        initToolBarBack(getResources().getString(R.string.login_pwd_hint));
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override // com.lianhexinye.m90.mvp.BaseActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.index) {
            return true;
        }
        setResult(-1);
        finish();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpActivity
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStatus() {
        if (this.pwdManager.hasUserPassword()) {
            this.tvStatus.setText("Status: User Password has been set");
        } else {
            this.tvStatus.setText("Status: User Password not set");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showToast(String str) {
        Toast.makeText(this, str, 0).show();
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }
}
