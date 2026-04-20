package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

/**
 * 旧版调度中心页骨架。
 */
public final class LegacyDispatchCenterActivity extends LegacyBaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.act_dispatch_center;
    }

    @Override
    protected int getTitleResId() {
        return R.string.dispatchcenter_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        RadioGroup radioGroup = findViewById(R.id.rg_radio_dispatch);
        RadioButton attendanceButton = findViewById(R.id.rb_radio_attendance);
        if (attendanceButton != null) {
            attendanceButton.setChecked(true);
        }
        showContent(new LegacyDispatchAttendanceFragment());
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rb_radio_dispatch) {
                    showContent(new LegacyDispatchOperationsFragment());
                } else {
                    showContent(new LegacyDispatchAttendanceFragment());
                }
            });
        }
    }

    private void showContent(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flDispatchContext, fragment)
                .commit();
    }
}
