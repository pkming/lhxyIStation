package com.lhxy.istationdevice.android11.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

/**
 * 旧版登录页骨架。
 */
public final class LegacyLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        initToolbar();
        bindLogin();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.toolbar_title);
        if (title != null) {
            title.setText(R.string.login_title);
        }
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.icon_back_normal);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void bindLogin() {
        AppCompatEditText etUserName = findViewById(R.id.etUserName);
        AppCompatEditText etUserPwd = findViewById(R.id.etUserPwd);
        Button butLogin = findViewById(R.id.butLogin);
        if (butLogin == null) {
            return;
        }
        butLogin.setOnClickListener(v -> {
            String user = etUserName != null && etUserName.getText() != null
                    ? etUserName.getText().toString().trim() : "";
            String pwd = etUserPwd != null && etUserPwd.getText() != null
                    ? etUserPwd.getText().toString().trim() : "";
            if (TextUtils.isEmpty(pwd)) {
                Toast.makeText(this, R.string.login_notnull_hint1, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(user)) {
                Toast.makeText(this, R.string.login_notnull_hint, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, LegacyMenuActivity.class));
        });
    }
}
