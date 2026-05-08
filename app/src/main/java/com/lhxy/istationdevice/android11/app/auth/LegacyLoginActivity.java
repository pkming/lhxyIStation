package com.lhxy.istationdevice.android11.app.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.ShellApplication;
import com.lhxy.istationdevice.android11.app.menu.LegacyMenuActivity;
import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * 旧版登录页。
 */
public final class LegacyLoginActivity extends AppCompatActivity {
    private static final String EXTRA_NEXT_ACTIVITY = "next_activity";
    private LegacyPasswordManager passwordManager;
    private ViewTreeObserver.OnGlobalLayoutListener loginLayoutListener;

    public static Intent createIntent(Context context) {
        return new Intent(context, LegacyLoginActivity.class);
    }

    public static Intent createIntent(Context context, Class<?> nextActivityClass) {
        Intent intent = createIntent(context);
        intent.putExtra(EXTRA_NEXT_ACTIVITY, nextActivityClass.getName());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        passwordManager = new LegacyPasswordManager(this);
        initToolbar();
        keepLoginButtonVisible();
        bindLogin();
    }

    @Override
    protected void onDestroy() {
        clearLoginButtonVisibleListener();
        super.onDestroy();
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
        MaterialEditText etUserPwd = findViewById(R.id.etUserPwd);
        Button butLogin = findViewById(R.id.butLogin);
        if (butLogin == null) {
            return;
        }
        butLogin.setOnClickListener(v -> {
            String pwd = etUserPwd != null && etUserPwd.getText() != null
                    ? etUserPwd.getText().toString().trim() : "";
            if (TextUtils.isEmpty(pwd)) {
                Toast.makeText(this, R.string.login_notnull_hint1, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!passwordManager.checkPassword(pwd)) {
                Toast.makeText(this, R.string.login_error_hint1, Toast.LENGTH_SHORT).show();
                AppLogCenter.log(
                        LogCategory.UI,
                        LogLevel.WARN,
                        "LegacyLoginActivity",
                        "登录失败，密码校验未通过",
                        TraceIds.next("legacy-login-fail")
                );
                return;
            }
            if (passwordManager.isSuperPassword(pwd)) {
                ShellApplication.isUserPassword = false;
                Toast.makeText(this, R.string.login_super_success, Toast.LENGTH_SHORT).show();
            } else {
                ShellApplication.isUserPassword = true;
                Toast.makeText(this, R.string.login_user_success, Toast.LENGTH_SHORT).show();
            }
            AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    "LegacyLoginActivity",
                    "登录成功，进入菜单页",
                    TraceIds.next("legacy-login-success")
            );
            startActivity(resolveNextIntent());
            finish();
        });
    }

    private void keepLoginButtonVisible() {
        LinearLayout loginRoot = findViewById(R.id.lyLoginRoot);
        RelativeLayout contentRoot = findViewById(R.id.rlLoginRoot);
        if (loginRoot == null || contentRoot == null) {
            return;
        }
        clearLoginButtonVisibleListener();
        loginLayoutListener = () -> {
            Rect visibleRect = new Rect();
            loginRoot.getWindowVisibleDisplayFrame(visibleRect);
            int keyboardHeight = loginRoot.getRootView().getHeight() - visibleRect.bottom;
            if (keyboardHeight > 200) {
                int overlap = keyboardHeight - (loginRoot.getHeight() - contentRoot.getHeight());
                loginRoot.scrollTo(0, Math.max(overlap, 0));
                return;
            }
            loginRoot.scrollTo(0, 0);
        };
        loginRoot.getViewTreeObserver().addOnGlobalLayoutListener(loginLayoutListener);
    }

    private void clearLoginButtonVisibleListener() {
        View root = findViewById(R.id.lyLoginRoot);
        if (root == null || loginLayoutListener == null) {
            return;
        }
        ViewTreeObserver observer = root.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.removeOnGlobalLayoutListener(loginLayoutListener);
        }
        loginLayoutListener = null;
    }

    private Intent resolveNextIntent() {
        String nextActivityName = getIntent().getStringExtra(EXTRA_NEXT_ACTIVITY);
        if (TextUtils.isEmpty(nextActivityName)) {
            return new Intent(this, LegacyMenuActivity.class);
        }
        try {
            Class<?> nextActivityClass = Class.forName(nextActivityName);
            return new Intent(this, nextActivityClass);
        } catch (ClassNotFoundException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.ERROR,
                    "LegacyLoginActivity",
                    "未找到目标页面，回退菜单页: " + nextActivityName,
                    TraceIds.next("legacy-login-missing-target")
            );
            return new Intent(this, LegacyMenuActivity.class);
        }
    }
}
