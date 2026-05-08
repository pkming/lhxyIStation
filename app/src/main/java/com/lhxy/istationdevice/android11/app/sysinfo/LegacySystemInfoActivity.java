package com.lhxy.istationdevice.android11.app.sysinfo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;

/**
 * 旧版系统信息页骨架。
 */
public final class LegacySystemInfoActivity extends LegacyBaseActivity {
    private static final String TAG_VERSION = "VersionInfoFragment";
    private static final String TAG_NETWORK = "NetworkInfoFragment";

    private FragmentManager fragmentManager;
    private String activeTag = TAG_VERSION;

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
        fragmentManager = getSupportFragmentManager();
        RadioGroup radioGroup = findViewById(R.id.rg_radio_sys_navigation);
        RadioButton defaultButton = findViewById(R.id.rb_radio_sys_version);
        if (defaultButton != null) {
            defaultButton.setChecked(true);
        }
        showContent();
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                activeTag = checkedId == R.id.rb_radio_sys_network
                        ? TAG_NETWORK
                        : TAG_VERSION;
                showContent();
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.index) {
            return true;
        }
        setResult(RESULT_OK);
        finish();
        return true;
    }

    private void showContent() {
        if (fragmentManager == null) {
            return;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment target = fragmentManager.findFragmentByTag(activeTag);
        if (target != null) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment == null) {
                    continue;
                }
                if (fragment == target) {
                    transaction.show(fragment);
                } else {
                    transaction.hide(fragment);
                }
            }
            transaction.commit();
            return;
        }

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }

        transaction.add(R.id.flSysContext, createFragment(activeTag), activeTag);
        transaction.commit();
    }

    private Fragment createFragment(String tag) {
        if (TAG_NETWORK.equals(tag)) {
            return new LegacySystemNetworkFragment();
        }
        return new LegacySystemVersionFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragmentManager = null;
    }
}
