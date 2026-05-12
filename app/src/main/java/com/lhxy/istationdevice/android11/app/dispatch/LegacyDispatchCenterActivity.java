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
    private static final String TAG_PAYCARD_TEST = "PAYCARD_TEST";
    private static final String TAG_LED_PERIPHERAL = "LED_PERIPHERAL";

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
        showContent(TAG_PAYCARD_TEST);
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rb_radio_ledPeripheral) {
                    showContent(TAG_LED_PERIPHERAL);
                } else {
                    showContent(TAG_PAYCARD_TEST);
                }
            });
        }
    }

    private void showContent(String tag) {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment paycardTestFragment = fragmentManager.findFragmentByTag(TAG_PAYCARD_TEST);
        Fragment ledPeripheralFragment = fragmentManager.findFragmentByTag(TAG_LED_PERIPHERAL);
        if (paycardTestFragment != null) {
            transaction.hide(paycardTestFragment);
        }
        if (ledPeripheralFragment != null) {
            transaction.hide(ledPeripheralFragment);
        }

        Fragment target = fragmentManager.findFragmentByTag(tag);
        if (target == null) {
            if (TAG_LED_PERIPHERAL.equals(tag)) {
                target = new LegacyDispatchLedPeripheralFragment();
            } else {
                target = new LegacyDispatchPaycardTestFragment();
            }
            transaction.add(R.id.flDispatchContext, target, tag);
        } else {
            transaction.show(target);
        }
        transaction.commit();
    }
}
