package com.lhxy.istationdevice.android11.app.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;
import com.lhxy.istationdevice.android11.app.dispatch.LegacyDispatchCenterActivity;
import com.lhxy.istationdevice.android11.app.file.LegacyFileManageActivity;
import com.lhxy.istationdevice.android11.app.info.LegacyInfoBrowsActivity;
import com.lhxy.istationdevice.android11.app.line.LegacyLineChoiceActivity;
import com.lhxy.istationdevice.android11.app.media.LegacyVoiceCallActivity;
import com.lhxy.istationdevice.android11.app.setup.LegacyBasicSetupActivity;
import com.lhxy.istationdevice.android11.app.station.LegacySiteCollectionActivity;
import com.lhxy.istationdevice.android11.app.sysinfo.LegacySystemInfoActivity;

/**
 * 旧版菜单页骨架。
 * <p>
 * 先恢复八宫格导航样式，并接通第一批正式子页面跳转。
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
        view.setOnClickListener(v -> startActivityForResult(new Intent(this, targetClass), REQUEST_SET_CODE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SET_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }
}
