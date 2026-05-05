package com.lhxy.istationdevice.android11.app.dispatch;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;

/**
 * 旧版调度中心页骨架。
 */
public final class LegacyDispatchCenterActivity extends LegacyBaseActivity {
    private static final String TAG_ATTENDANCE = "ATTENDANCE";
    private static final String TAG_DISPATCH = "DISPATCH";

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
        showContent(TAG_ATTENDANCE);
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                showContent(checkedId == R.id.rb_radio_dispatch ? TAG_DISPATCH : TAG_ATTENDANCE);
            });
        }
    }

    private void showContent(String tag) {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment attendanceFragment = fragmentManager.findFragmentByTag(TAG_ATTENDANCE);
        Fragment dispatchFragment = fragmentManager.findFragmentByTag(TAG_DISPATCH);
        if (attendanceFragment != null) {
            transaction.hide(attendanceFragment);
        }
        if (dispatchFragment != null) {
            transaction.hide(dispatchFragment);
        }

        Fragment target = fragmentManager.findFragmentByTag(tag);
        if (target == null) {
            target = TAG_DISPATCH.equals(tag) ? new LegacyDispatchOperationsFragment() : new LegacyDispatchAttendanceFragment();
            transaction.add(R.id.flDispatchContext, target, tag);
        } else {
            transaction.show(target);
        }
        transaction.commit();
    }
}
