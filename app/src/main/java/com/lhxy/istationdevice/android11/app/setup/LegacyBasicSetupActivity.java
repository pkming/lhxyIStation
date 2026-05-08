package com.lhxy.istationdevice.android11.app.setup;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;

/**
 * 旧版系统设置页骨架。
 */
public final class LegacyBasicSetupActivity extends LegacyBaseActivity {
    private static final String SECTION_NEWSPAPER = "NEWSPAPER";
    private static final String SECTION_NETWORK = "NETWORK";
    private static final String SECTION_SERIAL_PORT = "SERIAL_PORT";
    private static final String SECTION_TTS = "TTS";
    private static final String SECTION_LANGUAGE = "LANGUAGE";
    private static final String SECTION_OTHER = "OTHER";
    private static final String SECTION_WIRELESS = "WIRELESS";

    @Override
    protected int getLayoutId() {
        return R.layout.act_basic_setup;
    }

    @Override
    protected int getTitleResId() {
        return R.string.systemsetup_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        RadioGroup radioGroup = findViewById(R.id.rg_radio_setup_navigation);
        RadioButton defaultButton = findViewById(R.id.rb_radio_newspaper);
        if (defaultButton != null) {
            defaultButton.setChecked(true);
        }
        showContent(SECTION_NEWSPAPER);
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                String section = SECTION_NEWSPAPER;
                if (checkedId == R.id.rb_radio_network) {
                    section = SECTION_NETWORK;
                } else if (checkedId == R.id.rb_radio_serial_port) {
                    section = SECTION_SERIAL_PORT;
                } else if (checkedId == R.id.rb_radio_tts) {
                    section = SECTION_TTS;
                } else if (checkedId == R.id.rb_radio_language) {
                    section = SECTION_LANGUAGE;
                } else if (checkedId == R.id.rb_radio_other) {
                    section = SECTION_OTHER;
                } else if (checkedId == R.id.rb_radio_wireless) {
                    section = SECTION_WIRELESS;
                }
                showContent(section);
            });
        }
    }

    private void showContent(String section) {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : fragmentManager.getFragments()) {
            transaction.hide(fragment);
        }

        Fragment target = fragmentManager.findFragmentByTag(section);
        if (target == null) {
            target = LegacyBasicSetupSectionFragment.newInstance(resolveLayoutRes(section), section);
            transaction.add(R.id.flSetupContext, target, section);
        } else {
            transaction.show(target);
        }
        transaction.commit();
    }

    private int resolveLayoutRes(String section) {
        if (SECTION_NETWORK.equals(section)) {
            return R.layout.f_network;
        }
        if (SECTION_SERIAL_PORT.equals(section)) {
            return R.layout.f_serial_port;
        }
        if (SECTION_TTS.equals(section)) {
            return R.layout.f_tts_setup;
        }
        if (SECTION_LANGUAGE.equals(section)) {
            return R.layout.f_language;
        }
        if (SECTION_OTHER.equals(section)) {
            return R.layout.f_other_setup;
        }
        if (SECTION_WIRELESS.equals(section)) {
            return R.layout.f_wireless_setup;
        }
        return R.layout.f_newspaper_setup;
    }
}
