package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

/**
 * 旧版站点学习页骨架。
 */
public final class LegacySiteCollectionActivity extends LegacyBaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.act_site_collection;
    }

    @Override
    protected int getTitleResId() {
        return R.string.sitelearn_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        RadioGroup radioGroup = findViewById(R.id.rg_radio_learn_navigation);
        RadioButton defaultButton = findViewById(R.id.rb_radio_site_learn);
        if (defaultButton != null) {
            defaultButton.setChecked(true);
        }
        showContent(LegacySiteCollectionSectionFragment.newInstance(R.layout.f_site_collection, "SITE"));
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int layoutRes = checkedId == R.id.rb_radio_other ? R.layout.f_other_collection : R.layout.f_site_collection;
                String section = checkedId == R.id.rb_radio_other ? "OTHER" : "SITE";
                showContent(LegacySiteCollectionSectionFragment.newInstance(layoutRes, section));
            });
        }
    }

    private void showContent(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flSiteContext, fragment)
                .commit();
    }
}
