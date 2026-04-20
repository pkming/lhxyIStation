package com.lhxy.istationdevice.android11.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 旧版菜单页骨架。
 * <p>
 * 先恢复八宫格导航样式，并接通第一批正式子页面跳转。
 */
public final class LegacyMenuActivity extends LegacyBaseActivity {
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

    private void bindMenuEntries() {
        bindEntry(R.id.lyLineSele, LegacyLineChoiceActivity.class);
        bindEntry(R.id.lySiteLearn, LegacySiteCollectionActivity.class);
        bindEntry(R.id.lyFileManage, LegacyFileManageActivity.class);
        bindEntry(R.id.lySystemSet, LegacyBasicSetupActivity.class);
        bindEntry(R.id.lyVoiceCall, LegacyVoiceCallActivity.class);
        bindEntry(R.id.lyDispatchingCenter, LegacyDispatchCenterActivity.class);
        bindEntry(R.id.lyInfoBrowsing, LegacyInfoBrowsActivity.class);
        bindEntry(R.id.lySysInfo, LegacySystemInfoActivity.class);
    }

    private void bindEntry(int id, Class<?> targetClass) {
        View view = findViewById(id);
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> startActivity(new Intent(this, targetClass)));
    }
}
