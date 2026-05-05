package com.lhxy.istationdevice.android11.app.station;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;

/**
 * 旧版站点学习页骨架。
 */
public final class LegacySiteCollectionActivity extends LegacyBaseActivity {
    private static final String TAG_SITE = "SITE";
    private static final String TAG_OTHER = "OTHER";

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
        showContent(TAG_SITE);
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                showContent(checkedId == R.id.rb_radio_other ? TAG_OTHER : TAG_SITE);
            });
        }
    }

    private void showContent(String tag) {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment siteFragment = fragmentManager.findFragmentByTag(TAG_SITE);
        Fragment otherFragment = fragmentManager.findFragmentByTag(TAG_OTHER);
        if (siteFragment != null) {
            transaction.hide(siteFragment);
        }
        if (otherFragment != null) {
            transaction.hide(otherFragment);
        }

        Fragment target = fragmentManager.findFragmentByTag(tag);
        if (target == null) {
            int layoutRes = TAG_OTHER.equals(tag) ? R.layout.f_other_collection : R.layout.f_site_collection;
            String section = TAG_OTHER.equals(tag) ? TAG_OTHER : TAG_SITE;
            target = LegacySiteCollectionSectionFragment.newInstance(layoutRes, section);
            transaction.add(R.id.flSiteContext, target, tag);
        } else {
            transaction.show(target);
        }
        transaction.commit();
    }
}
