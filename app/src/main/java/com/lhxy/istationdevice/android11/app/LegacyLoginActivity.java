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

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;

/**
 * 旧版登录页。
 */
public final class LegacyLoginActivity extends AppCompatActivity {
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "999999";

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
                    ? etUserName.getText().toString().trim() : DEFAULT_USER;
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
            if (!DEFAULT_USER.equals(user) || !DEFAULT_PASSWORD.equals(pwd)) {
                Toast.makeText(this, R.string.login_error_hint1, Toast.LENGTH_SHORT).show();
                AppLogCenter.log(
                        LogCategory.UI,
                        LogLevel.WARN,
                        "LegacyLoginActivity",
                        "登录失败，账号=" + user,
                        TraceIds.next("legacy-login-fail")
                );
                return;
            }
            AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    "LegacyLoginActivity",
                    "登录成功，进入菜单页",
                    TraceIds.next("legacy-login-success")
            );
            startActivity(new Intent(this, LegacyMenuActivity.class));
            finish();
        });
    }
}
