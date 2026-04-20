package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

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
        showContent(LegacyBasicSetupSectionFragment.newInstance(R.layout.f_newspaper_setup, SECTION_NEWSPAPER));
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int layoutRes = R.layout.f_newspaper_setup;
                String section = SECTION_NEWSPAPER;
                if (checkedId == R.id.rb_radio_network) {
                    layoutRes = R.layout.f_network;
                    section = SECTION_NETWORK;
                } else if (checkedId == R.id.rb_radio_serial_port) {
                    layoutRes = R.layout.f_serial_port;
                    section = SECTION_SERIAL_PORT;
                } else if (checkedId == R.id.rb_radio_tts) {
                    layoutRes = R.layout.f_tts_setup;
                    section = SECTION_TTS;
                } else if (checkedId == R.id.rb_radio_language) {
                    layoutRes = R.layout.f_language;
                    section = SECTION_LANGUAGE;
                } else if (checkedId == R.id.rb_radio_other) {
                    layoutRes = R.layout.f_other_setup;
                    section = SECTION_OTHER;
                } else if (checkedId == R.id.rb_radio_wireless) {
                    layoutRes = R.layout.f_wireless_setup;
                    section = SECTION_WIRELESS;
                }
                showContent(LegacyBasicSetupSectionFragment.newInstance(layoutRes, section));
            });
        }
    }

    private void showContent(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flSetupContext, fragment)
                .commit();
    }
}
