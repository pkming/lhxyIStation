package com.lhxy.istationdevice.android11.app.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;

public final class LegacyPasswordActivity extends LegacyBaseActivity {
    private LegacyPasswordManager passwordManager;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private TextView statusView;

    @Override
    protected int getLayoutId() {
        return R.layout.act_password;
    }

    @Override
    protected int getTitleResId() {
        return R.string.login_pwd_hint;
    }

    @Override
    protected void onPageReady(@Nullable Bundle savedInstanceState) {
        passwordManager = new LegacyPasswordManager(this);
        passwordInput = findViewById(R.id.et_pwd);
        confirmPasswordInput = findViewById(R.id.et_confirm_pwd);
        statusView = findViewById(R.id.tv_status);
        Button verifyButton = findViewById(R.id.btn_verify);
        Button setButton = findViewById(R.id.btn_set);
        updateStatus();
        if (verifyButton != null) {
            verifyButton.setOnClickListener(v -> verifyPassword());
        }
        if (setButton != null) {
            setButton.setOnClickListener(v -> saveUserPassword());
        }
    }

    private void verifyPassword() {
        String password = readText(passwordInput);
        if (password.isEmpty()) {
            showToast(R.string.legacy_password_enter_hint);
            return;
        }
        if (passwordManager.verifyPassword(password)) {
            showToast(passwordManager.isSuperPassword(password)
                    ? R.string.legacy_password_super_success
                    : R.string.legacy_password_user_success);
            return;
        }
        showToast(R.string.legacy_password_error);
    }

    private void saveUserPassword() {
        String password = readText(passwordInput);
        String confirmPassword = readText(confirmPasswordInput);
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            showToast(R.string.legacy_password_fill_all);
            return;
        }
        if (!password.equals(confirmPassword)) {
            showToast(R.string.legacy_password_not_match);
            return;
        }
        passwordManager.setUserPassword(password);
        if (passwordInput != null) {
            passwordInput.setText("");
        }
        if (confirmPasswordInput != null) {
            confirmPasswordInput.setText("");
        }
        updateStatus();
        showToast(R.string.legacy_password_saved);
    }

    private void updateStatus() {
        if (statusView == null) {
            return;
        }
        statusView.setText(passwordManager.hasUserPassword()
                ? R.string.legacy_password_status_set
                : R.string.legacy_password_status_not_set);
    }

    private String readText(@Nullable EditText editText) {
        return editText != null && editText.getText() != null
                ? editText.getText().toString().trim()
                : "";
    }

    private void showToast(int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}