package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

/**
 * 旧版系统信息页骨架。
 */
public final class LegacySystemInfoActivity extends LegacyBaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.act_sys_info;
    }

    @Override
    protected int getTitleResId() {
        return R.string.sysinfo_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        RadioGroup radioGroup = findViewById(R.id.rg_radio_sys_navigation);
        RadioButton defaultButton = findViewById(R.id.rb_radio_sys_version);
        if (defaultButton != null) {
            defaultButton.setChecked(true);
        }
        showContent(new LegacySystemVersionFragment());
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                Fragment fragment = checkedId == R.id.rb_radio_sys_network
                        ? new LegacySystemNetworkFragment()
                        : new LegacySystemVersionFragment();
                showContent(fragment);
            });
        }
    }

    private void showContent(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flSysContext, fragment)
                .commit();
    }
}
