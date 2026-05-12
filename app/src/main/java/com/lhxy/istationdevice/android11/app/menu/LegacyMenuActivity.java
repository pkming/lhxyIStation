package com.lhxy.istationdevice.android11.app.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.ShellApplication;
import com.lhxy.istationdevice.android11.app.auth.LegacyPasswordActivity;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;
import com.lhxy.istationdevice.android11.app.dispatch.LegacyDispatchCenterActivity;
import com.lhxy.istationdevice.android11.app.file.LegacyFileManageActivity;
import com.lhxy.istationdevice.android11.app.info.LegacyInfoBrowsActivity;
import com.lhxy.istationdevice.android11.app.line.LegacyLineChoiceActivity;
import com.lhxy.istationdevice.android11.app.setup.LegacyBasicSetupActivity;
import com.lhxy.istationdevice.android11.app.station.LegacySiteCollectionActivity;
import com.lhxy.istationdevice.android11.app.sysinfo.LegacySystemInfoActivity;

/**
 * 旧版菜单页骨架。
 * <p>
 * 先恢复八宫格导航样式，并接通第一批正式子页面跳转。
 * <p>
 * 查找关键字：旧菜单入口、八宫格跳转、权限门禁、返回首页。
 */
public final class LegacyMenuActivity extends LegacyBaseActivity {
    private static final int REQUEST_SET_CODE = 1000;

    @Override
    protected int getLayoutId() {
        return R.layout.act_menu;
    }

    @Override
    protected int getTitleResId() {
        return R.string.menu_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        bindMenuEntries();
    }

    /**
     * 统一绑定八宫格入口到对应旧壳页面。
     */
    private void bindMenuEntries() {
        bindEntry(R.id.lyLineSele, LegacyLineChoiceActivity.class);
        bindEntry(R.id.lySiteLearn, LegacySiteCollectionActivity.class);
        bindFileManageEntry();
        bindEntry(R.id.lySystemSet, LegacyBasicSetupActivity.class);
        bindEntry(R.id.lyVoiceCall, LegacyPasswordActivity.class);
        bindEntry(R.id.lyDispatchingCenter, LegacyDispatchCenterActivity.class);
        bindEntry(R.id.lyInfoBrowsing, LegacyInfoBrowsActivity.class);
        bindEntry(R.id.lySysInfo, LegacySystemInfoActivity.class);
    }

    /**
     * 文件管理入口带超级密码门禁，不满足权限时直接拦截。
     */
    private void bindFileManageEntry() {
        View view = findViewById(R.id.lyFileManage);
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> {
            if (!ShellApplication.isUserPassword) {
                startActivityForResult(new Intent(this, LegacyFileManageActivity.class), REQUEST_SET_CODE);
                return;
            }
            Toast.makeText(this, R.string.legacy_password_super_required, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 绑定一个菜单格子到目标 Activity。
     */
    private void bindEntry(int id, Class<?> targetClass) {
        View view = findViewById(id);
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> startActivityForResult(new Intent(this, targetClass), REQUEST_SET_CODE));
    }

    /**
     * 绑定一个菜单格子到预先构造好的 Intent。
     */
    private void bindEntry(int id, Intent intent) {
        View view = findViewById(id);
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> startActivityForResult(intent, REQUEST_SET_CODE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SET_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.index) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
