package com.lhxy.istationdevice.android11.app.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.lhxy.istationdevice.android11.app.R;
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
        bindEntry(R.id.lyFileManage, LegacyFileManageActivity.class);
        bindEntry(R.id.lySystemSet, LegacyBasicSetupActivity.class);
        bindEntry(R.id.lyVoiceCall, LegacyPasswordActivity.class);
        bindEntry(R.id.lyDispatchingCenter, LegacyDispatchCenterActivity.class);
        bindEntry(R.id.lyInfoBrowsing, LegacyInfoBrowsActivity.class);
        bindEntry(R.id.lySysInfo, LegacySystemInfoActivity.class);
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
